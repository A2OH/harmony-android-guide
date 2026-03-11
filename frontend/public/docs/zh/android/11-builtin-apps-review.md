# Android 11 AOSP 代码审查：内置应用与 SystemUI

## 1. 概述

本报告审查了 AOSP Android 11 内置应用和 SystemUI 的架构、模式和 API 使用情况。这些应用作为参考实现，展示了 Google 工程师如何使用 Android 框架 API。审查内容涵盖 SystemUI（状态栏、通知、快捷设置、锁屏）、设置应用、Launcher3 以及其他精选内置应用。主要架构模式包括 SystemUI 中的 Dagger 依赖注入、设置中的 PreferenceController 模式以及 Launcher3 中的状态机架构。

---

## 2. SystemUI 架构

**源码位置：** `frameworks/base/packages/SystemUI/`

SystemUI 是 AOSP 中架构最复杂的应用。它管理状态栏、通知面板、快捷设置面板、锁屏、音量控制、屏幕录制、气泡、设备控制等功能。

### 2.1 应用引导启动

**关键文件：** `SystemUIApplication.java`

SystemUI 作为持久系统进程运行，使用 `android.uid.systemui` 共享 UID。应用通过以下方式引导启动：

1. `SystemUIAppComponentFactory` 在 `Application.onCreate()` 之前初始化 Dagger 依赖图
2. `SystemUIApplication.onCreate()` 从 `SystemUIFactory` 获取 `SystemUIRootComponent`
3. `startServicesIfNeeded()` 从资源数组中加载服务组件，通过反射或 Dagger 实例化它们，并对每个组件调用 `start()`

```
SystemUIAppComponentFactory -> SystemUIApplication.onCreate()
    -> SystemUIFactory.getInstance().getRootComponent()
    -> startServicesIfNeeded() -> instantiate SystemUI[] services -> start()
```

每个服务都继承抽象基类 `SystemUI`，该基类提供：
- `start()` -- 初始化入口点
- `onBootCompleted()` -- 启动完成后的回调
- `onConfigurationChanged()` -- 配置变更处理
- `dump()` -- 通过 `Dumpable` 接口集成 dumpsys

### 2.2 使用 Dagger 进行依赖注入

**关键文件：**
- `dagger/SystemUIRootComponent.java`
- `dagger/SystemUIModule.java`
- `dagger/DependencyProvider.java`
- `dagger/DependencyBinder.java`
- `Dependency.java`

SystemUI 使用 Dagger 2 进行依赖注入。根组件 `SystemUIRootComponent` 标注了 `@Singleton` 注解，由多个模块组成：

| 模块 | 用途 |
|--------|---------|
| `SystemUIModule` | 核心绑定（启动缓存、组件辅助、并发） |
| `SystemUIDefaultModule` | 接口的默认实现 |
| `SystemServicesModule` | Android 系统服务绑定（WindowManager、PackageManager 等） |
| `DependencyProvider` | 遗留的 Provider 风格绑定（`@Provides`） |
| `DependencyBinder` | 接口到实现的绑定（`@Binds`） |
| `SystemUIBinder` | SystemUI 服务组件的绑定 |
| `PipModule` | 画中画子组件 |

作用域注入的子组件：
- `StatusBarComponent` -- 作用域限定于 StatusBar 生命周期
- `NotificationRowComponent` -- 每个通知行
- `ExpandableNotificationRowComponent` -- 可展开的通知行

遗留的 `Dependency.java` 充当服务定位器（Dagger 迁移前的产物），使用 `ArrayMap` 存储懒初始化器。新代码使用 `@Inject` 构造函数注入。

**对应用开发者的启示：** SystemUI 从服务定位器（`Dependency.java`）到构造函数注入的迁移过程，展示了大型应用推荐的 Dagger 采用路径。

### 2.3 状态栏（`StatusBar.java`）

**关键文件：** `statusbar/phone/StatusBar.java`（4,411 行）

`StatusBar` 继承 `SystemUI`，是手机状态栏 UI 的中央协调器。它实现了多个接口：

- `ActivityStarter` -- 从系统 UI 上下文启动 Activity
- `KeyguardStateController.Callback` -- 锁屏状态变化
- `CommandQueue.Callbacks` -- 来自 system_server 的 IPC 命令
- `ColorExtractor.OnColorsChangedListener` -- 壁纸颜色变化
- `ConfigurationListener` -- 配置（旋转、密度）变化
- `StatusBarStateController.StateListener` -- 通知面板/锁屏状态转换
- `BatteryController.BatteryStateChangeCallback` -- 电池状态
- `LifecycleOwner` -- AndroidX 生命周期集成

