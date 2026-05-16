/*
 * Copyright (c) 2021-2023 Huawei Device Co., Ltd.
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

#include "bundle_installer.h"

#include <cinttypes>
#include <fstream>
#include <sys/statfs.h>
#include <sstream>

#include "app_log_tag_wrapper.h"
#include "bundle_mgr_service.h"
#include "parameter.h"
#include "parameters.h"

namespace OHOS {
namespace AppExecFwk {
#ifdef OH_ADAPTER_ANDROID
// Route C (feedback.txt 2026-04-17): APK parsing lives in the adapter-owned
// libapk_installer.so, NOT compiled into libbms. libbms only routes .apk
// install requests via runtime dlopen — this keeps libbms free of AOSP-chain
// DT_NEEDED (libandroidfw + its transitive libbase/libutils/libcutils/liblog/
// libicuuc) so that BMS SA can start in OH foundation without /system/android/
// lib being on the default namespace linker path.
#include <dlfcn.h>

namespace {
using AdapterApkInstallFn = int (*)(const char* apkPath, int userId);

AdapterApkInstallFn LoadAdapterApkInstaller() {
    static void* handle = dlopen("libapk_installer.so", RTLD_NOW | RTLD_GLOBAL);
    if (handle == nullptr) {
        return nullptr;
    }
    // Adapter-owned C ABI entry. libapk_installer.so exports this as
    // extern "C" to keep the bridge name-mangle-stable across toolchains.
    // Signature: int oh_adapter_install_apk(const char* apkPath, int userId);
    return reinterpret_cast<AdapterApkInstallFn>(
        dlsym(handle, "oh_adapter_install_apk"));
}

constexpr int kErrAdapterNotAvailable = -1001;
}  // namespace
#endif
namespace {
constexpr const char* USER_DATA_DIR = "/data";
constexpr double MIN_FREE_INODE_PERCENT = 0.005; // 0.5%

bool CheckSystemInodeSatisfied(const std::string &bundleName)
{
    std::string appGalleryName = OHOS::system::GetParameter(ServiceConstants::CLOUD_SHADER_OWNER, "");
    if (appGalleryName.empty() || appGalleryName != bundleName) {
        return true;
    }
    struct statfs stat;
    if (statfs(USER_DATA_DIR, &stat) != 0) {
        LOG_E(BMS_TAG_INSTALLER, "statfs failed for %{public}s, error %{public}d",
            USER_DATA_DIR, errno);
        return false;
    }
    uint32_t minFreeInodeNum = static_cast<uint32_t>(stat.f_files * MIN_FREE_INODE_PERCENT);
    if (stat.f_ffree < minFreeInodeNum) {
        LOG_E(BMS_TAG_INSTALLER, "free inodes not satisfied");
        return false;
    }
    LOG_D(BMS_TAG_INSTALLER, "total inodes: %{public}llu, free inodes: %{public}llu",
        stat.f_files, stat.f_ffree);
    return BundleUtil::CheckOrphanNodeUseRateIsSufficient();
}
} // namespace
BundleInstaller::BundleInstaller(const int64_t installerId, const sptr<IStatusReceiver> &statusReceiver)
    : installerId_(installerId), statusReceiver_(statusReceiver)
{
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "create bundle installer instance id:%{public}" PRId64 "", installerId_);
}

BundleInstaller::~BundleInstaller()
{
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "destroy installer id:%{public}" PRId64 "", installerId_);
}

void BundleInstaller::Install(const std::string &bundleFilePath, const InstallParam &installParam)
{
#ifdef OH_ADAPTER_ANDROID
    // Android APK detection: dispatch to adapter-owned libapk_installer.so
    // via runtime dlopen. Keeps libbms zero-AOSP-dep (Route C).
    if (bundleFilePath.size() > 4 &&
        bundleFilePath.compare(bundleFilePath.size() - 4, 4, ".apk") == 0) {
        AdapterApkInstallFn installFn = LoadAdapterApkInstaller();
        int rc = kErrAdapterNotAvailable;
        if (installFn != nullptr) {
            rc = installFn(bundleFilePath.c_str(), installParam.userId);
        }
        if (statusReceiver_ != nullptr) {
            statusReceiver_->OnFinished(rc == 0 ? ERR_OK : rc,
                rc == 0 ? "Install success" :
                installFn == nullptr ? "APK adapter unavailable" : "APK install failed");
        }
        return;
    }
#endif
    ErrCode resultCode = ERR_OK;
    if (installParam.userId == Constants::ALL_USERID) {
        auto userInstallParam = installParam;
        userInstallParam.allUser = true;
        for (auto userId : GetExistsCommonUserIds()) {
            userInstallParam.userId = userId;
            userInstallParam.installFlag = InstallFlag::REPLACE_EXISTING;
            resultCode = InstallBundle(
                bundleFilePath, userInstallParam, Constants::AppType::THIRD_PARTY_APP);
            ResetInstallProperties();
        }

        NotifyAllBundleStatus();
    } else {
        resultCode = InstallBundle(
            bundleFilePath, installParam, Constants::AppType::THIRD_PARTY_APP);
        InstallForAllUsers(installParam);
    }
    std::string resultMsg = GetCheckResultMsg();
    SetCheckResultMsg("");
    if (statusReceiver_ != nullptr) {
        statusReceiver_->OnFinished(resultCode, resultMsg);
    }
}

void BundleInstaller::Recover(const std::string &bundleName, const InstallParam &installParam)
{
    ErrCode resultCode = ERR_OK;
    if (installParam.userId == Constants::ALL_USERID) {
        auto userInstallParam = installParam;
        for (auto userId : GetExistsCommonUserIds()) {
            userInstallParam.userId = userId;
            userInstallParam.installFlag = InstallFlag::REPLACE_EXISTING;
            resultCode = BaseBundleInstaller::Recover(bundleName, userInstallParam);
            ResetInstallProperties();
        }
    } else {
        resultCode = BaseBundleInstaller::Recover(bundleName, installParam);
        RecoverDriverForAllUsers(bundleName, installParam);
    }

    if (statusReceiver_ != nullptr) {
        statusReceiver_->OnFinished(resultCode, "");
    }
}

void BundleInstaller::Install(const std::vector<std::string> &bundleFilePaths, const InstallParam &installParam)
{
#ifdef OH_ADAPTER_ANDROID
    // adapter project: vector-overload .apk dispatch.
    // bm install -p X.apk takes the stream-install path which calls THIS
    // overload (not the string overload). If any path ends in .apk, route
    // the whole batch through the libapk_installer.so dlopen entry and
    // skip the OH HAP signature/decompress/install pipeline entirely.
    {
        bool anyApk = false;
        for (const auto &p : bundleFilePaths) {
            if (p.size() > 4 && p.compare(p.size() - 4, 4, ".apk") == 0) {
                anyApk = true;
                break;
            }
        }
        if (anyApk) {
            AdapterApkInstallFn installFn = LoadAdapterApkInstaller();
            ErrCode rc = ERR_OK;
            std::string finalMsg = "Install success";
            if (installFn == nullptr) {
                rc = static_cast<ErrCode>(kErrAdapterNotAvailable);
                finalMsg = "APK adapter unavailable";
            } else {
                for (const auto &p : bundleFilePaths) {
                    if (p.size() > 4 && p.compare(p.size() - 4, 4, ".apk") == 0) {
                        int r = installFn(p.c_str(), installParam.userId);
                        if (r != 0) {
                            rc = static_cast<ErrCode>(r);
                            finalMsg = "APK install failed";
                            break;
                        }
                    }
                }
            }
            if (statusReceiver_ != nullptr) {
                statusReceiver_->OnFinished(rc, finalMsg);
            }
            return;
        }
    }
#endif
    ErrCode resultCode = ERR_OK;
    if (installParam.userId == Constants::ALL_USERID) {
        auto userInstallParam = installParam;
        userInstallParam.allUser = true;
        for (auto userId : GetExistsCommonUserIds()) {
            userInstallParam.userId = userId;
            userInstallParam.installFlag = InstallFlag::REPLACE_EXISTING;
            resultCode = InstallBundle(
                bundleFilePaths, userInstallParam, Constants::AppType::THIRD_PARTY_APP);
            ResetInstallProperties();
        }

        NotifyAllBundleStatus();
    } else {
        resultCode = InstallBundle(bundleFilePaths, installParam, Constants::AppType::THIRD_PARTY_APP);
        InstallForAllUsers(installParam);
    }
    std::string resultMsg = GetCheckResultMsg();
    SetCheckResultMsg("");
    if (statusReceiver_ != nullptr) {
        statusReceiver_->OnFinished(resultCode, resultMsg);
    }
}

void BundleInstaller::InstallByBundleName(const std::string &bundleName, const InstallParam &installParam)
{
    ErrCode resultCode = InstallBundleByBundleName(bundleName, installParam);
    if (statusReceiver_ != nullptr) {
        statusReceiver_->OnFinished(resultCode, "");
    }
}

void BundleInstaller::Uninstall(const std::string &bundleName, const InstallParam &installParam)
{
    ErrCode resultCode = ERR_OK;
    bool isDriver = IsDriverForAllUser(bundleName);
    int32_t userId = GetDriverInstallUser(bundleName);
    if (isDriver && (userId == Constants::DEFAULT_USERID || userId == Constants::U1)) {
        InstallParam param = installParam;
        param.userId = userId;
        resultCode = UninstallBundle(bundleName, param);
        if (statusReceiver_ != nullptr) {
            statusReceiver_->OnFinished(resultCode, "");
        }
        return;
    }
    if (installParam.userId == Constants::ALL_USERID ||
        (!installParam.isRemoveUser && isDriver)) {
        std::vector<ErrCode> errCode;
        auto userInstallParam = installParam;
        for (auto userId : GetExistsCommonUserIds()) {
            userInstallParam.userId = userId;
            resultCode = UninstallBundle(bundleName, userInstallParam);
            errCode.push_back(resultCode);
            ResetInstallProperties();
        }
        if (std::find(errCode.begin(), errCode.end(), ERR_OK) != errCode.end()) {
            for (const auto &err : errCode) {
                if (!(err == ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_BUNDLE ||
                    err == ERR_APPEXECFWK_USER_NOT_INSTALL_HAP || err == ERR_OK)) {
                    resultCode = err;
                    break;
                }
                resultCode = ERR_OK;
            }
        } else {
            resultCode = (errCode.size() > 0) ? errCode[0] : ERR_OK;
        }
    } else {
        resultCode = UninstallBundle(bundleName, installParam);
    }

    if (statusReceiver_ != nullptr) {
        statusReceiver_->OnFinished(resultCode, "");
    }
}

void BundleInstaller::Uninstall(const UninstallParam &uninstallParam)
{
    ErrCode resultCode = ERR_OK;
    resultCode = UninstallBundleByUninstallParam(uninstallParam);
    if (statusReceiver_ != nullptr) {
        statusReceiver_->OnFinished(resultCode, "");
    }
}

void BundleInstaller::Uninstall(
    const std::string &bundleName, const std::string &modulePackage, const InstallParam &installParam)
{
    ErrCode resultCode = ERR_OK;
    bool isDriver = IsDriverForAllUser(bundleName);
    int32_t userId = GetDriverInstallUser(bundleName);
    if (isDriver && (userId == Constants::DEFAULT_USERID || userId == Constants::U1)) {
        InstallParam param = installParam;
        param.userId = userId;
        resultCode = UninstallBundle(bundleName, modulePackage, param);
        if (statusReceiver_ != nullptr) {
            statusReceiver_->OnFinished(resultCode, "");
        }
        return;
    }
    if (installParam.userId == Constants::ALL_USERID || isDriver) {
        std::vector<ErrCode> errCode;
        auto userInstallParam = installParam;
        for (auto userId : GetExistsCommonUserIds()) {
            userInstallParam.userId = userId;
            resultCode = UninstallBundle(bundleName, modulePackage, userInstallParam);
            errCode.push_back(resultCode);
            ResetInstallProperties();
        }
        if (std::find(errCode.begin(), errCode.end(), ERR_OK) != errCode.end()) {
            for (const auto &err : errCode) {
                if (!(err == ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_BUNDLE ||
                    err == ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_MODULE ||
                    err == ERR_APPEXECFWK_USER_NOT_INSTALL_HAP || err == ERR_OK)) {
                    resultCode = err;
                    break;
                }
                resultCode = ERR_OK;
            }
        } else {
            resultCode = (errCode.size() > 0) ? errCode[0] : ERR_OK;
        }
    } else {
        resultCode = UninstallBundle(bundleName, modulePackage, installParam);
    }

    if (statusReceiver_ != nullptr) {
        statusReceiver_->OnFinished(resultCode, "");
    }
}

void BundleInstaller::UpdateInstallerState(const InstallerState state)
{
    LOG_D(BMS_TAG_INSTALLER, "state: %{public}d", static_cast<int32_t>(state));
    SetInstallerState(state);
    if (statusReceiver_) {
        statusReceiver_->OnStatusNotify(static_cast<int>(state));
    }
}

std::set<int32_t> BundleInstaller::GetExistsCommonUserIds()
{
    std::set<int32_t> userIds;
    auto dataMgr = DelayedSingleton<BundleMgrService>::GetInstance()->GetDataMgr();
    if (dataMgr == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "Get dataMgr shared_ptr nullptr");
        return userIds;
    }

    for (auto userId : dataMgr->GetAllUser()) {
        if (userId >= Constants::START_USERID) {
            userIds.insert(userId);
        }
    }
    return userIds;
}

void BundleInstaller::InstallForAllUsers(const InstallParam &installParam)
{
    std::string bundleName = GetCurrentBundleName();
    if (!IsDriverForAllUser(bundleName) && !IsEnterpriseForAllUser(installParam, bundleName)) {
        APP_LOGD("bundle %{public}s has no driver extension ability", bundleName.c_str());
        return;
    }
    APP_LOGI("bundle %{public}s need to install for all users", bundleName.c_str());
    ResetInstallProperties();
    auto userInstallParam = installParam;
    for (auto userId : GetExistsCommonUserIds()) {
        if (userId == installParam.userId) {
            continue;
        }
        userInstallParam.userId = userId;
        userInstallParam.installFlag = InstallFlag::REPLACE_EXISTING;
        ErrCode resultCode = InstallBundleByBundleName(bundleName, userInstallParam);
        ResetInstallProperties();
        if (resultCode != ERR_OK) {
            APP_LOGE("install for user %{public}d failed, resultCode: %{public}d", userId, resultCode);
        }
    }
    NotifyAllBundleStatus();
}

void BundleInstaller::RecoverDriverForAllUsers(const std::string &bundleName, const InstallParam &installParam)
{
    if (!IsDriverForAllUser(bundleName)) {
        APP_LOGD("bundle %{public}s has no driver extension ability", bundleName.c_str());
        return;
    }
    APP_LOGI("bundle %{public}s has driver extension ability, need to recover for all users", bundleName.c_str());
    ResetInstallProperties();
    auto userInstallParam = installParam;
    for (auto userId : GetExistsCommonUserIds()) {
        if (userId == installParam.userId) {
            continue;
        }
        userInstallParam.userId = userId;
        userInstallParam.installFlag = InstallFlag::REPLACE_EXISTING;
        ErrCode resultCode = BaseBundleInstaller::Recover(bundleName, userInstallParam);
        ResetInstallProperties();
        if (resultCode != ERR_OK) {
            APP_LOGE("recover driver for user %{public}d failed, resultCode: %{public}d", userId, resultCode);
        }
    }
}

void BundleInstaller::UninstallAndRecover(const std::string &bundleName, const InstallParam &installParam)
{
    ErrCode resultCode = ERR_OK;
    std::vector<ErrCode> errCode;
    auto userInstallParam = installParam;
    std::vector<int32_t> userIds;
    if (!installParam.isOTA && !CheckSystemInodeSatisfied(bundleName)) {
        APP_LOGE("System inode not satisfied for uninstall and recover");
        if (statusReceiver_ != nullptr) {
            statusReceiver_->OnFinished(ERR_BUNDLE_MANAGER_INSUFFICIENT_NUMBER_OF_SYSTEM_INODES, "");
        }
        return;
    }
    auto dataMgr = DelayedSingleton<BundleMgrService>::GetInstance()->GetDataMgr();
    if (dataMgr != nullptr) {
        userIds = dataMgr->GetUserIds(bundleName);
    }
    for (auto userId : userIds) {
        userInstallParam.userId = userId;
        userInstallParam.SetIsUninstallAndRecover(true);
        resultCode = UninstallBundle(bundleName, userInstallParam);
        errCode.push_back(resultCode);
        ResetInstallProperties();
    }
    for (auto userId : userIds) {
        userInstallParam.userId = userId;
        userInstallParam.installFlag = InstallFlag::REPLACE_EXISTING;
        resultCode = BaseBundleInstaller::Recover(bundleName, userInstallParam);
        errCode.push_back(resultCode);
        ResetInstallProperties();
    }

    if (std::find(errCode.begin(), errCode.end(), ERR_OK) != errCode.end()) {
        for (const auto &err : errCode) {
            if (err != ERR_OK) {
                resultCode = err;
                break;
            }
            resultCode = ERR_OK;
        }
    } else {
        resultCode = (errCode.size() > 0) ? errCode[0] : ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_BUNDLE;
    }
    if (statusReceiver_ != nullptr) {
        statusReceiver_->OnFinished(resultCode, "");
    }
}
}  // namespace AppExecFwk
}  // namespace OHOS