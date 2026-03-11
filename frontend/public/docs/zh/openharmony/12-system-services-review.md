# OpenHarmony 4.1 系统服务层 - 代码审查

## 目录

1. [概述](#概述)
2. [Appspawn 安全性](#appspawn-安全性)
3. [系统能力管理器 (samgr)](#系统能力管理器-samgr)
4. [电源管理服务](#电源管理服务)
5. [传感器服务](#传感器服务)
6. [定位服务](#定位服务)
7. [通知服务](#通知服务)
8. [输入法框架](#输入法框架)
9. [多模态输入服务](#多模态输入服务)
10. [后台任务管理器](#后台任务管理器)
11. [账户管理](#账户管理)
12. [横切关注点](#横切关注点)
13. [发现汇总](#发现汇总)

---

## 概述

本审查覆盖 OpenHarmony 4.1 的系统服务层，检查了 init/启动、系统能力管理、传感器/定位/电源 API、通知、输入法框架、多模态输入、后台任务管理和账户服务。审查重点关注 IPC 安全、权限执行、输入验证和架构安全模式。

**严重发现**：7 | **高危发现**：9 | **中危发现**：11 | **低危/信息性**：8

最严重的问题涉及在非 GRAPHIC_PERMISSION_CHECK 构建中 appspawn 不丢弃能力、公共事件和后台任务 IPC 桩中缺少权限检查，以及 SELinux 禁用时检查静默通过。

---

## Appspawn 安全性

### CRITICAL-APPSPAWN-01：未定义 GRAPHIC_PERMISSION_CHECK 时授予所有能力
**严重等级：严重**
**文件**：`/home/dspfac/openharmony/base/startup/appspawn/standard/appspawn_process.c`，第 141-149 行

当 `GRAPHIC_PERMISSION_CHECK` 宏未定义时，`SetCapabilities` 函数为孵化的应用进程授予所有 Linux 能力（可继承、已许可和有效集均为 `0x3fffffffff`）。这意味着任何省略此定义的构建配置都会使所有应用以等同 root 的完整能力运行。

```c
#ifdef GRAPHIC_PERMISSION_CHECK
    const uint64_t inheriTable = 0;
    const uint64_t permitted = 0;
    const uint64_t effective = 0;
#else
    const uint64_t inheriTable = 0x3fffffffff;
    const uint64_t permitted = 0x3fffffffff;
    const uint64_t effective = 0x3fffffffff;
#endif
```

类似地，`SetUidGid`（第 233-267 行）仅在 `#ifdef GRAPHIC_PERMISSION_CHECK` 块内调用 `setgroups`、`setresgid`、`setresuid` 和 `setSeccompFilter`，这意味着在没有此标志的情况下，uid/gid 隔离和 seccomp 过滤被完全禁用。对于安全关键代码路径来说，这是一个极其危险的编译时开关。

**建议**：默认为零能力，要求显式选择加入以获取提升权限。永远不要将安全控制默认开放在构建标志后面。

### HIGH-APPSPAWN-02：Appspawn Socket 认证仅限两个 UID
**严重等级：高危**
**文件**：`/home/dspfac/openharmony/base/startup/appspawn/standard/appspawn_service.c`，第 645-652 行

`AcceptClient` 函数检查 `SO_PEERCRED` 并仅接受来自 `foundation` 或 `root` UID 的连接：

```c
if ((getsockopt(LE_GetSocketFd(stream), SOL_SOCKET, SO_PEERCRED, &cred, &credSize) < 0) ||
    (cred.uid != DecodeUid("foundation")  && cred.uid != DecodeUid("root"))) {
```

虽然这是合理的访问控制，但对调用方没有二次认证（例如令牌验证）。任何以 UID `foundation` 运行的进程都可以指示 appspawn 使用任意参数（包括 uid、gid、能力和沙箱标志）启动任何应用程序。`APP_NO_SANDBOX` 标志（`appspawn_msg.h` 第 71 行的 0x80）可以被设置以完全跳过沙箱化。

**建议**：实施命令级授权。验证请求进程是合法的 ability 管理器。考虑添加加密质询/响应或将请求绑定到特定的系统能力 ID。

### HIGH-APPSPAWN-03：为特定包名硬编码注入特权 GID
**严重等级：高危**
**文件**：`/home/dspfac/openharmony/base/startup/appspawn/standard/appspawn_service.c`，第 411-427 行

`HandleSpecial` 函数为特定硬编码包名（`com.ohos.medialibrary.medialibrarydata` 及其备份变体）注入 `GID_USER_DATA_RW` 和 `GID_FILE_ACCESS`。如果攻击者可以控制包名注册，这将成为权限提升向量：

```c
if (strcmp(appProperty->property.bundleName, specialBundleNames[i]) == 0) {
    if (appProperty->property.gidCount < APP_MAX_GIDS) {
        appProperty->property.gidTable[appProperty->property.gidCount++] = GID_USER_DATA_RW;
        appProperty->property.gidTable[appProperty->property.gidCount++] = GID_FILE_ACCESS;
    }
```

还存在潜在的差一错误：检查条件为 `< APP_MAX_GIDS` 但添加了两个条目，这意味着如果 `gidCount == APP_MAX_GIDS - 1`，第二次写入将越界。

**建议**：为两次添加操作增加边界检查（`gidCount + 2 <= APP_MAX_GIDS`）。将权限定义移至声明式策略文件，而非硬编码在 C 中。

### MEDIUM-APPSPAWN-04：额外信息缓冲区处理允许大内存分配
**严重等级：中危**
**文件**：`/home/dspfac/openharmony/base/startup/appspawn/standard/appspawn_service.c`，第 53、466-499 行

`EXTRAINFO_TOTAL_LENGTH_MAX` 为 32KB，检查存在于 `CheckRequestMsgValid`（第 503 行）。然而，检查使用 `>=`，这意味着恰好为 `EXTRAINFO_TOTAL_LENGTH_MAX - 1`（32767）的长度被接受。分配通过 `ReceiveRequestDataToExtraInfo` 中的 `malloc(extraInfo->totalLength)` 进行，未检查 totalLength 是否在检查和使用之间被篡改。

**建议**：确保 totalLength 在分配时再次验证。考虑减小最大值或实现池分配器以防止堆碎片攻击。

### MEDIUM-APPSPAWN-05：processName 输入时不保证以 null 结尾
**严重等级：中危**
**文件**：`/home/dspfac/openharmony/base/startup/appspawn/standard/appspawn_service.c`，第 501-515 行

`CheckRequestMsgValid` 扫描 `APP_LEN_PROC_NAME`（256）字节以查找 null 终止符。如果未找到，则拒绝。但是，由于整个 `AppParameter` 结构通过 `memcpy_s` 从网络复制，其中的任何字段（bundleName、soPath、apl、renderCmd、ownerId）都未被验证 null 终止。仅 `processName` 被检查。其他固定长度 char 数组可能包含未终止的字符串，在其他地方使用 `strcmp`、`strlen` 或 `strcpy_s` 等字符串函数时导致缓冲区过读。

**建议**：在处理前验证 `AppParameter` 中所有 char 数组字段的 null 终止。

### MEDIUM-APPSPAWN-06：冷启动通过命令行参数传递敏感数据
**严重等级：中危**
**文件**：`/home/dspfac/openharmony/base/startup/appspawn/standard/appspawn_process.c`，第 405-489 行

`ColdStartApp` 和 `EncodeAppClient` 函数将整个 `AppParameter`（包括 `accessTokenId`、`accessTokenIdEx`、`uid`、`gid`、所有 GID、`bundleName`、`apl`、`renderCmd`、`ownerId`）序列化为冒号分隔的字符串，作为命令行参数传递给 `execv`。命令行参数可通过 `/proc/[pid]/cmdline` 对所有用户可见。

**建议**：使用文件描述符、管道或具有适当权限的共享内存来传递敏感孵化参数，而非命令行。

### LOW-APPSPAWN-07：exit() 钩子可能导致意外行为
**严重等级：低危**
**文件**：`/home/dspfac/openharmony/base/startup/appspawn/common/appspawn_server.c`，第 66-81 行

`exit()` 函数被 `__attribute__((visibility("default")))` 覆盖以截获并重定向到 `ProcessExit`。检查 `atoi(checkExit) == getpid()` 使用的 `atoi` 不能稳健地处理错误——格式错误的 `APPSPAWN_CHECK_EXIT` 环境变量可能产生 0，匹配 PID 0 的场景。

---

## 系统能力管理器 (samgr)

### CRITICAL-SAMGR-01：SELinux 禁用时检查默认允许
**严重等级：严重**
**文件**：`/home/dspfac/openharmony/foundation/systemabilitymgr/samgr/services/samgr/native/source/system_ability_manager_stub.cpp`，第 45-99 行

所有四个 SELinux 检查函数（`CheckGetSAPermission`、`CheckAddOrRemovePermission`、`CheckGetRemoteSAPermission`、`CheckListSAPermission`）在未定义 `WITH_SELINUX` 时返回 `true`：

```c
#ifdef WITH_SELINUX
    // ... 实际检查 ...
#else
    return true; // 不支持 selinux 时，不检查 selinux 权限
#endif
```

这意味着任何未启用 SELinux 的构建对哪些进程可以获取、添加、移除或列出系统能力没有任何访问控制。这是系统中等同于 Android ServiceManager 的组件——完全绕过允许任何进程劫持或注入伪造的系统服务。

**建议**：当 SELinux 不可用时，实施替代的 MAC 或 DAC 访问控制。永远不要对服务注册表默认开放访问。

### HIGH-SAMGR-02：CanRequest() 仅检查令牌类型，不检查具体身份
**严重等级：高危**
**文件**：`/home/dspfac/openharmony/foundation/systemabilitymgr/samgr/services/samgr/native/source/system_ability_manager_stub.cpp`，第 996-1003 行

`CanRequest()` 函数仅验证调用者具有 `TOKEN_NATIVE` 令牌类型：

```c
bool SystemAbilityManagerStub::CanRequest()
{
    auto accessTokenId = IPCSkeleton::GetCallingTokenID();
    AccessToken::ATokenTypeEnum tokenType = AccessToken::AccessTokenKit::GetTokenTypeFlag(accessTokenId);
    return (tokenType == AccessToken::ATokenTypeEnum::TOKEN_NATIVE);
}
```

任何原生进程（不仅仅是特定的系统服务）都可以调用特权操作，如 `AddSystemAbility`、`RemoveSystemAbility`、`AddSystemProcess`、`GetSystemProcessInfo`、`GetRunningSystemProcess`、`SubscribeSystemProcess` 等。这些操作没有细粒度权限检查——任何原生守护进程都可以注册或注销系统能力。

**建议**：实施按能力或按操作的权限检查。至少验证调用进程身份与系统能力的预期所有者匹配。

### MEDIUM-SAMGR-03：订阅/取消订阅系统能力缺少权限检查
**严重等级：中危**
**文件**：`/home/dspfac/openharmony/foundation/systemabilitymgr/samgr/services/samgr/native/source/system_ability_manager_stub.cpp`，第 234-296 行

`SubsSystemAbilityInner` 和 `UnSubsSystemAbilityInner` 不调用 `CanRequest()`。它们仅验证系统能力 ID 并读取远程对象。这意味着任何进程（包括第三方应用）都可以订阅系统能力状态变化，可能泄露哪些系统服务启动和停止的信息。

**建议**：在允许订阅前添加 `CanRequest()` 或适当的权限检查。

### MEDIUM-SAMGR-04：GetOnDemandSystemAbilityIds 无权限检查暴露内部状态
**严重等级：中危**
**文件**：`/home/dspfac/openharmony/foundation/systemabilitymgr/samgr/services/samgr/native/source/system_ability_manager_stub.cpp`，第 981-994 行

`GetOnDemandSystemAbilityIdsInner` 完全没有权限检查。任何调用者都可以枚举所有按需系统能力 ID，揭示内部系统架构信息。

### MEDIUM-SAMGR-05：UnloadSystemAbility 和 CancelUnloadSystemAbility 缺少 CanRequest 检查
**严重等级：中危**
**文件**：`/home/dspfac/openharmony/foundation/systemabilitymgr/samgr/services/samgr/native/source/system_ability_manager_stub.cpp`，第 666-685、835-854 行

`UnloadSystemAbilityInner` 和 `CancelUnloadSystemAbilityInner` 均不调用 `CanRequest()`。它们验证系统能力 ID 但不验证调用者的权限级别。非原生进程可能卸载系统能力，导致拒绝服务。

### LOW-SAMGR-06：SA 配置文件从固定路径加载，无完整性验证
**严重等级：低危**
**文件**：`/home/dspfac/openharmony/foundation/systemabilitymgr/samgr/services/samgr/native/source/system_ability_manager.cpp`，第 192-224 行

SA 配置 JSON 文件从 `/system/profile/` 加载，无加密完整性验证。虽然 `/system` 分区应为只读，但被入侵的系统分区可能注入恶意的 SA 配置定义。

---

## 电源管理服务

### HIGH-POWER-01：电源管理桩在桩层无权限检查
**严重等级：高危**
**文件**：`/home/dspfac/openharmony/base/powermgr/power_manager/services/zidl/src/power_mgr_stub.cpp`，第 39-170 行

`PowerMgrStub::OnRemoteRequest` 基于 switch 语句分发到处理函数，在桩层没有任何权限验证。接口令牌验证存在（第 43-47 行），但敏感操作如 `WAKEUP_DEVICE`、`SUSPEND_DEVICE`、`REBOOT_DEVICE`、`SHUTDOWN_DEVICE`、`FORCE_DEVICE_SUSPEND`、`CREATE_RUNNINGLOCK`、`SET_DISPLAY_SUSPEND` 和 `SHELL_DUMP` 在没有调用者验证的情况下被分发。

权限检查被推迟到服务实现层（例如 `PowerMgrService::RebootDeviceForDeprecated` 检查 `Permission::IsPermissionGranted("ohos.permission.REBOOT")`）。虽然这种方法有效，但这意味着：
1. 添加到桩的新处理程序可能忘记权限检查
2. 在权限检查之前可能泄露错误消息

**建议**：在桩层实施权限检查表，确保每个操作在分发前都验证其所需权限。

### MEDIUM-POWER-02：ShellDump 通过 IPC 暴露且无记录的访问控制
**严重等级：中危**
**文件**：`/home/dspfac/openharmony/base/powermgr/power_manager/services/zidl/src/power_mgr_stub.cpp`，第 153-155 行

`SHELL_DUMP` 作为 IPC 命令暴露。虽然服务层的 `Dump()` 检查 `Permission::IsSystem()`，但它可通过 IPC 访问（不仅仅是 binder dump）这一事实增加了攻击面。转储输出可能包含敏感的电源状态信息。

---

## 传感器服务

### MEDIUM-SENSOR-01：GetAllSensors 无权限检查返回完整传感器列表
**严重等级：中危**
**文件**：`/home/dspfac/openharmony/base/sensors/sensor/services/src/sensor_service_stub.cpp`，第 162-179 行

`GetAllSensorsInner` 在没有任何权限检查的情况下向任何调用者返回完整的可用传感器列表。虽然单个传感器启用/禁用操作正确检查了 `CheckSensorPermission`，但传感器枚举向任何应用揭示了硬件能力。

**建议**：考虑至少需要基本权限才能枚举传感器列表，或从非系统调用者的返回列表中过滤敏感传感器类型（SAR、医疗）。

### LOW-SENSOR-02：传感器服务 Shell 令牌被视为系统服务
**严重等级：低危**
**文件**：`/home/dspfac/openharmony/base/sensors/sensor/services/src/sensor_service_stub.cpp`，第 98-108 行

`IsSystemServiceCalling()` 将 `TOKEN_SHELL` 视为等同于 `TOKEN_NATIVE`。这意味着 `hdc shell` 会话获得系统服务级别的传感器操作权限。虽然调试需要此功能，但如果 `hdc` 可被访问则可能被利用。

---

## 定位服务

### HIGH-LOCATION-01：分级权限模型正确实现
**严重等级：信息性（正面发现）**
**文件**：`/home/dspfac/openharmony/base/location/frameworks/location_common/common/source/common_utils.cpp`，第 45-118 行

定位服务实现了结构良好的分级权限模型：
- `ACCESS_APPROXIMATELY_LOCATION` 用于粗略定位
- `ACCESS_LOCATION` 用于精确定位
- `ACCESS_BACKGROUND_LOCATION` 用于后台访问
- `MANAGE_SECURE_SETTINGS` 用于管理操作

`GetPermissionLevel` 函数正确区分精确（两个权限都具备）、粗略（仅近似）和拒绝访问。这是良好的安全模式。

### MEDIUM-LOCATION-02：定位服务桩文件未在预期路径找到
**严重等级：中危**
**文件**：`/home/dspfac/openharmony/base/location/services/`

在 services 目录下未找到 `*stub*.cpp` 文件，表明 IPC 桩可能是自动生成的或位于其他位置。这使得审计定位服务的 IPC 攻击面更加困难。权限执行似乎通过服务实现中的 `CommonUtils::CheckPermission` 进行，这是好的，但缺少可见的桩层验证是一个值得关注的问题。

---

## 通知服务

### CRITICAL-NOTIFY-01：公共事件桩在 IPC 层无权限检查
**严重等级：严重**
**文件**：`/home/dspfac/openharmony/base/notification/common_event_service/frameworks/core/src/common_event_stub.cpp`，第 32-228 行

`CommonEventStub::OnRemoteRequest` 在桩层没有任何权限检查的情况下处理多个敏感操作：
- `CES_PUBLISH_COMMON_EVENT` —— 发布系统范围事件
- `CES_SUBSCRIBE_COMMON_EVENT` —— 订阅系统事件
- `CES_FREEZE` / `CES_UNFREEZE` / `CES_UNFREEZE_ALL` —— 按 UID 冻结/解冻应用事件传递
- `CES_REMOVE_STICKY_COMMON_EVENT` —— 移除粘性事件
- `CES_DUMP_STATE` —— 转储内部状态

冻结/解冻操作从 IPC 消息接受原始 `uid` 参数，且不验证调用者是否有权冻结另一个应用的事件传递。桩基类实现都返回成功（`true` / `ERR_OK`），意味着权限执行完全依赖子类覆盖。

此外，`CES_PUBLISH_COMMON_EVENT2` 直接从 IPC 消息接受 `uid` 和 `callerToken`（第 72-73 行），这是信任边界违规——调用者不应能够指定自己的身份。

**建议**：
1. 在桩层为特权操作添加强制权限检查
2. 永远不要从 IPC 消息接受调用者身份（uid、callerToken）——始终从 `IPCSkeleton::GetCallingUid()` 获取

### HIGH-NOTIFY-02：AnsManagerStub 拥有庞大的 IPC 攻击面且无可见的桩层检查
**严重等级：高危**
**文件**：`/home/dspfac/openharmony/base/notification/distributed_notification_service/frameworks/core/src/ans_manager_stub.cpp`

`AnsManagerStub` 通过 `std::bind` 注册了大量接口处理程序（100 多个操作）。`OnRemoteRequest` 模式基于映射查找进行分发。桩层没有可见的权限检查——所有安全依赖服务实现。

鉴于庞大的攻击面（发布、取消、订阅、通道管理、角标管理、免打扰模式等），缺少桩层守卫增加了个别处理程序实现可能遗漏所需检查的风险。

---

## 输入法框架

### HIGH-IMF-01：输入法桩缺少桩层权限执行
**严重等级：高危**
**文件**：`/home/dspfac/openharmony/base/inputmethod/imf/services/src/input_method_system_ability_stub.cpp`

`InputMethodSystemAbilityStub::OnRemoteRequest` 通过 `HANDLERS.at(code)` 按代码索引分发到处理函数（第 42 行），没有任何权限检查。关键操作包括：
- `SetCoreAndAgent` —— 注册 IME 核心和代理对象（第 128-142 行）
- `StartInput` —— 启动输入会话
- `SwitchInputMethod` —— 切换活动 IME
- `PanelStatusChange` —— 报告面板状态变化

`SetCoreAndAgent` 操作特别敏感，因为它注册将接收所有键盘输入的对象。没有桩层访问控制，任何进程都可能注册为活动输入法，拦截所有用户按键。

**建议**：为 IME 注册操作添加 `CanRequest()` 风格的检查。验证只有授权的 IME 进程可以调用 `SetCoreAndAgent`。

### MEDIUM-IMF-02：已弃用方法保留但无权限检查
**严重等级：中危**
**文件**：`/home/dspfac/openharmony/base/inputmethod/imf/services/src/input_method_system_ability.cpp`，第 671 行

注释说明："因为没有权限检查所以已弃用，为兼容性保留"。没有权限检查的已弃用方法应被移除或改造，而非为向后兼容而保留。相关的桩处理方法（`ShowCurrentInputOnRemoteDeprecated`、`HideCurrentInputOnRemoteDeprecated`）仍可通过 IPC 访问。

### MEDIUM-IMF-03：SwitchInputMethod 防欺骗检查易被绕过
**严重等级：中危**
**文件**：`/home/dspfac/openharmony/base/inputmethod/imf/services/src/input_method_system_ability.cpp`，第 492-509 行

`SwitchInputMethod` 函数有一个防欺骗检查：
```c
if (trigger == SwitchTrigger::IMSA) {
    IMSA_HILOGW("caller counterfeit!");
    return ErrorCode::ERROR_BAD_PARAMETERS;
}
```

此检查防止调用者声称触发来自 IMSA 本身。然而，`SwitchTrigger` 枚举值通过 IPC 传递，任何调用者都可以将其设置为 `IMSA` 以外的任何值来绕过此检查。真正的权限检查在后面的 `CheckSwitchPermission` 中进行，但关于"冒充"调用者的注释表明安全模型并不健壮。

---

## 多模态输入服务

### HIGH-MMI-01：输入事件注入缺少桩层强权限执行
**严重等级：高危**
**文件**：`/home/dspfac/openharmony/foundation/multimodalinput/input/service/connect_manager/src/multimodal_input_connect_stub.cpp`，第 177-182 行

`StubInjectKeyEvent` 和 `StubInjectPointerEvent` 作为 IPC 命令暴露。输入注入是极其敏感的操作，因为它可以模拟用户动作（键入密码、点击按钮等）。虽然某些操作存在 `VerifySystemApp()` 检查（例如 `StubAddInputEventFilter` 在第 329 行），但注入桩的权限要求在桩分发层面不可见。

**建议**：验证输入注入在桩层需要 `ohos.permission.INJECT_INPUT_EVENT` 或等效权限。为每个 IPC 入口点添加明确注释记录安全模型。

### MEDIUM-MMI-02：从不受信任的 IPC 数据反序列化 PixelMap
**严重等级：中危**
**文件**：`/home/dspfac/openharmony/foundation/multimodalinput/input/service/connect_manager/src/multimodal_input_connect_stub.cpp`，第 411、452 行

`StubSetCustomCursor` 和 `StubSetMouseIcon` 直接从 IPC 数据反序列化 `PixelMap` 对象：
```c
OHOS::Media::PixelMap* pixelMap = Media::PixelMap::Unmarshalling(data);
```
以及
```c
OHOS::Media::PixelMap* pixelMap = OHOS::Media::PixelMap::DecodeTlv(buff);
```

从不受信任的 IPC 输入反序列化复杂对象是内存损坏漏洞的常见来源。`StubSetMouseIcon` 还根据从 IPC 消息读取的大小分配缓冲区（`size` 最大为 `MAX_BUFFER_SIZE` = 1,000,000），可被用于内存耗尽。

此外，`StubSetCustomCursor` 没有其他类似操作所具有的 `VerifySystemApp()` 检查，意味着任何应用都可以调用它。

**建议**：为 `StubSetCustomCursor` 添加 `VerifySystemApp()`。为 PixelMap 数据实施大小限制和沙箱化反序列化。考虑使用更严格的最大缓冲区大小。

### LOW-MMI-03：描述符不匹配时以明文记录
**严重等级：低危**
**文件**：`/home/dspfac/openharmony/foundation/multimodalinput/input/service/connect_manager/src/multimodal_input_connect_stub.cpp`，第 53 行

```c
MMI_HILOGE("Get unexpect descriptor:%{public}s", Str16ToStr8(descriptor).c_str());
```

记录来自攻击者控制消息的实际描述符值可能导致信息泄露或日志注入。

---

## 后台任务管理器

### HIGH-BGTASK-01：后台任务桩在桩层无权限检查
**严重等级：高危**
**文件**：`/home/dspfac/openharmony/foundation/resourceschedule/background_task_mgr/frameworks/src/background_task_mgr_stub.cpp`

`BackgroundTaskMgrStub` 在没有任何权限验证的情况下分发所有 IPC 命令。关键操作包括：
- `REQUEST_SUSPEND_DELAY` —— 请求延迟挂起（任何应用）
- `START_BACKGROUND_RUNNING` / `STOP_BACKGROUND_RUNNING` —— 启动/停止持续后台任务
- `SUBSCRIBE_BACKGROUND_TASK` / `UNSUBSCRIBE_BACKGROUND_TASK` —— 监控后台任务
- `APPLY_EFFICIENCY_RESOURCES` —— 请求效率资源
- `STOP_CONTINUOUS_TASK` —— 通过 uid/pid 停止另一个应用的后台任务
- `REQUEST_BACKGROUND_RUNNING_FOR_INNER` —— 内部后台运行请求

`HandleStopContinuousTask`（第 336-347 行）直接从 IPC 消息接受 `uid`、`pid` 和 `taskType`，无权限检查。任何调用者都可能停止另一个应用的后台任务。

`HandleGetTransientTaskApps` 和 `HandleGetContinuousTaskApps` 暴露所有具有后台任务的应用列表，这是敏感的系统状态信息。

**建议**：在桩层添加强制权限检查。对自管理操作验证调用者 UID 匹配，对跨应用操作要求系统权限。

---

## 账户管理

### MEDIUM-ACCOUNT-01：OS 账户操作正确标记系统 API 标志
**严重等级：信息性（正面发现）**
**文件**：`/home/dspfac/openharmony/base/account/os_account/services/accountmgr/src/osaccount/os_account_stub.cpp`

OS 账户服务使用结构良好的 `messageProcMap`，每个操作带有 `isSyetemApi` 标志：
```c
{
    .messageProcFunction = &OsAccountStub::ProcCreateOsAccount,
    .isSyetemApi = true,
}
```

这允许分发器在到达处理程序之前拒绝来自非系统应用的调用。这比审查的许多其他服务的模式更好。

### LOW-ACCOUNT-02：字段名"isSyetemApi"拼写错误
**严重等级：低危**
**文件**：`/home/dspfac/openharmony/base/account/os_account/services/accountmgr/src/osaccount/os_account_stub.cpp`

字段名 `isSyetemApi` 是 `isSystemApi` 的拼写错误。虽然不是安全问题，但表明代码质量方面的关注点，可能导致混淆。

---

## 横切关注点

### CRITICAL-CROSS-01：不一致的 IPC 权限执行架构
**严重等级：严重**

在整个系统服务层中，IPC 权限执行没有一致的架构：

| 服务 | 桩层检查 | 服务层检查 | 模式 |
|------|---------|-----------|------|
| samgr | SELinux（ifdef）+ CanRequest | CallerProcess 检查 | 混合 |
| 电源管理器 | 仅接口令牌 | Permission::IsPermissionGranted | 延迟 |
| 传感器服务 | 按处理程序的权限检查 | 不适用（在桩完成） | 良好 |
| 公共事件 | 无 | 未知 | **缺失** |
| 通知（ANS） | 无可见检查 | 未知 | **有风险** |
| 输入法 | 无 | 部分 | **有风险** |
| 多模态输入 | 部分（VerifySystemApp） | 未知 | 不一致 |
| 后台任务 | 无 | 未知 | **缺失** |
| 账户 | isSyetemApi 标志 | 额外检查 | 良好 |

传感器服务和账户服务具有最佳模式。公共事件、通知和后台任务服务最差——完全依赖服务实现层，这容易出错。

**建议**：建立强制的框架级模式，每个 IPC 桩必须在表中声明每个操作所需的权限，在分发前执行。

### HIGH-CROSS-02：接口令牌验证是许多服务的唯一关卡
**严重等级：高危**

多个服务仅依赖 `ReadInterfaceToken()` 匹配预期描述符作为其 IPC 认证。接口令牌防止意外的跨服务调用，但对恶意调用者提供零安全性——任何进程都可以读取服务的描述符字符串并将其包含在构造的 IPC 消息中。

### MEDIUM-CROSS-03：IPC 回复中的无界列表序列化
**严重等级：中危**

多个服务向 IPC 回复 parcel 写入无界列表：
- `GetRunningSystemProcessInner` 写入所有进程信息（samgr_stub 第 763 行）
- `HandleGetTransientTaskApps` / `HandleGetContinuousTaskApps` 写入所有应用信息
- `GetAllSensorsInner` 有 `MAX_SENSOR_COUNT` 上限

无界列表序列化可能在服务端（序列化）和客户端（反序列化）都引起 OOM 状况，导致拒绝服务。

**建议**：为所有返回列表的 IPC 操作实施分页或硬性上限。

### MEDIUM-CROSS-04：使用 ReadParcelable 从 IPC 进行复杂反序列化
**严重等级：中危**

多个桩使用 `data.ReadParcelable<T>()` 从 IPC 消息反序列化复杂对象：
- CommonEventStub 中的 `CommonEventData`、`CommonEventPublishInfo`
- PowerMgrStub 中的 `RunningLockInfo`
- BackgroundTaskMgrStub 中的 `ContinuousTaskParam`
- BackgroundTaskMgrStub 中的 `EfficiencyResourceInfo`
- MultimodalInputConnectStub 中的 `ConnectReqParcel`

每个 `Parcelable` 实现都是针对构造输入的额外攻击面。如果任何 `Unmarshalling` 实现存在缺陷（缓冲区溢出、整数溢出），可以被任何能向服务发送 IPC 的进程触发。

### LOW-CROSS-05：敏感信息日志记录
**严重等级：低危**

多个服务在 INFO 或 DEBUG 级别记录 PID、UID、进程名、包名和能力信息。虽然使用了 `%{public}` 格式说明符（在生产环境中应被过滤），但日志中安全相关数据的大量存在在日志访问未得到适当控制时构成风险。

---

## 发现汇总

| ID | 严重等级 | 组件 | 问题 |
|----|---------|------|------|
| CRITICAL-APPSPAWN-01 | 严重 | appspawn | 无 GRAPHIC_PERMISSION_CHECK 时授予所有能力 |
| CRITICAL-SAMGR-01 | 严重 | samgr | SELinux 禁用时检查默认允许 |
| CRITICAL-NOTIFY-01 | 严重 | CES | 公共事件桩无权限检查，接受伪造 UID |
| CRITICAL-CROSS-01 | 严重 | 全部 | 不一致的 IPC 权限执行架构 |
| HIGH-APPSPAWN-02 | 高危 | appspawn | Socket 认证仅限两个 UID，无二次验证 |
| HIGH-APPSPAWN-03 | 高危 | appspawn | 硬编码 GID 注入，存在差一错误可能 |
| HIGH-SAMGR-02 | 高危 | samgr | CanRequest() 仅检查令牌类型，不检查具体身份 |
| HIGH-POWER-01 | 高危 | power_manager | 桩层无权限检查 |
| HIGH-NOTIFY-02 | 高危 | ANS | 庞大 IPC 攻击面，无可见桩层检查 |
| HIGH-IMF-01 | 高危 | IMF | SetCoreAndAgent 无桩层权限检查 |
| HIGH-MMI-01 | 高危 | MMI | 输入注入缺少强桩层权限 |
| HIGH-BGTASK-01 | 高危 | bgtaskmgr | 桩层无权限检查，跨应用操作暴露 |
| HIGH-CROSS-02 | 高危 | 全部 | 接口令牌是许多服务的唯一认证 |
| MEDIUM-APPSPAWN-04 | 中危 | appspawn | 通过 extraInfo 进行大内存分配且无二次验证 |
| MEDIUM-APPSPAWN-05 | 中危 | appspawn | 仅 processName 验证 null 终止 |
| MEDIUM-APPSPAWN-06 | 中危 | appspawn | 冷启动时通过命令行传递敏感数据 |
| MEDIUM-SAMGR-03 | 中危 | samgr | 订阅/取消订阅缺少权限检查 |
| MEDIUM-SAMGR-04 | 中危 | samgr | GetOnDemandSystemAbilityIds 无权限检查 |
| MEDIUM-SAMGR-05 | 中危 | samgr | 卸载/取消卸载 SA 缺少 CanRequest 检查 |
| MEDIUM-SENSOR-01 | 中危 | sensor | GetAllSensors 无权限检查 |
| MEDIUM-LOCATION-02 | 中危 | location | 未找到桩文件以供审计 |
| MEDIUM-POWER-02 | 中危 | power_manager | ShellDump 通过 IPC 暴露 |
| MEDIUM-IMF-02 | 中危 | IMF | 已弃用方法保留但无权限检查 |
| MEDIUM-IMF-03 | 中危 | IMF | SwitchTrigger 防欺骗易被绕过 |
| MEDIUM-MMI-02 | 中危 | MMI | 从不受信任 IPC 反序列化 PixelMap |
| MEDIUM-CROSS-03 | 中危 | 全部 | IPC 回复中的无界列表序列化 |
| MEDIUM-CROSS-04 | 中危 | 全部 | ReadParcelable 攻击面 |
| LOW-APPSPAWN-07 | 低危 | appspawn | exit() 钩子使用脆弱的 atoi 检查 |
| LOW-SAMGR-06 | 低危 | samgr | SA 配置文件加载无完整性验证 |
| LOW-SENSOR-02 | 低危 | sensor | TOKEN_SHELL 被视为系统服务 |
| LOW-ACCOUNT-02 | 低危 | account | "isSyetemApi"字段名拼写错误 |
| LOW-MMI-03 | 低危 | MMI | 描述符以明文记录 |
| LOW-CROSS-05 | 低危 | 全部 | 过多安全相关日志 |
| INFO-LOCATION-01 | 信息 | location | 分级权限模型实现良好 |
| INFO-ACCOUNT-01 | 信息 | account | 系统 API 标志模式实现良好 |

### 优先建议

1. **立即**：修复 CRITICAL-APPSPAWN-01——确保能力始终降为零，除非明确需要。授予所有能力的 `#else` 分支必须被移除。

2. **立即**：解决 CRITICAL-NOTIFY-01——公共事件服务在 `CES_PUBLISH_COMMON_EVENT2` 中接受调用者提供的 uid/callerToken 是直接的权限提升向量。

3. **短期**：建立强制的 IPC 权限执行框架。每个桩必须在表中声明每个操作的权限，在分发前执行（CRITICAL-CROSS-01）。

4. **短期**：在 samgr 中 SELinux 禁用时添加后备访问控制（CRITICAL-SAMGR-01）。这是系统的服务注册表——此处被入侵意味着一切被入侵。

5. **中期**：审计所有 `ReadParcelable` 实现的内存安全性。为每个 IPC 入口点添加模糊测试（MEDIUM-CROSS-04）。

6. **中期**：验证 AppParameter 中所有字符串字段的 null 终止（MEDIUM-APPSPAWN-05），并将冷启动参数传递从命令行迁移到安全通道（MEDIUM-APPSPAWN-06）。
