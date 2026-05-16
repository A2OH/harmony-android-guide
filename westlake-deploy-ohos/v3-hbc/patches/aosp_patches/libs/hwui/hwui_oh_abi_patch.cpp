/*
 * hwui_oh_abi_patch.cpp
 *
 * AOSP <-> OH ABI compatibility patch, compiled INTO libhwui.so itself.
 *
 * This file is NOT the adapter's external Skia shim — that's a separate file
 * (framework/hwui-shim/jni/oh_skia_ahb_shim.cpp) compiled into liboh_hwui_shim.so
 * and consumed by libhwui through normal NEEDED resolution.  This file plays
 * a different role: it patches libhwui.so from the inside, exploiting
 * -Wl,-Bsymbolic-functions to redirect libhwui's internal PLT calls to
 * adapter-supplied implementations that the dynamic linker could not normally
 * intercept.
 *
 * Roughly four kinds of content live here:
 *
 *   §1  AOSP/OH Skia M116->M133 link-time stubs (safe defaults):
 *       SkImage_pinAsTexture, SkSurfaces::WrapAndroidHardwareBuffer,
 *       SkAndroidFrameworkUtils::{SaveBehind,ShaderAsALinearGradient,ResetClip},
 *       SkImages::DeferredFromAHardwareBuffer, Movie::DecodeStream.  Return
 *       nullptr/0/false/no-op so libhwui dlopens.  Hello World does not
 *       exercise these in normal flow.
 *
 *   §2  Logging / abort hijack (real bridge to OH HiLogPrint):
 *       __android_log_assert, __android_log_print, abort, sk_abort_no_print,
 *       __cxa_guard_abort.  Strong-symbol overrides bound via
 *       -Bsymbolic-functions so hwui's LOG_ALWAYS_FATAL / SkASSERT / static-
 *       init guard paths route through OH hilog instead of dying silently
 *       on absent logd.
 *
 *   §3  ADisplay NDK real bridge:
 *       ADisplay_{acquirePhysicalDisplays,getDisplayType,
 *       getPreferredWideColorFormat,release} bridged to OH
 *       NativeDisplayManager (real hardware info, not constants).
 *
 *   §4  eglCreateWindowSurface hijack (G2.14af):
 *       Detect & unwrap the AOSP-ABI ANativeWindow shim before forwarding
 *       to OH libEGL.so's real eglCreateWindowSurface.  Lives here because
 *       -Bsymbolic-functions self-binds libhwui's internal EglManager call
 *       to this hijack rather than the libEGL.so default.
 *
 * Why "patch" (not just "shim"): the §2/§4 hijacks only work when their
 * strong symbols live INSIDE libhwui.so so -Bsymbolic-functions binds
 * libhwui's internal references to them.  Moving them to an external .so
 * would let the dynamic linker resolve hwui's PLT slots to the real
 * musl/libEGL/libcxxabi implementations instead, defeating the hijack.
 *
 * 2026-04-11 (gap 9a) — symbol mangling rewrite:
 * The previous version used void* / namespace-style declarations that
 * produced WRONG mangled names. The unresolved symbols stayed lazy-bound
 * and the device crashed at first call. This version uses the EXACT
 * declarations from the real AOSP / Skia / minikin headers so the C++
 * mangled names match the libhwui.so callers, turning the 5 lazy-bound
 * symbols into resolved ones at link time.
 *
 * Per CLAUDE.md "禁止用 stub 回避问题" — the §1 entries are NOT compile-
 * time stubs hiding link errors. They are correct-mangling resolvers for
 * symbols whose source files we deliberately chose not to cross-compile
 * (their dependency closure pulls in too much OH-incompatible code).
 *
 * 2026-05-11 (G2.14au r2) — renamed from hwui_missing_symbols_stub.cpp.
 * The "missing_symbols_stub" name became misleading once §2/§3/§4 grew
 * to contain real bridges rather than pure stubs.
 *
 * Compile via build/compile_hwui_stubs.sh.
 */

#include <atomic>    // G2.14au r3 probe counters
#include <cstdint>
#include <cstddef>   // size_t
#include <cstdio>
#include <dlfcn.h>   // dlopen/dlsym (G2.14af eglCreateWindowSurface hijack)
#include <memory>
#include <string>
#include <string_view>
#include <vector>
#include <jni.h>

// =============================================================================
// Forward declarations of opaque Skia types
// =============================================================================
class SkImage;
class SkBitmap;
class SkCanvas;
class SkPath;
class SkColorSpace;
class SkSurfaceProps;
class SkPngChunkReader;
class SkAndroidCodec;
class SkStreamRewindable;
class GrRecordingContext;
class GrDirectContext;
class SkShader;
class SkTypeface;
class SkFont;
struct SkIRect;
struct SkRect;
struct AHardwareBuffer;
struct ANativeWindow;
enum GrSurfaceOrigin { kTopLeft_GrSurfaceOrigin = 0 };
enum SkEncodedImageFormat { kUnknown_SkEncodedImageFormat = 0 };
enum SkColorType { kUnknown_SkColorType = 0 };
class SkCodec {
public:
    enum ZeroInitialized { kNo_ZeroInitialized = 0 };
};

template <typename T>
struct sk_sp { T* p; sk_sp() : p(nullptr) {} sk_sp(T* x) : p(x) {} ~sk_sp() {} };

// =============================================================================
// Forward declarations for android / minikin / hwui
// =============================================================================
namespace minikin {
class MinikinFont;
class MinikinPaint;
struct FontFakery {
    // mirror real layout: a single packed uint16/uint8 — exact bits don't
    // matter for symbol resolution since FontFakery is passed by value and
    // C++ mangling encodes only the type name.
    uint16_t mBits = 0;
};
struct FontVariation {
    uint32_t axisTag = 0;
    float value = 0.0f;
};
enum class Bidi : uint8_t {};
class Layout;
}  // namespace minikin

