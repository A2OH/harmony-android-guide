# Android 11 (AOSP) Security and Permissions Subsystem - Code Review Report

## 1. Executive Summary

This report reviews the Android 11 security and permissions subsystem, covering the Android Keystore system, biometric authentication, the runtime permission model, network security configuration, and related cryptographic infrastructure. The subsystem comprises approximately 80+ Java source files across framework core, keystore, and server packages. Android 11 introduces significant enhancements including one-time permissions, biometric strength classification tiers, auto-revoke for unused permissions, and improvements to the Keystore authentication model.

---

## 2. Android Keystore System

### 2.1 KeyGenParameterSpec

**File:** `frameworks/base/keystore/java/android/security/keystore/KeyGenParameterSpec.java`

**Purpose:** Defines the `AlgorithmParameterSpec` used to initialize `KeyPairGenerator` or `KeyGenerator` for the Android Keystore. This is the primary class that controls all key generation parameters including authorization constraints, validity periods, and hardware-backed requirements.

**Key Fields (26 total):**
- `mKeystoreAlias` - Key alias in the Keystore
- `mPurposes` - Bitmask of PURPOSE_ENCRYPT, PURPOSE_DECRYPT, PURPOSE_SIGN, PURPOSE_VERIFY, PURPOSE_WRAP_KEY
- `mUserAuthenticationRequired` - Whether user auth is needed before key use
- `mUserAuthenticationValidityDurationSeconds` - Auth validity window (-1 = every use)
- `mUserAuthenticationType` - AUTH_BIOMETRIC_STRONG or AUTH_DEVICE_CREDENTIAL
- `mAttestationChallenge` - Blob for hardware attestation certificate extension
- `mIsStrongBoxBacked` - Whether key is in StrongBox secure element
- `mUserConfirmationRequired` - Requires ConfirmationPrompt before signing
- `mUnlockedDeviceRequired` - Screen must be unlocked for private operations
- `mInvalidatedByBiometricEnrollment` - Key invalidated when biometric enrollment changes
- `mCriticalToDeviceEncryption` - Key is critical to FBE (hidden API)
- `mUserPresenceRequired` - Requires hardware user presence test (e.g. button press)
- `mUniqueIdIncluded` - Attestation includes device unique ID (hidden/system API)

**Public Builder API:**
- `Builder(String keystoreAlias, @PurposeEnum int purposes)` - Primary constructor
- `setKeySize(int)`, `setAlgorithmParameterSpec(AlgorithmParameterSpec)` - Key params
- `setDigests(String...)`, `setBlockModes(String...)`, `setEncryptionPaddings(String...)`, `setSignaturePaddings(String...)` - Crypto params
- `setUserAuthenticationRequired(boolean)`, `setUserAuthenticationValidityDurationSeconds(int)` - Auth params
- `setUserAuthenticationParameters(int timeout, @AuthEnum int type)` - New in Android 11, replaces duration-only setter
- `setAttestationChallenge(byte[])` - Enables key attestation
- `setIsStrongBoxBacked(boolean)` - Request StrongBox hardware
- `setUserConfirmationRequired(boolean)` - ConfirmationPrompt integration
- `setInvalidatedByBiometricEnrollment(boolean)` - Biometric enrollment invalidation
- `setUnlockedDeviceRequired(boolean)` - Requires unlocked device

**Hidden/System APIs:**
- `setUid(int)` - `@SystemApi` - Cross-UID key ownership
- `setUniqueIdIncluded(boolean)` - `@SystemApi` - Unique ID in attestation
- `setCriticalToDeviceEncryption(boolean)` - `@SystemApi` - FBE critical keys
- `Builder(KeyGenParameterSpec)` - Copy constructor for parcelable transport

**Security Pattern:** Immutable after construction. Defensive copies of all mutable objects (Date, byte[]) via `Utils.cloneIfNotNull()`. Default `mRandomizedEncryptionRequired = true` enforces IND-CPA security. Default `mInvalidatedByBiometricEnrollment = true` ensures keys are invalidated when biometric enrollment changes.

**Notable Design Decision:** Self-signed certificates generated for asymmetric keys may have invalid signatures if the key does not authorize PURPOSE_SIGN. This is documented as acceptable since the certificate is only used as a container for the public key.

---

### 2.2 KeyProperties

**File:** `frameworks/base/keystore/java/android/security/keystore/KeyProperties.java`

**Purpose:** Abstract utility class defining all constants and type-safe enums for Android Keystore key properties. Provides bidirectional mapping between Java-level constants and Keymaster HAL constants.

**Public Constants:**

