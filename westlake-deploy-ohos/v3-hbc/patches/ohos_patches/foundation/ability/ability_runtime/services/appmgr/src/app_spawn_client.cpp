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
#include "app_spawn_client.h"

#include <unordered_set>

#include "ability_manager_errors.h"
#include "hitrace_meter.h"
#include "hilog_tag_wrapper.h"
#include "nlohmann/json.hpp"
#include "os_account_manager_wrapper.h"
#include "securec.h"
#include "singleton.h"
#include "time_util.h"

namespace OHOS {
namespace AppExecFwk {
using namespace OHOS::AbilityRuntime;
namespace {
constexpr const char* HSPLIST_BUNDLES = "bundles";
constexpr const char* HSPLIST_MODULES = "modules";
constexpr const char* HSPLIST_VERSIONS = "versions";
constexpr const char* DATAGROUPINFOLIST_DATAGROUPID = "dataGroupId";
constexpr const char* DATAGROUPINFOLIST_GID = "gid";
constexpr const char* DATAGROUPINFOLIST_UUID = "uuid";
constexpr const char* DATAGROUPINFOLIST_DIR = "dir";
constexpr const char* JSON_DATA_APP_DIR_EL2 = "/data/app/el2/";
constexpr const char* JSON_DATA_APP_DIR_EL3 = "/data/app/el3/";
constexpr const char* JSON_DATA_APP_DIR_EL4 = "/data/app/el4/";
constexpr const char* JSON_DATA_APP_DIR_EL5 = "/data/app/el5/";
constexpr const char* JSON_GROUP = "/group/";
constexpr const char* VERSION_PREFIX = "v";
constexpr const char* APPSPAWN_CLIENT_USER_NAME = "APP_MANAGER_SERVICE";
constexpr int32_t RIGHT_SHIFT_STEP = 1;
constexpr int32_t START_FLAG_TEST_NUM = 1;
constexpr const char* JITPERMISSIONSLIST_NAME = "name";
constexpr const char* JITPERMISSIONSLIST_NAME_VALUE = "Permissions";
constexpr const char* JITPERMISSIONSLIST_COUNT = "ohos.encaps.count";
constexpr const char* JITPERMISSIONSLIST_PERMISSIONS_NAME = "permissions";
constexpr const char* UNINSTALL_BUNDLE_NAME = "uninstallDebugHapMsg";
}
AppSpawnClient::AppSpawnClient(bool isNWebSpawn)
{
    TAG_LOGD(AAFwkTag::APPMGR, "call");
    if (isNWebSpawn) {
        serviceName_ = NWEBSPAWN_SERVER_NAME;
    }
    state_ = SpawnConnectionState::STATE_NOT_CONNECT;
}

AppSpawnClient::AppSpawnClient(const char* serviceName)
{
    TAG_LOGD(AAFwkTag::APPMGR, "call");
    std::string serviceName__ = serviceName;
    if (serviceName__ == APPSPAWN_SERVER_NAME) {
        serviceName_ = APPSPAWN_SERVER_NAME;
    } else if (serviceName__ == CJAPPSPAWN_SERVER_NAME) {
        serviceName_ = CJAPPSPAWN_SERVER_NAME;
    } else if (serviceName__ == NWEBSPAWN_SERVER_NAME) {
        serviceName_ = NWEBSPAWN_SERVER_NAME;
    } else if (serviceName__ == NATIVESPAWN_SERVER_NAME) {
        serviceName_ = NATIVESPAWN_SERVER_NAME;
    } else if (serviceName__ == HYBRIDSPAWN_SERVER_NAME) {
        serviceName_ = HYBRIDSPAWN_SERVER_NAME;
    } else if (serviceName__ == APPSPAWNX_SERVER_NAME) {  // adapter project
        serviceName_ = APPSPAWNX_SERVER_NAME;
    } else {
        TAG_LOGE(AAFwkTag::APPMGR, "unknown service name");
        serviceName_ = NWEBSPAWN_SERVER_NAME;
    }
    state_ = SpawnConnectionState::STATE_NOT_CONNECT;
}

AppSpawnClient::~AppSpawnClient()
{
    CloseConnection();
}

ErrCode AppSpawnClient::OpenConnection()
{
    HITRACE_METER_NAME(HITRACE_TAG_APP, __PRETTY_FUNCTION__);
    if (state_ == SpawnConnectionState::STATE_CONNECTED) {
        return 0;
    }
    TAG_LOGI(AAFwkTag::APPMGR, "call");
    int64_t startTime = AbilityRuntime::TimeUtil::SystemTimeMillisecond();
    AppSpawnClientHandle handle = nullptr;
    ErrCode ret = 0;
    ret = AppSpawnClientInit(serviceName_.c_str(), &handle);
    int64_t costTime = AbilityRuntime::TimeUtil::SystemTimeMillisecond() - startTime;
    if (costTime > MAX_COST_TIME) {
        TAG_LOGE(AAFwkTag::APPMGR, "appspawnclientInit cost %{public}" PRId64 "ms!", costTime);
    }

    if (FAILED(ret)) {
        TAG_LOGE(AAFwkTag::APPMGR, "appspawnclientInit failed");
        state_ = SpawnConnectionState::STATE_CONNECT_FAILED;
        return ret;
    }
    handle_ = handle;
    state_ = SpawnConnectionState::STATE_CONNECTED;

    return ret;
}

void AppSpawnClient::CloseConnection()
{
    TAG_LOGD(AAFwkTag::APPMGR, "call");
    if (state_ == SpawnConnectionState::STATE_CONNECTED) {
        AppSpawnClientDestroy(handle_);
    }
    state_ = SpawnConnectionState::STATE_NOT_CONNECT;
}

SpawnConnectionState AppSpawnClient::QueryConnectionState() const
{
    return state_;
}

AppSpawnClientHandle AppSpawnClient::GetAppSpawnClientHandle() const
{
    if (state_ == SpawnConnectionState::STATE_CONNECTED) {
        return handle_;
    }
    return nullptr;
}

static std::string DumpDataGroupInfoListToJson(const DataGroupInfoList &dataGroupInfoList, bool isScreenLockDataProtect)
{
    TAG_LOGD(AAFwkTag::APPMGR, "dataGroupInfoList size: %{public}zu", dataGroupInfoList.size());
    nlohmann::json dataGroupInfoListJson;
    for (auto& dataGroupInfo : dataGroupInfoList) {
        nlohmann::json dataGroupInfoJson;
        dataGroupInfoJson[DATAGROUPINFOLIST_DATAGROUPID] = dataGroupInfo.dataGroupId;
        dataGroupInfoJson[DATAGROUPINFOLIST_GID] = std::to_string(dataGroupInfo.gid);
        dataGroupInfoJson[DATAGROUPINFOLIST_UUID] = dataGroupInfo.uuid;
        std::string dir = std::to_string(dataGroupInfo.userId) + JSON_GROUP + dataGroupInfo.uuid;
        dataGroupInfoJson[DATAGROUPINFOLIST_DIR] = JSON_DATA_APP_DIR_EL2 + dir;
        dataGroupInfoListJson.emplace_back(dataGroupInfoJson);

        dataGroupInfoJson[DATAGROUPINFOLIST_DIR] = JSON_DATA_APP_DIR_EL3 + dir;
        dataGroupInfoListJson.emplace_back(dataGroupInfoJson);

        dataGroupInfoJson[DATAGROUPINFOLIST_DIR] = JSON_DATA_APP_DIR_EL4 + dir;
        dataGroupInfoListJson.emplace_back(dataGroupInfoJson);
        if (isScreenLockDataProtect) {
            dataGroupInfoJson[DATAGROUPINFOLIST_DIR] = JSON_DATA_APP_DIR_EL5 + dir;
            dataGroupInfoListJson.emplace_back(dataGroupInfoJson);
        }
    }
    TAG_LOGD(AAFwkTag::APPMGR, "dataGroupInfoListJson %{public}s", dataGroupInfoListJson.dump().c_str());
    return dataGroupInfoListJson.dump();
}

static std::string DumpHspListToJson(const HspList &hspList)
{
    nlohmann::json hspListJson;
    for (auto& hsp : hspList) {
        hspListJson[HSPLIST_BUNDLES].emplace_back(hsp.bundleName);
        hspListJson[HSPLIST_MODULES].emplace_back(hsp.moduleName);
        hspListJson[HSPLIST_VERSIONS].emplace_back(VERSION_PREFIX + std::to_string(hsp.versionCode));
    }
    return hspListJson.dump();
}

static std::string DumpAppEnvToJson(const std::map<std::string, std::string> &appEnv)
{
    nlohmann::json appEnvJson;
    for (const auto &[envName, envValue] : appEnv) {
        appEnvJson[envName] = envValue;
    }
    return appEnvJson.dump();
}

static std::string DumpJITPermissionListToJson(const JITPermissionsMap &jitPermissionsMap)
{
    nlohmann::json jitPermissionsListJson;
    jitPermissionsListJson[JITPERMISSIONSLIST_NAME] = JITPERMISSIONSLIST_NAME_VALUE;
    jitPermissionsListJson[JITPERMISSIONSLIST_COUNT] = jitPermissionsMap.size();
    for (const auto &[permissionName, permissionValue] : jitPermissionsMap) {
        nlohmann::json temp;
        if (permissionValue.empty()) {
            temp[permissionName] = true;
        } else {
            temp[permissionName] = permissionValue;
        }
        jitPermissionsListJson[JITPERMISSIONSLIST_PERMISSIONS_NAME].emplace_back(temp);
    }
    return jitPermissionsListJson.dump();
}

int32_t AppSpawnClient::SetDacInfo(const AppSpawnStartMsg &startMsg, AppSpawnReqMsgHandle reqHandle)
{
    int32_t ret = 0;
    AppDacInfo appDacInfo = {0};
    appDacInfo.uid = startMsg.uid;
    appDacInfo.gid = startMsg.gid;
    appDacInfo.gidCount = startMsg.gids.size() + startMsg.dataGroupInfoList.size();
    for (uint32_t i = 0; i < startMsg.gids.size(); i++) {
        appDacInfo.gidTable[i] = startMsg.gids[i];
    }
    for (uint32_t i = startMsg.gids.size(); i < appDacInfo.gidCount; i++) {
        appDacInfo.gidTable[i] = startMsg.dataGroupInfoList[i - startMsg.gids.size()].gid;
    }
    ret = strcpy_s(appDacInfo.userName, sizeof(appDacInfo.userName), APPSPAWN_CLIENT_USER_NAME);
    if (ret) {
        TAG_LOGE(AAFwkTag::APPMGR, "set dac userName fail");
        return ret;
    }
    return AppSpawnReqMsgSetAppDacInfo(reqHandle, &appDacInfo);
}

int32_t AppSpawnClient::SetMountPermission(const AppSpawnStartMsg &startMsg, AppSpawnReqMsgHandle reqHandle)
{
    int32_t ret = 0;
    std::set<std::string> mountPermissionList = startMsg.permissions;
    for (std::string permission : mountPermissionList) {
        ret = AppSpawnClientAddPermission(handle_, reqHandle, permission.c_str());
        if (ret != 0) {
            TAG_LOGE(AAFwkTag::APPMGR, "AppSpawnReqMsgAddPermission %{public}s failed", permission.c_str());
            return ret;
        }
    }
    return ret;
}

int32_t AppSpawnClient::SetStartFlags(const AppSpawnStartMsg &startMsg, AppSpawnReqMsgHandle reqHandle)
{
    int32_t ret = 0;
    uint64_t startFlagTmp = startMsg.flags;
    int flagIndex = 0;
    while (startFlagTmp > 0) {
        if (startFlagTmp & START_FLAG_TEST_NUM) {
            ret = AppSpawnReqMsgSetAppFlag(reqHandle, static_cast<AppFlagsIndex>(flagIndex));
            if (ret != 0) {
                TAG_LOGE(AAFwkTag::APPMGR, "SetFlagIdx %{public}d failed, ret: %{public}d", flagIndex, ret);
                return ret;
            }
        }
        startFlagTmp = startFlagTmp >> RIGHT_SHIFT_STEP;
        flagIndex++;
    }
    if (startMsg.atomicServiceFlag) {
        ret = AppSpawnReqMsgSetAppFlag(reqHandle, APP_FLAGS_ATOMIC_SERVICE);
        if (ret != 0) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            return ret;
        }
    }
    if (startMsg.strictMode) {
        ret = SetStrictMode(startMsg, reqHandle);
        if (ret != ERR_OK) {
            return ret;
        }
    }
    if (startMsg.isolatedExtension) {
        ret = AppSpawnReqMsgSetAppFlag(reqHandle, APP_FLAGS_EXTENSION_SANDBOX);
        if (ret != 0) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            return ret;
        }
    }
    if (startMsg.isCustomSandboxFlag) {
        ret = AppSpawnReqMsgSetAppFlag(reqHandle, APP_FLAGS_CUSTOM_SANDBOX);
        if (ret != 0) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            return ret;
        }
        TAG_LOGD(AAFwkTag::APPMGR, "Set APP_FLAGS_CUSTOM_SANDBOX flag success.");
    }
