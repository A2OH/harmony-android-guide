# Communication and Networking Subsystem - 代码审查

## OpenHarmony 4.1 Release

**Review Date:** 2026-03-10
**Scope:** Network management (HTTP/Socket/WebSocket/TLS), WiFi, Bluetooth, NFC, Distributed Softbus, Telephony
**Directories Reviewed:**
- `/home/dspfac/openharmony/foundation/communication/` (netstack, wifi, bluetooth, nfc, dsoftbus)
- `/home/dspfac/openharmony/base/telephony/` (call_manager, cellular_call, sms_mms, core_service)

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [CRITICAL: SSL/TLS Certificate Validation Disabled](#2-critical-ssltls-certificate-validation-disabled)
3. [CRITICAL: WebSocket SSL Verification Completely Bypassed](#3-critical-websocket-ssl-verification-completely-bypassed)
4. [HIGH: WiFi Permission Bypass via Build Configuration](#4-high-wifi-permission-bypass-via-build-configuration)
5. [HIGH: WiFi Lite Permissions Always Granted](#5-high-wifi-lite-permissions-always-granted)
6. [HIGH: Unsafe reinterpret_cast for Type-Erased Data in TLS NAPI](#6-high-unsafe-reinterpret_cast-for-type-erased-data-in-tls-napi)
7. [HIGH: TLS Socket Memory Leak on Bind Failure](#7-high-tls-socket-memory-leak-on-bind-failure)
8. [HIGH: WebSocket volatile bool Without Atomic Guarantees](#8-high-websocket-volatile-bool-without-atomic-guarantees)
9. [MEDIUM: Hardcoded Public DNS Servers in WiFi State Machine](#9-medium-hardcoded-public-dns-servers-in-wifi-state-machine)
10. [MEDIUM: Excessive Use of Detached Threads in Networking Stack](#10-medium-excessive-use-of-detached-threads-in-networking-stack)
11. [MEDIUM: HTTP Builtin Fetch SSL Disabled by Default](#11-medium-http-builtin-fetch-ssl-disabled-by-default)
12. [MEDIUM: DSoftBus Device Info Leaks Sensitive Identifiers](#12-medium-dsoftbus-device-info-leaks-sensitive-identifiers)
13. [MEDIUM: WebSocket Logs Leak Connection Details](#13-medium-websocket-logs-leak-connection-details)
14. [LOW: TLS Connect Context Silently Ignores Missing secureOptions](#14-low-tls-connect-context-silently-ignores-missing-secureoptions)
15. [LOW: Certificate Property Name Typo](#15-low-certificate-property-name-typo)
16. [LOW: Bluetooth BLE Scan Lacks Permission Check at NAPI Layer](#16-low-bluetooth-ble-scan-lacks-permission-check-at-napi-layer)
17. [Positive Findings](#17-positive-findings)
18. [Summary Table](#18-summary-table)

---

## 1. Executive Summary

The Communication and Networking subsystem is one of the most security-sensitive parts of OpenHarmony. This review uncovered **multiple critical security issues** related to SSL/TLS certificate validation being disabled across several networking APIs, as well as permission bypass capabilities that could be enabled via build flags. The most alarming findings are:

- **HTTP, Fetch, and WebSocket APIs all have paths where SSL certificate verification is completely disabled**, making them vulnerable to man-in-the-middle attacks.
- **WiFi permission checks can be globally bypassed** via a build-time flag (`wifi_feature_with_auth_disable`).
- **All WiFi permissions are unconditionally granted** on OHOS_ARCH_LITE builds.

Code quality is generally acceptable, with consistent error handling patterns and proper use of NAPI lifecycle management. However, the pervasive use of detached threads and `reinterpret_cast` for type-erased data creates reliability and type-safety risks.

---

## 2. CRITICAL: SSL/TLS Certificate Validation Disabled

**Severity:** CRITICAL
**Files:**
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/http/http_exec/src/http_exec.cpp` (lines 774-775)
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/fetch/fetch_exec/src/fetch_exec.cpp` (lines 274-275)
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/native/http/http_client/http_client_task.cpp` (lines 174-175)

**Description:**

When the build macro `NO_SSL_CERTIFICATION` is defined, the HTTP and Fetch APIs completely disable SSL verification:

```cpp
// http_exec.cpp:773-775
#else
    // in real life, you should buy a ssl certification and rename it to /etc/ssl/cert.pem
    NETSTACK_CURL_EASY_SET_OPTION(curl, CURLOPT_SSL_VERIFYHOST, 0L, context);
    NETSTACK_CURL_EASY_SET_OPTION(curl, CURLOPT_SSL_VERIFYPEER, 0L, context);
#endif // NO_SSL_CERTIFICATION
```

The comment "in real life, you should buy a ssl certification" suggests this was intended as a development-time workaround, but it is compiled into the builtin Fetch module by default:

```
// frameworks/js/builtin/BUILD.gn:47
defines = [ "NO_SSL_CERTIFICATION=1" ]
```

This means the legacy builtin `fetch()` API **always** operates with SSL verification disabled, making every HTTPS request from that API vulnerable to MITM attacks.

**Impact:** Any application using the builtin fetch API (or any build with `NO_SSL_CERTIFICATION`) has zero protection against MITM attacks on HTTPS connections. Attackers on the same network can intercept, modify, or inject data in all supposedly secure HTTP communications.

**Recommendation:** Remove the `NO_SSL_CERTIFICATION` build define from production builds. Provide a proper system CA bundle instead of disabling verification entirely. If a development-only override is needed, gate it behind a debuggable build type that cannot ship to production.

---

## 3. CRITICAL: WebSocket SSL Verification Completely Bypassed

**Severity:** CRITICAL
**Files:**
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/websocket/websocket_exec/src/websocket_exec.cpp` (line 573)
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/native/websocket_client/websocket_client.cpp` (line 373)

**Description:**

Both the NAPI WebSocket and the native WebSocket client unconditionally skip server certificate hostname checks and allow self-signed certificates for all WSS connections:

```cpp
// websocket_exec.cpp:571-573 (NAPI layer)
if (strcmp(prefix, PREFIX_HTTPS) == 0 || strcmp(prefix, PREFIX_WSS) == 0) {
    connectInfo.ssl_connection =
        LCCSCF_USE_SSL | LCCSCF_SKIP_SERVER_CERT_HOSTNAME_CHECK | LCCSCF_ALLOW_SELFSIGNED;
}

// websocket_client.cpp:372-373 (native layer - even worse)
    connectInfo.ssl_connection =
        LCCSCF_USE_SSL | LCCSCF_SKIP_SERVER_CERT_HOSTNAME_CHECK | LCCSCF_ALLOW_INSECURE | LCCSCF_ALLOW_SELFSIGNED;
```

This is **not conditional** on any build flag -- it applies to all WebSocket connections unconditionally. The native layer additionally sets `LCCSCF_ALLOW_INSECURE`.

**Impact:** All WSS (WebSocket Secure) connections in OpenHarmony are vulnerable to man-in-the-middle attacks. An attacker can present any certificate (self-signed, expired, wrong hostname) and it will be accepted. This defeats the entire purpose of using WSS.

**Recommendation:** Remove `LCCSCF_SKIP_SERVER_CERT_HOSTNAME_CHECK`, `LCCSCF_ALLOW_SELFSIGNED`, and `LCCSCF_ALLOW_INSECURE` flags. Use the system CA store for certificate validation. If an application needs to work with self-signed certificates (e.g., IoT), provide an explicit opt-in API parameter rather than disabling verification globally.

---

## 4. HIGH: WiFi Permission Bypass via Build Configuration

**Severity:** HIGH
**Files:**
- `/home/dspfac/openharmony/foundation/communication/wifi/wifi/services/wifi_standard/wifi_framework/wifi_manage/wifi_auth_center.cpp` (lines 30-34)
- `/home/dspfac/openharmony/foundation/communication/wifi/wifi/services/wifi_standard/wifi_framework/wifi_manage/BUILD.gn` (line 107)
- `/home/dspfac/openharmony/foundation/communication/wifi/wifi/wifi_lite.gni` (line 27)

**Description:**

When `wifi_feature_with_auth_disable` is set to `true` in the build configuration, the macro `PERMISSION_ALWAYS_GRANT` is defined, which causes **all WiFi permission checks to return GRANTED unconditionally**:

```cpp
// wifi_auth_center.cpp:30-34
#ifdef PERMISSION_ALWAYS_GRANT
bool g_permissinAlwaysGrant = true;
#else
bool g_permissinAlwaysGrant = false;
#endif

// Every permission check then does:
int WifiAuthCenter::VerifySetWifiInfoPermission(const int &pid, const int &uid)
{
    if (g_permissinAlwaysGrant) {
        return PERMISSION_GRANTED;
    }
    // ...
}
```

While the default value is `false`, this build flag exists and could be inadvertently enabled or deliberately set by device manufacturers, completely disabling WiFi permission enforcement.

**Impact:** If enabled, any application can scan WiFi networks, connect/disconnect, modify WiFi configuration, create hotspots, and access location-sensitive scan data without any permission checks.

**Recommendation:** Remove the `PERMISSION_ALWAYS_GRANT` mechanism entirely from production code. If needed for testing, restrict it to debug/test builds only and add compile-time assertions that prevent it from being set in release configurations. The variable name also contains a typo ("permissin" instead of "permission").

---

## 5. HIGH: WiFi Lite Permissions Always Granted

**Severity:** HIGH
**File:** `/home/dspfac/openharmony/foundation/communication/wifi/wifi/services/wifi_standard/wifi_framework/wifi_manage/common/wifi_permission_utils.cpp` (lines 23-87)

**Description:**

On `OHOS_ARCH_LITE` builds, **every single WiFi permission check returns `PERMISSION_GRANTED` unconditionally**:

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
// ... all 13 permission methods return PERMISSION_GRANTED
#endif
```

**Impact:** On lightweight/IoT devices running OHOS_ARCH_LITE, all WiFi operations are unrestricted. Any process can access WiFi scan results (location-sensitive data), modify WiFi settings, create hotspots, and read MAC addresses of nearby peers.

**Recommendation:** Implement a lightweight permission system for OHOS_ARCH_LITE rather than blanket-granting all permissions. At minimum, sensitive operations like scan info access (which reveals location data) and MAC address access should require some form of authorization.

---

## 6. HIGH: Unsafe reinterpret_cast for Type-Erased Data in TLS NAPI

**Severity:** HIGH
**File:** `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/tls/src/tlssocket_exec.cpp` (multiple lines)

**Description:**

Throughout the TLS socket NAPI bindings, `manager->GetData()` returns a `void*` which is cast to `TLSSocket*` via `reinterpret_cast` without any type verification:

```cpp
auto tlsSocket = reinterpret_cast<TLSSocket *>(manager->GetData());
```

This pattern appears 10 times in the file. If `manager->GetData()` ever returns data of the wrong type (due to a bug elsewhere, a race condition with `ExecBind` setting data, or a confused manager), undefined behavior results.

**Impact:** Type confusion bugs can lead to memory corruption, crashes, or potentially exploitable conditions. Since TLS sockets handle security-sensitive cryptographic operations, corruption could compromise encryption.

**Recommendation:** Use a typed wrapper or a tagged union pattern to store type information alongside the data pointer. At minimum, add a type tag check before the cast.

---

## 7. HIGH: TLS Socket Memory Leak on Bind Failure

**Severity:** HIGH
**File:** `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/tls/src/tlssocket_exec.cpp` (lines 230-239)

**Description:**

In `ExecBind`, a new `TLSSocket` is allocated with `new` and assigned to the manager regardless of whether `Bind` succeeds:

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
    manager->SetData(tlsSocket);  // Set regardless of bind result
    return context->errorNumber_ == TLSSOCKET_SUCCESS;
}
```

If `Bind` fails, the function returns `false` but the `TLSSocket*` is still assigned to the manager. Depending on error handling upstream, this may lead to an orphaned socket object being used in a partially initialized state, or if the manager is cleaned up without calling `ExecClose`, the `TLSSocket` leaks.

Additionally, in `ExecClose` (line 218), `delete tlsSocket` is called directly without any null check on the close callback completing successfully first -- the `Close` callback might fail, yet the object is still deleted.

**Impact:** Memory leaks and potential use-after-free. Failed bind operations leave allocated but improperly initialized socket objects.

**Recommendation:** Only assign the `TLSSocket*` to the manager if `Bind` succeeds. On failure, `delete tlsSocket` immediately. In `ExecClose`, ensure the delete only happens after confirming the close operation, and consider using `std::unique_ptr`.

---

## 8. HIGH: WebSocket volatile bool Without Atomic Guarantees

**Severity:** HIGH
**File:** `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/websocket/websocket_exec/src/websocket_exec.cpp` (line 181)

**Description:**

The `UserData` class uses `volatile bool closed_` for cross-thread state, but `volatile` does not provide atomicity or memory ordering guarantees in C++:

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
    volatile bool closed_;       // volatile is NOT sufficient for thread safety
    std::atomic_bool threadStop_; // correctly uses atomic for threadStop_
};
```

Notably, `threadStop_` correctly uses `std::atomic_bool`, but `closed_` uses `volatile` despite being accessed from multiple threads. The `IsClosed()` and `Close()` methods do use a mutex, but `closed_` is also read directly in `LwsCallbackClientWritable` via `userData->IsClosed()` while the LWS service thread runs concurrently.

**Impact:** Potential data races on the `closed_` flag. The `volatile` keyword does not prevent compiler/CPU reordering and does not guarantee visibility across cores. While the mutex partially mitigates this, the `volatile` declaration suggests a misunderstanding of the threading model.

**Recommendation:** Replace `volatile bool closed_` with `std::atomic_bool closed_` for consistency with `threadStop_`, or rely entirely on the mutex (removing `volatile`).

---

## 9. MEDIUM: Hardcoded Public DNS Servers in WiFi State Machine

**Severity:** MEDIUM
**File:** `/home/dspfac/openharmony/foundation/communication/wifi/wifi/services/wifi_standard/wifi_framework/wifi_manage/wifi_sta/sta_state_machine.cpp` (lines 48-49)

**Description:**

The WiFi state machine hardcodes Google's and Baidu's public DNS servers as fallbacks:

```cpp
#define FIRST_DNS "8.8.8.8"
#define SECOND_DNS "180.76.76.76"
```

These are used as replacement DNS servers when DHCP-provided DNS servers match certain conditions (line 2199+).

**Impact:**
1. **Privacy:** DNS queries are sent to third-party servers (Google, Baidu) without user consent or awareness.
2. **Regional concerns:** In some jurisdictions, routing DNS through foreign servers may violate data sovereignty requirements.
3. **Reliability:** If these servers are blocked (e.g., in certain network environments), DNS resolution fails.

**Recommendation:** Make fallback DNS servers configurable rather than hardcoded. Consider using the DHCP-provided DNS or a device-manufacturer-configurable system property. At minimum, document that these fallbacks exist.

---

## 10. MEDIUM: Excessive Use of Detached Threads in Networking Stack

**Severity:** MEDIUM
**Files:**
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/websocket/websocket_exec/src/websocket_exec.cpp` (line 670)
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/http/http_exec/src/http_exec.cpp` (line 188)
- `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/native/tls_socket/src/tls_socket.cpp` (line 410)
- Multiple socket_exec.cpp files

**Description:**

The networking stack uses `std::thread(...).detach()` extensively (10+ locations in netstack alone, plus WiFi). Detached threads:

1. Cannot be joined on shutdown, leading to undefined behavior if the process exits while threads are still running.
2. Make it impossible to propagate exceptions or errors from the thread back to the caller.
3. Can access dangling pointers if the owning object is destroyed while the thread is still running.

Example from WebSocket:
```cpp
std::thread serviceThread(RunService, manager);
serviceThread.detach();
```

The TLS socket `StartReadMessage()` also detaches a reading thread that captures `this`, creating a potential use-after-free if the TLSSocket is destroyed while the thread is running.

**Impact:** Resource leaks, potential crashes during shutdown, and use-after-free vulnerabilities.

**Recommendation:** Use managed thread pools, `std::jthread` (C++20), or ensure proper join-on-destruction semantics. For the TLS socket reading thread specifically, use a shared pointer to the socket or implement proper shutdown synchronization.

---

## 11. MEDIUM: HTTP Builtin Fetch SSL Disabled by Default

**Severity:** MEDIUM (overlaps with Finding #2 but specific to the builtin module)
**File:** `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/builtin/BUILD.gn` (line 47)

**Description:**

The legacy builtin fetch module explicitly defines `NO_SSL_CERTIFICATION=1`:

```gn
defines = [ "NO_SSL_CERTIFICATION=1" ]
```

Unlike the NAPI fetch/http modules where this is a conditional build flag, the builtin module **always** disables SSL verification. The corresponding code in `http_request.cpp:93` disables peer verification:

```cpp
ACE_CURL_EASY_SET_OPTION(handle.get(), CURLOPT_SSL_VERIFYPEER, 0L, responseData);
```

Note that `CURLOPT_SSL_VERIFYHOST` is not even explicitly set here, meaning it may default to 2 (verify) -- but without peer verification, hostname verification is meaningless.

**Impact:** Applications using the legacy fetch API have no SSL certificate validation.

**Recommendation:** Ship a proper CA bundle for the builtin module or migrate all callers to the NAPI fetch/http APIs that support proper certificate validation.

---

## 12. MEDIUM: DSoftBus Device Info Leaks Sensitive Identifiers

**Severity:** MEDIUM
**File:** `/home/dspfac/openharmony/foundation/communication/dsoftbus/core/authentication/src/auth_session_message.c`

**Description:**

The DSoftBus authentication session message handler exchanges extensive device information during the authentication handshake, including:

- `DEVICE_UDID` -- Unique device identifier
- `BT_MAC` / `BLE_MAC` -- Bluetooth MAC addresses
- `BR_MAC_ADDR` -- Bluetooth Radio MAC
- `P2P_MAC_ADDR` -- WiFi Direct MAC address
- `IP_MAC` -- IP-associated MAC address
- `ACCOUNT_ID` -- User account identifier
- `NETWORK_ID` -- Network identifier
- `DEVICE_NAME` -- Human-readable device name

These identifiers are exchanged during the initial authentication phase. The `AUTH_LOGD` and `AUTH_LOGI` calls may also log some of these values.

**Impact:** An attacker performing active discovery or intercepting DSoftBus authentication messages could harvest persistent device identifiers, enabling tracking across sessions and locations.

**Recommendation:** Minimize the information exchanged before authentication is complete. Use ephemeral identifiers where possible. Ensure all sensitive identifiers are logged with anonymization (some calls do use `Anonymizer` but coverage is inconsistent).

---

## 13. MEDIUM: WebSocket Logs Leak Connection Details

**Severity:** MEDIUM
**File:** `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/websocket/websocket_exec/src/websocket_exec.cpp`

**Description:**

Several log statements in the WebSocket implementation leak connection details:

```cpp
// Line 410: logs connection error details in plaintext
NETSTACK_LOGI("LwsCallbackClientConnectionError %{public}s",
              (in == nullptr) ? "null" : reinterpret_cast<char *>(in));

// Line 839: logs open status and message
NETSTACK_LOGI("OnOpen %{public}u %{public}s", status, message.c_str());

// Line 860: logs close status and reason
NETSTACK_LOGI("OnClose %{public}u %{public}s", closeStatus, closeReason.c_str());
```

Additionally, the CA path is logged at debug level (line 613):
```cpp
NETSTACK_LOGD("caPath: %{public}s", info.client_ssl_ca_filepath);
```

**Impact:** Connection error details, status messages, and certificate paths are logged and potentially accessible to other processes or via log collection, leaking server interaction details.

**Recommendation:** Use `%{private}s` for connection-specific details and error messages that might reveal server infrastructure information.

---

## 14. LOW: TLS Connect Context Silently Ignores Missing secureOptions

**Severity:** LOW
**File:** `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/tls/src/context/tls_connect_context.cpp` (lines 159-169)

**Description:**

If the `secureOptions` property is missing from the connection options, the function silently returns a default-constructed `TLSSecureOptions` object with no CA certificates, no client cert, and default verification mode:

```cpp
TLSSecureOptions TLSConnectContext::ReadTLSSecureOptions(napi_env env, napi_value *params)
{
    TLSSecureOptions secureOption;
    if (!NapiUtils::HasNamedProperty(GetEnv(), params[ARG_INDEX_0], SECURE_OPTIONS)) {
        return secureOption;  // Returns empty options silently
    }
    // ...
}
```

Similarly, if `ReadNecessaryOptions` returns false (e.g., no CA certificate provided), the partially-constructed options are still returned.

**Impact:** Developers may accidentally create TLS connections without proper security configuration and receive no warning or error.

**Recommendation:** Return an error or log a warning when `secureOptions` is missing entirely from a TLS connect call.

---

## 15. LOW: Certificate Property Name Typo

**Severity:** LOW
**File:** `/home/dspfac/openharmony/foundation/communication/netstack/frameworks/js/napi/tls/src/tlssocket_exec.cpp` (lines 35-36)

**Description:**

The constant names contain a typo -- "CERTIFICATA" instead of "CERTIFICATE":

```cpp
constexpr const char *CERTIFICATA_DATA = "data";
constexpr const char *CERTIFICATA_ENCODING_FORMAT = "encodingFormat";
```

While this does not affect functionality (the constant values are correct), it indicates careless naming that could confuse developers maintaining the code.

**Recommendation:** Rename to `CERTIFICATE_DATA` and `CERTIFICATE_ENCODING_FORMAT`.

---

## 16. LOW: Bluetooth BLE Scan Lacks Permission Check at NAPI Layer

**Severity:** LOW
**File:** `/home/dspfac/openharmony/foundation/communication/bluetooth/frameworks/js/napi/src/ble/napi_bluetooth_ble.cpp` (lines 105-131)

**Description:**

The `SysStartBLEScan` function starts BLE scanning without any visible permission check at the NAPI layer:

```cpp
napi_value SysStartBLEScan(napi_env env, napi_callback_info info)
{
    // ... argument parsing ...
    BleCentralManagerGetInstance()->StartScan(settinngs);  // No permission check
    return NapiGetNull(env);
}
```

BLE scan results can reveal nearby Bluetooth devices and their identifiers, which constitutes location-sensitive data. While permission checks may be enforced at a lower layer (in the BleCentralManager service), the NAPI layer performs no gating, meaning the permission error only surfaces asynchronously rather than being caught early.

Additionally, note the typo in variable name: `settinngs` should be `settings`.

**Recommendation:** Add an explicit permission check at the NAPI layer for immediate feedback to the caller, consistent with how the WiFi and Telephony subsystems handle permissions.

---

## 17. Positive Findings

1. **NFC Permission Enforcement:** The NFC `TagSessionStub` consistently checks `ExternalDepsProxy::GetInstance().IsGranted(OHOS::NFC::TAG_PERM)` before every IPC handler, demonstrating good permission enforcement practices.

2. **Telephony Permission System:** The `TelephonyPermission::CheckPermission` implementation properly verifies access tokens, records permission usage via `PrivacyKit::AddPermissionUsedRecord`, and logs failed permission checks.

3. **HTTP Error Handling:** The `RequestContext` class properly cleans up `curl_slist` and `curl_mime` resources in its destructor, preventing memory leaks on error paths.

4. **HTTP Request Context Threading:** The HTTP module uses mutex-protected queues (`dlLenLock_`, `ulLenLock_`, `tempDataLock_`) for thread-safe data transfer between curl worker threads and the main thread.

5. **WebSocket Send Queue:** The WebSocket `UserData::Push/Pop` operations use proper mutex synchronization for the send data queue.

6. **DSoftBus Encryption:** The DSoftBus authentication module uses AES-GCM encryption for fast authentication tokens, with proper key management through the `AuthDeviceKeyInfo` structure.

7. **HTTP DNS Validation:** The `ParseDnsServers` function in `request_context.cpp` properly validates that DNS server addresses are valid IPv4 or IPv6 before accepting them.

8. **TLS Context Configuration:** The TLS socket implementation in `tls_context.cpp` properly sets `SSL_VERIFY_PEER` for one-way mode and `SSL_VERIFY_FAIL_IF_NO_PEER_CERT` for two-way mode, providing correct certificate verification behavior for the TLS socket API (as opposed to the WebSocket and HTTP APIs).

---

## 18. Summary Table

| # | Finding | Severity | 分类 | 组件 |
|---|---------|----------|----------|-----------|
| 2 | SSL/TLS certificate validation disabled via NO_SSL_CERTIFICATION | CRITICAL | Security | netstack/http, fetch |
| 3 | WebSocket SSL verification unconditionally bypassed | CRITICAL | Security | netstack/websocket |
| 4 | WiFi permission bypass via PERMISSION_ALWAYS_GRANT | HIGH | Security | wifi |
| 5 | WiFi Lite all permissions always granted | HIGH | Security | wifi |
| 6 | Unsafe reinterpret_cast for TLS socket type-erased data | HIGH | Code Quality | netstack/tls |
| 7 | TLS Socket memory leak on bind failure | HIGH | Resource Mgmt | netstack/tls |
| 8 | WebSocket volatile bool threading issue | HIGH | Thread Safety | netstack/websocket |
| 9 | Hardcoded public DNS servers (8.8.8.8, 180.76.76.76) | MEDIUM | Privacy | wifi |
| 10 | Excessive detached thread usage | MEDIUM | Code Quality | netstack, wifi |
| 11 | HTTP builtin fetch SSL always disabled | MEDIUM | Security | netstack/fetch |
| 12 | DSoftBus auth exchanges sensitive device identifiers | MEDIUM | Privacy | dsoftbus |
| 13 | WebSocket logs leak connection details | MEDIUM | Privacy | netstack/websocket |
| 14 | TLS silently ignores missing secureOptions | LOW | Code Quality | netstack/tls |
| 15 | Certificate property name typo (CERTIFICATA) | LOW | Code Quality | netstack/tls |
| 16 | BLE scan lacks NAPI-layer permission check | LOW | Security | bluetooth |

**Total: 2 CRITICAL, 5 HIGH, 5 MEDIUM, 3 LOW**

The two CRITICAL findings around SSL/TLS certificate validation represent the most urgent issues requiring immediate remediation. The WebSocket SSL bypass is particularly concerning because it is unconditional and affects all secure WebSocket connections across the entire platform.
