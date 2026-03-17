#!/usr/bin/env python3
"""
Auto-stub generator for compiling AOSP classes against our shim layer.

Usage: python3 scripts/aosp-stub-gen.py /path/to/AOSP/View.java [--max-rounds 15] [--dry-run]

The script:
1. Compiles the AOSP file against shim/java sourcepath
2. Parses javac errors: missing classes, packages, inner classes, methods, fields
3. Auto-generates minimal stub files for missing symbols
4. Repeats until compilation succeeds or max rounds reached

SAFETY:
- Never modifies existing methods/fields in pre-existing shim files
- May add inner class/interface stubs to existing shim files when needed
- Creates NEW stub files for entirely missing classes
"""
import subprocess, re, os, sys
from pathlib import Path
from collections import defaultdict

PROJECT_ROOT = Path(__file__).resolve().parent.parent
SHIM_DIR = PROJECT_ROOT / "shim" / "java"
MOCK_DIR = PROJECT_ROOT / "test-apps" / "mock"
BUILD_DIR = Path("/tmp/aosp-stub-check")

# ─── Annotation heuristics ───────────────────────────────────────────────────

KNOWN_ANNOTATION_PACKAGES = {
    "android.annotation",
    "android.compat.annotation",
    "androidx.annotation",
    "dalvik.annotation.optimization",
}

ANNOTATION_NAME_HINTS = {
    "AttrRes", "CallSuper", "ColorInt", "ColorRes", "CriticalNative",
    "CurrentTimeMillisLong", "CurrentTimeSecondsLong", "DimenRes",
    "DrawableRes", "FastNative", "FlaggedApi", "FloatRange",
    "IdRes", "IntDef", "IntRange", "LayoutRes", "LongDef",
    "MainThread", "NonNull", "Nullable", "RequiresApi",
    "RequiresPermission", "Size", "StringDef", "StringRes",
    "StyleRes", "StyleableRes", "SuppressLint", "SystemApi",
    "TestApi", "UiThread", "UnsupportedAppUsage", "VisibleForTesting",
    "WorkerThread",
}

# ─── Tracking ────────────────────────────────────────────────────────────────

stats = {
    "stubs_created": 0,
    "inner_classes_added": 0,
    "methods_added": 0,
    "fields_added": 0,
    "rounds": 0,
}

# Files we created (safe to add methods/fields to)
created_files = set()


def is_our_creation(path):
    """True if we created this file in this session."""
    return path in created_files


# ─── Compilation ─────────────────────────────────────────────────────────────

def compile_check(java_file, extra_sourcepaths=None):
    """Try to compile; return (returncode, stderr)."""
    sourcepaths = [str(MOCK_DIR), str(SHIM_DIR)]
    if extra_sourcepaths:
        sourcepaths.extend(extra_sourcepaths)
    cmd = [
        "javac", "-d", str(BUILD_DIR),
        "-sourcepath", ":".join(sourcepaths),
        "-Xlint:none", "-proc:none",
        "-Xmaxerrs", "500",
        str(java_file)
    ]
    BUILD_DIR.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(cmd, capture_output=True, text=True, timeout=120)
    return result.returncode, result.stderr


# ─── Import resolution ───────────────────────────────────────────────────────

_import_cache = {}

def parse_source_imports(java_file):
    """Parse import statements from a Java source file.
    Returns (import_map: {short_name -> fqcn}, static_imports: {symbol -> class_fqcn})
    """
    java_file = str(java_file)
    if java_file in _import_cache:
        return _import_cache[java_file]

    import_map = {}
    static_imports = {}
    try:
        with open(java_file, "r", errors="replace") as f:
            for line in f:
                line = line.strip()
                if line.startswith("import static "):
                    m = re.match(r"import\s+static\s+([\w.]+)\.(\w+)\s*;", line)
                    if m:
                        class_fqcn, sym = m.group(1), m.group(2)
                        cls = class_fqcn.rsplit(".", 1)[-1]
                        import_map[cls] = class_fqcn
                        static_imports[sym] = class_fqcn
                elif line.startswith("import "):
                    m = re.match(r"import\s+([\w.]+\.(\w+))\s*;", line)
                    if m:
                        fqcn, cls = m.group(1), m.group(2)
                        import_map[cls] = fqcn
                elif not line.startswith("package") and not line.startswith("//") and \
                     not line.startswith("/*") and not line.startswith("*") and line:
                    if re.match(r"(public|abstract|final|class|interface|enum)\b", line):
                        break
    except Exception:
        pass

    _import_cache[java_file] = (import_map, static_imports)
    return import_map, static_imports


