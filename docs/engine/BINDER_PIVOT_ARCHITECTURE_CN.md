**[English](BINDER_PIVOT_ARCHITECTURE.md)** | **[中文](BINDER_PIVOT_ARCHITECTURE_CN.md)**

# Westlake Binder 转向 — 合并架构文档

**状态：** 权威版本（2026-05-14）。取代 `BINDER_PIVOT_DESIGN.md`（V1，2026-05-12）与 `BINDER_PIVOT_DESIGN_V2.md`（V2，2026-05-13），两份原始文档作为历史参考逐字保留。
**配套文档（前瞻性，仍然有效）：**
- `BINDER_PIVOT_MILESTONES.md` — M1-M17 规范化工作分解
- `CR41_PHASE2_OHOS_ROADMAP.md` — Phase 2 OHOS 预先规划（M9-M13）
- `CR60_BITNESS_PIVOT_DECISION.md` — DAYU200 上的 32 位 ARM dalvikvm 转向（2026-05-14）
- `CR61_BINDER_STRATEGY_POST_CR60.md` — 位宽转向之后的 binder 策略
- `MIGRATION_FROM_V1.md` — 具体的逐文件 V1 → V2 迁移图

---

## 0. 概述

Westlake 通过把 Android 框架视为可嵌入的运行时引擎来承载未修改的 Android APK。在 Phase 0 阶段验证了 Android 应用的视图树可以通过 Westlake 管线渲染之后，引擎进入了一段为期数月的逐类 shim 开发期，并且这种方式无法扩展——每接入一个新应用就会暴露约 5 个新的框架服务缺口需要自定义 shim 代码。问题诊断结论：引擎在框架类级别做替换，而该层 API 表面分散在数百个类上。

**V1（binder 转向，2026-05-12）** 修正了跨进程边界：在 **Binder 服务边界** 而不是框架类层面做替换。由 Westlake 自有的 `libbinder.so` + `servicemanager` + AOSP 自动生成的 `IXxxService.Stub` 子类统一处理每一个框架到服务的调用，AOSP 自身的同进程 `Stub.asInterface` 优化为进程内 Java 到 Java 调用消除了 marshaling。AOSP framework.jar 在 `Stub.asInterface` / Parcel / BinderProxy 这条线上保持不变运行。

**V2（substrate 精修，2026-05-13）** 修正了进程内 Java 边界：放在 `-Xbootclasspath` 上的真实 AOSP framework 代码缺少 `system_server` 的冷启动字段图谱会在 `Activity.attach`、`Application.attach`、`Resources.updateConfiguration` 等位置 NPE。逐字段地补这些状态是无界的"叠加式 shimming"——正是 V1 § 6.2 禁止的反模式，只是套上了服务 substrate 的外衣而已。V2 在 `Activity.attach` / `Application.attach` / `Window.<init>` 处放置第二个替换边界：Westlake 自有、通过 classpath shadow 实现的 `Application`、`Activity`、`Resources`、`Window`、`PhoneWindow`、`DecorView`、`WindowManagerImpl` 替换框架的冷启动路径；这些类以下的一切（`View`、`ViewGroup`、widget、`Handler`/`Looper`、自动生成的 `IXxxManager.Stub` 代理）继续不加修改地从 `framework.jar` 运行。

**当前状态（2026-05-14）：**
- M1-M4 binder substrate 已完成：musl + bionic `libbinder.so`、`servicemanager`、6 个 Java 服务（Activity / Power / Window / Display / Notification / InputMethod）外加 `WestlakePackageManagerStub`。
- M5 音频守护进程已能 **发声** ——通过 AAudio 后端复现了 440 Hz 测试音。
- M6 surface 守护进程以 60 Hz 垂直同步运行，采用 memfd 的 `GraphicBuffer` 传输，且与 Android 15 wire 兼容。
- V2 substrate 已落地：通过 `framework_duplicates.txt` 部署的 classpath shadow 的 `WestlakeActivity` / `WestlakeApplication` / 精简 Resources / Window+PhoneWindow / DecorView / WindowManagerImpl；新增 `Unsafe`/`setAccessible` 调用数为零。
- **M7-Step2（noice）和 M8-Step2（McD）都通过生产路径 `Instrumentation.callActivityOnCreate` 到达 `MainActivity.onCreate(Bundle)` 用户代码体**。今天（2026-05-14）关闭的 CR59 修复了 `Application.mBase=null`，两个应用现在都能干净地完成 `onCreate`。回归测试 14/14 PASS。

Phase 2（M9-M13）以 OHOS 为目标。OHOS 标准系统内核默认就启用了 binder（`CONFIG_ANDROID_BINDER_IPC=y`）；守护进程通过预先搭好的 `#ifdef OHOS_TARGET` 块把 AAudio/DLST 管道后端切换为 OHOS AudioRenderer / XComponent。Phase 2 预算：预期约 13 个人日（`CR41_PHASE2_OHOS_ROADMAP.md`），受限于硬件采购。CR60（2026-05-14）将 dalvikvm 切到 DAYU200 上的 32 位 ARM，因为该板卡的用户空间只有 32 位；CR61（2026-05-14）确定了 CR60 之后的 binder 策略——通过 `/dev/vndbinder` 提供我们自己的 libbinder + servicemanager，不与 OHOS 的 `libipc`/`samgr` 联邦。

该架构在精神上更接近精简版 Anbox，而不是最初的"引擎加 shim 层"模式，但比 Anbox 轻约 5 倍（不包含 zygote、init、完整 HAL、完整系统镜像或隔离命名空间）。常驻内存估计：~150 MB 活动，对比 Anbox 的 ~700 MB-1 GB。

---

## 1. 背景——我们是如何走到这一步的

### 1.1 转向之前的原始引擎

转向之前的 Westlake 引擎在 **框架类** 级别做替换：
- 约 2,300 个 Java 源文件 / 约 30 万 LOC 的 `android.*`、`androidx.*` 与 `com.westlake.engine.*` shim。
- `MiniActivityManager` 是 `getSystemService(ACTIVITY_SERVICE)` 返回的对象——AOSP `IActivityManager` 代理的简化重实现。
- `WestlakeLauncher`（最初约 23K LOC）编排应用加载、字段植入、渲染循环、McD 引导。
- `WestlakeFragmentLifecycle`（3,087 LOC，C1 中已删除）——渲染期反射式 fragment 生命周期驱动器，含每应用常量（`MCD_SIMPLE_PRODUCT_HOLDER_ID`、`MCD_ORDER_PDP_FRAGMENT`）。
- `DexLambdaScanner`（约 600 LOC，随 C1 删除）——用于按应用发现 Lazy<T> 类型的 dex 字节码解析器。
- OHBridge `liboh_bridge.so` —— Java/native 到 OHOS surface/audio/input 边缘的约 15 个 JNI 桥接。
- 约 50 个 ART 补丁（`PF-arch-001..051`）修复 dalvikvm 缺陷。

可运行的应用：Counter、Tip Calculator、TODO List、MockDonalds、McDonald's（真实 APK）。全部为单 Activity、简单生命周期、轻量多 fragment 导航。

### 1.2 失败的模式（以及失败的原因）

Westlake 在多 fragment 导航、深度 DI 或非平凡 NavController 使用的应用上撞墙。**noice**（`com.github.ashutoshgngwr.noice`）——多 fragment NavController + BottomNavigation + Hilt——暴露了这一模式：

1. 应用在框架服务的 NPE 处崩溃。
2. 修一个 shim 或植入字段。
3. 应用前进约 50 个 dex 字节码。
4. 在下一个框架服务的 NPE 处崩溃。
5. 循环。

任务追踪器上最近的 #86-#97 全都遵循该模式，每个解锁约 50 个 dex 字节码。诊断的高潮是 `Service.getServiceWithMetadata()` 返回 null，因为 Android 16 的 `ServiceManager.rawGetService` 把结果包装在一个携带元数据的对象里，而我们的 shim 没生成它——又是一个 shim 机会。模式还会继续下去。

**根因：** shim 层的替换点错了。AOSP framework 代码通过从 `ServiceManager.getService("activity")` 拿到的服务句柄调用 `IActivityManager`；shim 用精简的 Java 重实现替换了 `IActivityManager`。**AOSP framework 想调用的每个方法，我们都得知道并实现。** 扇出是数百个类乘以每个类几十个方法。Codex 二次审阅（2026-05-12）独立确认：

