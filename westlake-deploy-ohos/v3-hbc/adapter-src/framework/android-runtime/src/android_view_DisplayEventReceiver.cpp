// ============================================================================
// android_view_DisplayEventReceiver.cpp
//
// JNI binding for android.view.DisplayEventReceiver. Mirrors AOSP
// frameworks/base/core/jni/android_view_DisplayEventReceiver.cpp.
//
// Java side (frameworks/base/core/java/android/view/DisplayEventReceiver.java):
//   private static native long nativeInit(WeakReference<DER> receiver,
//                                          WeakReference<VsyncEventData> ved,
//                                          MessageQueue mq, int vsyncSource,
//                                          int eventRegistration, long layer);
//                                                                          // (...)J
//   private static native long nativeGetDisplayEventReceiverFinalizer();   // ()J
//   private static native void nativeScheduleVsync(long receiverPtr);      // (J)V
//   private static native VsyncEventData
//        nativeGetLatestVsyncEventData(long receiverPtr);                  // (J)Landroid/view/DisplayEventReceiver$VsyncEventData;
//
// AOSP impl drives vsync via libgui DisplayEventReceiver IPC to SurfaceFlinger.
// OH has no SurfaceFlinger; OH renderservice exposes vsync via OH_NativeVSync
// NDK API in libnative_vsync.so.
//
// 2026-05-06 — 方向 A 实施：接 OH 真 VSync API（取代之前的软件模拟方案）。
//   设计文档 §8.3 要求把 nativeScheduleVsync 桥到 OH_NativeVSync_RequestFrame。
//
//   旧实现（G2.14n 软件模拟，已注释）：
//     - 每次 nativeScheduleVsync 启动一个 std::thread，sleep 16ms 后 fire
//       dispatchVsync。fire-and-forget 模式，每次 attach + detach ART thread。
//     - 实测（2026-05-06）：反复 attach/detach 触发 ART CheckJNI fatal abort
//       "thread exited without DetachCurrentThread"，cppcrash thread name
//       <pre-initialize> + SIGABRT 在 activityResumed rc=0 后 76-101ms 触发。
//     - 方向 C 验证：DER_nativeScheduleVsync 改 no-op 后 helloworld 75s+ 不
//       SIGABRT，证实是 vsync attach/detach 链是真因。
//
//   新实现（方向 A）：
//     - OH_NativeVSync_Create 一次性创建 handle（per-DERState 实例）
//     - OH_NativeVSync_RequestFrame 注册回调，OH 在真 VBlank 时 callback
//     - callback 跑在 OH 自己长生命周期的 vsync thread 上（不会反复创建/销毁）
//     - 用 pthread_key destructor 在 OH 万一销毁该 thread 时自动 detach
//     - dlsym 动态解析 OH NativeVSync API（避免 link-time hard dep）
//
//   完全替代了原 std::thread + sleep 模拟方案。原代码用 #if 0 保留作记录。
//
// Code is adapted from the now-deprecated
// framework/surface/jni/android_view_surface_stubs.cpp (P13, 2026-04-11),
// with cleanup so it lives in the active liboh_android_runtime.so build path.
// ============================================================================

#include "AndroidRuntime.h"

#include <android/log.h>
#include <jni.h>
#include <atomic>
#include <chrono>
#include <cstdio>
#include <cstring>
#include <dlfcn.h>
#include <mutex>
#include <pthread.h>
#include <thread>

// 2026-05-06 — DER native logging via OH HiLogPrint (innerAPI) so it shows up
// in hilog (tag "OH_DER_VSync"). fprintf(stderr) goes to a redirected file in
// child processes; __android_log_print on OH may not be wired to hilog (the
// AOSP log shim isn't fully bridged for adapter .so).  HiLogPrint goes
// directly to OH hilog, same channel as oh_window_manager_client.cpp etc.
extern "C" int HiLogPrint(int type, int level, unsigned int domain,
                          const char* tag, const char* fmt, ...)
    __attribute__((__format__(printf, 5, 6)));
