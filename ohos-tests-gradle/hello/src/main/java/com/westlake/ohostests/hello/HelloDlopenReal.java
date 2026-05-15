package com.westlake.ohostests.hello;

import com.westlake.ohostests.xcomponent.XComponentBridge;

/**
 * CR60 follow-up E9a: prove a dynamic-PIE dalvikvm-arm32 actually
 * resolves a native library through the PURE-JAVA System.loadLibrary
 * path — no $DVM_PRELOAD_LIB env-var workaround — and that a JNI call
 * into the just-loaded .so returns a valid value (not a stub return).
 *
 * Why this gate exists (false positive of {@link HelloDlopen}):
 *   - Agent 11's CR60 spike (commit 8710bf4f) showed HelloDlopen.java
 *     reporting status=OK for every probed library. That run used
 *     core-kitkat.jar as the BCP; KitKat-stripped Runtime.loadLibrary /
 *     System.loadLibrary in that jar are STUBS (return-void). So "OK"
 *     proved nothing — the call returned without doing dlopen at all.
 *   - Agent 13 (commit e23124f1) worked around this with $DVM_PRELOAD_LIB
 *     which calls dvmLoadNativeCode in the launcher BEFORE main().
 *     That fix is per-launch / per-test, not durable for production.
 *
 * What this test proves (when run with core-android-x86.jar + NO env var):
 *   1. Pure Java {@code System.loadLibrary("xcomponent_bridge")} reaches
 *      the real Runtime.nativeLoad native, which calls dvmLoadNativeCode,
 *      which dlopens the .so and registers its JNI methods.
 *   2. A subsequent JNI call into the .so returns a meaningful value
 *      (XComponentBridge.nativeInit() returns 1 only if the bridge's
 *      internal dlopen chain reached libnative_buffer.so AND dlsym
 *      resolved OH_NativeBuffer_Alloc + OH_NativeBuffer_Unreference).
 *   3. A real OHOS NDK API returns useful data: nativeAlloc returns a
 *      non-zero handle, nativeGetSeqNum returns a non-negative seq.
 *
 * Marker contract (driver greps for these EXACT strings):
 *     hello-dlopen-real-start
 *     hello-dlopen-real loadLibrary OK
 *     hello-dlopen-real nativeInit=<rc>
 *     hello-dlopen-real nativeAllocNull=<rc>
 *     hello-dlopen-real nativeAlloc handle=<hex>
 *     hello-dlopen-real seqNum=<int>
 *     hello-dlopen-real nativeUnref rc=<rc>
 *     hello-dlopen-real-done passed=<int> failed=<int>
 * On any failure path it ALSO prints:
 *     hello-dlopen-real-FAIL stage=<name> reason=<msg>
 *
 * Macro-shim contract compliance:
 *   - NO Unsafe.allocateInstance / setAccessible / per-app branches.
 *   - System.loadLibrary is the standard Java API; we own neither
 *     Runtime nor System (those live in core-android-x86.jar — the real
 *     KitKat-derived implementation). The bridge .so + XComponentBridge
 *     class were added in E8 and are unchanged here.
 *   - This file does not add any new method on WestlakeContextImpl /
 *     any V2 substrate class. It only consumes the existing E8 bridge.
 *
 * Bitness discipline (per feedback_bitness_as_parameter.md):
 *   - All pointer values cross JNI as jlong → narrowed to uintptr_t
 *     inside the bridge. We do not assume 32 vs 64 bit on the Java side.
 */
public final class HelloDlopenReal {

    /** Library short name; resolved via java.library.path. The driver
     *  sets -Djava.library.path=$BOARD_DIR so this maps to
     *  $BOARD_DIR/libxcomponent_bridge.so (Runtime.mapLibraryName adds
     *  the lib prefix + .so suffix). */
    private static final String LIB_SHORT = "xcomponent_bridge";

    private HelloDlopenReal() { /* no instances */ }

    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;
        System.out.println("hello-dlopen-real-start");

        /* Stage 1: pure-Java loadLibrary. Goes through
         * Runtime.loadLibrary → Runtime.doLoad → nativeLoad (native).
         * If core-kitkat.jar is on the BCP, this returns without doing
         * anything (stub). With core-android-x86.jar, the real impl
         * iterates java.library.path, calls IoUtils.canOpenReadOnly,
         * then nativeLoad → dvmLoadNativeCode → dlopen. */
        try {
            System.loadLibrary(LIB_SHORT);
            System.out.println("hello-dlopen-real loadLibrary OK");
            passed++;
        } catch (Throwable t) {
            String reason = t.getClass().getSimpleName()
                    + ":" + String.valueOf(t.getMessage());
            System.out.println("hello-dlopen-real-FAIL stage=loadLibrary reason="
                    + reason);
            System.out.println("hello-dlopen-real-done passed=" + passed
                    + " failed=" + (failed + 1));
            return;
        }

