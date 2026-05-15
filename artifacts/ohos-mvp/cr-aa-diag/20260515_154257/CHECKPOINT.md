# CR-AA-diag — SoftwareCanvas instrumentation checkpoint

**Date:** 2026-05-15
**Agent:** 32
**Scope:** 1-2h bounded diagnostic. Determine empirically whether noice's REAL
View tree is being walked by SoftwareCanvas, or the substrate's recovery
FrameLayout fallback.
**Source artifact (noice):** `artifacts/ohos-mvp/cr60-e13-noice/20260515_154257-inproc-app/dalvikvm.stdout`
**Source artifact (E12 baseline):** `artifacts/ohos-mvp/cr60-e12/20260515_154503-inproc-app/dalvikvm.stdout`

## Instrumentation summary

Extended `SoftwareCanvas` (`ohos-tests-gradle/inproc-app-launcher/src/main/java/com/westlake/ohostests/inproc/SoftwareCanvas.java`)
to override every relevant draw / transform / clip method and record:

- total op count
- per-op histogram (drawColor, drawRect, drawRoundRect, drawText x4 variants,
  drawBitmap x4 variants, drawCircle, drawPath, drawLine, drawArc, drawOval,
  clipRect x3 variants, save, restore, translate, scale, rotate, concat,
  setMatrix)
- first-20 method calls with compact argument summaries
- 4 canvas sample positions: (0,0), (mid), (top-right), (bottom-left)

Extended `InProcessAppLauncher` to dump a compact decor-tree view (class +
bounds + visibility + child count, plus TextView.getText preview) AFTER
`View.draw(canvas)` returns.

Generic — no per-app branches, no Unsafe, no setAccessible. Macro-shim
contract clean. Note: `clipRect(float...)` and `clipPath(Path)` deliberately
NOT overridden because they have a return-type descriptor mismatch between
API 30 (`boolean`) and our runtime shim Canvas (`void`); the shim's own
implementations are no-op stubs anyway. `String.format("%08x", ...)` was
replaced with a local `hex(int)` helper because `Formatter` on this
dalvik-kitkat BCP crashes trying to init `libcore.icu.LocaleData`.

## Ground truth — noice run (20260515_154257)

### 1. Recorded op count
**totalOps = 4**

### 2. Op type histogram
| op           | count |
|--------------|-------|
| drawRect     | 1     |
| save         | 1     |
| translate    | 1     |
| clipRect_i   | 1     |

Zero `drawText`, zero `drawBitmap`, zero `drawColor`, zero `drawCircle`,
zero `drawPath`. Only 1 draw op total; the other 3 are infrastructure (save
+ translate + clipRect for ViewGroup.drawChild scaffolding).

### 3. First 20 method calls (only 4 in this run)
```
op[0] = drawRect(0,0,720,1280 color=0xfff5fbf4)
op[1] = save()
op[2] = translate(0.0,0.0)
op[3] = clipRect_i(0,0,720,1280)
```

`op[0]` is the windowBackground from CR-X's theme-resolved DecorView
background (ColorDrawable.draw -> Canvas.drawRect with full screen bounds).
`op[1]..[3]` are the standard ViewGroup.drawChild prelude that runs once
before walking children — but no child draw ops follow.

### 4. Decor tree walk identity
```
android.widget.FrameLayout [720x1280 @0,0 vis=0]
  childCount=1
  android.widget.FrameLayout [720x1280 @0,0 vis=0]
    childCount=0
```

The DecorView IS a `FrameLayout` containing exactly **one** child
`FrameLayout` (the content frame) with **zero** children. **This is the
substrate's recovery empty FrameLayout — CR-W's purported "real
LinearLayout + FragmentContainerView + TextView" tree did NOT land in
this run.** noice's `setContentView` either never ran successfully, or its
result was overwritten by the substrate's recovery fallback before
`getDecorView()` was called.

### 5. Canvas pixel samples
| position             | color (ARGB hex) |
|----------------------|------------------|
| (0, 0)               | 0xfff5fbf4       |
| (mid: 360, 640)      | 0xfff5fbf4       |
| (top-right: 719, 0)  | 0xfff5fbf4       |
| (bottom-left: 0,1279)| 0xfff5fbf4       |

All four corners are the same theme background color from `op[0]`.
The "pixel" on the panel reflects ONLY the windowBackground; there is
NO content drawn on top of it.

### 6. Recommendation per interpretation guide

