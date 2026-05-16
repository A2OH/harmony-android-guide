/*
 * oh_callback_handler.h
 *
 * OH system service -> Android App reverse callback handler.
 * Manages the creation and registration of OH IPC Stub adapters
 * that receive callbacks from OH system services and bridge them
 * to the Android application via JNI.
 *
 * Callback stubs managed:
 *   AppSchedulerAdapter       (OH IAppScheduler)      -> IApplicationThread
 *   AbilitySchedulerAdapter   (OH IAbilityScheduler)  -> Activity lifecycle
 *   AbilityConnectionBridge   (OH IAbilityConnection) -> IServiceConnection
 *   WindowManagerAgentAdapter (OH IWindowManagerAgent) -> internal
 */
#ifndef OH_CALLBACK_HANDLER_H
#define OH_CALLBACK_HANDLER_H

#include <jni.h>
#include <map>
#include <mutex>
#include "iremote_object.h"
#include "ability_connect_callback_interface.h"

// Pull in full adapter definitions; OHCallbackHandler holds sptr<...> members
// of each, and its implicitly-generated destructor needs each T to be complete
// at every translation unit that #includes this header.
#include "app_scheduler_adapter.h"
#include "ability_scheduler_adapter.h"
#include "../../window/jni/window_manager_agent_adapter.h"

namespace oh_adapter {

class OHCallbackHandler {
public:
    static OHCallbackHandler& getInstance();

    /**
     * Register callback stubs with OH system services.
     * Creates all adapter stubs. Actual registration with services
     * happens during service-specific calls (e.g., AttachApplication).
     *
     * @param jvm           JavaVM pointer for callback threads.
     * @param appThread     Java IApplicationThread instance.
     */
    bool registerCallbacks(JavaVM* jvm, jobject appThread);

    /**
     * Legacy registration without JNI context.
     */
    bool registerCallbacks();

    /**
     * Unregister callbacks.
     */
    void unregisterCallbacks();

    /**
     * Get the AbilityScheduler adapter for a specific ability.
     * Creates one if it doesn't exist for the given token.
     */
    OHOS::sptr<AbilitySchedulerAdapter> getAbilityScheduler(JavaVM* jvm, jobject appThread);

    /**
     * Get or create an AbilityConnection stub for a specific connectionId.
     * Each bindService call gets its own connection so that multiple
     * concurrent bindings can be tracked independently.
     *
     * @param connectionId  Local connection ID assigned by Java ServiceConnectionRegistry
     * @return IAbilityConnection stub for this connection
     */
    OHOS::sptr<OHOS::AAFwk::IAbilityConnection> getAbilityConnection(int connectionId);

    /**
     * Remove the AbilityConnection stub for a given connectionId.
     * Called when unbindService is performed.
     *
     * @param connectionId  The connection to remove
     */
    void removeAbilityConnection(int connectionId);

    /**
     * Get the WindowManagerAgent adapter.
     */
    OHOS::sptr<WindowManagerAgentAdapter> getWindowManagerAgent() const {
        return wmAgent_;
    }

    bool isRegistered() const { return registered_; }

private:
    OHCallbackHandler() = default;
    ~OHCallbackHandler() = default;

    // Factory: create an AbilityConnection stub that routes callbacks
    // to Java ServiceConnectionRegistry for the given connectionId. Uses
    // the per-instance jvm_ captured during registerCallbacks().
    OHOS::sptr<OHOS::AAFwk::IAbilityConnection> createAbilityConnection(int connectionId);

    bool registered_ = false;
    JavaVM* jvm_ = nullptr;
    jobject appThread_ = nullptr;

    OHOS::sptr<AppSchedulerAdapter> appScheduler_ = nullptr;
    OHOS::sptr<AbilitySchedulerAdapter> abilityScheduler_ = nullptr;
    // Per-connection AbilityConnection stubs (connectionId -> IAbilityConnection)
    std::map<int, OHOS::sptr<OHOS::AAFwk::IAbilityConnection>> abilityConnections_;
    std::mutex connectionMutex_;
    OHOS::sptr<WindowManagerAgentAdapter> wmAgent_ = nullptr;
};

}  // namespace oh_adapter

#endif  // OH_CALLBACK_HANDLER_H