> (a) "边界是对的：dalvikvm 内的 framework/类加 mini 系统服务，仅平台边缘有 OHBridge。"
> (b) "[WestlakeFragmentLifecycle] 不符合架构：渲染器触发的 fragment 实例化……重复了 FragmentManager.addFragmentInternal。"
> (e) "FragmentManager/LayoutInflater 中的 per-McD 常量是架构债务；只要不除掉它们就会持续扭曲推理。"

### 1.3 认知层面的修正——减法式验证

我们当时在做 **叠加式 shimming**：观察到 NPE，加一个 shim，观察下一个 NPE，再加一个 shim。正确做法是 **减法式验证**：从一个完全可工作的基线（真实 Android，noice 在那里渲染完美）出发，逐层移除，观察是哪一层的移除首先破坏了渲染。首先破坏渲染的那一层是承重的；其上的一切都是多余工作。本文档把减法确立为发现 Westlake 需要实现什么的 **唯一允许的** 方式。

---

## 2. V1 — binder 替换洞见

### 2.1 为什么 Binder 是正确的切口

Binder 是 Android 中 **唯一** 的统一服务接口。每一次框架到系统服务的调用都经过它。如果 Westlake 拥有 Binder 层：

- **AOSP framework.jar 不加修改地运行。** 框架通过 `ServiceManager` 按名字查服务，拿回一个 `IBinder`，调用 `IXxxService.Stub.asInterface(binder).someMethod(...)`。无论实现位于进程内、另一进程、还是别处，框架并不关心。
- **AOSP 自身的同进程优化白拿。** `Stub.asInterface(IBinder)` 会检查 `queryLocalInterface()`，当服务 Binder 位于调用者进程内时直接返回本地 Java 对象。没有 Parcel marshaling、没有 IPC，就是一次方法调用。进程内 Java 服务实现不必付出 Binder 开销。
- **AOSP native 库不加修改地运行。** `libaudioclient.so`、`libcamera2_jni.so`、`libgui.so` 链接 `libbinder.so` 并对话 `/dev/binder`。只要提供一个真实的 binder（内核 + 用户态），它们就能运行。
- **服务版本韧性。** 一个 AOSP 版本内 AIDL 事务码是稳定的——切换 framework.jar 时不必去追那些被改名的框架类。
- **所有服务工作的单一替换点**，Java 与 native 都包括。取代当前 shim 化框架类 + 精简 manager 类 + JNI 桥 + 渲染器旁路的混乱局面。

### 2.2 四种构件

1. **`libbinder.so`** —— Westlake 自有，从 AOSP 源码重新编译到 musl（在 Android 手机验证时同时编 bionic）。负责 `Binder`、`BBinder`、`BpBinder`、`IPCThreadState`、`Parcel`、`ProcessState`。和 `/dev/binder`（或 `/dev/vndbinder`）对话以做跨进程 IPC；常见的单进程情形里 AOSP 通过 `localBinder()` 直接绕过 ioctl。
2. **`servicemanager`** —— 从 AOSP 源码重新编译到 musl。内核交付 `binder context 0` 的"通讯录"守护进程。应用和服务用它来注册 / 查找命名的 Binder 对象。
3. **Java 服务实现** —— 继承 AOSP 自动生成的 `IXxxService.Stub` 类的 Java 类。在 dalvikvm 进程内运行。引擎启动时通过 `ServiceManager.addService("xxx", new WestlakeXxxService())` 注册。同进程优化让 Java 到 Java 的调用本质上就是直接方法分发。
4. **Native 守护进程** —— 通过真实 Binder 实现具体服务契约的独立 native 进程。AOSP 原生库经由 libbinder 时（音频、相机、媒体、surface）需要它们。每个守护进程内部把自己的服务契约翻译到 OHOS API。

Westlake 与 OHOS 的边界活在 **每个 native 守护进程内部** —— 通常是每个守护进程底部约 500 行 C++，位于 `#ifdef OHOS_TARGET` 后面。这一行以上的所有内容都是 AOSP 形状的 Binder 服务代码，跨平台完全一致。

### 2.3 架构图（跨进程层）

