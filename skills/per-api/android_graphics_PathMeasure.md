# SKILL: android.graphics.PathMeasure

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.PathMeasure`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.PathMeasure` |
| **Package** | `android.graphics` |
| **Total Methods** | 9 |
| **Avg Score** | 2.2 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (33%) |
| **No Mapping** | 6 (66%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isClosed` | `boolean isClosed()` | 5 | partial | moderate | `isClosed` | `isClosed: boolean` |

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getLength` | 4 | partial | Return safe default (null/false/0/empty) |
| `getMatrix` | 4 | composite | Return safe default (null/false/0/empty) |
| `PathMeasure` | 1 | none | throw UnsupportedOperationException |
| `PathMeasure` | 1 | none | throw UnsupportedOperationException |
| `getPosTan` | 1 | none | Return safe default (null/false/0/empty) |
| `getSegment` | 1 | none | Return safe default (null/false/0/empty) |
| `nextContour` | 1 | none | Store callback, never fire |
| `setPath` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.PathMeasure`:


## Quality Gates

Before marking `android.graphics.PathMeasure` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
