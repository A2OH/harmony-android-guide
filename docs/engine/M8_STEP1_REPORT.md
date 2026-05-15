# M8_STEP1_REPORT — McD end-to-end integration script (degraded-mode tolerant)

**Status:** done (mechanical orchestration PASS 2026-05-13 on cfb7c9e3; acceptance signals as expected pre-M5/M6 + pre-V2-§8.4)
**Owner:** Builder (M8-Step1 CR)
**Companion to:** [`CR38_M7_M8_INTEGRATION_SCOPING.md`](CR38_M7_M8_INTEGRATION_SCOPING.md), [`M6_STEP1_REPORT.md`](M6_STEP1_REPORT.md), [`PHASE_1_STATUS.md`](PHASE_1_STATUS.md)
**Predecessor:** CR38 (M7/M8 pre-scoping), CR27 (manifest-driven discovery harness)
**Sibling:** M7-Step1 (`scripts/run-noice-westlake.sh` + `aosp-libbinder-port/test/NoiceLauncher.java`) — being built in parallel; shared skeleton, app-specific config diff only.

---

## §1  Goal

CR38 §5.2 specified McDonald's end-to-end fixture: 7 acceptance signals,
10-minute interactive session, dashboard sections inflate, HTTP traffic.
M8-Step1 is the **first** of two steps for that milestone:

1. **M8-Step1 (this CR)** — build the orchestrator script + the dalvikvm-
   side launcher class. Verify the mechanical pipeline (artifacts present,
   SM starts, dalvikvm launches with McD bytecode, 7 signals are graded).
   Expect most signals to FAIL/PENDING because M5/M6 daemons are still
   in flight and V2 §8.4 (multi-Activity intent dispatch) hasn't landed.
2. **M8-Step2 (future)** — once M5+M6 daemons + V2 §8.4 land, the same
   script becomes the **production** acceptance gate. Most signals
   flip to PASS without script changes (because each signal grep is
   pattern-based, not state-based — once the daemons emit the expected
   markers, the verdict flips automatically).

This split lets M8 land its test infrastructure NOW (so future M5/M6
landings have a regression check pre-wired) without waiting for the
dependencies.

---

## §2  What was built

### 2.1 New files

| File | LOC | Description |
|------|-----|-------------|
| `scripts/run-mcd-westlake.sh` | 491 | Orchestrator: CR38 §2 12-step boot + §5.2 7-signal verdict + chain to `check-real-mcd-proof.sh` |
| `aosp-libbinder-port/test/McdLauncher.java` | 195 | Dalvikvm-side test fixture: drives `DiscoverWrapperBase.runFromManifest(mcd.discover.properties)` and emits `WL_M8_SIG[1..7]` markers at the canonical CR38 §5.2 checkpoints |
| `aosp-libbinder-port/build_mcd_launcher.sh` | 122 | `javac + dx` pipeline (near-verbatim of `build_mcd_discover_wrapper.sh`) → `out/McdLauncher.dex` (42 KB) |
| `docs/engine/M8_STEP1_REPORT.md` | this file | Report |

Total net-new: **~808 LOC** + 1 NEW dex (42 KB).

### 2.2 Files modified (outside the M8-Step1 scope)

| File | Change |
|------|--------|
| `docs/engine/PHASE_1_STATUS.md` | one row added: M8-Step1 done |

### 2.3 Files NOT touched (per the anti-drift contract)

- `scripts/run-noice-westlake.sh` — M7-Step1 active there (parallel CR)
- `aosp-libbinder-port/test/NoiceLauncher.java` — M7-Step1
- `shim/java/**` — CR43+CR44 active; V2 substrate work
- `daemons (M5/M6)` — M5/M6 active
- `art-latest/**` — stable
- `aosp-libbinder-port/aosp-src/**` — stable
- `aosp-shim.dex` — stable
- `memory/*` — none

Zero per-app branches introduced (the McD-specific bits — APK path,
manifest, package name — are config, not code branches; mirrors how
`mcd-discover.sh` differs from `noice-discover.sh`).

---

## §3  Symmetry with M7-Step1 (sibling)

