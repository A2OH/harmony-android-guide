# SKILL: android.util.SparseLongArray

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.SparseLongArray`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.SparseLongArray` |
| **Package** | `android.util` |
| **Total Methods** | 15 |
| **Avg Score** | 3.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 11 (73%) |
| **No Mapping** | 4 (26%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `size` | `int size()` | 6 | partial | moderate | `size` | `size: number` |
| `clear` | `void clear()` | 6 | partial | moderate | `clear` | `clear(): void` |
| `valueAt` | `long valueAt(int)` | 6 | partial | moderate | `value` | `value: number` |

## Stub APIs (score < 5): 12 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `indexOfKey` | 5 | partial | throw UnsupportedOperationException |
| `keyAt` | 5 | partial | throw UnsupportedOperationException |
| `indexOfValue` | 4 | partial | throw UnsupportedOperationException |
| `append` | 4 | partial | throw UnsupportedOperationException |
| `removeAt` | 4 | composite | Log warning + no-op |
| `delete` | 3 | composite | throw UnsupportedOperationException |
| `get` | 3 | composite | Return safe default (null/false/0/empty) |
| `get` | 3 | composite | Return safe default (null/false/0/empty) |
| `SparseLongArray` | 1 | none | Store callback, never fire |
| `SparseLongArray` | 1 | none | Store callback, never fire |
| `clone` | 1 | none | Store callback, never fire |
| `put` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 12 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.SparseLongArray`:


## Quality Gates

Before marking `android.util.SparseLongArray` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 15 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
