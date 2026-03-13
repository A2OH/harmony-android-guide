/*
 * Safe integer overflow checking — standalone shim using GCC builtins.
 */
#ifndef SAFE_IOP_H
#define SAFE_IOP_H

#include <stdint.h>
#include <stdbool.h>
#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

/* safe_add/safe_mul for size_t (works on both 32-bit and 64-bit)
 * Note: original safe_iop allows NULL output pointer (just checks overflow) */
static inline bool safe_add(size_t* out, size_t a, size_t b) {
    size_t tmp;
    if (!out) out = &tmp;
    return !__builtin_add_overflow(a, b, out);
}

static inline bool safe_mul(size_t* out, size_t a, size_t b) {
    size_t tmp;
    if (!out) out = &tmp;
    return !__builtin_mul_overflow(a, b, out);
}

#ifdef __cplusplus
}
#endif

#endif /* SAFE_IOP_H */
