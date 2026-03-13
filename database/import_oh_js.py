#!/usr/bin/env python3
"""Import OpenHarmony JS/TS SDK APIs from .d.ts files into SQLite database."""

import re
import sqlite3
import json
import os
import glob

API_DIR = os.path.expanduser("~/openharmony/interface/sdk-js/api/")
DB_FILE = os.path.expanduser("~/android-to-openharmony-migration/database/api_compat.db")

# Subsystem classification for OH modules
SUBSYSTEM_MAP = {
    'app': 'App Framework',
    'ability': 'App Framework',
    'application': 'App Framework',
    'bundle': 'Package Manager',
    'arkui': 'UI Framework',
    'data': 'Data Management',
    'file': 'File System',
    'net': 'Networking',
    'multimedia': 'Multimedia',
    'telephony': 'Telephony',
    'bluetooth': 'Bluetooth',
    'wifi': 'WiFi',
    'nfc': 'NFC',
    'sensor': 'Sensors',
    'vibrator': 'Sensors',
    'security': 'Security',
    'userIAM': 'Security',
    'account': 'Accounts',
    'notification': 'Notifications',
    'enterprise': 'Enterprise',
    'graphics': 'Graphics',
    'multimodalInput': 'Input',
    'inputMethod': 'Input Method',
    'accessibility': 'Accessibility',
    'ai': 'AI',
    'distributedHardware': 'Distributed',
    'resourceschedule': 'Resource Schedule',
    'advertising': 'Advertising',
}


def get_oh_subsystem(module_name):
    """Classify OH module into subsystem."""
    # module_name like "@ohos.net.http" or "@ohos.multimedia.media"
    clean = module_name.replace('@ohos.', '').replace('@system.', '')
    first_segment = clean.split('.')[0]
    return SUBSYSTEM_MAP.get(first_segment, 'Other')


def extract_braced_body(content, open_brace_pos):
    """Extract body between braces using depth counting, skipping strings/comments."""
    depth = 1
    i = open_brace_pos + 1
    in_string = None  # None, "'", '"', '`'
    in_line_comment = False
    in_block_comment = False

    while i < len(content) and depth > 0:
        ch = content[i]

        if in_line_comment:
            if ch == '\n':
                in_line_comment = False
        elif in_block_comment:
            if ch == '*' and i + 1 < len(content) and content[i + 1] == '/':
                in_block_comment = False
                i += 1
        elif in_string:
            if ch == '\\':
                i += 1  # skip escaped char
            elif ch == in_string:
                in_string = None
        else:
            if ch in ("'", '"', '`'):
                in_string = ch
            elif ch == '/' and i + 1 < len(content):
                if content[i + 1] == '/':
                    in_line_comment = True
                elif content[i + 1] == '*':
                    in_block_comment = True
                    i += 1
            elif ch == '{':
                depth += 1
            elif ch == '}':
                depth -= 1
        i += 1

    return content[open_brace_pos + 1:i - 1]  # body without outer braces


def extract_jsdoc_metadata(body, member_start_pos):
    """Find the JSDoc comment block just before a member declaration."""
    preceding = body[:member_start_pos].rstrip()
    jsdoc_match = re.search(r'/\*\*(.*?)\*/\s*$', preceding, re.DOTALL)
    if jsdoc_match:
        doc = jsdoc_match.group(1)
        since = None
        syscap = None
        deprecated = False
        since_m = re.search(r'@since\s+(\d+)', doc)
        if since_m:
            since = int(since_m.group(1))
        syscap_m = re.search(r'@syscap\s+(SystemCapability\.\S+)', doc)
        if syscap_m:
            syscap = syscap_m.group(1)
        if '@deprecated' in doc:
            deprecated = True
        return since, syscap, deprecated
    return None, None, False


def parse_methods_from_body(body, file_since=None, file_syscap=None):
    """Parse method declarations from a type body, with per-API JSDoc metadata."""
    methods = []
    for mm in re.finditer(
        r'(?:static\s+)?(\w+)\s*(?:<[^>]*>)?\s*\(([^)]*)\)\s*:\s*([^;]+);', body
    ):
        since, syscap, deprecated = extract_jsdoc_metadata(body, mm.start())
        methods.append({
            'name': mm.group(1),
            'params': mm.group(2).strip(),
            'return_type': mm.group(3).strip(),
            'signature': f"{mm.group(1)}({mm.group(2).strip()}): {mm.group(3).strip()}",
            'since': since if since is not None else file_since,
            'syscap': syscap if syscap is not None else file_syscap,
            'deprecated': deprecated,
        })
    return methods


