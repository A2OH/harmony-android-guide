// vixl_math_fix.h - Fix musl math.h macro conflicts for C++ code
// musl provides isnan/isinf/etc as macros only. libc++ cmath expects functions.
// We must: 1) save macro definitions, 2) undef them, 3) provide function versions
#ifndef VIXL_MATH_FIX_H
#define VIXL_MATH_FIX_H
#ifdef __cplusplus
#include <math.h>

// Save the macro implementations before undefining
static inline int __musl_isnan(double x) { return __builtin_isnan(x); }
static inline int __musl_isinf(double x) { return __builtin_isinf(x); }
static inline int __musl_isfinite(double x) { return __builtin_isfinite(x); }
static inline int __musl_isnormal(double x) { return __builtin_isnormal(x); }
static inline int __musl_signbit(double x) { return __builtin_signbit(x); }

// Undef macros
#undef isnan
#undef isinf
#undef isfinite
#undef isnormal
#undef signbit
#undef fpclassify

// Provide function versions in global namespace (for libc++ using ::isnan)
static inline bool isnan(float x) { return __builtin_isnan(x); }
static inline bool isnan(double x) { return __builtin_isnan(x); }
static inline bool isnan(long double x) { return __builtin_isnan(x); }
static inline bool isinf(float x) { return __builtin_isinf(x); }
static inline bool isinf(double x) { return __builtin_isinf(x); }
static inline bool isinf(long double x) { return __builtin_isinf(x); }
static inline bool isfinite(float x) { return __builtin_isfinite(x); }
static inline bool isfinite(double x) { return __builtin_isfinite(x); }
static inline bool isfinite(long double x) { return __builtin_isfinite(x); }
static inline bool isnormal(float x) { return __builtin_isnormal(x); }
static inline bool isnormal(double x) { return __builtin_isnormal(x); }
static inline bool signbit(float x) { return __builtin_signbit(x); }
static inline bool signbit(double x) { return __builtin_signbit(x); }
static inline int fpclassify(float x) { return __builtin_fpclassify(0,1,4,3,2,x); }
static inline int fpclassify(double x) { return __builtin_fpclassify(0,1,4,3,2,x); }


// abs overloads moved to libcxx_compat.h (force-included for all C++ files)
// so they are available globally, not just for VIXL targets

#endif // __cplusplus
#endif // VIXL_MATH_FIX_H
