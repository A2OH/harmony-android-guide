# SKILL: android.database.MatrixCursor

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.database.MatrixCursor`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.database.MatrixCursor` |
| **Package** | `android.database` |
| **Total Methods** | 14 |
| **Avg Score** | 2.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 8 (57%) |
| **No Mapping** | 6 (42%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getCount` | `int getCount()` | 6 | partial | moderate | `getCount` | `getCount(): number` |
| `getInt` | `int getInt(int)` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `getLong` | `long getLong(int)` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `getFloat` | `float getFloat(int)` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getColumnNames` | 4 | partial | Return safe default (null/false/0/empty) |
| `getString` | 3 | composite | Return safe default (null/false/0/empty) |
| `getShort` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDouble` | 3 | composite | Return safe default (null/false/0/empty) |
| `MatrixCursor` | 1 | none | throw UnsupportedOperationException |
| `MatrixCursor` | 1 | none | throw UnsupportedOperationException |
| `addRow` | 1 | none | Log warning + no-op |
| `addRow` | 1 | none | Log warning + no-op |
| `isNull` | 1 | none | Return safe default (null/false/0/empty) |
| `newRow` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 10 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.database.MatrixCursor`:


## Quality Gates

Before marking `android.database.MatrixCursor` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
