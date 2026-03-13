/*
 * Memory barrier macros for standalone Dalvik.
 */
#ifndef _CUTILS_MEMORY_BARRIER_H
#define _CUTILS_MEMORY_BARRIER_H

#define ANDROID_MEMBAR_FULL()      __atomic_thread_fence(__ATOMIC_SEQ_CST)
#define ANDROID_MEMBAR_STORE()     __atomic_thread_fence(__ATOMIC_RELEASE)
#define ANDROID_MEMBAR_LOAD()      __atomic_thread_fence(__ATOMIC_ACQUIRE)

#endif
