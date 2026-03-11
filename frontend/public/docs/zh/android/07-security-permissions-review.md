# Android 11 (AOSP) 安全与权限子系统 - 代码审查报告

## 1. 执行摘要

本报告审查了 Android 11 的安全与权限子系统，涵盖 Android Keystore 系统、生物特征认证、运行时权限模型、网络安全配置以及相关的加密基础设施。该子系统包含约 80 多个 Java 源文件，分布在框架核心、keystore 和 server 包中。Android 11 引入了重大增强功能，包括一次性权限、生物特征强度分级、未使用权限的自动撤销，以及 Keystore 认证模型的改进。

---

## 2. Android Keystore 系统

### 2.1 KeyGenParameterSpec

**文件：** `frameworks/base/keystore/java/android/security/keystore/KeyGenParameterSpec.java`

**用途：** 定义用于初始化 Android Keystore 的 `KeyPairGenerator` 或 `KeyGenerator` 的 `AlgorithmParameterSpec`。这是控制所有密钥生成参数（包括授权约束、有效期和硬件支持要求）的主要类。

**关键字段（共 26 个）：**
- `mKeystoreAlias` - Keystore 中的密钥别名
- `mPurposes` - PURPOSE_ENCRYPT、PURPOSE_DECRYPT、PURPOSE_SIGN、PURPOSE_VERIFY、PURPOSE_WRAP_KEY 的位掩码
- `mUserAuthenticationRequired` - 使用密钥前是否需要用户认证
- `mUserAuthenticationValidityDurationSeconds` - 认证有效窗口（-1 = 每次使用）
- `mUserAuthenticationType` - AUTH_BIOMETRIC_STRONG 或 AUTH_DEVICE_CREDENTIAL
- `mAttestationChallenge` - 硬件证明证书扩展的 Blob
- `mIsStrongBoxBacked` - 密钥是否存储在 StrongBox 安全元件中
- `mUserConfirmationRequired` - 签名前需要 ConfirmationPrompt
- `mUnlockedDeviceRequired` - 私钥操作需要屏幕解锁
- `mInvalidatedByBiometricEnrollment` - 生物特征注册变更时密钥失效
- `mCriticalToDeviceEncryption` - 密钥对 FBE 至关重要（隐藏 API）
- `mUserPresenceRequired` - 需要硬件用户在场测试（如按钮按压）
- `mUniqueIdIncluded` - 证明包含设备唯一 ID（隐藏/系统 API）

**公开 Builder API：**
- `Builder(String keystoreAlias, @PurposeEnum int purposes)` - 主构造函数
- `setKeySize(int)`、`setAlgorithmParameterSpec(AlgorithmParameterSpec)` - 密钥参数
- `setDigests(String...)`、`setBlockModes(String...)`、`setEncryptionPaddings(String...)`、`setSignaturePaddings(String...)` - 加密参数
- `setUserAuthenticationRequired(boolean)`、`setUserAuthenticationValidityDurationSeconds(int)` - 认证参数
- `setUserAuthenticationParameters(int timeout, @AuthEnum int type)` - Android 11 新增，替代仅设置时长的方法
- `setAttestationChallenge(byte[])` - 启用密钥证明
- `setIsStrongBoxBacked(boolean)` - 请求 StrongBox 硬件
- `setUserConfirmationRequired(boolean)` - ConfirmationPrompt 集成
- `setInvalidatedByBiometricEnrollment(boolean)` - 生物特征注册失效
- `setUnlockedDeviceRequired(boolean)` - 需要设备解锁

**隐藏/系统 API：**
- `setUid(int)` - `@SystemApi` - 跨 UID 密钥所有权
- `setUniqueIdIncluded(boolean)` - `@SystemApi` - 证明中的唯一 ID
- `setCriticalToDeviceEncryption(boolean)` - `@SystemApi` - FBE 关键密钥
- `Builder(KeyGenParameterSpec)` - 用于 parcelable 传输的拷贝构造函数

**安全模式：** 构造后不可变。通过 `Utils.cloneIfNotNull()` 对所有可变对象（Date、byte[]）进行防御性拷贝。默认 `mRandomizedEncryptionRequired = true` 强制 IND-CPA 安全性。默认 `mInvalidatedByBiometricEnrollment = true` 确保生物特征注册变更时密钥失效。

**值得注意的设计决策：** 如果密钥未授权 PURPOSE_SIGN，为非对称密钥生成的自签名证书可能具有无效签名。文档说明这是可接受的，因为证书仅用作公钥的容器。

---

### 2.2 KeyProperties

**文件：** `frameworks/base/keystore/java/android/security/keystore/KeyProperties.java`

**用途：** 定义 Android Keystore 密钥属性的所有常量和类型安全枚举的抽象工具类。提供 Java 层常量和 Keymaster HAL 常量之间的双向映射。

**公开常量：**

