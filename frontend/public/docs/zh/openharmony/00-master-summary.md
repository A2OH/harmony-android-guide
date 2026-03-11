# OpenHarmony 4.1 版本 -- 总体执行摘要

**日期：** 2026-03-10
**范围：** 涵盖14个子系统领域的完整代码库审查
**审查人：** 自动化代码审查 (Claude Opus 4.6)
**分类：** 技术领导层审查

---

## 目录

1. [执行摘要](#1-执行摘要)
2. [按严重级别分类的发现 -- 严重和高危汇总表](#2-按严重级别分类的发现--严重和高危汇总表)
3. [统计仪表板](#3-统计仪表板)
4. [关键安全发现 -- 前10项详细说明](#4-关键安全发现--前10项详细说明)
5. [应用开发者影响](#5-应用开发者影响)
6. [建议](#6-建议)
7. [正面观察](#7-正面观察)
8. [详细报告索引](#8-详细报告索引)

---

## 1. 执行摘要

OpenHarmony 4.1 代码库代表了一个大规模、架构宏大的操作系统，广泛支持从物联网设备 (LiteOS-M/A) 到功能齐全的智能手机和平板电脑（标准系统）的各种硬件。本次审查覆盖了整个技术栈：SDK API 声明、原生 NDK 头文件、ArkUI 框架、Ability/包管理、安全子系统、通信/网络、多媒体、内核/驱动、分布式数据管理、图形/窗口、ArkCompiler 运行时、系统服务、内置应用程序以及构建系统。在这14个审查领域中，共编目了**约261个不同的发现**。

OpenHarmony 4.1 的整体安全态势**喜忧参半**。该平台在多个领域展现了扎实的基础安全实践 -- 正确使用 OpenSSL/mbedTLS 而非自定义加密算法、系统化的 IPC 接口令牌验证、全面的模糊测试基础设施，以及基于 Rust 的 Asset 存储模块。然而，审查发现了**多个严重漏洞**，如不加以解决，可能会破坏这些基础。最令人担忧的模式包括：(1) 多个网络 API 中禁用了 SSL/TLS 证书验证，使 HTTPS 和 WSS 连接容易受到中间人攻击；(2) HUKS 安全子系统和示例应用中存在硬编码的加密密钥；(3) 安全控制通过构建标志条件编译的普遍模式（SELinux 检查、权限删除、签名验证、权限执行），导致脆弱的安全模型，单个构建配置错误就能禁用整个保护层；(4) 大多数系统服务在 stub 层缺乏充分的 IPC 权限执行，可能导致权限提升。

代码质量对于这种规模的项目来说总体上是可接受的，系统性地使用了智能指针、securec 安全字符串函数和一致的错误处理模式。然而，线程安全是几乎每个子系统都反复出现的问题，包括非原子全局状态、未保护的共享数据结构以及误用 `volatile` 代替 `std::atomic`。SDK API 声明存在普遍使用 `Object` 和 `any` 类型的问题，削弱了 TypeScript 的类型安全性，而内置应用程序通过 `globalThis` 滥用、`@ts-nocheck` 指令和空错误处理器等模式为第三方开发者树立了不良榜样。

---

## 2. 按严重级别分类的发现 -- 严重和高危汇总表

### 严重发现

| 编号 | 严重级别 | 子系统 | 描述 | 文件/位置 | 报告 |
|----|----------|-----------|-------------|---------------|--------|
| SEC-C01 | 严重 | 安全/HUKS | 硬编码的32字节 ECC 平台私钥编译进源代码 | `hks_chipset_platform_key_hardcoded.c:25-28` | [05-security-review.md] |
| SEC-C02 | 严重 | 安全/HUKS | 从静态字符串派生的硬编码默认认证令牌 MAC/加密密钥 | `hks_keyblob.c:616-627` | [05-security-review.md] |
| COMM-01 | 严重 | 通信 | 通过 `NO_SSL_CERTIFICATION` 构建标志禁用 SSL/TLS 证书验证；内置 fetch 总是禁用它 | `http_exec.cpp:774`, `fetch_exec.cpp:274`, `BUILD.gn:47` | [06-communication-review.md] |
| COMM-02 | 严重 | 通信 | WebSocket SSL 验证被无条件绕过 (SKIP_HOSTNAME_CHECK + ALLOW_SELFSIGNED + ALLOW_INSECURE) | `websocket_exec.cpp:573`, `websocket_client.cpp:373` | [06-communication-review.md] |
| KRN-01 | 严重 | 内核/驱动 | I2C mmap 允许用户空间映射任意物理内存地址 | `i2c_dev.c:399-417` | [08-kernel-drivers-review.md] |
| DIS-01 | 严重 | 分布式数据 | KV 存储数据库密钥的 AES-GCM 加密中静态 nonce/AAD 重用 | `security_manager.cpp:33-36` | [09-distributed-data-review.md] |
| DIS-02 | 严重 | 分布式数据 | RDB SQL 字符串构造中因未转义的表/列名导致 SQL 注入 | `rdb_store_impl.cpp:468-509`, `sqlite_sql_builder.cpp` | [09-distributed-data-review.md] |
| SYS-01 | 严重 | 系统服务 | 当 `GRAPHIC_PERMISSION_CHECK` 未定义时，Appspawn 授予所有 Linux 权能 | `appspawn_process.c:141-149` | [12-system-services-review.md] |
| SYS-02 | 严重 | 系统服务 | samgr 中的 SELinux 检查在 `WITH_SELINUX` 未定义时默认允许所有 | `system_ability_manager_stub.cpp:45-99` | [12-system-services-review.md] |
| SYS-03 | 严重 | 系统服务 | Common Event stub 没有权限检查；从 IPC 接受伪造的 UID/callerToken | `common_event_stub.cpp:32-228` | [12-system-services-review.md] |
| SYS-04 | 严重 | 系统服务 | 所有系统服务中 IPC 权限执行架构不一致 | 横切性问题 | [12-system-services-review.md] |
| BLD-01 | 严重 | 构建系统 | npm 和 ohpm 包管理器全局禁用了 SSL 验证 | `build.sh:108,125` | [14-build-system-review.md] |
| AUI-01 | 严重 | ArkUI 框架 | 调试线程阻塞命令 (`-threadstuck`) 在生产流水线中可访问 | `pipeline_context.cpp:130-133,3212-3225` | [03-arkui-framework-review.md] |
| APP-01 | 严重 | 内置应用 | PaySecurely 示例中硬编码的 RSA-1024 私钥和明文密码 | `PayServer.ts` | [13-builtin-apps-review.md] |
| APP-02 | 严重 | 内置应用 | WiFi 预共享密钥以明文形式暴露在二维码中 | `wifi.ets:793-798` | [13-builtin-apps-review.md] |
| MUL-01 | 严重 | 多媒体 | 摄像头 BeginConfig 中返回未初始化的 errCode -- 未定义行为 | `hcapture_session.cpp:162-177` | [07-multimedia-review.md] |
| MUL-02 | 严重 | 多媒体 | Root UID 绕过 CAPTURE_SCREEN 权限 | `screen_capture_server.cpp:178-199` | [07-multimedia-review.md] |
| MUL-03 | 严重 | 多媒体 | Root UID 绕过 MICROPHONE 权限 | `media_permission.cpp:31-40` | [07-multimedia-review.md] |
| MUL-04 | 严重 | 多媒体 | PulseAudio 守护线程中的 `exit(-1)` 终止整个音频服务进程 | `audio_server.cpp:74-85` | [07-multimedia-review.md] |

### 高危发现

| 编号 | 严重级别 | 子系统 | 描述 | 文件/位置 | 报告 |
|----|----------|-----------|-------------|---------------|--------|
| SEC-H01 | 高危 | 安全 | AES-ECB 模式暴露并作为默认加密类型 | `cipher_aes_openssl.c:52-65,203-206` | [05-security-review.md] |
| SEC-H02 | 高危 | 安全 | SELinux 共享内存中未进行边界检查的不安全 `memcpy` | `selinux_share_mem.c:61` | [05-security-review.md] |
| SEC-H03 | 高危 | 安全 | `VerifyAccessTokenInner` IPC 缺少权限检查，允许权限枚举 | `accesstoken_manager_stub.cpp:106-112` | [05-security-review.md] |
| SEC-H04 | 高危 | 安全 | AES-GCM IV 最小长度为1字节（应为12） | `cipher_aes_openssl.c:29-30` | [05-security-review.md] |
| COMM-03 | 高危 | 通信 | 通过 `PERMISSION_ALWAYS_GRANT` 构建标志绕过 WiFi 权限 | `wifi_auth_center.cpp:30-34` | [06-communication-review.md] |
| COMM-04 | 高危 | 通信 | WiFi Lite：所有13个权限方法无条件返回 GRANTED | `wifi_permission_utils.cpp:23-87` | [06-communication-review.md] |
| COMM-05 | 高危 | 通信 | 对类型擦除的 TLS 数据使用不安全的 `reinterpret_cast`（10处） | `tlssocket_exec.cpp` | [06-communication-review.md] |
| COMM-06 | 高危 | 通信 | TLS 套接字在绑定失败时的内存泄漏 | `tlssocket_exec.cpp:230-239` | [06-communication-review.md] |
| COMM-07 | 高危 | 通信 | WebSocket `volatile bool` 没有原子性保证的线程安全问题 | `websocket_exec.cpp:181` | [06-communication-review.md] |
| NDK-01 | 高危 | NDK | C API 头文件中使用 C++ 语法（3个严重子发现） | `neural_network_runtime_type.h`, `raw_file.h`, `native_interface_xcomponent.h` | [02-sdk-c-ndk-review.md] |
| NDK-02 | 高危 | NDK | 所有子系统中错误码策略不一致 | 多个 NDK 头文件 | [02-sdk-c-ndk-review.md] |
| NDK-03 | 高危 | NDK | ABI 冻结的暴露结构体缺乏版本控制 | `native_interface_xcomponent.h` | [02-sdk-c-ndk-review.md] |
| NDK-04 | 高危 | NDK | 可变参数 `OH_NativeWindow_NativeWindowHandleOpt` 类型不安全 | `external_window.h` | [02-sdk-c-ndk-review.md] |
| NDK-05 | 高危 | NDK | `canIUse()` -- 无前缀、无文档、易冲突的名称 | `syscap_ndk.h` | [02-sdk-c-ndk-review.md] |
| NDK-06 | 高危 | NDK | BundleManager 返回值中内存所有权不明确 | `native_interface_bundle.h` | [02-sdk-c-ndk-review.md] |
| NDK-07 | 高危 | NDK | `OH_Rdb_Config` 上的 `#pragma pack(1)` 造成 ABI 脆弱性 | `relational_store.h` | [02-sdk-c-ndk-review.md] |
| AUI-02 | 高危 | ArkUI | `RegisterFoldStatusChangedCallback` 中缺少 `callbackId` 递增 | `pipeline_context.h:433-440` | [03-arkui-framework-review.md] |
| AUI-03 | 高危 | ArkUI | NAPI 层中无安全保障的 `delete this` 模式 | `native_safe_async_work.cpp:325`, `ark_native_reference.cpp:134` | [03-arkui-framework-review.md] |
| AUI-04 | 高危 | ArkUI | Web 组件中无 URL 验证/清理 (javascript:/file:/data: URI) | `web_delegate.cpp:739-757` | [03-arkui-framework-review.md] |
| AUI-05 | 高危 | ArkUI | 生产流水线代码中的 `abort()` 调用 | `ui_task_scheduler.cpp:115`, `frame_node.cpp:215` | [03-arkui-framework-review.md] |
| AUI-06 | 高危 | ArkUI | 流水线上下文中无符号整数下溢 (`size_t = -1`) | `pipeline_context.h:991-992` | [03-arkui-framework-review.md] |
| ABL-01 | 高危 | Ability/包管理 | 线程安全问题：`LifecycleDeal::ContinueAbility` 在未加锁的情况下访问 scheduler | `lifecycle_deal.cpp:156-161` | [04-ability-bundle-review.md] |
| ABL-02 | 高危 | Ability/包管理 | 对 const 参数普遍使用 `const_cast<Want&>`（20+处） | `ability_manager_service.cpp` | [04-ability-bundle-review.md] |
| ABL-03 | 高危 | Ability/包管理 | Stub 实现无条件允许 URI 持久化权限 | `uri_permission_manager_stub_impl.cpp:182-207` | [04-ability-bundle-review.md] |
| ABL-04 | 高危 | Ability/包管理 | X86 模拟器模式完全禁用签名验证 | `bundle_install_checker.cpp:203-227` | [04-ability-bundle-review.md] |
| ABL-05 | 高危 | Ability/包管理 | URI 权限使用位或而非位与可能导致授予过多访问权 | `uri_permission_manager_stub_impl.cpp:85` | [04-ability-bundle-review.md] |
| MUL-05 | 高危 | 多媒体 | 摄像头 IPC stub 缺少按操作的权限检查（静音/预启动） | `hcamera_service_stub.cpp:45-127` | [07-multimedia-review.md] |
| MUL-06 | 高危 | 多媒体 | 全局 `g_operationMode` 在无同步的情况下读写 | `camera_util.cpp:108-109` | [07-multimedia-review.md] |
| MUL-07 | 高危 | 多媒体 | 分离线程访问 `this` -- 音频服务中的释放后使用风险 | `audio_service.cpp:81-82` | [07-multimedia-review.md] |
| MUL-08 | 高危 | 多媒体 | 音频 IPC 流缺少死亡通知接收器（已确认的 TODO） | `ipc_stream_in_server.cpp:114-118` | [07-multimedia-review.md] |
| MUL-09 | 高危 | 多媒体 | 关键任务队列函数上禁用了 CFI 消毒器 | `task_queue.cpp:96,135,147` | [07-multimedia-review.md] |
| KRN-02 | 高危 | 内核/驱动 | SELinux 权限检查被条件编译排除 (`#ifdef WITH_SELINUX`) | `devsvc_manager_stub.c:35-71` | [08-kernel-drivers-review.md] |
| KRN-03 | 高危 | 内核/驱动 | 通过 IPC 安装/移除内核模块时无调用者授权检查 | `devmgr_service_stub.c:152-223` | [08-kernel-drivers-review.md] |
| DIS-03 | 高危 | 分布式数据 | SingleStoreImpl 中的非原子引用计数器 `ref_` | `single_store_impl.h:121` | [09-distributed-data-review.md] |
| DIS-04 | 高危 | 分布式数据 | `BatchInsert` 在批处理中途失败时返回 E_OK，掩盖错误 | `rdb_store_impl.cpp:332-371` | [09-distributed-data-review.md] |
| DIS-05 | 高危 | 分布式数据 | AbsPredicates 字段名未验证，可能导致 WHERE 子句注入 | `abs_predicates.cpp` | [09-distributed-data-review.md] |
| DIS-06 | 高危 | 分布式数据 | 文件打开 API 未验证沙箱内的路径遍历 | `open.cpp` | [09-distributed-data-review.md] |
| GFX-01 | 高危 | 图形/窗口 | DoFlushBuffer 中64位到32位的使用量截断导致缓存刷新失效 | `buffer_queue.cpp:498` | [10-graphics-window-review.md] |
| GFX-02 | 高危 | 图形/窗口 | GetLastFlushedBuffer 缺少互斥锁保护 -- 数据竞争 | `buffer_queue.cpp:422-442` | [10-graphics-window-review.md] |
| GFX-03 | 高危 | 图形/窗口 | 未初始化的 `lastFlusedSequence_` -- 未定义行为 | `buffer_queue.h:190` | [10-graphics-window-review.md] |
| GFX-04 | 高危 | 图形/窗口 | WindowImpl 中的静态全局映射缺乏一致的锁保护 | `window_impl.h:596-614` | [10-graphics-window-review.md] |
| GFX-05 | 高危 | 图形/窗口 | WindowImpl::Snapshot 无权限检查 | `window_impl.cpp:713-729` | [10-graphics-window-review.md] |
| GFX-06 | 高危 | 图形/窗口 | VsyncStation::Init 在接收器创建失败时死循环 | `vsync_station.cpp:99-102` | [10-graphics-window-review.md] |
| ARC-01 | 高危 | ArkCompiler | JIT 通过 dlopen 加载 `libark_jsoptimizer.so` 而无完整性验证 | `jit/jit.cpp:35-38` | [11-arkcompiler-runtime-review.md] |
| ARC-02 | 高危 | ArkCompiler | Panda 字节码文件 (.abc) 仅使用 CRC 校验和，无加密签名 | `libpandafile/file.h:62` | [11-arkcompiler-runtime-review.md] |
| SYS-05 | 高危 | 系统服务 | Appspawn 套接字认证仅限两个 UID；无二次验证 | `appspawn_service.c:645-652` | [12-system-services-review.md] |
| SYS-06 | 高危 | 系统服务 | 对特定包名硬编码注入特权 GID（存在偏差一错误风险） | `appspawn_service.c:411-427` | [12-system-services-review.md] |
| SYS-07 | 高危 | 系统服务 | samgr CanRequest() 仅检查令牌类型，不检查特定身份 | `system_ability_manager_stub.cpp:996-1003` | [12-system-services-review.md] |
| SYS-08 | 高危 | 系统服务 | 电源管理 stub 在 stub 层无权限检查 | `power_mgr_stub.cpp:39-170` | [12-system-services-review.md] |
| SYS-09 | 高危 | 系统服务 | ANS 通知 stub：庞大的 IPC 接口无 stub 层检查 | `ans_manager_stub.cpp` | [12-system-services-review.md] |
| SYS-10 | 高危 | 系统服务 | 输入法 stub 缺乏权限执行；SetCoreAndAgent 未受保护 | `input_method_system_ability_stub.cpp` | [12-system-services-review.md] |
| SYS-11 | 高危 | 系统服务 | 输入事件注入缺乏强 stub 层权限执行 | `multimodal_input_connect_stub.cpp:177-182` | [12-system-services-review.md] |
| SYS-12 | 高危 | 系统服务 | 后台任务 stub 无权限检查；暴露跨应用停止操作 | `background_task_mgr_stub.cpp` | [12-system-services-review.md] |
| SYS-13 | 高危 | 系统服务 | 接口令牌验证是许多服务的唯一认证关卡 | 横切性问题 | [12-system-services-review.md] |
| BLD-02 | 高危 | 构建系统 | 标准系统全局禁用了 FORTIFY_SOURCE（被注释掉） | `BUILD.gn:1006-1016` | [14-build-system-review.md] |
| BLD-03 | 高危 | 构建系统 | 构建配置中硬编码签名凭据（密码"123456"） | `ohos_var.gni:303-308` | [14-build-system-review.md] |
| BLD-04 | 高危 | 构建系统 | npm 锁文件被明确禁用，破坏供应链完整性 | `build.sh:109-110` | [14-build-system-review.md] |
| APP-03 | 高危 | 内置应用 | DLP 管理器中将令牌值记录到控制台 | `HomeFeature.ets:125` | [13-builtin-apps-review.md] |
| APP-04 | 高危 | 内置应用 | 超过100个文件中将 `globalThis` 用作服务定位器 | 多个应用 | [13-builtin-apps-review.md] |
| APP-05 | 高危 | 内置应用 | 关键系统文件上使用 `@ts-nocheck`（WindowManager、SysFaultLogger） | 多个文件 | [13-builtin-apps-review.md] |
| APP-06 | 高危 | 内置应用 | SystemUI 窗口管理中静默吞没错误（15+处） | 多个 SystemUI 文件 | [13-builtin-apps-review.md] |
| SDK-01 | 高危 | SDK JS API | 公共 API 中的 `any` 类型破坏类型安全 | 5+个声明文件 | [01-sdk-js-api-review.md] |
| SDK-02 | 高危 | SDK JS API | 普遍使用 `Object` 类型（30+个文件中246+处） | 多个 `.d.ts` 文件 | [01-sdk-js-api-review.md] |

---

## 3. 统计仪表板

### 按严重级别统计发现总数

| 严重级别 | 数量 |
|----------|-------|
| **严重** | 19 |
| **高危** | 67 |
| **中危** | ~105 |
| **低危** | ~50 |
| **提示** | ~20 |
| **总计** | **~261** |

### 按子系统统计发现

```
系统服务         |################################################  | 35
通信             |#########################################         | 15
安全             |#################################                 | 13
多媒体           |####################################              | 34
内核/驱动        |##########################                       | 20
SDK JS API      |####################################              | 34
NDK C 头文件     |################################################  | 55
ArkUI 框架       |########################                         | 19
Ability/包管理   |#################################                 | 23
分布式数据       |################################                  | 23
图形/窗口        |############################                     | 22
ArkCompiler     |################                                 | 11
内置应用         |####################                             | 16
构建系统         |############################                     | 31
```

### 前5个重复出现的问题类别

| # | 类别 | 出现次数 | 受影响子系统 |
|---|----------|-------------|---------------------|
| 1 | **缺少 IPC/stub 层权限检查** | 20+个发现 | 系统服务、多媒体、Ability/包管理、内核/驱动、图形 |
| 2 | **线程安全违规**（非原子全局变量、缺少锁、数据竞争） | 25+个发现 | 除 SDK 声明外的所有子系统 |
| 3 | **构建标志禁用安全控制**（SELinux、SSL、权限、权能、FORTIFY_SOURCE） | 12+个发现 | 安全、通信、内核、系统服务、构建系统、Ability |
| 4 | **硬编码凭据/密钥**（加密密钥、签名密码、DNS 服务器、包名） | 8+个发现 | 安全、构建系统、内置应用、系统服务、通信 |
| 5 | **类型安全侵蚀**（`any`、`Object`、`reinterpret_cast`、未验证的字段名） | 15+个发现 | SDK JS API、NDK、通信、分布式数据、ArkCompiler |

---

## 4. 关键安全发现 -- 前10项详细说明

### 1. 网络栈中全局禁用 SSL/TLS 证书验证

**子系统：** 通信 (netstack)、构建系统
**发现：** COMM-01、COMM-02
**严重级别：** 严重

OpenHarmony 的网络栈有多条路径完全禁用了 SSL/TLS 证书验证。HTTP 和 Fetch API 在定义 `NO_SSL_CERTIFICATION` 时禁用验证 -- 而内置 Fetch 模块在其 BUILD.gn 中无条件定义了此标志。WebSocket 实现更为严重：它对所有 WSS 连接无条件设置 `LCCSCF_SKIP_SERVER_CERT_HOSTNAME_CHECK | LCCSCF_ALLOW_SELFSIGNED`，原生层还额外添加了 `LCCSCF_ALLOW_INSECURE`。

**攻击场景：** 在同一网络上的攻击者（咖啡店 WiFi、企业网络、ISP 层级）可以对通过受影响 API 发起的任何 HTTPS 或 WSS 连接进行中间人攻击。他们可以拦截凭据、注入恶意内容、修改 API 响应并窃取数据 -- 而应用程序始终认为连接是安全的。这影响银行应用、消息应用以及使用这些网络 API 的所有应用。

---

### 2. HUKS 安全子系统中的硬编码加密密钥

**子系统：** 安全 (HUKS)
**发现：** SEC-C01、SEC-C02
**严重级别：** 严重

硬件通用密钥库包含两个硬编码加密材料的文件。`hks_chipset_platform_key_hardcoded.c` 包含一个用于 ECDH 密钥协商的32字节 ECC 私钥。`hks_keyblob.c` 在未定义 `HKS_SUPPORT_GET_AT_KEY` 时从静态 ASCII 字符串派生认证令牌密钥。

**攻击场景：** 任何能够访问源代码（它是开源的）或编译二进制文件的人都可以提取平台私钥。然后他们可以在 ECDH 密钥交换中冒充平台、解密受派生密钥保护的数据并伪造认证令牌。这完全破坏了 HUKS 设计提供的硬件支持密钥安全模型。在 AT 密钥回退机制处于活动状态的设备上，所有 HUKS 管理密钥的用户认证访问控制都被绕过。

---

### 3. 无构建标志时 Appspawn 授予完整权能

**子系统：** 系统服务 (appspawn)
**发现：** SYS-01
**严重级别：** 严重

appspawn 中的 `SetCapabilities` 函数在未定义 `GRAPHIC_PERMISSION_CHECK` 时授予所有 Linux 权能（可继承、允许和有效集合均为 `0x3fffffffff`）。同一 ifdef 还控制 UID/GID 隔离和 seccomp 过滤。

**攻击场景：** 如果设备制造商或构建工程师遗漏了 `GRAPHIC_PERMISSION_CHECK` 定义（可能因配置错误、构建系统错误或有意的测试捷径），每个生成的应用进程都将以等同于 root 的权能运行。任何第三方应用都可以：加载内核模块、挂载文件系统、覆盖文件权限、绕过网络限制、跟踪其他进程，实际上完全控制设备。

---

### 4. 系统能力管理器在无 SELinux 时默认无访问控制

**子系统：** 系统服务 (samgr)、内核/驱动 (HDF 服务管理器)
**发现：** SYS-02、KRN-02
**严重级别：** 严重

系统能力管理器 (samgr) 和 HDF 服务管理器都通过 `#ifdef WITH_SELINUX` 保护所有访问控制。当未启用 SELinux 时，任何进程都可以：以特权名称注册服务（劫持系统服务）、移除关键系统服务（拒绝服务）、枚举所有系统能力、获取特权驱动服务的句柄。

**攻击场景：** 在没有 SELinux 的设备上（开发版本、某些物联网配置、LiteOS 变体），恶意应用可以注册系统服务的伪造版本（例如凭据管理器或支付服务）。当其他应用通过名称连接此服务时，它们连接到攻击者的实现，攻击者可以窃取凭据、修改交易或注入恶意数据。此攻击对调用方应用不可检测。

---

### 5. 分布式 KV 存储加密中的 AES-GCM Nonce 重用

**子系统：** 分布式数据管理
**发现：** DIS-01
**严重级别：** 严重

KV 存储 SecurityManager 从静态字符串常量（`HKS_BLOB_TYPE_NONCE` 和 `HKS_BLOB_TYPE_AAD`）派生其 AES-256-GCM 的 nonce 和 AAD。相同的根密钥和相同的 nonce 用于每次数据库密钥加密操作。

**攻击场景：** 在 AES-GCM 中，使用相同密钥重用 nonce 是灾难性的。能够观察到使用相同密钥和 nonce 加密的两个密文的攻击者可以将它们进行异或运算以恢复两个明文的异或值。对于短且可预测的明文（如数据库加密密钥），这可以直接恢复密钥。然后攻击者可以解密设备上的任何 KV 存储数据库。由于这些密钥用于分布式数据同步，破坏一台设备的密钥可能会暴露所有同步设备的数据。

---

### 6. 关系数据库框架中的 SQL 注入

**子系统：** 分布式数据管理 (RDB)
**发现：** DIS-02、DIS-05
**严重级别：** 严重

RDB 框架通过直接将表名、列名和字段名拼接到 SQL 字符串中来构造 SQL 语句，而不进行转义或引用。虽然值使用了参数化查询，但 SQL 的结构元素（INSERT/UPDATE/DELETE 中的表名、WHERE/ORDER BY 子句中的字段名）被原样注入。

**攻击场景：** 如果应用将用户控制的数据作为表名（例如来自深度链接参数）或列名（例如来自 JSON API 响应）传递，攻击者可以注入任意 SQL。例如，字段名 `1=1; DROP TABLE users; --` 将被直接拼接到 WHERE 子句中。虽然每个应用的数据库是沙箱化的，但这可能破坏或窃取应用自身的数据，包括存储在本地数据库中的凭据、令牌和个人信息。向其他应用暴露 RDB 操作的 DataShare 提供者尤其面临风险。

---

### 7. Common Event 服务接受伪造的调用者身份

**子系统：** 系统服务（通知/Common Event）
**发现：** SYS-03
**严重级别：** 严重

Common Event stub 的 `CES_PUBLISH_COMMON_EVENT2` 处理程序直接从 IPC 消息中读取 `uid` 和 `callerToken`，而不是从 `IPCSkeleton::GetCallingUid()` 派生。冻结/解冻操作也接受原始 UID 而不进行授权检查。

**攻击场景：** 恶意应用可以在冒充任何 UID（包括系统 UID）的同时发布系统范围的事件。它可以伪造触发其他应用操作的事件（例如伪造"网络已连接"事件、伪造"用户已登录"事件）。它可以冻结任何其他应用 UID 的事件传递，造成有针对性的拒绝服务。结合 stub 层缺乏权限检查，这使得整个事件系统中的欺骗和可用性攻击成为可能。

---

### 8. 构建系统中禁用 SSL 和锁文件的供应链攻击

**子系统：** 构建系统
**发现：** BLD-01、BLD-04
**严重级别：** 严重 + 高危

构建脚本无条件运行 `npm config set strict-ssl false` 和 `ohpm config set strict_ssl false`，禁用包下载的证书验证。它还明确禁用了 npm 锁文件 (`npm config set lockfile false`) 并将此设置持久化到 `$HOME/.npmrc`。

**攻击场景：** 在构建机器和 npm/ohpm 注册表之间网络路径上的攻击者可以执行中间人攻击，将恶意 JavaScript 包注入构建。没有锁文件，依赖版本可以浮动，意味着被入侵的或仿冒的包可以在未被检测的情况下被拉入。由于这些包在构建过程中以构建者的权限执行，攻击者获得构建机器上的代码执行能力，并可以在生成的操作系统映像中注入后门。这是一种供应链攻击，可能危及通过此流水线构建的每台设备。

---

### 9. I2C 驱动允许任意物理内存映射

**子系统：** 内核/驱动 (HDF)
**发现：** KRN-01
**严重级别：** 严重

LiteOS-A 上 I2C 设备驱动的 `mmap` 处理程序将物理地址直接映射到用户空间虚拟内存。唯一的限制是地址不能在 `SYS_MEM_BASE..SYS_MEM_END`（主内存）范围内。所有其他外设的 MMIO 空间都可访问。

**攻击场景：** I2C 设备组中的任何进程（以 0660 模式创建）都可以映射任何外设的 MMIO 寄存器：UART 控制器（拦截串行通信）、加密引擎寄存器（提取密钥）、安全熔丝寄存器（绕过安全启动）或 GPIO 控制器（控制物理输出）。这是一个经典的权限提升原语：通过映射和修改 MMIO 映射内存中的内核数据结构或直接控制安全敏感硬件，攻击者可以获得完全的设备控制权。

---

### 10. 构建系统使用"123456"签名凭据

**子系统：** 构建系统
**发现：** BLD-03
**严重级别：** 高危

构建配置 (`ohos_var.gni`) 包含密码为"123456"和共享密钥库路径的默认 HAP 签名凭据。没有构建时警告或断言来检测这些默认值在发布版本中的使用。

**攻击场景：** 如果设备制造商或应用开发者在发布版本中不小心使用了默认签名配置，他们的所有 HAP 都使用公开已知的密钥和简单密码签名。攻击者随后可以使用相同密钥重新签名修改过的 HAP，创建通过签名验证的合法应用的木马化版本。由于密钥库包含在开源存储库中，任何一方都可以提取签名密钥并生成伪造应用。

---

## 5. 应用开发者影响

在 OpenHarmony 4.1 上构建应用的第三方开发者面临本次审查发现的多项风险和阻碍点：

### 对应用开发者的安全风险

- **在某些 API 路径中，HTTPS/WSS 连接默认不安全。** 使用内置 `fetch()` API 或 WebSocket API 的开发者可能认为他们的连接是安全的，但实际上并非如此。通过这些 API 传输的敏感数据（认证令牌、用户凭据、支付信息）容易被拦截。开发者应验证他们使用的是哪条网络 API 路径，并确认 SSL 验证是否处于活动状态。

- **RDB/关系存储存在通过表名和列名的 SQL 注入向量。** 使用用户影响的表名或字段名（例如动态架构、插件系统）构造查询的开发者面临漏洞风险。在框架添加适当转义之前，开发者必须手动验证所有表名和列名是否在严格的允许列表中。

- **KV 存储加密存在密码学弱点**（nonce 重用）。依赖分布式 KV 存储进行敏感数据加密的开发者应注意，加密可能无法提供他们期望的保密性保证。

### SDK 和 API 质量影响

- SDK 声明中**246+处使用 `Object` 类型**意味着开发者在 HTTP 头、路由参数、worker 传输列表和其他常用 API 中无法获得 IDE 自动补全或类型检查。这增加了开发时间和错误风险。

- **`Permissions` 类型仅为 `string`**，不提供权限名称的编译时验证。类似 `"ohos.permission.CAMREA"` 的拼写错误可以无错误编译。

- 某些仅回调 API **缺少 Promise 重载**，迫使开发者在 async/await 更简洁的上下文中使用回调风格代码。

- **蓝牙 API 弃用频繁**（三个 API 版本中经历三次命名迭代）为维护跨 API 级别应用的开发者增加了迁移负担。

### 内置应用反模式

由于内置应用作为参考实现，开发者可能会复制有问题的模式：
- 使用 `globalThis` 进行状态管理（明确不推荐）
- 使用 `@ts-nocheck` 抑制 TypeScript 类型检查
- 使用空 `.catch()` 处理器静默错误
- 使用已弃用的 `@system.*` API
- 在示例代码中硬编码凭据
- 普遍使用 `any` 类型

### NDK/原生开发风险

- **C 头文件中的 C++ 语法**阻止了 NDK 头文件的纯 C 编译，影响需要 C 互操作性的开发者（例如 Rust FFI、Go CGo）。
- **跨子系统不一致的错误码策略**迫使开发者为每个 API 学习不同的错误处理模式。
- 多个 NDK 函数中的**内存所有权不明确**意味着开发者无法知道是否应释放返回的指针，导致内存泄漏或双重释放。

---

## 6. 建议

### 即时优先级 (P0) -- 安全关键

| # | 行动 | 解决的发现 |
|---|--------|-------------------|
| 1 | **启用所有网络 API 的 SSL 验证。** 从所有生产 BUILD.gn 文件中移除 `NO_SSL_CERTIFICATION`。从 WebSocket 代码中移除无条件的 `LCCSCF_SKIP_SERVER_CERT_HOSTNAME_CHECK` 和 `LCCSCF_ALLOW_SELFSIGNED`。 | COMM-01、COMM-02 |
| 2 | **对硬编码密钥文件添加生产构建限制。** `hks_chipset_platform_key_hardcoded.c` 和 `hks_keyblob.c` 中的默认 AT 密钥回退绝不能编译进发布映像。 | SEC-C01、SEC-C02 |
| 3 | **移除 appspawn 中的权能授予回退。** 授予所有权能的 `#else` 分支必须删除。默认始终为零权能。 | SYS-01 |
| 4 | **在 SELinux 禁用时实现回退访问控制**（samgr 和 HDF 服务管理器）。绝不默认为开放访问。 | SYS-02、KRN-02 |
| 5 | **修复 Common Event stub**，从 `IPCSkeleton::GetCallingUid()` 派生调用者身份，绝不从 IPC 消息获取。在 stub 层添加权限检查。 | SYS-03 |
| 6 | **在构建脚本中启用 npm 和 ohpm 的 SSL 验证。** 启用锁文件。为下载的工具添加校验和验证。 | BLD-01、BLD-04 |
| 7 | **移除或限制** I2C mmap 处理程序，使其仅允许已验证的 I2C 控制器 MMIO 范围。 | KRN-01 |
| 8 | **使用随机 nonce** 用于 KV 存储 SecurityManager 中的 AES-GCM 加密。将 nonce 与密文一起存储。 | DIS-01 |
| 9 | **在 RDB 框架的所有 SQL 构造路径中转义/引用表名和列名。** | DIS-02、DIS-05 |

### 短期优先级 (P1) -- 下一个版本内

| # | 行动 | 解决的发现 |
|---|--------|-------------------|
| 10 | **建立强制 IPC 权限执行框架。** 每个 IPC stub 必须在表中声明每个操作所需的权限，在分派前执行。在所有服务中采用 Account Service 的 `isSyetemApi` 标志模式。 | SYS-04、SYS-08 至 SYS-13 |
| 11 | **移除 AES-ECB 作为默认**加密模式。强制执行最小12字节的 GCM IV。 | SEC-H01、SEC-H04 |
| 12 | **在 musl libc 中实现 FORTIFY_SOURCE** 支持。全局启用 `_FORTIFY_SOURCE=2`。 | BLD-02 |
| 13 | **添加构建时警告**，当非调试版本使用默认签名凭据（"123456"）时发出告警。 | BLD-03 |
| 14 | **用适当类型替换 SDK 声明中的 `Object`**（HTTP 头 -> `Record<string, string>`、worker 传输 -> `ArrayBuffer[]`、路由参数 -> 类型化记录）。 | SDK-01、SDK-02 |
| 15 | **修复 `RegisterFoldStatusChangedCallback` 中的 callbackId 问题** -- 一行修复，对折叠设备影响重大。 | AUI-02 |
| 16 | **修复 C NDK 头文件中的 C++ 语法** -- 将 `<cstdint>` 替换为 `<stdint.h>`，移除 C++ 引用，修复 `enum : int8_t` 语法。 | NDK-01 |
| 17 | **为 SELinux 共享内存 `WriteSharedMem()` 添加边界验证。** | SEC-H02 |
| 18 | **移除多媒体权限检查中的 root UID 绕过。** 仅使用基于令牌的检查。 | MUL-02、MUL-03 |
| 19 | **将 Node.js 更新**到受支持的 LTS 版本 (20.x 或 22.x)。 | TC-01 |

### 长期优先级 (P2) -- 架构改进

| # | 行动 | 解决的发现 |
|---|--------|-------------------|
| 20 | **将所有网络 API 迁移到统一 TLS 配置**，强制证书验证，并提供开发者明确选择自签名证书的方式（例如 `allowSelfSigned: true` 参数）。 | COMM-01 至 COMM-07 |
| 21 | **分解上帝对象**（AbilityManagerService、PipelineContext）为专注的服务类。 | ABL、AUI 上帝对象相关发现 |
| 22 | **完成 ArkUI 中的 NG 管线迁移**并移除遗留组件目录。 | AUI 相关维护发现 |
| 23 | **为 .abc 字节码文件**和 .an AOT 文件实现加密签名。 | ARC-01、ARC-02 |
| 24 | **标准化所有 NDK 子系统的错误码策略**，使用通用 `OH_ErrorCode` 头文件。 | NDK-02 |
| 25 | **启用自动变量初始化** (`-ftrivial-auto-var-init=zero`) 作为默认选项，匹配 Android 13+ 基线。 | 构建系统中的 SEC-03 |
| 26 | **消除所有内置应用中的 `globalThis` 使用。** 建立并执行规范的应用架构。 | APP-04、APP-05 |
| 27 | **为令牌同步 SoftBus 通道添加消息认证。** | SEC-M04 |
| 28 | **使所有全局可变状态**成为 `std::atomic` 或受互斥锁保护的（所有子系统）。 | 所有报告中的线程安全发现 |
| 29 | **将构建时配置更改限定在构建目录范围内。** 停止修改全局 npm/ohpm 设置和 `$HOME/.npmrc`。 | DM-04、DX-02 |
| 30 | **为 OHOS_ARCH_LITE 实现轻量级权限系统**，而非全面授予所有 WiFi 权限。 | COMM-04 |

---

## 7. 正面观察

尽管发现了重大问题，审查也识别了多个优秀的工程实践领域：

### 安全架构优势

- **未使用不安全的伪随机数生成器**：安全子系统中未发现 `rand()`、`srand()` 或 `random()` 的实例。密钥生成正确使用了 `RAND_priv_bytes`。
- **HUKS 进程名注入预防**：HUKS 服务明确拒绝客户端提供的 `HKS_TAG_PROCESS_NAME` 参数，防止命名空间冒充。
- **Rust 实现的 Asset 模块**：Asset 存储服务使用 Rust 实现，为敏感数据处理提供内存安全保证。
- **全面的模糊测试基础设施**：广泛的模糊测试工具覆盖 access_token、HUKS、crypto_framework 和 device_auth。
- **密钥材料清零**：在加密操作中正确使用 `memset_s` 进行密钥清零（对称密钥、PAKE 返回密钥、环形缓存缓冲区）。
- **定位服务分层权限**：精心设计的权限模型，区分精确定位、粗略定位和后台定位访问。

### 代码质量优势

- **一致使用 securec 库**：HDF/驱动子系统系统性地使用 `memcpy_s`、`sprintf_s`、`snprintf_s` 和 `memset_s`。
- **ArkUI 中精心设计的引用计数**：`RefPtr<T>` / `WeakPtr<T>` 提供线程安全和非线程安全变体，正确的弱引用到强引用升级语义。
- **全面的 GC 验证系统**：ArkCompiler 运行时包含全面的堆验证，覆盖 GC 前、GC 后和各阶段不变量。
- **NAPI 一致的错误检查**：在所有公共 API 函数上通过 `CHECK_ENV`、`CHECK_ARG`、`NAPI_PREAMBLE` 宏进行系统化的错误验证。
- **AOT 文件的 ElfChecker**：AOT ELF 文件的结构验证，包括节头边界、字符串表完整性和对齐检查。
- **生命周期感知的 GC**：对应用生命周期事件的一流支持，在高敏感时期（动画、序列化）抑制 GC。

### 框架设计优势

- **`@ohos.base.d.ts` 堪称典范**：`Callback<T>`、`ErrorCallback<T>`、`AsyncCallback<T, E>` 和 `BusinessError<T>` 具有完善的泛型类型。
- **NFC 权限执行**：`TagSessionStub` 在每个 IPC 处理程序前一致检查权限。
- **电话子系统权限系统**：正确的访问令牌验证，通过 `PrivacyKit::AddPermissionUsedRecord` 进行权限使用跟踪。
- **Account 服务权限模式**：每个操作带 `isSyetemApi` 标志的 `messageProcMap` 是所有服务中发现的最佳 IPC 权限模式。
- **ScopeGuard 模式**：在包安装和其他子系统中实现良好的 RAII 清理保护。
- **HTTP DNS 验证**：在接受 DNS 服务器地址前正确验证 IPv4/IPv6。

### 构建系统优势

- **arm64 上启用 PAC/BTI**：指针认证码和分支目标标识提供硬件辅助的 CFI。
- **栈返回保护器**：`-fstack-protector-ret-strong` 在 arm64 上提供增强的返回地址保护。
- **PIE + RELRO + Bind Now**：标准系统默认启用标准可执行文件加固。
- **ThinLTO 启用**：链接时优化提供性能和安全双重收益（更好的边界检查内联）。

---

## 8. 详细报告索引

| # | 报告 | 范围 | 发现 |
|---|--------|-------|----------|
| 01 | [01-sdk-js-api-review.md](01-sdk-js-api-review.md) | SDK JS/TS API 声明（444个 `.d.ts`/`.d.ets` 文件） | 0个严重、7个高危、12个中危、10个低危、5个提示 |
| 02 | [02-sdk-c-ndk-review.md](02-sdk-c-ndk-review.md) | C/原生 SDK (NDK) 头文件（382个文件） | 3个严重、12个高危、22个中危、18个低危 |
| 03 | [03-arkui-framework-review.md](03-arkui-framework-review.md) | ArkUI 框架（ace_engine、napi、UI 组件） | 1个严重、5个高危、8个中危、5个低危 |
| 04 | [04-ability-bundle-review.md](04-ability-bundle-review.md) | Ability 框架和包管理 | 0个严重、5个高危、8个中危、10个低危 |
| 05 | [05-security-review.md](05-security-review.md) | 安全子系统（HUKS、access_token、crypto、useriam） | 2个严重、4个高危、7个中危、4个低危、9个提示 |
| 06 | [06-communication-review.md](06-communication-review.md) | 通信（netstack、WiFi、蓝牙、DSoftBus） | 2个严重、5个高危、5个中危、3个低危 |
| 07 | [07-multimedia-review.md](07-multimedia-review.md) | 多媒体（摄像头、音频、播放器、图像） | 4个严重、10个高危、13个中危、7个低危 |
| 08 | [08-kernel-drivers-review.md](08-kernel-drivers-review.md) | 内核和驱动（HDF、平台驱动、LiteOS） | 1个严重、2个高危、6个中危、11个低危 |
| 09 | [09-distributed-data-review.md](09-distributed-data-review.md) | 分布式数据（KV 存储、RDB、DataShare、Preferences） | 2个严重、4个高危、10个中危、5个低危、2个提示 |
| 10 | [10-graphics-window-review.md](10-graphics-window-review.md) | 图形和窗口管理 | 0个严重、5个高危、10个中危、7个低危 |
| 11 | [11-arkcompiler-runtime-review.md](11-arkcompiler-runtime-review.md) | ArkCompiler 和运行时（ets_runtime、runtime_core） | 0个严重、2个高危、5个中危、4个低危 |
| 12 | [12-system-services-review.md](12-system-services-review.md) | 系统服务（appspawn、samgr、电源、传感器、IMF 等） | 4个严重、9个高危、11个中危、6个低危、2个提示 |
| 13 | [13-builtin-apps-review.md](13-builtin-apps-review.md) | 内置应用（启动器、SystemUI、设置等） | 2个严重、4个高危、7个中危、3个低危 |
| 14 | [14-build-system-review.md](14-build-system-review.md) | 构建系统（GN/Ninja、工具链、安全标志） | 1个严重、3个高危、11个中危、8个低危、4个提示 |

---

*本总体摘要整合了 OpenHarmony 4.1 版本所有14个子系统审查的发现。每份详细报告包含完整的代码引用、复现上下文和特定于子系统的建议。本文档旨在为执行层和技术领导层审查提供参考，以确定修复工作的优先级。*