def is_annotation_class(class_name, package):
    """Heuristic: should this be generated as @interface?"""
    if package in KNOWN_ANNOTATION_PACKAGES:
        return True
    if class_name in ANNOTATION_NAME_HINTS:
        return True
    if "annotation" in package.lower():
        return True
    return False


# ─── Error parsing ───────────────────────────────────────────────────────────

def collect_error_file_imports(stderr):
    """Extract unique file paths from error lines and merge their imports."""
    files = set()
    for line in stderr.split("\n"):
        m = re.match(r"(\S+\.java):\d+:", line)
        if m:
            files.add(m.group(1))

    merged_imports = {}
    merged_statics = {}
    for f in files:
        if os.path.exists(f):
            imp, static = parse_source_imports(f)
            merged_imports.update(imp)
            merged_statics.update(static)

    return merged_imports, merged_statics, files


def parse_static_import_fqcn(lines, i):
    """From error context, extract the FQCN of the class in a static import statement."""
    # Look nearby for the actual import line
    for j in range(max(0, i - 3), min(i + 3, len(lines))):
        m = re.search(r"import\s+static\s+([\w.]+)\.(\w+)\s*;", lines[j])
        if m:
            return m.group(1)  # class FQCN
    return None


def is_inner_class_ref(fqcn):
    """
    Check if a FQCN like 'android.view.WindowInsets.Type' refers to an inner class
    by checking if a parent file exists in shim dir.
    Returns (parent_fqcn, inner_name) or None.
    Also catches deeper nesting like 'Outer.Inner.SubInner' -> adds 'Inner' to Outer
    (we only go one level deep for inner classes).
    """
    parts = fqcn.split(".")
    # Try splitting at each dot from the right, looking for a parent .java file
    for i in range(len(parts) - 1, 0, -1):
        parent_fqcn = ".".join(parts[:i])
        inner_parts = parts[i:]
        parent_path = SHIM_DIR / (parent_fqcn.replace(".", "/") + ".java")
        if parent_path.exists():
            # Only handle one level of nesting -- add the first inner name
            return (parent_fqcn, inner_parts[0])
    return None


def would_clash(fqcn):
    """
    Check if creating a stub file for fqcn would create a directory that clashes
    with an existing .java file, or vice versa.
    E.g., creating android/view/WindowInsets/Type.java would need mkdir WindowInsets/
    but android/view/WindowInsets.java already exists.
    """
    path = stub_path_for_fqcn(fqcn)
    # Check each ancestor directory: does a .java file exist with that name?
    current = path.parent
    while current != SHIM_DIR and current != current.parent:
        sibling_java = current.with_suffix(".java")
        if sibling_java.is_file():
            return True
        current = current.parent
    return False


