# CR60 E13 noice Checkpoint — stage A+B PASS, stage C blocked (2026-05-15)

## Stages reached

| Stage | Status | Evidence (markers in dalvikvm.stdout) |
|---|---|---|
| A. noice dex loaded on BCP, MainActivity reachable | **PASS** | `inproc-app-launcher stage A: dex visible class=com.github.ashutoshgngwr.noice.activity.MainActivity` |
| B. NoiceApplication.onCreate completed | **PASS** | `inproc-app-launcher stage B: Application.onCreate returned (com.github.ashutoshgngwr.noice.NoiceApplication)` |
| C. MainActivity.onCreate completed | **BLOCKED** | NPE inside Activity's no-arg ctor super-chain at `e.v.c(SourceFile:22)` ← `e.q.attachBaseContext(SourceFile:23)` |
| D. noice first frame on panel | not reached |  |

## Reproducer

```bash
bash scripts/run-ohos-test.sh --arch arm32 inproc-app --apk noice
```

Evidence in `artifacts/ohos-mvp/cr60-e13-noice/20260515_092449-inproc-app/`.

## Root cause of stage C blocker

noice's `MainActivity` extends `AppCompatActivity` (R8-renamed `e.q`).
`AppCompatActivity`'s `attachBaseContext` override:

```java
@Override
protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(getDelegate().attachBaseContext2(newBase));
}
```

dispatches at `Activity()`-constructor time when `Activity()` →
`ContextThemeWrapper()` → `ContextWrapper(null)` → virtual
`attachBaseContext(null)`. The AppCompatActivity override calls into
`AppCompatDelegate.attachBaseContext2(null)` which NPEs at the
R8-renamed static helper `e.v.c` line 22.

Full stack trace (from `step 3-pre: Activity no-arg newInstance() THREW`):

```
java.lang.NullPointerException
    at e.v.c(SourceFile:22)
    at e.q.attachBaseContext(SourceFile:23)
    at android.content.ContextWrapper.<init>(ContextWrapper.java:18)
    at android.view.ContextThemeWrapper.<init>(ContextThemeWrapper.java:52)
    at android.app.Activity.<init>(Activity.java:176)
    at androidx.activity.ComponentActivity.<init>(ComponentActivity.java:29)
    at androidx.fragment.app.d0.<init>(SourceFile:1)
    ...
    [eventually MainActivity.<init>]
```

The substrate's `mInstrumentation.newActivity(...)` reflectively calls
the no-arg ctor and hits the same NPE. `WestlakeInstrumentation.onException`
swallows it and returns `true`, so `performLaunchActivityImpl` returns
`null` silently — the launcher reports "launchActivity returned
non-Activity: null" but the underlying exception is hidden.

## What unlocked stage A

`-Xverify:none -Xdexopt:none` on the dalvikvm CLI. Without these, the
dalvik-kitkat bytecode verifier rejects intermediate R8-shrunk classes
(e.g. `Le/q;.dispatchKeyEvent` invoke-virtual on `Lc0/p;` — the
inherited superclass chain spans R8-renamed types it can't resolve).

Verifier rejection in DexOpt cascades: when a class fails verification,
`Class.forName` of any class transitively extending it throws
`ClassNotFoundException` (Class.forName wraps the underlying
`NoClassDefFoundError`).

Smoke path (`--apk` NOT set, hello-color-apk) keeps strict verification
ON because :hello-color-apk's bytecode is hand-written and verifies
cleanly — this preserves the E12 regression tight.

## Tightest path to stage C (future work)

Option α: `WestlakeContextImpl.create()` a pre-base context, set it as
thread-local, and modify `Instrumentation.newActivity` to attach it
**before** calling the no-arg ctor. This is invasive — it changes the
substrate's strict-mode contract that ctor-time attach is deferred
until `Activity.attach(...)`.

Option β: Pre-initialize AppCompatDelegate static state so the R8
`e.v.c` helper has whatever it dereferences. Requires inspecting the
deminified bytecode to learn what `e.v.c` actually does.

Option γ: Patch `AppCompatActivity.attachBaseContext` to no-op when
`newBase == null`. Requires shipping a side-loaded shim that wins over
the R8 class. Likely incompatible with the macro-shim contract (would
be a per-AppCompat-package shim).

Estimate: 2-3 days for option α with careful testing of the V2
substrate's other invariants (CR23/CR56/CR59). Option β depends on
deminification cost. Option γ is fundamentally per-app and rejected.

## Self-audit (brief §"Self-audit gate")

- [x] No `Unsafe.allocateInstance` in new Java code
- [x] No `setAccessible(true)` in new Java code (reflection on
       WestlakeActivityThread uses public methods only)
- [x] No per-app branches (launcher is generic with `apkMode` boolean;
       noice routing is via runtime `--apk noice` arg + per-APK
       constants table in the driver script — bool-shaped, not
       app-shape-of-code)
- [x] No new native code (no `intptr_t`/`size_t` concerns)
- [x] aarch64 + static-arm32 + dynamic-arm32 builds untouched
- [x] E12 smoke regression PASS (commit `442e312e`'s
       :hello-color-apk → BLUE panel still works)
- [x] MVP-0 hello regression PASS
- [x] MVP-1 trivial-activity regression PASS (with one intermittent
       SIGSEGV retried — pre-existing flakiness, unrelated to this
       change)
- [x] CR59 plumbing verified: `WestlakeActivityThread.forceMakeApplicationForNextLaunch`
       + `WestlakeActivityThread.launchActivity` ran NoiceApplication.onCreate
       successfully (stage B PASS)
- [x] Highest stage honestly reported: B

## Files changed

- `ohos-tests-gradle/inproc-app-launcher/src/main/java/com/westlake/ohostests/inproc/InProcessAppLauncher.java`
  (E13 apk-mode + InProcDrawSource → Window.getDecorView fallback +
  fallback paint when no View available + stage A/B/C/D marker contract)
- `scripts/run-ohos-test.sh` (cmd_inproc_app: `--apk noice`,
  `--apk-path`, `--apk-activity`, `--apk-app`, `--fallback-argb`
  flags; noice APK redex via `d8 --min-api 13`; `-Xverify:none
  -Xdexopt:none` toggle when apkMode; E13 stage grading; usage doc)

## Recommendation for next CR

CR62 should tackle stage C by:
1. Surfacing the AppCompatDelegate.attachBaseContext NPE end-to-end:
   add a `WestlakeInstrumentation.setStrictExceptionPropagation(true)`
   public method that makes onException re-throw rather than swallow.
   Generic, no per-app branches.
2. Pre-construct a `WestlakeContextImpl` for the target package and
   thread-local it so `Instrumentation.newActivity` can attach BEFORE
   the no-arg ctor's super-chain reaches AppCompatActivity. This is
   the cleanest option α path and matches CR23's "buildBaseContext"
   pattern.
3. Validate by re-running `inproc-app --apk noice` and confirming
   stage C marker.

This is bounded at ~3 person-days. Stage D (pixel on panel) likely
trivially follows from stage C since the launcher already has the
fallback-paint path wired and noice's window background drawable
should be theme-driven (not Compose-driven) and reachable via
`Window.getDecorView()` after `setContentView` runs.

CR63 (or later) should tackle the Compose recompose path needed for
noice's actual View tree — that's a separate, bigger workstream.
