#!/usr/bin/env python3
"""apply_installd_apk_resources_hap.py — route APK resources HAP synthesis to installd

Adds ExtractFileType::APK_RESOURCES_HAP + InstalldOperator::BuildApkResourcesHap.
When BMS calls InstalldClient::ExtractFiles with extractFileType=APK_RESOURCES_HAP,
installd (running in installs domain with write perms on data_app_el*_file)
dlopens libapk_installer.so and calls oh_adapter_build_resources_hap(apk, out)
which synthesizes the OH resources HAP from the APK launcher icon + baked-in
template and writes it directly to the target path.

Motivation:
    foundation has no SELinux write perm on data_app_el1_file. Moving the
    synthesis from BMS (foundation domain) to installd (installs domain,
    which has full write on data_app_el*_file + DAC_OVERRIDE) eliminates
    the need for a new WriteFileBytes IPC.

Modifies 3 OH files:
  1. services/bundlemgr/include/ipc/extract_param.h  (enum)
  2. services/bundlemgr/include/installd/installd_operator.h  (decl)
  3. services/bundlemgr/src/installd/installd_operator.cpp  (dispatch + impl)

Idempotent. Run with --dry-run to preview.
"""
import argparse, os, shutil, sys

MARKER = "// adapter project: APK_RESOURCES_HAP"


