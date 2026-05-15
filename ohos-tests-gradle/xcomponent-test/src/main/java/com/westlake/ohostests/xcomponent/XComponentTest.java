package com.westlake.ohostests.xcomponent;

/**
 * CR60 follow-up #2 (post-E7): in-process OHOS NDK API-call acceptance.
 *
 * Smoke gate vs. real gate: HelloDlopen.java proved that
 * System.loadLibrary("native_window") returns without exception.
 * That only proves the loader resolved DT_NEEDED dependencies. THIS
 * test invokes one symbol per tier with bounded inputs and reports
 * success/failure-but-no-crash for each tier:
 *
 *   Tier 0: System.loadLibrary("xcomponent_bridge") succeeded
 *           (libxcomponent_bridge.so is on the board's library path
 *            and was loaded into the VM).
 *
 *   Tier 1: OH_NativeBuffer_Alloc(NULL) returned 0 (NULL) without SIGSEGV.
 *           Proves dlopen resolved a real, callable function pointer
 *           in libnative_buffer.so.
 *
 *   Tier 2: OH_NativeBuffer_Alloc({720, 1280, BGRA8888, CPU+DMA})
 *           returned a non-NULL OH_NativeBuffer* handle. Proves we
 *           can obtain a buffer-like producer object in-process
 *           without an XComponent host signed-up by the composer.
 *
 *   Tier 3: OH_NativeBuffer_Map → fill BGRA8888 RED → OH_NativeBuffer_Unmap
 *           Proves the producer pipeline (alloc + CPU map + write +
 *           unmap) doesn't crash. Without a consumer-side wiring
 *           (XComponent / Surface) the panel won't change — Tier 4
 *           (direct DRM with this buffer or the m6-drm-daemon route)
 *           is deferred separately.
 *
 * Each tier is run in isolation; failures past Tier 1 are
 * non-aborting — they record FAIL with reason and move on. Acceptance
 * markers (do NOT rename without also updating
 * scripts/run-ohos-test.sh xcomponent-test):
 *
 *   xcomp-test-start
 *   tier-0 status=OK | FAIL reason=...
 *   tier-1 status=OK ret=0 | FAIL reason=...
 *   tier-2 status=OK handle=0x<hex> | FAIL reason=...
 *   tier-3 status=OK | FAIL reason=...
 *   xcomp-test-done  highest=<n>
 *
 * Macro-shim contract: no Unsafe / setAccessible / per-app branches.
 * The native bridge is exempt per the existing rule (JNI/native code
 * is allowed reflective access to its own JNI ABI).
 */
public final class XComponentTest {

    /* DAYU200 panel native resolution — only used as a sane size for
     * the Tier-2 allocation. The driver doesn't actually present this
     * buffer (that's Tier 4 / m6-drm-daemon territory). */
    private static final int W = 720;
    private static final int H = 1280;

    public static void main(String[] args) {
        emit("xcomp-test-start");

        int highest = -1;

        // ── Tier 0: did the .so load? ────────────────────────────────
        if (XComponentBridge.isLoaded()) {
            emit("tier-0 status=OK lib=libxcomponent_bridge.so loaded");
            highest = 0;
        } else {
            String reason = XComponentBridge.getLoadError();
            emit("tier-0 status=FAIL reason=" + reason);
            emit("xcomp-test-done highest=" + highest);
            return;
        }

        // ── nativeInit: dlopen the OHOS libs from the bridge ─────────
        long initRc;
        try {
            initRc = XComponentBridge.nativeInit();
        } catch (Throwable t) {
            emit("tier-0b status=FAIL reason=nativeInit threw "
                    + t.getClass().getSimpleName() + ":" + t.getMessage());
            emit("xcomp-test-done highest=" + highest);
            return;
        }
        if (initRc != 1) {
            String e = XComponentBridge.nativeLastError();
            emit("tier-0b status=FAIL reason=nativeInit=" + initRc
                    + " err=" + e);
            emit("xcomp-test-done highest=" + highest);
            return;
        }
        emit("tier-0b status=OK nativeInit=1");

        // ── Tier 1: OH_NativeBuffer_Alloc(NULL) ──────────────────────
        // Spec: returns NULL when config is NULL. We just need it to
        // NOT crash. A return value of 0 (NULL) is the success case.
        try {
            long ret = XComponentBridge.nativeAllocNull();
            // NULL == 0 expected; any non-crash outcome is Tier-1 PASS
            // (the symbol is real and reaches producer-lib code that
            // handles invalid input gracefully).
            emit("tier-1 status=OK ret=" + ret
                    + " (expected 0 for Alloc(NULL); any non-crash passes)");
            highest = 1;
        } catch (Throwable t) {
            emit("tier-1 status=FAIL reason=" + t.getClass().getSimpleName()
                    + ":" + t.getMessage());
            emit("xcomp-test-done highest=" + highest);
            return;
        }

        // ── Tier 2: OH_NativeBuffer_Alloc(720x1280 BGRA8888 cpu+dma) ─
        long handle = 0;
        try {
            handle = XComponentBridge.nativeAlloc(W, H);
            if (handle == 0) {
                String e = XComponentBridge.nativeLastError();
                emit("tier-2 status=FAIL reason=alloc returned NULL err=" + e);
                emit("xcomp-test-done highest=" + highest);
                return;
            }
            emit("tier-2 status=OK handle=0x" + Long.toHexString(handle)
                    + " size=" + W + "x" + H + " fmt=BGRA8888");
            highest = 2;
        } catch (Throwable t) {
            emit("tier-2 status=FAIL reason=" + t.getClass().getSimpleName()
                    + ":" + t.getMessage());
            emit("xcomp-test-done highest=" + highest);
            return;
        }

        // Diagnostic: try GetSeqNum on the live buffer. -1 means the
        // symbol wasn't resolved or rejected the input; we don't gate
        // tier ascension on this.
        try {
            int seq = XComponentBridge.nativeGetSeqNum(handle);
            emit("diag GetSeqNum=" + seq);
        } catch (Throwable t) {
            emit("diag GetSeqNum threw: " + t);
        }

        // ── Tier 3: map → fill RED → unmap ───────────────────────────
        try {
            int rc = XComponentBridge.nativeFillRed(handle, W, H, W);
            if (rc == 0) {
                emit("tier-3 status=OK fill=BGRA8888 0xFFFF0000 (red, alpha=ff)"
                        + " size=" + W + "x" + H);
                highest = 3;
            } else {
                String e = XComponentBridge.nativeLastError();
                emit("tier-3 status=FAIL reason=fillRed rc=" + rc
                        + " err=" + e);
            }
        } catch (Throwable t) {
            emit("tier-3 status=FAIL reason=" + t.getClass().getSimpleName()
                    + ":" + t.getMessage());
        }

        // ── cleanup ──────────────────────────────────────────────────
        try {
            int urc = XComponentBridge.nativeUnref(handle);
            emit("cleanup Unref rc=" + urc);
        } catch (Throwable t) {
            emit("cleanup Unref threw: " + t);
        }

        emit("xcomp-test-done highest=" + highest);
    }

    /** stdout-only emit (DirectPrintStream); no android.util.Log
     *  dependency because that goes through the broken libcore.os
     *  path under the current dalvik kitkat bring-up. */
    private static void emit(String msg) {
        System.out.println(msg);
    }
}
