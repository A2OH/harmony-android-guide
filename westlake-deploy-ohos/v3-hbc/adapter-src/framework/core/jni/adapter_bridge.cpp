/*
 * adapter_bridge.cpp
 *
 * JNI Bridge implementation.
 * Connects Android Java layer with OpenHarmony C++ IPC framework.
 *
 * Provides JNI native methods for:
 *   - OHEnvironment: initialization, service connection, shutdown
 *   - ActivityManagerAdapter / ActivityTaskManagerAdapter: ability start/connect
 *   - WindowManagerAdapter / WindowSessionAdapter: window creation, layout, surface
 */
#include "adapter_bridge.h"
#include "oh_ability_manager_client.h"
#include "oh_app_mgr_client.h"
#include "oh_callback_handler.h"
#include "oh_window_manager_client.h"
#include "oh_input_bridge.h"
#include "oh_surface_bridge.h"
#include "oh_graphic_buffer_producer.h"
#include "oh_common_event_client.h"
#include "oh_datashare_client.h"
#include "common_event_subscriber_adapter.h"
#include "intent_want_converter.h"

#include <android/log.h>
#include <cstdio>
#include <string>
#include <mutex>
#include <unistd.h>
#include <cerrno>
#include <cstring>
#include <dlfcn.h>

#include "ipc_skeleton.h"  // OHOS::IPCSkeleton::SetCallingIdentity below
#include <surface.h>       // OHOS::Surface — included transitively via oh_surface_bridge.h
                            // but make explicit so sptr<Surface> dtor sees complete type.

// initDataShareJniCache lives in framework/contentprovider/jni/oh_datashare_client.cpp
// inside `namespace oh_adapter {}`. Must declare extern in matching namespace
// so the C++ mangled name matches the definition:
//   defined:  _ZN10oh_adapter21initDataShareJniCacheEP7_JNIEnv
//   (global extern would look for _Z21initDataShareJniCacheP7_JNIEnv → mismatch)
namespace oh_adapter { extern bool initDataShareJniCache(JNIEnv* env); }

#define LOG_TAG "OH_JNI_Bridge"
// B.37 (2026-04-29 EOD+2): direct HiLogPrint bypass for child diagnostics.
// __android_log_print → liblog → hilog bridge is broken in fork child, so
// LOGI/LOGE silently drop in child.  Route to HiLogPrint directly.
extern "C" int HiLogPrint(int type, int level, unsigned int domain,
                          const char* tag, const char* fmt, ...)
    __attribute__((__format__(printf, 5, 6)));
#define LOGI(fmt, ...) HiLogPrint(3, 4, 0xD000F00u, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGE(fmt, ...) HiLogPrint(3, 6, 0xD000F00u, LOG_TAG, fmt, ##__VA_ARGS__)

// 2026-05-08 G2.14x: preload chipset Mali GPU driver into RTLD_GLOBAL scope so
// helloworld matches RS process loading: RS DT_NEEDED libmali.so.0 (eager link,
// global by default); App side OH wrapper EglWrapperLoader::Load uses RTLD_LOCAL
// for libEGL_impl.so (= libmali symlink), which keeps mali symbols private.
// This mismatch was identified as a candidate root cause for the EGLDisplay
// invalid issue seen in helloworld but not in RS. Pre-loading libmali.so.0 into
// the global scope before any hwui PLT resolution puts mali's eglXxx symbols
// into the global lookup chain, matching RS behavior.
__attribute__((constructor))
static void preload_libmali_global() {
    void* h = dlopen("libmali.so.0", RTLD_NOW | RTLD_GLOBAL);
    if (h == nullptr) {
        LOGE("preload libmali.so.0 failed: %{public}s", dlerror());
    } else {
        LOGI("preload libmali.so.0 -> RTLD_GLOBAL OK (handle=%p)", h);
    }
}

using namespace oh_adapter;

// ==================== AdapterBridge Implementation ====================

AdapterBridge& AdapterBridge::getInstance() {
    static AdapterBridge instance;
    return instance;
}

bool AdapterBridge::initialize(JNIEnv* env) {
    LOGI("AdapterBridge::initialize()");

    // Cache LifecycleAdapter JNI method IDs.  The class moved from
    // adapter.* to adapter.activity.*; FindClass on the legacy path returns
    // null (which propagated as a bare NoClassDefFoundError to the caller).
    jclass clazz = env->FindClass("adapter/activity/LifecycleAdapter");
    if (clazz == nullptr) {
        // Legacy path fallback in case some build still ships the old package
        if (env->ExceptionCheck()) env->ExceptionClear();
        clazz = env->FindClass("adapter/LifecycleAdapter");
    }
    if (clazz == nullptr) {
        LOGE("Failed to find adapter/activity/LifecycleAdapter class");
        if (env->ExceptionCheck()) env->ExceptionClear();
        return false;
    }
    lifecycle_adapter_class_ = (jclass)env->NewGlobalRef(clazz);
    on_oh_lifecycle_callback_ = env->GetMethodID(clazz, "onOHLifecycleCallback", "(II)V");
    if (on_oh_lifecycle_callback_ == nullptr) {
        LOGE("Failed to find onOHLifecycleCallback method");
        return false;
    }

    // Initialize DataShare JNI cache (MatrixCursor class/method IDs)
    if (!initDataShareJniCache(env)) {
        LOGE("Failed to initialize DataShare JNI cache");
        // Non-fatal: ContentProvider bridge won't work, but other bridges are fine
    }

    return true;
}

void AdapterBridge::shutdown() {
    LOGI("AdapterBridge::shutdown()");
    JNIEnv* env = getEnv();
    if (env && lifecycle_adapter_ref_) {
        env->DeleteGlobalRef(lifecycle_adapter_ref_);
        lifecycle_adapter_ref_ = nullptr;
    }
    if (env && lifecycle_adapter_class_) {
        env->DeleteGlobalRef(lifecycle_adapter_class_);
        lifecycle_adapter_class_ = nullptr;
    }
}

void AdapterBridge::setLifecycleAdapterRef(JNIEnv* env, jobject obj) {
    if (lifecycle_adapter_ref_) {
        env->DeleteGlobalRef(lifecycle_adapter_ref_);
    }
    lifecycle_adapter_ref_ = env->NewGlobalRef(obj);
}

JNIEnv* AdapterBridge::getEnv() {
    JNIEnv* env = nullptr;
    if (jvm_) {
        int status = jvm_->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
        if (status == JNI_EDETACHED) {
            jvm_->AttachCurrentThread(&env, nullptr);
        }
    }
    return env;
}

void AdapterBridge::callbackLifecycleChange(int abilityToken, int ohState) {
    JNIEnv* env = getEnv();
    if (!env || !lifecycle_adapter_ref_) {
        LOGE("Cannot callback: JNI env or LifecycleAdapter ref is null");
        return;
    }

    // Call LifecycleAdapter directly using the stored singleton reference
    env->CallVoidMethod(lifecycle_adapter_ref_, on_oh_lifecycle_callback_,
                        abilityToken, ohState);
}

// ==================== Static Helpers ====================

// Guard to ensure single initialization
static std::once_flag g_initFlag;
static bool g_initialized = false;

// Helper: extract a jstring to std::string safely
static std::string jstringToString(JNIEnv* env, jstring jstr) {
    if (!jstr) return "";
    const char* raw = env->GetStringUTFChars(jstr, nullptr);
    std::string result(raw);
    env->ReleaseStringUTFChars(jstr, raw);
    return result;
}

