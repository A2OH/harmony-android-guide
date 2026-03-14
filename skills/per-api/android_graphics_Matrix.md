# SKILL: android.graphics.Matrix

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Matrix`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Matrix` |
| **Package** | `android.graphics` |
| **Total Methods** | 48 |
| **Avg Score** | 1.3 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (12%) |
| **No Mapping** | 42 (87%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 48 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `invert` | 4 | partial | throw UnsupportedOperationException |
| `reset` | 3 | composite | Log warning + no-op |
| `set` | 3 | composite | Log warning + no-op |
| `setTranslate` | 3 | composite | Log warning + no-op |
| `postTranslate` | 2 | composite | throw UnsupportedOperationException |
| `preTranslate` | 2 | composite | throw UnsupportedOperationException |
| `Matrix` | 1 | none | throw UnsupportedOperationException |
| `Matrix` | 1 | none | throw UnsupportedOperationException |
| `getValues` | 1 | none | Return safe default (null/false/0/empty) |
| `isAffine` | 1 | none | Return safe default (null/false/0/empty) |
| `isIdentity` | 1 | none | Return safe default (null/false/0/empty) |
| `mapPoints` | 1 | none | throw UnsupportedOperationException |
| `mapPoints` | 1 | none | throw UnsupportedOperationException |
| `mapPoints` | 1 | none | throw UnsupportedOperationException |
| `mapRadius` | 1 | none | throw UnsupportedOperationException |
| `mapRect` | 1 | none | throw UnsupportedOperationException |
| `mapRect` | 1 | none | throw UnsupportedOperationException |
| `mapVectors` | 1 | none | throw UnsupportedOperationException |
| `mapVectors` | 1 | none | throw UnsupportedOperationException |
| `mapVectors` | 1 | none | throw UnsupportedOperationException |
| `postConcat` | 1 | none | Store callback, never fire |
| `postRotate` | 1 | none | throw UnsupportedOperationException |
| `postRotate` | 1 | none | throw UnsupportedOperationException |
| `postScale` | 1 | none | throw UnsupportedOperationException |
| `postScale` | 1 | none | throw UnsupportedOperationException |
| `postSkew` | 1 | none | throw UnsupportedOperationException |
| `postSkew` | 1 | none | throw UnsupportedOperationException |
| `preConcat` | 1 | none | Store callback, never fire |
| `preRotate` | 1 | none | throw UnsupportedOperationException |
| `preRotate` | 1 | none | throw UnsupportedOperationException |
| `preScale` | 1 | none | throw UnsupportedOperationException |
| `preScale` | 1 | none | throw UnsupportedOperationException |
| `preSkew` | 1 | none | throw UnsupportedOperationException |
| `preSkew` | 1 | none | throw UnsupportedOperationException |
| `rectStaysRect` | 1 | none | throw UnsupportedOperationException |
| `setConcat` | 1 | none | Log warning + no-op |
| `setPolyToPoly` | 1 | none | Log warning + no-op |
| `setRectToRect` | 1 | none | Log warning + no-op |
| `setRotate` | 1 | none | Log warning + no-op |
| `setRotate` | 1 | none | Log warning + no-op |
| `setScale` | 1 | none | Log warning + no-op |
| `setScale` | 1 | none | Log warning + no-op |
| `setSinCos` | 1 | none | Log warning + no-op |
| `setSinCos` | 1 | none | Log warning + no-op |
| `setSkew` | 1 | none | Log warning + no-op |
| `setSkew` | 1 | none | Log warning + no-op |
| `setValues` | 1 | none | Log warning + no-op |
| `toShortString` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Matrix`:


## Quality Gates

Before marking `android.graphics.Matrix` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 48 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
