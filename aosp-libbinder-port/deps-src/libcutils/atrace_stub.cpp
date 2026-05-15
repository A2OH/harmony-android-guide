// libbinder-port: atrace stub - tracing is a no-op since OHOS has no atrace
// daemon and we don't need tracing for binder IPC unit tests.

#include <stdint.h>

extern "C" {

uint64_t atrace_get_enabled_tags() {
    return 0;  // No tags enabled — disables all atrace_begin/end fast-paths.
}

void atrace_begin_body(const char* /*name*/) {}
void atrace_end_body() {}
void atrace_async_begin_body(const char* /*name*/, int32_t /*cookie*/) {}
void atrace_async_end_body(const char* /*name*/, int32_t /*cookie*/) {}
void atrace_async_for_track_begin_body(const char* /*track_name*/, const char* /*name*/, int32_t /*cookie*/) {}
void atrace_async_for_track_end_body(const char* /*track_name*/, int32_t /*cookie*/) {}
void atrace_instant_body(const char* /*name*/) {}
void atrace_instant_for_track_body(const char* /*track_name*/, const char* /*name*/) {}
void atrace_int_body(const char* /*name*/, int32_t /*value*/) {}
void atrace_int64_body(const char* /*name*/, int64_t /*value*/) {}

} // extern "C"
