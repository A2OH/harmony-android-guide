# Code Review Report: Package Management and App Installation Subsystem

## Android 11 (API Level 30) AOSP Source Review

**Review Date:** 2026-03-10
**Subsystem:** `frameworks/base/core/java/android/content/pm/`
**Scope:** Package queries, component info, intent resolution, APK installation flow, feature flags, package visibility, split APKs, app bundles

---

## 1. Executive Summary

The `android.content.pm` package forms the backbone of Android's package management system. It spans over 8,200 lines in `PackageManager.java` alone and over 2,700 lines in `PackageInstaller.java`, providing APIs for querying installed packages, resolving intents, managing components, and performing installation/uninstallation operations. Android 11 introduces significant behavioral changes, most notably the **package visibility** restrictions (`FILTER_APPLICATION_QUERY`) and new foreground service type requirements for camera and microphone. The code demonstrates careful evolution over many API levels, maintaining backward compatibility while layering new security restrictions. However, the resulting API surface is very large and carries considerable legacy complexity.

---

## 2. Architectural Overview

### 2.1 Class Hierarchy

```
PackageItemInfo
  +-- ComponentInfo
  |     +-- ActivityInfo      (activities and receivers)
  |     +-- ServiceInfo       (services)
  |     +-- ProviderInfo      (content providers)
  +-- ApplicationInfo        (application-level metadata)
  +-- PermissionInfo
  +-- InstrumentationInfo

PackageInfo                   (top-level container for all manifest data)
ResolveInfo                   (result of intent resolution)

PackageManager               (abstract facade for all queries)
PackageInstaller             (session-based installation API)
  +-- Session                (active write session)
  +-- SessionParams          (session configuration)
  +-- SessionInfo            (read-only session status)
  +-- SessionCallback        (lifecycle observer)
```

### 2.2 Key Files Reviewed

| File | Lines | Purpose |
|------|-------|---------|
| `PackageManager.java` | ~8,248 | Abstract facade for all package queries, permissions, features |
| `PackageInstaller.java` | ~2,780 | Session-based install/uninstall API |
| `PackageInfo.java` | ~400 | Container for parsed AndroidManifest.xml data |
| `ApplicationInfo.java` | ~1,800+ | Application-level flags and metadata |
| `ActivityInfo.java` | ~800+ | Activity/receiver configuration |
| `ServiceInfo.java` | ~200+ | Service configuration and foreground types |
| `ProviderInfo.java` | ~200+ | ContentProvider configuration |
| `ResolveInfo.java` | ~300+ | Intent resolution results |
| `parsing/ParsingPackageUtils.java` | ~4,000+ | Manifest XML parsing engine |

---

## 3. Package Queries and Information Retrieval

### 3.1 Flag System Design

The `PackageManager` uses a sophisticated integer flag system partitioned into two semantic categories (lines 137-145):

- **`GET_` flags**: Request additional data that may have been elided for efficiency (e.g., `GET_ACTIVITIES`, `GET_PERMISSIONS`, `GET_META_DATA`, `GET_SIGNING_CERTIFICATES`)
- **`MATCH_` flags**: Include components or packages that would otherwise be omitted from results (e.g., `MATCH_DISABLED_COMPONENTS`, `MATCH_UNINSTALLED_PACKAGES`, `MATCH_SYSTEM_ONLY`)

**Observation:** Multiple flag annotation types exist for different query contexts -- `@PackageInfoFlags`, `@ApplicationInfoFlags`, `@ComponentInfoFlags`, `@ResolveInfoFlags` -- providing compile-time safety on which flags apply where. This is well-designed.

**Concern - Flag Value Collision:**
```java
GET_SIGNATURES          = 0x00000040;  // deprecated
GET_RESOLVED_FILTER     = 0x00000040;  // same value
```
`GET_SIGNATURES` and `GET_RESOLVED_FILTER` share the hex value `0x00000040`. While they are used in non-overlapping contexts (`PackageInfoFlags` vs. `ResolveInfoFlags`), this is a maintainability concern. The `@IntDef` annotations serve as documentation but cannot prevent a developer from passing one where the other is expected.

