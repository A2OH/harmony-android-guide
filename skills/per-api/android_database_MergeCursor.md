# SKILL: android.database.MergeCursor

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.database.MergeCursor`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.database.MergeCursor` |
| **Package** | `android.database` |
| **Total Methods** | 10 |
| **Avg Score** | 3.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (10%) |
| **Partial/Composite** | 7 (70%) |
| **No Mapping** | 2 (20%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getCount` | `int getCount()` | 6 | near | moderate | `getCount` | `@ohos.data.distributedData.KvStoreResultSet` |
| `getInt` | `int getInt(int)` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `getLong` | `long getLong(int)` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `getFloat` | `float getFloat(int)` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## Gap Descriptions (per method)

- **`getCount`**: Auto-promoted: near score=6.02777777777778

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getColumnNames` | 5 | partial | Return safe default (null/false/0/empty) |
| `getString` | 3 | composite | Return safe default (null/false/0/empty) |
| `getShort` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDouble` | 3 | composite | Return safe default (null/false/0/empty) |
| `MergeCursor` | 1 | none | throw UnsupportedOperationException |
| `isNull` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.database.MergeCursor`:


## Quality Gates

Before marking `android.database.MergeCursor` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