// Shared startAbility implementation used by per-Adapter JNI methods
static jint bridgeStartAbility(JNIEnv* env, jstring bundleName, jstring abilityName,
                                jstring action, jstring uri, jstring extraJson) {
    std::string bundle = jstringToString(env, bundleName);
    std::string ability = jstringToString(env, abilityName);
    std::string act = jstringToString(env, action);
    std::string u = jstringToString(env, uri);
    std::string extra = jstringToString(env, extraJson);

    LOGI("bridgeStartAbility: bundle=%s, ability=%s, action=%s",
         bundle.c_str(), ability.c_str(), act.c_str());

    WantParams want;
    want.bundleName = bundle;
    want.abilityName = ability;
    want.action = act;
    want.uri = u;
    want.parametersJson = extra;

    return OHAbilityManagerClient::getInstance().startAbility(want);
}

// Shared connectAbility implementation
static jint bridgeConnectAbility(JNIEnv* env, jstring bundleName,
                                  jstring abilityName, jint connectionId) {
    std::string bundle = jstringToString(env, bundleName);
    std::string ability = jstringToString(env, abilityName);

    LOGI("bridgeConnectAbility: bundle=%s, ability=%s, connId=%d",
         bundle.c_str(), ability.c_str(), connectionId);

    WantParams want;
    want.bundleName = bundle;
    want.abilityName = ability;

    return OHAbilityManagerClient::getInstance().connectAbility(want, connectionId);
}

// Shared disconnectAbility implementation
static jint bridgeDisconnectAbility(jint connectionId) {
    LOGI("bridgeDisconnectAbility: connectionId=%d", connectionId);
    return OHAbilityManagerClient::getInstance().disconnectAbility(connectionId);
}

// Shared stopServiceAbility implementation
static jint bridgeStopServiceAbility(JNIEnv* env, jstring bundleName, jstring abilityName) {
    std::string bundle = jstringToString(env, bundleName);
    std::string ability = jstringToString(env, abilityName);

    LOGI("bridgeStopServiceAbility: bundle=%s, ability=%s", bundle.c_str(), ability.c_str());

    WantParams want;
    want.bundleName = bundle;
    want.abilityName = ability;

    return OHAbilityManagerClient::getInstance().stopServiceAbility(want);
}

// Forward decl outside extern "C" so the C++ mangled name matches the
// definition in skia_codec_register.cpp.
namespace adapter { void RegisterAllSkiaCodecs(); }

// ==================== JNI Method Implementations ====================

extern "C" {

// Forward decl for B.34.1 explicit RegisterNatives (defined below).
JNIEXPORT void JNICALL
Java_com_android_internal_os_AppSpawnXInit_nativeHiLog(
        JNIEnv* env, jclass clazz, jstring jtag, jstring jmsg);

// B.34.1: HiLogPrint declared further below (line ~415) for B.32 nativeHiLog.
// Forward-declare here so JNI_OnLoad LOGI calls reach hilog directly (the
// __android_log_print → liblog → hilog bridge path is broken in fork
// children and possibly not initialized at JNI_OnLoad time in parent either,
// per feedback.txt P3 backlog).
extern "C" int HiLogPrint(int type, int level, unsigned int domain,
                          const char* tag, const char* fmt, ...)
    __attribute__((__format__(printf, 5, 6)));
#define ZYG_LOG_INFO(fmt, ...)  HiLogPrint(3, 4, 0xD000F00u, "AppSpawnX", fmt, ##__VA_ARGS__)
#define ZYG_LOG_ERR(fmt, ...)   HiLogPrint(3, 6, 0xD000F00u, "AppSpawnX", fmt, ##__VA_ARGS__)

// 2026-04-30 方向 2 单类试点：cache PathClassLoader for cross-thread access.
// 设置时机：JNI_OnLoad 时 FindClass(AppSpawnXInit) 经常返回 null（system CL
// 找不到 PathClassLoader 类），所以由 appspawnx_runtime.cpp 在 B.35.A 时机
// 反向 dlsym 调 adapter_bridge_set_class_loader 注入。OH IPC native threads
// (attached, system CL only) 通过 adapter_bridge_load_class 用它加载已搬到
// oh-adapter-runtime.jar 的类（如 AppSchedulerBridge）。
static JavaVM* g_adapter_jvm = nullptr;
static jobject g_adapter_class_loader = nullptr;   // global ref to PathClassLoader
static jmethodID g_load_class_method = nullptr;    // ClassLoader.loadClass(String)

extern "C" void adapter_bridge_set_class_loader(JavaVM* vm, jobject classLoader,
                                                jmethodID loadClassMethod) {
    g_adapter_jvm = vm;
    if (!classLoader) {
        ZYG_LOG_ERR("[Direction2] adapter_bridge_set_class_loader called with null cl");
        return;
    }
    JNIEnv* env = nullptr;
    if (vm && vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) == JNI_OK
        && env != nullptr) {
        if (g_adapter_class_loader) {
            env->DeleteGlobalRef(g_adapter_class_loader);
        }
        g_adapter_class_loader = env->NewGlobalRef(classLoader);
        g_load_class_method = loadClassMethod;
        ZYG_LOG_INFO("[Direction2] adapter_bridge_set_class_loader cached cl=%{public}p loadClass=%{public}p",
            g_adapter_class_loader, g_load_class_method);
    } else {
        ZYG_LOG_ERR("[Direction2] adapter_bridge_set_class_loader: GetEnv failed");
    }
}

extern "C" jclass adapter_bridge_load_class(JNIEnv* env, const char* binaryName) {
    if (!env || !binaryName) return nullptr;
    if (g_adapter_class_loader && g_load_class_method) {
        jstring jName = env->NewStringUTF(binaryName);
        jobject cls = env->CallObjectMethod(g_adapter_class_loader,
                                            g_load_class_method, jName);
        env->DeleteLocalRef(jName);
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
            return nullptr;
        }
        if (cls) return reinterpret_cast<jclass>(cls);
    }
    // Fallback: FindClass with '/' separator
    std::string jniName(binaryName);
    for (char& c : jniName) if (c == '.') c = '/';
    jclass cls = env->FindClass(jniName.c_str());
    if (env->ExceptionCheck()) env->ExceptionClear();
    return cls;
}

// G2.14e — InputEventReceiver native stubs (NO_INPUT_EVENT_TRANSPORT baseline).
// Registered via RegisterNatives in JNI_OnLoad below — same trick we use for
// AppSpawnXInit.nativeHiLog (B.34.1). Putting the registration in bridge.so's
// JNI_OnLoad ensures it runs in the App's child process where ART has full
// BCP loaded, sidestepping the parent-appspawn-x ordering problem where
// liboh_android_runtime's startReg runs before InputEventReceiver class
// is loadable. Implementations are no-op (Android input events for HelloWorld
// flow via OH MMI → InputEventBridge, not through this AOSP path).
namespace {
struct OhIER { int32_t magic; int64_t id; jobject weakRef; };
constexpr int32_t kOhIERMagic = 0x4F484552; // 'OHER'
std::atomic<int64_t> g_ierId{1};
}