### 3.2 Core Query Methods

The primary query surface on `PackageManager`:

| Method | Returns | Scope |
|--------|---------|-------|
| `getPackageInfo(String, int)` | `PackageInfo` | Single package by name |
| `getPackageInfo(VersionedPackage, int)` | `PackageInfo` | Package by name+version |
| `getInstalledPackages(int)` | `List<PackageInfo>` | All installed for current user |
| `getInstalledApplications(int)` | `List<ApplicationInfo>` | All app info for current user |
| `resolveActivity(Intent, int)` | `ResolveInfo` | Best matching activity |
| `queryIntentActivities(Intent, int)` | `List<ResolveInfo>` | All matching activities |
| `queryBroadcastReceivers(Intent, int)` | `List<ResolveInfo>` | All matching receivers |
| `queryIntentServices(Intent, int)` | `List<ResolveInfo>` | All matching services |
| `resolveContentProvider(String, int)` | `ProviderInfo` | Provider by authority |

**Recommendation for app developers:** Always pass the narrowest set of flags needed. Requesting `GET_ACTIVITIES | GET_SERVICES | GET_PROVIDERS | GET_RECEIVERS | GET_PERMISSIONS | GET_META_DATA` unnecessarily creates large Parcelable transactions across Binder and can cause `TransactionTooLargeException` in builds with many components.

### 3.3 Deprecated Signatures API

```java
@Deprecated
public static final int GET_SIGNATURES = 0x00000040;
```
The field `PackageInfo.signatures` is deprecated in favor of `PackageInfo.signingInfo` (populated via `GET_SIGNING_CERTIFICATES = 0x08000000`). The new API supports signing certificate rotation. Developers should use `hasSigningCertificate()` for verification instead of direct certificate comparison, as the old API does not handle rotation correctly.

---

## 4. Package Visibility (Android 11 Feature)

### 4.1 Core Change

Android 11 introduces a fundamental behavioral change controlled by:

```java
@ChangeId
@EnabledAfter(targetSdkVersion = Build.VERSION_CODES.Q)
public static final long FILTER_APPLICATION_QUERY = 135549675L;
```

**File:** `PackageManager.java`, line 3716

Apps targeting Android R (API 30) and above receive **filtered results** from all PackageManager query methods. The class Javadoc at line 96-99 warns:

> "If your app targets Android 11 (API level 30) or higher, the methods in this class each return a filtered list of apps."

### 4.2 Visibility Mechanisms

Apps can declare visibility needs through the `<queries>` manifest tag (referenced at line 3710). Without explicit declarations, apps can only see:
- Their own package
- Packages that are currently interacting with them
- System packages

The `MATCH_UNINSTALLED_PACKAGES` flag now additionally requires `android.permission.QUERY_ALL_PACKAGES` to see uninstalled packages (line 394-395).

### 4.3 Feature Flag

```java
public static final String FEATURE_APP_ENUMERATION = "android.software.app_enumeration";
public static final boolean APP_ENUMERATION_ENABLED_BY_DEFAULT = true;
```

The `FEATURE_APP_ENUMERATION` (line 3130) indicates the device supports app enumeration filtering. It is enabled by default (line 3133), making this a universal behavioral change on Android 11.

### 4.4 Force Queryable Override

In `SessionParams`:
```java
public boolean forceQueryableOverride;
```
And the corresponding method:
```java
public void setForceQueryable() {
    this.forceQueryableOverride = true;
}
```
This hidden API allows system components to make installed packages universally visible, bypassing the `<queries>` filtering.

### 4.5 Implications for Developers

- Apps that enumerate installed packages (launchers, device management, accessibility) must declare `<queries>` in their manifest or hold `QUERY_ALL_PACKAGES`
- `getInstalledPackages()`, `getInstalledApplications()`, `queryIntentActivities()`, and related methods all return filtered results
- **Risk:** Apps that do not adapt may silently receive incomplete results, leading to degraded functionality without visible errors

