# Westlake In-Process App Run — Detailed Architecture

**Status (2026-05-14)**: Validated on OnePlus 6 (`cfb7c9e3`, Android 16 / LineageOS 22).
Both noice and McDonald's APKs run inside `com.westlake.host`'s process with their
real UI rendering through Westlake's PhoneWindow.

This document explains exactly what happens, step by step, from `am start` to the
visible view tree. It complements (not replaces) the binder-pivot V2 substrate work
which is a parallel strategy for Phase 2 (OHOS port, no real Android backing).

---

## TL;DR — proof points

| Check | Value |
|---|---|
| Phone's `mCurrentFocus` | `Window{... com.westlake.host/com.westlake.host.NoiceInProcessActivity}` |
| `Window mOwnerUid` | `10218` (host) |
| `topResumedActivity` | `com.westlake.host/.NoiceInProcessActivity` |
| noice's bytecode loaded? | yes — `/proc/<host-PID>/maps` shows noice's `oat/arm64/base.{odex,vdex}` mapped into host PID |
| Phone's standalone noice process visible? | No — `mCurrentFocus` is host's window, not noice's |

The standalone noice process may be alive in the background as a side-effect of
intent routing, but it is NOT the renderer. Open work item is an `IActivityTaskManager`
binder proxy to intercept cross-package intents before they reach system_server.

---

## Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│  com.westlake.host  (UID 10218, our APK, our process)                │
│                                                                       │
│  ┌─ NoiceInProcessActivity (or McdInProcessActivity)                │
│  │   • our Activity, our PhoneWindow, our DecorView                 │
│  │   • we receive `am start`, we render to surface                  │
│  │                                                                   │
│  ├─ createPackageContext("com.github.ashutoshgngwr.noice")         │
│  │   ↓                                                              │
│  │   noiceCtx : ContextImpl                                         │
│  │      ├─ classLoader → loads noice's base.apk + base.odex         │
│  │      ├─ resources   → noice's res + arsc                         │
│  │      ├─ applicationInfo → patched to host pkg (post-load)        │
│  │      └─ mPackageInfo (LoadedApk)                                 │
│  │            ├─ mDataDirFile → /data/user/0/com.westlake.host/...  │
│  │            └─ mApplication → noiceApp (Hilt-instrumented)         │
│  │                                                                   │
│  ├─ noiceApp = NoiceApplication() [classes loaded into our PID]    │
│  │   • attachBaseContext(NoiceSafeContext(noiceCtx))                │
│  │   • onCreate() — Hilt graph builds, WorkManager, Room, ...      │
│  │                                                                   │
│  └─ noiceActivity = MainActivity()                                  │
│      • Activity.attach(ourActivityThread, instrumentation, ...)     │
│      • onCreate(null) — sets content, inflates fragments            │
│      • performStart + performResume + performTopResumedChanged      │
│      • view tree stolen, re-parented into HOST window               │
└──────────────────────────────────────────────────────────────────────┘
```

The runtime that executes noice's code is **the same ART runtime that runs
our host APK** — there is no second VM. `createPackageContext` just builds a
secondary ClassLoader chain rooted at noice's APK. Once classes are loaded,
they are first-class citizens of our process; we can reflectively walk their
fields, call their methods, drive their lifecycle, etc.

---

## The Five Pillars

Every in-process app run uses the same five reflective patches. App-specific
code differs only in 4 constants (PKG, APP_CLS, MAIN_CLS, alias set).

### Pillar 1 — `bypassHiddenApiRestrictions()`

Android `targetSdkVersion ≥ 28` is denied reflective access to fields tagged
`max-target-o`/`max-target-p`. Several fields we need (LoadedApk's protected
data dir variants, ContextImpl's cached dir fields) fall into that bucket.

We use the **meta-reflection trick**:

```kotlin
val getDeclaredMethod = Class::class.java.getDeclaredMethod(
    "getDeclaredMethod", String::class.java, arrayOf<Class<*>>()::class.java)
val vmRuntime = Class.forName("dalvik.system.VMRuntime")
val getRuntime = getDeclaredMethod.invoke(vmRuntime, "getRuntime", arrayOf<Class<*>>())
    as java.lang.reflect.Method
