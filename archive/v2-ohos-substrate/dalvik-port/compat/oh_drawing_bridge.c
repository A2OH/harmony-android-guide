/*
 * oh_drawing_bridge.c -- JNI bridge for OHBridge that routes Canvas/Pen/Brush/
 * Path/Bitmap/Font calls to real OH_Drawing C NDK APIs via dlopen/dlsym.
 *
 * Also includes ArkUI Node API forwarding (dlopen libace_ndk.z.so).
 *
 * Cross-compile for ARM32 OHOS with:
 *   $CLANG --target=arm-linux-ohos --sysroot=$SYSROOT \
 *     -march=armv7-a -mfloat-abi=softfp -mfpu=neon \
 *     -shared -fPIC -o /tmp/liboh_bridge.so /tmp/oh_drawing_bridge.c \
 *     -I$OH/interface/sdk_c/graphic/graphic_2d/native_drawing \
 *     -I/home/dspfac/aosp-android-11/libnativehelper/include_jni \
 *     -ldl
 */

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dlfcn.h>
#include <stdint.h>
#include <stdbool.h>
#include <stdarg.h>

/* =====================================================================
 * Forward-declare opaque OH_Drawing types (same as drawing_types.h)
 * We don't #include the headers to avoid any C++ issues; we only need
 * the opaque pointer types and the BitmapFormat struct.
 * ===================================================================== */

typedef struct OH_Drawing_Canvas OH_Drawing_Canvas;
typedef struct OH_Drawing_Pen OH_Drawing_Pen;
typedef struct OH_Drawing_Brush OH_Drawing_Brush;
typedef struct OH_Drawing_Path OH_Drawing_Path;
typedef struct OH_Drawing_Bitmap OH_Drawing_Bitmap;
typedef struct OH_Drawing_Font OH_Drawing_Font;
typedef struct OH_Drawing_Rect OH_Drawing_Rect;
typedef struct OH_Drawing_RoundRect OH_Drawing_RoundRect;
typedef struct OH_Drawing_Point OH_Drawing_Point;
typedef struct OH_Drawing_Matrix OH_Drawing_Matrix;
typedef struct OH_Drawing_TextBlob OH_Drawing_TextBlob;

typedef enum {
    OHD_COLOR_FORMAT_UNKNOWN = 0,
    OHD_COLOR_FORMAT_ALPHA_8 = 1,
    OHD_COLOR_FORMAT_RGB_565 = 2,
    OHD_COLOR_FORMAT_ARGB_4444 = 3,
    OHD_COLOR_FORMAT_RGBA_8888 = 4,
    OHD_COLOR_FORMAT_BGRA_8888 = 5
} OHD_ColorFormat;

typedef enum {
    OHD_ALPHA_FORMAT_UNKNOWN = 0,
    OHD_ALPHA_FORMAT_OPAQUE = 1,
    OHD_ALPHA_FORMAT_PREMUL = 2,
    OHD_ALPHA_FORMAT_UNPREMUL = 3
} OHD_AlphaFormat;

typedef struct {
    OHD_ColorFormat colorFormat;
    OHD_AlphaFormat alphaFormat;
} OH_Drawing_BitmapFormat;

/* =====================================================================
 * Function pointer typedefs for all OH_Drawing_* APIs we need
 * ===================================================================== */

/* Bitmap */
typedef OH_Drawing_Bitmap* (*pf_BitmapCreate)(void);
typedef void (*pf_BitmapDestroy)(OH_Drawing_Bitmap*);
typedef void (*pf_BitmapBuild)(OH_Drawing_Bitmap*, uint32_t, uint32_t, const OH_Drawing_BitmapFormat*);
typedef uint32_t (*pf_BitmapGetWidth)(OH_Drawing_Bitmap*);
typedef uint32_t (*pf_BitmapGetHeight)(OH_Drawing_Bitmap*);
typedef void* (*pf_BitmapGetPixels)(OH_Drawing_Bitmap*);

/* Canvas */
typedef OH_Drawing_Canvas* (*pf_CanvasCreate)(void);
typedef void (*pf_CanvasDestroy)(OH_Drawing_Canvas*);
typedef void (*pf_CanvasBind)(OH_Drawing_Canvas*, OH_Drawing_Bitmap*);
typedef void (*pf_CanvasAttachPen)(OH_Drawing_Canvas*, const OH_Drawing_Pen*);
typedef void (*pf_CanvasDetachPen)(OH_Drawing_Canvas*);
typedef void (*pf_CanvasAttachBrush)(OH_Drawing_Canvas*, const OH_Drawing_Brush*);
typedef void (*pf_CanvasDetachBrush)(OH_Drawing_Canvas*);
typedef void (*pf_CanvasSave)(OH_Drawing_Canvas*);
typedef void (*pf_CanvasRestore)(OH_Drawing_Canvas*);
typedef void (*pf_CanvasDrawLine)(OH_Drawing_Canvas*, float, float, float, float);
typedef void (*pf_CanvasDrawPath)(OH_Drawing_Canvas*, const OH_Drawing_Path*);
typedef void (*pf_CanvasDrawBitmap)(OH_Drawing_Canvas*, const OH_Drawing_Bitmap*, float, float);
typedef void (*pf_CanvasDrawRect)(OH_Drawing_Canvas*, const OH_Drawing_Rect*);
typedef void (*pf_CanvasDrawCircle)(OH_Drawing_Canvas*, const OH_Drawing_Point*, float);
typedef void (*pf_CanvasDrawOval)(OH_Drawing_Canvas*, const OH_Drawing_Rect*);
typedef void (*pf_CanvasDrawArc)(OH_Drawing_Canvas*, const OH_Drawing_Rect*, float, float);
typedef void (*pf_CanvasDrawRoundRect)(OH_Drawing_Canvas*, const OH_Drawing_RoundRect*);
typedef void (*pf_CanvasDrawTextBlob)(OH_Drawing_Canvas*, const OH_Drawing_TextBlob*, float, float);
typedef void (*pf_CanvasClipRect)(OH_Drawing_Canvas*, const OH_Drawing_Rect*, int, bool);
typedef void (*pf_CanvasClipPath)(OH_Drawing_Canvas*, const OH_Drawing_Path*, int, bool);
typedef void (*pf_CanvasRotate)(OH_Drawing_Canvas*, float, float, float);
typedef void (*pf_CanvasTranslate)(OH_Drawing_Canvas*, float, float);
typedef void (*pf_CanvasScale)(OH_Drawing_Canvas*, float, float);
typedef void (*pf_CanvasClear)(OH_Drawing_Canvas*, uint32_t);
typedef void (*pf_CanvasConcatMatrix)(OH_Drawing_Canvas*, const OH_Drawing_Matrix*);

/* Pen */
typedef OH_Drawing_Pen* (*pf_PenCreate)(void);
typedef void (*pf_PenDestroy)(OH_Drawing_Pen*);
typedef void (*pf_PenSetColor)(OH_Drawing_Pen*, uint32_t);
typedef void (*pf_PenSetWidth)(OH_Drawing_Pen*, float);
typedef void (*pf_PenSetAntiAlias)(OH_Drawing_Pen*, bool);
typedef void (*pf_PenSetCap)(OH_Drawing_Pen*, int);
typedef void (*pf_PenSetJoin)(OH_Drawing_Pen*, int);

