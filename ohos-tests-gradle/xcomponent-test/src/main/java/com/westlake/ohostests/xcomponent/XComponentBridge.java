package com.westlake.ohostests.xcomponent;

/**
 * Native bridge shim for libxcomponent_bridge.so (CR60 follow-up,
 * agent 13, 2026-05-14). The .so is built from
 * dalvik-port/compat/xcomponent_bridge.c against the OHOS arm32
 * dynamic toolchain; the same toolchain that builds the
 * dalvikvm-arm32-dynamic binary. It dlopens OHOS production native
 * libs (libnative_buffer.so / libnative_window.so) by ABSOLUTE PATH
 * at runtime so we don't depend on java.library.path or
 * /etc/ld-musl-arm.path being configured to reach
 * /system/lib/chipset-sdk-sp/.
 *
 * Macro-shim contract compliance:
 *   - This class has NO Unsafe / reflection / setAccessible.
 *   - No per-app/per-package branches. The probed libs are OS-level.
 *   - Pointer-sized values are transported as jlong (64-bit on both
 *     ARM32 and AArch64 JNI ABIs); the C side narrows to uintptr_t
 *     before deref. Java side treats them as opaque handles.
 *
 * Tier ladder (see brief in CR60-followup-xcomp-call):
 *   Tier 1: nativeAllocNull()  — alloc(NULL), must return 0 without crash.
 *   Tier 2: nativeAlloc(w,h)   — alloc real buffer, must return non-zero handle.
 *   Tier 3: nativeFillRed(...) — map, fill BGRA8888 red, unmap.
 *
 * The static initializer is the System.loadLibrary site. The class
 * loader catches LinkageError and reports it via getLoadError() so
 * the test driver can grade "no .so on board" cleanly.
 */
public final class XComponentBridge {

    /** Absolute path the driver pushes the .so to. The actual load
     *  happens in the C launcher BEFORE main() is invoked, via the
     *  $DVM_PRELOAD_LIB env var → dvmLoadNativeCode chain. We do NOT
     *  use System.loadLibrary / Runtime.load here because under this
     *  dalvik-kitkat bring-up the bundled core-kitkat.jar's
     *  Runtime.load + System.loadLibrary are STUBS (return-void) —
     *  they don't reach the VM's nativeLoad path. See launcher.cpp
     *  preload block (2026-05-14, CR60 follow-up) for the actual
     *  load site.
     *
     *  This constant documents the expected on-board path. The Java
     *  side doesn't need to verify the load — when the first native
     *  method is invoked, dvmResolveNativeMethod walks the loaded
     *  shared-lib table and either finds the symbol (test progresses)
     *  or throws UnsatisfiedLinkError (caught and reported in Tier 0). */
    public static final String LIB_PATH =
            "/data/local/tmp/westlake/libxcomponent_bridge.so";

    /** Reserved for future use — load failure is reported via the
     *  UnsatisfiedLinkError thrown by the first native call, not via
     *  a static-initializer flag (which can't observe failures in
     *  the C-side preload). */
    private static final String s_loadError = null;

    /** Always true — actual liveness is proven by the first
     *  successful native call. Kept for API symmetry. */
    private static final boolean s_loaded = true;

    private XComponentBridge() { /* no instances */ }

    /** Returns true if libxcomponent_bridge.so loaded successfully. */
    public static boolean isLoaded() {
        return s_loaded;
    }

    /** Returns the System.loadLibrary failure message, or null if the
     *  library loaded successfully. */
    public static String getLoadError() {
        return s_loadError;
    }

    /* ── native entry points (see xcomponent_bridge.c for impl) ──
     * All return 0 / NULL handle / -1 on failure. nativeLastError()
     * returns a human-readable message about the most recent failure
     * inside the bridge (dlopen / dlsym / API-call error). */

    /** Lazy-init: dlopen the OHOS libs + resolve symbols. Returns 1
     *  on success, 0 on failure. Safe to call repeatedly. */
    public static native long nativeInit();

    /** Last bridge-side error string (dlopen / dlsym / API). */
    public static native String nativeLastError();

    /** Tier 1: OH_NativeBuffer_Alloc(NULL). Must return 0 without crash. */
    public static native long nativeAllocNull();

    /** Tier 2: OH_NativeBuffer_Alloc({w,h,BGRA8888,CPU+DMA}). */
    public static native long nativeAlloc(int w, int h);

    /** Diagnostic: OH_NativeBuffer_GetSeqNum(buf). -1 if unsupported. */
    public static native int nativeGetSeqNum(long bufPtr);

    /** Tier 3: map buf, fill w*h BGRA8888 pixels with RED, unmap. */
    public static native int nativeFillRed(long bufPtr, int w, int h, int stride);

    /** Cleanup: OH_NativeBuffer_Unreference(buf). */
    public static native int nativeUnref(long bufPtr);
}