---

## 5. APK Installation Flow

### 5.1 Session-Based Architecture

`PackageInstaller` uses a session-based model introduced in Android L (API 21), replacing the legacy `installPackage()` API. The flow:

1. **Create Session:** `PackageInstaller.createSession(SessionParams)` returns a unique session ID
2. **Open Session:** `PackageInstaller.openSession(sessionId)` returns a `Session` object
3. **Stream APKs:** `Session.openWrite(name, offset, length)` returns an `OutputStream`
4. **Fsync:** `Session.fsync(out)` ensures data persistence
5. **Commit:** `Session.commit(IntentSender)` seals the session and initiates installation
6. **Result:** Status delivered via `IntentSender` with `EXTRA_STATUS`

Sessions are persistent across reboots and the system auto-destroys abandoned sessions after approximately one day.

### 5.2 Session Modes

```java
public static final int MODE_FULL_INSTALL = 1;     // Replace all APKs
public static final int MODE_INHERIT_EXISTING = 2;  // Add/update split APKs
```

`MODE_INHERIT_EXISTING` is critical for split APK delivery, allowing incremental installation of new splits without replacing the entire package.

### 5.3 Installation Status Codes

The public status codes provide clear semantic feedback:

| Status | Value | Meaning |
|--------|-------|---------|
| `STATUS_PENDING_USER_ACTION` | -1 | User intervention required |
| `STATUS_PENDING_STREAMING` | -2 | Streaming data pending (hidden) |
| `STATUS_SUCCESS` | 0 | Installation succeeded |
| `STATUS_FAILURE` | 1 | Generic failure |
| `STATUS_FAILURE_BLOCKED` | 2 | Blocked by policy/verifier |
| `STATUS_FAILURE_ABORTED` | 3 | User declined or session abandoned |
| `STATUS_FAILURE_INVALID` | 4 | Malformed/corrupt/mismatched APKs |
| `STATUS_FAILURE_CONFLICT` | 5 | Conflicts with existing package |
| `STATUS_FAILURE_STORAGE` | 6 | Insufficient storage |
| `STATUS_FAILURE_INCOMPATIBLE` | 7 | Device incompatibility |

**Additionally**, there are over 30 legacy `INSTALL_FAILED_*` and `INSTALL_PARSE_FAILED_*` codes (values -1 through -115, and -100 through -113) exposed through `EXTRA_LEGACY_STATUS`. These are `@SystemApi` hidden codes providing granular failure reasons.

### 5.4 Install Flags (Hidden/System API)

Key installation flags (all `@hide`):

| Flag | Value | Purpose |
|------|-------|---------|
| `INSTALL_REPLACE_EXISTING` | 0x02 | Allow replacement of existing package |
| `INSTALL_ALLOW_TEST` | 0x04 | Allow test-only packages |
| `INSTALL_FROM_ADB` | 0x20 | ADB-initiated install |
| `INSTALL_ALL_USERS` | 0x40 | Install for all users |
| `INSTALL_GRANT_RUNTIME_PERMISSIONS` | 0x100 | Auto-grant runtime permissions |
| `INSTALL_INSTANT_APP` | 0x800 | Install as ephemeral/instant app |
| `INSTALL_DONT_KILL_APP` | 0x1000 | Do not kill app when adding splits |
| `INSTALL_APEX` | 0x20000 | APEX package install |
| `INSTALL_ENABLE_ROLLBACK` | 0x40000 | Enable rollback support |
| `INSTALL_DISABLE_VERIFICATION` | 0x80000 | Skip package verification |
| `INSTALL_STAGED` | 0x200000 | Staged install (apply at next reboot) |
| `INSTALL_DRY_RUN` | 0x800000 | Verify only, do not install |