def parse_errors(stderr, all_import_maps, all_static_imports):
    """
    Parse javac errors into actionable items.
    Returns dict with keys: classes, packages, inner, methods, fields, static_import_classes
    """
    missing_classes = set()         # fqcn strings
    missing_packages = set()        # package strings
    missing_inner = defaultdict(set)  # parent_fqcn -> {inner_name}
    missing_methods = defaultdict(set)  # fqcn -> {(method, params)}
    missing_fields = defaultdict(set)   # fqcn -> {field_name}
    static_import_classes = set()   # class fqcn from static imports

    lines = stderr.split("\n")
    i = 0
    while i < len(lines):
        line = lines[i]

        # ── "package X does not exist" ──
        m = re.search(r"package\s+([\w.]+)\s+does not exist", line)
        if m:
            pkg = m.group(1)
            missing_packages.add(pkg)
            # If from a static import, extract class FQCN
            cls_fqcn = parse_static_import_fqcn(lines, i)
            if cls_fqcn:
                static_import_classes.add(cls_fqcn)
            i += 1
            continue

        # ── "static import only from classes and interfaces" ──
        if "static import only from classes and interfaces" in line:
            cls_fqcn = parse_static_import_fqcn(lines, i)
            if cls_fqcn:
                static_import_classes.add(cls_fqcn)
            i += 1
            continue

        # ── "cannot find symbol" ──
        if "cannot find symbol" in line:
            error_file = None
            fm = re.match(r"(\S+\.java):\d+:", line)
            if fm:
                error_file = fm.group(1)

            symbol_line = ""
            location_line = ""
            for j in range(i + 1, min(i + 8, len(lines))):
                sl = lines[j].strip()
                if sl.startswith("symbol:"):
                    symbol_line = sl
                elif sl.startswith("location:"):
                    location_line = sl

            # ── symbol: class Foo ──
            sm = re.match(r"symbol:\s+class\s+(\w+)", symbol_line)
            if sm:
                cls_name = sm.group(1)
                loc_pkg = re.search(r"location:\s+package\s+([\w.]+)", location_line)
                loc_cls = re.search(r"location:\s+(?:class|interface)\s+([\w.]+)", location_line)

                if loc_pkg:
                    fqcn = f"{loc_pkg.group(1)}.{cls_name}"
                    missing_classes.add(fqcn)
                elif loc_cls:
                    parent_short = loc_cls.group(1)
                    parent_fqcn = _resolve_name(parent_short, error_file, all_import_maps)
                    if parent_fqcn:
                        missing_inner[parent_fqcn].add(cls_name)
                    elif cls_name in all_import_maps:
                        missing_classes.add(all_import_maps[cls_name])
                elif cls_name in all_import_maps:
                    missing_classes.add(all_import_maps[cls_name])
                i += 1
                continue

            # ── symbol: method foo(X,Y) ──
            sm = re.match(r"symbol:\s+method\s+(\w+)\((.*?)\)", symbol_line)
            if sm:
                method_name, params = sm.group(1), sm.group(2)
                loc_cls = re.search(r"location:\s+(?:class|interface)\s+([\w.]+)", location_line)
                if loc_cls:
                    fqcn = _resolve_name(loc_cls.group(1), error_file, all_import_maps)
                    if fqcn:
                        missing_methods[fqcn].add((method_name, params))
                i += 1
                continue

            # ── symbol: variable FOO ──
            sm = re.match(r"symbol:\s+variable\s+(\w+)", symbol_line)
            if sm:
                field_name = sm.group(1)
                loc_cls = re.search(r"location:\s+(?:class|interface)\s+([\w.]+)", location_line)
                if loc_cls:
                    fqcn = _resolve_name(loc_cls.group(1), error_file, all_import_maps)
                    if fqcn:
                        missing_fields[fqcn].add(field_name)
                i += 1
                continue

            # ── symbol: constructor Foo(X) ──
            sm = re.match(r"symbol:\s+constructor\s+(\w+)\((.*?)\)", symbol_line)
            if sm:
                ctor_name, params = sm.group(1), sm.group(2)
                loc_cls = re.search(r"location:\s+(?:class|interface)\s+([\w.]+)", location_line)
                if loc_cls:
                    fqcn = _resolve_name(loc_cls.group(1), error_file, all_import_maps)
                    if fqcn:
                        missing_methods[fqcn].add((ctor_name, params))
                i += 1
                continue

        i += 1

    return {
        "classes": missing_classes,
        "packages": missing_packages,
        "inner": dict(missing_inner),
        "methods": dict(missing_methods),
        "fields": dict(missing_fields),
        "static_import_classes": static_import_classes,
    }


def _resolve_name(name, error_file, all_import_maps):
    """Resolve a possibly-short class name to FQCN."""
    # Already qualified (2+ dots)?
    if name.count(".") >= 2:
        return name

    # Try the error file's imports
    if error_file and os.path.exists(error_file):
        imp, _ = parse_source_imports(error_file)
        short = name.rsplit(".", 1)[-1] if "." in name else name
        if short in imp:
            return imp[short]

    # Try merged import map
    short = name.rsplit(".", 1)[-1] if "." in name else name
    if short in all_import_maps:
        return all_import_maps[short]

    # Try known shim packages
    for prefix in ["android.view", "android.widget", "android.content",
                    "android.os", "android.util", "android.graphics",
                    "android.app", "android.text", "android.content.res"]:
        candidate = f"{prefix}.{name}"
        if stub_path_for_fqcn(candidate).exists():
            return candidate

    return None