#ifdef SUPPORT_CHILD_PROCESS
    ret = SetChildProcessTypeStartFlag(reqHandle, startMsg.childProcessType);
    if (ret != ERR_OK) {
        TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
        return ret;
    }
#endif // SUPPORT_CHILD_PROCESS
    ret = SetIsolationModeFlag(startMsg, reqHandle);
    return ret;
}

int32_t AppSpawnClient::SetStrictMode(const AppSpawnStartMsg &startMsg, const AppSpawnReqMsgHandle &reqHandle)
{
    int32_t ret = ERR_OK;
    TAG_LOGD(AAFwkTag::APPMGR, "SetStrictMode");
    if (startMsg.isolatedSandboxFlagLegacy) {
        TAG_LOGD(AAFwkTag::APPMGR, "SetIsolatedSandBoxLegacy");
        ret = AppSpawnReqMsgSetAppFlag(reqHandle, APP_FLAGS_ISOLATED_SANDBOX);
        if (ret != 0) {
            TAG_LOGE(AAFwkTag::APPMGR, "SetIsolatedSandBoxLegacy fail, ret: %{public}d", ret);
            return ret;
        }
    }
    if (startMsg.isolatedNetworkFlag) {
        TAG_LOGD(AAFwkTag::APPMGR, "Set isolatedNetwork");
        ret = AppSpawnReqMsgSetAppFlag(reqHandle, APP_FLAGS_ISOLATED_NETWORK);
        if (ret != 0) {
            TAG_LOGE(AAFwkTag::APPMGR, "Set isolatedNetwork fail, ret: %{public}d", ret);
            return ret;
        }
    }
    if (startMsg.isolatedSELinuxFlag) {
        TAG_LOGD(AAFwkTag::APPMGR, "Set isolatedSELinux,extType:%{public}s", startMsg.extensionTypeName.c_str());
        ret = AppSpawnReqMsgSetAppFlag(reqHandle, APP_FLAGS_ISOLATED_SELINUX_LABEL);
        if (ret != 0) {
            TAG_LOGE(AAFwkTag::APPMGR, "Set isolatedSELinuxFlag fail, ret: %{public}d", ret);
            return ret;
        }
    }
    if (startMsg.isolatedNetworkFlag || startMsg.isolatedSELinuxFlag) {
        ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_EXTENSION_TYPE, startMsg.extensionTypeName.c_str());
        if (ret != 0) {
            TAG_LOGE(AAFwkTag::APPMGR, "Set extensionTypeName fail, ret: %{public}d", ret);
            return ret;
        }
    }
    return ERR_OK;
}

