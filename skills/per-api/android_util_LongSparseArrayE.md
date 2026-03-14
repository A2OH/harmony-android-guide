# SKILL: android.util.LongSparseArray<E>

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.LongSparseArray<E>`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.LongSparseArray<E>` |
| **Package** | `android.util` |
| **Total Methods** | 17 |
| **Avg Score** | 3.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 13 (76%) |
| **No Mapping** | 4 (23%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `size` | `int size()` | 6 | partial | moderate | `size` | `size: number` |
| `clear` | `void clear()` | 6 | partial | moderate | `clear` | `clear(): void` |

## Stub APIs (score < 5): 15 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `valueAt` | 5 | partial | throw UnsupportedOperationException |
| `indexOfKey` | 5 | partial | throw UnsupportedOperationException |
| `keyAt` | 5 | partial | throw UnsupportedOperationException |
| `indexOfValue` | 5 | partial | throw UnsupportedOperationException |
| `append` | 4 | partial | throw UnsupportedOperationException |
| `remove` | 4 | partial | Log warning + no-op |
| `setValueAt` | 4 | composite | Log warning + no-op |
| `removeAt` | 4 | composite | Log warning + no-op |
| `delete` | 3 | composite | throw UnsupportedOperationException |
| `get` | 3 | composite | Return safe default (null/false/0/empty) |
| `get` | 3 | composite | Return safe default (null/false/0/empty) |
| `LongSparseArray` | 1 | none | Store callback, never fire |
| `LongSparseArray` | 1 | none | Store callback, never fire |
| `clone` | 1 | none | Store callback, never fire |
| `put` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 2 methods that have score >= 5
2. Stub 15 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.LongSparseArray<E>`:


## Quality Gates

Before marking `android.util.LongSparseArray<E>` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 17 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
