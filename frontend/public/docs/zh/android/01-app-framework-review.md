# Android 11 AOSP 代码审查：核心应用框架

**目录：** `frameworks/base/core/java/android/app/`
**目录中的文件总数：** 210
**审查范围：** 面向开发者的关键应用 API

---

## 目录

1. [Activity.java](#1-activityjava)
2. [Service.java](#2-servicejava)
3. [Application.java](#3-applicationjava)
4. [Fragment.java](#4-fragmentjava)
5. [ActivityManager.java](#5-activitymanagerjava)
6. [NotificationManager.java](#6-notificationmanagerjava)
7. [AlarmManager.java](#7-alarmmanagerjava)
8. [DownloadManager.java](#8-downloadmanagerjava)
9. [KeyguardManager.java](#9-keyguardmanagerjava)
10. [跨领域观察](#10-跨领域观察)

---

## 1. Activity.java

**文件：** `frameworks/base/core/java/android/app/Activity.java`
**行数：** 8,879
**起始版本：** API Level 1

### 类的用途和职责

`Activity` 是 Android 中用户交互的基本构建块。它提供了一个窗口，通过 `setContentView()` 放置 UI，管理自身的生命周期，处理配置变更，并与系统的 Activity 管理基础设施协调。

**声明（第 730 行）：**
```java
public class Activity extends ContextThemeWrapper
        implements LayoutInflater.Factory2,
        Window.Callback, KeyEvent.Callback,
        OnCreateContextMenuListener, ComponentCallbacks2,
        Window.OnWindowDismissedCallback,
        AutofillManager.AutofillClient, ContentCaptureManager.ContentCaptureClient
```

### 生命周期方法

完整的生命周期回调链，所有标注 `@CallSuper` 的方法都必须调用 super：

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `onCreate(Bundle)` | ~1534 | 初始设置：填充 UI、绑定数据。接收保存的实例状态。 |
| `onCreate(Bundle, PersistableBundle)` | ~1627 | 持久化状态变体，用于 `persistableMode="persistAcrossReboots"` 的 Activity。 |
| `onStart()` | ~1734 | Activity 即将可见。 |
| `onRestart()` | ~1720 | 在 `onStop()` 之后、Activity 重新启动之前调用。 |
| `onResume()` | ~1818 | Activity 在前台，与用户交互。 |
| `onTopResumedActivityChanged(boolean)` | ~2130 | **API 29（Android 10）新增。** 指示此 Activity 是否为多窗口模式下最顶层的已恢复 Activity。 |
| `onPause()` | ~1948 | 失去前台焦点。必须轻量级。 |
| `onStop()` | ~2149 | 不再可见。 |
| `onDestroy()` | ~2194 | 销毁前的最终清理。 |
| `onSaveInstanceState(Bundle)` | ~1850 | 保存瞬态 UI 状态。从 API 28 (P) 开始在 `onStop()` 之后调用。 |
| `onSaveInstanceState(Bundle, PersistableBundle)` | ~1900 | 持久化状态保存变体。 |
| `onRestoreInstanceState(Bundle)` | ~1674 | 恢复在 `onSaveInstanceState` 中保存的状态。在 `onStart()` 之后调用。 |

**生命周期顺序说明（第 446-448 行）：** 从 API P 开始，`onSaveInstanceState()` 始终在 `onStop()` 之后调用，允许在 `onStop()` 中安全地执行 Fragment 事务。

### 关键公共 API

#### Activity 导航和结果

| 方法 | 行号 | 签名 |
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

**结果码常量（第 740-744 行）：**
- `RESULT_CANCELED = 0`
- `RESULT_OK = -1`
- `RESULT_FIRST_USER = 1`

#### 运行时权限（第 5208-5257 行）

```java
public final void requestPermissions(@NonNull String[] permissions, int requestCode)
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
public boolean shouldShowRequestPermissionRationale(@NonNull String permission)
```

#### 多窗口和画中画（第 2700-2914 行）

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `isInMultiWindowMode()` | 2751 | 是否处于多窗口模式 |
| `onMultiWindowModeChanged(boolean, Configuration)` | 2723 | 多窗口状态变更时的系统回调 |
| `isInPictureInPictureMode()` | 2794 | 是否处于画中画模式 |
| `onPictureInPictureModeChanged(boolean, Configuration)` | 2766 | 画中画状态变更时的系统回调 |
| `enterPictureInPictureMode(PictureInPictureParams)` | 2834 | 以编程方式进入画中画。Activity 必须处于已恢复状态。 |
| `setPictureInPictureParams(PictureInPictureParams)` | 2864 | 更新画中画属性 |
| `onPictureInPictureRequested()` | 2912 | **Android 11 新增。** 系统请求应用考虑进入画中画模式。 |
| `getMaxNumPictureInPictureActions()` | 2883 | 查询最大画中画操作数 |

#### 窗口和 UI 管理

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `getWindow()` | 1096 | 返回 Window 以便直接访问 |
| `getWindowManager()` | 1084 | 返回 WindowManager 用于自定义窗口 |
| `getCurrentFocus()` | 1120 | 返回当前获得焦点的 View |
| `setContentView(int)` | -- | 将布局填充到 Activity 窗口 |
| `findViewById(int)` | -- | 通过 ID 查找 Activity 内容中的 View |
| `recreate()` | 6341 | 强制使用新实例重建 Activity |
| `reportFullyDrawn()` | 2700 | 向系统发出 UI 已完全渲染的信号 |

#### 配置和状态（第 2947-2997 行）

```java
public void onConfigurationChanged(@NonNull Configuration newConfig)
public int getChangingConfigurations()
public boolean isChangingConfigurations()  // 第 6331 行
```

#### Locus ID（第 1032-1066 行）

**Android 10 新增。** `setLocusContext(@Nullable LocusId, @Nullable Bundle)` 将 Activity 实例与特定内容位置关联，以便在分享、通知和快捷方式中获得更好的排名。

#### Activity 生命周期回调（第 1252-1302 行）

Activity 可以注册每个 Activity 的生命周期回调，与 Application 级别的回调不同：
```java
public void registerActivityLifecycleCallbacks(@NonNull Application.ActivityLifecycleCallbacks callback)
public void unregisterActivityLifecycleCallbacks(@NonNull Application.ActivityLifecycleCallbacks callback)
```

**顺序保证（第 1256-1269 行）：** Activity 注册的回调始终嵌套在 Application 注册的回调之内。前置事件顺序为 Application -> Activity；后置事件顺序为 Activity -> Application。

### 隐藏/系统 API

| 注解 | 方法 | 行号 | 备注 |
|-----------|--------|------|-------|
| `@SystemApi` | `convertFromTranslucent()` | 7267 | 将 Activity 从半透明转换 |
| `@SystemApi` | `convertToTranslucent()` | 7306 | 将 Activity 转换为半透明 |
| `@SystemApi` | `isBackgroundVisibleBehind()` | 7444 | 已废弃，始终返回 false |
| `@SystemApi` | `TranslucentConversionListener` | 8437 | 半透明转换的回调接口 |
| `@hide` | `FINISH_TASK_WITH_ROOT_ACTIVITY` | 752 | 任务完成行为常量 |
| `@hide` | `FINISH_TASK_WITH_ACTIVITY` | 757 | 任务完成行为常量 |
| `@hide` | `startActivityForResultAsUser()` | 5388 | 以特定用户身份启动 Activity |
| `@hide` | `startActivityAsCaller()` | 5474 | 以调用者身份启动 Activity |
| `@hide @TestApi` | `onMovedToDisplay()` | 2944 | 显示器切换回调 |

### 安全相关注解

- 在 `startActivityForResult()`、`startActivityIfNeeded()`、`startNextMatchingActivity()` 及相关方法的 `Intent` 参数上使用了 `@RequiresPermission`——权限取决于 Intent 目标。
- `registerRemoteAnimations()`（第 8753 行）和 `unregisterRemoteAnimations()`（第 8767 行）上使用了 `@RequiresPermission(CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS)`。

### 值得注意的设计模式

1. **`@UnsupportedAppUsage` 的普遍性：** 许多内部字段（`mToken`、`mIntent`、`mMainThread`、`mApplication` 等）都标注了 `@UnsupportedAppUsage`，表示它们历史上被应用通过反射访问，但不是公共 API。许多都有 `maxTargetSdk` 限制。

2. **回调迭代顺序：** 在"上升"生命周期事件（create/start/resume）期间，回调正向迭代；在"下降"事件（pause/stop/destroy）期间，回调反向迭代（LIFO），确保对称嵌套（第 1400-1500 行）。

3. **Content Capture 集成：** 与 `ContentCaptureManager`（第 1151-1229 行）深度集成，在生命周期转换时自动通知智能服务。

4. **自动填充集成：** Activity 实现了 `AutofillManager.AutofillClient`，在生命周期中管理自动填充会话。

---

## 2. Service.java

**文件：** `frameworks/base/core/java/android/app/Service.java`
**行数：** 889
**起始版本：** API Level 1

### 类的用途和职责

`Service` 是用于长时间运行的后台操作或通过 IPC 向其他应用提供功能的应用组件。默认在主线程上运行，必须在清单中声明。

**声明（第 310 行）：**
```java
public abstract class Service extends ContextWrapper implements ComponentCallbacks2,
        ContentCaptureManager.ContentCaptureClient
```

### 生命周期方法

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `onCreate()` | 351 | 首次创建。不要直接调用。 |
| `onStartCommand(Intent, int, int)` | 504 | 每次 `startService()` 调用时触发。返回启动行为。 |
| `onBind(Intent)` | 549 | **抽象方法。** 为绑定客户端返回 IBinder。可返回 null。 |
| `onUnbind(Intent)` | 564 | 所有客户端已断开连接。返回 true 以接收 `onRebind()`。 |
| `onRebind(Intent)` | 579 | `onUnbind()` 返回 true 后有新客户端连接。 |
| `onDestroy()` | 515 | 最终清理。 |
| `onTaskRemoved(Intent)` | 592 | 用户移除了 Service 所属应用的任务。 |
| `onConfigurationChanged(Configuration)` | 518 | 设备配置变更。 |
| `onLowMemory()` | 521 | 系统内存不足。 |
| `onTrimMemory(int)` | 524 | 内存修剪级别。 |

### 启动命令返回值（第 367-430 行）

| 常量 | 值 | 被杀死后的行为 |
|----------|-------|-------------------|
| `START_STICKY_COMPATIBILITY` | 0 | 旧版粘性模式，不保证重启 |
| `START_STICKY` | 1 | 重启服务，使用 null Intent 调用 `onStartCommand` |
| `START_NOT_STICKY` | 2 | 在显式 `startService()` 之前不重建 |
| `START_REDELIVER_INTENT` | 3 | 重启并重新传递上一个 Intent |

### 启动命令标志（第 455-462 行）

| 常量 | 值 | 含义 |
|----------|-------|---------|
| `START_FLAG_REDELIVERY` | 0x0001 | Intent 是进程被杀后的重新传递 |
| `START_FLAG_RETRY` | 0x0002 | Intent 是失败的 `onStartCommand` 的重试 |

### 前台服务 API（第 674-802 行）

```java
public final void startForeground(int id, Notification notification)
public final void startForeground(int id, @NonNull Notification notification,
        @ForegroundServiceType int foregroundServiceType)  // API 29+
public final void stopForeground(boolean removeNotification)
public final void stopForeground(@StopForegroundFlags int flags)
public final @ForegroundServiceType int getForegroundServiceType()
```

**前台服务类型（API 29+）：** 针对 Q+ 的应用可以通过清单中的 `android:foregroundServiceType` 指定类型（如 `camera`、`location`、`mediaPlayback`、`microphone`）。第 741 行的 `startForeground()` 重载接受一个 `foregroundServiceType` 参数，该参数必须是清单中声明的子集。

**停止前台标志（第 320-329 行）：**
- `STOP_FOREGROUND_REMOVE = 1` —— 移除通知
- `STOP_FOREGROUND_DETACH = 2` —— 将通知与服务分离（通知保持可见但独立）

### 自停止方法

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `stopSelf()` | 601 | 停止服务 |
| `stopSelf(int startId)` | 610 | 如果 `startId` 是最近的则停止 |
| `stopSelfResult(int startId)` | 642 | 如果服务将停止则返回 true |

### 隐藏/系统 API

| 注解 | 方法/字段 | 行号 | 备注 |
|-----------|-------------|------|-------|
| `@hide` | `START_TASK_REMOVED_COMPLETE` | 447 | 任务移除的内部常量 |
| `@hide @Deprecated @UnsupportedAppUsage` | `setForeground(boolean)` | 670 | 旧版空操作，已刻意禁用 |
| `@hide @UnsupportedAppUsage` | `attach(...)` | 836 | 内部初始化方法 |
| `@hide` | `detachAndCleanUp()` | 856 | 防止泄漏 |

### 安全说明

- API P+ 上 `startForeground()` 需要 `FOREGROUND_SERVICE` 权限（第 692 行文档说明）。
- 服务访问通过清单中的 `<service>` 声明和 `<uses-permission>` 强制执行。
- IPC 调用可以通过 `checkCallingPermission()` 进行权限检查。

---

## 3. Application.java

**文件：** `frameworks/base/core/java/android/app/Application.java`
**行数：** 655
**起始版本：** API Level 1

### 类的用途和职责

维护全局应用状态。在任何其他组件之前实例化。类文档明确指出（第 43-49 行）子类化 Application 通常是不必要的——推荐使用静态单例作为更模块化的替代方案。

**声明（第 52 行）：**
```java
public class Application extends ContextWrapper implements ComponentCallbacks2
```

### 生命周期方法

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `onCreate()` | 247 | 在任何 Activity/Service/Receiver 之前调用。必须快速。`@CallSuper` |
| `onTerminate()` | 257 | **在生产设备上永远不会被调用。** 仅用于模拟环境。`@CallSuper` |
| `onConfigurationChanged(Configuration)` | 261 | 委托给已注册的 ComponentCallbacks。`@CallSuper` |
| `onLowMemory()` | 271 | 委托给已注册的 ComponentCallbacks。`@CallSuper` |
| `onTrimMemory(int)` | 281 | 委托给已注册的 ComponentCallbacks2。`@CallSuper` |

### 关键公共 API

#### Activity 生命周期观察（第 67-206、305-315 行）

`ActivityLifecycleCallbacks` 接口提供全面的 Activity 生命周期观察，API 29 中添加了 **Pre/Post** 钩子：

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

注册：
```java
public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback)
public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback)
```

#### 组件回调（第 293-303 行）

```java
public void registerComponentCallbacks(ComponentCallbacks callback)
public void unregisterComponentCallbacks(ComponentCallbacks callback)
```

#### 辅助数据（第 212-221、317-332 行）

```java
public interface OnProvideAssistDataListener {
    void onProvideAssistData(Activity activity, Bundle data);
}
public void registerOnProvideAssistDataListener(OnProvideAssistDataListener callback)
public void unregisterOnProvideAssistDataListener(OnProvideAssistDataListener callback)
```

#### 进程名称（第 340 行）

**Android 11 新增：**
```java
public static String getProcessName()
```
返回当前进程名称。包的默认进程与包名相同。

### 隐藏/内部 API

| 注解 | 成员 | 行号 | 备注 |
|-----------|--------|------|-------|
| `@hide @UnsupportedAppUsage` | `mLoadedApk` | 65 | LoadedApk 引用 |
| `@hide @UnsupportedAppUsage` | `attach(Context)` | 350 | 内部初始化 |
| `@hide` | `getAutofillClient()` | 614 | 当 Application 上下文被滥用时查找有焦点的 Activity 用于自动填充 |

### 值得注意的设计模式

`getAutofillClient()` 重写（第 614-654 行）包含一个值得注意的变通方案：当应用使用 Application 上下文而非 Activity 上下文时（这会破坏自动填充），代码会搜索 `ActivityThread.mActivities` 以找到有焦点的 Activity 作为回退。代码中包含注释"Okay, ppl use the application context when they should not"（第 623 行）。

---

## 4. Fragment.java

**文件：** `frameworks/base/core/java/android/app/Fragment.java`
**行数：** 2,984
**起始版本：** API Level 11 (Honeycomb)
**状态：** **`@Deprecated`**（第 265 行）—— 请使用 `androidx.fragment.app.Fragment` 代替

### 类的用途和职责

可放置在 Activity 中的应用 UI 或行为片段。平台 Fragment 已废弃，推荐使用 AndroidX 版本以获得跨设备一致的行为和 Lifecycle 集成。

**声明（第 266 行）：**
```java
@Deprecated
public class Fragment implements ComponentCallbacks2, OnCreateContextMenuListener
```

### Fragment 状态（第 271-277 行）

```java
static final int INVALID_STATE = -1;
static final int INITIALIZING = 0;
static final int CREATED = 1;
static final int ACTIVITY_CREATED = 2;
static final int STOPPED = 3;
static final int STARTED = 4;
static final int RESUMED = 5;
```

### 生命周期方法（来自类 Javadoc，第 117-150 行）

**升至已恢复状态：**
1. `onAttach()` —— 与 Activity 关联
2. `onCreate()` —— 初始创建
3. `onCreateView()` —— 创建并返回视图层次结构
4. `onActivityCreated()` —— 宿主 Activity 的 `onCreate()` 已完成
5. `onViewStateRestored()` —— 视图层次结构状态已恢复
6. `onStart()` —— 可见
7. `onResume()` —— 与用户交互

**拆除：**
1. `onPause()`
2. `onStop()`
3. `onDestroyView()` —— 清理视图资源
4. `onDestroy()` —— 最终清理
5. `onDetach()` —— 不再与 Activity 关联

### 关键设计决策

平台 Fragment 已完全废弃。推荐使用 `androidx.fragment.app.Fragment`，它提供：
- 跨所有 API 级别的一致行为
- 与架构组件 Lifecycle 的集成
- 独立于平台版本的 Bug 修复

---

## 5. ActivityManager.java

**文件：** `frameworks/base/core/java/android/app/ActivityManager.java`
**行数：** 4,934
**起始版本：** API Level 1

### 类的用途和职责

提供有关 Activity、Service 和进程的信息和交互。大多数方法用于调试/信息目的，不应影响运行时行为。标注 `@SystemService(Context.ACTIVITY_SERVICE)`（第 139 行）。

### 面向应用开发者的关键公共 API

#### 内存信息

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `isLowRamDevice()` | 962 | 在低内存设备（通常 < 1GB）上返回 true |
| `getMemoryInfo(MemoryInfo)` | 2804 | 用系统内存状态填充 MemoryInfo |
| `getProcessMemoryInfo(int[])` | 3910 | 给定 PID 的调试内存信息 |
| `getMyMemoryState(RunningAppProcessInfo)` | 3884 | **静态方法。** 获取当前进程的重要性和内存状态 |

#### 应用管理

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `clearApplicationUserData()` | 2992 | 清除调用应用的所有数据（触发进程终止） |
| `getAppTasks()` | 1935 | 获取与调用应用关联的任务列表 |
| `killBackgroundProcesses(String)` | 3940 | 终止指定包的后台进程。需要 `KILL_BACKGROUND_PROCESSES`。 |
| `getRunningAppProcesses()` | 3669 | 运行进程列表。**在 API 22+ 上大多数应用仅限于自身进程。** |

#### 进程重要性级别（第 3230-3354 行）

`RunningAppProcessInfo.Importance` 层次结构（数值越低越重要）：

| 常量 | 值 | 描述 |
|----------|-------|-------------|
| `IMPORTANCE_FOREGROUND` | 100 | 顶层 UI，用户交互中 |
| `IMPORTANCE_FOREGROUND_SERVICE` | 125 | 运行前台服务 |
| `IMPORTANCE_VISIBLE` | 200 | 可见但非前台 |
| `IMPORTANCE_PERCEPTIBLE` | 230 | 不直接感知但可察觉 |
| `IMPORTANCE_SERVICE` | 300 | 后台服务 |
| `IMPORTANCE_TOP_SLEEPING` | 325 | 前台 UI 但设备休眠 |
| `IMPORTANCE_CANT_SAVE_STATE` | 350 | 重量级进程 |
| `IMPORTANCE_CACHED` | 400 | 缓存/后台（原 `IMPORTANCE_BACKGROUND`） |
| `IMPORTANCE_GONE` | 1000 | 进程不存在 |

**兼容性说明（第 3264-3302 行）：** `IMPORTANCE_PERCEPTIBLE` 和 `IMPORTANCE_CANT_SAVE_STATE` 在 API 26 之前有不正确的值。系统为面向 < O 的应用返回旧版值。

#### 进程状态（第 452-488 行，隐藏）

从 `PROCESS_STATE_PERSISTENT` (0) 到 `PROCESS_STATE_CACHED_EMPTY` (19) 的综合 `@ProcessState` 枚举。这些是内部的 (`@hide`) 但映射到公共 Importance 级别。

#### UID 重要性监听器

```java
public interface OnUidImportanceListener {
    void onUidImportance(int uid, int importance);
}
```

### 内部类

| 类 | 描述 |
|-------|-------------|
| `MemoryInfo` | 系统内存信息（总量、可用、阈值、低内存标志） |
| `RunningAppProcessInfo` | 运行进程详情（名称、pid、uid、重要性） |
| `ProcessErrorStateInfo` | 进程中的错误状态（崩溃、ANR） |
| `TaskDescription` | 任务的视觉属性（标签、图标、颜色） |
| `AppTask` | 管理应用任务的句柄 |
| `RecentTaskInfo` | 最近任务的信息 |

### 隐藏/系统 API

该文件包含大量隐藏 API，包括：

| 注解 | 项目 | 行号 | 备注 |
|-----------|------|------|-------|
| `@hide` | `INSTR_FLAG_DISABLE_HIDDEN_API_CHECKS` | 160 | 用于插桩测试 |
| `@hide` | `INSTR_FLAG_DISABLE_ISOLATED_STORAGE` | 165 | 用于插桩测试 |
| `@hide` | `START_*` 常量 | 222-350 | Activity 启动结果码 |
| `@hide` | `INTENT_SENDER_*` 常量 | 405-434 | PendingIntent 类型 |
| `@hide` | `PROCESS_STATE_*` 常量 | 492+ | 进程状态粒度 |
| `@hide` | `USER_OP_*` 常量 | 437-449 | 用户操作结果 |

### 安全注解

- `killBackgroundProcesses()` 需要 `@RequiresPermission(Manifest.permission.KILL_BACKGROUND_PROCESSES)`（在实现中验证）。

---

## 6. NotificationManager.java

**文件：** `frameworks/base/core/java/android/app/NotificationManager.java`
**行数：** 2,346
**起始版本：** API Level 1

### 类的用途和职责

用于发布状态栏通知的系统服务。管理通知渠道（API 26+）、渠道组、勿扰规则和通知策略。标注 `@SystemService(Context.NOTIFICATION_SERVICE)`（第 99 行）。

### 关键公共 API

#### 通知发布和取消

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `notify(int, Notification)` | 505 | 按 ID 发布通知 |
| `notify(String, int, Notification)` | 529 | 使用标签和 ID 发布（标签+ID 对必须唯一） |
| `notifyAsPackage(String, String, int, Notification)` | 556 | 作为委托包发布 |
| `cancel(int)` | 627 | 按 ID 取消 |
| `cancel(String, int)` | 641 | 按标签和 ID 取消 |
| `cancelAsPackage(String, String, int)` | 665 | 为委托包取消 |
| `cancelAll()` | 696 | 取消所有通知 |

**验证（第 588-610 行）：** `fixNotification()` 强制要求面向 > LOLLIPOP_MR1 的应用必须提供有效的小图标，否则抛出 `IllegalArgumentException`。

#### 通知渠道（API 26+）

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `createNotificationChannel(NotificationChannel)` | 819 | 创建或恢复渠道。重要性只能降低。 |
| `createNotificationChannels(List)` | 829 | 批量创建渠道 |
| `getNotificationChannel(String)` | 847 | 按 ID 获取渠道设置 |
| `getNotificationChannel(String, String)` | 866 | 按 ID 和会话 ID 获取渠道（API 30） |
| `getNotificationChannels()` | 886 | 获取调用包的所有渠道 |
| `deleteNotificationChannel(String)` | 903 | 删除渠道（使用相同 ID 重新创建会恢复设置） |
| `createNotificationChannelGroup(NotificationChannelGroup)` | 777 | 创建/更新渠道组 |
| `createNotificationChannelGroups(List)` | 786 | 批量创建组 |
| `getNotificationChannelGroup(String)` | 917 | 按 ID 获取组 |
| `getNotificationChannelGroups()` | 929 | 获取所有组 |
| `deleteNotificationChannelGroup(String)` | 947 | 删除组及其所有渠道 |

#### 通知委托

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `setNotificationDelegate(String)` | 720 | 允许其他包代表你发布通知 |
| `getNotificationDelegate()` | 735 | 获取当前委托 |
| `canNotifyAsPackage(String)` | 751 | 检查是否可以作为其他包发布 |

#### 勿扰 / Zen 模式

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `getCurrentInterruptionFilter()` | -- | 获取当前勿扰过滤器 |
| `setInterruptionFilter(int)` | -- | 设置勿扰过滤器。需要策略访问权限。 |
| `getNotificationPolicy()` | -- | 获取通知策略 |
| `setNotificationPolicy(Policy)` | -- | 设置通知策略 |
| `isNotificationPolicyAccessGranted()` | -- | 检查应用是否有勿扰访问权限 |
| `addAutomaticZenRule(AutomaticZenRule)` | -- | 添加自动勿扰规则 |
| `setAutomaticZenRuleState(String, Condition)` | -- | 更新规则状态 |

### 重要性级别（第 415-453 行）

| 常量 | 值 | 描述 |
|----------|-------|-------------|
| `IMPORTANCE_UNSPECIFIED` | -1000 | 未表达偏好（仅用于持久化） |
| `IMPORTANCE_NONE` | 0 | 不在通知栏中可见 |
| `IMPORTANCE_MIN` | 1 | 折叠区域之下。不适用于前台服务。 |
| `IMPORTANCE_LOW` | 2 | 通知栏和可能的状态栏，无声音 |
| `IMPORTANCE_DEFAULT` | 3 | 到处显示，发出声音 |
| `IMPORTANCE_HIGH` | 4 | 弹出，可能使用全屏 Intent |
| `IMPORTANCE_MAX` | 5 | 未使用 |

### 中断过滤器常量（第 303-332 行）

- `INTERRUPTION_FILTER_ALL = 1` —— 正常
- `INTERRUPTION_FILTER_PRIORITY = 2` —— 仅优先
- `INTERRUPTION_FILTER_NONE = 3` —— 完全静音
- `INTERRUPTION_FILTER_ALARMS = 4` —— 仅闹钟
- `INTERRUPTION_FILTER_UNKNOWN = 0`

### 广播 Action

| 常量 | 行号 | 描述 |
|----------|------|-------------|
| `ACTION_APP_BLOCK_STATE_CHANGED` | 113 | 应用被阻止/解除阻止 |
| `ACTION_NOTIFICATION_CHANNEL_BLOCK_STATE_CHANGED` | 129 | 渠道被阻止/解除阻止 |
| `ACTION_NOTIFICATION_CHANNEL_GROUP_BLOCK_STATE_CHANGED` | 172 | 渠道组被阻止/解除阻止 |
| `ACTION_AUTOMATIC_ZEN_RULE_STATUS_CHANGED` | 187 | 勿扰规则状态变更 |
| `ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED` | 264 | 勿扰访问权限授予/撤销 |
| `ACTION_NOTIFICATION_POLICY_CHANGED` | 272 | 通知策略变更 |
| `ACTION_INTERRUPTION_FILTER_CHANGED` | 280 | 勿扰过滤器变更 |

### 隐藏/系统 API

| 注解 | 方法 | 行号 | 备注 |
|-----------|--------|------|-------|
| `@hide @UnsupportedAppUsage` | `notifyAsUser()` | 574 | 以特定用户身份发布 |
| `@hide @UnsupportedAppUsage` | `cancelAsUser()` | 678 | 以特定用户身份取消 |
| `@hide @UnsupportedAppUsage` | `setZenMode()` | 998 | 直接 Zen 模式控制 |
| `@hide @TestApi` | `getEffectsSuppressor()` | 960 | 获取效果抑制器组件 |
| `@hide @TestApi` | `matchesCallFilter()` | 973 | 检查 extras 是否匹配来电过滤 |
| `@hide` | `BUBBLE_PREFERENCE_*` | 458-466 | 气泡通知偏好 |

---

## 7. AlarmManager.java

**文件：** `frameworks/base/core/java/android/app/AlarmManager.java`
**行数：** 1,158
**起始版本：** API Level 1

### 类的用途和职责

用于在未来时间调度应用执行的系统服务。支持墙钟时间 (RTC) 和引导相对 (ELAPSED_REALTIME) 时间。从 API 19 (KitKat) 开始，闹钟默认为非精确以优化电池。标注 `@SystemService(Context.ALARM_SERVICE)`。

### 闹钟类型（第 94-120 行）

| 常量 | 值 | 时钟 | 是否唤醒设备 |
|----------|-------|-------|-------------|
| `RTC_WAKEUP` | 0 | `System.currentTimeMillis()` | 是 |
| `RTC` | 1 | `System.currentTimeMillis()` | 否 |
| `ELAPSED_REALTIME_WAKEUP` | 2 | `SystemClock.elapsedRealtime()` | 是 |
| `ELAPSED_REALTIME` | 3 | `SystemClock.elapsedRealtime()` | 否 |

### 关键公共 API

#### 设置闹钟

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `set(int, long, PendingIntent)` | 348 | 调度闹钟。**自 API 19 起为非精确。** |
| `set(int, long, String, OnAlarmListener, Handler)` | 375 | 直接回调变体 |
| `setExact(int, long, PendingIntent)` | 540 | 精确传递。请谨慎使用。 |
| `setExact(int, long, String, OnAlarmListener, Handler)` | 554 | 直接回调精确闹钟 |
| `setWindow(int, long, long, PendingIntent)` | 486 | 在时间窗口内传递 |
| `setWindow(int, long, long, String, OnAlarmListener, Handler)` | 501 | 带直接回调的窗口 |
| `setRepeating(int, long, long, PendingIntent)` | 436 | 重复闹钟。**自 API 19 起为非精确。** |
| `setInexactRepeating(int, long, long, PendingIntent)` | 795 | 显式非精确重复 |
| `setAlarmClock(AlarmClockInfo, PendingIntent)` | 607 | 闹钟。始终精确，从 Doze 唤醒。 |
| `setAndAllowWhileIdle()` | -- | 在 Doze 模式期间执行（有频率限制） |
| `setExactAndAllowWhileIdle()` | -- | 精确 + Doze 绕过（有频率限制） |

#### 取消

```java
public void cancel(PendingIntent operation)
public void cancel(OnAlarmListener listener)
```

#### 查询

```java
public AlarmClockInfo getNextAlarmClock()
```

### 标准时间间隔（第 708-736 行）

```java
INTERVAL_FIFTEEN_MINUTES = 15 * 60 * 1000
INTERVAL_HALF_HOUR       = 2 * INTERVAL_FIFTEEN_MINUTES
INTERVAL_HOUR            = 2 * INTERVAL_HALF_HOUR
INTERVAL_HALF_DAY        = 12 * INTERVAL_HOUR
INTERVAL_DAY             = 2 * INTERVAL_HALF_DAY
```

### OnAlarmListener 接口（第 200-205 行）

```java
public interface OnAlarmListener {
    public void onAlarm();
}
```
直接通知闹钟：请求者必须持续运行。仅支持一次性闹钟（不支持重复）。

### 隐藏/系统 API

| 注解 | 方法 | 行号 | 备注 |
|-----------|--------|------|-------|
| `@hide @UnsupportedAppUsage` | `WINDOW_EXACT = 0` | 135 | 精确窗口常量 |
| `@hide @UnsupportedAppUsage` | `WINDOW_HEURISTIC = -1` | 138 | 启发式窗口常量 |
| `@hide @UnsupportedAppUsage` | `FLAG_STANDALONE` | 146 | 不批处理 |
| `@hide @UnsupportedAppUsage` | `FLAG_WAKE_FROM_IDLE` | 154 | 从 Doze 唤醒 |
| `@hide` | `FLAG_ALLOW_WHILE_IDLE` | 165 | 在 Doze 期间执行 |
| `@hide @UnsupportedAppUsage` | `FLAG_ALLOW_WHILE_IDLE_UNRESTRICTED` | 175 | 仅系统可用的无限制 Doze 绕过 |
| `@hide @UnsupportedAppUsage` | `FLAG_IDLE_UNTIL` | 185 | 空闲直到标记闹钟 |
| `@hide @SystemApi` | `set(..., WorkSource)` | 613 | 带 WorkSource 归因的闹钟。需要 `UPDATE_DEVICE_STATS`。 |
| `@hide` | `setIdleUntil()` | 565 | 使闹钟管理器保持空闲直到指定时间 |

### 值得注意的设计决策：向后兼容性（第 270-272 行）

```java
mAlwaysExact = (mTargetSdkVersion < Build.VERSION_CODES.KITKAT);
```
面向 KitKat 之前的应用继续从标准 `set()` 方法获得精确闹钟行为。

---

## 8. DownloadManager.java

**文件：** `frameworks/base/core/java/android/app/DownloadManager.java`
**行数：** 1,842
**起始版本：** API Level 9

### 类的用途和职责

用于在后台处理长时间运行的 HTTP 下载的系统服务。处理 HTTP 交互、重试、连接变更和系统重启。需要 `android.permission.INTERNET`。标注 `@SystemService(Context.DOWNLOAD_SERVICE)`。

### 关键公共 API

#### 下载操作

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `enqueue(Request)` | 1117 | 开始新下载。返回下载 ID。 |
| `remove(long...)` | 1149 | 按 ID 取消和移除下载 |
| `query(Query)` | 1159 | 查询下载状态 |
| `openDownloadedFile(long)` | 1178 | 以 ParcelFileDescriptor 打开已下载文件 |
| `getUriForDownloadedFile(long)` | 1190 | 获取已下载文件的 content URI |
| `getMimeTypeForDownloadedFile(long)` | 1222 | 获取 MIME 类型 |
| `addCompletedDownload(...)` | 1435+ | 将已下载的文件添加到管理器（多个重载） |

#### 下载状态常量（第 197-217 行）

| 常量 | 值 | 描述 |
|----------|-------|-------------|
| `STATUS_PENDING` | 1 | 等待开始 |
| `STATUS_RUNNING` | 2 | 正在运行 |
| `STATUS_PAUSED` | 4 | 等待重试/恢复 |
| `STATUS_SUCCESSFUL` | 8 | 成功完成 |
| `STATUS_FAILED` | 16 | 永久失败 |

#### 错误码（第 222-279 行）

- `ERROR_UNKNOWN = 1000`
- `ERROR_FILE_ERROR = 1001`
- `ERROR_UNHANDLED_HTTP_CODE = 1002`
- `ERROR_HTTP_DATA_ERROR = 1004`
- `ERROR_TOO_MANY_REDIRECTS = 1005`
- `ERROR_INSUFFICIENT_SPACE = 1006`
- `ERROR_DEVICE_NOT_FOUND = 1007`
- `ERROR_CANNOT_RESUME = 1008`
- `ERROR_FILE_ALREADY_EXISTS = 1009`

#### 暂停原因（第 285-302 行）

- `PAUSED_WAITING_TO_RETRY = 1`
- `PAUSED_WAITING_FOR_NETWORK = 2`
- `PAUSED_QUEUED_FOR_WIFI = 3`
- `PAUSED_UNKNOWN = 4`

### Request 内部类（第 382 行）

构建器风格的下载配置：

| 方法 | 描述 |
|--------|-------------|
| `Request(Uri)` | 构造函数。仅支持 HTTP/HTTPS。 |
| `setDestinationUri(Uri)` | 设置下载目标。Q+ 应用在应用专属目录不需要 WRITE_EXTERNAL_STORAGE。 |
| `setDestinationInExternalFilesDir(Context, String, String)` | 保存到应用的外部文件目录 |
| `setDestinationInExternalPublicDir(String, String)` | 保存到公共外部目录 |
| `setAllowedNetworkTypes(int)` | 限制为 `NETWORK_MOBILE` 和/或 `NETWORK_WIFI` |
| `setAllowedOverRoaming(boolean)` | 允许在漫游时下载 |
| `setAllowedOverMetered(boolean)` | 允许在计费网络上下载 |
| `setTitle(CharSequence)` | 通知标题 |
| `setDescription(CharSequence)` | 通知描述 |
| `setMimeType(String)` | 设置 MIME 类型 |
| `addRequestHeader(String, String)` | 添加 HTTP 头 |
| `setNotificationVisibility(int)` | 通知中的可见性 |

#### 通知可见性常量（第 432-457 行）

- `VISIBILITY_VISIBLE = 0` —— 在进度期间可见
- `VISIBILITY_VISIBLE_NOTIFY_COMPLETED = 1` —— 在进度期间和之后可见
- `VISIBILITY_HIDDEN = 2` —— 隐藏
- `VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION = 3` —— 仅在完成时显示

### 广播 Action

| 常量 | 行号 | 描述 |
|----------|------|-------------|
| `ACTION_DOWNLOAD_COMPLETE` | 308 | 下载完成 |
| `ACTION_NOTIFICATION_CLICKED` | 315 | 用户点击了下载通知 |
| `ACTION_VIEW_DOWNLOADS` | 322 | 查看所有下载 |

### 隐藏/系统 API

| 注解 | 成员 | 行号 | 备注 |
|-----------|--------|------|-------|
| `@SystemApi` | `ACTION_DOWNLOAD_COMPLETED` | 347 | 系统级下载完成 Action |
| `@hide @TestApi` | `COLUMN_MEDIASTORE_URI` | 187 | MediaStore URI 列 |
| `@hide @UnsupportedAppUsage` | `UNDERLYING_COLUMNS` | 355 | 查询的基本列 |
| `@hide @Deprecated` | `NETWORK_BLUETOOTH` | 401 | 蓝牙网络类型 |

### 分区存储影响（第 488-494 行）

对于面向 Q+ 的应用，当路径在应用专属目录或顶层 Downloads 目录中时，`setDestinationUri()` 不再需要 `WRITE_EXTERNAL_STORAGE`。

---

## 9. KeyguardManager.java

**文件：** `frameworks/base/core/java/android/app/KeyguardManager.java`
**行数：** 817
**起始版本：** API Level 1

### 类的用途和职责

控制键盘锁（锁屏）的锁定和解锁。提供凭据确认、设备安全状态查询和键盘锁关闭。标注 `@SystemService(Context.KEYGUARD_SERVICE)`。

### 关键公共 API

#### 设备状态查询

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `isKeyguardLocked()` | 456 | 键盘锁当前是否显示？ |
| `isKeyguardSecure()` | 472 | 是否设置了 PIN/图案/密码或 SIM 锁定？ |
| `isDeviceLocked()` | 499 | 是否需要凭据解锁？ |
| `isDeviceSecure()` | 525 | 是否设置了 PIN/图案/密码？（忽略 SIM） |

#### 凭据确认

| 方法 | 行号 | 描述 |
|--------|------|-------------|
| `createConfirmDeviceCredentialIntent(CharSequence, CharSequence)` | 148 | **已废弃。** 使用 BiometricPrompt。创建确认凭据的 Intent。 |

使用模式：
```java
Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(title, description);
if (intent != null) {
    startActivityForResult(intent, REQUEST_CODE);
    // 检查 RESULT_OK
}
```

#### 键盘锁关闭（第 543-628 行）

```java
public void requestDismissKeyguard(@NonNull Activity activity,
        @Nullable KeyguardDismissCallback callback)
```

`KeyguardDismissCallback`（第 399-418 行）提供：
- `onDismissError()` —— 关闭不可行
- `onDismissSucceeded()` —— 成功关闭
- `onDismissCancelled()` —— 用户取消

#### 旧版 API（已废弃）

| 方法 | 行号 | 废弃替代 |
|--------|------|----------------------|
| `newKeyguardLock(String)` | 447 | 使用 `FLAG_DISMISS_KEYGUARD` / `FLAG_SHOW_WHEN_LOCKED` |
| `exitKeyguardSecurely(OnKeyguardExitResult)` | 652 | 使用窗口标志 |

`KeyguardLock` 内部类（第 330 行）提供 `disableKeyguard()` 和 `reenableKeyguard()`，两者都需要 `DISABLE_KEYGUARD` 权限。

### 隐藏/系统 API

| 注解 | 方法 | 行号 | 备注 |
|-----------|--------|------|-------|
| `@hide` | `ACTION_CONFIRM_DEVICE_CREDENTIAL` | 79 | 凭据确认的 Intent |
| `@hide` | `ACTION_CONFIRM_DEVICE_CREDENTIAL_WITH_USER` | 86 | 每用户凭据确认 |
| `@hide` | `ACTION_CONFIRM_FRP_CREDENTIAL` | 93 | 恢复出厂设置保护 |
| `@hide @SystemApi` | `createConfirmFactoryResetCredentialIntent()` | 225 | FRP 凭据验证 |
| `@hide @SystemApi` | `setPrivateNotificationsAllowed(boolean)` | 282 | 控制锁屏上的私密通知可见性。需要 `CONTROL_KEYGUARD_SECURE_NOTIFICATIONS`。 |
| `@hide @SystemApi` | `getPrivateNotificationsAllowed()` | 301 | 查询私密通知可见性 |
| `@hide @SystemApi` | `requestDismissKeyguard(Activity, CharSequence, Callback)` | 599 | 带消息关闭。需要 `SHOW_KEYGUARD_MESSAGE`。 |
| `@hide @SystemApi` | `isValidLockPasswordComplexity()` | 690 | 验证密码。需要 `SET_INITIAL_LOCK`。 |
| `@hide @SystemApi` | `getMinLockLength()` | 719 | 最小密码长度。需要 `SET_INITIAL_LOCK`。 |
| `@hide @SystemApi` | `setLock()` | 747 | 设置锁屏密码。需要 `SET_INITIAL_LOCK`。仅限汽车平台。 |

### 锁定类型（第 807-816 行，隐藏）

```java
@interface LockTypes {
    int PASSWORD = 0;
    int PIN = 1;
    int PATTERN = 2;
}
```

### 安全说明

1. `createConfirmDeviceCredentialIntent()` 明确将包设置为设置应用以确保安全（第 156 行）。
2. `setLock()` 方法（第 747 行）仅限于汽车平台的初始设备设置——它检查 `FEATURE_AUTOMOTIVE` 并在设备已配置时拒绝。
3. `checkInitialLockMethodUsage()` 方法（第 666 行）强制执行 `SET_INITIAL_LOCK` 权限和汽车功能门控。

---

## 10. 跨领域观察

### 设计模式

1. **系统服务模式：** Manager 类（`ActivityManager`、`NotificationManager`、`AlarmManager`、`DownloadManager`、`KeyguardManager`）都标注了 `@SystemService`，通过 `Context.getSystemService()` 获取。它们充当客户端代理，通过 AIDL 接口（`INotificationManager`、`IAlarmManager`、`IActivityManager` 等）与系统服务器通信。

2. **`@UnsupportedAppUsage` 注解：** 在所有文件中普遍存在，标记应用历史上通过反射访问的内部成员。许多包含 `maxTargetSdk` 以从特定 API 级别开始强制执行灰名单/黑名单隐藏 API 访问。

3. **向后兼容层：** 多种情况下行为根据 `targetSdkVersion` 变化：
   - AlarmManager：精确与非精确闹钟（KitKat 边界）
   - Activity：`onSaveInstanceState` 顺序（P 边界）
   - ActivityManager：重要性值修正（O 边界）
   - NotificationManager：小图标强制（LOLLIPOP_MR1 边界）

4. **生命周期回调对称性：** `Activity` 和 `Application` 都实现了对称的回调分发模式："上升"回调正向迭代，"下降"回调反向迭代。

5. **`@CallSuper` 强制：** 用于 `Application.onCreate()`、`Application.onTerminate()`、`Application.onConfigurationChanged()`、`Application.onLowMemory()`、`Application.onTrimMemory()` 和 Activity 生命周期方法以强制执行 super 调用。

### 安全模式

1. **权限门控 API：**
   - `DISABLE_KEYGUARD` —— KeyguardManager 锁定/解锁
   - `KILL_BACKGROUND_PROCESSES` —— ActivityManager.killBackgroundProcesses()
   - `FOREGROUND_SERVICE` —— Service.startForeground()（API P+）
   - `UPDATE_DEVICE_STATS` —— AlarmManager 系统闹钟变体
   - `CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS` —— Activity 远程动画
   - `CONTROL_KEYGUARD_SECURE_NOTIFICATIONS` —— KeyguardManager 私密通知
   - `SET_INITIAL_LOCK` —— KeyguardManager 锁定设置（汽车平台）
   - `SHOW_KEYGUARD_MESSAGE` —— KeyguardManager 带消息关闭
   - `INTERNET` —— DownloadManager（文档要求）

2. **Intent 包固定：** `KeyguardManager.createConfirmDeviceCredentialIntent()` 明确将包设置为设置应用以防止 Intent 劫持（第 156 行）。

3. **@SystemApi 门控：** 系统 API 仅对使用平台密钥签名或获得特殊权限的应用可用，为敏感操作（半透明转换、FRP 凭据、锁定管理）提供二级保护。

### Android 11 中的 API 演进

1. **`Application.getProcessName()`**（第 340 行）—— 用于获取当前进程名称的新静态方法。
2. **`Activity.onPictureInPictureRequested()`**（第 2912 行）—— 系统发起的画中画建议的新回调。
3. **`NotificationManager` 会话渠道** —— `getNotificationChannel(String, String)` 支持会话 ID（第 866 行）。
4. **`Service` 前台服务类型** —— 从 Android 10 扩展，增加了额外的类型强制。
5. **Content Capture 集成** 在 Activity 和 Service 中深化，用于智能功能。

### 文件大小总结

| 文件 | 行数 | 复杂度 |
|------|-------|-----------|
| Activity.java | 8,879 | 最高——中央 UI 组件，API 表面广泛 |
| ActivityManager.java | 4,934 | 高——许多内部类，进程/任务管理 |
| Fragment.java | 2,984 | 中等——已废弃，被 AndroidX 取代 |
| NotificationManager.java | 2,346 | 中等——渠道、勿扰、委托 |
| DownloadManager.java | 1,842 | 中等——HTTP 下载、Request/Query 构建器 |
| AlarmManager.java | 1,158 | 低-中等——带 Doze 感知的闹钟调度 |
| Service.java | 889 | 低——简洁、专注的抽象 |
| KeyguardManager.java | 817 | 低——锁屏状态和控制 |
| Application.java | 655 | 低——全局状态和生命周期观察 |