/* Brush */
typedef OH_Drawing_Brush* (*pf_BrushCreate)(void);
typedef void (*pf_BrushDestroy)(OH_Drawing_Brush*);
typedef void (*pf_BrushSetColor)(OH_Drawing_Brush*, uint32_t);

/* Path */
typedef OH_Drawing_Path* (*pf_PathCreate)(void);
typedef void (*pf_PathDestroy)(OH_Drawing_Path*);
typedef void (*pf_PathMoveTo)(OH_Drawing_Path*, float, float);
typedef void (*pf_PathLineTo)(OH_Drawing_Path*, float, float);
typedef void (*pf_PathQuadTo)(OH_Drawing_Path*, float, float, float, float);
typedef void (*pf_PathCubicTo)(OH_Drawing_Path*, float, float, float, float, float, float);
typedef void (*pf_PathClose)(OH_Drawing_Path*);
typedef void (*pf_PathReset)(OH_Drawing_Path*);
typedef void (*pf_PathAddRect)(OH_Drawing_Path*, float, float, float, float, int);
typedef void (*pf_PathAddCircle)(OH_Drawing_Path*, float, float, float, int);

/* Font */
typedef OH_Drawing_Font* (*pf_FontCreate)(void);
typedef void (*pf_FontDestroy)(OH_Drawing_Font*);
typedef void (*pf_FontSetTextSize)(OH_Drawing_Font*, float);

/* Rect / RoundRect / Point / Matrix */
typedef OH_Drawing_Rect* (*pf_RectCreate)(float, float, float, float);
typedef void (*pf_RectDestroy)(OH_Drawing_Rect*);
typedef OH_Drawing_RoundRect* (*pf_RoundRectCreate)(const OH_Drawing_Rect*, float, float);
typedef void (*pf_RoundRectDestroy)(OH_Drawing_RoundRect*);
typedef OH_Drawing_Point* (*pf_PointCreate)(float, float);
typedef void (*pf_PointDestroy)(OH_Drawing_Point*);
typedef OH_Drawing_Matrix* (*pf_MatrixCreate)(void);
typedef void (*pf_MatrixSetMatrix)(OH_Drawing_Matrix*, float, float, float, float, float, float, float, float, float);
typedef void (*pf_MatrixDestroy)(OH_Drawing_Matrix*);

/* TextBlob */
typedef OH_Drawing_TextBlob* (*pf_TextBlobCreateFromString)(const char*, const OH_Drawing_Font*, int);
typedef void (*pf_TextBlobDestroy)(OH_Drawing_TextBlob*);

/* =====================================================================
 * Global function pointer table, loaded once via dlopen/dlsym
 * ===================================================================== */

static struct {
    void* lib;
    int loaded;

    /* Bitmap */
    pf_BitmapCreate BitmapCreate;
    pf_BitmapDestroy BitmapDestroy;
    pf_BitmapBuild BitmapBuild;
    pf_BitmapGetWidth BitmapGetWidth;
    pf_BitmapGetHeight BitmapGetHeight;
    pf_BitmapGetPixels BitmapGetPixels;

    /* Canvas */
    pf_CanvasCreate CanvasCreate;
    pf_CanvasDestroy CanvasDestroy;
    pf_CanvasBind CanvasBind;
    pf_CanvasAttachPen CanvasAttachPen;
    pf_CanvasDetachPen CanvasDetachPen;
    pf_CanvasAttachBrush CanvasAttachBrush;
    pf_CanvasDetachBrush CanvasDetachBrush;
    pf_CanvasSave CanvasSave;
    pf_CanvasRestore CanvasRestore;
    pf_CanvasDrawLine CanvasDrawLine;
    pf_CanvasDrawPath CanvasDrawPath;
    pf_CanvasDrawBitmap CanvasDrawBitmap;
    pf_CanvasDrawRect CanvasDrawRect;
    pf_CanvasDrawCircle CanvasDrawCircle;
    pf_CanvasDrawOval CanvasDrawOval;
    pf_CanvasDrawArc CanvasDrawArc;
    pf_CanvasDrawRoundRect CanvasDrawRoundRect;
    pf_CanvasDrawTextBlob CanvasDrawTextBlob;
    pf_CanvasClipRect CanvasClipRect;
    pf_CanvasClipPath CanvasClipPath;
    pf_CanvasRotate CanvasRotate;
    pf_CanvasTranslate CanvasTranslate;
    pf_CanvasScale CanvasScale;
    pf_CanvasClear CanvasClear;
    pf_CanvasConcatMatrix CanvasConcatMatrix;

    /* Pen */
    pf_PenCreate PenCreate;
    pf_PenDestroy PenDestroy;
    pf_PenSetColor PenSetColor;
    pf_PenSetWidth PenSetWidth;
    pf_PenSetAntiAlias PenSetAntiAlias;
    pf_PenSetCap PenSetCap;
    pf_PenSetJoin PenSetJoin;

    /* Brush */
    pf_BrushCreate BrushCreate;
    pf_BrushDestroy BrushDestroy;
    pf_BrushSetColor BrushSetColor;

    /* Path */
    pf_PathCreate PathCreate;
    pf_PathDestroy PathDestroy;
    pf_PathMoveTo PathMoveTo;
    pf_PathLineTo PathLineTo;
    pf_PathQuadTo PathQuadTo;
    pf_PathCubicTo PathCubicTo;
    pf_PathClose PathClose;
    pf_PathReset PathReset;
    pf_PathAddRect PathAddRect;
    pf_PathAddCircle PathAddCircle;

    /* Font */
    pf_FontCreate FontCreate;
    pf_FontDestroy FontDestroy;
    pf_FontSetTextSize FontSetTextSize;

    /* Rect / RoundRect / Point / Matrix */
    pf_RectCreate RectCreate;
    pf_RectDestroy RectDestroy;
    pf_RoundRectCreate RoundRectCreate;
    pf_RoundRectDestroy RoundRectDestroy;
    pf_PointCreate PointCreate;
    pf_PointDestroy PointDestroy;
    pf_MatrixCreate MatrixCreate;
    pf_MatrixSetMatrix MatrixSetMatrix;
    pf_MatrixDestroy MatrixDestroy;

    /* TextBlob */
    pf_TextBlobCreateFromString TextBlobCreateFromString;
    pf_TextBlobDestroy TextBlobDestroy;
} g_draw = {0};

