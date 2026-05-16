/*
 * android_log_hilog_bridge.cpp
 *
 * Bridges Android liblog to OpenHarmony hilog innerAPI.
 *
 * This file is compiled as part of liblog.so (added to the source list in
 * cross_compile_arm32.sh). At .so load time, the constructor automatically
 * calls __android_log_set_logger() to route ALL Android log calls (Java
 * Log.d(), native ALOGI(), ART internal logging) through OH hilog.
 *
 * Runtime dependency: libhilog.so (innerAPI, at /system/lib/platformsdk/ on DAYU200).
 * Not libhilog_ndk.z.so (NDK variant, at /system/lib/ndk/) — per project
 * memory rule feedback_prefer_inner_api.md: adapter system .so must link the
 * innerAPI/platformsdk hilog, NDK variant is for 3rd-party app native code.
 *
 * 2026-04-17: converted from NDK OH_LOG_Print → innerAPI HiLogPrint. liblog.so
 * was failing to resolve OH_LOG_Print at runtime because libhilog_ndk.z.so is
 * not in the default LD search path for system processes (foundation/
 * appspawn-x). HiLogPrint in /system/lib/platformsdk/libhilog.so IS on the
 * default system search path.
 *
 * Design: doc/debug_bridge_design.html (Ch.3.2, Ch.4.2)
 *
 * OH hilog innerAPI signature verified against:
 *   oh/base/hiviewdfx/hilog/interfaces/native/innerkits/include/hilog/log_c.h
 */

#include <android/log.h>  // __android_log_message, __android_log_set_logger, android_LogPriority

// ---------------------------------------------------------------------------
// OH hilog innerAPI declaration.
// Declared inline rather than #include <hilog/log_c.h> to avoid pulling in OH
// enum names (LOG_APP / LOG_CORE / LOG_DEBUG / ...) that would collide with
// AOSP liblog's own enum value namespace during cross-compilation.
// Signature matches libhilog.so export (see log_c.h):
//   int HiLogPrint(LogType type, LogLevel level, unsigned int domain,
//                  const char *tag, const char *fmt, ...)
// ---------------------------------------------------------------------------

// LogType enum values (from hilog/log_c.h)
#define OH_LOG_APP  0   // LOG_APP: for application logs (domain 0-0xFFFF)
#define OH_LOG_CORE 3   // LOG_CORE: for core service / framework logs (domain 0xD000000-0xD0FFFFF)

// LogLevel enum values (from hilog/log_c.h)
#define OH_LOG_DEBUG 3
#define OH_LOG_INFO  4
#define OH_LOG_WARN  5
#define OH_LOG_ERROR 6
#define OH_LOG_FATAL 7

#ifdef __cplusplus
extern "C" {
#endif

// HiLogPrint: main logging function in libhilog.so (innerAPI).
// Declared with int params to avoid redefining the LogType/LogLevel enums
// that OH headers would introduce (name collision with AOSP liblog).
int HiLogPrint(int type, int level, unsigned int domain,
               const char *tag, const char *fmt, ...)
    __attribute__((__format__(printf, 5, 6)));

#ifdef __cplusplus
}
#endif

// ---------------------------------------------------------------------------
// Android log priority -> OH hilog level mapping
// Android: VERBOSE=2, DEBUG=3, INFO=4, WARN=5, ERROR=6, FATAL=7
// OH:      (none=2),  DEBUG=3, INFO=4, WARN=5, ERROR=6, FATAL=7
// VERBOSE maps to DEBUG (OH has no VERBOSE level).
// ---------------------------------------------------------------------------
static int android_to_oh_level(int android_prio) {
    switch (android_prio) {
        case ANDROID_LOG_VERBOSE: return OH_LOG_DEBUG;   // 2 -> 3
        case ANDROID_LOG_DEBUG:   return OH_LOG_DEBUG;   // 3 -> 3
        case ANDROID_LOG_INFO:    return OH_LOG_INFO;    // 4 -> 4
        case ANDROID_LOG_WARN:    return OH_LOG_WARN;    // 5 -> 5
        case ANDROID_LOG_ERROR:   return OH_LOG_ERROR;   // 6 -> 6
        case ANDROID_LOG_FATAL:   return OH_LOG_FATAL;   // 7 -> 7
        default:                  return OH_LOG_INFO;
    }
}

// Fixed OH domain for all Android-side logs.
// 2026-05-09 G2.14ah: switched from 0xD002000u to 0xD000F00u — the former
// is filtered out of `hilog -x` output by OH default cfg (no OH_GfxShim /
// OH_SurfaceControl / OH_AssetManager / OH_Parcel ALOGI ever appeared),
// while 0xD000F00u is the same domain used by OH_AppSchedulerAdapter /
// OH_WindowMgrClient etc. and is fully visible.
#define ANDROID_HILOG_DOMAIN 0xD000F00u

// ---------------------------------------------------------------------------
// The logger function, matching __android_logger_function signature:
//   void (*)(const struct __android_log_message* log_message)
//
// Called by __android_log_write_log_message() for every log call that passes
// the priority filter. Routes the message to OH hilog via OH_LOG_Print().
// ---------------------------------------------------------------------------
static void __android_log_hilog_logger(
        const struct __android_log_message *log_message) {
    // Use OH_LOG_APP (0) as log type so logs appear in the "app" category
    // in hdc shell hilog output.
    HiLogPrint(OH_LOG_APP,
               android_to_oh_level(log_message->priority),
               ANDROID_HILOG_DOMAIN,
               log_message->tag ? log_message->tag : "AndroidLog",
               "%s",
               log_message->message ? log_message->message : "");
}

// ---------------------------------------------------------------------------
// Auto-register at .so load time.
//
// When liblog.so is loaded (by any .so that uses __android_log_* APIs),
// this constructor runs and sets our hilog logger as the active transport.
// This replaces the default logd logger (which writes to a dead socket on OH)
// or the file_logger (which writes to stderr).
//
// If libhilog.so (innerAPI, /system/lib/platformsdk/) is not present at
// runtime, the linker will fail to load liblog.so entirely — which is the
// correct behavior since on a non-OH system we wouldn't be using this bridge.
// ---------------------------------------------------------------------------
__attribute__((constructor))
static void register_hilog_logger(void) {
    __android_log_set_logger(__android_log_hilog_logger);
}