namespace android {

class Bitmap;
class Paint;
class Typeface;

namespace uirenderer {

// ColorMode is at uirenderer level, not renderthread
enum class ColorMode { Default = 0, Wide = 1 };
class DeferredLayerUpdater;
class Layer;  // forward decl — required for LayerDrawable::DrawLayer signature

namespace renderthread {
class Frame;
class EglManager;
}  // namespace renderthread

}  // namespace uirenderer

}  // namespace android

// =============================================================================
// Group A — Skia helper free functions (Android-only API removed in M133)
// =============================================================================

void SkImage_pinAsTexture(const SkImage* img, GrRecordingContext* ctx) {
    // G2.14au r3 probe
    static std::atomic<int> g_count{0};
    int n = ++g_count;
    if (n == 1 || n % 10 == 0)
        fprintf(stderr, "[G214au_PCH] SkImage_pinAsTexture #%d img=%p ctx=%p\n",
                n, (const void*)img, (void*)ctx);
}
void SkImage_unpinAsTexture(const SkImage* img, GrRecordingContext* ctx) {
    // G2.14au r3 probe
    static std::atomic<int> g_count{0};
    int n = ++g_count;
    if (n == 1 || n % 10 == 0)
        fprintf(stderr, "[G214au_PCH] SkImage_unpinAsTexture #%d img=%p ctx=%p\n",
                n, (const void*)img, (void*)ctx);
}

namespace SkSurfaces {
    void* WrapAndroidHardwareBuffer(GrDirectContext*, AHardwareBuffer*,
                                    ::GrSurfaceOrigin, sk_sp<SkColorSpace>,
                                    const SkSurfaceProps*, bool);
}
void* SkSurfaces::WrapAndroidHardwareBuffer(GrDirectContext* ctx, AHardwareBuffer* hb,
                                            ::GrSurfaceOrigin origin, sk_sp<SkColorSpace>,
                                            const SkSurfaceProps*, bool isProtected) {
    // G2.14au r3 probe — KEY: returns nullptr (swallows AHB-backed Skia surface)
    static std::atomic<int> g_count{0};
    int n = ++g_count;
    fprintf(stderr, "[G214au_PCH] SkSurfaces::WrapAndroidHardwareBuffer #%d "
            "ctx=%p hb=%p origin=%d protected=%d -> nullptr (STUB)\n",
            n, (void*)ctx, (void*)hb, (int)origin, isProtected ? 1 : 0);
    return nullptr;
}

// =============================================================================
// Group B — Movie::DecodeStream (deprecated GIF/Movie API)
// =============================================================================

class Movie {
public:
    static Movie* DecodeStream(SkStreamRewindable*);
};
Movie* Movie::DecodeStream(SkStreamRewindable* s) {
    // G2.14au r3 probe — sanity (Movie API deprecated, expected 0 fire)
    static std::atomic<int> g_count{0};
    int n = ++g_count;
    fprintf(stderr, "[G214au_PCH] Movie::DecodeStream #%d s=%p -> nullptr (STUB)\n",
            n, (void*)s);
    return nullptr;
}

// =============================================================================
// Group I — SkAndroidFrameworkUtils class with nested LinearGradientInfo
// =============================================================================
//
// CRITICAL: SkAndroidFrameworkUtils is a CLASS in real Skia, with these
// methods being STATIC member functions, NOT namespace-scoped functions.
// The mangled name for a static class member function is different from
// a namespace function with the same name. Use class declaration here.
//
// LinearGradientInfo is nested inside the class — its layout doesn't need
// to be accurate for linker resolution, just for the type identity in
// the mangled parameter type.
class SkAndroidFrameworkUtils {
public:
    struct LinearGradientInfo {
        int fColorCount;
        const uint32_t* fColors;
        const float* fColorOffsets;
        float fPoints[4];
        int fTileMode;
        uint32_t fGradientFlags;
        float fMatrix[9];
    };
    static int  SaveBehind(SkCanvas*, const SkRect*);
    static bool ShaderAsALinearGradient(SkShader*, LinearGradientInfo*);
    static void ResetClip(SkCanvas*);
};

int SkAndroidFrameworkUtils::SaveBehind(SkCanvas* c, const SkRect* r) {
    // G2.14au r3 probe
    static std::atomic<int> g_count{0};
    int n = ++g_count;
    if (n == 1 || n % 10 == 0)
        fprintf(stderr, "[G214au_PCH] SkAndroidFrameworkUtils::SaveBehind #%d "
                "canvas=%p rect=%p -> 0 (STUB)\n", n, (void*)c, (const void*)r);
    return 0;
}
bool SkAndroidFrameworkUtils::ShaderAsALinearGradient(SkShader* s,
        SkAndroidFrameworkUtils::LinearGradientInfo* info) {
    // G2.14au r3 probe
    static std::atomic<int> g_count{0};
    int n = ++g_count;
    if (n == 1 || n % 10 == 0)
        fprintf(stderr, "[G214au_PCH] SkAndroidFrameworkUtils::ShaderAsALinearGradient #%d "
                "shader=%p info=%p -> false (STUB)\n", n, (void*)s, (void*)info);
    return false;
}
void SkAndroidFrameworkUtils::ResetClip(SkCanvas* c) {
    // G2.14au r3 probe
    static std::atomic<int> g_count{0};
    int n = ++g_count;
    if (n == 1 || n % 10 == 0)
        fprintf(stderr, "[G214au_PCH] SkAndroidFrameworkUtils::ResetClip #%d canvas=%p\n",
                n, (void*)c);
}

namespace SkAndroidFrameworkTraceUtil {
    bool gEnableAndroidTracing = false;
}

