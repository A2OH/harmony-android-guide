package com.westlake.ohostests.hello;

/**
 * CR60 follow-up E9b (Path Y): prove a dalvikvm-arm32-dynamic can
 * drive the DAYU200 DSI-1 panel directly via /dev/dri/card0 DRM/KMS,
 * with NO daemon, NO host APK, NO composer-side surface plumbing.
 *
 * This is the "in-process M6 daemon" — combines the same scan-out
 * pipeline that agent 7's standalone `drm_present` helper proved
 * works (PASS report at
 * artifacts/ohos-mvp/mvp2-red-square-drm/PASS_REPORT.md) but driven
 * from inside the JVM process. The Java side is small: load lib,
 * call nativePresent, log the result. All real work happens in the
 * C bridge (drm_inproc_bridge.c).
 *
 * Marker contract (driver greps these EXACT strings):
 *     hello-drm-inprocess-start
 *     hello-drm-inprocess loadLibrary OK
 *     hello-drm-inprocess holdSecs=<n>
 *     hello-drm-inprocess nativePresent rc=<0|11..21> reason=<msg>
 *     hello-drm-inprocess-done passed=<int> failed=<int>
 * On unexpected exception ALSO prints:
 *     hello-drm-inprocess-FAIL stage=<name> reason=<msg>
 *
 * Macro-shim contract: NO Unsafe / setAccessible / per-app branches.
 * Pointer-sized values: not relevant on this Java side; the bridge
 * uses uintptr_t throughout.
 *
 * Coexistence with composer_host:
 *   - This test does NOT kill composer_host itself. The driver
 *     subcommand `hello-drm-inprocess` handles that as a separate
 *     stage so it's auditable and reversible.
 *   - The C bridge tries SET_MASTER first; if composer_host still
 *     holds master, the call returns DRM_FAIL_MASTER (rc=12) and the
 *     test cleanly aborts without altering compositor state.
 */
public final class HelloDrmInProcess {

    /** Library short name; loaded via {@code System.loadLibrary} →
     *  resolved through {@code java.library.path}. The driver sets
     *  this to {@code $BOARD_DIR} so the absolute path is
     *  {@code $BOARD_DIR/libdrm_inproc_bridge.so}. */
    private static final String LIB_SHORT = "drm_inproc_bridge";

    private HelloDrmInProcess() { /* no instances */ }

    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;
        int holdSecs = 6;
        if (args != null && args.length >= 1) {
            try {
                holdSecs = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                /* leave default; the bridge clamps anyway */
            }
        }

        System.out.println("hello-drm-inprocess-start");
        System.out.println("hello-drm-inprocess holdSecs=" + holdSecs);

        try {
            System.loadLibrary(LIB_SHORT);
            System.out.println("hello-drm-inprocess loadLibrary OK");
            passed++;
        } catch (Throwable t) {
            String reason = t.getClass().getSimpleName()
                    + ":" + String.valueOf(t.getMessage());
            System.out.println("hello-drm-inprocess-FAIL stage=loadLibrary reason="
                    + reason);
            System.out.println("hello-drm-inprocess-done passed=" + passed
                    + " failed=" + (failed + 1));
            return;
        }

        int rc;
        try {
            rc = DrmInProcessBridge.nativePresent(holdSecs);
        } catch (Throwable t) {
            String reason = t.getClass().getSimpleName()
                    + ":" + String.valueOf(t.getMessage());
            System.out.println("hello-drm-inprocess-FAIL stage=nativePresent reason="
                    + reason);
            System.out.println("hello-drm-inprocess-done passed=" + passed
                    + " failed=" + (failed + 1));
            return;
        }

        String reason;
        try {
            reason = DrmInProcessBridge.nativeLastError();
            if (reason == null) reason = "(null)";
        } catch (Throwable t) {
            reason = "(nativeLastError threw: " + t.getClass().getSimpleName() + ")";
        }
        System.out.println("hello-drm-inprocess nativePresent rc=" + rc
                + " reason=" + reason);

        if (rc == 0) {
            passed++;
        } else {
            failed++;
        }
        System.out.println("hello-drm-inprocess-done passed=" + passed
                + " failed=" + failed);
    }
}