M7-Step1 (`scripts/run-noice-westlake.sh`) is being built in parallel.
The two scripts share the same orchestration skeleton; differences are
purely app-specific config + the 7-signal acceptance set (§5.1 vs §5.2
of CR38). The shared structure:

```
preflight()                         identical (modulo dex name)
start_http_proxy_if_needed()        identical (proxy is dev infrastructure;
                                    McD uses it heavily, noice doesn't —
                                    starting it for noice is harmless)
start_servicemanager()              identical
start_audio_daemon()                identical (degraded if absent)
start_surface_daemon()              identical (degraded if absent)
run_dalvikvm()                      app-specific: -Dwestlake.apk.* +
                                    McdLauncher (vs NoiceLauncher)
capture_artifacts()                 identical
evaluate_signals()                  app-specific 7-signal table
chain_proof_check()                 chain into check-real-mcd-proof.sh
                                    (M7 chains into a noice analog if any;
                                    none exists today — M7 may skip this)
stop_all()                          identical
```

Approximate diff between M7 and M8 scripts: ~30 LOC out of ~500.
**This is the intended design** — per CR38 §5.3 "Shared infrastructure
(M7 + M8): both tests share boot scripts (each ~150-200 LOC,
parameterized identically)". The current size (491 LOC) is larger than
CR38's estimate because the script also includes the 7-signal grading
machinery + the proof analyzer chain + cleanup traps; M7 will likely
have a comparable structure with noice-specific signal patterns.

---

## §4  Acceptance signal contract (CR38 §5.2 + M8-Step1 mechanical run)

| # | Signal (CR38 §5.2) | Required? | Current verdict (M5/M6/V2-§8.4 not landed) | Source |
|---|--------------------|-----------|--------------------------------------------|--------|
| SIG1 | McDMarketApplication.onCreate completion | YES | FAIL (expected pre-M5/M6; PHASE E aborts on getSystemService("audio") / missing Tier-2) | `dalvikvm.log` grep "PHASE E: PASSED" or "onCreate() returned cleanly" |
| SIG2 | SplashActivity.onCreate exit | YES | FAIL (expected pre-V2-§8.4) | `dalvikvm.log` grep "PHASE G4: PASSED" |
| SIG3 | DashboardActivity launches | NO (soft) | PENDING (V2 §8.4 multi-Activity intent dispatch gap, per brief) | grep "DashboardActivity" |
| SIG4 | Dashboard sections inflate (HERO/MENU/PROMOTION/POPULAR) | NO | PENDING | grep "MCD_DASH_SECTIONS_READY" |
| SIG5 | dumpsys media.audio_flinger sanity | NO | PASS (system AF reachable) | `dumpsys-audio.txt` non-empty + AudioFlinger string |
| SIG6 | Zero crashes in dalvikvm | YES | PASS (after CR-M8-STEP1 fix: scope to dalvikvm.log only — the teardown SIGABRT in system logcat is our own SM cleanup, not a test failure) | `dalvikvm.log` grep "Fatal signal|SIGBUS|SIGSEGV|JNI DETECTED ERROR|FATAL EXCEPTION" |
| SIG7 | HTTP requests fire (Retrofit/OkHttp/PFCUT-MCD-NET) | NO | PENDING (pipeline halts pre-network, expected) | grep "okhttp3|retrofit2|OkHttpClient|PFCUT-MCD-NET|WestlakeHttp" |

**Mechanical pipeline verdict (M8-Step1 acceptance):** the script runs
end-to-end, captures artifacts, grades 7 signals, exits with a useful
code. **PASS.**

**Logical pipeline verdict (M8 acceptance, future):** SIG1 + SIG2
flip PASS once M5+M6 land + V2 §7.x service stubs land. SIG3 + SIG4
flip PASS once V2 §8.4 multi-Activity intent dispatch lands. SIG6
already PASS. SIG7 follows SIG1 (network init is downstream of
Application.onCreate in McD).

---

## §5  V2 §8.4 multi-Activity risk (brief: "KNOWN HIGH risk")