val runtime = getRuntime.invoke(null)
val setExemptions = getDeclaredMethod.invoke(
    vmRuntime, "setHiddenApiExemptions",
    arrayOf<Class<*>>(Array<String>::class.java)) as java.lang.reflect.Method
setExemptions.invoke(runtime, arrayOf("L"))     // exempt EVERY class starting with "L"
```

The trick: calling `Class.getDeclaredMethod` *through* a reflectively-obtained
method handle bypasses the call-site filter that Android uses to enforce hidden
API. After this one call, the entire process can freely reflect on hidden APIs
for the rest of the activity's lifetime.

### Pillar 2 — `redirectDataDir(noiceCtx)`

`ContextImpl.getDataDir()` is implemented as:

```java
public File getDataDir() {
    if (mPackageInfo != null) {
        if (isCredentialProtectedStorage() && Process.myUid() != Process.SYSTEM_UID)
            return mPackageInfo.getCredentialProtectedDataDirFile();
        else if (isDeviceProtectedStorage())
            return mPackageInfo.getDeviceProtectedDataDirFile();
        else
            return mPackageInfo.getDataDirFile();
    }
}
```

We reflectively set all three fields on `LoadedApk` to point at a host-owned
scratch dir:

```kotlin
val hostDataDir = File(filesDir.parentFile, "noice_data").apply { mkdirs() }
for (sub in listOf("shared_prefs", "databases", "cache", "code_cache", "files", "no_backup")) {
    File(hostDataDir, sub).mkdirs()
}
loadedApk.mDataDirFile                          = hostDataDir
loadedApk.mDeviceProtectedDataDirFile           = hostDataDir
loadedApk.mCredentialProtectedDataDirFile       = hostDataDir
noiceCtx.applicationInfo.dataDir                = hostDataDir.absolutePath
```

Now `noiceCtx.getFilesDir()` returns `/data/user/0/com.westlake.host/noice_data/files`,
`getDatabasePath("foo.db")` returns `/data/user/0/com.westlake.host/noice_data/databases/foo.db`,
and so on. Room can `mkdir + open` without `EACCES`.

We log a self-check (`Post-patch noiceCtx.dataDir = ...`) to confirm the patch
actually took. Pre-bypass, the credential-protected variant was denied → patch
silently no-op'd → unmodified data dir was used → EACCES storm.

### Pillar 3 — `NoiceSafeContext` (cross-package service-bind stub)

noice's `LibraryViewModel.<init>` calls `bindService` for its own service
`com.github.ashutoshgngwr.noice.service.SoundPlaybackService`. From our process
(uid 10218), binding a service in another package (uid 10206, not exported) is
a SecurityException → process death.

We wrap noiceCtx with a `ContextWrapper` that no-ops cross-package binds:

```kotlin
class NoiceSafeContext(base: Context, private val noicePkg: String) : ContextWrapper(base) {
    override fun bindService(service: Intent, conn: ServiceConnection, flags: Int): Boolean {
        if (service.component?.packageName == noicePkg) {
            Log.w(TAG, "stubbed bindService(${service.component?.shortClassName})")
            return false
        }
        return super.bindService(service, conn, flags)
    }
    // same for startService, startForegroundService, bindService(executor)
}
```

This wrapper becomes:
- `noiceApp.mBase` (so Application.bindService passes through it)
- The innermost wrapper of `attachCtx` (so Activity.bindService passes through it)

After the stub returns `false`, noice's code treats the service as unavailable
and the UI degrades gracefully (audio just doesn't play; the UI is still shown).

### Pillar 4 — `stubLocaleManager(noiceCtx)`

`AppCompatDelegate.Api33Impl.a()` (an `LazyLoad` for the app's locale list)
calls `LocaleManager.getApplicationLocales(packageName)` which routes through
the `ILocaleManager` binder to system_server. The system_server checks
"is uid 10218 allowed to read locales for `com.github.ashutoshgngwr.noice`?"
That permission is `READ_APP_SPECIFIC_LOCALES`, granted only to system apps.
→ SecurityException → process death.

We don't try to grant that permission (we can't). Instead we hook the binder
proxy inside our own process:

```kotlin
val lm = ctx.getSystemService(Context.LOCALE_SERVICE)
val mServiceField = lm.javaClass.getDeclaredField("mService")
mServiceField.isAccessible = true
val original = mServiceField.get(lm)
val iLm = Class.forName("android.app.ILocaleManager")
val empty = LocaleList.getEmptyLocaleList()
val proxy = Proxy.newProxyInstance(iLm.classLoader, arrayOf(iLm)) { _, method, args ->
    when (method.name) {
        "getApplicationLocales" -> empty
        else -> method.invoke(original, *(args ?: emptyArray()))
    }
}
mServiceField.set(lm, proxy)
```

`LocaleManager.mService` now points at a Java dynamic Proxy that returns an
empty `LocaleList` for `getApplicationLocales` and delegates everything else
to the original. The binder call never leaves our process when the method is
`getApplicationLocales`.

### Pillar 5 — `driveLifecycleToResumed(noiceActivity)`

After we reflectively call `Activity.onCreate(null)`, the view tree is built
but not RESUMED. Compose, Fragments, and ViewModelStore all hook into
`Lifecycle.Event.ON_START` and `ON_RESUME` to actually start composing /
inflating / observing. Without these, the screen stays blank.

```kotlin
for (method in listOf("performStart", "performResume", "performTopResumedActivityChanged")) {
    val m = Activity::class.java.declaredMethods.find {
        it.name == method && (it.parameterCount == 0 || it.parameterCount == 2)
    } ?: continue
    m.isAccessible = true
    val args = when (m.parameterCount) {
        0 -> emptyArray()
        2 -> arrayOf<Any?>(true, "in-process")
        else -> continue
    }
    m.invoke(activity, *args)
}
```

These are the framework's normal lifecycle entry points — the same methods
Android's own `ActivityThread.handleStartActivity` invokes during a normal
launch. We just call them ourselves, in order. After `performResume` returns,
the Compose layer composes, the Fragment manager attaches Fragments, the
ViewModelStore initializes ViewModels — and the view tree paints.

---

## Step-by-step run of `am start -n com.westlake.host/.NoiceInProcessActivity`

### Step 0 — Phone receives `am start`

`ActivityTaskManager` resolves `com.westlake.host/.NoiceInProcessActivity` from
the host's manifest, forks a zygote child, sets uid=10218, mounts our APK,
begins `ActivityThread.main` → `performLaunchActivity` → `NoiceInProcessActivity.onCreate()`.

### Step 1 — onCreate setup

```kotlin
super.onCreate(savedInstanceState)
bypassHiddenApiRestrictions()                    // Pillar 1
installSwallowingUncaughtHandler()               // background coroutine safety net
val targetCls = resolveTargetFromIntent(intent)  // MainActivity by default
launchTarget(targetCls)
```

### Step 2 — Load noice's classes via createPackageContext

```kotlin
val noiceCtx = createPackageContext(
    "com.github.ashutoshgngwr.noice",
    Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY)
