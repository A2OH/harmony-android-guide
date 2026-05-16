// ============================================================================
// android_view_InputEventReceiver.cpp
//
// JNI binding for android.view.InputEventReceiver — registered into
// liboh_android_runtime's kRegJNI table. Mirrors AOSP
// frameworks/base/core/jni/android_view_InputEventReceiver.cpp shape but
// implements the NO_INPUT_EVENT_TRANSPORT baseline: native methods are
// either no-op or return cheap default values.
//
// Java side (frameworks/base/core/java/android/view/InputEventReceiver.java
// lines 53-62):
//   private static native long    nativeInit(WeakReference<InputEventReceiver>,
//                                            InputChannel, MessageQueue);
//   private static native void    nativeDispose(long receiverPtr);
//   private static native void    nativeFinishInputEvent(long receiverPtr,
//                                                         int seq, boolean handled);
//   private static native void    nativeReportTimeline(long receiverPtr, int eventId,
//                                                       long gpuTime, long presentTime);
//   private static native boolean nativeConsumeBatchedInputEvents(long receiverPtr,
//                                                                  long frameTimeNanos);
//   private static native String  nativeDump(long receiverPtr, String prefix);
//
// AOSP impl uses libgui InputConsumer to read events from the InputChannel
// socketpair fd (server end held by InputDispatcher in system_server). On OH
// there is no InputDispatcher — input flows through OH MMI directly to
// InputEventBridge.java + adapter_bridge.cpp. So InputEventReceiver here is
// a "ghost" — it answers all native methods with safe defaults so that
// AOSP framework code (ViewRootImpl.WindowInputEventReceiver chain) can
// construct, call methods on, and dispose of an InputEventReceiver object
// without crashing. No real input event delivery happens through this path.
//
// Strategy:
//   * nativeInit returns a non-zero opaque handle (allocated OhInputEventReceiver
//     struct). Java-side fields nativeInit's result and treats !=0 as "OK".
//   * nativeDispose frees the struct.
//   * nativeFinishInputEvent / nativeReportTimeline are no-ops.
//   * nativeConsumeBatchedInputEvents returns false ("nothing consumed").
//   * nativeDump returns empty String.
//
// Future P2: hook OhInputEventReceiver.ohEndpoint to a real OH MMI input
// channel; route OH MMI keyboard/touch events to this receiver's Java
// callback via JNI.
// ============================================================================

#include "AndroidRuntime.h"

#include <jni.h>
#include <atomic>
#include <cstdint>
#include <cstdio>
#include <cstring>
#include <new>

extern "C" int HiLogPrint(int type, int level, unsigned int domain,
                          const char* tag, const char* fmt, ...)
    __attribute__((__format__(printf, 5, 6)));
#define HLOG_INFO(fmt, ...) HiLogPrint(3, 4, 0xD000F00u, "OH_LibRuntime", fmt, ##__VA_ARGS__)
#define HLOG_ERR(fmt, ...)  HiLogPrint(3, 6, 0xD000F00u, "OH_LibRuntime", fmt, ##__VA_ARGS__)

