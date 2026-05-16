#!/usr/bin/env python3
"""apply_bms_apk_dispatch_installapp.py — add .apk dispatch to InstallApp.

The vector-overload patch (apply_bms_apk_dispatch_vector.py) was correct in
intent but the path it inspected was a temp DIRECTORY, not the .apk file —
because BundleStreamInstallerHostImpl::Install() passes [tempDir_] (e.g.
".../security_stream_install/15018417022/") down, not the .apk filename.

The actual .apk file path only materializes after GetAppFilesFromBundlePath
populates appPaths inside InstallApp. So the dispatch must happen there,
right after the early-out checks (empty/shared/isCallByShell) and BEFORE
the OH HAP signature verification (which is what currently rejects the .apk
with code 9568318).

Once InstallApp dispatches a .apk, it:
  1. dlopens libapk_installer.so + dlsym oh_adapter_install_apk
  2. invokes the entry for each appPath ending in .apk
  3. calls receiver_->OnFinished with the result
  4. cleans up the temp .apk file
  5. returns FALSE so BundleStreamInstallerHostImpl::Install() short-circuits
     before calling installer->Install (which would invoke OH HAP install
     pipeline that .apk can never satisfy). User-visible result is whatever
     receiver_->OnFinished was told.

#include <dlfcn.h> is added at the top of the file.

Idempotent.
"""
import argparse
import os
import sys

DEFAULT = (
    "/home/HanBingChen/oh/foundation/bundlemanager/bundle_framework/"
    "services/bundlemgr/src/bundle_stream_installer_host_impl.cpp"
)

PATCH_MARKER = "// adapter project: dispatch .apk batches"

# We anchor on the existing "empty path" early-return + ! sharedBundleDirPaths
# block, then inject our dispatch right after isCallByShell check passes.
ANCHOR_OLD = (
    "    if (!installParam_.isCallByShell) {\n"
    "        receiver_->OnFinished(ERR_APPEXECFWK_INSTALL_FILE_PATH_INVALID, \"\");\n"
    "        return false;\n"
    "    }\n"
    "\n"
    "    auto bundleInstallChecker = std::make_unique<BundleInstallChecker>();\n"
)

ANCHOR_NEW = (
    "    if (!installParam_.isCallByShell) {\n"
    "        receiver_->OnFinished(ERR_APPEXECFWK_INSTALL_FILE_PATH_INVALID, \"\");\n"
    "        return false;\n"
    "    }\n"
    "\n"
    "#ifdef OH_ADAPTER_ANDROID\n"
    "    // adapter project: dispatch .apk batches via libapk_installer.so before\n"
    "    // OH HAP signature verification (which always rejects .apk).\n"
    "    {\n"
    "        bool anyApk = false;\n"
    "        for (const auto &p : appPaths) {\n"
    "            if (p.size() > 4 && p.compare(p.size() - 4, 4, \".apk\") == 0) {\n"
    "                anyApk = true;\n"
    "                break;\n"
    "            }\n"
    "        }\n"
    "        if (anyApk) {\n"
    "            void* handle = dlopen(\"libapk_installer.so\", RTLD_NOW | RTLD_GLOBAL);\n"
    "            using ApkFn = int (*)(const char*, int);\n"
    "            ApkFn fn = handle ? reinterpret_cast<ApkFn>(\n"
    "                dlsym(handle, \"oh_adapter_install_apk\")) : nullptr;\n"
    "            int rc = 0;\n"
    "            if (fn == nullptr) {\n"
    "                APP_LOGE(\"oh_adapter_install_apk symbol unavailable\");\n"
    "                rc = -1001;\n"
    "            } else {\n"
    "                for (const auto &p : appPaths) {\n"
    "                    if (p.size() > 4 && p.compare(p.size() - 4, 4, \".apk\") == 0) {\n"
    "                        int r = fn(p.c_str(), installParam_.userId);\n"
    "                        if (r != 0) { rc = r; break; }\n"
    "                    }\n"
    "                }\n"
    "            }\n"
    "            // Best-effort cleanup of staged .apk files.\n"
    "            for (const auto &p : appPaths) {\n"
    "                std::error_code ec;\n"
    "                std::filesystem::remove(p, ec);\n"
    "            }\n"
    "            receiver_->OnFinished(\n"
    "                rc == 0 ? ERR_OK : ERR_APPEXECFWK_INSTALL_FAILED_INVALID_SIGNATURE_FILE_PATH,\n"
    "                rc == 0 ? \"APK install success\" : \"APK install failed\");\n"
    "            return false;  // short-circuit OH HAP pipeline\n"
    "        }\n"
    "    }\n"
    "#endif\n"
    "\n"
    "    auto bundleInstallChecker = std::make_unique<BundleInstallChecker>();\n"
)

# We also need to ensure dlfcn.h is included at the top.
DLFCN_INCLUDE_MARKER = "#include <dlfcn.h>"
INCLUDE_ANCHOR_OLD = "#include \"bundle_stream_installer_host_impl.h\"\n"
INCLUDE_ANCHOR_NEW = (
    "#include \"bundle_stream_installer_host_impl.h\"\n"
    "#ifdef OH_ADAPTER_ANDROID\n"
    "#include <dlfcn.h>  // adapter project: libapk_installer.so dispatch in InstallApp\n"
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
        print(f"[skip] {args.src} already has installapp dispatch")
        return

    bak = args.src + ".adapter_orig_installapp"
    if not os.path.exists(bak):
        with open(bak, "w", encoding="utf-8") as f:
            f.write(text)
        print(f"[backup] {bak}")

    if INCLUDE_ANCHOR_OLD not in text:
        sys.exit("ERROR: include anchor not found")
    if ANCHOR_OLD not in text:
        sys.exit("ERROR: dispatch anchor not found")

    if DLFCN_INCLUDE_MARKER not in text:
        text = text.replace(INCLUDE_ANCHOR_OLD, INCLUDE_ANCHOR_NEW, 1)

    text = text.replace(ANCHOR_OLD, ANCHOR_NEW, 1)

    with open(args.src, "w", encoding="utf-8") as f:
        f.write(text)
    print(f"[patched] {args.src} — InstallApp dispatch installed")


if __name__ == "__main__":
    main()
