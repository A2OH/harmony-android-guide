#!/usr/bin/env python3
"""apply_bundle_file_util.py — patch BundleFileUtil::CheckFilePath to accept .apk.

Background:
    bm install -p <path>.apk fails with code 9568269 (ERR_INSTALL_FILE_PATH_INVALID)
    because BundleInstallerProxy::StreamInstall calls BundleFileUtil::CheckFilePath
    which whitelists only {.hap, .hsp, .hqf, .abc, .sig, .ap, .json, .app}. The
    OH_ADAPTER_ANDROID branch in bundle_installer.cpp only fires AFTER stream
    install passes this gate, so without this patch the .apk path is rejected
    before reaching the adapter dispatcher.

What this script does:
    1. Inserts an additional check `!CheckFileType(bundlePath, ".apk")` at the
       end of the suffix whitelist chain in CheckFilePath, guarded by
       #ifdef OH_ADAPTER_ANDROID (the project-wide marker also used in
       bundle_installer.cpp / BUILD.gn).
    2. Updates the matching error message to mention apk.

Idempotent: re-running detects existing patch and does nothing.

Companion: apply_BUILD_gn.py adds OH_ADAPTER_ANDROID define to libappexecfwk_common
so the #ifdef guard activates.

Usage:
    apply_bundle_file_util.py [--src /path/to/bundle_file_util.cpp]
"""
import argparse
import os
import sys

DEFAULT_SRC = (
    "/home/HanBingChen/oh/foundation/bundlemanager/bundle_framework/"
    "common/utils/src/bundle_file_util.cpp"
)

ANCHOR_OLD = (
    "        !CheckFileType(bundlePath, APP_FILE_SUFFIX)) {\n"
    "        APP_LOGE(\"file is not hap, hsp or hqf or sig or ap or json or app\");\n"
    "        return false;\n"
    "    }\n"
)

ANCHOR_NEW = (
    "        !CheckFileType(bundlePath, APP_FILE_SUFFIX)\n"
    "#ifdef OH_ADAPTER_ANDROID\n"
    "        // Adapter project: allow Android .apk extension; downstream\n"
    "        // BundleInstaller::Install dispatches .apk via libapk_installer.so.\n"
    "        && !CheckFileType(bundlePath, \".apk\")\n"
    "#endif\n"
    "        ) {\n"
    "        APP_LOGE(\"file is not hap, hsp or hqf or sig or ap or json or app or apk\");\n"
    "        return false;\n"
    "    }\n"
)

PATCH_MARKER = "// Adapter project: allow Android .apk extension"


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--src", default=DEFAULT_SRC)
    args = ap.parse_args()

    if not os.path.isfile(args.src):
        sys.exit(f"ERROR: source not found: {args.src}")

    with open(args.src, "r", encoding="utf-8") as f:
        text = f.read()

    if PATCH_MARKER in text:
        print(f"[skip] {args.src} already patched")
        return

    # Backup once
    bak = args.src + ".adapter_orig"
    if not os.path.exists(bak):
        with open(bak, "w", encoding="utf-8") as f:
            f.write(text)
        print(f"[backup] {bak}")

    if ANCHOR_OLD not in text:
        sys.exit(
            "ERROR: anchor block for CheckFilePath suffix chain not found.\n"
            "       OH source may have changed; re-derive patch against current bundle_file_util.cpp."
        )

    new_text = text.replace(ANCHOR_OLD, ANCHOR_NEW, 1)
    with open(args.src, "w", encoding="utf-8") as f:
        f.write(new_text)
    print(f"[patched] {args.src}: added .apk extension under #ifdef OH_ADAPTER_ANDROID")


if __name__ == "__main__":
    main()