CR38 §7.2 + §8 risk #8 flagged McD's SplashActivity → DashboardActivity
transition as the V2 substrate's biggest known gap. M8-Step1 codifies
this risk by:

1. Emitting `WL_M8_SIG3 PENDING reason=V2_8.4_multi_activity_dispatch_gap`
   when DiscoverWrapperBase prints "END OF DISCOVERY REPORT" without
   ever having reached a second Activity. The orchestrator script
   handles PENDING as soft-pass (degraded-mode tolerant per brief).
2. Documenting in the SignalEmittingPrinter Javadoc that SIG3 is the
   V2 §8.4 wait gate.
3. Leaving the SIG3/4 grep patterns generic enough that they will
   auto-flip to PASS once the V2 §8.4 work lands and DashboardActivity
   actually starts emitting log markers.

**M8-Step1 explicitly does NOT attempt to work around V2 §8.4 by
synthesizing a fake DashboardActivity launch.** That would be drift
(per `feedback_no_per_app_hacks.md` — per-app workarounds are out).
V2 §8.4 lands as a generic V2 substrate fix; until then, SIG3+SIG4
correctly report PENDING.

---

## §6  Mechanical test orchestration verification

Per the brief: "Don't expect full PASS — M5/M6 still in flight + V2
§8.4 multi-Activity is a known TODO. Verify: daemons start, dalvikvm
process launches with McD bytecode, script produces artifact directory,
7 checks run (most may FAIL initially)."

All 4 verification bullets PASS:

### 6.1 Daemons start

```
[run-mcd-westlake] Step 3: start our M2 servicemanager on /dev/vndbinder as uid=1000
[run-mcd-westlake]   SM up
[run-mcd-westlake] Step 4: try to start M5 audio_daemon
[run-mcd-westlake]   SKIP: bin-bionic/audio_daemon not present on device (M5 daemon not built yet)
[run-mcd-westlake] Step 5: try to start M6 surface_daemon
[run-mcd-westlake]   WARN: surface_daemon binary present but failed to launch
```

