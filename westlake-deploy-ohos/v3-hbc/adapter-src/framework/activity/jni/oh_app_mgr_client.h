/*
 * oh_app_mgr_client.h
 *
 * OpenHarmony AppMgr IPC client.
 * Wraps remote calls to OH AppMgrService (application process management).
 */
#ifndef OH_APP_MGR_CLIENT_H
#define OH_APP_MGR_CLIENT_H

#include <jni.h>
#include <string>
#include "iremote_object.h"
#include "app_mgr_interface.h"

namespace oh_adapter {

class AppSchedulerAdapter;

// App state constants (maps to OH ApplicationState)
enum class AppState : int {
    STATE_FOREGROUND = 2,
    STATE_BACKGROUND = 4,
    STATE_TERMINATED = 5,
};

class OHAppMgrClient {
public:
    static OHAppMgrClient& getInstance();

    /**
     * Connect to OH AppMgrService.
     */
    bool connect();

    /**
     * Disconnect from service.
     */
    void disconnect();

    /**
     * Register current App process with OH AppMgr.
     * Creates an AppSchedulerAdapter (OH IAppScheduler stub) and passes it
     * to AppMgr via AttachApplication. The stub receives lifecycle callbacks
     * from OH system and bridges them to Android IApplicationThread via JNI.
     *
     * @param jvm           JavaVM pointer for JNI in callback threads.
     * @param appThread     Java IApplicationThread instance (local ref).
     * @param pid           Process ID.
     * @param uid           User ID.
     * @param bundleName    OH bundle name / Android package name.
     */
    bool attachApplication(JavaVM* jvm, jobject appThread,
                           int pid, int uid, const std::string& bundleName);

    /**
     * Legacy overload (no JNI context). Used when JVM/appThread are set separately.
     */
    bool attachApplication(int pid, int uid, const std::string& bundleName);

    /**
     * Notify App state change.
     * Corresponds to OH IAppMgr.ApplicationForegrounded/Backgrounded/Terminated().
     */
    void notifyAppState(int state);

    /**
     * G2.14i: cache the recordId received via ScheduleLaunchApplication.AppLaunchData
     * so subsequent ApplicationForegrounded/Backgrounded calls reach the right
     * AppRunningRecord on the OH AppMS side. Default -1 hits "get appRecord fail".
     */
    void setRecordId(int32_t recordId) { recordId_ = recordId; }
    int32_t getRecordId() const { return recordId_; }

    /**
     * Get the AppScheduler adapter (for external use / testing).
     */
    OHOS::sptr<AppSchedulerAdapter> getAppSchedulerAdapter() const { return appScheduler_; }

    bool isConnected() const { return connected_; }

private:
    OHAppMgrClient() = default;
    ~OHAppMgrClient() = default;

    bool connected_ = false;
    int pid_ = 0;
    int uid_ = 0;
    int32_t recordId_ = -1;
    std::string bundleName_;
    OHOS::sptr<OHOS::AppExecFwk::IAppMgr> proxy_ = nullptr;
    OHOS::sptr<AppSchedulerAdapter> appScheduler_ = nullptr;
};

}  // namespace oh_adapter

#endif  // OH_APP_MGR_CLIENT_H
