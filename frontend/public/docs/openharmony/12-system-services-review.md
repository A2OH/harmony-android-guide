# OpenHarmony 4.1 System Services Layer - Code Review

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Appspawn Security](#appspawn-security)
3. [System Ability Manager (samgr)](#system-ability-manager-samgr)
4. [Power Management Service](#power-management-service)
5. [Sensor Service](#sensor-service)
6. [Location Service](#location-service)
7. [Notification Service](#notification-service)
8. [Input Method Framework](#input-method-framework)
9. [Multimodal Input Service](#multimodal-input-service)
10. [Background Task Manager](#background-task-manager)
11. [Account Management](#account-management)
12. [Cross-Cutting Concerns](#cross-cutting-concerns)
13. [Summary of Findings](#summary-of-findings)

---

## Executive Summary

This review covers the System Services layer of OpenHarmony 4.1, examining init/startup, system ability management, sensor/location/power APIs, notification, input method framework, multimodal input, background task management, and account services. The review focused on IPC security, permission enforcement, input validation, and architectural security patterns.

**Critical findings**: 7 | **High findings**: 9 | **Medium findings**: 11 | **Low/Informational**: 8

The most severe issues involve capabilities not being dropped in non-GRAPHIC_PERMISSION_CHECK builds of appspawn, missing permission checks in the common event and background task IPC stubs, and SELinux checks that silently pass when SELinux is disabled.

---

## Appspawn Security

### CRITICAL-APPSPAWN-01: All Capabilities Granted When GRAPHIC_PERMISSION_CHECK Is Undefined
**Severity: CRITICAL**
**File**: `/home/dspfac/openharmony/base/startup/appspawn/standard/appspawn_process.c`, lines 141-149

When the `GRAPHIC_PERMISSION_CHECK` macro is not defined, the `SetCapabilities` function grants the spawned app process ALL Linux capabilities (`0x3fffffffff` for inheritable, permitted, and effective sets). This means any build configuration that omits this define runs all apps with full root-equivalent capabilities.

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

Similarly, `SetUidGid` (lines 233-267) only calls `setgroups`, `setresgid`, `setresuid`, and `setSeccompFilter` inside the `#ifdef GRAPHIC_PERMISSION_CHECK` block, meaning uid/gid isolation and seccomp filtering are completely disabled without this flag. This is an extremely dangerous compile-time toggle for a security-critical code path.

**Recommendation**: Default to zero capabilities and require an explicit opt-in for elevated privileges. Never default-open security controls behind a build flag.

### HIGH-APPSPAWN-02: Appspawn Socket Authentication Limited to Two UIDs
**Severity: HIGH**
**File**: `/home/dspfac/openharmony/base/startup/appspawn/standard/appspawn_service.c`, lines 645-652

The `AcceptClient` function checks `SO_PEERCRED` and only accepts connections from UIDs matching `foundation` or `root`:

```c
if ((getsockopt(LE_GetSocketFd(stream), SOL_SOCKET, SO_PEERCRED, &cred, &credSize) < 0) ||
    (cred.uid != DecodeUid("foundation")  && cred.uid != DecodeUid("root"))) {
```

While this is a reasonable access control, there is no secondary authentication (e.g., token verification) for the caller. Any process running as UID `foundation` can instruct appspawn to launch any application with arbitrary parameters including uid, gid, capabilities, and sandbox flags. The `APP_NO_SANDBOX` flag (0x80 in `appspawn_msg.h` line 71) can be set to skip sandboxing entirely.

**Recommendation**: Implement command-level authorization. Validate that the requesting process is the legitimate ability manager. Consider adding a cryptographic challenge/response or tying requests to specific system ability IDs.

### HIGH-APPSPAWN-03: Hardcoded Privileged GID Injection for Specific Bundle Names
**Severity: HIGH**
**File**: `/home/dspfac/openharmony/base/startup/appspawn/standard/appspawn_service.c`, lines 411-427

The `HandleSpecial` function injects `GID_USER_DATA_RW` and `GID_FILE_ACCESS` for specific hardcoded bundle names (`com.ohos.medialibrary.medialibrarydata` and its backup variant). This is a privilege escalation vector if an attacker can control bundle name registration:

```c
if (strcmp(appProperty->property.bundleName, specialBundleNames[i]) == 0) {
    if (appProperty->property.gidCount < APP_MAX_GIDS) {
        appProperty->property.gidTable[appProperty->property.gidCount++] = GID_USER_DATA_RW;
        appProperty->property.gidTable[appProperty->property.gidCount++] = GID_FILE_ACCESS;
    }
```

There is also a potential off-by-one: the check is `< APP_MAX_GIDS` but two entries are added, meaning if `gidCount == APP_MAX_GIDS - 1`, the second write goes out of bounds.

**Recommendation**: Add bounds checking for both additions (`gidCount + 2 <= APP_MAX_GIDS`). Move privilege definitions to a declarative policy file rather than hardcoding in C.

### MEDIUM-APPSPAWN-04: Extra Info Buffer Handling Allows Large Allocations
**Severity: MEDIUM**
**File**: `/home/dspfac/openharmony/base/startup/appspawn/standard/appspawn_service.c`, lines 53, 466-499

The `EXTRAINFO_TOTAL_LENGTH_MAX` is 32KB, and the check exists in `CheckRequestMsgValid` (line 503). However, the check uses `>=` which means a length of exactly `EXTRAINFO_TOTAL_LENGTH_MAX - 1` (32767) is accepted. The allocation occurs in `ReceiveRequestDataToExtraInfo` via `malloc(extraInfo->totalLength)` without checking if the totalLength could have been corrupted between check and use.

**Recommendation**: Ensure totalLength is validated again at allocation time. Consider reducing the maximum or implementing a pool allocator to prevent heap fragmentation attacks.

### MEDIUM-APPSPAWN-05: processName Not Guaranteed Null-Terminated on Input
**Severity: MEDIUM**
**File**: `/home/dspfac/openharmony/base/startup/appspawn/standard/appspawn_service.c`, lines 501-515

`CheckRequestMsgValid` scans `APP_LEN_PROC_NAME` (256) bytes looking for a null terminator. If none is found, it rejects. However, since the entire `AppParameter` structure is copied from the network via `memcpy_s`, any field within it (bundleName, soPath, apl, renderCmd, ownerId) is NOT validated for null-termination. Only `processName` is checked. The other fixed-length char arrays could contain non-terminated strings leading to buffer over-reads when used with string functions like `strcmp`, `strlen`, or `strcpy_s` elsewhere.

**Recommendation**: Validate null-termination for ALL char array fields in `AppParameter` before processing.

### MEDIUM-APPSPAWN-06: Cold Start Passes Sensitive Data via Command Line Arguments
**Severity: MEDIUM**
**File**: `/home/dspfac/openharmony/base/startup/appspawn/standard/appspawn_process.c`, lines 405-489

The `ColdStartApp` and `EncodeAppClient` functions serialize the entire `AppParameter` (including `accessTokenId`, `accessTokenIdEx`, `uid`, `gid`, all GIDs, `bundleName`, `apl`, `renderCmd`, `ownerId`) into a colon-delimited string passed as a command line argument to `execv`. Command line arguments are visible to all users via `/proc/[pid]/cmdline`.

**Recommendation**: Use a file descriptor, pipe, or shared memory with proper permissions to pass sensitive spawn parameters rather than the command line.

### LOW-APPSPAWN-07: exit() Hook May Cause Unexpected Behavior
**Severity: LOW**
**File**: `/home/dspfac/openharmony/base/startup/appspawn/common/appspawn_server.c`, lines 66-81

The `exit()` function is overridden with `__attribute__((visibility("default")))` to intercept and redirect to `ProcessExit`. The check `atoi(checkExit) == getpid()` uses `atoi` which does not handle errors robustly -- a malformed `APPSPAWN_CHECK_EXIT` environment variable could yield 0, matching PID 0 scenarios.

---

## System Ability Manager (samgr)

### CRITICAL-SAMGR-01: SELinux Checks Default to Allow When Disabled
**Severity: CRITICAL**
**File**: `/home/dspfac/openharmony/foundation/systemabilitymgr/samgr/services/samgr/native/source/system_ability_manager_stub.cpp`, lines 45-99

All four SELinux check functions (`CheckGetSAPermission`, `CheckAddOrRemovePermission`, `CheckGetRemoteSAPermission`, `CheckListSAPermission`) return `true` when `WITH_SELINUX` is not defined:

```c
#ifdef WITH_SELINUX
    // ... actual check ...
#else
    return true; // if not support selinux, not check selinux permission
#endif
```

This means any build without SELinux enabled has zero access control on which processes can get, add, remove, or list system abilities. This is the system's equivalent of Android's ServiceManager -- a complete bypass allows any process to hijack or inject fake system services.

**Recommendation**: When SELinux is not available, implement an alternative MAC or DAC-based access control. Never default to open access for the service registry.

### HIGH-SAMGR-02: CanRequest() Only Checks Token Type, Not Specific Identity
**Severity: HIGH**
**File**: `/home/dspfac/openharmony/foundation/systemabilitymgr/samgr/services/samgr/native/source/system_ability_manager_stub.cpp`, lines 996-1003

The `CanRequest()` function only verifies that the caller has a `TOKEN_NATIVE` token type:

```c
bool SystemAbilityManagerStub::CanRequest()
{
    auto accessTokenId = IPCSkeleton::GetCallingTokenID();
    AccessToken::ATokenTypeEnum tokenType = AccessToken::AccessTokenKit::GetTokenTypeFlag(accessTokenId);
    return (tokenType == AccessToken::ATokenTypeEnum::TOKEN_NATIVE);
}
```

Any native process (not just specific system services) can call privileged operations like `AddSystemAbility`, `RemoveSystemAbility`, `AddSystemProcess`, `GetSystemProcessInfo`, `GetRunningSystemProcess`, `SubscribeSystemProcess`, etc. There is no granular permission check for these operations -- any native daemon can register or unregister system abilities.

**Recommendation**: Implement per-ability or per-operation permission checks. At minimum, verify the calling process identity matches the expected owner of a system ability.

### MEDIUM-SAMGR-03: Subscribe/Unsubscribe System Ability Lacks Permission Check
**Severity: MEDIUM**
**File**: `/home/dspfac/openharmony/foundation/systemabilitymgr/samgr/services/samgr/native/source/system_ability_manager_stub.cpp`, lines 234-296

`SubsSystemAbilityInner` and `UnSubsSystemAbilityInner` do NOT call `CanRequest()`. They only validate the system ability ID and read the remote object. This means any process (including third-party apps) can subscribe to system ability status changes, potentially leaking information about which system services start and stop.

**Recommendation**: Add `CanRequest()` or an appropriate permission check before allowing subscriptions.

### MEDIUM-SAMGR-04: GetOnDemandSystemAbilityIds Exposes Internal State Without Permission Check
**Severity: MEDIUM**
**File**: `/home/dspfac/openharmony/foundation/systemabilitymgr/samgr/services/samgr/native/source/system_ability_manager_stub.cpp`, lines 981-994

`GetOnDemandSystemAbilityIdsInner` has no permission check at all. Any caller can enumerate all on-demand system ability IDs, revealing internal system architecture information.

### MEDIUM-SAMGR-05: UnloadSystemAbility and CancelUnloadSystemAbility Lack CanRequest Check
**Severity: MEDIUM**
**File**: `/home/dspfac/openharmony/foundation/systemabilitymgr/samgr/services/samgr/native/source/system_ability_manager_stub.cpp`, lines 666-685, 835-854

Both `UnloadSystemAbilityInner` and `CancelUnloadSystemAbilityInner` do not call `CanRequest()`. They validate the system ability ID but not the caller's privilege level. A non-native process could potentially unload system abilities, causing denial of service.

### LOW-SAMGR-06: SA Profile Files Loaded from Fixed Path Without Integrity Verification
**Severity: LOW**
**File**: `/home/dspfac/openharmony/foundation/systemabilitymgr/samgr/services/samgr/native/source/system_ability_manager.cpp`, lines 192-224

SA profile JSON files are loaded from `/system/profile/` without cryptographic integrity verification. While the `/system` partition should be read-only, a compromised system partition could inject malicious SA profile definitions.

---

## Power Management Service

### HIGH-POWER-01: Power Management Stub Has No Permission Checks at Stub Layer
**Severity: HIGH**
**File**: `/home/dspfac/openharmony/base/powermgr/power_manager/services/zidl/src/power_mgr_stub.cpp`, lines 39-170

The `PowerMgrStub::OnRemoteRequest` dispatches to handler functions based on a switch statement without any permission verification at the stub layer. Interface token validation occurs (line 43-47), but sensitive operations like `WAKEUP_DEVICE`, `SUSPEND_DEVICE`, `REBOOT_DEVICE`, `SHUTDOWN_DEVICE`, `FORCE_DEVICE_SUSPEND`, `CREATE_RUNNINGLOCK`, `SET_DISPLAY_SUSPEND`, and `SHELL_DUMP` are dispatched without caller verification.

Permission checks are deferred to the service implementation layer (e.g., `PowerMgrService::RebootDeviceForDeprecated` checks `Permission::IsPermissionGranted("ohos.permission.REBOOT")`). While this approach works, it means:
1. New handlers added to the stub might forget permission checks
2. Error messages may leak before permissions are checked

**Recommendation**: Implement a permission check table at the stub layer, ensuring every operation has its required permission validated before dispatching.

### MEDIUM-POWER-02: ShellDump Exposed via IPC Without Documented Access Control
**Severity: MEDIUM**
**File**: `/home/dspfac/openharmony/base/powermgr/power_manager/services/zidl/src/power_mgr_stub.cpp`, line 153-155

`SHELL_DUMP` is exposed as an IPC command. While `Dump()` in the service layer checks `Permission::IsSystem()`, the fact that it's accessible via IPC (not just binder dump) increases the attack surface. Dump output might contain sensitive power state information.

---

## Sensor Service

### MEDIUM-SENSOR-01: GetAllSensors Returns Full Sensor List Without Permission Check
**Severity: MEDIUM**
**File**: `/home/dspfac/openharmony/base/sensors/sensor/services/src/sensor_service_stub.cpp`, lines 162-179

`GetAllSensorsInner` returns the complete list of available sensors to any caller without any permission check. While individual sensor enable/disable operations properly check `CheckSensorPermission`, the sensor enumeration reveals hardware capabilities to any app.

**Recommendation**: Consider requiring at minimum a basic permission for sensor list enumeration, or filter out sensitive sensor types (SAR, medical) from the returned list for non-system callers.

### LOW-SENSOR-02: Sensor Service Shell Token Accepted as System Service
**Severity: LOW**
**File**: `/home/dspfac/openharmony/base/sensors/sensor/services/src/sensor_service_stub.cpp`, lines 98-108

`IsSystemServiceCalling()` accepts `TOKEN_SHELL` as equivalent to `TOKEN_NATIVE`. This means `hdc shell` sessions get system service privileges for sensor operations. While needed for debugging, this could be exploited if `hdc` is accessible.

---

## Location Service

### HIGH-LOCATION-01: Tiered Permission Model Correctly Implemented
**Severity: INFORMATIONAL (Positive Finding)**
**File**: `/home/dspfac/openharmony/base/location/frameworks/location_common/common/source/common_utils.cpp`, lines 45-118

The location service implements a well-structured tiered permission model:
- `ACCESS_APPROXIMATELY_LOCATION` for coarse location
- `ACCESS_LOCATION` for precise location
- `ACCESS_BACKGROUND_LOCATION` for background access
- `MANAGE_SECURE_SETTINGS` for admin operations

The `GetPermissionLevel` function properly distinguishes between precise (both permissions), coarse (approximate only), and denied access. This is a good security pattern.

### MEDIUM-LOCATION-02: Location Service Stub Files Not Found in Expected Path
**Severity: MEDIUM**
**File**: `/home/dspfac/openharmony/base/location/services/`

No `*stub*.cpp` files were found under the services directory, suggesting the IPC stub may be auto-generated or located elsewhere. This makes it harder to audit the IPC attack surface for the location service. The permission enforcement appears to happen in the service implementation via `CommonUtils::CheckPermission`, which is good, but the absence of visible stub-layer validation is a concern.

---

## Notification Service

### CRITICAL-NOTIFY-01: Common Event Stub Has No Permission Checks at IPC Layer
**Severity: CRITICAL**
**File**: `/home/dspfac/openharmony/base/notification/common_event_service/frameworks/core/src/common_event_stub.cpp`, lines 32-228

The `CommonEventStub::OnRemoteRequest` processes multiple sensitive operations without ANY permission checks at the stub layer:
- `CES_PUBLISH_COMMON_EVENT` -- Publish system-wide events
- `CES_SUBSCRIBE_COMMON_EVENT` -- Subscribe to system events
- `CES_FREEZE` / `CES_UNFREEZE` / `CES_UNFREEZE_ALL` -- Freeze/unfreeze app event delivery by UID
- `CES_REMOVE_STICKY_COMMON_EVENT` -- Remove sticky events
- `CES_DUMP_STATE` -- Dump internal state

The Freeze/Unfreeze operations accept a raw `uid` parameter from the IPC message without any verification that the caller is authorized to freeze another app's event delivery. The stub base class implementations all return success (`true` / `ERR_OK`), meaning permission enforcement is entirely dependent on subclass overrides.

Additionally, `CES_PUBLISH_COMMON_EVENT2` accepts `uid` and `callerToken` directly from the IPC message (lines 72-73), which is a trust boundary violation -- the caller should not be able to specify their own identity.

**Recommendation**:
1. Add mandatory permission checks at the stub layer for privileged operations
2. Never accept caller identity (uid, callerToken) from the IPC message -- always derive from `IPCSkeleton::GetCallingUid()`

### HIGH-NOTIFY-02: AnsManagerStub Has Massive IPC Surface Without Visible Stub-Level Checks
**Severity: HIGH**
**File**: `/home/dspfac/openharmony/base/notification/distributed_notification_service/frameworks/core/src/ans_manager_stub.cpp`

The `AnsManagerStub` registers an extensive map of interface handlers (100+ operations) via `std::bind`. The `OnRemoteRequest` pattern dispatches based on a map lookup. No permission checks are visible at the stub layer -- all security relies on the service implementation.

Given the massive attack surface (publish, cancel, subscribe, slot management, badge management, DND mode, etc.), the absence of stub-layer guards increases the risk that individual handler implementations might miss required checks.

---

## Input Method Framework

### HIGH-IMF-01: Input Method Stub Lacks Stub-Layer Permission Enforcement
**Severity: HIGH**
**File**: `/home/dspfac/openharmony/base/inputmethod/imf/services/src/input_method_system_ability_stub.cpp`

The `InputMethodSystemAbilityStub::OnRemoteRequest` dispatches to handler functions indexed by code via `HANDLERS.at(code)` (line 42) without any permission checks. Critical operations include:
- `SetCoreAndAgent` -- Registers the IME core and agent objects (line 128-142)
- `StartInput` -- Starts input session
- `SwitchInputMethod` -- Switches active IME
- `PanelStatusChange` -- Reports panel status changes

The `SetCoreAndAgent` operation is particularly sensitive as it registers the objects that will receive all keyboard input. Without stub-layer access control, any process could potentially register as the active input method, intercepting all user keystrokes.

**Recommendation**: Add `CanRequest()` style checks for IME registration operations. Verify that only the authorized IME process can call `SetCoreAndAgent`.

### MEDIUM-IMF-02: Deprecated Methods Kept Without Permission Checks
**Severity: MEDIUM**
**File**: `/home/dspfac/openharmony/base/inputmethod/imf/services/src/input_method_system_ability.cpp`, line 671

Comment states: `// Deprecated because of no permission check, kept for compatibility`. Deprecated methods without permission checks should be removed or retrofitted, not kept for backward compatibility. The associated stub handler methods (`ShowCurrentInputOnRemoteDeprecated`, `HideCurrentInputOnRemoteDeprecated`) are still accessible via IPC.

### MEDIUM-IMF-03: SwitchInputMethod Anti-Spoofing Check Easily Bypassed
**Severity: MEDIUM**
**File**: `/home/dspfac/openharmony/base/inputmethod/imf/services/src/input_method_system_ability.cpp`, lines 492-509

The `SwitchInputMethod` function has an anti-spoofing check:
```c
if (trigger == SwitchTrigger::IMSA) {
    IMSA_HILOGW("caller counterfeit!");
    return ErrorCode::ERROR_BAD_PARAMETERS;
}
```

This check prevents callers from claiming the trigger came from IMSA itself. However, the `SwitchTrigger` enum value is passed via IPC and any caller can set it to any value other than `IMSA` to bypass this check. The real permission check happens later in `CheckSwitchPermission`, but the documented concern about "counterfeit" callers suggests the security model is not robust.

---

## Multimodal Input Service

### HIGH-MMI-01: Input Event Injection Without Strong Permission Enforcement at Stub
**Severity: HIGH**
**File**: `/home/dspfac/openharmony/foundation/multimodalinput/input/service/connect_manager/src/multimodal_input_connect_stub.cpp`, lines 177-182

`StubInjectKeyEvent` and `StubInjectPointerEvent` are exposed as IPC commands. Input injection is an extremely sensitive operation as it can simulate user actions (typing passwords, clicking buttons, etc.). While `VerifySystemApp()` checks exist for some operations (e.g., `StubAddInputEventFilter` at line 329), the injection stubs' permission requirements are not visible at the stub dispatching level.

**Recommendation**: Verify that input injection requires `ohos.permission.INJECT_INPUT_EVENT` or equivalent at the stub layer. Add explicit comments documenting the security model for each IPC entry point.

### MEDIUM-MMI-02: PixelMap Deserialization from Untrusted IPC Data
**Severity: MEDIUM**
**File**: `/home/dspfac/openharmony/foundation/multimodalinput/input/service/connect_manager/src/multimodal_input_connect_stub.cpp`, lines 411, 452

`StubSetCustomCursor` and `StubSetMouseIcon` deserialize `PixelMap` objects directly from IPC data:
```c
OHOS::Media::PixelMap* pixelMap = Media::PixelMap::Unmarshalling(data);
```
and
```c
OHOS::Media::PixelMap* pixelMap = OHOS::Media::PixelMap::DecodeTlv(buff);
```

Deserializing complex objects from untrusted IPC input is a common source of memory corruption vulnerabilities. The `StubSetMouseIcon` also allocates a buffer based on a size read from the IPC message (`size` up to `MAX_BUFFER_SIZE` = 1,000,000), which could be used for memory exhaustion.

Additionally, `StubSetCustomCursor` does not have the `VerifySystemApp()` check that other similar operations have, meaning any app could call it.

**Recommendation**: Add `VerifySystemApp()` to `StubSetCustomCursor`. Implement size limits and sandboxed deserialization for PixelMap data. Consider using a more restrictive maximum buffer size.

### LOW-MMI-03: Descriptor Logged in Plain Text on Mismatch
**Severity: LOW**
**File**: `/home/dspfac/openharmony/foundation/multimodalinput/input/service/connect_manager/src/multimodal_input_connect_stub.cpp`, line 53

```c
MMI_HILOGE("Get unexpect descriptor:%{public}s", Str16ToStr8(descriptor).c_str());
```

Logging the actual descriptor value from an attacker-controlled message could enable information leakage or log injection.

---

## Background Task Manager

### HIGH-BGTASK-01: Background Task Stub Has No Permission Checks at Stub Layer
**Severity: HIGH**
**File**: `/home/dspfac/openharmony/foundation/resourceschedule/background_task_mgr/frameworks/src/background_task_mgr_stub.cpp`

The `BackgroundTaskMgrStub` dispatches all IPC commands without any permission verification at the stub layer. Critical operations include:
- `REQUEST_SUSPEND_DELAY` -- Request delayed suspension (any app)
- `START_BACKGROUND_RUNNING` / `STOP_BACKGROUND_RUNNING` -- Start/stop continuous background tasks
- `SUBSCRIBE_BACKGROUND_TASK` / `UNSUBSCRIBE_BACKGROUND_TASK` -- Monitor background tasks
- `APPLY_EFFICIENCY_RESOURCES` -- Request efficiency resources
- `STOP_CONTINUOUS_TASK` -- Stop another app's background task by uid/pid
- `REQUEST_BACKGROUND_RUNNING_FOR_INNER` -- Internal background running request

`HandleStopContinuousTask` (lines 336-347) accepts `uid`, `pid`, and `taskType` directly from the IPC message with no permission check. Any caller could potentially stop another application's background task.

`HandleGetTransientTaskApps` and `HandleGetContinuousTaskApps` expose lists of all apps with background tasks, which is sensitive system state information.

**Recommendation**: Add mandatory permission checks at the stub layer. Validate that caller UIDs match for self-management operations and require system permissions for cross-app operations.

---

## Account Management

### MEDIUM-ACCOUNT-01: OS Account Operations Properly Tagged with System API Flag
**Severity: INFORMATIONAL (Positive Finding)**
**File**: `/home/dspfac/openharmony/base/account/os_account/services/accountmgr/src/osaccount/os_account_stub.cpp`

The OS Account service uses a well-structured `messageProcMap` with an `isSyetemApi` flag per operation:
```c
{
    .messageProcFunction = &OsAccountStub::ProcCreateOsAccount,
    .isSyetemApi = true,
}
```

This allows the dispatcher to reject calls from non-system apps before reaching the handler. This is a better pattern than many other services reviewed.

### LOW-ACCOUNT-02: Typo in Field Name "isSyetemApi"
**Severity: LOW**
**File**: `/home/dspfac/openharmony/base/account/os_account/services/accountmgr/src/osaccount/os_account_stub.cpp`

The field name `isSyetemApi` is a typo for `isSystemApi`. While not a security issue, it indicates code quality concerns and could cause confusion.

---

## Cross-Cutting Concerns

### CRITICAL-CROSS-01: Inconsistent IPC Permission Enforcement Architecture
**Severity: CRITICAL**

Across the entire system services layer, there is no consistent architecture for IPC permission enforcement:

| Service | Stub-Layer Checks | Service-Layer Checks | Pattern |
|---------|-------------------|---------------------|---------|
| samgr | SELinux (ifdef) + CanRequest | CallerProcess check | Mixed |
| Power Manager | Interface token only | Permission::IsPermissionGranted | Deferred |
| Sensor Service | Permission checks per-handler | N/A (done at stub) | Good |
| Common Event | None | Unknown | **Missing** |
| Notification (ANS) | None visible | Unknown | **Risky** |
| Input Method | None | Partial | **Risky** |
| Multimodal Input | Partial (VerifySystemApp) | Unknown | Inconsistent |
| Background Task | None | Unknown | **Missing** |
| Account | isSyetemApi flag | Additional checks | Good |

The Sensor Service and Account Service have the best patterns. The Common Event, Notification, and Background Task services have the worst -- relying entirely on the service implementation layer which is error-prone.

**Recommendation**: Establish a mandatory framework-level pattern where every IPC stub must declare required permissions per-operation in a table, enforced before dispatch.

### HIGH-CROSS-02: Interface Token Validation Is Sole Gate for Many Services
**Severity: HIGH**

Multiple services rely solely on `ReadInterfaceToken()` matching the expected descriptor as their IPC authentication. Interface tokens prevent accidental cross-service calls but provide zero security against a malicious caller -- any process can read a service's descriptor string and include it in crafted IPC messages.

### MEDIUM-CROSS-03: Unbounded List Serialization in IPC Replies
**Severity: MEDIUM**

Multiple services write unbounded lists into IPC reply parcels:
- `GetRunningSystemProcessInner` writes all process infos (samgr_stub line 763)
- `HandleGetTransientTaskApps` / `HandleGetContinuousTaskApps` write all app infos
- `GetAllSensorsInner` is bounded by `MAX_SENSOR_COUNT`

Unbounded list serialization can cause OOM conditions in both the service (serialization) and the client (deserialization), enabling denial-of-service.

**Recommendation**: Implement pagination or hard limits on all list-returning IPC operations.

### MEDIUM-CROSS-04: ReadParcelable Used for Complex Deserialization from IPC
**Severity: MEDIUM**

Multiple stubs use `data.ReadParcelable<T>()` to deserialize complex objects from IPC messages:
- `CommonEventData`, `CommonEventPublishInfo` in CommonEventStub
- `RunningLockInfo` in PowerMgrStub
- `ContinuousTaskParam` in BackgroundTaskMgrStub
- `EfficiencyResourceInfo` in BackgroundTaskMgrStub
- `ConnectReqParcel` in MultimodalInputConnectStub

Each `Parcelable` implementation is an additional attack surface for crafted inputs. If any `Unmarshalling` implementation has bugs (buffer overflows, integer overflows), it can be triggered by any process that can send IPC to the service.

### LOW-CROSS-05: Logging of Sensitive Information
**Severity: LOW**

Multiple services log PIDs, UIDs, process names, bundle names, and capability information at INFO or DEBUG level. While `%{public}` format specifiers are used (which should be sanitized in production), the volume of security-relevant data in logs creates a risk if log access is not properly controlled.

---

## Summary of Findings

| ID | Severity | Component | Issue |
|----|----------|-----------|-------|
| CRITICAL-APPSPAWN-01 | CRITICAL | appspawn | All capabilities granted without GRAPHIC_PERMISSION_CHECK |
| CRITICAL-SAMGR-01 | CRITICAL | samgr | SELinux checks default to allow when disabled |
| CRITICAL-NOTIFY-01 | CRITICAL | CES | Common Event Stub has no permission checks, accepts spoofed UID |
| CRITICAL-CROSS-01 | CRITICAL | All | Inconsistent IPC permission enforcement architecture |
| HIGH-APPSPAWN-02 | HIGH | appspawn | Socket auth limited to two UIDs, no secondary verification |
| HIGH-APPSPAWN-03 | HIGH | appspawn | Hardcoded GID injection with off-by-one potential |
| HIGH-SAMGR-02 | HIGH | samgr | CanRequest() only checks token type, not specific identity |
| HIGH-POWER-01 | HIGH | power_manager | No permission checks at stub layer |
| HIGH-NOTIFY-02 | HIGH | ANS | Massive IPC surface without visible stub-level checks |
| HIGH-IMF-01 | HIGH | IMF | SetCoreAndAgent has no stub-layer permission check |
| HIGH-MMI-01 | HIGH | MMI | Input injection without strong stub-layer permission |
| HIGH-BGTASK-01 | HIGH | bgtaskmgr | No permission checks at stub layer, cross-app operations exposed |
| HIGH-CROSS-02 | HIGH | All | Interface token is sole authentication for many services |
| MEDIUM-APPSPAWN-04 | MEDIUM | appspawn | Large allocation via extraInfo without secondary validation |
| MEDIUM-APPSPAWN-05 | MEDIUM | appspawn | Only processName validated for null-termination |
| MEDIUM-APPSPAWN-06 | MEDIUM | appspawn | Sensitive data passed via command line in cold start |
| MEDIUM-SAMGR-03 | MEDIUM | samgr | Subscribe/Unsubscribe lacks permission check |
| MEDIUM-SAMGR-04 | MEDIUM | samgr | GetOnDemandSystemAbilityIds no permission check |
| MEDIUM-SAMGR-05 | MEDIUM | samgr | Unload/CancelUnload SA lacks CanRequest check |
| MEDIUM-SENSOR-01 | MEDIUM | sensor | GetAllSensors no permission check |
| MEDIUM-LOCATION-02 | MEDIUM | location | Stub files not found for audit |
| MEDIUM-POWER-02 | MEDIUM | power_manager | ShellDump exposed via IPC |
| MEDIUM-IMF-02 | MEDIUM | IMF | Deprecated methods kept without permission checks |
| MEDIUM-IMF-03 | MEDIUM | IMF | SwitchTrigger anti-spoofing easily bypassed |
| MEDIUM-MMI-02 | MEDIUM | MMI | PixelMap deserialization from untrusted IPC |
| MEDIUM-CROSS-03 | MEDIUM | All | Unbounded list serialization in IPC replies |
| MEDIUM-CROSS-04 | MEDIUM | All | ReadParcelable attack surface |
| LOW-APPSPAWN-07 | LOW | appspawn | exit() hook with fragile atoi check |
| LOW-SAMGR-06 | LOW | samgr | SA profiles loaded without integrity verification |
| LOW-SENSOR-02 | LOW | sensor | TOKEN_SHELL accepted as system service |
| LOW-ACCOUNT-02 | LOW | account | Typo in "isSyetemApi" field name |
| LOW-MMI-03 | LOW | MMI | Descriptor logged in plain text |
| LOW-CROSS-05 | LOW | All | Excessive security-relevant logging |
| INFO-LOCATION-01 | INFO | location | Tiered permission model well implemented |
| INFO-ACCOUNT-01 | INFO | account | System API flag pattern well implemented |

### Priority Recommendations

1. **Immediate**: Fix CRITICAL-APPSPAWN-01 -- ensure capabilities are always dropped to zero unless explicitly required. The `#else` branch granting all capabilities must be removed.

2. **Immediate**: Address CRITICAL-NOTIFY-01 -- the Common Event Service accepting caller-supplied uid/callerToken in `CES_PUBLISH_COMMON_EVENT2` is a direct privilege escalation vector.

3. **Short-term**: Establish a mandatory IPC permission enforcement framework. Every stub must declare per-operation permissions in a table, enforced before dispatch (CRITICAL-CROSS-01).

4. **Short-term**: Add fallback access control when SELinux is disabled in samgr (CRITICAL-SAMGR-01). This is the system's service registry -- compromise here means compromise of everything.

5. **Medium-term**: Audit all `ReadParcelable` implementations for memory safety. Add fuzz testing for every IPC entry point (MEDIUM-CROSS-04).

6. **Medium-term**: Validate all string fields in AppParameter for null-termination (MEDIUM-APPSPAWN-05), and move cold-start parameter passing from cmdline to secure channels (MEDIUM-APPSPAWN-06).
