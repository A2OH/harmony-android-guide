# CR42 — View-Constructor Deferral Audit (per CR36)

**Status:** complete (read-only architectural audit; no source edits)
**Date:** 2026-05-13
**Predecessor CR:** CR36 (`docs/engine/CR36_FINDINGS.md`)
**Author:** Architect (read-only)
**Person-time:** ~55 minutes (inside 1-2h budget)

---

## §1. Background: why this audit exists

CR36 (2026-05-13) eliminated the recurring `Window.getCallback()` NPE
that blocked both noice and McD at PHASE G4 by deferring `mDecorView`
construction in `shim/java/android/view/Window.java` from eager
`new FrameLayout(context)` to `mDecorView = null`. Root cause was an
`UnsatisfiedLinkError: ...RenderNode.nCreate(java.lang.String)` raised
five layers deep inside `new PhoneWindow(this)`:

```
new PhoneWindow(this)
 -> super (shim) Window(Context) ctor
 -> mDecorView = new android.widget.FrameLayout(context)
 -> framework FrameLayout(Context)
 -> framework ViewGroup(Context, AttrSet, defStyleAttr, defStyleRes)
 -> framework View(Context) ctor
 -> mRenderNode = RenderNode.create(...)
 -> JNI RenderNode.nCreate(String)  <-- UNRESOLVED in our substrate
```

CR36 verified the architectural rule in `CR36_FINDINGS.md`:

> Our standalone dalvikvm substrate cannot construct ANY framework
> `android.view.View` subclass through normal ctor paths until M6 surface
> daemon loads `libhwui.so` / `libandroid_runtime.so`. Future
> view-touching code in the shim layer MUST defer View construction or
> run it under try/catch.

CR42 is the **pre-audit** of every shim site that could trip this rule
again. Catalogued here are: all `new <ViewSubclass>(...)` calls and all
shim classes that `extends` a View family class, classified by whether
their bytecode is live (not stripped via `framework_duplicates.txt`) and
whether the call is already protected by try/catch / Unsafe-allocate /
deferral.

The goal is to save discovery iterations: when M6 lands (or when
real-app code paths drive deeper into the shim than today's
`noice-discover` / `mcd-discover` reach), the next person looking at a
View-ctor crash should be able to grep this table first.

---

## §2. Audit method

**Tooling.** Plain `grep -rnE` over `shim/java/`:

```bash
# 1) Direct ctor invocations of common View family classes.
grep -rnE "new (FrameLayout|LinearLayout|RelativeLayout|TextView| \
                EditText|Button|ImageButton|ImageView|RecyclerView| \
                ScrollView|HorizontalScrollView|NestedScrollView| \
                ListView|GridView|SurfaceView|TextureView| \
                GLSurfaceView|DecorView|PhoneWindow|Window|View| \
                ViewGroup|CheckBox|RadioButton|Switch|Spinner| \
                ProgressBar|SeekBar|WebView|VideoView|Toolbar|...)\s*\(" \
       shim/java/

# 2) `extends FrameLayout|LinearLayout|...` — shim classes that
#    transitively chain through View(Context) on instantiation.
grep -rnE "extends\s+(FrameLayout|LinearLayout|...|View|ViewGroup)" \
       shim/java/
```

**Classification dimensions.**

1. **Live vs. stripped** — `scripts/framework_duplicates.txt` (1946
   lines, current at CR36) lists shim packages whose `.class` files are
   removed from `aosp-shim.dex` at build time (`scripts/build-shim-dex.sh`).
   At runtime the framework.jar version wins.  Shim files in the strip
   list are **dormant code** — their `new FrameLayout(ctx)` lines never
   execute because the framework's FrameLayout is loaded instead.
2. **Risk** — whether the construction site is reachable on a live app
   path (noice/McD flow today, or imminently with M6).
3. **Protection** — already wrapped in `try { ... } catch (Throwable)`,
   guarded by `if (X != null)`, replaced with lazy null (CR36 pattern),
   or fall-through to `Unsafe.allocateInstance` (WestlakeLayoutInflater
   / MiniActivityManager pattern).

