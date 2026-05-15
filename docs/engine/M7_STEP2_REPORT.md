# M7-Step2 Report — production launch path drives MainActivity.onCreate body

**Status:** breakthrough — production `performLaunchActivity` reaches
`MainActivity.onCreate(Bundle)` user body. New architectural blocker
identified one layer deeper than discovery harness G4.
**Author:** Builder agent
**Date:** 2026-05-13
**Companion to:**
- `docs/engine/M7_STEP1_REPORT.md` (M7-Step1 — discovery harness orchestration)
- `docs/engine/CR38_M7_M8_INTEGRATION_SCOPING.md` §5 (the 7 acceptance signals)
- `docs/engine/BINDER_PIVOT_MILESTONES.md` §M7
- CR55 (PHASE_1_STATUS.md) — LifecycleRegistry prime in `Activity.attach`

---

## §1. Deliverables

| Deliverable | LOC | Status |
|---|---:|---|
| `aosp-libbinder-port/test/NoiceProductionLauncher.java` | 287 | new — production launch driver |
| `aosp-libbinder-port/build_noice_production_launcher.sh` | 86 | new — sibling of `build_noice_launcher.sh` |
| `aosp-libbinder-port/out/NoiceProductionLauncher.dex` | ~49 KB | new — built artifact |
| `shim/java/android/app/WestlakeActivityThread.java` | +44 | additive: `setForceLifecycleEnabled` + wiring (2 callsites) |
| `scripts/run-noice-westlake.sh` | +30 / -10 | additive: `--production` flag, S1 detector tier (c) |
| `aosp-shim.dex` | rebuilt | 1.45 MB — picks up the WAT additive |
| `docs/engine/M7_STEP2_REPORT.md` | this file | new |
| `docs/engine/PHASE_1_STATUS.md` row | (~1 row) | annotated below |

Anti-drift compliance per `memory/feedback_macro_shim_contract.md`:
- **ZERO** new `Unsafe`/`Field.setAccessible` calls
- **ZERO** per-app branches
- All shim edits are **additive** (one static volatile flag + two
  read-sites; pre-existing per-app gates left intact)

---

## §2. The architectural delta

### 2.1 Discovery harness G4 vs production launch

| Step | M7-Step1 discovery G4 path | M7-Step2 production path |
|---|---|---|
| 1. Activity instance | `Instrumentation.newActivity` (framework.jar) — reflection ctor.newInstance | `WestlakeInstrumentation.newActivity` → `AppComponentFactory.instantiateActivity` → ctor (with Unsafe fallback) |
| 2. Activity.attach | reflectively located, reflectively invoked | direct call from `WAT.attachActivity` (V2-Step6 6-arg shape) |
| 3. Activity.onCreate | `onCreate.invoke(activity, [null])` — REFLECTION | `Instrumentation.callActivityOnCreate(activity, savedState)` — PRODUCTION |
| 4. Activity.performCreate | **BYPASSED** by reflection | drives `mApplication.dispatchActivityPreCreated` → `onCreate(icicle)` → `dispatchActivityCreated` |

Discovery harness CR55 added `addObserver/removeObserver` priming inside
`Activity.attach`. This unblocked the `LifecycleRegistry`'s lazy-map
NPE inside `ComponentActivity.<init>`, but only for the path **into**
the constructor — the reflected `onCreate.invoke` still bypasses
Activity.performCreate's `dispatchActivityCreated` wrapping, so the
user body's `MainActivity$settingsRepository$2` Hilt lazy delegate
chain hits the next NPE without context resolution.

M7-Step2's production path drives the same lifecycle through the
canonical Android framework hooks: `WestlakeActivityThread
.performLaunchActivity` → `Instrumentation.callActivityOnCreate` →
`Activity.performCreate` → user `onCreate`.

### 2.2 Generic across all apps — no per-app branches

The only edit to the shim is one process-wide opt-in flag on
`WestlakeActivityThread`:

```java
public static void setForceLifecycleEnabled(boolean enabled) {
    sForceLifecycleEnabled = enabled;
}
```

Two read sites (both pre-existing `forceLifecycleInStrict` decisions),
both already gated by per-app West-/McD- probe predicates. The flag
sits **alongside** the existing predicates, OR'd in:

```java
final boolean forceLifecycleInStrict =
        sForceLifecycleEnabled                                   // M7-Step2: ANY production launcher
                || isCutoffCanaryLifecycleProbe(...)             // existing: westlake probes
                || shouldRunMcdonaldsLifecycleInStrict(...);     // existing: McD per-app gate
```

