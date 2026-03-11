# OpenHarmony 4.1 Release -- C/Native SDK (NDK) API 头文件审查

**审查日期：** 2026-03-10
**范围：** `/home/dspfac/openharmony/interface/sdk_c/`
**审查的头文件总数：** 382（不包括 third_party 中的 musl/OpenGL/Vulkan/zlib 等上游库）

---

## 目录

1. [概要总结](#1-概要总结)
2. [跨领域发现](#2-跨领域发现)
3. [子系统审查](#3-子系统审查)
   - [3.1 AI（神经网络运行时）](#31-ai神经网络运行时)
   - [3.2 ArkRuntime (JSVM)](#32-arkruntime-jsvm)
   - [3.3 ArkUI (XComponent, NAPI)](#33-arkui-xcomponent-napi)
   - [3.4 BundleManager](#34-bundlemanager)
   - [3.5 CommonLibrary（可清除内存）](#35-commonlibrary可清除内存)
   - [3.6 DistributedDataMgr（关系型存储）](#36-distributeddatamgr关系型存储)
   - [3.7 Drivers（USB DDK、HID DDK）](#37-driversusb-ddkhid-ddk)
   - [3.8 Global（资源管理）](#38-global资源管理)
   - [3.9 Graphic（绘制、NativeBuffer、NativeWindow）](#39-graphic绘制nativebuffernativewindow)
   - [3.10 HiviewDFX (HiLog, HiAppEvent, HiTrace)](#310-hiviewdfx-hilog-hiappevent-hitrace)
   - [3.11 Multimedia（音频、AV 编解码、相机、图像、播放器、DRM）](#311-multimedia)
   - [3.12 Network（NetManager、NetSSL、WebSocket）](#312-network)
   - [3.13 ResourceSchedule (FFRT)](#313-resourceschedule-ffrt)
   - [3.14 Security（HUKS、Asset）](#314-securityhuks-asset)
   - [3.15 Sensors](#315-sensors)
   - [3.16 Startup](#316-startup)
   - [3.17 Web (ArkWeb)](#317-web-arkweb)
4. [汇总表](#4-汇总表)

---

## 1. 概要总结

OpenHarmony 4.1 NDK C API 头文件覆盖了 18 个子系统，API 涉及面广泛。整体质量参差不齐：部分子系统（Security/Asset、Sensors）展现出良好的 API 设计，具有不透明类型、一致的命名规范和完善的错误码体系；而其他子系统则存在明显问题，可能导致开发者困惑、ABI 破坏或安全漏洞。

**严重问题：** 3 个
**高等级问题：** 12 个
**中等级问题：** 22 个
**低等级/提示性问题：** 18 个

主要系统性问题：
- C++ 头文件伪装为 C 头文件（使用 `<cstdint>`、`<string>`、C++ 引用、`typedef enum : int8_t`）
- 各子系统间错误码策略不一致（有的使用 int，有的使用枚举，有的使用负值，有的使用大正数）
- 暴露的结构体内部细节冻结了 ABI（`OH_Rdb_Config` 使用 `#pragma pack(1)`、`BufferHandle`、`OH_Cursor` 虚表）
- 多个参数缺少 `const` 修饰
- 部分头文件缺少 C 与 C++ 编译的兼容性保护

---

## 2. 跨领域发现

### 2.1 C API 头文件中使用 C++ 语法 [严重]

**严重性：严重**

多个声称是 C API 的头文件使用了 C++ 专有语法，导致无法用 C 编译器编译：

| 文件 | C++ 语法 |
|------|---------|
| `neural_network_runtime_type.h` | `#include <cstddef>`、`#include <cstdint>`、`typedef enum : int8_t` |
| `oh_cursor.h` | `#include <cstdint>` |
| `raw_file.h` | `#include <string>`、C++ 引用参数（`&descriptor`） |
| `native_interface_xcomponent.h` | 文件作用域的 `const uint32_t`（在 C23 之前不合法）、`#include <vector>` |

这些是面向 C 开发者的 NDK 头文件。使用 C++ 语法使其无法用纯 C 编译，这是一个根本性的契约违反。

**建议：** 将 `<cstdint>` 替换为 `<stdint.h>`，`<cstddef>` 替换为 `<stddef.h>`，移除 `<string>` 和 `<vector>` 的包含，将 C++ 引用改为指针参数，并使用标准 C 枚举语法。

### 2.2 错误码策略不一致 [高]

**严重性：高**

各子系统定义了各自的错误码方案，没有统一的模式：

| 子系统 | 成功值 | 失败范围 | 类型 |
|--------|--------|----------|------|
| Neural Network | `OH_NN_SUCCESS = 0` | 1-14（正数） | 枚举 |
| XComponent | `0` | -1, -2（负数） | 匿名枚举 |
| USB DDK | `0` | -1 到 -7（负数） | 枚举 |
| RDB Store | `RDB_OK = 0` | -1, 801, 14800000+ | 枚举 |
| AV Errors | `AV_ERR_OK = 0` | 1-9（正数） | 枚举 |
| Audio Stream | `0` | 1-3（正数） | 枚举 |
| Asset | `0` | 201, 401, 24000001+ | 枚举 |
| Net Connection | `0` | 201, 401, 2100002+ | int32_t |
| HID DDK | `0` | -1 到 -6（负数） | 枚举 |

这种不一致性迫使开发者为每个子系统学习不同的错误处理模式。有些使用正数错误码，有些使用负数，有些直接嵌入权限错误码（201, 401）。

**建议：** 建立统一的 `OH_Error` 或 `OH_Status` 基础错误码枚举和子系统特定的范围。至少应当统一错误码的正负方向。

### 2.3 命名规范不一致 [中]

**严重性：中**

NDK 存在多种命名模式：

- **前缀风格：** `OH_`（大多数）、`OHOS_`（`OHOS_NetConn_RegisterDnsResolver`）、无前缀（syscap_ndk.h 中的 `canIUse`）、`ffrt_`（FFRT）
- **枚举命名：** 有的使用 `SCREAMING_CASE`（`AUDIOSTREAM_SUCCESS`），有的使用带前缀的 `PascalCase`（`OH_NN_SUCCESS`），有的使用 `camelCase`（`ffrt_qos_inherit`）
- **类型命名：** `OH_TypeName`、`TypeName`（无前缀）和 `ffrt_type_name_t` 混用

网络模块中的显著不一致：`OH_NetConn_HasDefaultNet` 与 `OHOS_NetConn_RegisterDnsResolver` -- 同一头文件中使用两种不同的前缀。

### 2.4 缺少线程安全文档 [中]

**严重性：中**

大多数头文件缺乏线程安全相关文档。值得注意的例外：
- Neural Network Runtime 明确声明"不支持多线程调用"
- FFRT 的互斥锁/条件变量类型暗示支持并发

所有其他子系统都未指定线程安全性，开发者只能靠猜测或通过试错来发现行为。

---

## 3. 子系统审查

### 3.1 AI（神经网络运行时）

**文件：** `neural_network_runtime.h`、`neural_network_core.h`、`neural_network_runtime_type.h`

#### C 头文件中使用 C++ 语法 [严重]
- `neural_network_runtime_type.h` 第 42-43 行：`#include <cstddef>` 和 `#include <cstdint>` -- 这是 C++ 头文件，必须使用 `<stddef.h>` 和 `<stdint.h>`。
- `neural_network_runtime_type.h` 第 233 行：`typedef enum : int8_t` -- C++ 作用域枚举基础类型说明符在 C 中无效。标准 C 枚举无法指定底层类型。

#### Doxygen 组名拼写错误 [低]
- `@addtogroup NeuralNeworkRuntime` -- "Nework" 应为 "Network"，在三个文件中都出现了此错误。

#### 错误码名称拼写错误 [中]
- `OH_NN_UNAVALIDABLE_DEVICE = 7` -- "UNAVALIDABLE" 明显是 "UNAVAILABLE" 的拼写错误。虽然已通过 `@useinstead OH_NN_UNAVAILABLE_DEVICE` 标记为弃用，但原始名称仍保留在 ABI 中，表明发布前审查不足。

#### 弃用 API 迁移 [提示]
- 自 API 11 起弃用了多个 API，并附有正确的 `@useinstead` 注解：`OH_NNModel_AddTensor`、`OH_NNExecutor_SetInput/SetOutput/Run`、`OH_NNExecutor_AllocateInputMemory/AllocateOutputMemory`、`OH_NNExecutor_DestroyInputMemory/DestroyOutputMemory`、`OH_NNExecutor_SetInputWithMemory/SetOutputWithMemory`。
- 良好实践：每个弃用的 API 都有明确的替代方案。

#### 内存管理 [提示]
- 良好：销毁函数使用双指针模式（`OH_NNModel_Destroy(OH_NNModel **model)`），在销毁后将指针置空。
- 良好：所有句柄均使用不透明 typedef 模式。

### 3.2 ArkRuntime (JSVM)

**文件：** `jsvm.h`、`jsvm_types.h`

#### 不透明类型注释不一致 [低]
- `JSVM_VM` 描述为"To represent a JavaScript VM instance"（第 65 行）
- `JSVM_Env` 也描述为"To represent a JavaScript VM instance"（第 93 行）-- 应为"JavaScript VM environment"
- `JSVM_Value` 描述为"To represent a JavaScript VM environment"（第 100 行）-- 应为"JavaScript value"
- 这些是复制粘贴导致的文档错误。

#### 平台特定类型定义 [低]
- `jsvm_types.h` 第 49 行：`typedef uint16_t char16_t;` 是有条件定义的。这可能与 C11/C23 编译器提供的 `char16_t` 冲突。

#### API 设计质量 [提示]
- 类型（`jsvm_types.h`）和 API 函数（`jsvm.h`）之间有清晰的分离。
- 所有函数统一使用 `JSVM_Status` 返回类型。
- 通过不完整结构体类型（`struct JSVM_VM__*`）正确使用不透明指针句柄。
- `JSVM_VERSION` 宏支持版本感知的编译。

### 3.3 ArkUI (XComponent, NAPI)

**文件：** `native_interface_xcomponent.h`、`native_xcomponent_key_event.h`、`native_api.h`、`common.h`

#### C 头文件中使用 C++ 语法 [严重]
- `native_interface_xcomponent.h` 第 43 行：在 `#ifdef __cplusplus` 保护内包含 `#include <vector>`。
- `native_interface_xcomponent.h` 第 53-54 行：`const uint32_t OH_XCOMPONENT_ID_LEN_MAX = 128;` -- 在 C 中，文件作用域的 `const` 不会创建适合数组大小的编译时常量。应使用 `#define` 或 `enum`。

#### 同一概念的重复枚举值 [中]
- `OH_NativeXComponent_TouchPointToolType`（自 API 9）和 `OH_NativeXComponent_TouchEvent_SourceTool`（自 API 10）定义了几乎相同的值（手指、笔、橡皮、画笔等），但使用不同的名称。这是令人困惑的 API 重复。

#### 结构体字段拼写错误 [低]
- `OH_NativeXComponent_HistoricalPoint` 字段 `titlX` 和 `titlY` -- 应为 `tiltX` 和 `tiltY`。由于这是 ABI 的一部分，无法轻易修复。

#### ABI 稳定性：暴露的结构体 [高]
- `OH_NativeXComponent_TouchEvent` 暴露了固定大小的数组 `touchPoints[OH_MAX_TOUCH_POINTS_NUMBER]`。更改 `OH_MAX_TOUCH_POINTS_NUMBER` 将破坏 ABI。
- `OH_NativeXComponent_HistoricalPoint`、`OH_NativeXComponent_TouchPoint`、`OH_NativeXComponent_TouchEvent`、`OH_NativeXComponent_MouseEvent` 都是暴露的结构体，没有版本控制或大小字段。添加字段将破坏 ABI。

#### NAPI native_api.h：无条件的 NAPI_EXPERIMENTAL [中]
- 第 24 行：`#define NAPI_EXPERIMENTAL` 被无条件定义，这意味着所有实验性 Node-API 功能始终启用。这可能会向不期望使用这些 API 的开发者暴露不稳定的 API。

#### common.h：错误的包含保护注释 [低]
- 第 26 行：`#endif /* FOUNDATION_ACE_NAPI_INTERFACES_KITS_NAPI_NATIVE_API_H */` -- 引用了错误的文件名；应引用 `COMMON_H`。

### 3.4 BundleManager

**文件：** `native_interface_bundle.h`

#### 内存所有权不明确 [高]
- `OH_NativeBundle_GetCurrentApplicationInfo()` 按值返回 `OH_NativeBundle_ApplicationInfo`。该结构体包含 `char* bundleName` 和 `char* fingerprint`。完全不清楚谁拥有这些指针以及如何释放它们。文档中没有任何关于内存管理的说明。
- `OH_NativeBundle_GetAppId()` 和 `OH_NativeBundle_GetAppIdentifier()` 返回 `char*`，文档声明"需要手动释放接口返回的指针"，但未指明释放函数（free？还是 OH 特定的 free？）。

#### 暴露含 char* 字段的结构体 [中]
- `OH_NativeBundle_ApplicationInfo` 包含原始 `char*` 字段而非使用不透明类型。这将 ABI 绑定到结构体布局，并使所有权语义变得不明确。

### 3.5 CommonLibrary（可清除内存）

**文件：** `purgeable_memory.h`

#### 文档使用错误的注释风格 [低]
- 所有文档使用 `/* */` 块注释而非 `/** */` Doxygen 注释。`/* */` 内的 `@brief` 标签不会被 Doxygen 处理。

#### Destroy 函数使用单指针 [中]
- `OH_PurgeableMemory_Destroy(OH_PurgeableMemory *purgObj)` 接受单指针。文档说它将 `*purgObj` 设为 NULL，但使用单指针参数是不可能做到的。这要么是文档错误，要么是 API 设计错误。应使用 `OH_PurgeableMemory **purgObj`（双指针）以便能够将调用者的指针置空。

#### API 设计质量 [提示]
- 良好的读/写锁定模式，具有 Begin/End 配对。
- 良好地使用不透明类型（`typedef struct PurgMem OH_PurgeableMemory`）。
- 修改器链模式对数据恢复的设计良好。

### 3.6 DistributedDataMgr（关系型存储）

**文件：** `relational_store.h`、`relational_store_error_code.h`、`oh_cursor.h`、`oh_predicates.h`、`oh_value_object.h`、`oh_values_bucket.h`、`data_asset.h`

#### C API 中使用 C++ 头文件 [高]
- `oh_cursor.h` 第 40 行：`#include <cstdint>` -- C++ 头文件，应为 `<stdint.h>`。

#### ABI 脆弱性：配置结构体使用 #pragma pack(1) [高]
- `relational_store.h` 第 102-139 行：`OH_Rdb_Config` 使用 `#pragma pack(1)`。这强制字节对齐，会导致：
  - 在不支持非对齐内存访问的架构上产生非对齐内存访问
  - 如果调用者编译时未使用匹配的 pack 指令，会造成 ABI 不兼容
  - `selfSize` 字段暗示了版本控制意图，但 `#pragma pack(1)` 使其变得脆弱

#### ABI 脆弱性：暴露的虚表模式 [高]
- `OH_Cursor` 是一个包含函数指针字段（虚表模式）的结构体。这意味着：
  - 添加新方法会破坏 ABI（sizeof 变化）
  - 字段顺序永远被冻结
  - 偏移量 0 处的 `id` 字段对于虚表结构体来说很不寻常
  - API 11 中新增的方法（`getAsset`、`getAssets`）扩展了该结构体，这将破坏任何分配或拷贝 `OH_Cursor` 结构体的代码

#### 错误处理不一致 [中]
- `OH_Rdb_Insert` 成功时返回 rowId，失败时返回错误码。由于正数 rowId 和错误码都可能被返回，没有干净的方式来区分成功和 `RDB_ERR = -1`。另外，`RDB_E_NOT_SUPPORTED = 801` 与主要错误范围 `E_BASE = 14800000` 相差甚远。

#### 结构体字段命名不一致 [低]
- `Rdb_ChangeInfo.ChangeType` 使用 PascalCase，而所有其他字段使用 camelCase。

#### 文档拼写错误 [低]
- `relational_store.h` 第 241 行：`OH__VBucket`（双下划线）出现在 @param 文档中。
- `relational_store.h` 第 879 行："synchornizaiton"（"synchronization"的拼写错误），多次出现。

### 3.7 Drivers（USB DDK、HID DDK）

**文件：** `usb_ddk_api.h`、`usb_ddk_types.h`、`hid_ddk_api.h`、`hid_ddk_types.h`

#### 不一致的对齐属性 [中]
- `UsbControlRequestSetup` 使用 `__attribute__((aligned(8)))`，但仅包含 uint8_t 和 uint16_t 字段（共 8 字节）。对齐到 8 字节会浪费空间。
- `UsbDeviceDescriptor` 也使用 `__attribute__((aligned(8)))`，但仅包含 18 字节的字段，实际占用 24 字节。
- `UsbConfigDescriptor` 使用 `__attribute__((packed))`（无填充），而 `UsbDeviceDescriptor` 使用 `aligned(8)`。同一子系统内的这种不一致令人困惑。
- `UsbRequestPipe` 使用 `__attribute__((aligned(8)))`，数据总量为 13 字节。

**关于合理性的疑虑：** USB 协议结构体在 USB 规范中有定义的字节布局。使用 `packed` 对于匹配线格式是正确的。使用 `aligned(8)` 对于线格式结构体是不正确的，会引入不必要的填充。

#### ABI 稳定性：UsbDeviceMemMap 被暴露 [中]
- `UsbDeviceMemMap` 暴露了 `uint8_t * const address` 和 `const size_t size` 作为结构体字段。结构体成员上的 `const` 修饰符意味着该结构体无法轻松进行零初始化，并会导致赋值问题。

#### @} 的右花括号位置 [低]
- `usb_ddk_types.h`：`/** @} */` 放在 `#ifdef __cplusplus` 的右花括号之后但在 `#endif` 之前，意味着 Doxygen 组在 C++ 条件内部结束。

#### HID DDK：公开结构体中的固定大小数组 [中]
- `Hid_EventProperties` 包含四个 `int32_t[64]` 数组（`hidAbsMax`、`hidAbsMin`、`hidAbsFuzz`、`hidAbsFlat`）。这将最大绝对轴数冻结为 64，即使使用很少的轴，每个实例也会浪费 1024 字节。

### 3.8 Global（资源管理）

**文件：** `raw_file.h`、`raw_file_manager.h`、`raw_dir.h`

#### C 头文件中使用 C++ 语法 [严重]
- `raw_file.h` 第 42 行：`#include <string>` -- 在 C API 文件中包含了 C++ 标准库头文件。
- `raw_file.h` 第 200 行：`OH_ResourceManager_GetRawFileDescriptor(const RawFile *rawFile, RawFileDescriptor &descriptor)` -- 使用了 C++ 引用参数 `&descriptor`，在 C 中无效。
- `raw_file.h` 第 212 行：`OH_ResourceManager_ReleaseRawFileDescriptor(const RawFileDescriptor &descriptor)` -- 同样的问题。

这意味着这些 API **只能从 C++ 调用**，完全违背了作为 C API 的目的。

#### 文件大小使用 `long` 类型 [中]
- 原始 `RawFile` 函数使用 `long` 表示偏移量和大小（`OH_ResourceManager_GetRawFileSize`、`OH_ResourceManager_SeekRawFile` 等）。在 32 位系统上，`long` 为 32 位，将文件大小限制在 2 GB。`RawFile64` 变体（API 11）正确使用了 `int64_t`。
- 然而，`RawFileDescriptor` 仍然使用 `long` 表示 `start` 和 `length` 字段，而 `RawFileDescriptor64` 则正确使用 `int64_t`。

### 3.9 Graphic（绘制、NativeBuffer、NativeWindow）

**文件：** native_drawing、native_buffer、native_image、native_window、native_vsync 共 27 个头文件

#### NativeBuffer：不完整的错误码文档 [中]
- `OH_NativeBuffer_Reference`、`OH_NativeBuffer_Unreference`、`OH_NativeBuffer_Map`、`OH_NativeBuffer_Unmap`、`OH_NativeBuffer_SetColorSpace` 都只说"返回错误码，0 表示成功，否则为失败"，未指明可能的错误码或引用错误码枚举。

#### NativeWindow：属性设置使用可变参数函数 [高]
- `OH_NativeWindow_NativeWindowHandleOpt(OHNativeWindow *window, int code, ...)` 使用可变参数列表。这本质上是类型不安全的，因为：
  - 对可变参数没有编译时类型检查
  - `NativeWindowOperation` 枚举仅在注释中说明预期类型
  - `GET_BUFFER_GEOMETRY` 文档中参数为 `[out] int32_t *height, [out] int32_t *width` -- 注意 height 在 width 之前，这是反直觉且容易出错的
  - 容易传递错误类型，导致未定义行为

#### NativeWindow：引用计数使用 void 指针 [中]
- `OH_NativeWindow_NativeObjectReference(void *obj)` 和 `OH_NativeWindow_NativeObjectUnreference(void *obj)` 接受 `void*` 并声明可用于 `OHNativeWindow` 和 `OHNativeWindowBuffer`。这不提供任何类型安全性，允许传递任意指针。

#### BufferHandle：暴露含灵活数组成员的结构体 [中]
- `BufferHandle` 暴露了 `int32_t reserve[0]`（零长度数组），这是 GCC 扩展（C99 应使用 `int32_t reserve[]`）。同时暴露了 `void *virAddr` 和 `uint64_t phyAddr` -- 物理地址不应在用户空间 API 中暴露。

#### Drawing API：良好的不透明类型设计 [提示]
- `drawing_types.h` 一致使用不透明类型（`typedef struct OH_Drawing_Canvas OH_Drawing_Canvas`）。
- drawing_*.h 文件间类型和函数的分离清晰。
- 枚举值遵循一致的命名规范，但缺少 `OH_` 前缀（例如 `BLEND_MODE_CLEAR` 而非 `OH_DRAWING_BLEND_MODE_CLEAR`），可能造成名称冲突。

#### NativeWindow：弃用 API 缺少正确的 Doxygen 格式 [低]
- `OHScalingMode`、`OHHDRMetadataKey`、`OHHDRMetaData`、`OHExtDataHandle` 使用 `@deprecated(since = "10")` 格式，这不是标准格式。标准 Doxygen 使用 `@deprecated` 后跟描述文本。这些类型上的若干弃用函数使用相同的格式。

### 3.10 HiviewDFX (HiLog, HiAppEvent, HiTrace)

**文件：** `log.h`、`hiappevent.h`、`hiappevent_cfg.h`、`hiappevent_event.h`、`hiappevent_param.h`、`trace.h`

#### HiLog：良好的 API 设计 [提示]
- 设计良好的隐私感知日志记录，具有 `{public}` 和 `{private}` 格式说明符。
- 提供了全面的文档和使用示例。
- `__attribute__((__format__(os_log, 5, 6)))` 启用了编译器格式字符串检查。
- 日志回调注册 API（`OH_LOG_SetCallback`）设计简洁。

#### HiLog：LogType 枚举过于限制 [低]
- `LogType` 枚举仅有 `LOG_APP = 0`。其他类型可能在内部存在。该枚举可以受益于添加哨兵值或关于未来可扩展性的文档。

### 3.11 Multimedia

**文件：** audio_framework、av_codec、camera_framework、drm_framework、image_framework、media_foundation、player_framework 共约 46 个头文件

#### AV Errors：枚举注释中的拼写错误 [低]
- `native_averrors.h` 第 40 行：`opertation` 应为 `operation`。

#### AV Errors：模糊的错误范围 [中]
- `AV_ERR_EXTEND_START = 100` 已声明，但没有文档或枚举定义扩展错误是什么。子系统特定的错误大概从 100+ 开始，但未在此头文件中定义。

#### Audio Stream：错误码方向不一致 [低]
- Audio Stream 使用正数错误码（1, 2, 3），AV Codec 也使用相同的正数方向。至少这两个子系统在内部是一致的。

#### Image Framework：SDK 头文件中包含测试文件 [低]
- `multimedia/image_framework/ndk_test_example/my_pixel_map.h` -- 测试/示例文件不应作为 SDK 头文件的一部分发布。

### 3.12 Network

**文件：** `net_connection.h`、`net_connection_type.h`、`net_ssl_c.h`、`net_ssl_c_type.h`、`net_websocket.h`、`net_websocket_type.h`

#### 命名前缀不一致 [中]
- 在 `net_connection.h` 中：`OH_NetConn_HasDefaultNet` 使用 `OH_` 前缀，但 `OHOS_NetConn_RegisterDnsResolver`（第 191 行）和 `OHOS_NetConn_UnregisterDnsResolver`（第 204 行）使用 `OHOS_` 前缀。同一文件中使用两种不同的前缀令人困惑，表明 API 在开发过程中经历了不同的命名规范。

#### 错误码仅在注释中记录 [中]
- 错误返回值以内联方式记录（例如 `0 - Success. 201 - Missing permissions. 401 - Parameter error.`），而非引用已定义的错误码枚举。值 201 和 401 与 HTTP 状态码匹配，并在不同子系统间复用，但没有共享的错误码头文件。

#### DNS 解析：非 const 输入参数 [低]
- `OH_NetConn_GetAddrInfo(char *host, char *serv, ...)` -- `host` 和 `serv` 应为 `const char *`，因为函数不应修改这些输入。

### 3.13 ResourceSchedule (FFRT)

**文件：** `ffrt.h`、`c/queue.h`、`c/sleep.h`、`c/type_def.h`、`c/task.h`、`c/mutex.h`、`c/condition_variable.h`

#### 非标准命名规范 [中]
- FFRT 使用带 `ffrt_` 前缀的 `snake_case`（`ffrt_mutex_t`、`ffrt_qos_default_t`），而 NDK 的其余部分使用 `OH_PascalCase`。这使得 FFRT 看起来像一个外部库，而非 OpenHarmony SDK 的一部分。

#### 不透明结构体存储模式 [提示]
- 使用 `uint32_t storage[N]` 模式来实现互斥锁、条件变量和属性类型。这是一种常见的技术，可以避免堆分配同时保持结构体不透明。然而：
  - 如果内部实现需要更多存储空间，ABI 会悄然破坏（运行时没有大小检查）
  - 存储大小是硬编码的（`ffrt_mutex_storage_size = 64`），没有版本控制机制

#### C 头文件中的 C++ 命名空间 [低]
- `type_def.h` 第 178-188 行：包含 C++ `namespace ffrt { ... }` 块和 `using qos = int`。虽然位于 `#ifdef __cplusplus` 内，但这在 C API 头文件中很不寻常，混合了 C 和 C++ 范式。

### 3.14 Security（HUKS、Asset）

**文件：** `native_huks_api.h`、`native_huks_type.h`、`native_huks_param.h`、`native_huks_api_adapter.h`、`asset_api.h`、`asset_type.h`

#### HUKS：缺少上下文的魔数常量 [低]
- `native_huks_type.h`：多个 `#define` 常量如 `TOKEN_CHALLENGE_LEN 32`、`SHA256_SIGN_LEN 32`、`TOKEN_SIZE 32`、`MAX_AUTH_TIMEOUT_SECOND 60`、`SECURE_SIGN_VERSION 0x01000001` 缺少 `OH_HUKS_` 前缀，有与用户代码名称冲突的风险。

#### HUKS：枚举未使用显式值 [低]
- `OH_Huks_KeyPurpose` 使用 2 的幂次方值（1, 2, 4, 8, ...），这对位掩码用法是正确的，但枚举成员未被标注为可组合的标志。

#### Asset API：良好的设计 [提示]
- 清晰的标签-值属性系统，类型安全编码在标签值中。
- 通过显式释放函数（`OH_Asset_FreeBlob`、`OH_Asset_FreeResultSet`）进行适当的内存管理。
- 清晰的错误码，具有有意义的范围。
- 所有函数均有完善的 @param 标签文档。

#### Asset API：文档错误 [低]
- `asset_api.h` 第 54-55 行：两个 `@param` 条目都命名为 `attributes` -- 第二个应为 `@param attrCnt`。
- `OH_Asset_Update` 文档（第 76 行）中出现相同模式：`@param attributes` 出现了两次。

### 3.15 Sensors

**文件：** `oh_sensor.h`、`oh_sensor_type.h`、`vibrator.h`、`vibrator_type.h`

#### 良好的 API 设计 [提示]
- 使用 `Sensor_Info`、`Sensor_SubscriptionId` 等进行了清晰的不透明类型使用。
- 一致的 `Sensor_Result` 返回类型。
- 通过 `@permission` 标签提供适当的权限文档。
- 订阅/取消订阅模式简洁且对称。

### 3.16 Startup

**文件：** `syscap_ndk.h`、`deviceinfo.h`、`init_sync.h`

#### 没有命名前缀 [高]
- `syscap_ndk.h` 第 27 行：`bool canIUse(const char *cap)` -- 该函数没有 `OH_` 前缀，且使用 camelCase，与所有其他 NDK API 完全不一致。`canIUse` 是一个通用名称，很可能与用户代码冲突。

#### 没有文档 [中]
- `syscap_ndk.h` 完全没有文档 -- 函数本身没有 `@brief`、`@param`、`@return`、`@since` 标签。只有包含保护和许可证头。

#### 冗余的 `#if __cplusplus` 检查 [低]
- `syscap_ndk.h` 使用双重保护模式（`#ifdef __cplusplus` 然后 `#if __cplusplus`）。内部的 `#if __cplusplus` 是冗余且非常规的。

### 3.17 Web (ArkWeb)

**文件：** `native_interface_arkweb.h`

#### 缺少返回值文档 [中]
- `OH_NativeArkWeb_RunJavaScript`、`OH_NativeArkWeb_RegisterJavaScriptProxy`、`OH_NativeArkWeb_UnregisterJavaScriptProxy` 返回 `void`，没有错误提示。如果注册或执行失败，调用者无法得知。

#### 回调内存所有权 [中]
- `NativeArkWeb_OnJavaScriptProxyCallback` 返回 `char*`。不清楚谁拥有这块内存。调用者（框架）大概必须释放它，但分配方式未指定。

#### 文档风格 [低]
- 所有函数文档使用 `/* */` 注释而非 `/** */` Doxygen 注释（缺少前导双星号）。

#### 文档中的拼写错误 [低]
- 第 21 行："javascirpt" 应为 "JavaScript"
- 第 28 行："javascirpt" 再次出现
- 第 98 行："Deletes the registered object which th given name" -- "th" 应为 "the"

---

## 4. 汇总表

| # | 子系统 | 严重性 | 问题 | 文件 |
|---|--------|--------|------|------|
| 1 | AI/NNR | 严重 | C 头文件中使用 C++ 包含（`<cstddef>`、`<cstdint>`）和 `enum : int8_t` | `neural_network_runtime_type.h` |
| 2 | Global | 严重 | C API 中使用 C++ `#include <string>` 和引用参数 `&` | `raw_file.h` |
| 3 | ArkUI | 严重 | 文件作用域的 `const` 变量、`#include <vector>` | `native_interface_xcomponent.h` |
| 4 | RDB | 高 | C 头文件中使用 C++ `#include <cstdint>` | `oh_cursor.h` |
| 5 | RDB | 高 | `OH_Rdb_Config` 上的 `#pragma pack(1)` 导致 ABI 脆弱 | `relational_store.h` |
| 6 | RDB | 高 | `OH_Cursor` 虚表结构体 ABI 脆弱（在 API 11 中被扩展） | `oh_cursor.h` |
| 7 | Graphic | 高 | 可变参数 `OH_NativeWindow_NativeWindowHandleOpt` 类型不安全 | `external_window.h` |
| 8 | Bundle | 高 | `OH_NativeBundle_GetCurrentApplicationInfo` 返回包含无主 `char*` 指针的结构体 | `native_interface_bundle.h` |
| 9 | Startup | 高 | `canIUse()` -- 无前缀、无文档、容易冲突的名称 | `syscap_ndk.h` |
| 10 | ArkUI | 高 | ABI 冻结的暴露结构体（TouchEvent、MouseEvent），无版本控制 | `native_interface_xcomponent.h` |
| 11 | 跨领域 | 高 | 所有子系统间错误码策略不一致 | 多个文件 |
| 12 | Network | 高 | 同一头文件中混用 `OH_`/`OHOS_` 前缀 | `net_connection.h` |
| 13 | 跨领域 | 中 | 各子系统命名规范不一致 | 多个文件 |
| 14 | 跨领域 | 中 | 缺少线程安全文档 | 多个文件 |
| 15 | ArkUI | 中 | 同一概念的重复枚举（ToolType 与 SourceTool） | `native_interface_xcomponent.h` |
| 16 | ArkUI | 中 | 无条件的 `NAPI_EXPERIMENTAL` 启用了不稳定 API | `native_api.h` |
| 17 | CommonLib | 中 | `OH_PurgeableMemory_Destroy` 使用单指针但声称会将其置空 | `purgeable_memory.h` |
| 18 | RDB | 中 | `OH_Rdb_Insert` 在返回值中混用 rowId 和错误码 | `relational_store.h` |
| 19 | Drivers | 中 | 不一致的对齐属性（`packed` 与 `aligned(8)`） | `usb_ddk_types.h` |
| 20 | Drivers | 中 | `Hid_EventProperties` 中固定大小数组（浪费 1024 字节） | `hid_ddk_types.h` |
| 21 | Graphic | 中 | 引用计数的 `void*` 参数（无类型安全） | `external_window.h` |
| 22 | Graphic | 中 | `BufferHandle` 在用户空间 API 中暴露物理地址 | `buffer_handle.h` |
| 23 | Graphic | 中 | NativeBuffer 错误返回值未记录 | `native_buffer.h` |
| 24 | Graphic | 中 | 枚举值缺少 `OH_` 前缀，有冲突风险 | `drawing_types.h` |
| 25 | Network | 中 | 错误码仅在注释中，未在枚举中 | `net_connection.h` |
| 26 | Network | 中 | DNS 函数中非 const 的 `char*` 参数 | `net_connection.h` |
| 27 | FFRT | 中 | 非标准的 `snake_case` 命名规范 | `c/type_def.h` |
| 28 | AV | 中 | `AV_ERR_EXTEND_START = 100` 未定义扩展错误 | `native_averrors.h` |
| 29 | Global | 中 | 文件大小使用 `long` 类型，32 位系统限制为 2 GB | `raw_file.h` |
| 30 | Web | 中 | JS 代理操作返回 `void`，无错误提示 | `native_interface_arkweb.h` |
| 31 | Web | 中 | 回调返回 `char*`，所有权未指定 | `native_interface_arkweb.h` |
| 32 | Startup | 中 | `canIUse` 函数完全没有文档 | `syscap_ndk.h` |
| 33 | Bundle | 中 | 暴露含原始 `char*` 字段的结构体 | `native_interface_bundle.h` |
| 34 | Security | 中 | Asset API `@param` 文档重复 | `asset_api.h` |
| 35 | AI/NNR | 低 | Doxygen 组名中 "NeuralNeworkRuntime" 拼写错误 | 所有 NNR 文件 |
| 36 | AI/NNR | 中 | 枚举名称中 `OH_NN_UNAVALIDABLE_DEVICE` 拼写错误（ABI 已冻结） | `neural_network_runtime_type.h` |
| 37 | ArkUI | 低 | `titlX`/`titlY` 字段名拼写错误（ABI 已冻结） | `native_interface_xcomponent.h` |
| 38 | ArkUI | 低 | `common.h` 中错误的包含保护结束注释 | `common.h` |
| 39 | JSVM | 低 | 不透明类型上的复制粘贴文档错误 | `jsvm_types.h` |
| 40 | RDB | 低 | `ChangeType` 字段的 PascalCase 与相邻字段不一致 | `relational_store.h` |
| 41 | RDB | 低 | 文档中 "synchornizaiton" 拼写错误反复出现 | `relational_store.h` |
| 42 | CommonLib | 低 | Doxygen 使用错误的注释风格（`/* */` 而非 `/** */`） | `purgeable_memory.h` |
| 43 | Security | 低 | HUKS 常量缺少 `OH_HUKS_` 前缀 | `native_huks_type.h` |
| 44 | Drivers | 低 | `/** @} */` 位于 `#ifdef __cplusplus` 块内 | `usb_ddk_types.h` |
| 45 | Graphic | 低 | 非标准的 `@deprecated(since = "10")` 格式 | `external_window.h` |
| 46 | HiLog | 低 | `LogType` 枚举仅有一个值 | `log.h` |
| 47 | AV | 低 | 错误码注释中 "opertation" 拼写错误 | `native_averrors.h` |
| 48 | Multimedia | 低 | 测试文件被包含在 SDK 头文件中 | `my_pixel_map.h` |
| 49 | FFRT | 低 | C 头文件中包含 C++ 命名空间 | `c/type_def.h` |
| 50 | Startup | 低 | 冗余的 `#if __cplusplus` 保护模式 | `syscap_ndk.h` |
| 51 | Web | 低 | 文档中 "javascirpt" 拼写错误、错误的注释风格 | `native_interface_arkweb.h` |
| 52 | Security | 提示 | Asset API 采用标签-值模式，设计良好 | `asset_api.h`、`asset_type.h` |
| 53 | AI/NNR | 提示 | 弃用 API 迁移良好，使用 `@useinstead` | NNR 文件 |
| 54 | Sensors | 提示 | 清晰的不透明类型和对称的订阅/取消订阅 | `oh_sensor.h` |
| 55 | HiLog | 提示 | 设计良好的隐私感知日志记录，附带示例 | `log.h` |

---

## 建议

### 立即行动（严重/高等级）

1. **修复 C 头文件中的 C++ 语法：** 将 `<cstdint>`、`<cstddef>`、`<string>` 替换为 C 等价物。将 C++ 引用替换为指针。移除 `enum : int8_t` 语法。涉及 `neural_network_runtime_type.h`、`oh_cursor.h`、`raw_file.h` 和 `native_interface_xcomponent.h`。

2. **统一错误码策略：** 创建一个 `OH_ErrorCode` 公共头文件，定义共享的错误码范围和规范。每个子系统应从定义的基础值扩展。

3. **为 `canIUse()` 添加 `OH_` 前缀：** 重命名为 `OH_CanIUse()` 或 `OH_SystemCapability_IsAvailable()`，并提供完善的文档。

4. **记录内存所有权：** 对于每个返回指针的函数，记录谁拥有内存以及如何释放。

### 短期行动（中等级）

5. **统一命名：** 一致采用 `OH_SubsystemName_FunctionName` 格式。逐步淘汰 `OHOS_` 前缀和 FFRT 的 `snake_case`。

6. **添加线程安全注解：** 为每个 API 在函数级或头文件级文档中记录线程安全性。

7. **为暴露的结构体添加版本控制：** 对于必须暴露的结构体（如 `OH_Rdb_Config`），确保 `selfSize` 模式得到一致使用，并移除 `#pragma pack(1)`。

8. **替换可变参数属性函数：** `OH_NativeWindow_NativeWindowHandleOpt` 应替换为类型安全的 getter/setter 函数。

### 长期行动（低等级/提示）

9. **合并重复枚举**，如 `OH_NativeXComponent_TouchPointToolType` 和 `OH_NativeXComponent_TouchEvent_SourceTool`。

10. **添加编译时 CI 检查**，确保所有 NDK 头文件都能使用 C11 和 C++17 编译器编译。

11. **修复所有 ABI 冻结的拼写错误**的文档；对代码级别的拼写错误（`titlX`、`OH_NN_UNAVALIDABLE_DEVICE`）需要接受并添加别名，因为它们在不破坏兼容性的情况下无法移除。