**Concern:** `INSTALL_DISABLE_VERIFICATION` (0x80000) bypasses package verification. While hidden, access to this flag through reflection or partner agreements could create security vulnerabilities.

### 5.5 Data Loader / Incremental Installation

Android 11 introduces three data loader types for streaming installation:

```java
DATA_LOADER_TYPE_NONE = DataLoaderType.NONE;           // Traditional
DATA_LOADER_TYPE_STREAMING = DataLoaderType.STREAMING;  // Network streaming
DATA_LOADER_TYPE_INCREMENTAL = DataLoaderType.INCREMENTAL; // Incremental FS
```

The Incremental FileSystem support (`FEATURE_INCREMENTAL_DELIVERY`) enables apps to be used while still downloading, gated by the system feature `android.software.incremental_delivery`. The `Session.addFile()` method supports adding files via data loaders with metadata and signature parameters, requiring `USE_INSTALLER_V2` permission.

### 5.6 Rollback Support

```java
@RollbackDataPolicy int RESTORE = 0;  // Back up data, restore on rollback
@RollbackDataPolicy int WIPE = 1;     // No backup, wipe on rollback
@RollbackDataPolicy int RETAIN = 2;   // No backup, no restore (TODO: Not implemented)
```

**Finding:** The `RETAIN` policy has a TODO comment indicating it is "Not implemented yet" (line 767-768). This is a gap in the rollback framework that developers relying on this behavior should be aware of.

### 5.7 Stream Implementation Detail

The `Session.openWrite()` method uses two different I/O mechanisms controlled by a system property:

```java
public static final boolean ENABLE_REVOCABLE_FD =
        SystemProperties.getBoolean("fw.revocable_fd", false);
```

When enabled, it uses `ParcelFileDescriptor.AutoCloseOutputStream`; otherwise, `FileBridge.FileBridgeOutputStream`. The `FileBridge` approach provides server-side control over the file descriptor lifecycle, enabling the system to revoke access when the session is sealed. The revocable FD path is disabled by default, suggesting the FileBridge approach is the production path.

---

## 6. Split APK and Multi-Package Support

### 6.1 Split APK Model

`PackageInfo.splitNames` (line 39) contains the names of installed split APKs. Split constraints documented in `PackageInstaller` class Javadoc:

- All APKs must share identical package name, version code, and signing certificates
- All split names must be unique
- A single base APK (with null split name) must always be present

### 6.2 Session Split Operations

```java
Session.removeSplit(String splitName)  // Remove a split before adding new ones
```

Split removals occur **prior to** adding new APKs. The API documentation explicitly notes that upgrading a feature split does not require removing it first.

### 6.3 Isolated Split Loading

`ApplicationInfo.PRIVATE_FLAG_ISOLATED_SPLIT_LOADING` (bit 15):

> "When set, the application will only have its splits loaded if they are required to load a component. Splits can be loaded on demand using the `Context.createContextForSplit(String)` API."

This supports on-demand delivery of feature modules (Play Feature Delivery / Dynamic Delivery), where splits are loaded lazily rather than all at once at startup.

### 6.4 Multi-Package Sessions

The `SessionParams.setMultiPackage()` method creates a session that:
- Contains no APKs itself
- References child sessions via `Session.addChildSessionId(int)`
- Commits all child sessions atomically
- If any child fails, all fail (atomic rollback)

This enables atomic installation of interdependent packages, essential for APEX and mainline module updates.

### 6.5 Staged Sessions

```java
SessionParams.setStaged()  // Requires INSTALL_PACKAGES permission
```

Staged sessions are scheduled for installation at next reboot. `SessionInfo` exposes staged session state:
- `isStagedSessionApplied` -- successfully applied
- `isStagedSessionReady` -- ready for activation
- `isStagedSessionFailed` -- failed with error code

Error codes: `STAGED_SESSION_NO_ERROR`, `STAGED_SESSION_VERIFICATION_FAILED`, `STAGED_SESSION_ACTIVATION_FAILED`, `STAGED_SESSION_UNKNOWN`.