| Category | Constants |
|----------|-----------|
| **Purpose** | `PURPOSE_ENCRYPT` (1), `PURPOSE_DECRYPT` (2), `PURPOSE_SIGN` (4), `PURPOSE_VERIFY` (8), `PURPOSE_WRAP_KEY` (32) |
| **Algorithm** | `KEY_ALGORITHM_RSA`, `KEY_ALGORITHM_EC`, `KEY_ALGORITHM_AES`, `KEY_ALGORITHM_3DES` (deprecated), `KEY_ALGORITHM_HMAC_SHA{1,224,256,384,512}` |
| **Block Mode** | `BLOCK_MODE_ECB`, `BLOCK_MODE_CBC`, `BLOCK_MODE_CTR`, `BLOCK_MODE_GCM` |
| **Encryption Padding** | `ENCRYPTION_PADDING_NONE`, `ENCRYPTION_PADDING_PKCS7`, `ENCRYPTION_PADDING_RSA_PKCS1`, `ENCRYPTION_PADDING_RSA_OAEP` |
| **Signature Padding** | `SIGNATURE_PADDING_RSA_PKCS1`, `SIGNATURE_PADDING_RSA_PSS` |
| **Digest** | `DIGEST_NONE`, `DIGEST_MD5`, `DIGEST_SHA1`, `DIGEST_SHA{224,256,384,512}` |
| **Origin** | `ORIGIN_GENERATED` (1), `ORIGIN_IMPORTED` (2), `ORIGIN_UNKNOWN` (4), `ORIGIN_SECURELY_IMPORTED` (8) |
| **Auth** | `AUTH_DEVICE_CREDENTIAL` (1), `AUTH_BIOMETRIC_STRONG` (2) |

**Hidden APIs:** All inner translation classes (`Purpose`, `KeyAlgorithm`, `BlockMode`, `EncryptionPadding`, `SignaturePadding`, `Digest`, `Origin`) provide `toKeymaster()` and `fromKeymaster()` conversions and are marked `@hide`.

**Security Note:** `KEY_ALGORITHM_3DES` is included but deprecated with a recommendation to use AES. MD5 digest is included in the enum but should be avoided for security-critical operations.

---

### 2.3 KeyProtection

**File:** `frameworks/base/keystore/java/android/security/keystore/KeyProtection.java`

**Purpose:** Specifies how an imported key (as opposed to generated) is secured in the Android Keystore. Mirrors `KeyGenParameterSpec` for import operations, implementing `KeyStore.ProtectionParameter` and `UserAuthArgs`.

**Key Difference from KeyGenParameterSpec:** Used with `KeyStore.setEntry()` for importing existing key material, while `KeyGenParameterSpec` is used with `KeyGenerator`/`KeyPairGenerator` for generating new keys. Does not include certificate parameters or attestation challenge.

---

### 2.4 AndroidKeyStoreProvider

**File:** `frameworks/base/keystore/java/android/security/keystore/AndroidKeyStoreProvider.java`

**Purpose:** JCA Security Provider implementation that registers all AndroidKeyStore services. Marked `@hide @SystemApi`.

**Registered Services:**
- `KeyStore.AndroidKeyStore` -> `AndroidKeyStoreSpi`
- `KeyPairGenerator.{EC,RSA}` -> `AndroidKeyStoreKeyPairGeneratorSpi$EC/$RSA`
- `KeyFactory.{EC,RSA}` -> `AndroidKeyStoreKeyFactorySpi`
- `KeyGenerator.{AES,HmacSHA*}` -> `AndroidKeyStoreKeyGeneratorSpi$*`
- `KeyGenerator.DESede` - Conditional on `ro.hardware.keystore_desede` system property
- `SecretKeyFactory.{AES,DESede,HmacSHA*}` -> `AndroidKeyStoreSecretKeyFactorySpi`

**Installation Strategy:** The `install()` method inserts `AndroidKeyStoreBCWorkaroundProvider` above the BouncyCastle provider, ensuring AndroidKeyStore keys use the correct crypto implementations rather than BC fallbacks.

**Hidden APIs:**
- `getKeyStoreOperationHandle(Object cryptoPrimitive)` - `@UnsupportedAppUsage` - Gets KeyStore operation handle from Cipher/Mac/Signature for BiometricPrompt binding
- `getKeyStoreForUid(int uid)` - `@SystemApi` - Cross-UID KeyStore access for system components (Wi-Fi, VPN)
- `loadAndroidKeyStoreKeyFromKeystore(KeyStore, String, int)` - Key loading with permanent invalidation detection

**Key Invalidation Pattern:** `getKeyCharacteristics()` returns `KEY_PERMANENTLY_INVALIDATED` when a user changes their auth credentials after a key was created with biometric binding, throwing `KeyPermanentlyInvalidatedException`.

