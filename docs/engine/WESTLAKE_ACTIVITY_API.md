# WestlakeActivity API Surface (V2 Step 1)

**Status:** Draft (2026-05-13) — read-only architect deliverable for `BINDER_PIVOT_DESIGN_V2.md` §7 Step 1
**Author:** Architect agent (no source touched; doc-only)
**Scope:** Public + protected methods of `android.app.Activity` as defined in AOSP Android 11 (API 30) at `/home/dspfac/aosp-android-11/frameworks/base/core/java/android/app/Activity.java`.
**Companion:** `BINDER_PIVOT_DESIGN_V2.md` §3.3 (decision 10-B classpath-shadow `WestlakeActivity`); `PHASE_1_STATUS.md` (V2-Step1 row).

---

## 0. Purpose

V2 §3.3 row-10 selected **Option 10-B**: `android.app.Activity` is added to `framework_duplicates.txt`; `aosp-shim.dex` provides `android.app.Activity` with a Westlake-controlled body. AppCompatActivity / FragmentActivity / ComponentActivity (from framework.jar / androidx) keep their bodies and call protected `Activity` API on our class.

The shadowing class must provide a method signature for every public + protected method of the original `Activity`, because:

- `super.foo()` from `ComponentActivity`/`FragmentActivity`/`AppCompatActivity`/app `MainActivity` must resolve in our class hierarchy.
- `instanceof Activity` checks pass (our class IS `android.app.Activity` — same FQCN, different body via classpath shadow).
- Reflective method lookups (`Activity.class.getMethod("startActivity", Intent.class)`) must succeed.

This table classifies every method into one of three implementation strategies for Step 2 (`WestlakeActivity` body):

- **Implement** — body has real logic; touched by at least one of {noice, McD, Counter} or required by Activity's own lifecycle invariants (e.g. `attach`, `performCreate`).
- **fail-loud** — body is `throw ServiceMethodMissing.fail("activity", "<method>")` (using the CR2 pattern). First app that hits it surfaces as a Tier-1 candidate for promotion to *Implement*.
- **no-op** — body is empty / returns a constant default. Mirrors framework `Activity` where the framework's own body is empty or a return-false-stub by design (lifecycle hooks the user is meant to override; constant getters that have no synthesizable answer in our model).

Speculative implementation is forbidden (V2 §10.3 "no almost framework"). Bias toward fail-loud; promote on the first real-world call. The Implement list is intentionally minimal.

---

## 1. Method count summary

| Source | Count |
|---|---|
| Lines in `Activity.java` | 8,879 |
| `public` (class-body level, indent=4) | 287 |
| `protected` (class-body level, indent=4) | 21 |
| **Total public + protected methods (declarations w/ `(`)** | **308** |
| Inner-class methods (`WindowControllerCallback` impl, etc., indent=8) | 5 |
| Package-private but architecturally critical (`attach`, `performXxx`) | 11 |
| **Total methods WestlakeActivity must declare** | **~324** |
| Constants (`RESULT_OK`, `RESULT_CANCELED`, ...) | 7 |
| Inherited from `Object` (not shadowed) | 11 |

### Per-category bucket sizes

| Bucket | Count | % |
|---|---|---|
| **Implement** | 87 | 27% |
| **fail-loud (ServiceMethodMissing)** | 178 | 55% |
| **No-op / default** | 47 | 14% |
| **Final (cannot be overridden in superclass; we still own the body)** | 12 | 4% |
| **Total** | **324** | 100% |

Numbers approximate; final/no-op overlap with implement/fail-loud where final methods need real bodies. See §3-§5 for the exhaustive lists.

---

## 2. Special cases flagged for Step 2

These methods need careful treatment that doesn't fit the three-bucket model. Step 2 (`WestlakeActivity` implementation) must address each:

### 2.1 `attach(...)` — both 6-arg and 17-arg variants

AOSP `Activity.attach` (Activity.java:7886) takes **18 parameters**:

```java
final void attach(Context context, ActivityThread aThread,
        Instrumentation instr, IBinder token, int ident,
        Application application, Intent intent, ActivityInfo info,
        CharSequence title, Activity parent, String id,
        NonConfigurationInstances lastNonConfigurationInstances,
        Configuration config, String referrer, IVoiceInteractor voiceInteractor,
        Window window, ActivityConfigCallback activityConfigCallback, IBinder assistToken)
```

`WestlakeActivityThread.attachActivity` currently calls a 6-arg synthetic shape (CR23-fix). V2 decision: `WestlakeActivity` declares **both** signatures (overload by arity). Both unify into a single private `attachInternal(...)` that records only the fields V2 keeps live:

- `mBase` ← `context` (the WestlakeContextImpl from Step 6)
- `mApplication` ← `application` (the WestlakeApplication from Step 3)
- `mIntent` ← `intent`
- `mComponent` ← `intent != null ? intent.getComponent() : null`
- `mInstrumentation` ← `instr` (may be null in 6-arg path)
- `mActivityInfo` ← `info` (may be null in 6-arg path)
- `mTitle` ← `title` (default `""`)
- `mWindow` ← null (V2 §3.5: no PhoneWindow machinery)
- `mWindowManager` ← null (V2 §3.5)

Notably **not** recorded: `mMainThread`, `mUiThread`, `mFragments` (the framework `FragmentController`), `mLoadedApk`, `mLastNonConfigurationInstances`, `mVoiceInteractor`, `mCurrentConfig` (the framework cold-init grab-bag).

`attach` is *package-private* in framework but `WestlakeActivity` must expose it as `public` to be callable from `WestlakeActivityThread` (different package).

### 2.2 `getApplicationContext()`

Returns the attached `WestlakeApplication` instance (set during `attach`), NOT `mBase`. This matters because Hilt's `dagger.hilt.android.internal.Contexts.getApplication(Context)` walks the context chain via `getApplicationContext()` looking for an `Application` instance (M4-PRE11 root cause).

Note: in framework `Activity`, `getApplicationContext()` is inherited from `ContextWrapper` which delegates to `mBase.getApplicationContext()`. Since our shadowed `Context` (CR23-fix) already routes `getApplicationContext()` to the attached Application via `WestlakeContextImpl.setAttachedApplication()`, we can either (a) inherit the same default behavior from our shadowed `ContextWrapper`, or (b) override here for clarity. Step 2 should override (clarity > clever inheritance for this load-bearing path).

### 2.3 `getBaseContext()`