# ─── Package resolution ──────────────────────────────────────────────────────

def resolve_package_classes(pkg, all_imports, all_statics):
    """For a missing package, find which classes from it are used via imports."""
    results = set()
    for cls_name, fqcn in all_imports.items():
        if fqcn.startswith(pkg + "."):
            remainder = fqcn[len(pkg) + 1:]
            if "." not in remainder:
                results.add(fqcn)
    for sym, class_fqcn in all_statics.items():
        if class_fqcn.startswith(pkg + "."):
            # The class_fqcn might be the class itself
            results.add(class_fqcn)
    return results


# ─── Stub file helpers ───────────────────────────────────────────────────────

def stub_path_for_fqcn(fqcn):
    return SHIM_DIR / (fqcn.replace(".", "/") + ".java")


def stub_exists(fqcn):
    return stub_path_for_fqcn(fqcn).exists()


def generate_stub_class(fqcn, kind="class", dry_run=False):
    """Generate a minimal stub .java file. Returns True if created."""
    # Check if this looks like an inner class reference (Outer.Inner)
    inner_check = is_inner_class_ref(fqcn)
    if inner_check:
        parent_fqcn, inner_name = inner_check
        return add_inner_to_existing(parent_fqcn, inner_name, dry_run=dry_run)

    path = stub_path_for_fqcn(fqcn)
    if path.exists():
        return False

    # Prevent creating files that would clash with existing .java files
    if would_clash(fqcn):
        return False

    parts = fqcn.split(".")
    class_name = parts[-1]
    package = ".".join(parts[:-1]) if len(parts) > 1 else ""

    if dry_run:
        print(f"  [DRY-RUN] Would create: {fqcn} ({kind})")
        stats["stubs_created"] += 1
        return True

    path.parent.mkdir(parents=True, exist_ok=True)
    lines = []
    if package:
        lines.append(f"package {package};")
        lines.append("")

    lines.append("/** Auto-generated stub for AOSP compilation. */")

    if kind == "annotation":
        lines.append("import java.lang.annotation.*;")
        lines.append("")
        lines.append("@Retention(RetentionPolicy.CLASS)")
        lines.append("@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER,")
        lines.append("         ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE,")
        lines.append("         ElementType.ANNOTATION_TYPE})")
        lines.append(f"public @interface {class_name} {{")
        lines.append("    String[] value() default {};")
        lines.append("    long maxTargetSdk() default Long.MAX_VALUE;")
        lines.append("    int[] apis() default {};")
        lines.append("    long from() default 0;")
        lines.append("    long to() default Long.MAX_VALUE;")
        lines.append("}")
    elif kind == "interface":
        lines.append(f"public interface {class_name} {{")
        lines.append("}")
    else:
        lines.append(f"public class {class_name} {{")
        lines.append(f"    public {class_name}() {{}}")
        lines.append("}")

    lines.append("")
    path.write_text("\n".join(lines))
    created_files.add(path)
    stats["stubs_created"] += 1
    return True


def add_inner_to_existing(parent_fqcn, inner_name, dry_run=False):
    """Add a public static inner class to an existing shim file (even pre-existing).
    Only adds the inner type -- never changes existing methods/fields."""
    path = stub_path_for_fqcn(parent_fqcn)
    if not path.exists():
        return False

    content = path.read_text()
    # Already has this inner?
    if re.search(rf"\b(class|interface|@interface)\s+{re.escape(inner_name)}\b", content):
        return False

    if dry_run:
        print(f"  [DRY-RUN] Would add inner {inner_name} to {parent_fqcn}")
        stats["inner_classes_added"] += 1
        return True

    # Heuristic for inner type kind
    is_iface = inner_name.endswith("Listener") or inner_name.endswith("Callback") or \
               inner_name.endswith("Info") or inner_name in {
                   "Callback", "Listener", "Observer", "Factory", "Provider", "Creator",
                   "Stub",  # AIDL stubs
               }

    if inner_name == "Stub":
        # AIDL pattern: Stub extends parent
        snippet = f"\n    /** Auto-generated stub. */\n"
        snippet += f"    public static abstract class Stub extends android.os.Binder {{\n"
        snippet += f"        public Stub() {{}}\n"
        snippet += f"    }}\n"
    elif is_iface:
        snippet = f"\n    /** Auto-generated stub. */\n"
        snippet += f"    public static interface {inner_name} {{}}\n"
    else:
        snippet = f"\n    /** Auto-generated stub. */\n"
        snippet += f"    public static class {inner_name} {{\n"
        snippet += f"        public {inner_name}() {{}}\n"
        snippet += f"    }}\n"

    last_brace = content.rfind("}")
    if last_brace == -1:
        return False

    new_content = content[:last_brace] + snippet + content[last_brace:]
    path.write_text(new_content)
    stats["inner_classes_added"] += 1
    return True