状态栏通过 `StatusBarState` 管理三种视觉状态：
1. **SHADE** -- 正常状态栏，可下拉通知面板
2. **KEYGUARD** -- 锁屏
3. **SHADE_LOCKED** -- 锁屏上下拉的通知面板

关键注入依赖包括：`NotificationsController`、`BubbleController`、`NavigationBarController`、`BrightnessMirrorController`、`KeyguardViewMediator`、`BiometricUnlockController`、`NotificationPanelViewController`。

### 2.4 通知面板

**关键文件：** `statusbar/phone/NotificationPanelViewController.java`

通知面板由 `NotificationPanelViewController` 管理，负责处理：
- 滑动展开/收起的触摸事件处理
- 协调快捷设置面板展开与通知列表滚动
- 锁屏时钟和状态显示
- 生物识别解锁动画
- 媒体播放器集成

通知系统使用一个复杂的管道：
```
NotificationListener (从 system_server 接收)
    -> NotificationEntryManager (管理通知条目)
    -> NotificationFilter (可见性过滤)
    -> NotificationGroupManager (分组逻辑)
    -> VisualStabilityManager (防止交互时重新排序)
    -> NotificationViewHierarchyManager (视图绑定)
```

### 2.5 快捷设置

**关键文件：**
- `qs/QSTileHost.java` -- 磁贴管理和生命周期
- `qs/tileimpl/QSTileImpl.java` -- 所有磁贴的抽象基类
- `qs/tiles/` -- 20 个内置磁贴实现

#### QSTileImpl 架构

`QSTileImpl<TState extends State>` 是所有快捷设置磁贴的基类。它使用泛型状态模式：

- 状态管理通过内部 `Handler H` 在后台 `Looper` 上运行
- UI 更新通过 `mUiHandler` 发布到主线程
- 每个磁贴实现 `handleUpdateState(TState state, Object arg)` 来填充其状态
- 状态变化触发已注册 `QSTile.Callback` 监听器的回调
- 磁贴实现 `LifecycleOwner` 以支持生命周期感知的观察

磁贴生命周期：
```
QSTileHost.createTile(spec) -> QSFactory.createTile(spec)
    -> tile.initialize() -> tile.handleSetListening(true)
    -> tile.refreshState() -> tile.handleUpdateState()
```

**内置磁贴**（在 `qs/tiles/` 中）：
`WifiTile`、`BluetoothTile`、`CellularTile`、`BatterySaverTile`、`DndTile`、`FlashlightTile`、`RotationLockTile`、`LocationTile`、`CastTile`、`HotspotTile`、`AirplaneModeTile`、`NfcTile`、`DataSaverTile`、`NightDisplayTile`、`ColorInversionTile`、`UiModeNightTile`、`ScreenRecordTile`、`UserTile`

**示例 -- WifiTile 模式：**
```java
public class WifiTile extends QSTileImpl<SignalState> {
    @Inject
    public WifiTile(QSHost host, NetworkController networkController,
            ActivityStarter activityStarter) {
        super(host);
        mController = networkController;
        mController.observe(getLifecycle(), mSignalCallback);  // 生命周期感知
    }
}
```

`QSTileHost` 从 `Settings.Secure.QS_TILES` 读取磁贴规格，并使用 `TunerService` 来观察变化。第三方磁贴通过 `CustomTile` 和 `TileLifecycleManager` 使用 `TileService` API。

### 2.6 锁屏（Keyguard）

**关键文件：** `keyguard/KeyguardViewMediator.java`

`KeyguardViewMediator` 继承 `SystemUI` 并协调锁屏：
- 监听 `KeyguardUpdateMonitor` 的生物识别事件、SIM 卡状态、信任代理
- 使用 `KeyguardViewController` 管理锁定/解锁过渡
- 通过 `AlarmManager` 处理锁定超时
- 与 `BiometricUnlockController` 协调指纹/面部解锁
- 使用 `LockPatternUtils` 进行凭据验证
- 与 `DeviceConfig` 集成以支持功能开关

### 2.7 Android 11 中 SystemUI 的新功能

#### 设备控制（`controls/`）
- `ControlsController` 接口管理收藏夹，并与 `ControlsProviderService` 实现进行通信
- 使用 `ControlsBindingController` 的绑定模式进行服务通信
- UI 由 `ControlsUiController` 处理

