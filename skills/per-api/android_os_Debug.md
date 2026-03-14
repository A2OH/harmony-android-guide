# SKILL: android.os.Debug

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Debug`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Debug` |
| **Package** | `android.os` |
| **Total Methods** | 30 |
| **Avg Score** | 3.5 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 27 (90%) |
| **No Mapping** | 3 (10%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isDebuggerConnected` | `static boolean isDebuggerConnected()` | 5 | partial | moderate | `isConnected` | `isConnected(): boolean` |

## Stub APIs (score < 5): 29 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getNativeHeapAllocatedSize` | 5 | partial | Return safe default (null/false/0/empty) |
| `getNativeHeapFreeSize` | 5 | partial | Return safe default (null/false/0/empty) |
| `getNativeHeapSize` | 5 | partial | Return safe default (null/false/0/empty) |
| `getPss` | 5 | partial | Return safe default (null/false/0/empty) |
| `threadCpuTimeNanos` | 5 | partial | Return safe default (null/false/0/empty) |
| `getRuntimeStats` | 4 | partial | Return safe default (null/false/0/empty) |
| `getBinderSentTransactions` | 4 | partial | Return safe default (null/false/0/empty) |
| `getBinderReceivedTransactions` | 4 | partial | Return safe default (null/false/0/empty) |
| `stopMethodTracing` | 4 | partial | No-op |
| `dumpHprofData` | 4 | composite | throw UnsupportedOperationException |
| `getMemoryInfo` | 4 | composite | Return safe default (null/false/0/empty) |
| `startMethodTracing` | 3 | composite | Return dummy instance / no-op |
| `startMethodTracing` | 3 | composite | Return dummy instance / no-op |
| `startMethodTracing` | 3 | composite | Return dummy instance / no-op |
| `startMethodTracing` | 3 | composite | Return dummy instance / no-op |
| `startNativeTracing` | 3 | composite | Return dummy instance / no-op |
| `stopNativeTracing` | 3 | composite | No-op |
| `getBinderDeathObjectCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getBinderLocalObjectCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getBinderProxyObjectCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `startMethodTracingSampling` | 3 | composite | Return dummy instance / no-op |
| `enableEmulatorTraceOutput` | 3 | composite | Log warning + no-op |
| `getRuntimeStat` | 3 | composite | Return safe default (null/false/0/empty) |
| `getLoadedClassCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `dumpService` | 3 | composite | throw UnsupportedOperationException |
| `attachJvmtiAgent` | 3 | composite | throw UnsupportedOperationException |
| `printLoadedClasses` | 1 | none | throw UnsupportedOperationException |
| `waitForDebugger` | 1 | none | throw UnsupportedOperationException |
| `waitingForDebugger` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Debug`:


## Quality Gates

Before marking `android.os.Debug` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 30 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
