# CR59 — Fix `Application.mBase=null` so MainActivity.onCreate clears past Hilt's lazy delegate

**Status:** CODE LANDED + ON-DEVICE PASS (cfb7c9e3). MainActivity.onCreate now completes WITHOUT NPE.
**Date:** 2026-05-14
**Owner:** agent10 (Builder)
**Companion to:**
- `docs/engine/CR56_REPORT.md` (CR56 wired `WestlakeContextImpl.setAttachedApplication` on the Activity's base context — necessary but not sufficient)
- `docs/engine/M7_STEP2_REPORT.md` §4 (documented the in-body Hilt NPE this CR fixes)
- `memory/feedback_macro_shim_contract.md` (anti-drift contract)

---

## §1. The actual root cause (took 30+ min to find)

CR56's premise was right at the Activity level but missed two upstream
contributors. The NPE stack trace was misleading:

```
[NPE] android.content.Context android.content.Context.getApplicationContext()
[NPE]   #0 android.content.Context android.content.ContextWrapper.getApplicationContext() (dex_pc=2)
[NPE]   #1 java.lang.Object f3.y.l(android.content.Context, java.lang.Class) (dex_pc=5)
[NPE]   #2 java.lang.Object com.github.ashutoshgngwr.noice.activity.MainActivity$settingsRepository$2.b() (dex_pc=13)
[NPE]   #3 java.lang.Object kotlin.SynchronizedLazyImpl.getValue() (dex_pc=20)
[NPE]   #4 void com.github.ashutoshgngwr.noice.activity.MainActivity.onCreate(android.os.Bundle) (dex_pc=2)
```

I initially read this as "Activity's mBase has null mAttachedApplication"
(CR56's theory). Then disassembled the noice APK at `/tmp/cr40-noice/decoded/`
and looked at `f3.y.l` (smali):

```
.method public static final l(Landroid/content/Context;Ljava/lang/Class;)Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/content/Context;->getApplicationContext()
    ...
```

And `MainActivity$settingsRepository$2.b()`:

```
.method public final b()Ljava/lang/Object;
    iget-object v0, p0, ...->n:Lcom/.../MainActivity;
    invoke-virtual {v0}, Landroid/app/Activity;->getApplication()Landroid/app/Application;   ← step A
    ...
    invoke-static {v0, v1}, Lf3/y;->l(Landroid/content/Context;Ljava/lang/Class;)            ← step B
```

So the chain is:
1. `b()` calls `mainActivity.getApplication()` → returns the Application
2. `b()` calls `f3.y.l(app, ...)` with the **Application** as the Context
3. `f3.y.l` calls `app.getApplicationContext()`
4. Framework `ContextWrapper.getApplicationContext()` is dispatched: `return mBase.getApplicationContext();`
5. **`app.mBase` is null** → NPE at dex_pc=2 (the `invoke-virtual` on null)

The receiver is the Application — NOT the Activity. CR56's fix
(`WestlakeContextImpl.setAttachedApplication` on the Activity's base
context) is unreachable because Hilt never walks the Activity's
context chain on this call site — it walks the **Application's** mBase
chain.

### §1.1 Why the Application's mBase was null

Tracing `WAT.performLaunchActivityImpl` for the M7-Step2 strict-standalone
path:

1. `attachStandalone` returns early in strict mode without populating
   `mInitialApplication` (WAT line 4485-4489).
2. `performLaunchActivityImpl` reads `mInitialApplication` = null at
   line 1315.
3. **Step 3** (line 1338-1340): `baseContext = buildBaseContext(...)`
   → fresh WestlakeContextImpl.
4. **Step 5** (line 1500-1518): tries
   `app = MiniServer.get().getApplication();` first.
5. **`MiniServer.get()` auto-inits** with default package "com.example.app"
   (MiniServer.java:58-63) when sInstance is null — which it always is on
   the production launcher path. That auto-init creates a placeholder
   `mApplication = new Application();` in MiniServer's constructor
   (MiniServer.java:35). The shim's `Application()` constructor calls
   `super(null)` which sets the framework `ContextWrapper.mBase = null`.
6. The placeholder Application is returned to `WAT.performLaunchActivityImpl`,
   `app != null`, so the `if (app == null) makeApplicationForLaunch(...)`
   branch at line 1526 is **skipped**.
7. The placeholder bare-Application becomes the Activity's `mApplication`
   via `attachActivity` → `Activity.attach` → `mApplication = application;`.
8. When MainActivity.onCreate's lazy fires, `getApplication()` returns
   this bare Application with mBase=null. NPE.

### §1.2 Why CR56 + CR58 didn't catch this

- CR56 wired `setAttachedApplication` on the Activity's mBase. That
  WestlakeContextImpl is fine. The NPE site doesn't walk through it.
- CR58 force-set the Activity's mBase to the WestlakeContextImpl (defeating
  Hilt's AppCompat-i.f wrap). Again, doesn't affect the Application's mBase.

Both were necessary plumbing but neither addressed the Application's mBase
being null in the first place.

---

## §2. What landed

### §2.1 Files touched

| File | Δ | Note |
|---|---:|---|
| `shim/java/android/app/MiniServer.java` | +46 / -2 | ctor + 2 setters: attach a default WestlakeContextImpl to the placeholder Application so `mBase != null` — generic, no per-app |
| `shim/java/android/app/WestlakeActivityThread.java` | +33 / -16 | (a) `attachApplicationBaseContext`: use shim Application's existing package-private `attach(Context)` helper (cross-package protected-method invocation was silently failing); (b) `performLaunchActivityImpl`: detect the MiniServer auto-init placeholder Application by exact class name and discard it so the `makeApplication` fallback runs |
| `aosp-shim.dex` | rebuilt | 1,454,232 bytes (delta +812 vs CR56) |
| `docs/engine/CR59_REPORT.md` | NEW | this file |

### §2.2 The two distinct fixes

**Fix A — `attachApplicationBaseContext` plumbing bug:**

Pre-CR59, `WAT.PackageInfo.attachApplicationBaseContext` called
`app.attachBaseContext(baseContext)` directly. `attachBaseContext` is
**protected** on framework's `android.content.ContextWrapper`. WAT lives
in `android.app`; ContextWrapper lives in `android.content`. Java access
control: cross-package call to a protected method from a non-subclass is
rejected. The reflective fallback used `getDeclaredMethod("attachBaseContext",
Context.class)` which doesn't find inherited methods — so the fallback
*also* silently failed. Net result: app.mBase stayed null.

The shim's `android.app.Application` already has a package-private
`attach(Context)` helper (Application.java:289-291, matches AOSP's verbatim:
calls `attachBaseContext(base)` from a class that IS a ContextWrapper
subclass — legal). WAT lives in `android.app`, same package as Application,
so it can call this helper.

```java
// before (CR59) — silently failed cross-package protected call
app.attachBaseContext(baseContext);

// after CR59 — uses existing package-private wrapper
app.attach(baseContext);
```

**Fix B — MiniServer auto-init placeholder Application discard:**

Even with Fix A, `WAT.performLaunchActivityImpl` line 1516 was returning
a `MiniServer.get().getApplication()` placeholder before the real app's
Application was instantiated. The placeholder is a bare
`android.app.Application` (no user subclass, wrong package). Even after
Fix A makes its mBase walkable, it has the wrong class and wrong package
to satisfy Hilt's downstream component lookup.

The fix is to detect the placeholder by exact class name and null it out
so the next branch (`makeApplicationForLaunch`) instantiates the real
user Application via the AppComponentFactory + APK classloader path:

```java
if (app != null && "android.app.Application".equals(app.getClass().getName())) {
    app = null;  // discard placeholder; let makeApplicationForLaunch run
}
```

Generic: no per-app branches, identified solely by the exact framework
class name of the bare-bones placeholder.

**Fix C — MiniServer ctor pre-CR59 ctor fixup (defensive):**

Also: ensure the MiniServer placeholder Application — for any caller
that DOES use it (e.g. test paths) — has a real mBase via the same
`app.attach(...)` route. Construct a WestlakeContextImpl in the
ctor, set its `mAttachedApplication`, then call `app.attach(baseContext)`.
This makes the placeholder defensively usable when other paths return it.

---

## §3. Anti-drift compliance (self-audit)

Per `memory/feedback_macro_shim_contract.md`:

| Constraint | Status | Note |
|---|---|---|
| ZERO new `Unsafe.allocateInstance` | PASS | grep across shim/ matches CR59 lines only inside comments |
| ZERO new `Field.setAccessible(true)` | PASS | grep across shim/ matches CR59 lines only inside comments. The pre-CR56 reflective fallback in attachApplicationBaseContext that used setAccessible was REMOVED by CR59 — net delta is **-1** setAccessible call |
| ZERO new methods on `WestlakeContextImpl` | PASS | WestlakeContextImpl.java untouched (mtime 2026-05-13 15:00, unchanged) |
| ZERO per-app branches | PASS | All checks are class-name agnostic. The bare-Application class name is `android.app.Application` for any app |
| Existing-method bodies only | PASS | `attach(Context)` already existed on shim Application; `setAttachedApplication` already existed on WestlakeContextImpl (CR56); MiniServer ctor and setters are existing methods, bodies augmented |
| Fail-loud where appropriate | PASS | Both fix sites use marker logging on success and failure paths |

Pre-CR59 setAccessible removal count: -1 (the broken reflective fallback
in `attachApplicationBaseContext`). Net effect on contract surface:
**strictly fewer reflection escape hatches** than before, not more.

---

## §4. Evidence

### §4.1 Before — M7-Step2 baseline NPE (artifact 20260514_160506)

```
M7_PROD_LAUNCHER: PRODUCTION_LAUNCH_BEGIN method=performLaunchActivity class=...MainActivity
[WLK-CR58] attachBaseContext ok=true base=com.westlake.services.WestlakeContextImpl mBase_after=i.f
[WLK-CR58] ContextWrapper.mBase forced from i.f -> com.westlake.services.WestlakeContextImpl
[Log/4] WestlakeStep: performLaunchActivity record stored ...MainActivity
[Log/4] WestlakeStep: performLaunchActivity onCreate begin ...MainActivity
[NPE] android.content.Context android.content.Context.getApplicationContext()
[NPE]   #0 android.content.Context android.content.ContextWrapper.getApplicationContext() (dex_pc=2)
[NPE]   #1 java.lang.Object f3.y.l(android.content.Context, java.lang.Class) (dex_pc=5)
[NPE]   #2 java.lang.Object com.github.ashutoshgngwr.noice.activity.MainActivity$settingsRepository$2.b() (dex_pc=13)
[NPE]   #3 java.lang.Object kotlin.SynchronizedLazyImpl.getValue() (dex_pc=20)
[NPE]   #4 void com.github.ashutoshgngwr.noice.activity.MainActivity.onCreate(android.os.Bundle) (dex_pc=2)
[NPE]   #5 void android.app.WestlakeInstrumentation.callActivityOnCreate(android.app.Activity, android.os.Bundle) (dex_pc=184)
[NPE]   #6 android.app.Activity android.app.WestlakeActivityThread.performLaunchActivityImpl(...) (dex_pc=2170)
[NPE]   #7 android.app.Activity android.app.WestlakeActivityThread.performLaunchActivity(...) (dex_pc=0)
[Log/4] WestlakeStep: performLaunchActivity onCreate done ...MainActivity
```

NPE stack 8 frames deep, originates inside the user body's Kotlin Lazy.

### §4.2 After — CR59 (artifact 20260514_162935)

```
M7_PROD_LAUNCHER: PRODUCTION_LAUNCH_BEGIN method=performLaunchActivity class=...MainActivity
[Log/4] WestlakeStep: performLaunchActivity record stored ...MainActivity
[Log/4] WestlakeStep: performLaunchActivity onCreate begin ...MainActivity
[Log/4] WestlakeStep: performLaunchActivity onCreate done ...MainActivity
M7_PROD_LAUNCHER: PRODUCTION_LAUNCH_RETURNED activity=...MainActivity@66915189
M7_PROD_LAUNCHER: PRODUCTION_LAUNCH_OK class=...MainActivity
```

**Zero `[NPE]` frames in the entire dalvikvm log.** Only pre-existing
`[NPE-SYNC]` (Charset bootstrap, well-documented in handoff_2026-05-14.md
as a pre-pivot artifact).

McD parallel — artifact `20260514_163555_m8step2`:

```
[Log/4] WestlakeStep: performLaunchActivity onCreate begin ...SplashActivity
[MCD-CALL] ... SplashActivity.onCreate -> Hilt_SplashActivity.onCreate ...
[Log/4] WestlakeStep: performLaunchActivity onCreate done ...SplashActivity
```

Also: zero `[NPE]` frames. SIG2 (SplashActivity.onCreate exit) flipped
from FAIL pre-CR59 to **PASS** post-CR59.

### §4.3 Regression suite

```bash
$ scripts/binder-pivot-regression.sh --full
Results: 14 PASS  0 FAIL  0 SKIP  (total 14, 81s)
REGRESSION SUITE: ALL PASS
```

Both noice and McD V2-substrate production paths advance past the Hilt
in-body NPE. McD picked up an additional acceptance signal (SIG2) for
free.

---

## §5. The "after" downstream state

Both noice and McD now complete `MainActivity.onCreate(Bundle)` /
`SplashActivity.onCreate(Bundle)` cleanly. The remaining failed signals
(noice S2 HomeFragment, McD SIG1 McDMarketApplication.onCreate
completion) are **lifecycle-progression gaps**, not in-body NPEs:

- **noice S2** — fails because the launcher only calls `performLaunchActivity`
  (CREATED) and does NOT drive START/RESUME, so Fragment lifecycle never
  fires. Adding `launchAndResumeActivity` to the launcher (via existing
  --resume flag) is M7-Step3 / CR-next scope.
- **McD SIG1** — fails because `WAT.currentApplication` reports null
  (the makeApplication runs with `forcedAppClassName=null`, so the bare
  Application is built instead of `McDMarketApplication`). Fixing
  this means the launcher should call
  `WAT.forceMakeApplicationForNextLaunch(appCls)` before
  `performLaunchActivity` to inject the correct user-class name. This
  is launcher-side work (M8-Step3) — purely a parameter-plumbing fix
  through an existing WAT API.

Both follow-ups are tractable per the established
"one-method-on-a-class-we-own" pattern.

---

## §6. Person-time

- Reading CR56/M7-Step2/MEMORY/handoff + shim source: 25 min
- Disassembling noice APK + finding the actual NPE site (f3.y.l + lazy chain): 25 min
- First (incorrect) fix attempt — attachApplicationBaseContext fix alone — and verifying no progress: 15 min
- Root-cause analysis: MiniServer auto-init placeholder Application + bare-class detection: 20 min
- Implementing Fix A + Fix B + Fix C + rebuild + push: 15 min
- Verification + McD parallel test + regression: 20 min
- Report: 30 min
- **Total: ~150 min** (2.5h, inside the 4h budget by 90 min)

---

## §7. Next steps

| Item | Owner | Note |
|---|---|---|
| Launcher: drive START/RESUME via `launchAndResumeActivity` (--resume flag) | next agent | noice S2 unlock; sibling McD path automatic |
| Launcher: call `forceMakeApplicationForNextLaunch(appCls)` before performLaunchActivity | next agent | McD SIG1 unlock; ensures user's Application class is instantiated |
| Promote CR59 self-audit pattern to the brief template | meta | The macro-shim contract gate caught zero violations in CR59 — the pattern works |
| Verify CR58's `[WLK-CR58]` markers remain valuable now that mBase wiring is correct upstream | next agent | CR58 may be defensible-but-removable now; keep until M7-Step3 says it's noise |
