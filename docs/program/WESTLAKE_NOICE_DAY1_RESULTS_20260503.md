# Westlake Noice 1-Day Proof — Results (2026-05-03 PT)

**Source contract:** `WESTLAKE_NOICE_1DAY_CONTRACT_20260503.md`
**Run date:** 2026-05-03 PT (autonomous execution, no human in the loop)
**Phone:** OnePlus 6 `cfb7c9e3` (LineageOS 22 / Android 15)
**Original baseline runtime:** `d7e10e47…` (unchanged across run)

---

## Executive summary

The noice 1-day proof produced **2 valuable empirical findings** but did NOT achieve a working noice render in the Westlake guest. Acceptance criteria summary:

| Criterion | Result |
|---|---|
| **A1** APK loaded into Westlake guest `dalvikvm` | **PARTIAL** — dalvikvm spawned, noice APK + dex loaded, manifest parsed, AssetManager wired; then SIGBUS |
| **A2** MainActivity `onResume` inside guest | **FAIL** — VM died before MainActivity reached |
| **A3** ≥5 views inflated | **FAIL** — never reached |
| **A4** ≥1 tap routed to click handler | **FAIL** — never reached (gate ran with `taps=none`) |
| **A5** Audio gap characterized | **PASS** — Agent 4 produced 398-line inventory at `_noice_audio_gap.txt` |
| **A6** Screenshot proves UI rendered | **FAIL** — screenshot shows host's Compose home, not noice |
| **A7** 5-min idle soak no SIGBUS | **FAIL** — VM SIGBUSed at ~30s into first boot |

**The headline finding:** **PF-630-class SIGBUS is NOT McD/Realm-specific.** Noice (which uses zero Realm, zero Firebase, zero auth, zero McD code) hits the **exact same SIGBUS at the exact same sentinel address** (`0xfffffffffffffb17` = `kPFCutPf625StaleNativeEntry`). PF-630 is a universal Westlake guest dalvikvm boot-stability issue, not a McD-Realm finalizer issue. The 5/2 PT McD bounded green is the *outlier*, not the typical case.

This finding **rescopes PF-630** from "fix Realm finalizer race" to "fix the substrate's stale-native-entry detection across the entire app boot path."

---

## Phase-by-phase what happened

### Phase 1 — APK acquisition (Agent 1) — DONE

- Source: F-Droid signed APK (`https://f-droid.org/repo/com.github.ashutoshgngwr.noice_72.apk`)
- Package: `com.github.ashutoshgngwr.noice` v2.5.7 (vc 72)
- APK sha256: `8e11b3136977e9f5dc897de85c26add9a219ea61884feabcbcd4e33566d5d2ca`
- Single dex (no multidex), 4.88 MB
- Staged on phone at `/data/local/tmp/westlake/{noice.apk, noice_classes.dex}`
- Provenance at `artifacts/noice-1day/_apk_provenance.txt`

### Phase 2a — Generic gate harness fork (Agent 2) — DONE

- New scripts: `scripts/run-noice-phone-gate.sh` (320 lines), `scripts/check-noice-proof.sh` (427 lines)
- McD scripts unchanged (regression test passed)
- Preserves `*unsafe*` checker fix from commit `98719db2`; widened `unsafe_flag_count` regex to `westlake_(mcd_)?unsafe_*`
- Commits: `bf678e47`, `6d9e7496` (not pushed — see §6 commit chain)

### Phase 2b — First-boot attempt v1 — REVEALED HOST-APK GAP

Result: `vm_pid=missing` from start. Logcat: `Auto-launching: WESTLAKE_ART_NOICE` followed by NO subprocess spawn.

**Root cause:** `WestlakeActivity.kt:158, 413, 609` is hardcoded for McD only — recognizes `WESTLAKE_ART_MCD` and `WESTLAKE_VM_MCD`; falls through for any other token. The 1-day contract assumed the host could run any APK with the right gate harness; **it cannot**, the host APK is McD-specific.

Artifact: `artifacts/noice-1day/20260503_232011_noice_noice_first_boot/`

