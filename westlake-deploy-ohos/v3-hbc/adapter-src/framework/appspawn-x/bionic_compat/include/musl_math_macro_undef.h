// musl_math_macro_undef.h
// OH/musl <math.h> 把 C99 classify 系列 (isnan/isinf/isfinite/isnormal/signbit/
// fpclassify/isgreater/isless/isunordered) 直接 #define 成宏。当 C++ 代码
// (尤其 libcxx 的 <cmath> via using-decl，或 AOSP std::isunordered(x,y)) 想
// 把它们当成 std:: 函数使用时，宏展开破坏语法或 lookup。
//
// 用法：在 *.cc / *.h 包含完所有可能拉 <math.h> / <cmath> 的头之后、用到
// std::isXXX 之前，加一行：
//     #include "musl_math_macro_undef.h"
// 不要 -include 此文件——必须在 <cmath> 之后才生效。
#ifndef BIONIC_COMPAT_MUSL_MATH_MACRO_UNDEF_H
#define BIONIC_COMPAT_MUSL_MATH_MACRO_UNDEF_H

// 1) 全部 #undef，把 musl 留下的宏清掉
#ifdef isunordered
#undef isunordered
#endif
#ifdef isnan
#undef isnan
#endif
#ifdef isinf
#undef isinf
#endif
#ifdef isfinite
#undef isfinite
#endif
#ifdef isnormal
#undef isnormal
#endif
#ifdef signbit
#undef signbit
#endif
#ifdef fpclassify
#undef fpclassify
#endif
#ifdef isgreater
#undef isgreater
#endif
#ifdef isgreaterequal
#undef isgreaterequal
#endif
#ifdef isless
#undef isless
#endif
#ifdef islessequal
#undef islessequal
#endif
#ifdef islessgreater
#undef islessgreater
#endif

// 2) 给 std:: 注入函数实现。直接用 clang/gcc __builtin_* 实现，不依赖 libcxx 的
//    using-decl 是否生效（libcxx-ohos 的 <cmath> 已有同名 using-decl，会与
//    "namespace std { inline bool isXxx(...) {...} }" 冲突 ambiguous）。
//    所以这里不再写 using::isnan 等，让 libcxx <cmath> 自己处理 std::isnan，
//    我们只补 libcxx 没用 using-decl 提供的 isunordered/isgreater 等。
#ifdef __cplusplus
namespace std {
  // libcxx 的 <cmath> 用 _LIBCPP_USING_IF_EXISTS 把 ::isunordered 引入 std::；
  // 当 ::isunordered 是宏（已 #undef）或不存在时，using-decl 是 silent no-op。
  // 我们这里用 __builtin 提供，名字不与 libcxx 冲突——若 libcxx 已成功 using
  // 了 ::isunordered (作为 C 函数)，那这两个声明会 overload-set 共存，调用
  // 时仍按精确签名匹配，不会 ambiguous。
  inline bool isunordered(double x, double y) noexcept {
    return __builtin_isunordered(x, y);
  }
  inline bool isunordered(float x, float y) noexcept {
    return __builtin_isunordered(x, y);
  }
  inline bool isgreater(double x, double y) noexcept {
    return __builtin_isgreater(x, y);
  }
  inline bool isgreater(float x, float y) noexcept {
    return __builtin_isgreater(x, y);
  }
  inline bool isgreaterequal(double x, double y) noexcept {
    return __builtin_isgreaterequal(x, y);
  }
  inline bool isgreaterequal(float x, float y) noexcept {
    return __builtin_isgreaterequal(x, y);
  }
  inline bool isless(double x, double y) noexcept {
    return __builtin_isless(x, y);
  }
  inline bool isless(float x, float y) noexcept {
    return __builtin_isless(x, y);
  }
  inline bool islessequal(double x, double y) noexcept {
    return __builtin_islessequal(x, y);
  }
  inline bool islessequal(float x, float y) noexcept {
    return __builtin_islessequal(x, y);
  }
  inline bool islessgreater(double x, double y) noexcept {
    return __builtin_islessgreater(x, y);
  }
  inline bool islessgreater(float x, float y) noexcept {
    return __builtin_islessgreater(x, y);
  }
}
#endif // __cplusplus

#endif // BIONIC_COMPAT_MUSL_MATH_MACRO_UNDEF_H
