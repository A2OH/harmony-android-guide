# SKILL: android.graphics.Canvas

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Canvas`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Canvas` |
| **Package** | `android.graphics` |
| **Total Methods** | 90 |
| **Avg Score** | 3.3 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 22 (24%) |
| **Partial/Composite** | 26 (28%) |
| **No Mapping** | 42 (46%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 10 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 22 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `clipPath` | `boolean clipPath(@NonNull android.graphics.Path)` | 9 | direct | impossible | `OH_Drawing_CanvasClipPath` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `clipRect` | `boolean clipRect(@NonNull android.graphics.RectF)` | 9 | direct | impossible | `OH_Drawing_CanvasClipRect` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `clipRect` | `boolean clipRect(@NonNull android.graphics.Rect)` | 9 | direct | impossible | `OH_Drawing_CanvasClipRect` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `clipRect` | `boolean clipRect(float, float, float, float)` | 9 | direct | impossible | `OH_Drawing_CanvasClipRect` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `clipRect` | `boolean clipRect(int, int, int, int)` | 9 | direct | impossible | `OH_Drawing_CanvasClipRect` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `restore` | `void restore()` | 9 | direct | hard | `OH_Drawing_CanvasRestore` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `rotate` | `void rotate(float)` | 9 | direct | rewrite | `OH_Drawing_CanvasRotate` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `rotate` | `final void rotate(float, float, float)` | 9 | direct | rewrite | `OH_Drawing_CanvasRotate` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `save` | `int save()` | 9 | direct | rewrite | `OH_Drawing_CanvasSave` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `scale` | `void scale(float, float)` | 9 | direct | rewrite | `OH_Drawing_CanvasScale` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `scale` | `final void scale(float, float, float, float)` | 9 | direct | rewrite | `OH_Drawing_CanvasScale` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `translate` | `void translate(float, float)` | 9 | direct | rewrite | `OH_Drawing_CanvasTranslate` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `drawBitmap` | `void drawBitmap(@NonNull android.graphics.Bitmap, float, float, @Nullable android.graphics.Paint)` | 7 | near | hard | `OH_Drawing_CanvasDrawBitmap` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `drawBitmap` | `void drawBitmap(@NonNull android.graphics.Bitmap, @Nullable android.graphics.Rect, @NonNull android.graphics.RectF, @Nullable android.graphics.Paint)` | 7 | near | hard | `OH_Drawing_CanvasDrawBitmap` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `drawBitmap` | `void drawBitmap(@NonNull android.graphics.Bitmap, @Nullable android.graphics.Rect, @NonNull android.graphics.Rect, @Nullable android.graphics.Paint)` | 7 | near | hard | `OH_Drawing_CanvasDrawBitmap` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `drawBitmap` | `void drawBitmap(@NonNull android.graphics.Bitmap, @NonNull android.graphics.Matrix, @Nullable android.graphics.Paint)` | 7 | near | hard | `OH_Drawing_CanvasDrawBitmap` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `drawCircle` | `void drawCircle(float, float, float, @NonNull android.graphics.Paint)` | 7 | near | hard | `OH_Drawing_CanvasDrawCircle` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `drawLine` | `void drawLine(float, float, float, float, @NonNull android.graphics.Paint)` | 7 | near | hard | `OH_Drawing_CanvasDrawLine` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `drawPath` | `void drawPath(@NonNull android.graphics.Path, @NonNull android.graphics.Paint)` | 7 | near | hard | `OH_Drawing_CanvasDrawPath` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `drawRect` | `void drawRect(@NonNull android.graphics.RectF, @NonNull android.graphics.Paint)` | 7 | near | rewrite | `OH_Drawing_CanvasDrawRect` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `drawRect` | `void drawRect(@NonNull android.graphics.Rect, @NonNull android.graphics.Paint)` | 7 | near | rewrite | `OH_Drawing_CanvasDrawRect` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |
| `drawRect` | `void drawRect(float, float, float, float, @NonNull android.graphics.Paint)` | 7 | near | rewrite | `OH_Drawing_CanvasDrawRect` | `@ohos.graphics.drawing (NDK).OH_Drawing_Canvas` |

## Gap Descriptions (per method)

- **`clipPath`**: Direct
- **`clipRect`**: Direct
- **`clipRect`**: Direct
- **`clipRect`**: Direct
- **`clipRect`**: Direct
- **`restore`**: Direct
- **`rotate`**: Direct
- **`rotate`**: Direct
- **`save`**: Direct
- **`scale`**: Direct
- **`scale`**: Direct
- **`translate`**: Direct
- **`drawBitmap`**: Pen/Brush model
- **`drawBitmap`**: Pen/Brush model
- **`drawBitmap`**: Pen/Brush model
- **`drawBitmap`**: Pen/Brush model
- **`drawCircle`**: Pen/Brush model
- **`drawLine`**: Pen/Brush model
- **`drawPath`**: Pen/Brush model
- **`drawRect`**: Pen/Brush model
- **`drawRect`**: Pen/Brush model
- **`drawRect`**: Pen/Brush model

## Stub APIs (score < 5): 68 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `drawPoint` | 4 | partial | throw UnsupportedOperationException |
| `drawBitmapMesh` | 3 | composite | throw UnsupportedOperationException |
| `restoreToCount` | 3 | composite | throw UnsupportedOperationException |
| `drawTextOnPath` | 3 | composite | Store callback, never fire |
| `drawTextOnPath` | 3 | composite | Store callback, never fire |
| `drawArc` | 3 | composite | throw UnsupportedOperationException |
| `drawArc` | 3 | composite | throw UnsupportedOperationException |
| `drawPaint` | 3 | composite | throw UnsupportedOperationException |
| `setMatrix` | 3 | composite | Log warning + no-op |
| `drawRoundRect` | 3 | composite | throw UnsupportedOperationException |
| `drawRoundRect` | 3 | composite | throw UnsupportedOperationException |
| `concat` | 3 | composite | Store callback, never fire |
| `getSaveCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `drawColor` | 3 | composite | throw UnsupportedOperationException |
| `drawColor` | 3 | composite | throw UnsupportedOperationException |
| `drawColor` | 3 | composite | throw UnsupportedOperationException |
| `drawColor` | 3 | composite | throw UnsupportedOperationException |
| `drawColor` | 3 | composite | throw UnsupportedOperationException |
| `drawDoubleRoundRect` | 3 | composite | throw UnsupportedOperationException |
| `drawDoubleRoundRect` | 3 | composite | throw UnsupportedOperationException |
| `setDrawFilter` | 2 | composite | Log warning + no-op |
| `getWidth` | 2 | composite | Return safe default (null/false/0/empty) |
| `getMaximumBitmapHeight` | 2 | composite | Return safe default (null/false/0/empty) |
| `getMaximumBitmapWidth` | 2 | composite | Return safe default (null/false/0/empty) |
| `clipOutPath` | 2 | composite | throw UnsupportedOperationException |
| `getHeight` | 2 | composite | Return safe default (null/false/0/empty) |
| `Canvas` | 1 | none | Return safe default (null/false/0/empty) |
| `Canvas` | 1 | none | Return safe default (null/false/0/empty) |
| `clipOutRect` | 1 | none | throw UnsupportedOperationException |
| `clipOutRect` | 1 | none | throw UnsupportedOperationException |
| `clipOutRect` | 1 | none | throw UnsupportedOperationException |
| `clipOutRect` | 1 | none | throw UnsupportedOperationException |
| `disableZ` | 1 | none | Return safe default (null/false/0/empty) |
| `drawARGB` | 1 | none | throw UnsupportedOperationException |
| `drawLines` | 1 | none | throw UnsupportedOperationException |
| `drawLines` | 1 | none | throw UnsupportedOperationException |
| `drawOval` | 1 | none | throw UnsupportedOperationException |
| `drawOval` | 1 | none | throw UnsupportedOperationException |
| `drawPicture` | 1 | none | throw UnsupportedOperationException |
| `drawPicture` | 1 | none | throw UnsupportedOperationException |
| `drawPicture` | 1 | none | throw UnsupportedOperationException |
| `drawPoints` | 1 | none | throw UnsupportedOperationException |
| `drawPoints` | 1 | none | throw UnsupportedOperationException |
| `drawRGB` | 1 | none | throw UnsupportedOperationException |
| `drawRenderNode` | 1 | none | throw UnsupportedOperationException |
| `drawText` | 1 | none | throw UnsupportedOperationException |
| `drawText` | 1 | none | throw UnsupportedOperationException |
| `drawText` | 1 | none | throw UnsupportedOperationException |
| `drawText` | 1 | none | throw UnsupportedOperationException |
| `drawTextRun` | 1 | none | throw UnsupportedOperationException |
| `drawTextRun` | 1 | none | throw UnsupportedOperationException |
| `drawTextRun` | 1 | none | throw UnsupportedOperationException |
| `drawVertices` | 1 | none | throw UnsupportedOperationException |
| `enableZ` | 1 | none | throw UnsupportedOperationException |
| `getClipBounds` | 1 | none | Return safe default (null/false/0/empty) |
| `getDensity` | 1 | none | Return safe default (null/false/0/empty) |
| `isHardwareAccelerated` | 1 | none | Return safe default (null/false/0/empty) |
| `isOpaque` | 1 | none | Return safe default (null/false/0/empty) |
| `quickReject` | 1 | none | throw UnsupportedOperationException |
| `quickReject` | 1 | none | throw UnsupportedOperationException |
| `quickReject` | 1 | none | throw UnsupportedOperationException |
| `saveLayer` | 1 | none | throw UnsupportedOperationException |
| `saveLayer` | 1 | none | throw UnsupportedOperationException |
| `saveLayerAlpha` | 1 | none | throw UnsupportedOperationException |
| `saveLayerAlpha` | 1 | none | throw UnsupportedOperationException |
| `setBitmap` | 1 | none | Log warning + no-op |
| `setDensity` | 1 | none | Log warning + no-op |
| `skew` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Canvas`:

- `android.graphics.Paint` (not yet shimmed)
- `android.graphics.Bitmap` (not yet shimmed)

## Quality Gates

Before marking `android.graphics.Canvas` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 90 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 22 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