int32_t AppSpawnClient::AppspawnSetExtMsg(const AppSpawnStartMsg &startMsg, AppSpawnReqMsgHandle reqHandle)
{
    int32_t ret = 0;
    ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_RENDER_CMD, startMsg.renderParam.c_str());
    if (ret) {
        TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
        return ret;
    }

    if (!startMsg.hspList.empty()) {
        ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_HSP_LIST,
            DumpHspListToJson(startMsg.hspList).c_str());
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            return ret;
        }
    }

    if (!startMsg.dataGroupInfoList.empty()) {
        ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_DATA_GROUP,
            DumpDataGroupInfoListToJson(startMsg.dataGroupInfoList, startMsg.isScreenLockDataProtect).c_str());
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            return ret;
        }
    }

    if (!startMsg.overlayInfo.empty()) {
        ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_OVERLAY, startMsg.overlayInfo.c_str());
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            return ret;
        }
    }

    if (!startMsg.appEnv.empty()) {
        std::string appEnv = DumpAppEnvToJson(startMsg.appEnv);
        TAG_LOGD(AAFwkTag::APPMGR, "bundleName: %{public}s, appEnv: %{public}s",
            startMsg.bundleName.c_str(), appEnv.c_str());
        ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_APP_ENV, appEnv.c_str());
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            return ret;
        }
    }

    if (!startMsg.atomicAccount.empty()) {
        ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_ACCOUNT_ID, startMsg.atomicAccount.c_str());
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            return ret;
        }
    }

    std::string apiTargetVersionStr = std::to_string(startMsg.apiTargetVersion);
    ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_API_TARGET_VERSION, apiTargetVersionStr.c_str());
    if (ret) {
        TAG_LOGE(AAFwkTag::APPMGR, "Send apiTargetVersion fail, ret: %{public}d", ret);
        return ret;
    }

    return AppspawnSetExtMsgMore(startMsg, reqHandle);
}

