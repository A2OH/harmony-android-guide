#!/usr/bin/env python3
"""apply_installd_write_file_bytes.py — add InstalldClient::WriteFileBytes IPC

Adds a new IPC method to InstalldClient that lets foundation-domain callers
(which have no SELinux write perms on data_app_el1_file / data_app_file)
write an in-memory byte buffer to a target path via the installs-domain
installd daemon (uid 5524) which has full write perms on data_app_el*_file.

Motivation:
    APK installer synthesizes OH resources HAP in memory by combining
    a baked-in template with the APK's launcher icon. Without this IPC,
    foundation can't write the synthesized bytes anywhere — previous
    design relied on setenforce 0 which was never a real solution.

Modifies 10 OH source files, idempotent. Follows bms-add-ipc skill recipe.

Files:
  1. include/bundle_framework_services_ipc_interface_code.h  (enum +77)
  2. include/ipc/installd_interface.h                         (virtual decl)
  3. include/ipc/installd_proxy.h                             (override decl)
  4. src/ipc/installd_proxy.cpp                               (proxy impl)
  5. include/ipc/installd_host.h                              (Handle decl)
  6. src/ipc/installd_host.cpp                                (case + Handle)
  7. include/installd/installd_host_impl.h                    (override decl)
  8. src/installd/installd_host_impl.cpp                      (business impl)
  9. include/installd_client.h                                (facade decl)
  10. src/installd_client.cpp                                 (facade impl)
"""
import argparse, os, shutil, sys

