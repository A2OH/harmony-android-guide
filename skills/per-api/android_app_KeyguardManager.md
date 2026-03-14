# SKILL: android.app.KeyguardManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.KeyguardManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.KeyguardManager` |
| **Package** | `android.app` |
| **Total Methods** | 9 |
| **Avg Score** | 3.6 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 2 (22%) |
| **Partial/Composite** | 3 (33%) |
| **No Mapping** | 4 (44%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isDeviceSecure` | `boolean isDeviceSecure()` | 9 | direct | moderate | `isSecure` | `isSecureMode(callback: AsyncCallback<boolean>): void` |
| `isKeyguardLocked` | `boolean isKeyguardLocked()` | 9 | direct | easy | `isLocked` | `isLocked(): boolean` |

## Gap Descriptions (per method)

- **`isDeviceSecure`**: Secure state
- **`isKeyguardLocked`**: Lock state

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onDismissSucceeded` | 5 | partial | Return safe default (null/false/0/empty) |
| `requestDismissKeyguard` | 3 | composite | Return safe default (null/false/0/empty) |
| `KeyguardDismissCallback` | 3 | composite | Return safe default (null/false/0/empty) |
| `isDeviceLocked` | 1 | none | Return safe default (null/false/0/empty) |
| `isKeyguardSecure` | 1 | none | Return safe default (null/false/0/empty) |
| `onDismissCancelled` | 1 | none | Return safe default (null/false/0/empty) |
| `onDismissError` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.app.KeyguardManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.KeyguardManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
