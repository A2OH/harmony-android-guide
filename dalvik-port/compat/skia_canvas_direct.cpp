/*
 * Skia-direct Canvas JNI — calls SkCanvas/SkBitmap/SkPaint C++ API directly.
 * Compiled with -DHAVE_SKIA_STATIC and linked against native Skia .o files.
 *
 * This file provides ONLY the canvas/bitmap/font JNI methods.
 * The rest (node API, path, pen, brush) come from arkui_bridge.cpp.
 *
 * Build: clang++ -DHAVE_SKIA_STATIC -I$SKIA/include -c skia_canvas_direct.cpp
 * Link: ... libdvm.a libskia_native.a ...
 */

#ifdef HAVE_SKIA_STATIC

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <sys/stat.h>
#include <sys/mman.h>

#include "include/core/SkCanvas.h"
#include "include/core/SkBitmap.h"
#include "include/core/SkPaint.h"
#include "include/core/SkPath.h"
#include "include/core/SkFont.h"
#include "include/core/SkFontMetrics.h"
#include "include/core/SkTextBlob.h"
#include "include/core/SkTypeface.h"
#include "include/core/SkFontMgr.h"
#include "include/core/SkData.h"
#include "include/core/SkRect.h"
#include "include/core/SkRRect.h"
#include "include/core/SkString.h"
#include "include/core/SkImageInfo.h"
#include "include/core/SkPoint.h"

static const char *FONT_PATH = "/data/a2oh/font.ttf";

/* ── Global typeface (raw pointer to avoid C++ global constructor) ── */
static SkTypeface *g_typeface = nullptr;
static SkFontMgr *g_fontMgr = nullptr;

extern sk_sp<SkFontMgr> SkFontMgr_New_Custom_Directory(const char*);

static void ensure_typeface() {
    if (g_typeface) return;
    /* Create a font manager that scans /data/a2oh/ for .ttf files */
    if (!g_fontMgr) {
        auto mgr = SkFontMgr_New_Custom_Directory("/data/a2oh/");
        if (mgr) g_fontMgr = mgr.release();
    }
    if (g_fontMgr) {
        /* Use the directory font manager to create typeface from font.ttf */
        auto data = SkData::MakeFromFileName(FONT_PATH);
        if (data) {
            auto tf = g_fontMgr->makeFromData(data);
            if (tf) { g_typeface = tf.release(); }
        }
        /* Fallback: pick first font the manager found */
        if (!g_typeface && g_fontMgr->countFamilies() > 0) {
            SkFontStyleSet *styleSet = g_fontMgr->createStyleSet(0);
            if (styleSet && styleSet->count() > 0) {
                auto tf = sk_sp<SkTypeface>(styleSet->createTypeface(0));
                if (tf) g_typeface = tf.release();
            }
            if (styleSet) styleSet->unref();
        }
    }
    if (g_typeface) fprintf(stderr, "Skia: typeface loaded OK\n");
    else fprintf(stderr, "Skia: no typeface, text disabled\n");
}

/* ── Skia Canvas wrapper ── */
struct SkCanvas2 {
    SkBitmap bitmap;
    SkCanvas *canvas;
    SkFont font;
    int w, h;
    /* Link to SWBitmap for bitmapWriteToFile/BlitToFb0 */
    void *swBitmap; /* SWBitmap* — pixels shared */
};

/* Paint helpers */
static SkPaint makeFill(uint32_t argb) {
    SkPaint p;
    p.setStyle(SkPaint::kFill_Style);
    p.setAntiAlias(true);
    /* ARGB → SkColor (same format) */
    p.setColor((SkColor)argb);
    return p;
}
static SkPaint makeStroke(uint32_t argb, float width) {
    SkPaint p;
    p.setStyle(SkPaint::kStroke_Style);
    p.setStrokeWidth(width);
    p.setAntiAlias(true);
    p.setColor((SkColor)argb);
    return p;
}

/* Forward decl — defined in software_canvas.h */
struct SWBitmap { int width; int height; uint32_t *pixels; };
struct SWBrush { int32_t color; float alpha; int antiAlias; };
struct SWPen { int32_t color; float width; float alpha; int antiAlias; int cap; int join; };
struct SWFont { float size; };

/* ── Bitmap JNI (Skia-backed) ── */

