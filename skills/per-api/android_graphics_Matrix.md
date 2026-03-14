# SKILL: android.graphics.Matrix

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Matrix`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Matrix` |
| **Package** | `android.graphics` |
| **Total Methods** | 48 |
| **Avg Score** | 4.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 15 (31%) |
| **Partial/Composite** | 33 (68%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 15 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Matrix` | `Matrix()` | 9 | direct | impossible | `OH_Drawing_MatrixCreate` | `native_drawing.OH_Drawing_Matrix` |
| `Matrix` | `Matrix(android.graphics.Matrix)` | 9 | direct | impossible | `OH_Drawing_MatrixCreate` | `native_drawing.OH_Drawing_Matrix` |
| `setValues` | `void setValues(float[])` | 9 | direct | impossible | `OH_Drawing_MatrixSetMatrix` | `native_drawing.OH_Drawing_Matrix` |
| `reset` | `void reset()` | 7 | near | hard | `OH_Drawing_MatrixSetMatrix` | `native_drawing.OH_Drawing_Matrix` |
| `set` | `void set(android.graphics.Matrix)` | 7 | near | hard | `OH_Drawing_MatrixSetMatrix` | `native_drawing.OH_Drawing_Matrix` |
| `setRotate` | `void setRotate(float, float, float)` | 7 | near | impossible | `OH_Drawing_MatrixSetMatrix` | `native_drawing.OH_Drawing_Matrix` |
| `setRotate` | `void setRotate(float)` | 7 | near | impossible | `OH_Drawing_MatrixSetMatrix` | `native_drawing.OH_Drawing_Matrix` |
| `setScale` | `void setScale(float, float, float, float)` | 7 | near | impossible | `OH_Drawing_MatrixSetMatrix` | `native_drawing.OH_Drawing_Matrix` |
| `setScale` | `void setScale(float, float)` | 7 | near | impossible | `OH_Drawing_MatrixSetMatrix` | `native_drawing.OH_Drawing_Matrix` |
| `setSinCos` | `void setSinCos(float, float, float, float)` | 7 | near | impossible | `OH_Drawing_MatrixSetMatrix` | `native_drawing.OH_Drawing_Matrix` |
| `setSinCos` | `void setSinCos(float, float)` | 7 | near | impossible | `OH_Drawing_MatrixSetMatrix` | `native_drawing.OH_Drawing_Matrix` |
| `setSkew` | `void setSkew(float, float, float, float)` | 7 | near | impossible | `OH_Drawing_MatrixSetMatrix` | `native_drawing.OH_Drawing_Matrix` |
| `setSkew` | `void setSkew(float, float)` | 7 | near | impossible | `OH_Drawing_MatrixSetMatrix` | `native_drawing.OH_Drawing_Matrix` |
| `setTranslate` | `void setTranslate(float, float)` | 7 | near | hard | `OH_Drawing_MatrixSetMatrix` | `native_drawing.OH_Drawing_Matrix` |
| `setRectToRect` | `boolean setRectToRect(android.graphics.RectF, android.graphics.RectF, android.graphics.Matrix.ScaleToFit)` | 6 | near | impossible | `OH_Drawing_MatrixSetMatrix` | `native_drawing.OH_Drawing_Matrix` |

## Gap Descriptions (per method)

- **`Matrix`**: Constructor
- **`Matrix`**: Constructor
- **`setValues`**: Direct mapping; pass all 9 float values
- **`reset`**: Set to identity values (1,0,0,0,1,0,0,0,1)
- **`set`**: Copy all 9 values via SetMatrix
- **`setRotate`**: Compute cos/sin and set via SetMatrix
- **`setRotate`**: Compute cos/sin and set via SetMatrix
- **`setScale`**: Set scale matrix via SetMatrix(sx,0,0,0,sy,0,0,0,1)
- **`setScale`**: Set scale matrix via SetMatrix(sx,0,0,0,sy,0,0,0,1)
- **`setSinCos`**: Set rotation via sin/cos values
- **`setSinCos`**: Set rotation via sin/cos values
- **`setSkew`**: Set skew matrix via SetMatrix(1,kx,0,ky,1,0,0,0,1)
- **`setSkew`**: Set skew matrix via SetMatrix(1,kx,0,ky,1,0,0,0,1)
- **`setTranslate`**: Set translate matrix via SetMatrix(1,0,tx,0,1,ty,0,0,1)
- **`setRectToRect`**: Compute transform from src to dst rect; call SetMatrix

## Stub APIs (score < 5): 33 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isAffine` | 4 | partial | Return safe default (null/false/0/empty) |
| `isIdentity` | 4 | partial | Return safe default (null/false/0/empty) |
| `mapPoints` | 4 | partial | throw UnsupportedOperationException |
| `mapPoints` | 4 | partial | throw UnsupportedOperationException |
| `mapPoints` | 4 | partial | throw UnsupportedOperationException |
| `mapRect` | 4 | partial | throw UnsupportedOperationException |
| `mapRect` | 4 | partial | throw UnsupportedOperationException |
| `mapVectors` | 4 | partial | throw UnsupportedOperationException |
| `mapVectors` | 4 | partial | throw UnsupportedOperationException |
| `mapVectors` | 4 | partial | throw UnsupportedOperationException |
| `postConcat` | 4 | partial | Store callback, never fire |
| `postRotate` | 4 | partial | throw UnsupportedOperationException |
| `postRotate` | 4 | partial | throw UnsupportedOperationException |
| `postScale` | 4 | partial | throw UnsupportedOperationException |
| `postScale` | 4 | partial | throw UnsupportedOperationException |
| `postSkew` | 4 | partial | throw UnsupportedOperationException |
| `postSkew` | 4 | partial | throw UnsupportedOperationException |
| `postTranslate` | 4 | partial | throw UnsupportedOperationException |
| `preConcat` | 4 | partial | Store callback, never fire |
| `preRotate` | 4 | partial | throw UnsupportedOperationException |
| `preRotate` | 4 | partial | throw UnsupportedOperationException |
| `preScale` | 4 | partial | throw UnsupportedOperationException |
| `preScale` | 4 | partial | throw UnsupportedOperationException |
| `preSkew` | 4 | partial | throw UnsupportedOperationException |
| `preSkew` | 4 | partial | throw UnsupportedOperationException |
| `preTranslate` | 4 | partial | throw UnsupportedOperationException |
| `setConcat` | 4 | partial | Log warning + no-op |
| `getValues` | 3 | composite | Return safe default (null/false/0/empty) |
| `invert` | 3 | composite | throw UnsupportedOperationException |
| `mapRadius` | 3 | composite | throw UnsupportedOperationException |
| `rectStaysRect` | 3 | composite | throw UnsupportedOperationException |
| `setPolyToPoly` | 3 | composite | Log warning + no-op |
| `toShortString` | 3 | composite | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 15 methods that have score >= 5
2. Stub 33 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Matrix`:


## Quality Gates

Before marking `android.graphics.Matrix` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 48 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 15 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
