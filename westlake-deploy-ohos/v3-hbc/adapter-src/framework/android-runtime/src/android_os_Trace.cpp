// ============================================================================
// android_os_Trace.cpp
//
// JNI bindings for android.os.Trace. Mirrors AOSP
// frameworks/base/core/jni/android_os_Trace.cpp but routes through OH
// HiTraceMeter NDK so trace events show up in OH's hitrace tooling.
//
// Minimum set to unblock ActivityThread.main() startup on Hello World:
//   Trace.isTagEnabled → Trace.nativeGetEnabledTags  (@CriticalNative)
//   Trace.traceBegin   → Trace.nativeTraceBegin
//   Trace.traceEnd     → Trace.nativeTraceEnd
//
// Additional AOSP Trace native methods (nativeAsyncTraceBegin / nativeCounter /
// nativeInstant / etc.) intentionally omitted — lazy-resolved on first call;
// will be added incrementally as child progresses past startup and new
// UnsatisfiedLinkError surfaces.
// ============================================================================

#include "AndroidRuntime.h"

#include <jni.h>
#include <stdint.h>

// OH HiTrace NDK C API (same declarations liboh_hwui_shim uses in atrace_compat.c).
// Runtime library: libhitrace_ndk.z.so (at /system/lib/ndk/ on DAYU200).
extern "C" void OH_HiTrace_StartTrace(const char* name);
extern "C" void OH_HiTrace_FinishTrace(void);

namespace android {

namespace {

// @CriticalNative — no JNIEnv/jclass parameters, direct C calling convention.
// Must match AOSP's "()J" signature; returns the bitmask of enabled trace tags.
jlong JNICALL
Trace_nativeGetEnabledTags() {
    // All tags enabled, matching atrace_compat.c::atrace_get_enabled_tags.
    // The Java-side Trace.isTagEnabled always returns true, so traceBegin/End
    // always fire. Events go to OH HiTrace via OH_HiTrace_* below.
    return static_cast<jlong>(~0ULL);
}

void JNICALL
Trace_nativeTraceBegin(JNIEnv* env, jclass /*clazz*/,
                       jlong /*tag*/, jstring name) {
    if (name == nullptr) {
        return;
    }
    const char* str = env->GetStringUTFChars(name, nullptr);
    if (str) {
        OH_HiTrace_StartTrace(str);
        env->ReleaseStringUTFChars(name, str);
    }
}

void JNICALL
Trace_nativeTraceEnd(JNIEnv* /*env*/, jclass /*clazz*/, jlong /*tag*/) {
    OH_HiTrace_FinishTrace();
}

// B.20: additional Trace natives — no-op since OH HiTrace NDK has no analog
// for app-tracing-allowed flag, async, counter, instant. handleBindApplication
// line 6866 calls nativeSetAppTracingAllowed.
void JNICALL Trace_nativeSetAppTracingAllowed(JNIEnv*, jclass, jboolean) {}
void JNICALL Trace_nativeSetTracingEnabled(JNIEnv*, jclass, jboolean) {}
void JNICALL Trace_nativeTraceCounter(JNIEnv*, jclass, jlong, jstring, jlong) {}
void JNICALL Trace_nativeAsyncTraceBegin(JNIEnv*, jclass, jlong, jstring, jint) {}
void JNICALL Trace_nativeAsyncTraceEnd(JNIEnv*, jclass, jlong, jstring, jint) {}
void JNICALL Trace_nativeAsyncTraceForTrackBegin(JNIEnv*, jclass, jlong, jstring, jstring, jint) {}
void JNICALL Trace_nativeAsyncTraceForTrackEnd(JNIEnv*, jclass, jlong, jstring, jint) {}
void JNICALL Trace_nativeInstant(JNIEnv*, jclass, jlong, jstring) {}
void JNICALL Trace_nativeInstantForTrack(JNIEnv*, jclass, jlong, jstring, jstring) {}

const JNINativeMethod kTraceMethods[] = {
    // Note: @CriticalNative bindings still register through the standard JNI
    // table; ART will invoke with the reduced calling convention based on the
    // @CriticalNative annotation on the Java declaration.
    { "nativeGetEnabledTags",
      "()J",
      reinterpret_cast<void*>(Trace_nativeGetEnabledTags) },
    { "nativeTraceBegin",
      "(JLjava/lang/String;)V",
      reinterpret_cast<void*>(Trace_nativeTraceBegin) },
    { "nativeTraceEnd",
      "(J)V",
      reinterpret_cast<void*>(Trace_nativeTraceEnd) },
    { "nativeSetAppTracingAllowed",        "(Z)V", reinterpret_cast<void*>(Trace_nativeSetAppTracingAllowed) },
    { "nativeSetTracingEnabled",           "(Z)V", reinterpret_cast<void*>(Trace_nativeSetTracingEnabled) },
    { "nativeTraceCounter",                "(JLjava/lang/String;J)V", reinterpret_cast<void*>(Trace_nativeTraceCounter) },
    { "nativeAsyncTraceBegin",             "(JLjava/lang/String;I)V", reinterpret_cast<void*>(Trace_nativeAsyncTraceBegin) },
    { "nativeAsyncTraceEnd",               "(JLjava/lang/String;I)V", reinterpret_cast<void*>(Trace_nativeAsyncTraceEnd) },
    { "nativeAsyncTraceForTrackBegin",     "(JLjava/lang/String;Ljava/lang/String;I)V", reinterpret_cast<void*>(Trace_nativeAsyncTraceForTrackBegin) },
    { "nativeAsyncTraceForTrackEnd",       "(JLjava/lang/String;I)V", reinterpret_cast<void*>(Trace_nativeAsyncTraceForTrackEnd) },
    { "nativeInstant",                     "(JLjava/lang/String;)V", reinterpret_cast<void*>(Trace_nativeInstant) },
    { "nativeInstantForTrack",             "(JLjava/lang/String;Ljava/lang/String;)V", reinterpret_cast<void*>(Trace_nativeInstantForTrack) },
};

}  // namespace

int register_android_os_Trace(JNIEnv* env) {
    jclass clazz = env->FindClass("android/os/Trace");
    if (!clazz) {
        return -1;
    }
    jint rc = env->RegisterNatives(clazz, kTraceMethods,
                                    sizeof(kTraceMethods) / sizeof(kTraceMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
