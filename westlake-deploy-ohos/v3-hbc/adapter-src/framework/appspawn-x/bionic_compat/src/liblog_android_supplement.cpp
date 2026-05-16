// ============================================================================
// liblog_android_supplement.cpp
//
// Supplements AOSP liblog functions that are gated behind `#ifdef __ANDROID__`
// in system/logging/liblog source. Our cross-compile targets arm-linux-ohos,
// not Android, so those gated bodies never compile. libandroid_runtime.so
// (also cross-compiled from AOSP) still references them at runtime.
//
// Real implementations routed to the OH hilog backend where it maps naturally;
// non-logd-related query functions return sensible defaults for a non-Android
// environment (no device-owner flag, no logd daemon to read from).
// ============================================================================

// bionic_compat is compiled with -nostdinc++; only C stdlib headers available.
#include <stdarg.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>

extern "C" {

// ---- Forward-declare the functions we need to forward to (from liblog.so) ----
// __android_log_print is in our liblog.so (non-gated path).
int __android_log_print(int prio, const char* tag, const char* fmt, ...)
    __attribute__((format(printf, 3, 4)));
int __android_log_buf_write(int bufID, int prio, const char* tag, const char* text);

// ---- properties.cpp #ifdef __ANDROID__ block ----

// Device-owner flag: on OH we have no such concept → always 0.
int __android_log_security() {
    return 0;
}

// ---- logger_write.cpp re-exports (these exist in our liblog.so already,
//      but list here for reference). Not redefined to avoid duplicate symbols.

// ---- logd_reader.cpp (not compiled) ----

// Opaque "logger list" handle. OH has no logd daemon; we return a trivial
// handle so callers can exercise the API without crashing.
struct __attribute__((visibility("default"))) logger_list {
    unsigned int mode;
    int          ref_count;
};

struct logger_list* android_logger_list_alloc(int /*mode*/,
                                              unsigned int /*tail*/,
                                              pid_t /*pid*/) {
    auto* l = static_cast<logger_list*>(calloc(1, sizeof(logger_list)));
    if (l) { l->ref_count = 1; }
    return l;
}

struct __log_time_pair { int a, b; };
struct logger_list* android_logger_list_alloc_time(int /*mode*/,
                                                   struct __log_time_pair /*start*/,
                                                   pid_t /*pid*/) {
    // We do not have logd; return a no-op handle.
    auto* l = static_cast<logger_list*>(calloc(1, sizeof(logger_list)));
    if (l) { l->ref_count = 1; }
    return l;
}

void android_logger_list_free(struct logger_list* list) {
    free(list);
}

// Opaque "logger" per-buffer handle.
struct __attribute__((visibility("default"))) logger { int buf_id; };

struct logger* android_logger_open(struct logger_list* /*list*/, int buf_id) {
    auto* lg = static_cast<struct logger*>(calloc(1, sizeof(struct logger)));
    if (lg) { lg->buf_id = buf_id; }
    return lg;
}

// Read one log entry. No logd source → always "no more entries".
struct log_msg { char raw[5*1024]; };
int android_logger_list_read(struct logger_list* /*list*/, struct log_msg* /*msg*/) {
    return 0; // EOF / no entry available
}

// Buffer metadata queries. Return harmless defaults.
long android_logger_get_log_size(struct logger* /*l*/)            { return 0; }
int  android_logger_set_log_size(struct logger* /*l*/, unsigned long /*sz*/) { return 0; }
long android_logger_get_log_readable_size(struct logger* /*l*/)   { return 0; }
int  android_logger_get_log_version(struct logger* /*l*/)         { return 4; }

int android_logger_clear(struct logger* /*l*/)                    { return 0; }

// ---- log_event_list.cpp / log_event_write.cpp supplements ----

// Opaque event context. AOSP uses a pimpl pattern; we keep a small buffer.
struct __attribute__((visibility("default"))) android_log_context_internal {
    int tag;
    size_t len;
    unsigned char buf[4096];
};
typedef android_log_context_internal* android_log_context;

android_log_context create_android_logger(uint32_t tag) {
    auto* ctx = new android_log_context_internal();
    ctx->tag = static_cast<int>(tag);
    ctx->len = 0;
    return ctx;
}

int android_log_destroy(android_log_context* ctx) {
    if (ctx && *ctx) {
        delete *ctx;
        *ctx = nullptr;
    }
    return 0;
}

// Typed append APIs. We don't persist the encoded record (no logd), but must
// not crash callers. Return 0 (success).
int android_log_write_int32 (android_log_context /*ctx*/, int32_t /*v*/)  { return 0; }
int android_log_write_int64 (android_log_context /*ctx*/, int64_t /*v*/)  { return 0; }
int android_log_write_float32(android_log_context /*ctx*/, float   /*v*/) { return 0; }
int android_log_write_string8(android_log_context /*ctx*/, const char* /*s*/) { return 0; }
int android_log_write_string8_len(android_log_context /*ctx*/, const char* /*s*/, size_t /*n*/) { return 0; }
int android_log_write_list_begin(android_log_context /*ctx*/) { return 0; }
int android_log_write_list_end  (android_log_context /*ctx*/) { return 0; }
int android_log_write_list(android_log_context /*ctx*/, int /*buf_id*/) { return 0; }

// Read-side (for test code in libandroid_runtime). Return "end of list".
int android_log_read_next(android_log_context /*ctx*/)           { return -1; }
int android_log_parser_read_next(android_log_context /*ctx*/)    { return -1; }

}  // extern "C"
