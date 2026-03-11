# 代码审查报告：包管理与应用安装子系统

## Android 11 (API Level 30) AOSP 源码审查

**审查日期：** 2026-03-10
**子系统：** `frameworks/base/core/java/android/content/pm/`
**范围：** 包查询、组件信息、Intent 解析、APK 安装流程、功能标志、包可见性、拆分 APK、应用包

---

## 1. 执行摘要

`android.content.pm` 包构成了 Android 包管理系统的骨干。仅 `PackageManager.java` 就超过 8,200 行，`PackageInstaller.java` 超过 2,700 行，提供了查询已安装包、解析 Intent、管理组件以及执行安装/卸载操作的 API。Android 11 引入了重大行为变更，最值得注意的是**包可见性**限制（`FILTER_APPLICATION_QUERY`）以及相机和麦克风的新前台服务类型要求。代码展示了跨多个 API 级别的谨慎演进，在保持向后兼容性的同时分层添加新的安全限制。然而，由此产生的 API 表面非常庞大，承载着相当大的遗留复杂性。

---

## 2. 架构概览

### 2.1 类层次结构

```
PackageItemInfo
  +-- ComponentInfo
  |     +-- ActivityInfo      (Activity 和广播接收器)
  |     +-- ServiceInfo       (服务)
  |     +-- ProviderInfo      (内容提供者)
  +-- ApplicationInfo        (应用级元数据)
  +-- PermissionInfo
  +-- InstrumentationInfo

PackageInfo                   (所有清单数据的顶级容器)
ResolveInfo                   (Intent 解析结果)

PackageManager               (所有查询的抽象门面)
PackageInstaller             (基于会话的安装 API)
  +-- Session                (活跃的写入会话)
  +-- SessionParams          (会话配置)
  +-- SessionInfo            (只读会话状态)
  +-- SessionCallback        (生命周期观察者)
```

### 2.2 审查的关键文件

| 文件 | 行数 | 用途 |
|------|------|------|
| `PackageManager.java` | ~8,248 | 所有包查询、权限、功能的抽象门面 |
| `PackageInstaller.java` | ~2,780 | 基于会话的安装/卸载 API |
| `PackageInfo.java` | ~400 | 已解析 AndroidManifest.xml 数据的容器 |
| `ApplicationInfo.java` | ~1,800+ | 应用级标志和元数据 |
| `ActivityInfo.java` | ~800+ | Activity/接收器配置 |
| `ServiceInfo.java` | ~200+ | 服务配置和前台类型 |
| `ProviderInfo.java` | ~200+ | ContentProvider 配置 |
| `ResolveInfo.java` | ~300+ | Intent 解析结果 |
| `parsing/ParsingPackageUtils.java` | ~4,000+ | 清单 XML 解析引擎 |

---

## 3. 包查询与信息检索

### 3.1 标志系统设计

`PackageManager` 使用一个复杂的整数标志系统，分为两个语义类别（第 137-145 行）：

- **`GET_` 标志**：请求可能为了效率而被省略的额外数据（如 `GET_ACTIVITIES`、`GET_PERMISSIONS`、`GET_META_DATA`、`GET_SIGNING_CERTIFICATES`）
- **`MATCH_` 标志**：包含否则会从结果中省略的组件或包（如 `MATCH_DISABLED_COMPONENTS`、`MATCH_UNINSTALLED_PACKAGES`、`MATCH_SYSTEM_ONLY`）

**观察：** 不同查询上下文存在多种标志注解类型 — `@PackageInfoFlags`、`@ApplicationInfoFlags`、`@ComponentInfoFlags`、`@ResolveInfoFlags` — 提供了编译时安全性，确定哪些标志适用于哪些场景。这个设计很好。

**关注 - 标志值冲突：**
```java
GET_SIGNATURES          = 0x00000040;  // 已弃用
GET_RESOLVED_FILTER     = 0x00000040;  // 相同的值
```
`GET_SIGNATURES` 和 `GET_RESOLVED_FILTER` 共享十六进制值 `0x00000040`。虽然它们用于不重叠的上下文（`PackageInfoFlags` vs. `ResolveInfoFlags`），但这是一个可维护性问题。`@IntDef` 注解作为文档使用，但无法防止开发者在期望一个的地方传入另一个。