int32_t AppSpawnClient::AppspawnSetExtMsgMore(const AppSpawnStartMsg &startMsg, AppSpawnReqMsgHandle reqHandle)
{
    int32_t ret = 0;

    if (!startMsg.provisionType.empty()) {
        ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_PROVISION_TYPE, startMsg.provisionType.c_str());
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            return ret;
        }
    }

    if (!startMsg.processType.empty()) {
        ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_PROCESS_TYPE, startMsg.processType.c_str());
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            return ret;
        }
    }

#ifdef SUPPORT_CHILD_PROCESS
    std::string maxChildProcessStr = std::to_string(startMsg.maxChildProcess);
    ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_MAX_CHILD_PROCCESS_MAX, maxChildProcessStr.c_str());
    if (ret) {
        TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
        return ret;
    }
    TAG_LOGD(AAFwkTag::APPMGR, "Send maxChildProcess %{public}s success", maxChildProcessStr.c_str());
#endif // SUPPORT_CHILD_PROCESS

    if (!startMsg.extensionSandboxPath.empty()) {
        ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_APP_EXTENSION,
            startMsg.extensionSandboxPath.c_str());
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            return ret;
        }
    }

    if (!startMsg.fds.empty()) {
        ret = SetExtMsgFds(reqHandle, startMsg.fds);
        if (ret != ERR_OK) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            return ret;
        }
    }

    if (!startMsg.jitPermissionsMap.empty()) {
        std::string jitPermissionsStr = DumpJITPermissionListToJson(startMsg.jitPermissionsMap);
        ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_JIT_PERMISSIONS, jitPermissionsStr.c_str());
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            return ret;
        }
        TAG_LOGD(AAFwkTag::APPMGR, "Send JIT Permission: %{public}s", jitPermissionsStr.c_str());
    }

    return AppspawnSetExtMsgSec(startMsg, reqHandle);
}

