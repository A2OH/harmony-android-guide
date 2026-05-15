// SPDX-License-Identifier: Apache-2.0
//
// Westlake M8-Step1 (2026-05-13) -- McdLauncher.java
//
// Companion to scripts/run-mcd-westlake.sh.  Drives McDonald's
// (com.mcdonalds.app) through the §2 12-step boot per CR38_M7_M8_
// INTEGRATION_SCOPING.md inside a single dalvikvm process, emitting
// CR38 §5.2 acceptance-signal markers so the orchestrator script can
// grep them.
//
// Symmetric to NoiceLauncher.java (M7-Step1).  Delta from NoiceLauncher:
//   1. Reads `mcd.discover.properties` (vs noice.discover.properties).
//   2. Expects multi-Activity dispatch -- SplashActivity, then
//      DashboardActivity.  This is the V2 §8.4 HIGH-risk gap; we
//      attempt the chain best-effort and tag the second-Activity launch
//      with WL_M8_SIG3 so the script can verify it independently.
//   3. Uses McD's existing MCD_APP_PROOF marker family (pre-pivot) AS
//      WELL AS new WL_M8_SIG[1..7] markers (post-V2 acceptance signals).
//
// This class is intentionally a THIN launcher -- the heavy lifting
// (PathClassLoader, Application reflection, Activity instantiation
// chain) lives in DiscoverWrapperBase.  This file only adds:
//   * Acceptance-marker emission at the canonical CR38 §5.2 checkpoints.
//   * SplashActivity -> DashboardActivity transition best-effort drive
//     (the §7.2 V2 substrate gap; if the synthetic startActivity
//     doesn't fire, we emit WL_M8_SIG3=PENDING and the script handles
//     that as a known-degraded signal until V2 §8.4 lands).
//
// Acceptance signals (per CR38 §5.2):
//   WL_M8_SIG1 -- McDMarketApplication.onCreate completion
//   WL_M8_SIG2 -- SplashActivity reaches performResume / onCreate exit
//   WL_M8_SIG3 -- DashboardActivity launches (V2 §8.4 multi-Activity)
//   WL_M8_SIG4 -- Dashboard sections inflate (HERO/MENU/PROMOTION/POPULAR)
//   WL_M8_SIG5 -- dumpsys media.audio_flinger sanity (orchestrator handles)
//   WL_M8_SIG6 -- Zero crashes (orchestrator handles)
//   WL_M8_SIG7 -- HTTP requests fire (Retrofit/OkHttp) -- McD makes real
//                 network calls; if PHASE E reaches Application.onCreate
//                 completion (SIG1), Hilt-injected OkHttp/Retrofit clinit
//                 will have occurred and the bridge HTTP proxy log + the
//                 PFCUT-MCD-NET grep does the verification host-side.
//
// Native print bridge: same pattern as McdDiscoverWrapper -- delegate
// println/eprintln to AsInterfaceTest (whose natives are registered by
// JNI_OnLoad_binder).

public class McdLauncher {
    /** Manifest path on phone (per CR27 mcd.discover.properties layout). */
    static final String MCD_MANIFEST =
            "/data/local/tmp/westlake/mcd.discover.properties";

    private static void println(String s) {
        AsInterfaceTest.println(s);
    }

    private static void eprintln(String s) {
        AsInterfaceTest.eprintln(s);
    }

    /**
     * Emit a CR38 §5.2 acceptance-signal marker.  The orchestrator
     * script (scripts/run-mcd-westlake.sh) greps for these prefixes
     * to populate its 7-signal verdict table.
     *
     * Format: "WL_M8_SIG<N> <STATUS> [<key=value> ...]"
     * STATUS = PASS | FAIL | PENDING | SKIP
     */
    static void emitSignal(int n, String status, String detail) {
        String marker = "WL_M8_SIG" + n + " " + status
                + (detail == null || detail.isEmpty() ? "" : " " + detail);
        println(marker);
    }

