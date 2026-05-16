/*
 * app_scheduler_adapter.h
 *
 * Reverse callback adapter: OH AppSchedulerHost -> Android IApplicationThread.
 *
 * This class inherits from OH's AppSchedulerHost (IPC Stub) and receives
 * lifecycle/memory/config callbacks from OH AppMgrService via Binder.
 * Each callback is bridged to the Android IApplicationThread via JNI.
 *
 * Replaces the simulation-based approach in oh_callback_handler.cpp with
 * a real OH Stub class that participates in OH IPC.
 */
#ifndef OH_ADAPTER_APP_SCHEDULER_ADAPTER_H
#define OH_ADAPTER_APP_SCHEDULER_ADAPTER_H

#include <jni.h>
#include <cstdint>
#include <cstdarg>
#include <string>
#include <mutex>

#include "app_scheduler_host.h"

namespace oh_adapter {

class AppSchedulerAdapter : public OHOS::AppExecFwk::AppSchedulerHost {
public:
    /**
     * Constructor.
     * @param vm           JavaVM pointer for obtaining JNIEnv on Binder threads.
     * @param appThread    Java IApplicationThread.Stub instance (local ref).
     *                     A global ref is created and stored internally.
     */
    AppSchedulerAdapter(JavaVM* vm, jobject appThread);
    ~AppSchedulerAdapter() override;

    // ================================================================
    // IAppScheduler virtual method overrides
    // ================================================================

    // --- App Lifecycle ---
    // [BRIDGED] -> IApplicationThread.setProcessState(PROCESS_STATE_TOP)
    bool ScheduleForegroundApplication() override;

    // [BRIDGED] -> IApplicationThread.setProcessState(PROCESS_STATE_CACHED_EMPTY)
    void ScheduleBackgroundApplication() override;

    // [BRIDGED] -> IApplicationThread.scheduleExit / scheduleSuicide
    void ScheduleTerminateApplication(bool isLastProcess = false) override;

    // [BRIDGED] -> IApplicationThread.scheduleSuicide
    void ScheduleProcessSecurityExit() override;

    // --- Memory Management ---
    // [BRIDGED] -> IApplicationThread.scheduleLowMemory
    void ScheduleLowMemory() override;

    // [BRIDGED] -> IApplicationThread.scheduleTrimMemory(level)
    void ScheduleShrinkMemory(const int level) override;

    // [BRIDGED] -> IApplicationThread.scheduleTrimMemory(mappedLevel)
    void ScheduleMemoryLevel(int32_t level, bool isShellCall = false) override;

    // [OH_ONLY] Heap memory diagnostics
    void ScheduleHeapMemory(const int32_t pid, OHOS::AppExecFwk::MallocInfo &mallocInfo) override;

    // [OH_ONLY] JS heap memory diagnostics
    void ScheduleJsHeapMemory(OHOS::AppExecFwk::JsHeapDumpInfo &info) override;

    // [OH_ONLY] CJ heap memory diagnostics
    void ScheduleCjHeapMemory(OHOS::AppExecFwk::CjHeapDumpInfo &info) override;

    // --- Application Launch ---
    // [BRIDGED] -> IApplicationThread.bindApplication(...)
    void ScheduleLaunchApplication(const OHOS::AppExecFwk::AppLaunchData &data,
                                   const OHOS::AppExecFwk::Configuration &config) override;

    // [OH_ONLY] Module installation update
    void ScheduleUpdateApplicationInfoInstalled(const OHOS::AppExecFwk::ApplicationInfo &appInfo,
                                                const std::string &bundleName) override;

    // [OH_ONLY] AbilityStage lifecycle
    void ScheduleAbilityStage(const OHOS::AppExecFwk::HapModuleInfo &hapModuleInfo) override;

    // --- Ability Lifecycle ---
    // [BRIDGED] -> IApplicationThread.scheduleTransaction(LaunchActivityItem)
    void ScheduleLaunchAbility(const OHOS::AppExecFwk::AbilityInfo &info,
                               const OHOS::sptr<OHOS::IRemoteObject> &token,
                               const std::shared_ptr<OHOS::AAFwk::Want> &want,
                               int32_t abilityRecordId) override;

    // [BRIDGED] -> IApplicationThread.scheduleTransaction(DestroyActivityItem)
    void ScheduleCleanAbility(const OHOS::sptr<OHOS::IRemoteObject> &token,
                              bool isCacheProcess = false) override;