Per the brief's interpretation rubric:
- "Op count <= 3, only drawColor: SUBSTRATE FALLBACK"
- "Op count 10-50, drawColor + drawRect/clipRect/save/restore but no drawText:
  REAL TREE but VIEW HIERARCHY ONLY"

Our case is **4 ops, ONE draw op (drawRect from theme background only),
with NO drawText / NO drawBitmap / NO child rendering of any kind**.
Combined with the decor-tree dump showing `childCount=0` on the content
frame, this is a clear **SUBSTRATE FALLBACK** result. The 4 ops differ
from the "0-2 drawColor" prediction only because CR-X's theme-bg path is
exercised; the underlying meaning is the same — noice's `setContentView`
did NOT populate the DecorView the launcher rendered from.

## Baseline cross-check — E12 hello-color-apk (20260515_154503)

Sanity: instrumentation is working correctly on a known-good path.

```
decor-tree-dump:
  com.westlake.ohostests.helloc.ColorView [720x1280 @0,0 vis=0]

canvas-diag totalOps=1
canvas-diag histogram: drawColor=1
canvas-diag op[0]=drawColor(0xff0000ff)
canvas sample(0,0)=0xff0000ff mid=0xff0000ff topRight=0xff0000ff bottomLeft=0xff0000ff
hasBackground=true bgARGB=0xff0000ff hasRect=false rectColor=0x0
```

E12 PASS (panel BLUE) — instrument correctly captures the single
`drawColor(0xff0000ff)` from `ColorView.onDraw`. Confirms the
instrumentation itself is not the reason noice shows 0 user-content ops.

## Recommendation for CR-AA+1

**Do NOT proceed to Fragment lifecycle work yet.** The CR67 lesson —
technical PASS != semantic PASS — applies here in full force.

Before sinking 5-10 days into Fragment lifecycle wiring:

1. **Re-verify CR-W's setContentView fix.** The decor-tree dump shows
   `childCount=0` on the content frame, but CR-W reported successful
   inflation of `R.layout.main_activity` (LinearLayout + FragmentContainerView
   + TextView). Either CR-W's tree is being lost somewhere between
   inflation and `getDecorView()`, or CR-W's fix was per-run flaky, or the
   substrate's recovery FrameLayout is clobbering the real content frame
   AFTER inflation. Check:
   - `[Window] decor windowBackground=0xfff5fbf4` is logged but no
     equivalent "[Window] setContentView added <viewClass>" trace appears
     in noice's stdout — search for it; if missing, setContentView never
     reached the production code path.
   - Is `Window.getDecorView()` returning the substrate's recovery
     DecorView (created lazily on first call when the activity's window
     wasn't initialized), bypassing whatever real content frame the
     LayoutInflater chain populated? Add a single-line marker
     `[Window] getDecorView path=<lazy|established>` to disambiguate.

2. **Find where the real inflated tree is going.** If CR-W's logs from
   20260515_140xxx-ish runs show the inflated tree, diff against this run.
   The diff will either reveal a regression that landed in CR-X/Y/Z, or
   reveal that the inflated tree was never put into the same DecorView the
   launcher reads.

3. **Only THEN — once op count climbs above 4 with at least one drawText
   or non-bg drawRect — proceed to Fragment lifecycle.** Otherwise we'd be
   wiring NavHostFragment.onCreateView into a host that the renderer can't
   see anyway.

## Files changed

- `ohos-tests-gradle/inproc-app-launcher/src/main/java/com/westlake/ohostests/inproc/SoftwareCanvas.java` — op-recording overrides for all major Canvas methods (`hex()` helper avoids LocaleData crash).
- `ohos-tests-gradle/inproc-app-launcher/src/main/java/com/westlake/ohostests/inproc/InProcessAppLauncher.java` — decor-tree dump after View.draw; 4-corner pixel sample; canvas-diag totalOps/histogram/first-N markers.

## Regression status

- E12 smoke (hello-color-apk): **PASS** on retry (run 20260515_154503 — `inproc-app-launcher present rc=0`, `has_oncreate=1 has_present_rc0=1 has_fill_argb=1`). Note: an initial retry (20260515_154348) hit a transient dalvikvm SIGSEGV inside `Activity.attachInternal` PRIOR to any of the new instrumentation running (crash on first `Class.forName(MainActivity).newInstance()`); this matches the pre-existing dalvik-kitkat flakiness pattern seen in earlier runs and is not caused by this CR.
- E13 noice apk-mode: PASS for stages A=B=C=D=1, `present rc=0`.
- MVP-0 / MVP-1: not exercised by this CR (driver path unchanged for those subcommands).
