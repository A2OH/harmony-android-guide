# SKILL: android.app.ActivityManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.ActivityManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.ActivityManager` |
| **Package** | `android.app` |
| **Total Methods** | 27 |
| **Avg Score** | 2.8 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 20 (74%) |
| **No Mapping** | 7 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isLowRamDevice` | `boolean isLowRamDevice()` | 5 | partial | moderate | `isRamConstrainedDevice` | `isRamConstrainedDevice(): Promise<boolean>` |
| `getMemoryClass` | `int getMemoryClass()` | 5 | partial | moderate | `getAppMemorySize` | `getAppMemorySize(): Promise<number>` |
| `getLargeMemoryClass` | `int getLargeMemoryClass()` | 5 | partial | moderate | `getAppMemorySize` | `getAppMemorySize(): Promise<number>` |

## Stub APIs (score < 5): 24 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getMyMemoryState` | 4 | partial | Return safe default (null/false/0/empty) |
| `getRunningAppProcesses` | 4 | partial | Return safe default (null/false/0/empty) |
| `getProcessesInErrorState` | 4 | partial | Return safe default (null/false/0/empty) |
| `getAppTaskThumbnailSize` | 4 | partial | Return safe default (null/false/0/empty) |
| `addAppTask` | 4 | partial | Log warning + no-op |
| `getMemoryInfo` | 4 | composite | Return safe default (null/false/0/empty) |
| `clearWatchHeapLimit` | 3 | composite | throw UnsupportedOperationException |
| `clearApplicationUserData` | 3 | composite | Store callback, never fire |
| `getProcessMemoryInfo` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDeviceConfigurationInfo` | 3 | composite | Return safe default (null/false/0/empty) |
| `isRunningInUserTestHarness` | 3 | composite | Return safe default (null/false/0/empty) |
| `setProcessStateSummary` | 3 | composite | Log warning + no-op |
| `isBackgroundRestricted` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRunningServiceControlPanel` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAppTasks` | 2 | composite | Return safe default (null/false/0/empty) |
| `isUserAMonkey` | 2 | composite | Return safe default (null/false/0/empty) |
| `getLauncherLargeIconDensity` | 2 | composite | Return safe default (null/false/0/empty) |
| `appNotResponding` | 1 | none | Store callback, never fire |
| `getLauncherLargeIconSize` | 1 | none | Return safe default (null/false/0/empty) |
| `getLockTaskModeState` | 1 | none | Return safe default (null/false/0/empty) |
| `isActivityStartAllowedOnDisplay` | 1 | none | Return dummy instance / no-op |
| `isLowMemoryKillReportSupported` | 1 | none | Return safe default (null/false/0/empty) |
| `setVrThread` | 1 | none | Return safe default (null/false/0/empty) |
| `setWatchHeapLimit` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.app.ActivityManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.ActivityManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 27 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