### 3.2 核心查询方法

`PackageManager` 上的主要查询接口：

| 方法 | 返回值 | 范围 |
|------|--------|------|
| `getPackageInfo(String, int)` | `PackageInfo` | 按名称查询单个包 |
| `getPackageInfo(VersionedPackage, int)` | `PackageInfo` | 按名称+版本查询包 |
| `getInstalledPackages(int)` | `List<PackageInfo>` | 当前用户所有已安装包 |
| `getInstalledApplications(int)` | `List<ApplicationInfo>` | 当前用户所有应用信息 |
| `resolveActivity(Intent, int)` | `ResolveInfo` | 最佳匹配的 Activity |
| `queryIntentActivities(Intent, int)` | `List<ResolveInfo>` | 所有匹配的 Activity |
| `queryBroadcastReceivers(Intent, int)` | `List<ResolveInfo>` | 所有匹配的接收器 |
| `queryIntentServices(Intent, int)` | `List<ResolveInfo>` | 所有匹配的服务 |
| `resolveContentProvider(String, int)` | `ProviderInfo` | 按 authority 查询提供者 |

**对应用开发者的建议：** 始终传递所需的最小标志集。不必要地请求 `GET_ACTIVITIES | GET_SERVICES | GET_PROVIDERS | GET_RECEIVERS | GET_PERMISSIONS | GET_META_DATA` 会在 Binder 上创建大型 Parcelable 事务，在组件较多的构建中可能导致 `TransactionTooLargeException`。

### 3.3 已弃用的签名 API

```java
@Deprecated
public static final int GET_SIGNATURES = 0x00000040;
```
字段 `PackageInfo.signatures` 已弃用，取而代之的是 `PackageInfo.signingInfo`（通过 `GET_SIGNING_CERTIFICATES = 0x08000000` 填充）。新 API 支持签名证书轮换。开发者应使用 `hasSigningCertificate()` 进行验证，而不是直接进行证书比较，因为旧 API 不能正确处理轮换。

---

## 4. 包可见性（Android 11 特性）

### 4.1 核心变更

Android 11 引入了一个基本行为变更，由以下控制：

```java
@ChangeId
@EnabledAfter(targetSdkVersion = Build.VERSION_CODES.Q)
public static final long FILTER_APPLICATION_QUERY = 135549675L;
```

**文件：** `PackageManager.java`，第 3716 行

目标为 Android R (API 30) 及以上的应用从所有 PackageManager 查询方法接收**过滤后的结果**。第 96-99 行的类 Javadoc 警告：

> "如果您的应用目标为 Android 11（API 级别 30）或更高版本，此类中的方法都返回过滤后的应用列表。"

### 4.2 可见性机制

应用可以通过清单中的 `<queries>` 标签声明可见性需求（在第 3710 行引用）。没有明确声明的情况下，应用只能看到：
- 自己的包
- 当前与其交互的包
- 系统包

`MATCH_UNINSTALLED_PACKAGES` 标志现在还需要 `android.permission.QUERY_ALL_PACKAGES` 才能查看已卸载的包（第 394-395 行）。

### 4.3 功能标志

```java
public static final String FEATURE_APP_ENUMERATION = "android.software.app_enumeration";
public static final boolean APP_ENUMERATION_ENABLED_BY_DEFAULT = true;
```

`FEATURE_APP_ENUMERATION`（第 3130 行）表示设备支持应用枚举过滤。默认启用（第 3133 行），使其成为 Android 11 上的通用行为变更。

### 4.4 强制可查询覆盖

在 `SessionParams` 中：
```java
public boolean forceQueryableOverride;
```
以及对应的方法：
```java
public void setForceQueryable() {
    this.forceQueryableOverride = true;
}
```
这个隐藏 API 允许系统组件使已安装的包对所有应用可见，绕过 `<queries>` 过滤。

### 4.5 对开发者的影响

- 枚举已安装包的应用（启动器、设备管理、辅助功能）必须在清单中声明 `<queries>` 或持有 `QUERY_ALL_PACKAGES`
- `getInstalledPackages()`、`getInstalledApplications()`、`queryIntentActivities()` 及相关方法都返回过滤后的结果
- **风险：** 未适配的应用可能静默接收不完整的结果，导致功能降级而没有可见错误