def add_method_to_stub(fqcn, method_name, params_hint, dry_run=False):
    """Add a stub method to a stub we created this session."""
    path = stub_path_for_fqcn(fqcn)
    if not path.exists() or not is_our_creation(path):
        return False

    content = path.read_text()
    if re.search(rf"\b{re.escape(method_name)}\s*\(", content):
        return False

    if dry_run:
        print(f"  [DRY-RUN] Would add method {method_name}({params_hint}) to {fqcn}")
        stats["methods_added"] += 1
        return True

    params = []
    if params_hint:
        for idx, p in enumerate(params_hint.split(",")):
            p = p.strip()
            if p:
                params.append(f"{p} arg{idx}")
    param_str = ", ".join(params)

    class_name = fqcn.rsplit(".", 1)[-1]
    if method_name == class_name:
        snippet = f"\n    public {method_name}({param_str}) {{}}\n"
    else:
        snippet = f"\n    public Object {method_name}({param_str}) {{ return null; }}\n"

    last_brace = content.rfind("}")
    new_content = content[:last_brace] + snippet + content[last_brace:]
    path.write_text(new_content)
    stats["methods_added"] += 1
    return True


def add_field_to_stub(fqcn, field_name, dry_run=False):
    """Add a field to a stub we created this session."""
    path = stub_path_for_fqcn(fqcn)
    if not path.exists() or not is_our_creation(path):
        return False

    content = path.read_text()
    if re.search(rf"\b{re.escape(field_name)}\b", content):
        return False

    if dry_run:
        print(f"  [DRY-RUN] Would add field {field_name} to {fqcn}")
        stats["fields_added"] += 1
        return True

    if field_name.isupper() or re.match(r"^[A-Z][A-Z0-9_]+$", field_name):
        snippet = f"\n    public static final int {field_name} = 0;\n"
    else:
        snippet = f"\n    public static Object {field_name};\n"

    last_brace = content.rfind("}")
    new_content = content[:last_brace] + snippet + content[last_brace:]
    path.write_text(new_content)
    stats["fields_added"] += 1
    return True


def _cleanup_clashes():
    """Remove directories that clash with .java files of the same name.
    This can happen when inner classes are first created as standalone files
    before the parent class exists, then the parent class is created later."""
    import shutil
    for path in list(created_files):
        # Check if a directory with the same stem exists alongside this file
        dir_path = path.with_suffix("")  # Remove .java -> get potential dir path
        if dir_path.is_dir():
            # Directory clashes with our .java file -- remove the directory
            shutil.rmtree(dir_path)
            created_files.discard(path)  # Will be re-added if regenerated


# ─── Main loop ───────────────────────────────────────────────────────────────