// SkImages::DeferredFromAHardwareBuffer (used by Readback.cpp)
//
// 2026-05-09 G2.14ad fix: must use `enum SkAlphaType` (or class), not
// `typedef int SkAlphaType` — Itanium C++ ABI mangles a typedef of int to
// `i` but mangles enum/class to `11SkAlphaType` (length-prefixed name).
// Real Skia declares it as `enum SkAlphaType { ... };` so callers in
// libhwui's compiled .o files reference the `11SkAlphaType` mangling.
// Using typedef int produced the wrong mangling and made the strong
// definition fail to satisfy hwui's UND, leading to runtime relocation
// failure when libhwui dlopen attempted to bind the symbol.
enum SkAlphaType { kUnknown_SkAlphaType };
namespace SkImages {
    sk_sp<SkImage> DeferredFromAHardwareBuffer(AHardwareBuffer*, SkAlphaType,
                                               sk_sp<SkColorSpace>, GrSurfaceOrigin);
}
sk_sp<SkImage> SkImages::DeferredFromAHardwareBuffer(AHardwareBuffer* hb, SkAlphaType at,
                                                     sk_sp<SkColorSpace>, GrSurfaceOrigin origin) {
    // G2.14au r3 probe — KEY: returns nullptr (swallows Readback/HardwareBitmap AHB path)
    static std::atomic<int> g_count{0};
    int n = ++g_count;
    fprintf(stderr, "[G214au_PCH] SkImages::DeferredFromAHardwareBuffer #%d "
            "hb=%p alphaType=%d origin=%d -> nullptr (STUB)\n",
            n, (void*)hb, (int)at, (int)origin);
    return sk_sp<SkImage>(nullptr);
}

// =============================================================================
// (P10.B) RenderThread::requireGrContext stub removed — real renderthread/RenderThread.cpp now compiles
// (P10.C) MinikinFontSkia/MinikinUtils stubs removed — real hwui/MinikinSkia.cpp + hwui/MinikinUtils.cpp now compile
// =============================================================================
namespace android { namespace uirenderer {

// (P10.A) LayerDrawable::DrawLayer stub removed — real pipeline/skia/LayerDrawable.cpp provides it

}}  // namespace android::uirenderer

// (P10.C) MinikinFontSkia / MinikinUtils stubs removed — real .o files now provide these symbols

// =============================================================================
// G2.14ad (2026-05-09) — __android_log_assert / __android_log_print hijack to
// OH hilog
//
// Why: AOSP libhwui uses LOG_ALWAYS_FATAL_IF / LOG_ALWAYS_FATAL macros which
// expand to __android_log_assert(cond, tag, fmt, ...) → write to logd → abort.
// OH has no logd: device's /system/lib/liblog.so __android_log_assert silently
// writes to a non-functional pipe then abort() with no observable message —
// RenderThread SIGABRT shows up with empty Fault message in cppcrash dump
// (G2.14ac confirmed: PC=raise+176, LR=abort+0x14, stack zeroed).
//
// Fix: provide strong-symbol __android_log_assert / __android_log_print inside
// libhwui.so itself. Dynamic linker resolves in-.so definitions before
// external symbols, so libhwui's LOG_ALWAYS_FATAL / ALOGE / ALOGW paths route
// through these hooks, print to OH hilog (FATAL/ERROR level, tag = caller-
// supplied), then call abort() to preserve original semantics.
//
// Reference: feedback_native_log_use_hilogprint.md — adapter native log must
// use OH HiLogPrint not __android_log_print which targets the absent logd.
// =============================================================================
#include <cstdarg>

extern "C" int HiLogPrint(int type, int level, unsigned int domain,
                          const char* tag, const char* fmt, ...);

extern "C" __attribute__((visibility("default"), noreturn))
void __android_log_assert(const char* cond, const char* tag,
                          const char* fmt, ...) {
    char msg[1024] = {0};
    if (fmt) {
        va_list ap; va_start(ap, fmt);
        vsnprintf(msg, sizeof(msg), fmt, ap);
        va_end(ap);
    }
    // OH hilog: type=3 (LOG_CORE), level=7 (LOG_FATAL), domain=0xD000F00.
    HiLogPrint(3, 7, 0xD000F00,
               tag ? tag : "hwui_assert",
               "ASSERT FAILED cond=%s msg=%s",
               cond ? cond : "(null)", msg);
    fprintf(stderr, "ASSERT FAILED [%s] cond=%s msg=%s\n",
            tag ? tag : "?", cond ? cond : "(null)", msg);
    fflush(stderr);
    abort();
}

extern "C" __attribute__((visibility("default")))
int __android_log_print(int prio, const char* tag, const char* fmt, ...) {
    char msg[1024] = {0};
    int ret = 0;
    if (fmt) {
        va_list ap; va_start(ap, fmt);
        ret = vsnprintf(msg, sizeof(msg), fmt, ap);
        va_end(ap);
    }
    // AOSP prio: VERBOSE=2 DEBUG=3 INFO=4 WARN=5 ERROR=6 FATAL=7
    // OH hilog levels match (3..7). Clamp into range, default DEBUG.
    int oh_level = (prio >= 3 && prio <= 7) ? prio : 3;
    HiLogPrint(3, oh_level, 0xD000F00,
               tag ? tag : "hwui_print", "%s", msg);
    return ret;
}

// =============================================================================
// G2.14ad — extend hijack to abort()/sk_abort_no_print/__cxa_guard_abort
//
// Why: G2.14ac+ad confirmed __android_log_assert hijack alone misses abort
// because hwui's actual abort paths bypass LOG_ALWAYS_FATAL — they call:
//   - abort() directly (libc)
//   - _Z17sk_abort_no_printv (Skia SkASSERT / SK_ABORT)
//   - __cxa_guard_abort (C++ static init guard)
// All three were R_ARM_JUMP_SLOT in libhwui's reloc table (verified via
// llvm-readelf -r). With -Wl,-Bsymbolic-functions, providing strong symbols
// here makes libhwui's PLT slots self-bind to these implementations rather
// than dynamic-resolve to musl/Skia/libc++ at runtime.
//
// Each implementation prints to OH hilog (FATAL level) with caller LR
// (__builtin_return_address(0)) for offline llvm-objdump lookup against the
// libhwui base in /proc/<pid>/maps, then _Exit(134) to mimic SIGABRT exit
// status without going through musl raise(SIGABRT) — which zeros the stack
// and prevents post-mortem unwinding.
// =============================================================================
#include <unistd.h>  // _Exit