**Note:** `getActiveStagedSession()` is deprecated in favor of `getActiveStagedSessions()`, reflecting that multiple staged sessions can be active simultaneously.

---

## 7. Component Information Classes

### 7.1 ActivityInfo

Key configuration fields:

- **Launch Modes:** `LAUNCH_MULTIPLE`, `LAUNCH_SINGLE_TOP`, `LAUNCH_SINGLE_TASK`, `LAUNCH_SINGLE_INSTANCE`
- **Document Launch Modes:** `DOCUMENT_LAUNCH_NONE`, `DOCUMENT_LAUNCH_INTO_EXISTING`, `DOCUMENT_LAUNCH_ALWAYS`, `DOCUMENT_LAUNCH_NEVER`
- **Resize Modes:** 8 values from `RESIZE_MODE_UNRESIZEABLE` (0) through `RESIZE_MODE_FORCE_RESIZABLE_PRESERVE_ORIENTATION` (7)
- **Color Modes:** `COLOR_MODE_DEFAULT`, `COLOR_MODE_WIDE_COLOR_GAMUT`, `COLOR_MODE_HDR`
- **Persistable Modes:** `PERSIST_ROOT_ONLY`, `PERSIST_NEVER`, `PERSIST_ACROSS_REBOOTS`

Notable Android 11 addition: `supportsSizeChanges` (line 252), indicating the activity handles display size changes gracefully.

### 7.2 ServiceInfo

Android 11 adds two new foreground service types requiring explicit declaration:

```java
FOREGROUND_SERVICE_TYPE_CAMERA = 1 << 6;      // Camera access
FOREGROUND_SERVICE_TYPE_MICROPHONE = 1 << 7;   // Microphone access
```

The Javadoc explicitly warns (lines 153-157, 164-169) that for apps targeting Android R and above, foreground services **cannot** access camera or microphone without declaring the appropriate type in both the manifest and the `startForeground()` call.

Service flags:
- `FLAG_STOP_WITH_TASK` -- Auto-stop when task removed
- `FLAG_ISOLATED_PROCESS` -- Run in isolated process
- `FLAG_EXTERNAL_SERVICE` -- Can run in caller's package
- `FLAG_USE_APP_ZYGOTE` -- Spawn from Application Zygote for faster start

### 7.3 ProviderInfo

`ProviderInfo` models content provider security:
- `readPermission` / `writePermission` -- Separate read/write permissions
- `grantUriPermissions` -- Whether URI-level grants are allowed
- `forceUriPermissions` -- Always apply URI permission grants
- `uriPermissionPatterns` -- Restrict which URIs can be granted
- `pathPermissions` -- Path-specific permission checks

The `multiprocess` flag (line 78) controls whether multiple instances can run in different processes. The `initOrder` field controls initialization order for single-process providers.

### 7.4 ResolveInfo

`ResolveInfo` is the result container for intent resolution. Exactly one of `activityInfo`, `serviceInfo`, or `providerInfo` will be non-null. Notable fields:

- `isInstantAppAvailable` -- Whether an instant app is available for this intent
- `auxiliaryInfo` (hidden) -- Auxiliary resolution data for instant apps or un-installed splits
- `filter` -- The `IntentFilter` that was matched (requires `GET_RESOLVED_FILTER`)
- `priority` / `preferredOrder` / `match` -- Resolution ranking

---

## 8. ApplicationInfo Flags Analysis

### 8.1 Public Flags (32 bits, fully consumed)

The `flags` field uses all 32 bits, from `FLAG_SYSTEM` (bit 0) through `FLAG_MULTIARCH` (bit 31). Notable entries:

| Flag | Bit | Security/Safety Relevance |
|------|-----|--------------------------|
| `FLAG_DEBUGGABLE` | 1 | Allows debugging; security-sensitive |
| `FLAG_TEST_ONLY` | 8 | Blocks production installation without `INSTALL_ALLOW_TEST` |
| `FLAG_USES_CLEARTEXT_TRAFFIC` | 27 | Controls HTTP vs HTTPS enforcement |
| `FLAG_EXTRACT_NATIVE_LIBS` | 28 | Controls native library extraction from APK |
| `FLAG_SUSPENDED` | 30 | App is suspended (not launchable) |

