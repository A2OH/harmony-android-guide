#!/usr/bin/env python3
"""Audit the Westlake guest/native boundary surfaces.

This intentionally counts source declarations and registration tables. It does
not claim that every registered method is a complete service implementation.
"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8", errors="replace")


def ohbridge_java_names(westlake: Path) -> set[str]:
    text = read(westlake / "shim/java/com/ohos/shim/bridge/OHBridge.java")
    return set(
        re.findall(
            r"public\s+static\s+native\s+[\w\[\]]+\s+(\w+)\s*\(",
            text,
            flags=re.S,
        )
    )


def westlake_launcher_native_count(westlake: Path) -> int:
    text = read(westlake / "shim/java/com/westlake/engine/WestlakeLauncher.java")
    return len(re.findall(r"\bprivate\s+static\s+native\b", text))


def android_shim_native_count(westlake: Path) -> int:
    count = 0
    for path in (westlake / "shim/java/android").rglob("*.java"):
        text = read(path)
        count += len(
            re.findall(
                r"^\s*(?:(?:public|private|protected|static|native|final)\s+)+"
                r".*\bnative\b.*\(",
                text,
                flags=re.M,
            )
        )
    return count


def ohbridge_static_registrations(art: Path) -> list[str]:
    text = read(art / "stubs/ohbridge_stub.c")
    start = text.index("static JNINativeMethod methods[]")
    end = text.index("};", start)
    return re.findall(r'\{"([^"]+)"', text[start:end])


def rust_ohbridge_exports(westlake: Path) -> set[str]:
    root = westlake / "shim/bridge/rust/src"
    names: set[str] = set()
    for path in root.rglob("*.rs"):
        text = read(path)
        names.update(re.findall(r"fn\s+Java_com_ohos_shim_bridge_OHBridge_(\w+)", text))
    return names


def android_host_registrations(westlake: Path) -> int:
    path = westlake / "westlake-host/jni/ohbridge_android.c"
    if not path.exists():
        return 0
    return len(re.findall(r'\{"[^"]+"', read(path)))


def runtime_gmethod_count(art: Path) -> tuple[int, int]:
    native_dir = art / "patches/runtime/native"
    entries = 0
    classes = 0
    for path in native_dir.glob("*.cc"):
        text = read(path)
        if "static JNINativeMethod gMethods[]" in text:
            classes += 1
        entries += len(
            re.findall(
                r"^\s*(?:NATIVE_METHOD|FAST_NATIVE_METHOD|CRITICAL_NATIVE_METHOD|"
                r"OVERLOADED_[A-Z_]*NATIVE_METHOD)\(",
                text,
                flags=re.M,
            )
        )
    return entries, classes


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--westlake",
        type=Path,
        default=Path(__file__).resolve().parents[1],
        help="android-to-openharmony-migration/westlake repository root",
    )
    parser.add_argument(
        "--art",
        type=Path,
        default=Path("/home/dspfac/art-latest"),
        help="art-latest repository root",
    )
    parser.add_argument("--check-static", action="store_true")
    args = parser.parse_args()

    java = ohbridge_java_names(args.westlake)
    regs = ohbridge_static_registrations(args.art)
    reg_set = set(regs)
    rust = rust_ohbridge_exports(args.westlake)
    runtime_entries, runtime_classes = runtime_gmethod_count(args.art)

    missing = sorted(java - reg_set)
    extra = sorted(reg_set - java)
    duplicates = sorted({name for name in regs if regs.count(name) > 1})

    print(f"OHBridge Java declarations: {len(java)}")
    print(f"OHBridge static registrations: {len(regs)} unique={len(reg_set)}")
    print(f"OHBridge static missing: {len(missing)}")
    print(f"OHBridge static extra: {len(extra)}")
    print(f"OHBridge static duplicate names: {len(duplicates)}")
    print(f"Rust OHBridge exports: {len(rust)}")
    print(f"Android host OHBridge registrations: {android_host_registrations(args.westlake)}")
    print(f"WestlakeLauncher native controls: {westlake_launcher_native_count(args.westlake)}")
    print(f"android.* shim native declarations: {android_shim_native_count(args.westlake)}")
    print(f"ART/libcore runtime native registrations: {runtime_entries} across {runtime_classes} classes")

    if missing:
        print("missing=" + ",".join(missing))
    if extra:
        print("extra=" + ",".join(extra))
    if duplicates:
        print("duplicates=" + ",".join(duplicates))

    if args.check_static and (missing or extra or duplicates):
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