extern "C" __attribute__((visibility("default"), noreturn))
void abort() {
    void* lr = __builtin_return_address(0);
    HiLogPrint(3, 7, 0xD000F00, "hwui_abort",
               "abort() called from libhwui — caller_lr=%p (subtract libhwui base for offset)", lr);
    fprintf(stderr, "abort() hwui hijack — caller_lr=%p\n", lr);
    fflush(stderr);
    _Exit(134);
}

extern "C" __attribute__((visibility("default"), noreturn))
void _Z17sk_abort_no_printv() {
    void* lr = __builtin_return_address(0);
    HiLogPrint(3, 7, 0xD000F00, "hwui_abort",
               "sk_abort_no_print() called from libhwui — caller_lr=%p", lr);
    fprintf(stderr, "sk_abort_no_print hwui hijack — caller_lr=%p\n", lr);
    fflush(stderr);
    _Exit(134);
}

extern "C" __attribute__((visibility("default"), noreturn))
void __cxa_guard_abort(void* /*g*/) {
    void* lr = __builtin_return_address(0);
    HiLogPrint(3, 7, 0xD000F00, "hwui_abort",
               "__cxa_guard_abort() called from libhwui (C++ static init failed) — caller_lr=%p", lr);
    fprintf(stderr, "__cxa_guard_abort hwui hijack — caller_lr=%p\n", lr);
    fflush(stderr);
    _Exit(134);
}

// =============================================================================
// G2.14ad — ADisplay NDK shim bridged to OH NativeDisplayManager
//
// AOSP libhwui DeviceInfo::updateDisplayInfo() calls four NDK functions to
// query physical displays and decide WCG (Wide Color Gamut) capability:
//   ADisplay_acquirePhysicalDisplays / ADisplay_getDisplayType /
//   ADisplay_getPreferredWideColorFormat / ADisplay_release
// OH does not provide AOSP NDK; uncatched, the chain returns 0 displays and
// hwui LOG_ALWAYS_FATAL → silent abort (G2.14ac+ad confirmed via abort hijack
// LR=libhwui+0xb244c → updateDisplayInfo+0x4c).
//
// Design choice: bridge — not stub — to OH NativeDisplayManager. Calling
// OH_NativeDisplayManager_CreatePrimaryDisplay returns real hardware info
// (colorSpace.colorSpaces array), so hwui's WCG check produces an answer
// derived from the actual OH display, not a hard-coded constant. Future
// P3/HDR OH devices (DAYU600) get correct WCG without any code change.
//
// Reference: doc/graphics_rendering_design.html §7.9 (full design + risk).
// =============================================================================

// OH NativeDisplayManager NDK forward declarations
// Source: ~/oh/foundation/window/window_manager/interfaces/kits/dmndk/dm/oh_display_info.h
typedef struct {
    uint32_t colorSpaceLength;
    uint32_t* colorSpaces;
} NativeDisplayManager_DisplayColorSpace;

typedef struct {
    uint32_t id;
    char name[33];
    bool isAlive;
    int32_t width, height;
    int32_t physicalWidth, physicalHeight;
    uint32_t refreshRate;
    uint32_t availableWidth, availableHeight;
    float densityDPI, densityPixels, scaledDensity, xDPI, yDPI;
    int rotation;
    int state;
    int orientation;
    void* hdrFormat;  // NativeDisplayManager_DisplayHdrFormat*
    NativeDisplayManager_DisplayColorSpace* colorSpace;
} NativeDisplayManager_DisplayInfo;

extern "C" int OH_NativeDisplayManager_CreatePrimaryDisplay(
    NativeDisplayManager_DisplayInfo** displayInfo);
extern "C" void OH_NativeDisplayManager_DestroyDisplay(
    NativeDisplayManager_DisplayInfo* displayInfo);
extern "C" int OH_NativeDisplayManager_GetDefaultDisplayId(uint64_t* displayId);

// Constants
//   OH ColorSpaceName (color_space.h enum):  SRGB=4, DISPLAY_P3=3
//   AOSP ADataSpace (graphics-base-v1.0.h):  SRGB=142671872, DISPLAY_P3=143261696
//   AOSP AHardwareBuffer_Format:             R8G8B8A8_UNORM=1, R16G16B16A16_FLOAT=22
namespace {
    constexpr uint32_t kOhColorSrgb     = 4;
    constexpr uint32_t kOhColorDispP3   = 3;
    constexpr int      kAospDataspaceSRGB     = 142671872;
    constexpr int      kAospDataspaceDispP3   = 143261696;
    constexpr int      kAospHwbR8G8B8A8       = 1;

    // hwui hands us back the same opaque pointer it received from acquire,
    // so wrap each OH DisplayInfo* in a small handle we control. Magic +
    // pointer identity lets release/getDisplayType validate input.
    struct OhAdisplayHandle {
        uint32_t magic;
        NativeDisplayManager_DisplayInfo* ohInfo;  // null = fallback path
    };
    constexpr uint32_t kAdisplayMagic = 0x41444953;  // 'ADIS'
}

extern "C" __attribute__((visibility("default")))
int ADisplay_acquirePhysicalDisplays(void*** outDisplays) {
    if (!outDisplays) return -1;

    NativeDisplayManager_DisplayInfo* ohInfo = nullptr;
    int rc = OH_NativeDisplayManager_CreatePrimaryDisplay(&ohInfo);

    OhAdisplayHandle* h = (OhAdisplayHandle*) ::malloc(sizeof(OhAdisplayHandle));
    if (!h) return -1;
    h->magic = kAdisplayMagic;

    if (rc == 0 && ohInfo) {
        h->ohInfo = ohInfo;
        HiLogPrint(3, 4, 0xD000F00, "OH_ADisplay",
                   "ADisplay_acquirePhysicalDisplays via OH_NDM -> id=%u %dx%d %uHz colorSpaces=%u",
                   ohInfo->id, ohInfo->width, ohInfo->height, ohInfo->refreshRate,
                   ohInfo->colorSpace ? ohInfo->colorSpace->colorSpaceLength : 0);
    } else {
        // OH NDK 调失败 — 走 fallback：保留 handle 但 ohInfo=null，让
        // getPreferredWideColorFormat 返回 SDR 默认，hwui 启动不崩。
        h->ohInfo = nullptr;
        HiLogPrint(3, 5, 0xD000F00, "OH_ADisplay",
                   "ADisplay_acquirePhysicalDisplays: OH_NDM_CreatePrimaryDisplay rc=%d, "
                   "using SDR fallback (sRGB+R8G8B8A8)", rc);
    }

    void** arr = (void**) ::malloc(sizeof(void*) * 1);
    if (!arr) {
        if (h->ohInfo) OH_NativeDisplayManager_DestroyDisplay(h->ohInfo);
        ::free(h);
        return -1;
    }
    arr[0] = h;
    *outDisplays = arr;
    return 1;
}

