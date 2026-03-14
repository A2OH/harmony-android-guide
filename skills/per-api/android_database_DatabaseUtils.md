# SKILL: android.database.DatabaseUtils

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.database.DatabaseUtils`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.database.DatabaseUtils` |
| **Package** | `android.database` |
| **Total Methods** | 48 |
| **Avg Score** | 1.5 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 9 (18%) |
| **No Mapping** | 39 (81%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 48 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getCollationKey` | 4 | partial | Return safe default (null/false/0/empty) |
| `queryNumEntries` | 4 | partial | Return safe default (null/false/0/empty) |
| `queryNumEntries` | 4 | partial | Return safe default (null/false/0/empty) |
| `queryNumEntries` | 4 | partial | Return safe default (null/false/0/empty) |
| `getHexCollationKey` | 4 | partial | Return safe default (null/false/0/empty) |
| `getSqlStatementType` | 4 | partial | Return safe default (null/false/0/empty) |
| `createDbFromSqlStatements` | 3 | composite | Return dummy instance / no-op |
| `blobFileDescriptorForQuery` | 2 | composite | Return safe default (null/false/0/empty) |
| `blobFileDescriptorForQuery` | 2 | composite | Return safe default (null/false/0/empty) |
| `DatabaseUtils` | 1 | none | throw UnsupportedOperationException |
| `appendEscapedSQLString` | 1 | none | throw UnsupportedOperationException |
| `appendSelectionArgs` | 1 | none | Store callback, never fire |
| `appendValueToSql` | 1 | none | throw UnsupportedOperationException |
| `bindObjectToProgram` | 1 | none | throw UnsupportedOperationException |
| `concatenateWhere` | 1 | none | Store callback, never fire |
| `cursorDoubleToContentValues` | 1 | none | Store callback, never fire |
| `cursorDoubleToContentValuesIfPresent` | 1 | none | Store callback, never fire |
| `cursorDoubleToCursorValues` | 1 | none | throw UnsupportedOperationException |
| `cursorFloatToContentValuesIfPresent` | 1 | none | Store callback, never fire |
| `cursorIntToContentValues` | 1 | none | Store callback, never fire |
| `cursorIntToContentValues` | 1 | none | Store callback, never fire |
| `cursorIntToContentValuesIfPresent` | 1 | none | Store callback, never fire |
| `cursorLongToContentValues` | 1 | none | Store callback, never fire |
| `cursorLongToContentValues` | 1 | none | Store callback, never fire |
| `cursorLongToContentValuesIfPresent` | 1 | none | Store callback, never fire |
| `cursorRowToContentValues` | 1 | none | Store callback, never fire |
| `cursorShortToContentValuesIfPresent` | 1 | none | Store callback, never fire |
| `cursorStringToContentValues` | 1 | none | Store callback, never fire |
| `cursorStringToContentValues` | 1 | none | Store callback, never fire |
| `cursorStringToContentValuesIfPresent` | 1 | none | Store callback, never fire |
| `cursorStringToInsertHelper` | 1 | none | throw UnsupportedOperationException |
| `dumpCurrentRow` | 1 | none | throw UnsupportedOperationException |
| `dumpCurrentRow` | 1 | none | throw UnsupportedOperationException |
| `dumpCurrentRow` | 1 | none | throw UnsupportedOperationException |
| `dumpCurrentRowToString` | 1 | none | throw UnsupportedOperationException |
| `dumpCursor` | 1 | none | throw UnsupportedOperationException |
| `dumpCursor` | 1 | none | throw UnsupportedOperationException |
| `dumpCursor` | 1 | none | throw UnsupportedOperationException |
| `dumpCursorToString` | 1 | none | throw UnsupportedOperationException |
| `longForQuery` | 1 | none | Return safe default (null/false/0/empty) |
| `longForQuery` | 1 | none | Return safe default (null/false/0/empty) |
| `readExceptionFromParcel` | 1 | none | Return safe default (null/false/0/empty) |
| `readExceptionWithFileNotFoundExceptionFromParcel` | 1 | none | Return safe default (null/false/0/empty) |
| `readExceptionWithOperationApplicationExceptionFromParcel` | 1 | none | Return safe default (null/false/0/empty) |
| `sqlEscapeString` | 1 | none | throw UnsupportedOperationException |
| `stringForQuery` | 1 | none | Return safe default (null/false/0/empty) |
| `stringForQuery` | 1 | none | Return safe default (null/false/0/empty) |
| `writeExceptionToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.database.DatabaseUtils`:


## Quality Gates

Before marking `android.database.DatabaseUtils` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 48 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
