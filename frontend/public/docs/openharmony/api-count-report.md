# OpenHarmony SDK API Count Report

Generated: 2026-03-10

This report provides a comprehensive count of all APIs available to OpenHarmony app
developers across both SDK surfaces: the JS/TS SDK and the C/NDK SDK.

---

## 1. JS/TS SDK APIs

**Location:** `/home/dspfac/openharmony/interface/sdk-js/api/`

### 1.1 File Counts

| Metric | Count |
|--------|------:|
| `.d.ts` declaration files | 666 |
| `.d.ets` declaration files | 24 |
| **Total declaration files** | **690** |

### 1.2 Exported API Element Counts

| API Element Type | Count |
|------------------|------:|
| Exported functions (`export function`) | 30 |
| Exported interfaces (`export interface`) | 686 |
| Exported classes (`export class`) | 43 |
| Exported enums (`export enum`) | 314 |
| Exported type aliases (`export type X =`) | 187 |
| Exported constants/variables (`export const/let/var`) | 2 |
| Interface/class method declarations (member methods) | 7,501 |
| **Total exported top-level API symbols** | **1,262** |
| **Total API surface (including member methods)** | **8,763** |

> Note: The vast majority of JS/TS APIs are exposed as methods within exported
> interfaces and classes (7,501 member methods), rather than as standalone exported
> functions. The 30 exported functions are module-level utility functions.

### 1.3 Breakdown by Top-Level @ohos Namespace

Top-level files at the API root, grouped by the first segment of `@ohos.<namespace>.*`:

| Namespace | File Count |
|-----------|----------:|
| `@ohos.app.*` (ability framework) | 53 |
| `@ohos.arkui.*` | 29 |
| `@ohos.application.*` (legacy) | 20 |
| `@ohos.enterprise.*` | 18 |
| `@ohos.file.*` | 17 |
| `@ohos.data.*` | 17 |
| `@ohos.util.*` | 15 |
| `@ohos.multimodalInput.*` | 14 |
| `@ohos.bluetooth.*` | 14 |
| `@ohos.net.*` | 12 |
| `@ohos.multimedia.*` | 12 |
| `@ohos.bundle.*` | 12 |
| `@ohos.telephony.*` | 7 |
| `@ohos.ability.*` | 6 |
| `@ohos.security.*` | 5 |
| `@ohos.graphics.*` | 5 |
| `@ohos.resourceschedule.*` | 4 |
| `@ohos.advertising.*` | 4 |
| `@ohos.accessibility.*` | 4 |
| `@ohos.nfc.*` | 3 |
| `@ohos.account.*` | 3 |
| `@ohos.userIAM.*` | 2 |
| `@ohos.inputMethod*` | 2 |
| `@ohos.distributedHardware.*` | 2 |
| `@ohos.ai.*` | 2 |
| Other single-file modules (window, web, rpc, i18n, etc.) | 99 |

### 1.4 Breakdown by Subdirectory

Files within subdirectories of the `api/` folder:

| Subdirectory | Declaration Files |
|--------------|------------------:|
| `@internal/` (ETS components) | 127 |
| `application/` | 51 |
| `bundleManager/` | 19 |
| `bundle/` | 13 |
| `notification/` | 12 |
| `common/` | 12 |
| `ability/` | 7 |
| `arkui/` | 6 |
| `commonEvent/` | 4 |
| `multimedia/` | 3 |
| `app/` | 3 |
| `wantAgent/` | 2 |
| `tag/` | 2 |
| `global/` | 2 |
| `continuation/` | 2 |
| `security/` | 1 |
| `data/` | 1 |
| `advertising/` | 1 |

---

## 2. C/NDK SDK APIs

**Location:** `/home/dspfac/openharmony/interface/sdk_c/`

### 2.1 File Counts

| Metric | Count |
|--------|------:|
| **Total header files (`.h`)** | **382** |

### 2.2 API Element Counts