```
┌─ Linux 内核 ─────────────────────────────────────────────────┐
│  CONFIG_ANDROID_BINDER_IPC=y                                 │
│  /dev/binder | /dev/vndbinder | /dev/hwbinder                │
│  （OHOS 标准系统内核默认就提供这些设备）                     │
└──────────────────────────────────────────────────────────────┘

┌─ 用户态（OHOS 或 Android 上的 Westlake）─────────────────────┐
│                                                              │
│ ┌─ servicemanager（约 3 MB 常驻）────────────────────────┐   │
│ │  AOSP 源码，musl/bionic 重编                           │   │
│ │  从内核接收 binder context 0                           │   │
│ └────────────────────────────────────────────────────────┘   │
│                                                              │
│ ┌─ dalvikvm 进程（约 70 MB 常驻）────────────────────────┐   │
│ │                                                        │   │
│ │ ┌─ AOSP framework.jar（基本不变）──────────────────┐   │   │
│ │ │  • View / ViewGroup / TextView / Layout / 控件   │   │   │
│ │ │  • Handler / Looper / MessageQueue / AsyncTask   │   │   │
│ │ │  • Stub.asInterface / Parcel / BinderProxy       │   │   │
│ │ │  • 自动生成的 IXxxManager.Stub 代理              │   │   │
│ │ └──────────────────────────────────────────────────┘   │   │
│ │                                                        │   │
│ │ ┌─ V2 substrate（Westlake 自有，classpath shadow）┐   │   │
│ │ │  WestlakeApplication / WestlakeActivity          │   │   │
│ │ │  WestlakeContextImpl / 精简 WestlakeResources    │   │   │
│ │ │  Window / PhoneWindow / DecorView /              │   │   │
│ │ │  WindowManagerImpl（stub，无系统装饰）           │   │   │
│ │ └──────────────────────────────────────────────────┘   │   │
│ │                                                        │   │
│ │ ┌─ 应用 classes.dex（不变）───────────────────────┐   │   │
│ │ │  noice / McDonald's / TikTok / Instagram         │   │   │
│ │ └──────────────────────────────────────────────────┘   │   │
│ │                                                        │   │
│ │ ┌─ Westlake Java 服务实现（进程内）────────────────┐   │   │
│ │ │  WestlakeActivityManagerService                  │   │   │
│ │ │  WestlakeWindowManagerService                    │   │   │
│ │ │  WestlakePackageManagerService                   │   │   │
│ │ │  WestlakeNotificationManagerService              │   │   │
│ │ │  WestlakeInputMethodManagerService               │   │   │
│ │ │  WestlakeDisplayManagerService                   │   │   │
│ │ │  WestlakePowerManagerService                     │   │   │
│ │ │  （启动时通过 servicemanager 注册；              │   │   │
│ │ │   同进程 Stub.asInterface 跳过 marshal）         │   │   │
│ │ └──────────────────────────────────────────────────┘   │   │
│ │                                                        │   │
│ │ ┌─ AOSP native 库（不变）──────────────────────────┐   │   │
│ │ │  libaudioclient.so → libbinder → 音频守护进程    │   │   │
│ │ │  libgui.so         → libbinder → surface 守护进程│   │   │
│ │ │  libcamera2_jni.so → libbinder → 相机守护进程    │   │   │
│ │ │  libmediandk.so    → libbinder → 媒体守护进程    │   │   │
│ │ └──────────────────────────────────────────────────┘   │   │
│ │                                                        │   │
│ │ ┌─ Westlake libbinder.so（musl/bionic 重编）───────┐   │   │
│ │ │  ProcessState、IPCThreadState、Parcel、BBinder、 │   │   │
│ │ │  BpBinder。对话 /dev/vndbinder。                 │   │   │
│ │ └──────────────────────────────────────────────────┘   │   │
│ └────────────────────────────────────────────────────────┘   │
│                                                              │
│ ┌─ Westlake native 守护进程（按需启动）──────────────────┐   │
│ │  westlake-audio-daemon（约 15 MB）                     │   │
│ │      通过真实 binder 实现 IAudioFlinger                │   │
│ │      后端：AAudio（Android） | AudioRenderer（OHOS）   │   │
│ │  westlake-surface-daemon（约 25 MB）                   │   │
│ │      通过真实 binder 实现 ISurfaceComposer             │   │
│ │      后端：SurfaceView pipe（Android） |               │   │
│ │             XComponent surface（OHOS）                 │   │
│ │  westlake-camera-daemon（约 10 MB，按需）              │   │
│ │  westlake-media-daemon（约 20 MB，按需）               │   │
│ └────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

### 2.4 Parcel kHeader 发送/接收不对称

每一个内核 binder 事务负载开头都有一个 4 字节 `kHeader` 魔数，`Parcel::readInterfaceToken()` 在分派前会校验它。AOSP 根据编译期设置的 `__ANDROID_*` 宏从 `'SYST'` / `'VNDR'` / `'RECO'` / `'UNKN'` 里选一个。Westlake 的 libbinder 落在 AOSP 的"非 Android"分支里，那里 AOSP 默认 `'UNKN'`——但 Android `system_server`（期望 `'SYST'`）和 OHOS 厂商 binder 端点（期望 `'VNDR'`）都拒收。

**发送端方案：** `aosp-libbinder-port/patches/0003-parcel-kheader-syst.patch` 让选择项可在编译期通过 Makefile 的 `KHEADER_FLAG` 切换：
- `-DWESTLAKE_KHEADER_SYST=1` → `'SYST'`（默认；Phase 1 沙箱互操作）
- `-DWESTLAKE_KHEADER_VNDR=1` → `'VNDR'`（Phase 2 标准 OHOS 厂商互操作）
- 都不定义 → `'UNKN'`（AOSP 默认，未修改）

**接收端方案（CR11 后续，2026-05-12）：** 0003 补丁只控制 *发送* 端。2026-05-12 OnePlus 6 的 `vndservicemanager` 开始在回复里写 `'VNDR'`；Westlake 的 libbinder（烤死 `'SYST'`）拒收每一个事务并报 `Expecting header 0x53595354 but found 0x564e4452`。`aosp-libbinder-port/patches/0004-parcel-accept-any-kheader-on-recv.patch` 把接收端比较扩展为 `{'SYST', 'VNDR', 'RECO', 'UNKN'}` 集合成员检查。发送端仍然写 `kHeader` 以便对端识别我们，但四字节魔数不再是门禁。这与 AOSP 自身跨分区的行为一致——分区切分是在更上层通过 SELinux 强制的，而不是通过这个魔数。

| 操作 | 位置 | 行为 | 理由 |
|---|---|---|---|
| 发送 | `writeInterfaceToken` | 写入 `kHeader`（今天通过 `WESTLAKE_KHEADER_SYST=1` 为 `'SYST'`） | 对端可在日志里识别我们；0003 补丁。 |
| 接收 | `enforceInterface` | 接受 `'SYST' | 'VNDR' | 'RECO' | 'UNKN'` | 跨分区 / 跨厂商互操作；0004 补丁。 |

---

## 3. V2 — 进程内 substrate 精修

### 3.1 V1 说了什么、我们听成了什么

V1 说的是 *"AOSP framework.jar 不加修改地运行"*。原意是：`ServiceManager.getService()`、`Stub.asInterface()` 以及 framework.jar 中的 marshaling 部分不加修改地运行，因为 Westlake 的 libbinder + Stub 子类服务满足它们。

实际发生的事，从 M4-PRE6 开始：把真实 `framework.jar` 和 `aosp-shim.dex` 一起放到 `-Xbootclasspath` 上，并通过 `framework_duplicates.txt`（CR23-fix § 49）从 shim 中剥离 `Context` / `Activity` / `ContextImpl` / `LoadedApk` / `Resources` / `AssetManager` / `Configuration`，让 framework.jar 的副本赢得类解析。目标：让 AOSP 自己的框架类一路跑通到 Westlake 的 Binder 服务。

**错误所在：** AOSP 框架类并不是纯传输代码。`Activity.attach(...)` 用 `mApplication`、`mResources`、`mWindow`、`mWindowManager`、`mInstrumentation`、`mUiThread`、`mMainThread`、`mActivityInfo`、`mIdent` 等输入做了 ~120 行状态搭建——这些字段是 AOSP `ActivityThread.handleLaunchActivity` 在进程启动期间根据 `system_server` 推过来的 `IBinder` 句柄、`ActivityClientRecord`、`LoadedApk`、`CompatibilityInfoHolder` 构造出来的。没有那次引导，所有字段都是 JVM 默认 null，`Activity.attach` 的第一次解引用就会击中 null。

### 3.2 表格化的漂移

| 里程碑 | 症状 | 应用的"修复" | 架构代价 |
|---|---|---|---|
| M4-PRE12（2026-05-12） | `AssetManager.getResourceText` → 在 `outValue`（private final `mValue`）上 NPE | 反射植入 `mValue = new TypedValue()` 和 `mOffsets = new long[2]` | +33 LOC；推进约 200 dex 字节码 |
| M4-PRE13（2026-05-12） | `Locale.toLanguageTag()` NPE；合成 `Configuration.mLocaleList` 是 null | 通过 `Configuration.setLocales` + 直接 mLocaleList 字段反射植入 `LocaleList(Locale.US)` | +58 LOC；推进约 100 dex 字节码 |
| M4-PRE14（2026-05-12） | `DisplayAdjustments.getCompatibilityInfo()` NPE；合成 `ResourcesImpl.mDisplayAdjustments` 是 null | 在 `ResourcesImpl` 上反射植入 `new DisplayAdjustments()` | +102 LOC；验证被无关 SIGBUS 阻塞 |
| CR23-fix（2026-05-13） | `Activity.attach(6 参数)` 在 framework 的 `Activity` 上不存在（17+ 参数）；NoSuchMethodError | 把 6 参数 `activity.attach(...)` 包在 `try { ... } catch (NoSuchMethodError | LinkageError)` 里；继续走遗留的字段直设路径 | +95 LOC |
| CR25-attempt（2026-05-13） | Activity → `setTheme` → `Window.setTheme(int)` 在 null `mWindow` 上 | （调查中途放弃）——范围膨胀 | n/a；用户在此处指出漂移 |

### 3.3 为什么"补齐缺失字段"是无界的

AOSP 进程引导（`ActivityThread.handleBindApplication`、`LoadedApk.makeApplication`、`ContextImpl.createAppContext`、`Resources.updateConfiguration` 等）大约会填充：
- `ActivityThread`：约 40 个实例字段
- `LoadedApk`：约 25 个字段
- `ContextImpl`：约 30 个字段（12 参数内部构造器）
- `Resources` / `ResourcesImpl`：`mAssets`、`mDisplayAdjustments`、`mCompatibilityInfo`、`mConfiguration`、`mMetrics`、`mAccessLock`、`mLocaleAdjustments` 等
- `Configuration`：`mLocaleList`、`locale`、`mLocale`、`screenLayout`、`densityDpi`、`screenWidthDp`、`screenHeightDp`、`smallestScreenWidthDp`、`uiMode`、`orientation` 等
- `AssetManager`：`mValue`（private final TypedValue）、`mOffsets`（private final long[2]）、native handle、theme 属性缓存等
- `DisplayAdjustments`：`mCompatInfo`、`mFixedRotationAdjustments`（API 30+）
- `Activity` 在 `attach` 期间：`mMainThread`、`mApplication`、`mIntent`、`mReferrer`、`mComponent`、`mActivityInfo`、`mTitle`、`mParent`、`mEmbeddedID`、`mLastNonConfigurationInstances`、`mWindow`（真实 PhoneWindow）、`mWindowManager`（真实 WindowManagerImpl）、`mCurrentConfig` 等
- `PhoneWindow`：`mDecor`、`mLayoutInflater`、`mContext`、`mForcedWindowFlags`、`mWindowStyle`（从 `R.styleable.Window` 解析出来的 TypedArray）等

总计远超 200 个字段。M4-PRE12/13/14 覆盖了三个。没有任何架构理由认为接下来 200 个会比前三个收敛得更快。

### 3.4 修正后的架构事实

**真实的 AOSP `framework.jar` 代码会在冷启动状态上 NPE。要么我们补状态（漂移），要么我们不跑那段代码。**

V2 选择：**不跑那段代码。** 具体说：不跑 `Activity.attach`、`Activity.attachBaseContext`（framework 那个）、`Window.<init>`、`PhoneWindow.<init>`、`Resources.updateConfiguration`，或任何会触及 `ActivityThread` 构造出来的引导图谱的框架路径。

Westlake **从 `framework.jar` 中不加修改地、进程内运行：**
- `android.view.View` 与子类（注意 § 3.7 关于 `mAttachInfo == null` 的注解）
- `android.view.ViewGroup`
- `android.widget.*`（TextView、Button、ImageView——基本无状态的叶子 widget）
- `android.os.Handler`、`Looper`、`MessageQueue`（M4-PRE4 MessageQueue JNI 已去风险）
- `android.os.Bundle`、`android.os.Parcel`（数据类）
- `android.util.*`（Log、TypedValue 等）
- `android.graphics.*` 数据类
- Binder marshaling 层（`Stub` / `Stub.asInterface` / `Parcel.writeStrongBinder` / `BinderProxy`）
- 我们的 Westlake 服务继承的、自动生成的 `IXxxManager.Stub` / `IXxxManager.Stub.Proxy` 类

Westlake **通过 classpath shadow 替换（不从 framework.jar 运行）：**
- `android.app.Application`
- `android.app.Activity`（沿继承链向上，FragmentActivity / AppCompatActivity / ComponentActivity 继续可用，因为它们只调用公共 / 受保护的 `Activity` API）
- `android.content.res.Resources`（精简的、Westlake 自有的表面，直接读 `resources.arsc`）
- `android.view.Window`、`com.android.internal.policy.PhoneWindow`、`com.android.internal.policy.DecorView`、`android.view.WindowManagerImpl`（stub；无系统装饰）

### 3.5 Classpath shadow 技术

对每个被 shadow 的类，Westlake：
1. 用与框架类相同的全限定名定义 `shim/java/<fqn>.java`。
2. 把该全限定名加入 `framework_duplicates.txt`，从 `aosp-shim.dex` 剥离 framework 的副本。
3. 编排 classpath 顺序，让 shim 在运行期赢得类解析。

应用的 `MainActivity extends AppCompatActivity extends FragmentActivity extends ComponentActivity extends Activity` 把 `Activity` 解析为 Westlake 的类。AppCompatActivity / FragmentActivity / ComponentActivity（位于 androidx，不在 framework.jar 中）继续可用，因为它们只调用公共 / 受保护的 `Activity` API。`instanceof android.app.Activity` 检查通过。

该 substrate 继承了 AOSP 的公共 / 受保护 API 表面——Activity 约 200 个方法、Resources 约 80 个、Theme 约 10 个、AssetManager 约 30 个——都是有文档的、自 API 14 起稳定的。**没有 `mLoadedApk` 这种内部状态**，没有冷启动字段级联，没有 per-app 分支。

### 3.6 切口在哪里

**真实的 `framework.jar` 代码一直运行到应用的 Application/Activity 构造器退出。** 从此刻起 Westlake 自有的 classpath shadow 版本接手。真实框架代码在 `View`、`ViewGroup`、widget 类、`Handler`/`Looper`、Binder marshaling 以及自动生成的 `IXxxManager.Stub` 代理内部恢复——这些在进程内都能干净跑，因为它们不期望 `system_server` 引导状态。

**架构红利：**
- 应用的 `Application.onCreate` 会运行，包括 Hilt 注入字段，那些字段会向 M4 服务发起真实的 Binder 事务。（V1 红利保留。）
- 应用的 `Activity.onCreate` 会运行，包括 `setContentView(int)` 通过 Westlake 的 `WestlakeLayoutInflater` 加载真实布局（通用，非 per-app）。
- 真实的 androidx Fragment 生命周期会运行——一旦 `getActivity()` 返回一个具备 `findViewById`、`getResources`、`getSystemService` 的合格 `WestlakeActivity`，整个 androidx.fragment 图都能无 per-app 代码地工作。我们删掉的 3,087 LOC `WestlakeFragmentLifecycle` **不需要替代品**——它在解决一个不复存在的问题。
- 真实的 `View` / `RecyclerView` / `ConstraintLayout` measure/layout/draw 会运行。

Per-app shimming 在结构上变得不可能：每个应用接触的都是同一份 `WestlakeActivity` / `WestlakeApplication` / `WestlakeResources`，跨所有 APK 通用。

### 3.7 第 13 行注解：来自 framework.jar 的真实 View / ViewGroup

`android.view.View` 有一个 `mAttachInfo` 字段，在 View 附加到 Window 时由 `ViewRootImpl` 填充。许多 View 方法在 `mAttachInfo == null` 时提前返回。**这对我们有利** —— 这是框架自己的"我还没附加"哨兵。Westlake 的 Activity / Window 不附加 `ViewRootImpl`；`mAttachInfo` 保持 null；依赖它的 View 代码会干净地短路。

唯一的小问题：`View.invalidate()` 沿父链向上走，最终调用 `ViewRootImpl.invalidateChild(this, dirty)`。如果 `mAttachInfo == null`，向上的遍历停止。Westlake 的渲染循环以帧节奏轮询 View 树（M6 设计），不是对 `invalidate()` 作反应。

### 3.8 宏 shim 反漂移契约

V2 设计由一份明确的契约强制（位于 agent 记忆中的 `feedback_macro_shim_contract.md`），每一份 Builder 简报必须逐字包含它。**禁止：**
- 对 framework.jar 类的 `sun.misc.Unsafe.allocateInstance(...)`
- `Field.setAccessible(true)` + 对 framework.jar 内部字段的反射设置
- 在 framework 的 `ResourcesImpl`、`AssetManager`、`Configuration`、`Theme`、`ActivityThread`、`LoadedApk`、`ContextImpl` 等上"植入"状态
- Per-app 分支（`if (pkg.equals("com.mcdonalds.app")) ...`）
- `try { realFrameworkCold(...); } catch (NoSuchMethodError | LinkageError) { fallback() }` 这类绕过模式
- 给 `WestlakeContextImpl` 加新方法（CR22 冻结）

**允许：** 在 Westlake 自有的类（classpath shadow 的框架类加上 Westlake 自有服务）上实现公共 / 受保护的 API 方法，每个方法体须为以下之一：(a) 与 AOSP 默认逐字一致，(b) 安全原语（返回 null / false / 0 / 空列表 / no-op），或 (c) 委托给我们自有的类的另一个方法。对真正无法实现的方法使用 `ServiceMethodMissing.fail(...)`（CR2 模式）。完成前运行自审门禁。

---

## 4. 已经构建的部分

### 4.1 Phase 1 里程碑 —— substrate

| 里程碑 | 状态 | 备注 |
|---|---|---|
| M1 — Westlake `libbinder.so`（musl + bionic） | 完成 | 补丁 0003（kHeader 发送）+ 0004（kHeader 接收）落地 |
| M2 — Westlake `servicemanager`（musl + bionic） | 完成 | |
| M3 — `ServiceManager.java` + 7 个 JNI native + JavaBBinder | 完成 | |
| M3++ — 同进程 `Stub.asInterface` 跳过（`nativeGetLocalService`） | 完成 | V1 单个最重要的优化 |
| M4a — `WestlakeActivityManagerService` | 完成 | |
| M4b — `WestlakeWindowManagerService` | 完成 | |
| M4c — `WestlakePackageManagerService`（binder 端） | 完成 | |
| M4d — `WestlakeDisplayManagerService` | 完成 | |
| M4e — `WestlakeNotificationManagerService`、`WestlakeInputMethodManagerService` | 完成 | |
| M4-power — `WestlakePowerManagerService` | 完成 | |
| M4-PRE4 — MessageQueue JNI（6 个 native） | 完成 | Handler/Looper/MessageQueue 仍为 AOSP 原生 |
| M4-PRE5 — `WestlakePackageManagerStub`（本地 PM） | 完成 | CR19 fail-loud 仍在 |
| M5 — westlake-audio-daemon | 可发声 | 通过辅助进程的 AAudio 后端发出 440 Hz 测试音 |
| M6 — westlake-surface-daemon | 60 Hz vsync | memfd `GraphicBuffer`；与 Android 15 wire 兼容 |

### 4.2 V2 substrate 落地

| 组件 | 状态 | 备注 |
|---|---|---|
| Classpath shadow 基础设施（`framework_duplicates.txt`） | 完成 | CR23-fix 为 `Context` 提供了模板；扩展到 `Application`/`Activity`/`Window` |
| `WestlakeApplication`（shadow `android.app.Application`） | 完成 | 全部公共 API；AOSP 默认实现 |
| `WestlakeActivity`（shadow `android.app.Activity`） | 完成（V2-Step2） | 约 1,500 LOC；6 参数 attach；生命周期驱动；`setContentView` 走 `WestlakeLayoutInflater` |
| 精简 `WestlakeResources` | 完成 | 直接读 `resources.arsc`；无合成 framework Resources/ResourcesImpl/AssetManager |
| `Window`/`PhoneWindow`/`DecorView`/`WindowManagerImpl` stub | 完成 | 无系统装饰、无 action bar |
| M4-PRE12/13/14 植入代码 | 已删除 | 反射字段植入随 V2 过渡被移除 |
| `WestlakeFragmentLifecycle`（3,087 LOC） | 已删除（C1） | 用在 `WestlakeActivity` 上不加修改运行的 androidx FragmentManager 替代 |
| `DexLambdaScanner`（约 600 LOC） | 已删除（C1） | 一旦 Fragment 生命周期由 FragmentManager 驱动，Lazy<T> 初始化器自然运行 |
| Per-app `MCD_*` 常量 | 已删除（CR14、CR16） | `WestlakeLauncher` 精简：22,983 → 12,403 LOC（-46%） |
| `WestlakeLauncher` 精简 | 持续中 | CR14 + CR16 之后 12,403 LOC（相对原始 -46%） |

### 4.3 集成里程碑

| 里程碑 | 状态 | 备注 |
|---|---|---|
| M7-Step1 — noice 发现 harness 编排 | PASS | 反射式 `onCreate.invoke` 到达 MainActivity，但绕过 `Activity.performCreate` |
| M7-Step2 — noice 生产启动路径 | 突破 | 生产 `Instrumentation.callActivityOnCreate` 到达 `MainActivity.onCreate(Bundle)` 用户代码体（Hilt lazy 委托）。通过 `WAT.setForceLifecycleEnabled` 静态标志做到跨所有应用通用 |
| M8-Step1 — McD 发现 harness | PASS | 形状同 M7-Step1 |
| M8-Step2 — McD 生产启动路径 | 突破 | 同样的通用模式；为 `com.mcdonalds.app` 到达 `SplashActivity.onCreate` 用户体。模式是 **1:1** 从 M7-Step2 移植，只改 manifest 路径 + 日志标记前缀 |
| CR56 — `WestlakeContextImpl.setAttachedApplication` 接线 | 落地 | 必要但不充分——Hilt 走的是 Application 的 mBase 链，而非 Activity 的 |
| CR59 — 修复 `Application.mBase = null` | 落地 + 设备上 PASS（2026-05-14） | 两部分修复：(A) WAT.attachApplicationBaseContext 改用 shim Application 的包私有 `attach(Context)` 帮手，替代跨包受保护的 `attachBaseContext`；(B) WAT 丢弃 MiniServer 自动初始化的裸 Application 占位符，从而触发 `makeApplicationForLaunch` 来实例化真实的用户 Application 类。两个应用现在都干净完成 `MainActivity.onCreate`（零 NPE 帧）。回归 14/14 PASS |

### 4.4 哪些留下、哪些被删

**保留（不变）：** M1-M4 binder substrate；M3++ 同进程跳过；M4-PRE4 MessageQueue JNI；M4-PRE5 WestlakePackageManagerStub；M4-PRE10 CharsetPrimer；M4-PRE11 ColdBootstrap（部分）；CR1 Tier-1 服务正确性；CR2 ServiceMethodMissing.fail 模式；CR3/CR4/CR5 getSystemService binder 路由；CR22 冻结的 WestlakeContextImpl 表面；转向前的 Westlake 类（`WestlakeLayoutInflater`、`WestlakeInflater`、`WestlakeTheme`、`WestlakeVector`、`WestlakeNavGraph`、`WestlakeNode`、`WestlakeLayout`、`WestlakeView`）。

**移除：** M4-PRE12/13/14 反射字段植入（约 190 LOC）；`WestlakeResources.buildReflective` / `createSyntheticAssetManager` / 合成 ResourcesImpl 路径（约 200 LOC）；`WestlakeFragmentLifecycle`（3,087 LOC）；`DexLambdaScanner`（约 600 LOC）；`FragmentTransactionImpl`/`FragmentManager` 中的 per-app `MCD_*` 常量（CR14、CR16）；CR23-fix 的 `Activity.attach` try/catch（V2 后不可达）。

**净删减：** 5,000+ LOC 的植入 / 旁路 / per-app 代码。引擎代码库净缩小。

---

## 5. 当前状态（2026-05-14）

### 5.1 Phase 1 — 绿

- 14/14 回归套件 PASS（`scripts/binder-pivot-regression.sh --full`）：HelloBinder.dex、AsInterfaceTest.dex、六个服务测试、SystemServiceRouteTest.dex、sm_smoke（musl + bionic）、binder_smoke。
- noice M7-Step2 到达 `MainActivity.onCreate` 用户体。今天关闭的 CR59 修了 Hilt 用户体内 NPE；两个应用现在都干净完成 `onCreate`。
- McD M8-Step2 到达 `SplashActivity.onCreate` 用户体。SIG2 从 `FAIL → PASS`。CR59 同时解锁了 McD 的并行路径。
- M5 音频守护进程在 OnePlus 6 扬声器上发出可闻的 440 Hz 测试音。
- M6 surface 守护进程交付 60 Hz vsync，memfd 背书的 GraphicBuffer；Android 15 wire 兼容已验证。

### 5.2 仍未关闭的下游阻塞

CR59 之后，剩余的失败接受信号都是 **生命周期推进缺口，不是用户体内 NPE：**
- **noice S2（HomeFragment）** —— 启动器只驱动 `performLaunchActivity`（CREATED），不推进到 START/RESUME，因此 Fragment 生命周期不会触发。通过现有 `--resume` 标志加上 `launchAndResumeActivity` 是 M7-Step3 / 下一 CR 范围。
- **McD SIG1（`McDMarketApplication.onCreate`）** —— `WAT.currentApplication` 报告 null，因为 `makeApplication` 以 `forcedAppClassName=null` 运行，构建了裸 Application 而非 `McDMarketApplication`。启动器应在 `performLaunchActivity` 之前调用 `WAT.forceMakeApplicationForNextLaunch(appCls)`。M8-Step3 范围。
- **多 Activity Intent 分派**（V2 § 8.4）—— McD 的 SplashActivity → DashboardActivity 跳转要求 M4a 的 `startActivity` 调用通用的 `WAT.launchActivityByComponent(component)`。新代码路径，HIGH 风险评级。
- **`AppCompatDelegate` 兼容表面**（V2 § 8.3）—— 下一次最可能产生漂移诱惑之处；通过仔细暴露 AppCompatDelegate 实际调用的方法来提前防范。
- **Theme 父链**（V2 § 8.6）—— 今天 WestlakeTheme.obtainStyledAttributes 做的是简化属性查找；AppCompat 主题有多层父链；正确的 V2 必须走完整条链。

### 5.3 M7-Step2 / M8-Step2 在架构上证明了什么

M7-Step2 设计 **跨所有应用通用**。shim 的唯一修改是一个进程级别的可选标志（`WAT.setForceLifecycleEnabled`），以及两个读位置——两处都是已经存在的、AOSP 默认形状的检查。M8-Step2 的 `McdProductionLauncher` 是 `NoiceProductionLauncher` 的近乎逐字副本，只有三处琐碎差异（manifest 路径、println 目标类、日志标记前缀）。逐字节相同的结构代码就是为两个应用同时偿付的架构红利——没有 McD 特定逻辑，没有 noice 特定逻辑。**未来的应用只需要同样三处琐碎差异加上一份同 schema 的 manifest 文件。**

---

## 6. Phase 2 — OHOS

`CR41_PHASE2_OHOS_ROADMAP.md` 是规范化的预先规划文档；`CR60_BITNESS_PIVOT_DECISION.md` 和 `CR61_BINDER_STRATEGY_POST_CR60.md` 是最新的策略决策。

### 6.1 Phase 1 → Phase 2 二进制一致的部分

- 所有 Java 代码：framework.jar、ext.jar、core-*.jar、**aosp-shim.dex（V2 substrate）**、**7 个 M4 Java 服务类** —— Java 启动产物的每一个字节都由 dalvikvm 加载。
- C++ 源码：libbinder、servicemanager、AudioServiceImpl、AudioTrackImpl、BufferQueueCore、GraphicBufferProducerImpl、SurfaceComposerImpl、GraphicBuffer-memfd、BitTube、VsyncThread、LayerState。**相同 .cpp 文件，不同 sysroot，不同输出 triple。**
- Binder wire 协议 —— OHOS 内核 5.10 逐字带 `drivers/android/binder.c`；检视过的每个标准系统板 defconfig 都设置 `CONFIG_ANDROID_BINDER_IPC=y`。
- memfd 背书的共享内存 —— OHOS 标准内核上 `CONFIG_MEMFD_CREATE=y`；`CONFIG_ASHMEM=y` 也在。
- AIDL 事务码 —— 由我们发布的 framework.jar 版本驱动，Phase 1 → Phase 2 完全相同。

### 6.2 需要交换的部分

1. **内核配置：** 验证 binder 开启（很可能已是；不是则一行补丁）。→ M9
2. **构建 ABI：** 把所有 native 产物从 `--target=aarch64-linux-android24` 重编为 `--target=aarch64-linux-ohos`（或按 CR60 改为 `arm-linux-ohos`）。→ M10
3. **音频后端 `.cpp`：** 激活已搭好脚手架的 `OhosBackend.cpp`（M5 计划 § 4.3）—— ~250-350 LOC 的 `OH_AudioRenderer` 适配。→ M11
4. **Surface 后端 `.cpp`：** 激活已搭好脚手架的 `XComponentBackend.cpp`（M6 计划 § 4.3）—— ~300-400 LOC 的 `OH_NativeWindow` 适配。→ M12
5. **宿主 APK → 宿主 HAP：** 把编排包装层（几百 LOC Kotlin/Compose）重写为 ArkUI ETS HAP。→ M13

**Phase 1 源码大约 80% 不加修改地重用**，剩余 20% 是预先用 `#ifdef` 选项接口预约好的平台桥代码。

