# Code Review Report 06: Android 11 System Services

## Table of Contents
1. [Overview and Architecture](#1-overview-and-architecture)
2. [SystemServer Boot Sequence](#2-systemserver-boot-sequence)
3. [ServiceManager and Binder IPC Bridge](#3-servicemanager-and-binder-ipc-bridge)
4. [ActivityManagerService (AMS)](#4-activitymanagerservice-ams)
5. [PackageManagerService (PMS)](#5-packagemanagerservice-pms)
6. [WindowManagerService (WMS)](#6-windowmanagerservice-wms)
7. [LocationManager / LocationManagerService](#7-locationmanager--locationmanagerservice)
8. [SensorManager](#8-sensormanager)
9. [Camera / CameraManager](#9-camera--cameramanager)
10. [Accessibility Services](#10-accessibility-services)
11. [NotificationListenerService](#11-notificationlistenerservice)
12. [Service Registration and Access Patterns](#12-service-registration-and-access-patterns)
13. [Permission Checks and Security Enforcement](#13-permission-checks-and-security-enforcement)
14. [Hidden and System APIs](#14-hidden-and-system-apis)
15. [Key Findings and Recommendations](#15-key-findings-and-recommendations)

---

## 1. Overview and Architecture

Android 11 system services form the backbone of the platform, providing the APIs that applications use to interact with the operating system. The architecture follows a strict client-server model mediated by Binder IPC:

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

**Key Principle**: App developers interact with "Manager" classes (client-side proxies), which communicate via AIDL-generated Binder interfaces to the actual service implementations running in the `system_server` process.

### Key Source Directories
- **Service implementations**: `frameworks/base/services/core/java/com/android/server/`
- **Client-side service APIs**: `frameworks/base/core/java/android/service/`
- **Hardware abstractions**: `frameworks/base/core/java/android/hardware/`
- **System boot**: `frameworks/base/services/java/com/android/server/SystemServer.java`

---

## 2. SystemServer Boot Sequence

**File**: `frameworks/base/services/java/com/android/server/SystemServer.java` (2,567 lines)

### 2.1 Entry Point

The system server is the first managed process started after Zygote. Its entry point is:

```java
// Line 413
public static void main(String[] args) {
    new SystemServer().run();
}
```

### 2.2 The `run()` Method (Line 436)

The `run()` method executes the complete boot sequence:

1. **Initialization** (lines 436-588): Sets timezone, configures Binder threading, prepares the main looper, loads native libraries (`android_servers`), creates the system context.

2. **Binder Thread Configuration** (line 528):
   ```java
   BinderInternal.setMaxThreads(sMaxBinderThreads); // 31 threads
   ```

3. **SystemServiceManager Creation** (line 562):
   ```java
   mSystemServiceManager = new SystemServiceManager(mSystemContext);
   ```

4. **Three-Phase Service Startup** (lines 594-604):
   ```java
   startBootstrapServices(t);  // Line 596 - Critical interdependent services
   startCoreServices(t);       // Line 597 - Essential but less tangled
   startOtherServices(t);      // Line 598 - Everything else
   ```

5. **Main Loop** (line 628):
   ```java
   Looper.loop(); // Never returns
   ```

### 2.3 Bootstrap Services (Line 710)

These are started first due to complex mutual dependencies:

| Order | Service | Line | Purpose |
|-------|---------|------|---------|
| 1 | Watchdog | 716 | Deadlock detection |
| 2 | PlatformCompat | 729 | API compatibility |
| 3 | FileIntegrityService | 740 | File integrity verification |
| 4 | Installer | 747 | Package installation daemon |
| 5 | DeviceIdentifiersPolicyService | 753 | Device ID access control |
| 6 | UriGrantsManagerService | 758 | URI permission grants |
| 7 | **ActivityTaskManagerService** | 764 | Activity/task management |
| 8 | **ActivityManagerService** | 766 | Process/component lifecycle |
| 9 | DataLoaderManagerService | 775 | Data loading |
| 10 | **PowerManagerService** | 789 | Power management |
| 11 | ThermalManagerService | 793 | Thermal monitoring |
| 12 | RecoverySystemService | 804 | OTA recovery |
| 13 | LightsService | 815 | LED/backlight control |
| 14 | **DisplayManagerService** | 828 | Display management |
| 15 | **PackageManagerService** | 857 | Package management |
| 16 | UserManagerService | 898 | Multi-user support |
| 17 | OverlayManagerService | 923 | Runtime resource overlays |
| 18 | SensorPrivacyService | 927 | Sensor access privacy |

**Critical dependency**: AMS is started before PMS. PMS requires the display to be ready (line 833: `PHASE_WAIT_FOR_DEFAULT_DISPLAY`).

### 2.4 Core Services (Line 953)

| Service | Line | Purpose |
|---------|------|---------|
| BatteryService | 963 | Battery level tracking |
| UsageStatsService | 968 | App usage statistics |
| WebViewUpdateService | 976 | WebView readiness |
| BinderCallsStatsService | 987 | Binder call profiling |
| RollbackManagerService | 997 | APK rollback management |
| BugreportManagerService | 1002 | Bug report capture |
| GpuService | 1007 | GPU driver management |

### 2.5 Other Services (Line 1016)

This is the largest phase, starting 80+ services including:

- **WindowManagerService** (line 1163)
- **InputManagerService** (line 1156)
- **NotificationManagerService** (line 1581)
- **LocationManagerService** (via lifecycle)
- **AccessibilityManagerService** (line 1277)
- **ConnectivityService** (line 1541)
- **AudioService** (line 1641)
- **CameraServiceProxy** (line 2058)
- **BiometricService** (line 1959)
- And many more...

### 2.6 Boot Phases

Defined in `frameworks/base/services/core/java/com/android/server/SystemService.java`:

| Constant | Value | Meaning |
|----------|-------|---------|
| `PHASE_WAIT_FOR_DEFAULT_DISPLAY` | 100 | Display is ready |
| `PHASE_LOCK_SETTINGS_READY` | 480 | Lock settings available |
| `PHASE_SYSTEM_SERVICES_READY` | 500 | Core services operational |
| `PHASE_DEVICE_SPECIFIC_SERVICES_READY` | 520 | OEM services ready |
| `PHASE_ACTIVITY_MANAGER_READY` | 550 | AMS fully operational |
| `PHASE_THIRD_PARTY_APPS_CAN_START` | 600 | Safe to launch apps |
| `PHASE_BOOT_COMPLETED` | 1000 | Boot fully complete |

### 2.7 The systemReady Callback (Line 2241)

After all services are started, `AMS.systemReady()` triggers a callback that:
1. Starts the `PHASE_ACTIVITY_MANAGER_READY` boot phase (line 2244)
2. Starts SystemUI (line 2278)
3. Makes network services ready (lines 2297-2346)
4. Starts the `PHASE_THIRD_PARTY_APPS_CAN_START` phase (line 2358)
5. Starts the network stack (line 2368)

---

## 3. ServiceManager and Binder IPC Bridge

### 3.1 ServiceManager

**File**: `frameworks/base/core/java/android/os/ServiceManager.java`

ServiceManager is the central registry for all system services. It is a hidden API (`@hide`) that apps cannot call directly.

**Key Methods**:

```java
// Line 165 - Register a service
public static void addService(String name, IBinder service)

// Line 194 - Register with options
public static void addService(String name, IBinder service,
    boolean allowIsolated, int dumpPriority)

// Line 128 - Retrieve a service (blocking)
public static IBinder getService(String name)

// Line 208 - Retrieve a service (non-blocking)
public static IBinder checkService(String name)

// Line 245 - Wait for a service to become available (native)
public static native IBinder waitForService(@NonNull String name)

// Line 266 - List all running services
public static String[] listServices()
```

**Service Cache** (line 42):
```java
private static Map<String, IBinder> sCache = new ArrayMap<String, IBinder>();
```
Well-known services (like WM and AM) are cached to avoid repeated lookups.

**IServiceManager Connection** (line 110):
```java
private static IServiceManager getIServiceManager() {
    sServiceManager = ServiceManagerNative
        .asInterface(Binder.allowBlocking(BinderInternal.getContextObject()));
    return sServiceManager;
}
```
The native `getContextObject()` returns a handle to the kernel-level service manager (context manager).

### 3.2 Binder IPC

**File**: `frameworks/base/core/java/android/os/Binder.java`

Binder is the core IPC mechanism. The `Binder` class (line 78) implements `IBinder`.

**Transaction Flow**:
1. Client calls a method on the proxy (e.g., `IActivityManager.Stub.Proxy`)
2. Proxy serializes arguments into a `Parcel`, calls `transact()`
3. Kernel Binder driver delivers the transaction to the server process
4. Server-side `execTransact()` (line 1116) is called by native code
5. `execTransactInternal()` (line 1129) deserializes and calls `onTransact()`
6. The Stub implementation dispatches to the concrete service method

**Key Security Pattern** (used throughout services):
```java
final int callingUid = Binder.getCallingUid();
final int callingPid = Binder.getCallingPid();
final long origId = Binder.clearCallingIdentity();
try {
    // Perform privileged operations
} finally {
    Binder.restoreCallingIdentity(origId);
}
```

**Observer Interface** (line 682):
```java
interface Observer {
    Object onTransactStarted(@NonNull IBinder binder, int transactionCode, int flags);
    void onTransactEnded(@Nullable Object session);
}
```
Used for monitoring and profiling Binder calls.

### 3.3 How Apps Access System Services

Apps do not use `ServiceManager` directly. Instead:

1. **Context.getSystemService()** returns a Manager object
2. **SystemServiceRegistry** maps service names to factory lambdas
3. Each Manager internally holds a reference to the AIDL proxy

Example flow for `LocationManager`:
```
context.getSystemService(Context.LOCATION_SERVICE)
  -> SystemServiceRegistry looks up "location"
  -> Creates LocationManager(context, ILocationManager.Stub.asInterface(
       ServiceManager.getServiceOrThrow(Context.LOCATION_SERVICE)))
```

---

## 4. ActivityManagerService (AMS)

**File**: `frameworks/base/services/core/java/com/android/server/am/ActivityManagerService.java` (20,423 lines)

### 4.1 Class Declaration (Line 416)
```java
public class ActivityManagerService extends IActivityManager.Stub
        implements Watchdog.Monitor, BatteryStatsImpl.BatteryCallback {
```

AMS extends `IActivityManager.Stub` -- the server side of the AIDL interface.

### 4.2 Purpose and Responsibility

AMS is the central manager of the Android application model:
- **Process lifecycle management**: Starting, stopping, OOM adjustment of app processes
- **Broadcast dispatch**: Ordered and unordered broadcast delivery
- **Content provider management**: Publishing and resolving content providers
- **Service management**: Starting, binding, and stopping services
- **Instrumentation**: Test framework integration
- **Battery stats**: Tracking power usage per UID

**Note**: In Android 10+, activity/task management was moved to `ActivityTaskManagerService` (ATMS). AMS delegates activity-related calls to ATMS.

### 4.3 AIDL Interface

**File**: `frameworks/base/core/java/android/app/IActivityManager.aidl`

Key methods exposed via Binder:

| Method | Purpose |
|--------|---------|
| `startActivity()` / `startActivityWithFeature()` | Launch activities |
| `broadcastIntent()` / `broadcastIntentWithFeature()` | Send broadcasts |
| `startService()` / `bindService()` | Manage services |
| `getContentProvider()` | Resolve content providers |
| `getRunningAppProcesses()` | List running processes |
| `getProcessMemoryInfo()` | Process memory diagnostics |
| `unbindService()` | Release service connections |

### 4.4 Service Registration (Line 2107)

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

AMS registers itself and several sub-services under different names. This demonstrates a pattern where one service object manages multiple related service endpoints.

### 4.5 Lifecycle (Line 2331)

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

AMS requires ATMS to be started first. They share a global lock (`mWindowManagerGlobalLock`).

### 4.6 Key Constants

| Constant | Value | Purpose |
|----------|-------|---------|
| `PROC_START_TIMEOUT` | 10,000 ms | Max time for process to attach |
| `BROADCAST_FG_TIMEOUT` | 10,000 ms | Foreground broadcast timeout |
| `BROADCAST_BG_TIMEOUT` | 60,000 ms | Background broadcast timeout |
| `MAX_RECEIVERS_ALLOWED_PER_APP` | 1,000 | Receiver registration limit |
| `TOP_APP_PRIORITY_BOOST` | -10 | Thread priority for top app |

### 4.7 Permission Enforcement

AMS enforces permissions extensively. Key permission check patterns:

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
    // throws SecurityException
}
```

Commonly enforced permissions include:
- `FORCE_STOP_PACKAGES` (lines 3724, 3739)
- `KILL_BACKGROUND_PROCESSES` (lines 4349, 4393, 4431)
- `SET_PROCESS_LIMIT` (lines 5917, 5955)
- `SET_DEBUG_APP` (line 8402)
- `DUMP` (line 8652)
- `PACKAGE_USAGE_STATS` (lines 3282, 3291)

### 4.8 App Developer Interaction

Developers use `ActivityManager`:
```java
ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
am.getRunningAppProcesses();
am.getMemoryInfo(memInfo);
am.clearApplicationUserData(packageName, observer);
```

---

## 5. PackageManagerService (PMS)

**File**: `frameworks/base/services/core/java/com/android/server/pm/PackageManagerService.java` (25,775 lines)

### 5.1 Class Declaration (Line 470)
```java
public class PackageManagerService extends IPackageManager.Stub
        implements PackageSender {
```

### 5.2 Purpose and Responsibility

PMS manages all aspects of application packages:
- **Package scanning and parsing**: Reading APK manifests
- **Package installation/uninstallation**: Including verification
- **Permission management**: Granting, revoking, checking permissions
- **Intent resolution**: Matching intents to activities/services/receivers
- **Package queries**: Package info, component info, feature queries
- **Shared library management**: System shared libraries
- **DEX optimization**: Triggering dexopt/dex2oat
- **Signature verification**: APK signing and certificate management

### 5.3 Initialization (Line 2568)

```java
public static PackageManagerService main(Context context, Installer installer,
        boolean factoryTest, boolean onlyCore) {
    PackageManagerServiceCompilerMapping.checkProperties();
    // Creates Injector with dependencies: UserManagerService,
    // PermissionManagerService, ComponentResolver, Settings
    Injector injector = new Injector(context, lock, installer, installLock, ...);
    PackageManagerService m = new PackageManagerService(injector, ...);
    ...
}
```

PMS uses an `Injector` pattern for dependency injection, facilitating testing. It creates:
- `ComponentResolver` - resolves intents to components
- `PermissionManagerService` - manages permissions
- `UserManagerService` - multi-user support
- `Settings` - persistent package state in `/data/system/packages.xml`

### 5.4 AIDL Interface

**File**: `frameworks/base/core/java/android/content/pm/IPackageManager.aidl`

Key methods:

| Method | Line | Purpose |
|--------|------|---------|
| `getPackageInfo()` | 70 | Full package metadata |
| `getApplicationInfo()` | 83 | Application-level info |
| `getPackageUid()` | 74 | Package UID lookup |
| `queryIntentActivities()` | 136 | Intent resolution |
| `checkPackageStartable()` | 66 | Startability check |
| `isPackageAvailable()` | 68 | Availability check |

### 5.5 Permission Imports

PMS imports and checks an extensive set of permissions (lines 19-108):
- `DELETE_PACKAGES`, `INSTALL_PACKAGES`
- `MANAGE_DEVICE_ADMINS`, `MANAGE_PROFILE_AND_DEVICE_OWNERS`
- `QUERY_ALL_PACKAGES` (new in Android 11 -- package visibility)
- `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`
- `SET_HARMFUL_APP_WARNINGS`

### 5.6 App Developer Interaction

```java
PackageManager pm = context.getPackageManager();
PackageInfo info = pm.getPackageInfo("com.example.app", 0);
List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
boolean granted = pm.checkPermission(permission, packageName) == PERMISSION_GRANTED;
```

### 5.7 Package Visibility (Android 11)

Android 11 introduced package visibility restrictions via `QUERY_ALL_PACKAGES` permission (line 23). Apps must declare `<queries>` in their manifest to see other packages, unless they hold this permission.

---

## 6. WindowManagerService (WMS)

**File**: `frameworks/base/services/core/java/com/android/server/wm/WindowManagerService.java` (8,278 lines)

### 6.1 Class Declaration (Line 317)
```java
public class WindowManagerService extends IWindowManager.Stub
        implements Watchdog.Monitor, WindowManagerPolicy.WindowManagerFuncs {
```

### 6.2 Purpose and Responsibility

WMS manages the window system:
- **Window lifecycle**: Adding, removing, relaying out windows
- **Display management**: Multi-display support
- **Input dispatch**: Routing input events to the correct window
- **Animations**: Window transitions and animations
- **Screen orientation**: Rotation management
- **Surface management**: Compositing via SurfaceFlinger

### 6.3 Initialization (Line 1104)

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

WMS is created on the `DisplayThread` (not the main thread). The `runWithScissors()` call blocks the caller until the creation completes on the target thread.

**Constructor** (line 1143): Initializes with `InputManagerService`, `ActivityTaskManagerService`, `WindowManagerPolicy` (PhoneWindowManager), and sets up the global lock shared with ATMS.

### 6.4 Service Registration (SystemServer line 1165)

```java
ServiceManager.addService(Context.WINDOW_SERVICE, wm, false,
    DUMP_FLAG_PRIORITY_CRITICAL | DUMP_FLAG_PROTO);
```

### 6.5 AIDL Interface

**File**: `frameworks/base/core/java/android/view/IWindowManager.aidl`

Key operations defined by the interface:
- Window rotation control (fixed rotation constants at lines 78-87)
- Display fold listener management
- System gesture exclusion zones
- Wallpaper visibility
- Pinned stack (PiP) management
- Scroll capture
- IME (input method) control

### 6.6 Key Window Operations

**addWindow()** (line 1374):
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

Permission check via `WindowManagerPolicy.checkAddPermission()` is the first operation, verifying the caller has rights to add the specified window type.

**relayoutWindow()** (line 2113): Handles window size/position changes with surface management.

### 6.7 Permission Requirements

WMS enforces several permissions (imported at lines 19-28):
- `INTERNAL_SYSTEM_WINDOW` - For system-level window types
- `MANAGE_APP_TOKENS` - Token management
- `MANAGE_ACTIVITY_STACKS` - Activity stack control
- `READ_FRAME_BUFFER` - Screenshot capability
- `REGISTER_WINDOW_MANAGER_LISTENERS` - Event listeners
- `STATUS_BAR_SERVICE` - Status bar window management
- `CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS` - Transition animations
- `INPUT_CONSUMER` - Input interception

### 6.8 App Developer Interaction

Apps interact with WMS indirectly through:
```java
WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
wm.addView(view, layoutParams);
wm.removeView(view);
wm.getDefaultDisplay().getMetrics(metrics);
```

---

## 7. LocationManager / LocationManagerService

### 7.1 Client Side: LocationManager

**File**: `frameworks/base/location/java/android/location/LocationManager.java`

**Annotations** (line 85-86):
```java
@SystemService(Context.LOCATION_SERVICE)
@RequiresFeature(PackageManager.FEATURE_LOCATION)
public class LocationManager {
```

**Key APIs for Developers**:

| Method | Permission Required | Purpose |
|--------|-------------------|---------|
| `requestLocationUpdates()` | `ACCESS_FINE_LOCATION` or `ACCESS_COARSE_LOCATION` | Continuous location updates |
| `getLastKnownLocation()` | Same as above | Last cached location |
| `getCurrentLocation()` | Same as above | One-shot location request |
| `requestGeofence()` | Same + background location | Geofence monitoring |
| `addGpsStatusListener()` | `ACCESS_FINE_LOCATION` | GPS satellite status |

**Behavior Changes** (lines 107-114):
```java
@ChangeId
@EnabledAfter(targetSdkVersion = Build.VERSION_CODES.Q)
private static final long GET_PROVIDER_SECURITY_EXCEPTIONS = 150935354L;
```
For apps targeting Android R+, `getProvider()` no longer throws security exceptions.

**Property Cache** (lines 89-102): LocationManager uses `PropertyInvalidatedCache` for `isLocationEnabled` checks to reduce IPC calls.

### 7.2 Server Side: LocationManagerService

**File**: `frameworks/base/services/core/java/com/android/server/location/LocationManagerService.java`

**Class Declaration** (line 132):
```java
public class LocationManagerService extends ILocationManager.Stub {
```

**Lifecycle** (line 137):
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

Location providers (GPS, Network, Fused, Passive) are initialized after `PHASE_THIRD_PARTY_APPS_CAN_START` because some rely on third-party code.

### 7.3 AIDL Interface

**File**: `frameworks/base/location/java/android/location/ILocationManager.aidl`

Key operations:
- `getLastLocation()` / `getCurrentLocation()` / `requestLocationUpdates()` / `removeUpdates()`
- `requestGeofence()` / `removeGeofence()`
- `registerGnssStatusCallback()` / `addGnssMeasurementsListener()`
- `getAllProviders()` / `getBestProvider()` / `getProviderProperties()`
- `isLocationEnabledForUser()` / `setLocationEnabledForUser()`
- `addTestProvider()` / `setTestProviderLocation()` (for testing)
- `injectLocation()` / `injectGnssMeasurementCorrections()`

**Hidden test APIs** (lines 114-118):
```
addTestProvider, removeTestProvider, setTestProviderLocation, setTestProviderEnabled
```

### 7.4 Permission Model

Location uses a layered permission model:
- `ACCESS_COARSE_LOCATION` -- Cell/WiFi-based location
- `ACCESS_FINE_LOCATION` -- GPS-based precise location
- `ACCESS_BACKGROUND_LOCATION` -- Location access when app is backgrounded (Android 10+)
- `LOCATION_HARDWARE` -- System-level location hardware access (system API)
- `WRITE_SECURE_SETTINGS` -- Enabling/disabling location

---

## 8. SensorManager

**File**: `frameworks/base/core/java/android/hardware/SensorManager.java`

### 8.1 Class Declaration (Line 83)
```java
@SystemService(Context.SENSOR_SERVICE)
public abstract class SensorManager {
```

SensorManager is an **abstract class**. The concrete implementation is `SystemSensorManager` (`frameworks/base/core/java/android/hardware/SystemSensorManager.java`).

### 8.2 Purpose

SensorManager provides access to the device's hardware sensors (accelerometer, gyroscope, magnetometer, proximity, light, etc.).

### 8.3 Key APIs

```java
// Get a sensor by type (line 490)
public Sensor getDefaultSensor(int type)

// Get all sensors of a type (line 418)
public List<Sensor> getSensorList(int type)

// Register for sensor events (abstract, line ~700+)
public boolean registerListener(SensorEventListener listener,
    Sensor sensor, int samplingPeriodUs)

// Unregister (abstract, line ~640+)
public void unregisterListener(SensorEventListener listener)
```

### 8.4 Architecture

Unlike most system services, sensor data flows through a **native sensor service** rather than a Java service in system_server:
- `SensorManager` (Java abstract) -> `SystemSensorManager` (Java concrete)
- -> JNI -> `SensorManager` (C++ in `frameworks/native/libs/sensor/`)
- -> Binder -> `SensorService` (C++ native service)

The native sensor service is started in `SystemServer` (line 940-945):
```java
mSensorServiceStart = SystemServerInitThreadPool.submit(() -> {
    startSensorService(); // Native method
}, START_SENSOR_SERVICE);
```

### 8.5 Power Considerations

From the Javadoc (lines 39-42):
> Always make sure to disable sensors you don't need, especially when your
> activity is paused. Failing to do so can drain the battery in just a few
> hours. Note that the system will *not* disable sensors automatically when
> the screen turns off.

### 8.6 System APIs

```java
@SystemApi
// SensorManager includes hidden APIs for:
// - Direct sensor channels (SensorDirectChannel)
// - Additional sensor info (SensorAdditionalInfo)
// - Sensor privacy management
```

---

## 9. Camera / CameraManager

### 9.1 Legacy Camera API

**File**: `frameworks/base/core/java/android/hardware/Camera.java` (line 158)

```java
public class Camera {
```

The legacy `Camera` class (deprecated since API 21) communicates with the camera service through JNI. It is a direct, non-Binder API that uses native handles.

### 9.2 Camera2 API: CameraManager

**File**: `frameworks/base/core/java/android/hardware/camera2/CameraManager.java`

**Class Declaration** (line 72):
```java
@SystemService(Context.CAMERA_SERVICE)
public final class CameraManager {
```

### 9.3 Architecture

CameraManager connects to the native `ICameraService` through Binder:
```java
import android.hardware.ICameraService;
import android.hardware.ICameraServiceListener;
```

The camera service itself is a native (C++) service, not a Java system service. `CameraServiceProxy` (started at SystemServer line 2058) is a Java-side proxy that assists with camera state management.

### 9.4 Key APIs

```java
// List cameras (line 114)
public String[] getCameraIdList() throws CameraAccessException

// Open a camera device
public void openCamera(@NonNull String cameraId,
    @NonNull final CameraDevice.StateCallback callback, @Nullable Handler handler)

// Get camera characteristics
public CameraCharacteristics getCameraCharacteristics(@NonNull String cameraId)

// Camera availability callback
public abstract static class AvailabilityCallback {
    public void onCameraAvailable(@NonNull String cameraId) {}
    public void onCameraUnavailable(@NonNull String cameraId) {}
}
```

### 9.5 CameraManagerGlobal (Line 1090)

```java
private static final class CameraManagerGlobal extends ICameraServiceListener.Stub
        implements IBinder.DeathRecipient {
```

A singleton that manages the connection to the camera service and dispatches availability callbacks. It implements `ICameraServiceListener.Stub` to receive callbacks from the native camera service.

### 9.6 Permissions

Camera access requires `android.permission.CAMERA`. CameraManager checks this implicitly through the camera service.

---

## 10. Accessibility Services

### 10.1 AccessibilityManagerService

**File**: `frameworks/base/services/accessibility/java/com/android/server/accessibility/AccessibilityManagerService.java`

**Class Declaration** (line 147):
```java
public class AccessibilityManagerService extends IAccessibilityManager.Stub
        implements AbstractAccessibilityServiceConnection.SystemSupport,
        AccessibilityUserState.ServiceInfoChangeListener,
        AccessibilityWindowManager.AccessibilityEventSender,
        AccessibilitySecurityPolicy.AccessibilityUserManager,
        SystemActionPerformer.SystemActionsChangedListener {
```

### 10.2 Purpose

Manages accessibility services that assist users with disabilities:
- Screen readers (TalkBack)
- Switch access
- Magnification
- Voice Access
- Braille display support

### 10.3 Boot Sequence

Started in SystemServer (line 1277):
```java
t.traceBegin("StartAccessibilityManagerService");
mSystemServiceManager.startService(ACCESSIBILITY_MANAGER_SERVICE_CLASS);
```

Where `ACCESSIBILITY_MANAGER_SERVICE_CLASS` is defined as (line 301):
```java
"com.android.server.accessibility.AccessibilityManagerService$Lifecycle"
```

### 10.4 App Developer Integration

Accessibility services extend `AccessibilityService`:
```java
public class MyAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) { }

    @Override
    public void onInterrupt() { }
}
```

Manifest declaration:
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

### 10.5 Security

- Services require `BIND_ACCESSIBILITY_SERVICE` permission (system-only binding)
- User must explicitly enable accessibility services in Settings
- `AccessibilitySecurityPolicy` (referenced at line 151) enforces access control
- Accessibility services gain powerful capabilities: reading screen content, performing gestures, controlling other apps

---

## 11. NotificationListenerService

**File**: `frameworks/base/core/java/android/service/notification/NotificationListenerService.java`

### 11.1 Class Declaration (Line 100)
```java
public abstract class NotificationListenerService extends Service {
```

### 11.2 Purpose

Allows apps to receive and interact with all notifications posted to the system. This is a powerful capability used by:
- Wearable companion apps
- Notification management apps
- Digital wellbeing tools
- Accessibility services

### 11.3 Manifest Declaration

From the Javadoc (lines 73-83):
```xml
<service android:name=".NotificationListener"
    android:label="@string/service_name"
    android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE">
    <intent-filter>
        <action android:name="android.service.notification.NotificationListenerService" />
    </intent-filter>
</service>
```

### 11.4 Key Callbacks

```java
// Line 360 - Called when a notification is posted
public void onNotificationPosted(StatusBarNotification sbn)

// Line 373 - With ranking information
public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap)

// Line 394 - Called when a notification is removed
public void onNotificationRemoved(StatusBarNotification sbn)
```

### 11.5 SERVICE_INTERFACE (Line 344)
```java
public static final String SERVICE_INTERFACE
    = "android.service.notification.NotificationListenerService";
```

### 11.6 Server Side: NotificationManagerService

Started in SystemServer (line 1581):
```java
mSystemServiceManager.startService(NotificationManagerService.class);
notification = INotificationManager.Stub.asInterface(
    ServiceManager.getService(Context.NOTIFICATION_SERVICE));
```

### 11.7 Security Constraints

- Requires `BIND_NOTIFICATION_LISTENER_SERVICE` (system-only)
- User must explicitly grant notification access in Settings
- On low-RAM devices running Android Q and below, notification listeners cannot be bound
- Notification listeners running in a work profile are ignored by the system (line 93)

---

## 12. Service Registration and Access Patterns

### 12.1 Two Registration Mechanisms

Android 11 uses two complementary mechanisms for service registration:

**1. Direct ServiceManager Registration**:
```java
ServiceManager.addService(Context.ACTIVITY_SERVICE, this);
ServiceManager.addService(Context.WINDOW_SERVICE, wm);
ServiceManager.addService(Context.CONNECTIVITY_SERVICE, connectivity);
```

Used for services that need to be directly accessible via Binder name lookup.

**2. SystemServiceManager Lifecycle**:
```java
mSystemServiceManager.startService(PowerManagerService.class);
mSystemServiceManager.startService(NotificationManagerService.class);
```

Used for services that follow the `SystemService` lifecycle pattern. These services use `publishBinderService()` internally to register with ServiceManager.

### 12.2 Service Access from Apps

The `@SystemService` annotation maps Manager classes to service names:

| Annotation | Context Constant | Manager Class |
|------------|-----------------|---------------|
| `@SystemService(Context.ACTIVITY_SERVICE)` | `"activity"` | `ActivityManager` |
| `@SystemService(Context.LOCATION_SERVICE)` | `"location"` | `LocationManager` |
| `@SystemService(Context.SENSOR_SERVICE)` | `"sensor"` | `SensorManager` |
| `@SystemService(Context.CAMERA_SERVICE)` | `"camera"` | `CameraManager` |
| `@SystemService(Context.WINDOW_SERVICE)` | `"window"` | `WindowManager` |

### 12.3 Internal (System-to-System) Access

System services access each other through `LocalServices`:
```java
LocalServices.addService(SystemServiceManager.class, mSystemServiceManager);
// Later...
PackageManagerInternal pmi = LocalServices.getService(PackageManagerInternal.class);
```

This avoids Binder IPC overhead for intra-process communication within `system_server`.

---

## 13. Permission Checks and Security Enforcement

### 13.1 Layered Permission Model

Android 11 system services enforce security at multiple layers:

**Layer 1 -- Binder-level identity**:
```java
int callingUid = Binder.getCallingUid();
int callingPid = Binder.getCallingPid();
```

**Layer 2 -- Permission checks**:
```java
// Direct permission check
if (checkCallingPermission(permission) != PERMISSION_GRANTED) {
    throw new SecurityException("...");
}

// Or via enforceCallingPermission (throws automatically)
enforceCallingPermission(android.Manifest.permission.DUMP, "requestBugReport");
```

**Layer 3 -- AppOps enforcement**:
```java
AppOpsManager appOps = context.getSystemService(AppOpsManager.class);
int mode = appOps.checkOp(AppOpsManager.OP_SYSTEM_ALERT_WINDOW, uid, packageName);
```

**Layer 4 -- UID-based checks**:
```java
if (callingUid != Process.SYSTEM_UID && callingUid != Process.ROOT_UID) {
    throw new SecurityException("Only system can call this");
}
```

### 13.2 Common Permission Patterns in AMS

From the grep of AMS permission checks (20+ distinct patterns):

| Permission | Purpose | Lines (examples) |
|-----------|---------|-------|
| `FORCE_STOP_PACKAGES` | Force-stopping apps | 3724, 3739, 4452 |
| `KILL_BACKGROUND_PROCESSES` | Killing background apps | 4349, 4393, 4431 |
| `PACKAGE_USAGE_STATS` | Usage statistics access | 3282, 3291, 8800, 8813 |
| `SET_PROCESS_LIMIT` | Setting process limits | 5917, 5955 |
| `SET_DEBUG_APP` | Debug app configuration | 8402 |
| `SET_ALWAYS_FINISH` | "Always finish" setting | 8533 |
| `SET_ACTIVITY_WATCHER` | Watching activity lifecycle | 8783 |
| `DUMP` | Dumping debug info | 8652, 8764 |
| `MANAGE_DEBUGGING` | Debug management | 8777 |
| `SHUTDOWN` | Device shutdown | 8360 |
| `GET_PROCESS_STATE_AND_OOM_SCORE` | Process state info | 6058 |
| `GET_INTENT_SENDER_INTENT` | Intent sender info | 5863 |

### 13.3 WMS Security

WMS enforces window-type-based permissions through `WindowManagerPolicy.checkAddPermission()` (line 1384):
- `INTERNAL_SYSTEM_WINDOW` -- Required for system window types
- `SYSTEM_ALERT_WINDOW` -- Required for overlay windows (TYPE_APPLICATION_OVERLAY)
- The check happens before any window state is modified

### 13.4 Identity Clearing Pattern

A critical security pattern used throughout services:
```java
final long token = Binder.clearCallingIdentity();
try {
    // Perform operations as system, not as caller
} finally {
    Binder.restoreCallingIdentity(token);
}
```

This is essential when a service needs to call other services on behalf of the caller. Without clearing, the downstream service would see the app's UID and potentially deny the operation.

---

## 14. Hidden and System APIs

### 14.1 ServiceManager Itself

The entire `ServiceManager` class is `@hide` (line 30):
```java
/** @hide */
public final class ServiceManager {
```

Apps cannot directly register or look up services. They must use `Context.getSystemService()`.

### 14.2 @SystemApi in LocationManager

LocationManager exposes system-only APIs (from API surface files):
- `flushGnssBatch()` -- requires `LOCATION_HARDWARE`
- `getCurrentLocation(LocationRequest, ...)` -- overloaded version with `LocationRequest`
- `getExtraLocationControllerPackage()` / `setExtraLocationControllerPackage()`

### 14.3 @UnsupportedAppUsage Annotations

Many hidden APIs are annotated with `@UnsupportedAppUsage` to track non-SDK interface usage:
```java
// ServiceManager, line 35
@UnsupportedAppUsage
private static IServiceManager sServiceManager;

// ServiceManager, line 127
@UnsupportedAppUsage
public static IBinder getService(String name)
```

Android 11 enforces restrictions on these APIs based on the `maxTargetSdk` parameter.

### 14.4 ILocationManager Hidden Operations

The AIDL interface (lines 123-130) includes internal-only operations:
```
// --- internal ---
void locationCallbackFinished(ILocationListener listener);
String[] getBackgroundThrottlingWhitelist();
String[] getIgnoreSettingsWhitelist();
```

### 14.5 Test APIs

```java
// LocationManager @TestApi methods
void addTestProvider(...)
void removeTestProvider(...)
void setTestProviderLocation(...)
void setTestProviderEnabled(...)
```

---

## 15. Key Findings and Recommendations

### 15.1 Architecture Observations

1. **Massive File Sizes**: AMS (20,423 lines) and PMS (25,775 lines) are among the largest files in AOSP. While Android 10 began splitting AMS by moving activity management to ATMS, these files remain difficult to maintain and understand.

2. **Boot Order Dependencies**: The three-phase boot (bootstrap, core, other) with explicit boot phases provides structured initialization, but the comment at SystemServer line 342 acknowledges: "TODO: remove all of these references by improving dependency resolution and boot phases."

3. **Mixed Registration Patterns**: Some services use `ServiceManager.addService()` directly while others use `SystemServiceManager.startService()`. This inconsistency makes it harder to trace service initialization.

4. **Binder Thread Limit**: The system server uses 31 Binder threads (line 327). Under heavy load, this can become a bottleneck, causing `TransactionTooLargeException` or delayed service responses.

### 15.2 Security Observations

1. **Permission Enforcement Consistency**: AMS has 30+ distinct `enforceCallingPermission()` / `checkCallingPermission()` call sites. The lack of a centralized permission policy makes auditing difficult.

2. **Identity Clearing Risks**: The `clearCallingIdentity()` / `restoreCallingIdentity()` pattern is used extensively but is error-prone. If `restoreCallingIdentity()` is missed (e.g., due to an exception not caught in the finally block), subsequent operations would run with elevated privileges.

3. **Accessibility Service Power**: Accessibility services effectively gain full screen access once enabled. The security boundary is entirely the user's explicit enablement, with no runtime permission prompts.

4. **Notification Listener Access**: Similar to accessibility services, notification listeners get access to all notification content. The low-RAM device restriction (line 91) is a pragmatic limitation, not a security measure.

### 15.3 Developer-Facing Observations

1. **Sensor Battery Drain**: The SensorManager documentation explicitly warns about battery drain (lines 39-42), but the system does not auto-unregister sensors when activities are paused. This remains a common source of battery issues in apps.

2. **Camera Service Architecture**: The Camera2 API's `CameraManagerGlobal` singleton (line 1090) implements `IBinder.DeathRecipient` for camera service death handling. Developers must handle `CameraAccessException` gracefully since the camera service can be restarted independently.

3. **Location Background Restrictions**: Android 11's location permission model requires careful handling. The `ACCESS_BACKGROUND_LOCATION` permission must be requested separately, and users are prompted to grant it via Settings, not a runtime dialog.

### 15.4 Key File Reference Table

| File | Path | Lines | Purpose |
|------|------|-------|---------|
| SystemServer.java | `frameworks/base/services/java/com/android/server/SystemServer.java` | 2,567 | Boot orchestration |
| ActivityManagerService.java | `frameworks/base/services/core/java/com/android/server/am/ActivityManagerService.java` | 20,423 | Process/component lifecycle |
| PackageManagerService.java | `frameworks/base/services/core/java/com/android/server/pm/PackageManagerService.java` | 25,775 | Package management |
| WindowManagerService.java | `frameworks/base/services/core/java/com/android/server/wm/WindowManagerService.java` | 8,278 | Window management |
| LocationManagerService.java | `frameworks/base/services/core/java/com/android/server/location/LocationManagerService.java` | ~2,500 | Location services |
| LocationManager.java | `frameworks/base/location/java/android/location/LocationManager.java` | ~1,800 | Location client API |
| SensorManager.java | `frameworks/base/core/java/android/hardware/SensorManager.java` | ~1,200 | Sensor client API |
| CameraManager.java | `frameworks/base/core/java/android/hardware/camera2/CameraManager.java` | ~1,400 | Camera2 client API |
| Camera.java | `frameworks/base/core/java/android/hardware/Camera.java` | ~2,400 | Legacy camera API |
| AccessibilityManagerService.java | `frameworks/base/services/accessibility/java/com/android/server/accessibility/AccessibilityManagerService.java` | ~5,000 | Accessibility services |
| NotificationListenerService.java | `frameworks/base/core/java/android/service/notification/NotificationListenerService.java` | ~1,400 | Notification listener API |
| ServiceManager.java | `frameworks/base/core/java/android/os/ServiceManager.java` | ~300 | Service registry |
| Binder.java | `frameworks/base/core/java/android/os/Binder.java` | ~1,200 | IPC base class |
| IActivityManager.aidl | `frameworks/base/core/java/android/app/IActivityManager.aidl` | ~600 | AMS Binder interface |
| IPackageManager.aidl | `frameworks/base/core/java/android/content/pm/IPackageManager.aidl` | ~800 | PMS Binder interface |
| IWindowManager.aidl | `frameworks/base/core/java/android/view/IWindowManager.aidl` | ~400 | WMS Binder interface |
| ILocationManager.aidl | `frameworks/base/location/java/android/location/ILocationManager.aidl` | 131 | Location Binder interface |
| SystemService.java | `frameworks/base/services/core/java/com/android/server/SystemService.java` | ~200 | Boot phase definitions |
| SystemServiceManager.java | `frameworks/base/services/core/java/com/android/server/SystemServiceManager.java` | ~400 | Service lifecycle management |

---

*Report generated from Android 11 AOSP source code review. All line numbers reference the source files at `~/aosp-android-11/`.*
