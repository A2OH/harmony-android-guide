# 代码审查报告：网络、电话和媒体 API

**来源**: Android 11 AOSP (`~/aosp-android-11/`)
**范围**: 连接性、Wi-Fi、电话、媒体、蓝牙和 NFC 系统 API
**日期**: 2026-03-10

---

## 目录

1. [网络 API](#1-网络-api)
   - [ConnectivityManager](#11-connectivitymanager)
   - [NetworkInfo（已废弃）](#12-networkinfo已废弃)
   - [Network](#13-network)
   - [Uri](#14-uri)
2. [Wi-Fi API](#2-wi-fi-api)
   - [WifiManager](#21-wifimanager)
3. [电话 API](#3-电话-api)
   - [TelephonyManager](#31-telephonymanager)
   - [SmsManager](#32-smsmanager)
4. [媒体 API](#4-媒体-api)
   - [MediaPlayer](#41-mediaplayer)
   - [MediaRecorder](#42-mediarecorder)
   - [AudioManager](#43-audiomanager)
5. [Camera2 API](#5-camera2-api)
6. [蓝牙 API](#6-蓝牙-api)
   - [BluetoothAdapter](#61-bluetoothadapter)
   - [BluetoothDevice](#62-bluetoothdevice)
7. [NFC API](#7-nfc-api)
   - [NfcAdapter](#71-nfcadapter)
8. [HttpURLConnection 模式](#8-httpurlconnection-模式)
9. [横切面观察](#9-横切面观察)

---

## 1. 网络 API

### 1.1 ConnectivityManager

**文件**: `frameworks/base/core/java/android/net/ConnectivityManager.java`（4770 行）

#### 类的目的和职责

`ConnectivityManager` 是用于查询和管理网络连接的主要系统服务（`Context.CONNECTIVITY_SERVICE`）。通过 `@SystemService` 注解（第 104 行），它：
- 监控网络连接（Wi-Fi、蜂窝、以太网、VPN 等）
- 在连接变化时发送广播 Intent
- 处理网络故障切换
- 提供查询网络状态和请求特定网络的 API
- 管理网络共享操作

#### 面向应用开发者的关键公共 API

**网络状态查询（大部分已废弃）：**
- `getActiveNetworkInfo()`（第 996 行）—— 返回默认数据网络的 `NetworkInfo`。**已废弃**，建议使用 `NetworkCallback`。
  - 权限：`ACCESS_NETWORK_STATE`
- `getActiveNetwork()`（第 1016 行）—— 返回当前默认网络的 `Network` 对象。
- `getNetworkInfo(int networkType)`（第 1201 行）—— 获取特定网络类型的信息。**已废弃**。
- `getNetworkCapabilities(Network)`（第 1383 行）—— 返回给定网络的 `NetworkCapabilities`。
- `getLinkProperties(Network)`（第 1366 行）—— 返回给定网络的 `LinkProperties`。
- `isActiveNetworkMetered()`（第 3059 行）—— 检查活跃网络是否为计量网络。

**现代网络请求 API（推荐方式）：**
- `requestNetwork(NetworkRequest, NetworkCallback)`（第 3814 行）—— 请求匹配指定能力的网络；系统将尝试启动一个。
  - 权限：`CHANGE_NETWORK_STATE` 或 `Settings.System.canWrite`
  - 限制：每个应用 UID 最多 100 个未完成请求
- `requestNetwork(NetworkRequest, NetworkCallback, int timeoutMs)`（第 3868 行）—— 带超时的版本；如果未找到匹配项，将调用 `onUnavailable()`。
- `requestNetwork(NetworkRequest, PendingIntent)`（第 3977 行）—— PendingIntent 变体；在应用进程终止后仍然有效。
- `registerNetworkCallback(NetworkRequest, NetworkCallback)`（第 4047 行）—— 被动监听匹配条件的网络，无需请求启动网络。
  - 权限：`ACCESS_NETWORK_STATE`
- `registerDefaultNetworkCallback(NetworkCallback)`（第 4157 行）—— 监听系统默认网络的变化。
  - 权限：`ACCESS_NETWORK_STATE`
- `unregisterNetworkCallback(NetworkCallback)`（第 4228 行）—— 释放回调并可能释放关联的网络。

**实用方法：**
- `bindProcessToNetwork(Network)` —— 将所有进程流量绑定到特定网络。
- `isNetworkTypeValid(int)`（第 841 行）—— **已废弃**。验证旧版网络类型常量。
- `isNetworkTypeMobile(int)`（第 911 行）—— **已废弃**。检查类型是否为蜂窝网络。

#### NetworkCallback（内部类，第 3287 行）

基于回调的 API 是现代推荐方式。关键回调方法：
```
onAvailable(Network)                                  -- 网络就绪（第 3361 行）
onLost(Network)                                       -- 网络断开（第 3401 行）
onLosing(Network, int maxMsToLive)                    -- 网络即将丢失（第 3380 行）
onUnavailable()                                       -- 未找到匹配的网络（第 3411 行）
onCapabilitiesChanged(Network, NetworkCapabilities)   -- 能力已变化（第 3428 行）
onLinkPropertiesChanged(Network, LinkProperties)      -- 链路属性已变化（第 3444 行）
onBlockedStatusChanged(Network, boolean)              -- 访问被阻止/解除阻止（第 3491 行）
```

**重要指导**（源代码第 3351-3357 行）：不要在回调中调用同步方法（如 `getNetworkCapabilities()` 或 `getLinkProperties()`）—— 它们容易出现竞态条件。应使用回调方法提供的参数。

#### 已废弃的常量（TYPE_* 网络类型，第 527-707 行）

所有 `TYPE_*` 常量已废弃：
- `TYPE_MOBILE = 0`, `TYPE_WIFI = 1`, `TYPE_WIMAX = 6`, `TYPE_BLUETOOTH = 7`, `TYPE_ETHERNET = 9`, `TYPE_VPN = 17`
- 开发者应改用 `NetworkCapabilities.hasTransport()` 配合 `TRANSPORT_*` 常量。

#### 广播 Action

- `CONNECTIVITY_ACTION`（第 146 行）—— **已废弃**，自 API 24 起（不能在清单中声明）。改用 `NetworkCallback`。
- `ACTION_CAPTIVE_PORTAL_SIGN_IN`（第 175 行）—— 检测到强制门户时触发。

#### 隐藏/系统 API

- 网络共享操作：`startTethering()`（第 2450 行）、`stopTethering()`（第 2527 行）—— `@SystemApi`
- `TETHERING_WIFI`、`TETHERING_USB`、`TETHERING_BLUETOOTH` —— `@SystemApi` 常量
- `registerNetworkAgent()`（第 3253 行）—— 需要 `NETWORK_FACTORY` 权限
- `setGlobalProxy()`（第 2964 行）—— `@hide`
- `setAirplaneMode()`（第 3146 行）—— `@hide`
- 私有 DNS 模式常量（第 798-815 行）—— `@hide`
- `setAcceptUnvalidated()`（第 4288 行）—— `@hide`，需要 `NETWORK_SETTINGS`
- `setAcceptPartialConnectivity()`（第 4311 行）—— `@hide`，需要 `NETWORK_STACK`

#### 状态管理

内部回调分发使用 `CallbackHandler`（第 3561 行），这是一个基于 `Handler` 的消息传递系统。回调消息包括 `CALLBACK_AVAILABLE`、`CALLBACK_LOST`、`CALLBACK_CAP_CHANGED` 等（第 3520-3540 行）。

静态 `HashMap<NetworkRequest, NetworkCallback>`（`sCallbacks`，第 3661 行）跟踪所有已注册的回调。清理在 `unregisterNetworkCallback()` 中执行，通过遍历此映射、移除条目并调用 `mService.releaseNetworkRequest()` 来完成。

---

### 1.2 NetworkInfo（已废弃）

**文件**: `frameworks/base/core/java/android/net/NetworkInfo.java`（599 行）

#### 类的目的

`NetworkInfo` 描述网络接口的状态。**整个类**已废弃（第 47 行）—— 应用应改用 `NetworkCallback`、`NetworkCapabilities` 和 `LinkProperties`。

#### 状态模型

两级状态模型（第 75-114 行）：

**粗粒度（`State` 枚举）**: `CONNECTING`, `CONNECTED`, `SUSPENDED`, `DISCONNECTING`, `DISCONNECTED`, `UNKNOWN`

**细粒度（`DetailedState` 枚举）**: `IDLE`, `SCANNING`, `CONNECTING`, `AUTHENTICATING`, `OBTAINING_IPADDR`, `CONNECTED`, `SUSPENDED`, `DISCONNECTING`, `DISCONNECTED`, `FAILED`, `BLOCKED`, `VERIFYING_POOR_LINK`, `CAPTIVE_PORTAL_CHECK`

两者之间的映射定义在静态 `EnumMap` 中（第 121-138 行）。

#### 关键方法（全部已废弃）

- `isConnected()`（第 327 行）—— 网络是否处于 `CONNECTED` 状态。
- `isConnectedOrConnecting()`（第 307 行）
- `isAvailable()`（第 355 行）—— 自 Android L 起，始终返回 `true`。
- `isRoaming()`（第 419 行）—— 改用 `NET_CAPABILITY_NOT_ROAMING`。
- `getType()`（第 217 行）—— 改用 `NetworkCapabilities.hasTransport()`。
- `getState()` / `getDetailedState()`（第 450、468 行）

#### 隐藏 API

- `setType()`、`setSubtype()`、`setIsAvailable()`、`setFailover()`、`setRoaming()` —— 全部为 `@hide` / `@UnsupportedAppUsage`

---

### 1.3 Network

**文件**: `frameworks/base/core/java/android/net/Network.java`（522 行）

#### 类的目的

`Network` 标识特定网络并提供将流量引导至该网络的方法。实现 `Parcelable` 以支持 IPC 传输。通过 `NetworkCallback.onAvailable()` 获取。

#### 关键公共 API

- `getSocketFactory()`（第 287 行）—— 返回一个 `SocketFactory`，其创建的套接字绑定到此网络。
- `openConnection(URL)`（第 339 行）—— 在此特定网络上打开 `HttpURLConnection`，支持 HTTP/HTTPS。
- `openConnection(URL, Proxy)`（第 366 行）—— 带显式代理的版本。
- `bindSocket(Socket)` / `bindSocket(DatagramSocket)` / `bindSocket(FileDescriptor)`（第 391、379、404 行）—— 将现有套接字绑定到此网络。
- `getAllByName(String)`（第 142 行）—— 在此网络上进行 DNS 解析。
- `getByName(String)`（第 155 行）—— 在此网络上进行 DNS 解析。
- `getNetworkHandle()`（第 453 行）—— 返回用于 NDK 的不透明 long 句柄。使用 `HANDLE_MAGIC = 0xcafed00dL`（第 91 行）进行混淆。
- `fromNetworkHandle(long)`（第 437 行）—— 从 NDK 句柄创建的静态工厂方法。

#### 隐藏/系统 API

- `netId` 字段（第 68 行）—— `@UnsupportedAppUsage`，内部网络标识符
- `getNetId()`（第 179 行）—— `@SystemApi @TestApi`
- `getPrivateDnsBypassingCopy()`（第 168 行）—— `@SystemApi @TestApi`，返回绕过私有 DNS 的副本
- `mPrivateDnsBypass`（第 107 行）—— 瞬态标志，不参与序列化

#### 设计说明

`Network` 类每个实例包含一个 `HttpURLConnectionFactory`（第 75 行），带有专用连接池。源代码包含一个 TODO 注释（第 298-312 行），指出这不是最优的——理想情况下应该每个 `netId` 一个连接池，而不是每个 `Network` 对象一个。

---

### 1.4 Uri

**文件**: `frameworks/base/core/java/android/net/Uri.java`（2431 行）

#### 类的目的

符合 RFC 2396 的不可变 URI 引用。在 Android 中广泛用于内容 URI、Intent 数据、文件 URI 和 Web URL。

#### 架构

`Uri` 是一个抽象类（第 56 行），具有私有实现：
- `StringUri` —— 从 URI 字符串延迟解析
- `OpaqueUri` —— 从方案 + 方案特定部分 + 片段构建
- `HierarchicalUri` —— 带有授权、路径、查询、片段的完整层次化 URI

该类使用带有 `NOT_CACHED` 哨兵字符串（第 128 行）的缓存模式，实现无需同步的线程安全延迟初始化。

#### 关键静态工厂方法

- `Uri.parse(String)`（第 457 行）—— 解析 URI 字符串。非常宽容——对无效输入返回垃圾数据而不是抛出异常。
- `Uri.fromFile(File)`（第 471 行）—— 创建 `file:///` URI。
- `Uri.fromParts(String scheme, String ssp, String fragment)`（第 845 行）—— 创建不透明 URI。
- `Uri.withAppendedPath(Uri, String)`（第 2340 行）—— 追加路径段。
- `Uri.EMPTY`（第 133 行）—— 空 URI。

#### 关键实例方法

- `getScheme()`、`getAuthority()`、`getHost()`、`getPort()`、`getPath()`、`getQuery()`、`getFragment()` —— 标准 URI 组件访问器
- `getSchemeSpecificPart()`（第 192 行）—— 解码版本
- `getEncodedSchemeSpecificPart()` —— 编码版本
- `getQueryParameter(String key)` —— 获取单个查询参数值
- `getQueryParameters(String key)` —— 获取查询参数的所有值
- `getPathSegments()` —— 已解码路径段列表
- `buildUpon()` —— 返回用于修改的 `Builder`
- `isHierarchical()` / `isOpaque()`（第 147、153 行）
- `isRelative()` / `isAbsolute()`（第 163、171 行）

#### 构建器模式

`Uri.Builder` 提供流畅的 API：
```
new Uri.Builder()
    .scheme("https")
    .authority("example.com")
    .appendPath("api")
    .appendQueryParameter("key", "value")
    .build();
```

---

## 2. Wi-Fi API

### 2.1 WifiManager

**文件**: `frameworks/base/wifi/java/android/net/wifi/WifiManager.java`（6239 行）

#### 类的目的

`WifiManager`（`Context.WIFI_SERVICE`，第 108 行）是管理 Wi-Fi 连接的主要 API。它处理：
- 已配置网络管理
- Wi-Fi 状态控制（启用/禁用）
- 扫描
- 连接信息
- 网络建议（Android 10+）
- 热点/网络共享（系统级）

#### 关键公共 API

**Wi-Fi 状态：**
- `isWifiEnabled()`（第 2949 行）—— 检查 Wi-Fi 是否开启
- `setWifiEnabled(boolean)`（第 2921 行）—— 启用/禁用 Wi-Fi
- `getWifiState()`（第 2936 行）—— 返回 `WIFI_STATE_DISABLED`、`_DISABLING`、`_ENABLED`、`_ENABLING`、`_UNKNOWN`

**扫描：**
- `startScan()`（第 2716 行）—— 发起 Wi-Fi 扫描（Android 8+ 中受限制）
- `getScanResults()` —— 获取 `ScanResult` 接入点列表

**连接信息：**
- `getConnectionInfo()`（第 2756 行）—— 返回包含 SSID、BSSID、RSSI、链接速度等的 `WifiInfo`
- `getDhcpInfo()`（第 2889 行）—— 返回包含 IP、网关、DNS 等的 `DhcpInfo`

**网络配置（API 29+ 中已废弃）：**
- `addNetwork(WifiConfiguration)`（第 1577 行）—— 添加新的已保存网络。返回网络 ID。
- `updateNetwork(WifiConfiguration)`（第 1618 行）—— 更新已保存的网络。
- `removeNetwork(int)`（第 2169 行）—— 移除已保存的网络。
- `enableNetwork(int, boolean)`（第 2218 行）—— 启用并可选连接。
- `disableNetwork(int)`（第 2254 行）—— 禁用已保存的网络。

**网络建议（Android 10+ 替代方案）：**
- `addNetworkSuggestions(List<WifiNetworkSuggestion>)` —— 建议自动连接的网络
- `removeNetworkSuggestions(List<WifiNetworkSuggestion>)` —— 移除建议
- `getMaxNumberOfNetworkSuggestionsPerApp()`（第 1995 行）—— 低内存设备：256，高内存设备：1024（第 157-160 行）

**信号工具：**
- `calculateSignalLevel(int rssi, int numLevels)`（第 2965 行）—— 静态方法，将 RSSI 映射到等级区间（已废弃形式）
- `calculateSignalLevel(int rssi)`（第 2986 行）—— 实例方法，使用系统定义的最大等级
- `compareSignalLevel(int rssiA, int rssiB)`（第 3012 行）—— 比较两个 RSSI 值

**连接控制：**
- `disconnect()`（第 2283 行）—— 断开当前网络
- `reconnect()`（第 2313 行）—— 重新连接到当前活跃网络
- `reassociate()`（第 2337 行）—— 重新关联到当前接入点

#### 所需权限

- `ACCESS_WIFI_STATE` —— 查询 Wi-Fi 状态、获取扫描结果
- `CHANGE_WIFI_STATE` —— 启用/禁用 Wi-Fi、连接/断开
- `ACCESS_FINE_LOCATION` —— 自 Android 8 起 `getScanResults()` 所需

#### 网络建议状态码（第 165-222 行）

- `STATUS_NETWORK_SUGGESTIONS_SUCCESS = 0`
- `STATUS_NETWORK_SUGGESTIONS_ERROR_INTERNAL = 1`
- `STATUS_NETWORK_SUGGESTIONS_ERROR_APP_DISALLOWED = 2`
- `STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE = 3`（Android 11 中不返回）
- `STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_EXCEEDS_MAX_PER_APP = 4`
- `STATUS_NETWORK_SUGGESTIONS_ERROR_REMOVE_INVALID = 5`
- `STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_NOT_ALLOWED = 6`
- `STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_INVALID = 7`

#### 功能检测方法

- `isP2pSupported()`（第 2460 行）
- `isWifiAwareSupported()`（第 2486 行）
- `is5GHzBandSupported()`（第 2583 行）
- `is6GHzBandSupported()`（第 2595 行）
- `isStaApConcurrencySupported()`（第 2495 行）
- `isWifiStandardSupported(int)`（第 2609 行）
- `isPasspointSupported()`（第 2453 行）
- `isDeviceToDeviceRttSupported()`（第 2509 行）

#### 隐藏/系统 API

- `WIFI_CREDENTIAL_CHANGED_ACTION`（第 288 行）—— `@SystemApi`
- `registerNetworkRequestMatchCallback()`（第 1863 行）—— `@SystemApi`
- `updateInterfaceIpState()`（第 3033 行）—— `@SystemApi`

---

## 3. 电话 API

### 3.1 TelephonyManager

**文件**: `frameworks/base/telephony/java/android/telephony/TelephonyManager.java`（13561 行）

#### 类的目的

`TelephonyManager`（`Context.TELEPHONY_SERVICE`，第 156 行）提供电话服务和设备信息的访问。它处理：
- 设备和 SIM 卡识别
- 网络运营商信息
- 通话状态监控
- 数据网络状态
- SIM 卡状态
- 运营商配置
- 多 SIM 卡支持

#### 面向应用开发者的关键公共 API

**设备信息：**
- `getDeviceId()`（第 1934 行）—— IMEI/MEID。**需要 `READ_PRIVILEGED_PHONE_STATE`**（Android 10+ 中普通应用已无法访问）。
- `getImei()` / `getImei(int slotIndex)`（第 2013、2054 行）—— 专门获取 IMEI。**需要 `READ_PRIVILEGED_PHONE_STATE`**。
- `getMeid()` / `getMeid(int slotIndex)`（第 2130、2170 行）—— 专门获取 MEID。**需要 `READ_PRIVILEGED_PHONE_STATE`**。
- `getDeviceSoftwareVersion()`（第 1863 行）—— 需要 `READ_PHONE_STATE`。
- `getTypeAllocationCode()`（第 2072 行）—— 设备的 TAC（无需特殊权限）。
- `getManufacturerCode()`（第 2194 行）—— 制造商代码。

**网络信息：**
- `getNetworkOperatorName()`（第 2716 行）—— 人类可读的运营商名称。
- `getNetworkOperator()`（第 2743 行）—— MCC+MNC 字符串。
- `getNetworkCountryIso()`（第 2854 行）—— ISO 国家代码。
- `isNetworkRoaming()`（第 2825 行）—— 检查漫游状态。
- `getNetworkType()`（第 ~3025 行）—— 返回 `NETWORK_TYPE_LTE`、`_NR` 等。需要 `READ_PHONE_STATE`。
- `getDataNetworkType()`（第 ~3090 行）—— 数据连接类型。需要 `READ_PHONE_STATE`。
- `getVoiceNetworkType()`（第 ~3129 行）—— 语音网络类型。需要 `READ_PHONE_STATE`。
- `getNetworkSpecifier()`（第 2791 行）

**SIM 卡信息：**
- `hasIccCard()`（第 3452 行）—— 检查 SIM 卡是否存在。
- `getSimOperator()`（第 3713 行）—— SIM 运营商 MCC+MNC。
- `getSimOperatorName()` —— SIM 运营商名称。
- `getSimCountryIso()` —— SIM 国家代码。
- `getSimState()` —— SIM 状态（缺失、需要 PIN、就绪等）

**电话状态：**
- `getPhoneType()`（第 2527 行）—— `PHONE_TYPE_GSM`、`_CDMA`、`_SIP`、`_NONE`
- `getCurrentPhoneType()`（第 2465 行）
- `getPhoneCount()`（第 446 行）—— 电话卡槽数量。
- `getActiveModemCount()`（第 458 行）—— 活跃调制解调器数量。

**位置：**
- `getCellLocation()`（第 2329 行）—— 需要 `ACCESS_FINE_LOCATION`。
- `getAllCellInfo()` —— 需要 `ACCESS_FINE_LOCATION`。

**多 SIM 卡：**
- `createForSubscriptionId(int)` —— 为特定订阅创建 `TelephonyManager`。
- `getActiveModemCount()`（第 458 行）
- `getSupportedModemCount()`（第 486 行）
- `isMultiSimEnabled()`（第 543 行）

#### 所需权限

| 权限 | 方法 |
|---|---|
| `READ_PHONE_STATE` | `getDeviceSoftwareVersion()`、`getNetworkType()`、`getDataNetworkType()`、`getVoiceNetworkType()` |
| `READ_PRIVILEGED_PHONE_STATE` | `getDeviceId()`、`getImei()`、`getMeid()`、`getNai()`、`isApplicationOnUicc()` |
| `ACCESS_FINE_LOCATION` | `getCellLocation()`、`getAllCellInfo()` |
| `ACCESS_COARSE_LOCATION` | 部分蜂窝信息方法 |
| `CONTROL_LOCATION_UPDATES` | `enableLocationUpdates()`、`disableLocationUpdates()` |

#### PhoneStateListener 注册

`listen()` 方法接受一个 `PhoneStateListener` 和事件标志：
- `LISTEN_CALL_STATE` —— 通话状态变化
- `LISTEN_DATA_CONNECTION_STATE` —— 数据连接状态变化
- `LISTEN_SERVICE_STATE` —— 服务状态变化
- `LISTEN_SIGNAL_STRENGTHS` —— 信号强度变化
- `LISTEN_CELL_LOCATION` —— 蜂窝位置变化

#### 隐藏/系统 API

该文件非常庞大（13,561 行），包含大量隐藏 API，涵盖：
- 呼叫转移管理（例如 `getCallForwarding()`）
- 运营商特权检查
- IMS 功能管理（第 87-92 行）
- 调制解调器活动信息
- 网络选择模式
- SIM 卡配置
- 紧急号码处理
- 号码验证回调

---

### 3.2 SmsManager

**文件**: `frameworks/base/telephony/java/android/telephony/SmsManager.java`（3007 行）

#### 类的目的

`SmsManager` 管理短信和彩信操作。使用每个订阅实例的单例模式。

#### 关键公共 API

**发送文本消息：**
- `sendTextMessage(String dest, String scAddress, String text, PendingIntent sentIntent, PendingIntent deliveryIntent)`（第 423 行）—— 主要文本短信 API。`sentIntent` 接收结果码，包括 `RESULT_OK`、`RESULT_ERROR_GENERIC_FAILURE`、`RESULT_ERROR_RADIO_OFF`、`RESULT_ERROR_NO_SERVICE` 等。
- `sendTextMessage(..., long messageId)`（第 441 行）—— 带诊断消息 ID 的变体。
- `sendMultipartTextMessage(String dest, String scAddress, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents)`（第 936 行）—— 用于超过单条短信大小的消息。

**发送数据消息：**
- `sendDataMessage(String dest, String scAddress, short destPort, byte[] data, PendingIntent sentIntent, PendingIntent deliveryIntent)`（第 1389 行）—— 向特定端口发送原始数据短信。

**彩信：**
- `sendMultimediaMessage(Context, Uri contentUri, String locationUrl, Bundle configOverrides, PendingIntent sentIntent)`（第 2525 行）—— 发送彩信。
- `downloadMultimediaMessage(Context, String locationUrl, Uri contentUri, Bundle configOverrides, PendingIntent downloadedIntent)`（第 2559 行）—— 下载彩信。

**工厂方法：**
- `SmsManager.getDefault()`（第 1451 行）—— 获取默认订阅的实例。
- `SmsManager.getSmsManagerForSubscriptionId(int)`（第 1468 行）—— 获取特定订阅的实例。

**SIM 卡操作（隐藏）：**
- `copyMessageToIcc()`（第 1678 行）—— `@SystemApi`
- `deleteMessageFromIcc()`（第 1723 行）—— `@SystemApi`
- `updateMessageOnIcc()`（第 1766 行）—— `@SystemApi`

#### 彩信配置常量（第 109-200 行）

丰富的运营商特定彩信配置常量：
- `MMS_CONFIG_MAX_MESSAGE_SIZE`、`MMS_CONFIG_MAX_IMAGE_WIDTH`、`MMS_CONFIG_MAX_IMAGE_HEIGHT`
- `MMS_CONFIG_MMS_ENABLED`、`MMS_CONFIG_GROUP_MMS_ENABLED`
- `MMS_CONFIG_RECIPIENT_LIMIT`
- `MMS_CONFIG_SMS_DELIVERY_REPORT_ENABLED`

#### 所需权限

- `SEND_SMS` —— `sendTextMessage()`、`sendMultipartTextMessage()`、`sendDataMessage()` 所需
- `READ_PHONE_STATE` —— 多 SIM 卡订阅解析所需

#### 隐藏/系统 API

- `sendTextMessageWithoutPersisting()`（第 652 行）—— `@SystemApi`，发送但不存储到短信提供者。
- `injectSmsPdu()`（第 796 行）—— `@SystemApi`，注入原始短信 PDU。
- `enableCellBroadcastRange()`（第 1869 行）—— `@SystemApi`
- `disableCellBroadcastRange()`（第 1926 行）—— `@SystemApi`
- `setSmscAddress()`（第 2921 行）—— `@SystemApi`
- `getSmsMessagesForFinancialApp()`（第 2684 行）—— `@SystemApi`

#### 错误处理

`sendTextMessage()` 方法定义了一组完整的结果码（第 400-522 行）：
- `RESULT_ERROR_GENERIC_FAILURE`、`RESULT_ERROR_RADIO_OFF`、`RESULT_ERROR_NULL_PDU`
- `RESULT_ERROR_NO_SERVICE`、`RESULT_ERROR_LIMIT_EXCEEDED`
- `RESULT_ERROR_FDN_CHECK_FAILURE`
- RIL 级别错误：`RESULT_RIL_RADIO_NOT_AVAILABLE`、`RESULT_RIL_NETWORK_REJECT` 等。

---

## 4. 媒体 API

### 4.1 MediaPlayer

**文件**: `frameworks/base/media/java/android/media/MediaPlayer.java`（5975 行）

#### 类的目的

`MediaPlayer` 控制音频/视频文件和流的播放。它作为状态机运行，具有清晰定义的状态和转换。

#### 状态机（第 87-250 行文档）

状态：**Idle** -> **Initialized** -> **Preparing/Prepared** -> **Started** -> **Paused** -> **Stopped** -> **End**

- `new MediaPlayer()` 或 `reset()` -> **Idle**
- `setDataSource()` -> **Initialized**
- `prepare()`（同步）或 `prepareAsync()` -> **Prepared**
- `start()` -> **Started**
- `pause()` -> **Paused**
- `stop()` -> **Stopped**
- `release()` -> **End**

**重要提示**：通过 `create()` 工厂方法创建的对象处于 **Prepared** 状态（第 164-167 行），而非 Idle。

#### 关键公共 API

**工厂方法：**
- `MediaPlayer.create(Context, Uri)`（第 867 行）—— 从 URI 创建并准备。
- `MediaPlayer.create(Context, int resid)`（第 950 行）—— 从原始资源创建并准备。
- `MediaPlayer.create(Context, Uri, SurfaceHolder, AudioAttributes, int audioSessionId)`（第 903 行）—— 全功能工厂方法。

**数据源：**
- `setDataSource(Context, Uri)`（第 1003 行）—— 从内容 URI 设置。
- `setDataSource(Context, Uri, Map<String,String> headers)`（第 1034 行）—— 带 HTTP 头。
- `setDataSource(String path)`（第 1126 行）—— 从文件路径或 URL 设置。
- `setDataSource(FileDescriptor fd)`（第 1226 行）—— 从文件描述符设置。
- `setDataSource(MediaDataSource)`（第 1259 行）—— 自定义数据源。

**播放控制：**
- `prepare()`（第 1276 行）—— 同步准备。抛出 `IOException`。
- `prepareAsync()` —— 异步准备。触发 `OnPreparedListener`。
- `start()`（第 1308 行）—— 开始播放。
- `pause()`（第 1372 行）—— 暂停播放。
- `stop()`（第 1358 行）—— 停止播放。
- `seekTo(long msec, @SeekMode mode)`（第 1886 行）—— 带模式的定位：`SEEK_PREVIOUS_SYNC`、`SEEK_NEXT_SYNC`、`SEEK_CLOSEST_SYNC`、`SEEK_CLOSEST`。
- `seekTo(int msec)`（第 1910 行）—— 旧版定位（等同于 `SEEK_PREVIOUS_SYNC`）。
- `reset()`（第 2125 行）—— 重置到 Idle 状态。
- `release()`（第 2083 行）—— 释放资源。完成后立即调用。

**显示：**
- `setDisplay(SurfaceHolder)`（第 753 行）—— 设置视频输出表面。
- `setSurface(Surface)`（第 785 行）—— 设置视频输出表面（更灵活）。
- `setVideoScalingMode(int)`（第 834 行）—— `VIDEO_SCALING_MODE_SCALE_TO_FIT` 或 `_SCALE_TO_FIT_WITH_CROPPING`。

**音频：**
- `setAudioAttributes(AudioAttributes)`（第 2216 行）
- `setAudioStreamType(int)`（第 2187 行）—— **已废弃**，改用 `setAudioAttributes()`。
- `setVolume(float left, float right)`（第 2259 行）
- `setAuxEffectSendLevel(float)`（第 2333 行）

**轨道管理：**
- `getTrackInfo()` —— 获取可用轨道（音频、视频、字幕）信息。
- `selectTrack(int index)`（第 3132 行）—— 选择轨道。
- `deselectTrack(int index)`（第 3150 行）
- `addTimedTextSource(String path, String mimeType)`（第 2860 行）
- `addSubtitleSource(InputStream, MediaFormat)`（第 2739 行）

**路由：**
- `setPreferredDevice(AudioDeviceInfo)`（第 1433 行）—— 首选输出设备。
- `addOnRoutingChangedListener()`（第 1508 行）—— 监听路由变化。

#### 回调

所有回调使用监听器模式：
- `OnPreparedListener.onPrepared(MediaPlayer)` —— 异步准备完成
- `OnCompletionListener.onCompletion(MediaPlayer)` —— 播放到达结尾
- `OnErrorListener.onError(MediaPlayer, int what, int extra)` —— 发生错误
- `OnInfoListener.onInfo(MediaPlayer, int what, int extra)` —— 信息事件
- `OnBufferingUpdateListener.onBufferingUpdate(MediaPlayer, int percent)` —— 缓冲进度
- `OnSeekCompleteListener.onSeekComplete(MediaPlayer)` —— 定位操作完成
- `OnVideoSizeChangedListener.onVideoSizeChanged(MediaPlayer, int width, int height)` —— 视频尺寸已知

#### 线程安全说明（第 93 行）

`MediaPlayer` **不是**线程安全的。所有访问应来自同一线程。如果注册回调，该线程必须有一个 `Looper`。

---

### 4.2 MediaRecorder

**文件**: `frameworks/base/media/java/android/media/MediaRecorder.java`（1875 行）

#### 类的目的

`MediaRecorder` 录制音频和视频。它的运行方式类似于 `MediaPlayer` 的状态机。实现了 `AudioRouting`、`AudioRecordingMonitor`、`MicrophoneDirection`（第 95-98 行）。

#### 典型使用模式（第 62-73 行）

```java
MediaRecorder recorder = new MediaRecorder();
recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
recorder.setOutputFile(PATH_NAME);
recorder.prepare();
recorder.start();
// ... 录制中 ...
recorder.stop();
recorder.reset();    // 可重用
recorder.release();  // 完成
```

#### AudioSource 常量（内部类，第 224 行）

- `DEFAULT = 0`（第 236 行）
- `MIC = 1`（第 239 行）—— 麦克风
- `VOICE_UPLINK = 2`（第 249 行）—— 通话上行链路，需要 `CAPTURE_AUDIO_OUTPUT`（仅系统级）
- `VOICE_DOWNLINK = 3`（第 259 行）—— 通话下行链路，需要 `CAPTURE_AUDIO_OUTPUT`（仅系统级）
- `VOICE_CALL = 4`（第 269 行）—— 上行+下行，需要 `CAPTURE_AUDIO_OUTPUT`（仅系统级）
- `CAMCORDER = 5`（第 273 行）—— 针对视频录制优化
- `VOICE_RECOGNITION = 6`（第 276 行）—— 针对语音识别优化
- `VOICE_COMMUNICATION = 7`（第 282 行）—— VoIP，带回声消除

#### 关键方法

- `setCamera(Camera)`（第 162 行）—— **已废弃**，改用 Camera2 + `getSurface()`。
- `getSurface()`（第 174 行）—— 获取录制表面（用于 Camera2 集成）。
- `setInputSurface(Surface)`（第 191 行）—— 使用来自 `MediaCodec` 的持久表面。
- `setPreviewDisplay(Surface)`（第 213 行）
- `setAudioSource(int)`、`setVideoSource(int)` —— 必须在 `setOutputFormat()` 之前调用。
- `setOutputFormat(int)` —— `THREE_GPP`、`MPEG_4`、`AMR_NB`、`AMR_WB`、`WEBM` 等。
- `setAudioEncoder(int)`、`setVideoEncoder(int)` —— 在输出格式之后设置编解码器。
- `setOutputFile(String)` / `setOutputFile(FileDescriptor)` / `setOutputFile(File)`
- `prepare()`、`start()`、`stop()`、`reset()`、`release()`

#### 所需权限

- `RECORD_AUDIO` —— 任何音频录制所需
- `CAMERA` —— 使用摄像头录制视频时所需
- `CAPTURE_AUDIO_OUTPUT` —— 用于 `VOICE_UPLINK`、`VOICE_DOWNLINK`、`VOICE_CALL` 源（仅系统级）

#### 回调

- `OnErrorListener.onError(MediaRecorder, int what, int extra)` —— 通过 `setOnErrorListener()` 设置（第 79-80 行）
- `OnInfoListener.onInfo(MediaRecorder, int what, int extra)` —— 通过 `setOnInfoListener()` 设置

---

### 4.3 AudioManager

**文件**: `frameworks/base/media/java/android/media/AudioManager.java`（6259 行）

#### 类的目的

`AudioManager`（`Context.AUDIO_SERVICE`，第 92 行）控制音量、铃声模式、音频路由和音频焦点。

#### 关键公共 API

**音量控制：**
- `adjustVolume(int direction, int flags)`（第 883 行）—— 按一步调整：`ADJUST_RAISE`、`ADJUST_LOWER`、`ADJUST_SAME`。
- `setStreamVolume(int streamType, int index, int flags)`（第 1173 行）—— 设置绝对音量。
- `getStreamVolume(int streamType)`（第 1023 行）—— 获取当前音量。
- `getStreamMaxVolume(int streamType)` —— 获取流的最大音量。
- `isStreamMute(int streamType)`（第 1373 行）—— 检查是否静音。

流类型：`STREAM_VOICE_CALL`、`STREAM_SYSTEM`、`STREAM_RING`、`STREAM_MUSIC`、`STREAM_ALARM`、`STREAM_NOTIFICATION`、`STREAM_DTMF`

**铃声模式：**
- `getRingerMode()`（第 937 行）—— 返回 `RINGER_MODE_NORMAL`、`_VIBRATE`、`_SILENT`。
- `setRingerMode(int)`（第 1144 行）—— 设置铃声模式。

**音频焦点（媒体应用的关键功能）：**
- `requestAudioFocus(OnAudioFocusChangeListener, int streamType, int durationHint)`（第 3058 行）—— **已废弃**。请求音频焦点。
- `requestAudioFocus(AudioFocusRequest)`（第 3131 行）—— 使用 `AudioFocusRequest` 构建器的现代 API。
- `abandonAudioFocus(OnAudioFocusChangeListener)`（第 3534 行）—— 释放音频焦点。

持续时间提示：`AUDIOFOCUS_GAIN`、`AUDIOFOCUS_GAIN_TRANSIENT`、`AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK`、`AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE`

焦点变化回调：`AUDIOFOCUS_GAIN`、`AUDIOFOCUS_LOSS`、`AUDIOFOCUS_LOSS_TRANSIENT`、`AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK`

**音频模式：**
- `setMode(int mode)`（第 2234 行）—— `MODE_NORMAL`、`MODE_RINGTONE`、`MODE_IN_CALL`、`MODE_IN_COMMUNICATION`
- `getMode()`（第 2249 行）

**麦克风：**
- `setMicrophoneMute(boolean)`（第 2146 行）
- `isMicrophoneMute()`（第 2181 行）

**蓝牙 SCO：**
- `isBluetoothScoAvailableOffCall()`（第 1939 行）
- `startBluetoothSco()`（第 1989 行）
- `stopBluetoothSco()`（第 2034 行）

#### 广播 Action

- `ACTION_AUDIO_BECOMING_NOISY`（第 117 行）—— 音频输出即将切换到扬声器（例如耳机拔出）。应用应暂停播放。
- `RINGER_MODE_CHANGED_ACTION`（第 126 行）—— 铃声模式已更改。
- `VOLUME_CHANGED_ACTION`（第 175 行）—— `@hide`，音量已更改。

#### 隐藏/系统 API

- `INTERNAL_RINGER_MODE_CHANGED_ACTION`（第 136 行）—— `@hide`
- `VOLUME_CHANGED_ACTION`（第 175 行）—— `@hide @UnsupportedAppUsage`
- `STREAM_DEVICES_CHANGED_ACTION`（第 190 行）—— `@hide`
- 通过 `AudioPolicy` 类的音频策略 API
- `requestAudioFocus(..., AudioPolicy)`（第 3286 行）—— 带音频策略的系统级焦点

---

## 5. Camera2 API

### 概述

**文件**：
- `frameworks/base/core/java/android/hardware/camera2/CameraManager.java`
- `frameworks/base/core/java/android/hardware/camera2/CameraDevice.java`
- `frameworks/base/core/java/android/hardware/camera2/CameraCaptureSession.java`
- `frameworks/base/core/java/android/hardware/camera2/CameraCharacteristics.java`
- `frameworks/base/core/java/android/hardware/camera2/CaptureRequest.java`
- `frameworks/base/core/java/android/hardware/camera2/CaptureResult.java`

#### CameraManager（`Context.CAMERA_SERVICE`）

**关键方法：**
- `getCameraIdList()`（第 114 行）—— 返回可用相机 ID 数组。
- `getCameraCharacteristics(String cameraId)`（第 401 行）—— 获取相机能力和属性。
- `openCamera(String cameraId, StateCallback callback, Handler handler)`（第 649 行）—— 打开相机设备。**需要 `CAMERA` 权限**。
- `openCamera(String cameraId, Executor executor, StateCallback callback)`（第 686 行）—— 基于 Executor 的变体。
- `registerAvailabilityCallback(AvailabilityCallback, Handler)`（第 248 行）—— 监控相机可用性。

#### CameraDevice

一个抽象类（第 67 行），表示已打开的相机。关键元素：

**请求模板：**
- `TEMPLATE_PREVIEW = 1`（第 78 行）—— 高帧率预览
- `TEMPLATE_STILL_CAPTURE = 2`（第 90 行）—— 静态图像质量优先
- `TEMPLATE_RECORD = 3`（第 97 行）—— 录制时稳定帧率
- `TEMPLATE_VIDEO_SNAPSHOT` —— 视频拍摄中捕获静态图像
- `TEMPLATE_ZERO_SHUTTER_LAG` —— 最小快门延迟
- `TEMPLATE_MANUAL` —— 手动控制

**关键方法：**
- `createCaptureSession(List<Surface>, StateCallback, Handler)`（第 243 行）—— **已废弃**，改用 `SessionConfiguration`。
- `createCaptureRequest(int templateType)`（第 983 行）—— 为给定模板创建 `CaptureRequest.Builder`。
- `close()`（第 1080 行）—— 释放相机设备。

**硬件级别**（来自 `CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL`）：
- `LEGACY` —— 最低 Camera2 支持，向后兼容模式
- `LIMITED` —— 大致等同于旧 Camera API
- `EXTERNAL` —— 可移除相机（略低于 LIMITED）
- `FULL` —— 比旧 API 有实质性改进
- `LEVEL3` —— 最大能力

#### 使用流程

1. `CameraManager.getCameraIdList()` —— 枚举相机
2. `CameraManager.getCameraCharacteristics()` —— 查询能力
3. `CameraManager.openCamera()` —— 打开，在 `StateCallback.onOpened()` 中接收 `CameraDevice`
4. `CameraDevice.createCaptureSession()` —— 使用输出表面创建会话
5. `CameraDevice.createCaptureRequest()` —— 构建捕获请求
6. `CameraCaptureSession.setRepeatingRequest()` 或 `.capture()` —— 执行请求
7. `CameraDevice.close()` —— 释放

#### 所需权限

- `android.permission.CAMERA`

---

## 6. 蓝牙 API

### 6.1 BluetoothAdapter

**文件**: `frameworks/base/core/java/android/bluetooth/BluetoothAdapter.java`（3626 行）

#### 类的目的

`BluetoothAdapter` 表示本地蓝牙硬件。它是所有蓝牙操作的起点：发现、配对、连接。线程安全（第 93 行）。

#### 获取方式

- 现代方式：`BluetoothManager.getAdapter()`（通过 `Context.getSystemService(BluetoothManager.class)`）
- 旧版方式：`BluetoothAdapter.getDefaultAdapter()`

#### 关键公共 API

**状态管理：**
- `isEnabled()`（第 866 行）—— 检查蓝牙是否开启。
- `enable()`（第 1118 行）—— 开启蓝牙。需要 `BLUETOOTH_ADMIN`。
- `disable()`（第 1155 行）—— 关闭。需要 `BLUETOOTH_ADMIN`。
- `getState()`（第 1036 行）—— 返回 `STATE_OFF` (10)、`STATE_TURNING_ON` (11)、`STATE_ON` (12)、`STATE_TURNING_OFF` (13)。

**设备发现：**
- `startDiscovery()`（第 1701 行）—— 开始扫描附近设备。需要 `BLUETOOTH_ADMIN` + 位置权限。
- `cancelDiscovery()`（第 1735 行）—— 停止发现。
- `isDiscovering()`（第 1771 行）—— 检查发现是否进行中。

**已配对设备：**
- `getBondedDevices()` —— 返回已配对设备的 `Set<BluetoothDevice>`。

**设备创建：**
- `getRemoteDevice(String address)`（第 771 行）—— 通过 MAC 地址获取 `BluetoothDevice`。
- `getRemoteDevice(byte[] address)`（第 786 行）—— 通过字节数组 MAC 获取。

**BLE：**
- `getBluetoothLeScanner()`（第 845 行）—— 获取用于 BLE 扫描的 `BluetoothLeScanner`。
- `isLeEnabled()`（第 879 行）—— 检查 BLE 状态。
- `isMultipleAdvertisementSupported()`（第 1931 行）—— BLE 广播支持。
- `isLe2MPhySupported()`（第 2039 行）—— 蓝牙 5 2M PHY。
- `isLeCodedPhySupported()`（第 2061 行）—— 蓝牙 5 Coded PHY。
- `isLeExtendedAdvertisingSupported()`（第 2083 行）
- `isLePeriodicAdvertisingSupported()`（第 2105 行）
- `getLeMaximumAdvertisingDataLength()`（第 2128 行）

**服务器套接字：**
- `listenUsingRfcommWithServiceRecord(String name, UUID uuid)` —— 创建安全 RFCOMM 服务器套接字。
- `listenUsingInsecureRfcommWithServiceRecord(String name, UUID uuid)` —— 非安全 RFCOMM。
- `listenUsingL2capChannel()` —— L2CAP CoC 服务器套接字（BLE）。

**适配器信息：**
- `getName()`（第 1207 行）—— 本地蓝牙名称。
- `setName(String)`（第 1281 行）—— 设置蓝牙名称。
- `getAddress()`（第 1191 行）—— 本地 MAC 地址（没有 `LOCAL_MAC_ADDRESS` 权限时返回 `02:00:00:00:00:00`）。
- `getScanMode()`（第 1472 行）—— `SCAN_MODE_NONE`、`_CONNECTABLE`、`_CONNECTABLE_DISCOVERABLE`。

#### 广播 Action

- `ACTION_STATE_CHANGED`（第 142 行）—— 蓝牙开/关状态变化，附加信息：`EXTRA_STATE`、`EXTRA_PREVIOUS_STATE`。
- `ACTION_DISCOVERY_STARTED` —— 发现扫描已开始。
- `ACTION_DISCOVERY_FINISHED` —— 发现扫描已完成。
- `ACTION_SCAN_MODE_CHANGED` —— 扫描模式已更改。

#### 所需权限

- `BLUETOOTH` —— 基本蓝牙操作
- `BLUETOOTH_ADMIN` —— `enable()`、`disable()`、`startDiscovery()`、`setScanMode()`
- `ACCESS_FINE_LOCATION`（或 `ACCESS_COARSE_LOCATION`）—— 发现和 BLE 扫描所需

#### 隐藏/系统 API

- `factoryReset()`（第 1224 行）—— `@SystemApi`
- `setBluetoothClass()`（第 1336 行）—— `@SystemApi`
- `setIoCapability()`（第 1391 行）—— `@SystemApi`
- `removeActiveDevice()`（第 1803 行）—— `@SystemApi`
- `setActiveDevice()`（第 1842 行）—— `@SystemApi`
- `connectAllEnabledProfiles()`（第 1883 行）—— `@SystemApi`
- `disconnectAllEnabledProfiles()`（第 1911 行）—— `@SystemApi`
- BLE 状态常量：`STATE_BLE_TURNING_ON`、`STATE_BLE_ON`、`STATE_BLE_TURNING_OFF`（第 198+ 行）—— `@hide`
- `enableBLE()`（第 961 行）、`disableBLE()`（第 916 行）—— `@hide`

---

### 6.2 BluetoothDevice

**文件**: `frameworks/base/core/java/android/bluetooth/BluetoothDevice.java`（2297 行）

#### 类的目的

`BluetoothDevice` 表示远程蓝牙设备。围绕蓝牙硬件地址的不可变包装器。使用本地 `BluetoothAdapter` 对远程设备执行操作。

#### 获取方式

- `BluetoothAdapter.getRemoteDevice(String address)` —— 从已知 MAC 获取。
- `BluetoothAdapter.getBondedDevices()` —— 从已配对设备集获取。
- 发现期间的 `BluetoothDevice.ACTION_FOUND` 广播（带 `EXTRA_DEVICE`）。

#### 关键公共 API

**设备信息：**
- `getAddress()`（第 1010 行）—— MAC 地址字符串。
- `getName()`（第 1025 行）—— 友好名称（如果尚未获取可能为 null）。
- `getAlias()`（第 1073 行）—— 用户分配的别名或远程名称。
- `setAlias(String)`（第 1103 行）—— 设置本地别名。
- `getType()`（第 1050 行）—— `DEVICE_TYPE_CLASSIC`、`_LE`、`_DUAL`、`_UNKNOWN`。
- `getBondState()`（第 1341 行）—— `BOND_NONE`、`BOND_BONDING`、`BOND_BONDED`。
- `isConnected()`（第 1367 行）—— `@hide`
- `isEncrypted()`（第 1390 行）—— `@hide`

**配对：**
- `createBond()`（第 1152 行）—— 发起配对。
- `createBond(int transport)`（第 1173 行）—— 指定传输方式：`TRANSPORT_AUTO`、`_BREDR`、`_LE`。
- `cancelBondProcess()`（第 1261 行）—— 取消配对。`@SystemApi`。
- `removeBond()`（第 1289 行）—— 取消配对。`@SystemApi`。
- `setPin(byte[])`（第 1520 行）—— 确认配对 PIN。
- `setPairingConfirmation(boolean)`（第 1556 行）—— 确认配对密钥。

**连接：**
- `createRfcommSocketToServiceRecord(UUID)`（第 1922 行）—— 创建安全 RFCOMM `BluetoothSocket`。
- `createInsecureRfcommSocketToServiceRecord(UUID)` —— 非安全 RFCOMM。
- `createL2capChannel(int psm)` —— L2CAP CoC 连接（BLE）。
- `connectGatt(Context, boolean autoConnect, BluetoothGattCallback)` —— 为 BLE 连接 GATT。
- `connectGatt(Context, boolean, BluetoothGattCallback, int transport)` —— 带传输方式。
- `connectGatt(Context, boolean, BluetoothGattCallback, int transport, int phy, Handler)` —— 完整 GATT 连接。

**服务发现：**
- `fetchUuidsWithSdp()`（第 1465 行）—— 发起 SDP 以获取远程服务。

#### 广播 Action

- `ACTION_FOUND`（第 114 行）—— 扫描期间发现设备。附加信息：`EXTRA_DEVICE`、`EXTRA_CLASS`、`EXTRA_NAME`、`EXTRA_RSSI`。
- `ACTION_BOND_STATE_CHANGED`（第 197 行）—— 配对状态已更改。附加信息：`EXTRA_BOND_STATE`、`EXTRA_PREVIOUS_BOND_STATE`。
- `ACTION_ACL_CONNECTED`（第 137 行）—— 低级连接已建立。
- `ACTION_ACL_DISCONNECT_REQUESTED`（第 150 行）—— 已请求断开连接。
- `ACTION_ACL_DISCONNECTED`（第 162 行）—— 已断开连接。
- `ACTION_NAME_CHANGED`（第 173 行）—— 远程名称已获取/更改。
- `ACTION_ALIAS_CHANGED`（第 184 行）—— 别名已更改。

#### 所需权限

- `BLUETOOTH` —— 大多数操作
- `BLUETOOTH_ADMIN` —— `createBond()`、`cancelBondProcess()`、`removeBond()`
- `ACCESS_COARSE_LOCATION` —— `ACTION_FOUND` 广播

#### 隐藏/系统 API

- `createBondOutOfBand()`（第 1195 行）—— `@SystemApi`
- `cancelBondProcess()`（第 1261 行）—— `@SystemApi`
- `removeBond()`（第 1289 行）—— `@SystemApi`
- `setSilenceMode()`（第 1651 行）—— `@SystemApi`
- `setPhonebookAccessPermission()`（第 1698 行）—— `@SystemApi`
- `setMessageAccessPermission()`（第 1743 行）—— `@SystemApi`
- `getBatteryLevel()`（第 1127 行）—— `@hide`
- `createRfcommSocket(int channel)`（第 1831 行）—— `@hide`，直接通道访问
- `isBondingInitiatedLocally()`（第 1218 行）—— `@hide`

---

## 7. NFC API

### 7.1 NfcAdapter

**文件**: `frameworks/base/core/java/android/nfc/NfcAdapter.java`（2113 行）

#### 类的目的

`NfcAdapter` 表示本地 NFC 适配器。它管理标签发现、NDEF 消息交换、Android Beam 和前台调度。

#### 获取方式

- `NfcAdapter.getDefaultAdapter(Context)`（第 671 行）—— 推荐方式。
- `NfcAdapter.getDefaultAdapter()`（第 705 行）—— **已废弃**的静态版本。

#### 关键公共 API

**状态：**
- `isEnabled()`（第 839 行）—— 检查 NFC 是否开启。
- `enable()`（第 889 行）—— `@SystemApi @RequiresPermission(WRITE_SECURE_SETTINGS)`。
- `disable()`（第 918 行）—— `@SystemApi`。

**标签发现 - Intent 调度：**

NFC 对发现的标签使用三级 Intent 调度系统：

1. `ACTION_NDEF_DISCOVERED`（第 86 行）—— 最高优先级。当标签具有包含已识别 URI、SmartPoster 或 MIME 类型的 NDEF 有效载荷时调度。
2. `ACTION_TECH_DISCOVERED`（第 142 行）—— 第二优先级。基于标签技术（NfcA、NfcB、MifareClassic、Ndef 等）调度。需要在清单中使用带有技术列表 XML 的 `<meta-data>`。
3. `ACTION_TAG_DISCOVERED`（第 151 行）—— 最低优先级后备。未处理标签的通用捕获。

附加键：
- `EXTRA_TAG`（第 189 行）—— `Tag` 对象
- `EXTRA_NDEF_MESSAGES`（第 200 行）—— `NdefMessage` 数组

**前台调度（高优先级标签处理）：**
- `enableForegroundDispatch(Activity, PendingIntent, IntentFilter[], String[][])`（第 1485 行）—— 给予前台 Activity 标签处理优先权。
- `disableForegroundDispatch(Activity)`（第 1527 行）

**读卡器模式（独占 NFC 访问）：**
- `enableReaderMode(Activity, ReaderCallback, int flags, Bundle extras)`（第 1579 行）—— 读卡器模式下的独占标签访问。
  - 标志：`FLAG_READER_NFC_A`、`FLAG_READER_NFC_B`、`FLAG_READER_NFC_F`、`FLAG_READER_NFC_V`、`FLAG_READER_NFC_BARCODE`、`FLAG_READER_SKIP_NDEF_CHECK`、`FLAG_READER_NO_PLATFORM_SOUNDS`
- `disableReaderMode(Activity)`（第 1597 行）

**Android Beam（Android Q 中已废弃）：**
- `setNdefPushMessage(NdefMessage, Activity, Activity...)`（第 1211 行）—— 为 Beam 设置静态 NDEF 消息。
- `setNdefPushMessageCallback(CreateNdefMessageCallback, Activity, Activity...)`（第 1330 行）—— 动态 NDEF 消息创建。
- `setBeamPushUris(Uri[], Activity)`（第 1033 行）—— 为 Beam 文件传输设置 URI。
- `setBeamPushUrisCallback(CreateBeamUrisCallback, Activity)`（第 1121 行）—— 动态 URI 创建。
- `setOnNdefPushCompleteCallback(OnNdefPushCompleteCallback, Activity, Activity...)`（第 1417 行）
- `invokeBeam(Activity)`（第 1634 行）—— 以编程方式触发 Beam。

**安全 NFC（Android 10+）：**
- `isSecureNfcSupported()`（第 1781 行）
- `isSecureNfcEnabled()`（第 1801 行）
- `enableSecureNfc(boolean)`（第 1763 行）—— `@SystemApi`

#### 回调接口

- `ReaderCallback.onTagDiscovered(Tag)`（第 404 行）—— 读卡器模式下发现标签。
- `CreateNdefMessageCallback.createNdefMessage(NfcEvent)` —— 为 Beam 创建 NDEF。
- `CreateBeamUrisCallback.createBeamUris(NfcEvent)` —— 为 Beam 创建 URI。
- `OnNdefPushCompleteCallback.onNdefPushComplete(NfcEvent)`（第 424 行）—— Beam 推送完成。
- `NfcUnlockHandler.onUnlockAttempted(Tag)`（第 495 行）—— NFC 解锁尝试。`@SystemApi`。

#### 广播 Action

- `ACTION_ADAPTER_STATE_CHANGED` —— NFC 开/关状态变化。
- `ACTION_TRANSACTION_DETECTED`（第 163 行）—— 安全元件事务事件。需要 `NFC_TRANSACTION_EVENT` 权限。
- `ACTION_PREFERRED_PAYMENT_CHANGED`（第 175 行）—— 首选支付服务已更改。需要 `NFC_PREFERRED_PAYMENT_INFO` 权限。

#### 所需权限

- `NFC` —— 基本 NFC 操作
- `NFC_TRANSACTION_EVENT` —— 接收 `ACTION_TRANSACTION_DETECTED`
- `NFC_PREFERRED_PAYMENT_INFO` —— 接收 `ACTION_PREFERRED_PAYMENT_CHANGED`
- `WRITE_SECURE_SETTINGS` —— `enable()`、`disable()`（仅系统级）

#### 隐藏/系统 API

- `enable()` / `disable()`（第 889、918 行）—— `@SystemApi`
- `enableSecureNfc()`（第 1763 行）—— `@SystemApi`
- `dispatch(Tag)`（第 1974 行）—— `@SystemApi`
- `setP2pModes()`（第 1988 行）—— `@SystemApi`
- `addNfcUnlockHandler()`（第 2013 行）—— `@SystemApi`
- `ignore(Tag, int debounceMs, OnTagRemovedListener)`（第 1934 行）—— `@SystemApi`
- `ACTION_TAG_LEFT_FIELD`（第 182 行）—— `@hide`
- `attemptDeadServiceRecovery()`（第 791 行）—— `@hide`

---

## 8. HttpURLConnection 模式

Android 11 AOSP 内部通过 OkHttp 使用 `HttpURLConnection`。`Network.openConnection(URL)` 方法（`Network.java`，第 339 行）是在特定网络上发起 HTTP 请求的推荐方式：

```java
ConnectivityManager cm = (ConnectivityManager)
    context.getSystemService(Context.CONNECTIVITY_SERVICE);
Network network = cm.getActiveNetwork();
HttpURLConnection conn = (HttpURLConnection) network.openConnection(new URL("https://example.com"));
```

#### 关键实现细节

1. **每网络连接池**：`Network` 为每个实例创建一个 `HttpURLConnectionFactory`，带有私有连接池（第 313-328 行）。连接池默认值：
   - `httpKeepAlive = true`（第 82 行）
   - `httpMaxConnections = 5`（第 84 行）
   - `httpKeepAliveDurationMs = 300000`（5 分钟，第 86 行）

2. **DNS 解析**：每个 `Network` 对象有自己的 DNS 查找，在该网络上进行解析：
   ```java
   Dns dnsLookup = hostname -> Arrays.asList(Network.this.getAllByName(hostname));
   ```
   （第 319 行）

3. **代理支持**：`openConnection()` 通过 `ConnectivityManager.getProxyForNetwork()` 自动查询网络的系统代理（第 345 行）。

4. **套接字绑定**：`NetworkBoundSocketFactory`（内部类，第 200 行）创建绑定到特定网络的套接字，通过 `bindSocket()` 实现。它会尝试主机的所有已解析地址后才放弃（第 206-222 行）。

5. **私有 DNS 绕过**：`mPrivateDnsBypass` 标志（第 107 行）允许系统组件绕过私有 DNS 进行解析，使用 netId 中的特殊标志（`0x80000000L`，第 193 行）。

---

## 9. 横切面观察

### 废弃模式

Android 11 的网络 API 中存在大规模废弃浪潮：

1. **NetworkInfo** —— 整个类已废弃。其所有方法都指向 `NetworkCallback`、`NetworkCapabilities` 和 `LinkProperties`。
2. `ConnectivityManager` 中的 **TYPE_* 常量**（自 API 21+ 起全部废弃）—— 被 `NetworkCapabilities.hasTransport()` 配合 `TRANSPORT_*` 常量替代。
3. **CONNECTIVITY_ACTION** 广播 —— 自 API 24 起不能在清单中声明。改用 `NetworkCallback`。
4. **Wi-Fi 配置 API**（`addNetwork()`、`updateNetwork()` 等）—— 在 API 29 中废弃，被 `WifiNetworkSuggestion` 替代。
5. **旧 Camera API** —— `MediaRecorder.setCamera()` 已废弃，改用 Camera2 + `getSurface()`。
6. **Android Beam** —— 在 Android Q 中废弃。

### 权限升级趋势

- 设备标识符（`getDeviceId()`、`getImei()`、`getMeid()`）现在需要 `READ_PRIVILEGED_PHONE_STATE` —— 第三方应用无法访问。
- Wi-Fi 扫描和蓝牙发现需要位置权限。
- `@SystemApi` 广泛用于敏感操作（网络共享、NFC 控制、蓝牙恢复出厂设置）。

### 线程安全问题

- `MediaPlayer` 明确**不是**线程安全的（第 93 行）。
- `MediaRecorder` 的回调需要 `Looper` 线程。
- `ConnectivityManager.NetworkCallback` 保证在单个线程上有序回调传递。
- `BluetoothAdapter` 文档记载为线程安全（第 93 行）。
- `NetworkInfo` 在所有状态访问方法上使用 `synchronized(this)`。

### 回调限制

`ConnectivityManager` 对**每个应用 UID 施加 100 个未完成请求**的限制（记录在第 3797-3803、4031-4038 行），在 `requestNetwork()`、`registerNetworkCallback()` 和 `ConnectivityDiagnosticsManager` 之间共享。超过此限制将抛出 `RuntimeException`。

### 状态机模式

`MediaPlayer` 和 `MediaRecorder` 都使用有完善文档的状态机。在无效状态下调用方法会抛出 `IllegalStateException`。`MediaPlayer` 还有一个显式的 **Error** 状态，可通过 `reset()` 恢复。

### 隐藏 API 表面

隐藏/系统 API 表面非常庞大：
- `TelephonyManager`：超过 50% 的方法为 `@hide` 或 `@SystemApi`。
- `ConnectivityManager`：网络共享、VPN、网络代理 API 为 `@SystemApi`。
- `BluetoothAdapter`：设备管理、恢复出厂设置、IO 能力为 `@SystemApi`。
- `NfcAdapter`：启用/禁用、调度、安全 NFC 控制为 `@SystemApi`。
- `SmsManager`：SIM 操作、小区广播、注入为 `@SystemApi`。

### 文件摘要

| 文件 | 行数 | 主要 API |
|------|------|---------|
| `ConnectivityManager.java` | 4,770 | 网络状态、请求、回调 |
| `NetworkInfo.java` | 599 | 网络状态（已废弃） |
| `Network.java` | 522 | 网络绑定套接字、DNS、HTTP |
| `Uri.java` | 2,431 | URI 解析和构建 |
| `WifiManager.java` | 6,239 | Wi-Fi 控制、扫描、建议 |
| `TelephonyManager.java` | 13,561 | 电话信息、SIM、通话状态 |
| `SmsManager.java` | 3,007 | 短信/彩信发送 |
| `MediaPlayer.java` | 5,975 | 音频/视频播放 |
| `MediaRecorder.java` | 1,875 | 音频/视频录制 |
| `AudioManager.java` | 6,259 | 音量、音频焦点、路由 |
| `BluetoothAdapter.java` | 3,626 | 本地蓝牙适配器、发现 |
| `BluetoothDevice.java` | 2,297 | 远程蓝牙设备、配对、套接字 |
| `NfcAdapter.java` | 2,113 | NFC 标签、Beam、读卡器模式 |
| **总计** | **53,274** | |
