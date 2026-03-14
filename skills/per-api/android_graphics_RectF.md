# SKILL: android.graphics.RectF

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.RectF`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.RectF` |
| **Package** | `android.graphics` |
| **Total Methods** | 33 |
| **Avg Score** | 1.9 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 11 (33%) |
| **No Mapping** | 22 (66%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `height` | `final float height()` | 6 | partial | moderate | `height` | `height: number` |
| `width` | `final float width()` | 6 | partial | moderate | `width` | `width: number` |

## Stub APIs (score < 5): 31 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `offset` | 4 | partial | Log warning + no-op |
| `contains` | 3 | composite | Store callback, never fire |
| `contains` | 3 | composite | Store callback, never fire |
| `contains` | 3 | composite | Store callback, never fire |
| `set` | 3 | composite | Log warning + no-op |
| `set` | 3 | composite | Log warning + no-op |
| `set` | 3 | composite | Log warning + no-op |
| `readFromParcel` | 3 | composite | Return safe default (null/false/0/empty) |
| `round` | 3 | composite | throw UnsupportedOperationException |
| `RectF` | 1 | none | throw UnsupportedOperationException |
| `RectF` | 1 | none | throw UnsupportedOperationException |
| `RectF` | 1 | none | throw UnsupportedOperationException |
| `RectF` | 1 | none | throw UnsupportedOperationException |
| `centerX` | 1 | none | throw UnsupportedOperationException |
| `centerY` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `inset` | 1 | none | Log warning + no-op |
| `intersect` | 1 | none | throw UnsupportedOperationException |
| `intersect` | 1 | none | throw UnsupportedOperationException |
| `intersects` | 1 | none | throw UnsupportedOperationException |
| `intersects` | 1 | none | throw UnsupportedOperationException |
| `isEmpty` | 1 | none | Return safe default (null/false/0/empty) |
| `offsetTo` | 1 | none | Log warning + no-op |
| `roundOut` | 1 | none | throw UnsupportedOperationException |
| `setEmpty` | 1 | none | Log warning + no-op |
| `setIntersect` | 1 | none | Log warning + no-op |
| `sort` | 1 | none | throw UnsupportedOperationException |
| `union` | 1 | none | Store callback, never fire |
| `union` | 1 | none | Store callback, never fire |
| `union` | 1 | none | Store callback, never fire |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.RectF`:


## Quality Gates

Before marking `android.graphics.RectF` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 33 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
