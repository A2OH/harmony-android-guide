/*
 * Minimal Android logging shim for standalone Dalvik.
 * Maps ALOG* macros to fprintf(stderr, ...).
 */
#ifndef _CUTILS_LOG_H
#define _CUTILS_LOG_H

#include <stdio.h>
#include <stdarg.h>
#include <stdlib.h>

#ifndef LOG_TAG
#define LOG_TAG "dalvikvm"
#endif

/* Log priority levels */
#define ANDROID_LOG_VERBOSE 2
#define ANDROID_LOG_DEBUG   3
#define ANDROID_LOG_INFO    4
#define ANDROID_LOG_WARN    5
#define ANDROID_LOG_ERROR   6
#define ANDROID_LOG_FATAL   7

/* Short aliases for ALOG() macro — must not conflict with enums */
#define LOG_VERBOSE ANDROID_LOG_VERBOSE
#define LOG_DEBUG   ANDROID_LOG_DEBUG
#define LOG_INFO    ANDROID_LOG_INFO
#define LOG_WARN    ANDROID_LOG_WARN
#define LOG_ERROR   ANDROID_LOG_ERROR
#define LOG_FATAL   ANDROID_LOG_FATAL

/* Core logging function */
static inline int __android_log_print(int prio, const char* tag, const char* fmt, ...)
    __attribute__((format(printf, 3, 4)));
static inline int __android_log_print(int prio, const char* tag, const char* fmt, ...) {
    const char* level;
    switch (prio) {
        case ANDROID_LOG_VERBOSE: level = "V"; break;
        case ANDROID_LOG_DEBUG:   level = "D"; break;
        case ANDROID_LOG_INFO:    level = "I"; break;
        case ANDROID_LOG_WARN:    level = "W"; break;
        case ANDROID_LOG_ERROR:   level = "E"; break;
        case ANDROID_LOG_FATAL:   level = "F"; break;
        default:                  level = "?"; break;
    }
    va_list ap;
    va_start(ap, fmt);
    fprintf(stderr, "[%s/%s] ", level, tag ? tag : "?");
    vfprintf(stderr, fmt, ap);
    fprintf(stderr, "\n");
    va_end(ap);
    return 0;
}

static inline int __android_log_vprint(int prio, const char* tag, const char* fmt, va_list ap) {
    const char* level;
    switch (prio) {
        case ANDROID_LOG_VERBOSE: level = "V"; break;
        case ANDROID_LOG_DEBUG:   level = "D"; break;
        case ANDROID_LOG_INFO:    level = "I"; break;
        case ANDROID_LOG_WARN:    level = "W"; break;
        case ANDROID_LOG_ERROR:   level = "E"; break;
        default:                  level = "?"; break;
    }
    fprintf(stderr, "[%s/%s] ", level, tag ? tag : "?");
    vfprintf(stderr, fmt, ap);
    fprintf(stderr, "\n");
    return 0;
}

/* ALOG* macros */
#define ALOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define ALOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,   LOG_TAG, __VA_ARGS__)
#define ALOGI(...)  __android_log_print(ANDROID_LOG_INFO,    LOG_TAG, __VA_ARGS__)
#define ALOGW(...)  __android_log_print(ANDROID_LOG_WARN,    LOG_TAG, __VA_ARGS__)
#define ALOGE(...)  __android_log_print(ANDROID_LOG_ERROR,   LOG_TAG, __VA_ARGS__)
#define ALOGF(...)  __android_log_print(ANDROID_LOG_FATAL,   LOG_TAG, __VA_ARGS__)
#define SLOGE(...)  ALOGE(__VA_ARGS__)
#define SLOGW(...)  ALOGW(__VA_ARGS__)

/* Generic ALOG */
#define ALOG(level, tag, ...) __android_log_print(level, tag, __VA_ARGS__)

/* Conditional macros */
#define IF_ALOGV() if (false)
#define IF_ALOGD() if (false)
#define IF_ALOGI() if (false)
#define IF_ALOGW() if (true)
#define IF_ALOGE() if (true)
#define IF_ALOG(level, tag) if ((level) >= ANDROID_LOG_WARN)

/* Fatal */
#define LOG_ALWAYS_FATAL(...)   do { ALOGE(__VA_ARGS__); abort(); } while(0)
#define LOG_FATAL_IF(cond, ...) do { if (cond) { ALOGE(__VA_ARGS__); abort(); } } while(0)
#define ALOG_ASSERT(cond, ...)  do { if (!(cond)) { ALOGE("ASSERT: " __VA_ARGS__); abort(); } } while(0)

/* LOG_PRI / LOG_PRI_VA */
#define LOG_PRI(priority, tag, ...) __android_log_print(priority, tag, __VA_ARGS__)
#define LOG_PRI_VA(prio, tag, fmt, ap) __android_log_vprint(prio, tag, fmt, ap)
#define android_printLog(prio, tag, ...) __android_log_print(prio, tag, __VA_ARGS__)

/* Event log stubs (Sync.cpp contention logging) */
#define EVENT_TYPE_INT    0
#define EVENT_TYPE_STRING 1
#define EVENT_TYPE_LIST   2
static inline int android_btWriteLog(int tag, int type, const void* payload, size_t len) {
    (void)tag; (void)type; (void)payload; (void)len;
    return 0;
}

/* JDWP stubs */
static inline int socket_peer_is_trusted(int fd) { (void)fd; return 1; }
static inline void android_setMinPriority(const char* tag, int prio) { (void)tag; (void)prio; }

/* Misc */
#ifndef OS_SHARED_LIB_FORMAT_STR
#define OS_SHARED_LIB_FORMAT_STR "lib%s.so"
#endif

#endif /* _CUTILS_LOG_H */
