# CR36 — Window.getCallback() NPE root cause + fix

**Status:** RESOLVED (Window.getCallback NPE eliminated; both apps advance to a new deeper blocker)
**Date:** 2026-05-13
**Predecessor CRs:** CR31-A (Window.Callback stub installed), CR32 (audit confirming identical NPE on both apps)
**Successor blocker:** "null array" NPE in MainActivity.onCreate path (different defect; out of scope)

---

## Problem (recap)

Both noice and McD failed PHASE G4 (MainActivity.onCreate) with an identical NPE:

```
NullPointerException: Attempt to invoke InvokeType(2) method
'android.view.Window$Callback android.view.Window.getCallback()'
on a null object reference
```

CR31-A had installed a no-op `Window.Callback` on `mWindow` inside
`Activity.attachInternal`. The expectation was that AppCompat
(`AppCompatDelegateImpl.attachToWindow` → `window.getCallback()`) would see
a non-null callback and continue. Instead the NPE persisted on both apps —
hence CR36's diagnostic mission.

## Hypothesis triage (the brief)

The brief enumerated four hypotheses:

- H1: setCallback ran but write wasn't visible at the read
- H2: AppCompat read a *different* Window instance than the one we set up
- H3: shim Window's mCallback field/visibility mismatch with the runtime read
- H4: race between attachInternal and AppCompat read

## Diagnostic step (Step 1 of brief)

Added `WLK-CR36` Log.i probes to two sites:

1. `Activity.attachInternal()` — immediately after the `setCallback(new Callback() {...})` block, prints `mWindow`, `mWindow.getClass()`, and `mWindow.getCallback()`.
2. `Activity.getWindow()` — prints the same triple every time AppCompat / app code reads the Window.

Built `aosp-shim.dex`, pushed, and re-ran `noice-discover.sh`. The probe output (single noice run):

```
WLK-CR36: Activity.attachInternal: this=MainActivity@35b5cec
    mWindow=null mWindow.class=<null> mWindow.getCallback=<null window>
...
WLK-CR36: Activity.getWindow: this=MainActivity@35b5cec
    mWindow=null mWindow.class=<null> mWindow.getCallback=<null>
```

`mWindow` was **null** at the END of `attachInternal` — meaning the
`new PhoneWindow(this)` allocation in lines 230-234 of `Activity.java` was
throwing and the `catch (Throwable t)` clause was silently swallowing the
exception while leaving `mWindow = null`. CR31-A's `setCallback` block
was guarded by `if (mWindow != null && ...)` so it never fired.

The NPE that crashed AppCompat was therefore NOT "getCallback() returned null".
The receiver itself (`mWindow`) was null. AppCompat read `null` from
`Activity.getWindow()` and the `invoke-virtual` on a null receiver is what
the dalvikvm interpreter prints as "Attempt to invoke InvokeType(2) ...
getCallback() ... on a null object reference".

**Hypothesis confirmed:** none of H2/H3/H4. The shim's own `mWindow` field
was null. Closest to H1, but the deeper cause is that PhoneWindow's
constructor was throwing.

## Root cause

Replacing the silent `catch (Throwable t) { mWindow = null; }` with a
`Log.w(tag, msg, throwable)` exposed the underlying error on the next run:

```
WLK-CR36: PhoneWindow(this) ctor threw -- mWindow stays null
java.lang.UnsatisfiedLinkError: No implementation found for long
android.graphics.RenderNode.nCreate(java.lang.String) (tried
Java_android_graphics_RenderNode_nCreate and
Java_android_graphics_RenderNode_nCreate__Ljava_lang_String_2) -
is the library loaded, e.g. System.loadLibrary?
```

Chain:

1. `attachInternal` calls `new PhoneWindow(this)`
2. `PhoneWindow(Context)` → `super(context)` → our `Window(Context)` ctor
3. Pre-fix `Window` ctor: `mDecorView = new android.widget.FrameLayout(context);`
4. `FrameLayout.<init>` chains through `ViewGroup` to `View(Context)`
5. `View(Context)` eventually constructs an `android.graphics.RenderNode`
6. `RenderNode.<clinit>` / `RenderNode.<init>` calls the native
   `RenderNode.nCreate(String)` JNI entry
7. Our standalone dalvikvm substrate hasn't loaded `libhwui.so` /
   `libandroid_runtime.so` (and we don't intend to — we run no real
   compositor in M3/M4 phases), so the JNI resolution fails with
   `UnsatisfiedLinkError`

The error bubbles up through `View → FrameLayout → Window` and out to
`Activity.attachInternal`'s try/catch — which silently swallowed it. The
no-op Window.Callback was never installed because the guard
`if (mWindow != null && ...)` saw mWindow still null.

## Fix

Make `Window.<init>(Context)` not depend on constructing a real View
subclass. Defer the decor view until `setContentView` runs (which already
handles null/failed-FrameLayout gracefully via its own try/catch).

`shim/java/android/view/Window.java` (V2-Step5 site):