### 8.2 Private Flags (Hidden API)

`privateFlags` provides additional system-internal metadata with over 25 bit positions:

| Flag | Bit | Purpose |
|------|-----|---------|
| `PRIVATE_FLAG_HIDDEN` | 0 | Hidden via restrictions |
| `PRIVATE_FLAG_PRIVILEGED` | 3 | Can hold privileged permissions |
| `PRIVATE_FLAG_INSTANT` | 7 | Installed as instant app |
| `PRIVATE_FLAG_ISOLATED_SPLIT_LOADING` | 15 | On-demand split loading |
| `PRIVATE_FLAG_SIGNED_WITH_PLATFORM_KEY` | 20 | Platform-signed |
| `PRIVATE_FLAG_PROFILEABLE_BY_SHELL` | 23 | Shell can profile this app |

**Concern - Partition Flags:** Five separate flags track which system partition an app originates from: `PRIVATE_FLAG_OEM` (17), `PRIVATE_FLAG_VENDOR` (18), `PRIVATE_FLAG_PRODUCT` (19), `PRIVATE_FLAG_SYSTEM_EXT` (21), plus `FLAG_SYSTEM` in public flags. This is a flat enumeration when a structured partition identifier might be cleaner, but the bit-flag approach is consistent with the rest of the codebase.

---

## 9. Permission Management

### 9.1 Runtime Permission Grants

The `PackageManager` provides a comprehensive permission management API:

```java
grantRuntimePermission(String packageName, String permName, UserHandle user)
revokeRuntimePermission(String packageName, String permName, UserHandle user)
getPermissionFlags(String permName, String packageName, UserHandle user)
updatePermissionFlags(String permName, String packageName, int flagMask, int flagValues, UserHandle user)
```

All are `@SystemApi` requiring `GRANT_RUNTIME_PERMISSIONS` or `REVOKE_RUNTIME_PERMISSIONS`.

### 9.2 Permission Flags

16 permission flag constants (bits 0-15) control fine-grained permission state:

- `FLAG_PERMISSION_USER_SET` / `FLAG_PERMISSION_USER_FIXED` -- User has made a choice
- `FLAG_PERMISSION_POLICY_FIXED` / `FLAG_PERMISSION_SYSTEM_FIXED` -- System-locked
- `FLAG_PERMISSION_ONE_TIME` -- One-time grant (Android 11)
- `FLAG_PERMISSION_AUTO_REVOKED` -- Auto-revoked for unused apps (Android 11)
- `FLAG_PERMISSION_REVOKED_COMPAT` -- Revoked on legacy app upgrade

### 9.3 Restricted Permissions Whitelisting

The three-tier whitelist system (system, upgrade, installer) provides:
- System whitelist: Controlled by pre-installed permission holders
- Upgrade whitelist: Applied during OS version transitions where permissions become restricted
- Installer whitelist: Managed by the package installer

`SessionParams.setWhitelistedRestrictedPermissions()` controls which restricted permissions are whitelisted at install time. The default is `RESTRICTED_PERMISSIONS_ALL`.

### 9.4 Auto-Revoke (Android 11)

```java
public void setAutoRevokePermissionsMode(boolean shouldAutoRevoke) {
    autoRevokePermissionsMode = shouldAutoRevoke ? MODE_ALLOWED : MODE_IGNORED;
}
```

**Typo in Javadoc:** Line 1742 has "extended periodd" (double 'd'). Minor documentation issue.

The installer can opt out of auto-revoke for packages where re-requesting permissions would cause breakage. User overrides via Settings take precedence.

---

## 10. Feature Flags and System Features

### 10.1 Feature Query API

