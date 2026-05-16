/*
 * Copyright (c) 2021-2026 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "installd/installd_operator.h"

#include <algorithm>
#if defined(CODE_SIGNATURE_ENABLE)
#include "code_sign_utils.h"
#endif
#if defined(CODE_ENCRYPTION_ENABLE)
#include "linux/code_decrypt.h"
#endif
#include <cerrno>
#include <cinttypes>
#include <cstdio>
#include <dirent.h>
#include <dlfcn.h>
#include <fcntl.h>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <map>
#include <regex>
#include <sstream>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <sys/quota.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

#include "app_log_tag_wrapper.h"
#include "app_log_wrapper.h"
#include "bundle_constants.h"
#include "bundle_service_constants.h"
#include "bundle_util.h"
#include "directory_ex.h"
#include "driver_install_ext.h"
#include "el5_filekey_manager_error.h"
#include "el5_filekey_manager_kit.h"
#include "ffrt.h"
#include "ipc/critical_manager.h"
#include "parameters.h"
#include "provision/provision_verify.h"
#include "securec.h"
#include "hnp_api.h"
#include "policycoreutils.h"
#ifdef WITH_SELINUX
#include <selinux/selinux.h>
#endif

namespace OHOS {
namespace AppExecFwk {

struct SoFileInfo {
    std::string filename;
    std::string fullpath;
    uint64_t size;
    
    SoFileInfo() : filename(""), fullpath(""), size(0) {}
    SoFileInfo(const std::string& name, const std::string& path, uint64_t sz)
        : filename(name), fullpath(path), size(sz) {}
    bool operator<(const SoFileInfo& other) const
    {
        return size > other.size;
    }
};
namespace {
constexpr const char* PREFIX_RESOURCE_PATH = "/resources/rawfile/";
constexpr const char* PREFIX_LIBS_PATH = "/libs/";
constexpr const char* PREFIX_TARGET_PATH = "/print_service/";
constexpr const char* HQF_DIR_PREFIX = "patch_";
#if defined(CODE_ENCRYPTION_ENABLE)
static const char LIB_CODE_CRYPTO_SO_PATH[] = "system/lib/libcode_crypto_metadata_process_utils.z.so";
static const char LIB64_CODE_CRYPTO_SO_PATH[] = "system/lib64/libcode_crypto_metadata_process_utils.z.so";
static const char CODE_CRYPTO_FUNCTION_NAME[] = "_ZN4OHOS8Security10CodeCrypto15CodeCryptoUtils28"
    "EnforceMetadataProcessForAppERKNSt3__h13unordered_mapINS3_12basic_stringIcNS3_11char_traitsIcEENS"
    "3_9allocatorIcEEEESA_NS3_4hashISA_EENS3_8equal_toISA_EENS8_INS3_4pairIKSA_SA_EEEEEERKNS2_17CodeCryptoHapInfoERb";
#endif

constexpr int64_t BUFFER_SIZE = 8192;
static constexpr int32_t PERMISSION_DENIED = 13;
static constexpr int32_t RESULT_OK = 0;
static constexpr int32_t CMDLINE_MAX_BUF_LEN = 4096;
static constexpr int16_t INSTALLS_UID = 3060;
static constexpr int16_t MODE_BASE = 07777;
static constexpr int8_t KEY_ID_STEP = 2;
static constexpr int8_t STR_LIBS_LEN = 4;
constexpr const char* PROC_MOUNTS_PATH = "/proc/mounts";
constexpr const char* QUOTA_DEVICE_DATA_PATH = "/data";
constexpr const char* CACHE_DIR = "cache";
constexpr const char* BUNDLE_BASE_CODE_DIR = "/data/app/el1/bundle";
constexpr const char* AP_PATH = "ap/";
constexpr const char* AI_SUFFIX = ".ai";
constexpr const char* DIFF_SUFFIX = ".diff";
constexpr const char* BUNDLE_BACKUP_KEEP_DIR = "/.backup";
constexpr const char* ATOMIC_SERVICE_PATH = "+auid-";
constexpr const char* PROC_CMDLINE_FILE_PATH = "/proc/cmdline";
constexpr const char* PERMISSION_KEY = "ohos.permission.kernel.SUPPORT_PLUGIN";
constexpr const char* PLUGIN_ID = "pluginDistributionIDs";
constexpr const char* PLUGIN_ID_SEPARATOR_OTHER = "|";
constexpr const char* PLUGIN_ID_SEPARATOR = ",";
const std::vector<std::string> DRIVER_EXECUTE_DIR {
    "/print_service/cups/serverbin/filter",
    "/print_service/sane/backend",
    "/print_service/cups/serverbin/backend",
    "/print_service/cups_enterprise/serverbin/filter",
    "/print_service/cups_enterprise/serverbin/backend",
};
#if defined(CODE_SIGNATURE_ENABLE)
using namespace OHOS::Security::CodeSign;
#endif
#if defined(CODE_ENCRYPTION_ENABLE)
static const char* CODE_DECRYPT = "/dev/code_decrypt";
static int8_t INVALID_RETURN_VALUE = -1;
static int8_t INVALID_FILE_DESCRIPTOR = -1;
#endif
std::mutex mMountsLock;
static std::map<std::string, std::string> mQuotaReverseMounts;
using ApplyPatch = int32_t (*)(const std::string, const std::string, const std::string);

static const std::map<BundleDirScene, std::vector<std::string>> ALLOWED_PATH_PREFIXES = {
    {BundleDirScene::SET_DIR_APL, {"/data/app/"}},
    {BundleDirScene::EXTRACT_HNP_FILES, {"/data/app/el1/bundle/public/"}},
    {BundleDirScene::SET_FILE_CON_FORCE, {"/data/app/", "/data/service/"}},
    {BundleDirScene::EXTRACT_DRIVER_SO_FILES, {"/data/app/el1/bundle/public/"}},

};

static const std::set<std::string> ALLOWED_APL = {
    "normal",
    "system_basic",
    "system_core",
};

static std::string HandleScanResult(
    const std::string &dir, const std::string &subName, ResultMode resultMode)
{
    if (resultMode == ResultMode::RELATIVE_PATH) {
        return subName;
    }

    return dir + ServiceConstants::PATH_SEPARATOR + subName;
}

static bool StartsWith(const std::string &sourceString, const std::string &targetPrefix)
{
    return sourceString.find(targetPrefix) == 0;
}

static bool EndsWith(const std::string &sourceString, const std::string &targetSuffix)
{
    if (sourceString.length() < targetSuffix.length()) {
        return false;
    }
    if (sourceString.rfind(targetSuffix) == (sourceString.length() - targetSuffix.length())) {
        return true;
    }
    return false;
}
} // namespace

#define FSCRYPT_KEY_DESCRIPTOR_SIZE 8
#define HMFS_IOCTL_MAGIC 0xf5
#define HMFS_IOC_SET_ASDP_ENCRYPTION_POLICY _IOW(HMFS_IOCTL_MAGIC, 84, struct fscrypt_asdp_policy)
#define FORCE_PROTECT 0x0
#define HMFS_MONITOR_FL 0x00000002
#define HMF_IOCTL_HW_GET_FLAGS _IOR(0xf5, 70, unsigned int)
#define HMF_IOCTL_HW_SET_FLAGS _IOR(0xf5, 71, unsigned int)
#define BMS_FDSAN_INSTALLD_TAG 0xD001122

struct fscrypt_asdp_policy {
    char version;
    char asdp_class;
    char flags;
    char reserved;
    char app_key2_descriptor[FSCRYPT_KEY_DESCRIPTOR_SIZE];
} __attribute__((__packed__));

bool InstalldOperator::CheckAndDeleteLinkFile(const std::string &path)
{
    struct stat path_stat;
    if (lstat(path.c_str(), &path_stat) == 0) {
        if (S_ISLNK(path_stat.st_mode)) {
            if (unlink(path.c_str()) == 0) {
                return true;
            }
            LOG_E(BMS_TAG_INSTALLD, "CheckAndDeleteLinkFile unlink %{public}s failed, error: %{public}d",
                path.c_str(), errno);
        }
    }
    LOG_E(BMS_TAG_INSTALLD, "CheckAndDeleteLinkFile lstat or S_ISLNK %{public}s failed, errno:%{public}d",
        path.c_str(), errno);
    return false;
}

bool InstalldOperator::IsExistFile(const std::string &path)
{
    if (path.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "path is empty");
        return false;
    }

    struct stat buf = {};
    if (stat(path.c_str(), &buf) != 0) {
        LOG_D(BMS_TAG_INSTALLD, "stat fail %{public}d", errno);
        return false;
    }
    return S_ISREG(buf.st_mode);
}

bool InstalldOperator::IsExistApFile(const std::string &path)
{
    std::string realPath;
    std::filesystem::path apFilePath(path);
    std::string apDir = apFilePath.parent_path().string();
    if (path.empty() || !PathToRealPath(apDir, realPath)) {
        return false;
    }

    std::error_code errorCode;
    std::filesystem::directory_iterator iter(realPath, errorCode);

    if (errorCode) {
        LOG_E(BMS_TAG_INSTALLD, "Error occurred while opening apDir: %{public}s", errorCode.message().c_str());
        return false;
    }
    for (const auto& entry : iter) {
        if (entry.path().extension() == ServiceConstants::AP_SUFFIX) {
            return true;
        }
    }
    return false;
}

bool InstalldOperator::IsExistDir(const std::string &path)
{
    if (path.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "path is empty");
        return false;
    }

    struct stat buf = {};
    if (stat(path.c_str(), &buf) != 0) {
        return false;
    }
    return S_ISDIR(buf.st_mode);
}

bool InstalldOperator::IsDirEmpty(const std::string &dir)
{
    return OHOS::IsEmptyFolder(dir);
}

bool InstalldOperator::MkRecursiveDir(const std::string &path, bool isReadByOthers)
{
    if (!OHOS::ForceCreateDirectory(path)) {
        LOG_E(BMS_TAG_INSTALLD, "mkdir failed");
        return false;
    }
    mode_t mode = S_IRWXU | S_IRGRP | S_IXGRP | S_IXOTH;
    mode |= (isReadByOthers ? S_IROTH : 0);
    return OHOS::ChangeModeDirectory(path, mode);
}

bool InstalldOperator::DeleteDir(const std::string &path)
{
    bool res = true;
    if (IsExistFile(path)) {
        res = OHOS::RemoveFile(path);
        if (!res && errno == ENOENT) {
            return true;
        }
        return res;
    }
    if (IsExistDir(path)) {
        LOG_NOFUNC_I(BMS_TAG_COMMON, "del %{public}s", path.c_str());
        res = OHOS::ForceRemoveDirectoryBMS(path);
        if (!res && errno == ENOENT) {
            return true;
        }
        return res;
    }
    return true;
}

bool InstalldOperator::DeleteDirFast(const std::string &path)
{
    std::string newPath = GetSameLevelTmpPath(path);
    if (newPath.empty()) {
        return DeleteDir(path);
    }
    if (rename(path.c_str(), newPath.c_str()) != 0) {
        LOG_W(BMS_TAG_INSTALLD, "rename failed:%{public}s,%{public}s,%{public}s",
            std::strerror(errno), path.c_str(), newPath.c_str());
        return DeleteDir(path);
    }
    CriticalManager::GetInstance().BeforeRequest();
    auto task = [newPath]() {
        bool ret = InstalldOperator::DeleteDir(newPath);
        if (!ret) {
            LOG_E(BMS_TAG_INSTALLD, "async del failed,%{public}s", newPath.c_str());
        }
        CriticalManager::GetInstance().AfterRequest();
    };
    ffrt::submit(task);
    return true;
}

bool InstalldOperator::DeleteDirFlexible(const std::string &path, const bool async)
{
    if (async) {
        return DeleteDirFast(path);
    }
    return DeleteDir(path);
}

bool InstalldOperator::DeleteUninstallTmpDir(const std::string &path)
{
    std::vector<std::string> deleteDirs;
    std::error_code ec;
    if (!std::filesystem::exists(path, ec) || !std::filesystem::is_directory(path, ec)) {
        LOG_W(BMS_TAG_INSTALLD, "invalid path:%{public}s,err:%{public}s", path.c_str(), ec.message().c_str());
        return false;
    }
    std::filesystem::directory_iterator dirIter(path, std::filesystem::directory_options::skip_permission_denied, ec);
    std::filesystem::directory_iterator endIter;
    if (ec) {
        LOG_W(BMS_TAG_INSTALLD, "create iterator failed,%{public}s,err:%{public}s", path.c_str(), ec.message().c_str());
        return false;
    }
    for (; dirIter != endIter; dirIter.increment(ec)) {
        if (ec) {
            LOG_W(BMS_TAG_INSTALLD, "iteration failed,%{public}s,err:%{public}s", path.c_str(), ec.message().c_str());
            return false;
        }
        const std::filesystem::directory_entry &entry = *dirIter;
        std::string fileName = entry.path().filename().string();
        if (fileName.rfind(ServiceConstants::UNINSTALL_TMP_PREFIX, 0) == 0) {
            deleteDirs.emplace_back(entry.path().string());
        }
    }
    bool ret = true;
    for (const std::string &dir : deleteDirs) {
        LOG_W(BMS_TAG_INSTALLD, "begin to delete dir %{public}s", dir.c_str());
        if (!DeleteDir(dir)) {
            ret = false;
            LOG_W(BMS_TAG_INSTALLD, "delete dir failed:%{public}s", dir.c_str());
        }
    }
    return ret;
}

std::string InstalldOperator::GetSameLevelTmpPath(const std::string &path)
{
    if (path.empty()) {
        LOG_I(BMS_TAG_INSTALLD, "path empty");
        return Constants::EMPTY_STRING;
    }
    std::filesystem::path parentPath = std::filesystem::path(path).parent_path();
    if (parentPath.empty()) {
        LOG_I(BMS_TAG_INSTALLD, "parentPath empty");
        return Constants::EMPTY_STRING;
    }
    std::error_code ec;
    std::filesystem::path canonicalParentPath = std::filesystem::canonical(parentPath, ec);
    if (ec) {
        LOG_I(BMS_TAG_INSTALLD, "canonical failed:%{public}s", ec.message().c_str());
        return Constants::EMPTY_STRING;
    }
    uint32_t maxTry = 3;
    for (uint32_t i = 1; i <= maxTry; ++i) {
        std::string childPath = ServiceConstants::UNINSTALL_TMP_PREFIX + std::to_string(BundleUtil::GetCurrentTimeNs());
        std::filesystem::path fullPath = canonicalParentPath / childPath;
        if (std::filesystem::exists(fullPath, ec)) {
            LOG_I(BMS_TAG_INSTALLD, "fullPath exists");
            continue;
        }
        if (ec) {
            LOG_I(BMS_TAG_INSTALLD, "exists failed:%{public}s", ec.message().c_str());
            continue;
        }
        return fullPath.string();
    }
    LOG_I(BMS_TAG_INSTALLD, "GetSameLevelTmpPath failed");
    return Constants::EMPTY_STRING;
}

bool InstalldOperator::ExtractFiles(const std::string &sourcePath, const std::string &targetSoPath,
    const std::string &cpuAbi)
{
    LOG_D(BMS_TAG_INSTALLD, "InstalldOperator::ExtractFiles start");
    if (targetSoPath.empty()) {
        LOG_D(BMS_TAG_INSTALLD, "targetSoPath is empty");
        return true;
    }

    BundleParallelExtractor extractor(sourcePath);
    if (!extractor.Init()) {
        return false;
    }

    std::vector<std::string> soEntryFiles;
    if (!ObtainNativeSoFile(extractor, cpuAbi, soEntryFiles)) {
        LOG_E(BMS_TAG_INSTALLD, "ExtractFiles obtain native so file entryName failed");
        return false;
    }

    if (soEntryFiles.empty()) {
        LOG_D(BMS_TAG_INSTALLD, "ExtractFiles no so files to extract");
        return true;
    }
    bool extractSoResult = true;
    std::vector<ffrt::dependence> handles;
    for_each(soEntryFiles.begin(), soEntryFiles.end(),
        [&extractor, &targetSoPath, &cpuAbi, &handles, &extractSoResult](const auto &entry) {
        ExtractParam param;
        param.targetPath = targetSoPath;
        param.cpuAbi = cpuAbi;
        param.extractFileType = ExtractFileType::SO;
        ffrt::task_handle handle = ffrt::submit_h([&extractor, entry, param, &extractSoResult] () {
            LOG_D(BMS_TAG_INSTALLD, "Extracting file %{private}s", entry.c_str());
            if (!ExtractTargetFile(extractor, entry, param)) {
                extractSoResult = false;
            }
            }, {}, {});
        handles.push_back(std::move(handle));
    });
    ffrt::wait(handles);

    LOG_D(BMS_TAG_INSTALLD, "InstalldOperator::ExtractFiles end");
    return extractSoResult;
}

bool InstalldOperator::ExtractFiles(const ExtractParam &extractParam)
{
    LOG_D(BMS_TAG_INSTALLD, "InstalldOperator::ExtractFiles start");

    // adapter project: APK_RESOURCES_HAP
    if (extractParam.extractFileType == ExtractFileType::APK_RESOURCES_HAP) {
        return BuildApkResourcesHap(extractParam.srcPath, extractParam.targetPath);
    }

    BundleExtractor extractor(extractParam.srcPath);
    if (!extractor.Init()) {
        LOG_E(BMS_TAG_INSTALLD, "extractor init failed");
        return false;
    }

    if (extractParam.extractFileType == ExtractFileType::RESOURCE) {
        return ExtractResourceFiles(extractParam, extractor);
    }

    if ((extractParam.extractFileType == ExtractFileType::AP) &&
        !extractor.IsDirExist(AP_PATH)) {
        LOG_D(BMS_TAG_INSTALLD, "hap has no ap files and does not need to be extracted");
        return true;
    }

    std::vector<std::string> entryNames;
    if (!extractor.GetZipFileNames(entryNames) || entryNames.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "get entryNames failed");
        return false;
    }

    for (const auto &entryName : entryNames) {
        if (strcmp(entryName.c_str(), ".") == 0 ||
            strcmp(entryName.c_str(), "..") == 0) {
            continue;
        }
        if (entryName.back() == ServiceConstants::PATH_SEPARATOR[0]) {
            continue;
        }
        // handle native file
        if (IsNativeFile(entryName, extractParam)) {
            ExtractTargetFile(extractor, entryName, extractParam);
            continue;
        }
    }

    LOG_D(BMS_TAG_INSTALLD, "InstalldOperator::ExtractFiles end");
    return true;
}

bool InstalldOperator::ExtractFiles(const std::map<std::string, std::string> &hnpPackageMap,
    const ExtractParam &extractParam)
{
    std::map<std::string, std::string> hnpPackageInfoMap = hnpPackageMap;
    BundleExtractor extractor(extractParam.srcPath);
    if (!extractor.Init()) {
        LOG_E(BMS_TAG_INSTALLD, "extractor init failed");
        return false;
    }

    std::vector<std::string> entryNames;
    if (!extractor.GetZipFileNames(entryNames) || entryNames.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "get entryNames failed");
        return false;
    }
    std::string targetPathAndName = "";
    for (const auto &entryName : entryNames) {
        if (strcmp(entryName.c_str(), ".") == 0 ||
            strcmp(entryName.c_str(), "..") == 0) {
                continue;
        }
        if (entryName.back() == ServiceConstants::PATH_SEPARATOR[0]) {
            continue;
        }
        // handle native file
        if (IsNativeFile(entryName, extractParam)) {
            std::string prefix;

            if (!DeterminePrefix(extractParam.extractFileType, extractParam.cpuAbi, prefix)) {
                LOG_E(BMS_TAG_INSTALLD, "determine prefix failed");
                return false;
            }

            std::string targetName = entryName.substr(prefix.length());
            if (hnpPackageInfoMap.find(targetName) == hnpPackageInfoMap.end()) {
                LOG_E(BMS_TAG_INSTALLD, "illegal native bundle");
                continue;
            }
            targetPathAndName = extractParam.targetPath + hnpPackageInfoMap[targetName]
                                + ServiceConstants::PATH_SEPARATOR + targetName;
            if (targetPathAndName.find("..") != std::string::npos) {
                LOG_E(BMS_TAG_INSTALLD, "hnp type err: %{public}s", hnpPackageInfoMap[targetName].c_str());
                continue;
            }
            ExtractTargetHnpFile(extractor, entryName, targetPathAndName, extractParam.extractFileType);
            hnpPackageInfoMap.erase(targetName);
            continue;
        }
    }

    if (hnpPackageInfoMap.size() > 0) {
        return false;
    }
    LOG_D(BMS_TAG_INSTALLD, "InstalldOperator::ExtractFiles end");
    return true;
}
bool InstalldOperator::IsNativeFile(
    const std::string &entryName, const ExtractParam &extractParam)
{
    LOG_D(BMS_TAG_INSTALLD, "IsNativeFile, entryName : %{public}s", entryName.c_str());
    if (extractParam.targetPath.empty()) {
        LOG_D(BMS_TAG_INSTALLD, "current hap not include so");
        return false;
    }
    std::string prefix;
    std::vector<std::string> suffixes;
    if (!DeterminePrefix(extractParam.extractFileType, extractParam.cpuAbi, prefix) ||
        !DetermineSuffix(extractParam.extractFileType, suffixes)) {
        LOG_E(BMS_TAG_INSTALLD, "determine prefix or suffix failed");
        return false;
    }

    if (!StartsWith(entryName, prefix)) {
        LOG_D(BMS_TAG_INSTALLD, "entryName not start with %{public}s", prefix.c_str());
        return false;
    }

    bool checkSuffix = false;
    for (const auto &suffix : suffixes) {
        if (EndsWith(entryName, suffix)) {
            checkSuffix = true;
            break;
        }
    }

    if (!checkSuffix && extractParam.extractFileType != ExtractFileType::RES_FILE
        && extractParam.extractFileType != ExtractFileType::SO
        && extractParam.extractFileType != ExtractFileType::HNPS_FILE) {
        LOG_D(BMS_TAG_INSTALLD, "file type error");
        return false;
    }

    LOG_D(BMS_TAG_INSTALLD, "find native file, prefix: %{public}s, entryName: %{public}s",
        prefix.c_str(), entryName.c_str());
    return true;
}

bool InstalldOperator::IsNativeSo(const std::string &entryName, const std::string &cpuAbi)
{
    LOG_D(BMS_TAG_INSTALLD, "IsNativeSo, entryName : %{public}s", entryName.c_str());
    std::string prefix = ServiceConstants::LIBS + cpuAbi + ServiceConstants::PATH_SEPARATOR;
    if (!StartsWith(entryName, prefix)) {
        LOG_D(BMS_TAG_INSTALLD, "entryName not start with %{public}s", prefix.c_str());
        return false;
    }
    LOG_D(BMS_TAG_INSTALLD, "find native so, entryName : %{public}s", entryName.c_str());
    return true;
}

bool InstalldOperator::IsDiffFiles(const std::string &entryName,
    const std::string &targetPath, const std::string &cpuAbi)
{
    LOG_D(BMS_TAG_INSTALLD, "IsDiffFiles, entryName : %{public}s", entryName.c_str());
    if (targetPath.empty()) {
        LOG_D(BMS_TAG_INSTALLD, "current hap not include diff");
        return false;
    }
    std::string prefix = ServiceConstants::LIBS + cpuAbi + ServiceConstants::PATH_SEPARATOR;
    if (!StartsWith(entryName, prefix)) {
        LOG_D(BMS_TAG_INSTALLD, "entryName not start with %{public}s", prefix.c_str());
        return false;
    }
    if (!EndsWith(entryName, DIFF_SUFFIX)) {
        LOG_D(BMS_TAG_INSTALLD, "file name not diff format");
        return false;
    }
    LOG_D(BMS_TAG_INSTALLD, "find native diff, entryName : %{public}s", entryName.c_str());
    return true;
}

void InstalldOperator::ExtractTargetHnpFile(const BundleExtractor &extractor, const std::string &entryName,
    const std::string &targetPath, const ExtractFileType &extractFileType)
{
    std::string path = targetPath;
    std::string dir = GetPathDir(path);
    if (!IsExistDir(dir) && !MkRecursiveDir(dir, true)) {
        LOG_E(BMS_TAG_INSTALLD, "create dir %{public}s failed", dir.c_str());
        return;
    }
    bool ret = extractor.ExtractFile(entryName, path);
    if (!ret) {
        LOG_E(BMS_TAG_INSTALLD, "extract file failed, entryName : %{public}s", entryName.c_str());
        return;
    }
    mode_t mode = S_IRWXU | S_IRGRP | S_IXGRP | S_IROTH | S_IXOTH;
    if (extractFileType == ExtractFileType::AP) {
        struct stat buf = {};
        if (stat(targetPath.c_str(), &buf) != 0) {
            LOG_E(BMS_TAG_INSTALLD, "fail to stat errno:%{public}d", errno);
            return;
        }
        ChangeFileAttr(path, buf.st_uid, buf.st_gid);
        mode = (buf.st_uid == buf.st_gid) ? (S_IRUSR | S_IWUSR) : (S_IRUSR | S_IWUSR | S_IRGRP);
    }
    if (!OHOS::ChangeModeFile(path, mode)) {
        LOG_E(BMS_TAG_INSTALLD, "ChangeModeFile %{public}s failed, errno: %{public}d", path.c_str(), errno);
        return;
    }
    LOG_D(BMS_TAG_INSTALLD, "extract file success, path : %{public}s", path.c_str());
}

bool InstalldOperator::ProcessBundleInstallNative(const InstallHnpParam &param)
{
    struct HapInfo hapInfo = {};
    if (strcpy_s(hapInfo.packageName, sizeof(hapInfo.packageName), param.packageName.c_str()) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLD, "failed to strcpy_s packageName: %{public}s", param.packageName.c_str());
        return false;
    }
    if (strcpy_s(hapInfo.hapPath, sizeof(hapInfo.hapPath), param.hapPath.c_str()) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLD, "failed to strcpy_s hapPath: %{public}s", param.hapPath.c_str());
        return false;
    }
    if (strcpy_s(hapInfo.abi, sizeof(hapInfo.abi), param.cpuAbi.c_str()) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLD, "failed to strcpy_s cpuAbi: %{public}s", param.cpuAbi.c_str());
        return false;
    }
    if (strcpy_s(hapInfo.appIdentifier, sizeof(hapInfo.appIdentifier), param.appIdentifier.c_str()) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLD, "failed to strcpy_s appIdentifier: %{public}s", param.appIdentifier.c_str());
        return false;
    }
    size_t count = param.hnpPaths.size();
    std::vector<char*> independentSignHnps;
    independentSignHnps.reserve(count);
    for (size_t i = 0; i < count; i++) {
        independentSignHnps.emplace_back(const_cast<char*>(param.hnpPaths[i].c_str()));
    }
    hapInfo.count = static_cast<int32_t>(count);
    hapInfo.independentSignHnpPaths = independentSignHnps.data();

    int ret = NativeInstallHnp(param.userId.c_str(), param.hnpRootPath.c_str(), &hapInfo, 1);
    LOG_I(BMS_TAG_INSTALLD, "NativeInstallHnp ret: %{public}d", ret);
    if (ret != 0) {
        LOG_E(BMS_TAG_INSTALLD, "Native package installation failed with error code: %{public}d", ret);
        return false;
    }
    return true;
}

bool InstalldOperator::ProcessBundleUnInstallNative(const std::string &userId, const std::string &packageName)
{
    int ret = NativeUnInstallHnp(userId.c_str(), packageName.c_str());
    LOG_I(BMS_TAG_INSTALLD, "NativeUnInstallHnp ret: %{public}d", ret);
    if (ret != 0) {
        LOG_E(BMS_TAG_INSTALLD, "Native package uninstallation failed with error code: %{public}d", ret);
        return false;
    }
    return true;
}

bool InstalldOperator::ExtractTargetFile(const BundleExtractor &extractor, const std::string &entryName,
    const ExtractParam &param)
{
    if (!IsFileNameValid(entryName)) {
        LOG_E(BMS_TAG_INSTALLD, "Invalid entryname %{public}s", entryName.c_str());
        return false;
    }
    // create dir if not exist
    if (!IsExistDir(param.targetPath)) {
        if (!MkRecursiveDir(param.targetPath, true)) {
            LOG_E(BMS_TAG_INSTALLD, "create targetPath %{public}s failed", param.targetPath.c_str());
            return false;
        }
    }

    std::string prefix;
    if (!DeterminePrefix(param.extractFileType, param.cpuAbi, prefix)) {
        LOG_E(BMS_TAG_INSTALLD, "determine prefix failed");
        return false;
    }
    std::string targetName = entryName.substr(prefix.length());
    std::string path = param.targetPath;
    if (path.back() != ServiceConstants::FILE_SEPARATOR_CHAR) {
        path += ServiceConstants::FILE_SEPARATOR_CHAR;
    }
    path += targetName;
    if (targetName.find(ServiceConstants::PATH_SEPARATOR) != std::string::npos) {
        std::string dir = GetPathDir(path);
        if (!IsExistDir(dir) && !MkRecursiveDir(dir, true)) {
            LOG_E(BMS_TAG_INSTALLD, "create dir %{public}s failed", dir.c_str());
            return false;
        }
    }
    if (param.needRemoveOld && IsExistFile(path) && !OHOS::RemoveFile(path)) {
        LOG_W(BMS_TAG_INSTALLD, "remove file %{public}s failed", path.c_str());
    }
    if (!extractor.ExtractFile(entryName, path)) {
        if (!extractor.ExtractFile(entryName, path)) {
            LOG_E(BMS_TAG_INSTALLD, "extract file failed, retry entryName : %{public}s", entryName.c_str());
            return false;
        }
    }
    mode_t mode = S_IRWXU | S_IRGRP | S_IXGRP | S_IROTH | S_IXOTH;
    if (param.extractFileType == ExtractFileType::AP) {
        struct stat buf = {};
        if (stat(param.targetPath.c_str(), &buf) != 0) {
            LOG_E(BMS_TAG_INSTALLD, "fail to stat errno:%{public}d", errno);
            return false;
        }
        ChangeFileAttr(path, buf.st_uid, buf.st_gid);
        mode = (buf.st_uid == buf.st_gid) ? (S_IRUSR | S_IWUSR) : (S_IRUSR | S_IWUSR | S_IRGRP);
    }
    if (!OHOS::ChangeModeFile(path, mode)) {
        LOG_W(BMS_TAG_INSTALLD, "ChangeModeFile %{public}s failed, errno: %{public}d", path.c_str(), errno);
    }
    return FsyncFile(path);
}

bool InstalldOperator::FsyncFile(const std::string &path)
{
    FILE *fileFp = fopen(path.c_str(), "r");
    if (fileFp == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "open %{public}s failed", path.c_str());
        return false;
    }
    int32_t fileFd = fileno(fileFp);
    if (fileFd < 0) {
        LOG_E(BMS_TAG_INSTALLER, "open %{public}s failed %{public}d", path.c_str(), errno);
        (void)fclose(fileFp);
        return false;
    }
    if (fsync(fileFd) != 0) {
        if (fsync(fileFd) != 0) {
            LOG_E(BMS_TAG_INSTALLER, "retry fsync %{public}s failed %{public}d", path.c_str(), errno);
            (void)fclose(fileFp);
            return false;
        }
    }
    (void)fclose(fileFp);
    return true;
}

bool InstalldOperator::DeterminePrefix(const ExtractFileType &extractFileType, const std::string &cpuAbi,
    std::string &prefix)
{
    switch (extractFileType) {
        case ExtractFileType::SO: {
            prefix = ServiceConstants::LIBS + cpuAbi + ServiceConstants::PATH_SEPARATOR;
            break;
        }
        case ExtractFileType::AN: {
            prefix = ServiceConstants::AN + cpuAbi + ServiceConstants::PATH_SEPARATOR;
            break;
        }
        case ExtractFileType::AP: {
            prefix = AP_PATH;
            break;
        }
        case ExtractFileType::RES_FILE: {
            prefix = ServiceConstants::RES_FILE_PATH;
            break;
        }
        case ExtractFileType::HNPS_FILE: {
            prefix = ServiceConstants::HNPS + cpuAbi + ServiceConstants::PATH_SEPARATOR;
            break;
        }
        default: {
            return false;
        }
    }
    return true;
}

bool InstalldOperator::DetermineSuffix(const ExtractFileType &extractFileType, std::vector<std::string> &suffixes)
{
    switch (extractFileType) {
        case ExtractFileType::SO: {
            break;
        }
        case ExtractFileType::AN: {
            suffixes.emplace_back(ServiceConstants::AN_SUFFIX);
            suffixes.emplace_back(AI_SUFFIX);
            break;
        }
        case ExtractFileType::AP: {
            suffixes.emplace_back(ServiceConstants::AP_SUFFIX);
            break;
        }
        case ExtractFileType::RES_FILE: {
            break;
        }
        case ExtractFileType::HNPS_FILE: {
            break;
        }
        default: {
            return false;
        }
    }
    return true;
}

bool InstalldOperator::RenameDir(const std::string &oldPath, const std::string &newPath)
{
    if (oldPath.empty() || oldPath.size() > PATH_MAX) {
        LOG_E(BMS_TAG_INSTALLD, "oldpath error");
        return false;
    }
    if (access(oldPath.c_str(), F_OK) != 0 && access(newPath.c_str(), F_OK) == 0) {
        LOG_E(BMS_TAG_INSTALLD, "fail to access %{public}s errno:%{public}d", oldPath.c_str(), errno);
        return true;
    }
    std::string realOldPath;
    realOldPath.reserve(PATH_MAX);
    realOldPath.resize(PATH_MAX - 1);
    if (realpath(oldPath.c_str(), &(realOldPath[0])) == nullptr) {
        LOG_NOFUNC_E(BMS_TAG_INSTALLD, "realpath for %{public}s failed, errno:%{public}d",
            oldPath.c_str(), errno);
        return false;
    }

    if (!(IsValidCodePath(realOldPath) && IsValidCodePath(newPath))) {
        LOG_E(BMS_TAG_INSTALLD, "IsValidCodePath failed");
        return false;
    }
    return RenameFile(realOldPath, newPath);
}

std::string InstalldOperator::GetPathDir(const std::string &path)
{
    std::size_t pos = path.rfind(ServiceConstants::PATH_SEPARATOR);
    if (pos == std::string::npos) {
        return std::string();
    }
    return path.substr(0, pos + 1);
}

bool InstalldOperator::ChangeDirOwnerRecursively(const std::string &path, const int uid, const int gid)
{
    std::string subPath;
    bool ret = true;
    DIR *dir = opendir(path.c_str());
    if (dir == nullptr) {
        LOG_D(BMS_TAG_INSTALLD, "fail to opendir:%{public}s, errno:%{public}d", path.c_str(), errno);
        return false;
    }

    while (true) {
        struct dirent *ptr = readdir(dir);
        if (ptr == nullptr) {
            LOG_D(BMS_TAG_INSTALLD, "fail to readdir errno:%{public}d", errno);
            break;
        }

        if (strcmp(ptr->d_name, ".") == 0 || strcmp(ptr->d_name, "..") == 0) {
            continue;
        }

        subPath = OHOS::IncludeTrailingPathDelimiter(path) + std::string(ptr->d_name);
        if (ptr->d_type == DT_DIR) {
            ret = ChangeDirOwnerRecursively(subPath, uid, gid);
            continue;
        }

        if (access(subPath.c_str(), F_OK) == 0) {
            if (!ChangeFileAttr(subPath, uid, gid)) {
                LOG_D(BMS_TAG_INSTALLD, "Failed to ChangeFileAttr %{public}s, uid=%{public}d", subPath.c_str(), uid);
                closedir(dir);
                return false;
            }
        }
    }

    closedir(dir);
    std::string currentPath = OHOS::ExcludeTrailingPathDelimiter(path);
    if (access(currentPath.c_str(), F_OK) == 0) {
        if (!ChangeFileAttr(currentPath, uid, gid)) {
            LOG_D(BMS_TAG_INSTALLD, "Failed to ChangeFileAttr %{public}s, uid=%{public}d", currentPath.c_str(), uid);
            return false;
        }
    }

    return ret;
}

bool InstalldOperator::ChangeFileAttr(const std::string &filePath, const int uid, const int gid)
{
    LOG_D(BMS_TAG_INSTALLD, "begin to change %{public}s file attribute", filePath.c_str());
    if (chown(filePath.c_str(), uid, gid) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "fail to change %{public}s ownership, uid=%{public}d, errno:%{public}d",
            filePath.c_str(), uid, errno);
        return false;
    }
    LOG_D(BMS_TAG_INSTALLD, "change %{public}s file attribute successfully", filePath.c_str());
    return true;
}

bool InstalldOperator::RenameFile(const std::string &oldPath, const std::string &newPath)
{
    if (oldPath.empty() || newPath.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "old path or new path is empty");
        return false;
    }
    if (oldPath == newPath) {
        LOG_W(BMS_TAG_INSTALLD, "old path %{public}s is same as new path", oldPath.c_str());
        return true;
    }
    if (!DeleteDir(newPath)) {
        return false;
    }
    return rename(oldPath.c_str(), newPath.c_str()) == 0;
}

bool InstalldOperator::IsValidPath(const std::string &rootDir, const std::string &path)
{
    if (rootDir.find(ServiceConstants::PATH_SEPARATOR) != 0 ||
        rootDir.rfind(ServiceConstants::PATH_SEPARATOR) != (rootDir.size() - 1) ||
        rootDir.find("..") != std::string::npos) {
        return false;
    }
    if (path.find("..") != std::string::npos) {
        return false;
    }
    return path.compare(0, rootDir.size(), rootDir) == 0;
}

bool InstalldOperator::IsValidCodePath(const std::string &codePath)
{
    if (codePath.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "code path is empty");
        return false;
    }
    return IsValidPath(std::string(BUNDLE_BASE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR, codePath);
}

bool InstalldOperator::DeleteFiles(const std::string &dataPath)
{
    LOG_D(BMS_TAG_INSTALLD, "InstalldOperator::DeleteFiles start");
    std::string subPath;
    bool ret = true;
    DIR *dir = opendir(dataPath.c_str());
    if (dir == nullptr) {
        LOG_D(BMS_TAG_INSTALLD, "fail to opendir:%{public}s, errno:%{public}d", dataPath.c_str(), errno);
        return true;
    }
    while (true) {
        struct dirent *ptr = readdir(dir);
        if (ptr == nullptr) {
            LOG_D(BMS_TAG_INSTALLD, "fail to readdir errno:%{public}d", errno);
            break;
        }
        if (strcmp(ptr->d_name, ".") == 0 || strcmp(ptr->d_name, "..") == 0) {
            continue;
        }
        subPath = OHOS::IncludeTrailingPathDelimiter(dataPath) + std::string(ptr->d_name);
        if (ptr->d_type == DT_DIR) {
            if (!OHOS::ForceRemoveDirectoryBMS(subPath)) {
                ret = false;
                LOG_W(BMS_TAG_INSTALLD, "ForceRemoveDirectory %{public}s failed, error: %{public}d",
                    dataPath.c_str(), errno);
            }
            continue;
        }
        if (access(subPath.c_str(), F_OK) == 0) {
            ret = OHOS::RemoveFile(subPath);
            if (!ret) {
                LOG_W(BMS_TAG_INSTALLD, "RemoveFile %{public}s failed, error: %{public}d", dataPath.c_str(), errno);
            }
            continue;
        }
        // maybe lnk_file
        ret = CheckAndDeleteLinkFile(subPath);
    }
    closedir(dir);
    return ret;
}

bool InstalldOperator::DeleteFilesExceptDirs(const std::string &dataPath, const std::vector<std::string> &dirsToKeep)
{
    LOG_D(BMS_TAG_INSTALLD, "InstalldOperator::DeleteFilesExceptBundleDataDirs start");
    std::string filePath;
    DIR *dir = opendir(dataPath.c_str());
    if (dir == nullptr) {
        LOG_E(BMS_TAG_INSTALLD, "fail to opendir:%{public}s, errno:%{public}d", dataPath.c_str(), errno);
        return false;
    }
    bool ret = true;
    while (true) {
        struct dirent *ptr = readdir(dir);
        if (ptr == nullptr) {
            LOG_E(BMS_TAG_INSTALLD, "fail to readdir errno:%{public}d", errno);
            break;
        }
        std::string dirName = ServiceConstants::PATH_SEPARATOR + std::string(ptr->d_name);
        if (std::find(dirsToKeep.begin(), dirsToKeep.end(), dirName) != dirsToKeep.end() ||
            std::string(BUNDLE_BACKUP_KEEP_DIR) == dirName) {
            continue;
        }
        if (strcmp(ptr->d_name, ".") == 0 || strcmp(ptr->d_name, "..") == 0) {
            continue;
        }
        filePath = OHOS::IncludeTrailingPathDelimiter(dataPath) + std::string(ptr->d_name);
        if (ptr->d_type == DT_DIR) {
            ret = OHOS::ForceRemoveDirectoryBMS(filePath);
            continue;
        }
        if (access(filePath.c_str(), F_OK) == 0) {
            ret = OHOS::RemoveFile(filePath);
            continue;
        }
        // maybe lnk_file
        ret = CheckAndDeleteLinkFile(filePath);
    }
    closedir(dir);
    return ret;
}

bool InstalldOperator::MkOwnerDir(const std::string &path, bool isReadByOthers, const int uid, const int gid)
{
    if (!MkRecursiveDir(path, isReadByOthers)) {
        return false;
    }
    return ChangeFileAttr(path, uid, gid);
}

bool InstalldOperator::CheckPathIsSame(const std::string &path, int32_t mode, const int32_t uid, const int32_t gid,
    bool &isPathExist)
{
    struct stat s;
    if (stat(path.c_str(), &s) != 0) {
        LOG_D(BMS_TAG_INSTALLD, "path :%{public}s is not exist, need create, errno:%{public}d", path.c_str(), errno);
        isPathExist = false;
        return false;
    }
    isPathExist = true;
    if ((s.st_mode & MODE_BASE) != mode) {
        LOG_NOFUNC_W(BMS_TAG_INSTALLD, "path:%{public}s mode not same, %{public}d vs %{public}d",
            path.c_str(), static_cast<int32_t>(s.st_mode & MODE_BASE), mode);
    }
    if ((static_cast<int32_t>(s.st_uid) == uid) && (static_cast<int32_t>(s.st_gid) == gid)) {
        LOG_D(BMS_TAG_INSTALLD, "path :%{public}s uid and gid are same, no need to create again", path.c_str());
        return true;
    }
    LOG_NOFUNC_W(BMS_TAG_INSTALLD, "path:%{public}s uid or gid are not same, "
        "uid:%{public}d vs %{public}d, gid:%{public}d vs %{public}d",
        path.c_str(), static_cast<int32_t>(s.st_uid), uid, static_cast<int32_t>(s.st_gid), gid);
    return false;
}

bool InstalldOperator::IsPathNeedChown(const std::string &path, int32_t mode, bool isPathExist)
{
    struct stat s;
    if (stat(path.c_str(), &s) != 0) {
        LOG_D(BMS_TAG_INSTALLD, "path :%{public}s is not exist, not need, errno:%{public}d", path.c_str(), errno);
        return false;
    }
    if (((static_cast<uint32_t>(mode) & S_ISGID) == S_ISGID) && (static_cast<int32_t>(s.st_gid) != INSTALLS_UID)) {
        LOG_W(BMS_TAG_INSTALLD, "path :%{public}s need chown when first create", path.c_str());
        return true;
    }
    return false;
}

bool InstalldOperator::MkOwnerDir(const std::string &path, int mode, const int uid, const int gid)
{
    bool isPathExist = false;
    if (CheckPathIsSame(path, mode, uid, gid, isPathExist)) {
        return true;
    }
    if (!OHOS::ForceCreateDirectory(path)) {
        LOG_E(BMS_TAG_INSTALLD, "mkdir failed, errno: %{public}d", errno);
        return false;
    }
    if (IsPathNeedChown(path, mode, isPathExist)) {
        if (chown(path.c_str(), INSTALLS_UID, INSTALLS_UID) != 0) {
            LOG_W(BMS_TAG_INSTALLD, "fail to change %{public}s ownership, errno:%{public}d", path.c_str(), errno);
        }
    }
    // only modify parent dir mode
    if (chmod(path.c_str(), mode) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "chmod path:%{public}s mode:%{public}d failed, errno:%{public}d",
            path.c_str(), mode, errno);
        return false;
    }
    if (!ChangeDirOwnerRecursively(path, uid, gid)) {
        LOG_E(BMS_TAG_INSTALLD, "ChangeDirOwnerRecursively failed, errno: %{public}d", errno);
        return false;
    }
    return true;
}

int64_t InstalldOperator::GetDiskUsage(const std::string &dir, bool isRealPath)
{
    if (dir.empty() || (dir.size() > ServiceConstants::PATH_MAX_SIZE)) {
        LOG_D(BMS_TAG_INSTALLD, "GetDiskUsage path invalid");
        return 0;
    }
    char tmpPath[PATH_MAX] = {0};
    if (!isRealPath && realpath(dir.c_str(), tmpPath) == nullptr) {
        LOG_D(BMS_TAG_INSTALLD, "file is not real path, file path: %{public}s", dir.c_str());
        return 0;
    }
    std::string filePath = tmpPath;
    uint64_t size = GetFolderSize(filePath);
    if (size > static_cast<uint64_t>(std::numeric_limits<int64_t>::max())) {
        LOG_E(BMS_TAG_INSTALLD, "GetFolderSize overflow:%{public}s", filePath.c_str());
        return 0;
    } else {
        return static_cast<int64_t>(size);
    }
}

void InstalldOperator::TraverseCacheDirectory(const std::string &currentPath, std::vector<std::string> &cacheDirs)
{
    if (currentPath.empty() || (currentPath.size() > ServiceConstants::PATH_MAX_SIZE)) {
        LOG_D(BMS_TAG_INSTALLD, "current path invaild");
        return;
    }
    std::string filePath = "";
    if (!PathToRealPath(currentPath, filePath)) {
        LOG_D(BMS_TAG_INSTALLD, "not real path: %{public}s", currentPath.c_str());
        return;
    }
    DIR* dir = opendir(filePath.c_str());
    if (dir == nullptr) {
        LOG_E(BMS_TAG_INSTALLD, "fail to opendir:%{public}s, errno:%{public}d", filePath.c_str(), errno);
        return;
    }
    if (filePath.back() != ServiceConstants::FILE_SEPARATOR_CHAR) {
        filePath.push_back(ServiceConstants::FILE_SEPARATOR_CHAR);
    }
    struct dirent *ptr = nullptr;
    while ((ptr = readdir(dir)) != nullptr) {
        if (strcmp(ptr->d_name, ".") == 0 || strcmp(ptr->d_name, "..") == 0) {
            continue;
        }
        if (ptr->d_type == DT_DIR && strcmp(ptr->d_name, CACHE_DIR) == 0) {
            std::string currentDir = filePath + std::string(ptr->d_name);
            cacheDirs.emplace_back(currentDir);
            continue;
        }
        if (ptr->d_type == DT_DIR) {
            std::string currentDir = filePath + std::string(ptr->d_name);
            TraverseCacheDirectory(currentDir, cacheDirs);
        }
    }
    closedir(dir);
}

int64_t InstalldOperator::GetDiskUsageFromPath(const std::vector<std::string> &path, int64_t timeoutMs)
{
    auto startTime = std::chrono::steady_clock::now();
    int64_t fileSize = 0;
    for (auto &st : path) {
        // check timeout
        if (timeoutMs > 0) {
            auto currentTime = std::chrono::steady_clock::now();
            auto elapsedTime = std::chrono::duration_cast<std::chrono::milliseconds>(currentTime - startTime);
            auto elapsedMs = std::abs(elapsedTime.count());
            if (elapsedMs >= timeoutMs) {
                LOG_W(BMS_TAG_INSTALLD, "timeout, return fileSize:%{public}" PRId64, fileSize);
                return fileSize;
            }
        }
        fileSize += GetDiskUsage(st);
        LOG_D(BMS_TAG_INSTALLD, "GetBundleStats get cache size:%{public}" PRId64 " from: %{public}s ",
            fileSize, st.c_str());
    }
    return fileSize;
}

bool InstalldOperator::InitialiseQuotaMounts()
{
    mQuotaReverseMounts.clear();
    std::ifstream mountsFile(PROC_MOUNTS_PATH);

    if (!mountsFile.is_open()) {
        LOG_E(BMS_TAG_INSTALLD, "Failed to open mounts file errno:%{public}d", errno);
        return false;
    }
    std::string line;

    while (std::getline(mountsFile, line)) {
        std::string device;
        std::string mountPoint;
        std::string fsType;
        std::istringstream lineStream(line);

        if (!(lineStream >> device >> mountPoint >> fsType)) {
            LOG_W(BMS_TAG_INSTALLD, "Failed to parse mounts file line: %{public}s", line.c_str());
            continue;
        }

        if (mountPoint == QUOTA_DEVICE_DATA_PATH) {
            struct dqblk dq;
            if (quotactl(QCMD(Q_GETQUOTA, USRQUOTA), device.c_str(), 0, reinterpret_cast<char*>(&dq)) == 0) {
                mQuotaReverseMounts[mountPoint] = device;
                LOG_D(BMS_TAG_INSTALLD, "InitialiseQuotaMounts, mountPoint: %{public}s, device: %{public}s",
                    mountPoint.c_str(), device.c_str());
            } else {
                LOG_W(BMS_TAG_INSTALLD, "InitialiseQuotaMounts, Failed to get quotactl, errno: %{public}d", errno);
            }
        }
    }
    return true;
}

int64_t InstalldOperator::GetDiskUsageFromQuota(const int32_t uid)
{
    auto dq = GetQuotaData(uid);
    if (!dq.has_value()) {
        return 0;
    }
    LOG_D(BMS_TAG_INSTALLD, "get disk usage from quota, uid: %{public}d, usage: %{public}llu",
        uid, static_cast<unsigned long long>(dq->dqb_curspace));
    return dq->dqb_curspace;
}

int64_t InstalldOperator::GetBundleInodeCount(int32_t uid)
{
    auto dq = GetQuotaData(uid);
    if (!dq.has_value()) {
        return 0;
    }
    LOG_NOFUNC_D(BMS_TAG_INSTALLD, "get inodes from quota, uid: %{public}d, inodes: %{public}llu",
        uid, static_cast<unsigned long long>(dq->dqb_curinodes));
    return dq->dqb_curinodes;
}

std::optional<struct dqblk> InstalldOperator::GetQuotaData(int32_t uid)
{
    std::string device = "";
    {
        std::lock_guard<std::mutex> lock(mMountsLock);
        if (mQuotaReverseMounts.find(QUOTA_DEVICE_DATA_PATH) == mQuotaReverseMounts.end()) {
            if (!InitialiseQuotaMounts()) {
                LOG_NOFUNC_E(BMS_TAG_INSTALLD, "Failed to initialise quota mounts");
                return std::nullopt;
            }
        }
        device = mQuotaReverseMounts[QUOTA_DEVICE_DATA_PATH];
    }
    if (device.empty()) {
        APP_LOGW_NOFUNC("skip when device no quotas present");
        return std::nullopt;
    }
    struct dqblk dq;
    if (quotactl(QCMD(Q_GETQUOTA, USRQUOTA), device.c_str(), uid, reinterpret_cast<char*>(&dq)) != 0) {
        APP_LOGE_NOFUNC("Failed to get quotactl, errno: %{public}d", errno);
        return std::nullopt;
    }
    return dq;
}

bool InstalldOperator::ScanDir(
    const std::string &dirPath, ScanMode scanMode, ResultMode resultMode, std::vector<std::string> &paths)
{
    if (dirPath.empty() || (dirPath.size() > ServiceConstants::PATH_MAX_SIZE)) {
        LOG_E(BMS_TAG_INSTALLD, "Scan dir path invaild");
        return false;
    }

    std::string realPath = "";
    if (!PathToRealPath(dirPath, realPath)) {
        LOG_E(BMS_TAG_INSTALLD, "file(%{public}s) is not real path", dirPath.c_str());
        return false;
    }

    DIR* dir = opendir(realPath.c_str());
    if (dir == nullptr) {
        LOG_E(BMS_TAG_INSTALLD, "Scan open dir(%{public}s) fail, errno:%{public}d", realPath.c_str(), errno);
        return false;
    }

    struct dirent *ptr = nullptr;
    while ((ptr = readdir(dir)) != nullptr) {
        if (strcmp(ptr->d_name, ".") == 0 || strcmp(ptr->d_name, "..") == 0) {
            continue;
        }

        std::string subName(ptr->d_name);
        if (scanMode == ScanMode::SUB_FILE_DIR) {
            if (ptr->d_type == DT_DIR) {
                paths.emplace_back(HandleScanResult(dirPath, subName, resultMode));
            }

            continue;
        }

        if (scanMode == ScanMode::SUB_FILE_FILE) {
            if (ptr->d_type == DT_REG) {
                paths.emplace_back(HandleScanResult(dirPath, subName, resultMode));
            }

            continue;
        }

        paths.emplace_back(HandleScanResult(dirPath, subName, resultMode));
    }

    closedir(dir);
    return true;
}

bool InstalldOperator::ScanSoFiles(const std::string &newSoPath, const std::string &originPath,
    const std::string &currentPath, std::vector<std::string> &paths)
{
    if (currentPath.empty() || (currentPath.size() > ServiceConstants::PATH_MAX_SIZE)) {
        LOG_E(BMS_TAG_INSTALLD, "ScanSoFiles current path invalid");
        return false;
    }
    std::string filePath = "";
    if (!PathToRealPath(currentPath, filePath)) {
        LOG_E(BMS_TAG_INSTALLD, "file is not real path, file path: %{public}s", currentPath.c_str());
        return false;
    }
    DIR* dir = opendir(filePath.c_str());
    if (dir == nullptr) {
        LOG_E(BMS_TAG_INSTALLD, "ScanSoFiles open dir(%{public}s) fail, errno:%{public}d", filePath.c_str(), errno);
        return false;
    }
    if (filePath.back() != ServiceConstants::FILE_SEPARATOR_CHAR) {
        filePath.push_back(ServiceConstants::FILE_SEPARATOR_CHAR);
    }
    struct dirent *ptr = nullptr;
    while ((ptr = readdir(dir)) != nullptr) {
        if (strcmp(ptr->d_name, ".") == 0 || strcmp(ptr->d_name, "..") == 0) {
            continue;
        }
        if (ptr->d_type == DT_DIR) {
            std::string currentDir = filePath + std::string(ptr->d_name);
            if (!ScanSoFiles(newSoPath, originPath, currentDir, paths)) {
                closedir(dir);
                return false;
            }
        }
        if (ptr->d_type == DT_REG) {
            std::string currentFile = filePath + std::string(ptr->d_name);
            std::string prefixPath = originPath;
            if (prefixPath.back() != ServiceConstants::FILE_SEPARATOR_CHAR) {
                prefixPath.push_back(ServiceConstants::FILE_SEPARATOR_CHAR);
            }
            std::string relativePath = currentFile.substr(prefixPath.size());
            paths.emplace_back(relativePath);
            std::string subNewSoPath = GetPathDir(newSoPath + ServiceConstants::PATH_SEPARATOR + relativePath);
            if (!IsExistDir(subNewSoPath) && !MkRecursiveDir(subNewSoPath, true)) {
                LOG_E(BMS_TAG_INSTALLD, "ScanSoFiles create subNewSoPath (%{public}s) failed", filePath.c_str());
                closedir(dir);
                return false;
            }
        }
    }
    closedir(dir);
    return true;
}

bool InstalldOperator::CopyFile(
    const std::string &sourceFile, const std::string &destinationFile)
{
    if (sourceFile.empty() || destinationFile.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "Copy file failed due to sourceFile or destinationFile is empty");
        return false;
    }

    std::ifstream in(sourceFile);
    if (!in.is_open()) {
        LOG_E(BMS_TAG_INSTALLD, "Copy file failed due to open sourceFile failed errno:%{public}d", errno);
        return false;
    }

    std::ofstream out(destinationFile);
    if (!out.is_open()) {
        LOG_E(BMS_TAG_INSTALLD, "Copy file failed due to open destinationFile failed errno:%{public}d", errno);
        in.close();
        return false;
    }

    out << in.rdbuf();
    in.close();
    out.close();
    return true;
}

bool InstalldOperator::CopyFileFast(const std::string &sourcePath, const std::string &destPath)
{
    LOG_D(BMS_TAG_INSTALLD, "begin");
    return BundleUtil::CopyFileFast(sourcePath, destPath);
}

bool InstalldOperator::ExtractDiffFiles(const std::string &filePath, const std::string &targetPath,
    const std::string &cpuAbi)
{
    BundleExtractor extractor(filePath);
    if (!extractor.Init()) {
        return false;
    }
    std::vector<std::string> entryNames;
    if (!extractor.GetZipFileNames(entryNames)) {
        return false;
    }
    for (const auto &entryName : entryNames) {
        if (strcmp(entryName.c_str(), ".") == 0 ||
            strcmp(entryName.c_str(), "..") == 0) {
            continue;
        }
        if (entryName.back() == ServiceConstants::PATH_SEPARATOR[0]) {
            continue;
        }
        // handle diff file
        if (IsDiffFiles(entryName, targetPath, cpuAbi)) {
            ExtractParam param;
            param.targetPath = targetPath;
            param.cpuAbi = cpuAbi;
            param.extractFileType = ExtractFileType::SO;
            ExtractTargetFile(extractor, entryName, param);
        }
    }
    return true;
}

bool InstalldOperator::ProcessApplyDiffPatchPath(
    const std::string &oldSoPath, const std::string &diffFilePath,
    const std::string &newSoPath, std::vector<std::string> &oldSoFileNames, std::vector<std::string> &diffFileNames)
{
    LOG_D(BMS_TAG_INSTALLD, "oldSoPath: %{public}s, diffFilePath: %{public}s, newSoPath: %{public}s",
        oldSoPath.c_str(), diffFilePath.c_str(), newSoPath.c_str());
    if (oldSoPath.empty() || diffFilePath.empty() || newSoPath.empty()) {
        return false;
    }
    if (!IsExistDir(oldSoPath) || !IsExistDir(diffFilePath)) {
        LOG_E(BMS_TAG_INSTALLD, "oldSoPath or diffFilePath not exist");
        return false;
    }

    if (!ScanSoFiles(newSoPath, oldSoPath, oldSoPath, oldSoFileNames)) {
        LOG_E(BMS_TAG_INSTALLD, "ScanSoFiles oldSoPath failed");
        return false;
    }

    if (!ScanSoFiles(newSoPath, diffFilePath, diffFilePath, diffFileNames)) {
        LOG_E(BMS_TAG_INSTALLD, "ScanSoFiles diffFilePath failed");
        return false;
    }

    if (oldSoFileNames.empty() || diffFileNames.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "so or diff files empty");
        return false;
    }

    if (!IsExistDir(newSoPath)) {
        LOG_D(BMS_TAG_INSTALLD, "ProcessApplyDiffPatchPath create newSoPath");
        if (!MkRecursiveDir(newSoPath, true)) {
            LOG_E(BMS_TAG_INSTALLD, "ProcessApplyDiffPatchPath create newSo dir (%{public}s) failed",
                newSoPath.c_str());
            return false;
        }
    }
    LOG_D(BMS_TAG_INSTALLD, "ProcessApplyDiffPatchPath end");
    return true;
}

bool InstalldOperator::ApplyDiffPatch(const std::string &oldSoPath, const std::string &diffFilePath,
    const std::string &newSoPath, int32_t uid)
{
    LOG_I(BMS_TAG_INSTALLD, "ApplyDiffPatch no need to process");
    return true;
}

bool InstalldOperator::ObtainQuickFixFileDir(const std::string &dir, std::vector<std::string> &fileVec)
{
    if (dir.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "ObtainQuickFixFileDir dir path invaild");
        return false;
    }

    std::string realPath = "";
    if (!PathToRealPath(dir, realPath)) {
        LOG_E(BMS_TAG_INSTALLD, "dir(%{public}s) is not real path", dir.c_str());
        return false;
    }

    DIR* directory = opendir(realPath.c_str());
    if (directory == nullptr) {
        LOG_E(BMS_TAG_INSTALLD, "ObtainQuickFixFileDir open dir(%{public}s) fail, errno:%{public}d", realPath.c_str(),
            errno);
        return false;
    }

    struct dirent *ptr = nullptr;
    bool isBundleCodeDir = dir.compare(Constants::BUNDLE_CODE_DIR) == 0;
    while ((ptr = readdir(directory)) != nullptr) {
        std::string currentName(ptr->d_name);
        if (currentName.compare(".") == 0 || currentName.compare("..") == 0) {
            continue;
        }

        std::string curPath = dir + ServiceConstants::PATH_SEPARATOR + currentName;
        struct stat s;
        if (stat(curPath.c_str(), &s) == 0) {
            // directory
            if ((s.st_mode & S_IFDIR) && (isBundleCodeDir || BundleUtil::StartWith(currentName, HQF_DIR_PREFIX))) {
                ObtainQuickFixFileDir(curPath, fileVec);
            }

            // file
            if ((s.st_mode & S_IFREG) &&
                (currentName.find(ServiceConstants::QUICK_FIX_FILE_SUFFIX) != std::string::npos)) {
                    fileVec.emplace_back(dir);
                }
        }
    }
    closedir(directory);
    return true;
}

bool InstalldOperator::CopyFiles(const std::string &sourceDir, const std::string &destinationDir)
{
    LOG_D(BMS_TAG_INSTALLD, "sourceDir is %{public}s, destinationDir is %{public}s",
        sourceDir.c_str(), destinationDir.c_str());
    if (sourceDir.empty() || destinationDir.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "Copy file failed due to sourceDir or destinationDir is empty");
        return false;
    }

    std::string realPath = "";
    if (!PathToRealPath(sourceDir, realPath)) {
        LOG_E(BMS_TAG_INSTALLD, "sourceDir(%{public}s) is not real path", sourceDir.c_str());
        return false;
    }

    DIR* directory = opendir(realPath.c_str());
    if (directory == nullptr) {
        LOG_E(BMS_TAG_INSTALLD, "CopyFiles open dir(%{public}s) fail, errno:%{public}d", realPath.c_str(), errno);
        return false;
    }

    struct dirent *ptr = nullptr;
    while ((ptr = readdir(directory)) != nullptr) {
        std::string currentName(ptr->d_name);
        if (currentName.compare(".") == 0 || currentName.compare("..") == 0) {
            continue;
        }

        std::string curPath = sourceDir + ServiceConstants::PATH_SEPARATOR + currentName;
        struct stat s;
        if ((stat(curPath.c_str(), &s) == 0) && (s.st_mode & S_IFREG)) {
            std::string innerDesStr = destinationDir + ServiceConstants::PATH_SEPARATOR + currentName;
            if (CopyFile(curPath, innerDesStr)) {
                ChangeFileAttr(innerDesStr, Constants::FOUNDATION_UID, ServiceConstants::BMS_GID);
            }
        }
    }
    closedir(directory);
    return true;
}

bool InstalldOperator::GetNativeLibraryFileNames(const std::string &filePath, const std::string &cpuAbi,
    std::vector<std::string> &fileNames)
{
    BundleExtractor extractor(filePath);
    if (!extractor.Init()) {
        return false;
    }
    std::vector<std::string> entryNames;
    if (!extractor.GetZipFileNames(entryNames)) {
        return false;
    }
    std::string prefix = ServiceConstants::LIBS + cpuAbi + ServiceConstants::PATH_SEPARATOR;
    for (const auto &entryName : entryNames) {
        if (StartsWith(entryName, prefix)) {
            fileNames.push_back(entryName.substr(prefix.length(), entryName.length()));
        }
    }
    LOG_D(BMS_TAG_INSTALLD, "InstalldOperator::GetNativeLibraryFileNames end");
    return true;
}

#if defined(CODE_SIGNATURE_ENABLE)
bool InstalldOperator::PrepareEntryMap(const CodeSignatureParam &codeSignatureParam,
    const std::vector<std::string> &soEntryFiles, Security::CodeSign::EntryMap &entryMap)
{
    if (codeSignatureParam.targetSoPath.empty()) {
        return false;
    }
    const std::string prefix = ServiceConstants::LIBS + codeSignatureParam.cpuAbi + ServiceConstants::PATH_SEPARATOR;
    for_each(soEntryFiles.begin(), soEntryFiles.end(),
        [&entryMap, &prefix, &codeSignatureParam](const auto &entry) {
        std::string fileName = entry.substr(prefix.length());
        std::string path = codeSignatureParam.targetSoPath;
        if (path.back() != ServiceConstants::FILE_SEPARATOR_CHAR) {
            path += ServiceConstants::FILE_SEPARATOR_CHAR;
        }
        entryMap.emplace(entry, path + fileName);
        LOG_D(BMS_TAG_INSTALLD, "VerifyCode the targetSoPath is %{public}s", (path + fileName).c_str());
    });
    return true;
}


ErrCode InstalldOperator::PerformCodeSignatureCheck(const CodeSignatureParam &codeSignatureParam,
    const Security::CodeSign::EntryMap &entryMap)
{
    ErrCode ret = ERR_OK;
    if (codeSignatureParam.isCompileSdkOpenHarmony &&
        !Security::CodeSign::CodeSignUtils::IsSupportOHCodeSign()) {
        LOG_D(BMS_TAG_INSTALLD, "code signature is not supported");
        return ret;
    }
    uint32_t codeSignFlag = 0;
    if (!codeSignatureParam.isCompressNativeLibrary) {
        codeSignFlag |= Security::CodeSign::CodeSignInfoFlag::IS_UNCOMPRESSED_NATIVE_LIBS;
    }
    if (codeSignatureParam.signatureFileDir.empty()) {
        std::shared_ptr<CodeSignHelper> codeSignHelper = std::make_shared<CodeSignHelper>();
        Security::CodeSign::FileType fileType = codeSignatureParam.isPreInstalledBundle ?
            FILE_ENTRY_ONLY : FILE_ENTRY_ADD;
        if (codeSignatureParam.isEnterpriseBundle) {
            LOG_D(BMS_TAG_INSTALLD, "Verify code signature for enterprise bundle");
            Security::CodeSign::ByteBuffer byteBuffer;
            byteBuffer.CopyFrom(reinterpret_cast<const uint8_t *>(codeSignatureParam.profileBlock.get()),
                codeSignatureParam.profileBlockLength);
            ret = codeSignHelper->EnforceCodeSignForAppWithOwnerId(
                codeSignatureParam.modulePath, entryMap, fileType, byteBuffer, codeSignFlag);
        } else {
            LOG_D(BMS_TAG_INSTALLD, "Verify code signature for non-enterprise bundle");
            ret = codeSignHelper->EnforceCodeSignForApp(
                codeSignatureParam.modulePath, entryMap, fileType, codeSignFlag);
        }
        LOG_NOFUNC_I(BMS_TAG_INSTALLD, "installd Verify code signature %{public}s",
            codeSignatureParam.modulePath.c_str());
    } else {
        ret = CodeSignUtils::EnforceCodeSignForApp(entryMap, codeSignatureParam.signatureFileDir);
    }
    return ret;
}
#endif

ErrCode InstalldOperator::VerifyCodeSignature(const CodeSignatureParam &codeSignatureParam)
{
    BundleExtractor extractor(codeSignatureParam.modulePath);
    if (!extractor.Init()) {
        return ERR_APPEXECFWK_INSTALL_ENCRYPTION_EXTRACTOR_INIT_FAILED;
    }

    std::vector<std::string> soEntryFiles;
    if (!ObtainNativeSoFile(extractor, codeSignatureParam.cpuAbi, soEntryFiles)) {
        return ERR_APPEXECFWK_INSTALL_ENCRYPTION_OBTAIN_SO_FAILED;
    }

    if (soEntryFiles.empty()) {
        LOG_D(BMS_TAG_INSTALLD, "soEntryFiles is empty");
        return ERR_OK;
    }

#if defined(CODE_SIGNATURE_ENABLE)
    Security::CodeSign::EntryMap entryMap;
    if (!PrepareEntryMap(codeSignatureParam, soEntryFiles, entryMap)) {
        return ERR_APPEXECFWK_INSTALLD_PARAM_ERROR;
    }

    ErrCode ret = PerformCodeSignatureCheck(codeSignatureParam, entryMap);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLD, "VerifyCode failed due to %{public}d", ret);
        if (CODE_SIGNATURE_ERR_MAP.find(ret) != CODE_SIGNATURE_ERR_MAP.end()) {
            return CODE_SIGNATURE_ERR_MAP.at(ret);
        }
        return ret;
    }
#endif
    return ERR_OK;
}

ErrCode InstalldOperator::CheckEncryption(const CheckEncryptionParam &checkEncryptionParam, bool &isEncryption)
{
    if (checkEncryptionParam.cpuAbi.empty() && checkEncryptionParam.targetSoPath.empty()) {
        return CheckHapEncryption(checkEncryptionParam, isEncryption);
    }
    const std::string cpuAbi = checkEncryptionParam.cpuAbi;
    InstallBundleType installBundleType = checkEncryptionParam.installBundleType;
    const bool isCompressNativeLibrary = checkEncryptionParam.isCompressNativeLibrary;
    LOG_D(BMS_TAG_INSTALLD, "a %{public}s, t %{public}d, p %{public}s", checkEncryptionParam.appIdentifier.c_str(),
        static_cast<int32_t>(installBundleType), checkEncryptionParam.modulePath.c_str());
    BundleExtractor extractor(checkEncryptionParam.modulePath);
    if (!extractor.Init()) {
        return ERR_APPEXECFWK_INSTALL_ENCRYPTION_EXTRACTOR_INIT_FAILED;
    }
    std::vector<std::string> soEntryFiles;
    if (!ObtainNativeSoFile(extractor, cpuAbi, soEntryFiles)) {
        LOG_E(BMS_TAG_INSTALLD, "ObtainNativeSoFile failed");
        return ERR_APPEXECFWK_INSTALL_ENCRYPTION_OBTAIN_SO_FAILED;
    }
    if (soEntryFiles.empty()) {
        LOG_D(BMS_TAG_INSTALLD, "no so file in installation file %{public}s", checkEncryptionParam.modulePath.c_str());
        return ERR_OK;
    }
#if defined(CODE_ENCRYPTION_ENABLE)
    const std::string targetSoPath = checkEncryptionParam.targetSoPath;
    std::unordered_map<std::string, std::string> entryMap;
    entryMap.emplace(ServiceConstants::CODE_SIGNATURE_HAP, checkEncryptionParam.modulePath);
    if (!targetSoPath.empty()) {
        const std::string prefix = ServiceConstants::LIBS + cpuAbi + ServiceConstants::PATH_SEPARATOR;
        std::for_each(soEntryFiles.begin(), soEntryFiles.end(), [&entryMap, &prefix, &targetSoPath](const auto &entry) {
            std::string fileName = entry.substr(prefix.length());
            std::string path = targetSoPath;
            if (path.back() != ServiceConstants::FILE_SEPARATOR_CHAR) {
                path += ServiceConstants::FILE_SEPARATOR_CHAR;
            }
            entryMap.emplace(entry, path + fileName);
            LOG_D(BMS_TAG_INSTALLD, "CheckEncryption the targetSoPath is %{public}s", (path + fileName).c_str());
        });
    }
    CodeCryptoHapInfo hapInfo;
    hapInfo.appIdentifier = checkEncryptionParam.appIdentifier;
    hapInfo.versionCode = checkEncryptionParam.versionCode;
    hapInfo.type = installBundleType;
    hapInfo.libCompressed = isCompressNativeLibrary;
    if (auto ret = EnforceEncryption(entryMap, hapInfo, isEncryption) != ERR_OK) {
        return ret;
    }
#endif
    return ERR_OK;
}

ErrCode InstalldOperator::CheckHapEncryption(const CheckEncryptionParam &checkEncryptionParam, bool &isEncryption)
{
    const std::string hapPath = checkEncryptionParam.modulePath;
    InstallBundleType installBundleType = checkEncryptionParam.installBundleType;
    const bool isCompressNativeLibrary = checkEncryptionParam.isCompressNativeLibrary;
    LOG_D(BMS_TAG_INSTALLD, "p %{public}s, t %{public}d, "
        "a %{public}s, c is %{public}d", hapPath.c_str(),
        static_cast<int32_t>(installBundleType), checkEncryptionParam.appIdentifier.c_str(), isCompressNativeLibrary);
#if defined(CODE_ENCRYPTION_ENABLE)
    std::unordered_map<std::string, std::string> entryMap;
    entryMap.emplace(ServiceConstants::CODE_SIGNATURE_HAP, hapPath);
    CodeCryptoHapInfo hapInfo;
    hapInfo.appIdentifier = checkEncryptionParam.appIdentifier;
    hapInfo.versionCode = checkEncryptionParam.versionCode;
    hapInfo.type = installBundleType;
    hapInfo.libCompressed = isCompressNativeLibrary;
    auto ret = EnforceEncryption(entryMap, hapInfo, isEncryption);
    if (ret != ERR_OK) {
        return ret;
    }
#endif
    return ERR_OK;
}

bool InstalldOperator::ObtainNativeSoFile(const BundleExtractor &extractor, const std::string &cpuAbi,
    std::vector<std::string> &soEntryFiles)
{
    std::vector<std::string> entryNames;
    if (!extractor.GetZipFileNames(entryNames)) {
        LOG_E(BMS_TAG_INSTALLD, "GetZipFileNames failed");
        return false;
    }
    if (entryNames.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "entryNames is empty");
        return false;
    }

    for (const auto &entryName : entryNames) {
        if (strcmp(entryName.c_str(), ".") == 0 ||
            strcmp(entryName.c_str(), "..") == 0) {
            continue;
        }
        if (entryName.back() == ServiceConstants::PATH_SEPARATOR[0]) {
            continue;
        }
        // save native so file entryName in the hap
        if (IsNativeSo(entryName, cpuAbi)) {
            soEntryFiles.emplace_back(entryName);
            continue;
        }
    }

    if (soEntryFiles.empty()) {
        LOG_D(BMS_TAG_INSTALLD, "no so file in installation file");
    }
    return true;
}

bool InstalldOperator::MoveFiles(const std::string &srcDir, const std::string &desDir, bool isDesDirNeedCreated)
{
    LOG_D(BMS_TAG_INSTALLD, "srcDir is %{public}s, desDir is %{public}s", srcDir.c_str(), desDir.c_str());
    if (isDesDirNeedCreated && !MkRecursiveDir(desDir, true)) {
        LOG_E(BMS_TAG_INSTALLD, "create desDir failed");
        return false;
    }

    if (srcDir.empty() || desDir.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "move file failed due to srcDir or desDir is empty");
        return false;
    }

    std::string realPath = "";
    if (!PathToRealPath(srcDir, realPath)) {
        LOG_E(BMS_TAG_INSTALLD, "srcDir(%{public}s) is not real path", srcDir.c_str());
        return false;
    }

    std::string realDesDir = "";
    if (!PathToRealPath(desDir, realDesDir)) {
        LOG_E(BMS_TAG_INSTALLD, "desDir(%{public}s) is not real path", desDir.c_str());
        return false;
    }

    DIR* directory = opendir(realPath.c_str());
    if (directory == nullptr) {
        LOG_E(BMS_TAG_INSTALLD, "MoveFiles open dir(%{public}s) fail, errno:%{public}d", realPath.c_str(), errno);
        return false;
    }

    struct dirent *ptr = nullptr;
    while ((ptr = readdir(directory)) != nullptr) {
        std::string currentName(ptr->d_name);
        if (currentName.compare(".") == 0 || currentName.compare("..") == 0) {
            continue;
        }

        std::string curPath = realPath + ServiceConstants::PATH_SEPARATOR + currentName;
        std::string innerDesStr = realDesDir + ServiceConstants::PATH_SEPARATOR + currentName;
        struct stat s;
        if (stat(curPath.c_str(), &s) != 0) {
            LOG_D(BMS_TAG_INSTALLD, "MoveFiles stat %{public}s failed, errno:%{public}d", curPath.c_str(), errno);
            continue;
        }
        if (!MoveFileOrDir(curPath, innerDesStr, s.st_mode)) {
            closedir(directory);
            return false;
        }
    }
    closedir(directory);
    return true;
}

bool InstalldOperator::MoveFileOrDir(const std::string &srcPath, const std::string &destPath, mode_t mode)
{
    if (mode & S_IFREG) {
        LOG_D(BMS_TAG_INSTALLD, "srcPath(%{public}s) is a file", srcPath.c_str());
        return MoveFile(srcPath, destPath);
    } else if (mode & S_IFDIR) {
        LOG_D(BMS_TAG_INSTALLD, "srcPath(%{public}s) is a dir", srcPath.c_str());
        return MoveFiles(srcPath, destPath, true);
    }
    return true;
}

bool InstalldOperator::MoveFile(const std::string &srcPath, const std::string &destPath)
{
    LOG_D(BMS_TAG_INSTALLD, "srcPath is %{public}s, destPath is %{public}s", srcPath.c_str(), destPath.c_str());
    if (!RenameFile(srcPath, destPath)) {
        LOG_E(BMS_TAG_INSTALLD, "move file from srcPath(%{public}s) to destPath(%{public}s) failed", srcPath.c_str(),
            destPath.c_str());
        return false;
    }
    mode_t mode = S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH;
    auto filterExecuteFile = [&destPath](const std::string &dir) {
        return destPath.find(dir) != std::string::npos;
    };
    if (std::any_of(DRIVER_EXECUTE_DIR.begin(), DRIVER_EXECUTE_DIR.end(), filterExecuteFile)) {
        mode |= S_IXUSR;
        mode &= ~S_IWUSR;
    }
    if (!OHOS::ChangeModeFile(destPath, mode)) {
        LOG_E(BMS_TAG_INSTALLD, "change mode failed");
        return false;
    }
    return true;
}

bool InstalldOperator::ExtractResourceFiles(const ExtractParam &extractParam, const BundleExtractor &extractor)
{
    LOG_D(BMS_TAG_INSTALLD, "ExtractResourceFiles begin");
    std::string targetDir = extractParam.targetPath;
    if (!MkRecursiveDir(targetDir, true)) {
        LOG_E(BMS_TAG_INSTALLD, "create targetDir failed");
        return false;
    }
    std::vector<std::string> entryNames;
    if (!extractor.GetZipFileNames(entryNames)) {
        LOG_E(BMS_TAG_INSTALLD, "GetZipFileNames failed");
        return false;
    }
    for (const auto &entryName : entryNames) {
        if (StartsWith(entryName, ServiceConstants::LIBS)
            || StartsWith(entryName, ServiceConstants::AN)
            || StartsWith(entryName, AP_PATH)) {
            continue;
        }
        const std::string relativeDir = GetPathDir(entryName);
        if (!relativeDir.empty()) {
            if (!MkRecursiveDir(targetDir + relativeDir, true)) {
                LOG_E(BMS_TAG_INSTALLD, "MkRecursiveDir failed");
                return false;
            }
        }
        std::string filePath = targetDir + entryName;
        if (!extractor.ExtractFile(entryName, filePath)) {
            LOG_E(BMS_TAG_INSTALLD, "ExtractFile failed");
            continue;
        }
        mode_t mode = S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH;
        if (!OHOS::ChangeModeFile(filePath, mode)) {
            LOG_E(BMS_TAG_INSTALLD, "change mode failed");
            return false;
        }
    }
    LOG_D(BMS_TAG_INSTALLD, "ExtractResourceFiles success");
    return true;
}

bool InstalldOperator::ExtractDriverSoFiles(const std::string &srcPath,
    const std::unordered_multimap<std::string, std::string> &dirMap)
{
    LOG_D(BMS_TAG_INSTALLD, "ExtractDriverSoFiles start with src(%{public}s)", srcPath.c_str());
    if (srcPath.empty() || dirMap.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "ExtractDriverSoFiles parameters are invalid");
        return false;
    }

    for (auto &[originalDir, destinedDir] : dirMap) {
        if ((originalDir.compare(".") == 0) || (originalDir.compare("..") == 0)) {
            LOG_E(BMS_TAG_INSTALLD, "the originalDir %{public}s is not existed in the hap", originalDir.c_str());
            return false;
        }
        if ((!BundleUtil::StartWith(originalDir, PREFIX_RESOURCE_PATH) &&
            !BundleUtil::StartWith(originalDir, PREFIX_LIBS_PATH)) ||
            !BundleUtil::StartWith(destinedDir, PREFIX_TARGET_PATH)) {
            LOG_E(BMS_TAG_INSTALLD, "the originalDir %{public}s and destined dir %{public}s are invalid",
                originalDir.c_str(), destinedDir.c_str());
            return false;
        }
        std::string fileName = originalDir;
        if (fileName.front() == ServiceConstants::PATH_SEPARATOR[0]) {
            fileName = fileName.substr(1);
        }
        int fileNamePos = 0;
        fileNamePos = static_cast<int32_t>(fileName.find(ServiceConstants::PATH_SEPARATOR[0], STR_LIBS_LEN + 1));
        fileName.erase(0, fileNamePos);
        LOG_D(BMS_TAG_INSTALLD, "ExtractDriverSoFiles fileName is %{public}s", fileName.c_str());
        std::string systemServiceDir = ServiceConstants::SYSTEM_SERVICE_DIR;
        if (!CopyDriverSoFiles(srcPath + fileName, systemServiceDir + destinedDir)) {
            LOG_E(BMS_TAG_INSTALLD, "CopyDriverSoFiles failed");
            return false;
        }
    }
    LOG_D(BMS_TAG_INSTALLD, "ExtractDriverSoFiles end");
    return true;
}

bool InstalldOperator::CopyDriverSoFiles(const std::string &originalDir, const std::string &destinedDir)
{
    LOG_D(BMS_TAG_INSTALLD, "CopyDriverSoFiles beign");
    auto pos = destinedDir.rfind(ServiceConstants::PATH_SEPARATOR);
    if ((pos == std::string::npos) || (pos == destinedDir.length() -1)) {
        LOG_E(BMS_TAG_INSTALLD, "destinedDir(%{public}s) is invalid path", destinedDir.c_str());
        return false;
    }
    std::string desDir = destinedDir.substr(0, pos);
    std::string realDesDir;
    if (!PathToRealPath(desDir, realDesDir)) {
        LOG_E(BMS_TAG_INSTALLD, "desDir(%{public}s) is not real path", desDir.c_str());
        return false;
    }
    std::string realDestinedDir = realDesDir + destinedDir.substr(pos);
    LOG_D(BMS_TAG_INSTALLD, "realDestinedDir is %{public}s", realDestinedDir.c_str());
    MoveFile(originalDir, realDestinedDir);

    struct stat buf = {};
    if (stat(realDesDir.c_str(), &buf) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "failed to obtain the stat status of realDesDir %{public}s, errno:%{public}d",
            realDesDir.c_str(), errno);
        return false;
    }
    ChangeFileAttr(realDestinedDir, buf.st_uid, buf.st_gid);
    // Refresh the selinux tag of the driver file so that it matches the selinux tag of the parent directory file
    int ret = RestoreconFromParentDir(realDestinedDir.c_str());
    if (ret != 0) {
        LOG_E(BMS_TAG_INSTALLD, "RefreshFileSelinuxTag %{public}s failed, ret: %{public}d", realDestinedDir.c_str(),
            ret);
        return false;
    }
    LOG_D(BMS_TAG_INSTALLD, "CopyDriverSoFiles end");
    return true;
}

#if defined(CODE_ENCRYPTION_ENABLE)
ErrCode InstalldOperator::ExtractSoFilesToTmpHapPath(const std::string &hapPath, const std::string &cpuAbi,
    const std::string &tmpSoPath, int32_t uid)
{
    LOG_D(BMS_TAG_INSTALLD, "start to obtain decoded so files from hapPath %{public}s", hapPath.c_str());
    BundleExtractor extractor(hapPath);
    if (!extractor.Init()) {
        LOG_E(BMS_TAG_INSTALLD, "init bundle extractor failed");
        return ERR_BUNDLEMANAGER_QUICK_FIX_INTERNAL_ERROR;
    }

    /* obtain the so list in the hap */
    std::vector<std::string> soEntryFiles;
    if (!ObtainNativeSoFile(extractor, cpuAbi, soEntryFiles)) {
        LOG_E(BMS_TAG_INSTALLD, "ExtractFiles obtain native so file entryName failed");
        return ERR_BUNDLEMANAGER_QUICK_FIX_INTERNAL_ERROR;
    }

    std::string innerTmpSoPath = tmpSoPath;
    if (innerTmpSoPath.back() != ServiceConstants::PATH_SEPARATOR[0]) {
        innerTmpSoPath += ServiceConstants::PATH_SEPARATOR;
    }

    /* create innerTmpSoPath */
    if (!IsExistDir(innerTmpSoPath)) {
        if (!MkRecursiveDir(innerTmpSoPath, true)) {
            LOG_E(BMS_TAG_INSTALLD, "create innerTmpSoPath %{public}s failed", innerTmpSoPath.c_str());
            return ERR_BUNDLEMANAGER_QUICK_FIX_INTERNAL_ERROR;
        }
    }

    for (const auto &entry : soEntryFiles) {
        LOG_D(BMS_TAG_INSTALLD, "entryName is %{public}s", entry.c_str());
        auto pos = entry.rfind(ServiceConstants::PATH_SEPARATOR[0]);
        if (pos == std::string::npos) {
            LOG_W(BMS_TAG_INSTALLD, "invalid so entry %{public}s", entry.c_str());
            continue;
        }
        std::string soFileName = entry.substr(pos + 1);
        if (soFileName.empty()) {
            LOG_W(BMS_TAG_INSTALLD, "invalid so entry %{public}s", entry.c_str());
            continue;
        }
        LOG_D(BMS_TAG_INSTALLD, "so file is %{public}s", soFileName.c_str());
        uint32_t offset = 0;
        uint32_t length = 0;
        if (!extractor.GetFileInfo(entry, offset, length) || length == 0) {
            LOG_W(BMS_TAG_INSTALLD, "GetFileInfo failed or invalid so file");
            continue;
        }
        LOG_D(BMS_TAG_INSTALLD, "so file %{public}s has offset %{public}d and file size %{public}d",
            entry.c_str(), offset, length);

        /* mmap so to ram and write so file to temp path */
        ErrCode res = ERR_OK;
        if ((res = DecryptSoFile(hapPath, innerTmpSoPath + soFileName, uid, length, offset)) != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLD, "decrypt file failed, srcPath is %{public}s and destPath is %{public}s",
                hapPath.c_str(), (innerTmpSoPath + soFileName).c_str());
            return res;
        }
    }

    return ERR_OK;
}

