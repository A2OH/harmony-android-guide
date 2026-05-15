// SPDX-License-Identifier: Apache-2.0
//
// M7-Step1 (CR38 §5 acceptance fixture) -- NoiceLauncher.java
//
// Driven by `scripts/run-noice-westlake.sh`.  This is the M7 entry-point
// counterpart to the W2 NoiceDiscoverWrapper.  M7 needs an end-to-end
// noice launch through the V2 substrate while the M5 audio_flinger
// daemon and M6 surface_daemon are also running -- exactly what the
// discovery harness already does, since DiscoverWrapperBase reaches
// PHASE G4 (MainActivity.onCreate(null)) on cfb7c9e3 today.
//
// CR38 §11 anti-drift contract: ZERO per-app code, ZERO new architectural
// decisions.  This launcher:
//   1. Emits an M7-specific startup marker that the orchestrator script
//      greps to confirm the dalvikvm process really booted into the M7
//      launch path (and not into one of the bare discovery harnesses).
//   2. Delegates to NoiceDiscoverWrapper.main() with the same noice
//      manifest path the discovery harness uses, so all PHASE-A..G
//      orchestration is shared.  NoiceDiscoverWrapper's existing JNI
//      println/eprintln natives (already registered in
//      art-latest/stubs/binder_jni_stub.cc) carry the print stream.
//
// Why use NoiceDiscoverWrapper as the JNI binding class:
// The brief explicitly forbids edits to `art-latest/`.  Today
// `binder_jni_stub.cc` JNI_OnLoad registers native methods for
// `HelloBinder`, `AsInterfaceTest`, `ActivityServiceTest`, and
// `NoiceDiscoverWrapper` (in that order).  If we declared `static native
// void println(String)` on NoiceLauncher, JNI_OnLoad would fail to bind
// it because the corresponding Java_NoiceLauncher_println symbol does
// not exist in art-latest.  Instead, NoiceLauncher carries NO native
// methods; ALL print I/O routes through NoiceDiscoverWrapper's already-
// bound natives.  Same result on the wire, zero art-latest edits.
//
// Why this isn't drift: NoiceLauncher is a 30-line dispatcher; the
// real discovery+launch logic remains in DiscoverWrapperBase /
// NoiceDiscoverWrapper.  The launch path is **identical** to a
// noice-discover.sh run -- run-noice-westlake.sh just adds the M5+M6
// daemon orchestration around it.  See CR38 §1.3:
//   "The architectural delta between [pre-M5/M6] and [M7 target] is
//   exactly: two native daemons run, framework-side audio + surface
//   paths reach them..."
//
// Anti-drift compliance: aosp-shim.dex unchanged; framework.jar
// unchanged; art-latest unchanged; daemons unchanged; ZERO per-app
// branches in this file (manifest path is the noice manifest only
// because this class is named NoiceLauncher; analogous McdLauncher
// would point at mcd.discover.properties -- exactly the
// NoiceDiscoverWrapper/McdDiscoverWrapper pattern CR27 established).

public class NoiceLauncher {
    /** Standard noice manifest path on the phone. */
    static final String NOICE_MANIFEST =
            "/data/local/tmp/westlake/noice.discover.properties";

    public static void main(String[] args) {
        // Load libandroid_runtime_stub.so BEFORE invoking any native
        // method on NoiceDiscoverWrapper.  art-latest/stubs/binder_jni_
        // _stub.cc::JNI_OnLoad_binder registers Java_NoiceDiscoverWrapper_
        // println/eprintln only when this .so is loaded (the lookup is
        // by class name; if we try println() before loadLibrary the
        // VM raises UnsatisfiedLinkError).  DiscoverWrapperBase
        // .runFromManifest does this itself but only after entering
        // runFromManifest; we need the natives bound up here too so
        // the M7 marker line below succeeds.
        try {
            System.loadLibrary("android_runtime_stub");
        } catch (UnsatisfiedLinkError e) {
            // Falling back to stderr -- if the native lib won't load,
            // discovery is going to fail anyway.  Print via the native
            // System.err which DOES work pre-Charset-init (vs
            // System.out which throws NPE inside println() -> writeBytes
            // -> Charset.UTF_8).
            System.err.println("M7_LAUNCHER: loadLibrary(android_runtime_stub) "
                    + "FAILED: " + e);
        }

        // Emit the M7 startup marker via the existing NoiceDiscoverWrapper
        // native println.  The orchestrator greps for this exact prefix
        // ("M7_LAUNCHER:") to confirm we entered through this path.
        NoiceDiscoverWrapper.println(
                "M7_LAUNCHER: NoiceLauncher.main() entered "
                + "(delegating to NoiceDiscoverWrapper.main)");

        // Delegate to NoiceDiscoverWrapper -- same code path, same
        // CharsetPrimer / ServiceRegistrar / PHASE A-G orchestration that
        // reaches MainActivity.onCreate body today.  The brief's S1/S2/S3
        // acceptance signals are emitted INSIDE DiscoverWrapperBase as
        // PHASE outcome lines ("PHASE G4: ...", "PHASE E: ...");
        // the orchestrator greps both the M7_LAUNCHER marker (this file)
        // and the PHASE markers (DiscoverWrapperBase) for verdicts.
        //
        // NoiceDiscoverWrapper.main(args) calls System.exit(0) inside
        // DiscoverWrapperBase.runFromManifest -- we never return.
        String[] childArgs = (args != null && args.length > 0)
                ? args
                : new String[] { NOICE_MANIFEST };
        NoiceDiscoverWrapper.main(childArgs);
    }
}