### 6.3 32 位转向（CR60，2026-05-14）

DAYU200 上 OHOS 用户空间 **只有 32 位** —— 内核跑 aarch64，但每个系统二进制都是 32 位 ARM EABI5，连 `/lib/ld-musl-arm.so.1`。当前的 64 位 dalvikvm 不能 `dlopen` 任何 OHOS native 库（XComponent、AudioRenderer、network），因为它们都是 32 位，因此它必须通过跨架构 IPC 桥（M6 守护进程 + AF_UNIX + memfd）和它们说话。

**决策（CR60）：** 在 DAYU200 上把 dalvikvm 切到 32 位 ARM，因为位宽匹配可以从生产路径中消除守护进程。在未来 64 位 OHOS ROM 出现时反向切回 64 位约 2-4 天复验，不是重写。整个 spike：硬上限 3-5 个人日。

**前进的纪律规则**（保持转向可逆）：
- 所有 native 代码使用 `intptr_t` / `uintptr_t` / `size_t` 表达指针。禁止 `(int)pointer`、`(long)pointer`。
- CI 中两种架构都要构建（开销低，回归立刻被发现）。
- 除非绝对必要，shim Java 或 JNI 桥中不出现 `#ifdef __aarch64__` 或 `__arm__` 分支。
- `scripts/run-ohos-test.sh` 通过 `hdc shell getconf LONG_BIT` 自动检测板卡位宽。