def parse_properties_from_body(body, methods, file_since=None, file_syscap=None):
    """Parse property declarations from a type body, with per-API JSDoc metadata."""
    props = []
    method_names = {m['name'] for m in methods}
    for pm in re.finditer(r'(readonly\s+)?(\w+)(\?)?:\s*([^;]+);', body):
        if pm.group(2) in method_names:
            continue
        since, syscap, deprecated = extract_jsdoc_metadata(body, pm.start())
        props.append({
            'name': pm.group(2),
            'type': pm.group(4).strip(),
            'readonly': bool(pm.group(1)),
            'optional': bool(pm.group(3)),
            'signature': (
                f"{'readonly ' if pm.group(1) else ''}"
                f"{pm.group(2)}{'?' if pm.group(3) else ''}: {pm.group(4).strip()}"
            ),
            'since': since if since is not None else file_since,
            'syscap': syscap if syscap is not None else file_syscap,
            'deprecated': deprecated,
        })
    return props


def parse_enum_values_from_body(body, file_since=None, file_syscap=None):
    """Parse enum value declarations from an enum body, with per-API JSDoc metadata."""
    values = []
    for vm in re.finditer(r'(\w+)\s*=\s*([^,\n]+)', body):
        since, syscap, deprecated = extract_jsdoc_metadata(body, vm.start())
        values.append({
            'name': vm.group(1),
            'value': vm.group(2).strip(),
            'signature': f"{vm.group(1)} = {vm.group(2).strip()}",
            'since': since if since is not None else file_since,
            'syscap': syscap if syscap is not None else file_syscap,
            'deprecated': deprecated,
        })
    return values


def find_opening_brace(content, start_pos):
    """Find the position of the first '{' at or after start_pos."""
    idx = content.find('{', start_pos)
    return idx if idx != -1 else None


def parse_type_declarations(content, file_since=None, file_syscap=None):
    """Parse all interface/class/enum/struct declarations from content.

    Returns (interfaces, classes, enums) lists.
    Matches export, export default, and declare prefixed declarations.
    """
    interfaces = []
    classes = []
    enums = []

    # Unified pattern for type declarations
    # Matches: export/export default/declare  interface/class/enum/struct  Name
    decl_pattern = re.compile(
        r'(?:export\s+default\s+|export\s+|declare\s+)'
        r'(interface|class|enum|struct)\s+'
        r'(\w+)'
    )

    for m in decl_pattern.finditer(content):
        kind = m.group(1)
        name = m.group(2)
        open_pos = find_opening_brace(content, m.end())
        if open_pos is None:
            continue

        body = extract_braced_body(content, open_pos)

        if kind in ('interface', 'struct'):
            methods = parse_methods_from_body(body, file_since, file_syscap)
            props = parse_properties_from_body(body, methods, file_since, file_syscap)
            interfaces.append({
                'name': name,
                'methods': methods,
                'properties': props,
            })
        elif kind == 'class':
            methods = parse_methods_from_body(body, file_since, file_syscap)
            props = parse_properties_from_body(body, methods, file_since, file_syscap)
            classes.append({
                'name': name,
                'methods': methods,
                'properties': props,
            })
        elif kind == 'enum':
            values = parse_enum_values_from_body(body, file_since, file_syscap)
            enums.append({
                'name': name,
                'values': values,
            })

    return interfaces, classes, enums


