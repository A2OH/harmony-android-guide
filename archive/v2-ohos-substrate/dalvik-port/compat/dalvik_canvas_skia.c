/*
 * libdalvik_canvas.so — OH_Drawing (Skia) wrapper for Dalvik Canvas JNI
 *
 * Replaces stb_truetype software renderer with real Skia via OH_Drawing API.
 * Loaded by dalvikvm via System.loadLibrary("dalvik_canvas").
 *
 * All OH_Drawing symbols resolved at runtime via dlopen("lib2d_graphics.z.so").
 */

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dlfcn.h>
#include <stdint.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <sys/stat.h>
#include <sys/mman.h>
#include <math.h>

/* ── Forward declarations for OH_Drawing types ── */
typedef struct OH_Drawing_Canvas OH_Drawing_Canvas;
typedef struct OH_Drawing_Bitmap OH_Drawing_Bitmap;
typedef struct OH_Drawing_Pen OH_Drawing_Pen;
typedef struct OH_Drawing_Brush OH_Drawing_Brush;
typedef struct OH_Drawing_Path OH_Drawing_Path;
typedef struct OH_Drawing_Font OH_Drawing_Font;
typedef struct OH_Drawing_Typeface OH_Drawing_Typeface;
typedef struct OH_Drawing_TextBlob OH_Drawing_TextBlob;
typedef struct OH_Drawing_TextBlobBuilder OH_Drawing_TextBlobBuilder;
typedef struct OH_Drawing_Rect OH_Drawing_Rect;
typedef struct OH_Drawing_RoundRect OH_Drawing_RoundRect;
typedef struct OH_Drawing_Point OH_Drawing_Point;
typedef struct OH_Drawing_Matrix OH_Drawing_Matrix;

typedef enum {
    COLOR_FORMAT_UNKNOWN,
    COLOR_FORMAT_ALPHA_8,
    COLOR_FORMAT_RGB_565,
    COLOR_FORMAT_ARGB_4444,
    COLOR_FORMAT_RGBA_8888,
    COLOR_FORMAT_BGRA_8888
} OH_Drawing_ColorFormat;

typedef enum {
    ALPHA_FORMAT_UNKNOWN,
    ALPHA_FORMAT_OPAQUE,
    ALPHA_FORMAT_PREMUL,
    ALPHA_FORMAT_UNPREMUL
} OH_Drawing_AlphaFormat;

typedef struct {
    OH_Drawing_ColorFormat colorFormat;
    OH_Drawing_AlphaFormat alphaFormat;
} OH_Drawing_BitmapFormat;

typedef enum { CLIP_DIFFERENCE, CLIP_INTERSECT } OH_Drawing_CanvasClipOp;

typedef struct {
    uint16_t* glyphs;
    float* pos;
    char* utf8text;
    uint32_t* clusters;
} OH_Drawing_RunBuffer;

/* Font metrics struct (from Skia) */
typedef struct {
    float flags;
    float top;
    float ascent;
    float descent;
    float bottom;
    float leading;
    float avgCharWidth;
    float maxCharWidth;
    float xMin;
    float xMax;
    float xHeight;
    float capHeight;
    float underlineThickness;
    float underlinePosition;
    float strikeoutThickness;
    float strikeoutPosition;
} OH_Drawing_Font_Metrics;

/* ── Function pointer typedefs ── */
#define FPTR(ret, name, ...) typedef ret (*pfn_##name)(__VA_ARGS__)

/* Bitmap */
FPTR(OH_Drawing_Bitmap*, BitmapCreate, void);
FPTR(void, BitmapDestroy, OH_Drawing_Bitmap*);
FPTR(void, BitmapBuild, OH_Drawing_Bitmap*, uint32_t, uint32_t, const OH_Drawing_BitmapFormat*);
FPTR(uint32_t, BitmapGetWidth, OH_Drawing_Bitmap*);
FPTR(uint32_t, BitmapGetHeight, OH_Drawing_Bitmap*);
FPTR(void*, BitmapGetPixels, OH_Drawing_Bitmap*);