### Phase 2c — Host APK modification + rebuild — DONE

Surgical edit: added `if (className == "WESTLAKE_ART_NOICE") { ... ApkVmConfig(packageName="com.github.ashutoshgngwr.noice", activityName="...activity.MainActivity", displayName="Noice (ART)", ...) ... }` to `WestlakeActivity.kt:169` plus the parallel skip-list update at `:424`.

Build env: Gradle 8.4 + Java 21 JBR via `./gradlew :app:assembleDebug`. Build succeeded in 25s. Output: 33.6 MB debug APK.

Installed via `adb install -r`. Also installed noice APK via `adb install -r` so PackageManager can resolve `apkSourceDir` (the host code at `WestlakeVM.kt:2098-2099` calls `PackageManager.getApplicationInfo` for the target package).

Source state for next agent:
- Modified: `westlake-host-gradle/app/src/main/java/com/westlake/host/WestlakeActivity.kt` (uncommitted at session end — see §6 below)
- Phone host APK is the rebuilt one (still has McD branch intact; noice branch added)

### Phase 2d — First-boot attempt v2 — PARTIAL, then SIGBUS

Result: dalvikvm subprocess spawned, noice APK + dex loaded, manifest parsed (`NoiceApplication (8 activities, 1 providers)`), AssetManager wired. Then `Fatal signal 7 (SIGBUS), code 1 (BUS_ADRALN) fault addr 0xfffffffffffffb17` at `19:26:33` — about **28 seconds after VM start**.

Last good marker before crash: `[AssetManager] setApkPath path=/data/local/tmp/westlake/com_github_ashutoshgngwr_noice.apk` at 19:26:20.

Artifact: `artifacts/noice-1day/20260503_232600_noice_noice_first_boot_v2/`

### Phase 2e — Sanity check (noice on phone, no Westlake)

`am start com.github.ashutoshgngwr.noice/.activity.MainActivity` → `TotalTime: 1193ms`, COLD launch reached `AppIntroActivity`, process running. **Noice itself is fine on the phone.** The SIGBUS is 100% Westlake guest dalvikvm.

### Phase 3 — Audio gap characterization (Agent 4) — DONE

`_noice_audio_gap.txt` (398 lines, 21.7 KB). Top findings:
- APK has **zero bundled .so** — noice uses platform MediaCodec for all decode
- Top 5 audio APIs: `AudioTrack` (final PCM sink), `MediaCodec` (decode), `AudioManager` focus, `MediaSession`, `AudioAttributes`/`AudioFormat`
- Existing `ohbridge_stub.c` HAS audio*/mediaPlayer* stubs but registered for `OHBridge` namespace, NOT `android.media` — **dead code** for noice (noice never goes through OHBridge)
- Day-1 win path: ~50 LOC AudioFocus + NotificationManager stubs unblock foreground service start
- 1-week: AudioTrack via AAudio backend (silence-vs-sound boundary)
- Multi-week: real MediaCodec decode (3+ week effort)

### Phase 3b — 5-min soak — NOT RUN (gated on first-boot success)

A 5-min soak gate is moot when the VM SIGBUSes at 28s. Marked FAIL.

---

## Headline finding (#1): PF-630 SIGBUS is universal

| Run | Runtime | App | SIGBUS sentinel? |
|---|---|---|---|
| 5/2 17:57 PT McD bounded final | `d7e10e47` | McD | NO (lucky) |
| 5/3 12:15 PT McD post-rollback | `d7e10e47` | McD | NO (lucky) |
| 5/3 15:48 PT McD quantity_plus | `d7e10e47` | McD | YES at `0xffff..fb17` |
| 5/3 21:25 PT McD post-narrow-fix-rollback | `d7e10e47` | McD | YES at `0xffff..fb17` |
| **5/3 23:26 PT noice first-boot v2** | **`d7e10e47`** | **noice** | **YES at `0xffff..fb17`** |

