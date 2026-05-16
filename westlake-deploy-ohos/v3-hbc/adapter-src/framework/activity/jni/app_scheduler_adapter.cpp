/*
 * app_scheduler_adapter.cpp
 *
 * Reverse callback adapter implementation.
 * Receives OH AppMgrService callbacks via Binder and bridges them
 * to the Android IApplicationThread via JNI.
 *
 * Thread safety: All callbacks arrive on OH Binder threads.
 * JNIEnv is obtained via JavaVM->AttachCurrentThread for each call.
 */

#include "app_scheduler_adapter.h"

#include <android/log.h>
#include <cstdarg>
#include <map>
#include <mutex>
#include <nlohmann/json.hpp>

// G2.2 (2026-04-30): missing ⑨ AttachAbilityThread reverse callback.
// We instantiate one AbilitySchedulerAdapter per launched ability and
// register it with OH AMS, so OH AMS has a per-ability IAbilityScheduler
// callback target.  Without this, OH AMS triggers LIFECYCLE_HALF_TIMEOUT
// ~1s after ScheduleLaunchAbility and kills the app.
#include "ability_scheduler_adapter.h"
#include "oh_ability_manager_client.h"
#include "oh_app_mgr_client.h"  // G2.14i: ApplicationForegrounded reverse callback
#include <iremote_object.h>

// 2026-04-30 方向 2: AppSchedulerBridge 已搬到 oh-adapter-runtime.jar，OH IPC
// native thread (system CL only) FindClass 找不到，必须走 PathClassLoader.loadClass。
// adapter_bridge_load_class 在 adapter_bridge.cpp 里实现，cache 自 JNI_OnLoad 时
// 通过 AppSpawnXInit.getClassLoader() 拿到的 PathClassLoader。
extern "C" jclass adapter_bridge_load_class(JNIEnv* env, const char* binaryName);

#define LOG_TAG "OH_AppSchedulerAdapter"
// B.37 (2026-04-29 EOD+2): direct HiLogPrint bypass for child diagnostics.
// __android_log_print → liblog → hilog bridge is broken in fork child (per
// feedback.txt P3 backlog), so logs from this adapter (which runs in child
// after fork) silently disappear.  Use HiLogPrint direct.  Domain 0xD000F00
// + tag "OH_AppSchedulerAdapter" appears as "C00f00/OH_AppSchedulerAdapter"
// in hilog.
extern "C" int HiLogPrint(int type, int level, unsigned int domain,
                          const char* tag, const char* fmt, ...)
    __attribute__((__format__(printf, 5, 6)));
#define ALOGD(fmt, ...) HiLogPrint(3, 3, 0xD000F00u, LOG_TAG, fmt, ##__VA_ARGS__)
#define ALOGI(fmt, ...) HiLogPrint(3, 4, 0xD000F00u, LOG_TAG, fmt, ##__VA_ARGS__)
#define ALOGW(fmt, ...) HiLogPrint(3, 5, 0xD000F00u, LOG_TAG, fmt, ##__VA_ARGS__)
#define ALOGE(fmt, ...) HiLogPrint(3, 6, 0xD000F00u, LOG_TAG, fmt, ##__VA_ARGS__)

// Android process state constants
static constexpr int PROCESS_STATE_TOP = 2;
static constexpr int PROCESS_STATE_CACHED_EMPTY = 16;

