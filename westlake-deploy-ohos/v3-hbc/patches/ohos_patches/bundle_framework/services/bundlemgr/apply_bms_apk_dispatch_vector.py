#!/usr/bin/env python3
"""apply_bms_apk_dispatch_vector.py — add OH_ADAPTER_ANDROID dispatch to vector Install overload.

Background:
    bm install -p X.apk goes through stream-install path:
        BundleStreamInstallerHostImpl::Install()
          → BundleInstaller::Install(vector<string>, InstallParam)   ← needs dispatch
          → InstallBundle(vector, ...) → ProcessBundleInstall → CheckMultipleHapsSignInfo (rejects .apk)

    The string overload BundleInstaller::Install(string, InstallParam) already has
    the OH_ADAPTER_ANDROID branch (Route C) but it isn't reached on stream-install.

    This script adds the SAME dispatch to the vector overload, before InstallBundle
    is called. If any path in the vector ends with .apk, dlopen libapk_installer.so
    + dispatch each .apk to oh_adapter_install_apk + OnFinished + return.
    Pure .hap batches fall through to the original code path.

Idempotent.

Usage:
    apply_bms_apk_dispatch_vector.py [--installer-cpp /path/to/bundle_installer.cpp]
"""
import argparse
import os
import sys

DEFAULT = (
    "/home/HanBingChen/oh/foundation/bundlemanager/bundle_framework/"
    "services/bundlemgr/src/bundle_installer.cpp"
)

PATCH_MARKER = "// adapter project: vector-overload .apk dispatch"

ANCHOR_OLD = (
    "void BundleInstaller::Install(const std::vector<std::string> &bundleFilePaths, const InstallParam &installParam)\n"
    "{\n"
    "    ErrCode resultCode = ERR_OK;\n"
    "    if (installParam.userId == Constants::ALL_USERID) {\n"
)

ANCHOR_NEW = (
    "void BundleInstaller::Install(const std::vector<std::string> &bundleFilePaths, const InstallParam &installParam)\n"
    "{\n"
    "#ifdef OH_ADAPTER_ANDROID\n"
    "    // adapter project: vector-overload .apk dispatch.\n"
    "    // bm install -p X.apk takes the stream-install path which calls THIS\n"
    "    // overload (not the string overload). If any path ends in .apk, route\n"
    "    // the whole batch through the libapk_installer.so dlopen entry and\n"
    "    // skip the OH HAP signature/decompress/install pipeline entirely.\n"
    "    {\n"
    "        bool anyApk = false;\n"
    "        for (const auto &p : bundleFilePaths) {\n"
    "            if (p.size() > 4 && p.compare(p.size() - 4, 4, \".apk\") == 0) {\n"
    "                anyApk = true;\n"
    "                break;\n"
    "            }\n"
    "        }\n"
    "        if (anyApk) {\n"
    "            AdapterApkInstallFn installFn = LoadAdapterApkInstaller();\n"
    "            ErrCode rc = ERR_OK;\n"
    "            std::string finalMsg = \"Install success\";\n"
    "            if (installFn == nullptr) {\n"
    "                rc = static_cast<ErrCode>(kErrAdapterNotAvailable);\n"
    "                finalMsg = \"APK adapter unavailable\";\n"
    "            } else {\n"
    "                for (const auto &p : bundleFilePaths) {\n"
    "                    if (p.size() > 4 && p.compare(p.size() - 4, 4, \".apk\") == 0) {\n"
    "                        int r = installFn(p.c_str(), installParam.userId);\n"
    "                        if (r != 0) {\n"
    "                            rc = static_cast<ErrCode>(r);\n"
    "                            finalMsg = \"APK install failed\";\n"
    "                            break;\n"
    "                        }\n"
    "                    }\n"
    "                }\n"
    "            }\n"
    "            if (statusReceiver_ != nullptr) {\n"
    "                statusReceiver_->OnFinished(rc, finalMsg);\n"
    "            }\n"
    "            return;\n"
    "        }\n"
    "    }\n"
    "#endif\n"
    "    ErrCode resultCode = ERR_OK;\n"
    "    if (installParam.userId == Constants::ALL_USERID) {\n"
)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--installer-cpp", default=DEFAULT)
    args = ap.parse_args()

    if not os.path.isfile(args.installer_cpp):
        sys.exit(f"ERROR: source not found: {args.installer_cpp}")

    with open(args.installer_cpp, "r", encoding="utf-8") as f:
        text = f.read()

    if PATCH_MARKER in text:
        print(f"[skip] {args.installer_cpp} already patched (vector dispatch)")
        return

    bak = args.installer_cpp + ".adapter_orig_vector"
    if not os.path.exists(bak):
        with open(bak, "w", encoding="utf-8") as f:
            f.write(text)
        print(f"[backup] {bak}")

    if ANCHOR_OLD not in text:
        sys.exit(
            "ERROR: anchor for vector Install overload not found.\n"
            "       OH source may have shifted or string overload was previously rewritten."
        )

    text = text.replace(ANCHOR_OLD, ANCHOR_NEW, 1)
    with open(args.installer_cpp, "w", encoding="utf-8") as f:
        f.write(text)
    print(f"[patched] {args.installer_cpp} — vector Install overload now dispatches .apk")


if __name__ == "__main__":
    main()