---

### 2.5 KeyInfo

**File:** `frameworks/base/keystore/java/android/security/keystore/KeyInfo.java`

**Purpose:** Immutable `KeySpec` implementation providing information about an existing Android Keystore key. Obtained via `SecretKeyFactory.getKeySpec()` or `KeyFactory.getKeySpec()`.

**Key Queryable Properties:**
- `isInsideSecureHardware()` - Whether key material is in TEE/StrongBox
- `getOrigin()` - How the key was created (generated, imported, securely imported)
- `isUserAuthenticationRequired()` / `getUserAuthenticationValidityDurationSeconds()`
- `isUserAuthenticationRequirementEnforcedBySecureHardware()` - Whether auth enforcement is hardware-backed
- `isInvalidatedByBiometricEnrollment()` / `isUserConfirmationRequired()`
- `isTrustedUserPresenceRequired()` - StrongBox physical presence requirement

---

### 2.6 AttestationUtils

**File:** `frameworks/base/keystore/java/android/security/keystore/AttestationUtils.java`

**Purpose:** `@SystemApi @TestApi` utility for device identity attestation. Allows system-privileged callers to attest device identifiers (IMEI, MEID, serial number) using hardware-backed key attestation.

**Public API:**
- `attestDeviceIds(Context, int[] idTypes, byte[] attestationChallenge)` - Requires `READ_PRIVILEGED_PHONE_STATE`
- Constants: `ID_TYPE_SERIAL` (1), `ID_TYPE_IMEI` (2), `ID_TYPE_MEID` (3), `USE_INDIVIDUAL_ATTESTATION` (4)

**Hidden APIs:**
- `prepareAttestationArguments()` - Internal use by KeyChain
- `prepareAttestationArgumentsIfMisprovisioned()` - Workaround for Pixel 2 misprovisioning (b/69471841)
- `parseCertificateChain()` - Used by DevicePolicyManager

**Security Pattern:** Always includes device brand, device, product, manufacturer, and model in attestation records. The `USE_INDIVIDUAL_ATTESTATION` flag requests the device use its device-unique attestation key rather than batch attestation.

---

## 3. KeyChain API

**File:** `frameworks/base/keystore/java/android/security/KeyChain.java`

**Purpose:** Provides user-facing access to private keys and certificate chains in credential storage. Unlike Android Keystore (per-app), KeyChain is system-wide and requires user approval for key selection.

**Public API Flow:**
1. `choosePrivateKeyAlias(Activity, KeyChainAliasCallback, String[], Principal[], Uri, String)` - Launches system UI for key selection
2. `getPrivateKey(Context, String alias)` - Retrieves selected private key (must be called on worker thread)
3. `getCertificateChain(Context, String alias)` - Retrieves corresponding certificate chain
4. `createInstallIntent()` - Intent for installing private keys and CA certificates

**Hidden Constants:**
- `ACCOUNT_TYPE = "com.android.keychain"` - Account type for KeyChain
- `KEYCHAIN_PACKAGE = "com.android.keychain"` - Package name for chooser
- `ACTION_CHOOSER = "com.android.keychain.CHOOSER"` - Chooser activity action

**Threading Requirement:** `getPrivateKey()` and `getCertificateChain()` are annotated `@WorkerThread` and must not be called on the main thread due to IPC binding.

---

## 4. Biometric Authentication

### 4.1 BiometricPrompt

**File:** `frameworks/base/core/java/android/hardware/biometrics/BiometricPrompt.java`

**Purpose:** System-provided biometric authentication dialog supporting fingerprint, face, and iris recognition, plus device credential fallback.

**Public Builder API:**
- `setTitle(CharSequence)` - Required
- `setSubtitle(CharSequence)`, `setDescription(CharSequence)` - Optional
- `setNegativeButton(CharSequence, Executor, OnClickListener)` - Required unless device credential allowed
- `setConfirmationRequired(boolean)` - Hint for implicit modalities (default: true)
- `setAllowedAuthenticators(@Authenticators.Types int)` - New in Android 11, replaces `setDeviceCredentialAllowed`
- `setDeviceCredentialAllowed(boolean)` - Deprecated in Android 11

**Authentication Methods:**
- `authenticate(CryptoObject, CancellationSignal, Executor, AuthenticationCallback)` - Crypto-bound auth (requires BIOMETRIC_STRONG)
- `authenticate(CancellationSignal, Executor, AuthenticationCallback)` - Non-crypto auth (allows BIOMETRIC_WEAK)

**CryptoObject:** Wraps `Signature`, `Cipher`, `Mac`, or `IdentityCredential` (new in Android 11). Crypto-bound authentication enforces only BIOMETRIC_STRONG authenticators.

