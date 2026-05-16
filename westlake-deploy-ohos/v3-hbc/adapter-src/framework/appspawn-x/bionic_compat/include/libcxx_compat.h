// bionic_compat/include/libcxx_compat.h
// C++ standard library compatibility: bionic libcxx -> OH musl libcxx-ohos
//
// OH's musl-based libcxx-ohos has subtle differences from bionic's libcxx.
// This header is force-included (-include) in all cross-compiled AOSP C++ files.
//
// IMPORTANT: This header must NOT include any C++ stdlib headers (<type_traits>,
// <cmath>, etc.) because:
//   1. Layer 1 (bionic_compat) is compiled with -nostdinc++
//   2. The musl sysroot's stddef.h doesn't define nullptr_t, causing cascade
//      failures in libcxx-ohos headers
// Instead, use compiler builtins and minimal definitions.
#ifndef BIONIC_COMPAT_LIBCXX_COMPAT_H
#define BIONIC_COMPAT_LIBCXX_COMPAT_H

// ============================================================
// 0. nullptr_t definition (missing from musl's stddef.h)
// ============================================================
// libcxx-ohos headers (type_traits, string, etc.) do `using ::nullptr_t`
// which requires nullptr_t in the global namespace. Musl's stddef.h doesn't
// provide it, but clang's builtin stddef.h does. Define it here before any
// C++ stdlib header is included.
#if defined(__cplusplus) && !defined(_NULLPTR_T_DEFINED)
#define _NULLPTR_T_DEFINED
namespace std { typedef decltype(nullptr) nullptr_t; }
using ::std::nullptr_t;
#endif

// ============================================================
// 1. __promote template (missing from musl's <cmath>)
// ============================================================
// AOSP code uses std::__promote<> for numeric type promotion in math functions.
// Provide it using only compiler builtins, no <type_traits> needed.
#if defined(__cplusplus) && !defined(__BIONIC__) && !defined(_LIBCPP_PROMOTE_H)
namespace std {

template <class _Tp, bool _IsInt = ((_Tp)1.5 == (_Tp)1)>  // integer check
struct __promote_impl { typedef double type; };  // integers promote to double

template <class _Tp>
struct __promote_impl<_Tp, false> {};  // non-integer, non-float: no type

template <> struct __promote_impl<float, false> { typedef float type; };
template <> struct __promote_impl<double, false> { typedef double type; };
template <> struct __promote_impl<long double, false> { typedef long double type; };

template <class _A1, class _A2 = void, class _A3 = void>
struct __promote {
    typedef decltype(
        typename __promote_impl<_A1>::type() +
        typename __promote_impl<_A2>::type() +
        typename __promote_impl<_A3>::type()
    ) type;
};

template <class _A1>
struct __promote<_A1, void, void> {
    typedef typename __promote_impl<_A1>::type type;
};

template <class _A1, class _A2>
struct __promote<_A1, _A2, void> {
    typedef decltype(
        typename __promote_impl<_A1>::type() +
        typename __promote_impl<_A2>::type()
    ) type;
};

} // namespace std
#endif // __cplusplus && !__BIONIC__ && !_LIBCPP_PROMOTE_H

// ============================================================
// 2. Math function compatibility (musl macro → C++ function)
// ============================================================
// musl math.h defines isnan/isinf/signbit/isfinite/isnormal as macros.
// When C++ code writes std::signbit(x), preprocessor expands to
// std::__builtin_signbit(x) which is invalid.
// Fix: include math.h first, undef macros, provide function replacements.
// math.h include guard prevents re-definition on later #include <math.h>.
#if defined(__cplusplus) && !defined(__BIONIC__)
#include <math.h>
#ifdef signbit
#undef signbit
#undef isfinite
#undef isinf
#undef isnan
#undef isnormal
#undef fpclassify
#endif
static inline bool signbit(float x) { return __builtin_signbit(x); }
static inline bool signbit(double x) { return __builtin_signbit(x); }
static inline bool isfinite(float x) { return __builtin_isfinite(x); }
static inline bool isfinite(double x) { return __builtin_isfinite(x); }
static inline bool isinf(float x) { return __builtin_isinf(x); }
static inline bool isinf(double x) { return __builtin_isinf(x); }
static inline bool isnan(float x) { return __builtin_isnan(x); }
static inline bool isnan(double x) { return __builtin_isnan(x); }
static inline bool isnan(long double x) { return __builtin_isnan(x); }
static inline bool isnormal(float x) { return __builtin_isnormal(x); }
static inline bool isnormal(double x) { return __builtin_isnormal(x); }
static inline int fpclassify(float x) { return __builtin_fpclassify(0,1,4,3,2,x); }
static inline int fpclassify(double x) { return __builtin_fpclassify(0,1,4,3,2,x); }
#endif

// ============================================================
// 3. Missing Bionic-specific macros/defines
// ============================================================

