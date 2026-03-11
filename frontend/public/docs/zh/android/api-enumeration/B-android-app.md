# Android 11 (API 30) 公共 API 枚举：Android 应用

基于 `frameworks/base/api/current.txt` 生成

## 概要

| 包 | 类型数 | 方法数 | 字段数 | 构造函数数 |
|---------|------:|--------:|-------:|------:|
| `android.app` | 149 | 1116 | 490 | 118 |
| `android.app.admin` | 17 | 265 | 227 | 8 |
| `android.app.assist` | 4 | 66 | 5 | 2 |
| `android.app.backup` | 11 | 41 | 4 | 6 |
| `android.app.blob` | 3 | 19 | 0 | 0 |
| `android.app.job` | 8 | 73 | 14 | 7 |
| `android.app.role` | 1 | 2 | 8 | 0 |
| `android.app.slice` | 7 | 53 | 41 | 7 |
| `android.app.usage` | 12 | 86 | 42 | 6 |
| `android.appwidget` | 5 | 59 | 52 | 6 |
| **总计** | **217** | **1780** | **883** | **160** |

---

## 包： `android.app`

### `class abstract ActionBar`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ActionBar()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int DISPLAY_HOME_AS_UP = 4` |  |
| `static final int DISPLAY_SHOW_CUSTOM = 16` |  |
| `static final int DISPLAY_SHOW_HOME = 2` |  |
| `static final int DISPLAY_SHOW_TITLE = 8` |  |
| `static final int DISPLAY_USE_LOGO = 1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `abstract void addOnMenuVisibilityListener(android.app.ActionBar.OnMenuVisibilityListener)` |  |
| `abstract android.view.View getCustomView()` |  |
| `abstract int getDisplayOptions()` |  |
| `float getElevation()` |  |
| `abstract int getHeight()` |  |
| `int getHideOffset()` |  |
| `abstract CharSequence getSubtitle()` |  |
| `android.content.Context getThemedContext()` |  |
| `abstract CharSequence getTitle()` |  |
| `abstract void hide()` |  |
| `boolean isHideOnContentScrollEnabled()` |  |
| `abstract boolean isShowing()` |  |
| `abstract void removeOnMenuVisibilityListener(android.app.ActionBar.OnMenuVisibilityListener)` |  |
| `abstract void setBackgroundDrawable(@Nullable android.graphics.drawable.Drawable)` |  |
| `abstract void setCustomView(android.view.View)` |  |
| `abstract void setCustomView(android.view.View, android.app.ActionBar.LayoutParams)` |  |
| `abstract void setCustomView(@LayoutRes int)` |  |
| `abstract void setDisplayHomeAsUpEnabled(boolean)` |  |
| `abstract void setDisplayOptions(int)` |  |
| `abstract void setDisplayOptions(int, int)` |  |
| `abstract void setDisplayShowCustomEnabled(boolean)` |  |
| `abstract void setDisplayShowHomeEnabled(boolean)` |  |
| `abstract void setDisplayShowTitleEnabled(boolean)` |  |
| `abstract void setDisplayUseLogoEnabled(boolean)` |  |
| `void setElevation(float)` |  |
| `void setHideOffset(int)` |  |
| `void setHideOnContentScrollEnabled(boolean)` |  |
| `void setHomeActionContentDescription(CharSequence)` |  |
| `void setHomeActionContentDescription(@StringRes int)` |  |
| `void setHomeAsUpIndicator(android.graphics.drawable.Drawable)` |  |
| `void setHomeAsUpIndicator(@DrawableRes int)` |  |
| `void setHomeButtonEnabled(boolean)` |  |
| `abstract void setIcon(@DrawableRes int)` |  |
| `abstract void setIcon(android.graphics.drawable.Drawable)` |  |
| `abstract void setLogo(@DrawableRes int)` |  |
| `abstract void setLogo(android.graphics.drawable.Drawable)` |  |
| `void setSplitBackgroundDrawable(android.graphics.drawable.Drawable)` |  |
| `void setStackedBackgroundDrawable(android.graphics.drawable.Drawable)` |  |
| `abstract void setSubtitle(CharSequence)` |  |
| `abstract void setSubtitle(@StringRes int)` |  |
| `abstract void setTitle(CharSequence)` |  |
| `abstract void setTitle(@StringRes int)` |  |
| `abstract void show()` |  |

---

### `class static ActionBar.LayoutParams`

- **继承：** `android.view.ViewGroup.MarginLayoutParams`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ActionBar.LayoutParams(@NonNull android.content.Context, android.util.AttributeSet)` |  |
| `ActionBar.LayoutParams(int, int)` |  |
| `ActionBar.LayoutParams(int, int, int)` |  |
| `ActionBar.LayoutParams(int)` |  |
| `ActionBar.LayoutParams(android.app.ActionBar.LayoutParams)` |  |
| `ActionBar.LayoutParams(android.view.ViewGroup.LayoutParams)` |  |

---

### `interface static ActionBar.OnMenuVisibilityListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onMenuVisibilityChanged(boolean)` |  |

---

### `interface static ActionBar.OnNavigationListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static ActionBar.TabListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class Activity`

- **继承：** `android.view.ContextThemeWrapper`
- **实现：** `android.content.ComponentCallbacks2 android.view.KeyEvent.Callback android.view.LayoutInflater.Factory2 android.view.View.OnCreateContextMenuListener android.view.Window.Callback`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Activity()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int DEFAULT_KEYS_DIALER = 1` |  |
| `static final int DEFAULT_KEYS_DISABLE = 0` |  |
| `static final int DEFAULT_KEYS_SEARCH_GLOBAL = 4` |  |
| `static final int DEFAULT_KEYS_SEARCH_LOCAL = 3` |  |
| `static final int DEFAULT_KEYS_SHORTCUT = 2` |  |
| `static final int[] FOCUSED_STATE_SET` |  |
| `static final int RESULT_CANCELED = 0` |  |
| `static final int RESULT_FIRST_USER = 1` |  |
| `static final int RESULT_OK = -1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void addContentView(android.view.View, android.view.ViewGroup.LayoutParams)` |  |
| `void closeContextMenu()` |  |
| `void closeOptionsMenu()` |  |
| `android.app.PendingIntent createPendingResult(int, @NonNull android.content.Intent, int)` |  |
| `final void dismissKeyboardShortcutsHelper()` |  |
| `boolean dispatchGenericMotionEvent(android.view.MotionEvent)` |  |
| `boolean dispatchKeyEvent(android.view.KeyEvent)` |  |
| `boolean dispatchKeyShortcutEvent(android.view.KeyEvent)` |  |
| `boolean dispatchPopulateAccessibilityEvent(android.view.accessibility.AccessibilityEvent)` |  |
| `boolean dispatchTouchEvent(android.view.MotionEvent)` |  |
| `boolean dispatchTrackballEvent(android.view.MotionEvent)` |  |
| `void dump(@NonNull String, @Nullable java.io.FileDescriptor, @NonNull java.io.PrintWriter, @Nullable String[])` |  |
| `boolean enterPictureInPictureMode(@NonNull android.app.PictureInPictureParams)` |  |
| `<T extends android.view.View> T findViewById(@IdRes int)` |  |
| `void finish()` |  |
| `void finishActivity(int)` |  |
| `void finishAffinity()` |  |
| `void finishAfterTransition()` |  |
| `void finishAndRemoveTask()` |  |
| `final android.app.Application getApplication()` |  |
| `int getChangingConfigurations()` |  |
| `android.content.ComponentName getComponentName()` |  |
| `android.transition.Scene getContentScene()` |  |
| `android.transition.TransitionManager getContentTransitionManager()` |  |
| `android.content.Intent getIntent()` |  |
| `int getMaxNumPictureInPictureActions()` |  |
| `final android.media.session.MediaController getMediaController()` |  |
| `final android.app.Activity getParent()` |  |
| `android.content.SharedPreferences getPreferences(int)` |  |
| `int getRequestedOrientation()` |  |
| `final android.view.SearchEvent getSearchEvent()` |  |
| `int getTaskId()` |  |
| `final CharSequence getTitle()` |  |
| `final int getTitleColor()` |  |
| `android.app.VoiceInteractor getVoiceInteractor()` |  |
| `final int getVolumeControlStream()` |  |
| `android.view.Window getWindow()` |  |
| `android.view.WindowManager getWindowManager()` |  |
| `boolean hasWindowFocus()` |  |
| `void invalidateOptionsMenu()` |  |
| `boolean isActivityTransitionRunning()` |  |
| `boolean isChangingConfigurations()` |  |
| `final boolean isChild()` |  |
| `boolean isDestroyed()` |  |
| `boolean isFinishing()` |  |
| `boolean isImmersive()` |  |
| `boolean isInMultiWindowMode()` |  |
| `boolean isInPictureInPictureMode()` |  |
| `boolean isLocalVoiceInteractionSupported()` |  |
| `boolean isTaskRoot()` |  |
| `boolean isVoiceInteraction()` |  |
| `boolean isVoiceInteractionRoot()` |  |
| `boolean moveTaskToBack(boolean)` |  |
| `boolean navigateUpTo(android.content.Intent)` |  |
| `void onActivityReenter(int, android.content.Intent)` |  |
| `void onActivityResult(int, int, android.content.Intent)` |  |
| `void onAttachedToWindow()` |  |
| `void onBackPressed()` |  |
| `void onChildTitleChanged(android.app.Activity, CharSequence)` |  |
| `void onConfigurationChanged(@NonNull android.content.res.Configuration)` |  |
| `void onContentChanged()` |  |
| `boolean onContextItemSelected(@NonNull android.view.MenuItem)` |  |
| `void onContextMenuClosed(@NonNull android.view.Menu)` |  |
| `void onCreate(@Nullable android.os.Bundle, @Nullable android.os.PersistableBundle)` |  |
| `void onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)` |  |
| `void onCreateNavigateUpTaskStack(android.app.TaskStackBuilder)` |  |
| `boolean onCreateOptionsMenu(android.view.Menu)` |  |
| `boolean onCreatePanelMenu(int, @NonNull android.view.Menu)` |  |
| `void onDetachedFromWindow()` |  |
| `void onEnterAnimationComplete()` |  |
| `boolean onGenericMotionEvent(android.view.MotionEvent)` |  |
| `void onGetDirectActions(@NonNull android.os.CancellationSignal, @NonNull java.util.function.Consumer<java.util.List<android.app.DirectAction>>)` |  |
| `boolean onKeyDown(int, android.view.KeyEvent)` |  |
| `boolean onKeyLongPress(int, android.view.KeyEvent)` |  |
| `boolean onKeyMultiple(int, int, android.view.KeyEvent)` |  |
| `boolean onKeyShortcut(int, android.view.KeyEvent)` |  |
| `boolean onKeyUp(int, android.view.KeyEvent)` |  |
| `void onLocalVoiceInteractionStarted()` |  |
| `void onLocalVoiceInteractionStopped()` |  |
| `void onLowMemory()` |  |
| `boolean onMenuItemSelected(int, @NonNull android.view.MenuItem)` |  |
| `boolean onMenuOpened(int, @NonNull android.view.Menu)` |  |
| `void onMultiWindowModeChanged(boolean, android.content.res.Configuration)` |  |
| `boolean onNavigateUp()` |  |
| `void onNewIntent(android.content.Intent)` |  |
| `boolean onOptionsItemSelected(@NonNull android.view.MenuItem)` |  |
| `void onOptionsMenuClosed(android.view.Menu)` |  |
| `void onPanelClosed(int, @NonNull android.view.Menu)` |  |
| `void onPerformDirectAction(@NonNull String, @NonNull android.os.Bundle, @NonNull android.os.CancellationSignal, @NonNull java.util.function.Consumer<android.os.Bundle>)` |  |
| `void onPictureInPictureModeChanged(boolean, android.content.res.Configuration)` |  |
| `boolean onPictureInPictureRequested()` |  |
| `void onPostCreate(@Nullable android.os.Bundle, @Nullable android.os.PersistableBundle)` |  |
| `void onPrepareNavigateUpTaskStack(android.app.TaskStackBuilder)` |  |
| `boolean onPrepareOptionsMenu(android.view.Menu)` |  |
| `boolean onPreparePanel(int, @Nullable android.view.View, @NonNull android.view.Menu)` |  |
| `void onProvideAssistContent(android.app.assist.AssistContent)` |  |
| `void onProvideAssistData(android.os.Bundle)` |  |
| `android.net.Uri onProvideReferrer()` |  |
| `void onRequestPermissionsResult(int, @NonNull String[], @NonNull int[])` |  |
| `void onRestoreInstanceState(@NonNull android.os.Bundle)` |  |
| `void onRestoreInstanceState(@Nullable android.os.Bundle, @Nullable android.os.PersistableBundle)` |  |
| `Object onRetainNonConfigurationInstance()` |  |
| `void onSaveInstanceState(@NonNull android.os.Bundle)` |  |
| `void onSaveInstanceState(@NonNull android.os.Bundle, @NonNull android.os.PersistableBundle)` |  |
| `boolean onSearchRequested(@Nullable android.view.SearchEvent)` |  |
| `boolean onSearchRequested()` |  |
| `void onTitleChanged(CharSequence, int)` |  |
| `void onTopResumedActivityChanged(boolean)` |  |
| `boolean onTouchEvent(android.view.MotionEvent)` |  |
| `boolean onTrackballEvent(android.view.MotionEvent)` |  |
| `void onTrimMemory(int)` |  |
| `void onUserInteraction()` |  |
| `void onUserLeaveHint()` |  |
| `void onWindowAttributesChanged(android.view.WindowManager.LayoutParams)` |  |
| `void onWindowFocusChanged(boolean)` |  |
| `void openContextMenu(android.view.View)` |  |
| `void openOptionsMenu()` |  |
| `void overridePendingTransition(int, int)` |  |
| `void postponeEnterTransition()` |  |
| `void recreate()` |  |
| `void registerActivityLifecycleCallbacks(@NonNull android.app.Application.ActivityLifecycleCallbacks)` |  |
| `void registerForContextMenu(android.view.View)` |  |
| `boolean releaseInstance()` |  |
| `void reportFullyDrawn()` |  |
| `android.view.DragAndDropPermissions requestDragAndDropPermissions(android.view.DragEvent)` |  |
| `final void requestPermissions(@NonNull String[], int)` |  |
| `final void requestShowKeyboardShortcuts()` |  |
| `final boolean requestWindowFeature(int)` |  |
| `final void runOnUiThread(Runnable)` |  |
| `void setActionBar(@Nullable android.widget.Toolbar)` |  |
| `void setContentTransitionManager(android.transition.TransitionManager)` |  |
| `void setContentView(@LayoutRes int)` |  |
| `void setContentView(android.view.View)` |  |
| `void setContentView(android.view.View, android.view.ViewGroup.LayoutParams)` |  |
| `final void setDefaultKeyMode(int)` |  |
| `void setEnterSharedElementCallback(android.app.SharedElementCallback)` |  |
| `void setExitSharedElementCallback(android.app.SharedElementCallback)` |  |
| `final void setFeatureDrawable(int, android.graphics.drawable.Drawable)` |  |
| `final void setFeatureDrawableAlpha(int, int)` |  |
| `final void setFeatureDrawableResource(int, @DrawableRes int)` |  |
| `final void setFeatureDrawableUri(int, android.net.Uri)` |  |
| `void setFinishOnTouchOutside(boolean)` |  |
| `void setImmersive(boolean)` |  |
| `void setInheritShowWhenLocked(boolean)` |  |
| `void setIntent(android.content.Intent)` |  |
| `void setLocusContext(@Nullable android.content.LocusId, @Nullable android.os.Bundle)` |  |
| `final void setMediaController(android.media.session.MediaController)` |  |
| `void setPictureInPictureParams(@NonNull android.app.PictureInPictureParams)` |  |
| `void setRequestedOrientation(int)` |  |
| `final void setResult(int)` |  |
| `final void setResult(int, android.content.Intent)` |  |
| `void setShowWhenLocked(boolean)` |  |
| `void setTaskDescription(android.app.ActivityManager.TaskDescription)` |  |
| `void setTitle(CharSequence)` |  |
| `void setTitle(int)` |  |
| `boolean setTranslucent(boolean)` |  |
| `void setTurnScreenOn(boolean)` |  |
| `void setVisible(boolean)` |  |
| `final void setVolumeControlStream(int)` |  |
| `void setVrModeEnabled(boolean, @NonNull android.content.ComponentName) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `boolean shouldShowRequestPermissionRationale(@NonNull String)` |  |
| `boolean shouldUpRecreateTask(android.content.Intent)` |  |
| `boolean showAssist(android.os.Bundle)` |  |
| `void showLockTaskEscapeMessage()` |  |
| `void startActivityForResult(@RequiresPermission android.content.Intent, int)` |  |
| `void startActivityForResult(@RequiresPermission android.content.Intent, int, @Nullable android.os.Bundle)` |  |
| `boolean startActivityIfNeeded(@NonNull @RequiresPermission android.content.Intent, int)` |  |
| `boolean startActivityIfNeeded(@NonNull @RequiresPermission android.content.Intent, int, @Nullable android.os.Bundle)` |  |
| `void startIntentSenderForResult(android.content.IntentSender, int, @Nullable android.content.Intent, int, int, int) throws android.content.IntentSender.SendIntentException` |  |
| `void startIntentSenderForResult(android.content.IntentSender, int, @Nullable android.content.Intent, int, int, int, android.os.Bundle) throws android.content.IntentSender.SendIntentException` |  |
| `void startLocalVoiceInteraction(android.os.Bundle)` |  |
| `void startLockTask()` |  |
| `boolean startNextMatchingActivity(@NonNull @RequiresPermission android.content.Intent)` |  |
| `boolean startNextMatchingActivity(@NonNull @RequiresPermission android.content.Intent, @Nullable android.os.Bundle)` |  |
| `void startPostponedEnterTransition()` |  |
| `void startSearch(@Nullable String, boolean, @Nullable android.os.Bundle, boolean)` |  |
| `void stopLocalVoiceInteraction()` |  |
| `void stopLockTask()` |  |
| `void takeKeyEvents(boolean)` |  |
| `void triggerSearch(String, @Nullable android.os.Bundle)` |  |
| `void unregisterActivityLifecycleCallbacks(@NonNull android.app.Application.ActivityLifecycleCallbacks)` |  |
| `void unregisterForContextMenu(android.view.View)` |  |

---

### `class ActivityGroup` ~~DEPRECATED~~

- **继承：** `android.app.Activity`
- **Annotations:** `@Deprecated`

---

### `class ActivityManager`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ACTION_REPORT_HEAP_LIMIT = "android.app.action.REPORT_HEAP_LIMIT"` |  |
| `static final int LOCK_TASK_MODE_LOCKED = 1` |  |
| `static final int LOCK_TASK_MODE_NONE = 0` |  |
| `static final int LOCK_TASK_MODE_PINNED = 2` |  |
| `static final String META_HOME_ALTERNATE = "android.app.home.alternate"` |  |
| `static final int MOVE_TASK_NO_USER_ACTION = 2` |  |
| `static final int MOVE_TASK_WITH_HOME = 1` |  |
| `static final int RECENT_IGNORE_UNAVAILABLE = 2` |  |
| `static final int RECENT_WITH_EXCLUDED = 1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int addAppTask(@NonNull android.app.Activity, @NonNull android.content.Intent, @Nullable android.app.ActivityManager.TaskDescription, @NonNull android.graphics.Bitmap)` |  |
| `void appNotResponding(@NonNull String)` |  |
| `boolean clearApplicationUserData()` |  |
| `void clearWatchHeapLimit()` |  |
| `android.util.Size getAppTaskThumbnailSize()` |  |
| `java.util.List<android.app.ActivityManager.AppTask> getAppTasks()` |  |
| `android.content.pm.ConfigurationInfo getDeviceConfigurationInfo()` |  |
| `int getLargeMemoryClass()` |  |
| `int getLauncherLargeIconDensity()` |  |
| `int getLauncherLargeIconSize()` |  |
| `int getLockTaskModeState()` |  |
| `int getMemoryClass()` |  |
| `void getMemoryInfo(android.app.ActivityManager.MemoryInfo)` |  |
| `static void getMyMemoryState(android.app.ActivityManager.RunningAppProcessInfo)` |  |
| `android.os.Debug.MemoryInfo[] getProcessMemoryInfo(int[])` |  |
| `java.util.List<android.app.ActivityManager.ProcessErrorStateInfo> getProcessesInErrorState()` |  |
| `java.util.List<android.app.ActivityManager.RunningAppProcessInfo> getRunningAppProcesses()` |  |
| `android.app.PendingIntent getRunningServiceControlPanel(android.content.ComponentName) throws java.lang.SecurityException` |  |
| `boolean isActivityStartAllowedOnDisplay(@NonNull android.content.Context, int, @NonNull android.content.Intent)` |  |
| `boolean isBackgroundRestricted()` |  |
| `static boolean isLowMemoryKillReportSupported()` |  |
| `boolean isLowRamDevice()` |  |
| `static boolean isRunningInUserTestHarness()` |  |
| `static boolean isUserAMonkey()` |  |
| `void setProcessStateSummary(@Nullable byte[])` |  |
| `static void setVrThread(int)` |  |
| `void setWatchHeapLimit(long)` |  |

---

### `class static ActivityManager.AppTask`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void finishAndRemoveTask()` |  |
| `android.app.ActivityManager.RecentTaskInfo getTaskInfo()` |  |
| `void moveToFront()` |  |
| `void setExcludeFromRecents(boolean)` |  |
| `void startActivity(android.content.Context, android.content.Intent, android.os.Bundle)` |  |

---

### `class static ActivityManager.MemoryInfo`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ActivityManager.MemoryInfo()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `long availMem` |  |
| `boolean lowMemory` |  |
| `long threshold` |  |
| `long totalMem` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void readFromParcel(android.os.Parcel)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ActivityManager.ProcessErrorStateInfo`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ActivityManager.ProcessErrorStateInfo()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int CRASHED = 1` |  |
| `static final int NOT_RESPONDING = 2` |  |
| `static final int NO_ERROR = 0` |  |
| `int condition` |  |
| `byte[] crashData` |  |
| `String longMsg` |  |
| `int pid` |  |
| `String processName` |  |
| `String shortMsg` |  |
| `String stackTrace` |  |
| `String tag` |  |
| `int uid` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void readFromParcel(android.os.Parcel)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ActivityManager.RecentTaskInfo`

- **继承：** `android.app.TaskInfo`
- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ActivityManager.RecentTaskInfo()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void readFromParcel(android.os.Parcel)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ActivityManager.RunningAppProcessInfo`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ActivityManager.RunningAppProcessInfo()` |  |
| `ActivityManager.RunningAppProcessInfo(String, int, String[])` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int IMPORTANCE_CACHED = 400` |  |
| `static final int IMPORTANCE_CANT_SAVE_STATE = 350` |  |
| `static final int IMPORTANCE_FOREGROUND = 100` |  |
| `static final int IMPORTANCE_FOREGROUND_SERVICE = 125` |  |
| `static final int IMPORTANCE_GONE = 1000` |  |
| `static final int IMPORTANCE_PERCEPTIBLE = 230` |  |
| `static final int IMPORTANCE_PERCEPTIBLE_PRE_26 = 130` |  |
| `static final int IMPORTANCE_SERVICE = 300` |  |
| `static final int IMPORTANCE_TOP_SLEEPING = 325` |  |
| `static final int IMPORTANCE_VISIBLE = 200` |  |
| `static final int REASON_PROVIDER_IN_USE = 1` |  |
| `static final int REASON_SERVICE_IN_USE = 2` |  |
| `static final int REASON_UNKNOWN = 0` |  |
| `int importance` |  |
| `int importanceReasonCode` |  |
| `android.content.ComponentName importanceReasonComponent` |  |
| `int importanceReasonPid` |  |
| `int lastTrimLevel` |  |
| `int lru` |  |
| `int pid` |  |
| `String[] pkgList` |  |
| `String processName` |  |
| `int uid` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void readFromParcel(android.os.Parcel)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ActivityManager.RunningServiceInfo`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ActivityManager.RunningServiceInfo()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int FLAG_FOREGROUND = 2` |  |
| `static final int FLAG_PERSISTENT_PROCESS = 8` |  |
| `static final int FLAG_STARTED = 1` |  |
| `static final int FLAG_SYSTEM_PROCESS = 4` |  |
| `long activeSince` |  |
| `int clientCount` |  |
| `int clientLabel` |  |
| `String clientPackage` |  |
| `int crashCount` |  |
| `int flags` |  |
| `boolean foreground` |  |
| `long lastActivityTime` |  |
| `int pid` |  |
| `String process` |  |
| `long restarting` |  |
| `android.content.ComponentName service` |  |
| `boolean started` |  |
| `int uid` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void readFromParcel(android.os.Parcel)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ActivityManager.RunningTaskInfo`

- **继承：** `android.app.TaskInfo`
- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ActivityManager.RunningTaskInfo()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void readFromParcel(android.os.Parcel)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ActivityManager.TaskDescription`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ActivityManager.TaskDescription(String, @DrawableRes int, int)` |  |
| `ActivityManager.TaskDescription(String, @DrawableRes int)` |  |
| `ActivityManager.TaskDescription(String)` |  |
| `ActivityManager.TaskDescription()` |  |
| `ActivityManager.TaskDescription(android.app.ActivityManager.TaskDescription)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getLabel()` |  |
| `int getPrimaryColor()` |  |
| `void readFromParcel(android.os.Parcel)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class ActivityOptions`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String EXTRA_USAGE_TIME_REPORT = "android.activity.usage_time"` |  |
| `static final String EXTRA_USAGE_TIME_REPORT_PACKAGES = "android.usage_time_packages"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int getLaunchDisplayId()` |  |
| `boolean getLockTaskMode()` |  |
| `static android.app.ActivityOptions makeBasic()` |  |
| `static android.app.ActivityOptions makeClipRevealAnimation(android.view.View, int, int, int, int)` |  |
| `static android.app.ActivityOptions makeCustomAnimation(android.content.Context, int, int)` |  |
| `static android.app.ActivityOptions makeScaleUpAnimation(android.view.View, int, int, int, int)` |  |
| `static android.app.ActivityOptions makeSceneTransitionAnimation(android.app.Activity, android.view.View, String)` |  |
| `static android.app.ActivityOptions makeTaskLaunchBehind()` |  |
| `static android.app.ActivityOptions makeThumbnailScaleUpAnimation(android.view.View, android.graphics.Bitmap, int, int)` |  |
| `void requestUsageTimeReport(android.app.PendingIntent)` |  |
| `android.app.ActivityOptions setAppVerificationBundle(android.os.Bundle)` |  |
| `android.app.ActivityOptions setLaunchBounds(@Nullable android.graphics.Rect)` |  |
| `android.app.ActivityOptions setLaunchDisplayId(int)` |  |
| `android.app.ActivityOptions setLockTaskEnabled(boolean)` |  |
| `android.os.Bundle toBundle()` |  |
| `void update(android.app.ActivityOptions)` |  |

---

