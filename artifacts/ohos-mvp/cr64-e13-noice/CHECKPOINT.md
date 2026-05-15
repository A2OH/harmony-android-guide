# CR64 E13 noice Checkpoint — Stage C+D REACHED, BCP classpath-collision deminified (2026-05-15)

Agent 22. Spike bound: 1-2 days. Actual elapsed: ~2h (deminify + fix + retest).

## Outcome: Stage C reached AND Stage D pixel pushed in one CR

| Stage | CR63 (after) | CR64 (after) |
|---|---|---|
| A (dex visible) | PASS | **PASS** |
| B (Application.onCreate) | PASS | **PASS** |
| **C (Activity.onCreate)** | **FAIL** (NSFE at `<init>:26`) | **PASS** — `MainActivity.onCreate returned` |
| **D (pixel pushed)** | not reached | **PASS** — `present rc=0 reason=OK crtc=92 fb=161 conn=159 mode=720x1280 fill=argb` |

Final E13 grade: `passed=4 failed=0`, all four stages green.

## Deminification result

Both CR63 (NSFE at `:26`) and CR64 (NSME at `:29`) were a single underlying
issue: **R8-minified-vs-AOSP-named classpath collision** between our shim's
`androidx.*` classes and noice's R8-shrunk private copy in the same dex BCP.

Two collision patterns, both fundamental to the AndroidX/R8 toolchain:

### 1. Enum-constant rename (was hitting `:26` until CR63 mistakenly added
methods to `Configuration` and `Resources$Theme`)

The actual error site was inside noice's `Landroidx/lifecycle/y.<init>(w)V`
(LifecycleRegistry constructor) at source `.line 26`:
```smali
sget-object v0, Landroidx/lifecycle/Lifecycle$State;->n:Landroidx/lifecycle/Lifecycle$State;
```

R8 renamed `INITIALIZED → n` in noice's `Lifecycle$State` enum (kept the
five values as `m, n, o, p, q` corresponding to DESTROYED/INITIALIZED/CREATED/
STARTED/RESUMED). Our shim's `androidx.lifecycle.Lifecycle$State.class`
defines the AOSP names. When dalvik resolves `Landroidx/lifecycle/Lifecycle$State;`
it picks the FIRST dex in `-Xbootclasspath` that defines it — which was
`aosp-shim-ohos.dex` (positioned before `noice-classes-min13.dex`). Result:
`sget-object …->n` fails with `NoSuchFieldError` because the resolved class
only has DESTROYED/INITIALIZED/CREATED/STARTED/RESUMED.

CR63's `Configuration.setTo` and `Theme.rebase` additions WERE legitimate
fixes for an earlier attachBaseContext NSME, but they didn't address the
true `:26` blocker (which was in the same line-number range but on a
different chain). The CHECKPOINT.md hypothesis about a missing AndroidX
field was correct; the resolution was to strip-rather-than-add.

### 2. Method-signature rename (the `:29` blocker that CR63 unblocked into)

After CR63's strip of `Lifecycle$State.class` from the shim dex (so noice's
own version with the right field IDs wins), the failure moved to source
`.line 29` of `Landroidx/fragment/app/d0.<init>()V` (FragmentActivity ctor):
```smali
invoke-virtual {v5}, Landroidx/activity/ComponentActivity;->getSavedStateRegistry()Lu2/c;
```

Same root cause, different mechanism: noice's R8 renamed
`androidx.savedstate.SavedStateRegistry` to `Lu2/c;` in its own dex, so the
invocation expects return type `Lu2/c;`. Our shim's
`androidx.activity.ComponentActivity.getSavedStateRegistry()` returns the
AOSP-named `androidx.savedstate.SavedStateRegistry`. Method lookup uses
`name + descriptor`, so `()Lu2/c;` doesn't match `()Landroidx/savedstate/SavedStateRegistry;`
on the shim's ComponentActivity → `NoSuchMethodError`.

## Fix

`scripts/build-shim-dex-ohos.sh` — selective duplicate stripping of four
classes from the OHOS shim dex BEFORE dx packaging:
- `androidx/lifecycle/Lifecycle.class` (defensive)
- `androidx/lifecycle/Lifecycle$State.class`
- `androidx/lifecycle/Lifecycle$Event.class`
- `androidx/activity/ComponentActivity.class`

Stripping these from the OHOS shim dex lets the app's own R8-shrunk copies
resolve at runtime. Java enum `name()` strings stay "INITIALIZED" etc
(they're set via `Enum.<init>(String,I)V` in the app's `<clinit>`), so any
host-side reflective lookup via `Enum.valueOf` continues to work.

The script's `STRIP_CLASSES` array is the place to extend if future apps
hit additional collisions (e.g. `androidx/fragment/app/FragmentActivity`
once a future app ships it un-minified).

## Validation

| Test | Result |
|---|---|
| MVP-0 hello (arm32) | **PASS** |
| MVP-1 trivial-activity (arm32) | **PASS** |
| E12 inproc-app (hello-color-apk → BLUE) (arm32) | **PASS** (`pixel rc=0`) |
| E13 noice (arm32) stage A | **PASS** |
| E13 noice (arm32) stage B (Application.onCreate) | **PASS** |
| E13 noice (arm32) **stage C (Activity.onCreate)** | **PASS** |
| E13 noice (arm32) **stage D (pixel pushed)** | **PASS** |

All E13 stages passed in the same run (`passed=4 failed=0`).

## Self-audit (pre-commit)

- [x] No `Unsafe.allocateInstance` in new code
- [x] No `setAccessible(true)` in new code
- [x] No per-app branches — the strip list targets R8-universal collisions
- [x] `WestlakeContextImpl` untouched (frozen)
- [x] No new methods on classes we own; this CR only removes classes
- [x] All regression PASS (MVP-0, MVP-1, E12)
- [x] E13 noice: original `<init>:29` NSME confirmed gone; Stage C+D reached

## Consecutive blockers identified (this spike)

ONE underlying issue (classpath collision), TWO surface symptoms:
1. `NoSuchFieldError` (enum field rename) — was being incorrectly attributed
   to AndroidX SDK references in CR63 hypothesis
2. `NoSuchMethodError` (method signature rename) — opened CR64's window

Both resolved by the same surgical strip; no further blockers hit before
Stage C completion.

## Recommendation for CR65 (Stage E and beyond)

Stage C+D both green means noice's Activity is rendering through the
in-process DRM pipeline. The next steps:
1. **Stage E** (if exists in the launcher): integrate noice's actual View
   tree (not just any pixel) so the noise app's Welcome screen is visible
   on the DSI panel — phone-camera evidence
2. **McDonalds**: same strip should unblock McD's d0/q chain (same R8
   pattern; sample with `bash scripts/run-ohos-test.sh --arch arm32 inproc-app --apk mcd`)
3. **Extend STRIP_CLASSES proactively** for any future-app collisions; the
   current list is the minimum-viable set
4. **Re-examine the CR63 Configuration/Theme additions**: those AOSP-method
   adds were legit per the macro-shim contract (we own those classes), but
   they fixed a different chain. Keep them — they pre-fix future attachBaseContext
   paths from other apps that DO reach those entry points.
