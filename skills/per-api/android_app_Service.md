# SKILL: android.app.Service

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Service`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Service` |
| **Package** | `android.app` |
| **Total Methods** | 20 |
| **Avg Score** | 5.0 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 7 (35%) |
| **Partial/Composite** | 8 (40%) |
| **No Mapping** | 5 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 4 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 11 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onCreate` | `void onCreate()` | 9 | direct | easy | `onCreate` | `@ohos.app.ability.ServiceExtensionAbility` |
| `onDestroy` | `void onDestroy()` | 9 | direct | easy | `onDestroy` | `@ohos.app.ability.ServiceExtensionAbility` |
| `onRebind` | `void onRebind(android.content.Intent)` | 9 | direct | moderate | `onReconnect` | `@ohos.app.ability.ServiceExtensionAbility` |
| `onStartCommand` | `int onStartCommand(android.content.Intent, int, int)` | 9 | direct | hard | `onRequest` | `@ohos.app.ability.ServiceExtensionAbility` |
| `onUnbind` | `boolean onUnbind(android.content.Intent)` | 9 | direct | moderate | `onDisconnect` | `@ohos.app.ability.ServiceExtensionAbility` |
| `stopSelf` | `final void stopSelf()` | 9 | direct | impossible | `terminateSelf` | `@ohos.app.ability.ServiceExtensionContext` |
| `stopSelf` | `final void stopSelf(int)` | 9 | direct | impossible | `terminateSelf` | `@ohos.app.ability.ServiceExtensionContext` |
| `startForeground` | `final void startForeground(int, android.app.Notification)` | 5 | partial | hard | `startBackgroundRunning` | `@ohos.backgroundTaskManager.` |
| `startForeground` | `final void startForeground(int, @NonNull android.app.Notification, int)` | 5 | partial | hard | `startBackgroundRunning` | `@ohos.backgroundTaskManager.` |
| `stopForeground` | `final void stopForeground(boolean)` | 5 | partial | hard | `stopBackgroundRunning` | `@ohos.backgroundTaskManager.` |
| `stopForeground` | `final void stopForeground(int)` | 5 | partial | hard | `stopBackgroundRunning` | `@ohos.backgroundTaskManager.` |

## Gap Descriptions (per method)

- **`onCreate`**: Lifecycle
- **`onDestroy`**: Lifecycle
- **`onRebind`**: Direct equivalent for reconnection
- **`onStartCommand`**: Lifecycle
- **`onUnbind`**: Lifecycle
- **`stopSelf`**: Direct self-termination
- **`stopSelf`**: Direct self-termination
- **`startForeground`**: Continuous task for foreground service
- **`startForeground`**: Continuous task for foreground service
- **`stopForeground`**: Stop continuous background task
- **`stopForeground`**: Stop continuous background task

## Stub APIs (score < 5): 9 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onConfigurationChanged` | 3 | composite | Store callback, never fire |
| `getApplication` | 3 | composite | Return safe default (null/false/0/empty) |
| `getForegroundServiceType` | 3 | composite | Return safe default (null/false/0/empty) |
| `stopSelfResult` | 3 | composite | No-op |
| `Service` | 1 | none | throw UnsupportedOperationException |
| `dump` | 1 | none | throw UnsupportedOperationException |
| `onLowMemory` | 1 | none | Store callback, never fire |
| `onTaskRemoved` | 1 | none | Log warning + no-op |
| `onTrimMemory` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Service`:

- `android.content.Context` (already shimmed)
- `android.content.Intent` (already shimmed)

## Quality Gates

Before marking `android.app.Service` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 20 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 11 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
