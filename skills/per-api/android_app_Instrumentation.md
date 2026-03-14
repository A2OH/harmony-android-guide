# SKILL: android.app.Instrumentation

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Instrumentation`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Instrumentation` |
| **Package** | `android.app` |
| **Total Methods** | 66 |
| **Avg Score** | 1.8 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 21 (31%) |
| **No Mapping** | 45 (68%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onCreate` | `void onCreate(android.os.Bundle)` | 5 | partial | moderate | `onPrepare` | `onPrepare(): void` |
| `onDestroy` | `void onDestroy()` | 5 | partial | moderate | `onDestroy` | `onDestroy?: () => void` |
| `stopProfiling` | `void stopProfiling()` | 5 | partial | moderate | `stopProfiling` | `stopProfiling(): void` |

## Stub APIs (score < 5): 63 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getContext` | 5 | partial | Return safe default (null/false/0/empty) |
| `getTargetContext` | 4 | partial | Return safe default (null/false/0/empty) |
| `getAllocCounts` | 4 | partial | Return safe default (null/false/0/empty) |
| `callApplicationOnCreate` | 3 | composite | Return dummy instance / no-op |
| `finish` | 3 | composite | Return safe default (null/false/0/empty) |
| `start` | 3 | composite | Return dummy instance / no-op |
| `startProfiling` | 3 | composite | Return dummy instance / no-op |
| `onStart` | 3 | composite | Return dummy instance / no-op |
| `startActivitySync` | 3 | composite | Return dummy instance / no-op |
| `acquireLooperManager` | 3 | composite | throw UnsupportedOperationException |
| `getProcessName` | 3 | composite | Return safe default (null/false/0/empty) |
| `getComponentName` | 3 | composite | Return safe default (null/false/0/empty) |
| `newApplication` | 3 | composite | Store callback, never fire |
| `newApplication` | 3 | composite | Store callback, never fire |
| `startPerformanceSnapshot` | 3 | composite | Return dummy instance / no-op |
| `getBinderCounts` | 3 | composite | Return safe default (null/false/0/empty) |
| `getUiAutomation` | 3 | composite | Return safe default (null/false/0/empty) |
| `getUiAutomation` | 3 | composite | Return safe default (null/false/0/empty) |
| `Instrumentation` | 1 | none | Store callback, never fire |
| `addMonitor` | 1 | none | Log warning + no-op |
| `addMonitor` | 1 | none | Log warning + no-op |
| `addMonitor` | 1 | none | Log warning + no-op |
| `addResults` | 1 | none | Log warning + no-op |
| `callActivityOnCreate` | 1 | none | Return dummy instance / no-op |
| `callActivityOnCreate` | 1 | none | Return dummy instance / no-op |
| `callActivityOnDestroy` | 1 | none | No-op |
| `callActivityOnNewIntent` | 1 | none | Store callback, never fire |
| `callActivityOnPause` | 1 | none | Store callback, never fire |
| `callActivityOnPictureInPictureRequested` | 1 | none | Store callback, never fire |
| `callActivityOnPostCreate` | 1 | none | Return dummy instance / no-op |
| `callActivityOnPostCreate` | 1 | none | Return dummy instance / no-op |
| `callActivityOnRestart` | 1 | none | Return dummy instance / no-op |
| `callActivityOnRestoreInstanceState` | 1 | none | Store callback, never fire |
| `callActivityOnRestoreInstanceState` | 1 | none | Store callback, never fire |
| `callActivityOnResume` | 1 | none | Store callback, never fire |
| `callActivityOnSaveInstanceState` | 1 | none | Store callback, never fire |
| `callActivityOnSaveInstanceState` | 1 | none | Store callback, never fire |
| `callActivityOnStart` | 1 | none | Return dummy instance / no-op |
| `callActivityOnStop` | 1 | none | No-op |
| `callActivityOnUserLeaving` | 1 | none | Store callback, never fire |
| `checkMonitorHit` | 1 | none | Store callback, never fire |
| `endPerformanceSnapshot` | 1 | none | throw UnsupportedOperationException |
| `invokeContextMenuAction` | 1 | none | Store callback, never fire |
| `invokeMenuActionSync` | 1 | none | Store callback, never fire |
| `isProfiling` | 1 | none | Return safe default (null/false/0/empty) |
| `newActivity` | 1 | none | throw UnsupportedOperationException |
| `newActivity` | 1 | none | throw UnsupportedOperationException |
| `onException` | 1 | none | Store callback, never fire |
| `removeMonitor` | 1 | none | Log warning + no-op |
| `runOnMainSync` | 1 | none | Store callback, never fire |
| `sendCharacterSync` | 1 | none | throw UnsupportedOperationException |
| `sendKeyDownUpSync` | 1 | none | throw UnsupportedOperationException |
| `sendKeySync` | 1 | none | throw UnsupportedOperationException |
| `sendPointerSync` | 1 | none | throw UnsupportedOperationException |
| `sendStatus` | 1 | none | throw UnsupportedOperationException |
| `sendStringSync` | 1 | none | throw UnsupportedOperationException |
| `sendTrackballEventSync` | 1 | none | throw UnsupportedOperationException |
| `setAutomaticPerformanceSnapshots` | 1 | none | Log warning + no-op |
| `setInTouchMode` | 1 | none | Log warning + no-op |
| `waitForIdle` | 1 | none | throw UnsupportedOperationException |
| `waitForIdleSync` | 1 | none | throw UnsupportedOperationException |
| `waitForMonitor` | 1 | none | Store callback, never fire |
| `waitForMonitorWithTimeout` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Instrumentation`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.Instrumentation` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 66 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
