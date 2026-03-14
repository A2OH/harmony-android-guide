# SKILL: android.os.BaseBundle

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.BaseBundle`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.BaseBundle` |
| **Package** | `android.os` |
| **Total Methods** | 26 |
| **Avg Score** | 3.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 17 (65%) |
| **No Mapping** | 9 (34%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `size` | `int size()` | 6 | partial | moderate | `size` | `size: number` |
| `clear` | `void clear()` | 6 | partial | moderate | `clear` | `clear(): void` |
| `isEmpty` | `boolean isEmpty()` | 5 | partial | moderate | `isKeepData` | `isKeepData: boolean` |

## Stub APIs (score < 5): 23 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `keySet` | 5 | partial | Log warning + no-op |
| `getDouble` | 4 | partial | Return safe default (null/false/0/empty) |
| `getDouble` | 4 | partial | Return safe default (null/false/0/empty) |
| `getLong` | 4 | partial | Return safe default (null/false/0/empty) |
| `getLong` | 4 | partial | Return safe default (null/false/0/empty) |
| `getBoolean` | 4 | partial | Return safe default (null/false/0/empty) |
| `getBoolean` | 4 | partial | Return safe default (null/false/0/empty) |
| `remove` | 4 | partial | Log warning + no-op |
| `containsKey` | 4 | composite | Store callback, never fire |
| `getString` | 4 | composite | Return safe default (null/false/0/empty) |
| `getInt` | 3 | composite | Return safe default (null/false/0/empty) |
| `getInt` | 3 | composite | Return safe default (null/false/0/empty) |
| `putString` | 3 | composite | Log warning + no-op |
| `putStringArray` | 2 | composite | Log warning + no-op |
| `putAll` | 1 | none | Log warning + no-op |
| `putBoolean` | 1 | none | Log warning + no-op |
| `putBooleanArray` | 1 | none | Log warning + no-op |
| `putDouble` | 1 | none | Log warning + no-op |
| `putDoubleArray` | 1 | none | Log warning + no-op |
| `putInt` | 1 | none | Log warning + no-op |
| `putIntArray` | 1 | none | Log warning + no-op |
| `putLong` | 1 | none | Log warning + no-op |
| `putLongArray` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 23 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.BaseBundle`:


## Quality Gates

Before marking `android.os.BaseBundle` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 26 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
