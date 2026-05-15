package com.westlake.ohostests.hello;

/**
 * CR60 followup stretch: prove a dynamic-PIE dalvikvm-arm32 can call
 * System.loadLibrary against an OHOS production native library at
 * runtime — the smoke test (ohos_dlopen_smoke.c) only proved C-level
 * dlopen; this confirms the same primitive is reachable from the dex
 * bytecode side, which is the actual gate for in-process XComponent.
 *
 * Acceptance markers (do NOT change wording without also updating any
 * future driver subcommand that greps for them):
 *     dlopen-test-start
 *     dlopen-result name=<libname> status=OK
 * OR  dlopen-result name=<libname> status=FAIL reason=<message>
 *     dlopen-test-done
 *
 * We attempt each library in isolation; the test logs success/failure
 * per-library and never aborts the JVM. If ANY library resolves, the
 * dynamic-PIE substrate is validated for runtime native lookup.
 *
 * Bitness note: pointer-width is irrelevant on the Java side; the
 * actual dlopen happens inside the VM's native dispatch. Per the
 * macro-shim contract this class uses NO Unsafe / setAccessible /
 * per-app branches.
 */
public class HelloDlopen {

    /** Libraries to try. Short names match how System.loadLibrary
     *  resolves them: System.loadLibrary("foo") → looks for "libfoo.so"
     *  on java.library.path. The smoke test proved these dlopen cleanly
     *  via absolute path; the System.loadLibrary route exercises the
     *  same musl ld-musl-arm.so.1 path internally. */
    private static final String[] LIBS = new String[] {
        "ace_napi.z",
        "native_window",
        "ace_ndk.z",
    };

    public static void main(String[] args) {
        System.out.println("dlopen-test-start");
        int ok = 0;
        int fail = 0;
        for (int i = 0; i < LIBS.length; i++) {
            String name = LIBS[i];
            try {
                System.loadLibrary(name);
                System.out.println("dlopen-result name=" + name + " status=OK");
                ok++;
            } catch (Throwable t) {
                /* Catch Throwable (not Exception) so UnsatisfiedLinkError —
                 * which extends LinkageError extends Error — is captured.
                 * Don't recurse into t.getCause(); the short message is
                 * sufficient for the driver to grade. */
                String reason = t.getClass().getSimpleName()
                        + ":" + t.getMessage();
                System.out.println("dlopen-result name=" + name
                        + " status=FAIL reason=" + reason);
                fail++;
            }
        }
        System.out.println("dlopen-test-summary ok=" + ok + " fail=" + fail);
        System.out.println("dlopen-test-done");
    }
}
