# SKILL: android.util.TypedValue

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.TypedValue`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.TypedValue` |
| **Package** | `android.util` |
| **Total Methods** | 15 |
| **Avg Score** | 2.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 9 (60%) |
| **No Mapping** | 6 (40%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 15 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isColorType` | 5 | partial | Return safe default (null/false/0/empty) |
| `getFraction` | 4 | partial | Return safe default (null/false/0/empty) |
| `getComplexUnit` | 4 | partial | Return safe default (null/false/0/empty) |
| `setTo` | 4 | composite | Log warning + no-op |
| `coerceToString` | 4 | composite | throw UnsupportedOperationException |
| `coerceToString` | 4 | composite | throw UnsupportedOperationException |
| `TypedValue` | 3 | composite | throw UnsupportedOperationException |
| `getFloat` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDimension` | 3 | composite | Return safe default (null/false/0/empty) |
| `applyDimension` | 1 | none | Store callback, never fire |
| `complexToDimension` | 1 | none | Store callback, never fire |
| `complexToDimensionPixelOffset` | 1 | none | Log warning + no-op |
| `complexToDimensionPixelSize` | 1 | none | Store callback, never fire |
| `complexToFloat` | 1 | none | throw UnsupportedOperationException |
| `complexToFraction` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.util.TypedValue`:


## Quality Gates

Before marking `android.util.TypedValue` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 15 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
