#!/usr/bin/env python3
"""apply_init_max_env_value.py — bump MAX_ENV_VALUE 128 -> 1024 so OH
init can carry large env values (notably BOOTCLASSPATH ~300 bytes for
the appspawn-x service).

Background: OH init stores cfg env[] entries in a fixed 128-byte buffer
in ServiceEnv.value[]. init_service_manager.c strcpy_s's srcLen+1 as
destMax, which silently truncates (and can overflow into the adjacent
ServiceEnv struct) when the value exceeds 127 bytes. BOOTCLASSPATH
with 8 jar paths colon-separated is ~300 bytes, far over the limit.

Symptom: appspawn-x init service SIGSEGV early (layer 4 crash in
project_init_service_checkpoint.md). Manual launch with the same env
from shell works fine — the difference is init's truncation/corruption
of the env values.

Fix: bump MAX_ENV_VALUE to 1024. 1KB per env value is more than enough
for any realistic path list and stays well under typical stack sizes.

Modifies 1 OH file:
  base/startup/init/services/init/include/init_service.h

Idempotent. Run with --dry-run to preview.
"""
import argparse, os, shutil, sys

MARKER = "// adapter project: MAX_ENV_VALUE bumped for BOOTCLASSPATH"


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--oh-root", default="/home/HanBingChen/oh")
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    path = os.path.join(args.oh_root,
        "base/startup/init/services/init/include/init_service.h")

    if not os.path.exists(path):
        print(f"FAIL {path} (not found)")
        sys.exit(1)

    with open(path, "r") as f:
        content = f.read()
    if MARKER in content:
        print(f"SKIP {path} (already has marker)")
        return

    anchor = "#define MAX_ENV_VALUE 128\n"
    new = "#define MAX_ENV_VALUE 1024  " + MARKER + "\n"

    if anchor not in content:
        # maybe already patched with different value
        if "#define MAX_ENV_VALUE 1024" in content:
            print(f"SKIP {path} (already 1024)")
            return
        print(f"FAIL {path} — anchor {anchor!r} not found")
        sys.exit(1)

    bak = path + ".adapter_orig"
    if not os.path.exists(bak) and not args.dry_run:
        shutil.copy(path, bak)

    content = content.replace(anchor, new, 1)

    if args.dry_run:
        print(f"DRY  {path}")
        return
    with open(path, "w") as f:
        f.write(content)
    print(f"DONE {path}  (MAX_ENV_VALUE 128 -> 1024)")
    print("Rebuild init: ./build.sh --product-name rk3568 --build-target init")


if __name__ == "__main__":
    main()
