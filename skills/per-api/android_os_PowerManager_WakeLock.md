# SKILL: android.os.PowerManager.WakeLock

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.PowerManager.WakeLock`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.PowerManager.WakeLock` |
| **Package** | `android.os.PowerManager` |
| **Total Methods** | 7 |
| **Avg Score** | 7.4 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 5 (71%) |
| **Partial/Composite** | 2 (28%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `acquire` | `void acquire()` | 9 | direct | moderate | `lock` | `isRunningLockTypeSupported(type: RunningLockType, callback: AsyncCallback<boolean>): void` |
| `acquire` | `void acquire(long)` | 9 | direct | moderate | `lock` | `isRunningLockTypeSupported(type: RunningLockType, callback: AsyncCallback<boolean>): void` |
| `isHeld` | `boolean isHeld()` | 9 | direct | moderate | `isHolding` | `@ohos.runningLock.RunningLock` |
| `release` | `void release()` | 9 | direct | trivial | `unlock` | `BACKGROUND = 1` |
| `release` | `void release(int)` | 9 | direct | trivial | `unlock` | `BACKGROUND = 1` |

## Gap Descriptions (per method)

- **`acquire`**: Wake lock
- **`acquire`**: Wake lock
- **`isHeld`**: Direct equivalent
- **`release`**: Wake lock
- **`release`**: Wake lock

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setReferenceCounted` | 3 | composite | Log warning + no-op |
| `setWorkSource` | 3 | composite | Log warning + no-op |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.os.PowerManager.WakeLock`:


## Quality Gates

Before marking `android.os.PowerManager.WakeLock` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