---

## 5. APK 安装流程

### 5.1 基于会话的架构

`PackageInstaller` 使用 Android L (API 21) 中引入的基于会话的模型，替代了遗留的 `installPackage()` API。流程：

1. **创建会话：** `PackageInstaller.createSession(SessionParams)` 返回唯一的会话 ID
2. **打开会话：** `PackageInstaller.openSession(sessionId)` 返回 `Session` 对象
3. **流式传输 APK：** `Session.openWrite(name, offset, length)` 返回 `OutputStream`
4. **同步：** `Session.fsync(out)` 确保数据持久化
5. **提交：** `Session.commit(IntentSender)` 封存会话并启动安装
6. **结果：** 通过 `IntentSender` 以 `EXTRA_STATUS` 传递状态

会话在重启后持久存在，系统在大约一天后自动销毁被放弃的会话。

### 5.2 会话模式

```java
public static final int MODE_FULL_INSTALL = 1;     // 替换所有 APK
public static final int MODE_INHERIT_EXISTING = 2;  // 添加/更新拆分 APK
```

`MODE_INHERIT_EXISTING` 对拆分 APK 交付至关重要，允许增量安装新的拆分而无需替换整个包。

### 5.3 安装状态码

公开状态码提供清晰的语义反馈：

| 状态 | 值 | 含义 |
|------|-----|------|
| `STATUS_PENDING_USER_ACTION` | -1 | 需要用户干预 |
| `STATUS_PENDING_STREAMING` | -2 | 流式数据待处理（隐藏） |
| `STATUS_SUCCESS` | 0 | 安装成功 |
| `STATUS_FAILURE` | 1 | 通用失败 |
| `STATUS_FAILURE_BLOCKED` | 2 | 被策略/验证器阻止 |
| `STATUS_FAILURE_ABORTED` | 3 | 用户拒绝或会话被放弃 |
| `STATUS_FAILURE_INVALID` | 4 | 格式错误/损坏/不匹配的 APK |
| `STATUS_FAILURE_CONFLICT` | 5 | 与现有包冲突 |
| `STATUS_FAILURE_STORAGE` | 6 | 存储空间不足 |
| `STATUS_FAILURE_INCOMPATIBLE` | 7 | 设备不兼容 |

**此外**，通过 `EXTRA_LEGACY_STATUS` 暴露了超过 30 个遗留的 `INSTALL_FAILED_*` 和 `INSTALL_PARSE_FAILED_*` 代码（值 -1 到 -115，以及 -100 到 -113）。这些是提供详细失败原因的 `@SystemApi` 隐藏代码。

### 5.4 安装标志（隐藏/系统 API）

关键安装标志（全部 `@hide`）：

| 标志 | 值 | 用途 |
|------|-----|------|
| `INSTALL_REPLACE_EXISTING` | 0x02 | 允许替换现有包 |
| `INSTALL_ALLOW_TEST` | 0x04 | 允许仅测试包 |
| `INSTALL_FROM_ADB` | 0x20 | ADB 发起的安装 |
| `INSTALL_ALL_USERS` | 0x40 | 为所有用户安装 |
| `INSTALL_GRANT_RUNTIME_PERMISSIONS` | 0x100 | 自动授予运行时权限 |
| `INSTALL_INSTANT_APP` | 0x800 | 安装为临时/免安装应用 |
| `INSTALL_DONT_KILL_APP` | 0x1000 | 添加拆分时不终止应用 |
| `INSTALL_APEX` | 0x20000 | APEX 包安装 |
| `INSTALL_ENABLE_ROLLBACK` | 0x40000 | 启用回滚支持 |
| `INSTALL_DISABLE_VERIFICATION` | 0x80000 | 跳过包验证 |
| `INSTALL_STAGED` | 0x200000 | 分阶段安装（下次重启时应用） |
| `INSTALL_DRY_RUN` | 0x800000 | 仅验证，不安装 |

**关注：** `INSTALL_DISABLE_VERIFICATION` (0x80000) 绕过包验证。虽然是隐藏的，但通过反射或合作协议访问此标志可能造成安全漏洞。

### 5.5 数据加载器 / 增量安装

Android 11 引入了三种用于流式安装的数据加载器类型：