| 类别 | 常量 |
|------|------|
| **用途** | `PURPOSE_ENCRYPT` (1)、`PURPOSE_DECRYPT` (2)、`PURPOSE_SIGN` (4)、`PURPOSE_VERIFY` (8)、`PURPOSE_WRAP_KEY` (32) |
| **算法** | `KEY_ALGORITHM_RSA`、`KEY_ALGORITHM_EC`、`KEY_ALGORITHM_AES`、`KEY_ALGORITHM_3DES`（已弃用）、`KEY_ALGORITHM_HMAC_SHA{1,224,256,384,512}` |
| **分组模式** | `BLOCK_MODE_ECB`、`BLOCK_MODE_CBC`、`BLOCK_MODE_CTR`、`BLOCK_MODE_GCM` |
| **加密填充** | `ENCRYPTION_PADDING_NONE`、`ENCRYPTION_PADDING_PKCS7`、`ENCRYPTION_PADDING_RSA_PKCS1`、`ENCRYPTION_PADDING_RSA_OAEP` |
| **签名填充** | `SIGNATURE_PADDING_RSA_PKCS1`、`SIGNATURE_PADDING_RSA_PSS` |
| **摘要** | `DIGEST_NONE`、`DIGEST_MD5`、`DIGEST_SHA1`、`DIGEST_SHA{224,256,384,512}` |
| **来源** | `ORIGIN_GENERATED` (1)、`ORIGIN_IMPORTED` (2)、`ORIGIN_UNKNOWN` (4)、`ORIGIN_SECURELY_IMPORTED` (8) |
| **认证** | `AUTH_DEVICE_CREDENTIAL` (1)、`AUTH_BIOMETRIC_STRONG` (2) |

**隐藏 API：** 所有内部转换类（`Purpose`、`KeyAlgorithm`、`BlockMode`、`EncryptionPadding`、`SignaturePadding`、`Digest`、`Origin`）提供 `toKeymaster()` 和 `fromKeymaster()` 转换，并标记为 `@hide`。

**安全注意事项：** 包含 `KEY_ALGORITHM_3DES` 但已弃用，建议使用 AES。枚举中包含 MD5 摘要，但不应用于安全关键操作。

---

### 2.3 KeyProtection

**文件：** `frameworks/base/keystore/java/android/security/keystore/KeyProtection.java`

**用途：** 指定导入的密钥（与生成的密钥不同）在 Android Keystore 中的安全方式。为导入操作镜像 `KeyGenParameterSpec`，实现 `KeyStore.ProtectionParameter` 和 `UserAuthArgs`。

**与 KeyGenParameterSpec 的主要区别：** 与 `KeyStore.setEntry()` 一起用于导入现有密钥材料，而 `KeyGenParameterSpec` 与 `KeyGenerator`/`KeyPairGenerator` 一起用于生成新密钥。不包含证书参数或证明挑战。

---

### 2.4 AndroidKeyStoreProvider

**文件：** `frameworks/base/keystore/java/android/security/keystore/AndroidKeyStoreProvider.java`

**用途：** 注册所有 AndroidKeyStore 服务的 JCA 安全提供者实现。标记为 `@hide @SystemApi`。

**已注册的服务：**
- `KeyStore.AndroidKeyStore` -> `AndroidKeyStoreSpi`
- `KeyPairGenerator.{EC,RSA}` -> `AndroidKeyStoreKeyPairGeneratorSpi$EC/$RSA`
- `KeyFactory.{EC,RSA}` -> `AndroidKeyStoreKeyFactorySpi`
- `KeyGenerator.{AES,HmacSHA*}` -> `AndroidKeyStoreKeyGeneratorSpi$*`
- `KeyGenerator.DESede` - 根据 `ro.hardware.keystore_desede` 系统属性有条件注册
- `SecretKeyFactory.{AES,DESede,HmacSHA*}` -> `AndroidKeyStoreSecretKeyFactorySpi`

**安装策略：** `install()` 方法将 `AndroidKeyStoreBCWorkaroundProvider` 插入 BouncyCastle 提供者上方，确保 AndroidKeyStore 密钥使用正确的加密实现而不是 BC 回退。

**隐藏 API：**
- `getKeyStoreOperationHandle(Object cryptoPrimitive)` - `@UnsupportedAppUsage` - 从 Cipher/Mac/Signature 获取 KeyStore 操作句柄，用于 BiometricPrompt 绑定
- `getKeyStoreForUid(int uid)` - `@SystemApi` - 系统组件（Wi-Fi、VPN）的跨 UID KeyStore 访问
- `loadAndroidKeyStoreKeyFromKeystore(KeyStore, String, int)` - 带永久失效检测的密钥加载

**密钥失效模式：** 当用户在创建带生物特征绑定的密钥后更改认证凭据时，`getKeyCharacteristics()` 返回 `KEY_PERMANENTLY_INVALIDATED`，抛出 `KeyPermanentlyInvalidatedException`。

---

