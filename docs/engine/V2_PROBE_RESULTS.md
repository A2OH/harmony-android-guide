# V2-Probe Results — Discovery Harness Re-Run Post V2 Substrate

**Date:** 2026-05-13
**Phone:** OnePlus 6 cfb7c9e3 (~30 min uptime at probe start)
**aosp-shim.dex md5:** `85201743954122115e7c5f925920ac48` (matches local ohos-deploy)
**Discovery harness:** CR27 manifest-driven (`DiscoverWrapperBase.java` + `NoiceDiscoverWrapper.java` 60 LOC, `McdDiscoverWrapper.java` 70 LOC)
**Substrate under test:** V2-Step1..Step8 (regression suite 14/14 PASS per `PHASE_1_STATUS.md`)
**Logs preserved:**
- `aosp-libbinder-port/test/v2-probe-noice.log` (209 KB, 2000 lines)
- `aosp-libbinder-port/test/v2-probe-mcd.log` (228 KB, 2130 lines)

---

## TL;DR

| App   | Pre-V2 high-water | V2-Probe high-water | Δ                     |
|-------|-------------------|---------------------|-----------------------|
| noice | PHASE G4 (onCreate body, `Configuration.setTo(null)` NPE) | **PHASE G3 FAIL** (Activity.attach not locatable) | **REGRESSION** (harness side) |
| McD   | PHASE G3 / G4 (Activity.attach 20-arg NoSuchMethod or onCreate Window.setTheme(null) NPE) | **PHASE G3 FAIL** (Activity.attach not locatable) — **plus** `McDMarketApplication.onCreate` advances deep into Hilt/NewRelic init before fail-loud NPE on `getSharedPreferences()` returning null | **PROGRESSION inside onCreate**, harness-G3 still blocked |

