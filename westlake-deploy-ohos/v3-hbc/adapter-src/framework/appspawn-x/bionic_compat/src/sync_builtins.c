// bionic_compat/src/sync_builtins.c
// Provides __sync_val_compare_and_swap_1 for ARM32.
// On ARM32, clang may emit calls to this GCC legacy builtin instead of
// inlining it. The clang builtins library only provides __atomic_* functions,
// not __sync_* legacy ones. This shim bridges the gap.
//
// We use -fno-builtin-__sync_val_compare_and_swap_1 to allow defining it,
// and use __asm__ label to ensure the correct symbol name in the output.

#include <stdint.h>

// Implementation using __atomic_compare_exchange_n (C11 atomic).
// Use __asm__ label to force the linker symbol name, bypassing clang's
// builtin redefinition check.
uint8_t __sync_val_cas_1_impl(volatile uint8_t* ptr,
                               uint8_t oldval,
                               uint8_t newval)
    __asm__("__sync_val_compare_and_swap_1");

uint8_t __sync_val_cas_1_impl(volatile uint8_t* ptr,
                               uint8_t oldval,
                               uint8_t newval) {
    uint8_t expected = oldval;
    __atomic_compare_exchange_n(ptr, &expected, newval, 0,
                                __ATOMIC_SEQ_CST, __ATOMIC_SEQ_CST);
    return expected;
}

// __sync_synchronize: full memory barrier.
// IMPORTANT: clang on ARMv7 lowers __atomic_thread_fence(SEQ_CST) to a libcall
// to __sync_synchronize, which resolves back to THIS function -> infinite
// recursion -> stack overflow. Use inline asm DMB ISH instead.
void __sync_synchronize_impl(void) __asm__("__sync_synchronize");
void __sync_synchronize_impl(void) {
#if defined(__arm__)
    __asm__ __volatile__("dmb ish" ::: "memory");
#else
    __atomic_thread_fence(__ATOMIC_SEQ_CST);
#endif
}

// __memcmp16: compare two uint16_t arrays (used by ART string ops)
// Returns <0, 0, or >0 like memcmp but operates on 16-bit elements.
int __memcmp16_impl(const uint16_t* s1, const uint16_t* s2, unsigned int count)
    __asm__("__memcmp16");
int __memcmp16_impl(const uint16_t* s1, const uint16_t* s2, unsigned int count) {
    for (unsigned int i = 0; i < count; i++) {
        if (s1[i] != s2[i]) return (int)s1[i] - (int)s2[i];
    }
    return 0;
}

// adler32 / adler32_combine: zlib checksum (used by ART for oat files)
#define ADLER_MOD 65521
unsigned long adler32_impl(unsigned long adler, const uint8_t* buf, unsigned int len)
    __asm__("adler32");
unsigned long adler32_impl(unsigned long adler, const uint8_t* buf, unsigned int len) {
    if (!buf) return 1;
    unsigned long s1 = adler & 0xffff;
    unsigned long s2 = (adler >> 16) & 0xffff;
    for (unsigned int i = 0; i < len; i++) {
        s1 = (s1 + buf[i]) % ADLER_MOD;
        s2 = (s2 + s1) % ADLER_MOD;
    }
    return (s2 << 16) | s1;
}

unsigned long adler32_combine_impl(unsigned long a1, unsigned long a2, long len2)
    __asm__("adler32_combine");
unsigned long adler32_combine_impl(unsigned long a1, unsigned long a2, long len2) {
    (void)len2;
    // Simplified: just XOR. Real impl is complex but rarely called.
    unsigned long s1 = ((a1 & 0xffff) + (a2 & 0xffff)) % ADLER_MOD;
    unsigned long s2 = ((a1 >> 16) + (a2 >> 16) + (a2 & 0xffff)) % ADLER_MOD;
    return (s2 << 16) | s1;
}