### 2.5 KeyInfo

**文件：** `frameworks/base/keystore/java/android/security/keystore/KeyInfo.java`

**用途：** 提供现有 Android Keystore 密钥信息的不可变 `KeySpec` 实现。通过 `SecretKeyFactory.getKeySpec()` 或 `KeyFactory.getKeySpec()` 获取。

**可查询的关键属性：**
- `isInsideSecureHardware()` - 密钥材料是否在 TEE/StrongBox 中
- `getOrigin()` - 密钥的创建方式（生成、导入、安全导入）
- `isUserAuthenticationRequired()` / `getUserAuthenticationValidityDurationSeconds()`
- `isUserAuthenticationRequirementEnforcedBySecureHardware()` - 认证强制是否由硬件支持
- `isInvalidatedByBiometricEnrollment()` / `isUserConfirmationRequired()`
- `isTrustedUserPresenceRequired()` - StrongBox 物理在场要求

---

### 2.6 AttestationUtils

**文件：** `frameworks/base/keystore/java/android/security/keystore/AttestationUtils.java`

**用途：** `@SystemApi @TestApi` 设备身份证明工具。允许具有系统特权的调用者使用硬件支持的密钥证明来证明设备标识符（IMEI、MEID、序列号）。

**公开 API：**
- `attestDeviceIds(Context, int[] idTypes, byte[] attestationChallenge)` - 需要 `READ_PRIVILEGED_PHONE_STATE`
- 常量：`ID_TYPE_SERIAL` (1)、`ID_TYPE_IMEI` (2)、`ID_TYPE_MEID` (3)、`USE_INDIVIDUAL_ATTESTATION` (4)

**隐藏 API：**
- `prepareAttestationArguments()` - KeyChain 内部使用
- `prepareAttestationArgumentsIfMisprovisioned()` - Pixel 2 错误配置的解决方案 (b/69471841)
- `parseCertificateChain()` - DevicePolicyManager 使用

**安全模式：** 始终在证明记录中包含设备品牌、设备、产品、制造商和型号。`USE_INDIVIDUAL_ATTESTATION` 标志请求设备使用其设备唯一的证明密钥，而非批量证明。

---

## 3. KeyChain API

**文件：** `frameworks/base/keystore/java/android/security/KeyChain.java`

**用途：** 提供面向用户的凭据存储中私钥和证书链的访问。与 Android Keystore（每应用）不同，KeyChain 是系统级的，需要用户批准密钥选择。

**公开 API 流程：**
1. `choosePrivateKeyAlias(Activity, KeyChainAliasCallback, String[], Principal[], Uri, String)` - 启动密钥选择的系统 UI
2. `getPrivateKey(Context, String alias)` - 检索选定的私钥（必须在工作线程调用）
3. `getCertificateChain(Context, String alias)` - 检索对应的证书链
4. `createInstallIntent()` - 用于安装私钥和 CA 证书的 Intent

**隐藏常量：**
- `ACCOUNT_TYPE = "com.android.keychain"` - KeyChain 的账户类型
- `KEYCHAIN_PACKAGE = "com.android.keychain"` - 选择器的包名
- `ACTION_CHOOSER = "com.android.keychain.CHOOSER"` - 选择器 Activity 的 action

**线程要求：** `getPrivateKey()` 和 `getCertificateChain()` 标注了 `@WorkerThread`，由于 IPC 绑定，不得在主线程调用。

---

## 4. 生物特征认证

### 4.1 BiometricPrompt

**文件：** `frameworks/base/core/java/android/hardware/biometrics/BiometricPrompt.java`

**用途：** 系统提供的生物特征认证对话框，支持指纹、面部和虹膜识别，以及设备凭据回退。

**公开 Builder API：**
- `setTitle(CharSequence)` - 必填
- `setSubtitle(CharSequence)`、`setDescription(CharSequence)` - 可选
- `setNegativeButton(CharSequence, Executor, OnClickListener)` - 除非允许设备凭据，否则必填
- `setConfirmationRequired(boolean)` - 隐式模态的提示（默认：true）
- `setAllowedAuthenticators(@Authenticators.Types int)` - Android 11 新增，替代 `setDeviceCredentialAllowed`
- `setDeviceCredentialAllowed(boolean)` - 在 Android 11 中已弃用

**认证方法：**
- `authenticate(CryptoObject, CancellationSignal, Executor, AuthenticationCallback)` - 加密绑定认证（需要 BIOMETRIC_STRONG）
- `authenticate(CancellationSignal, Executor, AuthenticationCallback)` - 非加密认证（允许 BIOMETRIC_WEAK）

**CryptoObject：** 包装 `Signature`、`Cipher`、`Mac` 或 `IdentityCredential`（Android 11 新增）。加密绑定认证仅强制使用 BIOMETRIC_STRONG 认证器。