ErrCode InstalldOperator::ExtractSoFilesToTmpSoPath(const std::string &hapPath, const std::string &realSoFilesPath,
    const std::string &cpuAbi, const std::string &tmpSoPath, int32_t uid)
{
    LOG_D(BMS_TAG_INSTALLD, "start to obtain decoded so files from so path");
    if (realSoFilesPath.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "real so file path is empty");
        return ERR_BUNDLEMANAGER_QUICK_FIX_INVALID_PATH;
    }
    BundleExtractor extractor(hapPath);
    if (!extractor.Init()) {
        LOG_E(BMS_TAG_INSTALLD, "init bundle extractor failed");
        return ERR_BUNDLEMANAGER_QUICK_FIX_INTERNAL_ERROR;
    }
    /* obtain the so list in the hap */
    std::vector<std::string> soEntryFiles;
    if (!ObtainNativeSoFile(extractor, cpuAbi, soEntryFiles)) {
        LOG_E(BMS_TAG_INSTALLD, "ExtractFiles obtain native so file entryName failed");
        return ERR_BUNDLEMANAGER_QUICK_FIX_INTERNAL_ERROR;
    }

    std::string innerTmpSoPath = tmpSoPath;
    if (innerTmpSoPath.back() != ServiceConstants::PATH_SEPARATOR[0]) {
        innerTmpSoPath += ServiceConstants::PATH_SEPARATOR;
    }
    // create innerTmpSoPath
    if (!IsExistDir(innerTmpSoPath)) {
        if (!MkRecursiveDir(innerTmpSoPath, true)) {
            LOG_E(BMS_TAG_INSTALLD, "create innerTmpSoPath %{public}s failed", innerTmpSoPath.c_str());
            return ERR_BUNDLEMANAGER_QUICK_FIX_INTERNAL_ERROR;
        }
    }

    for (const auto &entry : soEntryFiles) {
        auto pos = entry.rfind(ServiceConstants::PATH_SEPARATOR[0]);
        if (pos == std::string::npos) {
            LOG_W(BMS_TAG_INSTALLD, "invalid so entry %{public}s", entry.c_str());
            continue;
        }
        std::string soFileName = entry.substr(pos + 1);
        if (soFileName.empty()) {
            LOG_W(BMS_TAG_INSTALLD, "invalid so entry %{public}s", entry.c_str());
            continue;
        }

        std::string soPath = realSoFilesPath + soFileName;
        LOG_D(BMS_TAG_INSTALLD, "real path of the so file %{public}s is %{public}s",
            soFileName.c_str(), soPath.c_str());

        if (IsExistFile(soPath)) {
            /* mmap so file to ram and write to innerTmpSoPath */
            ErrCode res = ERR_OK;
            LOG_D(BMS_TAG_INSTALLD, "tmp so path is %{public}s", (innerTmpSoPath + soFileName).c_str());
            if ((res = DecryptSoFile(soPath, innerTmpSoPath + soFileName, uid, 0, 0)) != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLD, "decrypt file failed, srcPath is %{public}s and destPath is %{public}s",
                    soPath.c_str(), (innerTmpSoPath + soFileName).c_str());
                return res;
            }
        } else {
            LOG_W(BMS_TAG_INSTALLD, "so file %{public}s is not existed", soPath.c_str());
        }
    }
    return ERR_OK;
}

