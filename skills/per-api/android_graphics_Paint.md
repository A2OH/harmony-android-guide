# SKILL: android.graphics.Paint

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Paint`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Paint` |
| **Package** | `android.graphics` |
| **Total Methods** | 115 |
| **Avg Score** | 2.0 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 8 (6%) |
| **Partial/Composite** | 32 (27%) |
| **No Mapping** | 75 (65%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 5 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setStrokeCap` | `void setStrokeCap(android.graphics.Paint.Cap)` | 9 | direct | impossible | `OH_Drawing_PenSetCap` | `@ohos.graphics.drawing (NDK).OH_Drawing_Pen` |
| `setStrokeJoin` | `void setStrokeJoin(android.graphics.Paint.Join)` | 9 | direct | impossible | `OH_Drawing_PenSetJoin` | `@ohos.graphics.drawing (NDK).OH_Drawing_Pen` |
| `setStrokeWidth` | `void setStrokeWidth(float)` | 9 | direct | impossible | `OH_Drawing_PenSetWidth` | `@ohos.graphics.drawing (NDK).OH_Drawing_Pen` |
| `setAntiAlias` | `void setAntiAlias(boolean)` | 7 | near | hard | `OH_Drawing_PenSetAntiAlias/BrushSetAntiAlias` | `@ohos.graphics.drawing (NDK).OH_Drawing_Pen/Brush` |
| `setColor` | `void setColor(@ColorInt int)` | 7 | near | hard | `OH_Drawing_PenSetColor/BrushSetColor` | `@ohos.graphics.drawing (NDK).OH_Drawing_Pen/Brush` |
| `setColor` | `void setColor(@ColorLong long)` | 7 | near | hard | `OH_Drawing_PenSetColor/BrushSetColor` | `@ohos.graphics.drawing (NDK).OH_Drawing_Pen/Brush` |
| `setTextSize` | `void setTextSize(float)` | 7 | near | hard | `OH_Drawing_FontSetTextSize` | `@ohos.graphics.drawing (NDK).OH_Drawing_Font` |
| `setTypeface` | `android.graphics.Typeface setTypeface(android.graphics.Typeface)` | 7 | near | rewrite | `OH_Drawing_FontSetTypeface` | `@ohos.graphics.drawing (NDK).OH_Drawing_Font` |

## Gap Descriptions (per method)

- **`setStrokeCap`**: Enum mapping
- **`setStrokeJoin`**: Enum mapping
- **`setStrokeWidth`**: Direct
- **`setAntiAlias`**: Set on both
- **`setColor`**: Set on both pen and brush
- **`setColor`**: Set on both pen and brush
- **`setTextSize`**: Text on Font not Paint
- **`setTypeface`**: Via OH_Drawing_Typeface

## Stub APIs (score < 5): 107 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getRunAdvance` | 4 | composite | Return safe default (null/false/0/empty) |
| `getRunAdvance` | 4 | composite | Return safe default (null/false/0/empty) |
| `setFakeBoldText` | 3 | composite | Log warning + no-op |
| `setLinearText` | 3 | composite | Log warning + no-op |
| `reset` | 3 | composite | Log warning + no-op |
| `set` | 3 | composite | Log warning + no-op |
| `setTextLocale` | 3 | composite | Log warning + no-op |
| `setLetterSpacing` | 3 | composite | Log warning + no-op |
| `setTextSkewX` | 3 | composite | Log warning + no-op |
| `setTextLocales` | 3 | composite | Log warning + no-op |
| `setWordSpacing` | 3 | composite | Log warning + no-op |
| `setTextScaleX` | 3 | composite | Log warning + no-op |
| `setAlpha` | 3 | composite | Log warning + no-op |
| `setTextAlign` | 3 | composite | Log warning + no-op |
| `isFakeBoldText` | 3 | composite | Return safe default (null/false/0/empty) |
| `getColorFilter` | 3 | composite | Return safe default (null/false/0/empty) |
| `setColorFilter` | 3 | composite | Log warning + no-op |
| `getLetterSpacing` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTextSkewX` | 3 | composite | Return safe default (null/false/0/empty) |
| `isAntiAlias` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMaskFilter` | 3 | composite | Return safe default (null/false/0/empty) |
| `setMaskFilter` | 3 | composite | Log warning + no-op |
| `isLinearText` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTextSize` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTypeface` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTextAlign` | 2 | composite | Return safe default (null/false/0/empty) |
| `getTextScaleX` | 2 | composite | Return safe default (null/false/0/empty) |
| `getAlpha` | 2 | composite | Return safe default (null/false/0/empty) |
| `getFlags` | 2 | composite | Return safe default (null/false/0/empty) |
| `getStyle` | 2 | composite | Return safe default (null/false/0/empty) |
| `isElegantTextHeight` | 2 | composite | Return safe default (null/false/0/empty) |
| `isUnderlineText` | 2 | composite | Return safe default (null/false/0/empty) |
| `Paint` | 1 | none | throw UnsupportedOperationException |
| `Paint` | 1 | none | throw UnsupportedOperationException |
| `Paint` | 1 | none | throw UnsupportedOperationException |
| `ascent` | 1 | none | throw UnsupportedOperationException |
| `breakText` | 1 | none | throw UnsupportedOperationException |
| `breakText` | 1 | none | throw UnsupportedOperationException |
| `breakText` | 1 | none | throw UnsupportedOperationException |
| `clearShadowLayer` | 1 | none | throw UnsupportedOperationException |
| `descent` | 1 | none | throw UnsupportedOperationException |
| `equalsForTextMeasurement` | 1 | none | throw UnsupportedOperationException |
| `getEndHyphenEdit` | 1 | none | Return safe default (null/false/0/empty) |
| `getFillPath` | 1 | none | Return safe default (null/false/0/empty) |
| `getFontFeatureSettings` | 1 | none | Return safe default (null/false/0/empty) |
| `getFontMetrics` | 1 | none | Return safe default (null/false/0/empty) |
| `getFontMetrics` | 1 | none | Return safe default (null/false/0/empty) |
| `getFontMetricsInt` | 1 | none | Return safe default (null/false/0/empty) |
| `getFontMetricsInt` | 1 | none | Return safe default (null/false/0/empty) |
| `getFontSpacing` | 1 | none | Return safe default (null/false/0/empty) |
| `getFontVariationSettings` | 1 | none | Return safe default (null/false/0/empty) |
| `getHinting` | 1 | none | Return safe default (null/false/0/empty) |
| `getOffsetForAdvance` | 1 | none | Return safe default (null/false/0/empty) |
| `getOffsetForAdvance` | 1 | none | Return safe default (null/false/0/empty) |
| `getPathEffect` | 1 | none | Return safe default (null/false/0/empty) |
| `getShader` | 1 | none | Return safe default (null/false/0/empty) |
| `getShadowLayerDx` | 1 | none | Return safe default (null/false/0/empty) |
| `getShadowLayerDy` | 1 | none | Return safe default (null/false/0/empty) |
| `getShadowLayerRadius` | 1 | none | Return safe default (null/false/0/empty) |
| `getStartHyphenEdit` | 1 | none | Return dummy instance / no-op |
| `getStrokeCap` | 1 | none | Return safe default (null/false/0/empty) |
| `getStrokeJoin` | 1 | none | Return safe default (null/false/0/empty) |
| `getStrokeMiter` | 1 | none | Return safe default (null/false/0/empty) |
| `getStrokeWidth` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextBounds` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextBounds` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextBounds` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextPath` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextPath` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextRunAdvances` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextRunCursor` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextRunCursor` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextWidths` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextWidths` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextWidths` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextWidths` | 1 | none | Return safe default (null/false/0/empty) |
| `getXfermode` | 1 | none | Return safe default (null/false/0/empty) |
| `hasGlyph` | 1 | none | Return safe default (null/false/0/empty) |
| `isDither` | 1 | none | Return safe default (null/false/0/empty) |
| `isFilterBitmap` | 1 | none | Return safe default (null/false/0/empty) |
| `isStrikeThruText` | 1 | none | Return safe default (null/false/0/empty) |
| `isSubpixelText` | 1 | none | Return safe default (null/false/0/empty) |
| `measureText` | 1 | none | throw UnsupportedOperationException |
| `measureText` | 1 | none | throw UnsupportedOperationException |
| `measureText` | 1 | none | throw UnsupportedOperationException |
| `measureText` | 1 | none | throw UnsupportedOperationException |
| `setARGB` | 1 | none | Log warning + no-op |
| `setBlendMode` | 1 | none | Log warning + no-op |
| `setDither` | 1 | none | Log warning + no-op |
| `setElegantTextHeight` | 1 | none | Log warning + no-op |
| `setEndHyphenEdit` | 1 | none | Log warning + no-op |
| `setFilterBitmap` | 1 | none | Log warning + no-op |
| `setFlags` | 1 | none | Log warning + no-op |
| `setFontFeatureSettings` | 1 | none | Log warning + no-op |
| `setFontVariationSettings` | 1 | none | Log warning + no-op |
| `setHinting` | 1 | none | Log warning + no-op |
| `setPathEffect` | 1 | none | Log warning + no-op |
| `setShader` | 1 | none | Log warning + no-op |
| `setShadowLayer` | 1 | none | Log warning + no-op |
| `setShadowLayer` | 1 | none | Log warning + no-op |
| `setStartHyphenEdit` | 1 | none | Return dummy instance / no-op |
| `setStrikeThruText` | 1 | none | Log warning + no-op |
| `setStrokeMiter` | 1 | none | Log warning + no-op |
| `setStyle` | 1 | none | Log warning + no-op |
| `setSubpixelText` | 1 | none | Log warning + no-op |
| `setUnderlineText` | 1 | none | Log warning + no-op |
| `setXfermode` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Paint`:


## Quality Gates

Before marking `android.graphics.Paint` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 115 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