#### 媒体控制（`media/`）
Android 11 引入了通知面板中重新设计的媒体播放器：
- `MediaDataManager` -- 媒体会话的中央数据管理器（Kotlin）
- `MediaControlPanel` -- 单个媒体控制的 UI
- `MediaCarouselController` -- 可滚动的媒体播放器轮播
- `MediaHierarchyManager` -- 管理媒体播放器在快捷设置/通知/锁屏之间的位置
- `SeekBarViewModel` / `SeekBarObserver` -- 进度条的 MVVM 模式
- `MediaResumeListener` -- 可恢复媒体会话跟踪

#### 气泡（`bubbles/`）
- `BubbleController` -- 气泡管理的主控制器
- `BubbleData` -- 活跃气泡的数据模型
- `BubbleExpandedView` -- 展开的气泡视图
- `BubbleDataRepository`（Kotlin）-- 持久化层

### 2.8 SystemUI AndroidManifest 权限

SystemUI 在其清单中声明了超过 100 个权限，包括特权系统权限：

| 类别 | 示例权限 |
|----------|-------------------|
| 状态栏 | `STATUS_BAR_SERVICE`、`STATUS_BAR`、`EXPAND_STATUS_BAR` |
| 网络 | `BLUETOOTH_PRIVILEGED`、`NETWORK_SETTINGS`、`TETHER_PRIVILEGED` |
| 硬件 | `DEVICE_POWER`、`CONTROL_DISPLAY_BRIGHTNESS`、`MANAGE_USB` |
| Activity 管理 | `REAL_GET_TASKS`、`REMOVE_TASKS`、`START_ACTIVITIES_FROM_BACKGROUND` |
| 用户管理 | `MANAGE_USERS`、`INTERACT_ACROSS_USERS_FULL` |
| 安全 | `MANAGE_BIOMETRIC`、`CONTROL_KEYGUARD_SECURE_NOTIFICATIONS` |

`coreApp="true"` 标志和 `android.uid.systemui` 共享 UID 赋予 SystemUI 第三方应用无法获得的提升权限。

---

## 3. 设置应用架构

**源码位置：** `packages/apps/Settings/`

### 3.1 应用结构概览

设置应用使用基于 Fragment 的架构，有两个入口点：

1. **SettingsHomepageActivity**（`FragmentActivity`）-- 设置主界面
   - 在 `main_content` 容器中承载 `TopLevelSettings` Fragment
   - 承载 `ContextualCardsFragment` 用于上下文建议（仅限高内存设备）
   - 使用 `AvatarViewMixin` 作为用户头像的生命周期观察者

2. **SettingsActivity**（`SettingsBaseActivity`）-- 承载子设置界面
   - 支持 `EXTRA_SHOW_FRAGMENT` 以直接启动特定偏好设置 Fragment
   - 使用 `FragmentManager.OnBackStackChangedListener` 管理 Fragment 返回栈
   - 通过 `WizardManagerHelper` 与设置向导集成

### 3.2 DashboardFragment 模式

**关键文件：** `dashboard/DashboardFragment.java`

`DashboardFragment` 是大多数设置界面的基类。它继承 `SettingsPreferenceFragment` 并引入了双源偏好设置系统：

1. **静态偏好设置** -- 在 XML 中定义（`getPreferenceScreenResId()`）
2. **动态偏好设置** -- 在运行时从 `DashboardCategory` 磁贴注入

偏好设置控制器加载是一个关键模式：
```java
// 在 DashboardFragment.onAttach() 中：
// 1. 从代码加载控制器
List<AbstractPreferenceController> controllersFromCode = createPreferenceControllers(context);
// 2. 从 XML 加载控制器（android:controller 属性）
List<BasePreferenceController> controllersFromXml =
    PreferenceControllerListHelper.getPreferenceControllersFromXml(context, getPreferenceScreenResId());
// 3. 过滤重复项，合并，并绑定到偏好设置
```

### 3.3 BasePreferenceController 层次结构

**关键文件：** `core/BasePreferenceController.java`

控制器层次结构提供了 UI 逻辑与偏好设置显示的清晰分离：

```
AbstractPreferenceController (settingslib)
    |
    +-- BasePreferenceController (实现 Sliceable)
        |
        +-- TogglePreferenceController  -- 用于布尔设置
        +-- SliderPreferenceController   -- 用于滑块/拖动条设置
        +-- LiveDataController           -- 用于 LiveData 支持的设置
```

`BasePreferenceController` 定义了控制可见性和可搜索性的可用状态常量：
- `AVAILABLE` (0) -- 可见且可搜索
- `AVAILABLE_UNSEARCHABLE` (1) -- 可见但不可搜索
- `CONDITIONALLY_UNAVAILABLE` (2) -- 暂时隐藏
- `UNSUPPORTED_ON_DEVICE` (3) -- 在此设备上永不可用
- `DISABLED_FOR_USER` (4) -- 被用户限制阻止
- `DISABLED_DEPENDENT_SETTING` (5) -- 被依赖设置阻止