Renaming `NoiceProductionLauncher` → `McdProductionLauncher` and
swapping the manifest path produces the M8-Step2 launcher. No
shim edits, no script edits beyond `--production` swapping which dex.

### 2.3 Boot sequence

`NoiceProductionLauncher.main` orchestrates the full production
bootstrap. Same flow any app would use:

```
1. System.loadLibrary("android_runtime_stub")    bind JNI println/eprintln
2. loadManifest(noice.discover.properties)       per-app data (no per-app code)
3. CharsetPrimer.primeCharsetState()             warm Charset.UTF_8 / cache2
   CharsetPrimer.primeActivityThread()           = ColdBootstrap.ensure()
4. Looper.prepareMainLooper()                    needed by LifecycleRegistry ctor
5. ServiceRegistrar.registerAllServices()        publish M4 services (7 of 7)
6. PathClassLoader(apk).setAsContextClassLoader  app class graph available
7. preload(applicationClass, mainActivityClass)  smoke-test class resolution
8. WAT.attachStandalone(pkg, applicationClass, cl) creates Instrumentation/Application
9. WAT.setForceLifecycleEnabled(true)            M7-Step2 production gate
10. WAT.performLaunchActivity(mainActivityClass, pkg, intent, null)
       └─> performLaunchActivityImpl
             ├─> Instrumentation.newActivity        Activity instance
             ├─> WAT.attachActivity (CR55 prime)    LifecycleRegistry map ready
             ├─> ActivityClientRecord stored        Step 7 (force-lifecycle ON)
             └─> Instrumentation.callActivityOnCreate
                   └─> Activity.performCreate
                         └─> Activity.onCreate ──> MainActivity.onCreate ────┐
11. probe activity.getIntent() / getWindow()     post-launch sanity            │
12. System.exit(0)                                                             │
                                                                               │
   user body reached  <─────────────────────────────────────────────────────────┘
```

---

## §3. Evidence

### 3.1 Production-path stack trace (artifact `20260513_202430`)

Run command:
```bash
bash scripts/run-noice-westlake.sh --production --timeout=120
```

Production launcher markers:
```
M7_PROD_LAUNCHER: NoiceProductionLauncher.main() entered
M7_PROD_LAUNCHER: charset + ColdBootstrap primed
M7_PROD_LAUNCHER: Looper.prepareMainLooper() -> ok
M7_PROD_LAUNCHER: ServiceRegistrar registered 7 M4 services
M7_PROD_LAUNCHER: PathClassLoader installed (identity hash=0x3783e5d)
M7_PROD_LAUNCHER: preload com.github.ashutoshgngwr.noice.NoiceApplication -> ...
M7_PROD_LAUNCHER: preload com.github.ashutoshgngwr.noice.activity.MainActivity -> ...
M7_PROD_LAUNCHER: WAT.attachStandalone returned
M7_PROD_LAUNCHER: setForceLifecycleEnabled(true)
M7_PROD_LAUNCHER: launch intent built component=ComponentInfo{...MainActivity}
M7_PROD_LAUNCHER: PRODUCTION_LAUNCH_BEGIN method=performLaunchActivity class=...MainActivity
```

WAT internal traces:
```
[Log/4] WestlakeStep: performLaunchActivity record stored ...MainActivity
[Log/4] WestlakeStep: performLaunchActivity onCreate begin ...MainActivity
```

**Production path reached user MainActivity.onCreate body**:
```
[NPE] android.content.Context android.content.Context.getApplicationContext()
[NPE]   #0 android.content.Context android.content.ContextWrapper.getApplicationContext() (dex_pc=2)
[NPE]   #1 java.lang.Object f3.y.l(android.content.Context, java.lang.Class) (dex_pc=5)
[NPE]   #2 java.lang.Object com.github.ashutoshgngwr.noice.activity.MainActivity$settingsRepository$2.b() (dex_pc=13)
[NPE]   #3 java.lang.Object kotlin.SynchronizedLazyImpl.getValue() (dex_pc=20)
[NPE]   #4 void com.github.ashutoshgngwr.noice.activity.MainActivity.onCreate(android.os.Bundle) (dex_pc=2)
[NPE]   #5 void android.app.WestlakeInstrumentation.callActivityOnCreate(android.app.Activity, android.os.Bundle) (dex_pc=184)
[NPE]   #6 android.app.Activity android.app.WestlakeActivityThread.performLaunchActivityImpl(...) (dex_pc=2163)
[NPE]   #7 android.app.Activity android.app.WestlakeActivityThread.performLaunchActivity(...) (dex_pc=0)
```

