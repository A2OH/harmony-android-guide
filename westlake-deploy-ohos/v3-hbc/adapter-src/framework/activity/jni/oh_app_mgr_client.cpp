/*
 * oh_app_mgr_client.cpp
 *
 * OpenHarmony AppMgr IPC client implementation.
 *
 * Connects to OH AppMgrService via SystemAbilityManager. On attachApplication,
 * creates an AppSchedulerAdapter (OH IAppScheduler stub) and registers it with
 * OH AppMgr. The AppSchedulerAdapter receives all lifecycle callbacks from OH
 * system services and bridges them to Android IApplicationThread via JNI.
 *
 * Reference:
 *   OH: ability_rt/interfaces/inner_api/app_manager/include/appmgr/app_mgr_interface.h
 *   OH: ability_rt/interfaces/inner_api/app_manager/include/appmgr/app_mgr_proxy.h
 */
#include "oh_app_mgr_client.h"
#include "app_scheduler_adapter.h"
#include <android/log.h>

#include "ipc_skeleton.h"
#include "iservice_registry.h"
#include "system_ability_manager_proxy.h"

#define LOG_TAG "OH_AppMgrClient"
// B.37 (2026-04-29 EOD+2): direct HiLogPrint bypass for child diagnostics.
extern "C" int HiLogPrint(int type, int level, unsigned int domain,
                          const char* tag, const char* fmt, ...)
    __attribute__((__format__(printf, 5, 6)));
#define LOGI(fmt, ...) HiLogPrint(3, 4, 0xD000F00u, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGE(fmt, ...) HiLogPrint(3, 6, 0xD000F00u, LOG_TAG, fmt, ##__VA_ARGS__)

// OH system ability ID for AppMgrService
static constexpr int32_t APP_MGR_SERVICE_ID = 501;

namespace oh_adapter {

OHAppMgrClient& OHAppMgrClient::getInstance() {
    static OHAppMgrClient instance;
    return instance;
}

bool OHAppMgrClient::connect() {
    LOGI("Connecting to OH AppMgrService...");

    auto samgr = OHOS::SystemAbilityManagerClient::GetInstance().GetSystemAbilityManager();
    if (samgr == nullptr) {
        LOGE("Failed to get SystemAbilityManager");
        return false;
    }

    auto remoteObject = samgr->GetSystemAbility(APP_MGR_SERVICE_ID);
    if (remoteObject == nullptr) {
        LOGE("Failed to get AppMgrService remote object (SA ID=%d)", APP_MGR_SERVICE_ID);
        return false;
    }

    proxy_ = OHOS::iface_cast<OHOS::AppExecFwk::IAppMgr>(remoteObject);
    if (proxy_ == nullptr) {
        LOGE("Failed to cast remote object to IAppMgr");
        return false;
    }

    connected_ = true;
    LOGI("Connected to OH AppMgrService successfully");
    return true;
}

void OHAppMgrClient::disconnect() {
    LOGI("Disconnecting from OH AppMgrService");
    appScheduler_ = nullptr;
    proxy_ = nullptr;
    connected_ = false;
}

bool OHAppMgrClient::attachApplication(JavaVM* jvm, jobject appThread,
                                        int pid, int uid, const std::string& bundleName) {
    if (!connected_ || proxy_ == nullptr) {
        LOGE("Not connected to AppMgrService");
        return false;
    }

    pid_ = pid;
    uid_ = uid;
    bundleName_ = bundleName;

    LOGI("AttachApplication: pid=%d, uid=%d, bundle=%s", pid, uid, bundleName.c_str());

    // Create AppSchedulerAdapter (implements OH IAppScheduler / AppSchedulerHost)
    // This stub receives all callbacks from OH AppMgr and bridges them to Android
    appScheduler_ = new AppSchedulerAdapter(jvm, appThread);

    // Register the scheduler with OH AppMgr
    // After this call, OH AppMgr will invoke callbacks on appScheduler_:
    //   - ScheduleLaunchApplication(appLaunchData, config)
    //   - ScheduleLaunchAbility(abilityInfo, token, want, recordId)
    //   - ScheduleForegroundApplication()
    //   - ScheduleBackgroundApplication()
    //   - etc.
    proxy_->AttachApplication(appScheduler_);

    LOGI("AttachApplication completed: AppSchedulerAdapter registered with OH AppMgr");
    return true;
}

bool OHAppMgrClient::attachApplication(int pid, int uid, const std::string& bundleName) {
    // Legacy path without JNI context. The AppSchedulerAdapter should have been
    // created separately if this path is used.
    if (!connected_ || proxy_ == nullptr) {
        LOGE("Not connected to AppMgrService");
        return false;
    }

    pid_ = pid;
    uid_ = uid;
    bundleName_ = bundleName;

    LOGI("AttachApplication (legacy): pid=%d, uid=%d, bundle=%s", pid, uid, bundleName.c_str());

    if (appScheduler_ != nullptr) {
        proxy_->AttachApplication(appScheduler_);
        LOGI("AttachApplication completed with existing AppSchedulerAdapter");
    } else {
        LOGE("No AppSchedulerAdapter available. Use the JNI-aware overload.");
        return false;
    }

    return true;
}

void OHAppMgrClient::notifyAppState(int state) {
    if (!connected_ || proxy_ == nullptr) return;

    LOGI("NotifyAppState: state=%d", state);

    switch (static_cast<AppState>(state)) {
        case AppState::STATE_FOREGROUND:
            proxy_->ApplicationForegrounded(recordId_);
            break;
        case AppState::STATE_BACKGROUND:
            proxy_->ApplicationBackgrounded(recordId_);
            break;
        case AppState::STATE_TERMINATED:
            proxy_->ApplicationTerminated(recordId_);
            break;
        default:
            LOGI("Unknown app state %d, ignored", state);
            break;
    }
}

}  // namespace oh_adapter