#define LOADSYM(handle, field, name) \
    g_draw.field = (pf_##field)dlsym(handle, "OH_Drawing_" name)

static int ensureDrawingLib(void) {
    if (g_draw.loaded) return g_draw.lib ? 0 : -1;
    g_draw.loaded = 1;

    const char* paths[] = {
        "libnative_drawing.so",
        "/system/lib/libnative_drawing.so",
        "lib2d_graphics.z.so",
        "/system/lib/lib2d_graphics.z.so",
        NULL
    };
    for (int i = 0; paths[i]; i++) {
        g_draw.lib = dlopen(paths[i], RTLD_NOW);
        if (g_draw.lib) {
            fprintf(stderr, "[OHBridge] Loaded drawing lib: %s\n", paths[i]);
            break;
        }
    }
    if (!g_draw.lib) {
        fprintf(stderr, "[OHBridge] WARN: Could not load drawing library: %s\n", dlerror());
        return -1;
    }

    void *h = g_draw.lib;

    /* Bitmap */
    LOADSYM(h, BitmapCreate, "BitmapCreate");
    LOADSYM(h, BitmapDestroy, "BitmapDestroy");
    LOADSYM(h, BitmapBuild, "BitmapBuild");
    LOADSYM(h, BitmapGetWidth, "BitmapGetWidth");
    LOADSYM(h, BitmapGetHeight, "BitmapGetHeight");
    LOADSYM(h, BitmapGetPixels, "BitmapGetPixels");

    /* Canvas */
    LOADSYM(h, CanvasCreate, "CanvasCreate");
    LOADSYM(h, CanvasDestroy, "CanvasDestroy");
    LOADSYM(h, CanvasBind, "CanvasBind");
    LOADSYM(h, CanvasAttachPen, "CanvasAttachPen");
    LOADSYM(h, CanvasDetachPen, "CanvasDetachPen");
    LOADSYM(h, CanvasAttachBrush, "CanvasAttachBrush");
    LOADSYM(h, CanvasDetachBrush, "CanvasDetachBrush");
    LOADSYM(h, CanvasSave, "CanvasSave");
    LOADSYM(h, CanvasRestore, "CanvasRestore");
    LOADSYM(h, CanvasDrawLine, "CanvasDrawLine");
    LOADSYM(h, CanvasDrawPath, "CanvasDrawPath");
    LOADSYM(h, CanvasDrawBitmap, "CanvasDrawBitmap");
    LOADSYM(h, CanvasDrawRect, "CanvasDrawRect");
    LOADSYM(h, CanvasDrawCircle, "CanvasDrawCircle");
    LOADSYM(h, CanvasDrawOval, "CanvasDrawOval");
    LOADSYM(h, CanvasDrawArc, "CanvasDrawArc");
    LOADSYM(h, CanvasDrawRoundRect, "CanvasDrawRoundRect");
    LOADSYM(h, CanvasDrawTextBlob, "CanvasDrawTextBlob");
    LOADSYM(h, CanvasClipRect, "CanvasClipRect");
    LOADSYM(h, CanvasClipPath, "CanvasClipPath");
    LOADSYM(h, CanvasRotate, "CanvasRotate");
    LOADSYM(h, CanvasTranslate, "CanvasTranslate");
    LOADSYM(h, CanvasScale, "CanvasScale");
    LOADSYM(h, CanvasClear, "CanvasClear");
    LOADSYM(h, CanvasConcatMatrix, "CanvasConcatMatrix");

    /* Pen */
    LOADSYM(h, PenCreate, "PenCreate");
    LOADSYM(h, PenDestroy, "PenDestroy");
    LOADSYM(h, PenSetColor, "PenSetColor");
    LOADSYM(h, PenSetWidth, "PenSetWidth");
    LOADSYM(h, PenSetAntiAlias, "PenSetAntiAlias");
    LOADSYM(h, PenSetCap, "PenSetCap");
    LOADSYM(h, PenSetJoin, "PenSetJoin");

    /* Brush */
    LOADSYM(h, BrushCreate, "BrushCreate");
    LOADSYM(h, BrushDestroy, "BrushDestroy");
    LOADSYM(h, BrushSetColor, "BrushSetColor");

    /* Path */
    LOADSYM(h, PathCreate, "PathCreate");
    LOADSYM(h, PathDestroy, "PathDestroy");
    LOADSYM(h, PathMoveTo, "PathMoveTo");
    LOADSYM(h, PathLineTo, "PathLineTo");
    LOADSYM(h, PathQuadTo, "PathQuadTo");
    LOADSYM(h, PathCubicTo, "PathCubicTo");
    LOADSYM(h, PathClose, "PathClose");
    LOADSYM(h, PathReset, "PathReset");
    LOADSYM(h, PathAddRect, "PathAddRect");
    LOADSYM(h, PathAddCircle, "PathAddCircle");

    /* Font */
    LOADSYM(h, FontCreate, "FontCreate");
    LOADSYM(h, FontDestroy, "FontDestroy");
    /* Try both names: SDK header is FontSetTextSize, Rust FFI uses FontSetSize */
    g_draw.FontSetTextSize = (pf_FontSetTextSize)dlsym(h, "OH_Drawing_FontSetTextSize");
    if (!g_draw.FontSetTextSize)
        g_draw.FontSetTextSize = (pf_FontSetTextSize)dlsym(h, "OH_Drawing_FontSetSize");

    /* Rect / RoundRect / Point / Matrix */
    LOADSYM(h, RectCreate, "RectCreate");
    LOADSYM(h, RectDestroy, "RectDestroy");
    LOADSYM(h, RoundRectCreate, "RoundRectCreate");
    LOADSYM(h, RoundRectDestroy, "RoundRectDestroy");
    LOADSYM(h, PointCreate, "PointCreate");
    LOADSYM(h, PointDestroy, "PointDestroy");
    LOADSYM(h, MatrixCreate, "MatrixCreate");
    LOADSYM(h, MatrixSetMatrix, "MatrixSetMatrix");
    LOADSYM(h, MatrixDestroy, "MatrixDestroy");

    /* TextBlob */
    LOADSYM(h, TextBlobCreateFromString, "TextBlobCreateFromString");
    LOADSYM(h, TextBlobDestroy, "TextBlobDestroy");

    return 0;
}

/* =====================================================================
 * Helpers: attach/detach pen and brush to canvas before/after draw ops
 * ===================================================================== */

static void attach_pen_brush(OH_Drawing_Canvas *c, jlong pen, jlong brush) {
    if (pen && g_draw.CanvasAttachPen)
        g_draw.CanvasAttachPen(c, (const OH_Drawing_Pen*)(uintptr_t)pen);
    if (brush && g_draw.CanvasAttachBrush)
        g_draw.CanvasAttachBrush(c, (const OH_Drawing_Brush*)(uintptr_t)brush);
}

static void detach_pen_brush(OH_Drawing_Canvas *c, jlong pen, jlong brush) {
    if (pen && g_draw.CanvasDetachPen) g_draw.CanvasDetachPen(c);
    if (brush && g_draw.CanvasDetachBrush) g_draw.CanvasDetachBrush(c);
}

/* =====================================================================
 * ArkUI Node API (dlopen libace_ndk.z.so) -- same as arkui_bridge.cpp
 * ===================================================================== */

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
static volatile int g_apiInitDone = 0;
static void* g_aceLib = NULL;

static int initArkUIAPI(void) {
    if (g_apiInitDone) return g_api ? 0 : -1;
    g_apiInitDone = 1;
    const char* paths[] = { "libace_ndk.z.so", "/system/lib/libace_ndk.z.so", NULL };
    for (int i = 0; paths[i]; i++) {
        g_aceLib = dlopen(paths[i], RTLD_NOW);
        if (g_aceLib) break;
    }
    if (!g_aceLib) return -1;
    GetNativeAPI_fn getAPI = (GetNativeAPI_fn)dlsym(g_aceLib, "OH_ArkUI_GetNativeAPI");
    if (!getAPI) { dlclose(g_aceLib); g_aceLib = NULL; return -1; }
    ArkUI_AnyNativeAPI* any = getAPI(ARKUI_NATIVE_NODE, 1);
    if (!any || any->version < 1) { dlclose(g_aceLib); g_aceLib = NULL; return -1; }
    g_api = (ArkUI_NativeNodeAPI_1*)any;
    return 0;
}

/* =====================================================================
 * JNI implementations -- ArkUI Node API
 * ===================================================================== */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeCreate(JNIEnv *env, jclass cls, jint type) {
    (void)env; (void)cls;
    if (initArkUIAPI() < 0 || !g_api->createNode) return 0;
    return (jlong)(uintptr_t)g_api->createNode(type);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeDispose(JNIEnv *env, jclass cls, jlong h) {
    (void)env; (void)cls;
    if (!g_api || !g_api->disposeNode || !h) return;
    g_api->disposeNode((ArkUI_NodeHandle)(uintptr_t)h);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeAddChild(JNIEnv *env, jclass cls, jlong p, jlong c) {
    (void)env; (void)cls;
    if (!g_api || !g_api->addChild || !p || !c) return;
    g_api->addChild((ArkUI_NodeHandle)(uintptr_t)p, (ArkUI_NodeHandle)(uintptr_t)c);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeRemoveChild(JNIEnv *env, jclass cls, jlong p, jlong c) {
    (void)env; (void)cls;
    if (!g_api || !g_api->removeChild || !p || !c) return;
    g_api->removeChild((ArkUI_NodeHandle)(uintptr_t)p, (ArkUI_NodeHandle)(uintptr_t)c);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeInsertChildAt(JNIEnv *env, jclass cls, jlong p, jlong c, jint pos) {
    (void)env; (void)cls;
    if (!g_api || !g_api->insertChildAt || !p || !c) return;
    g_api->insertChildAt((ArkUI_NodeHandle)(uintptr_t)p, (ArkUI_NodeHandle)(uintptr_t)c, pos);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrString(JNIEnv *env, jclass cls, jlong h, jint attr, jstring jval) {
    (void)cls;
    if (!g_api || !g_api->setAttribute || !h || !jval) return;
    const char* val = (*env)->GetStringUTFChars(env, jval, NULL);
    if (!val) return;
    ArkUI_AttributeItem item;
    memset(&item, 0, sizeof(item));
    item.string = val;
    g_api->setAttribute((ArkUI_NodeHandle)(uintptr_t)h, attr, &item);
    (*env)->ReleaseStringUTFChars(env, jval, val);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrInt(JNIEnv *env, jclass cls, jlong h, jint attr, jint val) {
    (void)env; (void)cls;
    if (!g_api || !g_api->setAttribute || !h) return;
    ArkUI_NumberValue nv; nv.i32 = val;
    ArkUI_AttributeItem item;
    memset(&item, 0, sizeof(item));
    item.value = &nv; item.size = 1;
    g_api->setAttribute((ArkUI_NodeHandle)(uintptr_t)h, attr, &item);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrFloat(JNIEnv *env, jclass cls, jlong h, jint attr, jfloat val) {
    (void)env; (void)cls;
    if (!g_api || !g_api->setAttribute || !h) return;
    ArkUI_NumberValue nv; nv.f32 = val;
    ArkUI_AttributeItem item;
    memset(&item, 0, sizeof(item));
    item.value = &nv; item.size = 1;
    g_api->setAttribute((ArkUI_NodeHandle)(uintptr_t)h, attr, &item);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrColor(JNIEnv *env, jclass cls, jlong h, jint attr, jint argb) {
    (void)env; (void)cls;
    if (!g_api || !g_api->setAttribute || !h) return;
    ArkUI_NumberValue nv; nv.u32 = (uint32_t)argb;
    ArkUI_AttributeItem item;
    memset(&item, 0, sizeof(item));
    item.value = &nv; item.size = 1;
    g_api->setAttribute((ArkUI_NodeHandle)(uintptr_t)h, attr, &item);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeRegisterEvent(JNIEnv *env, jclass cls, jlong h, jint eventType, jint targetId) {
    (void)env; (void)cls;
    if (!g_api || !g_api->registerNodeEvent || !h) return;
    g_api->registerNodeEvent((ArkUI_NodeHandle)(uintptr_t)h, eventType, targetId);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeUnregisterEvent(JNIEnv *env, jclass cls, jlong h, jint eventType) {
    (void)env; (void)cls;
    if (!g_api || !g_api->unregisterNodeEvent || !h) return;
    g_api->unregisterNodeEvent((ArkUI_NodeHandle)(uintptr_t)h, eventType);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_nodeMarkDirty(JNIEnv *env, jclass cls, jlong h, jint flags) {
    (void)env; (void)cls; (void)h; (void)flags;
}

/* =====================================================================
 * JNI implementations -- Bitmap (forwarded to OH_Drawing)
 * ===================================================================== */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapCreate(JNIEnv *env, jclass cls, jint w, jint h, jint fmt) {
    (void)env; (void)cls; (void)fmt;
    if (ensureDrawingLib() < 0 || !g_draw.BitmapCreate) return 0;
    OH_Drawing_Bitmap *bmp = g_draw.BitmapCreate();
    if (!bmp) return 0;
    if (g_draw.BitmapBuild) {
        OH_Drawing_BitmapFormat bfmt;
        bfmt.colorFormat = OHD_COLOR_FORMAT_RGBA_8888;
        bfmt.alphaFormat = OHD_ALPHA_FORMAT_PREMUL;
        g_draw.BitmapBuild(bmp, (uint32_t)w, (uint32_t)h, &bfmt);
    }
    return (jlong)(uintptr_t)bmp;
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapDestroy(JNIEnv *env, jclass cls, jlong bmp) {
    (void)env; (void)cls;
    if (!bmp || !g_draw.BitmapDestroy) return;
    g_draw.BitmapDestroy((OH_Drawing_Bitmap*)(uintptr_t)bmp);
}

JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapGetWidth(JNIEnv *env, jclass cls, jlong bmp) {
    (void)env; (void)cls;
    if (!bmp || !g_draw.BitmapGetWidth) return 0;
    return (jint)g_draw.BitmapGetWidth((OH_Drawing_Bitmap*)(uintptr_t)bmp);
}

JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapGetHeight(JNIEnv *env, jclass cls, jlong bmp) {
    (void)env; (void)cls;
    if (!bmp || !g_draw.BitmapGetHeight) return 0;
    return (jint)g_draw.BitmapGetHeight((OH_Drawing_Bitmap*)(uintptr_t)bmp);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapSetPixel(JNIEnv *env, jclass cls, jlong bmp, jint x, jint y, jint argb) {
    (void)env; (void)cls;
    if (!bmp || !g_draw.BitmapGetPixels || !g_draw.BitmapGetWidth) return;
    OH_Drawing_Bitmap *b = (OH_Drawing_Bitmap*)(uintptr_t)bmp;
    uint8_t *pixels = (uint8_t*)g_draw.BitmapGetPixels(b);
    if (!pixels) return;
    int w = (int)g_draw.BitmapGetWidth(b);
    if (x < 0 || y < 0 || x >= w) return;
    /* Convert Android ARGB_8888 (0xAARRGGBB) to RGBA_8888 (R,G,B,A bytes) */
    uint8_t a = (uint8_t)((argb >> 24) & 0xFF);
    uint8_t r = (uint8_t)((argb >> 16) & 0xFF);
    uint8_t g = (uint8_t)((argb >> 8) & 0xFF);
    uint8_t bl = (uint8_t)(argb & 0xFF);
    int offset = (y * w + x) * 4;
    pixels[offset + 0] = r;
    pixels[offset + 1] = g;
    pixels[offset + 2] = bl;
    pixels[offset + 3] = a;
}

JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_bitmapGetPixel(JNIEnv *env, jclass cls, jlong bmp, jint x, jint y) {
    (void)env; (void)cls;
    if (!bmp || !g_draw.BitmapGetPixels || !g_draw.BitmapGetWidth) return 0;
    OH_Drawing_Bitmap *b = (OH_Drawing_Bitmap*)(uintptr_t)bmp;
    const uint8_t *pixels = (const uint8_t*)g_draw.BitmapGetPixels(b);
    if (!pixels) return 0;
    int w = (int)g_draw.BitmapGetWidth(b);
    if (x < 0 || y < 0 || x >= w) return 0;
    int offset = (y * w + x) * 4;
    int r = pixels[offset + 0];
    int g = pixels[offset + 1];
    int bl = pixels[offset + 2];
    int a = pixels[offset + 3];
    /* Convert RGBA -> ARGB */
    return (a << 24) | (r << 16) | (g << 8) | bl;
}

/* =====================================================================
 * JNI implementations -- Canvas
 * ===================================================================== */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasCreate(JNIEnv *env, jclass cls, jlong bmpH) {
    (void)env; (void)cls;
    if (ensureDrawingLib() < 0 || !g_draw.CanvasCreate) return 0;
    OH_Drawing_Canvas *canvas = g_draw.CanvasCreate();
    if (!canvas) return 0;
    if (bmpH && g_draw.CanvasBind)
        g_draw.CanvasBind(canvas, (OH_Drawing_Bitmap*)(uintptr_t)bmpH);
    return (jlong)(uintptr_t)canvas;
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDestroy(JNIEnv *env, jclass cls, jlong h) {
    (void)env; (void)cls;
    if (!h || !g_draw.CanvasDestroy) return;
    g_draw.CanvasDestroy((OH_Drawing_Canvas*)(uintptr_t)h);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawColor(JNIEnv *env, jclass cls, jlong h, jint argb) {
    (void)env; (void)cls;
    if (!h || !g_draw.CanvasClear) return;
    g_draw.CanvasClear((OH_Drawing_Canvas*)(uintptr_t)h, (uint32_t)argb);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawRect(
    JNIEnv *env, jclass cls, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b, jlong pen, jlong brush)
{
    (void)env; (void)cls;
    if (!ch || !g_draw.CanvasDrawRect || !g_draw.RectCreate) return;
    OH_Drawing_Canvas *c = (OH_Drawing_Canvas*)(uintptr_t)ch;
    attach_pen_brush(c, pen, brush);
    OH_Drawing_Rect *rect = g_draw.RectCreate(l, t, r, b);
    g_draw.CanvasDrawRect(c, rect);
    if (g_draw.RectDestroy) g_draw.RectDestroy(rect);
    detach_pen_brush(c, pen, brush);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawCircle(
    JNIEnv *env, jclass cls, jlong ch, jfloat cx, jfloat cy, jfloat radius, jlong pen, jlong brush)
{
    (void)env; (void)cls;
    if (!ch || !g_draw.CanvasDrawCircle || !g_draw.PointCreate) return;
    OH_Drawing_Canvas *c = (OH_Drawing_Canvas*)(uintptr_t)ch;
    attach_pen_brush(c, pen, brush);
    OH_Drawing_Point *pt = g_draw.PointCreate(cx, cy);
    g_draw.CanvasDrawCircle(c, pt, radius);
    if (g_draw.PointDestroy) g_draw.PointDestroy(pt);
    detach_pen_brush(c, pen, brush);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawLine(
    JNIEnv *env, jclass cls, jlong ch, jfloat x1, jfloat y1, jfloat x2, jfloat y2, jlong pen)
{
    (void)env; (void)cls;
    if (!ch || !g_draw.CanvasDrawLine) return;
    OH_Drawing_Canvas *c = (OH_Drawing_Canvas*)(uintptr_t)ch;
    if (pen && g_draw.CanvasAttachPen)
        g_draw.CanvasAttachPen(c, (const OH_Drawing_Pen*)(uintptr_t)pen);
    g_draw.CanvasDrawLine(c, x1, y1, x2, y2);
    if (pen && g_draw.CanvasDetachPen)
        g_draw.CanvasDetachPen(c);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawPath(
    JNIEnv *env, jclass cls, jlong ch, jlong path, jlong pen, jlong brush)
{
    (void)env; (void)cls;
    if (!ch || !path || !g_draw.CanvasDrawPath) return;
    OH_Drawing_Canvas *c = (OH_Drawing_Canvas*)(uintptr_t)ch;
    attach_pen_brush(c, pen, brush);
    g_draw.CanvasDrawPath(c, (const OH_Drawing_Path*)(uintptr_t)path);
    detach_pen_brush(c, pen, brush);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawBitmap(
    JNIEnv *env, jclass cls, jlong ch, jlong bmpH, jfloat x, jfloat y)
{
    (void)env; (void)cls;
    if (!ch || !bmpH || !g_draw.CanvasDrawBitmap) return;
    g_draw.CanvasDrawBitmap(
        (OH_Drawing_Canvas*)(uintptr_t)ch,
        (const OH_Drawing_Bitmap*)(uintptr_t)bmpH, x, y);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawText(
    JNIEnv *env, jclass cls, jlong ch, jstring jtext, jfloat x, jfloat y,
    jlong fontH, jlong pen, jlong brush)
{
    (void)cls;
    if (!ch || !fontH || !jtext) return;
    if (!g_draw.TextBlobCreateFromString || !g_draw.CanvasDrawTextBlob) return;
    const char *text = (*env)->GetStringUTFChars(env, jtext, NULL);
    if (!text) return;
    OH_Drawing_Canvas *c = (OH_Drawing_Canvas*)(uintptr_t)ch;
    attach_pen_brush(c, pen, brush);
    /* encoding 0 = UTF8 */
    OH_Drawing_TextBlob *blob = g_draw.TextBlobCreateFromString(
        text, (const OH_Drawing_Font*)(uintptr_t)fontH, 0);
    if (blob) {
        g_draw.CanvasDrawTextBlob(c, blob, x, y);
        if (g_draw.TextBlobDestroy) g_draw.TextBlobDestroy(blob);
    }
    detach_pen_brush(c, pen, brush);
    (*env)->ReleaseStringUTFChars(env, jtext, text);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasSave(JNIEnv *env, jclass cls, jlong ch) {
    (void)env; (void)cls;
    if (!ch || !g_draw.CanvasSave) return;
    g_draw.CanvasSave((OH_Drawing_Canvas*)(uintptr_t)ch);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasRestore(JNIEnv *env, jclass cls, jlong ch) {
    (void)env; (void)cls;
    if (!ch || !g_draw.CanvasRestore) return;
    g_draw.CanvasRestore((OH_Drawing_Canvas*)(uintptr_t)ch);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasTranslate(JNIEnv *env, jclass cls, jlong ch, jfloat dx, jfloat dy) {
    (void)env; (void)cls;
    if (!ch || !g_draw.CanvasTranslate) return;
    g_draw.CanvasTranslate((OH_Drawing_Canvas*)(uintptr_t)ch, dx, dy);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasScale(JNIEnv *env, jclass cls, jlong ch, jfloat sx, jfloat sy) {
    (void)env; (void)cls;
    if (!ch || !g_draw.CanvasScale) return;
    g_draw.CanvasScale((OH_Drawing_Canvas*)(uintptr_t)ch, sx, sy);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasRotate(JNIEnv *env, jclass cls, jlong ch, jfloat deg, jfloat px, jfloat py) {
    (void)env; (void)cls;
    if (!ch || !g_draw.CanvasRotate) return;
    g_draw.CanvasRotate((OH_Drawing_Canvas*)(uintptr_t)ch, deg, px, py);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasClipRect(
    JNIEnv *env, jclass cls, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b)
{
    (void)env; (void)cls;
    if (!ch || !g_draw.CanvasClipRect || !g_draw.RectCreate) return;
    OH_Drawing_Rect *rect = g_draw.RectCreate(l, t, r, b);
    /* clipOp=1 (INTERSECT), doAntiAlias=true */
    g_draw.CanvasClipRect((OH_Drawing_Canvas*)(uintptr_t)ch, rect, 1, true);
    if (g_draw.RectDestroy) g_draw.RectDestroy(rect);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasClipPath(JNIEnv *env, jclass cls, jlong ch, jlong path) {
    (void)env; (void)cls;
    if (!ch || !path || !g_draw.CanvasClipPath) return;
    /* clipOp=1 (INTERSECT), doAntiAlias=true */
    g_draw.CanvasClipPath((OH_Drawing_Canvas*)(uintptr_t)ch,
        (const OH_Drawing_Path*)(uintptr_t)path, 1, true);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawArc(
    JNIEnv *env, jclass cls, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b,
    jfloat startAngle, jfloat sweepAngle, jboolean useCenter, jlong pen, jlong brush)
{
    (void)env; (void)cls; (void)useCenter;
    if (!ch || !g_draw.CanvasDrawArc || !g_draw.RectCreate) return;
    OH_Drawing_Canvas *c = (OH_Drawing_Canvas*)(uintptr_t)ch;
    attach_pen_brush(c, pen, brush);
    OH_Drawing_Rect *rect = g_draw.RectCreate(l, t, r, b);
    g_draw.CanvasDrawArc(c, rect, startAngle, sweepAngle);
    if (g_draw.RectDestroy) g_draw.RectDestroy(rect);
    detach_pen_brush(c, pen, brush);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawOval(
    JNIEnv *env, jclass cls, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b, jlong pen, jlong brush)
{
    (void)env; (void)cls;
    if (!ch || !g_draw.CanvasDrawOval || !g_draw.RectCreate) return;
    OH_Drawing_Canvas *c = (OH_Drawing_Canvas*)(uintptr_t)ch;
    attach_pen_brush(c, pen, brush);
    OH_Drawing_Rect *rect = g_draw.RectCreate(l, t, r, b);
    g_draw.CanvasDrawOval(c, rect);
    if (g_draw.RectDestroy) g_draw.RectDestroy(rect);
    detach_pen_brush(c, pen, brush);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawRoundRect(
    JNIEnv *env, jclass cls, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b,
    jfloat rx, jfloat ry, jlong pen, jlong brush)
{
    (void)env; (void)cls;
    if (!ch || !g_draw.CanvasDrawRoundRect || !g_draw.RectCreate || !g_draw.RoundRectCreate) return;
    OH_Drawing_Canvas *c = (OH_Drawing_Canvas*)(uintptr_t)ch;
    attach_pen_brush(c, pen, brush);
    OH_Drawing_Rect *rect = g_draw.RectCreate(l, t, r, b);
    OH_Drawing_RoundRect *rr = g_draw.RoundRectCreate(rect, rx, ry);
    g_draw.CanvasDrawRoundRect(c, rr);
    if (g_draw.RoundRectDestroy) g_draw.RoundRectDestroy(rr);
    if (g_draw.RectDestroy) g_draw.RectDestroy(rect);
    detach_pen_brush(c, pen, brush);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasConcat(JNIEnv *env, jclass cls, jlong ch, jfloatArray matrix9) {
    (void)cls;
    if (!ch || !g_draw.CanvasConcatMatrix || !g_draw.MatrixCreate) return;
    jfloat vals[9];
    (*env)->GetFloatArrayRegion(env, matrix9, 0, 9, vals);
    OH_Drawing_Matrix *mat = g_draw.MatrixCreate();
    if (g_draw.MatrixSetMatrix)
        g_draw.MatrixSetMatrix(mat,
            vals[0], vals[1], vals[2],
            vals[3], vals[4], vals[5],
            vals[6], vals[7], vals[8]);
    g_draw.CanvasConcatMatrix((OH_Drawing_Canvas*)(uintptr_t)ch, mat);
    if (g_draw.MatrixDestroy) g_draw.MatrixDestroy(mat);
}

/* =====================================================================
 * JNI implementations -- Pen
 * ===================================================================== */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_penCreate(JNIEnv *env, jclass cls) {
    (void)env; (void)cls;
    if (ensureDrawingLib() < 0 || !g_draw.PenCreate) return 0;
    return (jlong)(uintptr_t)g_draw.PenCreate();
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_penDestroy(JNIEnv *env, jclass cls, jlong h) {
    (void)env; (void)cls;
    if (!h || !g_draw.PenDestroy) return;
    g_draw.PenDestroy((OH_Drawing_Pen*)(uintptr_t)h);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_penSetColor(JNIEnv *env, jclass cls, jlong h, jint argb) {
    (void)env; (void)cls;
    if (!h || !g_draw.PenSetColor) return;
    g_draw.PenSetColor((OH_Drawing_Pen*)(uintptr_t)h, (uint32_t)argb);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_penSetStrokeWidth(JNIEnv *env, jclass cls, jlong h, jfloat w) {
    (void)env; (void)cls;
    if (!h || !g_draw.PenSetWidth) return;
    g_draw.PenSetWidth((OH_Drawing_Pen*)(uintptr_t)h, w);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_penSetAntiAlias(JNIEnv *env, jclass cls, jlong h, jboolean aa) {
    (void)env; (void)cls;
    if (!h || !g_draw.PenSetAntiAlias) return;
    g_draw.PenSetAntiAlias((OH_Drawing_Pen*)(uintptr_t)h, aa != 0);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_penSetCap(JNIEnv *env, jclass cls, jlong h, jint cap) {
    (void)env; (void)cls;
    if (!h || !g_draw.PenSetCap) return;
    g_draw.PenSetCap((OH_Drawing_Pen*)(uintptr_t)h, cap);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_penSetJoin(JNIEnv *env, jclass cls, jlong h, jint join) {
    (void)env; (void)cls;
    if (!h || !g_draw.PenSetJoin) return;
    g_draw.PenSetJoin((OH_Drawing_Pen*)(uintptr_t)h, join);
}

/* =====================================================================
 * JNI implementations -- Brush
 * ===================================================================== */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_brushCreate(JNIEnv *env, jclass cls) {
    (void)env; (void)cls;
    if (ensureDrawingLib() < 0 || !g_draw.BrushCreate) return 0;
    return (jlong)(uintptr_t)g_draw.BrushCreate();
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_brushDestroy(JNIEnv *env, jclass cls, jlong h) {
    (void)env; (void)cls;
    if (!h || !g_draw.BrushDestroy) return;
    g_draw.BrushDestroy((OH_Drawing_Brush*)(uintptr_t)h);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_brushSetColor(JNIEnv *env, jclass cls, jlong h, jint argb) {
    (void)env; (void)cls;
    if (!h || !g_draw.BrushSetColor) return;
    g_draw.BrushSetColor((OH_Drawing_Brush*)(uintptr_t)h, (uint32_t)argb);
}

/* =====================================================================
 * JNI implementations -- Font
 * ===================================================================== */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_fontCreate(JNIEnv *env, jclass cls) {
    (void)env; (void)cls;
    if (ensureDrawingLib() < 0 || !g_draw.FontCreate) return 0;
    return (jlong)(uintptr_t)g_draw.FontCreate();
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_fontDestroy(JNIEnv *env, jclass cls, jlong h) {
    (void)env; (void)cls;
    if (!h || !g_draw.FontDestroy) return;
    g_draw.FontDestroy((OH_Drawing_Font*)(uintptr_t)h);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_fontSetSize(JNIEnv *env, jclass cls, jlong h, jfloat sz) {
    (void)env; (void)cls;
    if (!h || !g_draw.FontSetTextSize) return;
    g_draw.FontSetTextSize((OH_Drawing_Font*)(uintptr_t)h, sz);
}

JNIEXPORT jfloat JNICALL Java_com_ohos_shim_bridge_OHBridge_fontMeasureText(JNIEnv *env, jclass cls, jlong h, jstring jtext) {
    (void)cls;
    /* OH_Drawing does not have a simple text measure API in the public SDK.
     * Return 0.0f for now -- callers should use TextBlob bounds if precise measurement is needed. */
    (void)env; (void)h; (void)jtext;
    return 0.0f;
}

/* =====================================================================
 * JNI implementations -- Path
 * ===================================================================== */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_pathCreate(JNIEnv *env, jclass cls) {
    (void)env; (void)cls;
    if (ensureDrawingLib() < 0 || !g_draw.PathCreate) return 0;
    return (jlong)(uintptr_t)g_draw.PathCreate();
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathDestroy(JNIEnv *env, jclass cls, jlong h) {
    (void)env; (void)cls;
    if (!h || !g_draw.PathDestroy) return;
    g_draw.PathDestroy((OH_Drawing_Path*)(uintptr_t)h);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathMoveTo(JNIEnv *env, jclass cls, jlong h, jfloat x, jfloat y) {
    (void)env; (void)cls;
    if (!h || !g_draw.PathMoveTo) return;
    g_draw.PathMoveTo((OH_Drawing_Path*)(uintptr_t)h, x, y);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathLineTo(JNIEnv *env, jclass cls, jlong h, jfloat x, jfloat y) {
    (void)env; (void)cls;
    if (!h || !g_draw.PathLineTo) return;
    g_draw.PathLineTo((OH_Drawing_Path*)(uintptr_t)h, x, y);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathQuadTo(JNIEnv *env, jclass cls, jlong h, jfloat x1, jfloat y1, jfloat x2, jfloat y2) {
    (void)env; (void)cls;
    if (!h || !g_draw.PathQuadTo) return;
    g_draw.PathQuadTo((OH_Drawing_Path*)(uintptr_t)h, x1, y1, x2, y2);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathCubicTo(JNIEnv *env, jclass cls, jlong h, jfloat x1, jfloat y1, jfloat x2, jfloat y2, jfloat x3, jfloat y3) {
    (void)env; (void)cls;
    if (!h || !g_draw.PathCubicTo) return;
    g_draw.PathCubicTo((OH_Drawing_Path*)(uintptr_t)h, x1, y1, x2, y2, x3, y3);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathClose(JNIEnv *env, jclass cls, jlong h) {
    (void)env; (void)cls;
    if (!h || !g_draw.PathClose) return;
    g_draw.PathClose((OH_Drawing_Path*)(uintptr_t)h);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathReset(JNIEnv *env, jclass cls, jlong h) {
    (void)env; (void)cls;
    if (!h || !g_draw.PathReset) return;
    g_draw.PathReset((OH_Drawing_Path*)(uintptr_t)h);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathAddRect(JNIEnv *env, jclass cls, jlong h, jfloat l, jfloat t, jfloat r, jfloat b, jint dir) {
    (void)env; (void)cls;
    if (!h || !g_draw.PathAddRect) return;
    g_draw.PathAddRect((OH_Drawing_Path*)(uintptr_t)h, l, t, r, b, dir);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_pathAddCircle(JNIEnv *env, jclass cls, jlong h, jfloat cx, jfloat cy, jfloat r, jint dir) {
    (void)env; (void)cls;
    if (!h || !g_draw.PathAddCircle) return;
    g_draw.PathAddCircle((OH_Drawing_Path*)(uintptr_t)h, cx, cy, r, dir);
}

/* =====================================================================
 * JNI implementations -- Surface stubs
 * ===================================================================== */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_surfaceCreate(JNIEnv *env, jclass cls, jlong a, jint b, jint c) {
    (void)env; (void)cls; (void)a; (void)b; (void)c; return 0;
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_surfaceDestroy(JNIEnv *env, jclass cls, jlong h) {
    (void)env; (void)cls; (void)h;
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_surfaceResize(JNIEnv *env, jclass cls, jlong h, jint w, jint ht) {
    (void)env; (void)cls; (void)h; (void)w; (void)ht;
}
JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_surfaceGetCanvas(JNIEnv *env, jclass cls, jlong h) {
    (void)env; (void)cls; (void)h; return 0;
}
JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_surfaceFlush(JNIEnv *env, jclass cls, jlong h) {
    (void)env; (void)cls; (void)h; return 0;
}

/* =====================================================================
 * JNI implementations -- Log
 * ===================================================================== */

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_logDebug(JNIEnv *env, jclass cls, jstring tag, jstring msg) {
    (void)cls;
    if (!tag || !msg) return;
    const char* t = (*env)->GetStringUTFChars(env, tag, NULL);
    const char* m = (*env)->GetStringUTFChars(env, msg, NULL);
    fprintf(stderr, "[D/%s] %s\n", t ? t : "", m ? m : "");
    if (m) (*env)->ReleaseStringUTFChars(env, msg, m);
    if (t) (*env)->ReleaseStringUTFChars(env, tag, t);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_logInfo(JNIEnv *env, jclass cls, jstring tag, jstring msg) {
    Java_com_ohos_shim_bridge_OHBridge_logDebug(env, cls, tag, msg);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_logWarn(JNIEnv *env, jclass cls, jstring tag, jstring msg) {
    Java_com_ohos_shim_bridge_OHBridge_logDebug(env, cls, tag, msg);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_logError(JNIEnv *env, jclass cls, jstring tag, jstring msg) {
    Java_com_ohos_shim_bridge_OHBridge_logDebug(env, cls, tag, msg);
}

/* =====================================================================
 * JNI Registration table (same layout as arkui_bridge.cpp)
 * ===================================================================== */

static int ohbridge_registerClass(JNIEnv* env, const char* className,
                                   JNINativeMethod* methods, int count) {
    jclass c = (*env)->FindClass(env, className);
    if (c == NULL) { (*env)->ExceptionClear(env); return 1; }
    int registered = 0;
    for (int i = 0; i < count; i++) {
        if ((*env)->RegisterNatives(env, c, &methods[i], 1) < 0) {
            (*env)->ExceptionClear(env);
        } else {
            registered++;
        }
    }
    fprintf(stderr, "[OHBridge] Registered %s (%d/%d methods)\n", className, registered, count);
    return 1;
}

#define OHBM(name, sig, fn) { (char*)name, (char*)sig, (void*)(fn) }

static JNINativeMethod gOHBridgeMethods[] = {
    /* Node API */
    OHBM("nodeCreate", "(I)J", Java_com_ohos_shim_bridge_OHBridge_nodeCreate),
    OHBM("nodeDispose", "(J)V", Java_com_ohos_shim_bridge_OHBridge_nodeDispose),
    OHBM("nodeAddChild", "(JJ)V", Java_com_ohos_shim_bridge_OHBridge_nodeAddChild),
    OHBM("nodeRemoveChild", "(JJ)V", Java_com_ohos_shim_bridge_OHBridge_nodeRemoveChild),
    OHBM("nodeInsertChildAt", "(JJI)V", Java_com_ohos_shim_bridge_OHBridge_nodeInsertChildAt),
    OHBM("nodeSetAttrString", "(JILjava/lang/String;)V", Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrString),
    OHBM("nodeSetAttrInt", "(JII)V", Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrInt),
    OHBM("nodeSetAttrFloat", "(JIF)V", Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrFloat),
    OHBM("nodeSetAttrColor", "(JII)V", Java_com_ohos_shim_bridge_OHBridge_nodeSetAttrColor),
    OHBM("nodeRegisterEvent", "(JII)V", Java_com_ohos_shim_bridge_OHBridge_nodeRegisterEvent),
    OHBM("nodeUnregisterEvent", "(JI)V", Java_com_ohos_shim_bridge_OHBridge_nodeUnregisterEvent),
    OHBM("nodeMarkDirty", "(JI)V", Java_com_ohos_shim_bridge_OHBridge_nodeMarkDirty),

    /* Bitmap */
    OHBM("bitmapCreate", "(III)J", Java_com_ohos_shim_bridge_OHBridge_bitmapCreate),
    OHBM("bitmapDestroy", "(J)V", Java_com_ohos_shim_bridge_OHBridge_bitmapDestroy),
    OHBM("bitmapGetWidth", "(J)I", Java_com_ohos_shim_bridge_OHBridge_bitmapGetWidth),
    OHBM("bitmapGetHeight", "(J)I", Java_com_ohos_shim_bridge_OHBridge_bitmapGetHeight),
    OHBM("bitmapSetPixel", "(JIII)V", Java_com_ohos_shim_bridge_OHBridge_bitmapSetPixel),
    OHBM("bitmapGetPixel", "(JII)I", Java_com_ohos_shim_bridge_OHBridge_bitmapGetPixel),

    /* Canvas */
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

    /* Pen */
    OHBM("penCreate", "()J", Java_com_ohos_shim_bridge_OHBridge_penCreate),
    OHBM("penDestroy", "(J)V", Java_com_ohos_shim_bridge_OHBridge_penDestroy),
    OHBM("penSetColor", "(JI)V", Java_com_ohos_shim_bridge_OHBridge_penSetColor),
    OHBM("penSetWidth", "(JF)V", Java_com_ohos_shim_bridge_OHBridge_penSetStrokeWidth),
    OHBM("penSetAntiAlias", "(JZ)V", Java_com_ohos_shim_bridge_OHBridge_penSetAntiAlias),
    OHBM("penSetCap", "(JI)V", Java_com_ohos_shim_bridge_OHBridge_penSetCap),
    OHBM("penSetJoin", "(JI)V", Java_com_ohos_shim_bridge_OHBridge_penSetJoin),

    /* Brush */
    OHBM("brushCreate", "()J", Java_com_ohos_shim_bridge_OHBridge_brushCreate),
    OHBM("brushDestroy", "(J)V", Java_com_ohos_shim_bridge_OHBridge_brushDestroy),
    OHBM("brushSetColor", "(JI)V", Java_com_ohos_shim_bridge_OHBridge_brushSetColor),

    /* Font */
    OHBM("fontCreate", "()J", Java_com_ohos_shim_bridge_OHBridge_fontCreate),
    OHBM("fontDestroy", "(J)V", Java_com_ohos_shim_bridge_OHBridge_fontDestroy),
    OHBM("fontSetSize", "(JF)V", Java_com_ohos_shim_bridge_OHBridge_fontSetSize),
    OHBM("fontMeasureText", "(JLjava/lang/String;)F", Java_com_ohos_shim_bridge_OHBridge_fontMeasureText),

    /* Path */
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

    /* Surface stubs */
    OHBM("surfaceCreate", "(JII)J", Java_com_ohos_shim_bridge_OHBridge_surfaceCreate),
    OHBM("surfaceDestroy", "(J)V", Java_com_ohos_shim_bridge_OHBridge_surfaceDestroy),
    OHBM("surfaceResize", "(JII)V", Java_com_ohos_shim_bridge_OHBridge_surfaceResize),
    OHBM("surfaceGetCanvas", "(J)J", Java_com_ohos_shim_bridge_OHBridge_surfaceGetCanvas),
    OHBM("surfaceFlush", "(J)I", Java_com_ohos_shim_bridge_OHBridge_surfaceFlush),

    /* Log */
    OHBM("logDebug", "(Ljava/lang/String;Ljava/lang/String;)V", Java_com_ohos_shim_bridge_OHBridge_logDebug),
    OHBM("logInfo", "(Ljava/lang/String;Ljava/lang/String;)V", Java_com_ohos_shim_bridge_OHBridge_logInfo),
    OHBM("logWarn", "(Ljava/lang/String;Ljava/lang/String;)V", Java_com_ohos_shim_bridge_OHBridge_logWarn),
    OHBM("logError", "(Ljava/lang/String;Ljava/lang/String;)V", Java_com_ohos_shim_bridge_OHBridge_logError),
};

int dvmRegisterOHBridge(JNIEnv* env) {
    return ohbridge_registerClass(env, "com/ohos/shim/bridge/OHBridge",
        gOHBridgeMethods, sizeof(gOHBridgeMethods)/sizeof(gOHBridgeMethods[0]));
}