extern "C" JNIEXPORT jlong JNICALL
Java_com_ohos_shim_bridge_OHBridge_bitmapCreate(JNIEnv*, jclass, jint w, jint h, jint) {
    /* Create SWBitmap (for backward compat with bitmapWriteToFile/BlitToFb0) */
    SWBitmap *sb = (SWBitmap*)calloc(1, sizeof(SWBitmap));
    sb->width = w; sb->height = h;
    sb->pixels = (uint32_t*)calloc(w * h, 4);
    return (jlong)(uintptr_t)sb;
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_bitmapDestroy(JNIEnv*, jclass, jlong h) {
    SWBitmap *sb = (SWBitmap*)(uintptr_t)h;
    if (sb) { free(sb->pixels); free(sb); }
}

extern "C" JNIEXPORT jint JNICALL
Java_com_ohos_shim_bridge_OHBridge_bitmapGetWidth(JNIEnv*, jclass, jlong h) {
    SWBitmap *sb = (SWBitmap*)(uintptr_t)h;
    return sb ? sb->width : 0;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_ohos_shim_bridge_OHBridge_bitmapGetHeight(JNIEnv*, jclass, jlong h) {
    SWBitmap *sb = (SWBitmap*)(uintptr_t)h;
    return sb ? sb->height : 0;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_ohos_shim_bridge_OHBridge_bitmapGetPixel(JNIEnv*, jclass, jlong h, jint x, jint y) {
    SWBitmap *sb = (SWBitmap*)(uintptr_t)h;
    if (sb && x >= 0 && x < sb->width && y >= 0 && y < sb->height)
        return (jint)sb->pixels[y * sb->width + x];
    return 0;
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_bitmapSetPixel(JNIEnv*, jclass, jlong h, jint x, jint y, jint argb) {
    SWBitmap *sb = (SWBitmap*)(uintptr_t)h;
    if (sb && x >= 0 && x < sb->width && y >= 0 && y < sb->height)
        sb->pixels[y * sb->width + x] = (uint32_t)argb;
}

/* ── Bulk pixel operations ── */

extern "C" JNIEXPORT jint JNICALL
Java_com_ohos_shim_bridge_OHBridge_bitmapWriteToFile(JNIEnv* env, jclass, jlong h, jstring jpath) {
    SWBitmap *b = (SWBitmap*)(uintptr_t)h;
    if (!b || !b->pixels) return -1;
    const char *path = env->GetStringUTFChars(jpath, NULL);
    if (!path) return -2;
    mkdir("/data/a2oh", 0777);
    int fd = open(path, O_WRONLY | O_CREAT | O_TRUNC, 0666);
    env->ReleaseStringUTFChars(jpath, path);
    if (fd < 0) return -3;
    int w = b->width, h2 = b->height;
    write(fd, &w, 4); write(fd, &h2, 4);
    write(fd, b->pixels, w * h2 * 4);
    close(fd);
    return w * h2;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_ohos_shim_bridge_OHBridge_bitmapBlitToFb0(JNIEnv*, jclass, jlong h, jint scrollY) {
    SWBitmap *b = (SWBitmap*)(uintptr_t)h;
    if (!b || !b->pixels) return -1;
    int fb = open("/dev/fb0", O_RDWR);
    if (fb < 0) return -2;
    int fbW = 1280, fbH = 800, fbSize = fbW * fbH * 4;
    uint32_t *fbMem = (uint32_t*)mmap(NULL, fbSize, PROT_READ|PROT_WRITE, MAP_SHARED, fb, 0);
    if (fbMem == MAP_FAILED) {
        lseek(fb, 0, SEEK_SET);
        for (int y = 0; y < fbH; y++) {
            int sy = y + scrollY;
            if (sy >= 0 && sy < b->height && b->width >= fbW)
                write(fb, &b->pixels[sy * b->width], fbW * 4);
        }
        close(fb); return fbW * fbH;
    }
    for (int y = 0; y < fbH; y++) {
        int sy = y + scrollY;
        if (sy >= 0 && sy < b->height) {
            int cw = (b->width < fbW) ? b->width : fbW;
            memcpy(&fbMem[y * fbW], &b->pixels[sy * b->width], cw * 4);
        } else memset(&fbMem[y * fbW], 0xF5, fbW * 4);
    }
    munmap(fbMem, fbSize); close(fb);
    return fbW * fbH;
}

/* ── Canvas JNI (Skia-direct) ── */

extern "C" JNIEXPORT jlong JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasCreate(JNIEnv*, jclass, jlong bmpH) {
    SWBitmap *sb = (SWBitmap*)(uintptr_t)bmpH;
    if (!sb) return 0;

    SkCanvas2 *sc = new SkCanvas2();
    sc->w = sb->width;
    sc->h = sb->height;
    sc->swBitmap = sb;

    /* Create SkBitmap backed by the SWBitmap pixel buffer */
    SkImageInfo info = SkImageInfo::MakeN32Premul(sb->width, sb->height);
    sc->bitmap.installPixels(info, sb->pixels, sb->width * 4);
    sc->canvas = new SkCanvas(sc->bitmap);

    /* Setup font */
    ensure_typeface();
    if (g_typeface) sc->font.setTypeface(sk_ref_sp(g_typeface));
    sc->font.setSize(16);
    sc->font.setEdging(SkFont::Edging::kAntiAlias);

    fprintf(stderr, "canvasCreate: Skia direct %dx%d\n", sb->width, sb->height);
    return (jlong)(uintptr_t)sc;
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasDestroy(JNIEnv*, jclass, jlong h) {
    SkCanvas2 *sc = (SkCanvas2*)(uintptr_t)h;
    if (sc) {
        delete sc->canvas;
        delete sc;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasDrawColor(JNIEnv*, jclass, jlong h, jint argb) {
    SkCanvas2 *sc = (SkCanvas2*)(uintptr_t)h;
    if (sc) sc->canvas->clear((SkColor)argb);
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasDrawRect(
    JNIEnv*, jclass, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b, jlong penH, jlong brushH) {
    SkCanvas2 *sc = (SkCanvas2*)(uintptr_t)ch;
    if (!sc) return;
    SkRect rect = SkRect::MakeLTRB(l, t, r, b);
    SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
    SWPen *pen = (SWPen*)(uintptr_t)penH;
    if (brush && brush != (SWBrush*)1) {
        sc->canvas->drawRect(rect, makeFill(brush->color));
    }
    if (pen && pen != (SWPen*)1) {
        sc->canvas->drawRect(rect, makeStroke(pen->color, pen->width));
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasDrawCircle(
    JNIEnv*, jclass, jlong ch, jfloat cx, jfloat cy, jfloat radius, jlong penH, jlong brushH) {
    SkCanvas2 *sc = (SkCanvas2*)(uintptr_t)ch;
    if (!sc) return;
    SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
    SWPen *pen = (SWPen*)(uintptr_t)penH;
    if (brush && brush != (SWBrush*)1)
        sc->canvas->drawCircle(cx, cy, radius, makeFill(brush->color));
    if (pen && pen != (SWPen*)1)
        sc->canvas->drawCircle(cx, cy, radius, makeStroke(pen->color, pen->width));
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasDrawLine(
    JNIEnv*, jclass, jlong ch, jfloat x1, jfloat y1, jfloat x2, jfloat y2, jlong penH) {
    SkCanvas2 *sc = (SkCanvas2*)(uintptr_t)ch;
    SWPen *pen = (SWPen*)(uintptr_t)penH;
    if (!sc) return;
    uint32_t color = (pen && pen != (SWPen*)1) ? pen->color : 0xFF000000;
    float w = (pen && pen != (SWPen*)1) ? pen->width : 1.0f;
    sc->canvas->drawLine(x1, y1, x2, y2, makeStroke(color, w));
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasDrawText(
    JNIEnv* env, jclass, jlong ch, jstring jtext, jfloat x, jfloat y, jlong fontH, jlong penH, jlong brushH) {
    SkCanvas2 *sc = (SkCanvas2*)(uintptr_t)ch;
    if (!sc || !jtext) return;
    const char *text = env->GetStringUTFChars(jtext, NULL);
    if (!text) return;

    SWFont *f = (SWFont*)(uintptr_t)fontH;
    float size = (f && f != (SWFont*)1) ? f->size : 16.0f;
    sc->font.setSize(size);

    SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
    SWPen *pen = (SWPen*)(uintptr_t)penH;
    uint32_t color = 0xFF000000;
    if (brush && brush != (SWBrush*)1) color = brush->color;
    else if (pen && pen != (SWPen*)1) color = pen->color;

    auto blob = SkTextBlob::MakeFromString(text, sc->font);
    if (blob) sc->canvas->drawTextBlob(blob, x, y, makeFill(color));

    env->ReleaseStringUTFChars(jtext, text);
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasDrawRoundRect(
    JNIEnv*, jclass, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b, jfloat rx, jfloat ry, jlong penH, jlong brushH) {
    SkCanvas2 *sc = (SkCanvas2*)(uintptr_t)ch;
    if (!sc) return;
    SkRect rect = SkRect::MakeLTRB(l, t, r, b);
    SkRRect rrect;
    rrect.setRectXY(rect, rx, ry);
    SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
    SWPen *pen = (SWPen*)(uintptr_t)penH;
    if (brush && brush != (SWBrush*)1)
        sc->canvas->drawRRect(rrect, makeFill(brush->color));
    if (pen && pen != (SWPen*)1)
        sc->canvas->drawRRect(rrect, makeStroke(pen->color, pen->width));
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasDrawPath(JNIEnv*, jclass, jlong, jlong, jlong, jlong) {
    /* TODO: convert SWPath to SkPath */
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasDrawBitmap(JNIEnv*, jclass, jlong, jlong, jfloat, jfloat) {
    /* TODO */
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasSave(JNIEnv*, jclass, jlong ch) {
    SkCanvas2 *sc = (SkCanvas2*)(uintptr_t)ch;
    if (sc) sc->canvas->save();
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasRestore(JNIEnv*, jclass, jlong ch) {
    SkCanvas2 *sc = (SkCanvas2*)(uintptr_t)ch;
    if (sc) sc->canvas->restore();
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasTranslate(JNIEnv*, jclass, jlong ch, jfloat dx, jfloat dy) {
    SkCanvas2 *sc = (SkCanvas2*)(uintptr_t)ch;
    if (sc) sc->canvas->translate(dx, dy);
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasScale(JNIEnv*, jclass, jlong ch, jfloat sx, jfloat sy) {
    SkCanvas2 *sc = (SkCanvas2*)(uintptr_t)ch;
    if (sc) sc->canvas->scale(sx, sy);
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasRotate(JNIEnv*, jclass, jlong ch, jfloat deg, jfloat px, jfloat py) {
    SkCanvas2 *sc = (SkCanvas2*)(uintptr_t)ch;
    if (sc) { sc->canvas->translate(px, py); sc->canvas->rotate(deg); sc->canvas->translate(-px, -py); }
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasClipRect(JNIEnv*, jclass, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b) {
    SkCanvas2 *sc = (SkCanvas2*)(uintptr_t)ch;
    if (sc) sc->canvas->clipRect(SkRect::MakeLTRB(l, t, r, b));
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasDrawOval(
    JNIEnv*, jclass, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b, jlong penH, jlong brushH) {
    SkCanvas2 *sc = (SkCanvas2*)(uintptr_t)ch;
    if (!sc) return;
    SWBrush *brush = (SWBrush*)(uintptr_t)brushH;
    if (brush && brush != (SWBrush*)1)
        sc->canvas->drawOval(SkRect::MakeLTRB(l, t, r, b), makeFill(brush->color));
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasDrawArc(
    JNIEnv*, jclass, jlong ch, jfloat l, jfloat t, jfloat r, jfloat b,
    jfloat startAngle, jfloat sweepAngle, jboolean, jlong penH, jlong brushH) {
    SkCanvas2 *sc = (SkCanvas2*)(uintptr_t)ch;
    if (!sc) return;
    SWPen *pen = (SWPen*)(uintptr_t)penH;
    if (pen && pen != (SWPen*)1)
        sc->canvas->drawArc(SkRect::MakeLTRB(l, t, r, b), startAngle, sweepAngle, false,
                            makeStroke(pen->color, pen->width));
}

extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasClipPath(JNIEnv*, jclass, jlong, jlong) {}
extern "C" JNIEXPORT void JNICALL
Java_com_ohos_shim_bridge_OHBridge_canvasConcat(JNIEnv*, jclass, jlong, jfloatArray) {}

#endif /* HAVE_SKIA_STATIC */