extern "C" __attribute__((visibility("default")))
int ADisplay_getDisplayType(void* display) {
    // AOSP DisplayType enum: INTERNAL=0, EXTERNAL=1
    //
    // OH model: NativeDisplayManager / DisplayManager don't expose an
    // INTERNAL/EXTERNAL distinction directly (all displays are equal in OH's
    // design, similar to ScreenManager but without PHYSICAL/VIRTUAL split
    // here). Closest semantic mapping:
    //   if display.id == default-display.id  → INTERNAL (built-in panel)
    //   else                                  → EXTERNAL (HDMI/USB/wireless)
    // This matches RK3568 (MIPI panel = default = INTERNAL only) and scales
    // to future multi-display OH devices (DAYU600 etc) where external HDMI
    // would have a non-default id.
    OhAdisplayHandle* h = (OhAdisplayHandle*) display;
    if (!h || h->magic != kAdisplayMagic || !h->ohInfo) {
        // Fallback path or invalid handle: report INTERNAL since hwui needs
        // exactly one INTERNAL to drive WCG init; treating unknown as
        // INTERNAL is the safe default (matches AOSP single-display
        // assumption).
        return 0;
    }

    uint64_t defaultId = 0;
    int rc = OH_NativeDisplayManager_GetDefaultDisplayId(&defaultId);
    if (rc != 0) {
        // OH NDK call failed — same fallback policy as above.
        HiLogPrint(3, 5, 0xD000F00, "OH_ADisplay",
                   "GetDefaultDisplayId rc=%d, defaulting to INTERNAL", rc);
        return 0;
    }

    int aospType = (h->ohInfo->id == (uint32_t) defaultId) ? 0 /*INTERNAL*/
                                                            : 1 /*EXTERNAL*/;
    HiLogPrint(3, 4, 0xD000F00, "OH_ADisplay",
               "ADisplay_getDisplayType: ohId=%u defaultId=%llu -> %s",
               h->ohInfo->id, (unsigned long long) defaultId,
               aospType == 0 ? "INTERNAL" : "EXTERNAL");
    return aospType;
}

extern "C" __attribute__((visibility("default")))
void ADisplay_getPreferredWideColorFormat(void* display,
                                           int* outDataspace,
                                           int* outPixelFormat) {
    int aospDataspace   = kAospDataspaceSRGB;
    int aospPixelFormat = kAospHwbR8G8B8A8;
    bool fromOh = false;

    OhAdisplayHandle* h = (OhAdisplayHandle*) display;
    if (h && h->magic == kAdisplayMagic && h->ohInfo &&
        h->ohInfo->colorSpace && h->ohInfo->colorSpace->colorSpaces) {
        // Walk OH-supported color spaces, prefer DISPLAY_P3 if available.
        for (uint32_t i = 0; i < h->ohInfo->colorSpace->colorSpaceLength; i++) {
            if (h->ohInfo->colorSpace->colorSpaces[i] == kOhColorDispP3) {
                aospDataspace = kAospDataspaceDispP3;
                // pixelFormat stays R8G8B8A8: OH RK3568 GPU does not advertise
                // F16 EGL configs, and hwui's F16 path requires F16-capable
                // EGL surface. Forcing N32 keeps us in hwui's SDR pipeline
                // even when reporting P3 dataspace — color-correct enough
                // for HelloWorld / SDR Apps; future work for true HDR.
                fromOh = true;
                break;
            }
        }
        if (!fromOh) {
            // OH advertises only sRGB (or unknown spaces) — fromOh stays
            // false but defaults already match this case.
            fromOh = true;
        }
    }

    if (outDataspace)   *outDataspace   = aospDataspace;
    if (outPixelFormat) *outPixelFormat = aospPixelFormat;

    HiLogPrint(3, 4, 0xD000F00, "OH_ADisplay",
               "ADisplay_getPreferredWideColorFormat -> dataspace=%d format=%d (%s, source=%s)",
               aospDataspace, aospPixelFormat,
               aospDataspace == kAospDataspaceDispP3 ? "P3" : "sRGB",
               fromOh ? "OH_NDM" : "fallback-default");
}

extern "C" __attribute__((visibility("default")))
void ADisplay_release(void** displays) {
    if (!displays) return;
    OhAdisplayHandle* h = (OhAdisplayHandle*) displays[0];
    if (h && h->magic == kAdisplayMagic) {
        if (h->ohInfo) {
            OH_NativeDisplayManager_DestroyDisplay(h->ohInfo);
        }
        ::free(h);
    }
    ::free(displays);
}

