# CR56 — Wire `WestlakeContextImpl.setAttachedApplication` in production launch path

**Status:** code change landed; on-device validation blocked by phone-state caveat (kernel vndbinder context-manager EPERM — same M7-Step2-documented fragility)
**Date:** 2026-05-13
**Owner:** Builder
**Companion to:**
- `docs/engine/M7_STEP2_REPORT.md` (M7-Step2 — production launch path reaches MainActivity.onCreate user body; documented the NPE site this CR fixes)
- `docs/engine/BINDER_PIVOT_MILESTONES.md` §M7
- CR55 (PHASE_1_STATUS.md) — LifecycleRegistry prime in `Activity.attach`

---

## §1. Goal

M7-Step2's breakthrough delivered the first reachable user-code statement
in `MainActivity.onCreate(Bundle)` via the canonical
`Instrumentation.callActivityOnCreate → Activity.performCreate → user
onCreate body` path. The user body immediately NPEs inside Hilt's lazy
`settingsRepository$2` delegate because
`ContextWrapper.getApplicationContext()` walks to the Activity's mBase
(a `WestlakeContextImpl`) whose `mAttachedApplication` field is null.

`WestlakeContextImpl.setAttachedApplication(Application)` already exists
(M4-PRE11, WestlakeContextImpl.java:248). `publishApplicationToBaseContext`
already exists as a static helper (WestlakeActivityThread.java:186).
The fix is to call it after `mInitialApplication = app` lands in
`performLaunchActivityImpl`.

---

## §2. What landed

| File | Δ | Note |
|---|---:|---|
| `shim/java/android/app/WestlakeActivityThread.java` | +13 / -0 | additive: 1 call to existing static helper, 1 null+identity guard, 8 lines of comment |
| `aosp-shim.dex` | rebuilt | 1,453,196 bytes |
| `docs/engine/CR56_REPORT.md` | NEW | this file |
| `docs/engine/PHASE_1_STATUS.md` | 1 row | CR56 entry |

### §2.1 The diff

In `WestlakeActivityThread.performLaunchActivityImpl`, immediately after
`mInitialApplication = app; ShimCompat.setPackageName(...)` (line 1534-1535):

```java
// CR56: wire WestlakeContextImpl.setAttachedApplication on the
// Activity's base context so getApplicationContext() returns the
// Application instead of `this` (a non-Application Context).
// Hilt's dagger.hilt.android.internal.Contexts.getApplication(Context)
// walks getApplicationContext() expecting to find an Application;
// without this call, MainActivity.onCreate's first lazy Hilt
// delegate (e.g. settingsRepository$2) NPEs.  The M4-PRE11
// plumbing in makeApplication() already wires the SEPARATE
// WestlakeContextImpl that's the Application's own mBase, but the
// Activity's mBase is a DIFFERENT WestlakeContextImpl built at
// line ~1340 (buildBaseContext) — wire it here.  Generic across
// all apps, no per-app branches.
if (baseContext != null && baseContext != app) {
    publishApplicationToBaseContext(baseContext, app);
}
```

The `baseContext != app` guard prevents a self-publish when the
performLaunchActivityImpl flow uses the Application itself as the
Activity's base context (line 1338: `Context baseContext = mInitialApplication;`).

### §2.2 Why option-A (WAT) over option-B (launcher)

The brief offered two fix shapes:
- **A**: Call `publishApplicationToBaseContext` in WAT after
  `mInitialApplication` is set.
- **B**: Call `setAttachedApplication` reflectively in
  `NoiceProductionLauncher` after `attachStandalone` returns.

Option B fails because `attachStandalone`'s strict-standalone branch
(WAT.attachShared line 4470-4474) returns **before** `makeApplication`
runs — `mInitialApplication` is null at that point. The Application is
not created until inside `performLaunchActivityImpl` (line 1517's
`makeApplicationForLaunch(...)`). The launcher cannot read the
Application from `WAT.currentApplication()` at that point, so option B
would always observe a null. M7-Step2 itself documented this in §4.4:
> `WAT.currentApplication = null` in the launcher's probe trace.
> `makeApplicationForLaunch` apparently ran inside `performLaunchActivity`
> itself, not during `attachStandalone`.

