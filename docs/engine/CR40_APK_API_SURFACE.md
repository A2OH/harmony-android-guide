# CR40 — noice + McD Static API Surface Analysis

**Date:** 2026-05-13
**Role:** Architect (read-only static analysis)
**Scope:** Predictive Tier-1 promotion list from full smali scan of `com_github_ashutoshgngwr_noice.apk` + `com_mcdonalds_app.apk`
**Anti-drift compliance:** ZERO source edits; ZERO shim edits; 1 new doc + PHASE_1_STATUS.md row.
**Companion docs:** `WESTLAKE_ACTIVITY_API.md` (Activity buckets), `CR32_AUDIT_LOG.md` (recent promotions), `BINDER_PIVOT_DESIGN_V2.md`.

---

## §1. Summary

The two production APKs were pulled, baksmali-decoded with `apktool 2.9.3`, then every
`invoke-*` opcode targeting classes the Westlake shim owns was extracted, grouped, and
counted across the entire bytecode body (excluding sandbox / test code outside the APK).

| Metric | noice | McD |
|---|---|---|
| APK size | 5.1 MB | 185 MB (base) |
| `.dex` files | 1 | 33 |
| Smali classes | 5,832 | 119,275 |
| `<activity>` decls in AndroidManifest | 8 | 191 |
| Total framework `invoke-*` sites (Compose-runtime + framework, raw) | 36,335 | 522,279 |
| Unique framework methods called (raw) | 6,872 | 70,973 |
| **Calls into shim-surface classes (after filter)** | **7,270** | **33,322** |
| **Unique shim-surface methods called** | **591** | **834** |
| Methods called by **both** apps (common surface) | 460 | 460 |
| App-specific (called only in one app) | 131 (noice) | 374 (McD) |

Filter scope (`shim-surface`): `android/app/{Activity,Application,Service,ActivityManager,
NotificationManager,AlarmManager,SearchManager,DownloadManager,UiModeManager}`,
`android/content/{Context,ContextWrapper,SharedPreferences,SharedPreferences$Editor,
res/{Resources,AssetManager,Configuration,TypedArray,Resources$Theme,ColorStateList,
XmlResourceParser}}`, `android/view/{Window,WindowManager,LayoutInflater,Display}`,
`android/os/{Handler,Looper,ServiceManager,PowerManager,Bundle,BaseBundle,Process,
Build,Build$VERSION,UserHandle}`, `android/util/{Log,DisplayMetrics,TypedValue}`.

**Headline finding:** of the 67 currently-fail-loud methods on `android.app.Activity`, the
apps call **14** of them — the rest are fail-loud against zero traffic and can stay that
way. The 14 are the predictive Tier-1 list (§3).

---

## §2. Per-class call breakdown

How many calls and how many unique methods touch each shim-surface class. This is the
"size of the surface" each shim file is responsible for.

| Shim class | noice unique | noice calls | McD unique | McD calls | Shim file status |
|---|---:|---:|---:|---:|---|
| `Activity` | 85 | 257 | **113** | **2,068** | mostly Implement (CR32 promoted ~82 methods); 67 fail-loud remain |
| `Application` | 5 | 8 | 13 | 58 | Implement (V2-Step3 complete) |
| `Context` (abstract) | 55 | 1,001 | **90** | **8,432** | abstract — calls land on `ContextImpl`/`ContextWrapper`/`WestlakeContextImpl` |
| `ContextWrapper` | 4 | 26 | 7 | 75 | Implement (most are `super.foo()` defaults) |
| `SharedPreferences` | 9 | 69 | 21 | **3,222** | Implement (`SharedPreferencesImpl` + `WestlakeSharedPreferences`) |
| `SharedPreferences$Editor` | 9 | 53 | 14 | 1,885 | Implement |
| `Resources` | 41 | 531 | **55** | **2,532** | Implement (CR30-B shadowed; arsc-table parser + per-app resID maps) |
| `TypedArray` | 29 | 1,415 | 31 | 2,262 | Implement (stub) |
| `Window` | 22 | 152 | **39** | **398** | Implement (mDecorView lazy per CR36; `Window.Callback` per CR31-A) |
| `LayoutInflater` | 8 | 165 | 19 | 1,348 | Implement (`WestlakeLayoutInflater`) |
| `Bundle` / `BaseBundle` | 75 | 1,624 | 93 | 5,632 | Implement (full impl) |
| `Handler` / `Looper` | 29 | 433 | 34 | 1,674 | Implement |
| `Log` | 19 | 877 | 4 | 376 | Implement (Android Log thunks) |
| `Process` | 5 | 12 | 10 | 1,305 | Implement-as-needed; many AOSP-default-OK calls |
| `TypedValue` | 8 | 85 | 10 | 652 | Implement |

