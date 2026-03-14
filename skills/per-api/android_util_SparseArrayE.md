# SKILL: android.util.SparseArray<E>

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.SparseArray<E>`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.SparseArray<E>` |
| **Package** | `android.util` |
| **Total Methods** | 19 |
| **Avg Score** | 5.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 8 (42%) |
| **Partial/Composite** | 8 (42%) |
| **No Mapping** | 3 (15%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `clear` | `void clear()` | 9 | direct | moderate | `clear` | `@ohos.util.PlainArray.PlainArray` |
| `get` | `E get(int)` | 9 | direct | hard | `get` | `@ohos.util.PlainArray.PlainArray` |
| `get` | `E get(int, E)` | 9 | direct | hard | `get` | `@ohos.util.PlainArray.PlainArray` |
| `delete` | `void delete(int)` | 7 | near | hard | `remove` | `@ohos.util.PlainArray.PlainArray` |
| `keyAt` | `int keyAt(int)` | 7 | near | hard | `getKeyAt` | `@ohos.util.PlainArray.PlainArray` |
| `put` | `void put(int, E)` | 7 | near | impossible | `add` | `@ohos.util.PlainArray.PlainArray` |
| `size` | `int size()` | 7 | near | moderate | `length` | `@ohos.util.PlainArray.PlainArray` |
| `valueAt` | `E valueAt(int)` | 7 | near | hard | `getValueAt` | `@ohos.util.PlainArray.PlainArray` |

## Gap Descriptions (per method)

- **`clear`**: Direct
- **`get`**: Direct
- **`get`**: Direct
- **`delete`**: Name: remove vs delete
- **`keyAt`**: Name differs
- **`put`**: Name: add vs put
- **`size`**: Property vs method
- **`valueAt`**: Name differs

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `indexOfKey` | 5 | partial | throw UnsupportedOperationException |
| `indexOfValue` | 5 | partial | throw UnsupportedOperationException |
| `append` | 4 | partial | throw UnsupportedOperationException |
| `contains` | 4 | partial | Store callback, never fire |
| `remove` | 4 | partial | Log warning + no-op |
| `setValueAt` | 4 | composite | Log warning + no-op |
| `removeAt` | 4 | composite | Log warning + no-op |
| `removeAtRange` | 4 | composite | Log warning + no-op |
| `SparseArray` | 1 | none | throw UnsupportedOperationException |
| `SparseArray` | 1 | none | throw UnsupportedOperationException |
| `clone` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 8 methods that have score >= 5
2. Stub 11 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.SparseArray<E>`:


## Quality Gates

Before marking `android.util.SparseArray<E>` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 19 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
