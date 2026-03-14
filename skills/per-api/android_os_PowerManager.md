# SKILL: android.os.PowerManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.PowerManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.PowerManager` |
| **Package** | `android.os` |
| **Total Methods** | 14 |
| **Avg Score** | 4.2 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 3 (21%) |
| **Partial/Composite** | 11 (78%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 1 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isInteractive` | `boolean isInteractive()` | 9 | direct | easy | `isActive` | `isActive(): boolean` |
| `isPowerSaveMode` | `boolean isPowerSaveMode()` | 7 | near | rewrite | `getPowerMode` | `@ohos.power.power` |
| `newWakeLock` | `android.os.PowerManager.WakeLock newWakeLock(int, String)` | 7 | near | rewrite | `create` | `@ohos.runningLock.runningLock` |

## Gap Descriptions (per method)

- **`isInteractive`**: Interactive state
- **`isPowerSaveMode`**: Compare to MODE_POWER_SAVE
- **`newWakeLock`**: RunningLock = WakeLock

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isDeviceIdleMode` | 4 | partial | Return safe default (null/false/0/empty) |
| `getLocationPowerSaveMode` | 4 | partial | Return safe default (null/false/0/empty) |
| `getThermalHeadroom` | 4 | partial | Return safe default (null/false/0/empty) |
| `isIgnoringBatteryOptimizations` | 4 | partial | Return safe default (null/false/0/empty) |
| `isRebootingUserspaceSupported` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCurrentThermalStatus` | 3 | composite | Return safe default (null/false/0/empty) |
| `isWakeLockLevelSupported` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeThermalStatusListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `addThermalStatusListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `addThermalStatusListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `isSustainedPerformanceModeSupported` | 3 | composite | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.os.PowerManager`:


## Quality Gates

Before marking `android.os.PowerManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
