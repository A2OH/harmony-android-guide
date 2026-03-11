# OpenHarmony 4.1 Built-in Applications 代码审查

## Table of Contents

- [1. Executive Summary](#1-executive-summary)
- [2. Scope and Methodology](#2-scope-and-methodology)
- [3. Application Inventory](#3-application-inventory)
- [4. Critical Security Findings](#4-critical-security-findings)
  - [4.1 Hardcoded Cryptographic Keys in Sample App](#41-hardcoded-cryptographic-keys-in-sample-app)
  - [4.2 WiFi Pre-Shared Key Exposed via QR Code](#42-wifi-pre-shared-key-exposed-via-qr-code)
  - [4.3 Token Values Logged to Console](#43-token-values-logged-to-console)
- [5. Architecture and Code Quality Findings](#5-architecture-and-code-quality-findings)
  - [5.1 Pervasive Use of globalThis as Service Locator](#51-pervasive-use-of-globalthis-as-service-locator)
  - [5.2 Deeply Nested Relative Import Paths](#52-deeply-nested-relative-import-paths)
  - [5.3 Widespread Use of ts-ignore and ts-nocheck](#53-widespread-use-of-ts-ignore-and-ts-nocheck)
  - [5.4 Excessive Use of the any Type](#54-excessive-use-of-the-any-type)
  - [5.5 Deprecated API Usage Across Apps](#55-deprecated-api-usage-across-apps)
- [6. Error Handling Findings](#6-error-handling-findings)
  - [6.1 Silently Swallowed Errors in SystemUI](#61-silently-swallowed-errors-in-systemui)
  - [6.2 Empty Catch Blocks in Sample Apps](#62-empty-catch-blocks-in-sample-apps)
  - [6.3 Verbose Error Serialization Leaking Stack Traces](#63-verbose-error-serialization-leaking-stack-traces)
- [7. Permission Usage Findings](#7-permission-usage-findings)
  - [7.1 SystemUI Requests 21+ Permissions](#71-systemui-requests-21-permissions)
  - [7.2 Missing reason Fields on Sensitive Permissions](#72-missing-reason-fields-on-sensitive-permissions)
  - [7.3 Runtime Permission Requests Without Proper Handling](#73-runtime-permission-requests-without-proper-handling)
- [8. UI/UX Pattern Findings](#8-uiux-pattern-findings)
  - [8.1 Hardcoded Color Values Instead of Theme Tokens](#81-hardcoded-color-values-instead-of-theme-tokens)
  - [8.2 Inconsistent Component Structure Across Apps](#82-inconsistent-component-structure-across-apps)
  - [8.3 Magic Numbers for Layout Values](#83-magic-numbers-for-layout-values)
- [9. App-Specific Findings](#9-app-specific-findings)
  - [9.1 Launcher](#91-launcher)
  - [9.2 SystemUI](#92-systemui)
  - [9.3 Settings](#93-settings)
  - [9.4 ScreenLock](#94-screenlock)
  - [9.5 Notes](#95-notes)
  - [9.6 MMS](#96-mms)
- [10. Anti-Patterns for Third-Party Developers](#10-anti-patterns-for-third-party-developers)
- [11. Summary of Findings by Severity](#11-summary-of-findings-by-severity)
- [12. Recommendations](#12-recommendations)

---

## 1. Executive Summary

This review covers the built-in applications shipped with OpenHarmony 4.1 Release, located at `/home/dspfac/openharmony/applications/standard/`. These applications serve both as system components and as reference implementations for third-party developers. The review identified **3 critical security issues**, **5 high-severity code quality problems**, and numerous medium and low severity items that collectively undermine the example-setting purpose of these apps.

The most severe finding is a sample application that ships with hardcoded RSA private keys and a plaintext test password (`123456`). Additionally, the WiFi settings page constructs a QR code containing the plaintext WiFi pre-shared key, which could lead to credential exposure. Architecturally, the apps rely heavily on `globalThis` for state management (a deprecated anti-pattern), use deeply nested relative imports (8+ levels), and widely suppress TypeScript type checking with `@ts-nocheck` and `@ts-ignore`.

**Reviewed Apps:** Launcher (274 source files), SystemUI (272 files), Settings (149 files), ScreenLock, Screenshot, Notes, Contacts, Camera, MMS, Permission Manager, FilePicker, DLP Manager, Theme, CalendarData, Photos, PrintSpooler, Admin Provisioning, Call, Security Privacy Center, Auth Widget.

---

## 2. Scope and Methodology

- **Source Path:** `/home/dspfac/openharmony/applications/standard/`
- **File Types:** `.ts`, `.ets`, `.json5` (module configs)
- **Focus Areas:** Security (credentials, logging, data exposure), code quality (typing, architecture, error handling), permission usage, UI/UX patterns as developer references
- **Excluded:** `app_samples/` was included only where findings surfaced in security scans; it contains developer samples, not shipping system code

---

## 3. Application Inventory

| Application | Path | Source Files | 描述 |
|---|---|---|---|
| Launcher | `launcher/` | 274 | Home screen, app grid, folders, dock |
| SystemUI | `systemui/` | 272 | Status bar, notification panel, control center |
| Settings | `settings/` | 149 | System settings (WiFi, Bluetooth, display, etc.) |
| ScreenLock | `screenlock/` | ~100 | Lock screen, PIN/pattern/password authentication |
| Camera | `camera/` | ~80 | Camera app (photo, video, multi-mode) |
| 备注 | `notes/` | ~40 | Notes application |
| Contacts | `contacts/` | ~30 | Contacts management |
| Contacts Data | `contacts_data/` | ~20 | Contacts data provider |
| MMS | `mms/` | ~50 | Messaging application |
| Photos | `photos/` | ~60 | Photo gallery |
| Permission Manager | `permission_manager/` | ~30 | Runtime permission dialogs |
| Screenshot | `screenshot/` | 8 | Screenshot capture and display |
| FilePicker | `filepicker/` | ~20 | File selection UI |
| DLP Manager | `dlp_manager/` | ~20 | Document protection |
| Theme | `theme/` | ~10 | Wallpaper/theme management |
| CalendarData | `calendardata/` | ~30 | Calendar data provider |
| Settings Data | `settings_data/` | ~10 | Settings data provider |
| PrintSpooler | `printspooler/` | ~15 | Print management |
| Call | `call/` | ~20 | Phone/call management |
| Security Privacy Center | `security_privacy_center/` | ~30 | Certificate management |
| Admin Provisioning | `admin_provisioning/` | ~10 | Enterprise device admin |
| Auth Widget | `applications_auth_widget/` | ~10 | Authentication widget |

---

## 4. Critical Security Findings

### 4.1 Hardcoded Cryptographic Keys in Sample App

**Severity: CRITICAL**
**File:** `/home/dspfac/openharmony/applications/standard/app_samples/code/BasicFeature/Security/PaySecurely/cloud/src/main/ets/PayServer.ts`

A sample payment application ships with hardcoded RSA-1024 private keys and a plaintext test password:

```typescript
const PAY_PRI_KEY = 'MIICdQIBADANBgkqhkiG9w0BAQEFAAS...'; // Full RSA private key
const MERCHANT_PUB_KEY = 'MFkwEwYHKoZIzj0CAQY...';
const PAY_CLIENT_PRI_KEY = 'MIICdwIBADANBgkqhkiG9w0BAQEFAAS...';
const TEST_USER: User = {
  name: 'test',
  payPassword: '123456',
};
```

Additional issues:
- Uses RSA-1024, which is considered cryptographically weak (minimum RSA-2048 is recommended)
- Uses `RSA1024|PKCS1` padding (PKCS#1 v1.5) which is vulnerable to padding oracle attacks; OAEP should be used instead
- The hardcoded credentials are presented as a reference implementation for a "pay securely" feature, directly contradicting its stated purpose

While this is in `app_samples/`, it is distributed as an official code example. Developers copying this pattern will introduce critical vulnerabilities.

### 4.2 WiFi Pre-Shared Key Exposed via QR Code

**Severity: CRITICAL**
**File:** `/home/dspfac/openharmony/applications/standard/settings/product/phone/src/main/ets/pages/wifi.ets` (lines 793-798)

The WiFi settings details dialog constructs a QR code that contains the WiFi SSID and pre-shared key in plaintext:

```typescript
async getScanInfo() {
    if(this.apInfo?.ssid) {
      const deviceConfigsInfo = WifiModel.getDeviceConfigsInfo(this.apInfo.ssid)
      if(deviceConfigsInfo) {
        this.value = `ssid:${deviceConfigsInfo.ssid},preSharedKey:${deviceConfigsInfo.preSharedKey}`
      }
    }
}
```

This value is then rendered directly as a `QRCode` component (line 676), meaning the WiFi password is encoded into a QR code visible on screen. Any camera pointed at the screen can capture the credential.

### 4.3 Token Values Logged to Console

**Severity: HIGH**
**File:** `/home/dspfac/openharmony/applications/standard/dlp_manager/entry/src/main/ets/feature/HomeFeature.ets` (line 125)

```typescript
console.log(TAG, 'sandBoxLinkFileHome start', callerToken);
```

Token identifiers are logged to console in the DLP Manager (document protection app). While these may be capability tokens rather than authentication tokens, logging any token in a security-sensitive application is a concern as logs may be persisted or accessible to other apps.

---

## 5. Architecture and Code Quality Findings

### 5.1 Pervasive Use of globalThis as Service Locator

**Severity: HIGH**

The Launcher and other apps extensively use `globalThis` as a global state container and service locator. Over 30 distinct uses were found in the Launcher alone:

```typescript
// Launcher AppModel.ts
static getInstance(): AppModel {
    if (globalThis.AppModel == null) {
      globalThis.AppModel = new AppModel();
    }
    return globalThis.AppModel;
}
```

Other examples:
- `globalThis.desktopContext` - stores the ability context
- `globalThis.ResourceManager` - caches resource manager
- `globalThis.WindowManager` - window management singleton
- `globalThis.SettingsModelInstance` - settings model singleton
- `globalThis.abilityWant` (Theme app)
- `globalThis.abilityContext` (FilePicker)

**Impact:** `globalThis` usage is explicitly discouraged in OpenHarmony documentation. It creates implicit dependencies, makes testing difficult, and can lead to naming collisions. It also circumvents ArkTS strict typing.

**Affected files:** 100+ files across launcher, systemui, screenlock, filepicker, theme, and others.

### 5.2 Deeply Nested Relative Import Paths

**Severity: MEDIUM**

404 import statements across 162 files use relative paths with 8 or more `../` segments:

```typescript
// screenLockService.ts
import Log from '../../../../../../../../common/src/main/ets/default/Log';
import Trace from '../../../../../../../../common/src/main/ets/default/Trace'
import ScreenLockModel from './screenLockModel';
import AccountModel from './accountsModel'
import {ScreenLockStatus} from '../../../../../../../../common/src/main/ets/default/ScreenLockCommon';
```

This is particularly widespread in SystemUI (100+ occurrences) and ScreenLock (50+ occurrences).

**Impact:** Fragile imports that break on directory restructuring, extremely poor readability, and a bad example for third-party developers. Path aliases or module references should be used.

### 5.3 Widespread Use of ts-ignore and ts-nocheck

**Severity: HIGH**

30+ instances of `@ts-ignore` and `@ts-nocheck` were found across built-in apps:

| App | Occurrences | Comment |
|---|---|---|
| MMS | 17 (`@ts-ignore`), 3 (`@ts-nocheck`) | Entire files and individual lines |
| ScreenLock | 3 (`@ts-nocheck` on WindowManager, TimeManager, SysFaultLogger) |
| SystemUI | 1 (`@ts-nocheck` on WindowManager) |
| Launcher | 1 (`@ts-nocheck` on SettingsDataManager) |
| PrintSpooler | 5 (`@ts-ignore`) |
| Theme | 2 (`@ts-nocheck`) |
| Photos | 1 (`@ts-ignore`) |

**Impact:** These suppress type checking entirely, undermining ArkTS's safety guarantees. `@ts-nocheck` on critical files like `WindowManager.ts` and `SysFaultLogger.ts` in system apps is especially concerning.

### 5.4 Excessive Use of the any Type

**Severity: MEDIUM**

The `any` type is used extensively:
- Launcher: 62 occurrences across 20 files
- SystemUI: widespread through ViewModels and Services
- Settings: `connectParam: any` in WiFi connection code (`WifiModel.ts` line 320)

Example from the WiFi model:
```typescript
let connectParam: any = {
    "ssid": apInfo.ssid,
    "bssid": apInfo.bssid,
    "preSharedKey": password,
    ...
};
```

**Impact:** Defeats type safety, allows runtime type errors, and sets a poor example. ArkTS intentionally restricts `any` usage in stricter modes.

### 5.5 Deprecated API Usage Across Apps

**Severity: MEDIUM**

34 files across 15+ apps import deprecated `@system.*` modules:

- `@system.router` - deprecated in favor of `@ohos.router`
- `@system.prompt` - deprecated in favor of `@ohos.promptAction`
- `@system.app` - deprecated in favor of `@ohos.app.ability`

Affected apps include: MMS, Settings, Notes, ScreenLock, Launcher, Contacts, Call, Photos, Camera.

The ScreenLock service (`screenLockService.ts`) uses `Router` from `@system.router` for page navigation, mixing old and new APIs in the same file.

---

## 6. Error Handling Findings

### 6.1 Silently Swallowed Errors in SystemUI

**Severity: HIGH**

Multiple SystemUI files have `.catch()` handlers that completely discard errors:

```typescript
// StatusBarVM.ts lines 200-204
this.mStatusBarEnable ? WindowManager.showWindow(WindowType.STATUS_BAR).then(() => {
}).catch((err) => {
}) : WindowManager.hideWindow(WindowType.STATUS_BAR).then(() => {
}).catch((err) => {
});
```

This pattern appears in at least 15 locations across SystemUI:
- `product/pc/notificationpanel/src/main/ets/pages/index.ets`
- `product/phone/statusbar/src/main/ets/ServiceExtAbility/ServiceExtAbility.ts`
- `product/pc/statusbar/src/main/ets/ServiceExtAbility/ServiceExtAbility.ts`
- `product/pc/controlpanel/src/main/ets/pages/index.ets`
- `product/pc/controlpanel/src/main/ets/ServiceExtAbility/ServiceExtAbility.ts`

**Impact:** Window management failures in the status bar and control panel will be completely invisible, leading to hard-to-diagnose UI issues. In a system app, window creation failures could leave the user with no status bar or control panel.

### 6.2 Empty Catch Blocks in Sample Apps

**Severity: MEDIUM**

Multiple empty catch blocks found in `app_samples/`:
- `VoiceCallDemo/BufferModel.ets` - 3 empty catch blocks (audio buffer operations)
- `VoiceCallDemo/Index.ets` - 1 empty catch block
- `VoiceCallDemo/TimerUtil.ets` - 1 empty catch block
- `Asset/AssetModel.ets` - 1 empty catch block (security asset operations)
- `AbilityFeature/CharacterOperation.ets` - 1 empty catch block

**Impact:** As reference code, this teaches developers that silencing errors is acceptable.

### 6.3 Verbose Error Serialization Leaking Stack Traces

**Severity: LOW**

Hundreds of instances of `JSON.stringify(error)` in log output across all apps. While useful for debugging, this can expose internal implementation details in production logs.

Example from Settings:
```typescript
LogUtil.error(`${this.TAG_PAGE}, start failed. Cause: ${JSON.stringify(error)}`);
```

---

## 7. Permission Usage Findings

### 7.1 SystemUI Requests 21+ Permissions

**Severity: MEDIUM**
**File:** `/home/dspfac/openharmony/applications/standard/systemui/entry/phone/src/main/module.json5`

SystemUI requests 21 permissions including:
- `ohos.permission.CAPTURE_SCREEN`
- `ohos.permission.MANAGE_LOCAL_ACCOUNTS`
- `ohos.permission.INTERACT_ACROSS_LOCAL_ACCOUNTS_EXTENSION`
- `ohos.permission.ACCESS_SERVICE_DM`
- `ohos.permission.CONNECTIVITY_INTERNAL`
- `ohos.permission.START_INVISIBLE_ABILITY`
- `ohos.permission.START_ABILITIES_FROM_BACKGROUND`

While SystemUI legitimately needs many permissions, the breadth is concerning. Some (like `MANAGE_WIFI_CONNECTION`, `SET_WIFI_INFO`) may be unnecessary for a status bar app and could be delegated to the Settings app.

### 7.2 Missing reason Fields on Sensitive Permissions

**Severity: MEDIUM**

Most `requestPermissions` entries lack the `reason` field. Only 2 of SystemUI's 21 permissions include a reason string:

```json
{
    "name": "ohos.permission.GET_INSTALLED_BUNDLE_LIST",
    "reason": "$string:get_installed_bundle_list_reason"
},
{
    "name": "ohos.permission.DISTRIBUTED_DATASYNC",
    "reason": "$string:distributed_data_sync_reason"
}
```

The remaining 19 permissions have no reason. The Launcher similarly has 9 permissions, all without reason fields. While system apps may not show permission dialogs, including reasons is best practice and serves as documentation.

### 7.3 Runtime Permission Requests Without Proper Handling

**Severity: MEDIUM**

The Notes app requests runtime permissions but has inconsistent result checking:

```typescript
// NoteContentComp.ets
await AtManager.requestPermissionsFromUser(context, permissionList).then((data) => {
    let sum = 0
    for (let i = 0; i < data.authResults.length; i++) {
        sum += data.authResults[i]
    }
    LogUtil.info(TAG, 'request permissions sum: ' + sum)
}).catch((err: BusinessError) => {
    LogUtil.warn(TAG, 'failed to requestPermissionsFromUser : ' + err.code);
})
```

The permission result (`sum`) is calculated but never checked against expected values. The code proceeds to use the protected APIs regardless of whether permissions were granted.

Additionally, the Notes app requests `ohos.permission.MANAGE_DISPOSED_APP_STATUS` (a high-privilege system permission) which seems excessive for a notes application.

---

## 8. UI/UX Pattern Findings

### 8.1 Hardcoded Color Values Instead of Theme Tokens

**Severity: LOW**

Several apps use hardcoded color strings rather than system theme tokens:

```typescript
// wifi.ets line 206
.selectedColor('#007DFF')

// StatusBarVM.ts line 294
this.setComponentContent(id, '#FFFFFFFF');
```

While most color references correctly use `$r('sys.color.*')` system resources, the inconsistency means these elements will not respond to dark mode or theme changes.

### 8.2 Inconsistent Component Structure Across Apps

**Severity: LOW**

Different apps use different architectural patterns with no consistent standard:
- **Launcher:** MVVM with ViewModel/Model separation, uses `Presenter` pattern in some features
- **SystemUI:** MV pattern with VMs (e.g., `StatusBarVM`) and Services, uses `createOrGet` singleton helper
- **Settings:** MVC with `Controller` classes bound to pages
- **ScreenLock:** Service-oriented with `ScreenLockService` managing all state
- **Notes:** Direct component state management with minimal separation

This inconsistency makes it harder for developers to learn a canonical OpenHarmony app architecture.

### 8.3 Magic Numbers for Layout Values

**Severity: LOW**

While most layout values are correctly referenced via `$r('app.float.*')` resources, there are notable exceptions:

```typescript
// Screenshot index.ets
.border({ color: Color.White, radius: 8, width: 8 })

// wifi.ets
QRCode(this.value)
    .width(160)
    .height(160)

// NoteContentComp.ets
Image($r('app.media.picture_white'))
    .height(24)
    .width(24)
```

---

## 9. App-Specific Findings

### 9.1 Launcher

**Architecture:** Feature-based modularization (bigfolder, smartdock, appcenter, form, pagedesktop, settings, numbadge, gesturenavigation). Uses MVVM pattern with ViewModels and Models.

**Positive aspects:**
- Good modular decomposition with clear feature boundaries
- Consistent use of `Log` utility with TAG-based identification
- Proper event system via `LocalEventManager` for inter-feature communication
- Lifecycle-aware resource cleanup (unregister listeners)

**Issues:**
- Heavy reliance on `globalThis` for singletons (AppModel, SettingsModel, FormModel, WindowManager, etc.)
- Untyped listener parameters: `registerStateChangeListener(listener)` accepts `any`
- `SettingsDataManager.ts` has `@ts-nocheck` at the top, disabling all type checks
- `getItemIndex()` uses `indexOf` inside a `for...of` loop -- redundant double iteration

### 9.2 SystemUI

**Architecture:** Feature-component based with status bar, control center, notification panel, volume, brightness, bluetooth, location, signal, NFC, ring mode, and auto-rotate components.

**Positive aspects:**
- Clean separation between Service (data), ViewModel (state), and Component (UI) layers
- `TintStateManager` for coordinating status bar theming
- Plugin architecture for extensible status bar items

**Issues:**
- `WindowManager.ts` uses `@ts-nocheck` -- the most critical infrastructure file
- Empty `.catch()` handlers on window management operations (see 6.1)
- `StatusBarVM` has many `Log.showInfo()` calls that serialize full data objects on every state change, creating performance overhead
- `updateStatusBarData()` uses `for (let key in data)` which iterates inherited properties

### 9.3 Settings

**Architecture:** MVC pattern with page-level components and Controllers. Uses a shared component library and utility modules.

**Positive aspects:**
- Good use of `GridContainer` for responsive layout
- Proper device-type detection for phone vs. tablet layout
- Clean separation of WiFi model from UI

**Issues:**
- WiFi QR code exposes pre-shared key in plaintext (see 4.2)
- WiFi password page (`wifiPsd.ets`) stores password in component `@State` as plaintext string
- `PasswordSettingController` is bound to the component via string property names (`bindProperties(["passwordList", "pinChallenge"])`) -- fragile and untyped
- `pinChallenge` is passed via router params in plaintext
- Uses deprecated `@system.router` and `@system.prompt`

### 9.4 ScreenLock

**Architecture:** Service-oriented with `ScreenLockService` as central coordinator, `ScreenLockModel` for system interactions, and `AccountModel` for user authentication.

**Positive aspects:**
- Memory monitoring with PSS limit tracking and fault logging
- Proper face authentication state management
- Event-driven architecture with clear lifecycle

**Issues:**
- Module-level mutable variables (`mRouterPath`, `mWillRecognizeFace`, `mUnLockBeginAnimation`) outside any class -- shared global state
- `authUser()` method converts password array to string by `join('')` and passes to `registerPWDInputer` -- password handling could be improved
- Callback parameters are untyped: `authUser(authSubType, passwordData, callback)` -- no type annotation on `callback`
- `SERVICE_RESTART` event reconnects after a hardcoded 2000ms delay with no retry limit

### 9.5 Notes

**Architecture:** Feature module with shared components and utilities. Uses direct ArkUI state management.

**Issues:**
- Requests `ohos.permission.MANAGE_DISPOSED_APP_STATUS` -- an elevated system permission that seems unnecessary for a notes app
- Requests `READ_MEDIA` and `WRITE_MEDIA` at point of use without checking if already granted
- Uses deprecated `@system.router` for navigation
- Hardcodes bundle name `"com.ohos.photos"` for image selection -- should use intent-based routing

### 9.6 MMS

**Architecture:** Controller-based pages with service and model layers.

**Issues:**
- Highest concentration of `@ts-ignore` (17 instances) and `@ts-nocheck` (3 instances) across all built-in apps
- Uses deprecated `@system.router`, `@system.prompt` extensively
- Heavy use of `globalThis` for state management
- Multiple controllers use untyped parameters

---

## 10. Anti-Patterns for Third-Party Developers

Since built-in apps serve as reference implementations, the following patterns should **not** be copied:

| Anti-Pattern | Where Found | What To Do Instead |
|---|---|---|
| Using `globalThis` for singletons | Launcher, SystemUI, FilePicker | Use `AppStorage`, module-level exports, or dependency injection |
| `@ts-nocheck` / `@ts-ignore` | MMS, ScreenLock, SystemUI, Launcher | Fix the actual type errors; use proper interfaces |
| 8+ level relative imports | SystemUI, ScreenLock, Settings | Use path aliases in `tsconfig.json` or module references |
| Empty `.catch()` handlers | SystemUI | At minimum log the error; consider recovery/retry |
| Deprecated `@system.*` imports | Settings, MMS, Notes, ScreenLock | Use `@ohos.router`, `@ohos.promptAction` |
| Hardcoded color strings | Settings (WiFi), SystemUI | Use `$r('sys.color.*')` system design tokens |
| Untyped function parameters | Launcher, ScreenLock | Use TypeScript interfaces and type annotations |
| Logging sensitive data | DLP Manager, WiFi settings | Sanitize logs; never log tokens, keys, or passwords |
| `any` type usage | Launcher, Settings, SystemUI | Define proper interfaces |
| Hardcoded bundle names | 备注 | Use intent actions for implicit capability discovery |

---

## 11. Summary of Findings by Severity

| Severity | Count | Key Items |
|---|---|---|
| **CRITICAL** | 2 | Hardcoded RSA private keys + password in sample app; WiFi PSK in QR code |
| **HIGH** | 4 | Token logging in DLP Manager; `globalThis` usage (100+ files); `@ts-nocheck` on critical files; silently swallowed errors in SystemUI |
| **MEDIUM** | 7 | Deep relative imports (404 instances); `any` type (62+ in Launcher alone); deprecated APIs (34 files); SystemUI 21+ permissions; missing permission reasons; unhandled permission results; empty catch blocks in samples |
| **LOW** | 3 | Hardcoded colors; inconsistent architecture; magic numbers |

**Total: 16 distinct findings**

---

## 12. Recommendations

### Immediate (Security)

1. **Remove or replace hardcoded keys** in `PaySecurely` sample. Add prominent warnings that sample credentials must never be used in production. Upgrade to RSA-2048 with OAEP padding.
2. **Fix WiFi QR code** in Settings to not include the pre-shared key, or at minimum require authentication (PIN/biometric) before showing the QR code.
3. **Remove token logging** from DLP Manager. Audit all `console.log` and `Log.show*` calls in security-sensitive apps for credential/token leakage.

### Short-Term (Code Quality)

4. **Eliminate `globalThis` usage** across all built-in apps. Migrate to `AppStorage` or properly typed module-level singletons.
5. **Remove all `@ts-nocheck`** directives, especially on `WindowManager.ts` and `SysFaultLogger.ts`. Fix the underlying type issues.
6. **Replace empty `.catch()` handlers** in SystemUI with proper error logging and graceful degradation.
7. **Migrate deprecated API imports** from `@system.*` to `@ohos.*` equivalents.

### Medium-Term (Architecture)

8. **Establish a canonical app architecture** pattern documented alongside these apps. Choose one pattern (MVVM recommended) and apply it consistently.
9. **Configure path aliases** to eliminate deep relative imports. All `../../../../../../../../` paths should be replaced with aliased module imports.
10. **Add proper TypeScript types** to all function parameters, return types, and state variables. Eliminate `any` usage.
11. **Review permission lists** for each app, ensuring only necessary permissions are requested and all include `reason` strings.
12. **Create a shared coding standards guide** for built-in app development with linting rules that enforce these patterns.
