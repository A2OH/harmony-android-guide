# SKILL: android.database.AbstractCursor

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.database.AbstractCursor`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.database.AbstractCursor` |
| **Package** | `android.database` |
| **Total Methods** | 39 |
| **Avg Score** | 3.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 28 (71%) |
| **No Mapping** | 11 (28%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 13 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getPosition` | `final int getPosition()` | 6 | partial | moderate | `getPosition` | `getPosition(): number` |
| `isAfterLast` | `final boolean isAfterLast()` | 6 | partial | moderate | `isAfterLast` | `isAfterLast(): boolean` |
| `isBeforeFirst` | `final boolean isBeforeFirst()` | 6 | partial | moderate | `isBeforeFirst` | `isBeforeFirst(): boolean` |
| `isFirst` | `final boolean isFirst()` | 6 | partial | moderate | `isFirst` | `isFirst(): boolean` |
| `isLast` | `final boolean isLast()` | 6 | partial | moderate | `isLast` | `isLast(): boolean` |
| `moveToFirst` | `final boolean moveToFirst()` | 6 | partial | moderate | `moveToFirst` | `moveToFirst(): boolean` |
| `moveToLast` | `final boolean moveToLast()` | 6 | partial | moderate | `moveToLast` | `moveToLast(): boolean` |
| `moveToNext` | `final boolean moveToNext()` | 6 | partial | moderate | `moveToNext` | `moveToNext(): boolean` |
| `moveToPrevious` | `final boolean moveToPrevious()` | 6 | partial | moderate | `moveToPrevious` | `moveToPrevious(): boolean` |
| `getColumnCount` | `int getColumnCount()` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `getColumnIndex` | `int getColumnIndex(String)` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `isClosed` | `boolean isClosed()` | 5 | partial | moderate | `isClosed` | `isClosed: boolean` |
| `getColumnIndexOrThrow` | `int getColumnIndexOrThrow(String)` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## Stub APIs (score < 5): 26 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `checkPosition` | 5 | partial | Store callback, never fire |
| `getType` | 5 | partial | Return safe default (null/false/0/empty) |
| `getExtras` | 5 | partial | Return safe default (null/false/0/empty) |
| `getColumnName` | 4 | partial | Return safe default (null/false/0/empty) |
| `getNotificationUri` | 4 | partial | Return safe default (null/false/0/empty) |
| `move` | 4 | partial | throw UnsupportedOperationException |
| `moveToPosition` | 4 | partial | Store callback, never fire |
| `onMove` | 4 | composite | Store callback, never fire |
| `close` | 3 | composite | No-op |
| `deactivate` | 3 | composite | throw UnsupportedOperationException |
| `registerDataSetObserver` | 3 | composite | Return safe default (null/false/0/empty) |
| `onChange` | 3 | composite | Store callback, never fire |
| `unregisterDataSetObserver` | 3 | composite | Return safe default (null/false/0/empty) |
| `getWindow` | 3 | composite | Return safe default (null/false/0/empty) |
| `getBlob` | 2 | composite | Return safe default (null/false/0/empty) |
| `AbstractCursor` | 1 | none | throw UnsupportedOperationException |
| `copyStringToBuffer` | 1 | none | throw UnsupportedOperationException |
| `fillWindow` | 1 | none | throw UnsupportedOperationException |
| `finalize` | 1 | none | throw UnsupportedOperationException |
| `getWantsAllOnMoveCalls` | 1 | none | Return safe default (null/false/0/empty) |
| `registerContentObserver` | 1 | none | Return safe default (null/false/0/empty) |
| `requery` | 1 | none | Return safe default (null/false/0/empty) |
| `respond` | 1 | none | Store callback, never fire |
| `setExtras` | 1 | none | Log warning + no-op |
| `setNotificationUri` | 1 | none | Log warning + no-op |
| `unregisterContentObserver` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 13 methods that have score >= 5
2. Stub 26 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.database.AbstractCursor`:

- `android.database.Cursor` (already shimmed)

## Quality Gates

Before marking `android.database.AbstractCursor` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 39 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 13 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
