// ============================================================================
// android_util_EventLog.cpp
//
// JNI bindings for android.util.EventLog. Mirrors AOSP
// frameworks/base/core/jni/android_util_EventLog.cpp +
// frameworks/base/core/jni/eventlog_helper.h, but routes through OH HiLog
// instead of AOSP's android_log_event_list / liblog event-id channel
// (which OH does not ship — only the headers under
// third_party/mesa3d/include/android_stub/log/* are present, no impl).
//
// Java side (AOSP frameworks/base/core/java/android/util/EventLog.java):
//   public static native int  writeEvent(int tag, int value);                  // (II)I
//   public static native int  writeEvent(int tag, long value);                 // (IJ)I
//   public static native int  writeEvent(int tag, float value);                // (IF)I
//   public static native int  writeEvent(int tag, String str);                 // (ILjava/lang/String;)I
//   public static native int  writeEvent(int tag, Object... list);             // (I[Ljava/lang/Object;)I
//   public static native void readEvents(int[] tags, Collection<Event> out);   // ([ILjava/util/Collection;)V
//   public static native void readEventsOnWrapping(int[] tags, long ts,
//                                                  Collection<Event> out);     // ([IJLjava/util/Collection;)V
//
// Adaptation strategy (project-wide rule: keep impl in adapter layer, not in
// AOSP/OH source — feedback_blame_adapter_first.md):
//
//   * writeEvent(*) — log tag + value to OH HiLog at INFO level under domain
//     0xD002000 (LOG_APP) and tag "AndroidEventLog".  Returns approximate
//     bytes written (matches AOSP semantics where return = serialized event
//     length).  This preserves the ONLY observable side effect that 99% of
//     Android apps care about — that calling writeEvent doesn't crash and
//     leaves a trace in the system log buffer.  The full binary event-log
//     protocol (with tag tables under /system/etc/event-log-tags) is NOT
//     reconstructed; doing so would require porting AOSP's liblog event-id
//     channel + logd, which is multi-week work.
//
//   * readEvents / readEventsOnWrapping — return immediately without
//     populating the output Collection.  AOSP semantic is "read from /dev/log
//     event channel"; OH has no such channel, so empty result is the correct
//     adapter behavior.  Apps that depend on EventLog readback (system tests,
//     telemetry tools) will see "no events" rather than crash.
//
// Spec: doc/window_manager_ipc_adapter_design.html §6 (added 2026-04-30 G2.10)
// ============================================================================

#include "AndroidRuntime.h"

#include <jni.h>
#include <cstdint>
#include <cstdio>
#include <cstring>
#include <utility>

// OH HiLog direct entry — same pattern as android_util_Log.cpp uses
// (project-wide convention: prefer innerAPI over NDK variant; see
// feedback_prefer_inner_api.md).
extern "C" int HiLogPrint(int type, int level, unsigned int domain,
                          const char* tag, const char* fmt, ...)
    __attribute__((__format__(printf, 5, 6)));

// HiLog constants matching android_util_Log.cpp:
//   LOG_TYPE_CORE = 3
//   LOG_INFO      = 4
//   domain 0xD002000 = LOG_APP equivalent
static constexpr int kHiLogTypeCore = 3;
static constexpr int kHiLogLevelInfo = 4;
static constexpr unsigned int kHiLogDomainEvent = 0xD000F00;
static constexpr const char* kEventLogTag = "AndroidEventLog";