// __ANDROID_API__ - ART uses this for API-level gating
#ifndef __ANDROID_API__
#define __ANDROID_API__ 34
#endif

// __ANDROID_UNAVAILABLE_SYMBOLS_ARE_WEAK__
#ifndef __ANDROID_UNAVAILABLE_SYMBOLS_ARE_WEAK__
#define __ANDROID_UNAVAILABLE_SYMBOLS_ARE_WEAK__ 0
#endif

// PAGE_SIZE
#ifndef PAGE_SIZE
#define PAGE_SIZE 4096
#endif

// MAX_TASK_COMM_LEN - used by ART thread naming
#ifndef MAX_TASK_COMM_LEN
#define MAX_TASK_COMM_LEN 16
#endif

// ============================================================
// 4. userfaultfd compatibility
// ============================================================
#ifndef __NR_userfaultfd
#if defined(__aarch64__)
#define __NR_userfaultfd 282
#elif defined(__x86_64__)
#define __NR_userfaultfd 323
#endif
#endif

// ============================================================
// 5. TLS slot for ART thread self pointer
// ============================================================
#ifndef TLS_SLOT_ART_THREAD_SELF
#define TLS_SLOT_ART_THREAD_SELF 7
#endif

// ============================================================
// 6. android/log.h priority levels
// ============================================================
// NOTE: Do NOT define ANDROID_LOG_* here as macros.
// android/log.h defines them as enum values, and macro definitions
// would conflict with the enum (causing "expected identifier" errors).
// The enum values are provided by the AOSP log headers themselves.

// ============================================================
// 7. Suppress common cross-compilation warnings
// ============================================================
#if defined(__clang__)
#pragma clang diagnostic ignored "-Wc99-designator"
#pragma clang diagnostic ignored "-Wunused-private-field"
#pragma clang diagnostic ignored "-Wmissing-field-initializers"
#pragma clang diagnostic ignored "-Wgnu-designator"
#pragma clang diagnostic ignored "-Wextern-c-compat"
#endif


// ============================================================
// 9. Fix musl missing abs(double/float) for libc++ cmath
// ============================================================
// musl stdlib.h only provides int abs(int). libc++'s cmath uses
// "using ::abs _LIBCPP_USING_IF_EXISTS" which resolves to nothing for
// float/double/long double, causing "unresolved using declaration" errors
// in <random>/poisson_distribution.h.
// Provide ONLY floating-point overloads. musl has int abs(int).
// Do NOT add long/long long — causes ambiguity in poisson_distribution.h.
#if defined(__cplusplus) && !defined(__BIONIC__)
// OH's libc++ <cstdlib> has `using ::abs` commented out while <cmath> keeps
// `using ::abs _LIBCPP_USING_IF_EXISTS`. Depending on include order either
// std::abs(int) is missing (nterp.cc failure) or is ambiguous with musl's
// ::abs being pulled in twice. Anchor the situation: import musl's ::abs(int)
// once under std::, then supply the floating-point overloads the libc++
// headers (poisson etc.) expect. long / long long variants are inline wrappers
// so we never collide with musl's labs/llabs.
#include <stdlib.h>
namespace std {
using ::abs;  // int abs(int) from musl
inline long        abs(long __x)        { return __x < 0 ? -__x : __x; }
inline long long   abs(long long __x)   { return __x < 0 ? -__x : __x; }
inline float       abs(float __x)       { return __builtin_fabsf(__x); }
inline double      abs(double __x)      { return __builtin_fabs(__x); }
inline long double abs(long double __x) { return __builtin_fabsl(__x); }
}
#endif

#endif // BIONIC_COMPAT_LIBCXX_COMPAT_H

// ============================================================
// 8. ARM32: Suppress std::atomic lock-free static_asserts
// ============================================================
// On ARM32, std::atomic<uint64_t> is not lock-free (requires libatomic).
// ART assumes 64-bit atomics are always lock-free (true on arm64/x86_64).
// Instead of a macro (which breaks libc++ <atomic> header), we redefine
// static_assert to be a no-op when the condition involves is_always_lock_free.
// This is applied via -DART_ARM32_ATOMIC_WORKAROUND in the build script,
// and the actual static_asserts are patched in thread.h and metrics.h.


// ============================================================
// 10. Bionic-specific function stubs
// ============================================================
#ifdef __cplusplus
extern "C" {
#endif
#ifndef __BIONIC__
static inline void setprogname(const char* name) { (void)name; }
#endif
#ifdef __cplusplus
}
#endif


// ============================================================
// 11. ARM cacheflush() - Linux ARM specific syscall
// ============================================================
#if defined(__arm__) && !defined(__BIONIC__)
#include <unistd.h>
#include <sys/syscall.h>
static inline int cacheflush(long start, long end, long flags) {
    return syscall(__ARM_NR_cacheflush, start, end, flags);
}
#endif


