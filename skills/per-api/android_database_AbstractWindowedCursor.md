# SKILL: android.database.AbstractWindowedCursor

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.database.AbstractWindowedCursor`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.database.AbstractWindowedCursor` |
| **Package** | `android.database` |
| **Total Methods** | 10 |
| **Avg Score** | 2.8 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (60%) |
| **No Mapping** | 4 (40%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getInt` | `int getInt(int)` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `getLong` | `long getLong(int)` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `getFloat` | `float getFloat(int)` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getString` | 3 | composite | Return safe default (null/false/0/empty) |
| `getShort` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDouble` | 3 | composite | Return safe default (null/false/0/empty) |
| `AbstractWindowedCursor` | 1 | none | throw UnsupportedOperationException |
| `hasWindow` | 1 | none | Return safe default (null/false/0/empty) |
| `isNull` | 1 | none | Return safe default (null/false/0/empty) |
| `setWindow` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.database.AbstractWindowedCursor`:


## Quality Gates

Before marking `android.database.AbstractWindowedCursor` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