// LOG_CORE = 3, LOG_INFO = 4, LOG_WARN = 5, LOG_ERROR = 6
#define DER_LOG(...)  HiLogPrint(3, 4, 0xD000F00u, "OH_DER_VSync", __VA_ARGS__)
#define DER_LOGW(...) HiLogPrint(3, 5, 0xD000F00u, "OH_DER_VSync", __VA_ARGS__)
#define DER_LOGE(...) HiLogPrint(3, 6, 0xD000F00u, "OH_DER_VSync", __VA_ARGS__)

namespace android {
namespace {

// Cached JNI reflection info, populated lazily on first nativeInit call.
// (定义提前以便 detachJvmCallback 等下方函数可引用 — anonymous namespace 内
//  不能 extern 转发 internal-linkage 符号。)
JavaVM*    g_javaVm = nullptr;
jclass     g_derClass = nullptr;
jmethodID  g_dispatchVsyncMethod = nullptr;  // dispatchVsync(JJI)V

// ----------------------------------------------------------------------------
// 2026-05-06 方向 A: OH NativeVSync API forward declarations + dlsym wrappers.
//
// Header at: <native_vsync/native_vsync.h>
// Library:   libnative_vsync.so
//
// We resolve these symbols via dlsym (RTLD_NOW on libnative_vsync.so) instead
// of link-time dep so this .so doesn't have a hard runtime requirement on
// libnative_vsync.so being present (defensive against vendor shipping a
// stripped image).  Same pattern as G2.14r BBQ wiring + SC→RS routing.
// ----------------------------------------------------------------------------
struct OH_NativeVSync;
typedef void (*OH_NativeVSync_FrameCallback)(long long timestamp, void* data);

typedef OH_NativeVSync* (*Fn_OH_NativeVSync_Create)(const char* name, unsigned int length);
typedef void (*Fn_OH_NativeVSync_Destroy)(OH_NativeVSync* nv);
typedef int  (*Fn_OH_NativeVSync_RequestFrame)(OH_NativeVSync* nv,
                                                OH_NativeVSync_FrameCallback cb,
                                                void* data);

Fn_OH_NativeVSync_Create        g_oh_vsync_create  = nullptr;
Fn_OH_NativeVSync_Destroy       g_oh_vsync_destroy = nullptr;
Fn_OH_NativeVSync_RequestFrame  g_oh_vsync_request = nullptr;
std::once_flag                  g_oh_vsync_resolve_once;

void resolveOhVsyncApi() {
    std::call_once(g_oh_vsync_resolve_once, [](){
        // libnative_vsync.so 在 OH 设备上位于 /system/lib/platformsdk/，不在
        // default namespace 搜索路径里。先试默认（兼容未来 vendor 把它移到
        // /system/lib/），再 fallback 绝对路径。
        const char* kCandidates[] = {
            "libnative_vsync.so",
            "/system/lib/platformsdk/libnative_vsync.so",
            "/system/lib64/platformsdk/libnative_vsync.so",
        };
        void* h = nullptr;
        for (const char* p : kCandidates) {
            h = dlopen(p, RTLD_NOW);
            if (h) {
                DER_LOG("dlopen OK: %s -> %p", p, h);
                break;
            }
            DER_LOGW("dlopen %s failed: %s", p, dlerror() ? dlerror() : "(null)");
        }
        if (!h) return;
        g_oh_vsync_create  = reinterpret_cast<Fn_OH_NativeVSync_Create>(
            dlsym(h, "OH_NativeVSync_Create"));
        g_oh_vsync_destroy = reinterpret_cast<Fn_OH_NativeVSync_Destroy>(
            dlsym(h, "OH_NativeVSync_Destroy"));
        g_oh_vsync_request = reinterpret_cast<Fn_OH_NativeVSync_RequestFrame>(
            dlsym(h, "OH_NativeVSync_RequestFrame"));
        DER_LOG("OH NativeVSync API: create=%p destroy=%p request=%p",
                (void*)g_oh_vsync_create, (void*)g_oh_vsync_destroy,
                (void*)g_oh_vsync_request);
    });
}

// ----------------------------------------------------------------------------
// 2026-05-06 方向 A: pthread_key destructor for auto-detach on thread exit.
//
// OH NativeVSync 的 callback 跑在 OH 内部的长生命周期 vsync thread 上。第一次
// callback 时 attach 该 thread 到 ART JVM（thread name 设为 "DER-vsync-oh"），
// 后续 callback 重用 attach 状态。如果 OH 万一销毁该 vsync thread（如
// OH_NativeVSync_Destroy 时），pthread_key 的 destructor 自动 DetachCurrentThread
// 防止 ART CheckJNI fatal abort。
// ----------------------------------------------------------------------------
pthread_key_t g_detach_key;
std::once_flag g_detach_key_once;

void detachJvmCallback(void* /*ignored*/) {
    // g_javaVm 在本 anonymous namespace 顶部已定义，直接引用即可。
    if (g_javaVm) {
        g_javaVm->DetachCurrentThread();
    }
}

void initDetachKey() {
    std::call_once(g_detach_key_once, [](){
        pthread_key_create(&g_detach_key, detachJvmCallback);
    });
}

// Per-receiver native state.
struct DERState {
    // Global ref to the WeakReference<DisplayEventReceiver> passed in from Java.
    jobject weakRefGlobal = nullptr;

