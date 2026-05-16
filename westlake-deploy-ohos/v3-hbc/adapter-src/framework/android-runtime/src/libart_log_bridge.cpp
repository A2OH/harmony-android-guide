// ============================================================================
// libart_log_bridge.cpp
//
// Bridges libart's android::base::LogMessage / LOG(FATAL) / CHECK output
// and __android_log_set_aborter callback chain to OH hilog (innerAPI
// HiLogPrint).  Compiled into liboh_android_runtime.so.  Constructor runs
// at .so load time and installs:
//
//   - android::base::SetLogger(...)   — captures all libart LOG(INFO/.../FATAL)
//                                        and CHECK(...) DCHECK(...) output
//   - android::base::SetAborter(...)  — captures the abort message right
//                                        before std::abort() is called
//
// Together with bionic_compat's __android_log_set_aborter (separately fixed
// to retain the callback rather than no-op), this closes the silent ART
// abort gap documented in doc/debug_bridge_design.html §3.8.
//
// Spec: doc/debug_bridge_design.html §4.7
// Memory: feedback_prefer_inner_api.md (link innerAPI libhilog, not NDK)
// ============================================================================

#include <android-base/logging.h>

#include <cstdio>
#include <cstdlib>
#include <cstring>

// HiLogPrint: OH innerAPI from libhilog.so (/system/lib/platformsdk/).
// Declared inline rather than via #include <hilog/log_c.h> to avoid the
// OH enum names colliding with AOSP liblog's enum namespace.
extern "C" int HiLogPrint(int type, int level, unsigned int domain,
                          const char* tag, const char* fmt, ...)
    __attribute__((__format__(printf, 5, 6)));

namespace {

// LogType from hilog/log_c.h: LOG_APP=0, LOG_INIT=1, LOG_CORE=3.
constexpr int kOhLogTypeCore = 3;

// LogLevel from hilog/log_c.h: DEBUG=3, INFO=4, WARN=5, ERROR=6, FATAL=7.
constexpr int kOhLevelDebug = 3;
constexpr int kOhLevelInfo  = 4;
constexpr int kOhLevelWarn  = 5;
constexpr int kOhLevelError = 6;
constexpr int kOhLevelFatal = 7;

// Domain shared with adapter standard 0xD000F00 so AOSP framework + native +
// ART logs show up alongside OH_AppSchedulerAdapter / OH_WindowMgrClient etc.
// (was 0xD002000u — that domain is filtered out of hilog -x output by OH
// default config; switched 2026-05-09 G2.14ah to make ALOGI visible.)
constexpr unsigned int kAndroidHilogDomain = 0xD000F00u;

int SeverityToOhLevel(android::base::LogSeverity sev) {
    switch (sev) {
        case android::base::VERBOSE: return kOhLevelDebug;
        case android::base::DEBUG:   return kOhLevelDebug;
        case android::base::INFO:    return kOhLevelInfo;
        case android::base::WARNING: return kOhLevelWarn;
        case android::base::ERROR:   return kOhLevelError;
        case android::base::FATAL_WITHOUT_ABORT:
        case android::base::FATAL:   return kOhLevelFatal;
        default:                     return kOhLevelInfo;
    }
}

// Logger callback: matches LogFunction type
//   void(LogId, LogSeverity, const char* tag, const char* file,
//        unsigned line, const char* message)
void HiLogLogger(android::base::LogId /*id*/,
                 android::base::LogSeverity severity,
                 const char* tag,
                 const char* file,
                 unsigned int line,
                 const char* message) {
    HiLogPrint(kOhLogTypeCore,
               SeverityToOhLevel(severity),
               kAndroidHilogDomain,
               tag ? tag : "libart",
               "[%s:%u] %s",
               file ? file : "?",
               line,
               message ? message : "");
    // For FATAL severity also dump to native stderr (which child_main.cpp
    // §4.6 has redirected to /data/service/el1/public/appspawnx/adapter_child_<pid>.stderr).
    // This gives a second post-mortem trail in case hilog buffer is rotated.
    if (severity == android::base::FATAL ||
        severity == android::base::FATAL_WITHOUT_ABORT) {
        std::fprintf(stderr, "FATAL %s [%s:%u] %s\n",
                     tag ? tag : "libart",
                     file ? file : "?",
                     line,
                     message ? message : "");
        std::fflush(stderr);
    }
}

// Aborter callback: invoked right before std::abort().  Last chance to
// surface the message via hilog before the SIGABRT freezes the process.
void HiLogAborter(const char* abort_message) {
    HiLogPrint(kOhLogTypeCore,
               kOhLevelFatal,
               kAndroidHilogDomain,
               "libart",
               "ABORT: %s",
               abort_message ? abort_message : "(null)");
    std::fprintf(stderr, "ABORT: %s\n",
                 abort_message ? abort_message : "(null)");
    std::fflush(stderr);
    std::abort();
}

__attribute__((constructor))
void InstallHiLogBridge() {
    android::base::SetLogger(HiLogLogger);
    android::base::SetAborter(HiLogAborter);
    // No own log here — the logger we just installed isn't expected to
    // be called from a constructor (libbase global state may not be ready).
    // First real use will be when ART itself starts logging.
}

}  // namespace

// ----------------------------------------------------------------------------
// Reach-around: many AOSP modules call __android_set_abort_message(msg) right
// before abort().  bionic provides this; on OH our libbionic_compat used to
// stub it as a no-op (art_runtime_stubs.cpp:1273).  That fix lives in
// bionic_compat (commit alongside this file); here we ALSO export a strong
// definition that wins over the bionic_compat weak stub when liboh_android_runtime
// is loaded earlier than libbionic_compat in the link order.  The definition
// forwards the message to hilog with FATAL severity so it is visible even if
// the abort happens before any C++ logging machinery gets a chance to fire.
// ----------------------------------------------------------------------------
extern "C" __attribute__((visibility("default")))
void __android_set_abort_message(const char* msg) {
    HiLogPrint(kOhLogTypeCore,
               kOhLevelFatal,
               kAndroidHilogDomain,
               "abort",
               "%s",
               msg ? msg : "(null)");
    std::fprintf(stderr, "ABORT(set_message): %s\n", msg ? msg : "(null)");
    std::fflush(stderr);
}