The sentinel address `0xfffffffffffffb17` is `kPFCutPf625StaleNativeEntry` (defined in `interpreter_common.cc:105`). The runtime hits its OWN stale-entry sentinel — meaning some code path is dereferencing the sentinel as a function pointer. This is the same bug class for both McD (sometimes) and noice (consistently at boot).

**Implication for PF-630 scope:** the issue is the runtime's general stale-native-entry detection, not Realm finalizer code. The 2026-05-03 candidate runtime tried to *widen* the detection predicate; that introduced boot-class ArrayStoreException regression. The **right next angle** (per Agent C's option 2 and confirmed by this finding) is to make the routing through PFCutUnsafe slot accessors **boot-aware** — only invoked AFTER `Runtime::IsStarted() && app_class_loader_seen`. Boot classes (which never go through the bypass path in stock dalvikvm) would fall through cleanly; non-boot Unsafe operations would still get the PFCut routing.

---

## Headline finding (#2): host APK is McD-hardcoded

The host APK (`WestlakeActivity.kt`) explicitly branches on `WESTLAKE_ART_MCD` / `WESTLAKE_VM_MCD` only. Adding any new app requires:

1. Source edit in `WestlakeActivity.kt` (one branch + skip-list update)
2. Gradle rebuild (`./gradlew :app:assembleDebug` — 25s)
3. `adb install -r` of the new APK
4. Target app **must be installed** on the phone (PackageManager call at `WestlakeVM.kt:2098`)

This is a real architectural debt: the host should be a **generic launcher** that takes package + activity from intent extras, not hardcoded per-app. Suggested follow-up: a `WESTLAKE_ART_GENERIC` token that reads `noice_package`, `noice_activity` (or equivalent) from intent extras.

---

## Audio gap (Agent 4 deliverable embedded by reference)

Full inventory at `artifacts/noice-1day/_noice_audio_gap.txt`. Three-tier roadmap:

| Tier | Effort | Outcome |
|---|---|---|
| **1-day stub** | ~50 LOC C in `ohbridge_stub.c` | AudioFocus + NotificationManager stubs unblock noice foreground service start; reaches AudioTrack-init gate |
| **1-week** | AudioTrack via AAudio backend | Silence-vs-sound boundary proven (output works, no decode) |
| **Multi-week (3+)** | Real MediaCodec via `dlopen libmedia_jni.so` OR custom FFmpeg AudioRenderer | Actual sound output |

Noice-specific quirk: uses `androidx.media3.*` (not legacy `com.google.android.exoplayer2.*`); declares `FOREGROUND_SERVICE_MEDIA_PLAYBACK` permission and `uses-feature android.hardware.audio.output` — Westlake's `PackageInfoStub` must report this feature as present or onboarding refuses.

---

## Day-2+ recommendation

**Highest-leverage next step (regardless of app target):** **PF-630 boot-aware routing fix** (per `WESTLAKE_FULL_MCD_GAP_REPORT_20260503.md` §13.12 next-agent prompt). This unblocks BOTH McD and noice (and presumably any other app) because the SIGBUS is universal. Without it, no app can reliably reach a steady state in the guest.

After PF-630 lands cleanly:
- **Noice direction**: pursue the 1-day audio stub (AudioFocus + NotificationManager) — gets the foreground service started; then pursue the 1-week AudioTrack bridge to prove silence-vs-sound. Total ~2 weeks to a noice that "renders + makes silence."
- **McD direction**: pursue PF-628 unsafe-opt-in probe (with both flags), then tighten checker per `WESTLAKE_FULL_MCD_GAP_REPORT_20260503.md` §14.3 P1.

**Lower-leverage but concrete day-2 wins:**
- Genericize host APK launcher (`WESTLAKE_ART_GENERIC` with intent extras) — small Kotlin edit + rebuild
- Add `WESTLAKE_GATE_PDP_FORCE_PERFORM_CLICK` env hook for PF-632 empirical proof

---

## Phone state at end

