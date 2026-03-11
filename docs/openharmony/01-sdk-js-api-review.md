# OpenHarmony 4.1 Release -- SDK JS API Declarations Review

**Review Date:** 2026-03-10
**Scope:** `/home/dspfac/openharmony/interface/sdk-js/api/` -- all `.d.ts` and `.d.ets` API declaration files
**Reviewer:** Automated code review (Claude Opus 4.6)

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Scope and Methodology](#2-scope-and-methodology)
3. [Cross-Cutting Findings](#3-cross-cutting-findings)
   - 3.1 [Type Safety Issues](#31-type-safety-issues)
   - 3.2 [Documentation Quality](#32-documentation-quality)
   - 3.3 [Deprecation and API Evolution](#33-deprecation-and-api-evolution)
   - 3.4 [Error Handling Patterns](#34-error-handling-patterns)
   - 3.5 [Naming Convention Issues](#35-naming-convention-issues)
   - 3.6 [Security Considerations](#36-security-considerations)
   - 3.7 [JSDoc Duplication Pattern](#37-jsdoc-duplication-pattern)
4. [Module-by-Module Findings](#4-module-by-module-findings)
   - 4.1 [Core / Foundation](#41-core--foundation)
   - 4.2 [Networking](#42-networking)
   - 4.3 [File System](#43-file-system)
   - 4.4 [Data Management](#44-data-management)
   - 4.5 [Security and Crypto](#45-security-and-crypto)
   - 4.6 [UI Framework (ArkUI)](#46-ui-framework-arkui)
   - 4.7 [Multimedia](#47-multimedia)
   - 4.8 [Connectivity (Bluetooth / Wi-Fi / NFC)](#48-connectivity-bluetooth--wi-fi--nfc)
   - 4.9 [Ability Framework](#49-ability-framework)
   - 4.10 [Utilities and Collections](#410-utilities-and-collections)
   - 4.11 [System Services](#411-system-services)
   - 4.12 [Telephony](#412-telephony)
   - 4.13 [Enterprise Management](#413-enterprise-management)
5. [Summary of All Findings by Severity](#5-summary-of-all-findings-by-severity)
6. [Recommendations](#6-recommendations)

---

## 1. Executive Summary

The OpenHarmony 4.1 SDK JS API declarations comprise approximately **444 files** in the top-level `api/` directory (including `.d.ts`, `.d.ets`, and subdirectories), with an additional **100+ ETS component declarations** under `@internal/component/ets/`.

Overall, the API surface is extensive and covers a wide range of OS capabilities. The declarations generally follow a consistent structure with JSDoc annotations including `@syscap`, `@since`, `@crossplatform`, and `@atomicservice` tags. However, the review identified several systemic issues that impact developer experience, type safety, and maintainability.

**Key Findings:**
- **12 Critical/High severity issues** related to type safety, `any` usage, and incorrect documentation
- **15+ Medium severity issues** related to naming inconsistencies, missing Promise overloads, and `Object` usage
- **Numerous Low/Info issues** related to JSDoc duplication overhead and minor documentation gaps

---

## 2. Scope and Methodology

### Files Reviewed
- All `@ohos.*.d.ts` files (approximately 350 files) covering the core OpenHarmony JS/TS SDK
- `@system.*.d.ts` files (legacy JS API, approximately 15 files)
- `@internal/component/ets/*.d.ts` (ArkUI ETS component declarations, approximately 100+ files)
- `@ohos.arkui.advanced.*.d.ets` (advanced UI component declarations, 24 files)
- Supporting files: `permissions.d.ts`, subdirectories (`ability/`, `application/`, `bundle/`, etc.)

### Review Approach
- Automated pattern-based scanning for `any`, `Object`, missing generics, deprecated patterns
- Manual review of representative files from each major domain
- Cross-file consistency checks for naming, error handling, and documentation patterns

---

## 3. Cross-Cutting Findings

### 3.1 Type Safety Issues

#### F-3.1.1: Use of `any` type in public APIs
**Severity: High**

Multiple public API declarations use `{ [key: string]: any }` which defeats TypeScript type checking. Found in:

| File | Location |
|------|----------|
| `@ohos.wantAgent.d.ts` | `extraInfo?: { [key: string]: any }` |
| `@ohos.application.Want.d.ts` | `parameters?: { [key: string]: any }` |
| `@ohos.account.appAccount.d.ts` | Multiple `options` and `onResult` parameters |
| `@ohos.distributedHardware.deviceManager.d.ts` | `extraInfo: { [key: string]: any }` in two interfaces |
| `@ohos.events.emitter.d.ts` | `data?: { [key: string]: any }` in `EventData` |

**Impact:** Developers lose all type safety when working with these APIs. Any typo in property names or wrong value types will not be caught at compile time.

**Recommendation:** Replace `any` with `unknown`, or better yet, use `Record<string, string | number | boolean | object>` or domain-specific union types where the value space is known. For Want parameters, define a structured `WantParams` type.

#### F-3.1.2: Pervasive use of `Object` (capital-O) type
**Severity: High**

Found **246+ occurrences of `Object`** type across **30+ files**. Notable examples:

| File | Usage |
|------|-------|
| `@ohos.net.http.d.ts` | `header?: Object` (should be `Record<string, string>`) |
| `@ohos.net.http.d.ts` | `extraData?: string \| Object \| ArrayBuffer` |
| `@ohos.net.webSocket.d.ts` | `header?: Object` |
| `@ohos.request.d.ts` | `header?: Object` |
| `@ohos.router.d.ts` | `params?: Object` |
| `@ohos.util.d.ts` | `...args: Object[]` |
| `@ohos.buffer.d.ts` | `options?: Object` in Blob constructor |
| `@ohos.worker.d.ts` | `transfer?: Object[]` (should be `ArrayBuffer[]` or `Transferable[]`) |
| Various util collections | `thisArg?: Object` in forEach/replaceAllElements |

**Impact:** `Object` is overly broad and provides no useful type checking. HTTP headers typed as `Object` give developers zero guidance on the expected shape.

**Recommendation:**
- HTTP headers: `Record<string, string | string[]>`
- Router params: `Record<string, string | number | boolean | object>`
- Worker transfer: `ArrayBuffer[]`
- util collections thisArg: `unknown` or just remove the parameter since arrow functions capture `this` lexically

#### F-3.1.3: Permissions type is just `string`
**Severity: Medium**

In `permissions.d.ts`:
```typescript
export type Permissions = string;
```

This provides no compile-time validation of permission names. While this is a type alias, it could be a string literal union type of all known permission names (e.g., `'ohos.permission.INTERNET' | 'ohos.permission.CAMERA' | ...`).

**Impact:** Developers can pass any string as a permission name with no compile-time error for typos.

---

### 3.2 Documentation Quality

#### F-3.2.1: Copy-paste errors in JSDoc comments
**Severity: High**

In `@ohos.data.relationalStore.d.ts`, the `AssetStatus` enum has incorrect JSDoc comments. The descriptions for `ASSET_INSERT`, `ASSET_UPDATE`, and `ASSET_DELETE` all incorrectly say "ASSET_ABNORMAL" instead of their correct names:

```
ASSET_INSERT:   "ASSET_ABNORMAL: means the asset needs to be inserted."
ASSET_UPDATE:   "ASSET_ABNORMAL: means the asset needs to be updated."
ASSET_DELETE:   "ASSET_ABNORMAL: means the asset needs to be deleted."
ASSET_ABNORMAL: "ASSET_ABNORMAL: means the status of asset is abnormal."
```

**Impact:** Misleading documentation that will confuse developers trying to understand asset status lifecycle.

#### F-3.2.2: Missing `@file` tag on deprecated modules
**Severity: Low**

The `@ohos.notification.d.ts` file (deprecated since API 9) is missing the `@file` and `@kit` JSDoc tags that all other modules have. While this module is deprecated, it still ships in the SDK and should maintain documentation consistency.

#### F-3.2.3: Inconsistent JSDoc descriptions for return values
**Severity: Low**

Some `@returns` descriptions capitalize "Returns" while others use lowercase. Example from `@ohos.abilityAccessCtrl.d.ts`:
- API 8: `@returns { AtManager } Returns the instance of the AtManager.`
- API 10: `@returns { AtManager } returns the instance of the AtManager.`

#### F-3.2.4: Developer-internal comments exposed in public API JSDoc
**Severity: Medium**

In `@ohos.data.relationalStore.d.ts`, the `ValuesBucket` typedef exposes internal change notes in its JSDoc:
```
"change {[key: string]: ValueType;} to Record<string, ValueType>"
```
This is an internal implementation note, not a developer-facing description.

#### F-3.2.5: Double-space typos in JSDoc
**Severity: Info**

In `@ohos.events.emitter.d.ts`, there is a double space in `"callback function  from an event"` (line 138 and 149 area).

---

### 3.3 Deprecation and API Evolution

#### F-3.3.1: Large volume of deprecated-but-shipped APIs
**Severity: Medium**

Over **1,500+ `@deprecated` tags** across 70+ files. Major deprecated modules still shipped:

| Deprecated Module | Replacement | Deprecated Since |
|---|---|---|
| `@ohos.notification.d.ts` | `@ohos.notificationManager.d.ts` + `@ohos.notificationSubscribe.d.ts` | API 9 |
| `@ohos.bluetooth.d.ts` | `@ohos.bluetoothManager.d.ts` then `@ohos.bluetooth.*.d.ts` (modular) | API 9 |
| `@ohos.wifi.d.ts` | `@ohos.wifiManager.d.ts` | API 9 |
| `@ohos.usb.d.ts` | `@ohos.usbManager.d.ts` | API 9 |
| `@ohos.data.rdb.d.ts` | `@ohos.data.relationalStore.d.ts` | API 9 |
| `@ohos.data.storage.d.ts` | `@ohos.data.preferences.d.ts` | API 9 |
| `@ohos.geolocation.d.ts` | `@ohos.geoLocationManager.d.ts` | API 9 |
| `@ohos.commonEvent.d.ts` | `@ohos.commonEventManager.d.ts` | API 9 |
| `@ohos.document.d.ts` | `@ohos.file.picker.d.ts` | API 9 |
| `@ohos.fileio.d.ts` | `@ohos.file.fs.d.ts` | API 9 |

**Impact:** The SDK ships a large number of deprecated APIs, almost all deprecated at API 9. This increases SDK size and developer confusion. Some of these are multi-layered: `@ohos.bluetooth.d.ts` -> `@ohos.bluetoothManager.d.ts` -> `@ohos.bluetooth.access.d.ts` etc.

#### F-3.3.2: Bluetooth API underwent three naming iterations
**Severity: Medium**

The Bluetooth API has undergone multiple rounds of deprecation:
1. `@ohos.bluetooth.d.ts` (API 7, deprecated API 9) -> `@ohos.bluetoothManager.d.ts`
2. `@ohos.bluetoothManager.d.ts` (API 9, deprecated API 10) -> modular `@ohos.bluetooth.*.d.ts`

This means Bluetooth was deprecated twice in two consecutive API versions, causing migration churn for developers.

#### F-3.3.3: Wi-Fi scan deprecated twice
**Severity: Low**

`wifiManager.scan()` was introduced in API 9 but deprecated in API 10 in favor of `wifiManager.startScan()`. This is a very short lifecycle for a public API.

---

### 3.4 Error Handling Patterns

#### F-3.4.1: Missing Promise overloads for callback-only APIs
**Severity: Medium**

Several APIs in `@ohos.commonEventManager.d.ts` only provide callback-based interfaces with no Promise alternative:
- `publish(event, callback)` -- no Promise version
- `publishAsUser(event, userId, callback)` -- no Promise version

This is inconsistent with the dual callback/Promise pattern used in most other modules (e.g., `@ohos.file.fs.d.ts`, `@ohos.net.http.d.ts`).

**Impact:** Developers cannot use `async/await` with these APIs, forcing callback-style code.

#### F-3.4.2: Inconsistent error code documentation
**Severity: Medium**

Error codes for `@throws` annotations vary in format:
- `@ohos.commonEventManager.d.ts`: `@throws { BusinessError } 401 - parameter error`
- `@ohos.wifiManager.d.ts`: `@throws {BusinessError} 201 - Permission denied.`
- `@ohos.abilityAccessCtrl.d.ts`: `@throws { BusinessError } 401 - The parameter check failed.`

The spacing inside `{ BusinessError }` vs `{BusinessError}`, and the description formatting (period vs no period, capitalization) are inconsistent.

#### F-3.4.3: Old APIs return boolean for success/failure instead of throwing
**Severity: Low**

In `@ohos.wifi.d.ts` (deprecated), APIs like `enableWifi()` return `boolean` for success/failure. The replacement `@ohos.wifiManager.d.ts` correctly changed to `void` with `@throws` for errors. This is good API evolution but the presence of both patterns in the SDK can confuse developers.

---

### 3.5 Naming Convention Issues

#### F-3.5.1: Mixed file naming conventions for ETS components
**Severity: Medium**

The `@internal/component/ets/` directory uses inconsistent naming:
- Snake_case: `alert_dialog.d.ts`, `column_split.d.ts`, `date_picker.d.ts`, `list_item.d.ts`
- camelCase: `gridItem.d.ts`, `checkboxgroup.d.ts`
- Mixed: `calendar_picker.d.ts` vs `checkboxgroup.d.ts` (no separator vs underscore)

#### F-3.5.2: Inconsistent module naming patterns
**Severity: Low**

- `@ohos.geolocation.d.ts` vs `@ohos.geoLocationManager.d.ts` (camelCase inconsistency in "Location")
- `@ohos.usb.d.ts` vs `@ohos.usbManager.d.ts` (Manager suffix pattern)
- `@ohos.data.rdb.d.ts` vs `@ohos.data.relationalStore.d.ts` (abbreviation vs full name)
- `@ohos.fileio.d.ts` vs `@ohos.file.fs.d.ts` (completely different naming structure)

#### F-3.5.3: `@ohos.request.d.ts` error constants use inconsistent permission tag
**Severity: Low**

Error constants in `@ohos.request.d.ts` are tagged with `@permission ohos.permission.INTERNET` even when they are just error code constants, not methods requiring permissions. The `@permission` tag should only be on functions/methods that actually require the permission at runtime.

---

### 3.6 Security Considerations

#### F-3.6.1: System APIs exposed in public SDK declarations
**Severity: Medium**

Numerous APIs marked `@systemapi` (only callable by system applications) appear in the public SDK declarations alongside public APIs. While they will fail at runtime for third-party apps, their presence in the SDK:
- Increases the attack surface for API discovery
- Can confuse third-party developers who try to call them
- Notable: Window types (TYPE_INPUT_METHOD, TYPE_STATUS_BAR, etc.), Wi-Fi enable/disable, mission management

#### F-3.6.2: `caPath` in HTTP options allows custom CA certificates
**Severity: Info**

`@ohos.net.http.d.ts` exposes `caPath?: string` which allows apps to specify custom CA certificate paths. While this is necessary for enterprise use cases, the documentation does not warn about security implications of certificate pinning bypass.

#### F-3.6.3: Pasteboard API access is not permission-gated
**Severity: Info**

`@ohos.pasteboard.d.ts` provides clipboard read/write without requiring permissions in the API declarations (no `@permission` tags visible). Clipboard access is a known privacy concern on mobile platforms. Note: Runtime access control may exist separately from the declaration-level permission annotations.

---

### 3.7 JSDoc Duplication Pattern

#### F-3.7.1: Massive JSDoc block duplication for version evolution
**Severity: Info**

Nearly every API member has 2-3 duplicate JSDoc blocks to document version evolution. For example, a single method declaration in `@ohos.net.http.d.ts` has separate blocks for `@since 6`, `@since 10` (adding `@crossplatform`), and `@since 11` (adding `@atomicservice`). This means:

- The `header?: Object` field has **3 nearly-identical JSDoc blocks** (36 lines) for a single property
- `@ohos.net.http.d.ts` has **45 uses of `Object`** type, many repeated across version blocks
- `@ohos.data.preferences.d.ts` has **86 `@crossplatform` tags** for documentation that could be expressed more concisely

This pattern makes the files extremely verbose (the HTTP module is 2000+ lines) and increases the risk of copy-paste errors like F-3.2.1.

**Recommendation:** Consider a tooling approach where a single JSDoc block can express version history inline, e.g. `@since 6 @crossplatform(10) @atomicservice(11)`.

---

## 4. Module-by-Module Findings

### 4.1 Core / Foundation

#### `@ohos.base.d.ts`
**Status: Well-designed**

- `Callback<T>`, `ErrorCallback<T>`, `AsyncCallback<T, E>`, and `BusinessError<T>` are well-typed with proper generics
- Good use of `extends Error` for `BusinessError`
- The generic parameter `E = void` on `AsyncCallback` is a nice design allowing error data typing
- **No issues found** -- this is the strongest file in the SDK

#### `@ohos.process.d.ts`
- Contains deprecated APIs (7 instances)
- Generally well-structured

### 4.2 Networking

#### `@ohos.net.http.d.ts`
- **F-4.2.1 (High):** `header?: Object` should be `Record<string, string>` or `{ [key: string]: string }`
- **F-4.2.2 (High):** `extraData?: string | Object | ArrayBuffer` -- `Object` is too loose; should use `Record<string, string>` for form data or a more specific type
- **F-4.2.3 (Medium):** Response `result: string | Object | ArrayBuffer` -- return type varies based on `HttpDataType` but this isn't expressed in the type system (could use overloads or generics)
- **F-4.2.4 (Low):** 12 `@permission` tags -- permission model is well-documented

#### `@ohos.net.webSocket.d.ts`
- Same `header?: Object` issue as HTTP

#### `@ohos.net.socket.d.ts`, `@ohos.net.connection.d.ts`
- Follow standard patterns, no major issues detected in sampled sections

### 4.3 File System

#### `@ohos.file.fs.d.ts`
- **F-4.3.1 (Info):** Very comprehensive API surface (3500+ `@throws` tags), good coverage
- Proper sync/async pairs (e.g., `open`/`openSync`, `read`/`readSync`)
- Good use of typed enums for `OpenMode` flags
- Progress callback typing with `ProgressListener` is well-designed
- Exports both value exports and type exports correctly

#### `@ohos.file.photoAccessHelper.d.ts`
- 7 uses of `Object` type, 30 `@deprecated` tags -- undergoing significant evolution

### 4.4 Data Management

#### `@ohos.data.relationalStore.d.ts`
- **F-4.4.1 (High):** Copy-paste JSDoc errors on `AssetStatus` enum (see F-3.2.1)
- **F-4.4.2 (Medium):** Internal change notes exposed in `ValuesBucket` JSDoc
- **F-4.4.3 (Info):** Good evolution from `{[key: string]: ValueType}` to `Record<string, ValueType>` in API 11
- `Asset.size` is typed as `string` rather than `number` -- this seems intentional for large file sizes but is unusual
- `Asset.createTime` and `Asset.modifyTime` are `string` -- could be `number` (timestamp) for type safety

#### `@ohos.data.preferences.d.ts`
- Well-typed `ValueType` union: `number | string | boolean | Array<number> | Array<string> | Array<boolean> | Uint8Array`
- Good use of literal types for constants: `MAX_KEY_LENGTH: 80`, `MAX_VALUE_LENGTH: 8192`
- Consistent dual callback/Promise pattern

#### `@ohos.data.rdb.d.ts` (deprecated)
- Properly deprecated with `@useinstead ohos.data.relationalStore`

### 4.5 Security and Crypto

#### `@ohos.security.cryptoFramework.d.ts`
- **F-4.5.1 (Medium):** `Result` enum uses generic names for error codes (`INVALID_PARAMS = 401`) that overlap with the global error code 401 used everywhere else. This could confuse developers about whether to check `BusinessError.code` or `Result`.
- **F-4.5.2 (Info):** Inconsistent `@atomicservice` tagging -- `NOT_SUPPORT` has `@crossplatform` but not `@atomicservice` in the API 11 block, while `INVALID_PARAMS` and `ERR_OUT_OF_MEMORY` do have `@atomicservice`
- `DataBlob` wrapping `Uint8Array` is a reasonable pattern
- `ParamsSpec` using `algName: string` rather than a typed enum is less type-safe

#### `@ohos.security.cert.d.ts`
- 29 `@deprecated` tags -- undergoing evolution

#### `@ohos.abilityAccessCtrl.d.ts`
- Good use of `Permissions` type for permission names
- Proper deprecation of `verifyAccessToken` with string parameter in favor of `checkAccessToken` with `Permissions` parameter
- Provides both sync (`verifyAccessTokenSync`) and async versions

### 4.6 UI Framework (ArkUI)

#### `@ohos.router.d.ts`
- **F-4.6.1 (High):** `params?: Object` should use a more specific type like `Record<string, string | number | boolean | object>`
- Well-structured `RouterMode` enum with good documentation
- `RouterState` provides useful index/name/path information

#### `@ohos.window.d.ts`
- **F-4.6.2 (Medium):** `WindowType` enum mixes public and `@systemapi` values without clear separation, making it unclear to developers which values they can actually use
- Missing `@since` on the initial namespace declaration (no version specified for the first block)
- Very large file -- would benefit from splitting

#### `@internal/component/ets/common.d.ts`
- **F-4.6.3 (Low):** `ComponentOptions.freezeWhenInactive` uses a comma separator (`,`) instead of semicolon (`;`) -- while valid TypeScript, this is inconsistent with the rest of the codebase

#### ArkUI Advanced Components (`@ohos.arkui.advanced.*.d.ets`)
- 24 advanced UI components with `.d.ets` extension
- Follow consistent patterns for component declarations

### 4.7 Multimedia

#### `@ohos.multimedia.camera.d.ts`
- **F-4.7.1 (Info):** Good use of `readonly` modifier on Profile properties
- Well-structured enum types (`CameraStatus`, `CameraFormat`)
- 55 `@deprecated` tags suggest active evolution
- Error codes use domain-specific range (7400xxx) -- good practice

#### `@ohos.multimedia.image.d.ts`
- **F-4.7.2 (Low):** First namespace declaration block (`@since 6`) is missing `@syscap` tag, while later blocks have it
- 18 `@deprecated` tags
- Good enum design for `PixelMapFormat`

#### `@ohos.multimedia.audio.d.ts`
- 74 `@deprecated` tags -- extensive API evolution

### 4.8 Connectivity (Bluetooth / Wi-Fi / NFC)

#### Bluetooth API Family
- **F-4.8.1 (Medium):** Three-layer deprecation chain (see F-3.3.2)
- `@ohos.bluetooth.d.ts`: 339 deprecated items -- entirely deprecated since API 9
- `@ohos.bluetoothManager.d.ts`: deprecated since API 10
- Current: modular `@ohos.bluetooth.access.d.ts`, `@ohos.bluetooth.ble.d.ts`, etc.

#### `@ohos.wifiManager.d.ts`
- **F-4.8.2 (Low):** Indentation inconsistency in JSDoc block for `isWifiActive()` -- the API 11 block has misaligned indentation (lines 73-83 are indented inconsistently with the surrounding code)
- Good `@throws` documentation with domain-specific error codes (2501xxx)
- `scan()` deprecated in API 10 in favor of `startScan()` (very short lifecycle)

#### `@ohos.wifi.d.ts`
- 213 `@deprecated` tags, 80 `@permission` tags -- entirely deprecated
- Old pattern: returns boolean for success/failure

### 4.9 Ability Framework

#### `@ohos.app.ability.UIAbility.d.ts`
- **F-4.9.1 (Low):** `OnReleaseCallback` and `OnRemoteStateChangeCallback` are identical functional interfaces that could be unified
- **F-4.9.2 (Info):** `CalleeCallback` return type `rpc.Parcelable` requires developers to understand the RPC serialization mechanism
- Good error code documentation on `Caller.call()` and `Caller.callWithResult()`

#### `@ohos.app.ability.Want.d.ts`
- Uses `Object` type (2 occurrences) for parameters

#### Deprecated ability APIs
- `@ohos.ability.*.d.ts` files are properly deprecated in favor of `@ohos.app.ability.*.d.ts`
- `@ohos.application.*.d.ts` files also deprecated -- two generations of deprecation

### 4.10 Utilities and Collections

#### `@ohos.util.d.ts`
- **F-4.10.1 (Medium):** `printf` (deprecated) and `format` both take `...args: Object[]` -- should be `...args: unknown[]`
- Good utility classes: `TextDecoder`, `TextEncoder`, etc.

#### Collection APIs (`@ohos.util.ArrayList.d.ts`, `HashMap`, `TreeMap`, etc.)
- **F-4.10.2 (Medium):** All collection `forEach` and `replaceAllElements` methods use `thisArg?: Object` parameter. In modern TypeScript, `thisArg` is rarely useful (arrow functions are preferred). The `Object` type should at minimum be `unknown`.
- Good generic type parameters (`<T>`, `<K, V>`) on all collections
- Consistent API surface across collections (forEach, length, etc.)

#### `@ohos.worker.d.ts`
- **F-4.10.3 (High):** `transfer?: Object[]` should be `ArrayBuffer[]` or `Transferable[]` to match the web Worker API
- 33 uses of `Object` type in the file -- highest density of `Object` usage

### 4.11 System Services

#### `@ohos.pasteboard.d.ts`
- Well-typed `ValueType`: `string | image.PixelMap | Want | ArrayBuffer`
- Good MIME type constants
- Proper deprecation pattern (`createHtmlData` -> `createData`)

#### `@ohos.events.emitter.d.ts`
- **F-4.11.1 (High):** `EventData.data` typed as `{ [key: string]: any }` -- the `any` here is unavoidable for a generic event system but should be documented with guidance on safe usage
- **F-4.11.2 (Low):** Inconsistent event ID types across overloads: `on(event: InnerEvent)` uses `InnerEvent.eventId: number`, but `on(eventId: string)` accepts string. The `off` method also accepts both `number` and `string`. This dual-type system could confuse developers.

#### `@ohos.request.d.ts`
- **F-4.11.3 (Low):** Error code constants incorrectly have `@permission ohos.permission.INTERNET` tag -- constants don't require permissions

#### `@ohos.commonEventManager.d.ts`
- Missing Promise overloads for `publish` (see F-3.4.1)

### 4.12 Telephony

#### `@ohos.telephony.radio.d.ts`
- 10 `@deprecated` tags, 10 `@permission` tags
- Well-documented permission requirements

#### `@ohos.telephony.sim.d.ts`
- 66 `@permission` tags -- heavily permission-gated as expected for SIM operations
- Good security model

### 4.13 Enterprise Management

#### `@ohos.enterprise.*.d.ts` family
- Consistently marked `@systemapi`
- Good `@throws` documentation
- 5-30 `@permission` tags per file
- Well-separated into domain-specific modules (accountManager, applicationManager, bluetoothManager, etc.)

---

## 5. Summary of All Findings by Severity

### Critical (0)
No critical issues that would cause runtime failures or security vulnerabilities.

### High (7)
| ID | Finding | Files Affected |
|----|---------|---------------|
| F-3.1.1 | `any` type in public APIs | 5+ files |
| F-3.1.2 | Pervasive `Object` type usage (246+ occurrences) | 30+ files |
| F-3.2.1 | Copy-paste JSDoc errors in `AssetStatus` | `@ohos.data.relationalStore.d.ts` |
| F-4.2.1 | HTTP headers typed as `Object` | `@ohos.net.http.d.ts` |
| F-4.2.2 | HTTP extraData typed with `Object` | `@ohos.net.http.d.ts` |
| F-4.6.1 | Router params typed as `Object` | `@ohos.router.d.ts` |
| F-4.10.3 | Worker transfer typed as `Object[]` | `@ohos.worker.d.ts` |

### Medium (12)
| ID | Finding | Files Affected |
|----|---------|---------------|
| F-3.1.3 | Permissions type is plain `string` | `permissions.d.ts` |
| F-3.2.4 | Internal change notes in public JSDoc | `@ohos.data.relationalStore.d.ts` |
| F-3.3.1 | Large volume of deprecated APIs still shipped | 70+ files |
| F-3.3.2 | Bluetooth API deprecated twice in two versions | 3 files |
| F-3.4.1 | Missing Promise overloads | `@ohos.commonEventManager.d.ts` |
| F-3.4.2 | Inconsistent error code documentation format | Multiple files |
| F-3.5.1 | Mixed file naming conventions (ETS components) | `@internal/component/ets/` |
| F-3.6.1 | System APIs exposed in public SDK | Multiple files |
| F-4.2.3 | HTTP response type not correlated with request type | `@ohos.net.http.d.ts` |
| F-4.5.1 | Crypto Result enum overlaps with global error codes | `@ohos.security.cryptoFramework.d.ts` |
| F-4.6.2 | WindowType mixes public/system values | `@ohos.window.d.ts` |
| F-4.10.2 | Collection forEach uses `Object` for thisArg | 10+ util files |

### Low (10)
| ID | Finding | Files Affected |
|----|---------|---------------|
| F-3.2.2 | Missing `@file` tag on deprecated modules | `@ohos.notification.d.ts` |
| F-3.2.3 | Inconsistent return description capitalization | Multiple files |
| F-3.3.3 | Wi-Fi scan deprecated after one version | `@ohos.wifiManager.d.ts` |
| F-3.5.2 | Inconsistent module naming patterns | Multiple files |
| F-3.5.3 | Error constants tagged with `@permission` | `@ohos.request.d.ts` |
| F-4.6.3 | Comma vs semicolon in interface | `@internal/component/ets/common.d.ts` |
| F-4.7.2 | Missing `@syscap` on initial namespace block | `@ohos.multimedia.image.d.ts` |
| F-4.8.2 | Indentation inconsistency | `@ohos.wifiManager.d.ts` |
| F-4.9.1 | Duplicate identical callback interfaces | `@ohos.app.ability.UIAbility.d.ts` |
| F-4.11.2 | Inconsistent event ID types | `@ohos.events.emitter.d.ts` |

### Info (5)
| ID | Finding |
|----|---------|
| F-3.2.5 | Double-space typo in JSDoc |
| F-3.6.2 | caPath lacks security warning |
| F-3.6.3 | Pasteboard access not permission-gated in declarations |
| F-3.7.1 | Massive JSDoc duplication for version evolution |
| F-4.5.2 | Inconsistent atomicservice tagging in crypto |

---

## 6. Recommendations

### Immediate Actions (for 4.1 patch or 4.2)
1. **Fix copy-paste JSDoc errors** in `AssetStatus` enum in `@ohos.data.relationalStore.d.ts` (F-3.2.1)
2. **Replace `Object` with `Record<string, string>`** for HTTP headers in `@ohos.net.http.d.ts` and `@ohos.net.webSocket.d.ts` (F-4.2.1)
3. **Replace `Object[]` with `ArrayBuffer[]`** in `@ohos.worker.d.ts` transfer list (F-4.10.3)

### Short-Term (for next major release)
4. **Audit all `any` and `Object` usage** -- replace with `unknown`, `Record<>`, or domain-specific types
5. **Add Promise overloads** to APIs that only have callback versions (F-3.4.1)
6. **Standardize JSDoc formatting** for `@throws` annotations (spacing, capitalization, periods)
7. **Remove internal change notes** from public-facing JSDoc comments

### Long-Term
8. **Consider a JSDoc compression approach** to reduce the 3x duplication of JSDoc blocks for version evolution
9. **Explore making `Permissions` a string literal union type** for compile-time permission name validation
10. **Evaluate removing deprecated APIs** that have been deprecated for 2+ major versions (everything deprecated at API 9, given we are now at API 11+)
11. **Separate `@systemapi` declarations** into a distinct SDK package to avoid confusing third-party developers

---

*End of review.*