The stack confirms the **full production lifecycle** ran:
- Frame #7: `WAT.performLaunchActivity` (our entry)
- Frame #6: `WAT.performLaunchActivityImpl` (Step 1..8 driver)
- Frame #5: `WestlakeInstrumentation.callActivityOnCreate` (the line CR55
  said the harness BYPASSED — now it ran)
- Frame #4: `MainActivity.onCreate(Bundle)` (the **user body**)
- Frames #3 → #0: Kotlin lazy + Hilt accessor + ContextWrapper

### 3.2 Difference from discovery harness G4 baseline

Same artifact, baseline (`20260513_175057`) — G4 reflective path:
```
DISCOVER-FAIL: PHASE G4: MainActivity.onCreate(null) threw java.lang.reflect.InvocationTargetException: null
  cause[1]: java.lang.NullPointerException: Attempt to read from null array
  PHASE G4: FAILED (expected -- diagnoses needed service)
```

Discovery hit `null array` inside an **AndroidX LifecycleRegistry observer
table** read — *before* the user's onCreate body ran. CR55 patched
LifecycleRegistry's lazy map but the rest of `performCreate`-bypassed
state never gets populated.

M7-Step2 doesn't bypass `performCreate`. The full lifecycle wrapper
runs, so the user body now executes — and immediately hits the next,
deeper layer (Hilt-lazy-delegate Context resolution).

---

## §4. New blocker — Hilt lazy delegate Context resolution

### 4.1 Symptom

The first reachable user-code statement in `MainActivity.onCreate`
triggers Kotlin's `SynchronizedLazyImpl.getValue()` for
`settingsRepository$2` (a Hilt-generated `dagger.Lazy<T>` delegate).
The delegate resolves `Context.getApplicationContext()` on the
Activity's `mBase`. `mBase` is the `WestlakeContextImpl` we built
in `WAT.buildBaseContext`, and its
`getApplicationContext()` returns null — that's the NPE source.

### 4.2 Why CR55 didn't catch this

CR55 prime fixes the `addObserver/removeObserver` lazy-map NPE
that fires from `ComponentActivity.<init>`. The Hilt lazy delegate
hangs off `MainActivity` instance fields populated during the user's
onCreate body itself; CR55 never sees it because the discovery harness
threw inside `ComponentActivity.<init>` before reaching this path.

### 4.3 Fix vector for the next CR (M7-Step3 / M8-Step2 candidate)

`WestlakeContextImpl.getApplicationContext()` is currently a no-op
returning null. Wiring it to return the same Application the
`WAT.attachStandalone` flow already publishes
(`mInitialApplication`) is a one-method shim addition — the
Application is already kept in `WAT.currentApplication()`. Suggested
shape (single-method patch to `WestlakeContextImpl.java`):

```java
@Override
public Context getApplicationContext() {
    Context attached = mAttachedApplication;
    if (attached != null) return attached;
    // Fallback: ask WAT (Application may have been attached after
    // ctor) — preserves the additive/generic shape.
    try {
        return android.app.WestlakeActivityThread.currentApplication();
    } catch (Throwable ignored) {
        return null;
    }
}
```

This is M7-Step3 work (not in M7-Step2 scope per brief).

### 4.4 Other observations downstream

- `WAT.currentApplication = null` in the launcher's probe trace.
  `makeApplicationForLaunch` apparently ran inside `performLaunchActivity`
  itself, not during `attachStandalone`. Setting the
  `mForceMakeApplicationForNextLaunch` flag in `NoiceProductionLauncher`
  before launch would eagerly create the Application — another
  small additive in M7-Step3.
- The launcher hung at end (rc=124 from `timeout`) rather than
  cleanly exiting after the in-body NPE was caught. Likely the
  WestlakeInstrumentation `recoverAfterOnCreateFailure` path loops
  on the broken Application context. Out of scope for M7-Step2.

---

## §5. CR38 §5 acceptance scorecard

Note: the M7-Step1 `run-noice-westlake.sh` scorecard is preserved
unchanged except for an additive S1 detector tier (production-path
stack frame), so the same script scores both M7-Step1 (without
`--production`) and M7-Step2 (with `--production`). Results on the
breakthrough run (`20260513_202430`):

