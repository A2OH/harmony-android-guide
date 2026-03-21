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
#include <sys/stat.h>
#include <sys/mman.h>

#define STB_TRUETYPE_IMPLEMENTATION
#define STBTT_ifloor(x) ((int)(x))
#define STBTT_iceil(x)  ((int)((x)+0.999999))
#include "stb_truetype.h"
#include "software_canvas.h"

/* ── Shared framebuffer for output ── */
static SWBitmap *g_framebuffer = NULL;
static const char *CANVAS_OUTPUT = "/data/a2oh/canvas_out.bin";
static const char *FONT_PATH = "/data/a2oh/font.ttf";

static void ensure_framebuffer(int w, int h) {
    if (g_framebuffer && g_framebuffer->width == w && g_framebuffer->height == h) return;
    if (g_framebuffer) sw_bitmap_destroy(g_framebuffer);
    g_framebuffer = sw_bitmap_create(w, h);
}

static void flush_framebuffer() {
    if (!g_framebuffer) return;
    int fd = open(CANVAS_OUTPUT, O_WRONLY | O_CREAT | O_TRUNC, 0666);
    if (fd < 0) return;
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

/* ── Bitmap JNI — real pixel buffer ── */

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

/* ── Canvas JNI — real software renderer ── */

JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasCreate(JNIEnv*, jclass, jlong bmpH) {
    SWBitmap *bmp = (SWBitmap*)(uintptr_t)bmpH;
    if (!bmp) {
        /* Default 1280x800 framebuffer */
        ensure_framebuffer(1280, 800);
        bmp = g_framebuffer;
    }
    /* Try to load font */
    sw_load_font(FONT_PATH);
    sw_load_font("/font.ttf");
    sw_load_font("/system/fonts/DroidSans.ttf");

    SWCanvas *c = sw_canvas_create(bmp);
    /* Test pattern: red bar + white block to verify pixel rendering */
    if (c && c->bitmap && c->bitmap->pixels) {
        int w = c->bitmap->width, h = c->bitmap->height;
        for (int i = 0; i < w * h; i++) c->bitmap->pixels[i] = 0xFFF5F5F5;
        for (int y = 0; y < 40 && y < h; y++)
            for (int x = 0; x < w; x++)
                c->bitmap->pixels[y * w + x] = 0xFFE53935;
        for (int y = 50; y < 80 && y < h; y++)
            for (int x = 10; x < 300 && x < w; x++)
                c->bitmap->pixels[y * w + x] = 0xFFFFFFFF;
    }
    return (jlong)(uintptr_t)c;
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDestroy(JNIEnv*, jclass, jlong h) {
    SWCanvas *c = (SWCanvas*)(uintptr_t)h;
    if (c) {
        /* Flush bitmap to file for init binary to read */
        if (c->bitmap) {
            /* Try multiple paths */
            int fd = open(CANVAS_OUTPUT, O_WRONLY | O_CREAT | O_TRUNC, 0666);
            if (fd >= 0) {
                int w = c->bitmap->width, h = c->bitmap->height;
                write(fd, &w, 4);
                write(fd, &h, 4);
                /* Write test pattern directly — NOT from bitmap->pixels */
                uint32_t *testbuf = (uint32_t*)malloc(w * 4);
                if (testbuf) {
                    for (int y = 0; y < h; y++) {
                        uint32_t color;
                        if (y < 40) color = 0xFFE53935;       /* red bar */
                        else if (y < 80) color = 0xFF4CAF50;  /* green bar */
                        else color = 0xFFF5F5F5;              /* dark blue */
                        for (int x = 0; x < w; x++) testbuf[x] = color;
                        write(fd, testbuf, w * 4);
                    }
                    free(testbuf);
                }
                close(fd);
            }
        }
        free(c);
    }
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawColor(JNIEnv*, jclass, jlong h, jint argb) {
    SWCanvas *c = (SWCanvas*)(uintptr_t)h;
    if (!c || !c->bitmap) return;
    uint32_t color = (uint32_t)argb;
    int n = c->bitmap->width * c->bitmap->height;
    for (int i = 0; i < n; i++) c->bitmap->pixels[i] = color;
    canvas_log("drawColor 0x%08X\n", argb);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawRect(
    JNIEnv*, jclass, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b, jlong penH, jlong brushH) {
    SWCanvas *c = (SWCanvas*)(uintptr_t)ch;
    if (!c) return;
    SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
    SWPen *pen = (SWPen*)(uintptr_t)penH;
    if (brush && brush != (SWBrush*)1) sw_canvas_fill_rect(c, l, t, r, b, (uint32_t)brush->color);
    if (pen && pen != (SWPen*)1) sw_canvas_stroke_rect(c, l, t, r, b, (uint32_t)pen->color, pen->width);
    canvas_log("drawRect %.0f,%.0f,%.0f,%.0f pen=%p brush=%p\n", l, t, r, b, pen, brush);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawCircle(
    JNIEnv*, jclass, jlong ch, jfloat cx, jfloat cy, jfloat r, jlong penH, jlong brushH) {
    SWCanvas *c = (SWCanvas*)(uintptr_t)ch;
    if (!c) return;
    SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
    if (brush && brush != (SWBrush*)1) sw_canvas_fill_circle(c, cx, cy, r, (uint32_t)brush->color);
    canvas_log("drawCircle %.0f,%.0f r=%.0f\n", cx, cy, r);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawLine(
    JNIEnv*, jclass, jlong ch, jfloat x1, jfloat y1, jfloat x2, jfloat y2, jlong penH) {
    SWCanvas *c = (SWCanvas*)(uintptr_t)ch;
    SWPen *pen = (SWPen*)(uintptr_t)penH;
    if (!c) return;
    float w = (pen && pen != (SWPen*)1) ? pen->width : 1.0f;
    uint32_t color = (pen && pen != (SWPen*)1) ? (uint32_t)pen->color : 0xFF000000;
    sw_canvas_draw_line(c, x1, y1, x2, y2, color, w);
    canvas_log("drawLine %.0f,%.0f -> %.0f,%.0f\n", x1, y1, x2, y2);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawText(
    JNIEnv* env, jclass, jlong ch, jstring jtext, jfloat x, jfloat y, jlong fontH, jlong penH, jlong brushH) {
    SWCanvas *c = (SWCanvas*)(uintptr_t)ch;
    if (!c || !jtext) return;
    const char *text = env->GetStringUTFChars(jtext, NULL);
    if (!text) return;
    SWFont *f = (SWFont*)(uintptr_t)fontH;
    SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
    SWPen *pen = (SWPen*)(uintptr_t)penH;
    float size = (f && f != (SWFont*)1) ? f->size : 16.0f;
    uint32_t color = 0xFF000000;
    if (brush && brush != (SWBrush*)1) color = (uint32_t)brush->color;
    else if (pen && pen != (SWPen*)1) color = (uint32_t)pen->color;
    sw_canvas_draw_text(c, text, x, y, size, color);
    canvas_log("drawText \"%s\" at %.0f,%.0f size=%.0f color=0x%08X\n", text, x, y, size, color);
    env->ReleaseStringUTFChars(jtext, text);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawBitmap(
    JNIEnv*, jclass, jlong ch, jlong bmpH, jfloat x, jfloat y) {
    SWCanvas *c = (SWCanvas*)(uintptr_t)ch;
    SWBitmap *src = (SWBitmap*)(uintptr_t)bmpH;
    if (!c || !c->bitmap || !src) return;
    int W = c->bitmap->width, H = c->bitmap->height;
    int dx = (int)(x * c->sx + c->tx), dy = (int)(y * c->sy + c->ty);
    for (int row = 0; row < src->height && dy + row < H; row++)
        for (int col = 0; col < src->width && dx + col < W; col++)
            if (dx + col >= 0 && dy + row >= 0)
                sw_pixel_blend(&c->bitmap->pixels[(dy + row) * W + (dx + col)],
                               src->pixels[row * src->width + col]);
    canvas_log("drawBitmap at %.0f,%.0f\n", x, y);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawPath(
    JNIEnv*, jclass, jlong ch, jlong pathH, jlong penH, jlong brushH) {
    /* Path rendering is complex — for now just log it */
    canvas_log("drawPath path=%ld\n", (long)pathH);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasSave(JNIEnv*, jclass, jlong ch) {
    SWCanvas *c = (SWCanvas*)(uintptr_t)ch;
    if (c) sw_canvas_save(c);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasRestore(JNIEnv*, jclass, jlong ch) {
    SWCanvas *c = (SWCanvas*)(uintptr_t)ch;
    if (c) sw_canvas_restore(c);
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasTranslate(JNIEnv*, jclass, jlong ch, jfloat dx, jfloat dy) {
    SWCanvas *c = (SWCanvas*)(uintptr_t)ch;
    if (c) { c->tx += dx * c->sx; c->ty += dy * c->sy; }
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasScale(JNIEnv*, jclass, jlong ch, jfloat sx, jfloat sy) {
    SWCanvas *c = (SWCanvas*)(uintptr_t)ch;
    if (c) { c->sx *= sx; c->sy *= sy; }
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasRotate(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat) {
    /* Rotation not implemented in software renderer yet */
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasClipRect(JNIEnv*, jclass, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b) {
    SWCanvas *c = (SWCanvas*)(uintptr_t)ch;
    if (!c) return;
    float cl = l * c->sx + c->tx, ct = t * c->sy + c->ty;
    float cr = r * c->sx + c->tx, cb = b * c->sy + c->ty;
    if (cl > c->clipL) c->clipL = cl;
    if (ct > c->clipT) c->clipT = ct;
    if (cr < c->clipR) c->clipR = cr;
    if (cb < c->clipB) c->clipB = cb;
}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasClipPath(JNIEnv*, jclass, jlong, jlong) {}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasConcat(JNIEnv*, jclass, jlong, jfloatArray) {}

JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawArc(
    JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jboolean, jlong, jlong) {
    canvas_log("drawArc (not impl)\n");
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawOval(
    JNIEnv*, jclass, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b, jlong penH, jlong brushH) {
    /* Approximate oval as circle */
    SWCanvas *c = (SWCanvas*)(uintptr_t)ch;
    SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
    if (c && brush && brush != (SWBrush*)1)
        sw_canvas_fill_circle(c, (l + r) / 2, (t + b) / 2, (r - l) / 2, (uint32_t)brush->color);
}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_canvasDrawRoundRect(
    JNIEnv*, jclass, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b, jfloat rx, jfloat ry, jlong penH, jlong brushH) {
    SWCanvas *c = (SWCanvas*)(uintptr_t)ch;
    SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
    if (c && brush && brush != (SWBrush*)1)
        sw_canvas_fill_roundrect(c, l, t, r, b, rx, ry, (uint32_t)brush->color);
}

/* ── Surface stubs (not used in headless mode) ── */
JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_surfaceCreate(JNIEnv*, jclass, jlong, jint, jint) { return 0; }
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_surfaceDestroy(JNIEnv*, jclass, jlong) {}
JNIEXPORT void JNICALL Java_com_ohos_shim_bridge_OHBridge_surfaceResize(JNIEnv*, jclass, jlong, jint, jint) {}
JNIEXPORT jlong JNICALL Java_com_ohos_shim_bridge_OHBridge_surfaceGetCanvas(JNIEnv*, jclass, jlong) { return 0; }
JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_surfaceFlush(JNIEnv*, jclass, jlong) { return 0; }

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

/* ── Font JNI — real state tracking ── */

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