def parse_dts_file(filepath):
    """Parse a .d.ts file and extract API elements."""
    with open(filepath, 'r', errors='replace') as f:
        content = f.read()

    result = {
        'module_name': None,
        'kit': None,
        'syscap': None,
        'since': None,
        'interfaces': [],
        'classes': [],
        'enums': [],
        'functions': [],
        'type_aliases': [],
    }

    # Extract module name from filename and path
    basename = os.path.basename(filepath)
    parent_dir = os.path.basename(os.path.dirname(filepath))
    api_base_dir = os.path.basename(os.path.dirname(os.path.dirname(filepath)))

    mod_name = basename.replace('.d.ts', '').replace('.d.ets', '')

    # If the file is in a subdirectory (not the top-level api dir), use dirname/basename
    # e.g., application/UIAbilityContext.d.ts -> application/UIAbilityContext
    # But keep @ohos.xxx and @system.xxx names as-is
    if not mod_name.startswith('@ohos.') and not mod_name.startswith('@system.'):
        # Check if parent is a meaningful subdirectory (not api/ itself)
        api_dir_name = os.path.basename(API_DIR.rstrip('/'))
        if parent_dir and parent_dir != api_dir_name:
            mod_name = f"{parent_dir}/{mod_name}"

    result['module_name'] = mod_name

    # Extract kit from JSDoc
    kit_match = re.search(r'@kit\s+(\S+)', content)
    if kit_match:
        result['kit'] = kit_match.group(1)

    # Extract file-level syscap (first occurrence)
    syscap_match = re.search(r'@syscap\s+(SystemCapability\.\S+)', content)
    if syscap_match:
        result['syscap'] = syscap_match.group(1)

    # Extract file-level since version (minimum across all @since tags at file level)
    since_matches = re.findall(r'@since\s+(\d+)', content)
    if since_matches:
        result['since'] = min(int(v) for v in since_matches)

    file_since = result['since']
    file_syscap = result['syscap']

    # Parse all type declarations (export/export default/declare interface/class/enum/struct)
    interfaces, classes, enums = parse_type_declarations(
        content, file_since, file_syscap
    )
    result['interfaces'] = interfaces
    result['classes'] = classes
    result['enums'] = enums

    # Parse exported functions and declare functions (not inside braces — top-level)
    for m in re.finditer(
        r'(?:export\s+(?:default\s+)?|declare\s+)function\s+(\w+)\s*'
        r'(?:<[^>]*>)?\s*\(([^)]*)\)\s*:\s*([^;]+);',
        content
    ):
        since, syscap, deprecated = extract_jsdoc_metadata(content, m.start())
        result['functions'].append({
            'name': m.group(1),
            'params': m.group(2).strip(),
            'return_type': m.group(3).strip(),
            'signature': f"{m.group(1)}({m.group(2).strip()}): {m.group(3).strip()}",
            'since': since if since is not None else file_since,
            'syscap': syscap if syscap is not None else file_syscap,
            'deprecated': deprecated,
        })

    # Parse type aliases
    for m in re.finditer(r'export\s+type\s+(\w+)(?:<[^>]*>)?\s*=\s*([^;]+);', content):
        result['type_aliases'].append({
            'name': m.group(1),
            'definition': m.group(2).strip(),
            'signature': f"type {m.group(1)} = {m.group(2).strip()}",
        })

    # Parse namespace-level declarations using brace-counting
    for ns_match in re.finditer(r'declare\s+namespace\s+(\w+)\s*\{', content):
        open_pos = content.index('{', ns_match.start())
        ns_body = extract_braced_body(content, open_pos)

        # Parse functions inside namespace
        for m in re.finditer(
            r'function\s+(\w+)\s*(?:<[^>]*>)?\s*\(([^)]*)\)\s*:\s*([^;{]+);', ns_body
        ):
            fname = m.group(1)
            if not any(f['name'] == fname for f in result['functions']):
                since, syscap, deprecated = extract_jsdoc_metadata(ns_body, m.start())
                result['functions'].append({
                    'name': fname,
                    'params': m.group(2).strip(),
                    'return_type': m.group(3).strip(),
                    'signature': f"{fname}({m.group(2).strip()}): {m.group(3).strip()}",
                    'since': since if since is not None else file_since,
                    'syscap': syscap if syscap is not None else file_syscap,
                    'deprecated': deprecated,
                })

        # Parse type declarations inside namespace
        ns_interfaces, ns_classes, ns_enums = parse_type_declarations(
            ns_body, file_since, file_syscap
        )

        for iface in ns_interfaces:
            if not any(i['name'] == iface['name'] for i in result['interfaces']):
                result['interfaces'].append(iface)

        for cls in ns_classes:
            if not any(c['name'] == cls['name'] for c in result['classes']):
                result['classes'].append(cls)

        for enum in ns_enums:
            if not any(e['name'] == enum['name'] for e in result['enums']):
                result['enums'].append(enum)

    return result


