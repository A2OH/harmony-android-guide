# OpenHarmony 4.1 内置应用代码审查

## 目录

- [1. 执行摘要](#1-执行摘要)
- [2. 审查范围与方法](#2-审查范围与方法)
- [3. 应用清单](#3-应用清单)
- [4. 关键安全发现](#4-关键安全发现)
  - [4.1 示例应用中硬编码的加密密钥](#41-示例应用中硬编码的加密密钥)
  - [4.2 WiFi 预共享密钥通过二维码泄露](#42-wifi-预共享密钥通过二维码泄露)
  - [4.3 令牌值被记录到控制台](#43-令牌值被记录到控制台)
- [5. 架构与代码质量发现](#5-架构与代码质量发现)
  - [5.1 广泛使用 globalThis 作为服务定位器](#51-广泛使用-globalthis-作为服务定位器)
  - [5.2 深度嵌套的相对导入路径](#52-深度嵌套的相对导入路径)
  - [5.3 大量使用 ts-ignore 和 ts-nocheck](#53-大量使用-ts-ignore-和-ts-nocheck)
  - [5.4 过度使用 any 类型](#54-过度使用-any-类型)
  - [5.5 跨应用使用已弃用的 API](#55-跨应用使用已弃用的-api)
- [6. 错误处理发现](#6-错误处理发现)
  - [6.1 SystemUI 中静默吞没的错误](#61-systemui-中静默吞没的错误)
  - [6.2 示例应用中的空 catch 块](#62-示例应用中的空-catch-块)
  - [6.3 详细错误序列化泄露堆栈跟踪](#63-详细错误序列化泄露堆栈跟踪)
- [7. 权限使用发现](#7-权限使用发现)
  - [7.1 SystemUI 请求 21 项以上权限](#71-systemui-请求-21-项以上权限)
  - [7.2 敏感权限缺少 reason 字段](#72-敏感权限缺少-reason-字段)
  - [7.3 运行时权限请求缺乏适当处理](#73-运行时权限请求缺乏适当处理)
- [8. UI/UX 模式发现](#8-uiux-模式发现)
  - [8.1 硬编码颜色值而非主题令牌](#81-硬编码颜色值而非主题令牌)
  - [8.2 跨应用不一致的组件结构](#82-跨应用不一致的组件结构)
  - [8.3 布局值中的魔法数字](#83-布局值中的魔法数字)
- [9. 应用专项发现](#9-应用专项发现)
  - [9.1 Launcher](#91-launcher)
  - [9.2 SystemUI](#92-systemui)
  - [9.3 Settings](#93-settings)
  - [9.4 ScreenLock](#94-screenlock)
  - [9.5 Notes](#95-notes)
  - [9.6 MMS](#96-mms)
- [10. 第三方开发者应避免的反模式](#10-第三方开发者应避免的反模式)
- [11. 按严重程度汇总发现](#11-按严重程度汇总发现)
- [12. 建议](#12-建议)

---

## 1. 执行摘要

本次审查涵盖了 OpenHarmony 4.1 Release 附带的内置应用，位于 `/home/dspfac/openharmony/applications/standard/`。这些应用既是系统组件，也是第三方开发者的参考实现。审查发现了 **3 个关键安全问题**、**5 个高严重性代码质量问题**，以及大量中等和低严重性问题，这些问题共同削弱了这些应用的示范作用。

最严重的发现是一个示例应用附带了硬编码的 RSA 私钥和明文测试密码（`123456`）。此外，WiFi 设置页面构造了包含明文 WiFi 预共享密钥的二维码，可能导致凭据泄露。在架构方面，这些应用大量依赖 `globalThis` 进行状态管理（一种已弃用的反模式），使用深度嵌套的相对导入（8 层以上），并广泛使用 `@ts-nocheck` 和 `@ts-ignore` 来抑制 TypeScript 类型检查。

**已审查的应用：** Launcher（274 个源文件）、SystemUI（272 个文件）、Settings（149 个文件）、ScreenLock、Screenshot、Notes、Contacts、Camera、MMS、Permission Manager、FilePicker、DLP Manager、Theme、CalendarData、Photos、PrintSpooler、Admin Provisioning、Call、Security Privacy Center、Auth Widget。

---

## 2. 审查范围与方法

- **源码路径：** `/home/dspfac/openharmony/applications/standard/`
- **文件类型：** `.ts`、`.ets`、`.json5`（模块配置）
- **重点领域：** 安全性（凭据、日志记录、数据泄露）、代码质量（类型、架构、错误处理）、权限使用、作为开发者参考的 UI/UX 模式
- **排除项：** `app_samples/` 仅在安全扫描中发现问题时才被纳入；它包含开发者示例，而非出厂系统代码

---

## 3. 应用清单

| 应用 | 路径 | 源文件数 | 描述 |
|---|---|---|---|
| Launcher | `launcher/` | 274 | 主屏幕、应用网格、文件夹、底部导航栏 |
| SystemUI | `systemui/` | 272 | 状态栏、通知面板、控制中心 |
| Settings | `settings/` | 149 | 系统设置（WiFi、蓝牙、显示等） |
| ScreenLock | `screenlock/` | ~100 | 锁屏界面、PIN/图案/密码认证 |
| Camera | `camera/` | ~80 | 相机应用（拍照、视频、多模式） |
| Notes | `notes/` | ~40 | 笔记应用 |
| Contacts | `contacts/` | ~30 | 联系人管理 |
| Contacts Data | `contacts_data/` | ~20 | 联系人数据提供者 |
| MMS | `mms/` | ~50 | 短信应用 |
| Photos | `photos/` | ~60 | 相册 |
| Permission Manager | `permission_manager/` | ~30 | 运行时权限对话框 |
| Screenshot | `screenshot/` | 8 | 截屏捕获与显示 |
| FilePicker | `filepicker/` | ~20 | 文件选择界面 |
| DLP Manager | `dlp_manager/` | ~20 | 文档保护 |
| Theme | `theme/` | ~10 | 壁纸/主题管理 |
| CalendarData | `calendardata/` | ~30 | 日历数据提供者 |
| Settings Data | `settings_data/` | ~10 | 设置数据提供者 |
| PrintSpooler | `printspooler/` | ~15 | 打印管理 |
| Call | `call/` | ~20 | 电话/通话管理 |
| Security Privacy Center | `security_privacy_center/` | ~30 | 证书管理 |
| Admin Provisioning | `admin_provisioning/` | ~10 | 企业设备管理 |
| Auth Widget | `applications_auth_widget/` | ~10 | 认证小组件 |

---

## 4. 关键安全发现

### 4.1 示例应用中硬编码的加密密钥

**严重程度：关键**
**文件：** `/home/dspfac/openharmony/applications/standard/app_samples/code/BasicFeature/Security/PaySecurely/cloud/src/main/ets/PayServer.ts`

一个示例支付应用附带了硬编码的 RSA-1024 私钥和明文测试密码：

```typescript
const PAY_PRI_KEY = 'MIICdQIBADANBgkqhkiG9w0BAQEFAAS...'; // Full RSA private key
const MERCHANT_PUB_KEY = 'MFkwEwYHKoZIzj0CAQY...';
const PAY_CLIENT_PRI_KEY = 'MIICdwIBADANBgkqhkiG9w0BAQEFAAS...';
const TEST_USER: User = {
  name: 'test',
  payPassword: '123456',
};
```

其他问题：
- 使用 RSA-1024，该算法被认为加密强度不足（建议最低使用 RSA-2048）
- 使用 `RSA1024|PKCS1` 填充（PKCS#1 v1.5），容易受到填充预言攻击；应改用 OAEP
- 硬编码的凭据被作为"安全支付"功能的参考实现呈现，直接与其声称的目的相矛盾

虽然这位于 `app_samples/` 中，但它是作为官方代码示例分发的。复制此模式的开发者将引入关键漏洞。

### 4.2 WiFi 预共享密钥通过二维码泄露

**严重程度：关键**
**文件：** `/home/dspfac/openharmony/applications/standard/settings/product/phone/src/main/ets/pages/wifi.ets`（第 793-798 行）

WiFi 设置详情对话框构造了一个包含明文 WiFi SSID 和预共享密钥的二维码：

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

该值随后直接渲染为 `QRCode` 组件（第 676 行），这意味着 WiFi 密码被编码到屏幕上可见的二维码中。任何对准屏幕的摄像头都可以捕获该凭据。

### 4.3 令牌值被记录到控制台

**严重程度：高**
**文件：** `/home/dspfac/openharmony/applications/standard/dlp_manager/entry/src/main/ets/feature/HomeFeature.ets`（第 125 行）

```typescript
console.log(TAG, 'sandBoxLinkFileHome start', callerToken);
```

DLP Manager（文档保护应用）中令牌标识符被记录到控制台。虽然这些可能是能力令牌而非认证令牌，但在安全敏感应用中记录任何令牌都值得关注，因为日志可能被持久化或被其他应用访问。

---

## 5. 架构与代码质量发现

### 5.1 广泛使用 globalThis 作为服务定位器

**严重程度：高**

Launcher 和其他应用广泛使用 `globalThis` 作为全局状态容器和服务定位器。仅在 Launcher 中就发现了 30 多处不同的使用：

```typescript
// Launcher AppModel.ts
static getInstance(): AppModel {
    if (globalThis.AppModel == null) {
      globalThis.AppModel = new AppModel();
    }
    return globalThis.AppModel;
}
```

其他示例：
- `globalThis.desktopContext` - 存储 ability 上下文
- `globalThis.ResourceManager` - 缓存资源管理器
- `globalThis.WindowManager` - 窗口管理单例
- `globalThis.SettingsModelInstance` - 设置模型单例
- `globalThis.abilityWant`（Theme 应用）
- `globalThis.abilityContext`（FilePicker）

**影响：** OpenHarmony 文档明确不鼓励使用 `globalThis`。它创建隐式依赖关系，使测试变得困难，并可能导致命名冲突。它还绕过了 ArkTS 的严格类型检查。

**受影响文件：** launcher、systemui、screenlock、filepicker、theme 等中超过 100 个文件。

### 5.2 深度嵌套的相对导入路径

**严重程度：中**

162 个文件中的 404 条导入语句使用了 8 层或更多 `../` 段的相对路径：

```typescript
// screenLockService.ts
import Log from '../../../../../../../../common/src/main/ets/default/Log';
import Trace from '../../../../../../../../common/src/main/ets/default/Trace'
import ScreenLockModel from './screenLockModel';
import AccountModel from './accountsModel'
import {ScreenLockStatus} from '../../../../../../../../common/src/main/ets/default/ScreenLockCommon';
```

这在 SystemUI（100 多处）和 ScreenLock（50 多处）中尤为普遍。

**影响：** 脆弱的导入在目录重构时会断裂，可读性极差，并且为第三方开发者树立了不好的榜样。应使用路径别名或模块引用。

### 5.3 大量使用 ts-ignore 和 ts-nocheck

**严重程度：高**

在内置应用中发现了 30 多处 `@ts-ignore` 和 `@ts-nocheck` 的使用：

| 应用 | 出现次数 | 说明 |
|---|---|---|
| MMS | 17 处（`@ts-ignore`）、3 处（`@ts-nocheck`） | 整个文件和单行 |
| ScreenLock | 3 处（`@ts-nocheck` 在 WindowManager、TimeManager、SysFaultLogger 上） |
| SystemUI | 1 处（`@ts-nocheck` 在 WindowManager 上） |
| Launcher | 1 处（`@ts-nocheck` 在 SettingsDataManager 上） |
| PrintSpooler | 5 处（`@ts-ignore`） |
| Theme | 2 处（`@ts-nocheck`） |
| Photos | 1 处（`@ts-ignore`） |

**影响：** 这些指令完全抑制了类型检查，削弱了 ArkTS 的安全保障。在系统应用的关键文件如 `WindowManager.ts` 和 `SysFaultLogger.ts` 上使用 `@ts-nocheck` 尤其令人担忧。

### 5.4 过度使用 any 类型

**严重程度：中**

`any` 类型被大量使用：
- Launcher：20 个文件中出现 62 次
- SystemUI：在 ViewModel 和 Service 中广泛使用
- Settings：WiFi 连接代码中的 `connectParam: any`（`WifiModel.ts` 第 320 行）

WiFi 模型中的示例：
```typescript
let connectParam: any = {
    "ssid": apInfo.ssid,
    "bssid": apInfo.bssid,
    "preSharedKey": password,
    ...
};
```

**影响：** 破坏类型安全，允许运行时类型错误，并树立了不良示范。ArkTS 在更严格的模式下有意限制 `any` 的使用。

### 5.5 跨应用使用已弃用的 API

**严重程度：中**

15 个以上应用的 34 个文件导入了已弃用的 `@system.*` 模块：

- `@system.router` - 已弃用，建议使用 `@ohos.router`
- `@system.prompt` - 已弃用，建议使用 `@ohos.promptAction`
- `@system.app` - 已弃用，建议使用 `@ohos.app.ability`

受影响的应用包括：MMS、Settings、Notes、ScreenLock、Launcher、Contacts、Call、Photos、Camera。

ScreenLock 服务（`screenLockService.ts`）使用 `@system.router` 中的 `Router` 进行页面导航，在同一文件中混合使用新旧 API。

---

## 6. 错误处理发现

### 6.1 SystemUI 中静默吞没的错误

**严重程度：高**

多个 SystemUI 文件的 `.catch()` 处理器完全丢弃了错误：

```typescript
// StatusBarVM.ts lines 200-204
this.mStatusBarEnable ? WindowManager.showWindow(WindowType.STATUS_BAR).then(() => {
}).catch((err) => {
}) : WindowManager.hideWindow(WindowType.STATUS_BAR).then(() => {
}).catch((err) => {
});
```

此模式至少在 SystemUI 的 15 个位置出现：
- `product/pc/notificationpanel/src/main/ets/pages/index.ets`
- `product/phone/statusbar/src/main/ets/ServiceExtAbility/ServiceExtAbility.ts`
- `product/pc/statusbar/src/main/ets/ServiceExtAbility/ServiceExtAbility.ts`
- `product/pc/controlpanel/src/main/ets/pages/index.ets`
- `product/pc/controlpanel/src/main/ets/ServiceExtAbility/ServiceExtAbility.ts`

**影响：** 状态栏和控制面板中的窗口管理失败将完全不可见，导致难以诊断的 UI 问题。在系统应用中，窗口创建失败可能导致用户没有状态栏或控制面板。

### 6.2 示例应用中的空 catch 块

**严重程度：中**

在 `app_samples/` 中发现多个空 catch 块：
- `VoiceCallDemo/BufferModel.ets` - 3 个空 catch 块（音频缓冲区操作）
- `VoiceCallDemo/Index.ets` - 1 个空 catch 块
- `VoiceCallDemo/TimerUtil.ets` - 1 个空 catch 块
- `Asset/AssetModel.ets` - 1 个空 catch 块（安全资产操作）
- `AbilityFeature/CharacterOperation.ets` - 1 个空 catch 块

**影响：** 作为参考代码，这向开发者传达了静默忽略错误是可以接受的。

### 6.3 详细错误序列化泄露堆栈跟踪

**严重程度：低**

所有应用中有数百处 `JSON.stringify(error)` 的日志输出实例。虽然对调试有用，但在生产日志中可能会暴露内部实现细节。

Settings 中的示例：
```typescript
LogUtil.error(`${this.TAG_PAGE}, start failed. Cause: ${JSON.stringify(error)}`);
```

---

## 7. 权限使用发现

### 7.1 SystemUI 请求 21 项以上权限

**严重程度：中**
**文件：** `/home/dspfac/openharmony/applications/standard/systemui/entry/phone/src/main/module.json5`

SystemUI 请求 21 项权限，包括：
- `ohos.permission.CAPTURE_SCREEN`
- `ohos.permission.MANAGE_LOCAL_ACCOUNTS`
- `ohos.permission.INTERACT_ACROSS_LOCAL_ACCOUNTS_EXTENSION`
- `ohos.permission.ACCESS_SERVICE_DM`
- `ohos.permission.CONNECTIVITY_INTERNAL`
- `ohos.permission.START_INVISIBLE_ABILITY`
- `ohos.permission.START_ABILITIES_FROM_BACKGROUND`

虽然 SystemUI 确实需要许多权限，但其广度值得关注。某些权限（如 `MANAGE_WIFI_CONNECTION`、`SET_WIFI_INFO`）对于状态栏应用可能不是必需的，可以委托给 Settings 应用处理。

### 7.2 敏感权限缺少 reason 字段

**严重程度：中**

大多数 `requestPermissions` 条目缺少 `reason` 字段。SystemUI 的 21 项权限中只有 2 项包含 reason 字符串：

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

其余 19 项权限没有 reason。Launcher 类似地有 9 项权限，全部没有 reason 字段。虽然系统应用可能不会显示权限对话框，但包含 reason 是最佳实践，也可作为文档说明。

### 7.3 运行时权限请求缺乏适当处理

**严重程度：中**

Notes 应用请求运行时权限但结果检查不一致：

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

权限结果（`sum`）被计算但从未与预期值进行比较。代码无论权限是否被授予都会继续使用受保护的 API。

此外，Notes 应用请求 `ohos.permission.MANAGE_DISPOSED_APP_STATUS`（一项高权限系统权限），对于笔记应用来说似乎过于宽泛。

---

## 8. UI/UX 模式发现

### 8.1 硬编码颜色值而非主题令牌

**严重程度：低**

多个应用使用硬编码颜色字符串而非系统主题令牌：

```typescript
// wifi.ets line 206
.selectedColor('#007DFF')

// StatusBarVM.ts line 294
this.setComponentContent(id, '#FFFFFFFF');
```

虽然大多数颜色引用正确使用了 `$r('sys.color.*')` 系统资源，但这种不一致意味着这些元素不会响应深色模式或主题更改。

### 8.2 跨应用不一致的组件结构

**严重程度：低**

不同的应用使用不同的架构模式，没有统一标准：
- **Launcher：** 带有 ViewModel/Model 分离的 MVVM，某些功能使用 `Presenter` 模式
- **SystemUI：** 带有 VM（如 `StatusBarVM`）和 Service 的 MV 模式，使用 `createOrGet` 单例辅助方法
- **Settings：** 带有绑定到页面的 `Controller` 类的 MVC 模式
- **ScreenLock：** 面向服务的架构，由 `ScreenLockService` 管理所有状态
- **Notes：** 最小分离的直接组件状态管理

这种不一致使开发者更难学习规范的 OpenHarmony 应用架构。

### 8.3 布局值中的魔法数字

**严重程度：低**

虽然大多数布局值通过 `$r('app.float.*')` 资源正确引用，但也有显著的例外：

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

## 9. 应用专项发现

### 9.1 Launcher

**架构：** 基于功能的模块化（bigfolder、smartdock、appcenter、form、pagedesktop、settings、numbadge、gesturenavigation）。使用带有 ViewModel 和 Model 的 MVVM 模式。

**优点：**
- 良好的模块分解，功能边界清晰
- 一致使用带有 TAG 标识的 `Log` 工具
- 通过 `LocalEventManager` 实现跨功能通信的事件系统
- 生命周期感知的资源清理（注销监听器）

**问题：**
- 大量依赖 `globalThis` 实现单例（AppModel、SettingsModel、FormModel、WindowManager 等）
- 未类型化的监听器参数：`registerStateChangeListener(listener)` 接受 `any`
- `SettingsDataManager.ts` 顶部有 `@ts-nocheck`，禁用所有类型检查
- `getItemIndex()` 在 `for...of` 循环内使用 `indexOf` — 冗余的双重迭代

### 9.2 SystemUI

**架构：** 基于功能组件的架构，包含状态栏、控制中心、通知面板、音量、亮度、蓝牙、定位、信号、NFC、铃声模式和自动旋转组件。

**优点：**
- Service（数据）、ViewModel（状态）和 Component（UI）层之间的清晰分离
- 用于协调状态栏主题的 `TintStateManager`
- 可扩展状态栏项目的插件架构

**问题：**
- `WindowManager.ts` 使用 `@ts-nocheck` — 这是最关键的基础设施文件
- 窗口管理操作上的空 `.catch()` 处理器（参见 6.1）
- `StatusBarVM` 有许多 `Log.showInfo()` 调用，在每次状态更改时序列化完整数据对象，造成性能开销
- `updateStatusBarData()` 使用 `for (let key in data)` 会迭代继承的属性

### 9.3 Settings

**架构：** 带有页面级组件和 Controller 的 MVC 模式。使用共享组件库和工具模块。

**优点：**
- 良好使用 `GridContainer` 实现响应式布局
- 适当的设备类型检测以适配手机与平板布局
- WiFi 模型与 UI 的清晰分离

**问题：**
- WiFi 二维码以明文暴露预共享密钥（参见 4.2）
- WiFi 密码页面（`wifiPsd.ets`）在组件 `@State` 中以明文字符串存储密码
- `PasswordSettingController` 通过字符串属性名绑定到组件（`bindProperties(["passwordList", "pinChallenge"])`）— 脆弱且未类型化
- `pinChallenge` 通过路由参数以明文传递
- 使用已弃用的 `@system.router` 和 `@system.prompt`

### 9.4 ScreenLock

**架构：** 面向服务的架构，`ScreenLockService` 作为中央协调器，`ScreenLockModel` 用于系统交互，`AccountModel` 用于用户认证。

**优点：**
- 带有 PSS 限制跟踪和故障日志的内存监控
- 适当的人脸认证状态管理
- 事件驱动架构，生命周期清晰

**问题：**
- 模块级可变变量（`mRouterPath`、`mWillRecognizeFace`、`mUnLockBeginAnimation`）在类外部 — 共享全局状态
- `authUser()` 方法通过 `join('')` 将密码数组转换为字符串并传递给 `registerPWDInputer` — 密码处理可以改进
- 回调参数未类型化：`authUser(authSubType, passwordData, callback)` — `callback` 没有类型注解
- `SERVICE_RESTART` 事件在硬编码的 2000 毫秒延迟后重连，没有重试限制

### 9.5 Notes

**架构：** 带有共享组件和工具的功能模块。使用直接的 ArkUI 状态管理。

**问题：**
- 请求 `ohos.permission.MANAGE_DISPOSED_APP_STATUS` — 一项对笔记应用似乎不必要的高权限系统权限
- 在使用时请求 `READ_MEDIA` 和 `WRITE_MEDIA`，未检查是否已授权
- 使用已弃用的 `@system.router` 进行导航
- 硬编码包名 `"com.ohos.photos"` 用于图片选择 — 应使用基于 intent 的路由

### 9.6 MMS

**架构：** 基于 Controller 的页面，带有 Service 和 Model 层。

**问题：**
- 在所有内置应用中 `@ts-ignore`（17 处）和 `@ts-nocheck`（3 处）的使用浓度最高
- 大量使用已弃用的 `@system.router`、`@system.prompt`
- 大量使用 `globalThis` 进行状态管理
- 多个 Controller 使用未类型化的参数

---

## 10. 第三方开发者应避免的反模式

由于内置应用作为参考实现，以下模式**不应**被复制：

| 反模式 | 出现位置 | 应采取的做法 |
|---|---|---|
| 使用 `globalThis` 实现单例 | Launcher、SystemUI、FilePicker | 使用 `AppStorage`、模块级导出或依赖注入 |
| `@ts-nocheck` / `@ts-ignore` | MMS、ScreenLock、SystemUI、Launcher | 修复实际的类型错误；使用适当的接口 |
| 8 层以上相对导入 | SystemUI、ScreenLock、Settings | 在 `tsconfig.json` 中使用路径别名或模块引用 |
| 空 `.catch()` 处理器 | SystemUI | 至少记录错误；考虑恢复/重试 |
| 已弃用的 `@system.*` 导入 | Settings、MMS、Notes、ScreenLock | 使用 `@ohos.router`、`@ohos.promptAction` |
| 硬编码颜色字符串 | Settings（WiFi）、SystemUI | 使用 `$r('sys.color.*')` 系统设计令牌 |
| 未类型化的函数参数 | Launcher、ScreenLock | 使用 TypeScript 接口和类型注解 |
| 记录敏感数据 | DLP Manager、WiFi 设置 | 对日志进行脱敏；永远不要记录令牌、密钥或密码 |
| `any` 类型使用 | Launcher、Settings、SystemUI | 定义适当的接口 |
| 硬编码包名 | Notes | 使用 intent action 进行隐式能力发现 |

---

## 11. 按严重程度汇总发现

| 严重程度 | 数量 | 主要项目 |
|---|---|---|
| **关键** | 2 | 示例应用中硬编码的 RSA 私钥 + 密码；二维码中的 WiFi PSK |
| **高** | 4 | DLP Manager 中的令牌日志记录；`globalThis` 使用（100 多个文件）；关键文件上的 `@ts-nocheck`；SystemUI 中静默吞没的错误 |
| **中** | 7 | 深度相对导入（404 处）；`any` 类型（仅 Launcher 就有 62 处以上）；已弃用的 API（34 个文件）；SystemUI 21 项以上权限；缺少权限 reason；未处理的权限结果；示例中的空 catch 块 |
| **低** | 3 | 硬编码颜色；不一致的架构；魔法数字 |

**总计：16 项不同的发现**

---

## 12. 建议

### 立即行动（安全）

1. **移除或替换** `PaySecurely` 示例中的硬编码密钥。添加醒目警告说明示例凭据绝不能用于生产环境。升级到使用 OAEP 填充的 RSA-2048。
2. **修复 WiFi 二维码**，使 Settings 中的二维码不包含预共享密钥，或至少在显示二维码前要求认证（PIN/生物识别）。
3. **移除令牌日志记录**，从 DLP Manager 中删除。审计所有安全敏感应用中的 `console.log` 和 `Log.show*` 调用，检查凭据/令牌泄露。

### 短期（代码质量）

4. **消除所有内置应用中的 `globalThis` 使用**。迁移到 `AppStorage` 或正确类型化的模块级单例。
5. **移除所有 `@ts-nocheck`** 指令，特别是 `WindowManager.ts` 和 `SysFaultLogger.ts` 上的。修复底层类型问题。
6. **替换 SystemUI 中的空 `.catch()` 处理器**，使用适当的错误日志记录和优雅降级。
7. **将已弃用的 API 导入**从 `@system.*` 迁移到 `@ohos.*` 对应项。

### 中期（架构）

8. **建立规范的应用架构**模式，并与这些应用一起记录文档。选择一种模式（推荐 MVVM）并一致地应用。
9. **配置路径别名**以消除深度相对导入。所有 `../../../../../../../../` 路径应替换为别名模块导入。
10. **为所有函数参数、返回类型和状态变量添加适当的 TypeScript 类型**。消除 `any` 的使用。
11. **审查每个应用的权限列表**，确保仅请求必要的权限，且所有权限都包含 `reason` 字符串。
12. **创建共享的编码标准指南**用于内置应用开发，并配备强制执行这些模式的 lint 规则。