**TogglePreferenceController** 演示了模板方法模式：
```java
public abstract class TogglePreferenceController extends BasePreferenceController {
    public abstract boolean isChecked();       // 读取当前状态
    public abstract boolean setChecked(boolean isChecked);  // 写入状态
    // updateState() 自动将 UI 与 isChecked() 同步
}
```

### 3.4 FeatureFactory 模式

**关键文件：** `overlay/FeatureFactory.java`

设置使用抽象工厂模式进行 OEM 定制。`FeatureFactory` 在运行时从资源配置的类名（`R.string.config_featureFactory`）加载：

```java
sFactory = (FeatureFactory) context.getClassLoader()
    .loadClass(clsName).newInstance();
```

功能提供者包括：
- `DashboardFeatureProvider` -- 仪表盘磁贴处理
- `SearchFeatureProvider` -- 设置搜索
- `MetricsFeatureProvider` -- 分析日志记录
- `SecurityFeatureProvider` -- 安全设置
- `PowerUsageFeatureProvider` -- 电池统计
- `SuggestionFeatureProvider` -- 设置建议
- `PanelFeatureProvider` -- 设置面板（Android 10+）
- `ContextualCardFeatureProvider` -- 上下文卡片

**对应用开发者的启示：** 此模式支持清晰的产品线工程，OEM 可以在不分叉代码库的情况下覆盖行为。

### 3.5 设置 Slices

**关键文件：**
- `slices/SettingsSliceProvider.java`
- `slices/SliceData.java`
- `slices/SlicesDatabaseHelper.java`

设置实现了 `SliceProvider`，将设置项作为 Android Slices 暴露，以便在其他应用中内联显示：

1. `SlicesIndexer` 将所有 `BasePreferenceController` 实例索引到 SQLite 数据库中
2. `SettingsSliceProvider` 通过 URI 键查找控制器来提供 Slice 内容
3. `SliceBroadcastReceiver` 处理用户在 Slices 上的操作
4. 控制器实现 `Sliceable` 以定义 Slice 类型和后台工作器

Slice 管道：
```
SliceProvider.onBindSlice(uri)
    -> SlicesDatabaseAccessor.getSliceDataFromUri(uri)
    -> SliceBuilderUtils.buildSlice(sliceData)
    -> BasePreferenceController (处理开关/滑块操作)
```

### 3.6 搜索索引

**关键文件：** `search/BaseSearchIndexProvider.java`

设置为每个偏好设置界面实现了 `SearchIndexableResource`，以支持设置搜索功能：

```java
@SearchIndexable(forTarget = MOBILE)
public class TopLevelSettings extends DashboardFragment {
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        new BaseSearchIndexProvider(R.xml.top_level_settings);
}
```

`BaseSearchIndexProvider` 自动完成以下工作：
- 解析 XML 偏好设置界面以获取可索引的键
- 通过控制器的 `getAvailabilityStatus()` 过滤不可索引的键
- 支持动态原始数据索引

### 3.7 设置清单要点

设置应用使用 `android.uid.system` 共享 UID 并声明了 100 多个权限。值得注意的模式：

- 多个 `<activity-alias>` 条目用于不同入口点（WiFi 设置、蓝牙等）
- 使用 `android:exported="true"` 配合特定的 Intent 过滤器
- `READ_SEARCH_INDEXABLES` 权限用于搜索集成
- `WRITE_SECURE_SETTINGS` 用于修改系统设置

---

## 4. Launcher3 架构

**源码位置：** `packages/apps/Launcher3/`

### 4.1 核心架构

Launcher3 使用 MVC 变体配合状态机：

```
LauncherAppState (单例) -- 持有应用级状态
    -> LauncherModel -- 数据模型，后台加载
    -> IconCache -- 应用图标缓存
    -> InvariantDeviceProfile -- 设备配置计算
    -> WidgetPreviewLoader -- 小部件预览缓存

Launcher (StatefulActivity) -- 主 Activity
    -> StateManager<LauncherState> -- 状态机
    -> DragController -- 拖放
    -> Workspace (ViewGroup) -- 主屏幕页面
    -> AllAppsContainerView -- 应用抽屉
    -> Hotseat -- 底部快捷栏
```

### 4.2 状态机

**关键文件：**
- `statemanager/StateManager.java`
- `LauncherState.java`

Launcher3 的状态管理是一个精密的有限状态机。`StateManager<STATE_TYPE>` 管理状态之间的转换，并带有动画属性变化：