### 6.4 CR60 之后的 binder 策略（CR61，2026-05-14）

**我们为 32 位 OHOS 提供我们自己的 AOSP 衍生 `libbinder.so` + `servicemanager`，通过 `/dev/vndbinder` 与内核 binder 驱动对话。我们不与 OHOS 的 `libipc.dylib.so` / `samgr` 集成。**

DAYU200 板的经验状态：
- 内核 binder 驱动健在 —— 三个节点（`/dev/binder`、`/dev/hwbinder`、`/dev/vndbinder`）全部存在且 DAC 为 world-rw。M9 的内核侧由 OHOS 自己完成。
- OHOS 不出货 `libbinder.so` —— AOSP 用户态 IPC ABI 缺席。链接 AOSP `IBinder`/`Parcel`/`Stub.asInterface` 的应用没有宿主库可调。
- OHOS 出货 `libipc.dylib.so` + `samgr` —— 一个 wire 不兼容的体系，`IRemoteObject` 语义不同、parcel 格式不同、服务注册模型不同。
- `getenforce` → `Enforcing`。门禁是 SELinux MAC，而不是 Linux DAC 权限。

**自带 libbinder 的理由：** Westlake 承载的 Android APK（noice、McD）通过 framework.jar / aosp-shim.dex 调用 AOSP binder API。把每一条框架 binder 事务再针对 OHOS `IRemoteObject` 重实现一遍是一个巨大的"全替换"项目——本质上正是最初宏方案决策要避免的问题。Westlake 的 libbinder 已在 Android 手机上通过 14/14 回归；替换一个工作中的组件没有理由。OHOS 的 `libipc` 不会与 `framework.jar` 的服务 stub 互操作，除非我们再写一堆每服务适配器——这正是宏 shim 契约禁止的 per-app 分支失败模式。