extern "C" {
JNIEXPORT jlong JNICALL
Java_android_view_InputEventReceiver_nativeInit(JNIEnv* env, jclass,
        jobject receiverWeak, jobject /*inputChannel*/, jobject /*messageQueue*/) {
    auto* r = new (std::nothrow) OhIER();
    if (!r) return 0;
    r->magic = kOhIERMagic;
    r->id = g_ierId.fetch_add(1);
    r->weakRef = receiverWeak ? env->NewGlobalRef(receiverWeak) : nullptr;
    LOGI("InputEventReceiver.nativeInit id=%{public}lld (NO_INPUT_EVENT_TRANSPORT)",
         static_cast<long long>(r->id));
    return static_cast<jlong>(reinterpret_cast<uintptr_t>(r));
}

JNIEXPORT void JNICALL
Java_android_view_InputEventReceiver_nativeDispose(JNIEnv* env, jclass, jlong ptr) {
    auto* r = reinterpret_cast<OhIER*>(static_cast<uintptr_t>(ptr));
    if (!r || r->magic != kOhIERMagic) return;
    if (r->weakRef) env->DeleteGlobalRef(r->weakRef);
    delete r;
}

JNIEXPORT void JNICALL
Java_android_view_InputEventReceiver_nativeFinishInputEvent(JNIEnv*, jclass,
        jlong, jint, jboolean) { /* no-op */ }

JNIEXPORT void JNICALL
Java_android_view_InputEventReceiver_nativeReportTimeline(JNIEnv*, jclass,
        jlong, jint, jlong, jlong) { /* no-op */ }

JNIEXPORT jboolean JNICALL
Java_android_view_InputEventReceiver_nativeConsumeBatchedInputEvents(JNIEnv*, jclass,
        jlong, jlong) { return JNI_FALSE; }

JNIEXPORT jstring JNICALL
Java_android_view_InputEventReceiver_nativeDump(JNIEnv* env, jclass,
        jlong, jstring) { return env->NewStringUTF(""); }
}  // extern "C"

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    ZYG_LOG_INFO("[B34.1] JNI_OnLoad: oh_adapter_bridge entered");
    AdapterBridge::getInstance().setJavaVM(vm);

    // Bridge to OH libskia_canvaskit's SkCodecs::Register — without this the
    // 4-arg SkCodec::MakeFromStream wrapper sees an empty decoder list and
    // returns kUnimplemented for every PNG/JPEG/WEBP load (HelloWorld
    // setContentView -> ImageDecoder failure root cause).
    adapter::RegisterAllSkiaCodecs();

    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    // B.34.1 (2026-04-29 EOD+1): explicit RegisterNatives for
    // AppSpawnXInit.nativeHiLog.  bridge.so is loaded via OHEnvironment
    // System.loadLibrary which binds it to the BCP / bootstrap classloader.
    // AppSpawnXInit lives in oh-adapter-runtime.jar (PathClassLoader, non-BCP).
    // ART's auto-dlsym JNI lookup is classloader-scoped: a native method on
    // a PathClassLoader class is searched in libraries loaded by the same
    // PathClassLoader (or its ancestors via parent delegation).  bridge.so,
    // bound to bootstrap, is NOT in PathClassLoader's library set, so
    // AppSpawnXInit.nativeHiLog auto-dlsym fails → UnsatisfiedLinkError →
    // appLog catch falls through to System.err.println → /dev/null.
    //
    // RegisterNatives bypasses classloader scoping: the jclass arg is
    // resolved via the calling thread's classloader (here, JNI_OnLoad runs
    // in the System.loadLibrary caller's context = bootstrap, but FindClass
    // here uses the system classloader which can find the AppSpawnXInit
    // class via its full name once the runtime jar is on the classpath).
    //
    // If FindClass cannot find AppSpawnXInit yet (it gets loaded later via
    // PathClassLoader), this registration silently no-ops; appLog falls
    // back to System.err.  Best-effort: try, log result.
    {
        jclass appSpawnXInitClass = env->FindClass("com/android/internal/os/AppSpawnXInit");
        if (env->ExceptionCheck()) {
            env->ExceptionClear();
        }
        if (appSpawnXInitClass != nullptr) {
            JNINativeMethod m = {
                const_cast<char*>("nativeHiLog"),
                const_cast<char*>("(Ljava/lang/String;Ljava/lang/String;)V"),
                reinterpret_cast<void*>(Java_com_android_internal_os_AppSpawnXInit_nativeHiLog)
            };
            jint rc = env->RegisterNatives(appSpawnXInitClass, &m, 1);
            if (rc == JNI_OK) {
                ZYG_LOG_INFO("[B34.1] RegisterNatives(AppSpawnXInit.nativeHiLog) OK");
            } else {
                ZYG_LOG_ERR("[B34.1] RegisterNatives(AppSpawnXInit.nativeHiLog) failed rc=%{public}d", rc);
                if (env->ExceptionCheck()) {
                    env->ExceptionDescribe();
                    env->ExceptionClear();
                }
            }

            env->DeleteLocalRef(appSpawnXInitClass);
        } else {
            ZYG_LOG_ERR("[B34.1] FindClass(AppSpawnXInit) returned null at JNI_OnLoad — appLog will fall back to stderr until later resolution");
        }
    }

    // G2.14e — RegisterNatives for android.view.InputEventReceiver. Done in
    // bridge.so's JNI_OnLoad (which runs in child process) instead of
    // liboh_android_runtime's startReg (which runs in parent appspawn-x and
    // somehow fails to take effect for IER even though InputChannel works).
    {
        if (env->ExceptionCheck()) env->ExceptionClear();
        jclass ierClass = env->FindClass("android/view/InputEventReceiver");
        if (env->ExceptionCheck()) env->ExceptionClear();
        if (ierClass != nullptr) {
            JNINativeMethod ierMethods[] = {
                { const_cast<char*>("nativeInit"),
                  const_cast<char*>("(Ljava/lang/ref/WeakReference;Landroid/view/InputChannel;Landroid/os/MessageQueue;)J"),
                  reinterpret_cast<void*>(Java_android_view_InputEventReceiver_nativeInit) },
                { const_cast<char*>("nativeDispose"), const_cast<char*>("(J)V"),
                  reinterpret_cast<void*>(Java_android_view_InputEventReceiver_nativeDispose) },
                { const_cast<char*>("nativeFinishInputEvent"), const_cast<char*>("(JIZ)V"),
                  reinterpret_cast<void*>(Java_android_view_InputEventReceiver_nativeFinishInputEvent) },
                { const_cast<char*>("nativeReportTimeline"), const_cast<char*>("(JIJJ)V"),
                  reinterpret_cast<void*>(Java_android_view_InputEventReceiver_nativeReportTimeline) },
                { const_cast<char*>("nativeConsumeBatchedInputEvents"), const_cast<char*>("(JJ)Z"),
                  reinterpret_cast<void*>(Java_android_view_InputEventReceiver_nativeConsumeBatchedInputEvents) },
                { const_cast<char*>("nativeDump"),
                  const_cast<char*>("(JLjava/lang/String;)Ljava/lang/String;"),
                  reinterpret_cast<void*>(Java_android_view_InputEventReceiver_nativeDump) },
            };
            jint rc = env->RegisterNatives(ierClass, ierMethods, 6);
            if (rc == JNI_OK) {
                ZYG_LOG_INFO("[G2.14e] RegisterNatives(InputEventReceiver) OK 6 methods");
            } else {
                ZYG_LOG_ERR("[G2.14e] RegisterNatives(InputEventReceiver) failed rc=%{public}d", rc);
                if (env->ExceptionCheck()) {
                    env->ExceptionDescribe();
                    env->ExceptionClear();
                }
            }
            env->DeleteLocalRef(ierClass);
        } else {
            ZYG_LOG_ERR("[G2.14e] FindClass(InputEventReceiver) returned null in JNI_OnLoad");
        }
    }

    return JNI_VERSION_1_6;
}

// ==================== OHEnvironment JNI Methods ====================

/*
 * Class:     adapter_OHEnvironment
 * Method:    nativeInitialize
 */
