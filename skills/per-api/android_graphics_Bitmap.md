# SKILL: android.graphics.Bitmap

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Bitmap`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Bitmap` |
| **Package** | `android.graphics` |
| **Total Methods** | 56 |
| **Avg Score** | 3.5 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 18 (32%) |
| **Partial/Composite** | 16 (28%) |
| **No Mapping** | 22 (39%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 13 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 18 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getHeight` | `int getHeight()` | 9 | direct | rewrite | `OH_Drawing_BitmapGetHeight` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `getWidth` | `int getWidth()` | 9 | direct | rewrite | `OH_Drawing_BitmapGetWidth` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `recycle` | `void recycle()` | 9 | direct | impossible | `OH_Drawing_BitmapDestroy` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@NonNull android.graphics.Bitmap)` | 7 | near | rewrite | `OH_Drawing_BitmapCreate+Build` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@NonNull android.graphics.Bitmap, int, int, int, int)` | 7 | near | rewrite | `OH_Drawing_BitmapCreate+Build` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@NonNull android.graphics.Bitmap, int, int, int, int, @Nullable android.graphics.Matrix, boolean)` | 7 | near | rewrite | `OH_Drawing_BitmapCreate+Build` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(int, int, @NonNull android.graphics.Bitmap.Config)` | 7 | near | rewrite | `OH_Drawing_BitmapCreate+Build` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@Nullable android.util.DisplayMetrics, int, int, @NonNull android.graphics.Bitmap.Config)` | 7 | near | rewrite | `OH_Drawing_BitmapCreate+Build` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(int, int, @NonNull android.graphics.Bitmap.Config, boolean)` | 7 | near | rewrite | `OH_Drawing_BitmapCreate+Build` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(int, int, @NonNull android.graphics.Bitmap.Config, boolean, @NonNull android.graphics.ColorSpace)` | 7 | near | rewrite | `OH_Drawing_BitmapCreate+Build` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@Nullable android.util.DisplayMetrics, int, int, @NonNull android.graphics.Bitmap.Config, boolean)` | 7 | near | rewrite | `OH_Drawing_BitmapCreate+Build` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@Nullable android.util.DisplayMetrics, int, int, @NonNull android.graphics.Bitmap.Config, boolean, @NonNull android.graphics.ColorSpace)` | 7 | near | rewrite | `OH_Drawing_BitmapCreate+Build` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@ColorInt @NonNull int[], int, int, int, int, @NonNull android.graphics.Bitmap.Config)` | 7 | near | rewrite | `OH_Drawing_BitmapCreate+Build` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@NonNull android.util.DisplayMetrics, @ColorInt @NonNull int[], int, int, int, int, @NonNull android.graphics.Bitmap.Config)` | 7 | near | rewrite | `OH_Drawing_BitmapCreate+Build` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@ColorInt @NonNull int[], int, int, android.graphics.Bitmap.Config)` | 7 | near | rewrite | `OH_Drawing_BitmapCreate+Build` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@Nullable android.util.DisplayMetrics, @ColorInt @NonNull int[], int, int, @NonNull android.graphics.Bitmap.Config)` | 7 | near | rewrite | `OH_Drawing_BitmapCreate+Build` | `@ohos.graphics.drawing (NDK).OH_Drawing_Bitmap` |
| `getByteCount` | `int getByteCount()` | 7 | near | impossible | `getPixelBytesNumber` | `@ohos.multimedia.image.PixelMap` |
| `isMutable` | `boolean isMutable()` | 7 | near | impossible | `isEditable` | `@ohos.multimedia.image.PixelMap` |

## Gap Descriptions (per method)

- **`getHeight`**: Direct
- **`getWidth`**: Direct
- **`recycle`**: Direct
- **`createBitmap`**: Separate create and build
- **`createBitmap`**: Separate create and build
- **`createBitmap`**: Separate create and build
- **`createBitmap`**: Separate create and build
- **`createBitmap`**: Separate create and build
- **`createBitmap`**: Separate create and build
- **`createBitmap`**: Separate create and build
- **`createBitmap`**: Separate create and build
- **`createBitmap`**: Separate create and build
- **`createBitmap`**: Separate create and build
- **`createBitmap`**: Separate create and build
- **`createBitmap`**: Separate create and build
- **`createBitmap`**: Separate create and build
- **`getByteCount`**: Naming diff
- **`isMutable`**: Naming diff

## Stub APIs (score < 5): 38 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getPixels` | 4 | composite | Return safe default (null/false/0/empty) |
| `eraseColor` | 3 | composite | throw UnsupportedOperationException |
| `eraseColor` | 3 | composite | throw UnsupportedOperationException |
| `copyPixelsFromBuffer` | 3 | composite | throw UnsupportedOperationException |
| `setWidth` | 3 | composite | Log warning + no-op |
| `copy` | 3 | composite | throw UnsupportedOperationException |
| `setColorSpace` | 3 | composite | Log warning + no-op |
| `getScaledWidth` | 3 | composite | Return safe default (null/false/0/empty) |
| `getScaledWidth` | 3 | composite | Return safe default (null/false/0/empty) |
| `getScaledWidth` | 3 | composite | Return safe default (null/false/0/empty) |
| `hasAlpha` | 2 | composite | Return safe default (null/false/0/empty) |
| `getConfig` | 2 | composite | Return safe default (null/false/0/empty) |
| `getScaledHeight` | 2 | composite | Return safe default (null/false/0/empty) |
| `getScaledHeight` | 2 | composite | Return safe default (null/false/0/empty) |
| `getScaledHeight` | 2 | composite | Return safe default (null/false/0/empty) |
| `copyPixelsToBuffer` | 2 | composite | throw UnsupportedOperationException |
| `createScaledBitmap` | 1 | none | Return dummy instance / no-op |
| `describeContents` | 1 | none | Store callback, never fire |
| `getAllocationByteCount` | 1 | none | Return safe default (null/false/0/empty) |
| `getDensity` | 1 | none | Return safe default (null/false/0/empty) |
| `getGenerationId` | 1 | none | Return safe default (null/false/0/empty) |
| `getNinePatchChunk` | 1 | none | Return safe default (null/false/0/empty) |
| `getRowBytes` | 1 | none | Return safe default (null/false/0/empty) |
| `hasMipMap` | 1 | none | Return safe default (null/false/0/empty) |
| `isPremultiplied` | 1 | none | Return safe default (null/false/0/empty) |
| `isRecycled` | 1 | none | Return safe default (null/false/0/empty) |
| `prepareToDraw` | 1 | none | throw UnsupportedOperationException |
| `reconfigure` | 1 | none | Store callback, never fire |
| `sameAs` | 1 | none | throw UnsupportedOperationException |
| `setConfig` | 1 | none | Log warning + no-op |
| `setDensity` | 1 | none | Log warning + no-op |
| `setHasAlpha` | 1 | none | Return safe default (null/false/0/empty) |
| `setHasMipMap` | 1 | none | Return safe default (null/false/0/empty) |
| `setHeight` | 1 | none | Log warning + no-op |
| `setPixel` | 1 | none | Log warning + no-op |
| `setPixels` | 1 | none | Log warning + no-op |
| `setPremultiplied` | 1 | none | Log warning + no-op |
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

Check if these related classes are already shimmed before generating `android.graphics.Bitmap`:


## Quality Gates

Before marking `android.graphics.Bitmap` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 56 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 18 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