```java
DATA_LOADER_TYPE_NONE = DataLoaderType.NONE;           // 传统方式
DATA_LOADER_TYPE_STREAMING = DataLoaderType.STREAMING;  // 网络流式传输
DATA_LOADER_TYPE_INCREMENTAL = DataLoaderType.INCREMENTAL; // 增量文件系统
```

增量文件系统支持（`FEATURE_INCREMENTAL_DELIVERY`）使应用在仍在下载时即可使用，受系统功能 `android.software.incremental_delivery` 控制。`Session.addFile()` 方法支持通过带元数据和签名参数的数据加载器添加文件，需要 `USE_INSTALLER_V2` 权限。

### 5.6 回滚支持

```java
@RollbackDataPolicy int RESTORE = 0;  // 备份数据，回滚时恢复
@RollbackDataPolicy int WIPE = 1;     // 不备份，回滚时清除
@RollbackDataPolicy int RETAIN = 2;   // 不备份，不恢复（TODO：未实现）
```

**发现：** `RETAIN` 策略有一个 TODO 注释表明它"尚未实现"（第 767-768 行）。这是回滚框架中的空缺，依赖此行为的开发者应注意。

### 5.7 流实现细节

`Session.openWrite()` 方法使用两种不同的 I/O 机制，由系统属性控制：

```java
public static final boolean ENABLE_REVOCABLE_FD =
        SystemProperties.getBoolean("fw.revocable_fd", false);
```

启用时使用 `ParcelFileDescriptor.AutoCloseOutputStream`；否则使用 `FileBridge.FileBridgeOutputStream`。`FileBridge` 方式提供服务端对文件描述符生命周期的控制，使系统能在会话封存时撤销访问。可撤销 FD 路径默认禁用，表明 FileBridge 方式是生产路径。

---

## 6. 拆分 APK 和多包支持

### 6.1 拆分 APK 模型

`PackageInfo.splitNames`（第 39 行）包含已安装拆分 APK 的名称。`PackageInstaller` 类 Javadoc 中记录的拆分约束：

- 所有 APK 必须共享相同的包名、版本号和签名证书
- 所有拆分名称必须唯一
- 必须始终存在一个基础 APK（拆分名称为 null）

### 6.2 会话拆分操作

```java
Session.removeSplit(String splitName)  // 在添加新拆分之前移除旧拆分
```

拆分移除发生在添加新 APK **之前**。API 文档明确指出升级功能拆分不需要先移除它。

### 6.3 隔离拆分加载

`ApplicationInfo.PRIVATE_FLAG_ISOLATED_SPLIT_LOADING`（第 15 位）：

> "设置后，应用仅在需要加载组件时才加载其拆分。拆分可以使用 `Context.createContextForSplit(String)` API 按需加载。"

这支持功能模块的按需交付（Play Feature Delivery / Dynamic Delivery），其中拆分是延迟加载而不是在启动时全部加载。

### 6.4 多包会话

`SessionParams.setMultiPackage()` 方法创建一个会话，该会话：
- 本身不包含 APK
- 通过 `Session.addChildSessionId(int)` 引用子会话
- 原子性地提交所有子会话
- 如果任何子会话失败，所有都失败（原子回滚）

这实现了相互依赖的包的原子安装，对 APEX 和 mainline 模块更新至关重要。

### 6.5 分阶段会话

```java
SessionParams.setStaged()  // 需要 INSTALL_PACKAGES 权限
```

分阶段会话被安排在下次重启时安装。`SessionInfo` 暴露分阶段会话状态：
- `isStagedSessionApplied` -- 已成功应用
- `isStagedSessionReady` -- 准备激活
- `isStagedSessionFailed` -- 失败并带有错误码

错误码：`STAGED_SESSION_NO_ERROR`、`STAGED_SESSION_VERIFICATION_FAILED`、`STAGED_SESSION_ACTIVATION_FAILED`、`STAGED_SESSION_UNKNOWN`。

**注意：** `getActiveStagedSession()` 已弃用，取而代之的是 `getActiveStagedSessions()`，反映出多个分阶段会话可以同时处于活跃状态。

---

## 7. 组件信息类

### 7.1 ActivityInfo

关键配置字段：