JNIEXPORT jboolean JNICALL
Java_adapter_OHEnvironment_nativeInitialize(JNIEnv* env, jclass clazz) {
    LOGI("nativeInitialize()");

    jboolean result = JNI_FALSE;
    std::call_once(g_initFlag, [&]() {
        AdapterBridge& bridge = AdapterBridge::getInstance();

        if (!bridge.initialize(env)) {
            LOGE("AdapterBridge initialization failed");
            return;
        }

        // Initialize OH IPC framework — SetCallingIdentity takes std::string&
        // (non-const ref) in V7, so we can't pass a string literal directly.
        std::string emptyId;
        OHOS::IPCSkeleton::SetCallingIdentity(emptyId);
        LOGI("OH IPC framework initialized");

        // Initialize input event bridge — fetch JavaVM from JNIEnv since `vm`
        // is only in scope inside JNI_OnLoad.
        JavaVM* jvm = nullptr;
        env->GetJavaVM(&jvm);
        OHInputBridge::getInstance().setJavaVM(jvm);
        LOGI("Input event bridge initialized");

        // Initialize CommonEvent JNI callbacks
        if (!initCommonEventJNI(env)) {
            LOGE("CommonEvent JNI initialization failed (non-fatal)");
        }

        g_initialized = true;
        result = JNI_TRUE;
    });

    if (g_initialized) {
        result = JNI_TRUE;
    }

    return result;
}

/*
 * Class:     adapter_OHEnvironment
 * Method:    nativeConnectToOHServices
 */
JNIEXPORT jboolean JNICALL
Java_adapter_OHEnvironment_nativeConnectToOHServices(JNIEnv* env, jclass clazz) {
    LOGI("nativeConnectToOHServices()");

    // Connect to all OH system services
    bool abilityMgrOk = OHAbilityManagerClient::getInstance().connect();
    bool appMgrOk = OHAppMgrClient::getInstance().connect();
    bool windowMgrOk = OHWindowManagerClient::getInstance().connect();

    // Register callback stubs
    JavaVM* jvm = AdapterBridge::getInstance().getJavaVM();
    bool callbackOk = OHCallbackHandler::getInstance().registerCallbacks();

    LOGI("Service connections: AbilityMgr=%d, AppMgr=%d, WindowMgr=%d, Callbacks=%d",
         abilityMgrOk, appMgrOk, windowMgrOk, callbackOk);

    return (jboolean)(abilityMgrOk && appMgrOk);
}

/*
 * Class:     adapter_OHEnvironment
 * Method:    nativeAttachApplication
 *
 * New signature (2026-04-27): takes the IApplicationThread.Stub jobject so
 * the native side can construct an AppSchedulerAdapter wrapping it.  The
 * AppSchedulerAdapter is then registered with OH AppMgr via
 * IAppMgr.AttachApplication(sptr<IRemoteObject>) — this is the actual IPC
 * delivery point.  Returning false here means the IPC was NOT sent; callers
 * (Java side) must check the return and not silently log "OK".
 */
JNIEXPORT jboolean JNICALL
Java_adapter_OHEnvironment_nativeAttachApplication(
        JNIEnv* env, jclass clazz, jobject thread, jint pid, jint uid,
        jstring packageName) {
    const char* pkgName = env->GetStringUTFChars(packageName, nullptr);
    LOGI("nativeAttachApplication: pid=%d, uid=%d, pkg=%s, thread=%p",
         pid, uid, pkgName, thread);

    JavaVM* jvm = nullptr;
    env->GetJavaVM(&jvm);
    bool result = OHAppMgrClient::getInstance().attachApplication(
            jvm, thread, pid, uid, pkgName);

    env->ReleaseStringUTFChars(packageName, pkgName);
    return (jboolean)result;
}

/*
 * Class:     adapter_OHEnvironment
 * Method:    nativeNotifyAppState
 */
JNIEXPORT void JNICALL
Java_adapter_OHEnvironment_nativeNotifyAppState(
        JNIEnv* env, jclass clazz, jint state) {
    LOGI("nativeNotifyAppState: state=%d", state);
    OHAppMgrClient::getInstance().notifyAppState(state);
}

/*
 * Class:     adapter_OHEnvironment
 * Method:    nativeShutdown
 */
JNIEXPORT void JNICALL
Java_adapter_OHEnvironment_nativeShutdown(JNIEnv* env, jclass clazz) {
    LOGI("nativeShutdown()");
    OHCallbackHandler::getInstance().unregisterCallbacks();
    OHWindowManagerClient::getInstance().disconnect();
    OHAbilityManagerClient::getInstance().disconnect();
    OHAppMgrClient::getInstance().disconnect();
    AdapterBridge::getInstance().shutdown();
}

/*
 * Class:     adapter_OHEnvironment
 * Method:    nativeIsOHEnvironment
 */
JNIEXPORT jboolean JNICALL
Java_adapter_OHEnvironment_nativeIsOHEnvironment(JNIEnv* env, jclass clazz) {
    // Returns true when running in an OH-compatible environment
    return (jboolean)g_initialized;
}

// ==================== Correctly-Packaged JNI Aliases ====================
// The Java OHEnvironment lives in package "adapter.core" (not "adapter"),
// so ART's name-mangler looks for Java_adapter_core_OHEnvironment_*.  The
// historical exports above predate the package rename and would otherwise
// surface as UnsatisfiedLinkError at first call.  Each alias delegates to
// the existing implementation; the old short-package symbols are kept as
// no-cost legacy fallbacks.

JNIEXPORT jboolean JNICALL
Java_adapter_core_OHEnvironment_nativeInitialize(JNIEnv* env, jclass clazz) {
    return Java_adapter_OHEnvironment_nativeInitialize(env, clazz);
}

JNIEXPORT jboolean JNICALL
Java_adapter_core_OHEnvironment_nativeConnectToOHServices(JNIEnv* env, jclass clazz) {
    return Java_adapter_OHEnvironment_nativeConnectToOHServices(env, clazz);
}

JNIEXPORT jboolean JNICALL
Java_adapter_core_OHEnvironment_nativeAttachApplication(
        JNIEnv* env, jclass clazz, jobject thread, jint pid, jint uid,
        jstring packageName) {
    return Java_adapter_OHEnvironment_nativeAttachApplication(
            env, clazz, thread, pid, uid, packageName);
}

JNIEXPORT void JNICALL
Java_adapter_core_OHEnvironment_nativeNotifyAppState(
        JNIEnv* env, jclass clazz, jint state) {
    Java_adapter_OHEnvironment_nativeNotifyAppState(env, clazz, state);
}

JNIEXPORT void JNICALL
Java_adapter_core_OHEnvironment_nativeShutdown(JNIEnv* env, jclass clazz) {
    Java_adapter_OHEnvironment_nativeShutdown(env, clazz);
}

JNIEXPORT jboolean JNICALL
Java_adapter_core_OHEnvironment_nativeIsOHEnvironment(JNIEnv* env, jclass clazz) {
    return Java_adapter_OHEnvironment_nativeIsOHEnvironment(env, clazz);
}

// 2026-04-29 B.32: nativeHiLog — direct HiLogPrint bridge for Java side.
//
// Android Log → liblog.so → hilog bridge constructor path is broken in
// spawned child (B.31 verified: zero D002000 entries in child hilog).  This
// JNI native bypasses all of that and calls HiLogPrint directly.  Lets us
// see Java init checkpoints without depending on the liblog.so → hilog
// bridge state surviving fork.
//
// Filter via: hdc shell hilog | grep "AppSpawnXJava"   (or whatever tag)
extern "C" int HiLogPrint(int type, int level, unsigned int domain,
                          const char* tag, const char* fmt, ...)
    __attribute__((__format__(printf, 5, 6)));

#define B32_LOG_TYPE_CORE  3      // OH LOG_CORE
#define B32_LOG_INFO       4      // OH LOG_INFO
#define B32_LOG_DOMAIN     0xD000F00u  // matches APPSPAWNX_LOG_DOMAIN in spawn_msg.h

