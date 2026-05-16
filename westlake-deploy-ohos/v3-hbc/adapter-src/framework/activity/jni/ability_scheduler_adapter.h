/*
 * ability_scheduler_adapter.h
 *
 * Reverse callback adapter: OH AbilitySchedulerStub -> Android IApplicationThread.
 *
 * This class inherits from OH AbilitySchedulerStub so it can be registered
 * with OH AbilityManagerService as an IAbilityScheduler. When OH AMS calls
 * lifecycle/service/data methods on this stub, they are forwarded via JNI
 * to the Android IApplicationThread (and ContentProvider where applicable).
 *
 * Threading: OH AMS calls arrive on IPC binder threads. Each override
 * attaches to the JVM via JavaVM->AttachCurrentThread before making JNI calls.
 */
#ifndef OH_ADAPTER_ABILITY_SCHEDULER_ADAPTER_H
#define OH_ADAPTER_ABILITY_SCHEDULER_ADAPTER_H

#include <jni.h>
#include <string>
#include <mutex>

#include "ability_scheduler_stub.h"
// V7 inner_api: pull in the relational_store types so std::vector<ValuesBucket>
// member access doesn't see an incomplete type during template instantiation.
#include "values_bucket.h"

namespace oh_adapter {
using OHOS::sptr;

class AbilitySchedulerAdapter : public OHOS::AAFwk::AbilitySchedulerStub {
public:
    /**
     * Construct the adapter.
     *
     * @param jvm          Pointer to the JavaVM (for AttachCurrentThread on IPC threads).
     * @param appThread    A Java IApplicationThread.Stub instance. A JNI global ref
     *                     is created internally; the caller may release its local ref.
     * @param tokenAddr    G2.14m (2026-05-01) — the OH ability token's raw ptr, recorded
     *                     in OhTokenRegistry. Forwarded to Java onScheduleAbilityTransaction
     *                     so the Java side can find the matching Android Activity IBinder
     *                     when constructing ResumeActivityItem / Pause/Stop transactions.
     */
    AbilitySchedulerAdapter(JavaVM* jvm, jobject appThread, jlong tokenAddr = 0);
    ~AbilitySchedulerAdapter() override;

    // ================================================================
    // Category 1: Ability Lifecycle  [BRIDGED]
    // ================================================================

    bool ScheduleAbilityTransaction(const OHOS::AAFwk::Want& want,
                                    const OHOS::AAFwk::LifeCycleStateInfo& targetState,
                                    sptr<OHOS::AAFwk::SessionInfo> sessionInfo = nullptr) override;

    void ScheduleShareData(const int32_t& uniqueId) override;

    void SendResult(int requestCode, int resultCode,
                    const OHOS::AAFwk::Want& resultWant) override;

    bool SchedulePrepareTerminateAbility() override;

    void ScheduleSaveAbilityState() override;

    void ScheduleRestoreAbilityState(const OHOS::AppExecFwk::PacMap& inState) override;

    // ================================================================
    // Category 2: Service Connection  [BRIDGED]
    // ================================================================

    void ScheduleConnectAbility(const OHOS::AAFwk::Want& want) override;

    void ScheduleDisconnectAbility(const OHOS::AAFwk::Want& want) override;

    void ScheduleCommandAbility(const OHOS::AAFwk::Want& want,
                                bool restart, int startId) override;

    void ScheduleCommandAbilityWindow(const OHOS::AAFwk::Want& want,
                                      const sptr<OHOS::AAFwk::SessionInfo>& sessionInfo,
                                      OHOS::AAFwk::WindowCommand winCmd) override;

    // ================================================================
    // Category 3: Data Operations  [BRIDGED -> ContentProvider]
    // ================================================================

    std::vector<std::string> GetFileTypes(const OHOS::Uri& uri,
                                          const std::string& mimeTypeFilter) override;

    int OpenFile(const OHOS::Uri& uri, const std::string& mode) override;

    int OpenRawFile(const OHOS::Uri& uri, const std::string& mode) override;

    int Insert(const OHOS::Uri& uri,
               const OHOS::NativeRdb::ValuesBucket& value) override;

    int Update(const OHOS::Uri& uri,
               const OHOS::NativeRdb::ValuesBucket& value,
               const OHOS::NativeRdb::DataAbilityPredicates& predicates) override;

    int Delete(const OHOS::Uri& uri,
               const OHOS::NativeRdb::DataAbilityPredicates& predicates) override;