**Key observation:** McD calls Context~8.4k times — 10× noice's volume. This is driven by
Hilt-DI passing context through 1000+ dagger-generated `_HiltModules`. The per-method
distribution (next section) reveals which Context methods actually carry that traffic.

---

## §3. PREDICTED NEXT BLOCKERS (Tier-1 promotion candidates)

These methods are **currently fail-loud** in the shim AND **called by app bytecode**.
Each is a guaranteed UOE-throw at the first call site reached at runtime.

Ranked by combined call count (highest = first one likely to fire).

| # | Method | noice | McD | total | Why apps call it | Promotion strategy |
|---:|---|---:|---:|---:|---|---|
| 1 | `Activity.overridePendingTransition(int,int)` | 2 | 56 | **58** | McD splash→nav transitions; both apps decorate Activity launches | **no-op** (`return;`) — V2 has no animator |
| 2 | `Activity.startActivityForResult(Intent,int[,Bundle])` (2 overloads) | 3 | 21 | **24** | McD orderv2 NavHostActivity → checkout flows; system pickers | **delegate** to `WestlakeActivityManagerService.startActivityForResult(token, intent, reqCode)` — synthetic result via `M4-PRE16/V2-Step12` activity-stack |
| 3 | `Activity.startIntentSenderForResult(...)` (2 overloads) | 3 | 15 | **18** | McD payments (PaymentLauncher → Stripe/PayPal IntentSender); Apptentive surveys | **stub-fail** — for V2-Step12 this is acceptable to keep fail-loud; few real callers, all McD optional flows |
| 4 | `Activity.requestPermissions(String[],int)` | 1 | 10 | **11** | McD location/camera/notification permissions; noice notification permission | **no-op + synthetic callback** — call `onRequestPermissionsResult(reqCode, perms, [PERMISSION_GRANTED…])` on main Looper |
| 5 | `Activity.shouldShowRequestPermissionRationale(String)` | 3 | 7 | **10** | Permission UI flow — called BEFORE the request to decide whether to show a rationale dialog | **return false** (constant) |
| 6 | `Activity.requestWindowFeature(int)` | 1 | 8 | **9** | AppCompatActivity's onCreate sets `FEATURE_ACTION_BAR` / `FEATURE_NO_TITLE`; subactivity dialogs request `FEATURE_NO_TITLE` | **return true** (constant) — V2 has no Window features to track |
| 7 | `Activity.createPendingResult(int,Intent,int)` | 0 | 8 | **8** | McD foreground-service notifications need `PendingIntent` from activity context | **return null + log warn** (V2 has no PendingIntent machinery; null is what AOSP gives if app uses an invalid request code) |
| 8 | `Activity.recreate()` | 6 | 2 | **8** | noice theme switch / config change response | **no-op** (V2 has no configuration changes; `mIsRecreating=false`) |
| 9 | `Activity.requestDragAndDropPermissions(DragEvent)` | 2 | 2 | **4** | both apps; called from `onDragEvent` in image-pickers | **return null** (default if no permission required) |
| 10 | `Activity.onRequestPermissionsResult(int,String[],int[])` | 1 | 1 | **2** | OVERRIDE method (apps override; framework calls it) — currently fail-loud because `requestPermissions` (#4) can't fire it | **no-op base** — promote to public empty body; apps will override |
| 11 | `Activity.getParentActivityIntent()` | 1 | 1 | **2** | Up-navigation; called by `NavUtils` | **return null** (AOSP default: returns null if no `parentActivityName` meta-data) |
| 12 | `Activity.navigateUpTo(Intent)` | 1 | 1 | **2** | `NavUtils.navigateUpFromSameTask` → `activity.navigateUpTo` | **delegate to `finish()` + log** — for single-Activity apps this is fine; multi-Activity (McD orderv2 NavHost) needs CR45 |
| 13 | `Activity.finishAndRemoveTask()` | 0 | 1 | **1** | McD "logout" flow at end of session | **delegate to `finish()`** — V2 has no task-stack concept |
| 14 | `Activity.setPictureInPictureParams(PictureInPictureParams)` | 0 | 1 | **1** | one McD landing page checks for PIP support | **no-op** (V2 has no PIP) |

**Bulk implementation cost:** Methods #1, #5, #6, #8, #9, #10, #11, #13, #14 are
**constant returns or no-ops** — total ~10 lines of code. Methods #4 + #2 + #12 are
**delegate-to-finish-or-AMS-binder** — ~30 lines each (already mostly written in
`WestlakeActivityManagerService`'s `startActivity` plumbing). Methods #3 + #7 can stay
fail-loud (low traffic, complex semantics, valid sandbox limitations).

**Effort:** 1 CR ≈ 60-90 min to batch-promote rows 1, 5-11, 13, 14 from fail-loud to
constants/no-ops. Rows 2 + 4 + 12 require M4-PRE16 / V2-Step12 multi-Activity dispatch
and should be a separate CR.

---

## §4. Hot framework methods (top 50 by combined call count)

This is the absolute hot path — what app + framework + AppCompat actually touch most.
Status column reflects current shim impl (per spot-check of source + CR32 audit log).

| # | Method | noice | McD | total | Status |
|---:|---|---:|---:|---:|---|
| 1 | `Context.getString(int)` | 97 | 3,249 | 3,346 | OK — `WestlakeContextImpl` → Resources → arsc parser |
| 2 | `SharedPreferences.getString(String,String)` | 8 | 1,760 | 1,768 | OK — `SharedPreferencesImpl` |
| 3 | `Context.getResources()` | 221 | 1,101 | 1,322 | OK — returns `WestlakeResources` |
| 4 | `BaseBundle.putString(String,String)` | 54 | 943 | 997 | OK |
| 5 | `Bundle.<init>()` | 140 | 819 | 959 | OK |
| 6 | `SharedPreferences.edit()` | 23 | 728 | 751 | OK |
| 7 | `Context.getSharedPreferences(String,int)` | 18 | 708 | 726 | OK |
| 8 | `Resources.getString(int)` | 82 | 638 | 720 | OK (CR30-B shadowed) |
| 9 | `LayoutInflater.inflate(int,ViewGroup,boolean)` | 65 | 653 | 718 | OK — `WestlakeLayoutInflater` |
| 10 | `Activity.getIntent()` | 45 | 670 | 715 | OK |
| 11 | `BaseBundle.getString(String)` | 53 | 644 | 697 | OK |
| 12 | `SharedPreferences$Editor.apply()` | 22 | 637 | 659 | OK |
| 13 | `Context.getApplicationContext()` | 70 | 551 | 621 | OK — routes to `WestlakeApplication` (Hilt-critical per §2.2 of WESTLAKE_ACTIVITY_API) |
| 14 | `TypedArray.recycle()` | 189 | 345 | 534 | OK |
| 15 | `BaseBundle.putInt(String,int)` | 199 | 312 | 511 | OK |
| 16 | `Context.getPackageName()` | 56 | 442 | 498 | OK |
| 17 | `Log.isLoggable(String,int)` | 150 | 339 | 489 | OK |
| 18 | `SharedPreferences$Editor.putString(...)` | 7 | 481 | 488 | OK |
| 19 | `Context.getSystemService(String)` | 78 | 401 | 479 | OK — `SystemServiceWrapperRegistry` (see §6) |
| 20 | `LayoutInflater.from(Context)` | 43 | 388 | 431 | OK |
| 21 | `Looper.getMainLooper()` | 48 | 383 | 431 | OK |
| 22 | `Context.getPackageManager()` | 40 | 377 | 417 | OK — returns `WestlakePackageManagerStub` |
| 23 | `BaseBundle.putBoolean(String,boolean)` | 0 | 414 | 414 | OK |
| 24 | `TypedArray.getInt(int,int)` | 162 | 240 | 402 | OK |
| 25 | `Resources.getDimensionPixelSize(int)` | 94 | 287 | 381 | OK (returns 0 if no value) |
| 26 | `TypedArray.getDimensionPixelSize(int,int)` | 153 | 225 | 378 | OK |
| 27 | `TypedArray.getResourceId(int,int)` | 137 | 233 | 370 | OK |
| 28 | `Resources.getDimension(int)` | 30 | 328 | 358 | OK (returns 0f if no value) |
| 29 | `BaseBundle.containsKey(String)` | 80 | 270 | 350 | OK |
| 30 | `TypedArray.getFloat(int,float)` | 122 | 217 | 339 | OK |
| 31 | `Context.getString(int,Object[])` | 14 | 147 | 161 | OK |
| 32 | `Context.getAssets()` | 5 | 144 | 149 | OK (CR30-B `getAssets` shadowed) |
| 33 | `Activity.finish()` | 18 | 229 | 247 | OK — V2 sets `mFinished=true`, calls binder `finishActivity(token)` |
| 34 | `Activity.setResult(int,Intent)` | 0 | 195 | 195 | OK (CR32 promoted to Implement: stores `mResultCode`+`mResultData`) |
| 35 | `Activity.getWindow()` | 59 | 141 | 200 | OK — returns lazy `PhoneWindow` stub |
| 36 | `Context.getApplicationInfo()` | 8 | 55 | 63 | OK (`WestlakeApplicationInfo`) |
| 37 | `Context.getContentResolver()` | 20 | 136 | 156 | OK (stub) |
| 38 | `Context.startActivity(Intent)` | 22 | 190 | 212 | OK — V2 routes to AMS binder (M4a `startActivity`); first 1-Activity launch works, intra-app launches need V2-Step12 |
| 39 | `Activity.getApplication()` | 19 | 102 | 121 | OK |
| 40 | `Window.getDecorView()` | 64 | 120 | 184 | OK — lazy `FrameLayout` per CR36 |
| 41 | `WindowManager.getDefaultDisplay()` | 3 | 63 | 66 | OK — returns `WestlakeDisplay` (deprecated; modern apps use WindowManager.currentWindowMetrics) |
| 42 | `Window.getAttributes()` | 2 | 46 | 48 | OK |
| 43 | `Window.setSoftInputMode(int)` | 0 | 26 | 26 | OK (no-op) |
| 44 | `Resources.getColor(int)` | 4 | 170 | 174 | OK (returns black by default) |
| 45 | `Resources.getDisplayMetrics()` | 55 | 162 | 217 | OK |
| 46 | `Resources.getConfiguration()` | 47 | 156 | 203 | OK |
| 47 | `Activity.runOnUiThread(Runnable)` | 0 | 49 | 49 | OK |
| 48 | `Activity.findViewById(int)` | 6 | 42 | 48 | OK |
| 49 | `Activity.getWindowManager()` | 1 | 29 | 30 | OK |
| 50 | `Activity.getLayoutInflater()` | 3 | 29 | 32 | OK |

**Every method in the top 50 is working** — this is why both apps progress through
their respective onCreate paths in CR36+. The remaining UOEs are at the long tail.

---

## §5. Recommended pre-emptive CRs

Batch implementation plan to avoid 6-8 reactive 25-60min CRs. All three are
architectural (no per-app branches; matches BINDER_PIVOT_DESIGN_V2 anti-drift
contract).

### CR41 (proposed) — Batch-promote 9 Activity fail-loud to constants/no-ops (~60 min)

Promote methods #1, #5, #6, #8, #9, #10, #11, #13, #14 from §3:

```
overridePendingTransition(int,int)          -> no-op            (CR32-style C)
shouldShowRequestPermissionRationale(String)-> return false     (C)
requestWindowFeature(int)                   -> return true      (C)
recreate()                                  -> no-op            (N) — log info, set flag
requestDragAndDropPermissions(DragEvent)    -> return null      (C)
onRequestPermissionsResult(int,...)         -> no-op            (N) — user override hook
getParentActivityIntent()                   -> return null      (C)
finishAndRemoveTask()                       -> delegate to finish() (D)
setPictureInPictureParams(...)              -> no-op            (N)
```

Touches: `shim/java/android/app/Activity.java` only.
Risk: low — all are AOSP-default-equivalent bodies.
Regression: should remain 12/13 PASS (PF-arch-054 is unrelated).

### CR42 (proposed) — Implement permission-grant synthesis (~45 min)

Promote `Activity.requestPermissions(String[],int)` from fail-loud to:

```java
public void requestPermissions(String[] permissions, int requestCode) {
    // V2 sandbox grants all permissions; synthesize a delivered result.
    int[] grants = new int[permissions.length];
    java.util.Arrays.fill(grants, 0); // PackageManager.PERMISSION_GRANTED
    if (mHandler != null) {
        final Activity self = this;
        mHandler.post(() -> self.onRequestPermissionsResult(
            requestCode, permissions, grants));
    }
}
```

Touches: `shim/java/android/app/Activity.java`.
Risk: low — AOSP's PermissionController does this asynchronously; synchronous-on-next-tick
delivery is sufficient for callers that don't depend on a real UI dialog. Both apps'
permission flows (noice POST_NOTIFICATIONS, McD location/camera/notif) are non-blocking;
the apps treat denial gracefully via the result callback.
Regression: should remain 12/13 PASS.

### CR43 (proposed) — Stub getSystemService for unimplemented service names (~30 min)

§6 below identifies 16+ service name literals neither app currently has a routed wrapper
for. Each `getSystemService("accessibility")` etc. should NOT return `null` silently —
that masks bugs. Instead `SystemServiceWrapperRegistry` should add explicit no-op stubs
for the high-traffic ones identified in §6 (accessibility, connectivity, audio, alarm,
uimode, clipboard) so apps that null-check on the system service get the right answer
(non-null stub with no-op methods).

Touches: `shim/java/com/westlake/services/SystemServiceWrapperRegistry.java` + 6 new
tiny stub classes (`WestlakeAccessibilityManager` etc.).

Risk: medium — accessibility/connectivity callers may behave unexpectedly if they assume
real state. Mitigation: per-service constant returns (accessibility.isEnabled=false,
connectivity.getActiveNetworkInfo=null, etc.).

### CR44 (proposed, GATED on M4-PRE16/V2-Step12) — Multi-Activity startActivityForResult dispatch

Implement `Activity.startActivityForResult(Intent,int[,Bundle])` (§3 row #2) and
`Activity.startIntentSenderForResult` (§3 row #3) and `Activity.navigateUpTo`
(§3 row #12). This is the gating CR for McD orderv2's `NavHostActivity` flows and
McD splash→dashboard transition (V2 §8.4 risk #8 from CR38). Estimated 1.0 person-day
because it crosses the Binder boundary into AMS for inter-Activity result delivery.

---

## §6. ServiceManager service-name literals (`getSystemService(String)` strings)

Extracted by smali register-flow analysis: for each `getSystemService(...)` call,
find the immediately-preceding `const-string` load into the parameter register.

### noice

| Service name | Calls | Currently routed in `SystemServiceWrapperRegistry`? |
|---|---:|---|
| `accessibility` | 13 | **NO** — first-call returns null (silent) |
| `input_method` | 8 | YES (CR32) |
| `window` | 7 | YES |
| `connectivity` | 6 | **NO** |
| `audio` | 5 | **NO** |
| `power` | 4 | YES |
| `notification` | 4 | YES |
| `layout_inflater` | 3 | YES |
| `uimode` | 3 | **NO** |
| `jobscheduler` | 3 | **NO** |
| `activity` | 3 | YES |
| `alarm` | 3 | **NO** |
| `phone` | 2 | **NO** |
| `locale` | 2 | **NO** |
| `clipboard` | 2 | **NO** |
| `location`, `captioning`, `display`, `media_session`, `appops`, `wifi`, `status_bar_height`, `media_metrics`, `vibrator` | 1 each | mostly NO; `display` YES |

### McD

| Service name | Calls | Currently routed? |
|---|---:|---|
| `accessibility` | 64 | **NO** — biggest miss |
| `connectivity` | 36 | **NO** |
| `activity` | 28 | YES |
| `input_method` | 27 | YES |
| `layout_inflater` | 26 | YES |
| `notification` | 23 | YES |
| `alarm` | 18 | **NO** |
| `uimode` | 18 | **NO** |
| `window` | 14 | YES |
| `location` | 14 | **NO** |
| `phone` | 12 | **NO** |
| `power`, `jobscheduler`, `clipboard` | 9 each | `power` YES; others NO |
| `camera`, `audio`, `sensor`, `display` | 4-7 each | only `display` YES |
| `batterymanager`, `keyguard`, `servicediscovery`, `wifi`, `dropbox`, `credential`, `download`, `user`, `appops`, `vibrator` | 1-2 each | NO |

**Pre-emptive routing recommendation** (§5 CR43 scope): add no-op stub services for
the top 6 missing: `accessibility`, `connectivity`, `alarm`, `uimode`, `clipboard`,
`location`. Their no-op stubs are 5-15 lines each (constant returns: `isEnabled()=false`,
`getActiveNetworkInfo()=null`, `set(...)=no-op`, `getCurrentModeType()=UI_MODE_TYPE_NORMAL`,
`getPrimaryClip()=null`, `requestLocationUpdates(...)=no-op`).

---

## §7. App-specific findings (informational only — NOT recommendations for per-app branches)

Per the anti-drift contract, app-specific surface differences inform priority ordering
ONLY — they do NOT justify per-app code paths. The shim implements architectural Android
API; both apps benefit from the same body.

### noice-only hot methods (≥5 calls, 0 in McD)

| Method | noice calls | Reason McD doesn't hit it |
|---|---:|---|
| `Log.{d,v,w,e,i}(String,String[,Throwable])` | 143/108/108/118/53 | McD R8-minified strips Log.d/Log.v at build time |
| `Bundle.putBoolean(String,boolean)` | 80 | McD uses `BaseBundle.putBoolean` (superclass) — same JVM dispatch target after vtable resolution |
| `Bundle.getBoolean(String[,boolean])` | 59 | McD uses `BaseBundle.getBoolean` — same situation |

Insight: noice retains debug-friendly bytecode (Log calls intact); McD aggressively
R8-minified its app code. **No per-app action required** — both Bundle and BaseBundle
are implemented in our shim.

### McD-only hot methods (top 15, 0 in noice)

| Method | McD calls | Insight |
|---|---:|---|
| `BaseBundle.putBoolean(String,boolean)` | 414 | (see above) |
| `Process.getElapsedCpuTime()` | 272 | Performance telemetry — Crashlytics/Firebase; **return 0L** is safe |
| `TypedValue.complexToFraction(int,float,float)` | 253 | Complex dimension parsing (animation specs); static utility — works as long as we expose it |
| `Process.getThreadPriority(int)` | 239 | Thread priority queries — return `THREAD_PRIORITY_DEFAULT` (0) |
| `Process.getGidForName(String)` | 226 | Unix GID lookup — return -1 (not found) is the AOSP behavior |
| `Activity.setResult(int,Intent)` | 195 | McD orderv2 NavHost return values — OK (CR32 implemented) |
| `BaseBundle.getBoolean(String[,boolean])` | 180+144 | OK |
| `Activity.setResult(int)` | 72 | OK (CR32) |
| `Activity.runOnUiThread(Runnable)` | 49 | OK |
| `SharedPreferences.getAll()` | 42 | OK |
| `DisplayMetrics.<init>()` | 37 | OK |
| `Display.getMetrics(DisplayMetrics)` | 34 | OK |
| `Activity.getLocalClassName()` | 31 | OK |
| `Activity.getWindowManager()` | 29 | OK |
| `Window.setSoftInputMode(int)` | 26 | OK (no-op) |

**The only fully-missing surface here is `Process.*`**: McD calls
`getElapsedCpuTime / getThreadPriority / getGidForName` heavily (737 combined calls).
The shim's `android.os.Process` should be spot-checked — if any of these throw, McD's
Crashlytics/perf-telemetry path will UOE.

---

## §8. Methodology & reproducibility

1. **APK source.** noice pulled from
   `/data/local/tmp/westlake/com_github_ashutoshgngwr_noice.apk` on cfb7c9e3
   (5.1 MB, 1 dex). McD from
   `/home/dspfac/android-to-openharmony-migration/artifacts/real-mcd/apk/com_mcdonalds_app.apk`
   (185 MB, 33 dex).

2. **Decompile.** Both APKs run through `apktool 2.9.3 d -f`. Smali output trees:
   - noice: `/tmp/cr40-noice/decoded/` — 5,832 `.smali` files
   - McD: `/home/dspfac/android-to-openharmony-migration/artifacts/real-mcd/apktool_decoded/`
     — 119,275 `.smali` files (pre-existing decompile from artifacts; re-verified for this CR)

3. **Invoke extraction.** Single grep pass across full smali tree:
   ```
   find $SMALI_ROOT -name "*.smali" | xargs -P4 grep -hoE \
     'L(android|androidx)/[a-zA-Z0-9_/$]+;->[a-zA-Z_$<>][a-zA-Z_$0-9<>]*\([^)]*\)[^[:space:]]+'
   ```
   Outputs: `/tmp/cr40-{noice,mcd}/scan_all_invokes.txt`.

4. **Count per unique (class,method).** `sort | uniq -c | sort -rn` produces a flat
   ranked table. noice = 6,872 unique methods, McD = 70,973.

5. **Filter to shim surface.** Regex match against the class list in §1 produces
   `scan_filtered.txt`: noice 591 methods / McD 834 methods.

6. **Combine + cross-reference.** Python parser merges per-method counts across apps;
   another Python pass cross-references against `activity_failloud.txt` extracted from
   `shim/java/android/app/Activity.java` (66 fail-loud methods). The 14-row §3 table
   is the **intersection**.

7. **Service literals.** Per-method register-tracking Python parser walks `.method`
   bodies, tracks `const-string vN, "literal"`, then on `getSystemService(...)`
   resolves the parameter register to its loaded string. Outputs top-N service name
   counts per app.

8. **Status cross-check.** Each method in §4's hot-50 was spot-checked against the
   shim source (`Activity.java`, `WestlakeContextImpl.java`, `Resources.java`,
   `Window.java`, `SystemServiceWrapperRegistry.java`) to confirm impl status — all
   50 hot methods are currently implemented.

**Scratch artifacts** (gitignored):
- `/tmp/cr40-noice/scan_filtered.txt`, `/tmp/cr40-noice/scan_all_invokes.txt`,
  `/tmp/cr40-noice/activity_failloud.txt`, `/tmp/cr40-noice/combined.json`
- `/tmp/cr40-mcd/scan_filtered.txt`, `/tmp/cr40-mcd/scan_all_invokes.txt`
- `/tmp/cr40-noice/scan.sh`, `/tmp/cr40-noice/filter.sh`, `/tmp/cr40-noice/svcname.py`,
  `/tmp/cr40-noice/predict.py`

---

## §9. Anti-drift compliance

- ZERO source code edits.
- ZERO Westlake-shim changes.
- 1 new doc (this file) + 1 small PHASE_1_STATUS.md row.
- No per-app branches recommended — all promotion candidates are architectural Android
  API defaults derived from AOSP-equivalent bodies.
- All call counts derived from static bytecode analysis of unmodified production APKs;
  no runtime instrumentation, no per-app heuristics.

---

## §10. Bottom line

- **14 Tier-1 promotion candidates** identified (§3) covering 158 combined call sites
  that will UOE-throw at runtime in their current fail-loud form.
- **9 of the 14 are constant/no-op promotions** (~10 LOC) batchable in a single 60-min
  CR (CR41 proposed in §5).
- **3 of the 14 are permission-flow synthesis** (~45 min) — CR42.
- **2 of the 14 are multi-Activity intent dispatch** — gated on V2-Step12 / M4-PRE16
  (CR44; aligns with CR38 risk register #8).
- **6 missing system services** (accessibility, connectivity, alarm, uimode, clipboard,
  location) — CR43, 30 min for no-op stub additions to `SystemServiceWrapperRegistry`.

Implementing CR41+CR42+CR43 pre-emptively replaces an estimated 8 reactive single-method
CRs (25-60 min each = 3-7h person-time) with one ~2.5h batch — net savings ~50% person-time
plus tighter regression flow (one round of 12/13 verification vs. 8 rounds).