- **启动模式：** `LAUNCH_MULTIPLE`、`LAUNCH_SINGLE_TOP`、`LAUNCH_SINGLE_TASK`、`LAUNCH_SINGLE_INSTANCE`
- **文档启动模式：** `DOCUMENT_LAUNCH_NONE`、`DOCUMENT_LAUNCH_INTO_EXISTING`、`DOCUMENT_LAUNCH_ALWAYS`、`DOCUMENT_LAUNCH_NEVER`
- **调整大小模式：** 从 `RESIZE_MODE_UNRESIZEABLE` (0) 到 `RESIZE_MODE_FORCE_RESIZABLE_PRESERVE_ORIENTATION` (7) 共 8 个值
- **颜色模式：** `COLOR_MODE_DEFAULT`、`COLOR_MODE_WIDE_COLOR_GAMUT`、`COLOR_MODE_HDR`
- **持久化模式：** `PERSIST_ROOT_ONLY`、`PERSIST_NEVER`、`PERSIST_ACROSS_REBOOTS`

Android 11 值得注意的新增：`supportsSizeChanges`（第 252 行），表示 Activity 能优雅地处理显示尺寸变化。

### 7.2 ServiceInfo

Android 11 新增了两种需要显式声明的前台服务类型：

```java
FOREGROUND_SERVICE_TYPE_CAMERA = 1 << 6;      // 相机访问
FOREGROUND_SERVICE_TYPE_MICROPHONE = 1 << 7;   // 麦克风访问
```

Javadoc 明确警告（第 153-157、164-169 行），对于目标为 Android R 及以上的应用，前台服务**不能**在清单和 `startForeground()` 调用中都未声明适当类型的情况下访问相机或麦克风。

服务标志：
- `FLAG_STOP_WITH_TASK` -- 任务移除时自动停止
- `FLAG_ISOLATED_PROCESS` -- 在隔离进程中运行
- `FLAG_EXTERNAL_SERVICE` -- 可在调用者的包中运行
- `FLAG_USE_APP_ZYGOTE` -- 从应用 Zygote 派生以加快启动

### 7.3 ProviderInfo

`ProviderInfo` 建模内容提供者安全性：
- `readPermission` / `writePermission` -- 独立的读/写权限
- `grantUriPermissions` -- 是否允许 URI 级别的授权
- `forceUriPermissions` -- 始终应用 URI 权限授予
- `uriPermissionPatterns` -- 限制可授权的 URI
- `pathPermissions` -- 路径特定的权限检查

`multiprocess` 标志（第 78 行）控制是否可以在不同进程中运行多个实例。`initOrder` 字段控制单进程提供者的初始化顺序。

### 7.4 ResolveInfo

`ResolveInfo` 是 Intent 解析的结果容器。`activityInfo`、`serviceInfo` 或 `providerInfo` 中恰好一个不为 null。值得注意的字段：

- `isInstantAppAvailable` -- 是否有免安装应用可用于此 Intent
- `auxiliaryInfo`（隐藏）-- 免安装应用或未安装拆分的辅助解析数据
- `filter` -- 匹配的 `IntentFilter`（需要 `GET_RESOLVED_FILTER`）
- `priority` / `preferredOrder` / `match` -- 解析排序

---

## 8. ApplicationInfo 标志分析

### 8.1 公开标志（32 位，已全部使用）

`flags` 字段使用了所有 32 位，从 `FLAG_SYSTEM`（第 0 位）到 `FLAG_MULTIARCH`（第 31 位）。值得注意的条目：

| 标志 | 位 | 安全/安全性相关 |
|------|-----|----------------|
| `FLAG_DEBUGGABLE` | 1 | 允许调试；安全敏感 |
| `FLAG_TEST_ONLY` | 8 | 没有 `INSTALL_ALLOW_TEST` 则阻止生产安装 |
| `FLAG_USES_CLEARTEXT_TRAFFIC` | 27 | 控制 HTTP vs HTTPS 强制执行 |
| `FLAG_EXTRACT_NATIVE_LIBS` | 28 | 控制从 APK 提取原生库 |
| `FLAG_SUSPENDED` | 30 | 应用被暂停（不可启动） |

### 8.2 私有标志（隐藏 API）

`privateFlags` 提供额外的系统内部元数据，超过 25 个位位置：