ErrCode InstalldOperator::DecryptSoFile(const std::string &filePath, const std::string &tmpPath, int32_t uid,
    uint32_t fileSize, uint32_t offset)
{
    LOG_D(BMS_TAG_INSTALLD, "src file is %{public}s, temp path is %{public}s, bundle uid is %{public}d",
        filePath.c_str(), tmpPath.c_str(), uid);
    ErrCode result = ERR_BUNDLEMANAGER_QUICK_FIX_DECRYPTO_SO_FAILED;

    /* call CallIoctl */
    int32_t dev_fd = INVALID_FILE_DESCRIPTOR;
    auto ret = CallIoctl(CODE_DECRYPT_CMD_SET_KEY, CODE_DECRYPT_CMD_SET_ASSOCIATE_KEY, uid, dev_fd);
    if (ret != 0) {
        LOG_E(BMS_TAG_INSTALLD, "CallIoctl failed");
        return result;
    }

    /* mmap hap or so file to ram */
    std::string newfilePath;
    if (!PathToRealPath(filePath, newfilePath)) {
        LOG_E(BMS_TAG_INSTALLD, "file is not real path, file path: %{public}s", filePath.c_str());
        close(dev_fd);
        return result;
    }
    auto fd = open(newfilePath.c_str(), O_RDONLY);
    if (fd < 0) {
        LOG_E(BMS_TAG_INSTALLD, "open hap failed errno:%{public}d", errno);
        close(dev_fd);
        return result;
    }
    struct stat st;
    if (fstat(fd, &st) == INVALID_RETURN_VALUE) {
        LOG_E(BMS_TAG_INSTALLD, "obtain hap file status faield errno:%{public}d", errno);
        close(dev_fd);
        close(fd);
        return result;
    }
    off_t innerFileSize = fileSize;
    if (fileSize == 0) {
        innerFileSize = st.st_size;
    }
    void *addr = mmap(NULL, innerFileSize, PROT_READ, MAP_PRIVATE, fd, offset);
    if (addr == MAP_FAILED) {
        LOG_E(BMS_TAG_INSTALLD, "mmap hap file status faield errno:%{public}d", errno);
        close(dev_fd);
        close(fd);
        return result;
    }

    /* write hap file to the temp path */
    auto outPutFd = BundleUtil::CreateFileDescriptor(tmpPath, 0);
    if (outPutFd < 0) {
        LOG_E(BMS_TAG_INSTALLD, "create fd for tmp hap file failed");
        close(dev_fd);
        close(fd);
        munmap(addr, innerFileSize);
        return result;
    }
    if (write(outPutFd, addr, innerFileSize) != INVALID_RETURN_VALUE) {
        result = ERR_OK;
        LOG_D(BMS_TAG_INSTALLD, "write hap to temp path successfully");
    }
    close(dev_fd);
    close(fd);
    close(outPutFd);
    munmap(addr, innerFileSize);
    return result;
}

