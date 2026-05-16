// ============================================================================
// android_util_Log.cpp
//
// JNI bindings for android.util.Log. Mirrors AOSP
// frameworks/base/core/jni/android_util_Log.cpp, but routes through our
// cross-compiled liblog.so (which forwards to OH hilog via
// android_log_hilog_bridge.cpp — see liblog's JNI_OnLoad).
//
// Java side (AOSP):
//   public static native boolean isLoggable(String tag, int level);
//   public static native int println_native(int buffer, int priority,
//                                            String tag, String msg);
//   private static native int logger_entry_max_payload_native();
// ============================================================================

#include "AndroidRuntime.h"

#include <android/log.h>
#include <cstring>
#include <jni.h>

// From our cross-compiled liblog.so:
extern "C" int __android_log_buf_write(int bufID, int prio,
                                        const char* tag, const char* text);
extern "C" int __android_log_is_loggable(int prio, const char* tag, int default_prio);

// B.37 (2026-04-29): direct HiLogPrint bypass for child diagnostics.
// liblog's __android_log_print → liblog→hilog bridge is broken in fork
// child (per feedback.txt P3 backlog), so all AOSP framework Log.i/d/e
// calls silently disappear in the child process.  Route Log.println_native
// directly to HiLogPrint to make AOSP framework logs visible in child.
extern "C" int HiLogPrint(int type, int level, unsigned int domain,
                          const char* tag, const char* fmt, ...)
    __attribute__((__format__(printf, 5, 6)));

// Android Log priority → OH HiLog level mapping
//   ANDROID_LOG_VERBOSE=2 → LOG_DEBUG=3
//   ANDROID_LOG_DEBUG=3   → LOG_DEBUG=3
//   ANDROID_LOG_INFO=4    → LOG_INFO=4
//   ANDROID_LOG_WARN=5    → LOG_WARN=5
//   ANDROID_LOG_ERROR=6   → LOG_ERROR=6
//   ANDROID_LOG_FATAL=7   → LOG_FATAL=7
// LOG_TYPE_CORE=3 (HiLog type), domain 0xD002000 (= LOG_APP equivalent).
static int b37AndroidPrioToHiLogLevel(int prio) {
    switch (prio) {
        case 2: return 3;   // VERBOSE → DEBUG
        case 3: return 3;   // DEBUG
        case 4: return 4;   // INFO
        case 5: return 5;   // WARN
        case 6: return 6;   // ERROR
        case 7: return 7;   // FATAL
        default: return 4;
    }
}

// AOSP's LOGGER_ENTRY_MAX_PAYLOAD is 4068 bytes (liblog private header).
// We match that value so Java-side Log buffers size to the same assumption.
static constexpr jint kLoggerEntryMaxPayload = 4068;

namespace android {

namespace {

jboolean JNICALL
Log_isLoggable(JNIEnv* env, jclass /*clazz*/, jstring tagObj, jint level) {
    if (tagObj == nullptr) {
        return JNI_FALSE;
    }
    const char* tag = env->GetStringUTFChars(tagObj, nullptr);
    if (!tag) {
        return JNI_FALSE;
    }
    int rc = __android_log_is_loggable(level, tag, ANDROID_LOG_INFO);
    env->ReleaseStringUTFChars(tagObj, tag);
    return rc ? JNI_TRUE : JNI_FALSE;
}

jint JNICALL
Log_println_native(JNIEnv* env, jclass /*clazz*/,
                    jint bufID, jint priority, jstring tagObj, jstring msgObj) {
    if (msgObj == nullptr) {
        // Match AOSP: throw NPE via straight return; Java layer also guards.
        return -1;
    }
    // bufID range: MAIN(0)/RADIO(1)/EVENTS(2)/SYSTEM(3)/CRASH(4)/STATS(5)/
    // SECURITY(6)/KERNEL(7) per AOSP android_LogId.
    if (bufID < 0 || bufID > 7) {
        return -1;
    }

    const char* tag = nullptr;
    if (tagObj) {
        tag = env->GetStringUTFChars(tagObj, nullptr);
    }
    const char* msg = env->GetStringUTFChars(msgObj, nullptr);

    // B.37: bypass __android_log_buf_write (broken in child fork). Route
    // direct to HiLogPrint with the original Java priority/tag.  The bufID
    // mapping is collapsed: all bufIDs go to the same hilog domain;
    // distinguishing MAIN/SYSTEM/EVENTS would need separate hilog domains
    // which OH doesn't offer at app-level.  Tag is preserved.
    int hLevel = b37AndroidPrioToHiLogLevel(priority);
    // B.37: use 0xD000F00 (same as AppSpawnX direct logs) to avoid any
    // app-domain filtering and keep all child diagnostics in one stream.
    HiLogPrint(3 /*LOG_TYPE_CORE*/, hLevel, 0xD000F00u,
               tag ? tag : "AndroidLog",
               "%{public}s", msg ? msg : "");
    int rc = static_cast<int>(msg ? strlen(msg) : 0);

    if (tag) env->ReleaseStringUTFChars(tagObj, tag);
    if (msg) env->ReleaseStringUTFChars(msgObj, msg);
    return rc;
}

jint JNICALL
Log_logger_entry_max_payload_native(JNIEnv* /*env*/, jclass /*clazz*/) {
    return kLoggerEntryMaxPayload;
}

const JNINativeMethod kLogMethods[] = {
    { "isLoggable",
      "(Ljava/lang/String;I)Z",
      reinterpret_cast<void*>(Log_isLoggable) },
    { "println_native",
      "(IILjava/lang/String;Ljava/lang/String;)I",
      reinterpret_cast<void*>(Log_println_native) },
    { "logger_entry_max_payload_native",
      "()I",
      reinterpret_cast<void*>(Log_logger_entry_max_payload_native) },
};

}  // namespace

int register_android_util_Log(JNIEnv* env) {
    jclass clazz = env->FindClass("android/util/Log");
    if (!clazz) {
        return -1;
    }
    jint rc = env->RegisterNatives(clazz, kLogMethods,
                                    sizeof(kLogMethods) / sizeof(kLogMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