| 标志 | 位 | 用途 |
|------|-----|------|
| `PRIVATE_FLAG_HIDDEN` | 0 | 通过限制隐藏 |
| `PRIVATE_FLAG_PRIVILEGED` | 3 | 可持有特权权限 |
| `PRIVATE_FLAG_INSTANT` | 7 | 作为免安装应用安装 |
| `PRIVATE_FLAG_ISOLATED_SPLIT_LOADING` | 15 | 按需拆分加载 |
| `PRIVATE_FLAG_SIGNED_WITH_PLATFORM_KEY` | 20 | 平台签名 |
| `PRIVATE_FLAG_PROFILEABLE_BY_SHELL` | 23 | Shell 可分析此应用 |

**关注 - 分区标志：** 五个独立的标志跟踪应用来源的系统分区：`PRIVATE_FLAG_OEM` (17)、`PRIVATE_FLAG_VENDOR` (18)、`PRIVATE_FLAG_PRODUCT` (19)、`PRIVATE_FLAG_SYSTEM_EXT` (21)，加上公开标志中的 `FLAG_SYSTEM`。当结构化分区标识符可能更清晰时，这是一个扁平枚举，但位标志方式与代码库的其余部分保持一致。

---

## 9. 权限管理

### 9.1 运行时权限授予

`PackageManager` 提供了全面的权限管理 API：

```java
grantRuntimePermission(String packageName, String permName, UserHandle user)
revokeRuntimePermission(String packageName, String permName, UserHandle user)
getPermissionFlags(String permName, String packageName, UserHandle user)
updatePermissionFlags(String permName, String packageName, int flagMask, int flagValues, UserHandle user)
```

所有都是 `@SystemApi`，需要 `GRANT_RUNTIME_PERMISSIONS` 或 `REVOKE_RUNTIME_PERMISSIONS`。

### 9.2 权限标志

16 个权限标志常量（第 0-15 位）控制细粒度的权限状态：

- `FLAG_PERMISSION_USER_SET` / `FLAG_PERMISSION_USER_FIXED` -- 用户已做出选择
- `FLAG_PERMISSION_POLICY_FIXED` / `FLAG_PERMISSION_SYSTEM_FIXED` -- 系统锁定
- `FLAG_PERMISSION_ONE_TIME` -- 一次性授予（Android 11）
- `FLAG_PERMISSION_AUTO_REVOKED` -- 未使用应用的自动撤销（Android 11）
- `FLAG_PERMISSION_REVOKED_COMPAT` -- 遗留应用升级时撤销

### 9.3 受限权限白名单

三级白名单系统（系统、升级、安装器）提供：
- 系统白名单：由预安装的权限持有者控制
- 升级白名单：在权限变为受限的 OS 版本过渡期间应用
- 安装器白名单：由包安装器管理

`SessionParams.setWhitelistedRestrictedPermissions()` 控制安装时白名单中的受限权限。默认值为 `RESTRICTED_PERMISSIONS_ALL`。

### 9.4 自动撤销（Android 11）

```java
public void setAutoRevokePermissionsMode(boolean shouldAutoRevoke) {
    autoRevokePermissionsMode = shouldAutoRevoke ? MODE_ALLOWED : MODE_IGNORED;
}
```

**Javadoc 中的拼写错误：** 第 1742 行有 "extended periodd"（多了一个 'd'）。次要文档问题。

安装器可以为重新请求权限可能导致故障的包选择退出自动撤销。用户通过设置的覆盖优先。

---

## 10. 功能标志与系统功能

### 10.1 功能查询 API

```java
hasSystemFeature(String featureName)
hasSystemFeature(String featureName, int version)
getSystemAvailableFeatures()
```

功能有版本控制且向后兼容：`hasSystemFeature(name, version)` 在可用版本 >= 请求版本时返回 true。

### 10.2 值得注意的 Android 11 功能

| 功能常量 | 字符串值 |
|----------|---------|
| `FEATURE_CONTROLS` | `android.software.controls` |
| `FEATURE_INCREMENTAL_DELIVERY` | `android.software.incremental_delivery` |
| `FEATURE_APP_ENUMERATION` | `android.software.app_enumeration` |
| `FEATURE_REBOOT_ESCROW` | `android.hardware.reboot_escrow` |

---

## 11. 清单解析（parsing/ 包）