int32_t AppSpawnClient::SetUserIdInfo(const AppSpawnStartMsg &startMsg, AppSpawnReqMsgHandle &reqHandle)
{
    if (startMsg.uid < 0) {
        return ERR_INVALID_VALUE;
    }
    auto osAccountMgr = DelayedSingleton<OsAccountManagerWrapper>::GetInstance();
    if (osAccountMgr == nullptr) {
        TAG_LOGW(AAFwkTag::APPMGR, "osAccountMgr is nullptr");
        return ERR_NULL_OBJECT;
    }

    int32_t userId = -1;
    auto errCode = osAccountMgr->GetOsAccountLocalIdFromUid(startMsg.uid, userId);
    if (errCode != ERR_OK) {
        TAG_LOGW(AAFwkTag::APPMGR, "GetOsAccountLocalIdFromUid failed, uid=%{public}d, errcode=%{public}d",
            startMsg.uid, errCode);
        return errCode;
    }

    std::string userIdStr = std::to_string(userId);
    int32_t ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_USERID, userIdStr.c_str());
    if (ret != ERR_OK) {
        TAG_LOGW(AAFwkTag::APPMGR, "Set userId failed, uid=%{public}d, userId=%{public}d, ret=%{public}d",
            startMsg.uid, userId, ret);
        return ret;
    }

    TAG_LOGD(AAFwkTag::APPMGR, "Send userId=%{public}s to AppSpawn", userIdStr.c_str());
    return ERR_OK;
}