ErrCode InstalldOperator::RemoveEncryptedKey(int32_t uid, const std::vector<std::string> &soList)
{
    if (uid == Constants::INVALID_UID) {
        LOG_D(BMS_TAG_INSTALLD, "invalid uid and no need to remove encrypted key");
        return ERR_OK;
    }
    if (soList.empty()) {
        LOG_D(BMS_TAG_INSTALLD, "no new so generated and no need to remove encrypted key");
        return ERR_OK;
    }
    ErrCode result = ERR_BUNDLEMANAGER_QUICK_FIX_DECRYPTO_SO_FAILED;

    /* call CallIoctl */
    int32_t dev_fd = INVALID_FILE_DESCRIPTOR;
    auto ret = CallIoctl(CODE_DECRYPT_CMD_REMOVE_KEY, CODE_DECRYPT_CMD_REMOVE_KEY, uid, dev_fd);
    if (ret == 0) {
        LOG_D(BMS_TAG_INSTALLD, "ioctl successfully");
        result = ERR_OK;
    }
    close(dev_fd);
    return result;
}

int32_t InstalldOperator::CallIoctl(int32_t flag, int32_t associatedFlag, int32_t uid, int32_t &fd)
{
    int32_t installdUid = static_cast<int32_t>(getuid());
    int32_t bundleUid = uid;
    LOG_D(BMS_TAG_INSTALLD, "current process uid is %{public}d and bundle uid is %{public}d", installdUid, bundleUid);

    /* open CODE_DECRYPT */
    std::string newCodeDecrypt;
    if (!PathToRealPath(CODE_DECRYPT, newCodeDecrypt)) {
        LOG_E(BMS_TAG_INSTALLD, "file is not real path, file path: %{public}s", CODE_DECRYPT);
        return INVALID_RETURN_VALUE;
    }
    fd = open(newCodeDecrypt.c_str(), O_RDONLY);
    if (fd < 0) {
        LOG_E(BMS_TAG_INSTALLD, "call open failed errno:%{public}d", errno);
        return INVALID_RETURN_VALUE;
    }

    /* build ioctl args to set key or remove key*/
    struct code_decrypt_arg firstArg;
    firstArg.arg1_len = sizeof(bundleUid);
    firstArg.arg1 = reinterpret_cast<void *>(&bundleUid);
    auto ret = ioctl(fd, flag, &firstArg);
    if (ret != 0) {
        LOG_E(BMS_TAG_INSTALLD, "call ioctl failed errno:%{public}d", errno);
        close(fd);
        fd = INVALID_FILE_DESCRIPTOR;
        return ret;
    }

    struct code_decrypt_arg secondArg;
    secondArg.arg1_len = sizeof(installdUid);
    secondArg.arg1 = reinterpret_cast<void *>(&installdUid);
    if (associatedFlag == CODE_DECRYPT_CMD_SET_ASSOCIATE_KEY) {
        secondArg.arg2_len = sizeof(bundleUid);
        secondArg.arg2 = reinterpret_cast<void *>(&bundleUid);
    }
    ret = ioctl(fd, associatedFlag, &secondArg);
    if (ret != 0) {
        LOG_E(BMS_TAG_INSTALLD, "call ioctl failed errno:%{public}d", errno);
        close(fd);
        fd = INVALID_FILE_DESCRIPTOR;
    }
    return ret;
}
#endif