def run(aosp_file, max_rounds=15, dry_run=False, verbose=False):
    aosp_path = Path(aosp_file).resolve()
    if not aosp_path.exists():
        print(f"ERROR: File not found: {aosp_path}")
        return False

    print(f"{'=' * 60}")
    print(f"AOSP Stub Generator")
    print(f"{'=' * 60}")
    print(f"Input:      {aosp_path}")
    print(f"Shim dir:   {SHIM_DIR}")
    print(f"Max rounds: {max_rounds}")
    print(f"Dry run:    {dry_run}")
    print()

    # Find AOSP source root from package declaration
    aosp_sourcepath = None
    try:
        with open(aosp_path, "r", errors="replace") as f:
            for line in f:
                m = re.match(r"package\s+([\w.]+)\s*;", line.strip())
                if m:
                    pkg_path = m.group(1).replace(".", "/")
                    parent = str(aosp_path.parent)
                    if parent.endswith(pkg_path):
                        aosp_sourcepath = parent[:-len(pkg_path) - 1]
                    break
    except Exception:
        pass

    extra_sp = [aosp_sourcepath] if aosp_sourcepath else []
    print(f"AOSP source root: {aosp_sourcepath or '(not found)'}")

    prev_error_count = None
    stuck_rounds = 0
    generated_files = []

    for round_num in range(1, max_rounds + 1):
        stats["rounds"] = round_num
        print(f"\n--- Round {round_num} ---")

        returncode, stderr = compile_check(aosp_path, extra_sp)
        error_count = stderr.count("error:")
        print(f"  Errors: {error_count}")

        if error_count == 0:
            print(f"\n{'=' * 60}")
            print(f"COMPILATION SUCCESSFUL after {round_num} round(s)!")
            print(f"{'=' * 60}")
            print_stats(generated_files)
            return True

        # Collect imports from ALL error-producing files
        all_imports, all_statics, error_files = collect_error_file_imports(stderr)
        print(f"  Error files: {len(error_files)}, Merged imports: {len(all_imports)}")

        parsed = parse_errors(stderr, all_imports, all_statics)

        progress = False

        # 1. Static import classes (import static X.Y.Z -- X.Y is missing)
        for class_fqcn in parsed["static_import_classes"]:
            inner = is_inner_class_ref(class_fqcn)
            if inner:
                parent_fqcn, inner_name = inner
                if add_inner_to_existing(parent_fqcn, inner_name, dry_run=dry_run):
                    print(f"  + inner: {parent_fqcn}.{inner_name} (from static import)")
                    progress = True
            elif not stub_exists(class_fqcn):
                cls_name = class_fqcn.rsplit(".", 1)[-1]
                pkg = class_fqcn.rsplit(".", 1)[0] if "." in class_fqcn else ""
                kind = "annotation" if is_annotation_class(cls_name, pkg) else "class"
                if generate_stub_class(class_fqcn, kind=kind, dry_run=dry_run):
                    print(f"  + {kind}: {class_fqcn} (static import)")
                    generated_files.append(class_fqcn)
                    progress = True

        # 2. Missing packages -> resolve to specific classes
        for pkg in parsed["packages"]:
            resolved = resolve_package_classes(pkg, all_imports, all_statics)
            if resolved:
                for fqcn in resolved:
                    inner = is_inner_class_ref(fqcn)
                    if inner:
                        parent_fqcn, inner_name = inner
                        if add_inner_to_existing(parent_fqcn, inner_name, dry_run=dry_run):
                            print(f"  + inner: {parent_fqcn}.{inner_name} (from package)")
                            progress = True
                    elif not stub_exists(fqcn):
                        cls_name = fqcn.rsplit(".", 1)[-1]
                        pkg_part = fqcn.rsplit(".", 1)[0]
                        kind = "annotation" if is_annotation_class(cls_name, pkg_part) else "class"
                        if generate_stub_class(fqcn, kind=kind, dry_run=dry_run):
                            print(f"  + {kind}: {fqcn} (from package {pkg})")
                            generated_files.append(fqcn)
                            progress = True
            else:
                # No specific class found; create a placeholder
                last = pkg.rsplit(".", 1)[-1]
                placeholder = last[0].upper() + last[1:] if last else "Stub"
                placeholder_fqcn = f"{pkg}.{placeholder}"
                if not stub_exists(placeholder_fqcn):
                    if generate_stub_class(placeholder_fqcn, dry_run=dry_run):
                        print(f"  + class: {placeholder_fqcn} (package placeholder)")
                        generated_files.append(placeholder_fqcn)
                        progress = True

        # 3. Missing classes (fully qualified from location:package)
        for fqcn in parsed["classes"]:
            inner = is_inner_class_ref(fqcn)
            if inner:
                parent_fqcn, inner_name = inner
                if add_inner_to_existing(parent_fqcn, inner_name, dry_run=dry_run):
                    print(f"  + inner: {parent_fqcn}.{inner_name}")
                    progress = True
            elif not stub_exists(fqcn):
                cls_name = fqcn.rsplit(".", 1)[-1]
                pkg = fqcn.rsplit(".", 1)[0] if "." in fqcn else ""
                kind = "annotation" if is_annotation_class(cls_name, pkg) else "class"
                if generate_stub_class(fqcn, kind=kind, dry_run=dry_run):
                    print(f"  + {kind}: {fqcn}")
                    generated_files.append(fqcn)
                    progress = True

        # 4. Missing inner classes (from location:class Foo, symbol:class Bar)
        for parent_fqcn, inners in parsed["inner"].items():
            for inner_name in inners:
                if add_inner_to_existing(parent_fqcn, inner_name, dry_run=dry_run):
                    print(f"  + inner: {parent_fqcn}.{inner_name}")
                    progress = True

        # 5. Missing methods (only on stubs we created)
        for fqcn, methods in parsed["methods"].items():
            for method_name, params in methods:
                if add_method_to_stub(fqcn, method_name, params, dry_run=dry_run):
                    print(f"  + method: {fqcn}.{method_name}({params})")
                    progress = True

        # 6. Missing fields (only on stubs we created)
        for fqcn, fields in parsed["fields"].items():
            for field_name in fields:
                if add_field_to_stub(fqcn, field_name, dry_run=dry_run):
                    print(f"  + field: {fqcn}.{field_name}")
                    progress = True

        # Post-round cleanup: remove directory stubs that clash with class files
        _cleanup_clashes()

        if not progress:
            stuck_rounds += 1
            if stuck_rounds >= 2:
                print(f"\n  Stuck for {stuck_rounds} rounds. Remaining errors need manual fixes.")
                error_lines = [l.strip() for l in stderr.split("\n") if "error:" in l]
                unique = list(dict.fromkeys(error_lines))
                show = unique if (verbose or len(unique) <= 30) else unique[:25]
                print(f"\n  Unique error lines ({len(unique)} total):")
                for el in show:
                    print(f"    {el}")
                if len(unique) > len(show):
                    print(f"    ... and {len(unique) - len(show)} more")
                break
            else:
                print(f"  No progress this round, will retry once...")
        else:
            stuck_rounds = 0

        if prev_error_count is not None:
            delta = error_count - prev_error_count
            if delta > 0:
                print(f"  (errors increased by {delta} -- new transitive deps exposed)")
        prev_error_count = error_count

    else:
        print(f"\n{'=' * 60}")
        print(f"Max rounds ({max_rounds}) reached with {error_count} errors remaining")
        print(f"{'=' * 60}")

    print_stats(generated_files)
    return False