**AuthenticationResult（Android 11 新增）：**
- `getAuthenticationType()` 返回 `AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL` 或 `AUTHENTICATION_RESULT_TYPE_BIOMETRIC`

**隐藏 API：**
- `authenticateUser(CancellationSignal, Executor, AuthenticationCallback, int userId)` - 需要 `USE_BIOMETRIC_INTERNAL`
- `setUseDefaultTitle()`、`setTextForDeviceCredential()` - 需要 `USE_BIOMETRIC_INTERNAL`
- `setReceiveSystemEvents(boolean)` - 需要 `USE_BIOMETRIC_INTERNAL`
- `DISMISSED_REASON_*` 常量 - 内部对话框关闭跟踪

**安全强制：** 当提供 `CryptoObject` 时，代码明确验证只允许 `BIOMETRIC_STRONG`：
```java
final int biometricStrength = authenticators & Authenticators.BIOMETRIC_WEAK;
if ((biometricStrength & ~Authenticators.BIOMETRIC_STRONG) != 0) {
    throw new IllegalArgumentException("Only Strong biometrics supported with crypto");
}
```

---

### 4.2 BiometricManager

**文件：** `frameworks/base/core/java/android/hardware/biometrics/BiometricManager.java`

**用途：** 提供生物特征能力查询和认证器类型定义的系统服务。

**Authenticators 接口（Android 11 关键新增）：**

| 常量 | 值 | 描述 |
|------|-----|------|
| `BIOMETRIC_STRONG` | 0x000F | 3 级生物特征（符合 CDD 的加密要求） |
| `BIOMETRIC_WEAK` | 0x00FF | 2 级生物特征（STRONG 的超集） |
| `BIOMETRIC_CONVENIENCE` | 0x0FFF | 1 级生物特征（隐藏，仅 DeviceConfig） |
| `DEVICE_CREDENTIAL` | 0x8000 | PIN/图案/密码 |
| `EMPTY_SET` | 0x0000 | `@SystemApi`，用于 DeviceConfig 调整 |

**公开 API：**
- `canAuthenticate(int authenticators)` - 返回 `BIOMETRIC_SUCCESS`、`BIOMETRIC_ERROR_*`
- 新错误码：`BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED` - 传感器需要安全更新

**设计模式：** 认证器类型使用位掩码设计，其中 BIOMETRIC_WEAK 是 BIOMETRIC_STRONG 的超集（`BIOMETRIC_STRONG | BIOMETRIC_WEAK == BIOMETRIC_WEAK`）。

---

## 5. 确认提示（受保护的确认）

**文件：** `frameworks/base/core/java/android/security/ConfirmationPrompt.java`

**用途：** 硬件支持的确认对话框，提供高可信度的用户确认。即使 Android 框架（包括内核）被攻破，正面响应也表明用户看到并批准了显示的文本。

**公开 API：**
- `Builder(Context).setPromptText(CharSequence).setExtraData(byte[]).build()`
- `presentPrompt(Executor, ConfirmationCallback)` - 显示硬件支持的提示
- `cancelPrompt()` - 取消已显示的提示
- `isSupported(Context)` - 检查硬件支持的静态方法

**安全架构：**
- 需要专用硬件（TEE/StrongBox ConfirmationUI HAL）
- 辅助功能服务运行时不可用（防止欺骗）
- 设计用于与使用 `setUserConfirmationRequired(true)` 创建的密钥配合使用
- `extraData` 参数在与依赖方配合使用时支持基于随机数的防重放

**回调状态：** `onConfirmed(byte[])`、`onDismissed()`、`onCanceled()`、`onError(Exception)`

**UI 选项标志：**
- `UI_OPTION_ACCESSIBILITY_INVERTED_FLAG` - 辅助功能的颜色反转
- `UI_OPTION_ACCESSIBILITY_MAGNIFIED_FLAG` - 字体缩放 > 1.0

---

## 6. 权限模型

### 6.1 PermissionManager

**文件：** `frameworks/base/core/java/android/permission/PermissionManager.java`

**用途：** 管理运行时权限的 `@SystemApi @TestApi` 系统服务。通过 `Context.PERMISSION_SERVICE` 访问。

**公开 API：**
- `getSplitPermissions()` - 返回在较新 API 级别中拆分的权限（例如 `ACCESS_COARSE_LOCATION` 拆分为前台 + `ACCESS_BACKGROUND_LOCATION`）

**系统 API（Android 11 新增）：**
- `getAutoRevokeExemptionRequestedPackages()` - 请求自动撤销豁免的应用
- `getAutoRevokeExemptionGrantedPackages()` - 已获得自动撤销豁免的应用
- `startOneTimePermissionSession(String, long, int, int)` - 开始一次性权限跟踪
- `stopOneTimePermissionSession(String)` - 停止一次性权限跟踪
- `checkDeviceIdentifierAccess(String, String, String, int, int)` - 检查设备 ID 访问