redirectDataDir(noiceCtx)                        // Pillar 2 (BEFORE Application init)
```

After this call, `/proc/<host-PID>/maps` shows:

```
/data/app/.../com.github.ashutoshgngwr.noice-.../oat/arm64/base.odex
/data/app/.../com.github.ashutoshgngwr.noice-.../oat/arm64/base.vdex
/data/app/.../com.github.ashutoshgngwr.noice-.../base.apk
```

That is irrefutable evidence noice's code is loaded into Westlake's process.

### Step 3 — Apply noice's theme to our window

```kotlin
val fullRes = packageManager.getResourcesForApplication(NOICE_PKG)
val themeId = fullRes.getIdentifier("Theme.App", "style", NOICE_PKG)
                .takeIf { it != 0 }
              ?: fullRes.getIdentifier("AppTheme", "style", NOICE_PKG)
                .takeIf { it != 0 }
              ?: 0
if (themeId != 0) theme.applyStyle(themeId, true)
```

Applies noice's `Theme.App` style to our (host) activity's theme. This affects
text color, background color, etc. The transferred view tree inherits this theme.

### Step 4 — Instantiate noice's Application + wire LoadedApk

```kotlin
val noiceApp = noiceCtx.classLoader
    .loadClass("com.github.ashutoshgngwr.noice.NoiceApplication")
    .newInstance() as Application