**定义的状态：**
| 状态 | 序号 | 描述 |
|-------|---------|-------------|
| `NORMAL` | 0 | 主屏幕，工作区可见 |
| `SPRING_LOADED` | 1 | 拖放放置模式 |
| `ALL_APPS` | 2 | 应用抽屉打开 |
| `OVERVIEW` | 3 | 最近应用视图 |
| `OVERVIEW_PEEK` | 4 | 长按 Home 键时的概览提示 |
| `BACKGROUND_APP` | 5 | 应用在前台 |
| `HINT` | 6 | 导航提示状态 |
| `QUICK_SWITCH` | 7 | 快速切换手势 |
| `OVERVIEW_MODAL_TASK` | 8 | 概览中的模态任务 |

每个 `LauncherState` 使用位掩码定义可见性标志：
```java
public static final int HOTSEAT_ICONS = 1 << 0;
public static final int ALL_APPS_CONTENT = 1 << 4;
public static final int OVERVIEW_BUTTONS = 1 << 6;
```

以及行为标志：
```java
public static final int FLAG_MULTI_PAGE = BaseState.getFlag(0);
public static final int FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED = BaseState.getFlag(2);
public static final int FLAG_CLOSE_POPUPS = BaseState.getFlag(6);
```

`StateHandler` 实现在转换期间为组件（工作区、所有应用、概览）添加动画。

### 4.3 LauncherModel -- 数据加载

**关键文件：** `LauncherModel.java`

`LauncherModel` 继承 `LauncherApps.Callback` 以接收包变更事件，并管理所有数据加载：

- `LoaderTask` 在 `MODEL_EXECUTOR`（后台线程）上运行，从数据库加载工作区项目、所有应用、小部件和快捷方式
- `BgDataModel` 持有所有内存数据：`itemsIdMap`、`workspaceItems`、`appWidgets`、`folders`
- `Callbacks` 接口将加载的数据推送回 UI 线程
- 模型更新任务（`BaseModelUpdateTask`、`PackageUpdatedTask`、`CacheDataUpdatedTask`）增量处理变更

线程模型：
```
MODEL_EXECUTOR (后台) -- 数据加载、数据库操作
MAIN_EXECUTOR (UI 线程) -- 回调传递、UI 更新
```

### 4.4 LauncherProvider -- 内容提供者

**关键文件：** `LauncherProvider.java`

`LauncherProvider` 是一个管理启动器数据库（架构版本 28）的 `ContentProvider`：

- 在 `favorites` 表中存储工作区项目（快捷方式、文件夹、小部件）
- 支持批量 `ContentProviderOperation` 以实现原子化工作区更新
- 当设备配置文件变更时实现网格迁移（`GridSizeMigrationTask`）
- 通过 `BackupManager` 通知处理备份/恢复
- `LauncherDbUtils` 提供实用方法，包括用于安全事务的 `SQLiteTransaction`

数据库架构包括：`_id`、`title`、`intent`、`container`、`screen`、`cellX`、`cellY`、`spanX`、`spanY`、`itemType`、`appWidgetId`、`appWidgetProvider`、`profileId` 等。

### 4.5 小部件系统

**`widget/` 中的关键文件：**
- `WidgetsFullSheet.java` -- 全屏小部件选择器
- `WidgetsListAdapter.java` -- 小部件列表适配器
- `WidgetCell.java` -- 单个小部件预览单元格
- `LauncherAppWidgetHostView.java` -- 承载小部件视图
- `WidgetManagerHelper.java` -- `AppWidgetManager` 的包装器
- `WidgetHostViewLoader.java` -- 异步小部件视图膨胀

小部件生命周期：
```
用户选择小部件 -> 创建 PendingAddWidgetInfo
    -> WidgetAddFlowHandler.startBindFlow()
    -> AppWidgetHost.allocateAppWidgetId()
    -> AppWidgetManager.bindAppWidgetIdIfAllowed()
    -> LauncherAppWidgetHostView 添加到工作区
    -> ModelWriter 持久化到数据库
```

### 4.6 Launcher3 清单

```xml
<activity android:name="com.android.launcher3.Launcher"
    android:launchMode="singleTask"
    android:clearTaskOnLaunch="true"
    android:stateNotNeeded="true"
    android:taskAffinity=""
    android:resumeWhilePausing="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.HOME" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```

值得注意的属性：
- `singleTask` 启动模式 -- 只有一个实例
- `clearTaskOnLaunch` -- 返回时恢复初始状态
- `stateNotNeeded` -- 不需要状态保存（模型从数据库重新加载）
- `taskAffinity=""` -- 与已启动应用分开的任务
- `resumeWhilePausing` -- 更快地返回主屏幕