def patch_file(path, replacements, dry):
    if not os.path.exists(path):
        print(f"FAIL {path} (not found)")
        sys.exit(1)
    with open(path, 'r') as f:
        content = f.read()
    if MARKER in content:
        print(f"SKIP {path} (already has marker)")
        return
    original = content
    for anchor, new_block in replacements:
        if anchor not in content:
            print(f"FAIL {path}")
            print(f"  anchor not found: {anchor[:120]!r}")
            sys.exit(1)
        content = content.replace(anchor, new_block, 1)
    bak = path + '.adapter_orig'
    if not os.path.exists(bak) and not dry:
        shutil.copy(path, bak)
    if dry:
        print(f"DRY  {path}  delta=+{len(content) - len(original)}")
        return
    with open(path, 'w') as f:
        f.write(content)
    print(f"DONE {path}")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--oh-root', default='/home/HanBingChen/oh')
    ap.add_argument('--dry-run', action='store_true')
    args = ap.parse_args()
    bms = os.path.join(args.oh_root,
        'foundation/bundlemanager/bundle_framework/services/bundlemgr')

    # ---- 1. extract_param.h — add APK_RESOURCES_HAP to enum ----
    enum_anchor = (
        "enum ExtractFileType : uint8_t {\n"
        "    ALL,\n"
        "    SO,\n"
        "    AN,\n"
        "    PATCH,\n"
        "    AP,\n"
        "    RESOURCE,\n"
        "    RES_FILE,\n"
        "    HNPS_FILE\n"
        "};"
    )
    enum_new = (
        "enum ExtractFileType : uint8_t {\n"
        "    ALL,\n"
        "    SO,\n"
        "    AN,\n"
        "    PATCH,\n"
        "    AP,\n"
        "    RESOURCE,\n"
        "    RES_FILE,\n"
        "    HNPS_FILE,\n"
        "    APK_RESOURCES_HAP  " + MARKER + "\n"
        "};"
    )
    patch_file(os.path.join(bms, 'include/ipc/extract_param.h'),
        [(enum_anchor, enum_new)], args.dry_run)

    # ---- 2. installd_operator.h — add BuildApkResourcesHap decl ----
    op_h_anchor = (
        "    static bool ExtractFiles(const ExtractParam &extractParam);"
    )
    op_h_new = (
        "    static bool ExtractFiles(const ExtractParam &extractParam);\n"
        "\n"
        "    " + MARKER + "\n"
        "    // Synthesize OH resources HAP from APK launcher icon + baked-in template.\n"
        "    // Runs in installs domain — has write perm on data_app_el*_file and\n"
        "    // can dlopen libapk_installer.so which contains the synthesis logic.\n"
        "    static bool BuildApkResourcesHap(const std::string &apkPath, const std::string &outHapPath);"
    )
    patch_file(os.path.join(bms, 'include/installd/installd_operator.h'),
        [(op_h_anchor, op_h_new)], args.dry_run)

    # ---- 3. installd_operator.cpp — dispatch branch + impl ----
    op_cpp_path = os.path.join(bms, 'src/installd/installd_operator.cpp')

    # 3a. Early branch in ExtractFiles(ExtractParam)
    dispatch_anchor = (
        "bool InstalldOperator::ExtractFiles(const ExtractParam &extractParam)\n"
        "{\n"
        "    LOG_D(BMS_TAG_INSTALLD, \"InstalldOperator::ExtractFiles start\");\n"
        "    BundleExtractor extractor(extractParam.srcPath);"
    )
    dispatch_new = (
        "bool InstalldOperator::ExtractFiles(const ExtractParam &extractParam)\n"
        "{\n"
        "    LOG_D(BMS_TAG_INSTALLD, \"InstalldOperator::ExtractFiles start\");\n"
        "\n"
        "    " + MARKER + "\n"
        "    if (extractParam.extractFileType == ExtractFileType::APK_RESOURCES_HAP) {\n"
        "        return BuildApkResourcesHap(extractParam.srcPath, extractParam.targetPath);\n"
        "    }\n"
        "\n"
        "    BundleExtractor extractor(extractParam.srcPath);"
    )

    # 3b. BuildApkResourcesHap impl — append before closing namespaces
    ns_close_anchor = "}  // namespace AppExecFwk\n}  // namespace OHOS"
    impl_new = (
        MARKER + "\n"
        "bool InstalldOperator::BuildApkResourcesHap(const std::string &apkPath,\n"
        "    const std::string &outHapPath)\n"
        "{\n"
        "    LOG_I(BMS_TAG_INSTALLD, \"BuildApkResourcesHap: apk=%{public}s out=%{public}s\",\n"
        "        apkPath.c_str(), outHapPath.c_str());\n"
        "    if (apkPath.empty() || outHapPath.empty()) {\n"
        "        LOG_E(BMS_TAG_INSTALLD, \"BuildApkResourcesHap: empty path\");\n"
        "        return false;\n"
        "    }\n"
        "    // Cache handle across calls; installd is long-lived so no dlclose.\n"
        "    static void *cached = nullptr;\n"
        "    static bool tried = false;\n"
        "    if (!tried) {\n"
        "        tried = true;\n"
        "        cached = dlopen(\"libapk_installer.so\", RTLD_NOW | RTLD_GLOBAL);\n"
        "        if (cached == nullptr) {\n"
        "            LOG_E(BMS_TAG_INSTALLD, \"BuildApkResourcesHap: dlopen libapk_installer.so failed: %{public}s\",\n"
        "                dlerror());\n"
        "        }\n"
        "    }\n"
        "    if (cached == nullptr) {\n"
        "        return false;\n"
        "    }\n"
        "    using BuildFn = int (*)(const char*, const char*);\n"
        "    auto fn = reinterpret_cast<BuildFn>(dlsym(cached, \"oh_adapter_build_resources_hap\"));\n"
        "    if (fn == nullptr) {\n"
        "        LOG_E(BMS_TAG_INSTALLD, \"BuildApkResourcesHap: symbol oh_adapter_build_resources_hap missing\");\n"
        "        return false;\n"
        "    }\n"
        "    int rc = fn(apkPath.c_str(), outHapPath.c_str());\n"
        "    if (rc != 0) {\n"
        "        LOG_E(BMS_TAG_INSTALLD, \"BuildApkResourcesHap: synth returned %{public}d\", rc);\n"
        "        return false;\n"
        "    }\n"
        "    LOG_I(BMS_TAG_INSTALLD, \"BuildApkResourcesHap: OK\");\n"
        "    return true;\n"
        "}\n"
        "\n"
        "}  // namespace AppExecFwk\n"
        "}  // namespace OHOS"
    )

    patch_file(op_cpp_path,
        [(dispatch_anchor, dispatch_new), (ns_close_anchor, impl_new)],
        args.dry_run)

    print("\nAll patches applied. Rebuild libinstalls.z.so (target: installs) to activate.")


if __name__ == '__main__':
    main()
