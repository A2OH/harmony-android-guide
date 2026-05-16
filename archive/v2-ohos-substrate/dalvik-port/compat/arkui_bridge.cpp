/*
 * Software Canvas bridge for Dalvik — replaces OH_Drawing stubs with a real
 * pixel-pushing implementation using stb_truetype for text.
 *
 * All Canvas/Pen/Brush/Path/Bitmap/Font JNI methods produce real pixels in
 * an in-memory ARGB8888 buffer (SWBitmap). The ViewDumper init binary reads
 * the bitmap data from a shared file to blit to /dev/fb0.
 *
 * Also outputs draw commands to /data/a2oh/canvas_ops.txt for debugging.
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

#define STB_TRUETYPE_IMPLEMENTATION
#define STBTT_ifloor(x) ((int)(x))
#define STBTT_iceil(x)  ((int)((x)+0.999999))
#include "stb_truetype.h"
#include "software_canvas.h"

static const char *FONT_PATH = "/data/a2oh/font.ttf";

/* ── OH_Drawing (Skia) dynamic loading ── */
/* All OH_Drawing functions loaded via dlopen at runtime.
 * If available, canvas operations use real Skia. Otherwise falls back to stb_truetype. */

typedef void OH_Canvas;
typedef void OH_Bitmap;
typedef void OH_Pen;
typedef void OH_Brush;
typedef void OH_Path;
typedef void OH_Font;
typedef void OH_Typeface;
typedef void OH_TextBlob;
typedef void OH_Rect;
typedef void OH_RoundRect;
typedef struct { int colorFormat; int alphaFormat; } OH_BitmapFormat;
typedef struct { float flags,top,ascent,descent,bottom,leading,avgCharWidth,maxCharWidth,
    xMin,xMax,xHeight,capHeight,ulThick,ulPos,stThick,stPos; } OH_FontMetrics;

static void *g_skia_lib = NULL;
static int g_skia_tried = 0;
static int g_skia_ok = 0;

/* Function pointers */
#define SKFP(ret, name, ...) static ret (*sk_##name)(__VA_ARGS__) = NULL
SKFP(OH_Bitmap*, BitmapCreate, void);
SKFP(void, BitmapDestroy, OH_Bitmap*);
SKFP(void, BitmapBuild, OH_Bitmap*, uint32_t, uint32_t, const OH_BitmapFormat*);
SKFP(uint32_t, BitmapGetWidth, OH_Bitmap*);
SKFP(uint32_t, BitmapGetHeight, OH_Bitmap*);
SKFP(void*, BitmapGetPixels, OH_Bitmap*);
SKFP(OH_Canvas*, CanvasCreate, void);
SKFP(void, CanvasDestroy, OH_Canvas*);
SKFP(void, CanvasBind, OH_Canvas*, OH_Bitmap*);
SKFP(void, CanvasAttachPen, OH_Canvas*, const OH_Pen*);
SKFP(void, CanvasDetachPen, OH_Canvas*);
SKFP(void, CanvasAttachBrush, OH_Canvas*, const OH_Brush*);
SKFP(void, CanvasDetachBrush, OH_Canvas*);
SKFP(void, CanvasSave, OH_Canvas*);
SKFP(void, CanvasRestore, OH_Canvas*);
SKFP(void, CanvasDrawLine, OH_Canvas*, float, float, float, float);
SKFP(void, CanvasDrawRect, OH_Canvas*, const OH_Rect*);
SKFP(void, CanvasDrawCircle, OH_Canvas*, const void*, float);
SKFP(void, CanvasDrawOval, OH_Canvas*, const OH_Rect*);
SKFP(void, CanvasDrawArc, OH_Canvas*, const OH_Rect*, float, float);
SKFP(void, CanvasDrawRoundRect, OH_Canvas*, const OH_RoundRect*);
SKFP(void, CanvasDrawPath, OH_Canvas*, const OH_Path*);
SKFP(void, CanvasDrawTextBlob, OH_Canvas*, const OH_TextBlob*, float, float);
SKFP(void, CanvasDrawBitmap, OH_Canvas*, const OH_Bitmap*, float, float);
SKFP(void, CanvasClipRect, OH_Canvas*, const OH_Rect*, int, int);
SKFP(void, CanvasTranslate, OH_Canvas*, float, float);
SKFP(void, CanvasScale, OH_Canvas*, float, float);
SKFP(void, CanvasRotate, OH_Canvas*, float, float, float);
SKFP(void, CanvasClear, OH_Canvas*, uint32_t);
SKFP(OH_Pen*, PenCreate, void);
SKFP(void, PenDestroy, OH_Pen*);
SKFP(void, PenSetColor, OH_Pen*, uint32_t);
SKFP(void, PenSetWidth, OH_Pen*, float);
SKFP(void, PenSetAntiAlias, OH_Pen*, int);
SKFP(OH_Brush*, BrushCreate, void);
SKFP(void, BrushDestroy, OH_Brush*);
SKFP(void, BrushSetColor, OH_Brush*, uint32_t);
SKFP(void, BrushSetAntiAlias, OH_Brush*, int);
SKFP(OH_Font*, FontCreate, void);
SKFP(void, FontDestroy, OH_Font*);
SKFP(void, FontSetTypeface, OH_Font*, OH_Typeface*);
SKFP(void, FontSetTextSize, OH_Font*, float);
SKFP(float, FontGetMetrics, OH_Font*, OH_FontMetrics*);
SKFP(OH_Typeface*, TypefaceCreateDefault, void);
SKFP(OH_Typeface*, TypefaceCreateFromFile, const char*, int);
SKFP(void, TypefaceDestroy, OH_Typeface*);
SKFP(OH_TextBlob*, TextBlobCreateFromString, const char*, const OH_Font*, int);
SKFP(void, TextBlobDestroy, OH_TextBlob*);
SKFP(OH_Path*, PathCreate, void);
SKFP(void, PathDestroy, OH_Path*);
SKFP(void, PathMoveTo, OH_Path*, float, float);
SKFP(void, PathLineTo, OH_Path*, float, float);
SKFP(void, PathCubicTo, OH_Path*, float, float, float, float, float, float);
SKFP(void, PathQuadTo, OH_Path*, float, float, float, float);
SKFP(void, PathClose, OH_Path*);
SKFP(void, PathReset, OH_Path*);
SKFP(OH_Rect*, RectCreate, float, float, float, float);
SKFP(void, RectDestroy, OH_Rect*);
SKFP(OH_RoundRect*, RoundRectCreate, const OH_Rect*, float, float);
SKFP(void, RoundRectDestroy, OH_RoundRect*);

#define SK_RESOLVE(name) sk_##name = (decltype(sk_##name))dlsym(g_skia_lib, "OH_Drawing_" #name)

/* When linked with -DHAVE_SKIA_STATIC, call Skia C++ API directly (bypass OH_Drawing C wrapper) */
#ifdef HAVE_SKIA_STATIC
#include "include/core/SkCanvas.h"
#include "include/core/SkBitmap.h"
#include "include/core/SkPaint.h"
#include "include/core/SkPath.h"
#include "include/core/SkFont.h"
#include "include/core/SkTextBlob.h"
#include "include/core/SkTypeface.h"
#include "include/core/SkFontMgr.h"
#include "include/core/SkRect.h"
#include "include/core/SkRRect.h"
#include "include/core/SkString.h"
#include "include/core/SkData.h"
/* Skia direct canvas wrapper */
struct SkiaDirectCanvas {
    SkBitmap skBitmap;
    SkCanvas *skCanvas;
    SkPaint fillPaint;
    SkPaint strokePaint;
    SkFont skFont;
    sk_sp<SkTypeface> typeface;
    int width, height;
};
static sk_sp<SkTypeface> g_skTypeface;
static void ensure_sk_typeface() {
    if (g_skTypeface) return;
    auto data = SkData::MakeFromFileName(FONT_PATH);
    if (data) g_skTypeface = SkTypeface::MakeFromData(data);
    if (!g_skTypeface) g_skTypeface = SkTypeface::MakeDefault();
}
#endif