int32_t AppSpawnClient::AppspawnSetExtMsgSec(const AppSpawnStartMsg &startMsg, AppSpawnReqMsgHandle reqHandle)
{
    SetUserIdInfo(startMsg, reqHandle);

    int32_t ret = 0;
    if (startMsg.hostProcessUid != 0) {
        ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_PARENT_UID,
            std::to_string(startMsg.hostProcessUid).c_str());
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            return ret;
        }
    }

    if (!startMsg.appSignType.empty()) {
        ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_APP_SIGN_TYPE, startMsg.appSignType.c_str());
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            return ret;
        }
        TAG_LOGD(AAFwkTag::APPMGR, "Send appSignType: %{public}s", startMsg.appSignType.c_str());
    }
    return ret;
}

int32_t AppSpawnClient::AppspawnCreateDefaultMsg(const AppSpawnStartMsg &startMsg, AppSpawnReqMsgHandle reqHandle)
{
    TAG_LOGD(AAFwkTag::APPMGR, "call");
    int32_t ret = 0;
    do {
        ret = SetDacInfo(startMsg, reqHandle);
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            break;
        }
        ret = AppSpawnReqMsgSetBundleInfo(reqHandle, startMsg.bundleIndex, startMsg.bundleName.c_str());
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            break;
        }
        ret = AppSpawnReqMsgSetAppInternetPermissionInfo(reqHandle, startMsg.allowInternet,
            startMsg.setAllowInternet);
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            break;
        }
        if (startMsg.ownerId.size()) {
            ret = AppSpawnReqMsgSetAppOwnerId(reqHandle, startMsg.ownerId.c_str());
            if (ret) {
                TAG_LOGE(AAFwkTag::APPMGR, "SetOwnerId %{public}s failed, ret: %{public}d",
                    startMsg.ownerId.c_str(), ret);
                break;
            }
        }
        ret = AppSpawnReqMsgSetAppAccessToken(reqHandle, startMsg.accessTokenIdEx);
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "ret: %{public}d", ret);
            break;
        }
        ret = AppSpawnReqMsgSetAppDomainInfo(reqHandle, startMsg.hapFlags, startMsg.apl.c_str());
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR,
                "fail, hapFlags is %{public}d, apl is %{public}s, ret: %{public}d",
                startMsg.hapFlags, startMsg.apl.c_str(), ret);
            break;
        }
        ret = SetStartFlags(startMsg, reqHandle);
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            break;
        }
        ret = SetMountPermission(startMsg, reqHandle);
        if (ret) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            break;
        }
        ret = AppspawnSetExtMsg(startMsg, reqHandle);
        if (ret != ERR_OK) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
            break;
        }
        return ret;
    } while (0);

    TAG_LOGI(AAFwkTag::APPMGR, "AppSpawnReqMsgFree");
    AppSpawnReqMsgFree(reqHandle);

    return ret;
}

bool AppSpawnClient::VerifyMsg(const AppSpawnStartMsg &startMsg)
{
    TAG_LOGD(AAFwkTag::APPMGR, "VerifyMsg");
    if (startMsg.code == MSG_APP_SPAWN ||
        startMsg.code == MSG_SPAWN_NATIVE_PROCESS) {
        if (startMsg.uid < 0) {
            TAG_LOGE(AAFwkTag::APPMGR, "invalid uid! [%{public}d]", startMsg.uid);
            return false;
        }

        if (startMsg.gid < 0) {
            TAG_LOGE(AAFwkTag::APPMGR, "invalid gid! [%{public}d]", startMsg.gid);
            return false;
        }

        if (startMsg.gids.size() > APP_MAX_GIDS) {
            TAG_LOGE(AAFwkTag::APPMGR, "many app gids");
            return false;
        }

        for (uint32_t i = 0; i < startMsg.gids.size(); ++i) {
            if (startMsg.gids[i] < 0) {
                TAG_LOGE(AAFwkTag::APPMGR, "invalid gids array! [%{public}d]", startMsg.gids[i]);
                return false;
            }
        }
        if (startMsg.procName.empty() || startMsg.procName.size() >= MAX_PROC_NAME_LEN) {
            TAG_LOGE(AAFwkTag::APPMGR, "invalid procName");
            return false;
        }
    } else if (startMsg.code == MSG_GET_RENDER_TERMINATION_STATUS) {
        if (startMsg.pid < 0) {
            TAG_LOGE(AAFwkTag::APPMGR, "invalid pid");
            return false;
        }
    } else {
        TAG_LOGE(AAFwkTag::APPMGR, "invalid code");
        return false;
    }

    return true;
}