    // VSync scheduling state.
    std::atomic<bool> scheduled{false};
    std::atomic<bool> destroyed{false};

    // 2026-05-06 方向 A: OH NativeVSync handle (per-receiver).
    OH_NativeVSync* ohVsync = nullptr;

    // [DEPRECATED 2026-05-06] Legacy software vsync state (std::thread + sleep
    // modeled as 60Hz).  Replaced by OH NativeVSync; kept commented for
    // historical reference.  See file-level header for migration rationale.
    // std::mutex threadLock;
    // std::thread timerThread;

    int64_t frameNumber = 0;
};

// ----------------------------------------------------------------------------
// 2026-05-06 方向 A: ensure current thread is attached to ART JVM.
// First call on an OH vsync thread attaches with name "DER-vsync-oh" and
// registers pthread_key for auto-detach on thread exit.  Subsequent calls
// see GetEnv == JNI_OK and return the cached env.
// ----------------------------------------------------------------------------
JNIEnv* ensureAttachedForVsync() {
    if (!g_javaVm) return nullptr;
    JNIEnv* env = nullptr;
    jint rc = g_javaVm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
    if (rc == JNI_OK) return env;
    if (rc != JNI_EDETACHED) return nullptr;

    JavaVMAttachArgs args;
    args.version = JNI_VERSION_1_6;
    args.name    = const_cast<char*>("DER-vsync-oh");
    args.group   = nullptr;
    if (g_javaVm->AttachCurrentThread(&env, &args) != JNI_OK) return nullptr;

    // Register thread-exit auto-detach (only fires if OH destroys the thread).
    initDetachKey();
    pthread_setspecific(g_detach_key, reinterpret_cast<void*>(1));
    return env;
}

#if 0  // [DEPRECATED 2026-05-06] Legacy ScopedAttach RAII for software-vsync std::thread.
       // Removed because the per-vsync attach+detach pattern caused ART CheckJNI
       // fatal abort under sustained load (see file-level header).  Replaced by
       // ensureAttachedForVsync() + pthread_key auto-detach above.
struct ScopedAttach {
    JNIEnv* env = nullptr;
    bool didAttach = false;
    ScopedAttach() {
        if (!g_javaVm) return;
        jint rc = g_javaVm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
        if (rc == JNI_OK) return;  // already attached
        JavaVMAttachArgs args;
        args.version = JNI_VERSION_1_6;
        args.name    = const_cast<char*>("DER-vsync");
        args.group   = nullptr;
        if (g_javaVm->AttachCurrentThread(&env, &args) == JNI_OK) {
            didAttach = true;
        } else {
            env = nullptr;
        }
    }
    ~ScopedAttach() {
        if (didAttach && g_javaVm) {
            g_javaVm->DetachCurrentThread();
        }
    }
};
#endif  // #if 0 ScopedAttach

#if 0  // [DEPRECATED 2026-05-06] Legacy software-vsync timer thread function.
       // sleep_for(16ms) + dispatchVsync, fire-and-forget mode.  Replaced by
       // onOhVsync (registered via OH_NativeVSync_RequestFrame).
void vsyncTimerFn(DERState* state) {
    // Sleep 16ms (60Hz), then fire dispatchVsync on Java side.
    std::this_thread::sleep_for(std::chrono::milliseconds(16));

    if (state->destroyed.load()) return;
    if (!state->scheduled.exchange(false)) return;  // cancelled

    if (!g_javaVm) return;
    ScopedAttach attach;
    JNIEnv* env = attach.env;
    if (!env) return;

    // Resolve WeakReference<DER> -> DER strong ref.
    jclass weakRefClass = env->FindClass("java/lang/ref/WeakReference");
    if (!weakRefClass) { env->ExceptionClear(); return; }
    jmethodID getMethod = env->GetMethodID(weakRefClass, "get", "()Ljava/lang/Object;");
    env->DeleteLocalRef(weakRefClass);
    if (!getMethod) { env->ExceptionClear(); return; }
    jobject receiver = env->CallObjectMethod(state->weakRefGlobal, getMethod);
    if (!receiver || env->ExceptionCheck()) {
        env->ExceptionClear();
        return;
    }

    int64_t nowNs = std::chrono::duration_cast<std::chrono::nanoseconds>(
                        std::chrono::steady_clock::now().time_since_epoch()).count();
    constexpr int64_t kFrameIntervalNs = 16666666;  // 60Hz

    // 2026-05-01 G2.14n: populate the receiver's mVsyncEventData field BEFORE
    // calling dispatchVsync.  Java side's Choreographer.doFrame requires
    // frameTimelinesLength >= 1 — otherwise IllegalArgumentException.  In stock
    // AOSP, native code populates this from SurfaceFlinger data before
    // dispatch; we synthesize a 1-entry timeline.
    jclass derCls = env->GetObjectClass(receiver);
    if (derCls) {
        jfieldID vsyncDataFid = env->GetFieldID(
            derCls, "mVsyncEventData",
            "Landroid/view/DisplayEventReceiver$VsyncEventData;");
        env->DeleteLocalRef(derCls);
        if (vsyncDataFid) {
            jobject vsyncData = env->GetObjectField(receiver, vsyncDataFid);
            if (vsyncData) {
                jclass vsyncDataCls = env->GetObjectClass(vsyncData);
                if (vsyncDataCls) {
                    jfieldID lenFid = env->GetFieldID(
                        vsyncDataCls, "frameTimelinesLength", "I");
                    jfieldID intervalFid = env->GetFieldID(
                        vsyncDataCls, "frameInterval", "J");
                    jfieldID timelinesFid = env->GetFieldID(
                        vsyncDataCls, "frameTimelines",
                        "[Landroid/view/DisplayEventReceiver$VsyncEventData$FrameTimeline;");
                    jfieldID prefIdxFid = env->GetFieldID(
                        vsyncDataCls, "preferredFrameTimelineIndex", "I");
                    if (lenFid)      env->SetIntField(vsyncData, lenFid, 1);
                    if (prefIdxFid)  env->SetIntField(vsyncData, prefIdxFid, 0);
                    if (intervalFid) env->SetLongField(vsyncData, intervalFid, kFrameIntervalNs);

                    if (timelinesFid) {
                        jobjectArray timelines =
                            (jobjectArray)env->GetObjectField(vsyncData, timelinesFid);
                        if (timelines && env->GetArrayLength(timelines) >= 1) {
                            jobject t0 = env->GetObjectArrayElement(timelines, 0);
                            if (t0) {
                                jclass tCls = env->GetObjectClass(t0);
                                if (tCls) {
                                    jfieldID vidFid = env->GetFieldID(tCls, "vsyncId", "J");
                                    jfieldID eptFid = env->GetFieldID(tCls,
                                        "expectedPresentationTime", "J");
                                    jfieldID dlFid  = env->GetFieldID(tCls, "deadline", "J");
                                    if (vidFid) env->SetLongField(t0, vidFid,
                                        (jlong)(state->frameNumber + 1));
                                    if (eptFid) env->SetLongField(t0, eptFid,
                                        (jlong)(nowNs + kFrameIntervalNs));
                                    if (dlFid)  env->SetLongField(t0, dlFid,
                                        (jlong)(nowNs + kFrameIntervalNs));
                                    env->DeleteLocalRef(tCls);
                                }
                                env->DeleteLocalRef(t0);
                            }
                            env->DeleteLocalRef(timelines);
                        }
                    }
                    env->DeleteLocalRef(vsyncDataCls);
                }
                env->DeleteLocalRef(vsyncData);
            }
        }
    }
    if (env->ExceptionCheck()) env->ExceptionClear();

    // Call dispatchVsync(long timestampNanos, long physicalDisplayId, int frame).
    env->CallVoidMethod(receiver, g_dispatchVsyncMethod,
                        (jlong)nowNs, (jlong)0, (jint)(state->frameNumber++));
    if (env->ExceptionCheck()) env->ExceptionClear();
    env->DeleteLocalRef(receiver);
}
#endif  // #if 0 vsyncTimerFn

// ----------------------------------------------------------------------------
// 2026-05-06 方向 A: OH NativeVSync callback.
//
// OH 在真 VBlank 信号到达时调用本函数。运行在 OH 内部的 vsync thread 上（长
// 生命周期，OH 自己管理）。本函数：
//   1. 确认 thread 已 attach 到 ART JVM（首次自动 attach + 注册 pthread_key
//      auto-detach hook，后续重用 attach 状态不重复 attach）
//   2. 解 WeakReference<DER> 拿到 receiver
//   3. 填充 mVsyncEventData 字段（Choreographer.doFrame 要求 frameTimelinesLength >= 1）
//   4. CallVoidMethod dispatchVsync(timestamp, displayId=0, frameNumber)
//
// 与旧 vsyncTimerFn 的关键差异：
//   - 没有 sleep_for(16ms)：OH 真 vsync 信号驱动，不用软件模拟
//   - 没有 ScopedAttach：thread 长生命周期，attach 一次永久（pthread_key 兜底
//     thread 销毁时 detach）
//   - timestamp 由 OH 提供（vsync 真实时间），不是当前 monotonic
// ----------------------------------------------------------------------------
void onOhVsync(long long timestamp_ns, void* data) {
    DER_LOG("onOhVsync ENTRY ts=%lld data=%p", timestamp_ns, data);
    DERState* state = static_cast<DERState*>(data);
    if (!state || state->destroyed.load() || !g_javaVm) {
        DER_LOGW("onOhVsync: invalid state or destroyed; skip");
        return;
    }

    // Reset scheduled flag (matches AOSP idempotent semantics).
    state->scheduled.store(false);

    JNIEnv* env = ensureAttachedForVsync();
    if (!env) {
        DER_LOGE("onOhVsync: ensureAttachedForVsync failed");
        return;
    }

    // Resolve WeakReference<DER> -> DER strong ref.
    if (!state->weakRefGlobal) return;
    jclass weakRefClass = env->FindClass("java/lang/ref/WeakReference");
    if (!weakRefClass) { env->ExceptionClear(); return; }
    jmethodID getMethod = env->GetMethodID(weakRefClass, "get", "()Ljava/lang/Object;");
    env->DeleteLocalRef(weakRefClass);
    if (!getMethod) { env->ExceptionClear(); return; }
    jobject receiver = env->CallObjectMethod(state->weakRefGlobal, getMethod);
    if (!receiver || env->ExceptionCheck()) {
        env->ExceptionClear();
        return;
    }

    constexpr int64_t kFrameIntervalNs = 16666666;  // 60Hz fallback
    int64_t nowNs = (timestamp_ns > 0) ? timestamp_ns
                    : std::chrono::duration_cast<std::chrono::nanoseconds>(
                          std::chrono::steady_clock::now().time_since_epoch()).count();

    // Populate mVsyncEventData (Choreographer.doFrame requires frameTimelinesLength>=1).
    jclass derCls = env->GetObjectClass(receiver);
    if (derCls) {
        jfieldID vsyncDataFid = env->GetFieldID(
            derCls, "mVsyncEventData",
            "Landroid/view/DisplayEventReceiver$VsyncEventData;");
        env->DeleteLocalRef(derCls);
        if (vsyncDataFid) {
            jobject vsyncData = env->GetObjectField(receiver, vsyncDataFid);
            if (vsyncData) {
                jclass vsyncDataCls = env->GetObjectClass(vsyncData);
                if (vsyncDataCls) {
                    jfieldID lenFid = env->GetFieldID(
                        vsyncDataCls, "frameTimelinesLength", "I");
                    jfieldID intervalFid = env->GetFieldID(
                        vsyncDataCls, "frameInterval", "J");
                    jfieldID timelinesFid = env->GetFieldID(
                        vsyncDataCls, "frameTimelines",
                        "[Landroid/view/DisplayEventReceiver$VsyncEventData$FrameTimeline;");
                    jfieldID prefIdxFid = env->GetFieldID(
                        vsyncDataCls, "preferredFrameTimelineIndex", "I");
                    if (lenFid)      env->SetIntField(vsyncData, lenFid, 1);
                    if (prefIdxFid)  env->SetIntField(vsyncData, prefIdxFid, 0);
                    if (intervalFid) env->SetLongField(vsyncData, intervalFid, kFrameIntervalNs);

                    if (timelinesFid) {
                        jobjectArray timelines =
                            (jobjectArray)env->GetObjectField(vsyncData, timelinesFid);
                        if (timelines && env->GetArrayLength(timelines) >= 1) {
                            jobject t0 = env->GetObjectArrayElement(timelines, 0);
                            if (t0) {
                                jclass tCls = env->GetObjectClass(t0);
                                if (tCls) {
                                    jfieldID vidFid = env->GetFieldID(tCls, "vsyncId", "J");
                                    jfieldID eptFid = env->GetFieldID(tCls,
                                        "expectedPresentationTime", "J");
                                    jfieldID dlFid  = env->GetFieldID(tCls, "deadline", "J");
                                    if (vidFid) env->SetLongField(t0, vidFid,
                                        (jlong)(state->frameNumber + 1));
                                    if (eptFid) env->SetLongField(t0, eptFid,
                                        (jlong)(nowNs + kFrameIntervalNs));
                                    if (dlFid)  env->SetLongField(t0, dlFid,
                                        (jlong)(nowNs + kFrameIntervalNs));
                                    env->DeleteLocalRef(tCls);
                                }
                                env->DeleteLocalRef(t0);
                            }
                            env->DeleteLocalRef(timelines);
                        }
                    }
                    env->DeleteLocalRef(vsyncDataCls);
                }
                env->DeleteLocalRef(vsyncData);
            }
        }
    }
    if (env->ExceptionCheck()) env->ExceptionClear();

    // Call dispatchVsync(long timestampNanos, long physicalDisplayId, int frame).
    env->CallVoidMethod(receiver, g_dispatchVsyncMethod,
                        (jlong)nowNs, (jlong)0, (jint)(state->frameNumber++));
    if (env->ExceptionCheck()) env->ExceptionClear();
    env->DeleteLocalRef(receiver);
}

// ----------------------------------------------------------------------------
// JNI methods.
// ----------------------------------------------------------------------------

jlong JNICALL
DER_nativeInit(JNIEnv* env, jclass /*clazz*/,
                jobject receiverWeak, jobject /*vsyncEventDataWeak*/,
                jobject /*messageQueue*/, jint /*vsyncSource*/,
                jint /*eventRegistration*/, jlong /*layerHandle*/) {
    DER_LOG("nativeInit ENTRY env=%p receiverWeak=%p", (void*)env, (void*)receiverWeak);
    if (!g_javaVm) {
        env->GetJavaVM(&g_javaVm);
    }
    if (!g_derClass) {
        jclass local = env->FindClass("android/view/DisplayEventReceiver");
        if (local) {
            g_derClass = reinterpret_cast<jclass>(env->NewGlobalRef(local));
            g_dispatchVsyncMethod = env->GetMethodID(local, "dispatchVsync", "(JJI)V");
            env->DeleteLocalRef(local);
        }
    }

    auto* state = new DERState();
    if (receiverWeak) {
        state->weakRefGlobal = env->NewGlobalRef(receiverWeak);
    }

    // 2026-05-06 方向 A: create OH NativeVSync handle for this receiver.
    // dlsym lazy resolve avoids link-time hard dep on libnative_vsync.so.
    resolveOhVsyncApi();
    if (g_oh_vsync_create) {
        const char kVsyncName[] = "der-vsync";
        state->ohVsync = g_oh_vsync_create(kVsyncName,
                                           static_cast<unsigned int>(strlen(kVsyncName)));
        DER_LOG("OH_NativeVSync_Create -> %p", (void*)state->ohVsync);
    } else {
        DER_LOGW("OH_NativeVSync_Create unavailable; vsync will be no-op");
    }

    DER_LOG("nativeInit RETURN state=%p", (void*)state);
    return reinterpret_cast<jlong>(state);
}

// AOSP returns a function pointer that NativeAllocationRegistry passes to
// libcore.util.NativeAllocationRegistry.applyFreeFunction(long ptr, long size).
// 2026-05-02: previously returned 0 (which feeds NAR(0) crash via ART intrinsic).
// Attempted real DER_release impl but caused regression — main thread initChild
// SIGSEGV at very early Java entry.  Need separate diagnosis.  TODO P1: real
// release fn (delete DERState) without disturbing main-thread init path.
jlong JNICALL
DER_nativeGetDisplayEventReceiverFinalizer(JNIEnv* /*env*/, jclass /*clazz*/) {
    return 0;
}

void JNICALL
DER_nativeScheduleVsync(JNIEnv* /*env*/, jclass /*clazz*/, jlong receiverPtr) {
    // 2026-05-06 方向 A: bridge to OH_NativeVSync_RequestFrame (real VBlank).
    //
    // 演化过程（保留为提醒未来不要走回头路）：
    //   - G2.14n (2026-05-01): std::thread + sleep_for(16ms) 软件模拟 vsync
    //     每次 nativeScheduleVsync 创建新 thread → ScopedAttach 反复 attach/detach
    //     ART CheckJNI fatal abort "thread exited without DetachCurrentThread"
    //     表现：activityResumed rc=0 后 76-101ms SIGABRT，cppcrash thread name <pre-initialize>
    //   - 方向 C (2026-05-06): nativeScheduleVsync 改 no-op 验证 → abort 消失（确认根因）
    //   - 方向 A (本次实施): OH_NativeVSync_RequestFrame，OH 真 vsync 信号驱动
    //     OH 自己长期持有 vsync thread，adapter 第一次 callback attach + pthread_key
    //     auto-detach 兜底，不再反复 create/destroy thread
    //
    // 接口语义（per OH NDK doc）：在一个 vsync 周期内多次调用同一 vsync 的
    // RequestFrame 只执行最后一次 callback。这与 AOSP scheduleVsync 的 idempotent
    // 语义一致。
    DER_LOG("scheduleVsync ENTRY receiverPtr=0x%lx", (unsigned long)receiverPtr);
    auto* state = reinterpret_cast<DERState*>(receiverPtr);
    if (!state || state->destroyed.load()) {
        DER_LOGW("scheduleVsync: invalid state or destroyed");
        return;
    }

    // Idempotent: if already scheduled this frame, skip.
    bool expected = false;
    if (!state->scheduled.compare_exchange_strong(expected, true)) {
        DER_LOG("scheduleVsync: already scheduled, skip");
        return;
    }

    if (!state->ohVsync || !g_oh_vsync_request) {
        DER_LOGW("scheduleVsync: ohVsync=%p request=%p — drop",
                 (void*)state->ohVsync, (void*)g_oh_vsync_request);
        state->scheduled.store(false);
        return;
    }

    DER_LOG("scheduleVsync calling RequestFrame ohVsync=%p", (void*)state->ohVsync);
    int rc = g_oh_vsync_request(state->ohVsync, &onOhVsync, state);
    DER_LOG("scheduleVsync RequestFrame rc=%d", rc);
    if (rc != 0) {
        DER_LOGE("OH_NativeVSync_RequestFrame rc=%d", rc);
        state->scheduled.store(false);  // allow next attempt
    }
}

jobject JNICALL
DER_nativeGetLatestVsyncEventData(JNIEnv* env, jclass /*clazz*/, jlong /*receiverPtr*/) {
    // 2026-05-01 G2.14n: Java Choreographer.FrameData.update() at the late-frame
    // path (line 1208-1210) calls getLatestVsyncEventData() and then passes the
    // result to update() WITHOUT null check — NPE if we return null.  Construct
    // a minimal valid VsyncEventData with one synthetic FrameTimeline.
    constexpr int64_t kFrameIntervalNs = 16666666;  // 60Hz

    int64_t nowNs = std::chrono::duration_cast<std::chrono::nanoseconds>(
                        std::chrono::steady_clock::now().time_since_epoch()).count();

    // Find FrameTimeline class and ctor.
    jclass timelineCls = env->FindClass(
        "android/view/DisplayEventReceiver$VsyncEventData$FrameTimeline");
    if (!timelineCls) { env->ExceptionClear(); return nullptr; }
    jmethodID timelineCtor = env->GetMethodID(timelineCls, "<init>", "(JJJ)V");
    if (!timelineCtor) { env->ExceptionClear(); env->DeleteLocalRef(timelineCls); return nullptr; }

    // Build a 1-element FrameTimeline[].
    jobjectArray timelineArr = env->NewObjectArray(1, timelineCls, nullptr);
    if (!timelineArr) { env->ExceptionClear(); env->DeleteLocalRef(timelineCls); return nullptr; }
    jobject t0 = env->NewObject(timelineCls, timelineCtor,
                                (jlong)1,                          // vsyncId
                                (jlong)(nowNs + kFrameIntervalNs), // expectedPresentationTime
                                (jlong)(nowNs + kFrameIntervalNs)); // deadline
    if (!t0) { env->ExceptionClear(); env->DeleteLocalRef(timelineArr); env->DeleteLocalRef(timelineCls); return nullptr; }
    env->SetObjectArrayElement(timelineArr, 0, t0);
    env->DeleteLocalRef(t0);
    env->DeleteLocalRef(timelineCls);

    // Construct VsyncEventData(FrameTimeline[], int prefIdx, int len, long interval).
    jclass vedCls = env->FindClass("android/view/DisplayEventReceiver$VsyncEventData");
    if (!vedCls) { env->ExceptionClear(); env->DeleteLocalRef(timelineArr); return nullptr; }
    jmethodID vedCtor = env->GetMethodID(
        vedCls, "<init>",
        "([Landroid/view/DisplayEventReceiver$VsyncEventData$FrameTimeline;IIJ)V");
    if (!vedCtor) { env->ExceptionClear(); env->DeleteLocalRef(vedCls); env->DeleteLocalRef(timelineArr); return nullptr; }
    jobject ved = env->NewObject(vedCls, vedCtor,
                                  timelineArr, (jint)0, (jint)1, (jlong)kFrameIntervalNs);
    env->DeleteLocalRef(vedCls);
    env->DeleteLocalRef(timelineArr);
    if (env->ExceptionCheck()) { env->ExceptionClear(); return nullptr; }
    return ved;
}

const JNINativeMethod kDisplayEventReceiverMethods[] = {
    { "nativeInit",
      "(Ljava/lang/ref/WeakReference;Ljava/lang/ref/WeakReference;"
      "Landroid/os/MessageQueue;IIJ)J",
      reinterpret_cast<void*>(DER_nativeInit) },
    { "nativeGetDisplayEventReceiverFinalizer", "()J",
      reinterpret_cast<void*>(DER_nativeGetDisplayEventReceiverFinalizer) },
    { "nativeScheduleVsync", "(J)V",
      reinterpret_cast<void*>(DER_nativeScheduleVsync) },
    { "nativeGetLatestVsyncEventData",
      "(J)Landroid/view/DisplayEventReceiver$VsyncEventData;",
      reinterpret_cast<void*>(DER_nativeGetLatestVsyncEventData) },
};

}  // namespace

int register_android_view_DisplayEventReceiver(JNIEnv* env) {
    jclass clazz = env->FindClass("android/view/DisplayEventReceiver");
    if (clazz == nullptr) {
        fprintf(stderr, "[liboh_android_runtime] register_android_view_DisplayEventReceiver:"
                        " FindClass(android/view/DisplayEventReceiver) failed\n");
        return -1;
    }
    jint rc = env->RegisterNatives(clazz, kDisplayEventReceiverMethods,
                                    sizeof(kDisplayEventReceiverMethods)
                                        / sizeof(kDisplayEventReceiverMethods[0]));
    env->DeleteLocalRef(clazz);
    if (rc != JNI_OK) {
        fprintf(stderr, "[liboh_android_runtime] register_android_view_DisplayEventReceiver:"
                        " RegisterNatives rc=%d\n", (int)rc);
        return -1;
    }
    fprintf(stderr, "[liboh_android_runtime] register_android_view_DisplayEventReceiver:"
                    " 4 methods (Init, Finalizer, ScheduleVsync, GetLatestVsyncEventData)\n");
    return 0;
}

}  // namespace android