/* Canvas */
FPTR(OH_Drawing_Canvas*, CanvasCreate, void);
FPTR(void, CanvasDestroy, OH_Drawing_Canvas*);
FPTR(void, CanvasBind, OH_Drawing_Canvas*, OH_Drawing_Bitmap*);
FPTR(void, CanvasAttachPen, OH_Drawing_Canvas*, const OH_Drawing_Pen*);
FPTR(void, CanvasDetachPen, OH_Drawing_Canvas*);
FPTR(void, CanvasAttachBrush, OH_Drawing_Canvas*, const OH_Drawing_Brush*);
FPTR(void, CanvasDetachBrush, OH_Drawing_Canvas*);
FPTR(void, CanvasSave, OH_Drawing_Canvas*);
FPTR(void, CanvasRestore, OH_Drawing_Canvas*);
FPTR(uint32_t, CanvasGetSaveCount, OH_Drawing_Canvas*);
FPTR(void, CanvasRestoreToCount, OH_Drawing_Canvas*, uint32_t);
FPTR(void, CanvasDrawLine, OH_Drawing_Canvas*, float, float, float, float);
FPTR(void, CanvasDrawPath, OH_Drawing_Canvas*, const OH_Drawing_Path*);
FPTR(void, CanvasDrawBitmap, OH_Drawing_Canvas*, const OH_Drawing_Bitmap*, float, float);
FPTR(void, CanvasDrawRect, OH_Drawing_Canvas*, const OH_Drawing_Rect*);
FPTR(void, CanvasDrawCircle, OH_Drawing_Canvas*, const OH_Drawing_Point*, float);
FPTR(void, CanvasDrawOval, OH_Drawing_Canvas*, const OH_Drawing_Rect*);
FPTR(void, CanvasDrawArc, OH_Drawing_Canvas*, const OH_Drawing_Rect*, float, float);
FPTR(void, CanvasDrawRoundRect, OH_Drawing_Canvas*, const OH_Drawing_RoundRect*);
FPTR(void, CanvasDrawTextBlob, OH_Drawing_Canvas*, const OH_Drawing_TextBlob*, float, float);
FPTR(void, CanvasClipRect, OH_Drawing_Canvas*, const OH_Drawing_Rect*, OH_Drawing_CanvasClipOp, int);
FPTR(void, CanvasClipPath, OH_Drawing_Canvas*, const OH_Drawing_Path*, OH_Drawing_CanvasClipOp, int);
FPTR(void, CanvasRotate, OH_Drawing_Canvas*, float, float, float);
FPTR(void, CanvasTranslate, OH_Drawing_Canvas*, float, float);
FPTR(void, CanvasScale, OH_Drawing_Canvas*, float, float);
FPTR(void, CanvasClear, OH_Drawing_Canvas*, uint32_t);
FPTR(void, CanvasConcatMatrix, OH_Drawing_Canvas*, const OH_Drawing_Matrix*);

/* Pen */
FPTR(OH_Drawing_Pen*, PenCreate, void);
FPTR(void, PenDestroy, OH_Drawing_Pen*);
FPTR(void, PenSetColor, OH_Drawing_Pen*, uint32_t);
FPTR(void, PenSetWidth, OH_Drawing_Pen*, float);
FPTR(void, PenSetAntiAlias, OH_Drawing_Pen*, int);
FPTR(void, PenSetAlpha, OH_Drawing_Pen*, uint8_t);
FPTR(void, PenSetCap, OH_Drawing_Pen*, int);
FPTR(void, PenSetJoin, OH_Drawing_Pen*, int);

/* Brush */
FPTR(OH_Drawing_Brush*, BrushCreate, void);
FPTR(void, BrushDestroy, OH_Drawing_Brush*);
FPTR(void, BrushSetColor, OH_Drawing_Brush*, uint32_t);
FPTR(void, BrushSetAntiAlias, OH_Drawing_Brush*, int);
FPTR(void, BrushSetAlpha, OH_Drawing_Brush*, uint8_t);

/* Font */
FPTR(OH_Drawing_Font*, FontCreate, void);
FPTR(void, FontDestroy, OH_Drawing_Font*);
FPTR(void, FontSetTypeface, OH_Drawing_Font*, OH_Drawing_Typeface*);
FPTR(void, FontSetTextSize, OH_Drawing_Font*, float);
FPTR(float, FontGetMetrics, OH_Drawing_Font*, OH_Drawing_Font_Metrics*);

/* Typeface */
FPTR(OH_Drawing_Typeface*, TypefaceCreateDefault, void);
FPTR(OH_Drawing_Typeface*, TypefaceCreateFromFile, const char*, int);
FPTR(void, TypefaceDestroy, OH_Drawing_Typeface*);

/* TextBlob */
FPTR(OH_Drawing_TextBlob*, TextBlobCreateFromText, const void*, size_t, const OH_Drawing_Font*, int);
FPTR(OH_Drawing_TextBlob*, TextBlobCreateFromString, const char*, const OH_Drawing_Font*, int);
FPTR(void, TextBlobDestroy, OH_Drawing_TextBlob*);
FPTR(OH_Drawing_TextBlobBuilder*, TextBlobBuilderCreate, void);
FPTR(void, TextBlobBuilderDestroy, OH_Drawing_TextBlobBuilder*);
FPTR(OH_Drawing_TextBlob*, TextBlobBuilderMake, OH_Drawing_TextBlobBuilder*);