// Class:  com.android.internal.os.AppSpawnXInit (non-BCP runtime jar — see
// rationale in AppSpawnXInit.java appLog javadoc).  Adding the native to
// a non-BCP class avoids boot image rebuild.
JNIEXPORT void JNICALL
Java_com_android_internal_os_AppSpawnXInit_nativeHiLog(
        JNIEnv* env, jclass /*clazz*/, jstring jtag, jstring jmsg) {
    const char* tag = jtag ? env->GetStringUTFChars(jtag, nullptr) : "Java";
    const char* msg = jmsg ? env->GetStringUTFChars(jmsg, nullptr) : "(null)";
    HiLogPrint(B32_LOG_TYPE_CORE, B32_LOG_INFO, B32_LOG_DOMAIN,
               tag ? tag : "Java", "%{public}s", msg ? msg : "(null)");
    if (jtag && tag) env->ReleaseStringUTFChars(jtag, tag);
    if (jmsg && msg) env->ReleaseStringUTFChars(jmsg, msg);
}

// ==================== Per-Adapter JNI Methods ====================

/*
 * Class:     adapter_bridge_ActivityManagerAdapter
 * Method:    nativeGetOHAbilityManagerService
 */
JNIEXPORT jlong JNICALL
Java_adapter_bridge_ActivityManagerAdapter_nativeGetOHAbilityManagerService(
        JNIEnv* env, jclass clazz) {
    return (jlong)&OHAbilityManagerClient::getInstance();
}

/*
 * Class:     adapter_bridge_ActivityTaskManagerAdapter
 * Method:    nativeGetOHAbilityManagerService
 */
JNIEXPORT jlong JNICALL
Java_adapter_bridge_ActivityTaskManagerAdapter_nativeGetOHAbilityManagerService(
        JNIEnv* env, jclass clazz) {
    return (jlong)&OHAbilityManagerClient::getInstance();
}

/*
 * Class:     adapter_bridge_ActivityManagerAdapter
 * Method:    nativeStartAbility
 */
JNIEXPORT jint JNICALL
Java_adapter_bridge_ActivityManagerAdapter_nativeStartAbility(
        JNIEnv* env, jclass clazz,
        jstring bundleName, jstring abilityName,
        jstring action, jstring uri, jstring extraJson) {
    return bridgeStartAbility(env, bundleName, abilityName, action, uri, extraJson);
}

/*
 * Class:     adapter_bridge_ActivityTaskManagerAdapter
 * Method:    nativeStartAbility
 */
JNIEXPORT jint JNICALL
Java_adapter_bridge_ActivityTaskManagerAdapter_nativeStartAbility(
        JNIEnv* env, jclass clazz,
        jstring bundleName, jstring abilityName,
        jstring action, jstring uri, jstring extraJson) {
    return bridgeStartAbility(env, bundleName, abilityName, action, uri, extraJson);
}

// Legacy symbol for adapter_bridge_ package prefix (JNI RegisterNatives fallback)
JNIEXPORT jint JNICALL
Java_adapter_bridge_ActivityManagerAdapter_nativeConnectAbility(
        JNIEnv* env, jclass clazz,
        jstring bundleName, jstring abilityName, jint connectionId) {
    return bridgeConnectAbility(env, bundleName, abilityName, connectionId);
}


// Legacy symbol
JNIEXPORT jint JNICALL
Java_adapter_bridge_ActivityManagerAdapter_nativeDisconnectAbility(
        JNIEnv* env, jclass clazz, jint connectionId) {
    return bridgeDisconnectAbility(connectionId);
}


// Legacy symbol
JNIEXPORT jint JNICALL
Java_adapter_bridge_ActivityManagerAdapter_nativeStopServiceAbility(
        JNIEnv* env, jclass clazz,
        jstring bundleName, jstring abilityName) {
    return bridgeStopServiceAbility(env, bundleName, abilityName);
}

/*
 * Class:     adapter_bridge_WindowManagerAdapter
 * Method:    nativeGetOHWindowManagerService
 */
JNIEXPORT jlong JNICALL
Java_adapter_bridge_WindowManagerAdapter_nativeGetOHWindowManagerService(
        JNIEnv* env, jclass clazz) {
    return (jlong)&OHWindowManagerClient::getInstance();
}

/*
 * Class:     adapter_bridge_WindowSessionAdapter
 * Method:    nativeGetOHSessionService
 */
JNIEXPORT jlong JNICALL
Java_adapter_window_WindowSessionAdapter_nativeGetOHSessionService(
        JNIEnv* env, jclass clazz) {
    return (jlong)&OHWindowManagerClient::getInstance();
}

// ==================== Window Session JNI Methods ====================

/*
 * Class:     adapter_bridge_WindowSessionAdapter
 * Method:    nativeCreateSession
 *
 * Creates an OH window session and returns the session info as an int array:
 *   [0] = sessionId, [1] = surfaceNodeId, [2] = displayId, [3] = width, [4] = height
 *
 * Authoritative spec: doc/window_manager_ipc_adapter_design.html §3.1
 *
 * The bundleName / abilityName / moduleName triplet is required so OH SCB
 * routes window decoration / theme correctly (§3.1.4.2). The Java side
 * obtains them from ActivityThread.currentPackageName() / currentActivity().
 */
JNIEXPORT jintArray JNICALL
Java_adapter_window_WindowSessionAdapter_nativeCreateSession(
        JNIEnv* env, jclass clazz,
        jobject androidWindow,
        jstring bundleNameJ, jstring abilityNameJ, jstring moduleNameJ,
        jstring windowNameJ,
        jint androidWindowType, jint displayId,
        jint requestedWidth, jint requestedHeight,
        jlong ohTokenAddrJ) {

    auto getStr = [&](jstring js) -> std::string {
        if (js == nullptr) return std::string();
        const char* c = env->GetStringUTFChars(js, nullptr);
        std::string s = c ? c : "";
        if (c) env->ReleaseStringUTFChars(js, c);
        return s;
    };

    std::string bundleName  = getStr(bundleNameJ);
    std::string abilityName = getStr(abilityNameJ);
    std::string moduleName  = getStr(moduleNameJ);
    std::string windowName  = getStr(windowNameJ);
    if (moduleName.empty()) moduleName = "entry";       // HAP default
    if (abilityName.empty()) abilityName = "MainAbility";
    if (windowName.empty()) windowName = "AndroidWindow";

    JavaVM* jvm = AdapterBridge::getInstance().getJavaVM();
    OHWindowSession session = OHWindowManagerClient::getInstance().createSession(
        jvm, androidWindow,
        bundleName, abilityName, moduleName, windowName,
        androidWindowType, displayId,
        requestedWidth, requestedHeight,
        static_cast<uint64_t>(ohTokenAddrJ));

    // §3.1.5.6.2 — int[6]: index 5 carries OH WSError for Java-side
    // semantic mapping into ADD_* codes.
    jintArray result = env->NewIntArray(6);
    jint info[6] = {
        session.sessionId,
        session.surfaceNodeId,
        session.displayId,
        session.width,
        session.height,
        session.wsErr,
    };
    env->SetIntArrayRegion(result, 0, 6, info);
    return result;
}

/*
 * Class:     adapter_bridge_WindowSessionAdapter
 * Method:    nativeUpdateSessionRect
 */
JNIEXPORT jint JNICALL
Java_adapter_window_WindowSessionAdapter_nativeUpdateSessionRect(
        JNIEnv* env, jclass clazz,
        jint sessionId, jint x, jint y, jint width, jint height) {
    return OHWindowManagerClient::getInstance().updateSessionRect(
        sessionId, x, y, width, height);
}