### 4.7 插件系统

Launcher3 与 SystemUI 的插件系统集成：
```java
public class Launcher extends StatefulActivity<LauncherState>
    implements PluginListener<OverlayPlugin> { ... }
```

这允许通过 `LauncherOverlayManager` 和 `OverlayPlugin` 接口进行动态 UI 扩展（例如 Google Feed 面板），无需修改启动器二进制文件。

---

## 5. 其他内置应用

### 5.1 通讯录应用

**源码位置：** `packages/apps/Contacts/`

架构：
- `ContactsApplication` -- 自定义 Application 类，包含 `ContactPhotoManager` 初始化
- `PeopleActivity` -- 带有联系人列表的主 Activity（基于标签页的 UI）
- **Loader 模式**：广泛使用 `CursorLoader` 和 `AsyncTaskLoader` 进行 `ContactsProvider2` 查询
- `ContactSaveService` -- 用于后台保存操作的 `IntentService`
- `DynamicShortcuts` -- 为常用联系人发布启动器快捷方式
- 目录结构遵循基于功能的组织方式：`activities/`、`editor/`、`detail/`、`list/`、`group/`、`quickcontact/`

关键 API 使用：
- `ContactsContract` 内容提供者用于所有联系人数据
- `AccountManager` 用于多账户支持
- NFC beam 用于联系人共享（`NfcHandler`）
- `ShortcutManager` 用于动态快捷方式

### 5.2 DocumentsUI

**源码位置：** `packages/apps/DocumentsUI/`

存储访问框架的参考实现：
- `AbstractActionHandler` / `ActionHandler` -- 用户操作的命令模式
- `DirectoryLoader` -- 目录内容的 `AsyncTaskLoader`
- `DocsSelectionHelper` -- 多选支持
- `DragAndDropManager` -- 跨应用拖放
- 实现了选择器和文件管理器两种模式

### 5.3 日历、Camera2、Gallery2

这些应用展示了标准的 Android 模式：
- **日历**：对 `CalendarContract` 的内容提供者查询、同步适配器模式
- **Camera2**：Camera2 API 的使用，包括 `CameraCaptureSession`、基于 Surface 的预览
- **Gallery2**：`MediaStore` 内容提供者集成、位图内存管理

---

## 6. AOSP 应用中的通用模式

### 6.1 架构模式

| 模式 | 使用位置 | 描述 |
|---------|---------|-------------|
| **依赖注入（Dagger）** | SystemUI | 完整的 Dagger 2 组件层次结构 |
| **服务定位器** | SystemUI（遗留） | `Dependency.java` -- 正在迁移到 DI |
| **抽象工厂** | 设置 | `FeatureFactory` 用于 OEM 定制 |
| **状态机** | Launcher3 | `StateManager` 配合 `LauncherState` |
| **控制器模式** | 设置 | `BasePreferenceController` 层次结构 |
| **观察者/回调** | 所有应用 | 广泛使用监听器接口 |
| **模板方法** | 设置、SystemUI | `TogglePreferenceController`、`QSTileImpl` |
| **命令模式** | DocumentsUI | `ActionHandler` 用于用户操作 |
| **MVC 变体** | Launcher3 | `LauncherModel` + `Launcher` + Views |

### 6.2 线程模式

所有 AOSP 应用都遵循严格的线程规范：

- **命名执行器**：`MODEL_EXECUTOR`、`MAIN_EXECUTOR`（Launcher3）、`BG_LOOPER`（SystemUI）
- **后台 Handler**：用于特定子系统的自定义 `Handler`/`Looper` 对
- **AsyncTask**：在设置和较旧代码中仍在使用（在 API 30 中已弃用）
- **ThreadUtils**：settingslib 中用于执行器管理的工具类
- **@WorkerThread / @UiThread 注解**：用于强制执行线程约束

### 6.3 内容提供者模式

| 应用 | 提供者 | URI Authority | 用途 |
|-----|----------|---------------|---------|
| Launcher3 | `LauncherProvider` | `com.android.launcher3.settings` | 工作区持久化 |
| 设置 | `SettingsSliceProvider` | `com.android.settings.slices` | 设置作为 Slices |
| 设置 | 搜索索引 | `com.android.settings` | 搜索结果 |

AOSP 中常见的内容提供者模式：
- `SQLiteTransaction` 包装器用于安全事务处理（Launcher3）
- `ContentProviderOperation` 批量操作用于原子性
- `ContentObserver` 用于变更通知
- 基于 URI 的路由，使用 `UriMatcher`

### 6.4 生命周期集成

AOSP 应用展示了 AndroidX 生命周期的渐进式采用：

