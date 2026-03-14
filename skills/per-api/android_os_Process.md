# SKILL: android.os.Process

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Process`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Process` |
| **Package** | `android.os` |
| **Total Methods** | 19 |
| **Avg Score** | 4.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 18 (94%) |
| **No Mapping** | 1 (5%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `myPid` | `static final int myPid()` | 6 | partial | moderate | `pid` | `readonly pid: number` |
| `is64Bit` | `static final boolean is64Bit()` | 6 | partial | moderate | `is64Bit` | `is64Bit(): boolean` |
| `getStartElapsedRealtime` | `static final long getStartElapsedRealtime()` | 5 | partial | moderate | `getStartRealtime` | `getStartRealtime(): number` |
| `myUid` | `static final int myUid()` | 5 | partial | moderate | `uid` | `uid: number` |
| `Process` | `Process()` | 5 | partial | moderate | `process` | `readonly process: string` |
| `getElapsedCpuTime` | `static final long getElapsedCpuTime()` | 5 | partial | moderate | `getPastCpuTime` | `getPastCpuTime(): number` |
| `isIsolated` | `static final boolean isIsolated()` | 5 | partial | moderate | `isIsolatedProcess` | `isIsolatedProcess(): boolean` |
| `getStartUptimeMillis` | `static final long getStartUptimeMillis()` | 5 | partial | moderate | `getStartRealtime` | `getStartRealtime(): number` |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `killProcess` | 5 | partial | throw UnsupportedOperationException |
| `myUserHandle` | 4 | partial | throw UnsupportedOperationException |
| `getThreadPriority` | 4 | partial | Return safe default (null/false/0/empty) |
| `getUidForName` | 4 | partial | Return safe default (null/false/0/empty) |
| `getExclusiveCores` | 4 | partial | Return safe default (null/false/0/empty) |
| `getGidForName` | 4 | composite | Return safe default (null/false/0/empty) |
| `isApplicationUid` | 4 | composite | Return safe default (null/false/0/empty) |
| `setThreadPriority` | 3 | composite | Return safe default (null/false/0/empty) |
| `setThreadPriority` | 3 | composite | Return safe default (null/false/0/empty) |
| `sendSignal` | 3 | composite | throw UnsupportedOperationException |
| `myTid` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 8 methods that have score >= 5
2. Stub 11 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Process`:


## Quality Gates

Before marking `android.os.Process` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 19 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