**隐藏 API：**
- `grantDefaultPermissionsToLuiApp()`、`revokeDefaultPermissionsFromLuiApps()` - LUI 应用权限
- `grantDefaultPermissionsToEnabledImsServices()` - IMS 服务权限
- `grantDefaultPermissionsToEnabledTelephonyDataServices()` - 电话数据服务权限
- `grantDefaultPermissionsToEnabledCarrierApps()` - 运营商应用权限

**权限缓存：** 使用 `PropertyInvalidatedCache` 进行基于 UID 和包名的权限检查，缓存大小为 16 个条目，以 `CACHE_KEY_PACKAGE_INFO` 为键。

**值得注意的设计：** `PermissionQuery` 类有意在相等性比较中排除 `pid` — 安全检查仅使用 `uid`。pid 仅用于日志记录目的。

**SplitPermissionInfo：** 表示跨 API 级别的权限拆分：
- `getSplitPermission()` - 原始权限
- `getNewPermissions()` - 拆分后的权限
- `getTargetSdk()` - 低于此 SDK 的应用自动获得扩展

---

### 6.2 PermissionManagerService

**文件：** `frameworks/base/services/core/java/com/android/server/pm/permission/PermissionManagerService.java`

**用途：** `IPermissionManager` 的服务端实现。管理所有权限的授予、撤销和策略执行。继承 `IPermissionManager.Stub`。

**授权类型：**
- `GRANT_DENIED` (1) - 权限未授予
- `GRANT_INSTALL` (2) - 安装时权限（normal/signature 保护级别）
- `GRANT_RUNTIME` (3) - 运行时（dangerous）权限
- `GRANT_UPGRADE` (4) - 将安装时授权转换为运行时授权

**阻止权限标志：** 防止自动修改的标志：
- `FLAG_PERMISSION_SYSTEM_FIXED` - 系统设置，不可变
- `FLAG_PERMISSION_POLICY_FIXED` - 策略设置（如设备管理员）
- `FLAG_PERMISSION_GRANTED_BY_DEFAULT` - 默认授予

**Android 11 权限标志：**
- `FLAG_PERMISSION_ONE_TIME` - 一次性权限授予
- `FLAG_PERMISSION_AUTO_REVOKED` - 未使用应用的自动撤销
- `FLAG_PERMISSION_REVOKE_WHEN_REQUESTED` - 下次请求时撤销
- `FLAG_PERMISSION_GRANTED_BY_ROLE` - 通过角色分配授予

**更完整的权限映射：**
```
ACCESS_COARSE_LOCATION -> ACCESS_FINE_LOCATION
INTERACT_ACROSS_USERS -> INTERACT_ACROSS_USERS_FULL
```

**存储权限：** 作为组跟踪：`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`、`ACCESS_MEDIA_LOCATION`。

**关键内部组件：**
- `DefaultPermissionGrantPolicy` - 开箱即用的默认权限授予
- `PermissionSettings` - 持久化权限存储
- `PermissionsState` - 每包权限状态跟踪
- `OneTimePermissionUserManager` - 每用户一次性权限管理

**回调模式：** `PermissionCallback` 接口包含：
- `onGidsChanged()` - GID 变更时终止应用
- `onPermissionGranted()` - 触发异步设置写入
- `onPermissionRevoked()` - 触发同步设置写入和应用终止

---

### 6.3 BasePermission

**文件：** `frameworks/base/services/core/java/com/android/server/pm/permission/BasePermission.java`

**用途：** 表示系统中的单个权限定义。

**权限类型：**
- `TYPE_NORMAL` (0) - 由第三方包定义
- `TYPE_BUILTIN` (1) - 在平台清单中定义
- `TYPE_DYNAMIC` (2) - 动态注册

**保护级别：**
- `PROTECTION_NORMAL` - 安装时自动授予
- `PROTECTION_DANGEROUS` - 需要运行时批准
- `PROTECTION_SIGNATURE` - 仅授予相同签名证书的应用
- `PROTECTION_SIGNATURE_OR_SYSTEM` - 已弃用，签名或特权应用

**关键字段：**
- `name` - 权限名称字符串
- `sourcePackageName` - 定义此权限的包
- `protectionLevel` - 保护级别位掩码
- `uid` - 拥有权限定义的 UID
- `gids` - 与此权限一起授予的额外 Linux GID
- `perm` - 从包清单解析的权限

---

### 6.4 OneTimePermissionUserManager

**文件：** `frameworks/base/services/core/java/com/android/server/pm/permission/OneTimePermissionUserManager.java`

**用途：** Android 11 功能 - 管理每用户的一次性权限会话。跟踪应用不活跃状态，并在应用变为不活跃时撤销权限。

**不活跃检测：**
- 每个跟踪的包有三个 `OnUidImportanceListener` 实例：
  - `mStartTimerListener` - 在 `importanceToResetTimer` 阈值
  - `mSessionKillableListener` - 在 `importanceToKeepSessionAlive` 阈值
  - `mGoneListener` - 在 `IMPORTANCE_CACHED` 级别
