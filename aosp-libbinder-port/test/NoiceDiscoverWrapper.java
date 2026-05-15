// SPDX-License-Identifier: Apache-2.0
//
// W2-discover -- NoiceDiscoverWrapper.java (post-CR27 slim)
//
// Westlake CR27 (2026-05-13): extracted the 1174-LOC discovery harness into
// manifest-driven DiscoverWrapperBase + per-app .properties manifests so
// the same harness logic can drive a second app (McdDiscoverWrapper).
//
// Codex review #2 Tier 3 finding (verbatim quote):
//   "NoiceDiscoverWrapper.java is still sensible as a discovery harness,
//    but at 1174 LOC it is now carrying app config, boot sequencing,
//    service registration, synthetic activity/application info, and phase
//    orchestration.  It should become manifest-driven before it is reused
//    for a second app."
//
// All app-specific configuration (package name, APK path, Application
// class, MainActivity class, targetSdkVersion) is now in
// /data/local/tmp/westlake/noice.discover.properties.
//
// This file is now ~50 LOC.  Its only responsibilities are:
//   (1) Declare the JNI-bound println/eprintln natives.  These are
//       registered by name in art-latest/stubs/binder_jni_stub.cc which
//       the CR27 brief forbids modifying, so the natives MUST live on
//       this class (under the existing class name) -- not on the base.
//   (2) Pass a Printer (delegating to those natives) + the noice manifest
//       path to DiscoverWrapperBase.runFromManifest().
//
// All phase orchestration lives in DiscoverWrapperBase.java; all per-app
// constants live in the .properties file.  See M4_DISCOVERY.md §50 (CR27).

public class NoiceDiscoverWrapper {
    /**
     * Path to noice's manifest on the phone.  The manifest file is pushed
     * alongside the .dex by the build script + adb push pipeline.
     */
    static final String NOICE_MANIFEST =
            "/data/local/tmp/westlake/noice.discover.properties";

    // Native print helpers registered by name in art-latest/stubs/
    // binder_jni_stub.cc (Java_NoiceDiscoverWrapper_println /
    // Java_NoiceDiscoverWrapper_eprintln).  These bridge to fprintf
    // because System.out.println throws NPE in this cold-boot dalvikvm
    // (Charset isn't initialized).
    static native void println(String s);
    static native void eprintln(String s);

    public static void main(String[] args) {
        // Allow override from cmdline (mostly for ad-hoc testing); default
        // to the standard noice manifest path.
        String manifestPath = (args != null && args.length > 0)
                ? args[0] : NOICE_MANIFEST;
        DiscoverWrapperBase.runFromManifest(manifestPath, new DiscoverWrapperBase.Printer() {
            @Override public void println(String s) { NoiceDiscoverWrapper.println(s); }
            @Override public void eprintln(String s) { NoiceDiscoverWrapper.eprintln(s); }
        });
    }
}
