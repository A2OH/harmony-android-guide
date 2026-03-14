# SKILL: android.database.CursorWindow

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.database.CursorWindow`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.database.CursorWindow` |
| **Package** | `android.database` |
| **Total Methods** | 27 |
| **Avg Score** | 2.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 13 (48%) |
| **No Mapping** | 14 (51%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getStartPosition` | `int getStartPosition()` | 6 | partial | moderate | `getPosition` | `getPosition(): number` |
| `getInt` | `int getInt(int, int)` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `getLong` | `long getLong(int, int)` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `getFloat` | `float getFloat(int, int)` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `clear` | `void clear()` | 5 | partial | moderate | `clear` | `clear(): void` |

## Stub APIs (score < 5): 22 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setStartPosition` | 5 | partial | Return dummy instance / no-op |
| `getType` | 4 | partial | Return safe default (null/false/0/empty) |
| `getNumRows` | 4 | partial | Return safe default (null/false/0/empty) |
| `freeLastRow` | 4 | partial | throw UnsupportedOperationException |
| `getString` | 3 | composite | Return safe default (null/false/0/empty) |
| `getShort` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDouble` | 3 | composite | Return safe default (null/false/0/empty) |
| `getBlob` | 2 | composite | Return safe default (null/false/0/empty) |
| `CursorWindow` | 1 | none | throw UnsupportedOperationException |
| `CursorWindow` | 1 | none | throw UnsupportedOperationException |
| `allocRow` | 1 | none | throw UnsupportedOperationException |
| `copyStringToBuffer` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `newFromParcel` | 1 | none | throw UnsupportedOperationException |
| `onAllReferencesReleased` | 1 | none | No-op |
| `putBlob` | 1 | none | Log warning + no-op |
| `putDouble` | 1 | none | Log warning + no-op |
| `putLong` | 1 | none | Log warning + no-op |
| `putNull` | 1 | none | Log warning + no-op |
| `putString` | 1 | none | Log warning + no-op |
| `setNumColumns` | 1 | none | Log warning + no-op |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 22 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.database.CursorWindow`:


## Quality Gates

Before marking `android.database.CursorWindow` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 27 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