def print_stats(generated_files):
    print(f"\n--- Summary ---")
    print(f"  Rounds:              {stats['rounds']}")
    print(f"  Stub files created:  {stats['stubs_created']}")
    print(f"  Inner classes added: {stats['inner_classes_added']}")
    print(f"  Methods added:       {stats['methods_added']}")
    print(f"  Fields added:        {stats['fields_added']}")
    total = stats['stubs_created'] + stats['inner_classes_added'] + \
            stats['methods_added'] + stats['fields_added']
    print(f"  Total changes:       {total}")
    if generated_files:
        print(f"\n  Generated stub files ({len(set(generated_files))}):")
        for f in sorted(set(generated_files)):
            print(f"    {f}")


def main():
    import argparse
    parser = argparse.ArgumentParser(
        description="Auto-generate stub files for AOSP Java compilation against shim layer"
    )
    parser.add_argument("java_file", help="Path to AOSP .java file to compile")
    parser.add_argument("--max-rounds", type=int, default=15,
                        help="Maximum compilation rounds (default: 15)")
    parser.add_argument("--dry-run", action="store_true",
                        help="Show what would be generated without writing files")
    parser.add_argument("--verbose", "-v", action="store_true",
                        help="Show all remaining errors when stuck")
    args = parser.parse_args()

    success = run(args.java_file, max_rounds=args.max_rounds,
                  dry_run=args.dry_run, verbose=args.verbose)
    sys.exit(0 if success else 1)


if __name__ == "__main__":
    main()