- **SystemUI QSTileImpl**：使用 `LifecycleRegistry` 实现 `LifecycleOwner`，用于生命周期感知的组件观察
- **设置**：`DashboardFragment` 使用 settingslib 中的 `Lifecycle` 进行控制器生命周期管理；`SettingsHomepageActivity` 使用 `getLifecycle().addObserver()` 处理 mixin
- **Launcher3**：通过 `StateManager.StateListener` 自定义状态生命周期

### 6.5 配置变更处理

```xml
<!-- Launcher3 方法：在进程内处理所有变更 -->
android:configChanges="keyboard|keyboardHidden|mcc|mnc|navigation|
    orientation|screenSize|screenLayout|smallestScreenSize"
```

- Launcher3 自行处理大多数配置变更以避免重建
- 设置使用标准的 Activity 重建配合 Fragment 保存/恢复
- SystemUI 通过 `ConfigurationController` 向所有监听器广播来处理配置变更

### 6.6 权限模式

系统应用使用 `android:sharedUserId` 机制获取提升的访问权限：

| 应用 | 共享 UID | 效果 |
|-----|-----------|--------|
| SystemUI | `android.uid.systemui` | SystemUI 级别权限 |
| 设置 | `android.uid.system` | 系统级别权限 |
| Launcher3 | （无） | 标准应用权限 |
| 通讯录 | （无） | 标准 + 危险权限 |

系统应用可以使用第三方开发者无法获得的 `@SystemApi` 和 `@hide` API。值得注意的是 Launcher3 主要使用标准应用权限运行，使其成为第三方开发者更好的参考。

---

## 7. 面向应用开发者的关键 API 使用模式

### 7.1 来自 SystemUI

- **自定义 View 与 WindowManager**：SystemUI 通过 `WindowManager.addView()` 使用 `TYPE_STATUS_BAR` 直接向窗口添加视图 -- 展示了系统覆盖层 UI 的工作方式
- **通知监听器**：`NotificationListenerService` 集成用于处理通知
- **MediaSession 观察**：媒体控制展示了 `MediaController` 和 `MediaSession.Callback` 的使用
- **生命周期感知组件**：快捷设置磁贴展示了如何构建自动清理资源的生命周期感知控制器

### 7.2 来自设置

- **基于偏好设置的 UI**：`DashboardFragment` + `BasePreferenceController` 是设置界面的标准模式
- **SliceProvider**：`SettingsSliceProvider` 展示了如何将应用数据作为 Slices 暴露
- **搜索索引**：`BaseSearchIndexProvider` 演示了如何使内容可搜索
- **从 XML 延迟绑定**：通过 `android:controller` 属性在偏好设置 XML 中声明控制器

### 7.3 来自 Launcher3

- **AppWidgetHost**：完整的小部件托管实现，包括分配、绑定和视图管理
- **LauncherApps API**：`LauncherModel` 展示了 `LauncherApps.Callback` 的正确使用方法，用于包监控
- **ShortcutManager**：快捷方式固定、查询和显示
- **拖放**：完整的拖放框架，包含 `DragController`、`DragLayer`、`DropTarget`
- **带迁移的内容提供者**：`LauncherProvider` 演示了数据库架构版本控制和网格迁移

### 7.4 来自通讯录

- **ContentResolver 模式**：高效使用 `CursorLoader` 配合 `ContactsContract`
- **IntentService 用于写入**：`ContactSaveService` 用于后台数据持久化
- **动态快捷方式**：`DynamicShortcuts` 用于常联系人

---

## 8. 架构观察与要点总结

### 8.1 模式演进

代码库展示了跨 Android 版本的明显演进：
- **旧模式**：`AsyncTask`、手动线程管理、服务定位器（`Dependency.java`）
- **当前模式**：Dagger 注入、Kotlin 协程/流（在媒体代码中）、生命周期感知组件
- **混合状态**：某些类（例如 4,400 多行的 `StatusBar.java`）是正在逐步分解的遗留单体

### 8.2 代码组织

- **基于功能的包结构**：设置按功能组织（`wifi/`、`bluetooth/`、`notification/`）
- **基于层的包结构**：SystemUI 按架构层组织（`statusbar/`、`qs/`、`keyguard/`）
- **混合方式**：Launcher3 两种都使用（`model/`、`widget/`、`allapps/`）

### 8.3 可测试性考虑

- `@VisibleForTesting` 注解在所有应用中被广泛使用
- SystemUI 的 Dagger 设置支持测试中的依赖替换
- 设置的 `FeatureFactory` 和控制器模式支持模拟
- Launcher3 的 `TestProtocol` 类定义了 UI 自动化测试的常量