MARKER = "// adapter project: WriteFileBytes IPC"


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
            print(f"  anchor not found: {anchor[:100]!r}")
            sys.exit(1)
        # Replace only first occurrence to stay safe
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

    # ---- 1. enum code ----
    enum_anchor = (
        "    PROCESS_BIN_FILES = 76,\n"
        "};"
    )
    enum_new = (
        "    PROCESS_BIN_FILES = 76,\n"
        "    WRITE_FILE_BYTES = 77,  " + MARKER + "\n"
        "};"
    )
    patch_file(os.path.join(bms,
        'include/bundle_framework_services_ipc_interface_code.h'),
        [(enum_anchor, enum_new)], args.dry_run)

    # ---- 2. installd_interface.h ----
    iface_anchor = (
        "    virtual ErrCode DeleteCertAndRemoveKey(const std::vector<std::string> &certPaths)\n"
        "    {\n"
        "        return ERR_OK;\n"
        "    }\n"
        "\n"
        "    // ============================================================"
    )
    iface_new = (
        "    virtual ErrCode DeleteCertAndRemoveKey(const std::vector<std::string> &certPaths)\n"
        "    {\n"
        "        return ERR_OK;\n"
        "    }\n"
        "\n"
        "    " + MARKER + "\n"
        "    virtual ErrCode WriteFileBytes(const std::string &path, const std::vector<uint8_t> &bytes)\n"
        "    {\n"
        "        return ERR_OK;\n"
        "    }\n"
        "\n"
        "    // ============================================================"
    )
    # Fallback anchor if the adapter-added block above isn't present
    iface_anchor_alt = (
        "    virtual ErrCode DeleteCertAndRemoveKey(const std::vector<std::string> &certPaths)\n"
        "    {\n"
        "        return ERR_OK;\n"
        "    }\n"
        "\n"
        "};"
    )
    iface_new_alt = (
        "    virtual ErrCode DeleteCertAndRemoveKey(const std::vector<std::string> &certPaths)\n"
        "    {\n"
        "        return ERR_OK;\n"
        "    }\n"
        "\n"
        "    " + MARKER + "\n"
        "    virtual ErrCode WriteFileBytes(const std::string &path, const std::vector<uint8_t> &bytes)\n"
        "    {\n"
        "        return ERR_OK;\n"
        "    }\n"
        "\n"
        "};"
    )
    iface_path = os.path.join(bms, 'include/ipc/installd_interface.h')
    # Read to decide which anchor fits
    with open(iface_path) as f:
        ifc = f.read()
    if iface_anchor in ifc:
        patch_file(iface_path, [(iface_anchor, iface_new)], args.dry_run)
    else:
        patch_file(iface_path, [(iface_anchor_alt, iface_new_alt)], args.dry_run)

    # ---- 3. installd_proxy.h ----
    proxy_h_anchor = (
        "    virtual ErrCode DeleteCertAndRemoveKey(const std::vector<std::string> &certPaths) override;\n"
        "};"
    )
    proxy_h_new = (
        "    virtual ErrCode DeleteCertAndRemoveKey(const std::vector<std::string> &certPaths) override;\n"
        "\n"
        "    " + MARKER + "\n"
        "    virtual ErrCode WriteFileBytes(const std::string &path, const std::vector<uint8_t> &bytes) override;\n"
        "};"
    )
    patch_file(os.path.join(bms, 'include/ipc/installd_proxy.h'),
        [(proxy_h_anchor, proxy_h_new)], args.dry_run)

    # ---- 4. installd_proxy.cpp ----
    # Append impl before closing `}  // namespace AppExecFwk`
    proxy_cpp_anchor = "}  // namespace AppExecFwk\n}  // namespace OHOS"
    proxy_cpp_new = (
        MARKER + "\n"
        "ErrCode InstalldProxy::WriteFileBytes(const std::string &path, const std::vector<uint8_t> &bytes)\n"
        "{\n"
        "    if (path.empty()) {\n"
        "        LOG_E(BMS_TAG_INSTALLD, \"WriteFileBytes: empty path\");\n"
        "        return ERR_APPEXECFWK_INSTALLD_PARAM_ERROR;\n"
        "    }\n"
        "    // Cap payload size at 8 MB so a corrupted caller can't exhaust\n"
        "    // installd memory. 8 MB fits the OH Binder 1 MB default after\n"
        "    // switching to large Parcel mode; 2 MB resources HAPs are OK.\n"
        "    constexpr size_t kMaxBytes = 8 * 1024 * 1024;\n"
        "    if (bytes.size() > kMaxBytes) {\n"
        "        LOG_E(BMS_TAG_INSTALLD, \"WriteFileBytes: payload %{public}zu > %{public}zu\",\n"
        "            bytes.size(), kMaxBytes);\n"
        "        return ERR_APPEXECFWK_INSTALLD_PARAM_ERROR;\n"
        "    }\n"
        "\n"
        "    MessageParcel data;\n"
        "    INSTALLD_PARCEL_WRITE_INTERFACE_TOKEN(data, (GetDescriptor()));\n"
        "    INSTALLD_PARCEL_WRITE(data, String, path);\n"
        "    INSTALLD_PARCEL_WRITE(data, Uint32, static_cast<uint32_t>(bytes.size()));\n"
        "    if (bytes.size() > 0 && !data.WriteRawData(bytes.data(), bytes.size())) {\n"
        "        LOG_E(BMS_TAG_INSTALLD, \"WriteFileBytes: WriteRawData failed\");\n"
        "        return ERR_APPEXECFWK_PARCEL_ERROR;\n"
        "    }\n"
        "\n"
        "    MessageParcel reply;\n"
        "    MessageOption option(MessageOption::TF_SYNC);\n"
        "    return TransactInstalldCmd(InstalldInterfaceCode::WRITE_FILE_BYTES, data, reply, option);\n"
        "}\n"
        "\n"
        "}  // namespace AppExecFwk\n"
        "}  // namespace OHOS"
    )
    patch_file(os.path.join(bms, 'src/ipc/installd_proxy.cpp'),
        [(proxy_cpp_anchor, proxy_cpp_new)], args.dry_run)

    # ---- 5. installd_host.h ----
    host_h_anchor = (
        "    bool HandleDeleteCertAndRemoveKey(MessageParcel &data, MessageParcel &reply);\n"
        "};"
    )
    host_h_new = (
        "    bool HandleDeleteCertAndRemoveKey(MessageParcel &data, MessageParcel &reply);\n"
        "\n"
        "    " + MARKER + "\n"
        "    bool HandleWriteFileBytes(MessageParcel &data, MessageParcel &reply);\n"
        "};"
    )
    patch_file(os.path.join(bms, 'include/ipc/installd_host.h'),
        [(host_h_anchor, host_h_new)], args.dry_run)

    # ---- 6. installd_host.cpp: case + Handle impl ----
    host_cpp_case_anchor = (
        "        case static_cast<uint32_t>(InstalldInterfaceCode::PROCESS_BIN_FILES):"
    )
    # There's already a case statement; we need to add a new case BEFORE the next case
    # or default. To make this robust, we grep for the first default: occurrence.
    # Let's find any safe anchor by inserting right after PROCESS_BIN_FILES case block.
    # Read the file, locate the next case or default after PROCESS_BIN_FILES.
    host_cpp_path = os.path.join(bms, 'src/ipc/installd_host.cpp')
    with open(host_cpp_path) as f:
        host_cpp = f.read()
    # Find "PROCESS_BIN_FILES):" then the closing break; of its case
    idx = host_cpp.find("case static_cast<uint32_t>(InstalldInterfaceCode::PROCESS_BIN_FILES):")
    if idx < 0:
        print(f"FAIL {host_cpp_path}: PROCESS_BIN_FILES case not found")
        sys.exit(1)
    # Find "break;" after this idx
    break_idx = host_cpp.find("break;", idx)
    if break_idx < 0:
        print(f"FAIL {host_cpp_path}: break; after PROCESS_BIN_FILES not found")
        sys.exit(1)
    after_break = break_idx + len("break;")
    # Build new case text
    new_case = (
        "\n"
        "        " + MARKER + "\n"
        "        case static_cast<uint32_t>(InstalldInterfaceCode::WRITE_FILE_BYTES):\n"
        "            HandleWriteFileBytes(data, reply);\n"
        "            break;"
    )
    # Inject after the break;
    if new_case.strip() not in host_cpp:
        host_cpp_new = host_cpp[:after_break] + new_case + host_cpp[after_break:]
    else:
        host_cpp_new = host_cpp
    # Now append Handle impl. Find last `}  // namespace AppExecFwk` in the file.
    handle_impl = (
        "\n"
        + MARKER + "\n"
        "bool InstalldHost::HandleWriteFileBytes(MessageParcel &data, MessageParcel &reply)\n"
        "{\n"
        "    std::string path = Str16ToStr8(data.ReadString16());\n"
        "    uint32_t size = data.ReadUint32();\n"
        "    std::vector<uint8_t> bytes;\n"
        "    if (size > 0) {\n"
        "        const void *raw = data.ReadRawData(size);\n"
        "        if (raw == nullptr) {\n"
        "            LOG_E(BMS_TAG_INSTALLD, \"HandleWriteFileBytes: ReadRawData failed size=%{public}u\", size);\n"
        "            reply.WriteInt32(ERR_APPEXECFWK_PARCEL_ERROR);\n"
        "            return false;\n"
        "        }\n"
        "        bytes.assign(reinterpret_cast<const uint8_t*>(raw),\n"
        "                     reinterpret_cast<const uint8_t*>(raw) + size);\n"
        "    }\n"
        "    ErrCode result = WriteFileBytes(path, bytes);\n"
        "    if (!reply.WriteInt32(result)) {\n"
        "        LOG_E(BMS_TAG_INSTALLD, \"HandleWriteFileBytes: WriteInt32 failed\");\n"
        "        return false;\n"
        "    }\n"
        "    return true;\n"
        "}\n"
    )
    ns_close_anchor = "}  // namespace AppExecFwk\n}  // namespace OHOS"
    if MARKER not in host_cpp_new:
        host_cpp_new = host_cpp_new.replace(ns_close_anchor,
            handle_impl + "\n" + ns_close_anchor, 1)
    # Write back
    bak = host_cpp_path + '.adapter_orig'
    if not os.path.exists(bak) and not args.dry_run:
        shutil.copy(host_cpp_path, bak)
    if args.dry_run:
        print(f"DRY  {host_cpp_path}  delta=+{len(host_cpp_new) - len(host_cpp)}")
    else:
        with open(host_cpp_path, 'w') as f:
            f.write(host_cpp_new)
        print(f"DONE {host_cpp_path}")

    # ---- 7. installd_host_impl.h ----
    hi_h_anchor = (
        "    virtual ErrCode DeleteCertAndRemoveKey(const std::vector<std::string> &certPaths) override;"
    )
    hi_h_new = (
        "    virtual ErrCode DeleteCertAndRemoveKey(const std::vector<std::string> &certPaths) override;\n"
        "\n"
        "    " + MARKER + "\n"
        "    virtual ErrCode WriteFileBytes(const std::string &path, const std::vector<uint8_t> &bytes) override;"
    )
    patch_file(os.path.join(bms, 'include/installd/installd_host_impl.h'),
        [(hi_h_anchor, hi_h_new)], args.dry_run)

    # ---- 8. installd_host_impl.cpp ----
    hi_cpp_anchor = "}  // namespace AppExecFwk\n}  // namespace OHOS"
    hi_cpp_new = (
        MARKER + "\n"
        "// Write an in-memory byte buffer to `path`. Called by foundation via\n"
        "// InstalldClient when the caller lacks SELinux write perms on the\n"
        "// target label (e.g. foundation → data_app_el1_file). Runs in the\n"
        "// installs domain (uid 5524) which has full write rights on\n"
        "// data_app_el*_file and can create new files there.\n"
        "ErrCode InstalldHostImpl::WriteFileBytes(const std::string &path, const std::vector<uint8_t> &bytes)\n"
        "{\n"
        "    if (!InstalldPermissionMgr::VerifyCallingPermission(Constants::FOUNDATION_UID)) {\n"
        "        LOG_E(BMS_TAG_INSTALLD, \"WriteFileBytes: permission denied\");\n"
        "        return ERR_APPEXECFWK_INSTALLD_PERMISSION_DENIED;\n"
        "    }\n"
        "    if (path.empty()) {\n"
        "        LOG_E(BMS_TAG_INSTALLD, \"WriteFileBytes: empty path\");\n"
        "        return ERR_APPEXECFWK_INSTALLD_PARAM_ERROR;\n"
        "    }\n"
        "    LOG_I(BMS_TAG_INSTALLD, \"WriteFileBytes: path=%{public}s size=%{public}zu\",\n"
        "        path.c_str(), bytes.size());\n"
        "\n"
        "    FILE *fp = fopen(path.c_str(), \"wb\");\n"
        "    if (fp == nullptr) {\n"
        "        LOG_E(BMS_TAG_INSTALLD, \"WriteFileBytes: fopen failed errno=%{public}d (%{public}s)\",\n"
        "            errno, strerror(errno));\n"
        "        return ERR_APPEXECFWK_INSTALLD_COPY_FILE_FAILED;\n"
        "    }\n"
        "    size_t nWritten = 0;\n"
        "    if (!bytes.empty()) {\n"
        "        nWritten = fwrite(bytes.data(), 1, bytes.size(), fp);\n"
        "    }\n"
        "    fflush(fp);\n"
        "    int closeRet = fclose(fp);\n"
        "    if (nWritten != bytes.size() || closeRet != 0) {\n"
        "        LOG_E(BMS_TAG_INSTALLD, \"WriteFileBytes: short write %{public}zu/%{public}zu close=%{public}d\",\n"
        "            nWritten, bytes.size(), closeRet);\n"
        "        return ERR_APPEXECFWK_INSTALLD_COPY_FILE_FAILED;\n"
        "    }\n"
        "    // Standard HAP/APK perms: -rw-r--r--\n"
        "    mode_t mode = S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH;\n"
        "    if (!OHOS::ChangeModeFile(path, mode)) {\n"
        "        LOG_W(BMS_TAG_INSTALLD, \"WriteFileBytes: chmod failed for %{public}s\", path.c_str());\n"
        "    }\n"
        "    return ERR_OK;\n"
        "}\n"
        "\n"
        "}  // namespace AppExecFwk\n"
        "}  // namespace OHOS"
    )
    patch_file(os.path.join(bms, 'src/installd/installd_host_impl.cpp'),
        [(hi_cpp_anchor, hi_cpp_new)], args.dry_run)

    # ---- 9. installd_client.h ----
    client_h_anchor = (
        "    ErrCode CopyFile(const std::string &oldPath, const std::string &newPath,"
    )
    client_h_new = (
        "    " + MARKER + "\n"
        "    ErrCode WriteFileBytes(const std::string &path, const std::vector<uint8_t> &bytes);\n"
        "\n"
        "    ErrCode CopyFile(const std::string &oldPath, const std::string &newPath,"
    )
    patch_file(os.path.join(bms, 'include/installd_client.h'),
        [(client_h_anchor, client_h_new)], args.dry_run)

    # ---- 10. installd_client.cpp ----
    client_cpp_anchor = (
        "ErrCode InstalldClient::CopyFile(const std::string &oldPath, const std::string &newPath,\n"
        "    const std::string &signatureFilePath)"
    )
    client_cpp_new = (
        MARKER + "\n"
        "ErrCode InstalldClient::WriteFileBytes(const std::string &path, const std::vector<uint8_t> &bytes)\n"
        "{\n"
        "    if (path.empty()) {\n"
        "        APP_LOGE(\"WriteFileBytes params are invalid\");\n"
        "        return ERR_APPEXECFWK_INSTALLD_PARAM_ERROR;\n"
        "    }\n"
        "    return CallService(&IInstalld::WriteFileBytes, path, bytes);\n"
        "}\n"
        "\n"
        "ErrCode InstalldClient::CopyFile(const std::string &oldPath, const std::string &newPath,\n"
        "    const std::string &signatureFilePath)"
    )
    patch_file(os.path.join(bms, 'src/installd_client.cpp'),
        [(client_cpp_anchor, client_cpp_new)], args.dry_run)

    print("\nAll patches applied. Rebuild libbms.z.so to activate.")


if __name__ == '__main__':
    main()
