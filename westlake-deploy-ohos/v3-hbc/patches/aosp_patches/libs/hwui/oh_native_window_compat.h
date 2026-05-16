/*
 * oh_native_window_compat.h
 *
 * Compatibility layer for compiling libhwui on OH.
 * Provides ANativeWindow-compatible types and function stubs
 * that redirect to OH NativeWindow API at runtime.
 *
 * This header is force-included when compiling libhwui for OH,
 * via -include oh_native_window_compat.h
 *
 * Strategy:
 *   - Define ANativeWindow as typedef to OHNativeWindow (OH native type)
 *   - Stub out Android-specific ANativeWindow_* functions
 *   - Keep EGL interface unchanged (OH EGL accepts OHNativeWindow*)
 *   - ReliableSurface is replaced with a trivial passthrough
 */
#ifndef OH_NATIVE_WINDOW_COMPAT_H
#define OH_NATIVE_WINDOW_COMPAT_H

#ifdef HWUI_OH_SURFACE

#include <cstdint>
#include <cstddef>

// Forward declare OH NativeWindow type (struct from OH native_window.h)
struct NativeWindow;

// ANativeWindow: define as struct (not typedef) so EGL's forward declaration
// `struct ANativeWindow` in eglplatform.h is compatible.
// At runtime, this struct is actually OH's NativeWindow — they share the same
// memory layout entry point (no virtual table dependency for EGL operations).
struct ANativeWindow;

// OHNativeWindow alias (used by adapter code internally)
typedef struct NativeWindow OHNativeWindow;

// ANativeWindowBuffer: forward declaration only.
// AOSP nativebase/nativebase.h provides the full definition; we don't redefine.
struct ANativeWindowBuffer;

// ASurfaceControl: AOSP defines this as `typedef void* ASurfaceControl` in
// private/hwui/WebViewFunctor.h. Don't redefine here.
// We use forward declaration via void* compatibility.

// AHardwareBuffer stub
struct AHardwareBuffer;

// ============================================================
// ANativeWindow function stubs
// In the EGL hardware rendering path, most of these are NOT called
// directly by libhwui — EGL driver handles buffer management.
// These stubs prevent link errors.
// ============================================================

// ANativeWindow_* functions are declared in compat/android/native_window.h
// with proper extern "C" linkage. Don't redeclare here to avoid conflicts.
//
// The following functions are used by libhwui but are NOT in standard NDK
// android/native_window.h, so we declare them here with extern "C":

// Pull in the stub native_window.h so all ANativeWindow_* functions are visible
#include <android/native_window.h>

#ifdef __cplusplus
extern "C" {
#endif

// Additional functions used by libhwui that are NOT in standard NDK android/native_window.h
void ANativeWindow_setDequeueTimeout(ANativeWindow* w, int64_t timeout);
void ANativeWindow_tryAllocateBuffers(ANativeWindow* w);
int64_t ANativeWindow_getLastDequeueStartTime(ANativeWindow* w);
int64_t ANativeWindow_getLastDequeueDuration(ANativeWindow* w);
int64_t ANativeWindow_getLastQueueDuration(ANativeWindow* w);
int64_t ANativeWindow_getNextFrameId(ANativeWindow* w);

#ifdef __cplusplus
}
#endif

// ReliableSurface interceptor hooks: typedefs are defined in
// AOSP apex/window.h (which uses va_list signatures). Don't redefine here.
// The libhwui sources include apex/window.h directly when needed.

// ASurfaceControl is declared in AOSP private/hwui/WebViewFunctor.h as void*
// We do NOT touch it here.

// AHardwareBuffer / AChoreographer functions are declared in their dedicated
// stub headers (compat/android/hardware_buffer.h, compat/apex/choreographer.h)
// with proper extern "C" linkage. Don't redeclare here to avoid conflicts.

// ATrace: use the real AOSP <utils/Trace.h> when available.
// We don't redefine ATRACE_* macros here to avoid conflicts.

// Fence stubs (sync_wait is declared in compat/sync/sync.h with proper extern "C" linkage)

#endif  // HWUI_OH_SURFACE

#endif  // OH_NATIVE_WINDOW_COMPAT_H