/* Path */
FPTR(OH_Drawing_Path*, PathCreate, void);
FPTR(void, PathDestroy, OH_Drawing_Path*);
FPTR(void, PathMoveTo, OH_Drawing_Path*, float, float);
FPTR(void, PathLineTo, OH_Drawing_Path*, float, float);
FPTR(void, PathArcTo, OH_Drawing_Path*, float, float, float, float, float, float);
FPTR(void, PathQuadTo, OH_Drawing_Path*, float, float, float, float);
FPTR(void, PathCubicTo, OH_Drawing_Path*, float, float, float, float, float, float);
FPTR(void, PathClose, OH_Drawing_Path*);
FPTR(void, PathReset, OH_Drawing_Path*);

/* Rect, RoundRect, Point, Matrix */
FPTR(OH_Drawing_Rect*, RectCreate, float, float, float, float);
FPTR(void, RectDestroy, OH_Drawing_Rect*);
FPTR(OH_Drawing_RoundRect*, RoundRectCreate, const OH_Drawing_Rect*, float, float);
FPTR(void, RoundRectDestroy, OH_Drawing_RoundRect*);
FPTR(OH_Drawing_Matrix*, MatrixCreate, void);
FPTR(void, MatrixDestroy, OH_Drawing_Matrix*);

/* Color */
FPTR(uint32_t, ColorSetArgb, uint32_t, uint32_t, uint32_t, uint32_t);

/* ── Global function pointers ── */
static void *g_lib = NULL;
static int g_initialized = 0;

#define FP(name) static pfn_##name fp_##name = NULL

FP(BitmapCreate); FP(BitmapDestroy); FP(BitmapBuild); FP(BitmapGetWidth); FP(BitmapGetHeight); FP(BitmapGetPixels);
FP(CanvasCreate); FP(CanvasDestroy); FP(CanvasBind);
FP(CanvasAttachPen); FP(CanvasDetachPen); FP(CanvasAttachBrush); FP(CanvasDetachBrush);
FP(CanvasSave); FP(CanvasRestore); FP(CanvasGetSaveCount); FP(CanvasRestoreToCount);
FP(CanvasDrawLine); FP(CanvasDrawPath); FP(CanvasDrawBitmap); FP(CanvasDrawRect);
FP(CanvasDrawCircle); FP(CanvasDrawOval); FP(CanvasDrawArc); FP(CanvasDrawRoundRect);
FP(CanvasDrawTextBlob); FP(CanvasClipRect); FP(CanvasClipPath);
FP(CanvasRotate); FP(CanvasTranslate); FP(CanvasScale); FP(CanvasClear); FP(CanvasConcatMatrix);
FP(PenCreate); FP(PenDestroy); FP(PenSetColor); FP(PenSetWidth); FP(PenSetAntiAlias); FP(PenSetAlpha);
FP(PenSetCap); FP(PenSetJoin);
FP(BrushCreate); FP(BrushDestroy); FP(BrushSetColor); FP(BrushSetAntiAlias); FP(BrushSetAlpha);
FP(FontCreate); FP(FontDestroy); FP(FontSetTypeface); FP(FontSetTextSize); FP(FontGetMetrics);
FP(TypefaceCreateDefault); FP(TypefaceCreateFromFile); FP(TypefaceDestroy);
FP(TextBlobCreateFromText); FP(TextBlobCreateFromString); FP(TextBlobDestroy);
FP(TextBlobBuilderCreate); FP(TextBlobBuilderDestroy); FP(TextBlobBuilderMake);
FP(PathCreate); FP(PathDestroy); FP(PathMoveTo); FP(PathLineTo); FP(PathArcTo);
FP(PathQuadTo); FP(PathCubicTo); FP(PathClose); FP(PathReset);
FP(RectCreate); FP(RectDestroy); FP(RoundRectCreate); FP(RoundRectDestroy);
FP(MatrixCreate); FP(MatrixDestroy); FP(ColorSetArgb);

/* ── Resolve all symbols ── */
#define RESOLVE(name) do { \
    fp_##name = (pfn_##name)dlsym(g_lib, "OH_Drawing_" #name); \
    if (!fp_##name) fprintf(stderr, "dalvik_canvas: missing OH_Drawing_%s\n", #name); \
} while(0)