**AuthenticationResult (Android 11 addition):**
- `getAuthenticationType()` returns `AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL` or `AUTHENTICATION_RESULT_TYPE_BIOMETRIC`

**Hidden APIs:**
- `authenticateUser(CancellationSignal, Executor, AuthenticationCallback, int userId)` - Requires `USE_BIOMETRIC_INTERNAL`
- `setUseDefaultTitle()`, `setTextForDeviceCredential()` - Requires `USE_BIOMETRIC_INTERNAL`
- `setReceiveSystemEvents(boolean)` - Requires `USE_BIOMETRIC_INTERNAL`
- `DISMISSED_REASON_*` constants - Internal dialog dismissal tracking

**Security Enforcement:** When `CryptoObject` is provided, the code explicitly validates that only `BIOMETRIC_STRONG` is allowed:
```java
final int biometricStrength = authenticators & Authenticators.BIOMETRIC_WEAK;
if ((biometricStrength & ~Authenticators.BIOMETRIC_STRONG) != 0) {
    throw new IllegalArgumentException("Only Strong biometrics supported with crypto");
}
```

---

### 4.2 BiometricManager

**File:** `frameworks/base/core/java/android/hardware/biometrics/BiometricManager.java`

**Purpose:** System service providing biometric capability queries and authenticator type definitions.

**Authenticators Interface (Android 11 key addition):**

| Constant | Value | Description |
|----------|-------|-------------|
| `BIOMETRIC_STRONG` | 0x000F | Class 3 biometric (CDD-compliant for crypto) |
| `BIOMETRIC_WEAK` | 0x00FF | Class 2 biometric (superset of STRONG) |
| `BIOMETRIC_CONVENIENCE` | 0x0FFF | Class 1 biometric (hidden, DeviceConfig only) |
| `DEVICE_CREDENTIAL` | 0x8000 | PIN/pattern/password |
| `EMPTY_SET` | 0x0000 | `@SystemApi` for DeviceConfig adjustment |

**Public API:**
- `canAuthenticate(int authenticators)` - Returns `BIOMETRIC_SUCCESS`, `BIOMETRIC_ERROR_*`
- New error code: `BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED` - Sensor needs security update

**Design Pattern:** Authenticator types use bitmask design where BIOMETRIC_WEAK is a superset of BIOMETRIC_STRONG (`BIOMETRIC_STRONG | BIOMETRIC_WEAK == BIOMETRIC_WEAK`).

---

## 5. Confirmation Prompt (Protected Confirmation)

**File:** `frameworks/base/core/java/android/security/ConfirmationPrompt.java`

**Purpose:** Hardware-backed confirmation dialog providing high-assurance user confirmation. Even if the Android framework (including kernel) is compromised, a positive response indicates the user saw and approved the displayed text.

**Public API:**
- `Builder(Context).setPromptText(CharSequence).setExtraData(byte[]).build()`
- `presentPrompt(Executor, ConfirmationCallback)` - Shows the hardware-backed prompt
- `cancelPrompt()` - Cancels a displayed prompt
- `isSupported(Context)` - Static method to check hardware support

**Security Architecture:**
- Requires dedicated hardware (TEE/StrongBox ConfirmationUI HAL)
- Not available when accessibility services are running (to prevent spoofing)
- Designed for use with keys created with `setUserConfirmationRequired(true)`
- The `extraData` parameter enables nonce-based anti-replay when used with a Relying Party

**Callback States:** `onConfirmed(byte[])`, `onDismissed()`, `onCanceled()`, `onError(Exception)`

**UI Options Flags:**
- `UI_OPTION_ACCESSIBILITY_INVERTED_FLAG` - Color inversion for accessibility
- `UI_OPTION_ACCESSIBILITY_MAGNIFIED_FLAG` - Font scale > 1.0

---

## 6. Permission Model

### 6.1 PermissionManager

**File:** `frameworks/base/core/java/android/permission/PermissionManager.java`

**Purpose:** `@SystemApi @TestApi` system service for managing runtime permissions. Accessed via `Context.PERMISSION_SERVICE`.

**Public API:**
- `getSplitPermissions()` - Returns permissions that were split in newer API levels (e.g., `ACCESS_COARSE_LOCATION` split into foreground + `ACCESS_BACKGROUND_LOCATION`)

**System APIs (Android 11 additions):**
- `getAutoRevokeExemptionRequestedPackages()` - Apps requesting auto-revoke exemption
- `getAutoRevokeExemptionGrantedPackages()` - Apps granted auto-revoke exemption
- `startOneTimePermissionSession(String, long, int, int)` - Start one-time permission tracking
- `stopOneTimePermissionSession(String)` - Stop one-time permission tracking
- `checkDeviceIdentifierAccess(String, String, String, int, int)` - Check device ID access