/*
 * Class:     adapter_bridge_WindowSessionAdapter
 * Method:    nativeNotifyDrawingCompleted
 */
JNIEXPORT jint JNICALL
Java_adapter_window_WindowSessionAdapter_nativeNotifyDrawingCompleted(
        JNIEnv* env, jclass clazz, jint sessionId) {
    return OHWindowManagerClient::getInstance().notifyDrawingCompleted(sessionId);
}

/*
 * Class:     adapter_bridge_WindowSessionAdapter
 * Method:    nativeDestroySession
 */
JNIEXPORT void JNICALL
Java_adapter_window_WindowSessionAdapter_nativeDestroySession(
        JNIEnv* env, jclass clazz, jint sessionId) {
    OHWindowManagerClient::getInstance().destroySession(sessionId);
}


/*
 * Class:     adapter_bridge_WindowSessionAdapter
 * Method:    nativeGetSurfaceNodeId
 */
JNIEXPORT jlong JNICALL
Java_adapter_window_WindowSessionAdapter_nativeGetSurfaceNodeId(
        JNIEnv* env, jclass clazz, jint sessionId) {
    return OHWindowManagerClient::getInstance().getSurfaceNodeId(sessionId);
}

// 2026-05-02 G2.14r: nativeAttachSessionToSc JNI binding REMOVED.
// The corresponding Java method was reverted (avoiding BCP boot-image
// rebuild) and replaced with a process-global last-session fallback in
// liboh_android_runtime.so + OHWindowManagerClient.  Keeping this binding
// here would force liboh_adapter_bridge.so to carry an unresolved
// reference to oh_sc_attach_session (which lives in liboh_android_runtime.so),
// causing dlopen of bridge.so to fail at OHEnvironment static init →
// NoClassDefFoundError → AMS adapter not installed → ScheduleLaunchAbility
// LIFECYCLE_TIMEOUT.  Resolution: drop the JNI export entirely.

// ==================== Input Event Bridge JNI Methods ====================

/*
 * Class:     adapter_InputEventBridge
 * Method:    nativeRegisterInputChannel
 *
 * Registers the server-side InputChannel fd with the native InputPublisher.
 * The fd is extracted from the Java InputChannel object.
 */
JNIEXPORT void JNICALL
Java_adapter_window_InputEventBridge_nativeRegisterInputChannel(
        JNIEnv* env, jclass clazz, jint sessionId, jobject inputChannel) {

    // G2.14d — InputChannel.getFd() does not exist on AOSP 14; the class was
    // refactored in API 33 to use native pointer + IBinder token instead of
    // a directly-exposed fd accessor. Failed reflection lookups must clear
    // the pending JNI exception, otherwise it bubbles back to Java the next
    // time we cross the JNI boundary (was the actual NCDFE root cause for
    // WindowCallbackBridge — same anti-pattern).
    //
    // For HelloWorld P1 baseline, input events flow via OH MMI → adapter
    // input bridge directly (not through Android InputChannel fd). So this
    // registration is a no-op on this code path. Kept as a stub so the Java
    // side's nativeRegisterInputChannel callsite still binds.
    jclass channelClass = env->GetObjectClass(inputChannel);
    jmethodID getFdMethod = env->GetMethodID(channelClass, "getFd", "()I");
    if (getFdMethod == nullptr) {
        if (env->ExceptionCheck()) env->ExceptionClear();
        LOGI("nativeRegisterInputChannel: session=%d (no-op, getFd not available)",
             sessionId);
        env->DeleteLocalRef(channelClass);
        return;
    }

    int fd = env->CallIntMethod(inputChannel, getFdMethod);
    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        env->DeleteLocalRef(channelClass);
        return;
    }
    LOGI("nativeRegisterInputChannel: session=%d, fd=%d", sessionId, fd);
    env->DeleteLocalRef(channelClass);

    int dupFd = dup(fd);
    if (dupFd < 0) {
        LOGE("Failed to dup InputChannel fd: %s", strerror(errno));
        return;
    }

    OHInputBridge::getInstance().registerInputChannel(sessionId, dupFd);
}

/*
 * Class:     adapter_InputEventBridge
 * Method:    nativeUnregisterInputChannel
 */
JNIEXPORT void JNICALL
Java_adapter_window_InputEventBridge_nativeUnregisterInputChannel(
        JNIEnv* env, jclass clazz, jint sessionId) {
    LOGI("nativeUnregisterInputChannel: session=%d", sessionId);
    OHInputBridge::getInstance().unregisterInputChannel(sessionId);
}

/*
 * Class:     adapter_bridge_WindowSessionAdapter
 * Method:    nativeInjectTouchEvent
 *
 * Injects a single-pointer touch event into the Android InputChannel
 * for the specified session. Used for testing and for forwarding
 * OH-origin touch events.
 */
JNIEXPORT jint JNICALL
Java_adapter_window_WindowSessionAdapter_nativeInjectTouchEvent(
        JNIEnv* env, jclass clazz,
        jint sessionId, jint action, jfloat x, jfloat y,
        jlong downTime, jlong eventTime) {
    return OHInputBridge::getInstance().injectTouchEvent(
        sessionId, action, x, y, downTime, eventTime);
}

// ==================== Mission / Task Management JNI Methods ====================

/*
 * Class:     adapter_bridge_ActivityTaskManagerAdapter
 * Method:    nativeStartAbilityInMission
 *
 * Starts a new Ability within an existing Mission (pushes onto Ability stack).
 */
JNIEXPORT jint JNICALL
Java_adapter_bridge_ActivityTaskManagerAdapter_nativeStartAbilityInMission(
        JNIEnv* env, jclass clazz,
        jstring bundleName, jstring abilityName,
        jstring action, jstring uri, jstring extraJson,
        jint missionId) {
    std::string bundle = jstringToString(env, bundleName);
    std::string ability = jstringToString(env, abilityName);
    std::string act = jstringToString(env, action);
    std::string u = jstringToString(env, uri);
    std::string extra = jstringToString(env, extraJson);

    LOGI("nativeStartAbilityInMission: bundle=%s, ability=%s, missionId=%d",
         bundle.c_str(), ability.c_str(), missionId);

    WantParams want;
    want.bundleName = bundle;
    want.abilityName = ability;
    want.action = act;
    want.uri = u;
    want.parametersJson = extra;

    return OHAbilityManagerClient::getInstance().startAbilityInMission(want, missionId);
}

/*
 * Class:     adapter_bridge_ActivityTaskManagerAdapter
 * Method:    nativeCleanMission
 *
 * Removes a Mission and all its stacked Abilities.
 */
JNIEXPORT jint JNICALL
Java_adapter_bridge_ActivityTaskManagerAdapter_nativeCleanMission(
        JNIEnv* env, jclass clazz, jint missionId) {
    return OHAbilityManagerClient::getInstance().cleanMission(missionId);
}

/*
 * Class:     adapter_bridge_ActivityTaskManagerAdapter
 * Method:    nativeMoveMissionToFront
 *
 * Moves a Mission's top Ability to the foreground.
 */
JNIEXPORT jint JNICALL
Java_adapter_bridge_ActivityTaskManagerAdapter_nativeMoveMissionToFront(
        JNIEnv* env, jclass clazz, jint missionId) {
    return OHAbilityManagerClient::getInstance().moveMissionToFront(missionId);
}

/*
 * Class:     adapter_bridge_ActivityTaskManagerAdapter
 * Method:    nativeIsTopAbility
 *
 * Checks if the top Ability in a Mission matches the given name.
 */
