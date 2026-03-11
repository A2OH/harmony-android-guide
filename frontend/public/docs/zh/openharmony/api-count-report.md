# OpenHarmony SDK API 数量报告

生成日期：2026-03-10

本报告全面统计了 OpenHarmony 应用开发者可使用的所有 API，涵盖两个 SDK 层面：JS/TS SDK 和 C/NDK SDK。

---

## 1. JS/TS SDK API

**位置：** `/home/dspfac/openharmony/interface/sdk-js/api/`

### 1.1 文件数量

| 指标 | 数量 |
|--------|------:|
| `.d.ts` 声明文件 | 666 |
| `.d.ets` 声明文件 | 24 |
| **声明文件总计** | **690** |

### 1.2 导出 API 元素数量

| API 元素类型 | 数量 |
|------------------|------:|
| 导出函数（`export function`） | 30 |
| 导出接口（`export interface`） | 686 |
| 导出类（`export class`） | 43 |
| 导出枚举（`export enum`） | 314 |
| 导出类型别名（`export type X =`） | 187 |
| 导出常量/变量（`export const/let/var`） | 2 |
| 接口/类方法声明（成员方法） | 7,501 |
| **顶层导出 API 符号总计** | **1,262** |
| **API 总体规模（含成员方法）** | **8,763** |

> 注意：绝大多数 JS/TS API 以导出接口和类中的方法形式暴露（7,501 个成员方法），
> 而非独立的导出函数。30 个导出函数为模块级工具函数。

### 1.3 按顶层 @ohos 命名空间分类

API 根目录下的顶层文件，按 `@ohos.<namespace>.*` 的第一段分组：

| 命名空间 | 文件数量 |
|-----------|----------:|
| `@ohos.app.*`（Ability 框架） | 53 |
| `@ohos.arkui.*` | 29 |
| `@ohos.application.*`（旧版） | 20 |
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
| 其他单文件模块（window、web、rpc、i18n 等） | 99 |

### 1.4 按子目录分类

`api/` 文件夹子目录中的文件：

| 子目录 | 声明文件数 |
|--------------|------------------:|
| `@internal/`（ETS 组件） | 127 |
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

## 2. C/NDK SDK API

**位置：** `/home/dspfac/openharmony/interface/sdk_c/`

### 2.1 文件数量

| 指标 | 数量 |
|--------|------:|
| **头文件总计（`.h`）** | **382** |

### 2.2 API 元素数量

| API 元素类型 | 数量 |
|------------------|------:|
| 函数声明/原型 | 4,085 |
| 结构体定义 | 2,471 |
| 枚举定义 | 457 |
| Typedef 声明 | 5,077 |
| `#define` 宏（不含头文件保护宏） | 15,746 |
| **C/NDK API 元素总计** | **27,836** |

### 2.3 按子系统目录分类

| 子系统 | 头文件数 | 函数数 | 结构体数 | 枚举数 | Typedef 数 | #define 数 |
|-----------|--------:|----------:|--------:|------:|---------:|---------:|
| `third_party/`（musl、OpenGL ES、EGL、Vulkan、libuv、zlib、OpenSL ES、mindspore、node） | 251 | 3,333 | 2,071 | 263 | 4,628 | 15,638 |
| `multimedia/`（相机、音频、媒体、图像、AV 编解码、DRM） | 46 | 285 | 170 | 73 | 182 | 22 |
| `graphic/`（2D 绘图、native_window、native_buffer、native_image、vsync） | 27 | 194 | 43 | 27 | 60 | 15 |
| `resourceschedule/`（FFRT） | 7 | 39 | 1 | 7 | 19 | 3 |
| `distributeddatamgr/`（关系型存储） | 7 | 35 | 13 | 12 | 32 | 3 |
| `security/`（HUKS、Asset） | 6 | 15 | 61 | 29 | 13 | 17 |
| `network/`（网络连接、WebSocket、SSL） | 6 | 17 | 31 | 4 | 15 | 7 |
| `hiviewdfx/`（HiLog、HiTrace、HiAppEvent） | 6 | 27 | 1 | 3 | 4 | 15 |
| `sensors/`（传感器、振动器） | 4 | 22 | 7 | 4 | 13 | 0 |
| `drivers/`（USB DDK、HID DDK） | 4 | 14 | 36 | 9 | 28 | 0 |
| `arkui/`（NAPI、XComponent） | 4 | 17 | 5 | 10 | 20 | 7 |
| `startup/`（设备信息、syscap） | 3 | 5 | 0 | 0 | 0 | 6 |
| `global/`（资源管理、原始文件） | 3 | 19 | 8 | 0 | 6 | 0 |
| `ai/`（神经网络运行时） | 3 | 43 | 10 | 8 | 21 | 0 |
| `ark_runtime/`（JSVM） | 2 | 4 | 11 | 8 | 29 | 13 |
| `web/`（ArkWeb 原生） | 1 | 7 | 0 | 0 | 0 | 0 |
| `commonlibrary/`（可清除内存） | 1 | 6 | 1 | 0 | 2 | 0 |
| `bundlemanager/`（包管理框架） | 1 | 4 | 2 | 0 | 1 | 0 |

---

## 3. 总体汇总

| SDK 层面 | 声明/头文件数 | 顶层符号数 | API 总体规模 |
|-------------|-------------------------:|-----------------:|------------------:|
| JS/TS SDK | 690 | 1,262 个导出符号 | 8,763（含方法） |
| C/NDK SDK | 382 | 27,836 个元素 | 27,836 |
| **合计** | **1,072** | **29,098** | **36,599** |

### 统计方法说明

- **JS/TS SDK**："导出符号"统计顶层 `export function`、`export interface`、`export class`、`export enum`、`export type` 和 `export const/let/var` 声明。"API 总体规模"在此基础上加上接口/类的成员方法声明（匹配类型内部方法签名模式的行）。
- **C/NDK SDK**：统计包含所有函数原型、结构体定义、枚举定义、typedef 声明和 `#define` 宏（不含头文件保护宏）。`third_party/` 目录包含标准 C 库（musl libc）、OpenGL ES、EGL、Vulkan、libuv、zlib、OpenSL ES、MindSpore Lite 和 Node-API 头文件——这些体量较大，但属于 NDK 发行版的一部分。
- **#define 宏**：已排除头文件保护宏（以 `_H` 或 `_H_` 结尾的模式）。剩余的 15,746 个宏包括 API 常量、功能标志、GL/Vulkan/EGL 常量和函数式宏。
- 所有统计基于对声明文件的模式匹配，由于注释、条件编译或特殊格式，可能存在轻微的多计或少计。