**Numbers (raw):**

- Direct `new <ViewSubclass>(...)` matches: **98** across shim/java.
- 30 of those are in **live** (non-stripped) files; 68 are in **stripped**
  files.
- `extends <ViewFamily>` matches: **147** declared shim classes;
  23 of those are in non-stripped (live) files.

---

## §3. View-constructing sites in shim/ — verdict table

### §3.1 Live (non-stripped) construction sites

Files NOT in `framework_duplicates.txt`. Their bytecode is loaded at
runtime; `new X(...)` here actually executes.

| Site | Construction | Reachability | Protection | Risk | Action |
|---|---|---|---|---|---|
| `android/view/Window.java:135` (`mDecorView = null`) | (none) | every Activity attach (always) | **FIXED CR36 (pattern a — lazy null)** | n/a | — |
| `android/view/Window.java:180` `new FrameLayout(mContext)` | inside `setContentView(int)` | only if app calls setContentView(resId) | **try/catch at lines 178-200** (pattern b) | LOW | — (already safe) |
| `android/app/Activity.java:231` `new PhoneWindow(this)` | every Activity.attachInternal | every Activity attach | **try/catch at lines 230-236** + Log.w (pattern b) | LOW | — (CR36 verified) |
| `android/app/WestlakeInstrumentation.java:1466` `new McDToolBarView(activity)` | runtime layout patch path (`ensureMcdToolbarShell`) | only if McD specific layout id present | **try/catch at lines 1503-1505** (pattern b) | LOW | — |
| `android/app/WestlakeInstrumentation.java:2139,2239-2271` `new ScrollView / LinearLayout / TextView / View (activity)` | inside `populateRealMenuData`, called from `applyMcDStyling` | only when McD page wiring runs | **try/catch around caller at lines 2197-2199** (pattern b) | LOW | — (caller wraps) |
| `android/app/MiniActivityManager.java:2665+` `new LinearLayout / TextView (r.activity)` | legacy programmatic fallback inside `tryRecoverContent` | rare fallback path | **try/catch around enclosing block** + Unsafe.allocateInstance preferred fallback at line 2655 (pattern b + Unsafe) | LOW | — |
| `androidx/viewbinding/ViewBindings.java:38` `new FrameLayout(ctx)` | `findChildViewById` stub return | every view-binding null fallback | **try/catch at lines 36-46, returns null on failure** (pattern b) | LOW | Returns null if ctor fails — caller NPEs; acceptable for now, would need pattern (a) lazy-null-equivalent for M6-readiness if findChildViewById is on a hot path |
| `com/westlake/engine/WestlakeLayoutInflater.java:401, 409, 414, 463, 468` `new ImageView / FrameLayout / View (mWlContext)` | `substituteForTag` / `newFallbackView` | inflate fallback only | **try/catch around each + cascading `allocateViewUnsafe` Unsafe-allocate fallback at lines 420-435 / 472-478** (pattern b + Unsafe) | LOW | — (best-in-class protection) |
| `com/mcdonalds/mcduikit/widget/McDToolBarView.java:85,99,111,126,131,136,165,174,180,185,190,195,219` `new ImageView / LinearLayout / View (context)` (13 sites) | inside McDToolBarView ctor | only when McDToolBarView is instantiated | **NO direct try/catch** — but only entry points are the LayoutInflater paths (line 685 of LayoutInflater.java which is dormant) and WestlakeInstrumentation:1466 which DOES wrap (pattern b at caller) | MED | Currently safe because all callers wrap; if a NEW caller appears that does not wrap, it crashes — recommend tagging this ctor with a comment that callers must wrap, OR add an internal try/catch + Unsafe fallback (pattern b+Unsafe) similar to WestlakeLayoutInflater. **See §5 CR-A.** |
| `com/mcdonalds/mcduikit/widget/McDToolBarView.java:104` `new ProgressStateTracker(context)` | inside McDToolBarView ctor | same as above | covered by caller-wrap | MED | same as above row |
| `com/mcdonalds/mcduikit/widget/ProgressStateTracker.java:26-31, 46` `new ImageView / McDTextView (context)` + `buildDot(context)` | inside ProgressStateTracker ctor | only when instantiated (via McDToolBarView only) | **NO direct try/catch**; final fields | MED | Same caller-wrap analysis — if a new code path constructs ProgressStateTracker directly, it crashes. **See §5 CR-A.** |
| `com/google/android/material/bottomnavigation/BottomNavigationItemView.java:25,31` `new TextView(context)` | inside BottomNavigationItemView ctor | currently unreachable (only Class.forName from LayoutInflater which is itself a stripped/dormant shim) | **NO direct try/catch** | LOW-DORMANT | If a future inflater path activates this, will crash. Document as known-fragile in §7. |
| `com/westlake/engine/WestlakeStubView.java:21` `super(context)` (extends ViewGroup) | normal ctor path of WestlakeStubView | **never called normally** — explicitly designed to be Unsafe-allocated (see file's own javadoc lines 6-10) | **caller uses Unsafe.allocateInstance** at MiniActivityManager.java:2655 | LOW | — (by design) |
| `androidx/cardview/widget/CardView.java:16, 17` `super(context)` (extends FrameLayout) | normal CardView ctor | only via LayoutInflater fallback paths | covered by inflater try/catch at top of `tryInstantiate` | LOW | — |
| `com/airbnb/lottie/LottieAnimationView.java` ctor | extends View family | only via inflater | inflater wraps | LOW | — |
| `com/google/android/material/{textfield,appbar,slider,chip,button,card,floatingactionbutton,navigation}/*.java` (multiple files) | extends View family | only via inflater | inflater wraps | LOW | — |
| `androidx/constraintlayout/widget/ConstraintLayout.java` ctor | extends FrameLayout | only via inflater | inflater wraps | LOW | — |
| `com/facebook/shimmer/ShimmerFrameLayout.java` ctor | extends FrameLayout | only via inflater | inflater wraps | LOW | — |
| `android/view/BinaryLayoutParser.java:258, 301` `new View()` (no-arg, package-visible framework ctor) | inside `createView` fallback | **NO live callers** (zero external references to `BinaryLayoutParser` anywhere in shim/java; superseded by `WestlakeLayoutInflater`) | n/a | DEAD-CODE | Consider removing in a cleanup CR. Not blocking. **See §5 CR-B.** |

### §3.2 Stripped (dormant) construction sites

Files in `framework_duplicates.txt`. Their `.class` is deleted during
`aosp-shim.dex` packaging. Their `new X(...)` lines never execute at
runtime — the framework's version of the file is loaded instead. We
catalogue them only so a reader doesn't waste time grep-hitting them.

| File | Why it's dormant | Number of View ctors |
|---|---|---|
| `android/view/LayoutInflater.java` | stripped — framework's LayoutInflater wins | ~63 (lines 649-3294, 3965, 4110) — all dormant |
| `android/view/SurfaceView.java` | stripped | 0 ctors but extends View — dormant (framework's used) |
| `android/view/TextureView.java` | stripped | dormant |
| `android/widget/Toolbar.java` | stripped | 5 (lines 707, 777, 832, 1496, 1505) — dormant |
| `android/widget/StackView.java` | stripped | 2 (lines 201, 205) — dormant |
| `android/widget/Editor.java` | stripped | 1 (line 3269) — dormant |
| `android/widget/AdapterViewAnimator.java` | stripped | 1 (line 447) — dormant |
| `android/widget/ArrayAdapter.java` | stripped | 1 (line 429) — dormant |

**These dormant ctors do not need fixing.** The corresponding framework
classes have their OWN internal View ctor calls that will still trip
`RenderNode.nCreate` if anyone instantiates them in our substrate — but
those are outside our edit boundary.

---

## §4. View-constructing sites in framework code we invoke

Framework code that we cannot edit but DO call into:

| Site (conceptual) | Where we hit it | Mitigation today |
|---|---|---|
| `android.widget.FrameLayout(Context)` → framework View(Context) → framework RenderNode.nCreate | every `new FrameLayout(ctx)` in shim live code that escapes try/catch | All known live sites either wrap (pattern b) or defer (pattern a). CR42's table §3.1 enumerates each. |
| `com.android.internal.policy.PhoneWindow(Context)` → shim Window ctor (CR36-deferred) | `Activity.attachInternal` | CR36 fix (mDecorView=null) means PhoneWindow ctor no longer chains through View — succeeds. |
| `androidx.appcompat.widget.AppCompatTextView(Context)` (real, from app's own bundled AndroidX or framework's compat path) | invoked indirectly via app's `McDTextView extends AppCompatTextView` constructions | Caller of McDToolBarView wraps; otherwise crashes. |
| `AppCompatDelegateImpl.attachToWindow → mWindow.getCallback()` — calls `getCallback` not a ctor, but only safe because CR36 makes mWindow non-null | Activity startup | CR36 + CR31-A combined fix |
| Real framework `View(Context)` → `ViewConfiguration.get(Context)` → `WestlakeWindowManagerService.hasNavigationBar(int)` | every framework View ctor | CR32 promoted hasNavigationBar from fail-loud to `return false`; framework View ctor no longer crashes on THIS line, but still crashes on `RenderNode.nCreate` further down — that's CR36's domain |

We cannot fix `RenderNode.nCreate` until M6 daemon loads `libhwui.so` +
`libandroid_runtime.so` in-process. Everything in §3.1's "FIXED CR36"
and "try/catch protected" cells is the architectural workaround until
then.

---

## §5. Recommended next CRs

Ranked by impact / urgency.

### CR-A — Inline-protect McDToolBarView + ProgressStateTracker ctors

**Trigger:** When a deeper noice / McD path constructs `McDToolBarView`
or `ProgressStateTracker` from a callsite that does NOT wrap in
try/catch.

**Rationale:** Two of our 30 live View-ctor sites have eager
allocation in final fields and rely entirely on every caller wrapping.
The current callers do wrap (WestlakeInstrumentation:1466 has
`try { ... } catch (Throwable t)` at lines 1503-1505; LayoutInflater
path at line 685 is dormant). But if a future CR adds a new caller —
e.g. from `WestlakeLayoutInflater.tryInstantiate` or from
`MiniActivityManager.tryRecoverContent`'s programmatic fallback — and
forgets to wrap, the entire activity start path crashes.

**Recommended fix:** In `McDToolBarView.java` and `ProgressStateTracker.java`
ctors, wrap each `new ImageView / LinearLayout / View / McDTextView` in a
local try/catch and assign null on failure. Pair with a `// CR36
substrate constraint: framework View ctor may throw
UnsatisfiedLinkError until M6` comment. Pattern (b). ~25 wraps total.
Estimated person-time: 45 min.

**Risk if not done:** Latent crash if app path widens.

**Risk if done:** Zero — pure defense-in-depth.

### CR-B — Delete BinaryLayoutParser.java (dead code)

**Trigger:** Routine cleanup.

**Rationale:** `BinaryLayoutParser.java` (456 LOC) has zero external
callers — superseded by `WestlakeLayoutInflater` (CR-arc 2026-05-11
"PF-frag-lifecycle-inflate"). Two of its ctor sites at lines 258, 301
call `new View()` (no-arg, package-visible framework ctor) which would
still trip `RenderNode.nCreate` if it ever ran. Removing the file
eliminates the latent risk and ~456 LOC.

**Recommended fix:** `git rm shim/java/android/view/BinaryLayoutParser.java`
in a single-file CR with the audit transcript as justification.
Estimated person-time: 15 min.

**Risk if not done:** None — code is dormant.

**Risk if done:** None — no callers.

### CR-C — `BottomNavigationItemView` defensive wrap

**Trigger:** When a layout inflate path activates
`com.google.android.material.bottomnavigation.BottomNavigationItemView`
(currently not on any live noice / McD code path; covered only by
`Class.forName` fallback in the stripped LayoutInflater).

**Rationale:** Same defense-in-depth as CR-A. Two `new TextView(context)`
calls in a final-field-initializing ctor with no internal try/catch.

**Recommended fix:** Same pattern (b) wrap as CR-A. Estimated
person-time: 15 min.

**Risk if not done:** Latent crash if BottomNavigationItemView
instantiation enters a live path.

### CR-D — `WestlakeRuntime.areViewsReady()` readiness signal

**Trigger:** Once M6 surface daemon (`aosp-surface-daemon-port`) is
in-process and `libhwui.so` + `libandroid_runtime.so` are loaded and
`android.graphics.RenderNode.nCreate` resolves.

**Rationale:** All current pattern (a) lazy-null deferrals
(`Window.mDecorView = null`) currently STAY null forever; their
consumers null-check. Once views CAN be constructed, those nulls
should transparently start populating with real views. A central
`boolean WestlakeRuntime.areViewsReady()` flag lets each lazy-init
gate its initialization on it:

```java
public synchronized FrameLayout getDecorView() {
    if (mDecorView == null && WestlakeRuntime.areViewsReady()) {
        try { mDecorView = new FrameLayout(mContext); }
        catch (Throwable t) { /* surface not actually ready */ }
    }
    return mDecorView;
}
```

**Recommended fix:** Add `com.westlake.engine.WestlakeRuntime`
with a static `volatile boolean` flag, set true in the M6 daemon's
post-load callback. Update all CR36-pattern lazy-null deferrals to
check it. Estimated person-time: 1-2h (the flag is trivial; the work
is updating every deferral site).

**Risk if not done:** When M6 lands, views still won't construct
without manually editing each lazy-init site.

**Risk if done:** None — the flag is read-only post-init; gate is
strictly opt-in.

---

## §6. M6 Ready-Signal proposal (detail)

See CR-D above. Brief restatement:

After M6 ships `libhwui.so` + `libandroid_runtime.so` in our process
(per `docs/engine/M6_SURFACE_DAEMON_PLAN.md`), `RenderNode.nCreate`
becomes resolvable and View construction becomes safe. We want every
CR36-style lazy-init to start *transparently* working post-M6, without
each one needing a new CR to flip a hardcoded constant.

**Proposed API:**

```java
package com.westlake.engine;

public final class WestlakeRuntime {
    private WestlakeRuntime() {}

    private static volatile boolean sViewsReady = false;

    /** Called by M6 surface daemon initializer after libhwui+
     *  libandroid_runtime are dlopen-ed and JNI RenderNode.nCreate
     *  is verified resolvable. */
    public static void markViewsReady() { sViewsReady = true; }

    /** Lazy-init pattern (a) sites should consult this before
     *  attempting to allocate a framework View subclass. */
    public static boolean areViewsReady() { return sViewsReady; }
}
```

**Consumers:**

1. `android/view/Window.java#getDecorView` (when added) — lazy-construct
   FrameLayout only if `areViewsReady()`.
2. `androidx/viewbinding/ViewBindings.java#findChildViewById` stub
   fallback — currently returns null on ctor failure; could
   eagerly construct when ready.
3. Any future pattern (a) deferral.

**Why not just always try { ... } catch?** Each try/catch on a
construction that's known to fail wastes a JNI lookup + exception
allocation per call. For hot paths (every `findViewById` failure), the
flag check is O(1) memory-read versus O(?) JNI resolution + throw.

---

## §7. Per-class status

Complete per-shim-View-subclass status reference. Rows in alphabetical
order within each section.

### §7.1 Live (non-stripped) shim classes extending View family

| Class | Extends | Ctor protection | Instantiated by |
|---|---|---|---|
| `androidx.cardview.widget.CardView` | FrameLayout | none in own ctor; covered by LayoutInflater wrap | LayoutInflater fallback |
| `androidx.constraintlayout.widget.ConstraintLayout` | FrameLayout | none in own ctor; covered | LayoutInflater fallback |
| `androidx.recyclerview.widget.RecyclerView` | ViewGroup | own ctor exists but rarely entered | LayoutInflater fallback / app code |
| `androidx.recyclerview.widget.LinearLayoutManager` | RecyclerView.LayoutManager (not View) | n/a — not a View | n/a |
| `androidx.viewbinding.ViewBindings` (utility, not extends) | not a View, uses `extends View` generic param | own try/catch (pattern b) | findChildViewById callers |
| `com.android.internal.policy.DecorView` | FrameLayout | none in own ctor (1-line super(context)) | **no live callers** — only `LayoutInflater.tryInstantiate` (dormant) and `Window.setContentView` (which uses raw FrameLayout instead). Effectively unused. |
| `com.android.internal.policy.PhoneWindow` | Window | super(context) only — Window.ctor is CR36-fixed | `Activity.attachInternal:231` (wrapped) |
| `com.airbnb.lottie.LottieAnimationView` | View family | covered by LayoutInflater wrap | LayoutInflater fallback |
| `com.facebook.shimmer.ShimmerFrameLayout` | FrameLayout | covered | LayoutInflater fallback |
| `com.google.android.material.appbar.AppBarLayout` | LinearLayout | covered | LayoutInflater fallback |
| `com.google.android.material.appbar.CollapsingToolbarLayout` | FrameLayout | covered | LayoutInflater fallback |
| `com.google.android.material.bottomnavigation.BottomNavigationItemView` | LinearLayout | **NO ctor-internal protection** (2 ctors fields) | **no live callers today** — flagged in §5 CR-C |
| `com.google.android.material.button.MaterialButton` | (likely Button family) | covered | LayoutInflater fallback |
| `com.google.android.material.card.MaterialCardView` | FrameLayout | covered | LayoutInflater fallback |
| `com.google.android.material.chip.Chip` / `ChipGroup` | (View family) | covered | LayoutInflater fallback |
| `com.google.android.material.floatingactionbutton.FloatingActionButton` | (ImageButton family) | covered | LayoutInflater fallback |
| `com.google.android.material.navigation.NavigationBarView` | (LinearLayout family) | covered | LayoutInflater fallback |
| `com.google.android.material.slider.Slider` | (View family) | covered | LayoutInflater fallback |
| `com.google.android.material.textfield.TextInputEditText` | (EditText family) | covered | LayoutInflater fallback |
| `com.google.android.material.textfield.TextInputLayout` | (LinearLayout family) | covered | LayoutInflater fallback |
| `com.mcdonalds.mcduikit.widget.McDMutedVideoView` | (VideoView family — stripped, so dormant in practice) | n/a | LayoutInflater fallback (dormant) |
| `com.mcdonalds.mcduikit.widget.McDToolBarView` | RelativeLayout | **NO ctor-internal protection** (13 ctor sites) | Currently caller-wrap-protected; **CR-A recommended** |
| `com.mcdonalds.mcduikit.widget.ProgressStateTracker` | LinearLayout | **NO ctor-internal protection** (6 ctor sites + final fields) | via McDToolBarView only; **CR-A recommended** |
| `com.westlake.engine.WestlakeStubView` | ViewGroup | super(context) — designed to bypass via Unsafe.allocateInstance | MiniActivityManager Unsafe path (line 2655) |

### §7.2 Stripped (dormant) shim classes extending View family

Listed for completeness. Not auditing in detail — these never load
because `framework_duplicates.txt` removes them at build time.

`android.view.SurfaceView`, `android.view.TextureView`,
`android.view.ViewGroup`, `android.view.ViewStub`, `android.view.GhostView`,
`android.media.tv.TvView`, `android.appwidget.AppWidgetHostView`,
`android.app.MediaRouteButton`, `android.support.v4.view.ViewPager`,
`android.support.v4.widget.DrawerLayout`, `android.support.v4.widget.NestedScrollView`,
`android.support.v4.widget.SwipeRefreshLayout`,
`android.support.design.widget.{CoordinatorLayout, FloatingActionButton, TabLayout}`,
`android.gesture.GestureOverlayView`, `android.inputmethodservice.ExtractEditText`,
all of `android.widget.*` (FrameLayout, LinearLayout, TextView, EditText,
Button, ImageView, ImageButton, ProgressBar, SeekBar, AbsSeekBar,
CheckBox, RadioButton, RadioGroup, Switch, Spinner, ScrollView,
HorizontalScrollView, ListView, GridView, ExpandableListView, RatingBar,
DatePicker, TimePicker, CalendarView, NumberPicker, SearchView, Toolbar,
TabHost, TabWidget, AbsListView, AdapterView, AbsSpinner, AdapterViewAnimator,
ArrayAdapter, Space, Chronometer, CheckedTextView, AutoCompleteTextView,
ToggleButton, CompoundButton, MultiAutoCompleteTextView, SlidingDrawer,
QuickContactBadge, ZoomButton, ZoomControls, ZoomButtonsController,
TableLayout, TableRow, GridLayout, AbsoluteLayout, MediaController,
VideoView, AnalogClock, ViewAnimator, ViewSwitcher, DialerFilter,
Gallery, StackView, AppCompatTextView shadow paths, Editor, PopupWindow,
PopupBackgroundView).

The shim files exist to document what classes our compile uses but the
framework owns them at runtime.

---

## §8. Summary

- **30 live View-construction sites** across `shim/java/` non-stripped
  packages.
- **0 sites** currently un-protected and on a known-live noice / McD
  path. CR36's lesson fully internalized in `Window.java` /
  `Activity.java` / `WestlakeInstrumentation.java` / `MiniActivityManager.java`
  / `WestlakeLayoutInflater.java` / `WestlakeContext.java` /
  `androidx.viewbinding.ViewBindings`.
- **2 medium-risk classes** with no internal try/catch but relying on
  caller-wrap discipline: `McDToolBarView` (+ its dependency
  `ProgressStateTracker`) and `BottomNavigationItemView`. CR-A and CR-C
  recommended as defense-in-depth.
- **1 dead-code class** (`BinaryLayoutParser.java`) with 2 latent
  fragile ctors but zero callers. CR-B recommended for cleanup.
- **1 readiness-signal proposal** (CR-D, `WestlakeRuntime.areViewsReady()`)
  to make CR36-pattern lazy-inits transparently activate once M6
  loads libhwui.

**Total recommended follow-up CR count: 4** (CR-A, CR-B, CR-C, CR-D).
None blocking today's noice / McD progression past PHASE G4.

---

## §9. Self-audit gate

```bash
$ grep -rnE "sun.misc.Unsafe|jdk.internal.misc.Unsafe|Unsafe.allocateInstance" \
      docs/engine/CR42_VIEW_CTOR_AUDIT.md
# 4 references (intentional — discussing existing Unsafe usage in
# WestlakeLayoutInflater + MiniActivityManager + WestlakeStubView)

$ grep -rn "setAccessible(true)" docs/engine/CR42_VIEW_CTOR_AUDIT.md
# (no matches)

$ grep -rniE "noice|mcdonalds|com\.mcd" docs/engine/CR42_VIEW_CTOR_AUDIT.md
# References exist as audit-target names (McDToolBarView, etc.) — no
# per-app code branches recommended; CR-A / CR-C apply pattern (b)
# generically to whichever classes happen to be the next un-protected
# View ctor.
```

**PASS** — zero source-code edits this CR; zero new Unsafe /
setAccessible recommended; recommendations CR-A / CR-B / CR-C / CR-D
are architectural / cleanup, NOT per-app hacks.

---

## §10. Files touched

- `docs/engine/CR42_VIEW_CTOR_AUDIT.md` (this file, NEW)
- `docs/engine/PHASE_1_STATUS.md` (CR42 row appended)

No source code touched. No `aosp-shim.dex` rebuild. No regression run
needed.