def import_to_db(db_path, api_dir):
    conn = sqlite3.connect(db_path)
    c = conn.cursor()

    module_count = 0
    type_count = 0
    api_count = 0

    # Find all .d.ts and .d.ets files
    dts_files = glob.glob(os.path.join(api_dir, '*.d.ts'))
    dts_files += glob.glob(os.path.join(api_dir, '*.d.ets'))
    # Also check subdirectories
    dts_files += glob.glob(os.path.join(api_dir, '**', '*.d.ts'), recursive=True)
    dts_files += glob.glob(os.path.join(api_dir, '**', '*.d.ets'), recursive=True)

    # ArkUI component declarations
    internal_dir = os.path.join(api_dir, '@internal', 'component', 'ets')
    if os.path.isdir(internal_dir):
        dts_files += glob.glob(os.path.join(internal_dir, '*.d.ts'))
        dts_files += glob.glob(os.path.join(internal_dir, '*.d.ets'))

    # Deduplicate
    dts_files = sorted(set(dts_files))

    print(f"Found {len(dts_files)} declaration files")

    # Precompute the internal component directory for module naming
    internal_component_dir = os.path.normpath(
        os.path.join(api_dir, '@internal', 'component', 'ets')
    )

    for filepath in dts_files:
        try:
            parsed = parse_dts_file(filepath)
        except Exception as e:
            print(f"  Error parsing {filepath}: {e}")
            continue

        module_name = parsed['module_name']
        if not module_name:
            continue

        # Override module name and subsystem for @internal/component/ets/ files
        norm_filepath = os.path.normpath(filepath)
        if norm_filepath.startswith(internal_component_dir):
            basename = os.path.basename(filepath)
            comp_name = basename.replace('.d.ts', '').replace('.d.ets', '')
            module_name = f"arkui/component/{comp_name}"
            subsystem = 'UI Framework'
        else:
            subsystem = get_oh_subsystem(module_name)

        rel_path = os.path.relpath(filepath, os.path.expanduser("~/openharmony/"))

        # Insert module
        c.execute("""
            INSERT OR IGNORE INTO oh_modules
            (name, sdk_type, subsystem, file_path, kit, syscap, since_version)
            VALUES (?, 'js', ?, ?, ?, ?, ?)
        """, (module_name, subsystem, rel_path, parsed['kit'],
              parsed['syscap'], parsed['since']))

        module_id = c.execute(
            "SELECT id FROM oh_modules WHERE name=?", (module_name,)
        ).fetchone()[0]
        module_count += 1

        # Import interfaces
        for iface in parsed['interfaces']:
            c.execute("""
                INSERT OR IGNORE INTO oh_types (module_id, name, kind, syscap, since_version)
                VALUES (?, ?, 'interface', ?, ?)
            """, (module_id, iface['name'], parsed['syscap'], parsed['since']))
            type_id = c.execute(
                "SELECT id FROM oh_types WHERE module_id=? AND name=?",
                (module_id, iface['name'])
            ).fetchone()[0]
            type_count += 1

            for method in iface['methods']:
                c.execute("""
                    INSERT INTO oh_apis
                    (type_id, module_id, kind, name, signature, return_type, params,
                     subsystem, since_version, syscap, deprecated)
                    VALUES (?, ?, 'method', ?, ?, ?, ?, ?, ?, ?, ?)
                """, (type_id, module_id, method['name'], method['signature'],
                      method['return_type'], method['params'], subsystem,
                      method.get('since'), method.get('syscap'),
                      method.get('deprecated', False)))
                api_count += 1

            for prop in iface.get('properties', []):
                c.execute("""
                    INSERT INTO oh_apis
                    (type_id, module_id, kind, name, signature, return_type,
                     is_readonly, is_optional, subsystem, since_version, syscap, deprecated)
                    VALUES (?, ?, 'property', ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, (type_id, module_id, prop['name'], prop['signature'],
                      prop['type'], prop['readonly'], prop['optional'], subsystem,
                      prop.get('since'), prop.get('syscap'),
                      prop.get('deprecated', False)))
                api_count += 1

        # Import classes
        for cls in parsed['classes']:
            c.execute("""
                INSERT OR IGNORE INTO oh_types (module_id, name, kind, syscap, since_version)
                VALUES (?, ?, 'class', ?, ?)
            """, (module_id, cls['name'], parsed['syscap'], parsed['since']))
            type_id = c.execute(
                "SELECT id FROM oh_types WHERE module_id=? AND name=?",
                (module_id, cls['name'])
            ).fetchone()[0]
            type_count += 1

            for method in cls['methods']:
                c.execute("""
                    INSERT INTO oh_apis
                    (type_id, module_id, kind, name, signature, return_type, params,
                     subsystem, since_version, syscap, deprecated)
                    VALUES (?, ?, 'method', ?, ?, ?, ?, ?, ?, ?, ?)
                """, (type_id, module_id, method['name'], method['signature'],
                      method['return_type'], method['params'], subsystem,
                      method.get('since'), method.get('syscap'),
                      method.get('deprecated', False)))
                api_count += 1

            for prop in cls.get('properties', []):
                c.execute("""
                    INSERT INTO oh_apis
                    (type_id, module_id, kind, name, signature, return_type,
                     is_readonly, is_optional, subsystem, since_version, syscap, deprecated)
                    VALUES (?, ?, 'property', ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, (type_id, module_id, prop['name'], prop['signature'],
                      prop['type'], prop['readonly'], prop['optional'], subsystem,
                      prop.get('since'), prop.get('syscap'),
                      prop.get('deprecated', False)))
                api_count += 1

        # Import enums
        for enum in parsed['enums']:
            c.execute("""
                INSERT OR IGNORE INTO oh_types (module_id, name, kind, syscap, since_version)
                VALUES (?, ?, 'enum', ?, ?)
            """, (module_id, enum['name'], parsed['syscap'], parsed['since']))
            type_id = c.execute(
                "SELECT id FROM oh_types WHERE module_id=? AND name=?",
                (module_id, enum['name'])
            ).fetchone()[0]
            type_count += 1

            for val in enum['values']:
                c.execute("""
                    INSERT INTO oh_apis
                    (type_id, module_id, kind, name, signature, subsystem,
                     since_version, syscap, deprecated)
                    VALUES (?, ?, 'enum_value', ?, ?, ?, ?, ?, ?)
                """, (type_id, module_id, val['name'], val['signature'], subsystem,
                      val.get('since'), val.get('syscap'),
                      val.get('deprecated', False)))
                api_count += 1

        # Import standalone functions
        for func in parsed['functions']:
            c.execute("""
                INSERT INTO oh_apis
                (type_id, module_id, kind, name, signature, return_type, params,
                 subsystem, since_version, syscap, deprecated)
                VALUES (NULL, ?, 'function', ?, ?, ?, ?, ?, ?, ?, ?)
            """, (module_id, func['name'], func['signature'],
                  func['return_type'], func['params'], subsystem,
                  func.get('since'), func.get('syscap'),
                  func.get('deprecated', False)))
            api_count += 1

        # Import type aliases
        for ta in parsed['type_aliases']:
            c.execute("""
                INSERT OR IGNORE INTO oh_types (module_id, name, kind, syscap, since_version)
                VALUES (?, ?, 'type', ?, ?)
            """, (module_id, ta['name'], parsed['syscap'], parsed['since']))
            type_count += 1

    conn.commit()

    # Build FTS index
    print("Building FTS index for OH APIs...")
    c.execute("""
        INSERT INTO oh_apis_fts(rowid, name, signature, subsystem, description)
        SELECT id, name, signature, subsystem, COALESCE(description, '')
        FROM oh_apis
    """)
    conn.commit()

    print(f"Imported: {module_count} modules, {type_count} types, {api_count} APIs")
    conn.close()
    return module_count, type_count, api_count


def main():
    print(f"Importing OpenHarmony JS/TS APIs from {API_DIR}...")
    modules, types, apis = import_to_db(DB_FILE, API_DIR)
    print(f"\nDone!")
    print(f"  Modules: {modules}")
    print(f"  Types: {types}")
    print(f"  APIs: {apis}")


if __name__ == '__main__':
    main()