```java
public Window(Context context) {
    mContext = context;
    // CR36: don't construct a FrameLayout here. android.view.View ->
    // RenderNode -> RenderNode.nCreate native, which isn't loaded in
    // our standalone dalvikvm substrate. Defer decor construction to
    // setContentView (which already handles failure via try/catch).
    mDecorView = null;
}
```

All other consumers of `mDecorView` in the shim Window already null-check
(`if (mDecorView != null) ...`) or use `instanceof ViewGroup` (null-safe),
so no callsite cleanup was required.

Also kept a single `Log.w` in `Activity.attachInternal`'s PhoneWindow ctor
catch — cheap defensive logging so if a future substrate change re-breaks
PhoneWindow construction we see it immediately instead of debugging
through a null-receiver NPE four layers downstream.

## Verification

### Diagnostic logs after fix

```
WLK-CR36: Activity.attachInternal: this=MainActivity@8e5a3b
    mWindow=PhoneWindow@fed4722 mWindow.class=com.android.internal.policy.PhoneWindow
    mWindow.getCallback=android.app.Activity$1@9ffd5b3
WLK-CR36: Activity.getWindow: this=MainActivity@8e5a3b
    mWindow=PhoneWindow@fed4722 mWindow.class=com.android.internal.policy.PhoneWindow
    mWindow.getCallback=android.app.Activity$1@9ffd5b3
```

`mWindow` non-null. Callback non-null. Identical on McD.

After verification the diagnostic logs were removed (Step 5 of the brief);
the `Log.w` on PhoneWindow ctor failure is retained as a defensive permanent.

### Regression

```
$ scripts/binder-pivot-regression.sh
[ 1] sm_smoke / sandbox (M1+M2)                   PASS
[ 2] HelloBinder (M3)                             PASS
[ 3] AsInterfaceTest (M3++)                       PASS
[ 4] BCP-shim (M3+)                               PASS
[ 5] BCP-framework (M3+ / PF-arch-053)            PASS
[ 6] ActivityServiceTest (M4a)                    PASS
[ 7] PowerServiceTest (M4-power)                  PASS
[ 8] SystemServiceRouteTest (CR3)                 PASS
[ 9] DisplayServiceTest (M4d)                     PASS
[10] NotificationServiceTest (M4e)                PASS
[11] InputMethodServiceTest (M4e)                 PASS
[12] WindowServiceTest (M4b)                      PASS
[13] PackageServiceTest (M4c)                     PASS
[14] noice-discover (W2/M4-PRE)                   PASS
Results: 14 PASS  0 FAIL  0 SKIP
```

### App discovery transcripts

noice (`aosp-libbinder-port/test/cr36-noice-after.log`):

```
PHASE G3: PASSED -- Activity.attach succeeded
PHASE G4: FAILED (expected -- diagnoses needed service)
DISCOVER-FAIL: PHASE G4: MainActivity.onCreate(null) threw ...
  cause[1]: java.lang.NullPointerException: Attempt to read from null array
```

McD (`aosp-libbinder-port/test/cr36-mcd-after.log`):

```
PHASE G3: PASSED -- Activity.attach succeeded
PHASE G4: FAILED (expected -- diagnoses needed service)
DISCOVER-FAIL: PHASE G4: MainActivity.onCreate(null) threw ...
  cause[1]: java.lang.NullPointerException: Attempt to read from null array
```

Both apps cleared the Window.getCallback() boundary and now block on a
**new, deeper, different defect** — a "null array" NPE somewhere inside the
AppCompat / Fragment onCreate path. Out of scope for CR36; will be CR37+.

## Self-audit gate

```bash
$ grep -rnE "sun.misc.Unsafe|jdk.internal.misc.Unsafe|Unsafe.allocateInstance" \
    shim/java/android/app/Activity.java shim/java/android/view/Window.java
# (no matches)
$ grep -rn "setAccessible(true)" \
    shim/java/android/app/Activity.java shim/java/android/view/Window.java
# (no matches)
$ grep -rniE "noice|mcdonalds|com\.mcd|noice\.fragment" \
    shim/java/android/app/Activity.java shim/java/android/view/Window.java
# (no matches)
```

**PASS** — zero new Unsafe / setAccessible / per-app branches introduced.
The single `catch` clause widened from `Throwable` to `Throwable+log` is
on the PhoneWindow ctor in shim Activity (our own class), not on framework
class reflection — within contract bucket (b) "safe primitive" with
diagnostic-only logging.

## Files changed

- `shim/java/android/view/Window.java` — defer mDecorView in ctor (the actual fix)
- `shim/java/android/app/Activity.java` — `Log.w` on PhoneWindow ctor throwable (defensive permanent)
- `aosp-shim.dex` — rebuilt
- `aosp-libbinder-port/test/cr36-noice-after.log` — new transcript
- `aosp-libbinder-port/test/cr36-mcd-after.log` — new transcript
- `docs/engine/CR36_FINDINGS.md` — this document
- `docs/engine/PHASE_1_STATUS.md` — CR36 row updated