### `class AlarmManager`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ACTION_NEXT_ALARM_CLOCK_CHANGED = "android.app.action.NEXT_ALARM_CLOCK_CHANGED"` |  |
| `static final int ELAPSED_REALTIME = 3` |  |
| `static final int ELAPSED_REALTIME_WAKEUP = 2` |  |
| `static final long INTERVAL_DAY = 86400000L` |  |
| `static final long INTERVAL_FIFTEEN_MINUTES = 900000L` |  |
| `static final long INTERVAL_HALF_DAY = 43200000L` |  |
| `static final long INTERVAL_HALF_HOUR = 1800000L` |  |
| `static final long INTERVAL_HOUR = 3600000L` |  |
| `static final int RTC = 1` |  |
| `static final int RTC_WAKEUP = 0` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void cancel(android.app.PendingIntent)` |  |
| `void cancel(android.app.AlarmManager.OnAlarmListener)` |  |
| `android.app.AlarmManager.AlarmClockInfo getNextAlarmClock()` |  |
| `void set(int, long, android.app.PendingIntent)` |  |
| `void set(int, long, String, android.app.AlarmManager.OnAlarmListener, android.os.Handler)` |  |
| `void setAlarmClock(android.app.AlarmManager.AlarmClockInfo, android.app.PendingIntent)` |  |
| `void setAndAllowWhileIdle(int, long, android.app.PendingIntent)` |  |
| `void setExact(int, long, android.app.PendingIntent)` |  |
| `void setExact(int, long, String, android.app.AlarmManager.OnAlarmListener, android.os.Handler)` |  |
| `void setExactAndAllowWhileIdle(int, long, android.app.PendingIntent)` |  |
| `void setInexactRepeating(int, long, long, android.app.PendingIntent)` |  |
| `void setRepeating(int, long, long, android.app.PendingIntent)` |  |
| `void setWindow(int, long, long, android.app.PendingIntent)` |  |
| `void setWindow(int, long, long, String, android.app.AlarmManager.OnAlarmListener, android.os.Handler)` |  |

---

### `class static final AlarmManager.AlarmClockInfo`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AlarmManager.AlarmClockInfo(long, android.app.PendingIntent)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.app.PendingIntent getShowIntent()` |  |
| `long getTriggerTime()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface static AlarmManager.OnAlarmListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onAlarm()` |  |

---

### `class AlertDialog`

- **继承：** `android.app.Dialog`
- **实现：** `android.content.DialogInterface`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AlertDialog(android.content.Context)` |  |
| `AlertDialog(android.content.Context, boolean, android.content.DialogInterface.OnCancelListener)` |  |
| `AlertDialog(android.content.Context, @StyleRes int)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.widget.Button getButton(int)` |  |
| `android.widget.ListView getListView()` |  |
| `void setButton(int, CharSequence, android.os.Message)` |  |
| `void setButton(int, CharSequence, android.content.DialogInterface.OnClickListener)` |  |
| `void setCustomTitle(android.view.View)` |  |
| `void setIcon(@DrawableRes int)` |  |
| `void setIcon(android.graphics.drawable.Drawable)` |  |
| `void setIconAttribute(@AttrRes int)` |  |
| `void setInverseBackgroundForced(boolean)` |  |
| `void setMessage(CharSequence)` |  |
| `void setView(android.view.View)` |  |
| `void setView(android.view.View, int, int, int, int)` |  |

---

### `class static AlertDialog.Builder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AlertDialog.Builder(android.content.Context)` |  |
| `AlertDialog.Builder(android.content.Context, int)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.AlertDialog create()` |  |
| `android.content.Context getContext()` |  |
| `android.app.AlertDialog.Builder setAdapter(android.widget.ListAdapter, android.content.DialogInterface.OnClickListener)` |  |
| `android.app.AlertDialog.Builder setCancelable(boolean)` |  |
| `android.app.AlertDialog.Builder setCursor(android.database.Cursor, android.content.DialogInterface.OnClickListener, String)` |  |
| `android.app.AlertDialog.Builder setCustomTitle(android.view.View)` |  |
| `android.app.AlertDialog.Builder setIcon(@DrawableRes int)` |  |
| `android.app.AlertDialog.Builder setIcon(android.graphics.drawable.Drawable)` |  |
| `android.app.AlertDialog.Builder setIconAttribute(@AttrRes int)` |  |
| `android.app.AlertDialog.Builder setItems(@ArrayRes int, android.content.DialogInterface.OnClickListener)` |  |
| `android.app.AlertDialog.Builder setItems(CharSequence[], android.content.DialogInterface.OnClickListener)` |  |
| `android.app.AlertDialog.Builder setMessage(@StringRes int)` |  |
| `android.app.AlertDialog.Builder setMessage(CharSequence)` |  |
| `android.app.AlertDialog.Builder setMultiChoiceItems(@ArrayRes int, boolean[], android.content.DialogInterface.OnMultiChoiceClickListener)` |  |
| `android.app.AlertDialog.Builder setMultiChoiceItems(CharSequence[], boolean[], android.content.DialogInterface.OnMultiChoiceClickListener)` |  |
| `android.app.AlertDialog.Builder setMultiChoiceItems(android.database.Cursor, String, String, android.content.DialogInterface.OnMultiChoiceClickListener)` |  |
| `android.app.AlertDialog.Builder setNegativeButton(@StringRes int, android.content.DialogInterface.OnClickListener)` |  |
| `android.app.AlertDialog.Builder setNegativeButton(CharSequence, android.content.DialogInterface.OnClickListener)` |  |
| `android.app.AlertDialog.Builder setNeutralButton(@StringRes int, android.content.DialogInterface.OnClickListener)` |  |
| `android.app.AlertDialog.Builder setNeutralButton(CharSequence, android.content.DialogInterface.OnClickListener)` |  |
| `android.app.AlertDialog.Builder setOnCancelListener(android.content.DialogInterface.OnCancelListener)` |  |
| `android.app.AlertDialog.Builder setOnDismissListener(android.content.DialogInterface.OnDismissListener)` |  |
| `android.app.AlertDialog.Builder setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener)` |  |
| `android.app.AlertDialog.Builder setOnKeyListener(android.content.DialogInterface.OnKeyListener)` |  |
| `android.app.AlertDialog.Builder setPositiveButton(@StringRes int, android.content.DialogInterface.OnClickListener)` |  |
| `android.app.AlertDialog.Builder setPositiveButton(CharSequence, android.content.DialogInterface.OnClickListener)` |  |
| `android.app.AlertDialog.Builder setSingleChoiceItems(@ArrayRes int, int, android.content.DialogInterface.OnClickListener)` |  |
| `android.app.AlertDialog.Builder setSingleChoiceItems(android.database.Cursor, int, String, android.content.DialogInterface.OnClickListener)` |  |
| `android.app.AlertDialog.Builder setSingleChoiceItems(CharSequence[], int, android.content.DialogInterface.OnClickListener)` |  |
| `android.app.AlertDialog.Builder setSingleChoiceItems(android.widget.ListAdapter, int, android.content.DialogInterface.OnClickListener)` |  |
| `android.app.AlertDialog.Builder setTitle(@StringRes int)` |  |
| `android.app.AlertDialog.Builder setTitle(CharSequence)` |  |
| `android.app.AlertDialog.Builder setView(int)` |  |
| `android.app.AlertDialog.Builder setView(android.view.View)` |  |
| `android.app.AlertDialog show()` |  |

---

### `class AliasActivity` ~~DEPRECATED~~

- **继承：** `android.app.Activity`
- **Annotations:** `@Deprecated`

---

### `class AppComponentFactory`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AppComponentFactory()` |  |

---

### `class AppOpsManager`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int MODE_ALLOWED = 0` |  |
| `static final int MODE_DEFAULT = 3` |  |
| `static final int MODE_ERRORED = 2` |  |
| `static final int MODE_FOREGROUND = 4` |  |
| `static final int MODE_IGNORED = 1` |  |
| `static final String OPSTR_ADD_VOICEMAIL = "android:add_voicemail"` |  |
| `static final String OPSTR_ANSWER_PHONE_CALLS = "android:answer_phone_calls"` |  |
| `static final String OPSTR_BODY_SENSORS = "android:body_sensors"` |  |
| `static final String OPSTR_CALL_PHONE = "android:call_phone"` |  |
| `static final String OPSTR_CAMERA = "android:camera"` |  |
| `static final String OPSTR_COARSE_LOCATION = "android:coarse_location"` |  |
| `static final String OPSTR_FINE_LOCATION = "android:fine_location"` |  |
| `static final String OPSTR_GET_USAGE_STATS = "android:get_usage_stats"` |  |
| `static final String OPSTR_MOCK_LOCATION = "android:mock_location"` |  |
| `static final String OPSTR_MONITOR_HIGH_POWER_LOCATION = "android:monitor_location_high_power"` |  |
| `static final String OPSTR_MONITOR_LOCATION = "android:monitor_location"` |  |
| `static final String OPSTR_PICTURE_IN_PICTURE = "android:picture_in_picture"` |  |
| `static final String OPSTR_PROCESS_OUTGOING_CALLS = "android:process_outgoing_calls"` |  |
| `static final String OPSTR_READ_CALENDAR = "android:read_calendar"` |  |
| `static final String OPSTR_READ_CALL_LOG = "android:read_call_log"` |  |
| `static final String OPSTR_READ_CELL_BROADCASTS = "android:read_cell_broadcasts"` |  |
| `static final String OPSTR_READ_CONTACTS = "android:read_contacts"` |  |
| `static final String OPSTR_READ_EXTERNAL_STORAGE = "android:read_external_storage"` |  |
| `static final String OPSTR_READ_PHONE_NUMBERS = "android:read_phone_numbers"` |  |
| `static final String OPSTR_READ_PHONE_STATE = "android:read_phone_state"` |  |
| `static final String OPSTR_READ_SMS = "android:read_sms"` |  |
| `static final String OPSTR_RECEIVE_MMS = "android:receive_mms"` |  |
| `static final String OPSTR_RECEIVE_SMS = "android:receive_sms"` |  |
| `static final String OPSTR_RECEIVE_WAP_PUSH = "android:receive_wap_push"` |  |
| `static final String OPSTR_RECORD_AUDIO = "android:record_audio"` |  |
| `static final String OPSTR_SEND_SMS = "android:send_sms"` |  |
| `static final String OPSTR_SYSTEM_ALERT_WINDOW = "android:system_alert_window"` |  |
| `static final String OPSTR_USE_FINGERPRINT = "android:use_fingerprint"` |  |
| `static final String OPSTR_USE_SIP = "android:use_sip"` |  |
| `static final String OPSTR_WRITE_CALENDAR = "android:write_calendar"` |  |
| `static final String OPSTR_WRITE_CALL_LOG = "android:write_call_log"` |  |
| `static final String OPSTR_WRITE_CONTACTS = "android:write_contacts"` |  |
| `static final String OPSTR_WRITE_EXTERNAL_STORAGE = "android:write_external_storage"` |  |
| `static final String OPSTR_WRITE_SETTINGS = "android:write_settings"` |  |
| `static final int WATCH_FOREGROUND_CHANGES = 1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void finishOp(@NonNull String, int, @NonNull String, @Nullable String)` |  |
| `boolean isOpActive(@NonNull String, int, @NonNull String)` |  |
| `int noteOp(@NonNull String, int, @Nullable String, @Nullable String, @Nullable String)` |  |
| `int noteOpNoThrow(@NonNull String, int, @NonNull String, @Nullable String, @Nullable String)` |  |
| `int noteProxyOp(@NonNull String, @Nullable String, int, @Nullable String, @Nullable String)` |  |
| `int noteProxyOpNoThrow(@NonNull String, @Nullable String, int, @Nullable String, @Nullable String)` |  |
| `void setOnOpNotedCallback(@Nullable java.util.concurrent.Executor, @Nullable android.app.AppOpsManager.OnOpNotedCallback)` |  |
| `int startOp(@NonNull String, int, @Nullable String, @Nullable String, @Nullable String)` |  |
| `int startOpNoThrow(@NonNull String, int, @NonNull String, @NonNull String, @Nullable String)` |  |
| `void startWatchingActive(@NonNull String[], @NonNull java.util.concurrent.Executor, @NonNull android.app.AppOpsManager.OnOpActiveChangedListener)` |  |
| `void startWatchingMode(@NonNull String, @Nullable String, @NonNull android.app.AppOpsManager.OnOpChangedListener)` |  |
| `void startWatchingMode(@NonNull String, @Nullable String, int, @NonNull android.app.AppOpsManager.OnOpChangedListener)` |  |
| `void stopWatchingActive(@NonNull android.app.AppOpsManager.OnOpActiveChangedListener)` |  |
| `void stopWatchingMode(@NonNull android.app.AppOpsManager.OnOpChangedListener)` |  |
| `int unsafeCheckOp(@NonNull String, int, @NonNull String)` |  |
| `int unsafeCheckOpNoThrow(@NonNull String, int, @NonNull String)` |  |
| `int unsafeCheckOpRaw(@NonNull String, int, @NonNull String)` |  |
| `int unsafeCheckOpRawNoThrow(@NonNull String, int, @NonNull String)` |  |

---

### `interface static AppOpsManager.OnOpActiveChangedListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onOpActiveChanged(@NonNull String, int, @NonNull String, boolean)` |  |

---

### `interface static AppOpsManager.OnOpChangedListener`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AppOpsManager.OnOpNotedCallback()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onOpChanged(String, String)` |  |
| `abstract void onAsyncNoted(@NonNull android.app.AsyncNotedAppOp)` |  |
| `abstract void onNoted(@NonNull android.app.SyncNotedAppOp)` |  |
| `abstract void onSelfNoted(@NonNull android.app.SyncNotedAppOp)` |  |

---

### `class Application`

- **继承：** `android.content.ContextWrapper`
- **实现：** `android.content.ComponentCallbacks2`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Application()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `static String getProcessName()` |  |
| `void registerActivityLifecycleCallbacks(android.app.Application.ActivityLifecycleCallbacks)` |  |
| `void registerOnProvideAssistDataListener(android.app.Application.OnProvideAssistDataListener)` |  |
| `void unregisterActivityLifecycleCallbacks(android.app.Application.ActivityLifecycleCallbacks)` |  |
| `void unregisterOnProvideAssistDataListener(android.app.Application.OnProvideAssistDataListener)` |  |

---

### `interface static Application.ActivityLifecycleCallbacks`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onActivityCreated(@NonNull android.app.Activity, @Nullable android.os.Bundle)` |  |
| `void onActivityDestroyed(@NonNull android.app.Activity)` |  |
| `void onActivityPaused(@NonNull android.app.Activity)` |  |
| `default void onActivityPostCreated(@NonNull android.app.Activity, @Nullable android.os.Bundle)` |  |
| `default void onActivityPostDestroyed(@NonNull android.app.Activity)` |  |
| `default void onActivityPostPaused(@NonNull android.app.Activity)` |  |
| `default void onActivityPostResumed(@NonNull android.app.Activity)` |  |
| `default void onActivityPostSaveInstanceState(@NonNull android.app.Activity, @NonNull android.os.Bundle)` |  |
| `default void onActivityPostStarted(@NonNull android.app.Activity)` |  |
| `default void onActivityPostStopped(@NonNull android.app.Activity)` |  |
| `default void onActivityPreCreated(@NonNull android.app.Activity, @Nullable android.os.Bundle)` |  |
| `default void onActivityPreDestroyed(@NonNull android.app.Activity)` |  |
| `default void onActivityPrePaused(@NonNull android.app.Activity)` |  |
| `default void onActivityPreResumed(@NonNull android.app.Activity)` |  |
| `default void onActivityPreSaveInstanceState(@NonNull android.app.Activity, @NonNull android.os.Bundle)` |  |
| `default void onActivityPreStarted(@NonNull android.app.Activity)` |  |
| `default void onActivityPreStopped(@NonNull android.app.Activity)` |  |
| `void onActivityResumed(@NonNull android.app.Activity)` |  |
| `void onActivitySaveInstanceState(@NonNull android.app.Activity, @NonNull android.os.Bundle)` |  |
| `void onActivityStarted(@NonNull android.app.Activity)` |  |
| `void onActivityStopped(@NonNull android.app.Activity)` |  |

---

### `interface static Application.OnProvideAssistDataListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onProvideAssistData(android.app.Activity, android.os.Bundle)` |  |

---