### 8.4 关注领域

- 4,400 多行的 `StatusBar.java` 违反了单一职责原则（正在通过 `StatusBarComponent` 进行分解）
- `Dependency.java` 服务定位器是已知的反模式（正在迁移到 Dagger）
- SystemUI 中 Java 和 Kotlin 的混合可能导致摩擦（media 包是 Kotlin，大多数其他包是 Java）
- 设置中的一些控制器仍然使用已弃用的 `AsyncTask`

---

## 9. 文件参考索引

### SystemUI
| 文件 | 路径 |
|------|------|
| 应用引导启动 | `frameworks/base/packages/SystemUI/src/com/android/systemui/SystemUIApplication.java` |
| 基础 SystemUI 类 | `frameworks/base/packages/SystemUI/src/com/android/systemui/SystemUI.java` |
| 状态栏 | `frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/phone/StatusBar.java` |
| 通知面板 | `frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/phone/NotificationPanelViewController.java` |
| 快捷设置磁贴宿主 | `frameworks/base/packages/SystemUI/src/com/android/systemui/qs/QSTileHost.java` |
| 快捷设置磁贴基类 | `frameworks/base/packages/SystemUI/src/com/android/systemui/qs/tileimpl/QSTileImpl.java` |
| Dagger 根组件 | `frameworks/base/packages/SystemUI/src/com/android/systemui/dagger/SystemUIRootComponent.java` |
| Dagger 模块 | `frameworks/base/packages/SystemUI/src/com/android/systemui/dagger/SystemUIModule.java` |
| Keyguard 中介 | `frameworks/base/packages/SystemUI/src/com/android/systemui/keyguard/KeyguardViewMediator.java` |
| 遗留 DI | `frameworks/base/packages/SystemUI/src/com/android/systemui/Dependency.java` |
| 设备控制 | `frameworks/base/packages/SystemUI/src/com/android/systemui/controls/controller/ControlsController.kt` |
| 媒体控制 | `frameworks/base/packages/SystemUI/src/com/android/systemui/media/MediaControlPanel.java` |
| 清单 | `frameworks/base/packages/SystemUI/AndroidManifest.xml` |

### 设置
| 文件 | 路径 |
|------|------|
| 主页 | `packages/apps/Settings/src/com/android/settings/homepage/SettingsHomepageActivity.java` |
| 顶级设置 | `packages/apps/Settings/src/com/android/settings/homepage/TopLevelSettings.java` |
| Settings Activity | `packages/apps/Settings/src/com/android/settings/SettingsActivity.java` |
| Dashboard Fragment | `packages/apps/Settings/src/com/android/settings/dashboard/DashboardFragment.java` |
| 基础控制器 | `packages/apps/Settings/src/com/android/settings/core/BasePreferenceController.java` |
| 开关控制器 | `packages/apps/Settings/src/com/android/settings/core/TogglePreferenceController.java` |
| 功能工厂 | `packages/apps/Settings/src/com/android/settings/overlay/FeatureFactory.java` |
| Slice 提供者 | `packages/apps/Settings/src/com/android/settings/slices/SettingsSliceProvider.java` |
| 搜索索引 | `packages/apps/Settings/src/com/android/settings/search/BaseSearchIndexProvider.java` |
| 清单 | `packages/apps/Settings/AndroidManifest.xml` |

### Launcher3
| 文件 | 路径 |
|------|------|
| 主 Activity | `packages/apps/Launcher3/src/com/android/launcher3/Launcher.java` |
| 应用状态 | `packages/apps/Launcher3/src/com/android/launcher3/LauncherAppState.java` |
| 数据模型 | `packages/apps/Launcher3/src/com/android/launcher3/LauncherModel.java` |
| 后台数据模型 | `packages/apps/Launcher3/src/com/android/launcher3/model/BgDataModel.java` |
| 状态管理器 | `packages/apps/Launcher3/src/com/android/launcher3/statemanager/StateManager.java` |
| 启动器状态 | `packages/apps/Launcher3/src/com/android/launcher3/LauncherState.java` |
| 内容提供者 | `packages/apps/Launcher3/src/com/android/launcher3/LauncherProvider.java` |
| 数据库工具 | `packages/apps/Launcher3/src/com/android/launcher3/provider/LauncherDbUtils.java` |
| 清单 | `packages/apps/Launcher3/AndroidManifest.xml` |

### 其他应用
| 文件 | 路径 |
|------|------|
| 通讯录清单 | `packages/apps/Contacts/AndroidManifest.xml` |

所有路径相对于 `~/aosp-android-11/`。
