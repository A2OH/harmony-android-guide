// SPDX-License-Identifier: Apache-2.0
//
// DrmInprocessPresenter — E12 Java entry point to the in-process DRM
// bridge. JNI-declares the new "argb buffer + hold seconds" variant
// that libdrm_inproc_bridge.so exposes (drm_inproc_bridge.c).
//
// E9b's existing nativePresent(int holdSecs) hardcodes RED; this new
// nativePresentArgb takes a caller-supplied ARGB8888 int[] so the
// launcher can route any View.onDraw output through the same DRM/KMS
// pipeline (CREATE_DUMB + MAP_DUMB + mmap + write + ADDFB2 + SETCRTC +
// hold + teardown). Bridge does the composer_host coexistence + master
// retry the same way E9b's nativePresent does.
//
// Macro-shim contract: no Unsafe, no setAccessible, no per-app branches.
// All pointer-sized values stay on the C side as uintptr_t / size_t;
// Java only marshals primitive ints + an int[].

package com.westlake.ohostests.inproc;

public final class DrmInprocessPresenter {

    /** Library short-name; loaded via System.loadLibrary in the
     *  launcher (java.library.path set by the driver subcommand to
     *  $BOARD_DIR). The .so itself is the same artifact E9b uses;
     *  we just add a new JNI entry point. */
    public static final String LIB_SHORT = "drm_inproc_bridge";

    private DrmInprocessPresenter() { /* no instances */ }

    /**
     * Present an ARGB8888 buffer on the DSI panel via DRM/KMS direct
     * scan-out, fully in-process.
     *
     * @param argb     ARGB8888 pixel grid, row-major, length == w*h.
     *                 Element layout: bits 31..24 = A (ignored on rk3568
     *                 dumb BO), 23..16 = R, 15..8 = G, 7..0 = B.
     * @param w        width in pixels (must match DSI mode; 720 on DAYU200).
     * @param h        height in pixels (must match DSI mode; 1280 on DAYU200).
     * @param holdSecs seconds to keep scan-out before teardown (0..120,
     *                 clamped on C side). 6s is the driver default —
     *                 enough for a phone-camera capture.
     * @return 0 on success (panel showed the buffer for ~holdSecs);
     *         one of the DRM_FAIL_* codes from drm_inproc_bridge.c on
     *         failure. {@link #lastError()} returns a human-readable
     *         description for both success and failure cases.
     */
    public static int present(int[] argb, int w, int h, int holdSecs) {
        return nativePresentArgb(argb, w, h, holdSecs);
    }

    /** Last bridge-side error / success descriptor. Format on success:
     *  "OK crtc=… fb=… conn=… mode=WxH hold=Ns argb-pixels=N". */
    public static String lastError() {
        return nativeLastError();
    }

    // ---- JNI declarations -------------------------------------------

    private static native int nativePresentArgb(int[] argb, int w, int h, int holdSecs);

    private static native String nativeLastError();
}
