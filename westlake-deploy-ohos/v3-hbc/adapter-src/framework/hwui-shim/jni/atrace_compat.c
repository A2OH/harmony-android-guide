// atrace_compat.c
//
// Real bridge from Android ATrace C API to OpenHarmony HiTraceMeter NDK API.
//
// Android's libhwui uses ATRACE_* macros (expanded to atrace_begin_body /
// atrace_end_body / atrace_int_body / atrace_int64_body / atrace_get_enabled_tags)
// from system/core/libcutils/include/cutils/trace.h. On AOSP those are
// implemented in trace-dev.cpp and shipped in libcutils.so; this project's
// cross-compiled libcutils.so doesn't include them.
//
// Instead of no-op stubs, we bridge to OH's real HiTraceMeter C NDK API:
//   OH_HiTrace_StartTrace(name)      ← atrace_begin_body(name)
//   OH_HiTrace_FinishTrace()         ← atrace_end_body()
//   OH_HiTrace_CountTrace(name, val) ← atrace_int_body(name, val)
//                                      atrace_int64_body(name, val)
//
// Runtime library: libhitrace_ndk.z.so (at /system/lib/ndk/ on DAYU200)
// Header: interface/sdk_c/hiviewdfx/hitrace/include/hitrace/trace.h
//
// atrace_get_enabled_tags returns ~0ULL (all tags enabled). This makes
// every ATRACE_ENABLED() gate in libhwui evaluate true, so trace calls
// always fire and actually emit events into OH's hitrace system — visible
// via `hitrace --trace_begin app` / `hitrace --trace_dump` on the device.

#include <stdint.h>

// OH HiTrace NDK C API declarations.
// Declared here inline rather than #include <hitrace/trace.h> to keep the
// compile command self-contained (no extra -I paths into OH NDK tree).
// Signatures match interface/sdk_c/hiviewdfx/hitrace/include/hitrace/trace.h
// (verified 2026-04-12 against libhitrace_ndk.z.so symbol exports).
extern void OH_HiTrace_StartTrace(const char *name);
extern void OH_HiTrace_FinishTrace(void);
extern void OH_HiTrace_CountTrace(const char *name, int64_t count);

void atrace_begin_body(const char *name) {
    OH_HiTrace_StartTrace(name);
}

void atrace_end_body(void) {
    OH_HiTrace_FinishTrace();
}

void atrace_int_body(const char *name, int32_t value) {
    OH_HiTrace_CountTrace(name, (int64_t)value);
}

void atrace_int64_body(const char *name, int64_t value) {
    OH_HiTrace_CountTrace(name, value);
}

uint64_t atrace_get_enabled_tags(void) {
    // All trace categories enabled: every ATRACE_ENABLED() check in libhwui
    // evaluates true, so atrace_begin_body/end_body/int_body are always
    // invoked. The actual trace filtering happens inside OH's HiTraceMeter.
    return ~(uint64_t)0;
}