Option A wires the call at the only place where both
`baseContext` (the WestlakeContextImpl that becomes the Activity's
mBase) and `app` (the freshly-built Application) are simultaneously
live — five lines after `app` is assigned and five lines before
`attachActivity` consumes both. Minimal blast radius.

---

## §3. Anti-drift compliance

Per `memory/feedback_macro_shim_contract.md`:

| Constraint | Status |
|---|---|
| ZERO new Unsafe / setAccessible | OK — the helper `publishApplicationToBaseContext` calls `Method.setAccessible(true)` internally per its M4-PRE11 design, but no new such call is added in CR56 |
| ZERO per-app branches | OK — the call is unconditional on app identity |
| ZERO new fail-loud sites | OK — `publishApplicationToBaseContext` no-ops on non-WestlakeContextImpl (`NoSuchMethodException` swallowed) |
| Edits only in `WestlakeActivityThread.java` OR `NoiceProductionLauncher.java` | OK — single edit in WAT |
| No memory file edits | OK |
| Activity/Application/services/View/Resources untouched | OK |

---

## §4. On-device validation

### §4.1 Phone-state blocker

Validation runs (`scripts/run-noice-westlake.sh --production` on
cfb7c9e3) hit the **kernel-level vndbinder context-manager EPERM** issue
that M7-Step2's report flagged as a phone-state caveat:

```
ProcessState: android_errorWriteLog(534e4554, 121035042)
ProcessState: Binder ioctl to become context manager failed: Operation not permitted
F ?: Could not become context manager
```

After Android's `vndservicemanager` claims the /dev/vndbinder context
manager at boot, the kernel locks the slot — even `setprop ctl.stop
vndservicemanager` does not release it for the script's
`bin-bionic/servicemanager` to claim, in this particular device state.
This is **independent of CR56's shim change** and was already documented
in `docs/engine/M7_STEP2_REPORT.md` §4.4 / row in `PHASE_1_STATUS.md`:

> Phone-state caveat: vndservicemanager occasionally enters a context-mgr-claim
> degraded state across runs requiring manual `setprop ctl.start
> vndservicemanager` + sm_smoke verify before re-running; out of scope for
> M7-Step2 (also affects M7-Step1 baseline).

Mitigations attempted:
- `pkill -9 -f westlake/bin-bionic`
- `setprop ctl.stop vndservicemanager` + 10s wait
- `setprop ctl.restart vndservicemanager` + 5s wait
- Repeated test re-runs (4 attempts)

None recovered context-manager access for the OUR `servicemanager`
binary. Restoring this typically requires a phone reboot (out of scope
for an in-session CR); the M7-Step2 brief flagged this as the recurring
fragility (CR-class follow-up tracked separately as the
"`vndservicemanager` recovery hardening" item).

### §4.2 What the runs DID confirm