**Hidden APIs:**
- `grantDefaultPermissionsToLuiApp()`, `revokeDefaultPermissionsFromLuiApps()` - LUI app permissions
- `grantDefaultPermissionsToEnabledImsServices()` - IMS service permissions
- `grantDefaultPermissionsToEnabledTelephonyDataServices()` - Telephony permissions
- `grantDefaultPermissionsToEnabledCarrierApps()` - Carrier app permissions

**Permission Caching:** Uses `PropertyInvalidatedCache` for both UID-based and package-name-based permission checks, with cache size of 16 entries keyed by `CACHE_KEY_PACKAGE_INFO`.

**Notable Design:** The `PermissionQuery` class intentionally excludes `pid` from equality comparison -- security checks use only `uid`. The pid is tracked for logging purposes only.

**SplitPermissionInfo:** Represents permission splits across API levels:
- `getSplitPermission()` - The original permission
- `getNewPermissions()` - Permissions it was split into
- `getTargetSdk()` - Apps below this SDK get automatic expansion

---

### 6.2 PermissionManagerService

**File:** `frameworks/base/services/core/java/com/android/server/pm/permission/PermissionManagerService.java`

**Purpose:** Server-side implementation of `IPermissionManager`. Manages all permission grants, revocations, and policy enforcement. Extends `IPermissionManager.Stub`.

**Grant Types:**
- `GRANT_DENIED` (1) - Permission not granted
- `GRANT_INSTALL` (2) - Install-time permission (normal/signature protection)
- `GRANT_RUNTIME` (3) - Runtime (dangerous) permission
- `GRANT_UPGRADE` (4) - Convert install-time grant to runtime grant

**Blocking Permission Flags:** Flags that prevent automatic modification:
- `FLAG_PERMISSION_SYSTEM_FIXED` - System-set, immutable
- `FLAG_PERMISSION_POLICY_FIXED` - Policy-set (e.g., device admin)
- `FLAG_PERMISSION_GRANTED_BY_DEFAULT` - Default grant

**Android 11 Permission Flags:**
- `FLAG_PERMISSION_ONE_TIME` - One-time permission grant
- `FLAG_PERMISSION_AUTO_REVOKED` - Auto-revoked for unused apps
- `FLAG_PERMISSION_REVOKE_WHEN_REQUESTED` - Revoke on next request
- `FLAG_PERMISSION_GRANTED_BY_ROLE` - Granted via role assignment

**Fuller Permission Map:**
```
ACCESS_COARSE_LOCATION -> ACCESS_FINE_LOCATION
INTERACT_ACROSS_USERS -> INTERACT_ACROSS_USERS_FULL
```