int32_t AppSpawnClient::PreStartNWebSpawnProcess()
{
    TAG_LOGI(AAFwkTag::APPMGR, "call");
    return OpenConnection();
}

int32_t AppSpawnClient::StartProcess(const AppSpawnStartMsg &startMsg, pid_t &pid)
{
    TAG_LOGD(AAFwkTag::APPMGR, "StartProcess");
    HITRACE_METER_NAME(HITRACE_TAG_APP, __PRETTY_FUNCTION__);
    if (!VerifyMsg(startMsg)) {
        return ERR_INVALID_VALUE;
    }

    int32_t ret = 0;
    AppSpawnReqMsgHandle reqHandle = nullptr;
    int64_t startTime = AbilityRuntime::TimeUtil::SystemTimeMillisecond();

    ret = OpenConnection();
    if (ret != 0) {
        TAG_LOGE(AAFwkTag::APPMGR, "OpenConnection fail");
        return ret;
    }

    ret = AppSpawnReqMsgCreate(static_cast<AppSpawnMsgType>(startMsg.code), startMsg.procName.c_str(), &reqHandle);
    if (ret != 0) {
        TAG_LOGE(AAFwkTag::APPMGR, "AppSpawnReqMsgCreate fail");
        return ret;
    }

    ret = AppspawnCreateDefaultMsg(startMsg, reqHandle);
    if (ret != 0) {
        TAG_LOGE(AAFwkTag::APPMGR, "AppspawnCreateDefaultMsg fail");
        return ret; // create msg failed
    }

    TAG_LOGD(AAFwkTag::APPMGR, "AppspawnSendMsg");
    AppSpawnResult result = {0};
    ret = AppSpawnClientSendMsg(handle_, reqHandle, &result);

    int64_t costTime = AbilityRuntime::TimeUtil::SystemTimeMillisecond() - startTime;
    if (costTime > MAX_COST_TIME) {
        TAG_LOGE(AAFwkTag::APPMGR, "StartProcess cost %{public}" PRId64 "ms!", costTime);
    }
    if (ret != 0) {
        TAG_LOGE(AAFwkTag::APPMGR, "appspawn send msg fail");
        return ret;
    }
    if (result.pid <= 0) {
        TAG_LOGE(AAFwkTag::APPMGR, "pid invalid, result is %{public}d", result.result);
        return AAFwk::ERR_PROCESS_START_INVALID_PID;
    } else {
        pid = result.pid;
    }
    TAG_LOGI(AAFwkTag::APPMGR, "pid%{public}d", pid);
    return result.result;
}

int32_t AppSpawnClient::SendAppSpawnUninstallDebugHapMsg(int32_t userId)
{
    HITRACE_METER_NAME(HITRACE_TAG_APP, __PRETTY_FUNCTION__);
    AppSpawnReqMsgHandle reqHandle = nullptr;
    int32_t ret = OpenConnection();
    if (ret != ERR_OK) {
        TAG_LOGE(AAFwkTag::APPMGR, "OpenConnection failed");
        return ret;
    }
    ret = AppSpawnReqMsgCreate(MSG_UNINSTALL_DEBUG_HAP, UNINSTALL_BUNDLE_NAME, &reqHandle);
    if (ret != ERR_OK) {
        TAG_LOGE(AAFwkTag::APPMGR, "AppSpawnReqMsgCreate failed");
        return ret;
    }
    auto msg = std::to_string(userId);
    ret = AppSpawnReqMsgAddStringInfo(reqHandle, MSG_EXT_NAME_USERID, msg.c_str());
    if (ret != ERR_OK) {
        AppSpawnReqMsgFree(reqHandle);
        TAG_LOGE(AAFwkTag::APPMGR, "AppSpawnReqMsgAddStringInfo failed");
        return ret;
    }
    AppSpawnResult result = { 0 };
    ret = AppSpawnClientSendMsg(handle_, reqHandle, &result);
    if (ret != ERR_OK) {
        TAG_LOGE(AAFwkTag::APPMGR, "AppSpawnClientSendMsg failed");
        return ret;
    }
    return ret;
}