**共存模型：**
```
DAYU200 进程树
├── samgr                          ← OHOS，位于 /dev/samgr_inner
│   └── 提供：display_ability、audio_ability、…
├── westlake-servicemanager        ← 我们的，位于 /dev/vndbinder
│   └── 提供：activity、window、package、display、notification、input_method、power
└── dalvikvm（32 位）
    └── 通过我们的 libbinder.so 与 westlake-servicemanager 对话
    └── 另外：dlopen libnative_window.so、libaudio_renderer.z.so 做进程内 OHOS API 调用
```

两个 service-manager 宇宙互不相见。AOSP 框架代码要 `ActivityManagerService` 时，经我们的 libbinder 经 `/dev/vndbinder` 到达我们的实现。OHOS 代码要 `display_ability` 时，经 libipc 到达 OHOS 的实现。

**M9 / M10 / M11 / M12 重定位（按 CR61 § 5）：**

| 里程碑 | 原计划 | CR60 之后 |
|---|---|---|
| M9 | 移植 `binder.ko` | **OHOS 已经完成。** 3 个节点都在，内核驱动健在。仅做验证。 |
| M10 | 为 `aarch64-linux-ohos-musl` 交叉编译 | **为 `arm-linux-ohos-musl`（32 位）交叉编译。** 同一份 Makefile，新的 `--target` triple。 |
| M11 | 守护进程进程外切换后端 | 从 32 位 dalvikvm **进程内** `System.loadLibrary("libaudio_renderer.z.so")`。守护进程从生产路径消失；M5 守护进程作为回退保留。 |
| M12 | 守护进程进程外切换后端 | **进程内** `System.loadLibrary("libnative_window.so")` + XComponent NAPI。M6 守护进程作为回退保留。今日 smoke 测试已 PASS。 |
| M13 | noice 上 OHOS 手机 | 精神不变；范围调整到新架构。 |

**明确禁止 `setenforce 0`** 作为"修复"——它会为整个板卡关闭 MAC，对生产可行性给出虚假信号。如需 SELinux 策略工作，按文档化的单 domain 规则升级。

### 6.5 Phase 2 工作量与时程

| 里程碑 | 最佳 | 预期 | 最差 |
|---|---|---|---|
| M9 binder 内核验证 +（配置补丁） | 0.5 | 1.0 | 2.0 |
| M10 libbinder/servicemanager musl 重编 | 1.0 | 1.5 | 2.0 |
| M11 音频守护进程 → OHOS AudioRenderer | 2.5 | 2.75 | 3.0 |
| M12 surface 守护进程 → OHOS XComponent | 4.0 | 4.25 | 4.5 |
| M13 noice 在 OHOS 端到端 | 3.5 | 4.0 | 4.5 |
| 小计 | 11.5 | 13.5 | 16.0 |
| 风险储备（10-15%） | 1.2 | 1.5 | 2.0 |
| **Phase 2 总人日** | **约 13** | **约 15** | **约 18** |

2 个工程师并行：预期约 7 个日历日。1 个工程师：约 13-15 个日历日。最大时程风险是 **硬件采购**（rk3568 或 DAYU200 开发板）——工程侧是有界且可处理的。

---

## 7. 性能与权衡

| 维度 | 转向前（Option 2 不完整） | binder 转向（Option 3 精简） | Anbox（Option 3 完整） |
|---|---|---|---|
| 常驻内存 | ~50 MB | ~150 MB 活动 | ~700 MB-1 GB |
| 冷启动 | <1 秒 | ~2-3 秒 | ~20-30 秒 |
| 单应用启动 | ~10 秒（33 DEX） | ~3 秒（热 VM + 按需守护进程） | ~5 秒（zygote fork） |
| 支持的 AOSP native 库 | ~0（通过 JNI shim 替换） | ~全部（通过真实 binder） | ~全部 |
| WebView 支持 | 无 | 有（Chromium 子进程经真实 binder） | 有 |
| 多进程应用支持 | 无 | 有 | 有 |
| 需要内核改动 | 无 | `binder.ko`（Phase 2；OHOS 已启用） | `binder.ko` + namespace + cgroup + selinux |
| Per-app shim 工作 | 与应用数线性增长 | 趋近 0（服务收敛） | 0 |
| 与真实 Android 的架构距离 | 远 | 近 | 一致 |