    // --- Configuration / Profile ---
    // [BRIDGED] -> IApplicationThread.scheduleConfigurationChanged(config)
    void ScheduleConfigurationUpdated(const OHOS::AppExecFwk::Configuration &config,
        OHOS::AppExecFwk::ConfigUpdateReason reason =
            OHOS::AppExecFwk::ConfigUpdateReason::CONFIG_UPDATE_REASON_DEFAULT) override;

    // [OH_ONLY] Profile change
    void ScheduleProfileChanged(const OHOS::AppExecFwk::Profile &profile) override;

    // --- Service / Process ---
    // [OH_ONLY] Accept want for specified process
    void ScheduleAcceptWant(const OHOS::AAFwk::Want &want, const std::string &moduleName) override;

    // [OH_ONLY] Prepare terminate
    void SchedulePrepareTerminate(const std::string &moduleName) override;

    // [OH_ONLY] New process request
    void ScheduleNewProcessRequest(const OHOS::AAFwk::Want &want,
                                   const std::string &moduleName) override;

    // --- Hot Fix / Quick Fix ---
    // [OH_ONLY] Load repair patch
    int32_t ScheduleNotifyLoadRepairPatch(const std::string &bundleName,
        const OHOS::sptr<OHOS::AppExecFwk::IQuickFixCallback> &callback,
        const int32_t recordId) override;

    // [OH_ONLY] Hot reload page
    int32_t ScheduleNotifyHotReloadPage(
        const OHOS::sptr<OHOS::AppExecFwk::IQuickFixCallback> &callback,
        const int32_t recordId) override;

    // [OH_ONLY] Unload repair patch
    int32_t ScheduleNotifyUnLoadRepairPatch(const std::string &bundleName,
        const OHOS::sptr<OHOS::AppExecFwk::IQuickFixCallback> &callback,
        const int32_t recordId) override;

    // --- Fault / Debug ---
    // [OH_ONLY] App fault notification
    int32_t ScheduleNotifyAppFault(const OHOS::AppExecFwk::FaultData &faultData) override;

    // [OH_ONLY] GC state change
    int32_t ScheduleChangeAppGcState(int32_t state, uint64_t tid = 0) override;

    // [OH_ONLY] Debug attach/detach
    void AttachAppDebug(bool isDebugFromLocal) override;
    void DetachAppDebug() override;

    // --- IPC / FFRT / ArkWeb Dump ---
    // [OH_ONLY] IPC dump
    int32_t ScheduleDumpIpcStart(std::string &result) override;
    int32_t ScheduleDumpIpcStop(std::string &result) override;
    int32_t ScheduleDumpIpcStat(std::string &result) override;

    // [OH_ONLY] Cache process
    void ScheduleCacheProcess() override;

    // [OH_ONLY] FFRT dump
    int32_t ScheduleDumpFfrt(std::string &result) override;

    // [OH_ONLY] ArkWeb dump
    int32_t ScheduleDumpArkWeb(const std::string &customArgs, std::string &result) override;

    // [OH_ONLY] Clear page stack
    void ScheduleClearPageStack() override;

    // [OH_ONLY] Watchdog background status
    void SetWatchdogBackgroundStatus(bool status) override;

    // [OH_ONLY] Load ability finished callback
    void OnLoadAbilityFinished(uint64_t callbackId, int32_t pid) override;

private:
    /**
     * Obtain JNIEnv for the current thread (attaches if necessary).
     * Callbacks arrive on OH Binder threads, so we must attach to the JVM.
     * @param env  Output JNIEnv pointer.
     * @return true if the thread was newly attached (caller should detach).
     */
    bool getJNIEnv(JNIEnv** env);

    /**
     * Call a method on the Java IApplicationThread object.
     * Thread-safe: obtains JNIEnv via AttachCurrentThread.
     *
     * @param methodName  Java method name.
     * @param signature   JNI method signature.
     * @param ...         Method arguments.
     */
    void callJavaMethod(const char* methodName, const char* signature, ...);

    /**
     * Map OH memory level to Android TRIM_MEMORY_* constant.
     */
    int mapMemoryLevel(int ohLevel);

    JavaVM* jvm_ = nullptr;
    jobject app_thread_ = nullptr;       // Global ref to IApplicationThread
    jclass app_thread_class_ = nullptr;  // Global ref to the class
    std::mutex jni_mutex_;               // Protects JNI global ref access
};

}  // namespace oh_adapter

#endif  // OH_ADAPTER_APP_SCHEDULER_ADAPTER_H
