# 代码审查报告 06：Android 11 系统服务

## 目录
1. [概述与架构](#1-概述与架构)
2. [SystemServer 启动序列](#2-systemserver-启动序列)
3. [ServiceManager 和 Binder IPC 桥接](#3-servicemanager-和-binder-ipc-桥接)
4. [ActivityManagerService (AMS)](#4-activitymanagerservice-ams)
5. [PackageManagerService (PMS)](#5-packagemanagerservice-pms)
6. [WindowManagerService (WMS)](#6-windowmanagerservice-wms)
7. [LocationManager / LocationManagerService](#7-locationmanager--locationmanagerservice)
8. [SensorManager](#8-sensormanager)
9. [Camera / CameraManager](#9-camera--cameramanager)
10. [无障碍服务](#10-无障碍服务)
11. [NotificationListenerService](#11-notificationlistenerservice)
12. [服务注册和访问模式](#12-服务注册和访问模式)
13. [权限检查和安全执行](#13-权限检查和安全执行)
14. [隐藏和系统 API](#14-隐藏和系统-api)
15. [关键发现和建议](#15-关键发现和建议)

---

## 1. 概述与架构

Android 11 系统服务构成了平台的骨干，提供应用程序与操作系统交互所使用的 API。其架构遵循严格的客户端-服务器模型，通过 Binder IPC 进行中介：

```
App Process                          system_server Process
+-------------------+                +----------------------------+
| ActivityManager   |  --- Binder -> | ActivityManagerService     |
| PackageManager    |  --- Binder -> | PackageManagerService      |
| WindowManager     |  --- Binder -> | WindowManagerService       |
| LocationManager   |  --- Binder -> | LocationManagerService     |
| SensorManager     |  --- Binder -> | SensorService (native)     |
| CameraManager     |  --- Binder -> | CameraService (native)     |
+-------------------+                +----------------------------+
```

**关键原则**：应用开发者与"Manager"类（客户端代理）交互，这些类通过 AIDL 生成的 Binder 接口与运行在 `system_server` 进程中的实际服务实现通信。

### 关键源代码目录
- **服务实现**：`frameworks/base/services/core/java/com/android/server/`
- **客户端服务 API**：`frameworks/base/core/java/android/service/`
- **硬件抽象**：`frameworks/base/core/java/android/hardware/`
- **系统启动**：`frameworks/base/services/java/com/android/server/SystemServer.java`

---

## 2. SystemServer 启动序列

**文件**: `frameworks/base/services/java/com/android/server/SystemServer.java`（2,567 行）

### 2.1 入口点

系统服务器是 Zygote 之后启动的第一个托管进程。其入口点为：

```java
// Line 413
public static void main(String[] args) {
    new SystemServer().run();
}
```

### 2.2 `run()` 方法（第 436 行）

`run()` 方法执行完整的启动序列：

1. **初始化**（第 436-588 行）：设置时区，配置 Binder 线程，准备主循环器，加载原生库（`android_servers`），创建系统上下文。

2. **Binder 线程配置**（第 528 行）：
   ```java
   BinderInternal.setMaxThreads(sMaxBinderThreads); // 31 threads
   ```

3. **SystemServiceManager 创建**（第 562 行）：
   ```java
   mSystemServiceManager = new SystemServiceManager(mSystemContext);
   ```

4. **三阶段服务启动**（第 594-604 行）：
   ```java
   startBootstrapServices(t);  // Line 596 - 关键的相互依赖服务
   startCoreServices(t);       // Line 597 - 必需但较少纠缠的服务
   startOtherServices(t);      // Line 598 - 其他所有服务
   ```

5. **主循环**（第 628 行）：
   ```java
   Looper.loop(); // 永不返回
   ```

### 2.3 引导服务（第 710 行）

由于复杂的相互依赖关系，这些服务首先启动：

| 顺序 | 服务 | 行号 | 用途 |
|-------|---------|------|---------|
| 1 | Watchdog | 716 | 死锁检测 |
| 2 | PlatformCompat | 729 | API 兼容性 |
| 3 | FileIntegrityService | 740 | 文件完整性验证 |
| 4 | Installer | 747 | 包安装守护进程 |
| 5 | DeviceIdentifiersPolicyService | 753 | 设备 ID 访问控制 |
| 6 | UriGrantsManagerService | 758 | URI 权限授予 |
| 7 | **ActivityTaskManagerService** | 764 | Activity/任务管理 |
| 8 | **ActivityManagerService** | 766 | 进程/组件生命周期 |
| 9 | DataLoaderManagerService | 775 | 数据加载 |
| 10 | **PowerManagerService** | 789 | 电源管理 |
| 11 | ThermalManagerService | 793 | 温度监控 |
| 12 | RecoverySystemService | 804 | OTA 恢复 |
| 13 | LightsService | 815 | LED/背光控制 |
| 14 | **DisplayManagerService** | 828 | 显示管理 |
| 15 | **PackageManagerService** | 857 | 包管理 |
| 16 | UserManagerService | 898 | 多用户支持 |
| 17 | OverlayManagerService | 923 | 运行时资源覆盖 |
| 18 | SensorPrivacyService | 927 | 传感器访问隐私 |

**关键依赖**：AMS 在 PMS 之前启动。PMS 要求显示就绪（第 833 行：`PHASE_WAIT_FOR_DEFAULT_DISPLAY`）。

### 2.4 核心服务（第 953 行）

| 服务 | 行号 | 用途 |
|---------|------|---------|
| BatteryService | 963 | 电池电量跟踪 |
| UsageStatsService | 968 | 应用使用统计 |
| WebViewUpdateService | 976 | WebView 就绪状态 |
| BinderCallsStatsService | 987 | Binder 调用分析 |
| RollbackManagerService | 997 | APK 回滚管理 |
| BugreportManagerService | 1002 | Bug 报告捕获 |
| GpuService | 1007 | GPU 驱动管理 |

### 2.5 其他服务（第 1016 行）

这是最大的阶段，启动 80 多个服务，包括：

- **WindowManagerService**（第 1163 行）
- **InputManagerService**（第 1156 行）
- **NotificationManagerService**（第 1581 行）
- **LocationManagerService**（通过生命周期）
- **AccessibilityManagerService**（第 1277 行）
- **ConnectivityService**（第 1541 行）
- **AudioService**（第 1641 行）
- **CameraServiceProxy**（第 2058 行）
- **BiometricService**（第 1959 行）
- 以及更多...

### 2.6 启动阶段

定义在 `frameworks/base/services/core/java/com/android/server/SystemService.java` 中：

| 常量 | 值 | 含义 |
|----------|-------|---------|
| `PHASE_WAIT_FOR_DEFAULT_DISPLAY` | 100 | 显示就绪 |
| `PHASE_LOCK_SETTINGS_READY` | 480 | 锁定设置可用 |
| `PHASE_SYSTEM_SERVICES_READY` | 500 | 核心服务可操作 |
| `PHASE_DEVICE_SPECIFIC_SERVICES_READY` | 520 | OEM 服务就绪 |
| `PHASE_ACTIVITY_MANAGER_READY` | 550 | AMS 完全可操作 |
| `PHASE_THIRD_PARTY_APPS_CAN_START` | 600 | 可安全启动应用 |
| `PHASE_BOOT_COMPLETED` | 1000 | 启动完全完成 |

### 2.7 systemReady 回调（第 2241 行）

所有服务启动后，`AMS.systemReady()` 触发一个回调，该回调：
1. 启动 `PHASE_ACTIVITY_MANAGER_READY` 启动阶段（第 2244 行）
2. 启动 SystemUI（第 2278 行）
3. 使网络服务就绪（第 2297-2346 行）
4. 启动 `PHASE_THIRD_PARTY_APPS_CAN_START` 阶段（第 2358 行）
5. 启动网络栈（第 2368 行）

---

## 3. ServiceManager 和 Binder IPC 桥接

### 3.1 ServiceManager

**文件**: `frameworks/base/core/java/android/os/ServiceManager.java`

ServiceManager 是所有系统服务的中央注册表。它是一个隐藏 API（`@hide`），应用不能直接调用。

**关键方法**：

```java
// Line 165 - 注册一个服务
public static void addService(String name, IBinder service)

// Line 194 - 带选项注册
public static void addService(String name, IBinder service,
    boolean allowIsolated, int dumpPriority)

// Line 128 - 获取一个服务（阻塞）
public static IBinder getService(String name)

// Line 208 - 获取一个服务（非阻塞）
public static IBinder checkService(String name)

// Line 245 - 等待服务变为可用（原生）
public static native IBinder waitForService(@NonNull String name)

// Line 266 - 列出所有运行中的服务
public static String[] listServices()
```

**服务缓存**（第 42 行）：
```java
private static Map<String, IBinder> sCache = new ArrayMap<String, IBinder>();
```
知名服务（如 WM 和 AM）被缓存以避免重复查找。

**IServiceManager 连接**（第 110 行）：
```java
private static IServiceManager getIServiceManager() {
    sServiceManager = ServiceManagerNative
        .asInterface(Binder.allowBlocking(BinderInternal.getContextObject()));
    return sServiceManager;
}
```
原生的 `getContextObject()` 返回内核级服务管理器（上下文管理器）的句柄。

### 3.2 Binder IPC

**文件**: `frameworks/base/core/java/android/os/Binder.java`

Binder 是核心 IPC 机制。`Binder` 类（第 78 行）实现 `IBinder`。

**事务流程**：
1. 客户端调用代理上的方法（例如 `IActivityManager.Stub.Proxy`）
2. 代理将参数序列化到 `Parcel` 中，调用 `transact()`
3. 内核 Binder 驱动将事务传递到服务器进程
4. 服务端的 `execTransact()`（第 1116 行）被原生代码调用
5. `execTransactInternal()`（第 1129 行）反序列化并调用 `onTransact()`
6. Stub 实现分发到具体的服务方法

**关键安全模式**（在各服务中广泛使用）：
```java
final int callingUid = Binder.getCallingUid();
final int callingPid = Binder.getCallingPid();
final long origId = Binder.clearCallingIdentity();
try {
    // 执行特权操作
} finally {
    Binder.restoreCallingIdentity(origId);
}
```

**观察者接口**（第 682 行）：
```java
interface Observer {
    Object onTransactStarted(@NonNull IBinder binder, int transactionCode, int flags);
    void onTransactEnded(@Nullable Object session);
}
```
用于监控和分析 Binder 调用。

### 3.3 应用如何访问系统服务

应用不直接使用 `ServiceManager`。而是：

1. **Context.getSystemService()** 返回一个 Manager 对象
2. **SystemServiceRegistry** 将服务名称映射到工厂 lambda
3. 每个 Manager 内部持有对 AIDL 代理的引用

`LocationManager` 的示例流程：
```
context.getSystemService(Context.LOCATION_SERVICE)
  -> SystemServiceRegistry 查找 "location"
  -> 创建 LocationManager(context, ILocationManager.Stub.asInterface(
       ServiceManager.getServiceOrThrow(Context.LOCATION_SERVICE)))
```

---

## 4. ActivityManagerService (AMS)

**文件**: `frameworks/base/services/core/java/com/android/server/am/ActivityManagerService.java`（20,423 行）

### 4.1 类声明（第 416 行）
```java
public class ActivityManagerService extends IActivityManager.Stub
        implements Watchdog.Monitor, BatteryStatsImpl.BatteryCallback {
```

AMS 扩展 `IActivityManager.Stub` -- AIDL 接口的服务端。

### 4.2 用途和职责

AMS 是 Android 应用模型的中央管理器：
- **进程生命周期管理**：启动、停止、OOM 调整应用进程
- **广播分发**：有序和无序广播传递
- **内容提供者管理**：发布和解析内容提供者
- **服务管理**：启动、绑定和停止服务
- **测试框架**：测试框架集成
- **电池统计**：按 UID 跟踪电量使用

**注意**：在 Android 10+ 中，Activity/任务管理已移至 `ActivityTaskManagerService`（ATMS）。AMS 将 Activity 相关调用委托给 ATMS。

### 4.3 AIDL 接口

**文件**: `frameworks/base/core/java/android/app/IActivityManager.aidl`

通过 Binder 暴露的关键方法：

| 方法 | 用途 |
|--------|---------|
| `startActivity()` / `startActivityWithFeature()` | 启动 Activity |
| `broadcastIntent()` / `broadcastIntentWithFeature()` | 发送广播 |
| `startService()` / `bindService()` | 管理服务 |
| `getContentProvider()` | 解析内容提供者 |
| `getRunningAppProcesses()` | 列出运行中的进程 |
| `getProcessMemoryInfo()` | 进程内存诊断 |
| `unbindService()` | 释放服务连接 |

### 4.4 服务注册（第 2107 行）

```java
public void setSystemProcess() {
    ServiceManager.addService(Context.ACTIVITY_SERVICE, this,
        /* allowIsolated= */ true,
        DUMP_FLAG_PRIORITY_CRITICAL | DUMP_FLAG_PRIORITY_NORMAL | DUMP_FLAG_PROTO);
    ServiceManager.addService(ProcessStats.SERVICE_NAME, mProcessStats);
    ServiceManager.addService("meminfo", new MemBinder(this), false,
        DUMP_FLAG_PRIORITY_HIGH);
    ServiceManager.addService("gfxinfo", new GraphicsBinder(this));
    ServiceManager.addService("dbinfo", new DbBinder(this));
    ServiceManager.addService("cpuinfo", new CpuBinder(this), false,
        DUMP_FLAG_PRIORITY_CRITICAL);
    ServiceManager.addService("permission", new PermissionController(this));
    ServiceManager.addService("processinfo", new ProcessInfoService(this));
    ServiceManager.addService("cacheinfo", new CacheBinder(this));
}
```

AMS 注册自身和多个子服务，使用不同的名称。这展示了一种模式，即一个服务对象管理多个相关的服务端点。

### 4.5 生命周期（第 2331 行）

```java
public static final class Lifecycle extends SystemService {
    private final ActivityManagerService mService;
    private static ActivityTaskManagerService sAtm;

    public static ActivityManagerService startService(
            SystemServiceManager ssm, ActivityTaskManagerService atm) {
        sAtm = atm;
        return ssm.startService(ActivityManagerService.Lifecycle.class).getService();
    }

    @Override
    public void onStart() {
        mService.start();
    }
}
```

AMS 要求 ATMS 先启动。它们共享一个全局锁（`mWindowManagerGlobalLock`）。

### 4.6 关键常量

| 常量 | 值 | 用途 |
|----------|-------|---------|
| `PROC_START_TIMEOUT` | 10,000 ms | 进程附加的最大时间 |
| `BROADCAST_FG_TIMEOUT` | 10,000 ms | 前台广播超时 |
| `BROADCAST_BG_TIMEOUT` | 60,000 ms | 后台广播超时 |
| `MAX_RECEIVERS_ALLOWED_PER_APP` | 1,000 | 接收器注册限制 |
| `TOP_APP_PRIORITY_BOOST` | -10 | 顶部应用的线程优先级 |

### 4.7 权限执行

AMS 广泛执行权限检查。关键权限检查模式：

```java
// Line 6225
int checkCallingPermission(String permission) {
    return checkPermission(permission,
        Binder.getCallingPid(), UserHandle.getAppId(Binder.getCallingUid()));
}

// Line 6234
void enforceCallingPermission(String permission, String func) {
    if (checkCallingPermission(permission) == PackageManager.PERMISSION_GRANTED) {
        return;
    }
    // 抛出 SecurityException
}
```

常见的强制执行权限包括：
- `FORCE_STOP_PACKAGES`（第 3724、3739 行）
- `KILL_BACKGROUND_PROCESSES`（第 4349、4393、4431 行）
- `SET_PROCESS_LIMIT`（第 5917、5955 行）
- `SET_DEBUG_APP`（第 8402 行）
- `DUMP`（第 8652 行）
- `PACKAGE_USAGE_STATS`（第 3282、3291 行）

### 4.8 应用开发者交互

开发者使用 `ActivityManager`：
```java
ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
am.getRunningAppProcesses();
am.getMemoryInfo(memInfo);
am.clearApplicationUserData(packageName, observer);
```

---

## 5. PackageManagerService (PMS)

**文件**: `frameworks/base/services/core/java/com/android/server/pm/PackageManagerService.java`（25,775 行）

### 5.1 类声明（第 470 行）
```java
public class PackageManagerService extends IPackageManager.Stub
        implements PackageSender {
```

### 5.2 用途和职责

PMS 管理应用包的所有方面：
- **包扫描和解析**：读取 APK 清单
- **包安装/卸载**：包括验证
- **权限管理**：授予、撤销、检查权限
- **Intent 解析**：将 Intent 匹配到 Activity/Service/Receiver
- **包查询**：包信息、组件信息、功能查询
- **共享库管理**：系统共享库
- **DEX 优化**：触发 dexopt/dex2oat
- **签名验证**：APK 签名和证书管理

### 5.3 初始化（第 2568 行）

```java
public static PackageManagerService main(Context context, Installer installer,
        boolean factoryTest, boolean onlyCore) {
    PackageManagerServiceCompilerMapping.checkProperties();
    // 使用依赖项创建 Injector：UserManagerService,
    // PermissionManagerService, ComponentResolver, Settings
    Injector injector = new Injector(context, lock, installer, installLock, ...);
    PackageManagerService m = new PackageManagerService(injector, ...);
    ...
}
```

PMS 使用 `Injector` 模式进行依赖注入，便于测试。它创建：
- `ComponentResolver` - 将 Intent 解析到组件
- `PermissionManagerService` - 管理权限
- `UserManagerService` - 多用户支持
- `Settings` - 在 `/data/system/packages.xml` 中持久化包状态

### 5.4 AIDL 接口

**文件**: `frameworks/base/core/java/android/content/pm/IPackageManager.aidl`

关键方法：

| 方法 | 行号 | 用途 |
|--------|------|---------|
| `getPackageInfo()` | 70 | 完整的包元数据 |
| `getApplicationInfo()` | 83 | 应用级信息 |
| `getPackageUid()` | 74 | 包 UID 查找 |
| `queryIntentActivities()` | 136 | Intent 解析 |
| `checkPackageStartable()` | 66 | 可启动性检查 |
| `isPackageAvailable()` | 68 | 可用性检查 |

### 5.5 权限导入

PMS 导入并检查大量权限（第 19-108 行）：
- `DELETE_PACKAGES`、`INSTALL_PACKAGES`
- `MANAGE_DEVICE_ADMINS`、`MANAGE_PROFILE_AND_DEVICE_OWNERS`
- `QUERY_ALL_PACKAGES`（Android 11 新增——包可见性）
- `READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`
- `SET_HARMFUL_APP_WARNINGS`

### 5.6 应用开发者交互

```java
PackageManager pm = context.getPackageManager();
PackageInfo info = pm.getPackageInfo("com.example.app", 0);
List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
boolean granted = pm.checkPermission(permission, packageName) == PERMISSION_GRANTED;
```

### 5.7 包可见性（Android 11）

Android 11 通过 `QUERY_ALL_PACKAGES` 权限（第 23 行）引入了包可见性限制。应用必须在清单中声明 `<queries>` 才能看到其他包，除非持有此权限。

---

## 6. WindowManagerService (WMS)

**文件**: `frameworks/base/services/core/java/com/android/server/wm/WindowManagerService.java`（8,278 行）

### 6.1 类声明（第 317 行）
```java
public class WindowManagerService extends IWindowManager.Stub
        implements Watchdog.Monitor, WindowManagerPolicy.WindowManagerFuncs {
```

### 6.2 用途和职责

WMS 管理窗口系统：
- **窗口生命周期**：添加、移除、重新布局窗口
- **显示管理**：多显示器支持
- **输入分发**：将输入事件路由到正确的窗口
- **动画**：窗口过渡和动画
- **屏幕方向**：旋转管理
- **Surface 管理**：通过 SurfaceFlinger 进行合成

### 6.3 初始化（第 1104 行）

```java
public static WindowManagerService main(final Context context, final InputManagerService im,
        final boolean showBootMsgs, final boolean onlyCore, WindowManagerPolicy policy,
        ActivityTaskManagerService atm) {
    DisplayThread.getHandler().runWithScissors(() ->
        sInstance = new WindowManagerService(context, im, showBootMsgs, onlyCore, policy,
            atm, ...), 0);
    return sInstance;
}
```

WMS 在 `DisplayThread` 上创建（不是主线程）。`runWithScissors()` 调用会阻塞调用者，直到在目标线程上创建完成。

**构造函数**（第 1143 行）：使用 `InputManagerService`、`ActivityTaskManagerService`、`WindowManagerPolicy`（PhoneWindowManager）进行初始化，并设置与 ATMS 共享的全局锁。

### 6.4 服务注册（SystemServer 第 1165 行）

```java
ServiceManager.addService(Context.WINDOW_SERVICE, wm, false,
    DUMP_FLAG_PRIORITY_CRITICAL | DUMP_FLAG_PROTO);
```

### 6.5 AIDL 接口

**文件**: `frameworks/base/core/java/android/view/IWindowManager.aidl`

接口定义的关键操作：
- 窗口旋转控制（第 78-87 行的固定旋转常量）
- 显示折叠监听器管理
- 系统手势排除区域
- 壁纸可见性
- 固定堆栈（画中画）管理
- 滚动截取
- IME（输入法）控制

### 6.6 关键窗口操作

**addWindow()**（第 1374 行）：
```java
public int addWindow(Session session, IWindow client, int seq,
        LayoutParams attrs, int viewVisibility, int displayId, Rect outFrame,
        Rect outContentInsets, Rect outStableInsets,
        DisplayCutout.ParcelableWrapper outDisplayCutout, InputChannel outInputChannel,
        InsetsState outInsetsState, InsetsSourceControl[] outActiveControls,
        int requestUserId) {
    int res = mPolicy.checkAddPermission(attrs.type, isRoundedCornerOverlay,
        attrs.packageName, appOp);
    if (res != WindowManagerGlobal.ADD_OKAY) {
        return res;
    }
    // ...
}
```

通过 `WindowManagerPolicy.checkAddPermission()` 进行权限检查是第一个操作，验证调用者是否有权添加指定的窗口类型。

**relayoutWindow()**（第 2113 行）：处理带有 Surface 管理的窗口大小/位置变化。

### 6.7 权限要求

WMS 强制执行多种权限（第 19-28 行导入）：
- `INTERNAL_SYSTEM_WINDOW` - 用于系统级窗口类型
- `MANAGE_APP_TOKENS` - 令牌管理
- `MANAGE_ACTIVITY_STACKS` - Activity 栈控制
- `READ_FRAME_BUFFER` - 截屏能力
- `REGISTER_WINDOW_MANAGER_LISTENERS` - 事件监听器
- `STATUS_BAR_SERVICE` - 状态栏窗口管理
- `CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS` - 过渡动画
- `INPUT_CONSUMER` - 输入拦截

### 6.8 应用开发者交互

应用通过以下方式间接与 WMS 交互：
```java
WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
wm.addView(view, layoutParams);
wm.removeView(view);
wm.getDefaultDisplay().getMetrics(metrics);
```

---

## 7. LocationManager / LocationManagerService

### 7.1 客户端：LocationManager

**文件**: `frameworks/base/location/java/android/location/LocationManager.java`

**注解**（第 85-86 行）：
```java
@SystemService(Context.LOCATION_SERVICE)
@RequiresFeature(PackageManager.FEATURE_LOCATION)
public class LocationManager {
```

**面向开发者的关键 API**：

| 方法 | 所需权限 | 用途 |
|--------|-------------------|---------|
| `requestLocationUpdates()` | `ACCESS_FINE_LOCATION` 或 `ACCESS_COARSE_LOCATION` | 持续位置更新 |
| `getLastKnownLocation()` | 同上 | 最后缓存的位置 |
| `getCurrentLocation()` | 同上 | 一次性位置请求 |
| `requestGeofence()` | 同上 + 后台位置 | 地理围栏监控 |
| `addGpsStatusListener()` | `ACCESS_FINE_LOCATION` | GPS 卫星状态 |

**行为变更**（第 107-114 行）：
```java
@ChangeId
@EnabledAfter(targetSdkVersion = Build.VERSION_CODES.Q)
private static final long GET_PROVIDER_SECURITY_EXCEPTIONS = 150935354L;
```
对于目标为 Android R+ 的应用，`getProvider()` 不再抛出安全异常。

**属性缓存**（第 89-102 行）：LocationManager 使用 `PropertyInvalidatedCache` 进行 `isLocationEnabled` 检查以减少 IPC 调用。

### 7.2 服务端：LocationManagerService

**文件**: `frameworks/base/services/core/java/com/android/server/location/LocationManagerService.java`

**类声明**（第 132 行）：
```java
public class LocationManagerService extends ILocationManager.Stub {
```

**生命周期**（第 137 行）：
```java
public static class Lifecycle extends SystemService {
    @Override
    public void onStart() {
        LocationManager.invalidateLocalLocationEnabledCaches();
        publishBinderService(Context.LOCATION_SERVICE, mService);
    }

    @Override
    public void onBootPhase(int phase) {
        if (phase == PHASE_SYSTEM_SERVICES_READY) {
            mService.onSystemReady();
        } else if (phase == PHASE_THIRD_PARTY_APPS_CAN_START) {
            mService.onSystemThirdPartyAppsCanStart();
        }
    }
}
```

位置提供者（GPS、网络、融合、被动）在 `PHASE_THIRD_PARTY_APPS_CAN_START` 之后初始化，因为某些提供者依赖第三方代码。

### 7.3 AIDL 接口

**文件**: `frameworks/base/location/java/android/location/ILocationManager.aidl`

关键操作：
- `getLastLocation()` / `getCurrentLocation()` / `requestLocationUpdates()` / `removeUpdates()`
- `requestGeofence()` / `removeGeofence()`
- `registerGnssStatusCallback()` / `addGnssMeasurementsListener()`
- `getAllProviders()` / `getBestProvider()` / `getProviderProperties()`
- `isLocationEnabledForUser()` / `setLocationEnabledForUser()`
- `addTestProvider()` / `setTestProviderLocation()`（用于测试）
- `injectLocation()` / `injectGnssMeasurementCorrections()`

**隐藏的测试 API**（第 114-118 行）：
```
addTestProvider, removeTestProvider, setTestProviderLocation, setTestProviderEnabled
```

### 7.4 权限模型

位置使用分层权限模型：
- `ACCESS_COARSE_LOCATION` -- 基于基站/WiFi 的位置
- `ACCESS_FINE_LOCATION` -- 基于 GPS 的精确位置
- `ACCESS_BACKGROUND_LOCATION` -- 应用在后台时的位置访问（Android 10+）
- `LOCATION_HARDWARE` -- 系统级位置硬件访问（系统 API）
- `WRITE_SECURE_SETTINGS` -- 启用/禁用位置

---

## 8. SensorManager

**文件**: `frameworks/base/core/java/android/hardware/SensorManager.java`

### 8.1 类声明（第 83 行）
```java
@SystemService(Context.SENSOR_SERVICE)
public abstract class SensorManager {
```

SensorManager 是一个**抽象类**。具体实现是 `SystemSensorManager`（`frameworks/base/core/java/android/hardware/SystemSensorManager.java`）。

### 8.2 用途

SensorManager 提供对设备硬件传感器（加速度计、陀螺仪、磁力计、接近、光线等）的访问。

### 8.3 关键 API

```java
// 按类型获取传感器（第 490 行）
public Sensor getDefaultSensor(int type)

// 获取某类型的所有传感器（第 418 行）
public List<Sensor> getSensorList(int type)

// 注册传感器事件（抽象，约第 700+ 行）
public boolean registerListener(SensorEventListener listener,
    Sensor sensor, int samplingPeriodUs)

// 注销（抽象，约第 640+ 行）
public void unregisterListener(SensorEventListener listener)
```

### 8.4 架构

与大多数系统服务不同，传感器数据通过**原生传感器服务**流动，而不是 system_server 中的 Java 服务：
- `SensorManager`（Java 抽象）-> `SystemSensorManager`（Java 具体）
- -> JNI -> `SensorManager`（C++，位于 `frameworks/native/libs/sensor/`）
- -> Binder -> `SensorService`（C++ 原生服务）

原生传感器服务在 `SystemServer` 中启动（第 940-945 行）：
```java
mSensorServiceStart = SystemServerInitThreadPool.submit(() -> {
    startSensorService(); // 原生方法
}, START_SENSOR_SERVICE);
```

### 8.5 电量注意事项

来自 Javadoc（第 39-42 行）：
> 始终确保禁用不需要的传感器，尤其是在 Activity 暂停时。
> 否则可能在几小时内耗尽电池。注意，当屏幕关闭时，
> 系统*不会*自动禁用传感器。

### 8.6 系统 API

```java
@SystemApi
// SensorManager 包含以下隐藏 API：
// - 直接传感器通道（SensorDirectChannel）
// - 附加传感器信息（SensorAdditionalInfo）
// - 传感器隐私管理
```

---

## 9. Camera / CameraManager

### 9.1 旧版 Camera API

**文件**: `frameworks/base/core/java/android/hardware/Camera.java`（第 158 行）

```java
public class Camera {
```

旧版 `Camera` 类（自 API 21 起已弃用）通过 JNI 与摄像头服务通信。它是一个直接的、非 Binder 的 API，使用原生句柄。

### 9.2 Camera2 API: CameraManager

**文件**: `frameworks/base/core/java/android/hardware/camera2/CameraManager.java`

**类声明**（第 72 行）：
```java
@SystemService(Context.CAMERA_SERVICE)
public final class CameraManager {
```

### 9.3 架构

CameraManager 通过 Binder 连接到原生 `ICameraService`：
```java
import android.hardware.ICameraService;
import android.hardware.ICameraServiceListener;
```

摄像头服务本身是一个原生（C++）服务，不是 Java 系统服务。`CameraServiceProxy`（在 SystemServer 第 2058 行启动）是一个 Java 端代理，协助摄像头状态管理。

### 9.4 关键 API

```java
// 列出摄像头（第 114 行）
public String[] getCameraIdList() throws CameraAccessException

// 打开摄像头设备
public void openCamera(@NonNull String cameraId,
    @NonNull final CameraDevice.StateCallback callback, @Nullable Handler handler)

// 获取摄像头特性
public CameraCharacteristics getCameraCharacteristics(@NonNull String cameraId)

// 摄像头可用性回调
public abstract static class AvailabilityCallback {
    public void onCameraAvailable(@NonNull String cameraId) {}
    public void onCameraUnavailable(@NonNull String cameraId) {}
}
```

### 9.5 CameraManagerGlobal（第 1090 行）

```java
private static final class CameraManagerGlobal extends ICameraServiceListener.Stub
        implements IBinder.DeathRecipient {
```

一个单例，管理与摄像头服务的连接并分发可用性回调。它实现 `ICameraServiceListener.Stub` 以接收来自原生摄像头服务的回调。

### 9.6 权限

摄像头访问需要 `android.permission.CAMERA`。CameraManager 通过摄像头服务隐式检查此权限。

---

## 10. 无障碍服务

### 10.1 AccessibilityManagerService

**文件**: `frameworks/base/services/accessibility/java/com/android/server/accessibility/AccessibilityManagerService.java`

**类声明**（第 147 行）：
```java
public class AccessibilityManagerService extends IAccessibilityManager.Stub
        implements AbstractAccessibilityServiceConnection.SystemSupport,
        AccessibilityUserState.ServiceInfoChangeListener,
        AccessibilityWindowManager.AccessibilityEventSender,
        AccessibilitySecurityPolicy.AccessibilityUserManager,
        SystemActionPerformer.SystemActionsChangedListener {
```

### 10.2 用途

管理帮助残障用户的无障碍服务：
- 屏幕阅读器（TalkBack）
- 开关控制
- 放大
- 语音控制
- 盲文显示器支持

### 10.3 启动序列

在 SystemServer 中启动（第 1277 行）：
```java
t.traceBegin("StartAccessibilityManagerService");
mSystemServiceManager.startService(ACCESSIBILITY_MANAGER_SERVICE_CLASS);
```

其中 `ACCESSIBILITY_MANAGER_SERVICE_CLASS` 定义为（第 301 行）：
```java
"com.android.server.accessibility.AccessibilityManagerService$Lifecycle"
```

### 10.4 应用开发者集成

无障碍服务扩展 `AccessibilityService`：
```java
public class MyAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) { }

    @Override
    public void onInterrupt() { }
}
```

清单声明：
```xml
<service android:name=".MyAccessibilityService"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
    <intent-filter>
        <action android:name="android.accessibilityservice.AccessibilityService" />
    </intent-filter>
    <meta-data android:name="android.accessibilityservice"
        android:resource="@xml/accessibility_service_config" />
</service>
```

### 10.5 安全性

- 服务需要 `BIND_ACCESSIBILITY_SERVICE` 权限（仅系统绑定）
- 用户必须在设置中显式启用无障碍服务
- `AccessibilitySecurityPolicy`（在第 151 行引用）强制执行访问控制
- 无障碍服务获得强大的能力：读取屏幕内容、执行手势、控制其他应用

---

## 11. NotificationListenerService

**文件**: `frameworks/base/core/java/android/service/notification/NotificationListenerService.java`

### 11.1 类声明（第 100 行）
```java
public abstract class NotificationListenerService extends Service {
```

### 11.2 用途

允许应用接收和与系统中发布的所有通知进行交互。这是一个强大的能力，用于：
- 可穿戴设备伴侣应用
- 通知管理应用
- 数字健康工具
- 无障碍服务

### 11.3 清单声明

来自 Javadoc（第 73-83 行）：
```xml
<service android:name=".NotificationListener"
    android:label="@string/service_name"
    android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE">
    <intent-filter>
        <action android:name="android.service.notification.NotificationListenerService" />
    </intent-filter>
</service>
```

### 11.4 关键回调

```java
// Line 360 - 当通知被发布时调用
public void onNotificationPosted(StatusBarNotification sbn)

// Line 373 - 带排名信息
public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap)

// Line 394 - 当通知被移除时调用
public void onNotificationRemoved(StatusBarNotification sbn)
```

### 11.5 SERVICE_INTERFACE（第 344 行）
```java
public static final String SERVICE_INTERFACE
    = "android.service.notification.NotificationListenerService";
```

### 11.6 服务端：NotificationManagerService

在 SystemServer 中启动（第 1581 行）：
```java
mSystemServiceManager.startService(NotificationManagerService.class);
notification = INotificationManager.Stub.asInterface(
    ServiceManager.getService(Context.NOTIFICATION_SERVICE));
```

### 11.7 安全约束

- 需要 `BIND_NOTIFICATION_LISTENER_SERVICE`（仅系统级）
- 用户必须在设置中显式授予通知访问权限
- 在运行 Android Q 及以下版本的低内存设备上，无法绑定通知监听器
- 在工作配置文件中运行的通知监听器被系统忽略（第 93 行）

---

## 12. 服务注册和访问模式

### 12.1 两种注册机制

Android 11 使用两种互补的服务注册机制：

**1. 直接 ServiceManager 注册**：
```java
ServiceManager.addService(Context.ACTIVITY_SERVICE, this);
ServiceManager.addService(Context.WINDOW_SERVICE, wm);
ServiceManager.addService(Context.CONNECTIVITY_SERVICE, connectivity);
```

用于需要通过 Binder 名称查找直接访问的服务。

**2. SystemServiceManager 生命周期**：
```java
mSystemServiceManager.startService(PowerManagerService.class);
mSystemServiceManager.startService(NotificationManagerService.class);
```

用于遵循 `SystemService` 生命周期模式的服务。这些服务内部使用 `publishBinderService()` 向 ServiceManager 注册。

### 12.2 应用的服务访问

`@SystemService` 注解将 Manager 类映射到服务名称：

| 注解 | Context 常量 | Manager 类 |
|------------|-----------------|---------------|
| `@SystemService(Context.ACTIVITY_SERVICE)` | `"activity"` | `ActivityManager` |
| `@SystemService(Context.LOCATION_SERVICE)` | `"location"` | `LocationManager` |
| `@SystemService(Context.SENSOR_SERVICE)` | `"sensor"` | `SensorManager` |
| `@SystemService(Context.CAMERA_SERVICE)` | `"camera"` | `CameraManager` |
| `@SystemService(Context.WINDOW_SERVICE)` | `"window"` | `WindowManager` |

### 12.3 内部（系统间）访问

系统服务之间通过 `LocalServices` 互相访问：
```java
LocalServices.addService(SystemServiceManager.class, mSystemServiceManager);
// 后续...
PackageManagerInternal pmi = LocalServices.getService(PackageManagerInternal.class);
```

这避免了 `system_server` 内进程内通信的 Binder IPC 开销。

---

## 13. 权限检查和安全执行

### 13.1 分层权限模型

Android 11 系统服务在多个层次强制执行安全性：

**第 1 层 -- Binder 级身份**：
```java
int callingUid = Binder.getCallingUid();
int callingPid = Binder.getCallingPid();
```

**第 2 层 -- 权限检查**：
```java
// 直接权限检查
if (checkCallingPermission(permission) != PERMISSION_GRANTED) {
    throw new SecurityException("...");
}

// 或通过 enforceCallingPermission（自动抛出异常）
enforceCallingPermission(android.Manifest.permission.DUMP, "requestBugReport");
```

**第 3 层 -- AppOps 执行**：
```java
AppOpsManager appOps = context.getSystemService(AppOpsManager.class);
int mode = appOps.checkOp(AppOpsManager.OP_SYSTEM_ALERT_WINDOW, uid, packageName);
```

**第 4 层 -- 基于 UID 的检查**：
```java
if (callingUid != Process.SYSTEM_UID && callingUid != Process.ROOT_UID) {
    throw new SecurityException("Only system can call this");
}
```

### 13.2 AMS 中常见的权限模式

从 AMS 权限检查的 grep 结果（20+ 种不同模式）：

| 权限 | 用途 | 行号（示例） |
|-----------|---------|-------|
| `FORCE_STOP_PACKAGES` | 强制停止应用 | 3724, 3739, 4452 |
| `KILL_BACKGROUND_PROCESSES` | 终止后台应用 | 4349, 4393, 4431 |
| `PACKAGE_USAGE_STATS` | 使用统计访问 | 3282, 3291, 8800, 8813 |
| `SET_PROCESS_LIMIT` | 设置进程限制 | 5917, 5955 |
| `SET_DEBUG_APP` | 调试应用配置 | 8402 |
| `SET_ALWAYS_FINISH` | "始终结束"设置 | 8533 |
| `SET_ACTIVITY_WATCHER` | 监视 Activity 生命周期 | 8783 |
| `DUMP` | 转储调试信息 | 8652, 8764 |
| `MANAGE_DEBUGGING` | 调试管理 | 8777 |
| `SHUTDOWN` | 设备关机 | 8360 |
| `GET_PROCESS_STATE_AND_OOM_SCORE` | 进程状态信息 | 6058 |
| `GET_INTENT_SENDER_INTENT` | Intent 发送者信息 | 5863 |

### 13.3 WMS 安全

WMS 通过 `WindowManagerPolicy.checkAddPermission()`（第 1384 行）执行基于窗口类型的权限检查：
- `INTERNAL_SYSTEM_WINDOW` -- 系统窗口类型所需
- `SYSTEM_ALERT_WINDOW` -- 悬浮窗所需（TYPE_APPLICATION_OVERLAY）
- 检查在任何窗口状态修改之前进行

### 13.4 身份清除模式

一种在各服务中广泛使用的关键安全模式：
```java
final long token = Binder.clearCallingIdentity();
try {
    // 以系统身份而非调用者身份执行操作
} finally {
    Binder.restoreCallingIdentity(token);
}
```

这在服务需要代表调用者调用其他服务时至关重要。如果不清除，下游服务将看到应用的 UID 并可能拒绝操作。

---

## 14. 隐藏和系统 API

### 14.1 ServiceManager 本身

整个 `ServiceManager` 类是 `@hide`（第 30 行）：
```java
/** @hide */
public final class ServiceManager {
```

应用不能直接注册或查找服务。它们必须使用 `Context.getSystemService()`。

### 14.2 LocationManager 中的 @SystemApi

LocationManager 暴露仅限系统的 API（来自 API 表面文件）：
- `flushGnssBatch()` -- 需要 `LOCATION_HARDWARE`
- `getCurrentLocation(LocationRequest, ...)` -- 带 `LocationRequest` 的重载版本
- `getExtraLocationControllerPackage()` / `setExtraLocationControllerPackage()`

### 14.3 @UnsupportedAppUsage 注解

许多隐藏 API 使用 `@UnsupportedAppUsage` 注解来跟踪非 SDK 接口使用：
```java
// ServiceManager, line 35
@UnsupportedAppUsage
private static IServiceManager sServiceManager;

// ServiceManager, line 127
@UnsupportedAppUsage
public static IBinder getService(String name)
```

Android 11 根据 `maxTargetSdk` 参数对这些 API 执行限制。

### 14.4 ILocationManager 隐藏操作

AIDL 接口（第 123-130 行）包含仅供内部使用的操作：
```
// --- internal ---
void locationCallbackFinished(ILocationListener listener);
String[] getBackgroundThrottlingWhitelist();
String[] getIgnoreSettingsWhitelist();
```

### 14.5 测试 API

```java
// LocationManager @TestApi 方法
void addTestProvider(...)
void removeTestProvider(...)
void setTestProviderLocation(...)
void setTestProviderEnabled(...)
```

---

## 15. 关键发现和建议

### 15.1 架构观察

1. **巨大的文件规模**：AMS（20,423 行）和 PMS（25,775 行）是 AOSP 中最大的文件之一。虽然 Android 10 开始通过将 Activity 管理移至 ATMS 来拆分 AMS，但这些文件仍然难以维护和理解。

2. **启动顺序依赖**：三阶段启动（引导、核心、其他）配合显式启动阶段提供了结构化的初始化，但 SystemServer 第 342 行的注释承认："TODO: 通过改进依赖解析和启动阶段来移除所有这些引用。"

3. **混合注册模式**：一些服务直接使用 `ServiceManager.addService()`，而其他服务使用 `SystemServiceManager.startService()`。这种不一致性使得追踪服务初始化变得更加困难。

4. **Binder 线程限制**：系统服务器使用 31 个 Binder 线程（第 327 行）。在高负载下，这可能成为瓶颈，导致 `TransactionTooLargeException` 或服务响应延迟。

### 15.2 安全观察

1. **权限执行一致性**：AMS 有 30 多个不同的 `enforceCallingPermission()` / `checkCallingPermission()` 调用点。缺乏集中的权限策略使审计变得困难。

2. **身份清除风险**：`clearCallingIdentity()` / `restoreCallingIdentity()` 模式被广泛使用，但容易出错。如果 `restoreCallingIdentity()` 被遗漏（例如，由于 finally 块中未捕获的异常），后续操作将以提升的权限运行。

3. **无障碍服务的强大能力**：无障碍服务一旦启用就能有效获得完整的屏幕访问权限。安全边界完全依赖于用户的显式启用，没有运行时权限提示。

4. **通知监听器访问**：与无障碍服务类似，通知监听器可以访问所有通知内容。低内存设备的限制（第 91 行）是一个务实的限制，而非安全措施。

### 15.3 面向开发者的观察

1. **传感器电量消耗**：SensorManager 文档明确警告电量消耗（第 39-42 行），但系统不会在 Activity 暂停时自动注销传感器。这仍然是应用中电池问题的常见来源。

2. **摄像头服务架构**：Camera2 API 的 `CameraManagerGlobal` 单例（第 1090 行）实现 `IBinder.DeathRecipient` 用于摄像头服务死亡处理。开发者必须优雅地处理 `CameraAccessException`，因为摄像头服务可以独立重启。

3. **位置后台限制**：Android 11 的位置权限模型需要仔细处理。`ACCESS_BACKGROUND_LOCATION` 权限必须单独请求，用户通过设置而非运行时对话框授权。

### 15.4 关键文件参考表

| 文件 | 路径 | 行数 | 用途 |
|------|------|-------|---------|
| SystemServer.java | `frameworks/base/services/java/com/android/server/SystemServer.java` | 2,567 | 启动编排 |
| ActivityManagerService.java | `frameworks/base/services/core/java/com/android/server/am/ActivityManagerService.java` | 20,423 | 进程/组件生命周期 |
| PackageManagerService.java | `frameworks/base/services/core/java/com/android/server/pm/PackageManagerService.java` | 25,775 | 包管理 |
| WindowManagerService.java | `frameworks/base/services/core/java/com/android/server/wm/WindowManagerService.java` | 8,278 | 窗口管理 |
| LocationManagerService.java | `frameworks/base/services/core/java/com/android/server/location/LocationManagerService.java` | ~2,500 | 位置服务 |
| LocationManager.java | `frameworks/base/location/java/android/location/LocationManager.java` | ~1,800 | 位置客户端 API |
| SensorManager.java | `frameworks/base/core/java/android/hardware/SensorManager.java` | ~1,200 | 传感器客户端 API |
| CameraManager.java | `frameworks/base/core/java/android/hardware/camera2/CameraManager.java` | ~1,400 | Camera2 客户端 API |
| Camera.java | `frameworks/base/core/java/android/hardware/Camera.java` | ~2,400 | 旧版摄像头 API |
| AccessibilityManagerService.java | `frameworks/base/services/accessibility/java/com/android/server/accessibility/AccessibilityManagerService.java` | ~5,000 | 无障碍服务 |
| NotificationListenerService.java | `frameworks/base/core/java/android/service/notification/NotificationListenerService.java` | ~1,400 | 通知监听器 API |
| ServiceManager.java | `frameworks/base/core/java/android/os/ServiceManager.java` | ~300 | 服务注册表 |
| Binder.java | `frameworks/base/core/java/android/os/Binder.java` | ~1,200 | IPC 基类 |
| IActivityManager.aidl | `frameworks/base/core/java/android/app/IActivityManager.aidl` | ~600 | AMS Binder 接口 |
| IPackageManager.aidl | `frameworks/base/core/java/android/content/pm/IPackageManager.aidl` | ~800 | PMS Binder 接口 |
| IWindowManager.aidl | `frameworks/base/core/java/android/view/IWindowManager.aidl` | ~400 | WMS Binder 接口 |
| ILocationManager.aidl | `frameworks/base/location/java/android/location/ILocationManager.aidl` | 131 | 位置 Binder 接口 |
| SystemService.java | `frameworks/base/services/core/java/com/android/server/SystemService.java` | ~200 | 启动阶段定义 |
| SystemServiceManager.java | `frameworks/base/services/core/java/com/android/server/SystemServiceManager.java` | ~400 | 服务生命周期管理 |

---

*报告基于 Android 11 AOSP 源代码审查生成。所有行号引用位于 `~/aosp-android-11/` 的源文件。*