namespace android {
namespace {

struct OhInputEventReceiver {
    int32_t magic;          // 'OHER' = 0x4F484552 — sanity tag
    int64_t id;             // unique id for debug
    jobject receiverWeakRef; // global ref to the WeakReference passed by Java
};

constexpr int32_t kOhInputEventReceiverMagic = 0x4F484552;
std::atomic<int64_t> g_receiverId{1};

OhInputEventReceiver* validate(jlong ptr) {
    auto* r = reinterpret_cast<OhInputEventReceiver*>(static_cast<uintptr_t>(ptr));
    if (!r || r->magic != kOhInputEventReceiverMagic) return nullptr;
    return r;
}

// nativeInit(WeakReference<InputEventReceiver>, InputChannel, MessageQueue) -> long
jlong IER_nativeInit(JNIEnv* env, jclass /*clazz*/,
                     jobject receiverWeak, jobject /*inputChannel*/, jobject /*messageQueue*/) {
    auto* r = new (std::nothrow) OhInputEventReceiver();
    if (!r) return 0;
    r->magic = kOhInputEventReceiverMagic;
    r->id = g_receiverId.fetch_add(1);
    r->receiverWeakRef = receiverWeak ? env->NewGlobalRef(receiverWeak) : nullptr;
    fprintf(stderr, "[liboh_android_runtime] InputEventReceiver.nativeInit id=%lld "
                    "(NO_INPUT_EVENT_TRANSPORT baseline — no real event delivery)\n",
            static_cast<long long>(r->id));
    return static_cast<jlong>(reinterpret_cast<uintptr_t>(r));
}

// nativeDispose(long)
void IER_nativeDispose(JNIEnv* env, jclass /*clazz*/, jlong ptr) {
    auto* r = validate(ptr);
    if (!r) return;
    if (r->receiverWeakRef) {
        env->DeleteGlobalRef(r->receiverWeakRef);
        r->receiverWeakRef = nullptr;
    }
    delete r;
}

// nativeFinishInputEvent(long, int, boolean) — no-op (no real event to ack)
void IER_nativeFinishInputEvent(JNIEnv* /*env*/, jclass /*clazz*/,
                                 jlong /*ptr*/, jint /*seq*/, jboolean /*handled*/) {
    // no-op
}

// nativeReportTimeline(long, int, long, long) — no-op (no Choreographer FrameInfo)
void IER_nativeReportTimeline(JNIEnv* /*env*/, jclass /*clazz*/,
                               jlong /*ptr*/, jint /*eventId*/,
                               jlong /*gpuTime*/, jlong /*presentTime*/) {
    // no-op
}

// nativeConsumeBatchedInputEvents(long, long) -> boolean (always false: nothing batched)
jboolean IER_nativeConsumeBatchedInputEvents(JNIEnv* /*env*/, jclass /*clazz*/,
                                              jlong /*ptr*/, jlong /*frameTimeNanos*/) {
    return JNI_FALSE;
}

// nativeDump(long, String) -> String (returns "")
jstring IER_nativeDump(JNIEnv* env, jclass /*clazz*/,
                        jlong /*ptr*/, jstring /*prefix*/) {
    return env->NewStringUTF("");
}

const JNINativeMethod kInputEventReceiverMethods[] = {
    { "nativeInit",
      "(Ljava/lang/ref/WeakReference;Landroid/view/InputChannel;Landroid/os/MessageQueue;)J",
      reinterpret_cast<void*>(IER_nativeInit) },
    { "nativeDispose", "(J)V",
      reinterpret_cast<void*>(IER_nativeDispose) },
    { "nativeFinishInputEvent", "(JIZ)V",
      reinterpret_cast<void*>(IER_nativeFinishInputEvent) },
    { "nativeReportTimeline", "(JIJJ)V",
      reinterpret_cast<void*>(IER_nativeReportTimeline) },
    { "nativeConsumeBatchedInputEvents", "(JJ)Z",
      reinterpret_cast<void*>(IER_nativeConsumeBatchedInputEvents) },
    { "nativeDump", "(JLjava/lang/String;)Ljava/lang/String;",
      reinterpret_cast<void*>(IER_nativeDump) },
};

}  // namespace

int register_android_view_InputEventReceiver(JNIEnv* env) {
    if (env->ExceptionCheck()) env->ExceptionClear();

    HLOG_INFO("register_android_view_InputEventReceiver: ENTER");
    jclass clazz = env->FindClass("android/view/InputEventReceiver");
    if (clazz == nullptr) {
        HLOG_ERR("register_android_view_InputEventReceiver: FindClass returned null");
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
        }
        return -1;
    }
    HLOG_INFO("register_android_view_InputEventReceiver: FindClass OK, calling RegisterNatives 6 methods");
    jint rc = env->RegisterNatives(clazz, kInputEventReceiverMethods,
                                    sizeof(kInputEventReceiverMethods)
                                        / sizeof(kInputEventReceiverMethods[0]));
    env->DeleteLocalRef(clazz);
    if (rc != JNI_OK) {
        HLOG_ERR("register_android_view_InputEventReceiver: RegisterNatives rc=%{public}d", (int)rc);
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
        }
        return -1;
    }
    HLOG_INFO("register_android_view_InputEventReceiver: SUCCESS 6 methods registered");
    return 0;
}

}  // namespace android