bool InstalldOperator::GenerateKeyId(const EncryptionParam &encryptionParam, std::string &keyId)
{
    ErrCode ret = ERR_OK;
    switch (encryptionParam.encryptionDirType) {
        case EncryptionDirType::APP:
            ret = Security::AccessToken::El5FilekeyManagerKit::GenerateAppKey(
                static_cast<uint32_t>(encryptionParam.uid), encryptionParam.bundleName, keyId);
            break;
        case EncryptionDirType::GROUP:
            ret = Security::AccessToken::El5FilekeyManagerKit::GenerateGroupIDKey(
                static_cast<uint32_t>(encryptionParam.uid), encryptionParam.groupId, keyId);
            break;
    }

    if (ret == Security::AccessToken::EFM_ERR_KEYID_EXISTED) {
        LOG_I(BMS_TAG_INSTALLD, "key id is existed");
    } else if (ret != 0) {
        LOG_E(BMS_TAG_INSTALLD, "Call GenerateAppKey failed ret = %{public}d", ret);
        return false;
    }
    if (keyId.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "keyId is empty");
        return false;
    }
    return true;
}

bool InstalldOperator::SetKeyIdPolicy(const EncryptionParam &encryptionParam, const std::string &keyId)
{
    struct fscrypt_asdp_policy policy;
    policy.version = 0;
    policy.asdp_class = FORCE_PROTECT;
    // keyId length = KEY_ID_STEP * FSCRYPT_KEY_DESCRIPTOR_SIZE
    for (uint32_t i = 0; i < keyId.size(); i += KEY_ID_STEP) {
        if (i / KEY_ID_STEP >= FSCRYPT_KEY_DESCRIPTOR_SIZE) {
            break;
        }
        std::string byteString = keyId.substr(i, KEY_ID_STEP);
        char byte = (char)strtol(byteString.c_str(), NULL, 16);
        policy.app_key2_descriptor[i / KEY_ID_STEP] = byte;
    }
    std::vector<std::string> dirs;
    switch (encryptionParam.encryptionDirType) {
        case EncryptionDirType::APP:
            dirs.emplace_back(std::string(ServiceConstants::SCREEN_LOCK_FILE_DATA_PATH) +
                ServiceConstants::PATH_SEPARATOR + std::to_string(encryptionParam.userId) +
                ServiceConstants::BASE + encryptionParam.bundleName);
            dirs.emplace_back(std::string(ServiceConstants::SCREEN_LOCK_FILE_DATA_PATH) +
                ServiceConstants::PATH_SEPARATOR + std::to_string(encryptionParam.userId) +
                ServiceConstants::DATABASE + encryptionParam.bundleName);
            break;
        case EncryptionDirType::GROUP:
            dirs.emplace_back(std::string(ServiceConstants::SCREEN_LOCK_FILE_DATA_PATH) +
                ServiceConstants::PATH_SEPARATOR + std::to_string(encryptionParam.userId) +
                ServiceConstants::DATA_GROUP_PATH + encryptionParam.groupId);
            break;
    }

    for (const auto &dir : dirs) {
        auto fd = open(dir.c_str(), O_DIRECTORY | O_NOFOLLOW | O_CLOEXEC);
        if (fd < 0) {
            LOG_E(BMS_TAG_INSTALLD, "open filePath failed");
            return false;
        }
        fdsan_exchange_owner_tag(fd, 0, BMS_FDSAN_INSTALLD_TAG);
        // call ioctl to set e policy
        auto result = ioctl(fd, HMFS_IOC_SET_ASDP_ENCRYPTION_POLICY, &policy);
        if (result != 0) {
            LOG_E(BMS_TAG_INSTALLD, "ioctl failed result:%{public}d %{public}d", result, errno);
            fdsan_close_with_tag(fd, BMS_FDSAN_INSTALLD_TAG);
            return false;
        }
        fdsan_close_with_tag(fd, BMS_FDSAN_INSTALLD_TAG);
    }
    return true;
}