Returns `mBase` per the standard `ContextWrapper.mBase` pattern. `mBase` is set by `attachBaseContext(Context)` and *also* by `attach(...)` (V2's combined attach calls `attachBaseContext` internally).

### 2.4 `getResources()`

Returns `mBase.getResources()` which is `WestlakeResources` (Step 4 deliverable). Do **not** maintain a separate `mResources` field on the Activity — single source of truth on `mBase`.

### 2.5 `getSystemService(String)`

Delegates to `mBase.getSystemService(name)` which is `WestlakeContextImpl.getSystemService` (already routed to ServiceManager via the CR3-5 `SystemServiceWrapperRegistry`).

### 2.6 `getMenuInflater()`

Lower-priority. Initial impl: **fail-loud**. Promote to Implement when first app hits it. Both noice and McD use AppCompat's own MenuInflater path which goes through `getSupportActionBar()` — they probably never reach this method. The Hilt path doesn't need it.

### 2.7 `requestPermissions` / `checkSelfPermission` / `shouldShowRequestPermissionRationale`

Initial impl: **fail-loud**. The binder permission service can handle these later. Note: `checkSelfPermission` is inherited from `Context`, not declared on Activity — it appears in the method list as `requestPermissions(String[], int)`. `shouldShowRequestPermissionRationale` is on Activity (line 5255).

### 2.8 `startActivity` family (8 overloads + `startActivityForResult` family of ~10)

V2 §8.4 flagged multi-Activity Intent handling as HIGH risk and explicitly punted to a later milestone. Initial impl: **fail-loud** for all `startActivity*` / `startActivityForResult*` / `startIntentSender*` / `startActivityFromChild` / `startActivityFromFragment`. noice and McD are single-Activity apps (verified via apktool-decoded AndroidManifest.xml in CR27); they don't need it for first-render. McD's SplashActivity → MainActivity transition WILL hit this and that is exactly the next M4-PRE16 / V2-Step12 discovery boundary.

### 2.9 `findViewById(int)`

V2 §8.5 flagged as a perf concern. Initial impl: linear traversal of `mContentView` (the root inflated `FrameLayout`). Step 2 ships the simple version; if profiling shows it's hot, Step 12+ can add a HashMap cache invalidated on `setContentView`.

### 2.10 `setTheme(int)`

V2 §3.5 row-12 says theme is not load-bearing for first render. Initial impl: store the resid; no Window machinery to push to. `getTheme()` returns a Westlake-owned `Theme` from `WestlakeResources` (Step 4 deliverable).

### 2.11 `onCreate(Bundle)` / `onCreate(Bundle, PersistableBundle)`

These are **user-override hooks**. Framework's body does fragment-restore + autofill setup, but neither applies in V2. Implement: empty body (the user's `MainActivity.onCreate(Bundle)` runs first and calls `super.onCreate(...)` which lands here; no-op is correct).

Caveat: `mCalled = true` MUST be set in our body if `super.onCreate()` runs through us. Framework uses `mCalled` to detect missing-super-call. Our `performCreate` checks `mCalled` post-call. Step 2 sets `mCalled = true` in **every** lifecycle method body before returning.

### 2.12 `performCreate` / `performStart` / `performResume` / `performPause` / `performStop` / `performDestroy`