- 当重要性超过重置阈值时启动计时器
- 当重要性降至重置阈值以下时重置计时器
- 如果计时器触发时应用处于活跃和重置阈值之间，则延长会话
- 当应用超过 IMPORTANCE_CACHED 时，发生 5 秒延迟撤销（可通过 `DeviceConfig` 配置）

**会话超时流程：**
1. `startPackageOneTimeSession()` - 注册重要性监听器
2. 应用不活跃超过超时 -> `onPackageInactiveLocked()`
3. 调用 `PermissionControllerManager.notifyOneTimePermissionSessionTimeout()`
4. PermissionController 撤销一次性权限

**可配置延迟：** `DEFAULT_KILLED_DELAY_MILLIS = 5000`（5 秒），可通过 `DeviceConfig.NAMESPACE_PERMISSIONS` 键 `one_time_permissions_killed_delay_millis` 配置。

---

### 6.5 PermissionControllerService

**文件：** `frameworks/base/core/java/android/permission/PermissionControllerService.java`

**用途：** `@SystemApi` 抽象 `Service`，必须由系统权限控制器应用实现。提供框架与用户空间权限管理 UI 之间的接口。

**必须重写的方法：**
- `onRevokeRuntimePermissions(Map<String, List<String>>, boolean doDryRun, int reason, String callerPackageName, Consumer<Map<String, List<String>>>)`
- `onGetAppPermissions(String packageName, Consumer<List<RuntimePermissionPresentationInfo>>)`
- `onRevokeRuntimePermission(String packageName, String permissionName, Runnable)`
- `onCountPermissionApps(List<String>, int flags, IntConsumer)`
- `onGetPermissionUsages(boolean countSystem, long numMillis, Consumer<List<RuntimePermissionUsageInfo>>)`
- `onGrantOrUpgradeDefaultRuntimePermissions(Runnable)`
- `onSetRuntimePermissionGrantStateByDeviceAdmin(String, String, String, int, Consumer<Boolean>)`

**Android 11 新增：**
- `onOneTimePermissionSessionTimeout(String packageName)` - 一次性权限会话过期时调用
- `onUpdateUserSensitivePermissionFlags(int uid, Executor, Runnable)` - 更新 `FLAG_PERMISSION_USER_SENSITIVE_WHEN_*` 标志
- `CAMERA_MIC_INDICATORS_NOT_PRESENT` - `@ChangeId @Disabled` 相机/麦克风指示器兼容性标志

**备份/恢复：**
- `onGetRuntimePermissionsBackup()` / `onStageAndApplyRuntimePermissionsBackup()` - 权限状态备份
- `onApplyStagedRuntimePermissionBackup()` - 尚未安装的应用的延迟恢复

**Binder Stub 中的安全强制：**
- 每个方法验证调用者持有适当的权限（例如 `REVOKE_RUNTIME_PERMISSIONS`、`GET_RUNTIME_PERMISSIONS`、`GRANT_RUNTIME_PERMISSIONS`）
- `enforceSomePermissionsGrantedToCaller()` 检查任一语义
- 通过 `PackageManager.getPackageInfo()` 验证调用者 UID

**值得注意的 Bug：** 在 `setRuntimePermissionGrantStateByDeviceAdmin()` 中，`PERMISSION_GRANT_STATE_GRANTED` 和 `PERMISSION_GRANT_STATE_DENIED` 都检查 `GRANT_RUNTIME_PERMISSIONS`，但授予情况应该检查 `GRANT_RUNTIME_PERMISSIONS`（由于复制粘贴，条件 `grantState == PERMISSION_GRANT_STATE_DENIED` 出现了两次，导致代码只对 DENIED 强制执行 `GRANT_RUNTIME_PERMISSIONS` 检查，而不是对 GRANTED）。

---

## 7. 网络安全配置

### 7.1 NetworkSecurityPolicy

**文件：** `frameworks/base/core/java/android/security/NetworkSecurityPolicy.java`

**用途：** 管理进程明文网络流量的单例策略。网络栈遵循此策略以集中控制网络安全行为。

**公开 API：**
- `getInstance()` - 返回单例（可缓存）
- `isCleartextTrafficPermitted()` - 全局明文流量检查
- `isCleartextTrafficPermitted(String hostname)` - 每主机明文检查

**隐藏 API：**
- `setCleartextTrafficPermitted(boolean)` - 在应用初始化早期调用
- `handleTrustStorageUpdate()` - 证书变更时重新加载信任存储
- `getApplicationConfigForPackage(Context, String)` - 静态方法，返回包的 `ApplicationConfig`

---

### 7.2 NetworkSecurityConfig

**文件：** `frameworks/base/core/java/android/security/net/config/NetworkSecurityConfig.java`

**用途：** `@hide` - 表示域的网络安全设置或默认配置的不可变配置对象。

