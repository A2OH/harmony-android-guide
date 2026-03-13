/*
 * Atomic operations shim — maps Android atomics to GCC builtins.
 */
#ifndef _CUTILS_ATOMIC_H
#define _CUTILS_ATOMIC_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

static inline int32_t android_atomic_acquire_load(volatile const int32_t* addr) {
    return __atomic_load_n(addr, __ATOMIC_ACQUIRE);
}

static inline int32_t android_atomic_release_load(volatile const int32_t* addr) {
    return __atomic_load_n(addr, __ATOMIC_SEQ_CST);
}

static inline void android_atomic_acquire_store(int32_t value, volatile int32_t* addr) {
    __atomic_store_n(addr, value, __ATOMIC_SEQ_CST);
}

static inline void android_atomic_release_store(int32_t value, volatile int32_t* addr) {
    __atomic_store_n(addr, value, __ATOMIC_RELEASE);
}

static inline int android_atomic_cas(int32_t oldvalue, int32_t newvalue, volatile int32_t* addr) {
    return !__atomic_compare_exchange_n(addr, &oldvalue, newvalue, false,
                                         __ATOMIC_SEQ_CST, __ATOMIC_SEQ_CST);
}

static inline int android_atomic_acquire_cas(int32_t oldvalue, int32_t newvalue, volatile int32_t* addr) {
    return !__atomic_compare_exchange_n(addr, &oldvalue, newvalue, false,
                                         __ATOMIC_ACQUIRE, __ATOMIC_ACQUIRE);
}

static inline int android_atomic_release_cas(int32_t oldvalue, int32_t newvalue, volatile int32_t* addr) {
    return !__atomic_compare_exchange_n(addr, &oldvalue, newvalue, false,
                                         __ATOMIC_RELEASE, __ATOMIC_RELAXED);
}

static inline int32_t android_atomic_inc(volatile int32_t* addr) {
    return __atomic_fetch_add(addr, 1, __ATOMIC_SEQ_CST);
}

static inline int32_t android_atomic_dec(volatile int32_t* addr) {
    return __atomic_fetch_sub(addr, 1, __ATOMIC_SEQ_CST);
}

static inline int32_t android_atomic_add(int32_t value, volatile int32_t* addr) {
    return __atomic_fetch_add(addr, value, __ATOMIC_SEQ_CST);
}

static inline int32_t android_atomic_and(int32_t value, volatile int32_t* addr) {
    return __atomic_fetch_and(addr, value, __ATOMIC_SEQ_CST);
}

static inline int32_t android_atomic_or(int32_t value, volatile int32_t* addr) {
    return __atomic_fetch_or(addr, value, __ATOMIC_SEQ_CST);
}

/* Pointer-width atomics for 64-bit lock words */
static inline void android_atomic_release_store_ptr(intptr_t value, volatile intptr_t* addr) {
    __atomic_store_n(addr, value, __ATOMIC_RELEASE);
}

static inline int android_atomic_acquire_cas_ptr(intptr_t oldvalue, intptr_t newvalue, volatile intptr_t* addr) {
    return !__atomic_compare_exchange_n(addr, &oldvalue, newvalue, false,
                                         __ATOMIC_ACQUIRE, __ATOMIC_ACQUIRE);
}

static inline void android_compiler_barrier(void) {
    __asm__ __volatile__("" : : : "memory");
}

static inline void android_memory_barrier(void) {
    __atomic_thread_fence(__ATOMIC_SEQ_CST);
}

static inline void android_memory_store_barrier(void) {
    __atomic_thread_fence(__ATOMIC_RELEASE);
}

/* Memory barriers */
#define ANDROID_MEMBAR_FULL()   __atomic_thread_fence(__ATOMIC_SEQ_CST)
#define ANDROID_MEMBAR_STORE()  __atomic_thread_fence(__ATOMIC_RELEASE)
#define ANDROID_MEMBAR_LOAD()   __atomic_thread_fence(__ATOMIC_ACQUIRE)

#ifdef __cplusplus
}
#endif

#endif /* _CUTILS_ATOMIC_H */
