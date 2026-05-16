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

#include "base_bundle_installer.h"
#include "bundle_resource/bundle_resource_rdb.h"  // adapter project
#include "bundle_resource/resource_info.h"  // adapter project
#include "installd_client.h"  // adapter project: route entry.hap copy via installd
#include <sys/stat.h>  // adapter project: chmod for entry.hap copy
#ifdef OH_ADAPTER_ANDROID
#include <dlfcn.h>  // adapter project: libapk_installer.so dispatch
#endif

#include <algorithm>
#include <fcntl.h>
#include <fstream>
#include <sys/stat.h>
#include <sys/statfs.h>
#include <sstream>

#include "account_helper.h"
#ifdef BUNDLE_FRAMEWORK_FREE_INSTALL
#include "aging/bundle_aging_mgr.h"
#endif
#include "aot/aot_handler.h"
#include "app_control_constants.h"
#include "app_mgr_client.h"
#ifdef BUNDLE_FRAMEWORK_DEFAULT_APP
#include "default_app_mgr.h"
#endif
#ifdef BUNDLE_FRAMEWORK_QUICK_FIX
#include "quick_fix/app_quick_fix.h"
#include "quick_fix/inner_app_quick_fix.h"
#include "quick_fix/quick_fix_data_mgr.h"
#include "quick_fix/quick_fix_switcher.h"
#include "quick_fix/quick_fix_deleter.h"
#endif
#include "ability_manager_helper.h"
#include "app_log_tag_wrapper.h"
#include "app_provision_info_manager.h"
#include "bms_extension_data_mgr.h"
#include "bms_update_selinux_mgr.h"
#include "bundle_clone_installer.h"
#include "bundle_permission_mgr.h"
#include "bundle_resource_helper.h"
#include "bundle_parser.h"
#include "bundle_util.h"
#include "code_protect_bundle_info.h"
#include "datetime_ex.h"
#include "driver_installer.h"
#include "hitrace_meter.h"
#include "installd_client.h"
#include "install_exception_mgr.h"
#include "ipc/install_hnp_param.h"
#include "ipc/verify_bin_param.h"
#include "new_bundle_data_dir_mgr.h"
#include "on_demand_install_data_mgr.h"
#include "parameter.h"
#include "parameters.h"
#include "perf_profile.h"
#include "scope_guard.h"
#include "utd_handler.h"
#ifdef BUNDLE_FRAMEWORK_OVERLAY_INSTALLATION
#include "bundle_overlay_data_manager.h"
#include "bundle_overlay_install_checker.h"
#endif
#ifdef WEBVIEW_ENABLE
#include "app_fwk_update_client.h"
#endif

#ifdef STORAGE_SERVICE_ENABLE
#include "storage_manager_proxy.h"
#endif
#include "iservice_registry.h"
#include "ipc_skeleton.h"
#include "inner_bundle_clone_common.h"
#include "inner_patch_info.h"
#include "patch_data_mgr.h"

namespace OHOS {
namespace AppExecFwk {
using namespace OHOS::Security;
namespace {
constexpr const char* DATA_PRELOAD_APP = "/data/preload/app/";
constexpr const char* COMPILE_SDK_TYPE_OPEN_HARMONY = "OpenHarmony";
constexpr const char* LOG = "log";
constexpr const char* HSP_VERSION_PREFIX = "v";
constexpr const char* PRE_INSTALL_HSP_PATH = "/shared_bundles/";
constexpr const char* APP_INSTALL_PATH = "/data/app/el1/bundle";
constexpr const char* BMS_SERVICE_PATH = "/data/service";
constexpr const char* APP_INSTALL_SANDBOX_PATH = "/data/bms_app_install/";
constexpr const char* BACKUP_BUNDLES = "/backup/bundles/";
const int64_t FIVE_MB = 1024 * 1024 * 5; // 5MB
constexpr const char* DEBUG_APP_IDENTIFIER = "DEBUG_LIB_ID";
constexpr const char* SKILL_URI_SCHEME_HTTPS = "https";
constexpr const char* LIBS_TMP = "libs+tmp";
constexpr const char* PRIVILEGE_ALLOW_HDC_INSTALL = "AllowHdcInstall";
constexpr const char* KEY_STORAGE_SIZE = "storageSize";

#ifdef STORAGE_SERVICE_ENABLE
#ifdef QUOTA_PARAM_SET_ENABLE
constexpr const char* SYSTEM_PARAM_ATOMICSERVICE_DATASIZE_THRESHOLD =
    "persist.sys.bms.aging.policy.atomicservice.datasize.threshold";
constexpr int32_t THRESHOLD_VAL_LEN = 20;
#endif // QUOTA_PARAM_SET_ENABLE
constexpr int32_t STORAGE_MANAGER_MANAGER_ID = 5003;
#endif // STORAGE_SERVICE_ENABLE
constexpr int16_t ATOMIC_SERVICE_DATASIZE_THRESHOLD_MB_PRESET = 1024;
constexpr int8_t SINGLE_HSP_VERSION = 1;
constexpr int8_t USER_MODE = 0;
constexpr const char* BMS_KEY_SHELL_UID = "const.product.shell.uid";
constexpr const char* IS_ROOT_MODE_PARAM = "const.debuggable";
constexpr const char* BMS_ACTIVATION_LOCK = "persist.bms.activation-lock";
constexpr const char* APPSPAWN_PRELOAD_ARKWEB_ENGINE = "const.startup.appspawn.preload.arkwebEngine";
constexpr const char* BMS_TRUE = "true";
constexpr const char* BMS_FALSE = "false";
constexpr int8_t BMS_ACTIVATION_LOCK_VAL_LEN = 20;
const char* INSTALL_SOURCE_UNKNOWN = "unknown";
const char* ARK_WEB_BUNDLE_NAME_PARAM = "persist.arkwebcore.package_name";
const char* OLD_ARK_WEB_BUNDLE_NAME = "com.ohos.nweb";
const char* NEW_ARK_WEB_BUNDLE_NAME = "com.ohos.arkwebcore";
constexpr const char* DEDUPLICATEHAR_NOTE =
    "App contains HSP that supports har deduplication, requires entry module to run properly";
// module.json: hnpPackages type only allows [public, private]
constexpr const char* TYPE_PUBLIC = "public";
constexpr const char* TYPE_PRIVATE = "private";
constexpr const char* USER_DATA_DIR = "/data";
constexpr const char* MODULE_NAME_IS_LIBS = "libs";
constexpr const char* MODULE_DIR_IS_LIBS = "/libs";

std::string GetHapPath(const InnerBundleInfo &info, const std::string &moduleName)
{
    std::string fileSuffix = ServiceConstants::INSTALL_FILE_SUFFIX;
    auto moduleInfo = info.GetInnerModuleInfoByModuleName(moduleName);
    if (moduleInfo && moduleInfo->distro.moduleType == Profile::MODULE_TYPE_SHARED) {
        LOG_D(BMS_TAG_INSTALLER, "The module(%{public}s) is shared", moduleName.c_str());
        fileSuffix = ServiceConstants::HSP_FILE_SUFFIX;
    }

    return info.GetAppCodePath() + ServiceConstants::PATH_SEPARATOR + moduleName + fileSuffix;
}

std::string GetHapPath(const InnerBundleInfo &info)
{
    return GetHapPath(info, info.GetModuleName(info.GetCurrentModulePackage()));
}

std::string BuildTempNativeLibraryPath(const std::string &nativeLibraryPath)
{
    auto position = nativeLibraryPath.find(ServiceConstants::PATH_SEPARATOR);
    if (position == std::string::npos) {
        return nativeLibraryPath;
    }

    auto prefixPath = nativeLibraryPath.substr(0, position);
    auto suffixPath = nativeLibraryPath.substr(position);
    return prefixPath + ServiceConstants::TMP_SUFFIX + suffixPath;
}

void PrintDataStat()
{
    struct statfs stat;
    if (statfs(USER_DATA_DIR, &stat) != 0) {
        LOG_E(BMS_TAG_INSTALLER, "statfs failed for %{public}s, error %{public}d",
            USER_DATA_DIR, errno);
        return;
    }
    // Calculate required inodes with 1.2x safety factor (configurable)
    LOG_D(BMS_TAG_INSTALLER,
        "systemAvailable=%{public}llu, systemTotal=%{public}llu",
        static_cast<unsigned long long>(stat.f_ffree),
        static_cast<unsigned long long>(stat.f_files));
}
} // namespace

BaseBundleInstaller::BaseBundleInstaller()
    : bundleInstallChecker_(std::make_unique<BundleInstallChecker>()) {}

BaseBundleInstaller::~BaseBundleInstaller()
{
    bundlePaths_.clear();
    BundleUtil::DeleteTempDirs(toDeleteTempHapPath_);
    toDeleteTempHapPath_.clear();
    signatureFileTmpMap_.clear();
}

ErrCode BaseBundleInstaller::InstallBundle(
    const std::string &bundlePath, const InstallParam &installParam, const Constants::AppType appType)
{
    std::vector<std::string> bundlePaths { bundlePath };
    return InstallBundle(bundlePaths, installParam, appType);
}

void BaseBundleInstaller::SendStartInstallNotify(const InstallParam &installParam,
    const std::unordered_map<std::string, InnerBundleInfo> &infos)
{
    if (!installParam.needSendEvent) {
        LOG_NOFUNC_W(BMS_TAG_INSTALLER, "SendStartInstallNotify needSendEvent is false");
        return;
    }
    if (bundleName_.empty()) {
        LOG_NOFUNC_W(BMS_TAG_INSTALLER, "SendStartInstallNotify bundleName is empty");
        return;
    }
    bool isAppExist = false;
    if (InitDataMgr()) {
        isAppExist = dataMgr_->HasUserInstallInBundle(bundleName_, userId_);
    }
    for (const auto &item : infos) {
        LOG_D(BMS_TAG_INSTALLER, "SendStartInstallNotify %{public}s  %{public}s %{public}s %{public}s",
            bundleName_.c_str(), item.second.GetCurModuleName().c_str(),
            item.second.GetAppId().c_str(), item.second.GetAppIdentifier().c_str());
        NotifyBundleEvents installRes = {
            .isAppUpdate = isAppExist,
            .type = NotifyType::START_INSTALL,
            .bundleName = bundleName_,
            .modulePackage = item.second.GetCurModuleName(),
            .appId = item.second.GetAppId(),
            .appIdentifier = item.second.GetAppIdentifier()
        };
        if (NotifyBundleStatus(installRes) != ERR_OK) {
            LOG_W(BMS_TAG_INSTALLER, "notify status failed for start install");
        }
    }
}

ErrCode BaseBundleInstaller::InstallBundle(
    const std::vector<std::string> &bundlePaths, const InstallParam &installParam, const Constants::AppType appType)
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "begin to process bundle install");
    AddInstallingBundleName(installParam);
    PerfProfile::GetInstance().SetBundleInstallStartTime(GetTickCount());
    sysEventInfo_.startTime = BundleUtil::GetCurrentTimeMs();
    int32_t uid = Constants::INVALID_UID;
    ErrCode result = ProcessBundleInstall(bundlePaths, installParam, appType, uid, false);
    if (result != ERR_APPEXECFWK_INSTALL_ZERO_USER_WITH_NO_SINGLETON && result != ERR_OK &&
        installParam.isDataPreloadHap && GetUserId(installParam.userId) == Constants::DEFAULT_USERID) {
        LOG_E(BMS_TAG_INSTALLER, "set parameter BMS_DATA_PRELOAD false");
        OHOS::system::SetParameter(ServiceConstants::BMS_DATA_PRELOAD, BMS_FALSE);
    }
    CheckPreBundleRecoverResult(result);
    std::map<std::string, std::string> tokenIdMetadataInfos;
    tokenIdMetadataInfos[ServiceConstants::META_KEEP_TOKEN_ID_KEY] = isKeepTokenId_ ? BMS_TRUE : BMS_FALSE;
    if (installParam.needSendEvent && dataMgr_ && !bundleName_.empty()) {
        NotifyBundleEvents installRes = {
            .isModuleUpdate = isModuleUpdate_,
            .type = GetNotifyType(),
            .resultCode = result,
            .accessTokenId = accessTokenId_,
            .uid = uid,
            .bundleType = static_cast<int32_t>(bundleType_),
            .atomicServiceModuleUpgrade = atomicServiceModuleUpgrade_,
            .bundleName = bundleName_,
            .modulePackage = moduleName_,
            .abilityName = mainAbility_,
            .appDistributionType = appDistributionType_,
            .crossAppSharedConfig = isBundleCrossAppSharedConfig_,
            .metadataConfigInfos = tokenIdMetadataInfos
        };
        if (installParam.allUser || IsDriverForAllUser(bundleName_) ||
            IsEnterpriseForAllUser(installParam, bundleName_)) {
            AddBundleStatus(installRes);
        } else if (NotifyBundleStatus(installRes) != ERR_OK) {
            LOG_W(BMS_TAG_INSTALLER, "notify status failed for installation");
        }
    }
    if (result == ERR_OK) {
        OnSingletonChange(installParam.GetKillProcess());
    } else {
        RestoreHaps(bundlePaths, installParam);
    }
    if (!bundlePaths.empty()) {
        SendBundleSystemEvent(
            bundleName_.empty() ? bundlePaths[0] : bundleName_,
            ((isAppExist_ && hasInstalledInUser_) ? BundleEventType::UPDATE : BundleEventType::INSTALL),
            installParam, sysEventInfo_.preBundleScene, result);
    }
    PerfProfile::GetInstance().SetBundleInstallEndTime(GetTickCount());
    DeleteInstallingBundleName(installParam);
    NotifyBundleCallback(isAppExist_ ? NotifyType::UPDATE : NotifyType::INSTALL, uid);
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "InstallBundle finished -n %{public}s -u %{public}d",
        bundleName_.c_str(), installParam.userId);
    return result;
}

ErrCode BaseBundleInstaller::InstallBundleByBundleName(
    const std::string &bundleName, const InstallParam &installParam)
{
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "InstallBundleByBundleName -n %{public}s", bundleName.c_str());
    PerfProfile::GetInstance().SetBundleInstallStartTime(GetTickCount());
    sysEventInfo_.startTime = BundleUtil::GetCurrentTimeMs();

    int32_t uid = Constants::INVALID_UID;
    ErrCode result = ProcessInstallBundleByBundleName(bundleName, installParam, uid);
    std::map<std::string, std::string> tokenIdMetadataInfos;
    tokenIdMetadataInfos[ServiceConstants::META_KEEP_TOKEN_ID_KEY] = isKeepTokenId_ ? BMS_TRUE : BMS_FALSE;
    if (installParam.needSendEvent && dataMgr_ && !bundleName.empty()) {
        NotifyBundleEvents installRes = {
            .type = NotifyType::INSTALL,
            .resultCode = result,
            .accessTokenId = accessTokenId_,
            .uid = uid,
            .bundleType = static_cast<int32_t>(bundleType_),
            .atomicServiceModuleUpgrade = atomicServiceModuleUpgrade_,
            .userId = userId_,
            .bundleName = bundleName,
            .appDistributionType = appDistributionType_,
            .crossAppSharedConfig = isBundleCrossAppSharedConfig_,
            .metadataConfigInfos = tokenIdMetadataInfos,
            .isInstallByBundleName = true
        };
        if (installParam.concentrateSendEvent) {
            AddNotifyBundleEvents(installRes);
        } else if (IsDriverForAllUser(bundleName) || IsEnterpriseForAllUser(installParam, bundleName)) {
            AddBundleStatus(installRes);
        } else if (NotifyBundleStatus(installRes) != ERR_OK) {
            LOG_W(BMS_TAG_INSTALLER, "notify status failed for installation");
        }
    }

    SendBundleSystemEvent(
        bundleName,
        BundleEventType::INSTALL,
        installParam,
        installParam.isPreInstallApp ? InstallScene::CREATE_USER : InstallScene::NORMAL,
        result);
    PerfProfile::GetInstance().SetBundleInstallEndTime(GetTickCount());
    LOG_I(BMS_TAG_INSTALLER, "finish install %{public}s resultCode: %{public}d", bundleName.c_str(), result);
    return result;
}

ErrCode BaseBundleInstaller::Recover(
    const std::string &bundleName, const InstallParam &installParam)
{
    LOG_I(BMS_TAG_INSTALLER, "begin to process bundle recover by bundleName, which is %{public}s", bundleName.c_str());
    PerfProfile::GetInstance().SetBundleInstallStartTime(GetTickCount());
    sysEventInfo_.startTime = BundleUtil::GetCurrentTimeMs();
    int32_t userId = GetUserId(installParam.userId);
    ErrCode result = CheckAppBlackList(bundleName, userId);
    if (result != ERR_OK) {
        return result;
    }
    int32_t uid = Constants::INVALID_UID;
    result = ProcessRecover(bundleName, installParam, uid);
    std::map<std::string, std::string> tokenIdMetadataInfos;
    tokenIdMetadataInfos[ServiceConstants::META_KEEP_TOKEN_ID_KEY] = isKeepTokenId_ ? BMS_TRUE : BMS_FALSE;
    if (installParam.needSendEvent && dataMgr_) {
        NotifyBundleEvents installRes = {
            .type = NotifyType::INSTALL,
            .resultCode = result,
            .accessTokenId = accessTokenId_,
            .uid = uid,
            .bundleType = static_cast<int32_t>(bundleType_),
            .bundleName = bundleName,
            .appDistributionType = appDistributionType_,
            .crossAppSharedConfig = isBundleCrossAppSharedConfig_,
            .metadataConfigInfos = tokenIdMetadataInfos,
            .isRecover = true
        };
        if (NotifyBundleStatus(installRes) != ERR_OK) {
            LOG_W(BMS_TAG_INSTALLER, "notify status failed for installation");
        }
    }

    auto recoverInstallParam = installParam;
    recoverInstallParam.isPreInstallApp = true;
    SendBundleSystemEvent(
        bundleName,
        BundleEventType::RECOVER,
        recoverInstallParam,
        sysEventInfo_.preBundleScene,
        result);
    PerfProfile::GetInstance().SetBundleInstallEndTime(GetTickCount());
    LOG_D(BMS_TAG_INSTALLER, "finish to process %{public}s bundle recover", bundleName.c_str());
    return result;
}

ErrCode BaseBundleInstaller::UninstallBundle(const std::string &bundleName, const InstallParam &installParam)
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    CheckSystemFreeSizeAndClean();
    LOG_I(BMS_TAG_INSTALLER, "begin to process %{public}s bundle uninstall", bundleName.c_str());
    PerfProfile::GetInstance().SetBundleUninstallStartTime(GetTickCount());
    sysEventInfo_.startTime = BundleUtil::GetCurrentTimeMs();

    std::string developerId = GetDeveloperId(bundleName);
    std::string assetAccessGroups = GetAssetAccessGroups(bundleName);

    // uninstall all sandbox app before
    UninstallAllSandboxApps(bundleName, installParam.userId);

    int32_t uid = Constants::INVALID_UID;
    bool isUninstalledFromBmsExtension = false;
    ErrCode result = ProcessBundleUninstall(bundleName, installParam, uid);
    if (result == ERR_APPEXECFWK_UNINSTALL_STATE_NOT_ALLOW) {
        CheckBundleNameAndStratAbility(bundleName, appIdentifier_);
    }
    if (result == ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_BUNDLE) {
        ErrCode bmsExtensionResult = UninstallBundleFromBmsExtension(bundleName);
        if (bmsExtensionResult == ERR_OK) {
            isUninstalledFromBmsExtension = true;
            result = ERR_OK;
        } else if (bmsExtensionResult == ERR_APPEXECFWK_UNINSTALL_DISPOSED_RULE_FAILED) {
            result = ERR_APPEXECFWK_UNINSTALL_DISPOSED_RULE_FAILED;
        }
    }
    if (!installParam.isKeepData && (result == ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_BUNDLE ||
        result == ERR_APPEXECFWK_USER_NOT_INSTALL_HAP) &&
        DeleteUninstallBundleInfoFromDb(bundleName)) {
        DelayedSingleton<BmsUpdateSelinuxMgr>::GetInstance()->DeleteBundle(bundleName, userId_, 0);
        LOG_I(BMS_TAG_INSTALLER, "del uninstalled bundle %{public}s dir and info", bundleName.c_str());
        SendBundleSystemEvent(bundleName, BundleEventType::UNINSTALL, installParam,
            sysEventInfo_.preBundleScene, ERR_OK);
        return ERR_OK;
    }

    if (result == ERR_OK) {
        UtdHandler::UninstallUtdAsync(bundleName, userId_);
#ifdef BUNDLE_FRAMEWORK_DEFAULT_APP
        if (!installParam.isRemoveUser) {
            DefaultAppMgr::GetInstance().HandleUninstallBundle(userId_, bundleName, 0);
        }
#endif
    }

    std::map<std::string, std::string> tokenIdMetadataInfos;
    tokenIdMetadataInfos[ServiceConstants::META_KEEP_TOKEN_ID_KEY] = isKeepTokenId_ ? BMS_TRUE : BMS_FALSE;
    if (installParam.needSendEvent && dataMgr_) {
        NotifyBundleEvents installRes = {
            .isAgingUninstall = installParam.isAgingUninstall,
            .isBmsExtensionUninstalled = isUninstalledFromBmsExtension,
            .type = NotifyType::UNINSTALL_BUNDLE,
            .resultCode = result,
            .accessTokenId = accessTokenId_,
            .uid = uid,
            .bundleType = static_cast<int32_t>(bundleType_),
            .bundleName = bundleName,
            .appId = uninstallBundleAppId_,
            .appDistributionType = appDistributionType_,
            .developerId = developerId,
            .assetAccessGroups = assetAccessGroups,
            .keepData = installParam.isKeepData,
            .crossAppSharedConfig = isBundleCrossAppSharedConfig_,
            .metadataConfigInfos = tokenIdMetadataInfos
        };

        if (installParam.concentrateSendEvent) {
            AddNotifyBundleEvents(installRes);
        } else if (NotifyBundleStatus(installRes) != ERR_OK) {
            LOG_W(BMS_TAG_INSTALLER, "notify status failed for installation");
        }
    }
    NotifyBundleCallback(NotifyType::UNINSTALL_BUNDLE, uid);
    SendBundleSystemEvent(bundleName, BundleEventType::UNINSTALL, installParam, sysEventInfo_.preBundleScene, result);
    PerfProfile::GetInstance().SetBundleUninstallEndTime(GetTickCount());
    LOG_D(BMS_TAG_INSTALLER, "finish to process %{public}s bundle uninstall", bundleName.c_str());
    return result;
}

bool BaseBundleInstaller::IsAllowEnterPrise()
{
    if (!OHOS::system::GetBoolParameter(ServiceConstants::ALLOW_ENTERPRISE_BUNDLE, false) &&
        !OHOS::system::GetBoolParameter(ServiceConstants::IS_ENTERPRISE_DEVICE, false)) {
        LOG_E(BMS_TAG_INSTALLER, "not enterprise device or developer mode is off");
        return false;
    }
    return true;
}

void BaseBundleInstaller::MarkIsForceUninstall(const std::string &bundleName, bool isForceUninstalled)
{
    if (!dataMgr_) {
        LOG_E(BMS_TAG_INSTALLER, "dataMgr is nullptr");
        return;
    }
 
    PreInstallBundleInfo preInstallBundleInfo;
    preInstallBundleInfo.SetBundleName(bundleName);
    if (!dataMgr_->GetPreInstallBundleInfo(bundleName, preInstallBundleInfo)) {
        LOG_D(BMS_TAG_INSTALLER, "No PreInstallBundleInfo(%{public}s) in db", bundleName.c_str());
        return;
    }
    if (isForceUninstalled) {
        preInstallBundleInfo.AddForceUnisntalledUser(userId_);
        dataMgr_->SavePreInstallBundleInfo(bundleName, preInstallBundleInfo);
    }
    return;
}

bool BaseBundleInstaller::CheckCanInstallPreBundle(const std::string &bundleName, const int32_t userId)
{
    if (!InitDataMgr()) {
        LOG_E(BMS_TAG_INSTALLER, "DataMgr null");
        return false;
    }
    PreInstallBundleInfo preInstallBundleInfo;
    preInstallBundleInfo.SetBundleName(bundleName);
    dataMgr_->GetPreInstallBundleInfo(bundleName, preInstallBundleInfo);
    if (!preInstallBundleInfo.HasForceUninstalledUser(userId)) {
        LOG_D(BMS_TAG_INSTALLER, "GetBundleNameForUid %{public}s is not forceuninstalled in  %{public}d",
            bundleName.c_str(), userId);
        return true;
    }
    return false;
}

ErrCode BaseBundleInstaller::CheckUninstallInnerBundleInfo(const InnerBundleInfo &info, const std::string &bundleName)
{
    if (!info.IsRemovable()) {
        LOG_NOFUNC_E(BMS_TAG_INSTALLER, "uninstall system app");
        return ERR_APPEXECFWK_UNINSTALL_SYSTEM_APP_ERROR;
    }
    if (!info.GetUninstallState()) {
        LOG_E(BMS_TAG_INSTALLER, "bundle : %{public}s can not be uninstalled, uninstallState : %{public}d",
            bundleName.c_str(), info.GetUninstallState());
        return ERR_APPEXECFWK_UNINSTALL_STATE_NOT_ALLOW;
    }
    if (info.GetApplicationBundleType() != BundleType::SHARED) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall bundle is not shared library");
        return ERR_APPEXECFWK_UNINSTALL_SHARE_APP_LIBRARY_IS_NOT_EXIST;
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::UninstallBundleByUninstallParam(const UninstallParam &uninstallParam)
{
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "begin to process cross-app %{public}s uninstall",
        uninstallParam.bundleName.c_str());
    const std::string &bundleName = uninstallParam.bundleName;
    int32_t versionCode = uninstallParam.versionCode;
    versionCode_ = versionCode;
    if (bundleName.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall bundle name or module name empty");
        return ERR_APPEXECFWK_UNINSTALL_SHARE_APP_LIBRARY_IS_NOT_EXIST;
    }
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_UNINSTALL_BUNDLE_MGR_SERVICE_ERROR;
    }
    auto &mtx = dataMgr_->GetBundleMutex(bundleName);
    std::lock_guard lock {mtx};
    bool isAppExist = false;
    InnerBundleInfo info;
    if (!InitTempBundleFromCache(info, isAppExist, bundleName) || !isAppExist) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall bundle info missing");
        return ERR_APPEXECFWK_UNINSTALL_SHARE_APP_LIBRARY_IS_NOT_EXIST;
    }
    ScopeGuard enableGuard([&] { dataMgr_->EnableBundle(bundleName); });
    ErrCode ret = CheckUninstallInnerBundleInfo(info, bundleName);
    if (ret != ERR_OK) {
        LOG_NOFUNC_W(BMS_TAG_INSTALLER, "CheckUninstallInnerBundleInfo failed, errcode: %{public}d", ret);
        return ret;
    }
    if (dataMgr_->CheckHspVersionIsRelied(versionCode, info)) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall shared library is relied");
        return ERR_APPEXECFWK_UNINSTALL_SHARE_APP_LIBRARY_IS_RELIED;
    }
    // if uninstallParam do not contain versionCode, versionCode is ALL_VERSIONCODE
    std::vector<uint32_t> versionCodes = info.GetAllHspVersion();
    if (versionCode != Constants::ALL_VERSIONCODE &&
        std::find(versionCodes.begin(), versionCodes.end(), versionCode) == versionCodes.end()) {
        LOG_E(BMS_TAG_INSTALLER, "input versionCode is not exist");
        return ERR_APPEXECFWK_UNINSTALL_SHARE_APP_LIBRARY_IS_NOT_EXIST;
    }
    std::string uninstallDir = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR + bundleName;
    return UninstallHspAndBundle(info, versionCode, uninstallDir);
}

ErrCode BaseBundleInstaller::UninstallHspBundle(std::string &uninstallDir, const std::string &bundleName)
{
    LOG_D(BMS_TAG_INSTALLER, "begin to process hsp bundle %{public}s uninstall", bundleName.c_str());
    // remove bundle dir first, then delete data in bundle data manager
    ErrCode errCode;
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_NULL_PTR;
    }
     // delete bundle bunlde in data
    if (!dataMgr_->UpdateBundleInstallState(bundleName, InstallState::UNINSTALL_START)) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall start failed");
        return ERR_APPEXECFWK_UPDATE_BUNDLE_INSTALL_STATUS_ERROR;
    }
    if ((errCode = InstalldClient::GetInstance()->RemoveDir(uninstallDir)) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "delete dir %{public}s failed", uninstallDir.c_str());
        return errCode;
    }
    if (!dataMgr_->UpdateBundleInstallState(bundleName, InstallState::UNINSTALL_SUCCESS)) {
        LOG_E(BMS_TAG_INSTALLER, "update uninstall success failed");
        return ERR_APPEXECFWK_UPDATE_BUNDLE_INSTALL_STATUS_ERROR;
    }
    (void)InstalldClient::GetInstance()->RemoveDir(AOTHandler::BuildSharedArkCachePath(bundleName));
    // delete router map for hsp
    if (!dataMgr_->DeleteRouterInfo(bundleName)) {
        LOG_W(BMS_TAG_INSTALLER, "bundleName: %{public}s delete router map failed", bundleName.c_str());
    }
    if (!DelayedSingleton<AppProvisionInfoManager>::GetInstance()->DeleteAppProvisionInfo(bundleName)) {
        LOG_W(BMS_TAG_INSTALLER, "bundleName: %{public}s delete appProvisionInfo failed", bundleName.c_str());
    }
    userId_ = Constants::ALL_USERID;
    PerfProfile::GetInstance().SetBundleUninstallEndTime(GetTickCount());
    /* remove sign profile from code signature for cross-app hsp */
    RemoveProfileFromCodeSign(bundleName);
    return ERR_OK;
}

ErrCode BaseBundleInstaller::UninstallHspVersion(std::string &uninstallDir, int32_t versionCode, InnerBundleInfo &info)
{
    LOG_D(BMS_TAG_INSTALLER, "begin to process hsp bundle %{public}s uninstall", info.GetBundleName().c_str());
    // remove bundle dir first, then delete data in innerBundleInfo
    ErrCode errCode;
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_NULL_PTR;
    }
    if (!dataMgr_->UpdateBundleInstallState(info.GetBundleName(), InstallState::UNINSTALL_START)) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall start failed");
        return ERR_APPEXECFWK_UPDATE_BUNDLE_INSTALL_STATUS_ERROR;
    }
    if ((errCode = InstalldClient::GetInstance()->RemoveDir(uninstallDir)) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "delete dir %{public}s failed", uninstallDir.c_str());
        return errCode;
    }
    if (versionCode == info.GetVersionCode()) {
        info.ResetAOTFlags();
        (void)InstalldClient::GetInstance()->RemoveDir(AOTHandler::BuildSharedArkCachePath(info.GetBundleName()));
    } else {
        std::string versionAnDir = AOTHandler::BuildSharedArkCachePath(
            info.GetBundleName(), static_cast<uint32_t>(versionCode));
        (void)InstalldClient::GetInstance()->RemoveDir(versionAnDir);
    }
    if (!dataMgr_->RemoveHspModuleByVersionCode(versionCode, info)) {
        LOG_E(BMS_TAG_INSTALLER, "remove hsp module by versionCode failed");
        return ERR_APPEXECFWK_RMV_HSP_BY_VERSION_ERROR;
    }
    if (!dataMgr_->UpdateBundleInstallState(info.GetBundleName(), InstallState::INSTALL_SUCCESS)) {
        LOG_E(BMS_TAG_INSTALLER, "update install success failed");
        return ERR_APPEXECFWK_UPDATE_BUNDLE_INSTALL_STATUS_ERROR;
    }

    userId_ = Constants::ALL_USERID;
    PerfProfile::GetInstance().SetBundleUninstallEndTime(GetTickCount());
    return ERR_OK;
}

ErrCode BaseBundleInstaller::UninstallHspAndBundle(InnerBundleInfo &info, int32_t &versionCode,
    std::string &uninstallDir)
{
    sysEventInfo_.startTime = BundleUtil::GetCurrentTimeMs();
    std::vector<uint32_t> versionCodes = info.GetAllHspVersion();
    InstallParam installParam;
    ErrCode ret = ERR_OK;
    if ((versionCodes.size() > SINGLE_HSP_VERSION && versionCode == Constants::ALL_VERSIONCODE) ||
        versionCodes.size() == SINGLE_HSP_VERSION) {
        ErrCode ret = UninstallHspBundle(uninstallDir, info.GetBundleName());
        SendBundleSystemEvent(info.GetBundleName(), BundleEventType::UNINSTALL, installParam,
            sysEventInfo_.preBundleScene, ret);
        return ret;
    }
    uninstallDir += std::string(ServiceConstants::PATH_SEPARATOR) +
        HSP_VERSION_PREFIX + std::to_string(versionCode);
    ret = UninstallHspVersion(uninstallDir, versionCode, info);
    SendBundleSystemEvent(info.GetBundleName(), BundleEventType::UNINSTALL, installParam,
        sysEventInfo_.preBundleScene, ret);
    return ret;
}

ErrCode BaseBundleInstaller::UninstallBundle(
    const std::string &bundleName, const std::string &modulePackage, const InstallParam &installParam)
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    CheckSystemFreeSizeAndClean();
    LOG_I(BMS_TAG_INSTALLER, "begin to process %{public}s module in %{public}s uninstall",
        modulePackage.c_str(), bundleName.c_str());
    PerfProfile::GetInstance().SetBundleUninstallStartTime(GetTickCount());
    sysEventInfo_.startTime = BundleUtil::GetCurrentTimeMs();

    std::string developerId;
    std::string assetAccessGroups;
    std::vector<std::string> moduleNames;
    GetModuleNames(bundleName, moduleNames);
    if (moduleNames.size() == 1) {
        developerId = GetDeveloperId(bundleName);
        assetAccessGroups = GetAssetAccessGroups(bundleName);
    }
    // uninstall all sandbox app before
    UninstallAllSandboxApps(bundleName, installParam.userId);

    int32_t uid = Constants::INVALID_UID;
    bool isUninstalledFromBmsExtension = false;
    ErrCode result = ProcessBundleUninstall(bundleName, modulePackage, installParam, uid);
    if (result == ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_BUNDLE) {
        ErrCode bmsExtensionResult = UninstallBundleFromBmsExtension(bundleName);
        if (bmsExtensionResult == ERR_OK) {
            isUninstalledFromBmsExtension = true;
            result = ERR_OK;
        } else if (bmsExtensionResult == ERR_APPEXECFWK_UNINSTALL_DISPOSED_RULE_FAILED) {
            result = ERR_APPEXECFWK_UNINSTALL_DISPOSED_RULE_FAILED;
        }
    }
    std::map<std::string, std::string> tokenIdMetadataInfos;
    tokenIdMetadataInfos[ServiceConstants::META_KEEP_TOKEN_ID_KEY] = isKeepTokenId_ ? BMS_TRUE : BMS_FALSE;
    if (installParam.needSendEvent && dataMgr_) {
        NotifyBundleEvents installRes = {
            .isAgingUninstall = installParam.isAgingUninstall,
            .isBmsExtensionUninstalled = isUninstalledFromBmsExtension,
            .type = NotifyType::UNINSTALL_MODULE,
            .resultCode = result,
            .accessTokenId = accessTokenId_,
            .uid = uid,
            .bundleType = static_cast<int32_t>(bundleType_),
            .bundleName = bundleName,
            .modulePackage = modulePackage,
            .appId = uninstallBundleAppId_,
            .appDistributionType = appDistributionType_,
            .developerId = developerId,
            .assetAccessGroups = assetAccessGroups,
            .keepData = installParam.isKeepData,
            .isBundleExist = isBundleExist_,
            .crossAppSharedConfig = isBundleCrossAppSharedConfig_,
            .metadataConfigInfos = tokenIdMetadataInfos
        };
        if (NotifyBundleStatus(installRes) != ERR_OK) {
            LOG_W(BMS_TAG_INSTALLER, "notify status failed for installation");
        }
    }

    SendBundleSystemEvent(bundleName, BundleEventType::UNINSTALL, installParam, sysEventInfo_.preBundleScene, result);
    PerfProfile::GetInstance().SetBundleUninstallEndTime(GetTickCount());
    LOG_D(BMS_TAG_INSTALLER, "finish uninstall %{public}s in %{public}s", modulePackage.c_str(), bundleName.c_str());
    return result;
}

bool BaseBundleInstaller::UninstallAppControl(
    const std::string &appId, const std::string &appIdentifier, int32_t userId)
{
#ifdef BUNDLE_FRAMEWORK_APP_CONTROL
    std::vector<std::string> appIds;
    ErrCode ret = DelayedSingleton<AppControlManager>::GetInstance()->GetAppInstallControlRule(
        AppControlConstants::EDM_CALLING, AppControlConstants::APP_DISALLOWED_UNINSTALL, userId, appIds);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "GetAppInstallControlRule failed code:%{public}d", ret);
        return true;
    }
    if (!appIdentifier.empty() && std::find(appIds.begin(), appIds.end(), appIdentifier) != appIds.end()) {
        LOG_W(BMS_TAG_INSTALLER, "appIdentifier :%{public}s is not removable", appIdentifier.c_str());
        return false;
    }
    if (std::find(appIds.begin(), appIds.end(), appId) != appIds.end()) {
        LOG_W(BMS_TAG_INSTALLER, "appId :%{public}s is not removable", appId.c_str());
        return false;
    }
    return true;
#else
    LOG_W(BMS_TAG_INSTALLER, "appId is not removable");
    return true;
#endif
}

ErrCode BaseBundleInstaller::InstallNormalAppControl(
    const std::string &installAppId,
    const std::string &appIdentifier,
    int32_t userId,
    bool isPreInstallApp)
{
    LOG_D(BMS_TAG_INSTALLER, "InstallNormalAppControl start ");
#ifdef BUNDLE_FRAMEWORK_APP_CONTROL
    if (isPreInstallApp) {
        LOG_D(BMS_TAG_INSTALLER, "the preInstalled app does not support app control feature");
        return ERR_OK;
    }
    std::vector<std::string> allowedAppIds;
    ErrCode ret = DelayedSingleton<AppControlManager>::GetInstance()->GetAppInstallControlRule(
        AppControlConstants::EDM_CALLING, AppControlConstants::APP_ALLOWED_INSTALL, userId, allowedAppIds);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "GetAppInstallControlRule allowedInstall failed code:%{public}d", ret);
        return ret;
    }

    std::vector<std::string> disallowedAppIds;
    ret = DelayedSingleton<AppControlManager>::GetInstance()->GetAppInstallControlRule(
        AppControlConstants::EDM_CALLING, AppControlConstants::APP_DISALLOWED_INSTALL, userId, disallowedAppIds);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "GetAppInstallControlRule disallowedInstall failed code:%{public}d", ret);
        return ret;
    }

    // disallowed list and allowed list all empty.
    if (disallowedAppIds.empty() && allowedAppIds.empty()) {
        return ERR_OK;
    }

    return CheckInstallPermission(installAppId, appIdentifier, allowedAppIds, disallowedAppIds);
#else
    LOG_W(BMS_TAG_INSTALLER, "app control is disable");
    return ERR_OK;
#endif
}

#ifdef BUNDLE_FRAMEWORK_APP_CONTROL
ErrCode BaseBundleInstaller::CheckInstallPermission(
    const std::string &appId,
    const std::string &appIdentifier,
    const std::vector<std::string> &allowedAppIds,
    const std::vector<std::string> &disallowedAppIds)
{
    LOG_D(BMS_TAG_INSTALLER, "CheckInstallPermission start ");
    std::string targetId = appIdentifier.empty() ? appId : appIdentifier;
    // only allowed list empty.
    if (allowedAppIds.empty()) {
        if (std::find(disallowedAppIds.begin(), disallowedAppIds.end(), targetId) != disallowedAppIds.end()) {
            LOG_E(BMS_TAG_INSTALLER, "disallowedAppIds:%{public}s is disallow install", targetId.c_str());
            return ERR_BUNDLE_MANAGER_APP_CONTROL_DISALLOWED_INSTALL;
        }
        if (!appIdentifier.empty() &&
            std::find(disallowedAppIds.begin(), disallowedAppIds.end(), appId) != disallowedAppIds.end()) {
            LOG_E(BMS_TAG_INSTALLER, "disallowedAppIds:%{public}s is disallow install", appId.c_str());
            return ERR_BUNDLE_MANAGER_APP_CONTROL_DISALLOWED_INSTALL;
        }
        return ERR_OK;
    }

    // only disallowed list empty.
    if (disallowedAppIds.empty()) {
        if (std::find(allowedAppIds.begin(), allowedAppIds.end(), targetId) != allowedAppIds.end()) {
            return ERR_OK;
        }
        if (!appIdentifier.empty() &&
            std::find(allowedAppIds.begin(), allowedAppIds.end(), appId) != allowedAppIds.end()) {
            return ERR_OK;
        }
        LOG_E(BMS_TAG_INSTALLER, "allowedAppIds:%{public}s is disallow install", targetId.c_str());
        return ERR_BUNDLE_MANAGER_APP_CONTROL_DISALLOWED_INSTALL;
    }

    // disallowed list and allowed list all not empty.
    bool inDisallowed = std::find(disallowedAppIds.begin(), disallowedAppIds.end(), targetId) != disallowedAppIds.end();
    bool inAllowed = std::find(allowedAppIds.begin(), allowedAppIds.end(), targetId) != allowedAppIds.end();
    if (!appIdentifier.empty()) {
        if (!inAllowed || inDisallowed) {
            LOG_E(BMS_TAG_INSTALLER, "%{public}s is disallow install", targetId.c_str());
            return ERR_BUNDLE_MANAGER_APP_CONTROL_DISALLOWED_INSTALL;
        }
        return ERR_OK;
    }
    inDisallowed = std::find(disallowedAppIds.begin(), disallowedAppIds.end(), appId) != disallowedAppIds.end();
    inAllowed = std::find(allowedAppIds.begin(), allowedAppIds.end(), appId) != allowedAppIds.end();
    if (!inAllowed || inDisallowed) {
        LOG_E(BMS_TAG_INSTALLER, "%{public}s is disallow install", targetId.c_str());
        return ERR_BUNDLE_MANAGER_APP_CONTROL_DISALLOWED_INSTALL;
    }
    return ERR_OK;
}
#endif

void BaseBundleInstaller::UpdateInstallerState(const InstallerState state)
{
    LOG_D(BMS_TAG_INSTALLER, "UpdateInstallerState in BaseBundleInstaller state %{public}d",
        static_cast<int32_t>(state));
    SetInstallerState(state);
}

void BaseBundleInstaller::SaveOldRemovableInfo(
    InnerModuleInfo &newModuleInfo, InnerBundleInfo &oldInfo, bool existModule)
{
    if (existModule) {
        // save old module useId isRemovable info to new module
        auto oldModule = oldInfo.FetchInnerModuleInfos().find(newModuleInfo.modulePackage);
        if (oldModule == oldInfo.FetchInnerModuleInfos().end()) {
            LOG_E(BMS_TAG_INSTALLER, "can not find module %{public}s in oldInfo", newModuleInfo.modulePackage.c_str());
            return;
        }
        newModuleInfo.isRemovableSet = oldModule->second.isRemovableSet;
        for (const auto &remove : oldModule->second.isRemovable) {
            auto result = newModuleInfo.isRemovable.try_emplace(remove.first, remove.second);
            if (!result.second) {
                LOG_E(BMS_TAG_INSTALLER, "%{public}s removable add %{public}s from old:%{public}d failed",
                    newModuleInfo.modulePackage.c_str(), remove.first.c_str(), remove.second);
            }
            LOG_D(BMS_TAG_INSTALLER, "%{public}s removable add %{public}s from old:%{public}d",
                newModuleInfo.modulePackage.c_str(), remove.first.c_str(), remove.second);
        }
    }
}

void BaseBundleInstaller::CheckEnableRemovable(std::unordered_map<std::string, InnerBundleInfo> &newInfos,
    InnerBundleInfo &oldInfo, int32_t &userId, bool isFreeInstallFlag, bool isAppExist)
{
    for (auto &item : newInfos) {
        std::map<std::string, InnerModuleInfo> &moduleInfo = item.second.FetchInnerModuleInfos();
        bool hasInstalledInUser = oldInfo.HasInnerBundleUserInfo(userId);
        // now there are three cases for set haps isRemovable true:
        // 1. FREE_INSTALL flag
        // 2. bundle not exist in current user
        // 3. bundle exist, hap not exist
        // 4. hap exist not in current userId
        for (auto &iter : moduleInfo) {
            LOG_D(BMS_TAG_INSTALLER, "%{public}s, %{public}d, %{public}d, %{public}d",
                iter.second.modulePackage.c_str(), userId, isFreeInstallFlag, isAppExist);
            bool existModule = oldInfo.FindModule(iter.second.modulePackage);
            bool hasModuleInUser = item.second.IsUserExistModule(iter.second.moduleName, userId);
            LOG_D(BMS_TAG_INSTALLER, "%{public}d, (%{public}d), (%{public}d)",
                hasInstalledInUser, existModule, hasModuleInUser);
            if (isFreeInstallFlag && (!isAppExist || !hasInstalledInUser || !existModule || !hasModuleInUser)) {
                LOG_D(BMS_TAG_INSTALLER, "%{public}d, %{public}d (%{public}d)",
                    hasInstalledInUser, isAppExist, existModule);
                item.second.SetModuleRemovable(iter.second.moduleName, true, userId);
                SaveOldRemovableInfo(iter.second, oldInfo, existModule);
            }
        }
    }
}

bool BaseBundleInstaller::CheckDuplicateProxyData(const InnerBundleInfo &newInfo,
    const InnerBundleInfo &oldInfo)
{
    std::vector<ProxyData> proxyDatas;
    oldInfo.GetAllProxyDataInfos(proxyDatas);
    newInfo.GetAllProxyDataInfos(proxyDatas);
    return CheckDuplicateProxyData(proxyDatas);
}

bool BaseBundleInstaller::CheckDuplicateProxyData(const std::unordered_map<std::string, InnerBundleInfo> &newInfos)
{
    std::vector<ProxyData> proxyDatas;
    for (const auto &innerBundleInfo : newInfos) {
        innerBundleInfo.second.GetAllProxyDataInfos(proxyDatas);
    }
    return CheckDuplicateProxyData(proxyDatas);
}

bool BaseBundleInstaller::CheckDuplicateProxyData(const std::vector<ProxyData> &proxyDatas)
{
    std::set<std::string> uriSet;
    for (const auto &proxyData : proxyDatas) {
        if (!uriSet.insert(proxyData.uri).second) {
            LOG_E(BMS_TAG_INSTALLER, "uri %{public}s in proxyData is duplicated", proxyData.uri.c_str());
            return false;
        }
    }
    return true;
}

ErrCode BaseBundleInstaller::InnerProcessBundleInstall(std::unordered_map<std::string, InnerBundleInfo> &newInfos,
    InnerBundleInfo &oldInfo, const InstallParam &installParam, int32_t &uid)
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    LOG_NOFUNC_I(BMS_TAG_INSTALLER,
        "InnerProcessBundleInstall -n %{public}s -u %{public}d -f %{public}hhd isAppExist:%{public}d",
        bundleName_.c_str(), userId_, installParam.installFlag, isAppExist_);
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_NULL_PTR;
    }
    SetOldAppIsEncrypted(oldInfo);

    KillRelatedProcessIfArkWeb(installParam.isOTA);
    ErrCode result = ERR_OK;
    result = CheckAppService(newInfos.begin()->second, oldInfo, isAppExist_);
    CHECK_RESULT(result, "Check appService failed %{public}d");
    bool u1Enable = false;
    result = bundleInstallChecker_->CheckU1EnableSameInHaps(newInfos, bundleName_, u1Enable);
    CHECK_RESULT(result, "Check u1Enable in haps same failed %{public}d");

    if (installParam.needSavePreInstallInfo) {
        PreInstallBundleInfo preInstallBundleInfo;
        preInstallBundleInfo.SetBundleName(bundleName_);
        dataMgr_->GetPreInstallBundleInfo(bundleName_, preInstallBundleInfo);
        preInstallBundleInfo.SetAppType(newInfos.begin()->second.GetAppType());
        preInstallBundleInfo.SetVersionCode(bundleInstallChecker_->GetVersionCode(newInfos));
        preInstallBundleInfo.SetIsUninstalled(false);
        preInstallBundleInfo.DeleteForceUnisntalledUser(userId_);
        for (const auto &item : newInfos) {
            preInstallBundleInfo.AddBundlePath(item.first);
        }
#ifdef USE_PRE_BUNDLE_PROFILE
        preInstallBundleInfo.SetRemovable(installParam.removable);
#else
        preInstallBundleInfo.SetRemovable(newInfos.begin()->second.IsRemovable());
#endif
        for (const auto &innerBundleInfo : newInfos) {
            auto applicationInfo = innerBundleInfo.second.GetBaseApplicationInfo();
            if (innerBundleInfo.second.HasEntry() || preInstallBundleInfo.GetModuleName().empty()) {
                innerBundleInfo.second.AdaptMainLauncherResourceInfo(applicationInfo);
                preInstallBundleInfo.SetLabelId(applicationInfo.labelResource.id);
                preInstallBundleInfo.SetIconId(applicationInfo.iconResource.id);
                preInstallBundleInfo.SetModuleName(applicationInfo.labelResource.moduleName);
            }
            preInstallBundleInfo.SetSystemApp(applicationInfo.isSystemApp);
            auto moduleMap = innerBundleInfo.second.GetInnerModuleInfos();
            if (innerBundleInfo.second.GetIsNewVersion()) {
                preInstallBundleInfo.SetBundleType(innerBundleInfo.second.GetApplicationBundleType());
            } else if (!moduleMap.empty() && moduleMap.begin()->second.distro.installationFree) {
                preInstallBundleInfo.SetBundleType(BundleType::ATOMIC_SERVICE);
            }
            if (!moduleMap.empty() && moduleMap.begin()->second.distro.moduleType == Profile::MODULE_TYPE_ENTRY) {
                break;
            }
        }
        preInstallBundleInfo.SetU1Enable(u1Enable);
        dataMgr_->SavePreInstallBundleInfo(bundleName_, preInstallBundleInfo);
    } else {
        // remove userid record in preinstallbundleinfo
        PreInstallBundleInfo preInstallBundleInfo;
        preInstallBundleInfo.SetBundleName(bundleName_);
        if (dataMgr_->GetPreInstallBundleInfo(bundleName_, preInstallBundleInfo)) {
            preInstallBundleInfo.DeleteForceUnisntalledUser(userId_);
            if (!dataMgr_->SavePreInstallBundleInfo(bundleName_, preInstallBundleInfo)) {
                LOG_NOFUNC_E(BMS_TAG_DEFAULT, "update preinstall DB fail -n %{public}s", bundleName_.c_str());
            }
        }
    }

    result = CheckSingleton(newInfos.begin()->second, userId_);
    CHECK_RESULT(result, "Check singleton failed %{public}d");

    result = CheckU1Enable(newInfos.begin()->second, userId_);
    CHECK_RESULT(result, "Check u1Enable failed %{public}d");

    bool isFreeInstallFlag = (installParam.installFlag == InstallFlag::FREE_INSTALL);
    CheckEnableRemovable(newInfos, oldInfo, userId_, isFreeInstallFlag, isAppExist_);
    // check MDM self update
    result = CheckMDMUpdateBundleForSelf(installParam, oldInfo, newInfos, isAppExist_);
    CHECK_RESULT(result, "update MDM app failed %{public}d");

    GetExtensionDirsChange(newInfos, oldInfo);

    if (isAppExist_) {
        (void)InstalldClient::GetInstance()->RemoveDir(ServiceConstants::HAP_ARK_CACHE_PATH + oldInfo.GetBundleName());
        SetAtomicServiceModuleUpgrade(oldInfo);
        if (oldInfo.GetApplicationBundleType() == BundleType::SHARED) {
            LOG_E(BMS_TAG_INSTALLER, "old bundle info is shared package");
            return ERR_APPEXECFWK_INSTALL_COMPATIBLE_POLICY_NOT_SAME;
        }

        result = CheckInstallationFree(oldInfo, newInfos);
        CHECK_RESULT(result, "CheckInstallationFree failed %{public}d");
        // to guarantee that the hap version can be compatible.
        result = CheckVersionCompatibility(oldInfo);
        CheckInstallAllowDowngrade(installParam, oldInfo, result);
        CHECK_RESULT(result, "The app has been installed and update lower version bundle %{public}d");
        // to check native file between oldInfo and newInfos.
        result = CheckNativeFileWithOldInfo(oldInfo, newInfos);
        CHECK_RESULT(result, "Check native so between oldInfo and newInfos failed %{public}d");

        for (auto &info : newInfos) {
            std::string packageName = info.second.GetCurrentModulePackage();
            if (oldInfo.FindModule(packageName)) {
                installedModules_[packageName] = true;
            }
        }

        hasInstalledInUser_ = oldInfo.HasInnerBundleUserInfo(userId_);
        if (!hasInstalledInUser_) {
            if (AccountHelper::CheckOsAccountConstraintEnabled(userId_, ServiceConstants::CONSTRAINT_APPS_INSTALL)) {
                APP_LOGE("user %{public}d is not allowed to install app", userId_);
                return ERR_APPEXECFWK_INSTALL_FAILED_ACCOUNT_CONSTRAINT;
            }
            if (!CheckInstallOnKeepData(bundleName_, installParam.isOTA, newInfos)) {
                LOG_E(BMS_TAG_INSTALLER, "check failed");
                return ERR_APPEXECFWK_INSTALL_FAILED_INCONSISTENT_SIGNATURE;
            }
            LOG_D(BMS_TAG_INSTALLER, "new userInfo with bundleName %{public}s and userId %{public}d",
                bundleName_.c_str(), userId_);
            InnerBundleUserInfo newInnerBundleUserInfo;
            newInnerBundleUserInfo.bundleUserInfo.userId = userId_;
            newInnerBundleUserInfo.bundleName = bundleName_;
            oldInfo.AddInnerBundleUserInfo(newInnerBundleUserInfo);
            ScopeGuard userGuard([&] {
                if (!dataMgr_->GetUninstallBundleInfoWithUserAndAppIndex(bundleName_, userId_,
                    Constants::INITIAL_APP_INDEX)) {
                    RemoveBundleUserData(oldInfo, false);
                }
            });
            Security::AccessToken::AccessTokenIDEx accessTokenIdEx;
            Security::AccessToken::HapInfoCheckResult checkResult;
            isKeepTokenId_ = oldInfo.HasKeepTokenIdMetadata();
            if (!RecoverHapToken(bundleName_, userId_, accessTokenIdEx, oldInfo, false)
                && BundlePermissionMgr::InitHapToken(oldInfo, userId_, 0, accessTokenIdEx, checkResult,
                verifyRes_.GetProvisionInfo().appServiceCapabilities, false) != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLER, "bundleName:%{public}s InitHapToken failed", bundleName_.c_str());
                SetVerifyPermissionResult(checkResult);
                return ERR_APPEXECFWK_INSTALL_GRANT_REQUEST_PERMISSIONS_FAILED;
            }
            accessTokenId_ = accessTokenIdEx.tokenIdExStruct.tokenID;
            oldInfo.SetAccessTokenIdEx(accessTokenIdEx, userId_);
            result = CreateBundleUserData(oldInfo);
            CHECK_RESULT(result, "CreateBundleUserData failed %{public}d");

            if (!isFeatureNeedUninstall_) {
                // extract ap file in old haps
                result = ExtractAllArkProfileFile(oldInfo, true);
                CHECK_RESULT(result, "ExtractAllArkProfileFile failed %{public}d");
            }

            userGuard.Dismiss();
        }
        ErrCode res = CleanShaderCache(oldInfo, bundleName_, userId_);
        if (res != ERR_OK) {
            LOG_NOFUNC_I(BMS_TAG_INSTALLER, "%{public}s clean shader fail %{public}d", bundleName_.c_str(), res);
        }
        res = CleanArkStartupCache(bundleName_);
        if (res != ERR_OK) {
            LOG_NOFUNC_I(BMS_TAG_INSTALLER, "%{public}s clean ark startup cache fail %{public}d, try again",
                bundleName_.c_str(), res);
            res = CleanArkStartupCache(bundleName_);
            if (res != ERR_OK && installParam.isPatch) {
                CHECK_RESULT(res, "CleanArkStartupCache failed %{public}d");
            }
            if (res != ERR_OK) {
                LOG_NOFUNC_E(BMS_TAG_INSTALLER, "%{public}s clean ark startup cache fail %{public}d",
                    bundleName_.c_str(), res);
            }
        }
    }
    bool isDebugGrant = CheckIsDebugGrant(installParam, newInfos.begin()->second.GetAppProvisionType());
    auto it = newInfos.begin();
    if (!isAppExist_) {
        if (AccountHelper::CheckOsAccountConstraintEnabled(userId_, ServiceConstants::CONSTRAINT_APPS_INSTALL)) {
            APP_LOGE("user %{public}d is not allowed to install app", userId_);
            return ERR_APPEXECFWK_INSTALL_FAILED_ACCOUNT_CONSTRAINT;
        }
        if (!CheckInstallOnKeepData(bundleName_, installParam.isOTA, newInfos)) {
            LOG_E(BMS_TAG_INSTALLER, "check failed");
            return ERR_APPEXECFWK_INSTALL_FAILED_INCONSISTENT_SIGNATURE;
        }
        InnerBundleInfo &newInfo = it->second;
        modulePath_ = it->first;
        InnerBundleUserInfo newInnerBundleUserInfo;
        newInnerBundleUserInfo.bundleUserInfo.userId = userId_;
        newInnerBundleUserInfo.bundleName = bundleName_;
        newInfo.AddInnerBundleUserInfo(newInnerBundleUserInfo);
        newInfo.SetIsFreeInstallApp(InstallFlag::FREE_INSTALL == installParam.installFlag);
        SetApplicationFlagsAndInstallSource(newInfos, installParam);
        result = ProcessBundleInstallStatus(newInfo, uid, isDebugGrant);
        CHECK_RESULT(result, "ProcessBundleInstallStatus failed %{public}d");

        it++;
        hasInstalledInUser_ = true;
    }

    InnerBundleInfo bundleInfo;
    if (!GetTempBundleInfo(bundleInfo)) {
        return ERR_APPEXECFWK_GET_INSTALL_TEMP_BUNDLE_ERROR;
    }
    bool isOldSystemApp = bundleInfo.IsSystemApp();

    InnerBundleUserInfo innerBundleUserInfo;
    if (!bundleInfo.GetInnerBundleUserInfo(userId_, innerBundleUserInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "oldInfo do not have user");
        return ERR_APPEXECFWK_USER_NOT_EXIST;
    }
    CreateExtensionDataDir(bundleInfo);
    // update selinux apl for old extension dirs
    std::vector<std::string> updateSelinuxAplExtensionDirs = oldInfo.GetAllExtensionDirs();
    std::unordered_set<std::string> toRemove(removeExtensionDirs_.begin(), removeExtensionDirs_.end());
    updateSelinuxAplExtensionDirs.erase(
        std::remove_if(updateSelinuxAplExtensionDirs.begin(), updateSelinuxAplExtensionDirs.end(),
            [&toRemove](std::string x) {
                return toRemove.find(x) != toRemove.end();
            }),
        updateSelinuxAplExtensionDirs.end()
    );
    UpdateExtensionDirsApl(updateSelinuxAplExtensionDirs, oldInfo);
    ScopeGuard userGuard([&] {
        if ((!hasInstalledInUser_ || (!isAppExist_)) &&
            !dataMgr_->GetUninstallBundleInfoWithUserAndAppIndex(bundleName_, userId_, Constants::INITIAL_APP_INDEX)) {
            RemoveBundleUserData(oldInfo, false);
        }
    });
    (void)InnerProcessCodePathCreateNewDir(bundleName_, isFeatureNeedUninstall_);

    // update haps
    for (; it != newInfos.end(); ++it) {
        // install entry module firstly
        LOG_D(BMS_TAG_INSTALLER, "update module %{public}s, entry module packageName is %{public}s",
            it->second.GetCurrentModulePackage().c_str(), entryModuleName_.c_str());
        if ((result = InstallEntryMoudleFirst(newInfos, bundleInfo, innerBundleUserInfo,
            installParam)) != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "install entry module failed due to error %{public}d", result);
            break;
        }
        if (it->second.GetCurrentModulePackage().compare(entryModuleName_) == 0) {
            LOG_D(BMS_TAG_INSTALLER, "enrty has been installed");
            continue;
        }
        modulePath_ = it->first;
        InnerBundleInfo &newInfo = it->second;
        newInfo.AddInnerBundleUserInfo(innerBundleUserInfo);
        bool isReplace = (installParam.installFlag == InstallFlag::REPLACE_EXISTING ||
            installParam.installFlag == InstallFlag::FREE_INSTALL);
        // app exist, but module may not
        if ((result = ProcessBundleUpdateStatus(
            bundleInfo, newInfo, isReplace, installParam.GetKillProcess())) != ERR_OK) {
            break;
        }
    }
    if (result == ERR_OK) {
        result = InnerProcessUpdateHapToken(isOldSystemApp, isDebugGrant);
        CHECK_RESULT(result, "InnerProcessUpdateHapToken failed %{public}d");
    }

    if (result == ERR_OK) {
        userGuard.Dismiss();
    }

    uid = bundleInfo.GetUid(userId_);
    if (bundleInfo.GetApplicationBundleType() == BundleType::ATOMIC_SERVICE) {
        std::string bundleDataDir = ServiceConstants::BUNDLE_APP_DATA_BASE_DIR + ServiceConstants::BUNDLE_EL[0]
            + ServiceConstants::PATH_SEPARATOR + std::to_string(userId_) + ServiceConstants::BASE +
            bundleInfo.GetBundleName();
        PrepareBundleDirQuota(bundleInfo.GetBundleName(), uid, bundleDataDir,
            ATOMIC_SERVICE_DATASIZE_THRESHOLD_MB_PRESET);
        VerifyDelayedAging(bundleInfo, uid);
        tempInfo_.SetTempBundleInfo(bundleInfo);
    }
    mainAbility_ = bundleInfo.GetMainAbility();
    return result;
}

void BaseBundleInstaller::VerifyDelayedAging(InnerBundleInfo &bundleInfo, int32_t uid)
{
    LOG_D(BMS_TAG_INSTALLER, "Start verifying if there is a delay in aging");
    bool isDelayedAging = false;
    if (BundlePermissionMgr::VerifyPermission(bundleInfo.GetBundleName(), ServiceConstants::PERMISSION_MANAGE_AGING,
        uid / Constants::BASE_USER_RANGE) != Security::AccessToken::PermissionState::PERMISSION_GRANTED) {
        APP_LOGD("no permission to delay aging %{public}s", bundleInfo.GetBundleName().c_str());
        bundleInfo.SetDelayedAging(isDelayedAging);
        return;
    } else {
        isDelayedAging = true;
        bundleInfo.SetDelayedAging(isDelayedAging);
    }
    LOG_D(BMS_TAG_INSTALLER, "Verification of delayed aging permissions successful");
    return;
}

void BaseBundleInstaller::ProcessUpdateShortcut()
{
    if (!isAppExist_ || !InitDataMgr()) {
        return;
    }
    dataMgr_->UpdateShortcutInfos(bundleName_);
}

ErrCode BaseBundleInstaller::InnerProcessUpdateHapToken(const bool isOldSystemApp, const bool isDebugGrant)
{
    InnerBundleInfo newBundleInfo;
    if (!GetTempBundleInfo(newBundleInfo)) {
        APP_LOGE("bundleName:%{public}s not exist", bundleName_.c_str());
        return ERR_APPEXECFWK_GET_INSTALL_TEMP_BUNDLE_ERROR;
    }
    std::vector<std::string> moduleVec = newBundleInfo.GetModuleNameVec();
    if (!isAppExist_ && (moduleVec.size() == 1)) {
        APP_LOGD("bundleName:%{public}s only has one module, no need update", bundleName_.c_str());
        return ERR_OK;
    }

    if (!uninstallModuleVec_.empty()) {
        for (const auto &package : moduleVec) {
            if (std::find(uninstallModuleVec_.begin(), uninstallModuleVec_.end(), package)
                == uninstallModuleVec_.end()) {
                newBundleInfo.SetInnerModuleNeedDelete(package, true);
            }
        }
    }
    ErrCode result = UpdateHapToken(isOldSystemApp != newBundleInfo.IsSystemApp(), newBundleInfo, isDebugGrant);
    if (result != ERR_OK) {
        APP_LOGE("bundleName:%{public}s update hapToken failed, errCode:%{public}d", bundleName_.c_str(), result);
        return result;
    }
    if (isAppExist_ && isModuleUpdate_) {
        result = SetDirApl(newBundleInfo);
        if (result != ERR_OK) {
            APP_LOGE("bundleName:%{public}s setDirApl failed:%{public}d", bundleName_.c_str(), result);
            return result;
        }
    }
    return ERR_OK;
}

void BaseBundleInstaller::SetAtomicServiceModuleUpgrade(const InnerBundleInfo &oldInfo)
{
    std::vector<std::string> moduleNames;
    oldInfo.GetModuleNames(moduleNames);
    for (const std::string &moduleName : moduleNames) {
        int32_t flag = static_cast<int32_t>(oldInfo.GetModuleUpgradeFlag(moduleName));
        if (flag) {
            atomicServiceModuleUpgrade_ = flag;
            return;
        }
    }
}

bool BaseBundleInstaller::IsArkWeb(const std::string &bundleName) const
{
    std::string arkWebName = OHOS::system::GetParameter(ARK_WEB_BUNDLE_NAME_PARAM, "");
    if (!arkWebName.empty()) {
        if (bundleName != arkWebName) {
            LOG_D(BMS_TAG_INSTALLER, "Bundle(%{public}s) is not arkweb", bundleName.c_str());
            return false;
        }
    } else {
        if (bundleName != NEW_ARK_WEB_BUNDLE_NAME && bundleName != OLD_ARK_WEB_BUNDLE_NAME) {
            LOG_I(BMS_TAG_INSTALLER, "Failed to get arkweb name and bundle name is %{public}s",
                bundleName.c_str());
            return false;
        }
    }
    LOG_I(BMS_TAG_INSTALLER, "%{public}s is arkweb", bundleName.c_str());
    return true;
}

bool BaseBundleInstaller::IsShellOrDevAssistant() const
{
    return sysEventInfo_.callingUid == ServiceConstants::SHELL_UID ||
        sysEventInfo_.callingUid == Constants::DEV_ASSISTANT_UID;
}

#ifdef WEBVIEW_ENABLE
ErrCode BaseBundleInstaller::VerifyArkWebInstall()
{
    if (!IsArkWeb(bundleName_)) {
        return ERR_OK;
    }
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_NULL_PTR;
    }
    InnerBundleInfo info;
    if (!GetTempBundleInfo(info)) {
        LOG_W(BMS_TAG_INSTALLER, "bundle info missing");
        return ERR_APPEXECFWK_GET_INSTALL_TEMP_BUNDLE_ERROR;
    }
    std::string hapPath = info.GetModuleHapPath(info.GetEntryModuleName());
    LOG_I(BMS_TAG_INSTALLER, "arkweb hapPath is %{public}s", hapPath.c_str());
    if (NWeb::AppFwkUpdateClient::GetInstance().VerifyPackageInstall(bundleName_, hapPath) != ERR_OK) {
        return ERR_APPEXECFWK_VERIFY_PKG_INSTALL_ERROR;
    }
    return ERR_OK;
}

void BaseBundleInstaller::RestoreconForArkweb()
{
    if (!OHOS::system::GetBoolParameter(APPSPAWN_PRELOAD_ARKWEB_ENGINE, false)) {
        LOG_D(BMS_TAG_INSTALLER, "APPSPAWN_PRELOAD_ARKWEB_ENGINE is false");
        return;
    }

    if (!IsArkWeb(bundleName_)) {
        return;
    }
    const std::string arkWebLibPath = std::string(APP_INSTALL_PATH) + "/public/" + bundleName_ + "/libs/arm64/";
    LOG_I(BMS_TAG_INSTALLER, "RestoreconPath, arkWebLibPath: %{public}s", arkWebLibPath.c_str());
    ErrCode result = InstalldClient::GetInstance()->RestoreconPath(arkWebLibPath);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "Failed to restorecon arkweb dir, error code: %{public}d", result);
    }
    NWeb::AppFwkUpdateClient::GetInstance().NotifyArkWebInstallSuccess();
}
#endif

void BaseBundleInstaller::KillRelatedProcessIfArkWeb(bool isOta)
{
    if (!isAppExist_ || isOta || !IsArkWeb(bundleName_)) {
        return;
    }
    auto appMgrClient = DelayedSingleton<AppMgrClient>::GetInstance();
    if (appMgrClient == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "AppMgrClient is nullptr, kill ark web process failed");
        return;
    }
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "KillRelatedProcessIfArkWeb begin");
    appMgrClient->KillProcessDependedOnWeb();
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "KillRelatedProcessIfArkWeb end");
}

ErrCode BaseBundleInstaller::CheckAppService(
    const InnerBundleInfo &newInfo, const InnerBundleInfo &oldInfo, bool isAppExist)
{
    if ((newInfo.GetApplicationBundleType() == BundleType::APP_SERVICE_FWK) && !isAppExist) {
        LOG_W(BMS_TAG_INSTALLER, "Not alloweded instal appService hap(%{public}s) due to the hsp does not exist",
            newInfo.GetBundleName().c_str());
        return ERR_APP_SERVICE_FWK_INSTALL_TYPE_FAILED;
    }

    if (isAppExist) {
        isAppService_ = oldInfo.GetApplicationBundleType() == BundleType::APP_SERVICE_FWK;
        if (isAppService_ && oldInfo.GetApplicationBundleType() != newInfo.GetApplicationBundleType()) {
            LOG_W(BMS_TAG_INSTALLER, "Bundle(%{public}s) type is not same", newInfo.GetBundleName().c_str());
            return ERR_APPEXECFWK_BUNDLE_TYPE_NOT_SAME;
        }
        if (isAppService_ && (oldInfo.GetVersionCode() < newInfo.GetVersionCode())) {
            LOG_W(BMS_TAG_INSTALLER, "upgrade must first upgrade the hsp, cannot upgrade hap first");
            return ERR_APP_SERVICE_FWK_INSTALL_TYPE_FAILED;
        }
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CheckSingleton(const InnerBundleInfo &info, const int32_t userId)
{
    if (isAppService_) {
        if (userId != Constants::DEFAULT_USERID) {
            LOG_NOFUNC_W(BMS_TAG_INSTALLER, "appService(%{public}s) only install U0", info.GetBundleName().c_str());
            return ERR_APPEXECFWK_INSTALL_ZERO_USER_WITH_NO_SINGLETON;
        }

        return ERR_OK;
    }
    // singleton app can only be installed in U0 and U0 can only install singleton app.
    bool isSingleton = info.IsSingleton();
    if ((isSingleton && (userId != Constants::DEFAULT_USERID)) ||
        (!isSingleton && (userId == Constants::DEFAULT_USERID))) {
        LOG_D(BMS_TAG_INSTALLER, "singleton(%{public}d) app(%{public}s) and user(%{public}d) are not matched",
            isSingleton, info.GetBundleName().c_str(), userId);
        return ERR_APPEXECFWK_INSTALL_ZERO_USER_WITH_NO_SINGLETON;
    }

    return ERR_OK;
}

ErrCode BaseBundleInstaller::CheckU1Enable(const InnerBundleInfo &info,
    const int32_t userId)
{
    LOG_D(BMS_TAG_INSTALLER, "start for -n %{public}s -u %{public}d",
        info.GetBundleName().c_str(), userId);
    std::string bundleName = info.GetBundleName();
    bool u1Enable = info.IsU1Enable();
    bool isU1 = (userId == Constants::U1);
    if ((u1Enable && !isU1) || (!u1Enable && isU1)) {
        LOG_E(BMS_TAG_INSTALLER, "u1Enable:%{public}d and userId:%{public}d not matched for %{public}s",
            u1Enable, userId, bundleName.c_str());
        return ERR_APPEXECFWK_INSTALL_U1ENABLE_CAN_ONLY_INSTALL_IN_U1_WITH_NOT_SINGLETON;
    }

    std::vector<int32_t> currentBundleUserIds = dataMgr_->GetUserIds(bundleName);
    bool onlyInstallInU1 = currentBundleUserIds.size() == 1 && currentBundleUserIds[0] == Constants::U1;
    if (u1Enable && isU1) {
        if (isAppExist_ && !onlyInstallInU1) {
            LOG_E(BMS_TAG_INSTALLER, "%{public}s existed in other users, but not u1", bundleName.c_str());
            return ERR_APPEXECFWK_INSTALL_BUNDLE_CAN_NOT_BOTH_EXISTED_IN_U1_AND_OTHER_USERS;
        }
    } else {
        // u1Enable is false and userid is not u1
        if (isAppExist_ && onlyInstallInU1) {
            LOG_E(BMS_TAG_INSTALLER, "%{public}s existed in u1, but u1Enable is false and userId is not u1",
                bundleName.c_str());
            return ERR_APPEXECFWK_INSTALL_BUNDLE_CAN_NOT_BOTH_EXISTED_IN_U1_AND_OTHER_USERS;
        }
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CheckEnterpriseResign(const InnerBundleInfo &oldInfo)
{
    if (oldInfo.GetAppSignType() != Constants::APP_SIGN_TYPE_ENTERPRISE_RE_SIGN) {
        return ERR_OK;
    }
    const std::vector<std::string> hapPaths = oldInfo.GetAllHapPaths();
    std::vector<Security::Verify::HapVerifyResult> hapVerifyRes;
    if (bundleInstallChecker_->CheckMultipleHapsSignInfo(hapPaths, hapVerifyRes, true, userId_) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "Check multiple haps sign info failed");
        return ERR_APPEXECFWK_INSTALL_FAILED_VERIFY_ENTERPRISE_RESIGN_FAIL;
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::ProcessBundleInstall(const std::vector<std::string> &inBundlePaths,
    const InstallParam &installParam, const Constants::AppType appType, int32_t &uid, bool isRecover)
{
    LOG_D(BMS_TAG_INSTALLER, "ProcessBundleInstall bundlePath install paths=%{private}s, hspPaths=%{private}s",
        GetJsonStrFromInfo(inBundlePaths).c_str(), GetJsonStrFromInfo(installParam.sharedBundleDirPaths).c_str());
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_UNINSTALL_BUNDLE_MGR_SERVICE_ERROR;
    }

    SharedBundleInstaller sharedBundleInstaller(installParam, appType);
    ErrCode result = sharedBundleInstaller.ParseFiles();
    CHECK_RESULT(result, "parse cross-app shared bundles failed %{public}d");

    if (inBundlePaths.empty() && sharedBundleInstaller.NeedToInstall()) {
        result = sharedBundleInstaller.Install(sysEventInfo_);
        bundleType_ = BundleType::SHARED;
        LOG_I(BMS_TAG_INSTALLER, "install cross-app shared bundles only, result : %{public}d", result);
        return result;
    }

    userId_ = GetUserId(installParam.userId);
    result = CheckUserId(userId_);
    CHECK_RESULT(result, "userId check failed %{public}d");

    std::vector<std::string> parsedPaths;
    result = ParseHapPaths(installParam, inBundlePaths, parsedPaths);
    CHECK_RESULT(result, "hap file parse failed %{public}d");

    std::vector<std::string> bundlePaths;
    // check hap paths
    result = BundleUtil::CheckFilePath(parsedPaths, bundlePaths);
    CHECK_RESULT(result, "hap file check failed %{public}d");
    UpdateInstallerState(InstallerState::INSTALL_BUNDLE_CHECKED);                  // ---- 5%

#ifdef OH_ADAPTER_ANDROID
    // adapter project: .apk dispatch before HAP signature pipeline.
    // bundlePaths now holds the resolved real paths of files inside tempDir_;
    // for bm install -p X.apk it is [.../HelloWorld.apk]. Route .apk
    // batches to libapk_installer.so + skip CheckMultipleHapsSignInfo / parse.
    {
        bool anyApk = false;
        for (const auto &p : bundlePaths) {
            if (p.size() > 4 && p.compare(p.size() - 4, 4, ".apk") == 0) {
                anyApk = true;
                break;
            }
        }
        if (anyApk) {
            LOG_I(BMS_TAG_INSTALLER, "adapter .apk path detected, dispatching");
            void* handle = dlopen("libapk_installer.so", RTLD_NOW | RTLD_GLOBAL);
            using ApkFn = int (*)(const char*, int);
            ApkFn fn = handle ? reinterpret_cast<ApkFn>(
                dlsym(handle, "oh_adapter_install_apk")) : nullptr;
            if (fn == nullptr) {
                LOG_E(BMS_TAG_INSTALLER, "oh_adapter_install_apk symbol unavailable");
                return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;
            }
            for (const auto &p : bundlePaths) {
                if (!(p.size() > 4 && p.compare(p.size() - 4, 4, ".apk") == 0)) continue;
                int r = fn(p.c_str(), installParam.userId);
                if (r != 0) {
                    LOG_E(BMS_TAG_INSTALLER, "oh_adapter_install_apk failed: %{public}d", r);
                    return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;
                }
            }
            LOG_I(BMS_TAG_INSTALLER, "adapter .apk install path completed");
            return ERR_OK;
        }
    }
#endif

#ifdef OH_ADAPTER_ANDROID
    // adapter project: .apk register with manifest v2
    // 1. dispatch to libapk_installer.so — manifest parse only (no fs writes)
    // 2. parse returned JSON manifest data
    // 3. route file deployment via InstalldClient IPC (installs domain)
    // 4. construct minimum-viable InnerBundleInfo
    // 5. dataMgr->AddInnerBundleInfo + SaveInnerBundleInfo (reboot-safe)
    {
        bool anyApk = false;
        for (const auto &p : bundlePaths) {
            if (p.size() > 4 && p.compare(p.size() - 4, 4, ".apk") == 0) {
                anyApk = true;
                break;
            }
        }
        if (anyApk) {
            LOG_I(BMS_TAG_INSTALLER, "adapter .apk path detected, dispatching+register v2");
            void* handle = dlopen("libapk_installer.so", RTLD_NOW | RTLD_GLOBAL);
            using ApkFn2 = int (*)(const char*, int, char*, int);
            ApkFn2 fn = handle ? reinterpret_cast<ApkFn2>(
                dlsym(handle, "oh_adapter_install_apk_with_manifest")) : nullptr;
            if (fn == nullptr) {
                LOG_E(BMS_TAG_INSTALLER, "oh_adapter_install_apk_with_manifest unavailable");
                return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;
            }
            constexpr int kJsonBufSize = 65536;
            std::vector<char> jsonBuf(kJsonBufSize, 0);
            for (const auto &p : bundlePaths) {
                if (!(p.size() > 4 && p.compare(p.size() - 4, 4, ".apk") == 0)) continue;
                int r = fn(p.c_str(), installParam.userId, jsonBuf.data(), kJsonBufSize);
                if (r != 0) {
                    LOG_E(BMS_TAG_INSTALLER, "oh_adapter_install_apk_with_manifest failed: %{public}d", r);
                    return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;
                }
                // Parse manifest JSON and register bundle.
                try {
                    auto j = nlohmann::json::parse(std::string(jsonBuf.data()));
                    std::string pkg = j.value("bundleName", std::string());
                    if (pkg.empty()) {
                        LOG_E(BMS_TAG_INSTALLER, "manifest JSON missing bundleName");
                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;
                    }
                    // adapter v2: route all fs writes via InstalldClient IPC (installs
                    // domain has write perms on data_app_el*_file; foundation does not).
                    // Previous design leaked direct fs writes from foundation and
                    // required setenforce 0 to actually work.
                    std::string bundleRoot   = j.value("bundleRoot", std::string("/data/app/el1/bundle/public/") + pkg);
                    std::string deployedDir  = j.value("deployedDir", bundleRoot + "/android");
                    std::string deployedApk  = j.value("deployedApkPath", deployedDir + "/base.apk");
                    std::string entryHapPath = j.value("entryHapPath", bundleRoot + "/entry.hap");
                    std::string srcApkPath   = j.value("srcApkPath", p);
                    auto installd = InstalldClient::GetInstance();
                    if (installd == nullptr) {
                        LOG_E(BMS_TAG_INSTALLER, "InstalldClient unavailable — cannot deploy APK");
                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;
                    }
                    // 1. Bundle root dir (data_app_el1_file label — CreateBundleDir
                    //    removes if exists, creates fresh. foundation-readable.)
                    ErrCode ec = installd->CreateBundleDir(bundleRoot);
                    if (ec != ERR_OK) {
                        LOG_E(BMS_TAG_INSTALLER, "CreateBundleDir(%{public}s) failed: %{public}d", bundleRoot.c_str(), ec);
                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;
                    }
                    // 2. 'android/' subdir — holds adapter-specific files (base.apk, lib/, oat/).
                    //    Owned root:root so app process can't tamper.
                    ec = installd->Mkdir(deployedDir, 0755, 0, 0);
                    if (ec != ERR_OK) {
                        LOG_E(BMS_TAG_INSTALLER, "Mkdir(%{public}s) failed: %{public}d", deployedDir.c_str(), ec);
                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;
                    }
                    // 3. base.apk — copy from stream_install staging to persistent
                    //    location via installs-domain CopyFile.
                    ec = installd->CopyFile(srcApkPath, deployedApk, std::string());
                    if (ec != ERR_OK) {
                        LOG_E(BMS_TAG_INSTALLER, "CopyFile(%{public}s -> %{public}s) failed: %{public}d",
                            srcApkPath.c_str(), deployedApk.c_str(), ec);
                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;
                    }
                    // 4. Synthesize OH resources HAP directly at entry.hap location —
                    //    installd loads libapk_installer.so (installs domain) and calls
                    //    oh_adapter_build_resources_hap(apk, outPath). No fs work in BMS.
                    ExtractParam ep;
                    ep.srcPath = srcApkPath;
                    ep.targetPath = entryHapPath;
                    ep.extractFileType = ExtractFileType::APK_RESOURCES_HAP;
                    ec = installd->ExtractFiles(ep);
                    if (ec != ERR_OK) {
                        LOG_W(BMS_TAG_INSTALLER, "ExtractFiles(APK_RESOURCES_HAP) %{public}s -> %{public}s failed: %{public}d — launcher icon will fall back",
                            srcApkPath.c_str(), entryHapPath.c_str(), ec);
                    } else {
                        LOG_I(BMS_TAG_INSTALLER, "ExtractFiles(APK_RESOURCES_HAP) OK %{public}s", entryHapPath.c_str());
                    }

                    InnerBundleInfo innerBundleInfo;
                    BundleInfo bundleInfo;
                    bundleInfo.name = pkg;
                    bundleInfo.vendor = "android";
                    bundleInfo.versionCode = j.value("versionCode", 0);
                    bundleInfo.versionName = j.value("versionName", std::string());
                    bundleInfo.minSdkVersion = j.value("minSdkVersion", 0);
                    bundleInfo.targetVersion = j.value("targetSdkVersion", 0);
                    innerBundleInfo.SetBaseBundleInfo(bundleInfo);

                    ApplicationInfo appInfo;
                    appInfo.name = pkg;
                    appInfo.bundleName = pkg;
                    appInfo.label = j.value("appLabel", pkg);
                    appInfo.codePath = j.value("deployedDir", std::string());
                    appInfo.dataDir = std::string("/data/app/el2/0/android/") + pkg;
                    appInfo.uid = j.value("uid", 10000);
                    appInfo.debug = j.value("debuggable", false);
                    appInfo.deviceId = "PHONE-001";
                    // Icon path: launcher requires applicationInfo.iconPath OR
                    // applicationInfo.iconResource non-empty to render the icon.
                    // ApkInstaller writes a fallback icon.png to deployedDir at
                    // install time (extracts from APK or uses generic placeholder).
                    appInfo.iconPath = j.value("deployedDir", std::string()) + "/icon.png";
                    appInfo.icon = appInfo.iconPath;
                    // adapter project: app-level iconId/labelId from synthesized
                    // resources.index (restool-generated AppScope-level entries):
                    //   media:app_icon  -> 0x01000001
                    //   string:app_name -> 0x01000000
                    appInfo.iconId  = 0x01000001;
                    appInfo.labelId = 0x01000000;
                    // Mark as Android app so AMS routes spawn requests through
                    // the AndroidSpawnClient branch (-> appspawn-x) instead of
                    // standard OH appspawn (which expects HAP + ArkTS runtime).
                    appInfo.bundleType = BundleType::APP_ANDROID;
                    innerBundleInfo.SetBaseApplicationInfo(appInfo);
                    // Override the bundleType field on InnerBundleInfo itself
                    // (used by GetApplicationBundleType() which AMS reads to dispatch).
                    innerBundleInfo.SetApplicationBundleType(BundleType::APP_ANDROID);

                    InnerModuleInfo moduleInfo;
                    moduleInfo.name = "entry";
                    moduleInfo.modulePackage = "entry";
                    moduleInfo.moduleName = "entry";
                    moduleInfo.modulePath = j.value("deployedDir", std::string());
                    moduleInfo.isEntry = true;
                    moduleInfo.installationFree = false;
                    moduleInfo.distro.moduleType = "entry";
                    moduleInfo.distro.installationFree = false;
                    moduleInfo.distro.deliveryWithInstall = true;
                    moduleInfo.distro.moduleName = "entry";
                    // v2: entry.hap already synthesized by installd above via
                    //     ExtractFiles(APK_RESOURCES_HAP). No BMS-side staging.
                    moduleInfo.hapPath = entryHapPath;
                    moduleInfo.moduleResPath = moduleInfo.hapPath;

                    if (j.contains("abilities") && j["abilities"].is_array()) {
                        for (const auto &ab : j["abilities"]) {
                            std::string abilityName = ab.value("name", std::string());
                            if (abilityName.empty()) continue;
                            InnerAbilityInfo innerAbility;
                            innerAbility.name = abilityName;
                            innerAbility.bundleName = pkg;
                            innerAbility.moduleName = "entry";
                            innerAbility.package = "entry";
                            innerAbility.applicationName = pkg;
                            innerAbility.label = ab.value("label", abilityName);
                            innerAbility.launchMode = static_cast<LaunchMode>(
                                ab.value("launchMode", 0));
                            // OH InnerAbilityInfo defaults: visible=false, enabled=false,
                            // isLauncherAbility=false. All three need to be true for
                            // launcher's QueryLauncherAbilityInfos to surface this ability.
                            innerAbility.visible = true;
                            innerAbility.enabled = true;
                            innerAbility.isLauncherAbility = ab.value("isMainAbility", false);
                            innerAbility.kind = "page";
                            innerAbility.type = AbilityType::PAGE;
                            innerAbility.codePath = j.value("deployedDir", std::string());
                            innerAbility.deviceId = "PHONE-001";
                            innerAbility.iconPath = j.value("deployedDir", std::string()) + "/icon.png";
                            // adapter project: iconId / labelId match the synthesized
                            // resources.index inside entry.hap (restool-generated):
                            //   media:icon  -> 0x01000005
                            //   string:EntryAbility_label -> 0x01000003
                            // OH launcher's getAppIconWithCache requires iconId > 0
                            // and ResourceManager.getMediaBase64(iconId) succeeds.
                            innerAbility.iconId  = 0x01000005;
                            innerAbility.labelId = 0x01000003;
                            innerAbility.hapPath = entryHapPath;
                            innerAbility.resourcePath = innerAbility.hapPath;
                            // Translate Android intent-filter to OH skills.
                            // Crucially: when Android MAIN+LAUNCHER are present (isMainAbility=true)
                            // we also add ohos.want.action.home so OH launcher's QueryLauncherAbilityInfos
                            // (which filters by Want::ACTION_HOME = "ohos.want.action.home") finds this ability.
                            Skill skill;
                            if (ab.contains("actions") && ab["actions"].is_array()) {
                                for (const auto &act : ab["actions"]) {
                                    skill.actions.push_back(act.get<std::string>());
                                }
                            }
                            if (ab.contains("categories") && ab["categories"].is_array()) {
                                for (const auto &cat : ab["categories"]) {
                                    skill.entities.push_back(cat.get<std::string>());
                                }
                            }
                            if (ab.value("isMainAbility", false)) {
                                skill.actions.push_back("ohos.want.action.home");
                                skill.entities.push_back("entity.system.home");
                            }
                            innerAbility.skills.push_back(skill);
                            std::string key = pkg + ".entry." + abilityName;
                            innerBundleInfo.InsertAbilitiesInfo(key, innerAbility);
                            if (ab.value("isMainAbility", false)) {
                                moduleInfo.mainAbility = abilityName;
                                moduleInfo.entryAbilityKey = key;
                            }
                        }
                    }
                    innerBundleInfo.InsertInnerModuleInfo("entry", moduleInfo);

                    auto dataMgr = DelayedSingleton<BundleMgrService>::GetInstance()->GetDataMgr();
                    if (dataMgr == nullptr) {
                        LOG_E(BMS_TAG_INSTALLER, "BundleDataMgr unavailable for register");
                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;
                    }
                    // BMS requires installStates_[bundleName]==INSTALL_START before
                    // AddInnerBundleInfo will store the InnerBundleInfo.
                    if (!dataMgr->UpdateBundleInstallState(pkg, InstallState::INSTALL_START)) {
                        LOG_E(BMS_TAG_INSTALLER, "UpdateBundleInstallState INSTALL_START failed for %{public}s", pkg.c_str());
                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;
                    }
                    // Register InnerBundleUserInfo for BOTH userId 0 (system) and 100
                    // (current user). Launcher uses 100; AMS/AppMS internal calls
                    // (e.g. CheckLaunchApplicationParams in app_mgr_service_inner.cpp:1693)
                    // sometimes use userId 0 — without an entry there `aa start` fails
                    // with `getBundleInfo fail` → mission_list timeout → fork never happens.
                    auto installTime = std::chrono::duration_cast<std::chrono::milliseconds>(
                        std::chrono::system_clock::now().time_since_epoch()).count();
                    for (int32_t uid_user : {0, 100}) {
                        InnerBundleUserInfo userInfo;
                        userInfo.bundleName = pkg;
                        userInfo.bundleUserInfo.userId = uid_user;
                        userInfo.bundleUserInfo.enabled = true;
                        userInfo.uid = j.value("uid", 10000);
                        userInfo.gids.push_back(j.value("gid", 10000));
                        userInfo.installTime = installTime;
                        userInfo.updateTime = installTime;
                        innerBundleInfo.AddInnerBundleUserInfo(userInfo);
                    }
                    if (!dataMgr->AddInnerBundleInfo(pkg, innerBundleInfo, true)) {
                        LOG_E(BMS_TAG_INSTALLER, "AddInnerBundleInfo failed for %{public}s", pkg.c_str());
                        dataMgr->UpdateBundleInstallState(pkg, InstallState::INSTALL_FAIL);
                        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;
                    }
                    // v2 R.3.8 fix: persist to dataStorage so bundle survives reboot.
                    // HAP install path in base_bundle_installer.cpp (lines 2197/2428/7938)
                    // calls SaveInnerBundleInfo explicitly; the older adapter patch omitted
                    // it, leaving RAM-only state that vanished on BMS restart.
                    if (!dataMgr->SaveInnerBundleInfo(innerBundleInfo)) {
                        LOG_E(BMS_TAG_INSTALLER, "SaveInnerBundleInfo failed for %{public}s — bundle will be lost on reboot", pkg.c_str());
                    }
                    if (!dataMgr->UpdateBundleInstallState(pkg, InstallState::INSTALL_SUCCESS)) {
                        LOG_E(BMS_TAG_INSTALLER, "UpdateBundleInstallState INSTALL_SUCCESS failed for %{public}s", pkg.c_str());
                    }
                    LOG_I(BMS_TAG_INSTALLER, "registered %{public}s in BMS for userId=100 (persistent)", pkg.c_str());
                    // Fire COMMON_EVENT_PACKAGE_ADDED broadcast so launcher / Sceneboard
                    // refresh and add the icon dynamically. Without this, launcher only
                    // re-queries BMS at startup and won't notice our manual register.
                    NotifyBundleEvents installRes;
                    installRes.type           = NotifyType::INSTALL;
                    installRes.resultCode     = ERR_OK;
                    installRes.bundleName     = pkg;
                    installRes.modulePackage  = "entry";
                    installRes.uid            = j.value("uid", 10000);
                    installRes.userId         = 100;
                    installRes.isAppUpdate    = false;
                    installRes.bundleType     = 0;
                    installRes.appDistributionType = "none";
                    if (NotifyBundleStatus(installRes) != ERR_OK) {
                        LOG_W(BMS_TAG_INSTALLER, "NotifyBundleStatus(INSTALL) failed for %{public}s — launcher may not refresh", pkg.c_str());
                    } else {
                        LOG_I(BMS_TAG_INSTALLER, "broadcast COMMON_EVENT_PACKAGE_ADDED for %{public}s", pkg.c_str());
                    }
                    // Directly write ResourceInfo into BundleResourceRdb with the
                    // PNG bytes already extracted by ApkInstaller to
                    // /data/app/android/<pkg>/icon.png. Bypass
                    // BundleResourceManager::AddResourceInfoByBundleNameWhenInstall
                    // because it tries to parse resources.index (always missing for
                    // .apk) and ProcessResourceInfoWhenParseFailed substitutes a
                    // generic default icon instead of our extracted PNG.
                    {
                        std::vector<uint8_t> iconBytes;
                        // Try install dir first, then a stable staging path (so the
                        // icon can be pre-placed by the user / a future ApkInstaller
                        // extract step and survive across uninstalls).
                        std::vector<std::string> candidates = {
                            deployedDir + "/icon.png",
                            std::string("/data/local/tmp/") + pkg + "_icon.png"
                        };
                        for (const auto &iconFile : candidates) {
                            std::ifstream icf(iconFile, std::ios::binary | std::ios::ate);
                            if (!icf) continue;
                            std::streamsize sz = icf.tellg();
                            if (sz <= 0 || sz >= (10 * 1024 * 1024)) continue;
                            icf.seekg(0);
                            iconBytes.resize(static_cast<size_t>(sz));
                            if (icf.read(reinterpret_cast<char*>(iconBytes.data()), sz)) {
                                LOG_I(BMS_TAG_INSTALLER, "loaded icon %{public}s (%{public}zu bytes)", iconFile.c_str(), iconBytes.size());
                                break;
                            }
                            iconBytes.clear();
                        }
                        if (iconBytes.empty()) {
                            LOG_W(BMS_TAG_INSTALLER, "no icon file found for %{public}s — RDB row will lack icon", pkg.c_str());
                        }
                        // OH launcher reads the ICON TEXT column which holds a
                        // "data:image/png;base64,..." URI, NOT the FOREGROUND BLOB.
                        // Inline base64 encode iconBytes into a data URI so the row
                        // matches what BundleResourceParser would produce for a HAP.
                        std::string iconDataUri;
                        if (!iconBytes.empty()) {
                            static const char b64[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
                            std::string b64str;
                            b64str.reserve(((iconBytes.size() + 2) / 3) * 4);
                            size_t i = 0;
                            for (; i + 2 < iconBytes.size(); i += 3) {
                                uint32_t n = (uint32_t(iconBytes[i]) << 16) | (uint32_t(iconBytes[i+1]) << 8) | uint32_t(iconBytes[i+2]);
                                b64str.push_back(b64[(n >> 18) & 0x3F]);
                                b64str.push_back(b64[(n >> 12) & 0x3F]);
                                b64str.push_back(b64[(n >> 6)  & 0x3F]);
                                b64str.push_back(b64[n & 0x3F]);
                            }
                            if (i < iconBytes.size()) {
                                uint32_t n = uint32_t(iconBytes[i]) << 16;
                                if (i + 1 < iconBytes.size()) n |= uint32_t(iconBytes[i+1]) << 8;
                                b64str.push_back(b64[(n >> 18) & 0x3F]);
                                b64str.push_back(b64[(n >> 12) & 0x3F]);
                                b64str.push_back(i + 1 < iconBytes.size() ? b64[(n >> 6) & 0x3F] : '=');
                                b64str.push_back('=');
                            }
                            iconDataUri = std::string("data:image/png;base64,") + b64str;
                        }
                        BundleResourceRdb rdb;
                        // App-level row (abilityName empty).
                        ResourceInfo appRes;
                        appRes.bundleName_     = pkg;
                        appRes.moduleName_     = "entry";
                        appRes.abilityName_    = "";
                        appRes.label_          = j.value("appLabel", pkg);
                        appRes.iconNeedParse_  = false;
                        appRes.labelNeedParse_ = false;
                        appRes.icon_           = iconDataUri;
                        if (!iconBytes.empty()) {
                            appRes.foreground_ = iconBytes;
                        }
                        if (!rdb.AddResourceInfo(appRes)) {
                            LOG_W(BMS_TAG_INSTALLER, "BundleResourceRdb app-level Add fail %{public}s", pkg.c_str());
                        } else {
                            LOG_I(BMS_TAG_INSTALLER, "BundleResourceRdb app-level Add OK %{public}s", pkg.c_str());
                        }
                        // Per-ability rows (one per launcher activity so
                        // GetLauncherAbilityResourceInfo returns a list).
                        for (const auto &abilityJson : j["abilities"]) {
                            ResourceInfo aRes = appRes;
                            aRes.abilityName_ = abilityJson.value("name", std::string(""));
                            std::string aLabel = abilityJson.value("label", j.value("appLabel", pkg));
                            if (!aLabel.empty()) {
                                aRes.label_ = aLabel;
                            }
                            if (aRes.abilityName_.empty()) continue;
                            if (!rdb.AddResourceInfo(aRes)) {
                                LOG_W(BMS_TAG_INSTALLER, "BundleResourceRdb ability Add fail %{public}s/%{public}s", pkg.c_str(), aRes.abilityName_.c_str());
                            } else {
                                LOG_I(BMS_TAG_INSTALLER, "BundleResourceRdb ability Add OK %{public}s/%{public}s", pkg.c_str(), aRes.abilityName_.c_str());
                            }
                        }
                    }
                } catch (const std::exception &e) {
                    LOG_E(BMS_TAG_INSTALLER, "manifest JSON parse/register failed: %{public}s", e.what());
                    return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;
                }
            }
            LOG_I(BMS_TAG_INSTALLER, "adapter .apk install + register completed");
            return ERR_OK;
        }
    }
#endif

    // copy the haps to the dir which cannot be accessed from caller
    result = CopyHapsToSecurityDir(installParam, bundlePaths);
    CHECK_RESULT(result, "copy file failed %{public}d");

    // check syscap
    ErrCode checkSysCapRes = CheckSysCap(bundlePaths);
    if (checkSysCapRes != ERR_OK) {
        APP_LOGI_NOFUNC("check syscap failed %{public}d", result);
    }
    UpdateInstallerState(InstallerState::INSTALL_SYSCAP_CHECKED);                  // ---- 10%

    // verify signature info for all haps
    std::vector<Security::Verify::HapVerifyResult> hapVerifyResults;
    result = CheckMultipleHapsSignInfo(bundlePaths, installParam, hapVerifyResults);
    CHECK_RESULT(result, "hap files check signature info failed %{public}d");
    UpdateInstallerState(InstallerState::INSTALL_SIGNATURE_CHECKED);               // ---- 15%

    result = CheckShellInstall(hapVerifyResults);
    CHECK_RESULT(result, "check shell install failed %{public}d");

    result = CheckPreAppAllowHdcInstall(installParam, hapVerifyResults);
    CHECK_RESULT(result, "not allowed install os_integration bundle, %{public}d");

    result = CheckInstallGrantPermission(installParam, hapVerifyResults);
    CHECK_RESULT(result, "check grantPermission install failed %{public}d");

    // parse the bundle infos for all haps
    // key is bundlePath , value is innerBundleInfo
    std::unordered_map<std::string, InnerBundleInfo> newInfos;
    result = ParseHapFiles(bundlePaths, installParam, appType, hapVerifyResults, newInfos);
    CHECK_RESULT(result, "parse haps file failed %{public}d");

    result = CheckArkTSMode(newInfos);
    CHECK_RESULT(result, "check arkTS mode failed %{public}d");

    bool onDemandInstall = OnDemandInstallDataMgr::GetInstance().IsOnDemandInstall(installParam);
    if (!onDemandInstall) {
        if (userId_ == Constants::DEFAULT_USERID && installParam.isDataPreloadHap &&
            installParam.appIdentifier != appIdentifier_) {
            result = ERR_APPEXECFWK_INSTALL_VERIFICATION_FAILED;
        }
    } else {
        if (installParam.isDataPreloadHap &&
            OnDemandInstallDataMgr::GetInstance().GetAppidentifier(inBundlePaths[0]) != appIdentifier_) {
            result = ERR_APPEXECFWK_INSTALL_VERIFICATION_FAILED;
        }
    }
    CHECK_RESULT(result, "check DataPreloadHap appIdentifier failed %{public}d");
    // washing machine judge
    if (!installParam.isPreInstallApp && !newInfos.empty()) {
        auto &firstBundleInfo = newInfos.begin()->second;
        if (!firstBundleInfo.IsSystemApp()) {
            bool isBundleExist = dataMgr_->IsBundleExist(firstBundleInfo.GetBundleName());
            if (!isBundleExist && !VerifyActivationLock()) {
                result = ERR_APPEXECFWK_INSTALL_FAILED_CONTROLLED;
            }
        }
    }
    CHECK_RESULT(result, "check install verifyActivation failed %{public}d");
    // plugin judge
    if (!newInfos.empty() &&
        newInfos.begin()->second.GetApplicationBundleType() == BundleType::APP_PLUGIN) {
        result = ERR_APPEXECFWK_PLUGIN_INSTALL_NOT_ALLOW;
        CHECK_RESULT(result, "plugin install not allow %{public}d");
    }
    result = CheckShellCanInstallPreApp(newInfos);
    CHECK_RESULT(result, "check shell can install pre app failed %{public}d");
    CheckPreBundle(newInfos, installParam, isRecover);
    result = CheckInstallPermission(installParam, hapVerifyResults);
    CHECK_RESULT(result, "check install permission failed %{public}d");
    result = CheckInstallCondition(hapVerifyResults, newInfos, checkSysCapRes);
    CHECK_RESULT(result, "check install condition failed %{public}d");
    result = CheckHapBinInstallCondition(newInfos);
    CHECK_RESULT(result, "check hap bin install condition failed %{public}d");
    // check the dependencies whether or not exists
    result = CheckDependency(newInfos, sharedBundleInstaller);
    CHECK_RESULT(result, "check dependency failed %{public}d");
    // hapVerifyResults at here will not be empty
    verifyRes_ = hapVerifyResults[0];

    result = CheckDriverIsolation(verifyRes_, userId_, newInfos);
    CHECK_RESULT(result, "check debug scaner driver failed %{public}d");

    result = DeliveryProfileToCodeSign();
    CHECK_RESULT(result, "delivery profile failed %{public}d");

    UpdateInstallerState(InstallerState::INSTALL_PARSED);                          // ---- 20%

    userId_ = GetConfirmUserId(userId_, newInfos);
    result = CheckUserId(userId_);
    CHECK_RESULT(result, "userId check failed %{public}d");

    if (!installParam.isPreInstallApp) {
        result = CheckAppBlackList((newInfos.begin()->second).GetBundleName(), userId_);
        if (result != ERR_OK) {
            CHECK_RESULT(result, "app is in block list %{public}d");
        }
    }
    // check hap hash param
    result = CheckHapHashParams(newInfos, installParam.hashParams);
    CHECK_RESULT(result, "check hap hash param failed %{public}d");
    UpdateInstallerState(InstallerState::INSTALL_HAP_HASH_PARAM_CHECKED);         // ---- 25%

    // check overlay installation
    result = CheckOverlayInstallation(newInfos, userId_);
    CHECK_RESULT(result, "overlay hap check failed %{public}d");
    UpdateInstallerState(InstallerState::INSTALL_OVERLAY_CHECKED);                // ---- 30%

    // check app props in the configuration file
    result = CheckAppLabelInfo(newInfos);
    CHECK_RESULT(result, "verisoncode or bundleName is different in all haps %{public}d");
    UpdateInstallerState(InstallerState::INSTALL_VERSION_AND_BUNDLENAME_CHECKED);  // ---- 35%

    result = CheckSpaceIsolation(installParam, newInfos);
    CHECK_RESULT(result, "check space isolation failed:%{public}d");

    // to send notify of start install application
    SendStartInstallNotify(installParam, newInfos);

    // check if bundle exists in extension
    result = CheckBundleInBmsExtension(bundleName_, userId_);
    CHECK_RESULT(result, "bundle is already existed in bms extension %{public}d");

    // check native file
    result = CheckMultiNativeFile(newInfos);
    CHECK_RESULT(result, "native so is incompatible in all haps %{public}d");
    UpdateInstallerState(InstallerState::INSTALL_NATIVE_SO_CHECKED);               // ---- 40%

    // check proxy data
    result = CheckProxyDatas(newInfos);
    CHECK_RESULT(result, "proxy data check failed %{public}d");
    UpdateInstallerState(InstallerState::INSTALL_PROXY_DATA_CHECKED);              // ---- 45%

    // check hap is allow install by app control
    result = InstallNormalAppControl((newInfos.begin()->second).GetAppId(),
        (newInfos.begin()->second).GetAppIdentifier(), userId_, installParam.isPreInstallApp);
    CHECK_RESULT(result, "install app control failed %{public}d");

    auto &mtx = dataMgr_->GetBundleMutex(bundleName_);
    std::lock_guard lock {mtx};
    // add installing name, when parameters not set installingBundleName
    AddInstallingBundleName(installParam);
    ScopeGuard installBundleNameGuard([&] {
        DeleteInstallingBundleName(installParam);
    });
    // uninstall all sandbox app before
    UninstallAllSandboxApps(bundleName_);
    UpdateInstallerState(InstallerState::INSTALL_REMOVE_SANDBOX_APP);              // ---- 50%

    // this state should always be set when return
    ScopeGuard stateGuard([&] {
        dataMgr_->UpdateBundleInstallState(bundleName_, InstallState::INSTALL_SUCCESS);
        dataMgr_->EnableBundle(bundleName_);
    });

    InnerBundleInfo oldInfo;
    verifyCodeParams_ = installParam.verifyCodeParams;
    pgoParams_ = installParam.pgoParams;
    copyHapToInstallPath_ = installParam.copyHapToInstallPath;
    ScopeGuard extensionDirGuard([&] { RemoveCreatedExtensionDirsForException(); });
    // try to get the bundle info to decide use install or update. Always keep other exceptions below this line.
    if (!InitTempBundleFromCache(oldInfo, isAppExist_)) {
        return ERR_APPEXECFWK_INIT_INSTALL_TEMP_BUNDLE_ERROR;
    }
    if (!(installParam.isOTA || otaInstall_) && !newInfos.empty()) {
        result = bundleInstallChecker_->CalculateInstallInodes(newInfos, !isAppExist_);
        CHECK_RESULT(result, "check inode requirements failed %{public}d");
    }
    bool oldAppHasKey = oldInfo.GetApplicationReservedFlag() &
        static_cast<uint32_t>(ApplicationReservedFlag::ENCRYPTED_KEY_EXISTED);
    ScopeGuard encrytedKeyGuard([&] { dataMgr_->UpdateAppEncryptedStatus(bundleName_, oldAppHasKey, 0, false); });
    dataMgr_->UpdateAppEncryptedStatus(bundleName_, false, 0, false);
    // check AppDistributionType
    result = CheckAppDistributionType();
    CHECK_RESULT(result, "check app distribution type info failed %{public}d");

    // when bundle update start, bms need set disposed rule to forbidden app running.
    (void)SetDisposedRuleWhenBundleUpdateStart(newInfos, oldInfo, installParam.isPreInstallApp);
    // when bundle update end, bms need delete disposed rule.
    ScopeGuard deleteDisposedRuleGuard([&] { (void)DeleteDisposedRuleWhenBundleUpdateEnd(oldInfo); });
    ScopeGuard codePathGuard([&] { RollbackCodePath(bundleName_, isFeatureNeedUninstall_); });
    result = InnerProcessBundleInstall(newInfos, oldInfo, installParam, uid);
    PrintDataStat();
    CHECK_RESULT_WITH_ROLLBACK(result, "internal processing failed with result %{public}d", newInfos, oldInfo);
    UpdateInstallerState(InstallerState::INSTALL_INFO_SAVED);                      // ---- 80%

#ifdef WEBVIEW_ENABLE
    result = VerifyArkWebInstall();
    CHECK_RESULT_WITH_ROLLBACK(result, "web verify failed %{public}d", newInfos, oldInfo);
#endif
    // copy hap to app_tmp path
    (void)AddAppGalleryHapToTempPath(installParam.isPreInstallApp, newInfos);

    // Roolback is unavailable below this line
    // copy hap or hsp to real install dir
    SaveHapPathToRecords(installParam.isPreInstallApp, newInfos);
    if (installParam.copyHapToInstallPath) {
        LOG_D(BMS_TAG_INSTALLER, "begin to copy hap to install path");
        result = SaveHapToInstallPath(newInfos, oldInfo);
        CHECK_RESULT_WITH_ROLLBACK(result, "copy hap to install path failed %{public}d", newInfos, oldInfo);
    } else {
        if ((result = CheckHapEncryption(newInfos, oldInfo, false)) != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "check encryption of pre-hap failed %{public}d", result);
        }
    }

    if (installParam.isDataPreloadHap) {
        // Verify Code Signature For Data Preload Hap
        for (const auto &preinstalledAppPath : bundlePaths) {
            VerifyCodeSignatureForHap(newInfos, preinstalledAppPath, preinstalledAppPath);
        }
    }
    result = ProcessBundleCodePath(newInfos, oldInfo, bundleName_,
        isFeatureNeedUninstall_, installParam.copyHapToInstallPath);
    CHECK_RESULT_WITH_ROLLBACK(result, "final process code path failed %{public}d", newInfos, oldInfo);

    UpdateInstallerState(InstallerState::INSTALL_RENAMED);                         // ---- 90%

    // delete low-version hap or hsp when higher-version hap or hsp installed
    if (!uninstallModuleVec_.empty()) {
        UninstallLowerVersionFeature(uninstallModuleVec_, installParam.GetKillProcess());
    }

    // create Screen Lock File Protection Dir
    CreateScreenLockProtectionDir(true);
    ScopeGuard ScreenLockFileProtectionDirGuard([&] {
        if (!isAppExist_ && !dataMgr_->GetUninstallBundleInfoWithUserAndAppIndex(bundleName_, userId_,
            Constants::INITIAL_APP_INDEX)) {
            DeleteScreenLockProtectionDir(bundleName_);
        }
    });

    // process bin file permission
    result = ProcessBinFiles(newInfos);
    CHECK_RESULT_WITH_ROLLBACK(result, "process bin files failed %{public}d", newInfos, oldInfo);

    // install cross-app hsp which has rollback operation in sharedBundleInstaller when some one failure occurs
    result = sharedBundleInstaller.Install(sysEventInfo_);
    CHECK_RESULT_WITH_ROLLBACK(result, "install cross-app shared bundles failed %{public}d", newInfos, oldInfo);

    std::shared_ptr driverInstaller = std::make_shared<DriverInstaller>();
    result = driverInstaller->CopyAllDriverFile(newInfos, oldInfo);
    CHECK_RESULT_WITH_ROLLBACK(result, "copy driver files failed due to error %{public}d", newInfos, oldInfo);

    UpdateInstallerState(InstallerState::INSTALL_SUCCESS);                         // ---- 100%
#ifdef WEBVIEW_ENABLE
    RestoreconForArkweb();
#endif
    LOG_D(BMS_TAG_INSTALLER, "finish ProcessBundleInstall bundlePath install touch off aging");
    moduleName_ = GetModuleNames(newInfos);
    isBundleCrossAppSharedConfig_ = IsBundleCrossAppSharedConfig(newInfos);
#ifdef BUNDLE_FRAMEWORK_FREE_INSTALL
    if (installParam.installFlag == InstallFlag::FREE_INSTALL) {
        DelayedSingleton<BundleMgrService>::GetInstance()->GetAgingMgr()->Start(
            BundleAgingMgr::AgingTriggertype::FREE_INSTALL);
    }
#endif
    InnerBundleInfo cacheInfo;
    tempInfo_.GetTempBundleInfo(cacheInfo);
#ifdef BUNDLE_FRAMEWORK_QUICK_FIX
    if (needDeleteQuickFixInfo_) {
        LOG_D(BMS_TAG_INSTALLER, "module update, quick fix old patch need to delete:%{public}s", bundleName_.c_str());
        if (!oldInfo.GetAppQuickFix().deployedAppqfInfo.hqfInfos.empty()) {
            LOG_D(BMS_TAG_INSTALLER, "quickFixInfo need disable, bundleName:%{public}s", bundleName_.c_str());
            auto quickFixSwitcher = std::make_unique<QuickFixSwitcher>(bundleName_, false);
            quickFixSwitcher->DisableQuickFix(cacheInfo);
        }
        auto quickFixDeleter = std::make_unique<QuickFixDeleter>(bundleName_);
        quickFixDeleter->DeleteQuickFix(cacheInfo);
        tempInfo_.SetTempBundleInfo(cacheInfo);
    }
#endif
    // process ark startup cache
    std::string el1ArkStartupCachePath = ServiceConstants::SYSTEM_OPTIMIZE_PATH +
        bundleName_ + ServiceConstants::ARK_STARTUP_CACHE_DIR;
    el1ArkStartupCachePath = el1ArkStartupCachePath.replace(el1ArkStartupCachePath.find("%"), 1,
        std::to_string(userId_));
    ArkStartupCache ceateArk;
    ceateArk.bundleName = bundleName_;
    ceateArk.bundleType = oldInfo.GetApplicationBundleType();
    ceateArk.cacheDir = el1ArkStartupCachePath;
    ceateArk.mode = ServiceConstants::SYSTEM_OPTIMIZE_MODE;
    InnerBundleUserInfo newInnerBundleUserInfo;
    cacheInfo.GetInnerBundleUserInfo(userId_, newInnerBundleUserInfo);
    ceateArk.uid = newInnerBundleUserInfo.uid;
    ceateArk.gid = newInnerBundleUserInfo.uid;
    ProcessArkStartupCache(ceateArk, cacheInfo.GetModuleSize(), userId_);
    UpdateEncryptedStatus(oldInfo);
    GetInstallEventInfo(cacheInfo, sysEventInfo_);
    AddAppProvisionInfo(bundleName_, hapVerifyResults[0].GetProvisionInfo(), installParam);
    UpdateRouterInfo();
    ProcessOldNativeLibraryPath(newInfos, oldInfo.GetVersionCode(), oldInfo.GetNativeLibraryPath());
    RemoveOldHapIfOTA(installParam, newInfos, oldInfo);
    UpdateAppInstallControlled(userId_);
    extensionDirGuard.Dismiss();
    ScreenLockFileProtectionDirGuard.Dismiss();
    if (isAppExist_ && isModuleUpdate_ && (versionCode_ != oldInfo.GetVersionCode())) {
        LOG_NOFUNC_I(BMS_TAG_INSTALLER, "need to remove data/preload/app hap files for bundle:%{public}s",
            bundleName_.c_str());
        RemoveDataPreloadHapFiles(bundleName_);
    }
    RemoveOldExtensionDirs();
    /* process quick fix when install new moudle */
    ProcessQuickFixWhenInstallNewModule(installParam, newInfos);
    UpdateDynamicSkills();
    VerifyDomain();
    PatchDataMgr::GetInstance().ProcessPatchInfo(bundleName_, inBundlePaths,
        versionCode_, AppPatchType::INTERNAL, installParam.isPatch);
    UpdateHasCloudkitConfig();
    // check mark install finish
    result = MarkInstallFinish();
    if (result != ERR_OK) {
        PatchDataMgr::GetInstance().DeleteInnerPatchInfo(bundleName_);
    }
    CHECK_RESULT_WITH_ROLLBACK(result, "mark install finish failed %{public}d", newInfos, oldInfo);
    // delete app_tmp
    (void)DeleteAppGalleryHapFromTempPath();
    DeleteUninstallBundleInfo(bundleName_);
    codePathGuard.Dismiss();
    ProcessOldCodePath(bundleName_, isFeatureNeedUninstall_);
    // create data group dir
    CreateDataGroupDirs(hapVerifyResults, oldInfo);
    // process ark startup cache
    if (result == ERR_OK) {
        InnerBundleUserInfo newInnerBundleUserInfo;
        cacheInfo.GetInnerBundleUserInfo(userId_, newInnerBundleUserInfo);
        ArkStartupCache ceateArk = CreateArkStartupCacheParameter(bundleName_, userId_,
            oldInfo.GetApplicationBundleType(), newInnerBundleUserInfo.uid);
        ProcessArkStartupCache(ceateArk, cacheInfo.GetModuleSize(), userId_);
    }
    ProcessUpdateShortcut();
    BundleResourceHelper::AddResourceInfoByBundleName(bundleName_, userId_,
        (isAppExist_ && hasInstalledInUser_) ? ADD_RESOURCE_TYPE::UPDATE_BUNDLE : ADD_RESOURCE_TYPE::INSTALL_BUNDLE,
        !isAppExist_);
    if (!ProcessExtProfile(installParam)) {
        LOG_W(BMS_TAG_INSTALLER, "ProcessExtProfile failed");
    }
    SetHybridSpawn();
#ifdef BUNDLE_FRAMEWORK_DEFAULT_APP
    DefaultAppMgr::GetInstance().HandleInstallBundle(userId_, bundleName_);
#endif
    // set api and sdk version to systemevent
    SetAPIAndSdkVersions(cacheInfo.GetBaseApplicationInfo().apiTargetVersion,
        cacheInfo.GetBaseApplicationInfo().apiCompatibleVersion,
        cacheInfo.GetBaseApplicationInfo().compileSdkVersion);
    SetUid(uid);
    SetIsAbcCompressed();
    InnerProcessNewBundleDataDir(installParam.isOTA || otaInstall_, oldInfo, cacheInfo);
    LOG_I(BMS_TAG_INSTALLER, "finish install %{public}s", bundleName_.c_str());
    UtdHandler::InstallUtdAsync(bundleName_, userId_);
    CheckAddResultMsg(cacheInfo, isContainEntry_);
    PrintDataStat();
    ProcessAOT(installParam);
    return result;
}

void BaseBundleInstaller::RollBack(const std::unordered_map<std::string, InnerBundleInfo> &newInfos,
    InnerBundleInfo &oldInfo)
{
    LOG_D(BMS_TAG_INSTALLER, "start rollback due to install failed");
    if (!isAppExist_) {
        if (!newInfos.empty() && newInfos.begin()->second.IsPreInstallApp() &&
            !BundleUtil::CheckSystemFreeSize(APP_INSTALL_PATH, FIVE_MB)) {
            LOG_I(BMS_TAG_INSTALLER, "pre bundleName:%{public}s no need rollback due to no space",
                newInfos.begin()->second.GetBundleName().c_str());
            return;
        }
        if (newInfos.begin()->second.GetApplicationBundleType() == BundleType::ATOMIC_SERVICE) {
            int32_t uid = newInfos.begin()->second.GetUid(userId_);
            if (uid != Constants::INVALID_UID) {
                LOG_I(BMS_TAG_INSTALLER, "uninstall atomic service need delete quota, bundleName:%{public}s",
                    newInfos.begin()->second.GetBundleName().c_str());
                std::string bundleDataDir = ServiceConstants::BUNDLE_APP_DATA_BASE_DIR + ServiceConstants::BUNDLE_EL[1]
                    + ServiceConstants::PATH_SEPARATOR + std::to_string(userId_) + ServiceConstants::BASE +
                    newInfos.begin()->second.GetBundleName();
                PrepareBundleDirQuota(newInfos.begin()->second.GetBundleName(), uid, bundleDataDir, 0);
            }
        }
        if (!InitDataMgr()) {
            return;
        }
        bool isKeepData = dataMgr_->GetUninstallBundleInfoWithUserAndAppIndex(bundleName_, userId_,
            Constants::INITIAL_APP_INDEX);
        RemoveBundleAndDataDir(newInfos.begin()->second, isKeepData);
        isKeepTokenId_ = oldInfo.HasKeepTokenIdMetadata();
        if (!isKeepData) {
            // delete accessTokenId
            LOG_NOFUNC_I(BMS_TAG_INSTALLER, "DeleteAccessTokenId keepTokenParam=%{public}d", isKeepTokenId_);
            if (BundlePermissionMgr::DeleteAccessTokenId(newInfos.begin()->second.GetAccessTokenId(userId_),
                isKeepTokenId_) != AccessToken::AccessTokenKitRet::RET_SUCCESS) {
                LOG_E(BMS_TAG_INSTALLER, "delete accessToken failed");
            }
        }
        // remove driver file
        std::shared_ptr driverInstaller = std::make_shared<DriverInstaller>();
        for (const auto &info : newInfos) {
            driverInstaller->RemoveDriverSoFile(info.second, "", false);
        }
        // remove profile from code signature
        RemoveProfileFromCodeSign(bundleName_);
        // remove innerBundleInfo
        RemoveInfo(bundleName_, "");
        if (isHnpInstalled_) {
            RollbackHnpInstall(bundleName_, { userId_ });
        }
        return;
    }
    InnerBundleInfo preInfo;
    bool isExist = false;
    if (!FetchInnerBundleInfo(preInfo, isExist) || !isExist) {
        LOG_I(BMS_TAG_INSTALLER, "finish rollback due to install failed");
        return;
    }
    for (const auto &info : newInfos) {
        RollBack(info.second, oldInfo);
    }
    // need delete definePermissions and requestPermissions
    UpdateHapToken(preInfo.GetAppType() != oldInfo.GetAppType(), oldInfo);
    if (isHnpInstalled_) {
        RollbackHnpInstall(bundleName_, oldInfo.GetUsers());
    }
    LOG_D(BMS_TAG_INSTALLER, "finish rollback due to install failed");
}

void BaseBundleInstaller::RollBack(const InnerBundleInfo &info, InnerBundleInfo &oldInfo)
{
    // rollback hap installed
    std::shared_ptr driverInstaller = std::make_shared<DriverInstaller>();
    auto modulePackage = info.GetCurrentModulePackage();
    if (installedModules_[modulePackage]) {
        std::string createModulePath = info.GetAppCodePath() + ServiceConstants::PATH_SEPARATOR +
            modulePackage + ServiceConstants::TMP_SUFFIX;
        RemoveModuleDir(createModulePath);
        oldInfo.SetCurrentModulePackage(modulePackage);
        RollBackModuleInfo(bundleName_, oldInfo);
        // remove driver file of installed module
        driverInstaller->RemoveDriverSoFile(info, info.GetModuleName(modulePackage), true);
    } else {
        RemoveModuleDir(info.GetModuleDir(modulePackage));
        // remove driver file
        driverInstaller->RemoveDriverSoFile(info, info.GetModuleName(modulePackage), false);
        // remove module info
        RemoveInfo(bundleName_, modulePackage);
    }
}

ErrCode BaseBundleInstaller::CheckSpaceIsolation(
    const InstallParam &installParam, const std::unordered_map<std::string, InnerBundleInfo> &newInfos) const
{
    if (installParam.isPreInstallApp || installParam.isOTA || otaInstall_ ||
        installParam.isPatch || installParam.allUser || installParam.IsEnterpriseForAllUser()) {
        return ERR_OK;
    }

    if (newInfos.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "newInfos is empty");
        return ERR_APPEXECFWK_INSTALL_FAILED_CONTROLLED;
    }
    const InnerBundleInfo &newInfo = newInfos.begin()->second;

    if (!BundleInstallChecker::CheckSpaceIsolation(userId_, newInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "check space isolation failed");
        return ERR_APPEXECFWK_INSTALL_FAILED_CONTROLLED;
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CheckDriverIsolation(const Security::Verify::HapVerifyResult &hapVerifyResult,
    const int32_t userId, const std::unordered_map<std::string, InnerBundleInfo> &newInfos) const
{
    if (!BundleInstallChecker::CheckSaneDriverIsolation(hapVerifyResult, userId, newInfos)) {
        return ERR_APPEXECFWK_INSTALL_FAILED_CONTROLLED;
    }
    return ERR_OK;
}

void BaseBundleInstaller::RemoveInfo(const std::string &bundleName, const std::string &packageName)
{
    LOG_D(BMS_TAG_INSTALLER, "remove innerBundleInfo due to rollback");
    if (!InitDataMgr()) {
        return;
    }
    if (packageName.empty()) {
        dataMgr_->UpdateBundleInstallState(bundleName, InstallState::UPDATING_FAIL);
    } else {
        InnerBundleInfo innerBundleInfo;
        bool isExist = false;
        if (!FetchInnerBundleInfo(innerBundleInfo, isExist) || !isExist) {
            LOG_I(BMS_TAG_INSTALLER, "finish rollback due to install failed");
            return;
        }
        dataMgr_->UpdateBundleInstallState(bundleName, InstallState::ROLL_BACK);
        dataMgr_->RemoveModuleInfo(bundleName, packageName, innerBundleInfo);
    }
    LOG_D(BMS_TAG_INSTALLER, "finish to remove innerBundleInfo due to rollback");
}

void BaseBundleInstaller::RollBackModuleInfo(const std::string &bundleName, InnerBundleInfo &oldInfo)
{
    LOG_D(BMS_TAG_INSTALLER, "rollBackMoudleInfo due to rollback");
    if (!InitDataMgr()) {
        return;
    }
    InnerBundleInfo innerBundleInfo;
    bool isExist = false;
    if (!FetchInnerBundleInfo(innerBundleInfo, isExist) || !isExist) {
        return;
    }
    dataMgr_->UpdateBundleInstallState(bundleName, InstallState::ROLL_BACK);
    dataMgr_->UpdateInnerBundleInfo(bundleName, oldInfo, innerBundleInfo);
    LOG_D(BMS_TAG_INSTALLER, "finsih rollBackMoudleInfo due to rollback");
}

ErrCode BaseBundleInstaller::ProcessBundleUninstall(
    const std::string &bundleName, const InstallParam &installParam, int32_t &uid)
{
    LOG_D(BMS_TAG_INSTALLER, "start to process %{public}s bundle uninstall", bundleName.c_str());
    if (bundleName.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall bundle name empty");
        return ERR_APPEXECFWK_UNINSTALL_INVALID_NAME;
    }

    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_UNINSTALL_BUNDLE_MGR_SERVICE_ERROR;
    }

    userId_ = GetUserId(installParam.userId);
    if (userId_ == Constants::INVALID_USERID) {
        return ERR_APPEXECFWK_INSTALL_PARAM_ERROR;
    }

    if (!dataMgr_->HasUserId(userId_)) {
        LOG_E(BMS_TAG_INSTALLER, "The user %{public}d does not exist when uninstall", userId_);
        return ERR_APPEXECFWK_USER_NOT_EXIST;
    }

    auto &mtx = dataMgr_->GetBundleMutex(bundleName);
    std::lock_guard lock {mtx};
    InnerBundleInfo oldInfo;
    ScopeGuard enableGuard([&] { dataMgr_->EnableBundle(bundleName); });
    bool isAppExist = false;
    if (!InitTempBundleFromCache(oldInfo, isAppExist, bundleName) || !isAppExist) {
        LOG_W(BMS_TAG_INSTALLER, "uninstall bundle info missing");
        return ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_BUNDLE;
    }
    if (installParam.GetIsUninstallAndRecover()) {
        PreInstallBundleInfo preInstallBundleInfo;
        if (!dataMgr_->GetPreInstallBundleInfo(bundleName, preInstallBundleInfo)) {
            LOG_E(BMS_TAG_INSTALLER, "UninstallAndRecover %{public}s is not pre-install app", bundleName.c_str());
            return ERR_APPEXECFWK_UNINSTALL_AND_RECOVER_NOT_PREINSTALLED_BUNDLE;
        }
    }
    oldApplicationReservedFlag_ = oldInfo.GetApplicationReservedFlag();
    bundleType_ = oldInfo.GetApplicationBundleType();
    uninstallBundleAppId_ = oldInfo.GetAppId();
    versionCode_ = oldInfo.GetVersionCode();
    appIdentifier_ = oldInfo.GetAppIdentifier();
    appDistributionType_ = oldInfo.GetAppDistributionType();
    isBundleCrossAppSharedConfig_ = oldInfo.IsBundleCrossAppSharedConfig();
    if (oldInfo.GetApplicationBundleType() == BundleType::SHARED) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall bundle is shared library");
        return ERR_APPEXECFWK_UNINSTALL_BUNDLE_IS_SHARED_LIBRARY;
    }
    UninstallBundleInfo uninstallBundleInfo;
    GetUninstallBundleInfo(installParam.isKeepData, userId_, oldInfo, uninstallBundleInfo);

    InnerBundleUserInfo curInnerBundleUserInfo;
    if (!oldInfo.GetInnerBundleUserInfo(userId_, curInnerBundleUserInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "bundle(%{public}s) get user(%{public}d) failed when uninstall",
            oldInfo.GetBundleName().c_str(), userId_);
        return ERR_APPEXECFWK_USER_NOT_INSTALL_HAP;
    }

    uid = curInnerBundleUserInfo.uid;
    bool isForcedUninstall = installParam.IsForcedUninstall() && IsAllowEnterPrise();
    if (!installParam.GetForceExecuted() &&
        !oldInfo.IsRemovable() && installParam.GetKillProcess() && !installParam.GetIsUninstallAndRecover()) {
        if (!isForcedUninstall) {
            LOG_E(BMS_TAG_INSTALLER, "uninstall system app");
            return ERR_APPEXECFWK_UNINSTALL_SYSTEM_APP_ERROR;
        }
    }

    if (!installParam.GetForceExecuted() &&
        (!oldInfo.GetUninstallState() || !curInnerBundleUserInfo.canUninstall) && installParam.GetKillProcess() &&
        !installParam.GetIsUninstallAndRecover()) {
        if (!isForcedUninstall) {
            LOG_E(BMS_TAG_INSTALLER, "bundle : %{public}s can not be uninstalled, uninstallState : %{public}d",
                bundleName.c_str(), oldInfo.GetUninstallState());
            return ERR_APPEXECFWK_UNINSTALL_STATE_NOT_ALLOW;
        }
    }

    if (!UninstallAppControl(oldInfo.GetAppId(), oldInfo.GetAppIdentifier(), userId_)) {
        if (!isForcedUninstall) {
            LOG_E(BMS_TAG_INSTALLER, "bundleName: %{public}s is not allow uninstall", bundleName.c_str());
            return ERR_BUNDLE_MANAGER_APP_CONTROL_DISALLOWED_UNINSTALL;
        }
    }

    if (!CheckWhetherCanBeUninstalled(bundleName, appIdentifier_)) {
        return ERR_APPEXECFWK_UNINSTALL_CONTROLLED;
    }
    bool isMultiUser = oldInfo.GetInnerBundleUserInfos().size() > 1;
    // when bundle uninstall start, bms need set disposed rule to forbidden app running.
    (void)SetDisposedRuleWhenBundleUninstallStart(bundleName, uninstallBundleAppId_, isMultiUser);
    // when bundle uninstall end, bms need delete disposed rule.
    ScopeGuard deleteDisposedRuleGuard([bundleName, isMultiUser, this] {
        (void)DeleteDisposedRuleWhenBundleUninstallEnd(bundleName, uninstallBundleAppId_, isMultiUser);
    });

    // reboot scan case will not kill the bundle
    if (installParam.GetKillProcess()) {
        // kill the bundle process during uninstall.
        if (!AbilityManagerHelper::UninstallApplicationProcesses(oldInfo.GetApplicationName(), uid)) {
            LOG_E(BMS_TAG_INSTALLER, "can not kill process, uid : %{public}d", uid);
            return ERR_APPEXECFWK_UNINSTALL_KILLING_APP_ERROR;
        }
    }

    std::shared_ptr<BundleCloneInstaller> cloneInstaller = std::make_shared<BundleCloneInstaller>();
    cloneInstaller->UninstallAllCloneApps(bundleName, installParam.isRemoveUser, installParam.isKeepData,
        installParam.userId);

#ifdef BUNDLE_FRAMEWORK_APP_CONTROL
    std::shared_ptr<AppControlManager> appControlMgr = DelayedSingleton<AppControlManager>::GetInstance();
    if (appControlMgr != nullptr) {
        LOG_D(BMS_TAG_INSTALLER, "Delete disposed rule when bundleName :%{public}s uninstall", bundleName.c_str());
        appControlMgr->DeleteAllDisposedRuleByBundle(oldInfo, Constants::MAIN_APP_INDEX, userId_);
    }
#endif

    auto res = RemoveDataGroupDirs(oldInfo.GetBundleName(), userId_, installParam.isKeepData);
    if (res != ERR_OK) {
        APP_LOGW("remove group dir failed for %{public}s", oldInfo.GetBundleName().c_str());
    }

    DeleteEncryptionKeyId(oldInfo.GetBundleName(), oldInfo.NeedCreateEl5Dir(), installParam.isKeepData);
    if (!installParam.isRemoveUser &&
        !SaveFirstInstallBundleInfo(bundleName, userId_, oldInfo.IsPreInstallApp(), curInnerBundleUserInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "save first install bundle info failed");
    }

    if (DeleteEl1ShaderAndArkStartupCache(oldInfo, bundleName, userId_) != ERR_OK) {
        APP_LOGW("remove el1 shader cache dir failed for %{public}s", bundleName.c_str());
    }

    if (isMultiUser) {
        LOG_D(BMS_TAG_INSTALLER, "only delete userinfo %{public}d", userId_);
        if (oldInfo.IsPreInstallApp() && isForcedUninstall) {
            LOG_I(BMS_TAG_INSTALLER, "Pre-installed app %{public}s detected, Marking as force uninstalled",
                bundleName.c_str());
            MarkIsForceUninstall(bundleName, isForcedUninstall);
        }
        RemovePluginOnlyInCurrentUser(oldInfo);
        ErrCode ret = ProcessBundleUnInstallNative(oldInfo, userId_, bundleName);
        if (ret != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "rm hnp failed");
        }
        auto res = RemoveBundleUserData(oldInfo, installParam.isKeepData, !installParam.isRemoveUser);
        if (res != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "remove bundle user data failed");
            return res;
        }
        SaveUninstallBundleInfo(bundleName, installParam.isKeepData, uninstallBundleInfo);
        if (installParam.isKeepData) {
            BundleResourceHelper::AddUninstallBundleResource(bundleName, userId_, 0);
        } else {
            DelayedSingleton<BmsUpdateSelinuxMgr>::GetInstance()->DeleteBundle(bundleName, userId_, 0);
        }
        UninstallDebugAppSandbox(bundleName, uid, oldInfo);
        if (dataMgr_->DeleteShortcutVisibleInfo(bundleName, userId_, 0) != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER,
                "DeleteShortcutVisibleInfo failed, bundleName: %{public}s, userId: %{public}d, appIndex: 0",
                bundleName.c_str(), userId_);
        }
        BundleResourceHelper::DeleteBundleResourceInfo(bundleName, userId_, true);
        bool isDriverForAllUser = OHOS::system::GetBoolParameter(ServiceConstants::IS_DRIVER_FOR_ALL_USERS, true);
        bool isEntSpaceEnable = OHOS::system::GetBoolParameter(ServiceConstants::ENTERPRISE_SPACE_ENABLE, false);
        if (!isDriverForAllUser && isEntSpaceEnable) {
            // remove drive so file for single user
            std::shared_ptr driverInstaller = std::make_shared<DriverInstaller>();
            driverInstaller->RemoveDriverSoFile(oldInfo, "", false);
        }
        return ERR_OK;
    }
    dataMgr_->DisableBundle(bundleName);

    if (!dataMgr_->UpdateBundleInstallState(bundleName, InstallState::UNINSTALL_START)) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall already start");
        return ERR_APPEXECFWK_UPDATE_BUNDLE_INSTALL_STATUS_ERROR;
    }

    std::string packageName;
    oldInfo.SetInstallMark(bundleName, packageName, InstallExceptionStatus::UNINSTALL_BUNDLE_START);
    if (!dataMgr_->SaveInnerBundleInfo(oldInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "save install mark failed");
        return ERR_APPEXECFWK_UPDATE_BUNDLE_ERROR;
    }

    SaveUninstallBundleInfo(bundleName, installParam.isKeepData, uninstallBundleInfo);
    if (installParam.isKeepData) {
        BundleResourceHelper::AddUninstallBundleResource(bundleName, userId_, 0);
    }

    ErrCode result = RemoveBundle(oldInfo, installParam.isKeepData, !installParam.isRemoveUser);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "remove whole bundle failed");
        return result;
    }

    result = ProcessBundleUnInstallNative(oldInfo, userId_, bundleName);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "rm hnp failed");
    }

    result = DeleteOldArkNativeFile(oldInfo);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "delete old arkNativeFile failed");
    }

    result = DeleteArkProfile(bundleName, userId_);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to removeArkProfile, error is %{public}d", result);
    }

    result = DeleteShaderCache(bundleName);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to DeleteShaderCache, error is %{public}d", result);
    }

    DeleteUseLessSharefilesForDefaultUser(bundleName, userId_);

    result = CleanAsanDirectory(oldInfo);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to remove asan log path, error is %{public}d", result);
    }

    enableGuard.Dismiss();
#ifdef BUNDLE_FRAMEWORK_QUICK_FIX
    std::shared_ptr<QuickFixDataMgr> quickFixDataMgr = DelayedSingleton<QuickFixDataMgr>::GetInstance();
    if (quickFixDataMgr != nullptr) {
        LOG_D(BMS_TAG_INSTALLER, "DeleteInnerAppQuickFix when bundleName :%{public}s uninstall", bundleName.c_str());
        quickFixDataMgr->DeleteInnerAppQuickFix(bundleName);
    }
#endif
    if (!DelayedSingleton<AppProvisionInfoManager>::GetInstance()->DeleteAppProvisionInfo(bundleName)) {
        LOG_W(BMS_TAG_INSTALLER, "bundleName: %{public}s delete appProvisionInfo failed", bundleName.c_str());
    }
    LOG_D(BMS_TAG_INSTALLER, "finish to process %{public}s bundle uninstall", bundleName.c_str());
    RemoveDataPreloadHapFiles(bundleName);

    // remove drive so file
    std::shared_ptr driverInstaller = std::make_shared<DriverInstaller>();
    driverInstaller->RemoveDriverSoFile(oldInfo, "", false);
    BundleResourceHelper::DeleteBundleResourceInfo(bundleName, userId_, false);
    DeleteRouterInfo(oldInfo);
    // remove profile from code signature
    RemoveProfileFromCodeSign(bundleName);
    DeleteEncryptedStatus(bundleName, uid);
    ClearDomainVerifyStatus(oldInfo.GetAppIdentifier(), bundleName);
    if (oldInfo.IsPreInstallApp() && (oldInfo.IsRemovable() || isForcedUninstall)) {
        MarkPreInstallState(bundleName, true);
        if (isForcedUninstall) {
            LOG_I(BMS_TAG_INSTALLER, "Pre-installed app %{public}s detected, Marking as force uninstalled",
                bundleName.c_str());
            MarkIsForceUninstall(bundleName, isForcedUninstall);
        }
    }

    UninstallDebugAppSandbox(bundleName, uid, oldInfo);
    if (!PatchDataMgr::GetInstance().DeleteInnerPatchInfo(bundleName)) {
        LOG_E(BMS_TAG_INSTALLER, "DeleteInnerPatchInfo failed, bundleName: %{public}s", bundleName.c_str());
    }
    DeleteCloudShader(bundleName);
    if (dataMgr_->DeleteShortcutVisibleInfo(bundleName, userId_, 0) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER,
            "DeleteShortcutVisibleInfo failed, bundleName: %{public}s, userId: %{public}d, appIndex: 0",
            bundleName.c_str(), userId_);
    }
    if (!installParam.isKeepData) {
        StopRelable(oldInfo);
    }
    return ERR_OK;
}

void BaseBundleInstaller::UninstallDebugAppSandbox(const std::string &bundleName, const int32_t uid,
    const InnerBundleInfo& innerBundleInfo)
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    LOG_D(BMS_TAG_INSTALLER, "call UninstallDebugAppSandbox start");
    bool isDeveloperMode = OHOS::system::GetBoolParameter(ServiceConstants::DEVELOPERMODE_STATE, false);
    bool isDebugApp = innerBundleInfo.GetBaseApplicationInfo().appProvisionType == Constants::APP_PROVISION_TYPE_DEBUG;
    if (isDeveloperMode && isDebugApp) {
        int32_t flagIndex = 0;
        AppSpawnRemoveSandboxDirMsg removeSandboxDirMsg;
        removeSandboxDirMsg.code = MSG_UNINSTALL_DEBUG_HAP;
        removeSandboxDirMsg.bundleName = bundleName;
        removeSandboxDirMsg.bundleIndex = innerBundleInfo.GetAppIndex();
        removeSandboxDirMsg.uid = uid;
        if (innerBundleInfo.GetApplicationBundleType() == BundleType::ATOMIC_SERVICE) {
            removeSandboxDirMsg.flags = APP_FLAGS_ATOMIC_SERVICE;
        } else {
            removeSandboxDirMsg.flags = static_cast<AppFlagsIndex>(flagIndex);
        }
        if (BundleAppSpawnClient::GetInstance().RemoveSandboxDir(removeSandboxDirMsg) != 0) {
            LOG_E(BMS_TAG_INSTALLER, "removeSandboxDir failed");
        }
    }
    LOG_D(BMS_TAG_INSTALLER, "call UninstallDebugAppSandbox end");
}

void BaseBundleInstaller::DeleteRouterInfo(const InnerBundleInfo &info, const std::string &moduleName)
{
    if (!InitDataMgr()) {
        LOG_E(BMS_TAG_INSTALLER, "init failed");
        return;
    }
    if (moduleName.empty()) {
        DeleteRouterInfoForPlugin(info);
        dataMgr_->DeleteRouterInfo(info.GetBundleName());
    } else {
        dataMgr_->DeleteRouterInfo(info.GetBundleName(), moduleName);
    }
}

void BaseBundleInstaller::DeleteRouterInfoForPlugin(const InnerBundleInfo &info)
{
    if (!InitDataMgr()) {
        return;
    }
    auto pluginBundleInfos = info.GetAllPluginBundleInfo();
    for (const auto &item : pluginBundleInfos) {
        dataMgr_->DeleteRouterInfoForPlugin(info.GetBundleName(), item.second);
    }
}

ErrCode BaseBundleInstaller::ProcessBundleUninstall(
    const std::string &bundleName, const std::string &modulePackage, const InstallParam &installParam, int32_t &uid)
{
    LOG_D(BMS_TAG_INSTALLER, "process %{public}s in %{public}s uninstall", bundleName.c_str(), modulePackage.c_str());
    if (bundleName.empty() || modulePackage.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall bundle name or module name empty");
        return ERR_APPEXECFWK_UNINSTALL_INVALID_NAME;
    }
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_UNINSTALL_BUNDLE_MGR_SERVICE_ERROR;
    }

    userId_ = GetUserId(installParam.userId);
    if (userId_ == Constants::INVALID_USERID) {
        return ERR_APPEXECFWK_INSTALL_PARAM_ERROR;
    }

    if (!dataMgr_->HasUserId(userId_)) {
        LOG_E(BMS_TAG_INSTALLER, "The user %{public}d does not exist when uninstall", userId_);
        return ERR_APPEXECFWK_USER_NOT_EXIST;
    }

    auto &mtx = dataMgr_->GetBundleMutex(bundleName);
    std::lock_guard lock {mtx};
    InnerBundleInfo oldInfo;
    bool isAppExist = false;
    if (!InitTempBundleFromCache(oldInfo, isAppExist, bundleName) || !isAppExist) {
        LOG_W(BMS_TAG_INSTALLER, "uninstall bundle info missing");
        return ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_BUNDLE;
    }
    uninstallBundleAppId_ = oldInfo.GetAppId();
    versionCode_ = oldInfo.GetVersionCode();
    appDistributionType_ = oldInfo.GetAppDistributionType();
    ScopeGuard enableGuard([&] { dataMgr_->EnableBundle(bundleName); });
    if (oldInfo.GetApplicationBundleType() == BundleType::SHARED) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall bundle is shared library");
        return ERR_APPEXECFWK_UNINSTALL_BUNDLE_IS_SHARED_LIBRARY;
    }

    InnerBundleUserInfo curInnerBundleUserInfo;
    if (!oldInfo.GetInnerBundleUserInfo(userId_, curInnerBundleUserInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "bundle(%{public}s) get user(%{public}d) failed when uninstall",
            oldInfo.GetBundleName().c_str(), userId_);
        return ERR_APPEXECFWK_USER_NOT_INSTALL_HAP;
    }

    uid = curInnerBundleUserInfo.uid;
    if (!installParam.GetForceExecuted()
        && !oldInfo.IsRemovable() && installParam.GetKillProcess()) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall system app");
        return ERR_APPEXECFWK_UNINSTALL_SYSTEM_APP_ERROR;
    }

    if (!installParam.GetForceExecuted() &&
        !oldInfo.GetUninstallState() && installParam.GetKillProcess() && !installParam.GetIsUninstallAndRecover()) {
        LOG_E(BMS_TAG_INSTALLER, "bundle : %{public}s can not be uninstalled, uninstallState : %{public}d",
            bundleName.c_str(), oldInfo.GetUninstallState());
        return ERR_APPEXECFWK_UNINSTALL_STATE_NOT_ALLOW;
    }

    bool isModuleExist = oldInfo.FindModule(modulePackage);
    if (!isModuleExist) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall bundle info missing");
        return ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_MODULE;
    }
    isBundleCrossAppSharedConfig_ = oldInfo.IsBundleCrossAppSharedConfig();

    if (!UninstallAppControl(oldInfo.GetAppId(), oldInfo.GetAppIdentifier(), userId_)) {
        LOG_D(BMS_TAG_INSTALLER, "bundleName: %{public}s is not allow uninstall", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_APP_CONTROL_DISALLOWED_UNINSTALL;
    }

    if (!dataMgr_->UpdateBundleInstallState(bundleName, InstallState::UNINSTALL_START)) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall already start");
        return ERR_APPEXECFWK_UPDATE_BUNDLE_INSTALL_STATUS_ERROR;
    }

    ScopeGuard stateGuard([&] { dataMgr_->UpdateBundleInstallState(bundleName, InstallState::INSTALL_SUCCESS); });

    // reboot scan case will not kill the bundle
    if (installParam.GetKillProcess()) {
        // kill the bundle process during uninstall.
        if (!AbilityManagerHelper::UninstallApplicationProcesses(oldInfo.GetApplicationName(), uid)) {
            LOG_E(BMS_TAG_INSTALLER, "can not kill process, uid : %{public}d", uid);
            return ERR_APPEXECFWK_UNINSTALL_KILLING_APP_ERROR;
        }
    }

    oldInfo.SetInstallMark(bundleName, modulePackage, InstallExceptionStatus::UNINSTALL_PACKAGE_START);
    if (!dataMgr_->SaveInnerBundleInfo(oldInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "save install mark failed");
        return ERR_APPEXECFWK_UPDATE_BUNDLE_ERROR;
    }
    UninstallBundleInfo uninstallBundleInfo;
    GetUninstallBundleInfo(installParam.isKeepData, userId_, oldInfo, uninstallBundleInfo);

    bool onlyInstallInUser = oldInfo.GetInnerBundleUserInfos().size() == 1;
    ErrCode result = ERR_OK;
    // if it is the only module in the bundle
    if (oldInfo.IsOnlyModule(modulePackage)) {
        LOG_I(BMS_TAG_INSTALLER, "%{public}s is only module", modulePackage.c_str());
        enableGuard.Dismiss();
        stateGuard.Dismiss();
#ifdef BUNDLE_FRAMEWORK_APP_CONTROL
        std::shared_ptr<AppControlManager> appControlMgr = DelayedSingleton<AppControlManager>::GetInstance();
        if (appControlMgr != nullptr) {
            LOG_D(BMS_TAG_INSTALLER, "Delete disposed rule when bundleName :%{public}s uninstall", bundleName.c_str());
            appControlMgr->DeleteAllDisposedRuleByBundle(oldInfo, Constants::MAIN_APP_INDEX, userId_);
        }
#endif
        if (!installParam.isRemoveUser &&
            !SaveFirstInstallBundleInfo(bundleName, userId_, oldInfo.IsPreInstallApp(), curInnerBundleUserInfo)) {
            LOG_E(BMS_TAG_INSTALLER, "save first install bundle info failed");
            return ERR_APPEXECFWK_SAVE_FIRST_INSTALL_BUNDLE_ERROR;
        }
        result = ProcessBundleUnInstallNative(oldInfo, userId_, bundleName, modulePackage);
        if (result != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "rm hnp failed");
            return result;
        }
        if (!installParam.isKeepData) {
            DelayedSingleton<BmsUpdateSelinuxMgr>::GetInstance()->DeleteBundle(bundleName, userId_, 0);
        }
        if (onlyInstallInUser) {
            result = RemoveBundle(oldInfo, installParam.isKeepData);
            if (result != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLER, "remove bundle failed");
                return result;
            }
            SaveUninstallBundleInfo(bundleName, installParam.isKeepData, uninstallBundleInfo);
            if (installParam.isKeepData) {
                BundleResourceHelper::AddUninstallBundleResource(bundleName, userId_, 0);
            }
            // remove profile from code signature
            RemoveProfileFromCodeSign(bundleName);

            ClearDomainVerifyStatus(oldInfo.GetAppIdentifier(), bundleName);

            result = DeleteOldArkNativeFile(oldInfo);
            if (result != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLER, "delete old arkNativeFile failed");
                return result;
            }

            result = DeleteArkProfile(bundleName, userId_);
            if (result != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLER, "fail to removeArkProfile, error is %{public}d", result);
                return result;
            }

            if ((result = CleanAsanDirectory(oldInfo)) != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLER, "fail to remove asan log path, error is %{public}d", result);
                return result;
            }

            RemoveDataPreloadHapFiles(bundleName);
            if (oldInfo.IsPreInstallApp() && oldInfo.IsRemovable()) {
                LOG_I(BMS_TAG_INSTALLER, "%{public}s detected, Marking as uninstalled", bundleName.c_str());
                MarkPreInstallState(bundleName, true);
            }
            DeleteRouterInfo(oldInfo);
            UninstallDebugAppSandbox(bundleName, uid, oldInfo);
            BundleResourceHelper::DeleteBundleResourceInfo(bundleName, userId_, false);
            return ERR_OK;
        }
        auto removeRes = RemoveBundleUserData(oldInfo, installParam.isKeepData);
        if (removeRes != ERR_OK) {
            return removeRes;
        }
        SaveUninstallBundleInfo(bundleName, installParam.isKeepData, uninstallBundleInfo);
        UninstallDebugAppSandbox(bundleName, uid, oldInfo);
        if (!PatchDataMgr::GetInstance().DeleteInnerPatchInfo(bundleName)) {
            LOG_E(BMS_TAG_INSTALLER, "DeleteInnerPatchInfo failed, bundleName: %{public}s", bundleName.c_str());
        }
        BundleResourceHelper::DeleteBundleResourceInfo(bundleName, userId_, true);
        return ERR_OK;
    }
    result = ProcessBundleUnInstallNative(oldInfo, oldInfo.GetUsers(), bundleName, modulePackage);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "rm hnp failed");
        return result;
    }
    if (onlyInstallInUser) {
        LOG_I(BMS_TAG_INSTALLER, "%{public}s is only install at the userId %{public}d", bundleName.c_str(), userId_);
        result = RemoveModuleAndDataDir(oldInfo, modulePackage, userId_, installParam.isKeepData);
        DeleteRouterInfo(oldInfo, modulePackage);
    }

    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "remove module dir failed");
        return result;
    }

    oldInfo.ResetAOTFlags();
    (void)DeleteOldArkNativeFile(oldInfo);
    oldInfo.SetInstallMark(bundleName, modulePackage, InstallExceptionStatus::INSTALL_FINISH);
    LOG_D(BMS_TAG_INSTALLER, "remove module %{public}s in %{public}s ", modulePackage.c_str(), bundleName.c_str());
    if (!dataMgr_->RemoveModuleInfo(bundleName, modulePackage, oldInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "RemoveModuleInfo failed");
        return ERR_APPEXECFWK_RMV_MODULE_ERROR;
    }
    std::shared_ptr driverInstaller = std::make_shared<DriverInstaller>();
    driverInstaller->RemoveDriverSoFile(oldInfo, oldInfo.GetModuleName(modulePackage), false);
    isBundleExist_ = true;
    LOG_D(BMS_TAG_INSTALLER, "finish %{public}s in %{public}s uninstall", bundleName.c_str(), modulePackage.c_str());
    return ERR_OK;
}

void BaseBundleInstaller::MarkPreInstallState(const std::string &bundleName, bool isUninstalled)
{
    LOG_I(BMS_TAG_INSTALLER, "bundle: %{public}s isUninstalled: %{public}d", bundleName.c_str(), isUninstalled);
    if (!dataMgr_) {
        LOG_E(BMS_TAG_INSTALLER, "dataMgr is nullptr");
        return;
    }

    PreInstallBundleInfo preInstallBundleInfo;
    preInstallBundleInfo.SetBundleName(bundleName);
    if (!dataMgr_->GetPreInstallBundleInfo(bundleName, preInstallBundleInfo)) {
        LOG_I(BMS_TAG_INSTALLER, "No PreInstallBundleInfo(%{public}s) in db", bundleName.c_str());
        return;
    }

    preInstallBundleInfo.SetIsUninstalled(isUninstalled);
    dataMgr_->SavePreInstallBundleInfo(bundleName, preInstallBundleInfo);
}

void BaseBundleInstaller::UpdateRouterInfo()
{
    InnerBundleInfo bundle;
    if (tempInfo_.GetTempBundleInfo(bundle)) {
        dataMgr_->UpdateRouterInfo(bundle);
    }
}

ErrCode BaseBundleInstaller::ProcessInstallBundleByBundleName(
    const std::string &bundleName, const InstallParam &installParam, int32_t &uid)
{
    LOG_D(BMS_TAG_INSTALLER, "Process Install Bundle(%{public}s) start", bundleName.c_str());
    return InnerProcessInstallByPreInstallInfo(bundleName, installParam, uid);
}

ErrCode BaseBundleInstaller::ProcessRecover(
    const std::string &bundleName, const InstallParam &installParam, int32_t &uid)
{
    LOG_D(BMS_TAG_INSTALLER, "Process Recover Bundle(%{public}s) start", bundleName.c_str());
    int32_t userId = GetUserId(installParam.userId);
    if (!CheckCanInstallPreBundle(bundleName, userId)) {
        LOG_E(BMS_TAG_INSTALLER, "Bundle(%{public}s) was force uninstalled before, not allow recover",
            bundleName.c_str());
        return ERR_APPEXECFWK_INSTALL_FORCE_UNINSTALLED_BUNDLE_NOT_ALLOW_RECOVER;
    }
    if (AccountHelper::CheckOsAccountConstraintEnabled(userId, ServiceConstants::CONSTRAINT_APPS_INSTALL)) {
        LOG_E(BMS_TAG_INSTALLER, "user %{public}d is not allowed to recover %{public}s", userId, bundleName.c_str());
        return ERR_APPEXECFWK_INSTALL_FAILED_ACCOUNT_CONSTRAINT;
    }
    ErrCode result = InnerProcessInstallByPreInstallInfo(bundleName, installParam, uid);
    return result;
}

ErrCode BaseBundleInstaller::InnerProcessInstallByPreInstallInfo(
    const std::string &bundleName, const InstallParam &installParam, int32_t &uid)
{
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_UNINSTALL_BUNDLE_MGR_SERVICE_ERROR;
    }

    userId_ = GetUserId(installParam.userId);
    if (userId_ == Constants::INVALID_USERID) {
        return ERR_APPEXECFWK_INSTALL_PARAM_ERROR;
    }

    if (!dataMgr_->HasUserId(userId_)) {
        LOG_E(BMS_TAG_INSTALLER, "The user %{public}d does not exist", userId_);
        return ERR_APPEXECFWK_USER_NOT_EXIST;
    }

    {
        auto &mtx = dataMgr_->GetBundleMutex(bundleName);
        std::lock_guard lock {mtx};
        InnerBundleInfo oldInfo;
        InitTempBundleFromCache(oldInfo, isAppExist_, bundleName);
        if (isAppExist_) {
            if (oldInfo.GetApplicationBundleType() == BundleType::SHARED) {
                LOG_D(BMS_TAG_INSTALLER, "shared bundle (%{public}s) is irrelevant to user", bundleName.c_str());
                bundleType_ = BundleType::SHARED;
                return ERR_OK;
            }

            versionCode_ = oldInfo.GetVersionCode();
            if (oldInfo.GetApplicationBundleType() == BundleType::APP_SERVICE_FWK) {
                LOG_D(BMS_TAG_INSTALLER, "Appservice (%{public}s) only install in U0", bundleName.c_str());
                bundleType_ = BundleType::APP_SERVICE_FWK;
                return ERR_OK;
            }

            if (oldInfo.HasInnerBundleUserInfo(userId_)) {
                LOG_E(BMS_TAG_INSTALLER, "App is exist in user(%{public}d)", userId_);
                return ERR_APPEXECFWK_RECOVER_INVALID_BUNDLE_NAME;
            }

            ErrCode ret = InstallNormalAppControl(
                oldInfo.GetAppId(), oldInfo.GetAppIdentifier(), userId_, installParam.isPreInstallApp);
            if (ret != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLER, "%{private}s check install app control failed", oldInfo.GetAppId().c_str());
                return ret;
            }

            ret = CheckSingleton(oldInfo, userId_);
            CHECK_RESULT(ret, "Check singleton failed %{public}d");

            ret = CheckU1Enable(oldInfo, userId_);
            CHECK_RESULT(ret, "CheckU1Enable failed %{public}d");

            ret = CheckEnterpriseResign(oldInfo);
            CHECK_RESULT(ret, "Check enterprise resign failed %{public}d");

            InnerBundleUserInfo curInnerBundleUserInfo;
            curInnerBundleUserInfo.bundleUserInfo.userId = userId_;
            curInnerBundleUserInfo.bundleName = bundleName;
            oldInfo.AddInnerBundleUserInfo(curInnerBundleUserInfo);
            ScopeGuard userGuard([&] {
                if (!dataMgr_->GetUninstallBundleInfoWithUserAndAppIndex(bundleName_, userId_,
                    Constants::INITIAL_APP_INDEX)) {
                    RemoveBundleUserData(oldInfo, false);
                }
            });
            Security::AccessToken::AccessTokenIDEx accessTokenIdEx;
            Security::AccessToken::HapInfoCheckResult checkResult;
            isKeepTokenId_ = oldInfo.HasKeepTokenIdMetadata();
            if (!RecoverHapToken(bundleName_, userId_, accessTokenIdEx, oldInfo)) {
                if (BundlePermissionMgr::InitHapToken(oldInfo, userId_, 0, accessTokenIdEx, checkResult,
                    verifyRes_.GetProvisionInfo().appServiceCapabilities) != ERR_OK) {
                    LOG_E(BMS_TAG_INSTALLER, "bundleName:%{public}s InitHapToken failed", bundleName_.c_str());
                    SetVerifyPermissionResult(checkResult);
                    return ERR_APPEXECFWK_INSTALL_GRANT_REQUEST_PERMISSIONS_FAILED;
                }
            }
            accessTokenId_ = accessTokenIdEx.tokenIdExStruct.tokenID;
            oldInfo.SetAccessTokenIdEx(accessTokenIdEx, userId_);

            auto result = CreateBundleUserData(oldInfo);
            if (result != ERR_OK) {
                return result;
            }
            std::vector<std::string> extensionDirs = oldInfo.GetAllExtensionDirs();
            createExtensionDirs_.assign(extensionDirs.begin(), extensionDirs.end());
            CreateExtensionDataDir(oldInfo);
            bundleName_ = bundleName;
            CreateScreenLockProtectionDir();
            // extract ap file
            result = ExtractAllArkProfileFile(oldInfo);
            if (result != ERR_OK) {
                LOG_W(BMS_TAG_INSTALLER, "ExtractAllArkProfileFile failed -n %{public}s", bundleName_.c_str());
            }

            userGuard.Dismiss();
            uid = oldInfo.GetUid(userId_);
            GetInstallEventInfo(oldInfo, sysEventInfo_);
            if (!dataMgr_->UpdateInnerBundleInfo(oldInfo, true)) {
                if (!dataMgr_->UpdateInnerBundleInfo(oldInfo, true)) {
                    LOG_W(BMS_TAG_INSTALLER, "save mark failed, -n:%{public}s", bundleName_.c_str());
                    return ERR_APPEXECFWK_UPDATE_BUNDLE_ERROR;
                }
            }
            SetHybridSpawn();
#ifdef BUNDLE_FRAMEWORK_DEFAULT_APP
            DefaultAppMgr::GetInstance().HandleInstallBundle(userId_, bundleName_);
#endif
            UtdHandler::InstallUtdAsync(bundleName, userId_);
            GenerateNewUserDataGroupInfos(oldInfo);
            isBundleCrossAppSharedConfig_ = oldInfo.IsBundleCrossAppSharedConfig();
            // remove userid record in preInstallBundleInfo
            PreInstallBundleInfo preInstallBundleInfo;
            preInstallBundleInfo.SetBundleName(bundleName);
            if (dataMgr_->GetPreInstallBundleInfo(bundleName, preInstallBundleInfo)) {
                preInstallBundleInfo.DeleteForceUnisntalledUser(userId_);
                dataMgr_->SavePreInstallBundleInfo(bundleName, preInstallBundleInfo);
            }
            UninstallBundleInfo uninstallBundleInfo;
            if (dataMgr_->GetUninstallBundleInfo(bundleName, uninstallBundleInfo)) {
                existBeforeKeepDataApp_ = true;
                DeleteUninstallBundleInfo(bundleName);
            }
            // process resource
            BundleResourceHelper::AddResourceInfoByBundleName(bundleName, userId_, ADD_RESOURCE_TYPE::CREATE_USER);
            SetAPIAndSdkVersions(oldInfo.GetBaseApplicationInfo().apiTargetVersion,
                oldInfo.GetBaseApplicationInfo().apiCompatibleVersion,
                oldInfo.GetBaseApplicationInfo().compileSdkVersion);
            SetUid(uid);
            return ERR_OK;
        }
    }
    bool onDemandInstall = OnDemandInstallDataMgr::GetInstance().IsOnDemandInstall(installParam);
    PreInstallBundleInfo preInstallBundleInfo;
    preInstallBundleInfo.SetBundleName(bundleName);
    if (onDemandInstall) {
        return RecoverOnDemandInstallBundle(bundleName, installParam, uid);
    }
    if (!dataMgr_->GetPreInstallBundleInfo(bundleName, preInstallBundleInfo)
        || preInstallBundleInfo.GetBundlePaths().empty()) {
        LOG_E(BMS_TAG_INSTALLER, "Get PreInstallBundleInfo failed, bundleName: %{public}s", bundleName.c_str());
        return ERR_APPEXECFWK_RECOVER_INVALID_BUNDLE_NAME;
    }
    LOG_D(BMS_TAG_INSTALLER, "Get preInstall bundlePath success");
    std::vector<std::string> pathVec;
    auto innerInstallParam = installParam;
    bool isSharedBundle = preInstallBundleInfo.GetBundlePaths().front().find(PRE_INSTALL_HSP_PATH) != std::string::npos;
    if (isSharedBundle) {
        innerInstallParam.sharedBundleDirPaths = preInstallBundleInfo.GetBundlePaths();
    } else {
        pathVec = preInstallBundleInfo.GetBundlePaths();
    }
    innerInstallParam.isPreInstallApp = true;
    innerInstallParam.removable = preInstallBundleInfo.IsRemovable();
    innerInstallParam.copyHapToInstallPath = false;
    innerInstallParam.isDataPreloadHap = IsDataPreloadHap(pathVec.empty() ? "" : pathVec.front());
    ErrCode resultCode = ProcessBundleInstall(pathVec, innerInstallParam, preInstallBundleInfo.GetAppType(), uid, true);
    if (resultCode != ERR_OK && innerInstallParam.isDataPreloadHap) {
        LOG_E(BMS_TAG_INSTALLER, "set parameter BMS_DATA_PRELOAD false");
        OHOS::system::SetParameter(ServiceConstants::BMS_DATA_PRELOAD, BMS_FALSE);
    }
    return resultCode;
}

ErrCode BaseBundleInstaller::RemoveBundle(InnerBundleInfo &info, bool isKeepData, const bool async)
{
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_NULL_PTR;
    }
    if (!dataMgr_->UpdateBundleInstallState(info.GetBundleName(), InstallState::UNINSTALL_SUCCESS, isKeepData)) {
        LOG_E(BMS_TAG_INSTALLER, "delete inner info failed");
        return ERR_APPEXECFWK_UPDATE_BUNDLE_INSTALL_STATUS_ERROR;
    }
    if (info.GetApplicationBundleType() == BundleType::ATOMIC_SERVICE) {
        int32_t uid = info.GetUid(userId_);
        if (uid != Constants::INVALID_UID) {
            LOG_I(BMS_TAG_INSTALLER, "uninstall atomic service need delete quota, bundleName:%{public}s",
                info.GetBundleName().c_str());
            std::string bundleDataDir = ServiceConstants::BUNDLE_APP_DATA_BASE_DIR + ServiceConstants::BUNDLE_EL[1] +
                ServiceConstants::PATH_SEPARATOR + std::to_string(userId_) + ServiceConstants::BASE +
                info.GetBundleName();
            PrepareBundleDirQuota(info.GetBundleName(), uid, bundleDataDir, 0);
        }
    }
    ErrCode result = RemoveBundleAndDataDir(info, isKeepData, async);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "remove bundle dir failed");
    }

    accessTokenId_ = info.GetAccessTokenId(userId_);
    isKeepTokenId_ = info.HasKeepTokenIdMetadata();
    if (!isKeepData) {
        bool keepTokenParam = isKeepTokenId_ && async;
        LOG_NOFUNC_I(BMS_TAG_INSTALLER, "DeleteAccessTokenId keepTokenParam=%{public}d", keepTokenParam);
        if (BundlePermissionMgr::DeleteAccessTokenId(accessTokenId_, keepTokenParam) !=
            AccessToken::AccessTokenKitRet::RET_SUCCESS) {
            LOG_E(BMS_TAG_INSTALLER, "delete accessToken failed");
        }
        DelayedSingleton<BmsUpdateSelinuxMgr>::GetInstance()->DeleteBundle(info.GetBundleName(), userId_, 0);
    }

    return ERR_OK;
}

ErrCode BaseBundleInstaller::ProcessBundleInstallNative(const InnerBundleInfo &info, int32_t userId, bool removeDir)
{
    auto hnpPackages = info.GetInnerModuleInfoHnpInfo(info.GetCurModuleName()).value_or(std::vector<HnpPackage>{});
    if (!hnpPackages.empty()) {
        LOG_I(BMS_TAG_INSTALLER, "hnp install: %{public}s, %{public}d", info.GetCurModuleName().c_str(), userId);
        std::string moduleHnpsPath = info.GetInnerModuleInfoHnpPath(info.GetCurModuleName());
        InstallHnpParam installHnpParam;
        installHnpParam.userId = std::to_string(userId);
        installHnpParam.hnpRootPath = moduleHnpsPath;
        installHnpParam.hapPath = modulePath_;
        installHnpParam.cpuAbi = info.GetCpuAbi();
        installHnpParam.packageName = info.GetBundleName();
        installHnpParam.appIdentifier = appIdentifier_;
        for (auto &hnpPackage : hnpPackages) {
            if (hnpPackage.independentSign) {
                installHnpParam.hnpPaths.emplace_back(hnpPackage.type + ServiceConstants::PATH_SEPARATOR
                    + hnpPackage.package);
            }
        }
        ErrCode ret = InstalldClient::GetInstance()->ProcessBundleInstallNative(installHnpParam);
        isHnpInstalled_ = true;
        if (ret != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "installing the native package failed. error code: %{public}d", ret);
            return ret;
        }
        if (removeDir) {
            if ((InstalldClient::GetInstance()->RemoveDir(moduleHnpsPath)) != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLER, "delete dir %{public}s failed", moduleHnpsPath.c_str());
            }
        }
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::ProcessBundleInstallNative(const InnerBundleInfo &info,
    const std::unordered_set<int32_t> &userIds)
{
    for (int32_t userId : userIds) {
        ErrCode ret = ProcessBundleInstallNative(info, userId, false);
        if (ret != ERR_OK) {
            return ret;
        }
    }
    std::string moduleHnpsPath = info.GetInnerModuleInfoHnpPath(info.GetCurModuleName());
    if ((InstalldClient::GetInstance()->RemoveDir(moduleHnpsPath)) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "delete dir %{public}s failed", moduleHnpsPath.c_str());
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::ProcessBundleUnInstallNative(const InnerBundleInfo &info,
    int32_t userId, const std::string &bundleName, const std::string &moduleName)
{
    if (info.GetInnerModuleInfoHnpInfo(moduleName)) {
        LOG_I(BMS_TAG_INSTALLER, "hnp uninstall: %{public}s, %{public}d", moduleName.c_str(), userId);
        ErrCode ret = InstalldClient::GetInstance()->ProcessBundleUnInstallNative(
            std::to_string(userId).c_str(), bundleName.c_str());
        if (ret != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "uninstalling the native package failed. error code: %{public}d", ret);
            return ret;
        }
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::ProcessBundleUnInstallNative(const InnerBundleInfo &info,
    const std::unordered_set<int32_t> &userIds, const std::string &bundleName, const std::string &moduleName)
{
    for (int32_t userId : userIds) {
        ErrCode ret = ProcessBundleUnInstallNative(info, userId, bundleName, moduleName);
        if (ret != ERR_OK) {
            return ret;
        }
    }
    return ERR_OK;
}

void BaseBundleInstaller::RollbackHnpInstall(const std::string &bundleName, const std::unordered_set<int32_t> &userIds)
{
    for (int32_t userId : userIds) {
        ErrCode ret = InstalldClient::GetInstance()->ProcessBundleUnInstallNative(
            std::to_string(userId).c_str(), bundleName.c_str());
        LOG_I(BMS_TAG_INSTALLER, "hnp rollback: %{public}s, %{public}d, ret: %{public}d",
            bundleName.c_str(), userId, ret);
    }
}

ErrCode BaseBundleInstaller::ProcessBundleInstallStatus(InnerBundleInfo &info, int32_t &uid, const bool isDebugGrant)
{
    modulePackage_ = info.GetCurrentModulePackage();
    LOG_D(BMS_TAG_INSTALLER, "ProcessBundleInstallStatus with bundleName %{public}s and packageName %{public}s",
        bundleName_.c_str(), modulePackage_.c_str());
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_NULL_PTR;
    }
    if (!dataMgr_->UpdateBundleInstallState(bundleName_, InstallState::INSTALL_START)) {
        LOG_E(BMS_TAG_INSTALLER, "install already start");
        return ERR_APPEXECFWK_INSTALL_STATE_ERROR;
    }

    Security::AccessToken::AccessTokenIDEx accessTokenIdEx;
    Security::AccessToken::HapInfoCheckResult checkResult;
    isKeepTokenId_ = info.HasKeepTokenIdMetadata();
    if (!RecoverHapToken(bundleName_, userId_, accessTokenIdEx, info, isDebugGrant)) {
        if (BundlePermissionMgr::InitHapToken(info, userId_, 0, accessTokenIdEx, checkResult,
            verifyRes_.GetProvisionInfo().appServiceCapabilities, isDebugGrant) != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "bundleName:%{public}s InitHapToken failed", bundleName_.c_str());
            SetVerifyPermissionResult(checkResult);
            return ERR_APPEXECFWK_INSTALL_GRANT_REQUEST_PERMISSIONS_FAILED;
        }
    }
    accessTokenId_ = accessTokenIdEx.tokenIdExStruct.tokenID;
    info.SetAccessTokenIdEx(accessTokenIdEx, userId_);

    info.SetInstallMark(bundleName_, modulePackage_, InstallExceptionStatus::INSTALL_START);

    ScopeGuard stateGuard([&] { dataMgr_->UpdateBundleInstallState(bundleName_, InstallState::INSTALL_FAIL); });
    ErrCode result = CreateBundleAndDataDir(info);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "create bundle and data dir failed");
        return result;
    }

    ScopeGuard bundleGuard([&] {
        RemoveBundleAndDataDir(info, dataMgr_->GetUninstallBundleInfoWithUserAndAppIndex(bundleName_, userId_,
            Constants::INITIAL_APP_INDEX));
    });
    std::string modulePath = info.GetAppCodePath() + ServiceConstants::PATH_SEPARATOR + modulePackage_;
    result = ExtractModule(info, modulePath);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "extract module failed");
        return result;
    }

    result = ProcessBundleInstallNative(info, userId_);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "Install Native failed");
        return result;
    }

    uid = info.GetUid(userId_);
    int64_t currentTime = BundleUtil::GetCurrentTimeMs();
    info.SetBundleInstallTime(currentTime, userId_);
    SetFirstInstallTime(bundleName_, currentTime, info);
    tempInfo_.SetTempBundleInfo(info);
    stateGuard.Dismiss();
    bundleGuard.Dismiss();

    LOG_D(BMS_TAG_INSTALLER, "finish to call processBundleInstallStatus");
    return ERR_OK;
}

bool BaseBundleInstaller::AllowSingletonChange(const std::string &bundleName)
{
    return ServiceConstants::SINGLETON_WHITE_LIST.find(bundleName) != ServiceConstants::SINGLETON_WHITE_LIST.end();
}

ErrCode BaseBundleInstaller::ProcessBundleUpdateStatus(
    InnerBundleInfo &oldInfo, InnerBundleInfo &newInfo, bool isReplace, bool killProcess)
{
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_INSTALL_STATE_ERROR;
    }
    modulePackage_ = newInfo.GetCurrentModulePackage();
    if (modulePackage_.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "get current package failed");
        return ERR_APPEXECFWK_INSTALL_PARAM_ERROR;
    }

    if (isFeatureNeedUninstall_) {
        uninstallModuleVec_.emplace_back(modulePackage_);
    }

    if (oldInfo.IsSingleton() != newInfo.IsSingleton()) {
        if ((oldInfo.IsSingleton() && !newInfo.IsSingleton()) && newInfo.IsPreInstallApp()
            && AllowSingletonChange(newInfo.GetBundleName())) {
            singletonState_ = SingletonState::SINGLETON_TO_NON;
        } else if ((!oldInfo.IsSingleton() && newInfo.IsSingleton()) && newInfo.IsPreInstallApp()
            && AllowSingletonChange(newInfo.GetBundleName())) {
            singletonState_ = SingletonState::NON_TO_SINGLETON;
        } else {
            LOG_E(BMS_TAG_INSTALLER, "Singleton not allow changed");
            return ERR_APPEXECFWK_INSTALL_SINGLETON_INCOMPATIBLE;
        }
        LOG_I(BMS_TAG_INSTALLER, "Singleton %{public}s changed", newInfo.GetBundleName().c_str());
    }

    auto result = CheckOverlayUpdate(oldInfo, newInfo, userId_);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "CheckOverlayUpdate failed due to %{public}d", result);
        return result;
    }

    LOG_D(BMS_TAG_INSTALLER, "%{public}s, %{public}s", newInfo.GetBundleName().c_str(), modulePackage_.c_str());
    if (!dataMgr_->UpdateBundleInstallState(bundleName_, InstallState::UPDATING_START)) {
        LOG_E(BMS_TAG_INSTALLER, "update already start");
        return ERR_APPEXECFWK_INSTALL_STATE_ERROR;
    }

    if (!CheckAppIdentifier(oldInfo.GetAppIdentifier(), newInfo.GetAppIdentifier(),
        oldInfo.GetProvisionId(), newInfo.GetProvisionId())) {
        return ERR_APPEXECFWK_INSTALL_FAILED_INCONSISTENT_SIGNATURE;
    }
    LOG_D(BMS_TAG_INSTALLER, "ProcessBundleUpdateStatus killProcess = %{public}d", killProcess);
    // now there are two cases for updating:
    // 1. bundle exist, hap exist, update hap
    // 2. bundle exist, install new hap
    bool isModuleExist = oldInfo.FindModule(modulePackage_);
    if (isModuleExist) {
        isModuleUpdate_ = true;
    }
    newInfo.RestoreFromOldInfo(oldInfo);
    result = isModuleExist ? ProcessModuleUpdate(newInfo, oldInfo,
        isReplace, killProcess) : ProcessNewModuleInstall(newInfo, oldInfo);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "install module failed %{public}d", result);
        return result;
    }
    LOG_D(BMS_TAG_INSTALLER, "finish to call ProcessBundleUpdateStatus");
    return ERR_OK;
}

bool BaseBundleInstaller::CheckAppIdentifier(const std::string &oldAppIdentifier, const std::string &newAppIdentifier,
    const std::string &oldAppId, const std::string &newAppId)
{
    // for versionCode update
    if (!oldAppIdentifier.empty() &&
        !newAppIdentifier.empty() &&
        oldAppIdentifier == newAppIdentifier) {
        return true;
    }
    if (oldAppId == newAppId) {
        return true;
    }
    LOG_E(BMS_TAG_INSTALLER, "the appIdentifier or appId of the new bundle is not the same as old one");
    return false;
}

ErrCode BaseBundleInstaller::ProcessNewModuleInstall(InnerBundleInfo &newInfo, InnerBundleInfo &oldInfo)
{
    LOG_D(BMS_TAG_INSTALLER, "ProcessNewModuleInstall %{public}s, userId: %{public}d",
        newInfo.GetBundleName().c_str(), userId_);
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_NULL_PTR;
    }
    if ((!isFeatureNeedUninstall_ && !otaInstall_) && (newInfo.HasEntry() && oldInfo.HasEntry())) {
        LOG_E(BMS_TAG_INSTALLER, "install more than one entry module");
        return ERR_APPEXECFWK_INSTALL_ENTRY_ALREADY_EXIST;
    }

    if ((!isFeatureNeedUninstall_ && !otaInstall_) &&
        bundleInstallChecker_->IsContainModuleName(newInfo, oldInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "moduleName is already existed");
        return ERR_APPEXECFWK_INSTALL_NOT_UNIQUE_DISTRO_MODULE_NAME;
    }

    // same version need to check app label
    ErrCode result = ERR_OK;
    if (!otaInstall_ && (oldInfo.GetVersionCode() == newInfo.GetVersionCode())) {
        result = CheckAppLabel(oldInfo, newInfo);
        if (result != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "CheckAppLabel failed %{public}d", result);
            return result;
        }
        if (!CheckDuplicateProxyData(newInfo, oldInfo)) {
            LOG_E(BMS_TAG_INSTALLER, "CheckDuplicateProxyData with old info failed");
            return ERR_APPEXECFWK_INSTALL_CHECK_PROXY_DATA_URI_FAILED;
        }
    }
    if (isAppExist_) {
        oldInfo.SetInstallMark(bundleName_, modulePackage_, InstallExceptionStatus::UPDATING_NEW_START);
    }
    std::string modulePath = GetModulePath(newInfo, isFeatureNeedUninstall_, false);
    result = ExtractModule(newInfo, modulePath);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "extract module and rename failed");
        return result;
    }

    result = ProcessBundleInstallNative(newInfo, userId_);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "Install Native failed");
        return result;
    }

    ScopeGuard moduleGuard([&] { RemoveModuleDir(modulePath); });
    if (!dataMgr_->UpdateBundleInstallState(bundleName_, InstallState::UPDATING_SUCCESS)) {
        LOG_E(BMS_TAG_INSTALLER, "new moduleupdate state failed");
        return ERR_APPEXECFWK_UPDATE_BUNDLE_INSTALL_STATUS_ERROR;
    }

    oldInfo.SetBundleUpdateTimeForAllUser(BundleUtil::GetCurrentTimeMs());
    if ((result = ProcessAsanDirectory(newInfo)) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "process asan log directory failed");
        return result;
    }
    if (!dataMgr_->AddNewModuleInfo(newInfo, oldInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "add module %{public}s to innerBundleInfo %{public}s failed",
            modulePackage_.c_str(), bundleName_.c_str());
        return ERR_APPEXECFWK_ADD_MODULE_ERROR;
    }
    tempInfo_.SetTempBundleInfo(oldInfo);
    moduleGuard.Dismiss();
    return ERR_OK;
}

ErrCode BaseBundleInstaller::ProcessModuleUpdate(InnerBundleInfo &newInfo,
    InnerBundleInfo &oldInfo, bool isReplace, bool killProcess)
{
    LOG_D(BMS_TAG_INSTALLER, "bundleName :%{public}s, moduleName: %{public}s, userId: %{public}d",
        newInfo.GetBundleName().c_str(), newInfo.GetCurrentModulePackage().c_str(), userId_);
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_NULL_PTR;
    }
    // update module type is forbidden
    if ((!isFeatureNeedUninstall_ && !otaInstall_) && (newInfo.HasEntry() && oldInfo.HasEntry())) {
        if (!oldInfo.IsEntryModule(modulePackage_)) {
            LOG_E(BMS_TAG_INSTALLER, "install more than one entry module");
            return ERR_APPEXECFWK_INSTALL_ENTRY_ALREADY_EXIST;
        }
    }

    if ((!isFeatureNeedUninstall_ && !otaInstall_) &&
        !bundleInstallChecker_->IsExistedDistroModule(newInfo, oldInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "moduleName is inconsistent in the updating hap");
        return ERR_APPEXECFWK_INSTALL_INCONSISTENT_MODULE_NAME;
    }

    ErrCode result = ERR_OK;
    if (!otaInstall_ && (versionCode_ == oldInfo.GetVersionCode())) {
        if (((result = CheckAppLabel(oldInfo, newInfo)) != ERR_OK)) {
            LOG_E(BMS_TAG_INSTALLER, "CheckAppLabel failed %{public}d", result);
            return result;
        }

        if (!isReplace) {
            if (hasInstalledInUser_) {
                LOG_E(BMS_TAG_INSTALLER, "fail to install already existing bundle using normal flag");
                return ERR_APPEXECFWK_INSTALL_ALREADY_EXIST;
            }

            // app versionCode equals to the old and do not need to update module
            // and only need to update userInfo
            newInfo.SetOnlyCreateBundleUser(true);
            if (!dataMgr_->UpdateBundleInstallState(bundleName_, InstallState::UPDATING_SUCCESS)) {
                LOG_E(BMS_TAG_INSTALLER, "update state failed");
                return ERR_APPEXECFWK_INSTALL_STATE_ERROR;
            }
            return ERR_OK;
        }
    }
#ifdef BUNDLE_FRAMEWORK_OVERLAY_INSTALLATION
    result = OverlayDataMgr::GetInstance()->UpdateOverlayModule(newInfo, oldInfo);
    CHECK_RESULT(result, "UpdateOverlayModule failed %{public}d");
#endif

    LOG_D(BMS_TAG_INSTALLER, "ProcessModuleUpdate killProcess = %{public}d", killProcess);
    // reboot scan case will not kill the bundle
    if (killProcess) {
        UpdateKillApplicationProcess(oldInfo);
    }

    oldInfo.SetInstallMark(bundleName_, modulePackage_, InstallExceptionStatus::UPDATING_EXISTED_START);
    result = CheckArkProfileDir(newInfo, oldInfo);
    CHECK_RESULT(result, "CheckArkProfileDir failed %{public}d");
    auto hnpPackageOldInfos = oldInfo.GetInnerModuleInfoHnpInfo(newInfo.GetCurModuleName());
    if (hnpPackageOldInfos) {
        ErrCode ret = ProcessBundleUnInstallNative(oldInfo, oldInfo.GetUsers(), bundleName_,
            newInfo.GetCurModuleName());
        if (ret != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "remove nativeBundle failed");
            return ret;
        }
    }
    result = ProcessAsanDirectory(newInfo);
    CHECK_RESULT(result, "process asan log directory failed %{public}d");

    result = ExtractModule(newInfo, GetModulePath(newInfo, isFeatureNeedUninstall_, true));
    CHECK_RESULT(result, "extract module and rename failed %{public}d");

    result = ProcessBundleInstallNative(newInfo, oldInfo.GetUsers());
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "Install Native failed");
        return result;
    }

    if (!dataMgr_->UpdateBundleInstallState(bundleName_, InstallState::UPDATING_SUCCESS)) {
        LOG_E(BMS_TAG_INSTALLER, "old module update state failed");
        return ERR_APPEXECFWK_UPDATE_BUNDLE_INSTALL_STATUS_ERROR;
    }

    newInfo.RestoreModuleInfo(oldInfo);
    oldInfo.SetInstallMark(bundleName_, modulePackage_, InstallExceptionStatus::UPDATING_FINISH);
    oldInfo.SetBundleUpdateTimeForAllUser(BundleUtil::GetCurrentTimeMs());
    if (!dataMgr_->UpdateInnerBundleInfo(newInfo, oldInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "update innerBundleInfo %{public}s failed", bundleName_.c_str());
        return ERR_APPEXECFWK_UPDATE_BUNDLE_ERROR;
    }
    tempInfo_.SetTempBundleInfo(oldInfo);
    needDeleteQuickFixInfo_ = true;
    return ERR_OK;
}

void BaseBundleInstaller::ProcessQuickFixWhenInstallNewModule(const InstallParam &installParam,
    const std::unordered_map<std::string, InnerBundleInfo> &newInfos)
{
#ifdef BUNDLE_FRAMEWORK_QUICK_FIX
    // hqf extract diff file or apply diff patch failed does not affect the hap installation
    InnerBundleInfo bundleInfo;
    if (!GetTempBundleInfo(bundleInfo)) {
        return;
    }
    for (auto &info : newInfos) {
        modulePackage_ = info.second.GetCurrentModulePackage();
        if (!installedModules_[modulePackage_]) {
            modulePath_ = info.first;
            if (bundleInfo.IsEncryptedMoudle(modulePackage_) && installParam.copyHapToInstallPath) {
                modulePath_ = GetHapPath(info.second);
            }
            ProcessHqfInfo(bundleInfo, info.second);
        }
    }
#endif
}

void BaseBundleInstaller::ProcessHqfInfo(
    const InnerBundleInfo &oldInfo, const InnerBundleInfo &newInfo)
{
#ifdef BUNDLE_FRAMEWORK_QUICK_FIX
    LOG_D(BMS_TAG_INSTALLER, "bundleName: %{public}s, moduleName: %{public}s", bundleName_.c_str(),
        modulePackage_.c_str());
    std::string cpuAbi;
    std::string nativeLibraryPath;
    if (!newInfo.FetchNativeSoAttrs(modulePackage_, cpuAbi, nativeLibraryPath)) {
        LOG_I(BMS_TAG_INSTALLER, "No native so, bundleName: %{public}s, moduleName: %{public}s", bundleName_.c_str(),
            modulePackage_.c_str());
        return;
    }
    auto pos = nativeLibraryPath.rfind(ServiceConstants::LIBS);
    if (pos != std::string::npos) {
        nativeLibraryPath = nativeLibraryPath.substr(pos, nativeLibraryPath.length() - pos);
    }

    ErrCode ret = ProcessDeployedHqfInfo(
        nativeLibraryPath, cpuAbi, newInfo, oldInfo.GetAppQuickFix());
    if (ret != ERR_OK) {
        LOG_W(BMS_TAG_INSTALLER, "ProcessDeployedHqfInfo failed, errcode: %{public}d", ret);
        return;
    }

    ret = ProcessDeployingHqfInfo(nativeLibraryPath, cpuAbi, newInfo);
    if (ret != ERR_OK) {
        LOG_W(BMS_TAG_INSTALLER, "ProcessDeployingHqfInfo failed, errcode: %{public}d", ret);
        return;
    }

    LOG_D(BMS_TAG_INSTALLER, "ProcessHqfInfo end");
#endif
}

ErrCode BaseBundleInstaller::ProcessDeployedHqfInfo(const std::string &nativeLibraryPath,
    const std::string &cpuAbi, const InnerBundleInfo &newInfo, const AppQuickFix &oldAppQuickFix)
{
#ifdef BUNDLE_FRAMEWORK_QUICK_FIX
    LOG_D(BMS_TAG_INSTALLER, "ProcessDeployedHqfInfo");
    if (dataMgr_ == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "dataMgr_ is nullptr");
        return ERR_APPEXECFWK_NULL_PTR;
    }
    auto appQuickFix = oldAppQuickFix;
    AppqfInfo &appQfInfo = appQuickFix.deployedAppqfInfo;
    if (isFeatureNeedUninstall_ || appQfInfo.hqfInfos.empty()) {
        LOG_D(BMS_TAG_INSTALLER, "No need ProcessDeployedHqfInfo");
        return ERR_OK;
    }

    ErrCode ret = ProcessDiffFiles(appQfInfo, nativeLibraryPath, cpuAbi);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "ProcessDeployedHqfInfo failed, errcode: %{public}d", ret);
        return ret;
    }

    std::string newSoPath = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR + bundleName_ +
        ServiceConstants::PATH_SEPARATOR + ServiceConstants::PATCH_PATH +
        std::to_string(appQfInfo.versionCode) + ServiceConstants::PATH_SEPARATOR + nativeLibraryPath;
    if (!BundleUtil::IsExistDirNoLog(newSoPath)) {
        LOG_E(BMS_TAG_INSTALLER, "Patch no diff file");
        return ERR_OK;
    }

    ret = UpdateLibAttrs(newInfo, cpuAbi, nativeLibraryPath, appQfInfo);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "UpdateModuleLib failed, errcode: %{public}d", ret);
        return ret;
    }

    InnerBundleInfo innerBundleInfo;
    if (!GetTempBundleInfo(innerBundleInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "Fetch bundleInfo(%{public}s) failed", bundleName_.c_str());
        return ERR_APPEXECFWK_GET_INSTALL_TEMP_BUNDLE_ERROR;
    }

    innerBundleInfo.SetAppQuickFix(appQuickFix);
    if (!tempInfo_.SetTempBundleInfo(innerBundleInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "update quickfix innerbundleInfo failed");
        return ERR_BUNDLEMANAGER_QUICK_FIX_INTERNAL_ERROR;
    }
#endif
    return ERR_OK;
}

ErrCode BaseBundleInstaller::ProcessDeployingHqfInfo(
    const std::string &nativeLibraryPath, const std::string &cpuAbi, const InnerBundleInfo &newInfo) const
{
#ifdef BUNDLE_FRAMEWORK_QUICK_FIX
    LOG_D(BMS_TAG_INSTALLER, "ProcessDeployingHqfInfo");
    std::shared_ptr<QuickFixDataMgr> quickFixDataMgr = DelayedSingleton<QuickFixDataMgr>::GetInstance();
    if (quickFixDataMgr == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "quick fix data mgr is nullptr");
        return ERR_BUNDLEMANAGER_QUICK_FIX_INTERNAL_ERROR;
    }

    InnerAppQuickFix innerAppQuickFix;
    if (!quickFixDataMgr->QueryInnerAppQuickFix(bundleName_, innerAppQuickFix)) {
        return ERR_OK;
    }

    auto appQuickFix = innerAppQuickFix.GetAppQuickFix();
    AppqfInfo &appQfInfo = appQuickFix.deployingAppqfInfo;
    ErrCode ret = ProcessDiffFiles(appQfInfo, nativeLibraryPath, cpuAbi);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "failed errcode: %{public}d path: %{public}s", ret, nativeLibraryPath.c_str());
        return ret;
    }

    std::string newSoPath = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR + bundleName_ +
        ServiceConstants::PATH_SEPARATOR + ServiceConstants::PATCH_PATH +
        std::to_string(appQfInfo.versionCode) + ServiceConstants::PATH_SEPARATOR + nativeLibraryPath;
    if (!BundleUtil::IsExistDirNoLog(newSoPath)) {
        LOG_E(BMS_TAG_INSTALLER, "Patch no diff file");
        return ERR_OK;
    }

    ret = UpdateLibAttrs(newInfo, cpuAbi, nativeLibraryPath, appQfInfo);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "UpdateModuleLib failed, errcode: %{public}d", ret);
        return ret;
    }

    innerAppQuickFix.SetAppQuickFix(appQuickFix);
    if (!quickFixDataMgr->SaveInnerAppQuickFix(innerAppQuickFix)) {
        LOG_E(BMS_TAG_INSTALLER, "bundleName: %{public}s, inner app quick fix save failed", bundleName_.c_str());
        return ERR_BUNDLEMANAGER_QUICK_FIX_SAVE_APP_QUICK_FIX_FAILED;
    }
#endif
    return ERR_OK;
}

ErrCode BaseBundleInstaller::UpdateLibAttrs(const InnerBundleInfo &newInfo,
    const std::string &cpuAbi, const std::string &nativeLibraryPath, AppqfInfo &appQfInfo) const
{
#ifdef BUNDLE_FRAMEWORK_QUICK_FIX
    auto newNativeLibraryPath = ServiceConstants::PATCH_PATH +
        std::to_string(appQfInfo.versionCode) + ServiceConstants::PATH_SEPARATOR + nativeLibraryPath;
    auto moduleName = newInfo.GetCurModuleName();
    bool isLibIsolated = newInfo.IsLibIsolated(moduleName);
    if (!isLibIsolated) {
        appQfInfo.nativeLibraryPath = newNativeLibraryPath;
        appQfInfo.cpuAbi = cpuAbi;
        return ERR_OK;
    }

    for (auto &hqfInfo : appQfInfo.hqfInfos) {
        if (hqfInfo.moduleName != moduleName) {
            continue;
        }

        hqfInfo.nativeLibraryPath = newNativeLibraryPath;
        hqfInfo.cpuAbi = cpuAbi;
        if (!BundleUtil::StartWith(appQfInfo.nativeLibraryPath, ServiceConstants::PATCH_PATH)) {
            appQfInfo.nativeLibraryPath.clear();
        }

        return ERR_OK;
    }

    return ERR_BUNDLEMANAGER_QUICK_FIX_MODULE_NAME_NOT_EXIST;
#else
    return ERR_OK;
#endif
}

bool BaseBundleInstaller::CheckHapLibsWithPatchLibs(
    const std::string &nativeLibraryPath, const std::string &hqfLibraryPath) const
{
#ifdef BUNDLE_FRAMEWORK_QUICK_FIX
    if (!hqfLibraryPath.empty()) {
        auto position = hqfLibraryPath.find(ServiceConstants::PATH_SEPARATOR);
        if (position == std::string::npos) {
            return false;
        }

        auto newHqfLibraryPath = hqfLibraryPath.substr(position);
        if (!BundleUtil::EndWith(nativeLibraryPath, newHqfLibraryPath)) {
            LOG_E(BMS_TAG_INSTALLER, "error: nativeLibraryPath not same, newInfo: %{public}s, hqf: %{public}s",
                nativeLibraryPath.c_str(), newHqfLibraryPath.c_str());
            return false;
        }
    }
#endif
    return true;
}

bool BaseBundleInstaller::ExtractSoFiles(const std::string &soPath, const std::string &cpuAbi) const
{
    ExtractParam extractParam;
    extractParam.extractFileType = ExtractFileType::SO;
    extractParam.srcPath = modulePath_;
    extractParam.targetPath = soPath;
    extractParam.cpuAbi = cpuAbi;
    if (InstalldClient::GetInstance()->ExtractFiles(extractParam) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "bundleName: %{public}s moduleName: %{public}s extract so failed", bundleName_.c_str(),
            modulePackage_.c_str());
        return false;
    }
    return true;
}

bool BaseBundleInstaller::ExtractEncryptedSoFiles(const InnerBundleInfo &info,
    const std::string &tmpSoPath, int32_t uid) const
{
    LOG_D(BMS_TAG_INSTALLER, "start to extract decoded so files to tmp path");
    std::string cpuAbi = "";
    std::string nativeLibraryPath = "";
    bool isSoExisted = info.FetchNativeSoAttrs(info.GetCurrentModulePackage(), cpuAbi, nativeLibraryPath);
    if (!isSoExisted) {
        LOG_D(BMS_TAG_INSTALLER, "so is not existed");
        return true;
    }
    std::string realSoFilesPath;
    if (info.IsCompressNativeLibs(info.GetCurModuleName())) {
        realSoFilesPath.append(Constants::BUNDLE_CODE_DIR).append(ServiceConstants::PATH_SEPARATOR)
            .append(bundleName_).append(ServiceConstants::PATH_SEPARATOR).append(nativeLibraryPath);
        if (realSoFilesPath.back() != ServiceConstants::PATH_SEPARATOR[0]) {
            realSoFilesPath += ServiceConstants::PATH_SEPARATOR;
        }
    }
    LOG_D(BMS_TAG_INSTALLER, "real path %{public}s tmpPath %{public}s", realSoFilesPath.c_str(), tmpSoPath.c_str());
    return InstalldClient::GetInstance()->ExtractEncryptedSoFiles(modulePath_, realSoFilesPath, cpuAbi,
        tmpSoPath, uid) == ERR_OK;
}

ErrCode BaseBundleInstaller::ProcessDiffFiles(const AppqfInfo &appQfInfo, const std::string &nativeLibraryPath,
    const std::string &cpuAbi) const
{
#ifdef BUNDLE_FRAMEWORK_QUICK_FIX
    const std::string moduleName = modulePackage_;
    auto iter = find_if(appQfInfo.hqfInfos.begin(), appQfInfo.hqfInfos.end(),
        [&moduleName](const auto &hqfInfo) {
        return hqfInfo.moduleName == moduleName;
    });
    if (iter != appQfInfo.hqfInfos.end()) {
        std::string oldSoPath = std::string(ServiceConstants::HAP_COPY_PATH) + ServiceConstants::PATH_SEPARATOR +
            bundleName_ + ServiceConstants::TMP_SUFFIX + ServiceConstants::LIBS;
        ScopeGuard guardRemoveOldSoPath([oldSoPath] {InstalldClient::GetInstance()->RemoveDir(oldSoPath);});

        InnerBundleInfo innerBundleInfo;
        if (!GetTempBundleInfo(innerBundleInfo)) {
            LOG_E(BMS_TAG_INSTALLER, "Fetch bundleInfo(%{public}s) failed", bundleName_.c_str());
            return ERR_BUNDLEMANAGER_QUICK_FIX_BUNDLE_NAME_NOT_EXIST;
        }

        int32_t bundleUid = Constants::INVALID_UID;
        if (innerBundleInfo.IsEncryptedMoudle(modulePackage_)) {
            InnerBundleUserInfo innerBundleUserInfo;
            if (!innerBundleInfo.GetInnerBundleUserInfo(Constants::ALL_USERID, innerBundleUserInfo)) {
                LOG_E(BMS_TAG_INSTALLER, "no user info of bundle %{public}s", bundleName_.c_str());
                return ERR_BUNDLEMANAGER_QUICK_FIX_BUNDLE_NAME_NOT_EXIST;
            }
            bundleUid = innerBundleUserInfo.uid;
            if (!ExtractEncryptedSoFiles(innerBundleInfo, oldSoPath, bundleUid)) {
                LOG_W(BMS_TAG_INSTALLER, "module:%{public}s has no so file", moduleName.c_str());
                return ERR_BUNDLEMANAGER_QUICK_FIX_EXTRACT_DIFF_FILES_FAILED;
            }
        } else {
            if (!ExtractSoFiles(oldSoPath, cpuAbi)) {
                return ERR_BUNDLEMANAGER_QUICK_FIX_EXTRACT_DIFF_FILES_FAILED;
            }
        }

        const std::string tempDiffPath = std::string(ServiceConstants::HAP_COPY_PATH) +
            ServiceConstants::PATH_SEPARATOR +
            bundleName_ + ServiceConstants::TMP_SUFFIX;
        ScopeGuard removeDiffPath([tempDiffPath] { InstalldClient::GetInstance()->RemoveDir(tempDiffPath); });
        ErrCode ret = InstalldClient::GetInstance()->ExtractDiffFiles(iter->hqfFilePath, tempDiffPath, cpuAbi);
        if (ret != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "error: ExtractDiffFiles failed errcode :%{public}d", ret);
            return ERR_BUNDLEMANAGER_QUICK_FIX_EXTRACT_DIFF_FILES_FAILED;
        }

        std::string newSoPath = std::string(Constants::BUNDLE_CODE_DIR) +
            ServiceConstants::PATH_SEPARATOR + bundleName_ +
            ServiceConstants::PATH_SEPARATOR + ServiceConstants::PATCH_PATH +
            std::to_string(appQfInfo.versionCode) + ServiceConstants::PATH_SEPARATOR + nativeLibraryPath;
        ret = InstalldClient::GetInstance()->ApplyDiffPatch(oldSoPath, tempDiffPath, newSoPath, bundleUid);
        if (ret != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "error: ApplyDiffPatch failed errcode :%{public}d", ret);
            return ERR_BUNDLEMANAGER_QUICK_FIX_APPLY_DIFF_PATCH_FAILED;
        }
    }
#endif
    return ERR_OK;
}

ErrCode BaseBundleInstaller::SetDirApl(const InnerBundleInfo &info)
{
    auto& bundleUserInfos = info.GetInnerBundleUserInfos();
    CreateDirParam createDirParam;
    createDirParam.bundleName = info.GetBundleName();
    createDirParam.apl = info.GetAppPrivilegeLevel();
    createDirParam.isPreInstallApp = info.IsPreInstallApp();
    createDirParam.debug = info.GetBaseApplicationInfo().appProvisionType == Constants::APP_PROVISION_TYPE_DEBUG;
    for (const auto &userInfoPair : bundleUserInfos) {
        auto &userInfo = userInfoPair.second;
        const std::map<std::string, InnerBundleCloneInfo> &cloneInfos = userInfo.cloneInfos;
        auto userId = userInfo.bundleUserInfo.userId;
        createDirParam.userId = userId;
        createDirParam.uid = userInfo.uid;
        ErrCode userRet = SetDirApl(createDirParam, info.GetBundleName());
        if (userRet != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER,
                "fail to SetDirApl bundle dir, userId %{public}d, error is %{public}d", userId, userRet);
            return userRet;
        }
        for (const auto &cloneInfoPair : cloneInfos) {
            std::string cloneBundleName = BundleCloneCommonHelper::GetCloneDataDir(
                info.GetBundleName(), cloneInfoPair.second.appIndex);
            createDirParam.uid = cloneInfoPair.second.uid;
            ErrCode cloneRet = this->SetDirApl(createDirParam, cloneBundleName);
            if (cloneRet != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLER, "fail to SetDirApl clone bundle dir, error is %{public}d", cloneRet);
                return cloneRet;
            }
        }
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::SetDirApl(const CreateDirParam &createDirParam, const std::string &CloneBundleName)
{
    for (const auto &el : ServiceConstants::BUNDLE_EL) {
        std::string baseBundleDataDir = ServiceConstants::BUNDLE_APP_DATA_BASE_DIR +
                                        el +
                                        ServiceConstants::PATH_SEPARATOR +
                                        std::to_string(createDirParam.userId);
        std::string baseDataDir = baseBundleDataDir + ServiceConstants::BASE + CloneBundleName;
        std::string databaseDataDir = baseBundleDataDir + ServiceConstants::DATABASE + CloneBundleName;
        bool isBaseExist = true;
        bool isDatabaseExist = true;
        ErrCode result = InstalldClient::GetInstance()->IsExistDir(baseDataDir, isBaseExist);
        ErrCode dataResult = InstalldClient::GetInstance()->IsExistDir(databaseDataDir, isDatabaseExist);
        if (result != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "IsExistDir error is %{public}d", result);
            return result;
        }
        if (dataResult != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "IsExistDataDir error is %{public}d", dataResult);
            return dataResult;
        }
        if (!isBaseExist || !isDatabaseExist) {
            LOG_D(BMS_TAG_INSTALLER, "base %{public}s or data %{public}s is not exist", baseDataDir.c_str(),
                databaseDataDir.c_str());
            continue;
        }
        result = InstalldClient::GetInstance()->SetDirApl(
            baseDataDir, createDirParam.bundleName, createDirParam.apl, createDirParam.isPreInstallApp,
            createDirParam.debug, createDirParam.uid);
        if (result != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "fail to SetDirApl baseDir dir, error is %{public}d", result);
            return result;
        }
        result = InstalldClient::GetInstance()->SetDirApl(
            databaseDataDir, createDirParam.bundleName, createDirParam.apl, createDirParam.isPreInstallApp,
            createDirParam.debug, createDirParam.uid);
        if (result != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "fail to SetDirApl databaseDir dir, error is %{public}d", result);
            return result;
        }
        if (el == ServiceConstants::DIR_EL1 || el == ServiceConstants::DIR_EL2) {
            std::string backupDir = std::string(BMS_SERVICE_PATH) + ServiceConstants::PATH_SEPARATOR + el +
                ServiceConstants::PATH_SEPARATOR + std::to_string(createDirParam.userId) + BACKUP_BUNDLES +
                CloneBundleName;
            (void)InstalldClient::GetInstance()->SetDirApl(backupDir, createDirParam.bundleName, createDirParam.apl,
                createDirParam.isPreInstallApp, createDirParam.debug, createDirParam.uid);
        }
    }
    std::string sharefilesDir = std::string(ServiceConstants::BUNDLE_APP_DATA_BASE_DIR) +
        ServiceConstants::PATH_SEPARATOR + ServiceConstants::DIR_EL2 + ServiceConstants::PATH_SEPARATOR +
        std::to_string(createDirParam.userId) + ServiceConstants::SHAREFILES + CloneBundleName;
    (void)InstalldClient::GetInstance()->SetDirApl(sharefilesDir, createDirParam.bundleName, createDirParam.apl,
        createDirParam.isPreInstallApp, createDirParam.debug, createDirParam.uid);
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CreateBundleAndDataDir(InnerBundleInfo &info) const
{
    ErrCode result = CreateBundleCodeDir(info);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to create bundle code dir, error is %{public}d", result);
        return result;
    }
    ScopeGuard codePathGuard([&] { InstalldClient::GetInstance()->RemoveDir(info.GetAppCodePath()); });
    result = CreateBundleDataDir(info);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to create bundle data dir, error is %{public}d", result);
        return result;
    }
    codePathGuard.Dismiss();
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CreateBundleCodeDir(InnerBundleInfo &info) const
{
    auto appCodePath = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR + bundleName_;
    LOG_D(BMS_TAG_INSTALLER, "create bundle dir %{public}s", appCodePath.c_str());
    ErrCode result = InstalldClient::GetInstance()->CreateBundleDir(appCodePath);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to create bundle dir, error is %{public}d", result);
        return result;
    }

    info.SetAppCodePath(appCodePath);
    return ERR_OK;
}

static void SendToStorageQuota(const std::string &bundleName, const int uid,
    const std::string &bundleDataDirPath, const int limitSizeMb)
{
#ifdef STORAGE_SERVICE_ENABLE
    auto systemAbilityManager = SystemAbilityManagerClient::GetInstance().GetSystemAbilityManager();
    if (!systemAbilityManager) {
        LOG_W(BMS_TAG_INSTALLER, "SendToStorageQuota, systemAbilityManager error");
        return;
    }

    auto remote = systemAbilityManager->CheckSystemAbility(STORAGE_MANAGER_MANAGER_ID);
    if (!remote) {
        LOG_W(BMS_TAG_INSTALLER, "SendToStorageQuota, CheckSystemAbility error");
        return;
    }

    auto proxy = iface_cast<StorageManager::IStorageManager>(remote);
    if (!proxy) {
        LOG_W(BMS_TAG_INSTALLER, "SendToStorageQuotactl, proxy get error");
        return;
    }

    int err = proxy->SetBundleQuota(bundleName, uid, bundleDataDirPath, limitSizeMb);
    if (err != ERR_OK) {
        LOG_W(BMS_TAG_INSTALLER, "SendToStorageQuota, SetBundleQuota error, err=%{public}d, uid=%{public}d", err, uid);
    }
#endif // STORAGE_SERVICE_ENABLE
}

void BaseBundleInstaller::PrepareBundleDirQuota(const std::string &bundleName, const int32_t uid,
    const std::string &bundleDataDirPath, const int32_t limitSize) const
{
    if (limitSize == 0) {
        SendToStorageQuota(bundleName, uid, bundleDataDirPath, 0);
        return;
    }
    int32_t atomicserviceDatasizeThreshold = limitSize;
#ifdef STORAGE_SERVICE_ENABLE
#ifdef QUOTA_PARAM_SET_ENABLE
    char szAtomicDatasizeThresholdMb[THRESHOLD_VAL_LEN] = {0};
    int32_t ret = GetParameter(SYSTEM_PARAM_ATOMICSERVICE_DATASIZE_THRESHOLD, "",
        szAtomicDatasizeThresholdMb, THRESHOLD_VAL_LEN);
    if (ret <= 0) {
        LOG_I(BMS_TAG_INSTALLER, "GetParameter failed");
    } else if (strcmp(szAtomicDatasizeThresholdMb, "") != 0) {
        atomicserviceDatasizeThreshold = atoi(szAtomicDatasizeThresholdMb);
        LOG_I(BMS_TAG_INSTALLER, "InstalldQuotaUtils init atomicserviceDataThreshold mb success");
    }
    if (atomicserviceDatasizeThreshold <= 0) {
        LOG_W(BMS_TAG_INSTALLER, "no need to prepare quota");
        return;
    }
#endif // QUOTA_PARAM_SET_ENABLE
#endif // STORAGE_SERVICE_ENABLE
    ParseSizeFromProvision(bundleName, uid, atomicserviceDatasizeThreshold);
    SendToStorageQuota(bundleName, uid, bundleDataDirPath, atomicserviceDatasizeThreshold);
}

void BaseBundleInstaller::ParseSizeFromProvision(
    const std::string &bundleName, const int32_t uid, int32_t &sizeMb) const
{
    std::string appServiceCapabilities = verifyRes_.GetProvisionInfo().appServiceCapabilities;
    if (appServiceCapabilities.empty()) {
        return;
    }
    auto appServiceCapabilityMap = BundleUtil::ParseMapFromJson(appServiceCapabilities);
    for (auto &item : appServiceCapabilityMap) {
        if (item.first != ServiceConstants::PERMISSION_MANAGE_STORAGE) {
            continue;
        }
        if (BundlePermissionMgr::VerifyPermission(bundleName, ServiceConstants::PERMISSION_MANAGE_STORAGE,
            uid / Constants::BASE_USER_RANGE) != Security::AccessToken::PermissionState::PERMISSION_GRANTED) {
            APP_LOGW("no manage storage permission for %{public}s", bundleName.c_str());
            return;
        }
        std::unordered_map<std::string, std::string> storageMap = BundleUtil::ParseMapFromJson(item.second);
        auto it = storageMap.find(KEY_STORAGE_SIZE);
        if (it == storageMap.end()) {
            LOG_W(BMS_TAG_INSTALLER, "storageSize not found");
            return;
        }
        int32_t tempSize = atoi(it->second.c_str());
        if (tempSize >= sizeMb) {
            sizeMb = tempSize;
            LOG_I(BMS_TAG_INSTALLER, "set %{public}s quota to %{public}d", bundleName_.c_str(), sizeMb);
        } else {
            LOG_W(BMS_TAG_INSTALLER, "%{public}s storageSize %{public}d is not valid", bundleName_.c_str(), tempSize);
        }
        return;
    }
}

ErrCode BaseBundleInstaller::CreateBundleDataDir(InnerBundleInfo &info) const
{
    if (dataMgr_ == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "dataMgr_ is nullptr");
        return ERR_APPEXECFWK_NULL_PTR;
    }
    InnerBundleUserInfo newInnerBundleUserInfo;
    if (!info.GetInnerBundleUserInfo(userId_, newInnerBundleUserInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "bundle(%{public}s) get user(%{public}d) failed",
            info.GetBundleName().c_str(), userId_);
        return ERR_APPEXECFWK_USER_NOT_EXIST;
    }
    ErrCode ret = dataMgr_->GenerateUidAndGid(newInnerBundleUserInfo);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to generate uid and gid");
        return ret;
    }
    BundleUtil::MakeFsConfig(info.GetBundleName(), ServiceConstants::HMDFS_CONFIG_PATH, info.GetAppProvisionType(),
        Constants::APP_PROVISION_TYPE_FILE_NAME);
    CreateDirParam createDirParam;
    createDirParam.bundleName = info.GetBundleName();
    createDirParam.userId = userId_;
    createDirParam.uid = newInnerBundleUserInfo.uid;
    createDirParam.gid = newInnerBundleUserInfo.uid;
    createDirParam.apl = info.GetAppPrivilegeLevel();
    createDirParam.isPreInstallApp = info.IsPreInstallApp();
    createDirParam.debug = info.GetBaseApplicationInfo().appProvisionType == Constants::APP_PROVISION_TYPE_DEBUG;

    auto result = InstalldClient::GetInstance()->CreateBundleDataDir(createDirParam);
    if (result != ERR_OK) {
        // if user is not activated, access el2-el4 may return ok but dir cannot be created
        if (AccountHelper::IsOsAccountVerified(userId_)) {
            LOG_E(BMS_TAG_INSTALLER, "fail to create bundle data dir, error is %{public}d", result);
            return result;
        } else {
            LOG_W(BMS_TAG_INSTALLER, "user %{public}d is not activated", userId_);
        }
    }
    std::string bundleDataDir = ServiceConstants::BUNDLE_APP_DATA_BASE_DIR + ServiceConstants::BUNDLE_EL[1] +
        ServiceConstants::PATH_SEPARATOR + std::to_string(userId_) + ServiceConstants::BASE + info.GetBundleName();
    if (info.GetApplicationBundleType() == BundleType::ATOMIC_SERVICE) {
        PrepareBundleDirQuota(info.GetBundleName(), newInnerBundleUserInfo.uid, bundleDataDir,
            ATOMIC_SERVICE_DATASIZE_THRESHOLD_MB_PRESET);
    } else {
        PrepareBundleDirQuota(info.GetBundleName(), newInnerBundleUserInfo.uid, bundleDataDir, 0);
    }
    if (info.GetIsNewVersion()) {
        int32_t gid = (info.GetAppProvisionType() == Constants::APP_PROVISION_TYPE_DEBUG) ?
            GetIntParameter(BMS_KEY_SHELL_UID, ServiceConstants::SHELL_UID) :
            newInnerBundleUserInfo.uid;
        result = CreateArkProfile(
            info.GetBundleName(), userId_, newInnerBundleUserInfo.uid, gid);
        if (result != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "fail to create ark profile, error is %{public}d", result);
            return result;
        }
    }

    result = CreateShaderCache(info.GetBundleName(), createDirParam.uid, createDirParam.gid);
    if (result != ERR_OK) {
        LOG_W(BMS_TAG_INSTALLER, "fail to create shader cache, error is %{public}d", result);
    }

    if (userId_ == Constants::START_USERID) {
        CreateCloudShader(info.GetBundleName(), createDirParam.uid, createDirParam.gid);
    }

    // create asan log directory when asanEnabled is true
    // In update condition, delete asan log directory when asanEnabled is false if directory is exist
    if ((result = ProcessAsanDirectory(info)) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "process asan log directory failed");
        return result;
    }

    std::string dataBaseDir = ServiceConstants::BUNDLE_APP_DATA_BASE_DIR + ServiceConstants::BUNDLE_EL[1] +
        ServiceConstants::DATABASE + info.GetBundleName();
    info.SetAppDataBaseDir(dataBaseDir);
    info.AddInnerBundleUserInfo(newInnerBundleUserInfo);
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CreateDataGroupDirs(
    const std::vector<Security::Verify::HapVerifyResult> &hapVerifyResults, const InnerBundleInfo &oldInfo)
{
    if (dataMgr_ == nullptr) {
        LOG_W(BMS_TAG_INSTALLER, "dataMgr_ null");
        return ERR_APPEXECFWK_NULL_PTR;
    }
    if (hapVerifyResults.empty()) {
        LOG_W(BMS_TAG_INSTALLER, "hapVerifyResults empty");
        return ERR_APPEXECFWK_HAP_VERIFY_RES_EMPTY;
    }
    std::unordered_set<std::string> groupIds;
    GetDataGroupIds(hapVerifyResults, groupIds);
    dataMgr_->GenerateDataGroupInfos(bundleName_, groupIds, userId_, true);
    return ERR_OK;
}

std::vector<std::string> BaseBundleInstaller::GenerateScreenLockProtectionDir(const std::string &bundleName) const
{
    std::vector<std::string> dirs;
    if (bundleName.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "bundleName is empty");
        return dirs;
    }
    dirs.emplace_back(std::string(ServiceConstants::SCREEN_LOCK_FILE_DATA_PATH) + ServiceConstants::PATH_SEPARATOR +
        std::to_string(userId_) + ServiceConstants::BASE + bundleName);
    dirs.emplace_back(std::string(ServiceConstants::SCREEN_LOCK_FILE_DATA_PATH) + ServiceConstants::PATH_SEPARATOR +
        std::to_string(userId_) + ServiceConstants::DATABASE + bundleName);
    return dirs;
}

void BaseBundleInstaller::CreateScreenLockProtectionDir(bool withOtherUsers)
{
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "CreateScreenLockProtectionDir start");
    InnerBundleInfo info;
    if (!GetTempBundleInfo(info)) {
        LOG_E(BMS_TAG_INSTALLER, "get failed");
        return;
    }

    std::vector<std::string> dirs = GenerateScreenLockProtectionDir(bundleName_);
    std::vector<RequestPermission> reqPermissions = info.GetAllRequestPermissions();
    auto it = std::find_if(reqPermissions.begin(), reqPermissions.end(), [](const RequestPermission& permission) {
        return permission.name == ServiceConstants::PERMISSION_PROTECT_SCREEN_LOCK_DATA;
    });
    if (it != reqPermissions.end()) {
        CreateEl5AndSetPolicy(info, withOtherUsers);
    }
}

void BaseBundleInstaller::CreateEl5AndSetPolicy(InnerBundleInfo &info, bool withOtherUsers)
{
    if (!InitDataMgr()) {
        LOG_E(BMS_TAG_INSTALLER, "init failed");
        return;
    }
    std::vector<CreateDirParam> params;
    CreateDirParam el5Param;
    el5Param.bundleName = info.GetBundleName();
    el5Param.userId = userId_;
    el5Param.uid = info.GetUid(userId_);
    el5Param.apl = info.GetAppPrivilegeLevel();
    el5Param.isPreInstallApp = info.IsPreInstallApp();
    el5Param.debug = info.GetBaseApplicationInfo().appProvisionType == Constants::APP_PROVISION_TYPE_DEBUG;
    params.emplace_back(el5Param);
    if (withOtherUsers) {
        for (const auto &otherUserId : info.GetUsers()) {
            if (otherUserId == userId_) {
                continue;
            }
            el5Param.userId = otherUserId;
            el5Param.uid = info.GetUid(otherUserId);
            params.emplace_back(el5Param);
        }
    }
    InnerBundleUserInfo userInfo;
    if (!info.GetInnerBundleUserInfo(userId_, userInfo)) {
        APP_LOGE("get user info failed");
        return;
    }
    for (const auto &cloneInfo : userInfo.cloneInfos) {
        CreateDirParam cloneParam = el5Param;
        cloneParam.uid = cloneInfo.second.uid;
        cloneParam.gid = cloneInfo.second.uid;
        cloneParam.appIndex = cloneInfo.second.appIndex;
        params.emplace_back(cloneParam);
    }
    dataMgr_->CreateEl5DirNoCache(params, info);
    tempInfo_.SetTempBundleInfo(info);
}

void BaseBundleInstaller::GetUninstallBundleInfo(bool isKeepData, int32_t userId,
    const InnerBundleInfo &oldInfo, UninstallBundleInfo &uninstallBundleInfo)
{
    if (!isKeepData) {
        return;
    }
    uninstallBundleInfo.userInfos[std::to_string(userId)].uid = oldInfo.GetUid(userId);
    uninstallBundleInfo.userInfos[std::to_string(userId)].gids.emplace_back(oldInfo.GetGid(userId));
    uninstallBundleInfo.userInfos[std::to_string(userId)].accessTokenId = oldInfo.GetAccessTokenId(userId);
    uninstallBundleInfo.userInfos[std::to_string(userId)].accessTokenIdEx = oldInfo.GetAccessTokenIdEx(userId);
    uninstallBundleInfo.appId = oldInfo.GetAppId();
    uninstallBundleInfo.appIdentifier = oldInfo.GetAppIdentifier();
    uninstallBundleInfo.appProvisionType = oldInfo.GetAppProvisionType();
    uninstallBundleInfo.bundleType = oldInfo.GetApplicationBundleType();
    oldInfo.GetModuleNames(uninstallBundleInfo.moduleNames);
}

void BaseBundleInstaller::SaveUninstallBundleInfo(const std::string bundleName, bool isKeepData,
    const UninstallBundleInfo &uninstallBundleInfo)
{
    if (!isKeepData) {
        return;
    }
    if (!dataMgr_->UpdateUninstallBundleInfo(bundleName, uninstallBundleInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "update failed");
    }
}

void BaseBundleInstaller::DeleteUninstallBundleInfo(const std::string &bundleName)
{
    if (!InitDataMgr()) {
        LOG_E(BMS_TAG_INSTALLER, "init failed");
        return;
    }
    if (!existBeforeKeepDataApp_) {
        return;
    }
    if (!dataMgr_->DeleteUninstallBundleInfo(bundleName, userId_)) {
        LOG_E(BMS_TAG_INSTALLER, "delete failed");
    }
    BundleResourceHelper::DeleteUninstallBundleResource(bundleName, userId_, 0);
}

bool BaseBundleInstaller::DeleteUninstallBundleInfoFromDb(const std::string &bundleName)
{
    if (!InitDataMgr()) {
        LOG_E(BMS_TAG_INSTALLER, "init failed");
        return false;
    }
    UninstallBundleInfo uninstallBundleInfo;
    if (!dataMgr_->GetUninstallBundleInfo(bundleName, uninstallBundleInfo)) {
        return false;
    }
    auto it = uninstallBundleInfo.userInfos.find(std::to_string(userId_));
    if (it == uninstallBundleInfo.userInfos.end()) {
        LOG_I(BMS_TAG_INSTALLER, "%{public}s has been uninstalled on user %{public}d without keepData",
            bundleName.c_str(), userId_);
        return false;
    }
    ErrCode result = InstalldClient::GetInstance()->RemoveBundleDataDir(bundleName, userId_,
        uninstallBundleInfo.bundleType == BundleType::ATOMIC_SERVICE, true);
    LOG_I(BMS_TAG_INSTALLER, "remove dirs res %{public}d", result);
    if (!uninstallBundleInfo.extensionDirs.empty()) {
        result = InstalldClient::GetInstance()->RemoveExtensionDir(userId_, uninstallBundleInfo.extensionDirs);
        LOG_I(BMS_TAG_INSTALLER, "remove extension dirs res %{public}d", result);
    }
    DeleteEncryptionKeyId(bundleName, true, false);
    BundleResourceHelper::DeleteUninstallBundleResource(bundleName, userId_, 0);
    bool ret = dataMgr_->DeleteUninstallBundleInfo(bundleName, userId_);
    if (!ret) {
        LOG_E(BMS_TAG_INSTALLER, "failed %{public}s %{public}d", bundleName.c_str(), userId_);
        return false;
    }
    std::map<std::string, std::string> tokenIdMetadataInfos;
    tokenIdMetadataInfos[ServiceConstants::META_KEEP_TOKEN_ID_KEY] = isKeepTokenId_ ? BMS_TRUE : BMS_FALSE;
    NotifyBundleEvents installRes = {
        .resultCode = ERR_OK,
        .accessTokenId = it->second.accessTokenId,
        .uid = it->second.uid,
        .bundleType = static_cast<int32_t>(uninstallBundleInfo.bundleType),
        .appIndex = 0,
        .bundleName = bundleName,
        .appId = uninstallBundleInfo.appId,
        .appIdentifier = uninstallBundleInfo.appIdentifier,
        .metadataConfigInfos = tokenIdMetadataInfos,
        .keepData = false,
    };
    std::shared_ptr<BundleCommonEventMgr> commonEventMgr = std::make_shared<BundleCommonEventMgr>();
    commonEventMgr->NotifyUninstalledBundleCleared(installRes);
    return true;
}

void BaseBundleInstaller::SetFirstInstallTime(const std::string &bundleName, const int64_t &time,
    InnerBundleInfo &info)
{
    if (!InitDataMgr()) {
        LOG_E(BMS_TAG_INSTALLER, "init data manager failed");
        info.SetFirstInstallTime(time, userId_);
        return;
    }
    FirstInstallBundleInfo firstInstallBundleInfo;
    if (dataMgr_->GetFirstInstallBundleInfo(bundleName, userId_, firstInstallBundleInfo)) {
        info.SetFirstInstallTime(firstInstallBundleInfo.firstInstallTime, userId_);
        return;
    }
    info.SetFirstInstallTime(time, userId_);
}

bool BaseBundleInstaller::SaveFirstInstallBundleInfo(const std::string &bundleName, const int32_t userId,
    bool isPreInstallApp, const InnerBundleUserInfo &innerBundleUserInfo)
{
    if (!InitDataMgr()) {
        LOG_E(BMS_TAG_INSTALLER, "init data manager failed");
        return false;
    }
    FirstInstallBundleInfo firstInstallInfo;
    if (innerBundleUserInfo.firstInstallTime == ServiceConstants::DEFAULT_FIRST_INSTALL_TIME) {
        firstInstallInfo.firstInstallTime = isPreInstallApp ?
            ServiceConstants::PREINSTALL_FIRST_INSTALL_TIME : innerBundleUserInfo.installTime;
    } else {
        firstInstallInfo.firstInstallTime = innerBundleUserInfo.firstInstallTime;
    }
    if (!dataMgr_->AddFirstInstallBundleInfo(bundleName, userId, firstInstallInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "failed to add first install info for bundle %{public}s", bundleName.c_str());
        return false;
    }
    // Save odidResetCount and lastOdid from InnerBundleInfo
    InnerBundleInfo innerBundleInfo;
    FirstInstallBundleInfo odidCount;
    if (GetTempBundleInfo(innerBundleInfo)) {
        std::string currentOdid;
        innerBundleInfo.GetOdid(currentOdid);
        odidCount.lastOdid = currentOdid;
        LOG_I(BMS_TAG_INSTALLER, "save odidResetCount for %{public}s", bundleName.c_str());
    }
    if (!dataMgr_->AddFirstInstallBundleInfo(bundleName, Constants::ALL_USERID, odidCount)) {
        LOG_W(BMS_TAG_INSTALLER, "failed to add odidCount for bundle %{public}s", bundleName.c_str());
    }
    return true;
}

bool BaseBundleInstaller::CheckInstallOnKeepData(const std::string &bundleName, bool isOTA,
    const std::unordered_map<std::string, InnerBundleInfo> &infos)
{
    if (!InitDataMgr() || infos.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "init failed or empty infos");
        return true;
    }
    UninstallBundleInfo uninstallBundleInfo;
    if (!dataMgr_->GetUninstallBundleInfo(bundleName, uninstallBundleInfo)) {
        return true;
    }
    existBeforeKeepDataApp_ = true;
    if (isOTA) {
        return true;
    }
    LOG_I(BMS_TAG_INSTALLER, "this app was uninstalled with keep data before");
    if (!CheckAppIdentifier(uninstallBundleInfo.appIdentifier, infos.begin()->second.GetAppIdentifier(),
        uninstallBundleInfo.appId, infos.begin()->second.GetAppId())) {
        LOG_E(BMS_TAG_INSTALLER,
            "%{public}s has been uninstalled with keep data, and the appIdentifier or appId is not the same",
            bundleName.c_str());
        return false;
    }
    return true;
}

void BaseBundleInstaller::DeleteEncryptionKeyId(const std::string &bundleName, bool existEl5Dir,
    bool isKeepData) const
{
    if (bundleName.empty()) {
        LOG_W(BMS_TAG_INSTALLER, "bundleName is empty");
        return;
    }
    if (isKeepData) {
        LOG_I(BMS_TAG_INSTALLER, "keep el5 dir -n %{public}s", bundleName.c_str());
        return;
    }
    LOG_D(BMS_TAG_INSTALLER, "delete el5 dir -n %{public}s", bundleName.c_str());
    std::vector<std::string> dirs = GenerateScreenLockProtectionDir(bundleName);
    for (const std::string &dir : dirs) {
        if (InstalldClient::GetInstance()->RemoveDir(dir) != ERR_OK) {
            LOG_W(BMS_TAG_INSTALLER, "remove Screen Lock Protection dir %{public}s failed", dir.c_str());
        }
    }

    if (!existEl5Dir) {
        return;
    }
    EncryptionParam encryptionParam(bundleName, "", 0, userId_, EncryptionDirType::APP);
    if (InstalldClient::GetInstance()->DeleteEncryptionKeyId(encryptionParam) != ERR_OK) {
        LOG_D(BMS_TAG_INSTALLER, "delete encryption key id failed");
    }
}

void BaseBundleInstaller::DeleteScreenLockProtectionDir(const std::string bundleName) const
{
    std::vector<std::string> dirs = GenerateScreenLockProtectionDir(bundleName);
    for (const std::string &dir : dirs) {
        auto result = InstalldClient::GetInstance()->RemoveDir(dir);
        if (result != ERR_OK) {
            LOG_W(BMS_TAG_INSTALLER, "remove Screen Lock Protection dir %{public}s failed", dir.c_str());
        }
    }
}

void BaseBundleInstaller::DeleteGroupDirsForException(const InnerBundleInfo &oldInfo) const
{
    if (dataMgr_ == nullptr) {
        LOG_W(BMS_TAG_INSTALLER, "dataMgr_ null");
        return;
    }
    dataMgr_->DeleteGroupDirsForException(oldInfo, userId_);
}

ErrCode BaseBundleInstaller::RemoveDataGroupDirs(const std::string &bundleName, int32_t userId, bool isKeepData) const
{
    if (dataMgr_ == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "dataMgr_ is nullptr");
        return ERR_APPEXECFWK_NULL_PTR;
    }
    dataMgr_->DeleteUserDataGroupInfos(bundleName, userId, isKeepData);
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CreateArkProfile(
    const std::string &bundleName, int32_t userId, int32_t uid, int32_t gid) const
{
    ErrCode result = DeleteArkProfile(bundleName, userId);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to removeArkProfile, error is %{public}d", result);
        return result;
    }

    std::string arkProfilePath = AOTHandler::BuildArkProfilePath(userId, bundleName);
    LOG_D(BMS_TAG_INSTALLER, "CreateArkProfile %{public}s", arkProfilePath.c_str());
    int32_t mode = (uid == gid) ? S_IRWXU : (S_IRWXU | S_IRGRP | S_IXGRP);
    return InstalldClient::GetInstance()->Mkdir(arkProfilePath, mode, uid, gid);
}

ErrCode BaseBundleInstaller::DeleteArkProfile(const std::string &bundleName, int32_t userId) const
{
    std::string arkProfilePath = AOTHandler::BuildArkProfilePath(userId, bundleName);
    LOG_D(BMS_TAG_INSTALLER, "DeleteArkProfile %{public}s", arkProfilePath.c_str());
    return InstalldClient::GetInstance()->RemoveDir(arkProfilePath);
}

bool BaseBundleInstaller::RemoveDataPreloadHapFiles(const std::string &bundleName) const
{
    if (dataMgr_ == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "null dataMgr_");
        return false;
    }

    PreInstallBundleInfo preInstallBundleInfo;
    preInstallBundleInfo.SetBundleName(bundleName);
    if (!dataMgr_->GetPreInstallBundleInfo(bundleName, preInstallBundleInfo)) {
        LOG_I(BMS_TAG_INSTALLER, "no PreInstallBundleInfo(%{public}s) in db", bundleName.c_str());
        return true;
    }
    if (preInstallBundleInfo.GetBundlePaths().empty()) {
        LOG_W(BMS_TAG_INSTALLER, "path is empty , bundleName: %{public}s", bundleName.c_str());
        return false;
    }

    auto preinstalledAppPath = preInstallBundleInfo.GetBundlePaths().front();
    LOG_D(BMS_TAG_INSTALLER, "preinstalledAppPath %{public}s", preinstalledAppPath.c_str());
    if (IsDataPreloadHap(preinstalledAppPath)) {
        std::filesystem::path apFilePath(preinstalledAppPath);
        std::string delDir = apFilePath.parent_path().string();
        if (InstalldClient::GetInstance()->RemoveDir(delDir) != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "removeDir failed :%{public}s", delDir.c_str());
        }
        if (!dataMgr_->DeletePreInstallBundleInfo(bundleName, preInstallBundleInfo)) {
            LOG_E(BMS_TAG_INSTALLER, "deletePreInfoInDb bundle %{public}s failed", bundleName.c_str());
        }
    }
    return true;
}

bool BaseBundleInstaller::IsDataPreloadHap(const std::string &path) const
{
    return path.find(DATA_PRELOAD_APP) == 0;
}

ErrCode BaseBundleInstaller::ExtractModule(InnerBundleInfo &info, const std::string &modulePath)
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    // need remove modulePath, make sure the directory is empty
    if (InstalldClient::GetInstance()->RemoveDir(modulePath) != ERR_OK) {
        APP_LOGW("remove dir %{public}s failed", modulePath.c_str());
    }
    auto result = InnerProcessNativeLibs(info, modulePath);
    CHECK_RESULT(result, "fail to InnerProcessNativeLibs, error is %{public}d");

    result = ExtractArkNativeFile(info, modulePath);
    CHECK_RESULT(result, "fail to extractArkNativeFile, error is %{public}d");
    if (info.GetIsNewVersion()) {
        result = CopyPgoFileToArkProfileDir(modulePackage_, modulePath_, info.GetBundleName(), userId_);
        if (result != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "fail to CopyPgoFileToArkProfileDir, error is %{public}d", result);
            return result;
        }
    }

    ExtractResourceFiles(info, modulePath);

    result = ExtractResFileDir(modulePath);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to ExtractResFileDir, error is %{public}d", result);
        return result;
    }

    if (auto hnpPackageInfos = info.GetInnerModuleInfoHnpInfo(info.GetCurModuleName())) {
        std::map<std::string, std::string> hnpPackageInfoMap;
        std::stringstream hnpPackageInfoString;
        for (const auto &hnpPackageInfo : *hnpPackageInfos) {
            if (hnpPackageInfo.type != TYPE_PUBLIC && hnpPackageInfo.type != TYPE_PRIVATE) {
                LOG_E(BMS_TAG_INSTALLER, "hnp type err: %{public}s", hnpPackageInfo.type.c_str());
                continue;
            }
            if (hnpPackageInfo.package.find(ServiceConstants::RELATIVE_PATH_NAME) != std::string::npos) {
                LOG_E(BMS_TAG_INSTALLER, "hnp package err: %{public}s", hnpPackageInfo.package.c_str());
                continue;
            }
            hnpPackageInfoMap[hnpPackageInfo.package] = hnpPackageInfo.type;
        }
        std::string cpuAbi = info.GetCpuAbi();
        result = ExtractHnpFileDir(cpuAbi, hnpPackageInfoMap, modulePath);
        if (result != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "fail to ExtractHnpsFileDir, error is %{public}d", result);
            return result;
        }
    }

    if (info.IsPreInstallApp()) {
        info.SetModuleHapPath(modulePath_);
    } else {
        info.SetModuleHapPath(GetHapPath(info));
    }

    auto moduleDir = info.GetAppCodePath() + ServiceConstants::PATH_SEPARATOR + info.GetCurrentModulePackage();
    info.AddModuleSrcDir(moduleDir);
    info.AddModuleResPath(moduleDir);
    info.AddModuleHnpsPath(modulePath);
    return ERR_OK;
}

void BaseBundleInstaller::ExtractResourceFiles(const InnerBundleInfo &info, const std::string &targetPath) const
{
    LOG_D(BMS_TAG_INSTALLER, "ExtractResourceFiles begin");
    int32_t apiTargetVersion = info.GetBaseApplicationInfo().apiTargetVersion;
    if (info.IsPreInstallApp() || apiTargetVersion > ServiceConstants::API_VERSION_NINE) {
        LOG_D(BMS_TAG_INSTALLER, "no need to extract resource files");
        return;
    }
    LOG_D(BMS_TAG_INSTALLER, "apiTargetVersion is %{public}d, extract resource files", apiTargetVersion);
    ExtractParam extractParam;
    extractParam.srcPath = modulePath_;
    extractParam.targetPath = targetPath + ServiceConstants::PATH_SEPARATOR;
    extractParam.extractFileType = ExtractFileType::RESOURCE;
    ErrCode ret = InstalldClient::GetInstance()->ExtractFiles(extractParam);
    LOG_D(BMS_TAG_INSTALLER, "ExtractResourceFiles ret : %{public}d", ret);
}

ErrCode BaseBundleInstaller::ExtractResFileDir(const std::string &modulePath) const
{
    LOG_D(BMS_TAG_INSTALLER, "ExtractResFileDir begin");
    ExtractParam extractParam;
    extractParam.srcPath = modulePath_;
    extractParam.targetPath = modulePath + ServiceConstants::PATH_SEPARATOR + ServiceConstants::RES_FILE_PATH;
    LOG_D(BMS_TAG_INSTALLER, "ExtractResFileDir targetPath: %{public}s", extractParam.targetPath.c_str());
    extractParam.extractFileType = ExtractFileType::RES_FILE;
    ErrCode ret = InstalldClient::GetInstance()->ExtractFiles(extractParam);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "ExtractResFileDir ExtractFiles failed, error is %{public}d", ret);
        return ret;
    }
    LOG_D(BMS_TAG_INSTALLER, "ExtractResFileDir end");
    return ret;
}

ErrCode BaseBundleInstaller::ExtractHnpFileDir(const std::string &cpuAbi,
    const std::map<std::string, std::string> &hnpPackageMap, const std::string &modulePath) const
{
    LOG_D(BMS_TAG_INSTALLER, "ExtractHnpFileDir begin");
    ExtractParam extractParam;
    extractParam.srcPath = modulePath_;
    extractParam.targetPath = modulePath + ServiceConstants::PATH_SEPARATOR + ServiceConstants::HNPS_FILE_PATH;
    if (ServiceConstants::ABI_MAP.find(cpuAbi) == ServiceConstants::ABI_MAP.end()) {
        LOG_E(BMS_TAG_INSTALLER, "No support %{public}s abi", cpuAbi.c_str());
        return ERR_APPEXECFWK_NATIVE_HNP_EXTRACT_FAILED;
    }
    extractParam.cpuAbi = cpuAbi;
    LOG_D(BMS_TAG_INSTALLER, "ExtractHnpFileDir targetPath: %{public}s", extractParam.targetPath.c_str());
    extractParam.extractFileType = ExtractFileType::HNPS_FILE;
    ErrCode ret = InstalldClient::GetInstance()->ExtractHnpFiles(hnpPackageMap, extractParam);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "ExtractHnpFileDir ExtractFiles failed, error is %{public}d", ret);
        return ret;
    }
    LOG_D(BMS_TAG_INSTALLER, "ExtractHnpFileDir end");
    return ret;
}

ErrCode BaseBundleInstaller::ExtractArkNativeFile(InnerBundleInfo &info, const std::string &modulePath)
{
    if (!info.GetArkNativeFilePath().empty()) {
        LOG_D(BMS_TAG_INSTALLER, "Module %{public}s no need to extract an", modulePackage_.c_str());
        return ERR_OK;
    }

    std::string cpuAbi = info.GetArkNativeFileAbi();
    if (cpuAbi.empty()) {
        LOG_D(BMS_TAG_INSTALLER, "Module %{public}s no native file", modulePackage_.c_str());
        return ERR_OK;
    }

    if (ServiceConstants::ABI_MAP.find(cpuAbi) == ServiceConstants::ABI_MAP.end()) {
        LOG_E(BMS_TAG_INSTALLER, "No support %{public}s abi", cpuAbi.c_str());
        return ERR_APPEXECFWK_PARSE_AN_FAILED;
    }

    std::string arkNativeFilePath;
    arkNativeFilePath.append(ServiceConstants::ABI_MAP.at(cpuAbi)).append(ServiceConstants::PATH_SEPARATOR);
    std::string targetPath;
    targetPath.append(ServiceConstants::HAP_ARK_CACHE_PATH).append(info.GetBundleName())
        .append(ServiceConstants::PATH_SEPARATOR).append(arkNativeFilePath);
    LOG_D(BMS_TAG_INSTALLER, "Begin extract an modulePath: %{public}s targetPath: %{public}s cpuAbi: %{public}s",
        modulePath.c_str(), targetPath.c_str(), cpuAbi.c_str());
    ExtractParam extractParam;
    extractParam.srcPath = modulePath_;
    extractParam.targetPath = targetPath;
    extractParam.cpuAbi = cpuAbi;
    extractParam.extractFileType = ExtractFileType::AN;
    auto result = InstalldClient::GetInstance()->ExtractFiles(extractParam);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "extract files failed, error is %{public}d", result);
        return result;
    }

    info.SetArkNativeFilePath(arkNativeFilePath);
    return ERR_OK;
}

ErrCode BaseBundleInstaller::ExtractAllArkProfileFile(const InnerBundleInfo &oldInfo, bool checkRepeat)
{
    if (!oldInfo.GetIsNewVersion()) {
        return ERR_OK;
    }
    std::string bundleName = oldInfo.GetBundleName();
    LOG_I(BMS_TAG_INSTALLER, "Begin to ExtractAllArkProfileFile, bundleName : %{public}s", bundleName.c_str());
    const auto &innerModuleInfos = oldInfo.GetInnerModuleInfos();
    for (auto iter = innerModuleInfos.cbegin(); iter != innerModuleInfos.cend(); ++iter) {
        if (checkRepeat && installedModules_.find(iter->first) != installedModules_.end()) {
            continue;
        }

        ErrCode ret = CopyPgoFileToArkProfileDir(iter->second.name, iter->second.hapPath, bundleName, userId_);
        if (ret != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "fail to CopyPgoFileToArkProfileDir, error is %{public}d", ret);
            return ret;
        }
    }
    LOG_D(BMS_TAG_INSTALLER, "ExtractAllArkProfileFile succeed, bundleName : %{public}s", bundleName.c_str());
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CopyPgoFileToArkProfileDir(
    const std::string &moduleName,
    const std::string &modulePath,
    const std::string &bundleName,
    int32_t userId)
{
    ErrCode result = ERR_OK;
    auto it = pgoParams_.find(moduleName);
    if (it != pgoParams_.end()) {
        result = CopyPgoFile(moduleName, it->second, bundleName, userId);
    } else {
        result = ExtractArkProfileFile(modulePath, bundleName, userId);
    }
    if (result != ERR_OK) {
        return result;
    }
    std::set<int32_t> userIds;
    if (!InitDataMgr() || !dataMgr_->GetInnerBundleInfoUsers(bundleName, userIds)) {
        LOG_W(BMS_TAG_INSTALLER, "get users failed");
        return ERR_OK;
    }
    for (const auto otherUserId : userIds) {
        if (otherUserId == userId) {
            continue;
        }
        if (it != pgoParams_.end()) {
            (void)CopyPgoFile(moduleName, it->second, bundleName, otherUserId);
        } else {
            (void)ExtractArkProfileFile(modulePath, bundleName, otherUserId);
        }
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CopyPgoFile(
    const std::string &moduleName,
    const std::string &pgoPath,
    const std::string &bundleName,
    int32_t userId) const
{
    std::string targetPath =
        AOTHandler::BuildArkProfilePath(userId, bundleName, moduleName + ServiceConstants::AP_SUFFIX);
    if (InstalldClient::GetInstance()->CopyFile(pgoPath, targetPath) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "copy file from %{public}s to %{public}s failed", pgoPath.c_str(), targetPath.c_str());
        return ERR_APPEXECFWK_INSTALL_COPY_HAP_FAILED;
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::ExtractArkProfileFile(
    const std::string &modulePath,
    const std::string &bundleName,
    int32_t userId) const
{
    std::string targetPath = AOTHandler::BuildArkProfilePath(userId, bundleName);
    LOG_D(BMS_TAG_INSTALLER, "Begin to extract ap file, modulePath : %{public}s, targetPath : %{public}s",
        modulePath.c_str(), targetPath.c_str());
    ExtractParam extractParam;
    extractParam.srcPath = modulePath;
    extractParam.targetPath = targetPath;
    extractParam.cpuAbi = Constants::EMPTY_STRING;
    extractParam.extractFileType = ExtractFileType::AP;
    auto result = InstalldClient::GetInstance()->ExtractFiles(extractParam);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "extract ap files failed, error is %{public}d", result);
        return result;
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::DeleteOldArkNativeFile(const InnerBundleInfo &oldInfo)
{
    std::string targetPath;
    targetPath.append(ServiceConstants::HAP_ARK_CACHE_PATH).append(oldInfo.GetBundleName());
    auto result = InstalldClient::GetInstance()->RemoveDir(targetPath);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to remove arkNativeFilePath %{public}s, error is %{public}d",
            targetPath.c_str(), result);
    }

    return result;
}

ErrCode BaseBundleInstaller::RemoveBundleAndDataDir(const InnerBundleInfo &info, bool isKeepData, const bool async)
{
    ErrCode result = ERR_OK;
    if (!isKeepData) {
        result = RemoveBundleDataDir(info, false, async);
        if (result != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "fail to remove bundleData dir %{public}s, error is %{public}d",
                info.GetBundleName().c_str(), result);
            return result;
        }
    }
    // remove bundle dir
    result = RemoveBundleCodeDir(info, async);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "remove dir fail %{public}s error %{public}d", info.GetAppCodePath().c_str(), result);
        return result;
    }
    return result;
}

ErrCode BaseBundleInstaller::RemoveBundleCodeDir(const InnerBundleInfo &info, const bool async) const
{
    auto result = InstalldClient::GetInstance()->RemoveDir(info.GetAppCodePath(), async);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to remove bundle code dir %{public}s, error is %{public}d",
            info.GetAppCodePath().c_str(), result);
    }
    return result;
}

ErrCode BaseBundleInstaller::RemoveBundleDataDir(
    const InnerBundleInfo &info, bool forException, const bool async)
{
    sysEventInfo_.startTime = BundleUtil::GetCurrentTimeMs();
    ErrCode result =
        InstalldClient::GetInstance()->RemoveBundleDataDir(info.GetBundleName(), userId_,
            info.GetApplicationBundleType() == BundleType::ATOMIC_SERVICE, async);
    InstallParam installParam;
    if (result == ERR_APPEXECFWK_INSTALLD_REMOVE_DIR_FAILED) {
        LOG_W(BMS_TAG_INSTALLER, "RemoveBundleDataDir failed %{public}d", result);
        SendBundleSystemEvent(
            info.GetBundleName(),
            BundleEventType::UNINSTALL,
            installParam,
            sysEventInfo_.preBundleScene,
            ERR_APPEXECFWK_INSTALLD_REMOVE_DIR_FAILED);
    }

    if (forException) {
        result = InstalldClient::GetInstance()->RemoveExtensionDir(userId_, createExtensionDirs_);
    } else {
        auto extensionDirs = info.GetAllExtensionDirs();
        result = InstalldClient::GetInstance()->RemoveExtensionDir(userId_, extensionDirs);
    }
    if (result == ERR_APPEXECFWK_INSTALLD_REMOVE_DIR_FAILED) {
        LOG_E(BMS_TAG_INSTALLER, "fail to remove bundle extension dir, error is %{public}d", result);
        SendBundleSystemEvent(
            info.GetBundleName(),
            BundleEventType::UNINSTALL,
            installParam,
            sysEventInfo_.preBundleScene,
            result);
    }
    return ERR_OK;
}

void BaseBundleInstaller::RemoveEmptyDirs(const std::unordered_map<std::string, InnerBundleInfo> &infos,
    const bool isBundleUpdate) const
{
    for (const auto &item : infos) {
        const InnerBundleInfo &info = item.second;
        std::string moduleDir = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR;
        if (isBundleUpdate) {
            moduleDir += ServiceConstants::BUNDLE_NEW_CODE_DIR;
        }
        moduleDir += info.GetBundleName() + ServiceConstants::PATH_SEPARATOR + info.GetCurrentModulePackage();
        bool isDirEmpty = false;
        InstalldClient::GetInstance()->IsDirEmpty(moduleDir, isDirEmpty);
        if (isDirEmpty) {
            LOG_D(BMS_TAG_INSTALLER, "remove empty dir : %{public}s", moduleDir.c_str());
            InstalldClient::GetInstance()->RemoveDir(moduleDir);
        }
    }
}

std::string BaseBundleInstaller::GetModuleNames(const std::unordered_map<std::string, InnerBundleInfo> &infos) const
{
    if (infos.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "module info is empty");
        return Constants::EMPTY_STRING;
    }
    std::string moduleNames;
    for (const auto &item : infos) {
        moduleNames.append(item.second.GetCurrentModulePackage()).append(ServiceConstants::MODULE_NAME_SEPARATOR);
    }
    moduleNames.pop_back();
    LOG_D(BMS_TAG_INSTALLER, "moduleNames : %{public}s", moduleNames.c_str());
    return moduleNames;
}

ErrCode BaseBundleInstaller::RemoveModuleAndDataDir(
    const InnerBundleInfo &info, const std::string &modulePackage, int32_t userId, bool isKeepData) const
{
    LOG_D(BMS_TAG_INSTALLER, "RemoveModuleAndDataDir with package name %{public}s", modulePackage.c_str());
    auto moduleDir = info.GetModuleDir(modulePackage);
    auto result = RemoveModuleDir(moduleDir);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to remove module dir, error is %{public}d", result);
        return result;
    }

    // remove hap
    result = RemoveModuleDir(GetHapPath(info, info.GetModuleName(modulePackage)));
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to remove module hap, error is %{public}d", result);
        return result;
    }
    LOG_D(BMS_TAG_INSTALLER, "RemoveModuleAndDataDir successfully");
    return ERR_OK;
}

ErrCode BaseBundleInstaller::RemoveModuleDir(const std::string &modulePath) const
{
    LOG_D(BMS_TAG_INSTALLER, "module dir %{public}s to be removed", modulePath.c_str());
    return InstalldClient::GetInstance()->RemoveDir(modulePath);
}

ErrCode BaseBundleInstaller::ExtractModuleFiles(const InnerBundleInfo &info, const std::string &modulePath,
    const std::string &targetSoPath, const std::string &cpuAbi)
{
    LOG_D(BMS_TAG_INSTALLER, "extract module to %{public}s", modulePath.c_str());
    auto result = InstalldClient::GetInstance()->ExtractModuleFiles(modulePath_, modulePath, targetSoPath, cpuAbi);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "extract module files failed, error is %{public}d", result);
        return result;
    }

    return ERR_OK;
}

ErrCode BaseBundleInstaller::RenameModuleDir(const InnerBundleInfo &info) const
{
    auto moduleDir = info.GetAppCodePath() + ServiceConstants::PATH_SEPARATOR + info.GetCurrentModulePackage();
    LOG_D(BMS_TAG_INSTALLER, "rename module to %{public}s", moduleDir.c_str());
    auto result = InstalldClient::GetInstance()->RenameModuleDir(moduleDir + ServiceConstants::TMP_SUFFIX, moduleDir);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "rename module dir failed, error is %{public}d", result);
        return result;
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CheckSysCap(const std::vector<std::string> &bundlePaths)
{
    return bundleInstallChecker_->CheckSysCap(bundlePaths);
}

ErrCode BaseBundleInstaller::CheckMultipleHapsSignInfo(
    const std::vector<std::string> &bundlePaths,
    const InstallParam &installParam,
    std::vector<Security::Verify::HapVerifyResult>& hapVerifyRes)
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    return bundleInstallChecker_->CheckMultipleHapsSignInfo(bundlePaths, hapVerifyRes, true, userId_);
}

ErrCode BaseBundleInstaller::CheckShellInstall(std::vector<Security::Verify::HapVerifyResult> &hapVerifyRes)
{
#ifdef X86_EMULATOR_MODE
    return CheckShellInstallForEmulator(hapVerifyRes);
#else
    if (!IsShellOrDevAssistant() || hapVerifyRes.empty()) {
        return ERR_OK;
    }
    Security::Verify::ProvisionInfo provisionInfo = hapVerifyRes.begin()->GetProvisionInfo();
    if (provisionInfo.distributionType == Security::Verify::AppDistType::APP_GALLERY &&
        provisionInfo.type == Security::Verify::ProvisionType::RELEASE && !IsRdDevice()) {
        return ERR_APPEXECFWK_INSTALL_RELEASE_BUNDLE_NOT_ALLOWED_FOR_SHELL;
    }
    return ERR_OK;
#endif
}

#ifdef X86_EMULATOR_MODE
ErrCode BaseBundleInstaller::CheckShellInstallForEmulator(std::vector<Security::Verify::HapVerifyResult> &hapVerifyRes)
{
    if (hapVerifyRes.empty()) {
        return ERR_OK;
    }
    Security::Verify::ProvisionInfo provisionInfo = hapVerifyRes.begin()->GetProvisionInfo();
    if (provisionInfo.distributionType != Security::Verify::AppDistType::APP_GALLERY ||
        provisionInfo.type != Security::Verify::ProvisionType::RELEASE) {
        return ERR_OK;
    }
#ifdef BUILD_VARIANT_USER
    if (sysEventInfo_.callingUid == ServiceConstants::SHELL_UID ||
        sysEventInfo_.callingUid == Constants::ROOT_UID) {
        return ERR_APPEXECFWK_INSTALL_RELEASE_BUNDLE_NOT_ALLOWED_FOR_SHELL;
    }
#else
    if (sysEventInfo_.callingUid == ServiceConstants::SHELL_UID && !IsRdDevice()) {
        return ERR_APPEXECFWK_INSTALL_RELEASE_BUNDLE_NOT_ALLOWED_FOR_SHELL;
    }
#endif
    return ERR_OK;
}
#endif

ErrCode BaseBundleInstaller::ParseHapFiles(
    const std::vector<std::string> &bundlePaths,
    const InstallParam &installParam,
    const Constants::AppType appType,
    std::vector<Security::Verify::HapVerifyResult> &hapVerifyRes,
    std::unordered_map<std::string, InnerBundleInfo> &infos)
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    InstallCheckParam checkParam;
    checkParam.isPreInstallApp = installParam.isPreInstallApp;
    checkParam.crowdtestDeadline = installParam.crowdtestDeadline;
    checkParam.specifiedDistributionType = installParam.specifiedDistributionType;
    checkParam.appType = appType;
    checkParam.removable = installParam.removable;
    checkParam.isInstalledForAllUser = installParam.IsEnterpriseForAllUser();
    ErrCode ret = bundleInstallChecker_->ParseHapFiles(
        bundlePaths, checkParam, hapVerifyRes, infos);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "parse hap file failed due to errorCode : %{public}d", ret);
        return ret;
    }
    if (!infos.empty()) {
        bundleType_ = infos.begin()->second.GetApplicationBundleType();
    }
    UpdateDeveloperId(infos, hapVerifyRes);
    isContainEntry_ = bundleInstallChecker_->IsContainEntry();
    /* At this place, hapVerifyRes cannot be empty and unnecessary to check it */
    isEnterpriseBundle_ = bundleInstallChecker_->CheckEnterpriseBundle(hapVerifyRes[0]);
    isInternaltestingBundle_ = bundleInstallChecker_->CheckInternaltestingBundle(hapVerifyRes[0]);
    appIdentifier_ = (hapVerifyRes[0].GetProvisionInfo().type == Security::Verify::ProvisionType::DEBUG) ?
        DEBUG_APP_IDENTIFIER : hapVerifyRes[0].GetProvisionInfo().bundleInfo.appIdentifier;
    SetAppDistributionType(infos);
    UpdateExtensionSandboxInfo(infos, hapVerifyRes);
    return ret;
}

ErrCode BaseBundleInstaller::CheckArkTSMode(const std::unordered_map<std::string, InnerBundleInfo> &newInfos)
{
    if (newInfos.empty()) {
        LOG_W(BMS_TAG_INSTALLER, "newInfos is empty");
        return ERR_APPEXECFWK_INSTALL_INTERNAL_ERROR;
    }
    if (newInfos.begin()->second.GetApplicationBundleType() != BundleType::ATOMIC_SERVICE) {
        return ERR_OK;
    }
    auto needControl = std::any_of(newInfos.begin(), newInfos.end(),
        [](const auto &item) {
            return std::any_of(item.second.GetInnerModuleInfos().begin(),
                item.second.GetInnerModuleInfos().end(),
                [](const auto &moduleItem) {
                    return moduleItem.second.moduleArkTSMode != Constants::ARKTS_MODE_DYNAMIC;
                });
        });
    if (needControl) {
        LOG_W(BMS_TAG_INSTALLER, "atomicService is not dynamic -n:%{public}s",
            newInfos.begin()->second.GetBundleName().c_str());
        return ERR_APPEXECFWK_INSTALL_NOT_SUPPORT_STATIC_ATOMIC_SERVICE;
    }
    return ERR_OK;
}

void BaseBundleInstaller::UpdateExtensionSandboxInfo(std::unordered_map<std::string, InnerBundleInfo> &newInfos,
    const std::vector<Security::Verify::HapVerifyResult> &hapVerifyRes)
{
    if (newInfos.empty() || hapVerifyRes.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "innerBundleInfo map or hapVerifyRes is empty");
        return;
    }
    Security::Verify::ProvisionInfo provisionInfo = hapVerifyRes.begin()->GetProvisionInfo();
    auto dataGroupGids = provisionInfo.bundleInfo.dataGroupIds;
    std::vector<std::string> typeList;
    ErrCode res = InstalldClient::GetInstance()->GetExtensionSandboxTypeList(typeList);
    if (res != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "GetExtensionSandboxTypeList failed %{public}d", res);
    }
    for (auto &item : newInfos) {
        item.second.UpdateExtensionSandboxInfo(typeList);
        auto innerBundleInfo = item.second;
        auto extensionInfoMap = innerBundleInfo.GetInnerExtensionInfos();
        for (auto iter = extensionInfoMap.begin(); iter != extensionInfoMap.end(); iter++) {
            if (!iter->second.needCreateSandbox) {
                continue;
            }
            std::string key = iter->second.bundleName + "." + iter->second.moduleName + "." +  iter->second.name;

            std::vector<std::string> validGroupIds;
            GetValidDataGroupIds(iter->second.dataGroupIds, dataGroupGids, validGroupIds);
            LOG_I(BMS_TAG_INSTALLER, "extension %{public}s need to create dir on user %{public}d",
                iter->second.name.c_str(), userId_);
            item.second.UpdateExtensionDataGroupInfo(key, validGroupIds);
        }
    }
}

void BaseBundleInstaller::GetValidDataGroupIds(const std::vector<std::string> &extensionDataGroupIds,
    const std::vector<std::string> &bundleDataGroupIds, std::vector<std::string> &validGroupIds) const
{
    for (const std::string &dataGroupId : extensionDataGroupIds) {
        if (std::find(bundleDataGroupIds.begin(), bundleDataGroupIds.end(), dataGroupId) != bundleDataGroupIds.end()) {
            validGroupIds.emplace_back(dataGroupId);
        }
        LOG_I(BMS_TAG_INSTALLER, "dataGroupId %{public}s is invalid", dataGroupId.c_str());
    }
}

void BaseBundleInstaller::GetExtensionDirsChange(std::unordered_map<std::string, InnerBundleInfo> &newInfos,
    const InnerBundleInfo &oldInfo)
{
    GetCreateExtensionDirs(newInfos);
    GetRemoveExtensionDirs(newInfos, oldInfo);
}

void BaseBundleInstaller::CreateExtensionDataDir(InnerBundleInfo &info) const
{
    if (createExtensionDirs_.empty()) {
        return;
    }
    InnerBundleUserInfo newInnerBundleUserInfo;
    if (!info.GetInnerBundleUserInfo(userId_, newInnerBundleUserInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "bundle(%{public}s) get user(%{public}d) failed",
            info.GetBundleName().c_str(), userId_);
        return;
    }
    CreateDirParam createDirParam;
    createDirParam.bundleName = info.GetBundleName();
    createDirParam.userId = userId_;
    createDirParam.uid = newInnerBundleUserInfo.uid;
    createDirParam.gid = newInnerBundleUserInfo.uid;
    createDirParam.apl = info.GetAppPrivilegeLevel();
    createDirParam.isPreInstallApp = info.IsPreInstallApp();
    createDirParam.debug = info.GetBaseApplicationInfo().appProvisionType == Constants::APP_PROVISION_TYPE_DEBUG;
    createDirParam.extensionDirs.assign(createExtensionDirs_.begin(), createExtensionDirs_.end());

    auto result = InstalldClient::GetInstance()->CreateExtensionDataDir(createDirParam);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to create bundle extension data dir, error is %{public}d", result);
    }
}

bool BaseBundleInstaller::UpdateExtensionDirsApl(const std::vector<std::string> &updateExtensionDirs,
    const InnerBundleInfo &info) const
{
    if (updateExtensionDirs.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "bundle(%{public}s):no extensiondirs need to update selinux apl",
            info.GetBundleName().c_str());
        return false;
    }
    if (dataMgr_ == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "dataMgr_ is nullptr");
        return false;
    }
    bool res = true;
    std::set<int32_t> currentUserIds = dataMgr_->GetAllUser();
    for (int32_t user : currentUserIds) {
        InnerBundleUserInfo newInnerBundleUserInfo;
        if (!info.GetInnerBundleUserInfo(user, newInnerBundleUserInfo)) {
            continue;
        }

        CreateDirParam createDirParam;
        createDirParam.bundleName = info.GetBundleName();
        createDirParam.userId = user;
        createDirParam.uid = newInnerBundleUserInfo.uid;
        createDirParam.gid = newInnerBundleUserInfo.uid;
        createDirParam.apl = info.GetAppPrivilegeLevel();
        createDirParam.isPreInstallApp = info.IsPreInstallApp();
        createDirParam.debug = info.GetBaseApplicationInfo().appProvisionType == Constants::APP_PROVISION_TYPE_DEBUG;
        createDirParam.extensionDirs.assign(updateExtensionDirs.begin(), updateExtensionDirs.end());
        auto result = InstalldClient::GetInstance()->SetDirsApl(createDirParam, true);
        if (result != ERR_OK) {
            LOG_W(BMS_TAG_INSTALLER, "fail for bundle(%{public}s) in user(%{public}d), error:%{public}d",
                info.GetBundleName().c_str(), user, result);
            res = false;
        }
    }
    return res;
}

void BaseBundleInstaller::GenerateNewUserDataGroupInfos(InnerBundleInfo &info) const
{
    if (dataMgr_ == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "dataMgr_ is nullptr");
        return;
    }
    dataMgr_->GenerateNewUserDataGroupInfos(info.GetBundleName(), userId_);
}

void BaseBundleInstaller::GetCreateExtensionDirs(std::unordered_map<std::string, InnerBundleInfo> &newInfos)
{
    for (auto &item : newInfos) {
        auto innerBundleInfo = item.second;
        auto moduleName = innerBundleInfo.GetCurModuleName();
        auto extensionDirSet = innerBundleInfo.GetAllExtensionDirsInSpecifiedModule(moduleName);
        for (const std::string &dir : extensionDirSet) {
            newExtensionDirs_.emplace_back(dir);
            bool dirExist = false;
            auto result = InstalldClient::GetInstance()->IsExistExtensionDir(userId_, dir, dirExist);
            if (result != ERR_OK || !dirExist) {
                LOG_I(BMS_TAG_INSTALLER, "dir: %{public}s need to be created", dir.c_str());
                createExtensionDirs_.emplace_back(dir);
            }
        }
    }
}

void BaseBundleInstaller::GetRemoveExtensionDirs(
    std::unordered_map<std::string, InnerBundleInfo> &newInfos, const InnerBundleInfo &oldInfo)
{
    if (newInfos.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "newInfos is empty");
        return;
    }
    if (!isAppExist_) {
        // Install it for the first time
        return;
    }
    std::vector<std::string> oldModuleNames;
    const auto &innerBundleInfo = newInfos.begin()->second;
    oldInfo.GetModuleNames(oldModuleNames);
    if (innerBundleInfo.GetVersionCode() != oldInfo.GetVersionCode()) {
        std::set<std::string> newModules;
        for (const auto &item : newInfos) {
            std::vector<std::string> curModules;
            item.second.GetModuleNames(curModules);
            newModules.insert(curModules.begin(), curModules.end());
        }
        for (const std::string &oldModuleName : oldModuleNames) {
            if (newModules.find(oldModuleName) == newModules.end()) {
                // module does not exist in the later version, so it's extension dir needs to be removed
                const auto oldExtensionDirs = oldInfo.GetAllExtensionDirsInSpecifiedModule(oldModuleName);
                LOG_I(BMS_TAG_INSTALLER, "Dirs size %{public}zu need to be removed", oldExtensionDirs.size());
                std::copy(oldExtensionDirs.begin(), oldExtensionDirs.end(), std::back_inserter(removeExtensionDirs_));
            }
        }
    }
    for (const auto& item : newInfos) {
        std::string modulePackage = item.second.GetCurModuleName();
        if (!oldInfo.FindModule(modulePackage)) {
            // install a new module
            continue;
        }
        // update a existed module
        auto oldDirList = oldInfo.GetAllExtensionDirsInSpecifiedModule(
            oldInfo.GetModuleNameByPackage(modulePackage));
        for (const std::string &oldDir : oldDirList) {
            if (std::find(newExtensionDirs_.begin(), newExtensionDirs_.end(), oldDir) == newExtensionDirs_.end()) {
                LOG_I(BMS_TAG_INSTALLER, "dir %{public}s need to be removed", oldDir.c_str());
                removeExtensionDirs_.emplace_back(oldDir);
            }
        }
    }
}

void BaseBundleInstaller::RemoveCreatedExtensionDirsForException() const
{
    if (createExtensionDirs_.empty()) {
        LOG_D(BMS_TAG_INSTALLER, "no need to remove extension sandbox dir");
        return;
    }
    if (InstalldClient::GetInstance()->RemoveExtensionDir(userId_, createExtensionDirs_) != ERR_OK) {
        LOG_W(BMS_TAG_INSTALLER, "remove created extension sandbox dir failed");
    }
}

void BaseBundleInstaller::RemoveOldExtensionDirs() const
{
    if (removeExtensionDirs_.empty()) {
        LOG_D(BMS_TAG_INSTALLER, "no need to remove old extension sandbox dir");
        return;
    }
    auto result = InstalldClient::GetInstance()->RemoveExtensionDir(userId_, removeExtensionDirs_);
    if (result != ERR_OK) {
        LOG_W(BMS_TAG_INSTALLER, "remove old extension sandbox dir failed");
    }
}

std::string BaseBundleInstaller::GetCloneInstallSource(
    const std::string &originalInstallSource, const std::string &callingBundleName) const
{
    if (originalInstallSource == ServiceConstants::INSTALL_SOURCE_PREINSTALL ||
        originalInstallSource == ServiceConstants::INSTALL_SOURCE_OTA ||
        originalInstallSource == ServiceConstants::INSTALL_SOURCE_RECOVERY ||
        originalInstallSource == INSTALL_SOURCE_UNKNOWN) {
        LOG_NOFUNC_I(BMS_TAG_INSTALLER, "clone install source: %{public}s -> %{public}s",
            callingBundleName.c_str(), originalInstallSource.c_str());
        return std::string(ServiceConstants::INSTALL_SOURCE_PREFIX) +
            callingBundleName + "+" + originalInstallSource;
    }

    std::shared_ptr<BundleDataMgr> dataMgr = DelayedSingleton<BundleMgrService>::GetInstance()->GetDataMgr();
    if (dataMgr == nullptr) {
        LOG_NOFUNC_E(BMS_TAG_INSTALLER, "dataMgr is nullptr, return calling bundle name");
        return callingBundleName;
    }

    // Verify original install source bundle is installed
    InnerBundleInfo innerBundleInfo;
    if (!dataMgr->FetchInnerBundleInfo(originalInstallSource, innerBundleInfo)) {
        LOG_NOFUNC_W(BMS_TAG_INSTALLER, "original install source bundle not installed: %{public}s",
            originalInstallSource.c_str());
        return callingBundleName;
    }

    // Verify original install source bundle is a system app
    if (!innerBundleInfo.IsSystemApp()) {
        LOG_NOFUNC_W(BMS_TAG_INSTALLER, "original install source bundle is not system app: %{public}s",
            originalInstallSource.c_str());
        return callingBundleName;
    }

    // Format: +installSource:callingBundleName+originalInstallSource
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "clone install source: %{public}s -> %{public}s",
        callingBundleName.c_str(), originalInstallSource.c_str());
    return std::string(ServiceConstants::INSTALL_SOURCE_PREFIX) + callingBundleName + "+" + originalInstallSource;
}

std::string BaseBundleInstaller::GetInstallSource(const InstallParam &installParam) const
{
    if (installParam.isPreInstallApp) {
        switch (installParam.preinstallSourceFlag) {
            case ApplicationInfoFlag::FLAG_BOOT_INSTALLED:
                return ServiceConstants::INSTALL_SOURCE_PREINSTALL;
            case ApplicationInfoFlag::FLAG_OTA_INSTALLED:
                return ServiceConstants::INSTALL_SOURCE_OTA;
            case ApplicationInfoFlag::FLAG_RECOVER_INSTALLED:
                return ServiceConstants::INSTALL_SOURCE_RECOVERY;
            default:
                return ServiceConstants::INSTALL_SOURCE_PREINSTALL;
        }
    }
    std::shared_ptr<BundleDataMgr> dataMgr = DelayedSingleton<BundleMgrService>::GetInstance()->GetDataMgr();
    if (dataMgr == nullptr) {
        LOG_I(BMS_TAG_INSTALLER, "dataMgr is nullptr return unknown");
        return INSTALL_SOURCE_UNKNOWN;
    }
    std::string callingBundleName;
    ErrCode ret = dataMgr->GetNameForUid(sysEventInfo_.callingUid, callingBundleName);
    if (ret != ERR_OK) {
        LOG_I(BMS_TAG_INSTALLER, "get bundle name failed return unknown");
        return INSTALL_SOURCE_UNKNOWN;
    }

    auto item = installParam.parameters.find(ServiceConstants::BMS_PARA_ORIGINAL_INSTALL_SOURCE);
    if (item != installParam.parameters.end() && !item->second.empty()) {
        return GetCloneInstallSource(item->second, callingBundleName);
    }

    return callingBundleName;
}

void BaseBundleInstaller::SetApplicationFlagsAndInstallSource(
    std::unordered_map<std::string, InnerBundleInfo> &infos, const InstallParam &installParam) const
{
    std::string installSource = GetInstallSource(installParam);
    for (auto &info : infos) {
        info.second.SetApplicationFlags(installParam.preinstallSourceFlag);
        info.second.SetInstallSource(installSource);
    }
}

void BaseBundleInstaller::SetAppDistributionType(const std::unordered_map<std::string, InnerBundleInfo> &infos)
{
    if (infos.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "infos is empty");
        return;
    }
    appDistributionType_ = infos.begin()->second.GetAppDistributionType();
}

void BaseBundleInstaller::UpdateDeveloperId(
    std::unordered_map<std::string, InnerBundleInfo> &infos,
    const std::vector<Security::Verify::HapVerifyResult> &hapVerifyRes) const
{
    if (hapVerifyRes.size() < infos.size() || infos.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "hapVerifyRes size less than infos size or infos is empty");
        return;
    }
    std::shared_ptr<BundleDataMgr> dataMgr = DelayedSingleton<BundleMgrService>::GetInstance()->GetDataMgr();
    if (dataMgr == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "Get dataMgr shared_ptr nullptr");
        return;
    }

    std::string developerId = hapVerifyRes[0].GetProvisionInfo().bundleInfo.developerId;
    if (developerId.empty()) {
        developerId = hapVerifyRes[0].GetProvisionInfo().bundleInfo.bundleName;
    }

    for (auto &item : infos) {
        item.second.UpdateDeveloperId(developerId);
    }
}

void BaseBundleInstaller::GetDataGroupIds(const std::vector<Security::Verify::HapVerifyResult> &hapVerifyRes,
    std::unordered_set<std::string> &groupIds)
{
    for (uint32_t i = 0; i < hapVerifyRes.size(); ++i) {
        Security::Verify::ProvisionInfo provisionInfo = hapVerifyRes[i].GetProvisionInfo();
        auto dataGroupGids = provisionInfo.bundleInfo.dataGroupIds;
        if (dataGroupGids.empty()) {
            continue;
        }
        for (const std::string &id : dataGroupGids) {
            groupIds.insert(id);
        }
    }
}

ErrCode BaseBundleInstaller::CheckInstallCondition(
    std::vector<Security::Verify::HapVerifyResult> &hapVerifyRes,
    std::unordered_map<std::string, InnerBundleInfo> &infos, ErrCode checkSysCapRes)
{
    ErrCode ret;
    if (checkSysCapRes != ERR_OK) {
        ret = bundleInstallChecker_->CheckDeviceType(infos, checkSysCapRes);
        if (ret != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "CheckDeviceType failed due to errorCode : %{public}d", ret);
            return ERR_APPEXECFWK_INSTALL_SYSCAP_FAILED_AND_DEVICE_TYPE_ERROR;
        }
    }
    ret = bundleInstallChecker_->CheckIsolationMode(infos);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "CheckIsolationMode failed due to errorCode : %{public}d", ret);
        return ret;
    }
    ret = bundleInstallChecker_->CheckHspInstallCondition(hapVerifyRes, callerToken_);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "CheckInstallCondition failed due to errorCode : %{public}d", ret);
        return ret;
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CheckHapBinInstallCondition(const std::unordered_map<std::string,
    InnerBundleInfo> &infos) const
{
    for (const auto &infoPair : infos) {
        const InnerBundleInfo &info = infoPair.second;
        auto innerModuleInfos = info.GetInnerModuleInfos();
        for (const auto &modulePair : innerModuleInfos) {
            const InnerModuleInfo &moduleInfo = modulePair.second;
            if (moduleInfo.executableBinaryPaths.empty()) {
                continue;
            }
            if (!moduleInfo.compressNativeLibs || !OHOS::system::GetBoolParameter(
                ServiceConstants::HAP_BIN_INSTALL_ENABLE, false)) {
                LOG_E(BMS_TAG_INSTALLER, "hap bin install condition check failed for module %{public}s",
                    moduleInfo.name.c_str());
                return ERR_APPEXECFWK_INSTALL_FAILED_CHECK_BIN_FILE_FAILED;
            }
        }
    }
    return ERR_OK;
}

std::vector<std::string> BaseBundleInstaller::GetBinFilePaths(const InnerBundleInfo &info,
    const std::string &nativeLibraryPath) const
{
    std::vector<std::string> binFilePaths;
    if (nativeLibraryPath.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "nativeLibraryPath is empty");
        return binFilePaths;
    }
    std::string prefix = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR +
        info.GetBundleName() + ServiceConstants::PATH_SEPARATOR + nativeLibraryPath;
    if (prefix.back() != ServiceConstants::FILE_SEPARATOR_CHAR) {
        prefix += ServiceConstants::FILE_SEPARATOR_CHAR;
    }
    auto innerModuleInfos = info.GetInnerModuleInfos();
    std::string cpuAbi = info.GetCpuAbi();
    std::string libsPrefix = ServiceConstants::LIBS + cpuAbi + ServiceConstants::PATH_SEPARATOR;
    for (const auto &modulePair : innerModuleInfos) {
        const InnerModuleInfo &moduleInfo = modulePair.second;
        const auto &executableBinaryPaths = moduleInfo.executableBinaryPaths;
        for (const auto &executableBinaryPath : executableBinaryPaths) {
            if (executableBinaryPath.path.find("..") != std::string::npos) {
                continue;
            }
            std::string binPath = executableBinaryPath.path;
            if (binPath.find(libsPrefix) != 0) {
                continue;
            }
            binPath = binPath.substr(libsPrefix.length());
            std::string fullPath = prefix + binPath;
            if (!BundleUtil::IsExecutableBinaryFile(fullPath)) {
                LOG_D(BMS_TAG_INSTALLER, "skip non-executable file: %{private}s", fullPath.c_str());
                continue;
            }
            binFilePaths.push_back(fullPath);
        }
    }
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "binFilePaths size: %{public}zu", binFilePaths.size());
    return binFilePaths;
}

ErrCode BaseBundleInstaller::ProcessBinFiles(
    const std::unordered_map<std::string, InnerBundleInfo> &infos) const
{
    if (infos.empty()) {
        return ERR_OK;
    }
    std::vector<std::string> binFilePaths;
    for (const auto &infoPair : infos) {
        const InnerBundleInfo &info = infoPair.second;
        std::string nativeLibraryPath = info.GetNativeLibraryPath();
        auto paths = GetBinFilePaths(info, nativeLibraryPath);
        binFilePaths.insert(binFilePaths.end(), paths.begin(), paths.end());
    }
    if (binFilePaths.empty()) {
        LOG_D(BMS_TAG_INSTALLER, "binFilePaths is empty");
        return ERR_OK;
    }
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "ProcessBinFiles start -n %{public}s", bundleName_.c_str());
    VerifyBinParam verifyBinParam;
    verifyBinParam.bundleName = bundleName_;
    verifyBinParam.appIdentifier = appIdentifier_;
    verifyBinParam.userId = userId_;
    verifyBinParam.binFilePaths = binFilePaths;

    return InstalldClient::GetInstance()->ProcessBinFiles(verifyBinParam);
}

ErrCode BaseBundleInstaller::CheckInstallPermission(const InstallParam &installParam,
    std::vector<Security::Verify::HapVerifyResult> &hapVerifyRes)
{
    if ((installParam.installBundlePermissionStatus != PermissionStatus::NOT_VERIFIED_PERMISSION_STATUS ||
        installParam.installEnterpriseBundlePermissionStatus != PermissionStatus::NOT_VERIFIED_PERMISSION_STATUS ||
        installParam.installEtpNormalBundlePermissionStatus != PermissionStatus::NOT_VERIFIED_PERMISSION_STATUS ||
        installParam.installEtpMdmBundlePermissionStatus != PermissionStatus::NOT_VERIFIED_PERMISSION_STATUS ||
        installParam.installInternaltestingBundlePermissionStatus != PermissionStatus::NOT_VERIFIED_PERMISSION_STATUS ||
        installParam.installUpdateSelfBundlePermissionStatus != PermissionStatus::NOT_VERIFIED_PERMISSION_STATUS) &&
        !bundleInstallChecker_->VaildInstallPermission(installParam, hapVerifyRes)) {
        // need vaild permission
        LOG_E(BMS_TAG_INSTALLER, "install permission denied");
        return ERR_APPEXECFWK_INSTALL_PERMISSION_DENIED;
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CheckDependency(std::unordered_map<std::string, InnerBundleInfo> &infos,
    const SharedBundleInstaller &sharedBundleInstaller)
{
    for (const auto &info : infos) {
        if (!sharedBundleInstaller.CheckDependency(info.second)) {
            LOG_E(BMS_TAG_INSTALLER, "cross-app dependency check failed");
            return ERR_APPEXECFWK_INSTALL_DEPENDENT_MODULE_NOT_EXIST;
        }
    }

    return bundleInstallChecker_->CheckDependency(infos);
}

ErrCode BaseBundleInstaller::CheckHapHashParams(
    std::unordered_map<std::string, InnerBundleInfo> &infos,
    std::map<std::string, std::string> hashParams)
{
    return bundleInstallChecker_->CheckHapHashParams(infos, hashParams);
}

ErrCode BaseBundleInstaller::CheckAppLabelInfo(const std::unordered_map<std::string, InnerBundleInfo> &infos)
{
    for (const auto &info : infos) {
        if (info.second.GetApplicationBundleType() == BundleType::SHARED) {
            LOG_E(BMS_TAG_INSTALLER, "installing cross-app shared library");
            return ERR_APPEXECFWK_INSTALL_FILE_IS_SHARED_LIBRARY;
        }
    }

    ErrCode ret = bundleInstallChecker_->CheckAppLabelInfo(infos);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "check app label info error");
        return ret;
    }

    if (!CheckApiInfo(infos)) {
        LOG_E(BMS_TAG_INSTALLER, "CheckApiInfo failed");
        return ERR_APPEXECFWK_INSTALL_SDK_INCOMPATIBLE;
    }

    bundleName_ = (infos.begin()->second).GetBundleName();
    versionCode_ = bundleInstallChecker_->GetVersionCode(infos);
    return ERR_OK;
}

bool BaseBundleInstaller::CheckApiInfo(const std::unordered_map<std::string, InnerBundleInfo> &infos)
{
    std::string compileSdkType = infos.begin()->second.GetBaseApplicationInfo().compileSdkType;
    auto bundleInfo = infos.begin()->second.GetBaseBundleInfo();
    auto systemApiVersion = std::make_tuple(
        static_cast<uint32_t>(GetSdkApiVersion()),
        static_cast<uint32_t>(GetSdkMinorApiVersion()),
        static_cast<uint32_t>(GetSdkPatchApiVersion()));
    auto compatibleVersion = std::make_tuple(
        bundleInfo.compatibleVersion, bundleInfo.compatibleMinorVersion, bundleInfo.compatiblePatchVersion);
    if (compileSdkType == COMPILE_SDK_TYPE_OPEN_HARMONY) {
        bool res = compatibleVersion <= systemApiVersion;
        if (!res) {
            auto [major, minor, patch] = systemApiVersion;
            LOG_E(BMS_TAG_INSTALLER, "CheckApiInfo failed with compatibleVersion: %{public}d.%{public}d.%{public}d, "
                "systemApiVersion: %{public}d.%{public}d.%{public}d",
                bundleInfo.compatibleVersion, bundleInfo.compatibleMinorVersion, bundleInfo.compatiblePatchVersion,
                major, minor, patch);
        }
        return res;
    }
    BmsExtensionDataMgr bmsExtensionDataMgr;
    return bmsExtensionDataMgr.CheckApiInfo(infos.begin()->second.GetBaseBundleInfo(), systemApiVersion);
}

ErrCode BaseBundleInstaller::CheckMultiNativeFile(
    std::unordered_map<std::string, InnerBundleInfo> &infos)
{
    return bundleInstallChecker_->CheckMultiNativeFile(infos);
}

ErrCode BaseBundleInstaller::CheckProxyDatas(
    const std::unordered_map<std::string, InnerBundleInfo> &infos)
{
    if (!CheckDuplicateProxyData(infos)) {
        LOG_E(BMS_TAG_INSTALLER, "duplicated uri in proxyDatas");
        return ERR_APPEXECFWK_INSTALL_CHECK_PROXY_DATA_URI_FAILED;
    }
    for (const auto &info : infos) {
        ErrCode ret = bundleInstallChecker_->CheckProxyDatas(info.second);
        if (ret != ERR_OK) {
            return ret;
        }
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CheckMDMUpdateBundleForSelf(const InstallParam &installParam,
    InnerBundleInfo &oldInfo, const std::unordered_map<std::string, InnerBundleInfo> &newInfos, bool isAppExist)
{
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_NULL_PTR;
    }
    if (!installParam.isSelfUpdate) {
        return ERR_OK;
    }
    if (!OHOS::system::GetBoolParameter(ServiceConstants::ALLOW_ENTERPRISE_BUNDLE, false) &&
        !OHOS::system::GetBoolParameter(ServiceConstants::IS_ENTERPRISE_DEVICE, false) &&
        !OHOS::system::GetBoolParameter(ServiceConstants::DEVELOPERMODE_STATE, false)) {
        LOG_E(BMS_TAG_INSTALLER, "not enterprise device or developer mode is off");
        return ERR_APPEXECFWK_INSTALL_ENTERPRISE_BUNDLE_NOT_ALLOWED;
    }
    if (!isAppExist) {
        LOG_E(BMS_TAG_INSTALLER, "not self update");
        return ERR_APPEXECFWK_INSTALL_SELF_UPDATE_BUNDLENAME_NOT_SAME;
    }
    std::string appDistributionType = oldInfo.GetAppDistributionType();
    if (appDistributionType != Constants::APP_DISTRIBUTION_TYPE_ENTERPRISE_MDM) {
        LOG_E(BMS_TAG_INSTALLER, "not mdm app");
        return ERR_APPEXECFWK_INSTALL_SELF_UPDATE_NOT_MDM;
    }
    std::string bundleName;
    if (!dataMgr_->GetBundleNameForUid(sysEventInfo_.callingUid, bundleName)) {
        bundleName = Constants::EMPTY_STRING;
    }
    for (const auto &info : newInfos) {
        if (bundleName != info.second.GetBundleName()) {
            LOG_E(BMS_TAG_INSTALLER, "callingBundleName %{public}s bundleName %{public}s not same",
                bundleName.c_str(), info.second.GetBundleName().c_str());
            return ERR_APPEXECFWK_INSTALL_SELF_UPDATE_BUNDLENAME_NOT_SAME;
        }
    }
    return ERR_OK;
}

bool BaseBundleInstaller::FetchInnerBundleInfo(InnerBundleInfo &info, bool &isAppExist)
{
    if (!InitDataMgr()) {
        return false;
    }
    isAppExist = dataMgr_->FetchInnerBundleInfo(bundleName_, info);
    return true;
}

bool BaseBundleInstaller::InitTempBundleFromCache(InnerBundleInfo &info, bool &isAppExist, std::string bundleName)
{
    if (!InitDataMgr()) {
        return false;
    }
    std::string cacheBundle = bundleName;
    if (cacheBundle.empty()) {
        cacheBundle = bundleName_;
    }
    isAppExist = dataMgr_->FetchInnerBundleInfo(cacheBundle, info);
    if (isAppExist) {
        tempInfo_.SetTempBundleInfo(info);
    }
    bundleName_ = cacheBundle;
    return true;
}

bool BaseBundleInstaller::GetTempBundleInfo(InnerBundleInfo &info) const
{
    return tempInfo_.GetTempBundleInfo(info);
}

bool BaseBundleInstaller::InitDataMgr()
{
    if (dataMgr_ == nullptr) {
        dataMgr_ = DelayedSingleton<BundleMgrService>::GetInstance()->GetDataMgr();
        if (dataMgr_ == nullptr) {
            LOG_E(BMS_TAG_INSTALLER, "Get dataMgr shared_ptr nullptr");
            return false;
        }
    }
    return true;
}

void BaseBundleInstaller::CheckInstallAllowDowngrade(
    const InstallParam &installParam, const InnerBundleInfo &oldBundleInfo, ErrCode &result)
{
    if ((result != ERR_APPEXECFWK_INSTALL_VERSION_DOWNGRADE) || oldBundleInfo.IsSystemApp()) {
        return;
    }
    if ((oldBundleInfo.GetAppDistributionType() != Constants::APP_DISTRIBUTION_TYPE_NONE) &&
        (oldBundleInfo.GetAppDistributionType() != Constants::APP_DISTRIBUTION_TYPE_APP_GALLERY)) {
        return;
    }
    auto item = installParam.parameters.find(ServiceConstants::BMS_PARA_INSTALL_ALLOW_DOWNGRADE);
    if ((item == installParam.parameters.end()) || (item->second != ServiceConstants::BMS_TRUE)) {
        return;
    }
    // check provision type
    auto provisionInfo = verifyRes_.GetProvisionInfo();
    // emulator support no signature
    if (provisionInfo.profileBlockLength != 0) {
        auto newProvisionType = (provisionInfo.type == Security::Verify::ProvisionType::DEBUG) ?
            Constants::APP_PROVISION_TYPE_DEBUG : Constants::APP_PROVISION_TYPE_RELEASE;
        if (oldBundleInfo.GetAppProvisionType() != newProvisionType) {
            LOG_E(BMS_TAG_INSTALLER, "%{public}s update from %{public}s to %{public}s denied", bundleName_.c_str(),
                oldBundleInfo.GetAppProvisionType().c_str(), newProvisionType);
            result = ERR_APPEXECFWK_INSTALL_APP_PROVISION_TYPE_NOT_SAME;
            return;
        }
    }
    if (!oldBundleInfo.GetEntryInstallationFree()) {
        if (oldBundleInfo.HasEntry() && !isContainEntry_) {
            LOG_E(BMS_TAG_INSTALLER, "-n %{public}s -v %{public}d only has lower version feature",
                bundleName_.c_str(), versionCode_);
            result = ERR_APPEXECFWK_INSTALL_VERSION_NOT_COMPATIBLE;
            return;
        }
    }
    LOG_I(BMS_TAG_INSTALLER, "-n %{public}s -v %{public}d lower than installed", bundleName_.c_str(), versionCode_);
    isFeatureNeedUninstall_ = true;
    result = ERR_OK;
}

ErrCode BaseBundleInstaller::CheckVersionCompatibility(const InnerBundleInfo &oldInfo)
{
    if (oldInfo.GetEntryInstallationFree()) {
        return CheckVersionCompatibilityForHmService(oldInfo);
    }
    return CheckVersionCompatibilityForApplication(oldInfo);
}

// In the process of hap updating, the version code of the entry hap which is about to be updated must not less the
// version code of the current entry haps in the device; if no-entry hap in the device, the updating haps should
// have same version code with the current version code; if the no-entry haps is to be updated, which should has the
// same version code with that of the entry hap in the device.
ErrCode BaseBundleInstaller::CheckVersionCompatibilityForApplication(const InnerBundleInfo &oldInfo)
{
    LOG_D(BMS_TAG_INSTALLER, "start to check version compatibility for application");
    if (oldInfo.HasEntry()) {
        if (isContainEntry_ && versionCode_ < oldInfo.GetVersionCode()) {
            LOG_E(BMS_TAG_INSTALLER, "fail to update lower version bundle");
            return ERR_APPEXECFWK_INSTALL_VERSION_DOWNGRADE;
        }
        if (!isContainEntry_ && versionCode_ > oldInfo.GetVersionCode()) {
            LOG_E(BMS_TAG_INSTALLER, "version code is not compatible");
            return ERR_APPEXECFWK_INSTALL_VERSION_NOT_COMPATIBLE;
        }
        if (!isContainEntry_ && versionCode_ < oldInfo.GetVersionCode()) {
            LOG_E(BMS_TAG_INSTALLER, "version code is not compatible");
            return ERR_APPEXECFWK_INSTALL_VERSION_DOWNGRADE;
        }
    } else {
        if (versionCode_ < oldInfo.GetVersionCode()) {
            LOG_E(BMS_TAG_INSTALLER, "fail to update lower version bundle");
            return ERR_APPEXECFWK_INSTALL_VERSION_DOWNGRADE;
        }
    }

    if (versionCode_ != oldInfo.GetVersionCode()) {
        if (oldInfo.GetApplicationBundleType() == BundleType::APP_SERVICE_FWK) {
            LOG_E(BMS_TAG_INSTALLER, "Not alloweded instal appService hap(%{public}s) due to the hsp does not exist",
                oldInfo.GetBundleName().c_str());
            return ERR_APP_SERVICE_FWK_INSTALL_TYPE_FAILED;
        }
        LOG_D(BMS_TAG_INSTALLER, "need to uninstall lower version feature hap");
        isFeatureNeedUninstall_ = true;
    }
    LOG_D(BMS_TAG_INSTALLER, "finish to check version compatibility for application");
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CheckVersionCompatibilityForHmService(const InnerBundleInfo &oldInfo)
{
    LOG_D(BMS_TAG_INSTALLER, "start to check version compatibility for hm service");
    if (versionCode_ < oldInfo.GetVersionCode()) {
        LOG_E(BMS_TAG_INSTALLER, "fail to update lower version bundle");
        return ERR_APPEXECFWK_INSTALL_VERSION_DOWNGRADE;
    }
    if (versionCode_ > oldInfo.GetVersionCode()) {
        LOG_D(BMS_TAG_INSTALLER, "need to uninstall lower version hap");
        isFeatureNeedUninstall_ = true;
    }
    LOG_D(BMS_TAG_INSTALLER, "finish to check version compatibility for hm service");
    return ERR_OK;
}

ErrCode BaseBundleInstaller::UninstallLowerVersionFeature(const std::vector<std::string> &packageVec, bool killProcess)
{
    LOG_D(BMS_TAG_INSTALLER, "start to uninstall lower version feature hap");
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_NULL_PTR;
    }
    InnerBundleInfo info;
    if (!GetTempBundleInfo(info)) {
        return ERR_APPEXECFWK_UNINSTALL_BUNDLE_MGR_SERVICE_ERROR;
    }

    if (!dataMgr_->UpdateBundleInstallState(bundleName_, InstallState::UNINSTALL_START)) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall already start");
        return ERR_APPEXECFWK_UPDATE_BUNDLE_INSTALL_STATUS_ERROR;
    }

    // kill the bundle process during uninstall.
    if (killProcess) {
        if (!AbilityManagerHelper::UninstallApplicationProcesses(
            info.GetApplicationName(), info.GetUid(userId_), true)) {
            LOG_W(BMS_TAG_INSTALLER, "can not kill process");
        }
        InnerBundleUserInfo userInfo;
        if (!info.GetInnerBundleUserInfo(userId_, userInfo)) {
            LOG_W(BMS_TAG_INSTALLER, "the origin application is not installed at current user");
            return ERR_APPEXECFWK_GET_USERINFO_ERROR;
        }
        for (auto &cloneInfo : userInfo.cloneInfos) {
            if (!AbilityManagerHelper::UninstallApplicationProcesses(
                info.GetApplicationName(), cloneInfo.second.uid, true, atoi(cloneInfo.first.c_str()))) {
                LOG_W(BMS_TAG_INSTALLER, "fail to kill clone application");
            }
        }
    }

    std::vector<std::string> moduleVec = info.GetModuleNameVec();
    for (const auto &package : moduleVec) {
        if (find(packageVec.begin(), packageVec.end(), package) == packageVec.end()) {
            LOG_D(BMS_TAG_INSTALLER, "uninstall package %{public}s", package.c_str());
            if (!isFeatureNeedUninstall_) {
                ErrCode result = RemoveModuleAndDataDir(info, package, Constants::UNSPECIFIED_USERID, true);
                CHECK_RESULT(result, "remove module dir failed %{public}d");
            }

            // remove driver file
            std::shared_ptr driverInstaller = std::make_shared<DriverInstaller>();
            driverInstaller->RemoveDriverSoFile(info, info.GetModuleName(package), false);

            if (!dataMgr_->RemoveModuleInfo(bundleName_, package, info, false)) {
                LOG_E(BMS_TAG_INSTALLER, "RemoveModuleInfo failed");
                return ERR_APPEXECFWK_RMV_MODULE_ERROR;
            }
        }
    }
    tempInfo_.SetTempBundleInfo(info);
    needDeleteQuickFixInfo_ = true;
    LOG_D(BMS_TAG_INSTALLER, "finish to uninstall lower version feature hap");
    return ERR_OK;
}

int32_t BaseBundleInstaller::GetConfirmUserId(
    const int32_t &userId, std::unordered_map<std::string, InnerBundleInfo> &newInfos)
{
    bool isSingleton = newInfos.begin()->second.IsSingleton();
    bool u1Enabled = newInfos.begin()->second.IsU1Enable();
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "userId is Unspecified, singleton(%{public}d), u1Enabled(%{public}d)",
        static_cast<int32_t>(isSingleton), static_cast<int32_t>(u1Enabled));
    if (!otaInstall_) {
        if (isSingleton) {
            return Constants::DEFAULT_USERID;
        }
        if (u1Enabled) {
            return Constants::U1;
        }
    }
    if (userId != Constants::UNSPECIFIED_USERID || newInfos.size() <= 0) {
        return userId;
    }
    int32_t currUserId = sysEventInfo_.callingUid / Constants::BASE_USER_RANGE;
    currUserId = currUserId < Constants::START_USERID ? AccountHelper::GetUserIdByCallerType() : currUserId;
    return currUserId;
}

ErrCode BaseBundleInstaller::CheckUserId(const int32_t &userId) const
{
    if (userId == Constants::UNSPECIFIED_USERID) {
        return ERR_OK;
    }

    if (dataMgr_ == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "dataMgr_ is nullptr");
        return ERR_APPEXECFWK_NULL_PTR;
    }
    if (!dataMgr_->HasUserId(userId)) {
        LOG_E(BMS_TAG_INSTALLER, "The user %{public}d does not exist when install", userId);
        return ERR_APPEXECFWK_USER_NOT_EXIST;
    }

    return ERR_OK;
}

int32_t BaseBundleInstaller::GetUserId(const int32_t &userId) const
{
    if (userId == Constants::UNSPECIFIED_USERID) {
        return userId;
    }

    if (userId < Constants::DEFAULT_USERID) {
        LOG_E(BMS_TAG_INSTALLER, "userId(%{public}d) is invalid", userId);
        return Constants::INVALID_USERID;
    }

    LOG_D(BMS_TAG_INSTALLER, "BundleInstaller GetUserId, now userId is %{public}d", userId);
    return userId;
}

ErrCode BaseBundleInstaller::CreateBundleUserData(InnerBundleInfo &innerBundleInfo)
{
    LOG_I(BMS_TAG_INSTALLER, "CreateNewUserData %{public}s userId: %{public}d",
        innerBundleInfo.GetBundleName().c_str(), userId_);
    if (!innerBundleInfo.HasInnerBundleUserInfo(userId_)) {
        return ERR_APPEXECFWK_USER_NOT_EXIST;
    }

    ErrCode result = CreateBundleDataDir(innerBundleInfo);
    if (result != ERR_OK) {
        RemoveBundleDataDir(innerBundleInfo, true);
        return result;
    }

    int64_t currentTime = BundleUtil::GetCurrentTimeMs();
    innerBundleInfo.SetBundleInstallTime(currentTime, userId_);
    SetFirstInstallTime(innerBundleInfo.GetBundleName(), currentTime, innerBundleInfo);
    InnerBundleUserInfo innerBundleUserInfo;
    if (!innerBundleInfo.GetInnerBundleUserInfo(userId_, innerBundleUserInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "oldInfo do not have user");
        return ERR_APPEXECFWK_USER_NOT_EXIST;
    }

#ifdef BUNDLE_FRAMEWORK_OVERLAY_INSTALLATION
    OverlayDataMgr::GetInstance()->AddOverlayModuleStates(innerBundleInfo, innerBundleUserInfo);
#endif
    innerBundleInfo.AddInnerBundleUserInfo(innerBundleUserInfo);
    tempInfo_.SetTempBundleInfo(innerBundleInfo);
    return ERR_OK;
}

ErrCode BaseBundleInstaller::UninstallAllSandboxApps(const std::string &bundleName, int32_t userId)
{
    // All sandbox will be uninstalled when the original application is updated or uninstalled
    LOG_D(BMS_TAG_INSTALLER, "UninstallAllSandboxApps begin");
    if (bundleName.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "UninstallAllSandboxApps failed due to empty bundle name");
        return ERR_APPEXECFWK_INSTALL_PARAM_ERROR;
    }
    auto helper = DelayedSingleton<BundleSandboxAppHelper>::GetInstance();
    if (helper == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "UninstallAllSandboxApps failed due to helper nullptr");
        return ERR_APPEXECFWK_NULL_PTR;
    }
    if (helper->UninstallAllSandboxApps(bundleName, userId) != ERR_OK) {
        LOG_W(BMS_TAG_INSTALLER, "UninstallAllSandboxApps failed");
        return ERR_APPEXECFWK_UNINSTALL_ALL_SANDBOX_BUNDLE_ERROR;
    }
    LOG_D(BMS_TAG_INSTALLER, "UninstallAllSandboxApps finish");
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CheckNativeFileWithOldInfo(
    const InnerBundleInfo &oldInfo, std::unordered_map<std::string, InnerBundleInfo> &newInfos)
{
    LOG_D(BMS_TAG_INSTALLER, "CheckNativeFileWithOldInfo begin");
    if (HasAllOldModuleUpdate(oldInfo, newInfos)) {
        LOG_D(BMS_TAG_INSTALLER, "All installed haps will be updated");
        return ERR_OK;
    }

    ErrCode result = CheckNativeSoWithOldInfo(oldInfo, newInfos);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "Check nativeSo with oldInfo failed, result: %{public}d", result);
        return result;
    }

    result = CheckArkNativeFileWithOldInfo(oldInfo, newInfos);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "Check arkNativeFile with oldInfo failed, result: %{public}d", result);
        return result;
    }

    LOG_D(BMS_TAG_INSTALLER, "CheckNativeFileWithOldInfo end");
    return ERR_OK;
}

bool BaseBundleInstaller::HasAllOldModuleUpdate(
    const InnerBundleInfo &oldInfo, const std::unordered_map<std::string, InnerBundleInfo> &newInfos)
{
    const auto &newInfo = newInfos.begin()->second;
    bool allOldModuleUpdate = true;
    if (newInfo.GetVersionCode() != oldInfo.GetVersionCode()) {
        LOG_D(BMS_TAG_INSTALLER, "All installed haps will be updated");
        DeleteOldArkNativeFile(oldInfo);
        return allOldModuleUpdate;
    }

    std::vector<std::string> installedModules = oldInfo.GetModuleNameVec();
    for (const auto &installedModule : installedModules) {
        auto updateModule = std::find_if(std::begin(newInfos), std::end(newInfos),
            [ &installedModule ] (const auto &item) { return item.second.FindModule(installedModule); });
        if (updateModule == newInfos.end()) {
            LOG_D(BMS_TAG_INSTALLER, "Some installed haps will not be updated");
            allOldModuleUpdate = false;
            break;
        }
    }
    return allOldModuleUpdate;
}

ErrCode BaseBundleInstaller::CheckArkNativeFileWithOldInfo(
    const InnerBundleInfo &oldInfo, std::unordered_map<std::string, InnerBundleInfo> &newInfos)
{
    LOG_D(BMS_TAG_INSTALLER, "CheckArkNativeFileWithOldInfo begin");
    std::string oldArkNativeFileAbi = oldInfo.GetArkNativeFileAbi();
    if (oldArkNativeFileAbi.empty()) {
        LOG_D(BMS_TAG_INSTALLER, "OldInfo no arkNativeFile");
        return ERR_OK;
    }

    if (newInfos.empty()) {
        LOG_D(BMS_TAG_INSTALLER, "newInfos is empty");
        return ERR_APPEXECFWK_INSTALL_AN_INCOMPATIBLE;
    }

    std::string arkNativeFileAbi = newInfos.begin()->second.GetArkNativeFileAbi();
    if (arkNativeFileAbi.empty()) {
        LOG_D(BMS_TAG_INSTALLER, "NewInfos no arkNativeFile");
        for (auto& item : newInfos) {
            item.second.SetArkNativeFileAbi(oldInfo.GetArkNativeFileAbi());
            item.second.SetArkNativeFilePath(oldInfo.GetArkNativeFilePath());
        }
    }

    LOG_D(BMS_TAG_INSTALLER, "CheckArkNativeFileWithOldInfo end");
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CheckNativeSoWithOldInfo(
    const InnerBundleInfo &oldInfo, std::unordered_map<std::string, InnerBundleInfo> &newInfos)
{
    LOG_D(BMS_TAG_INSTALLER, "CheckNativeSoWithOldInfo begin");
    if (oldInfo.GetNativeLibraryPath().empty()) {
        LOG_D(BMS_TAG_INSTALLER, "OldInfo does not has so");
        return ERR_OK;
    }

    const auto &newInfo = newInfos.begin()->second;
    bool newInfoHasSo = !newInfo.GetNativeLibraryPath().empty();
    //newInfo should be consistent with oldInfo
    if (!newInfoHasSo) {
        for (auto& item : newInfos) {
            item.second.SetNativeLibraryPath(oldInfo.GetNativeLibraryPath());
            item.second.SetCpuAbi(oldInfo.GetCpuAbi());
        }
    }

    LOG_D(BMS_TAG_INSTALLER, "CheckNativeSoWithOldInfo end");
    return ERR_OK;
}

void BaseBundleInstaller::SetOldAppIsEncrypted(const InnerBundleInfo &oldInfo)
{
    if (!isAppExist_) {
        return;
    }
    oldApplicationReservedFlag_ = oldInfo.GetApplicationReservedFlag();
}

bool BaseBundleInstaller::UpdateEncryptedStatus(const InnerBundleInfo &oldInfo)
{
    if (!InitDataMgr()) {
        LOG_E(BMS_TAG_INSTALLER, "init failed");
        return false;
    }
    InnerBundleInfo innerBundleInfo;
    if (!GetTempBundleInfo(innerBundleInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "get failed");
        return false;
    }
    CodeProtectBundleInfo info;
    info.bundleName = innerBundleInfo.GetBundleName();
    info.versionCode = innerBundleInfo.GetVersionCode();
    info.applicationReservedFlag = innerBundleInfo.GetApplicationReservedFlag();
    info.uid = innerBundleInfo.GetUid(userId_);
    info.appIdentifier = innerBundleInfo.GetAppIdentifier();
    std::vector<CodeProtectBundleInfo> infos { info };
    bool oldAppEncrypted = oldApplicationReservedFlag_ &
        static_cast<uint32_t>(ApplicationReservedFlag::ENCRYPTED_APPLICATION);
    bool newAppEncrypted = innerBundleInfo.GetApplicationReservedFlag() &
        static_cast<uint32_t>(ApplicationReservedFlag::ENCRYPTED_APPLICATION);
    BmsExtensionDataMgr bmsExtensionDataMgr;
    if (!isAppExist_ && newAppEncrypted) {
        // add a new encrypted app, need to add operation
        auto res = bmsExtensionDataMgr.KeyOperation(infos, CodeOperation::ADD);
        ProcessEncryptedKeyExisted(res, CodeOperation::ADD, infos);
        return res == ERR_OK;
    }
    if (isAppExist_ && oldAppEncrypted && !newAppEncrypted) {
        // new app is not a encrypted app, need to delete operation on main app & all clone app
        infos[0].versionCode = oldInfo.GetVersionCode();
        auto res = bmsExtensionDataMgr.KeyOperation(infos, CodeOperation::DELETE);
        ProcessEncryptedKeyExisted(res, CodeOperation::DELETE, infos);
        return res == ERR_OK;
    }
    if (isAppExist_ && newAppEncrypted) {
        // update a new encrypted app, need to update operation
        auto res = bmsExtensionDataMgr.KeyOperation(infos, CodeOperation::UPDATE);
        ProcessEncryptedKeyExisted(res, CodeOperation::UPDATE, infos);
        return res == ERR_OK;
    }
    return true;
}

void BaseBundleInstaller::ProcessEncryptedKeyExisted(int32_t res, uint32_t type,
    const std::vector<CodeProtectBundleInfo> &infos)
{
    if (infos.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "init failed or infos is empty");
        return;
    }
    std::string bundleName = infos.begin()->bundleName;
    if (type == CodeOperation::ADD) {
        if (res == ERR_OK) {
            UpdateAppEncryptedStatus(bundleName, true, 0);
        } else {
            UpdateAppEncryptedStatus(bundleName, false, 0);
        }
        return;
    } else if (type == CodeOperation::DELETE) {
        if (res == ERR_OK) {
            UpdateAppEncryptedStatus(bundleName, false, 0);
        } else {
            UpdateAppEncryptedStatus(bundleName, true, 0);
        }
        return;
    }
    // UPDATE
    if (res == ERR_OK) {
        UpdateAppEncryptedStatus(bundleName, true, 0);
        for (const auto &codeProtectBundleInfo : infos) {
            UpdateAppEncryptedStatus(bundleName, true, codeProtectBundleInfo.appIndex);
        }
    } else {
        UpdateAppEncryptedStatus(bundleName, false, 0);
        for (const auto &codeProtectBundleInfo : infos) {
            UpdateAppEncryptedStatus(bundleName, false, codeProtectBundleInfo.appIndex);
        }
    }
}

ErrCode BaseBundleInstaller::UpdateAppEncryptedStatus(const std::string &bundleName, bool isExisted, int32_t appIndex)
{
    InnerBundleInfo info;
    tempInfo_.GetTempBundleInfo(info);
    auto res = info.UpdateAppEncryptedStatus(bundleName, isExisted, appIndex);
    tempInfo_.SetTempBundleInfo(info);
    if (res != ERR_OK) {
        LOG_E(BMS_TAG_DEFAULT, "UpdateAppEncryptedStatus err %{public}s %{public}d", bundleName.c_str(), res);
        return res;
    }
    return ERR_OK;
}

bool BaseBundleInstaller::DeleteEncryptedStatus(const std::string &bundleName, int32_t uid)
{
    bool oldAppEncrypted = oldApplicationReservedFlag_ &
        static_cast<uint32_t>(ApplicationReservedFlag::ENCRYPTED_APPLICATION);
    if (!oldAppEncrypted) {
        return true;
    }
    CodeProtectBundleInfo info;
    info.bundleName = bundleName;
    info.applicationReservedFlag = oldApplicationReservedFlag_;
    info.versionCode = versionCode_;
    info.uid = uid;
    info.appIdentifier = appIdentifier_;
    std::vector<CodeProtectBundleInfo> infos { info };
    BmsExtensionDataMgr bmsExtensionDataMgr;
    return bmsExtensionDataMgr.KeyOperation(infos, CodeOperation::DELETE) == ERR_OK;
}

ErrCode BaseBundleInstaller::CheckAppLabel(const InnerBundleInfo &oldInfo, const InnerBundleInfo &newInfo) const
{
    // check app label for inheritance installation
    LOG_D(BMS_TAG_INSTALLER, "CheckAppLabel begin");
    if (!CheckReleaseTypeIsCompatible(oldInfo, newInfo)) {
        return ERR_APPEXECFWK_INSTALL_RELEASETYPE_NOT_SAME;
    }
    if (oldInfo.GetAppProvisionType() != newInfo.GetAppProvisionType()) {
        return ERR_APPEXECFWK_INSTALL_APP_PROVISION_TYPE_NOT_SAME;
    }
    if (oldInfo.GetAppFeature() != newInfo.GetAppFeature()) {
        return ERR_APPEXECFWK_INSTALL_APPTYPE_NOT_SAME;
    }
    if (oldInfo.GetIsNewVersion() != newInfo.GetIsNewVersion()) {
        LOG_E(BMS_TAG_INSTALLER, "same version update module condition, model type must be the same");
        return ERR_APPEXECFWK_INSTALL_STATE_ERROR;
    }
#ifdef BUNDLE_FRAMEWORK_OVERLAY_INSTALLATION
    if (oldInfo.GetTargetBundleName() != newInfo.GetTargetBundleName()) {
        return ERR_BUNDLEMANAGER_OVERLAY_INSTALLATION_FAILED_TARGET_BUNDLE_NAME_NOT_SAME;
    }
    if (oldInfo.GetTargetPriority() != newInfo.GetTargetPriority()) {
        return ERR_BUNDLEMANAGER_OVERLAY_INSTALLATION_FAILED_TARGET_PRIORITY_NOT_SAME;
    }
#endif
    if (oldInfo.GetApplicationBundleType() != newInfo.GetApplicationBundleType()) {
        return ERR_APPEXECFWK_BUNDLE_TYPE_NOT_SAME;
    }

    LOG_D(BMS_TAG_INSTALLER, "CheckAppLabel end");
    return ERR_OK;
}

bool BaseBundleInstaller::CheckReleaseTypeIsCompatible(
    const InnerBundleInfo &oldInfo, const InnerBundleInfo &newInfo) const
{
    if (oldInfo.GetReleaseType() != newInfo.GetReleaseType()) {
        LOG_W(BMS_TAG_INSTALLER, "the releaseType not same: [%{public}s, %{public}s] vs [%{public}s, %{public}s]",
            oldInfo.GetCurModuleName().c_str(), oldInfo.GetReleaseType().c_str(),
            newInfo.GetCurModuleName().c_str(), newInfo.GetReleaseType().c_str());
    }
    return true;
}

ErrCode BaseBundleInstaller::RemoveBundleUserData(
    InnerBundleInfo &innerBundleInfo, bool isKeepData, const bool async)
{
    auto bundleName = innerBundleInfo.GetBundleName();
    LOG_D(BMS_TAG_INSTALLER, "remove user(%{public}d) in bundle(%{public}s)", userId_, bundleName.c_str());
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_NULL_PTR;
    }
    if (!innerBundleInfo.HasInnerBundleUserInfo(userId_)) {
        return ERR_APPEXECFWK_USER_NOT_EXIST;
    }

    // delete accessTokenId
    accessTokenId_ = innerBundleInfo.GetAccessTokenId(userId_);
    isKeepTokenId_ = innerBundleInfo.HasKeepTokenIdMetadata();
    if (!isKeepData) {
        bool keepTokenParam = isKeepTokenId_ && async;
        LOG_NOFUNC_I(BMS_TAG_INSTALLER, "DeleteAccessTokenId keepTokenParam=%{public}d", keepTokenParam);
        if (BundlePermissionMgr::DeleteAccessTokenId(accessTokenId_, keepTokenParam) !=
            AccessToken::AccessTokenKitRet::RET_SUCCESS) {
            LOG_E(BMS_TAG_INSTALLER, "delete accessToken failed");
        }
    }
    if (innerBundleInfo.GetApplicationBundleType() == BundleType::ATOMIC_SERVICE) {
        int32_t uid = innerBundleInfo.GetUid(userId_);
        if (uid != Constants::INVALID_UID) {
            LOG_I(BMS_TAG_INSTALLER, "uninstall atomic service need delete quota, bundleName:%{public}s",
                innerBundleInfo.GetBundleName().c_str());
            std::string bundleDataDir = ServiceConstants::BUNDLE_APP_DATA_BASE_DIR + ServiceConstants::BUNDLE_EL[1] +
                ServiceConstants::PATH_SEPARATOR + std::to_string(userId_) + ServiceConstants::BASE +
                innerBundleInfo.GetBundleName();
            PrepareBundleDirQuota(innerBundleInfo.GetBundleName(), uid, bundleDataDir, 0);
        }
    }

    innerBundleInfo.RemoveInnerBundleUserInfo(userId_);
    if (!dataMgr_->RemoveInnerBundleUserInfo(bundleName, userId_)) {
        LOG_E(BMS_TAG_INSTALLER, "update bundle user info to db failed %{public}s when remove user",
            bundleName.c_str());
        return ERR_APPEXECFWK_RMV_USERINFO_ERROR;
    }

    ErrCode result = ERR_OK;
    if (!isKeepData) {
        result = RemoveBundleDataDir(innerBundleInfo, false, async);
        if (result != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "remove user data directory failed");
        }
    }

    result = DeleteArkProfile(bundleName, userId_);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to removeArkProfile, error is %{public}d", result);
    }

    if ((result = CleanAsanDirectory(innerBundleInfo)) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to remove asan log path, error is %{public}d", result);
    }

    if (!isKeepData) {
        result = dataMgr_->DeleteDesktopShortcutInfo(bundleName, userId_, 0);
        if (result != ERR_OK) {
            LOG_W(BMS_TAG_INSTALLER, "fail to delete shortcut info");
        }
        EventReport::SendDesktopShortcutEvent(DesktopShortcutOperation::DELETE, userId_, bundleName,
            0, Constants::EMPTY_STRING, IPCSkeleton::GetCallingUid(), result);
    }

    return ERR_OK;
}

ErrCode BaseBundleInstaller::CheckInstallationFree(const InnerBundleInfo &innerBundleInfo,
    const std::unordered_map<std::string, InnerBundleInfo> &infos) const
{
    for (const auto &item : infos) {
        if (innerBundleInfo.GetEntryInstallationFree() != item.second.GetEntryInstallationFree()) {
            LOG_E(BMS_TAG_INSTALLER, "CheckInstallationFree cannot install application and hm service simultaneously");
            return ERR_APPEXECFWK_INSTALL_TYPE_ERROR;
        }
    }
    return ERR_OK;
}

void BaseBundleInstaller::SaveHapPathToRecords(
    bool isPreInstallApp, const std::unordered_map<std::string, InnerBundleInfo> &infos)
{
    if (isPreInstallApp) {
        LOG_D(BMS_TAG_INSTALLER, "PreInstallApp do not need to save hap path to record");
        return;
    }

    for (const auto &item : infos) {
        auto hapPathIter = hapPathRecords_.find(item.first);
        if (hapPathIter == hapPathRecords_.end()) {
            std::string tempDir = GetTempHapPath(item.second);
            if (tempDir.empty()) {
                LOG_W(BMS_TAG_INSTALLER, "get temp hap path failed");
                continue;
            }
            LOG_D(BMS_TAG_INSTALLER, "tempDir is %{public}s", tempDir.c_str());
            hapPathRecords_.emplace(item.first, tempDir);
        }

        std::string signatureFileDir = "";
        FindSignatureFileDir(item.second.GetCurModuleName(), signatureFileDir);
        auto signatureFileIter = signatureFileMap_.find(item.first);
        if (signatureFileIter == signatureFileMap_.end()) {
            signatureFileMap_.emplace(item.first, signatureFileDir);
        }
    }
}

ErrCode BaseBundleInstaller::SaveHapToInstallPath(const std::unordered_map<std::string, InnerBundleInfo> &infos,
    const InnerBundleInfo &oldInfo)
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    // size of code signature files should be same with the size of hap and hsp
    if (!signatureFileMap_.empty() && (signatureFileMap_.size() != hapPathRecords_.size())) {
        LOG_E(BMS_TAG_INSTALLER, "code signature file size not same with the size of hap and hsp");
        return ERR_BUNDLEMANAGER_INSTALL_CODE_SIGNATURE_FILE_IS_INVALID;
    }
    // 1. copy hsp or hap file to temp installation dir
    ErrCode result = ERR_OK;
    LOG_I(BMS_TAG_INSTALLER, "codesign start");
    for (const auto &hapPathRecord : hapPathRecords_) {
        LOG_D(BMS_TAG_INSTALLER, "Save from %{public}s to %{public}s",
            hapPathRecord.first.c_str(), hapPathRecord.second.c_str());
        if ((signatureFileMap_.find(hapPathRecord.first) != signatureFileMap_.end()) &&
            (!signatureFileMap_.at(hapPathRecord.first).empty())) {
            result = InstalldClient::GetInstance()->CopyFile(hapPathRecord.first, hapPathRecord.second,
                signatureFileMap_.at(hapPathRecord.first));
            CHECK_RESULT(result, "Copy hap to install path failed or code signature hap failed %{public}d");
        } else {
            result = InstalldClient::GetInstance()->MoveHapToCodeDir(hapPathRecord.first, hapPathRecord.second);
            if (result == ERR_APPEXECFWK_INSTALL_FILE_PATH_INVALID) {
                LOG_W(BMS_TAG_INSTALLER, "rename hap failed, due to hap not existed");
                return result;
            }
            if (result != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLER, "Move hap to install path failed");
                return ERR_APPEXECFWK_INSTALLD_MOVE_FILE_FAILED;
            }
            result = VerifyCodeSignatureForHap(infos, hapPathRecord.first, hapPathRecord.second);
            if (result != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLER, "enable code signature failed: %{public}d", result);
                return result;
            }
        }
    }
    LOG_I(BMS_TAG_INSTALLER, "codesign end");

    // 2. check encryption of hap
    if ((result = CheckHapEncryption(infos, oldInfo)) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "check encryption of hap failed %{public}d", result);
        return result;
    }

    // move file from temp dir to real installation dir, see FinalProcessHapAndSoForBundleUpdate
    return ERR_OK;
}

void BaseBundleInstaller::ResetInstallProperties()
{
    bundleInstallChecker_->ResetProperties();
    isContainEntry_ = false;
    isAppExist_ = false;
    hasInstalledInUser_ = false;
    isFeatureNeedUninstall_ = false;
    versionCode_ = 0;
    uninstallModuleVec_.clear();
    installedModules_.clear();
    state_ = InstallerState::INSTALL_START;
    singletonState_ = SingletonState::DEFAULT;
    accessTokenId_ = 0;
    sysEventInfo_.Reset();
    moduleName_.clear();
    verifyCodeParams_.clear();
    pgoParams_.clear();
    otaInstall_ = false;
    signatureFileMap_.clear();
    hapPathRecords_.clear();
    uninstallBundleAppId_.clear();
    isModuleUpdate_ = false;
    isEntryInstalled_ = false;
    isHnpInstalled_ = false;
    entryModuleName_.clear();
    isEnterpriseBundle_ = false;
    isInternaltestingBundle_ = false;
    appIdentifier_.clear();
    targetSoPathMap_.clear();
    isAppService_ = false;
    oldApplicationReservedFlag_ = 0;
    newExtensionDirs_.clear();
    createExtensionDirs_.clear();
    removeExtensionDirs_.clear();
    existBeforeKeepDataApp_ = false;
    needSetDisposeRule_ = false;
    needDeleteAppTempPath_ = false;
    isPreBundleRecovered_ = false;
    callerToken_ = 0;
    isBundleExist_ = false;
    isBundleCrossAppSharedConfig_ = false;
    isKeepTokenId_ = false;
    appDistributionType_ = Constants::APP_DISTRIBUTION_TYPE_NONE;
}

void BaseBundleInstaller::OnSingletonChange(bool killProcess)
{
    if (singletonState_ == SingletonState::DEFAULT) {
        return;
    }
    if (!InitDataMgr()) {
        return;
    }
    // need enable bundle when return
    ScopeGuard enableGuard([&] {
        dataMgr_->EnableBundle(bundleName_);
    });

    InnerBundleInfo info;
    bool isExist = false;
    if (!FetchInnerBundleInfo(info, isExist) || !isExist) {
        LOG_E(BMS_TAG_INSTALLER, "Get innerBundleInfo failed when singleton changed");
        return;
    }
    dataMgr_->DisableBundle(bundleName_);
    InstallParam installParam;
    installParam.needSendEvent = false;
    installParam.SetForceExecuted(true);
    installParam.SetKillProcess(killProcess);
    if (singletonState_ == SingletonState::SINGLETON_TO_NON) {
        LOG_I(BMS_TAG_INSTALLER, "Bundle changes from singleton app to non singleton app");
        installParam.userId = Constants::DEFAULT_USERID;
        UninstallBundle(bundleName_, installParam);
        return;
    }

    if (singletonState_ == SingletonState::NON_TO_SINGLETON) {
        LOG_I(BMS_TAG_INSTALLER, "Bundle changes from non singleton app to singleton app");
        for (const auto &infoItem : info.GetInnerBundleUserInfos()) {
            int32_t installedUserId = infoItem.second.bundleUserInfo.userId;
            if (installedUserId == Constants::DEFAULT_USERID) {
                continue;
            }

            installParam.userId = installedUserId;
            UninstallBundle(bundleName_, installParam);
        }
    }
}

void BaseBundleInstaller::RestoreHaps(const std::vector<std::string> &bundlePaths, const InstallParam &installParam)
{
    if (!installParam.IsRenameInstall() || bundlePaths_.empty() || bundlePaths.empty()) {
        LOG_D(BMS_TAG_INSTALLER, "No need to restore haps");
        return;
    }
    const std::string newPrefix = std::string(ServiceConstants::BUNDLE_MANAGER_SERVICE_PATH) +
        ServiceConstants::GALLERY_DOWNLOAD_PATH + std::to_string(userId_) + ServiceConstants::PATH_SEPARATOR;
    std::string targetDir = bundlePaths.front().substr(0, bundlePaths.front().find_last_of('/') + 1);
    if (bundlePaths.front().find(APP_INSTALL_SANDBOX_PATH) == 0) {
        targetDir = newPrefix + targetDir.substr(std::strlen(APP_INSTALL_SANDBOX_PATH));
    } else {
        LOG_W(BMS_TAG_INSTALLER, "Invalid bundle path: %{public}s", bundlePaths.front().c_str());
        return;
    }

    for (const auto &originPath : bundlePaths_) {
        std::string targetPath = targetDir + originPath.substr(originPath.find_last_of('/') + 1);
        LOG_I(BMS_TAG_INSTALLER, "Restore hap: %{public}s -> %{public}s", originPath.c_str(), targetPath.c_str());
        if (!BundleUtil::RenameFile(originPath, targetPath)) {
            LOG_W(BMS_TAG_INSTALLER, "failed: %{public}s -> %{public}s", originPath.c_str(), targetPath.c_str());
        }
    }
}

void BaseBundleInstaller::SendBundleSystemEvent(const std::string &bundleName, BundleEventType bundleEventType,
    const InstallParam &installParam, InstallScene preBundleScene, ErrCode errCode)
{
    if (std::find(ServiceConstants::EXPECTED_ERROR.begin(), ServiceConstants::EXPECTED_ERROR.end(), errCode) !=
        ServiceConstants::EXPECTED_ERROR.end()) {
        APP_LOGD("No need report for -e:%{public}d", errCode);
        return;
    }
    sysEventInfo_.bundleName = bundleName;
    sysEventInfo_.isPreInstallApp = installParam.isPreInstallApp;
    sysEventInfo_.errCode = errCode;
    sysEventInfo_.isFreeInstallMode = (installParam.installFlag == InstallFlag::FREE_INSTALL);
    sysEventInfo_.userId = userId_;
    sysEventInfo_.versionCode = versionCode_;
    sysEventInfo_.preBundleScene = preBundleScene;
    sysEventInfo_.isPatch = installParam.isPatch;
    sysEventInfo_.isKeepData = installParam.isKeepData;
    sysEventInfo_.endTime = BundleUtil::GetCurrentTimeMs();
    GetCallingEventInfo(sysEventInfo_);
    if (InitDataMgr()) {
        dataMgr_->GetOdidByBundleName(bundleName, sysEventInfo_.odid);
        sysEventInfo_.applicationInfoSize = static_cast<int32_t>(dataMgr_->GetAllBundleName().size());
    }
    EventReport::SendBundleSystemEvent(bundleEventType, sysEventInfo_);
}

void BaseBundleInstaller::GetCallingEventInfo(EventInfo &eventInfo)
{
    LOG_D(BMS_TAG_INSTALLER, "GetCallingEventInfo start, bundleName:%{public}s", eventInfo.callingBundleName.c_str());
    if (!InitDataMgr()) {
        LOG_E(BMS_TAG_INSTALLER, "Get dataMgr shared_ptr nullptr");
        return;
    }
    if (!dataMgr_->GetBundleNameForUid(eventInfo.callingUid, eventInfo.callingBundleName)) {
        LOG_D(BMS_TAG_INSTALLER, "CallingUid %{public}d is not hap, no bundleName", eventInfo.callingUid);
        eventInfo.callingBundleName = Constants::EMPTY_STRING;
        return;
    }
    BundleInfo bundleInfo;
    if (!dataMgr_->GetBundleInfo(eventInfo.callingBundleName, BundleFlag::GET_BUNDLE_DEFAULT, bundleInfo,
        eventInfo.callingUid / Constants::BASE_USER_RANGE)) {
        LOG_E(BMS_TAG_INSTALLER, "GetBundleInfo failed, bundleName: %{public}s", eventInfo.callingBundleName.c_str());
        return;
    }
    eventInfo.callingAppId = bundleInfo.appId;
}

void BaseBundleInstaller::GetInstallEventInfo(EventInfo &eventInfo)
{
    LOG_D(BMS_TAG_INSTALLER, "GetInstallEventInfo start, bundleName:%{public}s", bundleName_.c_str());
    if (!InitDataMgr()) {
        LOG_E(BMS_TAG_INSTALLER, "Get dataMgr shared_ptr nullptr");
        return;
    }
    InnerBundleInfo info;
    if (!dataMgr_->FetchInnerBundleInfo(bundleName_, info)) {
        LOG_E(BMS_TAG_INSTALLER, "Get innerBundleInfo failed, bundleName: %{public}s", bundleName_.c_str());
        return;
    }
    GetInstallEventInfo(info, eventInfo);
}

void BaseBundleInstaller::GetInstallEventInfo(const InnerBundleInfo &bundleInfo, EventInfo &eventInfo)
{
    LOG_D(BMS_TAG_INSTALLER, "GetInstallEventInfo start, bundleName:%{public}s", bundleInfo.GetBundleName().c_str());
    eventInfo.fingerprint = bundleInfo.GetCertificateFingerprint();
    eventInfo.appDistributionType = bundleInfo.GetAppDistributionType();
    eventInfo.hideDesktopIcon = bundleInfo.IsHideDesktopIconForEvent();
    eventInfo.timeStamp = bundleInfo.GetBundleUpdateTime(userId_);
    // report hapPath and hashValue
    for (const auto &innerModuleInfo : bundleInfo.GetInnerModuleInfos()) {
        eventInfo.filePath.push_back(innerModuleInfo.second.hapPath);
        eventInfo.hashValue.push_back(innerModuleInfo.second.hashValue);
    }
}

void BaseBundleInstaller::SetCallingUid(int32_t callingUid)
{
    sysEventInfo_.callingUid = callingUid;
}

void BaseBundleInstaller::SetCallingTokenId(const Security::AccessToken::AccessTokenID callerToken)
{
    callerToken_ = callerToken;
}

ErrCode BaseBundleInstaller::NotifyBundleStatus(const NotifyBundleEvents &installRes)
{
    std::shared_ptr<BundleCommonEventMgr> commonEventMgr = std::make_shared<BundleCommonEventMgr>();
    commonEventMgr->NotifyBundleStatus(installRes, dataMgr_);
    return ERR_OK;
}

void BaseBundleInstaller::AddBundleStatus(const NotifyBundleEvents &installRes)
{
    bundleEvents_.emplace_back(installRes);
}

bool BaseBundleInstaller::NotifyAllBundleStatus()
{
    if (bundleEvents_.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "bundleEvents is empty");
        return false;
    }

    auto dataMgr = DelayedSingleton<BundleMgrService>::GetInstance()->GetDataMgr();
    if (!dataMgr) {
        LOG_E(BMS_TAG_INSTALLER, "Get dataMgr shared_ptr nullptr");
        return false;
    }

    std::shared_ptr<BundleCommonEventMgr> commonEventMgr = std::make_shared<BundleCommonEventMgr>();
    for (const auto &bundleEvent : bundleEvents_) {
        commonEventMgr->NotifyBundleStatus(bundleEvent, dataMgr);
    }
    return true;
}

void BaseBundleInstaller::AddNotifyBundleEvents(const NotifyBundleEvents &notifyBundleEvents)
{
    auto userMgr = DelayedSingleton<BundleMgrService>::GetInstance()->GetBundleUserMgr();
    if (userMgr == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "userMgr is null");
        return;
    }

    userMgr->AddNotifyBundleEvents(notifyBundleEvents);
}

ErrCode BaseBundleInstaller::CheckOverlayInstallation(std::unordered_map<std::string, InnerBundleInfo> &newInfos,
    int32_t userId)
{
#ifdef BUNDLE_FRAMEWORK_OVERLAY_INSTALLATION
    std::shared_ptr<BundleOverlayInstallChecker> overlayChecker = std::make_shared<BundleOverlayInstallChecker>();
    return overlayChecker->CheckOverlayInstallation(newInfos, userId, overlayType_);
#else
    LOG_D(BMS_TAG_INSTALLER, "overlay is not supported");
    return ERR_OK;
#endif
}

ErrCode BaseBundleInstaller::CheckOverlayUpdate(const InnerBundleInfo &oldInfo, const InnerBundleInfo &newInfo,
    int32_t userId) const
{
#ifdef BUNDLE_FRAMEWORK_OVERLAY_INSTALLATION
    std::shared_ptr<BundleOverlayInstallChecker> overlayChecker = std::make_shared<BundleOverlayInstallChecker>();
    return overlayChecker->CheckOverlayUpdate(oldInfo, newInfo, userId);
#else
    LOG_D(BMS_TAG_INSTALLER, "overlay is not supported");
    return ERR_OK;
#endif
}

NotifyType BaseBundleInstaller::GetNotifyType()
{
    if (isAppExist_ && hasInstalledInUser_) {
        if (overlayType_ != NON_OVERLAY_TYPE) {
            return NotifyType::OVERLAY_UPDATE;
        }
        return NotifyType::UPDATE;
    }

    if (overlayType_ != NON_OVERLAY_TYPE) {
        return NotifyType::OVERLAY_INSTALL;
    }
    return NotifyType::INSTALL;
}

ErrCode BaseBundleInstaller::CheckArkProfileDir(const InnerBundleInfo &newInfo, const InnerBundleInfo &oldInfo) const
{
    if (newInfo.GetVersionCode() != oldInfo.GetVersionCode()) {
        const auto userInfos = oldInfo.GetInnerBundleUserInfos();
        for (auto iter = userInfos.begin(); iter != userInfos.end(); iter++) {
            int32_t userId = iter->second.bundleUserInfo.userId;
            int32_t gid = (newInfo.GetAppProvisionType() == Constants::APP_PROVISION_TYPE_DEBUG) ?
                GetIntParameter(BMS_KEY_SHELL_UID, ServiceConstants::SHELL_UID) :
                oldInfo.GetUid(userId);
            ErrCode result = newInfo.GetIsNewVersion() ?
                CreateArkProfile(bundleName_, userId, oldInfo.GetUid(userId), gid) :
                DeleteArkProfile(bundleName_, userId);
            if (result != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLER, "bundleName: %{public}s CheckArkProfileDir failed, result:%{public}d",
                    bundleName_.c_str(), result);
                return result;
            }
        }
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::ProcessAsanDirectory(InnerBundleInfo &info) const
{
    if (dataMgr_ == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "dataMgr_ is nullptr");
        return ERR_APPEXECFWK_NULL_PTR;
    }
    const std::string bundleName = info.GetBundleName();
    const std::string asanLogDir = std::string(ServiceConstants::BUNDLE_ASAN_LOG_DIR) + ServiceConstants::PATH_SEPARATOR
        + std::to_string(userId_) + ServiceConstants::PATH_SEPARATOR + bundleName
        + ServiceConstants::PATH_SEPARATOR + LOG;
    bool dirExist = false;
    ErrCode errCode = InstalldClient::GetInstance()->IsExistDir(asanLogDir, dirExist);
    if (errCode != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "check asan log directory failed");
        return errCode;
    }
    bool asanEnabled = info.GetAsanEnabled();
    // create asan log directory if asanEnabled is true
    if (asanEnabled) {
        InnerBundleUserInfo newInnerBundleUserInfo;
        if (!info.GetInnerBundleUserInfo(userId_, newInnerBundleUserInfo)) {
            LOG_E(BMS_TAG_INSTALLER, "bundle(%{public}s) get user(%{public}d) failed",
                info.GetBundleName().c_str(), userId_);
            return ERR_APPEXECFWK_USER_NOT_EXIST;
        }

        errCode = dataMgr_->GenerateUidAndGid(newInnerBundleUserInfo);
        if (errCode != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "fail to generate uid and gid");
            return errCode;
        }
        BundleUtil::MakeFsConfig(info.GetBundleName(), ServiceConstants::HMDFS_CONFIG_PATH, info.GetAppProvisionType(),
            Constants::APP_PROVISION_TYPE_FILE_NAME);
        mode_t mode = S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH;
        if ((errCode = InstalldClient::GetInstance()->Mkdir(asanLogDir, mode,
            newInnerBundleUserInfo.uid, newInnerBundleUserInfo.uid)) != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "create asan log directory failed");
            return errCode;
        }
    }
    if (asanEnabled) {
        info.SetAsanLogPath(LOG);
    }
    // clean asan directory
    if (dirExist && !asanEnabled) {
        if ((errCode = CleanAsanDirectory(info)) != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "clean asan log directory failed");
            return errCode;
        }
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CleanAsanDirectory(InnerBundleInfo &info) const
{
    const std::string bundleName = info.GetBundleName();
    const std::string asanLogDir = std::string(ServiceConstants::BUNDLE_ASAN_LOG_DIR) + ServiceConstants::PATH_SEPARATOR
        + std::to_string(userId_) + ServiceConstants::PATH_SEPARATOR + bundleName;
    ErrCode errCode =  InstalldClient::GetInstance()->RemoveDir(asanLogDir);
    if (errCode != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "clean asan log path failed");
        return errCode;
    }
    info.SetAsanLogPath("");
    return errCode;
}

void BaseBundleInstaller::AddAppProvisionInfo(const std::string &bundleName,
    const Security::Verify::ProvisionInfo &provisionInfo,
    const InstallParam &installParam) const
{
    AppProvisionInfo appProvisionInfo = bundleInstallChecker_->ConvertToAppProvisionInfo(provisionInfo);
    if (!DelayedSingleton<AppProvisionInfoManager>::GetInstance()->AddAppProvisionInfo(
        bundleName, appProvisionInfo)) {
        LOG_W(BMS_TAG_INSTALLER, "bundleName: %{public}s add appProvisionInfo failed", bundleName.c_str());
    }
    if (!installParam.specifiedDistributionType.empty()) {
        if (!DelayedSingleton<AppProvisionInfoManager>::GetInstance()->SetSpecifiedDistributionType(
            bundleName, installParam.specifiedDistributionType)) {
            LOG_W(BMS_TAG_INSTALLER, "bundleName: %{public}s SetSpecifiedDistributionType failed", bundleName.c_str());
        }
    }
    if (!installParam.additionalInfo.empty()) {
        if (!DelayedSingleton<AppProvisionInfoManager>::GetInstance()->SetAdditionalInfo(
            bundleName, installParam.additionalInfo)) {
            LOG_W(BMS_TAG_INSTALLER, "bundleName: %{public}s SetAdditionalInfo failed", bundleName.c_str());
        }
    }
}

ErrCode BaseBundleInstaller::InnerProcessNativeLibs(InnerBundleInfo &info, const std::string &modulePath)
{
    std::string targetSoPath;
    std::string cpuAbi;
    std::string nativeLibraryPath;
    bool isCompressNativeLibrary = info.IsCompressNativeLibs(info.GetCurModuleName());
    if (info.FetchNativeSoAttrs(modulePackage_, cpuAbi, nativeLibraryPath)) {
        if (isCompressNativeLibrary) {
            InnerProcessTargetSoPath(info, isFeatureNeedUninstall_, modulePath, nativeLibraryPath, targetSoPath);
            // for code signature
            targetSoPathMap_.emplace(info.GetCurModuleName(), targetSoPath);
        }
    }

    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "extract module %{public}s targetSo %{public}s abi:%{public}s compress %{public}d",
        modulePath.c_str(), targetSoPath.c_str(), cpuAbi.c_str(), isCompressNativeLibrary);
    std::string signatureFileDir = "";
    auto ret = FindSignatureFileDir(info.GetCurModuleName(), signatureFileDir);
    if (ret != ERR_OK) {
        return ret;
    }
    if (isCompressNativeLibrary) {
        auto result = ExtractModuleFiles(info, modulePath, targetSoPath, cpuAbi);
        CHECK_RESULT(result, "fail to extract module dir, error is %{public}d");
        // verify hap or hsp code signature for compressed so files
        result = VerifyCodeSignatureForNativeFiles(info, cpuAbi, targetSoPath, signatureFileDir);
        CHECK_RESULT(result, "fail to VerifyCodeSignature, error is %{public}d");
        // check whether the hap or hsp is encrypted
        result = CheckSoEncryption(info, cpuAbi, targetSoPath);
        CHECK_RESULT(result, "fail to CheckSoEncryption, error is %{public}d");
    } else {
        auto result = InstalldClient::GetInstance()->CreateBundleDir(modulePath);
        CHECK_RESULT(result, "fail to create temp bundle dir, error is %{public}d");
        std::vector<std::string> fileNames;
        result = InstalldClient::GetInstance()->GetNativeLibraryFileNames(modulePath_, cpuAbi, fileNames);
        CHECK_RESULT(result, "fail to GetNativeLibraryFileNames, error is %{public}d");
        info.SetNativeLibraryFileNames(modulePackage_, fileNames);
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::VerifyCodeSignatureForNativeFiles(InnerBundleInfo &info, const std::string &cpuAbi,
    const std::string &targetSoPath, const std::string &signatureFileDir) const
{
    if (copyHapToInstallPath_) {
        LOG_D(BMS_TAG_INSTALLER, "hap will be copied to install path, verified code signature later");
        return ERR_OK;
    }
    LOG_D(BMS_TAG_INSTALLER, "begin to verify code signature for native files");
    const std::string compileSdkType = info.GetBaseApplicationInfo().compileSdkType;
    CodeSignatureParam codeSignatureParam;
    codeSignatureParam.modulePath = modulePath_;
    codeSignatureParam.cpuAbi = cpuAbi;
    codeSignatureParam.targetSoPath = targetSoPath;
    codeSignatureParam.signatureFileDir = signatureFileDir;
    codeSignatureParam.isEnterpriseBundle = isEnterpriseBundle_;
    codeSignatureParam.isInternaltestingBundle = isInternaltestingBundle_;
    codeSignatureParam.appIdentifier = appIdentifier_;
    codeSignatureParam.isPreInstalledBundle = IsDataPreloadHap(modulePath_) ? false : info.IsPreInstallApp();
    codeSignatureParam.isCompileSdkOpenHarmony = (compileSdkType == COMPILE_SDK_TYPE_OPEN_HARMONY);
    bundleInstallChecker_->ProcessCodeSignatureParam(verifyRes_, codeSignatureParam);
    return InstalldClient::GetInstance()->VerifyCodeSignature(codeSignatureParam);
}

ErrCode BaseBundleInstaller::VerifyCodeSignatureForHap(const std::unordered_map<std::string, InnerBundleInfo> &infos,
    const std::string &srcHapPath, const std::string &realHapPath)
{
    LOG_D(BMS_TAG_INSTALLER, "begin to verify code signature for hap or internal hsp");
    auto iter = infos.find(srcHapPath);
    if (iter == infos.end()) {
        return ERR_OK;
    }
    const auto &info = iter->second;
    std::string moduleName = info.GetCurModuleName();
    std::string cpuAbi;
    std::string nativeLibraryPath;
    info.FetchNativeSoAttrs(info.GetCurrentModulePackage(), cpuAbi, nativeLibraryPath);
    const std::string compileSdkType = info.GetBaseApplicationInfo().compileSdkType;
    std::string signatureFileDir = "";
    auto ret = FindSignatureFileDir(moduleName, signatureFileDir);
    if (ret != ERR_OK) {
        return ret;
    }
    auto targetSoPath = targetSoPathMap_.find(moduleName);
    CodeSignatureParam codeSignatureParam;
    if (targetSoPath != targetSoPathMap_.end()) {
        codeSignatureParam.targetSoPath = targetSoPath->second;
    }
    codeSignatureParam.cpuAbi = cpuAbi;
    codeSignatureParam.modulePath = realHapPath;
    codeSignatureParam.signatureFileDir = signatureFileDir;
    codeSignatureParam.isEnterpriseBundle = isEnterpriseBundle_;
    codeSignatureParam.isInternaltestingBundle = isInternaltestingBundle_;
    codeSignatureParam.appIdentifier = appIdentifier_;
    codeSignatureParam.isCompileSdkOpenHarmony = (compileSdkType == COMPILE_SDK_TYPE_OPEN_HARMONY);
    codeSignatureParam.isPreInstalledBundle = IsDataPreloadHap(realHapPath) ? false : info.IsPreInstallApp();
    codeSignatureParam.isCompressNativeLibrary = info.IsCompressNativeLibs(info.GetCurModuleName());
    bundleInstallChecker_->ProcessCodeSignatureParam(verifyRes_, codeSignatureParam);
    return InstalldClient::GetInstance()->VerifyCodeSignatureForHap(codeSignatureParam);
}

ErrCode BaseBundleInstaller::CheckSoEncryption(InnerBundleInfo &info, const std::string &cpuAbi,
    const std::string &targetSoPath)
{
    LOG_D(BMS_TAG_INSTALLER, "begin to check so encryption");
    CheckEncryptionParam param;
    param.modulePath = modulePath_;
    param.cpuAbi = cpuAbi;
    param.targetSoPath = targetSoPath;
    int uid = info.GetUid(userId_);
    param.bundleId = uid - userId_ * Constants::BASE_USER_RANGE;
    param.isCompressNativeLibrary = info.IsCompressNativeLibs(info.GetCurModuleName());
    param.appIdentifier = info.GetAppIdentifier();
    param.versionCode = info.GetVersionCode();
    if (info.GetModuleTypeByPackage(modulePackage_) == Profile::MODULE_TYPE_SHARED) {
        param.installBundleType = InstallBundleType::INTER_APP_HSP;
    }
    bool isEncrypted = false;
    ErrCode result = InstalldClient::GetInstance()->CheckEncryption(param, isEncrypted);
    CHECK_RESULT(result, "fail to CheckSoEncryption, error is %{public}d");
    if ((info.GetBaseApplicationInfo().debug || (info.GetAppProvisionType() == Constants::APP_PROVISION_TYPE_DEBUG))
        && isEncrypted) {
        LOG_E(BMS_TAG_INSTALLER, "-n %{public}s debug encrypted bundle is not allowed to install",
            info.GetBundleName().c_str());
        return ERR_APPEXECFWK_INSTALL_DEBUG_ENCRYPTED_BUNDLE_FAILED;
    }
    if (isEncrypted && IsShellOrDevAssistant()) {
        LOG_E(BMS_TAG_INSTALLER, "-n %{public}s encrypted bundle is not allowed for shell",
            info.GetBundleName().c_str());
        return ERR_APPEXECFWK_INSTALL_ENCRYPTED_BUNDLE_NOT_ALLOWED_FOR_SHELL;
    }
    if (isEncrypted) {
        LOG_D(BMS_TAG_INSTALLER, "module %{public}s is encrypted", modulePath_.c_str());
        info.SetApplicationReservedFlag(static_cast<uint32_t>(ApplicationReservedFlag::ENCRYPTED_APPLICATION));
    }
    return ERR_OK;
}

void BaseBundleInstaller::ProcessOldNativeLibraryPath(const std::unordered_map<std::string, InnerBundleInfo> &newInfos,
    uint32_t oldVersionCode, const std::string &oldNativeLibraryPath) const
{
    if (isFeatureNeedUninstall_) {
        return;
    }
    if (((oldVersionCode >= versionCode_) && !otaInstall_) || oldNativeLibraryPath.empty()) {
        return;
    }
    for (const auto &item : newInfos) {
        const auto &moduleInfos = item.second.GetInnerModuleInfos();
        for (const auto &moduleItem: moduleInfos) {
            if (moduleItem.second.compressNativeLibs) {
                // no need to delete library path
                return;
            }
        }
    }
    std::string oldLibPath = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR + bundleName_ +
        ServiceConstants::PATH_SEPARATOR + ServiceConstants::LIBS;
    if (InstalldClient::GetInstance()->RemoveDir(oldLibPath) != ERR_OK) {
        LOG_W(BMS_TAG_INSTALLER, "bundleNmae: %{public}s remove old libs dir failed", bundleName_.c_str());
    }
}

void BaseBundleInstaller::ProcessAOT(const InstallParam &installParam) const
{
    if (installParam.isFirstBootInstall) {
        LOG_D(BMS_TAG_INSTALLER, "is first boot install, no need to AOT");
        return;
    }
    if (installParam.isOTA || otaInstall_) {
        LOG_D(BMS_TAG_INSTALLER, "is OTA, no need to AOT");
        return;
    }
    if (installParam.isCreateUser) {
        LOG_D(BMS_TAG_INSTALLER, "is create user, no need to AOT");
        return;
    }
    AOTHandler::GetInstance().HandleInstallAOTAsync(bundleName_);
}

void BaseBundleInstaller::RemoveOldHapIfOTA(const InstallParam &installParam,
    const std::unordered_map<std::string, InnerBundleInfo> &newInfos, const InnerBundleInfo &oldInfo)
{
    if (!installParam.isOTA || installParam.copyHapToInstallPath) {
        return;
    }
    InnerBundleInfo newInfo;
    if (!GetTempBundleInfo(newInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "get failed for %{public}s", bundleName_.c_str());
        return;
    }
    for (const auto &info : newInfos) {
        std::string oldHapPath = oldInfo.GetModuleHapPath(info.second.GetCurrentModulePackage());
        std::string newHapPath = newInfo.GetModuleHapPath(info.second.GetCurrentModulePackage());
        if (oldHapPath == newHapPath || oldHapPath.find(Constants::BUNDLE_CODE_DIR) == std::string::npos) {
            continue;
        }
        LOG_NOFUNC_W(BMS_TAG_INSTALLER, "remove old hap %{public}s", oldHapPath.c_str());
        if (InstalldClient::GetInstance()->RemoveDir(oldHapPath) != ERR_OK) {
            LOG_W(BMS_TAG_INSTALLER, "remove old hap failed, errno: %{public}d", errno);
        }
    }
}

ErrCode BaseBundleInstaller::CopyHapsToSecurityDir(const InstallParam &installParam,
    std::vector<std::string> &bundlePaths)
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    if (!installParam.withCopyHaps) {
        LOG_D(BMS_TAG_INSTALLER, "no need to copy preInstallApp to secure dir");
        return ERR_OK;
    }
    for (size_t index = 0; index < bundlePaths.size(); ++index) {
        if (!BundleUtil::CheckSystemSize(bundlePaths[index], APP_INSTALL_PATH)) {
            LOG_E(BMS_TAG_INSTALLER, "install %{private}s failed insufficient disk memory", bundlePaths[index].c_str());
            return ERR_APPEXECFWK_INSTALL_DISK_MEM_INSUFFICIENT;
        }
        auto destination = BundleUtil::CopyFileToSecurityDir(bundlePaths[index], DirType::STREAM_INSTALL_DIR,
            toDeleteTempHapPath_, installParam.IsRenameInstall());
        if (destination.empty()) {
            LOG_E(BMS_TAG_INSTALLER, "copy file %{private}s to security dir failed", bundlePaths[index].c_str());
            return ERR_APPEXECFWK_INSTALL_COPY_HAP_FAILED;
        }
        bundlePaths[index] = destination;
    }
    bundlePaths_ = bundlePaths;
    return ERR_OK;
}

ErrCode BaseBundleInstaller::ParseHapPaths(const InstallParam &installParam,
    const std::vector<std::string> &inBundlePaths, std::vector<std::string> &parsedPaths)
{
    parsedPaths.reserve(inBundlePaths.size());
    if (!installParam.IsRenameInstall()) {
        parsedPaths.assign(inBundlePaths.begin(), inBundlePaths.end());
        return ERR_OK;
    }
    LOG_I(BMS_TAG_INSTALLER, "rename install");
    int32_t userId = userId_ < Constants::START_USERID ?
        sysEventInfo_.callingUid / Constants::BASE_USER_RANGE : userId_;
    const std::string newPrefix = std::string(ServiceConstants::BUNDLE_MANAGER_SERVICE_PATH) +
        ServiceConstants::GALLERY_DOWNLOAD_PATH + std::to_string(userId) + ServiceConstants::PATH_SEPARATOR;

    for (const auto &bundlePath : inBundlePaths) {
        if (bundlePath.find("..") != std::string::npos) {
            LOG_E(BMS_TAG_INSTALLER, "path invalid: %{public}s", bundlePath.c_str());
            return ERR_APPEXECFWK_INSTALL_FILE_PATH_INVALID;
        }
        if (bundlePath.find(APP_INSTALL_SANDBOX_PATH) == 0) {
            std::string newPath = newPrefix + bundlePath.substr(std::strlen(APP_INSTALL_SANDBOX_PATH));
            parsedPaths.push_back(newPath);
            LOG_D(BMS_TAG_INSTALLER, "parsed path: %{public}s", newPath.c_str());
        } else {
            LOG_E(BMS_TAG_INSTALLER, "path invalid: %{public}s", bundlePath.c_str());
            return ERR_APPEXECFWK_INSTALL_RENAME_INSTALL_FILE_PATH_NOT_START_WITH_APP_INSTALL_SANDBOX;
        }
    }
    if (!parsedPaths.empty()) {
        std::filesystem::path hapPath(parsedPaths.front());
        std::string bundleNameDir = hapPath.parent_path().string();
        FileStat fileStat;
        ErrCode res = InstalldClient::GetInstance()->GetFileStat(bundleNameDir, fileStat);
        int32_t sharedMode = S_IRWXU | S_IRWXG | S_ISGID;
        if (res == ERR_OK && ((static_cast<uint32_t>(fileStat.mode) & ServiceConstants::MODE_BASE) != sharedMode
            || fileStat.gid != ServiceConstants::APP_INSTALL_GID)) {
            LOG_W(BMS_TAG_INSTALLER, "shared dir mode is not correct %{public}d for %{public}s",
                fileStat.mode, bundleNameDir.c_str());
            fileStat.gid = ServiceConstants::APP_INSTALL_GID;
            fileStat.mode = sharedMode;
            InstalldClient::GetInstance()->ChangeFileStat(bundleNameDir, fileStat);
            fileStat.mode = S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH | S_IWOTH;
            for (const auto &path : parsedPaths) {
                InstalldClient::GetInstance()->ChangeFileStat(path, fileStat);
            }
        }
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::RenameAllTempDir(const std::unordered_map<std::string, InnerBundleInfo> &newInfos) const
{
    LOG_D(BMS_TAG_INSTALLER, "begin to rename all temp dir");
    ErrCode ret = ERR_OK;
    for (const auto &info : newInfos) {
        if (info.second.IsOnlyCreateBundleUser()) {
            continue;
        }
        if (info.second.GetCurModuleName() == MODULE_NAME_IS_LIBS) {
            LOG_W(BMS_TAG_INSTALLER, "no rename libs module dir again");
            continue;
        }
        if ((ret = RenameModuleDir(info.second)) != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "rename dir failed");
            break;
        }
    }
    RemoveEmptyDirs(newInfos);
    return ret;
}

ErrCode BaseBundleInstaller::FindSignatureFileDir(const std::string &moduleName, std::string &signatureFileDir)
{
    LOG_D(BMS_TAG_INSTALLER, "begin to find code signature file of moudle %{public}s", moduleName.c_str());
    if (verifyCodeParams_.empty()) {
        signatureFileDir = "";
        LOG_D(BMS_TAG_INSTALLER, "verifyCodeParams_ is empty and no need to verify code signature of module %{public}s",
            moduleName.c_str());
        return ERR_OK;
    }
    if (signatureFileTmpMap_.find(moduleName) != signatureFileTmpMap_.end()) {
        signatureFileDir = signatureFileTmpMap_.at(moduleName);
        LOG_D(BMS_TAG_INSTALLER, "signature file of %{public}s is existed in temp map", moduleName.c_str());
        return ERR_OK;
    }
    auto iterator = verifyCodeParams_.find(moduleName);
    if (iterator == verifyCodeParams_.end()) {
        LOG_E(BMS_TAG_INSTALLER, "no signature file dir exist of module %{public}s", moduleName.c_str());
        return ERR_BUNDLEMANAGER_INSTALL_CODE_SIGNATURE_FAILED;
    }
    signatureFileDir = verifyCodeParams_.at(moduleName);

    // check validity of the signature file
    auto ret = bundleInstallChecker_->CheckSignatureFileDir(signatureFileDir);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "checkout signature file dir %{private}s failed", signatureFileDir.c_str());
        return ret;
    }

    // copy code signature file to security dir
    std::string destinationStr =
        BundleUtil::CopyFileToSecurityDir(signatureFileDir, DirType::SIG_FILE_DIR, toDeleteTempHapPath_);
    if (destinationStr.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "copy file %{private}s to security dir failed", signatureFileDir.c_str());
        return ERR_APPEXECFWK_INSTALL_COPY_HAP_FAILED;
    }
    if (signatureFileDir.find(ServiceConstants::SIGNATURE_FILE_PATH) != std::string::npos) {
        BundleUtil::DeleteDir(signatureFileDir);
    }
    signatureFileDir = destinationStr;
    signatureFileTmpMap_.emplace(moduleName, destinationStr);
    LOG_D(BMS_TAG_INSTALLER, "signatureFileDir is %{public}s", signatureFileDir.c_str());
    return ERR_OK;
}

std::string BaseBundleInstaller::GetTempHapPath(const InnerBundleInfo &info)
{
    std::string hapPath = GetHapPath(info);
    if (hapPath.empty() || (!BundleUtil::EndWith(hapPath, ServiceConstants::INSTALL_FILE_SUFFIX) &&
        !BundleUtil::EndWith(hapPath, ServiceConstants::HSP_FILE_SUFFIX))) {
        LOG_E(BMS_TAG_INSTALLER, "invalid hapPath %{public}s", hapPath.c_str());
        return "";
    }
    auto posOfPathSep = hapPath.rfind(ServiceConstants::PATH_SEPARATOR);
    if (posOfPathSep == std::string::npos) {
        return "";
    }

    if (isFeatureNeedUninstall_) {
        return std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR +
            std::string(ServiceConstants::BUNDLE_NEW_CODE_DIR) + info.GetBundleName() +
            hapPath.substr(posOfPathSep);
    }
    std::string tempDir = hapPath.substr(0, posOfPathSep + 1) + info.GetCurrentModulePackage();
    if (installedModules_[info.GetCurrentModulePackage()]) {
        tempDir += ServiceConstants::TMP_SUFFIX;
    }

    return tempDir.append(hapPath.substr(posOfPathSep));
}

ErrCode BaseBundleInstaller::CheckHapEncryption(const std::unordered_map<std::string, InnerBundleInfo> &infos,
    const InnerBundleInfo &oldInfo, bool copyHapToInstallPath)
{
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "CheckHapEncryption begin");
    InnerBundleInfo newInfo;
    if (!GetTempBundleInfo(newInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "Get innerBundleInfo failed, bundleName: %{public}s", bundleName_.c_str());
        return ERR_APPEXECFWK_GET_INSTALL_TEMP_BUNDLE_ERROR;
    }
    for (const auto &info : infos) {
        std::string hapPath;
        if (!copyHapToInstallPath) {
            hapPath = info.first;
        } else {
            if (hapPathRecords_.find(info.first) == hapPathRecords_.end()) {
                LOG_E(BMS_TAG_INSTALLER, "path %{public}s cannot be found in hapPathRecord", info.first.c_str());
                return ERR_APPEXECFWK_HAP_ENCRYPTION_CHECK_ERROR;
            }
            hapPath = hapPathRecords_.at(info.first);
        }
        CheckEncryptionParam param;
        param.modulePath = hapPath;
        int uid = info.second.GetUid(userId_);
        param.bundleId = uid - userId_ * Constants::BASE_USER_RANGE;
        param.isCompressNativeLibrary = info.second.IsCompressNativeLibs(info.second.GetCurModuleName());
        param.appIdentifier = info.second.GetAppIdentifier();
        param.versionCode = versionCode_;
        if (info.second.GetModuleTypeByPackage(modulePackage_) == Profile::MODULE_TYPE_SHARED) {
            param.installBundleType = InstallBundleType::INTER_APP_HSP;
        }
        bool isEncrypted = false;
        ErrCode result = InstalldClient::GetInstance()->CheckEncryption(param, isEncrypted);
        if (copyHapToInstallPath) {
            CHECK_RESULT(result, "fail to CheckHapEncryption, error is %{public}d");
        }
        if ((info.second.GetBaseApplicationInfo().debug ||
            (info.second.GetAppProvisionType() == Constants::APP_PROVISION_TYPE_DEBUG)) && isEncrypted) {
            LOG_E(BMS_TAG_INSTALLER, "-n %{public}s debug encrypted bundle is not allowed to install",
                bundleName_.c_str());
            return ERR_APPEXECFWK_INSTALL_DEBUG_ENCRYPTED_BUNDLE_FAILED;
        }
        newInfo.SetMoudleIsEncrpted(info.second.GetCurrentModulePackage(), isEncrypted);
    }
    UpdateEncryptionStatus(infos, oldInfo, newInfo);
    if (!tempInfo_.SetTempBundleInfo(newInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "save UpdateInnerBundleInfo failed");
        return ERR_APPEXECFWK_SET_INSTALL_TEMP_BUNDLE_ERROR;
    }
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "CheckHapEncryption end");
    return ERR_OK;
}

void BaseBundleInstaller::UpdateEncryptionStatus(const std::unordered_map<std::string, InnerBundleInfo> &infos,
    const InnerBundleInfo &oldInfo, InnerBundleInfo &newInfo)
{
    if (IsBundleEncrypted(infos, oldInfo, newInfo)) {
        LOG_I(BMS_TAG_INSTALLER, "%{public}s is encrypted", newInfo.GetBundleName().c_str());
        newInfo.SetApplicationReservedFlag(static_cast<uint32_t>(ApplicationReservedFlag::ENCRYPTED_APPLICATION));
    } else {
        LOG_D(BMS_TAG_INSTALLER, "application does not contain encrypted module");
        newInfo.ClearApplicationReservedFlag(static_cast<uint32_t>(ApplicationReservedFlag::ENCRYPTED_APPLICATION));
    }
}

bool BaseBundleInstaller::IsBundleEncrypted(const std::unordered_map<std::string, InnerBundleInfo> &infos,
    const InnerBundleInfo &oldInfo, const InnerBundleInfo &newInfo)
{
    // any of the new module is entryped, then the bundle is entryped
    for (const auto &info : infos) {
        if (newInfo.IsEncryptedMoudle(info.second.GetCurrentModulePackage())) {
            LOG_D(BMS_TAG_INSTALLER, "new installed module is encrypted");
            return true;
        }
    }
    // infos does not contain encrypted module
    // if upgrade, no need to check old bundle
    if (infos.empty() || versionCode_ != oldInfo.GetVersionCode()) {
        return false;
    }
    // if not upgrade and old bundle is not encrypted, the new bundle is alse not encrypted
    if (!oldInfo.IsContainEncryptedModule()) {
        return false;
    }
    // if old bundle is encrypted, check whether all encrypted old modules are updated
    std::vector<std::string> encryptedModuleNames;
    oldInfo.GetAllEncryptedModuleNames(encryptedModuleNames);
    for (const auto &moduleName : encryptedModuleNames) {
        bool moduleUpdated = false;
        for (const auto &info : infos) {
            if (moduleName == info.second.GetModuleName(info.second.GetCurrentModulePackage())) {
                moduleUpdated = true;
                break;
            }
        }
        if (!moduleUpdated) {
            LOG_I(BMS_TAG_INSTALLER, "%{public}s is encrypted and not updated", moduleName.c_str());
            return true;
        }
    }
    return false;
}

ErrCode BaseBundleInstaller::MoveFileToRealInstallationDir(
    const std::unordered_map<std::string, InnerBundleInfo> &infos)
{
    LOG_D(BMS_TAG_INSTALLER, "start to move file to real installation dir");
    for (const auto &info : infos) {
        if (hapPathRecords_.find(info.first) == hapPathRecords_.end()) {
            LOG_E(BMS_TAG_INSTALLER, "path %{public}s cannot be found in hapPathRecord", info.first.c_str());
            return ERR_APPEXECFWK_INSTALLD_MOVE_FILE_FAILED;
        }

        std::string realInstallationPath = GetHapPath(info.second);
        LOG_D(BMS_TAG_INSTALLER, "move hsp or hsp file from path %{public}s to path %{public}s",
            hapPathRecords_.at(info.first).c_str(), realInstallationPath.c_str());
        // 1. move hap or hsp to real installation dir
        auto result = InstalldClient::GetInstance()->MoveFile(hapPathRecords_.at(info.first), realInstallationPath);
        if (result != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "move file to real path failed %{public}d", result);
            return ERR_APPEXECFWK_INSTALLD_MOVE_FILE_FAILED;
        }
        FILE *hapFp = fopen(realInstallationPath.c_str(), "r");
        if (hapFp == nullptr) {
            LOG_E(BMS_TAG_INSTALLER, "fopen %{public}s failed, errno: %{public}d", realInstallationPath.c_str(), errno);
            return ERR_APPEXECFWK_INSTALLD_MOVE_FILE_FAILED;
        }
        int32_t hapFd = fileno(hapFp);
        if (hapFd < 0) {
            LOG_E(BMS_TAG_INSTALLER, "open %{public}s failed, errno: %{public}d", realInstallationPath.c_str(), errno);
            (void)fclose(hapFp);
            return ERR_APPEXECFWK_INSTALLD_MOVE_FILE_FAILED;
        }
        if (fsync(hapFd) != 0) {
            LOG_E(BMS_TAG_INSTALLER, "fsync %{public}s failed, errno: %{public}d", realInstallationPath.c_str(), errno);
            (void)fclose(hapFp);
            return ERR_APPEXECFWK_INSTALLD_MOVE_FILE_FAILED;
        }
        (void)fclose(hapFp);
    }
    return ERR_OK;
}

std::string BaseBundleInstaller::GetRealSoPath(const std::string &bundleName,
    const std::string &nativeLibraryPath, bool isNeedDeleteOldPath) const
{
    std::string realSoDir;
    if (isNeedDeleteOldPath) {
        realSoDir.append(Constants::BUNDLE_CODE_DIR).append(ServiceConstants::PATH_SEPARATOR)
            .append(bundleName).append(ServiceConstants::PATH_SEPARATOR)
            .append(LIBS_TMP);
    } else {
        realSoDir.append(Constants::BUNDLE_CODE_DIR).append(ServiceConstants::PATH_SEPARATOR)
            .append(bundleName).append(ServiceConstants::PATH_SEPARATOR)
            .append(nativeLibraryPath);
    }
    return realSoDir;
}

ErrCode BaseBundleInstaller::FinalProcessHapAndSoForBundleUpdate(
    const std::unordered_map<std::string, InnerBundleInfo> &infos,
    bool needCopyHapToInstallPath, bool needDeleteOldLibraryPath)
{
    if (infos.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "infos are empty");
        return ERR_OK;
    }
    std::string bundleName = infos.begin()->second.GetBundleName();
    ErrCode result = ERR_OK;
    if (needCopyHapToInstallPath) {
        // move hap file from temp dir to real installation dir
        LOG_I(BMS_TAG_INSTALLER, "-n %{public}s start to move file", bundleName.c_str());
        result = MoveFileToRealInstallationDir(infos);
        if (result != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "-n %{public}s move file failed %{public}d", bundleName.c_str(), result);
            return result;
        }
    }
    if (needDeleteOldLibraryPath) {
        // move so file from temp dir to real installation dir
        LOG_NOFUNC_I(BMS_TAG_INSTALLER, "-n %{public}s process so start", bundleName.c_str());
        std::string nativeLibraryPath = "";
        bool hasModuleNameIsLibs = false;
        for (const auto &info : infos) {
            if (info.second.GetCurModuleName() == MODULE_NAME_IS_LIBS) {
                hasModuleNameIsLibs = true;
            }
            if (info.second.GetNativeLibraryPath().empty() ||
                info.second.IsLibIsolated(info.second.GetCurModuleName()) ||
                !info.second.IsCompressNativeLibs(info.second.GetCurModuleName())) {
                continue;
            }
            // multi-hap application nativeLiraryPath is same, CheckMultiNativeSo
            nativeLibraryPath = info.second.GetNativeLibraryPath();
            break;
        }
        // delete old native library path
        DeleteOldNativeLibraryPath();
        // if has libs modulename, must rename module dir before rename so dir
        if (hasModuleNameIsLibs) {
            std::string libsModuleDir = std::string(Constants::BUNDLE_CODE_DIR) +
                ServiceConstants::PATH_SEPARATOR + bundleName + MODULE_DIR_IS_LIBS;
            std::string renameDir = libsModuleDir + ServiceConstants::TMP_SUFFIX;
            result = InstalldClient::GetInstance()->RenameModuleDir(renameDir, libsModuleDir);
            if (result != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLER, "rename libs module dir failed, error is %{public}d", result);
                return result;
            }
        }
        if (!nativeLibraryPath.empty()) {
            std::string realSoDir = GetRealSoPath(bundleName, nativeLibraryPath, false);
            InstalldClient::GetInstance()->CreateBundleDir(realSoDir);
            std::string tempSoDir = GetRealSoPath(bundleName, nativeLibraryPath, true);
            result = InstalldClient::GetInstance()->RenameModuleDir(tempSoDir, realSoDir);
            if (result != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLER, "-n %{public}s rename module dir failed, error is %{public}d",
                    bundleName.c_str(), result);
                return result;
            }
        }
        LOG_NOFUNC_I(BMS_TAG_INSTALLER, "-n %{public}s process so end", bundleName.c_str());
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::MoveSoFileToRealInstallationDir(
    const std::unordered_map<std::string, InnerBundleInfo> &infos, bool needDeleteOldLibraryPath,
    bool isBundleUpdate)
{
    LOG_D(BMS_TAG_INSTALLER, "start to move so file to real installation dir");
    if (infos.empty()) {
        LOG_NOFUNC_I(BMS_TAG_INSTALLER, "no hap info to process");
        return ERR_OK;
    }
    std::string bundleName = infos.begin()->second.GetBundleName();
    std::string bundleCodePathName = isBundleUpdate ?
        (std::string(ServiceConstants::BUNDLE_NEW_CODE_DIR) + bundleName) : bundleName;
    if (needDeleteOldLibraryPath) {
        // first delete libs+tmp dir, make sure is empty
        std::string tempSoDir;
        tempSoDir.append(Constants::BUNDLE_CODE_DIR).append(ServiceConstants::PATH_SEPARATOR)
            .append(bundleCodePathName).append(ServiceConstants::PATH_SEPARATOR).append(LIBS_TMP);
        InstalldClient::GetInstance()->RemoveDir(tempSoDir);
    }
    for (const auto &info : infos) {
        if (info.second.IsLibIsolated(info.second.GetCurModuleName()) ||
            !info.second.IsCompressNativeLibs(info.second.GetCurModuleName())) {
            LOG_NOFUNC_I(BMS_TAG_INSTALLER, "-n %{public}s so isolated or decompressed no necessary to move",
                bundleName.c_str());
            continue;
        }
        std::string cpuAbi = "";
        std::string nativeLibraryPath = "";
        bool isSoExisted = info.second.FetchNativeSoAttrs(info.second.GetCurrentModulePackage(), cpuAbi,
            nativeLibraryPath);
        if (isSoExisted) {
            std::string tempSoDir;
            tempSoDir.append(Constants::BUNDLE_CODE_DIR).append(ServiceConstants::PATH_SEPARATOR)
                .append(bundleCodePathName).append(ServiceConstants::PATH_SEPARATOR)
                .append(info.second.GetCurrentModulePackage())
                .append(ServiceConstants::TMP_SUFFIX).append(ServiceConstants::PATH_SEPARATOR)
                .append(nativeLibraryPath);
            bool isDirExisted = BundleUtil::IsExistDirNoLog(tempSoDir);
            if (!isDirExisted) {
                LOG_NOFUNC_W(BMS_TAG_INSTALLER, "%{public}s not existed not need move", tempSoDir.c_str());
                continue;
            }
            std::string realSoDir = GetRealSoPath(bundleCodePathName, nativeLibraryPath, needDeleteOldLibraryPath);
            LOG_D(BMS_TAG_INSTALLER, "move file from %{public}s to %{public}s", tempSoDir.c_str(), realSoDir.c_str());
            isDirExisted = BundleUtil::IsExistDirNoLog(realSoDir);
            if (!isDirExisted) {
                LOG_NOFUNC_W(BMS_TAG_INSTALLER, "%{public}s not existed", realSoDir.c_str());
                InstalldClient::GetInstance()->CreateBundleDir(realSoDir);
            }
            auto result = InstalldClient::GetInstance()->MoveFiles(tempSoDir, realSoDir);
            if (result != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLER, "move file to real path failed %{public}d", result);
                return ERR_APPEXECFWK_INSTALLD_MOVE_FILE_FAILED;
            }
            RemoveTempSoDir(tempSoDir);
            if (!installedModules_[info.second.GetCurrentModulePackage()]) {
                RemoveTempPathOnlyUsedForSo(info.second);
            }
        }
    }
    return ERR_OK;
}

void BaseBundleInstaller::UpdateAppInstallControlled(int32_t userId)
{
#ifdef BUNDLE_FRAMEWORK_APP_CONTROL
    if (!DelayedSingleton<AppControlManager>::GetInstance()->IsAppInstallControlEnabled()) {
        LOG_D(BMS_TAG_INSTALLER, "app control feature is disabled");
        return;
    }

    if (bundleName_.empty() || dataMgr_ == nullptr) {
        LOG_W(BMS_TAG_INSTALLER, "invalid bundleName_ or dataMgr is nullptr");
        return;
    }
    if (!isAppExist_) {
        LOG_W(BMS_TAG_INSTALLER, "bundle %{public}s is not existed", bundleName_.c_str());
        return;
    }

    InnerBundleInfo info;
    tempInfo_.GetTempBundleInfo(info);
    InnerBundleUserInfo userInfo;
    if (!info.GetInnerBundleUserInfo(userId, userInfo)) {
        LOG_W(BMS_TAG_INSTALLER, "current bundle (%{public}s) is not installed at current userId (%{public}d)",
            bundleName_.c_str(), userId);
        return;
    }

    std::string currentAppId = info.GetAppId();
    std::string currentAppIdentifier = info.GetAppIdentifier();
    std::vector<std::string> appIds;
    ErrCode ret = DelayedSingleton<AppControlManager>::GetInstance()->GetAppInstallControlRule(
        AppControlConstants::EDM_CALLING, AppControlConstants::APP_DISALLOWED_UNINSTALL, userId, appIds);
    bool needUpdateInfo = false;
    if (ret == ERR_OK) {
        if ((std::find(appIds.begin(), appIds.end(), currentAppId) != appIds.end()) ||
            (std::find(appIds.begin(), appIds.end(), currentAppIdentifier) != appIds.end())) {
            needUpdateInfo = true;
        }
    }
    if (needUpdateInfo) {
        LOG_W(BMS_TAG_INSTALLER, "bundle %{public}s cannot be removed", bundleName_.c_str());
        userInfo.isRemovable = false;
        info.AddInnerBundleUserInfo(userInfo);
        info.SetBundleStatus(InnerBundleInfo::BundleStatus::ENABLED);
        tempInfo_.SetTempBundleInfo(info);
    }
#else
    LOG_W(BMS_TAG_INSTALLER, "app control is disable");
#endif
}

void BaseBundleInstaller::UpdateHasCloudkitConfig()
{
    InnerBundleInfo info;
    tempInfo_.GetTempBundleInfo(info);
    info.UpdateHasCloudkitConfig();
    tempInfo_.SetTempBundleInfo(info);
}

ErrCode BaseBundleInstaller::UninstallBundleFromBmsExtension(const std::string &bundleName)
{
    LOG_D(BMS_TAG_INSTALLER, "start to uninstall bundle from bms extension");
    if (!DelayedSingleton<BundleMgrService>::GetInstance()->IsBrokerServiceStarted()) {
        LOG_W(BMS_TAG_INSTALLER, "broker is not started");
        return ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_BUNDLE;
    }
    BmsExtensionDataMgr bmsExtensionDataMgr;
    auto ret = bmsExtensionDataMgr.Uninstall(bundleName);
    if (ret == ERR_OK) {
        LOG_D(BMS_TAG_INSTALLER, "uninstall bundle(%{public}s) from bms extension successfully", bundleName.c_str());
        return ERR_OK;
    }
    if ((ret == ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_BUNDLE) ||
        (ret == ERR_BUNDLE_MANAGER_INSTALL_FAILED_BUNDLE_EXTENSION_NOT_EXISTED)) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall failed due to bundle(%{public}s is not existed)", bundleName.c_str());
        return ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_BUNDLE;
    }
    LOG_E(BMS_TAG_INSTALLER, "uninstall bundle(%{public}s) from bms extension faile due to errcode %{public}d",
        bundleName.c_str(), ret);
    return ret;
}

ErrCode BaseBundleInstaller::CheckBundleInBmsExtension(const std::string &bundleName, int32_t userId)
{
    LOG_D(BMS_TAG_INSTALLER, "start to check bundle(%{public}s) from bms extension", bundleName.c_str());
    if (!DelayedSingleton<BundleMgrService>::GetInstance()->IsBrokerServiceStarted()) {
        LOG_NOFUNC_W(BMS_TAG_INSTALLER, "broker is not started");
        return ERR_OK;
    }
    BmsExtensionDataMgr bmsExtensionDataMgr;
    BundleInfo extensionBundleInfo;
    auto ret = bmsExtensionDataMgr.GetBundleInfo(bundleName, BundleFlag::GET_BUNDLE_DEFAULT, userId,
        extensionBundleInfo);
    if (ret == ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "the bundle(%{public}s) is already existed in the bms extension", bundleName.c_str());
        return ERR_APPEXECFWK_INSTALL_ALREADY_EXIST;
    }
    return ERR_OK;
}

void BaseBundleInstaller::RemoveTempSoDir(const std::string &tempSoDir)
{
    LOG_D(BMS_TAG_INSTALLER, "tempSoDir is %{public}s", tempSoDir.c_str());
    auto firstPos = tempSoDir.find(ServiceConstants::TMP_SUFFIX);
    if (firstPos == std::string::npos) {
        LOG_W(BMS_TAG_INSTALLER, "invalid tempSoDir %{public}s", tempSoDir.c_str());
        return;
    }
    auto secondPos = tempSoDir.find(ServiceConstants::PATH_SEPARATOR, firstPos);
    if (secondPos == std::string::npos) {
        LOG_W(BMS_TAG_INSTALLER, "invalid tempSoDir %{public}s", tempSoDir.c_str());
        return;
    }
    auto thirdPos = tempSoDir.find(ServiceConstants::PATH_SEPARATOR, secondPos + 1);
    if (thirdPos == std::string::npos) {
        InstalldClient::GetInstance()->RemoveDir(tempSoDir);
        return;
    }
    std::string subTempSoDir = tempSoDir.substr(0, thirdPos);
    LOG_D(BMS_TAG_INSTALLER, "subTempSoDir %{public}s", subTempSoDir.c_str());
    InstalldClient::GetInstance()->RemoveDir(subTempSoDir);
}

ErrCode BaseBundleInstaller::InstallEntryMoudleFirst(std::unordered_map<std::string, InnerBundleInfo> &newInfos,
    InnerBundleInfo &bundleInfo, const InnerBundleUserInfo &innerBundleUserInfo, const InstallParam &installParam)
{
    LOG_D(BMS_TAG_INSTALLER, "start to install entry firstly");
    if (!isAppExist_ || isEntryInstalled_) {
        LOG_D(BMS_TAG_INSTALLER, "no need to install entry firstly");
        return ERR_OK;
    }
    ErrCode result = ERR_OK;
    for (auto &info : newInfos) {
        if (info.second.HasEntry()) {
            modulePath_ = info.first;
            InnerBundleInfo &newInfo = info.second;
            newInfo.AddInnerBundleUserInfo(innerBundleUserInfo);
            bool isReplace = (installParam.installFlag == InstallFlag::REPLACE_EXISTING ||
                installParam.installFlag == InstallFlag::FREE_INSTALL);
            // app exist, but module may not
            result = ProcessBundleUpdateStatus(bundleInfo, newInfo, isReplace, installParam.GetKillProcess());
            if (result == ERR_OK) {
                entryModuleName_ = info.second.GetCurrentModulePackage();
                LOG_D(BMS_TAG_INSTALLER, "entry packageName is %{public}s", entryModuleName_.c_str());
            }
            break;
        }
    }
    isEntryInstalled_ = true;
    return result;
}

ErrCode BaseBundleInstaller::DeliveryProfileToCodeSign() const
{
    LOG_D(BMS_TAG_INSTALLER, "start to delivery sign profile to code signature");
    Security::Verify::ProvisionInfo provisionInfo = verifyRes_.GetProvisionInfo();
    if (provisionInfo.profileBlockLength == 0 || provisionInfo.profileBlock == nullptr) {
        LOG_D(BMS_TAG_INSTALLER, "Emulator does not verify signature");
        return ERR_OK;
    }
    if (provisionInfo.distributionType == Security::Verify::AppDistType::ENTERPRISE ||
        provisionInfo.distributionType == Security::Verify::AppDistType::ENTERPRISE_NORMAL ||
        provisionInfo.distributionType == Security::Verify::AppDistType::ENTERPRISE_MDM ||
        provisionInfo.distributionType == Security::Verify::AppDistType::INTERNALTESTING ||
        provisionInfo.type == Security::Verify::ProvisionType::DEBUG) {
        return InstalldClient::GetInstance()->DeliverySignProfile(provisionInfo.bundleInfo.bundleName,
            provisionInfo.profileBlockLength, provisionInfo.profileBlock.get());
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::RemoveProfileFromCodeSign(const std::string &bundleName) const
{
    LOG_D(BMS_TAG_INSTALLER, "remove sign profile of bundle %{public}s from code signature", bundleName.c_str());
    return InstalldClient::GetInstance()->RemoveSignProfile(bundleName);
}

void BaseBundleInstaller::DeleteOldNativeLibraryPath() const
{
    std::string oldLibPath = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR + bundleName_ +
        ServiceConstants::PATH_SEPARATOR + ServiceConstants::LIBS;
    if (InstalldClient::GetInstance()->RemoveDir(oldLibPath) != ERR_OK) {
        LOG_W(BMS_TAG_INSTALLER, "bundleNmae: %{public}s remove old libs dir failed", bundleName_.c_str());
    }
}

void BaseBundleInstaller::RemoveTempPathOnlyUsedForSo(const InnerBundleInfo &innerBundleInfo,
    const bool isBundleUpdate) const
{
    LOG_D(BMS_TAG_INSTALLER, "start");
    std::string bundleCodePathName = isBundleUpdate ? (std::string(ServiceConstants::BUNDLE_NEW_CODE_DIR) +
        innerBundleInfo.GetBundleName()) : innerBundleInfo.GetBundleName();
    std::string tempDir;
    tempDir.append(Constants::BUNDLE_CODE_DIR).append(ServiceConstants::PATH_SEPARATOR)
        .append(bundleCodePathName).append(ServiceConstants::PATH_SEPARATOR)
        .append(innerBundleInfo.GetCurrentModulePackage())
        .append(ServiceConstants::TMP_SUFFIX);
    bool isDirEmpty = false;
    if (InstalldClient::GetInstance()->IsDirEmpty(tempDir, isDirEmpty) != ERR_OK) {
        LOG_W(BMS_TAG_INSTALLER, "IsDirEmpty failed");
    }
    if (isDirEmpty && (InstalldClient::GetInstance()->RemoveDir(tempDir) != ERR_OK)) {
        LOG_W(BMS_TAG_INSTALLER, "remove tmp so path:%{public}s failed", tempDir.c_str());
    }
    LOG_D(BMS_TAG_INSTALLER, "end");
}

bool BaseBundleInstaller::NeedDeleteOldNativeLib(
    const std::unordered_map<std::string, InnerBundleInfo> &newInfos,
    const InnerBundleInfo &oldInfo)
{
    if (newInfos.empty()) {
        LOG_D(BMS_TAG_INSTALLER, "NewInfos is null");
        return false;
    }

    if (!isAppExist_) {
        LOG_D(BMS_TAG_INSTALLER, "No old app");
        return false;
    }

    if (oldInfo.GetNativeLibraryPath().empty()) {
        LOG_D(BMS_TAG_INSTALLER, "Old app no library");
        return false;
    }

    if ((versionCode_ != oldInfo.GetVersionCode())) {
        LOG_D(BMS_TAG_INSTALLER, "versionCode not same");
        return true;
    }

    if (oldInfo.GetApplicationBundleType() == BundleType::APP_SERVICE_FWK) {
        LOG_D(BMS_TAG_INSTALLER, "Appservice not delete library");
        return false;
    }

    for (const auto &info : newInfos) {
        if (info.second.IsOnlyCreateBundleUser()) {
            LOG_D(BMS_TAG_INSTALLER, "Some hap no update module");
            return false;
        }
    }

    return otaInstall_ || HasAllOldModuleUpdate(oldInfo, newInfos);
}

ErrCode BaseBundleInstaller::UpdateHapToken(bool needUpdate, InnerBundleInfo &newInfo, const bool isDebugGrant)
{
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "UpdateHapToken %{public}s start, needUpdate:%{public}d",
        bundleName_.c_str(), needUpdate);
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_NULL_PTR;
    }
    auto bundleUserInfos = newInfo.GetInnerBundleUserInfos();
    for (const auto &uerInfo : bundleUserInfos) {
        if (uerInfo.second.accessTokenId == 0) {
            continue;
        }
        int32_t userId = uerInfo.second.bundleUserInfo.userId;
        Security::AccessToken::AccessTokenIDEx accessTokenIdEx;
        accessTokenIdEx.tokenIDEx = uerInfo.second.accessTokenIdEx;
        Security::AccessToken::HapInfoCheckResult checkResult;
        if (BundlePermissionMgr::UpdateHapToken(accessTokenIdEx, newInfo, userId, checkResult,
            verifyRes_.GetProvisionInfo().appServiceCapabilities, false, isDebugGrant) != ERR_OK) {
            LOG_NOFUNC_E(BMS_TAG_INSTALLER, "UpdateHapToken failed %{public}s", bundleName_.c_str());
            SetVerifyPermissionResult(checkResult);
            return ERR_APPEXECFWK_INSTALL_GRANT_REQUEST_PERMISSIONS_FAILED;
        }
        if (needUpdate) {
            newInfo.SetAccessTokenIdEx(accessTokenIdEx, uerInfo.second.bundleUserInfo.userId);
        }

        const std::map<std::string, InnerBundleCloneInfo> &cloneInfos = uerInfo.second.cloneInfos;
        for (const auto &cloneInfoPair : cloneInfos) {
            Security::AccessToken::AccessTokenIDEx cloneAccessTokenIdEx;
            cloneAccessTokenIdEx.tokenIDEx = cloneInfoPair.second.accessTokenIdEx;
            Security::AccessToken::HapInfoCheckResult checkResult;
            if (BundlePermissionMgr::UpdateHapToken(cloneAccessTokenIdEx, newInfo, userId, checkResult,
                verifyRes_.GetProvisionInfo().appServiceCapabilities) != ERR_OK) {
                LOG_NOFUNC_E(BMS_TAG_INSTALLER, "UpdateHapToken failed %{public}s", bundleName_.c_str());
                SetVerifyPermissionResult(checkResult);
                return ERR_APPEXECFWK_INSTALL_GRANT_REQUEST_PERMISSIONS_FAILED;
            }
            if (needUpdate) {
                newInfo.SetAccessTokenIdExWithAppIndex(cloneAccessTokenIdEx,
                    uerInfo.second.bundleUserInfo.userId, cloneInfoPair.second.appIndex);
            }
        }
    }
    if (needUpdate && !tempInfo_.SetTempBundleInfo(newInfo)) {
        LOG_NOFUNC_E(BMS_TAG_INSTALLER, "save UpdateInnerBundleInfo failed %{public}s", bundleName_.c_str());
        return ERR_APPEXECFWK_SET_INSTALL_TEMP_BUNDLE_ERROR;
    }
    return ERR_OK;
}

#ifdef APP_DOMAIN_VERIFY_ENABLED
void BaseBundleInstaller::PrepareSkillUri(const std::vector<Skill> &skills,
    std::vector<AppDomainVerify::SkillUri> &skillUris) const
{
    for (const auto &skill : skills) {
        if (!skill.domainVerify) {
            continue;
        }
        for (const auto &uri : skill.uris) {
            if (uri.scheme != SKILL_URI_SCHEME_HTTPS) {
                continue;
            }
            AppDomainVerify::SkillUri skillUri;
            skillUri.scheme = uri.scheme;
            skillUri.host = uri.host;
            skillUri.port = uri.port;
            skillUri.path = uri.path;
            skillUri.pathStartWith = uri.pathStartWith;
            skillUri.pathRegex = uri.pathRegex;
            skillUri.type = uri.type;
            skillUris.push_back(skillUri);
        }
    }
}
#endif

void BaseBundleInstaller::UpdateDynamicSkills()
{
    InnerBundleInfo info;
    if (!GetTempBundleInfo(info)) {
        LOG_E(BMS_TAG_INSTALLER, "GetTempBundleInfo failed, bundleName: %{public}s", bundleName_.c_str());
        return;
    }
    info.UpdateDynamicSkills();
    (void)tempInfo_.SetTempBundleInfo(info);
}

void BaseBundleInstaller::VerifyDomain()
{
#ifdef APP_DOMAIN_VERIFY_ENABLED
    LOG_D(BMS_TAG_INSTALLER, "start to verify domain");
    InnerBundleInfo bundleInfo;
    if (!GetTempBundleInfo(bundleInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "Get innerBundleInfo failed, bundleName: %{public}s", bundleName_.c_str());
        return;
    }
    std::string appIdentifier = bundleInfo.GetAppIdentifier();
    if (isAppExist_) {
        LOG_NOFUNC_I(BMS_TAG_INSTALLER, "app exist, need to clear old domain info");
        ClearDomainVerifyStatus(appIdentifier, bundleName_);
    }
    std::vector<AppDomainVerify::SkillUri> skillUris;
    std::map<std::string, std::vector<Skill>> skillInfos = bundleInfo.GetInnerSkillInfos();
    for (const auto &skillInfo : skillInfos) {
        PrepareSkillUri(skillInfo.second, skillUris);
    }
    if (skillUris.empty()) {
        LOG_NOFUNC_I(BMS_TAG_INSTALLER, "no skill uri need to verify domain");
        return;
    }
    std::string fingerprint = bundleInfo.GetCertificateFingerprint();
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "start VerifyDomain, size: %{public}zu", skillUris.size());
    // call VerifyDomain
    std::string identity = IPCSkeleton::ResetCallingIdentity();
    DelayedSingleton<AppDomainVerify::AppDomainVerifyMgrClient>::GetInstance()->VerifyDomain(
        appIdentifier, bundleName_, fingerprint, skillUris);
    IPCSkeleton::SetCallingIdentity(identity);
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "end VerifyDomain");
#else
    LOG_I(BMS_TAG_INSTALLER, "app domain verify is disabled");
    return;
#endif
}

void BaseBundleInstaller::ClearDomainVerifyStatus(const std::string &appIdentifier,
    const std::string &bundleName) const
{
#ifdef APP_DOMAIN_VERIFY_ENABLED
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "start ClearDomainVerifyStatus");
    std::string identity = IPCSkeleton::ResetCallingIdentity();
    // call ClearDomainVerifyStatus
    if (!DelayedSingleton<AppDomainVerify::AppDomainVerifyMgrClient>::GetInstance()->ClearDomainVerifyStatus(
        appIdentifier, bundleName)) {
        LOG_W(BMS_TAG_INSTALLER, "ClearDomainVerifyStatus failed");
    }
    IPCSkeleton::SetCallingIdentity(identity);
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "end ClearDomainVerifyStatus");
#else
    LOG_I(BMS_TAG_INSTALLER, "app domain verify is disabled");
    return;
#endif
}

bool BaseBundleInstaller::IsRdDevice() const
{
    BmsExtensionDataMgr bmsExtensionDataMgr;
    bool res = bmsExtensionDataMgr.IsRdDevice();
    if (res) {
        LOG_I(BMS_TAG_INSTALLER, "current device is rd device");
        return true;
    }
    return false;
}

ArkStartupCache BaseBundleInstaller::CreateArkStartupCacheParameter(const std::string &bundleName,
    int32_t userId, BundleType bundleType, int32_t uid)
{
    std::string el1ArkStartupCachePath = ServiceConstants::SYSTEM_OPTIMIZE_PATH +
        bundleName + ServiceConstants::ARK_STARTUP_CACHE_DIR;
    el1ArkStartupCachePath = el1ArkStartupCachePath.replace(el1ArkStartupCachePath.find("%"), 1,
        std::to_string(userId));
    ArkStartupCache ceateArk;
    ceateArk.bundleName = bundleName;
    ceateArk.bundleType = bundleType;
    ceateArk.cacheDir = el1ArkStartupCachePath;
    ceateArk.mode = ServiceConstants::SYSTEM_OPTIMIZE_MODE;
    ceateArk.uid = uid;
    ceateArk.gid = uid;
    return ceateArk;
}

ErrCode BaseBundleInstaller::ProcessArkStartupCache(const ArkStartupCache &createArk,
    int32_t moduleNum, int32_t userId) const
{
    if (moduleNum != 1) {
        return DeleteArkStartupCache(ServiceConstants::SYSTEM_OPTIMIZE_PATH, createArk.bundleName, userId);
    }
    return CreateArkStartupCache(createArk);
}

ErrCode BaseBundleInstaller::CreateArkStartupCache(const ArkStartupCache &createArk) const
{
    if (createArk.bundleType != BundleType::APP && createArk.bundleType != BundleType::ATOMIC_SERVICE) {
        LOG_W(BMS_TAG_INSTALLER, "%{public}s is not app or atomic service, not allow create ark startup cache dir",
            createArk.bundleName.c_str());
        return ERR_APPEXECFWK_ARK_STARTUP_CACHE_ONLY_ALLOW_CREATE_APP_OR_ATOMIC;
    }

    std::unordered_set<std::string> startupBundles;
    BundleParser::ParseArkStartupCacheConfig(ServiceConstants::APP_STARTUP_CACHE_CONG, startupBundles);
    if (startupBundles.find(createArk.bundleName) == startupBundles.end()) {
        LOG_NOFUNC_W(BMS_TAG_INSTALLER, "%{public}s is not in startupBundles", createArk.bundleName.c_str());
        return ERR_APPEXECFWK_ARK_STARTUP_CACHE_ONLY_ALLOW_CREATE_IN_WHITE_LIST;
    }

    ErrCode result = InstalldClient::GetInstance()->Mkdir(createArk.cacheDir,
        createArk.mode, createArk.uid, createArk.gid);
    if (result != ERR_OK) {
        LOG_W(BMS_TAG_DEFAULT, "-n: %{public}s, Mkdir %{public}s failed, error:%{public}d",
            createArk.bundleName.c_str(), createArk.cacheDir.c_str(), errno);
        return result;
    }
    return InstalldClient::GetInstance()->SetArkStartupCacheApl(createArk.cacheDir);
}

ErrCode BaseBundleInstaller::DeleteArkStartupCache(const std::string &cacheDir,
    const std::string &bundleName, int32_t userId) const
{
    std::string el1ArkStartupCachePath = cacheDir + bundleName + ServiceConstants::ARK_STARTUP_CACHE_DIR;
    el1ArkStartupCachePath = el1ArkStartupCachePath.replace(el1ArkStartupCachePath.find("%"), 1,
        std::to_string(userId));
    return InstalldClient::GetInstance()->RemoveDir(el1ArkStartupCachePath);
}

void BaseBundleInstaller::SetVerifyPermissionResult(const Security::AccessToken::HapInfoCheckResult &checkResult)
{
    auto result = BundlePermissionMgr::GetCheckResultMsg(checkResult);
    SetCheckResultMsg(result);
    LOG_NOFUNC_E(BMS_TAG_INSTALLER, "%{public}s", result.c_str());
}

ErrCode BaseBundleInstaller::RollbackHmpUserInfo(const std::string &bundleName)
{
    LOG_I(BMS_TAG_INSTALLER, "RollbackHmpInstall %{public}s start", bundleName.c_str());
    if (bundleName.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "rollback hmp bundle name empty");
        return ERR_APPEXECFWK_UNINSTALL_INVALID_NAME;
    }
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_UNINSTALL_BUNDLE_MGR_SERVICE_ERROR;
    }
    auto &mtx = dataMgr_->GetBundleMutex(bundleName);
    std::lock_guard lock {mtx};
    InnerBundleInfo oldInfo;
    if (!dataMgr_->FetchInnerBundleInfo(bundleName, oldInfo)) {
        LOG_W(BMS_TAG_INSTALLER, "rollback hmp bundle info missing");
        return ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_BUNDLE;
    }
    std::set<int32_t> userIds;
    if (!dataMgr_->GetInnerBundleInfoUsers(bundleName, userIds)) {
        LOG_W(BMS_TAG_INSTALLER, "rollback hmp bundle users missing");
        return ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_BUNDLE;
    }
    for (auto userId : userIds) {
        if (oldInfo.GetApplicationBundleType() == BundleType::ATOMIC_SERVICE &&
            oldInfo.GetUid(userId) != Constants::INVALID_UID) {
            std::string bundleDataDir = ServiceConstants::BUNDLE_APP_DATA_BASE_DIR + ServiceConstants::BUNDLE_EL[1]
                + ServiceConstants::PATH_SEPARATOR + std::to_string(userId) + ServiceConstants::BASE +
                oldInfo.GetBundleName();
            PrepareBundleDirQuota(oldInfo.GetBundleName(), oldInfo.GetUid(userId), bundleDataDir, 0);
        }
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::RollbackHmpCommonInfo(const std::string &bundleName)
{
    if (bundleName.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "rollback hmp bundle name empty");
        return ERR_APPEXECFWK_UNINSTALL_INVALID_NAME;
    }
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_UNINSTALL_BUNDLE_MGR_SERVICE_ERROR;
    }
    auto &mtx = dataMgr_->GetBundleMutex(bundleName);
    std::lock_guard lock {mtx};
    InnerBundleInfo oldInfo;
    if (!dataMgr_->FetchInnerBundleInfo(bundleName, oldInfo)) {
        LOG_W(BMS_TAG_INSTALLER, "rollback hmp bundle info missing");
        return ERR_APPEXECFWK_UNINSTALL_MISSING_INSTALLED_BUNDLE;
    }
    if (!dataMgr_->UpdateBundleInstallState(oldInfo.GetBundleName(), InstallState::UNINSTALL_START)) {
        APP_LOGE("rollback hmp start uninstall failed");
        return ERR_APPEXECFWK_UNINSTALL_BUNDLE_MGR_SERVICE_ERROR;
    }
    if (!dataMgr_->UpdateBundleInstallState(oldInfo.GetBundleName(), InstallState::UNINSTALL_SUCCESS)) {
        LOG_E(BMS_TAG_INSTALLER, "rollback hmp delete inner info failed");
        return ERR_APPEXECFWK_UNINSTALL_BUNDLE_MGR_SERVICE_ERROR;
    }
    PreInstallBundleInfo preInfo;
    preInfo.SetBundleName(bundleName);
    if (!dataMgr_->DeletePreInstallBundleInfo(bundleName, preInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "rollback hmp delete pre install info failed");
    }
#ifdef BUNDLE_FRAMEWORK_QUICK_FIX
    std::shared_ptr<QuickFixDataMgr> quickFixDataMgr = DelayedSingleton<QuickFixDataMgr>::GetInstance();
    if (quickFixDataMgr != nullptr) {
        LOG_D(BMS_TAG_INSTALLER, "DeleteInnerAppQuickFix when bundleName :%{public}s uninstall",
            oldInfo.GetBundleName().c_str());
        quickFixDataMgr->DeleteInnerAppQuickFix(oldInfo.GetBundleName());
    }
#endif
    if (!DelayedSingleton<AppProvisionInfoManager>::GetInstance()->DeleteAppProvisionInfo(oldInfo.GetBundleName())) {
        LOG_W(BMS_TAG_INSTALLER, "bundleName: %{public}s delete appProvisionInfo failed",
            oldInfo.GetBundleName().c_str());
    }
    DeleteRouterInfo(oldInfo);
    BundleResourceHelper::DeleteBundleResourceInfo(oldInfo.GetBundleName(), userId_, false);
    RemoveProfileFromCodeSign(oldInfo.GetBundleName());
    ClearDomainVerifyStatus(oldInfo.GetAppIdentifier(), oldInfo.GetBundleName());
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CreateShaderCache(const std::string &bundleName, int32_t uid, int32_t gid) const
{
    std::string shaderCachePath;
    shaderCachePath.append(ServiceConstants::SHADER_CACHE_PATH).append(bundleName);
    LOG_D(BMS_TAG_INSTALLER, "CreateShaderCache %{public}s", shaderCachePath.c_str());
    return InstalldClient::GetInstance()->Mkdir(shaderCachePath, S_IRWXU, uid, gid);
}

ErrCode BaseBundleInstaller::DeleteShaderCache(const std::string &bundleName) const
{
    std::string shaderCachePath;
    shaderCachePath.append(ServiceConstants::SHADER_CACHE_PATH).append(bundleName);
    LOG_D(BMS_TAG_INSTALLER, "DeleteShaderCache %{public}s", shaderCachePath.c_str());
    return InstalldClient::GetInstance()->RemoveDir(shaderCachePath);
}

void BaseBundleInstaller::DeleteUseLessSharefilesForDefaultUser(const std::string &bundleName,
    int32_t userId) const
{
    if (userId == Constants::DEFAULT_USERID) {
        LOG_D(BMS_TAG_INSTALLD, "bundle %{public}s delete useless sharefiles for userid:%{public}d",
            bundleName.c_str(), userId);
        std::set<int32_t> currentUserIds = dataMgr_->GetAllUser();
        for (int32_t user : currentUserIds) {
            std::string dataDir = ServiceConstants::BUNDLE_APP_DATA_BASE_DIR + ServiceConstants::BUNDLE_EL[1] +
                ServiceConstants::PATH_SEPARATOR + std::to_string(user);
            std::string shareFilesDataDir = dataDir + ServiceConstants::SHAREFILES + bundleName;
            InstalldClient::GetInstance()->RemoveDir(shareFilesDataDir);
        }
    }
    return;
}

ErrCode BaseBundleInstaller::CleanShaderCache(const InnerBundleInfo &oldInfo,
    const std::string &bundleName, int32_t userId) const
{
    LOG_D(BMS_TAG_INSTALLER, "start for -n:%{public}s -u:%{public}d", bundleName.c_str(), userId);
    std::vector<std::string> dirs;
    std::string shaderCachePath = ServiceConstants::SHADER_CACHE_PATH + bundleName;
    dirs.emplace_back(shaderCachePath);

    std::vector<int32_t> allAppIndexes = {0};
    std::vector<int32_t> cloneAppIndexes = dataMgr_->GetCloneAppIndexesByInnerBundleInfo(oldInfo, userId);
    allAppIndexes.insert(allAppIndexes.end(), cloneAppIndexes.begin(), cloneAppIndexes.end());
    for (int32_t appIndex: allAppIndexes) {
        std::string cloneBundleName = bundleName;
        if (appIndex != 0) {
            cloneBundleName = BundleCloneCommonHelper::GetCloneDataDir(bundleName, appIndex);
        }
        std::string el1ShaderCachePath = ServiceConstants::NEW_SHADER_CACHE_PATH + cloneBundleName;
        el1ShaderCachePath = el1ShaderCachePath.replace(el1ShaderCachePath.find("%"), 1, std::to_string(userId));
        dirs.emplace_back(el1ShaderCachePath);

        // clean shader cache in /system_optimize
        std::string systemOptimizeShaderCache = ServiceConstants::SYSTEM_OPTIMIZE_PATH +
            cloneBundleName + ServiceConstants::SHADER_CACHE_SUBDIR;
        systemOptimizeShaderCache = systemOptimizeShaderCache.replace(systemOptimizeShaderCache.find("%"),
            1, std::to_string(userId));
        dirs.emplace_back(systemOptimizeShaderCache);
    }

    ErrCode result = InstalldClient::GetInstance()->CleanBundleDirs(dirs, true);
    if (result != ERR_OK) {
        LOG_W(BMS_TAG_DEFAULT, "clean bundle shader cache dirs failed, error:%{public}d", result);
        return result;
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CleanArkStartupCache(const std::string &bundleName) const
{
    LOG_D(BMS_TAG_INSTALLER, "start for -n:%{public}s", bundleName.c_str());
    std::string el1ArkStartupCachePath = ServiceConstants::SYSTEM_OPTIMIZE_PATH + bundleName +
        ServiceConstants::ARK_STARTUP_CACHE_DIR;
    std::vector<std::string> dirs;
    auto userIds = dataMgr_->GetUserIds(bundleName);
    for (const auto &userId : userIds) {
        auto multiPath = el1ArkStartupCachePath;
        multiPath = multiPath.replace(multiPath.find("%"), 1, std::to_string(userId));
        dirs.emplace_back(multiPath);
    }

    if (dirs.empty()) {
        return ERR_OK;
    }
    ErrCode result = InstalldClient::GetInstance()->CleanBundleDirs(dirs, true);
    if (result != ERR_OK) {
        LOG_W(BMS_TAG_DEFAULT, "clean bundle shader cache dirs failed, error:%{public}d", result);
        return result;
    }
    return ERR_OK;
}

void BaseBundleInstaller::CreateCloudShader(const std::string &bundleName, int32_t uid, int32_t gid) const
{
    const std::string cloudShaderOwner = OHOS::system::GetParameter(ServiceConstants::CLOUD_SHADER_OWNER, "");
    if (cloudShaderOwner.empty() || (bundleName != cloudShaderOwner)) {
        return;
    }

    constexpr int32_t mode = (S_IRWXU | S_IXGRP | S_IXOTH);
    ErrCode result = InstalldClient::GetInstance()->Mkdir(ServiceConstants::CLOUD_SHADER_PATH, mode, uid, gid);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_DEFAULT, "Mkdir %{public}s failed, error is %{public}d",
            ServiceConstants::CLOUD_SHADER_PATH, result);
        return;
    }

    constexpr int32_t commonMode = (S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
    result = InstalldClient::GetInstance()->Mkdir(ServiceConstants::CLOUD_SHADER_COMMON_PATH, commonMode, uid, gid);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_DEFAULT, "Mkdir %{public}s failed, error is %{public}d",
            ServiceConstants::CLOUD_SHADER_COMMON_PATH, result);
        return;
    }
}

ErrCode BaseBundleInstaller::DeleteCloudShader(const std::string &bundleName) const
{
    std::string newShaderCloudPath;
    newShaderCloudPath.append(ServiceConstants::NEW_CLOUD_SHADER_PATH).append(bundleName);
    LOG_D(BMS_TAG_INSTALLER, "DeleteCloudShader %{public}s", newShaderCloudPath.c_str());
    return InstalldClient::GetInstance()->RemoveDir(newShaderCloudPath, true);
}

ErrCode BaseBundleInstaller::DeleteEl1ShaderAndArkStartupCache(const InnerBundleInfo &oldInfo,
    const std::string &bundleName, int32_t userId) const
{
    std::vector<int32_t> allAppIndexes = {0};
    std::vector<int32_t> cloneAppIndexes = dataMgr_->GetCloneAppIndexesByInnerBundleInfo(oldInfo, userId);
    allAppIndexes.insert(allAppIndexes.end(), cloneAppIndexes.begin(), cloneAppIndexes.end());

    std::vector<std::string> dirs;
    for (int32_t appIndex: allAppIndexes) {
        std::string cloneBundleName = bundleName;
        if (appIndex != 0) {
            cloneBundleName = BundleCloneCommonHelper::GetCloneDataDir(bundleName, appIndex);
        }
        std::string el1ShaderCachePath = ServiceConstants::NEW_SHADER_CACHE_PATH + cloneBundleName;
        el1ShaderCachePath = el1ShaderCachePath.replace(el1ShaderCachePath.find("%"), 1, std::to_string(userId));
        dirs.emplace_back(el1ShaderCachePath);
        
        // Remove shader cache in /system_optimize
        std::string systemOptimizeShaderCache = ServiceConstants::SYSTEM_OPTIMIZE_PATH +
            cloneBundleName;
        systemOptimizeShaderCache = systemOptimizeShaderCache.replace(systemOptimizeShaderCache.find("%"),
            1, std::to_string(userId));
        dirs.emplace_back(systemOptimizeShaderCache);
    }

    ErrCode result = InstalldClient::GetInstance()->CleanBundleDirs(dirs, false);
    if (result != ERR_OK) {
        LOG_W(BMS_TAG_DEFAULT, "delete el1 bundle shader cache dirs failed, error:%{public}d", result);
        return result;
    }
    return ERR_OK;
}

std::string BaseBundleInstaller::GetCheckResultMsg() const
{
    return bundleInstallChecker_->GetCheckResultMsg();
}

void BaseBundleInstaller::SetCheckResultMsg(const std::string checkResultMsg) const
{
    bundleInstallChecker_->SetCheckResultMsg(checkResultMsg);
}

bool BaseBundleInstaller::VerifyActivationLock() const
{
    LOG_I(BMS_TAG_INSTALLER, "verify activation lock start");
    int32_t mode = GetIntParameter(IS_ROOT_MODE_PARAM, USER_MODE);
    if (mode != USER_MODE) {
        char enableActivationLock[BMS_ACTIVATION_LOCK_VAL_LEN] = {0};
        int32_t ret = GetParameter(BMS_ACTIVATION_LOCK, "", enableActivationLock, BMS_ACTIVATION_LOCK_VAL_LEN);
        if (ret <= 0) {
            return true;
        }
        if (std::strcmp(enableActivationLock, BMS_TRUE) != 0) {
            LOG_D(BMS_TAG_INSTALLER, "activation lock no check, because lock is off");
            return true;
        }
    }

    BmsExtensionDataMgr bmsExtensionDataMgr;
    bool pass = false;
    ErrCode res = bmsExtensionDataMgr.VerifyActivationLock(pass);
    if ((res == ERR_OK) && !pass) {
        LOG_E(BMS_TAG_INSTALLER, "machine be controlled, not allow to install app");
        return false;
    }

    LOG_D(BMS_TAG_INSTALLER, "activation lock pass");
    // otherwise, pass
    return true;
}

ErrCode BaseBundleInstaller::CheckAppBlackList(const std::string &bundleName, const int32_t userId) const
{
    BmsExtensionDataMgr bmsExtensionDataMgr;
    return bmsExtensionDataMgr.CheckAppBlackList(bundleName, userId);
}

bool BaseBundleInstaller::CheckWhetherCanBeUninstalled(const std::string &bundleName,
    const std::string &appIdentifier) const
{
    BmsExtensionDataMgr bmsExtensionDataMgr;
    LOG_D(BMS_TAG_INSTALLER, "CheckUninstall %{public}s", bundleName.c_str());
    bool res = bmsExtensionDataMgr.CheckWhetherCanBeUninstalled(bundleName, appIdentifier);
    if (!res) {
        LOG_E(BMS_TAG_INSTALLER, "uninstall %{public}s rejected", bundleName.c_str());
        return false;
    }
    return true;
}

void BaseBundleInstaller::CheckSystemFreeSizeAndClean() const
{
    if (BundleUtil::CheckSystemFreeSize(BMS_SERVICE_PATH, FIVE_MB)) {
        return;
    }
    BundleMgrHostImpl impl;
    ErrCode ret = impl.CleanBundleCacheFilesAutomatic(FIVE_MB);
    LOG_I(BMS_TAG_INSTALLER, "clean disk ret:%{public}d", ret);
}

void BaseBundleInstaller::CheckBundleNameAndStratAbility(const std::string &bundleName,
    const std::string &appIdentifier) const
{
    LOG_I(BMS_TAG_INSTALLER, "CheckBundleNameAndStratAbility %{public}s", bundleName.c_str());
    BmsExtensionDataMgr bmsExtensionDataMgr;
    bmsExtensionDataMgr.CheckBundleNameAndStratAbility(bundleName, appIdentifier);
}

ErrCode BaseBundleInstaller::MarkInstallFinish()
{
    InnerBundleInfo info;
    if (!GetTempBundleInfo(info)) {
        LOG_W(BMS_TAG_INSTALLER, "mark finish failed");
        return ERR_APPEXECFWK_GET_INSTALL_TEMP_BUNDLE_ERROR;
    }
    info.ResetAOTFlags();
    (void)bundleInstallChecker_->DetermineCloneApp(info);
    info.SetBundleStatus(InnerBundleInfo::BundleStatus::ENABLED);
    info.SetInstallMark(bundleName_, info.GetCurModuleName(), InstallExceptionStatus::INSTALL_FINISH);
    if (!InitDataMgr()) {
        return ERR_APPEXECFWK_NULL_PTR;
    }
    if (isAppExist_) {
        if (!dataMgr_->UpdateInnerBundleInfo(info, true)) {
            if (!dataMgr_->UpdateInnerBundleInfo(info, true)) {
                LOG_W(BMS_TAG_INSTALLER, "save mark failed, -n:%{public}s", bundleName_.c_str());
                return ERR_APPEXECFWK_ADD_BUNDLE_ERROR;
            }
        }
        for (const auto &userInfo : info.GetInnerBundleUserInfos()) {
            dataMgr_->UpdateShortcutInfoResId(bundleName_, userInfo.second.bundleUserInfo.userId);
        }
        PrintStartWindowIconId(info);
        return ERR_OK;
    }
    if (!dataMgr_->AddInnerBundleInfo(bundleName_, info, false)) {
        LOG_E(BMS_TAG_INSTALLER, "add bundle failed, -n:%{public}s", bundleName_.c_str());
        dataMgr_->UpdateBundleInstallState(bundleName_, InstallState::UNINSTALL_START);
        dataMgr_->UpdateBundleInstallState(bundleName_, InstallState::UNINSTALL_SUCCESS);
        return ERR_APPEXECFWK_UPDATE_BUNDLE_ERROR;
    }
    if (!dataMgr_->SaveInnerBundleInfo(info)) {
        LOG_W(BMS_TAG_INSTALLER, "save mark failed, -n:%{public}s", bundleName_.c_str());
        return ERR_APPEXECFWK_UPDATE_BUNDLE_ERROR;
    }
    PrintStartWindowIconId(info);
    return ERR_OK;
}

void BaseBundleInstaller::PrintStartWindowIconId(const InnerBundleInfo &info)
{
    const auto &abilityMap = info.GetInnerAbilityInfos();
    for (const auto &ability : abilityMap) {
        const auto &abilityInfo = ability.second;
        if (abilityInfo.isLauncherAbility) {
            APP_LOGI_NOFUNC("startWindowIconId %{public}s_%{public}s_%{public}s_%{public}d",
                abilityInfo.bundleName.c_str(), abilityInfo.moduleName.c_str(), abilityInfo.name.c_str(),
                abilityInfo.startWindowIconId);
        }
    }
}

bool BaseBundleInstaller::IsDriverForAllUser(const std::string &bundleName)
{
    if (!OHOS::system::GetBoolParameter(ServiceConstants::IS_DRIVER_FOR_ALL_USERS, true)) {
        return false;
    }

    if (!InitDataMgr()) {
        return false;
    }

    InnerBundleInfo info;
    bool isAppExist = dataMgr_->FetchInnerBundleInfo(bundleName, info);
    if (isAppExist) {
        const auto extensions = info.GetInnerExtensionInfos();
        for (const auto &item : extensions) {
            if (item.second.type == ExtensionAbilityType::DRIVER) {
                APP_LOGI("find driver extension ability, bundleName: %{public}s, moduleName: %{public}s",
                    item.second.bundleName.c_str(), item.second.moduleName.c_str());
                return true;
            }
        }
    }
    return false;
}

int32_t BaseBundleInstaller::GetDriverInstallUser(const std::string &bundleName)
{
    if (!InitDataMgr()) {
        return Constants::START_USERID;
    }

    InnerBundleInfo info;
    bool isAppExist = dataMgr_->FetchInnerBundleInfo(bundleName, info);
    if (isAppExist) {
        const auto userInfos = info.GetInnerBundleUserInfos();
        if (!userInfos.empty()) {
            const auto item = userInfos.begin();
            return item->second.bundleUserInfo.userId;
        }
    }
    return Constants::START_USERID;
}

bool BaseBundleInstaller::IsEnterpriseForAllUser(const InstallParam &installParam, const std::string &bundleName)
{
    if (!installParam.IsEnterpriseForAllUser()) {
        return false;
    }
    if (!OHOS::system::GetBoolParameter(ServiceConstants::IS_ENTERPRISE_DEVICE, false)) {
        LOG_W(BMS_TAG_INSTALLER, "not enterprise device, bundleName: %{public}s", bundleName.c_str());
        return false;
    }
    if (!InitDataMgr()) {
        return false;
    }
    InnerBundleInfo info;
    bool isAppExist = dataMgr_->FetchInnerBundleInfo(bundleName, info);
    if (isAppExist) {
        std::string appDistributionType = info.GetAppDistributionType();
        if (Constants::APP_DISTRIBUTION_TYPE_ENTERPRISE_MDM != appDistributionType &&
            Constants::APP_DISTRIBUTION_TYPE_ENTERPRISE_NORMAL != appDistributionType) {
            LOG_W(BMS_TAG_INSTALLER, "not enterprise app, bundleName: %{public}s", bundleName.c_str());
            return false;
        }
        return info.IsInstalledForAllUser();
    }
    LOG_W(BMS_TAG_INSTALLER, "app not exist: %{public}s", bundleName.c_str());
    return false;
}

bool BaseBundleInstaller::SetDisposedRuleWhenBundleUpdateStart(
    const std::unordered_map<std::string, InnerBundleInfo> &infos,
    const InnerBundleInfo &oldBundleInfo, bool isPreInstallApp)
{
#ifdef BUNDLE_FRAMEWORK_APP_CONTROL
    if (isPreInstallApp || !isAppExist_) {
        return false;
    }
    std::vector<std::string> oldModuleNames;
    oldBundleInfo.GetModuleNames(oldModuleNames);
    std::vector<std::string> newModuleNames;
    for (const auto &iter : infos) {
        iter.second.GetModuleNames(newModuleNames);
    }
    needSetDisposeRule_ = false;
    for (const auto &moduleName : oldModuleNames) {
        if (std::find(newModuleNames.begin(), newModuleNames.end(), moduleName) != newModuleNames.end()) {
            needSetDisposeRule_ = true;
            break;
        }
    }
    if (needSetDisposeRule_) {
        LOG_I(BMS_TAG_INSTALLER, "set bms disposed rule when -n :%{public}s install start",
            bundleName_.c_str());
        std::shared_ptr<AppControlManager> appControlMgr = DelayedSingleton<AppControlManager>::GetInstance();
        if (appControlMgr == nullptr) {
            LOG_E(BMS_TAG_INSTALLER, "appControlMgr is nullptr, when -n :%{public}s install start",
                bundleName_.c_str());
            return false;
        }
        appControlMgr->SetDisposedRuleOnlyForBms(oldBundleInfo.GetAppId());
    }
    return needSetDisposeRule_;
#else
    LOG_W(BMS_TAG_INSTALLER, "app control is disable");
    return false;
#endif
}

bool BaseBundleInstaller::DeleteDisposedRuleWhenBundleUpdateEnd(const InnerBundleInfo &oldBundleInfo)
{
#ifdef BUNDLE_FRAMEWORK_APP_CONTROL
    if (!needSetDisposeRule_) {
        return false;
    }
    LOG_I(BMS_TAG_INSTALLER, "delete bms disposed rule when -n :%{public}s install end", bundleName_.c_str());
    std::shared_ptr<AppControlManager> appControlMgr = DelayedSingleton<AppControlManager>::GetInstance();
    if (appControlMgr == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "appControlMgr is nullptr, when -n :%{public}s install end", bundleName_.c_str());
        return false;
    }

    appControlMgr->DeleteDisposedRuleOnlyForBms(oldBundleInfo.GetAppId());
    return true;
#else
    LOG_W(BMS_TAG_INSTALLER, "app control is disable");
    return false;
#endif
}

bool BaseBundleInstaller::SetDisposedRuleWhenBundleUninstallStart(
    const std::string &bundleName, const std::string &appId, bool isMultiUser)
{
#ifdef BUNDLE_FRAMEWORK_APP_CONTROL
    if (isMultiUser) {
        return false;
    }
    LOG_I(BMS_TAG_INSTALLER, "set bms disposed rule when -n :%{public}s uninstall start",
        bundleName.c_str());
    std::shared_ptr<AppControlManager> appControlMgr = DelayedSingleton<AppControlManager>::GetInstance();
    if (appControlMgr == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "appControlMgr is nullptr, when -n :%{public}s uninstall start",
            bundleName.c_str());
        return false;
    }
    appControlMgr->SetDisposedRuleOnlyForBms(appId);
    return true;
#else
    LOG_W(BMS_TAG_INSTALLER, "app control is disable");
    return false;
#endif
}

bool BaseBundleInstaller::DeleteDisposedRuleWhenBundleUninstallEnd(
    const std::string &bundleName, const std::string &appId, bool isMultiUser)
{
#ifdef BUNDLE_FRAMEWORK_APP_CONTROL
    if (isMultiUser) {
        return false;
    }
    LOG_I(BMS_TAG_INSTALLER, "delete bms disposed rule when -n :%{public}s uninstall end", bundleName.c_str());
    std::shared_ptr<AppControlManager> appControlMgr = DelayedSingleton<AppControlManager>::GetInstance();
    if (appControlMgr == nullptr) {
        LOG_E(BMS_TAG_INSTALLER, "appControlMgr is nullptr, when -n :%{public}s uninstall end", bundleName.c_str());
        return false;
    }
    appControlMgr->DeleteDisposedRuleOnlyForBms(appId);
    return true;
#else
    LOG_W(BMS_TAG_INSTALLER, "app control is disable");
    return false;
#endif
}

bool BaseBundleInstaller::AddAppGalleryHapToTempPath(const bool isPreInstall,
    const std::unordered_map<std::string, InnerBundleInfo> &infos)
{
    if ((dataMgr_ == nullptr) || isPreInstall || infos.empty()) {
        LOG_D(BMS_TAG_INSTALLER, "no need to process");
        return false;
    }
    const InnerBundleInfo &bundleInfo = infos.begin()->second;
    if (!bundleInfo.IsSystemApp()) {
        LOG_D(BMS_TAG_INSTALLER, "not system app");
        return false;
    }
    std::string bundleName = OHOS::system::GetParameter(ServiceConstants::CLOUD_SHADER_OWNER, "");
    if (bundleName.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "ag param get failed");
        return false;
    }
    if (bundleInfo.GetBundleName() != bundleName) {
        LOG_D(BMS_TAG_INSTALLER, "bundle %{public}s is not appGallery", bundleInfo.GetBundleName().c_str());
        return false;
    }
    if (!BundleUtil::DeleteDir(ServiceConstants::BMS_APP_COPY_TEMP_PATH)) {
        LOG_E(BMS_TAG_INSTALLER, "delete app copy temp path failed %{public}d", errno);
    }
    std::string targetPath = BundleUtil::CreateTempDir(ServiceConstants::BMS_APP_COPY_TEMP_PATH);
    if (targetPath.empty()) {
        LOG_E(BMS_TAG_INSTALLER, "app copy temp path create failed %{public}d", errno);
        return false;
    }
    needDeleteAppTempPath_ = true;
    for (const auto &item : infos) {
        auto pos = item.first.rfind(ServiceConstants::FILE_SEPARATOR_CHAR);
        if (pos == std::string::npos) {
            LOG_E(BMS_TAG_INSTALLER, "path %{public}s may error", item.first.c_str());
            continue;
        }
        if (!BundleUtil::CopyFileFast(item.first, targetPath + item.first.substr(pos), true)) {
            LOG_E(BMS_TAG_INSTALLER, "copy hap %{public}s failed err %{public}d", item.first.c_str(), errno);
            return false;
        }
    }
    if (!BundleUtil::RenameFile(targetPath, ServiceConstants::BMS_APP_TEMP_PATH)) {
        LOG_E(BMS_TAG_INSTALLER, "rename app temp path failed %{public}d", errno);
        return false;
    }

    LOG_I(BMS_TAG_INSTALLER, "copy hap file to app_temp end");
    return true;
}

bool BaseBundleInstaller::DeleteAppGalleryHapFromTempPath()
{
    if (!needDeleteAppTempPath_) {
        return true;
    }
    // rename app_temp to app_copy_temp
    if (!BundleUtil::RenameFile(ServiceConstants::BMS_APP_TEMP_PATH, ServiceConstants::BMS_APP_COPY_TEMP_PATH)) {
        LOG_E(BMS_TAG_INSTALLER, "rename app_temp to app_copy_temp failed %{public}d", errno);
        return false;
    }
    if (!BundleUtil::DeleteDir(ServiceConstants::BMS_APP_COPY_TEMP_PATH)) {
        LOG_E(BMS_TAG_INSTALLER, "delete app_copy_temp failed %{public}d", errno);
        return false;
    }
    LOG_I(BMS_TAG_INSTALLER, "delete app_temp file end");
    return true;
}

void BaseBundleInstaller::CheckPreBundle(const std::unordered_map<std::string, InnerBundleInfo> &newInfos,
    const InstallParam &installParam, bool isRecover)
{
    if (newInfos.empty() || isRecover || installParam.isPreInstallApp) {
        LOG_NOFUNC_W(BMS_TAG_INSTALLER, "newInfos is null or no need to check");
        return;
    }
    // check is system app or not
    const auto &newInfo = newInfos.begin()->second;
    std::string bundleName = newInfo.GetBundleName();
    if (!newInfo.IsSystemApp()) {
        LOG_NOFUNC_W(BMS_TAG_INSTALLER, "%{public}s not system app", bundleName.c_str());
        return;
    }
    // check is uninstalled preset app or not
    if (dataMgr_->IsBundleExist(bundleName)) {
        LOG_NOFUNC_W(BMS_TAG_INSTALLER, "%{public}s has installed", bundleName.c_str());
        return;
    }
    PreInstallBundleInfo preInstallBundleInfo;
    if (!dataMgr_->GetPreInstallBundleInfo(bundleName, preInstallBundleInfo)) {
        LOG_NOFUNC_W(BMS_TAG_INSTALLER, "%{public}s not preset app", bundleName.c_str());
        return;
    }
    if (preInstallBundleInfo.GetBundlePaths().empty()) {
        LOG_NOFUNC_W(BMS_TAG_INSTALLER, "preset app bundle path empty");
        return;
    }
    // check identifier and appId
    std::string hapPath = preInstallBundleInfo.GetBundlePaths().front();
    Security::Verify::HapVerifyResult hapVerifyResult;
    ErrCode result = BundleVerifyMgr::HapVerify(hapPath, hapVerifyResult, true);
    if (result != ERR_OK) {
        LOG_NOFUNC_W(BMS_TAG_INSTALLER, "get preset app's appId fail %{public}s", hapPath.c_str());
        return;
    }
    Security::Verify::ProvisionInfo provisionInfo = hapVerifyResult.GetProvisionInfo();
    if (CheckAppIdentifier(provisionInfo.bundleInfo.appIdentifier, newInfo.GetAppIdentifier(),
        provisionInfo.appId, newInfo.GetProvisionId())) {
        LOG_NOFUNC_I(BMS_TAG_INSTALLER, "%{public}s need recover before install", bundleName.c_str());
        InstallParam userInstallParam;
        userInstallParam.userId = installParam.userId;
        userInstallParam.installFlag = InstallFlag::REPLACE_EXISTING;
        BaseBundleInstaller installer;
        result = installer.Recover(bundleName, userInstallParam);
        isPreBundleRecovered_ = (result == ERR_OK);
        if (!isPreBundleRecovered_) {
            LOG_NOFUNC_E(BMS_TAG_INSTALLER, "recover before install result: %{public}d", result);
        }
        return;
    }
    LOG_NOFUNC_W(BMS_TAG_INSTALLER, "%{public}s appId or appIdentifier not same with preset app, need not recover",
        bundleName.c_str());
}

ErrCode BaseBundleInstaller::CheckShellCanInstallPreApp(
    const std::unordered_map<std::string, InnerBundleInfo> &newInfos)
{
    if (!IsShellOrDevAssistant()) {
        return ERR_OK;
    }
    if (newInfos.empty()) {
        return ERR_OK;
    }
    std::string bundleName = newInfos.begin()->second.GetBundleName();
    if (dataMgr_->IsBundleExist(bundleName)) {
        return ERR_OK;
    }
    PreInstallBundleInfo preInstallBundleInfo;
    if (!dataMgr_->GetPreInstallBundleInfo(bundleName, preInstallBundleInfo)) {
        return ERR_OK;
    }
    if (preInstallBundleInfo.GetBundlePaths().empty()) {
        LOG_NOFUNC_W(BMS_TAG_DEFAULT, "pre bundle path empty");
        return ERR_OK;
    }
    std::string hapPath = preInstallBundleInfo.GetBundlePaths().front();
    Security::Verify::HapVerifyResult hapVerifyResult;
    ErrCode verifyRes = BundleVerifyMgr::HapVerify(hapPath, hapVerifyResult);
    if (verifyRes != ERR_OK) {
        LOG_NOFUNC_W(BMS_TAG_DEFAULT, "get appId fail %{public}s", hapPath.c_str());
        return ERR_OK;
    }
    Security::Verify::ProvisionInfo provisionInfo = hapVerifyResult.GetProvisionInfo();
    if (CheckAppIdentifier(provisionInfo.bundleInfo.appIdentifier, newInfos.begin()->second.GetAppIdentifier(),
        provisionInfo.appId, newInfos.begin()->second.GetProvisionId())) {
        return ERR_OK;
    }
    LOG_E(BMS_TAG_DEFAULT, "%{public}s appId or appIdentifier not same with preinstalled app",
        newInfos.begin()->second.GetBundleName().c_str());
    return ERR_APPEXECFWK_INSTALL_APPID_NOT_SAME_WITH_PREINSTALLED;
}

bool BaseBundleInstaller::RecoverHapToken(const std::string &bundleName, const int32_t userId,
    Security::AccessToken::AccessTokenIDEx& accessTokenIdEx, const InnerBundleInfo &innerBundleInfo,
    const bool isDebugGrant)
{
    UninstallBundleInfo uninstallBundleInfo;
    if (!dataMgr_->GetUninstallBundleInfo(bundleName, uninstallBundleInfo)) {
        return false;
    }
    LOG_I(BMS_TAG_INSTALLER, "bundleName:%{public}s getUninstallBundleInfo success", bundleName.c_str());
    if (uninstallBundleInfo.userInfos.empty()) {
        LOG_W(BMS_TAG_INSTALLER, "bundleName:%{public}s empty userInfos", bundleName.c_str());
        return false;
    }
    if (uninstallBundleInfo.userInfos.find(std::to_string(userId)) != uninstallBundleInfo.userInfos.end()) {
        accessTokenIdEx.tokenIdExStruct.tokenID =
            uninstallBundleInfo.userInfos.at(std::to_string(userId)).accessTokenId;
        accessTokenIdEx.tokenIDEx = uninstallBundleInfo.userInfos.at(std::to_string(userId)).accessTokenIdEx;
        Security::AccessToken::HapInfoCheckResult checkResult;
        if (BundlePermissionMgr::UpdateHapToken(accessTokenIdEx, innerBundleInfo, userId, checkResult,
            verifyRes_.GetProvisionInfo().appServiceCapabilities, false, isDebugGrant) == ERR_OK) {
            return true;
        } else {
            LOG_W(BMS_TAG_INSTALLER, "bundleName:%{public}s UpdateHapToken failed", bundleName.c_str());
            SetVerifyPermissionResult(checkResult);
        }
    }
    return false;
}

void BaseBundleInstaller::UpdateKillApplicationProcess(const InnerBundleInfo &oldInfo)
{
    if (!InitDataMgr()) {
        LOG_E(BMS_TAG_INSTALLER, "DataMgr null");
        return;
    }
    auto currentBundleUserIds = dataMgr_->GetUserIds(bundleName_);
    //kill the bundle process in all user during updating
    for (auto userId : currentBundleUserIds) {
        // kill the bundle process during updating
        if (!AbilityManagerHelper::UninstallApplicationProcesses(
            oldInfo.GetApplicationName(), oldInfo.GetUid(userId), true)) {
            LOG_W(BMS_TAG_INSTALLER, "fail to kill running application");
        }
        InnerBundleUserInfo userInfo;
        if (!oldInfo.GetInnerBundleUserInfo(userId, userInfo)) {
            LOG_W(BMS_TAG_INSTALLER, "the origin application is not installed at current user");
            continue;
        }
        for (auto &cloneInfo : userInfo.cloneInfos) {
            if (!AbilityManagerHelper::UninstallApplicationProcesses(
                oldInfo.GetApplicationName(), cloneInfo.second.uid, true, atoi(cloneInfo.first.c_str()))) {
                LOG_W(BMS_TAG_INSTALLER, "fail to kill clone application");
            }
        }
    }
}

std::string BaseBundleInstaller::GetAssetAccessGroups(const std::string &bundleName)
{
    if (!InitDataMgr()) {
        LOG_E(BMS_TAG_INSTALLER, "DataMgr null");
        return Constants::EMPTY_STRING;
    }
    std::vector<std::string> assetAccessGroups;
    ErrCode ret = dataMgr_->GetAssetAccessGroups(bundleName, assetAccessGroups);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "GetAssetAccessGroups failed, ret=%{public}d", ret);
        return Constants::EMPTY_STRING;
    }
    std::string assetAccessGroupsStr;
    if (!assetAccessGroups.empty()) {
        std::stringstream assetAccessGroupsStream;
        std::copy(assetAccessGroups.begin(), assetAccessGroups.end(),
            std::ostream_iterator<std::string>(assetAccessGroupsStream, ","));
        assetAccessGroupsStr = assetAccessGroupsStream.str();
        if (!assetAccessGroupsStr.empty()) {
            assetAccessGroupsStr.pop_back();
        }
    }
    return assetAccessGroupsStr;
}

std::string BaseBundleInstaller::GetDeveloperId(const std::string &bundleName)
{
    if (!InitDataMgr()) {
        LOG_E(BMS_TAG_INSTALLER, "DataMgr null");
        return Constants::EMPTY_STRING;
    }
    std::string developerId;
    ErrCode ret = dataMgr_->GetDeveloperId(bundleName, developerId);
    if (ret != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "GetDeveloperId failed, ret=%{public}d", ret);
    }
    return developerId;
}

void BaseBundleInstaller::GetModuleNames(const std::string &bundleName, std::vector<std::string> &moduleNames)
{
    if (!InitDataMgr()) {
        LOG_E(BMS_TAG_INSTALLER, "DataMgr null");
        return;
    }
    dataMgr_->GetBundleModuleNames(bundleName, moduleNames);
}

ErrCode BaseBundleInstaller::CheckAppDistributionType()
{
    if (isAppExist_) {
        LOG_D(BMS_TAG_INSTALLER, "no check appDisType when bundle update");
        return ERR_OK;
    }
    auto res = bundleInstallChecker_->CheckAppDistributionType(appDistributionType_);
    if (res != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "check appDisType failed when bundles installed %{public}d", res);
        return ERR_APP_DISTRIBUTION_TYPE_NOT_ALLOW_INSTALL;
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::CheckPreAppAllowHdcInstall(const InstallParam &installParam,
    const std::vector<Security::Verify::HapVerifyResult> &hapVerifyRes)
{
    if (!installParam.isCallByShell || sysEventInfo_.callingUid == Constants::ROOT_UID) {
        return ERR_OK;
    }

    if (hapVerifyRes.empty()) {
        return ERR_OK;
    }

    Security::Verify::ProvisionInfo provisionInfo = hapVerifyRes.begin()->GetProvisionInfo();
    if (provisionInfo.isOpenHarmony) {
        return ERR_OK;
    }

    if (provisionInfo.distributionType != Security::Verify::AppDistType::OS_INTEGRATION) {
        return ERR_OK;
    }

    if (provisionInfo.type != Security::Verify::ProvisionType::RELEASE) {
        return ERR_OK;
    }

    auto privileges = provisionInfo.appPrivilegeCapabilities;
    if (find(privileges.begin(), privileges.end(), PRIVILEGE_ALLOW_HDC_INSTALL) != privileges.end()) {
        return ERR_OK;
    }

    if (IsRdDevice()) {
        return ERR_OK;
    }
    return ERR_APPEXECFWK_INSTALL_OS_INTEGRATION_BUNDLE_NOT_ALLOWED_FOR_SHELL;
}

ErrCode BaseBundleInstaller::CheckInstallGrantPermission(const InstallParam &installParam,
    const std::vector<Security::Verify::HapVerifyResult> &hapVerifyRes)
{
    if (!installParam.isCallByShell || hapVerifyRes.empty()) {
        return ERR_OK;
    }
    auto item = installParam.parameters.find(ServiceConstants::BMS_PARA_INSTALL_GRANT_PERMISSION);
    if ((item == installParam.parameters.end()) || (item->second != ServiceConstants::BMS_TRUE)) {
        return ERR_OK;
    }
    Security::Verify::ProvisionInfo provisionInfo = hapVerifyRes.begin()->GetProvisionInfo();
    if (provisionInfo.type != Security::Verify::ProvisionType::DEBUG) {
        return ERR_APPEXECFWK_INSTALL_GRANT_PERMISSION_NOT_DEBUG_BUNDLE;
    }
    return ERR_OK;
}

bool BaseBundleInstaller::CheckIsDebugGrant(const InstallParam &installParam,
    const std::string &appProvisionType)
{
    auto item = installParam.parameters.find(ServiceConstants::BMS_PARA_INSTALL_GRANT_PERMISSION);
    if ((item != installParam.parameters.end()) && (item->second == ServiceConstants::BMS_TRUE) &&
        installParam.isCallByShell &&
        appProvisionType == Constants::APP_PROVISION_TYPE_DEBUG) {
        return true;
    }
    return false;
}

void BaseBundleInstaller::CheckPreBundleRecoverResult(ErrCode &result)
{
    if (isPreBundleRecovered_ && result != ERR_OK && sysEventInfo_.callingUid != ServiceConstants::SHELL_UID &&
        sysEventInfo_.callingUid != Constants::ROOT_UID) {
        LOG_E(BMS_TAG_INSTALLER, "install failed, result = %{public}d, only restore to preinstalled", result);
        result = ERR_APPEXECFWK_INSTALL_FAILED_AND_RESTORE_TO_PREINSTALLED;
    }
}

void BaseBundleInstaller::RemovePluginOnlyInCurrentUser(const InnerBundleInfo &info)
{
    InnerBundleUserInfo userInfo;
    std::string hostBundleName = info.GetBundleName();
    if (!info.GetInnerBundleUserInfo(userId_, userInfo)) {
        LOG_E(BMS_TAG_INSTALLER, "%{public}s get user %{public}d failed",
            hostBundleName.c_str(), userId_);
        return;
    }
    if (!InitDataMgr()) {
        return;
    }
    std::unordered_map<std::string, PluginBundleInfo> pluginBundleInfos = info.GetAllPluginBundleInfo();
    for (const auto &pluginName : userInfo.installedPluginSet) {
        if (info.HasMultiUserPlugin(pluginName)) {
            continue;
        }
        LOG_I(BMS_TAG_INSTALLER, "%{public}s remove plugin %{public}s", hostBundleName.c_str(), pluginName.c_str());
        auto it = pluginBundleInfos.find(pluginName);
        if (it == pluginBundleInfos.end()) {
            LOG_E(BMS_TAG_INSTALLER, "can not find plugin %{public}s when uninstall %{public}s",
                pluginName.c_str(), hostBundleName.c_str());
            continue;
        }
        dataMgr_->DeleteRouterInfoForPlugin(hostBundleName, it->second);
        if (dataMgr_->RemovePluginInfo(hostBundleName, pluginName, userId_) != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "remove plugin %{public}s fail when uninstall %{public}s",
                pluginName.c_str(), hostBundleName.c_str());
            continue;
        }
        if ((InstalldClient::GetInstance()->RemoveDir(it->second.codePath)) != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "delete dir %{public}s failed", it->second.codePath.c_str());
        }
    }
}

std::string BaseBundleInstaller::GetModulePath(
    const InnerBundleInfo &info, const bool isBundleUpdate, const bool isModuleUpdate)
{
    if (isBundleUpdate) {
        return std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR +
            std::string(ServiceConstants::BUNDLE_NEW_CODE_DIR) + info.GetBundleName() +
            ServiceConstants::PATH_SEPARATOR + info.GetCurrentModulePackage();
    }
    if (isModuleUpdate) {
        return info.GetAppCodePath() + ServiceConstants::PATH_SEPARATOR +
            info.GetCurrentModulePackage() + ServiceConstants::TMP_SUFFIX;
    }
    return info.GetAppCodePath() + ServiceConstants::PATH_SEPARATOR + info.GetCurrentModulePackage();
}

ErrCode BaseBundleInstaller::ProcessBundleCodePath(
    const std::unordered_map<std::string, InnerBundleInfo> &newInfos,
    const InnerBundleInfo &oldInfo, const std::string &bundleName,
    const bool isBundleUpdate, const bool needCopyHap)
{
    if (!isBundleUpdate) {
        // move so file to real installation dir
        bool needDeleteOldLibraryPath = NeedDeleteOldNativeLib(newInfos, oldInfo);
        auto result = MoveSoFileToRealInstallationDir(newInfos, needDeleteOldLibraryPath);
        CHECK_RESULT(result, "move so file to install path failed %{public}d");
        result = FinalProcessHapAndSoForBundleUpdate(newInfos, needCopyHap, needDeleteOldLibraryPath);
        CHECK_RESULT(result, "final process hap and so failed %{public}d");
        // attention pls, rename operation shoule be almost the last operation to guarantee the rollback operation
        // when someone failure occurs in the installation flow
        result = RenameAllTempDir(newInfos);
        CHECK_RESULT(result, "rename temp dirs failed with result %{public}d");
        return result;
    }
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "ProcessBundleCodePath start -n %{public}s", bundleName.c_str());
    // rename so path
    ErrCode result = MoveSoFileToRealInstallationDir(newInfos, false, true);
    CHECK_RESULT(result, "move so file to install path failed %{public}d");
    RemoveEmptyDirs(newInfos, true);
    for (const auto &item : newInfos) {
        RemoveTempPathOnlyUsedForSo(item.second, true);
    }
    // process files in +new-
    std::string realAppCodePath = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR +
        bundleName;
    std::string newAppCodePath = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR +
        std::string(ServiceConstants::BUNDLE_NEW_CODE_DIR) + bundleName;
    // process dynamic icon file
    result = ProcessDynamicIconFileWhenUpdate(oldInfo, realAppCodePath, newAppCodePath);
    CHECK_RESULT(result, "copy extend resource to install path failed %{public}d");
    // process plugin dir
    result = ProcessPluginFilesWhenUpdate(oldInfo, realAppCodePath, newAppCodePath);
    CHECK_RESULT(result, "copy plugin file to install path failed %{public}d");
    // rename real code path to +old code path
    result = InnerProcessCodePathRealToOld(bundleName, oldInfo.GetVersionCode());
    CHECK_RESULT(result, "rename rename real to +old code path failed, error is %{public}d");
    // rename +new- code path to real code path
    result = InnerProcessCodePathNewToReal(bundleName, oldInfo.GetVersionCode());
    CHECK_RESULT(result, "rename rename +new to real code path failed, error is %{public}d");
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "ProcessBundleCodePath end -n %{public}s", bundleName.c_str());
    return ERR_OK;
}

ErrCode BaseBundleInstaller::ProcessDynamicIconFileWhenUpdate(
    const InnerBundleInfo &oldInfo,
    const std::string &oldPath,
    const std::string &newPath)
{
    auto extendResourceInfos = oldInfo.GetExtendResourceInfos();
    if (extendResourceInfos.empty()) {
        return ERR_OK;
    }
    APP_LOGI("-n %{public}s has dynamic icon, process start", oldInfo.GetBundleName().c_str());
    std::string oldExtendResourcePath = oldPath + ServiceConstants::PATH_SEPARATOR +
        ServiceConstants::EXT_RESOURCE_FILE_PATH;
    bool isExtResource = false;
    InstalldClient::GetInstance()->IsExistDir(oldExtendResourcePath, isExtResource);
    if (!isExtResource) {
        APP_LOGW("-n %{public}s old ext_resource path not exist", oldInfo.GetBundleName().c_str());
        return ERR_OK;
    }
    std::string newExtendResourcePath = newPath + ServiceConstants::PATH_SEPARATOR +
        ServiceConstants::EXT_RESOURCE_FILE_PATH;
    auto result = InstalldClient::GetInstance()->CreateBundleDir(newExtendResourcePath);
    if (result != ERR_OK) {
        APP_LOGE("-n %{public}s create ext_resource failed", oldInfo.GetBundleName().c_str());
        return result;
    }
    for (const auto &extendResource : extendResourceInfos) {
        std::string fileName = ServiceConstants::PATH_SEPARATOR +extendResource.second.moduleName +
            ServiceConstants::HSP_FILE_SUFFIX;
        result = InstalldClient::GetInstance()->CopyFile(oldExtendResourcePath + fileName,
            newExtendResourcePath + fileName);
        if (result != ERR_OK) {
            APP_LOGE("-n %{public}s copy ext_resource failed", oldInfo.GetBundleName().c_str());
            return result;
        }
    }
    APP_LOGI("-n %{public}s has dynamic icon, process end", oldInfo.GetBundleName().c_str());
    return ERR_OK;
}

ErrCode BaseBundleInstaller::ProcessPluginFilesWhenUpdate(
    const InnerBundleInfo &oldInfo,
    const std::string &oldPath,
    const std::string &newPath)
{
    auto pluginInfos = oldInfo.GetAllPluginBundleInfo();
    if (pluginInfos.empty()) {
        return ERR_OK;
    }
    APP_LOGI("-n %{public}s has plugin", oldInfo.GetBundleName().c_str());
    std::string oldPluginPath = oldPath + ServiceConstants::PATH_SEPARATOR +
        ServiceConstants::PLUGIN_FILE_PATH;
    bool isPluginDir = false;
    InstalldClient::GetInstance()->IsExistDir(oldPluginPath, isPluginDir);
    if (!isPluginDir) {
        APP_LOGW("-n %{public}s old plugin path not exist", oldInfo.GetBundleName().c_str());
        return ERR_OK;
    }
    std::string newPluginPath = newPath + ServiceConstants::PATH_SEPARATOR +
        ServiceConstants::PLUGIN_FILE_PATH;
    auto result = InstalldClient::GetInstance()->CopyDir(oldPluginPath, newPluginPath);
    if (result != ERR_OK) {
        APP_LOGE("-n %{public}s copy plugin file failed", oldInfo.GetBundleName().c_str());
        return result;
    }
    APP_LOGI("-n %{public}s process plugin file end", oldInfo.GetBundleName().c_str());
    return ERR_OK;
}

ErrCode BaseBundleInstaller::InnerProcessCodePathCreateNewDir(const std::string &bundleName,
    const bool isBundleUpdate)
{
    if (!isBundleUpdate) {
        return ERR_OK;
    }
    (void)DelayedSingleton<InstallExceptionMgr>::GetInstance()->HandleBundleExceptionInfo(bundleName);
    InstallExceptionInfo exceptionInfo;
    exceptionInfo.status = InstallRenameExceptionStatus::CREATE_NEW_DIR;
    ErrCode result = DelayedSingleton<InstallExceptionMgr>::GetInstance()->SaveBundleExceptionInfo(bundleName,
        exceptionInfo);
    CHECK_RESULT(result, "save create new dir exception failed, error is %{public}d");
    return result;
}

ErrCode BaseBundleInstaller::InnerProcessCodePathRealToOld(
    const std::string &bundleName, const uint32_t oldVersionCode)
{
    // 1. save real to +old- status
    InstallExceptionInfo exceptionInfo;
    exceptionInfo.status = InstallRenameExceptionStatus::RENAME_RELA_TO_OLD_PATH;
    exceptionInfo.versionCode = oldVersionCode;
    ErrCode result = DelayedSingleton<InstallExceptionMgr>::GetInstance()->SaveBundleExceptionInfo(bundleName,
        exceptionInfo);
    CHECK_RESULT(result, "save real to +old exception failed, error is %{public}d");
    // 2. rename real to +old-
    std::string realAppCodePath = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR +
        bundleName;
    std::string oldAppCodePath = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR +
        std::string(ServiceConstants::BUNDLE_OLD_CODE_DIR) + bundleName;
    result = InstalldClient::GetInstance()->RenameModuleDir(realAppCodePath, oldAppCodePath);
    if (result != ERR_OK) {
        if (!BundleUtil::IsExistDir(realAppCodePath) && errno == ENOENT) {
            LOG_NOFUNC_W(BMS_TAG_INSTALLER, "realAppCodePath(%{public}s) is not exist", realAppCodePath.c_str());
        } else {
            LOG_NOFUNC_E(BMS_TAG_INSTALLER, "rename real to +old- code path failed, error is %{public}d", result);
            return result;
        }
    }
    return ERR_OK;
}

ErrCode BaseBundleInstaller::InnerProcessCodePathNewToReal(
    const std::string &bundleName, const uint32_t oldVersionCode)
{
    // 1. save +new to real status
    InstallExceptionInfo exceptionInfo;
    exceptionInfo.status = InstallRenameExceptionStatus::RENAME_NEW_TO_REAL_PATH;
    exceptionInfo.versionCode = oldVersionCode;
    ErrCode result = DelayedSingleton<InstallExceptionMgr>::GetInstance()->SaveBundleExceptionInfo(bundleName,
        exceptionInfo);
    CHECK_RESULT(result, "save +new to real exception failed, error is %{public}d");
    // 2. rename +new to real
    std::string newAppCodePath = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR +
        std::string(ServiceConstants::BUNDLE_NEW_CODE_DIR) + bundleName;
    std::string realAppCodePath = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR +
        bundleName;
    result = InstalldClient::GetInstance()->RenameModuleDir(newAppCodePath, realAppCodePath);
    CHECK_RESULT(result, "rename +new to real path failed, error is %{public}d");
    return ERR_OK;
}

void BaseBundleInstaller::ProcessOldCodePath(
    const std::string &bundleName, const bool isBundleUpdate)
{
    if (!isBundleUpdate) {
        return;
    }
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "ProcessOldCodePath start -n %{public}s", bundleName.c_str());
    InstallExceptionInfo exceptionInfo;
    exceptionInfo.status = InstallRenameExceptionStatus::DELETE_OLD_PATH;
    ErrCode result = DelayedSingleton<InstallExceptionMgr>::GetInstance()->SaveBundleExceptionInfo(bundleName,
        exceptionInfo);
    if (result != ERR_OK) {
        LOG_NOFUNC_E(BMS_TAG_INSTALLER, "save delete +old -bundle %{public}s exception failed, error is %{public}d",
            bundleName.c_str(), result);
    }
    // rename first
    std::string oldAppCodePath = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR +
        std::string(ServiceConstants::BUNDLE_OLD_CODE_DIR) + bundleName;
    std::string tempPath = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR +
        std::string(ServiceConstants::BUNDLE_TEMP_CODE_DIR) + bundleName;
    result = InstalldClient::GetInstance()->RenameModuleDir(oldAppCodePath, tempPath);
    if (result == ERR_OK) {
        // delete +temp- code path
        result = InstalldClient::GetInstance()->RemoveDir(tempPath);
    } else {
        LOG_NOFUNC_E(BMS_TAG_INSTALLER, "rename bundle %{public}s old to temp path error is %{public}d",
            bundleName.c_str(), result);
        result = InstalldClient::GetInstance()->RemoveDir(oldAppCodePath);
    }
    if (result == ERR_OK) {
        result = DelayedSingleton<InstallExceptionMgr>::GetInstance()->DeleteBundleExceptionInfo(bundleName);
    } else {
        LOG_NOFUNC_E(BMS_TAG_INSTALLER, "remove bundle %{public}s old code path error is %{public}d",
            bundleName.c_str(), result);
    }
    if (result != ERR_OK) {
        LOG_NOFUNC_E(BMS_TAG_INSTALLER, "delete bundle %{public}s exception error is %{public}d",
            bundleName.c_str(), result);
    }
    LOG_NOFUNC_I(BMS_TAG_INSTALLER, "ProcessOldCodePath end -n %{public}s", bundleName.c_str());
}

void BaseBundleInstaller::RollbackCodePath(const std::string &bundleName, bool isBundleUpdate)
{
    if (!isBundleUpdate) {
        return;
    }
    bool result = DelayedSingleton<InstallExceptionMgr>::GetInstance()->HandleBundleExceptionInfo(bundleName);
    if (!result) {
        LOG_NOFUNC_I(BMS_TAG_INSTALLER, "Rollback codePath failed -n %{public}s", bundleName.c_str());
        // check real path whether exist
        std::string realPath = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR +
            bundleName;
        if (!BundleUtil::IsExistDirNoLog(realPath)) {
            LOG_NOFUNC_E(BMS_TAG_INSTALLER, "real codePath not exist -n %{public}s -e %{public}d",
                bundleName.c_str(), errno);
        }
    }
}

void BaseBundleInstaller::InnerProcessTargetSoPath(const InnerBundleInfo &info, const bool isBundleUpdate,
    const std::string &modulePath, std::string &nativeLibraryPath, std::string &targetSoPath)
{
    bool isLibIsolated = info.IsLibIsolated(info.GetCurModuleName());
    // extract so file: if hap so is not isolated, then extract so to tmp path.
    if (isLibIsolated) {
        if (BundleUtil::EndWith(modulePath, ServiceConstants::TMP_SUFFIX)) {
            nativeLibraryPath = BuildTempNativeLibraryPath(nativeLibraryPath);
        }
    } else {
        nativeLibraryPath = info.GetCurrentModulePackage() + ServiceConstants::TMP_SUFFIX +
            ServiceConstants::PATH_SEPARATOR + nativeLibraryPath;
    }
    if (isBundleUpdate) {
        targetSoPath.append(Constants::BUNDLE_CODE_DIR).append(ServiceConstants::PATH_SEPARATOR)
            .append(ServiceConstants::BUNDLE_NEW_CODE_DIR).append(info.GetBundleName())
            .append(ServiceConstants::PATH_SEPARATOR).append(nativeLibraryPath)
            .append(ServiceConstants::PATH_SEPARATOR);
    } else {
        targetSoPath.append(Constants::BUNDLE_CODE_DIR).append(ServiceConstants::PATH_SEPARATOR)
            .append(info.GetBundleName()).append(ServiceConstants::PATH_SEPARATOR).append(nativeLibraryPath)
            .append(ServiceConstants::PATH_SEPARATOR);
    }
}

ErrCode BaseBundleInstaller::RecoverOnDemandInstallBundle(const std::string &bundleName,
    const InstallParam &installParam, int32_t &uid)
{
    LOG_I(BMS_TAG_INSTALLER, "recover on demand install bundle -n: %{public}s", bundleName.c_str());
    PreInstallBundleInfo preInstallBundleInfo;
    preInstallBundleInfo.SetBundleName(bundleName);
    if (!OnDemandInstallDataMgr::GetInstance().GetOnDemandInstallBundleInfo(bundleName, preInstallBundleInfo)
        || preInstallBundleInfo.GetBundlePaths().empty()) {
        LOG_E(BMS_TAG_INSTALLER, "Get onDemandBundle failed, -n: %{public}s", bundleName.c_str());
        return ERR_APPEXECFWK_RECOVER_INVALID_BUNDLE_NAME;
    }
    LOG_D(BMS_TAG_INSTALLER, "Get on demand bundle success");
    std::vector<std::string> pathVec = preInstallBundleInfo.GetBundlePaths();
    auto innerInstallParam = installParam;
    innerInstallParam.needSavePreInstallInfo = true;
    innerInstallParam.isPreInstallApp = true;
    innerInstallParam.removable = true;
    innerInstallParam.preinstallSourceFlag = ApplicationInfoFlag::FLAG_PREINSTALLED_APP;
    innerInstallParam.isDataPreloadHap = IsDataPreloadHap(pathVec.empty() ? "" : pathVec.front());
    if (!innerInstallParam.isDataPreloadHap) {
        innerInstallParam.copyHapToInstallPath = false;
    }
    ErrCode result = ProcessBundleInstall(pathVec, innerInstallParam, preInstallBundleInfo.GetAppType(), uid, true);
    if (result == ERR_OK) {
        OnDemandInstallDataMgr::GetInstance().DeleteOnDemandInstallBundleInfo(bundleName);
    }
    return result;
}

bool BaseBundleInstaller::ProcessExtProfile(const InstallParam &installParam)
{
    auto extProfileDir = std::string(Constants::BUNDLE_CODE_DIR) + ServiceConstants::PATH_SEPARATOR + bundleName_
        + ServiceConstants::PATH_SEPARATOR + ServiceConstants::EXT_PROFILE;
    std::string targetPath = extProfileDir + ServiceConstants::PATH_SEPARATOR + ServiceConstants::MANIFEST_JSON;
    auto iter = installParam.parameters.find(ServiceConstants::ENTERPRISE_MANIFEST);
    if ((iter == installParam.parameters.end()) || (iter->second.empty())) {
        bool isExtProfileExist = false;
        InstalldClient::GetInstance()->IsExistDir(extProfileDir, isExtProfileExist);
        if (isExtProfileExist) {
            if (RemoveModuleDir(targetPath) != ERR_OK) {
                LOG_E(BMS_TAG_INSTALLER, "fail to delete ext profile file, error is %{public}d", errno);
                return false;
            }
        }
        return true;
    }
    LOG_I(BMS_TAG_INSTALLER, "create ext profile dir %{public}s", extProfileDir.c_str());
    ErrCode result = InstalldClient::GetInstance()->CreateBundleDir(extProfileDir);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "fail to create ext profile dir, error is %{public}d", result);
        return false;
    }
    if (InstalldClient::GetInstance()->CopyFile(iter->second, targetPath) != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "copy file from %{public}s to %{public}s failed", iter->second.c_str(),
            targetPath.c_str());
        return false;
    }
    return true;
}

void BaseBundleInstaller::SetHybridSpawn()
{
    if (!InitDataMgr()) {
        return;
    }
    InnerBundleInfo info;
    if (!dataMgr_->FetchInnerBundleInfo(bundleName_, info)) {
        LOG_E(BMS_TAG_INSTALLER, "Get innerBundleInfo failed");
        return;
    }
    std::string arkTSMode = info.GetApplicationArkTSMode();
    if (arkTSMode == Constants::ARKTS_MODE_STATIC || arkTSMode == Constants::ARKTS_MODE_HYBRID) {
        if (OHOS::system::GetBoolParameter(ServiceConstants::HYBRID_SPAWN_UNIFIED, false)) {
            LOG_D(BMS_TAG_INSTALLER, "persist.appspawn.hybridspawn.unified is true");
            return;
        }
        LOG_I(BMS_TAG_INSTALLER, "set persist.appspawn.hybridspawn.enable true");
        OHOS::system::SetParameter(ServiceConstants::HYBRID_SPAWN_ENABLE, BMS_TRUE);
    }
}

bool BaseBundleInstaller::CheckAddResultMsg(const InnerBundleInfo &info, bool isContainEntry)
{
    if (!isContainEntry) {
        if (info.GetModuleDeduplicateHar()) {
            SetCheckResultMsg(DEDUPLICATEHAR_NOTE);
            return true;
        }
    }
    return false;
}

bool BaseBundleInstaller::IsBundleCrossAppSharedConfig(const std::unordered_map<std::string, InnerBundleInfo> &newInfos)
{
    for (const auto &item : newInfos) {
        if (item.second.IsBundleCrossAppSharedConfig()) {
            return true;
        }
    }
    return false;
}

void BaseBundleInstaller::SetAPIAndSdkVersions(int32_t targetAPIVersion,
    uint32_t minAPIVersion, const std::string &compileSdkVersion)
{
    sysEventInfo_.minAPIVersion = minAPIVersion;
    sysEventInfo_.targetAPIVersion = targetAPIVersion;
    sysEventInfo_.compileSdkVersion = compileSdkVersion;
}

void BaseBundleInstaller::SetUid(int32_t uid)
{
    sysEventInfo_.uid = uid;
}

void BaseBundleInstaller::InnerProcessNewBundleDataDir(const bool isOta,
    const InnerBundleInfo &oldBundleInfo, const InnerBundleInfo &newBundleInfo)
{
    if (!isOta || !isAppExist_) {
        return;
    }
    uint32_t dirType = static_cast<uint32_t>(CreateBundleDirType::CREATE_NONE_DIR);
    if (!oldBundleInfo.NeedCreateEl5Dir() && newBundleInfo.NeedCreateEl5Dir()) {
        dirType |= static_cast<uint32_t>(CreateBundleDirType::CREATE_EL5_DIR);
    }
    auto newDataGroup = newBundleInfo.GetDataGroupInfos();
    if (!newDataGroup.empty()) {
        auto oldDataGroup = oldBundleInfo.GetDataGroupInfos();
        for (const auto &item : newDataGroup) {
            if (oldDataGroup.find(item.first) == oldDataGroup.end()) {
                dirType |= static_cast<uint32_t>(CreateBundleDirType::CREATE_GROUP_DIR);
                break;
            }
        }
    }
    if (dirType == static_cast<uint32_t>(CreateBundleDirType::CREATE_NONE_DIR)) {
        return;
    }
    auto newBundleDirMgr = DelayedSingleton<NewBundleDataDirMgr>::GetInstance();
    if (newBundleDirMgr != nullptr) {
        (void)newBundleDirMgr->AddNewBundleDirInfo(newBundleInfo.GetBundleName(), dirType);
    }
}

void BaseBundleInstaller::StopRelable(const InnerBundleInfo &info)
{
    if (!OHOS::system::GetBoolParameter(ServiceConstants::BMS_RELABEL_PARAM, false)) {
        return;
    }
    CreateDirParam param;
    param.bundleName = info.GetBundleName();
    param.uid = info.GetUid(userId_);
    param.debug = info.GetBaseApplicationInfo().appProvisionType == Constants::APP_PROVISION_TYPE_DEBUG;
    param.apl = info.GetAppPrivilegeLevel();
    param.isPreInstallApp = info.IsPreInstallApp();
    param.stopReason = "ProcessBundleUninstall";
    InstalldClient::GetInstance()->StopSetFileCon(param, ServiceConstants::StopReason::DELETE);
}

void BaseBundleInstaller::SetIsAbcCompressed()
{
    sysEventInfo_.isAbcCompressed = bundleInstallChecker_->GetIsAbcCompressed();
}

bool BaseBundleInstaller::AddInstallingBundleName(const InstallParam &installParam)
{
    if (installParam.isOTA || otaInstall_) {
        return false;
    }
    if (!InitDataMgr()) {
        return false;
    }
    if (!bundleName_.empty()) {
        dataMgr_->AddInstallingBundleName(bundleName_, installParam.userId);
    }
    auto iter = installParam.parameters.find(ServiceConstants::BMS_PARA_INSTALL_BUNDLE_NAME);
    if (iter == installParam.parameters.end()) {
        return false;
    }
    if (iter->second != bundleName_) {
        dataMgr_->AddInstallingBundleName(iter->second, installParam.userId);
    }
    return true;
}

bool BaseBundleInstaller::DeleteInstallingBundleName(const InstallParam &installParam)
{
    if (installParam.isOTA || otaInstall_) {
        return false;
    }
    if (!InitDataMgr()) {
        return false;
    }
    if (!bundleName_.empty()) {
        dataMgr_->DeleteInstallingBundleName(bundleName_, installParam.userId);
    }
    auto iter = installParam.parameters.find(ServiceConstants::BMS_PARA_INSTALL_BUNDLE_NAME);
    if (iter == installParam.parameters.end()) {
        return false;
    }
    if (iter->second != bundleName_) {
        dataMgr_->DeleteInstallingBundleName(iter->second, installParam.userId);
    }
    return true;
}

void BaseBundleInstaller::NotifyBundleCallback(const NotifyType &type, int32_t uid)
{
    NotifyBundleEvents event = {
        .type = type,
        .uid = uid,
        .bundleType = static_cast<int32_t>(BundleType::APP),
        .bundleName = bundleName_,
        .modulePackage = moduleName_,
    };
    std::shared_ptr<BundleCommonEventMgr> commonEventMgr = std::make_shared<BundleCommonEventMgr>();
    commonEventMgr->NotifyPluginEvents(event, dataMgr_, true);
}
}  // namespace AppExecFwk
}  // namespace OHOS
