// ============================================================================
// android_os_SystemClock.cpp
//
// JNI bindings for android.os.SystemClock. Used everywhere in Android
// framework (Looper, Handler, ActivityThread.attach, View frame timing,
// trace timestamps, etc.). Without it, ServiceManager.<clinit> already
// triggers UnsatisfiedLinkError chain and the child can't start.
//
// All methods are @CriticalNative — direct C calling convention, no
// JNIEnv/jclass parameters. Implemented via Linux clock_gettime which is
// available identically on AOSP and OH (musl).
//
// Time semantics:
//   uptimeMillis / uptimeNanos      — CLOCK_MONOTONIC (ms / ns), excludes
//                                      time spent in suspend
//   elapsedRealtime / Nanos         — CLOCK_BOOTTIME (ms / ns), includes
//                                      suspend; matches ATRACE / log tags
//   currentThreadTimeMillis / Micro — CLOCK_THREAD_CPUTIME_ID, CPU time
//                                      consumed by current thread
//   currentTimeMicro                — CLOCK_REALTIME microseconds, wall clock
// ============================================================================

#include "AndroidRuntime.h"

#include <jni.h>
#include <stdint.h>
#include <time.h>
#include <sys/time.h>

namespace android {

namespace {

static inline int64_t toMillis(const struct timespec& ts) {
    return static_cast<int64_t>(ts.tv_sec) * 1000LL +
           static_cast<int64_t>(ts.tv_nsec) / 1000000LL;
}
static inline int64_t toNanos(const struct timespec& ts) {
    return static_cast<int64_t>(ts.tv_sec) * 1000000000LL +
           static_cast<int64_t>(ts.tv_nsec);
}
static inline int64_t toMicros(const struct timespec& ts) {
    return static_cast<int64_t>(ts.tv_sec) * 1000000LL +
           static_cast<int64_t>(ts.tv_nsec) / 1000LL;
}

jlong JNICALL SC_uptimeMillis() {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return toMillis(ts);
}

jlong JNICALL SC_uptimeNanos() {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return toNanos(ts);
}

jlong JNICALL SC_elapsedRealtime() {
    struct timespec ts;
    clock_gettime(CLOCK_BOOTTIME, &ts);
    return toMillis(ts);
}

jlong JNICALL SC_elapsedRealtimeNanos() {
    struct timespec ts;
    clock_gettime(CLOCK_BOOTTIME, &ts);
    return toNanos(ts);
}

jlong JNICALL SC_currentThreadTimeMillis() {
    struct timespec ts;
    clock_gettime(CLOCK_THREAD_CPUTIME_ID, &ts);
    return toMillis(ts);
}

jlong JNICALL SC_currentThreadTimeMicro() {
    struct timespec ts;
    clock_gettime(CLOCK_THREAD_CPUTIME_ID, &ts);
    return toMicros(ts);
}

jlong JNICALL SC_currentTimeMicro() {
    struct timespec ts;
    clock_gettime(CLOCK_REALTIME, &ts);
    return toMicros(ts);
}

const JNINativeMethod kSystemClockMethods[] = {
    { "uptimeMillis",            "()J", reinterpret_cast<void*>(SC_uptimeMillis) },
    { "uptimeNanos",             "()J", reinterpret_cast<void*>(SC_uptimeNanos) },
    { "elapsedRealtime",         "()J", reinterpret_cast<void*>(SC_elapsedRealtime) },
    { "elapsedRealtimeNanos",    "()J", reinterpret_cast<void*>(SC_elapsedRealtimeNanos) },
    { "currentThreadTimeMillis", "()J", reinterpret_cast<void*>(SC_currentThreadTimeMillis) },
    { "currentThreadTimeMicro",  "()J", reinterpret_cast<void*>(SC_currentThreadTimeMicro) },
    { "currentTimeMicro",        "()J", reinterpret_cast<void*>(SC_currentTimeMicro) },
};

}  // namespace

int register_android_os_SystemClock(JNIEnv* env) {
    jclass clazz = env->FindClass("android/os/SystemClock");
    if (!clazz) {
        return -1;
    }
    jint rc = env->RegisterNatives(clazz, kSystemClockMethods,
                                    sizeof(kSystemClockMethods) / sizeof(kSystemClockMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