bool InstalldOperator::GenerateKeyIdAndSetPolicy(const EncryptionParam &encryptionParam, std::string &keyId)
{
    if (!GenerateKeyId(encryptionParam, keyId)) {
        LOG_E(BMS_TAG_INSTALLD, "generate keyId error");
        return false;
    }
    if (!SetKeyIdPolicy(encryptionParam, keyId)) {
        LOG_E(BMS_TAG_INSTALLD, "set keyId policy error");
        return false;
    }
    std::string key = encryptionParam.encryptionDirType == EncryptionDirType::APP ?
        encryptionParam.bundleName : encryptionParam.groupId;
    LOG_I(BMS_TAG_INSTALLD, "success for %{public}s", key.c_str());
    return true;
}

bool InstalldOperator::DeleteKeyId(const EncryptionParam &encryptionParam)
{
    const int32_t userId = encryptionParam.userId;
    std::string key;
    ErrCode result = ERR_OK;
    switch (encryptionParam.encryptionDirType) {
        case EncryptionDirType::APP:
            key = encryptionParam.bundleName;
            result = Security::AccessToken::El5FilekeyManagerKit::DeleteAppKey(key, userId);
            break;
        case EncryptionDirType::GROUP:
            key = encryptionParam.groupId;
            result = Security::AccessToken::El5FilekeyManagerKit::DeleteGroupIDKey(userId, key);
            break;
    }
    LOG_I(BMS_TAG_INSTALLD, "DeleteKeyId %{public}s %{public}d", key.c_str(), userId);
    if (result != 0) {
        LOG_E(BMS_TAG_INSTALLD, "failed ret = %{public}d", result);
        return false;
    }
    return true;
}

bool InstalldOperator::GetAtomicServiceBundleDataDir(const std::string &bundleName,
    const int32_t userId, std::vector<std::string> &allPathNames)
{
    std::string baseDir = ServiceConstants::BUNDLE_APP_DATA_BASE_DIR + ServiceConstants::BUNDLE_EL[0] +
        ServiceConstants::PATH_SEPARATOR + std::to_string(userId) + ServiceConstants::BASE;
    DIR *dir = opendir(baseDir.c_str());
    if (dir == nullptr) {
        LOG_E(BMS_TAG_INSTALLD, "fail to opendir:%{public}s, errno:%{public}d", baseDir.c_str(), errno);
        return false;
    }
    struct dirent *ptr = nullptr;
    while ((ptr = readdir(dir)) != nullptr) {
        if (ptr->d_type == DT_DIR) {
            std::string pathName(ptr->d_name);
            if (pathName.find(ATOMIC_SERVICE_PATH) != 0) {
                continue;
            }
            auto pos = pathName.rfind(bundleName);
            if ((pos != std::string::npos) && (pos == (pathName.size() - bundleName.size()))) {
                allPathNames.emplace_back(pathName);
            }
        }
    }
    closedir(dir);
    return !allPathNames.empty();
}

void InstalldOperator::AddDeleteDfx(const std::string &path)
{
    FILE* fp = fopen(path.c_str(), "r");
    if (fp == nullptr) {
        LOG_D(BMS_TAG_INSTALLD, "open dfx path %{public}s failed", path.c_str());
        return;
    }
    int32_t fd = fileno(fp);
    unsigned int flags = 0;
    int32_t ret = ioctl(fd, HMF_IOCTL_HW_GET_FLAGS, &flags);
    if (ret < 0) {
        LOG_D(BMS_TAG_INSTALLD, "check dfx flag path %{public}s failed errno:%{public}d", path.c_str(), errno);
        (void)fclose(fp);
        return;
    }
    if (flags & HMFS_MONITOR_FL) {
        LOG_D(BMS_TAG_INSTALLD, "Delete Control flag is already set");
        (void)fclose(fp);
        return;
    }
    flags |= HMFS_MONITOR_FL;
    ret = ioctl(fd, HMF_IOCTL_HW_SET_FLAGS, &flags);
    if (ret < 0) {
        LOG_W(BMS_TAG_INSTALLD, "Add dfx flag failed errno:%{public}d path %{public}s", errno, path.c_str());
        (void)fclose(fp);
        return;
    }
    LOG_I(BMS_TAG_INSTALLD, "Delete Control flag of %{public}s is set succeed", path.c_str());
    (void)fclose(fp);
    return;
}

void InstalldOperator::RmvDeleteDfx(const std::string &path)
{
    if (path.find(BUNDLE_BASE_CODE_DIR) != std::string::npos) {
        LOG_D(BMS_TAG_INSTALLD, "codeDir:%{public}s not need delete", path.c_str());
        return;
    }
    FILE* fp = fopen(path.c_str(), "r");
    if (fp == nullptr) {
        LOG_D(BMS_TAG_INSTALLD, "open dfx path %{public}s failed", path.c_str());
        return;
    }
    int32_t fd = fileno(fp);
    unsigned int flags = 0;
    int32_t ret = ioctl(fd, HMF_IOCTL_HW_GET_FLAGS, &flags);
    if (ret < 0) {
        LOG_D(BMS_TAG_INSTALLD, "check dfx flag path %{public}s failed errno:%{public}d", path.c_str(), errno);
        (void)fclose(fp);
        return;
    }
    if (flags & HMFS_MONITOR_FL) {
        // flag is already set
        flags -= HMFS_MONITOR_FL;
        ret = ioctl(fd, HMF_IOCTL_HW_SET_FLAGS, &flags);
        if (ret < 0) {
            LOG_W(BMS_TAG_INSTALLD, "Rmv dfx flag failed errno:%{public}d path %{public}s", errno, path.c_str());
            (void)fclose(fp);
            return;
        }
        LOG_D(BMS_TAG_INSTALLD, "Delete Control flag of %{public}s is Rmv succeed", path.c_str());
    }
    (void)fclose(fp);
    return;
}

int32_t InstalldOperator::MigrateData(const std::vector<std::string> &sourcePaths, const std::string &destinationPath)
{
    LOG_I(BMS_TAG_INSTALLD, "MigrateData start");
    std::vector<std::string> realSourcePaths;
    std::for_each(sourcePaths.begin(), sourcePaths.end(), [&realSourcePaths](const std::string &path) {
        std::string realPath;
        if (!PathToRealPath(path, realPath)) {
            LOG_E(BMS_TAG_INSTALLD, "file(%{private}s) is not real path", path.c_str());
            return;
        }
        realSourcePaths.push_back(realPath);
    });
    // all sourcePaths are invalid, need return error
    if (realSourcePaths.empty()) {
        return ERR_BUNDLE_MANAGER_MIGRATE_DATA_SOURCE_PATH_INVALID;
    }
    std::string destPath;
    if (!PathToRealPath(destinationPath, destPath)) {
        LOG_E(BMS_TAG_INSTALLD, "file(%{private}s) is not real path", destinationPath.c_str());
        return ERR_BUNDLE_MANAGER_MIGRATE_DATA_DESTINATION_PATH_INVALID;
    }
    OwnershipInfo info;
    auto result = MigrateDataCheckPrmissions(realSourcePaths, destPath, info);
    if (result != RESULT_OK) {
        LOG_E(BMS_TAG_INSTALLD, "migrate data check permissions failed, result:%{public}d", result);
        if (realSourcePaths.empty() || result != ERR_BUNDLE_MANAGER_MIGRATE_DATA_SOURCE_PATH_ACCESS_FAILED) {
            return result;
        }
    }
    auto ret = RESULT_OK;
    for (const auto &sourcePath : realSourcePaths) {
        ret = InnerMigrateData(sourcePath, destPath, info);
        if (ret != RESULT_OK) {
            LOG_W(BMS_TAG_INSTALLD, "inner migrate data failed, errno:%{public}d", errno);
            result = ret;
        }
    }
    LOG_I(BMS_TAG_INSTALLD, "MigrateData end");
    return result;
}

int32_t InstalldOperator::InnerMigrateData(
    const std::string &sourcePaths, const std::string &destinationPath, const OwnershipInfo &info)
{
    struct stat buf = {};
    if (stat(sourcePaths.c_str(), &buf) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "fail to stat errno:%{public}d", errno);
        return ERR_BUNDLE_MANAGER_MIGRATE_DATA_SOURCE_PATH_ACCESS_FAILED;
    }
    if (access(sourcePaths.c_str(), R_OK) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "source path[%{public}s] access failed", sourcePaths.c_str());
        return ERR_BUNDLE_MANAGER_MIGRATE_DATA_SOURCE_PATH_ACCESS_FAILED;
    }
    auto result = RESULT_OK;
    std::string targetPath = destinationPath;
    result = MigrateDataCreateAhead(sourcePaths, targetPath, info);
    if (result != RESULT_OK) {
        LOG_E(BMS_TAG_INSTALLD, "migrate data create ahead failed");
        return result;
    }
    if (!S_ISDIR(buf.st_mode)) {
        std::string fileName = sourcePaths;
        auto pos = sourcePaths.rfind(ServiceConstants::PATH_SEPARATOR);
        if (pos != std::string::npos) {
            fileName = sourcePaths.substr(pos + 1);
        }
        std::string destPath = OHOS::IncludeTrailingPathDelimiter(targetPath) + fileName;
        result = MigrateDataCopyFile(sourcePaths, destPath, info);
        if (result != RESULT_OK) {
            LOG_E(BMS_TAG_INSTALLD, "migrate data source:%{private}s to destination %{public}s failed",
                sourcePaths.c_str(), destPath.c_str());
        }
        return result;
    }
    result = MigrateDataCopyDir(sourcePaths, targetPath, info);
    if (result != RESULT_OK) {
        LOG_E(BMS_TAG_INSTALLD, "migrate data copy dir failed, source:%{private}s to destination %{public}s",
            sourcePaths.c_str(), destinationPath.c_str());
    }
    return result;
}

int32_t InstalldOperator::MigrateDataCopyFile(
    const std::string &sourceFile, const std::string &destinationFile, const OwnershipInfo &info)
{
    std::ifstream in(sourceFile);
    if (!in.is_open()) {
        LOG_E(BMS_TAG_INSTALLD, "Copy file failed due to open sourceFile failed errno:%{public}d", errno);
        return errno == PERMISSION_DENIED ? ERR_BUNDLE_MANAGER_MIGRATE_DATA_SOURCE_PATH_ACCESS_FAILED
                                          : ERR_BUNDLE_MANAGER_MIGRATE_DATA_OTHER_REASON_FAILED;
    }
    std::ofstream out(destinationFile);
    if (!out.is_open()) {
        LOG_E(BMS_TAG_INSTALLD, "Copy file failed due to open destinationFile[%{public}s] failed errno:%{public}d",
            destinationFile.c_str(), errno);
        in.close();
        return errno == PERMISSION_DENIED ? ERR_BUNDLE_MANAGER_MIGRATE_DATA_DESTINATION_PATH_ACCESS_FAILED
                                          : ERR_BUNDLE_MANAGER_MIGRATE_DATA_OTHER_REASON_FAILED;
    }
    out << in.rdbuf();
    in.close();
    out.close();
    auto result = UpdateFileProperties(destinationFile, info);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLD, "fail to update file properties");
        return result;
    }
    return ERR_OK;
}