**Storage Permissions:** Tracked as a group: `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `ACCESS_MEDIA_LOCATION`.

**Key Internal Components:**
- `DefaultPermissionGrantPolicy` - OOB default permission grants
- `PermissionSettings` - Persistent permission storage
- `PermissionsState` - Per-package permission state tracking
- `OneTimePermissionUserManager` - Per-user one-time permission management

**Callback Pattern:** `PermissionCallback` interface with:
- `onGidsChanged()` - Kills app when GIDs change
- `onPermissionGranted()` - Triggers async settings write
- `onPermissionRevoked()` - Triggers synchronous settings write and app kill

---

### 6.3 BasePermission

**File:** `frameworks/base/services/core/java/com/android/server/pm/permission/BasePermission.java`

**Purpose:** Represents a single permission definition in the system.

**Permission Types:**
- `TYPE_NORMAL` (0) - Defined by a third-party package
- `TYPE_BUILTIN` (1) - Defined in the platform manifest
- `TYPE_DYNAMIC` (2) - Dynamically registered

**Protection Levels:**
- `PROTECTION_NORMAL` - Automatically granted at install
- `PROTECTION_DANGEROUS` - Requires runtime approval
- `PROTECTION_SIGNATURE` - Granted only to same-signing-certificate apps
- `PROTECTION_SIGNATURE_OR_SYSTEM` - Deprecated, signature or privileged app

**Key Fields:**
- `name` - Permission name string
- `sourcePackageName` - Package that defines this permission
- `protectionLevel` - Protection level bitmask
- `uid` - UID owning the permission definition
- `gids` - Additional Linux GIDs granted with this permission
- `perm` - Parsed permission from package manifest

---

### 6.4 OneTimePermissionUserManager

**File:** `frameworks/base/services/core/java/com/android/server/pm/permission/OneTimePermissionUserManager.java`

**Purpose:** Android 11 feature - manages one-time permission sessions per user. Tracks app inactivity and revokes permissions when apps become inactive.

**Inactivity Detection:**
- Three `OnUidImportanceListener` instances per tracked package:
  - `mStartTimerListener` at `importanceToResetTimer` threshold
  - `mSessionKillableListener` at `importanceToKeepSessionAlive` threshold
  - `mGoneListener` at `IMPORTANCE_CACHED` level
- Timer starts when importance exceeds reset threshold
- Timer resets when importance drops below reset threshold
- Session extends if app is between alive and reset thresholds when timer fires
- When app goes beyond IMPORTANCE_CACHED, a 5-second delayed revocation occurs (configurable via `DeviceConfig`)

**Session Timeout Flow:**
1. `startPackageOneTimeSession()` - Registers importance listeners
2. App becomes inactive beyond timeout -> `onPackageInactiveLocked()`
3. `PermissionControllerManager.notifyOneTimePermissionSessionTimeout()` is called
4. PermissionController revokes the one-time permissions

**Configurable Delay:** `DEFAULT_KILLED_DELAY_MILLIS = 5000` (5 seconds), configurable via `DeviceConfig.NAMESPACE_PERMISSIONS` key `one_time_permissions_killed_delay_millis`.

---

### 6.5 PermissionControllerService

**File:** `frameworks/base/core/java/android/permission/PermissionControllerService.java`

**Purpose:** `@SystemApi` abstract `Service` that must be implemented by the system permission controller app. Provides the interface between the framework and the user-space permission management UI.

**Required Overrides:**
- `onRevokeRuntimePermissions(Map<String, List<String>>, boolean doDryRun, int reason, String callerPackageName, Consumer<Map<String, List<String>>>)`
- `onGetAppPermissions(String packageName, Consumer<List<RuntimePermissionPresentationInfo>>)`
- `onRevokeRuntimePermission(String packageName, String permissionName, Runnable)`
- `onCountPermissionApps(List<String>, int flags, IntConsumer)`
- `onGetPermissionUsages(boolean countSystem, long numMillis, Consumer<List<RuntimePermissionUsageInfo>>)`
- `onGrantOrUpgradeDefaultRuntimePermissions(Runnable)`
- `onSetRuntimePermissionGrantStateByDeviceAdmin(String, String, String, int, Consumer<Boolean>)`

**Android 11 Additions:**
- `onOneTimePermissionSessionTimeout(String packageName)` - Called when one-time permission session expires
- `onUpdateUserSensitivePermissionFlags(int uid, Executor, Runnable)` - Update `FLAG_PERMISSION_USER_SENSITIVE_WHEN_*` flags
- `CAMERA_MIC_INDICATORS_NOT_PRESENT` - `@ChangeId @Disabled` compatibility flag for camera/mic indicators

**Backup/Restore:**
- `onGetRuntimePermissionsBackup()` / `onStageAndApplyRuntimePermissionsBackup()` - Permission state backup
- `onApplyStagedRuntimePermissionBackup()` - Delayed restore for not-yet-installed apps

**Security Enforcement in Binder Stub:**
- Each method verifies caller holds the appropriate permission (e.g., `REVOKE_RUNTIME_PERMISSIONS`, `GET_RUNTIME_PERMISSIONS`, `GRANT_RUNTIME_PERMISSIONS`)
- `enforceSomePermissionsGrantedToCaller()` checks any-of semantics
- Caller UID verification via `PackageManager.getPackageInfo()`

**Notable Bug:** In `setRuntimePermissionGrantStateByDeviceAdmin()`, both `PERMISSION_GRANT_STATE_GRANTED` and `PERMISSION_GRANT_STATE_DENIED` check for `GRANT_RUNTIME_PERMISSIONS`, but the granted case should check for `GRANT_RUNTIME_PERMISSIONS` instead (the code checks `GRANT_RUNTIME_PERMISSIONS` for the denied case twice due to a copy-paste in the condition `grantState == PERMISSION_GRANT_STATE_DENIED` appearing twice).

---

## 7. Network Security Configuration

### 7.1 NetworkSecurityPolicy

**File:** `frameworks/base/core/java/android/security/NetworkSecurityPolicy.java`

**Purpose:** Singleton policy governing cleartext network traffic for the process. Network stacks honor this to centrally control network security behavior.

**Public API:**
- `getInstance()` - Returns singleton (cacheable)
- `isCleartextTrafficPermitted()` - Global cleartext traffic check
- `isCleartextTrafficPermitted(String hostname)` - Per-host cleartext check

**Hidden APIs:**
- `setCleartextTrafficPermitted(boolean)` - Called early in app initialization
- `handleTrustStorageUpdate()` - Reloads trust store on certificate change
- `getApplicationConfigForPackage(Context, String)` - Static, returns `ApplicationConfig` for a package

---

### 7.2 NetworkSecurityConfig

**File:** `frameworks/base/core/java/android/security/net/config/NetworkSecurityConfig.java`

**Purpose:** `@hide` - Immutable configuration object representing network security settings for a domain or the default configuration.

**Configuration Properties:**
- `mCleartextTrafficPermitted` - Whether HTTP is allowed
- `mHstsEnforced` - Whether HSTS is enforced
- `mPins` - `PinSet` for certificate pinning
- `mCertificatesEntryRefs` - Ordered list of certificate sources

**Default Configuration Builder (`getDefaultBuilder`):**
- Apps targeting < API 28 (P): Cleartext traffic permitted (unless instant app)
- Apps targeting <= API 23 (M) and non-privileged: User certificate store trusted
- Apps targeting >= API 24 (N): Only system certificate store trusted by default
- Privileged apps on Android P+: Do not trust user certificate store

**Trust Anchor Resolution:** Certificate entry refs are sorted so that `overridesPins` entries come first. When the same certificate appears in multiple refs, the first occurrence (with overridesPins) wins.

---

### 7.3 XmlConfigSource

**File:** `frameworks/base/core/java/android/security/net/config/XmlConfigSource.java`

**Purpose:** `@hide` - Parses the `network_security_config.xml` resource file into `NetworkSecurityConfig` objects.

**XML Structure Parsed:**
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

**Certificate Sources:**
- `system` - `SystemCertificateSource` (singleton)
- `user` - `UserCertificateSource` (singleton)
- `wfa` - `WfaCertificateSource` (singleton, Wi-Fi Alliance)
- `@resourceId` - `ResourceCertificateSource` (custom bundled cert)

**Debug Overrides:** Only applied when `ApplicationInfo.FLAG_DEBUGGABLE` is set. Also checks for a `{resource_name}_debug.xml` companion resource.

**Validation:** Duplicate domains throw `ParserException`. Only one `base-config` and one `debug-overrides` section allowed. Pin sets only allowed in `domain-config`.

---

## 8. File Integrity Manager

**File:** `frameworks/base/core/java/android/security/FileIntegrityManager.java`

**Purpose:** `@SystemService(FILE_INTEGRITY_SERVICE)` - New in Android 11. Provides access to file integrity operations, specifically APK Verity support.

**Public API:**
- `isApkVeritySupported()` - Whether the device supports fs-verity for APK files
- `isAppSourceCertificateTrusted(X509Certificate)` - Whether a certificate can prove app install source. Requires `INSTALL_PACKAGES` or `REQUEST_INSTALL_PACKAGES`.

---

## 9. Cryptographic Utilities

### 9.1 Scrypt

**File:** `frameworks/base/core/java/android/security/Scrypt.java`

**Purpose:** `@hide` - JNI wrapper for the scrypt password hashing algorithm. Used internally for device encryption key derivation.

**API:** `scrypt(byte[] password, byte[] salt, int n, int r, int p, int outLen)` - Delegates to native `nativeScrypt()`.

---

## 10. Security Patterns and Architectural Observations

### 10.1 Defense in Depth

1. **Hardware-backed keys**: Keys in TEE/StrongBox are inaccessible to the application processor. `isInsideSecureHardware()` and `isStrongBoxBacked()` allow verification.
2. **Attestation chain**: Key attestation provides cryptographic proof of key properties, rooted at a CA trusted by relying parties.
3. **Biometric strength tiers**: Android 11 formalizes Class 1/2/3 biometric strength, preventing weak biometrics from being used with cryptographic operations.
4. **One-time permissions**: Permissions auto-revoke after app becomes inactive, reducing persistent permission accumulation.

### 10.2 Key Security Defaults

| Default | Value | Rationale |
|---------|-------|-----------|
| `mRandomizedEncryptionRequired` | `true` | Enforces IND-CPA (prevents deterministic encryption) |
| `mInvalidatedByBiometricEnrollment` | `true` | Keys invalidated when biometric enrollment changes |
| `mUserAuthenticationType` | `AUTH_BIOMETRIC_STRONG` | Only strong biometrics by default |
| Cleartext traffic (target >= API 28) | `false` | HTTPS by default |
| User cert trust (target >= API 24) | Not trusted | Only system certs trusted |

### 10.3 Hidden/System API Surface

The security subsystem has a significant hidden API surface:
- **AndroidKeyStoreProvider** is entirely `@hide @SystemApi`
- **Cross-UID KeyStore access** via `getKeyStoreForUid()`
- **UniqueId attestation** for device identification
- **Critical-to-device-encryption** flag for FBE keys
- **PermissionManager** telephony/carrier permission grants
- **BiometricPrompt** internal methods requiring `USE_BIOMETRIC_INTERNAL`

### 10.4 Potential Issues and Observations

1. **PermissionControllerService Binder stub bug:** In `setRuntimePermissionGrantStateByDeviceAdmin()`, the GRANT case checks `grantState == PERMISSION_GRANT_STATE_DENIED` instead of `PERMISSION_GRANT_STATE_GRANTED`, meaning the `GRANT_RUNTIME_PERMISSIONS` check is only enforced for DENIED, not GRANTED. However, the `ADJUST_RUNTIME_PERMISSIONS_POLICY` check still applies to all cases.

2. **PermissionQuery pid exclusion:** The `PermissionQuery.equals()` method intentionally ignores pid, relying solely on uid for security decisions. This is documented as correct (uid determines security identity), but the pid is still passed to `checkPermissionUncached()` for ActivityManager checks.

3. **3DES conditional support:** `AndroidKeyStoreProvider` conditionally registers DESede support based on `ro.hardware.keystore_desede`, meaning the algorithm availability varies by device. The algorithm is deprecated in `KeyProperties`.

4. **BiometricPrompt fallback behavior:** When `CryptoObject` is provided with BIOMETRIC_STRONG, the default authenticator is set correctly. However, the non-crypto `authenticate()` method does not restrict authenticator types, allowing BIOMETRIC_WEAK by default.

5. **NetworkSecurityConfig cleartext default:** The `DEFAULT_CLEARTEXT_TRAFFIC_PERMITTED` constant is `true`, but `getDefaultBuilder()` overrides this to `false` for apps targeting API 28+. The constant itself could be misleading.

6. **One-time permission delayed revocation:** The 5-second kill delay (`DEFAULT_KILLED_DELAY_MILLIS`) before revoking permissions on app death is a balance between security (immediate revocation) and usability (allowing quick restarts). This is remotely configurable via DeviceConfig.

---

## 11. File Index

| File | Category |
|------|----------|
| `frameworks/base/keystore/java/android/security/keystore/KeyGenParameterSpec.java` | Keystore - Key Generation |
| `frameworks/base/keystore/java/android/security/keystore/KeyProperties.java` | Keystore - Constants |
| `frameworks/base/keystore/java/android/security/keystore/KeyProtection.java` | Keystore - Key Import |
| `frameworks/base/keystore/java/android/security/keystore/KeyInfo.java` | Keystore - Key Metadata |
| `frameworks/base/keystore/java/android/security/keystore/AndroidKeyStoreProvider.java` | Keystore - JCA Provider |
| `frameworks/base/keystore/java/android/security/keystore/AttestationUtils.java` | Keystore - Device Attestation |
| `frameworks/base/keystore/java/android/security/KeyChain.java` | KeyChain API |
| `frameworks/base/core/java/android/hardware/biometrics/BiometricPrompt.java` | Biometric Authentication |
| `frameworks/base/core/java/android/hardware/biometrics/BiometricManager.java` | Biometric Capability Query |
| `frameworks/base/core/java/android/security/ConfirmationPrompt.java` | Protected Confirmation |
| `frameworks/base/core/java/android/security/FileIntegrityManager.java` | File Integrity |
| `frameworks/base/core/java/android/security/NetworkSecurityPolicy.java` | Network Security |
| `frameworks/base/core/java/android/security/net/config/NetworkSecurityConfig.java` | Network Security Config |
| `frameworks/base/core/java/android/security/net/config/XmlConfigSource.java` | Network Security XML Parser |
| `frameworks/base/core/java/android/security/Scrypt.java` | Crypto - Password Hashing |
| `frameworks/base/core/java/android/permission/PermissionManager.java` | Permission Management |
| `frameworks/base/core/java/android/permission/PermissionControllerService.java` | Permission Controller Interface |
| `frameworks/base/services/core/java/com/android/server/pm/permission/PermissionManagerService.java` | Permission Server Implementation |
| `frameworks/base/services/core/java/com/android/server/pm/permission/BasePermission.java` | Permission Definition |
| `frameworks/base/services/core/java/com/android/server/pm/permission/OneTimePermissionUserManager.java` | One-Time Permission Management |
| `frameworks/base/services/core/java/com/android/server/pm/permission/PermissionSettings.java` | Permission Storage |
| `frameworks/base/services/core/java/com/android/server/pm/permission/PermissionsState.java` | Per-Package Permission State |
| `frameworks/base/services/core/java/com/android/server/pm/permission/DefaultPermissionGrantPolicy.java` | Default Permission Grants |
