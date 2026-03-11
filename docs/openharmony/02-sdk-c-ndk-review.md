# OpenHarmony 4.1 Release -- C/Native SDK (NDK) API Headers Review

**Review Date:** 2026-03-10
**Scope:** `/home/dspfac/openharmony/interface/sdk_c/`
**Total Header Files Reviewed:** 382 (excluding third_party musl/OpenGL/Vulkan/zlib which are upstream)

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Cross-Cutting Findings](#2-cross-cutting-findings)
3. [Subsystem Reviews](#3-subsystem-reviews)
   - [3.1 AI (Neural Network Runtime)](#31-ai-neural-network-runtime)
   - [3.2 ArkRuntime (JSVM)](#32-arkruntime-jsvm)
   - [3.3 ArkUI (XComponent, NAPI)](#33-arkui-xcomponent-napi)
   - [3.4 BundleManager](#34-bundlemanager)
   - [3.5 CommonLibrary (Purgeable Memory)](#35-commonlibrary-purgeable-memory)
   - [3.6 DistributedDataMgr (Relational Store)](#36-distributeddatamgr-relational-store)
   - [3.7 Drivers (USB DDK, HID DDK)](#37-drivers-usb-ddk-hid-ddk)
   - [3.8 Global (Resource Management)](#38-global-resource-management)
   - [3.9 Graphic (Drawing, NativeBuffer, NativeWindow)](#39-graphic-drawing-nativebuffer-nativewindow)
   - [3.10 HiviewDFX (HiLog, HiAppEvent, HiTrace)](#310-hiviewdfx-hilog-hiappevent-hitrace)
   - [3.11 Multimedia (Audio, AV Codec, Camera, Image, Player, DRM)](#311-multimedia)
   - [3.12 Network (NetManager, NetSSL, WebSocket)](#312-network)
   - [3.13 ResourceSchedule (FFRT)](#313-resourceschedule-ffrt)
   - [3.14 Security (HUKS, Asset)](#314-security-huks-asset)
   - [3.15 Sensors](#315-sensors)
   - [3.16 Startup](#316-startup)
   - [3.17 Web (ArkWeb)](#317-web-arkweb)
4. [Summary Table](#4-summary-table)

---

## 1. Executive Summary

The OpenHarmony 4.1 NDK C API headers represent a large surface area spanning 18 subsystems. The overall quality is mixed: some subsystems (Security/Asset, Sensors) demonstrate clean API design with opaque types, consistent naming, and proper error codes, while others exhibit significant issues that could cause developer confusion, ABI breakage, or security vulnerabilities.

**Critical findings:** 3
**High findings:** 12
**Medium findings:** 22
**Low/Info findings:** 18

Key systemic issues:
- C++ headers masquerading as C headers (using `<cstdint>`, `<string>`, C++ references, `typedef enum : int8_t`)
- Inconsistent error code strategies across subsystems (some use int, some use enums, some use negative values, some use large positive values)
- Exposed struct internals that freeze ABI (OH_Rdb_Config with `#pragma pack(1)`, BufferHandle, OH_Cursor vtable)
- Missing `const` qualifiers on several parameters
- Several headers lack include guards for C vs C++ compilation

---

## 2. Cross-Cutting Findings

### 2.1 C++ Constructs in C API Headers [Critical]

**Severity: Critical**

Multiple headers that claim to be C API headers use C++-only constructs, making them uncompilable with a C compiler:

| File | C++ Construct |
|------|--------------|
| `neural_network_runtime_type.h` | `#include <cstddef>`, `#include <cstdint>`, `typedef enum : int8_t` |
| `oh_cursor.h` | `#include <cstdint>` |
| `raw_file.h` | `#include <string>`, C++ reference parameters (`&descriptor`) |
| `native_interface_xcomponent.h` | `const uint32_t` at file scope (not valid C until C23), `#include <vector>` |

These are NDK headers intended for C developers. Using C++ constructs makes them incompatible with pure C compilation, which is a fundamental contract violation.

**Recommendation:** Replace `<cstdint>` with `<stdint.h>`, `<cstddef>` with `<stddef.h>`, remove `<string>` and `<vector>` includes, change C++ references to pointer parameters, and use standard C enum syntax.

### 2.2 Inconsistent Error Code Strategies [High]

**Severity: High**

Each subsystem defines its own error code scheme with no common pattern:

| Subsystem | Success | Failure Range | Type |
|-----------|---------|---------------|------|
| Neural Network | `OH_NN_SUCCESS = 0` | 1-14 (positive) | Enum |
| XComponent | `0` | -1, -2 (negative) | Anonymous enum |
| USB DDK | `0` | -1 to -7 (negative) | Enum |
| RDB Store | `RDB_OK = 0` | -1, 801, 14800000+ | Enum |
| AV Errors | `AV_ERR_OK = 0` | 1-9 (positive) | Enum |
| Audio Stream | `0` | 1-3 (positive) | Enum |
| Asset | `0` | 201, 401, 24000001+ | Enum |
| Net Connection | `0` | 201, 401, 2100002+ | int32_t |
| HID DDK | `0` | -1 to -6 (negative) | Enum |

This lack of consistency forces developers to learn different error handling patterns for each subsystem. Some use positive errors, some negative, some embed permission error codes (201, 401) directly.

**Recommendation:** Establish a unified `OH_Error` or `OH_Status` base error code enum and subsystem-specific ranges. At minimum, standardize on whether errors are positive or negative.

### 2.3 Inconsistent Naming Conventions [Medium]

**Severity: Medium**

The NDK has multiple naming patterns:

- **Prefix styles:** `OH_` (most), `OHOS_` (`OHOS_NetConn_RegisterDnsResolver`), no prefix (`canIUse` in syscap_ndk.h), `ffrt_` (FFRT)
- **Enum naming:** Some use `SCREAMING_CASE` (`AUDIOSTREAM_SUCCESS`), some use `PascalCase` with prefix (`OH_NN_SUCCESS`), some use `camelCase` (`ffrt_qos_inherit`)
- **Type naming:** Mix of `OH_TypeName`, `TypeName` (no prefix), and `ffrt_type_name_t`

Notable inconsistency in network module: `OH_NetConn_HasDefaultNet` vs `OHOS_NetConn_RegisterDnsResolver` -- two different prefixes in the same header file.

### 2.4 Missing Thread Safety Documentation [Medium]

**Severity: Medium**

Most headers lack any thread safety documentation. Notable exceptions:
- Neural Network Runtime explicitly states "do not support multi-thread calling"
- FFRT mutex/condition variable types imply concurrency support

All other subsystems leave thread safety unspecified, requiring developers to guess or discover behavior through trial and error.

---

## 3. Subsystem Reviews

### 3.1 AI (Neural Network Runtime)

**Files:** `neural_network_runtime.h`, `neural_network_core.h`, `neural_network_runtime_type.h`

#### C++ in C Header [Critical]
- `neural_network_runtime_type.h` line 42-43: `#include <cstddef>` and `#include <cstdint>` -- these are C++ headers. Must use `<stddef.h>` and `<stdint.h>`.
- `neural_network_runtime_type.h` line 233: `typedef enum : int8_t` -- C++ scoped enum base type specifier is not valid C. Standard C enums cannot specify an underlying type.

#### Typo in Doxygen Group Name [Low]
- `@addtogroup NeuralNeworkRuntime` -- "Nework" should be "Network" throughout all three files.

#### Typo in Error Code Name [Medium]
- `OH_NN_UNAVALIDABLE_DEVICE = 7` -- "UNAVALIDABLE" is clearly a typo for "UNAVAILABLE". While deprecated with `@useinstead OH_NN_UNAVAILABLE_DEVICE`, the original name persists in the ABI and demonstrates insufficient review before release.

#### Deprecated API Migration [Info]
- Several APIs deprecated since API 11 with proper `@useinstead` annotations: `OH_NNModel_AddTensor`, `OH_NNExecutor_SetInput/SetOutput/Run`, `OH_NNExecutor_AllocateInputMemory/AllocateOutputMemory`, `OH_NNExecutor_DestroyInputMemory/DestroyOutputMemory`, `OH_NNExecutor_SetInputWithMemory/SetOutputWithMemory`.
- Good practice: each deprecated API has a clear replacement.

#### Memory Management [Info]
- Good: Uses double-pointer pattern for destroy functions (`OH_NNModel_Destroy(OH_NNModel **model)`) which nullifies the pointer after destruction.
- Good: Opaque typedef pattern used for all handles.

### 3.2 ArkRuntime (JSVM)

**Files:** `jsvm.h`, `jsvm_types.h`

#### Inconsistent Comment for Opaque Types [Low]
- `JSVM_VM` described as "To represent a JavaScript VM instance" (line 65)
- `JSVM_Env` also described as "To represent a JavaScript VM instance" (line 93) -- should say "JavaScript VM environment"
- `JSVM_Value` described as "To represent a JavaScript VM environment" (line 100) -- should say "JavaScript value"
- These are copy-paste documentation errors.

#### Platform-Specific Type Definition [Low]
- `jsvm_types.h` line 49: `typedef uint16_t char16_t;` conditionally defined. This may conflict with compiler-provided `char16_t` in C11/C23.

#### API Design Quality [Info]
- Clean separation between types (`jsvm_types.h`) and API functions (`jsvm.h`).
- Consistent `JSVM_Status` return type for all functions.
- Good use of opaque pointer handles via incomplete struct types (`struct JSVM_VM__*`).
- JSVM_VERSION macro allows version-aware compilation.

### 3.3 ArkUI (XComponent, NAPI)

**Files:** `native_interface_xcomponent.h`, `native_xcomponent_key_event.h`, `native_api.h`, `common.h`

#### C++ Constructs in C Header [Critical]
- `native_interface_xcomponent.h` line 43: `#include <vector>` inside `#ifdef __cplusplus` guard.
- `native_interface_xcomponent.h` lines 53-54: `const uint32_t OH_XCOMPONENT_ID_LEN_MAX = 128;` -- In C, `const` at file scope does not create a compile-time constant suitable for array sizing. Should use `#define` or `enum`.

#### Duplicate Enum Values for Same Concept [Medium]
- `OH_NativeXComponent_TouchPointToolType` (since API 9) and `OH_NativeXComponent_TouchEvent_SourceTool` (since API 10) define nearly identical values (finger, pen, rubber, brush, etc.) with different names. This is confusing API duplication.

#### Struct Field Typo [Low]
- `OH_NativeXComponent_HistoricalPoint` fields `titlX` and `titlY` -- should be `tiltX` and `tiltY`. Since this is part of the ABI, it cannot be easily fixed.

#### ABI Stability: Exposed Structs [High]
- `OH_NativeXComponent_TouchEvent` exposes fixed-size array `touchPoints[OH_MAX_TOUCH_POINTS_NUMBER]`. Changing `OH_MAX_TOUCH_POINTS_NUMBER` would break ABI.
- `OH_NativeXComponent_HistoricalPoint`, `OH_NativeXComponent_TouchPoint`, `OH_NativeXComponent_TouchEvent`, `OH_NativeXComponent_MouseEvent` are all exposed structs with no versioning or size field. Adding fields will break ABI.

#### NAPI native_api.h: Unconditional NAPI_EXPERIMENTAL [Medium]
- Line 24: `#define NAPI_EXPERIMENTAL` is unconditionally defined, which means all experimental Node-API features are always enabled. This may expose unstable APIs to developers who don't expect them.

#### common.h: Wrong Include Guard Comment [Low]
- Line 26: `#endif /* FOUNDATION_ACE_NAPI_INTERFACES_KITS_NAPI_NATIVE_API_H */` -- references wrong file name; should reference `COMMON_H`.

### 3.4 BundleManager

**File:** `native_interface_bundle.h`

#### Memory Ownership Ambiguity [High]
- `OH_NativeBundle_GetCurrentApplicationInfo()` returns `OH_NativeBundle_ApplicationInfo` by value. The struct contains `char* bundleName` and `char* fingerprint`. It is completely unclear who owns these pointers and how to free them. The documentation says nothing about memory management.
- `OH_NativeBundle_GetAppId()` and `OH_NativeBundle_GetAppIdentifier()` return `char*` with documentation stating "it is necessary to manually release the pointer returned by the interface" but does not specify the deallocation function (free? OH-specific free?).

#### Exposed Struct with char* Fields [Medium]
- `OH_NativeBundle_ApplicationInfo` contains raw `char*` fields rather than using opaque types. This ties the ABI to the struct layout and makes ownership semantics ambiguous.

### 3.5 CommonLibrary (Purgeable Memory)

**File:** `purgeable_memory.h`

#### Documentation Uses Wrong Comment Style [Low]
- All documentation uses `/* */` block comments instead of `/** */` Doxygen comments. The `@brief` tags inside `/* */` will not be processed by Doxygen.

#### Destroy Function Takes Single Pointer [Medium]
- `OH_PurgeableMemory_Destroy(OH_PurgeableMemory *purgObj)` takes a single pointer. Documentation says it sets `*purgObj` to NULL, which is impossible with a single pointer parameter. This is either a documentation error or an API design error. Should take `OH_PurgeableMemory **purgObj` (double pointer) to be able to nullify the caller's pointer.

#### API Design Quality [Info]
- Good read/write locking pattern with Begin/End pairs.
- Good use of opaque type (`typedef struct PurgMem OH_PurgeableMemory`).
- Modifier chain pattern is well-designed for data recovery.

### 3.6 DistributedDataMgr (Relational Store)

**Files:** `relational_store.h`, `relational_store_error_code.h`, `oh_cursor.h`, `oh_predicates.h`, `oh_value_object.h`, `oh_values_bucket.h`, `data_asset.h`

#### C++ Header in C API [High]
- `oh_cursor.h` line 40: `#include <cstdint>` -- C++ header. Should be `<stdint.h>`.

#### ABI Fragility: #pragma pack(1) on Configuration Struct [High]
- `relational_store.h` lines 102-139: `OH_Rdb_Config` uses `#pragma pack(1)`. This forces byte-aligned packing, which:
  - Causes unaligned memory access on architectures that don't support it
  - Creates ABI incompatibility if callers compile without matching pack pragma
  - The `selfSize` field suggests versioning intent, but `#pragma pack(1)` makes this brittle

#### ABI Fragility: Exposed Virtual Table Pattern [High]
- `OH_Cursor` is a struct with function pointer fields (vtable pattern). This means:
  - Adding new methods breaks ABI (sizeof changes)
  - Field ordering is frozen forever
  - The `id` field at offset 0 is unusual for a vtable struct
  - New methods added in API 11 (`getAsset`, `getAssets`) extend the struct, which would break any code that allocates or copies `OH_Cursor` structs

#### Inconsistent Error Handling [Medium]
- `OH_Rdb_Insert` returns rowId on success or error code on failure. Since both positive rowIds and error codes can be returned, there is no clean way to distinguish success from `RDB_ERR = -1`. Also, `RDB_E_NOT_SUPPORTED = 801` is far from the main error range `E_BASE = 14800000`.

#### Naming Inconsistency in Struct Fields [Low]
- `Rdb_ChangeInfo.ChangeType` uses PascalCase while all other fields use camelCase.

#### Documentation Typos [Low]
- `relational_store.h` line 241: `OH__VBucket` (double underscore) in @param documentation.
- `relational_store.h` line 879: "synchornizaiton" (typo for "synchronization"), appears multiple times.

### 3.7 Drivers (USB DDK, HID DDK)

**Files:** `usb_ddk_api.h`, `usb_ddk_types.h`, `hid_ddk_api.h`, `hid_ddk_types.h`

#### Inconsistent Alignment Attributes [Medium]
- `UsbControlRequestSetup` uses `__attribute__((aligned(8)))` while it only contains uint8_t and uint16_t fields (8 bytes total). The alignment to 8 bytes wastes space.
- `UsbDeviceDescriptor` also uses `__attribute__((aligned(8)))` but contains only 18 bytes of fields. This pads to 24 bytes.
- `UsbConfigDescriptor` uses `__attribute__((packed))` (no padding) while `UsbDeviceDescriptor` uses `aligned(8)`. This inconsistency within the same subsystem is confusing.
- `UsbRequestPipe` uses `__attribute__((aligned(8)))` with total data of 13 bytes.

**Rationale concern:** The USB protocol structures have defined byte layouts from the USB specification. Using `packed` is correct for wire format matching. Using `aligned(8)` is incorrect for wire format structures and introduces unnecessary padding.

#### ABI Stability: UsbDeviceMemMap Exposed [Medium]
- `UsbDeviceMemMap` exposes `uint8_t * const address` and `const size_t size` as struct fields. The `const` qualifier on struct members means the struct cannot be zero-initialized easily and creates assignment complications.

#### Closing Brace Placement for @} [Low]
- `usb_ddk_types.h`: `/** @} */` is placed after `#ifdef __cplusplus` closing brace but before `#endif`, meaning the Doxygen group ends inside the C++ conditional.

#### HID DDK: Fixed-Size Arrays in Public Struct [Medium]
- `Hid_EventProperties` contains four `int32_t[64]` arrays (`hidAbsMax`, `hidAbsMin`, `hidAbsFuzz`, `hidAbsFlat`). This freezes the maximum number of absolute axes at 64 and wastes 1024 bytes per instance even if few axes are used.

### 3.8 Global (Resource Management)

**Files:** `raw_file.h`, `raw_file_manager.h`, `raw_dir.h`

#### C++ Constructs in C Header [Critical]
- `raw_file.h` line 42: `#include <string>` -- C++ standard library header in a C API file.
- `raw_file.h` line 200: `OH_ResourceManager_GetRawFileDescriptor(const RawFile *rawFile, RawFileDescriptor &descriptor)` -- uses C++ reference parameter `&descriptor`. This is not valid C.
- `raw_file.h` line 212: `OH_ResourceManager_ReleaseRawFileDescriptor(const RawFileDescriptor &descriptor)` -- same issue.

This means these APIs are **only callable from C++**, completely defeating the purpose of being a C API.

#### `long` Type for File Sizes [Medium]
- Original `RawFile` functions use `long` for offsets and sizes (`OH_ResourceManager_GetRawFileSize`, `OH_ResourceManager_SeekRawFile`, etc.). On 32-bit systems, `long` is 32 bits, limiting file sizes to 2 GB. The `RawFile64` variants (API 11) correctly use `int64_t`.
- However, `RawFileDescriptor` still uses `long` for `start` and `length` fields while `RawFileDescriptor64` correctly uses `int64_t`.

### 3.9 Graphic (Drawing, NativeBuffer, NativeWindow)

**Files:** 27 headers across native_drawing, native_buffer, native_image, native_window, native_vsync

#### NativeBuffer: Incomplete Error Code Documentation [Medium]
- `OH_NativeBuffer_Reference`, `OH_NativeBuffer_Unreference`, `OH_NativeBuffer_Map`, `OH_NativeBuffer_Unmap`, `OH_NativeBuffer_SetColorSpace` all say "Returns an error code, 0 is success, otherwise, failed" without specifying what error codes are possible or referencing an error code enum.

#### NativeWindow: Variadic Function for Properties [High]
- `OH_NativeWindow_NativeWindowHandleOpt(OHNativeWindow *window, int code, ...)` uses a variadic argument list. This is inherently type-unsafe since:
  - No compile-time type checking on the variable arguments
  - The `NativeWindowOperation` enum documents the expected types only in comments
  - `GET_BUFFER_GEOMETRY` documents parameters as `[out] int32_t *height, [out] int32_t *width` -- note height before width, which is counterintuitive and error-prone
  - Easy to pass wrong types, leading to undefined behavior

#### NativeWindow: Void Pointer for Reference Counting [Medium]
- `OH_NativeWindow_NativeObjectReference(void *obj)` and `OH_NativeWindow_NativeObjectUnreference(void *obj)` accept `void*` and state they work on both `OHNativeWindow` and `OHNativeWindowBuffer`. This provides no type safety and allows passing arbitrary pointers.

#### BufferHandle: Exposed Struct with Flexible Array Member [Medium]
- `BufferHandle` exposes `int32_t reserve[0]` (zero-length array), which is a GCC extension (C99 would use `int32_t reserve[]`). Also exposes `void *virAddr` and `uint64_t phyAddr` -- physical addresses should not be exposed in a user-space API.

#### Drawing API: Good Opaque Type Design [Info]
- `drawing_types.h` consistently uses opaque types (`typedef struct OH_Drawing_Canvas OH_Drawing_Canvas`).
- Clean separation of types and functions across drawing_*.h files.
- Enum values follow consistent naming but lack the `OH_` prefix (e.g., `BLEND_MODE_CLEAR` rather than `OH_DRAWING_BLEND_MODE_CLEAR`), creating potential name collisions.

#### NativeWindow: Deprecated APIs Without Proper Doxygen [Low]
- `OHScalingMode`, `OHHDRMetadataKey`, `OHHDRMetaData`, `OHExtDataHandle` use `@deprecated(since = "10")` format which is non-standard. Standard Doxygen uses `@deprecated` followed by description text. Several deprecated functions on these types use the same format.

### 3.10 HiviewDFX (HiLog, HiAppEvent, HiTrace)

**Files:** `log.h`, `hiappevent.h`, `hiappevent_cfg.h`, `hiappevent_event.h`, `hiappevent_param.h`, `trace.h`

#### HiLog: Good API Design [Info]
- Well-designed privacy-aware logging with `{public}` and `{private}` format specifiers.
- Comprehensive documentation with usage examples.
- `__attribute__((__format__(os_log, 5, 6)))` enables compiler format string checking.
- Log callback registration API (`OH_LOG_SetCallback`) is clean.

#### HiLog: LogType Enum Too Restrictive [Low]
- `LogType` enum only has `LOG_APP = 0`. Other types may exist internally. The enum could benefit from a sentinel value or documentation about future extensibility.

### 3.11 Multimedia

**Files:** ~46 headers across audio_framework, av_codec, camera_framework, drm_framework, image_framework, media_foundation, player_framework

#### AV Errors: Spelling Mistake in Enum Comment [Low]
- `native_averrors.h` line 40: `opertation` should be `operation`.

#### AV Errors: Ambiguous Error Ranges [Medium]
- `AV_ERR_EXTEND_START = 100` is declared but there's no documentation or enum defining what the extended errors are. Subsystem-specific errors presumably start at 100+ but are not defined in this header.

#### Audio Stream: Inconsistent Error Code Direction [Low]
- Audio stream uses positive error codes (1, 2, 3) while AV codec uses the same positive direction. At least these two subsystems are internally consistent.

#### Image Framework: Test File in SDK Headers [Low]
- `multimedia/image_framework/ndk_test_example/my_pixel_map.h` -- a test/example file should not be shipped as part of the SDK headers.

### 3.12 Network

**Files:** `net_connection.h`, `net_connection_type.h`, `net_ssl_c.h`, `net_ssl_c_type.h`, `net_websocket.h`, `net_websocket_type.h`

#### Naming Prefix Inconsistency [Medium]
- In `net_connection.h`: `OH_NetConn_HasDefaultNet` uses `OH_` prefix, but `OHOS_NetConn_RegisterDnsResolver` (line 191) and `OHOS_NetConn_UnregisterDnsResolver` (line 204) use `OHOS_` prefix. Two different prefixes in the same file is confusing and suggests the API went through different naming conventions during development.

#### Error Codes Documented Only in Comments [Medium]
- Error return values are documented inline (e.g., `0 - Success. 201 - Missing permissions. 401 - Parameter error.`) rather than referencing a defined error code enum. The values 201 and 401 match HTTP status codes and are reused across different subsystems, but there is no shared error code header.

#### DNS Resolution: Non-const Input Parameters [Low]
- `OH_NetConn_GetAddrInfo(char *host, char *serv, ...)` -- `host` and `serv` should be `const char *` since the function should not modify these inputs.

### 3.13 ResourceSchedule (FFRT)

**Files:** `ffrt.h`, `c/queue.h`, `c/sleep.h`, `c/type_def.h`, `c/task.h`, `c/mutex.h`, `c/condition_variable.h`

#### Non-Standard Naming Convention [Medium]
- FFRT uses `snake_case` with `ffrt_` prefix (`ffrt_mutex_t`, `ffrt_qos_default_t`) while the rest of the NDK uses `OH_PascalCase`. This makes FFRT look like a foreign library rather than part of the OpenHarmony SDK.

#### Opaque Struct Storage Pattern [Info]
- Uses `uint32_t storage[N]` pattern for mutex, condition variable, and attribute types. This is a common technique to avoid heap allocation while keeping the struct opaque. However:
  - If the internal implementation changes to need more storage, ABI breaks silently (no size check at runtime)
  - The storage sizes are hardcoded (`ffrt_mutex_storage_size = 64`) with no versioning mechanism

#### C++ Namespace in C Header [Low]
- `type_def.h` lines 178-188: Contains C++ `namespace ffrt { ... }` block with `using qos = int`. While inside `#ifdef __cplusplus`, this is unusual for a C API header and mixes C and C++ paradigms.

### 3.14 Security (HUKS, Asset)

**Files:** `native_huks_api.h`, `native_huks_type.h`, `native_huks_param.h`, `native_huks_api_adapter.h`, `asset_api.h`, `asset_type.h`

#### HUKS: Magic Number Constants Without Context [Low]
- `native_huks_type.h`: Multiple `#define` constants like `TOKEN_CHALLENGE_LEN 32`, `SHA256_SIGN_LEN 32`, `TOKEN_SIZE 32`, `MAX_AUTH_TIMEOUT_SECOND 60`, `SECURE_SIGN_VERSION 0x01000001` lack the `OH_HUKS_` prefix, risking name collisions with user code.

#### HUKS: Enum Without Explicit Values [Low]
- `OH_Huks_KeyPurpose` uses power-of-2 values (1, 2, 4, 8, ...) which is correct for bitmask usage, but the enum members are not documented as combinable flags.

#### Asset API: Good Design [Info]
- Clean tag-value attribute system with type safety encoded in tag values.
- Proper memory management with explicit free functions (`OH_Asset_FreeBlob`, `OH_Asset_FreeResultSet`).
- Clear error codes with meaningful ranges.
- Well-documented @param tags for all functions.

#### Asset API: Documentation Bug [Low]
- `asset_api.h` lines 54-55: Two `@param` entries both named `attributes` -- the second should be `@param attrCnt`.
- Same pattern in `OH_Asset_Update` documentation (line 76): `@param attributes` appears twice.

### 3.15 Sensors

**Files:** `oh_sensor.h`, `oh_sensor_type.h`, `vibrator.h`, `vibrator_type.h`

#### Good API Design [Info]
- Clean opaque type usage with `Sensor_Info`, `Sensor_SubscriptionId`, etc.
- Consistent `Sensor_Result` return type.
- Proper permission documentation with `@permission` tags.
- Subscribe/Unsubscribe pattern is clean and symmetric.

### 3.16 Startup

**Files:** `syscap_ndk.h`, `deviceinfo.h`, `init_sync.h`

#### No Naming Prefix [High]
- `syscap_ndk.h` line 27: `bool canIUse(const char *cap)` -- this function has no `OH_` prefix and uses camelCase, completely inconsistent with all other NDK APIs. `canIUse` is a generic name likely to collide with user code.

#### No Documentation [Medium]
- `syscap_ndk.h` has zero documentation -- no `@brief`, no `@param`, no `@return`, no `@since` tags on the function itself. Only the include guard and license header are present.

#### Redundant `#if __cplusplus` Check [Low]
- `syscap_ndk.h` uses double-guard pattern (`#ifdef __cplusplus` then `#if __cplusplus`). The inner `#if __cplusplus` is redundant and unconventional.

### 3.17 Web (ArkWeb)

**File:** `native_interface_arkweb.h`

#### Missing Return Value Documentation [Medium]
- `OH_NativeArkWeb_RunJavaScript`, `OH_NativeArkWeb_RegisterJavaScriptProxy`, `OH_NativeArkWeb_UnregisterJavaScriptProxy` return `void` with no error indication. If registration or execution fails, the caller has no way to know.

#### Callback Memory Ownership [Medium]
- `NativeArkWeb_OnJavaScriptProxyCallback` returns `char*`. It is unclear who owns this memory. The caller (framework) presumably must free it, but the allocation method is unspecified.

#### Documentation Style [Low]
- All function documentation uses `/* */` comments instead of `/** */` Doxygen comments (missing the leading double-star).

#### Typos in Documentation [Low]
- Line 21: "javascirpt" should be "JavaScript"
- Line 28: "javascirpt" again
- Line 98: "Deletes the registered object which th given name" -- "th" should be "the"

---

## 4. Summary Table

| # | Subsystem | Severity | Issue | File |
|---|-----------|----------|-------|------|
| 1 | AI/NNR | Critical | C++ includes (`<cstddef>`, `<cstdint>`) and `enum : int8_t` in C header | `neural_network_runtime_type.h` |
| 2 | Global | Critical | C++ `#include <string>` and reference parameters `&` in C API | `raw_file.h` |
| 3 | ArkUI | Critical | `const` variables at file scope, `#include <vector>` | `native_interface_xcomponent.h` |
| 4 | RDB | High | C++ `#include <cstdint>` in C header | `oh_cursor.h` |
| 5 | RDB | High | `#pragma pack(1)` on `OH_Rdb_Config` creates ABI fragility | `relational_store.h` |
| 6 | RDB | High | `OH_Cursor` vtable struct is ABI-fragile (extended in API 11) | `oh_cursor.h` |
| 7 | Graphic | High | Variadic `OH_NativeWindow_NativeWindowHandleOpt` is type-unsafe | `external_window.h` |
| 8 | Bundle | High | `OH_NativeBundle_GetCurrentApplicationInfo` returns struct with unowned `char*` pointers | `native_interface_bundle.h` |
| 9 | Startup | High | `canIUse()` -- no prefix, no documentation, collision-prone name | `syscap_ndk.h` |
| 10 | ArkUI | High | ABI-frozen exposed structs (TouchEvent, MouseEvent) with no versioning | `native_interface_xcomponent.h` |
| 11 | Cross | High | Inconsistent error code strategies across all subsystems | Multiple |
| 12 | Network | High | Mixed `OH_`/`OHOS_` prefixes in same header | `net_connection.h` |
| 13 | Cross | Medium | Inconsistent naming conventions across subsystems | Multiple |
| 14 | Cross | Medium | Missing thread safety documentation | Multiple |
| 15 | ArkUI | Medium | Duplicate enums for same concept (ToolType vs SourceTool) | `native_interface_xcomponent.h` |
| 16 | ArkUI | Medium | Unconditional `NAPI_EXPERIMENTAL` enables unstable APIs | `native_api.h` |
| 17 | CommonLib | Medium | `OH_PurgeableMemory_Destroy` takes single pointer but claims to nullify it | `purgeable_memory.h` |
| 18 | RDB | Medium | `OH_Rdb_Insert` conflates rowId and error code in return value | `relational_store.h` |
| 19 | Drivers | Medium | Inconsistent alignment attributes (`packed` vs `aligned(8)`) | `usb_ddk_types.h` |
| 20 | Drivers | Medium | Fixed-size arrays in `Hid_EventProperties` (1024 bytes wasted) | `hid_ddk_types.h` |
| 21 | Graphic | Medium | `void*` parameter for reference counting (no type safety) | `external_window.h` |
| 22 | Graphic | Medium | `BufferHandle` exposes physical address in user-space API | `buffer_handle.h` |
| 23 | Graphic | Medium | NativeBuffer error returns undocumented | `native_buffer.h` |
| 24 | Graphic | Medium | Enum values lack `OH_` prefix, risking collisions | `drawing_types.h` |
| 25 | Network | Medium | Error codes only in comments, not in an enum | `net_connection.h` |
| 26 | Network | Medium | Non-const `char*` parameters in DNS function | `net_connection.h` |
| 27 | FFRT | Medium | Non-standard `snake_case` naming convention | `c/type_def.h` |
| 28 | AV | Medium | `AV_ERR_EXTEND_START = 100` with no defined extended errors | `native_averrors.h` |
| 29 | Global | Medium | `long` type for file sizes limits to 2 GB on 32-bit | `raw_file.h` |
| 30 | Web | Medium | `void` return with no error indication on JS proxy operations | `native_interface_arkweb.h` |
| 31 | Web | Medium | Callback return `char*` with unspecified ownership | `native_interface_arkweb.h` |
| 32 | Startup | Medium | Zero documentation on `canIUse` function | `syscap_ndk.h` |
| 33 | Bundle | Medium | Exposed struct with raw `char*` fields | `native_interface_bundle.h` |
| 34 | Security | Medium | Asset API `@param` documentation duplicated | `asset_api.h` |
| 35 | AI/NNR | Low | "NeuralNeworkRuntime" typo in Doxygen group name | All NNR files |
| 36 | AI/NNR | Medium | `OH_NN_UNAVALIDABLE_DEVICE` typo in enum name (ABI-frozen) | `neural_network_runtime_type.h` |
| 37 | ArkUI | Low | `titlX`/`titlY` field name typos (ABI-frozen) | `native_interface_xcomponent.h` |
| 38 | ArkUI | Low | Wrong include guard end comment in `common.h` | `common.h` |
| 39 | JSVM | Low | Copy-paste documentation errors on opaque types | `jsvm_types.h` |
| 40 | RDB | Low | `ChangeType` field PascalCase inconsistent with siblings | `relational_store.h` |
| 41 | RDB | Low | "synchornizaiton" typo repeated in docs | `relational_store.h` |
| 42 | CommonLib | Low | Wrong comment style (`/* */` vs `/** */`) for Doxygen | `purgeable_memory.h` |
| 43 | Security | Low | HUKS constants lack `OH_HUKS_` prefix | `native_huks_type.h` |
| 44 | Drivers | Low | `/** @} */` placement inside `#ifdef __cplusplus` block | `usb_ddk_types.h` |
| 45 | Graphic | Low | Non-standard `@deprecated(since = "10")` format | `external_window.h` |
| 46 | HiLog | Low | `LogType` enum only has one value | `log.h` |
| 47 | AV | Low | "opertation" typo in error code comment | `native_averrors.h` |
| 48 | Multimedia | Low | Test file shipped in SDK headers | `my_pixel_map.h` |
| 49 | FFRT | Low | C++ namespace in C header | `c/type_def.h` |
| 50 | Startup | Low | Redundant `#if __cplusplus` guard pattern | `syscap_ndk.h` |
| 51 | Web | Low | "javascirpt" typo in docs, wrong comment style | `native_interface_arkweb.h` |
| 52 | Security | Info | Asset API well-designed with tag-value pattern | `asset_api.h`, `asset_type.h` |
| 53 | AI/NNR | Info | Good deprecated API migration with `@useinstead` | NNR files |
| 54 | Sensors | Info | Clean opaque types and symmetric subscribe/unsubscribe | `oh_sensor.h` |
| 55 | HiLog | Info | Well-designed privacy-aware logging with examples | `log.h` |

---

## Recommendations

### Immediate Actions (Critical/High)

1. **Fix C++ constructs in C headers:** Replace `<cstdint>`, `<cstddef>`, `<string>` with C equivalents. Replace C++ references with pointers. Remove `enum : int8_t` syntax. This affects `neural_network_runtime_type.h`, `oh_cursor.h`, `raw_file.h`, and `native_interface_xcomponent.h`.

2. **Standardize error code strategy:** Create an `OH_ErrorCode` common header defining shared error code ranges and conventions. Each subsystem should extend from defined base values.

3. **Add `OH_` prefix to `canIUse()`:** Rename to `OH_CanIUse()` or `OH_SystemCapability_IsAvailable()` with proper documentation.

4. **Document memory ownership:** For every function returning a pointer, document who owns the memory and how to free it.

### Short-Term Actions (Medium)

5. **Standardize naming:** Adopt `OH_SubsystemName_FunctionName` consistently. Phase out `OHOS_` prefix and FFRT `snake_case`.

6. **Add thread safety annotations:** Document thread safety for every API, either in function-level docs or in header-level docs.

7. **Version exposed structs:** For structs that must be exposed (like `OH_Rdb_Config`), ensure the `selfSize` pattern is used consistently and remove `#pragma pack(1)`.

8. **Replace variadic property function:** `OH_NativeWindow_NativeWindowHandleOpt` should be replaced with type-safe getter/setter functions.

### Long-Term Actions (Low/Info)

9. **Consolidate duplicate enums** like `OH_NativeXComponent_TouchPointToolType` and `OH_NativeXComponent_TouchEvent_SourceTool`.

10. **Add compile-time CI checks** to ensure all NDK headers compile with both C11 and C++17 compilers.

11. **Fix all ABI-frozen typos** in documentation; accept and alias the code-level typos (`titlX`, `OH_NN_UNAVALIDABLE_DEVICE`) since they cannot be removed without breaking compatibility.