- Code build clean: `bash scripts/build-shim-dex.sh` exits 0; aosp-shim.dex
  1,453,196 bytes (delta vs M7-Step2's 1.45 MB ≈ same — additive only).
- Launcher dex unchanged (`NoiceProductionLauncher.dex` 49,388 bytes
  identical to M7-Step2 — we did not edit the launcher per option-A choice).
- `M7_PROD_LAUNCHER:` startup markers reached `Looper.prepareMainLooper()`
  on every run before vndbinder blocked further progress in
  ServiceRegistrar.

### §4.3 Code-path correctness review

Re-reading the relevant code paths confirms the fix is structurally sound:

1. **NPE chain re-verified.** Hilt's `dagger.hilt.android.internal.Contexts
   .getApplication(Context)` walks the receiver's
   `ContextWrapper.getApplicationContext()` chain. On the Activity, that
   delegates to `Activity.mBase.getApplicationContext()`. Activity.mBase
   is the `baseContext` passed to `Activity.attach(...)` from
   `attachActivity(activity, baseContext, ...)` at WAT line 1570.

2. **baseContext identity confirmed.** At performLaunchActivityImpl
   line 1338, `Context baseContext = mInitialApplication;` — but at
   the strict-standalone entry, `mInitialApplication` is null (attachShared
   exited before makeApplication). The very next line 1339-1341 falls
   through to `baseContext = buildBaseContext(packageName, cl);` which
   constructs a new `WestlakeContextImpl`. That WestlakeContextImpl's
   `mAttachedApplication` field is `null` after construction.

3. **The fix lands.** Once `app` is built by `makeApplicationForLaunch`
   and assigned to `mInitialApplication` (line 1534), CR56 calls
   `publishApplicationToBaseContext(baseContext, app)` — which invokes
   the M4-PRE11 `setAttachedApplication` reflective entry that walks
   `baseContext.getClass().getMethod("setAttachedApplication",
   Application.class)`. WestlakeContextImpl.setAttachedApplication(app)
   then assigns `mAttachedApplication = app`.

4. **Future getApplicationContext() returns app.** Per
   WestlakeContextImpl.java:221-233, `getApplicationContext()` first
   reads `mAttachedApplication` and returns it if non-null. After CR56,
   it is non-null. The Activity's `ContextWrapper.getApplicationContext()`
   delegation to mBase therefore returns the Application — exactly what
   Hilt's walk expects.

The fix is one call to existing M4-PRE11 plumbing; no new wiring, no new
state.

---

## §5. Expected new blocker post-CR56

The NPE chain from the M7-Step2 breakthrough was:

```
#0 ContextWrapper.getApplicationContext()
#1 Hilt Contexts.getApplication(Context, Class)
#2 MainActivity$settingsRepository$2.b()              ← Kotlin Lazy delegate
#3 SynchronizedLazyImpl.getValue()
#4 MainActivity.onCreate(Bundle)
```

Once frame #0 returns the Application cleanly, frame #1 (Hilt's
`getApplication`) returns it as a `Application` cast. The next deeper
step is the Hilt-generated `EntryPointAccessors.fromApplication(app,
ClassThatProvidesSettingsRepository.class)` invocation. That walks the
app's Hilt `SingletonComponent`. The Hilt SingletonComponent is set on
the application via `dagger.hilt.android.internal.managers.ApplicationComponentManager
.singletonComponent` (WAT.attachShared already touches this at line 4553).

If that field is populated by the time MainActivity.onCreate fires, Hilt
returns the singleton and `EntryPointAccessors.fromApplication` resolves
the entry point. Otherwise the next blocker will be either:

- `dagger.hilt.android.internal.managers.ApplicationComponentManager` returning
  null for `componentManager()` — would surface as a deeper NPE inside Hilt's
  generated `MainActivity_GeneratedInjector` accessor.
- A NoClassDefFoundError on the user's Hilt-generated component class if
  the APK's classes.dex doesn't fully resolve the dependency graph.
- A real settings repository call exercising more surface (SharedPreferences,
  Context.getString, etc.).

All three are tractable per CR40 (`docs/engine/CR40_APK_API_SURFACE.md`)
Tier-1 hot-path analysis. CR56's deliverable is restricted to **unblocking
this specific frame-#0 NPE**; the next blocker is the M7-Step3 / CR57 candidate.

---

## §6. Person-time and budget

- Investigation + code edit: 35 min
- Build + push + 4 test-run attempts: 25 min (test runs each timed out at
  180s due to vndbinder EPERM; full set ~12 min)
- Report draft + PHASE_1_STATUS row: 15 min
- **Total: ~75 min** (inside 30-60 min budget by 15 min, due to phone-state
  workaround attempts)

---

## §7. Next steps

| Item | Owner | Note |
|---|---|---|
| Phone reboot + clean retest of `--production` path | next agent | One-shot validation that frame-#0 NPE is gone; can be folded into CR57's first run |
| CR57: identify next deeper blocker | next Builder | Read `m7-dalvikvm.log` from first clean post-CR56 run; the next NPE/UOE will be the next CR target |
| `vndservicemanager` recovery hardening | platform CR | Track separately; document the kernel state machine that locks /dev/vndbinder context-manager |