        /* Stage 2: nativeInit. If loadLibrary stubbed (no real dlopen),
         * this throws UnsatisfiedLinkError because JNI dispatch can't
         * find Java_..._nativeInit — that's exactly the false-positive
         * detector we need. */
        long initRc;
        try {
            initRc = XComponentBridge.nativeInit();
            System.out.println("hello-dlopen-real nativeInit=" + initRc);
            if (initRc == 1) {
                passed++;
            } else {
                String err = safeLastError();
                System.out.println("hello-dlopen-real-FAIL stage=nativeInit reason="
                        + err);
                failed++;
                System.out.println("hello-dlopen-real-done passed=" + passed
                        + " failed=" + failed);
                return;
            }
        } catch (Throwable t) {
            String reason = t.getClass().getSimpleName()
                    + ":" + String.valueOf(t.getMessage());
            System.out.println("hello-dlopen-real-FAIL stage=nativeInit reason="
                    + reason);
            System.out.println("hello-dlopen-real-done passed=" + passed
                    + " failed=" + (failed + 1));
            return;
        }

        /* Stage 3: tier-1 call — alloc(NULL) returns 0 without SIGSEGV.
         * Same as E8 Tier 1; we repeat it here so a single PASS run
         * covers both the load path and the proven-by-E8 API. */
        try {
            long nullRc = XComponentBridge.nativeAllocNull();
            System.out.println("hello-dlopen-real nativeAllocNull=" + nullRc);
            passed++;
        } catch (Throwable t) {
            String reason = t.getClass().getSimpleName()
                    + ":" + String.valueOf(t.getMessage());
            System.out.println("hello-dlopen-real-FAIL stage=nativeAllocNull reason="
                    + reason);
            failed++;
        }

        /* Stage 4: tier-2 call — real buffer alloc returns non-zero
         * handle. Then read its seq num as a cheap liveness probe.
         * Finally release the reference. */
        long handle = 0;
        try {
            handle = XComponentBridge.nativeAlloc(720, 1280);
            System.out.println("hello-dlopen-real nativeAlloc handle=0x"
                    + Long.toHexString(handle));
            if (handle != 0) {
                passed++;
            } else {
                System.out.println("hello-dlopen-real-FAIL stage=nativeAlloc reason="
                        + safeLastError());
                failed++;
            }
        } catch (Throwable t) {
            String reason = t.getClass().getSimpleName()
                    + ":" + String.valueOf(t.getMessage());
            System.out.println("hello-dlopen-real-FAIL stage=nativeAlloc reason="
                    + reason);
            failed++;
        }

        if (handle != 0) {
            try {
                int seq = XComponentBridge.nativeGetSeqNum(handle);
                System.out.println("hello-dlopen-real seqNum=" + seq);
                if (seq >= 0) {
                    passed++;
                } else {
                    System.out.println("hello-dlopen-real-FAIL stage=nativeGetSeqNum reason=rc=" + seq);
                    failed++;
                }
            } catch (Throwable t) {
                String reason = t.getClass().getSimpleName()
                        + ":" + String.valueOf(t.getMessage());
                System.out.println("hello-dlopen-real-FAIL stage=nativeGetSeqNum reason="
                        + reason);
                failed++;
            }

            try {
                int unrefRc = XComponentBridge.nativeUnref(handle);
                System.out.println("hello-dlopen-real nativeUnref rc=" + unrefRc);
                passed++;
            } catch (Throwable t) {
                String reason = t.getClass().getSimpleName()
                        + ":" + String.valueOf(t.getMessage());
                System.out.println("hello-dlopen-real-FAIL stage=nativeUnref reason="
                        + reason);
                failed++;
            }
        }

        System.out.println("hello-dlopen-real-done passed=" + passed
                + " failed=" + failed);
    }

    /** Helper: ask the bridge for its last error string, defending
     *  against a second exception during cleanup. */
    private static String safeLastError() {
        try {
            String s = XComponentBridge.nativeLastError();
            return (s == null) ? "(null)" : s;
        } catch (Throwable t) {
            return "(nativeLastError threw: " + t.getClass().getSimpleName() + ")";
        }
    }
}