static int init_oh_drawing() {
    if (g_initialized) return g_lib != NULL;
    g_initialized = 1;

    const char *paths[] = {
        "lib2d_graphics.z.so",
        "/system/lib/platformsdk/lib2d_graphics.z.so",
        "/system/lib/lib2d_graphics.z.so",
        NULL
    };
    for (int i = 0; paths[i]; i++) {
        g_lib = dlopen(paths[i], RTLD_NOW);
        if (g_lib) { fprintf(stderr, "dalvik_canvas: loaded %s\n", paths[i]); break; }
    }
    if (!g_lib) {
        fprintf(stderr, "dalvik_canvas: FAILED to load lib2d_graphics.z.so: %s\n", dlerror());
        return 0;
    }

    RESOLVE(BitmapCreate); RESOLVE(BitmapDestroy); RESOLVE(BitmapBuild);
    RESOLVE(BitmapGetWidth); RESOLVE(BitmapGetHeight); RESOLVE(BitmapGetPixels);
    RESOLVE(CanvasCreate); RESOLVE(CanvasDestroy); RESOLVE(CanvasBind);
    RESOLVE(CanvasAttachPen); RESOLVE(CanvasDetachPen);
    RESOLVE(CanvasAttachBrush); RESOLVE(CanvasDetachBrush);
    RESOLVE(CanvasSave); RESOLVE(CanvasRestore);
    RESOLVE(CanvasGetSaveCount); RESOLVE(CanvasRestoreToCount);
    RESOLVE(CanvasDrawLine); RESOLVE(CanvasDrawPath); RESOLVE(CanvasDrawBitmap);
    RESOLVE(CanvasDrawRect); RESOLVE(CanvasDrawCircle); RESOLVE(CanvasDrawOval);
    RESOLVE(CanvasDrawArc); RESOLVE(CanvasDrawRoundRect);
    RESOLVE(CanvasDrawTextBlob); RESOLVE(CanvasClipRect); RESOLVE(CanvasClipPath);
    RESOLVE(CanvasRotate); RESOLVE(CanvasTranslate); RESOLVE(CanvasScale);
    RESOLVE(CanvasClear); RESOLVE(CanvasConcatMatrix);
    RESOLVE(PenCreate); RESOLVE(PenDestroy); RESOLVE(PenSetColor); RESOLVE(PenSetWidth);
    RESOLVE(PenSetAntiAlias); RESOLVE(PenSetAlpha); RESOLVE(PenSetCap); RESOLVE(PenSetJoin);
    RESOLVE(BrushCreate); RESOLVE(BrushDestroy); RESOLVE(BrushSetColor);
    RESOLVE(BrushSetAntiAlias); RESOLVE(BrushSetAlpha);
    RESOLVE(FontCreate); RESOLVE(FontDestroy); RESOLVE(FontSetTypeface);
    RESOLVE(FontSetTextSize); RESOLVE(FontGetMetrics);
    RESOLVE(TypefaceCreateDefault); RESOLVE(TypefaceCreateFromFile); RESOLVE(TypefaceDestroy);
    RESOLVE(TextBlobCreateFromText); RESOLVE(TextBlobCreateFromString); RESOLVE(TextBlobDestroy);
    RESOLVE(TextBlobBuilderCreate); RESOLVE(TextBlobBuilderDestroy); RESOLVE(TextBlobBuilderMake);
    RESOLVE(PathCreate); RESOLVE(PathDestroy); RESOLVE(PathMoveTo); RESOLVE(PathLineTo);
    RESOLVE(PathArcTo); RESOLVE(PathQuadTo); RESOLVE(PathCubicTo);
    RESOLVE(PathClose); RESOLVE(PathReset);
    RESOLVE(RectCreate); RESOLVE(RectDestroy);
    RESOLVE(RoundRectCreate); RESOLVE(RoundRectDestroy);
    RESOLVE(MatrixCreate); RESOLVE(MatrixDestroy);
    RESOLVE(ColorSetArgb);

    return 1;
}

/* ── Skia Canvas wrapper struct ── */
typedef struct {
    OH_Drawing_Canvas *canvas;
    OH_Drawing_Bitmap *bitmap;
    OH_Drawing_Pen    *pen;
    OH_Drawing_Brush  *brush;
    OH_Drawing_Font   *font;
    OH_Drawing_Typeface *typeface;
    int width, height;
} SkiaCanvas;

/* ── Global typeface (loaded once) ── */
static OH_Drawing_Typeface *g_typeface = NULL;
static void ensure_typeface() {
    if (g_typeface) return;
    if (fp_TypefaceCreateFromFile)
        g_typeface = fp_TypefaceCreateFromFile("/data/a2oh/font.ttf", 0);
    if (!g_typeface && fp_TypefaceCreateDefault)
        g_typeface = fp_TypefaceCreateDefault();
}