    std::shared_ptr<OHOS::NativeRdb::AbsSharedResultSet> Query(
        const OHOS::Uri& uri,
        std::vector<std::string>& columns,
        const OHOS::NativeRdb::DataAbilityPredicates& predicates) override;

    std::string GetType(const OHOS::Uri& uri) override;

    bool Reload(const OHOS::Uri& uri, const OHOS::AppExecFwk::PacMap& extras) override;

    int BatchInsert(const OHOS::Uri& uri,
                    const std::vector<OHOS::NativeRdb::ValuesBucket>& values) override;

    std::shared_ptr<OHOS::AppExecFwk::PacMap> Call(
        const OHOS::Uri& uri, const std::string& method,
        const std::string& arg, const OHOS::AppExecFwk::PacMap& pacMap) override;

    OHOS::Uri NormalizeUri(const OHOS::Uri& uri) override;

    OHOS::Uri DenormalizeUri(const OHOS::Uri& uri) override;

    std::vector<std::shared_ptr<OHOS::AppExecFwk::DataAbilityResult>> ExecuteBatch(
        const std::vector<std::shared_ptr<OHOS::AppExecFwk::DataAbilityOperation>>& operations) override;

    // ================================================================
    // Category 4: Data Observer  [BRIDGED]
    // ================================================================

    bool ScheduleRegisterObserver(const OHOS::Uri& uri,
                                  const sptr<OHOS::AAFwk::IDataAbilityObserver>& dataObserver) override;

    bool ScheduleUnregisterObserver(const OHOS::Uri& uri,
                                    const sptr<OHOS::AAFwk::IDataAbilityObserver>& dataObserver) override;

    bool ScheduleNotifyChange(const OHOS::Uri& uri) override;

    // ================================================================
    // Category 5: Continuation  [OH_ONLY]
    // ================================================================

    void ContinueAbility(const std::string& deviceId, uint32_t versionCode) override;

    void NotifyContinuationResult(int32_t result) override;

    // ================================================================
    // Category 6: Misc
    // ================================================================

    void DumpAbilityInfo(const std::vector<std::string>& params,
                         std::vector<std::string>& info) override;

    int CreateModalUIExtension(const OHOS::AAFwk::Want& want) override;

    void OnExecuteIntent(const OHOS::AAFwk::Want& want) override;

    void CallRequest() override;

    void UpdateSessionToken(sptr<OHOS::IRemoteObject> sessionToken) override;

    void ScheduleCollaborate(const OHOS::AAFwk::Want& want) override;

    void ScheduleAbilityRequestFailure(const std::string& requestId,
                                       const OHOS::AppExecFwk::ElementName& element,
                                       const std::string& message,
                                       int32_t resultCode = 0) override;

    void ScheduleAbilityRequestSuccess(const std::string& requestId,
                                       const OHOS::AppExecFwk::ElementName& element) override;

    void ScheduleAbilitiesRequestDone(const std::string& requestKey,
                                      int32_t resultCode) override;

private:
    // Generic helper to invoke a method on mApplicationThread_ via JNI.
    // Returns the jvalue result (caller interprets). Logs errors internally.
    // sig: JNI method signature, e.g. "(Ljava/lang/String;I)V"
    // The variadic args are passed through to CallObjectMethod / CallVoidMethod / etc.
    void callJavaVoidMethod(const char* methodName, const char* sig, ...);
    jboolean callJavaBooleanMethod(const char* methodName, const char* sig, ...);
    jint callJavaIntMethod(const char* methodName, const char* sig, ...);

    // Convert OH Want to JSON string for passing through JNI.
    std::string wantToJson(const OHOS::AAFwk::Want& want);

    // Convert OH PacMap to JSON string for passing through JNI.
    std::string pacMapToJson(const OHOS::AppExecFwk::PacMap& pacMap);

    JavaVM* jvm_ = nullptr;
    jobject mApplicationThread_ = nullptr;   // JNI global ref to Java IApplicationThread.Stub
    jclass bridgeClass_ = nullptr;           // Cached global ref to AbilitySchedulerBridge class
    jobject bridgeObj_ = nullptr;            // JNI global ref to AbilitySchedulerBridge instance
    jlong tokenAddr_ = 0;                    // G2.14m: OH ability token raw ptr (key into OhTokenRegistry)
    std::mutex jniMutex_;                    // Protects JNI calls from concurrent IPC threads
};

}  // namespace oh_adapter

#endif  // OH_ADAPTER_ABILITY_SCHEDULER_ADAPTER_H