// ============================================================
// 12. ART address space defines
// ============================================================
#ifndef ART_BASE_ADDRESS_MIN_DELTA
#define ART_BASE_ADDRESS_MIN_DELTA (-0x1000000)
#define ART_BASE_ADDRESS_MAX_DELTA 0x1000000
#endif


// ============================================================
// 13. Sigchain stubs
// ============================================================
#ifdef __cplusplus
extern "C" {
#endif
#if defined(__cplusplus) && !defined(__BIONIC__) && __has_include(<signal.h>)
// Sigchain stubs: ART code references these bionic-specific symbols.
// Must match art/sigchainlib/sigchain.h exactly.
// Guard with HAVE_SIGCHAIN: avoid conflicting types when building sigchain itself.
#include <signal.h>
#ifndef HAVE_SIGCHAIN
struct SigchainAction {
    bool (*sc_sigaction)(int, siginfo_t*, void*);
    sigset_t sc_mask;
    unsigned long long sc_flags;
};
void AddSpecialSignalHandlerFn(int signal, SigchainAction* sa) __attribute__((weak));
void RemoveSpecialSignalHandlerFn(int signal, bool (*fn)(int, siginfo_t*, void*)) __attribute__((weak));
void EnsureFrontOfChain(int signal) __attribute__((weak));
void SkipAddSignalHandler(bool value) __attribute__((weak));
#endif // !HAVE_SIGCHAIN
#endif // __cplusplus && !__BIONIC__ && signal.h
#ifdef __cplusplus
}
#endif


// ============================================================
// 14. userfaultfd defines for ARM32
// ============================================================
// OH's linux/userfaultfd.h declares `struct uffdio_msg { ... } __packed;` but
// does not #define __packed. When __packed is not a macro, the trailing name
// is parsed as a tentative variable declaration — every TU that includes the
// header then emits its own `__packed` object, which collides at link time.
// Defining __packed as an attribute macro up-front turns it back into the
// intended packed-struct attribute.
#ifndef __packed
#define __packed __attribute__((__packed__))
#endif
#ifndef UFFD_USER_MODE_ONLY
#define UFFD_USER_MODE_ONLY 1
#endif
#ifndef UFFD_FEATURE_MINOR_SHMEM
#define UFFD_FEATURE_MINOR_SHMEM (1 << 10)
#endif
#ifndef UFFDIO_CONTINUE
#define UFFDIO_CONTINUE 0xC018AA07
#endif
#ifndef UFFD_PAGEFAULT_FLAG_MINOR
#define UFFD_PAGEFAULT_FLAG_MINOR (1 << 2)
#endif
#ifndef UFFDIO_REGISTER_MODE_MINOR
#define UFFDIO_REGISTER_MODE_MINOR (1 << 1)
#endif
// struct uffdio_continue is Linux 5.7+; OH's UAPI predates it. mark_compact.cc
// references the type once. An aosp_patches patch on mark_compact.cc adds the
// local struct definition so only that one TU sees it.


// Minimal std::span polyfill for C++17 (used by zip_archive.cc).
// Suppressed in C++20+: the real libcxx ships std::span there, and defining
// our own polyfill in namespace std causes ambiguous-reference errors in
// headers that use std::span (e.g. libprocessgroup/processgroup.h).
#if __cplusplus >= 201703L && __cplusplus < 202002L && !defined(__cpp_lib_span)
namespace std {
template<typename T>
class span {
public:
    using element_type = T;
    using value_type = T;  // simplified: no remove_cv in force-include context
    using size_type = size_t;
    using pointer = T*;
    using reference = T&;
    using iterator = T*;

    constexpr span() noexcept : data_(nullptr), size_(0) {}
    constexpr span(T* ptr, size_t count) : data_(ptr), size_(count) {}
    template<size_t N> constexpr span(T (&arr)[N]) : data_(arr), size_(N) {}
    constexpr span(const span&) = default;
    // Implicit conversion from containers (vector, array, etc.)
    template<typename Container,
             typename = decltype(static_cast<T*>(static_cast<Container*>(nullptr)->data())),
             typename = decltype(static_cast<size_t>(static_cast<Container*>(nullptr)->size()))>
    constexpr span(Container& c) : data_(c.data()), size_(c.size()) {}
    span& operator=(const span&) = default;

    constexpr pointer data() const noexcept { return data_; }
    constexpr size_type size() const noexcept { return size_; }
    constexpr bool empty() const noexcept { return size_ == 0; }
    constexpr reference operator[](size_type idx) const { return data_[idx]; }
    constexpr iterator begin() const noexcept { return data_; }
    constexpr iterator end() const noexcept { return data_ + size_; }
    constexpr span subspan(size_type offset, size_type count = static_cast<size_type>(-1)) const {
        return span(data_ + offset, count == static_cast<size_type>(-1) ? size_ - offset : count);
    }
private:
    T* data_;
    size_t size_;
};
} // namespace std
#endif
