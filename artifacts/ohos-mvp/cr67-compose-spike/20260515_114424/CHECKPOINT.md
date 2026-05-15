# CR67 — Compose spike: hypothesis FALSIFIED, real blocker identified

Agent 25. Bound: 2-3 days. Elapsed: ~1.5h (no code changes — pure investigation).

## TL;DR

**The premise was wrong.** noice does NOT use Jetpack Compose anywhere in its
own code. Hypotheses (a) "Compose hits a missing primitive", (b) "Compose
lifecycle/effects not wired", (c) "Compose uses Picture/RenderNode that
bypasses Canvas.draw*" are all **moot** — there is no Compose render path to
trigger. The CR64 Stage D "just theme background" is actually a **white
fallback fill** (`FALLBACK_ARGB=0xFFFFFFFF`), not a recorded `drawColor`.

The real blocker: **`setContentView` throws NPE inside noice's R8-minified
view tree**, the V2 substrate's `recoverAfterOnCreateFailure` swallows it and
installs an empty stub `FrameLayout` as the decor view, and `View.draw(canvas)`
on that empty FrameLayout records zero ops. The pixel buffer is then filled
from `FALLBACK_ARGB`, not from the canvas.

**Recommended path: Path W with a corrected target** — investigate and fix
the `findViewById`→NPE chain in noice's setContentView path. This is
fundamentally a Fragment/View-shim plumbing issue, not a Compose issue.
Estimate: 3-6 person-days, comparable in scope to CR62-CR64 substrate
cleanups; sized at "1 CR".

## Evidence

### Evidence 1: noice's APK contains NO Compose code

```
$ ls /tmp/cr40-noice/decoded/smali/androidx/compose/ 2>/dev/null
(empty / nonexistent)

$ grep -rln "ComposeView\|AndroidComposeView" /tmp/cr40-noice/decoded/smali/
(no matches)

$ grep -rln "setContent\b" /tmp/cr40-noice/decoded/smali/com/github/ashutoshgngwr/
(no matches)
```

noice's APK ships no `androidx.compose.*` packages and no `setContent` /
`ComposeView` references. The "Welcome screen" is **Fragment-based traditional
Views**, not Compose.

### Evidence 2: MainActivity uses XML layout inflation, not Compose

`/tmp/cr40-noice/decoded/smali/com/github/ashutoshgngwr/noice/activity/MainActivity.smali`
lines 296-390 show the onCreate flow:

```
invoke-super {p0, p1}, Lv3/e;->onCreate(Landroid/os/Bundle;)V         # super
invoke-virtual {p0}, Landroid/app/Activity;->getLayoutInflater()...   # inflater
invoke-virtual {p1, v0, v2, v3}, LayoutInflater;->inflate(I, ViewGroup, Z)View
check-cast p1, Landroidx/fragment/app/FragmentContainerView;          # children
check-cast v5, Landroid/widget/TextView;
check-cast p1, Landroid/widget/LinearLayout;                          # root
invoke-virtual {p0, p1}, Le/q;->setContentView(Landroid/view/View;)V  # install
```

Layout `R.layout.main_activity` (`0x7f0c0055`) — `/tmp/cr40-noice/decoded/res/layout/main_activity.xml`:

```xml
<LinearLayout ...>
  <androidx.fragment.app.FragmentContainerView
      android:name="androidx.navigation.fragment.NavHostFragment"
      android:id="@id/main_nav_host_fragment" ... app:navGraph="@navigation/main" />
  <TextView ... android:id="@id/network_indicator" ... />
</LinearLayout>
```

Welcome screen is `HomeFragment` (per `res/navigation/main.xml` startDestination
`@id/home` → `com.github.ashutoshgngwr.noice.fragment.HomeFragment`), driven
through `NavHostFragment`.

### Evidence 3: actual run-log shows setContentView NPE + empty-decor fallback

`/home/dspfac/android-to-openharmony-migration/artifacts/ohos-mvp/cr60-e13-noice/20260515_114007-inproc-app/dalvikvm.stdout`:

Lines 17363, 17393, 17398 — **three** NPE throws caught by substrate recovery:

```
17363: Handling exception NPE at y:386
       No matching catch ... in findViewById ...
       Match on catch ... in WestlakeInstrumentation;.findPageContentContainer

17393: Handling exception NPE at y:386
       No matching catch ... in y / h / setContentView ...
       Match on catch ... in WestlakeInstrumentation;.ensureStructuredPageShell

17398: Handling exception NPE at y:386
       No matching catch ... in y / h / setContentView ...
       Match on catch ... in WestlakeInstrumentation;.recoverAfterOnCreateFailure
```