#ifdef HAVE_SKIA_STATIC_OH_DRAWING_UNUSED
/* Original OH_Drawing extern declarations — kept for reference but not used */
extern "C" {
OH_Bitmap* OH_Drawing_BitmapCreate(void);
void OH_Drawing_BitmapDestroy(OH_Bitmap*);
void OH_Drawing_BitmapBuild(OH_Bitmap*, uint32_t, uint32_t, const OH_BitmapFormat*);
uint32_t OH_Drawing_BitmapGetWidth(OH_Bitmap*);
uint32_t OH_Drawing_BitmapGetHeight(OH_Bitmap*);
void* OH_Drawing_BitmapGetPixels(OH_Bitmap*);
OH_Canvas* OH_Drawing_CanvasCreate(void);
void OH_Drawing_CanvasDestroy(OH_Canvas*);
void OH_Drawing_CanvasBind(OH_Canvas*, OH_Bitmap*);
void OH_Drawing_CanvasAttachPen(OH_Canvas*, const OH_Pen*);
void OH_Drawing_CanvasDetachPen(OH_Canvas*);
void OH_Drawing_CanvasAttachBrush(OH_Canvas*, const OH_Brush*);
void OH_Drawing_CanvasDetachBrush(OH_Canvas*);
void OH_Drawing_CanvasSave(OH_Canvas*);
void OH_Drawing_CanvasRestore(OH_Canvas*);
void OH_Drawing_CanvasDrawLine(OH_Canvas*, float, float, float, float);
void OH_Drawing_CanvasDrawRect(OH_Canvas*, const OH_Rect*);
void OH_Drawing_CanvasDrawCircle(OH_Canvas*, const void*, float);
void OH_Drawing_CanvasDrawOval(OH_Canvas*, const OH_Rect*);
void OH_Drawing_CanvasDrawArc(OH_Canvas*, const OH_Rect*, float, float);
void OH_Drawing_CanvasDrawRoundRect(OH_Canvas*, const OH_RoundRect*);
void OH_Drawing_CanvasDrawPath(OH_Canvas*, const OH_Path*);
void OH_Drawing_CanvasDrawTextBlob(OH_Canvas*, const OH_TextBlob*, float, float);
void OH_Drawing_CanvasDrawBitmap(OH_Canvas*, const OH_Bitmap*, float, float);
void OH_Drawing_CanvasClipRect(OH_Canvas*, const OH_Rect*, int, int);
void OH_Drawing_CanvasTranslate(OH_Canvas*, float, float);
void OH_Drawing_CanvasScale(OH_Canvas*, float, float);
void OH_Drawing_CanvasRotate(OH_Canvas*, float, float, float);
void OH_Drawing_CanvasClear(OH_Canvas*, uint32_t);
OH_Pen* OH_Drawing_PenCreate(void);
void OH_Drawing_PenDestroy(OH_Pen*);
void OH_Drawing_PenSetColor(OH_Pen*, uint32_t);
void OH_Drawing_PenSetWidth(OH_Pen*, float);
void OH_Drawing_PenSetAntiAlias(OH_Pen*, int);
OH_Brush* OH_Drawing_BrushCreate(void);
void OH_Drawing_BrushDestroy(OH_Brush*);
void OH_Drawing_BrushSetColor(OH_Brush*, uint32_t);
void OH_Drawing_BrushSetAntiAlias(OH_Brush*, int);
OH_Font* OH_Drawing_FontCreate(void);
void OH_Drawing_FontDestroy(OH_Font*);
void OH_Drawing_FontSetTypeface(OH_Font*, OH_Typeface*);
void OH_Drawing_FontSetTextSize(OH_Font*, float);
float OH_Drawing_FontGetMetrics(OH_Font*, OH_FontMetrics*);
OH_Typeface* OH_Drawing_TypefaceCreateDefault(void);
OH_Typeface* OH_Drawing_TypefaceCreateFromFile(const char*, int);
void OH_Drawing_TypefaceDestroy(OH_Typeface*);
OH_TextBlob* OH_Drawing_TextBlobCreateFromString(const char*, const OH_Font*, int);
void OH_Drawing_TextBlobDestroy(OH_TextBlob*);
OH_Path* OH_Drawing_PathCreate(void);
void OH_Drawing_PathDestroy(OH_Path*);
void OH_Drawing_PathMoveTo(OH_Path*, float, float);
void OH_Drawing_PathLineTo(OH_Path*, float, float);
void OH_Drawing_PathCubicTo(OH_Path*, float, float, float, float, float, float);
void OH_Drawing_PathQuadTo(OH_Path*, float, float, float, float);
void OH_Drawing_PathClose(OH_Path*);
void OH_Drawing_PathReset(OH_Path*);
OH_Rect* OH_Drawing_RectCreate(float, float, float, float);
void OH_Drawing_RectDestroy(OH_Rect*);
OH_RoundRect* OH_Drawing_RoundRectCreate(const OH_Rect*, float, float);
void OH_Drawing_RoundRectDestroy(OH_RoundRect*);
}
#define SK_STATIC_ASSIGN(name) sk_##name = OH_Drawing_##name
#endif

static void try_load_skia() {
    if (g_skia_tried) return;
    g_skia_tried = 1;

#ifdef HAVE_SKIA_STATIC
    /* Skia C++ API linked directly — use SkiaDirectCanvas instead of OH_Drawing */
    g_skia_ok = 1;
    ensure_sk_typeface();
    fprintf(stderr, "Skia: STATIC LINK (direct SkCanvas API)\n");
    return;
#endif

    /* Dynamic loading fallback */
    const char *paths[] = {
        "lib2d_graphics.z.so",
        "/system/lib/platformsdk/lib2d_graphics.z.so",
        "/system/lib/lib2d_graphics.z.so",
        NULL
    };
    for (int i = 0; paths[i]; i++) {
        g_skia_lib = dlopen(paths[i], RTLD_NOW);
        if (g_skia_lib) {
            fprintf(stderr, "OH_Drawing: loaded %s\n", paths[i]);
            break;
        }
    }
    if (!g_skia_lib) {
        fprintf(stderr, "OH_Drawing: not available, using stb_truetype fallback\n");
        return;
    }
    SK_RESOLVE(BitmapCreate); SK_RESOLVE(BitmapDestroy); SK_RESOLVE(BitmapBuild);
    SK_RESOLVE(BitmapGetWidth); SK_RESOLVE(BitmapGetHeight); SK_RESOLVE(BitmapGetPixels);
    SK_RESOLVE(CanvasCreate); SK_RESOLVE(CanvasDestroy); SK_RESOLVE(CanvasBind);
    SK_RESOLVE(CanvasAttachPen); SK_RESOLVE(CanvasDetachPen);
    SK_RESOLVE(CanvasAttachBrush); SK_RESOLVE(CanvasDetachBrush);
    SK_RESOLVE(CanvasSave); SK_RESOLVE(CanvasRestore);
    SK_RESOLVE(CanvasDrawLine); SK_RESOLVE(CanvasDrawRect); SK_RESOLVE(CanvasDrawCircle);
    SK_RESOLVE(CanvasDrawOval); SK_RESOLVE(CanvasDrawArc); SK_RESOLVE(CanvasDrawRoundRect);
    SK_RESOLVE(CanvasDrawPath); SK_RESOLVE(CanvasDrawTextBlob); SK_RESOLVE(CanvasDrawBitmap);
    SK_RESOLVE(CanvasClipRect); SK_RESOLVE(CanvasTranslate); SK_RESOLVE(CanvasScale);
    SK_RESOLVE(CanvasRotate); SK_RESOLVE(CanvasClear);
    SK_RESOLVE(PenCreate); SK_RESOLVE(PenDestroy); SK_RESOLVE(PenSetColor);
    SK_RESOLVE(PenSetWidth); SK_RESOLVE(PenSetAntiAlias);
    SK_RESOLVE(BrushCreate); SK_RESOLVE(BrushDestroy); SK_RESOLVE(BrushSetColor);
    SK_RESOLVE(BrushSetAntiAlias);
    SK_RESOLVE(FontCreate); SK_RESOLVE(FontDestroy); SK_RESOLVE(FontSetTypeface);
    SK_RESOLVE(FontSetTextSize); SK_RESOLVE(FontGetMetrics);
    SK_RESOLVE(TypefaceCreateDefault); SK_RESOLVE(TypefaceCreateFromFile); SK_RESOLVE(TypefaceDestroy);
    SK_RESOLVE(TextBlobCreateFromString); SK_RESOLVE(TextBlobDestroy);
    SK_RESOLVE(PathCreate); SK_RESOLVE(PathDestroy); SK_RESOLVE(PathMoveTo); SK_RESOLVE(PathLineTo);
    SK_RESOLVE(PathCubicTo); SK_RESOLVE(PathQuadTo); SK_RESOLVE(PathClose); SK_RESOLVE(PathReset);
    SK_RESOLVE(RectCreate); SK_RESOLVE(RectDestroy);
    SK_RESOLVE(RoundRectCreate); SK_RESOLVE(RoundRectDestroy);
    /* Minimum check: need at least canvas + bitmap + text */
    g_skia_ok = (sk_CanvasCreate && sk_BitmapCreate && sk_BitmapBuild &&
                 sk_CanvasBind && sk_CanvasDrawTextBlob && sk_FontCreate);
    fprintf(stderr, "OH_Drawing: %s\n", g_skia_ok ? "READY (Skia backend)" : "INCOMPLETE (fallback to stb)");
}

