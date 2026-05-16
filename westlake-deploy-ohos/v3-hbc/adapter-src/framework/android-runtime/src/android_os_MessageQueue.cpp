// ============================================================================
// android_os_MessageQueue.cpp
//
// JNI for android.os.MessageQueue — Looper core. ActivityThread.main() calls
// Looper.prepareMainLooper → new MessageQueue(true) → nativeInit() at line 79.
// Without this registered, UnsatisfiedLinkError kills the child.
//
// Implementation: real Linux epoll + eventfd, matching AOSP system/core's
// libutils Looper semantics. This is a functional MessageQueue, not a stub —
// Hello World needs pollOnce/wake to actually deliver UI events after the
// main looper is created.
//
// AOSP NativeMessageQueue wraps android::Looper (from libutils) with sp<> +
// NativeMessageQueue::raiseException. We skip the sp/libutils dependency
// and implement directly with POSIX primitives. Behavior matches what
// MessageQueue.java expects:
//   - nativeInit: allocate native handle, returns long pointer
//   - nativePollOnce(ptr, timeoutMs): block up to timeoutMs waiting on
//       wake fd or registered fds; timeoutMs==-1 = infinite; 0 = no-wait
//   - nativeWake(ptr): signal wake fd so pollOnce returns immediately
//   - nativeIsPolling(ptr): true if pollOnce is currently blocking
//   - nativeDestroy(ptr): close fds + delete handle
//   - nativeSetFileDescriptorEvents: register/unregister fd monitoring
//       (stub no-op — Hello World doesn't add custom fd watchers)
// ============================================================================

#include "AndroidRuntime.h"

#include <jni.h>
#include <atomic>
#include <new>          // std::nothrow
#include <errno.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/epoll.h>
#include <sys/eventfd.h>

namespace android {

namespace {

struct NativeMQ {
    int epfd;
    int wakefd;
    std::atomic<bool> polling{false};
};

jlong MQ_nativeInit(JNIEnv* env, jclass /*clazz*/) {
    NativeMQ* mq = new (std::nothrow) NativeMQ();
    if (!mq) {
        return 0;
    }
    mq->wakefd = eventfd(0, EFD_NONBLOCK | EFD_CLOEXEC);
    if (mq->wakefd < 0) {
        delete mq;
        return 0;
    }
    mq->epfd = epoll_create1(EPOLL_CLOEXEC);
    if (mq->epfd < 0) {
        close(mq->wakefd);
        delete mq;
        return 0;
    }
    struct epoll_event ev;
    memset(&ev, 0, sizeof(ev));
    ev.events = EPOLLIN;
    ev.data.fd = mq->wakefd;
    if (epoll_ctl(mq->epfd, EPOLL_CTL_ADD, mq->wakefd, &ev) < 0) {
        close(mq->wakefd);
        close(mq->epfd);
        delete mq;
        return 0;
    }
    return reinterpret_cast<jlong>(mq);
}

void MQ_nativeDestroy(JNIEnv* /*env*/, jclass /*clazz*/, jlong ptr) {
    NativeMQ* mq = reinterpret_cast<NativeMQ*>(ptr);
    if (!mq) return;
    if (mq->epfd >= 0)  close(mq->epfd);
    if (mq->wakefd >= 0) close(mq->wakefd);
    delete mq;
}

void MQ_nativePollOnce(JNIEnv* /*env*/, jobject /*obj*/,
                        jlong ptr, jint timeoutMs) {
    NativeMQ* mq = reinterpret_cast<NativeMQ*>(ptr);
    if (!mq) return;

    mq->polling.store(true, std::memory_order_release);

    struct epoll_event events[16];
    int timeout = (timeoutMs < 0) ? -1 : timeoutMs;
    int n;
    do {
        n = epoll_wait(mq->epfd, events, 16, timeout);
    } while (n < 0 && errno == EINTR);

    for (int i = 0; i < n; ++i) {
        if (events[i].data.fd == mq->wakefd) {
            // Drain wake counter so next wait blocks correctly until next wake.
            uint64_t tmp;
            (void) read(mq->wakefd, &tmp, sizeof(tmp));
        }
        // Other fds: AOSP dispatches to registered listeners. Adapter stub
        // currently does not support file-descriptor watchers, so no dispatch.
    }

    mq->polling.store(false, std::memory_order_release);
}

void MQ_nativeWake(JNIEnv* /*env*/, jclass /*clazz*/, jlong ptr) {
    NativeMQ* mq = reinterpret_cast<NativeMQ*>(ptr);
    if (!mq || mq->wakefd < 0) return;
    uint64_t v = 1;
    ssize_t unused = write(mq->wakefd, &v, sizeof(v));
    (void) unused;
}

jboolean MQ_nativeIsPolling(JNIEnv* /*env*/, jclass /*clazz*/, jlong ptr) {
    NativeMQ* mq = reinterpret_cast<NativeMQ*>(ptr);
    if (!mq) return JNI_FALSE;
    return mq->polling.load(std::memory_order_acquire) ? JNI_TRUE : JNI_FALSE;
}

void MQ_nativeSetFileDescriptorEvents(JNIEnv* /*env*/, jclass /*clazz*/,
                                       jlong /*ptr*/, jint /*fd*/, jint /*events*/) {
    // Stub no-op. Hello World does not install custom fd watchers on the main
    // MessageQueue. If a later stage adds watchers (e.g. InputChannel, Binder
    // driver reader), extend to epoll_ctl ADD/MOD/DEL and track watcher→fd
    // map. Until then, silently ignore.
}

const JNINativeMethod kMessageQueueMethods[] = {
    { "nativeInit",                    "()J",   reinterpret_cast<void*>(MQ_nativeInit) },
    { "nativeDestroy",                 "(J)V",  reinterpret_cast<void*>(MQ_nativeDestroy) },
    { "nativePollOnce",                "(JI)V", reinterpret_cast<void*>(MQ_nativePollOnce) },
    { "nativeWake",                    "(J)V",  reinterpret_cast<void*>(MQ_nativeWake) },
    { "nativeIsPolling",               "(J)Z",  reinterpret_cast<void*>(MQ_nativeIsPolling) },
    { "nativeSetFileDescriptorEvents", "(JII)V",reinterpret_cast<void*>(MQ_nativeSetFileDescriptorEvents) },
};

}  // namespace

int register_android_os_MessageQueue(JNIEnv* env) {
    jclass clazz = env->FindClass("android/os/MessageQueue");
    if (!clazz) {
        return -1;
    }
    jint rc = env->RegisterNatives(clazz, kMessageQueueMethods,
                                    sizeof(kMessageQueueMethods) / sizeof(kMessageQueueMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
