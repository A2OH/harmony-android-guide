# SKILL: android.util.Half

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.Half`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.Half` |
| **Package** | `android.util` |
| **Total Methods** | 26 |
| **Avg Score** | 2.6 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 14 (53%) |
| **No Mapping** | 12 (46%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `intValue` | `int intValue()` | 5 | partial | moderate | `value` | `value: number` |
| `isNormalized` | `static boolean isNormalized(@HalfFloat short)` | 5 | partial | moderate | `isEnabled` | `readonly isEnabled?: boolean` |
| `longValue` | `long longValue()` | 5 | partial | moderate | `value` | `value: number` |
| `isInfinite` | `static boolean isInfinite(@HalfFloat short)` | 5 | partial | moderate | `isWifiActive` | `isWifiActive(): boolean` |
| `isNaN` | `boolean isNaN()` | 5 | partial | moderate | `isInSandbox` | `isInSandbox(): Promise<boolean>` |
| `isNaN` | `static boolean isNaN(@HalfFloat short)` | 5 | partial | moderate | `isInSandbox` | `isInSandbox(): Promise<boolean>` |

## Stub APIs (score < 5): 20 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `doubleValue` | 4 | partial | throw UnsupportedOperationException |
| `compare` | 3 | composite | throw UnsupportedOperationException |
| `compareTo` | 3 | composite | throw UnsupportedOperationException |
| `getSign` | 3 | composite | Return safe default (null/false/0/empty) |
| `floatValue` | 3 | composite | throw UnsupportedOperationException |
| `hashCode` | 3 | composite | Return safe default (null/false/0/empty) |
| `getExponent` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSignificand` | 2 | composite | Return safe default (null/false/0/empty) |
| `Half` | 1 | none | throw UnsupportedOperationException |
| `Half` | 1 | none | throw UnsupportedOperationException |
| `Half` | 1 | none | throw UnsupportedOperationException |
| `Half` | 1 | none | throw UnsupportedOperationException |
| `equals` | 1 | none | throw UnsupportedOperationException |
| `greater` | 1 | none | throw UnsupportedOperationException |
| `greaterEquals` | 1 | none | throw UnsupportedOperationException |
| `halfToIntBits` | 1 | none | throw UnsupportedOperationException |
| `halfToRawIntBits` | 1 | none | throw UnsupportedOperationException |
| `less` | 1 | none | throw UnsupportedOperationException |
| `lessEquals` | 1 | none | throw UnsupportedOperationException |
| `toFloat` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.util.Half`:


## Quality Gates

Before marking `android.util.Half` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 26 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