ContextWrapper.attachBaseContext(noiceApp, NoiceSafeContext(noiceCtx, NOICE_PKG))    // Pillar 3
noiceApp.applicationInfo.packageName = applicationInfo.packageName                     // host pkg
LoadedApk.mApplication = noiceApp                                                     // reflective
noiceApp.onCreate()
```

`NoiceApplication` is `Hilt_NoiceApplication` (Hilt-generated). Its constructor
plus `onCreate` build the entire Hilt component graph, initialize WorkManager,
open Room databases (at our redirected path), load preferences, etc.

The `LoadedApk.mApplication = noiceApp` wire is critical: Hilt's
`EntryPoints.get(applicationContext, ...)` walks `Context → mBase → ContextImpl
.mPackageInfo.mApplication`. If this final field is null, Hilt throws "Could
not find an Application in the given context: null". Setting it to our
instantiated Hilt-Application makes the chain resolve.

### Step 5 — Load + attach noice's MainActivity

```kotlin
val noiceActivity = noiceCtx.classLoader
    .loadClass("com.github.ashutoshgngwr.noice.activity.MainActivity")
    .newInstance() as Activity

val at = ActivityThread.sCurrentActivityThread          // host's real ActivityThread
val attachMethod = Activity::class.java.declaredMethods.find {
    it.name == "attach" && it.parameterCount >= 19
}!!

val ai = ActivityInfo().apply {
    packageName = NOICE_PKG; name = targetCls
    applicationInfo = noiceCtx.applicationInfo
    if (themeId != 0) theme = themeId
}

val safeInflateCtx = NoiceSafeContext(ContextThemeWrapper(noiceCtx, themeId), NOICE_PKG)
val attachCtx = object : ContextWrapper(safeInflateCtx) {
    override fun getPackageName(): String = hostPkg
    override fun getOpPackageName(): String = hostPkg
}

