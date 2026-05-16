/*
 * Copyright (c) 2025 Huawei Device Co., Ltd.
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

#include "bundle_stream_installer_host_impl.h"
#ifdef OH_ADAPTER_ANDROID
#include <dlfcn.h>  // adapter project: libapk_installer.so dispatch in InstallApp
#endif

#include <dirent.h>
#include "bundle_file_util.h"
#include "bundle_hitrace_chain.h"
#include "bundle_mgr_service.h"
#include "bundle_parser.h"
#include "bundle_permission_mgr.h"
#include "contrib/minizip/unzip.h"
#include "ipc_skeleton.h"
#include "scope_guard.h"
namespace OHOS {
namespace AppExecFwk {
namespace {
constexpr const char* ILLEGAL_PATH_FIELD = "../";
constexpr const char* PATH_SEPARATOR = "/";
constexpr const char FILE_SEPARATOR_CHAR = '/';
constexpr const char PACK_INFO[] = "pack.info";
constexpr const int32_t ZIP_MAX_PATH = 256;
constexpr const int32_t ZIP_BUF_SIZE = 8192;
}

BundleStreamInstallerHostImpl::BundleStreamInstallerHostImpl(uint32_t installerId, int32_t installedUid)
{
    APP_LOGD("create bundle stream installer host impl instance");
    installerId_ = installerId;
    installedUid_ = installedUid;
}

BundleStreamInstallerHostImpl::~BundleStreamInstallerHostImpl()
{
    APP_LOGD("destory bundle stream installer host impl instance");
    UnInit();
}

bool BundleStreamInstallerHostImpl::Init(const InstallParam &installParam,
    const sptr<IStatusReceiver> &statusReceiver, const std::vector<std::string> &originHapPaths)
{
    installParam_ = installParam;
    receiver_ = statusReceiver;
    originHapPaths_ = originHapPaths;
    std::string tempDir = BundleUtil::CreateInstallTempDir(installerId_, DirType::STREAM_INSTALL_DIR);
    if (tempDir.empty()) {
        APP_LOGE("tempDir is empty");
        return false;
    }
    tempDir_ = tempDir;

    installParam_.sharedBundleDirPaths.clear();
    uint32_t size = static_cast<uint32_t>(installParam.sharedBundleDirPaths.size());
    for (uint32_t i = 0; i < size; ++i) {
        tempDir = BundleUtil::CreateSharedBundleTempDir(installerId_, i);
        if (tempDir.empty()) {
            APP_LOGE("create temp dir for hsp failed");
            return false;
        }
        installParam_.sharedBundleDirPaths.emplace_back(tempDir);
    }

    installParam_.pgoParams.clear();

    tempSignatureFileDir_ = BundleUtil::CreateInstallTempDir(installerId_, DirType::SIG_FILE_DIR);
    if (tempSignatureFileDir_.empty()) {
        APP_LOGE("tempSignatureFileDir_ is empty");
        return false;
    }

    tempPgoFileDir_ = BundleUtil::CreateInstallTempDir(installerId_, DirType::PGO_FILE_DIR);
    if (tempPgoFileDir_.empty()) {
        APP_LOGE("tempPgoFileDir_ is empty");
        return false;
    }

    auto iter = installParam.parameters.find(ServiceConstants::ENTERPRISE_MANIFEST);
    if ((iter != installParam.parameters.end()) && !(iter->second.empty())) {
        installParam_.parameters[ServiceConstants::ENTERPRISE_MANIFEST] = "";
        tempExtProfileDir_ = BundleUtil::CreateInstallTempDir(installerId_, DirType::EXT_PROFILE_DIR);
        if (tempExtProfileDir_.empty()) {
            APP_LOGW("tempExtProfileDir_ is empty");
        }
    }
    
    return true;
}

void BundleStreamInstallerHostImpl::UnInit()
{
    APP_LOGD("destory stream installer with installerId %{public}d and temp dir %{public}s", installerId_,
        tempDir_.c_str());
    std::lock_guard<std::mutex> lock(fdVecMutex_);
    BundleUtil::CloseFileDescriptor(streamFdVec_);
    BundleUtil::DeleteDir(tempDir_);
    for (const auto &path : installParam_.sharedBundleDirPaths) {
        BundleUtil::DeleteDir(path);
    }
    BundleUtil::DeleteDir(tempSignatureFileDir_);
    BundleUtil::DeleteDir(tempPgoFileDir_);
    BundleUtil::DeleteDir(tempExtProfileDir_);
}

int32_t BundleStreamInstallerHostImpl::CreateStream(const std::string &fileName)
{
    if (fileName.empty()) {
        APP_LOGE("CreateStream param fileName is empty");
        return Constants::DEFAULT_STREAM_FD;
    }

    if (!BundlePermissionMgr::VerifyCallingPermissionForAll(Constants::PERMISSION_INSTALL_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(ServiceConstants::PERMISSION_INSTALL_ENTERPRISE_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(
            ServiceConstants::PERMISSION_INSTALL_ENTERPRISE_NORMAL_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(
            ServiceConstants::PERMISSION_INSTALL_ENTERPRISE_MDM_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(
            ServiceConstants::PERMISSION_INSTALL_INTERNALTESTING_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(ServiceConstants::PERMISSION_INSTALL_SELF_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(ServiceConstants::PERMISSION_INSTALL_SANDBOX_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(ServiceConstants::PERMISSION_INSTALL_QUICK_FIX_BUNDLE)) {
        APP_LOGE("CreateStream permission denied");
        return Constants::DEFAULT_STREAM_FD;
    }

    int32_t callingUid = IPCSkeleton::GetCallingUid();
    if (callingUid != installedUid_) {
        APP_LOGE("calling uid is inconsistent");
        return Constants::DEFAULT_STREAM_FD;
    }

    if (!BundleUtil::CheckFileType(fileName, ServiceConstants::INSTALL_FILE_SUFFIX) &&
        !BundleUtil::CheckFileType(fileName, ServiceConstants::HSP_FILE_SUFFIX) &&
        !BundleUtil::CheckFileType(fileName, ServiceConstants::APP_FILE_SUFFIX)
#ifdef OH_ADAPTER_ANDROID
        // adapter project: allow .apk extension
        && !BundleUtil::CheckFileType(fileName, ".apk")
#endif
        ) {
        APP_LOGE("file is not hap or hsp or app or apk");
        return Constants::DEFAULT_STREAM_FD;
    }
    // to prevent the hap copied to relevant path
    if (fileName.find(ILLEGAL_PATH_FIELD) != std::string::npos) {
        APP_LOGE("CreateStream failed due to invalid fileName");
        return Constants::DEFAULT_STREAM_FD;
    }
    std::string filePath = tempDir_ + fileName;
    int32_t fd = BundleUtil::CreateFileDescriptor(filePath, 0);
    if (fd < 0) {
        APP_LOGE("stream installer create file descriptor failed");
    }
    if (fd > 0) {
        std::lock_guard<std::mutex> lock(fdVecMutex_);
        streamFdVec_.emplace_back(fd);
        isInstallSharedBundlesOnly_ = false;
    }
    return fd;
}

int32_t BundleStreamInstallerHostImpl::CreateSignatureFileStream(const std::string &moduleName,
    const std::string &fileName)
{
    if (moduleName.empty() || fileName.empty()) {
        APP_LOGE("CreateSignatureFileStream params are invalid");
        return Constants::DEFAULT_STREAM_FD;
    }

    if (!BundlePermissionMgr::VerifyCallingPermissionForAll(Constants::PERMISSION_INSTALL_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(ServiceConstants::PERMISSION_INSTALL_ENTERPRISE_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(
            ServiceConstants::PERMISSION_INSTALL_ENTERPRISE_NORMAL_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(
            ServiceConstants::PERMISSION_INSTALL_ENTERPRISE_MDM_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(
            ServiceConstants::PERMISSION_INSTALL_INTERNALTESTING_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(ServiceConstants::PERMISSION_INSTALL_SELF_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(ServiceConstants::PERMISSION_INSTALL_SANDBOX_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(ServiceConstants::PERMISSION_INSTALL_QUICK_FIX_BUNDLE)) {
        APP_LOGE("CreateSignatureFileStream permission denied");
        return Constants::DEFAULT_STREAM_FD;
    }

    int32_t callingUid = IPCSkeleton::GetCallingUid();
    if (callingUid != installedUid_) {
        APP_LOGE("calling uid is inconsistent");
        return Constants::DEFAULT_STREAM_FD;
    }

    if (!BundleUtil::CheckFileType(fileName, ServiceConstants::CODE_SIGNATURE_FILE_SUFFIX)) {
        APP_LOGE("file is not sig");
        return Constants::DEFAULT_STREAM_FD;
    }

    auto iterator = installParam_.verifyCodeParams.find(moduleName);
    if (iterator == installParam_.verifyCodeParams.end()) {
        APP_LOGE("module name cannot be found");
        return Constants::DEFAULT_STREAM_FD;
    }

    // to prevent the sig copied to relevant path
    if (fileName.find(ILLEGAL_PATH_FIELD) != std::string::npos) {
        APP_LOGE("CreateStream failed due to invalid fileName");
        return Constants::DEFAULT_STREAM_FD;
    }
    std::string filePath = tempSignatureFileDir_ + fileName;
    int32_t fd = BundleUtil::CreateFileDescriptor(filePath, 0);
    if (fd < 0) {
        APP_LOGE("stream installer create file descriptor failed");
    }

    if (fd > 0) {
        std::lock_guard<std::mutex> lock(fdVecMutex_);
        streamFdVec_.emplace_back(fd);
        installParam_.verifyCodeParams.at(moduleName) = filePath;
    }
    return fd;
}

int32_t BundleStreamInstallerHostImpl::CreateSharedBundleStream(const std::string &hspName, uint32_t index)
{
    if (!BundlePermissionMgr::VerifyCallingPermissionForAll(Constants::PERMISSION_INSTALL_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(ServiceConstants::PERMISSION_INSTALL_ENTERPRISE_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(
            ServiceConstants::PERMISSION_INSTALL_ENTERPRISE_NORMAL_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(
            ServiceConstants::PERMISSION_INSTALL_ENTERPRISE_MDM_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(
            ServiceConstants::PERMISSION_INSTALL_INTERNALTESTING_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(ServiceConstants::PERMISSION_INSTALL_SELF_BUNDLE)) {
        APP_LOGE("CreateSharedBundleStream permission denied");
        return Constants::DEFAULT_STREAM_FD;
    }

    int32_t callingUid = IPCSkeleton::GetCallingUid();
    if (callingUid != installedUid_) {
        APP_LOGE("calling uid is inconsistent");
        return Constants::DEFAULT_STREAM_FD;
    }

    if (!BundleUtil::CheckFileType(hspName, ServiceConstants::INSTALL_FILE_SUFFIX) &&
        !BundleUtil::CheckFileType(hspName, ServiceConstants::HSP_FILE_SUFFIX) &&
        !BundleUtil::CheckFileType(hspName, ServiceConstants::CODE_SIGNATURE_FILE_SUFFIX)
#ifdef OH_ADAPTER_ANDROID
        // adapter project: allow .apk extension (covers "two-stream" install path used by some bm flows)
        && !BundleUtil::CheckFileType(hspName, ".apk")
#endif
        ) {
        APP_LOGE("file is not hap or hsp or apk");
        return Constants::DEFAULT_STREAM_FD;
    }

    // to prevent the hsp copied to relevant path
    if (hspName.find(ILLEGAL_PATH_FIELD) != std::string::npos) {
        APP_LOGE("CreateSharedBundleStream failed due to invalid hapName");
        return Constants::DEFAULT_STREAM_FD;
    }

    if (index >= installParam_.sharedBundleDirPaths.size()) {
        APP_LOGE("invalid shared bundle index");
        return Constants::DEFAULT_STREAM_FD;
    }

    std::string bundlePath = installParam_.sharedBundleDirPaths[index] + hspName;
    int32_t fd = BundleUtil::CreateFileDescriptor(bundlePath, 0);
    if (fd < 0) {
        APP_LOGE("stream installer create file descriptor failed");
    }
    if (fd > 0) {
        std::lock_guard<std::mutex> lock(fdVecMutex_);
        streamFdVec_.emplace_back(fd);
    }
    return fd;
}

int32_t BundleStreamInstallerHostImpl::CreatePgoFileStream(const std::string &moduleName,
    const std::string &fileName)
{
    if (moduleName.empty() || fileName.empty()) {
        APP_LOGE("CreatePgoFileStream params are invalid");
        return Constants::DEFAULT_STREAM_FD;
    }

    if (!BundlePermissionMgr::VerifyCallingPermissionForAll(Constants::PERMISSION_INSTALL_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(ServiceConstants::PERMISSION_INSTALL_ENTERPRISE_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(
            ServiceConstants::PERMISSION_INSTALL_ENTERPRISE_NORMAL_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(
            ServiceConstants::PERMISSION_INSTALL_ENTERPRISE_MDM_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(
            ServiceConstants::PERMISSION_INSTALL_INTERNALTESTING_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(ServiceConstants::PERMISSION_INSTALL_SELF_BUNDLE)) {
        APP_LOGE("CreatePgoFileStream permission denied");
        return Constants::DEFAULT_STREAM_FD;
    }

    int32_t callingUid = IPCSkeleton::GetCallingUid();
    if (callingUid != installedUid_) {
        APP_LOGE("calling uid is inconsistent");
        return Constants::DEFAULT_STREAM_FD;
    }

    if (!BundleUtil::CheckFileType(fileName, ServiceConstants::PGO_FILE_SUFFIX)) {
        APP_LOGE("file is not pgo");
        return Constants::DEFAULT_STREAM_FD;
    }

    // to prevent the pgo copied to relevant path
    if (fileName.find(ILLEGAL_PATH_FIELD) != std::string::npos) {
        APP_LOGE("CreateStream failed due to invalid fileName");
        return Constants::DEFAULT_STREAM_FD;
    }
    std::string filePath = tempPgoFileDir_ + fileName;
    int32_t fd = BundleUtil::CreateFileDescriptor(filePath, 0);
    if (fd < 0) {
        APP_LOGE("stream installer create file descriptor failed");
    }

    if (fd > 0) {
        std::lock_guard<std::mutex> lock(fdVecMutex_);
        streamFdVec_.emplace_back(fd);
        installParam_.pgoParams[moduleName] = filePath;
    }
    return fd;
}

int32_t BundleStreamInstallerHostImpl::CreateExtProfileFileStream(const std::string &fileName)
{
    if (fileName.empty()) {
        APP_LOGE("CreateExtProfileFileStream param is invalid");
        return Constants::DEFAULT_STREAM_FD;
    }

    if (!BundlePermissionMgr::VerifyCallingPermissionForAll(ServiceConstants::PERMISSION_INSTALL_ENTERPRISE_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(
            ServiceConstants::PERMISSION_INSTALL_ENTERPRISE_NORMAL_BUNDLE) &&
        !BundlePermissionMgr::VerifyCallingPermissionForAll(
            ServiceConstants::PERMISSION_INSTALL_ENTERPRISE_MDM_BUNDLE)) {
        APP_LOGE("CreateExtProfileFileStream permission denied");
        return Constants::DEFAULT_STREAM_FD;
    }

    int32_t callingUid = IPCSkeleton::GetCallingUid();
    if (callingUid != installedUid_) {
        APP_LOGE("calling uid is inconsistent");
        return Constants::DEFAULT_STREAM_FD;
    }

    if (!BundleUtil::CheckFileType(fileName, ServiceConstants::JSON_SUFFIX)) {
        APP_LOGE("file is not json");
        return Constants::DEFAULT_STREAM_FD;
    }

    // to prevent the sig copied to relevant path
    if (fileName.find(ILLEGAL_PATH_FIELD) != std::string::npos) {
        APP_LOGE("CreateStream failed due to invalid fileName");
        return Constants::DEFAULT_STREAM_FD;
    }
    std::string filePath = tempExtProfileDir_ + fileName;
    int32_t fd = BundleUtil::CreateFileDescriptor(filePath, 0);
    if (fd < 0) {
        APP_LOGE("stream installer create file descriptor failed");
    } else {
        std::lock_guard<std::mutex> lock(fdVecMutex_);
        streamFdVec_.emplace_back(fd);
        installParam_.parameters[ServiceConstants::ENTERPRISE_MANIFEST] = filePath;
    }
    return fd;
}

static bool GetAppFilesFromBundlePath(const std::string &currentBundlePath, std::vector<std::string> &appFileList)
{
    if (currentBundlePath.empty()) {
        return false;
    }

    DIR* dir = opendir(currentBundlePath.c_str());
    if (dir == nullptr) {
        char errMsg[256] = {0};
        strerror_r(errno, errMsg, sizeof(errMsg));
        APP_LOGE("open %{public}s failure due to %{public}s errno %{public}d",
            currentBundlePath.c_str(), errMsg, errno);
        return false;
    }

    ScopeGuard dirGuard([&dir]() {
        closedir(dir);
    });

    std::string bundlePath = currentBundlePath;
    if (bundlePath.back() != FILE_SEPARATOR_CHAR) {
        bundlePath.append(PATH_SEPARATOR);
    }

    bool ret = true;
    struct dirent *entry = nullptr;
    while ((entry = readdir(dir)) != nullptr) {
        if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0) {
            continue;
        }
        const std::string hapFilePath = bundlePath + entry->d_name;
        if (!BundleFileUtil::CheckFileType(hapFilePath, ServiceConstants::APP_FILE_SUFFIX)) {
            ret = false;
            continue;
        }

        if (std::find(appFileList.begin(), appFileList.end(), hapFilePath) == appFileList.end()) {
            appFileList.emplace_back(hapFilePath);
        }
    }
    return ret;
}

static bool ExtractFileFromZip(const unzFile &zipFile, const std::string &outFilePath,
    const char* filename, std::vector<std::string> &filePaths)
{
    std::string fullPath = outFilePath + "/" + std::string(filename);
    if (strcmp(filename, PACK_INFO) == 0) {
        return true;
    }
    filePaths.emplace_back(fullPath);

    if (unzOpenCurrentFile(zipFile) != UNZ_OK) {
        APP_LOGE("Failed to open file in zip: %{public}s", filename);
        return false;
    }

    ScopeGuard zipGuard([&zipFile]() {
        unzCloseCurrentFile(zipFile);
    });

    FILE *outFile = fopen(fullPath.c_str(), "wb");
    if (!outFile) {
        APP_LOGE("Failed to create output file: %{public}s", fullPath.c_str());
        unzCloseCurrentFile(zipFile);
        return false;
    }

    ScopeGuard fileGuard([&outFile]() {
        if (fclose(outFile) != 0) {
            APP_LOGE("Failed to close file");
        }
    });

    std::string buffer;
    buffer.reserve(ZIP_BUF_SIZE);
    buffer.resize(ZIP_BUF_SIZE - 1);
    size_t bytesRead;
    while ((bytesRead = unzReadCurrentFile(zipFile, &(buffer[0]), ZIP_BUF_SIZE)) > 0) {
        if (fwrite(&(buffer[0]), 1, bytesRead, outFile) != bytesRead) {
            APP_LOGE("Failed to write file");
            return false;
        }
    }
    return true;
}

static bool DecompressToFile(const std::string &zipFilePath, const std::string &outFilePath,
    std::vector<std::string> &filePaths)
{
    mode_t rootMode = 0777;
    struct stat st;
    if (stat(outFilePath.c_str(), &st) != 0) {
        if (mkdir(outFilePath.c_str(), rootMode) != 0) {
            APP_LOGE("Failed to create directory: %{public}s", outFilePath.c_str());
            return false;
        }
    }

    unzFile zipFile = unzOpen(zipFilePath.c_str());
    if (!zipFile) {
        APP_LOGE("Failed to open zip file: %{public}s", zipFilePath.c_str());
        return false;
    }

    ScopeGuard zipGuard([&zipFile]() {
        unzClose(zipFile);
    });

    unz_global_info globalInfo = {};
    if (unzGetGlobalInfo(zipFile, &globalInfo) != UNZ_OK) {
        APP_LOGE("Failed to get global info from zip file: %{public}s", zipFilePath.c_str());
        unzClose(zipFile);
        return false;
    }

    bool result = true;
    for (uLong i = 0; i < globalInfo.number_entry; i++) {
        char filename[ZIP_MAX_PATH] = {};
        unz_file_info fileInfo = {};
        if (unzGetCurrentFileInfo(zipFile, &fileInfo, filename, sizeof(filename) - 1, nullptr, 0, nullptr, 0) !=
            UNZ_OK) {
            APP_LOGE("Failed to get file info");
            result = false;
            break;
        }

        if (!ExtractFileFromZip(zipFile, outFilePath, filename, filePaths)) {
            result = false;
            break;
        }

        if ((i + 1) < globalInfo.number_entry) {
            if (unzGoToNextFile(zipFile) != UNZ_OK) {
                APP_LOGE("Failed to go to next file");
                result = false;
                break;
            }
        }
    }
    return result;
}

static bool GetInnerBundleInfo(const std::string &hapFilePath, std::unordered_map<std::string, InnerBundleInfo> &infos)
{
    if (hapFilePath.find(ServiceConstants::RELATIVE_PATH) != std::string::npos) {
        APP_LOGE("invalid hapFilePath");
        return false;
    }
    std::string realPath;
    auto ret = BundleUtil::CheckFilePath(hapFilePath, realPath);
    if (ret != ERR_OK) {
        APP_LOGE("getInnerBundleInfo file path %{private}s invalid", hapFilePath.c_str());
        return false;
    }

    InnerBundleInfo innerBundleInfo;
    BundleParser bundleParser;
    bool isAbcCompressed = false;
    ret = bundleParser.Parse(realPath, innerBundleInfo, isAbcCompressed);
    if (ret != ERR_OK) {
        APP_LOGE("parse bundle info failed, error: %{public}d", ret);
        return false;
    }
    infos.emplace(hapFilePath, innerBundleInfo);
    return true;
}

static bool SelectApp(const std::vector<std::string> &filePaths)
{
    bool ret = true;
    for (const auto &path : filePaths) {
        auto bundleInstallChecker = std::make_unique<BundleInstallChecker>();
        if (bundleInstallChecker == nullptr) {
            APP_LOGW("bundleInstallChecker is null");
            continue;
        }

        std::unordered_map<std::string, InnerBundleInfo> infos;
        if (!GetInnerBundleInfo(path, infos)
            || bundleInstallChecker->CheckDeviceType(infos, ERR_OK) != OHOS::ERR_OK) {
            std::error_code ec;
            if (!std::filesystem::remove(path, ec) || ec) {
                APP_LOGE("remove file failed.");
            }
            continue;
        }
        ret = false;
    }
    return ret;
}

bool BundleStreamInstallerHostImpl::InstallApp(const std::vector<std::string> &pathVec)
{
    std::vector<std::string> appPaths;
    for (const auto &path : pathVec) {
        if ((!GetAppFilesFromBundlePath(path, appPaths) && !appPaths.empty()) || appPaths.size() > 1) {
            receiver_->OnFinished(ERR_APPEXECFWK_INSTALL_MORE_THAN_ONE_APP, "");
            return false;
        }
    }
    if (appPaths.empty()) {
        return true;
    }
    if (!installParam_.sharedBundleDirPaths.empty()) {
        receiver_->OnFinished(ERR_APPEXECFWK_INSTALL_MORE_THAN_ONE_APP, "");
        return false;
    }
    if (!installParam_.isCallByShell) {
        receiver_->OnFinished(ERR_APPEXECFWK_INSTALL_FILE_PATH_INVALID, "");
        return false;
    }

#ifdef OH_ADAPTER_ANDROID
    // adapter project: dispatch .apk batches via libapk_installer.so before
    // OH HAP signature verification (which always rejects .apk).
    {
        bool anyApk = false;
        for (const auto &p : appPaths) {
            if (p.size() > 4 && p.compare(p.size() - 4, 4, ".apk") == 0) {
                anyApk = true;
                break;
            }
        }
        if (anyApk) {
            void* handle = dlopen("libapk_installer.so", RTLD_NOW | RTLD_GLOBAL);
            using ApkFn = int (*)(const char*, int);
            ApkFn fn = handle ? reinterpret_cast<ApkFn>(
                dlsym(handle, "oh_adapter_install_apk")) : nullptr;
            int rc = 0;
            if (fn == nullptr) {
                APP_LOGE("oh_adapter_install_apk symbol unavailable");
                rc = -1001;
            } else {
                for (const auto &p : appPaths) {
                    if (p.size() > 4 && p.compare(p.size() - 4, 4, ".apk") == 0) {
                        int r = fn(p.c_str(), installParam_.userId);
                        if (r != 0) { rc = r; break; }
                    }
                }
            }
            // Best-effort cleanup of staged .apk files.
            for (const auto &p : appPaths) {
                std::error_code ec;
                std::filesystem::remove(p, ec);
            }
            receiver_->OnFinished(
                rc == 0 ? ERR_OK : ERR_APPEXECFWK_INSTALL_FAILED_INVALID_SIGNATURE_FILE_PATH,
                rc == 0 ? "APK install success" : "APK install failed");
            return false;  // short-circuit OH HAP pipeline
        }
    }
#endif

    auto bundleInstallChecker = std::make_unique<BundleInstallChecker>();
    std::vector<Security::Verify::HapVerifyResult> hapVerifyResults;
    if (bundleInstallChecker == nullptr ||
        bundleInstallChecker->CheckMultipleHapsSignInfo(appPaths, hapVerifyResults) != OHOS::ERR_OK) {
        receiver_->OnFinished(ERR_APPEXECFWK_INSTALL_VERIFY_APP_SIGNATURE_FAILED, "");
        return false;
    }

    std::vector<std::string> filePaths;
    for (const auto &appPath : appPaths) {
        if (!DecompressToFile(appPath, pathVec.front(), filePaths)) {
            receiver_->OnFinished(ERR_APPEXECFWK_INSTALL_DECOMPRESS_APP_FAILED, "");
            return false;
        }
        std::error_code ec;
        if (!std::filesystem::remove(appPath, ec) || ec) {
            APP_LOGW("remove file failed.");
        }
    }

    if (SelectApp(filePaths)) {
        receiver_->OnFinished(ERR_APPEXECFWK_INSTALL_NO_SUITABLE_BUNDLES, "");
        return false;
    }
    return true;
}

bool BundleStreamInstallerHostImpl::Install()
{
    BUNDLE_MANAGER_HITRACE_CHAIN_NAME("Install", HITRACE_FLAG_INCLUDE_ASYNC);
    if (receiver_ == nullptr) {
        APP_LOGE("receiver_ is nullptr");
        return false;
    }
    receiver_->SetStreamInstallId(installerId_);
    auto installer = DelayedSingleton<BundleMgrService>::GetInstance()->GetBundleInstaller();
    if (installer == nullptr) {
        APP_LOGE("get bundle installer failed");
        receiver_->OnFinished(ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR, "");
        return false;
    }
    std::vector<std::string> pathVec;
    if (installParam_.IsRenameInstall()) {
        pathVec = originHapPaths_;
    } else if (!isInstallSharedBundlesOnly_) {
        pathVec.emplace_back(tempDir_);
    }
    installParam_.withCopyHaps = true;

    if (!InstallApp(pathVec)) {
        APP_LOGE("install app failed");
        return false;
    }

    bool res;
    if (installParam_.isSelfUpdate) {
        installParam_.parameters.erase(ServiceConstants::BMS_PARA_INSTALL_ALLOW_DOWNGRADE);
        res = installer->UpdateBundleForSelf(pathVec, installParam_, receiver_);
    } else {
        res = installer->Install(pathVec, installParam_, receiver_);
    }
    if (!res) {
        APP_LOGE("install bundle failed");
        return false;
    }
    return true;
}

uint32_t BundleStreamInstallerHostImpl::GetInstallerId() const
{
    return installerId_;
}

void BundleStreamInstallerHostImpl::SetInstallerId(uint32_t installerId)
{
    installerId_ = installerId;
}
} // AppExecFwk
} // OHOS