### 11.1 架构

Android 11 引入了 `android.content.pm.parsing` 下的重构清单解析系统：

- `ParsingPackageUtils` -- 主解析引擎，处理 AndroidManifest.xml
- `ParsingPackage` / `ParsingPackageImpl` -- 可变的已解析包表示
- `ParsingPackageRead` -- 只读接口
- `ApkLiteParseUtils` -- 轻量级 APK 解析（用于安装决策）
- `PackageInfoWithoutStateUtils` -- 在不含运行时状态的情况下将已解析的包转换为 `PackageInfo`

### 11.2 组件解析

每个清单元素都有单独的组件解析器：
- `ParsedActivityUtils` -- `<activity>` 和 `<receiver>`
- `ParsedServiceUtils` -- `<service>`
- `ParsedProviderUtils` -- `<provider>`
- `ParsedPermissionUtils` -- `<permission>` 和 `<permission-group>`
- `ParsedInstrumentationUtils` -- `<instrumentation>`
- `ParsedIntentInfoUtils` -- `<intent-filter>`
- `ParsedAttributionUtils` -- `<attribution>`（Android 11 新增）

### 11.3 错误处理

解析系统使用 `ParseResult<T>` 模式（类似结果/错误单子），`ParseInput` 提供工厂方法。错误引用 `PackageManager` 中的 `INSTALL_PARSE_FAILED_*` 常量。`DeferredError` 机制允许收集非致命解析错误并稍后报告。

---

## 12. 安全考虑

### 12.1 包验证

验证系统在安装期间触发，附带 extras：
- `EXTRA_VERIFICATION_ID` -- 待验证的唯一 ID
- `EXTRA_VERIFICATION_PACKAGE_NAME` -- 正在验证的包
- `EXTRA_VERIFICATION_ROOT_HASH` -- Merkle 树根哈希（SHA-256，4096 字节块）
- `EXTRA_VERIFICATION_INSTALL_FLAGS` -- 用于上下文的安装标志

验证结果为 `VERIFICATION_ALLOW` 或 `VERIFICATION_REJECT`，带有超时回退（`INSTALL_FAILED_VERIFICATION_TIMEOUT`）。

### 12.2 签名证书验证

```java
hasSigningCertificate(String packageName, byte[] certificate, @CertificateInputType int type)
hasSigningCertificate(int uid, byte[] certificate, @CertificateInputType int type)
```

支持原始 X.509（`CERT_INPUT_RAW_X509`）和 SHA256 哈希（`CERT_INPUT_SHA256`）输入。此 API 正确处理签名证书轮换，不同于已弃用的 `GET_SIGNATURES` 方式。

### 12.3 未知来源控制

```java
public abstract boolean canRequestPackageInstalls();
```

需要 `REQUEST_INSTALL_PACKAGES` 权限并目标为 Android O+。返回调用包是否被用户信任以安装包。用户可以在设置中逐应用管理此项。

### 12.4 有害应用警告

```java
setHarmfulAppWarning(String packageName, CharSequence warning)  // @SystemApi
getHarmfulAppWarning(String packageName)                         // @SystemApi
```

系统级 API，用于将应用标记为潜在有害，向用户显示为警告。

---

## 13. 已识别的问题和建议

### 13.1 API 表面复杂性（中等）

`PackageManager` 8,248 行是一个异常大的类。它混合了：
- 包查询（第 3757-4913 行）
- 权限管理（第 4261-4700 行）
- 组件状态（第 6809-6851 行）
- 功能查询（第 5130-5165 行）
- Intent 解析（第 5166-5400 行）
- 首选 Activity（已弃用，第 6596-6798 行）
- 包验证（第 3135-3270 行）
- 各种系统包名获取器（第 7840-7945 行）

**建议：** 虽然由于 API 稳定性要求重构此类不切实际，但文档应引导开发者使用适当的子集。考虑新功能领域（如模块、增量安装）是否应使用专用的管理器类。

### 13.2 已弃用 API 累积（低）