```java
hasSystemFeature(String featureName)
hasSystemFeature(String featureName, int version)
getSystemAvailableFeatures()
```

Features are versioned and backward-compatible: `hasSystemFeature(name, version)` returns true if the available version is >= requested version.

### 10.2 Notable Android 11 Features

| Feature Constant | String Value |
|-----------------|-------------|
| `FEATURE_CONTROLS` | `android.software.controls` |
| `FEATURE_INCREMENTAL_DELIVERY` | `android.software.incremental_delivery` |
| `FEATURE_APP_ENUMERATION` | `android.software.app_enumeration` |
| `FEATURE_REBOOT_ESCROW` | `android.hardware.reboot_escrow` |

---

## 11. Manifest Parsing (parsing/ Package)

### 11.1 Architecture

Android 11 introduces a refactored manifest parsing system under `android.content.pm.parsing`:

- `ParsingPackageUtils` -- Main parsing engine, processes AndroidManifest.xml
- `ParsingPackage` / `ParsingPackageImpl` -- Mutable parsed package representation
- `ParsingPackageRead` -- Read-only interface
- `ApkLiteParseUtils` -- Lightweight APK parsing (for install decisions)
- `PackageInfoWithoutStateUtils` -- Convert parsed packages to `PackageInfo` without runtime state

### 11.2 Component Parsing

Individual component parsers exist for each manifest element:
- `ParsedActivityUtils` -- `<activity>` and `<receiver>`
- `ParsedServiceUtils` -- `<service>`
- `ParsedProviderUtils` -- `<provider>`
- `ParsedPermissionUtils` -- `<permission>` and `<permission-group>`
- `ParsedInstrumentationUtils` -- `<instrumentation>`
- `ParsedIntentInfoUtils` -- `<intent-filter>`
- `ParsedAttributionUtils` -- `<attribution>` (new in Android 11)

### 11.3 Error Handling

The parsing system uses a `ParseResult<T>` pattern (similar to a result/error monad) with `ParseInput` providing factory methods. Errors reference the `INSTALL_PARSE_FAILED_*` constants from `PackageManager`. The `DeferredError` mechanism allows non-fatal parse errors to be collected and reported later.

---

## 12. Security Considerations

### 12.1 Package Verification

The verification system is triggered during installation with extras:
- `EXTRA_VERIFICATION_ID` -- Unique ID for the pending verification
- `EXTRA_VERIFICATION_PACKAGE_NAME` -- Package being verified
- `EXTRA_VERIFICATION_ROOT_HASH` -- Merkle tree root hash (SHA-256, 4096-byte blocks)
- `EXTRA_VERIFICATION_INSTALL_FLAGS` -- Installation flags for context

Verification results are `VERIFICATION_ALLOW` or `VERIFICATION_REJECT`, with timeout fallback (`INSTALL_FAILED_VERIFICATION_TIMEOUT`).

### 12.2 Signing Certificate Verification

```java
hasSigningCertificate(String packageName, byte[] certificate, @CertificateInputType int type)
hasSigningCertificate(int uid, byte[] certificate, @CertificateInputType int type)
```

Supports both raw X.509 (`CERT_INPUT_RAW_X509`) and SHA256 hash (`CERT_INPUT_SHA256`) inputs. This API handles signing certificate rotation correctly, unlike the deprecated `GET_SIGNATURES` approach.

### 12.3 Unknown Sources Control

```java
public abstract boolean canRequestPackageInstalls();
```

Requires `REQUEST_INSTALL_PACKAGES` permission and targeting Android O+. Returns whether the calling package is trusted by the user to install packages. The user can manage this per-app in Settings.

### 12.4 Harmful App Warnings

```java
setHarmfulAppWarning(String packageName, CharSequence warning)  // @SystemApi
getHarmfulAppWarning(String packageName)                         // @SystemApi
```

System-level API for marking apps as potentially harmful, displayed as warnings to users.

---

## 13. Identified Issues and Recommendations