Then line 17418: `step 5: drawView=android.widget.FrameLayout
source=Window.getDecorView` — the decor view that survived recovery is a
plain FrameLayout (the substrate's empty fallback shell), NOT the inflated
LinearLayout from `main_activity.xml`.

Line 17480: `canvas sample(0,0)=0x0 mid=0x0 hasBackground=false bgARGB=0x0`
— **SoftwareCanvas recorded ZERO drawColor / drawRect ops**. `View.draw(canvas)`
returned cleanly (line 17476) but the FrameLayout was empty (no background, no
children, no onDraw override), so nothing was recorded.

Line 17503: `step 8: argb materialized 921600 ints argb[0]=0xffffffff
argb[mid]=0xffffffff` — the buffer is filled from `FALLBACK_ARGB`
(`InProcessAppLauncher.java:94 FALLBACK_ARGB=0xFFFFFFFF`), per the launcher's
`canvas.hasBackground() ? canvas.getBackgroundARGB() : fallbackArgb` fallback
at line 450.

The DSI panel showed white = the launcher's fallback. The "theme background"
read of the pixel was a coincidence (white is also a plausible theme color).

### Evidence 4: what about apps that DO use Compose?

The Compose draw path (e.g. McD, which DOES ship `androidx.compose.*`):

`/home/dspfac/mcdonalds-apk/base-decompiled/smali/androidx/compose/ui/platform/AndroidComposeView.smali`
lines 4857-4917, `dispatchDraw(Landroid/graphics/Canvas;)V`:

1. Grab `CanvasHolder` → `AndroidCanvas` (Compose's wrapper around the framework Canvas).
2. `AndroidCanvas.C(android.graphics.Canvas)` — store the platform canvas.
3. `LayoutNode.H(androidx.compose.ui.graphics.Canvas, GraphicsLayer)` — traverse
   layout tree, drawing through Compose's `Canvas` interface.
4. Each compose primitive (e.g. `AndroidCanvas.m(F,F,F,F,Paint)`) just delegates
   back to `android.graphics.Canvas.drawRect(F,F,F,F,Paint)`.

So Compose's drawing **bottoms out at `android.graphics.Canvas.drawRect/drawText/...`** —
the same methods our shim's `android.graphics.Canvas` already implements
(`shim/java/android/graphics/Canvas.java:211-344`). If Compose were the path
in play, our existing shim primitives would be invoked.

This means hypothesis (a) is unlikely even for actual-Compose apps; hypothesis
(c) is false (no `Picture`, no `RenderNode.draw` — Compose calls the same
draw* methods as classic Views); hypothesis (b) is the only one that might
apply to a Compose app, but it requires a Compose app to test, which noice
isn't.

## Failure mode classification

For noice: **NONE of (a)/(b)/(c) apply** — the premise is invalid.

Re-classifying against the actual evidence, the noice-specific failure is:

**(d) Pre-draw view-tree wiring fails: setContentView NPE in
findViewById path during the inflated LinearLayout's attach, substrate
recovery installs an empty stub, View.draw on empty stub records nothing.**

## Recommendation: Path W (with corrected target)

The brief defined Path W as "defer — Compose is a separate workstream." Since
Compose ISN'T the issue, deferring Compose is correct, but the recommendation
should be re-framed: **defer Compose entirely and fix the actual view-tree
wiring blocker exposed here.**

### Concrete next CR

**CR67-followup ("noice view-tree wiring")** — diagnose and fix the
setContentView NPE so noice's real `LinearLayout` (FragmentContainerView +
TextView) reaches the decor view. Specifically:

1. **Identify class `y`** in noice's R8 dex — the smali file behind the NPE
   site (`y:386` is the bytecode offset, not source line). Cross-reference
   `dalvikvm -verbose:class` output during stage C to map the FQN.
2. **Inspect which `findViewById` call is throwing.** The post-catch line
   shows the catcher is `WestlakeInstrumentation.findPageContentContainer`,
   which calls `activity.findViewById(R.id.page_content)`. That ID doesn't
   exist in noice's resources, but the NPE happens **before** the
   lookup-fails-and-returns-null path — so something inside our shim's
   View.findViewById / mAttachInfo / RootViewManager chain is null-dereffing.
3. **Fix is likely 1-3 nullability guards** in `shim/java/android/view/View.java`
   `findViewById` / `findViewTraversal` / `mAttachInfo` lazy init (the V2
   substrate's PhoneWindow doesn't attach a ViewRootImpl so AttachInfo is
   never populated, but findViewById should still work via local tree walk).
4. **Validate with the existing E13 framework**: stage D's pixel should now
   come from `canvas.getBackgroundARGB()` (non-fallback) or `canvas.hasRect()`
   true, and `argb[0]` should match the LinearLayout's theme background.
5. **Stretch:** Fragment lifecycle (HomeFragment.onCreateView → its layout
   inflated) — that's where the actual Welcome-screen widgets come from. The
   Fragment lifecycle drive may itself be a 5-10 day workstream, but step
   1-4 above should at least make the LinearLayout's background visible
   (currently invisible because the LinearLayout isn't attached).

### Sizing

- View-tree wiring fix (steps 1-4): **3-5 person-days**, comparable to CR62
  (substrate context plumbing).
- Fragment lifecycle drive (step 5): **5-10 person-days**, separate
  workstream.
- Combined to "noice Welcome widgets visible": **8-15 person-days**.

Compose-specific paths (X / Y / Z from the brief) should be **deferred
indefinitely** — they apply to McD, not noice. McD would need its own spike
once McD-stage-D pixels are pushed.

### What Path X / Y / Z would look like if/when a Compose app reaches this point

For reference, after McD reaches its equivalent of E13 stage D:

- **Path X (extend SoftwareCanvas primitives):** Compose's `AndroidCanvas`
  calls `android.graphics.Canvas.drawRect`, `drawText`, `drawPath`,
  `drawBitmap`, `clipRect`, `save/restore`, `concat(Matrix)`, etc. — the
  full ~25-method API. The launcher's local `SoftwareCanvas` (this file:
  `ohos-tests-gradle/inproc-app-launcher/src/main/java/.../SoftwareCanvas.java`)
  records only 2 ops (drawColor + first drawRect). For a Compose app you'd
  need either (a) record into a list of op-records that the materialize
  step replays, or (b) wire SoftwareCanvas to the OHBridge native path that
  `shim/java/android/graphics/Canvas.java` already implements. The latter
  is closer to "free."
- **Path Y (intercept Compose's drawing entry point):** unnecessary —
  Compose already terminates at `android.graphics.Canvas.draw*`. Not
  recommended.
- **Path Z (Skia-software backend):** out of scope per brief; days/weeks.

The single architectural recommendation: when a Compose-using app eventually
needs to render, **make the launcher use the shim's
`android.graphics.Canvas` directly instead of the duplicated
`SoftwareCanvas`**. The shim's Canvas already records ops through OHBridge,
which (per `project_full_ohos_integration.md`) routes to the DRM
presenter. That collapses Path X to "remove the local SoftwareCanvas."

## Self-audit

- [x] No contract violations (no code edits made)
- [x] Diagnostic evidence captured + interpreted (4 evidence blocks above)
- [x] Failure mode classified — found that (a)/(b)/(c) all wrong; real mode
      is (d) view-tree wiring NPE
- [x] Recommendation: ONE path (Path W, with corrected target) + estimate
      (3-5 days for the unblock, 8-15 days to widgets visible)
- [x] noice E13 stages A/B/C/D still PASS (no code changes; latest run
      `artifacts/ohos-mvp/cr60-e13-noice/20260515_114007-inproc-app/e13-stages.txt`
      shows `stage_a=1 stage_b=1 stage_c=1 stage_d=1`)
- [x] No Path X stretch attempted — irrelevant given the diagnosis
- [x] Did not touch CR65 (McD build) or CR66 (libsurface) areas
- [x] Did not push to remote

## Files referenced

- `/home/dspfac/android-to-openharmony-migration/ohos-tests-gradle/inproc-app-launcher/src/main/java/com/westlake/ohostests/inproc/SoftwareCanvas.java`
  — duplicated 110-LOC canvas with only 2 recorded ops
- `/home/dspfac/android-to-openharmony-migration/ohos-tests-gradle/inproc-app-launcher/src/main/java/com/westlake/ohostests/inproc/InProcessAppLauncher.java`
  — lines 90-94 define `FALLBACK_ARGB=0xFFFFFFFF`; lines 446-451 materialize
  buffer from fallback when `!canvas.hasBackground()`
- `/home/dspfac/android-to-openharmony-migration/shim/java/android/graphics/Canvas.java`
  — full primitive surface (drawRect/drawText/drawPath/drawBitmap/clipRect/
  save/restore/concat) routing through OHBridge; would supersede SoftwareCanvas
  if launcher used it directly
- `/home/dspfac/android-to-openharmony-migration/shim/java/android/view/Window.java`
  — lines 177-245 `setContentView` ladder; the NPE happens further down the
  inflate→findViewById chain (in View.java) not here
- `/home/dspfac/android-to-openharmony-migration/shim/java/android/app/WestlakeInstrumentation.java`
  — lines 1454-1814 cover ensureStructuredPageShell / recoverAfterOnCreateFailure
  (the recovery paths catching the three NPEs)
- `/home/dspfac/android-to-openharmony-migration/artifacts/ohos-mvp/cr60-e13-noice/20260515_114007-inproc-app/dalvikvm.stdout`
  — full run log; key lines 17363, 17393, 17398 (NPEs), 17418 (drawView), 17480 (canvas state)
- `/tmp/cr40-noice/decoded/smali/com/github/ashutoshgngwr/noice/activity/MainActivity.smali`
  — noice MainActivity onCreate, confirms XML inflation path
- `/tmp/cr40-noice/decoded/res/layout/main_activity.xml`
  — noice's actual root layout (LinearLayout + FragmentContainerView + TextView)
- `/tmp/cr40-noice/decoded/res/navigation/main.xml`
  — confirms HomeFragment is the startDestination (Welcome screen)
- `/home/dspfac/mcdonalds-apk/base-decompiled/smali/androidx/compose/ui/platform/AndroidComposeView.smali`
  lines 4857-4917 — reference Compose draw path, for future McD spike only