/* Skia canvas wrapper — holds both OH_Drawing objects and SWCanvas fallback */
typedef struct {
    /* Skia objects (non-NULL if g_skia_ok) */
    OH_Canvas *skCanvas;
    OH_Bitmap *skBitmap;
    OH_Pen *skPen;
    OH_Brush *skBrush;
    OH_Font *skFont;
    OH_Typeface *skTypeface;
    int skW, skH;
    /* Software fallback (always available) */
    SWCanvas *sw;
    SWBitmap *swBitmap; /* owned by the Bitmap JNI handle, not us */
} HybridCanvas;

/* ── Shared framebuffer for output ── */
static SWBitmap *g_framebuffer = NULL;
static const char *CANVAS_OUTPUT = "/data/a2oh/canvas_out.bin";

static void ensure_framebuffer(int w, int h) {
    if (g_framebuffer && g_framebuffer->width == w && g_framebuffer->height == h) return;
    if (g_framebuffer) sw_bitmap_destroy(g_framebuffer);
    g_framebuffer = sw_bitmap_create(w, h);
}

static void flush_framebuffer() {
    if (!g_framebuffer) return;
    /* Ensure parent directory exists */
    mkdir("/data/a2oh", 0777);
    int fd = open(CANVAS_OUTPUT, O_WRONLY | O_CREAT | O_TRUNC, 0666);
    if (fd < 0) {
        fprintf(stderr, "flush_framebuffer: open(%s) failed: %s", CANVAS_OUTPUT, strerror(errno));
        return;
    }
    /* Write header: width, height */
    write(fd, &g_framebuffer->width, 4);
    write(fd, &g_framebuffer->height, 4);
    /* Write pixels */
    write(fd, g_framebuffer->pixels, g_framebuffer->width * g_framebuffer->height * 4);
    close(fd);
}

/* Log canvas operation for debugging */
static FILE *g_log = NULL;
static void canvas_log(const char *fmt, ...) {
    if (!g_log) g_log = fopen("/data/a2oh/canvas_ops.txt", "w");
    if (!g_log) return;
    va_list args;
    va_start(args, fmt);
    vfprintf(g_log, fmt, args);
    va_end(args);
    fflush(g_log);
}

/* ── ArkUI Node API (kept from original for backward compat) ── */

typedef void* ArkUI_NodeHandle;
typedef union { float f32; int32_t i32; uint32_t u32; } ArkUI_NumberValue;
typedef struct { const ArkUI_NumberValue* value; int32_t size; const char* string; void* object; } ArkUI_AttributeItem;
typedef struct { int32_t version; } ArkUI_AnyNativeAPI;
typedef enum { ARKUI_NATIVE_NODE = 0 } ArkUI_NativeAPIVariantKind;
typedef struct {
    int32_t version;
    ArkUI_NodeHandle (*createNode)(int32_t type);
    void (*disposeNode)(ArkUI_NodeHandle node);
    int32_t (*addChild)(ArkUI_NodeHandle parent, ArkUI_NodeHandle child);
    int32_t (*removeChild)(ArkUI_NodeHandle parent, ArkUI_NodeHandle child);
    int32_t (*insertChildAt)(ArkUI_NodeHandle parent, ArkUI_NodeHandle child, int32_t pos);
    int32_t (*setAttribute)(ArkUI_NodeHandle node, int32_t attr, const ArkUI_AttributeItem* item);
    const ArkUI_AttributeItem* (*getAttribute)(ArkUI_NodeHandle node, int32_t attr);
    int32_t (*resetAttribute)(ArkUI_NodeHandle node, int32_t attr);
    int32_t (*registerNodeEvent)(ArkUI_NodeHandle node, int32_t eventType, int32_t targetId);
    void (*unregisterNodeEvent)(ArkUI_NodeHandle node, int32_t eventType);
} ArkUI_NativeNodeAPI_1;
typedef ArkUI_AnyNativeAPI* (*GetNativeAPI_fn)(ArkUI_NativeAPIVariantKind, int32_t);

static ArkUI_NativeNodeAPI_1* g_api = NULL;
static volatile int g_initDone = 0;
static void* g_libHandle = NULL;

static int initAPI() {
    if (g_initDone) return g_api ? 0 : -1;
    g_initDone = 1;
    const char* paths[] = { "libace_ndk.z.so", "/system/lib/libace_ndk.z.so", NULL };
    for (int i = 0; paths[i]; i++) {
        g_libHandle = dlopen(paths[i], RTLD_NOW);
        if (g_libHandle) break;
    }
    if (!g_libHandle) return -1;
    GetNativeAPI_fn getAPI = (GetNativeAPI_fn)dlsym(g_libHandle, "OH_ArkUI_GetNativeAPI");
    if (!getAPI) { dlclose(g_libHandle); g_libHandle = NULL; return -1; }
    ArkUI_AnyNativeAPI* any = getAPI(ARKUI_NATIVE_NODE, 1);
    if (!any || any->version < 1) { dlclose(g_libHandle); g_libHandle = NULL; return -1; }
    g_api = (ArkUI_NativeNodeAPI_1*)any;
    return 0;
}