namespace oh_adapter {

// ================================================================
// Construction / Destruction
// ================================================================

AppSchedulerAdapter::AppSchedulerAdapter(JavaVM* vm, jobject appThread)
    : jvm_(vm)
{
    ALOGI("AppSchedulerAdapter created");
    if (jvm_ == nullptr) {
        ALOGE("JavaVM is null, JNI bridging will not work");
        return;
    }
    JNIEnv* env = nullptr;
    bool needDetach = getJNIEnv(&env);
    if (env == nullptr) {
        ALOGE("Failed to obtain JNIEnv in constructor");
        return;
    }

    // Create global references so they survive across threads
    app_thread_ = env->NewGlobalRef(appThread);
    if (app_thread_ == nullptr) {
        ALOGE("Failed to create global ref for IApplicationThread");
    } else {
        jclass clazz = env->GetObjectClass(app_thread_);
        app_thread_class_ = static_cast<jclass>(env->NewGlobalRef(clazz));
        env->DeleteLocalRef(clazz);
        ALOGI("IApplicationThread global ref created successfully");
    }

    if (needDetach) {
        jvm_->DetachCurrentThread();
    }
}

AppSchedulerAdapter::~AppSchedulerAdapter()
{
    ALOGI("AppSchedulerAdapter destroyed");
    if (jvm_ == nullptr) {
        return;
    }
    JNIEnv* env = nullptr;
    bool needDetach = getJNIEnv(&env);
    if (env != nullptr) {
        if (app_thread_ != nullptr) {
            env->DeleteGlobalRef(app_thread_);
            app_thread_ = nullptr;
        }
        if (app_thread_class_ != nullptr) {
            env->DeleteGlobalRef(app_thread_class_);
            app_thread_class_ = nullptr;
        }
    }
    if (needDetach) {
        jvm_->DetachCurrentThread();
    }
}

// ================================================================
// JNI Helpers
// ================================================================

bool AppSchedulerAdapter::getJNIEnv(JNIEnv** env)
{
    *env = nullptr;
    if (jvm_ == nullptr) {
        return false;
    }

    // Check if current thread is already attached
    jint result = jvm_->GetEnv(reinterpret_cast<void**>(env), JNI_VERSION_1_6);
    if (result == JNI_OK) {
        return false; // Already attached, no need to detach
    }

    // Attach current thread (OH Binder thread) to JVM
    JavaVMAttachArgs args;
    args.version = JNI_VERSION_1_6;
    args.name = "OH_BinderThread";
    args.group = nullptr;

    result = jvm_->AttachCurrentThread(env, &args);
    if (result != JNI_OK) {
        ALOGE("AttachCurrentThread failed with error %d", result);
        *env = nullptr;
        return false;
    }
    return true; // Newly attached, caller should detach
}

void AppSchedulerAdapter::callJavaMethod(const char* methodName, const char* signature, ...)
{
    std::lock_guard<std::mutex> lock(jni_mutex_);

    if (app_thread_ == nullptr || app_thread_class_ == nullptr) {
        ALOGW("callJavaMethod(%s): IApplicationThread ref is null, skipping", methodName);
        return;
    }

    JNIEnv* env = nullptr;
    bool needDetach = getJNIEnv(&env);
    if (env == nullptr) {
        ALOGE("callJavaMethod(%s): Failed to obtain JNIEnv", methodName);
        return;
    }

    jmethodID method = env->GetMethodID(app_thread_class_, methodName, signature);
    if (method == nullptr) {
        ALOGW("callJavaMethod(%s): Method not found with signature %s", methodName, signature);
        env->ExceptionClear();
        if (needDetach) jvm_->DetachCurrentThread();
        return;
    }

    va_list args;
    va_start(args, signature);
    env->CallVoidMethodV(app_thread_, method, args);
    va_end(args);

    if (env->ExceptionCheck()) {
        ALOGE("callJavaMethod(%s): Java exception occurred", methodName);
        env->ExceptionDescribe();
        env->ExceptionClear();
    }

    if (needDetach) {
        jvm_->DetachCurrentThread();
    }
}

int AppSchedulerAdapter::mapMemoryLevel(int ohLevel)
{
    // OH memory levels -> Android TRIM_MEMORY_* constants
    // OH: 0=normal, 1=low, 2=critical
    switch (ohLevel) {
        case 0:  return 5;   // TRIM_MEMORY_RUNNING_MODERATE
        case 1:  return 10;  // TRIM_MEMORY_RUNNING_LOW
        case 2:  return 15;  // TRIM_MEMORY_RUNNING_CRITICAL
        default: return 5;   // Default to moderate
    }
}

// ================================================================
// Category 1: App Lifecycle
// ================================================================

bool AppSchedulerAdapter::ScheduleForegroundApplication()
{
    ALOGI("[BRIDGED] ScheduleForegroundApplication -> setProcessState(PROCESS_STATE_TOP)");
    callJavaMethod("setProcessState", "(I)V", static_cast<jint>(PROCESS_STATE_TOP));

    // G2.14i (2026-05-01): mirror OH MainThread::HandleForegroundApplication.
    // OH 标准客户端协议（见 main_thread.cpp:2826 HandleForegroundApplication）：
    //   IAppScheduler::ScheduleForegroundApplication IPC handler 把工作 PostTask 到
    //   main looper；handler 立即返回让 AppMS::ScheduleForegroundRunning 同步 IPC
    //   完成（这样 AppMS 接下来执行 foregroundingAbilityTokens_.insert(token)）。
    //   下一轮 main looper iter 跑 PerformForeground()，**然后**才调
    //   appMgr_->ApplicationForegrounded(recordId) 给 AppMS 反向回调。
    //
    // adapter 等价：调 Java 端 AppSchedulerBridge.notifyForegroundDeferred(recordId)
    // 它内部用 ActivityThread main Handler.post(Runnable) 把反向回调入队。当前 IPC
    // 同步返回后，AppMS::AbilityForeground 完成 insert，main looper 下一轮 iter 处理
    // queue 中的 Runnable，调 native notifyApplicationForegrounded(recordId) 反向 IPC。
    // 整链路用 looper 做同步，不依赖任何时间窗口。
    JNIEnv* env = nullptr;
    bool needDetach = getJNIEnv(&env);
    if (env != nullptr) {
        jclass bridgeCls = adapter_bridge_load_class(env, "adapter.activity.AppSchedulerBridge");
        if (bridgeCls != nullptr) {
            jmethodID mid = env->GetStaticMethodID(bridgeCls, "notifyForegroundDeferred", "(I)V");
            if (mid != nullptr) {
                jint recordId = static_cast<jint>(OHAppMgrClient::getInstance().getRecordId());
                env->CallStaticVoidMethod(bridgeCls, mid, recordId);
                if (env->ExceptionCheck()) {
                    env->ExceptionDescribe();
                    env->ExceptionClear();
                    ALOGE("[G2.14i] notifyForegroundDeferred threw");
                } else {
                    ALOGI("[G2.14i] notifyForegroundDeferred(recordId=%d) posted to main looper",
                          recordId);
                }
            } else {
                ALOGE("[G2.14i] notifyForegroundDeferred method not found on AppSchedulerBridge");
                if (env->ExceptionCheck()) env->ExceptionClear();
            }
            env->DeleteLocalRef(bridgeCls);
        } else {
            ALOGE("[G2.14i] adapter_bridge_load_class(AppSchedulerBridge) returned null");
        }
        if (needDetach) jvm_->DetachCurrentThread();
    } else {
        ALOGE("[G2.14i] getJNIEnv failed; ApplicationForegrounded callback skipped");
    }

    return true;
}

void AppSchedulerAdapter::ScheduleBackgroundApplication()
{
    ALOGI("[BRIDGED] ScheduleBackgroundApplication -> setProcessState(PROCESS_STATE_CACHED_EMPTY)");
    callJavaMethod("setProcessState", "(I)V", static_cast<jint>(PROCESS_STATE_CACHED_EMPTY));
    OHAppMgrClient::getInstance().notifyAppState(static_cast<int>(AppState::STATE_BACKGROUND));
}

void AppSchedulerAdapter::ScheduleTerminateApplication(bool isLastProcess)
{
    ALOGI("[BRIDGED] ScheduleTerminateApplication(isLastProcess=%d) -> scheduleExit/scheduleSuicide",
          isLastProcess);
    if (isLastProcess) {
        callJavaMethod("scheduleSuicide", "()V");
    } else {
        callJavaMethod("scheduleExit", "()V");
    }
}

void AppSchedulerAdapter::ScheduleProcessSecurityExit()
{
    ALOGI("[BRIDGED] ScheduleProcessSecurityExit -> scheduleSuicide");
    callJavaMethod("scheduleSuicide", "()V");
}

// ================================================================
// Category 2: Memory Management
// ================================================================

void AppSchedulerAdapter::ScheduleLowMemory()
{
    ALOGI("[BRIDGED] ScheduleLowMemory -> scheduleLowMemory");
    callJavaMethod("scheduleLowMemory", "()V");
}

void AppSchedulerAdapter::ScheduleShrinkMemory(const int level)
{
    int androidLevel = mapMemoryLevel(level);
    ALOGI("[BRIDGED] ScheduleShrinkMemory(oh=%d) -> scheduleTrimMemory(%d)", level, androidLevel);
    callJavaMethod("scheduleTrimMemory", "(I)V", static_cast<jint>(androidLevel));
}

void AppSchedulerAdapter::ScheduleMemoryLevel(int32_t level, bool isShellCall)
{
    int androidLevel = mapMemoryLevel(level);
    ALOGI("[BRIDGED] ScheduleMemoryLevel(oh=%d, shell=%d) -> scheduleTrimMemory(%d)",
          level, isShellCall, androidLevel);
    callJavaMethod("scheduleTrimMemory", "(I)V", static_cast<jint>(androidLevel));
}

void AppSchedulerAdapter::ScheduleHeapMemory(const int32_t pid,
                                              OHOS::AppExecFwk::MallocInfo &mallocInfo)
{
    ALOGD("[OH_ONLY] ScheduleHeapMemory(pid=%d) - OH malloc diagnostics, no direct Android equivalent",
          pid);
    // No Android equivalent - OH expects MallocInfo struct output
}

void AppSchedulerAdapter::ScheduleJsHeapMemory(OHOS::AppExecFwk::JsHeapDumpInfo &info)
{
    ALOGD("[OH_ONLY] ScheduleJsHeapMemory - ArkTS JS engine specific, no Android equivalent");
}

void AppSchedulerAdapter::ScheduleCjHeapMemory(OHOS::AppExecFwk::CjHeapDumpInfo &info)
{
    ALOGD("[OH_ONLY] ScheduleCjHeapMemory - CJ language specific, no Android equivalent");
}

// ================================================================
// Category 3: Application Launch
// ================================================================

void AppSchedulerAdapter::ScheduleLaunchApplication(const OHOS::AppExecFwk::AppLaunchData &data,
                                                     const OHOS::AppExecFwk::Configuration &config)
{
    // 2026-04-30 方向 3 真适配: OH IPC ScheduleLaunchApplication 是 OH AppMgr 在
    // AttachApplication 后的反向 callback, 时机对应 AOSP IApplicationThread.bindApplication
    // → handleBindApplication。本入口路由到 Java AppSchedulerBridge.nativeOnScheduleLaunchApplication
    // 让 AOSP 自己跑完整 bind 流程 (LoadedApk / Application / Instrumentation / onCreate)。
    const std::string& bundleName = data.GetApplicationInfo().bundleName;
    const std::string& processName = data.GetProcessInfo().GetProcessName();
    pid_t pid = data.GetProcessInfo().GetPid();
    int32_t recordId = data.GetRecordId();
    ALOGI("[BRIDGED] ScheduleLaunchApplication -> bindApplication: bundle=%s process=%s pid=%d recordId=%d",
          bundleName.c_str(), processName.c_str(), pid, recordId);

    // G2.14i: cache recordId so async ApplicationForegrounded reverse callback
    // (from ScheduleForegroundApplication) reaches the right OH AppRunningRecord.
    OHAppMgrClient::getInstance().setRecordId(recordId);

    std::lock_guard<std::mutex> lock(jni_mutex_);

    if (app_thread_ == nullptr || app_thread_class_ == nullptr) {
        ALOGW("ScheduleLaunchApplication: IApplicationThread ref is null");
        return;
    }

    JNIEnv* env = nullptr;
    bool needDetach = getJNIEnv(&env);
    if (env == nullptr) {
        ALOGE("ScheduleLaunchApplication: Failed to obtain JNIEnv");
        return;
    }

    // Set process state to TOP (existing OH state sync)
    jmethodID setProcessState = env->GetMethodID(app_thread_class_, "setProcessState", "(I)V");
    if (setProcessState != nullptr) {
        env->CallVoidMethod(app_thread_, setProcessState, static_cast<jint>(PROCESS_STATE_TOP));
        if (env->ExceptionCheck()) { env->ExceptionDescribe(); env->ExceptionClear(); }
    }

    // 2026-04-30 方向 3: route to AppSchedulerBridge.nativeOnScheduleLaunchApplication
    // for real handleBindApplication invocation. AppSchedulerBridge lives in
    // oh-adapter-runtime.jar (PathClassLoader, non-BCP) so we use adapter_bridge_load_class.
    //
    // Phase 1 (本次扩展): 把 OH Configuration 8 个标准 key 通过 GetItem 提取，
    // 以扁平 String[] {k0,v0,k1,v1,...} 形式传给 Java 桥；Java 侧
    // OhConfigurationConverter 据此构造完整 android.content.res.Configuration。
    // 字段映射权威：doc/ability_manager_ipc_adapter_design.html §1.1.4.4
    static const char* kConfigKeys[] = {
        "ohos.system.locale",
        "ohos.system.language",
        "ohos.system.colorMode",
        "ohos.system.hour",
        "ohos.system.fontSizeScale",
        "ohos.system.fontWeightScale",
        "ohos.system.mcc",
        "ohos.system.mnc",
        "const.build.characteristics",
        "input.pointer.device",
    };
    constexpr size_t kKeyCount = sizeof(kConfigKeys) / sizeof(kConfigKeys[0]);

    jclass stringClass = env->FindClass("java/lang/String");
    jobjectArray jKvArr = env->NewObjectArray(static_cast<jsize>(kKeyCount * 2),
                                              stringClass, nullptr);
    for (size_t i = 0; i < kKeyCount; ++i) {
        const char* key = kConfigKeys[i];
        std::string val = config.GetItem(key);
        jstring jKey = env->NewStringUTF(key);
        jstring jVal = env->NewStringUTF(val.c_str());
        env->SetObjectArrayElement(jKvArr, static_cast<jsize>(i * 2), jKey);
        env->SetObjectArrayElement(jKvArr, static_cast<jsize>(i * 2 + 1), jVal);
        env->DeleteLocalRef(jKey);
        env->DeleteLocalRef(jVal);
    }
    env->DeleteLocalRef(stringClass);

    ALOGI("[B39-LA] routing to Java nativeOnScheduleLaunchApplication");
    jclass bridgeClass = adapter_bridge_load_class(env, "adapter.activity.AppSchedulerBridge");
    if (bridgeClass != nullptr) {
        jmethodID onLaunchApp = env->GetStaticMethodID(bridgeClass,
            "nativeOnScheduleLaunchApplication",
            "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;I[Ljava/lang/String;)V");
        if (onLaunchApp != nullptr) {
            jstring jBundle = env->NewStringUTF(bundleName.c_str());
            jstring jProc = env->NewStringUTF(processName.c_str());
            env->CallStaticVoidMethod(bridgeClass, onLaunchApp, app_thread_,
                                      jBundle, jProc, static_cast<jint>(pid), jKvArr);
            env->DeleteLocalRef(jBundle);
            env->DeleteLocalRef(jProc);
            if (env->ExceptionCheck()) {
                ALOGE("[B39-LA] Java exception thrown by nativeOnScheduleLaunchApplication");
                env->ExceptionDescribe();
                env->ExceptionClear();
            } else {
                ALOGI("[B39-LA] nativeOnScheduleLaunchApplication returned without exception");
            }
        } else {
            ALOGW("[B39-LA] nativeOnScheduleLaunchApplication method not found");
            if (env->ExceptionCheck()) env->ExceptionClear();
        }
        env->DeleteLocalRef(bridgeClass);
    } else {
        ALOGE("[B39-LA] AppSchedulerBridge class not loaded — cannot trigger bindApplication");
    }
    env->DeleteLocalRef(jKvArr);

    if (needDetach) {
        jvm_->DetachCurrentThread();
    }
}

void AppSchedulerAdapter::ScheduleUpdateApplicationInfoInstalled(
    const OHOS::AppExecFwk::ApplicationInfo &appInfo, const std::string &bundleName)
{
    ALOGD("[OH_ONLY] ScheduleUpdateApplicationInfoInstalled(bundle=%s) "
          "- logged, no direct bridge in Phase 1", bundleName.c_str());
}

void AppSchedulerAdapter::ScheduleAbilityStage(const OHOS::AppExecFwk::HapModuleInfo &hapModuleInfo)
{
    ALOGD("[OH_ONLY] ScheduleAbilityStage - OH module-level lifecycle, "
          "no direct Android AbilityStage concept");
}

// ================================================================
// Category 4: Ability Lifecycle
// ================================================================

void AppSchedulerAdapter::ScheduleLaunchAbility(const OHOS::AppExecFwk::AbilityInfo &info,
                                                 const OHOS::sptr<OHOS::IRemoteObject> &token,
                                                 const std::shared_ptr<OHOS::AAFwk::Want> &want,
                                                 int32_t abilityRecordId)
{
    ALOGI("[BRIDGED] ScheduleLaunchAbility(recordId=%d) "
          "-> scheduleTransaction(LaunchActivityItem)", abilityRecordId);

    // Bridge to Android IApplicationThread.scheduleTransaction with LaunchActivityItem.
    // The actual Activity launch transaction construction requires Android-side
    // ClientTransaction building which is done in the Java bridge layer.
    std::lock_guard<std::mutex> lock(jni_mutex_);

    if (app_thread_ == nullptr || app_thread_class_ == nullptr) {
        ALOGW("ScheduleLaunchAbility: IApplicationThread ref is null");
        return;
    }

    JNIEnv* env = nullptr;
    bool needDetach = getJNIEnv(&env);
    if (env == nullptr) {
        ALOGE("ScheduleLaunchAbility: Failed to obtain JNIEnv");
        return;
    }

    // 2026-04-30 (B.47, P2 §1.2): pass full AbilityInfo + Want as JSON, plus
    // OH IRemoteObject token address (for reverse-callback map). Java side
    // OhAbilityInfoConverter / OhWantConverter parse the JSON and build a
    // complete LaunchActivityItem for any Android App.
    // Want::ToJson is private — compose JSON manually from public getters.
    nlohmann::json wantJson;
    if (want != nullptr) {
        wantJson["bundleName"]  = want->GetElement().GetBundleName();
        wantJson["abilityName"] = want->GetElement().GetAbilityName();
        wantJson["moduleName"]  = want->GetElement().GetModuleName();
        wantJson["deviceId"]    = want->GetElement().GetDeviceID();
        wantJson["action"]      = want->GetAction();
        wantJson["uri"]         = want->GetUriString();
        wantJson["type"]        = want->GetType();
        wantJson["flags"]       = static_cast<int>(want->GetFlags());
        nlohmann::json entitiesJson = nlohmann::json::array();
        for (const auto& e : want->GetEntities()) entitiesJson.push_back(e);
        wantJson["entities"]    = entitiesJson;
        nlohmann::json paramsJson;
        want->GetParams().ToJson(paramsJson);
        wantJson["parameters"]  = paramsJson;
    }
    nlohmann::json abilityJson;
    abilityJson["name"]              = info.name;
    abilityJson["bundleName"]        = info.bundleName;
    abilityJson["moduleName"]        = info.moduleName;
    abilityJson["className"]         = info.className;
    abilityJson["process"]           = info.process;
    abilityJson["label"]             = info.label;
    abilityJson["labelId"]           = info.labelId;
    abilityJson["description"]       = info.description;
    abilityJson["descriptionId"]     = info.descriptionId;
    abilityJson["iconPath"]          = info.iconPath;
    abilityJson["iconId"]            = info.iconId;
    abilityJson["theme"]             = info.theme;
    abilityJson["launchMode"]        = static_cast<int>(info.launchMode);
    abilityJson["orientation"]       = static_cast<int>(info.orientation);
    abilityJson["visible"]           = info.visible;
    abilityJson["enabled"]           = info.enabled;
    abilityJson["uri"]               = info.uri;
    abilityJson["readPermission"]    = info.readPermission;
    abilityJson["writePermission"]   = info.writePermission;
    abilityJson["maxWindowRatio"]    = info.maxWindowRatio;
    abilityJson["minWindowRatio"]    = info.minWindowRatio;
    abilityJson["maxWindowWidth"]    = info.maxWindowWidth;
    abilityJson["minWindowWidth"]    = info.minWindowWidth;
    abilityJson["maxWindowHeight"]   = info.maxWindowHeight;
    abilityJson["minWindowHeight"]   = info.minWindowHeight;
    abilityJson["removeMissionAfterTerminate"] = info.removeMissionAfterTerminate;
    abilityJson["excludeFromMissions"] = info.excludeFromMissions;
    abilityJson["allowSelfRedirect"] = info.allowSelfRedirect;
    abilityJson["continuable"]       = info.continuable;
    abilityJson["type"]              = static_cast<int>(info.type);
    nlohmann::json permsJson = nlohmann::json::array();
    for (const auto& p : info.permissions) permsJson.push_back(p);
    abilityJson["permissions"] = permsJson;
    nlohmann::json configChangesJson = nlohmann::json::array();
    for (const auto& c : info.configChanges) configChangesJson.push_back(c);
    abilityJson["configChanges"] = configChangesJson;
    nlohmann::json windowModesJson = nlohmann::json::array();
    for (const auto& m : info.windowModes) windowModesJson.push_back(static_cast<int>(m));
    abilityJson["windowModes"] = windowModesJson;
    std::string abilityJsonStr = abilityJson.dump();
    std::string wantJsonStr = wantJson.dump();
    // OH IRemoteObject pointer encoded as long for token map (reverse callback).
    // Java side stores in OhTokenRegistry; LifecycleAdapter uses for finishActivity → OH TerminateAbility.
    jlong tokenAddr = reinterpret_cast<jlong>(token.GetRefPtr());

    ALOGI("[B47-SLA] ScheduleLaunchAbility: ability=%s want.action=%s abilityJsonLen=%zu wantJsonLen=%zu tokenAddr=0x%llx",
        info.name.c_str(),
        (want ? want->GetAction().c_str() : ""),
        abilityJsonStr.size(), wantJsonStr.size(),
        static_cast<unsigned long long>(tokenAddr));

    // §3.1.5.6.4 P2 — DeathRecipient on OH AbilityRecord token.
    // When OH AMS terminates the ability and releases the token, this fires
    // on a binder thread; we then notify Java OhTokenRegistry to drop the
    // mapping so adapter doesn't hold a stale token + so subsequent
    // sub-window CreateAndConnect attempts don't reference a dead parent.
    class TokenDeathRecipient : public OHOS::IRemoteObject::DeathRecipient {
    public:
        TokenDeathRecipient(JavaVM* jvm, jlong addr) : jvm_(jvm), tokenAddr_(addr) {}
        void OnRemoteDied(const OHOS::wptr<OHOS::IRemoteObject>& /*remote*/) override {
            ALOGI("[B47-SLA] TokenDeathRecipient: OH token 0x%llx died — clean up registry",
                  static_cast<unsigned long long>(tokenAddr_));
            JNIEnv* env = nullptr;
            bool needDetach = false;
            if (jvm_->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
                if (jvm_->AttachCurrentThread(&env, nullptr) != JNI_OK) {
                    ALOGE("[B47-SLA] TokenDeathRecipient: AttachCurrentThread failed");
                    return;
                }
                needDetach = true;
            }
            // adapter.core.OhTokenRegistry lives in BCP — reachable via
            // FindClass on the system class loader (no PathClassLoader needed).
            jclass regClass = env->FindClass("adapter/core/OhTokenRegistry");
            if (regClass != nullptr) {
                jmethodID m = env->GetStaticMethodID(regClass, "removeByOhToken", "(J)V");
                if (m != nullptr) {
                    env->CallStaticVoidMethod(regClass, m, tokenAddr_);
                    if (env->ExceptionCheck()) {
                        env->ExceptionDescribe();
                        env->ExceptionClear();
                    }
                } else {
                    if (env->ExceptionCheck()) env->ExceptionClear();
                }
                env->DeleteLocalRef(regClass);
            } else {
                if (env->ExceptionCheck()) env->ExceptionClear();
            }
            if (needDetach) jvm_->DetachCurrentThread();
        }
    private:
        JavaVM* jvm_;
        jlong tokenAddr_;
    };

    // G2.2 (2026-04-30): ⑨ AttachAbilityThread reverse callback.
    // OH AMS expects each per-ability IAbilityScheduler stub to be registered
    // via this IPC before processing lifecycle states.  Without it, OH AMS
    // triggers LIFECYCLE_HALF_TIMEOUT (~1s) and killProcessByPid the app.
    //
    // We instantiate one AbilitySchedulerAdapter per launched token and store
    // the sptr in a static map (keyed by raw IRemoteObject ptr) to keep the
    // local stub alive while OH AMS holds a remote ref to it.  Released on
    // ScheduleCleanAbility for the same token (best-effort).
    {
        static std::mutex schedulerMapMu;
        static std::map<jlong, OHOS::sptr<AbilitySchedulerAdapter>> schedulerMap;
        // §3.1.5.6.4 — keep DeathRecipient sptrs alive alongside schedulers
        // (they share the same lifetime).
        static std::map<jlong, OHOS::sptr<TokenDeathRecipient>> deathRecipientMap;
        std::lock_guard<std::mutex> lk(schedulerMapMu);
        bool firstAttach = false;
        if (schedulerMap.find(tokenAddr) == schedulerMap.end()) {
            OHOS::sptr<AbilitySchedulerAdapter> scheduler =
                new AbilitySchedulerAdapter(jvm_, app_thread_, tokenAddr);
            schedulerMap[tokenAddr] = scheduler;
            firstAttach = true;
            // §3.1.5.6.4 — register DeathRecipient so when OH AMS releases
            // this token (ability terminate / process die), Java OhTokenRegistry
            // gets cleaned up.
            OHOS::sptr<TokenDeathRecipient> recipient =
                OHOS::sptr<TokenDeathRecipient>::MakeSptr(jvm_, tokenAddr);
            if (token != nullptr && token->IsProxyObject()) {
                if (token->AddDeathRecipient(recipient)) {
                    deathRecipientMap[tokenAddr] = recipient;
                    ALOGI("[B47-SLA] TokenDeathRecipient registered for token=0x%llx",
                          static_cast<unsigned long long>(tokenAddr));
                } else {
                    ALOGW("[B47-SLA] AddDeathRecipient failed for token=0x%llx",
                          static_cast<unsigned long long>(tokenAddr));
                }
            } else {
                // Local IRemoteObject (in-process token, e.g. tests) — no death.
                deathRecipientMap[tokenAddr] = recipient;
            }
            auto& client = OHAbilityManagerClient::getInstance();
            auto proxy = client.getProxy();
            if (proxy != nullptr) {
                int rc = proxy->AttachAbilityThread(scheduler, token);
                ALOGI("[G2.2] AttachAbilityThread(token=0x%llx) rc=%d",
                      static_cast<unsigned long long>(tokenAddr), rc);
            } else {
                ALOGW("[G2.2] AttachAbilityThread skipped: AMS proxy not connected");
            }
        } else {
            ALOGI("[G2.2] AttachAbilityThread skipped: already registered for token=0x%llx",
                  static_cast<unsigned long long>(tokenAddr));
        }

        // G2.14m (2026-05-01): RETIRED proactive INACTIVE echo.
        // Earlier G2.3 hack synthesized AbilityTransitionDone(INACTIVE=1) right after
        // AttachAbilityThread to stop AMS LIFECYCLE_HALF_TIMEOUT. That worked but
        // collapsed OH's two-phase lifecycle (Application.foreground vs Activity.resume)
        // into one Android LaunchActivityItem+ResumeActivityItem transaction, which
        // raced AMS state machine and made AbilityTransitionDone(FG) hit rc=22.
        //
        // Correct two-phase flow (now active):
        //   1. AttachAbilityThread (above) → AMS::SetScheduler → isReady_=true →
        //      AppMS::MoveToForeground → AppMS::ScheduleForegroundRunning IPC →
        //      adapter ScheduleForegroundApplication → notifyForegroundDeferred →
        //      next main-looper iter → ApplicationForegrounded → AMS state→FOREGROUNDING
        //   2. AMS::ScheduleAbilityTransaction(FOREGROUND_NEW) IPC → adapter
        //      AbilitySchedulerAdapter::ScheduleAbilityTransaction →
        //      Java onScheduleAbilityTransaction → ResumeActivityItem transaction →
        //      Activity.onResume → activityResumed → AbilityTransitionDone(FG) rc=0
        //
        // No INACTIVE needs to be synthesized — AMS doesn't require it for the
        // launch→foreground path. INACTIVE only appears when foregrounded ability
        // is later paused (Activity.onPause), which is naturally driven by
        // Android lifecycle then.
    }

    jclass bridgeClass = adapter_bridge_load_class(env, "adapter.activity.AppSchedulerBridge");
    if (bridgeClass != nullptr) {
        // New signature (B.47): nativeOnScheduleLaunchAbility(Object, String, String, int, String, String, long)
        // params: appThread, bundleName, abilityName, abilityRecordId, abilityJson, wantJson, ohTokenAddr
        jmethodID onLaunch = env->GetStaticMethodID(bridgeClass,
            "nativeOnScheduleLaunchAbility",
            "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;J)V");
        if (onLaunch != nullptr) {
            jstring jBundle  = env->NewStringUTF(info.bundleName.c_str());
            jstring jAbility = env->NewStringUTF(info.name.c_str());
            jstring jAbJson  = env->NewStringUTF(abilityJsonStr.c_str());
            jstring jWantJson = env->NewStringUTF(wantJsonStr.c_str());
            env->CallStaticVoidMethod(bridgeClass, onLaunch, app_thread_,
                                      jBundle, jAbility,
                                      static_cast<jint>(abilityRecordId),
                                      jAbJson, jWantJson, tokenAddr);
            env->DeleteLocalRef(jBundle);
            env->DeleteLocalRef(jAbility);
            env->DeleteLocalRef(jAbJson);
            env->DeleteLocalRef(jWantJson);
            if (env->ExceptionCheck()) {
                ALOGE("[B47-SLA] Java exception thrown by nativeOnScheduleLaunchAbility");
                env->ExceptionDescribe();
                env->ExceptionClear();
            }
        } else {
            ALOGW("[B47-SLA] nativeOnScheduleLaunchAbility (new sig) not found");
            if (env->ExceptionCheck()) env->ExceptionClear();
        }
        env->DeleteLocalRef(bridgeClass);
    } else {
        ALOGW("[B47-SLA] AppSchedulerBridge class not found");
        if (env->ExceptionCheck()) env->ExceptionClear();
    }

    if (needDetach) {
        jvm_->DetachCurrentThread();
    }
}

void AppSchedulerAdapter::ScheduleCleanAbility(const OHOS::sptr<OHOS::IRemoteObject> &token,
                                                bool isCacheProcess)
{
    ALOGI("[BRIDGED] ScheduleCleanAbility(cache=%d) "
          "-> scheduleTransaction(DestroyActivityItem)", isCacheProcess);

    // Bridge to Android IApplicationThread.scheduleTransaction with DestroyActivityItem.
    // Similar to ScheduleLaunchAbility, the actual transaction construction is
    // delegated to the Java bridge layer.
    std::lock_guard<std::mutex> lock(jni_mutex_);

    if (app_thread_ == nullptr || app_thread_class_ == nullptr) {
        ALOGW("ScheduleCleanAbility: IApplicationThread ref is null");
        return;
    }

    JNIEnv* env = nullptr;
    bool needDetach = getJNIEnv(&env);
    if (env == nullptr) {
        ALOGE("ScheduleCleanAbility: Failed to obtain JNIEnv");
        return;
    }

    jclass bridgeClass = adapter_bridge_load_class(env, "adapter.activity.AppSchedulerBridge");
    if (bridgeClass != nullptr) {
        jmethodID onClean = env->GetStaticMethodID(bridgeClass,
            "nativeOnScheduleCleanAbility", "(Ljava/lang/Object;Z)V");
        if (onClean != nullptr) {
            env->CallStaticVoidMethod(bridgeClass, onClean, app_thread_,
                                      static_cast<jboolean>(isCacheProcess));
            if (env->ExceptionCheck()) {
                ALOGE("ScheduleCleanAbility: Java exception");
                env->ExceptionDescribe();
                env->ExceptionClear();
            }
        } else {
            ALOGW("ScheduleCleanAbility: nativeOnScheduleCleanAbility not found");
            env->ExceptionClear();
        }
        env->DeleteLocalRef(bridgeClass);
    } else {
        ALOGW("ScheduleCleanAbility: AppSchedulerBridge class not found");
        env->ExceptionClear();
    }

    if (needDetach) {
        jvm_->DetachCurrentThread();
    }
}

// ================================================================
// Category 5: Configuration / Profile
// ================================================================

void AppSchedulerAdapter::ScheduleConfigurationUpdated(
    const OHOS::AppExecFwk::Configuration &config,
    OHOS::AppExecFwk::ConfigUpdateReason reason)
{
    ALOGI("[BRIDGED] ScheduleConfigurationUpdated(reason=%d) "
          "-> scheduleConfigurationChanged", static_cast<int>(reason));

    // Bridge to IApplicationThread.scheduleConfigurationChanged.
    // OH Configuration fields are converted to Android Configuration in the Java layer.
    std::lock_guard<std::mutex> lock(jni_mutex_);

    if (app_thread_ == nullptr || app_thread_class_ == nullptr) {
        ALOGW("ScheduleConfigurationUpdated: IApplicationThread ref is null");
        return;
    }

    JNIEnv* env = nullptr;
    bool needDetach = getJNIEnv(&env);
    if (env == nullptr) {
        ALOGE("ScheduleConfigurationUpdated: Failed to obtain JNIEnv");
        return;
    }

    jclass bridgeClass = adapter_bridge_load_class(env, "adapter.activity.AppSchedulerBridge");
    if (bridgeClass != nullptr) {
        jmethodID onConfig = env->GetStaticMethodID(bridgeClass,
            "nativeOnScheduleConfigurationUpdated", "(Ljava/lang/Object;Ljava/lang/String;)V");
        if (onConfig != nullptr) {
            // Serialize config to string for Java-side parsing
            std::string configStr = config.GetName();
            jstring jConfigStr = env->NewStringUTF(configStr.c_str());
            env->CallStaticVoidMethod(bridgeClass, onConfig, app_thread_, jConfigStr);
            env->DeleteLocalRef(jConfigStr);
            if (env->ExceptionCheck()) {
                ALOGE("ScheduleConfigurationUpdated: Java exception");
                env->ExceptionDescribe();
                env->ExceptionClear();
            }
        } else {
            ALOGW("ScheduleConfigurationUpdated: native method not found");
            env->ExceptionClear();
        }
        env->DeleteLocalRef(bridgeClass);
    } else {
        ALOGW("ScheduleConfigurationUpdated: AppSchedulerBridge class not found");
        env->ExceptionClear();
    }

    if (needDetach) {
        jvm_->DetachCurrentThread();
    }
}

void AppSchedulerAdapter::ScheduleProfileChanged(const OHOS::AppExecFwk::Profile &profile)
{
    ALOGD("[OH_ONLY] ScheduleProfileChanged - OH Profile, partial Configuration mapping");
}

// ================================================================
// Category 6: Service / Process
// ================================================================

void AppSchedulerAdapter::ScheduleAcceptWant(const OHOS::AAFwk::Want &want,
                                              const std::string &moduleName)
{
    ALOGD("[OH_ONLY] ScheduleAcceptWant(module=%s) - OH specified process, "
          "approximate bindService mapping", moduleName.c_str());
}

void AppSchedulerAdapter::SchedulePrepareTerminate(const std::string &moduleName)
{
    ALOGD("[OH_ONLY] SchedulePrepareTerminate(module=%s) - "
          "OH module terminate, Android uses onDestroy", moduleName.c_str());
}

void AppSchedulerAdapter::ScheduleNewProcessRequest(const OHOS::AAFwk::Want &want,
                                                     const std::string &moduleName)
{
    ALOGD("[OH_ONLY] ScheduleNewProcessRequest(module=%s) - "
          "OH new process request, logged only", moduleName.c_str());
}

// ================================================================
// Category 7: Hot Fix / Quick Fix (OH_ONLY)
// ================================================================

int32_t AppSchedulerAdapter::ScheduleNotifyLoadRepairPatch(
    const std::string &bundleName,
    const OHOS::sptr<OHOS::AppExecFwk::IQuickFixCallback> &callback,
    const int32_t recordId)
{
    ALOGD("[OH_ONLY] ScheduleNotifyLoadRepairPatch(bundle=%s, record=%d) - "
          "OH hot-fix, no Android equivalent", bundleName.c_str(), recordId);
    return 0;
}

int32_t AppSchedulerAdapter::ScheduleNotifyHotReloadPage(
    const OHOS::sptr<OHOS::AppExecFwk::IQuickFixCallback> &callback,
    const int32_t recordId)
{
    ALOGD("[OH_ONLY] ScheduleNotifyHotReloadPage(record=%d) - "
          "OH hot reload, no Android equivalent", recordId);
    return 0;
}

int32_t AppSchedulerAdapter::ScheduleNotifyUnLoadRepairPatch(
    const std::string &bundleName,
    const OHOS::sptr<OHOS::AppExecFwk::IQuickFixCallback> &callback,
    const int32_t recordId)
{
    ALOGD("[OH_ONLY] ScheduleNotifyUnLoadRepairPatch(bundle=%s, record=%d) - "
          "OH hot-fix, no Android equivalent", bundleName.c_str(), recordId);
    return 0;
}

// ================================================================
// Category 8: Fault / Debug
// ================================================================

int32_t AppSchedulerAdapter::ScheduleNotifyAppFault(const OHOS::AppExecFwk::FaultData &faultData)
{
    ALOGD("[OH_ONLY] ScheduleNotifyAppFault - OH fault notification, logged only");
    return 0;
}

int32_t AppSchedulerAdapter::ScheduleChangeAppGcState(int32_t state, uint64_t tid)
{
    ALOGD("[OH_ONLY] ScheduleChangeAppGcState(state=%d, tid=%llu) - "
          "OH NativeEngine GC, Android uses ART GC",
          state, static_cast<unsigned long long>(tid));
    return 0;
}

void AppSchedulerAdapter::AttachAppDebug(bool isDebugFromLocal)
{
    ALOGD("[OH_ONLY] AttachAppDebug(local=%d) - OH debug attach, mechanism differs from Android",
          isDebugFromLocal);
}

void AppSchedulerAdapter::DetachAppDebug()
{
    ALOGD("[OH_ONLY] DetachAppDebug - no Android detach equivalent");
}

// ================================================================
// Category 9: IPC / FFRT / ArkWeb Dump (OH_ONLY)
// ================================================================

int32_t AppSchedulerAdapter::ScheduleDumpIpcStart(std::string &result)
{
    ALOGD("[OH_ONLY] ScheduleDumpIpcStart - OH IPC diagnostics, no Android equivalent");
    result = "Not supported in adapter mode";
    return 0;
}

int32_t AppSchedulerAdapter::ScheduleDumpIpcStop(std::string &result)
{
    ALOGD("[OH_ONLY] ScheduleDumpIpcStop - OH IPC diagnostics, no Android equivalent");
    result = "Not supported in adapter mode";
    return 0;
}

int32_t AppSchedulerAdapter::ScheduleDumpIpcStat(std::string &result)
{
    ALOGD("[OH_ONLY] ScheduleDumpIpcStat - OH IPC diagnostics, no Android equivalent");
    result = "Not supported in adapter mode";
    return 0;
}

void AppSchedulerAdapter::ScheduleCacheProcess()
{
    ALOGD("[OH_ONLY] ScheduleCacheProcess - OH process cache, Android uses oom_adj");
}

int32_t AppSchedulerAdapter::ScheduleDumpFfrt(std::string &result)
{
    ALOGD("[OH_ONLY] ScheduleDumpFfrt - OH FFRT diagnostics, no Android equivalent");
    result = "Not supported in adapter mode";
    return 0;
}

int32_t AppSchedulerAdapter::ScheduleDumpArkWeb(const std::string &customArgs, std::string &result)
{
    ALOGD("[OH_ONLY] ScheduleDumpArkWeb - OH ArkWeb diagnostics, no Android equivalent");
    result = "Not supported in adapter mode";
    return 0;
}

void AppSchedulerAdapter::ScheduleClearPageStack()
{
    ALOGD("[OH_ONLY] ScheduleClearPageStack - OH recovery specific");
}

void AppSchedulerAdapter::SetWatchdogBackgroundStatus(bool status)
{
    ALOGD("[OH_ONLY] SetWatchdogBackgroundStatus(status=%d) - "
          "OH watchdog, Android uses ANR mechanism", status);
}

void AppSchedulerAdapter::OnLoadAbilityFinished(uint64_t callbackId, int32_t pid)
{
    ALOGD("[OH_ONLY] OnLoadAbilityFinished(callbackId=%llu, pid=%d) - "
          "OH ability load completion callback",
          static_cast<unsigned long long>(callbackId), pid);
}

}  // namespace oh_adapter