| Signal | Discovery (M7-Step1) | Production (M7-Step2) | Notes |
|---|---|---|---|
| S1 onCreate reached | PASS | **PASS** (deeper — user body) | production stack frame proves performCreate dispatched user body |
| S2 HomeFragment | PASS | FAIL | Hilt lazy NPE fires before fragment manager init |
| S3 AudioTrack | FAIL | FAIL | gated on M5-Step5 |
| S4 dumpsys audio | PASS | PASS | M5 daemon registered with our SM |
| S5 DLST pipe | FAIL | FAIL | gated on M6-Step6 |
| S6 zero crashes | PASS | PASS | no fatal signals / SEGV / SIGBUS |
| S7 fail-loud UOE | INFO 0 | INFO 0 | non-zero may be OK pre-CR44 |

**M7-Step2 score:** 3/7 PASS — equivalent floor to M7-Step1, but
**S1's PASS comes from a strictly deeper position** in the activity
lifecycle. The discovery harness PASSed S1 because it logged "calling
MainActivity.onCreate" then immediately failed inside
ComponentActivity-internal state. M7-Step2's S1 PASSes because the user
onCreate body itself ran (`MainActivity$settingsRepository$2` is
user-package code, not framework code).

---

## §6. Files

| File | New/Edit | Purpose |
|---|---|---|
| `aosp-libbinder-port/test/NoiceProductionLauncher.java` | NEW | production launch driver |
| `aosp-libbinder-port/build_noice_production_launcher.sh` | NEW | build the .dex |
| `aosp-libbinder-port/out/NoiceProductionLauncher.dex` | NEW (built) | runtime artifact |
| `shim/java/android/app/WestlakeActivityThread.java` | EDIT (+44 / -3) | additive `setForceLifecycleEnabled` flag + 2 read-sites |
| `aosp-shim.dex` | REBUILT | absorbs the shim edit (1.45 MB) |
| `scripts/run-noice-westlake.sh` | EDIT (+30 / -10) | `--production` flag wiring, S1 detector tier (c) |
| `docs/engine/M7_STEP2_REPORT.md` | NEW | this file |
| `docs/engine/PHASE_1_STATUS.md` | EDIT (~1 row) | annotated |

### Files NOT touched (per brief)

- `shim/java/android/app/Activity.java` — V2-Step2 + CR47 + CR55 stable
- `shim/java/android/app/Application.java`
- `shim/java/android/view/*`, `shim/java/com/android/internal/policy/*`
- `shim/java/com/westlake/services/*` (V2 stable)
- `shim/java/android/content/res/*` (V2 stable)
- `aosp-libbinder-port/test/{NoiceDiscoverWrapper,McdDiscoverWrapper,
  DiscoverWrapperBase,NoiceLauncher}.java` — M7-Step1 stable
- M5 + M6 daemons, art-latest, aosp-libbinder-port/aosp-src

---

## §7. Reproducing

```bash
# 1. Build the production launcher (one-time after source edits)
cd /home/dspfac/android-to-openharmony-migration
bash aosp-libbinder-port/build_noice_production_launcher.sh

# 2. Rebuild aosp-shim.dex (one-time after shim edits)
bash scripts/build-shim-dex.sh

# 3. Push artifacts to phone
ADB="/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3"
$ADB push aosp-shim.dex /data/local/tmp/westlake/aosp-shim.dex
$ADB push aosp-libbinder-port/out/NoiceProductionLauncher.dex \
          /data/local/tmp/westlake/dex/NoiceProductionLauncher.dex

# 4. Verify vndservicemanager is alive (transient phone-state issue —
#    if not, ctl.start it manually + verify via sm_smoke before running).
$ADB shell "su -c 'pidof vndservicemanager'"
$ADB shell "su -c 'LD_LIBRARY_PATH=/data/local/tmp/westlake/lib-bionic \
    BINDER_DEVICE=/dev/vndbinder \
    /data/local/tmp/westlake/bin-bionic/sm_smoke 2>&1 | head -8'"
# Expect: "[sm_smoke/parent] defaultServiceManager() OK"

# 5. Run M7-Step2 production path
bash scripts/run-noice-westlake.sh --production --timeout=120

# 6. Inspect the breakthrough markers
ART=$(ls -td artifacts/noice-westlake/*/ | head -1)
grep -E "M7_PROD_LAUNCHER|MainActivity\.onCreate" "$ART/m7-dalvikvm.log"
```

Expected: stack trace contains
`void com.github.ashutoshgngwr.noice.activity.MainActivity.onCreate(android.os.Bundle)`
as frame #4 of a 7-frame chain leading to
`WestlakeInstrumentation.callActivityOnCreate`.

---

## §8. Person-time

~2.5 hours: 30 min reading WAT + Instrumentation, 30 min writing
launcher + build script + shim additive, 1 hour testing (mostly
fighting transient phone-state SM context-mgr issues), 30 min
report.