**服务调用延迟：** 进程内 Java 到 Java 经 `Stub.asInterface` 跳过 ≈ 50 ns（一次虚调用）。Java 到守护进程经真实 Binder ≈ 6 µs 来回。对服务调用可接受（典型应用 < 1000/s）。热路径（SurfaceFlinger 帧提交）使用共享内存缓冲传输（memfd `GraphicBuffer`、BufferQueue）—— 与 AOSP 一致。

**帧预算：** 60 Hz 即 16.6 ms。SurfaceFlinger 合成工作在守护进程中完成；帧提交是一次 Binder 事务传递一个 `GraphicBuffer` 句柄（内核 dup fd，数据面零拷贝）。

**内存压力：** 按需启动守护进程让空闲足迹保持在约 75 MB（dalvikvm + servicemanager + Java 服务）。音频 / surface 守护进程在首次使用时启动。相机 / 媒体守护进程仅在需要的应用上启动。

---

## 8. 反模式（已编码）

由每个 agent 和每个 CR 强制执行（agent 记忆中的 `feedback_no_per_app_hacks.md`、`feedback_subtraction_not_addition.md`、`feedback_macro_shim_contract.md`）：

### 8.1 Per-app 硬编码捷径

不允许 `if (className.equals("com.example.MyApp"))`，不允许 `if (resourceId == 0x7f0b171c)`。只允许通用的 Android API shim。已删除的 `MCD_*` 常量（CR14/CR16）是典型的违例例子。

### 8.2 叠加式 shimming

不要观察 NPE → 加 shim → 继续。NPE 上的正确反射是："缺了哪一层？真正的 Android 在这里提供什么？这归到我们架构的哪里？"如果答案是"归到 Binder 后面的某个服务"，就加服务方法（并通过 Phase 1 沙箱验证）；不要类级别 shim。

### 8.3 渲染期旁路

渲染器只渲染。它不跑生命周期、不植入字段、不加载 fragment。已删除的 `WestlakeFragmentLifecycle.runLifecycleAndConvert` 是典型反例。

### 8.4 反射当答案

避免对框架对象做 `Unsafe.allocateInstance`，避免 `Method.setAccessible(true)`，避免对框架内部 `Field.get/set`。这些都是在绕过我们本应正确参与的框架路径的信号。（对应用提供的扩展点反射—— 如 manifest 声明的 activity 类——是可以的。）

### 8.5 推测性完备

不要实现我们没有观察到被调用的服务方法。通过减法发现（Phase 1 中 noice 跑时的日志会精确揭示哪些 Binder 事务击中各服务）。只实现观察到的。

### 8.6 不对框架冷启动对象做反射字段植入（V2 新增）

如果发现自己在写
```java
Field f = SomeFrameworkClass.class.getDeclaredField("mSomething");
f.setAccessible(true);
f.set(instance, reflectivelyConstructed());
```
针对一个刚 `Unsafe.allocateInstance` 出来的框架类实例，**停下**。你又在做 M4-PRE12/13/14。改为通过 `framework_duplicates.txt` 把该类整个 shadow 掉。

### 8.7 不允许 `try { realFrameworkCold(...); } catch (...) { fallback() }` 模式

如果真实框架代码在冷启动时抛错，正确的修复是不调用它。包在 try/catch 里会掩盖架构问题，并堆积死分支。CR23-fix 的 `try { activity.attach(6 args) } catch (NoSuchMethodError | LinkageError)` 是典型例子——当下必要，在 V2 Step 6 中删除。

### 8.8 不要"差不多框架"的类

`WestlakeContextImpl extends Context` 是对的（实现一个抽象表面）。通过 classpath shadow 的 `WestlakeActivity extends Activity` 是对的（系统级替换一个具体类）。但：
- 不要写 `class WestlakeActivityHelper { static void prepare(android.app.Activity a) { ... } }`，在框架 Activity 旁边运行并部分替代它的状态。这就是被删的 WestlakeFragmentLifecycle 模式换了件外衣。

### 8.9 不允许通过直接的框架反射绕过 V2 substrate

如果 V2 出货 `WestlakeActivity`，那么 `WestlakeLauncher.mainImpl` **绝不能** 反射式地构造框架的 `android.app.Activity` 并操作它。V2 的全部意义在于运行期只存在一个 `Activity` 类——我们的。

---

## 9. 开放的架构问题

### 9.1 binder.ko 适用于 LiteOS-A 目标吗？

Binder 是 Linux 内核模块。**OHOS LiteOS-A 不是 Linux** —— 它是为小型 / 嵌入式设备设计的另一种内核。binder.ko 不适用。如果 LiteOS-A 目标进入范围，那是另一种架构（很可能是"转译到原生 ArkTS"——它自己有完备性问题）。**建议：** 把这次转向限定在标准系统 OHOS（基于 Linux）目标。

### 9.2 BufferQueue ABI 兼容

AOSP 的 BufferQueue 协议（客户端应用与 SurfaceFlinger 之间）偶尔在 Android 版本之间变化。Westlake 的 surface 守护进程必须遵守所发布 AOSP 框架版本的同一 ABI。锁定一个框架版本最简单；支持多版本需要版本相关的事务表。

### 9.3 权限模型

真实 Binder 传递调用者的 UID/PID。许多框架权限检查调 `Binder.getCallingUid()`。我们的设定里调用者总是我们自己的 UID，因此检查平凡通过——但如果应用检查"这个调用者是我的 UID 还是 SYSTEM_UID？"，答案就重要了。**解决：** 启动时把服务侧调用的 caller UID 配为 `SYSTEM_UID`（1000），app 侧调用配为 app UID。通过 `IPCThreadState` 串起来。

### 9.4 ashmem / memfd / 共享内存区域

AOSP 服务通过共享内存区域传输大数据（音频缓冲、surface 帧、资源块），而不是 Parcel marshaling。Westlake 以 **memfd** 为目标；若某些厂商库需要，再用一层薄薄的 ashmem-shim 把 ashmem API 映射到 memfd。OHOS 内核同时带 `CONFIG_MEMFD_CREATE=y` 和 `CONFIG_ASHMEM=y`。

### 9.5 ServiceManager 句柄 0 设置

内核 binder 驱动把 `binder context 0` 分配给第一个用特定 ioctl 打开 `/dev/binder` 的进程。启动编排者硬性按顺序先启 servicemanager，待其就绪再启动其他任何东西。

### 9.6 多 Activity Intent 分派

应用的 `Activity.startActivity(Intent)` 最终调用 `ActivityTaskManager.getService().startActivity(...)`。V2 里 `WestlakeActivity.startActivity(Intent)` 调用 `IActivityManager.startActivity(...)`（M4a 的 Stub）。M4a 的实现：将 intent 入队、标记为活跃、通过本地 PackageManager 解析目标 Activity 类、然后触发 `WAT.launchActivityByComponent(component)`。**HIGH 风险评级** —— 新代码路径；将在 M7-Step3 / M8-Step3 工作期暴露。

### 9.7 AppCompatDelegate 兼容表面

`AppCompatDelegate.getDelegate(this)` 读 `activity.getWindow()` 来安装自己的 DecorView。Westlake 的 `Window` 是个 stub。AppCompatDelegate 的 `installViewFactory(LayoutInflater)` 调 `getLayoutInflater().setFactory2(this)`。AppCompatDelegate 有 ~15K LOC 的兼容 shim；某些路径读框架 Activity 字段。**MEDIUM 风险评级** —— 最可能产生下一次漂移诱惑之处。

### 9.8 隐藏 API 访问（`@hide` / `@SystemApi`）

某些应用 + 库代码会触及 `@hide` 框架 API。`WestlakeActivity` / `WestlakeApplication` 应在野外观察到时再暴露 `@hide` 方法——通过运行发现，而不是预先实现。CR2 `ServiceMethodMissing.fail` 模式处理此类情况。

### 9.9 WebView

Chromium 的 WebView 本质上是多进程——渲染子进程（沙箱化）、GPU 子进程、网络子进程，它们之间通过真实 binder IPC。这是 Phase 3 工作；binder 转向是先决条件。没有真实 Binder，WebView 不可达。

---

