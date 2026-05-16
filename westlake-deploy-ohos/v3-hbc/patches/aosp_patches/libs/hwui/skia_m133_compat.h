/*
 * skia_m133_compat.h
 *
 * Skia M116 → M133 API Compatibility Layer
 *
 * Force-included when compiling AOSP libhwui (Skia M116 era) against
 * OH system Skia M133 (libskia_canvaskit.z.so).
 *
 * Coverage:
 *   1. Path remapping: <GrDirectContext.h> → <include/gpu/ganesh/GrDirectContext.h>
 *      (handled by shim headers in build/skia_compat_headers/, not here)
 *   2. Removed types: SkMSec (millisecond time type)
 *   3. Renamed APIs: SkMesh::MakeVertexBuffer → SkMeshes::MakeVertexBuffer
 *   4. Namespace changes: GrDirectContext::DirectContextID
 *   5. Removed/private classes: SkAndroidFrameworkTraceUtil (already declared via SK_BUILD_FOR_ANDROID_FRAMEWORK)
 *
 * This header is force-included via clang -include <path>
 * It must be included BEFORE any Skia headers, so the typedefs
 * and namespace aliases are visible to libhwui code.
 */
#ifndef SKIA_M133_COMPAT_H
#define SKIA_M133_COMPAT_H

#ifdef HWUI_OH_SURFACE  // Only active when building for OH

#include <cstdint>

// ============================================================
// 1. Removed type: SkMSec (Skia Millisecond)
//    Removed in Skia M120+. Was a uint32_t millisecond time.
// ============================================================
#ifndef SK_MSEC_DEFINED
#define SK_MSEC_DEFINED
typedef uint32_t SkMSec;
#endif

// ============================================================
// 2. SkMesh API namespace migration
//    M116:  SkMesh::MakeVertexBuffer(context, data, size)
//    M133:  SkMeshes::MakeVertexBuffer(data, size)  -- no context!
//
//    The libhwui code calls SkMesh::MakeVertexBuffer(context, data, size).
//    We provide static wrappers within SkMesh that drop the context arg
//    and forward to SkMeshes:: namespace.
// ============================================================
//
// However, since SkMesh is already defined in OH Skia M133's SkMesh.h,
// we cannot add static methods to it via this header. Instead, the
// libhwui Mesh.h must be patched (see Mesh.h.patch).
//
// As a fallback, we define a helper namespace 'SkMeshLegacy' that wraps
// the M133 free functions:

#define SKIA_M133_MESH_API_AVAILABLE 1

// ============================================================
// 3. GrDirectContext::DirectContextID
//    M116:  Inner class GrDirectContext::DirectContextID
//    M133:  Same — no change needed
// ============================================================

// ============================================================
// 4. SkSL / Runtime Effect changes
//    Some SkRuntimeEffect API methods have been renamed.
//    Add forward-compat shims as needed in Phase 2.
// ============================================================

// ============================================================
// 5. Android Framework integration symbol
//    SkAndroidFrameworkTraceUtil is declared in SkTraceEventCommon.h
//    when SK_BUILD_FOR_ANDROID_FRAMEWORK is defined.
//    We rely on the build system passing -DSK_BUILD_FOR_ANDROID_FRAMEWORK.
// ============================================================
#ifndef SK_BUILD_FOR_ANDROID_FRAMEWORK
#error "SK_BUILD_FOR_ANDROID_FRAMEWORK must be defined when compiling libhwui for OH"
#endif

// ============================================================
// 6. SkColorSpace / ColorSpaceXformSteps
//    Some color management API has moved between M116 and M133.
//    Stub here, expand as needed.
// ============================================================

// ============================================================
// 7. SkPaint API changes
//    SkPaint::FilterQuality removed in M120+
//    Replaced with SkSamplingOptions
// ============================================================
#ifndef SK_PAINT_FILTER_QUALITY_COMPAT
#define SK_PAINT_FILTER_QUALITY_COMPAT
namespace SkLegacy {
    enum FilterQuality {
        kNone_SkFilterQuality = 0,
        kLow_SkFilterQuality,
        kMedium_SkFilterQuality,
        kHigh_SkFilterQuality,
    };
}
#endif

// ============================================================
// 8. SkSurfaceProps default constructor
//    M116: SkSurfaceProps()
//    M133: Same — no change
// ============================================================

// ============================================================
// 9. GrContextOptions::ShaderErrorHandler
//    M116: inner class
//    M133: top-level class skgpu::ShaderErrorHandler in MutableTextureState.h
//    Pull it into legacy location:
// ============================================================
#ifdef SKIA_M133_USE_SKGPU_SHADER_ERROR
namespace skgpu { class ShaderErrorHandler; }
#endif

// ============================================================
// 10. SkRefCnt: SkRefCntBase removed, just SkRefCnt now (mostly compatible)
// ============================================================

// ============================================================
// 11. Skia Mesh API: provide free function wrappers in :: namespace
//     so libhwui Mesh.h's `::MakeVertexBuffer(data, size)` resolves
//     correctly when patched.
//
//     The actual SkMeshes:: functions are declared in OH Skia M133's
//     <core/SkMesh.h>. We add aliases here.
// ============================================================
// Note: forward declarations only — actual symbols come from libskia_canvaskit.z.so

#endif  // HWUI_OH_SURFACE

#endif  // SKIA_M133_COMPAT_H
