#!/usr/bin/env python3
"""apply_bms_apk_gates.py — open .apk in libbms server-side suffix gates.

Three gates inside libbms reject anything not {.hap, .hsp, .hqf, .sig, .app}:
  1. bundle_util.cpp:CheckFilePath              ("file is not hap, hsp, hqf or sig")
  2. bundle_stream_installer_host_impl.cpp:CreateStream            ("file is not hap or hsp or app")
  3. bundle_stream_installer_host_impl.cpp:CreateSharedBundleStream ("file is not hap or hsp")

bm install -p X.apk hits gate #2 first (server-side after StreamInstall proxy
already passes the client-side bundle_file_util.cpp check that we patched in
common/apply_bundle_file_util.py). All three are guarded behind
#ifdef OH_ADAPTER_ANDROID — libbms BUILD.gn already has that define
(see ohos_patches/bundle_framework/services/bundlemgr/apply_BUILD_gn.py).

Idempotent (PATCH_MARKER detects prior application).

Usage:
    apply_bms_apk_gates.py [--bms-src-dir /path/to/services/bundlemgr/src]
"""
import argparse
import os
import sys

DEFAULT_DIR = (
    "/home/HanBingChen/oh/foundation/bundlemanager/bundle_framework/"
    "services/bundlemgr/src"
)

PATCH_MARKER = "// adapter project: allow .apk extension"