### `class ApplicationErrorReport`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ApplicationErrorReport()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int TYPE_ANR = 2` |  |
| `static final int TYPE_BATTERY = 3` |  |
| `static final int TYPE_CRASH = 1` |  |
| `static final int TYPE_NONE = 0` |  |
| `static final int TYPE_RUNNING_SERVICE = 5` |  |
| `android.app.ApplicationErrorReport.AnrInfo anrInfo` |  |
| `android.app.ApplicationErrorReport.BatteryInfo batteryInfo` |  |
| `android.app.ApplicationErrorReport.CrashInfo crashInfo` |  |
| `String installerPackageName` |  |
| `String packageName` |  |
| `String processName` |  |
| `android.app.ApplicationErrorReport.RunningServiceInfo runningServiceInfo` |  |
| `boolean systemApp` |  |
| `long time` |  |
| `int type` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void dump(android.util.Printer, String)` |  |
| `static android.content.ComponentName getErrorReportReceiver(android.content.Context, String, int)` |  |
| `void readFromParcel(android.os.Parcel)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ApplicationErrorReport.AnrInfo`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ApplicationErrorReport.AnrInfo()` |  |
| `ApplicationErrorReport.AnrInfo(android.os.Parcel)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `String activity` |  |
| `String cause` |  |
| `String info` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void dump(android.util.Printer, String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ApplicationErrorReport.BatteryInfo`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ApplicationErrorReport.BatteryInfo()` |  |
| `ApplicationErrorReport.BatteryInfo(android.os.Parcel)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `String checkinDetails` |  |
| `long durationMicros` |  |
| `String usageDetails` |  |
| `int usagePercent` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void dump(android.util.Printer, String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ApplicationErrorReport.CrashInfo`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ApplicationErrorReport.CrashInfo()` |  |
| `ApplicationErrorReport.CrashInfo(Throwable)` |  |
| `ApplicationErrorReport.CrashInfo(android.os.Parcel)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `String exceptionClassName` |  |
| `String exceptionMessage` |  |
| `String stackTrace` |  |
| `String throwClassName` |  |
| `String throwFileName` |  |
| `int throwLineNumber` |  |
| `String throwMethodName` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void dump(android.util.Printer, String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ApplicationErrorReport.RunningServiceInfo`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ApplicationErrorReport.RunningServiceInfo()` |  |
| `ApplicationErrorReport.RunningServiceInfo(android.os.Parcel)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `long durationMillis` |  |
| `String serviceDetails` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void dump(android.util.Printer, String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final ApplicationExitInfo`

- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int REASON_ANR = 6` |  |
| `static final int REASON_CRASH = 4` |  |
| `static final int REASON_CRASH_NATIVE = 5` |  |
| `static final int REASON_DEPENDENCY_DIED = 12` |  |
| `static final int REASON_EXCESSIVE_RESOURCE_USAGE = 9` |  |
| `static final int REASON_EXIT_SELF = 1` |  |
| `static final int REASON_INITIALIZATION_FAILURE = 7` |  |
| `static final int REASON_LOW_MEMORY = 3` |  |
| `static final int REASON_OTHER = 13` |  |
| `static final int REASON_PERMISSION_CHANGE = 8` |  |
| `static final int REASON_SIGNALED = 2` |  |
| `static final int REASON_UNKNOWN = 0` |  |
| `static final int REASON_USER_REQUESTED = 10` |  |
| `static final int REASON_USER_STOPPED = 11` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getDefiningUid()` |  |
| `int getImportance()` |  |
| `int getPackageUid()` |  |
| `int getPid()` |  |
| `long getPss()` |  |
| `int getRealUid()` |  |
| `int getReason()` |  |
| `long getRss()` |  |
| `int getStatus()` |  |
| `long getTimestamp()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class final AsyncNotedAppOp`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getTime()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class final AuthenticationRequiredException`

- **继承：** `java.lang.SecurityException`
- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AuthenticationRequiredException(Throwable, android.app.PendingIntent)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.app.PendingIntent getUserAction()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final AutomaticZenRule`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AutomaticZenRule(@NonNull String, @Nullable android.content.ComponentName, @Nullable android.content.ComponentName, @NonNull android.net.Uri, @Nullable android.service.notification.ZenPolicy, int, boolean)` |  |
| `AutomaticZenRule(android.os.Parcel)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.net.Uri getConditionId()` |  |
| `long getCreationTime()` |  |
| `int getInterruptionFilter()` |  |
| `String getName()` |  |
| `android.content.ComponentName getOwner()` |  |
| `android.service.notification.ZenPolicy getZenPolicy()` |  |
| `boolean isEnabled()` |  |
| `void setConditionId(android.net.Uri)` |  |
| `void setConfigurationActivity(@Nullable android.content.ComponentName)` |  |
| `void setEnabled(boolean)` |  |
| `void setInterruptionFilter(int)` |  |
| `void setName(String)` |  |
| `void setZenPolicy(android.service.notification.ZenPolicy)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class DatePickerDialog`

- **继承：** `android.app.AlertDialog`
- **实现：** `android.widget.DatePicker.OnDateChangedListener android.content.DialogInterface.OnClickListener`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `DatePickerDialog(@NonNull android.content.Context)` |  |
| `DatePickerDialog(@NonNull android.content.Context, @StyleRes int)` |  |
| `DatePickerDialog(@NonNull android.content.Context, @Nullable android.app.DatePickerDialog.OnDateSetListener, int, int, int)` |  |
| `DatePickerDialog(@NonNull android.content.Context, @StyleRes int, @Nullable android.app.DatePickerDialog.OnDateSetListener, int, int, int)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onClick(@NonNull android.content.DialogInterface, int)` |  |
| `void onDateChanged(@NonNull android.widget.DatePicker, int, int, int)` |  |
| `void setOnDateSetListener(@Nullable android.app.DatePickerDialog.OnDateSetListener)` |  |
| `void updateDate(int, int, int)` |  |

---

### `interface static DatePickerDialog.OnDateSetListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onDateSet(android.widget.DatePicker, int, int, int)` |  |

---

### `class Dialog`

- **实现：** `android.content.DialogInterface android.view.KeyEvent.Callback android.view.View.OnCreateContextMenuListener android.view.Window.Callback`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Dialog(@NonNull android.content.Context)` |  |
| `Dialog(@NonNull android.content.Context, @StyleRes int)` |  |
| `Dialog(@NonNull android.content.Context, boolean, @Nullable android.content.DialogInterface.OnCancelListener)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void addContentView(@NonNull android.view.View, @Nullable android.view.ViewGroup.LayoutParams)` |  |
| `void cancel()` |  |
| `void closeOptionsMenu()` |  |
| `void create()` |  |
| `void dismiss()` |  |
| `boolean dispatchGenericMotionEvent(@NonNull android.view.MotionEvent)` |  |
| `boolean dispatchKeyEvent(@NonNull android.view.KeyEvent)` |  |
| `boolean dispatchKeyShortcutEvent(@NonNull android.view.KeyEvent)` |  |
| `boolean dispatchPopulateAccessibilityEvent(@NonNull android.view.accessibility.AccessibilityEvent)` |  |
| `boolean dispatchTouchEvent(@NonNull android.view.MotionEvent)` |  |
| `boolean dispatchTrackballEvent(@NonNull android.view.MotionEvent)` |  |
| `<T extends android.view.View> T findViewById(@IdRes int)` |  |
| `final int getVolumeControlStream()` |  |
| `void hide()` |  |
| `void invalidateOptionsMenu()` |  |
| `boolean isShowing()` |  |
| `void onAttachedToWindow()` |  |
| `void onBackPressed()` |  |
| `void onContentChanged()` |  |
| `boolean onContextItemSelected(@NonNull android.view.MenuItem)` |  |
| `void onContextMenuClosed(@NonNull android.view.Menu)` |  |
| `void onCreate(android.os.Bundle)` |  |
| `void onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)` |  |
| `boolean onCreateOptionsMenu(@NonNull android.view.Menu)` |  |
| `boolean onCreatePanelMenu(int, @NonNull android.view.Menu)` |  |
| `android.view.View onCreatePanelView(int)` |  |
| `void onDetachedFromWindow()` |  |
| `boolean onGenericMotionEvent(@NonNull android.view.MotionEvent)` |  |
| `boolean onKeyDown(int, @NonNull android.view.KeyEvent)` |  |
| `boolean onKeyLongPress(int, @NonNull android.view.KeyEvent)` |  |
| `boolean onKeyMultiple(int, int, @NonNull android.view.KeyEvent)` |  |
| `boolean onKeyShortcut(int, @NonNull android.view.KeyEvent)` |  |
| `boolean onKeyUp(int, @NonNull android.view.KeyEvent)` |  |
| `boolean onMenuItemSelected(int, @NonNull android.view.MenuItem)` |  |
| `boolean onMenuOpened(int, @NonNull android.view.Menu)` |  |
| `boolean onOptionsItemSelected(@NonNull android.view.MenuItem)` |  |
| `void onOptionsMenuClosed(@NonNull android.view.Menu)` |  |
| `void onPanelClosed(int, @NonNull android.view.Menu)` |  |
| `boolean onPrepareOptionsMenu(@NonNull android.view.Menu)` |  |
| `boolean onPreparePanel(int, @Nullable android.view.View, @NonNull android.view.Menu)` |  |
| `void onRestoreInstanceState(@NonNull android.os.Bundle)` |  |
| `boolean onSearchRequested(@NonNull android.view.SearchEvent)` |  |
| `boolean onSearchRequested()` |  |
| `void onStart()` |  |
| `void onStop()` |  |
| `boolean onTouchEvent(@NonNull android.view.MotionEvent)` |  |
| `boolean onTrackballEvent(@NonNull android.view.MotionEvent)` |  |
| `void onWindowAttributesChanged(android.view.WindowManager.LayoutParams)` |  |
| `void onWindowFocusChanged(boolean)` |  |
| `android.view.ActionMode onWindowStartingActionMode(android.view.ActionMode.Callback)` |  |
| `android.view.ActionMode onWindowStartingActionMode(android.view.ActionMode.Callback, int)` |  |
| `void openContextMenu(@NonNull android.view.View)` |  |
| `void openOptionsMenu()` |  |
| `void registerForContextMenu(@NonNull android.view.View)` |  |
| `final boolean requestWindowFeature(int)` |  |
| `void setCancelMessage(@Nullable android.os.Message)` |  |
| `void setCancelable(boolean)` |  |
| `void setCanceledOnTouchOutside(boolean)` |  |
| `void setContentView(@LayoutRes int)` |  |
| `void setContentView(@NonNull android.view.View)` |  |
| `void setContentView(@NonNull android.view.View, @Nullable android.view.ViewGroup.LayoutParams)` |  |
| `void setDismissMessage(@Nullable android.os.Message)` |  |
| `final void setFeatureDrawable(int, @Nullable android.graphics.drawable.Drawable)` |  |
| `final void setFeatureDrawableAlpha(int, int)` |  |
| `final void setFeatureDrawableResource(int, @DrawableRes int)` |  |
| `final void setFeatureDrawableUri(int, @Nullable android.net.Uri)` |  |
| `void setOnCancelListener(@Nullable android.content.DialogInterface.OnCancelListener)` |  |
| `void setOnDismissListener(@Nullable android.content.DialogInterface.OnDismissListener)` |  |
| `void setOnKeyListener(@Nullable android.content.DialogInterface.OnKeyListener)` |  |
| `void setOnShowListener(@Nullable android.content.DialogInterface.OnShowListener)` |  |
| `final void setOwnerActivity(@NonNull android.app.Activity)` |  |
| `void setTitle(@Nullable CharSequence)` |  |
| `void setTitle(@StringRes int)` |  |
| `final void setVolumeControlStream(int)` |  |
| `void show()` |  |
| `void takeKeyEvents(boolean)` |  |
| `void unregisterForContextMenu(@NonNull android.view.View)` |  |

---

### `class DialogFragment` ~~DEPRECATED~~

- **继承：** `android.app.Fragment`
- **实现：** `android.content.DialogInterface.OnCancelListener android.content.DialogInterface.OnDismissListener`
- **Annotations:** `@Deprecated`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.view.LayoutInflater onGetLayoutInflater(android.os.Bundle)` |  |

---

### `class final DirectAction`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final DirectAction.Builder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `DirectAction.Builder(@NonNull String)` |  |

---

### `class DownloadManager`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ACTION_DOWNLOAD_COMPLETE = "android.intent.action.DOWNLOAD_COMPLETE"` |  |
| `static final String ACTION_NOTIFICATION_CLICKED = "android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED"` |  |
| `static final String ACTION_VIEW_DOWNLOADS = "android.intent.action.VIEW_DOWNLOADS"` |  |
| `static final String COLUMN_BYTES_DOWNLOADED_SO_FAR = "bytes_so_far"` |  |
| `static final String COLUMN_DESCRIPTION = "description"` |  |
| `static final String COLUMN_ID = "_id"` |  |
| `static final String COLUMN_LAST_MODIFIED_TIMESTAMP = "last_modified_timestamp"` |  |
| `static final String COLUMN_LOCAL_URI = "local_uri"` |  |
| `static final String COLUMN_MEDIAPROVIDER_URI = "mediaprovider_uri"` |  |
| `static final String COLUMN_MEDIA_TYPE = "media_type"` |  |
| `static final String COLUMN_REASON = "reason"` |  |
| `static final String COLUMN_STATUS = "status"` |  |
| `static final String COLUMN_TITLE = "title"` |  |
| `static final String COLUMN_TOTAL_SIZE_BYTES = "total_size"` |  |
| `static final String COLUMN_URI = "uri"` |  |
| `static final int ERROR_CANNOT_RESUME = 1008` |  |
| `static final int ERROR_DEVICE_NOT_FOUND = 1007` |  |
| `static final int ERROR_FILE_ALREADY_EXISTS = 1009` |  |
| `static final int ERROR_FILE_ERROR = 1001` |  |
| `static final int ERROR_HTTP_DATA_ERROR = 1004` |  |
| `static final int ERROR_INSUFFICIENT_SPACE = 1006` |  |
| `static final int ERROR_TOO_MANY_REDIRECTS = 1005` |  |
| `static final int ERROR_UNHANDLED_HTTP_CODE = 1002` |  |
| `static final int ERROR_UNKNOWN = 1000` |  |
| `static final String EXTRA_DOWNLOAD_ID = "extra_download_id"` |  |
| `static final String EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS = "extra_click_download_ids"` |  |
| `static final String INTENT_EXTRAS_SORT_BY_SIZE = "android.app.DownloadManager.extra_sortBySize"` |  |
| `static final int PAUSED_QUEUED_FOR_WIFI = 3` |  |
| `static final int PAUSED_UNKNOWN = 4` |  |
| `static final int PAUSED_WAITING_FOR_NETWORK = 2` |  |
| `static final int PAUSED_WAITING_TO_RETRY = 1` |  |
| `static final int STATUS_FAILED = 16` |  |
| `static final int STATUS_PAUSED = 4` |  |
| `static final int STATUS_PENDING = 1` |  |
| `static final int STATUS_RUNNING = 2` |  |
| `static final int STATUS_SUCCESSFUL = 8` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `long enqueue(android.app.DownloadManager.Request)` |  |
| `static Long getMaxBytesOverMobile(android.content.Context)` |  |
| `String getMimeTypeForDownloadedFile(long)` |  |
| `static Long getRecommendedMaxBytesOverMobile(android.content.Context)` |  |
| `android.net.Uri getUriForDownloadedFile(long)` |  |
| `android.os.ParcelFileDescriptor openDownloadedFile(long) throws java.io.FileNotFoundException` |  |
| `android.database.Cursor query(android.app.DownloadManager.Query)` |  |
| `int remove(long...)` |  |

---

### `class static DownloadManager.Query`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `DownloadManager.Query()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.DownloadManager.Query setFilterById(long...)` |  |
| `android.app.DownloadManager.Query setFilterByStatus(int)` |  |

---

### `class static DownloadManager.Request`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `DownloadManager.Request(android.net.Uri)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int NETWORK_MOBILE = 1` |  |
| `static final int NETWORK_WIFI = 2` |  |
| `static final int VISIBILITY_HIDDEN = 2` |  |
| `static final int VISIBILITY_VISIBLE = 0` |  |
| `static final int VISIBILITY_VISIBLE_NOTIFY_COMPLETED = 1` |  |
| `static final int VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION = 3` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.DownloadManager.Request addRequestHeader(String, String)` |  |
| `android.app.DownloadManager.Request setAllowedNetworkTypes(int)` |  |
| `android.app.DownloadManager.Request setAllowedOverMetered(boolean)` |  |
| `android.app.DownloadManager.Request setAllowedOverRoaming(boolean)` |  |
| `android.app.DownloadManager.Request setDescription(CharSequence)` |  |
| `android.app.DownloadManager.Request setDestinationInExternalFilesDir(android.content.Context, String, String)` |  |
| `android.app.DownloadManager.Request setDestinationInExternalPublicDir(String, String)` |  |
| `android.app.DownloadManager.Request setDestinationUri(android.net.Uri)` |  |
| `android.app.DownloadManager.Request setMimeType(String)` |  |
| `android.app.DownloadManager.Request setNotificationVisibility(int)` |  |
| `android.app.DownloadManager.Request setRequiresCharging(boolean)` |  |
| `android.app.DownloadManager.Request setRequiresDeviceIdle(boolean)` |  |
| `android.app.DownloadManager.Request setTitle(CharSequence)` |  |

---

### `class ExpandableListActivity` ~~DEPRECATED~~

- **继承：** `android.app.Activity`
- **实现：** `android.widget.ExpandableListView.OnChildClickListener android.widget.ExpandableListView.OnGroupCollapseListener android.widget.ExpandableListView.OnGroupExpandListener android.view.View.OnCreateContextMenuListener`
- **Annotations:** `@Deprecated`

---

### `class Fragment` ~~DEPRECATED~~

- **实现：** `android.content.ComponentCallbacks2 android.view.View.OnCreateContextMenuListener`
- **Annotations:** `@Deprecated`

---

### `class static Fragment.InstantiationException` ~~DEPRECATED~~

- **继承：** `android.util.AndroidRuntimeException`
- **Annotations:** `@Deprecated`

---

### `class static Fragment.SavedState` ~~DEPRECATED~~

- **实现：** `android.os.Parcelable`
- **Annotations:** `@Deprecated`

---

### `class FragmentBreadCrumbs` ~~DEPRECATED~~

- **继承：** `android.view.ViewGroup`
- **实现：** `android.app.FragmentManager.OnBackStackChangedListener`
- **Annotations:** `@Deprecated`

---

### `interface static FragmentBreadCrumbs.OnBreadCrumbClickListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class abstract FragmentContainer` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class FragmentController` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class abstract FragmentHostCallback<E>` ~~DEPRECATED~~

- **继承：** `android.app.FragmentContainer`
- **Annotations:** `@Deprecated`

---

### `class abstract FragmentManager` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static FragmentManager.BackStackEntry` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static FragmentManager.OnBackStackChangedListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class FragmentManagerNonConfig` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class abstract FragmentTransaction` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class Instrumentation`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Instrumentation()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String REPORT_KEY_IDENTIFIER = "id"` |  |
| `static final String REPORT_KEY_STREAMRESULT = "stream"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.os.TestLooperManager acquireLooperManager(android.os.Looper)` |  |
| `void addMonitor(android.app.Instrumentation.ActivityMonitor)` |  |
| `android.app.Instrumentation.ActivityMonitor addMonitor(android.content.IntentFilter, android.app.Instrumentation.ActivityResult, boolean)` |  |
| `android.app.Instrumentation.ActivityMonitor addMonitor(String, android.app.Instrumentation.ActivityResult, boolean)` |  |
| `void addResults(android.os.Bundle)` |  |
| `void callActivityOnCreate(android.app.Activity, android.os.Bundle)` |  |
| `void callActivityOnCreate(android.app.Activity, android.os.Bundle, android.os.PersistableBundle)` |  |
| `void callActivityOnDestroy(android.app.Activity)` |  |
| `void callActivityOnNewIntent(android.app.Activity, android.content.Intent)` |  |
| `void callActivityOnPause(android.app.Activity)` |  |
| `void callActivityOnPictureInPictureRequested(@NonNull android.app.Activity)` |  |
| `void callActivityOnPostCreate(@NonNull android.app.Activity, @Nullable android.os.Bundle)` |  |
| `void callActivityOnPostCreate(@NonNull android.app.Activity, @Nullable android.os.Bundle, @Nullable android.os.PersistableBundle)` |  |
| `void callActivityOnRestart(android.app.Activity)` |  |
| `void callActivityOnRestoreInstanceState(@NonNull android.app.Activity, @NonNull android.os.Bundle)` |  |
| `void callActivityOnRestoreInstanceState(@NonNull android.app.Activity, @Nullable android.os.Bundle, @Nullable android.os.PersistableBundle)` |  |
| `void callActivityOnResume(android.app.Activity)` |  |
| `void callActivityOnSaveInstanceState(@NonNull android.app.Activity, @NonNull android.os.Bundle)` |  |
| `void callActivityOnSaveInstanceState(@NonNull android.app.Activity, @NonNull android.os.Bundle, @NonNull android.os.PersistableBundle)` |  |
| `void callActivityOnStart(android.app.Activity)` |  |
| `void callActivityOnStop(android.app.Activity)` |  |
| `void callActivityOnUserLeaving(android.app.Activity)` |  |
| `void callApplicationOnCreate(android.app.Application)` |  |
| `boolean checkMonitorHit(android.app.Instrumentation.ActivityMonitor, int)` |  |
| `void endPerformanceSnapshot()` |  |
| `void finish(int, android.os.Bundle)` |  |
| `android.os.Bundle getAllocCounts()` |  |
| `android.os.Bundle getBinderCounts()` |  |
| `android.content.ComponentName getComponentName()` |  |
| `android.content.Context getContext()` |  |
| `String getProcessName()` |  |
| `android.content.Context getTargetContext()` |  |
| `android.app.UiAutomation getUiAutomation()` |  |
| `android.app.UiAutomation getUiAutomation(int)` |  |
| `boolean invokeContextMenuAction(android.app.Activity, int, int)` |  |
| `boolean invokeMenuActionSync(android.app.Activity, int, int)` |  |
| `boolean isProfiling()` |  |
| `android.app.Activity newActivity(Class<?>, android.content.Context, android.os.IBinder, android.app.Application, android.content.Intent, android.content.pm.ActivityInfo, CharSequence, android.app.Activity, String, Object) throws java.lang.IllegalAccessException, java.lang.InstantiationException` |  |
| `android.app.Activity newActivity(ClassLoader, String, android.content.Intent) throws java.lang.ClassNotFoundException, java.lang.IllegalAccessException, java.lang.InstantiationException` |  |
| `android.app.Application newApplication(ClassLoader, String, android.content.Context) throws java.lang.ClassNotFoundException, java.lang.IllegalAccessException, java.lang.InstantiationException` |  |
| `static android.app.Application newApplication(Class<?>, android.content.Context) throws java.lang.ClassNotFoundException, java.lang.IllegalAccessException, java.lang.InstantiationException` |  |
| `void onCreate(android.os.Bundle)` |  |
| `void onDestroy()` |  |
| `boolean onException(Object, Throwable)` |  |
| `void onStart()` |  |
| `void removeMonitor(android.app.Instrumentation.ActivityMonitor)` |  |
| `void runOnMainSync(Runnable)` |  |
| `void sendCharacterSync(int)` |  |
| `void sendKeyDownUpSync(int)` |  |
| `void sendKeySync(android.view.KeyEvent)` |  |
| `void sendPointerSync(android.view.MotionEvent)` |  |
| `void sendStatus(int, android.os.Bundle)` |  |
| `void sendStringSync(String)` |  |
| `void sendTrackballEventSync(android.view.MotionEvent)` |  |
| `void setAutomaticPerformanceSnapshots()` |  |
| `void setInTouchMode(boolean)` |  |
| `void start()` |  |
| `android.app.Activity startActivitySync(android.content.Intent)` |  |
| `void startPerformanceSnapshot()` |  |
| `void startProfiling()` |  |
| `void stopProfiling()` |  |
| `void waitForIdle(Runnable)` |  |
| `void waitForIdleSync()` |  |
| `android.app.Activity waitForMonitor(android.app.Instrumentation.ActivityMonitor)` |  |
| `android.app.Activity waitForMonitorWithTimeout(android.app.Instrumentation.ActivityMonitor, long)` |  |

---

### `class static Instrumentation.ActivityMonitor`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Instrumentation.ActivityMonitor(android.content.IntentFilter, android.app.Instrumentation.ActivityResult, boolean)` |  |
| `Instrumentation.ActivityMonitor(String, android.app.Instrumentation.ActivityResult, boolean)` |  |
| `Instrumentation.ActivityMonitor()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `final android.content.IntentFilter getFilter()` |  |
| `final int getHits()` |  |
| `final android.app.Activity getLastActivity()` |  |
| `final android.app.Instrumentation.ActivityResult getResult()` |  |
| `final boolean isBlocking()` |  |
| `android.app.Instrumentation.ActivityResult onStartActivity(android.content.Intent)` |  |
| `final android.app.Activity waitForActivity()` |  |
| `final android.app.Activity waitForActivityWithTimeout(long)` |  |

---

### `class static final Instrumentation.ActivityResult`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Instrumentation.ActivityResult(int, android.content.Intent)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int getResultCode()` |  |
| `android.content.Intent getResultData()` |  |

---

### `class abstract IntentService` ~~DEPRECATED~~

- **继承：** `android.app.Service`
- **Annotations:** `@Deprecated`

---

### `class KeyguardManager`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `KeyguardManager.KeyguardDismissCallback()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean isDeviceLocked()` |  |
| `boolean isDeviceSecure()` |  |
| `boolean isKeyguardLocked()` |  |
| `boolean isKeyguardSecure()` |  |
| `void requestDismissKeyguard(@NonNull android.app.Activity, @Nullable android.app.KeyguardManager.KeyguardDismissCallback)` |  |
| `void onDismissCancelled()` |  |
| `void onDismissError()` |  |
| `void onDismissSucceeded()` |  |

---

### `class KeyguardManager.KeyguardLock` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static KeyguardManager.OnKeyguardExitResult` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class abstract LauncherActivity` ~~DEPRECATED~~

- **继承：** `android.app.ListActivity`
- **Annotations:** `@Deprecated`

---

### `class LauncherActivity.IconResizer` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class static LauncherActivity.ListItem` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class ListActivity` ~~DEPRECATED~~

- **继承：** `android.app.Activity`
- **Annotations:** `@Deprecated`

---

### `class ListFragment` ~~DEPRECATED~~

- **继承：** `android.app.Fragment`
- **Annotations:** `@Deprecated`

---

### `class abstract LoaderManager` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static LoaderManager.LoaderCallbacks<D>` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class LocalActivityManager` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class MediaRouteActionProvider`

- **继承：** `android.view.ActionProvider`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `MediaRouteActionProvider(android.content.Context)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.view.View onCreateActionView()` |  |
| `void setExtendedSettingsClickListener(android.view.View.OnClickListener)` |  |
| `void setRouteTypes(int)` |  |

---

### `class MediaRouteButton`

- **继承：** `android.view.View`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `MediaRouteButton(android.content.Context)` |  |
| `MediaRouteButton(android.content.Context, android.util.AttributeSet)` |  |
| `MediaRouteButton(android.content.Context, android.util.AttributeSet, int)` |  |
| `MediaRouteButton(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int getRouteTypes()` |  |
| `void onAttachedToWindow()` |  |
| `void onDetachedFromWindow()` |  |
| `void setExtendedSettingsClickListener(android.view.View.OnClickListener)` |  |
| `void setRouteTypes(int)` |  |
| `void showDialog()` |  |

---

### `class NativeActivity`

- **继承：** `android.app.Activity`
- **实现：** `android.view.InputQueue.Callback android.view.SurfaceHolder.Callback2 android.view.ViewTreeObserver.OnGlobalLayoutListener`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `NativeActivity()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String META_DATA_FUNC_NAME = "android.app.func_name"` |  |
| `static final String META_DATA_LIB_NAME = "android.app.lib_name"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onGlobalLayout()` |  |
| `void onInputQueueCreated(android.view.InputQueue)` |  |
| `void onInputQueueDestroyed(android.view.InputQueue)` |  |
| `void surfaceChanged(android.view.SurfaceHolder, int, int, int)` |  |
| `void surfaceCreated(android.view.SurfaceHolder)` |  |
| `void surfaceDestroyed(android.view.SurfaceHolder)` |  |
| `void surfaceRedrawNeeded(android.view.SurfaceHolder)` |  |

---

### `class Notification`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification()` |  |
| `Notification(android.os.Parcel)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final android.media.AudioAttributes AUDIO_ATTRIBUTES_DEFAULT` |  |
| `static final int BADGE_ICON_LARGE = 2` |  |
| `static final int BADGE_ICON_NONE = 0` |  |
| `static final int BADGE_ICON_SMALL = 1` |  |
| `static final String CATEGORY_ALARM = "alarm"` |  |
| `static final String CATEGORY_CALL = "call"` |  |
| `static final String CATEGORY_EMAIL = "email"` |  |
| `static final String CATEGORY_ERROR = "err"` |  |
| `static final String CATEGORY_EVENT = "event"` |  |
| `static final String CATEGORY_MESSAGE = "msg"` |  |
| `static final String CATEGORY_NAVIGATION = "navigation"` |  |
| `static final String CATEGORY_PROGRESS = "progress"` |  |
| `static final String CATEGORY_PROMO = "promo"` |  |
| `static final String CATEGORY_RECOMMENDATION = "recommendation"` |  |
| `static final String CATEGORY_REMINDER = "reminder"` |  |
| `static final String CATEGORY_SERVICE = "service"` |  |
| `static final String CATEGORY_SOCIAL = "social"` |  |
| `static final String CATEGORY_STATUS = "status"` |  |
| `static final String CATEGORY_SYSTEM = "sys"` |  |
| `static final String CATEGORY_TRANSPORT = "transport"` |  |
| `static final int DEFAULT_ALL = -1` |  |
| `static final int DEFAULT_LIGHTS = 4` |  |
| `static final int DEFAULT_SOUND = 1` |  |
| `static final int DEFAULT_VIBRATE = 2` |  |
| `static final String EXTRA_AUDIO_CONTENTS_URI = "android.audioContents"` |  |
| `static final String EXTRA_BACKGROUND_IMAGE_URI = "android.backgroundImageUri"` |  |
| `static final String EXTRA_BIG_TEXT = "android.bigText"` |  |
| `static final String EXTRA_CHANNEL_GROUP_ID = "android.intent.extra.CHANNEL_GROUP_ID"` |  |
| `static final String EXTRA_CHANNEL_ID = "android.intent.extra.CHANNEL_ID"` |  |
| `static final String EXTRA_CHRONOMETER_COUNT_DOWN = "android.chronometerCountDown"` |  |
| `static final String EXTRA_COLORIZED = "android.colorized"` |  |
| `static final String EXTRA_COMPACT_ACTIONS = "android.compactActions"` |  |
| `static final String EXTRA_CONVERSATION_TITLE = "android.conversationTitle"` |  |
| `static final String EXTRA_HISTORIC_MESSAGES = "android.messages.historic"` |  |
| `static final String EXTRA_INFO_TEXT = "android.infoText"` |  |
| `static final String EXTRA_IS_GROUP_CONVERSATION = "android.isGroupConversation"` |  |
| `static final String EXTRA_LARGE_ICON_BIG = "android.largeIcon.big"` |  |
| `static final String EXTRA_MEDIA_SESSION = "android.mediaSession"` |  |
| `static final String EXTRA_MESSAGES = "android.messages"` |  |
| `static final String EXTRA_MESSAGING_PERSON = "android.messagingUser"` |  |
| `static final String EXTRA_NOTIFICATION_ID = "android.intent.extra.NOTIFICATION_ID"` |  |
| `static final String EXTRA_NOTIFICATION_TAG = "android.intent.extra.NOTIFICATION_TAG"` |  |
| `static final String EXTRA_PEOPLE_LIST = "android.people.list"` |  |
| `static final String EXTRA_PICTURE = "android.picture"` |  |
| `static final String EXTRA_PROGRESS = "android.progress"` |  |
| `static final String EXTRA_PROGRESS_INDETERMINATE = "android.progressIndeterminate"` |  |
| `static final String EXTRA_PROGRESS_MAX = "android.progressMax"` |  |
| `static final String EXTRA_REMOTE_INPUT_DRAFT = "android.remoteInputDraft"` |  |
| `static final String EXTRA_REMOTE_INPUT_HISTORY = "android.remoteInputHistory"` |  |
| `static final String EXTRA_SHOW_CHRONOMETER = "android.showChronometer"` |  |
| `static final String EXTRA_SHOW_WHEN = "android.showWhen"` |  |
| `static final String EXTRA_SUB_TEXT = "android.subText"` |  |
| `static final String EXTRA_SUMMARY_TEXT = "android.summaryText"` |  |
| `static final String EXTRA_TEMPLATE = "android.template"` |  |
| `static final String EXTRA_TEXT = "android.text"` |  |
| `static final String EXTRA_TEXT_LINES = "android.textLines"` |  |
| `static final String EXTRA_TITLE = "android.title"` |  |
| `static final String EXTRA_TITLE_BIG = "android.title.big"` |  |
| `static final int FLAG_AUTO_CANCEL = 16` |  |
| `static final int FLAG_BUBBLE = 4096` |  |
| `static final int FLAG_FOREGROUND_SERVICE = 64` |  |
| `static final int FLAG_GROUP_SUMMARY = 512` |  |
| `static final int FLAG_INSISTENT = 4` |  |
| `static final int FLAG_LOCAL_ONLY = 256` |  |
| `static final int FLAG_NO_CLEAR = 32` |  |
| `static final int FLAG_ONGOING_EVENT = 2` |  |
| `static final int FLAG_ONLY_ALERT_ONCE = 8` |  |
| `static final int GROUP_ALERT_ALL = 0` |  |
| `static final int GROUP_ALERT_CHILDREN = 2` |  |
| `static final int GROUP_ALERT_SUMMARY = 1` |  |
| `static final String INTENT_CATEGORY_NOTIFICATION_PREFERENCES = "android.intent.category.NOTIFICATION_PREFERENCES"` |  |
| `static final int VISIBILITY_PRIVATE = 0` |  |
| `static final int VISIBILITY_PUBLIC = 1` |  |
| `static final int VISIBILITY_SECRET = -1` |  |
| `android.app.Notification.Action[] actions` |  |
| `String category` |  |
| `android.app.PendingIntent contentIntent` |  |
| `android.app.PendingIntent deleteIntent` |  |
| `android.os.Bundle extras` |  |
| `int flags` |  |
| `android.app.PendingIntent fullScreenIntent` |  |
| `int iconLevel` |  |
| `int number` |  |
| `android.app.Notification publicVersion` |  |
| `CharSequence tickerText` |  |
| `int visibility` |  |
| `long when` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.Notification clone()` |  |
| `int describeContents()` |  |
| `boolean getAllowSystemGeneratedContextualActions()` |  |
| `int getBadgeIconType()` |  |
| `String getChannelId()` |  |
| `String getGroup()` |  |
| `int getGroupAlertBehavior()` |  |
| `android.graphics.drawable.Icon getLargeIcon()` |  |
| `CharSequence getSettingsText()` |  |
| `String getShortcutId()` |  |
| `android.graphics.drawable.Icon getSmallIcon()` |  |
| `String getSortKey()` |  |
| `long getTimeoutAfter()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static Notification.Action`

- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int SEMANTIC_ACTION_ARCHIVE = 5` |  |
| `static final int SEMANTIC_ACTION_CALL = 10` |  |
| `static final int SEMANTIC_ACTION_DELETE = 4` |  |
| `static final int SEMANTIC_ACTION_MARK_AS_READ = 2` |  |
| `static final int SEMANTIC_ACTION_MARK_AS_UNREAD = 3` |  |
| `static final int SEMANTIC_ACTION_MUTE = 6` |  |
| `static final int SEMANTIC_ACTION_NONE = 0` |  |
| `static final int SEMANTIC_ACTION_REPLY = 1` |  |
| `static final int SEMANTIC_ACTION_THUMBS_DOWN = 9` |  |
| `static final int SEMANTIC_ACTION_THUMBS_UP = 8` |  |
| `static final int SEMANTIC_ACTION_UNMUTE = 7` |  |
| `android.app.PendingIntent actionIntent` |  |
| `CharSequence title` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.Notification.Action clone()` |  |
| `int describeContents()` |  |
| `boolean getAllowGeneratedReplies()` |  |
| `android.app.RemoteInput[] getDataOnlyRemoteInputs()` |  |
| `android.os.Bundle getExtras()` |  |
| `android.graphics.drawable.Icon getIcon()` |  |
| `android.app.RemoteInput[] getRemoteInputs()` |  |
| `int getSemanticAction()` |  |
| `boolean isContextual()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final Notification.Action.Builder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification.Action.Builder(android.graphics.drawable.Icon, CharSequence, android.app.PendingIntent)` |  |
| `Notification.Action.Builder(android.app.Notification.Action)` |  |

---

### `interface static Notification.Action.Extender`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.Notification.Action.Builder extend(android.app.Notification.Action.Builder)` |  |

---

### `class static final Notification.Action.WearableExtender`

- **实现：** `android.app.Notification.Action.Extender`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification.Action.WearableExtender()` |  |
| `Notification.Action.WearableExtender(android.app.Notification.Action)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.Notification.Action.WearableExtender clone()` |  |
| `android.app.Notification.Action.Builder extend(android.app.Notification.Action.Builder)` |  |
| `boolean getHintDisplayActionInline()` |  |
| `boolean getHintLaunchesActivity()` |  |
| `boolean isAvailableOffline()` |  |
| `android.app.Notification.Action.WearableExtender setAvailableOffline(boolean)` |  |
| `android.app.Notification.Action.WearableExtender setHintDisplayActionInline(boolean)` |  |
| `android.app.Notification.Action.WearableExtender setHintLaunchesActivity(boolean)` |  |

---

### `class static Notification.BigPictureStyle`

- **继承：** `android.app.Notification.Style`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification.BigPictureStyle()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.Notification.BigPictureStyle bigLargeIcon(android.graphics.Bitmap)` |  |
| `android.app.Notification.BigPictureStyle bigLargeIcon(android.graphics.drawable.Icon)` |  |
| `android.app.Notification.BigPictureStyle bigPicture(android.graphics.Bitmap)` |  |
| `android.app.Notification.BigPictureStyle setBigContentTitle(CharSequence)` |  |
| `android.app.Notification.BigPictureStyle setSummaryText(CharSequence)` |  |

---

### `class static Notification.BigTextStyle`

- **继承：** `android.app.Notification.Style`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification.BigTextStyle()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.Notification.BigTextStyle bigText(CharSequence)` |  |
| `android.app.Notification.BigTextStyle setBigContentTitle(CharSequence)` |  |
| `android.app.Notification.BigTextStyle setSummaryText(CharSequence)` |  |

---

### `class static final Notification.BubbleMetadata`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean getAutoExpandBubble()` |  |
| `boolean isNotificationSuppressed()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final Notification.BubbleMetadata.Builder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification.BubbleMetadata.Builder(@NonNull String)` |  |
| `Notification.BubbleMetadata.Builder(@NonNull android.app.PendingIntent, @NonNull android.graphics.drawable.Icon)` |  |

---

### `class static Notification.Builder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification.Builder(android.content.Context, String)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.widget.RemoteViews createBigContentView()` |  |
| `android.widget.RemoteViews createContentView()` |  |
| `android.widget.RemoteViews createHeadsUpContentView()` |  |
| `android.os.Bundle getExtras()` |  |
| `android.app.Notification.Style getStyle()` |  |

---

### `class static final Notification.CarExtender`

- **实现：** `android.app.Notification.Extender`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification.CarExtender()` |  |
| `Notification.CarExtender(android.app.Notification)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.Notification.Builder extend(android.app.Notification.Builder)` |  |
| `android.graphics.Bitmap getLargeIcon()` |  |
| `android.app.Notification.CarExtender.UnreadConversation getUnreadConversation()` |  |
| `android.app.Notification.CarExtender setColor(@ColorInt int)` |  |
| `android.app.Notification.CarExtender setLargeIcon(android.graphics.Bitmap)` |  |
| `android.app.Notification.CarExtender setUnreadConversation(android.app.Notification.CarExtender.UnreadConversation)` |  |

---

### `class static Notification.CarExtender.Builder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification.CarExtender.Builder(String)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.Notification.CarExtender.Builder addMessage(String)` |  |
| `android.app.Notification.CarExtender.UnreadConversation build()` |  |
| `android.app.Notification.CarExtender.Builder setLatestTimestamp(long)` |  |
| `android.app.Notification.CarExtender.Builder setReadPendingIntent(android.app.PendingIntent)` |  |
| `android.app.Notification.CarExtender.Builder setReplyAction(android.app.PendingIntent, android.app.RemoteInput)` |  |

---

### `class static Notification.CarExtender.UnreadConversation`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `long getLatestTimestamp()` |  |
| `String[] getMessages()` |  |
| `String getParticipant()` |  |
| `String[] getParticipants()` |  |
| `android.app.PendingIntent getReadPendingIntent()` |  |
| `android.app.RemoteInput getRemoteInput()` |  |
| `android.app.PendingIntent getReplyPendingIntent()` |  |

---

### `class static Notification.DecoratedCustomViewStyle`

- **继承：** `android.app.Notification.Style`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification.DecoratedCustomViewStyle()` |  |

---

### `class static Notification.DecoratedMediaCustomViewStyle`

- **继承：** `android.app.Notification.MediaStyle`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification.DecoratedMediaCustomViewStyle()` |  |

---

### `interface static Notification.Extender`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.Notification.Builder extend(android.app.Notification.Builder)` |  |

---

### `class static Notification.InboxStyle`

- **继承：** `android.app.Notification.Style`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification.InboxStyle()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.Notification.InboxStyle addLine(CharSequence)` |  |
| `android.app.Notification.InboxStyle setBigContentTitle(CharSequence)` |  |
| `android.app.Notification.InboxStyle setSummaryText(CharSequence)` |  |

---

### `class static Notification.MediaStyle`

- **继承：** `android.app.Notification.Style`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification.MediaStyle()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.Notification.MediaStyle setMediaSession(android.media.session.MediaSession.Token)` |  |
| `android.app.Notification.MediaStyle setShowActionsInCompactView(int...)` |  |

---

### `class static Notification.MessagingStyle`

- **继承：** `android.app.Notification.Style`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification.MessagingStyle(@NonNull android.app.Person)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int MAXIMUM_RETAINED_MESSAGES = 25` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.Notification.MessagingStyle addHistoricMessage(android.app.Notification.MessagingStyle.Message)` |  |
| `android.app.Notification.MessagingStyle addMessage(@NonNull CharSequence, long, @Nullable android.app.Person)` |  |
| `android.app.Notification.MessagingStyle addMessage(android.app.Notification.MessagingStyle.Message)` |  |
| `java.util.List<android.app.Notification.MessagingStyle.Message> getHistoricMessages()` |  |
| `java.util.List<android.app.Notification.MessagingStyle.Message> getMessages()` |  |
| `boolean isGroupConversation()` |  |
| `android.app.Notification.MessagingStyle setConversationTitle(@Nullable CharSequence)` |  |
| `android.app.Notification.MessagingStyle setGroupConversation(boolean)` |  |

---

### `class static final Notification.MessagingStyle.Message`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification.MessagingStyle.Message(@NonNull CharSequence, long, @Nullable android.app.Person)` |  |
| `Notification.Style()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.Notification.Builder mBuilder` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `String getDataMimeType()` |  |
| `android.net.Uri getDataUri()` |  |
| `android.os.Bundle getExtras()` |  |
| `CharSequence getText()` |  |
| `long getTimestamp()` |  |
| `android.app.Notification.MessagingStyle.Message setData(String, android.net.Uri)` |  |
| `android.app.Notification build()` |  |
| `void checkBuilder()` |  |
| `android.widget.RemoteViews getStandardView(int)` |  |
| `void internalSetBigContentTitle(CharSequence)` |  |
| `void internalSetSummaryText(CharSequence)` |  |
| `void setBuilder(android.app.Notification.Builder)` |  |

---

### `class static final Notification.WearableExtender`

- **实现：** `android.app.Notification.Extender`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Notification.WearableExtender()` |  |
| `Notification.WearableExtender(android.app.Notification)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int UNSET_ACTION_INDEX = -1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.Notification.WearableExtender addAction(android.app.Notification.Action)` |  |
| `android.app.Notification.WearableExtender addActions(java.util.List<android.app.Notification.Action>)` |  |
| `android.app.Notification.WearableExtender clearActions()` |  |
| `android.app.Notification.WearableExtender clone()` |  |
| `android.app.Notification.Builder extend(android.app.Notification.Builder)` |  |
| `java.util.List<android.app.Notification.Action> getActions()` |  |
| `String getBridgeTag()` |  |
| `int getContentAction()` |  |
| `boolean getContentIntentAvailableOffline()` |  |
| `String getDismissalId()` |  |
| `boolean getHintContentIntentLaunchesActivity()` |  |
| `boolean getStartScrollBottom()` |  |
| `android.app.Notification.WearableExtender setBridgeTag(String)` |  |
| `android.app.Notification.WearableExtender setContentAction(int)` |  |
| `android.app.Notification.WearableExtender setContentIntentAvailableOffline(boolean)` |  |
| `android.app.Notification.WearableExtender setDismissalId(String)` |  |
| `android.app.Notification.WearableExtender setHintContentIntentLaunchesActivity(boolean)` |  |
| `android.app.Notification.WearableExtender setStartScrollBottom(boolean)` |  |

---

### `class final NotificationChannel`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `NotificationChannel(String, CharSequence, int)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String DEFAULT_CHANNEL_ID = "miscellaneous"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean canBubble()` |  |
| `boolean canBypassDnd()` |  |
| `boolean canShowBadge()` |  |
| `int describeContents()` |  |
| `void enableLights(boolean)` |  |
| `void enableVibration(boolean)` |  |
| `android.media.AudioAttributes getAudioAttributes()` |  |
| `String getDescription()` |  |
| `String getGroup()` |  |
| `String getId()` |  |
| `int getImportance()` |  |
| `int getLightColor()` |  |
| `int getLockscreenVisibility()` |  |
| `CharSequence getName()` |  |
| `android.net.Uri getSound()` |  |
| `long[] getVibrationPattern()` |  |
| `boolean hasUserSetImportance()` |  |
| `boolean hasUserSetSound()` |  |
| `boolean isImportantConversation()` |  |
| `void setAllowBubbles(boolean)` |  |
| `void setBypassDnd(boolean)` |  |
| `void setConversationId(@NonNull String, @NonNull String)` |  |
| `void setDescription(String)` |  |
| `void setGroup(String)` |  |
| `void setImportance(int)` |  |
| `void setLightColor(int)` |  |
| `void setLockscreenVisibility(int)` |  |
| `void setName(CharSequence)` |  |
| `void setShowBadge(boolean)` |  |
| `void setSound(android.net.Uri, android.media.AudioAttributes)` |  |
| `void setVibrationPattern(long[])` |  |
| `boolean shouldShowLights()` |  |
| `boolean shouldVibrate()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final NotificationChannelGroup`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `NotificationChannelGroup(String, CharSequence)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.NotificationChannelGroup clone()` |  |
| `int describeContents()` |  |
| `java.util.List<android.app.NotificationChannel> getChannels()` |  |
| `String getDescription()` |  |
| `String getId()` |  |
| `CharSequence getName()` |  |
| `boolean isBlocked()` |  |
| `void setDescription(String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class NotificationManager`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ACTION_APP_BLOCK_STATE_CHANGED = "android.app.action.APP_BLOCK_STATE_CHANGED"` |  |
| `static final String ACTION_AUTOMATIC_ZEN_RULE = "android.app.action.AUTOMATIC_ZEN_RULE"` |  |
| `static final String ACTION_AUTOMATIC_ZEN_RULE_STATUS_CHANGED = "android.app.action.AUTOMATIC_ZEN_RULE_STATUS_CHANGED"` |  |
| `static final String ACTION_INTERRUPTION_FILTER_CHANGED = "android.app.action.INTERRUPTION_FILTER_CHANGED"` |  |
| `static final String ACTION_NOTIFICATION_CHANNEL_BLOCK_STATE_CHANGED = "android.app.action.NOTIFICATION_CHANNEL_BLOCK_STATE_CHANGED"` |  |
| `static final String ACTION_NOTIFICATION_CHANNEL_GROUP_BLOCK_STATE_CHANGED = "android.app.action.NOTIFICATION_CHANNEL_GROUP_BLOCK_STATE_CHANGED"` |  |
| `static final String ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED = "android.app.action.NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED"` |  |
| `static final String ACTION_NOTIFICATION_POLICY_CHANGED = "android.app.action.NOTIFICATION_POLICY_CHANGED"` |  |
| `static final int AUTOMATIC_RULE_STATUS_DISABLED = 2` |  |
| `static final int AUTOMATIC_RULE_STATUS_ENABLED = 1` |  |
| `static final int AUTOMATIC_RULE_STATUS_REMOVED = 3` |  |
| `static final int AUTOMATIC_RULE_STATUS_UNKNOWN = -1` |  |
| `static final String EXTRA_AUTOMATIC_RULE_ID = "android.app.extra.AUTOMATIC_RULE_ID"` |  |
| `static final String EXTRA_AUTOMATIC_ZEN_RULE_ID = "android.app.extra.AUTOMATIC_ZEN_RULE_ID"` |  |
| `static final String EXTRA_AUTOMATIC_ZEN_RULE_STATUS = "android.app.extra.AUTOMATIC_ZEN_RULE_STATUS"` |  |
| `static final String EXTRA_BLOCKED_STATE = "android.app.extra.BLOCKED_STATE"` |  |
| `static final String EXTRA_NOTIFICATION_CHANNEL_GROUP_ID = "android.app.extra.NOTIFICATION_CHANNEL_GROUP_ID"` |  |
| `static final String EXTRA_NOTIFICATION_CHANNEL_ID = "android.app.extra.NOTIFICATION_CHANNEL_ID"` |  |
| `static final int IMPORTANCE_DEFAULT = 3` |  |
| `static final int IMPORTANCE_HIGH = 4` |  |
| `static final int IMPORTANCE_LOW = 2` |  |
| `static final int IMPORTANCE_MAX = 5` |  |
| `static final int IMPORTANCE_MIN = 1` |  |
| `static final int IMPORTANCE_NONE = 0` |  |
| `static final int IMPORTANCE_UNSPECIFIED = -1000` |  |
| `static final int INTERRUPTION_FILTER_ALARMS = 4` |  |
| `static final int INTERRUPTION_FILTER_ALL = 1` |  |
| `static final int INTERRUPTION_FILTER_NONE = 3` |  |
| `static final int INTERRUPTION_FILTER_PRIORITY = 2` |  |
| `static final int INTERRUPTION_FILTER_UNKNOWN = 0` |  |
| `static final String META_DATA_AUTOMATIC_RULE_TYPE = "android.service.zen.automatic.ruleType"` |  |
| `static final String META_DATA_RULE_INSTANCE_LIMIT = "android.service.zen.automatic.ruleInstanceLimit"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `String addAutomaticZenRule(android.app.AutomaticZenRule)` |  |
| `boolean areBubblesAllowed()` |  |
| `boolean areNotificationsEnabled()` |  |
| `boolean areNotificationsPaused()` |  |
| `boolean canNotifyAsPackage(@NonNull String)` |  |
| `void cancel(int)` |  |
| `void cancel(@Nullable String, int)` |  |
| `void cancelAll()` |  |
| `void cancelAsPackage(@NonNull String, @Nullable String, int)` |  |
| `void createNotificationChannel(@NonNull android.app.NotificationChannel)` |  |
| `void createNotificationChannelGroup(@NonNull android.app.NotificationChannelGroup)` |  |
| `void createNotificationChannelGroups(@NonNull java.util.List<android.app.NotificationChannelGroup>)` |  |
| `void createNotificationChannels(@NonNull java.util.List<android.app.NotificationChannel>)` |  |
| `void deleteNotificationChannel(String)` |  |
| `void deleteNotificationChannelGroup(String)` |  |
| `android.service.notification.StatusBarNotification[] getActiveNotifications()` |  |
| `android.app.AutomaticZenRule getAutomaticZenRule(String)` |  |
| `java.util.Map<java.lang.String,android.app.AutomaticZenRule> getAutomaticZenRules()` |  |
| `final int getCurrentInterruptionFilter()` |  |
| `int getImportance()` |  |
| `android.app.NotificationChannel getNotificationChannel(String)` |  |
| `android.app.NotificationChannelGroup getNotificationChannelGroup(String)` |  |
| `java.util.List<android.app.NotificationChannelGroup> getNotificationChannelGroups()` |  |
| `java.util.List<android.app.NotificationChannel> getNotificationChannels()` |  |
| `android.app.NotificationManager.Policy getNotificationPolicy()` |  |
| `boolean isNotificationListenerAccessGranted(android.content.ComponentName)` |  |
| `boolean isNotificationPolicyAccessGranted()` |  |
| `void notify(int, android.app.Notification)` |  |
| `void notify(String, int, android.app.Notification)` |  |
| `void notifyAsPackage(@NonNull String, @Nullable String, int, @NonNull android.app.Notification)` |  |
| `boolean removeAutomaticZenRule(String)` |  |
| `void setAutomaticZenRuleState(@NonNull String, @NonNull android.service.notification.Condition)` |  |
| `final void setInterruptionFilter(int)` |  |
| `void setNotificationDelegate(@Nullable String)` |  |
| `void setNotificationPolicy(@NonNull android.app.NotificationManager.Policy)` |  |
| `boolean shouldHideSilentStatusBarIcons()` |  |
| `boolean updateAutomaticZenRule(String, android.app.AutomaticZenRule)` |  |

---

### `class static NotificationManager.Policy`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `NotificationManager.Policy(int, int, int)` |  |
| `NotificationManager.Policy(int, int, int, int)` |  |
| `NotificationManager.Policy(int, int, int, int, int)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int CONVERSATION_SENDERS_ANYONE = 1` |  |
| `static final int CONVERSATION_SENDERS_IMPORTANT = 2` |  |
| `static final int CONVERSATION_SENDERS_NONE = 3` |  |
| `static final int PRIORITY_CATEGORY_ALARMS = 32` |  |
| `static final int PRIORITY_CATEGORY_CALLS = 8` |  |
| `static final int PRIORITY_CATEGORY_CONVERSATIONS = 256` |  |
| `static final int PRIORITY_CATEGORY_EVENTS = 2` |  |
| `static final int PRIORITY_CATEGORY_MEDIA = 64` |  |
| `static final int PRIORITY_CATEGORY_MESSAGES = 4` |  |
| `static final int PRIORITY_CATEGORY_REMINDERS = 1` |  |
| `static final int PRIORITY_CATEGORY_REPEAT_CALLERS = 16` |  |
| `static final int PRIORITY_CATEGORY_SYSTEM = 128` |  |
| `static final int PRIORITY_SENDERS_ANY = 0` |  |
| `static final int PRIORITY_SENDERS_CONTACTS = 1` |  |
| `static final int PRIORITY_SENDERS_STARRED = 2` |  |
| `static final int SUPPRESSED_EFFECT_AMBIENT = 128` |  |
| `static final int SUPPRESSED_EFFECT_BADGE = 64` |  |
| `static final int SUPPRESSED_EFFECT_FULL_SCREEN_INTENT = 4` |  |
| `static final int SUPPRESSED_EFFECT_LIGHTS = 8` |  |
| `static final int SUPPRESSED_EFFECT_NOTIFICATION_LIST = 256` |  |
| `static final int SUPPRESSED_EFFECT_PEEK = 16` |  |
| `static final int SUPPRESSED_EFFECT_STATUS_BAR = 32` |  |
| `final int priorityCallSenders` |  |
| `final int priorityCategories` |  |
| `final int priorityConversationSenders` |  |
| `final int priorityMessageSenders` |  |
| `final int suppressedVisualEffects` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `static String priorityCategoriesToString(int)` |  |
| `static String prioritySendersToString(int)` |  |
| `static String suppressedEffectsToString(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final PendingIntent`

- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int FLAG_CANCEL_CURRENT = 268435456` |  |
| `static final int FLAG_IMMUTABLE = 67108864` |  |
| `static final int FLAG_NO_CREATE = 536870912` |  |
| `static final int FLAG_ONE_SHOT = 1073741824` |  |
| `static final int FLAG_UPDATE_CURRENT = 134217728` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void cancel()` |  |
| `int describeContents()` |  |
| `static android.app.PendingIntent getActivities(android.content.Context, int, @NonNull android.content.Intent[], int)` |  |
| `static android.app.PendingIntent getActivities(android.content.Context, int, @NonNull android.content.Intent[], int, @Nullable android.os.Bundle)` |  |
| `static android.app.PendingIntent getActivity(android.content.Context, int, android.content.Intent, int)` |  |
| `static android.app.PendingIntent getActivity(android.content.Context, int, @NonNull android.content.Intent, int, @Nullable android.os.Bundle)` |  |
| `static android.app.PendingIntent getBroadcast(android.content.Context, int, android.content.Intent, int)` |  |
| `int getCreatorUid()` |  |
| `static android.app.PendingIntent getForegroundService(android.content.Context, int, @NonNull android.content.Intent, int)` |  |
| `android.content.IntentSender getIntentSender()` |  |
| `static android.app.PendingIntent getService(android.content.Context, int, @NonNull android.content.Intent, int)` |  |
| `void send() throws android.app.PendingIntent.CanceledException` |  |
| `void send(int) throws android.app.PendingIntent.CanceledException` |  |
| `void send(android.content.Context, int, @Nullable android.content.Intent) throws android.app.PendingIntent.CanceledException` |  |
| `void send(int, @Nullable android.app.PendingIntent.OnFinished, @Nullable android.os.Handler) throws android.app.PendingIntent.CanceledException` |  |
| `void send(android.content.Context, int, @Nullable android.content.Intent, @Nullable android.app.PendingIntent.OnFinished, @Nullable android.os.Handler) throws android.app.PendingIntent.CanceledException` |  |
| `void send(android.content.Context, int, @Nullable android.content.Intent, @Nullable android.app.PendingIntent.OnFinished, @Nullable android.os.Handler, @Nullable String) throws android.app.PendingIntent.CanceledException` |  |
| `void send(android.content.Context, int, @Nullable android.content.Intent, @Nullable android.app.PendingIntent.OnFinished, @Nullable android.os.Handler, @Nullable String, @Nullable android.os.Bundle) throws android.app.PendingIntent.CanceledException` |  |
| `static void writePendingIntentOrNullToParcel(@Nullable android.app.PendingIntent, @NonNull android.os.Parcel)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static PendingIntent.CanceledException`

- **继承：** `android.util.AndroidException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `PendingIntent.CanceledException()` |  |
| `PendingIntent.CanceledException(String)` |  |
| `PendingIntent.CanceledException(Exception)` |  |

---

### `interface static PendingIntent.OnFinished`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onSendFinished(android.app.PendingIntent, android.content.Intent, int, String, android.os.Bundle)` |  |

---

### `class final Person`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean isBot()` |  |
| `boolean isImportant()` |  |
| `android.app.Person.Builder toBuilder()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static Person.Builder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Person.Builder()` |  |

---

### `class final PictureInPictureParams`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static PictureInPictureParams.Builder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `PictureInPictureParams.Builder()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.PictureInPictureParams build()` |  |
| `android.app.PictureInPictureParams.Builder setActions(java.util.List<android.app.RemoteAction>)` |  |
| `android.app.PictureInPictureParams.Builder setAspectRatio(android.util.Rational)` |  |
| `android.app.PictureInPictureParams.Builder setSourceRectHint(android.graphics.Rect)` |  |

---

### `class Presentation`

- **继承：** `android.app.Dialog`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Presentation(android.content.Context, android.view.Display)` |  |
| `Presentation(android.content.Context, android.view.Display, int)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.view.Display getDisplay()` |  |
| `android.content.res.Resources getResources()` |  |
| `void onDisplayChanged()` |  |
| `void onDisplayRemoved()` |  |

---

### `class ProgressDialog` ~~DEPRECATED~~

- **继承：** `android.app.AlertDialog`
- **Annotations:** `@Deprecated`

---

### `class final RecoverableSecurityException`

- **继承：** `java.lang.SecurityException`
- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `RecoverableSecurityException(@NonNull Throwable, @NonNull CharSequence, @NonNull android.app.RemoteAction)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final RemoteAction`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `RemoteAction(@NonNull android.graphics.drawable.Icon, @NonNull CharSequence, @NonNull CharSequence, @NonNull android.app.PendingIntent)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.RemoteAction clone()` |  |
| `int describeContents()` |  |
| `void dump(String, java.io.PrintWriter)` |  |
| `boolean isEnabled()` |  |
| `void setEnabled(boolean)` |  |
| `void setShouldShowIcon(boolean)` |  |
| `boolean shouldShowIcon()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final RemoteInput`

- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int EDIT_CHOICES_BEFORE_SENDING_AUTO = 0` |  |
| `static final int EDIT_CHOICES_BEFORE_SENDING_DISABLED = 1` |  |
| `static final int EDIT_CHOICES_BEFORE_SENDING_ENABLED = 2` |  |
| `static final String EXTRA_RESULTS_DATA = "android.remoteinput.resultsData"` |  |
| `static final String RESULTS_CLIP_LABEL = "android.remoteinput.results"` |  |
| `static final int SOURCE_CHOICE = 1` |  |
| `static final int SOURCE_FREE_FORM_INPUT = 0` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `static void addDataResultToIntent(android.app.RemoteInput, android.content.Intent, java.util.Map<java.lang.String,android.net.Uri>)` |  |
| `static void addResultsToIntent(android.app.RemoteInput[], android.content.Intent, android.os.Bundle)` |  |
| `int describeContents()` |  |
| `boolean getAllowFreeFormInput()` |  |
| `java.util.Set<java.lang.String> getAllowedDataTypes()` |  |
| `CharSequence[] getChoices()` |  |
| `static java.util.Map<java.lang.String,android.net.Uri> getDataResultsFromIntent(android.content.Intent, String)` |  |
| `int getEditChoicesBeforeSending()` |  |
| `android.os.Bundle getExtras()` |  |
| `CharSequence getLabel()` |  |
| `String getResultKey()` |  |
| `static android.os.Bundle getResultsFromIntent(android.content.Intent)` |  |
| `static int getResultsSource(android.content.Intent)` |  |
| `boolean isDataOnly()` |  |
| `static void setResultsSource(android.content.Intent, int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final RemoteInput.Builder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `RemoteInput.Builder(@NonNull String)` |  |

---

### `class SearchManager`

- **实现：** `android.content.DialogInterface.OnCancelListener android.content.DialogInterface.OnDismissListener`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ACTION_KEY = "action_key"` |  |
| `static final String ACTION_MSG = "action_msg"` |  |
| `static final String APP_DATA = "app_data"` |  |
| `static final String CURSOR_EXTRA_KEY_IN_PROGRESS = "in_progress"` |  |
| `static final String EXTRA_DATA_KEY = "intent_extra_data_key"` |  |
| `static final String EXTRA_NEW_SEARCH = "new_search"` |  |
| `static final String EXTRA_SELECT_QUERY = "select_query"` |  |
| `static final String EXTRA_WEB_SEARCH_PENDINGINTENT = "web_search_pendingintent"` |  |
| `static final int FLAG_QUERY_REFINEMENT = 1` |  |
| `static final String INTENT_ACTION_GLOBAL_SEARCH = "android.search.action.GLOBAL_SEARCH"` |  |
| `static final String INTENT_ACTION_SEARCHABLES_CHANGED = "android.search.action.SEARCHABLES_CHANGED"` |  |
| `static final String INTENT_ACTION_SEARCH_SETTINGS = "android.search.action.SEARCH_SETTINGS"` |  |
| `static final String INTENT_ACTION_SEARCH_SETTINGS_CHANGED = "android.search.action.SETTINGS_CHANGED"` |  |
| `static final String INTENT_ACTION_WEB_SEARCH_SETTINGS = "android.search.action.WEB_SEARCH_SETTINGS"` |  |
| `static final String INTENT_GLOBAL_SEARCH_ACTIVITY_CHANGED = "android.search.action.GLOBAL_SEARCH_ACTIVITY_CHANGED"` |  |
| `static final char MENU_KEY = 115` |  |
| `static final int MENU_KEYCODE = 47` |  |
| `static final String QUERY = "query"` |  |
| `static final String SHORTCUT_MIME_TYPE = "vnd.android.cursor.item/vnd.android.search.suggest"` |  |
| `static final String SUGGEST_COLUMN_AUDIO_CHANNEL_CONFIG = "suggest_audio_channel_config"` |  |
| `static final String SUGGEST_COLUMN_CONTENT_TYPE = "suggest_content_type"` |  |
| `static final String SUGGEST_COLUMN_DURATION = "suggest_duration"` |  |
| `static final String SUGGEST_COLUMN_FLAGS = "suggest_flags"` |  |
| `static final String SUGGEST_COLUMN_FORMAT = "suggest_format"` |  |
| `static final String SUGGEST_COLUMN_ICON_1 = "suggest_icon_1"` |  |
| `static final String SUGGEST_COLUMN_ICON_2 = "suggest_icon_2"` |  |
| `static final String SUGGEST_COLUMN_INTENT_ACTION = "suggest_intent_action"` |  |
| `static final String SUGGEST_COLUMN_INTENT_DATA = "suggest_intent_data"` |  |
| `static final String SUGGEST_COLUMN_INTENT_DATA_ID = "suggest_intent_data_id"` |  |
| `static final String SUGGEST_COLUMN_INTENT_EXTRA_DATA = "suggest_intent_extra_data"` |  |
| `static final String SUGGEST_COLUMN_IS_LIVE = "suggest_is_live"` |  |
| `static final String SUGGEST_COLUMN_LAST_ACCESS_HINT = "suggest_last_access_hint"` |  |
| `static final String SUGGEST_COLUMN_PRODUCTION_YEAR = "suggest_production_year"` |  |
| `static final String SUGGEST_COLUMN_PURCHASE_PRICE = "suggest_purchase_price"` |  |
| `static final String SUGGEST_COLUMN_QUERY = "suggest_intent_query"` |  |
| `static final String SUGGEST_COLUMN_RATING_SCORE = "suggest_rating_score"` |  |
| `static final String SUGGEST_COLUMN_RATING_STYLE = "suggest_rating_style"` |  |
| `static final String SUGGEST_COLUMN_RENTAL_PRICE = "suggest_rental_price"` |  |
| `static final String SUGGEST_COLUMN_RESULT_CARD_IMAGE = "suggest_result_card_image"` |  |
| `static final String SUGGEST_COLUMN_SHORTCUT_ID = "suggest_shortcut_id"` |  |
| `static final String SUGGEST_COLUMN_SPINNER_WHILE_REFRESHING = "suggest_spinner_while_refreshing"` |  |
| `static final String SUGGEST_COLUMN_TEXT_1 = "suggest_text_1"` |  |
| `static final String SUGGEST_COLUMN_TEXT_2 = "suggest_text_2"` |  |
| `static final String SUGGEST_COLUMN_TEXT_2_URL = "suggest_text_2_url"` |  |
| `static final String SUGGEST_COLUMN_VIDEO_HEIGHT = "suggest_video_height"` |  |
| `static final String SUGGEST_COLUMN_VIDEO_WIDTH = "suggest_video_width"` |  |
| `static final String SUGGEST_MIME_TYPE = "vnd.android.cursor.dir/vnd.android.search.suggest"` |  |
| `static final String SUGGEST_NEVER_MAKE_SHORTCUT = "_-1"` |  |
| `static final String SUGGEST_PARAMETER_LIMIT = "limit"` |  |
| `static final String SUGGEST_URI_PATH_QUERY = "search_suggest_query"` |  |
| `static final String SUGGEST_URI_PATH_SHORTCUT = "search_suggest_shortcut"` |  |
| `static final String USER_QUERY = "user_query"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.content.ComponentName getGlobalSearchActivity()` |  |
| `android.app.SearchableInfo getSearchableInfo(android.content.ComponentName)` |  |
| `java.util.List<android.app.SearchableInfo> getSearchablesInGlobalSearch()` |  |
| `void setOnCancelListener(android.app.SearchManager.OnCancelListener)` |  |
| `void setOnDismissListener(android.app.SearchManager.OnDismissListener)` |  |
| `void startSearch(String, boolean, android.content.ComponentName, android.os.Bundle, boolean)` |  |
| `void stopSearch()` |  |
| `void triggerSearch(String, android.content.ComponentName, android.os.Bundle)` |  |

---

### `interface static SearchManager.OnCancelListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onCancel()` |  |

---

### `interface static SearchManager.OnDismissListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onDismiss()` |  |

---

### `class final SearchableInfo`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean autoUrlDetect()` |  |
| `int describeContents()` |  |
| `int getHintId()` |  |
| `int getImeOptions()` |  |
| `int getInputType()` |  |
| `android.content.ComponentName getSearchActivity()` |  |
| `int getSettingsDescriptionId()` |  |
| `String getSuggestAuthority()` |  |
| `String getSuggestIntentAction()` |  |
| `String getSuggestIntentData()` |  |
| `String getSuggestPackage()` |  |
| `String getSuggestPath()` |  |
| `String getSuggestSelection()` |  |
| `int getSuggestThreshold()` |  |
| `int getVoiceMaxResults()` |  |
| `boolean getVoiceSearchEnabled()` |  |
| `boolean getVoiceSearchLaunchRecognizer()` |  |
| `boolean getVoiceSearchLaunchWebSearch()` |  |
| `boolean queryAfterZeroResults()` |  |
| `boolean shouldIncludeInGlobalSearch()` |  |
| `boolean shouldRewriteQueryFromData()` |  |
| `boolean shouldRewriteQueryFromText()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class abstract Service`

- **继承：** `android.content.ContextWrapper`
- **实现：** `android.content.ComponentCallbacks2`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Service()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int START_CONTINUATION_MASK = 15` |  |
| `static final int START_FLAG_REDELIVERY = 1` |  |
| `static final int START_FLAG_RETRY = 2` |  |
| `static final int START_NOT_STICKY = 2` |  |
| `static final int START_REDELIVER_INTENT = 3` |  |
| `static final int START_STICKY = 1` |  |
| `static final int START_STICKY_COMPATIBILITY = 0` |  |
| `static final int STOP_FOREGROUND_DETACH = 2` |  |
| `static final int STOP_FOREGROUND_REMOVE = 1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void dump(java.io.FileDescriptor, java.io.PrintWriter, String[])` |  |
| `final android.app.Application getApplication()` |  |
| `final int getForegroundServiceType()` |  |
| `void onConfigurationChanged(android.content.res.Configuration)` |  |
| `void onCreate()` |  |
| `void onDestroy()` |  |
| `void onLowMemory()` |  |
| `void onRebind(android.content.Intent)` |  |
| `int onStartCommand(android.content.Intent, int, int)` |  |
| `void onTaskRemoved(android.content.Intent)` |  |
| `void onTrimMemory(int)` |  |
| `boolean onUnbind(android.content.Intent)` |  |
| `final void startForeground(int, android.app.Notification)` |  |
| `final void startForeground(int, @NonNull android.app.Notification, int)` |  |
| `final void stopForeground(boolean)` |  |
| `final void stopForeground(int)` |  |
| `final void stopSelf()` |  |
| `final void stopSelf(int)` |  |
| `final boolean stopSelfResult(int)` |  |

---

### `class abstract SharedElementCallback`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SharedElementCallback()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.os.Parcelable onCaptureSharedElementSnapshot(android.view.View, android.graphics.Matrix, android.graphics.RectF)` |  |
| `android.view.View onCreateSnapshotView(android.content.Context, android.os.Parcelable)` |  |
| `void onMapSharedElements(java.util.List<java.lang.String>, java.util.Map<java.lang.String,android.view.View>)` |  |
| `void onRejectSharedElements(java.util.List<android.view.View>)` |  |
| `void onSharedElementEnd(java.util.List<java.lang.String>, java.util.List<android.view.View>, java.util.List<android.view.View>)` |  |
| `void onSharedElementStart(java.util.List<java.lang.String>, java.util.List<android.view.View>, java.util.List<android.view.View>)` |  |
| `void onSharedElementsArrived(java.util.List<java.lang.String>, java.util.List<android.view.View>, android.app.SharedElementCallback.OnSharedElementsReadyListener)` |  |

---

### `interface static SharedElementCallback.OnSharedElementsReadyListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onSharedElementsReady()` |  |

---

### `class StatusBarManager`


---

### `class final SyncNotedAppOp`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SyncNotedAppOp(@IntRange(from=0L) int, @Nullable String)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class TabActivity` ~~DEPRECATED~~

- **继承：** `android.app.ActivityGroup`
- **Annotations:** `@Deprecated`

---

### `class TaskInfo`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean isRunning` |  |
| `int numActivities` |  |
| `int taskId` |  |

---

### `class TaskStackBuilder`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.TaskStackBuilder addNextIntent(android.content.Intent)` |  |
| `android.app.TaskStackBuilder addNextIntentWithParentStack(android.content.Intent)` |  |
| `android.app.TaskStackBuilder addParentStack(android.app.Activity)` |  |
| `android.app.TaskStackBuilder addParentStack(Class<?>)` |  |
| `android.app.TaskStackBuilder addParentStack(android.content.ComponentName)` |  |
| `static android.app.TaskStackBuilder create(android.content.Context)` |  |
| `android.content.Intent editIntentAt(int)` |  |
| `int getIntentCount()` |  |
| `android.app.PendingIntent getPendingIntent(int, int)` |  |
| `android.app.PendingIntent getPendingIntent(int, int, android.os.Bundle)` |  |
| `void startActivities()` |  |
| `void startActivities(android.os.Bundle)` |  |

---

### `class TimePickerDialog`

- **继承：** `android.app.AlertDialog`
- **实现：** `android.content.DialogInterface.OnClickListener android.widget.TimePicker.OnTimeChangedListener`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `TimePickerDialog(android.content.Context, android.app.TimePickerDialog.OnTimeSetListener, int, int, boolean)` |  |
| `TimePickerDialog(android.content.Context, int, android.app.TimePickerDialog.OnTimeSetListener, int, int, boolean)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onClick(android.content.DialogInterface, int)` |  |
| `void onTimeChanged(android.widget.TimePicker, int, int)` |  |
| `void updateTime(int, int)` |  |

---

### `interface static TimePickerDialog.OnTimeSetListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onTimeSet(android.widget.TimePicker, int, int)` |  |

---

### `class final UiAutomation`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES = 1` |  |
| `static final int ROTATION_FREEZE_0 = 0` |  |
| `static final int ROTATION_FREEZE_180 = 2` |  |
| `static final int ROTATION_FREEZE_270 = 3` |  |
| `static final int ROTATION_FREEZE_90 = 1` |  |
| `static final int ROTATION_FREEZE_CURRENT = -1` |  |
| `static final int ROTATION_UNFREEZE = -2` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void adoptShellPermissionIdentity()` |  |
| `void adoptShellPermissionIdentity(@Nullable java.lang.String...)` |  |
| `void clearWindowAnimationFrameStats()` |  |
| `boolean clearWindowContentFrameStats(int)` |  |
| `void dropShellPermissionIdentity()` |  |
| `android.view.accessibility.AccessibilityEvent executeAndWaitForEvent(Runnable, android.app.UiAutomation.AccessibilityEventFilter, long) throws java.util.concurrent.TimeoutException` |  |
| `android.os.ParcelFileDescriptor executeShellCommand(String)` |  |
| `android.view.accessibility.AccessibilityNodeInfo findFocus(int)` |  |
| `android.view.accessibility.AccessibilityNodeInfo getRootInActiveWindow()` |  |
| `android.accessibilityservice.AccessibilityServiceInfo getServiceInfo()` |  |
| `android.view.WindowAnimationFrameStats getWindowAnimationFrameStats()` |  |
| `android.view.WindowContentFrameStats getWindowContentFrameStats(int)` |  |
| `java.util.List<android.view.accessibility.AccessibilityWindowInfo> getWindows()` |  |
| `void grantRuntimePermission(String, String)` |  |
| `void grantRuntimePermissionAsUser(String, String, android.os.UserHandle)` |  |
| `boolean injectInputEvent(android.view.InputEvent, boolean)` |  |
| `boolean performGlobalAction(int)` |  |
| `void revokeRuntimePermission(String, String)` |  |
| `void revokeRuntimePermissionAsUser(String, String, android.os.UserHandle)` |  |
| `void setOnAccessibilityEventListener(android.app.UiAutomation.OnAccessibilityEventListener)` |  |
| `boolean setRotation(int)` |  |
| `void setRunAsMonkey(boolean)` |  |
| `void setServiceInfo(android.accessibilityservice.AccessibilityServiceInfo)` |  |
| `android.graphics.Bitmap takeScreenshot()` |  |
| `void waitForIdle(long, long) throws java.util.concurrent.TimeoutException` |  |

---

### `interface static UiAutomation.AccessibilityEventFilter`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean accept(android.view.accessibility.AccessibilityEvent)` |  |

---

### `interface static UiAutomation.OnAccessibilityEventListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onAccessibilityEvent(android.view.accessibility.AccessibilityEvent)` |  |

---

### `class UiModeManager`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static String ACTION_ENTER_CAR_MODE` |  |
| `static String ACTION_ENTER_DESK_MODE` |  |
| `static String ACTION_EXIT_CAR_MODE` |  |
| `static String ACTION_EXIT_DESK_MODE` |  |
| `static final int DISABLE_CAR_MODE_GO_HOME = 1` |  |
| `static final int ENABLE_CAR_MODE_ALLOW_SLEEP = 2` |  |
| `static final int ENABLE_CAR_MODE_GO_CAR_HOME = 1` |  |
| `static final int MODE_NIGHT_AUTO = 0` |  |
| `static final int MODE_NIGHT_CUSTOM = 3` |  |
| `static final int MODE_NIGHT_NO = 1` |  |
| `static final int MODE_NIGHT_YES = 2` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void disableCarMode(int)` |  |
| `void enableCarMode(int)` |  |
| `int getCurrentModeType()` |  |
| `int getNightMode()` |  |
| `void setCustomNightModeEnd(@NonNull java.time.LocalTime)` |  |
| `void setCustomNightModeStart(@NonNull java.time.LocalTime)` |  |
| `void setNightMode(int)` |  |

---

### `class final VoiceInteractor`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.VoiceInteractor.Request getActiveRequest(String)` |  |
| `android.app.VoiceInteractor.Request[] getActiveRequests()` |  |
| `boolean isDestroyed()` |  |
| `void notifyDirectActionsChanged()` |  |
| `boolean registerOnDestroyedCallback(@NonNull java.util.concurrent.Executor, @NonNull Runnable)` |  |
| `boolean submitRequest(android.app.VoiceInteractor.Request)` |  |
| `boolean submitRequest(android.app.VoiceInteractor.Request, String)` |  |
| `boolean[] supportsCommands(String[])` |  |
| `boolean unregisterOnDestroyedCallback(@NonNull Runnable)` |  |

---

### `class static VoiceInteractor.AbortVoiceRequest`

- **继承：** `android.app.VoiceInteractor.Request`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `VoiceInteractor.AbortVoiceRequest(@Nullable android.app.VoiceInteractor.Prompt, @Nullable android.os.Bundle)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onAbortResult(android.os.Bundle)` |  |

---

### `class static VoiceInteractor.CommandRequest`

- **继承：** `android.app.VoiceInteractor.Request`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `VoiceInteractor.CommandRequest(String, android.os.Bundle)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onCommandResult(boolean, android.os.Bundle)` |  |

---

### `class static VoiceInteractor.CompleteVoiceRequest`

- **继承：** `android.app.VoiceInteractor.Request`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `VoiceInteractor.CompleteVoiceRequest(@Nullable android.app.VoiceInteractor.Prompt, @Nullable android.os.Bundle)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onCompleteResult(android.os.Bundle)` |  |

---

### `class static VoiceInteractor.ConfirmationRequest`

- **继承：** `android.app.VoiceInteractor.Request`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `VoiceInteractor.ConfirmationRequest(@Nullable android.app.VoiceInteractor.Prompt, @Nullable android.os.Bundle)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onConfirmationResult(boolean, android.os.Bundle)` |  |

---

### `class static VoiceInteractor.PickOptionRequest`

- **继承：** `android.app.VoiceInteractor.Request`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `VoiceInteractor.PickOptionRequest(@Nullable android.app.VoiceInteractor.Prompt, android.app.VoiceInteractor.PickOptionRequest.Option[], @Nullable android.os.Bundle)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onPickOptionResult(boolean, android.app.VoiceInteractor.PickOptionRequest.Option[], android.os.Bundle)` |  |

---

### `class static final VoiceInteractor.PickOptionRequest.Option`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `VoiceInteractor.PickOptionRequest.Option(CharSequence, int)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.VoiceInteractor.PickOptionRequest.Option addSynonym(CharSequence)` |  |
| `int countSynonyms()` |  |
| `int describeContents()` |  |
| `android.os.Bundle getExtras()` |  |
| `int getIndex()` |  |
| `CharSequence getLabel()` |  |
| `CharSequence getSynonymAt(int)` |  |
| `void setExtras(android.os.Bundle)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static VoiceInteractor.Prompt`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `VoiceInteractor.Prompt(@NonNull CharSequence[], @NonNull CharSequence)` |  |
| `VoiceInteractor.Prompt(@NonNull CharSequence)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int countVoicePrompts()` |  |
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |
| `void cancel()` |  |
| `android.app.Activity getActivity()` |  |
| `android.content.Context getContext()` |  |
| `String getName()` |  |
| `void onAttached(android.app.Activity)` |  |
| `void onCancel()` |  |
| `void onDetached()` |  |

---

### `class final WallpaperColors`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `WallpaperColors(android.os.Parcel)` |  |
| `WallpaperColors(@NonNull android.graphics.Color, @Nullable android.graphics.Color, @Nullable android.graphics.Color)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `static android.app.WallpaperColors fromBitmap(@NonNull android.graphics.Bitmap)` |  |
| `static android.app.WallpaperColors fromDrawable(android.graphics.drawable.Drawable)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final WallpaperInfo`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `WallpaperInfo(android.content.Context, android.content.pm.ResolveInfo) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void dump(android.util.Printer, String)` |  |
| `android.content.ComponentName getComponent()` |  |
| `String getPackageName()` |  |
| `android.content.pm.ServiceInfo getServiceInfo()` |  |
| `String getServiceName()` |  |
| `String getSettingsActivity()` |  |
| `boolean getShowMetadataInPreview()` |  |
| `CharSequence loadAuthor(android.content.pm.PackageManager) throws android.content.res.Resources.NotFoundException` |  |
| `CharSequence loadContextDescription(android.content.pm.PackageManager) throws android.content.res.Resources.NotFoundException` |  |
| `android.net.Uri loadContextUri(android.content.pm.PackageManager) throws android.content.res.Resources.NotFoundException` |  |
| `CharSequence loadDescription(android.content.pm.PackageManager) throws android.content.res.Resources.NotFoundException` |  |
| `android.graphics.drawable.Drawable loadIcon(android.content.pm.PackageManager)` |  |
| `CharSequence loadLabel(android.content.pm.PackageManager)` |  |
| `android.graphics.drawable.Drawable loadThumbnail(android.content.pm.PackageManager)` |  |
| `boolean supportsMultipleDisplays()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class WallpaperManager`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ACTION_CHANGE_LIVE_WALLPAPER = "android.service.wallpaper.CHANGE_LIVE_WALLPAPER"` |  |
| `static final String ACTION_CROP_AND_SET_WALLPAPER = "android.service.wallpaper.CROP_AND_SET_WALLPAPER"` |  |
| `static final String ACTION_LIVE_WALLPAPER_CHOOSER = "android.service.wallpaper.LIVE_WALLPAPER_CHOOSER"` |  |
| `static final String COMMAND_DROP = "android.home.drop"` |  |
| `static final String COMMAND_SECONDARY_TAP = "android.wallpaper.secondaryTap"` |  |
| `static final String COMMAND_TAP = "android.wallpaper.tap"` |  |
| `static final String EXTRA_LIVE_WALLPAPER_COMPONENT = "android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT"` |  |
| `static final int FLAG_LOCK = 2` |  |
| `static final int FLAG_SYSTEM = 1` |  |
| `static final String WALLPAPER_PREVIEW_META_DATA = "android.wallpaper.preview"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void addOnColorsChangedListener(@NonNull android.app.WallpaperManager.OnColorsChangedListener, @NonNull android.os.Handler)` |  |
| `void clearWallpaperOffsets(android.os.IBinder)` |  |
| `void forgetLoadedWallpaper()` |  |
| `android.graphics.drawable.Drawable getBuiltInDrawable()` |  |
| `android.graphics.drawable.Drawable getBuiltInDrawable(int)` |  |
| `android.graphics.drawable.Drawable getBuiltInDrawable(int, int, boolean, float, float)` |  |
| `android.graphics.drawable.Drawable getBuiltInDrawable(int, int, boolean, float, float, int)` |  |
| `android.content.Intent getCropAndSetWallpaperIntent(android.net.Uri)` |  |
| `int getDesiredMinimumHeight()` |  |
| `int getDesiredMinimumWidth()` |  |
| `android.graphics.drawable.Drawable getDrawable()` |  |
| `static android.app.WallpaperManager getInstance(android.content.Context)` |  |
| `int getWallpaperId(int)` |  |
| `android.app.WallpaperInfo getWallpaperInfo()` |  |
| `boolean hasResourceWallpaper(@RawRes int)` |  |
| `boolean isSetWallpaperAllowed()` |  |
| `boolean isWallpaperSupported()` |  |
| `android.graphics.drawable.Drawable peekDrawable()` |  |
| `void removeOnColorsChangedListener(@NonNull android.app.WallpaperManager.OnColorsChangedListener)` |  |
| `void sendWallpaperCommand(android.os.IBinder, String, int, int, int, android.os.Bundle)` |  |
| `void setWallpaperOffsetSteps(float, float)` |  |
| `void setWallpaperOffsets(android.os.IBinder, float, float)` |  |
| `void suggestDesiredDimensions(int, int)` |  |

---

### `interface static WallpaperManager.OnColorsChangedListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onColorsChanged(android.app.WallpaperColors, int)` |  |

---

### `interface ZygotePreload`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void doPreload(@NonNull android.content.pm.ApplicationInfo)` |  |

---

## 包： `android.app.admin`

### `class final ConnectEvent`

- **继承：** `android.app.admin.NetworkEvent`
- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `java.net.InetAddress getInetAddress()` |  |
| `int getPort()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class DelegatedAdminReceiver`

- **继承：** `android.content.BroadcastReceiver`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `DelegatedAdminReceiver()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onNetworkLogsAvailable(@NonNull android.content.Context, @NonNull android.content.Intent, long, @IntRange(from=1) int)` |  |
| `final void onReceive(@NonNull android.content.Context, @NonNull android.content.Intent)` |  |

---

### `class final DeviceAdminInfo`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `DeviceAdminInfo(android.content.Context, android.content.pm.ResolveInfo) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int USES_ENCRYPTED_STORAGE = 7` |  |
| `static final int USES_POLICY_DISABLE_CAMERA = 8` |  |
| `static final int USES_POLICY_DISABLE_KEYGUARD_FEATURES = 9` |  |
| `static final int USES_POLICY_EXPIRE_PASSWORD = 6` |  |
| `static final int USES_POLICY_FORCE_LOCK = 3` |  |
| `static final int USES_POLICY_LIMIT_PASSWORD = 0` |  |
| `static final int USES_POLICY_RESET_PASSWORD = 2` |  |
| `static final int USES_POLICY_WATCH_LOGIN = 1` |  |
| `static final int USES_POLICY_WIPE_DATA = 4` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void dump(android.util.Printer, String)` |  |
| `android.content.pm.ActivityInfo getActivityInfo()` |  |
| `String getPackageName()` |  |
| `String getReceiverName()` |  |
| `String getTagForPolicy(int)` |  |
| `boolean isVisible()` |  |
| `CharSequence loadDescription(android.content.pm.PackageManager) throws android.content.res.Resources.NotFoundException` |  |
| `android.graphics.drawable.Drawable loadIcon(android.content.pm.PackageManager)` |  |
| `CharSequence loadLabel(android.content.pm.PackageManager)` |  |
| `boolean supportsTransferOwnership()` |  |
| `boolean usesPolicy(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class DeviceAdminReceiver`

- **继承：** `android.content.BroadcastReceiver`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `DeviceAdminReceiver()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ACTION_CHOOSE_PRIVATE_KEY_ALIAS = "android.app.action.CHOOSE_PRIVATE_KEY_ALIAS"` |  |
| `static final String ACTION_DEVICE_ADMIN_DISABLED = "android.app.action.DEVICE_ADMIN_DISABLED"` |  |
| `static final String ACTION_DEVICE_ADMIN_DISABLE_REQUESTED = "android.app.action.DEVICE_ADMIN_DISABLE_REQUESTED"` |  |
| `static final String ACTION_DEVICE_ADMIN_ENABLED = "android.app.action.DEVICE_ADMIN_ENABLED"` |  |
| `static final String ACTION_LOCK_TASK_ENTERING = "android.app.action.LOCK_TASK_ENTERING"` |  |
| `static final String ACTION_LOCK_TASK_EXITING = "android.app.action.LOCK_TASK_EXITING"` |  |
| `static final String ACTION_NETWORK_LOGS_AVAILABLE = "android.app.action.NETWORK_LOGS_AVAILABLE"` |  |
| `static final String ACTION_PASSWORD_CHANGED = "android.app.action.ACTION_PASSWORD_CHANGED"` |  |
| `static final String ACTION_PASSWORD_EXPIRING = "android.app.action.ACTION_PASSWORD_EXPIRING"` |  |
| `static final String ACTION_PASSWORD_FAILED = "android.app.action.ACTION_PASSWORD_FAILED"` |  |
| `static final String ACTION_PASSWORD_SUCCEEDED = "android.app.action.ACTION_PASSWORD_SUCCEEDED"` |  |
| `static final String ACTION_PROFILE_PROVISIONING_COMPLETE = "android.app.action.PROFILE_PROVISIONING_COMPLETE"` |  |
| `static final int BUGREPORT_FAILURE_FAILED_COMPLETING = 0` |  |
| `static final int BUGREPORT_FAILURE_FILE_NO_LONGER_AVAILABLE = 1` |  |
| `static final String DEVICE_ADMIN_META_DATA = "android.app.device_admin"` |  |
| `static final String EXTRA_DISABLE_WARNING = "android.app.extra.DISABLE_WARNING"` |  |
| `static final String EXTRA_LOCK_TASK_PACKAGE = "android.app.extra.LOCK_TASK_PACKAGE"` |  |
| `static final String EXTRA_TRANSFER_OWNERSHIP_ADMIN_EXTRAS_BUNDLE = "android.app.extra.TRANSFER_OWNERSHIP_ADMIN_EXTRAS_BUNDLE"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onBugreportFailed(@NonNull android.content.Context, @NonNull android.content.Intent, int)` |  |
| `void onBugreportShared(@NonNull android.content.Context, @NonNull android.content.Intent, @NonNull String)` |  |
| `void onBugreportSharingDeclined(@NonNull android.content.Context, @NonNull android.content.Intent)` |  |
| `void onDisabled(@NonNull android.content.Context, @NonNull android.content.Intent)` |  |
| `void onEnabled(@NonNull android.content.Context, @NonNull android.content.Intent)` |  |
| `void onLockTaskModeEntering(@NonNull android.content.Context, @NonNull android.content.Intent, @NonNull String)` |  |
| `void onLockTaskModeExiting(@NonNull android.content.Context, @NonNull android.content.Intent)` |  |
| `void onNetworkLogsAvailable(@NonNull android.content.Context, @NonNull android.content.Intent, long, @IntRange(from=1) int)` |  |
| `void onPasswordChanged(@NonNull android.content.Context, @NonNull android.content.Intent, @NonNull android.os.UserHandle)` |  |
| `void onPasswordExpiring(@NonNull android.content.Context, @NonNull android.content.Intent, @NonNull android.os.UserHandle)` |  |
| `void onPasswordFailed(@NonNull android.content.Context, @NonNull android.content.Intent, @NonNull android.os.UserHandle)` |  |
| `void onPasswordSucceeded(@NonNull android.content.Context, @NonNull android.content.Intent, @NonNull android.os.UserHandle)` |  |
| `void onProfileProvisioningComplete(@NonNull android.content.Context, @NonNull android.content.Intent)` |  |
| `void onReceive(@NonNull android.content.Context, @NonNull android.content.Intent)` |  |
| `void onSecurityLogsAvailable(@NonNull android.content.Context, @NonNull android.content.Intent)` |  |
| `void onSystemUpdatePending(@NonNull android.content.Context, @NonNull android.content.Intent, long)` |  |
| `void onTransferAffiliatedProfileOwnershipComplete(@NonNull android.content.Context, @NonNull android.os.UserHandle)` |  |
| `void onTransferOwnershipComplete(@NonNull android.content.Context, @Nullable android.os.PersistableBundle)` |  |
| `void onUserAdded(@NonNull android.content.Context, @NonNull android.content.Intent, @NonNull android.os.UserHandle)` |  |
| `void onUserRemoved(@NonNull android.content.Context, @NonNull android.content.Intent, @NonNull android.os.UserHandle)` |  |
| `void onUserStarted(@NonNull android.content.Context, @NonNull android.content.Intent, @NonNull android.os.UserHandle)` |  |
| `void onUserStopped(@NonNull android.content.Context, @NonNull android.content.Intent, @NonNull android.os.UserHandle)` |  |
| `void onUserSwitched(@NonNull android.content.Context, @NonNull android.content.Intent, @NonNull android.os.UserHandle)` |  |

---

### `class DeviceAdminService`

- **继承：** `android.app.Service`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `DeviceAdminService()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `final android.os.IBinder onBind(android.content.Intent)` |  |

---

### `class DevicePolicyManager`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `DevicePolicyManager.InstallSystemUpdateCallback()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ACTION_ADD_DEVICE_ADMIN = "android.app.action.ADD_DEVICE_ADMIN"` |  |
| `static final String ACTION_ADMIN_POLICY_COMPLIANCE = "android.app.action.ADMIN_POLICY_COMPLIANCE"` |  |
| `static final String ACTION_APPLICATION_DELEGATION_SCOPES_CHANGED = "android.app.action.APPLICATION_DELEGATION_SCOPES_CHANGED"` |  |
| `static final String ACTION_CHECK_POLICY_COMPLIANCE = "android.app.action.CHECK_POLICY_COMPLIANCE"` |  |
| `static final String ACTION_DEVICE_ADMIN_SERVICE = "android.app.action.DEVICE_ADMIN_SERVICE"` |  |
| `static final String ACTION_DEVICE_OWNER_CHANGED = "android.app.action.DEVICE_OWNER_CHANGED"` |  |
| `static final String ACTION_GET_PROVISIONING_MODE = "android.app.action.GET_PROVISIONING_MODE"` |  |
| `static final String ACTION_MANAGED_PROFILE_PROVISIONED = "android.app.action.MANAGED_PROFILE_PROVISIONED"` |  |
| `static final String ACTION_PROFILE_OWNER_CHANGED = "android.app.action.PROFILE_OWNER_CHANGED"` |  |
| `static final String ACTION_PROVISIONING_SUCCESSFUL = "android.app.action.PROVISIONING_SUCCESSFUL"` |  |
| `static final String ACTION_PROVISION_MANAGED_DEVICE = "android.app.action.PROVISION_MANAGED_DEVICE"` |  |
| `static final String ACTION_PROVISION_MANAGED_PROFILE = "android.app.action.PROVISION_MANAGED_PROFILE"` |  |
| `static final String ACTION_SET_NEW_PARENT_PROFILE_PASSWORD = "android.app.action.SET_NEW_PARENT_PROFILE_PASSWORD"` |  |
| `static final String ACTION_SET_NEW_PASSWORD = "android.app.action.SET_NEW_PASSWORD"` |  |
| `static final String ACTION_START_ENCRYPTION = "android.app.action.START_ENCRYPTION"` |  |
| `static final String ACTION_SYSTEM_UPDATE_POLICY_CHANGED = "android.app.action.SYSTEM_UPDATE_POLICY_CHANGED"` |  |
| `static final String DELEGATION_APP_RESTRICTIONS = "delegation-app-restrictions"` |  |
| `static final String DELEGATION_BLOCK_UNINSTALL = "delegation-block-uninstall"` |  |
| `static final String DELEGATION_CERT_INSTALL = "delegation-cert-install"` |  |
| `static final String DELEGATION_CERT_SELECTION = "delegation-cert-selection"` |  |
| `static final String DELEGATION_ENABLE_SYSTEM_APP = "delegation-enable-system-app"` |  |
| `static final String DELEGATION_INSTALL_EXISTING_PACKAGE = "delegation-install-existing-package"` |  |
| `static final String DELEGATION_KEEP_UNINSTALLED_PACKAGES = "delegation-keep-uninstalled-packages"` |  |
| `static final String DELEGATION_NETWORK_LOGGING = "delegation-network-logging"` |  |
| `static final String DELEGATION_PACKAGE_ACCESS = "delegation-package-access"` |  |
| `static final String DELEGATION_PERMISSION_GRANT = "delegation-permission-grant"` |  |
| `static final int ENCRYPTION_STATUS_ACTIVATING = 2` |  |
| `static final int ENCRYPTION_STATUS_ACTIVE = 3` |  |
| `static final int ENCRYPTION_STATUS_ACTIVE_DEFAULT_KEY = 4` |  |
| `static final int ENCRYPTION_STATUS_ACTIVE_PER_USER = 5` |  |
| `static final int ENCRYPTION_STATUS_INACTIVE = 1` |  |
| `static final int ENCRYPTION_STATUS_UNSUPPORTED = 0` |  |
| `static final String EXTRA_ADD_EXPLANATION = "android.app.extra.ADD_EXPLANATION"` |  |
| `static final String EXTRA_DELEGATION_SCOPES = "android.app.extra.DELEGATION_SCOPES"` |  |
| `static final String EXTRA_DEVICE_ADMIN = "android.app.extra.DEVICE_ADMIN"` |  |
| `static final String EXTRA_PROVISIONING_ACCOUNT_TO_MIGRATE = "android.app.extra.PROVISIONING_ACCOUNT_TO_MIGRATE"` |  |
| `static final String EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE = "android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE"` |  |
| `static final String EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME = "android.app.extra.PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME"` |  |
| `static final String EXTRA_PROVISIONING_DEVICE_ADMIN_MINIMUM_VERSION_CODE = "android.app.extra.PROVISIONING_DEVICE_ADMIN_MINIMUM_VERSION_CODE"` |  |
| `static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM"` |  |
| `static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_COOKIE_HEADER = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_COOKIE_HEADER"` |  |
| `static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION"` |  |
| `static final String EXTRA_PROVISIONING_DEVICE_ADMIN_SIGNATURE_CHECKSUM = "android.app.extra.PROVISIONING_DEVICE_ADMIN_SIGNATURE_CHECKSUM"` |  |
| `static final String EXTRA_PROVISIONING_DISCLAIMERS = "android.app.extra.PROVISIONING_DISCLAIMERS"` |  |
| `static final String EXTRA_PROVISIONING_DISCLAIMER_CONTENT = "android.app.extra.PROVISIONING_DISCLAIMER_CONTENT"` |  |
| `static final String EXTRA_PROVISIONING_DISCLAIMER_HEADER = "android.app.extra.PROVISIONING_DISCLAIMER_HEADER"` |  |
| `static final String EXTRA_PROVISIONING_IMEI = "android.app.extra.PROVISIONING_IMEI"` |  |
| `static final String EXTRA_PROVISIONING_KEEP_ACCOUNT_ON_MIGRATION = "android.app.extra.PROVISIONING_KEEP_ACCOUNT_ON_MIGRATION"` |  |
| `static final String EXTRA_PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED = "android.app.extra.PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED"` |  |
| `static final String EXTRA_PROVISIONING_LOCALE = "android.app.extra.PROVISIONING_LOCALE"` |  |
| `static final String EXTRA_PROVISIONING_LOCAL_TIME = "android.app.extra.PROVISIONING_LOCAL_TIME"` |  |
| `static final String EXTRA_PROVISIONING_LOGO_URI = "android.app.extra.PROVISIONING_LOGO_URI"` |  |
| `static final String EXTRA_PROVISIONING_MAIN_COLOR = "android.app.extra.PROVISIONING_MAIN_COLOR"` |  |
| `static final String EXTRA_PROVISIONING_MODE = "android.app.extra.PROVISIONING_MODE"` |  |
| `static final String EXTRA_PROVISIONING_SERIAL_NUMBER = "android.app.extra.PROVISIONING_SERIAL_NUMBER"` |  |
| `static final String EXTRA_PROVISIONING_SKIP_EDUCATION_SCREENS = "android.app.extra.PROVISIONING_SKIP_EDUCATION_SCREENS"` |  |
| `static final String EXTRA_PROVISIONING_SKIP_ENCRYPTION = "android.app.extra.PROVISIONING_SKIP_ENCRYPTION"` |  |
| `static final String EXTRA_PROVISIONING_SKIP_USER_CONSENT = "android.app.extra.PROVISIONING_SKIP_USER_CONSENT"` |  |
| `static final String EXTRA_PROVISIONING_TIME_ZONE = "android.app.extra.PROVISIONING_TIME_ZONE"` |  |
| `static final String EXTRA_PROVISIONING_WIFI_ANONYMOUS_IDENTITY = "android.app.extra.PROVISIONING_WIFI_ANONYMOUS_IDENTITY"` |  |
| `static final String EXTRA_PROVISIONING_WIFI_CA_CERTIFICATE = "android.app.extra.PROVISIONING_WIFI_CA_CERTIFICATE"` |  |
| `static final String EXTRA_PROVISIONING_WIFI_DOMAIN = "android.app.extra.PROVISIONING_WIFI_DOMAIN"` |  |
| `static final String EXTRA_PROVISIONING_WIFI_EAP_METHOD = "android.app.extra.PROVISIONING_WIFI_EAP_METHOD"` |  |
| `static final String EXTRA_PROVISIONING_WIFI_HIDDEN = "android.app.extra.PROVISIONING_WIFI_HIDDEN"` |  |
| `static final String EXTRA_PROVISIONING_WIFI_IDENTITY = "android.app.extra.PROVISIONING_WIFI_IDENTITY"` |  |
| `static final String EXTRA_PROVISIONING_WIFI_PAC_URL = "android.app.extra.PROVISIONING_WIFI_PAC_URL"` |  |
| `static final String EXTRA_PROVISIONING_WIFI_PASSWORD = "android.app.extra.PROVISIONING_WIFI_PASSWORD"` |  |
| `static final String EXTRA_PROVISIONING_WIFI_PHASE2_AUTH = "android.app.extra.PROVISIONING_WIFI_PHASE2_AUTH"` |  |
| `static final String EXTRA_PROVISIONING_WIFI_PROXY_BYPASS = "android.app.extra.PROVISIONING_WIFI_PROXY_BYPASS"` |  |
| `static final String EXTRA_PROVISIONING_WIFI_PROXY_HOST = "android.app.extra.PROVISIONING_WIFI_PROXY_HOST"` |  |
| `static final String EXTRA_PROVISIONING_WIFI_PROXY_PORT = "android.app.extra.PROVISIONING_WIFI_PROXY_PORT"` |  |
| `static final String EXTRA_PROVISIONING_WIFI_SECURITY_TYPE = "android.app.extra.PROVISIONING_WIFI_SECURITY_TYPE"` |  |
| `static final String EXTRA_PROVISIONING_WIFI_SSID = "android.app.extra.PROVISIONING_WIFI_SSID"` |  |
| `static final String EXTRA_PROVISIONING_WIFI_USER_CERTIFICATE = "android.app.extra.PROVISIONING_WIFI_USER_CERTIFICATE"` |  |
| `static final int FLAG_EVICT_CREDENTIAL_ENCRYPTION_KEY = 1` |  |
| `static final int FLAG_MANAGED_CAN_ACCESS_PARENT = 2` |  |
| `static final int FLAG_PARENT_CAN_ACCESS_MANAGED = 1` |  |
| `static final int ID_TYPE_BASE_INFO = 1` |  |
| `static final int ID_TYPE_IMEI = 4` |  |
| `static final int ID_TYPE_INDIVIDUAL_ATTESTATION = 16` |  |
| `static final int ID_TYPE_MEID = 8` |  |
| `static final int ID_TYPE_SERIAL = 2` |  |
| `static final int INSTALLKEY_REQUEST_CREDENTIALS_ACCESS = 1` |  |
| `static final int INSTALLKEY_SET_USER_SELECTABLE = 2` |  |
| `static final int KEYGUARD_DISABLE_BIOMETRICS = 416` |  |
| `static final int KEYGUARD_DISABLE_FACE = 128` |  |
| `static final int KEYGUARD_DISABLE_FEATURES_ALL = 2147483647` |  |
| `static final int KEYGUARD_DISABLE_FEATURES_NONE = 0` |  |
| `static final int KEYGUARD_DISABLE_FINGERPRINT = 32` |  |
| `static final int KEYGUARD_DISABLE_IRIS = 256` |  |
| `static final int KEYGUARD_DISABLE_REMOTE_INPUT = 64` |  |
| `static final int KEYGUARD_DISABLE_SECURE_CAMERA = 2` |  |
| `static final int KEYGUARD_DISABLE_SECURE_NOTIFICATIONS = 4` |  |
| `static final int KEYGUARD_DISABLE_TRUST_AGENTS = 16` |  |
| `static final int KEYGUARD_DISABLE_UNREDACTED_NOTIFICATIONS = 8` |  |
| `static final int KEYGUARD_DISABLE_WIDGETS_ALL = 1` |  |
| `static final int LEAVE_ALL_SYSTEM_APPS_ENABLED = 16` |  |
| `static final int LOCK_TASK_FEATURE_BLOCK_ACTIVITY_START_IN_TASK = 64` |  |
| `static final int LOCK_TASK_FEATURE_GLOBAL_ACTIONS = 16` |  |
| `static final int LOCK_TASK_FEATURE_HOME = 4` |  |
| `static final int LOCK_TASK_FEATURE_KEYGUARD = 32` |  |
| `static final int LOCK_TASK_FEATURE_NONE = 0` |  |
| `static final int LOCK_TASK_FEATURE_NOTIFICATIONS = 2` |  |
| `static final int LOCK_TASK_FEATURE_OVERVIEW = 8` |  |
| `static final int LOCK_TASK_FEATURE_SYSTEM_INFO = 1` |  |
| `static final int MAKE_USER_EPHEMERAL = 2` |  |
| `static final String MIME_TYPE_PROVISIONING_NFC = "application/com.android.managedprovisioning"` |  |
| `static final int PASSWORD_COMPLEXITY_HIGH = 327680` |  |
| `static final int PASSWORD_COMPLEXITY_LOW = 65536` |  |
| `static final int PASSWORD_COMPLEXITY_MEDIUM = 196608` |  |
| `static final int PASSWORD_COMPLEXITY_NONE = 0` |  |
| `static final int PASSWORD_QUALITY_ALPHABETIC = 262144` |  |
| `static final int PASSWORD_QUALITY_ALPHANUMERIC = 327680` |  |
| `static final int PASSWORD_QUALITY_BIOMETRIC_WEAK = 32768` |  |
| `static final int PASSWORD_QUALITY_COMPLEX = 393216` |  |
| `static final int PASSWORD_QUALITY_NUMERIC = 131072` |  |
| `static final int PASSWORD_QUALITY_NUMERIC_COMPLEX = 196608` |  |
| `static final int PASSWORD_QUALITY_SOMETHING = 65536` |  |
| `static final int PASSWORD_QUALITY_UNSPECIFIED = 0` |  |
| `static final int PERMISSION_GRANT_STATE_DEFAULT = 0` |  |
| `static final int PERMISSION_GRANT_STATE_DENIED = 2` |  |
| `static final int PERMISSION_GRANT_STATE_GRANTED = 1` |  |
| `static final int PERMISSION_POLICY_AUTO_DENY = 2` |  |
| `static final int PERMISSION_POLICY_AUTO_GRANT = 1` |  |
| `static final int PERMISSION_POLICY_PROMPT = 0` |  |
| `static final int PERSONAL_APPS_NOT_SUSPENDED = 0` |  |
| `static final int PERSONAL_APPS_SUSPENDED_EXPLICITLY = 1` |  |
| `static final int PERSONAL_APPS_SUSPENDED_PROFILE_TIMEOUT = 2` |  |
| `static final String POLICY_DISABLE_CAMERA = "policy_disable_camera"` |  |
| `static final String POLICY_DISABLE_SCREEN_CAPTURE = "policy_disable_screen_capture"` |  |
| `static final int PRIVATE_DNS_MODE_OFF = 1` |  |
| `static final int PRIVATE_DNS_MODE_OPPORTUNISTIC = 2` |  |
| `static final int PRIVATE_DNS_MODE_PROVIDER_HOSTNAME = 3` |  |
| `static final int PRIVATE_DNS_MODE_UNKNOWN = 0` |  |
| `static final int PRIVATE_DNS_SET_ERROR_FAILURE_SETTING = 2` |  |
| `static final int PRIVATE_DNS_SET_ERROR_HOST_NOT_SERVING = 1` |  |
| `static final int PRIVATE_DNS_SET_NO_ERROR = 0` |  |
| `static final int PROVISIONING_MODE_FULLY_MANAGED_DEVICE = 1` |  |
| `static final int PROVISIONING_MODE_MANAGED_PROFILE = 2` |  |
| `static final int RESET_PASSWORD_DO_NOT_ASK_CREDENTIALS_ON_BOOT = 2` |  |
| `static final int RESET_PASSWORD_REQUIRE_ENTRY = 1` |  |
| `static final int SKIP_SETUP_WIZARD = 1` |  |
| `static final int WIPE_EUICC = 4` |  |
| `static final int WIPE_EXTERNAL_STORAGE = 1` |  |
| `static final int WIPE_RESET_PROTECTION_DATA = 2` |  |
| `static final int WIPE_SILENTLY = 8` |  |
| `static final int UPDATE_ERROR_BATTERY_LOW = 5` |  |
| `static final int UPDATE_ERROR_FILE_NOT_FOUND = 4` |  |
| `static final int UPDATE_ERROR_INCORRECT_OS_VERSION = 2` |  |
| `static final int UPDATE_ERROR_UNKNOWN = 1` |  |
| `static final int UPDATE_ERROR_UPDATE_FILE_INVALID = 3` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void addCrossProfileIntentFilter(@NonNull android.content.ComponentName, android.content.IntentFilter, int)` |  |
| `boolean addCrossProfileWidgetProvider(@NonNull android.content.ComponentName, String)` |  |
| `int addOverrideApn(@NonNull android.content.ComponentName, @NonNull android.telephony.data.ApnSetting)` |  |
| `void addPersistentPreferredActivity(@NonNull android.content.ComponentName, android.content.IntentFilter, @NonNull android.content.ComponentName)` |  |
| `void addUserRestriction(@NonNull android.content.ComponentName, String)` |  |
| `boolean bindDeviceAdminServiceAsUser(@NonNull android.content.ComponentName, android.content.Intent, @NonNull android.content.ServiceConnection, int, @NonNull android.os.UserHandle)` |  |
| `void clearApplicationUserData(@NonNull android.content.ComponentName, @NonNull String, @NonNull java.util.concurrent.Executor, @NonNull android.app.admin.DevicePolicyManager.OnClearApplicationUserDataListener)` |  |
| `void clearCrossProfileIntentFilters(@NonNull android.content.ComponentName)` |  |
| `void clearPackagePersistentPreferredActivities(@NonNull android.content.ComponentName, String)` |  |
| `boolean clearResetPasswordToken(android.content.ComponentName)` |  |
| `void clearUserRestriction(@NonNull android.content.ComponentName, String)` |  |
| `android.content.Intent createAdminSupportIntent(@NonNull String)` |  |
| `void enableSystemApp(@NonNull android.content.ComponentName, String)` |  |
| `int enableSystemApp(@NonNull android.content.ComponentName, android.content.Intent)` |  |
| `android.security.AttestedKeyPair generateKeyPair(@Nullable android.content.ComponentName, @NonNull String, @NonNull android.security.keystore.KeyGenParameterSpec, int)` |  |
| `boolean getAutoTimeEnabled(@NonNull android.content.ComponentName)` |  |
| `boolean getAutoTimeZoneEnabled(@NonNull android.content.ComponentName)` |  |
| `boolean getBluetoothContactSharingDisabled(@NonNull android.content.ComponentName)` |  |
| `boolean getCameraDisabled(@Nullable android.content.ComponentName)` |  |
| `boolean getCrossProfileCallerIdDisabled(@NonNull android.content.ComponentName)` |  |
| `boolean getCrossProfileContactsSearchDisabled(@NonNull android.content.ComponentName)` |  |
| `int getCurrentFailedPasswordAttempts()` |  |
| `CharSequence getDeviceOwnerLockScreenInfo()` |  |
| `CharSequence getEndUserSessionMessage(@NonNull android.content.ComponentName)` |  |
| `int getGlobalPrivateDnsMode(@NonNull android.content.ComponentName)` |  |
| `int getKeyguardDisabledFeatures(@Nullable android.content.ComponentName)` |  |
| `int getLockTaskFeatures(@NonNull android.content.ComponentName)` |  |
| `long getManagedProfileMaximumTimeOff(@NonNull android.content.ComponentName)` |  |
| `int getMaximumFailedPasswordsForWipe(@Nullable android.content.ComponentName)` |  |
| `long getMaximumTimeToLock(@Nullable android.content.ComponentName)` |  |
| `java.util.List<android.telephony.data.ApnSetting> getOverrideApns(@NonNull android.content.ComponentName)` |  |
| `long getPasswordExpiration(@Nullable android.content.ComponentName)` |  |
| `long getPasswordExpirationTimeout(@Nullable android.content.ComponentName)` |  |
| `int getPasswordHistoryLength(@Nullable android.content.ComponentName)` |  |
| `int getPasswordMaximumLength(int)` |  |
| `int getPasswordMinimumLength(@Nullable android.content.ComponentName)` |  |
| `int getPasswordMinimumLetters(@Nullable android.content.ComponentName)` |  |
| `int getPasswordMinimumLowerCase(@Nullable android.content.ComponentName)` |  |
| `int getPasswordMinimumNonLetter(@Nullable android.content.ComponentName)` |  |
| `int getPasswordMinimumNumeric(@Nullable android.content.ComponentName)` |  |
| `int getPasswordMinimumSymbols(@Nullable android.content.ComponentName)` |  |
| `int getPasswordMinimumUpperCase(@Nullable android.content.ComponentName)` |  |
| `int getPasswordQuality(@Nullable android.content.ComponentName)` |  |
| `int getPermissionGrantState(@Nullable android.content.ComponentName, @NonNull String, @NonNull String)` |  |
| `int getPermissionPolicy(android.content.ComponentName)` |  |
| `int getPersonalAppsSuspendedReasons(@NonNull android.content.ComponentName)` |  |
| `long getRequiredStrongAuthTimeout(@Nullable android.content.ComponentName)` |  |
| `boolean getScreenCaptureDisabled(@Nullable android.content.ComponentName)` |  |
| `java.util.List<android.os.UserHandle> getSecondaryUsers(@NonNull android.content.ComponentName)` |  |
| `CharSequence getShortSupportMessage(@NonNull android.content.ComponentName)` |  |
| `CharSequence getStartUserSessionMessage(@NonNull android.content.ComponentName)` |  |
| `int getStorageEncryptionStatus()` |  |
| `boolean grantKeyPairToApp(@Nullable android.content.ComponentName, @NonNull String, @NonNull String)` |  |
| `boolean hasCaCertInstalled(@Nullable android.content.ComponentName, byte[])` |  |
| `boolean hasGrantedPolicy(@NonNull android.content.ComponentName, int)` |  |
| `boolean hasLockdownAdminConfiguredNetworks(@NonNull android.content.ComponentName)` |  |
| `boolean installCaCert(@Nullable android.content.ComponentName, byte[])` |  |
| `boolean installExistingPackage(@NonNull android.content.ComponentName, String)` |  |
| `boolean installKeyPair(@Nullable android.content.ComponentName, @NonNull java.security.PrivateKey, @NonNull java.security.cert.Certificate, @NonNull String)` |  |
| `boolean installKeyPair(@Nullable android.content.ComponentName, @NonNull java.security.PrivateKey, @NonNull java.security.cert.Certificate[], @NonNull String, boolean)` |  |
| `boolean installKeyPair(@Nullable android.content.ComponentName, @NonNull java.security.PrivateKey, @NonNull java.security.cert.Certificate[], @NonNull String, int)` |  |
| `void installSystemUpdate(@NonNull android.content.ComponentName, @NonNull android.net.Uri, @NonNull java.util.concurrent.Executor, @NonNull android.app.admin.DevicePolicyManager.InstallSystemUpdateCallback)` |  |
| `boolean isActivePasswordSufficient()` |  |
| `boolean isAdminActive(@NonNull android.content.ComponentName)` |  |
| `boolean isAffiliatedUser()` |  |
| `boolean isAlwaysOnVpnLockdownEnabled(@NonNull android.content.ComponentName)` |  |
| `boolean isApplicationHidden(@NonNull android.content.ComponentName, String)` |  |
| `boolean isBackupServiceEnabled(@NonNull android.content.ComponentName)` |  |
| `boolean isCommonCriteriaModeEnabled(@Nullable android.content.ComponentName)` |  |
| `boolean isDeviceIdAttestationSupported()` |  |
| `boolean isDeviceOwnerApp(String)` |  |
| `boolean isEphemeralUser(@NonNull android.content.ComponentName)` |  |
| `boolean isLockTaskPermitted(String)` |  |
| `boolean isLogoutEnabled()` |  |
| `boolean isManagedProfile(@NonNull android.content.ComponentName)` |  |
| `boolean isMasterVolumeMuted(@NonNull android.content.ComponentName)` |  |
| `boolean isNetworkLoggingEnabled(@Nullable android.content.ComponentName)` |  |
| `boolean isOrganizationOwnedDeviceWithManagedProfile()` |  |
| `boolean isOverrideApnEnabled(@NonNull android.content.ComponentName)` |  |
| `boolean isPackageSuspended(@NonNull android.content.ComponentName, String) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `boolean isProfileOwnerApp(String)` |  |
| `boolean isProvisioningAllowed(@NonNull String)` |  |
| `boolean isResetPasswordTokenActive(android.content.ComponentName)` |  |
| `boolean isSecurityLoggingEnabled(@Nullable android.content.ComponentName)` |  |
| `boolean isUninstallBlocked(@Nullable android.content.ComponentName, String)` |  |
| `boolean isUniqueDeviceAttestationSupported()` |  |
| `boolean isUsingUnifiedPassword(@NonNull android.content.ComponentName)` |  |
| `void lockNow()` |  |
| `void lockNow(int)` |  |
| `int logoutUser(@NonNull android.content.ComponentName)` |  |
| `void reboot(@NonNull android.content.ComponentName)` |  |
| `void removeActiveAdmin(@NonNull android.content.ComponentName)` |  |
| `boolean removeCrossProfileWidgetProvider(@NonNull android.content.ComponentName, String)` |  |
| `boolean removeKeyPair(@Nullable android.content.ComponentName, @NonNull String)` |  |
| `boolean removeOverrideApn(@NonNull android.content.ComponentName, int)` |  |
| `boolean removeUser(@NonNull android.content.ComponentName, @NonNull android.os.UserHandle)` |  |
| `boolean requestBugreport(@NonNull android.content.ComponentName)` |  |
| `boolean resetPasswordWithToken(@NonNull android.content.ComponentName, String, byte[], int)` |  |
| `boolean revokeKeyPairFromApp(@Nullable android.content.ComponentName, @NonNull String, @NonNull String)` |  |
| `void setAccountManagementDisabled(@NonNull android.content.ComponentName, String, boolean)` |  |
| `void setAffiliationIds(@NonNull android.content.ComponentName, @NonNull java.util.Set<java.lang.String>)` |  |
| `void setAlwaysOnVpnPackage(@NonNull android.content.ComponentName, @Nullable String, boolean) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `void setAlwaysOnVpnPackage(@NonNull android.content.ComponentName, @Nullable String, boolean, @Nullable java.util.Set<java.lang.String>) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `boolean setApplicationHidden(@NonNull android.content.ComponentName, String, boolean)` |  |
| `void setAutoTimeEnabled(@NonNull android.content.ComponentName, boolean)` |  |
| `void setAutoTimeZoneEnabled(@NonNull android.content.ComponentName, boolean)` |  |
| `void setBackupServiceEnabled(@NonNull android.content.ComponentName, boolean)` |  |
| `void setBluetoothContactSharingDisabled(@NonNull android.content.ComponentName, boolean)` |  |
| `void setCameraDisabled(@NonNull android.content.ComponentName, boolean)` |  |
| `void setCommonCriteriaModeEnabled(@NonNull android.content.ComponentName, boolean)` |  |
| `void setConfiguredNetworksLockdownState(@NonNull android.content.ComponentName, boolean)` |  |
| `void setCrossProfileCalendarPackages(@NonNull android.content.ComponentName, @Nullable java.util.Set<java.lang.String>)` |  |
| `void setCrossProfileCallerIdDisabled(@NonNull android.content.ComponentName, boolean)` |  |
| `void setCrossProfileContactsSearchDisabled(@NonNull android.content.ComponentName, boolean)` |  |
| `void setCrossProfilePackages(@NonNull android.content.ComponentName, @NonNull java.util.Set<java.lang.String>)` |  |
| `void setDefaultSmsApplication(@NonNull android.content.ComponentName, @NonNull String)` |  |
| `void setDelegatedScopes(@NonNull android.content.ComponentName, @NonNull String, @NonNull java.util.List<java.lang.String>)` |  |
| `void setDeviceOwnerLockScreenInfo(@NonNull android.content.ComponentName, CharSequence)` |  |
| `void setEndUserSessionMessage(@NonNull android.content.ComponentName, @Nullable CharSequence)` |  |
| `void setFactoryResetProtectionPolicy(@NonNull android.content.ComponentName, @Nullable android.app.admin.FactoryResetProtectionPolicy)` |  |
| `int setGlobalPrivateDnsModeOpportunistic(@NonNull android.content.ComponentName)` |  |
| `void setGlobalSetting(@NonNull android.content.ComponentName, String, String)` |  |
| `void setKeepUninstalledPackages(@Nullable android.content.ComponentName, @NonNull java.util.List<java.lang.String>)` |  |
| `boolean setKeyPairCertificate(@Nullable android.content.ComponentName, @NonNull String, @NonNull java.util.List<java.security.cert.Certificate>, boolean)` |  |
| `boolean setKeyguardDisabled(@NonNull android.content.ComponentName, boolean)` |  |
| `void setKeyguardDisabledFeatures(@NonNull android.content.ComponentName, int)` |  |
| `void setLocationEnabled(@NonNull android.content.ComponentName, boolean)` |  |
| `void setLockTaskFeatures(@NonNull android.content.ComponentName, int)` |  |
| `void setLockTaskPackages(@NonNull android.content.ComponentName, @NonNull String[]) throws java.lang.SecurityException` |  |
| `void setLogoutEnabled(@NonNull android.content.ComponentName, boolean)` |  |
| `void setLongSupportMessage(@NonNull android.content.ComponentName, @Nullable CharSequence)` |  |
| `void setManagedProfileMaximumTimeOff(@NonNull android.content.ComponentName, long)` |  |
| `void setMasterVolumeMuted(@NonNull android.content.ComponentName, boolean)` |  |
| `void setMaximumFailedPasswordsForWipe(@NonNull android.content.ComponentName, int)` |  |
| `void setMaximumTimeToLock(@NonNull android.content.ComponentName, long)` |  |
| `void setNetworkLoggingEnabled(@Nullable android.content.ComponentName, boolean)` |  |
| `void setOrganizationColor(@NonNull android.content.ComponentName, int)` |  |
| `void setOrganizationName(@NonNull android.content.ComponentName, @Nullable CharSequence)` |  |
| `void setOverrideApnsEnabled(@NonNull android.content.ComponentName, boolean)` |  |
| `void setPasswordExpirationTimeout(@NonNull android.content.ComponentName, long)` |  |
| `void setPasswordHistoryLength(@NonNull android.content.ComponentName, int)` |  |
| `void setPasswordMinimumLength(@NonNull android.content.ComponentName, int)` |  |
| `void setPasswordMinimumLetters(@NonNull android.content.ComponentName, int)` |  |
| `void setPasswordMinimumLowerCase(@NonNull android.content.ComponentName, int)` |  |
| `void setPasswordMinimumNonLetter(@NonNull android.content.ComponentName, int)` |  |
| `void setPasswordMinimumNumeric(@NonNull android.content.ComponentName, int)` |  |
| `void setPasswordMinimumSymbols(@NonNull android.content.ComponentName, int)` |  |
| `void setPasswordMinimumUpperCase(@NonNull android.content.ComponentName, int)` |  |
| `void setPasswordQuality(@NonNull android.content.ComponentName, int)` |  |
| `boolean setPermissionGrantState(@NonNull android.content.ComponentName, @NonNull String, @NonNull String, int)` |  |
| `void setPermissionPolicy(@NonNull android.content.ComponentName, int)` |  |
| `boolean setPermittedAccessibilityServices(@NonNull android.content.ComponentName, java.util.List<java.lang.String>)` |  |
| `boolean setPermittedCrossProfileNotificationListeners(@NonNull android.content.ComponentName, @Nullable java.util.List<java.lang.String>)` |  |
| `boolean setPermittedInputMethods(@NonNull android.content.ComponentName, java.util.List<java.lang.String>)` |  |
| `void setPersonalAppsSuspended(@NonNull android.content.ComponentName, boolean)` |  |
| `void setProfileEnabled(@NonNull android.content.ComponentName)` |  |
| `void setProfileName(@NonNull android.content.ComponentName, String)` |  |
| `void setRecommendedGlobalProxy(@NonNull android.content.ComponentName, @Nullable android.net.ProxyInfo)` |  |
| `void setRequiredStrongAuthTimeout(@NonNull android.content.ComponentName, long)` |  |
| `boolean setResetPasswordToken(android.content.ComponentName, byte[])` |  |
| `void setRestrictionsProvider(@NonNull android.content.ComponentName, @Nullable android.content.ComponentName)` |  |
| `void setScreenCaptureDisabled(@NonNull android.content.ComponentName, boolean)` |  |
| `void setSecureSetting(@NonNull android.content.ComponentName, String, String)` |  |
| `void setSecurityLoggingEnabled(@NonNull android.content.ComponentName, boolean)` |  |
| `void setShortSupportMessage(@NonNull android.content.ComponentName, @Nullable CharSequence)` |  |
| `void setStartUserSessionMessage(@NonNull android.content.ComponentName, @Nullable CharSequence)` |  |
| `boolean setStatusBarDisabled(@NonNull android.content.ComponentName, boolean)` |  |
| `void setSystemSetting(@NonNull android.content.ComponentName, @NonNull String, String)` |  |
| `void setSystemUpdatePolicy(@NonNull android.content.ComponentName, android.app.admin.SystemUpdatePolicy)` |  |
| `boolean setTime(@NonNull android.content.ComponentName, long)` |  |
| `boolean setTimeZone(@NonNull android.content.ComponentName, String)` |  |
| `void setTrustAgentConfiguration(@NonNull android.content.ComponentName, @NonNull android.content.ComponentName, android.os.PersistableBundle)` |  |
| `void setUninstallBlocked(@Nullable android.content.ComponentName, String, boolean)` |  |
| `void setUserControlDisabledPackages(@NonNull android.content.ComponentName, @NonNull java.util.List<java.lang.String>)` |  |
| `void setUserIcon(@NonNull android.content.ComponentName, android.graphics.Bitmap)` |  |
| `int startUserInBackground(@NonNull android.content.ComponentName, @NonNull android.os.UserHandle)` |  |
| `int stopUser(@NonNull android.content.ComponentName, @NonNull android.os.UserHandle)` |  |
| `boolean switchUser(@NonNull android.content.ComponentName, @Nullable android.os.UserHandle)` |  |
| `void transferOwnership(@NonNull android.content.ComponentName, @NonNull android.content.ComponentName, @Nullable android.os.PersistableBundle)` |  |
| `void uninstallAllUserCaCerts(@Nullable android.content.ComponentName)` |  |
| `void uninstallCaCert(@Nullable android.content.ComponentName, byte[])` |  |
| `boolean updateOverrideApn(@NonNull android.content.ComponentName, int, @NonNull android.telephony.data.ApnSetting)` |  |
| `void wipeData(int)` |  |
| `void wipeData(int, @NonNull CharSequence)` |  |
| `void onInstallUpdateError(int, @NonNull String)` |  |

---

### `interface static DevicePolicyManager.OnClearApplicationUserDataListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onApplicationUserDataCleared(String, boolean)` |  |

---

### `class final DnsEvent`

- **继承：** `android.app.admin.NetworkEvent`
- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `String getHostname()` |  |
| `java.util.List<java.net.InetAddress> getInetAddresses()` |  |
| `int getTotalResolvedAddressCount()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final FactoryResetProtectionPolicy`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean isFactoryResetProtectionEnabled()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, @Nullable int)` |  |

---

### `class static FactoryResetProtectionPolicy.Builder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `FactoryResetProtectionPolicy.Builder()` |  |

---

### `class FreezePeriod`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `FreezePeriod(java.time.MonthDay, java.time.MonthDay)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `java.time.MonthDay getEnd()` |  |
| `java.time.MonthDay getStart()` |  |

---

### `class abstract NetworkEvent`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getId()` |  |
| `String getPackageName()` |  |
| `long getTimestamp()` |  |

---

### `class SecurityLog`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SecurityLog()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int LEVEL_ERROR = 3` |  |
| `static final int LEVEL_INFO = 1` |  |
| `static final int LEVEL_WARNING = 2` |  |
| `static final int TAG_ADB_SHELL_CMD = 210002` |  |
| `static final int TAG_ADB_SHELL_INTERACTIVE = 210001` |  |
| `static final int TAG_APP_PROCESS_START = 210005` |  |
| `static final int TAG_CAMERA_POLICY_SET = 210034` |  |
| `static final int TAG_CERT_AUTHORITY_INSTALLED = 210029` |  |
| `static final int TAG_CERT_AUTHORITY_REMOVED = 210030` |  |
| `static final int TAG_CERT_VALIDATION_FAILURE = 210033` |  |
| `static final int TAG_CRYPTO_SELF_TEST_COMPLETED = 210031` |  |
| `static final int TAG_KEYGUARD_DISABLED_FEATURES_SET = 210021` |  |
| `static final int TAG_KEYGUARD_DISMISSED = 210006` |  |
| `static final int TAG_KEYGUARD_DISMISS_AUTH_ATTEMPT = 210007` |  |
| `static final int TAG_KEYGUARD_SECURED = 210008` |  |
| `static final int TAG_KEY_DESTRUCTION = 210026` |  |
| `static final int TAG_KEY_GENERATED = 210024` |  |
| `static final int TAG_KEY_IMPORT = 210025` |  |
| `static final int TAG_KEY_INTEGRITY_VIOLATION = 210032` |  |
| `static final int TAG_LOGGING_STARTED = 210011` |  |
| `static final int TAG_LOGGING_STOPPED = 210012` |  |
| `static final int TAG_LOG_BUFFER_SIZE_CRITICAL = 210015` |  |
| `static final int TAG_MAX_PASSWORD_ATTEMPTS_SET = 210020` |  |
| `static final int TAG_MAX_SCREEN_LOCK_TIMEOUT_SET = 210019` |  |
| `static final int TAG_MEDIA_MOUNT = 210013` |  |
| `static final int TAG_MEDIA_UNMOUNT = 210014` |  |
| `static final int TAG_OS_SHUTDOWN = 210010` |  |
| `static final int TAG_OS_STARTUP = 210009` |  |
| `static final int TAG_PASSWORD_COMPLEXITY_SET = 210017` |  |
| `static final int TAG_PASSWORD_EXPIRATION_SET = 210016` |  |
| `static final int TAG_PASSWORD_HISTORY_LENGTH_SET = 210018` |  |
| `static final int TAG_REMOTE_LOCK = 210022` |  |
| `static final int TAG_SYNC_RECV_FILE = 210003` |  |
| `static final int TAG_SYNC_SEND_FILE = 210004` |  |
| `static final int TAG_USER_RESTRICTION_ADDED = 210027` |  |
| `static final int TAG_USER_RESTRICTION_REMOVED = 210028` |  |
| `static final int TAG_WIPE_FAILURE = 210023` |  |

---

### `class static final SecurityLog.SecurityEvent`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `Object getData()` |  |
| `long getId()` |  |
| `int getLogLevel()` |  |
| `int getTag()` |  |
| `long getTimeNanos()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final SystemUpdateInfo`

- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int SECURITY_PATCH_STATE_FALSE = 1` |  |
| `static final int SECURITY_PATCH_STATE_TRUE = 2` |  |
| `static final int SECURITY_PATCH_STATE_UNKNOWN = 0` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getReceivedTime()` |  |
| `int getSecurityPatchState()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final SystemUpdatePolicy`

- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int TYPE_INSTALL_AUTOMATIC = 1` |  |
| `static final int TYPE_INSTALL_WINDOWED = 2` |  |
| `static final int TYPE_POSTPONE = 3` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `static android.app.admin.SystemUpdatePolicy createAutomaticInstallPolicy()` |  |
| `static android.app.admin.SystemUpdatePolicy createPostponeInstallPolicy()` |  |
| `static android.app.admin.SystemUpdatePolicy createWindowedInstallPolicy(int, int)` |  |
| `int describeContents()` |  |
| `java.util.List<android.app.admin.FreezePeriod> getFreezePeriods()` |  |
| `int getInstallWindowEnd()` |  |
| `int getInstallWindowStart()` |  |
| `int getPolicyType()` |  |
| `android.app.admin.SystemUpdatePolicy setFreezePeriods(java.util.List<android.app.admin.FreezePeriod>)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final SystemUpdatePolicy.ValidationFailedException`

- **继承：** `java.lang.IllegalArgumentException`
- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int ERROR_COMBINED_FREEZE_PERIOD_TOO_CLOSE = 6` |  |
| `static final int ERROR_COMBINED_FREEZE_PERIOD_TOO_LONG = 5` |  |
| `static final int ERROR_DUPLICATE_OR_OVERLAP = 2` |  |
| `static final int ERROR_NEW_FREEZE_PERIOD_TOO_CLOSE = 4` |  |
| `static final int ERROR_NEW_FREEZE_PERIOD_TOO_LONG = 3` |  |
| `static final int ERROR_UNKNOWN = 1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getErrorCode()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## 包： `android.app.assist`

### `class AssistContent`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AssistContent()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.content.ClipData getClipData()` |  |
| `android.os.Bundle getExtras()` |  |
| `android.content.Intent getIntent()` |  |
| `String getStructuredData()` |  |
| `android.net.Uri getWebUri()` |  |
| `boolean isAppProvidedIntent()` |  |
| `boolean isAppProvidedWebUri()` |  |
| `void setClipData(android.content.ClipData)` |  |
| `void setIntent(android.content.Intent)` |  |
| `void setStructuredData(String)` |  |
| `void setWebUri(android.net.Uri)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class AssistStructure`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AssistStructure()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getAcquisitionEndTime()` |  |
| `long getAcquisitionStartTime()` |  |
| `android.content.ComponentName getActivityComponent()` |  |
| `android.app.assist.AssistStructure.WindowNode getWindowNodeAt(int)` |  |
| `int getWindowNodeCount()` |  |
| `boolean isHomeActivity()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static AssistStructure.ViewNode`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int TEXT_COLOR_UNDEFINED = 1` |  |
| `static final int TEXT_STYLE_BOLD = 1` |  |
| `static final int TEXT_STYLE_ITALIC = 2` |  |
| `static final int TEXT_STYLE_STRIKE_THRU = 8` |  |
| `static final int TEXT_STYLE_UNDERLINE = 4` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `float getAlpha()` |  |
| `int getAutofillType()` |  |
| `android.app.assist.AssistStructure.ViewNode getChildAt(int)` |  |
| `int getChildCount()` |  |
| `float getElevation()` |  |
| `int getHeight()` |  |
| `int getId()` |  |
| `int getImportantForAutofill()` |  |
| `int getInputType()` |  |
| `int getLeft()` |  |
| `int getMaxTextEms()` |  |
| `int getMaxTextLength()` |  |
| `int getMinTextEms()` |  |
| `int getScrollX()` |  |
| `int getScrollY()` |  |
| `int getTextBackgroundColor()` |  |
| `int getTextColor()` |  |
| `int getTextSelectionEnd()` |  |
| `int getTextSelectionStart()` |  |
| `float getTextSize()` |  |
| `int getTextStyle()` |  |
| `int getTop()` |  |
| `android.graphics.Matrix getTransformation()` |  |
| `int getVisibility()` |  |
| `int getWidth()` |  |
| `boolean isAccessibilityFocused()` |  |
| `boolean isActivated()` |  |
| `boolean isAssistBlocked()` |  |
| `boolean isCheckable()` |  |
| `boolean isChecked()` |  |
| `boolean isClickable()` |  |
| `boolean isContextClickable()` |  |
| `boolean isEnabled()` |  |
| `boolean isFocusable()` |  |
| `boolean isFocused()` |  |
| `boolean isLongClickable()` |  |
| `boolean isOpaque()` |  |
| `boolean isSelected()` |  |

---

### `class static AssistStructure.WindowNode`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int getDisplayId()` |  |
| `int getHeight()` |  |
| `int getLeft()` |  |
| `android.app.assist.AssistStructure.ViewNode getRootViewNode()` |  |
| `CharSequence getTitle()` |  |
| `int getTop()` |  |
| `int getWidth()` |  |

---

## 包： `android.app.backup`

### `class abstract BackupAgent`

- **继承：** `android.content.ContextWrapper`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `BackupAgent()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int FLAG_CLIENT_SIDE_ENCRYPTION_ENABLED = 1` |  |
| `static final int FLAG_DEVICE_TO_DEVICE_TRANSFER = 2` |  |
| `static final int TYPE_DIRECTORY = 2` |  |
| `static final int TYPE_FILE = 1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `final void fullBackupFile(java.io.File, android.app.backup.FullBackupDataOutput)` |  |
| `abstract void onBackup(android.os.ParcelFileDescriptor, android.app.backup.BackupDataOutput, android.os.ParcelFileDescriptor) throws java.io.IOException` |  |
| `void onCreate()` |  |
| `void onDestroy()` |  |
| `void onFullBackup(android.app.backup.FullBackupDataOutput) throws java.io.IOException` |  |
| `void onQuotaExceeded(long, long)` |  |
| `abstract void onRestore(android.app.backup.BackupDataInput, int, android.os.ParcelFileDescriptor) throws java.io.IOException` |  |
| `void onRestore(android.app.backup.BackupDataInput, long, android.os.ParcelFileDescriptor) throws java.io.IOException` |  |
| `void onRestoreFile(android.os.ParcelFileDescriptor, long, java.io.File, int, long, long) throws java.io.IOException` |  |
| `void onRestoreFinished()` |  |

---

### `class BackupAgentHelper`

- **继承：** `android.app.backup.BackupAgent`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `BackupAgentHelper()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void addHelper(String, android.app.backup.BackupHelper)` |  |
| `void onBackup(android.os.ParcelFileDescriptor, android.app.backup.BackupDataOutput, android.os.ParcelFileDescriptor) throws java.io.IOException` |  |
| `void onRestore(android.app.backup.BackupDataInput, int, android.os.ParcelFileDescriptor) throws java.io.IOException` |  |

---

### `class BackupDataInput`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int getDataSize()` |  |
| `String getKey()` |  |
| `int readEntityData(byte[], int, int) throws java.io.IOException` |  |
| `boolean readNextHeader() throws java.io.IOException` |  |
| `void skipEntityData() throws java.io.IOException` |  |

---

### `class BackupDataInputStream`

- **继承：** `java.io.InputStream`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `String getKey()` |  |
| `int read() throws java.io.IOException` |  |
| `int size()` |  |

---

### `class BackupDataOutput`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `long getQuota()` |  |
| `int getTransportFlags()` |  |
| `int writeEntityData(byte[], int) throws java.io.IOException` |  |
| `int writeEntityHeader(String, int) throws java.io.IOException` |  |

---

### `interface BackupHelper`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void performBackup(android.os.ParcelFileDescriptor, android.app.backup.BackupDataOutput, android.os.ParcelFileDescriptor)` |  |
| `void restoreEntity(android.app.backup.BackupDataInputStream)` |  |
| `void writeNewStateDescription(android.os.ParcelFileDescriptor)` |  |

---

### `class BackupManager`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `BackupManager(android.content.Context)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void dataChanged()` |  |
| `static void dataChanged(String)` |  |

---

### `class FileBackupHelper`

- **实现：** `android.app.backup.BackupHelper`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `FileBackupHelper(android.content.Context, java.lang.String...)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void performBackup(android.os.ParcelFileDescriptor, android.app.backup.BackupDataOutput, android.os.ParcelFileDescriptor)` |  |
| `void restoreEntity(android.app.backup.BackupDataInputStream)` |  |
| `void writeNewStateDescription(android.os.ParcelFileDescriptor)` |  |

---

### `class FullBackupDataOutput`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `long getQuota()` |  |
| `int getTransportFlags()` |  |

---

### `class abstract RestoreObserver`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `RestoreObserver()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onUpdate(int, String)` |  |
| `void restoreFinished(int)` |  |
| `void restoreStarting(int)` |  |

---

### `class SharedPreferencesBackupHelper`

- **实现：** `android.app.backup.BackupHelper`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SharedPreferencesBackupHelper(android.content.Context, java.lang.String...)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void performBackup(android.os.ParcelFileDescriptor, android.app.backup.BackupDataOutput, android.os.ParcelFileDescriptor)` |  |
| `void restoreEntity(android.app.backup.BackupDataInputStream)` |  |
| `void writeNewStateDescription(android.os.ParcelFileDescriptor)` |  |

---

## 包： `android.app.blob`

### `class final BlobHandle`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getExpiryTimeMillis()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class BlobStoreManager`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void abandonSession(@IntRange(from=1) long) throws java.io.IOException` |  |
| `void acquireLease(@NonNull android.app.blob.BlobHandle, @IdRes int, long) throws java.io.IOException` |  |
| `void acquireLease(@NonNull android.app.blob.BlobHandle, @NonNull CharSequence, long) throws java.io.IOException` |  |
| `void acquireLease(@NonNull android.app.blob.BlobHandle, @IdRes int) throws java.io.IOException` |  |
| `void acquireLease(@NonNull android.app.blob.BlobHandle, @NonNull CharSequence) throws java.io.IOException` |  |
| `void releaseLease(@NonNull android.app.blob.BlobHandle) throws java.io.IOException` |  |

---

### `class static BlobStoreManager.Session`

- **实现：** `java.io.Closeable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void abandon() throws java.io.IOException` |  |
| `void allowPackageAccess(@NonNull String, @NonNull byte[]) throws java.io.IOException` |  |
| `void allowPublicAccess() throws java.io.IOException` |  |
| `void allowSameSignatureAccess() throws java.io.IOException` |  |
| `void close() throws java.io.IOException` |  |
| `void commit(@NonNull java.util.concurrent.Executor, @NonNull java.util.function.Consumer<java.lang.Integer>) throws java.io.IOException` |  |
| `long getSize() throws java.io.IOException` |  |
| `boolean isPackageAccessAllowed(@NonNull String, @NonNull byte[]) throws java.io.IOException` |  |
| `boolean isPublicAccessAllowed() throws java.io.IOException` |  |
| `boolean isSameSignatureAccessAllowed() throws java.io.IOException` |  |

---

## 包： `android.app.job`

### `class JobInfo`

- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int BACKOFF_POLICY_EXPONENTIAL = 1` |  |
| `static final int BACKOFF_POLICY_LINEAR = 0` |  |
| `static final long DEFAULT_INITIAL_BACKOFF_MILLIS = 30000L` |  |
| `static final long MAX_BACKOFF_DELAY_MILLIS = 18000000L` |  |
| `static final int NETWORK_BYTES_UNKNOWN = -1` |  |
| `static final int NETWORK_TYPE_ANY = 1` |  |
| `static final int NETWORK_TYPE_CELLULAR = 4` |  |
| `static final int NETWORK_TYPE_NONE = 0` |  |
| `static final int NETWORK_TYPE_NOT_ROAMING = 3` |  |
| `static final int NETWORK_TYPE_UNMETERED = 2` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getBackoffPolicy()` |  |
| `int getClipGrantFlags()` |  |
| `long getEstimatedNetworkDownloadBytes()` |  |
| `long getEstimatedNetworkUploadBytes()` |  |
| `long getFlexMillis()` |  |
| `int getId()` |  |
| `long getInitialBackoffMillis()` |  |
| `long getIntervalMillis()` |  |
| `long getMaxExecutionDelayMillis()` |  |
| `static final long getMinFlexMillis()` |  |
| `long getMinLatencyMillis()` |  |
| `static final long getMinPeriodMillis()` |  |
| `long getTriggerContentMaxDelay()` |  |
| `long getTriggerContentUpdateDelay()` |  |
| `boolean isImportantWhileForeground()` |  |
| `boolean isPeriodic()` |  |
| `boolean isPersisted()` |  |
| `boolean isPrefetch()` |  |
| `boolean isRequireBatteryNotLow()` |  |
| `boolean isRequireCharging()` |  |
| `boolean isRequireDeviceIdle()` |  |
| `boolean isRequireStorageNotLow()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final JobInfo.Builder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `JobInfo.Builder(int, @NonNull android.content.ComponentName)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.job.JobInfo.Builder addTriggerContentUri(@NonNull android.app.job.JobInfo.TriggerContentUri)` |  |
| `android.app.job.JobInfo build()` |  |
| `android.app.job.JobInfo.Builder setBackoffCriteria(long, int)` |  |
| `android.app.job.JobInfo.Builder setClipData(@Nullable android.content.ClipData, int)` |  |
| `android.app.job.JobInfo.Builder setEstimatedNetworkBytes(long, long)` |  |
| `android.app.job.JobInfo.Builder setExtras(@NonNull android.os.PersistableBundle)` |  |
| `android.app.job.JobInfo.Builder setImportantWhileForeground(boolean)` |  |
| `android.app.job.JobInfo.Builder setMinimumLatency(long)` |  |
| `android.app.job.JobInfo.Builder setOverrideDeadline(long)` |  |
| `android.app.job.JobInfo.Builder setPeriodic(long)` |  |
| `android.app.job.JobInfo.Builder setPeriodic(long, long)` |  |
| `android.app.job.JobInfo.Builder setPrefetch(boolean)` |  |
| `android.app.job.JobInfo.Builder setRequiredNetwork(@Nullable android.net.NetworkRequest)` |  |
| `android.app.job.JobInfo.Builder setRequiredNetworkType(int)` |  |
| `android.app.job.JobInfo.Builder setRequiresBatteryNotLow(boolean)` |  |
| `android.app.job.JobInfo.Builder setRequiresCharging(boolean)` |  |
| `android.app.job.JobInfo.Builder setRequiresDeviceIdle(boolean)` |  |
| `android.app.job.JobInfo.Builder setRequiresStorageNotLow(boolean)` |  |
| `android.app.job.JobInfo.Builder setTransientExtras(@NonNull android.os.Bundle)` |  |
| `android.app.job.JobInfo.Builder setTriggerContentMaxDelay(long)` |  |
| `android.app.job.JobInfo.Builder setTriggerContentUpdateDelay(long)` |  |

---

### `class static final JobInfo.TriggerContentUri`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `JobInfo.TriggerContentUri(@NonNull android.net.Uri, int)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int FLAG_NOTIFY_FOR_DESCENDANTS = 1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getFlags()` |  |
| `android.net.Uri getUri()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class JobParameters`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void completeWork(@NonNull android.app.job.JobWorkItem)` |  |
| `int describeContents()` |  |
| `int getClipGrantFlags()` |  |
| `int getJobId()` |  |
| `boolean isOverrideDeadlineExpired()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class abstract JobScheduler`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `JobScheduler()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int RESULT_FAILURE = 0` |  |
| `static final int RESULT_SUCCESS = 1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `abstract void cancel(int)` |  |
| `abstract void cancelAll()` |  |
| `abstract int enqueue(@NonNull android.app.job.JobInfo, @NonNull android.app.job.JobWorkItem)` |  |
| `abstract int schedule(@NonNull android.app.job.JobInfo)` |  |

---

### `class abstract JobService`

- **继承：** `android.app.Service`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `JobService()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String PERMISSION_BIND = "android.permission.BIND_JOB_SERVICE"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `final void jobFinished(android.app.job.JobParameters, boolean)` |  |
| `final android.os.IBinder onBind(android.content.Intent)` |  |
| `abstract boolean onStartJob(android.app.job.JobParameters)` |  |
| `abstract boolean onStopJob(android.app.job.JobParameters)` |  |

---

### `class abstract JobServiceEngine`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `JobServiceEngine(android.app.Service)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `final android.os.IBinder getBinder()` |  |
| `void jobFinished(android.app.job.JobParameters, boolean)` |  |
| `abstract boolean onStartJob(android.app.job.JobParameters)` |  |
| `abstract boolean onStopJob(android.app.job.JobParameters)` |  |

---

### `class final JobWorkItem`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `JobWorkItem(android.content.Intent)` |  |
| `JobWorkItem(android.content.Intent, long, long)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getDeliveryCount()` |  |
| `long getEstimatedNetworkDownloadBytes()` |  |
| `long getEstimatedNetworkUploadBytes()` |  |
| `android.content.Intent getIntent()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## 包： `android.app.role`

### `class final RoleManager`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ROLE_ASSISTANT = "android.app.role.ASSISTANT"` |  |
| `static final String ROLE_BROWSER = "android.app.role.BROWSER"` |  |
| `static final String ROLE_CALL_REDIRECTION = "android.app.role.CALL_REDIRECTION"` |  |
| `static final String ROLE_CALL_SCREENING = "android.app.role.CALL_SCREENING"` |  |
| `static final String ROLE_DIALER = "android.app.role.DIALER"` |  |
| `static final String ROLE_EMERGENCY = "android.app.role.EMERGENCY"` |  |
| `static final String ROLE_HOME = "android.app.role.HOME"` |  |
| `static final String ROLE_SMS = "android.app.role.SMS"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean isRoleAvailable(@NonNull String)` |  |
| `boolean isRoleHeld(@NonNull String)` |  |

---

## 包： `android.app.slice`

### `class final Slice`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Slice(android.os.Parcel)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String EXTRA_RANGE_VALUE = "android.app.slice.extra.RANGE_VALUE"` |  |
| `static final String EXTRA_TOGGLE_STATE = "android.app.slice.extra.TOGGLE_STATE"` |  |
| `static final String HINT_ACTIONS = "actions"` |  |
| `static final String HINT_ERROR = "error"` |  |
| `static final String HINT_HORIZONTAL = "horizontal"` |  |
| `static final String HINT_KEYWORDS = "keywords"` |  |
| `static final String HINT_LARGE = "large"` |  |
| `static final String HINT_LAST_UPDATED = "last_updated"` |  |
| `static final String HINT_LIST = "list"` |  |
| `static final String HINT_LIST_ITEM = "list_item"` |  |
| `static final String HINT_NO_TINT = "no_tint"` |  |
| `static final String HINT_PARTIAL = "partial"` |  |
| `static final String HINT_PERMISSION_REQUEST = "permission_request"` |  |
| `static final String HINT_SEE_MORE = "see_more"` |  |
| `static final String HINT_SELECTED = "selected"` |  |
| `static final String HINT_SHORTCUT = "shortcut"` |  |
| `static final String HINT_SUMMARY = "summary"` |  |
| `static final String HINT_TITLE = "title"` |  |
| `static final String HINT_TTL = "ttl"` |  |
| `static final String SUBTYPE_COLOR = "color"` |  |
| `static final String SUBTYPE_CONTENT_DESCRIPTION = "content_description"` |  |
| `static final String SUBTYPE_LAYOUT_DIRECTION = "layout_direction"` |  |
| `static final String SUBTYPE_MAX = "max"` |  |
| `static final String SUBTYPE_MESSAGE = "message"` |  |
| `static final String SUBTYPE_MILLIS = "millis"` |  |
| `static final String SUBTYPE_PRIORITY = "priority"` |  |
| `static final String SUBTYPE_RANGE = "range"` |  |
| `static final String SUBTYPE_SOURCE = "source"` |  |
| `static final String SUBTYPE_TOGGLE = "toggle"` |  |
| `static final String SUBTYPE_VALUE = "value"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `java.util.List<java.lang.String> getHints()` |  |
| `java.util.List<android.app.slice.SliceItem> getItems()` |  |
| `android.net.Uri getUri()` |  |
| `boolean isCallerNeeded()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static Slice.Builder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Slice.Builder(@NonNull android.net.Uri, android.app.slice.SliceSpec)` |  |
| `Slice.Builder(@NonNull android.app.slice.Slice.Builder)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.slice.Slice.Builder addAction(@NonNull android.app.PendingIntent, @NonNull android.app.slice.Slice, @Nullable String)` |  |
| `android.app.slice.Slice.Builder addBundle(android.os.Bundle, @Nullable String, java.util.List<java.lang.String>)` |  |
| `android.app.slice.Slice.Builder addHints(java.util.List<java.lang.String>)` |  |
| `android.app.slice.Slice.Builder addIcon(android.graphics.drawable.Icon, @Nullable String, java.util.List<java.lang.String>)` |  |
| `android.app.slice.Slice.Builder addInt(int, @Nullable String, java.util.List<java.lang.String>)` |  |
| `android.app.slice.Slice.Builder addLong(long, @Nullable String, java.util.List<java.lang.String>)` |  |
| `android.app.slice.Slice.Builder addRemoteInput(android.app.RemoteInput, @Nullable String, java.util.List<java.lang.String>)` |  |
| `android.app.slice.Slice.Builder addSubSlice(@NonNull android.app.slice.Slice, @Nullable String)` |  |
| `android.app.slice.Slice.Builder addText(CharSequence, @Nullable String, java.util.List<java.lang.String>)` |  |
| `android.app.slice.Slice build()` |  |
| `android.app.slice.Slice.Builder setCallerNeeded(boolean)` |  |

---

### `class final SliceItem`

- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String FORMAT_ACTION = "action"` |  |
| `static final String FORMAT_BUNDLE = "bundle"` |  |
| `static final String FORMAT_IMAGE = "image"` |  |
| `static final String FORMAT_INT = "int"` |  |
| `static final String FORMAT_LONG = "long"` |  |
| `static final String FORMAT_REMOTE_INPUT = "input"` |  |
| `static final String FORMAT_SLICE = "slice"` |  |
| `static final String FORMAT_TEXT = "text"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.app.PendingIntent getAction()` |  |
| `android.os.Bundle getBundle()` |  |
| `String getFormat()` |  |
| `android.graphics.drawable.Icon getIcon()` |  |
| `int getInt()` |  |
| `long getLong()` |  |
| `android.app.RemoteInput getRemoteInput()` |  |
| `android.app.slice.Slice getSlice()` |  |
| `String getSubType()` |  |
| `CharSequence getText()` |  |
| `boolean hasHint(String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class SliceManager`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String CATEGORY_SLICE = "android.app.slice.category.SLICE"` |  |
| `static final String SLICE_METADATA_KEY = "android.metadata.SLICE_URI"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int checkSlicePermission(@NonNull android.net.Uri, int, int)` |  |
| `void grantSlicePermission(@NonNull String, @NonNull android.net.Uri)` |  |
| `void pinSlice(@NonNull android.net.Uri, @NonNull java.util.Set<android.app.slice.SliceSpec>)` |  |
| `void revokeSlicePermission(@NonNull String, @NonNull android.net.Uri)` |  |
| `void unpinSlice(@NonNull android.net.Uri)` |  |

---

### `class SliceMetrics`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SliceMetrics(@NonNull android.content.Context, @NonNull android.net.Uri)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void logHidden()` |  |
| `void logTouch(int, @NonNull android.net.Uri)` |  |
| `void logVisible()` |  |

---

### `class abstract SliceProvider`

- **继承：** `android.content.ContentProvider`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SliceProvider(@NonNull java.lang.String...)` |  |
| `SliceProvider()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String SLICE_TYPE = "vnd.android.slice"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `final int delete(android.net.Uri, String, String[])` |  |
| `final String getType(android.net.Uri)` |  |
| `final android.net.Uri insert(android.net.Uri, android.content.ContentValues)` |  |
| `android.app.slice.Slice onBindSlice(android.net.Uri, java.util.Set<android.app.slice.SliceSpec>)` |  |
| `void onSlicePinned(android.net.Uri)` |  |
| `void onSliceUnpinned(android.net.Uri)` |  |
| `final android.database.Cursor query(android.net.Uri, String[], String, String[], String)` |  |
| `final android.database.Cursor query(android.net.Uri, String[], String, String[], String, android.os.CancellationSignal)` |  |
| `final android.database.Cursor query(android.net.Uri, String[], android.os.Bundle, android.os.CancellationSignal)` |  |
| `final int update(android.net.Uri, android.content.ContentValues, String, String[])` |  |

---

### `class final SliceSpec`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SliceSpec(@NonNull String, int)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean canRender(@NonNull android.app.slice.SliceSpec)` |  |
| `int describeContents()` |  |
| `int getRevision()` |  |
| `String getType()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## 包： `android.app.usage`

### `class final ConfigurationStats`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ConfigurationStats(android.app.usage.ConfigurationStats)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getActivationCount()` |  |
| `android.content.res.Configuration getConfiguration()` |  |
| `long getFirstTimeStamp()` |  |
| `long getLastTimeActive()` |  |
| `long getLastTimeStamp()` |  |
| `long getTotalTimeActive()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final EventStats`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `EventStats(android.app.usage.EventStats)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void add(android.app.usage.EventStats)` |  |
| `int describeContents()` |  |
| `int getCount()` |  |
| `int getEventType()` |  |
| `long getFirstTimeStamp()` |  |
| `long getLastEventTime()` |  |
| `long getLastTimeStamp()` |  |
| `long getTotalTime()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final ExternalStorageStats`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getAppBytes()` |  |
| `long getAudioBytes()` |  |
| `long getImageBytes()` |  |
| `long getTotalBytes()` |  |
| `long getVideoBytes()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final NetworkStats`

- **实现：** `java.lang.AutoCloseable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void close()` |  |
| `boolean getNextBucket(android.app.usage.NetworkStats.Bucket)` |  |
| `boolean hasNextBucket()` |  |

---

### `class static NetworkStats.Bucket`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `NetworkStats.Bucket()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int DEFAULT_NETWORK_ALL = -1` |  |
| `static final int DEFAULT_NETWORK_NO = 1` |  |
| `static final int DEFAULT_NETWORK_YES = 2` |  |
| `static final int METERED_ALL = -1` |  |
| `static final int METERED_NO = 1` |  |
| `static final int METERED_YES = 2` |  |
| `static final int ROAMING_ALL = -1` |  |
| `static final int ROAMING_NO = 1` |  |
| `static final int ROAMING_YES = 2` |  |
| `static final int STATE_ALL = -1` |  |
| `static final int STATE_DEFAULT = 1` |  |
| `static final int STATE_FOREGROUND = 2` |  |
| `static final int TAG_NONE = 0` |  |
| `static final int UID_ALL = -1` |  |
| `static final int UID_REMOVED = -4` |  |
| `static final int UID_TETHERING = -5` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int getDefaultNetworkStatus()` |  |
| `long getEndTimeStamp()` |  |
| `int getMetered()` |  |
| `int getRoaming()` |  |
| `long getRxBytes()` |  |
| `long getRxPackets()` |  |
| `long getStartTimeStamp()` |  |
| `int getState()` |  |
| `int getTag()` |  |
| `long getTxBytes()` |  |
| `long getTxPackets()` |  |
| `int getUid()` |  |

---

### `class NetworkStatsManager`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `NetworkStatsManager.UsageCallback()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.app.usage.NetworkStats queryDetails(int, String, long, long) throws android.os.RemoteException, java.lang.SecurityException` |  |
| `android.app.usage.NetworkStats queryDetailsForUid(int, String, long, long, int) throws java.lang.SecurityException` |  |
| `android.app.usage.NetworkStats queryDetailsForUidTag(int, String, long, long, int, int) throws java.lang.SecurityException` |  |
| `android.app.usage.NetworkStats queryDetailsForUidTagState(int, String, long, long, int, int, int) throws java.lang.SecurityException` |  |
| `android.app.usage.NetworkStats querySummary(int, String, long, long) throws android.os.RemoteException, java.lang.SecurityException` |  |
| `android.app.usage.NetworkStats.Bucket querySummaryForDevice(int, String, long, long) throws android.os.RemoteException, java.lang.SecurityException` |  |
| `android.app.usage.NetworkStats.Bucket querySummaryForUser(int, String, long, long) throws android.os.RemoteException, java.lang.SecurityException` |  |
| `void registerUsageCallback(int, String, long, android.app.usage.NetworkStatsManager.UsageCallback)` |  |
| `void registerUsageCallback(int, String, long, android.app.usage.NetworkStatsManager.UsageCallback, @Nullable android.os.Handler)` |  |
| `void unregisterUsageCallback(android.app.usage.NetworkStatsManager.UsageCallback)` |  |
| `abstract void onThresholdReached(int, String)` |  |

---

### `class final StorageStats`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getAppBytes()` |  |
| `long getCacheBytes()` |  |
| `long getDataBytes()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class StorageStatsManager`


---

### `class final UsageEvents`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean getNextEvent(android.app.usage.UsageEvents.Event)` |  |
| `boolean hasNextEvent()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final UsageEvents.Event`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `UsageEvents.Event()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int ACTIVITY_PAUSED = 2` |  |
| `static final int ACTIVITY_RESUMED = 1` |  |
| `static final int ACTIVITY_STOPPED = 23` |  |
| `static final int CONFIGURATION_CHANGE = 5` |  |
| `static final int DEVICE_SHUTDOWN = 26` |  |
| `static final int DEVICE_STARTUP = 27` |  |
| `static final int FOREGROUND_SERVICE_START = 19` |  |
| `static final int FOREGROUND_SERVICE_STOP = 20` |  |
| `static final int KEYGUARD_HIDDEN = 18` |  |
| `static final int KEYGUARD_SHOWN = 17` |  |
| `static final int NONE = 0` |  |
| `static final int SCREEN_INTERACTIVE = 15` |  |
| `static final int SCREEN_NON_INTERACTIVE = 16` |  |
| `static final int SHORTCUT_INVOCATION = 8` |  |
| `static final int STANDBY_BUCKET_CHANGED = 11` |  |
| `static final int USER_INTERACTION = 7` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int getAppStandbyBucket()` |  |
| `String getClassName()` |  |
| `android.content.res.Configuration getConfiguration()` |  |
| `int getEventType()` |  |
| `String getPackageName()` |  |
| `String getShortcutId()` |  |
| `long getTimeStamp()` |  |

---

### `class final UsageStats`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `UsageStats(android.app.usage.UsageStats)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void add(android.app.usage.UsageStats)` |  |
| `int describeContents()` |  |
| `long getFirstTimeStamp()` |  |
| `long getLastTimeForegroundServiceUsed()` |  |
| `long getLastTimeStamp()` |  |
| `long getLastTimeUsed()` |  |
| `long getLastTimeVisible()` |  |
| `String getPackageName()` |  |
| `long getTotalTimeForegroundServiceUsed()` |  |
| `long getTotalTimeInForeground()` |  |
| `long getTotalTimeVisible()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final UsageStatsManager`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int INTERVAL_BEST = 4` |  |
| `static final int INTERVAL_DAILY = 0` |  |
| `static final int INTERVAL_MONTHLY = 2` |  |
| `static final int INTERVAL_WEEKLY = 1` |  |
| `static final int INTERVAL_YEARLY = 3` |  |
| `static final int STANDBY_BUCKET_ACTIVE = 10` |  |
| `static final int STANDBY_BUCKET_FREQUENT = 30` |  |
| `static final int STANDBY_BUCKET_RARE = 40` |  |
| `static final int STANDBY_BUCKET_RESTRICTED = 45` |  |
| `static final int STANDBY_BUCKET_WORKING_SET = 20` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int getAppStandbyBucket()` |  |
| `boolean isAppInactive(String)` |  |
| `java.util.Map<java.lang.String,android.app.usage.UsageStats> queryAndAggregateUsageStats(long, long)` |  |
| `java.util.List<android.app.usage.ConfigurationStats> queryConfigurations(int, long, long)` |  |
| `java.util.List<android.app.usage.EventStats> queryEventStats(int, long, long)` |  |
| `android.app.usage.UsageEvents queryEvents(long, long)` |  |
| `android.app.usage.UsageEvents queryEventsForSelf(long, long)` |  |
| `java.util.List<android.app.usage.UsageStats> queryUsageStats(int, long, long)` |  |

---

## 包： `android.appwidget`

### `class AppWidgetHost`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AppWidgetHost(android.content.Context, int)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int allocateAppWidgetId()` |  |
| `void clearViews()` |  |
| `final android.appwidget.AppWidgetHostView createView(android.content.Context, int, android.appwidget.AppWidgetProviderInfo)` |  |
| `static void deleteAllHosts()` |  |
| `void deleteAppWidgetId(int)` |  |
| `void deleteHost()` |  |
| `int[] getAppWidgetIds()` |  |
| `void onAppWidgetRemoved(int)` |  |
| `android.appwidget.AppWidgetHostView onCreateView(android.content.Context, int, android.appwidget.AppWidgetProviderInfo)` |  |
| `void onProviderChanged(int, android.appwidget.AppWidgetProviderInfo)` |  |
| `void onProvidersChanged()` |  |
| `final void startAppWidgetConfigureActivityForResult(@NonNull android.app.Activity, int, int, int, @Nullable android.os.Bundle)` |  |
| `void startListening()` |  |
| `void stopListening()` |  |

---

### `class AppWidgetHostView`

- **继承：** `android.widget.FrameLayout`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AppWidgetHostView(android.content.Context)` |  |
| `AppWidgetHostView(android.content.Context, int, int)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int getAppWidgetId()` |  |
| `android.appwidget.AppWidgetProviderInfo getAppWidgetInfo()` |  |
| `static android.graphics.Rect getDefaultPaddingForWidget(android.content.Context, android.content.ComponentName, android.graphics.Rect)` |  |
| `android.view.View getDefaultView()` |  |
| `android.view.View getErrorView()` |  |
| `void prepareView(android.view.View)` |  |
| `void setAppWidget(int, android.appwidget.AppWidgetProviderInfo)` |  |
| `void setExecutor(java.util.concurrent.Executor)` |  |
| `void setOnLightBackground(boolean)` |  |
| `void updateAppWidget(android.widget.RemoteViews)` |  |
| `void updateAppWidgetOptions(android.os.Bundle)` |  |
| `void updateAppWidgetSize(android.os.Bundle, int, int, int, int)` |  |

---

### `class AppWidgetManager`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ACTION_APPWIDGET_BIND = "android.appwidget.action.APPWIDGET_BIND"` |  |
| `static final String ACTION_APPWIDGET_CONFIGURE = "android.appwidget.action.APPWIDGET_CONFIGURE"` |  |
| `static final String ACTION_APPWIDGET_DELETED = "android.appwidget.action.APPWIDGET_DELETED"` |  |
| `static final String ACTION_APPWIDGET_DISABLED = "android.appwidget.action.APPWIDGET_DISABLED"` |  |
| `static final String ACTION_APPWIDGET_ENABLED = "android.appwidget.action.APPWIDGET_ENABLED"` |  |
| `static final String ACTION_APPWIDGET_HOST_RESTORED = "android.appwidget.action.APPWIDGET_HOST_RESTORED"` |  |
| `static final String ACTION_APPWIDGET_OPTIONS_CHANGED = "android.appwidget.action.APPWIDGET_UPDATE_OPTIONS"` |  |
| `static final String ACTION_APPWIDGET_PICK = "android.appwidget.action.APPWIDGET_PICK"` |  |
| `static final String ACTION_APPWIDGET_RESTORED = "android.appwidget.action.APPWIDGET_RESTORED"` |  |
| `static final String ACTION_APPWIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE"` |  |
| `static final String EXTRA_APPWIDGET_ID = "appWidgetId"` |  |
| `static final String EXTRA_APPWIDGET_IDS = "appWidgetIds"` |  |
| `static final String EXTRA_APPWIDGET_OLD_IDS = "appWidgetOldIds"` |  |
| `static final String EXTRA_APPWIDGET_OPTIONS = "appWidgetOptions"` |  |
| `static final String EXTRA_APPWIDGET_PREVIEW = "appWidgetPreview"` |  |
| `static final String EXTRA_APPWIDGET_PROVIDER = "appWidgetProvider"` |  |
| `static final String EXTRA_APPWIDGET_PROVIDER_PROFILE = "appWidgetProviderProfile"` |  |
| `static final String EXTRA_CUSTOM_EXTRAS = "customExtras"` |  |
| `static final String EXTRA_CUSTOM_INFO = "customInfo"` |  |
| `static final String EXTRA_HOST_ID = "hostId"` |  |
| `static final int INVALID_APPWIDGET_ID = 0` |  |
| `static final String META_DATA_APPWIDGET_PROVIDER = "android.appwidget.provider"` |  |
| `static final String OPTION_APPWIDGET_HOST_CATEGORY = "appWidgetCategory"` |  |
| `static final String OPTION_APPWIDGET_MAX_HEIGHT = "appWidgetMaxHeight"` |  |
| `static final String OPTION_APPWIDGET_MAX_WIDTH = "appWidgetMaxWidth"` |  |
| `static final String OPTION_APPWIDGET_MIN_HEIGHT = "appWidgetMinHeight"` |  |
| `static final String OPTION_APPWIDGET_MIN_WIDTH = "appWidgetMinWidth"` |  |
| `static final String OPTION_APPWIDGET_RESTORE_COMPLETED = "appWidgetRestoreCompleted"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean bindAppWidgetIdIfAllowed(int, android.content.ComponentName)` |  |
| `boolean bindAppWidgetIdIfAllowed(int, android.content.ComponentName, android.os.Bundle)` |  |
| `boolean bindAppWidgetIdIfAllowed(int, android.os.UserHandle, android.content.ComponentName, android.os.Bundle)` |  |
| `int[] getAppWidgetIds(android.content.ComponentName)` |  |
| `android.appwidget.AppWidgetProviderInfo getAppWidgetInfo(int)` |  |
| `android.os.Bundle getAppWidgetOptions(int)` |  |
| `java.util.List<android.appwidget.AppWidgetProviderInfo> getInstalledProviders()` |  |
| `static android.appwidget.AppWidgetManager getInstance(android.content.Context)` |  |
| `boolean isRequestPinAppWidgetSupported()` |  |
| `void notifyAppWidgetViewDataChanged(int[], int)` |  |
| `void notifyAppWidgetViewDataChanged(int, int)` |  |
| `void partiallyUpdateAppWidget(int[], android.widget.RemoteViews)` |  |
| `void partiallyUpdateAppWidget(int, android.widget.RemoteViews)` |  |
| `boolean requestPinAppWidget(@NonNull android.content.ComponentName, @Nullable android.os.Bundle, @Nullable android.app.PendingIntent)` |  |
| `void updateAppWidget(int[], android.widget.RemoteViews)` |  |
| `void updateAppWidget(int, android.widget.RemoteViews)` |  |
| `void updateAppWidget(android.content.ComponentName, android.widget.RemoteViews)` |  |
| `void updateAppWidgetOptions(int, android.os.Bundle)` |  |
| `void updateAppWidgetProviderInfo(android.content.ComponentName, @Nullable String)` |  |

---

### `class AppWidgetProvider`

- **继承：** `android.content.BroadcastReceiver`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AppWidgetProvider()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onAppWidgetOptionsChanged(android.content.Context, android.appwidget.AppWidgetManager, int, android.os.Bundle)` |  |
| `void onDeleted(android.content.Context, int[])` |  |
| `void onDisabled(android.content.Context)` |  |
| `void onEnabled(android.content.Context)` |  |
| `void onReceive(android.content.Context, android.content.Intent)` |  |
| `void onRestored(android.content.Context, int[], int[])` |  |
| `void onUpdate(android.content.Context, android.appwidget.AppWidgetManager, int[])` |  |

---

### `class AppWidgetProviderInfo`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AppWidgetProviderInfo()` |  |
| `AppWidgetProviderInfo(android.os.Parcel)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int RESIZE_BOTH = 3` |  |
| `static final int RESIZE_HORIZONTAL = 1` |  |
| `static final int RESIZE_NONE = 0` |  |
| `static final int RESIZE_VERTICAL = 2` |  |
| `static final int WIDGET_CATEGORY_HOME_SCREEN = 1` |  |
| `static final int WIDGET_CATEGORY_KEYGUARD = 2` |  |
| `static final int WIDGET_CATEGORY_SEARCHBOX = 4` |  |
| `static final int WIDGET_FEATURE_HIDE_FROM_PICKER = 2` |  |
| `static final int WIDGET_FEATURE_RECONFIGURABLE = 1` |  |
| `int autoAdvanceViewId` |  |
| `android.content.ComponentName configure` |  |
| `int icon` |  |
| `int initialKeyguardLayout` |  |
| `int initialLayout` |  |
| `int minHeight` |  |
| `int minResizeHeight` |  |
| `int minResizeWidth` |  |
| `int minWidth` |  |
| `int previewImage` |  |
| `android.content.ComponentName provider` |  |
| `int resizeMode` |  |
| `int updatePeriodMillis` |  |
| `int widgetCategory` |  |
| `int widgetFeatures` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.appwidget.AppWidgetProviderInfo clone()` |  |
| `int describeContents()` |  |
| `final android.os.UserHandle getProfile()` |  |
| `final android.graphics.drawable.Drawable loadIcon(@NonNull android.content.Context, int)` |  |
| `final String loadLabel(android.content.pm.PackageManager)` |  |
| `final android.graphics.drawable.Drawable loadPreviewImage(@NonNull android.content.Context, int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