**配置属性：**
- `mCleartextTrafficPermitted` - 是否允许 HTTP
- `mHstsEnforced` - 是否强制执行 HSTS
- `mPins` - 证书固定的 `PinSet`
- `mCertificatesEntryRefs` - 证书源的有序列表

**默认配置 Builder（`getDefaultBuilder`）：**
- 目标 < API 28 (P) 的应用：允许明文流量（除非是免安装应用）
- 目标 <= API 23 (M) 且非特权的应用：信任用户证书存储
- 目标 >= API 24 (N) 的应用：默认只信任系统证书存储
- Android P+ 上的特权应用：不信任用户证书存储

**信任锚点解析：** 证书条目引用按排序，`overridesPins` 条目优先。当同一证书出现在多个引用中时，第一个出现的（带 overridesPins）优先。

---

### 7.3 XmlConfigSource

**文件：** `frameworks/base/core/java/android/security/net/config/XmlConfigSource.java`

**用途：** `@hide` - 将 `network_security_config.xml` 资源文件解析为 `NetworkSecurityConfig` 对象。

**解析的 XML 结构：**
```xml
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system"/>
        </trust-anchors>
    </base-config>
    <domain-config>
        <domain includeSubdomains="true">example.com</domain>
        <pin-set expiration="2025-01-01">
            <pin digest="SHA-256">base64-encoded-digest</pin>
        </pin-set>
        <trust-anchors>
            <certificates src="user|system|wfa|@resource"/>
        </trust-anchors>
    </domain-config>
    <debug-overrides>
        <trust-anchors>
            <certificates src="..." overridePins="true"/>
        </trust-anchors>
    </debug-overrides>
</network-security-config>
```

**证书来源：**
- `system` - `SystemCertificateSource`（单例）
- `user` - `UserCertificateSource`（单例）
- `wfa` - `WfaCertificateSource`（单例，Wi-Fi 联盟）
- `@resourceId` - `ResourceCertificateSource`（自定义捆绑证书）

**调试覆盖：** 仅在设置了 `ApplicationInfo.FLAG_DEBUGGABLE` 时应用。还会检查 `{resource_name}_debug.xml` 伴随资源。

**验证：** 重复的域名会抛出 `ParserException`。只允许一个 `base-config` 和一个 `debug-overrides` 部分。证书固定集只允许在 `domain-config` 中。

---

## 8. 文件完整性管理器

**文件：** `frameworks/base/core/java/android/security/FileIntegrityManager.java`

**用途：** `@SystemService(FILE_INTEGRITY_SERVICE)` - Android 11 新增。提供文件完整性操作的访问，特别是 APK Verity 支持。

**公开 API：**
- `isApkVeritySupported()` - 设备是否支持 APK 文件的 fs-verity
- `isAppSourceCertificateTrusted(X509Certificate)` - 证书是否可以证明应用安装来源。需要 `INSTALL_PACKAGES` 或 `REQUEST_INSTALL_PACKAGES`。

---

## 9. 加密工具

### 9.1 Scrypt

**文件：** `frameworks/base/core/java/android/security/Scrypt.java`

**用途：** `@hide` - scrypt 密码哈希算法的 JNI 包装。在内部用于设备加密密钥派生。

**API：** `scrypt(byte[] password, byte[] salt, int n, int r, int p, int outLen)` - 委托给原生 `nativeScrypt()`。

---

## 10. 安全模式与架构观察

### 10.1 纵深防御

1. **硬件支持的密钥**：TEE/StrongBox 中的密钥对应用处理器不可访问。`isInsideSecureHardware()` 和 `isStrongBoxBacked()` 允许验证。
2. **证明链**：密钥证明提供密钥属性的加密证明，根植于依赖方信任的 CA。
3. **生物特征强度分级**：Android 11 正式确定了 1/2/3 级生物特征强度，防止弱生物特征用于加密操作。
4. **一次性权限**：应用变为不活跃后权限自动撤销，减少持续的权限累积。

### 10.2 关键安全默认值

| 默认值 | 值 | 理由 |
|--------|-----|------|
| `mRandomizedEncryptionRequired` | `true` | 强制 IND-CPA（防止确定性加密） |
| `mInvalidatedByBiometricEnrollment` | `true` | 生物特征注册变更时密钥失效 |
| `mUserAuthenticationType` | `AUTH_BIOMETRIC_STRONG` | 默认仅强生物特征 |
| 明文流量（目标 >= API 28） | `false` | 默认 HTTPS |
| 用户证书信任（目标 >= API 24） | 不信任 | 仅信任系统证书 |

### 10.3 隐藏/系统 API 表面

安全子系统具有显著的隐藏 API 表面：
- **AndroidKeyStoreProvider** 完全是 `@hide @SystemApi`
- **跨 UID KeyStore 访问** - 通过 `getKeyStoreForUid()`
- **UniqueId 证明** - 用于设备识别
- **设备加密关键标志** - 用于 FBE 密钥
- **PermissionManager** 电话/运营商权限授予
- **BiometricPrompt** 需要 `USE_BIOMETRIC_INTERNAL` 的内部方法

