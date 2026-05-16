/*
 * oh_anativewindow_shim.h — AOSP ANativeWindow ABI ↔ OH NDK bridge.
 *
 * Why: AOSP libhwui hardcodes ANativeWindow as a POD struct + function
 * pointer table (10 hooks at fixed offsets). OH NativeWindow is a C++
 * virtual class deriving from RefBase, with sptr<Surface> / unordered_map
 * / atomic / etc as members. ABIs are completely incompatible — hwui
 * reading OH struct via AOSP offsets returns RefBase internal members
 * as function pointers and crashes on call.
 *
 * Solution: this shim allocates a real AOSP-compatible ANativeWindow
 * struct, fills the 10 function pointers with adapter wrappers, and
 * stores the OH handle in a trailing field. hwui uses the AOSP ABI
 * normally; wrappers internally call OH NDK OH_NativeWindow_* functions.
 *
 * Reference: doc/graphics_rendering_design.html §7.11.
 */

#ifndef OH_ANATIVEWINDOW_SHIM_H
#define OH_ANATIVEWINDOW_SHIM_H

#include <stdint.h>

// Forward declare AOSP ANativeWindow / OH NativeWindow without pulling in
// their headers — keeps shim API surface minimal so cross-language callers
// (Java JNI, C++) only need this single header.
struct ANativeWindow;
struct OHNativeWindow;

#ifdef __cplusplus
extern "C" {
#endif

// Wrap an OH NativeWindow handle into an AOSP-ABI-compatible ANativeWindow.
// Returns a struct whose first field is `struct ANativeWindow` so any
// reinterpret_cast<ANativeWindow*> from the returned pointer is valid.
//
// The returned pointer is owned by the shim — call oh_anw_destroy to free.
// While alive, the shim holds a reference to `oh` and forwards all
// AOSP ANativeWindow operations (dequeueBuffer / queueBuffer / cancelBuffer
// / setSwapInterval / query / perform / lock_DEPRECATED / etc) to OH NDK
// equivalents.
//
// Thread-safety: shim is safe for concurrent ANativeWindow API usage from
// hwui RenderThread + main thread (matches AOSP ANativeWindow guarantees).
//
// Returns nullptr on allocation failure.
struct ANativeWindow* oh_anw_wrap(OHNativeWindow* oh);

// Free a shim allocated by oh_anw_wrap. Does NOT free the underlying OH
// NativeWindow — caller still owns that.
void oh_anw_destroy(struct ANativeWindow* aosp);

// Recover the OH handle from a shim-allocated ANativeWindow*. Returns
// nullptr if `aosp` was not produced by oh_anw_wrap (magic mismatch),
// allowing call-site validation.
OHNativeWindow* oh_anw_get_oh(struct ANativeWindow* aosp);

// G2.14ag: AdapterAnw refcount probes for AOSP NDK compat shims.
//
// AOSP NDK exports ANativeWindow_acquire/release that adapter implements in
// liboh_android_runtime.so / liboh_hwui_shim.so. Pre-G2.14ae they forwarded
// to OH NativeObjectReference/Unreference unconditionally; that breaks now
// because hwui's `ANativeWindow*` is an AdapterAnw shim whose offset 0 is
// AOSP magic '_wnd' (not OH NATIVE_OBJECT_MAGIC_WINDOW), failing OH's check.
//
// These helpers consume that case: returns 1 if `aosp` is a shim and the
// refcount op was applied to AdapterAnw's own atomic counter (AOSP-style
// common.incRef path). Returns 0 if `aosp` is NOT a shim — caller must
// then forward to OH NativeObjectReference / NativeObjectUnreference.
int oh_anw_try_acquire(struct ANativeWindow* aosp);
int oh_anw_try_release(struct ANativeWindow* aosp);

#ifdef __cplusplus
}  // extern "C"
#endif

#endif  // OH_ANATIVEWINDOW_SHIM_H
