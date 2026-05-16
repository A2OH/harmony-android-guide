#!/usr/bin/env python3
"""apply_bms_apk_dispatch_processinstall.py — .apk dispatch in ProcessBundleInstall.

Why this is the definitive dispatch point (after 3 prior wrong tries):

  String-overload BundleInstaller::Install         — only called from non-stream
                                                     code paths; bm install -p
                                                     never reaches it.
  Vector-overload BundleInstaller::Install         — sees [tempDir_], not the
                                                     .apk filename. Dispatch
                                                     can't trigger.
  BundleStreamInstallerHostImpl::InstallApp        — its appPaths is built by
                                                     GetAppFilesFromBundlePath
                                                     which only allows .app
                                                     suffix (line 417). .apk
                                                     never enters appPaths,
                                                     so dispatch never fires.

  BaseBundleInstaller::ProcessBundleInstall        — calls ParseHapPaths which
                                                     enumerates dir contents
                                                     into parsedPaths (real
                                                     .apk filenames). At THIS
                                                     point we have the .apk
                                                     paths in hand and the
                                                     subsequent
                                                     CheckMultipleHapsSignInfo
                                                     is what currently rejects
                                                     them with code 9568318.
                                                     This is the right gate.

Action: right after ParseHapPaths populates parsedPaths, if any entry ends
with .apk, dlopen libapk_installer.so + dispatch each .apk via
oh_adapter_install_apk + return ERR_OK / error so ProcessBundleInstall
short-circuits before CheckMultipleHapsSignInfo. statusReceiver_ doesn't
exist in BaseBundleInstaller scope — the caller (BundleInstaller::Install
vector-overload) will call statusReceiver_->OnFinished with the ErrCode we
return, so user-visible result is preserved.

Idempotent.
"""
import argparse
import os
import sys

DEFAULT = (
    "/home/HanBingChen/oh/foundation/bundlemanager/bundle_framework/"
    "services/bundlemgr/src/base_bundle_installer.cpp"
)

PATCH_MARKER = "// adapter project: .apk dispatch before HAP signature pipeline"

# Dispatch must use bundlePaths (the actual .apk file paths after BundleUtil::CheckFilePath
# enumerates the temp directory). parsedPaths is just the directory, no .apk match.
# Anchor right after bundlePaths is populated and BEFORE CopyHapsToSecurityDir +
# CheckMultipleHapsSignInfo (which is what currently rejects .apk).
ANCHOR_OLD = (
    "    std::vector<std::string> bundlePaths;\n"
    "    // check hap paths\n"
    "    result = BundleUtil::CheckFilePath(parsedPaths, bundlePaths);\n"
    "    CHECK_RESULT(result, \"hap file check failed %{public}d\");\n"
    "    UpdateInstallerState(InstallerState::INSTALL_BUNDLE_CHECKED);                  // ---- 5%\n"
)

ANCHOR_NEW = (
    "    std::vector<std::string> bundlePaths;\n"
    "    // check hap paths\n"
    "    result = BundleUtil::CheckFilePath(parsedPaths, bundlePaths);\n"
    "    CHECK_RESULT(result, \"hap file check failed %{public}d\");\n"
    "    UpdateInstallerState(InstallerState::INSTALL_BUNDLE_CHECKED);                  // ---- 5%\n"
    "\n"
    "#ifdef OH_ADAPTER_ANDROID\n"
    "    // adapter project: .apk dispatch before HAP signature pipeline.\n"
    "    // bundlePaths now holds the resolved real paths of files inside tempDir_;\n"
    "    // for bm install -p X.apk it is [.../HelloWorld.apk]. Route .apk\n"
    "    // batches to libapk_installer.so + skip CheckMultipleHapsSignInfo / parse.\n"
    "    {\n"
    "        bool anyApk = false;\n"
    "        for (const auto &p : bundlePaths) {\n"
    "            if (p.size() > 4 && p.compare(p.size() - 4, 4, \".apk\") == 0) {\n"
    "                anyApk = true;\n"
    "                break;\n"
    "            }\n"
    "        }\n"
    "        if (anyApk) {\n"
    "            LOG_I(BMS_TAG_INSTALLER, \"adapter .apk path detected, dispatching\");\n"
    "            void* handle = dlopen(\"libapk_installer.so\", RTLD_NOW | RTLD_GLOBAL);\n"
    "            using ApkFn = int (*)(const char*, int);\n"
    "            ApkFn fn = handle ? reinterpret_cast<ApkFn>(\n"
    "                dlsym(handle, \"oh_adapter_install_apk\")) : nullptr;\n"
    "            if (fn == nullptr) {\n"
    "                LOG_E(BMS_TAG_INSTALLER, \"oh_adapter_install_apk symbol unavailable\");\n"
    "                return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;\n"
    "            }\n"
    "            for (const auto &p : bundlePaths) {\n"
    "                if (!(p.size() > 4 && p.compare(p.size() - 4, 4, \".apk\") == 0)) continue;\n"
    "                int r = fn(p.c_str(), installParam.userId);\n"
    "                if (r != 0) {\n"
    "                    LOG_E(BMS_TAG_INSTALLER, \"oh_adapter_install_apk failed: %{public}d\", r);\n"
    "                    return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;\n"
    "                }\n"
    "            }\n"
    "            LOG_I(BMS_TAG_INSTALLER, \"adapter .apk install path completed\");\n"
    "            return ERR_OK;\n"
    "        }\n"
    "    }\n"
    "#endif\n"
)

DLFCN_INCLUDE_MARKER = "<dlfcn.h>"
INCLUDE_ANCHOR_OLD = "#include \"base_bundle_installer.h\"\n"
INCLUDE_ANCHOR_NEW = (
    "#include \"base_bundle_installer.h\"\n"
    "#ifdef OH_ADAPTER_ANDROID\n"
    "#include <dlfcn.h>  // adapter project: libapk_installer.so dispatch\n"
    "#endif\n"
)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--src", default=DEFAULT)
    args = ap.parse_args()

    if not os.path.isfile(args.src):
        sys.exit(f"ERROR: source not found: {args.src}")

    with open(args.src, "r", encoding="utf-8") as f:
        text = f.read()

    if PATCH_MARKER in text:
        print(f"[skip] {args.src} already patched")
        return

    bak = args.src + ".adapter_orig_processinstall"
    if not os.path.exists(bak):
        with open(bak, "w", encoding="utf-8") as f:
            f.write(text)
        print(f"[backup] {bak}")

    if INCLUDE_ANCHOR_OLD not in text:
        sys.exit("ERROR: include anchor not found in base_bundle_installer.cpp")
    if ANCHOR_OLD not in text:
        sys.exit("ERROR: ParseHapPaths anchor not found")

    if DLFCN_INCLUDE_MARKER not in text:
        text = text.replace(INCLUDE_ANCHOR_OLD, INCLUDE_ANCHOR_NEW, 1)

    text = text.replace(ANCHOR_OLD, ANCHOR_NEW, 1)

    with open(args.src, "w", encoding="utf-8") as f:
        f.write(text)
    print(f"[patched] {args.src} — .apk dispatch in ProcessBundleInstall")


if __name__ == "__main__":
    main()