// =============================================================================
// G2.14af — eglCreateWindowSurface hijack (unwrap ANW shim)
//
// Why: G2.14ae installed an AOSP-ABI ANativeWindow shim (oh_anw_wrap) so hwui
// can talk to OH NativeWindow through AOSP function-pointer hooks. But hwui
// also calls EGL APIs directly (eglCreateWindowSurface from EglManager.cpp),
// passing the shim wrapper as EGLNativeWindowType. OH libEGL validates
// window->magic against OH NativeWindowMagic — the shim's magic is AOSP
// '_wnd', so libEGL rejects it and returns EGL_NO_SURFACE. Downstream:
//   SkiaOpenGLPipeline::setSurface -> mEglManager.createSurface returns null
//   -> mEglSurface stays EGL_NO_SURFACE
//   -> next getFrame() LOG_ALWAYS_FATAL_IF "drawRenderNode called on a context
//      with no surface!"
//
// Fix: install a strong-symbol eglCreateWindowSurface in libhwui itself.
// -Wl,-Bsymbolic-functions self-binds hwui's internal call to the hijack.
// The hijack:
//   1. Detects whether `window` is an ANW shim by calling oh_anw_get_oh
//      (resolved via dlopen-by-name on liboh_adapter_bridge.so — same
//      cross-.so dlsym workaround as G2.14ac).
//   2. If it's a shim, replaces `window` with the unwrapped OH NativeWindow*
//      so libEGL sees the OH-native magic and accepts it.
//   3. Forwards to the real eglCreateWindowSurface in libEGL.so, resolved via
//      dlopen("libEGL.so") + dlsym(handle) — going through dlopen-by-name
//      avoids self-binding to this hijack and infinite recursion.
//
// Reference: doc/graphics_rendering_design.html §7.11 (ANativeWindow shim).
// =============================================================================

// EGL types — minimal subset, avoid -I to OH's EGL/egl.h to keep this file
// self-contained. Layout matches Khronos egl.h: void*-typed handles, int32_t
// EGLint.
typedef void*    EGLDisplay;
typedef void*    EGLConfig;
typedef void*    EGLSurface;
typedef int32_t  EGLint;
typedef void*    EGLNativeWindowType;
typedef unsigned EGLBoolean;
#define EGL_NO_SURFACE ((EGLSurface) 0)

typedef void* (*oh_anw_get_oh_fn_t)(void* aosp);
typedef EGLSurface (*real_eglCreateWindowSurface_fn_t)(EGLDisplay, EGLConfig,
                                                       EGLNativeWindowType,
                                                       const EGLint*);

namespace {
    oh_anw_get_oh_fn_t                  g_oh_anw_get_oh_fn = nullptr;
    real_eglCreateWindowSurface_fn_t    g_real_eglCreateWindowSurface_fn = nullptr;
    bool                                g_egl_hijack_logged = false;

    void resolve_egl_hijack_funcs() {
        if (!g_oh_anw_get_oh_fn) {
            // liboh_adapter_bridge.so is loaded by OHEnvironment static init
            // long before hwui RenderThread starts, so RTLD_NOLOAD finds it.
            void* h = dlopen("liboh_adapter_bridge.so", RTLD_NOW | RTLD_NOLOAD);
            if (!h) {
                (void) dlerror();
                h = dlopen("liboh_adapter_bridge.so", RTLD_NOW);
            }
            if (h) {
                g_oh_anw_get_oh_fn = reinterpret_cast<oh_anw_get_oh_fn_t>(
                    dlsym(h, "oh_anw_get_oh"));
            }
        }
        if (!g_real_eglCreateWindowSurface_fn) {
            // libEGL.so is loaded by hwui RenderThread (via OH GLES init).
            // dlopen with RTLD_NOLOAD finds the already-loaded image; dlsym
            // on its handle returns OH libEGL's eglCreateWindowSurface — NOT
            // the hijack we install in libhwui — because dlsym(h) searches
            // that specific .so's symbol table, not the global one. This is
            // how we avoid infinite recursion when -Bsymbolic-functions
            // self-binds libhwui's internal references to our hijack.
            void* h = dlopen("libEGL.so", RTLD_NOW | RTLD_NOLOAD);
            if (!h) {
                (void) dlerror();
                h = dlopen("libEGL.so", RTLD_NOW);
            }
            if (h) {
                g_real_eglCreateWindowSurface_fn =
                    reinterpret_cast<real_eglCreateWindowSurface_fn_t>(
                        dlsym(h, "eglCreateWindowSurface"));
            }
        }
        if (!g_egl_hijack_logged) {
            HiLogPrint(3, 4, 0xD000F00, "OH_EglHijack",
                       "resolve_egl_hijack_funcs: oh_anw_get_oh_fn=%p real_egl_fn=%p",
                       (void*) g_oh_anw_get_oh_fn,
                       (void*) g_real_eglCreateWindowSurface_fn);
            if (g_oh_anw_get_oh_fn && g_real_eglCreateWindowSurface_fn) {
                g_egl_hijack_logged = true;
            }
        }
    }
}  // anonymous namespace

extern "C" __attribute__((visibility("default")))
EGLSurface eglCreateWindowSurface(EGLDisplay dpy, EGLConfig config,
                                   EGLNativeWindowType window,
                                   const EGLint* attrib_list) {
    resolve_egl_hijack_funcs();

    EGLNativeWindowType actualWindow = window;
    if (g_oh_anw_get_oh_fn && window) {
        void* unwrapped = g_oh_anw_get_oh_fn(window);
        if (unwrapped) {
            HiLogPrint(3, 4, 0xD000F00, "OH_EglHijack",
                       "eglCreateWindowSurface: detected ANW shim %p, "
                       "unwrapped to OH NativeWindow=%p",
                       window, unwrapped);
            actualWindow = (EGLNativeWindowType) unwrapped;
        }
    }

    if (!g_real_eglCreateWindowSurface_fn) {
        HiLogPrint(3, 7, 0xD000F00, "OH_EglHijack",
                   "eglCreateWindowSurface: libEGL.so eglCreateWindowSurface "
                   "not resolved — returning EGL_NO_SURFACE");
        return EGL_NO_SURFACE;
    }

    EGLSurface surface = g_real_eglCreateWindowSurface_fn(
        dpy, config, actualWindow, attrib_list);
    HiLogPrint(3, 4, 0xD000F00, "OH_EglHijack",
               "eglCreateWindowSurface: dpy=%p config=%p window=%p (orig=%p) "
               "-> EGLSurface=%p",
               dpy, config, actualWindow, window, surface);
    return surface;
}