/* ── JNI: Bitmap ── */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapCreate(JNIEnv* e, jclass, jint w, jint h) {
    if (!init_oh_drawing() || !fp_BitmapCreate || !fp_BitmapBuild) return 0;
    OH_Drawing_Bitmap *bmp = fp_BitmapCreate();
    OH_Drawing_BitmapFormat fmt = { COLOR_FORMAT_RGBA_8888, ALPHA_FORMAT_PREMUL };
    fp_BitmapBuild(bmp, w, h, &fmt);
    return (jlong)(uintptr_t)bmp;
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapDestroy(JNIEnv*, jclass, jlong h) {
    if (fp_BitmapDestroy && h) fp_BitmapDestroy((OH_Drawing_Bitmap*)(uintptr_t)h);
}

JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapGetWidth(JNIEnv*, jclass, jlong h) {
    if (!fp_BitmapGetWidth || !h) return 0;
    return fp_BitmapGetWidth((OH_Drawing_Bitmap*)(uintptr_t)h);
}

JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapGetHeight(JNIEnv*, jclass, jlong h) {
    if (!fp_BitmapGetHeight || !h) return 0;
    return fp_BitmapGetHeight((OH_Drawing_Bitmap*)(uintptr_t)h);
}

JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapGetPixel(JNIEnv*, jclass, jlong h, jint x, jint y) {
    if (!fp_BitmapGetPixels || !fp_BitmapGetWidth || !h) return 0;
    OH_Drawing_Bitmap *bmp = (OH_Drawing_Bitmap*)(uintptr_t)h;
    uint32_t w = fp_BitmapGetWidth(bmp);
    uint32_t *pixels = (uint32_t*)fp_BitmapGetPixels(bmp);
    if (!pixels || x < 0 || y < 0 || (uint32_t)x >= w) return 0;
    uint32_t rgba = pixels[y * w + x];
    /* RGBA_8888 → ARGB_8888 for Android */
    uint8_t r = rgba & 0xFF, g = (rgba >> 8) & 0xFF, b = (rgba >> 16) & 0xFF, a = (rgba >> 24) & 0xFF;
    return (a << 24) | (r << 16) | (g << 8) | b;
}

/* ── JNI: Canvas ── */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasCreate(JNIEnv*, jclass, jlong bmpH, jint w, jint h) {
    if (!init_oh_drawing() || !fp_CanvasCreate) return 0;
    SkiaCanvas *sc = (SkiaCanvas*)calloc(1, sizeof(SkiaCanvas));
    sc->canvas = fp_CanvasCreate();
    sc->bitmap = (OH_Drawing_Bitmap*)(uintptr_t)bmpH;
    sc->width = w;
    sc->height = h;
    if (fp_CanvasBind) fp_CanvasBind(sc->canvas, sc->bitmap);
    if (fp_PenCreate) sc->pen = fp_PenCreate();
    if (fp_BrushCreate) sc->brush = fp_BrushCreate();
    ensure_typeface();
    if (fp_FontCreate) {
        sc->font = fp_FontCreate();
        if (g_typeface && fp_FontSetTypeface) fp_FontSetTypeface(sc->font, g_typeface);
    }
    return (jlong)(uintptr_t)sc;
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDestroy(JNIEnv*, jclass, jlong h) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    if (!sc) return;
    if (sc->font && fp_FontDestroy) fp_FontDestroy(sc->font);
    if (sc->pen && fp_PenDestroy) fp_PenDestroy(sc->pen);
    if (sc->brush && fp_BrushDestroy) fp_BrushDestroy(sc->brush);
    if (sc->canvas && fp_CanvasDestroy) fp_CanvasDestroy(sc->canvas);
    /* Don't destroy bitmap — owned by Java Bitmap object */
    free(sc);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawColor(JNIEnv*, jclass, jlong h, jint color) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    if (!sc || !fp_CanvasClear) return;
    fp_CanvasClear(sc->canvas, (uint32_t)color);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawRect(JNIEnv*, jclass, jlong h, jfloat l, jfloat t, jfloat r, jfloat b, jint color, jint style, jfloat strokeW) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    if (!sc || !fp_RectCreate) return;
    OH_Drawing_Rect *rect = fp_RectCreate(l, t, r, b);
    if (style == 0 || style == 2) { /* FILL or FILL_AND_STROKE */
        if (fp_BrushSetColor) fp_BrushSetColor(sc->brush, (uint32_t)color);
        if (fp_BrushSetAntiAlias) fp_BrushSetAntiAlias(sc->brush, 1);
        fp_CanvasAttachBrush(sc->canvas, sc->brush);
        fp_CanvasDrawRect(sc->canvas, rect);
        fp_CanvasDetachBrush(sc->canvas);
    }
    if (style == 1 || style == 2) { /* STROKE or FILL_AND_STROKE */
        if (fp_PenSetColor) fp_PenSetColor(sc->pen, (uint32_t)color);
        if (fp_PenSetWidth) fp_PenSetWidth(sc->pen, strokeW);
        if (fp_PenSetAntiAlias) fp_PenSetAntiAlias(sc->pen, 1);
        fp_CanvasAttachPen(sc->canvas, sc->pen);
        fp_CanvasDrawRect(sc->canvas, rect);
        fp_CanvasDetachPen(sc->canvas);
    }
    fp_RectDestroy(rect);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawRoundRect(JNIEnv*, jclass, jlong h, jfloat l, jfloat t, jfloat r, jfloat b, jfloat rx, jfloat ry, jint color, jint style, jfloat strokeW) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    if (!sc || !fp_RectCreate || !fp_RoundRectCreate) return;
    OH_Drawing_Rect *rect = fp_RectCreate(l, t, r, b);
    OH_Drawing_RoundRect *rr = fp_RoundRectCreate(rect, rx, ry);
    if (style == 0 || style == 2) {
        fp_BrushSetColor(sc->brush, (uint32_t)color);
        fp_CanvasAttachBrush(sc->canvas, sc->brush);
        fp_CanvasDrawRoundRect(sc->canvas, rr);
        fp_CanvasDetachBrush(sc->canvas);
    }
    if (style == 1 || style == 2) {
        fp_PenSetColor(sc->pen, (uint32_t)color);
        fp_PenSetWidth(sc->pen, strokeW);
        fp_CanvasAttachPen(sc->canvas, sc->pen);
        fp_CanvasDrawRoundRect(sc->canvas, rr);
        fp_CanvasDetachPen(sc->canvas);
    }
    fp_RoundRectDestroy(rr);
    fp_RectDestroy(rect);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawCircle(JNIEnv*, jclass, jlong h, jfloat cx, jfloat cy, jfloat radius, jint color, jint style, jfloat strokeW) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    if (!sc || !fp_CanvasDrawCircle) return;
    /* OH_Drawing_CanvasDrawCircle takes OH_Drawing_Point* — allocate on stack */
    /* The Point struct is just {float x, float y} internally */
    float point[2] = {cx, cy};
    if (style == 0 || style == 2) {
        fp_BrushSetColor(sc->brush, (uint32_t)color);
        fp_CanvasAttachBrush(sc->canvas, sc->brush);
        fp_CanvasDrawCircle(sc->canvas, (const OH_Drawing_Point*)point, radius);
        fp_CanvasDetachBrush(sc->canvas);
    }
    if (style == 1 || style == 2) {
        fp_PenSetColor(sc->pen, (uint32_t)color);
        fp_PenSetWidth(sc->pen, strokeW);
        fp_CanvasAttachPen(sc->canvas, sc->pen);
        fp_CanvasDrawCircle(sc->canvas, (const OH_Drawing_Point*)point, radius);
        fp_CanvasDetachPen(sc->canvas);
    }
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawLine(JNIEnv*, jclass, jlong h, jfloat x1, jfloat y1, jfloat x2, jfloat y2, jint color, jfloat strokeW) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    if (!sc || !fp_CanvasDrawLine) return;
    fp_PenSetColor(sc->pen, (uint32_t)color);
    fp_PenSetWidth(sc->pen, strokeW);
    fp_PenSetAntiAlias(sc->pen, 1);
    fp_CanvasAttachPen(sc->canvas, sc->pen);
    fp_CanvasDrawLine(sc->canvas, x1, y1, x2, y2);
    fp_CanvasDetachPen(sc->canvas);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawText(JNIEnv* env, jclass, jlong h, jstring jtext, jfloat x, jfloat y, jfloat size, jint color, jint style) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    if (!sc || !fp_TextBlobCreateFromString || !fp_CanvasDrawTextBlob) return;
    const char *text = (*env)->GetStringUTFChars(env, jtext, NULL);
    if (!text) return;

    if (fp_FontSetTextSize) fp_FontSetTextSize(sc->font, size);

    /* Create TextBlob from string */
    OH_Drawing_TextBlob *blob = fp_TextBlobCreateFromString(text, sc->font, 0);
    (*env)->ReleaseStringUTFChars(env, jtext, text);

    if (!blob) return;

    /* Set color via brush for fill */
    fp_BrushSetColor(sc->brush, (uint32_t)color);
    fp_BrushSetAntiAlias(sc->brush, 1);
    fp_CanvasAttachBrush(sc->canvas, sc->brush);
    fp_CanvasDrawTextBlob(sc->canvas, blob, x, y);
    fp_CanvasDetachBrush(sc->canvas);

    fp_TextBlobDestroy(blob);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawPath(JNIEnv*, jclass, jlong h, jlong pathH, jint color, jint style, jfloat strokeW) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    OH_Drawing_Path *path = (OH_Drawing_Path*)(uintptr_t)pathH;
    if (!sc || !path || !fp_CanvasDrawPath) return;
    if (style == 0 || style == 2) {
        fp_BrushSetColor(sc->brush, (uint32_t)color);
        fp_CanvasAttachBrush(sc->canvas, sc->brush);
        fp_CanvasDrawPath(sc->canvas, path);
        fp_CanvasDetachBrush(sc->canvas);
    }
    if (style == 1 || style == 2) {
        fp_PenSetColor(sc->pen, (uint32_t)color);
        fp_PenSetWidth(sc->pen, strokeW);
        fp_CanvasAttachPen(sc->canvas, sc->pen);
        fp_CanvasDrawPath(sc->canvas, path);
        fp_CanvasDetachPen(sc->canvas);
    }
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawOval(JNIEnv*, jclass, jlong h, jfloat l, jfloat t, jfloat r, jfloat b, jint color, jint style, jfloat strokeW) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    if (!sc || !fp_CanvasDrawOval) return;
    OH_Drawing_Rect *rect = fp_RectCreate(l, t, r, b);
    if (style == 0 || style == 2) {
        fp_BrushSetColor(sc->brush, (uint32_t)color);
        fp_CanvasAttachBrush(sc->canvas, sc->brush);
        fp_CanvasDrawOval(sc->canvas, rect);
        fp_CanvasDetachBrush(sc->canvas);
    }
    if (style == 1 || style == 2) {
        fp_PenSetColor(sc->pen, (uint32_t)color);
        fp_PenSetWidth(sc->pen, strokeW);
        fp_CanvasAttachPen(sc->canvas, sc->pen);
        fp_CanvasDrawOval(sc->canvas, rect);
        fp_CanvasDetachPen(sc->canvas);
    }
    fp_RectDestroy(rect);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawArc(JNIEnv*, jclass, jlong h, jfloat l, jfloat t, jfloat r, jfloat b, jfloat startAngle, jfloat sweepAngle, jint color, jint style, jfloat strokeW) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    if (!sc || !fp_CanvasDrawArc) return;
    OH_Drawing_Rect *rect = fp_RectCreate(l, t, r, b);
    if (style == 1 || style == 2) {
        fp_PenSetColor(sc->pen, (uint32_t)color);
        fp_PenSetWidth(sc->pen, strokeW);
        fp_CanvasAttachPen(sc->canvas, sc->pen);
        fp_CanvasDrawArc(sc->canvas, rect, startAngle, sweepAngle);
        fp_CanvasDetachPen(sc->canvas);
    }
    fp_RectDestroy(rect);
}

/* ── Canvas state ── */

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasSave(JNIEnv*, jclass, jlong h) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    if (sc && fp_CanvasSave) fp_CanvasSave(sc->canvas);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasRestore(JNIEnv*, jclass, jlong h) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    if (sc && fp_CanvasRestore) fp_CanvasRestore(sc->canvas);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasTranslate(JNIEnv*, jclass, jlong h, jfloat dx, jfloat dy) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    if (sc && fp_CanvasTranslate) fp_CanvasTranslate(sc->canvas, dx, dy);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasScale(JNIEnv*, jclass, jlong h, jfloat sx, jfloat sy) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    if (sc && fp_CanvasScale) fp_CanvasScale(sc->canvas, sx, sy);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasRotate(JNIEnv*, jclass, jlong h, jfloat deg, jfloat px, jfloat py) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    if (sc && fp_CanvasRotate) fp_CanvasRotate(sc->canvas, deg, px, py);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasClipRect(JNIEnv*, jclass, jlong h, jfloat l, jfloat t, jfloat r, jfloat b) {
    SkiaCanvas *sc = (SkiaCanvas*)(uintptr_t)h;
    if (!sc || !fp_CanvasClipRect || !fp_RectCreate) return;
    OH_Drawing_Rect *rect = fp_RectCreate(l, t, r, b);
    fp_CanvasClipRect(sc->canvas, rect, CLIP_INTERSECT, 1);
    fp_RectDestroy(rect);
}

/* ── Path ── */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_pathCreate(JNIEnv*, jclass) {
    if (!init_oh_drawing() || !fp_PathCreate) return 0;
    return (jlong)(uintptr_t)fp_PathCreate();
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathDestroy(JNIEnv*, jclass, jlong h) {
    if (fp_PathDestroy && h) fp_PathDestroy((OH_Drawing_Path*)(uintptr_t)h);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathMoveTo(JNIEnv*, jclass, jlong h, jfloat x, jfloat y) {
    if (fp_PathMoveTo && h) fp_PathMoveTo((OH_Drawing_Path*)(uintptr_t)h, x, y);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathLineTo(JNIEnv*, jclass, jlong h, jfloat x, jfloat y) {
    if (fp_PathLineTo && h) fp_PathLineTo((OH_Drawing_Path*)(uintptr_t)h, x, y);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathCubicTo(JNIEnv*, jclass, jlong h, jfloat x1, jfloat y1, jfloat x2, jfloat y2, jfloat x3, jfloat y3) {
    if (fp_PathCubicTo && h) fp_PathCubicTo((OH_Drawing_Path*)(uintptr_t)h, x1, y1, x2, y2, x3, y3);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathQuadTo(JNIEnv*, jclass, jlong h, jfloat x1, jfloat y1, jfloat x2, jfloat y2) {
    if (fp_PathQuadTo && h) fp_PathQuadTo((OH_Drawing_Path*)(uintptr_t)h, x1, y1, x2, y2);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathClose(JNIEnv*, jclass, jlong h) {
    if (fp_PathClose && h) fp_PathClose((OH_Drawing_Path*)(uintptr_t)h);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathReset(JNIEnv*, jclass, jlong h) {
    if (fp_PathReset && h) fp_PathReset((OH_Drawing_Path*)(uintptr_t)h);
}

/* ── Font ── */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_fontCreate(JNIEnv*, jclass) {
    if (!init_oh_drawing() || !fp_FontCreate) return 0;
    OH_Drawing_Font *f = fp_FontCreate();
    ensure_typeface();
    if (g_typeface && fp_FontSetTypeface) fp_FontSetTypeface(f, g_typeface);
    return (jlong)(uintptr_t)f;
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_fontDestroy(JNIEnv*, jclass, jlong h) {
    if (fp_FontDestroy && h) fp_FontDestroy((OH_Drawing_Font*)(uintptr_t)h);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_fontSetSize(JNIEnv*, jclass, jlong h, jfloat sz) {
    if (fp_FontSetTextSize && h) fp_FontSetTextSize((OH_Drawing_Font*)(uintptr_t)h, sz);
}

JNIEXPORT jfloat JNICALL Java_com_ohos_shim_bridge_OHBridge_fontMeasureText(JNIEnv* env, jclass, jlong h, jstring jtext) {
    if (!fp_TextBlobCreateFromString || !h) return 0;
    const char *text = (*env)->GetStringUTFChars(env, jtext, NULL);
    if (!text) return 0;
    /* Create blob to measure bounds */
    OH_Drawing_TextBlob *blob = fp_TextBlobCreateFromString(text, (OH_Drawing_Font*)(uintptr_t)h, 0);
    (*env)->ReleaseStringUTFChars(env, jtext, text);
    if (!blob) return 0;
    /* TODO: get bounds from blob — for now, estimate from font size */
    fp_TextBlobDestroy(blob);
    /* Rough estimate: 0.6 * fontSize * strlen */
    return 0; /* Let Java fallback handle it */
}

JNIEXPORT jfloatArray JNICALL Java_com_ohos_shim_bridge_OHBridge_fontGetMetrics(JNIEnv* env, jclass, jlong h) {
    jfloatArray result = (*env)->NewFloatArray(env, 3);
    if (!result) return NULL;
    float metrics[3] = {0, 0, 0};
    if (fp_FontGetMetrics && h) {
        OH_Drawing_Font_Metrics fm;
        memset(&fm, 0, sizeof(fm));
        fp_FontGetMetrics((OH_Drawing_Font*)(uintptr_t)h, &fm);
        metrics[0] = fm.ascent;  /* already negative in Skia */
        metrics[1] = fm.descent;
        metrics[2] = fm.leading;
    }
    (*env)->SetFloatArrayRegion(env, result, 0, 3, metrics);
    return result;
}

/* ── JNI Registration ── */
#define OHBM(name, sig, fn) {(char*)"Java_com_ohos_shim_bridge_OHBridge_" name, (char*)sig, (void*)fn}

/* Note: This .so provides the SAME JNI method names as arkui_bridge.cpp.
 * When loaded via System.loadLibrary AFTER dalvikvm starts, these symbols
 * override the statically-linked stubs in the dalvikvm binary.
 * Dalvik resolves native methods by name at first call time. */
