#!/usr/bin/env python3
"""apply_BUILD_gn.py — add OH_ADAPTER_ANDROID define to libappexecfwk_common.

Without this, the #ifdef OH_ADAPTER_ANDROID guard in bundle_file_util.cpp
(see apply_bundle_file_util.py) compiles out and bm install -p <x>.apk
still gets rejected.

Idempotent.

Usage:
    apply_BUILD_gn.py [--build-gn /path/to/common/BUILD.gn]
"""
import argparse
import os
import sys

DEFAULT_BUILD = (
    "/home/HanBingChen/oh/foundation/bundlemanager/bundle_framework/common/BUILD.gn"
)

ANCHOR_OLD = (
    "  defines = [\n"
    "    \"APP_LOG_TAG = \\\"BMS\\\"\",\n"
    "    \"LOG_DOMAIN = 0xD001120\",\n"
    "  ]\n"
)

ANCHOR_NEW = (
    "  defines = [\n"
    "    \"APP_LOG_TAG = \\\"BMS\\\"\",\n"
    "    \"LOG_DOMAIN = 0xD001120\",\n"
    "    \"OH_ADAPTER_ANDROID\",  # Adapter project: enable .apk in CheckFilePath\n"
    "  ]\n"
)

PATCH_MARKER = "OH_ADAPTER_ANDROID"


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--build-gn", default=DEFAULT_BUILD)
    args = ap.parse_args()

    if not os.path.isfile(args.build_gn):
        sys.exit(f"ERROR: BUILD.gn not found: {args.build_gn}")

    with open(args.build_gn, "r", encoding="utf-8") as f:
        text = f.read()

    if PATCH_MARKER in text:
        print(f"[skip] {args.build_gn} already has OH_ADAPTER_ANDROID")
        return

    bak = args.build_gn + ".adapter_orig"
    if not os.path.exists(bak):
        with open(bak, "w", encoding="utf-8") as f:
            f.write(text)
        print(f"[backup] {bak}")

    if ANCHOR_OLD not in text:
        sys.exit(
            "ERROR: defines anchor block not found in libappexecfwk_common BUILD.gn.\n"
            "       OH source may have changed; re-derive patch."
        )

    new_text = text.replace(ANCHOR_OLD, ANCHOR_NEW, 1)
    with open(args.build_gn, "w", encoding="utf-8") as f:
        f.write(new_text)
    print(f"[patched] {args.build_gn}: added OH_ADAPTER_ANDROID define")


if __name__ == "__main__":
    main()
