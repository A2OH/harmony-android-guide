/*
 * Copyright (c) 2022-2025 Huawei Device Co., Ltd.
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

#include "bundle_file_util.h"

#include <cerrno>
#include <cinttypes>
#include <dirent.h>
#include <fcntl.h>
#include <string>
#include <sys/stat.h>
#include <sys/statfs.h>
#include <unistd.h>

#include "app_log_wrapper.h"
#include "directory_ex.h"
#include "string_ex.h"

namespace {
constexpr const char* INSTALL_FILE_SUFFIX = ".hap";
constexpr const char* HSP_FILE_SUFFIX = ".hsp";
constexpr const char* QUICK_FIX_FILE_SUFFIX = ".hqf";
constexpr const char* CODE_SIGNATURE_SUFFIX = ".sig";
constexpr const char* PGO_SUFFIX = ".ap";
constexpr const char* ABC_FILE_SUFFIX = ".abc";
constexpr const char* JSON_FILE_SUFFIX = ".json";
constexpr const char* APP_FILE_SUFFIX = ".app";
constexpr const char* PATH_SEPARATOR = "/";
constexpr int64_t ONE_GB = 1024 * 1024 * 1024;
constexpr int64_t MAX_HAP_SIZE = ONE_GB * 4;  // 4GB
constexpr int32_t PATH_MAX_SIZE = 4096;
const char FILE_SEPARATOR_CHAR = '/';
constexpr uint8_t MAX_HAP_NUMBER = 128;
constexpr double LOW_PARTITION_SPACE_THRESHOLD = 0.1;
} // namespace

namespace OHOS {
namespace AppExecFwk {
bool BundleFileUtil::CheckFilePath(const std::string &bundlePath, std::string &realPath)
{
    if (!CheckFileName(bundlePath)) {
        APP_LOGE("bundle file path invalid");
        return false;
    }
    if (!CheckFileType(bundlePath, INSTALL_FILE_SUFFIX) &&
        !CheckFileType(bundlePath, HSP_FILE_SUFFIX) &&
        !CheckFileType(bundlePath, QUICK_FIX_FILE_SUFFIX) &&
        !CheckFileType(bundlePath, ABC_FILE_SUFFIX) &&
        !CheckFileType(bundlePath, CODE_SIGNATURE_SUFFIX) &&
        !CheckFileType(bundlePath, PGO_SUFFIX) &&
        !CheckFileType(bundlePath, JSON_FILE_SUFFIX) &&
        !CheckFileType(bundlePath, APP_FILE_SUFFIX)
#ifdef OH_ADAPTER_ANDROID
        // Adapter project: allow Android .apk extension; downstream
        // BundleInstaller::Install dispatches .apk via libapk_installer.so.
        && !CheckFileType(bundlePath, ".apk")
#endif
        ) {
        APP_LOGE("file is not hap, hsp or hqf or sig or ap or json or app or apk");
        return false;
    }
    if (!PathToRealPath(bundlePath, realPath)) {
        APP_LOGE("file is not real path");
        return false;
    }
    if (access(realPath.c_str(), F_OK) != 0) {
        APP_LOGE("access failed path: %{public}s errno %{public}d", realPath.c_str(), errno);
        return false;
    }
    if (!CheckFileSize(realPath, MAX_HAP_SIZE)) {
        APP_LOGE("path is not a regular file or file size is too large");
        return false;
    }
    return true;
}

bool BundleFileUtil::CheckFilePath(const std::vector<std::string> &bundlePaths, std::vector<std::string> &realPaths)
{
    // there are three cases for bundlePaths:
    // 1. one bundle direction in the bundlePaths, some hap files under this bundle direction.
    // 2. one hap direction in the bundlePaths.
    // 3. some hap file directions in the bundlePaths.
    APP_LOGD("check file path");
    if (bundlePaths.empty()) {
        APP_LOGE("bundle file paths invalid");
        return false;
    }

    if (bundlePaths.size() == 1) {
        APP_LOGD("bundlePaths only has one element");
        const std::string& bundlePath = bundlePaths.front();
        std::string realPath = "";
        // it is a file
        if (CheckFilePath(bundlePaths.front(), realPath)) {
            APP_LOGD("path is a file");
            realPaths.emplace_back(realPath);
            return true;
        }
        if (!GetHapFilesFromBundlePath(bundlePath, realPaths)) {
            APP_LOGE("GetHapFilesFromBundlePath failed :%{public}s", bundlePaths.front().c_str());
            return false;
        }
        return true;
    }
    APP_LOGD("bundlePaths has more than one element");
    for (const std::string &bundlePath : bundlePaths) {
        std::string realPath = "";
        if (!CheckFilePath(bundlePath, realPath)) {
            return false;
        }
        realPaths.emplace_back(realPath);
    }
    APP_LOGD("finish check file path");
    return true;
}

bool BundleFileUtil::CheckFileType(const std::string &fileName, const std::string &extensionName)
{
    APP_LOGD("path is %{public}s, support suffix is %{public}s", fileName.c_str(), extensionName.c_str());
    if (!CheckFileName(fileName)) {
        return false;
    }

    auto position = fileName.rfind('.');
    if (position == std::string::npos) {
        APP_LOGE("filename no extension name");
        return false;
    }

    std::string suffixStr = fileName.substr(position);
    return LowerStr(suffixStr) == extensionName;
}

bool BundleFileUtil::CheckFileName(const std::string &fileName)
{
    if (fileName.empty()) {
        APP_LOGE("the file name is empty");
        return false;
    }
    if (fileName.size() > PATH_MAX_SIZE) {
        APP_LOGE("path length %{public}zu too long", fileName.size());
        return false;
    }
    return true;
}

bool BundleFileUtil::CheckFileSize(const std::string &bundlePath, const int64_t fileSize)
{
    struct stat fileInfo = { 0 };
    if (stat(bundlePath.c_str(), &fileInfo) != 0) {
        APP_LOGE("call stat error:%{public}d %{public}s", errno, bundlePath.c_str());
        return false;
    }
    if (!S_ISREG(fileInfo.st_mode)) {
        APP_LOGE("path is not a regular file: %{public}s", bundlePath.c_str());
        return false;
    }
    if (fileInfo.st_size > fileSize) {
        return false;
    }
    return true;
}

bool BundleFileUtil::GetHapFilesFromBundlePath(const std::string &currentBundlePath,
    std::vector<std::string> &hapFileList)
{
    APP_LOGD("GetHapFilesFromBundlePath with path is %{public}s", currentBundlePath.c_str());
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
    std::string bundlePath = currentBundlePath;
    if (bundlePath.back() != FILE_SEPARATOR_CHAR) {
        bundlePath.append(PATH_SEPARATOR);
    }
    struct dirent *entry = nullptr;
    while ((entry = readdir(dir)) != nullptr) {
        if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0) {
            continue;
        }
        const std::string hapFilePath = bundlePath + entry->d_name;
        std::string realPath = "";
        if (!CheckFilePath(hapFilePath, realPath)) {
            APP_LOGE("invalid hap path %{public}s", hapFilePath.c_str());
            closedir(dir);
            return false;
        }
        hapFileList.emplace_back(realPath);
        APP_LOGD("find hap path %{public}s", realPath.c_str());

        if (hapFileList.size() > MAX_HAP_NUMBER) {
            APP_LOGE("max hap number %{public}hhu, stop add", MAX_HAP_NUMBER);
            closedir(dir);
            return false;
        }
    }
    closedir(dir);
    return true;
}

bool BundleFileUtil::DeleteDir(const std::string &path)
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
        res = OHOS::ForceRemoveDirectoryBMS(path);
        if (!res && errno == ENOENT) {
            return true;
        }
        return res;
    }
    return true;
}

bool BundleFileUtil::IsExistFile(const std::string &filePath)
{
    if (filePath.empty()) {
        return false;
    }

    struct stat result = {};
    if (stat(filePath.c_str(), &result) != 0) {
        APP_LOGE("fail stat errno:%{public}d", errno);
        return false;
    }

    return S_ISREG(result.st_mode);
}

bool BundleFileUtil::IsExistDir(const std::string &dirPath)
{
    if (dirPath.empty()) {
        return false;
    }

    struct stat result = {};
    if (stat(dirPath.c_str(), &result) != 0) {
        APP_LOGE("fail stat errno %{public}d", errno);
        return false;
    }

    return S_ISDIR(result.st_mode);
}

bool BundleFileUtil::IsReportDataPartitionUsageEvent(const std::string &path)
{
    if (!IsExistDir(path)) {
        APP_LOGD("the path %{public}s does not exist", path.c_str());
        return false;
    }

    struct statvfs stst;
    if (statvfs(path.c_str(), &stst) != 0) {
        APP_LOGE("failed to get space info for path %{public}s",
            path.c_str());
        return false;
    }

    uint64_t remainPartitionTotalitySize = static_cast<uint64_t>(stst.f_blocks) * stst.f_frsize;
    uint64_t remainPartitionFreeSize = static_cast<uint64_t>(stst.f_bavail) * stst.f_frsize;

    if (remainPartitionTotalitySize == 0) {
        APP_LOGE("the partition %{public}s totality size is zero", path.c_str());
        return false;
    }

    double ratio = static_cast<double>(remainPartitionFreeSize) / remainPartitionTotalitySize;
    if (ratio < LOW_PARTITION_SPACE_THRESHOLD) {
        APP_LOGW("start hisysevent and partitionsize is smaller then 10%%");
        return true;
    }
    return false;
}
} // AppExecFwk
} // OHOS