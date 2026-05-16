/*
 * Copyright (c) 2021-2025 Huawei Device Co., Ltd.
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

#include "bundle_data_mgr.h"
#ifdef GLOBAL_RESMGR_ENABLE
#include <fstream>  // adapter project: iconPath fallback
#endif

#include <algorithm>
#include <sys/stat.h>
#include <tuple>

#ifdef BUNDLE_FRAMEWORK_FREE_INSTALL
#ifdef ACCOUNT_ENABLE
#include "os_account_info.h"
#endif
#endif
#include "ability_manager_helper.h"
#include "accesstoken_kit.h"
#include "account_helper.h"
#include "app_install_extended_info.h"
#include "app_log_tag_wrapper.h"
#include "app_provision_info_manager.h"
#include "bms_extension_client.h"
#include "bms_update_selinux_mgr.h"
#include "bundle_common_event_mgr.h"
#include "bundle_data_storage_rdb.h"
#include "preinstall_data_storage_rdb.h"
#include "hap_token_info.h"
#include "bundle_event_callback_death_recipient.h"
#include "bundle_mgr_service.h"
#include "bundle_mgr_client.h"
#include "bundle_parser.h"
#include "bundle_permission_mgr.h"
#include "bundle_status_callback_death_recipient.h"
#include "ipc/create_dir_param.h"
#ifdef CONFIG_POLOCY_ENABLE
#include "config_policy_utils.h"
#endif
#ifdef BUNDLE_FRAMEWORK_DEFAULT_APP
#include "default_app_mgr.h"
#endif
#include "event_report.h"
#include "hitrace_meter.h"
#include "inner_bundle_clone_common.h"
#include "installd_client.h"
#include "interfaces/hap_verify.h"
#include "ipc_skeleton.h"
#ifdef GLOBAL_I18_ENABLE
#include "locale_config.h"
#include "locale_info.h"
#endif
#include "mime_type_mgr.h"
#include "parameters.h"
#include "router_map_helper.h"
#ifdef BUNDLE_FRAMEWORK_OVERLAY_INSTALLATION
#include "bundle_overlay_data_manager.h"
#endif
#include "bundle_extractor.h"
#include "parameter.h"
#include "scope_guard.h"
#ifdef BUNDLE_FRAMEWORK_UDMF_ENABLED
#include "type_descriptor.h"
#include "utd_client.h"
#endif

#ifdef APP_DOMAIN_VERIFY_ENABLED
#include "app_domain_verify_mgr_client.h"
#endif

#include "router_data_storage_rdb.h"
#include "shortcut_data_storage_rdb.h"
#include "system_ability_definition.h"
#include "system_ability_helper.h"
#include "ohos_account_kits.h"
#include "xcollie_helper.h"

namespace OHOS {
namespace AppExecFwk {
namespace {
constexpr int MAX_EVENT_CALL_BACK_SIZE = 100;
constexpr int8_t DATA_GROUP_INDEX_START = 1;
constexpr int8_t PROFILE_PREFIX_LENGTH = 9;
constexpr int32_t MAX_PLUGIN_CALLBACK_SIZE = 256;
constexpr uint16_t UUID_LENGTH_MAX = 512;
constexpr const char* GLOBAL_RESOURCE_BUNDLE_NAME = "ohos.global.systemres";
// freeInstall action
constexpr const char* FREE_INSTALL_ACTION = "ohos.want.action.hapFreeInstall";
// share action
constexpr const char* SHARE_ACTION = "ohos.want.action.sendData";
constexpr const char* WANT_PARAM_PICKER_SUMMARY = "ability.picker.summary";
constexpr const char* SUMMARY_TOTAL_COUNT = "totalCount";
constexpr const char* WANT_PARAM_SUMMARY = "summary";
constexpr int8_t DEFAULT_SUMMARY_COUNT = 0;
// data share
constexpr const char* DATA_PROXY_URI_PREFIX = "datashareproxy://";
constexpr int8_t DATA_PROXY_URI_PREFIX_LEN = 17;
// profile path
constexpr const char* INTENT_PROFILE_PATH = "resources/base/profile/insight_intent.json";
constexpr const char* NETWORK_PROFILE_PATH = "resources/base/profile/network_config.json";
constexpr const char* ADDITION_PROFILE_PATH = "resources/base/profile/addition.json";
constexpr const char* UTD_SDT_PROFILE_PATH = "resources/rawfile/arkdata/utd/utd.json5";
constexpr const char* PKG_CONTEXT_PROFILE_PATH = "pkgContextInfo.json";
constexpr const char* FILE_ICON_PROFILE_PATH = "resources/base/profile/file_icon.json";
constexpr const char* INSIGHT_INTENT_PROFILE_PATH = "resources/base/profile/extract_insight_intent.json";
constexpr const char* PROFILE_PATH = "resources/base/profile/";
constexpr const char* PROFILE_PREFIX = "$profile:";
constexpr const char* JSON_SUFFIX = ".json";
constexpr const char* SCHEME_HTTPS = "https";
constexpr const char* META_DATA_SHORTCUTS_NAME = "ohos.ability.shortcuts";
constexpr const char* BMS_EVENT_ADDITIONAL_INFO_CHANGED = "bms.event.ADDITIONAL_INFO_CHANGED";
constexpr const char* CLONE_BUNDLE_PREFIX = "clone_";
constexpr const char* RESOURCE_STRING_PREFIX = "$string:";
constexpr const char* EMPTY_STRING = "";
constexpr const char* SHORTCUT_OPERATION_CREATE = "ADD";
constexpr const char* SHORTCUT_OPERATION_DELETE = "DEL";
constexpr const char* EXTEND_DATASIZE_PATH_SUFFIX = "/etc/hap_extend_datasize_ralations.json";
constexpr const char* HAP_EXTEND_DATASIZE_RELATIONS = "hap.extend.datasize.ralations";
constexpr const char* BUNDLE_NAME_KEY = "bundle_name";
constexpr const char* SA_UID = "sa_uid";

const std::map<ProfileType, const char*> PROFILE_TYPE_MAP = {
    { ProfileType::INTENT_PROFILE, INTENT_PROFILE_PATH },
    { ProfileType::ADDITION_PROFILE, ADDITION_PROFILE_PATH},
    { ProfileType::NETWORK_PROFILE, NETWORK_PROFILE_PATH },
    { ProfileType::UTD_SDT_PROFILE, UTD_SDT_PROFILE_PATH },
    { ProfileType::PKG_CONTEXT_PROFILE, PKG_CONTEXT_PROFILE_PATH },
    { ProfileType::FILE_ICON_PROFILE, FILE_ICON_PROFILE_PATH },
    { ProfileType::INSIGHT_INTENT_PROFILE, INSIGHT_INTENT_PROFILE_PATH },
    { ProfileType::CLOUD_PROFILE, ServiceConstants::CLOUD_PROFILE_PATH },
    { ProfileType::EASY_GO_PROFILE, EMPTY_STRING },
    { ProfileType::SHARE_FILES_PROFILE, EMPTY_STRING },
};
const std::vector<ProfileType> PROFILE_TYPES = {
    ProfileType::EASY_GO_PROFILE,
    ProfileType::SHARE_FILES_PROFILE,
};
const std::string SCHEME_END = "://";
const std::string LINK_FEATURE = "linkFeature";
const std::string ATOMIC_SERVICE_DIR_PREFIX = "+auid-";
const std::string CLONE_APP_DIR_PREFIX = "+clone-";
const std::string PLUS = "+";
constexpr const char* PARAM_URI_SEPARATOR = ":///";
constexpr const char* URI_SEPARATOR = "://";
constexpr uint8_t PARAM_URI_SEPARATOR_LEN = 4;
constexpr int8_t INVALID_BUNDLEID = -1;
constexpr int32_t DATA_GROUP_UID_OFFSET = 100000;
constexpr int32_t MAX_APP_UID = 65535;
constexpr int8_t ONLY_ONE_USER = 1;
constexpr unsigned int OTA_CODE_ENCRYPTION_TIMEOUT = 4 * 60;
const std::string FUNCATION_HANDLE_OTA_CODE_ENCRYPTION = "BundleDataMgr::HandleOTACodeEncryption()";
const std::string BUNDLE_NAME = "BUNDLE_NAME";
const std::string USER_ID = "USER_ID";
const std::string APP_INDEX = "APP_INDEX";
#ifndef BUNDLE_FRAMEWORK_FREE_INSTALL
constexpr int APP_MGR_SERVICE_ID = 501;
#endif
#ifdef GLOBAL_RESMGR_ENABLE
const std::string DOT_SEPARATOR = ".";
const std::string COLON_SEPARATOR = ":";
const std::string PREFIX_APP = "app.";
const std::string PREFIX_DOLLAR = "$";
const std::string FIELD_MEDIA = "media";
const std::string FIELD_STRING = "string";
#endif
}

BundleDataMgr::BundleDataMgr()
{
    InitStateTransferMap();
    dataStorage_ = std::make_shared<BundleDataStorageRdb>();
    preInstallDataStorage_ = std::make_shared<PreInstallDataStorageRdb>();
    sandboxAppHelper_ = DelayedSingleton<BundleSandboxAppHelper>::GetInstance();
    bundleStateStorage_ = std::make_shared<BundleStateStorage>();
    shortcutStorage_ = std::make_shared<ShortcutDataStorageRdb>();
    shortcutVisibleStorage_ = std::make_shared<ShortcutVisibleDataStorageRdb>();
    shortcutEnabledStorage_ = std::make_shared<ShortcutEnabledDataStorageRdb>();
    routerStorage_ = std::make_shared<RouterDataStorageRdb>();
    uninstallDataMgr_ = std::make_shared<UninstallDataMgrStorageRdb>();
    firstInstallDataMgr_ = std::make_shared<FirstInstallDataMgrStorageRdb>();
    baseAppUid_ = system::GetIntParameter<int32_t>("const.product.baseappid", Constants::BASE_APP_UID);
    if (baseAppUid_ < Constants::BASE_APP_UID || baseAppUid_ >= MAX_APP_UID) {
        baseAppUid_ = Constants::BASE_APP_UID;
    }
    APP_LOGI("BundleDataMgr instance is created");
}

BundleDataMgr::~BundleDataMgr()
{
    APP_LOGI("BundleDataMgr instance is destroyed");
    installStates_.clear();
    transferStates_.clear();
    bundleInfos_.clear();
}

void BundleDataMgr::DefragMemory()
{
    APP_LOGI_NOFUNC("start to defrag bundleInfos memory");
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    std::map<std::string, InnerBundleInfo> compactedMap(bundleInfos_);
    bundleInfos_.swap(compactedMap);
    APP_LOGI_NOFUNC("defrag bundleInfos memory done, size: %{public}zu", bundleInfos_.size());
}

bool BundleDataMgr::LoadDataFromPersistentStorage()
{
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    // Judge whether bundleState json db exists.
    // If it does not exist, create it and return the judgment result.
    bool bundleStateDbExist = bundleStateStorage_->HasBundleUserInfoJsonDb();
    if (!dataStorage_->LoadAllData(bundleInfos_)) {
        APP_LOGW("LoadAllData failed");
        return false;
    }

    if (bundleInfos_.empty()) {
        APP_LOGW("persistent data is empty");
        return false;
    }

    for (const auto &item : bundleInfos_) {
        std::lock_guard<std::mutex> stateLock(stateMutex_);
        installStates_.emplace(item.first, InstallState::INSTALL_SUCCESS);
        AddAppHspBundleName(item.second.GetApplicationBundleType(), item.first);
    }

    RestoreUidAndGid();
    if (!bundleStateDbExist) {
        // Compatible old bundle status in kV db
        CompatibleOldBundleStateInKvDb();
    } else {
        ResetBundleStateData();
        // Load all bundle status from json db.
        LoadAllBundleStateDataFromJsonDb();
    }

    SetInitialUserFlag(true);

    RestoreSandboxUidAndGid(bundleIdMap_);
    return true;
}

void BundleDataMgr::CompatibleOldBundleStateInKvDb()
{
    for (const auto& bundleInfoItem : bundleInfos_) {
        for (auto& innerBundleUserInfoItem : bundleInfoItem.second.GetInnerBundleUserInfos()) {
            auto& bundleUserInfo = innerBundleUserInfoItem.second.bundleUserInfo;
            if (bundleUserInfo.IsInitialState()) {
                continue;
            }

            // save old bundle state to json db
            bundleStateStorage_->SaveBundleStateStorage(
                bundleInfoItem.first, bundleUserInfo.userId, bundleUserInfo);
        }
    }
}

void BundleDataMgr::LoadAllBundleStateDataFromJsonDb()
{
    APP_LOGD("Load all bundle state start");
    std::map<std::string, std::map<int32_t, BundleUserInfo>> bundleStateInfos;
    if (!bundleStateStorage_->LoadAllBundleStateData(bundleStateInfos) || bundleStateInfos.empty()) {
        APP_LOGW("Load all bundle state failed");
        return;
    }

    for (const auto& bundleState : bundleStateInfos) {
        auto infoItem = bundleInfos_.find(bundleState.first);
        if (infoItem == bundleInfos_.end()) {
            APP_LOGW("BundleName(%{public}s) not exist in cache", bundleState.first.c_str());
            continue;
        }

        InnerBundleInfo& newInfo = infoItem->second;
        for (auto& bundleUserState : bundleState.second) {
            auto& tempUserInfo = bundleUserState.second;
            newInfo.SetApplicationEnabled(tempUserInfo.enabled, bundleUserState.second.setEnabledCaller,
                bundleUserState.first);
            for (auto& disabledAbility : tempUserInfo.disabledAbilities) {
                newInfo.SetAbilityEnabled("", disabledAbility, false, bundleUserState.first);
            }
        }
    }

    APP_LOGD("Load all bundle state end");
}

void BundleDataMgr::ResetBundleStateData()
{
    for (auto& bundleInfoItem : bundleInfos_) {
        bundleInfoItem.second.ResetBundleState(Constants::ALL_USERID);
    }
}

bool BundleDataMgr::UpdateBundleInstallState(const std::string &bundleName,
    const InstallState state, const bool isKeepData)
{
    if (bundleName.empty()) {
        APP_LOGW("update failed: bundle name is empty");
        return false;
    }

    // always keep lock bundleInfoMutex_ before locking stateMutex_ to avoid deadlock
    std::unique_lock<std::shared_mutex> lck(bundleInfoMutex_);
    std::lock_guard<std::mutex> lock(stateMutex_);
    auto item = installStates_.find(bundleName);
    if (item == installStates_.end()) {
        if (state == InstallState::INSTALL_START) {
            installStates_.emplace(bundleName, state);
            APP_LOGD("update succeed");
            return true;
        }
        APP_LOGW_NOFUNC("update failed: incorrect state -n: %{public}s", bundleName.c_str());
        return false;
    }

    auto stateRange = transferStates_.equal_range(state);
    for (auto previousState = stateRange.first; previousState != stateRange.second; ++previousState) {
        if (item->second == previousState->second) {
            APP_LOGD("update succeed, current:%{public}d, state:%{public}d",
                static_cast<int32_t>(previousState->second), static_cast<int32_t>(state));
            if (IsDeleteDataState(state)) {
                installStates_.erase(item);
                DeleteBundleInfo(bundleName, state, isKeepData);
                return true;
            }
            item->second = state;
            return true;
        }
    }
    APP_LOGW_NOFUNC("UpdateBundleInstallState -n %{public}s fail current:%{public}d state:%{public}d",
        bundleName.c_str(), static_cast<int32_t>(item->second), static_cast<int32_t>(state));
    return false;
}

bool BundleDataMgr::AddInnerBundleInfo(const std::string &bundleName, InnerBundleInfo &info, bool checkStatus)
{
    APP_LOGD("to save info:%{public}s", info.GetBundleName().c_str());
    if (bundleName.empty()) {
        APP_LOGW("save info fail, empty bundle name");
        return false;
    }

    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem != bundleInfos_.end()) {
        APP_LOGW("bundleName: %{public}s : bundle info already exist", bundleName.c_str());
        return false;
    }

    std::lock_guard<std::mutex> stateLock(stateMutex_);
    auto statusItem = installStates_.find(bundleName);
    if (statusItem == installStates_.end()) {
        APP_LOGW("save info fail, bundleName:%{public}s is not installed", bundleName.c_str());
        return false;
    }
    std::string developerId = info.GetDeveloperId();
    if (!developerId.empty()) {
        // Read lastOdid from FirstInstallBundleInfo using ALL_USERID to make ODID reset count
        // independent of userId, only associated with bundleName
        std::string lastOdid;
        FirstInstallBundleInfo lastOdidInfo;
        if (GetFirstInstallBundleInfo(bundleName, Constants::ALL_USERID, lastOdidInfo)) {
            lastOdid = lastOdidInfo.lastOdid;
        }

        std::string odid = GenerateOdidNoLock(developerId);
        info.UpdateOdid(developerId, odid);
        // Increment odid reset count when generating new odid different from last odid
        if (!lastOdid.empty() && !odid.empty() && odid != lastOdid) {
            lastOdidInfo.IncrementOdidResetCount();
            APP_LOGI("odid reset for bundle %{public}s, last odid:%{private}s, new odid:%{private}s, count:%{public}d",
                bundleName.c_str(), lastOdid.c_str(), odid.c_str(), lastOdidInfo.odidResetCount);
            firstInstallDataMgr_->AddFirstInstallBundleInfo(bundleName, Constants::ALL_USERID, lastOdidInfo);
        }
    }
    if (!checkStatus || statusItem->second == InstallState::INSTALL_START) {
        APP_LOGD("save bundle:%{public}s info", bundleName.c_str());
        if (info.GetBaseApplicationInfo().needAppDetail) {
            AddAppDetailAbilityInfo(info);
        }
#ifdef BUNDLE_FRAMEWORK_OVERLAY_INSTALLATION
        if (info.GetOverlayType() == OVERLAY_EXTERNAL_BUNDLE) {
            InnerBundleInfo newInfo = info;
            std::string targetBundleName = newInfo.GetTargetBundleName();
            auto targetInfoItem = bundleInfos_.find(targetBundleName);
            if (targetInfoItem != bundleInfos_.end()) {
                OverlayDataMgr::GetInstance()->UpdateExternalOverlayInfo(newInfo, info, targetInfoItem->second);
                // storage target bundle info
                dataStorage_->SaveStorageBundleInfo(targetInfoItem->second);
            }
        }
        if (info.GetOverlayType() == OVERLAY_INTERNAL_BUNDLE) {
            int32_t overlayModuleState = OverlayState::OVERLAY_INVALID;
            (void)info.GetOverlayModuleState(info.GetCurrentModulePackage(), info.GetUserId(), overlayModuleState);
            info.SetOverlayModuleState(info.GetCurrentModulePackage(), overlayModuleState, info.GetUserId());
        }
        if (info.GetOverlayType() == NON_OVERLAY_TYPE) {
            // build overlay connection for external overlay
            BuildExternalOverlayConnection(info.GetCurrentModulePackage(), info, info.GetUserId());
        }
#endif
        bundleInfos_.emplace(bundleName, info);
        AddAppHspBundleName(info.GetApplicationBundleType(), bundleName);
        return true;
    }
    return false;
}

bool BundleDataMgr::AddNewModuleInfo(
    const std::string &bundleName, const InnerBundleInfo &newInfo, InnerBundleInfo &oldInfo)
{
    LOG_I(BMS_TAG_DEFAULT, "addInfo:%{public}s", newInfo.GetCurrentModulePackage().c_str());
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("bundleName: %{public}s : bundle info not exist", bundleName.c_str());
        return false;
    }
    std::lock_guard<std::mutex> stateLock(stateMutex_);
    auto statusItem = installStates_.find(bundleName);
    if (statusItem == installStates_.end()) {
        APP_LOGW("save info fail, app:%{public}s is not updated", bundleName.c_str());
        return false;
    }
    if (statusItem->second == InstallState::UPDATING_SUCCESS) {
        if (AddNewModuleInfo(newInfo, oldInfo)) {
            bundleInfos_.at(bundleName) = oldInfo;
            return true;
        }
    }
    return false;
}

void BundleDataMgr::UpdateBaseBundleInfoIntoOld(const InnerBundleInfo &newInfo, InnerBundleInfo &oldInfo)
{
    oldInfo.UpdateBaseBundleInfo(newInfo.GetBaseBundleInfo(), newInfo.HasEntry());
    oldInfo.UpdateBaseApplicationInfo(newInfo);
    oldInfo.UpdateRemovable(newInfo.IsPreInstallApp(), newInfo.IsRemovable());
    oldInfo.UpdateMultiAppMode(newInfo);
    oldInfo.UpdateReleaseType(newInfo);
    oldInfo.SetAppType(newInfo.GetAppType());
    oldInfo.SetAppFeature(newInfo.GetAppFeature());
}

bool BundleDataMgr::AddNewModuleInfo(const InnerBundleInfo &newInfo, InnerBundleInfo &oldInfo)
{
    APP_LOGD("save bundle:%{public}s info", oldInfo.GetBundleName().c_str());
    ProcessAllowedAcls(newInfo, oldInfo);
    if (IsUpdateInnerBundleInfoSatisified(oldInfo, newInfo)) {
        UpdateBaseBundleInfoIntoOld(newInfo, oldInfo);
    }
    if (oldInfo.GetOldAppIds().empty()) {
        oldInfo.AddOldAppId(oldInfo.GetAppId());
    }
    oldInfo.SetProvisionId(newInfo.GetProvisionId());
    oldInfo.SetCertificateFingerprint(newInfo.GetCertificateFingerprint());
    oldInfo.SetAppIdentifier(newInfo.GetAppIdentifier());
    oldInfo.AddOldAppId(newInfo.GetAppId());
    oldInfo.SetAppPrivilegeLevel(newInfo.GetAppPrivilegeLevel());
    oldInfo.UpdateNativeLibAttrs(newInfo.GetBaseApplicationInfo());
    oldInfo.UpdateArkNativeAttrs(newInfo.GetBaseApplicationInfo());
    oldInfo.SetAsanLogPath(newInfo.GetAsanLogPath());
    oldInfo.SetBundlePackInfo(newInfo.GetBundlePackInfo());
    oldInfo.AddModuleInfo(newInfo);
    oldInfo.UpdateAppDetailAbilityAttrs();
    if (oldInfo.GetBaseApplicationInfo().needAppDetail) {
        AddAppDetailAbilityInfo(oldInfo);
    }
    oldInfo.SetBundleStatus(InnerBundleInfo::BundleStatus::ENABLED);
    oldInfo.SetIsNewVersion(newInfo.GetIsNewVersion());
    oldInfo.UpdateOdidByBundleInfo(newInfo);
    oldInfo.SetDFXParamStatus();
    oldInfo.SetInstalledForAllUser(newInfo.IsInstalledForAllUser());
#ifdef BUNDLE_FRAMEWORK_OVERLAY_INSTALLATION
    if ((oldInfo.GetOverlayType() == NON_OVERLAY_TYPE) && (newInfo.GetOverlayType() != NON_OVERLAY_TYPE)) {
        oldInfo.SetOverlayType(newInfo.GetOverlayType());
    }
    if (!UpdateOverlayInfo(newInfo, oldInfo)) {
        APP_LOGD("bundleName: %{public}s : update overlay info failed", oldInfo.GetBundleName().c_str());
        return false;
    }
#endif
    APP_LOGD("update storage success bundle:%{public}s", oldInfo.GetBundleName().c_str());
    return true;
}

bool BundleDataMgr::RemoveModuleInfo(
    const std::string &bundleName, const std::string &modulePackage, InnerBundleInfo &oldInfo, bool needSaveStorage)
{
    APP_LOGD("remove module info:%{public}s/%{public}s", bundleName.c_str(), modulePackage.c_str());
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("bundleName: %{public}s bundle info not exist", bundleName.c_str());
        return false;
    }
    std::lock_guard<std::mutex> stateLock(stateMutex_);
    auto statusItem = installStates_.find(bundleName);
    if (statusItem == installStates_.end()) {
        APP_LOGW("save info fail, app:%{public}s is not updated", bundleName.c_str());
        return false;
    }
    if (statusItem->second == InstallState::UNINSTALL_START || statusItem->second == InstallState::ROLL_BACK) {
        APP_LOGD("save bundle:%{public}s info", bundleName.c_str());
#ifdef BUNDLE_FRAMEWORK_OVERLAY_INSTALLATION
        std::string targetBundleName = oldInfo.GetTargetBundleName();
        InnerBundleInfo targetInnerBundleInfo;
        if (bundleInfos_.find(targetBundleName) != bundleInfos_.end()) {
            targetInnerBundleInfo = bundleInfos_.at(targetBundleName);
        }
        OverlayDataMgr::GetInstance()->RemoveOverlayModuleInfo(bundleName, modulePackage, oldInfo,
            targetInnerBundleInfo);
        if ((oldInfo.GetOverlayType() == OVERLAY_EXTERNAL_BUNDLE) && !targetInnerBundleInfo.GetBundleName().empty()) {
            // save target innerBundleInfo
            if (dataStorage_->SaveStorageBundleInfo(targetInnerBundleInfo)) {
                APP_LOGD("update storage success bundle:%{public}s", targetBundleName.c_str());
                bundleInfos_.at(targetBundleName) = targetInnerBundleInfo;
            }
        }
        // remove target module and overlay module state will change to OVERLAY_INVALID
        if (oldInfo.GetOverlayType() == NON_OVERLAY_TYPE) {
            ResetExternalOverlayModuleState(bundleName, modulePackage);
        }
#endif
        oldInfo.RemoveModuleInfo(modulePackage);
        oldInfo.SetBundleStatus(InnerBundleInfo::BundleStatus::ENABLED);
        if (!oldInfo.isExistedOverlayModule()) {
            oldInfo.SetOverlayType(NON_OVERLAY_TYPE);
        }
        oldInfo.SetDFXParamStatus();
        if (needSaveStorage && !dataStorage_->SaveStorageBundleInfo(oldInfo)) {
            APP_LOGE("update storage failed bundle:%{public}s", bundleName.c_str());
            return false;
        }
        DeleteRouterInfo(bundleName, modulePackage);
        bundleInfos_.at(bundleName) = oldInfo;
        APP_LOGD("update storage success bundle:%{public}s", bundleName.c_str());
    }
    return true;
}

bool BundleDataMgr::GetUninstallBundleInfoWithUserAndAppIndex(const std::string &bundleName,
    int32_t userId, int32_t appIndex) const
{
    UninstallBundleInfo uninstallBundleInfo;
    if (uninstallDataMgr_ == nullptr) {
        APP_LOGE("rdbDataManager is null");
        return false;
    }
    if (bundleName.empty()) {
        APP_LOGE("param error");
        return false;
    }
    
    if (!uninstallDataMgr_->GetUninstallBundleInfo(bundleName, uninstallBundleInfo)) {
        APP_LOGD("bundle is not uninstalled with keepdata before");
        return false;
    }
 
    int32_t uid = uninstallBundleInfo.GetUid(userId, appIndex);
    if (uid == Constants::INVALID_UID) {
        APP_LOGD("can not found bundle: %{public}s in uninstallBundleInfo for index: %{public}d in user: %{public}d",
            bundleName.c_str(), appIndex, userId);
        return false;
    }
    return true;
}

bool BundleDataMgr::UpdateUninstallBundleInfo(const std::string &bundleName,
    const UninstallBundleInfo &uninstallBundleInfo)
{
    if (uninstallDataMgr_ == nullptr) {
        APP_LOGE("rdbDataManager is null");
        return false;
    }
    if (bundleName.empty() || uninstallBundleInfo.userInfos.empty()) {
        APP_LOGE("param error");
        return false;
    }
    UninstallBundleInfo oldUninstallBundleInfo;
    if (uninstallDataMgr_->GetUninstallBundleInfo(bundleName, oldUninstallBundleInfo)) {
        std::string newUser = uninstallBundleInfo.userInfos.begin()->first;
        if (oldUninstallBundleInfo.userInfos.find(newUser) != oldUninstallBundleInfo.userInfos.end()) {
            APP_LOGE("u %{public}s has been saved", newUser.c_str());
            return false;
        }
        oldUninstallBundleInfo.userInfos[newUser] = uninstallBundleInfo.userInfos.begin()->second;
        return uninstallDataMgr_->UpdateUninstallBundleInfo(bundleName, oldUninstallBundleInfo);
    }
    return uninstallDataMgr_->UpdateUninstallBundleInfo(bundleName, uninstallBundleInfo);
}

bool BundleDataMgr::GetUninstallBundleInfo(const std::string &bundleName,
    UninstallBundleInfo &uninstallBundleInfo) const
{
    if (uninstallDataMgr_ == nullptr) {
        APP_LOGE("rdbDataManager is null");
        return false;
    }
    if (bundleName.empty()) {
        APP_LOGE("param error");
        return false;
    }
    return uninstallDataMgr_->GetUninstallBundleInfo(bundleName, uninstallBundleInfo);
}

bool BundleDataMgr::GetAllUninstallBundleInfo(
    std::map<std::string, UninstallBundleInfo> &uninstallBundleInfos) const
{
    if (uninstallDataMgr_ == nullptr) {
        APP_LOGE("rdbDataManager is null");
        return false;
    }
    return uninstallDataMgr_->GetAllUninstallBundleInfo(uninstallBundleInfos);
}

bool BundleDataMgr::DeleteUninstallBundleInfo(const std::string &bundleName, int32_t userId)
{
    if (uninstallDataMgr_ == nullptr) {
        APP_LOGE("rdbDataManager is null");
        return false;
    }
    if (bundleName.empty()) {
        APP_LOGE("param error");
        return false;
    }
    UninstallBundleInfo uninstallBundleInfo;
    if (!uninstallDataMgr_->GetUninstallBundleInfo(bundleName, uninstallBundleInfo)) {
        APP_LOGE("bundle %{public}s is not found", bundleName.c_str());
        return false;
    }
    auto it = uninstallBundleInfo.userInfos.find(std::to_string(userId));
    if (it == uninstallBundleInfo.userInfos.end()) {
        APP_LOGE("user %{public}d is not found", userId);
        return false;
    }
    uninstallBundleInfo.userInfos.erase(std::to_string(userId));
    if (uninstallBundleInfo.userInfos.empty()) {
        return uninstallDataMgr_->DeleteUninstallBundleInfo(bundleName);
    }
    return uninstallDataMgr_->UpdateUninstallBundleInfo(bundleName, uninstallBundleInfo);
}

bool BundleDataMgr::RemoveUninstalledBundleinfos(int32_t userId)
{
    if (uninstallDataMgr_ == nullptr) {
        APP_LOGE("uninstallDataMgr is null");
        return false;
    }
    std::map<std::string, UninstallBundleInfo> uninstallBundleInfos;
    if (!uninstallDataMgr_->GetAllUninstallBundleInfo(uninstallBundleInfos)) {
        APP_LOGE("get all uninstall bundle info failed");
        return false;
    }
    std::string userKey = std::to_string(userId);
    std::string prefix = userKey + "_";
    for (auto &item : uninstallBundleInfos) {
        bool modified = false;
        for (auto it = item.second.userInfos.begin(); it != item.second.userInfos.end();) {
            const std::string &key = it->first;
            if (key == userKey || key.rfind(prefix, 0) == 0) {
                it = item.second.userInfos.erase(it);
                modified = true;
            } else {
                ++it;
            }
        }
        if (!modified) {
            continue;
        }
        if (item.second.userInfos.empty()) {
            if (!uninstallDataMgr_->DeleteUninstallBundleInfo(item.first)) {
                APP_LOGE("delete uninstall bundle %{public}s failed", item.first.c_str());
                continue;
            }
        } else {
            if (!uninstallDataMgr_->UpdateUninstallBundleInfo(item.first, item.second)) {
                APP_LOGE("update uninstall bundle %{public}s failed", item.first.c_str());
                continue;
            }
        }
    }
    return true;
}

bool BundleDataMgr::DeleteUninstallCloneBundleInfo(const std::string &bundleName, int32_t userId, int32_t appIndex)
{
    if (uninstallDataMgr_ == nullptr) {
        APP_LOGE("uninstallDataMgr is null");
        return false;
    }
    if (bundleName.empty()) {
        APP_LOGE("param error");
        return false;
    }
    UninstallBundleInfo uninstallBundleInfo;
    if (!uninstallDataMgr_->GetUninstallBundleInfo(bundleName, uninstallBundleInfo)) {
        APP_LOGE("bundle %{public}s is not found", bundleName.c_str());
        return false;
    }
    std::string key = std::to_string(userId) + "_" + std::to_string(appIndex);
    auto it = uninstallBundleInfo.userInfos.find(key);
    if (it == uninstallBundleInfo.userInfos.end()) {
        APP_LOGE("-u %{public}d -i %{public}d is not found", userId, appIndex);
        return false;
    }
    uninstallBundleInfo.userInfos.erase(key);
    if (uninstallBundleInfo.userInfos.empty()) {
        return uninstallDataMgr_->DeleteUninstallBundleInfo(bundleName);
    }
    return uninstallDataMgr_->UpdateUninstallBundleInfo(bundleName, uninstallBundleInfo);
}

bool BundleDataMgr::AddFirstInstallBundleInfo(const std::string &bundleName, const int32_t userId,
    const FirstInstallBundleInfo &firstInstallBundleInfo)
{
    if (bundleName.empty()) {
        APP_LOGE("bundleName is empty");
        return false;
    }
    if (firstInstallDataMgr_ == nullptr) {
        APP_LOGE("firstInstallDataMgr_ is null");
        return false;
    }

    if (firstInstallDataMgr_->IsExistFirstInstallBundleInfo(bundleName, userId)
        || userId == Constants::ALL_USERID) {
        APP_LOGW("bundleName %{public}s, user %{public}d has been saved", bundleName.c_str(), userId);
        return true;
    }
    return firstInstallDataMgr_->AddFirstInstallBundleInfo(bundleName, userId, firstInstallBundleInfo);
}

bool BundleDataMgr::GetFirstInstallBundleInfo(const std::string &bundleName, const int32_t userId,
    FirstInstallBundleInfo &firstInstallBundleInfo)
{
    if (bundleName.empty()) {
        APP_LOGE("bundleName is empty");
        return false;
    }
    if (firstInstallDataMgr_ == nullptr) {
        APP_LOGE("firstInstallDataMgr_ is null");
        return false;
    }
    return firstInstallDataMgr_->GetFirstInstallBundleInfo(bundleName, userId, firstInstallBundleInfo);
}

bool BundleDataMgr::DeleteFirstInstallBundleInfo(int32_t userId)
{
    if (firstInstallDataMgr_ == nullptr) {
        APP_LOGE("firstInstallDataMgr_ is null");
        return false;
    }
    return firstInstallDataMgr_->DeleteFirstInstallBundleInfo(userId);
}

bool BundleDataMgr::RemoveHspModuleByVersionCode(int32_t versionCode, InnerBundleInfo &info)
{
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    std::string bundleName = info.GetBundleName();
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("bundleName: %{public}s bundle info not exist", bundleName.c_str());
        return false;
    }
    std::lock_guard<std::mutex> stateLock(stateMutex_);
    auto statusItem = installStates_.find(bundleName);
    if (statusItem == installStates_.end()) {
        APP_LOGW("save info fail, app:%{public}s is not updated", bundleName.c_str());
        return false;
    }
    if (statusItem->second == InstallState::UNINSTALL_START || statusItem->second == InstallState::ROLL_BACK) {
        if (!DeleteRouterInfoForSharedBundle(info, versionCode)) {
            APP_LOGE("delete router map failed bundle:%{public}s", bundleName.c_str());
            return false;
        }
        info.DeleteHspModuleByVersion(versionCode);
        info.SetBundleStatus(InnerBundleInfo::BundleStatus::ENABLED);
        if (dataStorage_->SaveStorageBundleInfo(info)) {
            APP_LOGD("update storage success bundle:%{public}s", bundleName.c_str());
            bundleInfos_.at(bundleName) = info;
            return true;
        }
    }
    return true;
}

bool BundleDataMgr::DeleteRouterInfoForSharedBundle(const InnerBundleInfo &info, const int32_t versionCode)
{
    std::vector<std::string> moduleNames;
    info.GetModuleNames(moduleNames);
    bool ret = true;
    for (auto &moduleName : moduleNames) {
        if (!routerStorage_->DeleteRouterInfo(info.GetBundleName(), moduleName, versionCode)) {
            ret = false;
        }
    }
    return ret;
}

ErrCode BundleDataMgr::AddInnerBundleUserInfo(
    const std::string &bundleName, const InnerBundleUserInfo& newUserInfo)
{
    APP_LOGD("AddInnerBundleUserInfo:%{public}s", bundleName.c_str());
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("bundleName: %{public}s bundle info not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }

    std::lock_guard<std::mutex> stateLock(stateMutex_);
    auto& info = bundleInfos_.at(bundleName);
    info.AddInnerBundleUserInfo(newUserInfo);
    info.SetBundleStatus(InnerBundleInfo::BundleStatus::ENABLED);
    ErrCode ret = dataStorage_->SaveStorageBundleInfoWithCode(info);
    if (ret != ERR_OK) {
        APP_LOGW("update storage failed bundle:%{public}s, errcode:%{public}d", bundleName.c_str(), ret);
        return ret;
    }
    return ERR_OK;
}

bool BundleDataMgr::RemoveInnerBundleUserInfo(
    const std::string &bundleName, int32_t userId)
{
    APP_LOGD("RemoveInnerBundleUserInfo:%{public}s", bundleName.c_str());
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("bundleName: %{public}s bundle info not exist", bundleName.c_str());
        return false;
    }

    std::lock_guard<std::mutex> stateLock(stateMutex_);
    auto& info = bundleInfos_.at(bundleName);
    info.RemoveInnerBundleUserInfo(userId);
    info.SetBundleStatus(InnerBundleInfo::BundleStatus::ENABLED);
    if (!dataStorage_->SaveStorageBundleInfo(info)) {
        APP_LOGW("update storage failed bundle:%{public}s", bundleName.c_str());
        return false;
    }

    bundleStateStorage_->DeleteBundleState(bundleName, userId);
    return true;
}

bool BundleDataMgr::UpdateInnerBundleInfo(
    const std::string &bundleName, InnerBundleInfo &newInfo, InnerBundleInfo &oldInfo)
{
    LOG_I(BMS_TAG_DEFAULT, "updateInfo:%{public}s", bundleName.c_str());
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("bundleName: %{public}s bundle info not exist", bundleName.c_str());
        return false;
    }
    std::lock_guard<std::mutex> stateLock(stateMutex_);
    auto statusItem = installStates_.find(bundleName);
    if (statusItem == installStates_.end()) {
        APP_LOGW("save info fail, app:%{public}s is not updated", bundleName.c_str());
        return false;
    }
    // ROLL_BACK and USER_CHANGE should not be here
    if (statusItem->second == InstallState::UPDATING_SUCCESS
        || statusItem->second == InstallState::ROLL_BACK
        || statusItem->second == InstallState::USER_CHANGE) {
        APP_LOGD("begin to update, bundleName : %{public}s, moduleName : %{public}s",
            oldInfo.GetBundleName().c_str(), newInfo.GetCurrentModulePackage().c_str());
        if (UpdateInnerBundleInfo(newInfo, oldInfo)) {
            bundleInfos_.at(bundleName) = oldInfo;
            APP_LOGD("update storage success bundle:%{public}s", oldInfo.GetBundleName().c_str());
            return true;
        }
    }
    return false;
}

bool BundleDataMgr::UpdateInnerBundleInfo(InnerBundleInfo &newInfo, InnerBundleInfo &oldInfo)
{
    if (newInfo.GetOverlayType() == NON_OVERLAY_TYPE) {
        oldInfo.KeepOldOverlayConnection(newInfo);
    }
    ProcessAllowedAcls(newInfo, oldInfo);
    newInfo.UpdateModuleRemovable(oldInfo);
    oldInfo.UpdateModuleInfo(newInfo);
    oldInfo.SetDFXParamStatus();
    // 1.exist entry, update entry.
    // 2.only exist feature, update feature.
    if (IsUpdateInnerBundleInfoSatisified(oldInfo, newInfo)) {
        UpdateBaseBundleInfoIntoOld(newInfo, oldInfo);
    }
    oldInfo.SetCertificateFingerprint(newInfo.GetCertificateFingerprint());
    if (oldInfo.GetOldAppIds().empty()) {
        oldInfo.AddOldAppId(oldInfo.GetAppId());
    }
    oldInfo.AddOldAppId(newInfo.GetAppId());
    oldInfo.SetProvisionId(newInfo.GetProvisionId());
    oldInfo.SetAppIdentifier(newInfo.GetAppIdentifier());
    oldInfo.SetAppPrivilegeLevel(newInfo.GetAppPrivilegeLevel());
    oldInfo.UpdateAppDetailAbilityAttrs();
    oldInfo.UpdateDataGroupInfos(newInfo.GetDataGroupInfos());
    if (oldInfo.GetBaseApplicationInfo().needAppDetail) {
        AddAppDetailAbilityInfo(oldInfo);
    }
    oldInfo.UpdateNativeLibAttrs(newInfo.GetBaseApplicationInfo());
    oldInfo.UpdateArkNativeAttrs(newInfo.GetBaseApplicationInfo());
    oldInfo.SetAsanLogPath(newInfo.GetAsanLogPath());
    if (newInfo.GetAppCrowdtestDeadline() != Constants::INHERIT_CROWDTEST_DEADLINE) {
        oldInfo.SetAppCrowdtestDeadline(newInfo.GetAppCrowdtestDeadline());
    }
    oldInfo.SetBundlePackInfo(newInfo.GetBundlePackInfo());
    // clear apply quick fix frequency
    oldInfo.ResetApplyQuickFixFrequency();
    oldInfo.SetBundleStatus(InnerBundleInfo::BundleStatus::ENABLED);
    oldInfo.SetIsNewVersion(newInfo.GetIsNewVersion());
    oldInfo.SetAppProvisionMetadata(newInfo.GetAppProvisionMetadata());
    oldInfo.UpdateOdidByBundleInfo(newInfo);
    oldInfo.SetInstalledForAllUser(newInfo.IsInstalledForAllUser());
#ifdef BUNDLE_FRAMEWORK_OVERLAY_INSTALLATION
    if (newInfo.GetIsNewVersion() && newInfo.GetOverlayType() == NON_OVERLAY_TYPE) {
        if (!UpdateOverlayInfo(newInfo, oldInfo)) {
            APP_LOGD("update overlay info failed");
            return false;
        }
    }
    if ((newInfo.GetOverlayType() != NON_OVERLAY_TYPE) && (!UpdateOverlayInfo(newInfo, oldInfo))) {
        APP_LOGD("update overlay info failed");
        return false;
    }
#endif
    return true;
}

bool BundleDataMgr::QueryAbilityInfo(const Want &want, int32_t flags, int32_t userId, AbilityInfo &abilityInfo,
    int32_t appIndex) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("request user id is invalid");
        return false;
    }

    ElementName element = want.GetElement();
    std::string bundleName = element.GetBundleName();
    std::string abilityName = element.GetAbilityName();
    LOG_D(BMS_TAG_QUERY, "QueryAbilityInfo bundleName:%{public}s abilityName:%{public}s",
        bundleName.c_str(), abilityName.c_str());
    // explicit query
    if (!bundleName.empty() && !abilityName.empty()) {
        bool ret = ExplicitQueryAbilityInfo(want, flags, requestUserId, abilityInfo, appIndex);
        if (!ret) {
            LOG_NOFUNC_I(BMS_TAG_QUERY, "ExplicitQueryAbility no match -n %{public}s -a %{public}s -u %{public}d"
                " -i %{public}d", bundleName.c_str(), abilityName.c_str(), userId, appIndex);
            return false;
        }
        return true;
    }
    std::vector<AbilityInfo> abilityInfos;
    bool ret = ImplicitQueryAbilityInfos(want, flags, requestUserId, abilityInfos, appIndex);
    if (!ret) {
        LOG_D(BMS_TAG_QUERY,
            "implicit queryAbilityInfos error action:%{public}s uri:%{private}s type:%{public}s",
            want.GetAction().c_str(), want.GetUriString().c_str(), want.GetType().c_str());
        return false;
    }
    if (abilityInfos.size() == 0) {
        LOG_W(BMS_TAG_QUERY, "no matching abilityInfo action:%{public}s uri:%{private}s type:%{public}s",
            want.GetAction().c_str(), want.GetUriString().c_str(), want.GetType().c_str());
        return false;
    }
    abilityInfo = abilityInfos[0];
    return true;
}

void BundleDataMgr::GetCloneAbilityInfos(std::vector<AbilityInfo> &abilityInfos,
    const ElementName &element, int32_t flags, int32_t userId) const
{
    std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexes(element.GetBundleName(), userId);
    if (cloneAppIndexes.empty()) {
        APP_LOGI("clone app index is empty");
        return;
    }
    for (int32_t appIndex: cloneAppIndexes) {
        AbilityInfo cloneAbilityInfo;
        bool ret = ExplicitQueryCloneAbilityInfo(element, flags, userId, appIndex, cloneAbilityInfo);
        if (ret) {
            abilityInfos.emplace_back(cloneAbilityInfo);
        }
    }
}

void BundleDataMgr::GetCloneAbilityInfosV9(std::vector<AbilityInfo> &abilityInfos,
    const ElementName &element, int32_t flags, int32_t userId) const
{
    std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexes(element.GetBundleName(), userId);
    if (cloneAppIndexes.empty()) {
        APP_LOGI("clone app index is empty");
        return;
    }
    for (int32_t appIndex: cloneAppIndexes) {
        AbilityInfo cloneAbilityInfo;
        ErrCode ret = ExplicitQueryCloneAbilityInfoV9(element, flags, userId, appIndex, cloneAbilityInfo);
        if (ret == ERR_OK) {
            abilityInfos.emplace_back(cloneAbilityInfo);
        }
    }
}

bool BundleDataMgr::QueryAbilityInfos(
    const Want &want, int32_t flags, int32_t userId, std::vector<AbilityInfo> &abilityInfos) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("request user id is invalid");
        return false;
    }

    ElementName element = want.GetElement();
    std::string bundleName = element.GetBundleName();
    std::string abilityName = element.GetAbilityName();
    LOG_D(BMS_TAG_QUERY, "QueryAbilityInfos bundleName:%{public}s abilityName:%{public}s",
        bundleName.c_str(), abilityName.c_str());
    // explicit query
    if (!bundleName.empty() && !abilityName.empty()) {
        AbilityInfo abilityInfo;
        bool ret = ExplicitQueryAbilityInfo(want, flags, requestUserId, abilityInfo);
        LOG_D(BMS_TAG_QUERY, "explicit query ret:%{public}d bundleName:%{public}s abilityName:%{public}s",
            ret, bundleName.c_str(), abilityName.c_str());
        if (ret) {
            abilityInfos.emplace_back(abilityInfo);
        }
        // get cloneApp's abilityInfos
        GetCloneAbilityInfos(abilityInfos, element, flags, userId);
        LOG_NOFUNC_I(BMS_TAG_QUERY, "ExplicitQueryAbility size:%{public}zu -n %{public}s -a %{public}s -u %{public}d",
            abilityInfos.size(), bundleName.c_str(), abilityName.c_str(), userId);
        return !abilityInfos.empty();
    }
    // implicit query
    (void)ImplicitQueryAbilityInfos(want, flags, requestUserId, abilityInfos);
    ImplicitQueryCloneAbilityInfos(want, flags, requestUserId, abilityInfos);
    if (abilityInfos.size() == 0) {
        LOG_W(BMS_TAG_QUERY, "no matching abilityInfo action:%{public}s uri:%{private}s type:%{public}s"
            " userId:%{public}d", want.GetAction().c_str(), want.GetUriString().c_str(), want.GetType().c_str(),
            requestUserId);
        return false;
    }
    return true;
}

ErrCode BundleDataMgr::QueryAbilityInfosV9(
    const Want &want, int32_t flags, int32_t userId, std::vector<AbilityInfo> &abilityInfos) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("request user id is invalid");
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    ElementName element = want.GetElement();
    std::string bundleName = element.GetBundleName();
    std::string abilityName = element.GetAbilityName();
    LOG_D(BMS_TAG_QUERY, "QueryAbilityInfosV9 bundleName:%{public}s abilityName:%{public}s",
        bundleName.c_str(), abilityName.c_str());
    // explicit query
    if (!bundleName.empty() && !abilityName.empty()) {
        AbilityInfo abilityInfo;
        ErrCode ret = ExplicitQueryAbilityInfoV9(want, flags, requestUserId, abilityInfo);
        LOG_D(BMS_TAG_QUERY, "explicit queryV9 ret:%{public}d, bundleName:%{public}s abilityName:%{public}s",
            ret, bundleName.c_str(), abilityName.c_str());
        if (ret == ERR_OK) {
            abilityInfos.emplace_back(abilityInfo);
        }
        // get cloneApp's abilityInfos
        GetCloneAbilityInfosV9(abilityInfos, element, flags, userId);
        LOG_NOFUNC_I(BMS_TAG_QUERY, "ExplicitQueryAbility V9 size:%{public}zu -n %{public}s -a %{public}s -u %{public}d",
            abilityInfos.size(), bundleName.c_str(), abilityName.c_str(), userId);
        if (abilityInfos.empty()) {
            return ret;
        }
        return ERR_OK;
    }
    // implicit query
    ErrCode ret = ImplicitQueryAbilityInfosV9(want, flags, requestUserId, abilityInfos);
    ImplicitQueryCloneAbilityInfosV9(want, flags, requestUserId, abilityInfos);
    if (abilityInfos.empty()) {
        if (ret != ERR_OK) {
            return ret;
        }
        LOG_W(BMS_TAG_QUERY, "no matching abilityInfo action:%{public}s uri:%{private}s type:%{public}s"
            " userId:%{public}d", want.GetAction().c_str(), want.GetUriString().c_str(), want.GetType().c_str(),
            requestUserId);
        return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::BatchQueryAbilityInfos(
    const std::vector<Want> &wants, int32_t flags, int32_t userId, std::vector<AbilityInfo> &abilityInfos) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("request user id is invalid");
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    for (size_t i = 0; i < wants.size(); i++) {
        std::vector<AbilityInfo> tmpAbilityInfos;
        ElementName element = wants[i].GetElement();
        std::string bundleName = element.GetBundleName();
        std::string abilityName = element.GetAbilityName();
        APP_LOGD("QueryAbilityInfosV9 bundle name:%{public}s, ability name:%{public}s",
            bundleName.c_str(), abilityName.c_str());
        // explicit query
        if (!bundleName.empty() && !abilityName.empty()) {
            AbilityInfo abilityInfo;
            ErrCode ret = ExplicitQueryAbilityInfoV9(wants[i], flags, requestUserId, abilityInfo);
            if (ret != ERR_OK) {
                APP_LOGE("explicit queryAbilityInfoV9 error:%{public}d, bundleName:%{public}s, abilityName:%{public}s",
                    ret, bundleName.c_str(), abilityName.c_str());
                return ret;
            }
            tmpAbilityInfos.emplace_back(abilityInfo);
        } else {
            // implicit query
            ErrCode ret = ImplicitQueryAbilityInfosV9(wants[i], flags, requestUserId, tmpAbilityInfos);
            if (ret != ERR_OK) {
                APP_LOGD("implicit queryAbilityInfosV9 error. action:%{public}s, uri:%{private}s, type:%{public}s",
                    wants[i].GetAction().c_str(), wants[i].GetUriString().c_str(), wants[i].GetType().c_str());
                return ret;
            }
        }
        for (size_t j = 0; j < tmpAbilityInfos.size(); j++) {
            auto it = std::find_if(abilityInfos.begin(), abilityInfos.end(),
                [&](const AbilityInfo& info) {
                    return tmpAbilityInfos[j].bundleName == info.bundleName &&
                        tmpAbilityInfos[j].moduleName == info.moduleName &&
                        tmpAbilityInfos[j].name == info.name;
                });
            if (it == abilityInfos.end()) {
                abilityInfos.push_back(tmpAbilityInfos[j]);
            }
        }
    }

    if (abilityInfos.empty()) {
        APP_LOGW("no matching abilityInfo");
        return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
    }

    return ERR_OK;
}

bool BundleDataMgr::ExplicitQueryAbilityInfo(const Want &want, int32_t flags, int32_t userId,
    AbilityInfo &abilityInfo, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    ElementName element = want.GetElement();
    std::string bundleName = element.GetBundleName();
    std::string abilityName = element.GetAbilityName();
    std::string moduleName = element.GetModuleName();
    LOG_D(BMS_TAG_QUERY,
        "ExplicitQueryAbilityInfo bundleName:%{public}s moduleName:%{public}s abilityName:%{public}s",
        bundleName.c_str(), moduleName.c_str(), abilityName.c_str());
    LOG_D(BMS_TAG_QUERY, "flags:%{public}d userId:%{public}d", flags, userId);

    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("request user id is invalid");
        return false;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;
    if ((appIndex == 0) && (!GetInnerBundleInfoWithFlags(bundleName, flags, innerBundleInfo, requestUserId))) {
        LOG_D(BMS_TAG_QUERY, "ExplicitQueryAbilityInfo failed, bundleName:%{public}s", bundleName.c_str());
        return false;
    }
    InnerBundleInfo sandboxInfo;
    // explict query from sandbox manager
    if (appIndex > 0) {
        if (sandboxAppHelper_ == nullptr) {
            LOG_W(BMS_TAG_QUERY, "sandboxAppHelper_ is nullptr");
            return false;
        }
        auto ret = sandboxAppHelper_->GetSandboxAppInfo(bundleName, appIndex, requestUserId, sandboxInfo);
        if (ret != ERR_OK) {
            LOG_D(BMS_TAG_QUERY, "GetSandboxAppInfo failed errCode %{public}d, bundleName:%{public}s",
                ret, bundleName.c_str());
            return false;
        }
        innerBundleInfo = &sandboxInfo;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ExplicitQueryAbilityInfo is null.");
        return false;
    }

    int32_t responseUserId = innerBundleInfo->GetResponseUserId(requestUserId);
    auto ability = innerBundleInfo->FindAbilityInfo(moduleName, abilityName, responseUserId);
    if (!ability) {
        LOG_D(BMS_TAG_QUERY, "ExplicitQueryAbility not found UIAbility -n %{public}s -m %{public}s -a %{public}s"
            " -u %{public}d", bundleName.c_str(), moduleName.c_str(), abilityName.c_str(), responseUserId);
        return false;
    }
    return QueryAbilityInfoWithFlags(ability, flags, responseUserId, *innerBundleInfo, abilityInfo);
}

ErrCode BundleDataMgr::ExplicitQueryAbilityInfoV9(const Want &want, int32_t flags, int32_t userId,
    AbilityInfo &abilityInfo, int32_t appIndex) const
{
    if (appIndex < 0) {
        LOG_E(BMS_TAG_QUERY, "ExplicitQueryAbilityInfoV9 appIndex out of range");
        return ERR_APPEXECFWK_APP_INDEX_OUT_OF_RANGE;
    }
    ElementName element = want.GetElement();
    std::string bundleName = element.GetBundleName();
    std::string abilityName = element.GetAbilityName();
    std::string moduleName = element.GetModuleName();
    LOG_D(BMS_TAG_QUERY,
        "ExplicitQueryAbilityInfoV9 bundleName:%{public}s moduleName:%{public}s abilityName:%{public}s",
        bundleName.c_str(), moduleName.c_str(), abilityName.c_str());
    LOG_D(BMS_TAG_QUERY, "flags:%{public}d userId:%{public}d", flags, userId);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;
    if (appIndex == 0) {
        ErrCode ret = GetInnerBundleInfoWithFlagsV9(bundleName, flags, innerBundleInfo, requestUserId);
        if (ret != ERR_OK) {
            LOG_D(BMS_TAG_QUERY, "ExplicitQueryAbilityInfoV9 fail bundleName:%{public}s", bundleName.c_str());
            return ret;
        }
    }
    InnerBundleInfo sandboxInfo;
    // explict query from sandbox manager
    if (appIndex > 0) {
        if (sandboxAppHelper_ == nullptr) {
            LOG_W(BMS_TAG_QUERY, "sandboxAppHelper_ is nullptr");
            return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
        }
        auto ret = sandboxAppHelper_->GetSandboxAppInfo(bundleName, appIndex, requestUserId, sandboxInfo);
        if (ret != ERR_OK) {
            LOG_D(BMS_TAG_QUERY, "GetSandboxAppInfo failed errCode %{public}d, bundleName:%{public}s",
                ret, bundleName.c_str());
            return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
        }
        innerBundleInfo = &sandboxInfo;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ExplicitQueryAbilityInfoV9 is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    int32_t responseUserId = innerBundleInfo->GetResponseUserId(requestUserId);
    auto ability = innerBundleInfo->FindAbilityInfoV9(moduleName, abilityName);
    if (!ability) {
        LOG_D(BMS_TAG_QUERY, "ExplicitQueryAbilityInfoV9 not found UIAbility -n %{public}s -m %{public}s "
            "-a %{public}s", bundleName.c_str(), moduleName.c_str(), abilityName.c_str());
        return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
    }

    return QueryAbilityInfoWithFlagsV9(ability, flags, responseUserId, *innerBundleInfo, abilityInfo);
}

void BundleDataMgr::FilterAbilityInfosByModuleName(const std::string &moduleName,
    std::vector<AbilityInfo> &abilityInfos) const
{
    LOG_D(BMS_TAG_QUERY, "FilterAbilityInfosByModuleName moduleName: %{public}s", moduleName.c_str());
    if (moduleName.empty()) {
        return;
    }
    for (auto iter = abilityInfos.begin(); iter != abilityInfos.end();) {
        if (iter->moduleName != moduleName) {
            iter = abilityInfos.erase(iter);
        } else {
            ++iter;
        }
    }
}

void BundleDataMgr::ImplicitQueryCloneAbilityInfos(
    const Want &want, int32_t flags, int32_t userId, std::vector<AbilityInfo> &abilityInfos) const
{
    LOG_D(BMS_TAG_QUERY, "begin ImplicitQueryCloneAbilityInfos");
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return;
    }

    if (want.GetAction().empty() && want.GetEntities().empty()
        && want.GetUriString().empty() && want.GetType().empty() && want.GetStringParam(LINK_FEATURE).empty()) {
        LOG_E(BMS_TAG_QUERY, "param invalid");
        return;
    }
    LOG_D(BMS_TAG_QUERY, "action:%{public}s, uri:%{private}s, type:%{public}s",
        want.GetAction().c_str(), want.GetUriString().c_str(), want.GetType().c_str());
    LOG_D(BMS_TAG_QUERY, "flags:%{public}d, userId:%{public}d", flags, userId);
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        LOG_W(BMS_TAG_QUERY, "bundleInfos_ is empty");
        return;
    }
    std::string bundleName = want.GetElement().GetBundleName();
    if (!bundleName.empty()) {
        // query in current bundleName
        if (!ImplicitQueryCurCloneAbilityInfos(want, flags, requestUserId, abilityInfos)) {
            return;
        }
    } else {
        // query all
        ImplicitQueryAllCloneAbilityInfos(want, flags, requestUserId, abilityInfos);
    }
    FilterAbilityInfosByAppLinking(want, flags, abilityInfos);
    // sort by priority, descending order.
    if (abilityInfos.size() > 1) {
        std::stable_sort(abilityInfos.begin(), abilityInfos.end(),
            [](AbilityInfo a, AbilityInfo b) { return a.priority > b.priority; });
    }
    LOG_D(BMS_TAG_QUERY, "end ImplicitQueryCloneAbilityInfos");
}

bool BundleDataMgr::ImplicitQueryAbilityInfos(
    const Want &want, int32_t flags, int32_t userId, std::vector<AbilityInfo> &abilityInfos, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return false;
    }

    if (want.GetAction().empty() && want.GetEntities().empty()
        && want.GetUriString().empty() && want.GetType().empty() && want.GetStringParam(LINK_FEATURE).empty()) {
        LOG_W(BMS_TAG_QUERY, "param invalid");
        return false;
    }
    LOG_D(BMS_TAG_QUERY, "action:%{public}s, uri:%{private}s, type:%{public}s",
        want.GetAction().c_str(), want.GetUriString().c_str(), want.GetType().c_str());
    LOG_D(BMS_TAG_QUERY, "flags:%{public}d, userId:%{public}d", flags, userId);
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        LOG_W(BMS_TAG_QUERY, "bundleInfos_ is empty");
        return false;
    }
    std::string bundleName = want.GetElement().GetBundleName();
    if (!bundleName.empty()) {
        // query in current bundleName
        if (!ImplicitQueryCurAbilityInfos(want, flags, requestUserId, abilityInfos, appIndex)) {
            LOG_D(BMS_TAG_QUERY, "ImplicitQueryCurAbilityInfos failed bundleName:%{public}s",
                bundleName.c_str());
            return false;
        }
    } else {
        // query all
        ImplicitQueryAllAbilityInfos(want, flags, requestUserId, abilityInfos, appIndex);
    }
    FilterAbilityInfosByAppLinking(want, flags, abilityInfos);
    // sort by priority, descending order.
    if (abilityInfos.size() > 1) {
        std::stable_sort(abilityInfos.begin(), abilityInfos.end(),
            [](AbilityInfo a, AbilityInfo b) { return a.priority > b.priority; });
    }
    return true;
}

ErrCode BundleDataMgr::ImplicitQueryAbilityInfosV9(
    const Want &want, int32_t flags, int32_t userId, std::vector<AbilityInfo> &abilityInfos, int32_t appIndex) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    if (want.GetAction().empty() && want.GetEntities().empty()
        && want.GetUriString().empty() && want.GetType().empty() && want.GetStringParam(LINK_FEATURE).empty()) {
        LOG_E(BMS_TAG_QUERY, "param invalid");
        return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
    }
    LOG_D(BMS_TAG_QUERY, "action:%{public}s uri:%{private}s type:%{public}s",
        want.GetAction().c_str(), want.GetUriString().c_str(), want.GetType().c_str());
    LOG_D(BMS_TAG_QUERY, "flags:%{public}d userId:%{public}d", flags, userId);
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ is empty");
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    std::string bundleName = want.GetElement().GetBundleName();
    if (!bundleName.empty()) {
        // query in current bundleName
        ErrCode ret = ImplicitQueryCurAbilityInfosV9(want, flags, requestUserId, abilityInfos, appIndex);
        if (ret != ERR_OK) {
            LOG_D(BMS_TAG_QUERY, "ImplicitQueryCurAbilityInfosV9 failed bundleName:%{public}s",
                bundleName.c_str());
            return ret;
        }
    } else {
        // query all
        ImplicitQueryAllAbilityInfosV9(want, flags, requestUserId, abilityInfos, appIndex);
    }
    FilterAbilityInfosByAppLinking(want, flags, abilityInfos);
    // sort by priority, descending order.
    if (abilityInfos.size() > 1) {
        std::stable_sort(abilityInfos.begin(), abilityInfos.end(),
            [](AbilityInfo a, AbilityInfo b) { return a.priority > b.priority; });
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::FindMatchedAbilityForLink(
    const std::string &link, int32_t flags, int32_t userId, bool &found) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        LOG_E(BMS_TAG_QUERY, "invalid userId");
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    if (link.empty()) {
        LOG_E(BMS_TAG_QUERY, "param invalid");
        return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ is empty");
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }

    Want want;
    want.SetUri(link);
    std::vector<std::string> mimeTypes;
    MimeTypeMgr::GetMimeTypeByUri(want.GetUriString(), mimeTypes);

    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &innerBundleInfo = item.second;
        if (CheckBundleAndAbilityDisabled(innerBundleInfo, flags, userId) != ERR_OK) {
            continue;
        }
        int32_t responseUserId = innerBundleInfo.GetResponseUserId(userId);
        if (IsMatchedAbilityExist(want, innerBundleInfo, responseUserId, mimeTypes)) {
            found = true;
            return ERR_OK;
        }
    }

    found = false;
    return ERR_OK;
}

bool BundleDataMgr::IsMatchedAbilityExist(const Want &want, const InnerBundleInfo &info,
    int32_t userId, const std::vector<std::string> &paramMimeTypes) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    const std::map<std::string, std::vector<Skill>> skillInfos = info.GetInnerSkillInfos();
    for (const auto &abilityInfoPair : info.GetInnerAbilityInfos()) {
        const InnerAbilityInfo &innerAbilityInfo = abilityInfoPair.second;

        if (innerAbilityInfo.name == ServiceConstants::APP_DETAIL_ABILITY) {
            continue;
        }

        if (!info.IsAbilityEnabled(innerAbilityInfo.bundleName, innerAbilityInfo.name,
            GetUserId(userId), 0)) {
            LOG_W(BMS_TAG_QUERY, "Ability %{public}s is disabled", innerAbilityInfo.name.c_str());
            continue;
        }

        auto skillsPair = skillInfos.find(abilityInfoPair.first);
        if (skillsPair == skillInfos.end()) {
            continue;
        }
        bool isPrivateType = MatchPrivateType(
            want, innerAbilityInfo.supportExtNames, innerAbilityInfo.supportMimeTypes, paramMimeTypes);
        if (isPrivateType) {
            return true;
        }
        for (size_t skillIndex = 0; skillIndex < skillsPair->second.size(); ++skillIndex) {
            const Skill &skill = skillsPair->second[skillIndex];
            size_t matchUriIndex = 0;
            if (skill.Match(want, matchUriIndex)) {
                return true;
            }
        }
    }
    return false;
}

void BundleDataMgr::ImplicitQueryCloneAbilityInfosV9(
    const Want &want, int32_t flags, int32_t userId, std::vector<AbilityInfo> &abilityInfos) const
{
    LOG_D(BMS_TAG_QUERY, "begin ImplicitQueryCloneAbilityInfosV9");
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return;
    }
    if (want.GetAction().empty() && want.GetEntities().empty()
        && want.GetUriString().empty() && want.GetType().empty() && want.GetStringParam(LINK_FEATURE).empty()) {
        LOG_E(BMS_TAG_QUERY, "param invalid");
        return;
    }
    LOG_D(BMS_TAG_QUERY, "action:%{public}s uri:%{private}s type:%{public}s",
        want.GetAction().c_str(), want.GetUriString().c_str(), want.GetType().c_str());
    LOG_D(BMS_TAG_QUERY, "flags:%{public}d userId:%{public}d", flags, userId);

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ is empty");
        return;
    }
    std::string bundleName = want.GetElement().GetBundleName();
    if (!bundleName.empty()) {
        // query in current bundleName
        if (!ImplicitQueryCurCloneAbilityInfosV9(want, flags, requestUserId, abilityInfos)) {
            return;
        }
    } else {
        // query all
        ImplicitQueryAllCloneAbilityInfosV9(want, flags, requestUserId, abilityInfos);
    }
    FilterAbilityInfosByAppLinking(want, flags, abilityInfos);
    // sort by priority, descending order.
    if (abilityInfos.size() > 1) {
        std::stable_sort(abilityInfos.begin(), abilityInfos.end(),
            [](AbilityInfo a, AbilityInfo b) { return a.priority > b.priority; });
    }
    LOG_D(BMS_TAG_QUERY, "end ImplicitQueryCloneAbilityInfosV9");
}

bool BundleDataMgr::QueryAbilityInfoWithFlags(const std::optional<AbilityInfo> &option, int32_t flags, int32_t userId,
    const InnerBundleInfo &innerBundleInfo, AbilityInfo &info, int32_t appIndex) const
{
    LOG_D(BMS_TAG_QUERY,
        "begin to QueryAbilityInfoWithFlags flags=%{public}d,userId=%{public}d,appIndex=%{public}d",
        flags, userId, appIndex);
    if ((static_cast<uint32_t>(flags) & GET_ABILITY_INFO_SYSTEMAPP_ONLY) == GET_ABILITY_INFO_SYSTEMAPP_ONLY &&
        !innerBundleInfo.IsSystemApp()) {
        LOG_W(BMS_TAG_QUERY, "no system app ability info for this calling");
        return false;
    }
    if (!(static_cast<uint32_t>(flags) & GET_ABILITY_INFO_WITH_DISABLE)) {
        if (!innerBundleInfo.IsAbilityEnabled((*option), userId, appIndex)) {
            LOG_W(BMS_TAG_QUERY, "bundleName:%{public}s ability:%{public}s is disabled",
                option->bundleName.c_str(), option->name.c_str());
            return false;
        }
    }
    info = (*option);
    if ((static_cast<uint32_t>(flags) & GET_ABILITY_INFO_WITH_PERMISSION) != GET_ABILITY_INFO_WITH_PERMISSION) {
        info.permissions.clear();
    }
    if ((static_cast<uint32_t>(flags) & GET_ABILITY_INFO_WITH_METADATA) != GET_ABILITY_INFO_WITH_METADATA) {
        info.metaData.customizeData.clear();
        info.metadata.clear();
    }
    if ((static_cast<uint32_t>(flags) & GET_ABILITY_INFO_WITH_SKILL) != GET_ABILITY_INFO_WITH_SKILL) {
        info.skills.clear();
    } else {
        innerBundleInfo.AppendDynamicSkillsToAbilityIfExist(info);
    }
    if ((static_cast<uint32_t>(flags) & GET_ABILITY_INFO_WITH_APPLICATION) == GET_ABILITY_INFO_WITH_APPLICATION) {
        innerBundleInfo.GetApplicationInfo(
            ApplicationFlag::GET_APPLICATION_INFO_WITH_CERTIFICATE_FINGERPRINT, userId, info.applicationInfo);
    }
    // set uid for NAPI cache use
    const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
    if (innerBundleInfo.GetInnerBundleUserInfo(userId, innerBundleUserInfoPtr)) {
        if (!innerBundleUserInfoPtr) {
            LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by QueryAbilityInfoWithFlags is null");
            return false;
        }
        if (appIndex == 0) {
            info.uid = innerBundleUserInfoPtr->uid;
        } else {
            std::string appIndexKey = InnerBundleUserInfo::AppIndexToKey(appIndex);
            if (innerBundleUserInfoPtr->cloneInfos.find(appIndexKey) != innerBundleUserInfoPtr->cloneInfos.end()) {
                auto cloneInfo = innerBundleUserInfoPtr->cloneInfos.at(appIndexKey);
                info.uid = cloneInfo.uid;
                info.appIndex = cloneInfo.appIndex;
            } else {
                LOG_W(BMS_TAG_QUERY, "can't find cloneInfos");
                return false;
            }
        }
    }
    return true;
}

ErrCode BundleDataMgr::IsSystemApp(const std::string &bundleName, bool &isSystemApp)
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto bundleInfoItem = bundleInfos_.find(bundleName);
    if (bundleInfoItem == bundleInfos_.end()) {
        APP_LOGW("%{public}s not found", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    isSystemApp = bundleInfoItem->second.IsSystemApp();
    return ERR_OK;
}

ErrCode BundleDataMgr::QueryAbilityInfoWithFlagsV9(const std::optional<AbilityInfo> &option,
    int32_t flags, int32_t userId, const InnerBundleInfo &innerBundleInfo, AbilityInfo &info,
    int32_t appIndex) const
{
    LOG_D(BMS_TAG_QUERY, "begin to QueryAbilityInfoWithFlagsV9");
    if ((static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_ONLY_SYSTEM_APP)) ==
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_ONLY_SYSTEM_APP) &&
        !innerBundleInfo.IsSystemApp()) {
        LOG_W(BMS_TAG_QUERY, "target not system app");
        return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
    }
    if (!(static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_DISABLE))) {
        if (!innerBundleInfo.IsAbilityEnabled((*option), userId, appIndex)) {
            LOG_W(BMS_TAG_QUERY, "bundleName:%{public}s ability:%{public}s is disabled",
                option->bundleName.c_str(), option->name.c_str());
            return ERR_BUNDLE_MANAGER_ABILITY_DISABLED;
        }
    }
    info = (*option);
    if ((static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_PERMISSION)) !=
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_PERMISSION)) {
        info.permissions.clear();
    }
    if ((static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_METADATA)) !=
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_METADATA)) {
        info.metaData.customizeData.clear();
        info.metadata.clear();
    }
    if ((static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_SKILL)) !=
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_SKILL)) {
        info.skills.clear();
    } else {
        innerBundleInfo.AppendDynamicSkillsToAbilityIfExist(info);
    }
    if ((static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_APPLICATION)) ==
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_APPLICATION)) {
        innerBundleInfo.GetApplicationInfoV9(static_cast<int32_t>(GetApplicationFlag::GET_APPLICATION_INFO_DEFAULT),
            userId, info.applicationInfo, appIndex);
    }
    // set uid for NAPI cache use
    const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
    if (innerBundleInfo.GetInnerBundleUserInfo(userId, innerBundleUserInfoPtr)) {
        if (!innerBundleUserInfoPtr) {
            LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by QueryAbilityInfoWithFlagsV9 is null");
            return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
        }
        if (appIndex == 0) {
            info.uid = innerBundleUserInfoPtr->uid;
        } else {
            std::string appIndexKey = InnerBundleUserInfo::AppIndexToKey(appIndex);
            if (innerBundleUserInfoPtr->cloneInfos.find(appIndexKey) != innerBundleUserInfoPtr->cloneInfos.end()) {
                auto cloneInfo = innerBundleUserInfoPtr->cloneInfos.at(appIndexKey);
                info.uid = cloneInfo.uid;
                info.appIndex = cloneInfo.appIndex;
            } else {
                return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
            }
        }
    }
    return ERR_OK;
}

bool BundleDataMgr::ImplicitQueryCurAbilityInfos(const Want &want, int32_t flags, int32_t userId,
    std::vector<AbilityInfo> &abilityInfos, int32_t appIndex) const
{
    LOG_D(BMS_TAG_QUERY, "begin to ImplicitQueryCurAbilityInfos");
    std::string bundleName = want.GetElement().GetBundleName();
    const InnerBundleInfo* innerBundleInfo = nullptr;
    if ((appIndex == 0) && (!GetInnerBundleInfoWithFlags(bundleName, flags, innerBundleInfo, userId))) {
        LOG_W(BMS_TAG_QUERY, "ImplicitQueryCurAbilityInfos failed bundleName:%{public}s", bundleName.c_str());
        return false;
    }
    InnerBundleInfo sandboxInfo;
    if (appIndex > 0) {
        if (sandboxAppHelper_ == nullptr) {
            LOG_W(BMS_TAG_QUERY, "sandboxAppHelper_ is nullptr");
            return false;
        }
        auto ret = sandboxAppHelper_->GetSandboxAppInfo(bundleName, appIndex, userId, sandboxInfo);
        if (ret != ERR_OK) {
            LOG_D(BMS_TAG_QUERY, "GetSandboxAppInfo failed errCode:%{public}d bundleName:%{public}s",
                ret, bundleName.c_str());
            return false;
        }
        innerBundleInfo = &sandboxInfo;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ImplicitQueryCurAbilityInfos is null.");
        return false;
    }
    int32_t responseUserId = innerBundleInfo->GetResponseUserId(userId);
    std::vector<std::string> mimeTypes;
    MimeTypeMgr::GetMimeTypeByUri(want.GetUriString(), mimeTypes);
    GetMatchAbilityInfos(want, flags, *innerBundleInfo, responseUserId, abilityInfos, mimeTypes);
    FilterAbilityInfosByModuleName(want.GetElement().GetModuleName(), abilityInfos);
    return true;
}

bool BundleDataMgr::ImplicitQueryCurCloneAbilityInfos(const Want &want, int32_t flags, int32_t userId,
    std::vector<AbilityInfo> &abilityInfos) const
{
    LOG_D(BMS_TAG_QUERY, "begin ImplicitQueryCurCloneAbilityInfos");
    std::string bundleName = want.GetElement().GetBundleName();
    std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexesNoLock(bundleName, userId);
    if (cloneAppIndexes.empty()) {
        return false;
    }
    std::vector<std::string> mimeTypes;
    MimeTypeMgr::GetMimeTypeByUri(want.GetUriString(), mimeTypes);
    for (int32_t appIndex: cloneAppIndexes) {
        const InnerBundleInfo* innerBundleInfo = nullptr;
        if (!GetInnerBundleInfoWithFlags(bundleName, flags, innerBundleInfo, userId, appIndex)) {
            continue;
        }
        if (!innerBundleInfo) {
            LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ImplicitQueryCurCloneAbilityInfos is null.");
            continue;
        }
        int32_t responseUserId = innerBundleInfo->GetResponseUserId(userId);

        GetMatchAbilityInfos(want, flags, *innerBundleInfo, responseUserId, abilityInfos, mimeTypes, appIndex);
        FilterAbilityInfosByModuleName(want.GetElement().GetModuleName(), abilityInfos);
    }
    LOG_D(BMS_TAG_QUERY, "end ImplicitQueryCurCloneAbilityInfos");
    return true;
}

ErrCode BundleDataMgr::ImplicitQueryCurAbilityInfosV9(const Want &want, int32_t flags, int32_t userId,
    std::vector<AbilityInfo> &abilityInfos, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    LOG_D(BMS_TAG_QUERY, "begin to ImplicitQueryCurAbilityInfosV9");
    if (appIndex < 0) {
        LOG_E(BMS_TAG_QUERY, "ImplicitQueryCurAbilityInfosV9 appIndex out of range");
        return ERR_APPEXECFWK_APP_INDEX_OUT_OF_RANGE;
    }
    std::string bundleName = want.GetElement().GetBundleName();
    const InnerBundleInfo* innerBundleInfo = nullptr;
    if (appIndex == 0) {
        ErrCode ret = GetInnerBundleInfoWithFlagsV9(bundleName, flags, innerBundleInfo, userId);
        if (ret != ERR_OK) {
            LOG_D(BMS_TAG_QUERY, "ImplicitQueryCurAbilityInfosV9 failed, bundleName:%{public}s",
                bundleName.c_str());
            return ret;
        }
    }
    InnerBundleInfo sandboxInfo;
    if (appIndex > 0) {
        if (sandboxAppHelper_ == nullptr) {
            LOG_W(BMS_TAG_QUERY, "sandboxAppHelper_ is nullptr");
            return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
        }
        auto ret = sandboxAppHelper_->GetSandboxAppInfo(bundleName, appIndex, userId, sandboxInfo);
        if (ret != ERR_OK) {
            LOG_D(BMS_TAG_QUERY, "GetSandboxAppInfo failed errCode %{public}d bundleName:%{public}s",
                ret, bundleName.c_str());
            return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
        }
        innerBundleInfo = &sandboxInfo;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ImplicitQueryCurAbilityInfosV9 is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    int32_t responseUserId = innerBundleInfo->GetResponseUserId(userId);
    std::vector<std::string> mimeTypes;
    MimeTypeMgr::GetMimeTypeByUri(want.GetUriString(), mimeTypes);
    GetMatchAbilityInfosV9(want, flags, *innerBundleInfo, responseUserId, abilityInfos, mimeTypes);
    FilterAbilityInfosByModuleName(want.GetElement().GetModuleName(), abilityInfos);
    return ERR_OK;
}

bool BundleDataMgr::ImplicitQueryCurCloneAbilityInfosV9(const Want &want, int32_t flags, int32_t userId,
    std::vector<AbilityInfo> &abilityInfos) const
{
    LOG_D(BMS_TAG_QUERY, "begin ImplicitQueryCurCloneAbilityInfosV9");
    std::string bundleName = want.GetElement().GetBundleName();

    std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexesNoLock(bundleName, userId);
    if (cloneAppIndexes.empty()) {
        return false;
    }
    std::vector<std::string> mimeTypes;
    MimeTypeMgr::GetMimeTypeByUri(want.GetUriString(), mimeTypes);
    for (int32_t appIndex: cloneAppIndexes) {
        const InnerBundleInfo* innerBundleInfo = nullptr;
        ErrCode ret = GetInnerBundleInfoWithFlagsV9(bundleName, flags, innerBundleInfo, userId, appIndex);
        if (ret != ERR_OK) {
            LOG_W(BMS_TAG_QUERY, "failed, bundleName:%{public}s, appIndex:%{public}d",
                bundleName.c_str(), appIndex);
            continue;
        }
        if (!innerBundleInfo) {
            LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ImplicitQueryCurCloneAbilityInfosV9 is null.");
            continue;
        }
        int32_t responseUserId = innerBundleInfo->GetResponseUserId(userId);
        GetMatchAbilityInfosV9(want, flags, *innerBundleInfo, responseUserId, abilityInfos, mimeTypes, appIndex);
        FilterAbilityInfosByModuleName(want.GetElement().GetModuleName(), abilityInfos);
    }
    LOG_D(BMS_TAG_QUERY, "end ImplicitQueryCurCloneAbilityInfosV9");
    return true;
}

void BundleDataMgr::ImplicitQueryAllAbilityInfos(const Want &want, int32_t flags, int32_t userId,
    std::vector<AbilityInfo> &abilityInfos, int32_t appIndex) const
{
    LOG_D(BMS_TAG_QUERY, "begin to ImplicitQueryAllAbilityInfos");
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        LOG_W(BMS_TAG_QUERY, "invalid userId");
        return;
    }
    std::vector<std::string> mimeTypes;
    MimeTypeMgr::GetMimeTypeByUri(want.GetUriString(), mimeTypes);
    // query from bundleInfos_
    if (appIndex == 0) {
        for (const auto &item : bundleInfos_) {
            const InnerBundleInfo &innerBundleInfo = item.second;
            int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
            if (CheckInnerBundleInfoWithFlags(innerBundleInfo, flags, responseUserId) != ERR_OK) {
                LOG_D(BMS_TAG_QUERY,
                    "ImplicitQueryAllAbilityInfos failed, bundleName:%{public}s, responseUserId:%{public}d",
                    innerBundleInfo.GetBundleName().c_str(), responseUserId);
                continue;
            }
            GetMatchAbilityInfos(want, flags, innerBundleInfo, responseUserId, abilityInfos, mimeTypes);
        }
    } else {
        // query from sandbox manager for sandbox bundle
        if (sandboxAppHelper_ == nullptr) {
            LOG_W(BMS_TAG_QUERY, "sandboxAppHelper_ is nullptr");
            return;
        }
        auto sandboxMap = sandboxAppHelper_->GetSandboxAppInfoMap();
        for (const auto &item : sandboxMap) {
            InnerBundleInfo info;
            size_t pos = item.first.rfind(Constants::FILE_UNDERLINE);
            if (pos == std::string::npos) {
                LOG_D(BMS_TAG_QUERY, "sandbox map contains invalid element");
                continue;
            }
            std::string innerBundleName = item.first.substr(pos + 1);
            if (sandboxAppHelper_->GetSandboxAppInfo(innerBundleName, appIndex, userId, info) != ERR_OK) {
                LOG_D(BMS_TAG_QUERY, "obtain innerBundleInfo of sandbox app failed");
                continue;
            }
            int32_t responseUserId = info.GetResponseUserId(userId);
            GetMatchAbilityInfos(want, flags, info, responseUserId, abilityInfos, mimeTypes);
        }
    }
    APP_LOGD("finish to ImplicitQueryAllAbilityInfos");
}

void BundleDataMgr::ImplicitQueryAllCloneAbilityInfos(const Want &want, int32_t flags, int32_t userId,
    std::vector<AbilityInfo> &abilityInfos) const
{
    LOG_D(BMS_TAG_QUERY, "begin ImplicitQueryAllCloneAbilityInfos");
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        LOG_W(BMS_TAG_QUERY, "invalid userId");
        return;
    }
    std::vector<std::string> mimeTypes;
    MimeTypeMgr::GetMimeTypeByUri(want.GetUriString(), mimeTypes);
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &innerBundleInfo = item.second;
        std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexesNoLock(innerBundleInfo.GetBundleName(), userId);
        if (cloneAppIndexes.empty()) {
            continue;
        }
        int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
        for (int32_t appIndex: cloneAppIndexes) {
            if (CheckInnerBundleInfoWithFlags(innerBundleInfo, flags, responseUserId, appIndex) != ERR_OK) {
                LOG_D(BMS_TAG_QUERY,
                    "failed, bundleName:%{public}s, responseUserId:%{public}d, appIndex:%{public}d",
                    innerBundleInfo.GetBundleName().c_str(), responseUserId, appIndex);
                continue;
            }
            GetMatchAbilityInfos(want, flags, innerBundleInfo, responseUserId, abilityInfos, mimeTypes, appIndex);
        }
    }
    LOG_D(BMS_TAG_QUERY, "end ImplicitQueryAllCloneAbilityInfos");
}

void BundleDataMgr::ImplicitQueryAllAbilityInfosV9(const Want &want, int32_t flags, int32_t userId,
    std::vector<AbilityInfo> &abilityInfos, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    LOG_D(BMS_TAG_QUERY, "begin to ImplicitQueryAllAbilityInfosV9");
    // query from bundleInfos_
    std::vector<std::string> mimeTypes;
    MimeTypeMgr::GetMimeTypeByUri(want.GetUriString(), mimeTypes);
    if (appIndex == 0) {
        for (const auto &item : bundleInfos_) {
            const InnerBundleInfo &innerBundleInfo = item.second;
            ErrCode ret = CheckBundleAndAbilityDisabled(innerBundleInfo, flags, userId);
            if (ret != ERR_OK) {
                continue;
            }

            int32_t responseUserId = innerBundleInfo.GetResponseUserId(userId);
            GetMatchAbilityInfosV9(want, flags, innerBundleInfo, responseUserId, abilityInfos, mimeTypes);
        }
    } else {
        // query from sandbox manager for sandbox bundle
        if (sandboxAppHelper_ == nullptr) {
            LOG_W(BMS_TAG_QUERY, "sandboxAppHelper_ is nullptr");
            return;
        }
        auto sandboxMap = sandboxAppHelper_->GetSandboxAppInfoMap();
        for (const auto &item : sandboxMap) {
            InnerBundleInfo info;
            size_t pos = item.first.rfind(Constants::FILE_UNDERLINE);
            if (pos == std::string::npos) {
                LOG_W(BMS_TAG_QUERY, "sandbox map contains invalid element");
                continue;
            }
            std::string innerBundleName = item.first.substr(pos + 1);
            if (sandboxAppHelper_->GetSandboxAppInfo(innerBundleName, appIndex, userId, info) != ERR_OK) {
                LOG_D(BMS_TAG_QUERY, "obtain innerBundleInfo of sandbox app failed");
                continue;
            }

            int32_t responseUserId = info.GetResponseUserId(userId);
            GetMatchAbilityInfosV9(want, flags, info, responseUserId, abilityInfos, mimeTypes);
        }
    }
    LOG_D(BMS_TAG_QUERY, "finish to ImplicitQueryAllAbilityInfosV9");
}

void BundleDataMgr::ImplicitQueryAllCloneAbilityInfosV9(const Want &want, int32_t flags, int32_t userId,
    std::vector<AbilityInfo> &abilityInfos) const
{
    LOG_D(BMS_TAG_QUERY, "begin ImplicitQueryAllCloneAbilityInfosV9");
    std::vector<std::string> mimeTypes;
    MimeTypeMgr::GetMimeTypeByUri(want.GetUriString(), mimeTypes);
    for (const auto &item : bundleInfos_) {
        std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexesNoLock(item.second.GetBundleName(), userId);
        if (cloneAppIndexes.empty()) {
            continue;
        }
        for (int32_t appIndex: cloneAppIndexes) {
            const InnerBundleInfo* innerBundleInfo = nullptr;
            ErrCode ret = GetInnerBundleInfoWithFlagsV9(item.first, flags, innerBundleInfo, userId, appIndex);
            if (ret != ERR_OK) {
                LOG_W(BMS_TAG_QUERY, "failed, bundleName:%{public}s, appIndex:%{public}d",
                    item.first.c_str(), appIndex);
                continue;
            }
            if (!innerBundleInfo) {
                LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ImplicitQueryAllCloneAbilityInfosV9 is null.");
                continue;
            }
            int32_t responseUserId = innerBundleInfo->GetResponseUserId(userId);
            GetMatchAbilityInfosV9(want, flags, *innerBundleInfo, responseUserId, abilityInfos, mimeTypes, appIndex);
        }
    }
    LOG_D(BMS_TAG_QUERY, "end ImplicitQueryAllCloneAbilityInfosV9");
}

bool BundleDataMgr::CheckAbilityInfoFlagExist(int32_t flags, AbilityInfoFlag abilityInfoFlag) const
{
    return (static_cast<uint32_t>(flags) & static_cast<uint32_t>(abilityInfoFlag)) == abilityInfoFlag;
}

void BundleDataMgr::GetMatchAbilityInfos(const Want &want, int32_t flags, const InnerBundleInfo &info,
    int32_t userId, std::vector<AbilityInfo> &abilityInfos,
    const std::vector<std::string> &paramMimeTypes, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    if (CheckAbilityInfoFlagExist(flags, GET_ABILITY_INFO_SYSTEMAPP_ONLY) && !info.IsSystemApp()) {
        return;
    }
    const std::map<std::string, std::vector<Skill>> skillInfos = info.GetInnerSkillInfos();
    for (const auto &abilityInfoPair : info.GetInnerAbilityInfos()) {
        bool isPrivateType = MatchPrivateType(
            want, abilityInfoPair.second.supportExtNames, abilityInfoPair.second.supportMimeTypes, paramMimeTypes);
        auto skillsPair = skillInfos.find(abilityInfoPair.first);
        if (skillsPair == skillInfos.end()) {
            continue;
        }
        for (size_t skillIndex = 0; skillIndex < skillsPair->second.size(); ++skillIndex) {
            const Skill &skill = skillsPair->second[skillIndex];
            size_t matchUriIndex = 0;
            if (isPrivateType || skill.Match(want, matchUriIndex)) {
                AbilityInfo abilityinfo = InnerAbilityInfo::ConvertToAbilityInfo(abilityInfoPair.second);
                if (abilityinfo.name == ServiceConstants::APP_DETAIL_ABILITY) {
                    continue;
                }
                if (!CheckAbilityInfoFlagExist(flags, GET_ABILITY_INFO_WITH_DISABLE) &&
                    !info.IsAbilityEnabled(abilityinfo, GetUserId(userId), appIndex)) {
                    LOG_W(BMS_TAG_QUERY, "Ability %{public}s is disabled", abilityinfo.name.c_str());
                    continue;
                }
                if (CheckAbilityInfoFlagExist(flags, GET_ABILITY_INFO_WITH_APPLICATION)) {
                    info.GetApplicationInfo(ApplicationFlag::GET_APPLICATION_INFO_WITH_CERTIFICATE_FINGERPRINT,
                        userId, abilityinfo.applicationInfo, appIndex);
                }
                if (!CheckAbilityInfoFlagExist(flags, GET_ABILITY_INFO_WITH_PERMISSION)) {
                    abilityinfo.permissions.clear();
                }
                if (!CheckAbilityInfoFlagExist(flags, GET_ABILITY_INFO_WITH_METADATA)) {
                    abilityinfo.metaData.customizeData.clear();
                    abilityinfo.metadata.clear();
                }
                if (!CheckAbilityInfoFlagExist(flags, GET_ABILITY_INFO_WITH_SKILL)) {
                    abilityinfo.skills.clear();
                } else {
                    info.AppendDynamicSkillsToAbilityIfExist(abilityinfo);
                }
                if (CheckAbilityInfoFlagExist(flags, GET_ABILITY_INFO_WITH_SKILL_URI)) {
                    AddSkillUrisInfo(skillsPair->second, abilityinfo.skillUri, skillIndex, matchUriIndex);
                }
                abilityinfo.appIndex = appIndex;
                abilityInfos.emplace_back(abilityinfo);
                break;
            }
        }
    }
}

void BundleDataMgr::AddSkillUrisInfo(const std::vector<Skill> &skills,
    std::vector<SkillUriForAbilityAndExtension> &skillUris,
    std::optional<size_t> matchSkillIndex, std::optional<size_t> matchUriIndex) const
{
    for (size_t skillIndex = 0; skillIndex < skills.size(); ++skillIndex) {
        const Skill &skill = skills[skillIndex];
        for (size_t uriIndex = 0; uriIndex < skill.uris.size(); ++uriIndex) {
            const SkillUri &uri = skill.uris[uriIndex];
            SkillUriForAbilityAndExtension skillinfo;
            skillinfo.scheme = uri.scheme;
            skillinfo.host = uri.host;
            skillinfo.port = uri.port;
            skillinfo.path = uri.path;
            skillinfo.pathStartWith = uri.pathStartWith;
            skillinfo.pathRegex = uri.pathRegex;
            skillinfo.type = uri.type;
            skillinfo.utd = uri.utd;
            skillinfo.maxFileSupported = uri.maxFileSupported;
            skillinfo.linkFeature = uri.linkFeature;
            if (matchSkillIndex.has_value() && matchUriIndex.has_value() &&
                skillIndex == matchSkillIndex.value() && uriIndex == matchUriIndex.value()) {
                skillinfo.isMatch = true;
            }
            skillUris.emplace_back(skillinfo);
        }
    }
}

void BundleDataMgr::EmplaceAbilityInfo(const InnerBundleInfo &info, const std::vector<Skill> &skills,
    AbilityInfo &abilityInfo, int32_t flags, int32_t userId, std::vector<AbilityInfo> &infos,
    std::optional<size_t> matchSkillIndex, std::optional<size_t> matchUriIndex, int32_t appIndex) const
{
    if (!(static_cast<uint32_t>(flags) & static_cast<uint32_t>(
        GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_DISABLE))) {
        if (!info.IsAbilityEnabled(abilityInfo, GetUserId(userId), appIndex)) {
            LOG_W(BMS_TAG_QUERY, "Ability %{public}s is disabled", abilityInfo.name.c_str());
            return;
        }
    }
    if ((static_cast<uint32_t>(flags) &
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_APPLICATION)) ==
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_APPLICATION)) {
        info.GetApplicationInfoV9(static_cast<uint32_t>(GetApplicationFlag::GET_APPLICATION_INFO_DEFAULT),
            userId, abilityInfo.applicationInfo, appIndex);
    }
    if ((static_cast<uint32_t>(flags) &
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_PERMISSION)) !=
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_PERMISSION)) {
        abilityInfo.permissions.clear();
    }
    if ((static_cast<uint32_t>(flags) &
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_METADATA)) !=
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_METADATA)) {
        abilityInfo.metaData.customizeData.clear();
        abilityInfo.metadata.clear();
    }
    if ((static_cast<uint32_t>(flags) &
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_SKILL)) !=
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_SKILL)) {
        abilityInfo.skills.clear();
    } else {
        info.AppendDynamicSkillsToAbilityIfExist(abilityInfo);
    }
    if ((static_cast<uint32_t>(flags) &
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_SKILL_URI)) ==
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_SKILL_URI)) {
        AddSkillUrisInfo(skills, abilityInfo.skillUri, matchSkillIndex, matchUriIndex);
    }
    if (appIndex > Constants::INITIAL_APP_INDEX && appIndex <= Constants::INITIAL_SANDBOX_APP_INDEX) {
        // set uid for NAPI cache use
        const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
        if (info.GetInnerBundleUserInfo(userId, innerBundleUserInfoPtr)) {
            if (innerBundleUserInfoPtr) {
                std::string appIndexKey = InnerBundleUserInfo::AppIndexToKey(appIndex);
                if (innerBundleUserInfoPtr->cloneInfos.find(appIndexKey) != innerBundleUserInfoPtr->cloneInfos.end()) {
                    abilityInfo.uid = innerBundleUserInfoPtr->cloneInfos.at(appIndexKey).uid;
                    abilityInfo.appIndex = innerBundleUserInfoPtr->cloneInfos.at(appIndexKey).appIndex;
                }
            } else {
                LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by EmplaceAbilityInfo is null");
            }
        }
    }
    infos.emplace_back(abilityInfo);
}

void BundleDataMgr::GetMatchAbilityInfosV9(const Want &want, int32_t flags, const InnerBundleInfo &info,
    int32_t userId, std::vector<AbilityInfo> &abilityInfos,
    const std::vector<std::string> &paramMimeTypes, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    if ((static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_ONLY_SYSTEM_APP)) ==
        static_cast<uint32_t>((GetAbilityInfoFlag::GET_ABILITY_INFO_ONLY_SYSTEM_APP)) && !info.IsSystemApp()) {
        LOG_W(BMS_TAG_QUERY, "target not system app");
        return;
    }
    const std::map<std::string, std::vector<Skill>> skillInfos = info.GetInnerSkillInfos();
    for (const auto &abilityInfoPair : info.GetInnerAbilityInfos()) {
        AbilityInfo abilityinfo = InnerAbilityInfo::ConvertToAbilityInfo(abilityInfoPair.second);
        auto skillsPair = skillInfos.find(abilityInfoPair.first);
        if (skillsPair == skillInfos.end()) {
            continue;
        }
        bool isPrivateType = MatchPrivateType(
            want, abilityInfoPair.second.supportExtNames, abilityInfoPair.second.supportMimeTypes, paramMimeTypes);
        if (isPrivateType) {
            EmplaceAbilityInfo(info, skillsPair->second, abilityinfo, flags, userId, abilityInfos,
                std::nullopt, std::nullopt, appIndex);
            continue;
        }
        if (want.GetAction() == SHARE_ACTION) {
            if (!MatchShare(want, skillsPair->second)) {
                continue;
            }
            EmplaceAbilityInfo(info, skillsPair->second, abilityinfo, flags, userId, abilityInfos,
                std::nullopt, std::nullopt, appIndex);
            continue;
        }
        for (size_t skillIndex = 0; skillIndex < skillsPair->second.size(); ++skillIndex) {
            const Skill &skill = skillsPair->second[skillIndex];
            size_t matchUriIndex = 0;
            if (skill.Match(want, matchUriIndex)) {
                if (abilityinfo.name == ServiceConstants::APP_DETAIL_ABILITY) {
                    continue;
                }
                EmplaceAbilityInfo(info, skillsPair->second, abilityinfo, flags, userId, abilityInfos,
                    skillIndex, matchUriIndex, appIndex);
                break;
            }
        }
    }
}

bool BundleDataMgr::MatchShare(const Want &want, const std::vector<Skill> &skills) const
{
    if (want.GetAction() != SHARE_ACTION) {
        LOG_E(BMS_TAG_QUERY, "action not action");
        return false;
    }
    std::vector<Skill> shareActionSkills = FindSkillsContainShareAction(skills);
    if (shareActionSkills.empty()) {
        LOG_D(BMS_TAG_QUERY, "shareActionSkills is empty");
        return false;
    }
    auto wantParams = want.GetParams();
    auto pickerSummary = wantParams.GetWantParams(WANT_PARAM_PICKER_SUMMARY);
    int32_t totalCount = pickerSummary.GetIntParam(SUMMARY_TOTAL_COUNT, DEFAULT_SUMMARY_COUNT);
    if (totalCount <= DEFAULT_SUMMARY_COUNT) {
        LOG_W(BMS_TAG_QUERY, "Invalid total count");
    }
    auto shareSummary = pickerSummary.GetWantParams(WANT_PARAM_SUMMARY);
    auto utds = shareSummary.KeySet();
    for (auto &skill : shareActionSkills) {
        bool match = true;
        for (const auto &utd : utds) {
            int32_t count = shareSummary.GetIntParam(utd, DEFAULT_SUMMARY_COUNT);
            if (!MatchUtd(skill, utd, count)) {
                match = false;
                break;
            }
        }
        if (match) {
            return true;
        }
    }
    return false;
}

bool BundleDataMgr::MatchUtd(Skill &skill, const std::string &utd, int32_t count) const
{
    if (skill.uris.empty() || count <= DEFAULT_SUMMARY_COUNT) {
        LOG_W(BMS_TAG_QUERY, "skill.uris is empty or invalid utd count");
        return false;
    }
    bool isMatch = false;
    for (SkillUri &skillUri : skill.uris) {
        if (!skillUri.utd.empty()) {
            if (MatchUtd(skillUri.utd, utd)) {
                skillUri.maxFileSupported -= count;
                isMatch = true;
                if (skillUri.maxFileSupported < 0) {
                    return false;
                }
            }
        } else {
            if (MatchTypeWithUtd(skillUri.type, utd)) {
                skillUri.maxFileSupported -= count;
                isMatch = true;
                if (skillUri.maxFileSupported < 0) {
                    return false;
                }
            }
        }
    }
    return isMatch;
}

bool BundleDataMgr::MatchUtd(const std::string &skillUtd, const std::string &wantUtd) const
{
#ifdef BUNDLE_FRAMEWORK_UDMF_ENABLED
    LOG_D(BMS_TAG_QUERY, "skillUtd %{public}s, wantUtd %{public}s", skillUtd.c_str(), wantUtd.c_str());
    std::shared_ptr<UDMF::TypeDescriptor> wantTypeDescriptor;
    auto ret = UDMF::UtdClient::GetInstance().GetTypeDescriptor(wantUtd, wantTypeDescriptor);
    if (ret != ERR_OK || wantTypeDescriptor == nullptr) {
        LOG_W(BMS_TAG_QUERY, "GetTypeDescriptor failed");
        return false;
    }
    bool matchRet = false;
    ret = wantTypeDescriptor->BelongsTo(skillUtd, matchRet);
    if (ret != ERR_OK) {
        LOG_W(BMS_TAG_QUERY, "GetTypeDescriptor failed");
        return false;
    }
    return matchRet;
#endif
    return false;
}

bool BundleDataMgr::MatchTypeWithUtd(const std::string &mimeType, const std::string &wantUtd) const
{
#ifdef BUNDLE_FRAMEWORK_UDMF_ENABLED
    LOG_D(BMS_TAG_QUERY, "mimeType %{public}s, wantUtd %{public}s", mimeType.c_str(), wantUtd.c_str());
    std::vector<std::string> typeUtdVector = BundleUtil::GetUtdVectorByMimeType(mimeType);
    for (const std::string &typeUtd : typeUtdVector) {
        if (MatchUtd(typeUtd, wantUtd)) {
            return true;
        }
    }
    return false;
#endif
    return false;
}

std::vector<Skill> BundleDataMgr::FindSkillsContainShareAction(const std::vector<Skill> &skills) const
{
    std::vector<Skill> shareActionSkills;
    for (const auto &skill : skills) {
        auto &actions = skill.actions;
        auto matchAction = std::find_if(std::begin(actions), std::end(actions), [](const auto &action) {
            return SHARE_ACTION == action;
        });
        if (matchAction == actions.end()) {
            continue;
        }
        shareActionSkills.emplace_back(skill);
    }
    return shareActionSkills;
}

void BundleDataMgr::ModifyLauncherAbilityInfo(bool isStage, AbilityInfo &abilityInfo) const
{
    if (abilityInfo.labelId == 0) {
        if (isStage) {
            abilityInfo.labelId = abilityInfo.applicationInfo.labelId;
            abilityInfo.label = abilityInfo.applicationInfo.label;
        } else {
            abilityInfo.applicationInfo.label = abilityInfo.bundleName;
            abilityInfo.label = abilityInfo.bundleName;
        }
    }

    if (abilityInfo.iconId == 0) {
        abilityInfo.iconId = abilityInfo.applicationInfo.iconId;
    }
}

void BundleDataMgr::GetMatchLauncherAbilityInfos(const Want& want,
    const InnerBundleInfo& info, std::vector<AbilityInfo>& abilityInfos,
    int64_t installTime, int32_t userId) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGD("request user id is invalid");
        return;
    }
    int32_t responseUserId = info.GetResponseUserId(requestUserId);
    if (responseUserId == Constants::INVALID_USERID) {
        APP_LOGD("response user id is invalid");
        return;
    }
    // get clone bundle info
    const InnerBundleUserInfo *bundleUserInfoPtr = nullptr;
    (void)info.GetInnerBundleUserInfo(responseUserId, bundleUserInfoPtr);
    if (!bundleUserInfoPtr) {
        LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetMatchLauncherAbilityInfos is null.");
        return;
    }
    if (ServiceConstants::ALLOW_MULTI_ICON_BUNDLE.find(info.GetBundleName()) !=
        ServiceConstants::ALLOW_MULTI_ICON_BUNDLE.end()) {
        GetMultiLauncherAbilityInfo(want, info, *bundleUserInfoPtr, installTime, abilityInfos);
        return;
    }
    AbilityInfo mainAbilityInfo;
    info.GetMainAbilityInfo(mainAbilityInfo);
    if (!mainAbilityInfo.name.empty() && (mainAbilityInfo.type == AbilityType::PAGE)) {
        APP_LOGD("bundleName %{public}s exist mainAbility", info.GetBundleName().c_str());
        info.GetApplicationInfo(ApplicationFlag::GET_APPLICATION_INFO_WITH_CERTIFICATE_FINGERPRINT,
            responseUserId, mainAbilityInfo.applicationInfo);
        BmsExtensionDataMgr bmsExtensionDataMgr;
        if (mainAbilityInfo.applicationInfo.removable &&
            !bmsExtensionDataMgr.IsTargetApp(info.GetBundleName(), info.GetAppIdentifier())) {
            mainAbilityInfo.applicationInfo.removable = info.GetUninstallState() && bundleUserInfoPtr->canUninstall;
        }
        mainAbilityInfo.installTime = installTime;
        // fix labelId or iconId is equal 0
        ModifyLauncherAbilityInfo(info.GetIsNewVersion(), mainAbilityInfo);
        abilityInfos.emplace_back(mainAbilityInfo);
        GetMatchLauncherAbilityInfosForCloneInfos(info, mainAbilityInfo, *bundleUserInfoPtr,
            abilityInfos);
        return;
    }
    // add app detail ability
    if (info.GetBaseApplicationInfo().needAppDetail) {
        LOG_D(BMS_TAG_QUERY, "bundleName: %{public}s add detail ability info", info.GetBundleName().c_str());
        std::string moduleName = "";
        auto ability = info.FindAbilityInfo(moduleName, ServiceConstants::APP_DETAIL_ABILITY, responseUserId);
        if (!ability) {
            LOG_D(BMS_TAG_QUERY, "bundleName: %{public}s cant find ability", info.GetBundleName().c_str());
            return;
        }
        if (!info.GetIsNewVersion()) {
            ability->applicationInfo.label = info.GetBundleName();
        }
        ability->installTime = installTime;
        abilityInfos.emplace_back(*ability);
        GetMatchLauncherAbilityInfosForCloneInfos(info, *ability, *bundleUserInfoPtr, abilityInfos);
    }
}

void BundleDataMgr::GetMultiLauncherAbilityInfo(const Want& want,
    const InnerBundleInfo& info, const InnerBundleUserInfo &bundleUserInfo,
    int64_t installTime, std::vector<AbilityInfo>& abilityInfos) const
{
    int32_t count = 0;
    const std::map<std::string, std::vector<Skill>> skillInfos = info.GetInnerSkillInfos();
    for (const auto& abilityInfoPair : info.GetInnerAbilityInfos()) {
        auto skillsPair = skillInfos.find(abilityInfoPair.first);
        if (skillsPair == skillInfos.end()) {
            continue;
        }
        for (const Skill& skill : skillsPair->second) {
            if (skill.MatchLauncher(want) && (abilityInfoPair.second.type == AbilityType::PAGE)) {
                count++;
                AbilityInfo abilityInfo = InnerAbilityInfo::ConvertToAbilityInfo(abilityInfoPair.second);
                AbilityInfo tmpInfo = abilityInfo;
                info.GetApplicationInfo(ApplicationFlag::GET_APPLICATION_INFO_WITH_CERTIFICATE_FINGERPRINT,
                    bundleUserInfo.bundleUserInfo.userId, abilityInfo.applicationInfo);
                abilityInfo.installTime = installTime;
                // fix labelId or iconId is equal 0
                ModifyLauncherAbilityInfo(info.GetIsNewVersion(), abilityInfo);
                abilityInfos.emplace_back(abilityInfo);
                GetMatchLauncherAbilityInfosForCloneInfos(info, tmpInfo, bundleUserInfo, abilityInfos);
                break;
            }
        }
    }
    APP_LOGI_NOFUNC("GetMultiLauncherAbilityInfo -n %{public}s has %{public}d launcher ability",
        info.GetBundleName().c_str(), count);
}

void BundleDataMgr::GetMatchLauncherAbilityInfosForCloneInfos(
    const InnerBundleInfo& info,
    const AbilityInfo &abilityInfo,
    const InnerBundleUserInfo &bundleUserInfo,
    std::vector<AbilityInfo>& abilityInfos) const
{
    for (const auto &item : bundleUserInfo.cloneInfos) {
        APP_LOGD("bundleName:%{public}s appIndex:%{public}d start", info.GetBundleName().c_str(), item.second.appIndex);
        AbilityInfo cloneAbilityInfo = abilityInfo;
        info.GetApplicationInfo(ApplicationFlag::GET_APPLICATION_INFO_WITH_CERTIFICATE_FINGERPRINT,
            bundleUserInfo.bundleUserInfo.userId, cloneAbilityInfo.applicationInfo, item.second.appIndex);
        cloneAbilityInfo.installTime = item.second.installTime;
        cloneAbilityInfo.uid = item.second.uid;
        cloneAbilityInfo.appIndex = item.second.appIndex;
        // fix labelId or iconId is equal 0
        ModifyLauncherAbilityInfo(info.GetIsNewVersion(), cloneAbilityInfo);
        abilityInfos.emplace_back(cloneAbilityInfo);
    }
}

void BundleDataMgr::ModifyApplicationInfoByCloneInfo(const InnerBundleCloneInfo &cloneInfo,
    ApplicationInfo &applicationInfo) const
{
    applicationInfo.accessTokenId = cloneInfo.accessTokenId;
    applicationInfo.accessTokenIdEx = cloneInfo.accessTokenIdEx;
    applicationInfo.enabled = cloneInfo.enabled;
    applicationInfo.uid = cloneInfo.uid;
    applicationInfo.appIndex = cloneInfo.appIndex;
}

void BundleDataMgr::ModifyBundleInfoByCloneInfo(const InnerBundleCloneInfo &cloneInfo,
    BundleInfo &bundleInfo) const
{
    bundleInfo.uid = cloneInfo.uid;
    bundleInfo.gid = cloneInfo.uid; // no gids, need add
    bundleInfo.installTime = cloneInfo.installTime;
    bundleInfo.appIndex = cloneInfo.appIndex;
    if (!bundleInfo.applicationInfo.bundleName.empty()) {
        ModifyApplicationInfoByCloneInfo(cloneInfo, bundleInfo.applicationInfo);
    }
}

void BundleDataMgr::GetCloneBundleInfos(const InnerBundleInfo& info, int32_t flags, int32_t userId,
    BundleInfo &bundleInfo, std::vector<BundleInfo> &bundleInfos) const
{
    // get clone bundle info
    const InnerBundleUserInfo *bundleUserInfoPtr = nullptr;
    (void)info.GetInnerBundleUserInfo(userId, bundleUserInfoPtr);
    if (!bundleUserInfoPtr) {
        LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetCloneBundleInfos is null");
        return;
    }
    if (bundleUserInfoPtr->cloneInfos.empty()) {
        return;
    }
    LOG_D(BMS_TAG_QUERY, "app %{public}s start get bundle clone info",
        info.GetBundleName().c_str());
    for (const auto &item : bundleUserInfoPtr->cloneInfos) {
        BundleInfo cloneBundleInfo;
        ErrCode ret = info.GetBundleInfoV9(flags, cloneBundleInfo, userId, item.second.appIndex);
        if (ret == ERR_OK) {
            ProcessCertificate(cloneBundleInfo, info.GetBundleName(), flags);
            ProcessBundleMenu(cloneBundleInfo, flags, true);
            ProcessBundleRouterMap(cloneBundleInfo, flags, userId);
            bundleInfos.emplace_back(std::move(cloneBundleInfo));
        }
    }
}

void BundleDataMgr::GetBundleNameAndIndexByName(
    const std::string &keyName, std::string &bundleName, int32_t &appIndex) const
{
    bundleName = keyName;
    appIndex = 0;
    // for clone bundle name
    auto pos = keyName.find(CLONE_BUNDLE_PREFIX);
    if ((pos == std::string::npos) || (pos == 0)) {
        return;
    }
    std::string index = keyName.substr(0, pos);
    if (!OHOS::StrToInt(index, appIndex)) {
        appIndex = 0;
        return;
    }
    bundleName = keyName.substr(pos + strlen(CLONE_BUNDLE_PREFIX));
}

std::vector<int32_t> BundleDataMgr::GetCloneAppIndexes(const std::string &bundleName, int32_t userId) const
{
    std::vector<int32_t> cloneAppIndexes;
    std::vector<InnerBundleUserInfo> innerBundleUserInfos;
    if (userId == Constants::ANY_USERID) {
        if (!GetInnerBundleUserInfos(bundleName, innerBundleUserInfos)) {
            LOG_W(BMS_TAG_QUERY, "no userInfos for this bundle(%{public}s)", bundleName.c_str());
            return cloneAppIndexes;
        }
        userId = innerBundleUserInfos.begin()->bundleUserInfo.userId;
    }
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return cloneAppIndexes;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        LOG_W(BMS_TAG_QUERY, "no bundleName %{public}s found", bundleName.c_str());
        return cloneAppIndexes;
    }
    const InnerBundleInfo &bundleInfo = infoItem->second;
    const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
    if (!bundleInfo.GetInnerBundleUserInfo(requestUserId, innerBundleUserInfoPtr)) {
        return cloneAppIndexes;
    }
    if (!innerBundleUserInfoPtr) {
        LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetCloneAppIndexes is null");
        return cloneAppIndexes;
    }
    const std::map<std::string, InnerBundleCloneInfo> &cloneInfos = innerBundleUserInfoPtr->cloneInfos;
    if (cloneInfos.empty()) {
        return cloneAppIndexes;
    }
    for (const auto &cloneInfo : cloneInfos) {
        LOG_I(BMS_TAG_QUERY, "get cloneAppIndexes: %{public}d", cloneInfo.second.appIndex);
        cloneAppIndexes.emplace_back(cloneInfo.second.appIndex);
    }
    return cloneAppIndexes;
}

std::set<int32_t> BundleDataMgr::GetCloneAppIndexes(const std::string &bundleName) const
{
    std::set<int32_t> cloneAppIndexes;
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        LOG_W(BMS_TAG_QUERY, "no bundleName %{public}s found", bundleName.c_str());
        return cloneAppIndexes;
    }
    return infoItem->second.GetCloneBundleAppIndexes();
}

std::vector<int32_t> BundleDataMgr::GetCloneAppIndexesNoLock(const std::string &bundleName, int32_t userId) const
{
    std::vector<int32_t> cloneAppIndexes;
    std::vector<InnerBundleUserInfo> innerBundleUserInfos;
    if (userId == Constants::ANY_USERID) {
        if (!GetInnerBundleUserInfos(bundleName, innerBundleUserInfos)) {
            LOG_W(BMS_TAG_QUERY, "no userInfos for this bundle(%{public}s)", bundleName.c_str());
            return cloneAppIndexes;
        }
        userId = innerBundleUserInfos.begin()->bundleUserInfo.userId;
    }
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return cloneAppIndexes;
    }
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        LOG_W(BMS_TAG_QUERY, "no bundleName %{public}s found", bundleName.c_str());
        return cloneAppIndexes;
    }
    const InnerBundleInfo &bundleInfo = infoItem->second;
    const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
    if (!bundleInfo.GetInnerBundleUserInfo(requestUserId, innerBundleUserInfoPtr)) {
        return cloneAppIndexes;
    }
    if (!innerBundleUserInfoPtr) {
        LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetCloneAppIndexesNoLock is null");
        return cloneAppIndexes;
    }
    const std::map<std::string, InnerBundleCloneInfo> &cloneInfos = innerBundleUserInfoPtr->cloneInfos;
    if (cloneInfos.empty()) {
        return cloneAppIndexes;
    }
    for (const auto &cloneInfo : cloneInfos) {
        LOG_I(BMS_TAG_QUERY, "get cloneAppIndexes unLock: %{public}d", cloneInfo.second.appIndex);
        cloneAppIndexes.emplace_back(cloneInfo.second.appIndex);
    }
    return cloneAppIndexes;
}

void BundleDataMgr::AddAppDetailAbilityInfo(InnerBundleInfo &info) const
{
    InnerAbilityInfo appDetailAbility;
    appDetailAbility.name = ServiceConstants::APP_DETAIL_ABILITY;
    appDetailAbility.bundleName = info.GetBundleName();
    appDetailAbility.enabled = true;
    appDetailAbility.type = AbilityType::PAGE;
    appDetailAbility.isNativeAbility = true;

    ApplicationInfo applicationInfo = info.GetBaseApplicationInfo();
    appDetailAbility.applicationName = applicationInfo.name;
    appDetailAbility.labelId = applicationInfo.labelResource.id;
    if (!info.GetIsNewVersion()) {
        appDetailAbility.labelId = 0;
        appDetailAbility.label = info.GetBundleName();
    }
    appDetailAbility.iconId = applicationInfo.iconResource.id;
    appDetailAbility.moduleName = applicationInfo.iconResource.moduleName;

    if ((appDetailAbility.iconId == 0) || !info.GetIsNewVersion()) {
        LOG_D(BMS_TAG_QUERY, "AddAppDetailAbilityInfo appDetailAbility.iconId is 0");
        // get system resource icon Id
        auto iter = bundleInfos_.find(GLOBAL_RESOURCE_BUNDLE_NAME);
        if (iter != bundleInfos_.end()) {
            LOG_D(BMS_TAG_QUERY, "AddAppDetailAbilityInfo get system resource iconId");
            appDetailAbility.iconId = iter->second.GetBaseApplicationInfo().iconId;
        } else {
            LOG_W(BMS_TAG_QUERY, "AddAppDetailAbilityInfo error: ohos.global.systemres does not exist");
        }
    }
    // not show in the mission list
    appDetailAbility.removeMissionAfterTerminate = true;
    // set hapPath, for label resource
    auto innerModuleInfo = info.GetInnerModuleInfoByModuleName(appDetailAbility.moduleName);
    if (innerModuleInfo) {
        appDetailAbility.package = innerModuleInfo->modulePackage;
        appDetailAbility.hapPath = innerModuleInfo->hapPath;
    }
    appDetailAbility.visible = true;
    std::string keyName;
    keyName.append(appDetailAbility.bundleName).append(".")
        .append(appDetailAbility.package).append(".").append(appDetailAbility.name);
    info.InsertAbilitiesInfo(keyName, appDetailAbility);
}

void BundleDataMgr::GetAllLauncherAbility(const Want &want, std::vector<AbilityInfo> &abilityInfos,
    const int32_t userId, const int32_t requestUserId) const
{
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &info = item.second;
        if (info.IsDisabled()) {
            LOG_W(BMS_TAG_QUERY, "app %{public}s is disabled", info.GetBundleName().c_str());
            continue;
        }
        if (info.GetBaseApplicationInfo().hideDesktopIcon) {
            LOG_D(BMS_TAG_QUERY, "Bundle(%{public}s) hide desktop icon", info.GetBundleName().c_str());
            continue;
        }
        if (info.GetBaseBundleInfo().entryInstallationFree) {
            LOG_D(BMS_TAG_QUERY, "Bundle(%{public}s) is atomic service, hide desktop icon",
                info.GetBundleName().c_str());
            continue;
        }

        // get installTime from innerBundleUserInfo
        int64_t installTime = 0;
        std::string userIdKey = info.GetBundleName() + "_" + std::to_string(userId);
        std::string userZeroKey = info.GetBundleName() + "_" + std::to_string(0);
        std::string userOneKey = info.GetBundleName() + "_" + std::to_string(1);
        auto iter = std::find_if(info.GetInnerBundleUserInfos().begin(), info.GetInnerBundleUserInfos().end(),
            [&userIdKey, &userZeroKey, &userOneKey](const std::pair<std::string, InnerBundleUserInfo> &infoMap) {
            return (infoMap.first == userIdKey || infoMap.first == userZeroKey || infoMap.first == userOneKey);
        });
        if (iter != info.GetInnerBundleUserInfos().end()) {
            installTime = iter->second.installTime;
        }
        GetMatchLauncherAbilityInfos(want, info, abilityInfos, installTime, userId);
    }
}

ErrCode BundleDataMgr::GetLauncherAbilityByBundleName(const Want &want, std::vector<AbilityInfo> &abilityInfos,
    const int32_t userId, const int32_t requestUserId) const
{
    ElementName element = want.GetElement();
    std::string bundleName = element.GetBundleName();
    const auto &item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        LOG_W(BMS_TAG_QUERY, "no bundleName %{public}s found", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    const InnerBundleInfo &info = item->second;
    if (info.IsDisabled()) {
        LOG_W(BMS_TAG_QUERY, "app %{public}s is disabled", info.GetBundleName().c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_DISABLED;
    }
    if (info.GetBaseApplicationInfo().hideDesktopIcon) {
        LOG_D(BMS_TAG_QUERY, "Bundle(%{public}s) hide desktop icon", bundleName.c_str());
        return ERR_OK;
    }
    if (info.GetBaseBundleInfo().entryInstallationFree) {
        LOG_D(BMS_TAG_QUERY, "Bundle(%{public}s) is atomic service, hide desktop icon", bundleName.c_str());
        return ERR_OK;
    }
    // get installTime from innerBundleUserInfo
    int64_t installTime = 0;
    std::string userIdKey = info.GetBundleName() + "_" + std::to_string(userId);
    std::string userZeroKey = info.GetBundleName() + "_" + std::to_string(0);
    std::string userOneKey = info.GetBundleName() + "_" + std::to_string(1);
    auto iter = std::find_if(info.GetInnerBundleUserInfos().begin(), info.GetInnerBundleUserInfos().end(),
        [&userIdKey, &userZeroKey, &userOneKey](const std::pair<std::string, InnerBundleUserInfo> &infoMap) {
        return (infoMap.first == userIdKey || infoMap.first == userZeroKey || infoMap.first == userOneKey);
    });
    if (iter != info.GetInnerBundleUserInfos().end()) {
        installTime = iter->second.installTime;
    }
    GetMatchLauncherAbilityInfos(want, item->second, abilityInfos, installTime, userId);
    FilterAbilityInfosByModuleName(element.GetModuleName(), abilityInfos);
    return ERR_OK;
}

ErrCode BundleDataMgr::QueryLauncherAbilityInfos(
    const Want &want, int32_t userId, std::vector<AbilityInfo> &abilityInfos) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        LOG_E(BMS_TAG_QUERY, "request user id is invalid");
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        LOG_W(BMS_TAG_QUERY, "bundleInfos_ is empty");
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }

    ElementName element = want.GetElement();
    std::string bundleName = element.GetBundleName();
    if (bundleName.empty()) {
        // query all launcher ability
        GetAllLauncherAbility(want, abilityInfos, userId, requestUserId);
        return ERR_OK;
    }
    // query definite abilities by bundle name
    ErrCode ret = GetLauncherAbilityByBundleName(want, abilityInfos, userId, requestUserId);
    if (ret == ERR_OK) {
        LOG_D(BMS_TAG_QUERY, "ability infos have been found");
    }
    return ret;
}

ErrCode BundleDataMgr::GetLauncherAbilityInfoSync(const Want &want, const int32_t userId,
    std::vector<AbilityInfo> &abilityInfos) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        LOG_E(BMS_TAG_QUERY, "request user id is invalid");
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    ElementName element = want.GetElement();
    std::string bundleName = element.GetBundleName();
    const auto &item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        LOG_W(BMS_TAG_QUERY, "no bundleName %{public}s found", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    const InnerBundleInfo &info = item->second;
    if(!BundlePermissionMgr::IsSystemApp()){
        int32_t responseUserId = info.GetResponseUserId(userId);
        if (responseUserId == Constants::INVALID_USERID) {
            return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
        }
    }
    if (info.GetBaseApplicationInfo().hideDesktopIcon) {
        LOG_D(BMS_TAG_QUERY, "Bundle(%{public}s) hide desktop icon", bundleName.c_str());
        return ERR_OK;
    }
    if (info.GetBaseBundleInfo().entryInstallationFree) {
        LOG_D(BMS_TAG_QUERY, "Bundle(%{public}s) is atomic service, hide desktop icon", bundleName.c_str());
        return ERR_OK;
    }
    // get installTime from innerBundleUserInfo
    int64_t installTime = 0;
    std::string userIdKey = info.GetBundleName() + "_" + std::to_string(userId);
    std::string userZeroKey = info.GetBundleName() + "_" + std::to_string(0);
    std::string userOneKey = info.GetBundleName() + "_" + std::to_string(1);
    auto iter = std::find_if(info.GetInnerBundleUserInfos().begin(), info.GetInnerBundleUserInfos().end(),
        [&userIdKey, &userZeroKey, &userOneKey](const std::pair<std::string, InnerBundleUserInfo> &infoMap) {
        return (infoMap.first == userIdKey || infoMap.first == userZeroKey || infoMap.first == userOneKey);
    });
    if (iter != info.GetInnerBundleUserInfos().end()) {
        installTime = iter->second.installTime;
    }
    GetMatchLauncherAbilityInfos(want, item->second, abilityInfos, installTime, userId);
    FilterAbilityInfosByModuleName(element.GetModuleName(), abilityInfos);
    return ERR_OK;
}

bool BundleDataMgr::QueryAbilityInfoByUri(
    const std::string &abilityUri, int32_t userId, AbilityInfo &abilityInfo) const
{
    LOG_D(BMS_TAG_QUERY, "abilityUri is %{private}s", abilityUri.c_str());
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return false;
    }

    if (abilityUri.empty()) {
        return false;
    }
    if (abilityUri.find(ServiceConstants::DATA_ABILITY_URI_PREFIX) == std::string::npos) {
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        LOG_W(BMS_TAG_QUERY, "bundleInfos_ data is empty");
        return false;
    }
    std::string noPpefixUri = abilityUri.substr(strlen(ServiceConstants::DATA_ABILITY_URI_PREFIX));
    auto posFirstSeparator = noPpefixUri.find(ServiceConstants::FILE_SEPARATOR_CHAR);
    if (posFirstSeparator == std::string::npos) {
        return false;
    }
    auto posSecondSeparator = noPpefixUri.find(ServiceConstants::FILE_SEPARATOR_CHAR, posFirstSeparator + 1);
    std::string uri;
    if (posSecondSeparator == std::string::npos) {
        uri = noPpefixUri.substr(posFirstSeparator + 1, noPpefixUri.size() - posFirstSeparator - 1);
    } else {
        uri = noPpefixUri.substr(posFirstSeparator + 1, posSecondSeparator - posFirstSeparator - 1);
    }
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &info = item.second;
        if (info.IsDisabled()) {
            LOG_D(BMS_TAG_QUERY, "app %{public}s is disabled", info.GetBundleName().c_str());
            continue;
        }

        int32_t responseUserId = info.GetResponseUserId(requestUserId);
        if (!info.GetApplicationEnabled(responseUserId)) {
            continue;
        }

        auto ability = info.FindAbilityInfoByUri(uri);
        if (!ability) {
            continue;
        }

        abilityInfo = (*ability);
        info.GetApplicationInfo(
            ApplicationFlag::GET_APPLICATION_INFO_WITH_CERTIFICATE_FINGERPRINT, responseUserId,
            abilityInfo.applicationInfo);
        return true;
    }

    LOG_W(BMS_TAG_QUERY, "query abilityUri(%{private}s) failed", abilityUri.c_str());
    return false;
}

bool BundleDataMgr::QueryAbilityInfosByUri(const std::string &abilityUri, std::vector<AbilityInfo> &abilityInfos)
{
    LOG_D(BMS_TAG_QUERY, "abilityUri is %{private}s", abilityUri.c_str());
    if (abilityUri.empty()) {
        return false;
    }
    if (abilityUri.find(ServiceConstants::DATA_ABILITY_URI_PREFIX) == std::string::npos) {
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        LOG_W(BMS_TAG_QUERY, "bundleInfos_ data is empty");
        return false;
    }
    std::string noPpefixUri = abilityUri.substr(strlen(ServiceConstants::DATA_ABILITY_URI_PREFIX));
    auto posFirstSeparator = noPpefixUri.find(ServiceConstants::FILE_SEPARATOR_CHAR);
    if (posFirstSeparator == std::string::npos) {
        return false;
    }
    auto posSecondSeparator = noPpefixUri.find(ServiceConstants::FILE_SEPARATOR_CHAR, posFirstSeparator + 1);
    std::string uri;
    if (posSecondSeparator == std::string::npos) {
        uri = noPpefixUri.substr(posFirstSeparator + 1, noPpefixUri.size() - posFirstSeparator - 1);
    } else {
        uri = noPpefixUri.substr(posFirstSeparator + 1, posSecondSeparator - posFirstSeparator - 1);
    }

    for (auto &item : bundleInfos_) {
        InnerBundleInfo &info = item.second;
        if (info.IsDisabled()) {
            LOG_D(BMS_TAG_QUERY, "app %{public}s is disabled", info.GetBundleName().c_str());
            continue;
        }
        info.FindAbilityInfosByUri(uri, abilityInfos, GetUserId());
    }
    if (abilityInfos.size() == 0) {
        return false;
    }

    return true;
}

bool BundleDataMgr::GetApplicationInfo(
    const std::string &appName, int32_t flags, const int userId, ApplicationInfo &appInfo) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return false;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;
    if (!GetInnerBundleInfoWithFlags(appName, flags, innerBundleInfo, requestUserId)) {
        LOG_D(BMS_TAG_QUERY, "GetApplicationInfo failed, bundleName:%{public}s", appName.c_str());
        return false;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetApplicationInfo is null.");
        return false;
    }

    int32_t responseUserId = innerBundleInfo->GetResponseUserId(requestUserId);
    innerBundleInfo->GetApplicationInfo(flags, responseUserId, appInfo);
    return true;
}

ErrCode BundleDataMgr::GetApplicationInfoV9(
    const std::string &appName, int32_t flags, int32_t userId, ApplicationInfo &appInfo, const int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo *innerBundleInfo = nullptr;
    int32_t flag = 0;
    if ((static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetApplicationFlag::GET_APPLICATION_INFO_WITH_DISABLE))
        == static_cast<uint32_t>(GetApplicationFlag::GET_APPLICATION_INFO_WITH_DISABLE)) {
        flag = static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_DISABLE);
    }
    auto ret = GetInnerBundleInfoWithBundleFlagsV9(appName, flag, innerBundleInfo, requestUserId, appIndex);
    if (ret != ERR_OK) {
        LOG_NOFUNC_E(BMS_TAG_QUERY, "GetApplicationInfoV9 failed -n:%{public}s -u:%{public}d -i:%{public}d",
            appName.c_str(), requestUserId, appIndex);
        return ret;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetApplicationInfoV9 is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    int32_t responseUserId = innerBundleInfo->GetResponseUserId(requestUserId);
    ret = innerBundleInfo->GetApplicationInfoV9(flags, responseUserId, appInfo, appIndex);
    if (ret != ERR_OK) {
        LOG_NOFUNC_E(BMS_TAG_QUERY, "GetApplicationInfoV9 failed -n:%{public}s -u:%{public}d -i:%{public}d",
            appName.c_str(), responseUserId, appIndex);
        return ret;
    }
    return ret;
}

ErrCode BundleDataMgr::GetApplicationInfoWithResponseId(
    const std::string &appName, int32_t flags, int32_t &userId, ApplicationInfo &appInfo) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;
    int32_t flag = 0;
    if ((static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetApplicationFlag::GET_APPLICATION_INFO_WITH_DISABLE))
        == static_cast<uint32_t>(GetApplicationFlag::GET_APPLICATION_INFO_WITH_DISABLE)) {
        flag = static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_DISABLE);
    }
    auto ret = GetInnerBundleInfoWithBundleFlagsV9(appName, flag, innerBundleInfo, requestUserId);
    if (ret != ERR_OK) {
        LOG_D(BMS_TAG_QUERY,
            "GetApplicationInfoV9 failed, bundleName:%{public}s, requestUserId:%{public}d",
            appName.c_str(), requestUserId);
        return ret;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetApplicationInfoWithResponseId is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    int32_t responseUserId = innerBundleInfo->GetResponseUserId(requestUserId);
    ret = innerBundleInfo->GetApplicationInfoV9(flags, responseUserId, appInfo);
    if (ret != ERR_OK) {
        LOG_D(BMS_TAG_QUERY,
            "GetApplicationInfoV9 failed, bundleName:%{public}s, responseUserId:%{public}d",
            appName.c_str(), responseUserId);
        return ret;
    }
    userId = responseUserId;
    return ret;
}

void BundleDataMgr::GetCloneAppInfo(const InnerBundleInfo &info, int32_t userId, int32_t flags,
    std::vector<ApplicationInfo> &appInfos) const
{
    std::vector<int32_t> appIndexVec = GetCloneAppIndexesNoLock(info.GetBundleName(), userId);
    for (int32_t appIndex : appIndexVec) {
        bool isEnabled = false;
        ErrCode ret = info.GetApplicationEnabledV9(userId, isEnabled, appIndex);
        if (ret != ERR_OK) {
            continue;
        }
        if (isEnabled || (static_cast<uint32_t>(flags) & GET_APPLICATION_INFO_WITH_DISABLE)) {
            ApplicationInfo cloneAppInfo;
            info.GetApplicationInfo(flags, userId, cloneAppInfo, appIndex);
            if (cloneAppInfo.appIndex == appIndex) {
                appInfos.emplace_back(std::move(cloneAppInfo));
            }
        }
    }
}

bool BundleDataMgr::GetApplicationInfos(
    int32_t flags, const int userId, std::vector<ApplicationInfo> &appInfos) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return false;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        LOG_W(BMS_TAG_QUERY, "bundleInfos_ data is empty");
        return false;
    }

    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &info = item.second;
        if (info.IsDisabled()) {
            LOG_D(BMS_TAG_QUERY, "app %{public}s is disabled", info.GetBundleName().c_str());
            continue;
        }
        int32_t responseUserId = info.GetResponseUserId(requestUserId);
        if (info.GetApplicationEnabled(responseUserId) ||
            (static_cast<uint32_t>(flags) & GET_APPLICATION_INFO_WITH_DISABLE)) {
            ApplicationInfo appInfo;
            info.GetApplicationInfo(flags, responseUserId, appInfo);
            appInfos.emplace_back(std::move(appInfo));
        }
        GetCloneAppInfo(info, responseUserId, flags, appInfos);
    }
    LOG_D(BMS_TAG_QUERY, "get installed bundles success");
    return !appInfos.empty();
}

bool BundleDataMgr::UpdateExtResources(const std::string &bundleName,
    const std::vector<ExtendResourceInfo> &extendResourceInfos)
{
    if (bundleName.empty()) {
        APP_LOGW("bundleName is empty");
        return false;
    }

    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return false;
    }

    auto info = infoItem->second;
    info.AddExtendResourceInfos(extendResourceInfos);
    if (!dataStorage_->SaveStorageBundleInfo(info)) {
        APP_LOGW("SaveStorageBundleInfo failed %{public}s", bundleName.c_str());
        return false;
    }

    bundleInfos_.at(bundleName) = info;
    return true;
}

bool BundleDataMgr::RemoveExtResources(const std::string &bundleName,
    const std::vector<std::string> &moduleNames)
{
    if (bundleName.empty()) {
        APP_LOGW("bundleName is empty");
        return false;
    }

    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return false;
    }

    auto info = infoItem->second;
    info.RemoveExtendResourceInfos(moduleNames);
    if (!dataStorage_->SaveStorageBundleInfo(info)) {
        APP_LOGW("SaveStorageBundleInfo failed %{public}s", bundleName.c_str());
        return false;
    }

    bundleInfos_.at(bundleName) = info;
    return true;
}

ErrCode BundleDataMgr::GetExtendResourceInfo(
    const std::string &bundleName, const std::string &moduleName,
    ExtendResourceInfo &extendResourceInfo)
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGE("can not find bundle %{public}s", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    auto extendResourceInfos = infoItem->second.GetExtendResourceInfos();
    auto iter = extendResourceInfos.find(moduleName);
    if (iter == extendResourceInfos.end()) {
        APP_LOGE("can not find module %{public}s", moduleName.c_str());
        return ERR_BUNDLE_MANAGER_MODULE_NOT_EXIST;
    }
    extendResourceInfo = iter->second;
    return ERR_OK;
}

bool BundleDataMgr::UpateCurDynamicIconModule(
    const std::string &bundleName, const std::string &moduleName, const int32_t userId, const int32_t appIndex)
{
    if (bundleName.empty()) {
        APP_LOGW("bundleName is empty");
        return false;
    }

    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return false;
    }

    auto info = infoItem->second;
    info.SetCurDynamicIconModule(moduleName, userId, appIndex);
    if (!dataStorage_->SaveStorageBundleInfo(info)) {
        APP_LOGW("SaveStorageBundleInfo failed %{public}s", bundleName.c_str());
        return false;
    }

    bundleInfos_.at(bundleName) = info;
    return true;
}

void BundleDataMgr::GetCloneAppInfoV9(const InnerBundleInfo &info, int32_t userId, int32_t flags,
    std::vector<ApplicationInfo> &appInfos) const
{
    std::vector<int32_t> appIndexVec = GetCloneAppIndexesNoLock(info.GetBundleName(), userId);
    for (int32_t appIndex : appIndexVec) {
        bool isEnabled = false;
        ErrCode ret = info.GetApplicationEnabledV9(userId, isEnabled, appIndex);
        if (ret != ERR_OK) {
            continue;
        }
        if (isEnabled || (static_cast<uint32_t>(flags) &
            static_cast<uint32_t>(GetApplicationFlag::GET_APPLICATION_INFO_WITH_DISABLE))) {
            ApplicationInfo cloneAppInfo;
            ret = info.GetApplicationInfoV9(flags, userId, cloneAppInfo, appIndex);
            if (ret == ERR_OK) {
                appInfos.emplace_back(std::move(cloneAppInfo));
            }
        }
    }
}

ErrCode BundleDataMgr::GetApplicationInfosV9(
    int32_t flags, int32_t userId, std::vector<ApplicationInfo> &appInfos) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &info = item.second;
        if (info.IsDisabled()) {
            APP_LOGD("app %{public}s is disabled", info.GetBundleName().c_str());
            continue;
        }
        int32_t responseUserId = info.GetResponseUserId(requestUserId);
        if (info.GetApplicationEnabled(responseUserId) ||
            (static_cast<uint32_t>(flags) &
            static_cast<uint32_t>(GetApplicationFlag::GET_APPLICATION_INFO_WITH_DISABLE))) {
            ApplicationInfo appInfo;
            if (info.GetApplicationInfoV9(flags, responseUserId, appInfo) == ERR_OK) {
                appInfos.emplace_back(std::move(appInfo));
            }
        }
        GetCloneAppInfoV9(info, responseUserId, flags, appInfos);
    }
    APP_LOGD("get installed bundles success");
    return ERR_OK;
}

bool BundleDataMgr::GetBundleInfo(
    const std::string &bundleName, int32_t flags, BundleInfo &bundleInfo, int32_t userId) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    std::vector<InnerBundleUserInfo> innerBundleUserInfos;
    if (userId == Constants::ANY_USERID) {
        if (!GetInnerBundleUserInfos(bundleName, innerBundleUserInfos)) {
            LOG_W(BMS_TAG_QUERY, "no userInfos for this bundle(%{public}s)", bundleName.c_str());
            return false;
        }
        userId = innerBundleUserInfos.begin()->bundleUserInfo.userId;
    }

    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo *innerBundleInfo = nullptr;
    if (!GetInnerBundleInfoWithFlags(bundleName, flags, innerBundleInfo, requestUserId)) {
        LOG_NOFUNC_W(BMS_TAG_QUERY, "GetBundleInfo failed -n %{public}s -u %{public}d",
            bundleName.c_str(), requestUserId);
        return false;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetBundleInfo is null.");
        return false;
    }
    // for only one user, bundle info can not be obtained during installation
    if ((innerBundleInfo->GetInnerBundleUserInfos().size() <= ONLY_ONE_USER) &&
        (innerBundleInfo->GetInstallMark().status == InstallExceptionStatus::INSTALL_START)) {
        LOG_NOFUNC_W(BMS_TAG_QUERY, "GetBundleInfo failed -n %{public}s -u %{public}d, not ready",
            bundleName.c_str(), requestUserId);
        return false;
    }

    int32_t responseUserId = innerBundleInfo->GetResponseUserId(requestUserId);
    innerBundleInfo->GetBundleInfo(flags, bundleInfo, responseUserId);

    if ((static_cast<uint32_t>(flags) & BundleFlag::GET_BUNDLE_WITH_MENU) == BundleFlag::GET_BUNDLE_WITH_MENU) {
        ProcessBundleMenu(bundleInfo, flags, false);
    }
    if ((static_cast<uint32_t>(flags) & BundleFlag::GET_BUNDLE_WITH_ROUTER_MAP) ==
        BundleFlag::GET_BUNDLE_WITH_ROUTER_MAP) {
        ProcessBundleRouterMap(bundleInfo, static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_HAP_MODULE) |
            static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_ROUTER_MAP), userId);
    }
    LOG_D(BMS_TAG_QUERY, "get bundleInfo(%{public}s) successfully in user(%{public}d)",
        bundleName.c_str(), userId);
    return true;
}

ErrCode BundleDataMgr::GetBundleInfoV9(
    const std::string &bundleName, int32_t flags, BundleInfo &bundleInfo, int32_t userId, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);

    if (userId == Constants::ANY_USERID) {
        std::vector<InnerBundleUserInfo> innerBundleUserInfos;
        if (!GetInnerBundleUserInfos(bundleName, innerBundleUserInfos)) {
            LOG_W(BMS_TAG_QUERY, "no userInfos for this bundle(%{public}s)", bundleName.c_str());
            return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
        }
        userId = innerBundleUserInfos.begin()->bundleUserInfo.userId;
    }

    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    int32_t originalUserId = requestUserId;
    PreProcessAnyUserFlag(bundleName, flags, requestUserId);
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;

    auto ret = GetInnerBundleInfoWithBundleFlagsV9(bundleName, flags, innerBundleInfo, requestUserId);
    if (ret != ERR_OK) {
        LOG_D(BMS_TAG_QUERY, "GetBundleInfoV9 failed, error code: %{public}d, bundleName:%{public}s",
            ret, bundleName.c_str());
        return ret;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetBundleInfoV9 is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    // for only one user, bundle info can not be obtained during installation
    if ((innerBundleInfo->GetInnerBundleUserInfos().size() <= ONLY_ONE_USER) &&
        (innerBundleInfo->GetInstallMark().status == InstallExceptionStatus::INSTALL_START)) {
        LOG_NOFUNC_W(BMS_TAG_QUERY, "GetBundleInfo failed -n %{public}s -u %{public}d, not ready",
            bundleName.c_str(), requestUserId);
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }

    int32_t responseUserId = innerBundleInfo->GetResponseUserId(requestUserId);
    innerBundleInfo->GetBundleInfoV9(flags, bundleInfo, responseUserId, appIndex);
    PostProcessAnyUserFlags(flags, responseUserId, originalUserId, bundleInfo, *innerBundleInfo);

    ProcessCertificate(bundleInfo, bundleName, flags);
    ProcessBundleMenu(bundleInfo, flags, true);
    ProcessBundleRouterMap(bundleInfo, flags, userId);
    LOG_D(BMS_TAG_QUERY, "get bundleInfo(%{public}s) successfully in user(%{public}d)",
        bundleName.c_str(), userId);
    return ERR_OK;
}

ErrCode BundleDataMgr::GetAssetGroupsInfo(const int32_t uid, AssetGroupInfo &assetGroupInfo) const
{
    InnerBundleInfo innerBundleInfo;
    if (GetInnerBundleInfoAndIndexByUid(uid, innerBundleInfo, assetGroupInfo.appIndex) != ERR_OK) {
        if (sandboxAppHelper_ == nullptr) {
            return ERR_BUNDLE_MANAGER_INVALID_UID;
        }
        if (sandboxAppHelper_->GetInnerBundleInfoByUid(uid, innerBundleInfo) != ERR_OK) {
            return ERR_BUNDLE_MANAGER_INVALID_UID;
        }
    }

    assetGroupInfo.bundleName = innerBundleInfo.GetBundleName();
    assetGroupInfo.appId = innerBundleInfo.GetAppId();
    assetGroupInfo.appIdentifier = innerBundleInfo.GetAppIdentifier();
    assetGroupInfo.developerId = innerBundleInfo.GetDeveloperId();
    assetGroupInfo.assetAccessGroups = innerBundleInfo.GetAssetAccessGroups();

    APP_LOGD("-u:%{public}d, -n:%{public}s, -i:%{public}d, ai:%{public}s, air:%{public}s, d:%{public}s",
        uid, assetGroupInfo.bundleName.c_str(), assetGroupInfo.appIndex, assetGroupInfo.appId.c_str(),
        assetGroupInfo.appIdentifier.c_str(), assetGroupInfo.developerId.c_str());
    return ERR_OK;
}

void BundleDataMgr::BatchGetBundleInfo(const std::vector<std::string> &bundleNames, int32_t flags,
    std::vector<BundleInfo> &bundleInfos, int32_t userId) const
{
    for (const auto &bundleName : bundleNames) {
        BundleInfo bundleInfo;
        ErrCode ret = GetBundleInfoV9(bundleName, flags, bundleInfo, userId);
        if (ret != ERR_OK) {
            continue;
        }
        bundleInfos.push_back(bundleInfo);
    }
}

ErrCode BundleDataMgr::GetBundleInfoForSelf(int32_t flags, BundleInfo &bundleInfo)
{
    int32_t uid = IPCSkeleton::GetCallingUid();
    int32_t appIndex = 0;
    std::string bundleName;
    ErrCode ret = GetBundleNameAndIndex(uid, bundleName, appIndex);
    if (ret != ERR_OK) {
        APP_LOGD("uid: %{public}d invalid!", uid);
        return ret;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;
    InnerBundleInfo sandboxInfo;
    if (GetInnerBundleInfoNoLock(bundleName, uid, appIndex, innerBundleInfo) != ERR_OK) {
        if (sandboxAppHelper_ == nullptr) {
            LOG_E(BMS_TAG_QUERY, "GetBundleInfoForSelf failed uid:%{public}d", uid);
            return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
        }
        if (sandboxAppHelper_->GetInnerBundleInfoByUid(uid, sandboxInfo) != ERR_OK) {
            LOG_D(BMS_TAG_QUERY, "sandbox GetBundleInfoForSelf failed uid:%{public}d", uid);
            return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
        }
        innerBundleInfo = &sandboxInfo;
    }
    
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetBundleInfoForSelf is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    int32_t userId = uid / Constants::BASE_USER_RANGE;
    innerBundleInfo->GetBundleInfoV9(flags, bundleInfo, userId, appIndex);
    ProcessCertificate(bundleInfo, innerBundleInfo->GetBundleName(), flags);
    ProcessBundleMenu(bundleInfo, flags, true);
    ProcessBundleRouterMap(bundleInfo, flags, userId);
    LOG_D(BMS_TAG_QUERY, "get bundleInfoForSelf %{public}s successfully in user %{public}d",
        innerBundleInfo->GetBundleName().c_str(), userId);
    return ERR_OK;
}

ErrCode BundleDataMgr::ProcessBundleMenu(BundleInfo &bundleInfo, int32_t flags, bool clearData) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    if (clearData) {
        if ((static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_HAP_MODULE))
            != static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_HAP_MODULE)) {
            return ERR_OK;
        }
        if ((static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_MENU))
            != static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_MENU)) {
            APP_LOGD("no GET_BUNDLE_INFO_WITH_MENU flag, remove menu content");
            std::for_each(bundleInfo.hapModuleInfos.begin(), bundleInfo.hapModuleInfos.end(), [](auto &hapModuleInfo) {
                hapModuleInfo.fileContextMenu = Constants::EMPTY_STRING;
            });
            return ERR_OK;
        }
    }
    for (auto &hapModuleInfo : bundleInfo.hapModuleInfos) {
        std::string menuProfile = hapModuleInfo.fileContextMenu;
        auto pos = menuProfile.find(PROFILE_PREFIX);
        if (pos == std::string::npos) {
            APP_LOGD("invalid menu profile");
            continue;
        }
        std::string menuFileName = menuProfile.substr(pos + PROFILE_PREFIX_LENGTH);
        std::string menuFilePath = PROFILE_PATH + menuFileName + JSON_SUFFIX;

        std::string menuProfileContent;
        GetJsonProfileByExtractor(hapModuleInfo.hapPath, menuFilePath, menuProfileContent);
        hapModuleInfo.fileContextMenu = menuProfileContent;
    }
    return ERR_OK;
}

void BundleDataMgr::ProcessBundleRouterMap(BundleInfo& bundleInfo, int32_t flag, int32_t userId) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    if (routerStorage_ == nullptr) {
        APP_LOGE("routerStorage_ is null");
        return;
    }
    APP_LOGD("ProcessBundleRouterMap with flags: %{public}d", flag);
    if ((static_cast<uint32_t>(flag) & static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_HAP_MODULE))
        != static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_HAP_MODULE)) {
        return;
    }
    if ((static_cast<uint32_t>(flag) & static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_ROUTER_MAP))
        != static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_ROUTER_MAP)) {
        return;
    }
    for (auto &hapModuleInfo : bundleInfo.hapModuleInfos) {
        std::string routerPath = hapModuleInfo.routerMap;
        auto pos = routerPath.find(PROFILE_PREFIX);
        if (pos == std::string::npos) {
            APP_LOGD("invalid router map profile");
            continue;
        }
        if (!routerStorage_->GetRouterInfo(bundleInfo.name, hapModuleInfo.moduleName,
            bundleInfo.versionCode, hapModuleInfo.routerArray)) {
            APP_LOGE("get failed for %{public}s", hapModuleInfo.moduleName.c_str());
            continue;
        }
    }

    // get plugin router info
    std::vector<RouterItem> pluginRouterInfos;
    GetRouterInfoForPlugin(bundleInfo.name, userId, pluginRouterInfos);
    // get hsp router info
    std::vector<RouterItem> sharedBundleRouterInfos;
    GetRouterInfoForSharedBundle(bundleInfo.name, sharedBundleRouterInfos);
    MergeRouterItems(sharedBundleRouterInfos, pluginRouterInfos);
    RouterMapHelper::MergeRouter(bundleInfo, pluginRouterInfos);
}

void BundleDataMgr::GetRouterInfoForPlugin(const std::string &hostBundleName,
    int32_t userId, std::vector<RouterItem> &routerInfos) const
{
    if (routerStorage_ == nullptr) {
        APP_LOGE("routerStorage_ is null");
        return;
    }
    std::vector<PluginBundleInfo> pluginBundleInfos;
    std::vector<RouterItem> tempInfos;
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("invalid userid :%{public}d", userId);
        return;
    }
    InnerGetAllPluginInfo(hostBundleName, requestUserId, pluginBundleInfos);
    for (const auto &pluginInfo : pluginBundleInfos) {
        for (const auto &module : pluginInfo.pluginModuleInfos) {
            if (!routerStorage_->GetRouterInfo(pluginInfo.pluginBundleName, module.moduleName,
                pluginInfo.versionCode, tempInfos) || tempInfos.empty()) {
                continue;
            }
            routerInfos.insert(routerInfos.end(), tempInfos.begin(), tempInfos.end());
            tempInfos.clear();
        }
    }
}

void BundleDataMgr::GetRouterInfoForSharedBundle(const std::string &bundleName,
    std::vector<RouterItem> &routerInfos) const
{
    std::vector<BaseSharedBundleInfo> baseSharedBundleInfos;
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("get bundle info failed, bundleName:%{public}s", bundleName.c_str());
        return;
    }
    const InnerBundleInfo &innerBundleInfo = infoItem->second;
    std::vector<Dependency> dependencies = innerBundleInfo.GetDependencies();
    for (const auto &item : dependencies) {
        BaseSharedBundleInfo baseSharedBundleInfo;
        if (GetBaseSharedBundleInfo(item, baseSharedBundleInfo)) {
            baseSharedBundleInfos.emplace_back(baseSharedBundleInfo);
        }
    }

    std::vector<RouterItem> tempInfos;
    for (auto &info : baseSharedBundleInfos) {
        if (!routerStorage_->GetRouterInfo(info.bundleName, info.moduleName,
            info.versionCode, tempInfos) || tempInfos.empty()) {
            continue;
        }
        routerInfos.insert(routerInfos.end(), tempInfos.begin(), tempInfos.end());
        tempInfos.clear();
    }
}

void BundleDataMgr::MergeRouterItems(
    const std::vector<RouterItem>& sharedBundleRouterInfos,
    std::vector<RouterItem>& pluginRouterInfos) const
{
    std::unordered_map<std::string, RouterItem> routerMap;
    
    for (const auto& item : pluginRouterInfos) {
        routerMap[item.name] = item;
    }
    
    for (const auto& item : sharedBundleRouterInfos) {
        routerMap[item.name] = item;
    }
    
    pluginRouterInfos.clear();
    pluginRouterInfos.reserve(routerMap.size());
    for (const auto& pair : routerMap) {
        pluginRouterInfos.push_back(pair.second);
    }
}

bool BundleDataMgr::UpdateRouterDB()
{
    if (routerStorage_ == nullptr) {
        APP_LOGE("routerStorage_ is null");
        return false;
    }
    return routerStorage_->UpdateDB();
}

void BundleDataMgr::InsertRouterInfo(const InnerBundleInfo &innerBundleInfo)
{
    std::map<std::string, std::pair<std::string, std::string>> hapPathMap;
    FindRouterHapPath(innerBundleInfo, hapPathMap);
    std::map<std::string, std::string> routerInfoMap;
    for (auto hapIter = hapPathMap.begin(); hapIter != hapPathMap.end(); hapIter++) {
        std::string routerMapString;
        if (GetJsonProfileByExtractor(hapIter->second.first, hapIter->second.second, routerMapString) != ERR_OK) {
            APP_LOGW("get json string from %{public}s failed", hapIter->second.second.c_str());
            continue;
        }
        routerInfoMap[hapIter->first] = routerMapString;
    }
    std::string bundleName = innerBundleInfo.GetBundleName();
    if (!routerStorage_->InsertRouterInfo(bundleName, routerInfoMap, innerBundleInfo.GetVersionCode())) {
        APP_LOGW("-n %{public}s insert router failed", bundleName.c_str());
    }
}

bool BundleDataMgr::HasPluginInstalledByOtherBundle(const std::string &hostBundleName,
    const std::string &pluginBundleName, const uint32_t versionCode)
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto &infoItem : bundleInfos_) {
        for (auto& innerBundleUserInfoItem : infoItem.second.GetInnerBundleUserInfos()) {
            if ((infoItem.first != hostBundleName) &&
                innerBundleUserInfoItem.second.IsPluginInstalled(pluginBundleName)) {
                auto& bundleUserInfo = innerBundleUserInfoItem.second.bundleUserInfo;
                PluginBundleInfo pluginInfo;
                if (!InnerGetPluginBundleInfo(infoItem.first, pluginBundleName, bundleUserInfo.userId, pluginInfo)) {
                    continue;
                }
                if (pluginInfo.versionCode == versionCode) {
                    // other app has installed the same plugin
                    return true;
                }
            }
        }
    }
    return false;
}


void BundleDataMgr::ProcessCertificate(BundleInfo& bundleInfo, const std::string &bundleName, int32_t flags) const
{
    if ((static_cast<uint32_t>(flags) &
        static_cast<int32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_SIGNATURE_INFO))
        == static_cast<int32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_SIGNATURE_INFO)) {
        AppProvisionInfo appProvisionInfo;
        if (!DelayedSingleton<AppProvisionInfoManager>::GetInstance()->
            GetAppProvisionInfo(bundleName, appProvisionInfo)) {
            APP_LOGW("bundleName:%{public}s GetAppProvisionInfo failed", bundleName.c_str());
            return;
        }
        bundleInfo.signatureInfo.certificate = appProvisionInfo.certificate;
    }
}

bool BundleDataMgr::DeleteRouterInfo(const std::string &bundleName, const std::string &moduleName)
{
    if (routerStorage_ == nullptr) {
        APP_LOGE("routerStorage_ is null");
        return false;
    }
    return routerStorage_->DeleteRouterInfo(bundleName, moduleName);
}

bool BundleDataMgr::DeleteRouterInfo(const std::string &bundleName)
{
    if (routerStorage_ == nullptr) {
        APP_LOGE("routerStorage_ is null");
        return false;
    }
    return routerStorage_->DeleteRouterInfo(bundleName);
}

void BundleDataMgr::DeleteRouterInfoForPlugin(const std::string &hostBundleName,
    const PluginBundleInfo &pluginInfo)
{
    if (HasPluginInstalledByOtherBundle(hostBundleName, pluginInfo.pluginBundleName, pluginInfo.versionCode)) {
        return;
    }
    if (routerStorage_ == nullptr) {
        APP_LOGE("routerStorage_ is null");
        return;
    }
    for (const auto &module : pluginInfo.pluginModuleInfos) {
        routerStorage_->DeleteRouterInfo(pluginInfo.pluginBundleName, module.moduleName, pluginInfo.versionCode);
    }
}

void BundleDataMgr::UpdateRouterInfo(const std::string &bundleName)
{
    if (routerStorage_ == nullptr) {
        APP_LOGE("routerStorage_ is null");
        return;
    }
    std::map<std::string, std::pair<std::string, std::string>> hapPathMap;
    uint32_t versionCode = 0;
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        const auto infoItem = bundleInfos_.find(bundleName);
        if (infoItem == bundleInfos_.end()) {
            APP_LOGW("bundleName: %{public}s bundle info not exist", bundleName.c_str());
            return;
        }
        FindRouterHapPath(infoItem->second, hapPathMap);
        versionCode = infoItem->second.GetVersionCode();
    }
    UpdateRouterInfo(bundleName, hapPathMap, versionCode);
}

void BundleDataMgr::FindRouterHapPath(const InnerBundleInfo &innerBundleInfo,
    std::map<std::string, std::pair<std::string, std::string>> &hapPathMap)
{
    auto moduleMap = innerBundleInfo.GetInnerModuleInfos();
    for (auto it = moduleMap.begin(); it != moduleMap.end(); it++) {
        std::string routerPath = it->second.routerMap;
        auto pos = routerPath.find(PROFILE_PREFIX);
        if (pos == std::string::npos) {
            continue;
        }
        std::string routerJsonName = routerPath.substr(pos + PROFILE_PREFIX_LENGTH);
        std::string routerJsonPath = PROFILE_PATH + routerJsonName + JSON_SUFFIX;
        hapPathMap[it->second.moduleName] = std::make_pair(it->second.hapPath, routerJsonPath);
    }
}

void BundleDataMgr::UpdateRouterInfo(InnerBundleInfo &innerBundleInfo)
{
    std::map<std::string, std::pair<std::string, std::string>> hapPathMap;
    FindRouterHapPath(innerBundleInfo, hapPathMap);
    UpdateRouterInfo(innerBundleInfo.GetBundleName(), hapPathMap, innerBundleInfo.GetVersionCode());
}

void BundleDataMgr::UpdateRouterInfo(const std::string &bundleName,
    std::map<std::string, std::pair<std::string, std::string>> &hapPathMap, const uint32_t versionCode)
{
    std::map<std::string, std::string> routerInfoMap;
    for (auto hapIter = hapPathMap.begin(); hapIter != hapPathMap.end(); hapIter++) {
        std::string routerMapString;
        if (GetJsonProfileByExtractor(hapIter->second.first, hapIter->second.second, routerMapString) != ERR_OK) {
            APP_LOGW("get json string from %{public}s failed", hapIter->second.second.c_str());
            continue;
        }
        routerInfoMap[hapIter->first] = routerMapString;
    }
    if (!routerStorage_->UpdateRouterInfo(bundleName, routerInfoMap, versionCode)) {
        APP_LOGW("add router for %{public}s failed", bundleName.c_str());
    }
}

void BundleDataMgr::GetAllBundleNames(std::set<std::string> &bundleNames)
{
    if (routerStorage_ == nullptr) {
        APP_LOGE("routerStorage_ is null");
        return;
    }
    return routerStorage_->GetAllBundleNames(bundleNames);
}

void BundleDataMgr::PreProcessAnyUserFlag(const std::string &bundleName, int32_t& flags, int32_t &userId) const
{
    if ((static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_OF_ANY_USER)) != 0) {
        flags = static_cast<uint32_t>(
            static_cast<uint32_t>(flags) | static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_DISABLE));
        std::vector<InnerBundleUserInfo> innerBundleUserInfos;
        if (!GetInnerBundleUserInfos(bundleName, innerBundleUserInfos)) {
            LOG_W(BMS_TAG_QUERY, "no userInfos for this bundle(%{public}s)", bundleName.c_str());
            return;
        }
        if (innerBundleUserInfos.empty()) {
            return;
        }
        for (auto &bundleUserInfo: innerBundleUserInfos) {
            if (bundleUserInfo.bundleUserInfo.userId == userId) {
                return;
            }
            if (bundleUserInfo.bundleUserInfo.userId < Constants::START_USERID) {
                return;
            }
        }
        userId = innerBundleUserInfos.begin()->bundleUserInfo.userId;
    }
}

void BundleDataMgr::PostProcessAnyUserFlags(
    int32_t flags, int32_t userId, int32_t originalUserId, BundleInfo &bundleInfo,
    const InnerBundleInfo &innerBundleInfo) const
{
    bool withApplicationFlag =
        (static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_APPLICATION))
            == static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_APPLICATION);
    if (withApplicationFlag) {
        if (userId >= Constants::START_USERID && userId != originalUserId) {
            uint32_t flagInstalled = static_cast<uint32_t>(ApplicationInfoFlag::FLAG_INSTALLED);
            uint32_t applicationFlags = static_cast<uint32_t>(bundleInfo.applicationInfo.applicationFlags);
            if ((applicationFlags & flagInstalled) != 0) {
                bundleInfo.applicationInfo.applicationFlags = static_cast<int32_t>(applicationFlags ^ flagInstalled);
            }
        }

        bool withAnyUser =
            (static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_OF_ANY_USER))
                == static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_OF_ANY_USER);
        if (withAnyUser) {
            const std::map<std::string, InnerBundleUserInfo>& innerUserInfos
                = innerBundleInfo.GetInnerBundleUserInfos();
            uint32_t flagOtherInstalled = static_cast<uint32_t>(ApplicationInfoFlag::FLAG_OTHER_INSTALLED);
            uint32_t applicationFlags = static_cast<uint32_t>(bundleInfo.applicationInfo.applicationFlags);
            if (!innerBundleInfo.HasInnerBundleUserInfo(originalUserId)) {
                bundleInfo.applicationInfo.applicationFlags =
                    static_cast<int32_t>(applicationFlags | flagOtherInstalled);
            } else if (innerUserInfos.size() > 1) {
                bundleInfo.applicationInfo.applicationFlags =
                    static_cast<int32_t>(applicationFlags | flagOtherInstalled);
            }
        }
    }
}

ErrCode BundleDataMgr::GetBaseSharedBundleInfos(const std::string &bundleName,
    std::vector<BaseSharedBundleInfo> &baseSharedBundleInfos, GetDependentBundleInfoFlag flag) const
{
    APP_LOGD("start, bundleName:%{public}s", bundleName.c_str());
    if ((flag == GetDependentBundleInfoFlag::GET_APP_SERVICE_HSP_BUNDLE_INFO) ||
        (flag == GetDependentBundleInfoFlag::GET_ALL_DEPENDENT_BUNDLE_INFO)) {
        // for app service hsp
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        std::lock_guard<std::mutex> hspLock(hspBundleNameMutex_);
        for (const std::string &hspName : appServiceHspBundleName_) {
            APP_LOGD("get hspBundleName: %{public}s", hspName.c_str());
            auto infoItem = bundleInfos_.find(hspName);
            if (infoItem == bundleInfos_.end()) {
                APP_LOGW("get hsp bundleInfo failed, hspName:%{public}s", hspName.c_str());
                continue;
            }
            ConvertServiceHspToSharedBundleInfo(infoItem->second, baseSharedBundleInfos);
        }
    }
    if (flag == GetDependentBundleInfoFlag::GET_APP_CROSS_HSP_BUNDLE_INFO ||
        flag == GetDependentBundleInfoFlag::GET_ALL_DEPENDENT_BUNDLE_INFO) {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        auto infoItem = bundleInfos_.find(bundleName);
        if (infoItem == bundleInfos_.end()) {
            APP_LOGW("GetBaseSharedBundleInfos get bundleInfo failed, bundleName:%{public}s", bundleName.c_str());
            return (flag == GetDependentBundleInfoFlag::GET_APP_CROSS_HSP_BUNDLE_INFO) ?
                ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST : ERR_OK;
        }
        const InnerBundleInfo &innerBundleInfo = infoItem->second;
        std::vector<Dependency> dependencies = innerBundleInfo.GetDependencies();
        for (const auto &item : dependencies) {
            BaseSharedBundleInfo baseSharedBundleInfo;
            if (GetBaseSharedBundleInfo(item, baseSharedBundleInfo)) {
                baseSharedBundleInfos.emplace_back(baseSharedBundleInfo);
            }
        }
    }
    APP_LOGD("GetBaseSharedBundleInfos(%{public}s) successfully", bundleName.c_str());
    return ERR_OK;
}

bool BundleDataMgr::GetBundleType(const std::string &bundleName, BundleType &bundleType)const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return false;
    }
    bundleType = item->second.GetApplicationBundleType();
    APP_LOGI("bundle %{public}s Type is %{public}d", bundleName.c_str(), bundleType);
    return true;
}

bool BundleDataMgr::GetBaseSharedBundleInfo(const Dependency &dependency,
    BaseSharedBundleInfo &baseSharedBundleInfo) const
{
    auto infoItem = bundleInfos_.find(dependency.bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGD("GetBaseSharedBundleInfo failed, can not find dependency bundle %{public}s",
            dependency.bundleName.c_str());
        return false;
    }
    const InnerBundleInfo &innerBundleInfo = infoItem->second;
    if (innerBundleInfo.GetApplicationBundleType() == BundleType::SHARED) {
        (void)GetAdaptBaseShareBundleInfo(innerBundleInfo, dependency, baseSharedBundleInfo);
    } else {
        APP_LOGW("GetBaseSharedBundleInfo failed, can not find bundleType %{public}d",
            innerBundleInfo.GetApplicationBundleType());
        return false;
    }
    APP_LOGD("GetBaseSharedBundleInfo(%{public}s) successfully)", dependency.bundleName.c_str());
    return true;
}

bool BundleDataMgr::GetAdaptBaseShareBundleInfo(
    const InnerBundleInfo &innerBundleInfo,
    const Dependency &dependency,
    BaseSharedBundleInfo &baseSharedBundleInfo) const
{
    // check cross hsp size and calling token
    if ((innerBundleInfo.GetAllHspVersion().size() <= 1) || (BundlePermissionMgr::IsNativeTokenType())) {
        return innerBundleInfo.GetMaxVerBaseSharedBundleInfo(dependency.moduleName, baseSharedBundleInfo);
    }
    // get running app hsp versionCode
    std::map<std::string, uint32_t> shareBundles;
    if ((AbilityManagerHelper::QueryRunningSharedBundles(IPCSkeleton::GetCallingPid(), shareBundles) == ERR_OK)) {
        auto item = shareBundles.find(dependency.bundleName);
        if ((item != shareBundles.end()) && innerBundleInfo.GetBaseSharedBundleInfo(dependency.moduleName,
            item->second, baseSharedBundleInfo)) {
            APP_LOGI_NOFUNC("get share bundle by pid -n %{public}s -v %{public}u succeed",
                dependency.bundleName.c_str(), item->second);
            return true;
        }
    }
    return innerBundleInfo.GetMaxVerBaseSharedBundleInfo(dependency.moduleName, baseSharedBundleInfo);
}

bool BundleDataMgr::DeleteSharedBundleInfo(const std::string &bundleName)
{
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    bool ret = false;
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem != bundleInfos_.end()) {
        APP_LOGD("del bundle name:%{public}s", bundleName.c_str());
        const InnerBundleInfo &innerBundleInfo = infoItem->second;
        ret = dataStorage_->DeleteStorageBundleInfo(innerBundleInfo);
        if (!ret) {
            APP_LOGW("delete storage error name:%{public}s", bundleName.c_str());
        }
        bundleInfos_.erase(bundleName);
    }
    std::lock_guard<std::mutex> stateLock(stateMutex_);
    installStates_.erase(bundleName);
    return ret;
}

ErrCode BundleDataMgr::GetBundlePackInfo(
    const std::string &bundleName, int32_t flags, BundlePackInfo &bundlePackInfo, int32_t userId) const
{
    APP_LOGD("Service BundleDataMgr GetBundlePackInfo start");
    int32_t requestUserId;
    if (userId == Constants::UNSPECIFIED_USERID) {
        requestUserId = GetUserIdByCallingUid();
    } else {
        requestUserId = userId;
    }

    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGW("getBundlePackInfo userId is invalid");
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo *innerBundleInfo = nullptr;
    if (!GetInnerBundleInfoWithFlags(bundleName, flags, innerBundleInfo, requestUserId)) {
        APP_LOGW("GetBundlePackInfo failed, bundleName:%{public}s", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetBundlePackInfo is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    BundlePackInfo innerBundlePackInfo = innerBundleInfo->GetBundlePackInfo();
    if (static_cast<uint32_t>(flags) & GET_PACKAGES) {
        bundlePackInfo.packages = innerBundlePackInfo.packages;
        return ERR_OK;
    }
    if (static_cast<uint32_t>(flags) & GET_BUNDLE_SUMMARY) {
        bundlePackInfo.summary.app = innerBundlePackInfo.summary.app;
        bundlePackInfo.summary.modules = innerBundlePackInfo.summary.modules;
        return ERR_OK;
    }
    if (static_cast<uint32_t>(flags) & GET_MODULE_SUMMARY) {
        bundlePackInfo.summary.modules = innerBundlePackInfo.summary.modules;
        return ERR_OK;
    }
    bundlePackInfo = innerBundlePackInfo;
    return ERR_OK;
}

bool BundleDataMgr::GetBundleInfosByMetaData(
    const std::string &metaData, std::vector<BundleInfo> &bundleInfos) const
{
    if (metaData.empty()) {
        APP_LOGW("bundle name is empty");
        return false;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return false;
    }

    bool find = false;
    int32_t requestUserId = GetUserId();
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &info = item.second;
        if (info.IsDisabled()) {
            APP_LOGD("app %{public}s is disabled", info.GetBundleName().c_str());
            continue;
        }
        if (info.CheckSpecialMetaData(metaData)) {
            BundleInfo bundleInfo;
            int32_t responseUserId = info.GetResponseUserId(requestUserId);
            info.GetBundleInfo(
                BundleFlag::GET_BUNDLE_WITH_ABILITIES, bundleInfo, responseUserId);
            bundleInfos.emplace_back(bundleInfo);
            find = true;
        }
    }
    return find;
}

bool BundleDataMgr::GetBundleList(std::vector<std::string> &bundleNames,
    int32_t userId, int32_t flags) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return false;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return false;
    }

    bool find = false;
    for (const auto &infoItem : bundleInfos_) {
        const InnerBundleInfo &innerBundleInfo = infoItem.second;
        int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
        if (CheckInnerBundleInfoWithFlags(
            innerBundleInfo, flags, responseUserId) != ERR_OK) {
            continue;
        }

        bundleNames.emplace_back(infoItem.first);
        find = true;
    }
    APP_LOGD("user(%{public}d) get installed bundles list result(%{public}d)", userId, find);
    return find;
}

bool BundleDataMgr::GetDebugBundleList(std::vector<std::string> &bundleNames, int32_t userId) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("UserId is invalid");
        return false;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGE("bundleInfos_ data is empty");
        return false;
    }

    for (const auto &infoItem : bundleInfos_) {
        const InnerBundleInfo &innerBundleInfo = infoItem.second;
        int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
        if (CheckInnerBundleInfoWithFlags(
            innerBundleInfo, BundleFlag::GET_BUNDLE_DEFAULT, responseUserId) != ERR_OK) {
            continue;
        }

        ApplicationInfo appInfo = innerBundleInfo.GetBaseApplicationInfo();
        if (appInfo.appProvisionType == Constants::APP_PROVISION_TYPE_DEBUG) {
            bundleNames.emplace_back(infoItem.first);
        }
    }

    bool find = !bundleNames.empty();
    APP_LOGD("user(%{public}d) get installed debug bundles list result(%{public}d)", userId, find);
    return find;
}

bool BundleDataMgr::GetBundleInfos(
    int32_t flags, std::vector<BundleInfo> &bundleInfos, int32_t userId) const
{
    if (userId == Constants::ALL_USERID) {
        return GetAllBundleInfos(flags, bundleInfos);
    }

    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return false;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        LOG_W(BMS_TAG_QUERY, "bundleInfos_ data is empty");
        return false;
    }

    bool find = false;
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &innerBundleInfo = item.second;
        if (innerBundleInfo.GetApplicationBundleType() == BundleType::SHARED) {
            LOG_D(BMS_TAG_QUERY, "app %{public}s is cross-app shared bundle, ignore",
                innerBundleInfo.GetBundleName().c_str());
            continue;
        }

        int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
        if (CheckInnerBundleInfoWithFlags(innerBundleInfo, flags, responseUserId) != ERR_OK) {
            continue;
        }

        BundleInfo bundleInfo;
        if (!innerBundleInfo.GetBundleInfo(flags, bundleInfo, responseUserId)) {
            continue;
        }

        bundleInfos.emplace_back(bundleInfo);
        find = true;
        // add clone bundle info
        // flags convert
        GetCloneBundleInfos(innerBundleInfo, flags, responseUserId, bundleInfo, bundleInfos);
    }

    LOG_D(BMS_TAG_QUERY, "get bundleInfos result(%{public}d) in user(%{public}d)", find, userId);
    return find;
}

ErrCode BundleDataMgr::CheckInnerBundleInfoWithFlags(
    const InnerBundleInfo &innerBundleInfo, const int32_t flags, int32_t userId, int32_t appIndex) const
{
    if (userId == Constants::INVALID_USERID) {
        APP_LOGD("userId is invalid");
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    if (innerBundleInfo.IsDisabled()) {
        APP_LOGW("bundleName: %{public}s status is disabled", innerBundleInfo.GetBundleName().c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_DISABLED;
    }

    if (appIndex == 0) {
        if (!(static_cast<uint32_t>(flags) & GET_APPLICATION_INFO_WITH_DISABLE)
            && !innerBundleInfo.GetApplicationEnabled(userId)) {
            APP_LOGW("bundleName: %{public}s userId: %{public}d incorrect",
                innerBundleInfo.GetBundleName().c_str(), userId);
            return ERR_BUNDLE_MANAGER_APPLICATION_DISABLED;
        }
    } else if (appIndex > 0 && appIndex <= Constants::INITIAL_SANDBOX_APP_INDEX) {
        int32_t requestUserId = GetUserId(userId);
        if (requestUserId == Constants::INVALID_USERID) {
            return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
        }
        int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
        bool isEnabled = false;
        ErrCode ret = innerBundleInfo.GetApplicationEnabledV9(responseUserId, isEnabled, appIndex);
        if (ret != ERR_OK) {
            return ERR_BUNDLE_MANAGER_APPLICATION_DISABLED;
        }
        if (!(static_cast<uint32_t>(flags) & GET_APPLICATION_INFO_WITH_DISABLE) && !isEnabled) {
            APP_LOGW("bundleName: %{public}s userId: %{public}d, appIndex: %{public}d incorrect",
                innerBundleInfo.GetBundleName().c_str(), requestUserId, appIndex);
            return ERR_BUNDLE_MANAGER_APPLICATION_DISABLED;
        }
    } else {
        LOG_E(BMS_TAG_QUERY, "CheckInnerBundleInfoWithFlags appIndex out of range");
        return ERR_APPEXECFWK_APP_INDEX_OUT_OF_RANGE;
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::CheckInnerBundleInfoWithFlagsV9(
    const InnerBundleInfo &innerBundleInfo, const int32_t flags, int32_t userId, int32_t appIndex) const
{
    if (userId == Constants::INVALID_USERID) {
        APP_LOGD("userId is invalid");
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    if (innerBundleInfo.IsDisabled()) {
        APP_LOGW("bundleName: %{public}s status is disabled", innerBundleInfo.GetBundleName().c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_DISABLED;
    }

    if (appIndex == 0) {
        if (!(static_cast<uint32_t>(flags) &
            static_cast<uint32_t>(GetApplicationFlag::GET_APPLICATION_INFO_WITH_DISABLE))) {
            bool isEnabled = false;
            ErrCode ret = innerBundleInfo.GetApplicationEnabledV9(userId, isEnabled, appIndex);
            if (ret != ERR_OK) {
                APP_LOGW("bundleName: %{public}s userId: %{public}d incorrect",
                    innerBundleInfo.GetBundleName().c_str(), userId);
                return ERR_BUNDLE_MANAGER_APPLICATION_DISABLED;
            }
            if (!isEnabled) {
                APP_LOGW("bundleName: %{public}s userId: %{public}d incorrect",
                    innerBundleInfo.GetBundleName().c_str(), userId);
                return ERR_BUNDLE_MANAGER_APPLICATION_DISABLED;
            }
        }
    } else if (appIndex > 0 && appIndex <= Constants::INITIAL_SANDBOX_APP_INDEX) {
        int32_t requestUserId = GetUserId(userId);
        if (requestUserId == Constants::INVALID_USERID) {
            return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
        }
        int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
        bool isEnabled = false;
        ErrCode ret = innerBundleInfo.GetApplicationEnabledV9(responseUserId, isEnabled, appIndex);
        if (ret != ERR_OK) {
            return ERR_BUNDLE_MANAGER_APPLICATION_DISABLED;
        }
        if (!(static_cast<uint32_t>(flags) &
            static_cast<uint32_t>(GetApplicationFlag::GET_APPLICATION_INFO_WITH_DISABLE))
            && !isEnabled) {
            APP_LOGW("bundleName: %{public}s userId: %{public}d, appIndex: %{public}d incorrect",
                innerBundleInfo.GetBundleName().c_str(), requestUserId, appIndex);
            return ERR_BUNDLE_MANAGER_APPLICATION_DISABLED;
        }
    } else {
        LOG_E(BMS_TAG_QUERY, "CheckInnerBundleInfoWithFlagsV9 appIndex out of range");
        return ERR_APPEXECFWK_APP_INDEX_OUT_OF_RANGE;
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::CheckBundleAndAbilityDisabled(
    const InnerBundleInfo &info, int32_t flags, int32_t userId) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    if (info.IsDisabled()) {
        LOG_NOFUNC_E(BMS_TAG_COMMON, "bundle disabled -n %{public}s -u %{public}d -f %{public}d",
            info.GetBundleName().c_str(), userId, flags);
        return ERR_BUNDLE_MANAGER_BUNDLE_DISABLED;
    }

    int32_t responseUserId = info.GetResponseUserId(requestUserId);
    bool isEnabled = false;
    auto ret = info.GetApplicationEnabledV9(responseUserId, isEnabled);
    if (ret != ERR_OK) {
        LOG_NOFUNC_W(BMS_TAG_COMMON, "bundle %{public}s not install in user %{public}d ret:%{public}d",
                info.GetBundleName().c_str(), responseUserId, ret);
        return ret;
    }
    if (!(static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_DISABLE))
        && !isEnabled) {
        LOG_NOFUNC_W(BMS_TAG_COMMON, "set enabled false -n %{public}s -u %{public}d -f %{public}d",
            info.GetBundleName().c_str(), responseUserId, flags);
        return ERR_BUNDLE_MANAGER_APPLICATION_DISABLED;
    }
    return ERR_OK;
}

bool BundleDataMgr::GetAllBundleInfos(int32_t flags, std::vector<BundleInfo> &bundleInfos) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return false;
    }

    bool find = false;
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &info = item.second;
        if (info.IsDisabled()) {
            APP_LOGD("app %{public}s is disabled", info.GetBundleName().c_str());
            continue;
        }
        if (info.GetApplicationBundleType() == BundleType::SHARED) {
            APP_LOGD("app %{public}s is cross-app shared bundle, ignore", info.GetBundleName().c_str());
            continue;
        }
        BundleInfo bundleInfo;
        info.GetBundleInfo(flags, bundleInfo, Constants::ALL_USERID);
        bundleInfos.emplace_back(bundleInfo);
        find = true;
        // add clone bundle info
        GetCloneBundleInfos(info, flags, Constants::ALL_USERID, bundleInfo, bundleInfos);
    }

    APP_LOGD("get all bundleInfos result(%{public}d)", find);
    return find;
}

ErrCode BundleDataMgr::GetBundleInfosV9(int32_t flags, std::vector<BundleInfo> &bundleInfos, int32_t userId) const
{
    if (userId == Constants::ALL_USERID) {
        return GetAllBundleInfosV9(flags, bundleInfos);
    }
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        LOG_W(BMS_TAG_QUERY, "bundleInfos_ data is empty");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    bool ofAnyUserFlag =
        (static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_OF_ANY_USER)) != 0;
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &innerBundleInfo = item.second;
        if (innerBundleInfo.GetApplicationBundleType() == BundleType::SHARED) {
            LOG_D(BMS_TAG_QUERY, "app %{public}s is cross-app shared bundle, ignore",
                innerBundleInfo.GetBundleName().c_str());
            continue;
        }
        int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
        auto flag = GET_BASIC_APPLICATION_INFO;
        if ((static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_DISABLE))
            == static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_DISABLE)) {
            flag = GET_APPLICATION_INFO_WITH_DISABLE;
        }
        if (CheckInnerBundleInfoWithFlags(innerBundleInfo, flag, responseUserId) != ERR_OK) {
            auto &hp = innerBundleInfo.GetInnerBundleUserInfos();
            if (ofAnyUserFlag && hp.size() > 0) {
                responseUserId = hp.begin()->second.bundleUserInfo.userId;
            } else {
                continue;
            }
        }
        uint32_t launchFlag = static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_ONLY_WITH_LAUNCHER_ABILITY);
        if (((static_cast<uint32_t>(flags) & launchFlag) == launchFlag) && (innerBundleInfo.IsHideDesktopIcon())) {
            LOG_D(BMS_TAG_QUERY, "bundleName %{public}s is hide desktopIcon",
                innerBundleInfo.GetBundleName().c_str());
            continue;
        }
        uint32_t cloudFlag = static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_CLOUD_KIT);
        if (((static_cast<uint32_t>(flags) & cloudFlag) == cloudFlag) &&
            !innerBundleInfo.GetCloudFileSyncEnabled() &&
            !innerBundleInfo.GetCloudStructuredDataSyncEnabled()) {
            APP_LOGD("getAllBundleInfosV9 bundleName %{public}s does not enable cloud sync",
                innerBundleInfo.GetBundleName().c_str());
            continue;
        }
        BundleInfo bundleInfo;
        if (innerBundleInfo.GetBundleInfoV9(flags, bundleInfo, responseUserId) != ERR_OK) {
            continue;
        }
        ProcessCertificate(bundleInfo, innerBundleInfo.GetBundleName(), flags);
        ProcessBundleMenu(bundleInfo, flags, true);
        ProcessBundleRouterMap(bundleInfo, flags, userId);
        PostProcessAnyUserFlags(flags, responseUserId, requestUserId, bundleInfo, innerBundleInfo);
        bundleInfos.emplace_back(bundleInfo);
        if (!ofAnyUserFlag && ((static_cast<uint32_t>(flags) &
            static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_EXCLUDE_CLONE)) !=
            static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_EXCLUDE_CLONE))) {
            // add clone bundle info
            GetCloneBundleInfos(innerBundleInfo, flags, responseUserId, bundleInfo, bundleInfos);
        }
    }
    if (bundleInfos.empty()) {
        LOG_W(BMS_TAG_QUERY, "bundleInfos is empty");
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::GetAllBundleInfosV9(int32_t flags, std::vector<BundleInfo> &bundleInfos) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &info = item.second;
        if (info.IsDisabled()) {
            APP_LOGD("app %{public}s is disabled", info.GetBundleName().c_str());
            continue;
        }
        if (info.GetApplicationBundleType() == BundleType::SHARED) {
            APP_LOGD("app %{public}s is cross-app shared bundle, ignore", info.GetBundleName().c_str());
            continue;
        }
        if (((static_cast<uint32_t>(flags) &
            static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_ONLY_WITH_LAUNCHER_ABILITY)) ==
            static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_ONLY_WITH_LAUNCHER_ABILITY)) &&
            (info.IsHideDesktopIcon())) {
            APP_LOGD("getAllBundleInfosV9 bundleName %{public}s is hide desktopIcon",
                info.GetBundleName().c_str());
            continue;
        }
        uint32_t cloudFlag = static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_CLOUD_KIT);
        if (((static_cast<uint32_t>(flags) & cloudFlag) == cloudFlag) &&
            !info.GetCloudFileSyncEnabled() &&
            !info.GetCloudStructuredDataSyncEnabled()) {
            APP_LOGD("getAllBundleInfosV9 bundleName %{public}s does not enable cloud sync",
                info.GetBundleName().c_str());
            continue;
        }
        BundleInfo bundleInfo;
        info.GetBundleInfoV9(flags, bundleInfo, Constants::ALL_USERID);
        ProcessCertificate(bundleInfo, info.GetBundleName(), flags);
        auto ret = ProcessBundleMenu(bundleInfo, flags, true);
        if (ret == ERR_OK) {
            bundleInfos.emplace_back(bundleInfo);
            if (((static_cast<uint32_t>(flags) &
                static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_EXCLUDE_CLONE)) !=
                static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_EXCLUDE_CLONE))) {
                // add clone bundle info
                GetCloneBundleInfos(info, flags, Constants::ALL_USERID, bundleInfo, bundleInfos);
            }
        }
    }
    if (bundleInfos.empty()) {
        APP_LOGW("bundleInfos is empty");
    }
    return ERR_OK;
}

bool BundleDataMgr::GetBundleNameForUid(const int32_t uid, std::string &bundleName) const
{
    int32_t appIndex = 0;
    return GetBundleNameAndIndexForUid(uid, bundleName, appIndex) == ERR_OK;
}

ErrCode BundleDataMgr::GetBundleNameAndIndexForUid(const int32_t uid, std::string &bundleName,
    int32_t &appIndex) const
{
    ErrCode ret = GetBundleNameAndIndex(uid, bundleName, appIndex);
    if (ret != ERR_OK) {
        APP_LOGD("uid: %{public}d invalid!", uid);
        return ret;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;
    InnerBundleInfo sandboxInfo;
    if (GetInnerBundleInfoNoLock(bundleName, uid, appIndex, innerBundleInfo) != ERR_OK) {
        if (sandboxAppHelper_ == nullptr) {
            return ERR_BUNDLE_MANAGER_INVALID_UID;
        }
        if (sandboxAppHelper_->GetInnerBundleInfoByUid(uid, sandboxInfo) != ERR_OK) {
            return ERR_BUNDLE_MANAGER_INVALID_UID;
        }
        innerBundleInfo = &sandboxInfo;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetBundleNameAndIndexForUid is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    bundleName = innerBundleInfo->GetBundleName();
    APP_LOGD("GetBundleNameForUid, uid %{public}d, bundleName %{public}s, appIndex %{public}d",
        uid, bundleName.c_str(), appIndex);
    return ERR_OK;
}

ErrCode BundleDataMgr::GetBundleNameAndIndex(const int32_t uid, std::string &bundleName,
    int32_t &appIndex) const
{
    if (uid < Constants::BASE_APP_UID) {
        APP_LOGD("the uid(%{public}d) is not an application", uid);
        return ERR_BUNDLE_MANAGER_INVALID_UID;
    }
    int32_t userId = GetUserIdByUid(uid);
    int32_t bundleId = uid - userId * Constants::BASE_USER_RANGE;
    if (bundleId < 0) {
        APP_LOGD("the uid(%{public}d) is not an application", uid);
        return ERR_BUNDLE_MANAGER_INVALID_UID;
    }

    std::shared_lock<std::shared_mutex> bundleIdLock(bundleIdMapMutex_);
    auto bundleIdIter = bundleIdMap_.find(bundleId);
    if (bundleIdIter == bundleIdMap_.end()) {
        APP_LOGD("bundleId %{public}d is not existed", bundleId);
        return ERR_BUNDLE_MANAGER_INVALID_UID;
    }
    std::string keyName = bundleIdIter->second;
    if (keyName.empty()) {
        return ERR_BUNDLE_MANAGER_INVALID_UID;
    }
    // bundleName, sandbox_app: \d+_w+, clone_app: \d+clone_w+, others
    if (isdigit(keyName[0])) {
        size_t pos = keyName.find_first_not_of("0123456789");
        if (pos == std::string::npos) {
            return ERR_BUNDLE_MANAGER_INVALID_UID;
        }
        std::string index = keyName.substr(0, pos);
        if (!OHOS::StrToInt(index, appIndex)) {
            return ERR_BUNDLE_MANAGER_INVALID_UID;
        }

        auto clonePos = keyName.find(CLONE_BUNDLE_PREFIX);
        if (clonePos != std::string::npos && clonePos == pos) {
            bundleName = keyName.substr(clonePos + strlen(CLONE_BUNDLE_PREFIX));
            return ERR_OK;
        }

        auto sandboxPos = keyName.find(Constants::FILE_UNDERLINE);
        if (sandboxPos != std::string::npos && sandboxPos == pos) {
            bundleName = keyName.substr(sandboxPos + strlen(Constants::FILE_UNDERLINE));
            return ERR_OK;
        }
    }

    bundleName = keyName;
    appIndex = 0;
    return ERR_OK;
}

ErrCode BundleDataMgr::GetInnerBundleInfoNoLock(const std::string bundleName, const int32_t uid,
    const int32_t appIndex, const InnerBundleInfo *&innerBundleInfo) const
{
    int32_t userId = GetUserIdByUid(uid);
    auto bundleInfoIter = bundleInfos_.find(bundleName);
    if (bundleInfoIter == bundleInfos_.end()) {
        APP_LOGE("bundleName %{public}s is not existed in bundleInfos_", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_INVALID_UID;
    }
    int32_t oriUid = bundleInfoIter->second.GetUid(userId, appIndex);
    if (oriUid == uid) {
        innerBundleInfo = &(bundleInfoIter->second);
        return ERR_OK;
    }

    APP_LOGW("bn %{public}s uid %{public}d oriUid %{public}d ", bundleName.c_str(), uid, oriUid);
    return ERR_BUNDLE_MANAGER_INVALID_UID;
}

ErrCode BundleDataMgr::GetInnerBundleInfoAndIndexByUid(const int32_t uid, InnerBundleInfo& innerBundleInfo,
    int32_t &appIndex) const
{
    if (uid < Constants::BASE_APP_UID) {
        APP_LOGD("the uid(%{public}d) is not an application", uid);
        return ERR_BUNDLE_MANAGER_INVALID_UID;
    }
    int32_t userId = GetUserIdByUid(uid);
    int32_t bundleId = uid - userId * Constants::BASE_USER_RANGE;

    std::string keyName;
    {
        std::shared_lock<std::shared_mutex> bundleIdLock(bundleIdMapMutex_);
        auto bundleIdIter = bundleIdMap_.find(bundleId);
        if (bundleIdIter == bundleIdMap_.end()) {
            APP_LOGW_NOFUNC("uid %{public}d is not existed", uid);
            return ERR_BUNDLE_MANAGER_INVALID_UID;
        }
        keyName = bundleIdIter->second;
    }
    std::string bundleName = keyName;
    GetBundleNameAndIndexByName(keyName, bundleName, appIndex);

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto bundleInfoIter = bundleInfos_.find(bundleName);
    if (bundleInfoIter == bundleInfos_.end()) {
        APP_LOGE("bundleName %{public}s is not existed in bundleInfos_", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_INVALID_UID;
    }
    int32_t oriUid = bundleInfoIter->second.GetUid(userId, appIndex);
    if (oriUid == uid) {
        innerBundleInfo = bundleInfoIter->second;
        return ERR_OK;
    }

    APP_LOGW("bn %{public}s uid %{public}d oriUid %{public}d ", bundleName.c_str(), uid, oriUid);
    return ERR_BUNDLE_MANAGER_INVALID_UID;
}

ErrCode BundleDataMgr::GetInnerBundleInfoByUid(const int32_t uid, InnerBundleInfo& innerBundleInfo) const
{
    int32_t appIndex = 0;
    return GetInnerBundleInfoAndIndexByUid(uid, innerBundleInfo, appIndex);
}

const std::vector<PreInstallBundleInfo> BundleDataMgr::GetRecoverablePreInstallBundleInfos(int32_t userId)
{
    std::vector<PreInstallBundleInfo> recoverablePreInstallBundleInfos;
    if (userId == Constants::INVALID_USERID) {
        APP_LOGW("userId %{public}d is invalid", userId);
        return recoverablePreInstallBundleInfos;
    }
    std::vector<PreInstallBundleInfo> preInstallBundleInfos = GetAllPreInstallBundleInfos();
    for (auto preInstallBundleInfo: preInstallBundleInfos) {
        if (!preInstallBundleInfo.IsRemovable()) {
            if ((preInstallBundleInfo.GetBundleType() != BundleType::APP) &&
                (preInstallBundleInfo.GetBundleType() != BundleType::ATOMIC_SERVICE)) {
                continue;
            }
        }
        if (preInstallBundleInfo.HasForceUninstalledUser(userId)) {
            APP_LOGW("-n %{public}s is force unisntalled in -u %{public}d",
                preInstallBundleInfo.GetBundleName().c_str(), userId);
            continue;
        }
        if (BundleUserMgrHostImpl::SkipThirdPreloadAppInstallation(userId, preInstallBundleInfo)) {
            continue;
        }
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        auto infoItem = bundleInfos_.find(preInstallBundleInfo.GetBundleName());
        if (infoItem == bundleInfos_.end()) {
            recoverablePreInstallBundleInfos.emplace_back(preInstallBundleInfo);
            continue;
        }
        if (infoItem->second.IsU1Enable() &&
            !infoItem->second.HasInnerBundleUserInfo(Constants::U1)) {
            recoverablePreInstallBundleInfos.emplace_back(preInstallBundleInfo);
            continue;
        }
        if (!infoItem->second.HasInnerBundleUserInfo(Constants::DEFAULT_USERID) &&
            !infoItem->second.HasInnerBundleUserInfo(userId)) {
            recoverablePreInstallBundleInfos.emplace_back(preInstallBundleInfo);
        }
    }
    return recoverablePreInstallBundleInfos;
}

bool BundleDataMgr::IsBundleExist(const std::string &bundleName) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    return bundleInfos_.find(bundleName) != bundleInfos_.end();
}

bool BundleDataMgr::HasUserInstallInBundle(
    const std::string &bundleName, const int32_t userId) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        return false;
    }

    return infoItem->second.HasInnerBundleUserInfo(userId);
}

#ifdef ABILITY_RUNTIME_ENABLE
std::vector<int32_t> BundleDataMgr::GetNoRunningBundleCloneIndexes(const sptr<IAppMgr> appMgrProxy,
    const std::string &bundleName, int32_t userId, const std::vector<int32_t> &cloneAppIndexes) const
{
    std::vector<int32_t> noRunningCloneAppIndexes;
    if (appMgrProxy == nullptr) {
        APP_LOGW("fail to find the app mgr service to check app is running");
        return noRunningCloneAppIndexes;
    }

    for (const auto &appIndex : cloneAppIndexes) {
        bool running = SystemAbilityHelper::IsAppRunning(appMgrProxy, bundleName, appIndex, userId);
        if (running) {
            APP_LOGW("No del cache for %{public}s[%{public}d] in user[%{public}d]: is running",
                bundleName.c_str(), appIndex, userId);
            continue;
        }
        noRunningCloneAppIndexes.emplace_back(appIndex);
    }
    return noRunningCloneAppIndexes;
}
#endif

void BundleDataMgr::GetBundleCacheInfo(
    std::function<std::vector<int32_t>(std::string&, std::vector<int32_t>&)> idxFilter,
    const InnerBundleInfo &info,
    std::vector<std::tuple<std::string, std::vector<std::string>, std::vector<int32_t>>> &validBundles,
    const int32_t userId, bool isClean) const
{
    std::string bundleName = info.GetBundleName();
    if (isClean && !info.GetBaseApplicationInfo().userDataClearable) {
        APP_LOGW("Not clearable:%{public}s, userid:%{public}d", bundleName.c_str(), userId);
        return;
    }
    std::vector<std::string> moduleNameList;
    info.GetModuleNames(moduleNameList);
    std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexesByInnerBundleInfo(info, userId);
    cloneAppIndexes.emplace_back(0);
    std::vector<int32_t> allAppIndexes = cloneAppIndexes;
    if (isClean) {
        allAppIndexes = idxFilter(bundleName, cloneAppIndexes);
    }
    validBundles.emplace_back(std::make_tuple(bundleName, moduleNameList, allAppIndexes));
    // add atomic service
    if (info.GetApplicationBundleType() == BundleType::ATOMIC_SERVICE) {
        std::string atomicServiceName;
        AccountSA::OhosAccountInfo accountInfo;
        auto ret = GetDirForAtomicServiceByUserId(bundleName, userId, accountInfo, atomicServiceName);
        if (ret == ERR_OK && !atomicServiceName.empty()) {
            APP_LOGD("atomicServiceName: %{public}s", atomicServiceName.c_str());
            validBundles.emplace_back(std::make_tuple(atomicServiceName, moduleNameList, allAppIndexes));
        }
    }
}

void BundleDataMgr::GetBundleCacheInfos(const int32_t userId, std::vector<std::tuple<std::string,
    std::vector<std::string>, std::vector<int32_t>>> &validBundles, bool isClean) const
{
#ifdef ABILITY_RUNTIME_ENABLE
    sptr<IAppMgr> appMgrProxy = iface_cast<IAppMgr>(SystemAbilityHelper::GetSystemAbility(APP_MGR_SERVICE_ID));
    if (appMgrProxy == nullptr) {
        APP_LOGE("CleanBundleCache fail to find the app mgr service to check app is running");
        return;
    }
    auto idxFiltor = [&appMgrProxy, userId, this](std::string &bundleName, std::vector<int32_t> &allidx) {
        return this->GetNoRunningBundleCloneIndexes(appMgrProxy, bundleName, userId, allidx);
    };
#else
    auto idxFiltor = [](std::string &bundleName, std::vector<int32_t> &allidx) {
        return allidx;
    };
#endif
    std::map<std::string, InnerBundleInfo> infos = GetAllInnerBundleInfos();
    for (const auto &item : infos) {
        GetBundleCacheInfo(idxFiltor, item.second, validBundles, userId, isClean);
    }
    return;
}

std::string BundleDataMgr::GetRelationPath() const
{
#ifdef CONFIG_POLOCY_ENABLE
    char buf[MAX_PATH_LEN] = { 0 };
    char *relationPath = GetOneCfgFile(EXTEND_DATASIZE_PATH_SUFFIX, buf, MAX_PATH_LEN);
    if (relationPath == nullptr || relationPath[0] == '\0') {
        LOG_E(BMS_TAG_INSTALLD, "GetOneCfgFile failed");
        return "";
    }
    if (strlen(relationPath) >= MAX_PATH_LEN) {
        LOG_E(BMS_TAG_INSTALLD, "relationPath length exceeds");
        return "";
    }
    return relationPath;
#endif
    return "";
}

void BundleDataMgr::LoadSaUidMap(std::map<std::string, std::set<int32_t>> &saUidMap) const
{
    std::string relationPath = GetRelationPath();
    if (relationPath.empty()) {
        APP_LOGE("relationPath is empty.");
        return;
    }
    if (relationPath.length() >= PATH_MAX) {
        APP_LOGE("relationPath length(%{public}u) longer than max length(%{public}d)",
            static_cast<unsigned int>(relationPath.length()), PATH_MAX);
        return;
    }
    std::string realPath;
    realPath.reserve(PATH_MAX);
    realPath.resize(PATH_MAX);
    if (realpath(relationPath.c_str(), &(realPath[0])) == nullptr) {
        APP_LOGE("transform real path: %{private}s  error: %{public}d", relationPath.c_str(), errno);
        return;
    }
    nlohmann::json object;
    if (!BundleParser::ReadFileIntoJson(relationPath, object)) {
        APP_LOGE("Parse file %{private}s failed", relationPath.c_str());
        return;
    }

    if (!object.contains(HAP_EXTEND_DATASIZE_RELATIONS) || !object.at(HAP_EXTEND_DATASIZE_RELATIONS).is_array()) {
        APP_LOGE("Hap extend dataSize relations not existed");
        return;
    }

    for (auto &relation : object.at(HAP_EXTEND_DATASIZE_RELATIONS).items()) {
        const nlohmann::json &jsonObject = relation.value();
        if (!jsonObject.contains(BUNDLE_NAME_KEY) || !jsonObject.at(BUNDLE_NAME_KEY).is_string()) {
            continue;
        }
        std::string bundleName = jsonObject.at(BUNDLE_NAME_KEY).get<std::string>();
        if (bundleName.empty() || !jsonObject.contains(SA_UID) || !jsonObject.at(SA_UID).is_array()) {
            continue;
        }
        std::set<int32_t> saUidList;
        for (auto &saUid : jsonObject.at(SA_UID).items()) {
            if (!saUid.value().is_number()) {
                continue;
            }
            saUidList.emplace(saUid.value().get<int32_t>());
        }
        saUidMap[bundleName] = saUidList;
    }
}

std::set<int32_t> BundleDataMgr::GetBindingSAUidsByBundleName(const std::string &bundleName,
    const std::map<std::string, std::set<int32_t>> &saUidMap) const
{
    std::set<int32_t> saUids;
    if (saUidMap.empty()) {
        APP_LOGE("saUid map is empty");
        return saUids;
    }
    auto relation = saUidMap.find(bundleName);
    if (relation == saUidMap.end()) {
        APP_LOGD("Bundle %{public}s not found in saUidMap", bundleName.c_str());
        return saUids;
    }
    return relation->second;
}

bool BundleDataMgr::GetBundleStats(const std::string &bundleName,
    const int32_t userId, std::vector<int64_t> &bundleStats, const int32_t appIndex, const uint32_t statFlag) const
{
    int32_t responseUserId = -1;
    int32_t uid = Constants::INVALID_UID;
    std::vector<std::string> moduleNameList;
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        const auto infoItem = bundleInfos_.find(bundleName);
        if (infoItem != bundleInfos_.end()) {
            responseUserId = infoItem->second.GetResponseUserId(userId);
            uid = infoItem->second.GetUid(responseUserId, appIndex);
            infoItem->second.GetModuleNames(moduleNameList);
        }
    }
    if (uid == Constants::INVALID_UID) {
        UninstallBundleInfo uninstallBundleInfo;
        if (!GetUninstallBundleInfo(bundleName, uninstallBundleInfo)) {
            APP_LOGD("bundle:%{public}s [%{public}d] is not existed and not uninstalled with keepdata",
                bundleName.c_str(), appIndex);
            return false;
        }
        responseUserId = userId;
        uid = uninstallBundleInfo.GetUid(responseUserId, appIndex);
        moduleNameList = uninstallBundleInfo.moduleNames;
        if (uid == Constants::INVALID_UID) {
            APP_LOGD("can not found bundle: %{public}s in uninstallBundleInfo for index: %{public}d",
            bundleName.c_str(), appIndex);
        }
    }
    std::unordered_set<int32_t> uids;
    uids.emplace(uid);
    auto activeUserId = AccountHelper::GetUserIdByCallerType();
    if (appIndex == Constants::MAIN_APP_INDEX && activeUserId == userId) {
        std::map<std::string, std::set<int32_t>> saUidMap;
        LoadSaUidMap(saUidMap);
        auto saUids = GetBindingSAUidsByBundleName(bundleName, saUidMap);
        uids.insert(saUids.begin(), saUids.end());
    }
    ErrCode ret = InstalldClient::GetInstance()->GetBundleStats(
        bundleName, responseUserId, bundleStats, uids, appIndex, statFlag, moduleNameList);
    if (ret != ERR_OK) {
        APP_LOGW("%{public}s getStats failed", bundleName.c_str());
        return false;
    }
    CalculatePreInstalledBundleSize(bundleName, appIndex, bundleStats);
    return true;
}

void BundleDataMgr::CalculatePreInstalledBundleSize(const std::string& bundleName, const int32_t appIndex,
    std::vector<int64_t> &bundleStats) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem != bundleInfos_.end() && appIndex == 0 && infoItem->second.IsPreInstallApp() && !bundleStats.empty()) {
        for (const auto &innerModuleInfo : infoItem->second.GetInnerModuleInfos()) {
            if (innerModuleInfo.second.hapPath.find(Constants::BUNDLE_CODE_DIR) == 0) {
                continue;
            }
            bundleStats[0] += BundleUtil::GetFileSize(innerModuleInfo.second.hapPath);
        }
    }
}

ErrCode BundleDataMgr::BatchGetBundleStats(const std::vector<std::string> &bundleNames, const int32_t userId,
    std::unordered_map<std::string, std::unordered_set<int32_t>> &uidMap,
    std::vector<BundleStorageStats> &bundleStats) const
{
    auto activeUserId = AccountHelper::GetUserIdByCallerType();
    if (activeUserId != userId) {
        return InstalldClient::GetInstance()->BatchGetBundleStats(bundleNames, uidMap, bundleStats);
    }
    std::map<std::string, std::set<int32_t>> saUidMap;
    LoadSaUidMap(saUidMap);
    if (!bundleNames.empty()) {
        for (const auto &bundleName : bundleNames) {
            auto saUids = GetBindingSAUidsByBundleName(bundleName, saUidMap);
            if (!saUids.empty()) {
                uidMap[bundleName].insert(saUids.begin(), saUids.end());
            }
        }
    }
    return InstalldClient::GetInstance()->BatchGetBundleStats(bundleNames, uidMap, bundleStats);
}

ErrCode BundleDataMgr::BatchGetBundleStats(const std::vector<std::string> &bundleNames, const int32_t userId,
    std::vector<BundleStorageStats> &bundleStats) const
{
    std::unordered_map<std::string, std::unordered_set<int32_t>> uidMap;
    std::vector<std::string> bundleNameList = bundleNames;
    std::vector<BundleStorageStats> bundleStatsList;
    if (!HasUserId(userId)) {
        APP_LOGE("userId %{public}d not exist.", userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        for (auto bundleName = bundleNameList.begin(); bundleName != bundleNameList.end();) {
            const auto infoItem = bundleInfos_.find(*bundleName);
            std::string name = *bundleName;
            InnerBundleUserInfo userInfo;
            if (infoItem == bundleInfos_.end() ||
                !infoItem->second.GetInnerBundleUserInfo(infoItem->second.GetResponseUserId(userId), userInfo)) {
                UninstallBundleInfo uninstallBundleInfo;
                if (!GetUninstallBundleInfo(*bundleName, uninstallBundleInfo)) {
                    APP_LOGD("bundle is not existed and not uninstalled with keepdata before");
                    BundleStorageStats stats;
                    stats.bundleName = *bundleName;
                    bundleName = bundleNameList.erase(bundleName);
                    stats.errCode = ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
                    bundleStatsList.push_back(stats);
                } else {
                    APP_LOGD("bundle: %{public}s is uninstalled with keepdata before, uid: %{public}d",
                        name.c_str(), uninstallBundleInfo.GetUid(userId));
                    uidMap[*bundleName].insert(uninstallBundleInfo.GetUid(userId));
                    ++bundleName;
                }
                continue;
            }
            APP_LOGD("bundle: %{public}s is installed, uid: %{public}d",
                    name.c_str(), userInfo.uid);
            uidMap[*bundleName].insert(userInfo.uid);
            ++bundleName;
        }
    }
    ErrCode ret = BatchGetBundleStats(bundleNameList, userId, uidMap, bundleStats);
    if (ret != ERR_OK) {
        APP_LOGE("getStats failed");
        return ret;
    }
    if (!bundleStatsList.empty()) {
        bundleStats.insert(bundleStats.end(), bundleStatsList.begin(), bundleStatsList.end());
    }
    for (const auto &name : bundleNameList) {
        GetPreBundleSize(name, bundleStats);
    }
    return ERR_OK;
}

void BundleDataMgr::GetPreBundleSize(const std::string &name, std::vector<BundleStorageStats> &bundleStats) const
{
    auto statsIter = std::find_if(bundleStats.begin(), bundleStats.end(),
        [&name](const BundleStorageStats &stats) { return stats.bundleName == name; });
    if (statsIter == bundleStats.end()) {
        return;
    }
    std::vector<std::string> hapPaths;
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        const auto infoItem = bundleInfos_.find(name);
        if (infoItem != bundleInfos_.end() && infoItem->second.IsPreInstallApp() && !bundleStats.empty()) {
            for (const auto &innerModuleInfo : infoItem->second.GetInnerModuleInfos()) {
                if (innerModuleInfo.second.hapPath.find(Constants::BUNDLE_CODE_DIR) == 0) {
                    continue;
                }
                hapPaths.emplace_back(innerModuleInfo.second.hapPath);
            }
        }
    }
    for (const auto &path : hapPaths) {
        statsIter->bundleStats[0] += BundleUtil::GetFileSize(path);
    }
}

void BundleDataMgr::GetBundleModuleNames(const std::string &bundleName,
    std::vector<std::string> &moduleNameList) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("No modules of: %{public}s", bundleName.c_str());
        return;
    }
    infoItem->second.GetModuleNames(moduleNameList);
}

bool BundleDataMgr::GetAllUnisntallBundleUids(const int32_t requestUserId,
    const std::map<std::string, UninstallBundleInfo> &uninstallBundleInfos,
    std::unordered_set<int32_t> &uids) const
{
    for (const auto &info : uninstallBundleInfos) {
        std::string bundleName = info.first;
        if (info.second.userInfos.empty()) {
            continue;
        }
        
        if (info.second.bundleType != BundleType::ATOMIC_SERVICE && info.second.bundleType != BundleType::APP) {
            APP_LOGD("BundleType is invalid: %{public}d, bundname: %{public}s", info.second.bundleType, bundleName.c_str());
            continue;
        }
        
        std::string mainBundle = std::to_string(requestUserId);
        std::string cloneBundle = mainBundle + "_";
        for (const auto& pair : info.second.userInfos) {
            const std::string& key = pair.first;
            if (key == mainBundle || key.find(cloneBundle) == 0) {
                uids.emplace(pair.second.uid);
                APP_LOGI("get uid: %{public}d for app: %{public}s", pair.second.uid,
                    bundleName.c_str());
            }
        }
    }
    return true;
}

void BundleDataMgr::GetAllInstallBundleUids(const int32_t userId, const int32_t requestUserId, int32_t &responseUserId,
    std::unordered_set<int32_t> &uids, std::vector<std::string> &bundleNames) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    responseUserId = userId;
    auto activeUserId = AccountHelper::GetUserIdByCallerType();
    bool isActiveUserId = (activeUserId == userId);
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &info = item.second;
        std::string bundleName = info.GetBundleName();
        responseUserId = info.GetResponseUserId(requestUserId);
        if (responseUserId == Constants::INVALID_USERID) {
            APP_LOGD("bundle %{public}s is not installed in user %{public}d or 0", bundleName.c_str(), userId);
            continue;
        }
        BundleType type = info.GetApplicationBundleType();
        if (type != BundleType::ATOMIC_SERVICE && type != BundleType::APP) {
            APP_LOGD("BundleType is invalid: %{public}d, bundname: %{public}s", type, bundleName.c_str());
            continue;
        }
        std::vector<int32_t> allAppIndexes = {0};
        if (type == BundleType::APP) {
            std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexesByInnerBundleInfo(info, responseUserId);
            allAppIndexes.insert(allAppIndexes.end(), cloneAppIndexes.begin(), cloneAppIndexes.end());
        }
        for (int32_t appIndex: allAppIndexes) {
            int32_t uid = info.GetUid(responseUserId, appIndex);
            uids.emplace(uid);
        }
        if (isActiveUserId) {
            bundleNames.emplace_back(bundleName);
        }
    }
}

bool BundleDataMgr::GetAllBundleStats(const int32_t userId, std::vector<int64_t> &bundleStats) const
{
    std::unordered_set<int32_t> uids;
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("invalid userid :%{public}d", userId);
        return false;
    }
    int32_t responseUserId = -1;
    std::vector<std::string> bundleNames;
    GetAllInstallBundleUids(userId, requestUserId, responseUserId, uids, bundleNames);
    if (!bundleNames.empty()) {
        std::map<std::string, std::set<int32_t>> saUidMap;
        LoadSaUidMap(saUidMap);
        for (const auto &bundleName : bundleNames) {
            auto saUids = GetBindingSAUidsByBundleName(bundleName, saUidMap);
            if (!saUids.empty()) {
                uids.insert(saUids.begin(), saUids.end());
            }
        }
    }
    std::map<std::string, UninstallBundleInfo> uninstallBundleInfos;
    GetAllUninstallBundleInfo(uninstallBundleInfos);
    GetAllUnisntallBundleUids(requestUserId, uninstallBundleInfos, uids);
    std::vector<int32_t> uidVec(uids.begin(), uids.end());
    if (InstalldClient::GetInstance()->GetAllBundleStats(responseUserId, bundleStats, uidVec) != ERR_OK) {
        APP_LOGW("GetAllBundleStats failed, userId: %{public}d", responseUserId);
        return false;
    }
    if (bundleStats.empty()) {
        APP_LOGE("bundle stats is empty");
        return true;
    }
    return true;
}
#ifdef BUNDLE_FRAMEWORK_FREE_INSTALL
int64_t BundleDataMgr::GetBundleSpaceSize(const std::string &bundleName) const
{
    return GetBundleSpaceSize(bundleName, AccountHelper::GetUserIdByCallerType());
}

int64_t BundleDataMgr::GetBundleSpaceSize(const std::string &bundleName, int32_t userId) const
{
    int64_t spaceSize = 0;
    int64_t bundleDataSize = 0;
    int64_t localBundleDataSize = 0;
    if (userId != Constants::ALL_USERID) {
        std::vector<int64_t> bundleStats;
        if (!GetBundleStats(bundleName, userId, bundleStats) || bundleStats.empty()) {
            APP_LOGW("GetBundleStats: bundleName: %{public}s failed", bundleName.c_str());
            return spaceSize;
        }

        bundleDataSize = bundleStats[0];
        localBundleDataSize = bundleStats[1];
        spaceSize = bundleDataSize + localBundleDataSize;
        return spaceSize;
    }

    for (const auto &iterUserId : GetAllUser()) {
        std::vector<int64_t> bundleStats;
        if (!GetBundleStats(bundleName, iterUserId, bundleStats) || bundleStats.empty()) {
            APP_LOGW("GetBundleStats: bundleName: %{public}s failed", bundleName.c_str());
            continue;
        }

        bundleDataSize = bundleStats[0];
        localBundleDataSize = bundleStats[1];
        if (spaceSize == 0) {
            spaceSize = bundleDataSize + localBundleDataSize;
        } else {
            spaceSize += localBundleDataSize;
        }
    }

    return spaceSize;
}

int64_t BundleDataMgr::GetAllFreeInstallBundleSpaceSize() const
{
    int64_t allSize = 0;
    std::map<std::string, std::vector<std::string>> freeInstallModules;
    if (!GetFreeInstallModules(freeInstallModules)) {
        APP_LOGW("no removable bundles");
        return allSize;
    }

    for (const auto &iter : freeInstallModules) {
        APP_LOGD("%{public}s is freeInstall bundle", iter.first.c_str());
        allSize += GetBundleSpaceSize(iter.first, Constants::ALL_USERID);
    }

    APP_LOGI("All freeInstall app size:%{public}" PRId64, allSize);
    return allSize;
}

bool BundleDataMgr::GetFreeInstallModules(
    std::map<std::string, std::vector<std::string>> &freeInstallModules) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ is data is empty");
        return false;
    }

    for (const auto &iter : bundleInfos_) {
        std::vector<std::string> modules;
        if (!iter.second.GetFreeInstallModules(modules)) {
            continue;
        }

        freeInstallModules.emplace(iter.first, modules);
    }

    return !freeInstallModules.empty();
}
#endif

bool BundleDataMgr::GetBundlesForUid(const int uid, std::vector<std::string> &bundleNames) const
{
    int32_t appIndex = 0;
    std::string bundleName;
    ErrCode ret = GetBundleNameAndIndex(uid, bundleName, appIndex);
    if (ret != ERR_OK) {
        APP_LOGW("uid: %{public}d invalid!", uid);
        return false;
    }
    bundleNames.emplace_back(bundleName);
    return true;
}

ErrCode BundleDataMgr::GetNameForUid(const int uid, std::string &name) const
{
    int32_t appIndex = 0;
    ErrCode ret = GetBundleNameAndIndex(uid, name, appIndex);
    if (ret != ERR_OK) {
        APP_LOGD("uid(%{public}d) invalid", uid);
        return ret;
    }
    APP_LOGD("GetBundleNameForUid, uid %{public}d, bundleName %{public}s, appIndex %{public}d",
        uid, name.c_str(), appIndex);
    return ERR_OK;
}

ErrCode BundleDataMgr::GetAppIdentifierAndAppIndex(const uint32_t accessTokenId,
    std::string &appIdentifier, int32_t &appIndex)
{
    Security::AccessToken::HapTokenInfo tokenInfo;
    if (Security::AccessToken::AccessTokenKit::GetHapTokenInfo(accessTokenId, tokenInfo) != ERR_OK) {
        APP_LOGE("accessTokenId %{public}d not exist", accessTokenId);
        return ERR_BUNDLE_MANAGER_ACCESS_TOKENID_NOT_EXIST;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const auto infoItem = bundleInfos_.find(tokenInfo.bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGE("bundleName %{public}s not exist", tokenInfo.bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }

    const InnerBundleInfo &innerBundleInfo = infoItem->second;
    appIdentifier = innerBundleInfo.GetAppIdentifier();
    appIndex = tokenInfo.instIndex;
    return ERR_OK;
}

ErrCode BundleDataMgr::GetInnerBundleInfoWithSandboxByUid(const int uid, InnerBundleInfo &innerBundleInfo) const
{
    ErrCode ret = GetInnerBundleInfoByUid(uid, innerBundleInfo);
    if (ret != ERR_OK) {
        APP_LOGD("get innerBundleInfo from bundleInfo_ by uid failed");
        if (sandboxAppHelper_ == nullptr) {
            APP_LOGW("sandboxAppHelper_ is nullptr");
            return ERR_BUNDLE_MANAGER_INVALID_UID;
        }
        if (sandboxAppHelper_->GetInnerBundleInfoByUid(uid, innerBundleInfo) != ERR_OK) {
            APP_LOGE("Call GetInnerBundleInfoByUid failed");
            return ERR_BUNDLE_MANAGER_INVALID_UID;
        }
    }
    return ERR_OK;
}

bool BundleDataMgr::GetBundleGids(const std::string &bundleName, std::vector<int> &gids) const
{
    int32_t requestUserId = GetUserId();
    InnerBundleUserInfo innerBundleUserInfo;
    if (!GetInnerBundleUserInfoByUserId(bundleName, requestUserId, innerBundleUserInfo)) {
        APP_LOGW("the user(%{public}d) is not exists in bundleName(%{public}s) ",
            requestUserId, bundleName.c_str());
        return false;
    }

    gids = innerBundleUserInfo.gids;
    return true;
}

bool BundleDataMgr::GetBundleGidsByUid(
    const std::string &bundleName, const int &uid, std::vector<int> &gids) const
{
    return true;
}

bool BundleDataMgr::QueryKeepAliveBundleInfos(std::vector<BundleInfo> &bundleInfos) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return false;
    }

    int32_t requestUserId = GetUserId();
    for (const auto &info : bundleInfos_) {
        if (info.second.IsDisabled()) {
            APP_LOGD("app %{public}s is disabled", info.second.GetBundleName().c_str());
            continue;
        }
        if (info.second.GetIsKeepAlive()) {
            BundleInfo bundleInfo;
            int32_t responseUserId = info.second.GetResponseUserId(requestUserId);
            info.second.GetBundleInfo(BundleFlag::GET_BUNDLE_WITH_ABILITIES, bundleInfo, responseUserId);
            if (bundleInfo.name == "") {
                continue;
            }
            bundleInfos.emplace_back(bundleInfo);
        }
    }
    return !(bundleInfos.empty());
}

ErrCode BundleDataMgr::GetAbilityLabel(const std::string &bundleName, const std::string &moduleName,
    const std::string &abilityName, std::string &label) const
{
#ifdef GLOBAL_RESMGR_ENABLE
    AbilityInfo abilityInfo;
    int32_t responseUserId = -1;
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        int32_t requestUserId = GetUserId();
        if (requestUserId == Constants::INVALID_USERID) {
            return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
        }
        const InnerBundleInfo *innerBundleInfo = nullptr;
        ErrCode ret =
            GetInnerBundleInfoWithFlagsV9(bundleName, BundleFlag::GET_BUNDLE_DEFAULT, innerBundleInfo, requestUserId);
        if (ret != ERR_OK) {
            return ret;
        }
        if (!innerBundleInfo) {
            LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetAbilityLabel is null.");
            return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
        }
        
        ret = FindAbilityInfoInBundleInfo(*innerBundleInfo, moduleName, abilityName, abilityInfo);
        if (ret != ERR_OK) {
            APP_LOGE("Find ability failed. bundleName: %{public}s, moduleName: %{public}s, abilityName: %{public}s",
                bundleName.c_str(), moduleName.c_str(), abilityName.c_str());
            return ret;
        }
        responseUserId = innerBundleInfo->GetResponseUserId(requestUserId);
        bool isEnable = false;
        ret = innerBundleInfo->IsAbilityEnabledV9(abilityInfo, responseUserId, isEnable);
        if (ret != ERR_OK) {
            return ret;
        }
        if (!isEnable) {
            APP_LOGW("%{public}s ability disabled: %{public}s", bundleName.c_str(), abilityName.c_str());
            return ERR_BUNDLE_MANAGER_ABILITY_DISABLED;
        }
    }
    if (abilityInfo.labelId == 0) {
        label = abilityInfo.label;
        return ERR_OK;
    }
    std::shared_ptr<OHOS::Global::Resource::ResourceManager> resourceManager =
        GetResourceManager(bundleName, abilityInfo.moduleName, responseUserId);
    if (resourceManager == nullptr) {
        APP_LOGW("InitResourceManager failed");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    auto state = resourceManager->GetStringById(static_cast<uint32_t>(abilityInfo.labelId), label);
    if (state != OHOS::Global::Resource::RState::SUCCESS) {
        APP_LOGW("ResourceManager GetStringById failed");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    return ERR_OK;
#else
    APP_LOGW("GLOBAL_RES_MGR_ENABLE is false");
    return ERR_BUNDLE_MANAGER_GLOBAL_RES_MGR_ENABLE_DISABLED;
#endif
}

bool BundleDataMgr::GetHapModuleInfo(
    const AbilityInfo &abilityInfo, HapModuleInfo &hapModuleInfo, int32_t userId) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return false;
    }

    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return false;
    }

    APP_LOGD("GetHapModuleInfo bundleName: %{public}s", abilityInfo.bundleName.c_str());
    auto infoItem = bundleInfos_.find(abilityInfo.bundleName);
    if (infoItem == bundleInfos_.end()) {
        return false;
    }

    const InnerBundleInfo &innerBundleInfo = infoItem->second;
    if (innerBundleInfo.IsDisabled()) {
        APP_LOGW("app %{public}s is disabled", innerBundleInfo.GetBundleName().c_str());
        return false;
    }

    int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
    auto module = innerBundleInfo.FindHapModuleInfo(abilityInfo.package, responseUserId);
    if (!module) {
        APP_LOGW("can not find module %{public}s, bundleName:%{public}s", abilityInfo.package.c_str(),
            abilityInfo.bundleName.c_str());
        return false;
    }
    hapModuleInfo = std::move(*module);
    return true;
}

ErrCode BundleDataMgr::GetLaunchWantForBundle(
    const std::string &bundleName, Want &want, int32_t userId) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo *innerBundleInfo = nullptr;
    ErrCode ret = GetInnerBundleInfoWithFlagsV9(
        bundleName, BundleFlag::GET_BUNDLE_DEFAULT, innerBundleInfo, userId);
    if (ret != ERR_OK) {
        APP_LOGD("GetInnerBundleInfoWithFlagsV9 failed, bundleName:%{public}s", bundleName.c_str());
        return ret;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetLaunchWantForBundle is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    std::string mainAbility = innerBundleInfo->GetMainAbility();
    if (mainAbility.empty()) {
        APP_LOGW("no main ability in the bundle %{public}s", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    want.SetElementName("", bundleName, mainAbility);
    want.SetAction(Constants::ACTION_HOME);
    want.AddEntity(Constants::ENTITY_HOME);
    return ERR_OK;
}

bool BundleDataMgr::CheckIsSystemAppByUid(const int uid) const
{
    // If the value of uid is 0 (ROOT_UID) or 1000 (BMS_UID),
    // the uid should be the system uid.
    if (uid == Constants::ROOT_UID || uid == ServiceConstants::BMS_UID) {
        return true;
    }
    int32_t appIndex = 0;
    std::string bundleName;
    ErrCode ret = GetBundleNameAndIndex(uid, bundleName, appIndex);
    if (ret != ERR_OK) {
        APP_LOGW("uid: %{public}d invalid!", uid);
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;
    if (GetInnerBundleInfoNoLock(bundleName, uid, appIndex, innerBundleInfo) != ERR_OK) {
        APP_LOGE("get innerBundleInfo by uid :%{public}d failed", uid);
        return false;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by CheckIsSystemAppByUid is null.");
        return false;
    }

    return innerBundleInfo->IsSystemApp();
}

void BundleDataMgr::InitStateTransferMap()
{
    transferStates_.emplace(InstallState::INSTALL_SUCCESS, InstallState::INSTALL_START);
    transferStates_.emplace(InstallState::INSTALL_FAIL, InstallState::INSTALL_START);
    transferStates_.emplace(InstallState::UNINSTALL_START, InstallState::INSTALL_SUCCESS);
    transferStates_.emplace(InstallState::UNINSTALL_START, InstallState::INSTALL_START);
    transferStates_.emplace(InstallState::UNINSTALL_START, InstallState::UPDATING_SUCCESS);
    transferStates_.emplace(InstallState::UNINSTALL_FAIL, InstallState::UNINSTALL_START);
    transferStates_.emplace(InstallState::UNINSTALL_START, InstallState::UNINSTALL_START);
    transferStates_.emplace(InstallState::UNINSTALL_SUCCESS, InstallState::UNINSTALL_START);
    transferStates_.emplace(InstallState::UPDATING_START, InstallState::INSTALL_SUCCESS);
    transferStates_.emplace(InstallState::UPDATING_SUCCESS, InstallState::UPDATING_START);
    transferStates_.emplace(InstallState::UPDATING_FAIL, InstallState::UPDATING_START);
    transferStates_.emplace(InstallState::UPDATING_FAIL, InstallState::INSTALL_START);
    transferStates_.emplace(InstallState::UPDATING_START, InstallState::INSTALL_START);
    transferStates_.emplace(InstallState::INSTALL_SUCCESS, InstallState::UPDATING_START);
    transferStates_.emplace(InstallState::INSTALL_SUCCESS, InstallState::UPDATING_SUCCESS);
    transferStates_.emplace(InstallState::INSTALL_SUCCESS, InstallState::UNINSTALL_START);
    transferStates_.emplace(InstallState::UPDATING_START, InstallState::UPDATING_SUCCESS);
    transferStates_.emplace(InstallState::ROLL_BACK, InstallState::UPDATING_START);
    transferStates_.emplace(InstallState::ROLL_BACK, InstallState::UPDATING_SUCCESS);
    transferStates_.emplace(InstallState::UPDATING_FAIL, InstallState::UPDATING_SUCCESS);
    transferStates_.emplace(InstallState::INSTALL_SUCCESS, InstallState::ROLL_BACK);
    transferStates_.emplace(InstallState::UNINSTALL_START, InstallState::USER_CHANGE);
    transferStates_.emplace(InstallState::UPDATING_START, InstallState::USER_CHANGE);
    transferStates_.emplace(InstallState::INSTALL_SUCCESS, InstallState::USER_CHANGE);
    transferStates_.emplace(InstallState::UPDATING_SUCCESS, InstallState::USER_CHANGE);
    transferStates_.emplace(InstallState::USER_CHANGE, InstallState::INSTALL_SUCCESS);
    transferStates_.emplace(InstallState::USER_CHANGE, InstallState::UPDATING_SUCCESS);
    transferStates_.emplace(InstallState::USER_CHANGE, InstallState::UPDATING_START);
}

bool BundleDataMgr::IsDeleteDataState(const InstallState state) const
{
    return (state == InstallState::INSTALL_FAIL || state == InstallState::UNINSTALL_FAIL ||
            state == InstallState::UNINSTALL_SUCCESS || state == InstallState::UPDATING_FAIL);
}

bool BundleDataMgr::IsDisableState(const InstallState state) const
{
    if (state == InstallState::UPDATING_START || state == InstallState::UNINSTALL_START) {
        return true;
    }
    return false;
}

void BundleDataMgr::DeleteBundleInfo(const std::string &bundleName, const InstallState state, const bool isKeepData)
{
    if (InstallState::INSTALL_FAIL == state) {
        APP_LOGW("del fail, bundle:%{public}s has no installed info", bundleName.c_str());
        return;
    }

    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("create infoItem fail, bundleName:%{public}s", bundleName.c_str());
        return;
    }
#ifdef BUNDLE_FRAMEWORK_OVERLAY_INSTALLATION
    // remove external overlay bundle info and connection
    RemoveOverlayInfoAndConnection(infoItem->second, bundleName);
#endif
    APP_LOGI("del bundle name:%{public}s", bundleName.c_str());
    const InnerBundleInfo &innerBundleInfo = infoItem->second;
    if (!isKeepData) {
        RecycleUidAndGid(innerBundleInfo);
    }
    bool ret = dataStorage_->DeleteStorageBundleInfo(innerBundleInfo);
    if (!ret) {
        APP_LOGW("delete storage error name:%{public}s", bundleName.c_str());
    }
    bundleInfos_.erase(bundleName);
    if (DeleteShortcutEnabledInfo(bundleName) != ERR_OK) {
        APP_LOGW("DeleteShortcutEnabledInfo failed, bundleName: %{public}s", bundleName.c_str());
    }
    std::lock_guard<std::mutex> hspLock(hspBundleNameMutex_);
    if (appServiceHspBundleName_.find(bundleName) != appServiceHspBundleName_.end()) {
        appServiceHspBundleName_.erase(bundleName);
    }
    if (!isKeepData) {
        ErrCode deleteDesktopRes = DeleteDesktopShortcutInfo(bundleName);
        EventReport::SendDesktopShortcutEvent(DesktopShortcutOperation::DELETE, Constants::ALL_USERID, bundleName,
            0, Constants::EMPTY_STRING, IPCSkeleton::GetCallingUid(), deleteDesktopRes);
    }
}

bool BundleDataMgr::GetInnerBundleInfoWithFlags(const std::string &bundleName,
    const int32_t flags, int32_t userId, int32_t appIndex) const
{
    if (bundleName.empty()) {
        return false;
    }
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return false;
    }

    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return false;
    }
    APP_LOGD("GetInnerBundleInfoWithFlags: %{public}s", bundleName.c_str());
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        LOG_NOFUNC_W(BMS_TAG_COMMON, "bundle not exist -n %{public}s -u %{public}d -i %{public}d -f %{public}d",
            bundleName.c_str(), userId, appIndex, flags);
        return false;
    }
    const InnerBundleInfo &innerBundleInfo = item->second;
    if (innerBundleInfo.IsDisabled()) {
        LOG_NOFUNC_E(BMS_TAG_COMMON, "bundle disabled -n %{public}s -u %{public}d -i %{public}d -f %{public}d",
            bundleName.c_str(), userId, appIndex, flags);
        return false;
    }

    int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
    if (appIndex == 0) {
        if (!(static_cast<uint32_t>(flags) & GET_APPLICATION_INFO_WITH_DISABLE)
            && !innerBundleInfo.GetApplicationEnabled(responseUserId)) {
            LOG_NOFUNC_W(BMS_TAG_COMMON, "not found -n %{public}s -u %{public}d"
                " -i %{public}d -f %{public}d", bundleName.c_str(), responseUserId, appIndex, flags);
            return false;
        }
    } else if (appIndex > 0 && appIndex <= Constants::INITIAL_SANDBOX_APP_INDEX) {
        bool isEnabled = false;
        ErrCode ret = innerBundleInfo.GetApplicationEnabledV9(responseUserId, isEnabled, appIndex);
        if (ret != ERR_OK) {
            LOG_NOFUNC_W(BMS_TAG_COMMON, "bundle %{public}s not install in user %{public}d -i %{public}d",
                bundleName.c_str(), responseUserId, appIndex);
            return false;
        }
        if (!(static_cast<uint32_t>(flags) & GET_APPLICATION_INFO_WITH_DISABLE) && !isEnabled) {
            LOG_NOFUNC_W(BMS_TAG_COMMON, "set enabled false -n %{public}s -u %{public}d -i %{public}d -f %{public}d",
                bundleName.c_str(), responseUserId, appIndex, flags);
            return false;
        }
    } else {
        return false;
    }
    return true;
}

bool BundleDataMgr::GetInnerBundleInfoWithFlags(const std::string &bundleName,
    const int32_t flags, const InnerBundleInfo *&info, int32_t userId, int32_t appIndex) const
{
    bool res = GetInnerBundleInfoWithFlags(bundleName, flags, userId, appIndex);
    if (!res) {
        return false;
    }
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGW_NOFUNC("%{public}s not find", bundleName.c_str());
        return false;
    }
    info = &(item->second);
    return true;
}

bool BundleDataMgr::GetInnerBundleInfoWithBundleFlagsAndLock(const std::string &bundleName,
    const int32_t flags, InnerBundleInfo& info, int32_t userId) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;
    bool res = GetInnerBundleInfoWithFlags(bundleName, flags, innerBundleInfo, userId);
    if (!res) {
        APP_LOGD("GetInnerBundleInfoWithBundleFlagsAndLock: bundleName %{public}s not find", bundleName.c_str());
        return res;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetInnerBundleInfoWithBundleFlagsAndLock is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    info = *innerBundleInfo;
    return true;
}

ErrCode BundleDataMgr::GetInnerBundleInfoWithFlagsV9(const std::string &bundleName,
    const int32_t flags, const InnerBundleInfo *&info, int32_t userId, int32_t appIndex) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    if (bundleInfos_.empty()) {
        APP_LOGD("bundleInfos_ data is empty");
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    APP_LOGD(
        "GetInnerBundleInfoWithFlagsV9:bundleName:%{public}s,flags:%{public}d,userId:%{public}d,appIndex:%{public}d",
        bundleName.c_str(), flags, userId, appIndex);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        LOG_NOFUNC_W(BMS_TAG_COMMON, "bundle not exist -n %{public}s -u %{public}d -i %{public}d -f %{public}d",
            bundleName.c_str(), userId, appIndex, flags);
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    const InnerBundleInfo &innerBundleInfo = item->second;
    if (innerBundleInfo.IsDisabled()) {
        LOG_NOFUNC_E(BMS_TAG_COMMON, "bundle disabled -n %{public}s -u %{public}d -i %{public}d -f %{public}d",
            bundleName.c_str(), userId, appIndex, flags);
        return ERR_BUNDLE_MANAGER_BUNDLE_DISABLED;
    }

    int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
    bool isEnabled = false;
    auto ret = innerBundleInfo.GetApplicationEnabledV9(responseUserId, isEnabled, appIndex);
    if (ret != ERR_OK) {
        LOG_NOFUNC_W(BMS_TAG_COMMON, "bundle %{public}s not install in user %{public}d -i %{public}d",
            bundleName.c_str(), responseUserId, appIndex);
        return ret;
    }
    if (!(static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_DISABLE))
        && !isEnabled) {
        LOG_NOFUNC_W(BMS_TAG_COMMON, "set enabled false -n %{public}s -u %{public}d -i %{public}d -f %{public}d",
            bundleName.c_str(), responseUserId, appIndex, flags);
        return ERR_BUNDLE_MANAGER_APPLICATION_DISABLED;
    }
    info = &innerBundleInfo;
    return ERR_OK;
}

ErrCode BundleDataMgr::GetInnerBundleInfoWithBundleFlagsV9(const std::string &bundleName,
    const int32_t flags, const InnerBundleInfo *&info, int32_t userId, int32_t appIndex) const
{
    if (bundleName.empty()) {
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty, bundleName: %{public}s", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    APP_LOGD("GetInnerBundleInfoWithFlagsV9: %{public}s", bundleName.c_str());
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGW_NOFUNC("%{public}s not find", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    const InnerBundleInfo &innerBundleInfo = item->second;
    if (innerBundleInfo.IsDisabled()) {
        APP_LOGW("bundleName: %{public}s status is disabled", innerBundleInfo.GetBundleName().c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_DISABLED;
    }

    int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
    bool isEnabled = false;
    auto ret = innerBundleInfo.GetApplicationEnabledV9(responseUserId, isEnabled, appIndex);
    if (ret != ERR_OK) {
        return ret;
    }
    if (!(static_cast<uint32_t>(flags) & static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_DISABLE))
        && !isEnabled) {
        APP_LOGW("bundleName: %{public}s is disabled", innerBundleInfo.GetBundleName().c_str());
        return ERR_BUNDLE_MANAGER_APPLICATION_DISABLED;
    }
    info = &innerBundleInfo;
    return ERR_OK;
}

bool BundleDataMgr::DisableBundle(const std::string &bundleName)
{
    APP_LOGD("DisableBundle %{public}s", bundleName.c_str());
    if (bundleName.empty()) {
        APP_LOGW("bundleName empty");
        return false;
    }

    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return false;
    }
    infoItem->second.SetBundleStatus(InnerBundleInfo::BundleStatus::DISABLED);
    return true;
}

bool BundleDataMgr::EnableBundle(const std::string &bundleName)
{
    APP_LOGD("EnableBundle %{public}s", bundleName.c_str());
    if (bundleName.empty()) {
        APP_LOGW("bundleName empty");
        return false;
    }

    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return false;
    }
    infoItem->second.SetBundleStatus(InnerBundleInfo::BundleStatus::ENABLED);
    return true;
}

ErrCode BundleDataMgr::IsDebuggableApplication(const std::string &bundleName, bool &isDebuggable) const
{
    APP_LOGD("IsDebuggableApplication %{public}s", bundleName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        return ERR_OK;
    }

    const InnerBundleInfo &bundleInfo = infoItem->second;
    isDebuggable = bundleInfo.GetBaseApplicationInfo().appProvisionType == Constants::APP_PROVISION_TYPE_DEBUG;

    return ERR_OK;
}

ErrCode BundleDataMgr::IsApplicationEnabled(
    const std::string &bundleName, int32_t appIndex, bool &isEnabled, int32_t userId) const
{
    APP_LOGD("IsApplicationEnabled %{public}s", bundleName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    int32_t responseUserId = infoItem->second.GetResponseUserId(GetUserId(userId));
    if (appIndex == 0) {
        ErrCode ret = infoItem->second.GetApplicationEnabledV9(responseUserId, isEnabled);
        if (ret != ERR_OK) {
            APP_LOGW("GetApplicationEnabled failed: %{public}s", bundleName.c_str());
        }
        return ret;
    }
    const InnerBundleInfo &bundleInfo = infoItem->second;
    const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
    if (!bundleInfo.GetInnerBundleUserInfo(responseUserId, innerBundleUserInfoPtr)) {
        APP_LOGW("can not find userId %{public}d", responseUserId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    if (!innerBundleUserInfoPtr) {
        LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by IsApplicationEnabled is null");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    auto iter = innerBundleUserInfoPtr->cloneInfos.find(std::to_string(appIndex));
    if (iter == innerBundleUserInfoPtr->cloneInfos.end()) {
        APP_LOGW("can not find appIndex %{public}d", appIndex);
        return ERR_APPEXECFWK_SANDBOX_INSTALL_INVALID_APP_INDEX;
    }
    isEnabled = iter->second.enabled;
    return ERR_OK;
}

ErrCode BundleDataMgr::SetApplicationEnabled(const std::string &bundleName,
    int32_t appIndex, bool isEnable, const std::string &caller, int32_t userId, bool &stateChanged)
{
    APP_LOGD("SetApplicationEnabled %{public}s", bundleName.c_str());
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    stateChanged = false;
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGW("Request userId %{public}d is invalid, bundleName:%{public}s", userId, bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }

    InnerBundleInfo& newInfo = infoItem->second;

    bool currentEnabled = false;
    (void)newInfo.GetApplicationEnabledV9(requestUserId, currentEnabled, appIndex);
    stateChanged = (currentEnabled != isEnable);

    if (appIndex != 0) {
        auto ret = newInfo.SetCloneApplicationEnabled(isEnable, appIndex, caller, requestUserId);
        if (ret != ERR_OK) {
            APP_LOGW("SetCloneApplicationEnabled for innerBundleInfo fail, errCode is %{public}d", ret);
            return ret;
        }
        if (!dataStorage_->SaveStorageBundleInfo(newInfo)) {
            APP_LOGE("SaveStorageBundleInfo failed for bundle %{public}s", newInfo.GetBundleName().c_str());
            return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
        }
        return ERR_OK;
    }

    auto ret = newInfo.SetApplicationEnabled(isEnable, caller, requestUserId);
    if (ret != ERR_OK) {
        APP_LOGW("SetApplicationEnabled failed, err %{public}d", ret);
        return ret;
    }

    const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
    if (!newInfo.GetInnerBundleUserInfo(requestUserId, innerBundleUserInfoPtr)) {
        APP_LOGW("can not find bundleUserInfo in userId: %{public}d", requestUserId);
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    if (!innerBundleUserInfoPtr) {
        LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by SetApplicationEnabled is null");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    if (innerBundleUserInfoPtr->bundleUserInfo.IsInitialState()) {
        bundleStateStorage_->DeleteBundleState(bundleName, requestUserId);
    } else {
        bundleStateStorage_->SaveBundleStateStorage(
            bundleName, requestUserId, innerBundleUserInfoPtr->bundleUserInfo);
    }
    return ERR_OK;
}

bool BundleDataMgr::SetModuleRemovable(const std::string &bundleName, const std::string &moduleName, 
    bool isEnable, const int32_t userId, const int32_t callingUid)
{
    if (bundleName.empty() || moduleName.empty()) {
        APP_LOGW("bundleName or moduleName is empty");
        return false;
    }
    if (userId == Constants::INVALID_USERID) {
        APP_LOGW("get a invalid userid, bundleName: %{public}s", bundleName.c_str());
        return false;
    }
    std::string callingBundleName;
    if (GetNameForUid(callingUid, callingBundleName) != ERR_OK) {
        callingBundleName = std::to_string(callingUid);
    }
    APP_LOGD("bundleName:%{public}s, moduleName:%{public}s, userId:%{public}d",
        bundleName.c_str(), moduleName.c_str(), userId);
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return false;
    }
    InnerBundleInfo newInfo = infoItem->second;
    newInfo.SetModuleRemovableSet(moduleName, isEnable, userId, callingBundleName);
    bool ret = newInfo.SetModuleRemovable(moduleName, isEnable, userId);
    if (ret && dataStorage_->SaveStorageBundleInfo(newInfo)) {
        ret = infoItem->second.SetModuleRemovable(moduleName, isEnable, userId);
        infoItem->second.SetModuleRemovableSet(moduleName, isEnable, userId, callingBundleName);
#ifdef BUNDLE_FRAMEWORK_FREE_INSTALL
        if (isEnable) {
            // call clean task
            APP_LOGD("bundle:%{public}s isEnable:%{public}d ret:%{public}d call clean task",
                bundleName.c_str(), isEnable, ret);
            DelayedSingleton<BundleMgrService>::GetInstance()->GetAgingMgr()->Start(
                BundleAgingMgr::AgingTriggertype::UPDATE_REMOVABLE_FLAG);
        }
#endif
        return ret;
    } else {
        APP_LOGW("bundle:%{public}s SetModuleRemoved failed", bundleName.c_str());
        return false;
    }
}

ErrCode BundleDataMgr::IsModuleRemovable(const std::string &bundleName, const std::string &moduleName,
    bool &isRemovable, int32_t userId) const
{
    if (bundleName.empty() || moduleName.empty()) {
        APP_LOGW("bundleName or moduleName is empty");
        return ERR_BUNDLE_MANAGER_PARAM_ERROR;
    }
    if (userId == Constants::INVALID_USERID) {
        APP_LOGW("get a invalid userid, bundleName: %{public}s", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_PARAM_ERROR;
    }
    APP_LOGD("bundleName:%{public}s, moduleName:%{public}s, userId:%{public}d",
        bundleName.c_str(), moduleName.c_str(), userId);
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    InnerBundleInfo newInfo = infoItem->second;
    if (!newInfo.HasInnerBundleUserInfo(userId)) {
        APP_LOGW("%{public}d can not find bundle %{public}s", userId, bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    return newInfo.IsModuleRemovable(moduleName, userId, isRemovable);
}

ErrCode BundleDataMgr::IsAbilityEnabled(const AbilityInfo &abilityInfo, int32_t appIndex, bool &isEnable) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(abilityInfo.bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", abilityInfo.bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    std::vector<int32_t> appIndexVec = GetCloneAppIndexesNoLock(abilityInfo.bundleName, Constants::ALL_USERID);
    if ((appIndex != 0) && (std::find(appIndexVec.begin(), appIndexVec.end(), appIndex) == appIndexVec.end())) {
        APP_LOGE("appIndex %{public}d is invalid", appIndex);
        return ERR_APPEXECFWK_SANDBOX_INSTALL_INVALID_APP_INDEX;
    }
    InnerBundleInfo innerBundleInfo = infoItem->second;
    auto ability = innerBundleInfo.FindAbilityInfoV9(
        abilityInfo.moduleName, abilityInfo.name);
    if (!ability) {
        APP_LOGW("ability not found, bundleName:%{public}s, moduleName:%{public}s, abilityName:%{public}s",
            abilityInfo.bundleName.c_str(), abilityInfo.moduleName.c_str(), abilityInfo.name.c_str());
        return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
    }
    int32_t responseUserId = innerBundleInfo.GetResponseUserId(GetUserId());
    return innerBundleInfo.IsAbilityEnabledV9((*ability), responseUserId, isEnable, appIndex);
}

ErrCode BundleDataMgr::SetAbilityEnabled(const AbilityInfo &abilityInfo, int32_t appIndex,
    bool isEnabled, int32_t userId, bool &stateChanged)
{
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    stateChanged = false;
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGW("Request userId is invalid, bundleName:%{public}s, abilityName:%{public}s",
            abilityInfo.bundleName.c_str(), abilityInfo.name.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    auto infoItem = bundleInfos_.find(abilityInfo.bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", abilityInfo.bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    InnerBundleInfo& newInfo = infoItem->second;

    bool currentEnabled = false;
    (void)newInfo.IsAbilityEnabledV9(abilityInfo, requestUserId, currentEnabled, appIndex);
    stateChanged = (currentEnabled != isEnabled);

    if (appIndex != 0) {
        ErrCode ret = newInfo.SetCloneAbilityEnabled(
            abilityInfo.moduleName, abilityInfo.name, isEnabled, userId, appIndex);
        if (ret != ERR_OK) {
            APP_LOGW("SetCloneAbilityEnabled failed result: %{public}d, bundleName:%{public}s, abilityName:%{public}s",
                ret, abilityInfo.bundleName.c_str(), abilityInfo.name.c_str());
            return ret;
        }
        if (!dataStorage_->SaveStorageBundleInfo(newInfo)) {
            APP_LOGE("SaveStorageBundleInfo bundle %{public}s failed", newInfo.GetBundleName().c_str());
            return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
        }
        return ERR_OK;
    }
    auto ret = newInfo.SetAbilityEnabled(
        abilityInfo.moduleName, abilityInfo.name, isEnabled, userId);
    if (ret != ERR_OK) {
        APP_LOGW("SetAbilityEnabled failed result: %{public}d, bundleName:%{public}s, abilityName:%{public}s",
            ret, abilityInfo.bundleName.c_str(), abilityInfo.name.c_str());
        return ret;
    }
    InnerBundleUserInfo innerBundleUserInfo;
    const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
    if (!newInfo.GetInnerBundleUserInfo(requestUserId, innerBundleUserInfoPtr)) {
        APP_LOGW("can not find bundleUserInfo in userId: %{public}d, bundleName:%{public}s, abilityName:%{public}s",
            requestUserId, abilityInfo.bundleName.c_str(), abilityInfo.name.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    if (!innerBundleUserInfoPtr) {
        LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by SetAbilityEnabled is null");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    if (innerBundleUserInfoPtr->bundleUserInfo.IsInitialState()) {
        bundleStateStorage_->DeleteBundleState(abilityInfo.bundleName, requestUserId);
    } else {
        bundleStateStorage_->SaveBundleStateStorage(
            abilityInfo.bundleName, requestUserId, innerBundleUserInfoPtr->bundleUserInfo);
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::SetAbilityFileTypes(const std::string &bundleName, const std::string &moduleName,
    const std::string &abilityName, const std::vector<std::string> &fileTypes)
{
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("-n %{public}s not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    ErrCode ret = item->second.SetAbilityFileTypes(moduleName, abilityName, fileTypes);
    if (ret != ERR_OK) {
        APP_LOGE("SetAbilityFileTypes failed:%{public}d", ret);
        return ret;
    }
    if (!dataStorage_->SaveStorageBundleInfo(item->second)) {
        APP_LOGE("SaveStorageBundleInfo failed");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    return ERR_OK;
}

std::shared_ptr<BundleSandboxAppHelper> BundleDataMgr::GetSandboxAppHelper() const
{
    return sandboxAppHelper_;
}

bool BundleDataMgr::RegisterBundleStatusCallback(const sptr<IBundleStatusCallback> &bundleStatusCallback)
{
    APP_LOGD("RegisterBundleStatusCallback %{public}s", bundleStatusCallback->GetBundleName().c_str());
    std::unique_lock<std::shared_mutex> lock(callbackMutex_);
    callbackList_.emplace_back(bundleStatusCallback);
    if (bundleStatusCallback->AsObject() != nullptr) {
        sptr<BundleStatusCallbackDeathRecipient> deathRecipient =
            new (std::nothrow) BundleStatusCallbackDeathRecipient();
        if (deathRecipient == nullptr) {
            APP_LOGW("deathRecipient is null");
            return false;
        }
        bundleStatusCallback->AsObject()->AddDeathRecipient(deathRecipient);
    }
    return true;
}

bool BundleDataMgr::RegisterBundleEventCallback(const sptr<IBundleEventCallback> &bundleEventCallback)
{
    if (bundleEventCallback == nullptr) {
        APP_LOGW("bundleEventCallback is null");
        return false;
    }
    std::lock_guard lock(eventCallbackMutex_);
    if (eventCallbackList_.size() >= MAX_EVENT_CALL_BACK_SIZE) {
        APP_LOGW("eventCallbackList_ reach max size %{public}d", MAX_EVENT_CALL_BACK_SIZE);
        return false;
    }
    if (bundleEventCallback->AsObject() != nullptr) {
        sptr<BundleEventCallbackDeathRecipient> deathRecipient =
            new (std::nothrow) BundleEventCallbackDeathRecipient();
        if (deathRecipient == nullptr) {
            APP_LOGW("deathRecipient is null");
            return false;
        }
        bundleEventCallback->AsObject()->AddDeathRecipient(deathRecipient);
    }
    eventCallbackList_.emplace_back(bundleEventCallback);
    return true;
}

bool BundleDataMgr::UnregisterBundleEventCallback(const sptr<IBundleEventCallback> &bundleEventCallback)
{
    APP_LOGD("begin to UnregisterBundleEventCallback");
    if (bundleEventCallback == nullptr) {
        APP_LOGW("bundleEventCallback is null");
        return false;
    }
    std::lock_guard lock(eventCallbackMutex_);
    eventCallbackList_.erase(std::remove_if(eventCallbackList_.begin(), eventCallbackList_.end(),
        [&bundleEventCallback](const sptr<IBundleEventCallback> &callback) {
            return callback->AsObject() == bundleEventCallback->AsObject();
        }), eventCallbackList_.end());
    return true;
}

void BundleDataMgr::NotifyBundleEventCallback(const EventFwk::CommonEventData &eventData) const
{
    APP_LOGD("begin to NotifyBundleEventCallback");
    std::vector<sptr<IBundleEventCallback>> eventCallbackListCopy;
    {
        std::lock_guard lock(eventCallbackMutex_);
        eventCallbackListCopy = eventCallbackList_;
    }
    for (const auto &callback : eventCallbackListCopy) {
        if (callback == nullptr) {
            continue;
        }
        callback->OnReceiveEvent(eventData);
    }
    APP_LOGD("finish to NotifyBundleEventCallback");
}

bool BundleDataMgr::ClearBundleStatusCallback(const sptr<IBundleStatusCallback> &bundleStatusCallback)
{
    APP_LOGD("ClearBundleStatusCallback %{public}s", bundleStatusCallback->GetBundleName().c_str());
    std::unique_lock<std::shared_mutex> lock(callbackMutex_);
    callbackList_.erase(std::remove_if(callbackList_.begin(),
        callbackList_.end(),
        [&](const sptr<IBundleStatusCallback> &callback) {
            return callback->AsObject() == bundleStatusCallback->AsObject();
        }),
        callbackList_.end());
    return true;
}

bool BundleDataMgr::UnregisterBundleStatusCallback()
{
    std::unique_lock<std::shared_mutex> lock(callbackMutex_);
    callbackList_.clear();
    return true;
}

ErrCode BundleDataMgr::GenerateUidAndGid(InnerBundleUserInfo &innerBundleUserInfo)
{
    if (innerBundleUserInfo.bundleName.empty()) {
        APP_LOGW("bundleName is null");
        return ERR_APPEXECFWK_INSTALL_BUNDLENAME_IS_EMPTY;
    }

    int32_t bundleId = INVALID_BUNDLEID;
    ErrCode ret = GenerateBundleId(innerBundleUserInfo.bundleName, bundleId);
    if (ret != ERR_OK) {
        APP_LOGW("Generate bundleId failed, bundleName: %{public}s", innerBundleUserInfo.bundleName.c_str());
        return ERR_APPEXECFWK_INSTALL_BUNDLEID_EXCEED_MAX_NUMBER;
    }

    innerBundleUserInfo.uid = innerBundleUserInfo.bundleUserInfo.userId * Constants::BASE_USER_RANGE
        + bundleId % Constants::BASE_USER_RANGE;
    innerBundleUserInfo.gids.emplace_back(innerBundleUserInfo.uid);
    return ERR_OK;
}

ErrCode BundleDataMgr::GenerateBundleId(const std::string &bundleName, int32_t &bundleId)
{
    std::unique_lock<std::shared_mutex> lock(bundleIdMapMutex_);
    if (bundleIdMap_.empty()) {
        APP_LOGD("first app install");
        bundleId = baseAppUid_;
        bundleIdMap_.emplace(bundleId, bundleName);
        return ERR_OK;
    }

    for (const auto &innerBundleId : bundleIdMap_) {
        if (innerBundleId.second == bundleName) {
            bundleId = innerBundleId.first;
            return ERR_OK;
        }
    }
    if (bundleIdMap_.rbegin()->first == MAX_APP_UID) {
        for (int32_t i = baseAppUid_; i < bundleIdMap_.rbegin()->first; ++i) {
            if (bundleIdMap_.find(i) == bundleIdMap_.end()) {
                APP_LOGD("the %{public}d app install bundleName:%{public}s", i, bundleName.c_str());
                bundleId = i;
                bundleIdMap_.emplace(bundleId, bundleName);
                BundleUtil::MakeFsConfig(bundleName, bundleId, ServiceConstants::HMDFS_CONFIG_PATH);
                BundleUtil::MakeFsConfig(bundleName, bundleId, ServiceConstants::SHAREFS_CONFIG_PATH);
                return ERR_OK;
            }
        }
        APP_LOGW("the bundleId exceeding the maximum value, bundleName:%{public}s", bundleName.c_str());
        return ERR_APPEXECFWK_INSTALL_BUNDLEID_EXCEED_MAX_NUMBER;
    }

    bundleId = bundleIdMap_.rbegin()->first + 1;
    bundleIdMap_.emplace(bundleId, bundleName);
    BundleUtil::MakeFsConfig(bundleName, bundleId, ServiceConstants::HMDFS_CONFIG_PATH);
    BundleUtil::MakeFsConfig(bundleName, bundleId, ServiceConstants::SHAREFS_CONFIG_PATH);
    return ERR_OK;
}

ErrCode BundleDataMgr::SetModuleUpgradeFlag(const std::string &bundleName,
    const std::string &moduleName, const int32_t upgradeFlag)
{
    APP_LOGD("SetModuleUpgradeFlag %{public}d", upgradeFlag);
    if (bundleName.empty() || moduleName.empty()) {
        APP_LOGW("bundleName or moduleName is empty");
        return ERR_BUNDLE_MANAGER_PARAM_ERROR;
    }
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    InnerBundleInfo &newInfo = infoItem->second;
    ErrCode setFlag = newInfo.SetModuleUpgradeFlag(moduleName, upgradeFlag);
    if (setFlag == ERR_OK) {
        if (dataStorage_->SaveStorageBundleInfo(newInfo)) {
            return ERR_OK;
        }
        return ERR_APPEXECFWK_SERVICE_INTERNAL_ERROR;
    }
    APP_LOGW("dataStorage SetModuleUpgradeFlag %{public}s failed", bundleName.c_str());
    return setFlag;
}

int32_t BundleDataMgr::GetModuleUpgradeFlag(const std::string &bundleName, const std::string &moduleName) const
{
    APP_LOGD("bundleName is bundleName:%{public}s, moduleName:%{public}s", bundleName.c_str(), moduleName.c_str());
    if (bundleName.empty() || moduleName.empty()) {
        APP_LOGW("bundleName or moduleName is empty");
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return false;
    }
    InnerBundleInfo newInfo = infoItem->second;
    return newInfo.GetModuleUpgradeFlag(moduleName);
}

void BundleDataMgr::RecycleUidAndGid(const InnerBundleInfo &info)
{
    auto userInfos = info.GetInnerBundleUserInfos();
    if (userInfos.empty()) {
        APP_LOGE("user infos is empty");
        return;
    }

    auto innerBundleUserInfo = userInfos.begin()->second;
    int32_t bundleId = innerBundleUserInfo.uid -
        innerBundleUserInfo.bundleUserInfo.userId * Constants::BASE_USER_RANGE;
    std::unique_lock<std::shared_mutex> lock(bundleIdMapMutex_);
    auto infoItem = bundleIdMap_.find(bundleId);
    if (infoItem == bundleIdMap_.end()) {
        return;
    }

    UninstallBundleInfo uninstallBundleInfo;
    if (GetUninstallBundleInfo(info.GetBundleName(), uninstallBundleInfo)) {
        return;
    }
    bundleIdMap_.erase(bundleId);
    BundleUtil::RemoveFsConfig(innerBundleUserInfo.bundleName, ServiceConstants::HMDFS_CONFIG_PATH);
    BundleUtil::RemoveFsConfig(innerBundleUserInfo.bundleName, ServiceConstants::SHAREFS_CONFIG_PATH);
}

bool BundleDataMgr::RestoreUidAndGid()
{
    for (const auto &info : bundleInfos_) {
        bool onlyInsertOne = false;
        for (auto infoItem : info.second.GetInnerBundleUserInfos()) {
            auto innerBundleUserInfo = infoItem.second;
            AddUserId(innerBundleUserInfo.bundleUserInfo.userId);
            if (!onlyInsertOne) {
                onlyInsertOne = true;
                int32_t bundleId = innerBundleUserInfo.uid -
                    innerBundleUserInfo.bundleUserInfo.userId * Constants::BASE_USER_RANGE;
                std::unique_lock<std::shared_mutex> lock(bundleIdMapMutex_);
                auto item = bundleIdMap_.find(bundleId);
                if (item == bundleIdMap_.end()) {
                    bundleIdMap_.emplace(bundleId, innerBundleUserInfo.bundleName);
                } else {
                    bundleIdMap_[bundleId] = innerBundleUserInfo.bundleName;
                }
                BundleUtil::MakeFsConfig(innerBundleUserInfo.bundleName, bundleId, ServiceConstants::HMDFS_CONFIG_PATH);
                BundleUtil::MakeFsConfig(innerBundleUserInfo.bundleName, bundleId,
                    ServiceConstants::SHAREFS_CONFIG_PATH);
                BundleUtil::MakeFsConfig(innerBundleUserInfo.bundleName, ServiceConstants::HMDFS_CONFIG_PATH,
                    info.second.GetAppProvisionType(), Constants::APP_PROVISION_TYPE_FILE_NAME);
            }
            // appClone
            std::string bundleName = info.second.GetBundleName();
            std::map<std::string, InnerBundleCloneInfo> &clones = innerBundleUserInfo.cloneInfos;
            for (auto iter = clones.begin(); iter != clones.end(); iter++) {
                auto &cloneInfo = iter->second;
                int32_t bundleId = cloneInfo.uid - cloneInfo.userId * Constants::BASE_USER_RANGE;
                std::string cloneBundleName =
                    BundleCloneCommonHelper::GetCloneBundleIdKey(bundleName, cloneInfo.appIndex);
                std::unique_lock<std::shared_mutex> lock(bundleIdMapMutex_);
                auto item = bundleIdMap_.find(bundleId);
                if (item == bundleIdMap_.end()) {
                    bundleIdMap_.emplace(bundleId, cloneBundleName);
                } else {
                    bundleIdMap_[bundleId] = cloneBundleName;
                }
                BundleUtil::MakeFsConfig(cloneBundleName, bundleId, ServiceConstants::HMDFS_CONFIG_PATH);
                BundleUtil::MakeFsConfig(cloneBundleName, bundleId,
                    ServiceConstants::SHAREFS_CONFIG_PATH);
                BundleUtil::MakeFsConfig(innerBundleUserInfo.bundleName, ServiceConstants::HMDFS_CONFIG_PATH,
                    info.second.GetAppProvisionType(), Constants::APP_PROVISION_TYPE_FILE_NAME);
            }
        }
    }
    RestoreUidAndGidFromUninstallInfo();
    return true;
}

void BundleDataMgr::RestoreSandboxUidAndGid(std::map<int32_t, std::string> &bundleIdMap)
{
    if (sandboxAppHelper_ != nullptr) {
        std::unique_lock<std::shared_mutex> lock(bundleIdMapMutex_);
        sandboxAppHelper_->RestoreSandboxUidAndGid(bundleIdMap);
    }
}

std::mutex &BundleDataMgr::GetBundleMutex(const std::string &bundleName)
{
    bundleMutex_.lock_shared();
    auto it = bundleMutexMap_.find(bundleName);
    if (it == bundleMutexMap_.end()) {
        bundleMutex_.unlock_shared();
        std::unique_lock lock {bundleMutex_};
        return bundleMutexMap_[bundleName];
    }
    bundleMutex_.unlock_shared();
    return it->second;
}

bool BundleDataMgr::GetProvisionId(const std::string &bundleName, std::string &provisionId) const
{
    APP_LOGD("GetProvisionId %{public}s", bundleName.c_str());
    if (bundleName.empty()) {
        APP_LOGW("bundleName empty");
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return false;
    }
    provisionId = infoItem->second.GetProvisionId();
    return true;
}

bool BundleDataMgr::GetAppFeature(const std::string &bundleName, std::string &appFeature) const
{
    APP_LOGD("GetAppFeature %{public}s", bundleName.c_str());
    if (bundleName.empty()) {
        APP_LOGW("bundleName empty");
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return false;
    }
    appFeature = infoItem->second.GetAppFeature();
    return true;
}

void BundleDataMgr::SetInitialUserFlag(bool flag)
{
    APP_LOGD("SetInitialUserFlag %{public}d", flag);
    if (!initialUserFlag_ && flag && bundlePromise_ != nullptr) {
        bundlePromise_->NotifyAllTasksExecuteFinished();
    }

    initialUserFlag_ = flag;
}

std::shared_ptr<IBundleDataStorage> BundleDataMgr::GetDataStorage() const
{
    return dataStorage_;
}

bool BundleDataMgr::GetAllFormsInfo(std::vector<FormInfo> &formInfos) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return false;
    }
    auto result = false;
    for (const auto &item : bundleInfos_) {
        if (item.second.IsDisabled()) {
            APP_LOGD("app %{public}s is disabled", item.second.GetBundleName().c_str());
            continue;
        }
        item.second.GetFormsInfoByApp(formInfos);
        result = true;
    }
    APP_LOGD("all the form infos find success");
    return result;
}

bool BundleDataMgr::GetFormsInfoByModule(
    const std::string &bundleName, const std::string &moduleName, std::vector<FormInfo> &formInfos) const
{
    if (bundleName.empty()) {
        APP_LOGW("bundle name is empty");
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return false;
    }
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("bundleName %{public}s not exist", bundleName.c_str());
        return false;
    }
    if (infoItem->second.IsDisabled()) {
        APP_LOGW("app %{public}s is disabled", infoItem->second.GetBundleName().c_str());
        return false;
    }
    infoItem->second.GetFormsInfoByModule(moduleName, formInfos);
    if (formInfos.empty()) {
        return false;
    }
    APP_LOGD("module forminfo find success");
    return true;
}

bool BundleDataMgr::GetFormsInfoByApp(const std::string &bundleName, std::vector<FormInfo> &formInfos) const
{
    if (bundleName.empty()) {
        APP_LOGW("bundle name is empty");
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return false;
    }
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("bundleName %{public}s not exist", bundleName.c_str());
        return false;
    }
    if (infoItem->second.IsDisabled()) {
        APP_LOGW("app %{public}s is disabled", infoItem->second.GetBundleName().c_str());
        return false;
    }
    infoItem->second.GetFormsInfoByApp(formInfos);
    APP_LOGD("App forminfo find success");
    return true;
}

void BundleDataMgr::UpdateShortcutInfoResId(const std::string &bundleName, const int32_t userId)
{
#ifdef GLOBAL_RESMGR_ENABLE
    std::vector<ShortcutInfo> shortcutInfos;
    shortcutVisibleStorage_->GetStorageShortcutInfos(bundleName, Constants::ALL_CLONE_APP_INDEX,
        userId, shortcutInfos, true);
    if (shortcutInfos.empty()) {
        APP_LOGW("shortcutInfos is empty");
        return;
    }
    auto ProcessResourceField = [](const std::shared_ptr<OHOS::Global::Resource::ResourceManager> &resManager,
        const std::string &prefix, const std::string &field, uint32_t &resId) {
        if (resManager == nullptr) {
            APP_LOGW("resManager is nullptr");
            return;
        }
        if (field.empty() || resId == 0) {
            return;
        }
        std::string resType = PREFIX_DOLLAR + prefix + COLON_SEPARATOR;
        std::string resourceName;
        if (field.compare(0, resType.size(), resType) == 0) {
            resourceName = PREFIX_APP + prefix + DOT_SEPARATOR + field.substr(resType.size());
        } else {
            resourceName = PREFIX_APP + prefix + DOT_SEPARATOR + field;
        }
        if (resManager->GetResId(resourceName, resId) != Global::Resource::SUCCESS) {
            APP_LOGW("Get resId by name error, name:%{public}s", resourceName.c_str());
        }
    };
    std::unordered_map<std::string, std::shared_ptr<OHOS::Global::Resource::ResourceManager>> resManagers;
    for (auto &info : shortcutInfos) {
        auto [iter, inserted] = resManagers.emplace(info.moduleName, nullptr);
        if (inserted) {
            iter->second = GetResourceManager(info.bundleName, info.moduleName, userId);
            if (iter->second == nullptr) {
                APP_LOGW("InitResourceManager failed");
                resManagers.erase(iter);
                continue;
            }
        }
        ProcessResourceField(iter->second, FIELD_MEDIA, info.icon, info.iconId);
        ProcessResourceField(iter->second, FIELD_STRING, info.label, info.labelId);
    }
    if (!shortcutVisibleStorage_->AddDynamicShortcutInfos(shortcutInfos, userId)) {
        APP_LOGE("rdb AddDynamicShortcutInfos failed");
    }
#else
    APP_LOGW("GLOBAL_RESMGR_ENABLE disable");
#endif
}

bool BundleDataMgr::GetShortcutInfos(
    const std::string &bundleName, int32_t userId, std::vector<ShortcutInfo> &shortcutInfos) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGW("input invalid userid, bundleName:%{public}s, userId:%{public}d", bundleName.c_str(), userId);
        return false;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo *innerBundleInfo = nullptr;
    if (!GetInnerBundleInfoWithFlags(
        bundleName, BundleFlag::GET_BUNDLE_DEFAULT, innerBundleInfo, requestUserId)) {
        APP_LOGW("GetShortcutInfos failed, bundleName:%{public}s, requestUserId:%{public}d",
            bundleName.c_str(), requestUserId);
        return false;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetShortcutInfos is null.");
        return false;
    }
    GetShortcutInfosByInnerBundleInfo(*innerBundleInfo, shortcutInfos);

    shortcutVisibleStorage_->GetStorageShortcutInfos(
        bundleName, Constants::MAIN_APP_INDEX, requestUserId, shortcutInfos);
    shortcutEnabledStorage_->FilterShortcutInfosEnabled(bundleName, shortcutInfos);
    return true;
}

std::string BundleDataMgr::TryGetRawDataByExtractor(const std::string &hapPath, const std::string &profileName,
    const AbilityInfo &abilityInfo) const
{
    std::string rawData;
    GetJsonProfileByExtractor(hapPath, profileName, rawData);
    if (rawData.empty()) { // if get failed ,try get from resmgr
        BundleMgrClient bundleMgrClient;
        std::vector<std::string> rawJson;
        if (!bundleMgrClient.GetResConfigFile(abilityInfo, META_DATA_SHORTCUTS_NAME, rawJson)) {
            APP_LOGD("GetResConfigFile return false");
            return "";
        }
        return rawJson.empty() ? "" : rawJson[0];
    }
    return rawData;
}

bool BundleDataMgr::ProcessShortcutInfo(const AbilityInfo &abilityInfo, ShortcutJson &shortcutJson) const
{
    std::string rawData;
    for (const auto &meta : abilityInfo.metadata) {
        if (meta.name.compare(META_DATA_SHORTCUTS_NAME) == 0) {
            std::string resName = meta.resource;
            std::string hapPath = abilityInfo.hapPath;
            size_t pos = resName.rfind(PROFILE_PREFIX);
            bool posValid = (pos != std::string::npos) && (pos != resName.length() - strlen(PROFILE_PREFIX));
            if (!posValid) {
                APP_LOGE("resName invalid %{public}s", resName.c_str());
                return false;
            }
            std::string profileName = PROFILE_PATH + resName.substr(pos + strlen(PROFILE_PREFIX)) + JSON_SUFFIX;
            rawData = TryGetRawDataByExtractor(hapPath, profileName, abilityInfo);
            break;
        }
    }
    if (rawData.empty()) {
        APP_LOGE("shortcutinfo is empty");
        return false;
    }
    nlohmann::json jsonObject = nlohmann::json::parse(rawData, nullptr, false, true);
    if (jsonObject.is_discarded()) {
        APP_LOGE("shortcuts json invalid");
        return false;
    }
    shortcutJson = jsonObject.get<ShortcutJson>();
    return true;
}

bool BundleDataMgr::GetShortcutInfosByInnerBundleInfo(
    const InnerBundleInfo &info, std::vector<ShortcutInfo> &shortcutInfos) const
{
    if (!info.GetIsNewVersion()) {
        info.GetShortcutInfos(shortcutInfos);
        return true;
    }
    AbilityInfo abilityInfo;
    info.GetMainAbilityInfo(abilityInfo);
    if (abilityInfo.hapPath.empty() || abilityInfo.metadata.size() <= 0) {
        return false;
    }
    
    ShortcutJson shortcutJson;
    if (!ProcessShortcutInfo(abilityInfo, shortcutJson)) {
        return false;
    }
    for (const Shortcut &item : shortcutJson.shortcuts) {
        ShortcutInfo shortcutInfo;
        shortcutInfo.bundleName = abilityInfo.bundleName;
        shortcutInfo.moduleName = abilityInfo.moduleName;
        info.InnerProcessShortcut(item, shortcutInfo);
        shortcutInfo.sourceType = 1;
        APP_LOGI_NOFUNC("shortcutInfo: -n %{public}s, id %{public}s, iconId %{public}d, labelId %{public}d",
            shortcutInfo.bundleName.c_str(), shortcutInfo.id.c_str(), shortcutInfo.iconId, shortcutInfo.labelId);
        shortcutInfos.emplace_back(shortcutInfo);
    }
    (void)InnerProcessShortcutId(info.GetBundleUpdateTime(Constants::ALL_USERID), abilityInfo.hapPath, shortcutInfos);
    return true;
}

bool BundleDataMgr::GetShortcutInfosByAbilityInfo(const InnerBundleInfo &info, const AbilityInfo &abilityInfo,
    std::vector<ShortcutInfo> &shortcutInfos) const 
{
    if (abilityInfo.hapPath.empty() || abilityInfo.metadata.size() <= 0) {
        return false;
    }
    
    ShortcutJson shortcutJson;
    if (!ProcessShortcutInfo(abilityInfo, shortcutJson)) {
        return false;
    }
    for (const Shortcut &item : shortcutJson.shortcuts) {
        ShortcutInfo shortcutInfo;
        shortcutInfo.bundleName = abilityInfo.bundleName;
        shortcutInfo.moduleName = abilityInfo.moduleName;
        shortcutInfo.hostAbility = abilityInfo.name;
        info.InnerProcessShortcut(item, shortcutInfo);
        shortcutInfo.sourceType = 1;
        APP_LOGI_NOFUNC("shortcutInfo: -n %{public}s, id %{public}s, iconId %{public}d, labelId %{public}d",
            shortcutInfo.bundleName.c_str(), shortcutInfo.id.c_str(), shortcutInfo.iconId, shortcutInfo.labelId);
        shortcutInfos.emplace_back(shortcutInfo);
    }
    (void)InnerProcessShortcutId(info.GetBundleUpdateTime(Constants::ALL_USERID), abilityInfo.hapPath, shortcutInfos);
    return true;
}

#ifdef GLOBAL_RESMGR_ENABLE
std::shared_ptr<Global::Resource::ResourceManager> BundleDataMgr::GetResourceManager(const std::string &hapPath) const
{
    if (hapPath.empty()) {
        APP_LOGE("hapPath is empty");
        return nullptr;
    }
    std::shared_ptr<Global::Resource::ResourceManager> resourceManager(Global::Resource::CreateResourceManager());
    if (resourceManager == nullptr) {
        APP_LOGE("InitResMgr failed, -h:%{public}s", hapPath.c_str());
        return nullptr;
    }

    std::unique_ptr<Global::Resource::ResConfig> resConfig(Global::Resource::CreateResConfig());
    if (!resConfig) {
        APP_LOGE("resConfig is nullptr");
        return nullptr;
    }
    resourceManager->UpdateResConfig(*resConfig);
    if (!resourceManager->AddResource(hapPath.c_str(), Global::Resource::SELECT_STRING)) {
        APP_LOGW("AddResource failed");
    }
    return resourceManager;
}
#endif

bool BundleDataMgr::CheckUpdateTimeWithBmsParam(const int64_t updateTime) const
{
    auto bmsPara = DelayedSingleton<BundleMgrService>::GetInstance()->GetBmsParam();
    if (bmsPara == nullptr) {
        LOG_E(BMS_TAG_DEFAULT, "bmsPara is nullptr");
        return false;
    }
    std::string val;
    if (!bmsPara->GetBmsParam(ServiceConstants::BMS_SYSTEM_TIME_FOR_SHORTCUT, val)) {
        LOG_E(BMS_TAG_DEFAULT, "GetBmsParam BMS_SYSTEM_TIME_FOR_SHORTCUT failed");
        return false;
    }
    if (std::to_string(updateTime) < val) {
        LOG_W(BMS_TAG_DEFAULT, "updateTime is less than val");
        return false;
    }
    return true;
}

bool BundleDataMgr::InnerProcessShortcutId(const int64_t updateTime,
    const std::string &hapPath, std::vector<ShortcutInfo> &shortcutInfos) const
{
#ifdef GLOBAL_RESMGR_ENABLE
    bool needToParseShortcutId = false;
    for (const auto &info : shortcutInfos) {
        if (info.id.find(RESOURCE_STRING_PREFIX) == 0) {
            needToParseShortcutId = true;
            break;
        }
    }
    if (!needToParseShortcutId) {
        return false;
    }

    if (!CheckUpdateTimeWithBmsParam(updateTime)) {
        return false;
    }

    APP_LOGI("shortcut id conatins $string: need parse");
    auto resourceManager = GetResourceManager(hapPath);
    if (resourceManager == nullptr) {
        APP_LOGI("create resource mgr failed");
        return false;
    }

    for (auto &info : shortcutInfos) {
        if (info.id.find(RESOURCE_STRING_PREFIX) != 0) {
            continue;
        }
        uint32_t id = static_cast<uint32_t>(atoi(info.id.substr(std::string(RESOURCE_STRING_PREFIX).size()).c_str()));
        std::string shortcutId;
        OHOS::Global::Resource::RState errValue = resourceManager->GetStringById(id, shortcutId);
        if (errValue != OHOS::Global::Resource::RState::SUCCESS) {
            APP_LOGE("GetStringById failed, id:%{public}d", id);
            continue;
        }
        info.id = shortcutId;
    }
    return true;

#else
    return true;
#endif
}

ErrCode BundleDataMgr::GetShortcutInfoV9(
    const std::string &bundleName, int32_t userId, std::vector<ShortcutInfo> &shortcutInfos) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGW("input invalid userid, bundleName:%{public}s, userId:%{public}d", bundleName.c_str(), userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo *innerBundleInfo = nullptr;
    ErrCode ret = GetInnerBundleInfoWithFlagsV9(bundleName,
        BundleFlag::GET_BUNDLE_DEFAULT, innerBundleInfo, requestUserId);
    if (ret != ERR_OK) {
        APP_LOGD("GetInnerBundleInfoWithFlagsV9 failed, bundleName:%{public}s, requestUserId:%{public}d",
            bundleName.c_str(), requestUserId);
        return ret;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetShortcutInfoV9 is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    GetShortcutInfosByInnerBundleInfo(*innerBundleInfo, shortcutInfos);

    shortcutVisibleStorage_->GetStorageShortcutInfos(
        bundleName, Constants::MAIN_APP_INDEX, requestUserId, shortcutInfos);
    shortcutEnabledStorage_->FilterShortcutInfosEnabled(bundleName, shortcutInfos);
    return ERR_OK;
}

ErrCode BundleDataMgr::GetShortcutInfoByAppIndex(const std::string &bundleName, const int32_t appIndex,
    std::vector<ShortcutInfo> &shortcutInfos) const
{
    if ((appIndex < 0) || (appIndex > ServiceConstants::CLONE_APP_INDEX_MAX)) {
        APP_LOGE("name %{public}s invalid appIndex :%{public}d", bundleName.c_str(), appIndex);
        return ERR_APPEXECFWK_APP_INDEX_OUT_OF_RANGE;
    }
    int32_t requestUserId = GetUserId();
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGW("input invalid userid, bundleName:%{public}s, userId:%{public}d", bundleName.c_str(), requestUserId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo *innerBundleInfo = nullptr;
    int32_t flag = static_cast<int32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_DISABLE);
    ErrCode ret = GetInnerBundleInfoWithFlagsV9(bundleName, flag, innerBundleInfo, requestUserId, appIndex);
    if (ret != ERR_OK) {
        APP_LOGD("GetInnerBundleInfoWithFlagsV9 failed, bundleName:%{public}s, requestUserId:%{public}d",
            bundleName.c_str(), requestUserId);
        return ret;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetShortcutInfoByAppIndex is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    GetShortcutInfosByInnerBundleInfo(*innerBundleInfo, shortcutInfos);
    for (auto &info : shortcutInfos) {
        info.appIndex = appIndex;
    }
    shortcutVisibleStorage_->GetStorageShortcutInfos(bundleName, appIndex, requestUserId, shortcutInfos);
    shortcutEnabledStorage_->FilterShortcutInfosEnabled(bundleName, shortcutInfos);
    return ERR_OK;
}

ErrCode BundleDataMgr::GetShortcutInfoByAbility(const std::string &bundleName,
    const std::string &moduleName, const std::string &abilityName,
    int32_t userId, int32_t appIndex, std::vector<ShortcutInfo> &shortcutInfos) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE_NOFUNC("invalid userId, -n:%{public}s -u:%{public}d", bundleName.c_str(), userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    if ((appIndex < 0) || (appIndex > ServiceConstants::CLONE_APP_INDEX_MAX)) {
        APP_LOGE("name %{public}s invalid appIndex:%{public}d", bundleName.c_str(), appIndex);
        return ERR_APPEXECFWK_APP_INDEX_OUT_OF_RANGE;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo *innerBundleInfo = nullptr;
    ErrCode ret = GetInnerBundleInfoWithFlagsV9(bundleName,
        BundleFlag::GET_BUNDLE_DEFAULT, innerBundleInfo, requestUserId, appIndex);
    if (ret != ERR_OK) {
        APP_LOGD("GetInnerBundleInfoWithFlagsV9 failed, -n:%{public}s, -u:%{public}d",
            bundleName.c_str(), requestUserId);
        return ret;
    }
    if (!innerBundleInfo) {
        APP_LOGE_NOFUNC("The InnerBundleInfo obtained by GetShortcutInfoByAbility is null.");
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }

    // Find ability info
    AbilityInfo abilityInfo;
    ret = innerBundleInfo->FindAbilityInfo(moduleName, abilityName, abilityInfo);
    if (ret != ERR_OK) {
        APP_LOGE_NOFUNC("find ability failed -n:%{public}s -m:%{public}s -a:%{public}s", bundleName.c_str(),
            moduleName.c_str(), abilityName.c_str());
        return ret;
    }

    // Get all shortcut infos by abilityInfo
    std::vector<ShortcutInfo> allShortcutInfos;
    if (!innerBundleInfo->GetIsNewVersion()) {
        innerBundleInfo->GetShortcutInfos(allShortcutInfos);
    } else {
        (void)GetShortcutInfosByAbilityInfo(*innerBundleInfo, abilityInfo, allShortcutInfos);
    }
    
    for (auto &info : allShortcutInfos) {
        info.appIndex = appIndex;
    }

    ProcessShortcutInfos(*innerBundleInfo, moduleName, abilityName, appIndex, requestUserId, allShortcutInfos);

    // Filter by host ability
    for (const auto &shortcutInfo : allShortcutInfos) {
        if (shortcutInfo.moduleName == moduleName && shortcutInfo.hostAbility == abilityName) {
            shortcutInfos.emplace_back(shortcutInfo);
        }
    }

    return ERR_OK;
}

void BundleDataMgr::ProcessShortcutInfos(const InnerBundleInfo &innerBundleInfo, const std::string moduleName,
    const std::string abilityName, const int32_t appIndex,
    const int32_t requestUserId, std::vector<ShortcutInfo> &shortcutInfos) const
{
    auto moduleInfo = innerBundleInfo.GetInnerModuleInfoForEntry();
    std::string mainAbilityName = innerBundleInfo.GetMainAbility();
    std::string bundleName = innerBundleInfo.GetBundleName();
    if (!moduleInfo) {
        APP_LOGW("bundle %{public}s has no entry", bundleName.c_str());
        shortcutVisibleStorage_->GetStorageShortcutInfos(bundleName, appIndex,
            requestUserId, shortcutInfos, true);
    } else {
        if (moduleName == moduleInfo->moduleName && abilityName == mainAbilityName) {
            shortcutVisibleStorage_->GetStorageShortcutInfos(bundleName, appIndex,
                requestUserId, shortcutInfos);
            shortcutEnabledStorage_->FilterShortcutInfosEnabled(bundleName, shortcutInfos);
        } else {
            shortcutVisibleStorage_->GetStorageShortcutInfos(bundleName, appIndex,
                requestUserId, shortcutInfos, true);
        }
    }
    
    for (auto &shortcut : shortcutInfos) {
        if (shortcut.hostAbility.empty()) {
            shortcut.hostAbility = mainAbilityName;
        }
    }
}

bool BundleDataMgr::GetAllCommonEventInfo(const std::string &eventKey,
    std::vector<CommonEventInfo> &commonEventInfos) const
{
    if (eventKey.empty()) {
        APP_LOGW("event key is empty");
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return false;
    }
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &info = item.second;
        if (info.IsDisabled()) {
            APP_LOGD("app %{public}s is disabled", info.GetBundleName().c_str());
            continue;
        }
        info.GetCommonEvents(eventKey, commonEventInfos);
    }
    if (commonEventInfos.size() == 0) {
        APP_LOGW("commonEventInfos is empty");
        return false;
    }
    APP_LOGE("commonEventInfos find success");
    return true;
}

bool BundleDataMgr::SavePreInstallBundleInfo(
    const std::string &bundleName, const PreInstallBundleInfo &preInstallBundleInfo)
{
    if (preInstallDataStorage_ == nullptr) {
        APP_LOGW("preInstallDataStorage_ is nullptr");
        return false;
    }

    if (preInstallDataStorage_->SavePreInstallStorageBundleInfo(preInstallBundleInfo)) {
        APP_LOGD("write storage success bundle:%{public}s", bundleName.c_str());
        return true;
    }

    return false;
}

bool BundleDataMgr::DeletePreInstallBundleInfo(
    const std::string &bundleName, const PreInstallBundleInfo &preInstallBundleInfo)
{
    if (preInstallDataStorage_ == nullptr) {
        APP_LOGW("preInstallDataStorage_ is nullptr");
        return false;
    }

    if (preInstallDataStorage_->DeletePreInstallStorageBundleInfo(preInstallBundleInfo)) {
        APP_LOGD("Delete PreInstall Storage success bundle:%{public}s", bundleName.c_str());
        return true;
    }

    return false;
}

bool BundleDataMgr::GetPreInstallBundleInfo(
    const std::string &bundleName, PreInstallBundleInfo &preInstallBundleInfo)
{
    if (bundleName.empty()) {
        APP_LOGW("bundleName is empty");
        return false;
    }
    if (preInstallDataStorage_ == nullptr) {
        return false;
    }
    if (!preInstallDataStorage_->LoadPreInstallBundleInfo(bundleName, preInstallBundleInfo)) {
        APP_LOGW_NOFUNC("get preInstall bundleInfo failed -n: %{public}s", bundleName.c_str());
        return false;
    }
    return true;
}

bool BundleDataMgr::LoadAllPreInstallBundleInfos(std::vector<PreInstallBundleInfo> &preInstallBundleInfos)
{
    if (preInstallDataStorage_ == nullptr) {
        APP_LOGW("preInstallDataStorage_ is nullptr");
        return false;
    }

    if (preInstallDataStorage_->LoadAllPreInstallBundleInfos(preInstallBundleInfos)) {
        APP_LOGD("load all storage success");
        return true;
    }

    return false;
}

bool BundleDataMgr::SaveInnerBundleInfo(const InnerBundleInfo &info) const
{
    APP_LOGD("write install InnerBundleInfo to storage with bundle:%{public}s", info.GetBundleName().c_str());
    if (dataStorage_->SaveStorageBundleInfo(info)) {
        APP_LOGD("save install InnerBundleInfo successfully");
        return true;
    }
    APP_LOGW("save install InnerBundleInfo failed, bundleName:%{public}s", info.GetBundleName().c_str());
    return false;
}

bool BundleDataMgr::GetInnerBundleUserInfoByUserId(const std::string &bundleName,
    int32_t userId, InnerBundleUserInfo &innerBundleUserInfo) const
{
    APP_LOGD("get user info start: bundleName: (%{public}s)  userId: (%{public}d) ",
        bundleName.c_str(), userId);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return false;
    }

    if (bundleName.empty()) {
        APP_LOGW("bundle name is empty");
        return false;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos data is empty, bundleName:%{public}s", bundleName.c_str());
        return false;
    }

    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("bundleName:%{public}s not exist", bundleName.c_str());
        return false;
    }

    return infoItem->second.GetInnerBundleUserInfo(requestUserId, innerBundleUserInfo);
}

int32_t BundleDataMgr::GetUserId(int32_t userId) const
{
    if (userId == Constants::ANY_USERID || userId == Constants::ALL_USERID) {
        return userId;
    }

    if (userId == Constants::UNSPECIFIED_USERID) {
        userId = GetUserIdByCallingUid();
    }

    if (!HasUserId(userId)) {
        APP_LOGD("user is not existed");
        userId = Constants::INVALID_USERID;
    }

    return userId;
}

int32_t BundleDataMgr::GetUserIdByUid(int32_t uid) const
{
    return BundleUtil::GetUserIdByUid(uid);
}

void BundleDataMgr::AddUserId(int32_t userId)
{
    std::lock_guard<std::mutex> lock(multiUserIdSetMutex_);
    auto item = multiUserIdsSet_.find(userId);
    if (item != multiUserIdsSet_.end()) {
        return;
    }

    multiUserIdsSet_.insert(userId);
}

void BundleDataMgr::RemoveUserId(int32_t userId)
{
    std::lock_guard<std::mutex> lock(multiUserIdSetMutex_);
    auto item = multiUserIdsSet_.find(userId);
    if (item == multiUserIdsSet_.end()) {
        return;
    }

    multiUserIdsSet_.erase(item);
}

bool BundleDataMgr::HasUserId(int32_t userId) const
{
    std::lock_guard<std::mutex> lock(multiUserIdSetMutex_);
    return multiUserIdsSet_.find(userId) != multiUserIdsSet_.end();
}

int32_t BundleDataMgr::GetUserIdByCallingUid() const
{
    return BundleUtil::GetUserIdByCallingUid();
}

std::set<int32_t> BundleDataMgr::GetAllUser() const
{
    std::lock_guard<std::mutex> lock(multiUserIdSetMutex_);
    return multiUserIdsSet_;
}

void BundleDataMgr::CreateAppInstallDir(int32_t userId)
{
    std::string path = std::string(ServiceConstants::HAP_COPY_PATH) +
        ServiceConstants::GALLERY_DOWNLOAD_PATH + std::to_string(userId);
    ErrCode ret = InstalldClient::GetInstance()->Mkdir(path,
        S_IRWXU | S_IRWXG | S_IXOTH | S_ISGID,
        Constants::FOUNDATION_UID, ServiceConstants::APP_INSTALL_GID);
    if (ret != ERR_OK) {
        APP_LOGE("create app install %{public}d failed", userId);
        return;
    }

    std::string appClonePath = path + ServiceConstants::GALLERY_CLONE_PATH;
    ret = InstalldClient::GetInstance()->Mkdir(appClonePath,
        S_IRWXU | S_IRWXG | S_IXOTH | S_ISGID,
        Constants::FOUNDATION_UID, ServiceConstants::APP_INSTALL_GID);
    if (ret != ERR_OK) {
        APP_LOGE("create app clone %{public}d failed", userId);
    }
}

void BundleDataMgr::RemoveAppInstallDir(int32_t userId)
{
    std::string path = std::string(ServiceConstants::HAP_COPY_PATH) +
        ServiceConstants::GALLERY_DOWNLOAD_PATH + std::to_string(userId);
    ErrCode ret = InstalldClient::GetInstance()->RemoveDir(path);
    if (ret != ERR_OK) {
        APP_LOGE("remove app install %{public}d failed", userId);
    }
}

bool BundleDataMgr::GetInnerBundleUserInfos(
    const std::string &bundleName, std::vector<InnerBundleUserInfo> &innerBundleUserInfos) const
{
    APP_LOGD("get all user info in bundle(%{public}s)", bundleName.c_str());
    if (bundleName.empty()) {
        APP_LOGW("bundle name is empty");
        return false;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos data is empty, bundleName:%{public}s", bundleName.c_str());
        return false;
    }

    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW_NOFUNC("%{public}s not exist", bundleName.c_str());
        return false;
    }

    for (auto userInfo : infoItem->second.GetInnerBundleUserInfos()) {
        innerBundleUserInfos.emplace_back(userInfo.second);
    }

    return !innerBundleUserInfos.empty();
}

std::string BundleDataMgr::GetAppPrivilegeLevel(const std::string &bundleName, int32_t userId)
{
    APP_LOGD("GetAppPrivilegeLevel:%{public}s, userId:%{public}d", bundleName.c_str(), userId);
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo *info = nullptr;
    if (!GetInnerBundleInfoWithFlags(bundleName, 0, info, userId)) {
        return Constants::EMPTY_STRING;
    }
    if (!info) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetAppPrivilegeLevel is null.");
        return Constants::EMPTY_STRING;
    }

    return info->GetAppPrivilegeLevel();
}

bool BundleDataMgr::QueryExtensionAbilityInfos(const Want &want, int32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &extensionInfos, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        LOG_E(BMS_TAG_QUERY, "invalid userId, userId:%{public}d", userId);
        return false;
    }

    ElementName element = want.GetElement();
    std::string bundleName = element.GetBundleName();
    std::string extensionName = element.GetAbilityName();
    LOG_D(BMS_TAG_QUERY, "bundleName:%{public}s extensionName:%{public}s",
        bundleName.c_str(), extensionName.c_str());
    // explicit query
    if (!bundleName.empty() && !extensionName.empty()) {
        ExtensionAbilityInfo info;
        if (ExplicitQueryExtensionInfo(want, flags, requestUserId, info, appIndex)) {
            extensionInfos.emplace_back(info);
        }
        LOG_NOFUNC_I(BMS_TAG_QUERY, "ExplicitQueryExtension size:%{public}zu -n %{public}s -e %{public}s -u %{public}d"
            " -i %{public}d", extensionInfos.size(), bundleName.c_str(), extensionName.c_str(), userId, appIndex);
        return !extensionInfos.empty();
    }

    bool ret = ImplicitQueryExtensionInfos(want, flags, requestUserId, extensionInfos, appIndex);
    if (!ret) {
        LOG_D(BMS_TAG_QUERY,
            "implicit queryExtension error action:%{public}s uri:%{private}s type:%{public}s"
            " userId:%{public}d", want.GetAction().c_str(), want.GetUriString().c_str(), want.GetType().c_str(),
            requestUserId);
        return false;
    }
    if (extensionInfos.size() == 0) {
        LOG_W(BMS_TAG_QUERY, "no matching abilityInfo action:%{public}s uri:%{private}s type:%{public}s"
            " userId:%{public}d", want.GetAction().c_str(), want.GetUriString().c_str(), want.GetType().c_str(),
            requestUserId);
        return false;
    }
    LOG_D(BMS_TAG_QUERY, "query extensionAbilityInfo successfully");
    return true;
}

ErrCode BundleDataMgr::QueryExtensionAbilityInfosV9(const Want &want, int32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &extensionInfos, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    ElementName element = want.GetElement();
    std::string bundleName = element.GetBundleName();
    std::string extensionName = element.GetAbilityName();
    LOG_D(BMS_TAG_QUERY, "bundle name:%{public}s, extension name:%{public}s",
        bundleName.c_str(), extensionName.c_str());
    // explicit query
    if (!bundleName.empty() && !extensionName.empty()) {
        ExtensionAbilityInfo info;
        ErrCode ret = ExplicitQueryExtensionInfoV9(want, flags, requestUserId, info, appIndex);
        if (ret == ERR_OK) {
            extensionInfos.emplace_back(info);
        }
        LOG_NOFUNC_I(BMS_TAG_QUERY, "ExplicitQueryExtension V9 size:%{public}zu -n %{public}s -e %{public}s"
            " -u %{public}d -i %{public}d", extensionInfos.size(), bundleName.c_str(), extensionName.c_str(),
            userId, appIndex);
        return ret;
    }
    ErrCode ret = ImplicitQueryExtensionInfosV9(want, flags, requestUserId, extensionInfos, appIndex);
    if (ret != ERR_OK) {
        LOG_D(BMS_TAG_QUERY, "ImplicitQueryExtensionInfosV9 error");
        return ret;
    }
    if (extensionInfos.empty()) {
        LOG_W(BMS_TAG_QUERY, "no matching abilityInfo action:%{public}s uri:%{private}s type:%{public}s"
            " userId:%{public}d", want.GetAction().c_str(), want.GetUriString().c_str(), want.GetType().c_str(),
            requestUserId);
        return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
    }
    LOG_D(BMS_TAG_QUERY, "QueryExtensionAbilityInfosV9 success");
    return ERR_OK;
}

ErrCode BundleDataMgr::QueryExtensionAbilityInfos(uint32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &extensionInfos, int32_t appIndex) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        LOG_E(BMS_TAG_QUERY, "invalid userId, userId:%{public}d", userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    ErrCode ret = ImplicitQueryAllExtensionInfos(flags, requestUserId, extensionInfos, appIndex);
    if (ret != ERR_OK) {
        LOG_D(BMS_TAG_QUERY, "ImplicitQueryAllExtensionInfos error: %{public}d", ret);
        return ret;
    }
    if (extensionInfos.empty()) {
        LOG_W(BMS_TAG_QUERY, "no matching abilityInfo");
        return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
    }
    LOG_D(BMS_TAG_QUERY, "success");
    return ERR_OK;
}

ErrCode BundleDataMgr::QueryExtensionAbilityInfosByExtensionTypeName(const std::string &typeName,
    uint32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &extensionInfos, int32_t appIndex) const
{
    LOG_I(BMS_TAG_QUERY, "query failed %{public}s %{public}d", typeName.c_str(), userId);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    ErrCode ret = ImplicitQueryAllExtensionInfos(
        flags, requestUserId, extensionInfos, appIndex, typeName);
    if (ret != ERR_OK) {
        LOG_W(BMS_TAG_QUERY, "ImplicitQueryAllExtensionInfos error: %{public}d", ret);
        return ret;
    }
    if (extensionInfos.empty()) {
        LOG_W(BMS_TAG_QUERY, "no matching abilityInfo");
        return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
    }
    LOG_D(BMS_TAG_QUERY, "success");
    return ERR_OK;
}

void BundleDataMgr::GetOneExtensionInfosByExtensionTypeName(const std::string &typeName, uint32_t flags, int32_t userId,
    const InnerBundleInfo &info, std::vector<ExtensionAbilityInfo> &infos, int32_t appIndex) const
{
    auto innerExtensionInfos = info.GetInnerExtensionInfos();
    for (const auto &item : innerExtensionInfos) {
        if (typeName != item.second.extensionTypeName) {
            continue;
        }
        infos.emplace_back(InnerExtensionInfo::ConvertToExtensionInfo(item.second));
        return;
    }
}

bool BundleDataMgr::ExplicitQueryExtensionInfo(const Want &want, int32_t flags, int32_t userId,
    ExtensionAbilityInfo &extensionInfo, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    ElementName element = want.GetElement();
    std::string bundleName = element.GetBundleName();
    std::string moduleName = element.GetModuleName();
    std::string extensionName = element.GetAbilityName();
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo *innerBundleInfo = nullptr;
    if ((appIndex == 0) && (!GetInnerBundleInfoWithFlags(bundleName, flags, innerBundleInfo, requestUserId))) {
        LOG_W(BMS_TAG_QUERY, "ExplicitQueryExtensionInfo failed");
        return false;
    }
    InnerBundleInfo sandboxInfo;
    if (appIndex > Constants::INITIAL_SANDBOX_APP_INDEX) {
        if (sandboxAppHelper_ == nullptr) {
            LOG_W(BMS_TAG_QUERY, "sandboxAppHelper_ is nullptr");
            return false;
        }
        auto ret = sandboxAppHelper_->GetSandboxAppInfo(bundleName, appIndex, requestUserId, sandboxInfo);
        if (ret != ERR_OK) {
            LOG_D(BMS_TAG_QUERY, "GetSandboxAppInfo failed errCode %{public}d", ret);
            return false;
        }
        innerBundleInfo = &sandboxInfo;
    }
    if (appIndex > Constants::INITIAL_APP_INDEX && appIndex <= Constants::INITIAL_SANDBOX_APP_INDEX) {
        bool res = GetInnerBundleInfoWithFlags(bundleName, flags, innerBundleInfo, requestUserId, appIndex);
        if (!res) {
            LOG_W(BMS_TAG_QUERY, "ExplicitQueryExtensionInfo failed");
            return false;
        }
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ExplicitQueryExtensionInfo is null.");
        return false;
    }
    auto extension = innerBundleInfo->FindExtensionInfo(moduleName, extensionName);
    if (!extension) {
        LOG_W(BMS_TAG_QUERY, "extensionAbility not found or disabled");
        return false;
    }
    if ((static_cast<uint32_t>(flags) & GET_ABILITY_INFO_WITH_PERMISSION) != GET_ABILITY_INFO_WITH_PERMISSION) {
        extension->permissions.clear();
    }
    if ((static_cast<uint32_t>(flags) & GET_ABILITY_INFO_WITH_METADATA) != GET_ABILITY_INFO_WITH_METADATA) {
        extension->metadata.clear();
    }
    if ((static_cast<uint32_t>(flags) & GET_ABILITY_INFO_WITH_SKILL) != GET_ABILITY_INFO_WITH_SKILL) {
        extension->skills.clear();
    }
    extensionInfo = (*extension);
    if ((static_cast<uint32_t>(flags) & GET_ABILITY_INFO_WITH_APPLICATION) == GET_ABILITY_INFO_WITH_APPLICATION) {
        int32_t responseUserId = innerBundleInfo->GetResponseUserId(requestUserId);
        innerBundleInfo->GetApplicationInfo(ApplicationFlag::GET_BASIC_APPLICATION_INFO |
            ApplicationFlag::GET_APPLICATION_INFO_WITH_CERTIFICATE_FINGERPRINT, responseUserId,
            extensionInfo.applicationInfo, appIndex);
    }
    // set uid for NAPI cache use
    const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
    if (innerBundleInfo->GetInnerBundleUserInfo(userId, innerBundleUserInfoPtr)) {
        if (innerBundleUserInfoPtr) {
            extensionInfo.uid = innerBundleUserInfoPtr->uid;
            if (appIndex > 0 && appIndex <= Constants::INITIAL_SANDBOX_APP_INDEX) {
                std::string key = InnerBundleUserInfo::AppIndexToKey(appIndex);
                if (innerBundleUserInfoPtr->cloneInfos.find(key) != innerBundleUserInfoPtr->cloneInfos.end()) {
                    auto cloneInfo = innerBundleUserInfoPtr->cloneInfos.at(key);
                    extensionInfo.uid = cloneInfo.uid;
                }
            }
        } else {
            LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ExplicitQueryExtensionInfo is null");
        }
    }
    extensionInfo.appIndex = appIndex;
    return true;
}

ErrCode BundleDataMgr::ExplicitQueryExtensionInfoV9(const Want &want, int32_t flags, int32_t userId,
    ExtensionAbilityInfo &extensionInfo, int32_t appIndex) const
{
    if (appIndex < 0) {
        LOG_E(BMS_TAG_QUERY, "ExplicitQueryExtensionInfoV9 appIndex out of range");
        return ERR_APPEXECFWK_APP_INDEX_OUT_OF_RANGE;
    }
    ElementName element = want.GetElement();
    std::string bundleName = element.GetBundleName();
    std::string moduleName = element.GetModuleName();
    std::string extensionName = element.GetAbilityName();
    LOG_D(BMS_TAG_QUERY, "bundleName:%{public}s, moduleName:%{public}s, abilityName:%{public}s",
        bundleName.c_str(), moduleName.c_str(), extensionName.c_str());
    LOG_D(BMS_TAG_QUERY, "flags:%{public}d, userId:%{public}d, appIndex:%{public}d",
        flags, userId, appIndex);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo *innerBundleInfo = nullptr;
    InnerBundleInfo sandboxInfo;
    if (appIndex == 0) {
        ErrCode ret = GetInnerBundleInfoWithFlagsV9(bundleName, flags, innerBundleInfo, requestUserId);
        if (ret != ERR_OK) {
            LOG_D(BMS_TAG_QUERY, "ExplicitQueryExtensionInfoV9 failed");
            return ret;
        }
    } else if (appIndex > 0 && appIndex <= Constants::INITIAL_SANDBOX_APP_INDEX) {
        ErrCode ret = GetInnerBundleInfoWithFlagsV9(bundleName, flags, innerBundleInfo, requestUserId, appIndex);
        if (ret != ERR_OK) {
            LOG_W(BMS_TAG_QUERY, "ExplicitQueryExtensionInfoV9 failed");
            return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
        }
    } else if (appIndex > Constants::INITIAL_SANDBOX_APP_INDEX) {
        if (sandboxAppHelper_ == nullptr) {
            LOG_W(BMS_TAG_QUERY, "sandboxAppHelper_ is nullptr");
            return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
        }
        auto ret = sandboxAppHelper_->GetSandboxAppInfo(bundleName, appIndex, requestUserId, sandboxInfo);
        if (ret != ERR_OK) {
            LOG_D(BMS_TAG_QUERY, "GetSandboxAppInfo failed errCode %{public}d", ret);
            return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
        }
        innerBundleInfo = &sandboxInfo;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ExplicitQueryExtensionInfoV9 is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    auto extension = innerBundleInfo->FindExtensionInfo(moduleName, extensionName);
    if (!extension) {
        LOG_W(BMS_TAG_QUERY, "extensionAbility not found or disabled");
        return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
    }
    if ((static_cast<uint32_t>(flags) &
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_PERMISSION)) !=
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_PERMISSION)) {
        extension->permissions.clear();
    }
    if ((static_cast<uint32_t>(flags) &
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_METADATA)) !=
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_METADATA)) {
        extension->metadata.clear();
    }
    if ((static_cast<uint32_t>(flags) &
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_SKILL)) !=
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_SKILL)) {
        extension->skills.clear();
    }
    extensionInfo = (*extension);
    if ((static_cast<uint32_t>(flags) &
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_APPLICATION)) ==
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_APPLICATION)) {
        int32_t responseUserId = innerBundleInfo->GetResponseUserId(requestUserId);
        innerBundleInfo->GetApplicationInfoV9(
            static_cast<int32_t>(GetApplicationFlag::GET_APPLICATION_INFO_DEFAULT),
            responseUserId, extensionInfo.applicationInfo, appIndex);
    }
    // set uid for NAPI cache use
    const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
    if (innerBundleInfo->GetInnerBundleUserInfo(userId, innerBundleUserInfoPtr)) {
        if (innerBundleUserInfoPtr) {
            extensionInfo.uid = innerBundleUserInfoPtr->uid;
            if (appIndex > 0 && appIndex <= Constants::INITIAL_SANDBOX_APP_INDEX) {
                std::string key = InnerBundleUserInfo::AppIndexToKey(appIndex);
                if (innerBundleUserInfoPtr->cloneInfos.find(key) != innerBundleUserInfoPtr->cloneInfos.end()) {
                    auto cloneInfo = innerBundleUserInfoPtr->cloneInfos.at(key);
                    extensionInfo.uid = cloneInfo.uid;
                }
            }
        } else {
            LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ExplicitQueryExtensionInfoV9 is null");
        }
    }
    extensionInfo.appIndex = appIndex;
    return ERR_OK;
}

void BundleDataMgr::FilterExtensionAbilityInfosByModuleName(const std::string &moduleName,
    std::vector<ExtensionAbilityInfo> &extensionInfos) const
{
    LOG_D(BMS_TAG_QUERY, "FilterExtensionAbilityInfos moduleName: %{public}s", moduleName.c_str());
    if (moduleName.empty()) {
        return;
    }
    for (auto iter = extensionInfos.begin(); iter != extensionInfos.end();) {
        if (iter->moduleName != moduleName) {
            iter = extensionInfos.erase(iter);
        } else {
            ++iter;
        }
    }
}

bool BundleDataMgr::ImplicitQueryExtensionInfos(const Want &want, int32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &extensionInfos, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    if (want.GetAction().empty() && want.GetEntities().empty()
        && want.GetUriString().empty() && want.GetType().empty() && want.GetStringParam(LINK_FEATURE).empty()) {
        LOG_W(BMS_TAG_QUERY, "param invalid");
        return false;
    }
    LOG_D(BMS_TAG_QUERY, "action:%{public}s, uri:%{private}s, type:%{public}s",
        want.GetAction().c_str(), want.GetUriString().c_str(), want.GetType().c_str());
    LOG_D(BMS_TAG_QUERY, "flags:%{public}d, userId:%{public}d", flags, userId);

    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    std::string bundleName = want.GetElement().GetBundleName();
    if (!bundleName.empty()) {
        // query in current bundle
        if (!ImplicitQueryCurExtensionInfos(want, flags, requestUserId, extensionInfos, appIndex)) {
            LOG_D(BMS_TAG_QUERY, "ImplicitQueryCurExtension failed, bundleName:%{public}s",
                bundleName.c_str());
            return false;
        }
    } else {
        // query all
        ImplicitQueryAllExtensionInfos(want, flags, requestUserId, extensionInfos, appIndex);
    }
    // sort by priority, descending order.
    if (extensionInfos.size() > 1) {
        std::stable_sort(extensionInfos.begin(), extensionInfos.end(),
            [](ExtensionAbilityInfo a, ExtensionAbilityInfo b) { return a.priority > b.priority; });
    }
    return true;
}

ErrCode BundleDataMgr::ImplicitQueryExtensionInfosV9(const Want &want, int32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &extensionInfos, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    if (want.GetAction().empty() && want.GetEntities().empty()
        && want.GetUriString().empty() && want.GetType().empty() && want.GetStringParam(LINK_FEATURE).empty()) {
        LOG_W(BMS_TAG_QUERY, "param invalid");
        return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
    }
    LOG_D(BMS_TAG_QUERY, "action:%{public}s, uri:%{private}s, type:%{public}s",
        want.GetAction().c_str(), want.GetUriString().c_str(), want.GetType().c_str());
    LOG_D(BMS_TAG_QUERY, "flags:%{public}d, userId:%{public}d", flags, userId);

    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    std::string bundleName = want.GetElement().GetBundleName();
    if (!bundleName.empty()) {
        // query in current bundle
        ErrCode ret = ImplicitQueryCurExtensionInfosV9(want, flags, requestUserId, extensionInfos, appIndex);
        if (ret != ERR_OK) {
            LOG_D(BMS_TAG_QUERY, "ImplicitQueryCurExtensionInfos failed, bundleName:%{public}s",
                bundleName.c_str());
            return ret;
        }
    } else {
        // query all
        ImplicitQueryAllExtensionInfosV9(want, flags, requestUserId, extensionInfos, appIndex);
    }
    // sort by priority, descending order.
    if (extensionInfos.size() > 1) {
        std::stable_sort(extensionInfos.begin(), extensionInfos.end(),
            [](ExtensionAbilityInfo a, ExtensionAbilityInfo b) { return a.priority > b.priority; });
    }
    return ERR_OK;
}

bool BundleDataMgr::ImplicitQueryCurExtensionInfos(const Want &want, int32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &infos, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    LOG_D(BMS_TAG_QUERY, "begin to ImplicitQueryCurExtensionInfos");
    std::string bundleName = want.GetElement().GetBundleName();
    const InnerBundleInfo *innerBundleInfo = nullptr;
    if ((appIndex == 0) && (!GetInnerBundleInfoWithFlags(bundleName, flags, innerBundleInfo, userId))) {
        LOG_D(BMS_TAG_QUERY, "ImplicitQueryExtensionAbilityInfos failed, bundleName:%{public}s",
            bundleName.c_str());
        return false;
    }
    InnerBundleInfo sandboxInfo;
    if (appIndex > Constants::INITIAL_SANDBOX_APP_INDEX) {
        if (sandboxAppHelper_ == nullptr) {
            LOG_W(BMS_TAG_QUERY, "sandboxAppHelper_ is nullptr");
            return false;
        }
        auto ret = sandboxAppHelper_->GetSandboxAppInfo(bundleName, appIndex, userId, sandboxInfo);
        if (ret != ERR_OK) {
            LOG_D(BMS_TAG_QUERY, "GetSandboxAppInfo failed errCode %{public}d", ret);
            return false;
        }
        innerBundleInfo = &sandboxInfo;
    }
    if (appIndex > 0 && appIndex <= Constants::INITIAL_SANDBOX_APP_INDEX) {
        bool ret = GetInnerBundleInfoWithFlags(bundleName, flags, innerBundleInfo, userId, appIndex);
        if (!ret) {
            LOG_D(BMS_TAG_QUERY, "ImplicitQueryExtensionAbilityInfos failed errCode %{public}d", ret);
            return false;
        }
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ImplicitQueryCurExtensionInfos is null.");
        return false;
    }
    int32_t responseUserId = innerBundleInfo->GetResponseUserId(userId);
    GetMatchExtensionInfos(want, flags, responseUserId, *innerBundleInfo, infos, appIndex);
    FilterExtensionAbilityInfosByModuleName(want.GetElement().GetModuleName(), infos);
    LOG_D(BMS_TAG_QUERY, "finish to ImplicitQueryCurExtensionInfos");
    return true;
}

ErrCode BundleDataMgr::ImplicitQueryCurExtensionInfosV9(const Want &want, int32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &infos, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    LOG_D(BMS_TAG_QUERY, "begin to ImplicitQueryCurExtensionInfosV9");
    if (appIndex < 0) {
        LOG_E(BMS_TAG_QUERY, "ImplicitQueryCurExtensionInfosV9 appIndex out of range");
        return ERR_APPEXECFWK_APP_INDEX_OUT_OF_RANGE;
    }
    std::string bundleName = want.GetElement().GetBundleName();
    const InnerBundleInfo *innerBundleInfo = nullptr;
    InnerBundleInfo sandboxInfo;
    if (appIndex == 0) {
        ErrCode ret = GetInnerBundleInfoWithFlagsV9(bundleName, flags, innerBundleInfo, userId);
        if (ret != ERR_OK) {
            LOG_W(BMS_TAG_QUERY, "GetInnerBundleInfoWithFlagsV9 failed, bundleName:%{public}s",
                bundleName.c_str());
            return ret;
        }
    } else if (appIndex > 0 && appIndex <= Constants::INITIAL_SANDBOX_APP_INDEX) {
        ErrCode ret = GetInnerBundleInfoWithFlagsV9(bundleName, flags, innerBundleInfo, userId, appIndex);
        if (ret != ERR_OK) {
            LOG_W(BMS_TAG_QUERY, "GetInnerBundleInfoWithFlagsV9 failed, bundleName:%{public}s",
                bundleName.c_str());
            return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
        }
    } else if (appIndex > Constants::INITIAL_SANDBOX_APP_INDEX) {
        if (sandboxAppHelper_ == nullptr) {
            LOG_W(BMS_TAG_QUERY, "sandboxAppHelper_ is nullptr");
            return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
        }
        auto ret = sandboxAppHelper_->GetSandboxAppInfo(bundleName, appIndex, userId, sandboxInfo);
        if (ret != ERR_OK) {
            LOG_D(BMS_TAG_QUERY, "GetSandboxAppInfo failed errCode %{public}d", ret);
            return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
        }
        innerBundleInfo = &sandboxInfo;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ImplicitQueryCurExtensionInfosV9 is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    int32_t responseUserId = innerBundleInfo->GetResponseUserId(userId);
    GetMatchExtensionInfosV9(want, flags, responseUserId, *innerBundleInfo, infos, appIndex);
    FilterExtensionAbilityInfosByModuleName(want.GetElement().GetModuleName(), infos);
    LOG_D(BMS_TAG_QUERY, "finish to ImplicitQueryCurExtensionInfosV9");
    return ERR_OK;
}

void BundleDataMgr::ImplicitQueryAllExtensionInfos(const Want &want, int32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &infos, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    LOG_D(BMS_TAG_QUERY, "begin to ImplicitQueryAllExtensionInfos");
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        LOG_E(BMS_TAG_QUERY, "invalid userId, userId:%{public}d", userId);
        return;
    }

    // query from bundleInfos_
    if (appIndex == 0) {
        for (const auto &item : bundleInfos_) {
            const InnerBundleInfo &innerBundleInfo = item.second;
            int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
            if (CheckInnerBundleInfoWithFlags(innerBundleInfo, flags, responseUserId) != ERR_OK) {
                continue;
            }
            GetMatchExtensionInfos(want, flags, responseUserId, innerBundleInfo, infos);
        }
    } else if (appIndex > Constants::INITIAL_SANDBOX_APP_INDEX) {
        // query from sandbox manager for sandbox bundle
        if (sandboxAppHelper_ == nullptr) {
            LOG_W(BMS_TAG_QUERY, "sandboxAppHelper_ is nullptr");
            return;
        }
        auto sandboxMap = sandboxAppHelper_->GetSandboxAppInfoMap();
        for (const auto &item : sandboxMap) {
            InnerBundleInfo info;
            size_t pos = item.first.rfind(Constants::FILE_UNDERLINE);
            if (pos == std::string::npos) {
                LOG_W(BMS_TAG_QUERY, "sandbox map contains invalid element");
                continue;
            }
            std::string innerBundleName = item.first.substr(pos + 1);
            if (sandboxAppHelper_->GetSandboxAppInfo(innerBundleName, appIndex, userId, info) != ERR_OK) {
                LOG_D(BMS_TAG_QUERY, "obtain innerBundleInfo of sandbox app failed");
                continue;
            }

            int32_t responseUserId = info.GetResponseUserId(userId);
            GetMatchExtensionInfos(want, flags, responseUserId, info, infos);
        }
    } else if (appIndex > Constants::INITIAL_APP_INDEX && appIndex <= Constants::INITIAL_SANDBOX_APP_INDEX) {
        LOG_D(BMS_TAG_QUERY, "start to query extensionAbility in appClone");
        for (const auto &item : bundleInfos_) {
            int32_t responseUserId = item.second.GetResponseUserId(requestUserId);
            const InnerBundleInfo &innerBundleInfo = item.second;
            if (CheckInnerBundleInfoWithFlags(innerBundleInfo, flags, responseUserId, appIndex) != ERR_OK) {
                LOG_D(BMS_TAG_QUERY,
                    "failed, bundleName:%{public}s, responseUserId:%{public}d, appIndex:%{public}d",
                    innerBundleInfo.GetBundleName().c_str(), responseUserId, appIndex);
                continue;
            }
            GetMatchExtensionInfos(want, flags, responseUserId, innerBundleInfo, infos, appIndex);
        }
    }
    LOG_D(BMS_TAG_QUERY, "finish to ImplicitQueryAllExtensionInfos");
}

void BundleDataMgr::ImplicitQueryAllExtensionInfosV9(const Want &want, int32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &infos, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    LOG_D(BMS_TAG_QUERY, "begin to ImplicitQueryAllExtensionInfosV9");
    // query from bundleInfos_
    if (appIndex == 0) {
        for (const auto &item : bundleInfos_) {
            const InnerBundleInfo &innerBundleInfo = item.second;
            ErrCode ret = CheckBundleAndAbilityDisabled(innerBundleInfo, flags, userId);
            if (ret != ERR_OK) {
                LOG_D(BMS_TAG_QUERY, "ImplicitQueryExtensionAbilityInfos failed, bundleName:%{public}s",
                    item.first.c_str());
                continue;
            }
            int32_t responseUserId = innerBundleInfo.GetResponseUserId(userId);
            GetMatchExtensionInfosV9(want, flags, responseUserId, innerBundleInfo, infos);
        }
    } else if (appIndex > 0 && appIndex <= Constants::INITIAL_SANDBOX_APP_INDEX) {
        for (const auto &item : bundleInfos_) {
            const InnerBundleInfo &innerBundleInfo = item.second;
            int32_t responseUserId = innerBundleInfo.GetResponseUserId(userId);
            if (CheckInnerBundleInfoWithFlagsV9(innerBundleInfo, flags, responseUserId, appIndex) != ERR_OK) {
                LOG_D(BMS_TAG_QUERY,
                    "failed, bundleName:%{public}s, responseUserId:%{public}d, appIndex:%{public}d",
                    innerBundleInfo.GetBundleName().c_str(), responseUserId, appIndex);
                continue;
            }
            GetMatchExtensionInfosV9(want, flags, responseUserId, innerBundleInfo, infos, appIndex);
        }
    } else if (appIndex > Constants::INITIAL_SANDBOX_APP_INDEX) {
        // query from sandbox manager for sandbox bundle
        if (sandboxAppHelper_ == nullptr) {
            LOG_W(BMS_TAG_QUERY, "sandboxAppHelper_ is nullptr");
            return;
        }
        auto sandboxMap = sandboxAppHelper_->GetSandboxAppInfoMap();
        for (const auto &item : sandboxMap) {
            InnerBundleInfo info;
            size_t pos = item.first.rfind(Constants::FILE_UNDERLINE);
            if (pos == std::string::npos) {
                LOG_W(BMS_TAG_QUERY, "sandbox map contains invalid element");
                continue;
            }
            std::string innerBundleName = item.first.substr(pos + 1);
            if (sandboxAppHelper_->GetSandboxAppInfo(innerBundleName, appIndex, userId, info) != ERR_OK) {
                LOG_D(BMS_TAG_QUERY, "obtain innerBundleInfo of sandbox app failed");
                continue;
            }

            int32_t responseUserId = info.GetResponseUserId(userId);
            GetMatchExtensionInfosV9(want, flags, responseUserId, info, infos);
        }
    }
    LOG_D(BMS_TAG_QUERY, "finish to ImplicitQueryAllExtensionInfosV9");
}

void BundleDataMgr::GetExtensionAbilityInfoByTypeName(uint32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &infos, const std::string &typeName) const
{
    for (const auto &item : bundleInfos_) {
        if ((flags &
                static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_BY_TYPE_NAME)) ==
                static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_BY_TYPE_NAME)) {
            if (item.second.GetInnerExtensionInfos().empty() || !item.second.IsSystemApp()) {
                continue;
            }
            bool ret = GetInnerBundleInfoWithFlags(item.first, flags, userId);
            if (!ret) {
                LOG_D(BMS_TAG_QUERY, "GetInnerBundleInfoWithFlagsV9 failed, bundleName:%{public}s",
                    item.first.c_str());
                continue;
            }
            int32_t responseUserId = item.second.GetResponseUserId(userId);
            GetOneExtensionInfosByExtensionTypeName(typeName, flags, responseUserId, item.second, infos);
            if (infos.size() > 0) {
                return;
            }
        } else {
            const InnerBundleInfo* innerBundleInfo = nullptr;
            bool ret = GetInnerBundleInfoWithFlags(item.first, flags, innerBundleInfo, userId);
            if (!ret) {
                LOG_D(BMS_TAG_QUERY, "GetInnerBundleInfoWithFlagsV9 failed, bundleName:%{public}s",
                    item.first.c_str());
                continue;
            }
            if (!innerBundleInfo) {
                LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetExtensionAbilityInfoByTypeName is null.");
                continue;
            }
            int32_t responseUserId = innerBundleInfo->GetResponseUserId(userId);
            GetAllExtensionInfos(flags, responseUserId, *innerBundleInfo, infos);
        }
    }
}

ErrCode BundleDataMgr::ImplicitQueryAllExtensionInfos(uint32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &infos, int32_t appIndex, const std::string &typeName) const
{
    LOG_D(BMS_TAG_QUERY, "begin to ImplicitQueryAllExtensionInfos");
    // query from bundleInfos_
    if (appIndex == 0) {
        GetExtensionAbilityInfoByTypeName(flags, userId, infos, typeName);
    } else if (appIndex > Constants::INITIAL_SANDBOX_APP_INDEX) {
        // query from sandbox manager for sandbox bundle
        if (sandboxAppHelper_ == nullptr) {
            LOG_W(BMS_TAG_QUERY, "sandboxAppHelper_ is nullptr");
            return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
        }
        auto sandboxMap = sandboxAppHelper_->GetSandboxAppInfoMap();
        for (const auto &item : sandboxMap) {
            InnerBundleInfo info;
            size_t pos = item.first.rfind(Constants::FILE_UNDERLINE);
            if (pos == std::string::npos) {
                LOG_W(BMS_TAG_QUERY, "sandbox map contains invalid element");
                continue;
            }
            std::string innerBundleName = item.first.substr(pos + 1);
            if (sandboxAppHelper_->GetSandboxAppInfo(innerBundleName, appIndex, userId, info) != ERR_OK) {
                LOG_D(BMS_TAG_QUERY, "obtain innerBundleInfo of sandbox app failed");
                continue;
            }
            int32_t responseUserId = info.GetResponseUserId(userId);
            GetAllExtensionInfos(flags, responseUserId, info, infos, appIndex);
        }
    } else if (appIndex > 0 && appIndex <= Constants::INITIAL_SANDBOX_APP_INDEX) {
        for (const auto &item : bundleInfos_) {
            const InnerBundleInfo* innerBundleInfo = nullptr;
            bool ret = GetInnerBundleInfoWithFlags(item.first, flags, innerBundleInfo, userId, appIndex);
            if (!ret) {
                LOG_D(BMS_TAG_QUERY, "GetInnerBundleInfoWithFlagsV9 failed, bundleName:%{public}s",
                    item.first.c_str());
                continue;
            }
            if (!innerBundleInfo) {
                LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ImplicitQueryAllExtensionInfos is null.");
                continue;
            }
            int32_t responseUserId = innerBundleInfo->GetResponseUserId(userId);
            GetAllExtensionInfos(flags, responseUserId, *innerBundleInfo, infos, appIndex);
        }
    }
    LOG_D(BMS_TAG_QUERY, "finish to ImplicitQueryAllExtensionInfos");
    return ERR_OK;
}

void BundleDataMgr::GetMatchExtensionInfos(const Want &want, int32_t flags, const int32_t &userId,
    const InnerBundleInfo &info, std::vector<ExtensionAbilityInfo> &infos, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    auto extensionSkillInfos = info.GetExtensionSkillInfos();
    auto innerExtensionInfos = info.GetInnerExtensionInfos();
    for (const auto &skillInfos : extensionSkillInfos) {
        for (size_t skillIndex = 0; skillIndex < skillInfos.second.size(); ++skillIndex) {
            const Skill &skill = skillInfos.second[skillIndex];
            size_t matchUriIndex = 0;
            if (!skill.Match(want, matchUriIndex)) {
                continue;
            }
            if (innerExtensionInfos.find(skillInfos.first) == innerExtensionInfos.end()) {
                LOG_W(BMS_TAG_QUERY, "cannot find the extension info with %{public}s",
                    skillInfos.first.c_str());
                break;
            }
            ExtensionAbilityInfo extensionInfo =
                InnerExtensionInfo::ConvertToExtensionInfo(innerExtensionInfos[skillInfos.first]);
            if ((static_cast<uint32_t>(flags) & GET_EXTENSION_INFO_WITH_APPLICATION) ==
                GET_EXTENSION_INFO_WITH_APPLICATION) {
                info.GetApplicationInfo(
                    ApplicationFlag::GET_BASIC_APPLICATION_INFO |
                    ApplicationFlag::GET_APPLICATION_INFO_WITH_CERTIFICATE_FINGERPRINT, userId,
                    extensionInfo.applicationInfo);
            }
            if ((static_cast<uint32_t>(flags) & GET_EXTENSION_INFO_WITH_PERMISSION) !=
                GET_EXTENSION_INFO_WITH_PERMISSION) {
                extensionInfo.permissions.clear();
            }
            if ((static_cast<uint32_t>(flags) & GET_EXTENSION_INFO_WITH_METADATA) != GET_EXTENSION_INFO_WITH_METADATA) {
                extensionInfo.metadata.clear();
            }
            if ((static_cast<uint32_t>(flags) & GET_EXTENSION_INFO_WITH_SKILL) != GET_EXTENSION_INFO_WITH_SKILL) {
                extensionInfo.skills.clear();
            }
            if ((static_cast<uint32_t>(flags) &
                GET_EXTENSION_INFO_WITH_SKILL_URI) == GET_EXTENSION_INFO_WITH_SKILL_URI) {
                AddSkillUrisInfo(skillInfos.second, extensionInfo.skillUri, skillIndex, matchUriIndex);
            }
            extensionInfo.appIndex = appIndex;
            infos.emplace_back(std::move(extensionInfo));
            break;
        }
    }
}

void BundleDataMgr::EmplaceExtensionInfo(const InnerBundleInfo &info, const std::vector<Skill> &skills,
    ExtensionAbilityInfo &extensionInfo, int32_t flags, int32_t userId, std::vector<ExtensionAbilityInfo> &infos,
    std::optional<size_t> matchSkillIndex, std::optional<size_t> matchUriIndex, int32_t appIndex) const
{
    if ((static_cast<uint32_t>(flags) &
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_APPLICATION)) ==
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_APPLICATION)) {
        info.GetApplicationInfoV9(static_cast<int32_t>(
            GetApplicationFlag::GET_APPLICATION_INFO_DEFAULT), userId, extensionInfo.applicationInfo, appIndex);
    }
    if ((static_cast<uint32_t>(flags) &
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_PERMISSION)) !=
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_PERMISSION)) {
        extensionInfo.permissions.clear();
    }
    if ((static_cast<uint32_t>(flags) &
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_METADATA)) !=
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_METADATA)) {
        extensionInfo.metadata.clear();
    }
    if ((static_cast<uint32_t>(flags) &
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_SKILL)) !=
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_SKILL)) {
        extensionInfo.skills.clear();
    }
    if ((static_cast<uint32_t>(flags) &
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_SKILL_URI)) ==
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_SKILL_URI)) {
        AddSkillUrisInfo(skills, extensionInfo.skillUri, matchSkillIndex, matchUriIndex);
    }
    extensionInfo.appIndex = appIndex;
    infos.emplace_back(extensionInfo);
}

void BundleDataMgr::GetMatchExtensionInfosV9(const Want &want, int32_t flags, int32_t userId,
    const InnerBundleInfo &info, std::vector<ExtensionAbilityInfo> &infos, int32_t appIndex) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    auto extensionSkillInfos = info.GetExtensionSkillInfos();
    auto innerExtensionInfos = info.GetInnerExtensionInfos();
    for (const auto &skillInfos : extensionSkillInfos) {
        if (want.GetAction() == SHARE_ACTION) {
            if (!MatchShare(want, skillInfos.second)) {
                continue;
            }
            if (innerExtensionInfos.find(skillInfos.first) == innerExtensionInfos.end()) {
                LOG_W(BMS_TAG_QUERY, "cannot find the extension info with %{public}s",
                    skillInfos.first.c_str());
                continue;
            }
            ExtensionAbilityInfo extensionInfo =
                InnerExtensionInfo::ConvertToExtensionInfo(innerExtensionInfos[skillInfos.first]);
            EmplaceExtensionInfo(info, skillInfos.second, extensionInfo, flags, userId, infos,
                std::nullopt, std::nullopt, appIndex);
            continue;
        }
        for (size_t skillIndex = 0; skillIndex < skillInfos.second.size(); ++skillIndex) {
            const Skill &skill = skillInfos.second[skillIndex];
            size_t matchUriIndex = 0;
            if (!skill.Match(want, matchUriIndex)) {
                continue;
            }
            if (innerExtensionInfos.find(skillInfos.first) == innerExtensionInfos.end()) {
                LOG_W(BMS_TAG_QUERY, "cannot find the extension info with %{public}s",
                    skillInfos.first.c_str());
                break;
            }
            ExtensionAbilityInfo extensionInfo =
                InnerExtensionInfo::ConvertToExtensionInfo(innerExtensionInfos[skillInfos.first]);
            EmplaceExtensionInfo(info, skillInfos.second, extensionInfo, flags, userId, infos,
                skillIndex, matchUriIndex, appIndex);
            break;
        }
    }
}

void BundleDataMgr::GetAllExtensionInfos(uint32_t flags, int32_t userId,
    const InnerBundleInfo &info, std::vector<ExtensionAbilityInfo> &infos, int32_t appIndex) const
{
    auto innerExtensionInfos = info.GetInnerExtensionInfos();
    for (const auto &item : innerExtensionInfos) {
        ExtensionAbilityInfo extensionInfo = InnerExtensionInfo::ConvertToExtensionInfo(item.second);
        if ((flags &
            static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_APPLICATION)) ==
            static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_APPLICATION)) {
            info.GetApplicationInfoV9(static_cast<int32_t>(
                GetApplicationFlag::GET_APPLICATION_INFO_DEFAULT), userId, extensionInfo.applicationInfo, appIndex);
        }
        if ((flags &
            static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_PERMISSION)) !=
            static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_PERMISSION)) {
            extensionInfo.permissions.clear();
        }
        if ((flags &
            static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_METADATA)) !=
            static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_METADATA)) {
            extensionInfo.metadata.clear();
        }
        if ((flags &
            static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_SKILL)) !=
            static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_WITH_SKILL)) {
            extensionInfo.skills.clear();
        }
        extensionInfo.appIndex = appIndex;
        infos.emplace_back(extensionInfo);
    }
}

bool BundleDataMgr::QueryExtensionAbilityInfos(const ExtensionAbilityType &extensionType, const int32_t &userId,
    std::vector<ExtensionAbilityInfo> &extensionInfos) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        LOG_E(BMS_TAG_QUERY, "invalid userId, userId:%{public}d", requestUserId);
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &innerBundleInfo = item.second;
        int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
        if (CheckInnerBundleInfoWithFlags(innerBundleInfo, 0, responseUserId) != ERR_OK) {
            continue;
        }
        auto innerExtensionInfos = innerBundleInfo.GetInnerExtensionInfos();
        for (const auto &info : innerExtensionInfos) {
            if (info.second.type == extensionType) {
                ExtensionAbilityInfo extensionAbilityInfo = InnerExtensionInfo::ConvertToExtensionInfo(info.second);
                innerBundleInfo.GetApplicationInfo(
                    ApplicationFlag::GET_APPLICATION_INFO_WITH_CERTIFICATE_FINGERPRINT, responseUserId,
                    extensionAbilityInfo.applicationInfo);
                extensionInfos.emplace_back(extensionAbilityInfo);
            }
        }
    }
    return true;
}

bool BundleDataMgr::QueryExtensionAbilityInfoByUri(const std::string &uri, int32_t userId,
    ExtensionAbilityInfo &extensionAbilityInfo) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        LOG_W(BMS_TAG_QUERY, "invalid userId -1");
        return false;
    }
    if (uri.empty()) {
        LOG_W(BMS_TAG_QUERY, "uri empty");
        return false;
    }
    std::string convertUri = uri;
    // example of valid param uri : fileShare:///com.example.FileShare/person/10
    // example of convertUri : fileShare://com.example.FileShare
    size_t schemePos = uri.find(PARAM_URI_SEPARATOR);
    if (schemePos != uri.npos) {
        // 1. cut string
        size_t cutPos = uri.find(ServiceConstants::PATH_SEPARATOR, schemePos + PARAM_URI_SEPARATOR_LEN);
        if (cutPos != uri.npos) {
            convertUri = uri.substr(0, cutPos);
        }
        // 2. replace :/// with ://
        convertUri.replace(schemePos, PARAM_URI_SEPARATOR_LEN, URI_SEPARATOR);
    } else {
        if (convertUri.compare(0, DATA_PROXY_URI_PREFIX_LEN, DATA_PROXY_URI_PREFIX) != 0) {
            LOG_W(BMS_TAG_QUERY, "invalid uri : %{private}s", uri.c_str());
            return false;
        }
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        LOG_W(BMS_TAG_QUERY, "bundleInfos_ data is empty, uri:%{public}s", uri.c_str());
        return false;
    }
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &info = item.second;
        if (info.IsDisabled()) {
            LOG_D(BMS_TAG_QUERY, "app %{public}s is disabled", info.GetBundleName().c_str());
            continue;
        }

        int32_t responseUserId = info.GetResponseUserId(requestUserId);
        if (!info.GetApplicationEnabled(responseUserId)) {
            continue;
        }

        bool ret = info.FindExtensionAbilityInfoByUri(convertUri, extensionAbilityInfo);
        if (!ret) {
            continue;
        }
        info.GetApplicationInfo(
            ApplicationFlag::GET_APPLICATION_INFO_WITH_CERTIFICATE_FINGERPRINT, responseUserId,
            extensionAbilityInfo.applicationInfo);
        return true;
    }
    LOG_NOFUNC_W(BMS_TAG_QUERY, "QueryExtensionAbilityInfoByUri (%{public}s) failed", convertUri.c_str());
    return false;
}

std::string BundleDataMgr::GetStringById(const std::string &bundleName, const std::string &moduleName,
    uint32_t resId, int32_t userId, const std::string &localeInfo)
{
    APP_LOGD("GetStringById:%{public}s , %{public}s, %{public}d", bundleName.c_str(), moduleName.c_str(), resId);
#ifdef GLOBAL_RESMGR_ENABLE
    std::shared_ptr<OHOS::Global::Resource::ResourceManager> resourceManager =
        GetResourceManager(bundleName, moduleName, userId);
    if (resourceManager == nullptr) {
        APP_LOGW("InitResourceManager failed");
        return Constants::EMPTY_STRING;
    }
    std::string label;
    OHOS::Global::Resource::RState errValue = resourceManager->GetStringById(resId, label);
    if (errValue != OHOS::Global::Resource::RState::SUCCESS) {
        APP_LOGW("GetStringById failed, bundleName:%{public}s, id:%{public}d", bundleName.c_str(), resId);
        return Constants::EMPTY_STRING;
    }
    return label;
#else
    APP_LOGW("GLOBAL_RESMGR_ENABLE is false");
    return Constants::EMPTY_STRING;
#endif
}

std::string BundleDataMgr::GetIconById(
    const std::string &bundleName, const std::string &moduleName, uint32_t resId, uint32_t density, int32_t userId)
{
    APP_LOGI("GetIconById bundleName:%{public}s, moduleName:%{public}s, resId:%{public}d, density:%{public}d",
        bundleName.c_str(), moduleName.c_str(), resId, density);
#ifdef GLOBAL_RESMGR_ENABLE
    std::shared_ptr<OHOS::Global::Resource::ResourceManager> resourceManager =
        GetResourceManager(bundleName, moduleName, userId);
    if (resourceManager == nullptr) {
        APP_LOGW("InitResourceManager failed");
        return Constants::EMPTY_STRING;
    }
    std::string base64;
    OHOS::Global::Resource::RState errValue = resourceManager->GetMediaBase64DataById(resId, base64, density);
    if (errValue != OHOS::Global::Resource::RState::SUCCESS) {
        APP_LOGW("GetIconById failed, bundleName:%{public}s, id:%{public}d", bundleName.c_str(), resId);
        return Constants::EMPTY_STRING;
    }
    return base64;
#else
    APP_LOGW("GLOBAL_RESMGR_ENABLE is false");
    return Constants::EMPTY_STRING;
#endif
}

#ifdef GLOBAL_RESMGR_ENABLE
std::shared_ptr<Global::Resource::ResourceManager> BundleDataMgr::GetResourceManager(
    const std::string &bundleName, const std::string &moduleName, int32_t userId, const std::string &localeInfo) const
{
    BundleInfo bundleInfo;
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        const InnerBundleInfo* innerBundleInfo = nullptr;
        if (!GetInnerBundleInfoWithFlags(bundleName, BundleFlag::GET_BUNDLE_DEFAULT, innerBundleInfo, userId)) {
            APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
            return nullptr;
        }
        if (!innerBundleInfo) {
            LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetResourceManager is null.");
            return nullptr;
        }
        int32_t responseUserId = innerBundleInfo->GetResponseUserId(userId);
        innerBundleInfo->GetBundleInfo(BundleFlag::GET_BUNDLE_DEFAULT, bundleInfo, responseUserId);
    }
    std::shared_ptr<Global::Resource::ResourceManager> resourceManager(Global::Resource::CreateResourceManager());
    if (resourceManager == nullptr) {
        APP_LOGE("InitResourceManager failed, bundleName:%{public}s", bundleName.c_str());
        return nullptr;
    }

    std::unique_ptr<Global::Resource::ResConfig> resConfig(Global::Resource::CreateResConfig());
    if (!resConfig) {
        APP_LOGE("resConfig is nullptr");
        return nullptr;
    }
#ifdef GLOBAL_I18_ENABLE
    std::map<std::string, std::string> configs;
    OHOS::Global::I18n::LocaleInfo locale(
        localeInfo.empty() ? Global::I18n::LocaleConfig::GetEffectiveLanguage() : localeInfo, configs);
    resConfig->SetLocaleInfo(locale.GetLanguage().c_str(), locale.GetScript().c_str(), locale.GetRegion().c_str());
#endif
    resourceManager->UpdateResConfig(*resConfig);

    for (auto hapModuleInfo : bundleInfo.hapModuleInfos) {
        std::string moduleResPath;
        if (moduleName.empty() || moduleName == hapModuleInfo.moduleName) {
            moduleResPath = hapModuleInfo.hapPath.empty() ? hapModuleInfo.resourcePath : hapModuleInfo.hapPath;
        }
        if (!moduleResPath.empty()) {
            APP_LOGD("DistributedBms::InitResourceManager, moduleResPath: %{public}s", moduleResPath.c_str());
            if (!resourceManager->AddResource(moduleResPath.c_str(), Global::Resource::SELECT_STRING
            | Global::Resource::SELECT_MEDIA)) {
                APP_LOGW("DistributedBms::InitResourceManager AddResource failed");
            }
        }
    }
    return resourceManager;
}
#endif

const std::vector<PreInstallBundleInfo> BundleDataMgr::GetAllPreInstallBundleInfos()
{
    std::vector<PreInstallBundleInfo> preInstallBundleInfos;
    LoadAllPreInstallBundleInfos(preInstallBundleInfos);
    return preInstallBundleInfos;
}

bool BundleDataMgr::ImplicitQueryInfoByPriority(const Want &want, int32_t flags, int32_t userId,
    AbilityInfo &abilityInfo, ExtensionAbilityInfo &extensionInfo) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGW("invalid userId: %{public}d", userId);
        return false;
    }
    std::vector<AbilityInfo> abilityInfos;
    bool abilityValid =
        ImplicitQueryAbilityInfos(want, flags, requestUserId, abilityInfos) && (abilityInfos.size() > 0);
    std::vector<ExtensionAbilityInfo> extensionInfos;
    bool extensionValid =
        ImplicitQueryExtensionInfos(want, flags, requestUserId, extensionInfos) && (extensionInfos.size() > 0);
    if (!abilityValid && !extensionValid) {
        // both invalid
        APP_LOGW("can't find target AbilityInfo or ExtensionAbilityInfo");
        return false;
    }
    if (abilityValid && extensionValid) {
        // both valid
        if (abilityInfos[0].priority >= extensionInfos[0].priority) {
            APP_LOGD("find target AbilityInfo with higher priority, name : %{public}s", abilityInfos[0].name.c_str());
            abilityInfo = abilityInfos[0];
        } else {
            APP_LOGD("find target ExtensionAbilityInfo with higher priority, name : %{public}s",
                extensionInfos[0].name.c_str());
            extensionInfo = extensionInfos[0];
        }
    } else if (abilityValid) {
        // only ability valid
        APP_LOGD("find target AbilityInfo, name : %{public}s", abilityInfos[0].name.c_str());
        abilityInfo = abilityInfos[0];
    } else {
        // only extension valid
        APP_LOGD("find target ExtensionAbilityInfo, name : %{public}s", extensionInfos[0].name.c_str());
        extensionInfo = extensionInfos[0];
    }
    return true;
}

bool BundleDataMgr::ImplicitQueryInfos(const Want &want, int32_t flags, int32_t userId, bool withDefault,
    std::vector<AbilityInfo> &abilityInfos, std::vector<ExtensionAbilityInfo> &extensionInfos, bool &findDefaultApp)
{
    APP_LOGI_NOFUNC("ImplicitQueryInfos action:%{public}s uri:%{private}s type:%{public}s flags:%{public}d "
        "userId:%{public}d withDefault:%{public}d", want.GetAction().c_str(), want.GetUriString().c_str(),
        want.GetType().c_str(), flags, userId, withDefault);
#ifdef BUNDLE_FRAMEWORK_DEFAULT_APP
    // step1 : find default infos
    if (withDefault && DefaultAppMgr::GetInstance().GetDefaultApplication(want, userId, abilityInfos, extensionInfos)) {
        FilterAbilityInfosByAppLinking(want, flags, abilityInfos);
        if (!abilityInfos.empty() || !extensionInfos.empty()) {
            APP_LOGI("find target default application");
            findDefaultApp = true;
            if (want.GetUriString().rfind(SCHEME_HTTPS, 0) != 0) {
                return true;
            }
            for (auto &info : abilityInfos) {
                info.linkType = LinkType::DEFAULT_APP;
            }
        }
    }
    // step2 : find backup default infos
    if (withDefault &&
        DefaultAppMgr::GetInstance().GetDefaultApplication(want, userId, abilityInfos, extensionInfos, true)) {
        FilterAbilityInfosByAppLinking(want, flags, abilityInfos);
        if (!abilityInfos.empty() || !extensionInfos.empty()) {
            APP_LOGI("find target backup default application");
            findDefaultApp = true;
            if (want.GetUriString().rfind(SCHEME_HTTPS, 0) != 0) {
                return true;
            }
            for (auto &info : abilityInfos) {
                info.linkType = LinkType::DEFAULT_APP;
            }
        }
    }
#endif
    // step3 : implicit query infos
    bool abilityRet =
        ImplicitQueryAbilityInfos(want, flags, userId, abilityInfos) && (abilityInfos.size() > 0);
    APP_LOGD("abilityRet: %{public}d, abilityInfos size: %{public}zu", abilityRet, abilityInfos.size());

    bool extensionRet =
        ImplicitQueryExtensionInfos(want, flags, userId, extensionInfos) && (extensionInfos.size() > 0);
    APP_LOGD("extensionRet: %{public}d, extensionInfos size: %{public}zu", extensionRet, extensionInfos.size());

    ImplicitQueryCloneAbilityInfos(want, flags, userId, abilityInfos);
    return abilityRet || extensionRet || abilityInfos.size() > 0;
}

bool BundleDataMgr::GetAllDependentModuleNames(const std::string &bundleName, const std::string &moduleName,
    std::vector<std::string> &dependentModuleNames)
{
    APP_LOGD("GetAllDependentModuleNames bundleName: %{public}s, moduleName: %{public}s",
        bundleName.c_str(), moduleName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGW("GetAllDependentModuleNames: bundleName:%{public}s not find", bundleName.c_str());
        return false;
    }
    const InnerBundleInfo &innerBundleInfo = item->second;
    return innerBundleInfo.GetAllDependentModuleNames(moduleName, dependentModuleNames);
}

void BundleDataMgr::UpdateRemovable(
    const std::string &bundleName, bool removable)
{
    APP_LOGD("UpdateRemovable %{public}s", bundleName.c_str());
    if (bundleName.empty()) {
        APP_LOGW("bundleName is empty");
        return;
    }

    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return;
    }

    if (infoItem->second.IsRemovable() != removable) {
        infoItem->second.UpdateRemovable(true, removable);
        SaveInnerBundleInfo(infoItem->second);
    }
}

void BundleDataMgr::UpdatePrivilegeCapability(
    const std::string &bundleName, const ApplicationInfo &appInfo)
{
    APP_LOGD("UpdatePrivilegeCapability %{public}s", bundleName.c_str());
    if (bundleName.empty()) {
        APP_LOGW("bundleName is empty");
        return;
    }

    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return;
    }

    infoItem->second.UpdatePrivilegeCapability(appInfo);
}

bool BundleDataMgr::FetchInnerBundleInfo(
    const std::string &bundleName, InnerBundleInfo &innerBundleInfo)
{
    APP_LOGD("FetchInnerBundleInfo %{public}s", bundleName.c_str());
    if (bundleName.empty()) {
        APP_LOGW("bundleName is empty");
        return false;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW_NOFUNC("FetchInnerBundleInfo not found %{public}s", bundleName.c_str());
        return false;
    }

    innerBundleInfo = infoItem->second;
    return true;
}

bool BundleDataMgr::GetInnerBundleInfoUsers(const std::string &bundleName, std::set<int32_t> &userIds)
{
    InnerBundleInfo info;
    if (!FetchInnerBundleInfo(bundleName, info)) {
        APP_LOGW("FetchInnerBundleInfo failed");
        return false;
    }
    std::map<std::string, InnerBundleUserInfo> userInfos = info.GetInnerBundleUserInfos();
    for (const auto &userInfo : userInfos) {
        userIds.insert(userInfo.second.bundleUserInfo.userId);
    }
    return true;
}

bool BundleDataMgr::IsSystemHsp(const std::string &bundleName)
{
    InnerBundleInfo info;
    if (!FetchInnerBundleInfo(bundleName, info)) {
        APP_LOGW("FetchInnerBundleInfo %{public}s failed", bundleName.c_str());
        return false;
    }
    return info.GetApplicationBundleType() == BundleType::APP_SERVICE_FWK;
}

#ifdef BUNDLE_FRAMEWORK_DEFAULT_APP
bool BundleDataMgr::QueryInfoAndSkillsByElement(int32_t userId, const Element& element,
    AbilityInfo& abilityInfo, ExtensionAbilityInfo& extensionInfo, std::vector<Skill>& skills) const
{
    APP_LOGD("begin to QueryInfoAndSkillsByElement");
    const std::string& bundleName = element.bundleName;
    const std::string& moduleName = element.moduleName;
    const std::string& abilityName = element.abilityName;
    const std::string& extensionName = element.extensionName;
    const int32_t appIndex = element.appIndex;
    Want want;
    ElementName elementName("", bundleName, abilityName, moduleName);
    want.SetElement(elementName);
    bool isAbility = !element.abilityName.empty();

    if (isAbility) {
        // get ability info
        ErrCode ret = ExplicitQueryCloneAbilityInfoV9(elementName,
            static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_DEFAULT), userId, appIndex, abilityInfo);
        if (ret != ERR_OK) {
            LOG_I(BMS_TAG_QUERY, "ExplicitQueryAbility no match -n %{public}s -m %{public}s -a %{public}s"
                " -u %{public}d", bundleName.c_str(), moduleName.c_str(), abilityName.c_str(), userId);
            return false;
        }
    } else {
        // get extension info
        elementName.SetAbilityName(extensionName);
        want.SetElement(elementName);
        ErrCode ret = ExplicitQueryExtensionInfoV9(want,
            static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_DEFAULT),
            userId, extensionInfo, appIndex);
        if (ret != ERR_OK) {
            APP_LOGD("ExplicitQueryExtensionInfo failed, extensionName:%{public}s", extensionName.c_str());
            return false;
        }
    }

    // get skills info
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ is empty");
        return false;
    }
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGW("can't find bundleName : %{public}s", bundleName.c_str());
        return false;
    }
    const InnerBundleInfo& innerBundleInfo = item->second;
    if (isAbility) {
        std::string key;
        key.append(bundleName).append(".").append(abilityInfo.package).append(".").append(abilityName);
        APP_LOGD("begin to find ability skills, key : %{public}s", key.c_str());
        for (const auto& infoItem : innerBundleInfo.GetInnerSkillInfos()) {
            if (infoItem.first == key) {
                skills = infoItem.second;
                APP_LOGD("find ability skills success");
                break;
            }
        }
    } else {
        std::string key;
        key.append(bundleName).append(".").append(moduleName).append(".").append(extensionName);
        APP_LOGD("begin to find extension skills, key : %{public}s", key.c_str());
        for (const auto& infoItem : innerBundleInfo.GetExtensionSkillInfos()) {
            if (infoItem.first == key) {
                skills = infoItem.second;
                APP_LOGD("find extension skills success");
                break;
            }
        }
    }
    APP_LOGD("QueryInfoAndSkillsByElement success");
    return true;
}

bool BundleDataMgr::GetElement(int32_t userId, const int32_t appIndex, const ElementName& elementName, 
    Element& element) const
{
    APP_LOGD("begin to GetElement");
    const std::string& bundleName = elementName.GetBundleName();
    const std::string& moduleName = elementName.GetModuleName();
    const std::string& abilityName = elementName.GetAbilityName();
    if (bundleName.empty() || moduleName.empty() || abilityName.empty()) {
        APP_LOGW("bundleName or moduleName or abilityName is empty");
        return false;
    }
    Want want;
    want.SetElement(elementName);
    AbilityInfo abilityInfo;
    ErrCode ret = ExplicitQueryCloneAbilityInfoV9(elementName,
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_DEFAULT), userId, appIndex, abilityInfo);
    if (ret == ERR_OK) {
        APP_LOGD("ElementName is ability");
        element.bundleName = bundleName;
        element.moduleName = moduleName;
        element.abilityName = abilityName;
        element.appIndex = appIndex;
        return true;
    }

    ExtensionAbilityInfo extensionInfo;
    ret = ExplicitQueryExtensionInfoV9(want,
        static_cast<uint32_t>(GetExtensionAbilityInfoFlag::GET_EXTENSION_ABILITY_INFO_DEFAULT),
        userId, extensionInfo, appIndex);
    if (ret == ERR_OK) {
        APP_LOGD("ElementName is extension");
        element.bundleName = bundleName;
        element.moduleName = moduleName;
        element.extensionName = abilityName;
        element.appIndex = appIndex;
        return true;
    }

    if (DelayedSingleton<BundleMgrService>::GetInstance()->IsBrokerServiceStarted()) {
        APP_LOGI("query ability from broker");
        AbilityInfo brokerAbilityInfo;
        auto bmsExtensionClient = std::make_shared<BmsExtensionClient>();
        ErrCode resultCode = bmsExtensionClient->QueryAbilityInfo(want, 0, userId, brokerAbilityInfo, true);
        if (resultCode == ERR_OK) {
            APP_LOGI("ElementName is brokerAbility");
            element.bundleName = bundleName;
            element.moduleName = moduleName;
            element.abilityName = abilityName;
            return true;
        }
    }

    APP_LOGW("ElementName doesn't exist");
    return false;
}
#endif

ErrCode BundleDataMgr::GetMediaData(const std::string &bundleName, const std::string &moduleName,
    const std::string &abilityName, std::unique_ptr<uint8_t[]> &mediaDataPtr, size_t &len, int32_t userId) const
{
    APP_LOGI("begin");
#ifdef GLOBAL_RESMGR_ENABLE
    AbilityInfo abilityInfo;
    int32_t responseUserId;
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        int32_t requestUserId = GetUserId(userId);
        if (requestUserId == Constants::INVALID_USERID) {
            LOG_E(BMS_TAG_QUERY, "The requestUserId is invalid.");
            return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
        }
        const InnerBundleInfo* innerBundleInfo = nullptr;
        ErrCode errCode = GetInnerBundleInfoWithFlagsV9(
            bundleName, BundleFlag::GET_BUNDLE_DEFAULT, innerBundleInfo, requestUserId);
        if (errCode != ERR_OK) {
            LOG_E(BMS_TAG_QUERY, "GetInnerBundleInfoWithFlagsV9 failed, bundleName:%{public}s",
                bundleName.c_str());
            return errCode;
        }
        if (!innerBundleInfo) {
            LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetMediaData is null.");
            return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
        }
        errCode = FindAbilityInfoInBundleInfo(*innerBundleInfo, moduleName, abilityName, abilityInfo);
        if (errCode != ERR_OK) {
            APP_LOGE("Find ability failed. bundleName: %{public}s, moduleName: %{public}s, abilityName: %{public}s",
                bundleName.c_str(), moduleName.c_str(), abilityName.c_str());
            return errCode;
        }
        bool isEnable = false;
        responseUserId = innerBundleInfo->GetResponseUserId(requestUserId);
        errCode = innerBundleInfo->IsAbilityEnabledV9(abilityInfo, responseUserId, isEnable);
        if (errCode != ERR_OK) {
            LOG_E(BMS_TAG_QUERY, "The IsAbilityEnabledV9 not enabled.");
            return errCode;
        }
        if (!isEnable) {
            APP_LOGE("%{public}s ability disabled: %{public}s", bundleName.c_str(), abilityName.c_str());
            return ERR_BUNDLE_MANAGER_ABILITY_DISABLED;
        }
    }
    // adapter project: iconPath fallback when iconId=0
    // Android-APK bundles registered without OH resources.index have iconId=0.
    // Read PNG bytes from abilityInfo.iconPath directly, bypass ResourceManager.
    if (abilityInfo.iconId == 0 && !abilityInfo.iconPath.empty()) {
        std::ifstream ifs(abilityInfo.iconPath, std::ios::binary | std::ios::ate);
        if (ifs) {
            std::streamsize sz = ifs.tellg();
            if (sz > 0 && sz < (10 * 1024 * 1024)) {
                ifs.seekg(0);
                mediaDataPtr = std::make_unique<uint8_t[]>(static_cast<size_t>(sz));
                if (ifs.read(reinterpret_cast<char*>(mediaDataPtr.get()), sz)) {
                    len = static_cast<size_t>(sz);
                    APP_LOGI("adapter iconPath fallback: %{public}s (%{public}zu bytes)",
                        abilityInfo.iconPath.c_str(), len);
                    return ERR_OK;
                }
                mediaDataPtr.reset();
            }
        }
        APP_LOGW("adapter iconPath fallback failed for %{public}s", abilityInfo.iconPath.c_str());
    }
    std::shared_ptr<Global::Resource::ResourceManager> resourceManager =
        GetResourceManager(bundleName, abilityInfo.moduleName, responseUserId);
    if (resourceManager == nullptr) {
        APP_LOGE("InitResourceManager failed, bundleName:%{public}s", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    OHOS::Global::Resource::RState ret =
        resourceManager->GetMediaDataById(static_cast<uint32_t>(abilityInfo.iconId), len, mediaDataPtr);
    if (ret != OHOS::Global::Resource::RState::SUCCESS || mediaDataPtr == nullptr || len == 0) {
        APP_LOGE("GetMediaDataById failed, bundleName:%{public}s", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    return ERR_OK;
#else
    return ERR_BUNDLE_MANAGER_GLOBAL_RES_MGR_ENABLE_DISABLED;
#endif
}

std::shared_mutex &BundleDataMgr::GetStatusCallbackMutex()
{
    return callbackMutex_;
}

std::vector<sptr<IBundleStatusCallback>> BundleDataMgr::GetCallBackList() const
{
    return callbackList_;
}

bool BundleDataMgr::UpdateQuickFixInnerBundleInfo(const std::string &bundleName,
    const InnerBundleInfo &innerBundleInfo)
{
    APP_LOGD("to update info:%{public}s", bundleName.c_str());
    if (bundleName.empty()) {
        APP_LOGW("update info fail, empty bundle name");
        return false;
    }

    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("bundle:%{public}s info is not existed", bundleName.c_str());
        return false;
    }

    if (dataStorage_->SaveStorageBundleInfo(innerBundleInfo)) {
        bundleInfos_.at(bundleName) = innerBundleInfo;
        return true;
    }
    APP_LOGE("to update info:%{public}s failed", bundleName.c_str());
    return false;
}

bool BundleDataMgr::UpdateInnerBundleInfo(InnerBundleInfo &innerBundleInfo, bool needSaveStorage)
{
    std::string bundleName = innerBundleInfo.GetBundleName();
    if (bundleName.empty()) {
        APP_LOGW("UpdateInnerBundleInfo failed, empty bundle name");
        return false;
    }
    APP_LOGD("UpdateInnerBundleInfo:%{public}s", bundleName.c_str());
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("bundle:%{public}s info is not existed", bundleName.c_str());
        return false;
    }
    std::string developerId = innerBundleInfo.GetDeveloperId();
    if (!developerId.empty()) {
        // Read lastOdid from FirstInstallBundleInfo using ALL_USERID to make ODID reset count
        // independent of userId, only associated with bundleName
        std::string lastOdid;
        FirstInstallBundleInfo lastOdidInfo;
        if (GetFirstInstallBundleInfo(bundleName, Constants::ALL_USERID, lastOdidInfo)) {
            lastOdid = lastOdidInfo.lastOdid;
        }
        std::string odid = GenerateOdidNoLock(developerId);
        innerBundleInfo.UpdateOdid(developerId, odid);
        // Increment odid reset count when generating new odid different from last odid
        if (!lastOdid.empty() && !odid.empty() && odid != lastOdid) {
            lastOdidInfo.IncrementOdidResetCount();
            APP_LOGI("odid reset for bundle %{public}s, last odid:%{private}s, new odid:%{private}s, count:%{public}d",
                bundleName.c_str(), lastOdid.c_str(), odid.c_str(), lastOdidInfo.odidResetCount);
            firstInstallDataMgr_->AddFirstInstallBundleInfo(bundleName, Constants::ALL_USERID, lastOdidInfo);
        }
    }
    if (needSaveStorage && !dataStorage_->SaveStorageBundleInfo(innerBundleInfo)) {
        APP_LOGE("to update InnerBundleInfo:%{public}s failed", bundleName.c_str());
        return false;
    }
    bundleInfos_.at(bundleName) = innerBundleInfo;
    return true;
}

bool BundleDataMgr::UpdatePartialInnerBundleInfo(const InnerBundleInfo &info)
{
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    std::string bundleName = info.GetBundleName();
    if (bundleName.empty()) {
        APP_LOGE("bundle name empty");
        return false;
    }
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("%{public}s not exist", bundleName.c_str());
        return false;
    }
    item->second.UpdatePartialInnerBundleInfo(info);
    if (!dataStorage_->SaveStorageBundleInfo(item->second)) {
        APP_LOGE("save %{public}s to db failed", bundleName.c_str());
        return false;
    }
    return true;
}

bool BundleDataMgr::QueryOverlayInnerBundleInfo(const std::string &bundleName, InnerBundleInfo &info)
{
    APP_LOGD("start to query overlay innerBundleInfo");
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.find(bundleName) != bundleInfos_.end()) {
        info = bundleInfos_.at(bundleName);
        return true;
    }

    APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
    return false;
}

void BundleDataMgr::SaveOverlayInfo(const std::string &bundleName, InnerBundleInfo &innerBundleInfo)
{
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    innerBundleInfo.SetBundleStatus(InnerBundleInfo::BundleStatus::ENABLED);
    if (!dataStorage_->SaveStorageBundleInfo(innerBundleInfo)) {
        APP_LOGE("update storage failed bundle:%{public}s", bundleName.c_str());
        return;
    }
    bundleInfos_.at(bundleName) = innerBundleInfo;
}

void BundleDataMgr::GetBundleNameList(const int32_t userId, std::vector<std::string>& bundleNameList)
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto& [bundleName, infoItem] : bundleInfos_) {
        if (infoItem.GetApplicationBundleType() == BundleType::SHARED) {
                continue;
        } else {
            int32_t responseUserId = infoItem.GetResponseUserId(userId);
            if (responseUserId == Constants::INVALID_USERID) {
                continue;
            }
        }
        bundleNameList.emplace_back(bundleName);
    }
}

ErrCode BundleDataMgr::GetAllAppProvisionInfo(const int32_t userId, std::vector<AppProvisionInfo> &appProvisionInfos)
{
    if (!HasUserId(userId)) {
        APP_LOGW("GetAllAppProvisionInfo user is not existed.");
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::vector<std::string> bundleNameList;
    GetBundleNameList(userId, bundleNameList);
    for (const auto& bundleName : bundleNameList) {
        AppProvisionInfo appProvisionInfo;
        if (!DelayedSingleton<AppProvisionInfoManager>::GetInstance()->GetAppProvisionInfo(bundleName, appProvisionInfo)) {
            APP_LOGW("bundleName:%{public}s GetAllAppProvisionInfo failed", bundleName.c_str());
            continue;
        }
        appProvisionInfo.bundleName = bundleName;
        appProvisionInfos.emplace_back(appProvisionInfo);
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::GetAppProvisionInfo(const std::string &bundleName, int32_t userId,
    AppProvisionInfo &appProvisionInfo)
{
    if (!HasUserId(userId)) {
        APP_LOGW("GetAppProvisionInfo user is not existed. bundleName:%{public}s", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW_NOFUNC("-n %{public}s not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    if (infoItem->second.GetApplicationBundleType() != BundleType::SHARED) {
        int32_t responseUserId = infoItem->second.GetResponseUserId(userId);
        if (responseUserId == Constants::INVALID_USERID) {
            return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
        }
    }
    if (!DelayedSingleton<AppProvisionInfoManager>::GetInstance()->GetAppProvisionInfo(bundleName, appProvisionInfo)) {
        APP_LOGW("bundleName:%{public}s GetAppProvisionInfo failed", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    appProvisionInfo.bundleName = bundleName;
    return ERR_OK;
}

ErrCode BundleDataMgr::GetProvisionMetadata(const std::string &bundleName, int32_t userId,
    std::vector<Metadata> &provisionMetadatas) const
{
    // Reserved interface
    return ERR_OK;
}

ErrCode BundleDataMgr::GetAllSharedBundleInfo(std::vector<SharedBundleInfo> &sharedBundles) const
{
    APP_LOGD("GetAllSharedBundleInfo");
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);

    for (const auto& [key, innerBundleInfo] : bundleInfos_) {
        if (innerBundleInfo.GetApplicationBundleType() != BundleType::SHARED) {
            continue;
        }
        SharedBundleInfo sharedBundleInfo;
        innerBundleInfo.GetSharedBundleInfo(sharedBundleInfo);
        sharedBundles.emplace_back(sharedBundleInfo);
    }

    return ERR_OK;
}

ErrCode BundleDataMgr::GetSharedBundleInfo(const std::string &bundleName, const std::string &moduleName,
    std::vector<SharedBundleInfo> &sharedBundles)
{
    APP_LOGD("GetSharedBundleInfo");
    if (bundleName.empty() || moduleName.empty()) {
        APP_LOGW("bundleName or moduleName is empty");
        return ERR_BUNDLE_MANAGER_PARAM_ERROR;
    }

    std::vector<Dependency> dependencies;
    ErrCode errCode = GetSharedDependencies(bundleName, moduleName, dependencies);
    if (errCode != ERR_OK) {
        APP_LOGD("GetSharedDependencies failed errCode is %{public}d, bundleName:%{public}s",
            errCode, bundleName.c_str());
        return errCode;
    }

    for (const auto& dep : dependencies) {
        SharedBundleInfo sharedBundleInfo;
        errCode = GetSharedBundleInfoBySelf(dep.bundleName, sharedBundleInfo);
        if (errCode != ERR_OK) {
            APP_LOGD("GetSharedBundleInfoBySelf failed errCode is %{public}d, bundleName:%{public}s",
                errCode, bundleName.c_str());
            return errCode;
        }
        sharedBundles.emplace_back(sharedBundleInfo);
    }

    return ERR_OK;
}

ErrCode BundleDataMgr::GetSharedBundleInfoBySelf(const std::string &bundleName, SharedBundleInfo &sharedBundleInfo)
{
    APP_LOGD("GetSharedBundleInfoBySelf bundleName: %{public}s", bundleName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    return GetSharedBundleInfoBySelfNoLock(bundleName, sharedBundleInfo);
}

ErrCode BundleDataMgr::GetSharedBundleInfoBySelfNoLock(const std::string &bundleName,
    SharedBundleInfo &sharedBundleInfo)
{
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("GetSharedBundleInfoBySelf failed, can not find bundle %{public}s",
            bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    const InnerBundleInfo &innerBundleInfo = infoItem->second;
    if (innerBundleInfo.GetApplicationBundleType() != BundleType::SHARED) {
        APP_LOGW("GetSharedBundleInfoBySelf failed, the bundle(%{public}s) is not shared library",
            bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    innerBundleInfo.GetSharedBundleInfo(sharedBundleInfo);
    APP_LOGD("GetSharedBundleInfoBySelf(%{public}s) successfully)", bundleName.c_str());
    return ERR_OK;
}

ErrCode BundleDataMgr::GetSharedDependencies(const std::string &bundleName, const std::string &moduleName,
    std::vector<Dependency> &dependencies)
{
    APP_LOGD("GetSharedDependencies bundleName: %{public}s, moduleName: %{public}s",
        bundleName.c_str(), moduleName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGW("GetSharedDependencies failed, can not find bundle %{public}s", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    const InnerBundleInfo &innerBundleInfo = item->second;
    if (!innerBundleInfo.GetAllSharedDependencies(moduleName, dependencies)) {
        APP_LOGW("GetSharedDependencies failed, can not find module %{public}s", moduleName.c_str());
        return ERR_BUNDLE_MANAGER_MODULE_NOT_EXIST;
    }
    APP_LOGD("GetSharedDependencies(bundle %{public}s, module %{public}s) successfully)",
        bundleName.c_str(), moduleName.c_str());
    return ERR_OK;
}

bool BundleDataMgr::CheckHspVersionIsRelied(int32_t versionCode, const InnerBundleInfo &info) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    std::string hspBundleName = info.GetBundleName();
    if (versionCode == Constants::ALL_VERSIONCODE) {
        // uninstall hsp bundle, check other bundle denpendency
        return CheckHspBundleIsRelied(hspBundleName);
    }
    std::vector<std::string> hspModules = info.GetAllHspModuleNamesForVersion(static_cast<uint32_t>(versionCode));
    // check whether has higher version
    std::vector<uint32_t> versionCodes = info.GetAllHspVersion();
    for (const auto &item : versionCodes) {
        if (item > static_cast<uint32_t>(versionCode)) {
            return false;
        }
    }
    // check other bundle denpendency
    for (const auto &[bundleName, innerBundleInfo] : bundleInfos_) {
        if (bundleName == hspBundleName) {
            continue;
        }
        std::vector<Dependency> dependencyList = innerBundleInfo.GetDependencies();
        for (const auto &dependencyItem : dependencyList) {
            if (dependencyItem.bundleName == hspBundleName &&
                std::find(hspModules.begin(), hspModules.end(), dependencyItem.moduleName) != hspModules.end()) {
                return true;
            }
        }
    }
    return false;
}

bool BundleDataMgr::CheckHspBundleIsRelied(const std::string &hspBundleName) const
{
    for (const auto &[bundleName, innerBundleInfo] : bundleInfos_) {
        if (bundleName == hspBundleName) {
            continue;
        }
        std::vector<Dependency> dependencyList = innerBundleInfo.GetDependencies();
        for (const auto &dependencyItem : dependencyList) {
            if (dependencyItem.bundleName == hspBundleName) {
                return true;
            }
        }
    }
    return false;
}

ErrCode BundleDataMgr::GetSharedBundleInfo(const std::string &bundleName, int32_t flags, BundleInfo &bundleInfo)
{
    if (bundleName.empty()) {
        APP_LOGW("bundleName is empty");
        return ERR_BUNDLE_MANAGER_PARAM_ERROR;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    const InnerBundleInfo &innerBundleInfo = infoItem->second;
    innerBundleInfo.GetSharedBundleInfo(flags, bundleInfo);
    return ERR_OK;
}

bool BundleDataMgr::IsPreInstallApp(const std::string &bundleName)
{
    APP_LOGD("IsPreInstallApp bundleName: %{public}s", bundleName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGW("IsPreInstallApp failed, can not find bundle %{public}s",
            bundleName.c_str());
        return false;
    }
    return item->second.IsPreInstallApp();
}

ErrCode BundleDataMgr::GetProxyDataInfos(const std::string &bundleName, const std::string &moduleName,
    int32_t userId, std::vector<ProxyData> &proxyDatas) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* info = nullptr;
    auto ret = GetInnerBundleInfoWithBundleFlagsV9(
        bundleName, static_cast<int32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_HAP_MODULE), info, userId);
    if (ret != ERR_OK) {
        APP_LOGD("GetProxyData failed for GetInnerBundleInfo failed, bundleName:%{public}s", bundleName.c_str());
        return ret;
    }
    if (!info) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetProxyDataInfos is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    return info->GetProxyDataInfos(moduleName, proxyDatas);
}

ErrCode BundleDataMgr::GetAllProxyDataInfos(int32_t userId, std::vector<ProxyData> &proxyDatas) const
{
    std::vector<BundleInfo> bundleInfos;
    auto ret = GetBundleInfosV9(
        static_cast<int32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_HAP_MODULE), bundleInfos, userId);
    if (ret != ERR_OK) {
        APP_LOGD("GetAllProxyDataInfos failed for GetBundleInfos failed");
        return ret;
    }
    for (const auto &bundleInfo : bundleInfos) {
        for (const auto &hapModuleInfo : bundleInfo.hapModuleInfos) {
            proxyDatas.insert(
                proxyDatas.end(), hapModuleInfo.proxyDatas.begin(), hapModuleInfo.proxyDatas.end());
        }
    }
    return ERR_OK;
}

std::string BundleDataMgr::GetBundleNameByAppId(const std::string &appId) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto it = std::find_if(bundleInfos_.cbegin(), bundleInfos_.cend(), [&appId](const auto &pair) {
        return appId == pair.second.GetAppId();
    });
    if (it == bundleInfos_.cend()) {
        APP_LOGW("invalid appId, can't find bundleName");
        return Constants::EMPTY_STRING;
    }
    return it->second.GetBundleName();
}

void BundleDataMgr::SetAOTCompileStatus(const std::string &bundleName, const std::string &moduleName,
    AOTCompileStatus aotCompileStatus, uint32_t versionCode)
{
    APP_LOGD("SetAOTCompileStatus, bundleName : %{public}s, moduleName : %{public}s, aotCompileStatus : %{public}d",
        bundleName.c_str(), moduleName.c_str(), aotCompileStatus);
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGW("bundleName %{public}s not exist", bundleName.c_str());
        (void)InstalldClient::GetInstance()->RemoveDir(ServiceConstants::HAP_ARK_CACHE_PATH + bundleName);
        (void)InstalldClient::GetInstance()->RemoveDir(ServiceConstants::SHARED_HSP_ARK_CACHE_PATH + bundleName);
        return;
    }
    if (item->second.GetVersionCode() != versionCode) {
        APP_LOGW("versionCode inconsistent, param : %{public}u, current : %{public}u, bundleName:%{public}s",
            versionCode, item->second.GetVersionCode(), bundleName.c_str());
        return;
    }
    item->second.SetAOTCompileStatus(moduleName, aotCompileStatus);
    std::string abi;
    std::string path;
    if (aotCompileStatus == AOTCompileStatus::IDLE_COMPILE_SUCCESS ||
        aotCompileStatus == AOTCompileStatus::INSTALL_COMPILE_SUCCESS) {
        abi = ServiceConstants::ARM64_V8A;
        path = std::string(ServiceConstants::ARM64) + ServiceConstants::PATH_SEPARATOR;
    }
    item->second.SetArkNativeFileAbi(abi);
    item->second.SetArkNativeFilePath(path);
    if (!dataStorage_->SaveStorageBundleInfo(item->second)) {
        APP_LOGW("SaveStorageBundleInfo failed bundleName:%{public}s", bundleName.c_str());
    }
}

void BundleDataMgr::ResetAllBundleAOTFlags()
{
    APP_LOGI_NOFUNC("ResetAllBundleAOTFlags begin");
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    std::for_each(bundleInfos_.begin(), bundleInfos_.end(), [this](auto &item) {
        if (item.second.IsAOTFlagsInitial()) {
            return;
        }
        item.second.ResetAOTFlags();
        if (!dataStorage_->SaveStorageBundleInfo(item.second)) {
            APP_LOGW("SaveStorageBundleInfo failed, bundleName : %{public}s", item.second.GetBundleName().c_str());
        }
    });
    APP_LOGI_NOFUNC("ResetAllBundleAOTFlags end");
}

void BundleDataMgr::ResetAOTFlags(const std::string &bundleName)
{
    APP_LOGD("ResetAOTFlags begin, bundleName : %{public}s", bundleName.c_str());
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("bundleName %{public}s not exist", bundleName.c_str());
        return;
    }
    if (item->second.IsAOTFlagsInitial()) {
        return;
    }
    item->second.ResetAOTFlags();
    if (!dataStorage_->SaveStorageBundleInfo(item->second)) {
        APP_LOGW("SaveStorageBundleInfo failed, bundleName : %{public}s", item->second.GetBundleName().c_str());
        return;
    }
    APP_LOGD("ResetAOTFlags end");
}

ErrCode BundleDataMgr::ResetAOTCompileStatus(const std::string &bundleName, const std::string &moduleName,
    int32_t triggerMode)
{
    APP_LOGI("ResetAOTCompileStatus begin");
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("bundleName %{public}s not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    ErrCode ret = item->second.ResetAOTCompileStatus(moduleName);
    if (ret != ERR_OK) {
        return ret;
    }
    if (!dataStorage_->SaveStorageBundleInfo(item->second)) {
        APP_LOGW("SaveStorageBundleInfo failed, bundleName : %{public}s", item->second.GetBundleName().c_str());
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    APP_LOGI("ResetAOTCompileStatus end");
    return ERR_OK;
}

std::vector<std::string> BundleDataMgr::GetAllBundleName() const
{
    APP_LOGD("GetAllBundleName begin");
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    std::vector<std::string> bundleNames;
    bundleNames.reserve(bundleInfos_.size());
    std::transform(bundleInfos_.cbegin(), bundleInfos_.cend(), std::back_inserter(bundleNames), [](const auto &item) {
        return item.first;
    });
    return bundleNames;
}

std::vector<std::string> BundleDataMgr::GetAllSystemHspCodePaths() const
{
    std::vector<std::string> systemHspCodePaths;
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto &item : bundleInfos_) {
        if (item.second.GetApplicationBundleType() == BundleType::APP_SERVICE_FWK) {
            std::string installPath = item.second.GetAppCodePath();
            APP_LOGD("get appcodepath:%{public}s for %{public}s",
                installPath.c_str(), item.second.GetBundleName().c_str());
            systemHspCodePaths.emplace_back(installPath);
        }
    }
    return systemHspCodePaths;
}

std::vector<std::string> BundleDataMgr::GetAllExtensionBundleNames(const std::vector<ExtensionAbilityType> &types) const
{
    APP_LOGD("GetAllExtensionBundleNames begin");
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    std::vector<std::string> bundleNames;
    for (const auto &[bundleName, innerBundleInfo] : bundleInfos_) {
        const auto extensionAbilityInfos = innerBundleInfo.GetInnerExtensionInfos();
        for (const auto &extensionItem : extensionAbilityInfos) {
            if (std::find(types.begin(), types.end(), extensionItem.second.type) != types.end()) {
                bundleNames.emplace_back(bundleName);
                break;
            }
        }
    }
    return bundleNames;
}

std::vector<std::tuple<std::string, int32_t, int32_t>> BundleDataMgr::GetAllLiteBundleInfo(const int32_t userId) const
{
    std::set<int32_t> userIds = GetAllUser();
    if (userIds.find(userId) == userIds.end()) {
        APP_LOGW("invalid userId");
        return {};
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    std::vector<std::tuple<std::string, int32_t, int32_t>> bundles;
    for (const auto &[bundleName, innerBundleInfo] : bundleInfos_) {
        auto installedUsers = innerBundleInfo.GetUsers();
        if (installedUsers.find(userId) == installedUsers.end()) {
            continue;
        }
        bundles.emplace_back(bundleName, innerBundleInfo.GetUid(userId), innerBundleInfo.GetGid(userId));
    }
    return bundles;
}

std::vector<std::string> BundleDataMgr::GetBundleNamesForNewUser() const
{
    APP_LOGD("begin");
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    std::vector<std::string> bundleNames;
    for (const auto &item : bundleInfos_) {
        if (item.second.GetApplicationBundleType() == BundleType::SHARED ||
            item.second.GetApplicationBundleType() == BundleType::APP_SERVICE_FWK) {
            APP_LOGD("app %{public}s is cross-app shared bundle or appService, ignore",
                item.second.GetBundleName().c_str());
            continue;
        }
        // this function is used to install additional bundle in new user, so ignore pre-install app
        if (item.second.IsPreInstallApp()) {
            APP_LOGD("app %{public}s is pre-install app, ignore", item.second.GetBundleName().c_str());
            continue;
        }
        if (item.second.IsInstalledForAllUser() &&
            OHOS::system::GetBoolParameter(ServiceConstants::IS_ENTERPRISE_DEVICE, false)) {
            APP_LOGI("%{public}s is installed for all user", item.second.GetBundleName().c_str());
            bundleNames.emplace_back(item.second.GetBundleName());
            continue;
        }
        const auto extensions = item.second.GetInnerExtensionInfos();
        for (const auto &extensionItem : extensions) {
            if (extensionItem.second.type == ExtensionAbilityType::DRIVER &&
                OHOS::system::GetBoolParameter(ServiceConstants::IS_DRIVER_FOR_ALL_USERS, true)) {
                bundleNames.emplace_back(extensionItem.second.bundleName);
                APP_LOGI("driver bundle found: %{public}s", extensionItem.second.bundleName.c_str());
                break;
            }
        }
    }
    return bundleNames;
}

bool BundleDataMgr::QueryInnerBundleInfo(const std::string &bundleName, InnerBundleInfo &info) const
{
    APP_LOGD("QueryInnerBundleInfo begin, bundleName : %{public}s", bundleName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGD("QueryInnerBundleInfo not find %{public}s", bundleName.c_str());
        return false;
    }
    info = item->second;
    return true;
}

std::vector<int32_t> BundleDataMgr::GetUserIds(const std::string &bundleName) const
{
    APP_LOGD("GetUserIds begin, bundleName : %{public}s", bundleName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    std::vector<int32_t> userIds;
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can't find bundleName : %{public}s", bundleName.c_str());
        return userIds;
    }
    auto userInfos = infoItem->second.GetInnerBundleUserInfos();
    std::transform(userInfos.cbegin(), userInfos.cend(), std::back_inserter(userIds), [](const auto &item) {
        return item.second.bundleUserInfo.userId;
    });
    return userIds;
}

void BundleDataMgr::CreateAppEl5GroupDir(const std::string &bundleName, int32_t userId)
{
    std::unordered_map<std::string, std::vector<DataGroupInfo>> dataGroupInfoMap;
    bool hasInputMethodExtension = false;
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        auto bundleInfoItem = bundleInfos_.find(bundleName);
        if (bundleInfoItem == bundleInfos_.end()) {
            APP_LOGW("%{public}s not found", bundleName.c_str());
            return;
        }
        bool needCreateEl5Dir = bundleInfoItem->second.NeedCreateEl5Dir();
        if (!needCreateEl5Dir) {
            return;
        }
        hasInputMethodExtension = bundleInfoItem->second.HasInputMethodExtension();
        dataGroupInfoMap = bundleInfoItem->second.GetDataGroupInfos();
    }
    if (dataGroupInfoMap.empty()) {
        return;
    }
    std::vector<DataGroupInfo> dataGroupInfos;
    for (const auto &groupItem : dataGroupInfoMap) {
        for (const DataGroupInfo &dataGroupInfo : groupItem.second) {
            if (dataGroupInfo.userId == userId) {
                dataGroupInfos.emplace_back(dataGroupInfo);
            }
        }
    }
    if (CreateEl5GroupDirs(dataGroupInfos, userId, hasInputMethodExtension) != ERR_OK) {
        APP_LOGW("create el5 group dirs for %{public}s %{public}d failed", bundleName.c_str(), userId);
    }
}

bool BundleDataMgr::CreateAppGroupDir(const InnerBundleInfo &info, int32_t userId, DataDirEl dirEl)
{
    auto dataGroupInfoMap = info.GetDataGroupInfos();
    bool needCreateEl5Dir = info.NeedCreateEl5Dir();
    bool hasInputMethodExtension = info.HasInputMethodExtension();
    
    return CreateAppGroupDir(dataGroupInfoMap, userId, needCreateEl5Dir, dirEl, hasInputMethodExtension);
}

bool BundleDataMgr::CreateAppGroupDir(
    const std::unordered_map<std::string, std::vector<DataGroupInfo>> &dataGroupInfoMap,
    int32_t userId, bool needCreateEl5Dir, DataDirEl dirEl, bool hasInputMethodExtension)
{
    if (dataGroupInfoMap.empty()) {
        return true;
    }
    std::vector<DataGroupInfo> dataGroupInfos;
    for (const auto &groupItem : dataGroupInfoMap) {
        for (const DataGroupInfo &dataGroupInfo : groupItem.second) {
            if (dataGroupInfo.userId == userId) {
                dataGroupInfos.emplace_back(dataGroupInfo);
            }
        }
    }
    return CreateGroupDirs(dataGroupInfos, userId, needCreateEl5Dir, dirEl, hasInputMethodExtension) == ERR_OK;
}

bool BundleDataMgr::CreateAppGroupDir(const std::string &bundleName, int32_t userId)
{
    std::unordered_map<std::string, std::vector<DataGroupInfo>> dataGroupInfoMap;
    bool needCreateEl5Dir = false;
    bool hasInputMethodExtension = false;
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        auto bundleInfoItem = bundleInfos_.find(bundleName);
        if (bundleInfoItem == bundleInfos_.end()) {
            APP_LOGW("%{public}s not found", bundleName.c_str());
            return false;
        }
        dataGroupInfoMap = bundleInfoItem->second.GetDataGroupInfos();
        needCreateEl5Dir = bundleInfoItem->second.NeedCreateEl5Dir();
        hasInputMethodExtension = bundleInfoItem->second.HasInputMethodExtension();
    }
    return CreateAppGroupDir(dataGroupInfoMap, userId, needCreateEl5Dir, DataDirEl::NONE, hasInputMethodExtension);
}

ErrCode BundleDataMgr::CreateGroupDirs(const std::vector<DataGroupInfo> &dataGroupInfos,
    int32_t userId,
    bool needCreateEl5Dir, DataDirEl dirEl, bool hasInputMethodExtension)
{
    if (dataGroupInfos.empty()) {
        return ERR_OK;
    }
    std::vector<CreateDirParam> params;
    for (const DataGroupInfo &dataGroupInfo : dataGroupInfos) {
        CreateDirParam param;
        param.uuid = dataGroupInfo.uuid;
        param.uid = dataGroupInfo.uid;
        param.gid = dataGroupInfo.gid;
        param.userId = dataGroupInfo.userId;
        param.hasInputMethodExtension = hasInputMethodExtension;
        params.emplace_back(param);
    }
    ErrCode res = ERR_OK;
    auto nonEl5Res = InstalldClient::GetInstance()->CreateDataGroupDirs(params);
    if (nonEl5Res != ERR_OK) {
        APP_LOGE("mkdir group dir failed %{public}d", nonEl5Res);
        res = nonEl5Res;
    }
    if (!needCreateEl5Dir || (dirEl != DataDirEl::EL5 && dirEl != DataDirEl::NONE)) {
        return res;
    }
    auto el5Res = CreateEl5GroupDirs(dataGroupInfos, userId, hasInputMethodExtension);
    if (el5Res != ERR_OK) {
        APP_LOGE("el5Res %{public}d", el5Res);
        res = el5Res;
    }
    return res;
}

ErrCode BundleDataMgr::CreateEl5GroupDirs(const std::vector<DataGroupInfo> &dataGroupInfos,
    int32_t userId, bool hasInputMethodExtension)
{
    if (dataGroupInfos.empty()) {
        return ERR_OK;
    }
    std::vector<CreateDirParam> createDirParams;
    for (const DataGroupInfo &dataGroupInfo : dataGroupInfos) {
        CreateDirParam param;
        param.uuid = dataGroupInfo.uuid;
        param.userId = userId;
        param.uid = dataGroupInfo.uid;
        param.gid = dataGroupInfo.gid;
        param.dataDirEl = OHOS::AppExecFwk::DataDirEl::EL5;
        // Set inputmethod extension flag
        param.hasInputMethodExtension = hasInputMethodExtension;
        createDirParams.emplace_back(param);
    }
    // Use CreateDataGroupDirs to create group directories under el5
    auto result = InstalldClient::GetInstance()->CreateDataGroupDirs(createDirParams);
    if (result != ERR_OK) {
        APP_LOGE("CreateDataGroupDirs failed for el5 group dirs");
        return result;
    }
    // Set encryption policy for el5 group directories
    ErrCode res = ERR_OK;
    for (const DataGroupInfo &dataGroupInfo : dataGroupInfos) {
        EncryptionParam encryptionParam("", dataGroupInfo.uuid, dataGroupInfo.uid, userId, EncryptionDirType::GROUP);
        std::string keyId = "";
        auto setPolicyRes = InstalldClient::GetInstance()->SetEncryptionPolicy(encryptionParam, keyId);
        if (setPolicyRes != ERR_OK) {
            LOG_E(BMS_TAG_INSTALLER, "SetEncryptionPolicy failed");
            res = setPolicyRes;
        }
    }
    return res;
}

ErrCode BundleDataMgr::GetSpecifiedDistributionType(
    const std::string &bundleName, std::string &specifiedDistributionType)
{
    APP_LOGD("GetSpecifiedDistributionType bundleName: %{public}s", bundleName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW_NOFUNC("-n %{public}s does not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    if (infoItem->second.GetApplicationBundleType() != BundleType::SHARED) {
        int32_t userId = AccountHelper::GetOsAccountLocalIdFromUid(IPCSkeleton::GetCallingUid());
        int32_t responseUserId = infoItem->second.GetResponseUserId(userId);
        if (responseUserId == Constants::INVALID_USERID) {
            APP_LOGW("bundleName: %{public}s does not exist in current userId", bundleName.c_str());
            return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
        }
    }
    if (!DelayedSingleton<AppProvisionInfoManager>::GetInstance()->GetSpecifiedDistributionType(bundleName,
        specifiedDistributionType)) {
        APP_LOGW("bundleName:%{public}s GetSpecifiedDistributionType failed", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::GetAdditionalInfo(
    const std::string &bundleName, std::string &additionalInfo)
{
    APP_LOGD("GetAdditionalInfo bundleName: %{public}s", bundleName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW_NOFUNC("%{public}s not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    if (infoItem->second.GetApplicationBundleType() != BundleType::SHARED) {
        int32_t userId = AccountHelper::GetOsAccountLocalIdFromUid(IPCSkeleton::GetCallingUid());
        int32_t responseUserId = infoItem->second.GetResponseUserId(userId);
        if (responseUserId == Constants::INVALID_USERID) {
            APP_LOGW("bundleName: %{public}s does not exist in current userId", bundleName.c_str());
            return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
        }
    }
    if (!DelayedSingleton<AppProvisionInfoManager>::GetInstance()->GetAdditionalInfo(bundleName,
        additionalInfo)) {
        APP_LOGW("bundleName:%{public}s GetAdditionalInfo failed", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::GetAdditionalInfoForAllUser(
    const std::string &bundleName, std::string &additionalInfo)
{
    APP_LOGD("GetAdditionalInfo bundleName: %{public}s", bundleName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW_NOFUNC("%{public}s not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    if (!DelayedSingleton<AppProvisionInfoManager>::GetInstance()->GetAdditionalInfo(bundleName,
        additionalInfo)) {
        APP_LOGW("bundleName:%{public}s GetAdditionalInfo failed", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::GenerateAppInstallExtendedInfo(const InnerBundleInfo &innerBundleInfo,
    AppInstallExtendedInfo &appInstallExtendedInfo)
{
    std::string bundleName = innerBundleInfo.GetBundleName();
    appInstallExtendedInfo.bundleName = bundleName;

    if (!DelayedSingleton<AppProvisionInfoManager>::GetInstance()->GetSpecifiedDistributionType(bundleName,
        appInstallExtendedInfo.specifiedDistributionType)) {
        APP_LOGW("bundleName:%{public}s GetSpecifiedDistributionType failed", bundleName.c_str());
    }

    if (!DelayedSingleton<AppProvisionInfoManager>::GetInstance()->GetAdditionalInfo(bundleName,
        appInstallExtendedInfo.additionalInfo)) {
        APP_LOGW("bundleName:%{public}s GetAdditionalInfo failed", bundleName.c_str());
    }

    appInstallExtendedInfo.crowdtestDeadline = innerBundleInfo.GetAppCrowdtestDeadline();
    appInstallExtendedInfo.installSource = innerBundleInfo.GetInstallSource();
    appInstallExtendedInfo.compatibleVersion = innerBundleInfo.GetCompatibleVersion();
    appInstallExtendedInfo.compatibleMinorVersion = innerBundleInfo.GetCompatibleMinorVersion();
    appInstallExtendedInfo.compatiblePatchVersion = innerBundleInfo.GetCompatiblePatchVersion();

    // collect module info and dependencies in single pass
    for (const auto& [modulePackage, innerModuleInfo] : innerBundleInfo.GetInnerModuleInfos()) {
        appInstallExtendedInfo.hashParam.emplace(modulePackage, innerModuleInfo.hashValue);
        appInstallExtendedInfo.requiredDeviceFeatures.emplace(modulePackage,
            innerBundleInfo.GetRequiredDeviceFeatures(modulePackage));
        if (!innerModuleInfo.hapPath.empty()) {
            appInstallExtendedInfo.hapPath.push_back(innerModuleInfo.hapPath);
        }
    }

    // collect sharedBundleInfos with lambda to reduce nesting depth
    std::set<std::string> uniqueDependencies;
    auto addDependency = [this, &uniqueDependencies, &appInstallExtendedInfo](const Dependency &dep) {
        if (uniqueDependencies.find(dep.bundleName) != uniqueDependencies.end()) {
            return;
        }
        uniqueDependencies.insert(dep.bundleName);
        SharedBundleInfo sharedBundleInfo;
        if (GetSharedBundleInfoBySelfNoLock(dep.bundleName, sharedBundleInfo) == ERR_OK) {
            appInstallExtendedInfo.sharedBundleInfos.emplace_back(sharedBundleInfo);
        } else {
            APP_LOGW("GetSharedBundleInfo failed for bundle %{public}s", dep.bundleName.c_str());
        }
    };

    for (const auto& [modulePackage, innerModuleInfo] : innerBundleInfo.GetInnerModuleInfos()) {
        std::vector<Dependency> dependencies;
        if (!innerBundleInfo.GetAllSharedDependencies(modulePackage, dependencies)) {
            APP_LOGD("GetAllSharedDependencies failed for module %{public}s, skip", modulePackage.c_str());
            continue;
        }
        for (const auto& dep : dependencies) {
            addDependency(dep);
        }
    }

    return ERR_OK;
}

ErrCode BundleDataMgr::GetAllAppInstallExtendedInfo(std::vector<AppInstallExtendedInfo> &appInstallExtendedInfos)
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);

    for (const auto& [bundleName, innerBundleInfo] : bundleInfos_) {
        if (innerBundleInfo.IsDisabled()) {
            APP_LOGD("app %{public}s is disabled", bundleName.c_str());
            continue;
        }
        if (innerBundleInfo.GetApplicationBundleType() == BundleType::SHARED) {
            APP_LOGD("app %{public}s is cross-app shared bundle", bundleName.c_str());
            continue;
        }
        if (innerBundleInfo.GetApplicationBundleType() == BundleType::APP_SERVICE_FWK) {
            if (HasOnlySharedModules(innerBundleInfo)) {
                APP_LOGD("app %{public}s has only shared bundle", bundleName.c_str());
                continue;
            }
        }
        int32_t userId = AccountHelper::GetOsAccountLocalIdFromUid(IPCSkeleton::GetCallingUid());
        if (!innerBundleInfo.GetUsers().count(userId)) {
            APP_LOGD("user %{public}d is not exist", userId);
            continue;
        }
        AppInstallExtendedInfo appInstallExtendedInfo;
        ErrCode ret = GenerateAppInstallExtendedInfo(innerBundleInfo, appInstallExtendedInfo);
        if (ret != ERR_OK) {
            APP_LOGE("GenerateAppInstallExtendedInfo failed for %{public}s, ret: %{public}d",
                bundleName.c_str(), ret);
            continue;
        }
        appInstallExtendedInfos.emplace_back(appInstallExtendedInfo);
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::SetExtNameOrMIMEToApp(const std::string &bundleName, const std::string &moduleName,
    const std::string &abilityName, const std::string &extName, const std::string &mimeType)
{
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGW("bundleName %{public}s not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    ErrCode ret;
    if (!extName.empty()) {
        ret = item->second.SetExtName(moduleName, abilityName, extName);
        if (ret != ERR_OK) {
            APP_LOGD("set ext name to app failed, bundleName:%{public}s", bundleName.c_str());
            return ret;
        }
    }
    if (!mimeType.empty()) {
        ret = item->second.SetMimeType(moduleName, abilityName, mimeType);
        if (ret != ERR_OK) {
            APP_LOGD("set mime type to app failed, bundleName:%{public}s", bundleName.c_str());
            return ret;
        }
    }
    if (!dataStorage_->SaveStorageBundleInfo(item->second)) {
        APP_LOGE("SaveStorageBundleInfo failed, bundleName:%{public}s", bundleName.c_str());
        return ERR_APPEXECFWK_SERVICE_INTERNAL_ERROR;
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::DelExtNameOrMIMEToApp(const std::string &bundleName, const std::string &moduleName,
    const std::string &abilityName, const std::string &extName, const std::string &mimeType)
{
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGW("bundleName %{public}s not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    ErrCode ret;
    if (!extName.empty()) {
        ret = item->second.DelExtName(moduleName, abilityName, extName);
        if (ret != ERR_OK) {
            APP_LOGD("delete ext name to app failed, bundleName:%{public}s", bundleName.c_str());
            return ret;
        }
    }
    if (!mimeType.empty()) {
        ret = item->second.DelMimeType(moduleName, abilityName, mimeType);
        if (ret != ERR_OK) {
            APP_LOGD("delete mime type to app failed, bundleName:%{public}s", bundleName.c_str());
            return ret;
        }
    }
    if (!dataStorage_->SaveStorageBundleInfo(item->second)) {
        APP_LOGE("SaveStorageBundleInfo failed, bundleName:%{public}s", bundleName.c_str());
        return ERR_APPEXECFWK_SERVICE_INTERNAL_ERROR;
    }
    return ERR_OK;
}

bool BundleDataMgr::MatchPrivateType(const Want &want,
    const std::vector<std::string> &supportExtNames, const std::vector<std::string> &supportMimeTypes,
    const std::vector<std::string> &paramMimeTypes) const
{
    std::string uri = want.GetUriString();
    APP_LOGD("MatchPrivateType, uri is %{private}s", uri.c_str());
    auto suffixIndex = uri.rfind('.');
    if (suffixIndex == std::string::npos) {
        return false;
    }
    std::string suffix = uri.substr(suffixIndex + 1);
    bool supportPrivateType = std::any_of(supportExtNames.begin(), supportExtNames.end(), [&](const auto &extName) {
        return extName == suffix;
    });
    if (supportPrivateType) {
        APP_LOGI("uri is a supported private-type file");
        return true;
    }

    if (!paramMimeTypes.empty()) {
        auto iter = std::find_first_of(
            paramMimeTypes.begin(), paramMimeTypes.end(), supportMimeTypes.begin(), supportMimeTypes.end());
        if (iter != paramMimeTypes.end()) {
            APP_LOGI("uri is a supported mime-type file");
            return true;
        }
    }
    return false;
}

bool BundleDataMgr::QueryAppGalleryAbilityName(std::string &bundleName, std::string &abilityName)
{
    APP_LOGD("QueryAppGalleryAbilityName called");
    AbilityInfo abilityInfo;
    ExtensionAbilityInfo extensionInfo;
    Want want;
    want.SetAction(FREE_INSTALL_ACTION);
    if (!ImplicitQueryInfoByPriority(
        want, 0, Constants::ANY_USERID, abilityInfo, extensionInfo)) {
        APP_LOGD("ImplicitQueryInfoByPriority for action %{public}s failed", FREE_INSTALL_ACTION);
        return false;
    }
    if (!abilityInfo.name.empty()) {
        bundleName = abilityInfo.bundleName;
        abilityName = abilityInfo.name;
    } else {
        bundleName = extensionInfo.bundleName;
        abilityName = extensionInfo.name;
    }

    if (bundleName.empty() || abilityName.empty()) {
        APP_LOGW("bundleName: %{public}s or abilityName: %{public}s is empty()",
            bundleName.c_str(), abilityName.c_str());
        return false;
    }
    bool isSystemApp = false;
    if (IsSystemApp(bundleName, isSystemApp) != ERR_OK || !isSystemApp) {
        APP_LOGW("%{public}s is not systemApp", bundleName.c_str());
        bundleName.clear();
        abilityName.clear();
        return false;
    }
    APP_LOGD("QueryAppGalleryAbilityName bundleName: %{public}s, abilityName: %{public}s",
        bundleName.c_str(), abilityName.c_str());
    return true;
}

std::string BundleDataMgr::GetProfilePath(ProfileType profileType, const InnerModuleInfo &innerModuleInfo) const
{
    if (!innerModuleInfo.isEntry) {
        APP_LOGD("not a entry module");
        return EMPTY_STRING;
    }
    std::string profileConfig;
    switch (profileType) {
        case ProfileType::EASY_GO_PROFILE: {
            profileConfig = innerModuleInfo.easyGo;
            break;
        }
        case ProfileType::SHARE_FILES_PROFILE: {
            profileConfig = innerModuleInfo.shareFiles;
            break;
        }
        default: {
            APP_LOGE("unsupported profile type: %{public}d", profileType);
            return EMPTY_STRING;
        }
    }
    auto pos = profileConfig.find(PROFILE_PREFIX);
    if (pos == std::string::npos) {
        APP_LOGD("invalid profile config");
        return EMPTY_STRING;
    }
    std::string profileFileName = profileConfig.substr(pos + PROFILE_PREFIX_LENGTH);
    std::string profileFilePath = PROFILE_PATH + profileFileName + JSON_SUFFIX;
    return profileFilePath;
}

ErrCode BundleDataMgr::GetJsonProfile(ProfileType profileType, const std::string &bundleName,
    const std::string &moduleName, std::string &profile, int32_t userId) const
{
    APP_LOGD("profileType: %{public}d, bundleName: %{public}s, moduleName: %{public}s",
        profileType, bundleName.c_str(), moduleName.c_str());
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    auto mapItem = PROFILE_TYPE_MAP.find(profileType);
    if (mapItem == PROFILE_TYPE_MAP.end()) {
        APP_LOGE("profileType: %{public}d is invalid", profileType);
        return ERR_BUNDLE_MANAGER_PROFILE_NOT_EXIST;
    }
    std::string profilePath = mapItem->second;
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const auto &item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("bundleName: %{public}s is not found", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    const InnerBundleInfo &bundleInfo = item->second;
    bool isEnabled = false;
    int32_t responseUserId = bundleInfo.GetResponseUserId(requestUserId);
    ErrCode res = bundleInfo.GetApplicationEnabledV9(responseUserId, isEnabled);
    if (res != ERR_OK) {
        APP_LOGE("check application enabled failed, bundleName: %{public}s", bundleName.c_str());
        return res;
    }
    if (!isEnabled) {
        APP_LOGE("bundleName: %{public}s is disabled", bundleInfo.GetBundleName().c_str());
        return ERR_BUNDLE_MANAGER_APPLICATION_DISABLED;
    }
    std::string moduleNameTmp = moduleName;
    if (moduleName.empty()) {
        APP_LOGW("moduleName is empty, try to get profile from entry module");
        std::map<std::string, InnerModuleInfo> moduleInfos = bundleInfo.GetInnerModuleInfos();
        for (const auto &info : moduleInfos) {
            if (info.second.isEntry) {
                moduleNameTmp = info.second.moduleName;
                APP_LOGW("try to get profile from entry module: %{public}s", moduleNameTmp.c_str());
                break;
            }
        }
    }
    auto moduleInfo = bundleInfo.GetInnerModuleInfoByModuleName(moduleNameTmp);
    if (!moduleInfo) {
        APP_LOGE("moduleName: %{public}s is not found", moduleNameTmp.c_str());
        return ERR_BUNDLE_MANAGER_MODULE_NOT_EXIST;
    }
    if (profilePath.empty()) {
        profilePath = GetProfilePath(profileType, *moduleInfo);
    }
    if (profilePath.empty()) {
        APP_LOGD("profile: %{public}d not config", profileType);
        return ERR_BUNDLE_MANAGER_PROFILE_NOT_EXIST;
    }
    return GetJsonProfileByExtractor(moduleInfo->hapPath, profilePath, profile);
}

ErrCode __attribute__((no_sanitize("cfi"))) BundleDataMgr::GetJsonProfileByExtractor(const std::string &hapPath,
    const std::string &profilePath, std::string &profile) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    APP_LOGD("GetJsonProfileByExtractor with hapPath %{private}s and profilePath %{private}s",
        hapPath.c_str(), profilePath.c_str());
    BundleExtractor bundleExtractor(hapPath);
    if (!bundleExtractor.Init()) {
        APP_LOGE("bundle extractor init failed");
        return ERR_APPEXECFWK_SERVICE_INTERNAL_ERROR;
    }
    if (!bundleExtractor.HasEntry(profilePath)) {
        APP_LOGD("profile not exist");
        return ERR_BUNDLE_MANAGER_PROFILE_NOT_EXIST;
    }
    std::stringstream profileStream;
    if (!bundleExtractor.ExtractByName(profilePath, profileStream)) {
        APP_LOGE("extract profile failed");
        return ERR_APPEXECFWK_SERVICE_INTERNAL_ERROR;
    }
    profile = profileStream.str();
    return ERR_OK;
}

bool BundleDataMgr::QueryDataGroupInfos(const std::string &bundleName, int32_t userId,
    std::vector<DataGroupInfo> &infos) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW_NOFUNC("QueryDataGroupInfos %{public}s is not existed", bundleName.c_str());
        return false;
    }
    auto dataGroupInfos = infoItem->second.GetDataGroupInfos();
    for (const auto &item : dataGroupInfos) {
        auto dataGroupIter = std::find_if(std::begin(item.second), std::end(item.second),
            [userId](const DataGroupInfo &info) {
            return info.userId == userId;
        });
        if (dataGroupIter != std::end(item.second)) {
            infos.push_back(*dataGroupIter);
        }
    }
    return true;
}

bool BundleDataMgr::GetGroupDir(const std::string &dataGroupId, std::string &dir, int32_t userId) const
{
    std::string uuid;
    if (BundlePermissionMgr::IsSystemApp() &&
        BundlePermissionMgr::VerifyCallingPermissionForAll(Constants::PERMISSION_GET_BUNDLE_INFO_PRIVILEGED)) {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        for (const auto &item : bundleInfos_) {
            const auto &dataGroupInfos = item.second.GetDataGroupInfos();
            auto dataGroupInfosIter = dataGroupInfos.find(dataGroupId);
            if (dataGroupInfosIter == dataGroupInfos.end()) {
                continue;
            }
            auto dataInUserIter = std::find_if(std::begin(dataGroupInfosIter->second),
                std::end(dataGroupInfosIter->second),
                [userId](const DataGroupInfo &info) { return info.userId == userId; });
            if (dataInUserIter != std::end(dataGroupInfosIter->second)) {
                uuid = dataInUserIter->uuid;
                break;
            }
        }
    } else {
        int32_t callingUid = IPCSkeleton::GetCallingUid();
        int32_t appIndex = 0;
        std::string bundleName;
        ErrCode ret = GetBundleNameAndIndex(callingUid, bundleName, appIndex);
        if (ret != ERR_OK) {
            APP_LOGW("uid: %{public}d invalid!", callingUid);
            return false;
        }
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        const InnerBundleInfo* innerBundleInfo = nullptr;
        if (GetInnerBundleInfoNoLock(bundleName, callingUid, appIndex, innerBundleInfo) != ERR_OK) {
            APP_LOGD("verify uid failed, callingUid is %{public}d", callingUid);
            return false;
        }
        if (!innerBundleInfo) {
            LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetGroupDir is null.");
            return false;
        }
        const auto &dataGroupInfos = innerBundleInfo->GetDataGroupInfos();
        auto dataGroupInfosIter = dataGroupInfos.find(dataGroupId);
        if (dataGroupInfosIter == dataGroupInfos.end()) {
            APP_LOGW("calling bundle do not have dataGroupId: %{public}s", dataGroupId.c_str());
            return false;
        }
        auto dataGroupIter = std::find_if(std::begin(dataGroupInfosIter->second), std::end(dataGroupInfosIter->second),
            [userId](const DataGroupInfo &info) {
            return info.userId == userId;
        });
        if (dataGroupIter != std::end(dataGroupInfosIter->second)) {
            uuid = dataGroupIter->uuid;
        }
    }
    if (uuid.empty()) {
        APP_LOGW("get uuid by data group id failed");
        return false;
    }
    dir = std::string(ServiceConstants::REAL_DATA_PATH) + ServiceConstants::PATH_SEPARATOR + std::to_string(userId)
        + ServiceConstants::DATA_GROUP_PATH + uuid;
    APP_LOGD("groupDir: %{private}s", dir.c_str());
    return true;
}

void BundleDataMgr::CreateNewDataGroupInfo(const std::string &groupId, const int32_t userId,
    const DataGroupInfo &oldDataGroupInfo, DataGroupInfo &newDataGroupInfo)
{
    newDataGroupInfo.dataGroupId = groupId;
    newDataGroupInfo.userId = userId;

    newDataGroupInfo.uuid = oldDataGroupInfo.uuid;
    int32_t uniqueId = oldDataGroupInfo.uid - oldDataGroupInfo.userId * Constants::BASE_USER_RANGE -
        DATA_GROUP_UID_OFFSET;
    int32_t uid = uniqueId + userId * Constants::BASE_USER_RANGE + DATA_GROUP_UID_OFFSET;
    newDataGroupInfo.uid = uid;
    newDataGroupInfo.gid = uid;
}

void BundleDataMgr::ProcessAllUserDataGroupInfosWhenBundleUpdate(InnerBundleInfo &innerBundleInfo)
{
    auto dataGroupInfos = innerBundleInfo.GetDataGroupInfos();
    if (dataGroupInfos.empty()) {
        return;
    }
    for (int32_t userId : innerBundleInfo.GetUsers()) {
        for (const auto &dataItem : dataGroupInfos) {
            std::string groupId = dataItem.first;
            if (dataItem.second.empty()) {
                APP_LOGW("id infos %{public}s empty in -n %{public}s", groupId.c_str(),
                    innerBundleInfo.GetBundleName().c_str());
                continue;
            }
            DataGroupInfo dataGroupInfo;
            CreateNewDataGroupInfo(groupId, userId, dataItem.second[0], dataGroupInfo);
            innerBundleInfo.AddDataGroupInfo(groupId, dataGroupInfo);
            (void)CreateAppGroupDir(innerBundleInfo, userId);
        }
    }
}

void BundleDataMgr::GenerateDataGroupUuidAndUid(DataGroupInfo &dataGroupInfo, int32_t userId,
    std::unordered_set<int32_t> &uniqueIdSet) const
{
    int32_t uniqueId = DATA_GROUP_INDEX_START;
    for (int32_t i = DATA_GROUP_INDEX_START; i < DATA_GROUP_UID_OFFSET; i++) {
        if (uniqueIdSet.find(i) == uniqueIdSet.end()) {
            uniqueId = i;
            break;
        }
    }

    int32_t uid = userId * Constants::BASE_USER_RANGE + uniqueId + DATA_GROUP_UID_OFFSET;
    dataGroupInfo.uid = uid;
    dataGroupInfo.gid = uid;

    std::string str = GenerateUuidByKey(dataGroupInfo.dataGroupId);
    dataGroupInfo.uuid = str;
    uniqueIdSet.insert(uniqueId);
}

void BundleDataMgr::GenerateDataGroupInfos(const std::string &bundleName,
    const std::unordered_set<std::string> &dataGroupIdList, int32_t userId, bool needSaveStorage)
{
    APP_LOGD("called for user: %{public}d", userId);
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto bundleInfoItem = bundleInfos_.find(bundleName);
    if (bundleInfoItem == bundleInfos_.end()) {
        APP_LOGW("%{public}s not found", bundleName.c_str());
        return;
    }
    auto dataGroupInfos = bundleInfoItem->second.GetDataGroupInfos();
    for (const auto &dataItem : dataGroupInfos) {
        std::string oldGroupId = dataItem.first;
        if (dataGroupIdList.find(oldGroupId) == dataGroupIdList.end()) {
            bundleInfoItem->second.DeleteDataGroupInfo(oldGroupId);
        }
    }
    if (dataGroupIdList.empty()) {
        APP_LOGD("dataGroupIdList is empty");
        return;
    }
    std::map<std::string, std::pair<int32_t, std::string>> dataGroupIndexMap;
    std::unordered_set<int32_t> uniqueIdSet;
    GetDataGroupIndexMap(dataGroupIndexMap, uniqueIdSet);
    for (const std::string &groupId : dataGroupIdList) {
        DataGroupInfo dataGroupInfo;
        dataGroupInfo.dataGroupId = groupId;
        dataGroupInfo.userId = userId;
        auto iter = dataGroupIndexMap.find(groupId);
        if (iter != dataGroupIndexMap.end()) {
            dataGroupInfo.uuid = iter->second.second;
            int32_t uid = iter->second.first + userId * Constants::BASE_USER_RANGE + DATA_GROUP_UID_OFFSET;
            dataGroupInfo.uid = uid;
            dataGroupInfo.gid = uid;
        } else {
            // need to generate a valid uniqueId
            GenerateDataGroupUuidAndUid(dataGroupInfo, userId, uniqueIdSet);
        }
        bundleInfoItem->second.AddDataGroupInfo(groupId, dataGroupInfo);
    }
    (void)CreateAppGroupDir(bundleInfoItem->second, userId);
    ProcessAllUserDataGroupInfosWhenBundleUpdate(bundleInfoItem->second);
    if (needSaveStorage && !dataStorage_->SaveStorageBundleInfo(bundleInfoItem->second)) {
        APP_LOGW("update storage failed bundle:%{public}s", bundleName.c_str());
    }
}

void BundleDataMgr::GenerateNewUserDataGroupInfos(const std::string &bundleName, int32_t userId)
{
    APP_LOGD("called for -b %{public}s, -u %{public}d", bundleName.c_str(), userId);
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto bundleInfoItem = bundleInfos_.find(bundleName);
    if (bundleInfoItem == bundleInfos_.end()) {
        APP_LOGW("%{public}s not found", bundleName.c_str());
        return;
    }
    auto dataGroupInfos = bundleInfoItem->second.GetDataGroupInfos();
    if (dataGroupInfos.empty()) {
        return;
    }
    for (const auto &dataItem : dataGroupInfos) {
        std::string groupId = dataItem.first;
        if (dataItem.second.empty()) {
            APP_LOGW("id infos %{public}s empty in %{public}s", groupId.c_str(), bundleName.c_str());
            continue;
        }
        DataGroupInfo dataGroupInfo;
        CreateNewDataGroupInfo(groupId, userId, dataItem.second[0], dataGroupInfo);
        bundleInfoItem->second.AddDataGroupInfo(groupId, dataGroupInfo);
        //need create group dir
        (void)CreateAppGroupDir(bundleInfoItem->second, userId);
    }
    if (!dataStorage_->SaveStorageBundleInfo(bundleInfoItem->second)) {
        APP_LOGW("update storage failed bundle:%{public}s", bundleName.c_str());
    }
}

void BundleDataMgr::DeleteUserDataGroupInfos(const std::string &bundleName, int32_t userId, bool keepData)
{
    APP_LOGD("called for -b %{public}s, -u %{public}d", bundleName.c_str(), userId);
    std::vector<std::string> uuidList;
    {
        std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
        auto bundleInfoItem = bundleInfos_.find(bundleName);
        if (bundleInfoItem == bundleInfos_.end()) {
            APP_LOGW("%{public}s not found", bundleName.c_str());
            return;
        }
        auto dataGroupInfos = bundleInfoItem->second.GetDataGroupInfos();
        if (dataGroupInfos.empty()) {
            return;
        }
        for (const auto &dataItem : dataGroupInfos) {
            std::string groupId = dataItem.first;
            if (dataItem.second.empty()) {
                APP_LOGW("id infos %{public}s empty in %{public}s", groupId.c_str(), bundleName.c_str());
                continue;
            }
            bundleInfoItem->second.RemoveGroupInfos(userId, groupId);
            if (!keepData && !IsDataGroupIdExistNoLock(groupId, userId)) {
                uuidList.emplace_back(dataItem.second[0].uuid);
            }
        }
        if (!dataStorage_->SaveStorageBundleInfo(bundleInfoItem->second)) {
            APP_LOGW("update storage failed bundle:%{public}s", bundleName.c_str());
        }
    }
    auto result = InstalldClient::GetInstance()->DeleteDataGroupDirs(uuidList, userId);
    if (result != ERR_OK) {
        APP_LOGE("delete group dir failed, err %{public}d", result);
    }
}

void BundleDataMgr::GetDataGroupIndexMap(
    std::map<std::string, std::pair<int32_t, std::string>> &dataGroupIndexMap,
    std::unordered_set<int32_t> &uniqueIdSet) const
{
    for (const auto &bundleInfo : bundleInfos_) {
        for (const auto &infoItem : bundleInfo.second.GetDataGroupInfos()) {
            for_each(std::begin(infoItem.second), std::end(infoItem.second), [&](const DataGroupInfo &dataGroupInfo) {
                int32_t index = dataGroupInfo.uid - dataGroupInfo.userId * Constants::BASE_USER_RANGE
                    - DATA_GROUP_UID_OFFSET;
                dataGroupIndexMap[dataGroupInfo.dataGroupId] =
                    std::pair<int32_t, std::string>(index, dataGroupInfo.uuid);
                uniqueIdSet.insert(index);
            });
        }
    }
}

bool BundleDataMgr::IsShareDataGroupIdNoLock(const std::string &dataGroupId, int32_t userId) const
{
    APP_LOGD("IsShareDataGroupIdNoLock, dataGroupId is %{public}s", dataGroupId.c_str());
    int32_t count = 0;
    for (const auto &info : bundleInfos_) {
        auto dataGroupInfos = info.second.GetDataGroupInfos();
        auto iter = dataGroupInfos.find(dataGroupId);
        if (iter == dataGroupInfos.end()) {
            continue;
        }

        auto dataGroupIter = std::find_if(std::begin(iter->second), std::end(iter->second),
            [userId](const DataGroupInfo &dataGroupInfo) {
            return dataGroupInfo.userId == userId;
        });
        if (dataGroupIter == std::end(iter->second)) {
            continue;
        }
        count++;
        if (count > 1) {
            APP_LOGW("dataGroupId: %{public}s is shared", dataGroupId.c_str());
            return true;
        }
    }
    return false;
}

bool BundleDataMgr::IsDataGroupIdExistNoLock(const std::string &dataGroupId, int32_t userId) const
{
    APP_LOGD("dataGroupId is %{public}s, user %{public}d", dataGroupId.c_str(), userId);
    for (const auto &info : bundleInfos_) {
        auto dataGroupInfos = info.second.GetDataGroupInfos();
        auto iter = dataGroupInfos.find(dataGroupId);
        if (iter == dataGroupInfos.end()) {
            continue;
        }

        auto dataGroupIter = std::find_if(std::begin(iter->second), std::end(iter->second),
            [userId](const DataGroupInfo &dataGroupInfo) {
            return dataGroupInfo.userId == userId;
        });
        if (dataGroupIter == std::end(iter->second)) {
            continue;
        }
        return true;
    }
    return false;
}

void BundleDataMgr::DeleteGroupDirsForException(const InnerBundleInfo &oldInfo, int32_t userId) const
{
    //find ids existed in newInfo, but not in oldInfo when there is no others share this id
    std::vector<std::string> uuidList;
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        const auto bundleInfoItem = bundleInfos_.find(oldInfo.GetBundleName());
        if (bundleInfoItem == bundleInfos_.end()) {
            APP_LOGE("find bundle %{public}s failed", oldInfo.GetBundleName().c_str());
            return;
        }
        auto newDataGroupInfos = bundleInfoItem->second.GetDataGroupInfos();
        if (newDataGroupInfos.empty()) {
            return;
        }
        auto oldDatagroupInfos = oldInfo.GetDataGroupInfos();
        for (const auto &newDataItem : newDataGroupInfos) {
            std::string newGroupId = newDataItem.first;
            if (newDataItem.second.empty()) {
                APP_LOGE("infos empty in %{public}s %{public}s", oldInfo.GetBundleName().c_str(), newGroupId.c_str());
                continue;
            }
            if (oldDatagroupInfos.find(newGroupId) != oldDatagroupInfos.end() ||
                IsShareDataGroupIdNoLock(newGroupId, userId)) {
                continue;
            }
            uuidList.emplace_back(newDataItem.second[0].uuid);
        }
    }
    auto result = InstalldClient::GetInstance()->DeleteDataGroupDirs(uuidList, userId);
    if (result != ERR_OK) {
        APP_LOGE("delete group dir failed, err %{public}d", result);
    }
}

ErrCode BundleDataMgr::FindAbilityInfoInBundleInfo(const InnerBundleInfo &innerBundleInfo,
    const std::string &moduleName, const std::string &abilityName, AbilityInfo &abilityInfo) const
{
    if (moduleName.empty()) {
        auto ability = innerBundleInfo.FindAbilityInfoV9(moduleName, abilityName);
        if (!ability) {
            return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
        }
        abilityInfo = *ability;
        return ERR_OK;
    }

    ErrCode ret = innerBundleInfo.FindAbilityInfo(moduleName, abilityName, abilityInfo);
    if (ret != ERR_OK) {
        APP_LOGD("%{public}s:FindAbilityInfo failed: %{public}d", innerBundleInfo.GetBundleName().c_str(), ret);
    }
    return ret;
}

bool BundleDataMgr::HasAppOrAtomicServiceInUser(const std::string &bundleName, int32_t userId) const
{
    if (!HasUserId(userId)) {
        APP_LOGW("user %{public}d error", userId);
        return false;
    }
    if (bundleName.empty()) {
        APP_LOGW("param -n %{public}s error", bundleName.c_str());
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto iter = bundleInfos_.find(bundleName);
    if (iter == bundleInfos_.end()) {
        APP_LOGW("bundle %{public}s not found", bundleName.c_str());
        return false;
    }
    BundleType bundleType = iter->second.GetApplicationBundleType();
    if (bundleType != BundleType::APP && bundleType != BundleType::ATOMIC_SERVICE) {
        APP_LOGW("bundle %{public}s is not app or atomicservice", bundleName.c_str());
        return false;
    }
    return iter->second.GetResponseUserId(userId) != Constants::INVALID_USERID;
}

bool BundleDataMgr::GetAllAppAndAtomicServiceInUser(int32_t userId, std::vector<std::string> &bundleList) const
{
    if (!HasUserId(userId)) {
        APP_LOGW("param -u %{public}d error", userId);
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto &item : bundleInfos_) {
        BundleType bundleType = item.second.GetApplicationBundleType();
        if (bundleType != BundleType::APP && bundleType != BundleType::ATOMIC_SERVICE) {
            continue;
        }
        if (item.second.GetResponseUserId(userId) != Constants::INVALID_USERID) {
            bundleList.emplace_back(item.first);
        }
    }
    return !bundleList.empty();
}

void BundleDataMgr::ScanAllBundleGroupInfo()
{
    // valid info, key: index, value: dataGroupId
    std::map<int32_t, std::string> indexMap;
    // valid info, key: dataGroupId, value: index
    std::map<std::string, int32_t> groupIdMap;
    // invalid infos, key: bundleNames, value: dataGroupId
    std::map<std::string, std::set<std::string>> needProcessGroupInfoBundleNames;
    // invalid GroupId
    std::set<std::string> errorGroupIds;
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto &info : bundleInfos_) {
        std::unordered_map<std::string, std::vector<DataGroupInfo>> dataGroupInfos = info.second.GetDataGroupInfos();
        if (dataGroupInfos.empty()) {
            continue;
        }
        for (const auto &dataGroupItem : dataGroupInfos) {
            std::string dataGroupId = dataGroupItem.first;
            if (dataGroupItem.second.empty()) {
                APP_LOGW("dataGroupInfos is empty in %{public}s", dataGroupId.c_str());
                continue;
            }
            int32_t groupUidIndex = dataGroupItem.second[0].uid -
                dataGroupItem.second[0].userId * Constants::BASE_USER_RANGE - DATA_GROUP_UID_OFFSET;
            if (indexMap.find(groupUidIndex) == indexMap.end() && groupIdMap.find(dataGroupId) == groupIdMap.end()) {
                indexMap[groupUidIndex] = dataGroupId;
                groupIdMap[dataGroupId] = groupUidIndex;
                continue;
            }
            if (indexMap.find(groupUidIndex) == indexMap.end() && groupIdMap.find(dataGroupId) != groupIdMap.end()) {
                APP_LOGW("id %{public}s has valid index %{public}d, not index %{public}d",
                    dataGroupId.c_str(), groupIdMap[dataGroupId], groupUidIndex);
            }
            if (indexMap.find(groupUidIndex) != indexMap.end() && indexMap[groupUidIndex] == dataGroupId) {
                continue;
            }
            if (indexMap.find(groupUidIndex) != indexMap.end() && indexMap[groupUidIndex] != dataGroupId) {
                APP_LOGW("id %{public}s has invalid index %{public}d", dataGroupId.c_str(), groupUidIndex);
            }
            errorGroupIds.insert(dataGroupId);
            // invalid index or groupId
            APP_LOGW("error index %{public}d groudId %{public}s -n %{public}s",
                groupUidIndex, dataGroupId.c_str(), info.first.c_str());
            needProcessGroupInfoBundleNames[info.first].insert(dataGroupId);
        }
    }
    HandleGroupIdAndIndex(errorGroupIds, indexMap, groupIdMap);
    if (!HandleErrorDataGroupInfos(groupIdMap, needProcessGroupInfoBundleNames)) {
        APP_LOGE("process bundle data group failed");
    }
}

void BundleDataMgr::HandleGroupIdAndIndex(
    const std::set<std::string> errorGroupIds,
    std::map<int32_t, std::string> &indexMap,
    std::map<std::string, int32_t> &groupIdMap)
{
    if (errorGroupIds.empty() || indexMap.empty() || groupIdMap.empty()) {
        return;
    }
    for (const auto &groupId : errorGroupIds) {
        if (groupIdMap.find(groupId) != groupIdMap.end()) {
            continue;
        }
        int32_t groupIndex = DATA_GROUP_INDEX_START;
        for (int32_t index = DATA_GROUP_INDEX_START; index < DATA_GROUP_UID_OFFSET; ++index) {
            if (indexMap.find(index) == indexMap.end()) {
                groupIndex = index;
                break;
            }
        }
        groupIdMap[groupId] = groupIndex;
        indexMap[groupIndex] = groupId;
    }
}

bool BundleDataMgr::HandleErrorDataGroupInfos(
    const std::map<std::string, int32_t> &groupIdMap,
    const std::map<std::string, std::set<std::string>> &needProcessGroupInfoBundleNames)
{
    if (groupIdMap.empty() || needProcessGroupInfoBundleNames.empty()) {
        return true;
    }
    bool ret = true;
    for (const auto &item : needProcessGroupInfoBundleNames) {
        auto bundleInfoIter = bundleInfos_.find(item.first);
        if (bundleInfoIter == bundleInfos_.end()) {
            ret = false;
            continue;
        }
        std::unordered_map<std::string, std::vector<DataGroupInfo>> dataGroupInfos =
            bundleInfoIter->second.GetDataGroupInfos();
        if (dataGroupInfos.empty()) {
            continue;
        }
        auto userIds = bundleInfoIter->second.GetUsers();
        for (const auto &groudId : item.second) {
            auto groupIndexIter = groupIdMap.find(groudId);
            if (groupIndexIter == groupIdMap.end()) {
                APP_LOGW("id map not found group %{public}s", groudId.c_str());
                ret = false;
                continue;
            }
            auto dataGroupInfoIter = dataGroupInfos.find(groudId);
            if ((dataGroupInfoIter == dataGroupInfos.end()) || dataGroupInfoIter->second.empty()) {
                continue;
            }
            for (int32_t userId : userIds) {
                DataGroupInfo dataGroupInfo;
                dataGroupInfo.dataGroupId = groudId;
                dataGroupInfo.userId = userId;
                dataGroupInfo.uuid = dataGroupInfoIter->second[0].uuid;
                int32_t uid = userId * Constants::BASE_USER_RANGE + groupIndexIter->second + DATA_GROUP_UID_OFFSET;
                dataGroupInfo.uid = uid;
                dataGroupInfo.gid = uid;
                bundleInfoIter->second.AddDataGroupInfo(groudId, dataGroupInfo);
            }
        }
        if (!dataStorage_->SaveStorageBundleInfo(bundleInfoIter->second)) {
            APP_LOGE("SaveStorageBundleInfo bundle %{public}s failed", item.first.c_str());
            ret = false;
        }
    }
    return ret;
}

#ifdef BUNDLE_FRAMEWORK_OVERLAY_INSTALLATION
bool BundleDataMgr::UpdateOverlayInfo(const InnerBundleInfo &newInfo, InnerBundleInfo &oldInfo)
{
    InnerBundleInfo targetInnerBundleInfo;
    std::string targetBundleName = newInfo.GetTargetBundleName();
    auto targetInfoItem = bundleInfos_.find(targetBundleName);
    if (targetInfoItem != bundleInfos_.end()) {
        targetInnerBundleInfo = targetInfoItem->second;
    }

    if (OverlayDataMgr::GetInstance()->UpdateOverlayInfo(newInfo, oldInfo, targetInnerBundleInfo) != ERR_OK) {
        APP_LOGW("update overlay info failed");
        return false;
    }
    // storage target bundle info
    if (!targetInnerBundleInfo.GetBundleName().empty() &&
        dataStorage_->SaveStorageBundleInfo(targetInnerBundleInfo)) {
        bundleInfos_.at(targetInnerBundleInfo.GetBundleName()) = targetInnerBundleInfo;
    }
    // build overlay connection for external overlay
    if (newInfo.GetOverlayType() == NON_OVERLAY_TYPE) {
        const auto &moduleInfos = newInfo.GetInnerModuleInfos();
        std::string moduleName = (moduleInfos.begin()->second).moduleName;
        BuildExternalOverlayConnection(moduleName, oldInfo, newInfo.GetUserId());
    }
    return true;
}

void BundleDataMgr::ResetExternalOverlayModuleState(const std::string &bundleName, const std::string &modulePackage)
{
    for (auto &info : bundleInfos_) {
        if (info.second.GetTargetBundleName() != bundleName) {
            continue;
        }
        const auto &innerModuleInfos = info.second.GetInnerModuleInfos();
        for (const auto &moduleInfo : innerModuleInfos) {
            if (moduleInfo.second.targetModuleName == modulePackage) {
                info.second.SetOverlayModuleState(moduleInfo.second.moduleName, OverlayState::OVERLAY_INVALID);
                break;
            }
        }
        if (!dataStorage_->SaveStorageBundleInfo(info.second)) {
            APP_LOGW("update storage success bundle:%{public}s", info.second.GetBundleName().c_str());
        }
    }
}

void BundleDataMgr::BuildExternalOverlayConnection(const std::string &moduleName, InnerBundleInfo &oldInfo,
    int32_t userId)
{
    APP_LOGD("start to update external overlay connection of module %{public}s under user %{public}d",
        moduleName.c_str(), userId);
    for (auto &info : bundleInfos_) {
        if (info.second.GetTargetBundleName() != oldInfo.GetBundleName()) {
            continue;
        }
        // check target bundle is preInstall application
        if (!oldInfo.IsPreInstallApp()) {
            APP_LOGW("target bundle is not preInstall application");
            return;
        }

        // check fingerprint of current bundle with target bundle
        if (oldInfo.GetCertificateFingerprint() != info.second.GetCertificateFingerprint()) {
            APP_LOGW("target bundle has different fingerprint with current bundle");
            return;
        }
        // external overlay does not support FA model
        if (!oldInfo.GetIsNewVersion()) {
            APP_LOGW("target bundle is not stage model");
            return;
        }
        // external overlay does not support service
        if (oldInfo.GetEntryInstallationFree()) {
            APP_LOGW("target bundle is service");
            return;
        }

        const auto &innerModuleInfos = info.second.GetInnerModuleInfos();
        std::vector<std::string> overlayModuleVec;
        for (const auto &moduleInfo : innerModuleInfos) {
            if (moduleInfo.second.targetModuleName != moduleName) {
                continue;
            }
            OverlayModuleInfo overlayModuleInfo;
            overlayModuleInfo.bundleName = info.second.GetBundleName();
            overlayModuleInfo.moduleName = moduleInfo.second.moduleName;
            overlayModuleInfo.targetModuleName = moduleInfo.second.targetModuleName;
            overlayModuleInfo.hapPath = info.second.GetModuleHapPath(moduleInfo.second.moduleName);
            overlayModuleInfo.priority = moduleInfo.second.targetPriority;
            oldInfo.AddOverlayModuleInfo(overlayModuleInfo);
            overlayModuleVec.emplace_back(moduleInfo.second.moduleName);
        }
        std::string bundleDir;
        const std::string &moduleHapPath =
            info.second.GetModuleHapPath((innerModuleInfos.begin()->second).moduleName);
        OverlayDataMgr::GetInstance()->GetBundleDir(moduleHapPath, bundleDir);
        OverlayBundleInfo overlayBundleInfo;
        overlayBundleInfo.bundleName = info.second.GetBundleName();
        overlayBundleInfo.bundleDir = bundleDir;
        overlayBundleInfo.state = info.second.GetOverlayState();
        overlayBundleInfo.priority = info.second.GetTargetPriority();
        oldInfo.AddOverlayBundleInfo(overlayBundleInfo);
        auto userSet = GetAllUser();
        for (const auto &innerUserId : userSet) {
            for (const auto &overlayModule : overlayModuleVec) {
                int32_t state = OverlayState::OVERLAY_INVALID;
                info.second.GetOverlayModuleState(overlayModule, innerUserId, state);
                if (state == OverlayState::OVERLAY_INVALID) {
                    info.second.SetOverlayModuleState(overlayModule, OVERLAY_ENABLE, innerUserId);
                    // need save info
                    if (!dataStorage_->SaveStorageBundleInfo(info.second)) {
                        APP_LOGE("update storage bundle:%{public}s failed", info.second.GetBundleName().c_str());
                    }
                }
            }
        }
    }
}

void BundleDataMgr::RemoveOverlayInfoAndConnection(const InnerBundleInfo &innerBundleInfo,
    const std::string &bundleName)
{
    if (innerBundleInfo.GetOverlayType() == OVERLAY_EXTERNAL_BUNDLE) {
        std::string targetBundleName = innerBundleInfo.GetTargetBundleName();
        auto targetInfoItem = bundleInfos_.find(targetBundleName);
        if (targetInfoItem == bundleInfos_.end()) {
            APP_LOGW("target bundle(%{public}s) is not installed", targetBundleName.c_str());
        } else {
            InnerBundleInfo targetInnerBundleInfo = bundleInfos_.at(targetBundleName);
            OverlayDataMgr::GetInstance()->RemoveOverlayBundleInfo(bundleName, targetInnerBundleInfo);
            if (dataStorage_->SaveStorageBundleInfo(targetInnerBundleInfo)) {
                APP_LOGD("update storage success bundle:%{public}s", bundleName.c_str());
                bundleInfos_.at(targetBundleName) = targetInnerBundleInfo;
            }
        }
    }

    if (innerBundleInfo.GetOverlayType() == NON_OVERLAY_TYPE) {
        for (auto &info : bundleInfos_) {
            if (info.second.GetTargetBundleName() != bundleName) {
                continue;
            }
            const auto &innerModuleInfos = info.second.GetInnerModuleInfos();
            for (const auto &moduleInfo : innerModuleInfos) {
                info.second.SetOverlayModuleState(moduleInfo.second.moduleName, OverlayState::OVERLAY_INVALID);
            }
            dataStorage_->SaveStorageBundleInfo(info.second);
        }
    }
}
#endif

bool BundleDataMgr::GetOldAppIds(const std::string &bundleName, std::vector<std::string> &appIds) const
{
    if (bundleName.empty()) {
        APP_LOGE("bundleName is empty");
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto innerBundleInfo = bundleInfos_.find(bundleName);
    if (innerBundleInfo == bundleInfos_.end()) {
        APP_LOGE("can not find bundle %{public}s", bundleName.c_str());
        return false;
    }
    appIds = innerBundleInfo->second.GetOldAppIds();
    return true;
}

bool BundleDataMgr::IsUpdateInnerBundleInfoSatisified(const InnerBundleInfo &oldInfo,
    const InnerBundleInfo &newInfo) const
{
    return newInfo.GetApplicationBundleType() == BundleType::APP_SERVICE_FWK ||
        !oldInfo.HasEntry() || newInfo.HasEntry() ||
        (oldInfo.GetApplicationBundleType() == BundleType::ATOMIC_SERVICE &&
        oldInfo.GetVersionCode() != newInfo.GetVersionCode());
}

std::string BundleDataMgr::GetModuleNameByBundleAndAbility(
    const std::string& bundleName, const std::string& abilityName)
{
    if (bundleName.empty() || abilityName.empty()) {
        APP_LOGE("bundleName or abilityName is empty");
        return std::string();
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto innerBundleInfo = bundleInfos_.find(bundleName);
    if (innerBundleInfo == bundleInfos_.end()) {
        APP_LOGE("can not find bundle %{public}s", bundleName.c_str());
        return std::string();
    }
    auto abilityInfo = innerBundleInfo->second.FindAbilityInfoV9(Constants::EMPTY_STRING, abilityName);
    if (!abilityInfo) {
        APP_LOGE("bundleName:%{public}s, abilityName:%{public}s can find moduleName",
            bundleName.c_str(), abilityName.c_str());
        return std::string();
    }
    return abilityInfo->moduleName;
}

ErrCode BundleDataMgr::SetAdditionalInfo(const std::string& bundleName, const std::string& additionalInfo) const
{
    APP_LOGD("Called. BundleName: %{public}s", bundleName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGE("BundleName: %{public}s does not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }

    if (infoItem->second.GetApplicationBundleType() != BundleType::SHARED) {
        int32_t userId = AccountHelper::GetOsAccountLocalIdFromUid(IPCSkeleton::GetCallingUid());
        int32_t responseUserId = infoItem->second.GetResponseUserId(userId);
        if (responseUserId == Constants::INVALID_USERID) {
            APP_LOGE("BundleName: %{public}s does not exist in current userId", bundleName.c_str());
            return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
        }
    }

    auto appProvisionInfoManager = DelayedSingleton<AppProvisionInfoManager>::GetInstance();
    if (appProvisionInfoManager == nullptr) {
        APP_LOGE("Failed, appProvisionInfoManager is nullptr");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    if (!appProvisionInfoManager->SetAdditionalInfo(bundleName, additionalInfo)) {
        APP_LOGE("BundleName: %{public}s set additional info failed", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    ElementName element;
    element.SetBundleName(bundleName);
    OHOS::AAFwk::Want want;
    want.SetAction(BMS_EVENT_ADDITIONAL_INFO_CHANGED);
    want.SetElement(element);
    EventFwk::CommonEventData commonData { want };
    NotifyBundleEventCallback(commonData);
    return ERR_OK;
}

ErrCode BundleDataMgr::GetAppServiceHspBundleInfo(const std::string &bundleName, BundleInfo &bundleInfo)
{
    APP_LOGD("start, bundleName:%{public}s", bundleName.c_str());
    if (bundleName.empty()) {
        APP_LOGE("bundleName is empty");
        return ERR_BUNDLE_MANAGER_INVALID_PARAMETER;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGE("can not find bundle %{public}s", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    const InnerBundleInfo &innerBundleInfo = infoItem->second;
    auto res = innerBundleInfo.GetAppServiceHspInfo(bundleInfo);
    if (res != ERR_OK) {
        APP_LOGW("get hspInfo %{public}s fail", bundleName.c_str());
        return res;
    }
    return ERR_OK;
}

void BundleDataMgr::ConvertServiceHspToSharedBundleInfo(const InnerBundleInfo &innerBundleInfo,
    std::vector<BaseSharedBundleInfo> &baseSharedBundleInfos) const
{
    APP_LOGD("start");
    BundleInfo bundleInfo;
    if (innerBundleInfo.GetAppServiceHspInfo(bundleInfo) == ERR_OK) {
        APP_LOGD("get app service hsp bundleName:%{public}s", innerBundleInfo.GetBundleName().c_str());
        for (const auto &hapModule : bundleInfo.hapModuleInfos) {
            BaseSharedBundleInfo baseSharedBundleInfo;
            baseSharedBundleInfo.bundleName = bundleInfo.name;
            baseSharedBundleInfo.moduleName = hapModule.moduleName;
            baseSharedBundleInfo.versionCode = bundleInfo.versionCode;
            baseSharedBundleInfo.nativeLibraryPath = hapModule.nativeLibraryPath;
            baseSharedBundleInfo.hapPath = hapModule.hapPath;
            baseSharedBundleInfo.moduleArkTSMode = hapModule.moduleArkTSMode;
            baseSharedBundleInfo.compressNativeLibs = hapModule.compressNativeLibs;
            baseSharedBundleInfo.nativeLibraryFileNames = hapModule.nativeLibraryFileNames;
            baseSharedBundleInfos.emplace_back(baseSharedBundleInfo);
        }
        return;
    }
    APP_LOGW("GetAppServiceHspInfo failed, bundleName:%{public}s", innerBundleInfo.GetBundleName().c_str());
}

void BundleDataMgr::AddAppHspBundleName(const BundleType type, const std::string &bundleName)
{
    if (type == BundleType::APP_SERVICE_FWK) {
        APP_LOGD("add app hsp bundleName:%{public}s", bundleName.c_str());
        std::lock_guard<std::mutex> hspLock(hspBundleNameMutex_);
        appServiceHspBundleName_.insert(bundleName);
    }
}

ErrCode BundleDataMgr::CreateBundleDataDir(int32_t userId)
{
    APP_LOGI("with -u %{public}d begin", userId);
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    std::vector<CreateDirParam> createDirParams;
    std::vector<CreateDirParam> el5Params;
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &info = item.second;
        int32_t responseUserId = info.GetResponseUserId(userId);
        if (responseUserId == Constants::INVALID_USERID) {
            APP_LOGW("bundle %{public}s is not installed in user %{public}d or 0",
                info.GetBundleName().c_str(), userId);
            continue;
        }
        CreateDirParam createDirParam;
        createDirParam.bundleName = info.GetBundleName();
        createDirParam.userId = responseUserId;
        createDirParam.uid = info.GetUid(responseUserId);
        createDirParam.gid = info.GetGid(responseUserId);
        createDirParam.apl = info.GetAppPrivilegeLevel();
        createDirParam.isPreInstallApp = info.IsPreInstallApp();
        createDirParam.debug = info.GetBaseApplicationInfo().appProvisionType == Constants::APP_PROVISION_TYPE_DEBUG;
        createDirParam.createDirFlag = CreateDirFlag::CREATE_DIR_UNLOCKED;
        createDirParam.extensionDirs = info.GetAllExtensionDirs();
        createDirParams.emplace_back(createDirParam);

        std::vector<RequestPermission> reqPermissions = info.GetAllRequestPermissions();
        auto it = std::find_if(reqPermissions.begin(), reqPermissions.end(), [](const RequestPermission& permission) {
            return permission.name == ServiceConstants::PERMISSION_PROTECT_SCREEN_LOCK_DATA;
        });
        if (it != reqPermissions.end()) {
            el5Params.emplace_back(createDirParam);
        }
        CreateAppGroupDir(info, responseUserId);
    }
    lock.unlock();
    APP_LOGI("begin create dirs");
    auto res = InstalldClient::GetInstance()->CreateBundleDataDirWithVector(createDirParams);
    APP_LOGI("end, res %{public}d", res);
    CreateEl5Dir(el5Params, true);
    return res;
}

ErrCode BundleDataMgr::CreateBundleDataDirWithEl(int32_t userId, DataDirEl dirEl)
{
    APP_LOGI("with -u %{public}d -el %{public}d begin", userId, static_cast<uint8_t>(dirEl));
    std::vector<CreateDirParam> createDirParams;
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        for (const auto &item : bundleInfos_) {
            const InnerBundleInfo &info = item.second;
            if (!info.HasInnerBundleUserInfo(userId)) {
                APP_LOGW("bundle %{public}s is not installed in user %{public}d or 0",
                    info.GetBundleName().c_str(), userId);
                continue;
            }
            if (dirEl == DataDirEl::EL5 && !info.NeedCreateEl5Dir()) {
                continue;
            }
            CreateDirParam createDirParam;
            createDirParam.bundleName = info.GetBundleName();
            createDirParam.userId = userId;
            createDirParam.uid = info.GetUid(userId);
            createDirParam.gid = info.GetGid(userId);
            createDirParam.apl = info.GetAppPrivilegeLevel();
            createDirParam.isPreInstallApp = info.IsPreInstallApp();
            createDirParam.debug =
                info.GetBaseApplicationInfo().appProvisionType == Constants::APP_PROVISION_TYPE_DEBUG;
            createDirParam.extensionDirs = info.GetAllExtensionDirs();
            createDirParam.createDirFlag = CreateDirFlag::CREATE_DIR_UNLOCKED;
            createDirParam.dataDirEl = dirEl;
            createDirParams.emplace_back(createDirParam);
            CreateAppGroupDir(info, userId, dirEl);
        }
    }
    ErrCode res = ERR_OK;
    if (dirEl != DataDirEl::EL5) {
        res = InstalldClient::GetInstance()->CreateBundleDataDirWithVector(createDirParams);
    } else {
        CreateEl5Dir(createDirParams, true);
    }
    APP_LOGI("with -u %{public}d -el %{public}d end", userId, static_cast<uint8_t>(dirEl));
    return res;
}

void BundleDataMgr::CreateEl5Dir(const std::vector<CreateDirParam> &el5Params, bool needSaveStorage)
{
    for (const auto &el5Param : el5Params) {
        APP_LOGI("-n %{public}s -u %{public}d -i %{public}d",
            el5Param.bundleName.c_str(), el5Param.userId, el5Param.appIndex);
        InnerCreateEl5Dir(el5Param);
        SetEl5DirPolicy(el5Param, needSaveStorage);
    }
}

void BundleDataMgr::CreateEl5DirNoCache(const std::vector<CreateDirParam> &el5Params, InnerBundleInfo &info)
{
    for (const auto &el5Param : el5Params) {
        APP_LOGI("-n %{public}s -u %{public}d -i %{public}d",
            el5Param.bundleName.c_str(), el5Param.userId, el5Param.appIndex);
        InnerCreateEl5Dir(el5Param);
        std::string keyId = "";
        SetEl5DirPolicy(el5Param, info, keyId);
    }
}

int32_t BundleDataMgr::GetUidByBundleName(const std::string &bundleName, int32_t userId, int32_t appIndex) const
{
    if (bundleName.empty()) {
        APP_LOGW("bundleName is empty");
        return Constants::INVALID_UID;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGD("GetUidByBundleName not found %{public}s", bundleName.c_str());
        return Constants::INVALID_UID;
    }
    const InnerBundleInfo &innerBundleInfo = infoItem->second;
    if (userId == Constants::UNSPECIFIED_USERID) {
        userId = GetUserIdByCallingUid();
    }
    int32_t responseUserId = innerBundleInfo.GetResponseUserId(userId);
    return innerBundleInfo.GetUid(responseUserId, appIndex);
}

void BundleDataMgr::InnerCreateEl5Dir(const CreateDirParam &el5Param)
{
    std::string parentDir = std::string(ServiceConstants::SCREEN_LOCK_FILE_DATA_PATH) +
        ServiceConstants::PATH_SEPARATOR + std::to_string(el5Param.userId);
    if (!BundleUtil::IsExistDir(parentDir)) {
        APP_LOGE("parent dir(%{public}s) missing: el5", parentDir.c_str());
        return;
    }
    std::vector<std::string> dirs;
    std::string bundleNameDir = el5Param.bundleName;
    if (el5Param.appIndex > 0) {
        bundleNameDir = BundleCloneCommonHelper::GetCloneDataDir(el5Param.bundleName, el5Param.appIndex);
    }
    dirs.emplace_back(parentDir + ServiceConstants::BASE + bundleNameDir);
    dirs.emplace_back(parentDir + ServiceConstants::DATABASE + bundleNameDir);
    for (const std::string &dir : dirs) {
        uint32_t mode = S_IRWXU;
        if (dir.find(ServiceConstants::DATABASE) != std::string::npos) {
            mode = S_IRWXU | S_IRWXG;
        }
        if (InstalldClient::GetInstance()->Mkdir(dir, mode, el5Param.uid, el5Param.uid) != ERR_OK) {
            LOG_NOFUNC_W(BMS_TAG_INSTALLER, "create el5 dir %{public}s failed", dir.c_str());
        }
        ErrCode result = InstalldClient::GetInstance()->SetDirApl(
            dir, el5Param.bundleName, el5Param.apl, el5Param.isPreInstallApp, el5Param.debug, el5Param.uid);
        if (result != ERR_OK) {
            LOG_NOFUNC_W(BMS_TAG_INSTALLER, "fail to SetDirApl dir %{public}s, error is %{public}d", dir.c_str(), result);
        }
    }
}

void BundleDataMgr::SetEl5DirPolicy(const CreateDirParam &el5Param, bool needSaveStorage)
{
    InnerBundleInfo info;
    if (!FetchInnerBundleInfo(el5Param.bundleName, info)) {
        LOG_E(BMS_TAG_INSTALLER, "get bundle %{public}s failed", el5Param.bundleName.c_str());
        return;
    }
    std::string keyId = "";
    SetEl5DirPolicy(el5Param, info, keyId);
}

void BundleDataMgr::SetEl5DirPolicy(const CreateDirParam &el5Param, InnerBundleInfo &info, std::string &keyId)
{
    int32_t uid = el5Param.uid;
    std::string bundleName = info.GetBundleName();
    if (el5Param.appIndex > 0) {
        bundleName = BundleCloneCommonHelper::GetCloneDataDir(bundleName, el5Param.appIndex);
    }
    EncryptionParam encryptionParam(bundleName, "", uid, el5Param.userId, EncryptionDirType::APP);
    auto result = InstalldClient::GetInstance()->SetEncryptionPolicy(encryptionParam, keyId);
    if (result != ERR_OK) {
        LOG_E(BMS_TAG_INSTALLER, "SetEncryptionPolicy failed");
    }
    LOG_D(BMS_TAG_INSTALLER, "%{public}s, keyId: %{public}s", bundleName.c_str(), keyId.c_str());
}

ErrCode BundleDataMgr::CanOpenLink(
    const std::string &link, bool &canOpen) const
{
    APP_LOGD("link: %{public}s", link.c_str());
    auto uid = IPCSkeleton::GetCallingUid();
    int32_t appIndex = 0;
    std::string bundleName;
    ErrCode ret = GetBundleNameAndIndex(uid, bundleName, appIndex);
    if (ret != ERR_OK) {
        APP_LOGW("uid: %{public}d invalid!", uid);
        return ret;
    }
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        const InnerBundleInfo* innerBundleInfo = nullptr;
        if (GetInnerBundleInfoNoLock(bundleName, uid, appIndex, innerBundleInfo) != ERR_OK) {
            APP_LOGE("get innerBundleInfo by uid :%{public}d failed", uid);
            return ERR_BUNDLE_MANAGER_SCHEME_NOT_IN_QUERYSCHEMES;
        }
        if (!innerBundleInfo) {
            LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by CanOpenLink is null.");
            return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
        }
        auto querySchemes = innerBundleInfo->GetQuerySchemes();
        if (querySchemes.empty()) {
            APP_LOGI("querySchemes is empty");
            return ERR_BUNDLE_MANAGER_SCHEME_NOT_IN_QUERYSCHEMES;
        }

        size_t pos = link.find(SCHEME_END);
        if (pos == std::string::npos) {
            APP_LOGD("parse link : %{public}s failed", link.c_str());
            return ERR_BUNDLE_MANAGER_INVALID_SCHEME;
        }
        std::string scheme = link.substr(0, pos);
        transform(scheme.begin(), scheme.end(), scheme.begin(), ::tolower);
        if (std::find(querySchemes.begin(), querySchemes.end(), scheme) == querySchemes.end()) {
            APP_LOGI("scheme :%{public}s is not in the querySchemes", scheme.c_str());
            return ERR_BUNDLE_MANAGER_SCHEME_NOT_IN_QUERYSCHEMES;
        }
    }

    ret = FindMatchedAbilityForLink(
        link, static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_DEFAULT), GetUserIdByUid(uid), canOpen);
    if (ret != ERR_OK) {
        APP_LOGD("FindMatchedAbilityForLink error");
        return ERR_BUNDLE_MANAGER_SCHEME_NOT_IN_QUERYSCHEMES;
    }
    APP_LOGI_NOFUNC("link:%{public}s canOpen:%{public}d", link.c_str(), canOpen);
    return ERR_OK;
}

void BundleDataMgr::GenerateOdid(const std::string &developerId, std::string &odid) const
{
    APP_LOGD("GenerateOdid start, developerId:%{public}s", developerId.c_str());
    if (developerId.empty()) {
        APP_LOGE_NOFUNC("developerId is empty");
        return;
    }

    // Acquire lock and call the no-lock version
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    odid = GenerateOdidNoLock(developerId);
}

std::string BundleDataMgr::GenerateOdidNoLock(const std::string &developerId) const
{
    APP_LOGD("GenerateOdidNoLock start, developerId:%{public}s", developerId.c_str());
    if (developerId.empty()) {
        APP_LOGE_NOFUNC("developerId is empty");
        return "";
    }
    std::string groupId = BundleUtil::ExtractGroupIdByDevelopId(developerId);
    // Check if odid already exists in bundleInfos_ (caller must hold bundleInfoMutex_)
    for (const auto &item : bundleInfos_) {
        std::string developerIdExist;
        std::string odidExist;
        item.second.GetDeveloperidAndOdid(developerIdExist, odidExist);
        std::string groupIdExist = BundleUtil::ExtractGroupIdByDevelopId(developerIdExist);
        if (groupId == groupIdExist) {
            // Found existing odid for this groupId
            APP_LOGI_NOFUNC("found existing odid:%{private}s for groupId:%{public}s",
                odidExist.c_str(), groupId.c_str());
            return odidExist;
        }
    }

    // No existing odid found, generate new one
    std::string newOdid = GenerateUuid();
    APP_LOGI_NOFUNC("generated new odid:%{private}s for groupId:%{public}s, developerId:%{public}s",
        newOdid.c_str(), groupId.c_str(), developerId.c_str());
    return newOdid;
}

ErrCode BundleDataMgr::GetOdid(std::string &odid) const
{
    int32_t callingUid = IPCSkeleton::GetCallingUid();
    int32_t appIndex = 0;
    std::string bundleName;
    ErrCode ret = GetBundleNameAndIndex(callingUid, bundleName, appIndex);
    if (ret != ERR_OK) {
        APP_LOGW("uid: %{public}d invalid!", callingUid);
        return ret;
    }  
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;
    InnerBundleInfo sandboxInfo;
    if (GetInnerBundleInfoNoLock(bundleName, callingUid, appIndex, innerBundleInfo) != ERR_OK) {
        if (sandboxAppHelper_ == nullptr) {
            APP_LOGE("sandboxAppHelper_ is nullptr");
            return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
        }
        if (sandboxAppHelper_->GetInnerBundleInfoByUid(callingUid, sandboxInfo) != ERR_OK) {
            APP_LOGW("app that corresponds to the callingUid %{public}d could not be found", callingUid);
            return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
        }
        innerBundleInfo = &sandboxInfo;
    }
    
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetOdid is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    std::string developerId;
    innerBundleInfo->GetDeveloperidAndOdid(developerId, odid);
    return ERR_OK;
}

ErrCode BundleDataMgr::GetOdidByBundleName(const std::string &bundleName, std::string &odid) const
{
    APP_LOGI_NOFUNC("start GetOdidByBundleName -n %{public}s", bundleName.c_str());
    InnerBundleInfo innerBundleInfo;
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const auto &item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("bundleName: %{public}s is not found", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    const InnerBundleInfo &bundleInfo = item->second;
    bundleInfo.GetOdid(odid);
    return ERR_OK;
}

ErrCode BundleDataMgr::GetOdidResetCount(const std::string &bundleName, std::string &odid, int32_t &count)
{
    APP_LOGD("GetOdidResetCount start, bundleName:%{public}s", bundleName.c_str());
    if (bundleName.empty()) {
        APP_LOGE("bundleName is empty");
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("bundle:%{public}s not found", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    infoItem->second.GetOdid(odid);
    FirstInstallBundleInfo lastOdidInfo;
    if (GetFirstInstallBundleInfo(bundleName, Constants::ALL_USERID, lastOdidInfo)) {
        count = lastOdidInfo.odidResetCount;
    } else {
        count = 0;
    }
    APP_LOGD("GetOdidResetCount success, bundleName:%{public}s, count:%{public}d", bundleName.c_str(), count);
    return ERR_OK;
}

void BundleDataMgr::HandleOTACodeEncryption()
{
    int32_t timerId =
        XCollieHelper::SetRecoveryTimer(FUNCATION_HANDLE_OTA_CODE_ENCRYPTION, OTA_CODE_ENCRYPTION_TIMEOUT);
    ScopeGuard cancelTimerIdGuard([timerId] { XCollieHelper::CancelTimer(timerId); });
    APP_LOGI("begin");
    std::vector<std::string> withoutKeyBundles;
    std::vector<std::string> withKeyBundles;
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        for (const auto &item : bundleInfos_) {
            item.second.HandleOTACodeEncryption(withoutKeyBundles, withKeyBundles);
        }
    }
    for (const std::string &bundleName : withKeyBundles) {
        UpdateAppEncryptedStatus(bundleName, true, 0, true);
    }
    for (const std::string &bundleName : withoutKeyBundles) {
        UpdateAppEncryptedStatus(bundleName, false, 0, true);
    }
    APP_LOGI("end");
}

ErrCode BundleDataMgr::HandleDetermineCloneNumList(
    const std::vector<std::tuple<std::string, std::string, uint32_t>> &determineCloneNumList)
{
    APP_LOGI_NOFUNC("HandleDetermineCloneNumList begin");
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto& [bundleName, appIdentifier, cloneNum] : determineCloneNumList) {
        if (bundleName.empty()) {
            APP_LOGW_NOFUNC("determineClone bundleName empty");
            continue;
        }
        auto infoItem = bundleInfos_.find(bundleName);
        if (infoItem == bundleInfos_.end()) {
            APP_LOGW_NOFUNC("%{public}s does not exist", bundleName.c_str());
            continue;
        }
        if (infoItem->second.GetAppIdentifier() != appIdentifier) {
            APP_LOGW_NOFUNC("%{public}s check appIdentidier failed", bundleName.c_str());
            continue;
        }
        if (infoItem->second.GetMultiAppModeType() != MultiAppModeType::UNSPECIFIED &&
            infoItem->second.GetMultiAppModeType() != MultiAppModeType::APP_CLONE) {
            APP_LOGW_NOFUNC("%{public}s mutiAppMode is %{public}d",
                bundleName.c_str(), static_cast<int32_t>(infoItem->second.GetMultiAppModeType()));
            continue;
        }
        if (static_cast<uint32_t>(infoItem->second.GetMultiAppMaxCount()) >= cloneNum) {
            APP_LOGW_NOFUNC("%{public}s cloneNum is smaller no need to refresh", bundleName.c_str());
            continue;
        }
        MultiAppModeData multiAppMode;
        multiAppMode.multiAppModeType = MultiAppModeType::APP_CLONE;
        multiAppMode.maxCount = std::min(cloneNum, static_cast<uint32_t>(Constants::CLONE_APP_INDEX_MAX));
        infoItem->second.SetMultiAppMode(multiAppMode);
        if (!dataStorage_->SaveStorageBundleInfo(infoItem->second)) {
            APP_LOGW_NOFUNC("SaveStorageBundleInfo failed -n %{public}s", bundleName.c_str());
        }
        APP_LOGI_NOFUNC("DetermineClone %{public}s %{public}d", bundleName.c_str(), multiAppMode.maxCount);
    }
    APP_LOGI_NOFUNC("HandleDetermineCloneNumList end");
    return ERR_OK;
}

bool BundleDataMgr::GetMultiAppModeTypeByBundleName(const std::string &bundleName, MultiAppModeType &type)
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        return false;
    }
    type = item->second.GetMultiAppModeType();
    return true;
}

void BundleDataMgr::ProcessAllowedAcls(const InnerBundleInfo &newInfo, InnerBundleInfo &oldInfo) const
{
    if (oldInfo.GetVersionCode() != newInfo.GetVersionCode()) {
        oldInfo.SetAllowedAcls(newInfo.GetAllowedAcls());
        return;
    }
    oldInfo.AddAllowedAcls(newInfo.GetAllowedAcls());
}

ErrCode BundleDataMgr::GetAllBundleInfoByDeveloperId(const std::string &developerId,
    std::vector<BundleInfo> &bundleInfos, int32_t userId)
{
    int32_t requestUserId = GetUserId(userId);
    APP_LOGI("requestUserId: %{public}d", requestUserId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &innerBundleInfo = item.second;
        if (innerBundleInfo.GetApplicationBundleType() == BundleType::SHARED ||
            innerBundleInfo.GetApplicationBundleType() == BundleType::APP_SERVICE_FWK) {
            APP_LOGD("app %{public}s is cross-app shared bundle or appService, ignore",
                innerBundleInfo.GetBundleName().c_str());
            continue;
        }

        int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
        auto flag = GET_BASIC_APPLICATION_INFO;
        if (CheckInnerBundleInfoWithFlags(innerBundleInfo, flag, responseUserId) != ERR_OK) {
            continue;
        }
        // check developerId
        std::string developerIdExist;
        std::string odidExist;
        innerBundleInfo.GetDeveloperidAndOdid(developerIdExist, odidExist);
        if (developerIdExist != developerId) {
            continue;
        }

        BundleInfo bundleInfo;

        if (innerBundleInfo.GetBundleInfoV9(static_cast<int32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_APPLICATION),
            bundleInfo, responseUserId) != ERR_OK) {
            continue;
        }
        bundleInfos.emplace_back(bundleInfo);
    }
    if (bundleInfos.empty()) {
        APP_LOGW("bundleInfos is empty");
        return ERR_BUNDLE_MANAGER_INVALID_DEVELOPERID;
    }
    APP_LOGI("have %{public}d applications, their developerId is %{public}s", requestUserId, developerId.c_str());
    return ERR_OK;
}

ErrCode BundleDataMgr::GetDeveloperIds(const std::string &appDistributionType,
    std::vector<std::string> &developerIdList, int32_t userId)
{
    int32_t requestUserId = GetUserId(userId);
    APP_LOGI("requestUserId: %{public}d", requestUserId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    std::set<std::string> developerIdSet;
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &innerBundleInfo = item.second;
        if (innerBundleInfo.GetApplicationBundleType() == BundleType::SHARED ||
            innerBundleInfo.GetApplicationBundleType() == BundleType::APP_SERVICE_FWK) {
            APP_LOGD("app %{public}s is cross-app shared bundle or appService, ignore",
                innerBundleInfo.GetBundleName().c_str());
            continue;
        }

        int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
        auto flag = GET_BASIC_APPLICATION_INFO;
        if (CheckInnerBundleInfoWithFlags(innerBundleInfo, flag, responseUserId) != ERR_OK) {
            continue;
        }
        // check appDistributionType
        if (!appDistributionType.empty() && innerBundleInfo.GetAppDistributionType() != appDistributionType) {
            continue;
        }

        std::string developerIdExist;
        std::string odidExist;
        innerBundleInfo.GetDeveloperidAndOdid(developerIdExist, odidExist);
        developerIdSet.emplace(developerIdExist);
    }
    for (const std::string &developerId : developerIdSet) {
        developerIdList.emplace_back(developerId);
    }
    APP_LOGI("have %{public}d developers, their appDistributionType is %{public}s",
        static_cast<int32_t>(developerIdList.size()), appDistributionType.c_str());
    return ERR_OK;
}

ErrCode BundleDataMgr::SwitchUninstallState(const std::string &bundleName, const bool &state,
    const bool isNeedSendNotify, bool &stateChange)
{
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGE("BundleName: %{public}s does not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    InnerBundleInfo &innerBundleInfo = infoItem->second;
    if (!innerBundleInfo.IsRemovable() && state) {
        APP_LOGW("the bundle : %{public}s is not removable", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_CAN_NOT_BE_UNINSTALLED;
    }
    if (innerBundleInfo.GetUninstallState() == state) {
        stateChange = false;
        return ERR_OK;
    }
    innerBundleInfo.SetUninstallState(state);
    innerBundleInfo.SetNeedSendNotify(isNeedSendNotify);
    if (!dataStorage_->SaveStorageBundleInfo(innerBundleInfo)) {
        APP_LOGW("update storage failed bundle:%{public}s", bundleName.c_str());
        return ERR_APPEXECFWK_SERVICE_INTERNAL_ERROR;
    }
    stateChange = true;
    return ERR_OK;
}

ErrCode BundleDataMgr::SwitchUninstallStateByUserId(const std::string &bundleName, const bool state,
    int32_t userId, bool &stateChange)
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("name %{public}s invalid userid :%{public}d", bundleName.c_str(), userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("BundleName: %{public}s does not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    InnerBundleInfo &innerBundleInfo = item->second;
    if (!innerBundleInfo.IsRemovable() && state) {
        APP_LOGW("the bundle : %{public}s is not removable", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_CAN_NOT_BE_UNINSTALLED;
    }

    auto ret = innerBundleInfo.SetCanUninstall(requestUserId, state, stateChange);
    if (ret != ERR_OK) {
        return ret;
    }
    if (!stateChange) {
        APP_LOGD("CanUninstall is not changed -n %{public}s -u %{public}d", bundleName.c_str(), userId);
        return ERR_OK;
    }
    if (!dataStorage_->SaveStorageBundleInfo(innerBundleInfo)) {
        APP_LOGW("update storage failed bundle:%{public}s", bundleName.c_str());
        innerBundleInfo.SetCanUninstall(requestUserId, !state, stateChange);
        return ERR_APPEXECFWK_SERVICE_INTERNAL_ERROR;
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::AddCloneBundle(const std::string &bundleName, const InnerBundleCloneInfo &attr)
{
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGE("BundleName: %{public}s does not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    InnerBundleInfo &innerBundleInfo = infoItem->second;
    ErrCode res = innerBundleInfo.AddCloneBundle(attr);
    if (res != ERR_OK) {
        APP_LOGE("innerBundleInfo addCloneBundleInfo fail");
        return res;
    }
    APP_LOGD("update bundle info in memory for add clone, userId: %{public}d, appIndex: %{public}d",
        attr.userId, attr.appIndex);
    auto nowBundleStatus = innerBundleInfo.GetBundleStatus();
    innerBundleInfo.SetBundleStatus(InnerBundleInfo::BundleStatus::ENABLED);
    if (!dataStorage_->SaveStorageBundleInfo(innerBundleInfo)) {
        innerBundleInfo.SetBundleStatus(nowBundleStatus);
        innerBundleInfo.RemoveCloneBundle(attr.userId, attr.appIndex);
        APP_LOGW("update storage failed bundle:%{public}s", bundleName.c_str());
        return ERR_APPEXECFWK_SERVICE_INTERNAL_ERROR;
    }
    innerBundleInfo.SetBundleStatus(nowBundleStatus);
    APP_LOGD("update bundle info in storage for add clone, userId: %{public}d, appIndex: %{public}d",
        attr.userId, attr.appIndex);
    return ERR_OK;
}

ErrCode BundleDataMgr::RemoveCloneBundle(const std::string &bundleName, const int32_t userId, int32_t appIndex)
{
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGE("BundleName: %{public}s does not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    InnerBundleInfo &innerBundleInfo = infoItem->second;
    ErrCode res = innerBundleInfo.RemoveCloneBundle(userId, appIndex);
    if (res != ERR_OK) {
        APP_LOGE("innerBundleInfo RemoveCloneBundle fail");
        return res;
    }
    auto nowBundleStatus = innerBundleInfo.GetBundleStatus();
    innerBundleInfo.SetBundleStatus(InnerBundleInfo::BundleStatus::ENABLED);
    if (!dataStorage_->SaveStorageBundleInfo(innerBundleInfo)) {
        innerBundleInfo.SetBundleStatus(nowBundleStatus);
        APP_LOGW("update storage failed bundle:%{public}s", bundleName.c_str());
        return ERR_APPEXECFWK_SERVICE_INTERNAL_ERROR;
    }
    innerBundleInfo.SetBundleStatus(nowBundleStatus);
    ErrCode deleteDesktopRes = DeleteDesktopShortcutInfo(bundleName, userId, appIndex);
    EventReport::SendDesktopShortcutEvent(DesktopShortcutOperation::DELETE, userId, bundleName,
        appIndex, Constants::EMPTY_STRING, IPCSkeleton::GetCallingUid(), deleteDesktopRes);
    if (DeleteShortcutVisibleInfo(bundleName, userId, appIndex) != ERR_OK) {
        APP_LOGE("DeleteShortcutVisibleInfo failed, bundleName: %{public}s, userId: %{public}d, appIndex: %{public}d",
            bundleName.c_str(), userId, appIndex);
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::QueryAbilityInfoByContinueType(const std::string &bundleName,
    const std::string &continueType, AbilityInfo &abilityInfo, int32_t userId, int32_t appIndex) const
{
    int32_t requestUserId = GetUserId(userId);
    APP_LOGI("requestUserId: %{public}d", requestUserId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    const InnerBundleInfo* innerBundleInfo = nullptr;
    if (appIndex == 0) {
        ErrCode ret = GetInnerBundleInfoWithFlagsV9(bundleName, 0, innerBundleInfo, requestUserId);
        if (ret != ERR_OK) {
            APP_LOGD("QueryAbilityInfoByContinueType failed, bundleName:%{public}s", bundleName.c_str());
            return ret;
        }
    }
    InnerBundleInfo sandboxInfo;
    if (appIndex > 0) {
        if (sandboxAppHelper_ == nullptr) {
            APP_LOGW("sandboxAppHelper_ is nullptr");
            return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
        }
        auto ret = sandboxAppHelper_->GetSandboxAppInfo(bundleName, appIndex, requestUserId, sandboxInfo);
        if (ret != ERR_OK) {
            APP_LOGD("obtain innerBundleInfo of sandbox app failed due to errCode %{public}d, bundleName:%{public}s",
                ret, bundleName.c_str());
            return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
        }
        innerBundleInfo = &sandboxInfo;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by QueryAbilityInfoByContinueType is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    auto ability = innerBundleInfo->FindAbilityInfo(continueType, requestUserId);
    if (!ability) {
        APP_LOGW("ability not found, bundleName:%{public}s, coutinueType:%{public}s",
            bundleName.c_str(), continueType.c_str());
        return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
    }
    abilityInfo = (*ability);
    const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
    if (innerBundleInfo->GetInnerBundleUserInfo(userId, innerBundleUserInfoPtr)) {
        if (!innerBundleUserInfoPtr) {
            LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by QueryAbilityInfoByContinueType is null");
            return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
        }
        abilityInfo.uid = innerBundleUserInfoPtr->uid;
    }
    return ERR_OK;
}

void BundleDataMgr::FilterAbilityInfosByAppLinking(const Want &want, int32_t flags,
    std::vector<AbilityInfo> &abilityInfos) const
{
#ifdef APP_DOMAIN_VERIFY_ENABLED
    APP_LOGD("FilterAbility start");
    if (abilityInfos.empty()) {
        APP_LOGD("abilityInfos is empty");
        return;
    }
    if (want.GetUriString().rfind(SCHEME_HTTPS, 0) != 0) {
        APP_LOGD("scheme is not https");
        if (HasAppLinkingFlag(static_cast<uint32_t>(flags))) {
            APP_LOGI("using app linking flag and scheme is not https, return empty list");
            abilityInfos.clear();
        }
        return;
    }
    std::vector<AbilityInfo> filteredAbilityInfos;
    // call FiltedAbilityInfos
    APP_LOGI("call FilterAbilities");
    std::string identity = IPCSkeleton::ResetCallingIdentity();
    if (!DelayedSingleton<AppDomainVerify::AppDomainVerifyMgrClient>::GetInstance()->FilterAbilities(
        want, abilityInfos, filteredAbilityInfos)) {
        APP_LOGE("FilterAbilities failed");
    }
    IPCSkeleton::SetCallingIdentity(identity);
    if (HasAppLinkingFlag(static_cast<uint32_t>(flags))) {
        APP_LOGD("return filteredAbilityInfos");
        abilityInfos = filteredAbilityInfos;
        for (auto &abilityInfo : abilityInfos) {
            abilityInfo.linkType = LinkType::APP_LINK;
        }
        return;
    }
    for (auto &filteredAbilityInfo : filteredAbilityInfos) {
        for (auto &abilityInfo : abilityInfos) {
            if (filteredAbilityInfo.bundleName == abilityInfo.bundleName &&
                filteredAbilityInfo.name == abilityInfo.name &&
                filteredAbilityInfo.appIndex == abilityInfo.appIndex) {
                abilityInfo.linkType = LinkType::APP_LINK;
                break;
            }
        }
    }
    return;
#else
    APP_LOGI("AppDomainVerify is not enabled");
    if (HasAppLinkingFlag(static_cast<uint32_t>(flags))) {
        APP_LOGI("has flag and return empty list");
        abilityInfos.clear();
    }
    return;
#endif
}

bool BundleDataMgr::HasAppLinkingFlag(uint32_t flags)
{
    return (flags & static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_APP_LINKING)) ==
        static_cast<uint32_t>(GetAbilityInfoFlag::GET_ABILITY_INFO_WITH_APP_LINKING);
}

bool BundleDataMgr::HasOnlySharedModules(const InnerBundleInfo &innerBundleInfo)
{
    std::map<std::string, InnerModuleInfo> moduleInfos = innerBundleInfo.GetInnerModuleInfos();
    for (const auto &info : moduleInfos) {
        if (info.second.distro.moduleType != Profile::MODULE_TYPE_SHARED) {
            return false;
        }
    }
    return true;
}

ErrCode BundleDataMgr::QueryCloneAbilityInfo(const ElementName &element, int32_t flags, int32_t userId,
    int32_t appIndex, AbilityInfo &abilityInfo) const
{
    std::string bundleName = element.GetBundleName();
    std::string abilityName = element.GetAbilityName();
    std::string moduleName = element.GetModuleName();
    LOG_D(BMS_TAG_QUERY,
        "QueryCloneAbilityInfo bundleName:%{public}s moduleName:%{public}s abilityName:%{public}s",
        bundleName.c_str(), moduleName.c_str(), abilityName.c_str());
    LOG_D(BMS_TAG_QUERY, "flags:%{public}d userId:%{public}d appIndex:%{public}d", flags, userId, appIndex);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;

    ErrCode ret = GetInnerBundleInfoWithFlagsV9(bundleName, flags, innerBundleInfo, requestUserId, appIndex);
    if (ret != ERR_OK) {
        LOG_D(BMS_TAG_QUERY, "QueryCloneAbilityInfo fail bundleName:%{public}s", bundleName.c_str());
        return ret;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by QueryCloneAbilityInfo is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    int32_t responseUserId = innerBundleInfo->GetResponseUserId(requestUserId);
    auto ability = innerBundleInfo->FindAbilityInfoV9(moduleName, abilityName);
    if (!ability) {
        LOG_W(BMS_TAG_QUERY, "not found bundleName:%{public}s moduleName:%{public}s abilityName:%{public}s",
            bundleName.c_str(), moduleName.c_str(), abilityName.c_str());
        return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
    }
    return QueryAbilityInfoWithFlagsV9(ability, flags, responseUserId, *innerBundleInfo, abilityInfo, appIndex);
}

ErrCode BundleDataMgr::ExplicitQueryCloneAbilityInfo(const ElementName &element, int32_t flags, int32_t userId,
    int32_t appIndex, AbilityInfo &abilityInfo) const
{
    std::string bundleName = element.GetBundleName();
    std::string abilityName = element.GetAbilityName();
    std::string moduleName = element.GetModuleName();
    LOG_D(BMS_TAG_QUERY,
        "ExplicitQueryCloneAbilityInfo bundleName:%{public}s moduleName:%{public}s abilityName:%{public}s",
        bundleName.c_str(), moduleName.c_str(), abilityName.c_str());
    LOG_D(BMS_TAG_QUERY, "flags:%{public}d userId:%{public}d appIndex:%{public}d", flags, userId, appIndex);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return false;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;

    bool ret = GetInnerBundleInfoWithFlags(bundleName, flags, innerBundleInfo, requestUserId, appIndex);
    if (!ret) {
        LOG_D(BMS_TAG_QUERY, "ExplicitQueryCloneAbilityInfo fail bundleName:%{public}s", bundleName.c_str());
        return false;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ExplicitQueryCloneAbilityInfo is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    int32_t responseUserId = innerBundleInfo->GetResponseUserId(requestUserId);
    auto ability = innerBundleInfo->FindAbilityInfo(moduleName, abilityName, responseUserId);
    if (!ability) {
        LOG_W(BMS_TAG_QUERY, "not found bundleName:%{public}s moduleName:%{public}s abilityName:%{public}s",
            bundleName.c_str(), moduleName.c_str(), abilityName.c_str());
        return false;
    }
    return QueryAbilityInfoWithFlags(ability, flags, responseUserId, *innerBundleInfo, abilityInfo, appIndex);
}

ErrCode BundleDataMgr::ExplicitQueryCloneAbilityInfoV9(const ElementName &element, int32_t flags, int32_t userId,
    int32_t appIndex, AbilityInfo &abilityInfo) const
{
    std::string bundleName = element.GetBundleName();
    std::string abilityName = element.GetAbilityName();
    std::string moduleName = element.GetModuleName();
    LOG_D(BMS_TAG_QUERY,
        "ExplicitQueryCloneAbilityInfoV9 bundleName:%{public}s moduleName:%{public}s abilityName:%{public}s",
        bundleName.c_str(), moduleName.c_str(), abilityName.c_str());
    LOG_D(BMS_TAG_QUERY, "flags:%{public}d userId:%{public}d appIndex:%{public}d", flags, userId, appIndex);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;

    ErrCode ret = GetInnerBundleInfoWithFlagsV9(bundleName, flags, innerBundleInfo, requestUserId, appIndex);
    if (ret != ERR_OK) {
        LOG_D(BMS_TAG_QUERY, "ExplicitQueryCloneAbilityInfoV9 fail bundleName:%{public}s", bundleName.c_str());
        return ret;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ExplicitQueryCloneAbilityInfoV9 is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    int32_t responseUserId = innerBundleInfo->GetResponseUserId(requestUserId);
    auto ability = innerBundleInfo->FindAbilityInfoV9(moduleName, abilityName);
    if (!ability) {
        LOG_W(BMS_TAG_QUERY, "not found bundleName:%{public}s moduleName:%{public}s abilityName:%{public}s",
            bundleName.c_str(), moduleName.c_str(), abilityName.c_str());
        return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
    }
    return QueryAbilityInfoWithFlagsV9(ability, flags, responseUserId, *innerBundleInfo, abilityInfo, appIndex);
}

ErrCode BundleDataMgr::GetCloneBundleInfo(
    const std::string &bundleName, int32_t flags, int32_t appIndex, BundleInfo &bundleInfo, int32_t userId) const
{
    std::vector<InnerBundleUserInfo> innerBundleUserInfos;
    if (userId == Constants::ANY_USERID) {
        if (!GetInnerBundleUserInfos(bundleName, innerBundleUserInfos)) {
            LOG_W(BMS_TAG_QUERY, "no userInfos for this bundle(%{public}s)", bundleName.c_str());
            return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
        }
        userId = innerBundleUserInfos.begin()->bundleUserInfo.userId;
    }

    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;

    auto ret = GetInnerBundleInfoWithBundleFlagsV9(bundleName, flags, innerBundleInfo, requestUserId, appIndex);
    if (ret != ERR_OK) {
        LOG_D(BMS_TAG_QUERY, "GetCloneBundleInfo failed, error code: %{public}d, bundleName:%{public}s",
            ret, bundleName.c_str());
        return ret;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetCloneBundleInfo is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }

    int32_t responseUserId = innerBundleInfo->GetResponseUserId(requestUserId);
    innerBundleInfo->GetBundleInfoV9(flags, bundleInfo, responseUserId, appIndex);

    ProcessCertificate(bundleInfo, bundleName, flags);
    ProcessBundleMenu(bundleInfo, flags, true);
    ProcessBundleRouterMap(bundleInfo, flags, userId);
    LOG_D(BMS_TAG_QUERY, "get bundleInfo(%{public}s) successfully in user(%{public}d)",
        bundleName.c_str(), userId);
    return ERR_OK;
}

void BundleDataMgr::QueryAllCloneExtensionInfos(const Want &want, int32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &infos) const
{
    LOG_D(BMS_TAG_QUERY, "begin to ImplicitQueryAllCloneExtensionInfosV9");
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        LOG_D(BMS_TAG_QUERY, "invalid user");
        return;
    }

    ElementName element = want.GetElement();
    std::string bundleName = element.GetBundleName();
    std::string extensionName = element.GetAbilityName();
    LOG_D(BMS_TAG_QUERY, "bundle name:%{public}s, extension name:%{public}s",
        bundleName.c_str(), extensionName.c_str());

    if (!bundleName.empty() && !extensionName.empty()) {
        std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexes(bundleName, userId);
        if (cloneAppIndexes.empty()) {
            LOG_D(BMS_TAG_QUERY, "explicit queryAllCloneExtensionInfo empty");
            return;
        }
        for (int32_t appIndex: cloneAppIndexes) {
            ExtensionAbilityInfo info;
            ErrCode ret = ExplicitQueryExtensionInfo(want, flags, requestUserId, info, appIndex);
            if (ret != ERR_OK) {
                LOG_D(BMS_TAG_QUERY, "explicit queryExtensionInfo error");
                continue;
            }
            infos.emplace_back(info);
        }
        return;
    } else if (!bundleName.empty()) {
        ImplicitQueryCurCloneExtensionAbilityInfos(want, flags, requestUserId, infos);
    } else {
        ImplicitQueryAllCloneExtensionAbilityInfos(want, flags, requestUserId, infos);
    }
}

void BundleDataMgr::QueryAllCloneExtensionInfosV9(const Want &want, int32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &infos) const
{
    LOG_D(BMS_TAG_QUERY, "begin to ImplicitQueryAllCloneExtensionInfosV9");
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        LOG_D(BMS_TAG_QUERY, "invalid user");
        return;
    }

    ElementName element = want.GetElement();
    std::string bundleName = element.GetBundleName();
    std::string extensionName = element.GetAbilityName();
    LOG_D(BMS_TAG_QUERY, "bundle name:%{public}s, extension name:%{public}s",
        bundleName.c_str(), extensionName.c_str());

    if (!bundleName.empty() && !extensionName.empty()) {
        std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexes(bundleName, userId);
        if (cloneAppIndexes.empty()) {
            LOG_D(BMS_TAG_QUERY, "explicit queryAllCloneExtensionInfo empty");
            return;
        }
        for (int32_t appIndex: cloneAppIndexes) {
            ExtensionAbilityInfo info;
            ErrCode ret = ExplicitQueryExtensionInfoV9(want, flags, requestUserId, info, appIndex);
            if (ret != ERR_OK) {
                LOG_D(BMS_TAG_QUERY, "explicit queryExtensionInfo error");
                continue;
            }
            infos.emplace_back(info);
        }
        return;
    } else if (!bundleName.empty()) {
        ImplicitQueryCurCloneExtensionAbilityInfosV9(want, flags, requestUserId, infos);
    } else {
        ImplicitQueryAllCloneExtensionAbilityInfosV9(want, flags, requestUserId, infos);
    }
}

bool BundleDataMgr::ImplicitQueryCurCloneExtensionAbilityInfos(const Want &want, int32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &abilityInfos) const
{
    LOG_D(BMS_TAG_QUERY, "begin ImplicitQueryCurCloneExtensionAbilityInfos");
    std::string bundleName = want.GetElement().GetBundleName();

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;
    bool ret = GetInnerBundleInfoWithFlags(bundleName, flags, innerBundleInfo, userId);
    if (!ret) {
        LOG_D(BMS_TAG_QUERY, "ImplicitQueryCurCloneExtensionAbilityInfos failed");
        return false;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ImplicitQueryCurCloneExtensionAbilityInfos is null.");
        return false;
    }
    std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexesNoLock(bundleName, userId);
    if (cloneAppIndexes.empty()) {
        LOG_D(BMS_TAG_QUERY, "explicit ImplicitQueryCurCloneExtensionAbilityInfos empty");
        return true;
    }
    int32_t responseUserId = innerBundleInfo->GetResponseUserId(userId);
    for (int32_t appIndex: cloneAppIndexes) {
        if (CheckInnerBundleInfoWithFlags(*innerBundleInfo, flags, responseUserId, appIndex) != ERR_OK) {
            LOG_D(BMS_TAG_QUERY,
                "failed, bundleName:%{public}s, responseUserId:%{public}d, appIndex:%{public}d",
                innerBundleInfo->GetBundleName().c_str(), responseUserId, appIndex);
            continue;
        }
        GetMatchExtensionInfos(want, flags, responseUserId, *innerBundleInfo, abilityInfos, appIndex);
    }
    LOG_D(BMS_TAG_QUERY, "end ImplicitQueryCurCloneExtensionAbilityInfos");
    return true;
}

ErrCode BundleDataMgr::ImplicitQueryCurCloneExtensionAbilityInfosV9(const Want &want, int32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &abilityInfos) const
{
    LOG_D(BMS_TAG_QUERY, "begin ImplicitQueryCurCloneExtensionAbilityInfosV9");
    std::string bundleName = want.GetElement().GetBundleName();

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;
    ErrCode ret = GetInnerBundleInfoWithFlagsV9(bundleName, flags, innerBundleInfo, userId);
    if (ret != ERR_OK) {
        LOG_D(BMS_TAG_QUERY, "ImplicitQueryCurCloneExtensionAbilityInfosV9 failed");
        return ret;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by ImplicitQueryCurCloneExtensionAbilityInfosV9 is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexesNoLock(bundleName, userId);
    if (cloneAppIndexes.empty()) {
        LOG_D(BMS_TAG_QUERY, "explicit ImplicitQueryCurCloneExtensionAbilityInfosV9 empty");
        return ERR_OK;
    }
    int32_t responseUserId = innerBundleInfo->GetResponseUserId(userId);
    for (int32_t appIndex: cloneAppIndexes) {
        if (CheckInnerBundleInfoWithFlagsV9(*innerBundleInfo, flags, responseUserId, appIndex) != ERR_OK) {
            LOG_D(BMS_TAG_QUERY,
                "failed, bundleName:%{public}s, responseUserId:%{public}d, appIndex:%{public}d",
                innerBundleInfo->GetBundleName().c_str(), responseUserId, appIndex);
            continue;
        }
        GetMatchExtensionInfosV9(want, flags, responseUserId, *innerBundleInfo, abilityInfos, appIndex);
    }
    LOG_D(BMS_TAG_QUERY, "end ImplicitQueryCurCloneExtensionAbilityInfosV9");
    return ERR_OK;
}

bool BundleDataMgr::ImplicitQueryAllCloneExtensionAbilityInfos(const Want &want, int32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &infos) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &innerBundleInfo = item.second;
        std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexesNoLock(innerBundleInfo.GetBundleName(), userId);
        if (cloneAppIndexes.empty()) {
            continue;
        }
        int32_t responseUserId = innerBundleInfo.GetResponseUserId(userId);
        for (int32_t appIndex: cloneAppIndexes) {
            if (CheckInnerBundleInfoWithFlags(innerBundleInfo, flags, responseUserId, appIndex) != ERR_OK) {
                LOG_D(BMS_TAG_QUERY,
                    "failed, bundleName:%{public}s, responseUserId:%{public}d, appIndex:%{public}d",
                    innerBundleInfo.GetBundleName().c_str(), responseUserId, appIndex);
                continue;
            }
            int32_t responseUserId = innerBundleInfo.GetResponseUserId(userId);
            GetMatchExtensionInfos(want, flags, responseUserId, innerBundleInfo, infos, appIndex);
        }
    }
    FilterExtensionAbilityInfosByModuleName(want.GetElement().GetModuleName(), infos);
    return true;
}

ErrCode BundleDataMgr::ImplicitQueryAllCloneExtensionAbilityInfosV9(const Want &want, int32_t flags, int32_t userId,
    std::vector<ExtensionAbilityInfo> &infos) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &innerBundleInfo = item.second;
        std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexesNoLock(innerBundleInfo.GetBundleName(), userId);
        if (cloneAppIndexes.empty()) {
            continue;
        }
        int32_t responseUserId = innerBundleInfo.GetResponseUserId(userId);
        for (int32_t appIndex: cloneAppIndexes) {
            if (CheckInnerBundleInfoWithFlagsV9(innerBundleInfo, flags, responseUserId, appIndex) != ERR_OK) {
                LOG_D(BMS_TAG_QUERY,
                    "failed, bundleName:%{public}s, responseUserId:%{public}d, appIndex:%{public}d",
                    innerBundleInfo.GetBundleName().c_str(), responseUserId, appIndex);
                continue;
            }
            int32_t responseUserId = innerBundleInfo.GetResponseUserId(userId);
            GetMatchExtensionInfosV9(want, flags, responseUserId, innerBundleInfo, infos, appIndex);
        }
    }
    FilterExtensionAbilityInfosByModuleName(want.GetElement().GetModuleName(), infos);
    return ERR_OK;
}

ErrCode BundleDataMgr::GetAppIdByBundleName(
    const std::string &bundleName, std::string &appId) const
{
    HITRACE_METER_NAME_EX(HITRACE_LEVEL_INFO, HITRACE_TAG_APP, __PRETTY_FUNCTION__, nullptr);
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }

    appId = item->second.GetBaseBundleInfo().appId;
    return ERR_OK;
}

ErrCode BundleDataMgr::GetAppIdAndAppIdentifierByBundleName(
    const std::string &bundleName, std::string &appId, std::string &appIdentifier) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }

    appId = item->second.GetAppId();
    appIdentifier = item->second.GetAppIdentifier();
    return ERR_OK;
}

std::string BundleDataMgr::AppIdAndAppIdentifierTransform(const std::string appIdOrAppIdentifier) const
{
    if (appIdOrAppIdentifier.empty()) {
        APP_LOGW("appIdOrAppIdentifier is empty");
        return Constants::EMPTY_STRING;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto it = std::find_if(bundleInfos_.cbegin(), bundleInfos_.cend(), [&appIdOrAppIdentifier](const auto &pair) {
        return (appIdOrAppIdentifier == pair.second.GetAppId() ||
            appIdOrAppIdentifier == pair.second.GetAppIdentifier());
    });
    if (it == bundleInfos_.cend()) {
        APP_LOGW("can't find appIdOrAppIdentifier in the installed bundle");
        return Constants::EMPTY_STRING;
    }
    if (appIdOrAppIdentifier == it->second.GetAppId()) {
        return it->second.GetAppIdentifier();
    }
    return it->second.GetAppId();
}

ErrCode BundleDataMgr::GetAllBundleNames(uint32_t flags, int32_t userId, std::vector<std::string> &bundleNames)
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("Input invalid userid, userId:%{public}d", userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }

    bool ofAnyUserFlag = (flags & static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_OF_ANY_USER)) != 0;
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto &[bundleName, innerInfo] : bundleInfos_) {
        if (innerInfo.GetApplicationBundleType() == BundleType::SHARED) {
            LOG_D(BMS_TAG_QUERY, "%{public}s is not app or atomic, ignore", bundleName.c_str());
            continue;
        }
        int32_t responseUserId = innerInfo.GetResponseUserId(requestUserId);
        auto flag = GET_BASIC_APPLICATION_INFO;
        if ((flags & static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_DISABLE))
            == static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_DISABLE)) {
            flag = GET_APPLICATION_INFO_WITH_DISABLE;
        }
        if ((CheckInnerBundleInfoWithFlags(innerInfo, flag, responseUserId) != ERR_OK) &&
            (!ofAnyUserFlag || innerInfo.GetInnerBundleUserInfos().empty())) {
            continue;
        }
        uint32_t launchFlag = static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_ONLY_WITH_LAUNCHER_ABILITY);
        if (((static_cast<uint32_t>(flags) & launchFlag) == launchFlag) && (innerInfo.IsHideDesktopIcon())) {
            LOG_D(BMS_TAG_QUERY, "bundleName %{public}s is hide desktopIcon", bundleName.c_str());
            continue;
        }
        uint32_t cloudFlag = static_cast<uint32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_WITH_CLOUD_KIT);
        if (((static_cast<uint32_t>(flags) & cloudFlag) == cloudFlag) &&
            !innerInfo.GetCloudFileSyncEnabled() &&
            !innerInfo.GetCloudStructuredDataSyncEnabled()) {
            APP_LOGD("bundle %{public}s does not enable cloud sync", bundleName.c_str());
            continue;
        }
        bundleNames.emplace_back(bundleName);
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::GetSignatureInfoByBundleName(const std::string &bundleName, SignatureInfo &signatureInfo) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        LOG_E(BMS_TAG_DEFAULT, "%{public}s not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    const InnerBundleInfo &innerBundleInfo = item->second;
    signatureInfo.appId = innerBundleInfo.GetBaseBundleInfo().appId;
    signatureInfo.fingerprint = innerBundleInfo.GetBaseApplicationInfo().fingerprint;
    signatureInfo.appIdentifier = innerBundleInfo.GetAppIdentifier();
    return ERR_OK;
}

ErrCode BundleDataMgr::GetSignatureInfoByUid(const int32_t uid, SignatureInfo &signatureInfo) const
{
    InnerBundleInfo innerBundleInfo;
    ErrCode errCode = GetInnerBundleInfoWithSandboxByUid(uid, innerBundleInfo);
    if (errCode != ERR_OK) {
        APP_LOGE("Get innerBundleInfo failed, uid:%{public}d", uid);
        return errCode;
    }
    signatureInfo.appId = innerBundleInfo.GetBaseBundleInfo().appId;
    signatureInfo.fingerprint = innerBundleInfo.GetBaseApplicationInfo().fingerprint;
    signatureInfo.appIdentifier = innerBundleInfo.GetAppIdentifier();
    AppProvisionInfo appProvisionInfo;
    if (!DelayedSingleton<AppProvisionInfoManager>::GetInstance()->
        GetAppProvisionInfo(innerBundleInfo.GetBundleName(), appProvisionInfo)) {
        APP_LOGW("bundleName:%{public}s GetAppProvisionInfo failed", innerBundleInfo.GetBundleName().c_str());
    } else {
        signatureInfo.certificate = appProvisionInfo.certificate;
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::UpdateAppEncryptedStatus(
    const std::string &bundleName, bool isExisted, int32_t appIndex, bool needSaveStorage)
{
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    auto res = item->second.UpdateAppEncryptedStatus(bundleName, isExisted, appIndex);
    if (res != ERR_OK) {
        LOG_E(BMS_TAG_DEFAULT, "UpdateAppEncryptedStatus failed %{public}s %{public}d", bundleName.c_str(), res);
        return res;
    }
    if (dataStorage_ == nullptr) {
        LOG_E(BMS_TAG_DEFAULT, "dataStorage_ nullptr");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    if (needSaveStorage && !dataStorage_->SaveStorageBundleInfo(item->second)) {
        APP_LOGE("SaveStorageBundleInfo failed for bundle %{public}s", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::AddDesktopShortcutInfo(const ShortcutInfo &shortcutInfo, int32_t userId)
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGW("Input invalid userid, userId:%{public}d", userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    bool isEnabled = false;
    ErrCode ret = IsApplicationEnabled(shortcutInfo.bundleName, shortcutInfo.appIndex, isEnabled, userId);
    if (ret != ERR_OK) {
        APP_LOGD("IsApplicationEnabled ret:%{public}d, bundleName:%{public}s, appIndex:%{public}d, userId:%{public}d",
            ret, shortcutInfo.bundleName.c_str(), shortcutInfo.appIndex, userId);
        return ret;
    }
    if (!isEnabled) {
        APP_LOGD("BundleName: %{public}s is disabled, appIndex:%{public}d, userId:%{public}d",
            shortcutInfo.bundleName.c_str(), shortcutInfo.appIndex, userId);
        return ERR_BUNDLE_MANAGER_APPLICATION_DISABLED;
    }
    bool isIdIllegal = false;
    if (!shortcutStorage_->AddDesktopShortcutInfo(shortcutInfo, userId, isIdIllegal)) {
        if (isIdIllegal) {
            return ERR_SHORTCUT_MANAGER_SHORTCUT_ID_ILLEGAL;
        }
        return ERR_SHORTCUT_MANAGER_INTERNAL_ERROR;
    }
    APP_LOGI_NOFUNC("AddDesktopShortcutInfo -n %{public}s -i %{public}d, -u %{public}d",
        shortcutInfo.bundleName.c_str(), shortcutInfo.appIndex, userId);
    return ERR_OK;
}

ErrCode BundleDataMgr::DeleteDesktopShortcutInfo(const ShortcutInfo &shortcutInfo, int32_t userId)
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGW("Input invalid userid, userId:%{public}d", userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    if (!shortcutStorage_->DeleteDesktopShortcutInfo(shortcutInfo, userId)) {
        return ERR_SHORTCUT_MANAGER_INTERNAL_ERROR;
    }
    APP_LOGI_NOFUNC("DeleteDesktopShortcutInfo -n %{public}s -i %{public}d, -u %{public}d",
        shortcutInfo.bundleName.c_str(), shortcutInfo.appIndex, userId);
    return ERR_OK;
}

ErrCode BundleDataMgr::GetAllDesktopShortcutInfo(int32_t userId, std::vector<ShortcutInfo> &shortcutInfos)
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGW("Input invalid userid, userId:%{public}d", userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::vector<ShortcutInfo> datas;
    shortcutStorage_->GetAllDesktopShortcutInfo(userId, datas);
    for (const auto &data : datas) {
        bool isEnabled = false;
        ErrCode ret = IsApplicationEnabled(data.bundleName, data.appIndex, isEnabled, userId);
        if (ret != ERR_OK) {
            APP_LOGD(
                "IsApplicationEnabled ret:%{public}d, bundleName:%{public}s, appIndex:%{public}d, userId:%{public}d",
                ret, data.bundleName.c_str(), data.appIndex, userId);
            continue;
        }
        if (!isEnabled) {
            APP_LOGD("BundleName: %{public}s is disabled, appIndex:%{public}d, userId:%{public}d",
                data.bundleName.c_str(), data.appIndex, userId);
            continue;
        }
        shortcutInfos.emplace_back(data);
    }
    APP_LOGI_NOFUNC("GetAllDesktopShortcutInfo size:%{public}zu -u %{public}d", shortcutInfos.size(), userId);
    return ERR_OK;
}

ErrCode BundleDataMgr::DeleteDesktopShortcutInfo(const std::string &bundleName)
{
    APP_LOGD("DeleteDesktopShortcutInfo by uninstall, bundleName:%{public}s", bundleName.c_str());
    if (!shortcutStorage_->DeleteDesktopShortcutInfo(bundleName)) {
        return ERR_SHORTCUT_MANAGER_INTERNAL_ERROR;
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::DeleteDesktopShortcutInfo(const std::string &bundleName, int32_t userId, int32_t appIndex)
{
    APP_LOGD(
        "DeleteDesktopShortcutInfo by remove cloneApp, bundleName:%{public}s, userId:%{public}d, appIndex:%{public}d",
        bundleName.c_str(), userId, appIndex);
    if (!shortcutStorage_->DeleteDesktopShortcutInfo(bundleName, userId, appIndex)) {
        return ERR_SHORTCUT_MANAGER_INTERNAL_ERROR;
    }
    return ERR_OK;
}

void BundleDataMgr::GetBundleInfosForContinuation(std::vector<BundleInfo> &bundleInfos) const
{
    if (bundleInfos.empty()) {
        APP_LOGD("bundleInfos is empty");
        return;
    }
    bundleInfos.erase(std::remove_if(bundleInfos.begin(), bundleInfos.end(), [](BundleInfo bundleInfo) {
        for (const auto &hapModuleInfo : bundleInfo.hapModuleInfos) {
            for (const auto &abilityInfo : hapModuleInfo.abilityInfos) {
                if (abilityInfo.continuable) {
                    return false;
                }
            }
        }
        return true;
        }), bundleInfos.end());
}

ErrCode BundleDataMgr::GetContinueBundleNames(
    const std::string &continueBundleName, std::vector<std::string> &bundleNames, int32_t userId)
{
    auto requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("Input invalid userid, userId:%{public}d", userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    if (continueBundleName.empty()) {
        return ERR_BUNDLE_MANAGER_INVALID_PARAMETER;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto &[key, innerInfo] : bundleInfos_) {
        if (CheckInnerBundleInfoWithFlags(
            innerInfo, BundleFlag::GET_BUNDLE_WITH_ABILITIES, innerInfo.GetResponseUserId(requestUserId)) != ERR_OK) {
            continue;
        }
        for (const auto &[key, abilityInfo] : innerInfo.GetInnerAbilityInfos()) {
            if (abilityInfo.continueBundleNames.find(continueBundleName) != abilityInfo.continueBundleNames.end()) {
                bundleNames.emplace_back(abilityInfo.bundleName);
                break;
            }
        }
    }

    APP_LOGD("The number of found continue packs, size:[%{public}d]", static_cast<int32_t>(bundleNames.size()));
    return ERR_OK;
}

ErrCode BundleDataMgr::IsBundleInstalled(const std::string &bundleName, int32_t userId,
    int32_t appIndex, bool &isInstalled)
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("name %{public}s invalid userid :%{public}d", bundleName.c_str(), userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    if ((appIndex < 0) || (appIndex > ServiceConstants::CLONE_APP_INDEX_MAX)) {
        APP_LOGE("name %{public}s invalid appIndex :%{public}d", bundleName.c_str(), appIndex);
        return ERR_APPEXECFWK_CLONE_INSTALL_INVALID_APP_INDEX;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        isInstalled = false;
        return ERR_OK;
    }
    if (item->second.GetInstallMark().status == InstallExceptionStatus::INSTALL_START) {
        APP_LOGW("name %{public}s is installing", bundleName.c_str());
        isInstalled = false;
        return ERR_OK;
    }
    if ((item->second.GetApplicationBundleType() == BundleType::SHARED) ||
        ((item->second.GetApplicationBundleType() == BundleType::APP_SERVICE_FWK) &&
        item->second.GetInnerBundleUserInfos().empty())) {
        isInstalled = true;
        return ERR_OK;
    }
    int32_t responseUserId = item->second.GetResponseUserId(requestUserId);
    if (responseUserId == Constants::INVALID_USERID) {
        isInstalled = false;
        return ERR_OK;
    }
    if (appIndex == 0) {
        isInstalled = true;
        return ERR_OK;
    }
    const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
    if (item->second.GetInnerBundleUserInfo(responseUserId, innerBundleUserInfoPtr)) {
        if (!innerBundleUserInfoPtr) {
            LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by IsBundleInstalled is null");
            return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
        }
        if (innerBundleUserInfoPtr->cloneInfos.find(InnerBundleUserInfo::AppIndexToKey(appIndex)) !=
            innerBundleUserInfoPtr->cloneInfos.end()) {
            isInstalled = true;
            return ERR_OK;
        }
    }
    isInstalled = false;
    return ERR_OK;
}

void BundleDataMgr::UpdateIsPreInstallApp(const std::string &bundleName, bool isPreInstallApp)
{
    APP_LOGD("UpdateIsPreInstallApp %{public}s", bundleName.c_str());
    if (bundleName.empty()) {
        APP_LOGW("bundleName is empty");
        return;
    }

    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("can not find bundle %{public}s", bundleName.c_str());
        return;
    }

    if (infoItem->second.IsPreInstallApp() != isPreInstallApp) {
        infoItem->second.SetIsPreInstallApp(isPreInstallApp);
        SaveInnerBundleInfo(infoItem->second);
    }
}

ErrCode BundleDataMgr::GetBundleNameByAppId(const std::string &appId, std::string &bundleName)
{
    APP_LOGD("start GetBundleNameByAppId %{private}s", appId.c_str());
    if (appId.empty()) {
        APP_LOGW("appId is empty");
        return ERR_APPEXECFWK_INSTALL_PARAM_ERROR;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto &[key, innerInfo] : bundleInfos_) {
        if (innerInfo.GetAppId() == appId || innerInfo.GetAppIdentifier() == appId) {
            bundleName = key;
            return ERR_OK;
        }
    }
    APP_LOGI("get bundleName failed %{private}s", appId.c_str());
    return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
}

ErrCode BundleDataMgr::GetDirForAtomicService(const std::string &bundleName, std::string &dataDir) const
{
    APP_LOGD("start GetDirForAtomicService name: %{public}s", bundleName.c_str());
    AccountSA::OhosAccountInfo accountInfo;
    auto ret = AccountSA::OhosAccountKits::GetInstance().GetOhosAccountInfo(accountInfo);
    if (ret != ERR_OK) {
        APP_LOGE("GetOhosAccountInfo failed, errCode: %{public}d", ret);
        return ERR_BUNDLE_MANAGER_GET_ACCOUNT_INFO_FAILED;
    }
    dataDir = ATOMIC_SERVICE_DIR_PREFIX + accountInfo.uid_ + PLUS + bundleName;
    return ERR_OK;
}

ErrCode BundleDataMgr::GetDirForAtomicServiceByUserId(const std::string &bundleName, int32_t userId,
    AccountSA::OhosAccountInfo &accountInfo, std::string &dataDir) const
{
    APP_LOGD("start GetDirForAtomicServiceByUserId name: %{public}s userId: %{public}d", bundleName.c_str(), userId);
    if (accountInfo.uid_.empty()) {
        auto ret = AccountSA::OhosAccountKits::GetInstance().GetOsAccountDistributedInfo(userId, accountInfo);
        if (ret != ERR_OK) {
            APP_LOGE("GetOsAccountDistributedInfo failed, errCode: %{public}d", ret);
            return ERR_BUNDLE_MANAGER_GET_ACCOUNT_INFO_FAILED;
        }
    }
    dataDir = ATOMIC_SERVICE_DIR_PREFIX + accountInfo.uid_ + PLUS + bundleName;
    return ERR_OK;
}

std::string BundleDataMgr::GetDirForApp(const std::string &bundleName, const int32_t appIndex) const
{
    APP_LOGD("start GetDirForApp name: %{public}s appIndex: %{public}d", bundleName.c_str(), appIndex);
    if (appIndex == 0) {
        return bundleName;
    } else {
        return CLONE_APP_DIR_PREFIX + std::to_string(appIndex) + PLUS + bundleName;
    }
}

ErrCode BundleDataMgr::GetDirByBundleNameAndAppIndex(const std::string &bundleName, const int32_t appIndex,
    std::string &dataDir)
{
    APP_LOGD("start GetDir bundleName : %{public}s appIndex : %{public}d", bundleName.c_str(), appIndex);
    if (appIndex < 0 || appIndex > ServiceConstants::CLONE_APP_INDEX_MAX) {
        return ERR_BUNDLE_MANAGER_GET_DIR_INVALID_APP_INDEX;
    }
    if (!BundlePermissionMgr::IsNativeTokenType()) {
        int32_t callingUid = IPCSkeleton::GetCallingUid();
        int32_t userId = callingUid / Constants::BASE_USER_RANGE;
        bool isBundleInstalled = false;
        auto ret = IsBundleInstalled(bundleName, userId, appIndex, isBundleInstalled);
        if (ret != ERR_OK) {
            APP_LOGE("IsBundleInstalled failed, ret:%{public}d", ret);
            return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
        }
        if (!isBundleInstalled) {
            APP_LOGE("bundle %{public}s is not installed", bundleName.c_str());
            return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
        }
    }
    BundleType type = BundleType::APP;
    GetBundleType(bundleName, type);
    if (type == BundleType::ATOMIC_SERVICE) {
        return GetDirForAtomicService(bundleName, dataDir);
    }
    dataDir = GetDirForApp(bundleName, appIndex);
    return ERR_OK;
}

std::vector<int32_t> BundleDataMgr::GetCloneAppIndexesByInnerBundleInfo(const InnerBundleInfo &innerBundleInfo,
    int32_t userId) const
{
    std::vector<int32_t> cloneAppIndexes;
    const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
    if (!innerBundleInfo.GetInnerBundleUserInfo(userId, innerBundleUserInfoPtr)) {
        return cloneAppIndexes;
    }
    if (!innerBundleUserInfoPtr) {
        LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetCloneAppIndexesByInnerBundleInfo is null");
        return cloneAppIndexes;
    }
    const std::map<std::string, InnerBundleCloneInfo> &cloneInfos = innerBundleUserInfoPtr->cloneInfos;
    if (cloneInfos.empty()) {
        return cloneAppIndexes;
    }
    for (const auto &cloneInfo : cloneInfos) {
        LOG_D(BMS_TAG_QUERY, "get cloneAppIndexes by inner bundle info: %{public}d", cloneInfo.second.appIndex);
        cloneAppIndexes.emplace_back(cloneInfo.second.appIndex);
    }
    return cloneAppIndexes;
}

ErrCode BundleDataMgr::GetBundleDir(int32_t userId, BundleType type, AccountSA::OhosAccountInfo &accountInfo,
    BundleDir &bundleDir) const
{
    APP_LOGD("start GetBundleDir");
    if (type == BundleType::ATOMIC_SERVICE) {
        std::string dataDir;
        auto ret = GetDirForAtomicServiceByUserId(bundleDir.bundleName, userId, accountInfo, dataDir);
        if (ret != ERR_OK) {
            return ret;
        }
        bundleDir.dir = dataDir;
    } else {
        bundleDir.dir = GetDirForApp(bundleDir.bundleName, bundleDir.appIndex);
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::GetAllBundleDirs(int32_t userId, std::vector<BundleDir> &bundleDirs) const
{
    APP_LOGD("start GetAllBundleDirs");
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("invalid userid :%{public}d", userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    AccountSA::OhosAccountInfo accountInfo;
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &info = item.second;
        std::string bundleName = info.GetBundleName();
        int32_t responseUserId = info.GetResponseUserId(requestUserId);
        if (responseUserId == Constants::INVALID_USERID) {
            APP_LOGD("bundle %{public}s is not installed in user %{public}d or 0", bundleName.c_str(), userId);
            continue;
        }
        BundleType type = info.GetApplicationBundleType();
        if (type != BundleType::ATOMIC_SERVICE && type != BundleType::APP) {
            continue;
        }

        std::vector<int32_t> allAppIndexes = {0};
        if (type == BundleType::APP) {
            std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexesByInnerBundleInfo(info, responseUserId);
            allAppIndexes.insert(allAppIndexes.end(), cloneAppIndexes.begin(), cloneAppIndexes.end());
        }
        for (int32_t appIndex: allAppIndexes) {
            BundleDir bundleDir;
            bundleDir.bundleName = bundleName;
            bundleDir.appIndex = appIndex;
            auto ret = GetBundleDir(responseUserId, type, accountInfo, bundleDir);
            if (ret != ERR_OK) {
                return ret;
            }
            bundleDirs.emplace_back(bundleDir);
        }
    }
    return ERR_OK;
}

void BundleDataMgr::RestoreUidAndGidFromUninstallInfo()
{
    std::unique_lock<std::shared_mutex> lock(bundleIdMapMutex_);
    std::map<std::string, UninstallBundleInfo> uninstallBundleInfos;
    if (!GetAllUninstallBundleInfo(uninstallBundleInfos)) {
        return;
    }
    std::map<int32_t, std::string> uninstallBundleIdMap;
    for (const auto &info : uninstallBundleInfos) {
        if (info.second.userInfos.empty()) {
            continue;
        }
        int32_t userId = -1;
        if (!OHOS::StrToInt(info.second.userInfos.begin()->first, userId)) {
            APP_LOGW("strToInt fail");
            continue;
        }
        int32_t bundleId = info.second.userInfos.begin()->second.uid
            - userId * Constants::BASE_USER_RANGE;
        if (bundleId < Constants::BASE_APP_UID || bundleId >= MAX_APP_UID) {
            APP_LOGW("invalid bundleId");
            continue;
        }
        auto item = bundleIdMap_.find(bundleId);
        if (item == bundleIdMap_.end()) {
            uninstallBundleIdMap.emplace(bundleId, info.first);
            BundleUtil::MakeFsConfig(info.first, bundleId, ServiceConstants::HMDFS_CONFIG_PATH);
            BundleUtil::MakeFsConfig(info.first, bundleId, ServiceConstants::SHAREFS_CONFIG_PATH);
            BundleUtil::MakeFsConfig(info.first, ServiceConstants::HMDFS_CONFIG_PATH,
                info.second.appProvisionType, Constants::APP_PROVISION_TYPE_FILE_NAME);
        }
    }
    for (const auto &item : uninstallBundleIdMap) {
        bundleIdMap_.emplace(item.first, item.second);
    }
}

ErrCode BundleDataMgr::GetAssetAccessGroups(const std::string &bundleName,
    std::vector<std::string> &assetAccessGroups) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("%{public}s not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    assetAccessGroups = item->second.GetAssetAccessGroups();
    return ERR_OK;
}

ErrCode BundleDataMgr::GetDeveloperId(const std::string &bundleName, std::string &developerId) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("%{public}s not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    developerId = item->second.GetDeveloperId();
    return ERR_OK;
}

bool BundleDataMgr::IsObtainAbilityInfo(const Want &want, int32_t userId, AbilityInfo &abilityInfo)
{
    APP_LOGI("IsObtainAbilityInfo");
    std::string bundleName = want.GetElement().GetBundleName();
    std::string abilityName = want.GetElement().GetAbilityName();
    std::string moduleName = want.GetElement().GetModuleName();
    if (bundleName.empty()) {
        APP_LOGE("bundle name empty");
        return false;
    }
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        const auto infoItem = bundleInfos_.find(bundleName);
        if (infoItem == bundleInfos_.end()) {
            APP_LOGE("%{public}s not found", bundleName.c_str());
            return false;
        }
        if (abilityName.empty()) {
            return true;
        }
    }
    int32_t flags = static_cast<int32_t>(GET_ABILITY_INFO_DEFAULT);
    return ExplicitQueryAbilityInfo(want, flags, userId, abilityInfo);
}

ErrCode BundleDataMgr::GetAllPluginInfo(const std::string &hostBundleName, int32_t userId,
    std::vector<PluginBundleInfo> &pluginBundleInfos) const
{
    APP_LOGD("start GetAllPluginInfo -n : %{public}s, -u : %{public}d", hostBundleName.c_str(), userId);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("invalid userid :%{public}d", userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    return InnerGetAllPluginInfo(hostBundleName, requestUserId, pluginBundleInfos);
}

ErrCode BundleDataMgr::InnerGetAllPluginInfo(const std::string &hostBundleName, int32_t userId,
    std::vector<PluginBundleInfo> &pluginBundleInfos) const
{
    auto item = bundleInfos_.find(hostBundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("hostBundleName: %{public}s does not exist", hostBundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    const InnerBundleInfo &innerBundleInfo = item->second;
    int32_t responseUserId = innerBundleInfo.GetResponseUserId(userId);
    if (responseUserId == Constants::INVALID_USERID) {
        APP_LOGE("-n : %{public}s is not installed in user %{public}d or 0", hostBundleName.c_str(), userId);
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    std::unordered_map<std::string, PluginBundleInfo> pluginInfoMap = innerBundleInfo.GetAllPluginBundleInfo();
    const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
    if (innerBundleInfo.GetInnerBundleUserInfo(userId, innerBundleUserInfoPtr)) {
        if (!innerBundleUserInfoPtr) {
        LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by InnerGetAllPluginInfo is null");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
        }
        for (const auto &pluginName : innerBundleUserInfoPtr->installedPluginSet) {
            if (pluginInfoMap.find(pluginName) != pluginInfoMap.end()) {
                APP_LOGD("pluginName: %{public}s", pluginName.c_str());
                pluginBundleInfos.emplace_back(pluginInfoMap[pluginName]);
            }
        }
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::AddPluginInfo(const std::string &bundleName,
    const PluginBundleInfo &pluginBundleInfo, const int32_t userId)
{
    APP_LOGD("start AddPluginInfo");
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("%{public}s not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    InnerBundleInfo newInfo = item->second;
    if (!newInfo.AddPluginBundleInfo(pluginBundleInfo, userId)) {
        APP_LOGE("%{public}s add plugin info failed", bundleName.c_str());
        return ERR_APPEXECFWK_ADD_PLUGIN_INFO_ERROR;
    }
    if (!dataStorage_->SaveStorageBundleInfo(newInfo)) {
        APP_LOGE("save InnerBundleInfo:%{public}s failed", bundleName.c_str());
        return ERR_APPEXECFWK_ADD_PLUGIN_INFO_ERROR;
    }
    bundleInfos_.at(bundleName) = newInfo;
    return ERR_OK;
}

ErrCode BundleDataMgr::RemovePluginInfo(const std::string &bundleName,
    const std::string &pluginBundleName, const int32_t userId)
{
    APP_LOGD("start RemovePluginInfo");
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("%{public}s not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    InnerBundleInfo newInfo = item->second;
    if (!newInfo.RemovePluginBundleInfo(pluginBundleName, userId)) {
        APP_LOGE("%{public}s remove plugin info failed", bundleName.c_str());
        return ERR_APPEXECFWK_REMOVE_PLUGIN_INFO_ERROR;
    }
    if (!dataStorage_->SaveStorageBundleInfo(newInfo)) {
        APP_LOGE("save InnerBundleInfo:%{public}s failed", bundleName.c_str());
        return ERR_APPEXECFWK_REMOVE_PLUGIN_INFO_ERROR;
    }
    bundleInfos_.at(bundleName) = newInfo;
    return ERR_OK;
}

bool BundleDataMgr::GetPluginBundleInfo(const std::string &hostBundleName, const std::string &pluginBundleName,
    const int32_t userId, PluginBundleInfo &pluginBundleInfo)
{
    APP_LOGD("bundleName:%{public}s start GetPluginBundleInfo", hostBundleName.c_str());
    if (hostBundleName.empty() || pluginBundleName.empty()) {
        APP_LOGW("bundleName is empty");
        return false;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    return InnerGetPluginBundleInfo(hostBundleName, pluginBundleName, userId, pluginBundleInfo);
}

bool BundleDataMgr::InnerGetPluginBundleInfo(const std::string &hostBundleName, const std::string &pluginBundleName,
    const int32_t userId, PluginBundleInfo &pluginBundleInfo)
{
    auto infoItem = bundleInfos_.find(hostBundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW_NOFUNC("%{public}s GetPluginBundleInfo not found %{public}s", hostBundleName.c_str(),
            pluginBundleName.c_str());
        return false;
    }
    std::unordered_map<std::string, PluginBundleInfo> pluginBundleInfos;
    if (!infoItem->second.GetPluginBundleInfos(userId, pluginBundleInfos)) {
        APP_LOGE("bundleName:%{public}s can not find userId %{public}d", hostBundleName.c_str(), userId);
        return false;
    }
    auto it = pluginBundleInfos.find(pluginBundleName);
    if (it == pluginBundleInfos.end()) {
        APP_LOGE("bundleName:%{public}s can not find plugin info for %{public}s in user(%{public}d)",
            hostBundleName.c_str(), pluginBundleName.c_str(), userId);
        return false;
    }
    pluginBundleInfo = it->second;
    return true;
}

bool BundleDataMgr::FetchPluginBundleInfo(const std::string &hostBundleName, const std::string &pluginBundleName,
    PluginBundleInfo &pluginBundleInfo)
{
    APP_LOGD("bundleName:%{public}s start FetchPluginBundleInfo, plugin:%{public}s",
        hostBundleName.c_str(), pluginBundleName.c_str());
    if (hostBundleName.empty() || pluginBundleName.empty()) {
        APP_LOGW("bundleName is empty");
        return false;
    }

    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(hostBundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW_NOFUNC("%{public}s FetchPluginBundleInfo not found %{public}s", hostBundleName.c_str(),
            pluginBundleName.c_str());
        return false;
    }
    std::unordered_map<std::string, PluginBundleInfo> pluginInfoMap = infoItem->second.GetAllPluginBundleInfo();
    auto iter = pluginInfoMap.find(pluginBundleName);
    if (iter != pluginInfoMap.end()) {
        pluginBundleInfo = iter->second;
        return true;
    }
    APP_LOGE("bundleName:%{public}s can not find plugin info, plugin:%{public}s",
        hostBundleName.c_str(), pluginBundleName.c_str());
    return false;
}

ErrCode BundleDataMgr::UpdatePluginBundleInfo(const std::string &hostBundleName,
    const PluginBundleInfo &pluginBundleInfo)
{
    APP_LOGD("hostBundleName:%{public}s start UpdatePluginBundleInfo, plugin:%{public}s",
        hostBundleName.c_str(), pluginBundleInfo.pluginBundleName.c_str());
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(hostBundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("%{public}s not exist", hostBundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    InnerBundleInfo newInfo = item->second;
    if (!newInfo.UpdatePluginBundleInfo(pluginBundleInfo)) {
        APP_LOGE("%{public}s update plugin info failed", hostBundleName.c_str());
        return ERR_APPEXECFWK_ADD_PLUGIN_INFO_ERROR;
    }
    if (!dataStorage_->SaveStorageBundleInfo(newInfo)) {
        APP_LOGE("save InnerBundleInfo:%{public}s failed, plugin:%{public}s",
            hostBundleName.c_str(), pluginBundleInfo.pluginBundleName.c_str());
        return ERR_APPEXECFWK_ADD_PLUGIN_INFO_ERROR;
    }
    bundleInfos_.at(hostBundleName) = newInfo;
    return ERR_OK;
}

ErrCode BundleDataMgr::RemovePluginFromUserInfo(const std::string &hostBundleName, const std::string &pluginBundleName,
    const int32_t userId)
{
    APP_LOGD("hostBundleName:%{public}s start RemovePluginFromUserInfo, plugin:%{public}s",
        hostBundleName.c_str(), pluginBundleName.c_str());
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(hostBundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("%{public}s not exist", hostBundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    InnerBundleInfo newInfo = item->second;
    if (!newInfo.RemovePluginFromUserInfo(pluginBundleName, userId)) {
        APP_LOGE("%{public}s reomve plugin info failed", hostBundleName.c_str());
        return ERR_APPEXECFWK_REMOVE_PLUGIN_INFO_ERROR;
    }
    if (!dataStorage_->SaveStorageBundleInfo(newInfo)) {
        APP_LOGE("save InnerBundleInfo:%{public}s failed, plugin:%{public}s",
            hostBundleName.c_str(), pluginBundleName.c_str());
        return ERR_APPEXECFWK_REMOVE_PLUGIN_INFO_ERROR;
    }
    bundleInfos_.at(hostBundleName) = newInfo;
    return ERR_OK;
}

ErrCode BundleDataMgr::GetPluginAbilityInfo(const std::string &hostBundleName, const std::string &pluginBundleName,
    const std::string &pluginModuleName, const std::string &pluginAbilityName, const int32_t userId, AbilityInfo &abilityInfo)
{
    APP_LOGD("bundleName:%{public}s start GetPluginAbilityInfo, plugin:%{public}s, abilityName:%{public}s",
        hostBundleName.c_str(), pluginBundleName.c_str(), pluginAbilityName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(hostBundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("%{public}s not exist", hostBundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    std::unordered_map<std::string, PluginBundleInfo> pluginInfos;
    if (!item->second.GetPluginBundleInfos(userId, pluginInfos)) {
        APP_LOGE("bundleName:%{public}s GetPluginBundleInfos failed, plugin:%{public}s, user: %{public}d",
            hostBundleName.c_str(), pluginBundleName.c_str(), userId);
        return ERR_APPEXECFWK_GET_PLUGIN_INFO_ERROR;
    }
    auto it = pluginInfos.find(pluginBundleName);
    if (it == pluginInfos.end()) {
        APP_LOGE("bundleName: %{public}s can not find plugin: %{public}s",
            hostBundleName.c_str(), pluginBundleName.c_str());
        return ERR_APPEXECFWK_PLUGIN_NOT_FOUND;
    }
    if (!it->second.GetAbilityInfoByName(pluginAbilityName, pluginModuleName, abilityInfo)) {
        APP_LOGE("plugin: %{public}s can not find ability: %{public}s module: %{public}s",
            pluginBundleName.c_str(), pluginAbilityName.c_str(), pluginModuleName.c_str());
        return ERR_APPEXECFWK_PLUGIN_ABILITY_NOT_FOUND;
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::GetPluginHapModuleInfo(const std::string &hostBundleName, const std::string &pluginBundleName,
    const std::string &pluginModuleName, const int32_t userId, HapModuleInfo &hapModuleInfo)
{
    APP_LOGD("bundleName:%{public}s start GetPluginHapModuleInfo, plugin:%{public}s, moduleName:%{public}s",
        hostBundleName.c_str(), pluginBundleName.c_str(), pluginModuleName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("invalid userid :%{public}d", userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    auto item = bundleInfos_.find(hostBundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("%{public}s not exist", hostBundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    std::unordered_map<std::string, PluginBundleInfo> pluginInfos;
    if (!item->second.GetPluginBundleInfos(requestUserId, pluginInfos)) {
        APP_LOGE("bundleName:%{public}s GetPluginBundleInfos failed, plugin:%{public}s, user: %{public}d",
            hostBundleName.c_str(), pluginBundleName.c_str(), requestUserId);
        return ERR_APPEXECFWK_GET_PLUGIN_INFO_ERROR;
    }
    auto it = pluginInfos.find(pluginBundleName);
    if (it == pluginInfos.end()) {
        APP_LOGE("bundleName: %{public}s can not find plugin: %{public}s",
            hostBundleName.c_str(), pluginBundleName.c_str());
        return ERR_APPEXECFWK_PLUGIN_NOT_FOUND;
    }
    if (!it->second.GetHapModuleInfo(pluginModuleName, hapModuleInfo)) {
        APP_LOGE("plugin: %{public}s can not find module: %{public}s",
            pluginBundleName.c_str(), pluginModuleName.c_str());
        return ERR_APPEXECFWK_PLUGIN_MODULE_NOT_FOUND;
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::RegisterPluginEventCallback(const sptr<IBundleEventCallback> pluginEventCallback,
    const std::string callingBundleName)
{
    if (pluginEventCallback == nullptr) {
        APP_LOGW("pluginEventCallback is null");
        return ERR_APPEXECFWK_NULL_PTR;
    }
    std::lock_guard lock(pluginCallbackMutex_);
    if (pluginEventCallback->AsObject() != nullptr) {
        sptr<BundleEventCallbackDeathRecipient> deathRecipient =
            new (std::nothrow) BundleEventCallbackDeathRecipient(callingBundleName);
        if (deathRecipient == nullptr) {
            APP_LOGW("deathRecipient is null");
            return ERR_APPEXECFWK_NULL_PTR;
        }
        pluginEventCallback->AsObject()->AddDeathRecipient(deathRecipient);
    }

    auto &callbackList = pluginCallbackMap_[callingBundleName];
    if (callbackList.size() >= MAX_PLUGIN_CALLBACK_SIZE) {
        APP_LOGE("plugin callback list size exceeds limit for %{public}s", callingBundleName.c_str());
        return ERR_APPEXECFWK_PLUGIN_CALLBACK_LIST_FULL;
    }
    callbackList.emplace_back(pluginEventCallback);
    APP_LOGI("success");
    return ERR_OK;
}

ErrCode BundleDataMgr::UnregisterPluginEventCallback(const sptr<IBundleEventCallback> pluginEventCallback,
    const std::string &callingBundleName)
{
    if (pluginEventCallback == nullptr) {
        APP_LOGW("pluginEventCallback is null");
        return ERR_APPEXECFWK_NULL_PTR;
    }
    std::lock_guard lock(pluginCallbackMutex_);
    auto iter = pluginCallbackMap_.find(callingBundleName);
    if (iter != pluginCallbackMap_.end()) {
        auto &callbackList = iter->second;
        callbackList.erase(std::remove_if(callbackList.begin(), callbackList.end(),
        [&pluginEventCallback](const sptr<IBundleEventCallback> &callback) {
            return callback->AsObject() == pluginEventCallback->AsObject();
        }), callbackList.end());
        if (callbackList.empty()) {
            pluginCallbackMap_.erase(callingBundleName);
        }
    }
    APP_LOGI("success");
    return ERR_OK;
}

void BundleDataMgr::NotifyPluginEventCallback(const EventFwk::CommonEventData &eventData,
    const std::string &bundleName, bool isHsp)
{
    APP_LOGI("begin");
    std::vector<sptr<IBundleEventCallback>> callbackList;
    {
        std::lock_guard lock(pluginCallbackMutex_);
        if (!isHsp) {
            auto iter = pluginCallbackMap_.find(std::string(Constants::FOUNDATION_PROCESS_NAME));
            if (iter != pluginCallbackMap_.end()) {
                for (const auto &callback : iter->second) {
                    callbackList.push_back(callback);
                }
            }
        }
        auto bundleIter = pluginCallbackMap_.find(bundleName);
        if (bundleIter != pluginCallbackMap_.end()) {
            for (const auto &callback : bundleIter->second) {
                callbackList.push_back(callback);
            }
        }
    }
    for (const auto &callback : callbackList) {
        if (callback == nullptr) {
            continue;
        }
        callback->OnReceiveEvent(eventData);
    }
    APP_LOGI("end");
}

ErrCode BundleDataMgr::GetAllDynamicIconInfo(const int32_t userId, std::vector<DynamicIconInfo> &dynamicIconInfos)
{
    APP_LOGI("start userId %{public}d", userId);
    if (userId != Constants::UNSPECIFIED_USERID) {
        if (!HasUserId(userId)) {
            APP_LOGE("userId %{public}d not exist", userId);
            return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
        }
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto &item : bundleInfos_) {
        item.second.GetAllDynamicIconInfo(userId, dynamicIconInfos);
    }
    return ERR_OK;
}

void BundleDataMgr::ProcessDynamicIconForOta()
{
    std::map<std::string, std::string> bundleNames;
    {
        // process all old curDynamicIconModule when first ota
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        for (const auto &item : bundleInfos_) {
            if (!item.second.GetCurDynamicIconModule().empty()) {
                bundleNames[item.first] = item.second.GetCurDynamicIconModule();
            }
        }
    }
    if (bundleNames.empty()) {
        return;
    }
    for (const auto &item : bundleNames) {
        UpateCurDynamicIconModule(item.first, item.second);
    }
}

ErrCode BundleDataMgr::GetDynamicIconInfo(const std::string &bundleName,
    std::vector<DynamicIconInfo> &dynamicIconInfos)
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGW("bundleName: %{public}s not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    item->second.GetAllDynamicIconInfo(Constants::UNSPECIFIED_USERID, dynamicIconInfos);
    return ERR_OK;
}

std::string BundleDataMgr::GetCurDynamicIconModule(
    const std::string &bundleName, const int32_t userId, const int32_t appIndex)
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        return Constants::EMPTY_STRING;
    }
    return item->second.GetCurDynamicIconModule(userId, appIndex);
}

ErrCode BundleDataMgr::GetTargetShortcutInfo(const std::string &bundleName, const std::string &shortcutId,
    const std::vector<ShortcutInfo> &shortcutInfos, ShortcutInfo &targetShortcutInfo) const
{
    bool isEnabled = true;
    bool isShortcutIdExist = false;
    auto ret = shortcutEnabledStorage_->GetShortcutEnabledStatus(bundleName, shortcutId, isEnabled);
    if (ret != ERR_OK) {
        APP_LOGW("Get isEnabled status failed, shortcut id %{public}s", shortcutId.c_str());
    }
    for (const auto& shortcut : shortcutInfos) {
        if (shortcut.id == shortcutId) {
            if (shortcut.sourceType == Constants::ShortcutSourceType::STATIC_SHORTCUT && !isEnabled) {
                APP_LOGE("shortcut id %{public}s is disabled", shortcutId.c_str());
                return ERR_SHORTCUT_MANAGER_SHORTCUT_ID_ILLEGAL;
            }
            isShortcutIdExist = true;
            targetShortcutInfo = shortcut;
            break;
        }
    }
    if (!isShortcutIdExist) {
        APP_LOGE("shortcut id %{public}s not exist", shortcutId.c_str());
        return ERR_SHORTCUT_MANAGER_SHORTCUT_ID_ILLEGAL;
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::SetShortcutVisibleForSelf(const std::string &shortcutId, bool visible)
{
    APP_LOGD("SetShortcutVisibleForSelf begin");
    int32_t uid = IPCSkeleton::GetCallingUid();
    int32_t appIndex = 0;
    std::string bundleName;
    auto ret = GetBundleNameAndIndex(uid, bundleName, appIndex);
    if (ret != ERR_OK) {
        APP_LOGE("get bundleName failed uid %{public}d", uid);
        return ERR_BUNDLE_MANAGER_INVALID_UID;
    }
    int32_t userId = GetUserIdByCallingUid();
    std::vector<ShortcutInfo> shortcutInfos;
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        auto iter = bundleInfos_.find(bundleName);
        if (iter != bundleInfos_.end()) {
            GetShortcutInfosByInnerBundleInfo(iter->second, shortcutInfos);
        } else {
            APP_LOGE("%{public}s not exist", bundleName.c_str());
            return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
        }
    }
    shortcutVisibleStorage_->GetStorageShortcutInfos(bundleName, appIndex, userId, shortcutInfos);
    ShortcutInfo targetShortcutInfo;
    ret = GetTargetShortcutInfo(bundleName, shortcutId, shortcutInfos, targetShortcutInfo);
    if (ret != ERR_OK) {
        return ret;
    }
    if (targetShortcutInfo.visible == visible) {
        return ERR_OK;
    }
    targetShortcutInfo.visible = visible;
    if (!shortcutVisibleStorage_->SaveStorageShortcutVisibleInfo(
        bundleName, shortcutId, appIndex, userId, targetShortcutInfo)) {
        APP_LOGE("SaveStorageShortcutVisibleInfo failed");
        return ERR_APPEXECFWK_DB_INSERT_ERROR;
    }
    std::shared_ptr<BundleCommonEventMgr> commonEventMgr = std::make_shared<BundleCommonEventMgr>();
    commonEventMgr->NotifyShortcutVisibleChanged(bundleName, shortcutId, userId, appIndex, visible);
    return ERR_OK;
}

ErrCode BundleDataMgr::DeleteShortcutVisibleInfo(const std::string &bundleName, int32_t userId, int32_t appIndex)
{
    APP_LOGD(
        "DeleteShortcutVisibleInfo by remove cloneApp, bundleName:%{public}s, userId:%{public}d, appIndex:%{public}d",
        bundleName.c_str(), userId, appIndex);
    if (!shortcutVisibleStorage_->DeleteShortcutVisibleInfo(bundleName, userId, appIndex)) {
        return ERR_APPEXECFWK_DB_DELETE_ERROR;
    }
    return ERR_OK;
}

bool BundleDataMgr::GreatOrEqualTargetAPIVersion(const int32_t platformVersion, const int32_t minorVersion, const int32_t patchVersion)
{
    if (platformVersion > ServiceConstants::API_VERSION_MAX || platformVersion < 1) {
        APP_LOGE("GreatOrEqualTargetAPIVersion Error, platformVersion is invalid: %{public}d", platformVersion);
        return false;
    }
    if (minorVersion > ServiceConstants::API_VERSION_MAX || minorVersion < 0) {
        APP_LOGE("GreatOrEqualTargetAPIVersion Error, minorVersion is invalid: %{public}d", minorVersion);
        return false;
    }
    if (patchVersion > ServiceConstants::API_VERSION_MAX || patchVersion < 0) {
        APP_LOGE("GreatOrEqualTargetAPIVersion Error, patchVersion is invalid: %{public}d", patchVersion);
        return false;
    }

    BundleInfo bundleInfo;
    auto ret = GetBundleInfoForSelf(static_cast<int32_t>(GetBundleInfoFlag::GET_BUNDLE_INFO_DEFAULT), bundleInfo);
    if (ret != ERR_OK) {
        APP_LOGE("GreatOrEqualTargetAPIVersion, GetBundleInfoForSelf fail");
        return false;
    }

    APP_LOGD("BundleDataMgr::GreatOrEqualTargetAPIVersion, name: %{public}s, major: %{public}d, minor: %{public}d, patch: %{public}d",
        bundleInfo.name.c_str(), (bundleInfo.targetVersion % ServiceConstants::API_VERSION_MOD),
        bundleInfo.targetMinorApiVersion, bundleInfo.targetPatchApiVersion);
    if (static_cast<uint32_t>(platformVersion) != (bundleInfo.targetVersion % ServiceConstants::API_VERSION_MOD)) {
        return static_cast<uint32_t>(platformVersion) < (bundleInfo.targetVersion % ServiceConstants::API_VERSION_MOD);
    }

    if (minorVersion != bundleInfo.targetMinorApiVersion) {
        return minorVersion < bundleInfo.targetMinorApiVersion;
    }
    return patchVersion <= bundleInfo.targetPatchApiVersion;
}

std::string BundleDataMgr::GenerateUuid() const
{
    auto currentTime = std::chrono::system_clock::now();
    auto timestampNanoseconds =
        std::chrono::duration_cast<std::chrono::nanoseconds>(currentTime.time_since_epoch()).count();

    // convert nanosecond timestamps to string
    std::string timeStr = std::to_string(timestampNanoseconds);

    char deviceId[UUID_LENGTH_MAX] = { 0 };
    auto ret = GetDevUdid(deviceId, UUID_LENGTH_MAX);
    std::string deviceUdid;
    if (ret != 0) {
        APP_LOGW("GetDevUdid failed");
    } else {
        deviceUdid = std::string{ deviceId };
    }

    std::string message = timeStr + deviceUdid;
    std::string uuid = OHOS::Security::Verify::GenerateUuidByKey(message);
    return uuid;
}

std::string BundleDataMgr::GenerateUuidByKey(const std::string &key) const
{
    char deviceId[UUID_LENGTH_MAX] = { 0 };
    auto ret = GetDevUdid(deviceId, UUID_LENGTH_MAX);
    std::string deviceUdid;
    if (ret != 0) {
        APP_LOGW("GetDevUdid failed");
    } else {
        deviceUdid = std::string{ deviceId };
    }

    std::string message = key + deviceUdid;
    return OHOS::Security::Verify::GenerateUuidByKey(message);
}

void BundleDataMgr::FilterShortcutJson(nlohmann::json &jsonResult)
{
    if (!jsonResult.is_array()) {
        APP_LOGE("Invalid JSON format: expected array");
        return;
    }
    for (auto it = jsonResult.begin(); it != jsonResult.end();) {
        if (!it->contains(BUNDLE_NAME) || !it->at(BUNDLE_NAME).is_string()) {
            it = jsonResult.erase(it);
            continue;
        }
        if (!it->contains(APP_INDEX) || !it->at(APP_INDEX).is_number()) {
            it = jsonResult.erase(it);
            continue;
        }
        if (!it->contains(USER_ID) || !it->at(USER_ID).is_number()) {
            it = jsonResult.erase(it);
            continue;
        }
        int32_t userId = (*it)[USER_ID].get<int>();
        if (userId != Constants::START_USERID) {
            std::string bundleName = (*it)[BUNDLE_NAME].get<std::string>();
            int32_t appIndex = (*it)[APP_INDEX].get<int>();
            APP_LOGW("userId %{public}d is not supported for clone %{public}s %{public}d",
                userId, bundleName.c_str(), appIndex);
            it = jsonResult.erase(it);
            continue;
        }
        ++it;
    }
}

void BundleDataMgr::UpdateShortcutInfos(const std::string &bundleName)
{
    APP_LOGD("UpdateShortcutInfos begin");
    std::vector<ShortcutInfo> shortcutInfos;
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        auto iter = bundleInfos_.find(bundleName);
        if (iter != bundleInfos_.end()) {
            GetShortcutInfosByInnerBundleInfo(iter->second, shortcutInfos);
        } else {
            APP_LOGE("%{public}s not exist", bundleName.c_str());
            return;
        }
    }
    if (shortcutInfos.empty()) {
        shortcutEnabledStorage_->DeleteShortcutEnabledInfo(bundleName);
        return;
    } else {
        shortcutStorage_->UpdateDesktopShortcutInfo(bundleName, shortcutInfos);
        shortcutEnabledStorage_->UpdateShortcutEnabledInfo(bundleName, shortcutInfos);
    }
}

ErrCode BundleDataMgr::GetAllShortcutInfoForSelf(std::vector<ShortcutInfo> &shortcutInfos)
{
    APP_LOGD("GetAllShortcutInfoForSelf begin");
    int32_t uid = IPCSkeleton::GetCallingUid();
    int32_t appIndex = 0;
    std::string bundleName;
    auto ret = GetBundleNameAndIndex(uid, bundleName, appIndex);
    if (ret != ERR_OK) {
        APP_LOGE("get inner bundle info failed");
        return ERR_BUNDLE_MANAGER_INVALID_UID;
    }
    int32_t userId = GetUserIdByCallingUid();
    {
        std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
        auto iter = bundleInfos_.find(bundleName);
        if (iter != bundleInfos_.end()) {
            GetShortcutInfosByInnerBundleInfo(iter->second, shortcutInfos);
        } else {
            APP_LOGE("%{public}s not exist", bundleName.c_str());
            return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
        }
    }
    for (auto &info : shortcutInfos) {
        info.appIndex = appIndex;
    }

    shortcutVisibleStorage_->GetStorageShortcutInfos(bundleName, appIndex, userId, shortcutInfos);
    shortcutEnabledStorage_->FilterShortcutInfosEnabled(bundleName, shortcutInfos);
    return ERR_OK;
}

ErrCode BundleDataMgr::CheckShortcutIdsUnique(const InnerBundleInfo &innerBundleInfo, const int32_t userId,
    const std::vector<ShortcutInfo> &shortcutInfos, std::vector<std::string> &ids) const
{
    std::unordered_set<std::string> staticShortcutIds;
    {
        std::vector<ShortcutInfo> staticShortcutInfos;
        GetShortcutInfosByInnerBundleInfo(innerBundleInfo, staticShortcutInfos);
        staticShortcutIds.reserve(staticShortcutInfos.size());
        for (const auto &shortcutInfo : staticShortcutInfos) {
            staticShortcutIds.insert(shortcutInfo.id);
        }
    }
    for (const auto &shortcut : shortcutInfos) {
        if (staticShortcutIds.count(shortcut.id) > 0) {
            APP_LOGE("Shortcut ID:%{public}s conflicts with static shortcut ids", shortcut.id.c_str());
            return ERR_SHORTCUT_MANAGER_SHORTCUT_ID_ILLEGAL;
        }
    }

    std::unordered_set<std::string> storageShortcutIds;
    {
        std::vector<ShortcutInfo> storageShortcutInfos;
        shortcutVisibleStorage_->GetStorageShortcutInfos(
            shortcutInfos.begin()->bundleName, shortcutInfos.begin()->appIndex, userId, storageShortcutInfos, true);
        storageShortcutIds.reserve(storageShortcutInfos.size());
        for (const auto &shortcutInfo : storageShortcutInfos) {
            storageShortcutIds.insert(shortcutInfo.id);
        }
    }
    for (const auto &shortcut : shortcutInfos) {
        if (storageShortcutIds.count(shortcut.id) > 0) {
            APP_LOGE("Shortcut ID:%{public}s already exists in the database", shortcut.id.c_str());
            return ERR_SHORTCUT_MANAGER_SHORTCUT_ID_ILLEGAL;
        }
        ids.emplace_back(shortcut.id);
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::CheckModuleNameAndAbilityName(const std::vector<ShortcutInfo>& shortcutInfos,
    const InnerBundleInfo &innerBundleInfo) const
{
    std::unordered_set<std::string> processedModules;
    std::set<std::pair<std::string, std::string>> processedAbilities;
    for (const auto &info : shortcutInfos) {
        if (processedModules.insert(info.moduleName).second) {
            if (!innerBundleInfo.GetInnerModuleInfoByModuleName(info.moduleName)) {
                APP_LOGE("moduleName:%{public}s is not found", info.moduleName.c_str());
                return ERR_BUNDLE_MANAGER_MODULE_NOT_EXIST;
            }
        }

        if (!info.hostAbility.empty() && processedAbilities.emplace(info.moduleName, info.hostAbility).second) {
            if (!innerBundleInfo.isAbilityNameExist(info.moduleName, info.hostAbility)) {
                APP_LOGE("abilityName:%{public}s is not found", info.hostAbility.c_str());
                return ERR_BUNDLE_MANAGER_ABILITY_NOT_EXIST;
            }
        }
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::AddDynamicShortcutInfos(const std::vector<ShortcutInfo> &shortcutInfos, int32_t userId)
{
    APP_LOGD("AddDynamicShortcutInfos begin");
    if (!HasUserId(userId)) {
        APP_LOGW("input invalid userId, userId:%{public}d", userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    if (shortcutInfos.empty()) {
        APP_LOGE("shortcutInfos is empty");
        return ERR_BUNDLE_MANAGER_INVALID_PARAMETER;
    }
    std::string bundleName = shortcutInfos.begin()->bundleName;
    int32_t appIndex = shortcutInfos.begin()->appIndex;
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;
    ErrCode ret = GetInnerBundleInfoWithFlagsV9(bundleName,
    BundleFlag::GET_BUNDLE_DEFAULT, innerBundleInfo, userId, appIndex);
    if (ret != ERR_OK) {
        APP_LOGD("GetInnerBundleInfoWithFlagsV9 failed, bundleName:%{public}s, userId:%{public}d",
            bundleName.c_str(), userId);
        return ret;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by AddDynamicShortcutInfos is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    ret = CheckModuleNameAndAbilityName(shortcutInfos, *innerBundleInfo);
    if (ret != ERR_OK) {
        APP_LOGE("CheckModuleNameAndAbilityName failed");
        return ret;
    }
    std::vector<std::string> ids;
    ret = CheckShortcutIdsUnique(*innerBundleInfo, userId, shortcutInfos, ids);
    if (ret != ERR_OK) {
        APP_LOGE("CheckShortcutIdsUnique failed");
        return ret;
    }

    if (!shortcutVisibleStorage_->AddDynamicShortcutInfos(shortcutInfos, userId)) {
        APP_LOGE("rdb AddDynamicShortcutInfos failed");
        return ERR_APPEXECFWK_DB_BATCH_INSERT_ERROR;
    }
    std::shared_ptr<BundleCommonEventMgr> commonEventMgr = std::make_shared<BundleCommonEventMgr>();
    commonEventMgr->NotifyDynamicShortcutChanged(
        bundleName, ids, userId, appIndex, SHORTCUT_OPERATION_CREATE);
    EventReport::SendDynamicShortcutEvent(
        bundleName, userId, ids, SHORTCUT_OPERATION_CREATE, IPCSkeleton::GetCallingUid());
    return ERR_OK;
}

ErrCode BundleDataMgr::DeleteDynamicShortcutInfos(const std::string &bundleName, const int32_t appIndex, int32_t userId,
    const std::vector<std::string> &ids)
{
    APP_LOGD("DeleteDynamicShortcutInfos begin");
    if (!HasUserId(userId)) {
        APP_LOGW("input invalid userId, userId:%{public}d", userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    bool isEnabled = false;
    ErrCode ret = IsApplicationEnabled(bundleName, appIndex, isEnabled, userId);
    if (ret != ERR_OK) {
        APP_LOGE("IsApplicationEnabled failed");
        return ret;
    }
    if (!isEnabled) {
        APP_LOGE("application is disabled");
        return ERR_BUNDLE_MANAGER_APPLICATION_DISABLED;
    }

    if (!ids.empty()) {
        std::vector<ShortcutInfo> dynamicShortcutInfos;
        shortcutVisibleStorage_->GetStorageShortcutInfos(bundleName, appIndex, userId, dynamicShortcutInfos, true);
        std::unordered_set<std::string> dynamicShortcutIds;
        if (dynamicShortcutInfos.empty()) {
            APP_LOGE("no specified dynamic shortcutInfos in the database");
            return ERR_SHORTCUT_MANAGER_SHORTCUT_ID_ILLEGAL;
        }
        dynamicShortcutIds.reserve(dynamicShortcutInfos.size());
        for (const auto &shortcutInfo : dynamicShortcutInfos) {
            dynamicShortcutIds.insert(shortcutInfo.id);
        }
        for (const auto &id : ids) {
            if (dynamicShortcutIds.find(id) == dynamicShortcutIds.end()) {
                APP_LOGE("shortcut id %{public}s not exists in the database", id.c_str());
                return ERR_SHORTCUT_MANAGER_SHORTCUT_ID_ILLEGAL;
            }
        }
    }
    if (!shortcutVisibleStorage_->DeleteDynamicShortcutInfos(bundleName, appIndex, userId, ids)) {
        APP_LOGE("delete dynamic shortcutInfos failed");
        return ERR_APPEXECFWK_DB_DELETE_ERROR;
    }
    std::shared_ptr<BundleCommonEventMgr> commonEventMgr = std::make_shared<BundleCommonEventMgr>();
    commonEventMgr->NotifyDynamicShortcutChanged(bundleName, ids, userId, appIndex, SHORTCUT_OPERATION_DELETE);
    EventReport::SendDynamicShortcutEvent(
        bundleName, userId, ids, SHORTCUT_OPERATION_DELETE, IPCSkeleton::GetCallingUid());
    return ERR_OK;
}

ErrCode BundleDataMgr::GetAllCloneAppIndexesAndUidsByInnerBundleInfo(const int32_t userId,
    std::unordered_map<std::string, std::vector<std::pair<int32_t, int32_t>>> &cloneInfos) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &info = item.second;
        std::string bundleName = item.second.GetBundleName();
        if (bundleName.empty()) {
            continue;
        }
        std::vector<int32_t> allAppIndexes = {0};
        std::vector<int32_t> cloneAppIndexes = GetCloneAppIndexesByInnerBundleInfo(info, userId);
        allAppIndexes.insert(allAppIndexes.end(), cloneAppIndexes.begin(), cloneAppIndexes.end());
        for (int32_t appIndex: allAppIndexes) {
            int32_t uid = info.GetUid(userId, appIndex);
            cloneInfos[bundleName].push_back({appIndex, uid});
        }
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::SetShortcutsEnabled(const std::vector<ShortcutInfo> &shortcutInfos, bool isEnabled)
{
    APP_LOGD("SetShortcutsEnabled begin");
    std::string bundleName;
    std::string shortcutId;
    std::vector<ShortcutInfo> shortcutInfosInBundle;
    bool isShortcutIdExist = false;
    for (const auto& shortcutinfo : shortcutInfos) {
        bundleName = shortcutinfo.bundleName;
        shortcutId = shortcutinfo.id;
        shortcutInfosInBundle.clear();
        {
            std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
            auto iter = bundleInfos_.find(bundleName);
            if (iter != bundleInfos_.end()) {
                GetShortcutInfosByInnerBundleInfo(iter->second, shortcutInfosInBundle);
            } else {
                APP_LOGE("%{public}s not exist", bundleName.c_str());
                return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
            }
        }
        isShortcutIdExist = false;
        for (const auto& shortcutInBundle : shortcutInfosInBundle) {
            if (shortcutInBundle.id == shortcutId) {
                isShortcutIdExist = true;
                break;
            }
        }
        if (!isShortcutIdExist) {
            APP_LOGE("shortcut id %{public}s not exist", shortcutId.c_str());
            return ERR_SHORTCUT_MANAGER_SHORTCUT_ID_ILLEGAL;
        }
    }

    if (!(shortcutEnabledStorage_->SaveStorageShortcutEnabledInfos(shortcutInfos, isEnabled))) {
        APP_LOGE("SaveStorageShortcutEnabledInfos failed");
        return ERR_APPEXECFWK_DB_INSERT_ERROR;
    }
    std::shared_ptr<BundleCommonEventMgr> commonEventMgr = std::make_shared<BundleCommonEventMgr>();
    commonEventMgr->NotifyShortcutsEnabledChanged(shortcutInfos, isEnabled);
    return ERR_OK;
}

ErrCode BundleDataMgr::DeleteShortcutEnabledInfo(const std::string &bundleName)
{
    APP_LOGD("DeleteShortcutEnabledInfo by remove App, bundleName:%{public}s", bundleName.c_str());
    if (!shortcutEnabledStorage_->DeleteShortcutEnabledInfo(bundleName)) {
        return ERR_APPEXECFWK_DB_DELETE_ERROR;
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::GetPluginInfo(const std::string &hostBundleName, const std::string &pluginBundleName,
    const int32_t userId, PluginBundleInfo &pluginBundleInfo)
{
    APP_LOGD("start GetPluginInfo -n %{public}s -u %{public}d", hostBundleName.c_str(), userId);
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("invalid userid :%{public}d", userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(hostBundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("-n %{public}s does not exist", hostBundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    const InnerBundleInfo &innerBundleInfo = item->second;
    int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
    if (responseUserId == Constants::INVALID_USERID) {
        APP_LOGE("-n %{public}s is not installed in user %{public}d or 0", hostBundleName.c_str(), userId);
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    std::unordered_map<std::string, PluginBundleInfo> pluginInfoMap = innerBundleInfo.GetAllPluginBundleInfo();
    const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
    innerBundleInfo.GetInnerBundleUserInfo(responseUserId, innerBundleUserInfoPtr);
    if (!innerBundleUserInfoPtr) {
        LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetPluginInfo is null");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    if ((pluginInfoMap.find(pluginBundleName) == pluginInfoMap.end()) ||
        (innerBundleUserInfoPtr->installedPluginSet.find(pluginBundleName) ==
        innerBundleUserInfoPtr->installedPluginSet.end())) {
        APP_LOGE("-p %{public}s -u %{public}d not exist", pluginBundleName.c_str(), userId);
        return ERR_APPEXECFWK_PLUGIN_NOT_FOUND;
    }
    pluginBundleInfo = pluginInfoMap.at(pluginBundleName);
    return ERR_OK;
}

bool BundleDataMgr::SetBundleUserInfoRemovable(const std::string bundleName, int32_t userId, bool removable)
{
    APP_LOGD("SetBundleUserInfoRemovable: %{public}s", bundleName.c_str());
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto infoItem = bundleInfos_.find(bundleName);
    if (infoItem == bundleInfos_.end()) {
        APP_LOGW("%{public}s bundle info not exist", bundleName.c_str());
        return false;
    }
    auto& info = bundleInfos_.at(bundleName);
    InnerBundleUserInfo userInfo;
    if (!info.GetInnerBundleUserInfo(userId, userInfo)) {
        LOG_W(BMS_TAG_DEFAULT, "-n %{public}s is not installed at -u %{public}d", bundleName.c_str(), userId);
        return false;
    }
    if (userInfo.isRemovable == removable) {
        APP_LOGI("-n %{public}s -u %{public}d no need to change", bundleName.c_str(), userId);
        return true;
    }
    userInfo.isRemovable = removable;
    info.AddInnerBundleUserInfo(userInfo);
    info.SetBundleStatus(InnerBundleInfo::BundleStatus::ENABLED);
    if (!dataStorage_->SaveStorageBundleInfo(info)) {
        APP_LOGW("update storage failed bundle:%{public}s", bundleName.c_str());
        return false;
    }
    return true;
}

ErrCode BundleDataMgr::GetTestRunner(const std::string &bundleName, const std::string &moduleName,
    ModuleTestRunner &testRunner)
{
    APP_LOGD("start GetTestRunner -n %{public}s -m %{public}s", bundleName.c_str(), moduleName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("-n %{public}s does not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    auto moduleInfo = item->second.GetInnerModuleInfoByModuleName(moduleName);
    if (!moduleInfo) {
        APP_LOGE("-m %{public}s is not found", moduleName.c_str());
        return ERR_BUNDLE_MANAGER_MODULE_NOT_EXIST;
    }
    BundleParser bundleParser;
    return bundleParser.ParseTestRunner(moduleInfo->hapPath, testRunner);
}
ErrCode BundleDataMgr::ImplicitQueryAbilityInfosWithDefault(const Want &want, int32_t flags, int32_t userId,
    std::vector<AbilityInfo> &abilityInfos, AbilityInfo &defaultAbilityInfo, bool &findDefaultApp)
{
    (void)ImplicitQueryAbilityInfos(want, flags, userId, abilityInfos);
    ImplicitQueryCloneAbilityInfos(want, flags, userId, abilityInfos);
    if(abilityInfos.empty()) {
        return ERR_BUNDLE_MANAGER_ABILITY_INFO_NOT_FOUND;
    }
#ifdef BUNDLE_FRAMEWORK_DEFAULT_APP
    std::string identity = IPCSkeleton::ResetCallingIdentity();
    std::vector<AbilityInfo> defaultAbilityInfos;
    std::vector<AbilityInfo> backupAbilityInfos;
    std::vector<ExtensionAbilityInfo> extensionInfos;
    if (DefaultAppMgr::GetInstance().GetDefaultApplication(want, userId, defaultAbilityInfos, extensionInfos)) {
        if (!defaultAbilityInfos.empty()) {
            APP_LOGI("find default ability");
            findDefaultApp = true;
            defaultAbilityInfo = defaultAbilityInfos[0];
            IPCSkeleton::SetCallingIdentity(identity);
            return ERR_OK;
        }
    }
    if (DefaultAppMgr::GetInstance().GetDefaultApplication(want, userId, backupAbilityInfos, extensionInfos, true)) {
        if (!backupAbilityInfos.empty()) {
            APP_LOGI("find backup default ability");
            findDefaultApp = true;
            defaultAbilityInfo = backupAbilityInfos[0];
            IPCSkeleton::SetCallingIdentity(identity);
            return ERR_OK;
        }
    }
    IPCSkeleton::SetCallingIdentity(identity);
#endif
    return ERR_OK;
}

void BundleDataMgr::AddInstallingBundleName(const std::string &bundleName, const int32_t userId)
{
    std::unique_lock<std::shared_mutex> lock(installingBundleNamesMutex_);
    installingBundleNames_[bundleName].insert(userId);
}

void BundleDataMgr::DeleteInstallingBundleName(const std::string &bundleName, const int32_t userId)
{
    std::unique_lock<std::shared_mutex> lock(installingBundleNamesMutex_);
    auto item = installingBundleNames_.find(bundleName);
    if (item == installingBundleNames_.end()) {
        return;
    }
    item->second.erase(userId);
    if (item->second.empty()) {
        installingBundleNames_.erase(bundleName);
    }
}

void BundleDataMgr::GetBundleInstallStatus(const std::string &bundleName, const int32_t userId,
    BundleInstallStatus &bundleInstallStatus)
{
    {
        std::shared_lock<std::shared_mutex> lock(installingBundleNamesMutex_);
        auto item = installingBundleNames_.find(bundleName);
        if (item != installingBundleNames_.end()) {
            if ((item->second.find(userId) != item->second.end()) ||
                (item->second.find(Constants::DEFAULT_USERID) != item->second.end()) ||
                (item->second.find(Constants::U1) != item->second.end())) {
                APP_LOGI("-n %{public}s -u %{public}d is installing", bundleName.c_str(), userId);
                bundleInstallStatus = BundleInstallStatus::BUNDLE_INSTALLING;
                return;
            }
        }
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        bundleInstallStatus = BundleInstallStatus::BUNDLE_NOT_EXIST;
        return;
    }
    int32_t responseUserId = item->second.GetResponseUserId(userId);
    if (responseUserId == Constants::INVALID_USERID) {
        APP_LOGW("-n %{public}s not exist in -u %{public}d", bundleName.c_str(), userId);
        bundleInstallStatus = BundleInstallStatus::BUNDLE_NOT_EXIST;
        return;
    }
    APP_LOGD("-n %{public}s -u %{public}d exist, bundle installed", bundleName.c_str(), responseUserId);
    bundleInstallStatus = BundleInstallStatus::BUNDLE_INSTALLED;
}

ErrCode BundleDataMgr::GetPluginBundlePathForSelf(const std::string &pluginBundleName, std::string &codePath)
{
    int32_t uid = IPCSkeleton::GetCallingUid();
    int32_t appIndex = 0;
    std::string bundleName;
    ErrCode ret = GetBundleNameAndIndex(uid, bundleName, appIndex);
    if (ret != ERR_OK) {
        APP_LOGW("uid: %{public}d invalid!", uid);
        return ret;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    const InnerBundleInfo* innerBundleInfo = nullptr;
    if (GetInnerBundleInfoNoLock(bundleName, uid, appIndex, innerBundleInfo) != ERR_OK) {
        APP_LOGE("get innerBundleInfo by uid :%{public}d failed", uid);
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    if (!innerBundleInfo) {
        LOG_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetPluginBundlePathForSelf is null.");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    int32_t userId = GetUserIdByCallingUid();
    std::unordered_map<std::string, PluginBundleInfo> pluginBundleInfos;
    if (!innerBundleInfo->GetPluginBundleInfos(userId, pluginBundleInfos)) {
        APP_LOGE("pluginBundleName:%{public}s can not find pluginBundleInfo", pluginBundleName.c_str());
        return ERR_APPEXECFWK_GET_PLUGIN_INFO_ERROR;
    }
    auto it = pluginBundleInfos.find(pluginBundleName);
    if (it == pluginBundleInfos.end()) {
        APP_LOGE("can not find plugin info for %{public}s in user(%{public}d)", pluginBundleName.c_str(), userId);
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    codePath = it->second.codePath;
    return ERR_OK;
}

ErrCode BundleDataMgr::AtomicProcessWithBundleInfo(const std::string &bundleName,
    const std::function<ErrCode(InnerBundleInfo&)>& callback)
{
    APP_LOGD("start to query innerBundleInfo");
    std::unique_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    InnerBundleInfo innerBundleInfo = item->second;
    ErrCode res = callback(innerBundleInfo);
    if (res != ERR_OK) {
        return res;
    }
    if (!dataStorage_->SaveStorageBundleInfo(innerBundleInfo)) {
        APP_LOGE("update storage failed bundle:%{public}s", bundleName.c_str());
        return ERR_APPEXECFWK_UPDATE_BUNDLE_ERROR;
    }
    bundleInfos_.at(bundleName) = innerBundleInfo;
    return ERR_OK;
}

void BundleDataMgr::GetProfileDataList(ProfileType profileType, int32_t requestUserId,
    std::vector<BundleProfileData> &profileDataList) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &info = item.second;
        std::string bundleName = info.GetBundleName();
        if (info.GetApplicationBundleType() == BundleType::SHARED) {
            APP_LOGD("app %{public}s is cross-app shared bundle, ignore", bundleName.c_str());
            continue;
        }
        int32_t responseUserId = info.GetResponseUserId(requestUserId);
        if (responseUserId == Constants::INVALID_USERID) {
            APP_LOGD("bundle %{public}s is not installed in user %{public}d", bundleName.c_str(), requestUserId);
            continue;
        }
        bool isEnabled = false;
        if (info.GetApplicationEnabledV9(responseUserId, isEnabled) != ERR_OK) {
            APP_LOGD("get application enabled failed, bundle: %{public}s", bundleName.c_str());
            continue;
        }
        if (!isEnabled) {
            APP_LOGD("bundle: %{public}s is disabled", bundleName.c_str());
            continue;
        }
        auto moduleInfo = info.GetInnerModuleInfoForEntry();
        if (!moduleInfo) {
            APP_LOGE("bundle %{public}s has no entry", bundleName.c_str());
            continue;
        }
        std::string profilePath = GetProfilePath(profileType, *moduleInfo);
        if (profilePath.empty()) {
            APP_LOGD("bundle: %{public}s profile: %{public}d not config", bundleName.c_str(), profileType);
            continue;
        }
        BundleProfileData bundleProfileData;
        bundleProfileData.bundleName = bundleName;
        bundleProfileData.moduleName = moduleInfo->moduleName;
        bundleProfileData.hapPath = moduleInfo->hapPath;
        bundleProfileData.profilePath = std::move(profilePath);
        profileDataList.emplace_back(std::move(bundleProfileData));
    }
}

ErrCode BundleDataMgr::GetAllJsonProfile(ProfileType profileType, int32_t userId,
    std::vector<JsonProfileInfo> &profileInfos) const
{
    APP_LOGD("start GetAllJsonProfile");
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE("invalid userid :%{public}d", userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    if (std::find(PROFILE_TYPES.begin(), PROFILE_TYPES.end(), profileType) == PROFILE_TYPES.end()) {
        APP_LOGE("profileType: %{public}d is invalid", profileType);
        return ERR_BUNDLE_MANAGER_PROFILE_NOT_EXIST;
    }
    std::vector<BundleProfileData> profileDataList;
    GetProfileDataList(profileType, requestUserId, profileDataList);
    if (profileDataList.empty()) {
        APP_LOGD("no bundle config profile: %{public}d", profileType);
        return ERR_OK;
    }
    profileInfos.reserve(profileDataList.size());
    for (const auto &data : profileDataList) {
        std::string profile;
        if (GetJsonProfileByExtractor(data.hapPath, data.profilePath, profile) != ERR_OK) {
            APP_LOGW("get json string from %{public}s failed -n %{public}s", data.profilePath.c_str(),
                data.bundleName.c_str());
            continue;
        }
        JsonProfileInfo profileInfo;
        profileInfo.profileType = profileType;
        profileInfo.bundleName = data.bundleName;
        profileInfo.moduleName = data.moduleName;
        profileInfo.profile = std::move(profile);
        profileInfos.emplace_back(std::move(profileInfo));
    }
    return ERR_OK;
}

ErrCode BundleDataMgr::GetPluginExtensionInfo(
    const std::string &hostBundleName, const Want &want, const int32_t userId, ExtensionAbilityInfo &extensionInfo)
{
    std::string pluginBundleName = want.GetElement().GetBundleName();
    std::string pluginAbilityName = want.GetElement().GetAbilityName();
    std::string pluginModuleName = want.GetElement().GetModuleName();
    APP_LOGD("bundleName:%{public}s start GetPluginAbilityInfo, plugin:%{public}s, abilityName:%{public}s",
        hostBundleName.c_str(), pluginBundleName.c_str(), pluginAbilityName.c_str());
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(hostBundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("%{public}s not exist", hostBundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    PluginBundleInfo pluginInfo;
    if (!item->second.GetPluginBundleInfoByName(userId, pluginBundleName, pluginInfo)) {
        APP_LOGE("bundleName: %{public}s can not find plugin: %{public}s",
            hostBundleName.c_str(), pluginBundleName.c_str());
        return ERR_APPEXECFWK_PLUGIN_NOT_FOUND;
    }
    if (!pluginInfo.GetExtensionInfoByName(pluginAbilityName, pluginModuleName, extensionInfo)) {
        APP_LOGE("plugin: %{public}s can not find ability: %{public}s module: %{public}s",
            pluginBundleName.c_str(), pluginAbilityName.c_str(), pluginModuleName.c_str());
        return ERR_APPEXECFWK_PLUGIN_ABILITY_NOT_FOUND;
    }
    const InnerBundleUserInfo *innerBundleUserInfoPtr = nullptr;
    if (!item->second.GetInnerBundleUserInfo(userId, innerBundleUserInfoPtr)) {
        APP_LOGE("can not find bundleUserInfo in userId: %{public}d, bundleName:%{public}s",
            userId, hostBundleName.c_str());
        return ERR_APPEXECFWK_USER_NOT_EXIST;
    }
    if (!innerBundleUserInfoPtr) {
        LOG_NOFUNC_E(BMS_TAG_QUERY, "The InnerBundleInfo obtained by GetPluginExtensionInfo is null");
        return ERR_BUNDLE_MANAGER_INTERNAL_ERROR;
    }
    extensionInfo.applicationInfo.uid = innerBundleUserInfoPtr->uid;
    return ERR_OK;
}

bool BundleDataMgr::CheckDeveloperIdSameWithDataGroupIds(const std::unordered_set<std::string> &dataGroupIds,
    const std::string &targetDeveloperId, int32_t userId) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (const auto &item : bundleInfos_) {
        const auto &dataGroupInfos = item.second.GetDataGroupInfos();
        for (const auto &dataGroupId: dataGroupIds) {
            auto dataGroupInfosIter = dataGroupInfos.find(dataGroupId);
            if (dataGroupInfosIter == dataGroupInfos.end()) {
                continue;
            }
            auto dataInUserIter = std::find_if(std::begin(dataGroupInfosIter->second),
                std::end(dataGroupInfosIter->second),
                [userId](const DataGroupInfo &info) { return info.userId == userId; });
            if (dataInUserIter != std::end(dataGroupInfosIter->second)) {
                std::string developerId = item.second.GetDeveloperId();
                if (developerId != targetDeveloperId) {
                    APP_LOGE("dataGroupid: %{public}s in both %{public}s and %{public}s",
                        dataGroupId.c_str(), developerId.c_str(), targetDeveloperId.c_str());
                    return false;
                }
            }
        }
    }
    return true;
}

std::vector<CreateDirParam> BundleDataMgr::GetAllExtensionDirsToUpdateSelinuxApl()
{
    std::vector<CreateDirParam> allExtensionDirsToUpdateSelinuxApl;
    std::set<int32_t> userIds = GetAllUser();
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    for (auto &infoPair : bundleInfos_) {
        auto &info = infoPair.second;
        std::string bundleName = infoPair.first;
        std::vector<std::string> extensionDirs = info.GetAllExtensionDirs();
        if (extensionDirs.empty()) {
            continue;
        }
        for (const auto &userId : userIds) {
            int32_t uid = info.GetUid(userId);
            if (uid == Constants::INVALID_UID) {
                continue;
            }
            CreateDirParam createDirParam;
            createDirParam.bundleName = bundleName;
            createDirParam.userId = userId;
            createDirParam.uid = uid;
            createDirParam.gid = uid;
            createDirParam.apl = info.GetAppPrivilegeLevel();
            createDirParam.isPreInstallApp = info.IsPreInstallApp();
            createDirParam.debug = info.GetBaseApplicationInfo().appProvisionType ==
                Constants::APP_PROVISION_TYPE_DEBUG;
            createDirParam.extensionDirs.assign(extensionDirs.begin(), extensionDirs.end());
            allExtensionDirsToUpdateSelinuxApl.emplace_back(createDirParam);
        }
    }

    return allExtensionDirsToUpdateSelinuxApl;
}

ErrCode BundleDataMgr::GetCreateDirParamByBundleOption(
    const BundleOptionInfo &optionInfo, CreateDirParam &createDirParam) const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(optionInfo.bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE("%{public}s not exist", optionInfo.bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    InnerBundleUserInfo innerBundleUserInfo;
    if (!item->second.GetInnerBundleUserInfo(optionInfo.userId, innerBundleUserInfo)) {
        APP_LOGE("can not find bundleUserInfo in -u %{public}d -n %{public}s",
            optionInfo.userId, optionInfo.bundleName.c_str());
        return ERR_APPEXECFWK_USER_NOT_EXIST;
    }
    if (optionInfo.appIndex == 0) {
        createDirParam.uid = innerBundleUserInfo.uid;
    } else {
        auto cloneInfo = innerBundleUserInfo.cloneInfos.find(InnerBundleUserInfo::AppIndexToKey(optionInfo.appIndex));
        if (cloneInfo == innerBundleUserInfo.cloneInfos.end()) {
            APP_LOGE("can not find in -u %{public}d -i %{public}d -n%{public}s",
                optionInfo.userId, optionInfo.appIndex, optionInfo.bundleName.c_str());
            return ERR_BUNDLE_MANAGER_APPINDEX_NOT_EXIST;
        }
        createDirParam.uid = cloneInfo->second.uid;
    }
    createDirParam.bundleName = item->second.GetBundleName();
    createDirParam.apl = item->second.GetAppPrivilegeLevel();
    createDirParam.isPreInstallApp = item->second.IsPreInstallApp();
    createDirParam.debug = item->second.GetBaseApplicationInfo().appProvisionType ==
        Constants::APP_PROVISION_TYPE_DEBUG;
    createDirParam.userId = optionInfo.userId;
    createDirParam.appIndex = optionInfo.appIndex;
    createDirParam.isContainsEl5Dir = item->second.NeedCreateEl5Dir();
    return ERR_OK;
}

bool BundleDataMgr::ProcessIdleInfo() const
{
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    if (bundleInfos_.empty()) {
        APP_LOGW("bundleInfos_ data is empty");
        return false;
    }

    std::vector<BundleOptionInfo> bundleOptionInfos;
    for (const auto &item : bundleInfos_) {
        const InnerBundleInfo &info = item.second;
        std::string bundleName = info.GetBundleName();
        if (info.GetApplicationBundleType() != BundleType::APP) {
            APP_LOGD("%{public}s is not app, ignore", bundleName.c_str());
            continue;
        }
        if (info.GetAppPrivilegeLevel() != ServiceConstants::APL_NORMAL) {
            APP_LOGD("%{public}s is not normal app, ignore", bundleName.c_str());
            continue;
        }
        auto &userInfos = info.GetInnerBundleUserInfos();
        if (userInfos.empty()) {
            APP_LOGW("%{public}s userInfo is empty, ignore", bundleName.c_str());
            continue;
        }
        for (const auto &userInfo : userInfos) {
            BundleOptionInfo bundleOptionInfo;
            bundleOptionInfo.bundleName = bundleName;
            bundleOptionInfo.userId = userInfo.second.bundleUserInfo.userId;
            bundleOptionInfos.emplace_back(bundleOptionInfo);
            auto cloneInfos = userInfo.second.cloneInfos;
            if (cloneInfos.empty()) {
                continue;
            }
            for (const auto &cloneInfo : cloneInfos) {
                BundleOptionInfo bundleCloneInfo;
                bundleCloneInfo.bundleName = bundleName;
                bundleCloneInfo.userId = cloneInfo.second.userId;
                bundleCloneInfo.appIndex = cloneInfo.second.appIndex;
                bundleOptionInfos.emplace_back(bundleCloneInfo);
            }
        }
    }
    (void)ProcessUninstallBundle(bundleOptionInfos);
    if (DelayedSingleton<BmsUpdateSelinuxMgr>::GetInstance()->AddBundles(bundleOptionInfos) != ERR_OK) {
        return false;
    }
    return true;
}

bool BundleDataMgr::ProcessUninstallBundle(std::vector<BundleOptionInfo> &bundleOptionInfos) const
{
    std::map<std::string, UninstallBundleInfo> uninstallBundleInfos;
    if (!GetAllUninstallBundleInfo(uninstallBundleInfos)) {
        APP_LOGE("get all uninstall bundle info failed");
        return false;
    }
    for (const auto &item : uninstallBundleInfos) {
        const auto &userInfos = item.second.userInfos;
        if (userInfos.empty()) {
            APP_LOGW("%{public}s userInfo is empty, ignore", item.first.c_str());
            continue;
        }
        for (const auto &userInfo : userInfos) {
            BundleOptionInfo bundleOptionInfo;
            bundleOptionInfo.bundleName = item.first;
            std::string key = userInfo.first;
            auto pos = key.find(ServiceConstants::UNDER_LINE);
            if (pos == std::string::npos) {
                if (!OHOS::StrToInt(key, bundleOptionInfo.userId)) {
                    APP_LOGW("parse userId failed -n:%{public}s -u:%{public}s", item.first.c_str(), key.c_str());
                    continue;
                }
                bundleOptionInfos.emplace_back(bundleOptionInfo);
            } else {
                std::string userId = key.substr(0, pos);
                std::string appIndex = key.substr(pos + 1);
                if (!OHOS::StrToInt(userId, bundleOptionInfo.userId)) {
                    APP_LOGW("parse userId failed -n:%{public}s -u:%{public}s", item.first.c_str(), userId.c_str());
                    continue;
                }
                if (!OHOS::StrToInt(appIndex, bundleOptionInfo.appIndex)) {
                    APP_LOGW("parse appIndex failed -n:%{public}s -i:%{public}s", item.first.c_str(), appIndex.c_str());
                    continue;
                }
                bundleOptionInfos.emplace_back(bundleOptionInfo);
            }
        }
    }
    return true;
}

ErrCode BundleDataMgr::CheckBundleExist(const std::string &bundleName, int32_t userId, int32_t appIndex) const
{
    int32_t requestUserId = GetUserId(userId);
    if (requestUserId == Constants::INVALID_USERID) {
        APP_LOGE_NOFUNC("check bundle invalid -u %{public}d", userId);
        return ERR_BUNDLE_MANAGER_INVALID_USER_ID;
    }
    if (appIndex < 0 || appIndex > ServiceConstants::CLONE_APP_INDEX_MAX) {
        APP_LOGE_NOFUNC("check bundle invalid appIndex -n %{public}s -a %{public}d", bundleName.c_str(), appIndex);
        return ERR_APPEXECFWK_CLONE_INSTALL_INVALID_APP_INDEX;
    }
    std::shared_lock<std::shared_mutex> lock(bundleInfoMutex_);
    auto item = bundleInfos_.find(bundleName);
    if (item == bundleInfos_.end()) {
        APP_LOGE_NOFUNC("check bundle -n %{public}s not exist", bundleName.c_str());
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    const InnerBundleInfo &innerBundleInfo = item->second;
    int32_t responseUserId = innerBundleInfo.GetResponseUserId(requestUserId);
    if (responseUserId == Constants::INVALID_USERID) {
        APP_LOGE_NOFUNC("-n: %{public}s is not installed in user %{public}d", bundleName.c_str(), userId);
        return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
    }
    if (appIndex > 0) {
        InnerBundleUserInfo innerBundleUserInfo;
        if (!innerBundleInfo.GetInnerBundleUserInfo(responseUserId, innerBundleUserInfo)) {
            APP_LOGE_NOFUNC("check bundle GetInnerBundleUserInfo failed");
            return ERR_BUNDLE_MANAGER_BUNDLE_NOT_EXIST;
        }
        auto cloneInfo = innerBundleUserInfo.cloneInfos.find(std::to_string(appIndex));
        if (cloneInfo == innerBundleUserInfo.cloneInfos.end()) {
            APP_LOGE_NOFUNC("check bundle invalid appIndex %{public}d", appIndex);
            return ERR_BUNDLE_MANAGER_APPINDEX_NOT_EXIST;
        }
    }
    return ERR_OK;
}
}  // namespace AppExecFwk
}  // namespace OHOS