## 10. 决策日志

| 日期 | 决策 | 理由 | 可逆性 |
|---|---|---|---|
| 2026-05-12 | 采用 Binder 替换边界 | 用户确认；codex 二审确认；叠加式 shimming 模式已被证明不收敛 | 可逆——当前 shim 路径仍在 tree；转向起初是叠加式的 |
| 2026-05-12 | 先在 Android 手机上验证 | 没有架构验证就直接 OHOS 内核依赖太冒险 | 不适用 |
| 2026-05-12 | 接受 OHOS 内核 binder 依赖 | 用户：「优先让所有 APK 不加修改运行，胜过修改 OHOS 内核」 | Phase 2 出货前需要 OHOS 团队决定 |
| 2026-05-12 | 范围限定到标准系统 OHOS（Linux）；推迟 LiteOS-A | binder.ko 仅限 Linux | 若 LiteOS-A 成为优先项可逆——需要不同架构 |
| 2026-05-13 | V2 在进程内 Java 边界取代 V1；V1 binder substrate 保留 | 用户指出漂移；M4-PRE12/13/14 证明方案 (a) 无界；CR14 前的 per-app shim 证明方案 (b) 无界 | 任何步骤可逆 |
| 2026-05-13 | 在 `Activity.attach` / `Application.attach` / `Window.<init>` 缝合处替换 | 这些是带无界字段级联的冷启动边界；下面大多是无状态框架代码，在进程内能工作 | 若发现真实 Activity/Application 字段访问的成本比预计小，可重新评估 |
| 2026-05-13 | 通用 classpath shadow 替换，不做 per-app | per-app 正是 C1/CR14/CR16 删除的；CR23-fix 已为 Context 使用同样机制 | 移除 shadow 条目即可逆 |
| 2026-05-13 | Resources 表面由 Westlake 拥有；直接解析 resources.arsc | 框架的 ResourcesImpl/AssetManager 冷启动级联吞没了 M4-PRE12-14；arsc 解析器是有限的（约 400 LOC） | 可逆 |
| 2026-05-13 | 真实 androidx Fragment 生命周期不加修改地在 WestlakeActivity 上运行 | androidx 在应用 classpath；不经 ActivityThread；只依赖 Activity 公共 API | 由 V2 step 8 noice-discover 跑验证 |
| 2026-05-14 | CR60：在 DAYU200 上把 dalvikvm 切到 32 位 ARM | 板卡用户空间只有 32 位；位宽匹配可从生产路径消除守护进程 | 若 64 位 OHOS ROM 出货则 2-4 天反向切换 |
| 2026-05-14 | CR61：提供我们自己的 libbinder + servicemanager；不与 OHOS libipc/samgr 集成 | OHOS libipc 是 wire 不兼容的；集成需要每服务适配器（被宏 shim 契约禁止）。Westlake libbinder 已通过 14/14 回归 | 仅在接受"全替换"项目的前提下可逆 |

---

## 11. 术语表

- **Binder**：Android 主要 IPC 机制。一个内核模块，提供进程间单拷贝的同步与异步 RPC，附带对象引用、死亡通知和 UID 传递。
- **binderfs**：Linux 特性（≥5.0），允许用户态创建独立 binder context。
- **`/dev/binder`、`/dev/vndbinder`、`/dev/hwbinder`**：binder 模块暴露给系统 / 厂商 / HIDL context 的字符设备。
- **`servicemanager`**：内核交付"context 0"的 AOSP 守护进程。所有命名服务的通讯录。
- **`Stub.asInterface`**：AIDL 生成的方法，要么返回本地 Java 实现（同进程），要么返回 marshaling 代理（跨进程）。对进程内服务情形是关键优化。
- **`localBinder()`**：AOSP libbinder 在 native 侧的对应物；同一目的。
- **`IPCThreadState`**：AOSP libbinder 每线程状态，持有当前事务的 caller 信息。
- **gralloc**：Android 的图形缓冲区分配器。硬件 HAL；Westlake 通过让 surface 守护进程经 OHOS 等价机制（或便携测试用的 memfd）分配缓冲来绕开它。
- **AAudio / OpenSL ES**：Android 音频 API；AAudio 是现代低延迟的那个。
- **XComponent / OH_Drawing / OH AudioRenderer**：OHOS native API；Westlake 守护进程内部桥代码的目标。
- **classpath shadow**：在 `aosp-shim.dex` 中以与 `framework.jar` 中某个类相同的全限定名声明类，并安排 classpath 顺序使我们的类赢得解析。框架副本不可达。
- **冷启动字段**：框架类上某个由 `system_server` 驱动的引导（`ActivityThread.handleBindApplication` / `handleLaunchActivity`）设置的字段，而非该类的构造器。例：`Activity.mApplication`、`ResourcesImpl.mDisplayAdjustments`、`Configuration.mLocaleList`。
- **植入（plant）**：在一个刚 `Unsafe.allocateInstance` 出来的框架对象实例上反射式地设置冷启动字段。M4-PRE12-14 模式。**V2 禁止。**
- **substrate**：从下方满足框架公共 / 受保护 API 表面的层。Binder substrate（libbinder + servicemanager + 服务）满足 `Stub.asInterface` 的预期。Java substrate（WestlakeActivity + WestlakeApplication + WestlakeResources）满足应用 `super.foo()` 的预期。
- **替换边界**：Westlake 自有代码替换 AOSP 代码的分界线。V1 把它放在 Binder（对跨进程正确）。V2 把它 **同时** 放在 dalvikvm 内的 `Activity.attach` / `Application.attach` / `Resources` API 表面（对进程内修正）。
- **drift（漂移）**：M4-PRE12-14 风格的逐个冷启动字段推进的模式，永不收敛。V2 中识别并命名。
- **宏 shim（macro shim）**：在我们自有的类（classpath shadow 的框架类 + Westlake 自有服务 + Westlake 自有 binder 服务）上实现公共 / 受保护 API 方法。每个方法体是 AOSP 默认逐字版、安全原语、或委托。是 V2 下 **唯一允许** 的 shim 形式。
- **微 shim（micro shim）**：任何 Unsafe.allocateInstance / setAccessible / framework-private-field 访问 / per-app 分支 / catch-NoSuchMethodError-然后-fallback 模式。**禁止。**

---

## 12. 交叉引用

**权威配套文档（前瞻性）：**
- `BINDER_PIVOT_MILESTONES.md` — 带接受标准的 M1-M17 规范化工作分解
- `MIGRATION_FROM_V1.md` — 具体的逐文件 V1 → V2 迁移图
- `CR41_PHASE2_OHOS_ROADMAP.md` — Phase 2 OHOS 预先规划（M9-M13）
- `CR60_BITNESS_PIVOT_DECISION.md` — 32 位 ARM dalvikvm 转向
- `CR61_BINDER_STRATEGY_POST_CR60.md` — CR60 之后的 binder 策略
- `OHOS_MVP_WORKSTREAMS.md` — Phase 2 实施工作流
- `PHASE_1_STATUS.md` — Phase 1 substrate 的运行状态

**里程碑报告（近期状态）：**
- `M5_AUDIO_DAEMON_PLAN.md` + `M5_STEP1..5_REPORT.md` — 音频守护进程设计 + 落地
- `M6_SURFACE_DAEMON_PLAN.md` + `M6_STEP1..6_REPORT.md` — surface 守护进程设计 + 落地
- `M7_STEP1_REPORT.md` + `M7_STEP2_REPORT.md` — noice 发现与生产路径
- `M8_STEP1_REPORT.md` + `M8_STEP2_REPORT.md` — McD 发现与生产路径
- `CR56_REPORT.md` — `WestlakeContextImpl.setAttachedApplication` 接线
- `CR59_REPORT.md` — Application.mBase=null 修复（2026-05-14 关闭的突破性 CR）

**历史（已被取代；保留以便溯源）：**
- `BINDER_PIVOT_DESIGN.md`（V1，2026-05-12）—— 被本文档取代
- `BINDER_PIVOT_DESIGN_V2.md`（V2，2026-05-13）—— 被本文档取代

**Agent 记忆（每个 Builder 简报必须包含）：**
- `feedback_macro_shim_contract.md` — 宏 / 微边界；自审门禁
- `feedback_no_per_app_hacks.md` — 不允许 per-app 分支
- `feedback_subtraction_not_addition.md` — 减法式验证
- `feedback_bitness_as_parameter.md` — 位宽纪律（CR60 之后）

---

**合并架构文档完。**
