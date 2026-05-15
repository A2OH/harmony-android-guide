// Bionic-build prefix header (compile-time -include).
//
// NDK's <android/binder_internal_logging.h> pulls in <syslog.h>, which
// defines `LOG_INFO 6`, `LOG_WARN 4`, etc.  liblog_stub/log.h then expands
// `ALOGI(...) -> ALOG(LOG_INFO, ...)` whose `ANDROID_##priority` token-paste
// produces `ANDROID_6` instead of `ANDROID_LOG_INFO`.
//
// To prevent the syslog macros from leaking into the libbinder source, we
// pre-undef them in a -include prefix header so any later include of
// <syslog.h> via binder NDK headers triggers redefinitions back, but the
// concrete token expansion in our macros uses the symbolic name from
// <android/log.h> (which liblog_stub also includes).
//
// Order of include resolution at compile time:
//   1. (-include) THIS file: nothing yet defines LOG_INFO.
//   2. <android/log.h> via liblog_stub/log.h: defines ANDROID_LOG_INFO enum.
//   3. (somewhere later) <syslog.h> via <android/binder_internal_logging.h>:
//      tries to define `LOG_INFO 6` — but we've pre-undef'd it via the
//      pragma so the existing definition wins... actually #define still
//      replaces.  Instead we use a different mitigation: keep LOG_INFO
//      as an int and never use it in macros.
//
// Cleanest workaround: define a guard that makes liblog_stub stop using
// ALOG_##priority token paste.  But liblog_stub is what libbinder uses, so
// patching the stub itself is simpler than chasing every -include path.
// We do NOT need to touch this file for that approach; we just patch the
// stub instead.  Keeping this file as a marker / future-proofing.

#pragma once