| API Element Type | Count |
|------------------|------:|
| Function declarations/prototypes | 4,085 |
| Struct definitions | 2,471 |
| Enum definitions | 457 |
| Typedef declarations | 5,077 |
| `#define` macros (excluding include guards) | 15,746 |
| **Total C/NDK API elements** | **27,836** |

### 2.3 Breakdown by Subsystem Directory

| Subsystem | Headers | Functions | Structs | Enums | Typedefs | #defines |
|-----------|--------:|----------:|--------:|------:|---------:|---------:|
| `third_party/` (musl, OpenGL ES, EGL, Vulkan, libuv, zlib, OpenSL ES, mindspore, node) | 251 | 3,333 | 2,071 | 263 | 4,628 | 15,638 |
| `multimedia/` (camera, audio, media, image, AV codec, DRM) | 46 | 285 | 170 | 73 | 182 | 22 |
| `graphic/` (2D drawing, native_window, native_buffer, native_image, vsync) | 27 | 194 | 43 | 27 | 60 | 15 |
| `resourceschedule/` (FFRT) | 7 | 39 | 1 | 7 | 19 | 3 |
| `distributeddatamgr/` (relational store) | 7 | 35 | 13 | 12 | 32 | 3 |
| `security/` (HUKS, Asset) | 6 | 15 | 61 | 29 | 13 | 17 |
| `network/` (net connection, WebSocket, SSL) | 6 | 17 | 31 | 4 | 15 | 7 |
| `hiviewdfx/` (HiLog, HiTrace, HiAppEvent) | 6 | 27 | 1 | 3 | 4 | 15 |
| `sensors/` (sensor, vibrator) | 4 | 22 | 7 | 4 | 13 | 0 |
| `drivers/` (USB DDK, HID DDK) | 4 | 14 | 36 | 9 | 28 | 0 |
| `arkui/` (NAPI, XComponent) | 4 | 17 | 5 | 10 | 20 | 7 |
| `startup/` (deviceinfo, syscap) | 3 | 5 | 0 | 0 | 0 | 6 |
| `global/` (resource management, raw files) | 3 | 19 | 8 | 0 | 6 | 0 |
| `ai/` (neural network runtime) | 3 | 43 | 10 | 8 | 21 | 0 |
| `ark_runtime/` (JSVM) | 2 | 4 | 11 | 8 | 29 | 13 |
| `web/` (ArkWeb native) | 1 | 7 | 0 | 0 | 0 | 0 |
| `commonlibrary/` (purgeable memory) | 1 | 6 | 1 | 0 | 2 | 0 |
| `bundlemanager/` (bundle framework) | 1 | 4 | 2 | 0 | 1 | 0 |

---

## 3. Grand Total Summary

| SDK Surface | Declaration/Header Files | Top-Level Symbols | Total API Surface |
|-------------|-------------------------:|-----------------:|------------------:|
| JS/TS SDK | 690 | 1,262 exported symbols | 8,763 (incl. methods) |
| C/NDK SDK | 382 | 27,836 elements | 27,836 |
| **Combined** | **1,072** | **29,098** | **36,599** |

### Notes on Counting Methodology

- **JS/TS SDK**: "Exported symbols" counts top-level `export function`, `export interface`,
  `export class`, `export enum`, `export type`, and `export const/let/var` declarations.
  "Total API surface" adds interface/class member method declarations (lines matching
  method signature patterns inside types).
- **C/NDK SDK**: Counts include all function prototypes, struct definitions, enum definitions,
  typedef declarations, and `#define` macros (excluding include guards). The `third_party/`
  directory contains standard C library (musl libc), OpenGL ES, EGL, Vulkan, libuv, zlib,
  OpenSL ES, MindSpore Lite, and Node-API headers -- these are large but are part of the
  shipped NDK surface.
- **#define macros**: Include guards (patterns ending in `_H` or `_H_`) are excluded.
  The remaining 15,746 macros include API constants, feature flags, GL/Vulkan/EGL
  constants, and function-like macros.
- All counts are based on pattern matching against declaration files and may include
  minor over/under-counts due to comments, conditional compilation, or unusual formatting.
