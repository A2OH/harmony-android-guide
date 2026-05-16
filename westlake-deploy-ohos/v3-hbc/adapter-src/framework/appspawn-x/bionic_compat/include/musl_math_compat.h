// musl_math_compat.h - Fix musl macro/C++ function conflict
// Must be included BEFORE any C++ standard library headers
#ifndef MUSL_MATH_COMPAT_H
#define MUSL_MATH_COMPAT_H
#include <math.h>
// Replace musl macros with builtin functions usable in C++
#ifdef signbit
#undef signbit
#endif
#ifdef isfinite
#undef isfinite
#endif
#ifdef isinf
#undef isinf
#endif
#ifdef isnan
#undef isnan
#endif
#ifdef isnormal
#undef isnormal
#endif
#ifdef fpclassify
#undef fpclassify
#endif
// Provide C++ inline functions in global namespace (for fmtlib/etc)
#ifdef __cplusplus
inline bool signbit(double x) { return __builtin_signbit(x); }
inline bool signbit(float x) { return __builtin_signbitf(x); }
inline bool isfinite(double x) { return __builtin_isfinite(x); }
inline bool isfinite(float x) { return __builtin_isfinite(x); }
inline bool isinf(double x) { return __builtin_isinf(x); }
inline bool isinf(float x) { return __builtin_isinf(x); }
inline bool isnan(double x) { return __builtin_isnan(x); }
inline bool isnan(float x) { return __builtin_isnan(x); }
#endif
#endif