int32_t InstalldOperator::MigrateDataCopyDir(
    const std::string &sourcePath, const std::string &destinationPath, const OwnershipInfo &info)
{
    // create dir if not exist
    auto result = RESULT_OK;
    result = ForceCreateDirectory(destinationPath, info);
    if (result != RESULT_OK) {
        LOG_E(BMS_TAG_INSTALLD, "create targetPath %{public}s failed", destinationPath.c_str());
        return ERR_BUNDLE_MANAGER_MIGRATE_DATA_OTHER_REASON_FAILED;
    }
    DIR *dir = opendir(sourcePath.c_str());
    if (dir == nullptr) {
        LOG_E(BMS_TAG_INSTALLD, "fail to opendir:%{private}s, errno:%{public}d", sourcePath.c_str(), errno);
        return errno == PERMISSION_DENIED ? ERR_BUNDLE_MANAGER_MIGRATE_DATA_SOURCE_PATH_ACCESS_FAILED
                                          : ERR_BUNDLE_MANAGER_MIGRATE_DATA_OTHER_REASON_FAILED;
    }

    struct dirent *ptr = nullptr;
    while ((ptr = readdir(dir)) != nullptr) {
        if (strcmp(ptr->d_name, ".") == 0 || strcmp(ptr->d_name, "..") == 0) {
            continue;
        }
        std::string subPath = OHOS::IncludeTrailingPathDelimiter(sourcePath) + std::string(ptr->d_name);
        std::string destPath = OHOS::IncludeTrailingPathDelimiter(destinationPath) + std::string(ptr->d_name);
        if (ptr->d_type == DT_DIR) {
            result = MigrateDataCopyDir(subPath, destPath, info);
            if (result != RESULT_OK) {
                LOG_E(BMS_TAG_INSTALLD, "migrate data failed, result:%{public}d", result);
            }
            continue;
        }
        result = MigrateDataCopyFile(subPath, destPath, info);
        if (result != RESULT_OK) {
            LOG_E(BMS_TAG_INSTALLD,
                "migrate data source:%{private}s to destination %{public}s failed, result:%{public}d", subPath.c_str(),
                destPath.c_str(), result);
        }
    }
    closedir(dir);
    return result;
}

int32_t InstalldOperator::MigrateDataCheckPrmissions(
    std::vector<std::string> &realSourcePaths, const std::string &destPath, OwnershipInfo &info)
{
    auto result = RESULT_OK;
    std::vector<std::string> unablePath;
    // check read permissions
    auto noReadPermission = [&unablePath, &result](const std::string &path) {
        if (access(path.c_str(), R_OK) != 0) {
            LOG_E(BMS_TAG_INSTALLD, "source path[%{public}s] access failed", path.c_str());
            unablePath.push_back(path);
            result = ERR_BUNDLE_MANAGER_MIGRATE_DATA_SOURCE_PATH_ACCESS_FAILED;
        }
    };
    std::for_each(realSourcePaths.begin(), realSourcePaths.end(), noReadPermission);
    if (result != RESULT_OK) {
        auto isUnablePath = [&unablePath](const std::string &path) -> bool {
            return std::find(unablePath.begin(), unablePath.end(), path) != unablePath.end();
        };
        realSourcePaths.erase(
            std::remove_if(realSourcePaths.begin(), realSourcePaths.end(), isUnablePath), realSourcePaths.end());
        if (realSourcePaths.empty()) {
            return result;
        }
    }
    // check write permissions
    if (access(destPath.c_str(), W_OK) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "dest path[%{public}s] access failed", destPath.c_str());
        return ERR_BUNDLE_MANAGER_MIGRATE_DATA_DESTINATION_PATH_ACCESS_FAILED;
    }
    if (!IsExistDir(destPath)) {
        LOG_E(BMS_TAG_INSTALLD, "dest path[%{public}s] not a directory", destPath.c_str());
        return ERR_BUNDLE_MANAGER_MIGRATE_DATA_DESTINATION_PATH_INVALID;
    }
    struct stat destPathStat = {};
    if (stat(destPath.c_str(), &destPathStat) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "fail to get dest path stat stat errno:%{public}d", errno);
        return ERR_BUNDLE_MANAGER_MIGRATE_DATA_DESTINATION_PATH_ACCESS_FAILED;
    }
    info.uid = static_cast<int32_t>(destPathStat.st_uid);
    info.gid = static_cast<int32_t>(destPathStat.st_gid);
    info.mode = static_cast<int32_t>(destPathStat.st_mode);
    return result;
}


int32_t InstalldOperator::UpdateFileProperties(const std::string &newFile, const OwnershipInfo &info)
{
    int32_t result = ERR_BUNDLE_MANAGER_MIGRATE_DATA_OTHER_REASON_FAILED;
    if (access(newFile.c_str(), F_OK) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "new file not existence.");
        return result;
    }
    if (chmod(newFile.c_str(), info.mode) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "failed to set mode for new file: %{public}s", newFile.c_str());
        return result;
    }
    if (chown(newFile.c_str(), info.uid, info.gid) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "failed to set uid and gid for new file: %{public}s", newFile.c_str());
        return result;
    }
    return ERR_OK;
}

int32_t InstalldOperator::ForceCreateDirectory(const std::string &path, const OwnershipInfo &info)
{
    auto result = ERR_BUNDLE_MANAGER_MIGRATE_DATA_OTHER_REASON_FAILED;
    if (IsExistDir(path)) {
        LOG_D(BMS_TAG_INSTALLD, "path already exists");
        return RESULT_OK;
    }
    LOG_D(BMS_TAG_INSTALLD, "need crate dir: %{public}s", path.c_str());
    if (!OHOS::ForceCreateDirectory(path)) {
        LOG_E(BMS_TAG_INSTALLD, "mkdir failed");
        return result;
    }
    if (!OHOS::ChangeModeDirectory(path, info.mode)) {
        LOG_E(BMS_TAG_INSTALLD, "failed to set mod for path: %{public}s", path.c_str());
        return result;
    }
    if (chown(path.c_str(), info.uid, info.gid) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "failed to set uid and gid for new file: %{public}s", path.c_str());
        return result;
    }
    return RESULT_OK;
}

int32_t InstalldOperator::MigrateDataCreateAhead(
    const std::string &sourcePaths, std::string &destinationPath, const OwnershipInfo &info)
{
    if (sourcePaths.empty() || destinationPath.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "incoming path exception");
        return sourcePaths.empty() ? ERR_BUNDLE_MANAGER_MIGRATE_DATA_SOURCE_PATH_INVALID
                                   : ERR_BUNDLE_MANAGER_MIGRATE_DATA_DESTINATION_PATH_INVALID;
    }
    // to set the directory properties correctly
    std::vector<std::string> parts;
    size_t pos = 0;
    size_t nextPos = 0;
    while ((nextPos = sourcePaths.find('/', pos + 1)) != std::string::npos) {
        parts.push_back(sourcePaths.substr(pos, nextPos - pos));
        pos = nextPos;
    }
    if (std::filesystem::is_directory(sourcePaths) && pos < sourcePaths.length()) {
        parts.push_back(sourcePaths.substr(pos));
    }
    if (parts.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "parts is empty");
        return ERR_BUNDLE_MANAGER_MIGRATE_DATA_OTHER_REASON_FAILED;
    }
    std::string destPath = OHOS::ExcludeTrailingPathDelimiter(destinationPath);
    for (const auto &dir : parts) {
        destPath += dir;
        LOG_D(BMS_TAG_INSTALLD, "will be destPath(%{public}s)", destPath.c_str());
        auto resutl = ForceCreateDirectory(destPath, info);
        if (resutl != RESULT_OK) {
            LOG_E(BMS_TAG_INSTALLD, "create destPath failed");
            return resutl;
        }
    }
    // new target path
    destinationPath = destPath;
    return RESULT_OK;
}

#if defined(CODE_ENCRYPTION_ENABLE)
std::mutex InstalldOperator::encryptionMutex_;
void *InstalldOperator::encryptionHandle_ = nullptr;
EnforceMetadataProcessForApp InstalldOperator::enforceMetadataProcessForApp_ = nullptr;

bool InstalldOperator::OpenEncryptionHandle()
{
    std::lock_guard<std::mutex> lock(encryptionMutex_);
    if (encryptionHandle_ != nullptr && enforceMetadataProcessForApp_ != nullptr) {
        LOG_NOFUNC_D(BMS_TAG_INSTALLD, "encrypt handle opened");
        return true;
    }
    LOG_NOFUNC_D(BMS_TAG_INSTALLD, "OpenEncryption start");
    encryptionHandle_ = dlopen(LIB64_CODE_CRYPTO_SO_PATH, RTLD_NOW | RTLD_GLOBAL);
    if (encryptionHandle_ == nullptr) {
        LOG_W(BMS_TAG_INSTALLD, "open encrypt lib64 failed %{public}s", dlerror());
        encryptionHandle_ = dlopen(LIB_CODE_CRYPTO_SO_PATH, RTLD_NOW | RTLD_GLOBAL);
    }
    if (encryptionHandle_ == nullptr) {
        LOG_E(BMS_TAG_INSTALLD, "open encrypt lib failed %{public}s", dlerror());
        return false;
    }
    enforceMetadataProcessForApp_ =
        reinterpret_cast<EnforceMetadataProcessForApp>(dlsym(encryptionHandle_, CODE_CRYPTO_FUNCTION_NAME));
    if (enforceMetadataProcessForApp_ == nullptr) {
        LOG_E(BMS_TAG_INSTALLD, "dlsym encrypt err:%{public}s", dlerror());
        dlclose(encryptionHandle_);
        encryptionHandle_ = nullptr;
        return false;
    }
    return true;
}

ErrCode InstalldOperator::EnforceEncryption(std::unordered_map<std::string, std::string> &entryMap,
    const CodeCryptoHapInfo &hapInfo, bool &isEncryption)
{
    if (!OpenEncryptionHandle()) {
        return ERR_APPEXECFWK_INSTALL_ENCRYPTION_DLL_ERROR;
    }
    ErrCode ret = enforceMetadataProcessForApp_(entryMap, hapInfo, isEncryption);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLD, "CheckEncryption failed due to %{public}d", ret);
        return ERR_APPEXECFWK_INSTALL_CHECK_ENCRYPTION_FAILED;
    }
    return ERR_OK;
}
#endif

bool InstalldOperator::ClearDir(const std::string &dir)
{
    std::filesystem::path path(dir);
    std::error_code ec;
    if (!std::filesystem::exists(path, ec) || !std::filesystem::is_directory(path, ec)) {
        LOG_E(BMS_TAG_INSTALLD, "invalid path:%{public}s,err:%{public}s", dir.c_str(), ec.message().c_str());
        return true;
    }

    std::filesystem::directory_iterator dirIter(path, std::filesystem::directory_options::skip_permission_denied, ec);
    std::filesystem::directory_iterator endIter;
    if (ec) {
        LOG_E(BMS_TAG_INSTALLD, "create iterator failed,%{public}s,err:%{public}s", dir.c_str(), ec.message().c_str());
        return false;
    }

    std::vector<std::filesystem::path> delPathVector;
    for (; dirIter != endIter; dirIter.increment(ec)) {
        if (ec) {
            LOG_E(BMS_TAG_INSTALLD, "iteration failed,%{public}s,err:%{public}s", dir.c_str(), ec.message().c_str());
            return false;
        }
        delPathVector.emplace_back(dirIter->path());
    }

    bool isSuccess = true;
    for (const auto &delPath : delPathVector) {
        ec.clear();
        std::filesystem::remove_all(delPath, ec);
        if (ec) {
            LOG_E(BMS_TAG_INSTALLD, "remove_all failed,%{public}s,err:%{public}s", dir.c_str(), ec.message().c_str());
            isSuccess = false;
        }
    }
    if (isSuccess) {
        LOG_NOFUNC_I(BMS_TAG_INSTALLD, "clearDir success");
    } else {
        LOG_NOFUNC_W(BMS_TAG_INSTALLD, "clearDir completed with errors");
    }
    return isSuccess;
}

std::string InstalldOperator::Sha256File(const std::string& filePath)
{
    if (filePath.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "failed due to filePath is empty");
        return "";
    }

    std::ifstream file(filePath, std::ios::binary);
    if (!file) {
        LOG_E(BMS_TAG_INSTALLD, "failed due to open filePath failed errno:%{public}d", errno);
        return "";
    }

    SHA256_CTX sha256;
    SHA256_Init(&sha256);

    std::vector<char> buffer(BUFFER_SIZE);
    while (file.read(buffer.data(), buffer.size()) || file.gcount() > 0) {
        SHA256_Update(&sha256, buffer.data(), file.gcount());
    }

    unsigned char hash[SHA256_DIGEST_LENGTH];
    SHA256_Final(hash, &sha256);

    std::stringstream ss;
    for (int i = 0; i < SHA256_DIGEST_LENGTH; i++) {
        ss << std::hex << std::setw(2) << std::setfill('0') << (int)hash[i];
    }

    return ss.str();
}

ErrCode InstalldOperator::HashSoFile(const std::string& soPath,
    uint32_t catchSoNum,
    uint64_t catchSoMaxSize,
    std::vector<std::string> &soName,
    std::vector<std::string> &soHash)
{
    uint64_t maxSize = static_cast<uint64_t>(catchSoMaxSize);
    std::vector<SoFileInfo> soFiles;
    std::error_code ec;
    if (!std::filesystem::exists(soPath, ec) || !std::filesystem::is_directory(soPath, ec)) {
        LOG_W(BMS_TAG_INSTALLD, "invalid path:%{public}s,err:%{public}s", soPath.c_str(), ec.message().c_str());
        return ERR_APPEXECFWK_NO_SO_EXISTED;
    }
    std::filesystem::directory_iterator dirIter(soPath,
        std::filesystem::directory_options::skip_permission_denied, ec);
    std::filesystem::directory_iterator endIter;
    if (ec) {
        LOG_W(BMS_TAG_INSTALLD, "create iterator failed,%{public}s,err:%{public}s",
            soPath.c_str(), ec.message().c_str());
        return ERR_APPEXECFWK_NO_SO_EXISTED;
    }
    for (; dirIter != endIter; dirIter.increment(ec)) {
        if (ec) {
            LOG_W(BMS_TAG_INSTALLD, "iteration failed,%{public}s,err:%{public}s", soPath.c_str(), ec.message().c_str());
            return ERR_APPEXECFWK_NO_SO_EXISTED;
        }
        const std::filesystem::directory_entry &entry = *dirIter;
        if (!entry.is_regular_file()) {
            continue;
        }
        const auto& path = entry.path();
        uint64_t fileSize = std::filesystem::file_size(path);
        if (fileSize <= maxSize) {
            soFiles.emplace_back(SoFileInfo(path.filename().string(), path.string(), fileSize));
        }
    }

    if (soFiles.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "no so file existed");
        return ERR_APPEXECFWK_NO_SO_EXISTED;
    }

    std::sort(soFiles.begin(), soFiles.end());
    if (soFiles.size() > static_cast<size_t>(catchSoNum)) {
        soFiles.resize(catchSoNum);
    }

    for (const auto& soFile : soFiles) {
        std::string hash = Sha256File(soFile.fullpath);
        soName.push_back(soFile.filename);
        soHash.push_back(hash);
    }
    return ERR_OK;
}

bool InstalldOperator::WriteCertToFile(const std::string &certFilePath, const std::string &certContent)
{
    if (certFilePath.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "certFilePath is empty");
        return false;
    }

    std::string tmpPath = certFilePath + ".tmp";
    int fd = open(tmpPath.c_str(), O_CREAT | O_TRUNC | O_WRONLY, S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH);
    if (fd < 0) {
        LOG_E(BMS_TAG_INSTALLD, "open tmp cert file failed %{public}s errno:%{public}d", tmpPath.c_str(), errno);
        return false;
    }
    fdsan_exchange_owner_tag(fd, 0, BMS_FDSAN_INSTALLD_TAG);

    const char *buf = certContent.data();
    size_t toWrite = certContent.size();
    size_t written = 0;
    while (written < toWrite) {
        ssize_t writeSize = write(fd, buf + written, toWrite - written);
        if (writeSize < 0) {
            if (errno == EINTR) continue;
            LOG_E(BMS_TAG_INSTALLD, "write tmp cert file failed %{public}s errno:%{public}d", tmpPath.c_str(), errno);
            fdsan_close_with_tag(fd, BMS_FDSAN_INSTALLD_TAG);
            unlink(tmpPath.c_str());
            return false;
        }
        written += static_cast<size_t>(writeSize);
    }

    if (fsync(fd) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "fsync failed %{public}s errno:%{public}d", tmpPath.c_str(), errno);
        fdsan_close_with_tag(fd, BMS_FDSAN_INSTALLD_TAG);
        unlink(tmpPath.c_str());
        return false;
    }

    if (fdsan_close_with_tag(fd, BMS_FDSAN_INSTALLD_TAG) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "close failed %{public}s errno:%{public}d", tmpPath.c_str(), errno);
        unlink(tmpPath.c_str());
        return false;
    }

    if (chown(tmpPath.c_str(), Constants::FOUNDATION_UID, Constants::FOUNDATION_UID) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "chown tmp cert file failed %{public}s errno:%{public}d", tmpPath.c_str(), errno);
        unlink(tmpPath.c_str());
        return false;
    }

    if (rename(tmpPath.c_str(), certFilePath.c_str()) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "rename tmp cert file to final failed %{public}s -> %{public}s errno:%{public}d",
            tmpPath.c_str(), certFilePath.c_str(), errno);
        unlink(tmpPath.c_str());
        return false;
    }

    return true;
}

bool InstalldOperator::RestoreconPath(const std::string &path)
{
    int ret = RestoreconRecurse(path.c_str());
    if (ret == ERR_OK) {
        LOG_I(BMS_TAG_INSTALLD, "RestoreconPath success");
        return true;
    }
    LOG_E(BMS_TAG_INSTALLD, "RestoreconPath failed, ret: %{public}d", ret);
    return false;
}

