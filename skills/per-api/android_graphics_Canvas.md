# SKILL: android.graphics.Canvas

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Canvas`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Canvas` |
| **Package** | `android.graphics` |
| **Total Methods** | 90 |
| **Avg Score** | 6.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 60 (66%) |
| **Partial/Composite** | 30 (33%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 64 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Canvas` | `Canvas()` | 9 | direct | impossible | `OH_Drawing_CanvasCreate` | `native_drawing.OH_Drawing_Canvas` |
| `Canvas` | `Canvas(@NonNull android.graphics.Bitmap)` | 9 | direct | impossible | `OH_Drawing_CanvasCreate` | `native_drawing.OH_Drawing_Canvas` |
| `clipPath` | `boolean clipPath(@NonNull android.graphics.Path)` | 9 | direct | impossible | `OH_Drawing_CanvasClipPath` | `native_drawing.OH_Drawing_Canvas` |
| `clipRect` | `boolean clipRect(@NonNull android.graphics.RectF)` | 9 | direct | impossible | `OH_Drawing_CanvasClipRect` | `native_drawing.OH_Drawing_Canvas` |
| `clipRect` | `boolean clipRect(@NonNull android.graphics.Rect)` | 9 | direct | impossible | `OH_Drawing_CanvasClipRect` | `native_drawing.OH_Drawing_Canvas` |
| `clipRect` | `boolean clipRect(float, float, float, float)` | 9 | direct | impossible | `OH_Drawing_CanvasClipRect` | `native_drawing.OH_Drawing_Canvas` |
| `clipRect` | `boolean clipRect(int, int, int, int)` | 9 | direct | impossible | `OH_Drawing_CanvasClipRect` | `native_drawing.OH_Drawing_Canvas` |
| `drawArc` | `void drawArc(@NonNull android.graphics.RectF, float, float, boolean, @NonNull android.graphics.Paint)` | 9 | direct | hard | `OH_Drawing_CanvasDrawArc` | `native_drawing.OH_Drawing_Canvas` |
| `drawArc` | `void drawArc(float, float, float, float, float, float, boolean, @NonNull android.graphics.Paint)` | 9 | direct | hard | `OH_Drawing_CanvasDrawArc` | `native_drawing.OH_Drawing_Canvas` |
| `drawBitmap` | `void drawBitmap(@NonNull android.graphics.Bitmap, float, float, @Nullable android.graphics.Paint)` | 9 | direct | hard | `OH_Drawing_CanvasDrawBitmap` | `native_drawing.OH_Drawing_Canvas` |
| `drawBitmap` | `void drawBitmap(@NonNull android.graphics.Bitmap, @Nullable android.graphics.Rect, @NonNull android.graphics.RectF, @Nullable android.graphics.Paint)` | 9 | direct | hard | `OH_Drawing_CanvasDrawBitmap` | `native_drawing.OH_Drawing_Canvas` |
| `drawBitmap` | `void drawBitmap(@NonNull android.graphics.Bitmap, @Nullable android.graphics.Rect, @NonNull android.graphics.Rect, @Nullable android.graphics.Paint)` | 9 | direct | hard | `OH_Drawing_CanvasDrawBitmap` | `native_drawing.OH_Drawing_Canvas` |
| `drawBitmap` | `void drawBitmap(@NonNull android.graphics.Bitmap, @NonNull android.graphics.Matrix, @Nullable android.graphics.Paint)` | 9 | direct | hard | `OH_Drawing_CanvasDrawBitmap` | `native_drawing.OH_Drawing_Canvas` |
| `drawCircle` | `void drawCircle(float, float, float, @NonNull android.graphics.Paint)` | 9 | direct | hard | `OH_Drawing_CanvasDrawCircle` | `native_drawing.OH_Drawing_Canvas` |
| `drawLine` | `void drawLine(float, float, float, float, @NonNull android.graphics.Paint)` | 9 | direct | hard | `OH_Drawing_CanvasDrawLine` | `native_drawing.OH_Drawing_Canvas` |
| `drawOval` | `void drawOval(@NonNull android.graphics.RectF, @NonNull android.graphics.Paint)` | 9 | direct | impossible | `OH_Drawing_CanvasDrawOval` | `native_drawing.OH_Drawing_Canvas` |
| `drawOval` | `void drawOval(float, float, float, float, @NonNull android.graphics.Paint)` | 9 | direct | impossible | `OH_Drawing_CanvasDrawOval` | `native_drawing.OH_Drawing_Canvas` |
| `drawPath` | `void drawPath(@NonNull android.graphics.Path, @NonNull android.graphics.Paint)` | 9 | direct | hard | `OH_Drawing_CanvasDrawPath` | `native_drawing.OH_Drawing_Canvas` |
| `drawRect` | `void drawRect(@NonNull android.graphics.RectF, @NonNull android.graphics.Paint)` | 9 | direct | rewrite | `OH_Drawing_CanvasDrawRect` | `native_drawing.OH_Drawing_Canvas` |
| `drawRect` | `void drawRect(@NonNull android.graphics.Rect, @NonNull android.graphics.Paint)` | 9 | direct | rewrite | `OH_Drawing_CanvasDrawRect` | `native_drawing.OH_Drawing_Canvas` |
| `drawRect` | `void drawRect(float, float, float, float, @NonNull android.graphics.Paint)` | 9 | direct | rewrite | `OH_Drawing_CanvasDrawRect` | `native_drawing.OH_Drawing_Canvas` |
| `drawRoundRect` | `void drawRoundRect(@NonNull android.graphics.RectF, float, float, @NonNull android.graphics.Paint)` | 9 | direct | rewrite | `OH_Drawing_CanvasDrawRoundRect` | `native_drawing.OH_Drawing_Canvas` |
| `drawRoundRect` | `void drawRoundRect(float, float, float, float, float, float, @NonNull android.graphics.Paint)` | 9 | direct | rewrite | `OH_Drawing_CanvasDrawRoundRect` | `native_drawing.OH_Drawing_Canvas` |
| `getSaveCount` | `int getSaveCount()` | 9 | direct | rewrite | `OH_Drawing_CanvasGetSaveCount` | `native_drawing.OH_Drawing_Canvas` |
| `restore` | `void restore()` | 9 | direct | hard | `OH_Drawing_CanvasRestore` | `native_drawing.OH_Drawing_Canvas` |
| `restoreToCount` | `void restoreToCount(int)` | 9 | direct | hard | `OH_Drawing_CanvasRestoreToCount` | `native_drawing.OH_Drawing_Canvas` |
| `rotate` | `void rotate(float)` | 9 | direct | rewrite | `OH_Drawing_CanvasRotate` | `native_drawing.OH_Drawing_Canvas` |
| `rotate` | `final void rotate(float, float, float)` | 9 | direct | rewrite | `OH_Drawing_CanvasRotate` | `native_drawing.OH_Drawing_Canvas` |
| `save` | `int save()` | 9 | direct | rewrite | `OH_Drawing_CanvasSave` | `native_drawing.OH_Drawing_Canvas` |
| `scale` | `void scale(float, float)` | 9 | direct | rewrite | `OH_Drawing_CanvasScale` | `native_drawing.OH_Drawing_Canvas` |
| `scale` | `final void scale(float, float, float, float)` | 9 | direct | rewrite | `OH_Drawing_CanvasScale` | `native_drawing.OH_Drawing_Canvas` |
| `translate` | `void translate(float, float)` | 9 | direct | rewrite | `OH_Drawing_CanvasTranslate` | `native_drawing.OH_Drawing_Canvas` |
| `clipOutPath` | `boolean clipOutPath(@NonNull android.graphics.Path)` | 8 | direct | rewrite | `OH_Drawing_CanvasClipPath` | `native_drawing.OH_Drawing_Canvas` |
| `clipOutRect` | `boolean clipOutRect(@NonNull android.graphics.RectF)` | 8 | direct | impossible | `OH_Drawing_CanvasClipRect` | `native_drawing.OH_Drawing_Canvas` |
| `clipOutRect` | `boolean clipOutRect(@NonNull android.graphics.Rect)` | 8 | direct | impossible | `OH_Drawing_CanvasClipRect` | `native_drawing.OH_Drawing_Canvas` |
| `clipOutRect` | `boolean clipOutRect(float, float, float, float)` | 8 | direct | impossible | `OH_Drawing_CanvasClipRect` | `native_drawing.OH_Drawing_Canvas` |
| `clipOutRect` | `boolean clipOutRect(int, int, int, int)` | 8 | direct | impossible | `OH_Drawing_CanvasClipRect` | `native_drawing.OH_Drawing_Canvas` |
| `drawARGB` | `void drawARGB(int, int, int, int)` | 8 | direct | impossible | `OH_Drawing_CanvasClear` | `native_drawing.OH_Drawing_Canvas` |
| `drawColor` | `void drawColor(@ColorInt int)` | 8 | direct | rewrite | `OH_Drawing_CanvasClear` | `native_drawing.OH_Drawing_Canvas` |
| `drawColor` | `void drawColor(@ColorLong long)` | 8 | direct | rewrite | `OH_Drawing_CanvasClear` | `native_drawing.OH_Drawing_Canvas` |
| `drawColor` | `void drawColor(@ColorInt int, @NonNull android.graphics.PorterDuff.Mode)` | 8 | direct | rewrite | `OH_Drawing_CanvasClear` | `native_drawing.OH_Drawing_Canvas` |
| `drawColor` | `void drawColor(@ColorInt int, @NonNull android.graphics.BlendMode)` | 8 | direct | rewrite | `OH_Drawing_CanvasClear` | `native_drawing.OH_Drawing_Canvas` |
| `drawColor` | `void drawColor(@ColorLong long, @NonNull android.graphics.BlendMode)` | 8 | direct | rewrite | `OH_Drawing_CanvasClear` | `native_drawing.OH_Drawing_Canvas` |
| `drawText` | `void drawText(@NonNull char[], int, int, float, float, @NonNull android.graphics.Paint)` | 8 | direct | impossible | `OH_Drawing_CanvasDrawTextBlob` | `native_drawing.OH_Drawing_Canvas` |
| `drawText` | `void drawText(@NonNull String, float, float, @NonNull android.graphics.Paint)` | 8 | direct | impossible | `OH_Drawing_CanvasDrawTextBlob` | `native_drawing.OH_Drawing_Canvas` |
| `drawText` | `void drawText(@NonNull String, int, int, float, float, @NonNull android.graphics.Paint)` | 8 | direct | impossible | `OH_Drawing_CanvasDrawTextBlob` | `native_drawing.OH_Drawing_Canvas` |
| `drawText` | `void drawText(@NonNull CharSequence, int, int, float, float, @NonNull android.graphics.Paint)` | 8 | direct | impossible | `OH_Drawing_CanvasDrawTextBlob` | `native_drawing.OH_Drawing_Canvas` |
| `setBitmap` | `void setBitmap(@Nullable android.graphics.Bitmap)` | 8 | direct | impossible | `OH_Drawing_CanvasBind` | `native_drawing.OH_Drawing_Canvas` |
| `drawLines` | `void drawLines(@NonNull @Size(multiple=4) float[], int, int, @NonNull android.graphics.Paint)` | 7 | near | impossible | `OH_Drawing_CanvasDrawLine` | `native_drawing.OH_Drawing_Canvas` |
| `drawLines` | `void drawLines(@NonNull @Size(multiple=4) float[], @NonNull android.graphics.Paint)` | 7 | near | impossible | `OH_Drawing_CanvasDrawLine` | `native_drawing.OH_Drawing_Canvas` |
| `drawRGB` | `void drawRGB(int, int, int)` | 7 | near | impossible | `OH_Drawing_CanvasClear` | `native_drawing.OH_Drawing_Canvas` |
| `getHeight` | `int getHeight()` | 7 | near | rewrite | `OH_Drawing_BitmapGetHeight` | `native_drawing.OH_Drawing_Bitmap` |
| `getWidth` | `int getWidth()` | 7 | near | rewrite | `OH_Drawing_BitmapGetWidth` | `native_drawing.OH_Drawing_Bitmap` |
| `drawPaint` | `void drawPaint(@NonNull android.graphics.Paint)` | 6 | near | hard | `OH_Drawing_CanvasClear` | `native_drawing.OH_Drawing_Canvas` |
| `drawPoint` | `void drawPoint(float, float, @NonNull android.graphics.Paint)` | 6 | near | hard | `OH_Drawing_CanvasDrawLine` | `native_drawing.OH_Drawing_Canvas` |
| `drawTextRun` | `void drawTextRun(@NonNull char[], int, int, int, int, float, float, boolean, @NonNull android.graphics.Paint)` | 6 | near | impossible | `OH_Drawing_CanvasDrawTextBlob` | `native_drawing.OH_Drawing_Canvas` |
| `drawTextRun` | `void drawTextRun(@NonNull CharSequence, int, int, int, int, float, float, boolean, @NonNull android.graphics.Paint)` | 6 | near | impossible | `OH_Drawing_CanvasDrawTextBlob` | `native_drawing.OH_Drawing_Canvas` |
| `drawTextRun` | `void drawTextRun(@NonNull android.graphics.text.MeasuredText, int, int, int, int, float, float, boolean, @NonNull android.graphics.Paint)` | 6 | near | impossible | `OH_Drawing_CanvasDrawTextBlob` | `native_drawing.OH_Drawing_Canvas` |
| `saveLayer` | `int saveLayer(@Nullable android.graphics.RectF, @Nullable android.graphics.Paint)` | 6 | near | impossible | `OH_Drawing_CanvasSave` | `native_drawing.OH_Drawing_Canvas` |
| `saveLayer` | `int saveLayer(float, float, float, float, @Nullable android.graphics.Paint)` | 6 | near | impossible | `OH_Drawing_CanvasSave` | `native_drawing.OH_Drawing_Canvas` |
| `concat` | `void concat(@Nullable android.graphics.Matrix)` | 5 | partial | rewrite | `concat` | `native_drawing.OH_Drawing_Canvas` |
| `saveLayerAlpha` | `int saveLayerAlpha(@Nullable android.graphics.RectF, int)` | 5 | partial | impossible | `OH_Drawing_CanvasSave` | `native_drawing.OH_Drawing_Canvas` |
| `saveLayerAlpha` | `int saveLayerAlpha(float, float, float, float, int)` | 5 | partial | impossible | `OH_Drawing_CanvasSave` | `native_drawing.OH_Drawing_Canvas` |
| `setMatrix` | `void setMatrix(@Nullable android.graphics.Matrix)` | 5 | partial | hard | `OH_Drawing_MatrixSetMatrix` | `native_drawing.OH_Drawing_Canvas` |

## Gap Descriptions (per method)

- **`Canvas`**: Constructor
- **`Canvas`**: Constructor
- **`clipPath`**: Direct; supports ClipOp and antiAlias
- **`clipRect`**: Direct; supports ClipOp and antiAlias
- **`clipRect`**: Direct; supports ClipOp and antiAlias
- **`clipRect`**: Direct; supports ClipOp and antiAlias
- **`clipRect`**: Direct; supports ClipOp and antiAlias
- **`drawArc`**: Direct mapping
- **`drawArc`**: Direct mapping
- **`drawBitmap`**: Direct mapping
- **`drawBitmap`**: Direct mapping
- **`drawBitmap`**: Direct mapping
- **`drawBitmap`**: Direct mapping
- **`drawCircle`**: Direct mapping
- **`drawLine`**: Direct mapping
- **`drawOval`**: Direct mapping
- **`drawOval`**: Direct mapping
- **`drawPath`**: Direct mapping
- **`drawRect`**: Direct mapping
- **`drawRect`**: Direct mapping
- **`drawRect`**: Direct mapping
- **`drawRoundRect`**: Direct mapping via OH_Drawing_RoundRect
- **`drawRoundRect`**: Direct mapping via OH_Drawing_RoundRect
- **`getSaveCount`**: Direct mapping
- **`restore`**: Direct mapping
- **`restoreToCount`**: Direct mapping
- **`rotate`**: Direct mapping; OH version takes pivot point
- **`rotate`**: Direct mapping; OH version takes pivot point
- **`save`**: Direct mapping
- **`scale`**: Direct mapping
- **`scale`**: Direct mapping
- **`translate`**: Direct mapping
- **`clipOutPath`**: Use DIFFERENCE ClipOp
- **`clipOutRect`**: Use DIFFERENCE ClipOp
- **`clipOutRect`**: Use DIFFERENCE ClipOp
- **`clipOutRect`**: Use DIFFERENCE ClipOp
- **`clipOutRect`**: Use DIFFERENCE ClipOp
- **`drawARGB`**: Clear with composed ARGB color
- **`drawColor`**: Direct mapping (clear fills entire canvas)
- **`drawColor`**: Direct mapping (clear fills entire canvas)
- **`drawColor`**: Direct mapping (clear fills entire canvas)
- **`drawColor`**: Direct mapping (clear fills entire canvas)
- **`drawColor`**: Direct mapping (clear fills entire canvas)
- **`drawText`**: Build TextBlob from text then draw
- **`drawText`**: Build TextBlob from text then draw
- **`drawText`**: Build TextBlob from text then draw
- **`drawText`**: Build TextBlob from text then draw
- **`setBitmap`**: Bind bitmap to canvas
- **`drawLines`**: Call DrawLine in a loop
- **`drawLines`**: Call DrawLine in a loop
- **`drawRGB`**: Clear with composed RGB color
- **`getHeight`**: Get from bound bitmap
- **`getWidth`**: Get from bound bitmap
- **`drawPaint`**: Approximate: clear with paint color
- **`drawPoint`**: Draw zero-length line at point
- **`drawTextRun`**: Build TextBlob then draw; complex text shaping needed
- **`drawTextRun`**: Build TextBlob then draw; complex text shaping needed
- **`drawTextRun`**: Build TextBlob then draw; complex text shaping needed
- **`saveLayer`**: OH save does not support layer bounds/alpha; approximate
- **`saveLayer`**: OH save does not support layer bounds/alpha; approximate
- **`concat`**: No direct concat; build matrix and apply
- **`saveLayerAlpha`**: OH save does not support alpha layers
- **`saveLayerAlpha`**: OH save does not support alpha layers
- **`setMatrix`**: No setMatrix; use save/restore + matrix ops

## Stub APIs (score < 5): 26 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `drawPoints` | 4 | partial | throw UnsupportedOperationException |
| `drawPoints` | 4 | partial | throw UnsupportedOperationException |
| `getClipBounds` | 4 | partial | Return safe default (null/false/0/empty) |
| `skew` | 4 | partial | throw UnsupportedOperationException |
| `drawDoubleRoundRect` | 3 | composite | throw UnsupportedOperationException |
| `drawDoubleRoundRect` | 3 | composite | throw UnsupportedOperationException |
| `drawPicture` | 3 | composite | throw UnsupportedOperationException |
| `drawPicture` | 3 | composite | throw UnsupportedOperationException |
| `drawPicture` | 3 | composite | throw UnsupportedOperationException |
| `drawTextOnPath` | 3 | composite | Store callback, never fire |
| `drawTextOnPath` | 3 | composite | Store callback, never fire |
| `getDensity` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMaximumBitmapHeight` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMaximumBitmapWidth` | 3 | composite | Return safe default (null/false/0/empty) |
| `isHardwareAccelerated` | 3 | composite | Return safe default (null/false/0/empty) |
| `isOpaque` | 3 | composite | Return safe default (null/false/0/empty) |
| `quickReject` | 3 | composite | throw UnsupportedOperationException |
| `quickReject` | 3 | composite | throw UnsupportedOperationException |
| `quickReject` | 3 | composite | throw UnsupportedOperationException |
| `setDensity` | 3 | composite | Log warning + no-op |
| `disableZ` | 2 | composite | Return safe default (null/false/0/empty) |
| `drawBitmapMesh` | 2 | composite | throw UnsupportedOperationException |
| `drawRenderNode` | 2 | composite | throw UnsupportedOperationException |
| `drawVertices` | 2 | composite | throw UnsupportedOperationException |
| `enableZ` | 2 | composite | throw UnsupportedOperationException |
| `setDrawFilter` | 2 | composite | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 64 methods that have score >= 5
2. Stub 26 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Canvas`:

- `android.graphics.Paint` (not yet shimmed)
- `android.graphics.Bitmap` (not yet shimmed)

## Quality Gates

Before marking `android.graphics.Canvas` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 90 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 64 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
