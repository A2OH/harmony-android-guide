/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

/*
 * libbinder-port: cutils/atomic.h replacement that avoids
 * including <stdatomic.h> (whose C macros collide with libcxx-ohos
 * declarations).  Uses GCC/Clang __atomic_* builtins exclusively.
 */

#ifndef ANDROID_CUTILS_ATOMIC_H
#define ANDROID_CUTILS_ATOMIC_H

#include <stdint.h>
#include <sys/types.h>

#ifdef __cplusplus
extern "C" {
#endif

#ifndef ANDROID_ATOMIC_INLINE
#define ANDROID_ATOMIC_INLINE static inline
#endif

ANDROID_ATOMIC_INLINE
int32_t android_atomic_inc(volatile int32_t* addr) {
    return __atomic_fetch_add(addr, 1, __ATOMIC_RELEASE);
}
ANDROID_ATOMIC_INLINE
int32_t android_atomic_dec(volatile int32_t* addr) {
    return __atomic_fetch_sub(addr, 1, __ATOMIC_RELEASE);
}
ANDROID_ATOMIC_INLINE
int32_t android_atomic_add(int32_t value, volatile int32_t* addr) {
    return __atomic_fetch_add(addr, value, __ATOMIC_RELEASE);
}
ANDROID_ATOMIC_INLINE
int32_t android_atomic_and(int32_t value, volatile int32_t* addr) {
    return __atomic_fetch_and(addr, value, __ATOMIC_RELEASE);
}
ANDROID_ATOMIC_INLINE
int32_t android_atomic_or(int32_t value, volatile int32_t* addr) {
    return __atomic_fetch_or(addr, value, __ATOMIC_RELEASE);
}
ANDROID_ATOMIC_INLINE
int32_t android_atomic_acquire_load(volatile const int32_t* addr) {
    return __atomic_load_n(addr, __ATOMIC_ACQUIRE);
}
ANDROID_ATOMIC_INLINE
int32_t android_atomic_release_load(volatile const int32_t* addr) {
    __atomic_thread_fence(__ATOMIC_SEQ_CST);
    return __atomic_load_n(addr, __ATOMIC_RELAXED);
}
ANDROID_ATOMIC_INLINE
void android_atomic_acquire_store(int32_t value, volatile int32_t* addr) {
    __atomic_store_n(addr, value, __ATOMIC_SEQ_CST);
}
ANDROID_ATOMIC_INLINE
void android_atomic_release_store(int32_t value, volatile int32_t* addr) {
    __atomic_store_n(addr, value, __ATOMIC_RELEASE);
}
ANDROID_ATOMIC_INLINE
int android_atomic_cas(int32_t old_value, int32_t new_value, volatile int32_t* addr) {
    int32_t expected = old_value;
    return !__atomic_compare_exchange_n(addr, &expected, new_value, 0,
                                        __ATOMIC_RELAXED, __ATOMIC_RELAXED);
}
ANDROID_ATOMIC_INLINE
int android_atomic_acquire_cas(int32_t old_value, int32_t new_value, volatile int32_t* addr) {
    int32_t expected = old_value;
    return !__atomic_compare_exchange_n(addr, &expected, new_value, 0,
                                        __ATOMIC_ACQUIRE, __ATOMIC_ACQUIRE);
}
ANDROID_ATOMIC_INLINE
int android_atomic_release_cas(int32_t old_value, int32_t new_value, volatile int32_t* addr) {
    int32_t expected = old_value;
    return !__atomic_compare_exchange_n(addr, &expected, new_value, 0,
                                        __ATOMIC_RELEASE, __ATOMIC_RELAXED);
}

#ifdef __cplusplus
} // extern "C"
#endif

#endif // ANDROID_CUTILS_ATOMIC_H
