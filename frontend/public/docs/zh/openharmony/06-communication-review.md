# 通信与网络子系统——代码审查

## OpenHarmony 4.1 版本

**审查日期:** 2026-03-10
**范围:** 网络管理（HTTP/Socket/WebSocket/TLS）、WiFi、蓝牙、NFC、分布式软总线、电话
**审查目录:**
- `/home/dspfac/openharmony/foundation/communication/`（netstack、wifi、bluetooth、nfc、dsoftbus）
- `/home/dspfac/openharmony/base/telephony/`（call_manager、cellular_call、sms_mms、core_service）

---

## 目录

1. [概要](#1-概要)
2. [严重：SSL/TLS 证书验证被禁用](#2-严重ssltls-证书验证被禁用)
3. [严重：WebSocket SSL 验证被完全绕过](#3-严重websocket-ssl-验证被完全绕过)
4. [高：WiFi 权限通过构建配置被绕过](#4-高wifi-权限通过构建配置被绕过)
5. [高：WiFi Lite 权限始终被授予](#5-高wifi-lite-权限始终被授予)
6. [高：TLS NAPI 中类型擦除数据使用不安全的 reinterpret_cast](#6-高tls-napi-中类型擦除数据使用不安全的-reinterpret_cast)
7. [高：TLS Socket 绑定失败时内存泄漏](#7-高tls-socket-绑定失败时内存泄漏)
8. [高：WebSocket 使用 volatile bool 而无原子性保证](#8-高websocket-使用-volatile-bool-而无原子性保证)
9. [中：WiFi 状态机中硬编码公共 DNS 服务器](#9-中wifi-状态机中硬编码公共-dns-服务器)
10. [中：网络栈中过度使用分离线程](#10-中网络栈中过度使用分离线程)
11. [中：HTTP 内置 Fetch SSL 默认禁用](#11-中http-内置-fetch-ssl-默认禁用)
12. [中：DSoftBus 设备信息泄露敏感标识符](#12-中dsoftbus-设备信息泄露敏感标识符)
13. [中：WebSocket 日志泄露连接详情](#13-中websocket-日志泄露连接详情)
14. [低：TLS 连接上下文静默忽略缺少的 secureOptions](#14-低tls-连接上下文静默忽略缺少的-secureoptions)
15. [低：证书属性名拼写错误](#15-低证书属性名拼写错误)
16. [低：蓝牙 BLE 扫描在 NAPI 层缺少权限检查](#16-低蓝牙-ble-扫描在-napi-层缺少权限检查)
17. [正面发现](#17-正面发现)
18. [汇总表](#18-汇总表)

---

## 1. 概要

通信与网络子系统是 OpenHarmony 中最安全敏感的部分之一。本次审查发现了**多个严重安全问题**，涉及 SSL/TLS 证书验证在多个网络 API 中被禁用，以及可通过构建标志启用的权限绕过能力。最令人警觉的发现包括：

- **HTTP、Fetch 和 WebSocket API 均存在完全禁用 SSL 证书验证的路径**，使其容易受到中间人攻击。
- **WiFi 权限检查可通过构建时标志全局绕过**（`wifi_feature_with_auth_disable`）。
- **在 OHOS_ARCH_LITE 构建中，所有 WiFi 权限均无条件授予**。

代码质量总体上可接受，具有一致的错误处理模式和正确的 NAPI 生命周期管理。然而，普遍使用分离线程和类型擦除数据的 `reinterpret_cast` 带来了可靠性和类型安全风险。

---

## 2. 严重：SSL/TLS 证书验证被禁用

**严重性:** 严重
**文件:**
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/http/http_exec/src/http_exec.cpp`（第 774-775 行）
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/fetch/fetch_exec/src/fetch_exec.cpp`（第 274-275 行）
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/native/http/http_client/http_client_task.cpp`（第 174-175 行）

**描述:**

当定义了构建宏 `NO_SSL_CERTIFICATION` 时，HTTP 和 Fetch API 完全禁用 SSL 验证：

```cpp
// http_exec.cpp:773-775
#else
    // in real life, you should buy a ssl certification and rename it to /etc/ssl/cert.pem
    NETSTACK_CURL_EASY_SET_OPTION(curl, CURLOPT_SSL_VERIFYHOST, 0L, context);
    NETSTACK_CURL_EASY_SET_OPTION(curl, CURLOPT_SSL_VERIFYPEER, 0L, context);
#endif // NO_SSL_CERTIFICATION
```

注释"in real life, you should buy a ssl certification"表明这原本是开发时的临时方案，但它被默认编译到了内置 Fetch 模块中：

```
// frameworks/js/builtin/BUILD.gn:47
defines = [ "NO_SSL_CERTIFICATION=1" ]
```

这意味着旧版内置 `fetch()` API **始终**在禁用 SSL 验证的状态下运行，使该 API 的每个 HTTPS 请求都容易受到中间人攻击。

**影响:** 任何使用内置 fetch API（或任何带有 `NO_SSL_CERTIFICATION` 的构建）的应用程序对 HTTPS 连接的中间人攻击没有任何保护。同一网络上的攻击者可以拦截、修改或注入所有原本应安全的 HTTP 通信中的数据。

**建议:** 从生产构建中移除 `NO_SSL_CERTIFICATION` 构建定义。提供适当的系统 CA 包，而非完全禁用验证。如果需要开发专用的覆盖选项，请将其限制在不可发布到生产环境的可调试构建类型中。

---

## 3. 严重：WebSocket SSL 验证被完全绕过

**严重性:** 严重
**文件:**
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/websocket/websocket_exec/src/websocket_exec.cpp`（第 573 行）
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/native/websocket_client/websocket_client.cpp`（第 373 行）

**描述:**

NAPI WebSocket 和原生 WebSocket 客户端均无条件跳过服务器证书主机名检查，并允许所有 WSS 连接使用自签名证书：

```cpp
// websocket_exec.cpp:571-573（NAPI 层）
if (strcmp(prefix, PREFIX_HTTPS) == 0 || strcmp(prefix, PREFIX_WSS) == 0) {
    connectInfo.ssl_connection =
        LCCSCF_USE_SSL | LCCSCF_SKIP_SERVER_CERT_HOSTNAME_CHECK | LCCSCF_ALLOW_SELFSIGNED;
}

// websocket_client.cpp:372-373（原生层——更严重）
    connectInfo.ssl_connection =
        LCCSCF_USE_SSL | LCCSCF_SKIP_SERVER_CERT_HOSTNAME_CHECK | LCCSCF_ALLOW_INSECURE | LCCSCF_ALLOW_SELFSIGNED;
```

这**不依赖于**任何构建标志——它无条件地应用于所有 WebSocket 连接。原生层还额外设置了 `LCCSCF_ALLOW_INSECURE`。

**影响:** OpenHarmony 中所有 WSS（安全 WebSocket）连接都容易受到中间人攻击。攻击者可以出示任何证书（自签名、过期、错误主机名），都会被接受。这完全违背了使用 WSS 的目的。

**建议:** 移除 `LCCSCF_SKIP_SERVER_CERT_HOSTNAME_CHECK`、`LCCSCF_ALLOW_SELFSIGNED` 和 `LCCSCF_ALLOW_INSECURE` 标志。使用系统 CA 存储进行证书验证。如果应用程序需要使用自签名证书（例如物联网场景），请提供显式的可选 API 参数，而非全局禁用验证。

---

## 4. 高：WiFi 权限通过构建配置被绕过

**严重性:** 高
**文件:**
- `/home/dspfac/openharmony/foundation/communication/wifi/wifi/services/wifi_standard/wifi_framework/wifi_manage/wifi_auth_center.cpp`（第 30-34 行）
- `/home/dspfac/openharmony/foundation/communication/wifi/wifi/services/wifi_standard/wifi_framework/wifi_manage/BUILD.gn`（第 107 行）
- `/home/dspfac/openharmony/foundation/communication/wifi/wifi/wifi_lite.gni`（第 27 行）

**描述:**

当构建配置中 `wifi_feature_with_auth_disable` 设为 `true` 时，宏 `PERMISSION_ALWAYS_GRANT` 被定义，导致**所有 WiFi 权限检查无条件返回 GRANTED**：

```cpp
// wifi_auth_center.cpp:30-34
#ifdef PERMISSION_ALWAYS_GRANT
bool g_permissinAlwaysGrant = true;
#else
bool g_permissinAlwaysGrant = false;
#endif

// 然后每个权限检查都会执行：
int WifiAuthCenter::VerifySetWifiInfoPermission(const int &pid, const int &uid)
{
    if (g_permissinAlwaysGrant) {
        return PERMISSION_GRANTED;
    }
    // ...
}
```

虽然默认值为 `false`，但此构建标志的存在意味着它可能被无意启用或被设备制造商故意设置，从而完全禁用 WiFi 权限执行。

**影响:** 如果启用，任何应用程序都可以扫描 WiFi 网络、连接/断开、修改 WiFi 配置、创建热点，并访问位置敏感的扫描数据，而无需任何权限检查。

**建议:** 从生产代码中完全移除 `PERMISSION_ALWAYS_GRANT` 机制。如果测试需要，仅限于调试/测试构建，并添加编译时断言防止在发布配置中设置。变量名也包含拼写错误（"permissin" 应为 "permission"）。

---

## 5. 高：WiFi Lite 权限始终被授予

**严重性:** 高
**文件:** `/home/dspfac/openharmony/foundation/communication/wifi/wifi/services/wifi_standard/wifi_framework/wifi_manage/common/wifi_permission_utils.cpp`（第 23-87 行）

**描述:**

在 `OHOS_ARCH_LITE` 构建中，**每一个 WiFi 权限检查均无条件返回 `PERMISSION_GRANTED`**：

```cpp
#ifdef OHOS_ARCH_LITE
int WifiPermissionUtils::VerifySetWifiInfoPermission()
{
    return PERMISSION_GRANTED;
}
int WifiPermissionUtils::VerifyGetScanInfosPermission()
{
    return PERMISSION_GRANTED;
}
// ... 所有 13 个权限方法均返回 PERMISSION_GRANTED
#endif
```

**影响:** 在运行 OHOS_ARCH_LITE 的轻量级/物联网设备上，所有 WiFi 操作都不受限制。任何进程都可以访问 WiFi 扫描结果（位置敏感数据）、修改 WiFi 设置、创建热点以及读取附近对等设备的 MAC 地址。

**建议:** 为 OHOS_ARCH_LITE 实现轻量级权限系统，而非一揽子授予所有权限。至少，对扫描信息访问（会揭示位置数据）和 MAC 地址访问等敏感操作应要求某种形式的授权。

---

## 6. 高：TLS NAPI 中类型擦除数据使用不安全的 reinterpret_cast

**严重性:** 高
**文件:** `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/tls/src/tlssocket_exec.cpp`（多处）

**描述:**

在整个 TLS socket NAPI 绑定中，`manager->GetData()` 返回 `void*`，通过 `reinterpret_cast` 转换为 `TLSSocket*` 而无任何类型验证：

```cpp
auto tlsSocket = reinterpret_cast<TLSSocket *>(manager->GetData());
```

此模式在文件中出现 10 次。如果 `manager->GetData()` 返回了错误类型的数据（由于其他地方的 bug、与 `ExecBind` 设置数据的竞态条件或管理器混乱），则会导致未定义行为。

**影响:** 类型混淆 bug 可导致内存损坏、崩溃或潜在的可利用条件。由于 TLS socket 处理安全敏感的加密操作，损坏可能危及加密安全。

**建议:** 使用类型化包装器或带标签联合模式来存储类型信息及数据指针。至少在转换前添加类型标签检查。

---

## 7. 高：TLS Socket 绑定失败时内存泄漏

**严重性:** 高
**文件:** `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/tls/src/tlssocket_exec.cpp`（第 230-239 行）

**描述:**

在 `ExecBind` 中，使用 `new` 分配新的 `TLSSocket` 并无论 `Bind` 是否成功都赋值给管理器：

```cpp
bool TLSSocketExec::ExecBind(TLSBindContext *context)
{
    // ...
    auto tlsSocket = new TLSSocket();
    tlsSocket->Bind(context->address_, [&context](int32_t errorNumber) {
        context->errorNumber_ = errorNumber;
        if (errorNumber != TLSSOCKET_SUCCESS) {
            std::string errorString = MakeErrorMessage(errorNumber);
            context->SetError(errorNumber, errorString);
        }
    });
    manager->SetData(tlsSocket);  // 无论绑定结果都设置
    return context->errorNumber_ == TLSSOCKET_SUCCESS;
}
```

如果 `Bind` 失败，函数返回 `false`，但 `TLSSocket*` 仍然被赋值给管理器。取决于上游的错误处理，这可能导致一个孤立的 socket 对象在部分初始化状态下被使用，或者如果管理器在未调用 `ExecClose` 的情况下被清理，`TLSSocket` 就会泄漏。

此外，在 `ExecClose`（第 218 行）中，`delete tlsSocket` 在未先进行空指针检查、也未确认关闭回调是否成功完成的情况下被直接调用——`Close` 回调可能失败，但对象仍然被删除。

**影响:** 内存泄漏和潜在的释放后使用。失败的绑定操作会留下已分配但未正确初始化的 socket 对象。

**建议:** 仅在 `Bind` 成功时将 `TLSSocket*` 赋值给管理器。失败时立即 `delete tlsSocket`。在 `ExecClose` 中，确保仅在确认关闭操作完成后才进行删除，并考虑使用 `std::unique_ptr`。

---

## 8. 高：WebSocket 使用 volatile bool 而无原子性保证

**严重性:** 高
**文件:** `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/websocket/websocket_exec/src/websocket_exec.cpp`（第 181 行）

**描述:**

`UserData` 类使用 `volatile bool closed_` 进行跨线程状态管理，但 `volatile` 在 C++ 中不提供原子性或内存排序保证：

```cpp
class UserData {
    // ...
    bool IsClosed()
    {
        std::lock_guard<std::mutex> lock(mutex_);
        return closed_;
    }
    void Close(lws_close_status status, const std::string &reason)
    {
        std::lock_guard<std::mutex> lock(mutex_);
        // ...
        closed_ = true;
    }
private:
    volatile bool closed_;       // volatile 不足以保证线程安全
    std::atomic_bool threadStop_; // threadStop_ 正确使用了 atomic
};
```

值得注意的是，`threadStop_` 正确使用了 `std::atomic_bool`，但 `closed_` 在被多个线程访问的情况下使用了 `volatile`。`IsClosed()` 和 `Close()` 方法确实使用了互斥锁，但 `closed_` 也在 `LwsCallbackClientWritable` 中通过 `userData->IsClosed()` 被直接读取，而 LWS 服务线程同时在运行。

**影响:** `closed_` 标志上可能存在数据竞争。`volatile` 关键字不能防止编译器/CPU 重排序，也不能保证跨核心的可见性。虽然互斥锁部分缓解了这个问题，但 `volatile` 声明表明对线程模型存在误解。

**建议:** 将 `volatile bool closed_` 替换为 `std::atomic_bool closed_` 以与 `threadStop_` 保持一致，或者完全依赖互斥锁（移除 `volatile`）。

---

## 9. 中：WiFi 状态机中硬编码公共 DNS 服务器

**严重性:** 中
**文件:** `/home/dspfac/openharmony/foundation/communication/wifi/wifi/services/wifi_standard/wifi_framework/wifi_manage/wifi_sta/sta_state_machine.cpp`（第 48-49 行）

**描述:**

WiFi 状态机硬编码了 Google 和百度的公共 DNS 服务器作为备用：

```cpp
#define FIRST_DNS "8.8.8.8"
#define SECOND_DNS "180.76.76.76"
```

这些在 DHCP 提供的 DNS 服务器满足特定条件时用作替代 DNS 服务器（第 2199 行以后）。

**影响:**
1. **隐私:** DNS 查询在未经用户同意或知情的情况下发送到第三方服务器（Google、百度）。
2. **地区合规:** 在某些司法管辖区，将 DNS 路由通过外国服务器可能违反数据主权要求。
3. **可靠性:** 如果这些服务器被封锁（例如在某些网络环境中），DNS 解析将失败。

**建议:** 使备用 DNS 服务器可配置而非硬编码。考虑使用 DHCP 提供的 DNS 或设备制造商可配置的系统属性。至少应记录这些备用服务器的存在。

---

## 10. 中：网络栈中过度使用分离线程

**严重性:** 中
**文件:**
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/websocket/websocket_exec/src/websocket_exec.cpp`（第 670 行）
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/http/http_exec/src/http_exec.cpp`（第 188 行）
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/native/tls_socket/src/tls_socket.cpp`（第 410 行）
- 多个 socket_exec.cpp 文件

**描述:**

网络栈大量使用 `std::thread(...).detach()`（仅 netstack 中就有 10 处以上，WiFi 中也有）。分离线程存在以下问题：

1. 在关闭时无法 join，如果进程在线程仍在运行时退出，则导致未定义行为。
2. 无法将异常或错误从线程传播回调用者。
3. 如果拥有者对象在线程仍在运行时被销毁，则可能访问悬空指针。

WebSocket 中的示例：
```cpp
std::thread serviceThread(RunService, manager);
serviceThread.detach();
```

TLS socket 的 `StartReadMessage()` 也分离了一个捕获 `this` 的读取线程，如果 TLSSocket 在线程运行时被销毁，则会造成潜在的释放后使用。

**影响:** 资源泄漏、关闭时可能崩溃以及释放后使用漏洞。

**建议:** 使用受管理的线程池、`std::jthread`（C++20），或确保在析构时正确 join 的语义。特别是对于 TLS socket 读取线程，使用 shared pointer 指向 socket 或实现适当的关闭同步机制。

---

## 11. 中：HTTP 内置 Fetch SSL 默认禁用

**严重性:** 中（与发现 #2 重叠，但特定于内置模块）
**文件:** `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/builtin/BUILD.gn`（第 47 行）

**描述:**

旧版内置 fetch 模块显式定义了 `NO_SSL_CERTIFICATION=1`：

```gn
defines = [ "NO_SSL_CERTIFICATION=1" ]
```

与 NAPI fetch/http 模块中这是条件构建标志不同，内置模块**始终**禁用 SSL 验证。`http_request.cpp:93` 中相应的代码禁用了对等验证：

```cpp
ACE_CURL_EASY_SET_OPTION(handle.get(), CURLOPT_SSL_VERIFYPEER, 0L, responseData);
```

请注意，这里甚至没有显式设置 `CURLOPT_SSL_VERIFYHOST`，意味着它可能默认为 2（验证）——但在没有对等验证的情况下，主机名验证毫无意义。

**影响:** 使用旧版 fetch API 的应用程序没有 SSL 证书验证。

**建议:** 为内置模块提供适当的 CA 包，或将所有调用者迁移到支持正确证书验证的 NAPI fetch/http API。

---

## 12. 中：DSoftBus 设备信息泄露敏感标识符

**严重性:** 中
**文件:** `/home/dspfac/openharmony/foundation/communication/dsoftbus/core/authentication/src/auth_session_message.c`

**描述:**

DSoftBus 认证会话消息处理程序在认证握手期间交换大量设备信息，包括：

- `DEVICE_UDID` —— 唯一设备标识符
- `BT_MAC` / `BLE_MAC` —— 蓝牙 MAC 地址
- `BR_MAC_ADDR` —— 蓝牙无线电 MAC
- `P2P_MAC_ADDR` —— WiFi Direct MAC 地址
- `IP_MAC` —— IP 关联 MAC 地址
- `ACCOUNT_ID` —— 用户账户标识符
- `NETWORK_ID` —— 网络标识符
- `DEVICE_NAME` —— 人类可读设备名称

这些标识符在初始认证阶段交换。`AUTH_LOGD` 和 `AUTH_LOGI` 调用也可能记录其中一些值。

**影响:** 执行主动发现或拦截 DSoftBus 认证消息的攻击者可以收集持久设备标识符，实现跨会话和跨位置的追踪。

**建议:** 最小化认证完成前交换的信息。尽可能使用临时标识符。确保所有敏感标识符在日志中进行匿名化处理（部分调用确实使用了 `Anonymizer`，但覆盖不一致）。

---

## 13. 中：WebSocket 日志泄露连接详情

**严重性:** 中
**文件:** `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/websocket/websocket_exec/src/websocket_exec.cpp`

**描述:**

WebSocket 实现中多个日志语句泄露连接详情：

```cpp
// 第 410 行：以明文记录连接错误详情
NETSTACK_LOGI("LwsCallbackClientConnectionError %{public}s",
              (in == nullptr) ? "null" : reinterpret_cast<char *>(in));

// 第 839 行：记录打开状态和消息
NETSTACK_LOGI("OnOpen %{public}u %{public}s", status, message.c_str());

// 第 860 行：记录关闭状态和原因
NETSTACK_LOGI("OnClose %{public}u %{public}s", closeStatus, closeReason.c_str());
```

此外，CA 路径在调试级别被记录（第 613 行）：
```cpp
NETSTACK_LOGD("caPath: %{public}s", info.client_ssl_ca_filepath);
```

**影响:** 连接错误详情、状态消息和证书路径被记录，可能被其他进程或通过日志收集访问，泄露服务器交互详情。

**建议:** 对连接特定的详情和可能揭示服务器基础设施信息的错误消息使用 `%{private}s`。

---

## 14. 低：TLS 连接上下文静默忽略缺少的 secureOptions

**严重性:** 低
**文件:** `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/tls/src/context/tls_connect_context.cpp`（第 159-169 行）

**描述:**

如果连接选项中缺少 `secureOptions` 属性，函数静默返回一个默认构造的 `TLSSecureOptions` 对象，其中没有 CA 证书、没有客户端证书，使用默认验证模式：

```cpp
TLSSecureOptions TLSConnectContext::ReadTLSSecureOptions(napi_env env, napi_value *params)
{
    TLSSecureOptions secureOption;
    if (!NapiUtils::HasNamedProperty(GetEnv(), params[ARG_INDEX_0], SECURE_OPTIONS)) {
        return secureOption;  // 静默返回空选项
    }
    // ...
}
```

同样，如果 `ReadNecessaryOptions` 返回 false（例如未提供 CA 证书），部分构造的选项仍然被返回。

**影响:** 开发者可能在没有适当安全配置的情况下意外创建 TLS 连接，且不会收到任何警告或错误。

**建议:** 当 TLS 连接调用中完全缺少 `secureOptions` 时，返回错误或记录警告。

---

## 15. 低：证书属性名拼写错误

**严重性:** 低
**文件:** `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/tls/src/tlssocket_exec.cpp`（第 35-36 行）

**描述:**

常量名称包含拼写错误——"CERTIFICATA" 应为 "CERTIFICATE"：

```cpp
constexpr const char *CERTIFICATA_DATA = "data";
constexpr const char *CERTIFICATA_ENCODING_FORMAT = "encodingFormat";
```

虽然这不影响功能（常量值是正确的），但表明命名不够严谨，可能会给维护代码的开发者带来困惑。

**建议:** 重命名为 `CERTIFICATE_DATA` 和 `CERTIFICATE_ENCODING_FORMAT`。

---

## 16. 低：蓝牙 BLE 扫描在 NAPI 层缺少权限检查

**严重性:** 低
**文件:** `/home/dspfac/openharmony/foundation/communication/bluetooth/frameworks/js/napi/src/ble/napi_bluetooth_ble.cpp`（第 105-131 行）

**描述:**

`SysStartBLEScan` 函数在 NAPI 层启动 BLE 扫描时没有可见的权限检查：

```cpp
napi_value SysStartBLEScan(napi_env env, napi_callback_info info)
{
    // ... 参数解析 ...
    BleCentralManagerGetInstance()->StartScan(settinngs);  // 无权限检查
    return NapiGetNull(env);
}
```

BLE 扫描结果可以揭示附近的蓝牙设备及其标识符，这构成位置敏感数据。虽然权限检查可能在底层执行（在 BleCentralManager 服务中），但 NAPI 层不进行拦截，意味着权限错误只能异步显现，而非被及早捕获。

此外，请注意变量名中的拼写错误：`settinngs` 应为 `settings`。

**建议:** 在 NAPI 层添加显式权限检查以向调用者提供即时反馈，与 WiFi 和电话子系统处理权限的方式保持一致。

---

## 17. 正面发现

1. **NFC 权限执行:** NFC `TagSessionStub` 在每个 IPC 处理程序之前一致地检查 `ExternalDepsProxy::GetInstance().IsGranted(OHOS::NFC::TAG_PERM)`，展示了良好的权限执行实践。

2. **电话权限系统:** `TelephonyPermission::CheckPermission` 实现正确验证访问令牌，通过 `PrivacyKit::AddPermissionUsedRecord` 记录权限使用情况，并记录失败的权限检查。

3. **HTTP 错误处理:** `RequestContext` 类在析构函数中正确清理 `curl_slist` 和 `curl_mime` 资源，防止错误路径上的内存泄漏。

4. **HTTP 请求上下文线程安全:** HTTP 模块使用互斥锁保护的队列（`dlLenLock_`、`ulLenLock_`、`tempDataLock_`）在 curl 工作线程和主线程之间进行线程安全的数据传输。

5. **WebSocket 发送队列:** WebSocket 的 `UserData::Push/Pop` 操作对发送数据队列使用适当的互斥锁同步。

6. **DSoftBus 加密:** DSoftBus 认证模块使用 AES-GCM 加密进行快速认证令牌处理，通过 `AuthDeviceKeyInfo` 结构进行适当的密钥管理。

7. **HTTP DNS 验证:** `request_context.cpp` 中的 `ParseDnsServers` 函数在接受 DNS 服务器地址前正确验证其为有效的 IPv4 或 IPv6 地址。

8. **TLS 上下文配置:** `tls_context.cpp` 中的 TLS socket 实现为单向模式正确设置 `SSL_VERIFY_PEER`，为双向模式正确设置 `SSL_VERIFY_FAIL_IF_NO_PEER_CERT`，为 TLS socket API 提供了正确的证书验证行为（与 WebSocket 和 HTTP API 不同）。

---

## 18. 汇总表

| # | 发现 | 严重性 | 类别 | 组件 |
|---|------|--------|------|------|
| 2 | 通过 NO_SSL_CERTIFICATION 禁用 SSL/TLS 证书验证 | 严重 | 安全 | netstack/http, fetch |
| 3 | WebSocket SSL 验证被无条件绕过 | 严重 | 安全 | netstack/websocket |
| 4 | WiFi 权限通过 PERMISSION_ALWAYS_GRANT 被绕过 | 高 | 安全 | wifi |
| 5 | WiFi Lite 所有权限始终被授予 | 高 | 安全 | wifi |
| 6 | TLS socket 类型擦除数据使用不安全的 reinterpret_cast | 高 | 代码质量 | netstack/tls |
| 7 | TLS Socket 绑定失败时内存泄漏 | 高 | 资源管理 | netstack/tls |
| 8 | WebSocket volatile bool 线程安全问题 | 高 | 线程安全 | netstack/websocket |
| 9 | 硬编码公共 DNS 服务器（8.8.8.8, 180.76.76.76） | 中 | 隐私 | wifi |
| 10 | 过度使用分离线程 | 中 | 代码质量 | netstack, wifi |
| 11 | HTTP 内置 fetch SSL 始终禁用 | 中 | 安全 | netstack/fetch |
| 12 | DSoftBus 认证交换敏感设备标识符 | 中 | 隐私 | dsoftbus |
| 13 | WebSocket 日志泄露连接详情 | 中 | 隐私 | netstack/websocket |
| 14 | TLS 静默忽略缺少的 secureOptions | 低 | 代码质量 | netstack/tls |
| 15 | 证书属性名拼写错误（CERTIFICATA） | 低 | 代码质量 | netstack/tls |
| 16 | BLE 扫描在 NAPI 层缺少权限检查 | 低 | 安全 | bluetooth |

**总计：2 个严重、5 个高、5 个中、3 个低**

围绕 SSL/TLS 证书验证的两个严重发现代表了需要立即修复的最紧急问题。WebSocket SSL 绕过尤其令人担忧，因为它是无条件的，影响整个平台上所有安全 WebSocket 连接。
