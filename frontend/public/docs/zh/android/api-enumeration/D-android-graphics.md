# Android 11 (API 30) Public API Enumeration: Android Graphics

Generated from `frameworks/base/api/current.txt`

## 概要

| Package | Types | Methods | Fields | Ctors |
|---------|------:|--------:|-------:|------:|
| `android.graphics` | 98 | 708 | 158 | 102 |
| `android.graphics.drawable` | 32 | 356 | 20 | 40 |
| `android.graphics.drawable.shapes` | 6 | 18 | 0 | 6 |
| `android.graphics.fonts` | 7 | 3 | 13 | 10 |
| `android.graphics.pdf` | 6 | 22 | 2 | 3 |
| `android.graphics.text` | 6 | 7 | 8 | 4 |
| `android.opengl` | 36 | 1004 | 1829 | 19 |
| `android.renderscript` | 73 | 626 | 81 | 61 |
| **TOTAL** | **264** | **2744** | **2111** | **245** |

---

## Package: `android.graphics`

### `class final Bitmap`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DENSITY_NONE = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.Bitmap copy(android.graphics.Bitmap.Config, boolean)` |  |
| `void copyPixelsFromBuffer(java.nio.Buffer)` |  |
| `void copyPixelsToBuffer(java.nio.Buffer)` |  |
| `static android.graphics.Bitmap createBitmap(@NonNull android.graphics.Bitmap)` |  |
| `static android.graphics.Bitmap createBitmap(@NonNull android.graphics.Bitmap, int, int, int, int)` |  |
| `static android.graphics.Bitmap createBitmap(@NonNull android.graphics.Bitmap, int, int, int, int, @Nullable android.graphics.Matrix, boolean)` |  |
| `static android.graphics.Bitmap createBitmap(int, int, @NonNull android.graphics.Bitmap.Config)` |  |
| `static android.graphics.Bitmap createBitmap(@Nullable android.util.DisplayMetrics, int, int, @NonNull android.graphics.Bitmap.Config)` |  |
| `static android.graphics.Bitmap createBitmap(int, int, @NonNull android.graphics.Bitmap.Config, boolean)` |  |
| `static android.graphics.Bitmap createBitmap(int, int, @NonNull android.graphics.Bitmap.Config, boolean, @NonNull android.graphics.ColorSpace)` |  |
| `static android.graphics.Bitmap createBitmap(@Nullable android.util.DisplayMetrics, int, int, @NonNull android.graphics.Bitmap.Config, boolean)` |  |
| `static android.graphics.Bitmap createBitmap(@Nullable android.util.DisplayMetrics, int, int, @NonNull android.graphics.Bitmap.Config, boolean, @NonNull android.graphics.ColorSpace)` |  |
| `static android.graphics.Bitmap createBitmap(@ColorInt @NonNull int[], int, int, int, int, @NonNull android.graphics.Bitmap.Config)` |  |
| `static android.graphics.Bitmap createBitmap(@NonNull android.util.DisplayMetrics, @ColorInt @NonNull int[], int, int, int, int, @NonNull android.graphics.Bitmap.Config)` |  |
| `static android.graphics.Bitmap createBitmap(@ColorInt @NonNull int[], int, int, android.graphics.Bitmap.Config)` |  |
| `static android.graphics.Bitmap createBitmap(@Nullable android.util.DisplayMetrics, @ColorInt @NonNull int[], int, int, @NonNull android.graphics.Bitmap.Config)` |  |
| `static android.graphics.Bitmap createScaledBitmap(@NonNull android.graphics.Bitmap, int, int, boolean)` |  |
| `int describeContents()` |  |
| `void eraseColor(@ColorInt int)` |  |
| `void eraseColor(@ColorLong long)` |  |
| `int getAllocationByteCount()` |  |
| `int getByteCount()` |  |
| `android.graphics.Bitmap.Config getConfig()` |  |
| `int getDensity()` |  |
| `int getGenerationId()` |  |
| `int getHeight()` |  |
| `byte[] getNinePatchChunk()` |  |
| `void getPixels(@ColorInt int[], int, int, int, int, int, int)` |  |
| `int getRowBytes()` |  |
| `int getScaledHeight(android.graphics.Canvas)` |  |
| `int getScaledHeight(android.util.DisplayMetrics)` |  |
| `int getScaledHeight(int)` |  |
| `int getScaledWidth(android.graphics.Canvas)` |  |
| `int getScaledWidth(android.util.DisplayMetrics)` |  |
| `int getScaledWidth(int)` |  |
| `int getWidth()` |  |
| `boolean hasAlpha()` |  |
| `boolean hasMipMap()` |  |
| `boolean isMutable()` |  |
| `boolean isPremultiplied()` |  |
| `boolean isRecycled()` |  |
| `void prepareToDraw()` |  |
| `void reconfigure(int, int, android.graphics.Bitmap.Config)` |  |
| `void recycle()` |  |
| `boolean sameAs(android.graphics.Bitmap)` |  |
| `void setColorSpace(@NonNull android.graphics.ColorSpace)` |  |
| `void setConfig(android.graphics.Bitmap.Config)` |  |
| `void setDensity(int)` |  |
| `void setHasAlpha(boolean)` |  |
| `void setHasMipMap(boolean)` |  |
| `void setHeight(int)` |  |
| `void setPixel(int, int, @ColorInt int)` |  |
| `void setPixels(@ColorInt int[], int, int, int, int, int, int)` |  |
| `void setPremultiplied(boolean)` |  |
| `void setWidth(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `enum Bitmap.CompressFormat`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.Bitmap.CompressFormat JPEG` |  |
| `static final android.graphics.Bitmap.CompressFormat PNG` |  |
| `static final android.graphics.Bitmap.CompressFormat WEBP_LOSSLESS` |  |
| `static final android.graphics.Bitmap.CompressFormat WEBP_LOSSY` |  |

---

### `enum Bitmap.Config`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.Bitmap.Config ALPHA_8` |  |
| `static final android.graphics.Bitmap.Config ARGB_8888` |  |
| `static final android.graphics.Bitmap.Config HARDWARE` |  |
| `static final android.graphics.Bitmap.Config RGBA_F16` |  |
| `static final android.graphics.Bitmap.Config RGB_565` |  |

---

### `class BitmapFactory`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BitmapFactory()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.graphics.Bitmap decodeByteArray(byte[], int, int, android.graphics.BitmapFactory.Options)` |  |
| `static android.graphics.Bitmap decodeByteArray(byte[], int, int)` |  |
| `static android.graphics.Bitmap decodeFile(String, android.graphics.BitmapFactory.Options)` |  |
| `static android.graphics.Bitmap decodeFile(String)` |  |
| `static android.graphics.Bitmap decodeFileDescriptor(java.io.FileDescriptor, android.graphics.Rect, android.graphics.BitmapFactory.Options)` |  |
| `static android.graphics.Bitmap decodeFileDescriptor(java.io.FileDescriptor)` |  |
| `static android.graphics.Bitmap decodeResource(android.content.res.Resources, int, android.graphics.BitmapFactory.Options)` |  |
| `static android.graphics.Bitmap decodeResource(android.content.res.Resources, int)` |  |
| `static android.graphics.Bitmap decodeStream(java.io.InputStream)` |  |

---

### `class static BitmapFactory.Options`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BitmapFactory.Options()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.Bitmap inBitmap` |  |
| `int inDensity` |  |
| `boolean inJustDecodeBounds` |  |
| `boolean inMutable` |  |
| `android.graphics.ColorSpace inPreferredColorSpace` |  |
| `android.graphics.Bitmap.Config inPreferredConfig` |  |
| `boolean inPremultiplied` |  |
| `int inSampleSize` |  |
| `boolean inScaled` |  |
| `int inScreenDensity` |  |
| `int inTargetDensity` |  |
| `byte[] inTempStorage` |  |
| `android.graphics.ColorSpace outColorSpace` |  |
| `android.graphics.Bitmap.Config outConfig` |  |
| `int outHeight` |  |
| `String outMimeType` |  |
| `int outWidth` |  |

---

### `class final BitmapRegionDecoder`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.Bitmap decodeRegion(android.graphics.Rect, android.graphics.BitmapFactory.Options)` |  |
| `int getHeight()` |  |
| `int getWidth()` |  |
| `boolean isRecycled()` |  |
| `static android.graphics.BitmapRegionDecoder newInstance(byte[], int, int, boolean) throws java.io.IOException` |  |
| `static android.graphics.BitmapRegionDecoder newInstance(java.io.FileDescriptor, boolean) throws java.io.IOException` |  |
| `static android.graphics.BitmapRegionDecoder newInstance(java.io.InputStream, boolean) throws java.io.IOException` |  |
| `static android.graphics.BitmapRegionDecoder newInstance(String, boolean) throws java.io.IOException` |  |
| `void recycle()` |  |

---

### `class BitmapShader`

- **继承：** `android.graphics.Shader`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BitmapShader(@NonNull android.graphics.Bitmap, @NonNull android.graphics.Shader.TileMode, @NonNull android.graphics.Shader.TileMode)` |  |

---

### `enum BlendMode`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.BlendMode CLEAR` |  |
| `static final android.graphics.BlendMode COLOR` |  |
| `static final android.graphics.BlendMode COLOR_BURN` |  |
| `static final android.graphics.BlendMode COLOR_DODGE` |  |
| `static final android.graphics.BlendMode DARKEN` |  |
| `static final android.graphics.BlendMode DIFFERENCE` |  |
| `static final android.graphics.BlendMode DST` |  |
| `static final android.graphics.BlendMode DST_ATOP` |  |
| `static final android.graphics.BlendMode DST_IN` |  |
| `static final android.graphics.BlendMode DST_OUT` |  |
| `static final android.graphics.BlendMode DST_OVER` |  |
| `static final android.graphics.BlendMode EXCLUSION` |  |
| `static final android.graphics.BlendMode HARD_LIGHT` |  |
| `static final android.graphics.BlendMode HUE` |  |
| `static final android.graphics.BlendMode LIGHTEN` |  |
| `static final android.graphics.BlendMode LUMINOSITY` |  |
| `static final android.graphics.BlendMode MODULATE` |  |
| `static final android.graphics.BlendMode MULTIPLY` |  |
| `static final android.graphics.BlendMode OVERLAY` |  |
| `static final android.graphics.BlendMode PLUS` |  |
| `static final android.graphics.BlendMode SATURATION` |  |
| `static final android.graphics.BlendMode SCREEN` |  |
| `static final android.graphics.BlendMode SOFT_LIGHT` |  |
| `static final android.graphics.BlendMode SRC` |  |
| `static final android.graphics.BlendMode SRC_ATOP` |  |
| `static final android.graphics.BlendMode SRC_IN` |  |
| `static final android.graphics.BlendMode SRC_OUT` |  |
| `static final android.graphics.BlendMode SRC_OVER` |  |
| `static final android.graphics.BlendMode XOR` |  |

---

### `class final BlendModeColorFilter`

- **继承：** `android.graphics.ColorFilter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BlendModeColorFilter(@ColorInt int, @NonNull android.graphics.BlendMode)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.BlendMode getMode()` |  |

---

### `class BlurMaskFilter`

- **继承：** `android.graphics.MaskFilter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BlurMaskFilter(float, android.graphics.BlurMaskFilter.Blur)` |  |

---

### `enum BlurMaskFilter.Blur`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.BlurMaskFilter.Blur INNER` |  |
| `static final android.graphics.BlurMaskFilter.Blur NORMAL` |  |
| `static final android.graphics.BlurMaskFilter.Blur OUTER` |  |
| `static final android.graphics.BlurMaskFilter.Blur SOLID` |  |

---

### `class Camera`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Camera()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void applyToCanvas(android.graphics.Canvas)` |  |
| `float dotWithNormal(float, float, float)` |  |
| `float getLocationX()` |  |
| `float getLocationY()` |  |
| `float getLocationZ()` |  |
| `void getMatrix(android.graphics.Matrix)` |  |
| `void restore()` |  |
| `void rotate(float, float, float)` |  |
| `void rotateX(float)` |  |
| `void rotateY(float)` |  |
| `void rotateZ(float)` |  |
| `void save()` |  |
| `void setLocation(float, float, float)` |  |
| `void translate(float, float, float)` |  |

---

### `class Canvas`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Canvas()` |  |
| `Canvas(@NonNull android.graphics.Bitmap)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ALL_SAVE_FLAG = 31` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean clipOutPath(@NonNull android.graphics.Path)` |  |
| `boolean clipOutRect(@NonNull android.graphics.RectF)` |  |
| `boolean clipOutRect(@NonNull android.graphics.Rect)` |  |
| `boolean clipOutRect(float, float, float, float)` |  |
| `boolean clipOutRect(int, int, int, int)` |  |
| `boolean clipPath(@NonNull android.graphics.Path)` |  |
| `boolean clipRect(@NonNull android.graphics.RectF)` |  |
| `boolean clipRect(@NonNull android.graphics.Rect)` |  |
| `boolean clipRect(float, float, float, float)` |  |
| `boolean clipRect(int, int, int, int)` |  |
| `void concat(@Nullable android.graphics.Matrix)` |  |
| `void disableZ()` |  |
| `void drawARGB(int, int, int, int)` |  |
| `void drawArc(@NonNull android.graphics.RectF, float, float, boolean, @NonNull android.graphics.Paint)` |  |
| `void drawArc(float, float, float, float, float, float, boolean, @NonNull android.graphics.Paint)` |  |
| `void drawBitmap(@NonNull android.graphics.Bitmap, float, float, @Nullable android.graphics.Paint)` |  |
| `void drawBitmap(@NonNull android.graphics.Bitmap, @Nullable android.graphics.Rect, @NonNull android.graphics.RectF, @Nullable android.graphics.Paint)` |  |
| `void drawBitmap(@NonNull android.graphics.Bitmap, @Nullable android.graphics.Rect, @NonNull android.graphics.Rect, @Nullable android.graphics.Paint)` |  |
| `void drawBitmap(@NonNull android.graphics.Bitmap, @NonNull android.graphics.Matrix, @Nullable android.graphics.Paint)` |  |
| `void drawBitmapMesh(@NonNull android.graphics.Bitmap, int, int, @NonNull float[], int, @Nullable int[], int, @Nullable android.graphics.Paint)` |  |
| `void drawCircle(float, float, float, @NonNull android.graphics.Paint)` |  |
| `void drawColor(@ColorInt int)` |  |
| `void drawColor(@ColorLong long)` |  |
| `void drawColor(@ColorInt int, @NonNull android.graphics.PorterDuff.Mode)` |  |
| `void drawColor(@ColorInt int, @NonNull android.graphics.BlendMode)` |  |
| `void drawColor(@ColorLong long, @NonNull android.graphics.BlendMode)` |  |
| `void drawDoubleRoundRect(@NonNull android.graphics.RectF, float, float, @NonNull android.graphics.RectF, float, float, @NonNull android.graphics.Paint)` |  |
| `void drawDoubleRoundRect(@NonNull android.graphics.RectF, @NonNull float[], @NonNull android.graphics.RectF, @NonNull float[], @NonNull android.graphics.Paint)` |  |
| `void drawLine(float, float, float, float, @NonNull android.graphics.Paint)` |  |
| `void drawLines(@NonNull @Size(multiple=4) float[], int, int, @NonNull android.graphics.Paint)` |  |
| `void drawLines(@NonNull @Size(multiple=4) float[], @NonNull android.graphics.Paint)` |  |
| `void drawOval(@NonNull android.graphics.RectF, @NonNull android.graphics.Paint)` |  |
| `void drawOval(float, float, float, float, @NonNull android.graphics.Paint)` |  |
| `void drawPaint(@NonNull android.graphics.Paint)` |  |
| `void drawPath(@NonNull android.graphics.Path, @NonNull android.graphics.Paint)` |  |
| `void drawPicture(@NonNull android.graphics.Picture)` |  |
| `void drawPicture(@NonNull android.graphics.Picture, @NonNull android.graphics.RectF)` |  |
| `void drawPicture(@NonNull android.graphics.Picture, @NonNull android.graphics.Rect)` |  |
| `void drawPoint(float, float, @NonNull android.graphics.Paint)` |  |
| `void drawPoints(@Size(multiple=2) float[], int, int, @NonNull android.graphics.Paint)` |  |
| `void drawPoints(@NonNull @Size(multiple=2) float[], @NonNull android.graphics.Paint)` |  |
| `void drawRGB(int, int, int)` |  |
| `void drawRect(@NonNull android.graphics.RectF, @NonNull android.graphics.Paint)` |  |
| `void drawRect(@NonNull android.graphics.Rect, @NonNull android.graphics.Paint)` |  |
| `void drawRect(float, float, float, float, @NonNull android.graphics.Paint)` |  |
| `void drawRenderNode(@NonNull android.graphics.RenderNode)` |  |
| `void drawRoundRect(@NonNull android.graphics.RectF, float, float, @NonNull android.graphics.Paint)` |  |
| `void drawRoundRect(float, float, float, float, float, float, @NonNull android.graphics.Paint)` |  |
| `void drawText(@NonNull char[], int, int, float, float, @NonNull android.graphics.Paint)` |  |
| `void drawText(@NonNull String, float, float, @NonNull android.graphics.Paint)` |  |
| `void drawText(@NonNull String, int, int, float, float, @NonNull android.graphics.Paint)` |  |
| `void drawText(@NonNull CharSequence, int, int, float, float, @NonNull android.graphics.Paint)` |  |
| `void drawTextOnPath(@NonNull char[], int, int, @NonNull android.graphics.Path, float, float, @NonNull android.graphics.Paint)` |  |
| `void drawTextOnPath(@NonNull String, @NonNull android.graphics.Path, float, float, @NonNull android.graphics.Paint)` |  |
| `void drawTextRun(@NonNull char[], int, int, int, int, float, float, boolean, @NonNull android.graphics.Paint)` |  |
| `void drawTextRun(@NonNull CharSequence, int, int, int, int, float, float, boolean, @NonNull android.graphics.Paint)` |  |
| `void drawTextRun(@NonNull android.graphics.text.MeasuredText, int, int, int, int, float, float, boolean, @NonNull android.graphics.Paint)` |  |
| `void drawVertices(@NonNull android.graphics.Canvas.VertexMode, int, @NonNull float[], int, @Nullable float[], int, @Nullable int[], int, @Nullable short[], int, int, @NonNull android.graphics.Paint)` |  |
| `void enableZ()` |  |
| `boolean getClipBounds(@Nullable android.graphics.Rect)` |  |
| `int getDensity()` |  |
| `int getHeight()` |  |
| `int getMaximumBitmapHeight()` |  |
| `int getMaximumBitmapWidth()` |  |
| `int getSaveCount()` |  |
| `int getWidth()` |  |
| `boolean isHardwareAccelerated()` |  |
| `boolean isOpaque()` |  |
| `boolean quickReject(@NonNull android.graphics.RectF)` |  |
| `boolean quickReject(@NonNull android.graphics.Path)` |  |
| `boolean quickReject(float, float, float, float)` |  |
| `void restore()` |  |
| `void restoreToCount(int)` |  |
| `void rotate(float)` |  |
| `final void rotate(float, float, float)` |  |
| `int save()` |  |
| `int saveLayer(@Nullable android.graphics.RectF, @Nullable android.graphics.Paint)` |  |
| `int saveLayer(float, float, float, float, @Nullable android.graphics.Paint)` |  |
| `int saveLayerAlpha(@Nullable android.graphics.RectF, int)` |  |
| `int saveLayerAlpha(float, float, float, float, int)` |  |
| `void scale(float, float)` |  |
| `final void scale(float, float, float, float)` |  |
| `void setBitmap(@Nullable android.graphics.Bitmap)` |  |
| `void setDensity(int)` |  |
| `void setDrawFilter(@Nullable android.graphics.DrawFilter)` |  |
| `void setMatrix(@Nullable android.graphics.Matrix)` |  |
| `void skew(float, float)` |  |
| `void translate(float, float)` |  |

---

### `enum Canvas.EdgeType` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `enum Canvas.VertexMode`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.Canvas.VertexMode TRIANGLES` |  |
| `static final android.graphics.Canvas.VertexMode TRIANGLE_FAN` |  |
| `static final android.graphics.Canvas.VertexMode TRIANGLE_STRIP` |  |

---

### `class Color`

- **注解：** `@AnyThread`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Color()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void RGBToHSV(@IntRange(from=0, to=255) int, @IntRange(from=0, to=255) int, @IntRange(from=0, to=255) int, @Size(3) float[])` |  |
| `float alpha()` |  |
| `static float alpha(@ColorLong long)` |  |
| `float blue()` |  |
| `static float blue(@ColorLong long)` |  |
| `static void colorToHSV(@ColorInt int, @Size(3) float[])` |  |
| `float getComponent(@IntRange(from=0, to=4) int)` |  |
| `android.graphics.ColorSpace.Model getModel()` |  |
| `float green()` |  |
| `static float green(@ColorLong long)` |  |
| `static boolean isInColorSpace(@ColorLong long, @NonNull android.graphics.ColorSpace)` |  |
| `boolean isSrgb()` |  |
| `static boolean isSrgb(@ColorLong long)` |  |
| `boolean isWideGamut()` |  |
| `static boolean isWideGamut(@ColorLong long)` |  |
| `float luminance()` |  |
| `static float luminance(@ColorLong long)` |  |
| `static float luminance(@ColorInt int)` |  |
| `float red()` |  |
| `static float red(@ColorLong long)` |  |

---

### `class ColorFilter`


---

### `class ColorMatrix`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ColorMatrix()` |  |
| `ColorMatrix(float[])` |  |
| `ColorMatrix(android.graphics.ColorMatrix)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final float[] getArray()` |  |
| `void postConcat(android.graphics.ColorMatrix)` |  |
| `void preConcat(android.graphics.ColorMatrix)` |  |
| `void reset()` |  |
| `void set(android.graphics.ColorMatrix)` |  |
| `void set(float[])` |  |
| `void setConcat(android.graphics.ColorMatrix, android.graphics.ColorMatrix)` |  |
| `void setRGB2YUV()` |  |
| `void setRotate(int, float)` |  |
| `void setSaturation(float)` |  |
| `void setScale(float, float, float, float)` |  |
| `void setYUV2RGB()` |  |

---

### `class ColorMatrixColorFilter`

- **继承：** `android.graphics.ColorFilter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ColorMatrixColorFilter(@NonNull android.graphics.ColorMatrix)` |  |
| `ColorMatrixColorFilter(@NonNull float[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void getColorMatrix(android.graphics.ColorMatrix)` |  |

---

### `class abstract ColorSpace`

- **注解：** `@AnyThread`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final float[] ILLUMINANT_A` |  |
| `static final float[] ILLUMINANT_B` |  |
| `static final float[] ILLUMINANT_C` |  |
| `static final float[] ILLUMINANT_D50` |  |
| `static final float[] ILLUMINANT_D55` |  |
| `static final float[] ILLUMINANT_D60` |  |
| `static final float[] ILLUMINANT_D65` |  |
| `static final float[] ILLUMINANT_D75` |  |
| `static final float[] ILLUMINANT_E` |  |
| `static final int MAX_ID = 63` |  |
| `static final int MIN_ID = -1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract float getMaxValue(@IntRange(from=0, to=3) int)` |  |
| `abstract float getMinValue(@IntRange(from=0, to=3) int)` |  |
| `boolean isSrgb()` |  |
| `abstract boolean isWideGamut()` |  |

---

### `enum ColorSpace.Adaptation`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.ColorSpace.Adaptation BRADFORD` |  |
| `static final android.graphics.ColorSpace.Adaptation CIECAT02` |  |
| `static final android.graphics.ColorSpace.Adaptation VON_KRIES` |  |

---

### `class static ColorSpace.Connector`

- **注解：** `@AnyThread`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.ColorSpace.RenderIntent getRenderIntent()` |  |

---

### `enum ColorSpace.Model`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.ColorSpace.Model CMYK` |  |
| `static final android.graphics.ColorSpace.Model LAB` |  |
| `static final android.graphics.ColorSpace.Model RGB` |  |
| `static final android.graphics.ColorSpace.Model XYZ` |  |

---

### `enum ColorSpace.Named`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.ColorSpace.Named ACES` |  |
| `static final android.graphics.ColorSpace.Named ACESCG` |  |
| `static final android.graphics.ColorSpace.Named ADOBE_RGB` |  |
| `static final android.graphics.ColorSpace.Named BT2020` |  |
| `static final android.graphics.ColorSpace.Named BT709` |  |
| `static final android.graphics.ColorSpace.Named CIE_LAB` |  |
| `static final android.graphics.ColorSpace.Named CIE_XYZ` |  |
| `static final android.graphics.ColorSpace.Named DCI_P3` |  |
| `static final android.graphics.ColorSpace.Named DISPLAY_P3` |  |
| `static final android.graphics.ColorSpace.Named EXTENDED_SRGB` |  |
| `static final android.graphics.ColorSpace.Named LINEAR_EXTENDED_SRGB` |  |
| `static final android.graphics.ColorSpace.Named LINEAR_SRGB` |  |
| `static final android.graphics.ColorSpace.Named NTSC_1953` |  |
| `static final android.graphics.ColorSpace.Named PRO_PHOTO_RGB` |  |
| `static final android.graphics.ColorSpace.Named SMPTE_C` |  |
| `static final android.graphics.ColorSpace.Named SRGB` |  |

---

### `enum ColorSpace.RenderIntent`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.ColorSpace.RenderIntent ABSOLUTE` |  |
| `static final android.graphics.ColorSpace.RenderIntent PERCEPTUAL` |  |
| `static final android.graphics.ColorSpace.RenderIntent RELATIVE` |  |
| `static final android.graphics.ColorSpace.RenderIntent SATURATION` |  |

---

### `class static ColorSpace.Rgb`

- **继承：** `android.graphics.ColorSpace`
- **注解：** `@AnyThread`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ColorSpace.Rgb(@NonNull @Size(min=1) String, @NonNull @Size(9) float[], @NonNull java.util.function.DoubleUnaryOperator, @NonNull java.util.function.DoubleUnaryOperator)` |  |
| `ColorSpace.Rgb(@NonNull @Size(min=1) String, @NonNull @Size(min=6, max=9) float[], @NonNull @Size(min=2, max=3) float[], @NonNull java.util.function.DoubleUnaryOperator, @NonNull java.util.function.DoubleUnaryOperator, float, float)` |  |
| `ColorSpace.Rgb(@NonNull @Size(min=1) String, @NonNull @Size(9) float[], @NonNull android.graphics.ColorSpace.Rgb.TransferParameters)` |  |
| `ColorSpace.Rgb(@NonNull @Size(min=1) String, @NonNull @Size(min=6, max=9) float[], @NonNull @Size(min=2, max=3) float[], @NonNull android.graphics.ColorSpace.Rgb.TransferParameters)` |  |
| `ColorSpace.Rgb(@NonNull @Size(min=1) String, @NonNull @Size(9) float[], double)` |  |
| `ColorSpace.Rgb(@NonNull @Size(min=1) String, @NonNull @Size(min=6, max=9) float[], @NonNull @Size(min=2, max=3) float[], double)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `float getMaxValue(int)` |  |
| `float getMinValue(int)` |  |
| `boolean isWideGamut()` |  |

---

### `class static ColorSpace.Rgb.TransferParameters`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ColorSpace.Rgb.TransferParameters(double, double, double, double, double)` |  |
| `ColorSpace.Rgb.TransferParameters(double, double, double, double, double, double, double)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `final double a` |  |
| `final double b` |  |
| `final double c` |  |
| `final double d` |  |
| `final double e` |  |
| `final double f` |  |
| `final double g` |  |

---

### `class ComposePathEffect`

- **继承：** `android.graphics.PathEffect`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ComposePathEffect(android.graphics.PathEffect, android.graphics.PathEffect)` |  |

---

### `class ComposeShader`

- **继承：** `android.graphics.Shader`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ComposeShader(@NonNull android.graphics.Shader, @NonNull android.graphics.Shader, @NonNull android.graphics.PorterDuff.Mode)` |  |
| `ComposeShader(@NonNull android.graphics.Shader, @NonNull android.graphics.Shader, @NonNull android.graphics.BlendMode)` |  |

---

### `class CornerPathEffect`

- **继承：** `android.graphics.PathEffect`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CornerPathEffect(float)` |  |

---

### `class DashPathEffect`

- **继承：** `android.graphics.PathEffect`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DashPathEffect(float[], float)` |  |

---

### `class DiscretePathEffect`

- **继承：** `android.graphics.PathEffect`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DiscretePathEffect(float, float)` |  |

---

### `class DrawFilter`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DrawFilter()` |  |

---

### `class EmbossMaskFilter`

- **继承：** `android.graphics.MaskFilter`

---

### `class HardwareRenderer`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `HardwareRenderer()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SYNC_CONTEXT_IS_STOPPED = 4` |  |
| `static final int SYNC_FRAME_DROPPED = 8` |  |
| `static final int SYNC_LOST_SURFACE_REWARD_IF_FOUND = 2` |  |
| `static final int SYNC_OK = 0` |  |
| `static final int SYNC_REDRAW_REQUESTED = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void clearContent()` |  |
| `void destroy()` |  |
| `boolean isOpaque()` |  |
| `void notifyFramePending()` |  |
| `void setContentRoot(@Nullable android.graphics.RenderNode)` |  |
| `void setLightSourceAlpha(@FloatRange(from=0.0f, to=1.0f) float, @FloatRange(from=0.0f, to=1.0f) float)` |  |
| `void setLightSourceGeometry(float, float, float, float)` |  |
| `void setName(@NonNull String)` |  |
| `void setOpaque(boolean)` |  |
| `void setSurface(@Nullable android.view.Surface)` |  |
| `void start()` |  |
| `void stop()` |  |

---

### `class final HardwareRenderer.FrameRenderRequest`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int syncAndDraw()` |  |

---

### `class final ImageDecoder`

- **实现：** `java.lang.AutoCloseable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ALLOCATOR_DEFAULT = 0` |  |
| `static final int ALLOCATOR_HARDWARE = 3` |  |
| `static final int ALLOCATOR_SHARED_MEMORY = 2` |  |
| `static final int ALLOCATOR_SOFTWARE = 1` |  |
| `static final int MEMORY_POLICY_DEFAULT = 1` |  |
| `static final int MEMORY_POLICY_LOW_RAM = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `int getAllocator()` |  |
| `int getMemorySizePolicy()` |  |
| `boolean isDecodeAsAlphaMaskEnabled()` |  |
| `static boolean isMimeTypeSupported(@NonNull String)` |  |
| `boolean isMutableRequired()` |  |
| `boolean isUnpremultipliedRequired()` |  |
| `void setAllocator(int)` |  |
| `void setCrop(@Nullable android.graphics.Rect)` |  |
| `void setDecodeAsAlphaMaskEnabled(boolean)` |  |
| `void setMemorySizePolicy(int)` |  |
| `void setMutableRequired(boolean)` |  |
| `void setOnPartialImageListener(@Nullable android.graphics.ImageDecoder.OnPartialImageListener)` |  |
| `void setPostProcessor(@Nullable android.graphics.PostProcessor)` |  |
| `void setTargetColorSpace(android.graphics.ColorSpace)` |  |
| `void setTargetSampleSize(@IntRange(from=1) int)` |  |
| `void setTargetSize(@IntRange(from=1) @Px int, @IntRange(from=1) @Px int)` |  |
| `void setUnpremultipliedRequired(boolean)` |  |

---

### `class static final ImageDecoder.DecodeException`

- **继承：** `java.io.IOException`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SOURCE_EXCEPTION = 1` |  |
| `static final int SOURCE_INCOMPLETE = 2` |  |
| `static final int SOURCE_MALFORMED_DATA = 3` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getError()` |  |

---

### `class static ImageDecoder.ImageInfo`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean isAnimated()` |  |

---

### `interface static ImageDecoder.OnHeaderDecodedListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onHeaderDecoded(@NonNull android.graphics.ImageDecoder, @NonNull android.graphics.ImageDecoder.ImageInfo, @NonNull android.graphics.ImageDecoder.Source)` |  |

---

### `interface static ImageDecoder.OnPartialImageListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean onPartialImage(@NonNull android.graphics.ImageDecoder.DecodeException)` |  |

---

### `class ImageFormat`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ImageFormat()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DEPTH16 = 1144402265` |  |
| `static final int DEPTH_JPEG = 1768253795` |  |
| `static final int DEPTH_POINT_CLOUD = 257` |  |
| `static final int FLEX_RGBA_8888 = 42` |  |
| `static final int FLEX_RGB_888 = 41` |  |
| `static final int HEIC = 1212500294` |  |
| `static final int JPEG = 256` |  |
| `static final int NV16 = 16` |  |
| `static final int NV21 = 17` |  |
| `static final int PRIVATE = 34` |  |
| `static final int RAW10 = 37` |  |
| `static final int RAW12 = 38` |  |
| `static final int RAW_PRIVATE = 36` |  |
| `static final int RAW_SENSOR = 32` |  |
| `static final int RGB_565 = 4` |  |
| `static final int UNKNOWN = 0` |  |
| `static final int Y8 = 538982489` |  |
| `static final int YUV_420_888 = 35` |  |
| `static final int YUV_422_888 = 39` |  |
| `static final int YUV_444_888 = 40` |  |
| `static final int YUY2 = 20` |  |
| `static final int YV12 = 842094169` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static int getBitsPerPixel(int)` |  |

---

### `class final Insets`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `final int bottom` |  |
| `final int left` |  |
| `final int right` |  |
| `final int top` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class Interpolator`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Interpolator(int)` |  |
| `Interpolator(int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final int getKeyFrameCount()` |  |
| `final int getValueCount()` |  |
| `void reset(int)` |  |
| `void reset(int, int)` |  |
| `void setKeyFrame(int, int, float[])` |  |
| `void setKeyFrame(int, int, float[], float[])` |  |
| `void setRepeatMirror(float, boolean)` |  |
| `android.graphics.Interpolator.Result timeToValues(float[])` |  |
| `android.graphics.Interpolator.Result timeToValues(int, float[])` |  |

---

### `enum Interpolator.Result`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.Interpolator.Result FREEZE_END` |  |
| `static final android.graphics.Interpolator.Result FREEZE_START` |  |
| `static final android.graphics.Interpolator.Result NORMAL` |  |

---

### `class LightingColorFilter`

- **继承：** `android.graphics.ColorFilter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LightingColorFilter(@ColorInt int, @ColorInt int)` |  |

---

### `class LinearGradient`

- **继承：** `android.graphics.Shader`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LinearGradient(float, float, float, float, @ColorInt @NonNull int[], @Nullable float[], @NonNull android.graphics.Shader.TileMode)` |  |
| `LinearGradient(float, float, float, float, @ColorLong @NonNull long[], @Nullable float[], @NonNull android.graphics.Shader.TileMode)` |  |
| `LinearGradient(float, float, float, float, @ColorInt int, @ColorInt int, @NonNull android.graphics.Shader.TileMode)` |  |
| `LinearGradient(float, float, float, float, @ColorLong long, @ColorLong long, @NonNull android.graphics.Shader.TileMode)` |  |

---

### `class MaskFilter`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MaskFilter()` |  |

---

### `class Matrix`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Matrix()` |  |
| `Matrix(android.graphics.Matrix)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int MPERSP_0 = 6` |  |
| `static final int MPERSP_1 = 7` |  |
| `static final int MPERSP_2 = 8` |  |
| `static final int MSCALE_X = 0` |  |
| `static final int MSCALE_Y = 4` |  |
| `static final int MSKEW_X = 1` |  |
| `static final int MSKEW_Y = 3` |  |
| `static final int MTRANS_X = 2` |  |
| `static final int MTRANS_Y = 5` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void getValues(float[])` |  |
| `boolean invert(android.graphics.Matrix)` |  |
| `boolean isAffine()` |  |
| `boolean isIdentity()` |  |
| `void mapPoints(float[], int, float[], int, int)` |  |
| `void mapPoints(float[], float[])` |  |
| `void mapPoints(float[])` |  |
| `float mapRadius(float)` |  |
| `boolean mapRect(android.graphics.RectF, android.graphics.RectF)` |  |
| `boolean mapRect(android.graphics.RectF)` |  |
| `void mapVectors(float[], int, float[], int, int)` |  |
| `void mapVectors(float[], float[])` |  |
| `void mapVectors(float[])` |  |
| `boolean postConcat(android.graphics.Matrix)` |  |
| `boolean postRotate(float, float, float)` |  |
| `boolean postRotate(float)` |  |
| `boolean postScale(float, float, float, float)` |  |
| `boolean postScale(float, float)` |  |
| `boolean postSkew(float, float, float, float)` |  |
| `boolean postSkew(float, float)` |  |
| `boolean postTranslate(float, float)` |  |
| `boolean preConcat(android.graphics.Matrix)` |  |
| `boolean preRotate(float, float, float)` |  |
| `boolean preRotate(float)` |  |
| `boolean preScale(float, float, float, float)` |  |
| `boolean preScale(float, float)` |  |
| `boolean preSkew(float, float, float, float)` |  |
| `boolean preSkew(float, float)` |  |
| `boolean preTranslate(float, float)` |  |
| `boolean rectStaysRect()` |  |
| `void reset()` |  |
| `void set(android.graphics.Matrix)` |  |
| `boolean setConcat(android.graphics.Matrix, android.graphics.Matrix)` |  |
| `boolean setPolyToPoly(float[], int, float[], int, int)` |  |
| `boolean setRectToRect(android.graphics.RectF, android.graphics.RectF, android.graphics.Matrix.ScaleToFit)` |  |
| `void setRotate(float, float, float)` |  |
| `void setRotate(float)` |  |
| `void setScale(float, float, float, float)` |  |
| `void setScale(float, float)` |  |
| `void setSinCos(float, float, float, float)` |  |
| `void setSinCos(float, float)` |  |
| `void setSkew(float, float, float, float)` |  |
| `void setSkew(float, float)` |  |
| `void setTranslate(float, float)` |  |
| `void setValues(float[])` |  |
| `String toShortString()` |  |

---

### `enum Matrix.ScaleToFit`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.Matrix.ScaleToFit CENTER` |  |
| `static final android.graphics.Matrix.ScaleToFit END` |  |
| `static final android.graphics.Matrix.ScaleToFit FILL` |  |
| `static final android.graphics.Matrix.ScaleToFit START` |  |

---

### `class Movie` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class NinePatch`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NinePatch(android.graphics.Bitmap, byte[])` |  |
| `NinePatch(android.graphics.Bitmap, byte[], String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void draw(android.graphics.Canvas, android.graphics.RectF)` |  |
| `void draw(android.graphics.Canvas, android.graphics.Rect)` |  |
| `void draw(android.graphics.Canvas, android.graphics.Rect, android.graphics.Paint)` |  |
| `android.graphics.Bitmap getBitmap()` |  |
| `int getDensity()` |  |
| `int getHeight()` |  |
| `String getName()` |  |
| `android.graphics.Paint getPaint()` |  |
| `final android.graphics.Region getTransparentRegion(android.graphics.Rect)` |  |
| `int getWidth()` |  |
| `final boolean hasAlpha()` |  |
| `static boolean isNinePatchChunk(byte[])` |  |
| `void setPaint(android.graphics.Paint)` |  |

---

### `class final Outline`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Outline()` |  |
| `Outline(@NonNull android.graphics.Outline)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean canClip()` |  |
| `float getAlpha()` |  |
| `float getRadius()` |  |
| `boolean getRect(@NonNull android.graphics.Rect)` |  |
| `boolean isEmpty()` |  |
| `void offset(int, int)` |  |
| `void set(@NonNull android.graphics.Outline)` |  |
| `void setAlpha(@FloatRange(from=0.0, to=1.0) float)` |  |
| `void setEmpty()` |  |
| `void setOval(int, int, int, int)` |  |
| `void setOval(@NonNull android.graphics.Rect)` |  |
| `void setPath(@NonNull android.graphics.Path)` |  |
| `void setRect(int, int, int, int)` |  |
| `void setRect(@NonNull android.graphics.Rect)` |  |
| `void setRoundRect(int, int, int, int, float)` |  |
| `void setRoundRect(@NonNull android.graphics.Rect, float)` |  |

---

### `class Paint`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Paint()` |  |
| `Paint(int)` |  |
| `Paint(android.graphics.Paint)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ANTI_ALIAS_FLAG = 1` |  |
| `static final int CURSOR_AFTER = 0` |  |
| `static final int CURSOR_AT = 4` |  |
| `static final int CURSOR_AT_OR_AFTER = 1` |  |
| `static final int CURSOR_AT_OR_BEFORE = 3` |  |
| `static final int CURSOR_BEFORE = 2` |  |
| `static final int DEV_KERN_TEXT_FLAG = 256` |  |
| `static final int DITHER_FLAG = 4` |  |
| `static final int EMBEDDED_BITMAP_TEXT_FLAG = 1024` |  |
| `static final int END_HYPHEN_EDIT_INSERT_ARMENIAN_HYPHEN = 3` |  |
| `static final int END_HYPHEN_EDIT_INSERT_HYPHEN = 2` |  |
| `static final int END_HYPHEN_EDIT_INSERT_MAQAF = 4` |  |
| `static final int END_HYPHEN_EDIT_INSERT_UCAS_HYPHEN = 5` |  |
| `static final int END_HYPHEN_EDIT_INSERT_ZWJ_AND_HYPHEN = 6` |  |
| `static final int END_HYPHEN_EDIT_NO_EDIT = 0` |  |
| `static final int END_HYPHEN_EDIT_REPLACE_WITH_HYPHEN = 1` |  |
| `static final int FAKE_BOLD_TEXT_FLAG = 32` |  |
| `static final int FILTER_BITMAP_FLAG = 2` |  |
| `static final int HINTING_OFF = 0` |  |
| `static final int HINTING_ON = 1` |  |
| `static final int LINEAR_TEXT_FLAG = 64` |  |
| `static final int START_HYPHEN_EDIT_INSERT_HYPHEN = 1` |  |
| `static final int START_HYPHEN_EDIT_INSERT_ZWJ = 2` |  |
| `static final int START_HYPHEN_EDIT_NO_EDIT = 0` |  |
| `static final int STRIKE_THRU_TEXT_FLAG = 16` |  |
| `static final int SUBPIXEL_TEXT_FLAG = 128` |  |
| `static final int UNDERLINE_TEXT_FLAG = 8` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `float ascent()` |  |
| `int breakText(char[], int, int, float, float[])` |  |
| `int breakText(CharSequence, int, int, boolean, float, float[])` |  |
| `int breakText(String, boolean, float, float[])` |  |
| `void clearShadowLayer()` |  |
| `float descent()` |  |
| `boolean equalsForTextMeasurement(@NonNull android.graphics.Paint)` |  |
| `int getAlpha()` |  |
| `android.graphics.ColorFilter getColorFilter()` |  |
| `int getEndHyphenEdit()` |  |
| `boolean getFillPath(android.graphics.Path, android.graphics.Path)` |  |
| `int getFlags()` |  |
| `String getFontFeatureSettings()` |  |
| `float getFontMetrics(android.graphics.Paint.FontMetrics)` |  |
| `android.graphics.Paint.FontMetrics getFontMetrics()` |  |
| `int getFontMetricsInt(android.graphics.Paint.FontMetricsInt)` |  |
| `android.graphics.Paint.FontMetricsInt getFontMetricsInt()` |  |
| `float getFontSpacing()` |  |
| `String getFontVariationSettings()` |  |
| `int getHinting()` |  |
| `float getLetterSpacing()` |  |
| `android.graphics.MaskFilter getMaskFilter()` |  |
| `int getOffsetForAdvance(char[], int, int, int, int, boolean, float)` |  |
| `int getOffsetForAdvance(CharSequence, int, int, int, int, boolean, float)` |  |
| `android.graphics.PathEffect getPathEffect()` |  |
| `float getRunAdvance(char[], int, int, int, int, boolean, int)` |  |
| `float getRunAdvance(CharSequence, int, int, int, int, boolean, int)` |  |
| `android.graphics.Shader getShader()` |  |
| `float getShadowLayerDx()` |  |
| `float getShadowLayerDy()` |  |
| `float getShadowLayerRadius()` |  |
| `int getStartHyphenEdit()` |  |
| `android.graphics.Paint.Cap getStrokeCap()` |  |
| `android.graphics.Paint.Join getStrokeJoin()` |  |
| `float getStrokeMiter()` |  |
| `float getStrokeWidth()` |  |
| `android.graphics.Paint.Style getStyle()` |  |
| `android.graphics.Paint.Align getTextAlign()` |  |
| `void getTextBounds(String, int, int, android.graphics.Rect)` |  |
| `void getTextBounds(@NonNull CharSequence, int, int, @NonNull android.graphics.Rect)` |  |
| `void getTextBounds(char[], int, int, android.graphics.Rect)` |  |
| `void getTextPath(char[], int, int, float, float, android.graphics.Path)` |  |
| `void getTextPath(String, int, int, float, float, android.graphics.Path)` |  |
| `float getTextRunAdvances(@NonNull char[], @IntRange(from=0) int, @IntRange(from=0) int, @IntRange(from=0) int, @IntRange(from=0) int, boolean, @Nullable float[], @IntRange(from=0) int)` |  |
| `int getTextRunCursor(@NonNull char[], @IntRange(from=0) int, @IntRange(from=0) int, boolean, @IntRange(from=0) int, int)` |  |
| `int getTextRunCursor(@NonNull CharSequence, @IntRange(from=0) int, @IntRange(from=0) int, boolean, @IntRange(from=0) int, int)` |  |
| `float getTextScaleX()` |  |
| `float getTextSize()` |  |
| `float getTextSkewX()` |  |
| `int getTextWidths(char[], int, int, float[])` |  |
| `int getTextWidths(CharSequence, int, int, float[])` |  |
| `int getTextWidths(String, int, int, float[])` |  |
| `int getTextWidths(String, float[])` |  |
| `android.graphics.Typeface getTypeface()` |  |
| `android.graphics.Xfermode getXfermode()` |  |
| `boolean hasGlyph(String)` |  |
| `final boolean isAntiAlias()` |  |
| `final boolean isDither()` |  |
| `boolean isElegantTextHeight()` |  |
| `final boolean isFakeBoldText()` |  |
| `final boolean isFilterBitmap()` |  |
| `final boolean isLinearText()` |  |
| `final boolean isStrikeThruText()` |  |
| `final boolean isSubpixelText()` |  |
| `final boolean isUnderlineText()` |  |
| `float measureText(char[], int, int)` |  |
| `float measureText(String, int, int)` |  |
| `float measureText(String)` |  |
| `float measureText(CharSequence, int, int)` |  |
| `void reset()` |  |
| `void set(android.graphics.Paint)` |  |
| `void setARGB(int, int, int, int)` |  |
| `void setAlpha(int)` |  |
| `void setAntiAlias(boolean)` |  |
| `void setBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setColor(@ColorInt int)` |  |
| `void setColor(@ColorLong long)` |  |
| `android.graphics.ColorFilter setColorFilter(android.graphics.ColorFilter)` |  |
| `void setDither(boolean)` |  |
| `void setElegantTextHeight(boolean)` |  |
| `void setEndHyphenEdit(int)` |  |
| `void setFakeBoldText(boolean)` |  |
| `void setFilterBitmap(boolean)` |  |
| `void setFlags(int)` |  |
| `void setFontFeatureSettings(String)` |  |
| `boolean setFontVariationSettings(String)` |  |
| `void setHinting(int)` |  |
| `void setLetterSpacing(float)` |  |
| `void setLinearText(boolean)` |  |
| `android.graphics.MaskFilter setMaskFilter(android.graphics.MaskFilter)` |  |
| `android.graphics.PathEffect setPathEffect(android.graphics.PathEffect)` |  |
| `android.graphics.Shader setShader(android.graphics.Shader)` |  |
| `void setShadowLayer(float, float, float, @ColorInt int)` |  |
| `void setShadowLayer(float, float, float, @ColorLong long)` |  |
| `void setStartHyphenEdit(int)` |  |
| `void setStrikeThruText(boolean)` |  |
| `void setStrokeCap(android.graphics.Paint.Cap)` |  |
| `void setStrokeJoin(android.graphics.Paint.Join)` |  |
| `void setStrokeMiter(float)` |  |
| `void setStrokeWidth(float)` |  |
| `void setStyle(android.graphics.Paint.Style)` |  |
| `void setSubpixelText(boolean)` |  |
| `void setTextAlign(android.graphics.Paint.Align)` |  |
| `void setTextLocale(@NonNull java.util.Locale)` |  |
| `void setTextLocales(@NonNull @Size(min=1) android.os.LocaleList)` |  |
| `void setTextScaleX(float)` |  |
| `void setTextSize(float)` |  |
| `void setTextSkewX(float)` |  |
| `android.graphics.Typeface setTypeface(android.graphics.Typeface)` |  |
| `void setUnderlineText(boolean)` |  |
| `void setWordSpacing(@Px float)` |  |
| `android.graphics.Xfermode setXfermode(android.graphics.Xfermode)` |  |

---

### `enum Paint.Align`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.Paint.Align CENTER` |  |
| `static final android.graphics.Paint.Align LEFT` |  |
| `static final android.graphics.Paint.Align RIGHT` |  |

---

### `enum Paint.Cap`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.Paint.Cap BUTT` |  |
| `static final android.graphics.Paint.Cap ROUND` |  |
| `static final android.graphics.Paint.Cap SQUARE` |  |

---

### `class static Paint.FontMetrics`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Paint.FontMetrics()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `float ascent` |  |
| `float bottom` |  |
| `float descent` |  |
| `float leading` |  |
| `float top` |  |

---

### `class static Paint.FontMetricsInt`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Paint.FontMetricsInt()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `int ascent` |  |
| `int bottom` |  |
| `int descent` |  |
| `int leading` |  |
| `int top` |  |

---

### `enum Paint.Join`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.Paint.Join BEVEL` |  |
| `static final android.graphics.Paint.Join MITER` |  |
| `static final android.graphics.Paint.Join ROUND` |  |

---

### `enum Paint.Style`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.Paint.Style FILL` |  |
| `static final android.graphics.Paint.Style FILL_AND_STROKE` |  |
| `static final android.graphics.Paint.Style STROKE` |  |

---

### `class PaintFlagsDrawFilter`

- **继承：** `android.graphics.DrawFilter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PaintFlagsDrawFilter(int, int)` |  |

---

### `class Path`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Path()` |  |
| `Path(@Nullable android.graphics.Path)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addArc(@NonNull android.graphics.RectF, float, float)` |  |
| `void addArc(float, float, float, float, float, float)` |  |
| `void addCircle(float, float, float, @NonNull android.graphics.Path.Direction)` |  |
| `void addOval(@NonNull android.graphics.RectF, @NonNull android.graphics.Path.Direction)` |  |
| `void addOval(float, float, float, float, @NonNull android.graphics.Path.Direction)` |  |
| `void addPath(@NonNull android.graphics.Path, float, float)` |  |
| `void addPath(@NonNull android.graphics.Path)` |  |
| `void addPath(@NonNull android.graphics.Path, @NonNull android.graphics.Matrix)` |  |
| `void addRect(@NonNull android.graphics.RectF, @NonNull android.graphics.Path.Direction)` |  |
| `void addRect(float, float, float, float, @NonNull android.graphics.Path.Direction)` |  |
| `void addRoundRect(@NonNull android.graphics.RectF, float, float, @NonNull android.graphics.Path.Direction)` |  |
| `void addRoundRect(float, float, float, float, float, float, @NonNull android.graphics.Path.Direction)` |  |
| `void addRoundRect(@NonNull android.graphics.RectF, @NonNull float[], @NonNull android.graphics.Path.Direction)` |  |
| `void addRoundRect(float, float, float, float, @NonNull float[], @NonNull android.graphics.Path.Direction)` |  |
| `void arcTo(@NonNull android.graphics.RectF, float, float, boolean)` |  |
| `void arcTo(@NonNull android.graphics.RectF, float, float)` |  |
| `void arcTo(float, float, float, float, float, float, boolean)` |  |
| `void close()` |  |
| `void computeBounds(@NonNull android.graphics.RectF, boolean)` |  |
| `void cubicTo(float, float, float, float, float, float)` |  |
| `void incReserve(int)` |  |
| `boolean isEmpty()` |  |
| `boolean isInverseFillType()` |  |
| `boolean isRect(@Nullable android.graphics.RectF)` |  |
| `void lineTo(float, float)` |  |
| `void moveTo(float, float)` |  |
| `void offset(float, float, @Nullable android.graphics.Path)` |  |
| `void offset(float, float)` |  |
| `boolean op(@NonNull android.graphics.Path, @NonNull android.graphics.Path.Op)` |  |
| `boolean op(@NonNull android.graphics.Path, @NonNull android.graphics.Path, @NonNull android.graphics.Path.Op)` |  |
| `void quadTo(float, float, float, float)` |  |
| `void rCubicTo(float, float, float, float, float, float)` |  |
| `void rLineTo(float, float)` |  |
| `void rMoveTo(float, float)` |  |
| `void rQuadTo(float, float, float, float)` |  |
| `void reset()` |  |
| `void rewind()` |  |
| `void set(@NonNull android.graphics.Path)` |  |
| `void setFillType(@NonNull android.graphics.Path.FillType)` |  |
| `void setLastPoint(float, float)` |  |
| `void toggleInverseFillType()` |  |
| `void transform(@NonNull android.graphics.Matrix, @Nullable android.graphics.Path)` |  |
| `void transform(@NonNull android.graphics.Matrix)` |  |

---

### `enum Path.Direction`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.Path.Direction CCW` |  |
| `static final android.graphics.Path.Direction CW` |  |

---

### `enum Path.FillType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.Path.FillType EVEN_ODD` |  |
| `static final android.graphics.Path.FillType INVERSE_EVEN_ODD` |  |
| `static final android.graphics.Path.FillType INVERSE_WINDING` |  |
| `static final android.graphics.Path.FillType WINDING` |  |

---

### `enum Path.Op`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.Path.Op DIFFERENCE` |  |
| `static final android.graphics.Path.Op INTERSECT` |  |
| `static final android.graphics.Path.Op REVERSE_DIFFERENCE` |  |
| `static final android.graphics.Path.Op UNION` |  |
| `static final android.graphics.Path.Op XOR` |  |

---

### `class PathDashPathEffect`

- **继承：** `android.graphics.PathEffect`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PathDashPathEffect(android.graphics.Path, float, float, android.graphics.PathDashPathEffect.Style)` |  |

---

### `enum PathDashPathEffect.Style`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.PathDashPathEffect.Style MORPH` |  |
| `static final android.graphics.PathDashPathEffect.Style ROTATE` |  |
| `static final android.graphics.PathDashPathEffect.Style TRANSLATE` |  |

---

### `class PathEffect`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PathEffect()` |  |

---

### `class PathMeasure`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PathMeasure()` |  |
| `PathMeasure(android.graphics.Path, boolean)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int POSITION_MATRIX_FLAG = 1` |  |
| `static final int TANGENT_MATRIX_FLAG = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `float getLength()` |  |
| `boolean getMatrix(float, android.graphics.Matrix, int)` |  |
| `boolean getPosTan(float, float[], float[])` |  |
| `boolean getSegment(float, float, android.graphics.Path, boolean)` |  |
| `boolean isClosed()` |  |
| `boolean nextContour()` |  |
| `void setPath(android.graphics.Path, boolean)` |  |

---

### `class Picture`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Picture()` |  |
| `Picture(android.graphics.Picture)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void draw(@NonNull android.graphics.Canvas)` |  |
| `void endRecording()` |  |
| `int getHeight()` |  |
| `int getWidth()` |  |
| `boolean requiresHardwareAcceleration()` |  |

---

### `class PixelFormat`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PixelFormat()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int OPAQUE = -1` |  |
| `static final int RGBA_1010102 = 43` |  |
| `static final int RGBA_8888 = 1` |  |
| `static final int RGBA_F16 = 22` |  |
| `static final int RGBX_8888 = 2` |  |
| `static final int RGB_565 = 4` |  |
| `static final int RGB_888 = 3` |  |
| `static final int TRANSLUCENT = -3` |  |
| `static final int TRANSPARENT = -2` |  |
| `static final int UNKNOWN = 0` |  |
| `int bitsPerPixel` |  |
| `int bytesPerPixel` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean formatHasAlpha(int)` |  |
| `static void getPixelFormatInfo(int, android.graphics.PixelFormat)` |  |

---

### `class Point`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Point()` |  |
| `Point(int, int)` |  |
| `Point(@NonNull android.graphics.Point)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `int x` |  |
| `int y` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `final boolean equals(int, int)` |  |
| `final void negate()` |  |
| `final void offset(int, int)` |  |
| `void readFromParcel(@NonNull android.os.Parcel)` |  |
| `void set(int, int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class PointF`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PointF()` |  |
| `PointF(float, float)` |  |
| `PointF(@NonNull android.graphics.Point)` |  |
| `PointF(@NonNull android.graphics.PointF)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `float x` |  |
| `float y` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `final boolean equals(float, float)` |  |
| `final float length()` |  |
| `static float length(float, float)` |  |
| `final void negate()` |  |
| `final void offset(float, float)` |  |
| `void readFromParcel(@NonNull android.os.Parcel)` |  |
| `final void set(float, float)` |  |
| `final void set(@NonNull android.graphics.PointF)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class PorterDuff`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PorterDuff()` |  |

---

### `enum PorterDuff.Mode`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.PorterDuff.Mode ADD` |  |
| `static final android.graphics.PorterDuff.Mode CLEAR` |  |
| `static final android.graphics.PorterDuff.Mode DARKEN` |  |
| `static final android.graphics.PorterDuff.Mode DST` |  |
| `static final android.graphics.PorterDuff.Mode DST_ATOP` |  |
| `static final android.graphics.PorterDuff.Mode DST_IN` |  |
| `static final android.graphics.PorterDuff.Mode DST_OUT` |  |
| `static final android.graphics.PorterDuff.Mode DST_OVER` |  |
| `static final android.graphics.PorterDuff.Mode LIGHTEN` |  |
| `static final android.graphics.PorterDuff.Mode MULTIPLY` |  |
| `static final android.graphics.PorterDuff.Mode OVERLAY` |  |
| `static final android.graphics.PorterDuff.Mode SCREEN` |  |
| `static final android.graphics.PorterDuff.Mode SRC` |  |
| `static final android.graphics.PorterDuff.Mode SRC_ATOP` |  |
| `static final android.graphics.PorterDuff.Mode SRC_IN` |  |
| `static final android.graphics.PorterDuff.Mode SRC_OUT` |  |
| `static final android.graphics.PorterDuff.Mode SRC_OVER` |  |
| `static final android.graphics.PorterDuff.Mode XOR` |  |

---

### `class PorterDuffColorFilter`

- **继承：** `android.graphics.ColorFilter`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PorterDuffColorFilter(@ColorInt int, @NonNull android.graphics.PorterDuff.Mode)` |  |

---

### `class PorterDuffXfermode`

- **继承：** `android.graphics.Xfermode`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PorterDuffXfermode(android.graphics.PorterDuff.Mode)` |  |

---

### `interface PostProcessor`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int onPostProcess(@NonNull android.graphics.Canvas)` |  |

---

### `class RadialGradient`

- **继承：** `android.graphics.Shader`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RadialGradient(float, float, float, @ColorInt @NonNull int[], @Nullable float[], @NonNull android.graphics.Shader.TileMode)` |  |
| `RadialGradient(float, float, float, @ColorLong @NonNull long[], @Nullable float[], @NonNull android.graphics.Shader.TileMode)` |  |
| `RadialGradient(float, float, float, @ColorInt int, @ColorInt int, @NonNull android.graphics.Shader.TileMode)` |  |
| `RadialGradient(float, float, float, @ColorLong long, @ColorLong long, @NonNull android.graphics.Shader.TileMode)` |  |

---

### `class final RecordingCanvas`

- **继承：** `android.graphics.Canvas`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final void drawPatch(@NonNull android.graphics.NinePatch, @NonNull android.graphics.Rect, @Nullable android.graphics.Paint)` |  |
| `final void drawPatch(@NonNull android.graphics.NinePatch, @NonNull android.graphics.RectF, @Nullable android.graphics.Paint)` |  |

---

### `class final Rect`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Rect()` |  |
| `Rect(int, int, int, int)` |  |
| `Rect(@Nullable android.graphics.Rect)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `int bottom` |  |
| `int left` |  |
| `int right` |  |
| `int top` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int centerX()` |  |
| `int centerY()` |  |
| `boolean contains(int, int)` |  |
| `boolean contains(int, int, int, int)` |  |
| `boolean contains(@NonNull android.graphics.Rect)` |  |
| `int describeContents()` |  |
| `float exactCenterX()` |  |
| `float exactCenterY()` |  |
| `int height()` |  |
| `void inset(int, int)` |  |
| `boolean intersects(int, int, int, int)` |  |
| `static boolean intersects(@NonNull android.graphics.Rect, @NonNull android.graphics.Rect)` |  |
| `boolean isEmpty()` |  |
| `void offset(int, int)` |  |
| `void offsetTo(int, int)` |  |
| `void readFromParcel(@NonNull android.os.Parcel)` |  |
| `void set(int, int, int, int)` |  |
| `void set(@NonNull android.graphics.Rect)` |  |
| `void setEmpty()` |  |
| `void sort()` |  |
| `void union(int, int, int, int)` |  |
| `void union(@NonNull android.graphics.Rect)` |  |
| `void union(int, int)` |  |
| `int width()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class RectF`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RectF()` |  |
| `RectF(float, float, float, float)` |  |
| `RectF(@Nullable android.graphics.RectF)` |  |
| `RectF(@Nullable android.graphics.Rect)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `float bottom` |  |
| `float left` |  |
| `float right` |  |
| `float top` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final float centerX()` |  |
| `final float centerY()` |  |
| `boolean contains(float, float)` |  |
| `boolean contains(float, float, float, float)` |  |
| `boolean contains(@NonNull android.graphics.RectF)` |  |
| `int describeContents()` |  |
| `final float height()` |  |
| `void inset(float, float)` |  |
| `boolean intersect(float, float, float, float)` |  |
| `boolean intersect(@NonNull android.graphics.RectF)` |  |
| `boolean intersects(float, float, float, float)` |  |
| `static boolean intersects(@NonNull android.graphics.RectF, @NonNull android.graphics.RectF)` |  |
| `final boolean isEmpty()` |  |
| `void offset(float, float)` |  |
| `void offsetTo(float, float)` |  |
| `void readFromParcel(@NonNull android.os.Parcel)` |  |
| `void round(@NonNull android.graphics.Rect)` |  |
| `void roundOut(@NonNull android.graphics.Rect)` |  |
| `void set(float, float, float, float)` |  |
| `void set(@NonNull android.graphics.RectF)` |  |
| `void set(@NonNull android.graphics.Rect)` |  |
| `void setEmpty()` |  |
| `boolean setIntersect(@NonNull android.graphics.RectF, @NonNull android.graphics.RectF)` |  |
| `void sort()` |  |
| `void union(float, float, float, float)` |  |
| `void union(@NonNull android.graphics.RectF)` |  |
| `void union(float, float)` |  |
| `final float width()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class Region`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Region()` |  |
| `Region(@NonNull android.graphics.Region)` |  |
| `Region(@NonNull android.graphics.Rect)` |  |
| `Region(int, int, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean contains(int, int)` |  |
| `int describeContents()` |  |
| `boolean getBoundaryPath(@NonNull android.graphics.Path)` |  |
| `boolean getBounds(@NonNull android.graphics.Rect)` |  |
| `boolean isComplex()` |  |
| `boolean isEmpty()` |  |
| `boolean isRect()` |  |
| `boolean op(@NonNull android.graphics.Rect, @NonNull android.graphics.Region.Op)` |  |
| `boolean op(int, int, int, int, @NonNull android.graphics.Region.Op)` |  |
| `boolean op(@NonNull android.graphics.Region, @NonNull android.graphics.Region.Op)` |  |
| `boolean op(@NonNull android.graphics.Rect, @NonNull android.graphics.Region, @NonNull android.graphics.Region.Op)` |  |
| `boolean op(@NonNull android.graphics.Region, @NonNull android.graphics.Region, @NonNull android.graphics.Region.Op)` |  |
| `boolean quickContains(@NonNull android.graphics.Rect)` |  |
| `boolean quickContains(int, int, int, int)` |  |
| `boolean quickReject(@NonNull android.graphics.Rect)` |  |
| `boolean quickReject(int, int, int, int)` |  |
| `boolean quickReject(android.graphics.Region)` |  |
| `boolean set(@NonNull android.graphics.Region)` |  |
| `boolean set(@NonNull android.graphics.Rect)` |  |
| `boolean set(int, int, int, int)` |  |
| `void setEmpty()` |  |
| `boolean setPath(@NonNull android.graphics.Path, @NonNull android.graphics.Region)` |  |
| `void translate(int, int)` |  |
| `void translate(int, int, android.graphics.Region)` |  |
| `final boolean union(@NonNull android.graphics.Rect)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `enum Region.Op`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.Region.Op DIFFERENCE` |  |
| `static final android.graphics.Region.Op INTERSECT` |  |
| `static final android.graphics.Region.Op REPLACE` |  |
| `static final android.graphics.Region.Op REVERSE_DIFFERENCE` |  |
| `static final android.graphics.Region.Op UNION` |  |
| `static final android.graphics.Region.Op XOR` |  |

---

### `class RegionIterator`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RegionIterator(android.graphics.Region)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final boolean next(android.graphics.Rect)` |  |

---

### `class final RenderNode`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RenderNode(@Nullable String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `long computeApproximateMemoryUsage()` |  |
| `void discardDisplayList()` |  |
| `void endRecording()` |  |
| `float getAlpha()` |  |
| `int getBottom()` |  |
| `boolean getClipToBounds()` |  |
| `boolean getClipToOutline()` |  |
| `float getElevation()` |  |
| `int getHeight()` |  |
| `void getInverseMatrix(@NonNull android.graphics.Matrix)` |  |
| `int getLeft()` |  |
| `void getMatrix(@NonNull android.graphics.Matrix)` |  |
| `float getPivotX()` |  |
| `float getPivotY()` |  |
| `int getRight()` |  |
| `float getRotationX()` |  |
| `float getRotationY()` |  |
| `float getRotationZ()` |  |
| `float getScaleX()` |  |
| `float getScaleY()` |  |
| `int getTop()` |  |
| `float getTranslationX()` |  |
| `float getTranslationY()` |  |
| `float getTranslationZ()` |  |
| `long getUniqueId()` |  |
| `boolean getUseCompositingLayer()` |  |
| `int getWidth()` |  |
| `boolean hasDisplayList()` |  |
| `boolean hasIdentityMatrix()` |  |
| `boolean hasOverlappingRendering()` |  |
| `boolean hasShadow()` |  |
| `boolean isForceDarkAllowed()` |  |
| `boolean isPivotExplicitlySet()` |  |
| `boolean offsetLeftAndRight(int)` |  |
| `boolean offsetTopAndBottom(int)` |  |
| `boolean resetPivot()` |  |
| `boolean setAlpha(float)` |  |
| `boolean setAmbientShadowColor(@ColorInt int)` |  |
| `boolean setCameraDistance(@FloatRange(from=0.0f, to=java.lang.Float.MAX_VALUE) float)` |  |
| `boolean setClipRect(@Nullable android.graphics.Rect)` |  |
| `boolean setClipToBounds(boolean)` |  |
| `boolean setClipToOutline(boolean)` |  |
| `boolean setElevation(float)` |  |
| `boolean setForceDarkAllowed(boolean)` |  |
| `boolean setHasOverlappingRendering(boolean)` |  |
| `boolean setOutline(@Nullable android.graphics.Outline)` |  |
| `boolean setPivotX(float)` |  |
| `boolean setPivotY(float)` |  |
| `boolean setPosition(int, int, int, int)` |  |
| `boolean setPosition(@NonNull android.graphics.Rect)` |  |
| `boolean setProjectBackwards(boolean)` |  |
| `boolean setProjectionReceiver(boolean)` |  |
| `boolean setRotationX(float)` |  |
| `boolean setRotationY(float)` |  |
| `boolean setRotationZ(float)` |  |
| `boolean setScaleX(float)` |  |
| `boolean setScaleY(float)` |  |
| `boolean setSpotShadowColor(@ColorInt int)` |  |
| `boolean setTranslationX(float)` |  |
| `boolean setTranslationY(float)` |  |
| `boolean setTranslationZ(float)` |  |
| `boolean setUseCompositingLayer(boolean, @Nullable android.graphics.Paint)` |  |

---

### `class Shader`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean getLocalMatrix(@NonNull android.graphics.Matrix)` |  |
| `void setLocalMatrix(@Nullable android.graphics.Matrix)` |  |

---

### `enum Shader.TileMode`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.Shader.TileMode CLAMP` |  |
| `static final android.graphics.Shader.TileMode MIRROR` |  |
| `static final android.graphics.Shader.TileMode REPEAT` |  |

---

### `class SumPathEffect`

- **继承：** `android.graphics.PathEffect`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SumPathEffect(android.graphics.PathEffect, android.graphics.PathEffect)` |  |

---

### `class SurfaceTexture`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SurfaceTexture(int)` |  |
| `SurfaceTexture(int, boolean)` |  |
| `SurfaceTexture(boolean)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void attachToGLContext(int)` |  |
| `void detachFromGLContext()` |  |
| `long getTimestamp()` |  |
| `void getTransformMatrix(float[])` |  |
| `boolean isReleased()` |  |
| `void release()` |  |
| `void releaseTexImage()` |  |
| `void setDefaultBufferSize(int, int)` |  |
| `void setOnFrameAvailableListener(@Nullable android.graphics.SurfaceTexture.OnFrameAvailableListener)` |  |
| `void setOnFrameAvailableListener(@Nullable android.graphics.SurfaceTexture.OnFrameAvailableListener, @Nullable android.os.Handler)` |  |
| `void updateTexImage()` |  |

---

### `interface static SurfaceTexture.OnFrameAvailableListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onFrameAvailable(android.graphics.SurfaceTexture)` |  |

---

### `class static SurfaceTexture.OutOfResourcesException` ~~DEPRECATED~~

- **继承：** `java.lang.Exception`
- **注解：** `@Deprecated`

---

### `class SweepGradient`

- **继承：** `android.graphics.Shader`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SweepGradient(float, float, @ColorInt @NonNull int[], @Nullable float[])` |  |
| `SweepGradient(float, float, @ColorLong @NonNull long[], @Nullable float[])` |  |
| `SweepGradient(float, float, @ColorInt int, @ColorInt int)` |  |
| `SweepGradient(float, float, @ColorLong long, @ColorLong long)` |  |

---

### `class Typeface`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BOLD = 1` |  |
| `static final int BOLD_ITALIC = 3` |  |
| `static final android.graphics.Typeface DEFAULT` |  |
| `static final android.graphics.Typeface DEFAULT_BOLD` |  |
| `static final int ITALIC = 2` |  |
| `static final android.graphics.Typeface MONOSPACE` |  |
| `static final int NORMAL = 0` |  |
| `static final android.graphics.Typeface SANS_SERIF` |  |
| `static final android.graphics.Typeface SERIF` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.graphics.Typeface create(String, int)` |  |
| `static android.graphics.Typeface create(android.graphics.Typeface, int)` |  |
| `static android.graphics.Typeface createFromAsset(android.content.res.AssetManager, String)` |  |
| `static android.graphics.Typeface createFromFile(@Nullable java.io.File)` |  |
| `static android.graphics.Typeface createFromFile(@Nullable String)` |  |
| `static android.graphics.Typeface defaultFromStyle(int)` |  |
| `int getStyle()` |  |
| `final boolean isBold()` |  |
| `final boolean isItalic()` |  |

---

### `class static final Typeface.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Typeface.Builder(@NonNull java.io.File)` |  |
| `Typeface.Builder(@NonNull java.io.FileDescriptor)` |  |
| `Typeface.Builder(@NonNull String)` |  |
| `Typeface.Builder(@NonNull android.content.res.AssetManager, @NonNull String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.Typeface build()` |  |
| `android.graphics.Typeface.Builder setFallback(@Nullable String)` |  |
| `android.graphics.Typeface.Builder setFontVariationSettings(@Nullable String)` |  |
| `android.graphics.Typeface.Builder setFontVariationSettings(@Nullable android.graphics.fonts.FontVariationAxis[])` |  |
| `android.graphics.Typeface.Builder setItalic(boolean)` |  |
| `android.graphics.Typeface.Builder setTtcIndex(@IntRange(from=0) int)` |  |
| `android.graphics.Typeface.Builder setWeight(@IntRange(from=1, to=1000) int)` |  |

---

### `class static final Typeface.CustomFallbackBuilder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Typeface.CustomFallbackBuilder(@NonNull android.graphics.fonts.FontFamily)` |  |

---

### `class Xfermode`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Xfermode()` |  |

---

### `class YuvImage`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `YuvImage(byte[], int, int, int, int[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean compressToJpeg(android.graphics.Rect, int, java.io.OutputStream)` |  |
| `int getHeight()` |  |
| `int[] getStrides()` |  |
| `int getWidth()` |  |
| `byte[] getYuvData()` |  |
| `int getYuvFormat()` |  |

---

## Package: `android.graphics.drawable`

### `class AdaptiveIconDrawable`

- **继承：** `android.graphics.drawable.Drawable`
- **实现：** `android.graphics.drawable.Drawable.Callback`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AdaptiveIconDrawable(android.graphics.drawable.Drawable, android.graphics.drawable.Drawable)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void draw(android.graphics.Canvas)` |  |
| `android.graphics.drawable.Drawable getBackground()` |  |
| `static float getExtraInsetFraction()` |  |
| `android.graphics.drawable.Drawable getForeground()` |  |
| `android.graphics.Path getIconMask()` |  |
| `int getOpacity()` |  |
| `void invalidateDrawable(@NonNull android.graphics.drawable.Drawable)` |  |
| `void scheduleDrawable(@NonNull android.graphics.drawable.Drawable, @NonNull Runnable, long)` |  |
| `void setAlpha(int)` |  |
| `void setColorFilter(android.graphics.ColorFilter)` |  |
| `void setDither(boolean)` |  |
| `void setOpacity(int)` |  |
| `void unscheduleDrawable(@NonNull android.graphics.drawable.Drawable, @NonNull Runnable)` |  |

---

### `interface Animatable`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean isRunning()` |  |
| `void start()` |  |
| `void stop()` |  |

---

### `interface Animatable2`

- **继承：** `android.graphics.drawable.Animatable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Animatable2.AnimationCallback()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void clearAnimationCallbacks()` |  |
| `void registerAnimationCallback(@NonNull android.graphics.drawable.Animatable2.AnimationCallback)` |  |
| `boolean unregisterAnimationCallback(@NonNull android.graphics.drawable.Animatable2.AnimationCallback)` |  |
| `void onAnimationEnd(android.graphics.drawable.Drawable)` |  |
| `void onAnimationStart(android.graphics.drawable.Drawable)` |  |

---

### `class AnimatedImageDrawable`

- **继承：** `android.graphics.drawable.Drawable`
- **实现：** `android.graphics.drawable.Animatable2`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AnimatedImageDrawable()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int REPEAT_INFINITE = -1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void clearAnimationCallbacks()` |  |
| `void draw(@NonNull android.graphics.Canvas)` |  |
| `int getOpacity()` |  |
| `int getRepeatCount()` |  |
| `final boolean isAutoMirrored()` |  |
| `boolean isRunning()` |  |
| `void registerAnimationCallback(@NonNull android.graphics.drawable.Animatable2.AnimationCallback)` |  |
| `void setAlpha(@IntRange(from=0, to=255) int)` |  |
| `void setColorFilter(@Nullable android.graphics.ColorFilter)` |  |
| `void setRepeatCount(@IntRange(from=android.graphics.drawable.AnimatedImageDrawable.REPEAT_INFINITE) int)` |  |
| `void start()` |  |
| `void stop()` |  |
| `boolean unregisterAnimationCallback(@NonNull android.graphics.drawable.Animatable2.AnimationCallback)` |  |

---

### `class AnimatedStateListDrawable`

- **继承：** `android.graphics.drawable.StateListDrawable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AnimatedStateListDrawable()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addState(@NonNull int[], @NonNull android.graphics.drawable.Drawable, int)` |  |
| `<T extends android.graphics.drawable.Drawable & android.graphics.drawable.Animatable> void addTransition(int, int, @NonNull T, boolean)` |  |

---

### `class AnimatedVectorDrawable`

- **继承：** `android.graphics.drawable.Drawable`
- **实现：** `android.graphics.drawable.Animatable2`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AnimatedVectorDrawable()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void clearAnimationCallbacks()` |  |
| `void draw(android.graphics.Canvas)` |  |
| `int getOpacity()` |  |
| `boolean isRunning()` |  |
| `void registerAnimationCallback(@NonNull android.graphics.drawable.Animatable2.AnimationCallback)` |  |
| `void reset()` |  |
| `void setAlpha(int)` |  |
| `void setColorFilter(android.graphics.ColorFilter)` |  |
| `void start()` |  |
| `void stop()` |  |
| `boolean unregisterAnimationCallback(@NonNull android.graphics.drawable.Animatable2.AnimationCallback)` |  |

---

### `class AnimationDrawable`

- **继承：** `android.graphics.drawable.DrawableContainer`
- **实现：** `android.graphics.drawable.Animatable java.lang.Runnable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AnimationDrawable()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addFrame(@NonNull android.graphics.drawable.Drawable, int)` |  |
| `int getDuration(int)` |  |
| `android.graphics.drawable.Drawable getFrame(int)` |  |
| `int getNumberOfFrames()` |  |
| `boolean isOneShot()` |  |
| `boolean isRunning()` |  |
| `void run()` |  |
| `void setOneShot(boolean)` |  |
| `void start()` |  |
| `void stop()` |  |

---

### `class BitmapDrawable`

- **继承：** `android.graphics.drawable.Drawable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BitmapDrawable(android.content.res.Resources, android.graphics.Bitmap)` |  |
| `BitmapDrawable(android.content.res.Resources, String)` |  |
| `BitmapDrawable(android.content.res.Resources, java.io.InputStream)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void draw(android.graphics.Canvas)` |  |
| `final android.graphics.Bitmap getBitmap()` |  |
| `final android.graphics.drawable.Drawable.ConstantState getConstantState()` |  |
| `int getGravity()` |  |
| `int getOpacity()` |  |
| `final android.graphics.Paint getPaint()` |  |
| `android.graphics.Shader.TileMode getTileModeX()` |  |
| `android.graphics.Shader.TileMode getTileModeY()` |  |
| `boolean hasAntiAlias()` |  |
| `boolean hasMipMap()` |  |
| `final boolean isAutoMirrored()` |  |
| `void setAlpha(int)` |  |
| `void setAntiAlias(boolean)` |  |
| `void setColorFilter(android.graphics.ColorFilter)` |  |
| `void setDither(boolean)` |  |
| `void setGravity(int)` |  |
| `void setMipMap(boolean)` |  |
| `void setTargetDensity(android.graphics.Canvas)` |  |
| `void setTargetDensity(android.util.DisplayMetrics)` |  |
| `void setTargetDensity(int)` |  |
| `void setTileModeX(android.graphics.Shader.TileMode)` |  |
| `void setTileModeXY(android.graphics.Shader.TileMode, android.graphics.Shader.TileMode)` |  |
| `final void setTileModeY(android.graphics.Shader.TileMode)` |  |

---

### `class ClipDrawable`

- **继承：** `android.graphics.drawable.DrawableWrapper`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ClipDrawable(android.graphics.drawable.Drawable, int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int HORIZONTAL = 1` |  |
| `static final int VERTICAL = 2` |  |

---

### `class ColorDrawable`

- **继承：** `android.graphics.drawable.Drawable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ColorDrawable()` |  |
| `ColorDrawable(@ColorInt int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void draw(android.graphics.Canvas)` |  |
| `int getOpacity()` |  |
| `void setAlpha(int)` |  |
| `void setColor(@ColorInt int)` |  |
| `void setColorFilter(android.graphics.ColorFilter)` |  |

---

### `class ColorStateListDrawable`

- **继承：** `android.graphics.drawable.Drawable`
- **实现：** `android.graphics.drawable.Drawable.Callback`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ColorStateListDrawable()` |  |
| `ColorStateListDrawable(@NonNull android.content.res.ColorStateList)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void clearAlpha()` |  |
| `void draw(@NonNull android.graphics.Canvas)` |  |
| `int getOpacity()` |  |
| `boolean hasFocusStateSpecified()` |  |
| `void invalidateDrawable(@NonNull android.graphics.drawable.Drawable)` |  |
| `void scheduleDrawable(@NonNull android.graphics.drawable.Drawable, @NonNull Runnable, long)` |  |
| `void setAlpha(@IntRange(from=0, to=255) int)` |  |
| `void setColorFilter(@Nullable android.graphics.ColorFilter)` |  |
| `void setColorStateList(@NonNull android.content.res.ColorStateList)` |  |
| `void unscheduleDrawable(@NonNull android.graphics.drawable.Drawable, @NonNull Runnable)` |  |

---

### `class abstract Drawable`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Drawable()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void applyTheme(@NonNull android.content.res.Resources.Theme)` |  |
| `boolean canApplyTheme()` |  |
| `void clearColorFilter()` |  |
| `final void copyBounds(@NonNull android.graphics.Rect)` |  |
| `static android.graphics.drawable.Drawable createFromResourceStream(android.content.res.Resources, android.util.TypedValue, java.io.InputStream, String)` |  |
| `static android.graphics.drawable.Drawable createFromStream(java.io.InputStream, String)` |  |
| `abstract void draw(@NonNull android.graphics.Canvas)` |  |
| `int getChangingConfigurations()` |  |
| `void getHotspotBounds(@NonNull android.graphics.Rect)` |  |
| `int getIntrinsicHeight()` |  |
| `int getIntrinsicWidth()` |  |
| `int getLayoutDirection()` |  |
| `int getMinimumHeight()` |  |
| `int getMinimumWidth()` |  |
| `void getOutline(@NonNull android.graphics.Outline)` |  |
| `boolean getPadding(@NonNull android.graphics.Rect)` |  |
| `void inflate(@NonNull android.content.res.Resources, @NonNull org.xmlpull.v1.XmlPullParser, @NonNull android.util.AttributeSet) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException` |  |
| `void inflate(@NonNull android.content.res.Resources, @NonNull org.xmlpull.v1.XmlPullParser, @NonNull android.util.AttributeSet, @Nullable android.content.res.Resources.Theme) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException` |  |
| `void invalidateSelf()` |  |
| `boolean isAutoMirrored()` |  |
| `boolean isFilterBitmap()` |  |
| `boolean isProjected()` |  |
| `boolean isStateful()` |  |
| `final boolean isVisible()` |  |
| `void jumpToCurrentState()` |  |
| `void onBoundsChange(android.graphics.Rect)` |  |
| `boolean onLayoutDirectionChanged(int)` |  |
| `boolean onLevelChange(int)` |  |
| `boolean onStateChange(int[])` |  |
| `static int resolveOpacity(int, int)` |  |
| `void scheduleSelf(@NonNull Runnable, long)` |  |
| `abstract void setAlpha(@IntRange(from=0, to=255) int)` |  |
| `void setAutoMirrored(boolean)` |  |
| `void setBounds(int, int, int, int)` |  |
| `void setBounds(@NonNull android.graphics.Rect)` |  |
| `final void setCallback(@Nullable android.graphics.drawable.Drawable.Callback)` |  |
| `void setChangingConfigurations(int)` |  |
| `abstract void setColorFilter(@Nullable android.graphics.ColorFilter)` |  |
| `void setFilterBitmap(boolean)` |  |
| `void setHotspot(float, float)` |  |
| `void setHotspotBounds(int, int, int, int)` |  |
| `final boolean setLayoutDirection(int)` |  |
| `final boolean setLevel(@IntRange(from=0, to=10000) int)` |  |
| `boolean setState(@NonNull int[])` |  |
| `void setTint(@ColorInt int)` |  |
| `void setTintBlendMode(@Nullable android.graphics.BlendMode)` |  |
| `void setTintList(@Nullable android.content.res.ColorStateList)` |  |
| `void setTintMode(@Nullable android.graphics.PorterDuff.Mode)` |  |
| `boolean setVisible(boolean, boolean)` |  |
| `void unscheduleSelf(@NonNull Runnable)` |  |

---

### `interface static Drawable.Callback`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Drawable.ConstantState()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void invalidateDrawable(@NonNull android.graphics.drawable.Drawable)` |  |
| `void scheduleDrawable(@NonNull android.graphics.drawable.Drawable, @NonNull Runnable, long)` |  |
| `void unscheduleDrawable(@NonNull android.graphics.drawable.Drawable, @NonNull Runnable)` |  |
| `boolean canApplyTheme()` |  |
| `abstract int getChangingConfigurations()` |  |

---

### `class DrawableContainer`

- **继承：** `android.graphics.drawable.Drawable`
- **实现：** `android.graphics.drawable.Drawable.Callback`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DrawableContainer()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void draw(android.graphics.Canvas)` |  |
| `int getOpacity()` |  |
| `void invalidateDrawable(@NonNull android.graphics.drawable.Drawable)` |  |
| `void scheduleDrawable(@NonNull android.graphics.drawable.Drawable, @NonNull Runnable, long)` |  |
| `boolean selectDrawable(int)` |  |
| `void setAlpha(int)` |  |
| `void setColorFilter(android.graphics.ColorFilter)` |  |
| `void setConstantState(android.graphics.drawable.DrawableContainer.DrawableContainerState)` |  |
| `void setDither(boolean)` |  |
| `void setEnterFadeDuration(int)` |  |
| `void setExitFadeDuration(int)` |  |
| `void unscheduleDrawable(@NonNull android.graphics.drawable.Drawable, @NonNull Runnable)` |  |
| `final int addChild(android.graphics.drawable.Drawable)` |  |
| `boolean canConstantState()` |  |
| `void computeConstantSize()` |  |
| `int getChangingConfigurations()` |  |
| `final android.graphics.drawable.Drawable getChild(int)` |  |
| `final int getChildCount()` |  |
| `final android.graphics.drawable.Drawable[] getChildren()` |  |
| `final int getConstantHeight()` |  |
| `final int getConstantMinimumHeight()` |  |
| `final int getConstantMinimumWidth()` |  |
| `final android.graphics.Rect getConstantPadding()` |  |
| `final int getConstantWidth()` |  |
| `final int getEnterFadeDuration()` |  |
| `final int getExitFadeDuration()` |  |
| `final int getOpacity()` |  |
| `void growArray(int, int)` |  |
| `final boolean isConstantSize()` |  |
| `final boolean isStateful()` |  |
| `final void setConstantSize(boolean)` |  |
| `final void setEnterFadeDuration(int)` |  |
| `final void setExitFadeDuration(int)` |  |
| `final void setVariablePadding(boolean)` |  |

---

### `class abstract DrawableWrapper`

- **继承：** `android.graphics.drawable.Drawable`
- **实现：** `android.graphics.drawable.Drawable.Callback`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DrawableWrapper(@Nullable android.graphics.drawable.Drawable)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void draw(@NonNull android.graphics.Canvas)` |  |
| `int getOpacity()` |  |
| `void invalidateDrawable(@NonNull android.graphics.drawable.Drawable)` |  |
| `void scheduleDrawable(@NonNull android.graphics.drawable.Drawable, @NonNull Runnable, long)` |  |
| `void setAlpha(int)` |  |
| `void setColorFilter(@Nullable android.graphics.ColorFilter)` |  |
| `void setDrawable(@Nullable android.graphics.drawable.Drawable)` |  |
| `void unscheduleDrawable(@NonNull android.graphics.drawable.Drawable, @NonNull Runnable)` |  |

---

### `class GradientDrawable`

- **继承：** `android.graphics.drawable.Drawable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GradientDrawable()` |  |
| `GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation, @ColorInt int[])` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int LINE = 2` |  |
| `static final int LINEAR_GRADIENT = 0` |  |
| `static final int OVAL = 1` |  |
| `static final int RADIAL_GRADIENT = 1` |  |
| `static final int RECTANGLE = 0` |  |
| `static final int RING = 3` |  |
| `static final int SWEEP_GRADIENT = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void draw(android.graphics.Canvas)` |  |
| `float getCornerRadius()` |  |
| `float getGradientCenterX()` |  |
| `float getGradientCenterY()` |  |
| `float getGradientRadius()` |  |
| `int getGradientType()` |  |
| `float getInnerRadiusRatio()` |  |
| `int getOpacity()` |  |
| `android.graphics.drawable.GradientDrawable.Orientation getOrientation()` |  |
| `int getShape()` |  |
| `float getThicknessRatio()` |  |
| `boolean getUseLevel()` |  |
| `void setAlpha(int)` |  |
| `void setColor(@ColorInt int)` |  |
| `void setColor(@Nullable android.content.res.ColorStateList)` |  |
| `void setColorFilter(@Nullable android.graphics.ColorFilter)` |  |
| `void setColors(@ColorInt @Nullable int[])` |  |
| `void setColors(@ColorInt @Nullable int[], @Nullable float[])` |  |
| `void setCornerRadii(@Nullable float[])` |  |
| `void setCornerRadius(float)` |  |
| `void setDither(boolean)` |  |
| `void setGradientCenter(float, float)` |  |
| `void setGradientRadius(float)` |  |
| `void setGradientType(int)` |  |
| `void setInnerRadius(@Px int)` |  |
| `void setInnerRadiusRatio(@FloatRange(from=0.0f, fromInclusive=false) float)` |  |
| `void setOrientation(android.graphics.drawable.GradientDrawable.Orientation)` |  |
| `void setPadding(@Px int, @Px int, @Px int, @Px int)` |  |
| `void setShape(int)` |  |
| `void setSize(int, int)` |  |
| `void setStroke(int, @ColorInt int)` |  |
| `void setStroke(int, android.content.res.ColorStateList)` |  |
| `void setStroke(int, @ColorInt int, float, float)` |  |
| `void setStroke(int, android.content.res.ColorStateList, float, float)` |  |
| `void setThickness(@Px int)` |  |
| `void setThicknessRatio(@FloatRange(from=0.0f, fromInclusive=false) float)` |  |
| `void setUseLevel(boolean)` |  |

---

### `enum GradientDrawable.Orientation`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.graphics.drawable.GradientDrawable.Orientation BL_TR` |  |
| `static final android.graphics.drawable.GradientDrawable.Orientation BOTTOM_TOP` |  |
| `static final android.graphics.drawable.GradientDrawable.Orientation BR_TL` |  |
| `static final android.graphics.drawable.GradientDrawable.Orientation LEFT_RIGHT` |  |
| `static final android.graphics.drawable.GradientDrawable.Orientation RIGHT_LEFT` |  |
| `static final android.graphics.drawable.GradientDrawable.Orientation TL_BR` |  |
| `static final android.graphics.drawable.GradientDrawable.Orientation TOP_BOTTOM` |  |
| `static final android.graphics.drawable.GradientDrawable.Orientation TR_BL` |  |

---

### `class final Icon`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int TYPE_ADAPTIVE_BITMAP = 5` |  |
| `static final int TYPE_BITMAP = 1` |  |
| `static final int TYPE_DATA = 3` |  |
| `static final int TYPE_RESOURCE = 2` |  |
| `static final int TYPE_URI = 4` |  |
| `static final int TYPE_URI_ADAPTIVE_BITMAP = 6` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.graphics.drawable.Icon createWithAdaptiveBitmap(android.graphics.Bitmap)` |  |
| `static android.graphics.drawable.Icon createWithBitmap(android.graphics.Bitmap)` |  |
| `static android.graphics.drawable.Icon createWithContentUri(String)` |  |
| `static android.graphics.drawable.Icon createWithContentUri(android.net.Uri)` |  |
| `static android.graphics.drawable.Icon createWithData(byte[], int, int)` |  |
| `static android.graphics.drawable.Icon createWithFilePath(String)` |  |
| `static android.graphics.drawable.Icon createWithResource(android.content.Context, @DrawableRes int)` |  |
| `static android.graphics.drawable.Icon createWithResource(String, @DrawableRes int)` |  |
| `int describeContents()` |  |
| `int getType()` |  |
| `android.graphics.drawable.Drawable loadDrawable(android.content.Context)` |  |
| `void loadDrawableAsync(android.content.Context, android.os.Message)` |  |
| `void loadDrawableAsync(android.content.Context, android.graphics.drawable.Icon.OnDrawableLoadedListener, android.os.Handler)` |  |
| `android.graphics.drawable.Icon setTint(@ColorInt int)` |  |
| `android.graphics.drawable.Icon setTintList(android.content.res.ColorStateList)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface static Icon.OnDrawableLoadedListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onDrawableLoaded(android.graphics.drawable.Drawable)` |  |

---

### `class InsetDrawable`

- **继承：** `android.graphics.drawable.DrawableWrapper`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `InsetDrawable(@Nullable android.graphics.drawable.Drawable, int)` |  |
| `InsetDrawable(@Nullable android.graphics.drawable.Drawable, float)` |  |
| `InsetDrawable(@Nullable android.graphics.drawable.Drawable, int, int, int, int)` |  |
| `InsetDrawable(@Nullable android.graphics.drawable.Drawable, float, float, float, float)` |  |

---

### `class LayerDrawable`

- **继承：** `android.graphics.drawable.Drawable`
- **实现：** `android.graphics.drawable.Drawable.Callback`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LayerDrawable(@NonNull android.graphics.drawable.Drawable[])` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int INSET_UNDEFINED = -2147483648` |  |
| `static final int PADDING_MODE_NEST = 0` |  |
| `static final int PADDING_MODE_STACK = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int addLayer(android.graphics.drawable.Drawable)` |  |
| `void draw(android.graphics.Canvas)` |  |
| `android.graphics.drawable.Drawable findDrawableByLayerId(int)` |  |
| `int findIndexByLayerId(int)` |  |
| `int getBottomPadding()` |  |
| `android.graphics.drawable.Drawable getDrawable(int)` |  |
| `int getEndPadding()` |  |
| `int getId(int)` |  |
| `int getLayerGravity(int)` |  |
| `int getLayerHeight(int)` |  |
| `int getLayerInsetBottom(int)` |  |
| `int getLayerInsetEnd(int)` |  |
| `int getLayerInsetLeft(int)` |  |
| `int getLayerInsetRight(int)` |  |
| `int getLayerInsetStart(int)` |  |
| `int getLayerInsetTop(int)` |  |
| `int getLayerWidth(int)` |  |
| `int getLeftPadding()` |  |
| `int getNumberOfLayers()` |  |
| `int getOpacity()` |  |
| `int getPaddingMode()` |  |
| `int getRightPadding()` |  |
| `int getStartPadding()` |  |
| `int getTopPadding()` |  |
| `void invalidateDrawable(@NonNull android.graphics.drawable.Drawable)` |  |
| `void scheduleDrawable(@NonNull android.graphics.drawable.Drawable, @NonNull Runnable, long)` |  |
| `void setAlpha(int)` |  |
| `void setColorFilter(android.graphics.ColorFilter)` |  |
| `void setDither(boolean)` |  |
| `void setDrawable(int, android.graphics.drawable.Drawable)` |  |
| `boolean setDrawableByLayerId(int, android.graphics.drawable.Drawable)` |  |
| `void setId(int, int)` |  |
| `void setLayerGravity(int, int)` |  |
| `void setLayerHeight(int, int)` |  |
| `void setLayerInset(int, int, int, int, int)` |  |
| `void setLayerInsetBottom(int, int)` |  |
| `void setLayerInsetEnd(int, int)` |  |
| `void setLayerInsetLeft(int, int)` |  |
| `void setLayerInsetRelative(int, int, int, int, int)` |  |
| `void setLayerInsetRight(int, int)` |  |
| `void setLayerInsetStart(int, int)` |  |
| `void setLayerInsetTop(int, int)` |  |
| `void setLayerSize(int, int, int)` |  |
| `void setLayerWidth(int, int)` |  |
| `void setOpacity(int)` |  |
| `void setPadding(int, int, int, int)` |  |
| `void setPaddingMode(int)` |  |
| `void setPaddingRelative(int, int, int, int)` |  |
| `void unscheduleDrawable(@NonNull android.graphics.drawable.Drawable, @NonNull Runnable)` |  |

---

### `class LevelListDrawable`

- **继承：** `android.graphics.drawable.DrawableContainer`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LevelListDrawable()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addLevel(int, int, android.graphics.drawable.Drawable)` |  |

---

### `class NinePatchDrawable`

- **继承：** `android.graphics.drawable.Drawable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NinePatchDrawable(android.content.res.Resources, android.graphics.Bitmap, byte[], android.graphics.Rect, String)` |  |
| `NinePatchDrawable(@Nullable android.content.res.Resources, @NonNull android.graphics.NinePatch)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void draw(android.graphics.Canvas)` |  |
| `int getOpacity()` |  |
| `void setAlpha(int)` |  |
| `void setColorFilter(@Nullable android.graphics.ColorFilter)` |  |
| `void setDither(boolean)` |  |
| `void setTargetDensity(@NonNull android.graphics.Canvas)` |  |
| `void setTargetDensity(@NonNull android.util.DisplayMetrics)` |  |
| `void setTargetDensity(int)` |  |

---

### `class PaintDrawable`

- **继承：** `android.graphics.drawable.ShapeDrawable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PaintDrawable()` |  |
| `PaintDrawable(int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void setCornerRadii(float[])` |  |
| `void setCornerRadius(float)` |  |

---

### `class PictureDrawable`

- **继承：** `android.graphics.drawable.Drawable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PictureDrawable(android.graphics.Picture)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void draw(android.graphics.Canvas)` |  |
| `int getOpacity()` |  |
| `android.graphics.Picture getPicture()` |  |
| `void setAlpha(int)` |  |
| `void setColorFilter(android.graphics.ColorFilter)` |  |
| `void setPicture(android.graphics.Picture)` |  |

---

### `class RippleDrawable`

- **继承：** `android.graphics.drawable.LayerDrawable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RippleDrawable(@NonNull android.content.res.ColorStateList, @Nullable android.graphics.drawable.Drawable, @Nullable android.graphics.drawable.Drawable)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int RADIUS_AUTO = -1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getRadius()` |  |
| `void setColor(android.content.res.ColorStateList)` |  |
| `void setRadius(int)` |  |

---

### `class RotateDrawable`

- **继承：** `android.graphics.drawable.DrawableWrapper`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RotateDrawable()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `float getFromDegrees()` |  |
| `float getPivotX()` |  |
| `float getPivotY()` |  |
| `float getToDegrees()` |  |
| `boolean isPivotXRelative()` |  |
| `boolean isPivotYRelative()` |  |
| `void setFromDegrees(float)` |  |
| `void setPivotX(float)` |  |
| `void setPivotXRelative(boolean)` |  |
| `void setPivotY(float)` |  |
| `void setPivotYRelative(boolean)` |  |
| `void setToDegrees(float)` |  |

---

### `class ScaleDrawable`

- **继承：** `android.graphics.drawable.DrawableWrapper`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ScaleDrawable(android.graphics.drawable.Drawable, int, float, float)` |  |

---

### `class ShapeDrawable`

- **继承：** `android.graphics.drawable.Drawable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ShapeDrawable()` |  |
| `ShapeDrawable(android.graphics.drawable.shapes.Shape)` |  |
| `ShapeDrawable.ShaderFactory()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void draw(android.graphics.Canvas)` |  |
| `int getOpacity()` |  |
| `android.graphics.Paint getPaint()` |  |
| `android.graphics.drawable.ShapeDrawable.ShaderFactory getShaderFactory()` |  |
| `android.graphics.drawable.shapes.Shape getShape()` |  |
| `boolean inflateTag(String, android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet)` |  |
| `void onDraw(android.graphics.drawable.shapes.Shape, android.graphics.Canvas, android.graphics.Paint)` |  |
| `void setAlpha(int)` |  |
| `void setColorFilter(android.graphics.ColorFilter)` |  |
| `void setDither(boolean)` |  |
| `void setIntrinsicHeight(int)` |  |
| `void setIntrinsicWidth(int)` |  |
| `void setPadding(int, int, int, int)` |  |
| `void setPadding(android.graphics.Rect)` |  |
| `void setShaderFactory(android.graphics.drawable.ShapeDrawable.ShaderFactory)` |  |
| `void setShape(android.graphics.drawable.shapes.Shape)` |  |
| `abstract android.graphics.Shader resize(int, int)` |  |

---

### `class StateListDrawable`

- **继承：** `android.graphics.drawable.DrawableContainer`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `StateListDrawable()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addState(int[], android.graphics.drawable.Drawable)` |  |
| `int findStateDrawableIndex(@NonNull int[])` |  |
| `int getStateCount()` |  |

---

### `class TransitionDrawable`

- **继承：** `android.graphics.drawable.LayerDrawable`
- **实现：** `android.graphics.drawable.Drawable.Callback`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TransitionDrawable(android.graphics.drawable.Drawable[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean isCrossFadeEnabled()` |  |
| `void resetTransition()` |  |
| `void reverseTransition(int)` |  |
| `void setCrossFadeEnabled(boolean)` |  |
| `void startTransition(int)` |  |

---

### `class VectorDrawable`

- **继承：** `android.graphics.drawable.Drawable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `VectorDrawable()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void draw(android.graphics.Canvas)` |  |
| `int getOpacity()` |  |
| `void setAlpha(int)` |  |
| `void setColorFilter(android.graphics.ColorFilter)` |  |

---

## Package: `android.graphics.drawable.shapes`

### `class ArcShape`

- **继承：** `android.graphics.drawable.shapes.RectShape`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ArcShape(float, float)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.drawable.shapes.ArcShape clone() throws java.lang.CloneNotSupportedException` |  |
| `final float getStartAngle()` |  |
| `final float getSweepAngle()` |  |

---

### `class OvalShape`

- **继承：** `android.graphics.drawable.shapes.RectShape`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `OvalShape()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.drawable.shapes.OvalShape clone() throws java.lang.CloneNotSupportedException` |  |

---

### `class PathShape`

- **继承：** `android.graphics.drawable.shapes.Shape`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PathShape(@NonNull android.graphics.Path, float, float)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.drawable.shapes.PathShape clone() throws java.lang.CloneNotSupportedException` |  |
| `void draw(android.graphics.Canvas, android.graphics.Paint)` |  |

---

### `class RectShape`

- **继承：** `android.graphics.drawable.shapes.Shape`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RectShape()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.drawable.shapes.RectShape clone() throws java.lang.CloneNotSupportedException` |  |
| `void draw(android.graphics.Canvas, android.graphics.Paint)` |  |
| `final android.graphics.RectF rect()` |  |

---

### `class RoundRectShape`

- **继承：** `android.graphics.drawable.shapes.RectShape`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RoundRectShape(@Nullable float[], @Nullable android.graphics.RectF, @Nullable float[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.drawable.shapes.RoundRectShape clone() throws java.lang.CloneNotSupportedException` |  |

---

### `class abstract Shape`

- **实现：** `java.lang.Cloneable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Shape()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.drawable.shapes.Shape clone() throws java.lang.CloneNotSupportedException` |  |
| `abstract void draw(android.graphics.Canvas, android.graphics.Paint)` |  |
| `final float getHeight()` |  |
| `void getOutline(@NonNull android.graphics.Outline)` |  |
| `final float getWidth()` |  |
| `boolean hasAlpha()` |  |
| `void onResize(float, float)` |  |
| `final void resize(float, float)` |  |

---

## Package: `android.graphics.fonts`

### `class final Font`


---

### `class static final Font.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Font.Builder(@NonNull java.nio.ByteBuffer)` |  |
| `Font.Builder(@NonNull java.io.File)` |  |
| `Font.Builder(@NonNull android.os.ParcelFileDescriptor)` |  |
| `Font.Builder(@NonNull android.os.ParcelFileDescriptor, @IntRange(from=0) long, @IntRange(from=0xffffffff) long)` |  |
| `Font.Builder(@NonNull android.content.res.AssetManager, @NonNull String)` |  |
| `Font.Builder(@NonNull android.content.res.Resources, int)` |  |

---

### `class final FontFamily`


---

### `class static final FontFamily.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `FontFamily.Builder(@NonNull android.graphics.fonts.Font)` |  |

---

### `class final FontStyle`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `FontStyle()` |  |
| `FontStyle(int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int FONT_SLANT_ITALIC = 1` |  |
| `static final int FONT_SLANT_UPRIGHT = 0` |  |
| `static final int FONT_WEIGHT_BLACK = 900` |  |
| `static final int FONT_WEIGHT_BOLD = 700` |  |
| `static final int FONT_WEIGHT_EXTRA_BOLD = 800` |  |
| `static final int FONT_WEIGHT_EXTRA_LIGHT = 200` |  |
| `static final int FONT_WEIGHT_LIGHT = 300` |  |
| `static final int FONT_WEIGHT_MAX = 1000` |  |
| `static final int FONT_WEIGHT_MEDIUM = 500` |  |
| `static final int FONT_WEIGHT_MIN = 1` |  |
| `static final int FONT_WEIGHT_NORMAL = 400` |  |
| `static final int FONT_WEIGHT_SEMI_BOLD = 600` |  |
| `static final int FONT_WEIGHT_THIN = 100` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getSlant()` |  |

---

### `class final FontVariationAxis`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `FontVariationAxis(@NonNull String, float)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `float getStyleValue()` |  |
| `String getTag()` |  |

---

### `class final SystemFonts`


---

## Package: `android.graphics.pdf`

### `class PdfDocument`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PdfDocument()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `void finishPage(android.graphics.pdf.PdfDocument.Page)` |  |
| `java.util.List<android.graphics.pdf.PdfDocument.PageInfo> getPages()` |  |
| `android.graphics.pdf.PdfDocument.Page startPage(android.graphics.pdf.PdfDocument.PageInfo)` |  |
| `void writeTo(java.io.OutputStream) throws java.io.IOException` |  |

---

### `class static final PdfDocument.Page`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.Canvas getCanvas()` |  |
| `android.graphics.pdf.PdfDocument.PageInfo getInfo()` |  |

---

### `class static final PdfDocument.PageInfo`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.Rect getContentRect()` |  |
| `int getPageHeight()` |  |
| `int getPageNumber()` |  |
| `int getPageWidth()` |  |

---

### `class static final PdfDocument.PageInfo.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PdfDocument.PageInfo.Builder(int, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.graphics.pdf.PdfDocument.PageInfo create()` |  |
| `android.graphics.pdf.PdfDocument.PageInfo.Builder setContentRect(android.graphics.Rect)` |  |

---

### `class final PdfRenderer`

- **实现：** `java.lang.AutoCloseable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `PdfRenderer(@NonNull android.os.ParcelFileDescriptor) throws java.io.IOException` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `int getPageCount()` |  |
| `android.graphics.pdf.PdfRenderer.Page openPage(int)` |  |
| `boolean shouldScaleForPrinting()` |  |

---

### `class final PdfRenderer.Page`

- **实现：** `java.lang.AutoCloseable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int RENDER_MODE_FOR_DISPLAY = 1` |  |
| `static final int RENDER_MODE_FOR_PRINT = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `int getHeight()` |  |
| `int getIndex()` |  |
| `int getWidth()` |  |
| `void render(@NonNull android.graphics.Bitmap, @Nullable android.graphics.Rect, @Nullable android.graphics.Matrix, int)` |  |

---

## Package: `android.graphics.text`

### `class LineBreaker`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BREAK_STRATEGY_BALANCED = 2` |  |
| `static final int BREAK_STRATEGY_HIGH_QUALITY = 1` |  |
| `static final int BREAK_STRATEGY_SIMPLE = 0` |  |
| `static final int HYPHENATION_FREQUENCY_FULL = 2` |  |
| `static final int HYPHENATION_FREQUENCY_NONE = 0` |  |
| `static final int HYPHENATION_FREQUENCY_NORMAL = 1` |  |
| `static final int JUSTIFICATION_MODE_INTER_WORD = 1` |  |
| `static final int JUSTIFICATION_MODE_NONE = 0` |  |

---

### `class static final LineBreaker.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LineBreaker.Builder()` |  |

---

### `class static LineBreaker.ParagraphConstraints`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LineBreaker.ParagraphConstraints()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void setIndent(@FloatRange(from=0.0f) @Px float, @IntRange(from=0) @Px int)` |  |
| `void setTabStops(@Nullable float[], @FloatRange(from=0) @Px float)` |  |
| `void setWidth(@FloatRange(from=0.0f) @Px float)` |  |

---

### `class static LineBreaker.Result`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getEndLineHyphenEdit(int)` |  |
| `int getStartLineHyphenEdit(int)` |  |
| `boolean hasLineTab(int)` |  |

---

### `class MeasuredText`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void getBounds(@IntRange(from=0) int, @IntRange(from=0) int, @NonNull android.graphics.Rect)` |  |

---

### `class static final MeasuredText.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MeasuredText.Builder(@NonNull char[])` |  |
| `MeasuredText.Builder(@NonNull android.graphics.text.MeasuredText)` |  |

---

## Package: `android.opengl`

### `class EGL14`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `EGL14()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int EGL_ALPHA_MASK_SIZE = 12350` |  |
| `static final int EGL_ALPHA_SIZE = 12321` |  |
| `static final int EGL_BACK_BUFFER = 12420` |  |
| `static final int EGL_BAD_ACCESS = 12290` |  |
| `static final int EGL_BAD_ALLOC = 12291` |  |
| `static final int EGL_BAD_ATTRIBUTE = 12292` |  |
| `static final int EGL_BAD_CONFIG = 12293` |  |
| `static final int EGL_BAD_CONTEXT = 12294` |  |
| `static final int EGL_BAD_CURRENT_SURFACE = 12295` |  |
| `static final int EGL_BAD_DISPLAY = 12296` |  |
| `static final int EGL_BAD_MATCH = 12297` |  |
| `static final int EGL_BAD_NATIVE_PIXMAP = 12298` |  |
| `static final int EGL_BAD_NATIVE_WINDOW = 12299` |  |
| `static final int EGL_BAD_PARAMETER = 12300` |  |
| `static final int EGL_BAD_SURFACE = 12301` |  |
| `static final int EGL_BIND_TO_TEXTURE_RGB = 12345` |  |
| `static final int EGL_BIND_TO_TEXTURE_RGBA = 12346` |  |
| `static final int EGL_BLUE_SIZE = 12322` |  |
| `static final int EGL_BUFFER_DESTROYED = 12437` |  |
| `static final int EGL_BUFFER_PRESERVED = 12436` |  |
| `static final int EGL_BUFFER_SIZE = 12320` |  |
| `static final int EGL_CLIENT_APIS = 12429` |  |
| `static final int EGL_COLOR_BUFFER_TYPE = 12351` |  |
| `static final int EGL_CONFIG_CAVEAT = 12327` |  |
| `static final int EGL_CONFIG_ID = 12328` |  |
| `static final int EGL_CONFORMANT = 12354` |  |
| `static final int EGL_CONTEXT_CLIENT_TYPE = 12439` |  |
| `static final int EGL_CONTEXT_CLIENT_VERSION = 12440` |  |
| `static final int EGL_CONTEXT_LOST = 12302` |  |
| `static final int EGL_CORE_NATIVE_ENGINE = 12379` |  |
| `static final int EGL_DEFAULT_DISPLAY = 0` |  |
| `static final int EGL_DEPTH_SIZE = 12325` |  |
| `static final int EGL_DISPLAY_SCALING = 10000` |  |
| `static final int EGL_DRAW = 12377` |  |
| `static final int EGL_EXTENSIONS = 12373` |  |
| `static final int EGL_FALSE = 0` |  |
| `static final int EGL_GREEN_SIZE = 12323` |  |
| `static final int EGL_HEIGHT = 12374` |  |
| `static final int EGL_HORIZONTAL_RESOLUTION = 12432` |  |
| `static final int EGL_LARGEST_PBUFFER = 12376` |  |
| `static final int EGL_LEVEL = 12329` |  |
| `static final int EGL_LUMINANCE_BUFFER = 12431` |  |
| `static final int EGL_LUMINANCE_SIZE = 12349` |  |
| `static final int EGL_MATCH_NATIVE_PIXMAP = 12353` |  |
| `static final int EGL_MAX_PBUFFER_HEIGHT = 12330` |  |
| `static final int EGL_MAX_PBUFFER_PIXELS = 12331` |  |
| `static final int EGL_MAX_PBUFFER_WIDTH = 12332` |  |
| `static final int EGL_MAX_SWAP_INTERVAL = 12348` |  |
| `static final int EGL_MIN_SWAP_INTERVAL = 12347` |  |
| `static final int EGL_MIPMAP_LEVEL = 12419` |  |
| `static final int EGL_MIPMAP_TEXTURE = 12418` |  |
| `static final int EGL_MULTISAMPLE_RESOLVE = 12441` |  |
| `static final int EGL_MULTISAMPLE_RESOLVE_BOX = 12443` |  |
| `static final int EGL_MULTISAMPLE_RESOLVE_BOX_BIT = 512` |  |
| `static final int EGL_MULTISAMPLE_RESOLVE_DEFAULT = 12442` |  |
| `static final int EGL_NATIVE_RENDERABLE = 12333` |  |
| `static final int EGL_NATIVE_VISUAL_ID = 12334` |  |
| `static final int EGL_NATIVE_VISUAL_TYPE = 12335` |  |
| `static final int EGL_NONE = 12344` |  |
| `static final int EGL_NON_CONFORMANT_CONFIG = 12369` |  |
| `static final int EGL_NOT_INITIALIZED = 12289` |  |
| `static android.opengl.EGLContext EGL_NO_CONTEXT` |  |
| `static android.opengl.EGLDisplay EGL_NO_DISPLAY` |  |
| `static android.opengl.EGLSurface EGL_NO_SURFACE` |  |
| `static final int EGL_NO_TEXTURE = 12380` |  |
| `static final int EGL_OPENGL_API = 12450` |  |
| `static final int EGL_OPENGL_BIT = 8` |  |
| `static final int EGL_OPENGL_ES2_BIT = 4` |  |
| `static final int EGL_OPENGL_ES_API = 12448` |  |
| `static final int EGL_OPENGL_ES_BIT = 1` |  |
| `static final int EGL_OPENVG_API = 12449` |  |
| `static final int EGL_OPENVG_BIT = 2` |  |
| `static final int EGL_OPENVG_IMAGE = 12438` |  |
| `static final int EGL_PBUFFER_BIT = 1` |  |
| `static final int EGL_PIXEL_ASPECT_RATIO = 12434` |  |
| `static final int EGL_PIXMAP_BIT = 2` |  |
| `static final int EGL_READ = 12378` |  |
| `static final int EGL_RED_SIZE = 12324` |  |
| `static final int EGL_RENDERABLE_TYPE = 12352` |  |
| `static final int EGL_RENDER_BUFFER = 12422` |  |
| `static final int EGL_RGB_BUFFER = 12430` |  |
| `static final int EGL_SAMPLES = 12337` |  |
| `static final int EGL_SAMPLE_BUFFERS = 12338` |  |
| `static final int EGL_SINGLE_BUFFER = 12421` |  |
| `static final int EGL_SLOW_CONFIG = 12368` |  |
| `static final int EGL_STENCIL_SIZE = 12326` |  |
| `static final int EGL_SUCCESS = 12288` |  |
| `static final int EGL_SURFACE_TYPE = 12339` |  |
| `static final int EGL_SWAP_BEHAVIOR = 12435` |  |
| `static final int EGL_SWAP_BEHAVIOR_PRESERVED_BIT = 1024` |  |
| `static final int EGL_TEXTURE_2D = 12383` |  |
| `static final int EGL_TEXTURE_FORMAT = 12416` |  |
| `static final int EGL_TEXTURE_RGB = 12381` |  |
| `static final int EGL_TEXTURE_RGBA = 12382` |  |
| `static final int EGL_TEXTURE_TARGET = 12417` |  |
| `static final int EGL_TRANSPARENT_BLUE_VALUE = 12341` |  |
| `static final int EGL_TRANSPARENT_GREEN_VALUE = 12342` |  |
| `static final int EGL_TRANSPARENT_RED_VALUE = 12343` |  |
| `static final int EGL_TRANSPARENT_RGB = 12370` |  |
| `static final int EGL_TRANSPARENT_TYPE = 12340` |  |
| `static final int EGL_TRUE = 1` |  |
| `static final int EGL_VENDOR = 12371` |  |
| `static final int EGL_VERSION = 12372` |  |
| `static final int EGL_VERTICAL_RESOLUTION = 12433` |  |
| `static final int EGL_VG_ALPHA_FORMAT = 12424` |  |
| `static final int EGL_VG_ALPHA_FORMAT_NONPRE = 12427` |  |
| `static final int EGL_VG_ALPHA_FORMAT_PRE = 12428` |  |
| `static final int EGL_VG_ALPHA_FORMAT_PRE_BIT = 64` |  |
| `static final int EGL_VG_COLORSPACE = 12423` |  |
| `static final int EGL_VG_COLORSPACE_LINEAR = 12426` |  |
| `static final int EGL_VG_COLORSPACE_LINEAR_BIT = 32` |  |
| `static final int EGL_VG_COLORSPACE_sRGB = 12425` |  |
| `static final int EGL_WIDTH = 12375` |  |
| `static final int EGL_WINDOW_BIT = 4` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean eglBindAPI(int)` |  |
| `static boolean eglBindTexImage(android.opengl.EGLDisplay, android.opengl.EGLSurface, int)` |  |
| `static boolean eglChooseConfig(android.opengl.EGLDisplay, int[], int, android.opengl.EGLConfig[], int, int, int[], int)` |  |
| `static boolean eglCopyBuffers(android.opengl.EGLDisplay, android.opengl.EGLSurface, int)` |  |
| `static android.opengl.EGLContext eglCreateContext(android.opengl.EGLDisplay, android.opengl.EGLConfig, android.opengl.EGLContext, int[], int)` |  |
| `static android.opengl.EGLSurface eglCreatePbufferFromClientBuffer(android.opengl.EGLDisplay, int, int, android.opengl.EGLConfig, int[], int)` |  |
| `static android.opengl.EGLSurface eglCreatePbufferSurface(android.opengl.EGLDisplay, android.opengl.EGLConfig, int[], int)` |  |
| `static android.opengl.EGLSurface eglCreateWindowSurface(android.opengl.EGLDisplay, android.opengl.EGLConfig, Object, int[], int)` |  |
| `static boolean eglDestroyContext(android.opengl.EGLDisplay, android.opengl.EGLContext)` |  |
| `static boolean eglDestroySurface(android.opengl.EGLDisplay, android.opengl.EGLSurface)` |  |
| `static boolean eglGetConfigAttrib(android.opengl.EGLDisplay, android.opengl.EGLConfig, int, int[], int)` |  |
| `static boolean eglGetConfigs(android.opengl.EGLDisplay, android.opengl.EGLConfig[], int, int, int[], int)` |  |
| `static android.opengl.EGLContext eglGetCurrentContext()` |  |
| `static android.opengl.EGLDisplay eglGetCurrentDisplay()` |  |
| `static android.opengl.EGLSurface eglGetCurrentSurface(int)` |  |
| `static android.opengl.EGLDisplay eglGetDisplay(int)` |  |
| `static int eglGetError()` |  |
| `static boolean eglInitialize(android.opengl.EGLDisplay, int[], int, int[], int)` |  |
| `static boolean eglMakeCurrent(android.opengl.EGLDisplay, android.opengl.EGLSurface, android.opengl.EGLSurface, android.opengl.EGLContext)` |  |
| `static int eglQueryAPI()` |  |
| `static boolean eglQueryContext(android.opengl.EGLDisplay, android.opengl.EGLContext, int, int[], int)` |  |
| `static String eglQueryString(android.opengl.EGLDisplay, int)` |  |
| `static boolean eglQuerySurface(android.opengl.EGLDisplay, android.opengl.EGLSurface, int, int[], int)` |  |
| `static boolean eglReleaseTexImage(android.opengl.EGLDisplay, android.opengl.EGLSurface, int)` |  |
| `static boolean eglReleaseThread()` |  |
| `static boolean eglSurfaceAttrib(android.opengl.EGLDisplay, android.opengl.EGLSurface, int, int)` |  |
| `static boolean eglSwapBuffers(android.opengl.EGLDisplay, android.opengl.EGLSurface)` |  |
| `static boolean eglSwapInterval(android.opengl.EGLDisplay, int)` |  |
| `static boolean eglTerminate(android.opengl.EGLDisplay)` |  |
| `static boolean eglWaitClient()` |  |
| `static boolean eglWaitGL()` |  |
| `static boolean eglWaitNative(int)` |  |

---

### `class final EGL15`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int EGL_CL_EVENT_HANDLE = 12444` |  |
| `static final int EGL_CONDITION_SATISFIED = 12534` |  |
| `static final int EGL_CONTEXT_MAJOR_VERSION = 12440` |  |
| `static final int EGL_CONTEXT_MINOR_VERSION = 12539` |  |
| `static final int EGL_CONTEXT_OPENGL_COMPATIBILITY_PROFILE_BIT = 2` |  |
| `static final int EGL_CONTEXT_OPENGL_CORE_PROFILE_BIT = 1` |  |
| `static final int EGL_CONTEXT_OPENGL_DEBUG = 12720` |  |
| `static final int EGL_CONTEXT_OPENGL_FORWARD_COMPATIBLE = 12721` |  |
| `static final int EGL_CONTEXT_OPENGL_PROFILE_MASK = 12541` |  |
| `static final int EGL_CONTEXT_OPENGL_RESET_NOTIFICATION_STRATEGY = 12733` |  |
| `static final int EGL_CONTEXT_OPENGL_ROBUST_ACCESS = 12722` |  |
| `static final long EGL_FOREVER = -1L` |  |
| `static final int EGL_GL_COLORSPACE = 12445` |  |
| `static final int EGL_GL_COLORSPACE_LINEAR = 12426` |  |
| `static final int EGL_GL_COLORSPACE_SRGB = 12425` |  |
| `static final int EGL_GL_RENDERBUFFER = 12473` |  |
| `static final int EGL_GL_TEXTURE_2D = 12465` |  |
| `static final int EGL_GL_TEXTURE_3D = 12466` |  |
| `static final int EGL_GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 12468` |  |
| `static final int EGL_GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 12470` |  |
| `static final int EGL_GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 12472` |  |
| `static final int EGL_GL_TEXTURE_CUBE_MAP_POSITIVE_X = 12467` |  |
| `static final int EGL_GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 12469` |  |
| `static final int EGL_GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 12471` |  |
| `static final int EGL_GL_TEXTURE_LEVEL = 12476` |  |
| `static final int EGL_GL_TEXTURE_ZOFFSET = 12477` |  |
| `static final int EGL_IMAGE_PRESERVED = 12498` |  |
| `static final int EGL_LOSE_CONTEXT_ON_RESET = 12735` |  |
| `static final android.opengl.EGLContext EGL_NO_CONTEXT` |  |
| `static final android.opengl.EGLDisplay EGL_NO_DISPLAY` |  |
| `static final android.opengl.EGLImage EGL_NO_IMAGE` |  |
| `static final int EGL_NO_RESET_NOTIFICATION = 12734` |  |
| `static final android.opengl.EGLSurface EGL_NO_SURFACE` |  |
| `static final android.opengl.EGLSync EGL_NO_SYNC` |  |
| `static final int EGL_OPENGL_ES3_BIT = 64` |  |
| `static final int EGL_PLATFORM_ANDROID_KHR = 12609` |  |
| `static final int EGL_SIGNALED = 12530` |  |
| `static final int EGL_SYNC_CL_EVENT = 12542` |  |
| `static final int EGL_SYNC_CL_EVENT_COMPLETE = 12543` |  |
| `static final int EGL_SYNC_CONDITION = 12536` |  |
| `static final int EGL_SYNC_FENCE = 12537` |  |
| `static final int EGL_SYNC_FLUSH_COMMANDS_BIT = 1` |  |
| `static final int EGL_SYNC_PRIOR_COMMANDS_COMPLETE = 12528` |  |
| `static final int EGL_SYNC_STATUS = 12529` |  |
| `static final int EGL_SYNC_TYPE = 12535` |  |
| `static final int EGL_TIMEOUT_EXPIRED = 12533` |  |
| `static final int EGL_UNSIGNALED = 12531` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static int eglClientWaitSync(android.opengl.EGLDisplay, android.opengl.EGLSync, int, long)` |  |
| `static android.opengl.EGLImage eglCreateImage(android.opengl.EGLDisplay, android.opengl.EGLContext, int, long, long[], int)` |  |
| `static android.opengl.EGLSurface eglCreatePlatformPixmapSurface(android.opengl.EGLDisplay, android.opengl.EGLConfig, java.nio.Buffer, long[], int)` |  |
| `static android.opengl.EGLSurface eglCreatePlatformWindowSurface(android.opengl.EGLDisplay, android.opengl.EGLConfig, java.nio.Buffer, long[], int)` |  |
| `static android.opengl.EGLSync eglCreateSync(android.opengl.EGLDisplay, int, long[], int)` |  |
| `static boolean eglDestroyImage(android.opengl.EGLDisplay, android.opengl.EGLImage)` |  |
| `static boolean eglDestroySync(android.opengl.EGLDisplay, android.opengl.EGLSync)` |  |
| `static android.opengl.EGLDisplay eglGetPlatformDisplay(int, long, long[], int)` |  |
| `static boolean eglGetSyncAttrib(android.opengl.EGLDisplay, android.opengl.EGLSync, int, long[], int)` |  |
| `static boolean eglWaitSync(android.opengl.EGLDisplay, android.opengl.EGLSync, int)` |  |

---

### `class EGLConfig`

- **继承：** `android.opengl.EGLObjectHandle`

---

### `class EGLContext`

- **继承：** `android.opengl.EGLObjectHandle`

---

### `class EGLDisplay`

- **继承：** `android.opengl.EGLObjectHandle`

---

### `class EGLExt`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `EGLExt()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int EGL_CONTEXT_FLAGS_KHR = 12540` |  |
| `static final int EGL_CONTEXT_MAJOR_VERSION_KHR = 12440` |  |
| `static final int EGL_CONTEXT_MINOR_VERSION_KHR = 12539` |  |
| `static final int EGL_OPENGL_ES3_BIT_KHR = 64` |  |
| `static final int EGL_RECORDABLE_ANDROID = 12610` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean eglPresentationTimeANDROID(android.opengl.EGLDisplay, android.opengl.EGLSurface, long)` |  |

---

### `class EGLImage`

- **继承：** `android.opengl.EGLObjectHandle`

---

### `class abstract EGLObjectHandle`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `EGLObjectHandle(long)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `long getNativeHandle()` |  |

---

### `class EGLSurface`

- **继承：** `android.opengl.EGLObjectHandle`

---

### `class EGLSync`

- **继承：** `android.opengl.EGLObjectHandle`

---

### `class ETC1`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ETC1()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DECODED_BLOCK_SIZE = 48` |  |
| `static final int ENCODED_BLOCK_SIZE = 8` |  |
| `static final int ETC1_RGB8_OES = 36196` |  |
| `static final int ETC_PKM_HEADER_SIZE = 16` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void decodeBlock(java.nio.Buffer, java.nio.Buffer)` |  |
| `static void decodeImage(java.nio.Buffer, java.nio.Buffer, int, int, int, int)` |  |
| `static void encodeBlock(java.nio.Buffer, int, java.nio.Buffer)` |  |
| `static void encodeImage(java.nio.Buffer, int, int, int, int, java.nio.Buffer)` |  |
| `static void formatHeader(java.nio.Buffer, int, int)` |  |
| `static int getEncodedDataSize(int, int)` |  |
| `static int getHeight(java.nio.Buffer)` |  |
| `static int getWidth(java.nio.Buffer)` |  |
| `static boolean isValid(java.nio.Buffer)` |  |

---

### `class ETC1Util`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ETC1Util()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.opengl.ETC1Util.ETC1Texture compressTexture(java.nio.Buffer, int, int, int, int)` |  |
| `static android.opengl.ETC1Util.ETC1Texture createTexture(java.io.InputStream) throws java.io.IOException` |  |
| `static boolean isETC1Supported()` |  |
| `static void loadTexture(int, int, int, int, int, java.io.InputStream) throws java.io.IOException` |  |
| `static void loadTexture(int, int, int, int, int, android.opengl.ETC1Util.ETC1Texture)` |  |
| `static void writeTexture(android.opengl.ETC1Util.ETC1Texture, java.io.OutputStream) throws java.io.IOException` |  |

---

### `class static ETC1Util.ETC1Texture`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ETC1Util.ETC1Texture(int, int, java.nio.ByteBuffer)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.nio.ByteBuffer getData()` |  |
| `int getHeight()` |  |
| `int getWidth()` |  |

---

### `class GLDebugHelper`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GLDebugHelper()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CONFIG_CHECK_GL_ERROR = 1` |  |
| `static final int CONFIG_CHECK_THREAD = 2` |  |
| `static final int CONFIG_LOG_ARGUMENT_NAMES = 4` |  |
| `static final int ERROR_WRONG_THREAD = 28672` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static javax.microedition.khronos.opengles.GL wrap(javax.microedition.khronos.opengles.GL, int, java.io.Writer)` |  |
| `static javax.microedition.khronos.egl.EGL wrap(javax.microedition.khronos.egl.EGL, int, java.io.Writer)` |  |

---

### `class GLES10`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GLES10()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int GL_ADD = 260` |  |
| `static final int GL_ALIASED_LINE_WIDTH_RANGE = 33902` |  |
| `static final int GL_ALIASED_POINT_SIZE_RANGE = 33901` |  |
| `static final int GL_ALPHA = 6406` |  |
| `static final int GL_ALPHA_BITS = 3413` |  |
| `static final int GL_ALPHA_TEST = 3008` |  |
| `static final int GL_ALWAYS = 519` |  |
| `static final int GL_AMBIENT = 4608` |  |
| `static final int GL_AMBIENT_AND_DIFFUSE = 5634` |  |
| `static final int GL_AND = 5377` |  |
| `static final int GL_AND_INVERTED = 5380` |  |
| `static final int GL_AND_REVERSE = 5378` |  |
| `static final int GL_BACK = 1029` |  |
| `static final int GL_BLEND = 3042` |  |
| `static final int GL_BLUE_BITS = 3412` |  |
| `static final int GL_BYTE = 5120` |  |
| `static final int GL_CCW = 2305` |  |
| `static final int GL_CLAMP_TO_EDGE = 33071` |  |
| `static final int GL_CLEAR = 5376` |  |
| `static final int GL_COLOR_ARRAY = 32886` |  |
| `static final int GL_COLOR_BUFFER_BIT = 16384` |  |
| `static final int GL_COLOR_LOGIC_OP = 3058` |  |
| `static final int GL_COLOR_MATERIAL = 2903` |  |
| `static final int GL_COMPRESSED_TEXTURE_FORMATS = 34467` |  |
| `static final int GL_CONSTANT_ATTENUATION = 4615` |  |
| `static final int GL_COPY = 5379` |  |
| `static final int GL_COPY_INVERTED = 5388` |  |
| `static final int GL_CULL_FACE = 2884` |  |
| `static final int GL_CW = 2304` |  |
| `static final int GL_DECAL = 8449` |  |
| `static final int GL_DECR = 7683` |  |
| `static final int GL_DEPTH_BITS = 3414` |  |
| `static final int GL_DEPTH_BUFFER_BIT = 256` |  |
| `static final int GL_DEPTH_TEST = 2929` |  |
| `static final int GL_DIFFUSE = 4609` |  |
| `static final int GL_DITHER = 3024` |  |
| `static final int GL_DONT_CARE = 4352` |  |
| `static final int GL_DST_ALPHA = 772` |  |
| `static final int GL_DST_COLOR = 774` |  |
| `static final int GL_EMISSION = 5632` |  |
| `static final int GL_EQUAL = 514` |  |
| `static final int GL_EQUIV = 5385` |  |
| `static final int GL_EXP = 2048` |  |
| `static final int GL_EXP2 = 2049` |  |
| `static final int GL_EXTENSIONS = 7939` |  |
| `static final int GL_FALSE = 0` |  |
| `static final int GL_FASTEST = 4353` |  |
| `static final int GL_FIXED = 5132` |  |
| `static final int GL_FLAT = 7424` |  |
| `static final int GL_FLOAT = 5126` |  |
| `static final int GL_FOG = 2912` |  |
| `static final int GL_FOG_COLOR = 2918` |  |
| `static final int GL_FOG_DENSITY = 2914` |  |
| `static final int GL_FOG_END = 2916` |  |
| `static final int GL_FOG_HINT = 3156` |  |
| `static final int GL_FOG_MODE = 2917` |  |
| `static final int GL_FOG_START = 2915` |  |
| `static final int GL_FRONT = 1028` |  |
| `static final int GL_FRONT_AND_BACK = 1032` |  |
| `static final int GL_GEQUAL = 518` |  |
| `static final int GL_GREATER = 516` |  |
| `static final int GL_GREEN_BITS = 3411` |  |
| `static final int GL_IMPLEMENTATION_COLOR_READ_FORMAT_OES = 35739` |  |
| `static final int GL_IMPLEMENTATION_COLOR_READ_TYPE_OES = 35738` |  |
| `static final int GL_INCR = 7682` |  |
| `static final int GL_INVALID_ENUM = 1280` |  |
| `static final int GL_INVALID_OPERATION = 1282` |  |
| `static final int GL_INVALID_VALUE = 1281` |  |
| `static final int GL_INVERT = 5386` |  |
| `static final int GL_KEEP = 7680` |  |
| `static final int GL_LEQUAL = 515` |  |
| `static final int GL_LESS = 513` |  |
| `static final int GL_LIGHT0 = 16384` |  |
| `static final int GL_LIGHT1 = 16385` |  |
| `static final int GL_LIGHT2 = 16386` |  |
| `static final int GL_LIGHT3 = 16387` |  |
| `static final int GL_LIGHT4 = 16388` |  |
| `static final int GL_LIGHT5 = 16389` |  |
| `static final int GL_LIGHT6 = 16390` |  |
| `static final int GL_LIGHT7 = 16391` |  |
| `static final int GL_LIGHTING = 2896` |  |
| `static final int GL_LIGHT_MODEL_AMBIENT = 2899` |  |
| `static final int GL_LIGHT_MODEL_TWO_SIDE = 2898` |  |
| `static final int GL_LINEAR = 9729` |  |
| `static final int GL_LINEAR_ATTENUATION = 4616` |  |
| `static final int GL_LINEAR_MIPMAP_LINEAR = 9987` |  |
| `static final int GL_LINEAR_MIPMAP_NEAREST = 9985` |  |
| `static final int GL_LINES = 1` |  |
| `static final int GL_LINE_LOOP = 2` |  |
| `static final int GL_LINE_SMOOTH = 2848` |  |
| `static final int GL_LINE_SMOOTH_HINT = 3154` |  |
| `static final int GL_LINE_STRIP = 3` |  |
| `static final int GL_LUMINANCE = 6409` |  |
| `static final int GL_LUMINANCE_ALPHA = 6410` |  |
| `static final int GL_MAX_ELEMENTS_INDICES = 33001` |  |
| `static final int GL_MAX_ELEMENTS_VERTICES = 33000` |  |
| `static final int GL_MAX_LIGHTS = 3377` |  |
| `static final int GL_MAX_MODELVIEW_STACK_DEPTH = 3382` |  |
| `static final int GL_MAX_PROJECTION_STACK_DEPTH = 3384` |  |
| `static final int GL_MAX_TEXTURE_SIZE = 3379` |  |
| `static final int GL_MAX_TEXTURE_STACK_DEPTH = 3385` |  |
| `static final int GL_MAX_TEXTURE_UNITS = 34018` |  |
| `static final int GL_MAX_VIEWPORT_DIMS = 3386` |  |
| `static final int GL_MODELVIEW = 5888` |  |
| `static final int GL_MODULATE = 8448` |  |
| `static final int GL_MULTISAMPLE = 32925` |  |
| `static final int GL_NAND = 5390` |  |
| `static final int GL_NEAREST = 9728` |  |
| `static final int GL_NEAREST_MIPMAP_LINEAR = 9986` |  |
| `static final int GL_NEAREST_MIPMAP_NEAREST = 9984` |  |
| `static final int GL_NEVER = 512` |  |
| `static final int GL_NICEST = 4354` |  |
| `static final int GL_NOOP = 5381` |  |
| `static final int GL_NOR = 5384` |  |
| `static final int GL_NORMALIZE = 2977` |  |
| `static final int GL_NORMAL_ARRAY = 32885` |  |
| `static final int GL_NOTEQUAL = 517` |  |
| `static final int GL_NO_ERROR = 0` |  |
| `static final int GL_NUM_COMPRESSED_TEXTURE_FORMATS = 34466` |  |
| `static final int GL_ONE = 1` |  |
| `static final int GL_ONE_MINUS_DST_ALPHA = 773` |  |
| `static final int GL_ONE_MINUS_DST_COLOR = 775` |  |
| `static final int GL_ONE_MINUS_SRC_ALPHA = 771` |  |
| `static final int GL_ONE_MINUS_SRC_COLOR = 769` |  |
| `static final int GL_OR = 5383` |  |
| `static final int GL_OR_INVERTED = 5389` |  |
| `static final int GL_OR_REVERSE = 5387` |  |
| `static final int GL_OUT_OF_MEMORY = 1285` |  |
| `static final int GL_PACK_ALIGNMENT = 3333` |  |
| `static final int GL_PALETTE4_R5_G6_B5_OES = 35730` |  |
| `static final int GL_PALETTE4_RGB5_A1_OES = 35732` |  |
| `static final int GL_PALETTE4_RGB8_OES = 35728` |  |
| `static final int GL_PALETTE4_RGBA4_OES = 35731` |  |
| `static final int GL_PALETTE4_RGBA8_OES = 35729` |  |
| `static final int GL_PALETTE8_R5_G6_B5_OES = 35735` |  |
| `static final int GL_PALETTE8_RGB5_A1_OES = 35737` |  |
| `static final int GL_PALETTE8_RGB8_OES = 35733` |  |
| `static final int GL_PALETTE8_RGBA4_OES = 35736` |  |
| `static final int GL_PALETTE8_RGBA8_OES = 35734` |  |
| `static final int GL_PERSPECTIVE_CORRECTION_HINT = 3152` |  |
| `static final int GL_POINTS = 0` |  |
| `static final int GL_POINT_FADE_THRESHOLD_SIZE = 33064` |  |
| `static final int GL_POINT_SIZE = 2833` |  |
| `static final int GL_POINT_SMOOTH = 2832` |  |
| `static final int GL_POINT_SMOOTH_HINT = 3153` |  |
| `static final int GL_POLYGON_OFFSET_FILL = 32823` |  |
| `static final int GL_POLYGON_SMOOTH_HINT = 3155` |  |
| `static final int GL_POSITION = 4611` |  |
| `static final int GL_PROJECTION = 5889` |  |
| `static final int GL_QUADRATIC_ATTENUATION = 4617` |  |
| `static final int GL_RED_BITS = 3410` |  |
| `static final int GL_RENDERER = 7937` |  |
| `static final int GL_REPEAT = 10497` |  |
| `static final int GL_REPLACE = 7681` |  |
| `static final int GL_RESCALE_NORMAL = 32826` |  |
| `static final int GL_RGB = 6407` |  |
| `static final int GL_RGBA = 6408` |  |
| `static final int GL_SAMPLE_ALPHA_TO_COVERAGE = 32926` |  |
| `static final int GL_SAMPLE_ALPHA_TO_ONE = 32927` |  |
| `static final int GL_SAMPLE_COVERAGE = 32928` |  |
| `static final int GL_SCISSOR_TEST = 3089` |  |
| `static final int GL_SET = 5391` |  |
| `static final int GL_SHININESS = 5633` |  |
| `static final int GL_SHORT = 5122` |  |
| `static final int GL_SMOOTH = 7425` |  |
| `static final int GL_SMOOTH_LINE_WIDTH_RANGE = 2850` |  |
| `static final int GL_SMOOTH_POINT_SIZE_RANGE = 2834` |  |
| `static final int GL_SPECULAR = 4610` |  |
| `static final int GL_SPOT_CUTOFF = 4614` |  |
| `static final int GL_SPOT_DIRECTION = 4612` |  |
| `static final int GL_SPOT_EXPONENT = 4613` |  |
| `static final int GL_SRC_ALPHA = 770` |  |
| `static final int GL_SRC_ALPHA_SATURATE = 776` |  |
| `static final int GL_SRC_COLOR = 768` |  |
| `static final int GL_STACK_OVERFLOW = 1283` |  |
| `static final int GL_STACK_UNDERFLOW = 1284` |  |
| `static final int GL_STENCIL_BITS = 3415` |  |
| `static final int GL_STENCIL_BUFFER_BIT = 1024` |  |
| `static final int GL_STENCIL_TEST = 2960` |  |
| `static final int GL_SUBPIXEL_BITS = 3408` |  |
| `static final int GL_TEXTURE = 5890` |  |
| `static final int GL_TEXTURE0 = 33984` |  |
| `static final int GL_TEXTURE1 = 33985` |  |
| `static final int GL_TEXTURE10 = 33994` |  |
| `static final int GL_TEXTURE11 = 33995` |  |
| `static final int GL_TEXTURE12 = 33996` |  |
| `static final int GL_TEXTURE13 = 33997` |  |
| `static final int GL_TEXTURE14 = 33998` |  |
| `static final int GL_TEXTURE15 = 33999` |  |
| `static final int GL_TEXTURE16 = 34000` |  |
| `static final int GL_TEXTURE17 = 34001` |  |
| `static final int GL_TEXTURE18 = 34002` |  |
| `static final int GL_TEXTURE19 = 34003` |  |
| `static final int GL_TEXTURE2 = 33986` |  |
| `static final int GL_TEXTURE20 = 34004` |  |
| `static final int GL_TEXTURE21 = 34005` |  |
| `static final int GL_TEXTURE22 = 34006` |  |
| `static final int GL_TEXTURE23 = 34007` |  |
| `static final int GL_TEXTURE24 = 34008` |  |
| `static final int GL_TEXTURE25 = 34009` |  |
| `static final int GL_TEXTURE26 = 34010` |  |
| `static final int GL_TEXTURE27 = 34011` |  |
| `static final int GL_TEXTURE28 = 34012` |  |
| `static final int GL_TEXTURE29 = 34013` |  |
| `static final int GL_TEXTURE3 = 33987` |  |
| `static final int GL_TEXTURE30 = 34014` |  |
| `static final int GL_TEXTURE31 = 34015` |  |
| `static final int GL_TEXTURE4 = 33988` |  |
| `static final int GL_TEXTURE5 = 33989` |  |
| `static final int GL_TEXTURE6 = 33990` |  |
| `static final int GL_TEXTURE7 = 33991` |  |
| `static final int GL_TEXTURE8 = 33992` |  |
| `static final int GL_TEXTURE9 = 33993` |  |
| `static final int GL_TEXTURE_2D = 3553` |  |
| `static final int GL_TEXTURE_COORD_ARRAY = 32888` |  |
| `static final int GL_TEXTURE_ENV = 8960` |  |
| `static final int GL_TEXTURE_ENV_COLOR = 8705` |  |
| `static final int GL_TEXTURE_ENV_MODE = 8704` |  |
| `static final int GL_TEXTURE_MAG_FILTER = 10240` |  |
| `static final int GL_TEXTURE_MIN_FILTER = 10241` |  |
| `static final int GL_TEXTURE_WRAP_S = 10242` |  |
| `static final int GL_TEXTURE_WRAP_T = 10243` |  |
| `static final int GL_TRIANGLES = 4` |  |
| `static final int GL_TRIANGLE_FAN = 6` |  |
| `static final int GL_TRIANGLE_STRIP = 5` |  |
| `static final int GL_TRUE = 1` |  |
| `static final int GL_UNPACK_ALIGNMENT = 3317` |  |
| `static final int GL_UNSIGNED_BYTE = 5121` |  |
| `static final int GL_UNSIGNED_SHORT = 5123` |  |
| `static final int GL_UNSIGNED_SHORT_4_4_4_4 = 32819` |  |
| `static final int GL_UNSIGNED_SHORT_5_5_5_1 = 32820` |  |
| `static final int GL_UNSIGNED_SHORT_5_6_5 = 33635` |  |
| `static final int GL_VENDOR = 7936` |  |
| `static final int GL_VERSION = 7938` |  |
| `static final int GL_VERTEX_ARRAY = 32884` |  |
| `static final int GL_XOR = 5382` |  |
| `static final int GL_ZERO = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void glActiveTexture(int)` |  |
| `static void glAlphaFunc(int, float)` |  |
| `static void glAlphaFuncx(int, int)` |  |
| `static void glBindTexture(int, int)` |  |
| `static void glBlendFunc(int, int)` |  |
| `static void glClear(int)` |  |
| `static void glClearColor(float, float, float, float)` |  |
| `static void glClearColorx(int, int, int, int)` |  |
| `static void glClearDepthf(float)` |  |
| `static void glClearDepthx(int)` |  |
| `static void glClearStencil(int)` |  |
| `static void glClientActiveTexture(int)` |  |
| `static void glColor4f(float, float, float, float)` |  |
| `static void glColor4x(int, int, int, int)` |  |
| `static void glColorMask(boolean, boolean, boolean, boolean)` |  |
| `static void glColorPointer(int, int, int, java.nio.Buffer)` |  |
| `static void glCompressedTexImage2D(int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glCompressedTexSubImage2D(int, int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glCopyTexImage2D(int, int, int, int, int, int, int, int)` |  |
| `static void glCopyTexSubImage2D(int, int, int, int, int, int, int, int)` |  |
| `static void glCullFace(int)` |  |
| `static void glDeleteTextures(int, int[], int)` |  |
| `static void glDeleteTextures(int, java.nio.IntBuffer)` |  |
| `static void glDepthFunc(int)` |  |
| `static void glDepthMask(boolean)` |  |
| `static void glDepthRangef(float, float)` |  |
| `static void glDepthRangex(int, int)` |  |
| `static void glDisable(int)` |  |
| `static void glDisableClientState(int)` |  |
| `static void glDrawArrays(int, int, int)` |  |
| `static void glDrawElements(int, int, int, java.nio.Buffer)` |  |
| `static void glEnable(int)` |  |
| `static void glEnableClientState(int)` |  |
| `static void glFinish()` |  |
| `static void glFlush()` |  |
| `static void glFogf(int, float)` |  |
| `static void glFogfv(int, float[], int)` |  |
| `static void glFogfv(int, java.nio.FloatBuffer)` |  |
| `static void glFogx(int, int)` |  |
| `static void glFogxv(int, int[], int)` |  |
| `static void glFogxv(int, java.nio.IntBuffer)` |  |
| `static void glFrontFace(int)` |  |
| `static void glFrustumf(float, float, float, float, float, float)` |  |
| `static void glFrustumx(int, int, int, int, int, int)` |  |
| `static void glGenTextures(int, int[], int)` |  |
| `static void glGenTextures(int, java.nio.IntBuffer)` |  |
| `static int glGetError()` |  |
| `static void glGetIntegerv(int, int[], int)` |  |
| `static void glGetIntegerv(int, java.nio.IntBuffer)` |  |
| `static String glGetString(int)` |  |
| `static void glHint(int, int)` |  |
| `static void glLightModelf(int, float)` |  |
| `static void glLightModelfv(int, float[], int)` |  |
| `static void glLightModelfv(int, java.nio.FloatBuffer)` |  |
| `static void glLightModelx(int, int)` |  |
| `static void glLightModelxv(int, int[], int)` |  |
| `static void glLightModelxv(int, java.nio.IntBuffer)` |  |
| `static void glLightf(int, int, float)` |  |
| `static void glLightfv(int, int, float[], int)` |  |
| `static void glLightfv(int, int, java.nio.FloatBuffer)` |  |
| `static void glLightx(int, int, int)` |  |
| `static void glLightxv(int, int, int[], int)` |  |
| `static void glLightxv(int, int, java.nio.IntBuffer)` |  |
| `static void glLineWidth(float)` |  |
| `static void glLineWidthx(int)` |  |
| `static void glLoadIdentity()` |  |
| `static void glLoadMatrixf(float[], int)` |  |
| `static void glLoadMatrixf(java.nio.FloatBuffer)` |  |
| `static void glLoadMatrixx(int[], int)` |  |
| `static void glLoadMatrixx(java.nio.IntBuffer)` |  |
| `static void glLogicOp(int)` |  |
| `static void glMaterialf(int, int, float)` |  |
| `static void glMaterialfv(int, int, float[], int)` |  |
| `static void glMaterialfv(int, int, java.nio.FloatBuffer)` |  |
| `static void glMaterialx(int, int, int)` |  |
| `static void glMaterialxv(int, int, int[], int)` |  |
| `static void glMaterialxv(int, int, java.nio.IntBuffer)` |  |
| `static void glMatrixMode(int)` |  |
| `static void glMultMatrixf(float[], int)` |  |
| `static void glMultMatrixf(java.nio.FloatBuffer)` |  |
| `static void glMultMatrixx(int[], int)` |  |
| `static void glMultMatrixx(java.nio.IntBuffer)` |  |
| `static void glMultiTexCoord4f(int, float, float, float, float)` |  |
| `static void glMultiTexCoord4x(int, int, int, int, int)` |  |
| `static void glNormal3f(float, float, float)` |  |
| `static void glNormal3x(int, int, int)` |  |
| `static void glNormalPointer(int, int, java.nio.Buffer)` |  |
| `static void glOrthof(float, float, float, float, float, float)` |  |
| `static void glOrthox(int, int, int, int, int, int)` |  |
| `static void glPixelStorei(int, int)` |  |
| `static void glPointSize(float)` |  |
| `static void glPointSizex(int)` |  |
| `static void glPolygonOffset(float, float)` |  |
| `static void glPolygonOffsetx(int, int)` |  |
| `static void glPopMatrix()` |  |
| `static void glPushMatrix()` |  |
| `static void glReadPixels(int, int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glRotatef(float, float, float, float)` |  |
| `static void glRotatex(int, int, int, int)` |  |
| `static void glSampleCoverage(float, boolean)` |  |
| `static void glSampleCoveragex(int, boolean)` |  |
| `static void glScalef(float, float, float)` |  |
| `static void glScalex(int, int, int)` |  |
| `static void glScissor(int, int, int, int)` |  |
| `static void glShadeModel(int)` |  |
| `static void glStencilFunc(int, int, int)` |  |
| `static void glStencilMask(int)` |  |
| `static void glStencilOp(int, int, int)` |  |
| `static void glTexCoordPointer(int, int, int, java.nio.Buffer)` |  |
| `static void glTexEnvf(int, int, float)` |  |
| `static void glTexEnvfv(int, int, float[], int)` |  |
| `static void glTexEnvfv(int, int, java.nio.FloatBuffer)` |  |
| `static void glTexEnvx(int, int, int)` |  |
| `static void glTexEnvxv(int, int, int[], int)` |  |
| `static void glTexEnvxv(int, int, java.nio.IntBuffer)` |  |
| `static void glTexImage2D(int, int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glTexParameterf(int, int, float)` |  |
| `static void glTexParameterx(int, int, int)` |  |
| `static void glTexSubImage2D(int, int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glTranslatef(float, float, float)` |  |
| `static void glTranslatex(int, int, int)` |  |
| `static void glVertexPointer(int, int, int, java.nio.Buffer)` |  |
| `static void glViewport(int, int, int, int)` |  |

---

### `class GLES10Ext`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GLES10Ext()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static int glQueryMatrixxOES(int[], int, int[], int)` |  |
| `static int glQueryMatrixxOES(java.nio.IntBuffer, java.nio.IntBuffer)` |  |

---

### `class GLES11`

- **继承：** `android.opengl.GLES10`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GLES11()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int GL_ACTIVE_TEXTURE = 34016` |  |
| `static final int GL_ADD_SIGNED = 34164` |  |
| `static final int GL_ALPHA_SCALE = 3356` |  |
| `static final int GL_ALPHA_TEST_FUNC = 3009` |  |
| `static final int GL_ALPHA_TEST_REF = 3010` |  |
| `static final int GL_ARRAY_BUFFER = 34962` |  |
| `static final int GL_ARRAY_BUFFER_BINDING = 34964` |  |
| `static final int GL_BLEND_DST = 3040` |  |
| `static final int GL_BLEND_SRC = 3041` |  |
| `static final int GL_BUFFER_ACCESS = 35003` |  |
| `static final int GL_BUFFER_SIZE = 34660` |  |
| `static final int GL_BUFFER_USAGE = 34661` |  |
| `static final int GL_CLIENT_ACTIVE_TEXTURE = 34017` |  |
| `static final int GL_CLIP_PLANE0 = 12288` |  |
| `static final int GL_CLIP_PLANE1 = 12289` |  |
| `static final int GL_CLIP_PLANE2 = 12290` |  |
| `static final int GL_CLIP_PLANE3 = 12291` |  |
| `static final int GL_CLIP_PLANE4 = 12292` |  |
| `static final int GL_CLIP_PLANE5 = 12293` |  |
| `static final int GL_COLOR_ARRAY_BUFFER_BINDING = 34968` |  |
| `static final int GL_COLOR_ARRAY_POINTER = 32912` |  |
| `static final int GL_COLOR_ARRAY_SIZE = 32897` |  |
| `static final int GL_COLOR_ARRAY_STRIDE = 32899` |  |
| `static final int GL_COLOR_ARRAY_TYPE = 32898` |  |
| `static final int GL_COLOR_CLEAR_VALUE = 3106` |  |
| `static final int GL_COLOR_WRITEMASK = 3107` |  |
| `static final int GL_COMBINE = 34160` |  |
| `static final int GL_COMBINE_ALPHA = 34162` |  |
| `static final int GL_COMBINE_RGB = 34161` |  |
| `static final int GL_CONSTANT = 34166` |  |
| `static final int GL_COORD_REPLACE_OES = 34914` |  |
| `static final int GL_CULL_FACE_MODE = 2885` |  |
| `static final int GL_CURRENT_COLOR = 2816` |  |
| `static final int GL_CURRENT_NORMAL = 2818` |  |
| `static final int GL_CURRENT_TEXTURE_COORDS = 2819` |  |
| `static final int GL_DEPTH_CLEAR_VALUE = 2931` |  |
| `static final int GL_DEPTH_FUNC = 2932` |  |
| `static final int GL_DEPTH_RANGE = 2928` |  |
| `static final int GL_DEPTH_WRITEMASK = 2930` |  |
| `static final int GL_DOT3_RGB = 34478` |  |
| `static final int GL_DOT3_RGBA = 34479` |  |
| `static final int GL_DYNAMIC_DRAW = 35048` |  |
| `static final int GL_ELEMENT_ARRAY_BUFFER = 34963` |  |
| `static final int GL_ELEMENT_ARRAY_BUFFER_BINDING = 34965` |  |
| `static final int GL_FRONT_FACE = 2886` |  |
| `static final int GL_GENERATE_MIPMAP = 33169` |  |
| `static final int GL_GENERATE_MIPMAP_HINT = 33170` |  |
| `static final int GL_INTERPOLATE = 34165` |  |
| `static final int GL_LINE_WIDTH = 2849` |  |
| `static final int GL_LOGIC_OP_MODE = 3056` |  |
| `static final int GL_MATRIX_MODE = 2976` |  |
| `static final int GL_MAX_CLIP_PLANES = 3378` |  |
| `static final int GL_MODELVIEW_MATRIX = 2982` |  |
| `static final int GL_MODELVIEW_MATRIX_FLOAT_AS_INT_BITS_OES = 35213` |  |
| `static final int GL_MODELVIEW_STACK_DEPTH = 2979` |  |
| `static final int GL_NORMAL_ARRAY_BUFFER_BINDING = 34967` |  |
| `static final int GL_NORMAL_ARRAY_POINTER = 32911` |  |
| `static final int GL_NORMAL_ARRAY_STRIDE = 32895` |  |
| `static final int GL_NORMAL_ARRAY_TYPE = 32894` |  |
| `static final int GL_OPERAND0_ALPHA = 34200` |  |
| `static final int GL_OPERAND0_RGB = 34192` |  |
| `static final int GL_OPERAND1_ALPHA = 34201` |  |
| `static final int GL_OPERAND1_RGB = 34193` |  |
| `static final int GL_OPERAND2_ALPHA = 34202` |  |
| `static final int GL_OPERAND2_RGB = 34194` |  |
| `static final int GL_POINT_DISTANCE_ATTENUATION = 33065` |  |
| `static final int GL_POINT_FADE_THRESHOLD_SIZE = 33064` |  |
| `static final int GL_POINT_SIZE = 2833` |  |
| `static final int GL_POINT_SIZE_ARRAY_BUFFER_BINDING_OES = 35743` |  |
| `static final int GL_POINT_SIZE_ARRAY_OES = 35740` |  |
| `static final int GL_POINT_SIZE_ARRAY_POINTER_OES = 35212` |  |
| `static final int GL_POINT_SIZE_ARRAY_STRIDE_OES = 35211` |  |
| `static final int GL_POINT_SIZE_ARRAY_TYPE_OES = 35210` |  |
| `static final int GL_POINT_SIZE_MAX = 33063` |  |
| `static final int GL_POINT_SIZE_MIN = 33062` |  |
| `static final int GL_POINT_SPRITE_OES = 34913` |  |
| `static final int GL_POLYGON_OFFSET_FACTOR = 32824` |  |
| `static final int GL_POLYGON_OFFSET_UNITS = 10752` |  |
| `static final int GL_PREVIOUS = 34168` |  |
| `static final int GL_PRIMARY_COLOR = 34167` |  |
| `static final int GL_PROJECTION_MATRIX = 2983` |  |
| `static final int GL_PROJECTION_MATRIX_FLOAT_AS_INT_BITS_OES = 35214` |  |
| `static final int GL_PROJECTION_STACK_DEPTH = 2980` |  |
| `static final int GL_RGB_SCALE = 34163` |  |
| `static final int GL_SAMPLES = 32937` |  |
| `static final int GL_SAMPLE_BUFFERS = 32936` |  |
| `static final int GL_SAMPLE_COVERAGE_INVERT = 32939` |  |
| `static final int GL_SAMPLE_COVERAGE_VALUE = 32938` |  |
| `static final int GL_SCISSOR_BOX = 3088` |  |
| `static final int GL_SHADE_MODEL = 2900` |  |
| `static final int GL_SRC0_ALPHA = 34184` |  |
| `static final int GL_SRC0_RGB = 34176` |  |
| `static final int GL_SRC1_ALPHA = 34185` |  |
| `static final int GL_SRC1_RGB = 34177` |  |
| `static final int GL_SRC2_ALPHA = 34186` |  |
| `static final int GL_SRC2_RGB = 34178` |  |
| `static final int GL_STATIC_DRAW = 35044` |  |
| `static final int GL_STENCIL_CLEAR_VALUE = 2961` |  |
| `static final int GL_STENCIL_FAIL = 2964` |  |
| `static final int GL_STENCIL_FUNC = 2962` |  |
| `static final int GL_STENCIL_PASS_DEPTH_FAIL = 2965` |  |
| `static final int GL_STENCIL_PASS_DEPTH_PASS = 2966` |  |
| `static final int GL_STENCIL_REF = 2967` |  |
| `static final int GL_STENCIL_VALUE_MASK = 2963` |  |
| `static final int GL_STENCIL_WRITEMASK = 2968` |  |
| `static final int GL_SUBTRACT = 34023` |  |
| `static final int GL_TEXTURE_BINDING_2D = 32873` |  |
| `static final int GL_TEXTURE_COORD_ARRAY_BUFFER_BINDING = 34970` |  |
| `static final int GL_TEXTURE_COORD_ARRAY_POINTER = 32914` |  |
| `static final int GL_TEXTURE_COORD_ARRAY_SIZE = 32904` |  |
| `static final int GL_TEXTURE_COORD_ARRAY_STRIDE = 32906` |  |
| `static final int GL_TEXTURE_COORD_ARRAY_TYPE = 32905` |  |
| `static final int GL_TEXTURE_MATRIX = 2984` |  |
| `static final int GL_TEXTURE_MATRIX_FLOAT_AS_INT_BITS_OES = 35215` |  |
| `static final int GL_TEXTURE_STACK_DEPTH = 2981` |  |
| `static final int GL_VERTEX_ARRAY_BUFFER_BINDING = 34966` |  |
| `static final int GL_VERTEX_ARRAY_POINTER = 32910` |  |
| `static final int GL_VERTEX_ARRAY_SIZE = 32890` |  |
| `static final int GL_VERTEX_ARRAY_STRIDE = 32892` |  |
| `static final int GL_VERTEX_ARRAY_TYPE = 32891` |  |
| `static final int GL_VIEWPORT = 2978` |  |
| `static final int GL_WRITE_ONLY = 35001` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void glBindBuffer(int, int)` |  |
| `static void glBufferData(int, int, java.nio.Buffer, int)` |  |
| `static void glBufferSubData(int, int, int, java.nio.Buffer)` |  |
| `static void glClipPlanef(int, float[], int)` |  |
| `static void glClipPlanef(int, java.nio.FloatBuffer)` |  |
| `static void glClipPlanex(int, int[], int)` |  |
| `static void glClipPlanex(int, java.nio.IntBuffer)` |  |
| `static void glColor4ub(byte, byte, byte, byte)` |  |
| `static void glColorPointer(int, int, int, int)` |  |
| `static void glDeleteBuffers(int, int[], int)` |  |
| `static void glDeleteBuffers(int, java.nio.IntBuffer)` |  |
| `static void glDrawElements(int, int, int, int)` |  |
| `static void glGenBuffers(int, int[], int)` |  |
| `static void glGenBuffers(int, java.nio.IntBuffer)` |  |
| `static void glGetBooleanv(int, boolean[], int)` |  |
| `static void glGetBooleanv(int, java.nio.IntBuffer)` |  |
| `static void glGetBufferParameteriv(int, int, int[], int)` |  |
| `static void glGetBufferParameteriv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetClipPlanef(int, float[], int)` |  |
| `static void glGetClipPlanef(int, java.nio.FloatBuffer)` |  |
| `static void glGetClipPlanex(int, int[], int)` |  |
| `static void glGetClipPlanex(int, java.nio.IntBuffer)` |  |
| `static void glGetFixedv(int, int[], int)` |  |
| `static void glGetFixedv(int, java.nio.IntBuffer)` |  |
| `static void glGetFloatv(int, float[], int)` |  |
| `static void glGetFloatv(int, java.nio.FloatBuffer)` |  |
| `static void glGetLightfv(int, int, float[], int)` |  |
| `static void glGetLightfv(int, int, java.nio.FloatBuffer)` |  |
| `static void glGetLightxv(int, int, int[], int)` |  |
| `static void glGetLightxv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetMaterialfv(int, int, float[], int)` |  |
| `static void glGetMaterialfv(int, int, java.nio.FloatBuffer)` |  |
| `static void glGetMaterialxv(int, int, int[], int)` |  |
| `static void glGetMaterialxv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetTexEnvfv(int, int, float[], int)` |  |
| `static void glGetTexEnvfv(int, int, java.nio.FloatBuffer)` |  |
| `static void glGetTexEnviv(int, int, int[], int)` |  |
| `static void glGetTexEnviv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetTexEnvxv(int, int, int[], int)` |  |
| `static void glGetTexEnvxv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetTexParameterfv(int, int, float[], int)` |  |
| `static void glGetTexParameterfv(int, int, java.nio.FloatBuffer)` |  |
| `static void glGetTexParameteriv(int, int, int[], int)` |  |
| `static void glGetTexParameteriv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetTexParameterxv(int, int, int[], int)` |  |
| `static void glGetTexParameterxv(int, int, java.nio.IntBuffer)` |  |
| `static boolean glIsBuffer(int)` |  |
| `static boolean glIsEnabled(int)` |  |
| `static boolean glIsTexture(int)` |  |
| `static void glNormalPointer(int, int, int)` |  |
| `static void glPointParameterf(int, float)` |  |
| `static void glPointParameterfv(int, float[], int)` |  |
| `static void glPointParameterfv(int, java.nio.FloatBuffer)` |  |
| `static void glPointParameterx(int, int)` |  |
| `static void glPointParameterxv(int, int[], int)` |  |
| `static void glPointParameterxv(int, java.nio.IntBuffer)` |  |
| `static void glPointSizePointerOES(int, int, java.nio.Buffer)` |  |
| `static void glTexCoordPointer(int, int, int, int)` |  |
| `static void glTexEnvi(int, int, int)` |  |
| `static void glTexEnviv(int, int, int[], int)` |  |
| `static void glTexEnviv(int, int, java.nio.IntBuffer)` |  |
| `static void glTexParameterfv(int, int, float[], int)` |  |
| `static void glTexParameterfv(int, int, java.nio.FloatBuffer)` |  |
| `static void glTexParameteri(int, int, int)` |  |
| `static void glTexParameteriv(int, int, int[], int)` |  |
| `static void glTexParameteriv(int, int, java.nio.IntBuffer)` |  |
| `static void glTexParameterxv(int, int, int[], int)` |  |
| `static void glTexParameterxv(int, int, java.nio.IntBuffer)` |  |
| `static void glVertexPointer(int, int, int, int)` |  |

---

### `class GLES11Ext`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GLES11Ext()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int GL_3DC_XY_AMD = 34810` |  |
| `static final int GL_3DC_X_AMD = 34809` |  |
| `static final int GL_ATC_RGBA_EXPLICIT_ALPHA_AMD = 35987` |  |
| `static final int GL_ATC_RGBA_INTERPOLATED_ALPHA_AMD = 34798` |  |
| `static final int GL_ATC_RGB_AMD = 35986` |  |
| `static final int GL_BGRA = 32993` |  |
| `static final int GL_BLEND_DST_ALPHA_OES = 32970` |  |
| `static final int GL_BLEND_DST_RGB_OES = 32968` |  |
| `static final int GL_BLEND_EQUATION_ALPHA_OES = 34877` |  |
| `static final int GL_BLEND_EQUATION_OES = 32777` |  |
| `static final int GL_BLEND_EQUATION_RGB_OES = 32777` |  |
| `static final int GL_BLEND_SRC_ALPHA_OES = 32971` |  |
| `static final int GL_BLEND_SRC_RGB_OES = 32969` |  |
| `static final int GL_BUFFER_ACCESS_OES = 35003` |  |
| `static final int GL_BUFFER_MAPPED_OES = 35004` |  |
| `static final int GL_BUFFER_MAP_POINTER_OES = 35005` |  |
| `static final int GL_COLOR_ATTACHMENT0_OES = 36064` |  |
| `static final int GL_CURRENT_PALETTE_MATRIX_OES = 34883` |  |
| `static final int GL_DECR_WRAP_OES = 34056` |  |
| `static final int GL_DEPTH24_STENCIL8_OES = 35056` |  |
| `static final int GL_DEPTH_ATTACHMENT_OES = 36096` |  |
| `static final int GL_DEPTH_COMPONENT16_OES = 33189` |  |
| `static final int GL_DEPTH_COMPONENT24_OES = 33190` |  |
| `static final int GL_DEPTH_COMPONENT32_OES = 33191` |  |
| `static final int GL_DEPTH_STENCIL_OES = 34041` |  |
| `static final int GL_ETC1_RGB8_OES = 36196` |  |
| `static final int GL_FIXED_OES = 5132` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME_OES = 36049` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE_OES = 36048` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE_OES = 36051` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL_OES = 36050` |  |
| `static final int GL_FRAMEBUFFER_BINDING_OES = 36006` |  |
| `static final int GL_FRAMEBUFFER_COMPLETE_OES = 36053` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_OES = 36054` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_OES = 36057` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_FORMATS_OES = 36058` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_OES = 36055` |  |
| `static final int GL_FRAMEBUFFER_OES = 36160` |  |
| `static final int GL_FRAMEBUFFER_UNSUPPORTED_OES = 36061` |  |
| `static final int GL_FUNC_ADD_OES = 32774` |  |
| `static final int GL_FUNC_REVERSE_SUBTRACT_OES = 32779` |  |
| `static final int GL_FUNC_SUBTRACT_OES = 32778` |  |
| `static final int GL_INCR_WRAP_OES = 34055` |  |
| `static final int GL_INVALID_FRAMEBUFFER_OPERATION_OES = 1286` |  |
| `static final int GL_MATRIX_INDEX_ARRAY_BUFFER_BINDING_OES = 35742` |  |
| `static final int GL_MATRIX_INDEX_ARRAY_OES = 34884` |  |
| `static final int GL_MATRIX_INDEX_ARRAY_POINTER_OES = 34889` |  |
| `static final int GL_MATRIX_INDEX_ARRAY_SIZE_OES = 34886` |  |
| `static final int GL_MATRIX_INDEX_ARRAY_STRIDE_OES = 34888` |  |
| `static final int GL_MATRIX_INDEX_ARRAY_TYPE_OES = 34887` |  |
| `static final int GL_MATRIX_PALETTE_OES = 34880` |  |
| `static final int GL_MAX_CUBE_MAP_TEXTURE_SIZE_OES = 34076` |  |
| `static final int GL_MAX_PALETTE_MATRICES_OES = 34882` |  |
| `static final int GL_MAX_RENDERBUFFER_SIZE_OES = 34024` |  |
| `static final int GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT = 34047` |  |
| `static final int GL_MAX_VERTEX_UNITS_OES = 34468` |  |
| `static final int GL_MIRRORED_REPEAT_OES = 33648` |  |
| `static final int GL_MODELVIEW_MATRIX_FLOAT_AS_INT_BITS_OES = 35213` |  |
| `static final int GL_NONE_OES = 0` |  |
| `static final int GL_NORMAL_MAP_OES = 34065` |  |
| `static final int GL_PROJECTION_MATRIX_FLOAT_AS_INT_BITS_OES = 35214` |  |
| `static final int GL_REFLECTION_MAP_OES = 34066` |  |
| `static final int GL_RENDERBUFFER_ALPHA_SIZE_OES = 36179` |  |
| `static final int GL_RENDERBUFFER_BINDING_OES = 36007` |  |
| `static final int GL_RENDERBUFFER_BLUE_SIZE_OES = 36178` |  |
| `static final int GL_RENDERBUFFER_DEPTH_SIZE_OES = 36180` |  |
| `static final int GL_RENDERBUFFER_GREEN_SIZE_OES = 36177` |  |
| `static final int GL_RENDERBUFFER_HEIGHT_OES = 36163` |  |
| `static final int GL_RENDERBUFFER_INTERNAL_FORMAT_OES = 36164` |  |
| `static final int GL_RENDERBUFFER_OES = 36161` |  |
| `static final int GL_RENDERBUFFER_RED_SIZE_OES = 36176` |  |
| `static final int GL_RENDERBUFFER_STENCIL_SIZE_OES = 36181` |  |
| `static final int GL_RENDERBUFFER_WIDTH_OES = 36162` |  |
| `static final int GL_REQUIRED_TEXTURE_IMAGE_UNITS_OES = 36200` |  |
| `static final int GL_RGB565_OES = 36194` |  |
| `static final int GL_RGB5_A1_OES = 32855` |  |
| `static final int GL_RGB8_OES = 32849` |  |
| `static final int GL_RGBA4_OES = 32854` |  |
| `static final int GL_RGBA8_OES = 32856` |  |
| `static final int GL_SAMPLER_EXTERNAL_OES = 36198` |  |
| `static final int GL_STENCIL_ATTACHMENT_OES = 36128` |  |
| `static final int GL_STENCIL_INDEX1_OES = 36166` |  |
| `static final int GL_STENCIL_INDEX4_OES = 36167` |  |
| `static final int GL_STENCIL_INDEX8_OES = 36168` |  |
| `static final int GL_TEXTURE_BINDING_CUBE_MAP_OES = 34068` |  |
| `static final int GL_TEXTURE_BINDING_EXTERNAL_OES = 36199` |  |
| `static final int GL_TEXTURE_CROP_RECT_OES = 35741` |  |
| `static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_X_OES = 34070` |  |
| `static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Y_OES = 34072` |  |
| `static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Z_OES = 34074` |  |
| `static final int GL_TEXTURE_CUBE_MAP_OES = 34067` |  |
| `static final int GL_TEXTURE_CUBE_MAP_POSITIVE_X_OES = 34069` |  |
| `static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Y_OES = 34071` |  |
| `static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Z_OES = 34073` |  |
| `static final int GL_TEXTURE_EXTERNAL_OES = 36197` |  |
| `static final int GL_TEXTURE_GEN_MODE_OES = 9472` |  |
| `static final int GL_TEXTURE_GEN_STR_OES = 36192` |  |
| `static final int GL_TEXTURE_MATRIX_FLOAT_AS_INT_BITS_OES = 35215` |  |
| `static final int GL_TEXTURE_MAX_ANISOTROPY_EXT = 34046` |  |
| `static final int GL_UNSIGNED_INT_24_8_OES = 34042` |  |
| `static final int GL_WEIGHT_ARRAY_BUFFER_BINDING_OES = 34974` |  |
| `static final int GL_WEIGHT_ARRAY_OES = 34477` |  |
| `static final int GL_WEIGHT_ARRAY_POINTER_OES = 34476` |  |
| `static final int GL_WEIGHT_ARRAY_SIZE_OES = 34475` |  |
| `static final int GL_WEIGHT_ARRAY_STRIDE_OES = 34474` |  |
| `static final int GL_WEIGHT_ARRAY_TYPE_OES = 34473` |  |
| `static final int GL_WRITE_ONLY_OES = 35001` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void glAlphaFuncxOES(int, int)` |  |
| `static void glBindFramebufferOES(int, int)` |  |
| `static void glBindRenderbufferOES(int, int)` |  |
| `static void glBlendEquationOES(int)` |  |
| `static void glBlendEquationSeparateOES(int, int)` |  |
| `static void glBlendFuncSeparateOES(int, int, int, int)` |  |
| `static int glCheckFramebufferStatusOES(int)` |  |
| `static void glClearColorxOES(int, int, int, int)` |  |
| `static void glClearDepthfOES(float)` |  |
| `static void glClearDepthxOES(int)` |  |
| `static void glClipPlanefOES(int, float[], int)` |  |
| `static void glClipPlanefOES(int, java.nio.FloatBuffer)` |  |
| `static void glClipPlanexOES(int, int[], int)` |  |
| `static void glClipPlanexOES(int, java.nio.IntBuffer)` |  |
| `static void glColor4xOES(int, int, int, int)` |  |
| `static void glCurrentPaletteMatrixOES(int)` |  |
| `static void glDeleteFramebuffersOES(int, int[], int)` |  |
| `static void glDeleteFramebuffersOES(int, java.nio.IntBuffer)` |  |
| `static void glDeleteRenderbuffersOES(int, int[], int)` |  |
| `static void glDeleteRenderbuffersOES(int, java.nio.IntBuffer)` |  |
| `static void glDepthRangefOES(float, float)` |  |
| `static void glDepthRangexOES(int, int)` |  |
| `static void glDrawTexfOES(float, float, float, float, float)` |  |
| `static void glDrawTexfvOES(float[], int)` |  |
| `static void glDrawTexfvOES(java.nio.FloatBuffer)` |  |
| `static void glDrawTexiOES(int, int, int, int, int)` |  |
| `static void glDrawTexivOES(int[], int)` |  |
| `static void glDrawTexivOES(java.nio.IntBuffer)` |  |
| `static void glDrawTexsOES(short, short, short, short, short)` |  |
| `static void glDrawTexsvOES(short[], int)` |  |
| `static void glDrawTexsvOES(java.nio.ShortBuffer)` |  |
| `static void glDrawTexxOES(int, int, int, int, int)` |  |
| `static void glDrawTexxvOES(int[], int)` |  |
| `static void glDrawTexxvOES(java.nio.IntBuffer)` |  |
| `static void glEGLImageTargetRenderbufferStorageOES(int, java.nio.Buffer)` |  |
| `static void glEGLImageTargetTexture2DOES(int, java.nio.Buffer)` |  |
| `static void glFogxOES(int, int)` |  |
| `static void glFogxvOES(int, int[], int)` |  |
| `static void glFogxvOES(int, java.nio.IntBuffer)` |  |
| `static void glFramebufferRenderbufferOES(int, int, int, int)` |  |
| `static void glFramebufferTexture2DOES(int, int, int, int, int)` |  |
| `static void glFrustumfOES(float, float, float, float, float, float)` |  |
| `static void glFrustumxOES(int, int, int, int, int, int)` |  |
| `static void glGenFramebuffersOES(int, int[], int)` |  |
| `static void glGenFramebuffersOES(int, java.nio.IntBuffer)` |  |
| `static void glGenRenderbuffersOES(int, int[], int)` |  |
| `static void glGenRenderbuffersOES(int, java.nio.IntBuffer)` |  |
| `static void glGenerateMipmapOES(int)` |  |
| `static void glGetClipPlanefOES(int, float[], int)` |  |
| `static void glGetClipPlanefOES(int, java.nio.FloatBuffer)` |  |
| `static void glGetClipPlanexOES(int, int[], int)` |  |
| `static void glGetClipPlanexOES(int, java.nio.IntBuffer)` |  |
| `static void glGetFixedvOES(int, int[], int)` |  |
| `static void glGetFixedvOES(int, java.nio.IntBuffer)` |  |
| `static void glGetFramebufferAttachmentParameterivOES(int, int, int, int[], int)` |  |
| `static void glGetFramebufferAttachmentParameterivOES(int, int, int, java.nio.IntBuffer)` |  |
| `static void glGetLightxvOES(int, int, int[], int)` |  |
| `static void glGetLightxvOES(int, int, java.nio.IntBuffer)` |  |
| `static void glGetMaterialxvOES(int, int, int[], int)` |  |
| `static void glGetMaterialxvOES(int, int, java.nio.IntBuffer)` |  |
| `static void glGetRenderbufferParameterivOES(int, int, int[], int)` |  |
| `static void glGetRenderbufferParameterivOES(int, int, java.nio.IntBuffer)` |  |
| `static void glGetTexEnvxvOES(int, int, int[], int)` |  |
| `static void glGetTexEnvxvOES(int, int, java.nio.IntBuffer)` |  |
| `static void glGetTexGenfvOES(int, int, float[], int)` |  |
| `static void glGetTexGenfvOES(int, int, java.nio.FloatBuffer)` |  |
| `static void glGetTexGenivOES(int, int, int[], int)` |  |
| `static void glGetTexGenivOES(int, int, java.nio.IntBuffer)` |  |
| `static void glGetTexGenxvOES(int, int, int[], int)` |  |
| `static void glGetTexGenxvOES(int, int, java.nio.IntBuffer)` |  |
| `static void glGetTexParameterxvOES(int, int, int[], int)` |  |
| `static void glGetTexParameterxvOES(int, int, java.nio.IntBuffer)` |  |
| `static boolean glIsFramebufferOES(int)` |  |
| `static boolean glIsRenderbufferOES(int)` |  |
| `static void glLightModelxOES(int, int)` |  |
| `static void glLightModelxvOES(int, int[], int)` |  |
| `static void glLightModelxvOES(int, java.nio.IntBuffer)` |  |
| `static void glLightxOES(int, int, int)` |  |
| `static void glLightxvOES(int, int, int[], int)` |  |
| `static void glLightxvOES(int, int, java.nio.IntBuffer)` |  |
| `static void glLineWidthxOES(int)` |  |
| `static void glLoadMatrixxOES(int[], int)` |  |
| `static void glLoadMatrixxOES(java.nio.IntBuffer)` |  |
| `static void glLoadPaletteFromModelViewMatrixOES()` |  |
| `static void glMaterialxOES(int, int, int)` |  |
| `static void glMaterialxvOES(int, int, int[], int)` |  |
| `static void glMaterialxvOES(int, int, java.nio.IntBuffer)` |  |
| `static void glMatrixIndexPointerOES(int, int, int, java.nio.Buffer)` |  |
| `static void glMultMatrixxOES(int[], int)` |  |
| `static void glMultMatrixxOES(java.nio.IntBuffer)` |  |
| `static void glMultiTexCoord4xOES(int, int, int, int, int)` |  |
| `static void glNormal3xOES(int, int, int)` |  |
| `static void glOrthofOES(float, float, float, float, float, float)` |  |
| `static void glOrthoxOES(int, int, int, int, int, int)` |  |
| `static void glPointParameterxOES(int, int)` |  |
| `static void glPointParameterxvOES(int, int[], int)` |  |
| `static void glPointParameterxvOES(int, java.nio.IntBuffer)` |  |
| `static void glPointSizexOES(int)` |  |
| `static void glPolygonOffsetxOES(int, int)` |  |
| `static void glRenderbufferStorageOES(int, int, int, int)` |  |
| `static void glRotatexOES(int, int, int, int)` |  |
| `static void glSampleCoveragexOES(int, boolean)` |  |
| `static void glScalexOES(int, int, int)` |  |
| `static void glTexEnvxOES(int, int, int)` |  |
| `static void glTexEnvxvOES(int, int, int[], int)` |  |
| `static void glTexEnvxvOES(int, int, java.nio.IntBuffer)` |  |
| `static void glTexGenfOES(int, int, float)` |  |
| `static void glTexGenfvOES(int, int, float[], int)` |  |
| `static void glTexGenfvOES(int, int, java.nio.FloatBuffer)` |  |
| `static void glTexGeniOES(int, int, int)` |  |
| `static void glTexGenivOES(int, int, int[], int)` |  |
| `static void glTexGenivOES(int, int, java.nio.IntBuffer)` |  |
| `static void glTexGenxOES(int, int, int)` |  |
| `static void glTexGenxvOES(int, int, int[], int)` |  |
| `static void glTexGenxvOES(int, int, java.nio.IntBuffer)` |  |
| `static void glTexParameterxOES(int, int, int)` |  |
| `static void glTexParameterxvOES(int, int, int[], int)` |  |
| `static void glTexParameterxvOES(int, int, java.nio.IntBuffer)` |  |
| `static void glTranslatexOES(int, int, int)` |  |
| `static void glWeightPointerOES(int, int, int, java.nio.Buffer)` |  |

---

### `class GLES20`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GLES20()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int GL_ACTIVE_ATTRIBUTES = 35721` |  |
| `static final int GL_ACTIVE_ATTRIBUTE_MAX_LENGTH = 35722` |  |
| `static final int GL_ACTIVE_TEXTURE = 34016` |  |
| `static final int GL_ACTIVE_UNIFORMS = 35718` |  |
| `static final int GL_ACTIVE_UNIFORM_MAX_LENGTH = 35719` |  |
| `static final int GL_ALIASED_LINE_WIDTH_RANGE = 33902` |  |
| `static final int GL_ALIASED_POINT_SIZE_RANGE = 33901` |  |
| `static final int GL_ALPHA = 6406` |  |
| `static final int GL_ALPHA_BITS = 3413` |  |
| `static final int GL_ALWAYS = 519` |  |
| `static final int GL_ARRAY_BUFFER = 34962` |  |
| `static final int GL_ARRAY_BUFFER_BINDING = 34964` |  |
| `static final int GL_ATTACHED_SHADERS = 35717` |  |
| `static final int GL_BACK = 1029` |  |
| `static final int GL_BLEND = 3042` |  |
| `static final int GL_BLEND_COLOR = 32773` |  |
| `static final int GL_BLEND_DST_ALPHA = 32970` |  |
| `static final int GL_BLEND_DST_RGB = 32968` |  |
| `static final int GL_BLEND_EQUATION = 32777` |  |
| `static final int GL_BLEND_EQUATION_ALPHA = 34877` |  |
| `static final int GL_BLEND_EQUATION_RGB = 32777` |  |
| `static final int GL_BLEND_SRC_ALPHA = 32971` |  |
| `static final int GL_BLEND_SRC_RGB = 32969` |  |
| `static final int GL_BLUE_BITS = 3412` |  |
| `static final int GL_BOOL = 35670` |  |
| `static final int GL_BOOL_VEC2 = 35671` |  |
| `static final int GL_BOOL_VEC3 = 35672` |  |
| `static final int GL_BOOL_VEC4 = 35673` |  |
| `static final int GL_BUFFER_SIZE = 34660` |  |
| `static final int GL_BUFFER_USAGE = 34661` |  |
| `static final int GL_BYTE = 5120` |  |
| `static final int GL_CCW = 2305` |  |
| `static final int GL_CLAMP_TO_EDGE = 33071` |  |
| `static final int GL_COLOR_ATTACHMENT0 = 36064` |  |
| `static final int GL_COLOR_BUFFER_BIT = 16384` |  |
| `static final int GL_COLOR_CLEAR_VALUE = 3106` |  |
| `static final int GL_COLOR_WRITEMASK = 3107` |  |
| `static final int GL_COMPILE_STATUS = 35713` |  |
| `static final int GL_COMPRESSED_TEXTURE_FORMATS = 34467` |  |
| `static final int GL_CONSTANT_ALPHA = 32771` |  |
| `static final int GL_CONSTANT_COLOR = 32769` |  |
| `static final int GL_CULL_FACE = 2884` |  |
| `static final int GL_CULL_FACE_MODE = 2885` |  |
| `static final int GL_CURRENT_PROGRAM = 35725` |  |
| `static final int GL_CURRENT_VERTEX_ATTRIB = 34342` |  |
| `static final int GL_CW = 2304` |  |
| `static final int GL_DECR = 7683` |  |
| `static final int GL_DECR_WRAP = 34056` |  |
| `static final int GL_DELETE_STATUS = 35712` |  |
| `static final int GL_DEPTH_ATTACHMENT = 36096` |  |
| `static final int GL_DEPTH_BITS = 3414` |  |
| `static final int GL_DEPTH_BUFFER_BIT = 256` |  |
| `static final int GL_DEPTH_CLEAR_VALUE = 2931` |  |
| `static final int GL_DEPTH_COMPONENT = 6402` |  |
| `static final int GL_DEPTH_COMPONENT16 = 33189` |  |
| `static final int GL_DEPTH_FUNC = 2932` |  |
| `static final int GL_DEPTH_RANGE = 2928` |  |
| `static final int GL_DEPTH_TEST = 2929` |  |
| `static final int GL_DEPTH_WRITEMASK = 2930` |  |
| `static final int GL_DITHER = 3024` |  |
| `static final int GL_DONT_CARE = 4352` |  |
| `static final int GL_DST_ALPHA = 772` |  |
| `static final int GL_DST_COLOR = 774` |  |
| `static final int GL_DYNAMIC_DRAW = 35048` |  |
| `static final int GL_ELEMENT_ARRAY_BUFFER = 34963` |  |
| `static final int GL_ELEMENT_ARRAY_BUFFER_BINDING = 34965` |  |
| `static final int GL_EQUAL = 514` |  |
| `static final int GL_EXTENSIONS = 7939` |  |
| `static final int GL_FALSE = 0` |  |
| `static final int GL_FASTEST = 4353` |  |
| `static final int GL_FIXED = 5132` |  |
| `static final int GL_FLOAT = 5126` |  |
| `static final int GL_FLOAT_MAT2 = 35674` |  |
| `static final int GL_FLOAT_MAT3 = 35675` |  |
| `static final int GL_FLOAT_MAT4 = 35676` |  |
| `static final int GL_FLOAT_VEC2 = 35664` |  |
| `static final int GL_FLOAT_VEC3 = 35665` |  |
| `static final int GL_FLOAT_VEC4 = 35666` |  |
| `static final int GL_FRAGMENT_SHADER = 35632` |  |
| `static final int GL_FRAMEBUFFER = 36160` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = 36049` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = 36048` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 36051` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = 36050` |  |
| `static final int GL_FRAMEBUFFER_BINDING = 36006` |  |
| `static final int GL_FRAMEBUFFER_COMPLETE = 36053` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS = 36057` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055` |  |
| `static final int GL_FRAMEBUFFER_UNSUPPORTED = 36061` |  |
| `static final int GL_FRONT = 1028` |  |
| `static final int GL_FRONT_AND_BACK = 1032` |  |
| `static final int GL_FRONT_FACE = 2886` |  |
| `static final int GL_FUNC_ADD = 32774` |  |
| `static final int GL_FUNC_REVERSE_SUBTRACT = 32779` |  |
| `static final int GL_FUNC_SUBTRACT = 32778` |  |
| `static final int GL_GENERATE_MIPMAP_HINT = 33170` |  |
| `static final int GL_GEQUAL = 518` |  |
| `static final int GL_GREATER = 516` |  |
| `static final int GL_GREEN_BITS = 3411` |  |
| `static final int GL_HIGH_FLOAT = 36338` |  |
| `static final int GL_HIGH_INT = 36341` |  |
| `static final int GL_IMPLEMENTATION_COLOR_READ_FORMAT = 35739` |  |
| `static final int GL_IMPLEMENTATION_COLOR_READ_TYPE = 35738` |  |
| `static final int GL_INCR = 7682` |  |
| `static final int GL_INCR_WRAP = 34055` |  |
| `static final int GL_INFO_LOG_LENGTH = 35716` |  |
| `static final int GL_INT = 5124` |  |
| `static final int GL_INT_VEC2 = 35667` |  |
| `static final int GL_INT_VEC3 = 35668` |  |
| `static final int GL_INT_VEC4 = 35669` |  |
| `static final int GL_INVALID_ENUM = 1280` |  |
| `static final int GL_INVALID_FRAMEBUFFER_OPERATION = 1286` |  |
| `static final int GL_INVALID_OPERATION = 1282` |  |
| `static final int GL_INVALID_VALUE = 1281` |  |
| `static final int GL_INVERT = 5386` |  |
| `static final int GL_KEEP = 7680` |  |
| `static final int GL_LEQUAL = 515` |  |
| `static final int GL_LESS = 513` |  |
| `static final int GL_LINEAR = 9729` |  |
| `static final int GL_LINEAR_MIPMAP_LINEAR = 9987` |  |
| `static final int GL_LINEAR_MIPMAP_NEAREST = 9985` |  |
| `static final int GL_LINES = 1` |  |
| `static final int GL_LINE_LOOP = 2` |  |
| `static final int GL_LINE_STRIP = 3` |  |
| `static final int GL_LINE_WIDTH = 2849` |  |
| `static final int GL_LINK_STATUS = 35714` |  |
| `static final int GL_LOW_FLOAT = 36336` |  |
| `static final int GL_LOW_INT = 36339` |  |
| `static final int GL_LUMINANCE = 6409` |  |
| `static final int GL_LUMINANCE_ALPHA = 6410` |  |
| `static final int GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS = 35661` |  |
| `static final int GL_MAX_CUBE_MAP_TEXTURE_SIZE = 34076` |  |
| `static final int GL_MAX_FRAGMENT_UNIFORM_VECTORS = 36349` |  |
| `static final int GL_MAX_RENDERBUFFER_SIZE = 34024` |  |
| `static final int GL_MAX_TEXTURE_IMAGE_UNITS = 34930` |  |
| `static final int GL_MAX_TEXTURE_SIZE = 3379` |  |
| `static final int GL_MAX_VARYING_VECTORS = 36348` |  |
| `static final int GL_MAX_VERTEX_ATTRIBS = 34921` |  |
| `static final int GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS = 35660` |  |
| `static final int GL_MAX_VERTEX_UNIFORM_VECTORS = 36347` |  |
| `static final int GL_MAX_VIEWPORT_DIMS = 3386` |  |
| `static final int GL_MEDIUM_FLOAT = 36337` |  |
| `static final int GL_MEDIUM_INT = 36340` |  |
| `static final int GL_MIRRORED_REPEAT = 33648` |  |
| `static final int GL_NEAREST = 9728` |  |
| `static final int GL_NEAREST_MIPMAP_LINEAR = 9986` |  |
| `static final int GL_NEAREST_MIPMAP_NEAREST = 9984` |  |
| `static final int GL_NEVER = 512` |  |
| `static final int GL_NICEST = 4354` |  |
| `static final int GL_NONE = 0` |  |
| `static final int GL_NOTEQUAL = 517` |  |
| `static final int GL_NO_ERROR = 0` |  |
| `static final int GL_NUM_COMPRESSED_TEXTURE_FORMATS = 34466` |  |
| `static final int GL_NUM_SHADER_BINARY_FORMATS = 36345` |  |
| `static final int GL_ONE = 1` |  |
| `static final int GL_ONE_MINUS_CONSTANT_ALPHA = 32772` |  |
| `static final int GL_ONE_MINUS_CONSTANT_COLOR = 32770` |  |
| `static final int GL_ONE_MINUS_DST_ALPHA = 773` |  |
| `static final int GL_ONE_MINUS_DST_COLOR = 775` |  |
| `static final int GL_ONE_MINUS_SRC_ALPHA = 771` |  |
| `static final int GL_ONE_MINUS_SRC_COLOR = 769` |  |
| `static final int GL_OUT_OF_MEMORY = 1285` |  |
| `static final int GL_PACK_ALIGNMENT = 3333` |  |
| `static final int GL_POINTS = 0` |  |
| `static final int GL_POLYGON_OFFSET_FACTOR = 32824` |  |
| `static final int GL_POLYGON_OFFSET_FILL = 32823` |  |
| `static final int GL_POLYGON_OFFSET_UNITS = 10752` |  |
| `static final int GL_RED_BITS = 3410` |  |
| `static final int GL_RENDERBUFFER = 36161` |  |
| `static final int GL_RENDERBUFFER_ALPHA_SIZE = 36179` |  |
| `static final int GL_RENDERBUFFER_BINDING = 36007` |  |
| `static final int GL_RENDERBUFFER_BLUE_SIZE = 36178` |  |
| `static final int GL_RENDERBUFFER_DEPTH_SIZE = 36180` |  |
| `static final int GL_RENDERBUFFER_GREEN_SIZE = 36177` |  |
| `static final int GL_RENDERBUFFER_HEIGHT = 36163` |  |
| `static final int GL_RENDERBUFFER_INTERNAL_FORMAT = 36164` |  |
| `static final int GL_RENDERBUFFER_RED_SIZE = 36176` |  |
| `static final int GL_RENDERBUFFER_STENCIL_SIZE = 36181` |  |
| `static final int GL_RENDERBUFFER_WIDTH = 36162` |  |
| `static final int GL_RENDERER = 7937` |  |
| `static final int GL_REPEAT = 10497` |  |
| `static final int GL_REPLACE = 7681` |  |
| `static final int GL_RGB = 6407` |  |
| `static final int GL_RGB565 = 36194` |  |
| `static final int GL_RGB5_A1 = 32855` |  |
| `static final int GL_RGBA = 6408` |  |
| `static final int GL_RGBA4 = 32854` |  |
| `static final int GL_SAMPLER_2D = 35678` |  |
| `static final int GL_SAMPLER_CUBE = 35680` |  |
| `static final int GL_SAMPLES = 32937` |  |
| `static final int GL_SAMPLE_ALPHA_TO_COVERAGE = 32926` |  |
| `static final int GL_SAMPLE_BUFFERS = 32936` |  |
| `static final int GL_SAMPLE_COVERAGE = 32928` |  |
| `static final int GL_SAMPLE_COVERAGE_INVERT = 32939` |  |
| `static final int GL_SAMPLE_COVERAGE_VALUE = 32938` |  |
| `static final int GL_SCISSOR_BOX = 3088` |  |
| `static final int GL_SCISSOR_TEST = 3089` |  |
| `static final int GL_SHADER_BINARY_FORMATS = 36344` |  |
| `static final int GL_SHADER_COMPILER = 36346` |  |
| `static final int GL_SHADER_SOURCE_LENGTH = 35720` |  |
| `static final int GL_SHADER_TYPE = 35663` |  |
| `static final int GL_SHADING_LANGUAGE_VERSION = 35724` |  |
| `static final int GL_SHORT = 5122` |  |
| `static final int GL_SRC_ALPHA = 770` |  |
| `static final int GL_SRC_ALPHA_SATURATE = 776` |  |
| `static final int GL_SRC_COLOR = 768` |  |
| `static final int GL_STATIC_DRAW = 35044` |  |
| `static final int GL_STENCIL_ATTACHMENT = 36128` |  |
| `static final int GL_STENCIL_BACK_FAIL = 34817` |  |
| `static final int GL_STENCIL_BACK_FUNC = 34816` |  |
| `static final int GL_STENCIL_BACK_PASS_DEPTH_FAIL = 34818` |  |
| `static final int GL_STENCIL_BACK_PASS_DEPTH_PASS = 34819` |  |
| `static final int GL_STENCIL_BACK_REF = 36003` |  |
| `static final int GL_STENCIL_BACK_VALUE_MASK = 36004` |  |
| `static final int GL_STENCIL_BACK_WRITEMASK = 36005` |  |
| `static final int GL_STENCIL_BITS = 3415` |  |
| `static final int GL_STENCIL_BUFFER_BIT = 1024` |  |
| `static final int GL_STENCIL_CLEAR_VALUE = 2961` |  |
| `static final int GL_STENCIL_FAIL = 2964` |  |
| `static final int GL_STENCIL_FUNC = 2962` |  |
| `static final int GL_STENCIL_INDEX8 = 36168` |  |
| `static final int GL_STENCIL_PASS_DEPTH_FAIL = 2965` |  |
| `static final int GL_STENCIL_PASS_DEPTH_PASS = 2966` |  |
| `static final int GL_STENCIL_REF = 2967` |  |
| `static final int GL_STENCIL_TEST = 2960` |  |
| `static final int GL_STENCIL_VALUE_MASK = 2963` |  |
| `static final int GL_STENCIL_WRITEMASK = 2968` |  |
| `static final int GL_STREAM_DRAW = 35040` |  |
| `static final int GL_SUBPIXEL_BITS = 3408` |  |
| `static final int GL_TEXTURE = 5890` |  |
| `static final int GL_TEXTURE0 = 33984` |  |
| `static final int GL_TEXTURE1 = 33985` |  |
| `static final int GL_TEXTURE10 = 33994` |  |
| `static final int GL_TEXTURE11 = 33995` |  |
| `static final int GL_TEXTURE12 = 33996` |  |
| `static final int GL_TEXTURE13 = 33997` |  |
| `static final int GL_TEXTURE14 = 33998` |  |
| `static final int GL_TEXTURE15 = 33999` |  |
| `static final int GL_TEXTURE16 = 34000` |  |
| `static final int GL_TEXTURE17 = 34001` |  |
| `static final int GL_TEXTURE18 = 34002` |  |
| `static final int GL_TEXTURE19 = 34003` |  |
| `static final int GL_TEXTURE2 = 33986` |  |
| `static final int GL_TEXTURE20 = 34004` |  |
| `static final int GL_TEXTURE21 = 34005` |  |
| `static final int GL_TEXTURE22 = 34006` |  |
| `static final int GL_TEXTURE23 = 34007` |  |
| `static final int GL_TEXTURE24 = 34008` |  |
| `static final int GL_TEXTURE25 = 34009` |  |
| `static final int GL_TEXTURE26 = 34010` |  |
| `static final int GL_TEXTURE27 = 34011` |  |
| `static final int GL_TEXTURE28 = 34012` |  |
| `static final int GL_TEXTURE29 = 34013` |  |
| `static final int GL_TEXTURE3 = 33987` |  |
| `static final int GL_TEXTURE30 = 34014` |  |
| `static final int GL_TEXTURE31 = 34015` |  |
| `static final int GL_TEXTURE4 = 33988` |  |
| `static final int GL_TEXTURE5 = 33989` |  |
| `static final int GL_TEXTURE6 = 33990` |  |
| `static final int GL_TEXTURE7 = 33991` |  |
| `static final int GL_TEXTURE8 = 33992` |  |
| `static final int GL_TEXTURE9 = 33993` |  |
| `static final int GL_TEXTURE_2D = 3553` |  |
| `static final int GL_TEXTURE_BINDING_2D = 32873` |  |
| `static final int GL_TEXTURE_BINDING_CUBE_MAP = 34068` |  |
| `static final int GL_TEXTURE_CUBE_MAP = 34067` |  |
| `static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 34070` |  |
| `static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 34072` |  |
| `static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 34074` |  |
| `static final int GL_TEXTURE_CUBE_MAP_POSITIVE_X = 34069` |  |
| `static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 34071` |  |
| `static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 34073` |  |
| `static final int GL_TEXTURE_MAG_FILTER = 10240` |  |
| `static final int GL_TEXTURE_MIN_FILTER = 10241` |  |
| `static final int GL_TEXTURE_WRAP_S = 10242` |  |
| `static final int GL_TEXTURE_WRAP_T = 10243` |  |
| `static final int GL_TRIANGLES = 4` |  |
| `static final int GL_TRIANGLE_FAN = 6` |  |
| `static final int GL_TRIANGLE_STRIP = 5` |  |
| `static final int GL_TRUE = 1` |  |
| `static final int GL_UNPACK_ALIGNMENT = 3317` |  |
| `static final int GL_UNSIGNED_BYTE = 5121` |  |
| `static final int GL_UNSIGNED_INT = 5125` |  |
| `static final int GL_UNSIGNED_SHORT = 5123` |  |
| `static final int GL_UNSIGNED_SHORT_4_4_4_4 = 32819` |  |
| `static final int GL_UNSIGNED_SHORT_5_5_5_1 = 32820` |  |
| `static final int GL_UNSIGNED_SHORT_5_6_5 = 33635` |  |
| `static final int GL_VALIDATE_STATUS = 35715` |  |
| `static final int GL_VENDOR = 7936` |  |
| `static final int GL_VERSION = 7938` |  |
| `static final int GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 34975` |  |
| `static final int GL_VERTEX_ATTRIB_ARRAY_ENABLED = 34338` |  |
| `static final int GL_VERTEX_ATTRIB_ARRAY_NORMALIZED = 34922` |  |
| `static final int GL_VERTEX_ATTRIB_ARRAY_POINTER = 34373` |  |
| `static final int GL_VERTEX_ATTRIB_ARRAY_SIZE = 34339` |  |
| `static final int GL_VERTEX_ATTRIB_ARRAY_STRIDE = 34340` |  |
| `static final int GL_VERTEX_ATTRIB_ARRAY_TYPE = 34341` |  |
| `static final int GL_VERTEX_SHADER = 35633` |  |
| `static final int GL_VIEWPORT = 2978` |  |
| `static final int GL_ZERO = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void glActiveTexture(int)` |  |
| `static void glAttachShader(int, int)` |  |
| `static void glBindAttribLocation(int, int, String)` |  |
| `static void glBindBuffer(int, int)` |  |
| `static void glBindFramebuffer(int, int)` |  |
| `static void glBindRenderbuffer(int, int)` |  |
| `static void glBindTexture(int, int)` |  |
| `static void glBlendColor(float, float, float, float)` |  |
| `static void glBlendEquation(int)` |  |
| `static void glBlendEquationSeparate(int, int)` |  |
| `static void glBlendFunc(int, int)` |  |
| `static void glBlendFuncSeparate(int, int, int, int)` |  |
| `static void glBufferData(int, int, java.nio.Buffer, int)` |  |
| `static void glBufferSubData(int, int, int, java.nio.Buffer)` |  |
| `static int glCheckFramebufferStatus(int)` |  |
| `static void glClear(int)` |  |
| `static void glClearColor(float, float, float, float)` |  |
| `static void glClearDepthf(float)` |  |
| `static void glClearStencil(int)` |  |
| `static void glColorMask(boolean, boolean, boolean, boolean)` |  |
| `static void glCompileShader(int)` |  |
| `static void glCompressedTexImage2D(int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glCompressedTexSubImage2D(int, int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glCopyTexImage2D(int, int, int, int, int, int, int, int)` |  |
| `static void glCopyTexSubImage2D(int, int, int, int, int, int, int, int)` |  |
| `static int glCreateProgram()` |  |
| `static int glCreateShader(int)` |  |
| `static void glCullFace(int)` |  |
| `static void glDeleteBuffers(int, int[], int)` |  |
| `static void glDeleteBuffers(int, java.nio.IntBuffer)` |  |
| `static void glDeleteFramebuffers(int, int[], int)` |  |
| `static void glDeleteFramebuffers(int, java.nio.IntBuffer)` |  |
| `static void glDeleteProgram(int)` |  |
| `static void glDeleteRenderbuffers(int, int[], int)` |  |
| `static void glDeleteRenderbuffers(int, java.nio.IntBuffer)` |  |
| `static void glDeleteShader(int)` |  |
| `static void glDeleteTextures(int, int[], int)` |  |
| `static void glDeleteTextures(int, java.nio.IntBuffer)` |  |
| `static void glDepthFunc(int)` |  |
| `static void glDepthMask(boolean)` |  |
| `static void glDepthRangef(float, float)` |  |
| `static void glDetachShader(int, int)` |  |
| `static void glDisable(int)` |  |
| `static void glDisableVertexAttribArray(int)` |  |
| `static void glDrawArrays(int, int, int)` |  |
| `static void glDrawElements(int, int, int, int)` |  |
| `static void glDrawElements(int, int, int, java.nio.Buffer)` |  |
| `static void glEnable(int)` |  |
| `static void glEnableVertexAttribArray(int)` |  |
| `static void glFinish()` |  |
| `static void glFlush()` |  |
| `static void glFramebufferRenderbuffer(int, int, int, int)` |  |
| `static void glFramebufferTexture2D(int, int, int, int, int)` |  |
| `static void glFrontFace(int)` |  |
| `static void glGenBuffers(int, int[], int)` |  |
| `static void glGenBuffers(int, java.nio.IntBuffer)` |  |
| `static void glGenFramebuffers(int, int[], int)` |  |
| `static void glGenFramebuffers(int, java.nio.IntBuffer)` |  |
| `static void glGenRenderbuffers(int, int[], int)` |  |
| `static void glGenRenderbuffers(int, java.nio.IntBuffer)` |  |
| `static void glGenTextures(int, int[], int)` |  |
| `static void glGenTextures(int, java.nio.IntBuffer)` |  |
| `static void glGenerateMipmap(int)` |  |
| `static void glGetActiveAttrib(int, int, int, int[], int, int[], int, int[], int, byte[], int)` |  |
| `static String glGetActiveAttrib(int, int, int[], int, int[], int)` |  |
| `static String glGetActiveAttrib(int, int, java.nio.IntBuffer, java.nio.IntBuffer)` |  |
| `static void glGetActiveUniform(int, int, int, int[], int, int[], int, int[], int, byte[], int)` |  |
| `static String glGetActiveUniform(int, int, int[], int, int[], int)` |  |
| `static String glGetActiveUniform(int, int, java.nio.IntBuffer, java.nio.IntBuffer)` |  |
| `static void glGetAttachedShaders(int, int, int[], int, int[], int)` |  |
| `static void glGetAttachedShaders(int, int, java.nio.IntBuffer, java.nio.IntBuffer)` |  |
| `static int glGetAttribLocation(int, String)` |  |
| `static void glGetBooleanv(int, boolean[], int)` |  |
| `static void glGetBooleanv(int, java.nio.IntBuffer)` |  |
| `static void glGetBufferParameteriv(int, int, int[], int)` |  |
| `static void glGetBufferParameteriv(int, int, java.nio.IntBuffer)` |  |
| `static int glGetError()` |  |
| `static void glGetFloatv(int, float[], int)` |  |
| `static void glGetFloatv(int, java.nio.FloatBuffer)` |  |
| `static void glGetFramebufferAttachmentParameteriv(int, int, int, int[], int)` |  |
| `static void glGetFramebufferAttachmentParameteriv(int, int, int, java.nio.IntBuffer)` |  |
| `static void glGetIntegerv(int, int[], int)` |  |
| `static void glGetIntegerv(int, java.nio.IntBuffer)` |  |
| `static String glGetProgramInfoLog(int)` |  |
| `static void glGetProgramiv(int, int, int[], int)` |  |
| `static void glGetProgramiv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetRenderbufferParameteriv(int, int, int[], int)` |  |
| `static void glGetRenderbufferParameteriv(int, int, java.nio.IntBuffer)` |  |
| `static String glGetShaderInfoLog(int)` |  |
| `static void glGetShaderPrecisionFormat(int, int, int[], int, int[], int)` |  |
| `static void glGetShaderPrecisionFormat(int, int, java.nio.IntBuffer, java.nio.IntBuffer)` |  |
| `static void glGetShaderSource(int, int, int[], int, byte[], int)` |  |
| `static String glGetShaderSource(int)` |  |
| `static void glGetShaderiv(int, int, int[], int)` |  |
| `static void glGetShaderiv(int, int, java.nio.IntBuffer)` |  |
| `static String glGetString(int)` |  |
| `static void glGetTexParameterfv(int, int, float[], int)` |  |
| `static void glGetTexParameterfv(int, int, java.nio.FloatBuffer)` |  |
| `static void glGetTexParameteriv(int, int, int[], int)` |  |
| `static void glGetTexParameteriv(int, int, java.nio.IntBuffer)` |  |
| `static int glGetUniformLocation(int, String)` |  |
| `static void glGetUniformfv(int, int, float[], int)` |  |
| `static void glGetUniformfv(int, int, java.nio.FloatBuffer)` |  |
| `static void glGetUniformiv(int, int, int[], int)` |  |
| `static void glGetUniformiv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetVertexAttribfv(int, int, float[], int)` |  |
| `static void glGetVertexAttribfv(int, int, java.nio.FloatBuffer)` |  |
| `static void glGetVertexAttribiv(int, int, int[], int)` |  |
| `static void glGetVertexAttribiv(int, int, java.nio.IntBuffer)` |  |
| `static void glHint(int, int)` |  |
| `static boolean glIsBuffer(int)` |  |
| `static boolean glIsEnabled(int)` |  |
| `static boolean glIsFramebuffer(int)` |  |
| `static boolean glIsProgram(int)` |  |
| `static boolean glIsRenderbuffer(int)` |  |
| `static boolean glIsShader(int)` |  |
| `static boolean glIsTexture(int)` |  |
| `static void glLineWidth(float)` |  |
| `static void glLinkProgram(int)` |  |
| `static void glPixelStorei(int, int)` |  |
| `static void glPolygonOffset(float, float)` |  |
| `static void glReadPixels(int, int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glReleaseShaderCompiler()` |  |
| `static void glRenderbufferStorage(int, int, int, int)` |  |
| `static void glSampleCoverage(float, boolean)` |  |
| `static void glScissor(int, int, int, int)` |  |
| `static void glShaderBinary(int, int[], int, int, java.nio.Buffer, int)` |  |
| `static void glShaderBinary(int, java.nio.IntBuffer, int, java.nio.Buffer, int)` |  |
| `static void glShaderSource(int, String)` |  |
| `static void glStencilFunc(int, int, int)` |  |
| `static void glStencilFuncSeparate(int, int, int, int)` |  |
| `static void glStencilMask(int)` |  |
| `static void glStencilMaskSeparate(int, int)` |  |
| `static void glStencilOp(int, int, int)` |  |
| `static void glStencilOpSeparate(int, int, int, int)` |  |
| `static void glTexImage2D(int, int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glTexParameterf(int, int, float)` |  |
| `static void glTexParameterfv(int, int, float[], int)` |  |
| `static void glTexParameterfv(int, int, java.nio.FloatBuffer)` |  |
| `static void glTexParameteri(int, int, int)` |  |
| `static void glTexParameteriv(int, int, int[], int)` |  |
| `static void glTexParameteriv(int, int, java.nio.IntBuffer)` |  |
| `static void glTexSubImage2D(int, int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glUniform1f(int, float)` |  |
| `static void glUniform1fv(int, int, float[], int)` |  |
| `static void glUniform1fv(int, int, java.nio.FloatBuffer)` |  |
| `static void glUniform1i(int, int)` |  |
| `static void glUniform1iv(int, int, int[], int)` |  |
| `static void glUniform1iv(int, int, java.nio.IntBuffer)` |  |
| `static void glUniform2f(int, float, float)` |  |
| `static void glUniform2fv(int, int, float[], int)` |  |
| `static void glUniform2fv(int, int, java.nio.FloatBuffer)` |  |
| `static void glUniform2i(int, int, int)` |  |
| `static void glUniform2iv(int, int, int[], int)` |  |
| `static void glUniform2iv(int, int, java.nio.IntBuffer)` |  |
| `static void glUniform3f(int, float, float, float)` |  |
| `static void glUniform3fv(int, int, float[], int)` |  |
| `static void glUniform3fv(int, int, java.nio.FloatBuffer)` |  |
| `static void glUniform3i(int, int, int, int)` |  |
| `static void glUniform3iv(int, int, int[], int)` |  |
| `static void glUniform3iv(int, int, java.nio.IntBuffer)` |  |
| `static void glUniform4f(int, float, float, float, float)` |  |
| `static void glUniform4fv(int, int, float[], int)` |  |
| `static void glUniform4fv(int, int, java.nio.FloatBuffer)` |  |
| `static void glUniform4i(int, int, int, int, int)` |  |
| `static void glUniform4iv(int, int, int[], int)` |  |
| `static void glUniform4iv(int, int, java.nio.IntBuffer)` |  |
| `static void glUniformMatrix2fv(int, int, boolean, float[], int)` |  |
| `static void glUniformMatrix2fv(int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glUniformMatrix3fv(int, int, boolean, float[], int)` |  |
| `static void glUniformMatrix3fv(int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glUniformMatrix4fv(int, int, boolean, float[], int)` |  |
| `static void glUniformMatrix4fv(int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glUseProgram(int)` |  |
| `static void glValidateProgram(int)` |  |
| `static void glVertexAttrib1f(int, float)` |  |
| `static void glVertexAttrib1fv(int, float[], int)` |  |
| `static void glVertexAttrib1fv(int, java.nio.FloatBuffer)` |  |
| `static void glVertexAttrib2f(int, float, float)` |  |
| `static void glVertexAttrib2fv(int, float[], int)` |  |
| `static void glVertexAttrib2fv(int, java.nio.FloatBuffer)` |  |
| `static void glVertexAttrib3f(int, float, float, float)` |  |
| `static void glVertexAttrib3fv(int, float[], int)` |  |
| `static void glVertexAttrib3fv(int, java.nio.FloatBuffer)` |  |
| `static void glVertexAttrib4f(int, float, float, float, float)` |  |
| `static void glVertexAttrib4fv(int, float[], int)` |  |
| `static void glVertexAttrib4fv(int, java.nio.FloatBuffer)` |  |
| `static void glVertexAttribPointer(int, int, int, boolean, int, int)` |  |
| `static void glVertexAttribPointer(int, int, int, boolean, int, java.nio.Buffer)` |  |
| `static void glViewport(int, int, int, int)` |  |

---

### `class GLES30`

- **继承：** `android.opengl.GLES20`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GLES30()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int GL_ACTIVE_UNIFORM_BLOCKS = 35382` |  |
| `static final int GL_ACTIVE_UNIFORM_BLOCK_MAX_NAME_LENGTH = 35381` |  |
| `static final int GL_ALREADY_SIGNALED = 37146` |  |
| `static final int GL_ANY_SAMPLES_PASSED = 35887` |  |
| `static final int GL_ANY_SAMPLES_PASSED_CONSERVATIVE = 36202` |  |
| `static final int GL_BLUE = 6405` |  |
| `static final int GL_BUFFER_ACCESS_FLAGS = 37151` |  |
| `static final int GL_BUFFER_MAPPED = 35004` |  |
| `static final int GL_BUFFER_MAP_LENGTH = 37152` |  |
| `static final int GL_BUFFER_MAP_OFFSET = 37153` |  |
| `static final int GL_BUFFER_MAP_POINTER = 35005` |  |
| `static final int GL_COLOR = 6144` |  |
| `static final int GL_COLOR_ATTACHMENT1 = 36065` |  |
| `static final int GL_COLOR_ATTACHMENT10 = 36074` |  |
| `static final int GL_COLOR_ATTACHMENT11 = 36075` |  |
| `static final int GL_COLOR_ATTACHMENT12 = 36076` |  |
| `static final int GL_COLOR_ATTACHMENT13 = 36077` |  |
| `static final int GL_COLOR_ATTACHMENT14 = 36078` |  |
| `static final int GL_COLOR_ATTACHMENT15 = 36079` |  |
| `static final int GL_COLOR_ATTACHMENT2 = 36066` |  |
| `static final int GL_COLOR_ATTACHMENT3 = 36067` |  |
| `static final int GL_COLOR_ATTACHMENT4 = 36068` |  |
| `static final int GL_COLOR_ATTACHMENT5 = 36069` |  |
| `static final int GL_COLOR_ATTACHMENT6 = 36070` |  |
| `static final int GL_COLOR_ATTACHMENT7 = 36071` |  |
| `static final int GL_COLOR_ATTACHMENT8 = 36072` |  |
| `static final int GL_COLOR_ATTACHMENT9 = 36073` |  |
| `static final int GL_COMPARE_REF_TO_TEXTURE = 34894` |  |
| `static final int GL_COMPRESSED_R11_EAC = 37488` |  |
| `static final int GL_COMPRESSED_RG11_EAC = 37490` |  |
| `static final int GL_COMPRESSED_RGB8_ETC2 = 37492` |  |
| `static final int GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2 = 37494` |  |
| `static final int GL_COMPRESSED_RGBA8_ETC2_EAC = 37496` |  |
| `static final int GL_COMPRESSED_SIGNED_R11_EAC = 37489` |  |
| `static final int GL_COMPRESSED_SIGNED_RG11_EAC = 37491` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC = 37497` |  |
| `static final int GL_COMPRESSED_SRGB8_ETC2 = 37493` |  |
| `static final int GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2 = 37495` |  |
| `static final int GL_CONDITION_SATISFIED = 37148` |  |
| `static final int GL_COPY_READ_BUFFER = 36662` |  |
| `static final int GL_COPY_READ_BUFFER_BINDING = 36662` |  |
| `static final int GL_COPY_WRITE_BUFFER = 36663` |  |
| `static final int GL_COPY_WRITE_BUFFER_BINDING = 36663` |  |
| `static final int GL_CURRENT_QUERY = 34917` |  |
| `static final int GL_DEPTH = 6145` |  |
| `static final int GL_DEPTH24_STENCIL8 = 35056` |  |
| `static final int GL_DEPTH32F_STENCIL8 = 36013` |  |
| `static final int GL_DEPTH_COMPONENT24 = 33190` |  |
| `static final int GL_DEPTH_COMPONENT32F = 36012` |  |
| `static final int GL_DEPTH_STENCIL = 34041` |  |
| `static final int GL_DEPTH_STENCIL_ATTACHMENT = 33306` |  |
| `static final int GL_DRAW_BUFFER0 = 34853` |  |
| `static final int GL_DRAW_BUFFER1 = 34854` |  |
| `static final int GL_DRAW_BUFFER10 = 34863` |  |
| `static final int GL_DRAW_BUFFER11 = 34864` |  |
| `static final int GL_DRAW_BUFFER12 = 34865` |  |
| `static final int GL_DRAW_BUFFER13 = 34866` |  |
| `static final int GL_DRAW_BUFFER14 = 34867` |  |
| `static final int GL_DRAW_BUFFER15 = 34868` |  |
| `static final int GL_DRAW_BUFFER2 = 34855` |  |
| `static final int GL_DRAW_BUFFER3 = 34856` |  |
| `static final int GL_DRAW_BUFFER4 = 34857` |  |
| `static final int GL_DRAW_BUFFER5 = 34858` |  |
| `static final int GL_DRAW_BUFFER6 = 34859` |  |
| `static final int GL_DRAW_BUFFER7 = 34860` |  |
| `static final int GL_DRAW_BUFFER8 = 34861` |  |
| `static final int GL_DRAW_BUFFER9 = 34862` |  |
| `static final int GL_DRAW_FRAMEBUFFER = 36009` |  |
| `static final int GL_DRAW_FRAMEBUFFER_BINDING = 36006` |  |
| `static final int GL_DYNAMIC_COPY = 35050` |  |
| `static final int GL_DYNAMIC_READ = 35049` |  |
| `static final int GL_FLOAT_32_UNSIGNED_INT_24_8_REV = 36269` |  |
| `static final int GL_FLOAT_MAT2x3 = 35685` |  |
| `static final int GL_FLOAT_MAT2x4 = 35686` |  |
| `static final int GL_FLOAT_MAT3x2 = 35687` |  |
| `static final int GL_FLOAT_MAT3x4 = 35688` |  |
| `static final int GL_FLOAT_MAT4x2 = 35689` |  |
| `static final int GL_FLOAT_MAT4x3 = 35690` |  |
| `static final int GL_FRAGMENT_SHADER_DERIVATIVE_HINT = 35723` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_ALPHA_SIZE = 33301` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_BLUE_SIZE = 33300` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_COLOR_ENCODING = 33296` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_COMPONENT_TYPE = 33297` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_DEPTH_SIZE = 33302` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_GREEN_SIZE = 33299` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_RED_SIZE = 33298` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_STENCIL_SIZE = 33303` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LAYER = 36052` |  |
| `static final int GL_FRAMEBUFFER_DEFAULT = 33304` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE = 36182` |  |
| `static final int GL_FRAMEBUFFER_UNDEFINED = 33305` |  |
| `static final int GL_GREEN = 6404` |  |
| `static final int GL_HALF_FLOAT = 5131` |  |
| `static final int GL_INTERLEAVED_ATTRIBS = 35980` |  |
| `static final int GL_INT_2_10_10_10_REV = 36255` |  |
| `static final int GL_INT_SAMPLER_2D = 36298` |  |
| `static final int GL_INT_SAMPLER_2D_ARRAY = 36303` |  |
| `static final int GL_INT_SAMPLER_3D = 36299` |  |
| `static final int GL_INT_SAMPLER_CUBE = 36300` |  |
| `static final int GL_INVALID_INDEX = -1` |  |
| `static final int GL_MAJOR_VERSION = 33307` |  |
| `static final int GL_MAP_FLUSH_EXPLICIT_BIT = 16` |  |
| `static final int GL_MAP_INVALIDATE_BUFFER_BIT = 8` |  |
| `static final int GL_MAP_INVALIDATE_RANGE_BIT = 4` |  |
| `static final int GL_MAP_READ_BIT = 1` |  |
| `static final int GL_MAP_UNSYNCHRONIZED_BIT = 32` |  |
| `static final int GL_MAP_WRITE_BIT = 2` |  |
| `static final int GL_MAX = 32776` |  |
| `static final int GL_MAX_3D_TEXTURE_SIZE = 32883` |  |
| `static final int GL_MAX_ARRAY_TEXTURE_LAYERS = 35071` |  |
| `static final int GL_MAX_COLOR_ATTACHMENTS = 36063` |  |
| `static final int GL_MAX_COMBINED_FRAGMENT_UNIFORM_COMPONENTS = 35379` |  |
| `static final int GL_MAX_COMBINED_UNIFORM_BLOCKS = 35374` |  |
| `static final int GL_MAX_COMBINED_VERTEX_UNIFORM_COMPONENTS = 35377` |  |
| `static final int GL_MAX_DRAW_BUFFERS = 34852` |  |
| `static final int GL_MAX_ELEMENTS_INDICES = 33001` |  |
| `static final int GL_MAX_ELEMENTS_VERTICES = 33000` |  |
| `static final int GL_MAX_ELEMENT_INDEX = 36203` |  |
| `static final int GL_MAX_FRAGMENT_INPUT_COMPONENTS = 37157` |  |
| `static final int GL_MAX_FRAGMENT_UNIFORM_BLOCKS = 35373` |  |
| `static final int GL_MAX_FRAGMENT_UNIFORM_COMPONENTS = 35657` |  |
| `static final int GL_MAX_PROGRAM_TEXEL_OFFSET = 35077` |  |
| `static final int GL_MAX_SAMPLES = 36183` |  |
| `static final int GL_MAX_SERVER_WAIT_TIMEOUT = 37137` |  |
| `static final int GL_MAX_TEXTURE_LOD_BIAS = 34045` |  |
| `static final int GL_MAX_TRANSFORM_FEEDBACK_INTERLEAVED_COMPONENTS = 35978` |  |
| `static final int GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_ATTRIBS = 35979` |  |
| `static final int GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_COMPONENTS = 35968` |  |
| `static final int GL_MAX_UNIFORM_BLOCK_SIZE = 35376` |  |
| `static final int GL_MAX_UNIFORM_BUFFER_BINDINGS = 35375` |  |
| `static final int GL_MAX_VARYING_COMPONENTS = 35659` |  |
| `static final int GL_MAX_VERTEX_OUTPUT_COMPONENTS = 37154` |  |
| `static final int GL_MAX_VERTEX_UNIFORM_BLOCKS = 35371` |  |
| `static final int GL_MAX_VERTEX_UNIFORM_COMPONENTS = 35658` |  |
| `static final int GL_MIN = 32775` |  |
| `static final int GL_MINOR_VERSION = 33308` |  |
| `static final int GL_MIN_PROGRAM_TEXEL_OFFSET = 35076` |  |
| `static final int GL_NUM_EXTENSIONS = 33309` |  |
| `static final int GL_NUM_PROGRAM_BINARY_FORMATS = 34814` |  |
| `static final int GL_NUM_SAMPLE_COUNTS = 37760` |  |
| `static final int GL_OBJECT_TYPE = 37138` |  |
| `static final int GL_PACK_ROW_LENGTH = 3330` |  |
| `static final int GL_PACK_SKIP_PIXELS = 3332` |  |
| `static final int GL_PACK_SKIP_ROWS = 3331` |  |
| `static final int GL_PIXEL_PACK_BUFFER = 35051` |  |
| `static final int GL_PIXEL_PACK_BUFFER_BINDING = 35053` |  |
| `static final int GL_PIXEL_UNPACK_BUFFER = 35052` |  |
| `static final int GL_PIXEL_UNPACK_BUFFER_BINDING = 35055` |  |
| `static final int GL_PRIMITIVE_RESTART_FIXED_INDEX = 36201` |  |
| `static final int GL_PROGRAM_BINARY_FORMATS = 34815` |  |
| `static final int GL_PROGRAM_BINARY_LENGTH = 34625` |  |
| `static final int GL_PROGRAM_BINARY_RETRIEVABLE_HINT = 33367` |  |
| `static final int GL_QUERY_RESULT = 34918` |  |
| `static final int GL_QUERY_RESULT_AVAILABLE = 34919` |  |
| `static final int GL_R11F_G11F_B10F = 35898` |  |
| `static final int GL_R16F = 33325` |  |
| `static final int GL_R16I = 33331` |  |
| `static final int GL_R16UI = 33332` |  |
| `static final int GL_R32F = 33326` |  |
| `static final int GL_R32I = 33333` |  |
| `static final int GL_R32UI = 33334` |  |
| `static final int GL_R8 = 33321` |  |
| `static final int GL_R8I = 33329` |  |
| `static final int GL_R8UI = 33330` |  |
| `static final int GL_R8_SNORM = 36756` |  |
| `static final int GL_RASTERIZER_DISCARD = 35977` |  |
| `static final int GL_READ_BUFFER = 3074` |  |
| `static final int GL_READ_FRAMEBUFFER = 36008` |  |
| `static final int GL_READ_FRAMEBUFFER_BINDING = 36010` |  |
| `static final int GL_RED = 6403` |  |
| `static final int GL_RED_INTEGER = 36244` |  |
| `static final int GL_RENDERBUFFER_SAMPLES = 36011` |  |
| `static final int GL_RG = 33319` |  |
| `static final int GL_RG16F = 33327` |  |
| `static final int GL_RG16I = 33337` |  |
| `static final int GL_RG16UI = 33338` |  |
| `static final int GL_RG32F = 33328` |  |
| `static final int GL_RG32I = 33339` |  |
| `static final int GL_RG32UI = 33340` |  |
| `static final int GL_RG8 = 33323` |  |
| `static final int GL_RG8I = 33335` |  |
| `static final int GL_RG8UI = 33336` |  |
| `static final int GL_RG8_SNORM = 36757` |  |
| `static final int GL_RGB10_A2 = 32857` |  |
| `static final int GL_RGB10_A2UI = 36975` |  |
| `static final int GL_RGB16F = 34843` |  |
| `static final int GL_RGB16I = 36233` |  |
| `static final int GL_RGB16UI = 36215` |  |
| `static final int GL_RGB32F = 34837` |  |
| `static final int GL_RGB32I = 36227` |  |
| `static final int GL_RGB32UI = 36209` |  |
| `static final int GL_RGB8 = 32849` |  |
| `static final int GL_RGB8I = 36239` |  |
| `static final int GL_RGB8UI = 36221` |  |
| `static final int GL_RGB8_SNORM = 36758` |  |
| `static final int GL_RGB9_E5 = 35901` |  |
| `static final int GL_RGBA16F = 34842` |  |
| `static final int GL_RGBA16I = 36232` |  |
| `static final int GL_RGBA16UI = 36214` |  |
| `static final int GL_RGBA32F = 34836` |  |
| `static final int GL_RGBA32I = 36226` |  |
| `static final int GL_RGBA32UI = 36208` |  |
| `static final int GL_RGBA8 = 32856` |  |
| `static final int GL_RGBA8I = 36238` |  |
| `static final int GL_RGBA8UI = 36220` |  |
| `static final int GL_RGBA8_SNORM = 36759` |  |
| `static final int GL_RGBA_INTEGER = 36249` |  |
| `static final int GL_RGB_INTEGER = 36248` |  |
| `static final int GL_RG_INTEGER = 33320` |  |
| `static final int GL_SAMPLER_2D_ARRAY = 36289` |  |
| `static final int GL_SAMPLER_2D_ARRAY_SHADOW = 36292` |  |
| `static final int GL_SAMPLER_2D_SHADOW = 35682` |  |
| `static final int GL_SAMPLER_3D = 35679` |  |
| `static final int GL_SAMPLER_BINDING = 35097` |  |
| `static final int GL_SAMPLER_CUBE_SHADOW = 36293` |  |
| `static final int GL_SEPARATE_ATTRIBS = 35981` |  |
| `static final int GL_SIGNALED = 37145` |  |
| `static final int GL_SIGNED_NORMALIZED = 36764` |  |
| `static final int GL_SRGB = 35904` |  |
| `static final int GL_SRGB8 = 35905` |  |
| `static final int GL_SRGB8_ALPHA8 = 35907` |  |
| `static final int GL_STATIC_COPY = 35046` |  |
| `static final int GL_STATIC_READ = 35045` |  |
| `static final int GL_STENCIL = 6146` |  |
| `static final int GL_STREAM_COPY = 35042` |  |
| `static final int GL_STREAM_READ = 35041` |  |
| `static final int GL_SYNC_CONDITION = 37139` |  |
| `static final int GL_SYNC_FENCE = 37142` |  |
| `static final int GL_SYNC_FLAGS = 37141` |  |
| `static final int GL_SYNC_FLUSH_COMMANDS_BIT = 1` |  |
| `static final int GL_SYNC_GPU_COMMANDS_COMPLETE = 37143` |  |
| `static final int GL_SYNC_STATUS = 37140` |  |
| `static final int GL_TEXTURE_2D_ARRAY = 35866` |  |
| `static final int GL_TEXTURE_3D = 32879` |  |
| `static final int GL_TEXTURE_BASE_LEVEL = 33084` |  |
| `static final int GL_TEXTURE_BINDING_2D_ARRAY = 35869` |  |
| `static final int GL_TEXTURE_BINDING_3D = 32874` |  |
| `static final int GL_TEXTURE_COMPARE_FUNC = 34893` |  |
| `static final int GL_TEXTURE_COMPARE_MODE = 34892` |  |
| `static final int GL_TEXTURE_IMMUTABLE_FORMAT = 37167` |  |
| `static final int GL_TEXTURE_IMMUTABLE_LEVELS = 33503` |  |
| `static final int GL_TEXTURE_MAX_LEVEL = 33085` |  |
| `static final int GL_TEXTURE_MAX_LOD = 33083` |  |
| `static final int GL_TEXTURE_MIN_LOD = 33082` |  |
| `static final int GL_TEXTURE_SWIZZLE_A = 36421` |  |
| `static final int GL_TEXTURE_SWIZZLE_B = 36420` |  |
| `static final int GL_TEXTURE_SWIZZLE_G = 36419` |  |
| `static final int GL_TEXTURE_SWIZZLE_R = 36418` |  |
| `static final int GL_TEXTURE_WRAP_R = 32882` |  |
| `static final int GL_TIMEOUT_EXPIRED = 37147` |  |
| `static final long GL_TIMEOUT_IGNORED = -1L` |  |
| `static final int GL_TRANSFORM_FEEDBACK = 36386` |  |
| `static final int GL_TRANSFORM_FEEDBACK_ACTIVE = 36388` |  |
| `static final int GL_TRANSFORM_FEEDBACK_BINDING = 36389` |  |
| `static final int GL_TRANSFORM_FEEDBACK_BUFFER = 35982` |  |
| `static final int GL_TRANSFORM_FEEDBACK_BUFFER_BINDING = 35983` |  |
| `static final int GL_TRANSFORM_FEEDBACK_BUFFER_MODE = 35967` |  |
| `static final int GL_TRANSFORM_FEEDBACK_BUFFER_SIZE = 35973` |  |
| `static final int GL_TRANSFORM_FEEDBACK_BUFFER_START = 35972` |  |
| `static final int GL_TRANSFORM_FEEDBACK_PAUSED = 36387` |  |
| `static final int GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN = 35976` |  |
| `static final int GL_TRANSFORM_FEEDBACK_VARYINGS = 35971` |  |
| `static final int GL_TRANSFORM_FEEDBACK_VARYING_MAX_LENGTH = 35958` |  |
| `static final int GL_UNIFORM_ARRAY_STRIDE = 35388` |  |
| `static final int GL_UNIFORM_BLOCK_ACTIVE_UNIFORMS = 35394` |  |
| `static final int GL_UNIFORM_BLOCK_ACTIVE_UNIFORM_INDICES = 35395` |  |
| `static final int GL_UNIFORM_BLOCK_BINDING = 35391` |  |
| `static final int GL_UNIFORM_BLOCK_DATA_SIZE = 35392` |  |
| `static final int GL_UNIFORM_BLOCK_INDEX = 35386` |  |
| `static final int GL_UNIFORM_BLOCK_NAME_LENGTH = 35393` |  |
| `static final int GL_UNIFORM_BLOCK_REFERENCED_BY_FRAGMENT_SHADER = 35398` |  |
| `static final int GL_UNIFORM_BLOCK_REFERENCED_BY_VERTEX_SHADER = 35396` |  |
| `static final int GL_UNIFORM_BUFFER = 35345` |  |
| `static final int GL_UNIFORM_BUFFER_BINDING = 35368` |  |
| `static final int GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT = 35380` |  |
| `static final int GL_UNIFORM_BUFFER_SIZE = 35370` |  |
| `static final int GL_UNIFORM_BUFFER_START = 35369` |  |
| `static final int GL_UNIFORM_IS_ROW_MAJOR = 35390` |  |
| `static final int GL_UNIFORM_MATRIX_STRIDE = 35389` |  |
| `static final int GL_UNIFORM_NAME_LENGTH = 35385` |  |
| `static final int GL_UNIFORM_OFFSET = 35387` |  |
| `static final int GL_UNIFORM_SIZE = 35384` |  |
| `static final int GL_UNIFORM_TYPE = 35383` |  |
| `static final int GL_UNPACK_IMAGE_HEIGHT = 32878` |  |
| `static final int GL_UNPACK_ROW_LENGTH = 3314` |  |
| `static final int GL_UNPACK_SKIP_IMAGES = 32877` |  |
| `static final int GL_UNPACK_SKIP_PIXELS = 3316` |  |
| `static final int GL_UNPACK_SKIP_ROWS = 3315` |  |
| `static final int GL_UNSIGNALED = 37144` |  |
| `static final int GL_UNSIGNED_INT_10F_11F_11F_REV = 35899` |  |
| `static final int GL_UNSIGNED_INT_24_8 = 34042` |  |
| `static final int GL_UNSIGNED_INT_2_10_10_10_REV = 33640` |  |
| `static final int GL_UNSIGNED_INT_5_9_9_9_REV = 35902` |  |
| `static final int GL_UNSIGNED_INT_SAMPLER_2D = 36306` |  |
| `static final int GL_UNSIGNED_INT_SAMPLER_2D_ARRAY = 36311` |  |
| `static final int GL_UNSIGNED_INT_SAMPLER_3D = 36307` |  |
| `static final int GL_UNSIGNED_INT_SAMPLER_CUBE = 36308` |  |
| `static final int GL_UNSIGNED_INT_VEC2 = 36294` |  |
| `static final int GL_UNSIGNED_INT_VEC3 = 36295` |  |
| `static final int GL_UNSIGNED_INT_VEC4 = 36296` |  |
| `static final int GL_UNSIGNED_NORMALIZED = 35863` |  |
| `static final int GL_VERTEX_ARRAY_BINDING = 34229` |  |
| `static final int GL_VERTEX_ATTRIB_ARRAY_DIVISOR = 35070` |  |
| `static final int GL_VERTEX_ATTRIB_ARRAY_INTEGER = 35069` |  |
| `static final int GL_WAIT_FAILED = 37149` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void glBeginQuery(int, int)` |  |
| `static void glBeginTransformFeedback(int)` |  |
| `static void glBindBufferBase(int, int, int)` |  |
| `static void glBindBufferRange(int, int, int, int, int)` |  |
| `static void glBindSampler(int, int)` |  |
| `static void glBindTransformFeedback(int, int)` |  |
| `static void glBindVertexArray(int)` |  |
| `static void glBlitFramebuffer(int, int, int, int, int, int, int, int, int, int)` |  |
| `static void glClearBufferfi(int, int, float, int)` |  |
| `static void glClearBufferfv(int, int, float[], int)` |  |
| `static void glClearBufferfv(int, int, java.nio.FloatBuffer)` |  |
| `static void glClearBufferiv(int, int, int[], int)` |  |
| `static void glClearBufferiv(int, int, java.nio.IntBuffer)` |  |
| `static void glClearBufferuiv(int, int, int[], int)` |  |
| `static void glClearBufferuiv(int, int, java.nio.IntBuffer)` |  |
| `static int glClientWaitSync(long, int, long)` |  |
| `static void glCompressedTexImage3D(int, int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glCompressedTexImage3D(int, int, int, int, int, int, int, int, int)` |  |
| `static void glCompressedTexSubImage3D(int, int, int, int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glCompressedTexSubImage3D(int, int, int, int, int, int, int, int, int, int, int)` |  |
| `static void glCopyBufferSubData(int, int, int, int, int)` |  |
| `static void glCopyTexSubImage3D(int, int, int, int, int, int, int, int, int)` |  |
| `static void glDeleteQueries(int, int[], int)` |  |
| `static void glDeleteQueries(int, java.nio.IntBuffer)` |  |
| `static void glDeleteSamplers(int, int[], int)` |  |
| `static void glDeleteSamplers(int, java.nio.IntBuffer)` |  |
| `static void glDeleteSync(long)` |  |
| `static void glDeleteTransformFeedbacks(int, int[], int)` |  |
| `static void glDeleteTransformFeedbacks(int, java.nio.IntBuffer)` |  |
| `static void glDeleteVertexArrays(int, int[], int)` |  |
| `static void glDeleteVertexArrays(int, java.nio.IntBuffer)` |  |
| `static void glDrawArraysInstanced(int, int, int, int)` |  |
| `static void glDrawBuffers(int, int[], int)` |  |
| `static void glDrawBuffers(int, java.nio.IntBuffer)` |  |
| `static void glDrawElementsInstanced(int, int, int, java.nio.Buffer, int)` |  |
| `static void glDrawElementsInstanced(int, int, int, int, int)` |  |
| `static void glDrawRangeElements(int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glDrawRangeElements(int, int, int, int, int, int)` |  |
| `static void glEndQuery(int)` |  |
| `static void glEndTransformFeedback()` |  |
| `static long glFenceSync(int, int)` |  |
| `static void glFlushMappedBufferRange(int, int, int)` |  |
| `static void glFramebufferTextureLayer(int, int, int, int, int)` |  |
| `static void glGenQueries(int, int[], int)` |  |
| `static void glGenQueries(int, java.nio.IntBuffer)` |  |
| `static void glGenSamplers(int, int[], int)` |  |
| `static void glGenSamplers(int, java.nio.IntBuffer)` |  |
| `static void glGenTransformFeedbacks(int, int[], int)` |  |
| `static void glGenTransformFeedbacks(int, java.nio.IntBuffer)` |  |
| `static void glGenVertexArrays(int, int[], int)` |  |
| `static void glGenVertexArrays(int, java.nio.IntBuffer)` |  |
| `static void glGetActiveUniformBlockName(int, int, int, int[], int, byte[], int)` |  |
| `static void glGetActiveUniformBlockName(int, int, java.nio.Buffer, java.nio.Buffer)` |  |
| `static String glGetActiveUniformBlockName(int, int)` |  |
| `static void glGetActiveUniformBlockiv(int, int, int, int[], int)` |  |
| `static void glGetActiveUniformBlockiv(int, int, int, java.nio.IntBuffer)` |  |
| `static void glGetActiveUniformsiv(int, int, int[], int, int, int[], int)` |  |
| `static void glGetActiveUniformsiv(int, int, java.nio.IntBuffer, int, java.nio.IntBuffer)` |  |
| `static void glGetBufferParameteri64v(int, int, long[], int)` |  |
| `static void glGetBufferParameteri64v(int, int, java.nio.LongBuffer)` |  |
| `static java.nio.Buffer glGetBufferPointerv(int, int)` |  |
| `static int glGetFragDataLocation(int, String)` |  |
| `static void glGetInteger64i_v(int, int, long[], int)` |  |
| `static void glGetInteger64i_v(int, int, java.nio.LongBuffer)` |  |
| `static void glGetInteger64v(int, long[], int)` |  |
| `static void glGetInteger64v(int, java.nio.LongBuffer)` |  |
| `static void glGetIntegeri_v(int, int, int[], int)` |  |
| `static void glGetIntegeri_v(int, int, java.nio.IntBuffer)` |  |
| `static void glGetInternalformativ(int, int, int, int, int[], int)` |  |
| `static void glGetInternalformativ(int, int, int, int, java.nio.IntBuffer)` |  |
| `static void glGetProgramBinary(int, int, int[], int, int[], int, java.nio.Buffer)` |  |
| `static void glGetProgramBinary(int, int, java.nio.IntBuffer, java.nio.IntBuffer, java.nio.Buffer)` |  |
| `static void glGetQueryObjectuiv(int, int, int[], int)` |  |
| `static void glGetQueryObjectuiv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetQueryiv(int, int, int[], int)` |  |
| `static void glGetQueryiv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetSamplerParameterfv(int, int, float[], int)` |  |
| `static void glGetSamplerParameterfv(int, int, java.nio.FloatBuffer)` |  |
| `static void glGetSamplerParameteriv(int, int, int[], int)` |  |
| `static void glGetSamplerParameteriv(int, int, java.nio.IntBuffer)` |  |
| `static String glGetStringi(int, int)` |  |
| `static void glGetSynciv(long, int, int, int[], int, int[], int)` |  |
| `static void glGetSynciv(long, int, int, java.nio.IntBuffer, java.nio.IntBuffer)` |  |
| `static void glGetTransformFeedbackVarying(int, int, int, int[], int, int[], int, int[], int, byte[], int)` |  |
| `static void glGetTransformFeedbackVarying(int, int, int, java.nio.IntBuffer, java.nio.IntBuffer, java.nio.IntBuffer, java.nio.ByteBuffer)` |  |
| `static String glGetTransformFeedbackVarying(int, int, int[], int, int[], int)` |  |
| `static String glGetTransformFeedbackVarying(int, int, java.nio.IntBuffer, java.nio.IntBuffer)` |  |
| `static int glGetUniformBlockIndex(int, String)` |  |
| `static void glGetUniformIndices(int, String[], int[], int)` |  |
| `static void glGetUniformIndices(int, String[], java.nio.IntBuffer)` |  |
| `static void glGetUniformuiv(int, int, int[], int)` |  |
| `static void glGetUniformuiv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetVertexAttribIiv(int, int, int[], int)` |  |
| `static void glGetVertexAttribIiv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetVertexAttribIuiv(int, int, int[], int)` |  |
| `static void glGetVertexAttribIuiv(int, int, java.nio.IntBuffer)` |  |
| `static void glInvalidateFramebuffer(int, int, int[], int)` |  |
| `static void glInvalidateFramebuffer(int, int, java.nio.IntBuffer)` |  |
| `static void glInvalidateSubFramebuffer(int, int, int[], int, int, int, int, int)` |  |
| `static void glInvalidateSubFramebuffer(int, int, java.nio.IntBuffer, int, int, int, int)` |  |
| `static boolean glIsQuery(int)` |  |
| `static boolean glIsSampler(int)` |  |
| `static boolean glIsSync(long)` |  |
| `static boolean glIsTransformFeedback(int)` |  |
| `static boolean glIsVertexArray(int)` |  |
| `static java.nio.Buffer glMapBufferRange(int, int, int, int)` |  |
| `static void glPauseTransformFeedback()` |  |
| `static void glProgramBinary(int, int, java.nio.Buffer, int)` |  |
| `static void glProgramParameteri(int, int, int)` |  |
| `static void glReadBuffer(int)` |  |
| `static void glReadPixels(int, int, int, int, int, int, int)` |  |
| `static void glRenderbufferStorageMultisample(int, int, int, int, int)` |  |
| `static void glResumeTransformFeedback()` |  |
| `static void glSamplerParameterf(int, int, float)` |  |
| `static void glSamplerParameterfv(int, int, float[], int)` |  |
| `static void glSamplerParameterfv(int, int, java.nio.FloatBuffer)` |  |
| `static void glSamplerParameteri(int, int, int)` |  |
| `static void glSamplerParameteriv(int, int, int[], int)` |  |
| `static void glSamplerParameteriv(int, int, java.nio.IntBuffer)` |  |
| `static void glTexImage3D(int, int, int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glTexImage3D(int, int, int, int, int, int, int, int, int, int)` |  |
| `static void glTexStorage2D(int, int, int, int, int)` |  |
| `static void glTexStorage3D(int, int, int, int, int, int)` |  |
| `static void glTexSubImage3D(int, int, int, int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glTexSubImage3D(int, int, int, int, int, int, int, int, int, int, int)` |  |
| `static void glTransformFeedbackVaryings(int, String[], int)` |  |
| `static void glUniform1ui(int, int)` |  |
| `static void glUniform1uiv(int, int, int[], int)` |  |
| `static void glUniform1uiv(int, int, java.nio.IntBuffer)` |  |
| `static void glUniform2ui(int, int, int)` |  |
| `static void glUniform2uiv(int, int, int[], int)` |  |
| `static void glUniform2uiv(int, int, java.nio.IntBuffer)` |  |
| `static void glUniform3ui(int, int, int, int)` |  |
| `static void glUniform3uiv(int, int, int[], int)` |  |
| `static void glUniform3uiv(int, int, java.nio.IntBuffer)` |  |
| `static void glUniform4ui(int, int, int, int, int)` |  |
| `static void glUniform4uiv(int, int, int[], int)` |  |
| `static void glUniform4uiv(int, int, java.nio.IntBuffer)` |  |
| `static void glUniformBlockBinding(int, int, int)` |  |
| `static void glUniformMatrix2x3fv(int, int, boolean, float[], int)` |  |
| `static void glUniformMatrix2x3fv(int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glUniformMatrix2x4fv(int, int, boolean, float[], int)` |  |
| `static void glUniformMatrix2x4fv(int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glUniformMatrix3x2fv(int, int, boolean, float[], int)` |  |
| `static void glUniformMatrix3x2fv(int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glUniformMatrix3x4fv(int, int, boolean, float[], int)` |  |
| `static void glUniformMatrix3x4fv(int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glUniformMatrix4x2fv(int, int, boolean, float[], int)` |  |
| `static void glUniformMatrix4x2fv(int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glUniformMatrix4x3fv(int, int, boolean, float[], int)` |  |
| `static void glUniformMatrix4x3fv(int, int, boolean, java.nio.FloatBuffer)` |  |
| `static boolean glUnmapBuffer(int)` |  |
| `static void glVertexAttribDivisor(int, int)` |  |
| `static void glVertexAttribI4i(int, int, int, int, int)` |  |
| `static void glVertexAttribI4iv(int, int[], int)` |  |
| `static void glVertexAttribI4iv(int, java.nio.IntBuffer)` |  |
| `static void glVertexAttribI4ui(int, int, int, int, int)` |  |
| `static void glVertexAttribI4uiv(int, int[], int)` |  |
| `static void glVertexAttribI4uiv(int, java.nio.IntBuffer)` |  |
| `static void glVertexAttribIPointer(int, int, int, int, java.nio.Buffer)` |  |
| `static void glVertexAttribIPointer(int, int, int, int, int)` |  |
| `static void glWaitSync(long, int, long)` |  |

---

### `class GLES31`

- **继承：** `android.opengl.GLES30`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int GL_ACTIVE_ATOMIC_COUNTER_BUFFERS = 37593` |  |
| `static final int GL_ACTIVE_PROGRAM = 33369` |  |
| `static final int GL_ACTIVE_RESOURCES = 37621` |  |
| `static final int GL_ACTIVE_VARIABLES = 37637` |  |
| `static final int GL_ALL_BARRIER_BITS = -1` |  |
| `static final int GL_ALL_SHADER_BITS = -1` |  |
| `static final int GL_ARRAY_SIZE = 37627` |  |
| `static final int GL_ARRAY_STRIDE = 37630` |  |
| `static final int GL_ATOMIC_COUNTER_BARRIER_BIT = 4096` |  |
| `static final int GL_ATOMIC_COUNTER_BUFFER = 37568` |  |
| `static final int GL_ATOMIC_COUNTER_BUFFER_BINDING = 37569` |  |
| `static final int GL_ATOMIC_COUNTER_BUFFER_INDEX = 37633` |  |
| `static final int GL_ATOMIC_COUNTER_BUFFER_SIZE = 37571` |  |
| `static final int GL_ATOMIC_COUNTER_BUFFER_START = 37570` |  |
| `static final int GL_BLOCK_INDEX = 37629` |  |
| `static final int GL_BUFFER_BINDING = 37634` |  |
| `static final int GL_BUFFER_DATA_SIZE = 37635` |  |
| `static final int GL_BUFFER_UPDATE_BARRIER_BIT = 512` |  |
| `static final int GL_BUFFER_VARIABLE = 37605` |  |
| `static final int GL_COMMAND_BARRIER_BIT = 64` |  |
| `static final int GL_COMPUTE_SHADER = 37305` |  |
| `static final int GL_COMPUTE_SHADER_BIT = 32` |  |
| `static final int GL_COMPUTE_WORK_GROUP_SIZE = 33383` |  |
| `static final int GL_DEPTH_STENCIL_TEXTURE_MODE = 37098` |  |
| `static final int GL_DISPATCH_INDIRECT_BUFFER = 37102` |  |
| `static final int GL_DISPATCH_INDIRECT_BUFFER_BINDING = 37103` |  |
| `static final int GL_DRAW_INDIRECT_BUFFER = 36671` |  |
| `static final int GL_DRAW_INDIRECT_BUFFER_BINDING = 36675` |  |
| `static final int GL_ELEMENT_ARRAY_BARRIER_BIT = 2` |  |
| `static final int GL_FRAGMENT_SHADER_BIT = 2` |  |
| `static final int GL_FRAMEBUFFER_BARRIER_BIT = 1024` |  |
| `static final int GL_FRAMEBUFFER_DEFAULT_FIXED_SAMPLE_LOCATIONS = 37652` |  |
| `static final int GL_FRAMEBUFFER_DEFAULT_HEIGHT = 37649` |  |
| `static final int GL_FRAMEBUFFER_DEFAULT_SAMPLES = 37651` |  |
| `static final int GL_FRAMEBUFFER_DEFAULT_WIDTH = 37648` |  |
| `static final int GL_IMAGE_2D = 36941` |  |
| `static final int GL_IMAGE_2D_ARRAY = 36947` |  |
| `static final int GL_IMAGE_3D = 36942` |  |
| `static final int GL_IMAGE_BINDING_ACCESS = 36670` |  |
| `static final int GL_IMAGE_BINDING_FORMAT = 36974` |  |
| `static final int GL_IMAGE_BINDING_LAYER = 36669` |  |
| `static final int GL_IMAGE_BINDING_LAYERED = 36668` |  |
| `static final int GL_IMAGE_BINDING_LEVEL = 36667` |  |
| `static final int GL_IMAGE_BINDING_NAME = 36666` |  |
| `static final int GL_IMAGE_CUBE = 36944` |  |
| `static final int GL_IMAGE_FORMAT_COMPATIBILITY_BY_CLASS = 37065` |  |
| `static final int GL_IMAGE_FORMAT_COMPATIBILITY_BY_SIZE = 37064` |  |
| `static final int GL_IMAGE_FORMAT_COMPATIBILITY_TYPE = 37063` |  |
| `static final int GL_INT_IMAGE_2D = 36952` |  |
| `static final int GL_INT_IMAGE_2D_ARRAY = 36958` |  |
| `static final int GL_INT_IMAGE_3D = 36953` |  |
| `static final int GL_INT_IMAGE_CUBE = 36955` |  |
| `static final int GL_INT_SAMPLER_2D_MULTISAMPLE = 37129` |  |
| `static final int GL_IS_ROW_MAJOR = 37632` |  |
| `static final int GL_LOCATION = 37646` |  |
| `static final int GL_MATRIX_STRIDE = 37631` |  |
| `static final int GL_MAX_ATOMIC_COUNTER_BUFFER_BINDINGS = 37596` |  |
| `static final int GL_MAX_ATOMIC_COUNTER_BUFFER_SIZE = 37592` |  |
| `static final int GL_MAX_COLOR_TEXTURE_SAMPLES = 37134` |  |
| `static final int GL_MAX_COMBINED_ATOMIC_COUNTERS = 37591` |  |
| `static final int GL_MAX_COMBINED_ATOMIC_COUNTER_BUFFERS = 37585` |  |
| `static final int GL_MAX_COMBINED_COMPUTE_UNIFORM_COMPONENTS = 33382` |  |
| `static final int GL_MAX_COMBINED_IMAGE_UNIFORMS = 37071` |  |
| `static final int GL_MAX_COMBINED_SHADER_OUTPUT_RESOURCES = 36665` |  |
| `static final int GL_MAX_COMBINED_SHADER_STORAGE_BLOCKS = 37084` |  |
| `static final int GL_MAX_COMPUTE_ATOMIC_COUNTERS = 33381` |  |
| `static final int GL_MAX_COMPUTE_ATOMIC_COUNTER_BUFFERS = 33380` |  |
| `static final int GL_MAX_COMPUTE_IMAGE_UNIFORMS = 37309` |  |
| `static final int GL_MAX_COMPUTE_SHADER_STORAGE_BLOCKS = 37083` |  |
| `static final int GL_MAX_COMPUTE_SHARED_MEMORY_SIZE = 33378` |  |
| `static final int GL_MAX_COMPUTE_TEXTURE_IMAGE_UNITS = 37308` |  |
| `static final int GL_MAX_COMPUTE_UNIFORM_BLOCKS = 37307` |  |
| `static final int GL_MAX_COMPUTE_UNIFORM_COMPONENTS = 33379` |  |
| `static final int GL_MAX_COMPUTE_WORK_GROUP_COUNT = 37310` |  |
| `static final int GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS = 37099` |  |
| `static final int GL_MAX_COMPUTE_WORK_GROUP_SIZE = 37311` |  |
| `static final int GL_MAX_DEPTH_TEXTURE_SAMPLES = 37135` |  |
| `static final int GL_MAX_FRAGMENT_ATOMIC_COUNTERS = 37590` |  |
| `static final int GL_MAX_FRAGMENT_ATOMIC_COUNTER_BUFFERS = 37584` |  |
| `static final int GL_MAX_FRAGMENT_IMAGE_UNIFORMS = 37070` |  |
| `static final int GL_MAX_FRAGMENT_SHADER_STORAGE_BLOCKS = 37082` |  |
| `static final int GL_MAX_FRAMEBUFFER_HEIGHT = 37654` |  |
| `static final int GL_MAX_FRAMEBUFFER_SAMPLES = 37656` |  |
| `static final int GL_MAX_FRAMEBUFFER_WIDTH = 37653` |  |
| `static final int GL_MAX_IMAGE_UNITS = 36664` |  |
| `static final int GL_MAX_INTEGER_SAMPLES = 37136` |  |
| `static final int GL_MAX_NAME_LENGTH = 37622` |  |
| `static final int GL_MAX_NUM_ACTIVE_VARIABLES = 37623` |  |
| `static final int GL_MAX_PROGRAM_TEXTURE_GATHER_OFFSET = 36447` |  |
| `static final int GL_MAX_SAMPLE_MASK_WORDS = 36441` |  |
| `static final int GL_MAX_SHADER_STORAGE_BLOCK_SIZE = 37086` |  |
| `static final int GL_MAX_SHADER_STORAGE_BUFFER_BINDINGS = 37085` |  |
| `static final int GL_MAX_UNIFORM_LOCATIONS = 33390` |  |
| `static final int GL_MAX_VERTEX_ATOMIC_COUNTERS = 37586` |  |
| `static final int GL_MAX_VERTEX_ATOMIC_COUNTER_BUFFERS = 37580` |  |
| `static final int GL_MAX_VERTEX_ATTRIB_BINDINGS = 33498` |  |
| `static final int GL_MAX_VERTEX_ATTRIB_RELATIVE_OFFSET = 33497` |  |
| `static final int GL_MAX_VERTEX_ATTRIB_STRIDE = 33509` |  |
| `static final int GL_MAX_VERTEX_IMAGE_UNIFORMS = 37066` |  |
| `static final int GL_MAX_VERTEX_SHADER_STORAGE_BLOCKS = 37078` |  |
| `static final int GL_MIN_PROGRAM_TEXTURE_GATHER_OFFSET = 36446` |  |
| `static final int GL_NAME_LENGTH = 37625` |  |
| `static final int GL_NUM_ACTIVE_VARIABLES = 37636` |  |
| `static final int GL_OFFSET = 37628` |  |
| `static final int GL_PIXEL_BUFFER_BARRIER_BIT = 128` |  |
| `static final int GL_PROGRAM_INPUT = 37603` |  |
| `static final int GL_PROGRAM_OUTPUT = 37604` |  |
| `static final int GL_PROGRAM_PIPELINE_BINDING = 33370` |  |
| `static final int GL_PROGRAM_SEPARABLE = 33368` |  |
| `static final int GL_READ_ONLY = 35000` |  |
| `static final int GL_READ_WRITE = 35002` |  |
| `static final int GL_REFERENCED_BY_COMPUTE_SHADER = 37643` |  |
| `static final int GL_REFERENCED_BY_FRAGMENT_SHADER = 37642` |  |
| `static final int GL_REFERENCED_BY_VERTEX_SHADER = 37638` |  |
| `static final int GL_SAMPLER_2D_MULTISAMPLE = 37128` |  |
| `static final int GL_SAMPLE_MASK = 36433` |  |
| `static final int GL_SAMPLE_MASK_VALUE = 36434` |  |
| `static final int GL_SAMPLE_POSITION = 36432` |  |
| `static final int GL_SHADER_IMAGE_ACCESS_BARRIER_BIT = 32` |  |
| `static final int GL_SHADER_STORAGE_BARRIER_BIT = 8192` |  |
| `static final int GL_SHADER_STORAGE_BLOCK = 37606` |  |
| `static final int GL_SHADER_STORAGE_BUFFER = 37074` |  |
| `static final int GL_SHADER_STORAGE_BUFFER_BINDING = 37075` |  |
| `static final int GL_SHADER_STORAGE_BUFFER_OFFSET_ALIGNMENT = 37087` |  |
| `static final int GL_SHADER_STORAGE_BUFFER_SIZE = 37077` |  |
| `static final int GL_SHADER_STORAGE_BUFFER_START = 37076` |  |
| `static final int GL_STENCIL_INDEX = 6401` |  |
| `static final int GL_TEXTURE_2D_MULTISAMPLE = 37120` |  |
| `static final int GL_TEXTURE_ALPHA_SIZE = 32863` |  |
| `static final int GL_TEXTURE_ALPHA_TYPE = 35859` |  |
| `static final int GL_TEXTURE_BINDING_2D_MULTISAMPLE = 37124` |  |
| `static final int GL_TEXTURE_BLUE_SIZE = 32862` |  |
| `static final int GL_TEXTURE_BLUE_TYPE = 35858` |  |
| `static final int GL_TEXTURE_COMPRESSED = 34465` |  |
| `static final int GL_TEXTURE_DEPTH = 32881` |  |
| `static final int GL_TEXTURE_DEPTH_SIZE = 34890` |  |
| `static final int GL_TEXTURE_DEPTH_TYPE = 35862` |  |
| `static final int GL_TEXTURE_FETCH_BARRIER_BIT = 8` |  |
| `static final int GL_TEXTURE_FIXED_SAMPLE_LOCATIONS = 37127` |  |
| `static final int GL_TEXTURE_GREEN_SIZE = 32861` |  |
| `static final int GL_TEXTURE_GREEN_TYPE = 35857` |  |
| `static final int GL_TEXTURE_HEIGHT = 4097` |  |
| `static final int GL_TEXTURE_INTERNAL_FORMAT = 4099` |  |
| `static final int GL_TEXTURE_RED_SIZE = 32860` |  |
| `static final int GL_TEXTURE_RED_TYPE = 35856` |  |
| `static final int GL_TEXTURE_SAMPLES = 37126` |  |
| `static final int GL_TEXTURE_SHARED_SIZE = 35903` |  |
| `static final int GL_TEXTURE_STENCIL_SIZE = 35057` |  |
| `static final int GL_TEXTURE_UPDATE_BARRIER_BIT = 256` |  |
| `static final int GL_TEXTURE_WIDTH = 4096` |  |
| `static final int GL_TOP_LEVEL_ARRAY_SIZE = 37644` |  |
| `static final int GL_TOP_LEVEL_ARRAY_STRIDE = 37645` |  |
| `static final int GL_TRANSFORM_FEEDBACK_BARRIER_BIT = 2048` |  |
| `static final int GL_TRANSFORM_FEEDBACK_VARYING = 37620` |  |
| `static final int GL_TYPE = 37626` |  |
| `static final int GL_UNIFORM = 37601` |  |
| `static final int GL_UNIFORM_BARRIER_BIT = 4` |  |
| `static final int GL_UNIFORM_BLOCK = 37602` |  |
| `static final int GL_UNSIGNED_INT_ATOMIC_COUNTER = 37595` |  |
| `static final int GL_UNSIGNED_INT_IMAGE_2D = 36963` |  |
| `static final int GL_UNSIGNED_INT_IMAGE_2D_ARRAY = 36969` |  |
| `static final int GL_UNSIGNED_INT_IMAGE_3D = 36964` |  |
| `static final int GL_UNSIGNED_INT_IMAGE_CUBE = 36966` |  |
| `static final int GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE = 37130` |  |
| `static final int GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT = 1` |  |
| `static final int GL_VERTEX_ATTRIB_BINDING = 33492` |  |
| `static final int GL_VERTEX_ATTRIB_RELATIVE_OFFSET = 33493` |  |
| `static final int GL_VERTEX_BINDING_BUFFER = 36687` |  |
| `static final int GL_VERTEX_BINDING_DIVISOR = 33494` |  |
| `static final int GL_VERTEX_BINDING_OFFSET = 33495` |  |
| `static final int GL_VERTEX_BINDING_STRIDE = 33496` |  |
| `static final int GL_VERTEX_SHADER_BIT = 1` |  |
| `static final int GL_WRITE_ONLY = 35001` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void glActiveShaderProgram(int, int)` |  |
| `static void glBindImageTexture(int, int, int, boolean, int, int, int)` |  |
| `static void glBindProgramPipeline(int)` |  |
| `static void glBindVertexBuffer(int, int, long, int)` |  |
| `static int glCreateShaderProgramv(int, String[])` |  |
| `static void glDeleteProgramPipelines(int, int[], int)` |  |
| `static void glDeleteProgramPipelines(int, java.nio.IntBuffer)` |  |
| `static void glDispatchCompute(int, int, int)` |  |
| `static void glDispatchComputeIndirect(long)` |  |
| `static void glDrawArraysIndirect(int, long)` |  |
| `static void glDrawElementsIndirect(int, int, long)` |  |
| `static void glFramebufferParameteri(int, int, int)` |  |
| `static void glGenProgramPipelines(int, int[], int)` |  |
| `static void glGenProgramPipelines(int, java.nio.IntBuffer)` |  |
| `static void glGetBooleani_v(int, int, boolean[], int)` |  |
| `static void glGetBooleani_v(int, int, java.nio.IntBuffer)` |  |
| `static void glGetFramebufferParameteriv(int, int, int[], int)` |  |
| `static void glGetFramebufferParameteriv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetMultisamplefv(int, int, float[], int)` |  |
| `static void glGetMultisamplefv(int, int, java.nio.FloatBuffer)` |  |
| `static void glGetProgramInterfaceiv(int, int, int, int[], int)` |  |
| `static void glGetProgramInterfaceiv(int, int, int, java.nio.IntBuffer)` |  |
| `static String glGetProgramPipelineInfoLog(int)` |  |
| `static void glGetProgramPipelineiv(int, int, int[], int)` |  |
| `static void glGetProgramPipelineiv(int, int, java.nio.IntBuffer)` |  |
| `static int glGetProgramResourceIndex(int, int, String)` |  |
| `static int glGetProgramResourceLocation(int, int, String)` |  |
| `static String glGetProgramResourceName(int, int, int)` |  |
| `static void glGetProgramResourceiv(int, int, int, int, int[], int, int, int[], int, int[], int)` |  |
| `static void glGetProgramResourceiv(int, int, int, int, java.nio.IntBuffer, int, java.nio.IntBuffer, java.nio.IntBuffer)` |  |
| `static void glGetTexLevelParameterfv(int, int, int, float[], int)` |  |
| `static void glGetTexLevelParameterfv(int, int, int, java.nio.FloatBuffer)` |  |
| `static void glGetTexLevelParameteriv(int, int, int, int[], int)` |  |
| `static void glGetTexLevelParameteriv(int, int, int, java.nio.IntBuffer)` |  |
| `static boolean glIsProgramPipeline(int)` |  |
| `static void glMemoryBarrier(int)` |  |
| `static void glMemoryBarrierByRegion(int)` |  |
| `static void glProgramUniform1f(int, int, float)` |  |
| `static void glProgramUniform1fv(int, int, int, float[], int)` |  |
| `static void glProgramUniform1fv(int, int, int, java.nio.FloatBuffer)` |  |
| `static void glProgramUniform1i(int, int, int)` |  |
| `static void glProgramUniform1iv(int, int, int, int[], int)` |  |
| `static void glProgramUniform1iv(int, int, int, java.nio.IntBuffer)` |  |
| `static void glProgramUniform1ui(int, int, int)` |  |
| `static void glProgramUniform1uiv(int, int, int, int[], int)` |  |
| `static void glProgramUniform1uiv(int, int, int, java.nio.IntBuffer)` |  |
| `static void glProgramUniform2f(int, int, float, float)` |  |
| `static void glProgramUniform2fv(int, int, int, float[], int)` |  |
| `static void glProgramUniform2fv(int, int, int, java.nio.FloatBuffer)` |  |
| `static void glProgramUniform2i(int, int, int, int)` |  |
| `static void glProgramUniform2iv(int, int, int, int[], int)` |  |
| `static void glProgramUniform2iv(int, int, int, java.nio.IntBuffer)` |  |
| `static void glProgramUniform2ui(int, int, int, int)` |  |
| `static void glProgramUniform2uiv(int, int, int, int[], int)` |  |
| `static void glProgramUniform2uiv(int, int, int, java.nio.IntBuffer)` |  |
| `static void glProgramUniform3f(int, int, float, float, float)` |  |
| `static void glProgramUniform3fv(int, int, int, float[], int)` |  |
| `static void glProgramUniform3fv(int, int, int, java.nio.FloatBuffer)` |  |
| `static void glProgramUniform3i(int, int, int, int, int)` |  |
| `static void glProgramUniform3iv(int, int, int, int[], int)` |  |
| `static void glProgramUniform3iv(int, int, int, java.nio.IntBuffer)` |  |
| `static void glProgramUniform3ui(int, int, int, int, int)` |  |
| `static void glProgramUniform3uiv(int, int, int, int[], int)` |  |
| `static void glProgramUniform3uiv(int, int, int, java.nio.IntBuffer)` |  |
| `static void glProgramUniform4f(int, int, float, float, float, float)` |  |
| `static void glProgramUniform4fv(int, int, int, float[], int)` |  |
| `static void glProgramUniform4fv(int, int, int, java.nio.FloatBuffer)` |  |
| `static void glProgramUniform4i(int, int, int, int, int, int)` |  |
| `static void glProgramUniform4iv(int, int, int, int[], int)` |  |
| `static void glProgramUniform4iv(int, int, int, java.nio.IntBuffer)` |  |
| `static void glProgramUniform4ui(int, int, int, int, int, int)` |  |
| `static void glProgramUniform4uiv(int, int, int, int[], int)` |  |
| `static void glProgramUniform4uiv(int, int, int, java.nio.IntBuffer)` |  |
| `static void glProgramUniformMatrix2fv(int, int, int, boolean, float[], int)` |  |
| `static void glProgramUniformMatrix2fv(int, int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glProgramUniformMatrix2x3fv(int, int, int, boolean, float[], int)` |  |
| `static void glProgramUniformMatrix2x3fv(int, int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glProgramUniformMatrix2x4fv(int, int, int, boolean, float[], int)` |  |
| `static void glProgramUniformMatrix2x4fv(int, int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glProgramUniformMatrix3fv(int, int, int, boolean, float[], int)` |  |
| `static void glProgramUniformMatrix3fv(int, int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glProgramUniformMatrix3x2fv(int, int, int, boolean, float[], int)` |  |
| `static void glProgramUniformMatrix3x2fv(int, int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glProgramUniformMatrix3x4fv(int, int, int, boolean, float[], int)` |  |
| `static void glProgramUniformMatrix3x4fv(int, int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glProgramUniformMatrix4fv(int, int, int, boolean, float[], int)` |  |
| `static void glProgramUniformMatrix4fv(int, int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glProgramUniformMatrix4x2fv(int, int, int, boolean, float[], int)` |  |
| `static void glProgramUniformMatrix4x2fv(int, int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glProgramUniformMatrix4x3fv(int, int, int, boolean, float[], int)` |  |
| `static void glProgramUniformMatrix4x3fv(int, int, int, boolean, java.nio.FloatBuffer)` |  |
| `static void glSampleMaski(int, int)` |  |
| `static void glTexStorage2DMultisample(int, int, int, int, int, boolean)` |  |
| `static void glUseProgramStages(int, int, int)` |  |
| `static void glValidateProgramPipeline(int)` |  |
| `static void glVertexAttribBinding(int, int)` |  |
| `static void glVertexAttribFormat(int, int, int, boolean, int)` |  |
| `static void glVertexAttribIFormat(int, int, int, int)` |  |
| `static void glVertexBindingDivisor(int, int)` |  |

---

### `class GLES31Ext`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int GL_BLEND_ADVANCED_COHERENT_KHR = 37509` |  |
| `static final int GL_BUFFER_KHR = 33504` |  |
| `static final int GL_CLAMP_TO_BORDER_EXT = 33069` |  |
| `static final int GL_COLORBURN_KHR = 37530` |  |
| `static final int GL_COLORDODGE_KHR = 37529` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_10x10_KHR = 37819` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_10x5_KHR = 37816` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_10x6_KHR = 37817` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_10x8_KHR = 37818` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_12x10_KHR = 37820` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_12x12_KHR = 37821` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_4x4_KHR = 37808` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_5x4_KHR = 37809` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_5x5_KHR = 37810` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_6x5_KHR = 37811` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_6x6_KHR = 37812` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_8x5_KHR = 37813` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_8x6_KHR = 37814` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_8x8_KHR = 37815` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x10_KHR = 37851` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x5_KHR = 37848` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x6_KHR = 37849` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x8_KHR = 37850` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_12x10_KHR = 37852` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_12x12_KHR = 37853` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_4x4_KHR = 37840` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_5x4_KHR = 37841` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_5x5_KHR = 37842` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_6x5_KHR = 37843` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_6x6_KHR = 37844` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_8x5_KHR = 37845` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_8x6_KHR = 37846` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_8x8_KHR = 37847` |  |
| `static final int GL_CONTEXT_FLAG_DEBUG_BIT_KHR = 2` |  |
| `static final int GL_DARKEN_KHR = 37527` |  |
| `static final int GL_DEBUG_CALLBACK_FUNCTION_KHR = 33348` |  |
| `static final int GL_DEBUG_CALLBACK_USER_PARAM_KHR = 33349` |  |
| `static final int GL_DEBUG_GROUP_STACK_DEPTH_KHR = 33389` |  |
| `static final int GL_DEBUG_LOGGED_MESSAGES_KHR = 37189` |  |
| `static final int GL_DEBUG_NEXT_LOGGED_MESSAGE_LENGTH_KHR = 33347` |  |
| `static final int GL_DEBUG_OUTPUT_KHR = 37600` |  |
| `static final int GL_DEBUG_OUTPUT_SYNCHRONOUS_KHR = 33346` |  |
| `static final int GL_DEBUG_SEVERITY_HIGH_KHR = 37190` |  |
| `static final int GL_DEBUG_SEVERITY_LOW_KHR = 37192` |  |
| `static final int GL_DEBUG_SEVERITY_MEDIUM_KHR = 37191` |  |
| `static final int GL_DEBUG_SEVERITY_NOTIFICATION_KHR = 33387` |  |
| `static final int GL_DEBUG_SOURCE_API_KHR = 33350` |  |
| `static final int GL_DEBUG_SOURCE_APPLICATION_KHR = 33354` |  |
| `static final int GL_DEBUG_SOURCE_OTHER_KHR = 33355` |  |
| `static final int GL_DEBUG_SOURCE_SHADER_COMPILER_KHR = 33352` |  |
| `static final int GL_DEBUG_SOURCE_THIRD_PARTY_KHR = 33353` |  |
| `static final int GL_DEBUG_SOURCE_WINDOW_SYSTEM_KHR = 33351` |  |
| `static final int GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR_KHR = 33357` |  |
| `static final int GL_DEBUG_TYPE_ERROR_KHR = 33356` |  |
| `static final int GL_DEBUG_TYPE_MARKER_KHR = 33384` |  |
| `static final int GL_DEBUG_TYPE_OTHER_KHR = 33361` |  |
| `static final int GL_DEBUG_TYPE_PERFORMANCE_KHR = 33360` |  |
| `static final int GL_DEBUG_TYPE_POP_GROUP_KHR = 33386` |  |
| `static final int GL_DEBUG_TYPE_PORTABILITY_KHR = 33359` |  |
| `static final int GL_DEBUG_TYPE_PUSH_GROUP_KHR = 33385` |  |
| `static final int GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR_KHR = 33358` |  |
| `static final int GL_DECODE_EXT = 35401` |  |
| `static final int GL_DIFFERENCE_KHR = 37534` |  |
| `static final int GL_EXCLUSION_KHR = 37536` |  |
| `static final int GL_FIRST_VERTEX_CONVENTION_EXT = 36429` |  |
| `static final int GL_FRACTIONAL_EVEN_EXT = 36476` |  |
| `static final int GL_FRACTIONAL_ODD_EXT = 36475` |  |
| `static final int GL_FRAGMENT_INTERPOLATION_OFFSET_BITS_OES = 36445` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_LAYERED_EXT = 36263` |  |
| `static final int GL_FRAMEBUFFER_DEFAULT_LAYERS_EXT = 37650` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS_EXT = 36264` |  |
| `static final int GL_GEOMETRY_LINKED_INPUT_TYPE_EXT = 35095` |  |
| `static final int GL_GEOMETRY_LINKED_OUTPUT_TYPE_EXT = 35096` |  |
| `static final int GL_GEOMETRY_LINKED_VERTICES_OUT_EXT = 35094` |  |
| `static final int GL_GEOMETRY_SHADER_BIT_EXT = 4` |  |
| `static final int GL_GEOMETRY_SHADER_EXT = 36313` |  |
| `static final int GL_GEOMETRY_SHADER_INVOCATIONS_EXT = 34943` |  |
| `static final int GL_HARDLIGHT_KHR = 37531` |  |
| `static final int GL_HSL_COLOR_KHR = 37551` |  |
| `static final int GL_HSL_HUE_KHR = 37549` |  |
| `static final int GL_HSL_LUMINOSITY_KHR = 37552` |  |
| `static final int GL_HSL_SATURATION_KHR = 37550` |  |
| `static final int GL_IMAGE_BUFFER_EXT = 36945` |  |
| `static final int GL_IMAGE_CUBE_MAP_ARRAY_EXT = 36948` |  |
| `static final int GL_INT_IMAGE_BUFFER_EXT = 36956` |  |
| `static final int GL_INT_IMAGE_CUBE_MAP_ARRAY_EXT = 36959` |  |
| `static final int GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY_OES = 37132` |  |
| `static final int GL_INT_SAMPLER_BUFFER_EXT = 36304` |  |
| `static final int GL_INT_SAMPLER_CUBE_MAP_ARRAY_EXT = 36878` |  |
| `static final int GL_ISOLINES_EXT = 36474` |  |
| `static final int GL_IS_PER_PATCH_EXT = 37607` |  |
| `static final int GL_LAST_VERTEX_CONVENTION_EXT = 36430` |  |
| `static final int GL_LAYER_PROVOKING_VERTEX_EXT = 33374` |  |
| `static final int GL_LIGHTEN_KHR = 37528` |  |
| `static final int GL_LINES_ADJACENCY_EXT = 10` |  |
| `static final int GL_LINE_STRIP_ADJACENCY_EXT = 11` |  |
| `static final int GL_MAX_COMBINED_GEOMETRY_UNIFORM_COMPONENTS_EXT = 35378` |  |
| `static final int GL_MAX_COMBINED_TESS_CONTROL_UNIFORM_COMPONENTS_EXT = 36382` |  |
| `static final int GL_MAX_COMBINED_TESS_EVALUATION_UNIFORM_COMPONENTS_EXT = 36383` |  |
| `static final int GL_MAX_DEBUG_GROUP_STACK_DEPTH_KHR = 33388` |  |
| `static final int GL_MAX_DEBUG_LOGGED_MESSAGES_KHR = 37188` |  |
| `static final int GL_MAX_DEBUG_MESSAGE_LENGTH_KHR = 37187` |  |
| `static final int GL_MAX_FRAGMENT_INTERPOLATION_OFFSET_OES = 36444` |  |
| `static final int GL_MAX_FRAMEBUFFER_LAYERS_EXT = 37655` |  |
| `static final int GL_MAX_GEOMETRY_ATOMIC_COUNTERS_EXT = 37589` |  |
| `static final int GL_MAX_GEOMETRY_ATOMIC_COUNTER_BUFFERS_EXT = 37583` |  |
| `static final int GL_MAX_GEOMETRY_IMAGE_UNIFORMS_EXT = 37069` |  |
| `static final int GL_MAX_GEOMETRY_INPUT_COMPONENTS_EXT = 37155` |  |
| `static final int GL_MAX_GEOMETRY_OUTPUT_COMPONENTS_EXT = 37156` |  |
| `static final int GL_MAX_GEOMETRY_OUTPUT_VERTICES_EXT = 36320` |  |
| `static final int GL_MAX_GEOMETRY_SHADER_INVOCATIONS_EXT = 36442` |  |
| `static final int GL_MAX_GEOMETRY_SHADER_STORAGE_BLOCKS_EXT = 37079` |  |
| `static final int GL_MAX_GEOMETRY_TEXTURE_IMAGE_UNITS_EXT = 35881` |  |
| `static final int GL_MAX_GEOMETRY_TOTAL_OUTPUT_COMPONENTS_EXT = 36321` |  |
| `static final int GL_MAX_GEOMETRY_UNIFORM_BLOCKS_EXT = 35372` |  |
| `static final int GL_MAX_GEOMETRY_UNIFORM_COMPONENTS_EXT = 36319` |  |
| `static final int GL_MAX_LABEL_LENGTH_KHR = 33512` |  |
| `static final int GL_MAX_PATCH_VERTICES_EXT = 36477` |  |
| `static final int GL_MAX_TESS_CONTROL_ATOMIC_COUNTERS_EXT = 37587` |  |
| `static final int GL_MAX_TESS_CONTROL_ATOMIC_COUNTER_BUFFERS_EXT = 37581` |  |
| `static final int GL_MAX_TESS_CONTROL_IMAGE_UNIFORMS_EXT = 37067` |  |
| `static final int GL_MAX_TESS_CONTROL_INPUT_COMPONENTS_EXT = 34924` |  |
| `static final int GL_MAX_TESS_CONTROL_OUTPUT_COMPONENTS_EXT = 36483` |  |
| `static final int GL_MAX_TESS_CONTROL_SHADER_STORAGE_BLOCKS_EXT = 37080` |  |
| `static final int GL_MAX_TESS_CONTROL_TEXTURE_IMAGE_UNITS_EXT = 36481` |  |
| `static final int GL_MAX_TESS_CONTROL_TOTAL_OUTPUT_COMPONENTS_EXT = 36485` |  |
| `static final int GL_MAX_TESS_CONTROL_UNIFORM_BLOCKS_EXT = 36489` |  |
| `static final int GL_MAX_TESS_CONTROL_UNIFORM_COMPONENTS_EXT = 36479` |  |
| `static final int GL_MAX_TESS_EVALUATION_ATOMIC_COUNTERS_EXT = 37588` |  |
| `static final int GL_MAX_TESS_EVALUATION_ATOMIC_COUNTER_BUFFERS_EXT = 37582` |  |
| `static final int GL_MAX_TESS_EVALUATION_IMAGE_UNIFORMS_EXT = 37068` |  |
| `static final int GL_MAX_TESS_EVALUATION_INPUT_COMPONENTS_EXT = 34925` |  |
| `static final int GL_MAX_TESS_EVALUATION_OUTPUT_COMPONENTS_EXT = 36486` |  |
| `static final int GL_MAX_TESS_EVALUATION_SHADER_STORAGE_BLOCKS_EXT = 37081` |  |
| `static final int GL_MAX_TESS_EVALUATION_TEXTURE_IMAGE_UNITS_EXT = 36482` |  |
| `static final int GL_MAX_TESS_EVALUATION_UNIFORM_BLOCKS_EXT = 36490` |  |
| `static final int GL_MAX_TESS_EVALUATION_UNIFORM_COMPONENTS_EXT = 36480` |  |
| `static final int GL_MAX_TESS_GEN_LEVEL_EXT = 36478` |  |
| `static final int GL_MAX_TESS_PATCH_COMPONENTS_EXT = 36484` |  |
| `static final int GL_MAX_TEXTURE_BUFFER_SIZE_EXT = 35883` |  |
| `static final int GL_MIN_FRAGMENT_INTERPOLATION_OFFSET_OES = 36443` |  |
| `static final int GL_MIN_SAMPLE_SHADING_VALUE_OES = 35895` |  |
| `static final int GL_MULTIPLY_KHR = 37524` |  |
| `static final int GL_OVERLAY_KHR = 37526` |  |
| `static final int GL_PATCHES_EXT = 14` |  |
| `static final int GL_PATCH_VERTICES_EXT = 36466` |  |
| `static final int GL_PRIMITIVES_GENERATED_EXT = 35975` |  |
| `static final int GL_PRIMITIVE_BOUNDING_BOX_EXT = 37566` |  |
| `static final int GL_PRIMITIVE_RESTART_FOR_PATCHES_SUPPORTED = 33313` |  |
| `static final int GL_PROGRAM_KHR = 33506` |  |
| `static final int GL_QUADS_EXT = 7` |  |
| `static final int GL_QUERY_KHR = 33507` |  |
| `static final int GL_REFERENCED_BY_GEOMETRY_SHADER_EXT = 37641` |  |
| `static final int GL_REFERENCED_BY_TESS_CONTROL_SHADER_EXT = 37639` |  |
| `static final int GL_REFERENCED_BY_TESS_EVALUATION_SHADER_EXT = 37640` |  |
| `static final int GL_SAMPLER_2D_MULTISAMPLE_ARRAY_OES = 37131` |  |
| `static final int GL_SAMPLER_BUFFER_EXT = 36290` |  |
| `static final int GL_SAMPLER_CUBE_MAP_ARRAY_EXT = 36876` |  |
| `static final int GL_SAMPLER_CUBE_MAP_ARRAY_SHADOW_EXT = 36877` |  |
| `static final int GL_SAMPLER_KHR = 33510` |  |
| `static final int GL_SAMPLE_SHADING_OES = 35894` |  |
| `static final int GL_SCREEN_KHR = 37525` |  |
| `static final int GL_SHADER_KHR = 33505` |  |
| `static final int GL_SKIP_DECODE_EXT = 35402` |  |
| `static final int GL_SOFTLIGHT_KHR = 37532` |  |
| `static final int GL_STACK_OVERFLOW_KHR = 1283` |  |
| `static final int GL_STACK_UNDERFLOW_KHR = 1284` |  |
| `static final int GL_STENCIL_INDEX8_OES = 36168` |  |
| `static final int GL_STENCIL_INDEX_OES = 6401` |  |
| `static final int GL_TESS_CONTROL_OUTPUT_VERTICES_EXT = 36469` |  |
| `static final int GL_TESS_CONTROL_SHADER_BIT_EXT = 8` |  |
| `static final int GL_TESS_CONTROL_SHADER_EXT = 36488` |  |
| `static final int GL_TESS_EVALUATION_SHADER_BIT_EXT = 16` |  |
| `static final int GL_TESS_EVALUATION_SHADER_EXT = 36487` |  |
| `static final int GL_TESS_GEN_MODE_EXT = 36470` |  |
| `static final int GL_TESS_GEN_POINT_MODE_EXT = 36473` |  |
| `static final int GL_TESS_GEN_SPACING_EXT = 36471` |  |
| `static final int GL_TESS_GEN_VERTEX_ORDER_EXT = 36472` |  |
| `static final int GL_TEXTURE_2D_MULTISAMPLE_ARRAY_OES = 37122` |  |
| `static final int GL_TEXTURE_BINDING_2D_MULTISAMPLE_ARRAY_OES = 37125` |  |
| `static final int GL_TEXTURE_BINDING_BUFFER_EXT = 35884` |  |
| `static final int GL_TEXTURE_BINDING_CUBE_MAP_ARRAY_EXT = 36874` |  |
| `static final int GL_TEXTURE_BORDER_COLOR_EXT = 4100` |  |
| `static final int GL_TEXTURE_BUFFER_BINDING_EXT = 35882` |  |
| `static final int GL_TEXTURE_BUFFER_DATA_STORE_BINDING_EXT = 35885` |  |
| `static final int GL_TEXTURE_BUFFER_EXT = 35882` |  |
| `static final int GL_TEXTURE_BUFFER_OFFSET_ALIGNMENT_EXT = 37279` |  |
| `static final int GL_TEXTURE_BUFFER_OFFSET_EXT = 37277` |  |
| `static final int GL_TEXTURE_BUFFER_SIZE_EXT = 37278` |  |
| `static final int GL_TEXTURE_CUBE_MAP_ARRAY_EXT = 36873` |  |
| `static final int GL_TEXTURE_SRGB_DECODE_EXT = 35400` |  |
| `static final int GL_TRIANGLES_ADJACENCY_EXT = 12` |  |
| `static final int GL_TRIANGLE_STRIP_ADJACENCY_EXT = 13` |  |
| `static final int GL_UNDEFINED_VERTEX_EXT = 33376` |  |
| `static final int GL_UNSIGNED_INT_IMAGE_BUFFER_EXT = 36967` |  |
| `static final int GL_UNSIGNED_INT_IMAGE_CUBE_MAP_ARRAY_EXT = 36970` |  |
| `static final int GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY_OES = 37133` |  |
| `static final int GL_UNSIGNED_INT_SAMPLER_BUFFER_EXT = 36312` |  |
| `static final int GL_UNSIGNED_INT_SAMPLER_CUBE_MAP_ARRAY_EXT = 36879` |  |
| `static final int GL_VERTEX_ARRAY_KHR = 32884` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void glBlendBarrierKHR()` |  |
| `static void glBlendEquationSeparateiEXT(int, int, int)` |  |
| `static void glBlendEquationiEXT(int, int)` |  |
| `static void glBlendFuncSeparateiEXT(int, int, int, int, int)` |  |
| `static void glBlendFunciEXT(int, int, int)` |  |
| `static void glColorMaskiEXT(int, boolean, boolean, boolean, boolean)` |  |
| `static void glCopyImageSubDataEXT(int, int, int, int, int, int, int, int, int, int, int, int, int, int, int)` |  |
| `static void glDebugMessageCallbackKHR(android.opengl.GLES31Ext.DebugProcKHR)` |  |
| `static void glDebugMessageControlKHR(int, int, int, int, int[], int, boolean)` |  |
| `static void glDebugMessageControlKHR(int, int, int, int, java.nio.IntBuffer, boolean)` |  |
| `static void glDebugMessageInsertKHR(int, int, int, int, String)` |  |
| `static void glDisableiEXT(int, int)` |  |
| `static void glEnableiEXT(int, int)` |  |
| `static void glFramebufferTextureEXT(int, int, int, int)` |  |
| `static android.opengl.GLES31Ext.DebugProcKHR glGetDebugMessageCallbackKHR()` |  |
| `static int glGetDebugMessageLogKHR(int, int, int[], int, int[], int, int[], int, int[], int, int[], int, byte[], int)` |  |
| `static int glGetDebugMessageLogKHR(int, java.nio.IntBuffer, java.nio.IntBuffer, java.nio.IntBuffer, java.nio.IntBuffer, java.nio.IntBuffer, java.nio.ByteBuffer)` |  |
| `static String[] glGetDebugMessageLogKHR(int, int[], int, int[], int, int[], int, int[], int)` |  |
| `static String[] glGetDebugMessageLogKHR(int, java.nio.IntBuffer, java.nio.IntBuffer, java.nio.IntBuffer, java.nio.IntBuffer)` |  |
| `static String glGetObjectLabelKHR(int, int)` |  |
| `static String glGetObjectPtrLabelKHR(long)` |  |
| `static void glGetSamplerParameterIivEXT(int, int, int[], int)` |  |
| `static void glGetSamplerParameterIivEXT(int, int, java.nio.IntBuffer)` |  |
| `static void glGetSamplerParameterIuivEXT(int, int, int[], int)` |  |
| `static void glGetSamplerParameterIuivEXT(int, int, java.nio.IntBuffer)` |  |
| `static void glGetTexParameterIivEXT(int, int, int[], int)` |  |
| `static void glGetTexParameterIivEXT(int, int, java.nio.IntBuffer)` |  |
| `static void glGetTexParameterIuivEXT(int, int, int[], int)` |  |
| `static void glGetTexParameterIuivEXT(int, int, java.nio.IntBuffer)` |  |
| `static boolean glIsEnablediEXT(int, int)` |  |
| `static void glMinSampleShadingOES(float)` |  |
| `static void glObjectLabelKHR(int, int, int, String)` |  |
| `static void glObjectPtrLabelKHR(long, String)` |  |
| `static void glPatchParameteriEXT(int, int)` |  |
| `static void glPopDebugGroupKHR()` |  |
| `static void glPrimitiveBoundingBoxEXT(float, float, float, float, float, float, float, float)` |  |
| `static void glPushDebugGroupKHR(int, int, int, String)` |  |
| `static void glSamplerParameterIivEXT(int, int, int[], int)` |  |
| `static void glSamplerParameterIivEXT(int, int, java.nio.IntBuffer)` |  |
| `static void glSamplerParameterIuivEXT(int, int, int[], int)` |  |
| `static void glSamplerParameterIuivEXT(int, int, java.nio.IntBuffer)` |  |
| `static void glTexBufferEXT(int, int, int)` |  |
| `static void glTexBufferRangeEXT(int, int, int, int, int)` |  |
| `static void glTexParameterIivEXT(int, int, int[], int)` |  |
| `static void glTexParameterIivEXT(int, int, java.nio.IntBuffer)` |  |
| `static void glTexParameterIuivEXT(int, int, int[], int)` |  |
| `static void glTexParameterIuivEXT(int, int, java.nio.IntBuffer)` |  |
| `static void glTexStorage3DMultisampleOES(int, int, int, int, int, int, boolean)` |  |

---

### `interface static GLES31Ext.DebugProcKHR`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onMessage(int, int, int, int, String)` |  |

---

### `class GLES32`

- **继承：** `android.opengl.GLES31`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int GL_BUFFER = 33504` |  |
| `static final int GL_CLAMP_TO_BORDER = 33069` |  |
| `static final int GL_COLORBURN = 37530` |  |
| `static final int GL_COLORDODGE = 37529` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_10x10 = 37819` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_10x5 = 37816` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_10x6 = 37817` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_10x8 = 37818` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_12x10 = 37820` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_12x12 = 37821` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_4x4 = 37808` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_5x4 = 37809` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_5x5 = 37810` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_6x5 = 37811` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_6x6 = 37812` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_8x5 = 37813` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_8x6 = 37814` |  |
| `static final int GL_COMPRESSED_RGBA_ASTC_8x8 = 37815` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x10 = 37851` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x5 = 37848` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x6 = 37849` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x8 = 37850` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_12x10 = 37852` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_12x12 = 37853` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_4x4 = 37840` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_5x4 = 37841` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_5x5 = 37842` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_6x5 = 37843` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_6x6 = 37844` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_8x5 = 37845` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_8x6 = 37846` |  |
| `static final int GL_COMPRESSED_SRGB8_ALPHA8_ASTC_8x8 = 37847` |  |
| `static final int GL_CONTEXT_FLAGS = 33310` |  |
| `static final int GL_CONTEXT_FLAG_DEBUG_BIT = 2` |  |
| `static final int GL_CONTEXT_FLAG_ROBUST_ACCESS_BIT = 4` |  |
| `static final int GL_CONTEXT_LOST = 1287` |  |
| `static final int GL_DARKEN = 37527` |  |
| `static final int GL_DEBUG_CALLBACK_FUNCTION = 33348` |  |
| `static final int GL_DEBUG_CALLBACK_USER_PARAM = 33349` |  |
| `static final int GL_DEBUG_GROUP_STACK_DEPTH = 33389` |  |
| `static final int GL_DEBUG_LOGGED_MESSAGES = 37189` |  |
| `static final int GL_DEBUG_NEXT_LOGGED_MESSAGE_LENGTH = 33347` |  |
| `static final int GL_DEBUG_OUTPUT = 37600` |  |
| `static final int GL_DEBUG_OUTPUT_SYNCHRONOUS = 33346` |  |
| `static final int GL_DEBUG_SEVERITY_HIGH = 37190` |  |
| `static final int GL_DEBUG_SEVERITY_LOW = 37192` |  |
| `static final int GL_DEBUG_SEVERITY_MEDIUM = 37191` |  |
| `static final int GL_DEBUG_SEVERITY_NOTIFICATION = 33387` |  |
| `static final int GL_DEBUG_SOURCE_API = 33350` |  |
| `static final int GL_DEBUG_SOURCE_APPLICATION = 33354` |  |
| `static final int GL_DEBUG_SOURCE_OTHER = 33355` |  |
| `static final int GL_DEBUG_SOURCE_SHADER_COMPILER = 33352` |  |
| `static final int GL_DEBUG_SOURCE_THIRD_PARTY = 33353` |  |
| `static final int GL_DEBUG_SOURCE_WINDOW_SYSTEM = 33351` |  |
| `static final int GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR = 33357` |  |
| `static final int GL_DEBUG_TYPE_ERROR = 33356` |  |
| `static final int GL_DEBUG_TYPE_MARKER = 33384` |  |
| `static final int GL_DEBUG_TYPE_OTHER = 33361` |  |
| `static final int GL_DEBUG_TYPE_PERFORMANCE = 33360` |  |
| `static final int GL_DEBUG_TYPE_POP_GROUP = 33386` |  |
| `static final int GL_DEBUG_TYPE_PORTABILITY = 33359` |  |
| `static final int GL_DEBUG_TYPE_PUSH_GROUP = 33385` |  |
| `static final int GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR = 33358` |  |
| `static final int GL_DIFFERENCE = 37534` |  |
| `static final int GL_EXCLUSION = 37536` |  |
| `static final int GL_FIRST_VERTEX_CONVENTION = 36429` |  |
| `static final int GL_FRACTIONAL_EVEN = 36476` |  |
| `static final int GL_FRACTIONAL_ODD = 36475` |  |
| `static final int GL_FRAGMENT_INTERPOLATION_OFFSET_BITS = 36445` |  |
| `static final int GL_FRAMEBUFFER_ATTACHMENT_LAYERED = 36263` |  |
| `static final int GL_FRAMEBUFFER_DEFAULT_LAYERS = 37650` |  |
| `static final int GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS = 36264` |  |
| `static final int GL_GEOMETRY_INPUT_TYPE = 35095` |  |
| `static final int GL_GEOMETRY_OUTPUT_TYPE = 35096` |  |
| `static final int GL_GEOMETRY_SHADER = 36313` |  |
| `static final int GL_GEOMETRY_SHADER_BIT = 4` |  |
| `static final int GL_GEOMETRY_SHADER_INVOCATIONS = 34943` |  |
| `static final int GL_GEOMETRY_VERTICES_OUT = 35094` |  |
| `static final int GL_GUILTY_CONTEXT_RESET = 33363` |  |
| `static final int GL_HARDLIGHT = 37531` |  |
| `static final int GL_HSL_COLOR = 37551` |  |
| `static final int GL_HSL_HUE = 37549` |  |
| `static final int GL_HSL_LUMINOSITY = 37552` |  |
| `static final int GL_HSL_SATURATION = 37550` |  |
| `static final int GL_IMAGE_BUFFER = 36945` |  |
| `static final int GL_IMAGE_CUBE_MAP_ARRAY = 36948` |  |
| `static final int GL_INNOCENT_CONTEXT_RESET = 33364` |  |
| `static final int GL_INT_IMAGE_BUFFER = 36956` |  |
| `static final int GL_INT_IMAGE_CUBE_MAP_ARRAY = 36959` |  |
| `static final int GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY = 37132` |  |
| `static final int GL_INT_SAMPLER_BUFFER = 36304` |  |
| `static final int GL_INT_SAMPLER_CUBE_MAP_ARRAY = 36878` |  |
| `static final int GL_ISOLINES = 36474` |  |
| `static final int GL_IS_PER_PATCH = 37607` |  |
| `static final int GL_LAST_VERTEX_CONVENTION = 36430` |  |
| `static final int GL_LAYER_PROVOKING_VERTEX = 33374` |  |
| `static final int GL_LIGHTEN = 37528` |  |
| `static final int GL_LINES_ADJACENCY = 10` |  |
| `static final int GL_LINE_STRIP_ADJACENCY = 11` |  |
| `static final int GL_LOSE_CONTEXT_ON_RESET = 33362` |  |
| `static final int GL_MAX_COMBINED_GEOMETRY_UNIFORM_COMPONENTS = 35378` |  |
| `static final int GL_MAX_COMBINED_TESS_CONTROL_UNIFORM_COMPONENTS = 36382` |  |
| `static final int GL_MAX_COMBINED_TESS_EVALUATION_UNIFORM_COMPONENTS = 36383` |  |
| `static final int GL_MAX_DEBUG_GROUP_STACK_DEPTH = 33388` |  |
| `static final int GL_MAX_DEBUG_LOGGED_MESSAGES = 37188` |  |
| `static final int GL_MAX_DEBUG_MESSAGE_LENGTH = 37187` |  |
| `static final int GL_MAX_FRAGMENT_INTERPOLATION_OFFSET = 36444` |  |
| `static final int GL_MAX_FRAMEBUFFER_LAYERS = 37655` |  |
| `static final int GL_MAX_GEOMETRY_ATOMIC_COUNTERS = 37589` |  |
| `static final int GL_MAX_GEOMETRY_ATOMIC_COUNTER_BUFFERS = 37583` |  |
| `static final int GL_MAX_GEOMETRY_IMAGE_UNIFORMS = 37069` |  |
| `static final int GL_MAX_GEOMETRY_INPUT_COMPONENTS = 37155` |  |
| `static final int GL_MAX_GEOMETRY_OUTPUT_COMPONENTS = 37156` |  |
| `static final int GL_MAX_GEOMETRY_OUTPUT_VERTICES = 36320` |  |
| `static final int GL_MAX_GEOMETRY_SHADER_INVOCATIONS = 36442` |  |
| `static final int GL_MAX_GEOMETRY_SHADER_STORAGE_BLOCKS = 37079` |  |
| `static final int GL_MAX_GEOMETRY_TEXTURE_IMAGE_UNITS = 35881` |  |
| `static final int GL_MAX_GEOMETRY_TOTAL_OUTPUT_COMPONENTS = 36321` |  |
| `static final int GL_MAX_GEOMETRY_UNIFORM_BLOCKS = 35372` |  |
| `static final int GL_MAX_GEOMETRY_UNIFORM_COMPONENTS = 36319` |  |
| `static final int GL_MAX_LABEL_LENGTH = 33512` |  |
| `static final int GL_MAX_PATCH_VERTICES = 36477` |  |
| `static final int GL_MAX_TESS_CONTROL_ATOMIC_COUNTERS = 37587` |  |
| `static final int GL_MAX_TESS_CONTROL_ATOMIC_COUNTER_BUFFERS = 37581` |  |
| `static final int GL_MAX_TESS_CONTROL_IMAGE_UNIFORMS = 37067` |  |
| `static final int GL_MAX_TESS_CONTROL_INPUT_COMPONENTS = 34924` |  |
| `static final int GL_MAX_TESS_CONTROL_OUTPUT_COMPONENTS = 36483` |  |
| `static final int GL_MAX_TESS_CONTROL_SHADER_STORAGE_BLOCKS = 37080` |  |
| `static final int GL_MAX_TESS_CONTROL_TEXTURE_IMAGE_UNITS = 36481` |  |
| `static final int GL_MAX_TESS_CONTROL_TOTAL_OUTPUT_COMPONENTS = 36485` |  |
| `static final int GL_MAX_TESS_CONTROL_UNIFORM_BLOCKS = 36489` |  |
| `static final int GL_MAX_TESS_CONTROL_UNIFORM_COMPONENTS = 36479` |  |
| `static final int GL_MAX_TESS_EVALUATION_ATOMIC_COUNTERS = 37588` |  |
| `static final int GL_MAX_TESS_EVALUATION_ATOMIC_COUNTER_BUFFERS = 37582` |  |
| `static final int GL_MAX_TESS_EVALUATION_IMAGE_UNIFORMS = 37068` |  |
| `static final int GL_MAX_TESS_EVALUATION_INPUT_COMPONENTS = 34925` |  |
| `static final int GL_MAX_TESS_EVALUATION_OUTPUT_COMPONENTS = 36486` |  |
| `static final int GL_MAX_TESS_EVALUATION_SHADER_STORAGE_BLOCKS = 37081` |  |
| `static final int GL_MAX_TESS_EVALUATION_TEXTURE_IMAGE_UNITS = 36482` |  |
| `static final int GL_MAX_TESS_EVALUATION_UNIFORM_BLOCKS = 36490` |  |
| `static final int GL_MAX_TESS_EVALUATION_UNIFORM_COMPONENTS = 36480` |  |
| `static final int GL_MAX_TESS_GEN_LEVEL = 36478` |  |
| `static final int GL_MAX_TESS_PATCH_COMPONENTS = 36484` |  |
| `static final int GL_MAX_TEXTURE_BUFFER_SIZE = 35883` |  |
| `static final int GL_MIN_FRAGMENT_INTERPOLATION_OFFSET = 36443` |  |
| `static final int GL_MIN_SAMPLE_SHADING_VALUE = 35895` |  |
| `static final int GL_MULTIPLY = 37524` |  |
| `static final int GL_MULTISAMPLE_LINE_WIDTH_GRANULARITY = 37762` |  |
| `static final int GL_MULTISAMPLE_LINE_WIDTH_RANGE = 37761` |  |
| `static final int GL_NO_RESET_NOTIFICATION = 33377` |  |
| `static final int GL_OVERLAY = 37526` |  |
| `static final int GL_PATCHES = 14` |  |
| `static final int GL_PATCH_VERTICES = 36466` |  |
| `static final int GL_PRIMITIVES_GENERATED = 35975` |  |
| `static final int GL_PRIMITIVE_BOUNDING_BOX = 37566` |  |
| `static final int GL_PRIMITIVE_RESTART_FOR_PATCHES_SUPPORTED = 33313` |  |
| `static final int GL_PROGRAM = 33506` |  |
| `static final int GL_PROGRAM_PIPELINE = 33508` |  |
| `static final int GL_QUADS = 7` |  |
| `static final int GL_QUERY = 33507` |  |
| `static final int GL_REFERENCED_BY_GEOMETRY_SHADER = 37641` |  |
| `static final int GL_REFERENCED_BY_TESS_CONTROL_SHADER = 37639` |  |
| `static final int GL_REFERENCED_BY_TESS_EVALUATION_SHADER = 37640` |  |
| `static final int GL_RESET_NOTIFICATION_STRATEGY = 33366` |  |
| `static final int GL_SAMPLER = 33510` |  |
| `static final int GL_SAMPLER_2D_MULTISAMPLE_ARRAY = 37131` |  |
| `static final int GL_SAMPLER_BUFFER = 36290` |  |
| `static final int GL_SAMPLER_CUBE_MAP_ARRAY = 36876` |  |
| `static final int GL_SAMPLER_CUBE_MAP_ARRAY_SHADOW = 36877` |  |
| `static final int GL_SAMPLE_SHADING = 35894` |  |
| `static final int GL_SCREEN = 37525` |  |
| `static final int GL_SHADER = 33505` |  |
| `static final int GL_SOFTLIGHT = 37532` |  |
| `static final int GL_STACK_OVERFLOW = 1283` |  |
| `static final int GL_STACK_UNDERFLOW = 1284` |  |
| `static final int GL_TESS_CONTROL_OUTPUT_VERTICES = 36469` |  |
| `static final int GL_TESS_CONTROL_SHADER = 36488` |  |
| `static final int GL_TESS_CONTROL_SHADER_BIT = 8` |  |
| `static final int GL_TESS_EVALUATION_SHADER = 36487` |  |
| `static final int GL_TESS_EVALUATION_SHADER_BIT = 16` |  |
| `static final int GL_TESS_GEN_MODE = 36470` |  |
| `static final int GL_TESS_GEN_POINT_MODE = 36473` |  |
| `static final int GL_TESS_GEN_SPACING = 36471` |  |
| `static final int GL_TESS_GEN_VERTEX_ORDER = 36472` |  |
| `static final int GL_TEXTURE_2D_MULTISAMPLE_ARRAY = 37122` |  |
| `static final int GL_TEXTURE_BINDING_2D_MULTISAMPLE_ARRAY = 37125` |  |
| `static final int GL_TEXTURE_BINDING_BUFFER = 35884` |  |
| `static final int GL_TEXTURE_BINDING_CUBE_MAP_ARRAY = 36874` |  |
| `static final int GL_TEXTURE_BORDER_COLOR = 4100` |  |
| `static final int GL_TEXTURE_BUFFER = 35882` |  |
| `static final int GL_TEXTURE_BUFFER_BINDING = 35882` |  |
| `static final int GL_TEXTURE_BUFFER_DATA_STORE_BINDING = 35885` |  |
| `static final int GL_TEXTURE_BUFFER_OFFSET = 37277` |  |
| `static final int GL_TEXTURE_BUFFER_OFFSET_ALIGNMENT = 37279` |  |
| `static final int GL_TEXTURE_BUFFER_SIZE = 37278` |  |
| `static final int GL_TEXTURE_CUBE_MAP_ARRAY = 36873` |  |
| `static final int GL_TRIANGLES_ADJACENCY = 12` |  |
| `static final int GL_TRIANGLE_STRIP_ADJACENCY = 13` |  |
| `static final int GL_UNDEFINED_VERTEX = 33376` |  |
| `static final int GL_UNKNOWN_CONTEXT_RESET = 33365` |  |
| `static final int GL_UNSIGNED_INT_IMAGE_BUFFER = 36967` |  |
| `static final int GL_UNSIGNED_INT_IMAGE_CUBE_MAP_ARRAY = 36970` |  |
| `static final int GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY = 37133` |  |
| `static final int GL_UNSIGNED_INT_SAMPLER_BUFFER = 36312` |  |
| `static final int GL_UNSIGNED_INT_SAMPLER_CUBE_MAP_ARRAY = 36879` |  |
| `static final int GL_VERTEX_ARRAY = 32884` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void glBlendBarrier()` |  |
| `static void glBlendEquationSeparatei(int, int, int)` |  |
| `static void glBlendEquationi(int, int)` |  |
| `static void glBlendFuncSeparatei(int, int, int, int, int)` |  |
| `static void glBlendFunci(int, int, int)` |  |
| `static void glColorMaski(int, boolean, boolean, boolean, boolean)` |  |
| `static void glCopyImageSubData(int, int, int, int, int, int, int, int, int, int, int, int, int, int, int)` |  |
| `static void glDebugMessageCallback(android.opengl.GLES32.DebugProc)` |  |
| `static void glDebugMessageControl(int, int, int, int, int[], int, boolean)` |  |
| `static void glDebugMessageControl(int, int, int, int, java.nio.IntBuffer, boolean)` |  |
| `static void glDebugMessageInsert(int, int, int, int, int, String)` |  |
| `static void glDisablei(int, int)` |  |
| `static void glDrawElementsBaseVertex(int, int, int, java.nio.Buffer, int)` |  |
| `static void glDrawElementsInstancedBaseVertex(int, int, int, java.nio.Buffer, int, int)` |  |
| `static void glDrawElementsInstancedBaseVertex(int, int, int, int, int, int)` |  |
| `static void glDrawRangeElementsBaseVertex(int, int, int, int, int, java.nio.Buffer, int)` |  |
| `static void glEnablei(int, int)` |  |
| `static void glFramebufferTexture(int, int, int, int)` |  |
| `static int glGetDebugMessageLog(int, int, int[], int, int[], int, int[], int, int[], int, int[], int, byte[], int)` |  |
| `static int glGetDebugMessageLog(int, java.nio.IntBuffer, java.nio.IntBuffer, java.nio.IntBuffer, java.nio.IntBuffer, java.nio.IntBuffer, java.nio.ByteBuffer)` |  |
| `static String[] glGetDebugMessageLog(int, int[], int, int[], int, int[], int, int[], int)` |  |
| `static String[] glGetDebugMessageLog(int, java.nio.IntBuffer, java.nio.IntBuffer, java.nio.IntBuffer, java.nio.IntBuffer)` |  |
| `static int glGetGraphicsResetStatus()` |  |
| `static String glGetObjectLabel(int, int)` |  |
| `static String glGetObjectPtrLabel(long)` |  |
| `static long glGetPointerv(int)` |  |
| `static void glGetSamplerParameterIiv(int, int, int[], int)` |  |
| `static void glGetSamplerParameterIiv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetSamplerParameterIuiv(int, int, int[], int)` |  |
| `static void glGetSamplerParameterIuiv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetTexParameterIiv(int, int, int[], int)` |  |
| `static void glGetTexParameterIiv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetTexParameterIuiv(int, int, int[], int)` |  |
| `static void glGetTexParameterIuiv(int, int, java.nio.IntBuffer)` |  |
| `static void glGetnUniformfv(int, int, int, float[], int)` |  |
| `static void glGetnUniformfv(int, int, int, java.nio.FloatBuffer)` |  |
| `static void glGetnUniformiv(int, int, int, int[], int)` |  |
| `static void glGetnUniformiv(int, int, int, java.nio.IntBuffer)` |  |
| `static void glGetnUniformuiv(int, int, int, int[], int)` |  |
| `static void glGetnUniformuiv(int, int, int, java.nio.IntBuffer)` |  |
| `static boolean glIsEnabledi(int, int)` |  |
| `static void glMinSampleShading(float)` |  |
| `static void glObjectLabel(int, int, int, String)` |  |
| `static void glObjectPtrLabel(long, String)` |  |
| `static void glPatchParameteri(int, int)` |  |
| `static void glPopDebugGroup()` |  |
| `static void glPrimitiveBoundingBox(float, float, float, float, float, float, float, float)` |  |
| `static void glPushDebugGroup(int, int, int, String)` |  |
| `static void glReadnPixels(int, int, int, int, int, int, int, java.nio.Buffer)` |  |
| `static void glSamplerParameterIiv(int, int, int[], int)` |  |
| `static void glSamplerParameterIiv(int, int, java.nio.IntBuffer)` |  |
| `static void glSamplerParameterIuiv(int, int, int[], int)` |  |
| `static void glSamplerParameterIuiv(int, int, java.nio.IntBuffer)` |  |
| `static void glTexBuffer(int, int, int)` |  |
| `static void glTexBufferRange(int, int, int, int, int)` |  |
| `static void glTexParameterIiv(int, int, int[], int)` |  |
| `static void glTexParameterIiv(int, int, java.nio.IntBuffer)` |  |
| `static void glTexParameterIuiv(int, int, int[], int)` |  |
| `static void glTexParameterIuiv(int, int, java.nio.IntBuffer)` |  |
| `static void glTexStorage3DMultisample(int, int, int, int, int, int, boolean)` |  |

---

### `interface static GLES32.DebugProc`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onMessage(int, int, int, int, String)` |  |

---

### `class GLException`

- **继承：** `java.lang.RuntimeException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GLException(int)` |  |
| `GLException(int, String)` |  |

---

### `class GLSurfaceView`

- **继承：** `android.view.SurfaceView`
- **实现：** `android.view.SurfaceHolder.Callback2`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GLSurfaceView(android.content.Context)` |  |
| `GLSurfaceView(android.content.Context, android.util.AttributeSet)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DEBUG_CHECK_GL_ERROR = 1` |  |
| `static final int DEBUG_LOG_GL_CALLS = 2` |  |
| `static final int RENDERMODE_CONTINUOUSLY = 1` |  |
| `static final int RENDERMODE_WHEN_DIRTY = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getDebugFlags()` |  |
| `boolean getPreserveEGLContextOnPause()` |  |
| `int getRenderMode()` |  |
| `void onPause()` |  |
| `void onResume()` |  |
| `void queueEvent(Runnable)` |  |
| `void requestRender()` |  |
| `void setDebugFlags(int)` |  |
| `void setEGLConfigChooser(android.opengl.GLSurfaceView.EGLConfigChooser)` |  |
| `void setEGLConfigChooser(boolean)` |  |
| `void setEGLConfigChooser(int, int, int, int, int, int)` |  |
| `void setEGLContextClientVersion(int)` |  |
| `void setEGLContextFactory(android.opengl.GLSurfaceView.EGLContextFactory)` |  |
| `void setEGLWindowSurfaceFactory(android.opengl.GLSurfaceView.EGLWindowSurfaceFactory)` |  |
| `void setGLWrapper(android.opengl.GLSurfaceView.GLWrapper)` |  |
| `void setPreserveEGLContextOnPause(boolean)` |  |
| `void setRenderMode(int)` |  |
| `void setRenderer(android.opengl.GLSurfaceView.Renderer)` |  |
| `void surfaceChanged(android.view.SurfaceHolder, int, int, int)` |  |
| `void surfaceCreated(android.view.SurfaceHolder)` |  |
| `void surfaceDestroyed(android.view.SurfaceHolder)` |  |

---

### `interface static GLSurfaceView.EGLConfigChooser`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `javax.microedition.khronos.egl.EGLConfig chooseConfig(javax.microedition.khronos.egl.EGL10, javax.microedition.khronos.egl.EGLDisplay)` |  |

---

### `interface static GLSurfaceView.EGLContextFactory`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `javax.microedition.khronos.egl.EGLContext createContext(javax.microedition.khronos.egl.EGL10, javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLConfig)` |  |
| `void destroyContext(javax.microedition.khronos.egl.EGL10, javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLContext)` |  |

---

### `interface static GLSurfaceView.EGLWindowSurfaceFactory`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `javax.microedition.khronos.egl.EGLSurface createWindowSurface(javax.microedition.khronos.egl.EGL10, javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLConfig, Object)` |  |
| `void destroySurface(javax.microedition.khronos.egl.EGL10, javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLSurface)` |  |

---

### `interface static GLSurfaceView.GLWrapper`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `javax.microedition.khronos.opengles.GL wrap(javax.microedition.khronos.opengles.GL)` |  |

---

### `interface static GLSurfaceView.Renderer`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onDrawFrame(javax.microedition.khronos.opengles.GL10)` |  |
| `void onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)` |  |
| `void onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)` |  |

---

### `class GLU`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GLU()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static String gluErrorString(int)` |  |
| `static void gluLookAt(javax.microedition.khronos.opengles.GL10, float, float, float, float, float, float, float, float, float)` |  |
| `static void gluOrtho2D(javax.microedition.khronos.opengles.GL10, float, float, float, float)` |  |
| `static void gluPerspective(javax.microedition.khronos.opengles.GL10, float, float, float, float)` |  |
| `static int gluProject(float, float, float, float[], int, float[], int, int[], int, float[], int)` |  |
| `static int gluUnProject(float, float, float, float[], int, float[], int, int[], int, float[], int)` |  |

---

### `class final GLUtils`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static String getEGLErrorString(int)` |  |
| `static int getInternalFormat(android.graphics.Bitmap)` |  |
| `static int getType(android.graphics.Bitmap)` |  |
| `static void texImage2D(int, int, int, android.graphics.Bitmap, int)` |  |
| `static void texImage2D(int, int, int, android.graphics.Bitmap, int, int)` |  |
| `static void texImage2D(int, int, android.graphics.Bitmap, int)` |  |
| `static void texSubImage2D(int, int, int, int, android.graphics.Bitmap)` |  |
| `static void texSubImage2D(int, int, int, int, android.graphics.Bitmap, int, int)` |  |

---

### `class Matrix`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void frustumM(float[], int, float, float, float, float, float, float)` |  |
| `static boolean invertM(float[], int, float[], int)` |  |
| `static float length(float, float, float)` |  |
| `static void multiplyMM(float[], int, float[], int, float[], int)` |  |
| `static void multiplyMV(float[], int, float[], int, float[], int)` |  |
| `static void orthoM(float[], int, float, float, float, float, float, float)` |  |
| `static void perspectiveM(float[], int, float, float, float, float)` |  |
| `static void rotateM(float[], int, float[], int, float, float, float, float)` |  |
| `static void rotateM(float[], int, float, float, float, float)` |  |
| `static void scaleM(float[], int, float[], int, float, float, float)` |  |
| `static void scaleM(float[], int, float, float, float)` |  |
| `static void setIdentityM(float[], int)` |  |
| `static void setLookAtM(float[], int, float, float, float, float, float, float, float, float, float)` |  |
| `static void setRotateEulerM(float[], int, float, float, float)` |  |
| `static void setRotateM(float[], int, float, float, float, float)` |  |
| `static void translateM(float[], int, float[], int, float, float, float)` |  |
| `static void translateM(float[], int, float, float, float)` |  |
| `static void transposeM(float[], int, float[], int)` |  |

---

### `class Visibility`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Visibility()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void computeBoundingSphere(float[], int, int, float[], int)` |  |
| `static int frustumCullSpheres(float[], int, float[], int, int, int[], int, int)` |  |
| `static int visibilityTest(float[], int, float[], int, char[], int, int)` |  |

---

## Package: `android.renderscript`

### `class Allocation`

- **继承：** `android.renderscript.BaseObj`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int USAGE_GRAPHICS_CONSTANTS = 8` |  |
| `static final int USAGE_GRAPHICS_RENDER_TARGET = 16` |  |
| `static final int USAGE_GRAPHICS_TEXTURE = 2` |  |
| `static final int USAGE_GRAPHICS_VERTEX = 4` |  |
| `static final int USAGE_IO_INPUT = 32` |  |
| `static final int USAGE_IO_OUTPUT = 64` |  |
| `static final int USAGE_SCRIPT = 1` |  |
| `static final int USAGE_SHARED = 128` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void copy1DRangeFrom(int, int, Object)` |  |
| `void copy1DRangeFrom(int, int, int[])` |  |
| `void copy1DRangeFrom(int, int, short[])` |  |
| `void copy1DRangeFrom(int, int, byte[])` |  |
| `void copy1DRangeFrom(int, int, float[])` |  |
| `void copy1DRangeFrom(int, int, android.renderscript.Allocation, int)` |  |
| `void copy1DRangeFromUnchecked(int, int, Object)` |  |
| `void copy1DRangeFromUnchecked(int, int, int[])` |  |
| `void copy1DRangeFromUnchecked(int, int, short[])` |  |
| `void copy1DRangeFromUnchecked(int, int, byte[])` |  |
| `void copy1DRangeFromUnchecked(int, int, float[])` |  |
| `void copy1DRangeTo(int, int, Object)` |  |
| `void copy1DRangeTo(int, int, int[])` |  |
| `void copy1DRangeTo(int, int, short[])` |  |
| `void copy1DRangeTo(int, int, byte[])` |  |
| `void copy1DRangeTo(int, int, float[])` |  |
| `void copy1DRangeToUnchecked(int, int, Object)` |  |
| `void copy1DRangeToUnchecked(int, int, int[])` |  |
| `void copy1DRangeToUnchecked(int, int, short[])` |  |
| `void copy1DRangeToUnchecked(int, int, byte[])` |  |
| `void copy1DRangeToUnchecked(int, int, float[])` |  |
| `void copy2DRangeFrom(int, int, int, int, Object)` |  |
| `void copy2DRangeFrom(int, int, int, int, byte[])` |  |
| `void copy2DRangeFrom(int, int, int, int, short[])` |  |
| `void copy2DRangeFrom(int, int, int, int, int[])` |  |
| `void copy2DRangeFrom(int, int, int, int, float[])` |  |
| `void copy2DRangeFrom(int, int, int, int, android.renderscript.Allocation, int, int)` |  |
| `void copy2DRangeFrom(int, int, android.graphics.Bitmap)` |  |
| `void copy2DRangeTo(int, int, int, int, Object)` |  |
| `void copy2DRangeTo(int, int, int, int, byte[])` |  |
| `void copy2DRangeTo(int, int, int, int, short[])` |  |
| `void copy2DRangeTo(int, int, int, int, int[])` |  |
| `void copy2DRangeTo(int, int, int, int, float[])` |  |
| `void copy3DRangeFrom(int, int, int, int, int, int, Object)` |  |
| `void copy3DRangeFrom(int, int, int, int, int, int, android.renderscript.Allocation, int, int, int)` |  |
| `void copy3DRangeTo(int, int, int, int, int, int, Object)` |  |
| `void copyFrom(android.renderscript.BaseObj[])` |  |
| `void copyFrom(Object)` |  |
| `void copyFrom(int[])` |  |
| `void copyFrom(short[])` |  |
| `void copyFrom(byte[])` |  |
| `void copyFrom(float[])` |  |
| `void copyFrom(android.graphics.Bitmap)` |  |
| `void copyFrom(android.renderscript.Allocation)` |  |
| `void copyFromUnchecked(Object)` |  |
| `void copyFromUnchecked(int[])` |  |
| `void copyFromUnchecked(short[])` |  |
| `void copyFromUnchecked(byte[])` |  |
| `void copyFromUnchecked(float[])` |  |
| `void copyTo(android.graphics.Bitmap)` |  |
| `void copyTo(Object)` |  |
| `void copyTo(byte[])` |  |
| `void copyTo(short[])` |  |
| `void copyTo(int[])` |  |
| `void copyTo(float[])` |  |
| `static android.renderscript.Allocation[] createAllocations(android.renderscript.RenderScript, android.renderscript.Type, int, int)` |  |
| `static android.renderscript.Allocation createCubemapFromBitmap(android.renderscript.RenderScript, android.graphics.Bitmap, android.renderscript.Allocation.MipmapControl, int)` |  |
| `static android.renderscript.Allocation createCubemapFromBitmap(android.renderscript.RenderScript, android.graphics.Bitmap)` |  |
| `static android.renderscript.Allocation createCubemapFromCubeFaces(android.renderscript.RenderScript, android.graphics.Bitmap, android.graphics.Bitmap, android.graphics.Bitmap, android.graphics.Bitmap, android.graphics.Bitmap, android.graphics.Bitmap, android.renderscript.Allocation.MipmapControl, int)` |  |
| `static android.renderscript.Allocation createCubemapFromCubeFaces(android.renderscript.RenderScript, android.graphics.Bitmap, android.graphics.Bitmap, android.graphics.Bitmap, android.graphics.Bitmap, android.graphics.Bitmap, android.graphics.Bitmap)` |  |
| `static android.renderscript.Allocation createFromBitmap(android.renderscript.RenderScript, android.graphics.Bitmap, android.renderscript.Allocation.MipmapControl, int)` |  |
| `static android.renderscript.Allocation createFromBitmap(android.renderscript.RenderScript, android.graphics.Bitmap)` |  |
| `static android.renderscript.Allocation createFromBitmapResource(android.renderscript.RenderScript, android.content.res.Resources, int, android.renderscript.Allocation.MipmapControl, int)` |  |
| `static android.renderscript.Allocation createFromBitmapResource(android.renderscript.RenderScript, android.content.res.Resources, int)` |  |
| `static android.renderscript.Allocation createFromString(android.renderscript.RenderScript, String, int)` |  |
| `static android.renderscript.Allocation createSized(android.renderscript.RenderScript, android.renderscript.Element, int, int)` |  |
| `static android.renderscript.Allocation createSized(android.renderscript.RenderScript, android.renderscript.Element, int)` |  |
| `static android.renderscript.Allocation createTyped(android.renderscript.RenderScript, android.renderscript.Type, android.renderscript.Allocation.MipmapControl, int)` |  |
| `static android.renderscript.Allocation createTyped(android.renderscript.RenderScript, android.renderscript.Type, int)` |  |
| `static android.renderscript.Allocation createTyped(android.renderscript.RenderScript, android.renderscript.Type)` |  |
| `void generateMipmaps()` |  |
| `java.nio.ByteBuffer getByteBuffer()` |  |
| `int getBytesSize()` |  |
| `android.renderscript.Element getElement()` |  |
| `long getStride()` |  |
| `android.view.Surface getSurface()` |  |
| `long getTimeStamp()` |  |
| `android.renderscript.Type getType()` |  |
| `int getUsage()` |  |
| `void ioReceive()` |  |
| `void ioSend()` |  |
| `void setAutoPadding(boolean)` |  |
| `void setFromFieldPacker(int, android.renderscript.FieldPacker)` |  |
| `void setFromFieldPacker(int, int, android.renderscript.FieldPacker)` |  |
| `void setFromFieldPacker(int, int, int, int, android.renderscript.FieldPacker)` |  |
| `void setOnBufferAvailableListener(android.renderscript.Allocation.OnBufferAvailableListener)` |  |
| `void setSurface(android.view.Surface)` |  |
| `void syncAll(int)` |  |

---

### `enum Allocation.MipmapControl`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.renderscript.Allocation.MipmapControl MIPMAP_FULL` |  |
| `static final android.renderscript.Allocation.MipmapControl MIPMAP_NONE` |  |
| `static final android.renderscript.Allocation.MipmapControl MIPMAP_ON_SYNC_TO_TEXTURE` |  |

---

### `interface static Allocation.OnBufferAvailableListener`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onBufferAvailable(android.renderscript.Allocation)` |  |

---

### `class AllocationAdapter`

- **继承：** `android.renderscript.Allocation`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.renderscript.AllocationAdapter create1D(android.renderscript.RenderScript, android.renderscript.Allocation)` |  |
| `static android.renderscript.AllocationAdapter create2D(android.renderscript.RenderScript, android.renderscript.Allocation)` |  |
| `static android.renderscript.AllocationAdapter createTyped(android.renderscript.RenderScript, android.renderscript.Allocation, android.renderscript.Type)` |  |
| `void resize(int)` |  |
| `void setFace(android.renderscript.Type.CubemapFace)` |  |
| `void setLOD(int)` |  |
| `void setX(int)` |  |
| `void setY(int)` |  |
| `void setZ(int)` |  |

---

### `class BaseObj`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void destroy()` |  |
| `String getName()` |  |
| `void setName(String)` |  |

---

### `class Byte2`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Byte2()` |  |
| `Byte2(byte, byte)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `byte x` |  |
| `byte y` |  |

---

### `class Byte3`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Byte3()` |  |
| `Byte3(byte, byte, byte)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `byte x` |  |
| `byte y` |  |
| `byte z` |  |

---

### `class Byte4`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Byte4()` |  |
| `Byte4(byte, byte, byte, byte)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `byte w` |  |
| `byte x` |  |
| `byte y` |  |
| `byte z` |  |

---

### `class Double2`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Double2()` |  |
| `Double2(double, double)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `double x` |  |
| `double y` |  |

---

### `class Double3`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Double3()` |  |
| `Double3(double, double, double)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `double x` |  |
| `double y` |  |
| `double z` |  |

---

### `class Double4`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Double4()` |  |
| `Double4(double, double, double, double)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `double w` |  |
| `double x` |  |
| `double y` |  |
| `double z` |  |

---

### `class Element`

- **继承：** `android.renderscript.BaseObj`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.renderscript.Element ALLOCATION(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element A_8(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element BOOLEAN(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element ELEMENT(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element F16(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element F16_2(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element F16_3(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element F16_4(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element F32(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element F32_2(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element F32_3(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element F32_4(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element F64(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element F64_2(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element F64_3(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element F64_4(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element FONT(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I16(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I16_2(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I16_3(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I16_4(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I32(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I32_2(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I32_3(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I32_4(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I64(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I64_2(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I64_3(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I64_4(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I8(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I8_2(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I8_3(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element I8_4(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element MATRIX_2X2(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element MATRIX_3X3(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element MATRIX_4X4(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element MESH(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element PROGRAM_FRAGMENT(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element PROGRAM_RASTER(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element PROGRAM_STORE(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element PROGRAM_VERTEX(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element RGBA_4444(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element RGBA_5551(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element RGBA_8888(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element RGB_565(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element RGB_888(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element SAMPLER(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element SCRIPT(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element TYPE(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U16(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U16_2(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U16_3(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U16_4(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U32(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U32_2(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U32_3(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U32_4(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U64(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U64_2(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U64_3(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U64_4(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U8(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U8_2(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U8_3(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element U8_4(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element YUV(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Element createPixel(android.renderscript.RenderScript, android.renderscript.Element.DataType, android.renderscript.Element.DataKind)` |  |
| `static android.renderscript.Element createVector(android.renderscript.RenderScript, android.renderscript.Element.DataType, int)` |  |
| `int getBytesSize()` |  |
| `android.renderscript.Element.DataKind getDataKind()` |  |
| `android.renderscript.Element.DataType getDataType()` |  |
| `android.renderscript.Element getSubElement(int)` |  |
| `int getSubElementArraySize(int)` |  |
| `int getSubElementCount()` |  |
| `String getSubElementName(int)` |  |
| `int getSubElementOffsetBytes(int)` |  |
| `int getVectorSize()` |  |
| `boolean isCompatible(android.renderscript.Element)` |  |
| `boolean isComplex()` |  |

---

### `class static Element.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Element.Builder(android.renderscript.RenderScript)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.renderscript.Element.Builder add(android.renderscript.Element, String, int)` |  |
| `android.renderscript.Element.Builder add(android.renderscript.Element, String)` |  |
| `android.renderscript.Element create()` |  |

---

### `enum Element.DataKind`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.renderscript.Element.DataKind PIXEL_A` |  |
| `static final android.renderscript.Element.DataKind PIXEL_DEPTH` |  |
| `static final android.renderscript.Element.DataKind PIXEL_L` |  |
| `static final android.renderscript.Element.DataKind PIXEL_LA` |  |
| `static final android.renderscript.Element.DataKind PIXEL_RGB` |  |
| `static final android.renderscript.Element.DataKind PIXEL_RGBA` |  |
| `static final android.renderscript.Element.DataKind PIXEL_YUV` |  |
| `static final android.renderscript.Element.DataKind USER` |  |

---

### `enum Element.DataType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.renderscript.Element.DataType BOOLEAN` |  |
| `static final android.renderscript.Element.DataType FLOAT_16` |  |
| `static final android.renderscript.Element.DataType FLOAT_32` |  |
| `static final android.renderscript.Element.DataType FLOAT_64` |  |
| `static final android.renderscript.Element.DataType MATRIX_2X2` |  |
| `static final android.renderscript.Element.DataType MATRIX_3X3` |  |
| `static final android.renderscript.Element.DataType MATRIX_4X4` |  |
| `static final android.renderscript.Element.DataType NONE` |  |
| `static final android.renderscript.Element.DataType RS_ALLOCATION` |  |
| `static final android.renderscript.Element.DataType RS_ELEMENT` |  |
| `static final android.renderscript.Element.DataType RS_FONT` |  |
| `static final android.renderscript.Element.DataType RS_MESH` |  |
| `static final android.renderscript.Element.DataType RS_PROGRAM_FRAGMENT` |  |
| `static final android.renderscript.Element.DataType RS_PROGRAM_RASTER` |  |
| `static final android.renderscript.Element.DataType RS_PROGRAM_STORE` |  |
| `static final android.renderscript.Element.DataType RS_PROGRAM_VERTEX` |  |
| `static final android.renderscript.Element.DataType RS_SAMPLER` |  |
| `static final android.renderscript.Element.DataType RS_SCRIPT` |  |
| `static final android.renderscript.Element.DataType RS_TYPE` |  |
| `static final android.renderscript.Element.DataType SIGNED_16` |  |
| `static final android.renderscript.Element.DataType SIGNED_32` |  |
| `static final android.renderscript.Element.DataType SIGNED_64` |  |
| `static final android.renderscript.Element.DataType SIGNED_8` |  |
| `static final android.renderscript.Element.DataType UNSIGNED_16` |  |
| `static final android.renderscript.Element.DataType UNSIGNED_32` |  |
| `static final android.renderscript.Element.DataType UNSIGNED_4_4_4_4` |  |
| `static final android.renderscript.Element.DataType UNSIGNED_5_5_5_1` |  |
| `static final android.renderscript.Element.DataType UNSIGNED_5_6_5` |  |
| `static final android.renderscript.Element.DataType UNSIGNED_64` |  |
| `static final android.renderscript.Element.DataType UNSIGNED_8` |  |

---

### `class FieldPacker`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `FieldPacker(int)` |  |
| `FieldPacker(byte[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addBoolean(boolean)` |  |
| `void addF32(float)` |  |
| `void addF32(android.renderscript.Float2)` |  |
| `void addF32(android.renderscript.Float3)` |  |
| `void addF32(android.renderscript.Float4)` |  |
| `void addF64(double)` |  |
| `void addF64(android.renderscript.Double2)` |  |
| `void addF64(android.renderscript.Double3)` |  |
| `void addF64(android.renderscript.Double4)` |  |
| `void addI16(short)` |  |
| `void addI16(android.renderscript.Short2)` |  |
| `void addI16(android.renderscript.Short3)` |  |
| `void addI16(android.renderscript.Short4)` |  |
| `void addI32(int)` |  |
| `void addI32(android.renderscript.Int2)` |  |
| `void addI32(android.renderscript.Int3)` |  |
| `void addI32(android.renderscript.Int4)` |  |
| `void addI64(long)` |  |
| `void addI64(android.renderscript.Long2)` |  |
| `void addI64(android.renderscript.Long3)` |  |
| `void addI64(android.renderscript.Long4)` |  |
| `void addI8(byte)` |  |
| `void addI8(android.renderscript.Byte2)` |  |
| `void addI8(android.renderscript.Byte3)` |  |
| `void addI8(android.renderscript.Byte4)` |  |
| `void addMatrix(android.renderscript.Matrix4f)` |  |
| `void addMatrix(android.renderscript.Matrix3f)` |  |
| `void addMatrix(android.renderscript.Matrix2f)` |  |
| `void addObj(android.renderscript.BaseObj)` |  |
| `void addU16(int)` |  |
| `void addU16(android.renderscript.Int2)` |  |
| `void addU16(android.renderscript.Int3)` |  |
| `void addU16(android.renderscript.Int4)` |  |
| `void addU32(long)` |  |
| `void addU32(android.renderscript.Long2)` |  |
| `void addU32(android.renderscript.Long3)` |  |
| `void addU32(android.renderscript.Long4)` |  |
| `void addU64(long)` |  |
| `void addU64(android.renderscript.Long2)` |  |
| `void addU64(android.renderscript.Long3)` |  |
| `void addU64(android.renderscript.Long4)` |  |
| `void addU8(short)` |  |
| `void addU8(android.renderscript.Short2)` |  |
| `void addU8(android.renderscript.Short3)` |  |
| `void addU8(android.renderscript.Short4)` |  |
| `void align(int)` |  |
| `final byte[] getData()` |  |
| `void reset()` |  |
| `void reset(int)` |  |
| `void skip(int)` |  |
| `boolean subBoolean()` |  |
| `android.renderscript.Byte2 subByte2()` |  |
| `android.renderscript.Byte3 subByte3()` |  |
| `android.renderscript.Byte4 subByte4()` |  |
| `android.renderscript.Double2 subDouble2()` |  |
| `android.renderscript.Double3 subDouble3()` |  |
| `android.renderscript.Double4 subDouble4()` |  |
| `float subF32()` |  |
| `double subF64()` |  |
| `android.renderscript.Float2 subFloat2()` |  |
| `android.renderscript.Float3 subFloat3()` |  |
| `android.renderscript.Float4 subFloat4()` |  |
| `short subI16()` |  |
| `int subI32()` |  |
| `long subI64()` |  |
| `byte subI8()` |  |
| `android.renderscript.Int2 subInt2()` |  |
| `android.renderscript.Int3 subInt3()` |  |
| `android.renderscript.Int4 subInt4()` |  |
| `android.renderscript.Long2 subLong2()` |  |
| `android.renderscript.Long3 subLong3()` |  |
| `android.renderscript.Long4 subLong4()` |  |
| `android.renderscript.Matrix2f subMatrix2f()` |  |
| `android.renderscript.Matrix3f subMatrix3f()` |  |
| `android.renderscript.Matrix4f subMatrix4f()` |  |
| `android.renderscript.Short2 subShort2()` |  |
| `android.renderscript.Short3 subShort3()` |  |
| `android.renderscript.Short4 subShort4()` |  |
| `void subalign(int)` |  |

---

### `class Float2`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Float2()` |  |
| `Float2(float, float)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `float x` |  |
| `float y` |  |

---

### `class Float3`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Float3()` |  |
| `Float3(float, float, float)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `float x` |  |
| `float y` |  |
| `float z` |  |

---

### `class Float4`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Float4()` |  |
| `Float4(float, float, float, float)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `float w` |  |
| `float x` |  |
| `float y` |  |
| `float z` |  |

---

### `class Int2`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Int2()` |  |
| `Int2(int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `int x` |  |
| `int y` |  |

---

### `class Int3`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Int3()` |  |
| `Int3(int, int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `int x` |  |
| `int y` |  |
| `int z` |  |

---

### `class Int4`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Int4()` |  |
| `Int4(int, int, int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `int w` |  |
| `int x` |  |
| `int y` |  |
| `int z` |  |

---

### `class Long2`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Long2()` |  |
| `Long2(long, long)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `long x` |  |
| `long y` |  |

---

### `class Long3`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Long3()` |  |
| `Long3(long, long, long)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `long x` |  |
| `long y` |  |
| `long z` |  |

---

### `class Long4`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Long4()` |  |
| `Long4(long, long, long, long)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `long w` |  |
| `long x` |  |
| `long y` |  |
| `long z` |  |

---

### `class Matrix2f`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Matrix2f()` |  |
| `Matrix2f(float[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `float get(int, int)` |  |
| `float[] getArray()` |  |
| `void load(android.renderscript.Matrix2f)` |  |
| `void loadIdentity()` |  |
| `void loadMultiply(android.renderscript.Matrix2f, android.renderscript.Matrix2f)` |  |
| `void loadRotate(float)` |  |
| `void loadScale(float, float)` |  |
| `void multiply(android.renderscript.Matrix2f)` |  |
| `void rotate(float)` |  |
| `void scale(float, float)` |  |
| `void set(int, int, float)` |  |
| `void transpose()` |  |

---

### `class Matrix3f`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Matrix3f()` |  |
| `Matrix3f(float[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `float get(int, int)` |  |
| `float[] getArray()` |  |
| `void load(android.renderscript.Matrix3f)` |  |
| `void loadIdentity()` |  |
| `void loadMultiply(android.renderscript.Matrix3f, android.renderscript.Matrix3f)` |  |
| `void loadRotate(float, float, float, float)` |  |
| `void loadRotate(float)` |  |
| `void loadScale(float, float)` |  |
| `void loadScale(float, float, float)` |  |
| `void loadTranslate(float, float)` |  |
| `void multiply(android.renderscript.Matrix3f)` |  |
| `void rotate(float, float, float, float)` |  |
| `void rotate(float)` |  |
| `void scale(float, float)` |  |
| `void scale(float, float, float)` |  |
| `void set(int, int, float)` |  |
| `void translate(float, float)` |  |
| `void transpose()` |  |

---

### `class Matrix4f`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Matrix4f()` |  |
| `Matrix4f(float[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `float get(int, int)` |  |
| `float[] getArray()` |  |
| `boolean inverse()` |  |
| `boolean inverseTranspose()` |  |
| `void load(android.renderscript.Matrix4f)` |  |
| `void loadFrustum(float, float, float, float, float, float)` |  |
| `void loadIdentity()` |  |
| `void loadMultiply(android.renderscript.Matrix4f, android.renderscript.Matrix4f)` |  |
| `void loadOrtho(float, float, float, float, float, float)` |  |
| `void loadOrthoWindow(int, int)` |  |
| `void loadPerspective(float, float, float, float)` |  |
| `void loadProjectionNormalized(int, int)` |  |
| `void loadRotate(float, float, float, float)` |  |
| `void loadScale(float, float, float)` |  |
| `void loadTranslate(float, float, float)` |  |
| `void multiply(android.renderscript.Matrix4f)` |  |
| `void rotate(float, float, float, float)` |  |
| `void scale(float, float, float)` |  |
| `void set(int, int, float)` |  |
| `void translate(float, float, float)` |  |
| `void transpose()` |  |

---

### `class RSDriverException`

- **继承：** `android.renderscript.RSRuntimeException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RSDriverException(String)` |  |

---

### `class RSIllegalArgumentException`

- **继承：** `android.renderscript.RSRuntimeException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RSIllegalArgumentException(String)` |  |

---

### `class RSInvalidStateException`

- **继承：** `android.renderscript.RSRuntimeException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RSInvalidStateException(String)` |  |

---

### `class RSRuntimeException`

- **继承：** `java.lang.RuntimeException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RSRuntimeException(String)` |  |

---

### `class RenderScript`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CREATE_FLAG_LOW_LATENCY = 2` |  |
| `static final int CREATE_FLAG_LOW_POWER = 4` |  |
| `static final int CREATE_FLAG_NONE = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void contextDump()` |  |
| `static android.renderscript.RenderScript create(android.content.Context)` |  |
| `static android.renderscript.RenderScript create(android.content.Context, android.renderscript.RenderScript.ContextType)` |  |
| `static android.renderscript.RenderScript create(android.content.Context, android.renderscript.RenderScript.ContextType, int)` |  |
| `static android.renderscript.RenderScript createMultiContext(android.content.Context, android.renderscript.RenderScript.ContextType, int, int)` |  |
| `void destroy()` |  |
| `void finish()` |  |
| `final android.content.Context getApplicationContext()` |  |
| `android.renderscript.RenderScript.RSErrorHandler getErrorHandler()` |  |
| `android.renderscript.RenderScript.RSMessageHandler getMessageHandler()` |  |
| `static long getMinorVersion()` |  |
| `static void releaseAllContexts()` |  |
| `void sendMessage(int, int[])` |  |
| `void setErrorHandler(android.renderscript.RenderScript.RSErrorHandler)` |  |
| `void setMessageHandler(android.renderscript.RenderScript.RSMessageHandler)` |  |
| `void setPriority(android.renderscript.RenderScript.Priority)` |  |

---

### `enum RenderScript.ContextType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.renderscript.RenderScript.ContextType DEBUG` |  |
| `static final android.renderscript.RenderScript.ContextType NORMAL` |  |
| `static final android.renderscript.RenderScript.ContextType PROFILE` |  |

---

### `enum RenderScript.Priority`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.renderscript.RenderScript.Priority LOW` |  |
| `static final android.renderscript.RenderScript.Priority NORMAL` |  |

---

### `class static RenderScript.RSErrorHandler`

- **实现：** `java.lang.Runnable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RenderScript.RSErrorHandler()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `String mErrorMessage` |  |
| `int mErrorNum` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void run()` |  |

---

### `class static RenderScript.RSMessageHandler`

- **实现：** `java.lang.Runnable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RenderScript.RSMessageHandler()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `int[] mData` |  |
| `int mID` |  |
| `int mLength` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void run()` |  |

---

### `class Sampler`

- **继承：** `android.renderscript.BaseObj`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.renderscript.Sampler CLAMP_LINEAR(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Sampler CLAMP_LINEAR_MIP_LINEAR(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Sampler CLAMP_NEAREST(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Sampler MIRRORED_REPEAT_LINEAR(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Sampler MIRRORED_REPEAT_LINEAR_MIP_LINEAR(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Sampler MIRRORED_REPEAT_NEAREST(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Sampler WRAP_LINEAR(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Sampler WRAP_LINEAR_MIP_LINEAR(android.renderscript.RenderScript)` |  |
| `static android.renderscript.Sampler WRAP_NEAREST(android.renderscript.RenderScript)` |  |
| `float getAnisotropy()` |  |
| `android.renderscript.Sampler.Value getMagnification()` |  |
| `android.renderscript.Sampler.Value getMinification()` |  |
| `android.renderscript.Sampler.Value getWrapS()` |  |
| `android.renderscript.Sampler.Value getWrapT()` |  |

---

### `class static Sampler.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Sampler.Builder(android.renderscript.RenderScript)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.renderscript.Sampler create()` |  |
| `void setAnisotropy(float)` |  |
| `void setMagnification(android.renderscript.Sampler.Value)` |  |
| `void setMinification(android.renderscript.Sampler.Value)` |  |
| `void setWrapS(android.renderscript.Sampler.Value)` |  |
| `void setWrapT(android.renderscript.Sampler.Value)` |  |

---

### `enum Sampler.Value`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.renderscript.Sampler.Value CLAMP` |  |
| `static final android.renderscript.Sampler.Value LINEAR` |  |
| `static final android.renderscript.Sampler.Value LINEAR_MIP_LINEAR` |  |
| `static final android.renderscript.Sampler.Value LINEAR_MIP_NEAREST` |  |
| `static final android.renderscript.Sampler.Value MIRRORED_REPEAT` |  |
| `static final android.renderscript.Sampler.Value NEAREST` |  |
| `static final android.renderscript.Sampler.Value WRAP` |  |

---

### `class Script`

- **继承：** `android.renderscript.BaseObj`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void bindAllocation(android.renderscript.Allocation, int)` |  |
| `android.renderscript.Script.FieldID createFieldID(int, android.renderscript.Element)` |  |
| `android.renderscript.Script.InvokeID createInvokeID(int)` |  |
| `android.renderscript.Script.KernelID createKernelID(int, int, android.renderscript.Element, android.renderscript.Element)` |  |
| `void forEach(int, android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.FieldPacker)` |  |
| `void forEach(int, android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.FieldPacker, android.renderscript.Script.LaunchOptions)` |  |
| `void forEach(int, android.renderscript.Allocation[], android.renderscript.Allocation, android.renderscript.FieldPacker)` |  |
| `void forEach(int, android.renderscript.Allocation[], android.renderscript.Allocation, android.renderscript.FieldPacker, android.renderscript.Script.LaunchOptions)` |  |
| `boolean getVarB(int)` |  |
| `double getVarD(int)` |  |
| `float getVarF(int)` |  |
| `int getVarI(int)` |  |
| `long getVarJ(int)` |  |
| `void getVarV(int, android.renderscript.FieldPacker)` |  |
| `void invoke(int)` |  |
| `void invoke(int, android.renderscript.FieldPacker)` |  |
| `void reduce(int, android.renderscript.Allocation[], android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void setTimeZone(String)` |  |
| `void setVar(int, float)` |  |
| `void setVar(int, double)` |  |
| `void setVar(int, int)` |  |
| `void setVar(int, long)` |  |
| `void setVar(int, boolean)` |  |
| `void setVar(int, android.renderscript.BaseObj)` |  |
| `void setVar(int, android.renderscript.FieldPacker)` |  |
| `void setVar(int, android.renderscript.FieldPacker, android.renderscript.Element, int[])` |  |

---

### `class static Script.Builder`


---

### `class static Script.FieldBase`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Script.FieldBase()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `android.renderscript.Allocation mAllocation` |  |
| `android.renderscript.Element mElement` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.renderscript.Allocation getAllocation()` |  |
| `android.renderscript.Element getElement()` |  |
| `android.renderscript.Type getType()` |  |
| `void init(android.renderscript.RenderScript, int)` |  |
| `void init(android.renderscript.RenderScript, int, int)` |  |
| `void updateAllocation()` |  |

---

### `class static final Script.FieldID`

- **继承：** `android.renderscript.BaseObj`

---

### `class static final Script.InvokeID`

- **继承：** `android.renderscript.BaseObj`

---

### `class static final Script.KernelID`

- **继承：** `android.renderscript.BaseObj`

---

### `class static final Script.LaunchOptions`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Script.LaunchOptions()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getXEnd()` |  |
| `int getXStart()` |  |
| `int getYEnd()` |  |
| `int getYStart()` |  |
| `int getZEnd()` |  |
| `int getZStart()` |  |
| `android.renderscript.Script.LaunchOptions setX(int, int)` |  |
| `android.renderscript.Script.LaunchOptions setY(int, int)` |  |
| `android.renderscript.Script.LaunchOptions setZ(int, int)` |  |

---

### `class ScriptC`

- **继承：** `android.renderscript.Script`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ScriptC(int, android.renderscript.RenderScript)` |  |
| `ScriptC(long, android.renderscript.RenderScript)` |  |
| `ScriptC(android.renderscript.RenderScript, android.content.res.Resources, int)` |  |
| `ScriptC(android.renderscript.RenderScript, String, byte[], byte[])` |  |

---

### `class final ScriptGroup`

- **继承：** `android.renderscript.BaseObj`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `Object[] execute(java.lang.Object...)` |  |

---

### `class static final ScriptGroup.Binding`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ScriptGroup.Binding(android.renderscript.Script.FieldID, Object)` |  |

---

### `class static final ScriptGroup.Builder` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static final ScriptGroup.Builder2`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ScriptGroup.Builder2(android.renderscript.RenderScript)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.renderscript.ScriptGroup.Input addInput()` |  |
| `android.renderscript.ScriptGroup.Closure addInvoke(android.renderscript.Script.InvokeID, java.lang.Object...)` |  |
| `android.renderscript.ScriptGroup.Closure addKernel(android.renderscript.Script.KernelID, android.renderscript.Type, java.lang.Object...)` |  |
| `android.renderscript.ScriptGroup create(String, android.renderscript.ScriptGroup.Future...)` |  |

---

### `class static final ScriptGroup.Closure`

- **继承：** `android.renderscript.BaseObj`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.renderscript.ScriptGroup.Future getGlobal(android.renderscript.Script.FieldID)` |  |
| `android.renderscript.ScriptGroup.Future getReturn()` |  |

---

### `class static final ScriptGroup.Future`


---

### `class static final ScriptGroup.Input`


---

### `class abstract ScriptIntrinsic`

- **继承：** `android.renderscript.Script`

---

### `class final ScriptIntrinsic3DLUT`

- **继承：** `android.renderscript.ScriptIntrinsic`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.renderscript.ScriptIntrinsic3DLUT create(android.renderscript.RenderScript, android.renderscript.Element)` |  |
| `void forEach(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEach(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `android.renderscript.Script.KernelID getKernelID()` |  |
| `void setLUT(android.renderscript.Allocation)` |  |

---

### `class final ScriptIntrinsicBLAS`

- **继承：** `android.renderscript.ScriptIntrinsic`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CONJ_TRANSPOSE = 113` |  |
| `static final int LEFT = 141` |  |
| `static final int LOWER = 122` |  |
| `static final int NON_UNIT = 131` |  |
| `static final int NO_TRANSPOSE = 111` |  |
| `static final int RIGHT = 142` |  |
| `static final int TRANSPOSE = 112` |  |
| `static final int UNIT = 132` |  |
| `static final int UPPER = 121` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void BNNM(android.renderscript.Allocation, int, android.renderscript.Allocation, int, android.renderscript.Allocation, int, int)` |  |
| `void CGBMV(int, int, int, android.renderscript.Float2, android.renderscript.Allocation, android.renderscript.Allocation, int, android.renderscript.Float2, android.renderscript.Allocation, int)` |  |
| `void CGEMM(int, int, android.renderscript.Float2, android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Float2, android.renderscript.Allocation)` |  |
| `void CGEMV(int, android.renderscript.Float2, android.renderscript.Allocation, android.renderscript.Allocation, int, android.renderscript.Float2, android.renderscript.Allocation, int)` |  |
| `void CGERC(android.renderscript.Float2, android.renderscript.Allocation, int, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void CGERU(android.renderscript.Float2, android.renderscript.Allocation, int, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void CHBMV(int, int, android.renderscript.Float2, android.renderscript.Allocation, android.renderscript.Allocation, int, android.renderscript.Float2, android.renderscript.Allocation, int)` |  |
| `void CHEMM(int, int, android.renderscript.Float2, android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Float2, android.renderscript.Allocation)` |  |
| `void CHEMV(int, android.renderscript.Float2, android.renderscript.Allocation, android.renderscript.Allocation, int, android.renderscript.Float2, android.renderscript.Allocation, int)` |  |
| `void CHER(int, float, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void CHER2(int, android.renderscript.Float2, android.renderscript.Allocation, int, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void CHER2K(int, int, android.renderscript.Float2, android.renderscript.Allocation, android.renderscript.Allocation, float, android.renderscript.Allocation)` |  |
| `void CHERK(int, int, float, android.renderscript.Allocation, float, android.renderscript.Allocation)` |  |
| `void CHPMV(int, android.renderscript.Float2, android.renderscript.Allocation, android.renderscript.Allocation, int, android.renderscript.Float2, android.renderscript.Allocation, int)` |  |
| `void CHPR(int, float, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void CHPR2(int, android.renderscript.Float2, android.renderscript.Allocation, int, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void CSYMM(int, int, android.renderscript.Float2, android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Float2, android.renderscript.Allocation)` |  |
| `void CSYR2K(int, int, android.renderscript.Float2, android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Float2, android.renderscript.Allocation)` |  |
| `void CSYRK(int, int, android.renderscript.Float2, android.renderscript.Allocation, android.renderscript.Float2, android.renderscript.Allocation)` |  |
| `void CTBMV(int, int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void CTBSV(int, int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void CTPMV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void CTPSV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void CTRMM(int, int, int, int, android.renderscript.Float2, android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void CTRMV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void CTRSM(int, int, int, int, android.renderscript.Float2, android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void CTRSV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void DGBMV(int, int, int, double, android.renderscript.Allocation, android.renderscript.Allocation, int, double, android.renderscript.Allocation, int)` |  |
| `void DGEMM(int, int, double, android.renderscript.Allocation, android.renderscript.Allocation, double, android.renderscript.Allocation)` |  |
| `void DGEMV(int, double, android.renderscript.Allocation, android.renderscript.Allocation, int, double, android.renderscript.Allocation, int)` |  |
| `void DGER(double, android.renderscript.Allocation, int, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void DSBMV(int, int, double, android.renderscript.Allocation, android.renderscript.Allocation, int, double, android.renderscript.Allocation, int)` |  |
| `void DSPMV(int, double, android.renderscript.Allocation, android.renderscript.Allocation, int, double, android.renderscript.Allocation, int)` |  |
| `void DSPR(int, double, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void DSPR2(int, double, android.renderscript.Allocation, int, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void DSYMM(int, int, double, android.renderscript.Allocation, android.renderscript.Allocation, double, android.renderscript.Allocation)` |  |
| `void DSYMV(int, double, android.renderscript.Allocation, android.renderscript.Allocation, int, double, android.renderscript.Allocation, int)` |  |
| `void DSYR(int, double, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void DSYR2(int, double, android.renderscript.Allocation, int, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void DSYR2K(int, int, double, android.renderscript.Allocation, android.renderscript.Allocation, double, android.renderscript.Allocation)` |  |
| `void DSYRK(int, int, double, android.renderscript.Allocation, double, android.renderscript.Allocation)` |  |
| `void DTBMV(int, int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void DTBSV(int, int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void DTPMV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void DTPSV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void DTRMM(int, int, int, int, double, android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void DTRMV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void DTRSM(int, int, int, int, double, android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void DTRSV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void SGBMV(int, int, int, float, android.renderscript.Allocation, android.renderscript.Allocation, int, float, android.renderscript.Allocation, int)` |  |
| `void SGEMM(int, int, float, android.renderscript.Allocation, android.renderscript.Allocation, float, android.renderscript.Allocation)` |  |
| `void SGEMV(int, float, android.renderscript.Allocation, android.renderscript.Allocation, int, float, android.renderscript.Allocation, int)` |  |
| `void SGER(float, android.renderscript.Allocation, int, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void SSBMV(int, int, float, android.renderscript.Allocation, android.renderscript.Allocation, int, float, android.renderscript.Allocation, int)` |  |
| `void SSPMV(int, float, android.renderscript.Allocation, android.renderscript.Allocation, int, float, android.renderscript.Allocation, int)` |  |
| `void SSPR(int, float, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void SSPR2(int, float, android.renderscript.Allocation, int, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void SSYMM(int, int, float, android.renderscript.Allocation, android.renderscript.Allocation, float, android.renderscript.Allocation)` |  |
| `void SSYMV(int, float, android.renderscript.Allocation, android.renderscript.Allocation, int, float, android.renderscript.Allocation, int)` |  |
| `void SSYR(int, float, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void SSYR2(int, float, android.renderscript.Allocation, int, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void SSYR2K(int, int, float, android.renderscript.Allocation, android.renderscript.Allocation, float, android.renderscript.Allocation)` |  |
| `void SSYRK(int, int, float, android.renderscript.Allocation, float, android.renderscript.Allocation)` |  |
| `void STBMV(int, int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void STBSV(int, int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void STPMV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void STPSV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void STRMM(int, int, int, int, float, android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void STRMV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void STRSM(int, int, int, int, float, android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void STRSV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void ZGBMV(int, int, int, android.renderscript.Double2, android.renderscript.Allocation, android.renderscript.Allocation, int, android.renderscript.Double2, android.renderscript.Allocation, int)` |  |
| `void ZGEMM(int, int, android.renderscript.Double2, android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Double2, android.renderscript.Allocation)` |  |
| `void ZGEMV(int, android.renderscript.Double2, android.renderscript.Allocation, android.renderscript.Allocation, int, android.renderscript.Double2, android.renderscript.Allocation, int)` |  |
| `void ZGERC(android.renderscript.Double2, android.renderscript.Allocation, int, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void ZGERU(android.renderscript.Double2, android.renderscript.Allocation, int, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void ZHBMV(int, int, android.renderscript.Double2, android.renderscript.Allocation, android.renderscript.Allocation, int, android.renderscript.Double2, android.renderscript.Allocation, int)` |  |
| `void ZHEMM(int, int, android.renderscript.Double2, android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Double2, android.renderscript.Allocation)` |  |
| `void ZHEMV(int, android.renderscript.Double2, android.renderscript.Allocation, android.renderscript.Allocation, int, android.renderscript.Double2, android.renderscript.Allocation, int)` |  |
| `void ZHER(int, double, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void ZHER2(int, android.renderscript.Double2, android.renderscript.Allocation, int, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void ZHER2K(int, int, android.renderscript.Double2, android.renderscript.Allocation, android.renderscript.Allocation, double, android.renderscript.Allocation)` |  |
| `void ZHERK(int, int, double, android.renderscript.Allocation, double, android.renderscript.Allocation)` |  |
| `void ZHPMV(int, android.renderscript.Double2, android.renderscript.Allocation, android.renderscript.Allocation, int, android.renderscript.Double2, android.renderscript.Allocation, int)` |  |
| `void ZHPR(int, double, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void ZHPR2(int, android.renderscript.Double2, android.renderscript.Allocation, int, android.renderscript.Allocation, int, android.renderscript.Allocation)` |  |
| `void ZSYMM(int, int, android.renderscript.Double2, android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Double2, android.renderscript.Allocation)` |  |
| `void ZSYR2K(int, int, android.renderscript.Double2, android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Double2, android.renderscript.Allocation)` |  |
| `void ZSYRK(int, int, android.renderscript.Double2, android.renderscript.Allocation, android.renderscript.Double2, android.renderscript.Allocation)` |  |
| `void ZTBMV(int, int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void ZTBSV(int, int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void ZTPMV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void ZTPSV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void ZTRMM(int, int, int, int, android.renderscript.Double2, android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void ZTRMV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `void ZTRSM(int, int, int, int, android.renderscript.Double2, android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void ZTRSV(int, int, int, android.renderscript.Allocation, android.renderscript.Allocation, int)` |  |
| `static android.renderscript.ScriptIntrinsicBLAS create(android.renderscript.RenderScript)` |  |

---

### `class ScriptIntrinsicBlend`

- **继承：** `android.renderscript.ScriptIntrinsic`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.renderscript.ScriptIntrinsicBlend create(android.renderscript.RenderScript, android.renderscript.Element)` |  |
| `void forEachAdd(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEachAdd(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void forEachClear(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEachClear(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void forEachDst(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEachDst(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void forEachDstAtop(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEachDstAtop(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void forEachDstIn(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEachDstIn(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void forEachDstOut(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEachDstOut(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void forEachDstOver(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEachDstOver(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void forEachMultiply(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEachMultiply(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void forEachSrc(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEachSrc(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void forEachSrcAtop(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEachSrcAtop(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void forEachSrcIn(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEachSrcIn(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void forEachSrcOut(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEachSrcOut(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void forEachSrcOver(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEachSrcOver(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void forEachSubtract(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEachSubtract(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void forEachXor(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEachXor(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `android.renderscript.Script.KernelID getKernelIDAdd()` |  |
| `android.renderscript.Script.KernelID getKernelIDClear()` |  |
| `android.renderscript.Script.KernelID getKernelIDDst()` |  |
| `android.renderscript.Script.KernelID getKernelIDDstAtop()` |  |
| `android.renderscript.Script.KernelID getKernelIDDstIn()` |  |
| `android.renderscript.Script.KernelID getKernelIDDstOut()` |  |
| `android.renderscript.Script.KernelID getKernelIDDstOver()` |  |
| `android.renderscript.Script.KernelID getKernelIDMultiply()` |  |
| `android.renderscript.Script.KernelID getKernelIDSrc()` |  |
| `android.renderscript.Script.KernelID getKernelIDSrcAtop()` |  |
| `android.renderscript.Script.KernelID getKernelIDSrcIn()` |  |
| `android.renderscript.Script.KernelID getKernelIDSrcOut()` |  |
| `android.renderscript.Script.KernelID getKernelIDSrcOver()` |  |
| `android.renderscript.Script.KernelID getKernelIDSubtract()` |  |
| `android.renderscript.Script.KernelID getKernelIDXor()` |  |

---

### `class final ScriptIntrinsicBlur`

- **继承：** `android.renderscript.ScriptIntrinsic`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.renderscript.ScriptIntrinsicBlur create(android.renderscript.RenderScript, android.renderscript.Element)` |  |
| `void forEach(android.renderscript.Allocation)` |  |
| `void forEach(android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `android.renderscript.Script.FieldID getFieldID_Input()` |  |
| `android.renderscript.Script.KernelID getKernelID()` |  |
| `void setInput(android.renderscript.Allocation)` |  |
| `void setRadius(float)` |  |

---

### `class final ScriptIntrinsicColorMatrix`

- **继承：** `android.renderscript.ScriptIntrinsic`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.renderscript.ScriptIntrinsicColorMatrix create(android.renderscript.RenderScript)` |  |
| `void forEach(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEach(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `android.renderscript.Script.KernelID getKernelID()` |  |
| `void setAdd(android.renderscript.Float4)` |  |
| `void setAdd(float, float, float, float)` |  |
| `void setColorMatrix(android.renderscript.Matrix4f)` |  |
| `void setColorMatrix(android.renderscript.Matrix3f)` |  |
| `void setGreyscale()` |  |
| `void setRGBtoYUV()` |  |
| `void setYUVtoRGB()` |  |

---

### `class final ScriptIntrinsicConvolve3x3`

- **继承：** `android.renderscript.ScriptIntrinsic`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.renderscript.ScriptIntrinsicConvolve3x3 create(android.renderscript.RenderScript, android.renderscript.Element)` |  |
| `void forEach(android.renderscript.Allocation)` |  |
| `void forEach(android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `android.renderscript.Script.FieldID getFieldID_Input()` |  |
| `android.renderscript.Script.KernelID getKernelID()` |  |
| `void setCoefficients(float[])` |  |
| `void setInput(android.renderscript.Allocation)` |  |

---

### `class final ScriptIntrinsicConvolve5x5`

- **继承：** `android.renderscript.ScriptIntrinsic`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.renderscript.ScriptIntrinsicConvolve5x5 create(android.renderscript.RenderScript, android.renderscript.Element)` |  |
| `void forEach(android.renderscript.Allocation)` |  |
| `void forEach(android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `android.renderscript.Script.FieldID getFieldID_Input()` |  |
| `android.renderscript.Script.KernelID getKernelID()` |  |
| `void setCoefficients(float[])` |  |
| `void setInput(android.renderscript.Allocation)` |  |

---

### `class final ScriptIntrinsicHistogram`

- **继承：** `android.renderscript.ScriptIntrinsic`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.renderscript.ScriptIntrinsicHistogram create(android.renderscript.RenderScript, android.renderscript.Element)` |  |
| `void forEach(android.renderscript.Allocation)` |  |
| `void forEach(android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `void forEach_Dot(android.renderscript.Allocation)` |  |
| `void forEach_Dot(android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `android.renderscript.Script.FieldID getFieldID_Input()` |  |
| `android.renderscript.Script.KernelID getKernelID_Separate()` |  |
| `void setDotCoefficients(float, float, float, float)` |  |
| `void setOutput(android.renderscript.Allocation)` |  |

---

### `class final ScriptIntrinsicLUT`

- **继承：** `android.renderscript.ScriptIntrinsic`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.renderscript.ScriptIntrinsicLUT create(android.renderscript.RenderScript, android.renderscript.Element)` |  |
| `void forEach(android.renderscript.Allocation, android.renderscript.Allocation)` |  |
| `void forEach(android.renderscript.Allocation, android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `android.renderscript.Script.KernelID getKernelID()` |  |
| `void setAlpha(int, int)` |  |
| `void setBlue(int, int)` |  |
| `void setGreen(int, int)` |  |
| `void setRed(int, int)` |  |

---

### `class final ScriptIntrinsicResize`

- **继承：** `android.renderscript.ScriptIntrinsic`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.renderscript.ScriptIntrinsicResize create(android.renderscript.RenderScript)` |  |
| `void forEach_bicubic(android.renderscript.Allocation)` |  |
| `void forEach_bicubic(android.renderscript.Allocation, android.renderscript.Script.LaunchOptions)` |  |
| `android.renderscript.Script.FieldID getFieldID_Input()` |  |
| `android.renderscript.Script.KernelID getKernelID_bicubic()` |  |
| `void setInput(android.renderscript.Allocation)` |  |

---

### `class final ScriptIntrinsicYuvToRGB`

- **继承：** `android.renderscript.ScriptIntrinsic`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.renderscript.ScriptIntrinsicYuvToRGB create(android.renderscript.RenderScript, android.renderscript.Element)` |  |
| `void forEach(android.renderscript.Allocation)` |  |
| `android.renderscript.Script.FieldID getFieldID_Input()` |  |
| `android.renderscript.Script.KernelID getKernelID()` |  |
| `void setInput(android.renderscript.Allocation)` |  |

---

### `class Short2`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Short2()` |  |
| `Short2(short, short)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `short x` |  |
| `short y` |  |

---

### `class Short3`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Short3()` |  |
| `Short3(short, short, short)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `short x` |  |
| `short y` |  |
| `short z` |  |

---

### `class Short4`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Short4()` |  |
| `Short4(short, short, short, short)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `short w` |  |
| `short x` |  |
| `short y` |  |
| `short z` |  |

---

### `class Type`

- **继承：** `android.renderscript.BaseObj`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.renderscript.Type createX(android.renderscript.RenderScript, android.renderscript.Element, int)` |  |
| `static android.renderscript.Type createXY(android.renderscript.RenderScript, android.renderscript.Element, int, int)` |  |
| `static android.renderscript.Type createXYZ(android.renderscript.RenderScript, android.renderscript.Element, int, int, int)` |  |
| `int getCount()` |  |
| `android.renderscript.Element getElement()` |  |
| `int getX()` |  |
| `int getY()` |  |
| `int getYuv()` |  |
| `int getZ()` |  |
| `boolean hasFaces()` |  |
| `boolean hasMipmaps()` |  |

---

### `class static Type.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Type.Builder(android.renderscript.RenderScript, android.renderscript.Element)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.renderscript.Type create()` |  |
| `android.renderscript.Type.Builder setFaces(boolean)` |  |
| `android.renderscript.Type.Builder setMipmaps(boolean)` |  |
| `android.renderscript.Type.Builder setX(int)` |  |
| `android.renderscript.Type.Builder setY(int)` |  |
| `android.renderscript.Type.Builder setYuvFormat(int)` |  |
| `android.renderscript.Type.Builder setZ(int)` |  |

---

### `enum Type.CubemapFace`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.renderscript.Type.CubemapFace NEGATIVE_X` |  |
| `static final android.renderscript.Type.CubemapFace NEGATIVE_Y` |  |
| `static final android.renderscript.Type.CubemapFace NEGATIVE_Z` |  |
| `static final android.renderscript.Type.CubemapFace POSITIVE_X` |  |
| `static final android.renderscript.Type.CubemapFace POSITIVE_Y` |  |
| `static final android.renderscript.Type.CubemapFace POSITIVE_Z` |  |

---