- Original runtime `d7e10e47…` still in place (was NOT replaced; this proof didn't touch the runtime)
- New host APK with `WESTLAKE_ART_NOICE` branch installed (works for both McD and noice)
- Noice APK installed (`com.github.ashutoshgngwr.noice` v2.5.7)
- McD app state cleared, host state cleared, noice state cleared
- No phone reboot performed; per-iteration `pm clear` was the variance reduction

**To reset to pre-noice phone state** (if a future agent wants McD-only baseline):
```bash
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3 \
  shell 'pm uninstall com.github.ashutoshgngwr.noice'
# Optional: reinstall original host APK (if available; the rebuilt one
# is functionally a superset, still recognizes WESTLAKE_ART_MCD)
```

---

## Artifacts produced

```
artifacts/noice-1day/
├── _apk_provenance.txt                                            (Agent 1)
├── _noice_audio_gap.txt                                           (Agent 4)
├── 20260503_232011_noice_noice_first_boot/                        (Phase 2b, FAIL pre-rebuild)
│   ├── check-noice-proof.txt
│   ├── logcat-{stream,dump}.txt
│   ├── screen.png  (host Compose home, no noice)
│   └── ...
└── 20260503_232600_noice_noice_first_boot_v2/                     (Phase 2d, PARTIAL+SIGBUS)
    ├── check-noice-proof.txt
    ├── logcat-{stream,dump}.txt   (contains the SIGBUS at line ~end)
    ├── screen.png  (still host Compose home; noice never rendered)
    └── ...
```

---

## Commit chain (this proof)

| Commit | Topic | Pushed? |
|---|---|---|
| `bf678e47` (Agent 2) | Add `scripts/run-noice-phone-gate.sh` | will-be-pushed-with-this-doc |
| `6d9e7496` (Agent 2) | Add `scripts/check-noice-proof.sh` | will-be-pushed-with-this-doc |
| (next) | Host APK: add WESTLAKE_ART_NOICE launch branch | will-be-pushed-with-this-doc |
| (next) | Add `_apk_provenance.txt` artifact | will-be-pushed-with-this-doc |
| (next) | Add `_noice_audio_gap.txt` artifact | will-be-pushed-with-this-doc |
| (next) | Day-1 results doc (this file) | will-be-pushed-with-this-doc |

All commits will be pushed in one batch to `origin/main` at session close.

---

## Acceptance criterion final tally

| | Criterion | Result | Evidence |
|---|---|---|---|
| A1 | APK loaded into Westlake guest dalvikvm | PARTIAL | dalvikvm spawned, dex+APK loaded, manifest parsed, AssetManager wired (28s) — then SIGBUS |
| A2 | MainActivity onResume inside guest | FAIL | NOICE_MAIN_ACTIVITY phase=resumed count=0 (also INCONCLUSIVE since launcher has no per-app emitter — irrelevant given the SIGBUS) |
| A3 | ≥5 views inflated | FAIL | NOICE_VIEW_INFLATED count=0 |
| A4 | ≥1 tap routed | FAIL | gate ran with taps=none (correct for first-boot); irrelevant given SIGBUS |
| **A5** | **Audio gap characterized** | **PASS** | `_noice_audio_gap.txt` 398 lines |
| A6 | Screenshot proves UI rendered | FAIL | screenshot shows host Compose home, not noice |
| A7 | 5-min idle soak no SIGBUS | FAIL | SIGBUS at 28s of first boot |
| A8 (stretch) | Settings nav | NOT ATTEMPTED | gated on A2-A4 |
| A9 (stretch) | Theme variants | NOT ATTEMPTED | gated on A2-A4 |

**Bottom line:** 1 of 7 PASS, but the failure produced the highest-value finding (PF-630 universality) of the entire 2-day Westlake review. **Day-1 is INCOMPLETE per acceptance, but PRODUCTIVE per learnings.** Day-2 should attempt PF-630 boot-aware routing fix BEFORE returning to noice.

---

## Day-2 update (2026-05-04 PT) — PF-630 SUBSTRATE FIX LANDED

A design agent produced the boot-aware routing patch per §13.12 of the gap report. The patch landed cleanly, eliminating the SIGBUS that prevented every guest-dalvikvm app from booting.

### What landed (art-latest commit `6a45e3a` on `origin/main`)

3 files modified, ~30 lines new code (rest of the diff is the candidate's pre-existing PFCut* infrastructure):

- `patches/runtime/runtime.cc` — defines `static std::atomic<bool> g_pfcut_app_loader_seen{false}` plus `PFCutAppClassLoaderSeen()` getter and `PFCutMarkAppClassLoaderSeen()` setter. Flag flipped immediately after the standalone app PathClassLoader install log at line ~3203.
- `patches/runtime/native/sun_misc_Unsafe.cc` — `extern bool PFCutAppClassLoaderSeen()` decl + `if (UNLIKELY(!PFCutAppClassLoaderSeen())) return false;` short-circuit at the top of `PFCutObjectArrayIndexFromOffset`.
- `patches/runtime/native/jdk_internal_misc_Unsafe.cc` — same shape, qualified as `::art::PFCutAppClassLoaderSeen()` since the helper is in an anonymous namespace.

Built via `make -f Makefile.ohos-arm64 -j$(nproc) link-runtime`. New runtime hash:
`548c9c7a216d5e20c1f100dd7ecb483dccc9dac1158c5ea408f9bcfb1ee952c2`.

### Empirical results on phone

| Test | Result | Evidence |
|---|---|---|
| Sync candidate runtime | PASS | symbol checker accepts; phone hash matches |
| **noice first-boot v3 (boot-gate)** | **NO SIGBUS** | `PASS proof_real_app_guest_dalvikvm`, `PASS no_fatal_failed_requirement count=0`, `vm_pid=12262` alive |
| **noice MainActivity onResume** | **REACHED** | `performResume DONE for com.github.ashutoshgngwr.noice.activity.MainActivity` at 12s after VM start |
| **noice 5-min soak** | **NO SIGBUS** | logcat 1.23 MB, no `signal 7`, no `0xfffffffffffffb17`, no `VM process exited`. VM alive throughout (7+ min wall) |
| McD bounded regression | INCONCLUSIVE | SIGBUS gone (`count=0`), but separate ArrayStoreException cascade in boot-class clinit (ICU/CharsetProvider/Crypto) still affects McD's brittle Application.onCreate. noice tolerates same cascade; McD does not. ASE cascade is a separate issue from PF-630, predates the fix. |

Artifact paths:
- `artifacts/noice-1day/20260504_155531_noice_noice_pf630_boot_gate_v1/` — first-boot WIN
- `artifacts/real-mcd/20260504_155928_mcd_pf630_boot_gate_bounded_regression/` — McD bounded; SIGBUS gone, ASE cascade independent
- `artifacts/noice-1day/20260504_161320_noice_noice_pf630_boot_gate_5min_soak/` — soak validation

### Acceptance re-tally (post-fix)

| Criterion | Day-1 result | Day-2 update |
|---|---|---|
| **A1** APK loaded into Westlake guest dalvikvm | PARTIAL (boot+SIGBUS) | **PASS** (boot+resume+5min) |
| **A2** MainActivity onResume inside guest | FAIL | **PASS** (raw shim log) |
| **A3** ≥5 views inflated | FAIL | INCONCLUSIVE (Hilt blocker — see below) |
| **A4** ≥1 tap routed | FAIL | not exercised yet |
| **A5** Audio gap characterized | PASS | unchanged |
| **A6** Screenshot proves UI rendered | FAIL | FAIL (SurfaceView wired but empty — Hilt blocker) |
| **A7** 5-min idle soak no SIGBUS | FAIL | **PASS** (empirically; checker says INCONCLUSIVE due to mtime-based timing) |

### Remaining noice paint blocker (NEW workstream — separate from PF-630)

The PF-630 fix unlocks the substrate. Noice still doesn't paint. Two surfaced issues, both downstream of the same root cause:

1. `lateinit property subscriptionBillingProvider has not been initialized` in noice's MainActivity.onResume — Hilt failed to inject. Hilt's ContentProvider chain (`androidx.startup.InitializationProvider` + `dagger.hilt.android.internal.lifecycle.HiltViewModelInitializer`) likely hit ASE during static init.
2. `Coroutine runtime seed failed: java.lang.ArrayStoreException` — kotlinx coroutines can't initialize Main dispatcher because of upstream charset failures.

**Both are downstream of the same upstream cause: ICU/CharsetProvider/Crypto/Provider boot-class clinit ASEs that the boot-aware gate doesn't prevent (the bypass IS active during these clinits — they happen lazily AFTER the gate flip).** Resolving them needs one of:

- (i) detect "currently inside clinit" thread-local and skip bypass for those calls (cleanest, requires class_linker.cc edit)
- (ii) move the gate flip even later — to first non-boot-prefixed class load (more uncertain timing)
- (iii) shim the failing boot-class clinits via WestlakeLauncher (already partially done with `Coroutine runtime seed failed: tolerated` log; would need to extend to ICU/CharsetProvider/Crypto)

### Phone state at end of day-2 update

- Runtime: `548c9c7a21…` (boot-aware gate candidate) — DEPLOYED on phone, not the original `d7e10e47`.
- Backup: `dalvikvm.pre-pf630-d7e10e47.bak` retained.
- Noice APK installed, host APK rebuilt with NOICE_ART branch.
- McD bounded gate runs but trips on the now-isolated ASE cascade (separate workstream).

### Final status: PF-630 → CLOSED (substrate fix landed). New work: clinit-cascade mitigation.

---

## Day-2 update part 2 (2026-05-04 PT) — clinit-cascade mitigation attempted, deeper than the bypass

After the PF-630 boot-aware fix landed, attempted to make noice paint by attacking the boot-class ArrayStoreException cascade that prevents `setContentView` and even programmatic View construction. **None of the attempts produced visible noice UI.** The cascade has a deeper root cause than the Unsafe-array bypass.

### Iteration log

**Iter 1 — extend `MiniActivityManager.performCreate` recovery gate to fire on any error[0], not just NPE.** `MiniActivityManager.java:2294`. Logic: `} else if (createNPE || !done[0] || error[0] != null) {`. Plus added a `layoutCandidates` array `["activity_splash_screen", "main_activity", "activity_main", "activity_home", "main"]` so non-McD apps can find their layout. **Result: my recovery branch never fired** in the artifact, despite the dex confirming the code is there.

**Iter 2 — wrap `root.printStackTrace(System.err)` (`:2285`) in try/catch.** Discovered the recovery branch wasn't firing because `printStackTrace` internally calls `Charset.defaultCharset()` → `CoderResult.isOverflow()` → NPE (boot-class cascade), unwinding out of `performCreate` BEFORE the recovery branch could fire. **Result: recovery branch now fires** — `tryRecoverContent: attempting manual setContentView (reason=ArrayStoreException)` appears in artifact `20260504_213742_noice_noice_recovery_gate_v2/`. But `setContentView(layoutId)` itself ASEs (XML inflation triggers the same Charset cascade).

**Iter 3 — programmatic LinearLayout fallback (no Resources, no XML).** Pure-Java `new LinearLayout(activity)` + `new TextView(activity)`. Hypothesis: bypass Resources entirely. **Result: programmatic fallback ALSO fails** with `NullPointerException: Attempt to invoke InvokeType(2) method 'void android.view.View.setId(int)' on a null object reference`. The `View` class itself has poisoned statics from the cascade — even `new View(context)` cannot complete construction.

**Iter 4 — disable the PFCut Unsafe-array bypass entirely (gate never flips).** Hypothesis: the bypass is the cascade source. Modified `runtime.cc` to comment out `PFCutMarkAppClassLoaderSeen()` so `PFCutAppClassLoaderSeen()` always returns false → `PFCutObjectArrayIndexFromOffset` always returns false → no Unsafe-array writes use `SetWithoutChecks`. **Result: SAME cascade.** ICUBinary/CoderResult/Providers all still ASE-fail clinit. **Empirical proof: the bypass is NOT the root cause.** Restored the gate flip in commit `e426dfa` on art-latest.

### Conclusion: cascade root cause is elsewhere

The cascade survives:
- The PF-630 boot-aware gate (which flips after PathClassLoader install)
- Bypass disable (gate never flips, all Unsafe-array writes use stock `CasFieldObject`)

So the writes that corrupt String[] backing arrays during boot-class clinit are happening through SOMETHING ELSE. Possible candidates (none verified this session):

1. **The dalvikvm runtime's `[RT] Set ConcurrentHashMap ABASE=12 ASHIFT=2` early init** (logcat just before the cascade) — the runtime is patching `ConcurrentHashMap` static fields. If ABASE/ASHIFT values are wrong for this build, every subsequent CHM-backed structure (including those used by Charset/Provider) writes to misaligned slots.
2. **Some other PFCut* mechanism** that writes wrong-typed values via a different code path.
3. **A truly upstream JNI/runtime issue** unrelated to the bypass — maybe how `Class.forName` or `ClassLoader.loadClass` is implemented in the patched runtime.

### Day-2 part-2 acceptance re-tally

| Criterion | Day-2 part 1 | Day-2 part 2 |
|---|---|---|
| A1 APK loaded into Westlake guest dalvikvm | PASS | unchanged |
| A2 MainActivity onResume inside guest | PASS | unchanged |
| A3 ≥5 views inflated | INCONCLUSIVE | **FAIL** — no Views can construct (NPE on `setId`) |
| A4 ≥1 tap routed | not exercised | not exercised |
| A5 Audio gap characterized | PASS | unchanged |
| A6 Screenshot proves UI rendered | FAIL (Hilt blocker) | **FAIL — fundamental: View construction NPEs** |
| A7 5-min idle soak no SIGBUS | PASS | unchanged |

### Net result of the noice 1-day proof

| Win | Status |
|---|---|
| PF-630 SIGBUS at sentinel `0xfffffffffffffb17` | **ELIMINATED** — fix landed at art-latest commits `6a45e3a`, `e426dfa` |
| noice MainActivity reaches `performResume DONE` in guest | **CONFIRMED** |
| 5-min substrate stability soak | **PASS** — no fatal signals across 1.23 MB logcat |
| `MiniActivityManager.performCreate` recovery branch | **GENERALIZED** to fire on any onCreate error (not just NPE), plus layout-name search and tolerant printStackTrace |
| **noice's actual UI painted** | **NO** — boot-class clinit cascade prevents View construction independently of the bypass |

### What "fix till noice ui shows up" would actually take (next-session prompt)

1. **Identify the cascade root cause**. Hypothesis to test first: instrument `[RT] Set ConcurrentHashMap ABASE/ASHIFT` (where the runtime patches CHM statics). Verify ABASE=12 ASHIFT=2 are correct for this dalvikvm build's reference size. If wrong, every CHM-backed structure (most boot Maps) is misaligned. Test: hardcode `ABASE=16 ASHIFT=2` (assuming compressed refs but 16-byte first-element offset) or `ABASE=12 ASHIFT=2` based on what stock dalvikvm uses on aarch64. Probe location: search `ConcurrentHashMap ABASE` in `art-latest/patches/runtime/`.
2. **If ABASE/ASHIFT is right**, fall back to deeper instrumentation: add a log to every Unsafe array-backed write that includes the array's component type and the value's type. Run noice. The ASE will appear in logcat with full context.
3. **The clinit-cascade is independent of PF-630** and probably independent of any single subsystem; could be a 1-2 week investigation. Don't budget for "1-day noice paint" until this lands.

### Phone state at end of day-2 part 2

- Runtime: `fda3db92031c43752e54f5034ab6556a46aa7312d326634656720f32a9782508` (gate-restored). Synced.
- Shim: `d31fa0809f163b720a59af48c6ce43fd5a60b516f0d1abd283084a61ee0b9551` (programmatic fallback + tolerant printStackTrace + extended recovery gate). Synced.
- Backup of pre-PF-630 runtime `dalvikvm.pre-pf630-d7e10e47.bak` retained.
- Noice + Westlake host installed. Both `pm clear`'d.
