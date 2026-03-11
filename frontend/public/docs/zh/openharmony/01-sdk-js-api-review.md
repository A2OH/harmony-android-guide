# OpenHarmony 4.1 版本 -- SDK JS API 声明审查

**审查日期：** 2026-03-10
**范围：** `/home/dspfac/openharmony/interface/sdk-js/api/` -- 所有 `.d.ts` 和 `.d.ets` API 声明文件
**审查人：** 自动化代码审查 (Claude Opus 4.6)

---

## 目录

1. [执行摘要](#1-执行摘要)
2. [范围和方法](#2-范围和方法)
3. [横切性发现](#3-横切性发现)
   - 3.1 [类型安全问题](#31-类型安全问题)
   - 3.2 [文档质量](#32-文档质量)
   - 3.3 [弃用和 API 演进](#33-弃用和-api-演进)
   - 3.4 [错误处理模式](#34-错误处理模式)
   - 3.5 [命名规范问题](#35-命名规范问题)
   - 3.6 [安全考虑](#36-安全考虑)
   - 3.7 [JSDoc 重复模式](#37-jsdoc-重复模式)
4. [按模块分类的发现](#4-按模块分类的发现)
   - 4.1 [核心/基础](#41-核心基础)
   - 4.2 [网络](#42-网络)
   - 4.3 [文件系统](#43-文件系统)
   - 4.4 [数据管理](#44-数据管理)
   - 4.5 [安全和加密](#45-安全和加密)
   - 4.6 [UI 框架 (ArkUI)](#46-ui-框架-arkui)
   - 4.7 [多媒体](#47-多媒体)
   - 4.8 [连接（蓝牙/Wi-Fi/NFC）](#48-连接蓝牙wi-finfc)
   - 4.9 [Ability 框架](#49-ability-框架)
   - 4.10 [实用工具和集合](#410-实用工具和集合)
   - 4.11 [系统服务](#411-系统服务)
   - 4.12 [电话](#412-电话)
   - 4.13 [企业管理](#413-企业管理)
5. [按严重级别汇总所有发现](#5-按严重级别汇总所有发现)
6. [建议](#6-建议)

---

## 1. 执行摘要

OpenHarmony 4.1 SDK JS API 声明包含顶层 `api/` 目录下约**444个文件**（包括 `.d.ts`、`.d.ets` 和子目录），以及 `@internal/component/ets/` 下的**100多个 ETS 组件声明**。

总体而言，API 接口广泛，覆盖了大量操作系统能力。声明通常遵循一致的结构，带有包含 `@syscap`、`@since`、`@crossplatform` 和 `@atomicservice` 标签的 JSDoc 注释。然而，审查发现了多个系统性问题，影响开发者体验、类型安全和可维护性。

**主要发现：**
- **12个严重/高危问题**涉及类型安全、`any` 使用和不正确的文档
- **15+个中危问题**涉及命名不一致、缺少 Promise 重载和 `Object` 使用
- **大量低危/提示问题**涉及 JSDoc 重复开销和轻微文档空缺

---

## 2. 范围和方法

### 审查的文件
- 所有 `@ohos.*.d.ts` 文件（约350个文件），覆盖核心 OpenHarmony JS/TS SDK
- `@system.*.d.ts` 文件（遗留 JS API，约15个文件）
- `@internal/component/ets/*.d.ts`（ArkUI ETS 组件声明，约100+个文件）
- `@ohos.arkui.advanced.*.d.ets`（高级 UI 组件声明，24个文件）
- 支持文件：`permissions.d.ts`、子目录（`ability/`、`application/`、`bundle/` 等）

### 审查方法
- 自动化模式扫描，针对 `any`、`Object`、缺失泛型、弃用模式
- 从每个主要领域中选取代表性文件进行人工审查
- 跨文件一致性检查，涵盖命名、错误处理和文档模式

---

## 3. 横切性发现

### 3.1 类型安全问题

#### F-3.1.1：公共 API 中使用 `any` 类型
**严重级别：高危**

多个公共 API 声明使用 `{ [key: string]: any }`，破坏了 TypeScript 类型检查。发现于：

| 文件 | 位置 |
|------|----------|
| `@ohos.wantAgent.d.ts` | `extraInfo?: { [key: string]: any }` |
| `@ohos.application.Want.d.ts` | `parameters?: { [key: string]: any }` |
| `@ohos.account.appAccount.d.ts` | 多个 `options` 和 `onResult` 参数 |
| `@ohos.distributedHardware.deviceManager.d.ts` | 两个接口中的 `extraInfo: { [key: string]: any }` |
| `@ohos.events.emitter.d.ts` | `EventData` 中的 `data?: { [key: string]: any }` |

**影响：** 开发者在使用这些 API 时将失去所有类型安全性。属性名中的任何拼写错误或错误的值类型都不会在编译时被捕获。

**建议：** 将 `any` 替换为 `unknown`，或更好地使用 `Record<string, string | number | boolean | object>` 或在已知值空间的情况下使用特定领域的联合类型。对于 Want 参数，定义结构化的 `WantParams` 类型。

#### F-3.1.2：普遍使用 `Object`（大写 O）类型
**严重级别：高危**

在**30+个文件**中发现**246+处 `Object` 类型**的使用。典型示例：

| 文件 | 用法 |
|------|-------|
| `@ohos.net.http.d.ts` | `header?: Object`（应为 `Record<string, string>`） |
| `@ohos.net.http.d.ts` | `extraData?: string \| Object \| ArrayBuffer` |
| `@ohos.net.webSocket.d.ts` | `header?: Object` |
| `@ohos.request.d.ts` | `header?: Object` |
| `@ohos.router.d.ts` | `params?: Object` |
| `@ohos.util.d.ts` | `...args: Object[]` |
| `@ohos.buffer.d.ts` | Blob 构造函数中的 `options?: Object` |
| `@ohos.worker.d.ts` | `transfer?: Object[]`（应为 `ArrayBuffer[]` 或 `Transferable[]`） |
| 各种 util 集合 | forEach/replaceAllElements 中的 `thisArg?: Object` |

**影响：** `Object` 过于宽泛，不提供有用的类型检查。将 HTTP 头类型定义为 `Object` 对开发者来说完全没有预期结构的指导。

**建议：**
- HTTP 头：`Record<string, string | string[]>`
- 路由参数：`Record<string, string | number | boolean | object>`
- Worker 传输：`ArrayBuffer[]`
- util 集合 thisArg：`unknown` 或直接移除该参数，因为箭头函数会词法捕获 `this`

#### F-3.1.3：Permissions 类型仅为 `string`
**严重级别：中危**

在 `permissions.d.ts` 中：
```typescript
export type Permissions = string;
```

这不提供权限名称的编译时验证。虽然这是一个类型别名，但它可以是所有已知权限名称的字符串字面量联合类型（例如 `'ohos.permission.INTERNET' | 'ohos.permission.CAMERA' | ...`）。

**影响：** 开发者可以传递任何字符串作为权限名称，拼写错误不会产生编译时错误。

---

### 3.2 文档质量

#### F-3.2.1：JSDoc 注释中的复制粘贴错误
**严重级别：高危**

在 `@ohos.data.relationalStore.d.ts` 中，`AssetStatus` 枚举有不正确的 JSDoc 注释。`ASSET_INSERT`、`ASSET_UPDATE` 和 `ASSET_DELETE` 的描述都错误地说"ASSET_ABNORMAL"而不是它们的正确名称：

```
ASSET_INSERT:   "ASSET_ABNORMAL: means the asset needs to be inserted."
ASSET_UPDATE:   "ASSET_ABNORMAL: means the asset needs to be updated."
ASSET_DELETE:   "ASSET_ABNORMAL: means the asset needs to be deleted."
ASSET_ABNORMAL: "ASSET_ABNORMAL: means the status of asset is abnormal."
```

**影响：** 误导性文档会让试图理解资产状态生命周期的开发者困惑。

#### F-3.2.2：弃用模块缺少 `@file` 标签
**严重级别：低危**

`@ohos.notification.d.ts` 文件（自 API 9 起弃用）缺少所有其他模块都有的 `@file` 和 `@kit` JSDoc 标签。虽然此模块已弃用，但它仍在 SDK 中发布，应保持文档一致性。

#### F-3.2.3：返回值的 JSDoc 描述不一致
**严重级别：低危**

一些 `@returns` 描述中"Returns"大写，其他则使用小写。`@ohos.abilityAccessCtrl.d.ts` 中的示例：
- API 8: `@returns { AtManager } Returns the instance of the AtManager.`
- API 10: `@returns { AtManager } returns the instance of the AtManager.`

#### F-3.2.4：公共 API JSDoc 中暴露的开发内部注释
**严重级别：中危**

在 `@ohos.data.relationalStore.d.ts` 中，`ValuesBucket` 类型定义在其 JSDoc 中暴露了内部变更说明：
```
"change {[key: string]: ValueType;} to Record<string, ValueType>"
```
这是一个内部实现说明，而非面向开发者的描述。

#### F-3.2.5：JSDoc 中的双空格拼写错误
**严重级别：提示**

在 `@ohos.events.emitter.d.ts` 中，`"callback function  from an event"` 有一个双空格（第138和149行附近）。

---

### 3.3 弃用和 API 演进

#### F-3.3.1：大量已弃用但仍发布的 API
**严重级别：中危**

70+个文件中有超过**1,500+个 `@deprecated` 标签**。仍在发布的主要弃用模块：

| 弃用模块 | 替代 | 弃用自 |
|---|---|---|
| `@ohos.notification.d.ts` | `@ohos.notificationManager.d.ts` + `@ohos.notificationSubscribe.d.ts` | API 9 |
| `@ohos.bluetooth.d.ts` | `@ohos.bluetoothManager.d.ts` 然后 `@ohos.bluetooth.*.d.ts`（模块化） | API 9 |
| `@ohos.wifi.d.ts` | `@ohos.wifiManager.d.ts` | API 9 |
| `@ohos.usb.d.ts` | `@ohos.usbManager.d.ts` | API 9 |
| `@ohos.data.rdb.d.ts` | `@ohos.data.relationalStore.d.ts` | API 9 |
| `@ohos.data.storage.d.ts` | `@ohos.data.preferences.d.ts` | API 9 |
| `@ohos.geolocation.d.ts` | `@ohos.geoLocationManager.d.ts` | API 9 |
| `@ohos.commonEvent.d.ts` | `@ohos.commonEventManager.d.ts` | API 9 |
| `@ohos.document.d.ts` | `@ohos.file.picker.d.ts` | API 9 |
| `@ohos.fileio.d.ts` | `@ohos.file.fs.d.ts` | API 9 |

**影响：** SDK 发布了大量弃用的 API，几乎全部在 API 9 弃用。这增加了 SDK 大小和开发者困惑。其中一些是多层的：`@ohos.bluetooth.d.ts` -> `@ohos.bluetoothManager.d.ts` -> `@ohos.bluetooth.access.d.ts` 等。

#### F-3.3.2：蓝牙 API 经历了三次命名迭代
**严重级别：中危**

蓝牙 API 经历了多轮弃用：
1. `@ohos.bluetooth.d.ts`（API 7，API 9 弃用）-> `@ohos.bluetoothManager.d.ts`
2. `@ohos.bluetoothManager.d.ts`（API 9，API 10 弃用）-> 模块化 `@ohos.bluetooth.*.d.ts`

这意味着蓝牙在连续两个 API 版本中被弃用了两次，给维护跨 API 级别应用的开发者造成迁移负担。

#### F-3.3.3：Wi-Fi 扫描弃用了两次
**严重级别：低危**

`wifiManager.scan()` 在 API 9 引入，但在 API 10 被弃用，改用 `wifiManager.startScan()`。对于公共 API 来说，这是非常短的生命周期。

---

### 3.4 错误处理模式

#### F-3.4.1：仅回调 API 缺少 Promise 重载
**严重级别：中危**

`@ohos.commonEventManager.d.ts` 中的多个 API 仅提供基于回调的接口，没有 Promise 替代方案：
- `publish(event, callback)` -- 无 Promise 版本
- `publishAsUser(event, userId, callback)` -- 无 Promise 版本

这与大多数其他模块（例如 `@ohos.file.fs.d.ts`、`@ohos.net.http.d.ts`）中使用的双回调/Promise 模式不一致。

**影响：** 开发者无法对这些 API 使用 `async/await`，被迫使用回调风格代码。

#### F-3.4.2：错误码文档不一致
**严重级别：中危**

`@throws` 注释的错误码格式各不相同：
- `@ohos.commonEventManager.d.ts`: `@throws { BusinessError } 401 - parameter error`
- `@ohos.wifiManager.d.ts`: `@throws {BusinessError} 201 - Permission denied.`
- `@ohos.abilityAccessCtrl.d.ts`: `@throws { BusinessError } 401 - The parameter check failed.`

`{ BusinessError }` 与 `{BusinessError}` 之间的空格、描述格式（句号与否、大小写）不一致。

#### F-3.4.3：旧 API 返回 boolean 表示成功/失败而非抛出异常
**严重级别：低危**

在 `@ohos.wifi.d.ts`（已弃用）中，`enableWifi()` 等 API 返回 `boolean` 表示成功/失败。替代的 `@ohos.wifiManager.d.ts` 正确地改为 `void` 并使用 `@throws` 表示错误。这是良好的 API 演进，但 SDK 中同时存在两种模式可能会让开发者困惑。

---

### 3.5 命名规范问题

#### F-3.5.1：ETS 组件文件命名规范混乱
**严重级别：中危**

`@internal/component/ets/` 目录使用不一致的命名：
- 蛇形命名：`alert_dialog.d.ts`、`column_split.d.ts`、`date_picker.d.ts`、`list_item.d.ts`
- 驼峰命名：`gridItem.d.ts`、`checkboxgroup.d.ts`
- 混合：`calendar_picker.d.ts` 与 `checkboxgroup.d.ts`（无分隔符与下划线）

#### F-3.5.2：模块命名模式不一致
**严重级别：低危**

- `@ohos.geolocation.d.ts` 与 `@ohos.geoLocationManager.d.ts`（"Location"的驼峰命名不一致）
- `@ohos.usb.d.ts` 与 `@ohos.usbManager.d.ts`（Manager 后缀模式）
- `@ohos.data.rdb.d.ts` 与 `@ohos.data.relationalStore.d.ts`（缩写与全名）
- `@ohos.fileio.d.ts` 与 `@ohos.file.fs.d.ts`（完全不同的命名结构）

#### F-3.5.3：`@ohos.request.d.ts` 错误常量使用不一致的权限标签
**严重级别：低危**

`@ohos.request.d.ts` 中的错误常量标记了 `@permission ohos.permission.INTERNET`，即使它们只是错误码常量而不是需要权限的方法。`@permission` 标签应仅用于运行时实际需要权限的函数/方法。

---

### 3.6 安全考虑

#### F-3.6.1：系统 API 暴露在公共 SDK 声明中
**严重级别：中危**

许多标记为 `@systemapi`（仅系统应用可调用）的 API 与公共 API 一起出现在公共 SDK 声明中。虽然第三方应用在运行时调用会失败，但它们在 SDK 中的存在：
- 增加了 API 发现的攻击面
- 可能让试图调用它们的第三方开发者困惑
- 典型案例：窗口类型（TYPE_INPUT_METHOD、TYPE_STATUS_BAR 等）、Wi-Fi 启用/禁用、任务管理

#### F-3.6.2：HTTP 选项中的 `caPath` 允许自定义 CA 证书
**严重级别：提示**

`@ohos.net.http.d.ts` 暴露了 `caPath?: string`，允许应用指定自定义 CA 证书路径。虽然这对企业用例是必要的，但文档未警告证书固定绕过的安全影响。

#### F-3.6.3：剪贴板 API 访问未受权限保护
**严重级别：提示**

`@ohos.pasteboard.d.ts` 提供了无需权限的剪贴板读写（API 声明中无可见的 `@permission` 标签）。剪贴板访问是移动平台上已知的隐私问题。注意：运行时访问控制可能独立于声明级别的权限注释存在。

---

### 3.7 JSDoc 重复模式

#### F-3.7.1：版本演进导致大量 JSDoc 块重复
**严重级别：提示**

几乎每个 API 成员都有2-3个重复的 JSDoc 块来记录版本演进。例如，`@ohos.net.http.d.ts` 中的单个方法声明有 `@since 6`、`@since 10`（添加 `@crossplatform`）和 `@since 11`（添加 `@atomicservice`）的独立块。这意味着：

- `header?: Object` 字段有**3个几乎相同的 JSDoc 块**（36行），仅为一个属性
- `@ohos.net.http.d.ts` 有**45处使用 `Object`** 类型，许多在版本块之间重复
- `@ohos.data.preferences.d.ts` 有**86个 `@crossplatform` 标签**，文档本可以更简洁地表达

这种模式使文件极其冗长（HTTP 模块超过2000行），并增加了类似 F-3.2.1 的复制粘贴错误的风险。

**建议：** 考虑工具化方法，使单个 JSDoc 块可以内联表达版本历史，例如 `@since 6 @crossplatform(10) @atomicservice(11)`。

---

## 4. 按模块分类的发现

### 4.1 核心/基础

#### `@ohos.base.d.ts`
**状态：设计良好**

- `Callback<T>`、`ErrorCallback<T>`、`AsyncCallback<T, E>` 和 `BusinessError<T>` 具有完善的泛型类型
- 良好使用 `extends Error` 用于 `BusinessError`
- `AsyncCallback` 上的泛型参数 `E = void` 是一个优雅的设计，允许错误数据类型化
- **未发现问题** -- 这是 SDK 中最强的文件

#### `@ohos.process.d.ts`
- 包含已弃用的 API（7处）
- 总体结构良好

### 4.2 网络

#### `@ohos.net.http.d.ts`
- **F-4.2.1（高危）：** `header?: Object` 应为 `Record<string, string>` 或 `{ [key: string]: string }`
- **F-4.2.2（高危）：** `extraData?: string | Object | ArrayBuffer` -- `Object` 过于宽泛；表单数据应使用 `Record<string, string>` 或更具体的类型
- **F-4.2.3（中危）：** 响应 `result: string | Object | ArrayBuffer` -- 返回类型根据 `HttpDataType` 变化，但类型系统中未表达（可使用重载或泛型）
- **F-4.2.4（低危）：** 12个 `@permission` 标签 -- 权限模型文档完善

#### `@ohos.net.webSocket.d.ts`
- 与 HTTP 相同的 `header?: Object` 问题

#### `@ohos.net.socket.d.ts`、`@ohos.net.connection.d.ts`
- 遵循标准模式，在抽样部分中未检测到主要问题

### 4.3 文件系统

#### `@ohos.file.fs.d.ts`
- **F-4.3.1（提示）：** 非常全面的 API 接口（3500+ `@throws` 标签），覆盖良好
- 正确的同步/异步对（例如 `open`/`openSync`、`read`/`readSync`）
- `OpenMode` 标志使用了类型化枚举，设计良好
- 带有 `ProgressListener` 的进度回调类型设计良好
- 正确导出值导出和类型导出

#### `@ohos.file.photoAccessHelper.d.ts`
- 7处使用 `Object` 类型，30个 `@deprecated` 标签 -- 正在进行重大演进

### 4.4 数据管理

#### `@ohos.data.relationalStore.d.ts`
- **F-4.4.1（高危）：** `AssetStatus` 枚举上的复制粘贴 JSDoc 错误（见 F-3.2.1）
- **F-4.4.2（中危）：** `ValuesBucket` JSDoc 中暴露的内部变更说明
- **F-4.4.3（提示）：** API 11 中从 `{[key: string]: ValueType}` 到 `Record<string, ValueType>` 的良好演进
- `Asset.size` 类型为 `string` 而非 `number` -- 这似乎是为了大文件大小而有意为之，但不常见
- `Asset.createTime` 和 `Asset.modifyTime` 为 `string` -- 可使用 `number`（时间戳）以提高类型安全

#### `@ohos.data.preferences.d.ts`
- 类型化的 `ValueType` 联合：`number | string | boolean | Array<number> | Array<string> | Array<boolean> | Uint8Array`
- 良好使用字面量类型作为常量：`MAX_KEY_LENGTH: 80`、`MAX_VALUE_LENGTH: 8192`
- 一致的双回调/Promise 模式

#### `@ohos.data.rdb.d.ts`（已弃用）
- 使用 `@useinstead ohos.data.relationalStore` 正确弃用

### 4.5 安全和加密

#### `@ohos.security.cryptoFramework.d.ts`
- **F-4.5.1（中危）：** `Result` 枚举使用与全局错误码401重叠的通用名称（`INVALID_PARAMS = 401`）。这可能让开发者困惑于应检查 `BusinessError.code` 还是 `Result`。
- **F-4.5.2（提示）：** `@atomicservice` 标记不一致 -- `NOT_SUPPORT` 有 `@crossplatform` 但在 API 11 块中没有 `@atomicservice`，而 `INVALID_PARAMS` 和 `ERR_OUT_OF_MEMORY` 有
- `DataBlob` 包装 `Uint8Array` 是合理的模式
- `ParamsSpec` 使用 `algName: string` 而非类型化枚举，类型安全性较低

#### `@ohos.security.cert.d.ts`
- 29个 `@deprecated` 标签 -- 正在演进

#### `@ohos.abilityAccessCtrl.d.ts`
- 良好使用 `Permissions` 类型作为权限名称
- 正确弃用带字符串参数的 `verifyAccessToken`，改用带 `Permissions` 参数的 `checkAccessToken`
- 提供同步（`verifyAccessTokenSync`）和异步版本

### 4.6 UI 框架 (ArkUI)

#### `@ohos.router.d.ts`
- **F-4.6.1（高危）：** `params?: Object` 应使用更具体的类型如 `Record<string, string | number | boolean | object>`
- 结构良好的 `RouterMode` 枚举，文档完善
- `RouterState` 提供有用的 index/name/path 信息

#### `@ohos.window.d.ts`
- **F-4.6.2（中危）：** `WindowType` 枚举将公共和 `@systemapi` 值混在一起，没有明确分隔，使开发者不清楚哪些值可以实际使用
- 初始命名空间声明缺少 `@since`（第一个块未指定版本）
- 非常大的文件 -- 建议拆分

#### `@internal/component/ets/common.d.ts`
- **F-4.6.3（低危）：** `ComponentOptions.freezeWhenInactive` 使用逗号分隔符 (`,`) 而非分号 (`;`) -- 虽然在 TypeScript 中有效，但与代码库其余部分不一致

#### ArkUI 高级组件 (`@ohos.arkui.advanced.*.d.ets`)
- 24个使用 `.d.ets` 扩展名的高级 UI 组件
- 组件声明遵循一致的模式

### 4.7 多媒体

#### `@ohos.multimedia.camera.d.ts`
- **F-4.7.1（提示）：** Profile 属性上良好使用 `readonly` 修饰符
- 结构良好的枚举类型（`CameraStatus`、`CameraFormat`）
- 55个 `@deprecated` 标签表示活跃的演进
- 错误码使用领域特定范围（7400xxx） -- 良好实践

#### `@ohos.multimedia.image.d.ts`
- **F-4.7.2（低危）：** 第一个命名空间声明块（`@since 6`）缺少 `@syscap` 标签，而后续块有
- 18个 `@deprecated` 标签
- `PixelMapFormat` 的枚举设计良好

#### `@ohos.multimedia.audio.d.ts`
- 74个 `@deprecated` 标签 -- 广泛的 API 演进

### 4.8 连接（蓝牙/Wi-Fi/NFC）

#### 蓝牙 API 系列
- **F-4.8.1（中危）：** 三层弃用链（见 F-3.3.2）
- `@ohos.bluetooth.d.ts`：339个已弃用项 -- 自 API 9 起完全弃用
- `@ohos.bluetoothManager.d.ts`：自 API 10 起弃用
- 当前：模块化的 `@ohos.bluetooth.access.d.ts`、`@ohos.bluetooth.ble.d.ts` 等

#### `@ohos.wifiManager.d.ts`
- **F-4.8.2（低危）：** `isWifiActive()` JSDoc 块中的缩进不一致 -- API 11 块的缩进（第73-83行）与周围代码不一致
- 良好的 `@throws` 文档，使用领域特定错误码（2501xxx）
- `scan()` 在 API 10 弃用改用 `startScan()`（生命周期非常短）

#### `@ohos.wifi.d.ts`
- 213个 `@deprecated` 标签，80个 `@permission` 标签 -- 完全弃用
- 旧模式：返回 boolean 表示成功/失败

### 4.9 Ability 框架

#### `@ohos.app.ability.UIAbility.d.ts`
- **F-4.9.1（低危）：** `OnReleaseCallback` 和 `OnRemoteStateChangeCallback` 是可统一的相同函数接口
- **F-4.9.2（提示）：** `CalleeCallback` 返回类型 `rpc.Parcelable` 要求开发者理解 RPC 序列化机制
- `Caller.call()` 和 `Caller.callWithResult()` 上的错误码文档完善

#### `@ohos.app.ability.Want.d.ts`
- 参数使用 `Object` 类型（2处）

#### 已弃用的 ability API
- `@ohos.ability.*.d.ts` 文件已正确弃用，改用 `@ohos.app.ability.*.d.ts`
- `@ohos.application.*.d.ts` 文件也已弃用 -- 两代弃用

### 4.10 实用工具和集合

#### `@ohos.util.d.ts`
- **F-4.10.1（中危）：** `printf`（已弃用）和 `format` 都接受 `...args: Object[]` -- 应为 `...args: unknown[]`
- 良好的实用类：`TextDecoder`、`TextEncoder` 等

#### 集合 API（`@ohos.util.ArrayList.d.ts`、`HashMap`、`TreeMap` 等）
- **F-4.10.2（中危）：** 所有集合的 `forEach` 和 `replaceAllElements` 方法使用 `thisArg?: Object` 参数。在现代 TypeScript 中，`thisArg` 很少有用（首选箭头函数）。`Object` 类型至少应为 `unknown`。
- 所有集合上良好的泛型类型参数（`<T>`、`<K, V>`）
- 集合之间一致的 API 接口（forEach、length 等）

#### `@ohos.worker.d.ts`
- **F-4.10.3（高危）：** `transfer?: Object[]` 应为 `ArrayBuffer[]` 或 `Transferable[]` 以匹配 Web Worker API
- 文件中33处使用 `Object` 类型 -- `Object` 使用密度最高

### 4.11 系统服务

#### `@ohos.pasteboard.d.ts`
- 类型良好的 `ValueType`：`string | image.PixelMap | Want | ArrayBuffer`
- 良好的 MIME 类型常量
- 正确的弃用模式（`createHtmlData` -> `createData`）

#### `@ohos.events.emitter.d.ts`
- **F-4.11.1（高危）：** `EventData.data` 类型为 `{ [key: string]: any }` -- 对于通用事件系统，`any` 在此不可避免，但应提供安全使用指导文档
- **F-4.11.2（低危）：** 重载之间事件 ID 类型不一致：`on(event: InnerEvent)` 使用 `InnerEvent.eventId: number`，但 `on(eventId: string)` 接受字符串。`off` 方法也同时接受 `number` 和 `string`。这种双类型系统可能会让开发者困惑。

#### `@ohos.request.d.ts`
- **F-4.11.3（低危）：** 错误码常量错误地标有 `@permission ohos.permission.INTERNET` 标签 -- 常量不需要权限

#### `@ohos.commonEventManager.d.ts`
- `publish` 缺少 Promise 重载（见 F-3.4.1）

### 4.12 电话

#### `@ohos.telephony.radio.d.ts`
- 10个 `@deprecated` 标签，10个 `@permission` 标签
- 权限要求文档完善

#### `@ohos.telephony.sim.d.ts`
- 66个 `@permission` 标签 -- 如 SIM 操作预期般高度权限保护
- 良好的安全模型

### 4.13 企业管理

#### `@ohos.enterprise.*.d.ts` 系列
- 一致标记为 `@systemapi`
- 良好的 `@throws` 文档
- 每个文件5-30个 `@permission` 标签
- 良好地分离为领域特定模块（accountManager、applicationManager、bluetoothManager 等）

---

## 5. 按严重级别汇总所有发现

### 严重（0）
没有会导致运行时故障或安全漏洞的严重问题。

### 高危（7）
| 编号 | 发现 | 受影响文件 |
|----|---------|---------------|
| F-3.1.1 | 公共 API 中的 `any` 类型 | 5+个文件 |
| F-3.1.2 | 普遍使用 `Object` 类型（246+处） | 30+个文件 |
| F-3.2.1 | `AssetStatus` 中的复制粘贴 JSDoc 错误 | `@ohos.data.relationalStore.d.ts` |
| F-4.2.1 | HTTP 头类型为 `Object` | `@ohos.net.http.d.ts` |
| F-4.2.2 | HTTP extraData 使用 `Object` 类型 | `@ohos.net.http.d.ts` |
| F-4.6.1 | 路由参数类型为 `Object` | `@ohos.router.d.ts` |
| F-4.10.3 | Worker 传输类型为 `Object[]` | `@ohos.worker.d.ts` |

### 中危（12）
| 编号 | 发现 | 受影响文件 |
|----|---------|---------------|
| F-3.1.3 | Permissions 类型为普通 `string` | `permissions.d.ts` |
| F-3.2.4 | 公共 JSDoc 中的内部变更说明 | `@ohos.data.relationalStore.d.ts` |
| F-3.3.1 | 大量已弃用 API 仍在发布 | 70+个文件 |
| F-3.3.2 | 蓝牙 API 在两个版本中弃用了两次 | 3个文件 |
| F-3.4.1 | 缺少 Promise 重载 | `@ohos.commonEventManager.d.ts` |
| F-3.4.2 | 错误码文档格式不一致 | 多个文件 |
| F-3.5.1 | 混合文件命名规范（ETS 组件） | `@internal/component/ets/` |
| F-3.6.1 | 系统 API 暴露在公共 SDK 中 | 多个文件 |
| F-4.2.3 | HTTP 响应类型与请求类型不关联 | `@ohos.net.http.d.ts` |
| F-4.5.1 | Crypto Result 枚举与全局错误码重叠 | `@ohos.security.cryptoFramework.d.ts` |
| F-4.6.2 | WindowType 混合公共/系统值 | `@ohos.window.d.ts` |
| F-4.10.2 | 集合 forEach 对 thisArg 使用 `Object` | 10+个 util 文件 |

### 低危（10）
| 编号 | 发现 | 受影响文件 |
|----|---------|---------------|
| F-3.2.2 | 弃用模块缺少 `@file` 标签 | `@ohos.notification.d.ts` |
| F-3.2.3 | 返回描述大小写不一致 | 多个文件 |
| F-3.3.3 | Wi-Fi 扫描一个版本后弃用 | `@ohos.wifiManager.d.ts` |
| F-3.5.2 | 模块命名模式不一致 | 多个文件 |
| F-3.5.3 | 错误常量标有 `@permission` | `@ohos.request.d.ts` |
| F-4.6.3 | 接口中逗号与分号 | `@internal/component/ets/common.d.ts` |
| F-4.7.2 | 初始命名空间块缺少 `@syscap` | `@ohos.multimedia.image.d.ts` |
| F-4.8.2 | 缩进不一致 | `@ohos.wifiManager.d.ts` |
| F-4.9.1 | 重复的相同回调接口 | `@ohos.app.ability.UIAbility.d.ts` |
| F-4.11.2 | 事件 ID 类型不一致 | `@ohos.events.emitter.d.ts` |

### 提示（5）
| 编号 | 发现 |
|----|---------|
| F-3.2.5 | JSDoc 中的双空格拼写错误 |
| F-3.6.2 | caPath 缺少安全警告 |
| F-3.6.3 | 剪贴板访问在声明中未受权限保护 |
| F-3.7.1 | 版本演进导致大量 JSDoc 重复 |
| F-4.5.2 | crypto 中 atomicservice 标记不一致 |

---

## 6. 建议

### 即时行动（适用于 4.1 补丁或 4.2）
1. **修复 `AssetStatus` 枚举的复制粘贴 JSDoc 错误**（`@ohos.data.relationalStore.d.ts` 中）(F-3.2.1)
2. **将 HTTP 头的 `Object` 替换为 `Record<string, string>`**（`@ohos.net.http.d.ts` 和 `@ohos.net.webSocket.d.ts` 中）(F-4.2.1)
3. **将 `@ohos.worker.d.ts` 传输列表的 `Object[]` 替换为 `ArrayBuffer[]`** (F-4.10.3)

### 短期（下一个主要版本）
4. **审计所有 `any` 和 `Object` 使用** -- 替换为 `unknown`、`Record<>` 或领域特定类型
5. **为仅回调版本的 API 添加 Promise 重载** (F-3.4.1)
6. **标准化 `@throws` 注释的 JSDoc 格式**（空格、大小写、句号）
7. **从面向公众的 JSDoc 注释中移除内部变更说明**

### 长期
8. **考虑 JSDoc 压缩方法**以减少版本演进导致的3倍 JSDoc 块重复
9. **探索将 `Permissions` 设为字符串字面量联合类型**以实现编译时权限名称验证
10. **评估移除已弃用2+个主要版本的 API**（鉴于现在是 API 11+，所有在 API 9 弃用的内容）
11. **将 `@systemapi` 声明分离**到独立的 SDK 包中，避免让第三方开发者困惑

---

*审查结束。*
