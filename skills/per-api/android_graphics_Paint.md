# SKILL: android.graphics.Paint

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Paint`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Paint` |
| **Package** | `android.graphics` |
| **Total Methods** | 115 |
| **Avg Score** | 5.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 45 (39%) |
| **Partial/Composite** | 67 (58%) |
| **No Mapping** | 3 (2%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 5 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 64 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getAlpha` | `int getAlpha()` | 9 | direct | rewrite | `OH_Drawing_PenGetAlpha` | `native_drawing.OH_Drawing_Pen` |
| `getStrokeCap` | `android.graphics.Paint.Cap getStrokeCap()` | 9 | direct | impossible | `OH_Drawing_PenGetCap` | `native_drawing.OH_Drawing_Pen` |
| `getStrokeJoin` | `android.graphics.Paint.Join getStrokeJoin()` | 9 | direct | impossible | `OH_Drawing_PenGetJoin` | `native_drawing.OH_Drawing_Pen` |
| `getStrokeMiter` | `float getStrokeMiter()` | 9 | direct | impossible | `OH_Drawing_PenGetMiterLimit` | `native_drawing.OH_Drawing_Pen` |
| `getStrokeWidth` | `float getStrokeWidth()` | 9 | direct | impossible | `OH_Drawing_PenGetWidth` | `native_drawing.OH_Drawing_Pen` |
| `isAntiAlias` | `final boolean isAntiAlias()` | 9 | direct | rewrite | `OH_Drawing_PenIsAntiAlias` | `native_drawing.OH_Drawing_Pen` |
| `setAlpha` | `void setAlpha(int)` | 9 | direct | hard | `OH_Drawing_PenSetAlpha` | `native_drawing.OH_Drawing_Pen` |
| `setAntiAlias` | `void setAntiAlias(boolean)` | 9 | direct | hard | `OH_Drawing_PenSetAntiAlias` | `native_drawing.OH_Drawing_Pen` |
| `setColor` | `void setColor(@ColorInt int)` | 9 | direct | hard | `OH_Drawing_PenSetColor` | `native_drawing.OH_Drawing_Pen` |
| `setColor` | `void setColor(@ColorLong long)` | 9 | direct | hard | `OH_Drawing_PenSetColor` | `native_drawing.OH_Drawing_Pen` |
| `setStrokeCap` | `void setStrokeCap(android.graphics.Paint.Cap)` | 9 | direct | impossible | `OH_Drawing_PenSetCap` | `native_drawing.OH_Drawing_Pen` |
| `setStrokeJoin` | `void setStrokeJoin(android.graphics.Paint.Join)` | 9 | direct | impossible | `OH_Drawing_PenSetJoin` | `native_drawing.OH_Drawing_Pen` |
| `setStrokeMiter` | `void setStrokeMiter(float)` | 9 | direct | impossible | `OH_Drawing_PenSetMiterLimit` | `native_drawing.OH_Drawing_Pen` |
| `setStrokeWidth` | `void setStrokeWidth(float)` | 9 | direct | impossible | `OH_Drawing_PenSetWidth` | `native_drawing.OH_Drawing_Pen` |
| `setTextSize` | `void setTextSize(float)` | 9 | direct | hard | `OH_Drawing_FontSetTextSize` | `native_drawing.OH_Drawing_Font` |
| `setTextSkewX` | `void setTextSkewX(float)` | 9 | direct | hard | `OH_Drawing_FontSetTextSkewX` | `native_drawing.OH_Drawing_Font` |
| `Paint` | `Paint()` | 8 | direct | impossible | `OH_Drawing_PenCreate` | `native_drawing.OH_Drawing_Pen` |
| `Paint` | `Paint(int)` | 8 | direct | impossible | `OH_Drawing_PenCreate` | `native_drawing.OH_Drawing_Pen` |
| `Paint` | `Paint(android.graphics.Paint)` | 8 | direct | impossible | `OH_Drawing_PenCreate` | `native_drawing.OH_Drawing_Pen` |
| `setARGB` | `void setARGB(int, int, int, int)` | 8 | direct | impossible | `OH_Drawing_PenSetColor` | `native_drawing.OH_Drawing_Pen` |
| `setColorFilter` | `android.graphics.ColorFilter setColorFilter(android.graphics.ColorFilter)` | 8 | direct | rewrite | `OH_Drawing_FilterSetColorFilter` | `native_drawing.OH_Drawing_Filter` |
| `setFakeBoldText` | `void setFakeBoldText(boolean)` | 8 | direct | hard | `OH_Drawing_FontSetFakeBoldText` | `native_drawing.OH_Drawing_Font` |
| `setLetterSpacing` | `void setLetterSpacing(float)` | 8 | direct | hard | `OH_Drawing_SetTextStyleLetterSpacing` | `native_drawing.OH_Drawing_TextStyle` |
| `setMaskFilter` | `android.graphics.MaskFilter setMaskFilter(android.graphics.MaskFilter)` | 8 | direct | rewrite | `OH_Drawing_FilterSetMaskFilter` | `native_drawing.OH_Drawing_Filter` |
| `setShader` | `android.graphics.Shader setShader(android.graphics.Shader)` | 8 | direct | impossible | `OH_Drawing_PenSetShaderEffect` | `native_drawing.OH_Drawing_Pen` |
| `setTextLocale` | `void setTextLocale(@NonNull java.util.Locale)` | 8 | direct | hard | `OH_Drawing_SetTextStyleLocale` | `native_drawing.OH_Drawing_TextStyle` |
| `setTypeface` | `android.graphics.Typeface setTypeface(android.graphics.Typeface)` | 8 | direct | rewrite | `OH_Drawing_FontSetTypeface` | `native_drawing.OH_Drawing_Font` |
| `setWordSpacing` | `void setWordSpacing(@Px float)` | 8 | direct | hard | `OH_Drawing_SetTextStyleWordSpacing` | `native_drawing.OH_Drawing_TextStyle` |
| `getFontSpacing` | `float getFontSpacing()` | 7 | near | impossible | `OH_Drawing_TypographyGetLineHeight` | `native_drawing.OH_Drawing_Typography` |
| `measureText` | `float measureText(char[], int, int)` | 7 | near | impossible | `OH_Drawing_TypographyGetMaxIntrinsicWidth` | `native_drawing.OH_Drawing_Typography` |
| `measureText` | `float measureText(String, int, int)` | 7 | near | impossible | `OH_Drawing_TypographyGetMaxIntrinsicWidth` | `native_drawing.OH_Drawing_Typography` |
| `measureText` | `float measureText(String)` | 7 | near | impossible | `OH_Drawing_TypographyGetMaxIntrinsicWidth` | `native_drawing.OH_Drawing_Typography` |
| `measureText` | `float measureText(CharSequence, int, int)` | 7 | near | impossible | `OH_Drawing_TypographyGetMaxIntrinsicWidth` | `native_drawing.OH_Drawing_Typography` |
| `setLinearText` | `void setLinearText(boolean)` | 7 | near | hard | `OH_Drawing_FontSetLinearText` | `native_drawing.OH_Drawing_Font` |
| `setStrikeThruText` | `void setStrikeThruText(boolean)` | 7 | near | impossible | `OH_Drawing_SetTextStyleDecoration` | `native_drawing.OH_Drawing_TextStyle` |
| `setStyle` | `void setStyle(android.graphics.Paint.Style)` | 7 | near | impossible | `—` | `native_drawing.OH_Drawing_Pen` |
| `setTextAlign` | `void setTextAlign(android.graphics.Paint.Align)` | 7 | near | hard | `OH_Drawing_SetTypographyTextAlign` | `native_drawing.OH_Drawing_TypographyStyle` |
| `setUnderlineText` | `void setUnderlineText(boolean)` | 7 | near | impossible | `OH_Drawing_SetTextStyleDecoration` | `native_drawing.OH_Drawing_TextStyle` |
| `ascent` | `float ascent()` | 6 | near | impossible | `OH_Drawing_TypographyGetAlphabeticBaseline` | `native_drawing.OH_Drawing_Typography` |
| `descent` | `float descent()` | 6 | near | impossible | `OH_Drawing_TypographyGetIdeographicBaseline` | `native_drawing.OH_Drawing_Typography` |
| `reset` | `void reset()` | 6 | near | hard | `OH_Drawing_PenDestroy+PenCreate` | `native_drawing.OH_Drawing_Pen` |
| `setBlendMode` | `void setBlendMode(@Nullable android.graphics.BlendMode)` | 6 | near | impossible | `—` | `native_drawing.OH_Drawing_Pen` |
| `setElegantTextHeight` | `void setElegantTextHeight(boolean)` | 6 | near | impossible | `OH_Drawing_SetTextStyleFontHeight` | `native_drawing.OH_Drawing_TextStyle` |
| `setFilterBitmap` | `void setFilterBitmap(boolean)` | 6 | near | impossible | `OH_Drawing_FilterCreate` | `native_drawing.OH_Drawing_Filter` |
| `setTextLocales` | `void setTextLocales(@NonNull @Size(min=1) android.os.LocaleList)` | 6 | near | hard | `OH_Drawing_SetTextStyleLocale` | `native_drawing.OH_Drawing_TextStyle` |
| `getFontMetrics` | `float getFontMetrics(android.graphics.Paint.FontMetrics)` | 5 | partial | impossible | `—` | `native_drawing.OH_Drawing_Typography` |
| `getFontMetrics` | `android.graphics.Paint.FontMetrics getFontMetrics()` | 5 | partial | impossible | `—` | `native_drawing.OH_Drawing_Typography` |
| `getFontMetricsInt` | `int getFontMetricsInt(android.graphics.Paint.FontMetricsInt)` | 5 | partial | impossible | `—` | `native_drawing.OH_Drawing_Typography` |
| `getFontMetricsInt` | `android.graphics.Paint.FontMetricsInt getFontMetricsInt()` | 5 | partial | impossible | `—` | `native_drawing.OH_Drawing_Typography` |
| `getLetterSpacing` | `float getLetterSpacing()` | 5 | partial | rewrite | `OH_Drawing_SetTextStyleLetterSpacing` | `native_drawing.OH_Drawing_TextStyle` |
| `getStyle` | `android.graphics.Paint.Style getStyle()` | 5 | partial | rewrite | `get` | `native_drawing.OH_Drawing_Pen` |
| `getTextAlign` | `android.graphics.Paint.Align getTextAlign()` | 5 | partial | rewrite | `getRectangleById` | `native_drawing.OH_Drawing_TypographyStyle` |
| `getTextBounds` | `void getTextBounds(String, int, int, android.graphics.Rect)` | 5 | partial | impossible | `—` | `native_drawing.OH_Drawing_Typography` |
| `getTextBounds` | `void getTextBounds(@NonNull CharSequence, int, int, @NonNull android.graphics.Rect)` | 5 | partial | impossible | `—` | `native_drawing.OH_Drawing_Typography` |
| `getTextBounds` | `void getTextBounds(char[], int, int, android.graphics.Rect)` | 5 | partial | impossible | `—` | `native_drawing.OH_Drawing_Typography` |
| `getTextSize` | `float getTextSize()` | 5 | partial | rewrite | `OH_Drawing_FontSetTextSize` | `native_drawing.OH_Drawing_Font` |
| `getTextSkewX` | `float getTextSkewX()` | 5 | partial | rewrite | `OH_Drawing_FontSetTextSkewX` | `native_drawing.OH_Drawing_Font` |
| `isFakeBoldText` | `final boolean isFakeBoldText()` | 5 | partial | rewrite | `OH_Drawing_FontSetFakeBoldText` | `native_drawing.OH_Drawing_Font` |
| `isFilterBitmap` | `final boolean isFilterBitmap()` | 5 | partial | impossible | `—` | `native_drawing.OH_Drawing_Filter` |
| `isLinearText` | `final boolean isLinearText()` | 5 | partial | rewrite | `OH_Drawing_FontSetLinearText` | `native_drawing.OH_Drawing_Font` |
| `isStrikeThruText` | `final boolean isStrikeThruText()` | 5 | partial | impossible | `—` | `native_drawing.OH_Drawing_TextStyle` |
| `isUnderlineText` | `final boolean isUnderlineText()` | 5 | partial | rewrite | `OH_Drawing_FontSetLinearText` | `native_drawing.OH_Drawing_TextStyle` |
| `set` | `void set(android.graphics.Paint)` | 5 | partial | hard | `set` | `native_drawing.OH_Drawing_Pen` |
| `setXfermode` | `android.graphics.Xfermode setXfermode(android.graphics.Xfermode)` | 5 | partial | impossible | `—` | `native_drawing.OH_Drawing_Pen` |

## Gap Descriptions (per method)

- **`getAlpha`**: Direct mapping
- **`getStrokeCap`**: Direct mapping
- **`getStrokeJoin`**: Direct mapping
- **`getStrokeMiter`**: Direct mapping
- **`getStrokeWidth`**: Direct mapping
- **`isAntiAlias`**: Direct mapping
- **`setAlpha`**: Direct mapping
- **`setAntiAlias`**: Direct mapping
- **`setColor`**: Direct mapping; also set on Brush
- **`setColor`**: Direct mapping; also set on Brush
- **`setStrokeCap`**: Direct; map Cap enum to OH_Drawing_PenLineCapStyle
- **`setStrokeJoin`**: Direct; map Join enum to OH_Drawing_PenLineJoinStyle
- **`setStrokeMiter`**: Direct mapping
- **`setStrokeWidth`**: Direct mapping
- **`setTextSize`**: Direct mapping via Font
- **`setTextSkewX`**: Direct mapping via Font
- **`Paint`**: Constructor; OH splits Paint into Pen (stroke) + Brush (fill)
- **`Paint`**: Constructor; OH splits Paint into Pen (stroke) + Brush (fill)
- **`Paint`**: Constructor; OH splits Paint into Pen (stroke) + Brush (fill)
- **`setARGB`**: Compose ARGB then call PenSetColor
- **`setColorFilter`**: Via OH_Drawing_Filter + OH_Drawing_ColorFilter
- **`setFakeBoldText`**: Direct mapping via Font
- **`setLetterSpacing`**: Via Typography TextStyle
- **`setMaskFilter`**: Via OH_Drawing_Filter + OH_Drawing_MaskFilter
- **`setShader`**: Map Shader subclass to OH_Drawing_ShaderEffect
- **`setTextLocale`**: Direct mapping via TextStyle
- **`setTypeface`**: Via OH_Drawing_Font
- **`setWordSpacing`**: Via Typography TextStyle
- **`getFontSpacing`**: Line height approximates font spacing
- **`measureText`**: Layout single line then get width
- **`measureText`**: Layout single line then get width
- **`measureText`**: Layout single line then get width
- **`measureText`**: Layout single line then get width
- **`setLinearText`**: Mapped via OH_Drawing_Font not Pen
- **`setStrikeThruText`**: Via Typography text decoration
- **`setStyle`**: STROKE→attach Pen; FILL→attach Brush; FILL_AND_STROKE→both
- **`setTextAlign`**: Text alignment via Typography
- **`setUnderlineText`**: Via Typography text decoration
- **`ascent`**: Approximate; use typography metrics
- **`descent`**: Approximate; use typography metrics
- **`reset`**: No direct reset; destroy and recreate
- **`setBlendMode`**: OH_Drawing_BlendMode enum exists; set via brush/pen
- **`setElegantTextHeight`**: Approximate via font height
- **`setFilterBitmap`**: Use OH_Drawing_Filter for bitmap filtering
- **`setTextLocales`**: OH supports single locale; use first
- **`getFontMetrics`**: Compute from typography baselines and height
- **`getFontMetrics`**: Compute from typography baselines and height
- **`getFontMetricsInt`**: Compute from typography baselines and height; cast to int
- **`getFontMetricsInt`**: Compute from typography baselines and height; cast to int
- **`getLetterSpacing`**: No getter; track locally
- **`getStyle`**: Must track locally; OH has separate Pen/Brush model
- **`getTextAlign`**: No getter; track locally
- **`getTextBounds`**: Use typography layout then get rects for range
- **`getTextBounds`**: Use typography layout then get rects for range
- **`getTextBounds`**: Use typography layout then get rects for range
- **`getTextSize`**: No getter in OH Drawing Font
- **`getTextSkewX`**: No getter in OH Drawing Font
- **`isFakeBoldText`**: No getter in OH Drawing Font
- **`isFilterBitmap`**: No direct getter
- **`isLinearText`**: No getter in OH Drawing Font
- **`isStrikeThruText`**: Check decoration flags
- **`isUnderlineText`**: Check decoration flags
- **`set`**: Copy all properties manually; no single copy function
- **`setXfermode`**: Use OH_Drawing_BlendMode; set via blend mode APIs

## Stub APIs (score < 5): 51 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `breakText` | 4 | partial | throw UnsupportedOperationException |
| `breakText` | 4 | partial | throw UnsupportedOperationException |
| `breakText` | 4 | partial | throw UnsupportedOperationException |
| `getColorFilter` | 4 | partial | Return safe default (null/false/0/empty) |
| `getFlags` | 4 | partial | Return safe default (null/false/0/empty) |
| `getMaskFilter` | 4 | partial | Return safe default (null/false/0/empty) |
| `getOffsetForAdvance` | 4 | partial | Return safe default (null/false/0/empty) |
| `getOffsetForAdvance` | 4 | partial | Return safe default (null/false/0/empty) |
| `getRunAdvance` | 4 | partial | Return safe default (null/false/0/empty) |
| `getRunAdvance` | 4 | partial | Return safe default (null/false/0/empty) |
| `getShader` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTextRunAdvances` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTextRunCursor` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTextRunCursor` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTextWidths` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTextWidths` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTextWidths` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTextWidths` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTypeface` | 4 | partial | Return safe default (null/false/0/empty) |
| `getXfermode` | 4 | partial | Return safe default (null/false/0/empty) |
| `isElegantTextHeight` | 4 | partial | Return safe default (null/false/0/empty) |
| `setFlags` | 4 | partial | Log warning + no-op |
| `clearShadowLayer` | 3 | composite | throw UnsupportedOperationException |
| `equalsForTextMeasurement` | 3 | composite | throw UnsupportedOperationException |
| `getEndHyphenEdit` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFillPath` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFontFeatureSettings` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFontVariationSettings` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHinting` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPathEffect` | 3 | composite | Return safe default (null/false/0/empty) |
| `getStartHyphenEdit` | 3 | composite | Return dummy instance / no-op |
| `getTextPath` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTextPath` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTextScaleX` | 3 | composite | Return safe default (null/false/0/empty) |
| `hasGlyph` | 3 | composite | Return safe default (null/false/0/empty) |
| `isDither` | 3 | composite | Return safe default (null/false/0/empty) |
| `isSubpixelText` | 3 | composite | Return safe default (null/false/0/empty) |
| `setDither` | 3 | composite | Log warning + no-op |
| `setEndHyphenEdit` | 3 | composite | Log warning + no-op |
| `setFontFeatureSettings` | 3 | composite | Log warning + no-op |
| `setFontVariationSettings` | 3 | composite | Log warning + no-op |
| `setHinting` | 3 | composite | Log warning + no-op |
| `setPathEffect` | 3 | composite | Log warning + no-op |
| `setShadowLayer` | 3 | composite | Log warning + no-op |
| `setShadowLayer` | 3 | composite | Log warning + no-op |
| `setStartHyphenEdit` | 3 | composite | Return dummy instance / no-op |
| `setSubpixelText` | 3 | composite | Log warning + no-op |
| `setTextScaleX` | 3 | composite | Log warning + no-op |
| `getShadowLayerDx` | 1 | none | Return safe default (null/false/0/empty) |
| `getShadowLayerDy` | 1 | none | Return safe default (null/false/0/empty) |
| `getShadowLayerRadius` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 64 methods that have score >= 5
2. Stub 51 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Paint`:


## Quality Gates

Before marking `android.graphics.Paint` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 115 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 64 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