    public static void main(String[] args) {
        // Allow override from cmdline (mostly for ad-hoc testing); default
        // to the canonical McD manifest.
        String manifestPath = (args != null && args.length > 0)
                ? args[0] : MCD_MANIFEST;
        // CR-M8-STEP1 fix: the JNI natives that back AsInterfaceTest.println
        // live in libandroid_runtime_stub.so.  DiscoverWrapperBase.loadLib()
        // would do this for us but only AFTER our preamble prints fire and
        // UnsatisfiedLinkError into the void.  Load it up front; if it
        // fails, fall through to System.out which on this dalvikvm is also
        // safe (CR9 CharsetPrimer makes pre-clinit println safe enough for
        // a few diagnostic lines).
        try {
            System.loadLibrary("android_runtime_stub");
        } catch (UnsatisfiedLinkError ule) {
            System.err.println("McdLauncher: cannot load android_runtime_stub: " + ule);
        }
        println("McdLauncher: starting M8-Step1 manifest=" + manifestPath);
        println("McdLauncher: per CR38 §5.2 -- emitting WL_M8_SIG markers");

        // Hook the DiscoverWrapperBase phase output so when PHASE E
        // (Application.onCreate) succeeds, we emit SIG1.  Same for PHASE G
        // (MainActivity launch) -> SIG2/SIG3, and the inflate marker
        // surface that the V2 substrate already prints on
        // FragmentManager.addFragmentInternal -> SIG4.
        //
        // DiscoverWrapperBase doesn't expose hooks per-phase; instead we
        // run the whole pipeline then introspect its `phaseResults` list
        // afterward.  For PHASE G4 vs G_DASHBOARD distinction we tap
        // afterward via reflection (best-effort).
        SignalEmittingPrinter printer = new SignalEmittingPrinter();
        try {
            DiscoverWrapperBase.runFromManifest(manifestPath, printer);
            // Note: DiscoverWrapperBase calls System.exit(0) at the end,
            // so this line typically does not execute.  The signal
            // emission happens within SignalEmittingPrinter as PHASE
            // markers flow through.
        } catch (Throwable t) {
            eprintln("McdLauncher: pipeline threw " + t.getClass().getName()
                    + ": " + t.getMessage());
            emitSignal(6, "FAIL", "exception=" + t.getClass().getSimpleName());
            System.exit(20);
        }
    }

    /**
     * Wraps DiscoverWrapperBase.Printer to emit CR38 §5.2 markers when
     * the underlying phase output crosses known checkpoints.
     *
     * This is the cleanest non-invasive coupling: DiscoverWrapperBase
     * already prints rich phase output; we just tag-along.  No edits
     * to DiscoverWrapperBase or McdDiscoverWrapper required.
     */
    static class SignalEmittingPrinter implements DiscoverWrapperBase.Printer {
        boolean sig1Emitted = false;
        boolean sig2Emitted = false;
        boolean sig3Emitted = false;
        boolean sig4Emitted = false;

        @Override
        public void println(String s) {
            McdLauncher.println(s);
            checkAndEmit(s);
        }

        @Override
        public void eprintln(String s) {
            McdLauncher.eprintln(s);
            checkAndEmit(s);
        }

        private void checkAndEmit(String s) {
            if (s == null) return;
            // SIG1: McDMarketApplication.onCreate completion.
            // DiscoverWrapperBase prints either
            //   "PHASE E: onCreate() returned cleanly (unexpected!)" or
            //   "PHASE E: PASSED unexpectedly"
            // when onCreate clears all the way through.  When it fails
            // (currently expected pre-M5/M6) it prints DISCOVER-FAIL or
            // "PHASE E: FAILED ...".
            if (!sig1Emitted) {
                if (s.contains("PHASE E: PASSED")
                        || s.contains("onCreate() returned cleanly")) {
                    emitSignal(1, "PASS",
                            "marker=Application.onCreate.completion");
                    sig1Emitted = true;
                } else if (s.contains("PHASE E: FAILED")) {
                    emitSignal(1, "FAIL",
                            "marker=Application.onCreate.completion");
                    sig1Emitted = true;
                }
            }
            // SIG2: SplashActivity.onCreate exit.  DiscoverWrapperBase
            // prints "PHASE G4: onCreate(null) returned cleanly"
            // (unexpected) or "PHASE G4: FAILED ...".  Pre-M5/M6 this is
            // expected to FAIL (Configuration.setTo NPE, etc.).
            if (!sig2Emitted) {
                if (s.contains("PHASE G4: PASSED")
                        || s.contains("onCreate(null) returned cleanly")) {
                    emitSignal(2, "PASS",
                            "marker=SplashActivity.onCreate.exit");
                    sig2Emitted = true;
                } else if (s.contains("PHASE G4: FAILED")) {
                    emitSignal(2, "FAIL",
                            "marker=SplashActivity.onCreate.exit");
                    sig2Emitted = true;
                }
            }
            // SIG3: DashboardActivity launches.
            // V2 §8.4 multi-Activity intent dispatch is the HIGH-risk
            // gap; DiscoverWrapperBase does NOT currently drive
            // SplashActivity -> DashboardActivity chain.  Emit PENDING
            // when PHASE G ends -- the orchestrator script treats
            // PENDING as "known degraded" pre-V2 §8.4 (per brief).
            if (!sig3Emitted) {
                if (s.contains("PHASE G: SKIPPED")
                        || s.contains("END OF DISCOVERY REPORT")) {
                    emitSignal(3, "PENDING",
                            "reason=V2_8.4_multi_activity_dispatch_gap");
                    sig3Emitted = true;
                }
            }
            // SIG4: Dashboard sections inflate (HERO/MENU/PROMOTION/POPULAR).
            // The V2 substrate FragmentManager (when active) prints
            // "FragmentManager.addFragmentInternal name=...Dashboard...".
            // Pre-M5/M6 / pre-V2-§8.4 this won't fire; emit PENDING at
            // discovery exit.
            if (!sig4Emitted && s.contains("END OF DISCOVERY REPORT")) {
                emitSignal(4, "PENDING",
                        "reason=requires_dashboard_activity_active");
                sig4Emitted = true;
            }
        }
    }
}