JNIEXPORT jboolean JNICALL
Java_adapter_bridge_ActivityTaskManagerAdapter_nativeIsTopAbility(
        JNIEnv* env, jclass clazz,
        jint missionId, jstring abilityName) {
    const char* name = env->GetStringUTFChars(abilityName, nullptr);
    bool result = OHAbilityManagerClient::getInstance().isTopAbility(missionId, name);
    env->ReleaseStringUTFChars(abilityName, name);
    return (jboolean)result;
}

/*
 * Class:     adapter_bridge_ActivityTaskManagerAdapter
 * Method:    nativeClearAbilitiesAbove
 *
 * Clears all Abilities above the named one in a Mission's stack (FLAG_ACTIVITY_CLEAR_TOP).
 */
JNIEXPORT jint JNICALL
Java_adapter_bridge_ActivityTaskManagerAdapter_nativeClearAbilitiesAbove(
        JNIEnv* env, jclass clazz,
        jint missionId, jstring abilityName) {
    std::string name = jstringToString(env, abilityName);
    return OHAbilityManagerClient::getInstance().clearAbilitiesAbove(missionId, name);
}

/*
 * Class:     adapter_bridge_ActivityTaskManagerAdapter
 * Method:    nativeGetMissionIdForBundle
 *
 * Queries OH MissionInfos to find the Mission ID for a given bundle name.
 */
JNIEXPORT jint JNICALL
Java_adapter_bridge_ActivityTaskManagerAdapter_nativeGetMissionIdForBundle(
        JNIEnv* env, jclass clazz,
        jstring bundleName) {
    std::string bundle = jstringToString(env, bundleName);
    return OHAbilityManagerClient::getInstance().getMissionIdForBundle(bundle);
}

// ==================== Surface Bridge JNI Methods ====================

/*
 * Class:     adapter_bridge_WindowSessionAdapter
 * Method:    nativeCreateOHSurface
 *
 * Creates OH RSSurfaceNode + RSUIDirector + Surface for a window session.
 */
JNIEXPORT jboolean JNICALL
Java_adapter_window_WindowSessionAdapter_nativeCreateOHSurface(
        JNIEnv* env, jclass clazz,
        jint sessionId, jstring windowName,
        jint width, jint height, jint format) {
    const char* name = env->GetStringUTFChars(windowName, nullptr);
    bool result = OHSurfaceBridge::getInstance().createSurface(
        sessionId, name, width, height, format);
    env->ReleaseStringUTFChars(windowName, name);
    return (jboolean)result;
}

/*
 * Class:     adapter_bridge_WindowSessionAdapter
 * Method:    nativeGetSurfaceHandle
 *
 * Returns opaque native pointer to OHGraphicBufferProducer for Android SurfaceControl.
 */
JNIEXPORT jlong JNICALL
Java_adapter_window_WindowSessionAdapter_nativeGetSurfaceHandle(
        JNIEnv* env, jclass clazz,
        jint sessionId, jint width, jint height, jint format) {
    return OHSurfaceBridge::getInstance().getSurfaceHandle(
        sessionId, width, height, format);
}

/*
 * Class:     adapter_bridge_WindowSessionAdapter
 * Method:    nativeNotifySurfaceDrawingCompleted
 *
 * Flushes RSUIDirector::SendMessages() and RSTransaction for a session.
 */
JNIEXPORT void JNICALL
Java_adapter_window_WindowSessionAdapter_nativeNotifySurfaceDrawingCompleted(
        JNIEnv* env, jclass clazz, jint sessionId) {
    OHSurfaceBridge::getInstance().notifyDrawingCompleted(sessionId);
}

/*
 * Class:     adapter_bridge_WindowSessionAdapter
 * Method:    nativeUpdateSurfaceSize
 *
 * Updates RSSurfaceNode bounds when window is resized.
 */
JNIEXPORT void JNICALL
Java_adapter_window_WindowSessionAdapter_nativeUpdateSurfaceSize(
        JNIEnv* env, jclass clazz,
        jint sessionId, jint width, jint height) {
    OHSurfaceBridge::getInstance().updateSurfaceSize(sessionId, width, height);
}

/*
 * Class:     adapter_bridge_WindowSessionAdapter
 * Method:    nativeDestroyOHSurface
 *
 * Releases all OH surface resources for a session.
 */
JNIEXPORT void JNICALL
Java_adapter_window_WindowSessionAdapter_nativeDestroyOHSurface(
        JNIEnv* env, jclass clazz, jint sessionId) {
    OHSurfaceBridge::getInstance().destroySurface(sessionId);
}

/*
 * Class:     adapter_bridge_WindowSessionAdapter
 * Method:    nativeDequeueBuffer
 *
 * Dequeues a buffer from OH Surface for rendering.
 * Returns int[]: [slot, fenceFd, dmabufFd, width, height, stride]
 */
JNIEXPORT jintArray JNICALL
Java_adapter_window_WindowSessionAdapter_nativeDequeueBuffer(
        JNIEnv* env, jclass clazz,
        jlong producerHandle, jint width, jint height,
        jint format, jlong usage) {
    auto* producer = reinterpret_cast<OHGraphicBufferProducer*>(producerHandle);
    if (!producer) {
        LOGE("nativeDequeueBuffer: null producer handle");
        return nullptr;
    }

    int slot = -1;
    int fenceFd = -1;
    int ret = producer->dequeueBuffer(&slot, &fenceFd, width, height, format, usage);
    if (ret != 0) {
        LOGE("nativeDequeueBuffer: dequeueBuffer failed (ret=%d)", ret);
        return nullptr;
    }

    // Get buffer info
    int32_t bufWidth = 0, bufHeight = 0, stride = 0, bufFormat = 0;
    producer->getBufferInfo(slot, &bufWidth, &bufHeight, &stride, &bufFormat);

    int dmabufFd = producer->getBufferFd(slot);

    jintArray result = env->NewIntArray(6);
    jint info[6] = { slot, fenceFd, dmabufFd, bufWidth, bufHeight, stride };
    env->SetIntArrayRegion(result, 0, 6, info);
    return result;
}

/*
 * Class:     adapter_bridge_WindowSessionAdapter
 * Method:    nativeQueueBuffer
 *
 * Queues a rendered buffer to OH Surface for composition.
 */
JNIEXPORT jint JNICALL
Java_adapter_window_WindowSessionAdapter_nativeQueueBuffer(
        JNIEnv* env, jclass clazz,
        jlong producerHandle, jint slot, jint fenceFd,
        jlong timestamp, jint cropLeft, jint cropTop,
        jint cropRight, jint cropBottom) {
    auto* producer = reinterpret_cast<OHGraphicBufferProducer*>(producerHandle);
    if (!producer) {
        LOGE("nativeQueueBuffer: null producer handle");
        return -1;
    }

    return producer->queueBuffer(slot, fenceFd, timestamp,
                                  cropLeft, cropTop, cropRight, cropBottom);
}

/*
 * Class:     adapter_bridge_WindowSessionAdapter
 * Method:    nativeCancelBuffer
 *
 * Cancels a previously dequeued buffer.
 */
JNIEXPORT jint JNICALL
Java_adapter_window_WindowSessionAdapter_nativeCancelBuffer(
        JNIEnv* env, jclass clazz,
        jlong producerHandle, jint slot, jint fenceFd) {
    auto* producer = reinterpret_cast<OHGraphicBufferProducer*>(producerHandle);
    if (!producer) {
        LOGE("nativeCancelBuffer: null producer handle");
        return -1;
    }

    return producer->cancelBuffer(slot, fenceFd);
}

// ==================== Broadcast / CommonEvent JNI Methods ====================

/*
 * Class:     adapter_bridge_ActivityManagerAdapter
 * Method:    nativeSubscribeCommonEvent
 *
 * Subscribes to OH CommonEvents matching the given event names.
 */
