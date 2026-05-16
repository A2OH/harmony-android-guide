/*
 * oh_callback_handler.cpp
 *
 * OH callback handler implementation.
 * Creates and manages OH IPC Stub adapters that receive callbacks from
 * OH system services and bridge them to the Android application via JNI.
 *
 * Adapter lifecycle:
 *   1. registerCallbacks() creates AppSchedulerAdapter, AbilitySchedulerAdapter,
 *      AbilityConnectionBridge, and WindowManagerAgentAdapter.
 *   2. AppSchedulerAdapter is passed to OH AppMgr via AttachApplication.
 *   3. AbilitySchedulerAdapter is registered per-ability with OH AbilityManager.
 *   4. WindowManagerAgentAdapter is registered with OH WindowManager for
 *      system-wide window state notifications.
 *   5. unregisterCallbacks() releases all adapter references.
 */
#include "oh_callback_handler.h"
#include "adapter_bridge.h"
#include "app_scheduler_adapter.h"
#include "ability_scheduler_adapter.h"
#include "ability_connection_adapter.h"
#include "window_manager_agent_adapter.h"
#include <android/log.h>

#define LOG_TAG "OH_CallbackHandler"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

namespace oh_adapter {

OHCallbackHandler& OHCallbackHandler::getInstance() {
    static OHCallbackHandler instance;
    return instance;
}

bool OHCallbackHandler::registerCallbacks(JavaVM* jvm, jobject appThread) {
    LOGI("Registering OH callback stubs with JNI context...");

    jvm_ = jvm;
    appThread_ = appThread;

    // Create AppSchedulerAdapter
    // This adapter implements OH IAppScheduler (AppSchedulerHost).
    // It receives app-level lifecycle callbacks from OH AppMgrService.
    appScheduler_ = new AppSchedulerAdapter(jvm, appThread);

    // Create AbilitySchedulerAdapter
    // This adapter implements OH IAbilityScheduler (AbilitySchedulerStub).
    // It receives ability-level lifecycle callbacks from OH AbilityManagerService.
    abilityScheduler_ = new AbilitySchedulerAdapter(jvm, appThread);

    // Create WindowManagerAgentAdapter
    // This adapter implements OH IWindowManagerAgent.
    // It receives system-wide window state notifications.
    wmAgent_ = new WindowManagerAgentAdapter(jvm);

    // AbilityConnection is created on demand during ConnectAbility calls
    // (see getAbilityConnection)

    registered_ = true;
    LOGI("OH callback stubs registered successfully");
    return true;
}

bool OHCallbackHandler::registerCallbacks() {
    LOGI("Registering OH callback stubs (legacy, no JNI context)...");

    // Legacy path: stubs created without JNI. They will log but not
    // bridge to Android IApplicationThread. Used for testing.
    registered_ = true;
    LOGI("OH callback stubs registered (legacy mode)");
    return true;
}

void OHCallbackHandler::unregisterCallbacks() {
    LOGI("Unregistering OH callback stubs");

    appScheduler_ = nullptr;
    abilityScheduler_ = nullptr;
    {
        std::lock_guard<std::mutex> lock(connectionMutex_);
        abilityConnections_.clear();
    }
    wmAgent_ = nullptr;
    jvm_ = nullptr;
    appThread_ = nullptr;
    registered_ = false;
}

OHOS::sptr<AbilitySchedulerAdapter> OHCallbackHandler::getAbilityScheduler(
    JavaVM* jvm, jobject appThread)
{
    if (abilityScheduler_ == nullptr && jvm != nullptr) {
        abilityScheduler_ = new AbilitySchedulerAdapter(jvm, appThread);
    }
    return abilityScheduler_;
}

OHOS::sptr<OHOS::AAFwk::IAbilityConnection> OHCallbackHandler::getAbilityConnection(
    int connectionId)
{
    std::lock_guard<std::mutex> lock(connectionMutex_);

    auto it = abilityConnections_.find(connectionId);
    if (it != abilityConnections_.end()) {
        return it->second;
    }

    // Create a new AbilityConnection stub for this connectionId.
    // The stub forwards OnAbilityConnectDone/OnAbilityDisconnectDone to Java
    // via ServiceConnectionRegistry using the connectionId.
    auto connection = OHCallbackHandler::createAbilityConnection(connectionId);
    if (connection != nullptr) {
        abilityConnections_[connectionId] = connection;
        LOGI("Created AbilityConnection for connectionId=%d (total=%zu)",
             connectionId, abilityConnections_.size());
    }
    return connection;
}

void OHCallbackHandler::removeAbilityConnection(int connectionId)
{
    std::lock_guard<std::mutex> lock(connectionMutex_);
    auto it = abilityConnections_.find(connectionId);
    if (it != abilityConnections_.end()) {
        abilityConnections_.erase(it);
        LOGI("Removed AbilityConnection for connectionId=%d (remaining=%zu)",
             connectionId, abilityConnections_.size());
    }
}

OHOS::sptr<OHOS::AAFwk::IAbilityConnection> OHCallbackHandler::createAbilityConnection(
    int connectionId)
{
    if (!jvm_) {
        LOGE("Cannot create AbilityConnection: JavaVM is null");
        return nullptr;
    }
    return new AbilityConnectionAdapter(jvm_, connectionId);
}

}  // namespace oh_adapter
