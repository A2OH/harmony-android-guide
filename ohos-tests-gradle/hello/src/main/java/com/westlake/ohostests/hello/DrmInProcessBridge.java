package com.westlake.ohostests.hello;

/**
 * CR60 follow-up E9b (Path Y) JNI declarations for
 * libdrm_inproc_bridge.so. Symmetric to XComponentBridge from E8:
 * Java declares native methods; the .so dlopens at System.loadLibrary
 * time; the C side does the actual DRM/KMS work.
 *
 * The bridge does NOT touch composer_host — the driver subcommand
 * `hello-drm-inprocess` takes care of stopping/restarting the
 * compositor around this test (same protocol as the existing
 * red-square-drm test, which the bridge inherits its scan-out pipeline
 * from).
 *
 * Macro-shim contract compliance: NO Unsafe / setAccessible / per-app
 * branches. Holds no per-instance state; all bridge state lives in C
 * statics (last-error string only).
 */
public final class DrmInProcessBridge {

    /** Path the driver pushes the .so to. Documented here so the
     *  driver subcommand and any future operator running by hand stay
     *  in sync. */
    public static final String LIB_PATH =
            "/data/local/tmp/westlake/libdrm_inproc_bridge.so";

    private DrmInProcessBridge() { /* no instances */ }

    /**
     * Drive /dev/dri/card0 DSI-1 connector to display RED.
     *
     * Pipeline mirrors {@code dalvik-port/compat/drm_present.c} (agent
     * 7's MVP-2 helper) but executes in-process inside the calling
     * dalvikvm-arm32-dynamic — no separate aarch64 static binary.
     *
     * @param holdSecs how long to keep scan-out before tearing down
     *                 (0..120; clamped on the C side). 6s is the
     *                 driver default — enough for a phone-camera capture.
     * @return 0 on success (panel was RED for ~holdSecs); one of the
     *         {@code DRM_FAIL_*} codes from the bridge .c on failure.
     */
    public static native int nativePresent(int holdSecs);

    /** Last bridge-side error / success descriptor. On success
     *  returns "OK crtc=… fb=… conn=… mode=WxH hold=Ns". */
    public static native String nativeLastError();
}