Package-private lifecycle drivers. WestlakeActivity owns them. Each:
1. Calls the corresponding user-overridable `onXxx()` (which dispatches to user's MainActivity).
2. Calls registered `ActivityLifecycleCallbacks` (which `WestlakeApplication` aggregates).
3. Updates `mCalled` invariant tracking (or skips it — see §2.11).

These are the entry points from `WestlakeActivityThread.handleLaunchActivity` (Step 6 wires this).

### 2.13 `getFragmentManager()` (deprecated) / native `Activity` has no `getSupportFragmentManager`

`getSupportFragmentManager()` is on `androidx.fragment.app.FragmentActivity`, NOT on `Activity`. Our shadow only owns `Activity`; FragmentActivity is unmodified framework.jar code. So this concern is offloaded. `getFragmentManager()` (the deprecated platform one) returns null for Westlake (initial); promote on first call.

### 2.14 Final methods — we still own the body via class shadowing

Framework `Activity` declares ~12 methods as `final` (e.g. `getApplication`, `isChild`, `getParent`, `requestShowKeyboardShortcuts`, `dismissKeyboardShortcutsHelper`, `setDefaultKeyMode`, `requestPermissions`, `getActivityToken`, `getAssistToken`, `getActivityThread`, `setResult`, `runOnUiThread`, `autofillClient*` family). The `final` modifier prevents subclasses from overriding — but it doesn't prevent us from *replacing the class entirely* via classpath shadow. Step 2 keeps these `final` (preserves the API contract) but with Westlake bodies. None of them appear in user code's overridable surface, so no source-compat risk.

---

## 3. Implement bucket (87 methods)

These methods have real implementation in `WestlakeActivity`. Categorization rationale comes from one of:

- **(noice)** — observed in noice-discover.sh PHASE D/E/G logs or in apktool-decoded noice classes.dex.
- **(McD)** — observed in mcd-discover.sh logs or apktool-decoded McD bytecode (Hilt-DI / RxJava / Realm chain).
- **(Counter)** — required by AppCompatActivity's `super.onCreate` chain regardless of app.
- **(lifecycle)** — required by Activity's own lifecycle invariants (`attach`, `performCreate`, etc.).
- **(universal)** — every non-trivial Activity user calls this.

| # | Method (line) | Category | Rationale |
|---|---|---|---|
| 1 | `getIntent()` (1014) | Implement | universal; both apps |
| 2 | `setIntent(Intent)` (1028) | Implement | both apps; Activity restart paths |
| 3 | `getApplication()` (1069, final) | Implement | universal; returns `mApplication` |
| 4 | `getWindowManager()` (1084) | Implement | universal; returns stub (V2 §3.5) |
| 5 | `getWindow()` (1096) | Implement | universal; returns stub (V2 §3.5) |
| 6 | `getCurrentFocus()` (1120) | Implement | walks `mContentView` for focused view; falls back to null |
| 7 | `attachBaseContext(Context)` (1232, protected) | Implement | (lifecycle) sets `mBase` |
| 8 | `registerActivityLifecycleCallbacks(...)` (1282) | Implement | (lifecycle); store list; invoke from `performXxx` |
| 9 | `unregisterActivityLifecycleCallbacks(...)` (1297) | Implement | pair w/ register |
| 10 | `onCreate(Bundle)` (1562, protected) | Implement (empty) | (lifecycle) — user override; we set `mCalled=true` |
| 11 | `onCreate(Bundle, PersistableBundle)` (1618) | Implement (delegates to 1562) | API 21+ variant; delegate to `onCreate(Bundle)` |
| 12 | `onRestoreInstanceState(Bundle)` (1675, protected) | Implement (no-op) | (lifecycle); no saved-state in V2 |
| 13 | `onRestoreInstanceState(Bundle, PersistableBundle)` (1709) | Implement (delegate) | API 21+ variant |
| 14 | `onPostCreate(Bundle)` (1781, protected) | Implement (no-op) | (lifecycle); user override |
| 15 | `onPostCreate(Bundle, PersistableBundle)` (1803) | Implement (delegate) | API 21+ variant |
| 16 | `onStart()` (1827, protected) | Implement (empty) | (lifecycle); user override; `mCalled=true` |
| 17 | `onRestart()` (1861, protected) | Implement (empty) | (lifecycle) |
| 18 | `onStateNotSaved()` (1877) | Implement (no-op) | (lifecycle) |
| 19 | `onResume()` (1903, protected) | Implement (empty) | (lifecycle); user override; `mCalled=true` |
| 20 | `onPostResume()` (1942, protected) | Implement (empty) | (lifecycle) |
| 21 | `onNewIntent(Intent)` (2150, protected) | Implement (empty) | (lifecycle); user override |
| 22 | `onSaveInstanceState(Bundle)` (2238, protected) | Implement (no-op) | (lifecycle); no state save in V2 |
| 23 | `onSaveInstanceState(Bundle, PersistableBundle)` (2268) | Implement (delegate) | API 21+ variant |
| 24 | `onPause()` (2345, protected) | Implement (empty) | (lifecycle); user override; `mCalled=true` |
| 25 | `onStop()` (2587, protected) | Implement (empty) | (lifecycle); user override; `mCalled=true` |
| 26 | `onDestroy()` (2635, protected) | Implement (empty) | (lifecycle); user override; `mCalled=true` |
| 27 | `onConfigurationChanged(Configuration)` (2963) | Implement (empty) | (lifecycle); user override |
| 28 | `onLowMemory()` (3146) | Implement (empty) | (lifecycle); `mCalled=true` |
| 29 | `onTrimMemory(int)` (3152) | Implement (empty) | (lifecycle); `mCalled=true` |
| 30 | `getFragmentManager()` (3165) | Implement (return null) | deprecated; AppCompat doesn't use; promote on fail-loud-hit |
| 31 | `onAttachFragment(Fragment)` (3178) | Implement (empty) | platform Fragment hook; androidx doesn't go through this |
| 32 | `findViewById(int)` (3352, generic) | Implement | (universal) — linear traversal of mContentView |
| 33 | `requireViewById(int)` (3372, final, generic) | Implement | (noice + McD); calls findViewById, throws IllegalArgumentException if null |
| 34 | `getActionBar()` (3386) | Implement (return null) | both apps; AppCompatDelegate handles via getSupportActionBar |
| 35 | `setContentView(int)` (3467) | Implement | (universal); inflate via WestlakeLayoutInflater into mContentView |
| 36 | `setContentView(View)` (3487) | Implement | (universal); set mContentView = view |
| 37 | `setContentView(View, LayoutParams)` (3503) | Implement | (universal, McD); set mContentView = view; ignore params (no window) |
| 38 | `addContentView(View, LayoutParams)` (3515) | Implement | append to mContentView if it's a ViewGroup |
| 39 | `onBackPressed()` (3821) | Implement (call finish) | (universal user override); default body = finish() |
| 40 | `onTouchEvent(MotionEvent)` (3873) | Implement (return false) | (lifecycle); user override |
| 41 | `onUserInteraction()` (3948) | Implement (empty) | (lifecycle); user override hook |
| 42 | `onContentChanged()` (3966) | Implement (empty) | (lifecycle); user override hook |
| 43 | `onWindowFocusChanged(boolean)` (4009) | Implement (empty) | (lifecycle); user override hook |
| 44 | `onAttachedToWindow()` (4019) | Implement (empty) | (lifecycle); user override hook |
| 45 | `onDetachedFromWindow()` (4029) | Implement (empty) | (lifecycle); user override hook |
| 46 | `hasWindowFocus()` (4040) | Implement (return true) | (universal); we have no real window concept; report focused |
| 47 | `dispatchKeyEvent(KeyEvent)` (4073) | Implement (return false) | input plumbing not wired yet; promote when input service lands |
| 48 | `dispatchTouchEvent(MotionEvent)` (4121) | Implement (return false) | input plumbing not wired yet |
| 49 | `onCreateOptionsMenu(Menu)` (4366) | Implement (return true) | (lifecycle); user override hook; framework's default returns true |
| 50 | `onPrepareOptionsMenu(Menu)` (4391) | Implement (return true) | (lifecycle); framework's default returns true |
| 51 | `onOptionsItemSelected(MenuItem)` (4416) | Implement (return false) | (lifecycle); user override hook |
| 52 | `onOptionsMenuClosed(Menu)` (4536) | Implement (empty) | (lifecycle); user override hook |
| 53 | `onCreateContextMenu(...)` (4577) | Implement (empty) | (lifecycle); user override hook |
| 54 | `registerForContextMenu(View)` (4590) | Implement (empty) | View-event registration; no-op until input arrives |
| 55 | `unregisterForContextMenu(View)` (4601) | Implement (empty) | pair w/ register |
| 56 | `onContextItemSelected(MenuItem)` (4642) | Implement (return false) | (lifecycle); user override hook |
| 57 | `onContextMenuClosed(Menu)` (4656) | Implement (empty) | (lifecycle); user override hook |
| 58 | `onSearchRequested()` (4918) | Implement (return false) | (lifecycle); user override hook; default = no search |
| 59 | `onSearchRequested(SearchEvent)` (4908) | Implement (delegates) | API 23+ variant |
| 60 | `getLayoutInflater()` (5059) | Implement | (universal); returns the WestlakeLayoutInflater used by setContentView |
| 61 | `getMenuInflater()` (5067) | Implement (lazy WestlakeMenuInflater stub) | promoted from fail-loud if first call from AppCompat |
| 62 | `setTheme(int)` (5081) | Implement | store resid; no Window push (V2 §3.5) |
| 63 | `startActivity(Intent)` (5610) | Implement (delegate to startActivityForResult) | (lifecycle); core entry; routes through ActivityManagerService binder (M4a) |
| 64 | `startActivity(Intent, Bundle)` (5637) | Implement (delegate) | API 16+ variant |
| 65 | `isFinishing()` (6310) | Implement (return mFinished) | (lifecycle); accessor for mFinished flag |
| 66 | `isDestroyed()` (6318) | Implement (return mDestroyed) | (lifecycle); accessor |
| 67 | `isChangingConfigurations()` (6331) | Implement (return false) | (lifecycle); no config changes in V2 |
| 68 | `finish()` (6405) | Implement | (universal); set `mFinished=true`; call M4a `finishActivity(token)` |
| 69 | `finishAffinity()` (6423) | Implement (delegate to finish) | (McD splash → main); strict version unneeded |
| 70 | `finishAfterTransition()` (6461) | Implement (delegate to finish) | (universal); no transition in V2 |
| 71 | `onActivityResult(int, int, Intent)` (6566, protected) | Implement (empty) | (lifecycle); user override hook |
| 72 | `getTaskId()` (6686) | Implement (return mIdent) | (universal); identity for binder routing |
| 73 | `isTaskRoot()` (6700) | Implement (return true) | (lifecycle); single-task model in V2 |
| 74 | `getLocalClassName()` (6731) | Implement | (universal); extract from mComponent |
| 75 | `getComponentName()` (6747) | Implement (return mComponent) | (universal); set during attach |
| 76 | `getPreferences(int)` (6775) | Implement | (noice — preferences-heavy app); delegate to `mBase.getSharedPreferences(getLocalClassName(), mode)` |
| 77 | `getSystemService(String)` (6792) | Implement | (universal); delegate to mBase |
| 78 | `setTitle(CharSequence)` (6813) | Implement | (lifecycle); store mTitle |
| 79 | `setTitle(int)` (6828) | Implement (delegate to setTitle(CharSequence)) | (lifecycle) |
| 80 | `getTitle()` (6848, final) | Implement | (universal); return mTitle |
| 81 | `getTitleColor()` (6852, final) | Implement (return Color.BLACK) | (lifecycle); no UI chrome to color |
| 82 | `runOnUiThread(Runnable)` (7058, final) | Implement | (noice — RxJava observe on main); post to main Looper via Handler |
| 83 | `getActivityToken()` (7962, final) | Implement | (lifecycle); return mToken (synthetic IBinder) |
| 84 | `getActivityThread()` (7973, final) | Implement | (lifecycle); return WestlakeActivityThread.currentActivityThread() |
| 85 | `attach(...)` (7886, 18-arg, package-private) | Implement (public in our shadow) | (lifecycle) — see §2.1 |
| 86 | `attach(...)` (6-arg, ADDED for V2) | Implement (public in our shadow) | (lifecycle) — see §2.1; the existing WestlakeActivityThread.attachActivity call site (CR23-fix) |
| 87 | `performCreate(Bundle)` (7977, final) + `performCreate(Bundle, PersistableBundle)` (7982) + `performStart(String)` (8012) + `performRestart(boolean, String)` (8064) + `performResume(boolean, String)` (8109) + `performPause()` (8163) + `performStop(boolean, String)` (8186) + `performDestroy()` (8234) + `performNewIntent(Intent)` (8007) + `performUserLeaving()` (8181) + `performSaveInstanceState(Bundle)` (2161) + `performSaveInstanceState(Bundle, PersistableBundle)` (2180) + `performRestoreInstanceState(Bundle)` (1631) + `performRestoreInstanceState(Bundle, PersistableBundle)` (1645) | Implement (one row collapses 14 sibling methods) | (lifecycle) — see §2.12; each just calls the corresponding `onXxx` |

(Methods 85-87 count as ~16 individual method declarations, but they are mechanical sibling drivers — Step 2 will likely write them as a single helper + 16 thin wrappers. The bucket count in §1 reflects the per-method count.)

---

## 4. fail-loud bucket (178 methods)

These methods throw `ServiceMethodMissing.fail("activity", "<name>")` on first call. The vast majority are domains we have no infrastructure for (autofill, VR mode, picture-in-picture, voice interaction, assist, lock-task, content capture, multi-window, translucent conversion, drag-and-drop permissions, remote animations).

The fail-loud throw IS the discovery signal: the first call from a real app surfaces as a Tier-1 candidate. Without fail-loud, returning null/false/0 (the M4-PRE5/CR19 anti-pattern) would mask the call.

**Compactly listed by domain.** Each is `throw ServiceMethodMissing.fail("activity", "<methodName>")`:

### 4.1 Autofill (16 methods)

`getAutofillClient` (1242, final), `getContentCaptureClient` (1248, final), `autofillClientGetNextAutofillId` (2027), `getNextAutofillId` (2013), `autofillClientGetComponentName` (6753, final), `contentCaptureClientGetComponentName` (6759, final), `autofillClientRunOnUiThread` (7068, final), `autofillClientAuthenticate` (8475, final), `autofillClientResetableStateAvailable` (8487, final), `autofillClientRequestShowFillUi` (8493, final), `autofillClientDispatchUnhandledKey` (8510, final), `autofillClientRequestHideFillUi` (8523, final), `autofillClientIsFillUiShowing` (8534, final), `autofillClientFindViewsByAutofillIdTraversal` (8541, final), `autofillClientFindViewByAutofillIdTraversal` (8567, final), `autofillClientGetViewVisibility` (8586, final), `autofillClientFindViewByAccessibilityIdTraversal` (8608, final), `autofillClientGetActivityToken` (8626, final), `autofillClientIsVisibleForAutofill` (8632, final), `autofillClientIsCompatibilityModeEnabled` (8638, final), `isDisablingEnterExitEventForAutofill` (8644, final).

### 4.2 Picture-in-picture (8 methods)

`onPictureInPictureModeChanged(boolean, Configuration)` (2766), `onPictureInPictureModeChanged(boolean)` (2783), `isInPictureInPictureMode` (2794), `enterPictureInPictureMode()` (2807), `enterPictureInPictureMode(PictureInPictureParams)` (2834), `setPictureInPictureParams` (2864), `getMaxNumPictureInPictureActions` (2883), `onPictureInPictureRequested` (2912).

### 4.3 Multi-window (3 methods)

`onMultiWindowModeChanged(boolean, Configuration)` (2723), `onMultiWindowModeChanged(boolean)` (2740), `isInMultiWindowMode` (2751).

### 4.4 Voice interaction / assist (12 methods)

`isVoiceInteraction` (2036), `isVoiceInteractionRoot` (2051), `getVoiceInteractor` (2064), `isLocalVoiceInteractionSupported` (2075), `startLocalVoiceInteraction(Bundle)` (2089), `onLocalVoiceInteractionStarted` (2101), `onLocalVoiceInteractionStopped` (2110), `stopLocalVoiceInteraction` (2118), `onCreateThumbnail(Bitmap, Canvas)` (2389), `onCreateDescription` (2410), `onProvideAssistData(Bundle)` (2425), `onProvideAssistContent(AssistContent)` (2447), `showAssist(Bundle)` (2563).

### 4.5 Direct actions / keyboard shortcuts (5 methods)

`onGetDirectActions(...)` (2478), `onPerformDirectAction(...)` (2497), `requestShowKeyboardShortcuts` (2505, final), `dismissKeyboardShortcutsHelper` (2517, final), `onProvideKeyboardShortcuts(...)` (2527).

### 4.6 Permission family (3 methods)

`requestPermissions(String[], int)` (5208, final), `onRequestPermissionsResult(int, String[], int[])` (5240), `shouldShowRequestPermissionRationale(String)` (5255). Promote when binder permission service lands.

### 4.7 startActivity / startActivityForResult family (~21 methods)

`startActivityForResult(Intent, int)` (5271), `startActivityForResult(Intent, int, Bundle)` (5309), `isActivityTransitionRunning` (5369), `startActivityForResultAsUser(Intent, int, UserHandle)` (5388), `startActivityForResultAsUser(Intent, int, Bundle, UserHandle)` (5395), `startActivityForResultAsUser(Intent, String, int, Bundle, UserHandle)` (5403), `startActivityAsUser(Intent, UserHandle)` (5434), `startActivityAsUser(Intent, Bundle, UserHandle)` (5441), `startActivityAsCaller(Intent, Bundle, boolean, int)` (5474), `startIntentSenderForResult(IntentSender, int, Intent, int, int, int)` (5507), `startIntentSenderForResult(IntentSender, int, Intent, int, int, int, Bundle)` (5539), `startActivities(Intent[])` (5674), `startActivities(Intent[], Bundle)` (5701), `startIntentSender(IntentSender, Intent, int, int, int)` (5719), `startIntentSender(IntentSender, Intent, int, int, int, Bundle)` (5746), `startActivityIfNeeded(Intent, int)` (5775), `startActivityIfNeeded(Intent, int, Bundle)` (5810), `startNextMatchingActivity(Intent)` (5863), `startNextMatchingActivity(Intent, Bundle)` (5886), `startActivityFromChild(Activity, Intent, int)` (5920), `startActivityFromChild(Activity, Intent, int, Bundle)` (5947), `startActivityFromFragment(Fragment, Intent, int)` (5979), `startActivityFromFragment(Fragment, Intent, int, Bundle)` (6008), `startActivityForResult(...) protected wrapper` (6024), `canStartActivityForResult` (6047), `startIntentSenderFromChild(...)` (6058), `startIntentSenderFromChild(...)` (6076), `overridePendingTransition(int, int)` (6114). All promote together when M4-PRE16/V2-Step12 lands multi-Activity support.

### 4.8 Result / pending intent (5 methods)

`setResult(int)` (6134, final), `setResult(int, Intent)` (6163, final), `getReferrer` (6187), `onProvideReferrer` (6214), `getCallingPackage` (6239), `getCallingActivity` (6262), `setVisible(boolean)` (6280), `onActivityReenter(int, Intent)` (6587), `createPendingResult(int, Intent, int)` (6617), `finishActivity(int)` (6476), `finishFromChild(Activity)` (6450), `finishActivityFromChild(Activity, int)` (6499), `finishAndRemoveTask` (6512), `releaseInstance` (6527).

### 4.9 Task / orientation / display (10 methods)

`setRequestedOrientation(int)` (6643), `getRequestedOrientation` (6666), `moveTaskToBack(boolean)` (6715), `setTaskDescription(TaskDescription)` (6886), `recreate` (6341), `setLocusContext(LocusId, Bundle)` (1056), `onTopResumedActivityChanged(boolean)` (1969), `onMovedToDisplay(int, Configuration)` (2944), `getChangingConfigurations` (2995), `getLastNonConfigurationInstance` (3020), `onRetainNonConfigurationInstance` (3077).

**CR30-A (2026-05-13) reclassification:** `getLastNonConfigurationInstance` and `onRetainNonConfigurationInstance` are now Tier-1 real implementations returning `null` (cold-start AOSP semantics; matches AOSP default when `mLastNonConfigurationInstances == null` / no subclass override). Promoted out of the fail-loud bucket because `androidx.activity.ComponentActivity.ensureViewModelStore()` calls `getLastNonConfigurationInstance` unconditionally on every Activity construction — null is the correct return on first launch with no rotation history.

**CR32 (2026-05-13) bulk reclassification:** ~82 Activity methods promoted from fail-loud to Implement / no-op in a single batch (see `CR32_AUDIT_LOG.md` for the per-method table). Categories:
- Lifecycle hooks (`onCreateThumbnail`, `onCreateDescription`, `onProvideAssistData`, `onProvideAssistContent`, `onActivityReenter`, `onMultiWindowModeChanged*2`, `onPictureInPictureModeChanged*2`, `onMovedToDisplay`, `onActionModeStarted/Finished`, `onEnterAnimationComplete`, `dispatchEnterAnimationComplete`, `onChildTitleChanged`, `onWindowAttributesChanged`, `onWindowDismissed`, `onBackgroundVisibleBehindChanged`, `onVisibleBehindCanceled`) -> AOSP-empty no-ops.
- Input/dispatch (`onKeyDown/Up/LongPress/Multiple/Shortcut`, `dispatchKeyShortcutEvent`, `onTrackballEvent`, `onGenericMotionEvent`, `dispatchTrackballEvent`, `dispatchGenericMotionEvent`) -> AOSP defaults return false.
- Panel/menu (`onCreatePanelView` -> null, `onCreatePanelMenu` -> false, `onPreparePanel`/`onMenuOpened` -> true, `onMenuItemSelected` -> false, `onPanelClosed` -> no-op, `closeOptionsMenu`/`openOptionsMenu`/`openContextMenu`/`closeContextMenu`/`invalidateOptionsMenu`) -> AOSP-default no-ops.
- Inflation/dump/decor (`onCreateView*2` -> null, `dump` -> short marker, `setActionBar`/`getContentTransitionManager`/`setContentTransitionManager`/`getContentScene`/`postponeEnterTransition`/`startPostponedEnterTransition`/`setEnterSharedElementCallback`/`setExitSharedElementCallback`/`isOverlayWithDecorCaptionEnabled`/`setOverlayWithDecorCaptionEnabled`) -> null / no-op.
- Translucent/immersive/VR/lock/locus (`setImmersive`, `convertFromTranslucent`, `setTranslucent` -> true, `setVrModeEnabled`, `setShowWhenLocked`, `setInheritShowWhenLocked`, `setTurnScreenOn`, `setLocusContext`, `setTaskDescription`, `setPersistent`, `takeKeyEvents`, `setFinishOnTouchOutside`, `reportFullyDrawn`, `overridePendingTransition`, `setVisible`, `isBackgroundVisibleBehind` -> false, `requestVisibleBehind` -> false) -> sandbox no-ops.
- Dialogs/loader/navigate (`onCreateDialog*2` -> null, `onPrepareDialog*2` -> no-op, `getLoaderManager` -> null, `onNavigateUp` -> false, `onNavigateUpFromChild` -> delegate, `onCreateNavigateUpTaskStack`/`onPrepareNavigateUpTaskStack` -> no-op, `shouldUpRecreateTask` -> false).
- Action mode (`onWindowStartingActionMode*2` -> null) and `getChangingConfigurations` -> 0.

The remaining ~81 fail-loud Activity methods are those with genuinely-unimplementable binder semantics (autofill, voice interaction, content capture, intent-sender families, finish-from-child, drag-and-drop, lock task, search, voice/assist start, request permissions). See file header of `shim/java/android/app/Activity.java` for the rationale on what stayed fail-loud.

### 4.10 Cursor management (deprecated, 4 methods)

`managedQuery(Uri, String[], String, String)` (3213, final), `managedQuery(Uri, String[], String, String[], String)` (3253, final), `startManagingCursor(Cursor)` (3290), `stopManagingCursor(Cursor)` (3314), `setPersistent(boolean)` (3334).

### 4.11 Action bar / toolbar (1 method, plus action mode 4)

`setActionBar(Toolbar)` (3406), `startActionMode(Callback)` (7588), `startActionMode(Callback, int)` (7602), `onWindowStartingActionMode(Callback)` (7619), `onWindowStartingActionMode(Callback, int)` (7635), `onActionModeStarted(ActionMode)` (7652), `onActionModeFinished(ActionMode)` (7663).

### 4.12 Transition (5 methods)

`getContentTransitionManager` (3529), `setContentTransitionManager(TransitionManager)` (3539), `getContentScene` (3551), `setEnterSharedElementCallback(SharedElementCallback)` (7812), `setExitSharedElementCallback(SharedElementCallback)` (7828), `postponeEnterTransition` (7850), `startPostponedEnterTransition` (7859), `dispatchEnterAnimationComplete` (7480), `onEnterAnimationComplete` (7474).

### 4.13 Dialogs (deprecated, 6 methods)

`onCreateDialog(int)` (4666, protected), `onCreateDialog(int, Bundle)` (4705, protected), `onPrepareDialog(int, Dialog)` (4714, protected), `onPrepareDialog(int, Dialog, Bundle)` (4743, protected), `showDialog(int)` (4757, final), `showDialog(int, Bundle)` (4792, final), `dismissDialog(int)` (4830, final), `removeDialog(int)` (4874, final).

### 4.14 Search (5 methods)

`getSearchEvent` (4937, final), `startSearch(String, boolean, Bundle, boolean)` (4974), `triggerSearch(String, Bundle)` (4991), `takeKeyEvents(boolean)` (5003), `setFinishOnTouchOutside(boolean)` (3559), `setDefaultKeyMode(int)` (3638, final).

### 4.15 Window feature flags (4 methods)

`requestWindowFeature(int)` (5018, final), `setFeatureDrawableResource(int, int)` (5026, final), `setFeatureDrawableUri(int, Uri)` (5034, final), `setFeatureDrawable(int, Drawable)` (5042, final), `setFeatureDrawableAlpha(int, int)` (5050, final), `onApplyThemeResource(Theme, int, boolean)` (5087, protected).

### 4.16 Progress bar (5 methods, all deprecated)

`setProgressBarVisibility(boolean)` (6913, final), `setProgressBarIndeterminateVisibility(boolean)` (6928, final), `setProgressBarIndeterminate(boolean)` (6944, final), `setProgress(int)` (6962, final), `setSecondaryProgress(int)` (6981, final).

### 4.17 Volume control / media controller (4 methods)

`setVolumeControlStream(int)` (7002, final), `getVolumeControlStream` (7014, final), `setMediaController(MediaController)` (7036, final), `getMediaController` (7047, final).

### 4.18 Inflation / dump (3 methods)

`onCreateView(String, Context, AttributeSet)` (7084), `onCreateView(View, String, Context, AttributeSet)` (7100), `dump(String, FileDescriptor, PrintWriter, String[])` (7119).

### 4.19 Immersive / translucent / VR (8 methods)

`isImmersive` (7210), `setImmersive(boolean)` (7504), `setTranslucent(boolean)` (7246), `convertFromTranslucent` (7267), `convertToTranslucent(TranslucentConversionListener, ActivityOptions)` (7306), `onNewActivityOptions(ActivityOptions)` (7339), `requestVisibleBehind(boolean)` (7401), `onVisibleBehindCanceled` (7423), `isBackgroundVisibleBehind` (7444), `onBackgroundVisibleBehindChanged(boolean)` (7466), `setVrModeEnabled(boolean, ComponentName)` (7566).

### 4.20 Navigate up (5 methods)

`onNavigateUp` (4446), `onNavigateUpFromChild(Activity)` (4487), `onCreateNavigateUpTaskStack(TaskStackBuilder)` (4510), `onPrepareNavigateUpTaskStack(TaskStackBuilder)` (4526), `shouldUpRecreateTask(Intent)` (7679), `navigateUpTo(Intent)` (7720), `navigateUpToFromChild(Activity, Intent)` (7766), `getParentActivityIntent` (7782).

### 4.21 Drag-and-drop (1 method)

`requestDragAndDropPermissions(DragEvent)` (7870).

### 4.22 Resume confirmation (1 method)

`isResumed` (8279, final) — actually load-bearing for some Compose paths; on first hit promote to Implement.

### 4.23 Lock task / disable preview / show-when-locked (7 methods)

`startLockTask` (8361), `stopLockTask` (8384), `showLockTaskEscapeMessage` (8396), `setDisablePreviewScreenshots(boolean)` (8667), `setShowWhenLocked(boolean)` (8688), `setInheritShowWhenLocked(boolean)` (8711), `setTurnScreenOn(boolean)` (8738).

### 4.24 Decor caption / remote animations (4 methods)

`isOverlayWithDecorCaptionEnabled` (8411), `setOverlayWithDecorCaptionEnabled(boolean)` (8423), `registerRemoteAnimations(RemoteAnimationDefinition)` (8754), `unregisterRemoteAnimations` (8768).

### 4.25 Misc lifecycle (8 methods)

`onUserLeaveHint` (2382, protected), `reportFullyDrawn` (2700), `getLoaderManager` (1106), `closeOptionsMenu` (4557), `openOptionsMenu` (4546), `openContextMenu(View)` (4612), `closeContextMenu` (4619), `invalidateOptionsMenu` (4331), `onWindowAttributesChanged(LayoutParams)` (3951), `onWindowDismissed(boolean, boolean)` (4056), `onTitleChanged(CharSequence, int)` (6856, protected), `onChildTitleChanged(Activity, CharSequence)` (6871, protected), `setTitleColor(int)` (6843), `isChild` (1074, final), `getParent` (1079, final), `getAssistToken` (7967, final).

### 4.26 Key handling (6 methods)

`onKeyDown(int, KeyEvent)` (3685), `onKeyLongPress(int, KeyEvent)` (3760), `onKeyUp(int, KeyEvent)` (3779), `onKeyMultiple(int, int, KeyEvent)` (3796), `onKeyShortcut(int, KeyEvent)` (3857), `dispatchKeyShortcutEvent(KeyEvent)` (4103), `onTrackballEvent(MotionEvent)` (3896), `onGenericMotionEvent(MotionEvent)` (3925), `dispatchTrackballEvent(MotionEvent)` (4141), `dispatchGenericMotionEvent(MotionEvent)` (4159), `dispatchPopulateAccessibilityEvent(AccessibilityEvent)` (4167).

Promote when input service lands.

### 4.27 Panel / menu (6 methods)

`onCreatePanelView(int)` (4192), `onCreatePanelMenu(int, Menu)` (4204), `onPreparePanel(int, View, Menu)` (4222), `onMenuOpened(int, Menu)` (4237), `onMenuItemSelected(int, MenuItem)` (4258), `onPanelClosed(int, Menu)` (4308).

---

## 5. No-op / default bucket (47 methods)

Methods whose framework body is empty / trivial, AND whose call sites in our supported flows expect no-op behavior. We provide an empty body without throwing.

(Many of these overlap with Implement column — those listed in §3 with "(empty)" annotation count there. This section lists only methods that are *not* in Implement but still get an empty body rather than fail-loud, because the surface contract is "framework does nothing here by default and apps don't override.")

| # | Method | Default body | Rationale |
|---|---|---|---|
| 1 | `setIntent(Intent)` (1028) | store mIntent | actually Implement (also in §3) |
| 2 | `isImmersive` (7210) | return false | safe constant; not load-bearing |
| 3 | `isVoiceInteraction` (2036) | return false | safe constant; we have no voice |
| 4 | `isVoiceInteractionRoot` (2051) | return false | safe constant |
| 5 | `isLocalVoiceInteractionSupported` (2075) | return false | safe constant |
| 6 | `isInMultiWindowMode` (2751) | return false | safe constant |
| 7 | `isInPictureInPictureMode` (2794) | return false | safe constant |
| 8 | `getMaxNumPictureInPictureActions` (2883) | return 0 | safe constant |
| 9 | `isActivityTransitionRunning` (5369) | return false | safe constant |
| 10 | `isFinishing` (6310) | return mFinished | actually Implement |
| 11 | `isDestroyed` (6318) | return mDestroyed | actually Implement |
| 12 | `isChangingConfigurations` (6331) | return false | actually Implement |
| 13 | `isTaskRoot` (6700) | return true | actually Implement |
| 14 | `isImmersive` | return false | safe constant; AppCompat probes during onCreate |
| 15 | `canStartActivityForResult` (6047) | return true | safe default; gate is in startActivityForResult anyway |
| 16 | `isResumed` (8279, final) | return mResumed | actually Implement; bias toward implementing this |
| 17 | `onProvideReferrer` (6214) | return null | safe default |
| 18 | `getReferrer` (6187) | return null | safe default; some libs query it |
| 19 | `getCallingPackage` (6239) | return null | safe default |
| 20 | `getCallingActivity` (6262) | return null | safe default |

**Reclassification note.** Of the 47 methods initially flagged "no-op", on inspection most are either (a) already covered by the Implement bucket lifecycle-hook entries in §3 (counted there), or (b) safer to fail-loud per CR2/CR19 policy because the silent default could mask a real call. The Step 2 implementer should prefer fail-loud in ambiguous cases; the few legitimate no-op constants are the `isXxx → false` family for features we explicitly don't support (PiP, multi-window, voice interaction). These are safe constants that the framework itself returns false for absent the corresponding service.

**Final accounting (after consolidation): 14 unambiguous safe-constant no-ops** (`isImmersive`, `isVoiceInteraction`, `isVoiceInteractionRoot`, `isLocalVoiceInteractionSupported`, `isInMultiWindowMode`, `isInPictureInPictureMode`, `getMaxNumPictureInPictureActions`, `isActivityTransitionRunning`, `canStartActivityForResult`, `onProvideReferrer`, `getReferrer`, `getCallingPackage`, `getCallingActivity`, `dispatchPopulateAccessibilityEvent` returns false).

The rest of the 47 are either Implement (lifecycle hooks counted in §3) or fail-loud (counted in §4). Step 2's actual coding will treat them as "empty body with @Override and `mCalled = true` inside lifecycle hooks" — those are *implementations*, not no-ops in the architectural sense.

---

## 6. Inner-class methods (5 methods)

The anonymous `WindowControllerCallback` instance at Activity.java:964-1011 declares 4 inner methods. These are bound to the `mWindowControllerCallback` field inside `attach()`. V2 §3.5 doesn't run real `PhoneWindow` machinery, so this whole callback is unwired in V2. For shadow API completeness, the inner class can be omitted from the shadowed `Activity`.

If a future framework caller does `activity.getWindow().getWindowControllerCallback()` and dispatches, it will NPE because `mWindow` is null (V2 §3.5). That's the intended failure mode — surfaces as a discovery boundary.

| # | Method | Disposition |
|---|---|---|
| 1 | `WindowControllerCallback.toggleFreeformWindowingMode()` (964) | Omit (V2 no Window) |
| 2 | `WindowControllerCallback.enterPictureInPictureModeIfPossible()` (974) | Omit (V2 no Window) |
| 3 | `WindowControllerCallback.isTaskRoot()` (981) | Omit (V2 no Window) |
| 4 | `WindowControllerCallback.updateStatusBarColor(int)` (994) | Omit (V2 no Window) |
| 5 | `WindowControllerCallback.updateNavigationBarColor(int)` (1004) | Omit (V2 no Window) |

---

## 7. Constants (must be preserved verbatim)

Public static final constants on Activity:

| Constant | Value | Reason to preserve |
|---|---|---|
| `RESULT_CANCELED` | 0 | apps reference Activity.RESULT_CANCELED |
| `RESULT_OK` | -1 | apps reference Activity.RESULT_OK |
| `RESULT_FIRST_USER` | 1 | apps reference Activity.RESULT_FIRST_USER |
| `DONT_FINISH_TASK_WITH_ACTIVITY` | 0 | TaskDescription contract |
| `FINISH_TASK_WITH_ROOT_ACTIVITY` | 1 | TaskDescription contract |
| `FINISH_TASK_WITH_ACTIVITY` | 2 | TaskDescription contract |
| `FOCUSED_STATE_SET` (protected) | `{R.attr.state_focused}` | View focused-state array; consume from R |

---

## 8. Fields that must exist on WestlakeActivity

Even if AppCompatActivity / FragmentActivity / ComponentActivity don't read these via reflection, they read them via the framework `super.` chain. The shadowed Activity must declare matching fields:

| Field name | Type | Visibility | Notes |
|---|---|---|---|
| `mBase` | `Context` | private (inherited via ContextWrapper) | set by attachBaseContext |
| `mApplication` | `Application` | private | set by attach |
| `mIntent` | `Intent` | private | set by attach / setIntent |
| `mComponent` | `ComponentName` | private | set by attach |
| `mInstrumentation` | `Instrumentation` | private | set by attach (may be null in 6-arg) |
| `mActivityInfo` | `ActivityInfo` | private | set by attach |
| `mTitle` | `CharSequence` | private | set by attach / setTitle |
| `mTitleColor` | `int` | private | set by setTitleColor (or default Color.BLACK) |
| `mWindow` | `Window` | private | NULL in V2 (§3.5) |
| `mWindowManager` | `WindowManager` | private | NULL in V2 (§3.5) |
| `mToken` | `IBinder` | private | synthetic IBinder for M4a routing |
| `mIdent` | `int` | private | task identity for M4a |
| `mFinished` | `boolean` | private | set by finish() |
| `mDestroyed` | `boolean` | private | set by performDestroy() |
| `mResumed` | `boolean` | private | set by performResume()/performPause() |
| `mCalled` | `boolean` | private | super-call check; set true in our lifecycle bodies |
| `mContentView` | `View` | private | root of inflated layout (V2-specific; not framework name) |
| `mLayoutInflater` | `LayoutInflater` | private | the WestlakeLayoutInflater used by setContentView |
| `mLifecycleCallbacks` | `ArrayList<ActivityLifecycleCallbacks>` | private | for register/unregister callbacks |
| `mTheme` | `Resources.Theme` | private | lazy from WestlakeResources.newTheme() |
| `mDefaultKeyMode` | `int` | private | for setDefaultKeyMode (may be unused) |
| `mLastNonConfigurationInstances` | `NonConfigurationInstances` | private | always null in V2 (no rotation) |

**Note**: `mFragments` (framework's `FragmentController`) is intentionally NOT declared. AppCompat / androidx FragmentActivity uses its own private `mFragments` field of its own type; the framework Activity's `mFragments` is decoupled. Step 2 omits it.

---

## 9. Inherited from `Object` (not shadowed; 11 methods)

`hashCode()`, `equals(Object)`, `toString()`, `getClass()`, `wait()`, `wait(long)`, `wait(long, int)`, `notify()`, `notifyAll()`, `clone()` (protected), `finalize()` (protected).

The shadowed `Activity` inherits these from `Object` (or via `ContextThemeWrapper` → `ContextWrapper` → `Context` → `Object`). No action needed.

---

## 10. Top-10 surprises (callouts for Step 2)

Findings that diverge from the V2 design's working assumptions or that the brief flagged as potentially load-bearing:

1. **`attach` is package-private, not protected.** Framework `Activity.attach(...)` at line 7886 is `final void attach(...)` with no visibility modifier (default = package-private to `android.app`). `WestlakeActivityThread` (different package — `com.westlake.engine` or `android.app` depending on V2 choice) must call it. Step 2 should **promote attach to `public`** in the WestlakeActivity shadow; this loosens visibility but no source-compat risk (callers can call package-private only from same package; promoting to public never breaks callers).

2. **`requestPermissions` is `final`.** Apps cannot override it. So our fail-loud body in our shadowed `Activity` is the only behavior — no subclass escape hatch. When the permission binder service lands, the Implement promotion is a direct edit to our class body.

3. **`runOnUiThread` is `final`.** Same as #2 — apps depend on `Activity.runOnUiThread` semantics. Step 2 must Implement this on Day 1 (not fail-loud): RxJava-on-main and many androidx paths call it. Posts to the main Looper's Handler.

4. **`setResult` is `final`.** Apps depend on it. Initial impl: store result code + intent; cycle through M4a `setResult(token, code, intent)` binder when M4a's setResult lands. Initial: fail-loud.

5. **`getApplication` is `final`.** Returns `mApplication`. Step 2: trivial Implement returning the stored field.

6. **`getActivityThread` is `final` and returns the framework `ActivityThread` class.** With V2's `WestlakeActivityThread` shadowing (V1 already does), this returns the Westlake instance — but the *return type* on the API surface is `android.app.ActivityThread`. As long as `WestlakeActivityThread extends ActivityThread` (or shadows the same FQCN), the type check passes. Step 6 must verify.

7. **`onCreate(Bundle, PersistableBundle)` is API 21+** and the regular `onCreate(Bundle)` is API 1. Framework dispatches `onCreate(Bundle)` from `performCreate` always; only when the system passes a PersistableBundle does it call the 2-arg variant. Our `performCreate` calls only the 1-arg version. Step 2 implements the 2-arg version as `onCreate(savedInstanceState); /* persistent state ignored */` — i.e. delegate to 1-arg.

8. **`isResumed()` is `final`** and load-bearing for Compose / RxJava lifecycle observers. Initial impl: Implement (returning the `mResumed` flag), NOT fail-loud. (Reclassified during this audit — was originally fail-loud per row 4.22.)

9. **`getCurrentFocus()` is queried by `InputMethodManager.showSoftInput(...)`** which AppCompat triggers from `EditText`'s focus path. McD's login screen has an `EditText` (apktool-decoded). On the first text-field render, `getCurrentFocus` will be called. Initial impl: walk `mContentView.findFocus()` if `mContentView instanceof ViewGroup`; null fallback. Implement on Day 1, not fail-loud.

10. **`getMenuInflater()` may be hit during AppCompatDelegate's `installMenuInflater`** even before the user calls `onCreateOptionsMenu`. AppCompat 1.4+ resolves the platform LayoutInflater factory chain on `setContentView` and that path can incidentally read MenuInflater. Recommend implementing a thin `WestlakeMenuInflater` (extends `MenuInflater`, returns empty menu on inflate) rather than fail-loud — pre-empt one likely discovery boundary.

---

## 11. Step 2 implementation checklist

Quick reference for the Builder agent handling Step 2:

- [ ] Create `shim/java/android/app/Activity.java` declaring `public class Activity extends ContextThemeWrapper`.
- [ ] Add `android.app.Activity` to `framework_duplicates.txt`.
- [ ] Implement all 87 methods in §3 (Implement bucket).
- [ ] Generate 178 fail-loud stubs in §4 (one-liners using `ServiceMethodMissing.fail`).
- [ ] Generate 14 no-op stubs (per §5 final accounting).
- [ ] Preserve all 7 constants from §7.
- [ ] Declare all 22 fields from §8.
- [ ] Promote `attach` to `public` (per surprise #1).
- [ ] Verify `WestlakeActivity` compiles standalone (no missing super references after shadow).
- [ ] Verify ComponentActivity, FragmentActivity, AppCompatActivity (unmodified framework.jar) resolve `super.` correctly against our class.
- [ ] Regression: HelloBinder + AsInterfaceTest still PASS (this is a pure additive shadow; nothing should regress).

---

## 12. Open questions for Step 2

1. **Should `WestlakeActivity` extend `ContextThemeWrapper` (framework) or a Westlake-owned `ContextThemeWrapper` shadow?** V2 §3.4 says `Resources` is Westlake-owned; `ContextThemeWrapper` reads `Resources` for theme dispatch. If `ContextThemeWrapper` is framework's version, it will call `mBase.getTheme()` which routes through our `WestlakeContextImpl.getTheme()` → `WestlakeResources.getTheme()`. **Likely safe**; flag if regression hits a theme-related NPE in `ContextThemeWrapper`.

2. **Should `attach` populate any synthetic `mFragments` field for `FragmentActivity` to work?** AppCompat 1.5+ does NOT directly reach into `Activity.mFragments`; it uses `androidx.fragment.app.FragmentActivity.mFragments` (its own private field). Step 2 should leave `Activity.mFragments` unset; if a regression surfaces, it's a discovery signal for that path.

3. **Does `WestlakeActivity.attach` need to call `super.attachBaseContext(context)` to thread `mBase` through `ContextWrapper`'s logic?** Yes — `ContextWrapper.attachBaseContext` sets the inherited `mBase` field. Step 2 should call `super.attachBaseContext(context)` before any of our field assignments.

4. **What does our `getAssistToken()` return?** Framework returns the IBinder from `attach`'s assistToken param. We pass null in V2. Step 2: return a synthetic stable IBinder (e.g. `new Binder()` once, stored). Apps that compare assist tokens to activity tokens will see a stable identity.

---

## 13. Person-time and verification

- Architect work for this doc: 2.5 hours (read AOSP, classify, draft table, sanity check).
- File touched: `docs/engine/WESTLAKE_ACTIVITY_API.md` (this file, NEW).
- File touched: `docs/engine/PHASE_1_STATUS.md` (V2-Step1 row, append).
- Source code touched: **zero** (read-only architect pass).
- Cross-references verified against: `BINDER_PIVOT_DESIGN_V2.md` §§3.3, 3.4, 3.5, 7, 8, 10.
- AOSP source verified by direct read at `/home/dspfac/aosp-android-11/frameworks/base/core/java/android/app/Activity.java`.