JNIEXPORT jint JNICALL
Java_adapter_bridge_ActivityManagerAdapter_nativeSubscribeCommonEvent(
        JNIEnv* env, jclass clazz,
        jint subscriptionId, jobjectArray ohEventNames,
        jint priority, jstring permission) {

    // Extract event names from Java string array
    std::vector<std::string> events;
    if (ohEventNames) {
        int count = env->GetArrayLength(ohEventNames);
        for (int i = 0; i < count; i++) {
            jstring jstr = (jstring)env->GetObjectArrayElement(ohEventNames, i);
            events.push_back(jstringToString(env, jstr));
            if (jstr) env->DeleteLocalRef(jstr);
        }
    }

    std::string perm = jstringToString(env, permission);

    return OHCommonEventClient::getInstance().subscribe(subscriptionId, events, priority, perm);
}

/*
 * Class:     adapter_bridge_ActivityManagerAdapter
 * Method:    nativeUnsubscribeCommonEvent
 *
 * Unsubscribes from OH CommonEvents.
 */
JNIEXPORT jint JNICALL
Java_adapter_bridge_ActivityManagerAdapter_nativeUnsubscribeCommonEvent(
        JNIEnv* env, jclass clazz, jint subscriptionId) {
    return OHCommonEventClient::getInstance().unsubscribe(subscriptionId);
}

/*
 * Class:     adapter_bridge_ActivityManagerAdapter
 * Method:    nativePublishCommonEvent
 *
 * Publishes an OH CommonEvent.
 */
JNIEXPORT jint JNICALL
Java_adapter_bridge_ActivityManagerAdapter_nativePublishCommonEvent(
        JNIEnv* env, jclass clazz,
        jstring ohAction, jstring extrasJson, jstring uri,
        jint code, jstring data,
        jboolean ordered, jboolean sticky,
        jobjectArray subscriberPermissions) {

    std::string action = jstringToString(env, ohAction);
    std::string extras = jstringToString(env, extrasJson);
    std::string uriStr = jstringToString(env, uri);
    std::string dataStr = jstringToString(env, data);

    std::vector<std::string> permissions;
    if (subscriberPermissions) {
        int count = env->GetArrayLength(subscriberPermissions);
        for (int i = 0; i < count; i++) {
            jstring jstr = (jstring)env->GetObjectArrayElement(subscriberPermissions, i);
            permissions.push_back(jstringToString(env, jstr));
            if (jstr) env->DeleteLocalRef(jstr);
        }
    }

    return OHCommonEventClient::getInstance().publish(
            action, extras, uriStr, code, dataStr, ordered, sticky, permissions);
}

/*
 * Class:     adapter_bridge_ActivityManagerAdapter
 * Method:    nativeFinishCommonEvent
 *
 * Finishes processing an ordered CommonEvent (bridges Android finishReceiver).
 */
JNIEXPORT jint JNICALL
Java_adapter_bridge_ActivityManagerAdapter_nativeFinishCommonEvent(
        JNIEnv* env, jclass clazz,
        jint subscriptionId, jint resultCode, jstring resultData,
        jboolean abortEvent) {

    std::string data = jstringToString(env, resultData);
    return OHCommonEventClient::getInstance().finishReceiver(
            subscriptionId, resultCode, data, abortEvent);
}

/*
 * Class:     adapter_bridge_ActivityManagerAdapter
 * Method:    nativeGetStickyCommonEvent
 *
 * Queries a sticky CommonEvent.
 */
JNIEXPORT jstring JNICALL
Java_adapter_bridge_ActivityManagerAdapter_nativeGetStickyCommonEvent(
        JNIEnv* env, jclass clazz, jstring ohEventName) {

    std::string event = jstringToString(env, ohEventName);
    std::string result = OHCommonEventClient::getInstance().getStickyEvent(event);
    if (result.empty()) {
        return nullptr;
    }
    return env->NewStringUTF(result.c_str());
}

// ==================== Correctly-Packaged JNI Aliases ====================
//
// All Java adapter classes have moved from "adapter.bridge.*" /
// "adapter.client.*" to "adapter.activity.*" / "adapter.window.*", but the
// existing C++ symbols above retain the legacy package names.  ART's JNI
// name-mangler resolves a Java native method by searching for
// Java_<package>_<class>_<method>; the legacy symbols don't match the new
// packages, so every first call would surface as UnsatisfiedLinkError.
//
// Each alias below is a thin wrapper that forwards to the existing
// implementation.  Adds one extra branch per call (negligible) and keeps
// the legacy symbols in place so any prior boot image / oat cache that
// resolved against them keeps working.

extern "C" {

// ---------- adapter.activity.ActivityManagerAdapter ----------
JNIEXPORT jlong JNICALL
Java_adapter_activity_ActivityManagerAdapter_nativeGetOHAbilityManagerService(JNIEnv* e, jclass c) {
    return Java_adapter_bridge_ActivityManagerAdapter_nativeGetOHAbilityManagerService(e, c);
}
JNIEXPORT jint JNICALL
Java_adapter_activity_ActivityManagerAdapter_nativeStartAbility(
        JNIEnv* e, jclass c, jstring a, jstring b, jstring d, jstring f, jstring g) {
    return Java_adapter_bridge_ActivityManagerAdapter_nativeStartAbility(e, c, a, b, d, f, g);
}
JNIEXPORT jint JNICALL
Java_adapter_activity_ActivityManagerAdapter_nativeConnectAbility(
        JNIEnv* e, jclass c, jstring a, jstring b, jint d) {
    return Java_adapter_bridge_ActivityManagerAdapter_nativeConnectAbility(e, c, a, b, d);
}
JNIEXPORT jint JNICALL
Java_adapter_activity_ActivityManagerAdapter_nativeDisconnectAbility(
        JNIEnv* e, jclass c, jint id) {
    return Java_adapter_bridge_ActivityManagerAdapter_nativeDisconnectAbility(e, c, id);
}
JNIEXPORT jint JNICALL
Java_adapter_activity_ActivityManagerAdapter_nativeStopServiceAbility(
        JNIEnv* e, jclass c, jstring a, jstring b) {
    return Java_adapter_bridge_ActivityManagerAdapter_nativeStopServiceAbility(e, c, a, b);
}

// ---------- adapter.activity.ActivityTaskManagerAdapter ----------
JNIEXPORT jlong JNICALL
Java_adapter_activity_ActivityTaskManagerAdapter_nativeGetOHAbilityManagerService(JNIEnv* e, jclass c) {
    return Java_adapter_bridge_ActivityTaskManagerAdapter_nativeGetOHAbilityManagerService(e, c);
}
JNIEXPORT jint JNICALL
Java_adapter_activity_ActivityTaskManagerAdapter_nativeStartAbility(
        JNIEnv* e, jclass c, jstring a, jstring b, jstring d, jstring f, jstring g) {
    return Java_adapter_bridge_ActivityTaskManagerAdapter_nativeStartAbility(e, c, a, b, d, f, g);
}

// ---------- adapter.window.WindowManagerAdapter ----------
JNIEXPORT jlong JNICALL
Java_adapter_window_WindowManagerAdapter_nativeGetOHWindowManagerService(JNIEnv* e, jclass c) {
    return Java_adapter_bridge_WindowManagerAdapter_nativeGetOHWindowManagerService(e, c);
}

// ---------- adapter.window.WindowSessionAdapter ----------
// 2026-04-30 (P0 fix): all WindowSessionAdapter native exports now use the
// adapter_window_ binary name directly (renamed from adapter_bridge_).  The
// per-method forwarding wrappers that used to live here are gone — the real
// impls above (renamed in-place) match the Java JNI symbol resolver directly.

}  // extern "C"

}  // extern "C"