类中保留了多个已弃用的 API：
- `GET_SIGNATURES`（被 `GET_SIGNING_CERTIFICATES` 替代）
- `GET_DISABLED_COMPONENTS`（被 `MATCH_DISABLED_COMPONENTS` 替代）
- `GET_UNINSTALLED_PACKAGES`（被 `MATCH_UNINSTALLED_PACKAGES` 替代）
- `addPackageToPreferred()`、`removePackageFromPreferred()`、`getPreferredPackages()`、`addPreferredActivity()`、`clearPackagePreferredActivities()` -- 全部已弃用，推荐使用 `RoleManager`
- `PackageInfo` 上的 `versionCode` 字段（使用 `getLongVersionCode()`）

这些已弃用方法正确地委托或抛出异常，`@Deprecated` 注解已存在，但数量增加了认知负担。

### 13.3 RollbackDataPolicy.RETAIN 未实现（低-中）

```java
int RETAIN = 2;  // TODO: 尚未实现。
```

`RETAIN` 回滚数据策略已定义但标记为未实现。引用此值的任何代码可能不会按预期行为运行。

### 13.4 UnsupportedAppUsage 注解（低）

许多隐藏字段带有 `@UnsupportedAppUsage` 注解和 `maxTargetSdk` 限制，特别是在 `SessionParams` 和 `SessionInfo` 中。这些反映了 Android P 中引入的灰名单/黑名单强制执行。使用反射访问这些字段的应用将在较新 SDK 上失败。这是设计上的，但值得在兼容性分析中注意。

### 13.5 包可见性行为风险（对应用开发者高）

查询结果的静默过滤（`FILTER_APPLICATION_QUERY`）是影响最大的行为变更。与抛出异常的权限拒绝不同，过滤后的查询**静默返回空或不完整的结果**。不声明 `<queries>` 或 `QUERY_ALL_PACKAGES` 的应用将静默失去对其他包的可见性。

**对开发者的建议：**
1. 始终为需要交互的包声明特定的 `<queries>` 元素
2. 仅在真正需要时使用 `QUERY_ALL_PACKAGES`（如启动器、安全应用）
3. 开发期间启用包可见性限制进行测试

### 13.6 前台服务类型要求（对应用开发者中等）

从前台服务访问相机和麦克风现在需要在清单中（`foregroundServiceType`）和运行时（`startForeground(id, notification, type)`）都进行显式类型声明。未声明导致访问被拒绝而不是崩溃 — 一种可能难以调试的静默失败模式。

---

## 14. 此子系统中 Android 11 变更总结

| 变更 | 影响 | 风险 |
|------|------|------|
| 包可见性过滤（`<queries>`） | 所有查询其他包的应用 | 高 -- 静默数据丢失 |
| 前台服务类型：相机/麦克风 | 使用相机/麦克风的服务 | 中 -- 静默访问拒绝 |
| 未使用应用的权限自动撤销 | 所有带运行时权限的应用 | 中 -- 权限消失 |
| 增量交付 / 流式安装 | 应用商店、安装器 | 低 -- 可选功能 |
| 一次性权限授予 | 隐私敏感应用 | 低 -- 对大多数应用透明 |
| APEX/分阶段会话改进 | 系统组件 | 低 -- 系统内部 |
| 清单中的 `<attribution>` 标签 | 所有应用 | 低 -- 可选元数据 |

---

## 15. 关键源文件路径

- `/home/dspfac/aosp-android-11/frameworks/base/core/java/android/content/pm/PackageManager.java`
- `/home/dspfac/aosp-android-11/frameworks/base/core/java/android/content/pm/PackageInstaller.java`
- `/home/dspfac/aosp-android-11/frameworks/base/core/java/android/content/pm/PackageInfo.java`
- `/home/dspfac/aosp-android-11/frameworks/base/core/java/android/content/pm/ApplicationInfo.java`
- `/home/dspfac/aosp-android-11/frameworks/base/core/java/android/content/pm/ActivityInfo.java`
- `/home/dspfac/aosp-android-11/frameworks/base/core/java/android/content/pm/ServiceInfo.java`
- `/home/dspfac/aosp-android-11/frameworks/base/core/java/android/content/pm/ProviderInfo.java`
- `/home/dspfac/aosp-android-11/frameworks/base/core/java/android/content/pm/ResolveInfo.java`
- `/home/dspfac/aosp-android-11/frameworks/base/core/java/android/content/pm/parsing/ParsingPackageUtils.java`

---

*报告基于 Android 11 AOSP 源码审查生成。*