- M2 servicemanager: PASS (process up, log written).
- M5 audio_daemon: correctly SKIP (binary absent on phone — M5 not built).
- M6 surface_daemon: WARN (binary present from M6-Step1 but doesn't sit in joinThreadPool against our fresh SM; this is expected — M6-Step1 was a smoke test, not a long-running daemon; M6-Step2-6 will fix this).

### 6.2 Dalvikvm launches with McD bytecode

```
[run-mcd-westlake] Step 8: launch dalvikvm running McdLauncher (manifest-driven McD pipeline)
[run-mcd-westlake]   dalvikvm exit code=0 elapsed=14s
```

PASS. The dalvikvm process classloaded all 7 candidate classes from
`com_mcdonalds_app.apk` (PHASE B), instantiated `McDMarketApplication`
(PHASE C), invoked `attachBaseContext + setAttachedApplication` (PHASE D),
then attempted `Application.onCreate` (PHASE E). It halted in PHASE E
on a missing-Tier-2 service — the expected fail-loud surface per CR2.

### 6.3 Script produces artifact directory

```
$ ls artifacts/mcd-westlake/20260513_174337_m8step1_dryrun5/
audio_daemon.log              http-proxy.err   processes.txt   signals.txt
check-real-mcd-proof.out      http-proxy.out   screen.png      surface_daemon.log
dalvikvm.log                  logcat.txt       servicemanager.log
dumpsys-audio.txt
```

PASS — 12 artifact files, including the canonical `signals.txt`
table and `dalvikvm.log` (the McdLauncher transcript with all
`WL_M8_SIG*` markers).

### 6.4 7 checks run (most FAIL initially)

```
====================================================
CR38 §5.2 acceptance signals (M8-Step1)
====================================================
  FAIL SIG1 -- McDMarketApplication.onCreate completion (REQUIRED)
  FAIL SIG2 -- SplashActivity.onCreate exit (REQUIRED)
  PENDING SIG3 -- DashboardActivity launches (V2 §8.4 gap; PENDING expected)
  PENDING SIG4 -- Dashboard sections inflate (HERO/MENU/PROMOTION/POPULAR)
  PASS SIG5 -- dumpsys media.audio_flinger sanity (system AF reachable)
  PASS SIG6 -- zero crashes in dalvikvm path
  PENDING SIG7 -- HTTP stack absent (pipeline halted pre-network)

Summary: 2 PASS  2 FAIL  3 PENDING/SKIP
M8-Step1: FAIL (2 required signals failed)
```

PASS — all 7 signals graded with PASS / FAIL / PENDING as appropriate
to current state. The verdict matches CR38 §5.2 expectations exactly.

---

## §7  Architectural decisions taken in M8-Step1

### 7.1 Why a NEW Launcher class instead of reusing McdDiscoverWrapper

`McdDiscoverWrapper` is the manifest-driven discovery harness from
CR27 — its job is to print rich phase output for human inspection
during M4 / M4-PRE iteration. M8-Step1 needs a different consumer:
machine-readable acceptance markers (`WL_M8_SIG[1..7]`) that the
orchestrator script can grep.

Two options were considered:

**Option A: extend DiscoverWrapperBase with a Marker emitter.**
Rejected — adds coupling between the discovery harness (still useful
for ad-hoc iteration) and the acceptance fixture (CI-style).

**Option B: NEW thin Launcher class that wraps DiscoverWrapperBase
and tag-along-emits markers via the existing Printer interface.**
Chosen. McdLauncher (~195 LOC) is a `SignalEmittingPrinter` that
forwards println/eprintln to AsInterfaceTest's natives **and** scans
each line for the canonical PHASE-result strings, emitting a
`WL_M8_SIG*` marker at the appropriate checkpoint.

This means **zero edits** to DiscoverWrapperBase or McdDiscoverWrapper.
The discovery harness keeps its job; M8 has its own production-style
fixture.

### 7.2 Why not run McD via WestlakeLauncher (the production launcher)?

`shim/java/com/westlake/engine/WestlakeLauncher.java` (~5,500 LOC
post-CR16) is the production launcher: it sets up the Compose host
APK's WestlakeRenderer, the DLST pipe, the OHBridge, the
real-framework BCP, etc. M8 will eventually use it (M8-Step2+).

For M8-Step1 the right call is to NOT pull in WestlakeLauncher because:

- WestlakeLauncher's eager `OHBridge.isNativeAvailable()` clinit
  needs `libframework_stubs.so` (or the real Bionic libandroid_runtime),
  which our standalone dalvikvm doesn't ship — that's M3++ work that
  partially exists for noice-discover but isn't ready for production
  full-app launching.
- WestlakeLauncher's main path depends on real binder traffic
  reaching M5/M6 daemons (not yet built) for any frame to render.

DiscoverWrapperBase reaches PHASE G4 cleanly today (per cr36-mcd-after.log
log) — and that's exactly the "Application + onCreate + first
MainActivity attach + onCreate(null)" path that comprises SIG1, SIG2,
and most of the M8-Step1 acceptance signal surface. **Step 2+ will
flip the launcher to WestlakeLauncher** once M5/M6 land; the
orchestrator script's only change at that point is the `-cp` /
`-Dwestlake.apk.*` argument set.

### 7.3 Why scope SIG6 to dalvikvm.log only?

Initial implementation grep'd both `dalvikvm.log` and `logcat.txt`.
That caught a SIGABRT in the system logcat — which turned out to be
our own servicemanager being killed cleanly during `stop_all` teardown.
Real test-failure crashes (SIGBUS in dalvikvm path) surface in
`dalvikvm.log` directly. Scoping SIG6 to `dalvikvm.log` removes the
false positive without losing real detection. (M7-Step1 should do the
same; noice's path is identical.)

### 7.4 Why chain check-real-mcd-proof.sh?

Per the brief: "Could reuse the existing `scripts/check-real-mcd-proof.sh`
artifact analyzer (parses logcat for marker strings) — chain it at end."

The pre-pivot McD test infrastructure has 800+ LOC of mature
acceptance grading (HOMEDASHBOARD markers, MCD_DASH_SECTIONS_READY,
strict-frame-bytes thresholds, the cart-product hydration cascade,
etc.). Once M5/M6 + V2 §8.4 land and the McD app reaches dashboard,
chaining check-real-mcd-proof.sh gives M8 the entire pre-pivot
acceptance surface for free.

For M8-Step1 (degraded), check-real-mcd-proof.sh logs its findings
into `check-real-mcd-proof.out` but doesn't fail the script — its
exit code is ignored. Once M8 reaches PASS, M8-Step2 may upgrade this
to a hard gate.

---

## §8  Next-step blockers

### 8.1 M8-Step2 (full PASS) is blocked on

1. **M5 audio_daemon** — Step2+ pending; needs the bionic `audio_daemon`
   binary to be built and pushed to `bin-bionic/audio_daemon`. SIG1
   will likely flip to PASS once M5 registers `media.audio_flinger`
   because McD's Application.onCreate Hilt cascade indirectly reaches
   AudioManager. CR38 §3.3 walked the chain.
2. **M6 surface_daemon** — Step2-6 pending (Step1 done). Needs the
   real ISurfaceComposer transaction dispatch + DLST pipe writer in
   the daemon. SIG2 will likely flip to PASS once SplashActivity can
   call setContentView → ViewRootImpl.relayoutWindow → M6.
3. **V2 §8.4 multi-Activity intent dispatch** — currently the
   `WestlakeActivity.startActivity(Intent)` path doesn't tear down the
   current Activity + spin up a new one. SIG3 + SIG4 are gated here.
4. **V2 §7.x Tier-2 service stubs** (audio Java, connectivity, vibrator)
   — CR40's recommended CR41+CR42+CR43 batch. Some of these may
   surface as McDMarketApplication.onCreate gaps before M5 lands;
   they're cheap (~30-100 LOC across 3-5 services).

### 8.2 M8-Step1 itself is unblocked

The mechanical pipeline runs end-to-end today. No blockers for THIS
step. The pre-pivot artifact analyzer is wired. The dex builds. The
SM starts. McD bytecode classloads.

---

## §9  Person-time spent

- Read M7/M8 architecture (CR38 + noice/mcd-discover patterns): ~25 min
- Read pre-pivot McD scripts (run-real-mcd-phone-gate + check-real-mcd-proof): ~20 min
- Author McdLauncher.java + build_mcd_launcher.sh: ~40 min
- Author run-mcd-westlake.sh (~500 LOC): ~50 min
- Mechanical test + 2 iterations (println-before-loadLibrary fix; SIG6 scope refinement): ~20 min
- This report: ~20 min

**Total: ~2h 55 min** (inside the 2-3h budget the brief allotted).

---

## §10  Pointers for the M8-Step2 Builder

If you are picking up M8-Step2 (full PASS), read in this order:

1. This report §8.1 (what's blocked, what's not).
2. CR38 §5.2 (the 7 acceptance signals).
3. `scripts/run-mcd-westlake.sh` `evaluate_signals()` function — that's
   the verdict table. Each signal is pattern-driven; the most likely
   change between M8-Step1 and M8-Step2 is **none** to this code
   (the patterns auto-flip to PASS once M5/M6/V2-§8.4 land).
4. `aosp-libbinder-port/test/McdLauncher.java` `SignalEmittingPrinter`
   — if a new acceptance checkpoint surfaces (e.g., framework prints a
   marker we want to scope on), this is where you tag-along-emit a
   new `WL_M8_SIG*` marker.
5. `scripts/check-real-mcd-proof.sh` — the pre-pivot analyzer; consider
   tightening its hard-gate criteria once M8 reaches dashboard.

Anti-drift compliance for M8-Step2:
- DO NOT add McD-specific branches in WestlakeLauncher.
- DO NOT plant fields on synthetic Activity/Configuration objects.
- Fail-loud surfaces are **discovery signals**, not bugs (per CR2).
- Each new Tier-2 method that surfaces during M8-Step2 gets implemented
  in the responsible M4/M5/M6 service, generically. No per-app paths.