extern "C" {

/* ── ArkUI Node API JNI (unchanged) ── */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeCreate(JNIEnv*, jclass, jint type) {
    if (initAPI() < 0 || !g_api->createNode) return 0;
    return (jlong)(uintptr_t)g_api->createNode(type);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeDispose(JNIEnv*, jclass, jlong h) {
    if (!g_api || !g_api->disposeNode || !h) return;
    g_api->disposeNode((ArkUI_NodeHandle)(uintptr_t)h);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeAddChild(JNIEnv*, jclass, jlong p, jlong c) {
    if (!g_api || !g_api->addChild || !p || !c) return;
    g_api->addChild((ArkUI_NodeHandle)(uintptr_t)p, (ArkUI_NodeHandle)(uintptr_t)c);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeRemoveChild(JNIEnv*, jclass, jlong p, jlong c) {
    if (!g_api || !g_api->removeChild || !p || !c) return;
    g_api->removeChild((ArkUI_NodeHandle)(uintptr_t)p, (ArkUI_NodeHandle)(uintptr_t)c);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeInsertChildAt(JNIEnv*, jclass, jlong p, jlong c, jint pos) {
    if (!g_api || !g_api->insertChildAt || !p || !c) return;
    g_api->insertChildAt((ArkUI_NodeHandle)(uintptr_t)p, (ArkUI_NodeHandle)(uintptr_t)c, pos);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrString(JNIEnv* env, jclass, jlong h, jint attr, jstring jval) {
    if (!g_api || !g_api->setAttribute || !h || !jval) return;
    const char* val = env->GetStringUTFChars(jval, NULL);
    if (!val) return;
    ArkUI_AttributeItem item = {}; item.string = val;
    g_api->setAttribute((ArkUI_NodeHandle)(uintptr_t)h, attr, &item);
    env->ReleaseStringUTFChars(jval, val);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrInt(JNIEnv*, jclass, jlong h, jint attr, jint val) {
    if (!g_api || !g_api->setAttribute || !h) return;
    ArkUI_NumberValue nv; nv.i32 = val;
    ArkUI_AttributeItem item = {}; item.value = &nv; item.size = 1;
    g_api->setAttribute((ArkUI_NodeHandle)(uintptr_t)h, attr, &item);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrFloat(JNIEnv*, jclass, jlong h, jint attr, jfloat val) {
    if (!g_api || !g_api->setAttribute || !h) return;
    ArkUI_NumberValue nv; nv.f32 = val;
    ArkUI_AttributeItem item = {}; item.value = &nv; item.size = 1;
    g_api->setAttribute((ArkUI_NodeHandle)(uintptr_t)h, attr, &item);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrColor(JNIEnv*, jclass, jlong h, jint attr, jint argb) {
    if (!g_api || !g_api->setAttribute || !h) return;
    ArkUI_NumberValue nv; nv.u32 = (uint32_t)argb;
    ArkUI_AttributeItem item = {}; item.value = &nv; item.size = 1;
    g_api->setAttribute((ArkUI_NodeHandle)(uintptr_t)h, attr, &item);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeRegisterEvent(JNIEnv*, jclass, jlong h, jint eventType, jint targetId) {
    if (!g_api || !g_api->registerNodeEvent || !h) return;
    g_api->registerNodeEvent((ArkUI_NodeHandle)(uintptr_t)h, eventType, targetId);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeUnregisterEvent(JNIEnv*, jclass, jlong h, jint eventType) {
    if (!g_api || !g_api->unregisterNodeEvent || !h) return;
    g_api->unregisterNodeEvent((ArkUI_NodeHandle)(uintptr_t)h, eventType);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeMarkDirty(JNIEnv*, jclass, jlong, jint) {}

#ifdef HAVE_SKIA_STATIC
/* Forward declarations — these are provided by skia_canvas_direct.cpp */
extern "C" {
JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapCreate(JNIEnv*, jclass, jint, jint, jint);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapDestroy(JNIEnv*, jclass, jlong);
JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapGetWidth(JNIEnv*, jclass, jlong);
JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapGetHeight(JNIEnv*, jclass, jlong);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapSetPixel(JNIEnv*, jclass, jlong, jint, jint, jint);
JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapGetPixel(JNIEnv*, jclass, jlong, jint, jint);
JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapWriteToFile(JNIEnv*, jclass, jlong, jstring);
JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapBlitToFb0(JNIEnv*, jclass, jlong, jint);
JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasCreate(JNIEnv*, jclass, jlong);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDestroy(JNIEnv*, jclass, jlong);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawColor(JNIEnv*, jclass, jlong, jint);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawRect(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat, jfloat, jlong, jlong);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawCircle(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat, jlong, jlong);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawLine(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat, jfloat, jlong);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawPath(JNIEnv*, jclass, jlong, jlong, jlong, jlong);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawBitmap(JNIEnv*, jclass, jlong, jlong, jfloat, jfloat);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawText(JNIEnv*, jclass, jlong, jstring, jfloat, jfloat, jlong, jlong, jlong);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasSave(JNIEnv*, jclass, jlong);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasRestore(JNIEnv*, jclass, jlong);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasTranslate(JNIEnv*, jclass, jlong, jfloat, jfloat);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasScale(JNIEnv*, jclass, jlong, jfloat, jfloat);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasRotate(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasClipRect(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat, jfloat);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasClipPath(JNIEnv*, jclass, jlong, jlong);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasConcat(JNIEnv*, jclass, jlong, jfloatArray);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawArc(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jboolean, jlong, jlong);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawOval(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat, jfloat, jlong, jlong);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawRoundRect(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jlong, jlong);
JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_fontCreate(JNIEnv*, jclass);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_fontDestroy(JNIEnv*, jclass, jlong);
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_fontSetSize(JNIEnv*, jclass, jlong, jfloat);
JNIEXPORT jfloat JNICALL Java_com_ohos_shim_bridge_OHBridge_fontMeasureText(JNIEnv*, jclass, jlong, jstring);
JNIEXPORT jfloatArray JNICALL Java_com_ohos_shim_bridge_OHBridge_fontGetMetrics(JNIEnv*, jclass, jlong);
}
#endif

#ifndef HAVE_SKIA_STATIC
/* ── Bitmap JNI — real pixel buffer (skipped when Skia provides these) ── */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapCreate(JNIEnv*, jclass, jint w, jint h, jint fmt) {
    (void)fmt;
    SWBitmap *b = sw_bitmap_create(w, h);
    canvas_log("bitmapCreate %dx%d -> %p\n", w, h, b);
    return (jlong)(uintptr_t)b;
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapDestroy(JNIEnv*, jclass, jlong h) {
    if (h) sw_bitmap_destroy((SWBitmap*)(uintptr_t)h);
}
JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapGetWidth(JNIEnv*, jclass, jlong h) {
    SWBitmap *b = (SWBitmap*)(uintptr_t)h;
    return b ? b->width : 0;
}
JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapGetHeight(JNIEnv*, jclass, jlong h) {
    SWBitmap *b = (SWBitmap*)(uintptr_t)h;
    return b ? b->height : 0;
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapSetPixel(JNIEnv*, jclass, jlong h, jint x, jint y, jint argb) {
    SWBitmap *b = (SWBitmap*)(uintptr_t)h;
    if (b && x >= 0 && x < b->width && y >= 0 && y < b->height)
        b->pixels[y * b->width + x] = (uint32_t)argb;
}
JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapGetPixel(JNIEnv*, jclass, jlong h, jint x, jint y) {
    SWBitmap *b = (SWBitmap*)(uintptr_t)h;
    if (b && x >= 0 && x < b->width && y >= 0 && y < b->height)
        return (jint)b->pixels[y * b->width + x];
    return 0;
}

/* ── Bulk pixel operations (no per-pixel JNI crossing) ── */

JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapWriteToFile(JNIEnv* env, jclass, jlong h, jstring jpath) {
    SWBitmap *b = (SWBitmap*)(uintptr_t)h;
    if (!b || !b->pixels) return -1;
    const char *path = env->GetStringUTFChars(jpath, NULL);
    if (!path) return -2;
    mkdir("/data/a2oh", 0777);
    int fd = open(path, O_WRONLY | O_CREAT | O_TRUNC, 0666);
    env->ReleaseStringUTFChars(jpath, path);
    if (fd < 0) return -3;
    int w = b->width, h2 = b->height;
    write(fd, &w, 4);
    write(fd, &h2, 4);
    write(fd, b->pixels, w * h2 * 4);
    close(fd);
    return w * h2;
}

JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapBlitToFb0(JNIEnv*, jclass, jlong h, jint scrollY) {
    SWBitmap *b = (SWBitmap*)(uintptr_t)h;
    if (!b || !b->pixels) return -1;
    /* Try /dev/fb0 first, then mknod */
    int fb = open("/dev/fb0", O_RDWR);
    if (fb < 0) {
        mkdir("/dev", 0755);
        mknod("/dev/fb0", S_IFCHR | 0666, (dev_t)((29 << 8) | 0));
        fb = open("/dev/fb0", O_RDWR);
    }
    if (fb < 0) return -2;
    /* Get framebuffer size via ioctl or assume 1280x800 */
    int fbW = 1280, fbH = 800;
    int fbSize = fbW * fbH * 4;
    uint32_t *fbMem = (uint32_t*)mmap(NULL, fbSize, PROT_READ | PROT_WRITE, MAP_SHARED, fb, 0);
    if (fbMem == MAP_FAILED) {
        /* Fallback: write sequentially */
        lseek(fb, 0, SEEK_SET);
        for (int y = 0; y < fbH; y++) {
            int sy = y + scrollY;
            if (sy >= 0 && sy < b->height && b->width >= fbW)
                write(fb, &b->pixels[sy * b->width], fbW * 4);
            else {
                uint32_t blank[1280];
                memset(blank, 0xF5, fbW * 4); /* light gray */
                write(fb, blank, fbW * 4);
            }
        }
        close(fb);
        return fbW * fbH;
    }
    /* mmap blit — fast */
    for (int y = 0; y < fbH; y++) {
        int sy = y + scrollY;
        if (sy >= 0 && sy < b->height) {
            int copyW = (b->width < fbW) ? b->width : fbW;
            memcpy(&fbMem[y * fbW], &b->pixels[sy * b->width], copyW * 4);
        } else {
            memset(&fbMem[y * fbW], 0xF5, fbW * 4);
        }
    }
    munmap(fbMem, fbSize);
    close(fb);
    return fbW * fbH;
}

/* ── Canvas JNI — real software renderer ── */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasCreate(JNIEnv*, jclass, jlong bmpH) {
    try_load_skia();
    SWBitmap *bmp = (SWBitmap*)(uintptr_t)bmpH;
    if (!bmp) {
        ensure_framebuffer(1280, 800);
        bmp = g_framebuffer;
    }

    HybridCanvas *hc = (HybridCanvas*)calloc(1, sizeof(HybridCanvas));
    hc->swBitmap = bmp;

    if (g_skia_ok) {
        /* Create Skia canvas bound to a Skia bitmap of the same size */
        hc->skBitmap = sk_BitmapCreate();
        OH_BitmapFormat fmt = {4 /* RGBA_8888 */, 2 /* PREMUL */};
        sk_BitmapBuild(hc->skBitmap, bmp->width, bmp->height, &fmt);
        hc->skCanvas = sk_CanvasCreate();
        sk_CanvasBind(hc->skCanvas, hc->skBitmap);
        hc->skPen = sk_PenCreate ? sk_PenCreate() : NULL;
        hc->skBrush = sk_BrushCreate ? sk_BrushCreate() : NULL;
        hc->skFont = sk_FontCreate();
        if (sk_TypefaceCreateFromFile)
            hc->skTypeface = sk_TypefaceCreateFromFile(FONT_PATH, 0);
        if (!hc->skTypeface && sk_TypefaceCreateDefault)
            hc->skTypeface = sk_TypefaceCreateDefault();
        if (hc->skTypeface && sk_FontSetTypeface)
            sk_FontSetTypeface(hc->skFont, hc->skTypeface);
        hc->skW = bmp->width;
        hc->skH = bmp->height;
        fprintf(stderr, "canvasCreate: Skia backend %dx%d\n", bmp->width, bmp->height);
    } else {
        /* Software fallback */
        sw_load_font(FONT_PATH);
        sw_load_font("/font.ttf");
        sw_load_font("/system/fonts/DroidSans.ttf");
        hc->sw = sw_canvas_create(bmp);
        fprintf(stderr, "canvasCreate: stb_truetype fallback %dx%d\n", bmp->width, bmp->height);
    }
    return (jlong)(uintptr_t)hc;
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDestroy(JNIEnv*, jclass, jlong h) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)h;
    if (!hc) return;
    if (hc->skCanvas) {
        /* Copy Skia pixels back to SWBitmap so bitmapWriteToFile/BlitToFb0 work */
        if (hc->skBitmap && hc->swBitmap && sk_BitmapGetPixels) {
            void *skPx = sk_BitmapGetPixels(hc->skBitmap);
            if (skPx) {
                int n = hc->swBitmap->width * hc->swBitmap->height;
                /* Skia uses RGBA, SWBitmap uses ARGB — convert */
                uint32_t *src = (uint32_t*)skPx;
                uint32_t *dst = hc->swBitmap->pixels;
                for (int i = 0; i < n; i++) {
                    uint32_t rgba = src[i];
                    uint8_t r = rgba & 0xFF, g = (rgba>>8)&0xFF, b = (rgba>>16)&0xFF, a = (rgba>>24)&0xFF;
                    dst[i] = (a << 24) | (r << 16) | (g << 8) | b;
                }
            }
        }
        if (sk_FontDestroy && hc->skFont) sk_FontDestroy(hc->skFont);
        if (sk_TypefaceDestroy && hc->skTypeface) sk_TypefaceDestroy(hc->skTypeface);
        if (sk_PenDestroy && hc->skPen) sk_PenDestroy(hc->skPen);
        if (sk_BrushDestroy && hc->skBrush) sk_BrushDestroy(hc->skBrush);
        sk_CanvasDestroy(hc->skCanvas);
        sk_BitmapDestroy(hc->skBitmap);
    }
    if (hc->sw) free(hc->sw);
    free(hc);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawColor(JNIEnv*, jclass, jlong h, jint argb) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)h;
    if (!hc) return;
    if (hc->skCanvas && sk_CanvasClear) {
        sk_CanvasClear(hc->skCanvas, (uint32_t)argb);
    } else if (hc->sw && hc->sw->bitmap) {
        uint32_t color = (uint32_t)argb;
        int n = hc->sw->bitmap->width * hc->sw->bitmap->height;
        for (int i = 0; i < n; i++) hc->sw->bitmap->pixels[i] = color;
    }
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawRect(
    JNIEnv*, jclass, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b, jlong penH, jlong brushH) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)ch;
    if (!hc) return;
    SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
    SWPen *pen = (SWPen*)(uintptr_t)penH;
    if (hc->skCanvas && sk_RectCreate) {
        OH_Rect *rect = sk_RectCreate(l, t, r, b);
        if (brush && brush != (SWBrush*)1) {
            sk_BrushSetColor(hc->skBrush, (uint32_t)brush->color);
            sk_BrushSetAntiAlias(hc->skBrush, 1);
            sk_CanvasAttachBrush(hc->skCanvas, hc->skBrush);
            sk_CanvasDrawRect(hc->skCanvas, rect);
            sk_CanvasDetachBrush(hc->skCanvas);
        }
        if (pen && pen != (SWPen*)1) {
            sk_PenSetColor(hc->skPen, (uint32_t)pen->color);
            sk_PenSetWidth(hc->skPen, pen->width);
            sk_PenSetAntiAlias(hc->skPen, 1);
            sk_CanvasAttachPen(hc->skCanvas, hc->skPen);
            sk_CanvasDrawRect(hc->skCanvas, rect);
            sk_CanvasDetachPen(hc->skCanvas);
        }
        sk_RectDestroy(rect);
    } else if (hc->sw) {
        if (brush && brush != (SWBrush*)1) sw_canvas_fill_rect(hc->sw, l, t, r, b, (uint32_t)brush->color);
        if (pen && pen != (SWPen*)1) sw_canvas_stroke_rect(hc->sw, l, t, r, b, (uint32_t)pen->color, pen->width);
    }
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawCircle(
    JNIEnv*, jclass, jlong ch, jfloat cx, jfloat cy, jfloat r, jlong penH, jlong brushH) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)ch;
    if (!hc) return;
    SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
    SWPen *pen = (SWPen*)(uintptr_t)penH;
    if (hc->skCanvas && sk_CanvasDrawCircle) {
        float pt[2] = {cx, cy};
        if (brush && brush != (SWBrush*)1) {
            sk_BrushSetColor(hc->skBrush, (uint32_t)brush->color);
            sk_CanvasAttachBrush(hc->skCanvas, hc->skBrush);
            sk_CanvasDrawCircle(hc->skCanvas, pt, r);
            sk_CanvasDetachBrush(hc->skCanvas);
        }
        if (pen && pen != (SWPen*)1) {
            sk_PenSetColor(hc->skPen, (uint32_t)pen->color);
            sk_PenSetWidth(hc->skPen, pen->width);
            sk_CanvasAttachPen(hc->skCanvas, hc->skPen);
            sk_CanvasDrawCircle(hc->skCanvas, pt, r);
            sk_CanvasDetachPen(hc->skCanvas);
        }
    } else if (hc->sw) {
        if (brush && brush != (SWBrush*)1) sw_canvas_fill_circle(hc->sw, cx, cy, r, (uint32_t)brush->color);
    }
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawLine(
    JNIEnv*, jclass, jlong ch, jfloat x1, jfloat y1, jfloat x2, jfloat y2, jlong penH) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)ch;
    SWPen *pen = (SWPen*)(uintptr_t)penH;
    if (!hc) return;
    if (hc->skCanvas && sk_CanvasDrawLine) {
        uint32_t color = (pen && pen != (SWPen*)1) ? (uint32_t)pen->color : 0xFF000000;
        float w = (pen && pen != (SWPen*)1) ? pen->width : 1.0f;
        sk_PenSetColor(hc->skPen, color);
        sk_PenSetWidth(hc->skPen, w);
        sk_CanvasAttachPen(hc->skCanvas, hc->skPen);
        sk_CanvasDrawLine(hc->skCanvas, x1, y1, x2, y2);
        sk_CanvasDetachPen(hc->skCanvas);
    } else if (hc->sw) {
        float w = (pen && pen != (SWPen*)1) ? pen->width : 1.0f;
        uint32_t color = (pen && pen != (SWPen*)1) ? (uint32_t)pen->color : 0xFF000000;
        sw_canvas_draw_line(hc->sw, x1, y1, x2, y2, color, w);
    }
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawText(
    JNIEnv* env, jclass, jlong ch, jstring jtext, jfloat x, jfloat y, jlong fontH, jlong penH, jlong brushH) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)ch;
    if (!hc || !jtext) return;
    const char *text = env->GetStringUTFChars(jtext, NULL);
    if (!text) return;
    SWFont *f = (SWFont*)(uintptr_t)fontH;
    SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
    SWPen *pen = (SWPen*)(uintptr_t)penH;
    float size = (f && f != (SWFont*)1) ? f->size : 16.0f;
    uint32_t color = 0xFF000000;
    if (brush && brush != (SWBrush*)1) color = (uint32_t)brush->color;
    else if (pen && pen != (SWPen*)1) color = (uint32_t)pen->color;

    if (hc->skCanvas && sk_TextBlobCreateFromString && sk_CanvasDrawTextBlob) {
        sk_FontSetTextSize(hc->skFont, size);
        OH_TextBlob *blob = sk_TextBlobCreateFromString(text, hc->skFont, 0);
        if (blob) {
            sk_BrushSetColor(hc->skBrush, color);
            sk_BrushSetAntiAlias(hc->skBrush, 1);
            sk_CanvasAttachBrush(hc->skCanvas, hc->skBrush);
            sk_CanvasDrawTextBlob(hc->skCanvas, blob, x, y);
            sk_CanvasDetachBrush(hc->skCanvas);
            sk_TextBlobDestroy(blob);
        }
    } else if (hc->sw) {
        sw_canvas_draw_text(hc->sw, text, x, y, size, color);
    }
    env->ReleaseStringUTFChars(jtext, text);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawBitmap(
    JNIEnv*, jclass, jlong ch, jlong bmpH, jfloat x, jfloat y) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)ch;
    SWBitmap *src = (SWBitmap*)(uintptr_t)bmpH;
    if (!hc || !src) return;
    /* Skia drawBitmap would need a separate Skia bitmap — use SW fallback for now */
    if (hc->sw && hc->sw->bitmap) {
        int W = hc->sw->bitmap->width, H = hc->sw->bitmap->height;
        int dx = (int)(x * hc->sw->sx + hc->sw->tx), dy = (int)(y * hc->sw->sy + hc->sw->ty);
        for (int row = 0; row < src->height && dy + row < H; row++)
            for (int col = 0; col < src->width && dx + col < W; col++)
                if (dx + col >= 0 && dy + row >= 0)
                    sw_pixel_blend(&hc->sw->bitmap->pixels[(dy + row) * W + (dx + col)],
                                   src->pixels[row * src->width + col]);
    }
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawPath(
    JNIEnv*, jclass, jlong ch, jlong pathH, jlong penH, jlong brushH) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)ch;
    if (!hc) return;
    if (hc->skCanvas && sk_CanvasDrawPath) {
        SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
        SWPen *pen = (SWPen*)(uintptr_t)penH;
        /* pathH is an SWPath with sw_path_* — can't pass directly to Skia */
        /* TODO: convert SWPath to OH_Drawing_Path */
    }
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasSave(JNIEnv*, jclass, jlong ch) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)ch;
    if (!hc) return;
    if (hc->skCanvas && sk_CanvasSave) sk_CanvasSave(hc->skCanvas);
    if (hc->sw) sw_canvas_save(hc->sw);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasRestore(JNIEnv*, jclass, jlong ch) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)ch;
    if (!hc) return;
    if (hc->skCanvas && sk_CanvasRestore) sk_CanvasRestore(hc->skCanvas);
    if (hc->sw) sw_canvas_restore(hc->sw);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasTranslate(JNIEnv*, jclass, jlong ch, jfloat dx, jfloat dy) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)ch;
    if (!hc) return;
    if (hc->skCanvas && sk_CanvasTranslate) sk_CanvasTranslate(hc->skCanvas, dx, dy);
    if (hc->sw) { hc->sw->tx += dx * hc->sw->sx; hc->sw->ty += dy * hc->sw->sy; }
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasScale(JNIEnv*, jclass, jlong ch, jfloat sx, jfloat sy) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)ch;
    if (!hc) return;
    if (hc->skCanvas && sk_CanvasScale) sk_CanvasScale(hc->skCanvas, sx, sy);
    if (hc->sw) { hc->sw->sx *= sx; hc->sw->sy *= sy; }
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasRotate(JNIEnv*, jclass, jlong ch, jfloat deg, jfloat px, jfloat py) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)ch;
    if (hc && hc->skCanvas && sk_CanvasRotate) sk_CanvasRotate(hc->skCanvas, deg, px, py);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasClipRect(JNIEnv*, jclass, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)ch;
    if (!hc) return;
    if (hc->skCanvas && sk_CanvasClipRect && sk_RectCreate) {
        OH_Rect *rect = sk_RectCreate(l, t, r, b);
        sk_CanvasClipRect(hc->skCanvas, rect, 1 /* INTERSECT */, 1);
        sk_RectDestroy(rect);
    }
    if (hc->sw) {
        float cl = l * hc->sw->sx + hc->sw->tx, ct = t * hc->sw->sy + hc->sw->ty;
        float cr = r * hc->sw->sx + hc->sw->tx, cb = b * hc->sw->sy + hc->sw->ty;
        if (cl > hc->sw->clipL) hc->sw->clipL = cl;
        if (ct > hc->sw->clipT) hc->sw->clipT = ct;
        if (cr < hc->sw->clipR) hc->sw->clipR = cr;
        if (cb < hc->sw->clipB) hc->sw->clipB = cb;
    }
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasClipPath(JNIEnv*, jclass, jlong, jlong) {}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasConcat(JNIEnv*, jclass, jlong, jfloatArray) {}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawArc(
    JNIEnv*, jclass, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b, jfloat startAngle, jfloat sweepAngle, jboolean, jlong penH, jlong brushH) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)ch;
    if (hc && hc->skCanvas && sk_CanvasDrawArc && sk_RectCreate) {
        SWPen *pen = (SWPen*)(uintptr_t)penH;
        if (pen && pen != (SWPen*)1) {
            OH_Rect *rect = sk_RectCreate(l, t, r, b);
            sk_PenSetColor(hc->skPen, (uint32_t)pen->color);
            sk_PenSetWidth(hc->skPen, pen->width);
            sk_CanvasAttachPen(hc->skCanvas, hc->skPen);
            sk_CanvasDrawArc(hc->skCanvas, rect, startAngle, sweepAngle);
            sk_CanvasDetachPen(hc->skCanvas);
            sk_RectDestroy(rect);
        }
    }
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawOval(
    JNIEnv*, jclass, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b, jlong penH, jlong brushH) {
    HybridCanvas *hc = (HybridCanvas*)(uintptr_t)ch;
    if (!hc) return;
    SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
    if (hc->skCanvas && sk_CanvasDrawOval && sk_RectCreate) {
        OH_Rect *rect = sk_RectCreate(l, t, r, b);
        if (brush && brush != (SWBrush*)1) {
            sk_BrushSetColor(hc->skBrush, (uint32_t)brush->color);
            sk_CanvasAttachBrush(hc->skCanvas, hc->skBrush);
            sk_CanvasDrawOval(hc->skCanvas, rect);
            sk_CanvasDetachBrush(hc->skCanvas);
        }
        sk_RectDestroy(rect);
    } else if (hc->sw && brush && brush != (SWBrush*)1) {
        sw_canvas_fill_circle(hc->sw, (l+r)/2, (t+b)/2, (r-l)/2, (uint32_t)brush->color);
    }
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawRoundRect(
    JNIEnv*, jclass, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b, jfloat rx, jfloat ry, jlong penH, jlong brushH) {
    SWCanvas *c = (SWCanvas*)(uintptr_t)ch;
    SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
    if (c && brush && brush != (SWBrush*)1)
        sw_canvas_fill_roundrect(c, l, t, r, b, rx, ry, (uint32_t)brush->color);
}

#endif /* !HAVE_SKIA_STATIC */

/* ── Surface stubs (not used in headless mode) ── */
/* ── Surface JNI — framebuffer-backed rendering surface ── */
struct SWSurface {
    SWBitmap *bitmap;
    SWCanvas *canvas;
    int width, height;
    int fb_fd;
    uint32_t *fb_mmap;
};

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_surfaceCreate(JNIEnv*, jclass, jlong, jint w, jint h) {
    SWSurface *s = (SWSurface*)calloc(1, sizeof(SWSurface));
    s->width = w; s->height = h;
    s->bitmap = sw_bitmap_create(w, h);
    sw_load_font(FONT_PATH);
    sw_load_font("/data/a2oh/font.ttf");
    s->canvas = sw_canvas_create(s->bitmap);
    /* Open fb0 */
    mkdir("/dev", 0755);
    mknod("/dev/fb0", S_IFCHR | 0666, (dev_t)((29 << 8) | 0));
    s->fb_fd = open("/dev/fb0", O_RDWR);
    if (s->fb_fd >= 0) {
        int fbSize = 1280 * 800 * 4; /* VNC default */
        s->fb_mmap = (uint32_t*)mmap(NULL, fbSize, PROT_READ|PROT_WRITE, MAP_SHARED, s->fb_fd, 0);
        if (s->fb_mmap == MAP_FAILED) s->fb_mmap = NULL;
    }
    fprintf(stderr, "surfaceCreate %dx%d fb=%d mmap=%p\n", w, h, s->fb_fd, s->fb_mmap);
    return (jlong)(uintptr_t)s;
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_surfaceDestroy(JNIEnv*, jclass, jlong h) {
    SWSurface *s = (SWSurface*)(uintptr_t)h;
    if (!s) return;
    if (s->fb_mmap && s->fb_mmap != MAP_FAILED) munmap(s->fb_mmap, 1280*800*4);
    if (s->fb_fd >= 0) close(s->fb_fd);
    if (s->canvas) free(s->canvas);
    if (s->bitmap) sw_bitmap_destroy(s->bitmap);
    free(s);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_surfaceResize(JNIEnv*, jclass, jlong h, jint w, jint h2) {
    SWSurface *s = (SWSurface*)(uintptr_t)h;
    if (!s) return;
    if (s->bitmap) sw_bitmap_destroy(s->bitmap);
    s->bitmap = sw_bitmap_create(w, h2);
    if (s->canvas) free(s->canvas);
    s->canvas = sw_canvas_create(s->bitmap);
    s->width = w; s->height = h2;
}

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_surfaceGetCanvas(JNIEnv*, jclass, jlong h) {
    SWSurface *s = (SWSurface*)(uintptr_t)h;
    if (!s) return 0;
    return (jlong)(uintptr_t)s->canvas;
}

JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_surfaceFlush(JNIEnv*, jclass, jlong h) {
    SWSurface *s = (SWSurface*)(uintptr_t)h;
    if (!s || !s->bitmap || !s->bitmap->pixels) return -1;
    int bw = s->bitmap->width, bh = s->bitmap->height;
    int fbW = 1280, fbH = 800;
    if (s->fb_mmap) {
        for (int y = 0; y < fbH && y < bh; y++) {
            int copyW = (bw < fbW) ? bw : fbW;
            memcpy(&s->fb_mmap[y * fbW], &s->bitmap->pixels[y * bw], copyW * 4);
        }
        return bw * bh;
    } else if (s->fb_fd >= 0) {
        lseek(s->fb_fd, 0, SEEK_SET);
        for (int y = 0; y < fbH && y < bh; y++)
            write(s->fb_fd, &s->bitmap->pixels[y * bw], (bw < fbW ? bw : fbW) * 4);
        return bw * bh;
    }
    return -2;
}

/* ── Pen JNI — real state tracking ── */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_penCreate(JNIEnv*, jclass) {
    SWPen *p = (SWPen*)calloc(1, sizeof(SWPen));
    p->color = 0xFF000000;
    p->width = 1.0f;
    return (jlong)(uintptr_t)p;
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_penDestroy(JNIEnv*, jclass, jlong h) {
    if (h > 1) free((void*)(uintptr_t)h);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_penSetColor(JNIEnv*, jclass, jlong h, jint argb) {
    SWPen *p = (SWPen*)(uintptr_t)h;
    if (p && p != (SWPen*)1) p->color = argb;
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_penSetStrokeWidth(JNIEnv*, jclass, jlong h, jfloat w) {
    SWPen *p = (SWPen*)(uintptr_t)h;
    if (p && p != (SWPen*)1) p->width = w;
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_penSetAntiAlias(JNIEnv*, jclass, jlong h, jboolean aa) {
    SWPen *p = (SWPen*)(uintptr_t)h;
    if (p && p != (SWPen*)1) p->antialias = aa;
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_penSetCap(JNIEnv*, jclass, jlong h, jint cap) {
    SWPen *p = (SWPen*)(uintptr_t)h;
    if (p && p != (SWPen*)1) p->cap = cap;
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_penSetJoin(JNIEnv*, jclass, jlong h, jint join) {
    SWPen *p = (SWPen*)(uintptr_t)h;
    if (p && p != (SWPen*)1) p->join = join;
}

/* ── Brush JNI — real state tracking ── */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_brushCreate(JNIEnv*, jclass) {
    SWBrush *b = (SWBrush*)calloc(1, sizeof(SWBrush));
    b->color = 0xFF000000;
    return (jlong)(uintptr_t)b;
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_brushDestroy(JNIEnv*, jclass, jlong h) {
    if (h > 1) free((void*)(uintptr_t)h);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_brushSetColor(JNIEnv*, jclass, jlong h, jint argb) {
    SWBrush *b = (SWBrush*)(uintptr_t)h;
    if (b && b != (SWBrush*)1) b->color = argb;
}

#ifndef HAVE_SKIA_STATIC
/* ── Font JNI — real state tracking (skipped when Skia provides these) ── */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_fontCreate(JNIEnv*, jclass) {
    SWFont *f = (SWFont*)calloc(1, sizeof(SWFont));
    f->size = 16.0f;
    return (jlong)(uintptr_t)f;
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_fontDestroy(JNIEnv*, jclass, jlong h) {
    if (h > 1) free((void*)(uintptr_t)h);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_fontSetSize(JNIEnv*, jclass, jlong h, jfloat sz) {
    SWFont *f = (SWFont*)(uintptr_t)h;
    if (f && f != (SWFont*)1) f->size = sz;
}
JNIEXPORT jfloat JNICALL Java_com_ohos_shim_bridge_OHBridge_fontMeasureText(JNIEnv* env, jclass, jlong h, jstring jtext) {
    SWFont *f = (SWFont*)(uintptr_t)h;
    if (!f || f == (SWFont*)1 || !jtext || !g_sw_font_loaded) return 0.0f;
    const char *text = env->GetStringUTFChars(jtext, NULL);
    if (!text) return 0.0f;
    float scale = stbtt_ScaleForPixelHeight(&g_sw_font, f->size);
    float width = 0.0f;
    for (int i = 0; text[i]; i++) {
        int advance, lsb;
        stbtt_GetCodepointHMetrics(&g_sw_font, (unsigned char)text[i], &advance, &lsb);
        width += advance * scale;
        if (text[i + 1]) {
            int kern = stbtt_GetCodepointKernAdvance(&g_sw_font, (unsigned char)text[i], (unsigned char)text[i + 1]);
            width += kern * scale;
        }
    }
    env->ReleaseStringUTFChars(jtext, text);
    return width;
}

JNIEXPORT jfloatArray JNICALL Java_com_ohos_shim_bridge_OHBridge_fontGetMetrics(JNIEnv* env, jclass, jlong h) {
    SWFont *f = (SWFont*)(uintptr_t)h;
    jfloatArray result = env->NewFloatArray(3);
    if (!result) return NULL;
    float metrics[3] = {0, 0, 0}; /* ascent(neg), descent, leading */
    if (f && f != (SWFont*)1 && g_sw_font_loaded) {
        float scale = stbtt_ScaleForPixelHeight(&g_sw_font, f->size);
        int asc, desc, gap;
        stbtt_GetFontVMetrics(&g_sw_font, &asc, &desc, &gap);
        metrics[0] = asc * scale * -1.0f;  /* Android ascent is negative */
        metrics[1] = -desc * scale;         /* descent is positive */
        metrics[2] = gap * scale;           /* leading */
    } else {
        metrics[0] = -f->size * 0.93f;
        metrics[1] = f->size * 0.24f;
        metrics[2] = 0;
    }
    env->SetFloatArrayRegion(result, 0, 3, metrics);
    return result;
}

#endif /* !HAVE_SKIA_STATIC font */

/* ── Path JNI — real path state ── */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_pathCreate(JNIEnv*, jclass) {
    SWPath *p = (SWPath*)calloc(1, sizeof(SWPath));
    return (jlong)(uintptr_t)p;
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathDestroy(JNIEnv*, jclass, jlong h) {
    if (h) free((void*)(uintptr_t)h);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathMoveTo(JNIEnv*, jclass, jlong h, jfloat x, jfloat y) {
    SWPath *p = (SWPath*)(uintptr_t)h;
    if (p && p->count < PATH_MAX_OPS) { p->ops[p->count] = PATH_MOVE; p->xs[p->count] = x; p->ys[p->count] = y; p->count++; }
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathLineTo(JNIEnv*, jclass, jlong h, jfloat x, jfloat y) {
    SWPath *p = (SWPath*)(uintptr_t)h;
    if (p && p->count < PATH_MAX_OPS) { p->ops[p->count] = PATH_LINE; p->xs[p->count] = x; p->ys[p->count] = y; p->count++; }
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathQuadTo(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat, jfloat) {}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathCubicTo(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat) {}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathClose(JNIEnv*, jclass, jlong h) {
    SWPath *p = (SWPath*)(uintptr_t)h;
    if (p && p->count < PATH_MAX_OPS) { p->ops[p->count] = PATH_CLOSE; p->count++; }
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathReset(JNIEnv*, jclass, jlong h) {
    SWPath *p = (SWPath*)(uintptr_t)h;
    if (p) p->count = 0;
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathAddRect(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat, jfloat, jint) {}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathAddCircle(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat, jint) {}

/* ── Log stubs ── */

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_logDebug(JNIEnv* env, jclass, jstring tag, jstring msg) {
    if (!tag || !msg) return;
    const char* t = env->GetStringUTFChars(tag, NULL);
    const char* m = env->GetStringUTFChars(msg, NULL);
    fprintf(stderr, "[D/%s] %s\n", t ? t : "", m ? m : "");
    if (m) env->ReleaseStringUTFChars(msg, m);
    if (t) env->ReleaseStringUTFChars(tag, t);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_logInfo(JNIEnv* env, jclass, jstring tag, jstring msg) {
    Java_com_ohos_shim_bridge_OHBridge_logDebug(env, NULL, tag, msg);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_logWarn(JNIEnv* env, jclass, jstring tag, jstring msg) {
    Java_com_ohos_shim_bridge_OHBridge_logDebug(env, NULL, tag, msg);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_logError(JNIEnv* env, jclass, jstring tag, jstring msg) {
    Java_com_ohos_shim_bridge_OHBridge_logDebug(env, NULL, tag, msg);
}

} /* extern "C" */

/* ── JNI Registration for OHBridge ── */

static bool ohbridge_registerClass(JNIEnv* env, const char* className,
                                    JNINativeMethod* methods, int count) {
    jclass c = env->FindClass(className);
    if (c == NULL) { env->ExceptionClear(); return true; }
    int registered = 0;
    for (int i = 0; i < count; i++) {
        if (env->RegisterNatives(c, &methods[i], 1) < 0) {
            env->ExceptionClear();
        } else {
            registered++;
        }
    }
    fprintf(stderr, "[OHBridge] Registered %s (%d/%d methods)\n", className, registered, count);
    return true;
}

#define OHBM(name, sig, fn) { (char*)name, (char*)sig, (void*)fn }

static JNINativeMethod gOHBridgeMethods[] = {
    OHBM("bitmapCreate", "(III)J", Java_com_ohos_shim_bridge_OHBridge_bitmapCreate),
    OHBM("bitmapDestroy", "(J)V", Java_com_ohos_shim_bridge_OHBridge_bitmapDestroy),
    OHBM("bitmapGetWidth", "(J)I", Java_com_ohos_shim_bridge_OHBridge_bitmapGetWidth),
    OHBM("bitmapGetHeight", "(J)I", Java_com_ohos_shim_bridge_OHBridge_bitmapGetHeight),
    OHBM("bitmapSetPixel", "(JIII)V", Java_com_ohos_shim_bridge_OHBridge_bitmapSetPixel),
    OHBM("bitmapGetPixel", "(JII)I", Java_com_ohos_shim_bridge_OHBridge_bitmapGetPixel),
    OHBM("bitmapWriteToFile", "(JLjava/lang/String;)I", Java_com_ohos_shim_bridge_OHBridge_bitmapWriteToFile),
    OHBM("bitmapBlitToFb0", "(JI)I", Java_com_ohos_shim_bridge_OHBridge_bitmapBlitToFb0),
    OHBM("canvasCreate", "(J)J", Java_com_ohos_shim_bridge_OHBridge_canvasCreate),
    OHBM("canvasDestroy", "(J)V", Java_com_ohos_shim_bridge_OHBridge_canvasDestroy),
    OHBM("canvasDrawColor", "(JI)V", Java_com_ohos_shim_bridge_OHBridge_canvasDrawColor),
    OHBM("canvasDrawRect", "(JFFFFJJ)V", Java_com_ohos_shim_bridge_OHBridge_canvasDrawRect),
    OHBM("canvasDrawCircle", "(JFFFJJ)V", Java_com_ohos_shim_bridge_OHBridge_canvasDrawCircle),
    OHBM("canvasDrawLine", "(JFFFFJ)V", Java_com_ohos_shim_bridge_OHBridge_canvasDrawLine),
    OHBM("canvasDrawPath", "(JJJJ)V", Java_com_ohos_shim_bridge_OHBridge_canvasDrawPath),
    OHBM("canvasDrawBitmap", "(JJFF)V", Java_com_ohos_shim_bridge_OHBridge_canvasDrawBitmap),
    OHBM("canvasDrawText", "(JLjava/lang/String;FFJJJ)V", Java_com_ohos_shim_bridge_OHBridge_canvasDrawText),
    OHBM("canvasSave", "(J)V", Java_com_ohos_shim_bridge_OHBridge_canvasSave),
    OHBM("canvasRestore", "(J)V", Java_com_ohos_shim_bridge_OHBridge_canvasRestore),
    OHBM("canvasTranslate", "(JFF)V", Java_com_ohos_shim_bridge_OHBridge_canvasTranslate),
    OHBM("canvasScale", "(JFF)V", Java_com_ohos_shim_bridge_OHBridge_canvasScale),
    OHBM("canvasRotate", "(JFFF)V", Java_com_ohos_shim_bridge_OHBridge_canvasRotate),
    OHBM("canvasClipRect", "(JFFFF)V", Java_com_ohos_shim_bridge_OHBridge_canvasClipRect),
    OHBM("canvasClipPath", "(JJ)V", Java_com_ohos_shim_bridge_OHBridge_canvasClipPath),
    OHBM("canvasDrawArc", "(JFFFFFFZJJ)V", Java_com_ohos_shim_bridge_OHBridge_canvasDrawArc),
    OHBM("canvasDrawOval", "(JFFFFJJ)V", Java_com_ohos_shim_bridge_OHBridge_canvasDrawOval),
    OHBM("canvasDrawRoundRect", "(JFFFFFFJJ)V", Java_com_ohos_shim_bridge_OHBridge_canvasDrawRoundRect),
    OHBM("canvasConcat", "(J[F)V", Java_com_ohos_shim_bridge_OHBridge_canvasConcat),
    OHBM("penCreate", "()J", Java_com_ohos_shim_bridge_OHBridge_penCreate),
    OHBM("penDestroy", "(J)V", Java_com_ohos_shim_bridge_OHBridge_penDestroy),
    OHBM("penSetColor", "(JI)V", Java_com_ohos_shim_bridge_OHBridge_penSetColor),
    OHBM("penSetWidth", "(JF)V", Java_com_ohos_shim_bridge_OHBridge_penSetStrokeWidth),
    OHBM("penSetAntiAlias", "(JZ)V", Java_com_ohos_shim_bridge_OHBridge_penSetAntiAlias),
    OHBM("penSetCap", "(JI)V", Java_com_ohos_shim_bridge_OHBridge_penSetCap),
    OHBM("penSetJoin", "(JI)V", Java_com_ohos_shim_bridge_OHBridge_penSetJoin),
    OHBM("brushCreate", "()J", Java_com_ohos_shim_bridge_OHBridge_brushCreate),
    OHBM("brushDestroy", "(J)V", Java_com_ohos_shim_bridge_OHBridge_brushDestroy),
    OHBM("brushSetColor", "(JI)V", Java_com_ohos_shim_bridge_OHBridge_brushSetColor),
    OHBM("fontCreate", "()J", Java_com_ohos_shim_bridge_OHBridge_fontCreate),
    OHBM("fontDestroy", "(J)V", Java_com_ohos_shim_bridge_OHBridge_fontDestroy),
    OHBM("fontSetSize", "(JF)V", Java_com_ohos_shim_bridge_OHBridge_fontSetSize),
    OHBM("fontMeasureText", "(JLjava/lang/String;)F", Java_com_ohos_shim_bridge_OHBridge_fontMeasureText),
    OHBM("fontGetMetrics", "(J)[F", Java_com_ohos_shim_bridge_OHBridge_fontGetMetrics),
    OHBM("pathCreate", "()J", Java_com_ohos_shim_bridge_OHBridge_pathCreate),
    OHBM("pathDestroy", "(J)V", Java_com_ohos_shim_bridge_OHBridge_pathDestroy),
    OHBM("pathMoveTo", "(JFF)V", Java_com_ohos_shim_bridge_OHBridge_pathMoveTo),
    OHBM("pathLineTo", "(JFF)V", Java_com_ohos_shim_bridge_OHBridge_pathLineTo),
    OHBM("pathQuadTo", "(JFFFF)V", Java_com_ohos_shim_bridge_OHBridge_pathQuadTo),
    OHBM("pathCubicTo", "(JFFFFFF)V", Java_com_ohos_shim_bridge_OHBridge_pathCubicTo),
    OHBM("pathClose", "(J)V", Java_com_ohos_shim_bridge_OHBridge_pathClose),
    OHBM("pathReset", "(J)V", Java_com_ohos_shim_bridge_OHBridge_pathReset),
    OHBM("pathAddRect", "(JFFFFI)V", Java_com_ohos_shim_bridge_OHBridge_pathAddRect),
    OHBM("pathAddCircle", "(JFFFI)V", Java_com_ohos_shim_bridge_OHBridge_pathAddCircle),
    OHBM("surfaceCreate", "(JII)J", Java_com_ohos_shim_bridge_OHBridge_surfaceCreate),
    OHBM("surfaceDestroy", "(J)V", Java_com_ohos_shim_bridge_OHBridge_surfaceDestroy),
    OHBM("surfaceResize", "(JII)V", Java_com_ohos_shim_bridge_OHBridge_surfaceResize),
    OHBM("surfaceGetCanvas", "(J)J", Java_com_ohos_shim_bridge_OHBridge_surfaceGetCanvas),
    OHBM("surfaceFlush", "(J)I", Java_com_ohos_shim_bridge_OHBridge_surfaceFlush),
    OHBM("logDebug", "(Ljava/lang/String;Ljava/lang/String;)V", Java_com_ohos_shim_bridge_OHBridge_logDebug),
    OHBM("logInfo", "(Ljava/lang/String;Ljava/lang/String;)V", Java_com_ohos_shim_bridge_OHBridge_logInfo),
    OHBM("logWarn", "(Ljava/lang/String;Ljava/lang/String;)V", Java_com_ohos_shim_bridge_OHBridge_logWarn),
    OHBM("logError", "(Ljava/lang/String;Ljava/lang/String;)V", Java_com_ohos_shim_bridge_OHBridge_logError),
};

extern "C" bool dvmRegisterOHBridge(JNIEnv* env) {
    return ohbridge_registerClass(env, "com/ohos/shim/bridge/OHBridge",
        gOHBridgeMethods, sizeof(gOHBridgeMethods)/sizeof(gOHBridgeMethods[0]));
}
