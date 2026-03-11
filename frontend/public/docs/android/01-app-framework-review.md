# Android 11 AOSP Code Review: Core App Framework

**Directory:** `frameworks/base/core/java/android/app/`
**Total files in directory:** 210
**Review scope:** Key app-facing APIs for developers

---

## Table of Contents

1. [Activity.java](#1-activityjava)
2. [Service.java](#2-servicejava)
3. [Application.java](#3-applicationjava)
4. [Fragment.java](#4-fragmentjava)
5. [ActivityManager.java](#5-activitymanagerjava)
6. [NotificationManager.java](#6-notificationmanagerjava)
7. [AlarmManager.java](#7-alarmmanagerjava)
8. [DownloadManager.java](#8-downloadmanagerjava)
9. [KeyguardManager.java](#9-keyguardmanagerjava)
10. [Cross-Cutting Observations](#10-cross-cutting-observations)

---

## 1. Activity.java

**File:** `frameworks/base/core/java/android/app/Activity.java`
**Lines:** 8,879
**Since:** API Level 1

### Class Purpose and Responsibility

`Activity` is the fundamental building block for user interaction in Android. It provides a window in which to place UI via `setContentView()`, manages its own lifecycle, handles configuration changes, and coordinates with the system's activity management infrastructure.

**Declaration (line 730):**
```java
public class Activity extends ContextThemeWrapper
        implements LayoutInflater.Factory2,
        Window.Callback, KeyEvent.Callback,
        OnCreateContextMenuListener, ComponentCallbacks2,
        Window.OnWindowDismissedCallback,
        AutofillManager.AutofillClient, ContentCaptureManager.ContentCaptureClient
```

### Lifecycle Methods

The complete lifecycle callback chain, all annotated `@CallSuper` where the super call is mandatory:

| Method | Line | Description |
|--------|------|-------------|
| `onCreate(Bundle)` | ~1534 | Initial setup: inflate UI, bind data. Receives saved instance state. |
| `onCreate(Bundle, PersistableBundle)` | ~1627 | Persistent-state variant for activities with `persistableMode="persistAcrossReboots"`. |
| `onStart()` | ~1734 | Activity becoming visible. |
| `onRestart()` | ~1720 | Called after `onStop()`, before the activity is started again. |
| `onResume()` | ~1818 | Activity in foreground, interacting with user. |
| `onTopResumedActivityChanged(boolean)` | ~2130 | **New in API 29 (Android 10).** Indicates whether this is the topmost resumed activity in multi-window. |
| `onPause()` | ~1948 | Losing foreground focus. Must be lightweight. |
| `onStop()` | ~2149 | No longer visible. |
| `onDestroy()` | ~2194 | Final cleanup before destruction. |
| `onSaveInstanceState(Bundle)` | ~1850 | Save transient UI state. Called after `onStop()` as of API 28 (P). |
| `onSaveInstanceState(Bundle, PersistableBundle)` | ~1900 | Persistent state save variant. |
| `onRestoreInstanceState(Bundle)` | ~1674 | Restore state saved in `onSaveInstanceState`. Called after `onStart()`. |

**Lifecycle ordering note (line 446-448):** As of API P, `onSaveInstanceState()` is always called after `onStop()`, allowing safe fragment transactions in `onStop()`.

### Key Public APIs

#### Activity Navigation and Results

| Method | Line | Signature |
|--------|------|-----------|
| `startActivity()` | 5610 | `public void startActivity(Intent intent)` |
| `startActivityForResult()` | 5271 | `public void startActivityForResult(@RequiresPermission Intent intent, int requestCode)` |
| `startActivityForResult()` | 5309 | `public void startActivityForResult(@RequiresPermission Intent intent, int requestCode, @Nullable Bundle options)` |
| `onActivityResult()` | 6566 | `protected void onActivityResult(int requestCode, int resultCode, Intent data)` |
| `setResult()` | 6134 | `public final void setResult(int resultCode)` |
| `setResult()` | 6163 | `public final void setResult(int resultCode, Intent data)` |
| `finish()` | 6405 | `public void finish()` |
| `finishAffinity()` | 6423 | `public void finishAffinity()` |
| `finishAndRemoveTask()` | 6512 | `public void finishAndRemoveTask()` |
| `finishAfterTransition()` | 6461 | `public void finishAfterTransition()` |

**Result code constants (lines 740-744):**
- `RESULT_CANCELED = 0`
- `RESULT_OK = -1`
- `RESULT_FIRST_USER = 1`

#### Runtime Permissions (lines 5208-5257)

```java
public final void requestPermissions(@NonNull String[] permissions, int requestCode)
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
public boolean shouldShowRequestPermissionRationale(@NonNull String permission)
```

#### Multi-Window and Picture-in-Picture (lines 2700-2914)

| Method | Line | Description |
|--------|------|-------------|
| `isInMultiWindowMode()` | 2751 | Returns true if in multi-window mode |
| `onMultiWindowModeChanged(boolean, Configuration)` | 2723 | System callback when multi-window state changes |
| `isInPictureInPictureMode()` | 2794 | Returns true if in PiP mode |
| `onPictureInPictureModeChanged(boolean, Configuration)` | 2766 | System callback when PiP state changes |
| `enterPictureInPictureMode(PictureInPictureParams)` | 2834 | Programmatic PiP entry. Activity must be resumed. |
| `setPictureInPictureParams(PictureInPictureParams)` | 2864 | Update PiP properties |
| `onPictureInPictureRequested()` | 2912 | **New in Android 11.** System asks app to consider PiP mode. |
| `getMaxNumPictureInPictureActions()` | 2883 | Query max PiP actions |

#### Window and UI Management

| Method | Line | Description |
|--------|------|-------------|
| `getWindow()` | 1096 | Returns the Window for direct access |
| `getWindowManager()` | 1084 | Returns WindowManager for custom windows |
| `getCurrentFocus()` | 1120 | Returns currently focused View |
| `setContentView(int)` | -- | Inflate layout into the activity window |
| `findViewById(int)` | -- | Find view by ID in the activity's content |
| `recreate()` | 6341 | Force activity recreation with new instance |
| `reportFullyDrawn()` | 2700 | Signal to system that UI is fully rendered |

#### Configuration and State (lines 2947-2997)

```java
public void onConfigurationChanged(@NonNull Configuration newConfig)
public int getChangingConfigurations()
public boolean isChangingConfigurations()  // line 6331
```

#### Locus ID (lines 1032-1066)

**New in Android 10.** `setLocusContext(@Nullable LocusId, @Nullable Bundle)` links the activity instance to a specific content locus for better ranking in sharing, notifications, and shortcuts.

#### Activity Lifecycle Callbacks (lines 1252-1302)

Activities can register per-activity lifecycle callbacks, distinct from Application-level ones:
```java
public void registerActivityLifecycleCallbacks(@NonNull Application.ActivityLifecycleCallbacks callback)
public void unregisterActivityLifecycleCallbacks(@NonNull Application.ActivityLifecycleCallbacks callback)
```

**Ordering guarantee (line 1256-1269):** Activity-registered callbacks are always nested within Application-registered callbacks. Pre-events go Application -> Activity; post-events go Activity -> Application.

### Hidden/System APIs

| Annotation | Method | Line | Notes |
|-----------|--------|------|-------|
| `@SystemApi` | `convertFromTranslucent()` | 7267 | Convert activity from translucent |
| `@SystemApi` | `convertToTranslucent()` | 7306 | Convert activity to translucent |
| `@SystemApi` | `isBackgroundVisibleBehind()` | 7444 | Deprecated, always returns false |
| `@SystemApi` | `TranslucentConversionListener` | 8437 | Callback interface for translucency conversion |
| `@hide` | `FINISH_TASK_WITH_ROOT_ACTIVITY` | 752 | Task finish behavior constant |
| `@hide` | `FINISH_TASK_WITH_ACTIVITY` | 757 | Task finish behavior constant |
| `@hide` | `startActivityForResultAsUser()` | 5388 | Start activity as specific user |
| `@hide` | `startActivityAsCaller()` | 5474 | Start activity with caller's identity |
| `@hide @TestApi` | `onMovedToDisplay()` | 2944 | Display switch callback |

### Security-Relevant Annotations

- `@RequiresPermission` on `Intent` parameters of `startActivityForResult()`, `startActivityIfNeeded()`, `startNextMatchingActivity()`, and related methods -- permission depends on the Intent target.
- `@RequiresPermission(CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS)` on `registerRemoteAnimations()` (line 8753) and `unregisterRemoteAnimations()` (line 8767).

### Notable Design Patterns

1. **`@UnsupportedAppUsage` prevalence:** Many internal fields (`mToken`, `mIntent`, `mMainThread`, `mApplication`, etc.) are annotated with `@UnsupportedAppUsage`, indicating they were historically accessed via reflection by apps but are not public API. Many have `maxTargetSdk` limits.

2. **Callback iteration ordering:** During "winding up" lifecycle events (create/start/resume), callbacks iterate forward; during "winding down" events (pause/stop/destroy), they iterate in reverse order (LIFO), ensuring symmetric nesting (lines 1400-1500).

3. **Content Capture integration:** Deep integration with `ContentCaptureManager` (lines 1151-1229), automatically notifying on lifecycle transitions for intelligent services.

4. **Autofill integration:** Activity implements `AutofillManager.AutofillClient`, managing autofill sessions across the lifecycle.

---

## 2. Service.java

**File:** `frameworks/base/core/java/android/app/Service.java`
**Lines:** 889
**Since:** API Level 1

### Class Purpose and Responsibility

`Service` is an application component for long-running background operations or providing functionality to other apps via IPC. It runs on the main thread by default and must be declared in the manifest.

**Declaration (line 310):**
```java
public abstract class Service extends ContextWrapper implements ComponentCallbacks2,
        ContentCaptureManager.ContentCaptureClient
```

### Lifecycle Methods

| Method | Line | Description |
|--------|------|-------------|
| `onCreate()` | 351 | First creation. Do not call directly. |
| `onStartCommand(Intent, int, int)` | 504 | Called for each `startService()` call. Returns start behavior. |
| `onBind(Intent)` | 549 | **Abstract.** Return IBinder for bound clients. May return null. |
| `onUnbind(Intent)` | 564 | All clients disconnected. Return true to receive `onRebind()`. |
| `onRebind(Intent)` | 579 | New clients after `onUnbind()` returned true. |
| `onDestroy()` | 515 | Final cleanup. |
| `onTaskRemoved(Intent)` | 592 | User removed a task from the service's app. |
| `onConfigurationChanged(Configuration)` | 518 | Device config changed. |
| `onLowMemory()` | 521 | System low on memory. |
| `onTrimMemory(int)` | 524 | Memory trim levels. |

### Start Command Return Values (lines 367-430)

| Constant | Value | Behavior after kill |
|----------|-------|-------------------|
| `START_STICKY_COMPATIBILITY` | 0 | Legacy sticky, no guaranteed restart |
| `START_STICKY` | 1 | Restart service, call `onStartCommand` with null intent |
| `START_NOT_STICKY` | 2 | Do not recreate until explicit `startService()` |
| `START_REDELIVER_INTENT` | 3 | Restart and redeliver the last intent |

### Start Command Flags (lines 455-462)

| Constant | Value | Meaning |
|----------|-------|---------|
| `START_FLAG_REDELIVERY` | 0x0001 | Intent is a redelivery after process kill |
| `START_FLAG_RETRY` | 0x0002 | Intent is a retry of a failed `onStartCommand` |

### Foreground Service APIs (lines 674-802)

```java
public final void startForeground(int id, Notification notification)
public final void startForeground(int id, @NonNull Notification notification,
        @ForegroundServiceType int foregroundServiceType)  // API 29+
public final void stopForeground(boolean removeNotification)
public final void stopForeground(@StopForegroundFlags int flags)
public final @ForegroundServiceType int getForegroundServiceType()
```

**Foreground service type (API 29+):** Apps targeting Q+ can specify types via `android:foregroundServiceType` in manifest (e.g., `camera`, `location`, `mediaPlayback`, `microphone`). The `startForeground()` overload at line 741 accepts a `foregroundServiceType` parameter that must be a subset of what's declared in manifest.

**Stop foreground flags (lines 320-329):**
- `STOP_FOREGROUND_REMOVE = 1` -- Remove the notification
- `STOP_FOREGROUND_DETACH = 2` -- Detach notification from service (remains visible but independent)

### Self-Stop Methods

| Method | Line | Description |
|--------|------|-------------|
| `stopSelf()` | 601 | Stop the service |
| `stopSelf(int startId)` | 610 | Stop if `startId` is most recent |
| `stopSelfResult(int startId)` | 642 | Returns true if service will stop |

### Hidden/System APIs

| Annotation | Method/Field | Line | Notes |
|-----------|-------------|------|-------|
| `@hide` | `START_TASK_REMOVED_COMPLETE` | 447 | Internal constant for task removal |
| `@hide @Deprecated @UnsupportedAppUsage` | `setForeground(boolean)` | 670 | Legacy no-op, deliberately disabled |
| `@hide @UnsupportedAppUsage` | `attach(...)` | 836 | Internal initialization method |
| `@hide` | `detachAndCleanUp()` | 856 | Leak prevention |

### Security Notes

- `FOREGROUND_SERVICE` permission required for `startForeground()` on API P+ (documented at line 692).
- Service access is enforced via manifest `<service>` declarations and `<uses-permission>`.
- IPC calls can be permission-checked via `checkCallingPermission()`.

---

## 3. Application.java

**File:** `frameworks/base/core/java/android/app/Application.java`
**Lines:** 655
**Since:** API Level 1

### Class Purpose and Responsibility

Maintains global application state. Instantiated before any other component. The class doc explicitly notes (line 43-49) that subclassing Application is usually unnecessary -- static singletons are recommended as a more modular alternative.

**Declaration (line 52):**
```java
public class Application extends ContextWrapper implements ComponentCallbacks2
```

### Lifecycle Methods

| Method | Line | Description |
|--------|------|-------------|
| `onCreate()` | 247 | Called before any Activity/Service/Receiver. Must be fast. `@CallSuper` |
| `onTerminate()` | 257 | **Never called on production devices.** For emulated environments only. `@CallSuper` |
| `onConfigurationChanged(Configuration)` | 261 | Delegates to registered ComponentCallbacks. `@CallSuper` |
| `onLowMemory()` | 271 | Delegates to registered ComponentCallbacks. `@CallSuper` |
| `onTrimMemory(int)` | 281 | Delegates to registered ComponentCallbacks2. `@CallSuper` |

### Key Public APIs

#### Activity Lifecycle Observation (lines 67-206, 305-315)

The `ActivityLifecycleCallbacks` interface provides comprehensive activity lifecycle observation with **Pre/Post** hooks added in API 29:

```java
public interface ActivityLifecycleCallbacks {
    default void onActivityPreCreated(Activity, Bundle)       // API 29
    void onActivityCreated(Activity, Bundle)
    default void onActivityPostCreated(Activity, Bundle)      // API 29
    default void onActivityPreStarted(Activity)               // API 29
    void onActivityStarted(Activity)
    default void onActivityPostStarted(Activity)              // API 29
    default void onActivityPreResumed(Activity)               // API 29
    void onActivityResumed(Activity)
    default void onActivityPostResumed(Activity)              // API 29
    default void onActivityPrePaused(Activity)                // API 29
    void onActivityPaused(Activity)
    default void onActivityPostPaused(Activity)               // API 29
    default void onActivityPreStopped(Activity)               // API 29
    void onActivityStopped(Activity)
    default void onActivityPostStopped(Activity)              // API 29
    default void onActivityPreSaveInstanceState(Activity, Bundle)  // API 29
    void onActivitySaveInstanceState(Activity, Bundle)
    default void onActivityPostSaveInstanceState(Activity, Bundle) // API 29
    default void onActivityPreDestroyed(Activity)             // API 29
    void onActivityDestroyed(Activity)
    default void onActivityPostDestroyed(Activity)            // API 29
}
```

Registration:
```java
public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback)
public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback)
```

#### Component Callbacks (lines 293-303)

```java
public void registerComponentCallbacks(ComponentCallbacks callback)
public void unregisterComponentCallbacks(ComponentCallbacks callback)
```

#### Assist Data (lines 212-221, 317-332)

```java
public interface OnProvideAssistDataListener {
    void onProvideAssistData(Activity activity, Bundle data);
}
public void registerOnProvideAssistDataListener(OnProvideAssistDataListener callback)
public void unregisterOnProvideAssistDataListener(OnProvideAssistDataListener callback)
```

#### Process Name (line 340)

**New in Android 11:**
```java
public static String getProcessName()
```
Returns the current process name. Package default process has the same name as its package.

### Hidden/Internal APIs

| Annotation | Member | Line | Notes |
|-----------|--------|------|-------|
| `@hide @UnsupportedAppUsage` | `mLoadedApk` | 65 | LoadedApk reference |
| `@hide @UnsupportedAppUsage` | `attach(Context)` | 350 | Internal initialization |
| `@hide` | `getAutofillClient()` | 614 | Finds focused activity for autofill when Application context misused |

### Notable Design Pattern

The `getAutofillClient()` override (lines 614-654) contains a notable workaround: when apps use the Application context instead of an Activity context (which breaks autofill), the code searches through `ActivityThread.mActivities` to find the focused Activity as a fallback. The code includes the comment "Okay, ppl use the application context when they should not" (line 623).

---

## 4. Fragment.java

**File:** `frameworks/base/core/java/android/app/Fragment.java`
**Lines:** 2,984
**Since:** API Level 11 (Honeycomb)
**Status:** **`@Deprecated`** (line 265) -- Use `androidx.fragment.app.Fragment` instead

### Class Purpose and Responsibility

A piece of application UI or behavior that can be placed in an Activity. The platform Fragment is deprecated in favor of the AndroidX version for consistent behavior across devices and Lifecycle integration.

**Declaration (line 266):**
```java
@Deprecated
public class Fragment implements ComponentCallbacks2, OnCreateContextMenuListener
```

### Fragment States (lines 271-277)

```java
static final int INVALID_STATE = -1;
static final int INITIALIZING = 0;
static final int CREATED = 1;
static final int ACTIVITY_CREATED = 2;
static final int STOPPED = 3;
static final int STARTED = 4;
static final int RESUMED = 5;
```

### Lifecycle Methods (from class Javadoc, lines 117-150)

**Bringing up to resumed state:**
1. `onAttach()` -- Associated with activity
2. `onCreate()` -- Initial creation
3. `onCreateView()` -- Create and return the view hierarchy
4. `onActivityCreated()` -- Host activity's `onCreate()` completed
5. `onViewStateRestored()` -- View hierarchy state restored
6. `onStart()` -- Visible
7. `onResume()` -- Interacting with user

**Tearing down:**
1. `onPause()`
2. `onStop()`
3. `onDestroyView()` -- Clean up view resources
4. `onDestroy()` -- Final cleanup
5. `onDetach()` -- No longer associated with activity

### Key Design Decision

The platform Fragment is fully deprecated. The recommendation is `androidx.fragment.app.Fragment` which provides:
- Consistent behavior across all API levels
- Integration with the Architecture Components Lifecycle
- Bug fixes independent of platform releases

---

## 5. ActivityManager.java

**File:** `frameworks/base/core/java/android/app/ActivityManager.java`
**Lines:** 4,934
**Since:** API Level 1

### Class Purpose and Responsibility

Provides information about and interaction with activities, services, and processes. Most methods are for debugging/informational purposes and should not affect runtime behavior. Annotated `@SystemService(Context.ACTIVITY_SERVICE)` (line 139).

### Key Public APIs for App Developers

#### Memory Information

| Method | Line | Description |
|--------|------|-------------|
| `isLowRamDevice()` | 962 | Returns true on low-memory devices (< 1GB typically) |
| `getMemoryInfo(MemoryInfo)` | 2804 | Fills MemoryInfo with system memory state |
| `getProcessMemoryInfo(int[])` | 3910 | Debug memory info for given PIDs |
| `getMyMemoryState(RunningAppProcessInfo)` | 3884 | **Static.** Get current process importance and memory state |

#### Application Management

| Method | Line | Description |
|--------|------|-------------|
| `clearApplicationUserData()` | 2992 | Clear all data for calling app (triggers process kill) |
| `getAppTasks()` | 1935 | Get list of tasks associated with calling app |
| `killBackgroundProcesses(String)` | 3940 | Kill background processes of a package. Requires `KILL_BACKGROUND_PROCESSES`. |
| `getRunningAppProcesses()` | 3669 | List of running processes. **Limited on API 22+** to own process for most apps. |

#### Process Importance Levels (lines 3230-3354)

The `RunningAppProcessInfo.Importance` hierarchy (lower = more important):

| Constant | Value | Description |
|----------|-------|-------------|
| `IMPORTANCE_FOREGROUND` | 100 | Top UI, user interacting |
| `IMPORTANCE_FOREGROUND_SERVICE` | 125 | Running foreground service |
| `IMPORTANCE_VISIBLE` | 200 | Visible but not foreground |
| `IMPORTANCE_PERCEPTIBLE` | 230 | Not directly aware but perceptible |
| `IMPORTANCE_SERVICE` | 300 | Background services |
| `IMPORTANCE_TOP_SLEEPING` | 325 | Foreground UI but device asleep |
| `IMPORTANCE_CANT_SAVE_STATE` | 350 | Heavy-weight process |
| `IMPORTANCE_CACHED` | 400 | Cached/background (was `IMPORTANCE_BACKGROUND`) |
| `IMPORTANCE_GONE` | 1000 | Process does not exist |

**Compatibility note (lines 3264-3302):** `IMPORTANCE_PERCEPTIBLE` and `IMPORTANCE_CANT_SAVE_STATE` had incorrect values before API 26. The system returns legacy values for apps targeting < O.

#### Process States (lines 452-488, hidden)

Comprehensive `@ProcessState` enum with values from `PROCESS_STATE_PERSISTENT` (0) through `PROCESS_STATE_CACHED_EMPTY` (19). These are internal (`@hide`) but map to the public Importance levels.

#### UID Importance Listener

```java
public interface OnUidImportanceListener {
    void onUidImportance(int uid, int importance);
}
```

### Inner Classes

| Class | Description |
|-------|-------------|
| `MemoryInfo` | System memory information (total, available, threshold, low-memory flag) |
| `RunningAppProcessInfo` | Running process details (name, pid, uid, importance) |
| `ProcessErrorStateInfo` | Error conditions in processes (crash, ANR) |
| `TaskDescription` | Visual properties of a task (label, icon, colors) |
| `AppTask` | Handle to manage an application's task |
| `RecentTaskInfo` | Information about a recent task |

### Hidden/System APIs

The file contains extensive hidden APIs, including:

| Annotation | Item | Line | Notes |
|-----------|------|------|-------|
| `@hide` | `INSTR_FLAG_DISABLE_HIDDEN_API_CHECKS` | 160 | For instrumentation |
| `@hide` | `INSTR_FLAG_DISABLE_ISOLATED_STORAGE` | 165 | For instrumentation |
| `@hide` | `START_*` constants | 222-350 | Activity start result codes |
| `@hide` | `INTENT_SENDER_*` constants | 405-434 | PendingIntent types |
| `@hide` | `PROCESS_STATE_*` constants | 492+ | Process state granularity |
| `@hide` | `USER_OP_*` constants | 437-449 | User operation results |

### Security Annotations

- `killBackgroundProcesses()` requires `@RequiresPermission(Manifest.permission.KILL_BACKGROUND_PROCESSES)` (verified in implementation).

---

## 6. NotificationManager.java

**File:** `frameworks/base/core/java/android/app/NotificationManager.java`
**Lines:** 2,346
**Since:** API Level 1

### Class Purpose and Responsibility

System service for posting status bar notifications. Manages notification channels (API 26+), channel groups, Do Not Disturb rules, and notification policies. Annotated `@SystemService(Context.NOTIFICATION_SERVICE)` (line 99).

### Key Public APIs

#### Notification Posting and Cancellation

| Method | Line | Description |
|--------|------|-------------|
| `notify(int, Notification)` | 505 | Post notification by ID |
| `notify(String, int, Notification)` | 529 | Post with tag and ID (tag,id pair must be unique) |
| `notifyAsPackage(String, String, int, Notification)` | 556 | Post as a delegated package |
| `cancel(int)` | 627 | Cancel by ID |
| `cancel(String, int)` | 641 | Cancel by tag and ID |
| `cancelAsPackage(String, String, int)` | 665 | Cancel for delegated package |
| `cancelAll()` | 696 | Cancel all notifications |

**Validation (lines 588-610):** `fixNotification()` enforces that apps targeting > LOLLIPOP_MR1 must provide a valid small icon, or `IllegalArgumentException` is thrown.

#### Notification Channels (API 26+)

| Method | Line | Description |
|--------|------|-------------|
| `createNotificationChannel(NotificationChannel)` | 819 | Create or restore a channel. Importance can only be lowered. |
| `createNotificationChannels(List)` | 829 | Batch create channels |
| `getNotificationChannel(String)` | 847 | Get channel settings by ID |
| `getNotificationChannel(String, String)` | 866 | Get channel by ID and conversation ID (API 30) |
| `getNotificationChannels()` | 886 | Get all channels for the calling package |
| `deleteNotificationChannel(String)` | 903 | Delete channel (recreating with same ID restores settings) |
| `createNotificationChannelGroup(NotificationChannelGroup)` | 777 | Create/update channel group |
| `createNotificationChannelGroups(List)` | 786 | Batch create groups |
| `getNotificationChannelGroup(String)` | 917 | Get group by ID |
| `getNotificationChannelGroups()` | 929 | Get all groups |
| `deleteNotificationChannelGroup(String)` | 947 | Delete group and all its channels |

#### Notification Delegation

| Method | Line | Description |
|--------|------|-------------|
| `setNotificationDelegate(String)` | 720 | Allow another package to post notifications on your behalf |
| `getNotificationDelegate()` | 735 | Get current delegate |
| `canNotifyAsPackage(String)` | 751 | Check if you can post as another package |

#### Do Not Disturb / Zen Mode

| Method | Line | Description |
|--------|------|-------------|
| `getCurrentInterruptionFilter()` | -- | Get current DND filter |
| `setInterruptionFilter(int)` | -- | Set DND filter. Requires policy access. |
| `getNotificationPolicy()` | -- | Get notification policy |
| `setNotificationPolicy(Policy)` | -- | Set notification policy |
| `isNotificationPolicyAccessGranted()` | -- | Check if app has DND access |
| `addAutomaticZenRule(AutomaticZenRule)` | -- | Add automatic DND rule |
| `setAutomaticZenRuleState(String, Condition)` | -- | Update rule state |

### Importance Levels (lines 415-453)

| Constant | Value | Description |
|----------|-------|-------------|
| `IMPORTANCE_UNSPECIFIED` | -1000 | No preference expressed (persistence only) |
| `IMPORTANCE_NONE` | 0 | No shade visibility |
| `IMPORTANCE_MIN` | 1 | Below the fold. Not for foreground services. |
| `IMPORTANCE_LOW` | 2 | Shade and potentially status bar, no sound |
| `IMPORTANCE_DEFAULT` | 3 | Shows everywhere, makes noise |
| `IMPORTANCE_HIGH` | 4 | Peeks, may use full-screen intent |
| `IMPORTANCE_MAX` | 5 | Unused |

### Interruption Filter Constants (lines 303-332)

- `INTERRUPTION_FILTER_ALL = 1` -- Normal
- `INTERRUPTION_FILTER_PRIORITY = 2` -- Priority only
- `INTERRUPTION_FILTER_NONE = 3` -- Total silence
- `INTERRUPTION_FILTER_ALARMS = 4` -- Alarms only
- `INTERRUPTION_FILTER_UNKNOWN = 0`

### Broadcast Actions

| Constant | Line | Description |
|----------|------|-------------|
| `ACTION_APP_BLOCK_STATE_CHANGED` | 113 | App blocked/unblocked |
| `ACTION_NOTIFICATION_CHANNEL_BLOCK_STATE_CHANGED` | 129 | Channel blocked/unblocked |
| `ACTION_NOTIFICATION_CHANNEL_GROUP_BLOCK_STATE_CHANGED` | 172 | Channel group blocked/unblocked |
| `ACTION_AUTOMATIC_ZEN_RULE_STATUS_CHANGED` | 187 | DND rule status changed |
| `ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED` | 264 | DND access granted/revoked |
| `ACTION_NOTIFICATION_POLICY_CHANGED` | 272 | Notification policy changed |
| `ACTION_INTERRUPTION_FILTER_CHANGED` | 280 | DND filter changed |

### Hidden/System APIs

| Annotation | Method | Line | Notes |
|-----------|--------|------|-------|
| `@hide @UnsupportedAppUsage` | `notifyAsUser()` | 574 | Post as specific user |
| `@hide @UnsupportedAppUsage` | `cancelAsUser()` | 678 | Cancel as specific user |
| `@hide @UnsupportedAppUsage` | `setZenMode()` | 998 | Direct zen mode control |
| `@hide @TestApi` | `getEffectsSuppressor()` | 960 | Get effects suppressor component |
| `@hide @TestApi` | `matchesCallFilter()` | 973 | Check if extras match call filter |
| `@hide` | `BUBBLE_PREFERENCE_*` | 458-466 | Bubble notification preferences |

---

## 7. AlarmManager.java

**File:** `frameworks/base/core/java/android/app/AlarmManager.java`
**Lines:** 1,158
**Since:** API Level 1

### Class Purpose and Responsibility

System service for scheduling application execution at future times. Supports both wall-clock (RTC) and boot-relative (ELAPSED_REALTIME) timing. As of API 19 (KitKat), alarms are inexact by default for battery optimization. Annotated `@SystemService(Context.ALARM_SERVICE)`.

### Alarm Types (lines 94-120)

| Constant | Value | Clock | Wakes Device |
|----------|-------|-------|-------------|
| `RTC_WAKEUP` | 0 | `System.currentTimeMillis()` | Yes |
| `RTC` | 1 | `System.currentTimeMillis()` | No |
| `ELAPSED_REALTIME_WAKEUP` | 2 | `SystemClock.elapsedRealtime()` | Yes |
| `ELAPSED_REALTIME` | 3 | `SystemClock.elapsedRealtime()` | No |

### Key Public APIs

#### Setting Alarms

| Method | Line | Description |
|--------|------|-------------|
| `set(int, long, PendingIntent)` | 348 | Schedule alarm. **Inexact since API 19.** |
| `set(int, long, String, OnAlarmListener, Handler)` | 375 | Direct callback variant |
| `setExact(int, long, PendingIntent)` | 540 | Precise delivery. Use sparingly. |
| `setExact(int, long, String, OnAlarmListener, Handler)` | 554 | Direct callback exact alarm |
| `setWindow(int, long, long, PendingIntent)` | 486 | Deliver within a time window |
| `setWindow(int, long, long, String, OnAlarmListener, Handler)` | 501 | Window with direct callback |
| `setRepeating(int, long, long, PendingIntent)` | 436 | Repeating alarm. **Inexact since API 19.** |
| `setInexactRepeating(int, long, long, PendingIntent)` | 795 | Explicitly inexact repeating |
| `setAlarmClock(AlarmClockInfo, PendingIntent)` | 607 | Alarm clock. Always exact, wakes from doze. |
| `setAndAllowWhileIdle()` | -- | Execute during doze mode (rate-limited) |
| `setExactAndAllowWhileIdle()` | -- | Exact + doze bypass (rate-limited) |

#### Cancellation

```java
public void cancel(PendingIntent operation)
public void cancel(OnAlarmListener listener)
```

#### Querying

```java
public AlarmClockInfo getNextAlarmClock()
```

### Standard Intervals (lines 708-736)

```java
INTERVAL_FIFTEEN_MINUTES = 15 * 60 * 1000
INTERVAL_HALF_HOUR       = 2 * INTERVAL_FIFTEEN_MINUTES
INTERVAL_HOUR            = 2 * INTERVAL_HALF_HOUR
INTERVAL_HALF_DAY        = 12 * INTERVAL_HOUR
INTERVAL_DAY             = 2 * INTERVAL_HALF_DAY
```

### OnAlarmListener Interface (lines 200-205)

```java
public interface OnAlarmListener {
    public void onAlarm();
}
```
Direct-notification alarms: the requester must be continuously running. Only supports one-shot alarms (not repeating).

### Hidden/System APIs

| Annotation | Method | Line | Notes |
|-----------|--------|------|-------|
| `@hide @UnsupportedAppUsage` | `WINDOW_EXACT = 0` | 135 | Exact window constant |
| `@hide @UnsupportedAppUsage` | `WINDOW_HEURISTIC = -1` | 138 | Heuristic window constant |
| `@hide @UnsupportedAppUsage` | `FLAG_STANDALONE` | 146 | Do not batch |
| `@hide @UnsupportedAppUsage` | `FLAG_WAKE_FROM_IDLE` | 154 | Wake from doze |
| `@hide` | `FLAG_ALLOW_WHILE_IDLE` | 165 | Execute during doze |
| `@hide @UnsupportedAppUsage` | `FLAG_ALLOW_WHILE_IDLE_UNRESTRICTED` | 175 | System-only unrestricted doze bypass |
| `@hide @UnsupportedAppUsage` | `FLAG_IDLE_UNTIL` | 185 | Idle-until marker alarm |
| `@hide @SystemApi` | `set(..., WorkSource)` | 613 | Alarm with WorkSource attribution. Requires `UPDATE_DEVICE_STATS`. |
| `@hide` | `setIdleUntil()` | 565 | Keep alarm manager idle until specified time |

### Notable Design Decision: Backward Compatibility (lines 270-272)

```java
mAlwaysExact = (mTargetSdkVersion < Build.VERSION_CODES.KITKAT);
```
Apps targeting pre-KitKat continue to get exact alarm behavior from the standard `set()` method.

---

## 8. DownloadManager.java

**File:** `frameworks/base/core/java/android/app/DownloadManager.java`
**Lines:** 1,842
**Since:** API Level 9

### Class Purpose and Responsibility

System service for handling long-running HTTP downloads in the background. Handles HTTP interactions, retries, connectivity changes, and system reboots. Requires `android.permission.INTERNET`. Annotated `@SystemService(Context.DOWNLOAD_SERVICE)`.

### Key Public APIs

#### Download Operations

| Method | Line | Description |
|--------|------|-------------|
| `enqueue(Request)` | 1117 | Start a new download. Returns download ID. |
| `remove(long...)` | 1149 | Cancel and remove downloads by ID |
| `query(Query)` | 1159 | Query download status |
| `openDownloadedFile(long)` | 1178 | Open downloaded file as ParcelFileDescriptor |
| `getUriForDownloadedFile(long)` | 1190 | Get content URI for downloaded file |
| `getMimeTypeForDownloadedFile(long)` | 1222 | Get MIME type |
| `addCompletedDownload(...)` | 1435+ | Add already-downloaded file to manager (multiple overloads) |

#### Download Status Constants (lines 197-217)

| Constant | Value | Description |
|----------|-------|-------------|
| `STATUS_PENDING` | 1 | Waiting to start |
| `STATUS_RUNNING` | 2 | Currently running |
| `STATUS_PAUSED` | 4 | Waiting to retry/resume |
| `STATUS_SUCCESSFUL` | 8 | Completed successfully |
| `STATUS_FAILED` | 16 | Failed permanently |

#### Error Codes (lines 222-279)

- `ERROR_UNKNOWN = 1000`
- `ERROR_FILE_ERROR = 1001`
- `ERROR_UNHANDLED_HTTP_CODE = 1002`
- `ERROR_HTTP_DATA_ERROR = 1004`
- `ERROR_TOO_MANY_REDIRECTS = 1005`
- `ERROR_INSUFFICIENT_SPACE = 1006`
- `ERROR_DEVICE_NOT_FOUND = 1007`
- `ERROR_CANNOT_RESUME = 1008`
- `ERROR_FILE_ALREADY_EXISTS = 1009`

#### Pause Reasons (lines 285-302)

- `PAUSED_WAITING_TO_RETRY = 1`
- `PAUSED_WAITING_FOR_NETWORK = 2`
- `PAUSED_QUEUED_FOR_WIFI = 3`
- `PAUSED_UNKNOWN = 4`

### Request Inner Class (line 382)

Builder-style configuration for downloads:

| Method | Description |
|--------|-------------|
| `Request(Uri)` | Constructor. HTTP/HTTPS only. |
| `setDestinationUri(Uri)` | Set download destination. Q+ apps do not need WRITE_EXTERNAL_STORAGE for app-owned dirs. |
| `setDestinationInExternalFilesDir(Context, String, String)` | Save to app's external files dir |
| `setDestinationInExternalPublicDir(String, String)` | Save to public external dir |
| `setAllowedNetworkTypes(int)` | Restrict to `NETWORK_MOBILE` and/or `NETWORK_WIFI` |
| `setAllowedOverRoaming(boolean)` | Allow download while roaming |
| `setAllowedOverMetered(boolean)` | Allow download on metered network |
| `setTitle(CharSequence)` | Notification title |
| `setDescription(CharSequence)` | Notification description |
| `setMimeType(String)` | Set MIME type |
| `addRequestHeader(String, String)` | Add HTTP header |
| `setNotificationVisibility(int)` | Visibility in notifications |

#### Notification Visibility Constants (lines 432-457)

- `VISIBILITY_VISIBLE = 0` -- Visible during progress
- `VISIBILITY_VISIBLE_NOTIFY_COMPLETED = 1` -- Visible during and after
- `VISIBILITY_HIDDEN = 2` -- Hidden
- `VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION = 3` -- Only on completion

### Broadcast Actions

| Constant | Line | Description |
|----------|------|-------------|
| `ACTION_DOWNLOAD_COMPLETE` | 308 | Download completed |
| `ACTION_NOTIFICATION_CLICKED` | 315 | User clicked download notification |
| `ACTION_VIEW_DOWNLOADS` | 322 | View all downloads |

### Hidden/System APIs

| Annotation | Member | Line | Notes |
|-----------|--------|------|-------|
| `@SystemApi` | `ACTION_DOWNLOAD_COMPLETED` | 347 | System-level download complete action |
| `@hide @TestApi` | `COLUMN_MEDIASTORE_URI` | 187 | MediaStore URI column |
| `@hide @UnsupportedAppUsage` | `UNDERLYING_COLUMNS` | 355 | Base columns for queries |
| `@hide @Deprecated` | `NETWORK_BLUETOOTH` | 401 | Bluetooth network type |

### Scoped Storage Impact (line 488-494)

For apps targeting Q+, `setDestinationUri()` no longer requires `WRITE_EXTERNAL_STORAGE` when the path is within app-owned directories or the top-level Downloads directory.

---

## 9. KeyguardManager.java

**File:** `frameworks/base/core/java/android/app/KeyguardManager.java`
**Lines:** 817
**Since:** API Level 1

### Class Purpose and Responsibility

Controls keyguard (lock screen) locking and unlocking. Provides credential confirmation, device security state queries, and keyguard dismissal. Annotated `@SystemService(Context.KEYGUARD_SERVICE)`.

### Key Public APIs

#### Device State Queries

| Method | Line | Description |
|--------|------|-------------|
| `isKeyguardLocked()` | 456 | Is keyguard currently shown? |
| `isKeyguardSecure()` | 472 | PIN/pattern/password set or SIM locked? |
| `isDeviceLocked()` | 499 | Requires credential to unlock? |
| `isDeviceSecure()` | 525 | PIN/pattern/password set? (ignores SIM) |

#### Credential Confirmation

| Method | Line | Description |
|--------|------|-------------|
| `createConfirmDeviceCredentialIntent(CharSequence, CharSequence)` | 148 | **Deprecated.** Use BiometricPrompt. Creates intent to confirm credentials. |

Usage pattern:
```java
Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(title, description);
if (intent != null) {
    startActivityForResult(intent, REQUEST_CODE);
    // Check for RESULT_OK
}
```

#### Keyguard Dismissal (lines 543-628)

```java
public void requestDismissKeyguard(@NonNull Activity activity,
        @Nullable KeyguardDismissCallback callback)
```

The `KeyguardDismissCallback` (lines 399-418) provides:
- `onDismissError()` -- Dismissal not feasible
- `onDismissSucceeded()` -- Successfully dismissed
- `onDismissCancelled()` -- User cancelled

#### Legacy APIs (Deprecated)

| Method | Line | Deprecated Alternative |
|--------|------|----------------------|
| `newKeyguardLock(String)` | 447 | Use `FLAG_DISMISS_KEYGUARD` / `FLAG_SHOW_WHEN_LOCKED` |
| `exitKeyguardSecurely(OnKeyguardExitResult)` | 652 | Use window flags |

The `KeyguardLock` inner class (line 330) provides `disableKeyguard()` and `reenableKeyguard()`, both requiring `DISABLE_KEYGUARD` permission.

### Hidden/System APIs

| Annotation | Method | Line | Notes |
|-----------|--------|------|-------|
| `@hide` | `ACTION_CONFIRM_DEVICE_CREDENTIAL` | 79 | Intent for credential confirmation |
| `@hide` | `ACTION_CONFIRM_DEVICE_CREDENTIAL_WITH_USER` | 86 | Per-user credential confirmation |
| `@hide` | `ACTION_CONFIRM_FRP_CREDENTIAL` | 93 | Factory reset protection |
| `@hide @SystemApi` | `createConfirmFactoryResetCredentialIntent()` | 225 | FRP credential verification |
| `@hide @SystemApi` | `setPrivateNotificationsAllowed(boolean)` | 282 | Control private notification visibility on lockscreen. Requires `CONTROL_KEYGUARD_SECURE_NOTIFICATIONS`. |
| `@hide @SystemApi` | `getPrivateNotificationsAllowed()` | 301 | Query private notification visibility |
| `@hide @SystemApi` | `requestDismissKeyguard(Activity, CharSequence, Callback)` | 599 | Dismiss with message. Requires `SHOW_KEYGUARD_MESSAGE`. |
| `@hide @SystemApi` | `isValidLockPasswordComplexity()` | 690 | Validate password. Requires `SET_INITIAL_LOCK`. |
| `@hide @SystemApi` | `getMinLockLength()` | 719 | Min password length. Requires `SET_INITIAL_LOCK`. |
| `@hide @SystemApi` | `setLock()` | 747 | Set lockscreen password. Requires `SET_INITIAL_LOCK`. Automotive only. |

### Lock Types (lines 807-816, hidden)

```java
@interface LockTypes {
    int PASSWORD = 0;
    int PIN = 1;
    int PATTERN = 2;
}
```

### Security Notes

1. `createConfirmDeviceCredentialIntent()` explicitly sets the package to the Settings app for security (line 156).
2. The `setLock()` method (line 747) is restricted to the initial device setup on Automotive platforms only -- it checks for `FEATURE_AUTOMOTIVE` and refuses if the device is already provisioned.
3. The `checkInitialLockMethodUsage()` method (line 666) enforces `SET_INITIAL_LOCK` permission and Automotive feature gate.

---

## 10. Cross-Cutting Observations

### Design Patterns

1. **System Service Pattern:** Manager classes (`ActivityManager`, `NotificationManager`, `AlarmManager`, `DownloadManager`, `KeyguardManager`) are all annotated with `@SystemService` and obtained via `Context.getSystemService()`. They act as client-side proxies communicating with system server via AIDL interfaces (`INotificationManager`, `IAlarmManager`, `IActivityManager`, etc.).

2. **`@UnsupportedAppUsage` Annotation:** Pervasive across all files, marking internal members that apps have historically accessed via reflection. Many include `maxTargetSdk` to enforce greylisting/blacklisting of hidden API access starting from specific API levels.

3. **Backward Compatibility Layers:** Multiple cases where behavior changes based on `targetSdkVersion`:
   - AlarmManager: exact vs inexact alarms (KitKat boundary)
   - Activity: `onSaveInstanceState` ordering (P boundary)
   - ActivityManager: importance value corrections (O boundary)
   - NotificationManager: small icon enforcement (LOLLIPOP_MR1 boundary)

4. **Lifecycle Callback Symmetry:** Both `Activity` and `Application` implement a symmetric callback dispatch pattern: forward iteration for "winding up" callbacks, reverse iteration for "winding down" callbacks.

5. **`@CallSuper` Enforcement:** Used on `Application.onCreate()`, `Application.onTerminate()`, `Application.onConfigurationChanged()`, `Application.onLowMemory()`, `Application.onTrimMemory()`, and Activity lifecycle methods to enforce super calls.

### Security Patterns

1. **Permission-Gated APIs:**
   - `DISABLE_KEYGUARD` -- KeyguardManager lock/unlock
   - `KILL_BACKGROUND_PROCESSES` -- ActivityManager.killBackgroundProcesses()
   - `FOREGROUND_SERVICE` -- Service.startForeground() (API P+)
   - `UPDATE_DEVICE_STATS` -- AlarmManager system alarm variants
   - `CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS` -- Activity remote animations
   - `CONTROL_KEYGUARD_SECURE_NOTIFICATIONS` -- KeyguardManager private notifications
   - `SET_INITIAL_LOCK` -- KeyguardManager lock setup (Automotive)
   - `SHOW_KEYGUARD_MESSAGE` -- KeyguardManager dismiss with message
   - `INTERNET` -- DownloadManager (documented requirement)

2. **Intent Package Pinning:** `KeyguardManager.createConfirmDeviceCredentialIntent()` explicitly sets the package to Settings to prevent intent hijacking (line 156).

3. **@SystemApi Gatekeeping:** System APIs are available only to apps signed with the platform key or granted special privileges, providing a secondary layer for sensitive operations (translucency conversion, FRP credentials, lock management).

### API Evolution in Android 11

1. **`Application.getProcessName()`** (line 340) -- New static method for retrieving current process name.
2. **`Activity.onPictureInPictureRequested()`** (line 2912) -- New callback for system-initiated PiP suggestions.
3. **`NotificationManager` conversation channels** -- `getNotificationChannel(String, String)` with conversation ID support (line 866).
4. **`Service` foreground service types** -- Extended from Android 10 with additional type enforcement.
5. **Content Capture integration** deepened across Activity and Service for intelligence features.

### File Size Summary

| File | Lines | Complexity |
|------|-------|-----------|
| Activity.java | 8,879 | Highest -- central UI component with extensive API surface |
| ActivityManager.java | 4,934 | High -- many inner classes, process/task management |
| Fragment.java | 2,984 | Moderate -- deprecated, superseded by AndroidX |
| NotificationManager.java | 2,346 | Moderate -- channels, DND, delegation |
| DownloadManager.java | 1,842 | Moderate -- HTTP downloads, Request/Query builders |
| AlarmManager.java | 1,158 | Low-moderate -- alarm scheduling with doze awareness |
| Service.java | 889 | Low -- clean, focused abstraction |
| KeyguardManager.java | 817 | Low -- lock screen state and control |
| Application.java | 655 | Low -- global state and lifecycle observation |