V2 design's §7 Step 8 expectations were **partially met for McD, not at all for noice**:
- noice: real `NoiceApplication.onCreate()` "returned cleanly (unexpected!)" — the harness reports no exception but emits no Hilt activity, suggesting the obfuscated NoiceApplication body did not actually run any Hilt code (Hilt classes obfuscated and unreachable by FQN; harness's class-load probe reports `Hilt_NoiceApplication` `ClassNotFoundException`).
- McD: real `McDMarketApplication.onCreate()` executed multiple statements (NewRelic ctor, perf logging) and then hit a clean fail-loud `getSharedPreferences(...) must not be null` from Kotlin null-check — Hilt-generated `Hilt_SplashActivity` IS loadable, its `_initHiltInternal` IS firing in PHASE G2.

**No real Binder transactions** (`nativeTransact`) hit our M4 services in either run — only local in-process `nativeGetLocalService` lookups (Binder substrate is the in-process JavaBBinder direct-call optimization, not the cross-process driver path). The 7 framework Singleton lookups in PHASE F (activity/package/window/display/input_method/notification/power) all returned our `Westlake*` service objects but the subsequent method calls were resolved by Java method dispatch, not Binder serialization.

**No `ServiceMethodMissing.fail` from a real app code path** — the two `WestlakeServiceMethodMissing` UOEs (`window.isViewServerRunning`, `display.getPreferredWideGamutColorSpaceId`) both fired from PHASE F harness-driven framework Singletons, not from app body code.

---

## Section 1: noice progression

### PHASE A — service probes
7/70 typical Android services resolved (activity, package, window, display, input_method, notification, power) — all backed by our `Westlake*ManagerService` registered via `nativeAddService`. Unchanged from previous runs.

### PHASE B — APK classload
5/7 candidates loadable:
- `com.github.ashutoshgngwr.noice.NoiceApplication` — loadable
- `com.github.ashutoshgngwr.noice.Hilt_NoiceApplication` — **ClassNotFoundException** (R8-obfuscated; FQN no longer exists)
- `com.github.ashutoshgngwr.noice.activity.MainActivity` — loadable
- `com.github.ashutoshgngwr.noice.activity.Hilt_MainActivity` — **ClassNotFoundException** (same)
- `com.github.ashutoshgngwr.noice.fragment.HomeFragment` — loadable
- `dagger.hilt.android.internal.managers.ApplicationComponentManager` — loadable (Hilt runtime jar)
- `dagger.hilt.android.HiltAndroidApp` — loadable (annotation interface)

The R8-obfuscation hides `Hilt_NoiceApplication` behind some obfuscated name (e.g., something like `v3.e`, which IS observed as `MainActivity`'s superclass at PHASE G2). The Hilt-generated `Hilt_NoiceApplication` exists in the APK but only under its obfuscated FQN.

### PHASE C — `NoiceApplication.<init>`
Clean. Constructor returns. Identity hash `0xd048f27`.

### PHASE D — `attachBaseContext(WestlakeContextImpl)`
Clean. `getPackageName()` post-attach returns `com.github.ashutoshgngwr.noice`.

### PHASE E — `NoiceApplication.onCreate()`
**Reported "returned cleanly (unexpected!)"** — but the log between the `calling NoiceApplication.onCreate()` line and the PHASE F header contains only framework class-init traces (`[PFCUT] Class.newInstance ... ForBoolean`, `... ForStringSet`, then `StringFactory Charset fallback`). NO MCD-CALL-style `[NOICE-CALL]` log markers exist for noice (there's no equivalent dynamic-call tracer wired up for the noice package). We cannot tell from the log alone whether `NoiceApplication.onCreate`'s real body executed or merely returned without doing anything. The "unexpected!" annotation in the harness suggests the harness designers expected this to throw and treat the silent return as suspicious.

### PHASE F — framework Singletons
- `ActivityManager.getService()` → `WestlakeActivityManagerService@4d4a441` ✓
- `IActivityManager.getRunningTasks(10)` — method not found on our service (harness graceful)
- `IActivityManager.getCurrentUser()` → `UserInfo{0:Owner:1}` ✓
- `ActivityThread.getPackageManager()` → `WestlakePackageManagerService` ✓
- `IPackageManager.getApplicationInfo("com.github.ashutoshgngwr.noice")` → `null` (no app DB)
- `IPackageManager.getInstalledPackages(0)` → empty ParceledListSlice ✓
- `WindowManagerGlobal.getWindowManagerService()` → `WestlakeWindowManagerService` ✓
- `IWindowManager.isViewServerRunning()` → **fail-loud UOE** (Tier-1 candidate)
- `DisplayManagerGlobal.<init>` → **fail-loud UOE** on `display.getPreferredWideGamutColorSpaceId()` (Tier-1 candidate)
- `AudioSystem.getMasterMute()` → `false` ✓
- `InputMethodManager` class loadable ✓
- `IActivityManager.Stub.asInterface(null)` → `null` ✓

Two fail-loud UOEs surfaced (`isViewServerRunning`, `getPreferredWideGamutColorSpaceId`); both are M4_DISCOVERY Tier-1 candidates.

### PHASE G — MainActivity launch driver
- `Looper.prepareMainLooper()` ✓
- `Instrumentation.newActivity(MainActivity)` ✓ — `MainActivity@a1405ed`
- Superclass: `v3.e` (R8-obfuscated AppCompatActivity)
- `MainActivity.<init>` runs through `androidx.activity.ComponentActivity.<init>(SourceFile:121)` → triggers `Build$VERSION.<clinit>` → `[PF-arch-051] ThrowAIOOB length=0 index=0` (tolerated)

### PHASE G3 — **FAIL: Activity.attach not locatable**

```
DISCOVER-FAIL: PHASE G3: Activity.attach locate threw
  java.lang.NoSuchMethodException: no Activity.attach with Context first arg
```

**Root cause of the regression:** V2-Step2 (per `framework_duplicates.txt:86`) shadowed `android.app.Activity` with the shim version (`shim/java/android/app/Activity.java`, 1209 LOC). The shim's `attach` is the **6-arg** overload:
```java
public final void attach(
    Context base, Application application, Intent intent,
    ComponentName component, Window window, Instrumentation instrumentation)
```

The harness `DiscoverWrapperBase.locateActivityAttach` (line 939) **rejects** anything with `pt.length < 10`:
```java
if (pt.length < 10) continue;
```

— it was written for V1's framework-shaped 18-20-arg `Activity.attach`. It walks the class hierarchy (`v3.e` → ComponentActivity → FragmentActivity → ... → our shim Activity → ContextThemeWrapper → ContextWrapper → Context → Object) and never finds a 10+-arg attach because the V2 substrate doesn't HAVE one.

**This is a harness/substrate impedance mismatch**, not a substrate failure. The V2 design (`BINDER_PIVOT_DESIGN_V2.md §3.3` + `WESTLAKE_ACTIVITY_API.md §2.1`) explicitly chose the 6-arg attach as the boundary; V2-Step6 (`WestlakeActivityThread.attachActivity`) calls it directly with no reflection. The harness needs to be updated to match.

### Hilt @Inject firing? — **NO observable evidence**
- `Hilt_NoiceApplication` not loadable by FQN (obfuscated)
- `Hilt_MainActivity` not loadable by FQN (obfuscated)
- No `[NOICE-CALL]` style trace exists for noice's dynamic dispatch
- PHASE E onCreate "returned cleanly (unexpected!)" — body opacity prevents confirmation

### Real Binder transaction into M4 services? — **NO**
- Zero `nativeTransact` calls in the entire log
- 7 `nativeGetLocalService` lookups (in-process pointer fetches, NOT cross-process Binder transactions)
- noice never advanced past PHASE G3, so app body never invoked any service method

---

## Section 2: McD progression

### PHASE A — service probes
7/70 services resolved (identical to noice).

### PHASE B — APK classload
**7/7 candidates loadable** — all McD classes including activities all visible by FQN (NOT R8-obfuscated like noice).

### PHASE C — `McDMarketApplication.<init>`
Clean. Identity hash `0x3c9d9fa`.

### PHASE D — `attachBaseContext(WestlakeContextImpl)`
Clean. `getPackageName()` post-attach returns `com.mcdonalds.app`.

### PHASE E — `McDMarketApplication.onCreate()` — **FAIL with deep progression**

The log shows real app-body execution before the failure:
```
[MCD-CALL] McDMarketApplication.onCreate() -> System.currentTimeMillis()
[PFCUT] McD perf arg0 String("McDMarketApplication:onCreate : before")
[PFCUT] McD perf noop void com.mcdonalds.app.performanalytics.NewRelicImpl.<init>()
DISCOVER-FAIL: McDMarketApplication.onCreate() threw java.lang.reflect.InvocationTargetException: null
  cause[1]: java.lang.NullPointerException: getSharedPreferences(...) must not be null
```

**The failure is fail-loud and diagnostic.** Kotlin's compiler emits explicit not-null assertions for platform types; `WestlakeContextImpl.getSharedPreferences(name, mode)` at line 490 returns `null` (commented as "Hilt @Provides for preferences are common; we defer until we see a discovery hit"). McD's Application.onCreate just hit that discovery boundary.

This is exactly the V2 design's promise (§7 Step 8): "first dashboard binder transaction issued to our M4a (or fails fail-loud with `ServiceMethodMissing.fail` revealing the next service method to implement — *legitimate* discovery, not field-plant discovery)." We didn't quite get to a Binder transaction, but we DID get to a fail-loud null return from a shim-owned context method.

### PHASE F — framework Singletons (5 framework Singletons driven, 1 failed)
Same as noice: `IWindowManager.isViewServerRunning` UOE plus `DisplayManagerGlobal` chain UOE. Note `IActivityManager.getCurrentUser` succeeds and returns `UserInfo{0:Owner:1}` (proves WLK-binder-jni is wired correctly; the local optimization short-circuits real Binder).

### PHASE G — SplashActivity launch driver

**`Instrumentation.newActivity` succeeds** — `com.mcdonalds.mcdcoreapp.common.activity.SplashActivity@e988276`. Superclass: `com.mcdonalds.mcdcoreapp.common.activity.Hilt_SplashActivity` (Hilt-generated, **non-obfuscated** in McD).

Critical observation — `[MCD-CALL]` traces show **Hilt initialization firing**:
```
[MCD-CALL] SplashActivity.<init>() -> Hilt_SplashActivity.<init>()
[MCD-CALL] Hilt_SplashActivity.<init>() -> Hilt_SplashActivity._initHiltInternal()
[MCD-CALL] _initHiltInternal() -> Hilt_SplashActivity$1.<init>(Hilt_SplashActivity)
[MCD-CALL] _initHiltInternal() -> ComponentActivity.addOnContextAvailableListener(...)
```

`Hilt_SplashActivity._initHiltInternal()` is the Hilt-generated init that runs at activity-construction time. It builds a `OnContextAvailableListener` callback that fires when `attachBaseContext` runs, which is when Hilt's actual `@Inject` field injection happens. **The harness never reaches that callback** because PHASE G3 fails before `attach` is called.

### PHASE G3 — **FAIL: Activity.attach not locatable** (same as noice)

```
PHASE G:    superclass: com.mcdonalds.mcdcoreapp.common.activity.Hilt_SplashActivity
DISCOVER-FAIL: PHASE G3: Activity.attach locate threw
  java.lang.NoSuchMethodException: no Activity.attach with Context first arg
```

Same root cause as noice: shim Activity has only a 6-arg `attach`; harness requires `>= 10`.

### Hilt @Inject firing? — **PARTIALLY** (init scaffold only)
- `Hilt_SplashActivity._initHiltInternal()` runs ✓
- Hilt $1 lambda constructed and registered as OnContextAvailableListener ✓
- The listener body (which would call `inject(this)` to populate `@Inject` fields) **never fires** — it would fire from `Activity.attachBaseContext`, which is downstream of `Activity.attach`, which the harness can't locate.

### Real Binder transaction into M4 services? — **NO**
- Zero `nativeTransact` calls in the log
- 7 `nativeGetLocalService` lookups (same in-process optimization as noice)
- The 7 services are reached via local pointer; method calls dispatch through the in-process JavaBBinder, not the binder driver
- McD onCreate exception hit before any service-method call from real app body code

---

## Section 3: Hilt @Inject + Binder transaction evidence

| Question                                            | noice                                | McD                                                |
|-----------------------------------------------------|--------------------------------------|----------------------------------------------------|
| Hilt-generated classes loadable from APK?           | NO (R8-obfuscated)                   | YES                                                |
| Hilt init scaffold (`_initHiltInternal`) fires?     | UNKNOWN (no trace tooling for noice) | **YES** — observed in McD SplashActivity path      |
| Hilt @Inject fields populated?                      | UNKNOWN                              | **NO** — population deferred to OnContextAvailable, which never fires |
| Real `Binder.transact` to M4 service?               | **NO**                               | **NO**                                             |
| In-process JavaBBinder lookups to M4 services?      | YES (7)                              | YES (7)                                            |
| App-body calls into M4 service methods?             | NO (blocked at G3 before app body)   | NO (Application.onCreate blocked at getSharedPrefs NPE) |
| Fail-loud `ServiceMethodMissing.fail` from app body?| NO                                   | NO (the NPE came from `WestlakeContextImpl.getSharedPreferences` returning null, which is a Tier-1 method that needs implementation, NOT a `WestlakeServiceMethodMissing` throw) |

**Bottom line on V2 payoff: Hilt @Inject did NOT fire any real Binder transaction into our M4 services in either app.**

---

## Section 4: Comparison to V2 design expectations (§7 Step 8)

From `BINDER_PIVOT_DESIGN_V2.md §7 Step 8` (line 584-585):

> - Expect: noice reaches `MainActivity.onCreate` *completion*, hits the first Hilt @Inject method body
> - Expect: McD reaches `McDMarketApplication.onCreate` completion, `SplashActivity.onCreate` completion, Hilt-injected fields populated, first dashboard binder transaction issued to our M4a

| Expectation                                      | Actual                                                          | Verdict          |
|--------------------------------------------------|-----------------------------------------------------------------|------------------|
| noice MainActivity.onCreate completion           | Stopped at PHASE G3 before `attach`, never reached `onCreate`   | **NOT MET**      |
| noice first Hilt @Inject method body             | No Hilt evidence; obfuscation conceals progress                 | **NOT MET**      |
| McD McDMarketApplication.onCreate completion     | `Application.onCreate` threw NPE on `getSharedPreferences`      | **NOT MET**      |
| McD SplashActivity.onCreate completion           | SplashActivity instantiated; Hilt init scaffold fired; G3 blocked before `attach`/`onCreate` | **NOT MET**      |
| McD Hilt @Inject fields populated                | Only the OnContextAvailable LISTENER was registered             | **NOT MET**      |
| McD first dashboard binder transaction to M4a    | Zero cross-process Binder transactions; no app-body service calls | **NOT MET**      |

**V2 substrate did NOT deliver its §7 Step 8 architectural payoff in this probe run.** The substrate is structurally sound (14/14 regression PASS, 7/7 services bound, framework Singletons mostly reachable), but two issues block end-to-end exercise:

1. **Harness stale relative to V2 surface:** `DiscoverWrapperBase.locateActivityAttach` requires `>= 10`-arg `Activity.attach` (framework-shaped); V2's shim Activity declares only a 6-arg overload. PHASE G3 will always fail with V2 + the current harness. The harness needs a small change: locate by name only, accept 6-arg, and let `WestlakeActivityThread.attachActivity` (V2-Step6) drive the call.

2. **`WestlakeContextImpl.getSharedPreferences` returns null:** real McD onCreate code immediately needs a working SharedPreferences. The shim says `// we defer until we see a discovery hit on this` — this probe IS the discovery hit. A real (or stub-but-non-null) `SharedPreferences` impl needs to be wired in. Same problem will hit noice once G3 is unblocked.

**Note on the PHASE_1_STATUS.md "noice-discover PHASE G4" claim** (line 3): that claim references the post-CR24 / pre-V2 run state, where the shim Activity had NOT yet won classpath resolution and framework's 20-arg attach was being driven via the harness's `locateActivityAttach` + `buildAttachArgs` reflective dance. Post-V2-Step2, the shim Activity wins resolution and the reflective dance no longer applies. The status doc accurately reflects what the harness measured pre-V2 but is stale relative to V2's actual call path.

---

## Section 5: Next discovery iteration (M4-PRE15-class work)

Two distinct next blockers identified, with specific resolutions:

### Blocker #1: Harness PHASE G3 `locateActivityAttach` requires 10+-arg
**File:** `aosp-libbinder-port/test/DiscoverWrapperBase.java:939`
**Fix:** Drop the `pt.length < 10` filter; accept any `attach(Context, ...)` overload. Build args using the V2-aware path (6-arg: Context, Application, Intent, ComponentName, Window, Instrumentation) — much simpler than the existing 18-20-arg reflective construction.

The shim `Activity.attach` 6-arg signature is:
```java
public final void attach(
    Context base, Application application, Intent intent,
    ComponentName component, Window window, Instrumentation instrumentation)
```

Pull `mApplication` from PHASE D, the `Intent` from PHASE G's component, build a stub `Window` via `new PhoneWindow(this)` (the shim constructor already supports it), use the `Instrumentation` from PHASE G.

### Blocker #2: `WestlakeContextImpl.getSharedPreferences` returns null
**File:** `shim/java/com/westlake/services/WestlakeContextImpl.java:490`
**Fix candidates** (per ANDROID_SHIM_AUDIT.md / decision required):
- (a) Wire a real `android.app.SharedPreferencesImpl` backed by `/data/local/tmp/westlake/prefs/{packageName}/{name}.xml` (matches AOSP semantics)
- (b) Return an empty in-memory `SharedPreferences` stub (Map-backed, no persistence)
- (c) Route through Binder to a new M-tier service (over-engineered)

McD discovery argues for (a) since Hilt @Provides commonly produce `Preferences` modules that are heavily exercised at onCreate time. (b) gets us through discovery cheap; (a) is the production answer.

### Anticipated subsequent blockers (post-fix prediction, NOT yet observed)
Once G3 is unblocked AND getSharedPreferences works, the next likely blockers per V2 design §3.5 and observed `MCD-CALL` traces are:
- `WestlakeContextImpl.getSystemService(name)` paths for `connectivity` / `location` / other services NOT in the 7 registered (these will trip `WestlakeServiceMethodMissing.fail` honestly per CR19 audit, surfacing Tier-1 candidates as designed)
- Real `findViewById` chain inside `setContentView(R.layout.main_activity)` — exercises the WestlakeResources arsc parser path
- Hilt's `OnContextAvailableListener` body — runs `@Inject` injection; first call into a Hilt component-provided service; this is where real `@Inject` field population happens

Each of these is a *legitimate* discovery boundary (a new Tier-1 method or a missing surface area in WestlakeResources), not a field-plant boundary.

---

## Section 6: Honest gap to "app actually runs"

**Direct answer to the user's question — does noice run? does McD's dashboard attach?** No.

Neither app gets past the equivalent of the first 5-10 seconds of normal Android app boot. McD's `Application.onCreate` runs ~3 statements before NPE; noice's runs an opaque body and exits silently. The MainActivity / SplashActivity is instantiated but never `attach`'d, so its `onCreate` never runs, so `setContentView` never runs, so no UI exists.

### Specific gaps to bridge for "runs end-to-end"

| Gap                                       | Cost estimate          | Source                                       |
|-------------------------------------------|------------------------|----------------------------------------------|
| Fix PHASE G3 harness (1 file edit)        | ~0.5 day               | This probe, §5                               |
| `WestlakeContextImpl.getSharedPreferences`| ~1 day (stub) / 2-3 days (real file-backed) | This probe, §5                               |
| Next 5-10 Tier-1 service methods (estimated) | ~3-5 days             | V2 design §7 Step 8 expectation + history    |
| Hilt-injected Binder transactions actually firing | unknown until G3 unblocked | TBD                                          |
| Real visible UI (M6 surface daemon)       | **~12 person-days** per CR21 | `M6_SURFACE_DAEMON_PLAN.md`                  |
| Audio (noice critical path)               | **~6.5 person-days** per M5 plan | `M5_AUDIO_DAEMON_PLAN.md`                  |
| Real network / OkHttp / Retrofit          | unknown, large         | Not yet exercised by any test                |
| Real touch input / KeyEvent dispatch      | unknown, medium-large  | Not yet exercised                            |
| Real Lifecycle lifecycle observer chain   | unknown               | First exercised when Activity.attach actually invokes onStart() |

**Most optimistic scenario:** A focused 1-2 week sprint after this probe could unblock PHASE G3, get `getSharedPreferences` working, and reach **first real Binder transaction from Hilt-injected service** for McD's SplashActivity. That would be a legitimate V2 payoff demonstration.

**Most realistic scenario:** Reaching "McD splash screen renders pixels" requires M6 surface daemon (12 days) on top of all the above, and reaching "McD dashboard attaches" requires the full HTTP/OkHttp/auth chain working over those rendered pixels with touch — quarters of effort, not weeks.

The current architectural substrate (V2 + M1/M2/M3 + 6 M4 services) is sound for further discovery. The gap from "discovery-harness-instrumented dalvikvm boots an app's first onCreate" to "an end-user installable app runs" is large and consistent with the multi-month timeline implied by `WESTLAKE-STATUS.md`.

---

## Appendix: Probe execution details

**Step 1: clean phone state** — `vndservicemanager` PID 2986 after restart, clean.

**Step 2: noice run** — `noice-discover.sh` ran in 5-min cap, exited cleanly with the harness's normal "DISCOVERY REPORT" format. No SIGBUS, no PF-arch-054 sentinel hits (CR24 workaround held).

**Step 3: McD run** — `mcd-discover.sh` ran in 5-min cap (after second `vndservicemanager` restart, PID 4827), exited cleanly. Same shape report.

**SM logs** — both noice and McD SM logs contain only the single `Starting sm instance on /dev/vndbinder` line — no transaction activity, confirming all service traffic short-circuited via the in-process JavaBBinder optimization (this is normal and expected for the current substrate; cross-process Binder is exercised by the regression suite tests directly).

**Build state at probe time:**
- `aosp-shim.dex` 1,428,152 bytes, md5 `85201743...`, deployed 2026-05-13 09:01
- `NoiceDiscoverWrapper.dex` 35704 bytes, deployed 2026-05-13 07:25
- `McdDiscoverWrapper.dex` 40656 bytes, deployed 2026-05-13 07:25
- `framework_duplicates.txt` confirms V2-Step2 (Activity), V2-Step3 (Application), V2-Step8 (Resources) shadows active (entries commented out — meaning framework.jar's versions are NOT stripped, so shim wins by BCP ordering: `aosp-shim.dex` precedes `framework.jar` on BCP)

**Person-time spent on V2-Probe:** ~75 minutes.