int32_t AppSpawnClient::GetRenderProcessTerminationStatus(const AppSpawnStartMsg &startMsg, int &status)
{
    TAG_LOGD(AAFwkTag::APPMGR, "call");
    int32_t ret = 0;
    AppSpawnReqMsgHandle reqHandle = nullptr;

    // check parameters
    if (!VerifyMsg(startMsg)) {
        return ERR_INVALID_VALUE;
    }

    ret = OpenConnection();
    if (ret != 0) {
        return ret;
    }

    ret = AppSpawnTerminateMsgCreate(startMsg.pid, &reqHandle);
    if (ret != 0) {
        TAG_LOGE(AAFwkTag::APPMGR, "AppSpawnTerminateMsgCreate failed");
        return ret;
    }

    TAG_LOGD(AAFwkTag::APPMGR, "AppspawnSendMsg");
    AppSpawnResult result = {0};
    ret = AppSpawnClientSendMsg(handle_, reqHandle, &result);
    status = result.result;
    if (ret != 0) {
        TAG_LOGE(AAFwkTag::APPMGR, "appspawn send msg fail");
        return ret;
    }
    TAG_LOGI(AAFwkTag::APPMGR, "status = [%{public}d]", status);

    return ret;
}

#ifdef SUPPORT_CHILD_PROCESS
int32_t AppSpawnClient::SetChildProcessTypeStartFlag(const AppSpawnReqMsgHandle &reqHandle,
    int32_t childProcessType)
{
    TAG_LOGD(AAFwkTag::APPMGR, "SetChildProcessTypeStartFlag, type:%{public}d", childProcessType);
    if (childProcessType != CHILD_PROCESS_TYPE_NOT_CHILD) {
        return AppSpawnReqMsgSetAppFlag(reqHandle, APP_FLAGS_CHILDPROCESS);
    }
    return ERR_OK;
}
#endif // SUPPORT_CHILD_PROCESS

int32_t AppSpawnClient::SetExtMsgFds(const AppSpawnReqMsgHandle &reqHandle,
    const std::map<std::string, int32_t> &fds)
{
    TAG_LOGI(AAFwkTag::APPMGR, "size:%{public}zu", fds.size());
    int32_t ret = ERR_OK;
    for (const auto &item : fds) {
        ret = AppSpawnReqMsgAddFd(reqHandle, item.first.c_str(), item.second);
        if (ret != ERR_OK) {
            TAG_LOGE(AAFwkTag::APPMGR, "fail, key:%{public}s, fd:%{public}d, ret:%{public}d",
                item.first.c_str(), item.second, ret);
            return ret;
        }
    }
    return ERR_OK;
}

int32_t AppSpawnClient::SetIsolationModeFlag(const AppSpawnStartMsg &startMsg, const AppSpawnReqMsgHandle &reqHandle)
{
    TAG_LOGD(AAFwkTag::APPMGR, "isolationMode:%{public}d", startMsg.isolationMode);
    if (!startMsg.isolationMode) {
        return ERR_OK;
    }
    auto ret = AppSpawnReqMsgSetAppFlag(reqHandle, APP_FLAGS_ISOLATED_SANDBOX_TYPE);
    if (ret != 0) {
        TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
        return ret;
    }
    ret = AppSpawnReqMsgSetAppFlag(reqHandle, APP_FLAGS_ISOLATED_NETWORK);
    if (ret != 0) {
        TAG_LOGE(AAFwkTag::APPMGR, "fail, ret: %{public}d", ret);
        return ret;
    }
    return ERR_OK;
}
}  // namespace AppExecFwk
}  // namespace OHOS