### 13.1 API Surface Complexity (Moderate)

`PackageManager` at 8,248 lines is an exceptionally large class. It mixes:
- Package queries (lines 3757-4913)
- Permission management (lines 4261-4700)
- Component state (lines 6809-6851)
- Feature queries (lines 5130-5165)
- Intent resolution (lines 5166-5400)
- Preferred activities (deprecated, lines 6596-6798)
- Package verification (lines 3135-3270)
- Various system package name getters (lines 7840-7945)

**Recommendation:** While restructuring this class is impractical due to API stability requirements, documentation should guide developers to appropriate subsets. Consider whether new feature areas (e.g., modules, incremental install) should use dedicated manager classes.

### 13.2 Deprecated API Accumulation (Low)

Multiple deprecated APIs remain in the class:
- `GET_SIGNATURES` (replaced by `GET_SIGNING_CERTIFICATES`)
- `GET_DISABLED_COMPONENTS` (replaced by `MATCH_DISABLED_COMPONENTS`)
- `GET_UNINSTALLED_PACKAGES` (replaced by `MATCH_UNINSTALLED_PACKAGES`)
- `addPackageToPreferred()`, `removePackageFromPreferred()`, `getPreferredPackages()`, `addPreferredActivity()`, `clearPackagePreferredActivities()` -- all deprecated in favor of `RoleManager`
- `versionCode` field on PackageInfo (use `getLongVersionCode()`)

These deprecated methods correctly delegate or throw, and the `@Deprecated` annotations are present, but the volume adds cognitive load.

### 13.3 RollbackDataPolicy.RETAIN Not Implemented (Low-Medium)

```java
int RETAIN = 2;  // TODO: Not implemented yet.
```

The `RETAIN` rollback data policy is defined but marked as not implemented. Any code referencing this value may not behave as expected.

### 13.4 UnsupportedAppUsage Annotations (Low)

Many hidden fields carry `@UnsupportedAppUsage` annotations with `maxTargetSdk` restrictions, particularly in `SessionParams` and `SessionInfo`. These reflect the greylist/blacklist enforcement introduced in Android P. Apps using reflection to access these fields will fail on newer SDKs. This is by design but worth noting for compatibility analysis.

### 13.5 Package Visibility Behavioral Risk (High for App Developers)

The silent filtering of query results (`FILTER_APPLICATION_QUERY`) is the most impactful behavioral change. Unlike permission denials that throw exceptions, filtered queries return **empty or incomplete results without error**. Apps that do not declare `<queries>` or `QUERY_ALL_PACKAGES` will silently lose visibility into other packages.

**Recommendation for developers:**
1. Always declare specific `<queries>` elements for packages you need to interact with
2. Use `QUERY_ALL_PACKAGES` only when truly needed (e.g., launchers, security apps)
3. Test with package visibility restrictions enabled during development

### 13.6 Foreground Service Type Requirements (Medium for App Developers)

Camera and microphone access from foreground services now requires explicit type declaration both in the manifest (`foregroundServiceType`) and at runtime (`startForeground(id, notification, type)`). Failure to declare results in denied access rather than a crash -- a silent failure mode that may be difficult to debug.

---

## 14. Summary of Android 11 Changes in This Subsystem

| Change | Impact | Risk |
|--------|--------|------|
| Package visibility filtering (`<queries>`) | All apps querying other packages | High -- silent data loss |
| Foreground service type: camera/microphone | Services using camera/mic | Medium -- silent access denial |
| Auto-revoke permissions for unused apps | All apps with runtime permissions | Medium -- permissions disappear |
| Incremental delivery / streaming install | App stores, installers | Low -- opt-in feature |
| One-time permission grants | Privacy-sensitive apps | Low -- transparent to most apps |
| APEX/staged session improvements | System components | Low -- system-internal |
| `<attribution>` tag in manifest | All apps | Low -- optional metadata |

---

## 15. Key Source File Paths

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

*Report generated from Android 11 AOSP source code review.*