ErrCode InstalldOperator::SetBinFileLabel(const std::string &binFilePath)
{
    if (binFilePath.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "binFilePath is empty");
        return ERR_APPEXECFWK_INSTALLD_PARAM_ERROR;
    }

    if (access(binFilePath.c_str(), F_OK) != 0) {
        LOG_E(BMS_TAG_INSTALLD, "bin file not found: %{public}s", binFilePath.c_str());
        return ERR_APPEXECFWK_INSTALL_FAILED_ACCESS_BIN_FILE;
    }

#ifdef WITH_SELINUX
    // Set SELinux context for executable files
    const char* context = "u:object_r:app_bin_file:s0";
    if (lsetfilecon(binFilePath.c_str(), context) < 0) {
        LOG_E(BMS_TAG_INSTALLD, "setcon for %{public}s failed, errno:%{public}d",
            binFilePath.c_str(), errno);
        return ERR_APPEXECFWK_INSTALLD_SET_SELINUX_LABEL_FAILED;
    }
#endif

    LOG_I(BMS_TAG_INSTALLD, "SetBinFileLabel success %{public}s", binFilePath.c_str());
    return ERR_OK;
}

bool InstalldOperator::IsFileNameValid(const std::string &fileName)
{
    if (fileName.find("../") != std::string::npos
        || fileName.find("/..") != std::string::npos) {
        return false;
    }
    return true;
}

bool InstalldOperator::CopyDir(const std::string &sourceDir, const std::string &destinationDir)
{
    LOG_D(BMS_TAG_INSTALLD, "sourceDir is %{public}s, destinationDir is %{public}s",
        sourceDir.c_str(), destinationDir.c_str());
    if (sourceDir.empty() || destinationDir.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "Copy dir failed due to sourceDir or destinationDir is empty");
        return false;
    }

    std::string realPath = "";
    if (!PathToRealPath(sourceDir, realPath)) {
        LOG_E(BMS_TAG_INSTALLD, "sourceDir(%{public}s) is not real path", sourceDir.c_str());
        return false;
    }

    if (!OHOS::ForceCreateDirectory(destinationDir)) {
        LOG_E(BMS_TAG_INSTALLD, "Failed to create destination directory");
        return false;
    }

    DIR* directory = opendir(realPath.c_str());
    if (directory == nullptr) {
        LOG_E(BMS_TAG_INSTALLD, "open dir(%{public}s) fail, errno:%{public}d", realPath.c_str(), errno);
        return false;
    }

    struct dirent *ptr = nullptr;
    while ((ptr = readdir(directory)) != nullptr) {
        std::string currentName(ptr->d_name);
        if (currentName.compare(".") == 0 || currentName.compare("..") == 0) {
            continue;
        }

        std::string curPath = sourceDir + ServiceConstants::PATH_SEPARATOR + currentName;
        std::string destPath = destinationDir + ServiceConstants::PATH_SEPARATOR + currentName;
        if (ptr->d_type == DT_DIR) {
            if (!CopyDir(curPath, destPath)) {
                LOG_E(BMS_TAG_INSTALLD, "Create directory(%{public}s) fail", curPath.c_str());
            }
            continue;
        }
        struct stat fileStat;
        if (stat(curPath.c_str(), &fileStat) != 0) {
            LOG_E(BMS_TAG_INSTALLD, "Failed to get file info: %s", curPath.c_str());
            continue;
        }
        if (CopyFile(curPath, destPath)) {
            ChangeFileAttr(destPath, fileStat.st_uid, fileStat.st_gid);
            if (chmod(destPath.c_str(), fileStat.st_mode) != 0) {
                LOG_E(BMS_TAG_INSTALLD, "chmod file(%{public}s) fail", destPath.c_str());
            }
        } else {
            LOG_E(BMS_TAG_INSTALLD, "Copy file(%{public}s) to (%{public}s) fail", curPath.c_str(), destPath.c_str());
        }
    }
    closedir(directory);
    return true;
}

ErrCode InstalldOperator::DeleteCertAndRemoveKey(const std::string &path)
{
    if (path.empty() || path.size() > Constants::BMS_MAX_PATH_LENGTH) {
        LOG_E(BMS_TAG_INSTALLD, "path is empty or size is too large");
        return ERR_APPEXECFWK_INSTALLD_PARAM_ERROR;
    }
    std::vector<unsigned char> certData;
    if (!ReadCert(path, certData)) {
        LOG_E(BMS_TAG_INSTALLD, "read cert failed");
        return ERR_APPEXECFWK_ENTERPRISE_CERT_READ_CERT_FAILED;
    }
    std::string tempPath = path + ServiceConstants::DELETE_CERT_PREFIX;
    if (!RenameFile(path, tempPath)) {
        LOG_E(BMS_TAG_INSTALLD, "rename to tempPath failed, errno: %{public}d", errno);
        return ERR_APPEXECFWK_ENTERPRISE_CERT_RENAME_CERT_FAILED;
    }
#if defined(CODE_SIGNATURE_ENABLE)
    Security::CodeSign::ByteBuffer byteBuffer;
    byteBuffer.CopyFrom(reinterpret_cast<const uint8_t *>(certData.data()), static_cast<int32_t>(certData.size()));
    ErrCode ret = Security::CodeSign::CodeSignUtils::RemoveKeyForEnterpriseResign(byteBuffer);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLD, "failed due to error %{public}d", ret);
        if (!RenameFile(tempPath, path)) {
            LOG_E(BMS_TAG_INSTALLD, "rename failed, errno: %{public}d", errno);
        }
        return ERR_APPEXECFWK_ENTERPRISE_CERT_REMOVE_KEY_ERROR;
    }
    LOG_I(BMS_TAG_INSTALLD, "end");
#else
    LOG_W(BMS_TAG_INSTALLD, "code signature feature is not supported");
#endif
    if (!DeleteDir(tempPath)) {
        LOG_E(BMS_TAG_INSTALLD, "del cert failed, errno: %{public}d", errno);
    }
    return ERR_OK;
}

bool InstalldOperator::ReadCert(const std::string &path, std::vector<unsigned char> &certData)
{
#if defined(CODE_SIGNATURE_ENABLE)
    FILE *file = fopen(path.c_str(), "r");
    if (file == nullptr) {
        LOG_E(BMS_TAG_INSTALLD, "open file failed path:%{public}s errno: %{public}d", path.c_str(), errno);
        return false;
    }
    int32_t fd = fileno(file);
    if (fd < 0) {
        LOG_E(BMS_TAG_INSTALLD, "get fd failed");
        fclose(file);
        return false;
    }

    struct stat statBuf;
    if (fstat(fd, &statBuf) < 0) {
        LOG_E(BMS_TAG_INSTALLD, "Fstat failed, errno: %{public}d", errno);
        fclose(file);
        return false;
    }
    if (statBuf.st_size <= 0 || statBuf.st_size > static_cast<off_t>(Constants::CAPACITY_SIZE)) {
        LOG_E(BMS_TAG_INSTALLER, "cert size too large: %{public}lld", static_cast<long long>(statBuf.st_size));
        fclose(file);
        return false;
    }

    certData.resize(statBuf.st_size);
    if (lseek(fd, 0, SEEK_SET) == -1) {
        LOG_E(BMS_TAG_INSTALLD, "Lseek failed, errno: %{public}d", errno);
        fclose(file);
        return false;
    }
    ssize_t bytesRead = read(fd, certData.data(), static_cast<size_t>(statBuf.st_size));
    if (bytesRead != static_cast<ssize_t>(statBuf.st_size)) {
        LOG_E(BMS_TAG_INSTALLD, "read file failed, expected %{public}lld bytes, got %{public}lld",
            static_cast<long long>(statBuf.st_size), static_cast<long long>(bytesRead));
        fclose(file);
        return false;
    }
    fclose(file);
#else
    LOG_W(BMS_TAG_INSTALLD, "code signature feature is not supported");
#endif
    return true;
}

bool InstalldOperator::IsValidBundleName(const std::string &bundleName)
{
    if (bundleName.empty() || !IsFileNameValid(bundleName)) {
        LOG_NOFUNC_E(BMS_TAG_INSTALLD, "invalid name -n %{public}s", bundleName.c_str());
        return false;
    }
    // clone bundleName: +clone-<appIndex>+<bundleName>
    std::string tempBundleName = bundleName;
    if (bundleName.find(ServiceConstants::CLONE_PREFIX) == 0) {
        tempBundleName = bundleName.substr(strlen(ServiceConstants::CLONE_PREFIX));
        size_t plusPos = tempBundleName.find(ServiceConstants::PLUS_SIGN);
        if (plusPos == std::string::npos) {
            LOG_NOFUNC_E(BMS_TAG_INSTALLD, "invalid clone bundle name -n %{public}s", bundleName.c_str());
            return false;
        }
        int32_t appIndex = 0;
        if (!OHOS::StrToInt(tempBundleName.substr(0, plusPos), appIndex)) {
            LOG_NOFUNC_E(BMS_TAG_INSTALLD, "StrToInt failed -n %{public}s", bundleName.c_str());
            return false;
        }
        tempBundleName = tempBundleName.substr(plusPos + 1);
    }
    // sanbox bundleName: <appIndex>_<bundleName>
    auto pos = bundleName.find(Constants::FILE_UNDERLINE);
    if (pos != std::string::npos) {
        int32_t appIndex = 0;
        if (OHOS::StrToInt(bundleName.substr(0, pos), appIndex)) {
            tempBundleName = bundleName.substr(pos + 1);
        }
    }
    // for normal bundleName
    if (tempBundleName.size() < Constants::MIN_BUNDLE_NAME || tempBundleName.size() > Constants::MAX_BUNDLE_NAME) {
        LOG_NOFUNC_E(BMS_TAG_INSTALLD, "invalid name size -n %{public}s", tempBundleName.c_str());
        return false;
    }
    char head = tempBundleName.at(0);
    if (!isalpha(head)) {
        LOG_NOFUNC_E(BMS_TAG_INSTALLD, "invalid name -n %{public}s isalpha false", tempBundleName.c_str());
        return false;
    }
    for (const auto &c : tempBundleName) {
        if (!isalnum(static_cast<unsigned char>(c)) && (c != '.') && (c != '_')) {
            LOG_NOFUNC_E(BMS_TAG_INSTALLD, "invalid name -n %{public}s isalnum false", tempBundleName.c_str());
            return false;
        }
    }
    return true;
}

bool InstalldOperator::IsValidUserId(const int32_t userId)
{
    return userId >= 0;
}

bool InstalldOperator::IsValidUid(const int32_t uid)
{
    return uid >= 0;
}

bool InstalldOperator::IsValidAppIndex(const int32_t appIndex)
{
    return (appIndex >= Constants::MAIN_APP_INDEX) && (appIndex <= Constants::CLONE_APP_INDEX_MAX);
}

bool InstalldOperator::IsValidApl(const std::string &apl)
{
    return ALLOWED_APL.find(apl) != ALLOWED_APL.end();
}

bool InstalldOperator::IsValidPathByBundleDirScene(const BundleDirScene &scene, const std::string &path)
{
    if (!IsFileNameValid(path)) {
        LOG_NOFUNC_E(BMS_TAG_INSTALLD, "invalid path exist ../ or \\..");
        return false;
    }
    auto iter = ALLOWED_PATH_PREFIXES.find(scene);
    if (iter == ALLOWED_PATH_PREFIXES.end()) {
        LOG_NOFUNC_E(BMS_TAG_INSTALLD, "scene not exist in ALLOWED_PATH_PREFIXES");
        return false;
    }
    for (const auto &pre : iter->second) {
        if (path.find(pre) == 0) {
            return true;
        }
    }
    LOG_NOFUNC_E(BMS_TAG_INSTALLD, "path is invalid");
    return false;
}

bool InstalldOperator::IsValidUuid(const std::string &uuid)
{
    if (uuid.empty()) {
        return false;
    }
    return IsFileNameValid(uuid);
}

bool InstalldOperator::CheckDeviceMode(char *buf)
{
    bool status = false;
    char *onStr = strstr(buf, "oemmode=rd");
    char *offStr = strstr(buf, "oemmode=user");
    char *statusStr = strstr(buf, "oemmode=");
    if (onStr == nullptr && offStr == nullptr) {
        LOG_D(BMS_TAG_INSTALLD, "Not rd mode, cmdline = %{private}s", buf);
    } else if (offStr != nullptr && statusStr != nullptr && offStr != statusStr) {
        LOG_E(BMS_TAG_INSTALLD, "cmdline attacked, cmdline = %{private}s", buf);
    } else if (onStr != nullptr && offStr == nullptr) {
        status = true;
        LOG_D(BMS_TAG_INSTALLD, "Oemode is rd");
    }
    return status;
}

bool InstalldOperator::CheckEfuseStatus(char *buf)
{
    bool status = false;
    char *onStr = strstr(buf, "efuse_status=1");
    char *offStr = strstr(buf, "efuse_status=0");
    char *statusStr = strstr(buf, "efuse_status=");
    if (onStr == nullptr && offStr == nullptr) {
        LOG_D(BMS_TAG_INSTALLD, "device is efused, cmdline = %{private}s", buf);
    } else if (offStr != nullptr && statusStr != nullptr && offStr != statusStr) {
        LOG_E(BMS_TAG_INSTALLD, "cmdline attacked, cmdline = %{private}s", buf);
    } else if (onStr != nullptr && offStr == nullptr) {
        status = true;
        LOG_D(BMS_TAG_INSTALLD, "device is not efused");
    }
    return status;
}

bool InstalldOperator::IsRdDevice()
{
    int32_t fd = open(PROC_CMDLINE_FILE_PATH, O_RDONLY);
    if (fd < 0) {
        LOG_E(BMS_TAG_INSTALLD, "open %{public}s failed, %{public}s",
            PROC_CMDLINE_FILE_PATH, strerror(errno));
        return false;
    }
    std::vector<char> buf(CMDLINE_MAX_BUF_LEN, 0);
    ssize_t bufLen = read(fd, buf.data(), CMDLINE_MAX_BUF_LEN - 1);
    if (bufLen < 0) {
        LOG_E(BMS_TAG_INSTALLD, "Read %{public}s failed, %{public}s.",
            PROC_CMDLINE_FILE_PATH, strerror(errno));
        close(fd);
        return false;
    }
    close(fd);
    return CheckDeviceMode(buf.data()) || CheckEfuseStatus(buf.data());
}

ErrCode InstalldOperator::HapVerify(const std::string &filePath, Security::Verify::HapVerifyResult &hapVerifyResult)
{
    if (IsRdDevice()) {
        Security::Verify::SetRdDevice(true);
        Security::Verify::SetDevMode(Security::Verify::DevMode::DEV);
    }
    return Security::Verify::HapVerify(filePath, hapVerifyResult);
}

bool InstalldOperator::ParsePluginId(const std::string &appServiceCapabilities,
    std::vector<std::string> &pluginIds)
{
    if (appServiceCapabilities.empty()) {
        APP_LOGE("appServiceCapabilities is empty");
        return false;
    }
    auto appServiceCapabilityMap = BundleUtil::ParseMapFromJson(appServiceCapabilities);
    for (auto &item : appServiceCapabilityMap) {
        if (item.first == PERMISSION_KEY) {
            std::unordered_map<std::string, std::string> pluginIdMap = BundleUtil::ParseMapFromJson(item.second);
            auto it = pluginIdMap.find(PLUGIN_ID);
            if (it == pluginIdMap.end()) {
                APP_LOGE("pluginDistributionIDs not found in appServiceCapability");
                return false;
            }
            if (it->second.find(PLUGIN_ID_SEPARATOR_OTHER) != std::string::npos) {
                OHOS::SplitStr(it->second, PLUGIN_ID_SEPARATOR_OTHER, pluginIds);
            } else {
                OHOS::SplitStr(it->second, PLUGIN_ID_SEPARATOR, pluginIds);
            }
            return true;
        }
    }
    APP_LOGE("support plugin permission not found in appServiceCapability");
    return false;
}

bool InstalldOperator::ObtainSignInfoForPlugin(
    const std::string &filePath, std::string &appIdentifier, std::string &pluginId)
{
    Security::Verify::HapVerifyResult hapVerifyResult;
    ErrCode errCode = HapVerify(filePath, hapVerifyResult);
    if (errCode != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLD, "HapVerify failed, errCode: %{public}d", errCode);
        return false;
    }
    std::vector<std::string> pluginIds;
    if (!ParsePluginId(hapVerifyResult.GetProvisionInfo().appServiceCapabilities, pluginIds)) {
        APP_LOGE("parse plugin id failed");
        return false;
    }
    std::ostringstream oss;
    for (size_t i = 0; i < pluginIds.size(); ++i) {
        if (i != 0) {
            oss << std::string(PLUGIN_ID_SEPARATOR);
        }
        oss << pluginIds[i];
    }
    pluginId = oss.str();
    appIdentifier = hapVerifyResult.GetProvisionInfo().bundleInfo.appIdentifier;
    return true;
}
// adapter project: APK_RESOURCES_HAP
bool InstalldOperator::BuildApkResourcesHap(const std::string &apkPath,
    const std::string &outHapPath)
{
    LOG_I(BMS_TAG_INSTALLD, "BuildApkResourcesHap: apk=%{public}s out=%{public}s",
        apkPath.c_str(), outHapPath.c_str());
    if (apkPath.empty() || outHapPath.empty()) {
        LOG_E(BMS_TAG_INSTALLD, "BuildApkResourcesHap: empty path");
        return false;
    }
    // Cache handle across calls; installd is long-lived so no dlclose.
    static void *cached = nullptr;
    static bool tried = false;
    if (!tried) {
        tried = true;
        cached = dlopen("libapk_installer.so", RTLD_NOW | RTLD_GLOBAL);
        if (cached == nullptr) {
            LOG_E(BMS_TAG_INSTALLD, "BuildApkResourcesHap: dlopen libapk_installer.so failed: %{public}s",
                dlerror());
        }
    }
    if (cached == nullptr) {
        return false;
    }
    using BuildFn = int (*)(const char*, const char*);
    auto fn = reinterpret_cast<BuildFn>(dlsym(cached, "oh_adapter_build_resources_hap"));
    if (fn == nullptr) {
        LOG_E(BMS_TAG_INSTALLD, "BuildApkResourcesHap: symbol oh_adapter_build_resources_hap missing");
        return false;
    }
    int rc = fn(apkPath.c_str(), outHapPath.c_str());
    if (rc != 0) {
        LOG_E(BMS_TAG_INSTALLD, "BuildApkResourcesHap: synth returned %{public}d", rc);
        return false;
    }
    LOG_I(BMS_TAG_INSTALLD, "BuildApkResourcesHap: OK");
    return true;
}

}  // namespace AppExecFwk
}  // namespace OHOS