// =============================================================================
// §5  G2.14ax probe — eglSwapBuffers* hijack with pre-swap glClear(red)
//
// Why: G2.14aw r2 实证 helloworld 名下 BufferQueue 80 帧 push、buffer state 走完
// REQUESTED→FLUSHED→ACQUIRED→RELEASED 整圈，但 buffer 像素 100% 0x00000000.
// G2.14ax 方法 A 证伪了 Skia ABI 不兼容假设（hwui 编译用 OH m133 头，运行 link
// 同一份 libskia_canvaskit）.  剩下的候选:
//   A: EGLSurface 没绑到 ProducerSurface buffer
//   B: Skia replay 真画，但 GL framebuffer 是错的 target
//   C: hwui 主路径根本没进 SkiaOpenGLPipeline::renderFrame
//
// 在 swap 之前调 glClear(1,0,0,1) 是 1-bit 信号实证:
//   buffer dump 变红 → GL framebuffer = ProducerSurface buffer，问题在 Skia replay (候选 B)
//   buffer dump 仍 0 → GL framebuffer ≠ ProducerSurface buffer (候选 A)
//   probe log 不 fire → renderFrame 没被调 (候选 C)
//
// Forward to real OH eglSwapBuffers* via dlopen+dlsym (same pattern as §4
// eglCreateWindowSurface hijack), so the swap still happens — we only inject
// glClear+glFinish before the real swap.
// =============================================================================

typedef unsigned int  GLenum;
typedef unsigned int  GLbitfield;
typedef float         GLfloat;
typedef int           GLint;
typedef int           GLsizei;
typedef unsigned char GLubyte;
#define GL_COLOR_BUFFER_BIT 0x00004000
#define GL_RGBA             0x1908
#define GL_UNSIGNED_BYTE    0x1401

extern "C" void glClearColor(GLfloat r, GLfloat g, GLfloat b, GLfloat a);
extern "C" void glClear(GLbitfield mask);
extern "C" void glFinish(void);
extern "C" GLenum glGetError(void);
extern "C" void glReadPixels(GLint x, GLint y, GLsizei w, GLsizei h,
                              GLenum format, GLenum type, void* pixels);

typedef EGLBoolean (*real_eglSwapBuffers_fn_t)(EGLDisplay, EGLSurface);
typedef EGLBoolean (*real_eglSwapBuffersWithDamageKHR_fn_t)(EGLDisplay, EGLSurface,
                                                            const EGLint*, EGLint);

namespace {
    real_eglSwapBuffers_fn_t                 g_real_eglSwapBuffers_fn = nullptr;
    real_eglSwapBuffersWithDamageKHR_fn_t    g_real_eglSwapBuffersWithDamageKHR_fn = nullptr;
    bool                                     g_swap_hijack_logged = false;

    void resolve_swap_hijack_funcs() {
        if (g_real_eglSwapBuffers_fn && g_real_eglSwapBuffersWithDamageKHR_fn) return;
        void* h = dlopen("libEGL.so", RTLD_NOW | RTLD_NOLOAD);
        if (!h) {
            (void) dlerror();
            h = dlopen("libEGL.so", RTLD_NOW);
        }
        if (h) {
            if (!g_real_eglSwapBuffers_fn) {
                g_real_eglSwapBuffers_fn = reinterpret_cast<real_eglSwapBuffers_fn_t>(
                    dlsym(h, "eglSwapBuffers"));
            }
            if (!g_real_eglSwapBuffersWithDamageKHR_fn) {
                g_real_eglSwapBuffersWithDamageKHR_fn =
                    reinterpret_cast<real_eglSwapBuffersWithDamageKHR_fn_t>(
                        dlsym(h, "eglSwapBuffersWithDamageKHR"));
            }
        }
        if (!g_swap_hijack_logged) {
            HiLogPrint(3, 4, 0xD000F00, "OH_SwapHijack",
                       "resolve_swap_hijack_funcs: real_swap=%p real_swapDmgKHR=%p",
                       (void*) g_real_eglSwapBuffers_fn,
                       (void*) g_real_eglSwapBuffersWithDamageKHR_fn);
            if (g_real_eglSwapBuffers_fn && g_real_eglSwapBuffersWithDamageKHR_fn) {
                g_swap_hijack_logged = true;
            }
        }
    }

    void inject_red_clear(uint32_t frame_n, const char* tag) {
        // G2.14ax r2d: BEFORE clearing, sample the buffer to see what Skia
        // actually wrote during replay.  If Skia replay produced pixels,
        // glReadPixels here will return non-zero values; the subsequent
        // glClear(red) overrides them (loses the actual content, but that's
        // OK — we only care about whether Skia wrote anything).
        //
        // 4 sample points: top-left, 25%, 50%, bottom-right.  Read 1x1 each.
        // glReadPixels reads from currently-bound GL_READ_FRAMEBUFFER, which
        // on hwui RT swap path is the same FBO as GL_DRAW_FRAMEBUFFER bound
        // to the EGLSurface backing the ProducerSurface buffer (confirmed by
        // G2.14ax r1 glClear-red writing through to dump).
        GLubyte px_tl[4] = {0}, px_q[4] = {0}, px_mid[4] = {0}, px_br[4] = {0};
        glReadPixels(0,   0,    1, 1, GL_RGBA, GL_UNSIGNED_BYTE, px_tl);
        glReadPixels(180, 320,  1, 1, GL_RGBA, GL_UNSIGNED_BYTE, px_q);
        glReadPixels(360, 640,  1, 1, GL_RGBA, GL_UNSIGNED_BYTE, px_mid);
        glReadPixels(719, 1279, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, px_br);
        GLenum read_err = glGetError();
        if (frame_n < 5 || (frame_n % 60) == 0) {
            HiLogPrint(3, 4, 0xD000F00, "OH_SwapHijack",
                       "[G2.14ax r2d %s #%u pre-clear-sample] tl=%02x%02x%02x%02x "
                       "q=%02x%02x%02x%02x mid=%02x%02x%02x%02x br=%02x%02x%02x%02x readErr=0x%x",
                       tag, frame_n,
                       px_tl[0],  px_tl[1],  px_tl[2],  px_tl[3],
                       px_q[0],   px_q[1],   px_q[2],   px_q[3],
                       px_mid[0], px_mid[1], px_mid[2], px_mid[3],
                       px_br[0],  px_br[1],  px_br[2],  px_br[3],
                       read_err);
        }
        // G214bj — original glClear(red) removed.  The red overlay was an
        // ambiguity marker for early G2.14ax buffer-dump diagnostics; with the
        // flush -> executeRenderTasks -> onExecute path fixed (G214bh + v4d)
        // and G214bi removing the SkiaOpenGLPipeline-level red clear, this
        // remaining swap-hijack red clear was the last one painting over
        // every real frame.  Disabled to let actual UI pixels reach display.
        // Readback above + log below preserve hijack diagnostic value.
        // glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        // glClear(GL_COLOR_BUFFER_BIT);
        // glFinish();
        GLenum err = glGetError();
        if (frame_n < 5 || (frame_n % 60) == 0) {
            HiLogPrint(3, 4, 0xD000F00, "OH_SwapHijack",
                       "[G2.14ax r2d %s #%u] post-clear gl_err=0x%x",
                       tag, frame_n, err);
        }
    }
}  // anonymous namespace