### 10.4 潜在问题与观察

1. **PermissionControllerService Binder stub bug：** 在 `setRuntimePermissionGrantStateByDeviceAdmin()` 中，GRANT 情况检查 `grantState == PERMISSION_GRANT_STATE_DENIED` 而不是 `PERMISSION_GRANT_STATE_GRANTED`，这意味着 `GRANT_RUNTIME_PERMISSIONS` 检查仅对 DENIED 强制执行，而不是对 GRANTED。但是，`ADJUST_RUNTIME_PERMISSIONS_POLICY` 检查仍然适用于所有情况。

2. **PermissionQuery pid 排除：** `PermissionQuery.equals()` 方法有意忽略 pid，仅依赖 uid 进行安全决策。这被记录为正确的（uid 确定安全身份），但 pid 仍然传递给 `checkPermissionUncached()` 用于 ActivityManager 检查。

3. **3DES 条件支持：** `AndroidKeyStoreProvider` 根据 `ro.hardware.keystore_desede` 有条件地注册 DESede 支持，这意味着算法可用性因设备而异。该算法在 `KeyProperties` 中已弃用。

4. **BiometricPrompt 回退行为：** 当为 BIOMETRIC_STRONG 提供 `CryptoObject` 时，默认认证器设置正确。但是，非加密的 `authenticate()` 方法不限制认证器类型，默认允许 BIOMETRIC_WEAK。

5. **NetworkSecurityConfig 明文默认值：** `DEFAULT_CLEARTEXT_TRAFFIC_PERMITTED` 常量为 `true`，但 `getDefaultBuilder()` 对目标 API 28+ 的应用将其覆盖为 `false`。该常量本身可能具有误导性。

6. **一次性权限延迟撤销：** 应用终止前撤销权限的 5 秒终止延迟（`DEFAULT_KILLED_DELAY_MILLIS`）是安全性（立即撤销）和可用性（允许快速重启）之间的平衡。这可通过 DeviceConfig 远程配置。

---

## 11. 文件索引

| 文件 | 类别 |
|------|------|
| `frameworks/base/keystore/java/android/security/keystore/KeyGenParameterSpec.java` | Keystore - 密钥生成 |
| `frameworks/base/keystore/java/android/security/keystore/KeyProperties.java` | Keystore - 常量 |
| `frameworks/base/keystore/java/android/security/keystore/KeyProtection.java` | Keystore - 密钥导入 |
| `frameworks/base/keystore/java/android/security/keystore/KeyInfo.java` | Keystore - 密钥元数据 |
| `frameworks/base/keystore/java/android/security/keystore/AndroidKeyStoreProvider.java` | Keystore - JCA 提供者 |
| `frameworks/base/keystore/java/android/security/keystore/AttestationUtils.java` | Keystore - 设备证明 |
| `frameworks/base/keystore/java/android/security/KeyChain.java` | KeyChain API |
| `frameworks/base/core/java/android/hardware/biometrics/BiometricPrompt.java` | 生物特征认证 |
| `frameworks/base/core/java/android/hardware/biometrics/BiometricManager.java` | 生物特征能力查询 |
| `frameworks/base/core/java/android/security/ConfirmationPrompt.java` | 受保护的确认 |
| `frameworks/base/core/java/android/security/FileIntegrityManager.java` | 文件完整性 |
| `frameworks/base/core/java/android/security/NetworkSecurityPolicy.java` | 网络安全 |
| `frameworks/base/core/java/android/security/net/config/NetworkSecurityConfig.java` | 网络安全配置 |
| `frameworks/base/core/java/android/security/net/config/XmlConfigSource.java` | 网络安全 XML 解析器 |
| `frameworks/base/core/java/android/security/Scrypt.java` | 加密 - 密码哈希 |
| `frameworks/base/core/java/android/permission/PermissionManager.java` | 权限管理 |
| `frameworks/base/core/java/android/permission/PermissionControllerService.java` | 权限控制器接口 |
| `frameworks/base/services/core/java/com/android/server/pm/permission/PermissionManagerService.java` | 权限服务端实现 |
| `frameworks/base/services/core/java/com/android/server/pm/permission/BasePermission.java` | 权限定义 |
| `frameworks/base/services/core/java/com/android/server/pm/permission/OneTimePermissionUserManager.java` | 一次性权限管理 |
| `frameworks/base/services/core/java/com/android/server/pm/permission/PermissionSettings.java` | 权限存储 |
| `frameworks/base/services/core/java/com/android/server/pm/permission/PermissionsState.java` | 每包权限状态 |
| `frameworks/base/services/core/java/com/android/server/pm/permission/DefaultPermissionGrantPolicy.java` | 默认权限授予 |
