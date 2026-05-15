// SPDX-License-Identifier: Apache-2.0
//
// W2-discover -- McdDiscoverWrapper.java
//
// Westlake CR27 (2026-05-13): second consumer of DiscoverWrapperBase.
// Created to satisfy codex review #2 Tier 3 finding that the discovery
// harness "should become manifest-driven before it is reused for a second
// app".  This class IS that second app.
//
// All app-specific configuration (package name, APK path, Application
// class, MainActivity class, targetSdkVersion) is in
// /data/local/tmp/westlake/mcd.discover.properties.
//
// Subject under discovery:
//   App: com.mcdonalds.app  (Real McD client, Android 15)
//   Application: com.mcdonalds.app.application.McDMarketApplication
//   MainActivity: com.mcdonalds.mcdcoreapp.common.activity.SplashActivity
//
// Run with:
//   bash /data/local/tmp/westlake/bin-bionic/mcd-discover.sh
//
// Expected behaviour at first run: PHASE A passes (system service probing
// is fully generic), PHASE B reaches PathClassLoader (subject to the same
// PF-arch-054 SIGBUS workaround CR24 applied for noice), PHASE C onward
// will surface McD-specific binder lookups (different binder hits than
// noice -- noice = Hilt + Compose; McD = stock AOSP injection + Splash
// activity).  The discovery output IS the deliverable.
//
// Native print bridge: this class does NOT declare its own JNI natives
// because art-latest/stubs/binder_jni_stub.cc is the registration site
// for `Java_<ClassName>_println` and the CR27 brief explicitly forbids
// touching art-latest.  Instead we follow the same pattern as
// DisplayServiceTest / NotificationServiceTest etc: bundle
// AsInterfaceTest.class into our dex and delegate println/eprintln to
// it (its natives ARE registered).  See
// aosp-libbinder-port/test/DisplayServiceTest.java:32-41 for the
// canonical example.
//
// See M4_DISCOVERY.md §50 (CR27) for rationale + delta count.

public class McdDiscoverWrapper {
    /**
     * Path to McD's manifest on the phone.  The manifest file is pushed
     * alongside the .dex by the build script + adb push pipeline.
     */
    static final String MCD_MANIFEST =
            "/data/local/tmp/westlake/mcd.discover.properties";

    private static void println(String s) {
        // Delegate to AsInterfaceTest's println -- registered by
        // JNI_OnLoad_binder when android_runtime_stub loads, because
        // AsInterfaceTest.class is bundled into McdDiscoverWrapper.dex.
        AsInterfaceTest.println(s);
    }

    private static void eprintln(String s) {
        AsInterfaceTest.eprintln(s);
    }

    public static void main(String[] args) {
        // Allow override from cmdline (mostly for ad-hoc testing); default
        // to the standard McD manifest path.
        String manifestPath = (args != null && args.length > 0)
                ? args[0] : MCD_MANIFEST;
        DiscoverWrapperBase.runFromManifest(manifestPath, new DiscoverWrapperBase.Printer() {
            @Override public void println(String s) { McdDiscoverWrapper.println(s); }
            @Override public void eprintln(String s) { McdDiscoverWrapper.eprintln(s); }
        });
    }
}