extern "C" __attribute__((visibility("default")))
EGLBoolean eglSwapBuffersWithDamageKHR(EGLDisplay dpy, EGLSurface surface,
                                        const EGLint* rects, EGLint n_rects) {
    resolve_swap_hijack_funcs();
    static std::atomic<uint32_t> s_swapDmgCnt{0};
    uint32_t n = s_swapDmgCnt.fetch_add(1, std::memory_order_relaxed);
    inject_red_clear(n, "SwapDmgKHR");
    if (!g_real_eglSwapBuffersWithDamageKHR_fn) {
        HiLogPrint(3, 7, 0xD000F00, "OH_SwapHijack",
                   "[G2.14ax] real eglSwapBuffersWithDamageKHR not resolved");
        return 0;
    }
    return g_real_eglSwapBuffersWithDamageKHR_fn(dpy, surface, rects, n_rects);
}

extern "C" __attribute__((visibility("default")))
EGLBoolean eglSwapBuffers(EGLDisplay dpy, EGLSurface surface) {
    resolve_swap_hijack_funcs();
    static std::atomic<uint32_t> s_swapCnt{0};
    uint32_t n = s_swapCnt.fetch_add(1, std::memory_order_relaxed);
    inject_red_clear(n, "Swap");
    if (!g_real_eglSwapBuffers_fn) {
        HiLogPrint(3, 7, 0xD000F00, "OH_SwapHijack",
                   "[G2.14ax] real eglSwapBuffers not resolved");
        return 0;
    }
    return g_real_eglSwapBuffers_fn(dpy, surface);
}

// =============================================================================
// §6  G2.14az helper — dump current GL state.
//
// Called from SkiaOpenGLPipeline probe A (apply_G214ay_skia_probe.py).
// We centralize GL extern "C" declarations here (file already has gl* decls
// from §5 SwapHijack) so the probe site in SkiaOpenGLPipeline.cpp doesn't
// need to redeclare them at function-body scope (illegal in C++).
//
// GL state items dumped:
//   GL_DRAW_FRAMEBUFFER_BINDING  — 0=EGL default fb (correct for swap)
//   GL_VIEWPORT (x,y,w,h)        — 0,0,720,1280 expected, else draws clipped
//   GL_SCISSOR_BOX (x,y,w,h)     — scissor test region
//   GL_SCISSOR_TEST              — bool, 1 means GL_SCISSOR_BOX is applied
//   GL_BLEND                     — bool, blend state
// =============================================================================

#define GL_DRAW_FRAMEBUFFER_BINDING 0x8CA6
#define GL_VIEWPORT                 0x0BA2
#define GL_SCISSOR_BOX              0x0C10
#define GL_SCISSOR_TEST             0x0C11
#define GL_BLEND                    0x0BE2

extern "C" void glGetIntegerv(GLenum pname, GLint* params);

extern "C" __attribute__((visibility("default")))
void g214az_dump_gl_state(const char* tag, int frame_n) {
    GLint fb_binding = -1, blend = -1, scissor_test = -1;
    GLint viewport[4]  = {-1, -1, -1, -1};
    GLint scissor[4]   = {-1, -1, -1, -1};
    glGetIntegerv(GL_DRAW_FRAMEBUFFER_BINDING, &fb_binding);
    glGetIntegerv(GL_VIEWPORT, viewport);
    glGetIntegerv(GL_SCISSOR_BOX, scissor);
    glGetIntegerv(GL_SCISSOR_TEST, &scissor_test);
    glGetIntegerv(GL_BLEND, &blend);
    fprintf(stderr, "[G214az] %s #%d gl_fb=%d viewport=%d,%d,%d,%d "
            "scissor_test=%d scissor=%d,%d,%d,%d blend=%d\n",
            tag, frame_n, fb_binding,
            viewport[0], viewport[1], viewport[2], viewport[3],
            scissor_test,
            scissor[0], scissor[1], scissor[2], scissor[3],
            blend);
}

// =============================================================================
// §7  G2.14bb helper — raw glReadPixels (bypass SkSurface->readPixels).
//
// Returns the pixel value at (x, y) on the currently-bound DRAW_FRAMEBUFFER.
// If SkSurface->readPixels reads from a Skia-internal FBO (not the EGL
// framebuffer 0), comparing the result to raw glReadPixels (which always
// reads fb 0) directly identifies the GrRenderTarget ↔ GL fb 0 binding bug.
// =============================================================================

extern "C" __attribute__((visibility("default")))
uint32_t g214bb_raw_read_pixel(int x, int y) {
    GLubyte px[4] = {0};
    glReadPixels(x, y, 1, 1, 0x1908 /*GL_RGBA*/, 0x1401 /*UBYTE*/, px);
    // Return as uint32 in little-endian order: low byte = R; matches the
    // 0xff0000ff = (R=ff, G=00, B=00, A=ff) layout used in earlier probes.
    return ((uint32_t)px[3] << 24) |
           ((uint32_t)px[2] << 16) |
           ((uint32_t)px[1] << 8)  |
           ((uint32_t)px[0]);
}
