# OpenHarmony 4.1 Release -- Master Executive Summary

**Date:** 2026-03-10
**Scope:** Full codebase review across 14 subsystem areas
**Reviewer:** Automated code review (Claude Opus 4.6)
**Classification:** Technical Leadership Review

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Findings by Severity -- Consolidated Critical and High Table](#2-findings-by-severity--consolidated-critical-and-high-table)
3. [Statistics Dashboard](#3-statistics-dashboard)
4. [Critical Security Findings -- Top 10 Detailed Writeups](#4-critical-security-findings--top-10-detailed-writeups)
5. [App Developer Impact](#5-app-developer-impact)
6. [Recommendations](#6-recommendations)
7. [Positive Observations](#7-positive-observations)
8. [Index of Detailed Reports](#8-index-of-detailed-reports)

---

## 1. Executive Summary

The OpenHarmony 4.1 codebase represents a large-scale, architecturally ambitious operating system with broad hardware support spanning IoT devices (LiteOS-M/A) through full-featured smartphones and tablets (standard system). The review covered the entire stack: SDK API declarations, native NDK headers, ArkUI framework, ability/bundle management, security subsystem, communication/networking, multimedia, kernel/drivers, distributed data management, graphics/windowing, ArkCompiler runtime, system services, built-in applications, and the build system. Across these 14 review areas, a total of **approximately 261 distinct findings** were catalogued.

The overall security posture of OpenHarmony 4.1 is **mixed**. The platform demonstrates strong foundational security practices in several areas -- proper use of OpenSSL/mbedTLS rather than custom crypto, systematic IPC interface token validation, comprehensive fuzz testing infrastructure, and a Rust-based Asset storage module. However, the review uncovered **multiple critical vulnerabilities** that could undermine these foundations if left unaddressed. The most alarming patterns are: (1) SSL/TLS certificate validation being disabled in multiple networking APIs, leaving HTTPS and WSS connections vulnerable to man-in-the-middle attacks; (2) hardcoded cryptographic keys in the HUKS security subsystem and sample applications; (3) a pervasive pattern of security controls being conditionally compiled out via build flags (SELinux checks, capability dropping, signature verification, permission enforcement), creating a fragile security model where a single build misconfiguration can disable entire protection layers; and (4) insufficient IPC permission enforcement at the stub layer across most system services, allowing potential privilege escalation.

Code quality is generally acceptable for a project of this scale, with systematic use of smart pointers, securec safe string functions, and consistent error handling patterns. However, thread safety is a recurring concern across nearly every subsystem, with non-atomic global state, unprotected shared data structures, and misuse of `volatile` instead of `std::atomic`. The SDK API declarations suffer from pervasive use of `Object` and `any` types that undermine TypeScript type safety, and the built-in applications set poor examples for third-party developers through patterns like `globalThis` abuse, `@ts-nocheck` directives, and empty error handlers.

---

## 2. Findings by Severity -- Consolidated Critical and High Table

### Critical Findings

| ID | Severity | Subsystem | Description | File/Location | Report |
|----|----------|-----------|-------------|---------------|--------|
| SEC-C01 | CRITICAL | Security/HUKS | Hardcoded 32-byte ECC platform private key compiled into source code | `hks_chipset_platform_key_hardcoded.c:25-28` | [05-security-review.md] |
| SEC-C02 | CRITICAL | Security/HUKS | Hardcoded default auth token MAC/cipher keys from static strings | `hks_keyblob.c:616-627` | [05-security-review.md] |
| COMM-01 | CRITICAL | Communication | SSL/TLS certificate validation disabled via `NO_SSL_CERTIFICATION` build flag; builtin fetch always disables it | `http_exec.cpp:774`, `fetch_exec.cpp:274`, `BUILD.gn:47` | [06-communication-review.md] |
| COMM-02 | CRITICAL | Communication | WebSocket SSL verification unconditionally bypassed (SKIP_HOSTNAME_CHECK + ALLOW_SELFSIGNED + ALLOW_INSECURE) | `websocket_exec.cpp:573`, `websocket_client.cpp:373` | [06-communication-review.md] |
| KRN-01 | CRITICAL | Kernel/Drivers | I2C mmap allows user-space to map arbitrary physical memory addresses | `i2c_dev.c:399-417` | [08-kernel-drivers-review.md] |
| DIS-01 | CRITICAL | Distributed Data | Static nonce/AAD reuse in AES-GCM encryption for KV store database keys | `security_manager.cpp:33-36` | [09-distributed-data-review.md] |
| DIS-02 | CRITICAL | Distributed Data | SQL injection via unescaped table/column names in RDB SQL string construction | `rdb_store_impl.cpp:468-509`, `sqlite_sql_builder.cpp` | [09-distributed-data-review.md] |
| SYS-01 | CRITICAL | System Services | Appspawn grants ALL Linux capabilities when `GRAPHIC_PERMISSION_CHECK` undefined | `appspawn_process.c:141-149` | [12-system-services-review.md] |
| SYS-02 | CRITICAL | System Services | SELinux checks in samgr default to allow-all when `WITH_SELINUX` undefined | `system_ability_manager_stub.cpp:45-99` | [12-system-services-review.md] |
| SYS-03 | CRITICAL | System Services | Common Event stub has no permission checks; accepts spoofed UID/callerToken from IPC | `common_event_stub.cpp:32-228` | [12-system-services-review.md] |
| SYS-04 | CRITICAL | System Services | Inconsistent IPC permission enforcement architecture across all system services | Cross-cutting | [12-system-services-review.md] |
| BLD-01 | CRITICAL | Build System | SSL verification globally disabled for npm and ohpm package managers | `build.sh:108,125` | [14-build-system-review.md] |
| AUI-01 | CRITICAL | ArkUI Framework | Debug thread-stalling command (`-threadstuck`) accessible in production pipeline | `pipeline_context.cpp:130-133,3212-3225` | [03-arkui-framework-review.md] |
| APP-01 | CRITICAL | Built-in Apps | Hardcoded RSA-1024 private keys and plaintext password in PaySecurely sample | `PayServer.ts` | [13-builtin-apps-review.md] |
| APP-02 | CRITICAL | Built-in Apps | WiFi pre-shared key exposed in plaintext QR code | `wifi.ets:793-798` | [13-builtin-apps-review.md] |
| MUL-01 | CRITICAL | Multimedia | Uninitialized errCode returned in camera BeginConfig -- undefined behavior | `hcapture_session.cpp:162-177` | [07-multimedia-review.md] |
| MUL-02 | CRITICAL | Multimedia | Root UID bypasses CAPTURE_SCREEN permission | `screen_capture_server.cpp:178-199` | [07-multimedia-review.md] |
| MUL-03 | CRITICAL | Multimedia | Root UID bypasses MICROPHONE permission | `media_permission.cpp:31-40` | [07-multimedia-review.md] |
| MUL-04 | CRITICAL | Multimedia | `exit(-1)` in PulseAudio daemon thread kills entire audio server process | `audio_server.cpp:74-85` | [07-multimedia-review.md] |

### High Findings

| ID | Severity | Subsystem | Description | File/Location | Report |
|----|----------|-----------|-------------|---------------|--------|
| SEC-H01 | HIGH | Security | AES-ECB mode exposed and is the default cipher type | `cipher_aes_openssl.c:52-65,203-206` | [05-security-review.md] |
| SEC-H02 | HIGH | Security | Unsafe `memcpy` without bounds checking in SELinux shared memory | `selinux_share_mem.c:61` | [05-security-review.md] |
| SEC-H03 | HIGH | Security | Missing permission check on `VerifyAccessTokenInner` IPC allows permission enumeration | `accesstoken_manager_stub.cpp:106-112` | [05-security-review.md] |
| SEC-H04 | HIGH | Security | AES-GCM IV minimum length of 1 byte (should be 12) | `cipher_aes_openssl.c:29-30` | [05-security-review.md] |
| COMM-03 | HIGH | Communication | WiFi permission bypass via `PERMISSION_ALWAYS_GRANT` build flag | `wifi_auth_center.cpp:30-34` | [06-communication-review.md] |
| COMM-04 | HIGH | Communication | WiFi Lite: all 13 permission methods return GRANTED unconditionally | `wifi_permission_utils.cpp:23-87` | [06-communication-review.md] |
| COMM-05 | HIGH | Communication | Unsafe `reinterpret_cast` for type-erased TLS data (10 instances) | `tlssocket_exec.cpp` | [06-communication-review.md] |
| COMM-06 | HIGH | Communication | TLS socket memory leak on bind failure | `tlssocket_exec.cpp:230-239` | [06-communication-review.md] |
| COMM-07 | HIGH | Communication | WebSocket `volatile bool` without atomic guarantees for thread safety | `websocket_exec.cpp:181` | [06-communication-review.md] |
| NDK-01 | HIGH | NDK | C++ constructs in C API headers (3 Critical sub-findings) | `neural_network_runtime_type.h`, `raw_file.h`, `native_interface_xcomponent.h` | [02-sdk-c-ndk-review.md] |
| NDK-02 | HIGH | NDK | Inconsistent error code strategies across all subsystems | Multiple NDK headers | [02-sdk-c-ndk-review.md] |
| NDK-03 | HIGH | NDK | ABI-frozen exposed structs with no versioning | `native_interface_xcomponent.h` | [02-sdk-c-ndk-review.md] |
| NDK-04 | HIGH | NDK | Variadic `OH_NativeWindow_NativeWindowHandleOpt` is type-unsafe | `external_window.h` | [02-sdk-c-ndk-review.md] |
| NDK-05 | HIGH | NDK | `canIUse()` -- no prefix, no documentation, collision-prone name | `syscap_ndk.h` | [02-sdk-c-ndk-review.md] |
| NDK-06 | HIGH | NDK | Memory ownership ambiguity in BundleManager returns | `native_interface_bundle.h` | [02-sdk-c-ndk-review.md] |
| NDK-07 | HIGH | NDK | `#pragma pack(1)` on `OH_Rdb_Config` creates ABI fragility | `relational_store.h` | [02-sdk-c-ndk-review.md] |
| AUI-02 | HIGH | ArkUI | Missing `callbackId` increment in `RegisterFoldStatusChangedCallback` | `pipeline_context.h:433-440` | [03-arkui-framework-review.md] |
| AUI-03 | HIGH | ArkUI | `delete this` pattern in NAPI layer without safeguards | `native_safe_async_work.cpp:325`, `ark_native_reference.cpp:134` | [03-arkui-framework-review.md] |
| AUI-04 | HIGH | ArkUI | No URL validation/sanitization in web component (javascript:/file:/data: URIs) | `web_delegate.cpp:739-757` | [03-arkui-framework-review.md] |
| AUI-05 | HIGH | ArkUI | `abort()` calls in production pipeline code | `ui_task_scheduler.cpp:115`, `frame_node.cpp:215` | [03-arkui-framework-review.md] |
| AUI-06 | HIGH | ArkUI | Unsigned integer underflow in pipeline context (`size_t = -1`) | `pipeline_context.h:991-992` | [03-arkui-framework-review.md] |
| ABL-01 | HIGH | Ability/Bundle | Thread-safety bug: `LifecycleDeal::ContinueAbility` accesses scheduler without lock | `lifecycle_deal.cpp:156-161` | [04-ability-bundle-review.md] |
| ABL-02 | HIGH | Ability/Bundle | Pervasive `const_cast<Want&>` on const parameters (20+ occurrences) | `ability_manager_service.cpp` | [04-ability-bundle-review.md] |
| ABL-03 | HIGH | Ability/Bundle | Stub implementations unconditionally allow URI persist permissions | `uri_permission_manager_stub_impl.cpp:182-207` | [04-ability-bundle-review.md] |
| ABL-04 | HIGH | Ability/Bundle | X86 emulator mode completely disables signature verification | `bundle_install_checker.cpp:203-227` | [04-ability-bundle-review.md] |
| ABL-05 | HIGH | Ability/Bundle | URI permission bitwise OR instead of AND may grant excessive access | `uri_permission_manager_stub_impl.cpp:85` | [04-ability-bundle-review.md] |
| MUL-05 | HIGH | Multimedia | Camera IPC stub missing per-operation permission checks (mute/prelaunch) | `hcamera_service_stub.cpp:45-127` | [07-multimedia-review.md] |
| MUL-06 | HIGH | Multimedia | Global `g_operationMode` written/read without synchronization | `camera_util.cpp:108-109` | [07-multimedia-review.md] |
| MUL-07 | HIGH | Multimedia | Detached thread accessing `this` -- use-after-free risk in audio service | `audio_service.cpp:81-82` | [07-multimedia-review.md] |
| MUL-08 | HIGH | Multimedia | Missing death recipient for audio IPC streams (acknowledged TODO) | `ipc_stream_in_server.cpp:114-118` | [07-multimedia-review.md] |
| MUL-09 | HIGH | Multimedia | CFI sanitizer disabled on critical task queue functions | `task_queue.cpp:96,135,147` | [07-multimedia-review.md] |
| KRN-02 | HIGH | Kernel/Drivers | SELinux permission checks conditionally compiled out (`#ifdef WITH_SELINUX`) | `devsvc_manager_stub.c:35-71` | [08-kernel-drivers-review.md] |
| KRN-03 | HIGH | Kernel/Drivers | Kernel module install/remove via IPC without caller authorization | `devmgr_service_stub.c:152-223` | [08-kernel-drivers-review.md] |
| DIS-03 | HIGH | Distributed Data | Non-atomic reference counter `ref_` in SingleStoreImpl | `single_store_impl.h:121` | [09-distributed-data-review.md] |
| DIS-04 | HIGH | Distributed Data | `BatchInsert` returns E_OK on mid-batch failure, masking errors | `rdb_store_impl.cpp:332-371` | [09-distributed-data-review.md] |
| DIS-05 | HIGH | Distributed Data | AbsPredicates field names unvalidated, enabling WHERE clause injection | `abs_predicates.cpp` | [09-distributed-data-review.md] |
| DIS-06 | HIGH | Distributed Data | File open API does not validate path traversal within sandbox | `open.cpp` | [09-distributed-data-review.md] |
| GFX-01 | HIGH | Graphics/Window | 64-bit to 32-bit usage truncation in DoFlushBuffer disables cache flushing | `buffer_queue.cpp:498` | [10-graphics-window-review.md] |
| GFX-02 | HIGH | Graphics/Window | GetLastFlushedBuffer missing mutex protection -- data race | `buffer_queue.cpp:422-442` | [10-graphics-window-review.md] |
| GFX-03 | HIGH | Graphics/Window | Uninitialized `lastFlusedSequence_` -- undefined behavior | `buffer_queue.h:190` | [10-graphics-window-review.md] |
| GFX-04 | HIGH | Graphics/Window | Static global maps in WindowImpl without consistent lock protection | `window_impl.h:596-614` | [10-graphics-window-review.md] |
| GFX-05 | HIGH | Graphics/Window | WindowImpl::Snapshot has no permission check | `window_impl.cpp:713-729` | [10-graphics-window-review.md] |
| GFX-06 | HIGH | Graphics/Window | VsyncStation::Init infinite loop on receiver creation failure | `vsync_station.cpp:99-102` | [10-graphics-window-review.md] |
| ARC-01 | HIGH | ArkCompiler | JIT loads `libark_jsoptimizer.so` via dlopen with no integrity verification | `jit/jit.cpp:35-38` | [11-arkcompiler-runtime-review.md] |
| ARC-02 | HIGH | ArkCompiler | Panda bytecode files (.abc) use only CRC checksum, no cryptographic signing | `libpandafile/file.h:62` | [11-arkcompiler-runtime-review.md] |
| SYS-05 | HIGH | System Services | Appspawn socket auth limited to two UIDs; no secondary verification | `appspawn_service.c:645-652` | [12-system-services-review.md] |
| SYS-06 | HIGH | System Services | Hardcoded privileged GID injection for specific bundle names (off-by-one risk) | `appspawn_service.c:411-427` | [12-system-services-review.md] |
| SYS-07 | HIGH | System Services | samgr CanRequest() only checks token type, not specific identity | `system_ability_manager_stub.cpp:996-1003` | [12-system-services-review.md] |
| SYS-08 | HIGH | System Services | Power management stub has no permission checks at stub layer | `power_mgr_stub.cpp:39-170` | [12-system-services-review.md] |
| SYS-09 | HIGH | System Services | ANS notification stub: massive IPC surface without stub-level checks | `ans_manager_stub.cpp` | [12-system-services-review.md] |
| SYS-10 | HIGH | System Services | Input method stub lacks permission enforcement; SetCoreAndAgent unprotected | `input_method_system_ability_stub.cpp` | [12-system-services-review.md] |
| SYS-11 | HIGH | System Services | Input event injection without strong stub-layer permission enforcement | `multimodal_input_connect_stub.cpp:177-182` | [12-system-services-review.md] |
| SYS-12 | HIGH | System Services | Background task stub has no permission checks; cross-app stop exposed | `background_task_mgr_stub.cpp` | [12-system-services-review.md] |
| SYS-13 | HIGH | System Services | Interface token validation is sole authentication gate for many services | Cross-cutting | [12-system-services-review.md] |
| BLD-02 | HIGH | Build System | FORTIFY_SOURCE globally disabled (commented out) for standard system | `BUILD.gn:1006-1016` | [14-build-system-review.md] |
| BLD-03 | HIGH | Build System | Hardcoded signing credentials (password "123456") in build config | `ohos_var.gni:303-308` | [14-build-system-review.md] |
| BLD-04 | HIGH | Build System | npm lockfile explicitly disabled, undermining supply chain integrity | `build.sh:109-110` | [14-build-system-review.md] |
| APP-03 | HIGH | Built-in Apps | Token values logged to console in DLP Manager | `HomeFeature.ets:125` | [13-builtin-apps-review.md] |
| APP-04 | HIGH | Built-in Apps | `globalThis` used as service locator across 100+ files | Multiple apps | [13-builtin-apps-review.md] |
| APP-05 | HIGH | Built-in Apps | `@ts-nocheck` on critical system files (WindowManager, SysFaultLogger) | Multiple files | [13-builtin-apps-review.md] |
| APP-06 | HIGH | Built-in Apps | Silently swallowed errors in SystemUI window management (15+ locations) | Multiple SystemUI files | [13-builtin-apps-review.md] |
| SDK-01 | HIGH | SDK JS API | `any` type in public APIs defeating type safety | 5+ declaration files | [01-sdk-js-api-review.md] |
| SDK-02 | HIGH | SDK JS API | Pervasive `Object` type usage (246+ occurrences across 30+ files) | Multiple `.d.ts` files | [01-sdk-js-api-review.md] |

---

## 3. Statistics Dashboard

### Total Findings by Severity

| Severity | Count |
|----------|-------|
| **CRITICAL** | 19 |
| **HIGH** | 67 |
| **MEDIUM** | ~105 |
| **LOW** | ~50 |
| **INFO** | ~20 |
| **TOTAL** | **~261** |

### Findings by Subsystem

```
System Services     |################################################  | 35
Communication       |#########################################         | 15
Security            |#################################                 | 13
Multimedia          |####################################              | 34
Kernel/Drivers      |##########################                       | 20
SDK JS API          |####################################              | 34
NDK C Headers       |################################################  | 55
ArkUI Framework     |########################                         | 19
Ability/Bundle      |#################################                 | 23
Distributed Data    |################################                  | 23
Graphics/Window     |############################                     | 22
ArkCompiler         |################                                 | 11
Built-in Apps       |####################                             | 16
Build System        |############################                     | 31
```

### Top 5 Recurring Issue Categories

| # | Category | Occurrences | Subsystems Affected |
|---|----------|-------------|---------------------|
| 1 | **Missing IPC/stub-layer permission checks** | 20+ findings | System Services, Multimedia, Ability/Bundle, Kernel/Drivers, Graphics |
| 2 | **Thread safety violations** (non-atomic globals, missing locks, data races) | 25+ findings | All subsystems except SDK declarations |
| 3 | **Security controls disabled by build flags** (SELinux, SSL, permissions, capabilities, FORTIFY_SOURCE) | 12+ findings | Security, Communication, Kernel, System Services, Build System, Ability |
| 4 | **Hardcoded credentials/keys** (crypto keys, signing passwords, DNS servers, bundle names) | 8+ findings | Security, Build System, Built-in Apps, System Services, Communication |
| 5 | **Type safety erosion** (`any`, `Object`, `reinterpret_cast`, unvalidated field names) | 15+ findings | SDK JS API, NDK, Communication, Distributed Data, ArkCompiler |

---

## 4. Critical Security Findings -- Top 10 Detailed Writeups

### 1. SSL/TLS Certificate Validation Globally Disabled in Networking Stack

**Subsystems:** Communication (netstack), Build System
**Findings:** COMM-01, COMM-02
**Severity:** CRITICAL

OpenHarmony's networking stack has multiple paths where SSL/TLS certificate verification is completely disabled. The HTTP and Fetch APIs disable verification when `NO_SSL_CERTIFICATION` is defined -- and the builtin Fetch module defines this flag unconditionally in its BUILD.gn. The WebSocket implementation is worse: it unconditionally sets `LCCSCF_SKIP_SERVER_CERT_HOSTNAME_CHECK | LCCSCF_ALLOW_SELFSIGNED` for all WSS connections, with the native layer additionally adding `LCCSCF_ALLOW_INSECURE`.

**Attack Scenario:** An attacker on the same network (coffee shop WiFi, corporate network, ISP-level) can perform a man-in-the-middle attack on any HTTPS or WSS connection made through the affected APIs. They can intercept credentials, inject malicious content, modify API responses, and exfiltrate data -- all while the application believes it has a secure connection. This affects banking apps, messaging apps, and any app using these networking APIs.

---

### 2. Hardcoded Cryptographic Keys in HUKS Security Subsystem

**Subsystem:** Security (HUKS)
**Findings:** SEC-C01, SEC-C02
**Severity:** CRITICAL

The Hardware Universal Key Store contains two files with hardcoded cryptographic material. `hks_chipset_platform_key_hardcoded.c` contains a 32-byte ECC private key used for ECDH key agreement. `hks_keyblob.c` derives auth token keys from static ASCII strings when `HKS_SUPPORT_GET_AT_KEY` is not defined.

**Attack Scenario:** Any party with access to the source code (it is open source) or the compiled binary can extract the platform private key. They can then impersonate the platform in ECDH key exchanges, decrypt data protected by derived keys, and forge authentication tokens. This completely undermines the hardware-backed key security model that HUKS is designed to provide. On devices where the AT key fallback is active, user authentication access control for all HUKS-managed keys is bypassed.

---

### 3. Appspawn Grants Full Capabilities Without Build Flag

**Subsystem:** System Services (appspawn)
**Finding:** SYS-01
**Severity:** CRITICAL

The `SetCapabilities` function in appspawn grants ALL Linux capabilities (`0x3fffffffff` for inheritable, permitted, and effective sets) when `GRAPHIC_PERMISSION_CHECK` is not defined. The same ifdef also controls UID/GID isolation and seccomp filtering.

**Attack Scenario:** If a device manufacturer or build engineer omits the `GRAPHIC_PERMISSION_CHECK` define (which could happen due to misconfiguration, build system errors, or intentional testing shortcuts), every spawned application process runs with root-equivalent capabilities. Any third-party app could then: load kernel modules, mount filesystems, override file permissions, bypass network restrictions, trace other processes, and effectively have complete control over the device.

---

### 4. System Ability Manager Defaults to No Access Control Without SELinux

**Subsystems:** System Services (samgr), Kernel/Drivers (HDF service manager)
**Findings:** SYS-02, KRN-02
**Severity:** CRITICAL

Both the system ability manager (samgr) and the HDF service manager guard all access control with `#ifdef WITH_SELINUX`. When SELinux is not enabled, any process can: register services under privileged names (hijacking system services), remove critical system services (denial of service), enumerate all system abilities, and obtain handles to privileged driver services.

**Attack Scenario:** On devices without SELinux (development builds, certain IoT configurations, LiteOS variants), a malicious application can register a fake version of a system service (e.g., the credential manager or payment service). When other apps connect to this service by name, they connect to the attacker's implementation, which can steal credentials, modify transactions, or inject malicious data. The attack is undetectable to the calling application.

---

### 5. AES-GCM Nonce Reuse in Distributed KV Store Encryption

**Subsystem:** Distributed Data Management
**Finding:** DIS-01
**Severity:** CRITICAL

The KV store SecurityManager derives its AES-256-GCM nonce and AAD from static string constants (`HKS_BLOB_TYPE_NONCE` and `HKS_BLOB_TYPE_AAD`). The same root key and same nonce are used for every database key encryption operation.

**Attack Scenario:** In AES-GCM, nonce reuse with the same key is catastrophic. An attacker who can observe two ciphertexts encrypted with the same key and nonce can XOR them to recover the XOR of the two plaintexts. For short, predictable plaintext (like database encryption keys), this enables direct key recovery. The attacker can then decrypt any KV store database on the device. Since these keys are used for distributed data synchronization, compromising one device's keys could expose data from all synchronized devices.

---

### 6. SQL Injection in Relational Database Framework

**Subsystem:** Distributed Data Management (RDB)
**Findings:** DIS-02, DIS-05
**Severity:** CRITICAL

The RDB framework constructs SQL statements by directly concatenating table names, column names, and field names into SQL strings without escaping or quoting. While values use parameterized queries, the structural elements of the SQL (table names in INSERT/UPDATE/DELETE, field names in WHERE/ORDER BY clauses) are injected raw.

**Attack Scenario:** If an application passes user-controlled data as a table name (e.g., from a deep link parameter) or column name (e.g., from a JSON API response), an attacker can inject arbitrary SQL. For example, a field name of `1=1; DROP TABLE users; --` would be concatenated directly into the WHERE clause. While each app's database is sandboxed, this could corrupt or exfiltrate the app's own data, including credentials, tokens, and personal information stored in the local database. DataShare providers that expose RDB operations to other apps are particularly at risk.

---

### 7. Common Event Service Accepts Spoofed Caller Identity

**Subsystem:** System Services (Notification/Common Event)
**Finding:** SYS-03
**Severity:** CRITICAL

The Common Event stub's `CES_PUBLISH_COMMON_EVENT2` handler reads `uid` and `callerToken` directly from the IPC message without deriving them from `IPCSkeleton::GetCallingUid()`. The Freeze/Unfreeze operations also accept raw UIDs without authorization checks.

**Attack Scenario:** A malicious application can publish system-wide events while impersonating any UID, including system UIDs. It can spoof events that trigger actions in other apps (e.g., fake "network connected" events, fake "user logged in" events). It can freeze event delivery for any other application's UID, causing a targeted denial of service. Combined with the lack of stub-layer permission checks, this enables both spoofing and availability attacks across the entire event system.

---

### 8. Supply Chain Attack via Disabled SSL and Lockfiles in Build System

**Subsystem:** Build System
**Findings:** BLD-01, BLD-04
**Severity:** CRITICAL + HIGH

The build script unconditionally runs `npm config set strict-ssl false` and `ohpm config set strict_ssl false`, disabling certificate verification for package downloads. It also explicitly disables npm lockfiles (`npm config set lockfile false`) and persists this setting to `$HOME/.npmrc`.

**Attack Scenario:** An attacker on the network path between the build machine and the npm/ohpm registries can perform a man-in-the-middle attack to inject malicious JavaScript packages into the build. Without lockfiles, dependency versions can float, meaning a compromised or typosquatted package can be pulled in without detection. Since these packages execute during the build process with the builder's privileges, the attacker gains code execution on the build machine and can inject backdoors into the produced OS images. This is a supply chain attack that could compromise every device built through this pipeline.

---

### 9. I2C Driver Allows Arbitrary Physical Memory Mapping

**Subsystem:** Kernel/Drivers (HDF)
**Finding:** KRN-01
**Severity:** CRITICAL

The I2C device driver's `mmap` handler on LiteOS-A maps physical addresses directly into user-space virtual memory. The only restriction is that the address must not be within `SYS_MEM_BASE..SYS_MEM_END` (main RAM). All MMIO space for other peripherals is accessible.

**Attack Scenario:** Any process in the I2C device group (created with mode 0660) can map MMIO registers for any peripheral: UART controllers (to intercept serial communication), crypto engine registers (to extract keys), security fuse registers (to bypass secure boot), or GPIO controllers (to control physical outputs). This is a classic privilege escalation primitive: by mapping and modifying kernel data structures in MMIO-mapped memory or by directly controlling security-sensitive hardware, an attacker can gain full device control.

---

### 10. Build System Ships with "123456" Signing Credentials

**Subsystem:** Build System
**Finding:** BLD-03
**Severity:** HIGH

The build configuration (`ohos_var.gni`) contains default HAP signing credentials with the password "123456" and a shared keystore path. There are no build-time warnings or assertions to detect when these defaults are used in release builds.

**Attack Scenario:** If a device manufacturer or app developer inadvertently uses the default signing configuration for a release build, all their HAPs are signed with a publicly known key and trivial password. An attacker can then re-sign modified HAPs with the same key, creating trojanized versions of legitimate applications that pass signature verification. Since the keystore is included in the open-source repository, any party can extract the signing key and produce forged applications.

---

## 5. App Developer Impact

Third-party developers building applications on OpenHarmony 4.1 face several risks and friction points stemming from the findings in this review:

### Security Risks to App Developers

- **HTTPS/WSS connections are insecure by default** in certain API paths. Developers using the builtin `fetch()` API or the WebSocket API may believe their connections are secure when they are not. Sensitive data (auth tokens, user credentials, payment info) transmitted through these APIs is vulnerable to interception. Developers should verify which networking API path they are using and confirm SSL validation is active.

- **The RDB/relational store has SQL injection vectors** through table and column names. Developers who construct queries with user-influenced table or field names (e.g., dynamic schema, plugin systems) are vulnerable. Until the framework adds proper escaping, developers must manually validate all table and column names against a strict allowlist.

- **KV Store encryption has cryptographic weaknesses** (nonce reuse). Developers relying on the distributed KV store for sensitive data encryption should be aware that the encryption may not provide the confidentiality guarantees they expect.

### SDK and API Quality Impact

- **246+ uses of `Object` type** in the SDK declarations mean developers get no IDE autocompletion or type checking for HTTP headers, router params, worker transfer lists, and other common APIs. This increases development time and bug risk.

- **The `Permissions` type is just `string`**, providing no compile-time validation of permission names. A typo like `"ohos.permission.CAMREA"` compiles without error.

- **Missing Promise overloads** for some callback-only APIs forces developers to use callback-style code in contexts where async/await would be cleaner.

- **Bluetooth API deprecation churn** (three naming iterations across three API versions) creates migration burden for developers maintaining apps across API levels.

### Built-in App Anti-Patterns

Since built-in apps serve as reference implementations, developers may copy problematic patterns:
- Using `globalThis` for state management (explicitly discouraged)
- Suppressing TypeScript type checking with `@ts-nocheck`
- Silencing errors with empty `.catch()` handlers
- Using deprecated `@system.*` APIs
- Hardcoding credentials in sample code
- Using `any` type pervasively

### NDK/Native Development Risks

- **C++ constructs in C headers** prevent pure C compilation of NDK headers, affecting developers who need C interoperability (e.g., Rust FFI, Go CGo).
- **Inconsistent error code strategies** across subsystems force developers to learn different error handling patterns for each API.
- **Memory ownership ambiguity** in several NDK functions means developers cannot know whether to free returned pointers, leading to either leaks or double-frees.

---

## 6. Recommendations

### Immediate Priority (P0) -- Security Critical

| # | Action | Findings Addressed |
|---|--------|-------------------|
| 1 | **Enable SSL verification** for all networking APIs. Remove `NO_SSL_CERTIFICATION` from all production BUILD.gn files. Remove unconditional `LCCSCF_SKIP_SERVER_CERT_HOSTNAME_CHECK` and `LCCSCF_ALLOW_SELFSIGNED` from WebSocket code. | COMM-01, COMM-02 |
| 2 | **Gate hardcoded key files** with `#error` for production builds. `hks_chipset_platform_key_hardcoded.c` and the default AT key fallback in `hks_keyblob.c` must never compile into release images. | SEC-C01, SEC-C02 |
| 3 | **Remove the capability grant fallback** in appspawn. The `#else` branch granting all capabilities must be deleted. Default to zero capabilities always. | SYS-01 |
| 4 | **Implement fallback access control** in samgr and HDF service manager when SELinux is disabled. Never default to open access. | SYS-02, KRN-02 |
| 5 | **Fix the Common Event stub** to derive caller identity from `IPCSkeleton::GetCallingUid()`, never from the IPC message. Add permission checks at the stub layer. | SYS-03 |
| 6 | **Enable SSL verification** for npm and ohpm in build scripts. Enable lockfiles. Add checksum verification for downloaded tools. | BLD-01, BLD-04 |
| 7 | **Remove or restrict** the I2C mmap handler to validated I2C controller MMIO ranges only. | KRN-01 |
| 8 | **Use random nonces** for AES-GCM encryption in the KV store SecurityManager. Store nonces alongside ciphertext. | DIS-01 |
| 9 | **Escape/quote table and column names** in all SQL construction paths in the RDB framework. | DIS-02, DIS-05 |

### Short-Term Priority (P1) -- Within Next Release

| # | Action | Findings Addressed |
|---|--------|-------------------|
| 10 | **Establish mandatory IPC permission enforcement framework.** Every IPC stub must declare required permissions per-operation in a table, enforced before dispatch. Adopt the Account Service's `isSyetemApi` flag pattern across all services. | SYS-04, SYS-08 through SYS-13 |
| 11 | **Remove AES-ECB as default** cipher mode. Enforce minimum 12-byte GCM IVs. | SEC-H01, SEC-H04 |
| 12 | **Implement FORTIFY_SOURCE** support in musl libc. Enable `_FORTIFY_SOURCE=2` globally. | BLD-02 |
| 13 | **Add build-time warnings** when default signing credentials ("123456") are used in non-debug builds. | BLD-03 |
| 14 | **Replace `Object` with proper types** in SDK declarations (HTTP headers -> `Record<string, string>`, worker transfer -> `ArrayBuffer[]`, router params -> typed records). | SDK-01, SDK-02 |
| 15 | **Fix the callbackId bug** in `RegisterFoldStatusChangedCallback` -- a one-line fix with high impact on foldable devices. | AUI-02 |
| 16 | **Fix C++ constructs in C NDK headers** -- replace `<cstdint>` with `<stdint.h>`, remove C++ references, fix `enum : int8_t` syntax. | NDK-01 |
| 17 | **Add bounds validation** to SELinux shared memory `WriteSharedMem()`. | SEC-H02 |
| 18 | **Remove root UID bypasses** in multimedia permission checks. Use token-based checks exclusively. | MUL-02, MUL-03 |
| 19 | **Update Node.js** to a supported LTS version (20.x or 22.x). | TC-01 |

### Long-Term Priority (P2) -- Architectural Improvements

| # | Action | Findings Addressed |
|---|--------|-------------------|
| 20 | **Migrate all networking APIs to a unified TLS configuration** with mandatory certificate validation and an explicit developer opt-in for self-signed certificates (e.g., `allowSelfSigned: true` parameter). | COMM-01 through COMM-07 |
| 21 | **Decompose god objects** (AbilityManagerService, PipelineContext) into focused service classes. | ABL, AUI god object findings |
| 22 | **Complete the NG pipeline migration** in ArkUI and remove legacy component directories. | AUI-related maintenance findings |
| 23 | **Implement cryptographic signing for .abc bytecode files** and .an AOT files. | ARC-01, ARC-02 |
| 24 | **Standardize error code strategies** across all NDK subsystems with a common `OH_ErrorCode` header. | NDK-02 |
| 25 | **Enable auto variable initialization** (`-ftrivial-auto-var-init=zero`) by default, matching Android 13+ baseline. | SEC-03 in build system |
| 26 | **Eliminate `globalThis` usage** in all built-in apps. Establish and enforce a canonical app architecture. | APP-04, APP-05 |
| 27 | **Add message authentication** to the token sync SoftBus channel. | SEC-M04 |
| 28 | **Make all global mutable state** either `std::atomic` or mutex-protected across all subsystems. | Thread safety findings across all reports |
| 29 | **Scope build-time configuration changes** to the build directory. Stop modifying global npm/ohpm settings and `$HOME/.npmrc`. | DM-04, DX-02 |
| 30 | **Implement a lightweight permission system** for OHOS_ARCH_LITE rather than blanket-granting all WiFi permissions. | COMM-04 |

---

## 7. Positive Observations

Despite the significant findings, the review identified numerous areas of strong engineering practice:

### Security Architecture Strengths

- **No use of insecure PRNG**: No instances of `rand()`, `srand()`, or `random()` were found in the security subsystem. `RAND_priv_bytes` is correctly used for key generation.
- **HUKS process name injection prevention**: The HUKS service explicitly rejects client-supplied `HKS_TAG_PROCESS_NAME` parameters, preventing namespace impersonation.
- **Asset module in Rust**: The Asset storage service is implemented in Rust, providing memory safety guarantees for secret data handling.
- **Comprehensive fuzz testing infrastructure**: Extensive fuzz test harnesses cover access_token, HUKS, crypto_framework, and device_auth.
- **Key material zeroization**: Proper use of `memset_s` for key zeroization in crypto operations (symmetric keys, PAKE return keys, ring cache buffers).
- **Location service tiered permissions**: Well-structured permission model distinguishing precise, coarse, and background location access.

### Code Quality Strengths

- **Consistent use of securec library**: The HDF/driver subsystem systematically uses `memcpy_s`, `sprintf_s`, `snprintf_s`, and `memset_s`.
- **Well-designed reference counting** in ArkUI: `RefPtr<T>` / `WeakPtr<T>` with thread-safe and unsafe variants, proper weak-to-strong upgrade semantics.
- **Comprehensive GC verification system**: The ArkCompiler runtime includes thorough heap verification covering pre-GC, post-GC, and per-phase invariants.
- **NAPI consistent error checking**: Systematic error validation via `CHECK_ENV`, `CHECK_ARG`, `NAPI_PREAMBLE` macros on all public API functions.
- **ElfChecker for AOT files**: Structural validation of AOT ELF files including section header bounds, string table integrity, and alignment checks.
- **Lifecycle-aware GC**: First-class support for app lifecycle events with GC suppression during high-sensitivity periods (animations, serialization).

### Framework Design Strengths

- **`@ohos.base.d.ts` is exemplary**: `Callback<T>`, `ErrorCallback<T>`, `AsyncCallback<T, E>`, and `BusinessError<T>` are well-typed with proper generics.
- **NFC permission enforcement**: `TagSessionStub` consistently checks permissions before every IPC handler.
- **Telephony permission system**: Proper access token verification with `PrivacyKit::AddPermissionUsedRecord` for permission usage tracking.
- **Account service permission pattern**: The `messageProcMap` with `isSyetemApi` flag per-operation is the best IPC permission pattern found across all services.
- **ScopeGuard pattern**: Well-implemented RAII cleanup guards in bundle installation and other subsystems.
- **HTTP DNS validation**: Proper IPv4/IPv6 validation before accepting DNS server addresses.

### Build System Strengths

- **PAC/BTI enabled on arm64**: Pointer Authentication Codes and Branch Target Identification provide hardware-assisted CFI.
- **Stack return protector**: `-fstack-protector-ret-strong` provides enhanced return address protection on arm64.
- **PIE + RELRO + Bind Now**: Standard executable hardening is enabled by default for the standard system.
- **ThinLTO enabled**: Link-time optimization provides both performance and security (better inlining for bounds checks).

---

## 8. Index of Detailed Reports

| # | Report | Scope | Findings |
|---|--------|-------|----------|
| 01 | [01-sdk-js-api-review.md](01-sdk-js-api-review.md) | SDK JS/TS API declarations (444 `.d.ts`/`.d.ets` files) | 0 Critical, 7 High, 12 Medium, 10 Low, 5 Info |
| 02 | [02-sdk-c-ndk-review.md](02-sdk-c-ndk-review.md) | C/Native SDK (NDK) headers (382 files) | 3 Critical, 12 High, 22 Medium, 18 Low |
| 03 | [03-arkui-framework-review.md](03-arkui-framework-review.md) | ArkUI Framework (ace_engine, napi, UI components) | 1 Critical, 5 High, 8 Medium, 5 Low |
| 04 | [04-ability-bundle-review.md](04-ability-bundle-review.md) | Ability Framework and Bundle Management | 0 Critical, 5 High, 8 Medium, 10 Low |
| 05 | [05-security-review.md](05-security-review.md) | Security subsystem (HUKS, access_token, crypto, useriam) | 2 Critical, 4 High, 7 Medium, 4 Low, 9 Info |
| 06 | [06-communication-review.md](06-communication-review.md) | Communication (netstack, WiFi, Bluetooth, DSoftBus) | 2 Critical, 5 High, 5 Medium, 3 Low |
| 07 | [07-multimedia-review.md](07-multimedia-review.md) | Multimedia (camera, audio, player, image) | 4 Critical, 10 High, 13 Medium, 7 Low |
| 08 | [08-kernel-drivers-review.md](08-kernel-drivers-review.md) | Kernel and Drivers (HDF, platform drivers, LiteOS) | 1 Critical, 2 High, 6 Medium, 11 Low |
| 09 | [09-distributed-data-review.md](09-distributed-data-review.md) | Distributed Data (KV Store, RDB, DataShare, Preferences) | 2 Critical, 4 High, 10 Medium, 5 Low, 2 Info |
| 10 | [10-graphics-window-review.md](10-graphics-window-review.md) | Graphics and Window Management | 0 Critical, 5 High, 10 Medium, 7 Low |
| 11 | [11-arkcompiler-runtime-review.md](11-arkcompiler-runtime-review.md) | ArkCompiler and Runtime (ets_runtime, runtime_core) | 0 Critical, 2 High, 5 Medium, 4 Low |
| 12 | [12-system-services-review.md](12-system-services-review.md) | System Services (appspawn, samgr, power, sensor, IMF, etc.) | 4 Critical, 9 High, 11 Medium, 6 Low, 2 Info |
| 13 | [13-builtin-apps-review.md](13-builtin-apps-review.md) | Built-in Applications (Launcher, SystemUI, Settings, etc.) | 2 Critical, 4 High, 7 Medium, 3 Low |
| 14 | [14-build-system-review.md](14-build-system-review.md) | Build System (GN/Ninja, toolchain, security flags) | 1 Critical, 3 High, 11 Medium, 8 Low, 4 Info |

---

*This master summary consolidates findings from all 14 subsystem reviews of OpenHarmony 4.1 Release. Each detailed report contains full code references, reproduction context, and subsystem-specific recommendations. This document is intended for executive and technical leadership review to prioritize remediation efforts.*
