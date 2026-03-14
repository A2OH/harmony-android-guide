# SKILL: android.graphics.Path

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Path`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Path` |
| **Package** | `android.graphics` |
| **Total Methods** | 45 |
| **Avg Score** | 2.7 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 6 (13%) |
| **Partial/Composite** | 12 (26%) |
| **No Mapping** | 27 (60%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close()` | 9 | direct | hard | `OH_Drawing_PathClose` | `@ohos.graphics.drawing (NDK).OH_Drawing_Path` |
| `cubicTo` | `void cubicTo(float, float, float, float, float, float)` | 9 | direct | impossible | `OH_Drawing_PathCubicTo` | `@ohos.graphics.drawing (NDK).OH_Drawing_Path` |
| `lineTo` | `void lineTo(float, float)` | 9 | direct | impossible | `OH_Drawing_PathLineTo` | `@ohos.graphics.drawing (NDK).OH_Drawing_Path` |
| `moveTo` | `void moveTo(float, float)` | 9 | direct | impossible | `OH_Drawing_PathMoveTo` | `@ohos.graphics.drawing (NDK).OH_Drawing_Path` |
| `quadTo` | `void quadTo(float, float, float, float)` | 9 | direct | impossible | `OH_Drawing_PathQuadTo` | `@ohos.graphics.drawing (NDK).OH_Drawing_Path` |
| `reset` | `void reset()` | 9 | direct | hard | `OH_Drawing_PathReset` | `@ohos.graphics.drawing (NDK).OH_Drawing_Path` |

## Gap Descriptions (per method)

- **`close`**: Direct
- **`cubicTo`**: Direct
- **`lineTo`**: Direct
- **`moveTo`**: Direct
- **`quadTo`**: Direct
- **`reset`**: Direct

## Stub APIs (score < 5): 39 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Path` | 5 | partial | throw UnsupportedOperationException |
| `Path` | 5 | partial | throw UnsupportedOperationException |
| `offset` | 4 | partial | Log warning + no-op |
| `offset` | 4 | partial | Log warning + no-op |
| `setLastPoint` | 4 | partial | Log warning + no-op |
| `rewind` | 3 | composite | throw UnsupportedOperationException |
| `set` | 3 | composite | Log warning + no-op |
| `rCubicTo` | 3 | composite | throw UnsupportedOperationException |
| `addRoundRect` | 2 | composite | Log warning + no-op |
| `addRoundRect` | 2 | composite | Log warning + no-op |
| `addRoundRect` | 2 | composite | Log warning + no-op |
| `addRoundRect` | 2 | composite | Log warning + no-op |
| `addArc` | 1 | none | Log warning + no-op |
| `addArc` | 1 | none | Log warning + no-op |
| `addCircle` | 1 | none | Log warning + no-op |
| `addOval` | 1 | none | Log warning + no-op |
| `addOval` | 1 | none | Log warning + no-op |
| `addPath` | 1 | none | Log warning + no-op |
| `addPath` | 1 | none | Log warning + no-op |
| `addPath` | 1 | none | Log warning + no-op |
| `addRect` | 1 | none | Log warning + no-op |
| `addRect` | 1 | none | Log warning + no-op |
| `arcTo` | 1 | none | throw UnsupportedOperationException |
| `arcTo` | 1 | none | throw UnsupportedOperationException |
| `arcTo` | 1 | none | throw UnsupportedOperationException |
| `computeBounds` | 1 | none | Log warning + no-op |
| `incReserve` | 1 | none | throw UnsupportedOperationException |
| `isEmpty` | 1 | none | Return safe default (null/false/0/empty) |
| `isInverseFillType` | 1 | none | Return safe default (null/false/0/empty) |
| `isRect` | 1 | none | Return safe default (null/false/0/empty) |
| `op` | 1 | none | throw UnsupportedOperationException |
| `op` | 1 | none | throw UnsupportedOperationException |
| `rLineTo` | 1 | none | throw UnsupportedOperationException |
| `rMoveTo` | 1 | none | throw UnsupportedOperationException |
| `rQuadTo` | 1 | none | throw UnsupportedOperationException |
| `setFillType` | 1 | none | Log warning + no-op |
| `toggleInverseFillType` | 1 | none | throw UnsupportedOperationException |
| `transform` | 1 | none | throw UnsupportedOperationException |
| `transform` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Path`:


## Quality Gates

Before marking `android.graphics.Path` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 45 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
