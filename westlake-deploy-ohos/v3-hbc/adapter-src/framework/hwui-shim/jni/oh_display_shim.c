/*
 * oh_display_shim.c
 *
 * PENDING STUB — NOT a real implementation.
 *
 * Provides the 4 ADisplay_* AOSP NDK symbols that libhwui references but
 * OH has no corresponding native display NDK for. These are AOSP-only APIs
 * (defined in frameworks/native/libs/nativedisplay/include/apex/display.h),
 * used by libhwui's RenderThread / DeviceInfo / FrameInfoVisualizer to
 * query physical display capability (refresh rate, color format, etc.).
 *
 * Current status: all 4 functions are tier-4 stubs (AOSP semantic defaults
 * — see memory project_liboh_android_runtime.md §"实现策略分三类" §4).
 * They satisfy link relocation so bridge.so / libhwui.so can dlopen, but
 * any real use of multi-display / wide-color / refresh-rate-aware rendering
 * paths will silently fall back to single-display 60Hz sRGB.
 *
 * Why this is tier-4 (not a code smell):
 *   - OH truly has no matching native API (OH uses its own DisplayManager,
 *     but not via a C NDK layer at /system/android/lib).
 *   - AOSP allows graceful degradation to 0-display / internal-type /
 *     sRGB-SRGB8888 when hardware info is unavailable (see AOSP's own
 *     dumb display fallback in frameworks/base/libs/hwui/DeviceInfo.cpp).
 *   - Hello World TextView renders via Skia GL path that does not consult
 *     display info. More complex apps (animated GIF, HDR) will see
 *     degraded defaults until these are promoted to real implementations.
 *
 * Promotion path when needed:
 *   - Wire through OH innerAPI (DisplayManager::GetDefaultDisplay or
 *     DisplayManager::GetDisplayById), translating to ADisplay* opaque
 *     pointer → ADisplayType / ADataSpace values.
 *   - See graphics_rendering_design.html appendix A.4 for the promotion
 *     checklist.
 *
 * Registered in doc/graphics_rendering_design.html as a PENDING STUB.
 */

#include <stdint.h>
#include <stddef.h>

/*
 * AOSP NDK types (forward declared to avoid pulling in AOSP headers
 * here — the shim only needs to satisfy link symbols, and all arg
 * pointers are treated opaquely).
 */
typedef struct ADisplay ADisplay;
typedef int32_t ADataSpace;
typedef int32_t AHardwareBuffer_Format;
typedef int32_t ADisplayType;

/* ADisplayType enum values from AOSP display.h */
#define ADISPLAY_TYPE_INTERNAL   0
#define ADISPLAY_TYPE_EXTERNAL   1

/* ADataSpace values from AOSP */
#define ADATASPACE_UNKNOWN       0
#define ADATASPACE_SRGB          142671872  /* standard sRGB, AOSP value */

/* AHardwareBuffer_Format from AOSP */
#define AHARDWAREBUFFER_FORMAT_R8G8B8A8_UNORM   1

/* --------------------------------------------------------------------- */

/*
 * ADisplay_acquirePhysicalDisplays
 *
 * Real AOSP: enumerates all physical displays, returns owned array that
 * caller releases via ADisplay_release.
 *
 * Stub: report zero displays — caller's for-loop over the array will
 * execute zero iterations, which is the AOSP-defined semantic for
 * "headless" / "no display info" scenarios. Safe for Hello World;
 * degraded for multi-display / external-display cases.
 */
int ADisplay_acquirePhysicalDisplays(ADisplay*** outDisplays) {
    if (outDisplays) {
        *outDisplays = NULL;
    }
    return 0;  /* 0 displays */
}

/*
 * ADisplay_release
 *
 * Real AOSP: frees the array allocated by acquirePhysicalDisplays.
 *
 * Stub: since we never allocate (returned NULL above), no-op.
 */
void ADisplay_release(ADisplay** displays) {
    (void)displays;  /* not allocated, nothing to free */
}

/*
 * ADisplay_getDisplayType
 *
 * Real AOSP: reads hardware display type (internal panel vs HDMI vs cast).
 *
 * Stub: assume internal panel (DAYU200 has a built-in display). This is
 * only reached if somehow caller got a non-NULL ADisplay*, which is
 * impossible under acquirePhysicalDisplays's stub (returns 0 displays) —
 * this function is effectively unreachable, but we provide a sensible
 * default in case libhwui or Skia does an implicit display query via
 * a path we haven't traced.
 */
ADisplayType ADisplay_getDisplayType(ADisplay* display) {
    (void)display;
    return ADISPLAY_TYPE_INTERNAL;
}

/*
 * ADisplay_getPreferredWideColorFormat
 *
 * Real AOSP: reports the wide-color-gamut format the display prefers
 * (sRGB / P3 / BT2020 / etc.) for HDR rendering path selection.
 *
 * Stub: sRGB + RGBA8888 (basic 8-bit-per-channel standard gamut).
 * Hello World TextView in default sRGB renders correctly here. Real HDR
 * content will be tone-mapped to sRGB (quality loss, not crash).
 */
void ADisplay_getPreferredWideColorFormat(ADisplay* display,
                                          ADataSpace* outDataspace,
                                          AHardwareBuffer_Format* outPixelFormat) {
    (void)display;
    if (outDataspace) {
        *outDataspace = ADATASPACE_SRGB;
    }
    if (outPixelFormat) {
        *outPixelFormat = AHARDWAREBUFFER_FORMAT_R8G8B8A8_UNORM;
    }
}