def patch_file(path: str, anchor: str, replacement: str) -> bool:
    if not os.path.isfile(path):
        sys.exit(f"ERROR: source not found: {path}")
    with open(path, "r", encoding="utf-8") as f:
        text = f.read()
    if PATCH_MARKER in text:
        print(f"[skip] {path} already patched")
        return False
    bak = path + ".adapter_orig"
    if not os.path.exists(bak):
        with open(bak, "w", encoding="utf-8") as f:
            f.write(text)
        print(f"[backup] {bak}")
    if anchor not in text:
        sys.exit(f"ERROR: anchor not found in {path}; OH source may have shifted")
    text = text.replace(anchor, replacement, 1)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print(f"[patched] {path}")
    return True


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--bms-src-dir", default=DEFAULT_DIR)
    args = ap.parse_args()

    # ---- Gate 1: bundle_util.cpp::CheckFilePath ----
    util_path = os.path.join(args.bms_src_dir, "bundle_util.cpp")
    util_old = (
        "    if (!CheckFileType(bundlePath, ServiceConstants::INSTALL_FILE_SUFFIX) &&\n"
        "        !CheckFileType(bundlePath, ServiceConstants::HSP_FILE_SUFFIX) &&\n"
        "        !CheckFileType(bundlePath, ServiceConstants::QUICK_FIX_FILE_SUFFIX) &&\n"
        "        !CheckFileType(bundlePath, ServiceConstants::CODE_SIGNATURE_FILE_SUFFIX)) {\n"
        "        APP_LOGE(\"file is not hap, hsp, hqf or sig\");\n"
        "        return ERR_APPEXECFWK_INSTALL_INVALID_HAP_NAME;\n"
        "    }\n"
    )
    util_new = (
        "    if (!CheckFileType(bundlePath, ServiceConstants::INSTALL_FILE_SUFFIX) &&\n"
        "        !CheckFileType(bundlePath, ServiceConstants::HSP_FILE_SUFFIX) &&\n"
        "        !CheckFileType(bundlePath, ServiceConstants::QUICK_FIX_FILE_SUFFIX) &&\n"
        "        !CheckFileType(bundlePath, ServiceConstants::CODE_SIGNATURE_FILE_SUFFIX)\n"
        "#ifdef OH_ADAPTER_ANDROID\n"
        "        // adapter project: allow .apk extension (Route C dispatcher in BundleInstaller::Install)\n"
        "        && !CheckFileType(bundlePath, \".apk\")\n"
        "#endif\n"
        "        ) {\n"
        "        APP_LOGE(\"file is not hap, hsp, hqf or sig or apk\");\n"
        "        return ERR_APPEXECFWK_INSTALL_INVALID_HAP_NAME;\n"
        "    }\n"
    )
    patch_file(util_path, util_old, util_new)

    # ---- Gates 2 + 3: bundle_stream_installer_host_impl.cpp ----
    host_path = os.path.join(args.bms_src_dir, "bundle_stream_installer_host_impl.cpp")
    # Gate 2 — CreateStream ("file is not hap or hsp or app")
    g2_old = (
        "    if (!BundleUtil::CheckFileType(fileName, ServiceConstants::INSTALL_FILE_SUFFIX) &&\n"
        "        !BundleUtil::CheckFileType(fileName, ServiceConstants::HSP_FILE_SUFFIX) &&\n"
        "        !BundleUtil::CheckFileType(fileName, ServiceConstants::APP_FILE_SUFFIX)) {\n"
        "        APP_LOGE(\"file is not hap or hsp or app\");\n"
        "        return Constants::DEFAULT_STREAM_FD;\n"
        "    }\n"
    )
    g2_new = (
        "    if (!BundleUtil::CheckFileType(fileName, ServiceConstants::INSTALL_FILE_SUFFIX) &&\n"
        "        !BundleUtil::CheckFileType(fileName, ServiceConstants::HSP_FILE_SUFFIX) &&\n"
        "        !BundleUtil::CheckFileType(fileName, ServiceConstants::APP_FILE_SUFFIX)\n"
        "#ifdef OH_ADAPTER_ANDROID\n"
        "        // adapter project: allow .apk extension\n"
        "        && !BundleUtil::CheckFileType(fileName, \".apk\")\n"
        "#endif\n"
        "        ) {\n"
        "        APP_LOGE(\"file is not hap or hsp or app or apk\");\n"
        "        return Constants::DEFAULT_STREAM_FD;\n"
        "    }\n"
    )
    patch_file(host_path, g2_old, g2_new)

    # Gate 3 — CreateSharedBundleStream ("file is not hap or hsp")
    g3_old = (
        "    if (!BundleUtil::CheckFileType(hspName, ServiceConstants::INSTALL_FILE_SUFFIX) &&\n"
        "        !BundleUtil::CheckFileType(hspName, ServiceConstants::HSP_FILE_SUFFIX) &&\n"
        "        !BundleUtil::CheckFileType(hspName, ServiceConstants::CODE_SIGNATURE_FILE_SUFFIX)) {\n"
        "        APP_LOGE(\"file is not hap or hsp\");\n"
        "        return Constants::DEFAULT_STREAM_FD;\n"
        "    }\n"
    )
    g3_new = (
        "    if (!BundleUtil::CheckFileType(hspName, ServiceConstants::INSTALL_FILE_SUFFIX) &&\n"
        "        !BundleUtil::CheckFileType(hspName, ServiceConstants::HSP_FILE_SUFFIX) &&\n"
        "        !BundleUtil::CheckFileType(hspName, ServiceConstants::CODE_SIGNATURE_FILE_SUFFIX)\n"
        "#ifdef OH_ADAPTER_ANDROID\n"
        "        // adapter project: allow .apk extension (covers \"two-stream\" install path used by some bm flows)\n"
        "        && !BundleUtil::CheckFileType(hspName, \".apk\")\n"
        "#endif\n"
        "        ) {\n"
        "        APP_LOGE(\"file is not hap or hsp or apk\");\n"
        "        return Constants::DEFAULT_STREAM_FD;\n"
        "    }\n"
    )
    # Gate 2 + 3 share the same PATCH_MARKER on the file already; second patch_file
    # call will [skip] silently. Apply gate 3 only if gate 2 didn't already mark
    # the whole file. To keep both patches independent we instead pass them as a
    # combined replacement when called twice — but since g2_new and g3_new both
    # contain PATCH_MARKER, the second call's PATCH_MARKER check will short-circuit.
    # Workaround: do gate 3 on backup-comparison instead.
    with open(host_path, "r", encoding="utf-8") as f:
        text2 = f.read()
    if g3_old in text2:
        text2 = text2.replace(g3_old, g3_new, 1)
        with open(host_path, "w", encoding="utf-8") as f:
            f.write(text2)
        print(f"[patched] {host_path} (gate 3 — CreateSharedBundleStream)")
    else:
        print(f"[skip] {host_path} gate 3 already patched or anchor missing")


if __name__ == "__main__":
    main()