val args = arrayOfNulls<Any>(attachMethod.parameterCount).apply {
    set(0, attachCtx); set(1, at); set(2, Instrumentation()); set(3, Binder())
    set(4, 0); set(5, noiceApp); set(6, intent); set(7, ai); set(8, targetCls)
}
attachMethod.invoke(noiceActivity, *args)
if (themeId != 0) noiceActivity.setTheme(themeId)
```

This is the load-bearing call. `Activity.attach(...)` is the framework method
that normally gets called by `ActivityThread.performLaunchActivity` during a
real launch. It:

1. Creates a real `PhoneWindow` (allocates DecorView, status bar handling,
   animation choreographer, etc.)
2. Wires `mApplication = noiceApp`, `mActivityInfo = ai`, `mIntent = intent`,
   `mInstrumentation = instrumentation`, etc.
3. Sets `mBase = attachCtx`

After `attach()` returns, `noiceActivity.window` is a real PhoneWindow with a
real DecorView — fully functional, just not yet attached to a display.

Note: this PhoneWindow is **separate** from our (host) activity's PhoneWindow.
noice's activity has its own window, ours has its own. We're about to steal
the content from noice's into ours.

### Step 6 — Hook LocaleManager

```kotlin
stubLocaleManager(noiceCtx)                                                          // Pillar 4
```

Done BEFORE onCreate so the first AppCompatDelegate lookup finds the proxy
instead of the real binder.

### Step 7 — Drive onCreate

```kotlin
val oc = Activity::class.java.getDeclaredMethod("onCreate", Bundle::class.java)
oc.isAccessible = true
oc.invoke(noiceActivity, null as Bundle?)
```

Reflectively calls `Activity.onCreate(null)` on noice's MainActivity instance.
This is the moment noice's user code starts executing. Inside MainActivity.onCreate:

- `super.onCreate()` runs ComponentActivity / AppCompatActivity setup
- `setContentView(R.layout.activity_main)` inflates noice's layout into
  noice-activity's PhoneWindow
- Fragment manager attaches starting Fragments (Hilt-aware LibraryFragment etc.)
- Hilt-injected `settingsRepository` Lazy resolves — calls `EntryPoints.get`
  → walks Application chain → succeeds because LoadedApk.mApplication is wired
- noice may call `startActivity(AppIntroActivity)` for first-run

If `startActivity` is called targeting `com.github.ashutoshgngwr.noice/.*`,
Android resolves the intent component:
- If the alias FQCN is registered in OUR manifest (see Step 9), Android routes
  it via the alias → our `NoiceInProcessActivity` (with `onNewIntent`)
- If noice's code hardcodes a different package name (rare for noice, common
  for McD), Android resolves to noice's own activity in its own package →
  spawns the standalone process

### Step 8 — Steal noice's view tree into our window

```kotlin
val nContent = noiceActivity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
if (nContent.childCount > 0) {
    val nView = nContent.getChildAt(0)
    nContent.removeView(nView)
    val wrap = FrameLayout(this)
    wrap.addView(nView, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
    setContentView(wrap)                       // <-- HOST activity's setContentView
    wrap.post {
        nView.requestLayout(); nView.invalidate()
    }
}
```

`noiceActivity.window.decorView.findViewById(android.R.id.content)` is the
ViewGroup that `setContentView` inflates into. We take its first (and only)
child — the root of noice's content layout — detach it from noice's
never-displayed PhoneWindow, and re-parent it into our (host) activity's
content view.

After this, the screen shows noice's view tree, but it's mounted in
**Westlake's PhoneWindow**. The window manager owns one window, and the
window manager's records show that window owned by `com.westlake.host`.

### Step 9 — Lifecycle to RESUMED

```kotlin
driveLifecycleToResumed(noiceActivity)                                              // Pillar 5
```

Reflectively invokes `performStart` → `performResume` → `performTopResumedActivityChanged`.
This unblocks:
- Lifecycle observers (Compose recompose triggers, Flow collectors start)
- Fragment lifecycle (`onCreateView`, `onViewCreated`, `onStart`, `onResume`)
- ViewModelStore activates (ViewModels initialize; their service binds get
  stubbed by NoiceSafeContext)
- The render pipeline runs; the view tree paints to the surface

### Step 10 — onNewIntent + cross-activity routing

When noice's MainActivity calls `startActivity(intent for AppIntroActivity)`,
the intent's component is `com.westlake.host/com.github.ashutoshgngwr.noice
.activity.AppIntroActivity` (host package + noice activity class). Android
looks for that in our manifest:

```xml
<activity-alias android:name="com.github.ashutoshgngwr.noice.activity.AppIntroActivity"
                android:targetActivity=".NoiceInProcessActivity"
                android:exported="true" />
```

Android matches the alias and launches `com.westlake.host/.NoiceInProcessActivity`
again with the intent. Since our activity is already on top, Android calls
`onNewIntent` instead of creating a new instance:

```kotlin
override fun onNewIntent(newIntent: Intent) {
    val newTarget = resolveTargetFromIntent(newIntent)
        // ← reads alias FQCN from intent.component.className
    if (newTarget != currentTarget) {
        setIntent(newIntent)
        Activity::class.java.getDeclaredMethod("onPause")
            .also { it.isAccessible = true }
            .invoke(currentNoiceActivity)
        launchTarget(newTarget)
    }
}
```

`launchTarget(newTarget)` runs Steps 5-9 again but with `targetCls =
"com.github.ashutoshgngwr.noice.activity.AppIntroActivity"`. AppIntroActivity
gets instantiated, attached, onCreate'd, lifecycle-driven, and its view tree
replaces what was on screen.

---

## How McD differs from noice

The five-pillar pattern is **generic**. McdInProcessActivity is a sed-clone of
NoiceInProcessActivity with these constants substituted:

| Constant | noice | McD |
|---|---|---|
| `PKG` | `com.github.ashutoshgngwr.noice` | `com.mcdonalds.app` |
| `APP_CLS` | `...NoiceApplication` | `com.mcdonalds.app.application.McDMarketApplication` |
| `MAIN_CLS` | `...activity.MainActivity` | `com.mcdonalds.mcdcoreapp.common.activity.SplashActivity` |
| Manifest aliases | 5 entries (MainActivity, AppIntroActivity, ...) | 9 entries (SplashActivity, HomeDashboardActivity, DeepLinkRouter, ...) |
| Safe-context filter | `pkg == "com.github.ashutoshgngwr.noice"` | `pkg == "com.mcdonalds.app"` |

The five pillars themselves are identical. McD-specific issues encountered
during validation:

1. **`AppCompatDelegate.Api33Impl.a()` SecurityException** — McD's AppCompatDelegate
   actively calls `LocaleManager.getApplicationLocales(packageName)` during
   SplashActivity.onCreate. Fixed by Pillar 4 (LocaleManager binder hook).
2. **`SoundPlaybackService` analogue: many cross-pkg binds** — McD's Application
   binds to FCM, analytics, GMS measurement, etc. All caught by SafeContext.
3. **Cross-package intent rewriting** — McD's SplashActivity does
   `setClassName("com.mcdonalds.app", "...HomeDashboardActivity")` literally,
   bypassing our patched ApplicationInfo.packageName. Manifest aliases only
   catch `com.westlake.host/` intents. **OPEN ITEM** — see below.

---

## Verified working in `com.westlake.host` process

### noice
- ✅ Welcome / AppIntro screen renders
- ✅ Library tab (offline state, "Oops! We couldn't fetch the sound library...")
- ✅ Favorites tab (3 cached presets: Beach, Camping, Thunderstorm — with play buttons)
- ✅ Profile tab (Account, Sign up, Sign in, View plans)
- ✅ Fragment + ViewModel lifecycle resolves
- ✅ 8x `bindService(SoundPlaybackService / SubscriptionStatusPollService)` stubbed gracefully

### McD
- ✅ SplashActivity renders
- ✅ "Check your Wi-Fry" offline screen with McD branding (golden arches, fries graphic, Try again button)
- ⚠️ Crashes on cross-activity nav to `HomeDashboardActivity` (open item below)

---

## Manifest entries

```xml
<!-- Union of noice + McD permissions so in-process apps don't SecurityException -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
<!-- + READ_APP_SPECIFIC_LOCALES + INTERNET + READ/WRITE_EXTERNAL_STORAGE + SYSTEM_ALERT_WINDOW -->

<application ...>
    <activity android:name=".NoiceInProcessActivity" android:exported="true" />
    <activity android:name=".McdInProcessActivity"   android:exported="true" />

    <!-- noice cross-activity routing -->
    <activity-alias android:name="com.github.ashutoshgngwr.noice.activity.MainActivity"
                    android:targetActivity=".NoiceInProcessActivity" android:exported="true" />
    <activity-alias android:name="com.github.ashutoshgngwr.noice.activity.AppIntroActivity"
                    android:targetActivity=".NoiceInProcessActivity" android:exported="true" />
    <activity-alias android:name="com.github.ashutoshgngwr.noice.activity.SignInLinkHandlerActivity"
                    android:targetActivity=".NoiceInProcessActivity" android:exported="true" />
    <activity-alias android:name="com.github.ashutoshgngwr.noice.activity.SetAlarmHandlerActivity"
                    android:targetActivity=".NoiceInProcessActivity" android:exported="true" />
    <activity-alias android:name="com.github.ashutoshgngwr.noice.billing.StripeCheckoutSessionCallbackActivity"
                    android:targetActivity=".NoiceInProcessActivity" android:exported="true" />

    <!-- McD cross-activity routing -->
    <activity-alias android:name="com.mcdonalds.mcdcoreapp.common.activity.SplashActivity"
                    android:targetActivity=".McdInProcessActivity" android:exported="true" />
    <activity-alias android:name="com.mcdonalds.mcdcoreapp.common.activity.DeepLinkRouter"
                    android:targetActivity=".McdInProcessActivity" android:exported="true" />
    <activity-alias android:name="com.mcdonalds.account.profile.presentation.ProfileActivity"
                    android:targetActivity=".McdInProcessActivity" android:exported="true" />
    <activity-alias android:name="com.mcdonalds.payments.ui.activity.PayPalActivity"
                    android:targetActivity=".McdInProcessActivity" android:exported="true" />
    <activity-alias android:name="com.mcdonalds.loyalty.dashboard.linkedPartnership.presentation.activity.PartnerLoginActivity"
                    android:targetActivity=".McdInProcessActivity" android:exported="true" />
    <activity-alias android:name="com.mcdonalds.homedashboard.activity.HomeDashboardActivity"
                    android:targetActivity=".McdInProcessActivity" android:exported="true" />
    <activity-alias android:name="com.mcdonalds.account.activity.LoginRegistrationActivity"
                    android:targetActivity=".McdInProcessActivity" android:exported="true" />
    <!-- + several more McD activities -->
</application>
```

---

## Status of open items (post `PF-inproc-002`/`004` landing)

| # | Item | Status |
|---|---|---|
| 1 | Cross-package intent rewriting | ✅ **LANDED** — `installIntentRewriter()` via `IActivityTaskManager` Proxy hook. McD HomeDashboardActivity now navigates inside Westlake (verified `topResumedActivity=com.westlake.host/.McdInProcessActivity` after `Rewrote com.mcdonalds.app/HomeDashboardActivity → com.westlake.host/.McdInProcessActivity`). |
| 2 | Service hosting (cross-pkg bind delegation) | Open — #599 |
| 3 | Lifecycle down-drive | ✅ **LANDED** — `forwardLifecycle("performPause"/"performStop"/"performDestroy")`. Verified `Forwarded performPause to HomeDashboardActivity` on backgrounding. |
| 4 | Process death + savedInstanceState | Open — #601 |
| 5 | Runtime permission delegation | Open — #602 |
| 6 | Content provider hosting | Open — #603 |
| 7 | Native lib namespace isolation | Open — #604 |
| 8 | Configuration change handling | Open — #605 |
| 9 | Notification forwarding | Open — #606 |
| 10 | Activity-result cross-pkg | Open — #607 |
| 11 | Back-stack management | Open — #608 |
| 12 | InitializationProvider hosting | Open — #609 |
| 13 | WindowInsets propagation | Open — #610 |
| 14 | Memory hygiene + teardown | Open — #611 |
| 15 | Multi-process apps (was "won't fix") | ✅ **VALIDATED** — `MultiProcInProcessActivity` + 4-slot pool (`.MpServiceSlotN` declared with `android:process=":mp_procN"`) loads `multiproc-test-gradle/`'s 2-`:remote`-service synthetic test APK. PS shows 3 PIDs all under host UID; counter isolation proven per-tap (proc1 counter=3, proc2 counter=1 after 3+1 taps). |
| 16 | Generic InProcessLauncher refactor | Open — #613 |

## Open items (historical detail below)

### 1. Cross-package intent rewriting (McD blocker) — LANDED PF-inproc-002

McD's SplashActivity creates intents with `setClassName("com.mcdonalds.app", ...)`
literally. These bypass our manifest aliases (which only catch `com.westlake.host/`
intents).

**Solution**: hook `IActivityTaskManager` binder service (same pattern as
LocaleManager hook). Use `java.lang.reflect.Proxy` on the `IActivityTaskManager`
interface, wrap `ActivityTaskManager.IActivityTaskManagerSingleton.mInstance`.
In the proxy's invocation handler, for `startActivity` / `startActivityAsUser`
/ `startActivities` methods, scan args for `Intent` parameters, rewrite each
intent whose `component.packageName == NOICE_PKG` or `MCD_PKG` to target
`com.westlake.host/.{Noice,Mcd}InProcessActivity` with the original FQCN as
an extra. The host activity's `onNewIntent` already handles target dispatch.

Same fix benefits noice (eliminates the side-effect of spawning standalone
noice when noice's code does cross-pkg-targeted intents).

### 2. Generic refactor

`NoiceInProcessActivity` and `McdInProcessActivity` are ~330 LOC each and
~95% identical. Extract `InProcessLauncher` helper that takes an `AppConfig`
(PKG, APP_CLS, default MAIN_CLS, package filter). Activity subclasses become
~30 LOC delegates.

### 3. Specific service delegation

`NoiceSafeContext` / `McdSafeContext` blanket-stubs ALL cross-package binds.
If specific services need to actually function (deep-link, auth, payments),
declare proxy services in the host manifest that internally load the noice/McD
service class via classloader and forward `onBind` / `onStartCommand`.

### 4. View tree size / orientation handling

Currently we transfer the view tree to a `FrameLayout` with `MATCH_PARENT`
inside our content view. Configuration changes (rotation, density) won't
propagate to noice's view tree because Westlake's Resources object is
separate from noice's. Open question: should we override `onConfigurationChanged`
to forward to the noice activity?

### 5. Memory + lifecycle hygiene

Process-wide singleton refs to noice's Application/Context are stored in the
companion. On Westlake activity finish/relaunch, these aren't torn down — a
new launch reuses them. Should be fine for development but may leak under
heavy use. Should add `onDestroy` cleanup that nulls the refs and runs noice's
`Application.onTerminate`.

---

## Reproducer

```bash
# Build host APK with both in-process activities
cd westlake-host-gradle
./gradlew :app:assembleDebug

# Install (push first, then pm install to avoid `cmd install` flakiness on some Androids)
adb push app/build/outputs/apk/debug/app-debug.apk /data/local/tmp/host.apk
adb shell "pm install -r -t /data/local/tmp/host.apk"

# Wake screen + ensure no standalone processes lingering
adb shell "input keyevent KEYCODE_WAKEUP"
adb shell "am force-stop com.westlake.host"
adb shell "am force-stop com.github.ashutoshgngwr.noice"
adb shell "am force-stop com.mcdonalds.app"

# Launch noice in Westlake
adb shell "am start -n com.westlake.host/.NoiceInProcessActivity"

# Launch McD in Westlake
adb shell "am start -n com.westlake.host/.McdInProcessActivity"

# Verify it's Westlake rendering (not standalone)
adb shell "dumpsys window | grep -E 'mCurrentFocus|mFocusedApp' | head -3"
# Expected: mCurrentFocus = Window{... com.westlake.host/com.westlake.host.{Noice,Mcd}InProcessActivity}

# Confirm noice's bytecode loaded into Westlake's process
PID=$(adb shell "pidof com.westlake.host" | tr -d '\r\n')
adb shell "su -c 'cat /proc/$PID/maps' | grep -E 'noice|mcdonalds'"
# Expected: lines showing the foreign app's .odex / .vdex / .apk mapped into Westlake PID
```

---

## What this proves

The macro-shim / V2 substrate work (`docs/engine/BINDER_PIVOT_DESIGN_V2.md`)
tried to recreate Android's framework from scratch using shadow classes
(`WestlakeActivity`, `WestlakeApplication`, etc.). It hit a wall at Hilt-internal
context wrapping (CR58 close-out) — fundamentally because rebuilding
`LoadedApk + ContextImpl + PhoneWindow + ActivityThread` from outside is brittle.

This in-process approach **inverts the strategy**: we keep the real Android
framework wholesale and patch only the binder-boundary checks that block
in-process loading of foreign-package code. Five reflective patches (no shadow
classes, no dex shim, no binder pivot) are enough to run unmodified noice / McD
APKs inside a controlled host process on stock Android.

For Phase 1 (Android-on-Android validation), this strategy is strictly cleaner
than the V2 substrate. For Phase 2 (OHOS port), the V2 substrate retains value
because OHOS doesn't provide a compatible `Activity/ActivityThread/PackageManager`
surface — there's no real Android to patch. Phase 2 work continues on the V2
track; Phase 1 is now de-risked via this option-3 approach.

---

## File map

- `westlake-host-gradle/app/src/main/java/com/westlake/host/NoiceInProcessActivity.kt` — ~330 LOC
- `westlake-host-gradle/app/src/main/java/com/westlake/host/McdInProcessActivity.kt`   — ~330 LOC (sed-clone)
- `westlake-host-gradle/app/src/main/AndroidManifest.xml` — permission union + 14 activity aliases

Memory artifacts (cross-reference for next-agent handoff):
- `~/.claude/projects/-home-dspfac-openharmony/memory/project_noice_inprocess_breakthrough.md`
- `~/.claude/projects/-home-dspfac-openharmony/memory/MEMORY.md` (top engine pointer updated)
