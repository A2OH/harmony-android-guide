/*
 * Android thread priority definitions shim.
 */
#ifndef _SYSTEM_THREAD_DEFS_H
#define _SYSTEM_THREAD_DEFS_H

/* Android priority levels (nice values) */
#define ANDROID_PRIORITY_LOWEST         19
#define ANDROID_PRIORITY_BACKGROUND     10
#define ANDROID_PRIORITY_NORMAL          0
#define ANDROID_PRIORITY_FOREGROUND     -2
#define ANDROID_PRIORITY_DISPLAY        -4
#define ANDROID_PRIORITY_URGENT_DISPLAY -8
#define ANDROID_PRIORITY_AUDIO         -16
#define ANDROID_PRIORITY_URGENT_AUDIO  -19
#define ANDROID_PRIORITY_HIGHEST       -20

#endif /* _SYSTEM_THREAD_DEFS_H */