namespace android {
namespace {

// ----------------------------------------------------------------------------
// writeEvent overloads — log to HiLog, return byte-count estimate.
//
// Return value = approximate serialized payload size (header 8B + value-specific
// bytes).  This matches AOSP's android_log_event_list::write(LOG_ID_EVENTS)
// return semantics closely enough that no caller observes wrong behavior:
// AOSP callers either ignore the return or check `>= 0` for "ok".
// ----------------------------------------------------------------------------

jint JNICALL
EventLog_writeEventInteger(JNIEnv* /*env*/, jobject /*clazz*/,
                            jint tag, jint value) {
    HiLogPrint(kHiLogTypeCore, kHiLogLevelInfo, kHiLogDomainEvent,
               kEventLogTag, "[tag=%d] int=%d", (int)tag, (int)value);
    return 12;  // 4B tag + 4B type + 4B int payload
}

jint JNICALL
EventLog_writeEventLong(JNIEnv* /*env*/, jobject /*clazz*/,
                         jint tag, jlong value) {
    HiLogPrint(kHiLogTypeCore, kHiLogLevelInfo, kHiLogDomainEvent,
               kEventLogTag, "[tag=%d] long=%lld",
               (int)tag, (long long)value);
    return 16;  // 4B tag + 4B type + 8B long payload
}

jint JNICALL
EventLog_writeEventFloat(JNIEnv* /*env*/, jobject /*clazz*/,
                          jint tag, jfloat value) {
    HiLogPrint(kHiLogTypeCore, kHiLogLevelInfo, kHiLogDomainEvent,
               kEventLogTag, "[tag=%d] float=%f", (int)tag, (double)value);
    return 12;  // 4B tag + 4B type + 4B float payload
}

jint JNICALL
EventLog_writeEventString(JNIEnv* env, jobject /*clazz*/,
                           jint tag, jstring valueObj) {
    if (valueObj == nullptr) {
        HiLogPrint(kHiLogTypeCore, kHiLogLevelInfo, kHiLogDomainEvent,
                   kEventLogTag, "[tag=%d] str=NULL", (int)tag);
        return 12;  // 4B tag + 4B type + 4B "NULL" sentinel
    }
    const char* utf = env->GetStringUTFChars(valueObj, nullptr);
    jsize len = (utf != nullptr) ? (jsize)strlen(utf) : 0;
    HiLogPrint(kHiLogTypeCore, kHiLogLevelInfo, kHiLogDomainEvent,
               kEventLogTag, "[tag=%d] str=\"%s\"",
               (int)tag, utf ? utf : "");
    if (utf != nullptr) {
        env->ReleaseStringUTFChars(valueObj, utf);
    }
    return 8 + 4 + len;  // 4B tag + 4B type + 4B length prefix + utf8 bytes
}

// writeEventArray — Object[] payload.  AOSP supports Integer / Long / Float /
// String elements (see eventlog_helper.h::writeEventArray).  We render each
// element to a comma-separated string for the HiLog line.  Return the
// estimated byte count of the serialized form.  Cap at 255 elements like
// AOSP for parity.
jint JNICALL
EventLog_writeEventArray(JNIEnv* env, jobject /*clazz*/,
                          jint tag, jobjectArray valueArr) {
    if (valueArr == nullptr) {
        HiLogPrint(kHiLogTypeCore, kHiLogLevelInfo, kHiLogDomainEvent,
                   kEventLogTag, "[tag=%d] arr=[NULL]", (int)tag);
        return 16;  // header + "[NULL]" sentinel
    }

    // Build a single line by appending each element's string form.  Buffer
    // sized to AOSP's LOGGER_ENTRY_MAX_PAYLOAD (4068) minus header reserve.
    constexpr int kMaxBuf = 3800;
    char line[kMaxBuf];
    int pos = 0;

    auto appendf = [&](const char* fmt, auto&&... args) {
        if (pos >= kMaxBuf - 8) return;
        int n = snprintf(line + pos, kMaxBuf - pos - 1, fmt,
                         std::forward<decltype(args)>(args)...);
        if (n < 0) return;
        if (n > kMaxBuf - pos - 1) n = kMaxBuf - pos - 1;
        pos += n;
    };

    appendf("[tag=%d] arr=[", (int)tag);

    jsize num = env->GetArrayLength(valueArr);
    if (num > 255) num = 255;  // AOSP cap
    int byteCount = 8 + 4;     // tag + type + length prefix

    jclass kInteger = env->FindClass("java/lang/Integer");
    jclass kLong    = env->FindClass("java/lang/Long");
    jclass kFloat   = env->FindClass("java/lang/Float");
    jclass kString  = env->FindClass("java/lang/String");
    jmethodID mIntValue   = (kInteger != nullptr)
        ? env->GetMethodID(kInteger, "intValue", "()I")    : nullptr;
    jmethodID mLongValue  = (kLong != nullptr)
        ? env->GetMethodID(kLong,    "longValue", "()J")   : nullptr;
    jmethodID mFloatValue = (kFloat != nullptr)
        ? env->GetMethodID(kFloat,   "floatValue", "()F")  : nullptr;

    for (jsize i = 0; i < num; ++i) {
        if (i > 0) appendf(",");
        jobject elt = env->GetObjectArrayElement(valueArr, i);
        if (elt == nullptr) {
            appendf("NULL");
            byteCount += 4;
        } else if (kString != nullptr && env->IsInstanceOf(elt, kString)) {
            jstring sObj = (jstring)elt;
            const char* utf = env->GetStringUTFChars(sObj, nullptr);
            jsize sLen = utf ? (jsize)strlen(utf) : 0;
            appendf("\"%s\"", utf ? utf : "");
            if (utf) env->ReleaseStringUTFChars(sObj, utf);
            byteCount += 4 + sLen;
        } else if (kInteger != nullptr && mIntValue != nullptr
                   && env->IsInstanceOf(elt, kInteger)) {
            jint v = env->CallIntMethod(elt, mIntValue);
            appendf("%d", (int)v);
            byteCount += 8;
        } else if (kLong != nullptr && mLongValue != nullptr
                   && env->IsInstanceOf(elt, kLong)) {
            jlong v = env->CallLongMethod(elt, mLongValue);
            appendf("%lldL", (long long)v);
            byteCount += 12;
        } else if (kFloat != nullptr && mFloatValue != nullptr
                   && env->IsInstanceOf(elt, kFloat)) {
            jfloat v = env->CallFloatMethod(elt, mFloatValue);
            appendf("%fF", (double)v);
            byteCount += 8;
        } else {
            // AOSP throws IllegalArgumentException for unsupported types.
            // Match that here for behavioral parity.
            env->DeleteLocalRef(elt);
            if (kInteger) env->DeleteLocalRef(kInteger);
            if (kLong)    env->DeleteLocalRef(kLong);
            if (kFloat)   env->DeleteLocalRef(kFloat);
            if (kString)  env->DeleteLocalRef(kString);
            jclass exClz = env->FindClass("java/lang/IllegalArgumentException");
            if (exClz != nullptr) {
                env->ThrowNew(exClz, "Invalid payload item type");
                env->DeleteLocalRef(exClz);
            }
            return -1;
        }
        env->DeleteLocalRef(elt);
    }

    if (kInteger) env->DeleteLocalRef(kInteger);
    if (kLong)    env->DeleteLocalRef(kLong);
    if (kFloat)   env->DeleteLocalRef(kFloat);
    if (kString)  env->DeleteLocalRef(kString);

    appendf("]");
    HiLogPrint(kHiLogTypeCore, kHiLogLevelInfo, kHiLogDomainEvent,
               kEventLogTag, "%s", line);
    return byteCount;
}

// ----------------------------------------------------------------------------
// readEvents / readEventsOnWrapping — no event channel on OH, return empty.
//
// AOSP behavior: read from /dev/log event channel via android_logger_list_*,
// populate the output Collection<Event>.  OH has no such channel.  The
// adapter-correct response is "no events available" — return without throwing,
// without adding anything to the collection.  Apps that read EventLog (mostly
// system tests / telemetry) will see an empty result and either skip or
// log "no data".  None of the apps in our target set crash on empty result.
// ----------------------------------------------------------------------------

void JNICALL
EventLog_readEvents(JNIEnv* /*env*/, jobject /*clazz*/,
                     jintArray /*tags*/, jobject /*out*/) {
    // Intentionally empty — no event channel on OH.
}

void JNICALL
EventLog_readEventsOnWrapping(JNIEnv* /*env*/, jobject /*clazz*/,
                               jintArray /*tags*/, jlong /*timestamp*/,
                               jobject /*out*/) {
    // Intentionally empty — no event channel on OH.
}

// ----------------------------------------------------------------------------
// JNI registration table.
// ----------------------------------------------------------------------------

const JNINativeMethod kEventLogMethods[] = {
    { "writeEvent", "(II)I",
      reinterpret_cast<void*>(EventLog_writeEventInteger) },
    { "writeEvent", "(IJ)I",
      reinterpret_cast<void*>(EventLog_writeEventLong) },
    { "writeEvent", "(IF)I",
      reinterpret_cast<void*>(EventLog_writeEventFloat) },
    { "writeEvent", "(ILjava/lang/String;)I",
      reinterpret_cast<void*>(EventLog_writeEventString) },
    { "writeEvent", "(I[Ljava/lang/Object;)I",
      reinterpret_cast<void*>(EventLog_writeEventArray) },
    { "readEvents", "([ILjava/util/Collection;)V",
      reinterpret_cast<void*>(EventLog_readEvents) },
    { "readEventsOnWrapping", "([IJLjava/util/Collection;)V",
      reinterpret_cast<void*>(EventLog_readEventsOnWrapping) },
};

}  // namespace

int register_android_util_EventLog(JNIEnv* env) {
    jclass clazz = env->FindClass("android/util/EventLog");
    if (clazz == nullptr) {
        fprintf(stderr, "[liboh_android_runtime] register_android_util_EventLog:"
                        " FindClass(android/util/EventLog) failed\n");
        return -1;
    }
    jint rc = env->RegisterNatives(clazz, kEventLogMethods,
                                    sizeof(kEventLogMethods)
                                        / sizeof(kEventLogMethods[0]));
    env->DeleteLocalRef(clazz);
    if (rc != JNI_OK) {
        fprintf(stderr, "[liboh_android_runtime] register_android_util_EventLog:"
                        " RegisterNatives rc=%d\n", (int)rc);
        return -1;
    }
    fprintf(stderr, "[liboh_android_runtime] register_android_util_EventLog:"
                    " 7 methods (5 writeEvent overloads + 2 readEvents)\n");
    return 0;
}

}  // namespace android
