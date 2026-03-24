#include <stdio.h>
/*
 * OHBridge Android-backed JNI — uses Android Canvas via JNI callbacks.
 * Loaded by WestlakeHostActivity, registers methods for the shim's OHBridge class.
 *
 * The shim's OHBridge.canvasDrawRect() → this native → calls
 * WestlakeHostActivity.getOffscreenCanvas().drawRect() via JNI.
 */
#include <jni.h>
#include <android/log.h>


#include <string.h>
#include <math.h>

#define LOG_TAG "OHBridge"
#define LOGI(...) __android_log_print(4, "OHBridge", __VA_ARGS__)
#define LOGE(...) __android_log_print(6, "OHBridge", __VA_ARGS__)

/* Cached JNI references */
static jclass hostClass = NULL;
static jmethodID flushMethod = NULL;
static jmethodID getCanvasMethod = NULL;
static jmethodID createPenMethod = NULL;
static jmethodID getPenMethod = NULL;
static jmethodID createBrushMethod = NULL;
static jmethodID getBrushMethod = NULL;
static jmethodID createFontMethod = NULL;
static jmethodID getFontMethod = NULL;
static jmethodID createPathMethod = NULL;
static jmethodID getPathMethod = NULL;

/* Android Canvas/Paint method IDs */
static jclass canvasClass = NULL;
static jmethodID canvas_drawRect = NULL;
static jmethodID canvas_drawCircle = NULL;
static jmethodID canvas_drawLine = NULL;
static jmethodID canvas_drawText = NULL;
static jmethodID canvas_drawColor = NULL;
static jmethodID canvas_drawRoundRect = NULL;
static jmethodID canvas_drawPath = NULL;
static jmethodID canvas_drawBitmap = NULL;
static jmethodID canvas_save = NULL;
static jmethodID canvas_restore = NULL;
static jmethodID canvas_translate = NULL;
static jmethodID canvas_scale = NULL;
static jmethodID canvas_clipRect = NULL;

static jclass paintClass = NULL;
static jmethodID paint_setColor = NULL;
static jmethodID paint_setStrokeWidth = NULL;
static jmethodID paint_setTextSize = NULL;
static jmethodID paint_setAntiAlias = NULL;
static jmethodID paint_setStyle = NULL;
static jmethodID paint_measureText = NULL;
static jmethodID paint_getFontMetrics = NULL;
static jmethodID paint_setAlpha = NULL;

static jclass rectfClass = NULL;
static jmethodID rectf_init = NULL;

static jclass fontMetricsClass = NULL;
static jfieldID fm_top = NULL;
static jfieldID fm_ascent = NULL;
static jfieldID fm_descent = NULL;
static jfieldID fm_bottom = NULL;

static jclass pathClass = NULL;
static jmethodID path_moveTo = NULL;
static jmethodID path_lineTo = NULL;
static jmethodID path_close = NULL;
static jmethodID path_reset = NULL;
static jmethodID path_addRect = NULL;
static jmethodID path_addCircle = NULL;
static jmethodID path_cubicTo = NULL;
static jmethodID path_quadTo = NULL;

static JavaVM* g_vm = NULL;

static JNIEnv* getEnv() {
    JNIEnv* env;
    (*g_vm)->GetEnv(g_vm, (void**)&env, JNI_VERSION_1_6);
    return env;
}

static void initCache(JNIEnv* env) {
    if (hostClass) return;
    hostClass = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "com/westlake/host/WestlakeHostActivity"));
    flushMethod = (*env)->GetStaticMethodID(env, hostClass, "nativeFlush", "()V");
    getCanvasMethod = (*env)->GetStaticMethodID(env, hostClass, "getOffscreenCanvas", "()Landroid/graphics/Canvas;");
    createPenMethod = (*env)->GetStaticMethodID(env, hostClass, "createPen", "()I");
    getPenMethod = (*env)->GetStaticMethodID(env, hostClass, "getPen", "(I)Landroid/graphics/Paint;");
    createBrushMethod = (*env)->GetStaticMethodID(env, hostClass, "createBrush", "()I");
    getBrushMethod = (*env)->GetStaticMethodID(env, hostClass, "getBrush", "(I)Landroid/graphics/Paint;");
    createFontMethod = (*env)->GetStaticMethodID(env, hostClass, "createFont", "()I");
    getFontMethod = (*env)->GetStaticMethodID(env, hostClass, "getFont", "(I)Landroid/graphics/Paint;");
    createPathMethod = (*env)->GetStaticMethodID(env, hostClass, "createPath", "()I");
    getPathMethod = (*env)->GetStaticMethodID(env, hostClass, "getPath", "(I)Landroid/graphics/Path;");

    canvasClass = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "android/graphics/Canvas"));
    canvas_drawRect = (*env)->GetMethodID(env, canvasClass, "drawRect", "(FFFFLandroid/graphics/Paint;)V");
    canvas_drawCircle = (*env)->GetMethodID(env, canvasClass, "drawCircle", "(FFFLandroid/graphics/Paint;)V");
    canvas_drawLine = (*env)->GetMethodID(env, canvasClass, "drawLine", "(FFFFLandroid/graphics/Paint;)V");
    canvas_drawText = (*env)->GetMethodID(env, canvasClass, "drawText", "(Ljava/lang/String;FFLandroid/graphics/Paint;)V");
    canvas_drawColor = (*env)->GetMethodID(env, canvasClass, "drawColor", "(I)V");
    canvas_drawRoundRect = (*env)->GetMethodID(env, canvasClass, "drawRoundRect", "(FFFFFFLandroid/graphics/Paint;)V");
    canvas_drawPath = (*env)->GetMethodID(env, canvasClass, "drawPath", "(Landroid/graphics/Path;Landroid/graphics/Paint;)V");
    canvas_save = (*env)->GetMethodID(env, canvasClass, "save", "()I");
    canvas_restore = (*env)->GetMethodID(env, canvasClass, "restore", "()V");
    canvas_translate = (*env)->GetMethodID(env, canvasClass, "translate", "(FF)V");
    canvas_scale = (*env)->GetMethodID(env, canvasClass, "scale", "(FF)V");
    canvas_clipRect = (*env)->GetMethodID(env, canvasClass, "clipRect", "(FFFF)Z");

    paintClass = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "android/graphics/Paint"));
    paint_setColor = (*env)->GetMethodID(env, paintClass, "setColor", "(I)V");
    paint_setStrokeWidth = (*env)->GetMethodID(env, paintClass, "setStrokeWidth", "(F)V");
    paint_setTextSize = (*env)->GetMethodID(env, paintClass, "setTextSize", "(F)V");
    paint_setAntiAlias = (*env)->GetMethodID(env, paintClass, "setAntiAlias", "(Z)V");
    paint_measureText = (*env)->GetMethodID(env, paintClass, "measureText", "(Ljava/lang/String;)F");
    paint_setAlpha = (*env)->GetMethodID(env, paintClass, "setAlpha", "(I)V");

    fontMetricsClass = (*env)->FindClass(env, "android/graphics/Paint$FontMetrics");
    fm_top = (*env)->GetFieldID(env, fontMetricsClass, "top", "F");
    fm_ascent = (*env)->GetFieldID(env, fontMetricsClass, "ascent", "F");
    fm_descent = (*env)->GetFieldID(env, fontMetricsClass, "descent", "F");
    fm_bottom = (*env)->GetFieldID(env, fontMetricsClass, "bottom", "F");
    paint_getFontMetrics = (*env)->GetMethodID(env, paintClass, "getFontMetrics", "()Landroid/graphics/Paint$FontMetrics;");

    pathClass = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "android/graphics/Path"));
    path_moveTo = (*env)->GetMethodID(env, pathClass, "moveTo", "(FF)V");
    path_lineTo = (*env)->GetMethodID(env, pathClass, "lineTo", "(FF)V");
    path_close = (*env)->GetMethodID(env, pathClass, "close", "()V");
    path_reset = (*env)->GetMethodID(env, pathClass, "reset", "()V");
    path_cubicTo = (*env)->GetMethodID(env, pathClass, "cubicTo", "(FFFFFF)V");
    path_quadTo = (*env)->GetMethodID(env, pathClass, "quadTo", "(FFFF)V");

    LOGI("JNI cache initialized");
}

static jobject getCanvas(JNIEnv* env) {
    return (*env)->CallStaticObjectMethod(env, hostClass, getCanvasMethod);
}

/* ══════ OHBridge JNI implementations ══════ */

static void OHB_logDebug(JNIEnv* e, jclass c, jstring t, jstring m) {
    const char* tag = (*e)->GetStringUTFChars(e, t, 0);
    const char* msg = (*e)->GetStringUTFChars(e, m, 0);
    ((void)0);
    (*e)->ReleaseStringUTFChars(e, m, msg);
    (*e)->ReleaseStringUTFChars(e, t, tag);
}
static void OHB_logInfo(JNIEnv* e, jclass c, jstring t, jstring m) {
    const char* tag = (*e)->GetStringUTFChars(e, t, 0);
    const char* msg = (*e)->GetStringUTFChars(e, m, 0);
    ((void)0);
    (*e)->ReleaseStringUTFChars(e, m, msg);
    (*e)->ReleaseStringUTFChars(e, t, tag);
}
static void OHB_logWarn(JNIEnv* e, jclass c, jstring t, jstring m) {
    const char* tag = (*e)->GetStringUTFChars(e, t, 0);
    const char* msg = (*e)->GetStringUTFChars(e, m, 0);
    ((void)0);
    (*e)->ReleaseStringUTFChars(e, m, msg);
    (*e)->ReleaseStringUTFChars(e, t, tag);
}
static void OHB_logError(JNIEnv* e, jclass c, jstring t, jstring m) {
    const char* tag = (*e)->GetStringUTFChars(e, t, 0);
    const char* msg = (*e)->GetStringUTFChars(e, m, 0);
    ((void)0);
    (*e)->ReleaseStringUTFChars(e, m, msg);
    (*e)->ReleaseStringUTFChars(e, t, tag);
}

static jint OHB_arkuiInit(JNIEnv* e, jclass c) { initCache(e); return 0; }

/* Surface */
static jlong OHB_surfaceCreate(JNIEnv* e, jclass c, jlong u, jint w, jint h) { return 1; }
static jlong OHB_surfaceGetCanvas(JNIEnv* e, jclass c, jlong s) { return 1; }
static jint OHB_surfaceFlush(JNIEnv* e, jclass c, jlong s) {
    (*e)->CallStaticVoidMethod(e, hostClass, flushMethod);
    return 0;
}
static void OHB_surfaceDestroy(JNIEnv* e, jclass c, jlong s) {}
static void OHB_surfaceResize(JNIEnv* e, jclass c, jlong s, jint w, jint h) {}

/* Canvas */
static jlong OHB_canvasCreate(JNIEnv* e, jclass c, jlong bmp) { return 1; }
static void OHB_canvasDestroy(JNIEnv* e, jclass c, jlong cn) {}

static void OHB_canvasDrawColor(JNIEnv* e, jclass c, jlong cn, jint color) {
    jobject canvas = getCanvas(e);
    if (canvas) (*e)->CallVoidMethod(e, canvas, canvas_drawColor, color);
}

static void OHB_canvasDrawRect(JNIEnv* e, jclass c, jlong cn, jfloat l, jfloat t, jfloat r, jfloat b, jlong pen, jlong brush) {
    jobject canvas = getCanvas(e);
    if (!canvas) return;
    jobject paint = NULL;
    if (brush > 0) paint = (*e)->CallStaticObjectMethod(e, hostClass, getBrushMethod, (jint)brush);
    if (!paint && pen > 0) paint = (*e)->CallStaticObjectMethod(e, hostClass, getPenMethod, (jint)pen);
    if (paint) (*e)->CallVoidMethod(e, canvas, canvas_drawRect, l, t, r, b, paint);
}

static void OHB_canvasDrawRoundRect(JNIEnv* e, jclass c, jlong cn, jfloat l, jfloat t, jfloat r, jfloat b, jfloat rx, jfloat ry, jlong pen, jlong brush) {
    jobject canvas = getCanvas(e);
    if (!canvas) return;
    jobject paint = NULL;
    if (brush > 0) paint = (*e)->CallStaticObjectMethod(e, hostClass, getBrushMethod, (jint)brush);
    if (!paint && pen > 0) paint = (*e)->CallStaticObjectMethod(e, hostClass, getPenMethod, (jint)pen);
    if (paint) (*e)->CallVoidMethod(e, canvas, canvas_drawRoundRect, l, t, r, b, rx, ry, paint);
}

static void OHB_canvasDrawCircle(JNIEnv* e, jclass c, jlong cn, jfloat cx, jfloat cy, jfloat r, jlong pen, jlong brush) {
    jobject canvas = getCanvas(e);
    if (!canvas) return;
    jobject paint = NULL;
    if (brush > 0) paint = (*e)->CallStaticObjectMethod(e, hostClass, getBrushMethod, (jint)brush);
    if (!paint && pen > 0) paint = (*e)->CallStaticObjectMethod(e, hostClass, getPenMethod, (jint)pen);
    if (paint) (*e)->CallVoidMethod(e, canvas, canvas_drawCircle, cx, cy, r, paint);
}

static void OHB_canvasDrawLine(JNIEnv* e, jclass c, jlong cn, jfloat x1, jfloat y1, jfloat x2, jfloat y2, jlong pen) {
    jobject canvas = getCanvas(e);
    if (!canvas) return;
    jobject paint = (pen > 0) ? (*e)->CallStaticObjectMethod(e, hostClass, getPenMethod, (jint)pen) : NULL;
    if (paint) (*e)->CallVoidMethod(e, canvas, canvas_drawLine, x1, y1, x2, y2, paint);
}

static void OHB_canvasDrawText(JNIEnv* e, jclass c, jlong cn, jstring text, jfloat x, jfloat y, jlong font, jlong pen, jlong brush) {
    jobject canvas = getCanvas(e);
    if (!canvas || !text) return;
    jobject paint = NULL;
    if (font > 0) paint = (*e)->CallStaticObjectMethod(e, hostClass, getFontMethod, (jint)font);
    if (!paint && pen > 0) paint = (*e)->CallStaticObjectMethod(e, hostClass, getPenMethod, (jint)pen);
    if (!paint && brush > 0) paint = (*e)->CallStaticObjectMethod(e, hostClass, getBrushMethod, (jint)brush);
    if (paint) {
        /* Set color from pen or brush */
        if (pen > 0) {
            jobject p = (*e)->CallStaticObjectMethod(e, hostClass, getPenMethod, (jint)pen);
            if (p) {
                jmethodID getColor = (*e)->GetMethodID(e, paintClass, "getColor", "()I");
                jint col = (*e)->CallIntMethod(e, p, getColor);
                (*e)->CallVoidMethod(e, paint, paint_setColor, col);
            }
        }
        (*e)->CallVoidMethod(e, canvas, canvas_drawText, text, x, y, paint);
    }
}

static void OHB_canvasSave(JNIEnv* e, jclass c, jlong cn) {
    jobject canvas = getCanvas(e);
    if (canvas) (*e)->CallIntMethod(e, canvas, canvas_save);
}
static void OHB_canvasRestore(JNIEnv* e, jclass c, jlong cn) {
    jobject canvas = getCanvas(e);
    if (canvas) (*e)->CallVoidMethod(e, canvas, canvas_restore);
}
static void OHB_canvasTranslate(JNIEnv* e, jclass c, jlong cn, jfloat dx, jfloat dy) {
    jobject canvas = getCanvas(e);
    if (canvas) (*e)->CallVoidMethod(e, canvas, canvas_translate, dx, dy);
}
static void OHB_canvasScale(JNIEnv* e, jclass c, jlong cn, jfloat sx, jfloat sy) {
    jobject canvas = getCanvas(e);
    if (canvas) (*e)->CallVoidMethod(e, canvas, canvas_scale, sx, sy);
}
static void OHB_canvasClipRect(JNIEnv* e, jclass c, jlong cn, jfloat l, jfloat t, jfloat r, jfloat b) {
    jobject canvas = getCanvas(e);
    if (canvas) (*e)->CallBooleanMethod(e, canvas, canvas_clipRect, l, t, r, b);
}
static void OHB_canvasRotate(JNIEnv* e, jclass c, jlong cn, jfloat d, jfloat px, jfloat py) {}
static void OHB_canvasClipPath(JNIEnv* e, jclass c, jlong cn, jlong p) {}
static void OHB_canvasConcat(JNIEnv* e, jclass c, jlong cn, jfloatArray m) {}
static void OHB_canvasDrawOval(JNIEnv* e, jclass c, jlong cn, jfloat l, jfloat t, jfloat r, jfloat b, jlong pen, jlong brush) {}
static void OHB_canvasDrawArc(JNIEnv* e, jclass c, jlong cn, jfloat l, jfloat t, jfloat r, jfloat b, jfloat sa, jfloat sw, jboolean uc, jlong pen, jlong brush) {}
static void OHB_canvasDrawPath(JNIEnv* e, jclass c, jlong cn, jlong p, jlong pen, jlong brush) {
    jobject canvas = getCanvas(e);
    if (!canvas) return;
    jobject path = (p > 0) ? (*e)->CallStaticObjectMethod(e, hostClass, getPathMethod, (jint)p) : NULL;
    jobject paint = NULL;
    if (brush > 0) paint = (*e)->CallStaticObjectMethod(e, hostClass, getBrushMethod, (jint)brush);
    if (!paint && pen > 0) paint = (*e)->CallStaticObjectMethod(e, hostClass, getPenMethod, (jint)pen);
    if (path && paint) (*e)->CallVoidMethod(e, canvas, canvas_drawPath, path, paint);
}
static void OHB_canvasDrawBitmap(JNIEnv* e, jclass c, jlong cn, jlong bmp, jfloat x, jfloat y) {}
static void OHB_canvasDrawBitmapNine(JNIEnv* e, jclass c, jlong cn, jlong bmp, jint cl, jint ct, jint cr, jint cb, jfloat dl, jfloat dt, jfloat dr, jfloat db, jlong p) {}

/* Pen */
static jlong OHB_penCreate(JNIEnv* e, jclass c) { return (*e)->CallStaticIntMethod(e, hostClass, createPenMethod); }
static void OHB_penSetColor(JNIEnv* e, jclass c, jlong p, jint col) {
    jobject paint = (*e)->CallStaticObjectMethod(e, hostClass, getPenMethod, (jint)p);
    if (paint) (*e)->CallVoidMethod(e, paint, paint_setColor, col);
}
static void OHB_penSetWidth(JNIEnv* e, jclass c, jlong p, jfloat w) {
    jobject paint = (*e)->CallStaticObjectMethod(e, hostClass, getPenMethod, (jint)p);
    if (paint) (*e)->CallVoidMethod(e, paint, paint_setStrokeWidth, w);
}
static void OHB_penSetAntiAlias(JNIEnv* e, jclass c, jlong p, jboolean aa) {}
static void OHB_penSetCap(JNIEnv* e, jclass c, jlong p, jint cap) {}
static void OHB_penSetJoin(JNIEnv* e, jclass c, jlong p, jint j) {}
static void OHB_penDestroy(JNIEnv* e, jclass c, jlong p) {}

/* Brush */
static jlong OHB_brushCreate(JNIEnv* e, jclass c) { return (*e)->CallStaticIntMethod(e, hostClass, createBrushMethod); }
static void OHB_brushSetColor(JNIEnv* e, jclass c, jlong b, jint col) {
    jobject paint = (*e)->CallStaticObjectMethod(e, hostClass, getBrushMethod, (jint)b);
    if (paint) (*e)->CallVoidMethod(e, paint, paint_setColor, col);
}
static void OHB_brushSetAntiAlias(JNIEnv* e, jclass c, jlong b, jboolean aa) {}
static void OHB_brushDestroy(JNIEnv* e, jclass c, jlong b) {}

/* Font */
static jlong OHB_fontCreate(JNIEnv* e, jclass c) { return (*e)->CallStaticIntMethod(e, hostClass, createFontMethod); }
static void OHB_fontSetSize(JNIEnv* e, jclass c, jlong f, jfloat sz) {
    jobject paint = (*e)->CallStaticObjectMethod(e, hostClass, getFontMethod, (jint)f);
    if (paint) (*e)->CallVoidMethod(e, paint, paint_setTextSize, sz);
}
static jfloat OHB_fontMeasureText(JNIEnv* e, jclass c, jlong f, jstring s) {
    if (!s) return 0;
    jobject paint = (*e)->CallStaticObjectMethod(e, hostClass, getFontMethod, (jint)f);
    if (!paint) return 0;
    return (*e)->CallFloatMethod(e, paint, paint_measureText, s);
}
static void OHB_fontDestroy(JNIEnv* e, jclass c, jlong f) {}
static jfloatArray OHB_fontGetMetrics(JNIEnv* e, jclass c, jlong f) {
    jobject paint = (*e)->CallStaticObjectMethod(e, hostClass, getFontMethod, (jint)f);
    jfloatArray arr = (*e)->NewFloatArray(e, 4);
    if (paint) {
        jobject fm = (*e)->CallObjectMethod(e, paint, paint_getFontMetrics);
        if (fm) {
            jfloat m[4];
            m[0] = (*e)->GetFloatField(e, fm, fm_ascent);
            m[1] = (*e)->GetFloatField(e, fm, fm_descent);
            m[2] = 0; /* leading */
            m[3] = m[1] - m[0]; /* height */
            (*e)->SetFloatArrayRegion(e, arr, 0, 4, m);
        }
    }
    return arr;
}

/* Bitmap stubs */
static jlong OHB_bitmapCreate(JNIEnv* e, jclass c, jint w, jint h, jint fmt) { return 1; }
static void OHB_bitmapDestroy(JNIEnv* e, jclass c, jlong b) {}
static jint OHB_bitmapGetWidth(JNIEnv* e, jclass c, jlong b) { return 480; }
static jint OHB_bitmapGetHeight(JNIEnv* e, jclass c, jlong b) { return 800; }
static void OHB_bitmapSetPixel(JNIEnv* e, jclass c, jlong b, jint x, jint y, jint col) {}
static jint OHB_bitmapGetPixel(JNIEnv* e, jclass c, jlong b, jint x, jint y) { return 0; }
static jint OHB_bitmapWriteToFile(JNIEnv* e, jclass c, jlong b, jstring p) { return 0; }
static jint OHB_bitmapBlitToFb0(JNIEnv* e, jclass c, jlong b, jint off) { return 0; }

/* Path */
static jlong OHB_pathCreate(JNIEnv* e, jclass c) { return (*e)->CallStaticIntMethod(e, hostClass, createPathMethod); }
static void OHB_pathMoveTo(JNIEnv* e, jclass c, jlong p, jfloat x, jfloat y) {
    jobject path = (*e)->CallStaticObjectMethod(e, hostClass, getPathMethod, (jint)p);
    if (path) (*e)->CallVoidMethod(e, path, path_moveTo, x, y);
}
static void OHB_pathLineTo(JNIEnv* e, jclass c, jlong p, jfloat x, jfloat y) {
    jobject path = (*e)->CallStaticObjectMethod(e, hostClass, getPathMethod, (jint)p);
    if (path) (*e)->CallVoidMethod(e, path, path_lineTo, x, y);
}
static void OHB_pathClose(JNIEnv* e, jclass c, jlong p) {
    jobject path = (*e)->CallStaticObjectMethod(e, hostClass, getPathMethod, (jint)p);
    if (path) (*e)->CallVoidMethod(e, path, path_close);
}
static void OHB_pathDestroy(JNIEnv* e, jclass c, jlong p) {}
static void OHB_pathCubicTo(JNIEnv* e, jclass c, jlong p, jfloat x1, jfloat y1, jfloat x2, jfloat y2, jfloat x3, jfloat y3) {
    jobject path = (*e)->CallStaticObjectMethod(e, hostClass, getPathMethod, (jint)p);
    if (path) (*e)->CallVoidMethod(e, path, path_cubicTo, x1, y1, x2, y2, x3, y3);
}
static void OHB_pathQuadTo(JNIEnv* e, jclass c, jlong p, jfloat x1, jfloat y1, jfloat x2, jfloat y2) {
    jobject path = (*e)->CallStaticObjectMethod(e, hostClass, getPathMethod, (jint)p);
    if (path) (*e)->CallVoidMethod(e, path, path_quadTo, x1, y1, x2, y2);
}
static void OHB_pathAddRect(JNIEnv* e, jclass c, jlong p, jfloat l, jfloat t, jfloat r, jfloat b, jint d) {}
static void OHB_pathAddCircle(JNIEnv* e, jclass c, jlong p, jfloat cx, jfloat cy, jfloat r, jint d) {}
static void OHB_pathReset(JNIEnv* e, jclass c, jlong p) {
    jobject path = (*e)->CallStaticObjectMethod(e, hostClass, getPathMethod, (jint)p);
    if (path) (*e)->CallVoidMethod(e, path, path_reset);
}

/* Preferences stubs */
static jlong OHB_preferencesOpen(JNIEnv* e, jclass c, jstring n) { return 1; }
static jstring OHB_preferencesGetString(JNIEnv* e, jclass c, jlong h, jstring k, jstring d) { return d; }
static jint OHB_preferencesGetInt(JNIEnv* e, jclass c, jlong h, jstring k, jint d) { return d; }
static jlong OHB_preferencesGetLong(JNIEnv* e, jclass c, jlong h, jstring k, jlong d) { return d; }
static jfloat OHB_preferencesGetFloat(JNIEnv* e, jclass c, jlong h, jstring k, jfloat d) { return d; }
static jboolean OHB_preferencesGetBoolean(JNIEnv* e, jclass c, jlong h, jstring k, jboolean d) { return d; }
static void OHB_preferencesPutString(JNIEnv* e, jclass c, jlong h, jstring k, jstring v) {}
static void OHB_preferencesPutInt(JNIEnv* e, jclass c, jlong h, jstring k, jint v) {}
static void OHB_preferencesPutLong(JNIEnv* e, jclass c, jlong h, jstring k, jlong v) {}
static void OHB_preferencesPutFloat(JNIEnv* e, jclass c, jlong h, jstring k, jfloat v) {}
static void OHB_preferencesPutBoolean(JNIEnv* e, jclass c, jlong h, jstring k, jboolean v) {}
static void OHB_preferencesFlush(JNIEnv* e, jclass c, jlong h) {}
static void OHB_preferencesRemove(JNIEnv* e, jclass c, jlong h, jstring k) {}
static void OHB_preferencesClear(JNIEnv* e, jclass c, jlong h) {}
static void OHB_preferencesClose(JNIEnv* e, jclass c, jlong h) {}

/* RDB stubs */
static jlong OHB_rdbStoreOpen(JNIEnv* e, jclass c, jstring n, jint v) { return 1; }
static void OHB_rdbStoreExecSQL(JNIEnv* e, jclass c, jlong h, jstring sql) {}
static jlong OHB_rdbStoreQuery(JNIEnv* e, jclass c, jlong h, jstring sql, jobjectArray a) { return 0; }
static jlong OHB_rdbStoreInsert(JNIEnv* e, jclass c, jlong h, jstring t, jstring j) { return 1; }
static jint OHB_rdbStoreUpdate(JNIEnv* e, jclass c, jlong h, jstring j, jstring t, jstring w, jobjectArray a) { return 0; }
static jint OHB_rdbStoreDelete(JNIEnv* e, jclass c, jlong h, jstring t, jstring w, jobjectArray a) { return 0; }
static void OHB_rdbStoreBeginTransaction(JNIEnv* e, jclass c, jlong h) {}
static void OHB_rdbStoreCommit(JNIEnv* e, jclass c, jlong h) {}
static void OHB_rdbStoreRollback(JNIEnv* e, jclass c, jlong h) {}
static void OHB_rdbStoreClose(JNIEnv* e, jclass c, jlong h) {}
static jboolean OHB_resultSetGoToFirstRow(JNIEnv* e, jclass c, jlong h) { return JNI_FALSE; }
static jboolean OHB_resultSetGoToNextRow(JNIEnv* e, jclass c, jlong h) { return JNI_FALSE; }
static jint OHB_resultSetGetColumnIndex(JNIEnv* e, jclass c, jlong h, jstring n) { return -1; }
static jstring OHB_resultSetGetString(JNIEnv* e, jclass c, jlong h, jint i) { return (*e)->NewStringUTF(e, ""); }
static jint OHB_resultSetGetInt(JNIEnv* e, jclass c, jlong h, jint i) { return 0; }
static jlong OHB_resultSetGetLong(JNIEnv* e, jclass c, jlong h, jint i) { return 0; }
static jfloat OHB_resultSetGetFloat(JNIEnv* e, jclass c, jlong h, jint i) { return 0; }
static jdouble OHB_resultSetGetDouble(JNIEnv* e, jclass c, jlong h, jint i) { return 0; }
static jbyteArray OHB_resultSetGetBlob(JNIEnv* e, jclass c, jlong h, jint i) { return NULL; }
static jint OHB_resultSetGetColumnCount(JNIEnv* e, jclass c, jlong h) { return 0; }
static jstring OHB_resultSetGetColumnName(JNIEnv* e, jclass c, jlong h, jint i) { return (*e)->NewStringUTF(e, ""); }
static jint OHB_resultSetGetRowCount(JNIEnv* e, jclass c, jlong h) { return 0; }
static jboolean OHB_resultSetIsNull(JNIEnv* e, jclass c, jlong h, jint i) { return JNI_TRUE; }
static void OHB_resultSetClose(JNIEnv* e, jclass c, jlong h) {}

/* Misc stubs */
static void OHB_showToast(JNIEnv* e, jclass c, jstring m, jint d) {}
static jint OHB_checkPermission(JNIEnv* e, jclass c, jstring p) { return 0; }
static jstring OHB_getDeviceBrand(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e, "Westlake"); }
static jstring OHB_getDeviceModel(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e, "Phone"); }
static jstring OHB_getOSVersion(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e, "1.0"); }
static jint OHB_getNetworkType(JNIEnv* e, jclass c) { return 0; }
static void OHB_startAbility(JNIEnv* e, jclass c, jstring a, jstring b, jstring d) {}
static void OHB_terminateSelf(JNIEnv* e, jclass c) {}
static jstring OHB_httpRequest(JNIEnv* e, jclass c, jstring u, jstring m, jstring h, jstring b) { return NULL; }
static void OHB_clipboardSet(JNIEnv* e, jclass c, jstring t) {}
static jstring OHB_clipboardGet(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e, ""); }
static jboolean OHB_vibratorHasVibrator(JNIEnv* e, jclass c) { return JNI_FALSE; }
static void OHB_vibratorVibrate(JNIEnv* e, jclass c, jlong ms) {}
static void OHB_vibratorCancel(JNIEnv* e, jclass c) {}
static jboolean OHB_locationIsEnabled(JNIEnv* e, jclass c) { return JNI_FALSE; }
static jdoubleArray OHB_locationGetLast(JNIEnv* e, jclass c) { return NULL; }
static jboolean OHB_sensorIsAvailable(JNIEnv* e, jclass c, jint t) { return JNI_FALSE; }
static jfloatArray OHB_sensorGetData(JNIEnv* e, jclass c, jint t) { return NULL; }
static jlong OHB_mediaPlayerCreate(JNIEnv* e, jclass c) { return 0; }
static void OHB_mediaPlayerSetDataSource(JNIEnv* e, jclass c, jlong h, jstring p) {}
static void OHB_mediaPlayerPrepare(JNIEnv* e, jclass c, jlong h) {}
static void OHB_mediaPlayerStart(JNIEnv* e, jclass c, jlong h) {}
static void OHB_mediaPlayerPause(JNIEnv* e, jclass c, jlong h) {}
static void OHB_mediaPlayerStop(JNIEnv* e, jclass c, jlong h) {}
static void OHB_mediaPlayerRelease(JNIEnv* e, jclass c, jlong h) {}
static void OHB_mediaPlayerReset(JNIEnv* e, jclass c, jlong h) {}
static void OHB_mediaPlayerSeekTo(JNIEnv* e, jclass c, jlong h, jint ms) {}
static jint OHB_mediaPlayerGetCurrentPosition(JNIEnv* e, jclass c, jlong h) { return 0; }
static jint OHB_mediaPlayerGetDuration(JNIEnv* e, jclass c, jlong h) { return 0; }
static jboolean OHB_mediaPlayerIsPlaying(JNIEnv* e, jclass c, jlong h) { return JNI_FALSE; }
static void OHB_mediaPlayerSetLooping(JNIEnv* e, jclass c, jlong h, jboolean l) {}
static void OHB_mediaPlayerSetVolume(JNIEnv* e, jclass c, jlong h, jfloat lv, jfloat rv) {}
static jint OHB_audioGetStreamVolume(JNIEnv* e, jclass c, jint s) { return 50; }
static jint OHB_audioGetStreamMaxVolume(JNIEnv* e, jclass c, jint s) { return 100; }
static void OHB_audioSetStreamVolume(JNIEnv* e, jclass c, jint s, jint v, jint f) {}
static jint OHB_audioGetRingerMode(JNIEnv* e, jclass c) { return 2; }
static void OHB_audioSetRingerMode(JNIEnv* e, jclass c, jint m) {}
static jboolean OHB_audioIsMusicActive(JNIEnv* e, jclass c) { return JNI_FALSE; }
static jint OHB_telephonyGetPhoneType(JNIEnv* e, jclass c) { return 0; }
static jint OHB_telephonyGetNetworkType(JNIEnv* e, jclass c) { return 0; }
static jint OHB_telephonyGetSimState(JNIEnv* e, jclass c) { return 0; }
static jstring OHB_telephonyGetDeviceId(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e, ""); }
static jstring OHB_telephonyGetLine1Number(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e, ""); }
static jstring OHB_telephonyGetNetworkOperatorName(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e, ""); }
static jboolean OHB_wifiIsEnabled(JNIEnv* e, jclass c) { return JNI_FALSE; }
static jboolean OHB_wifiSetEnabled(JNIEnv* e, jclass c, jboolean en) { return JNI_FALSE; }
static jint OHB_wifiGetState(JNIEnv* e, jclass c) { return 0; }
static jstring OHB_wifiGetSSID(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e, ""); }
static jint OHB_wifiGetRssi(JNIEnv* e, jclass c) { return -50; }
static jint OHB_wifiGetLinkSpeed(JNIEnv* e, jclass c) { return 0; }
static jint OHB_wifiGetFrequency(JNIEnv* e, jclass c) { return 0; }
static void OHB_notificationPublish(JNIEnv* e, jclass c, jint id, jstring ch, jstring t, jstring tx, jint i) {}
static void OHB_notificationCancel(JNIEnv* e, jclass c, jint id) {}
static void OHB_notificationAddSlot(JNIEnv* e, jclass c, jstring id, jstring n, jint imp) {}
static jint OHB_reminderScheduleTimer(JNIEnv* e, jclass c, jint id, jstring t, jstring tx, jstring ch, jstring d) { return 0; }
static void OHB_reminderCancel(JNIEnv* e, jclass c, jint id) {}
static jlong OHB_nodeCreate(JNIEnv* e, jclass c, jint t) { return 0; }
static void OHB_nodeDispose(JNIEnv* e, jclass c, jlong n) {}
static void OHB_nodeAddChild(JNIEnv* e, jclass c, jlong p, jlong ch) {}
static void OHB_nodeRemoveChild(JNIEnv* e, jclass c, jlong p, jlong ch) {}
static void OHB_nodeInsertChildAt(JNIEnv* e, jclass c, jlong p, jlong ch, jint i) {}
static jint OHB_nodeSetAttrInt(JNIEnv* e, jclass c, jlong n, jint a, jint v) { return 0; }
static jint OHB_nodeSetAttrFloat(JNIEnv* e, jclass c, jlong n, jint a, jfloat v1, jfloat v2, jfloat v3, jfloat v4, jint u) { return 0; }
static jint OHB_nodeSetAttrColor(JNIEnv* e, jclass c, jlong n, jint a, jint col) { return 0; }
static jint OHB_nodeSetAttrString(JNIEnv* e, jclass c, jlong n, jint a, jstring v) { return 0; }
static void OHB_nodeMarkDirty(JNIEnv* e, jclass c, jlong n, jint f) {}
static jint OHB_nodeRegisterEvent(JNIEnv* e, jclass c, jlong n, jint ev, jint id) { return 0; }
static void OHB_nodeUnregisterEvent(JNIEnv* e, jclass c, jlong n, jint ev) {}

/* ══════ JNI_OnLoad — registers OHBridge methods in the shim's classloader ══════ */

/* This is called by WestlakeHostActivity's System.loadLibrary("ohbridge_android").
 * But the OHBridge class is in the DexClassLoader, not the system classloader.
 * So we can't FindClass here — we register lazily when arkuiInit() is called
 * from within the DexClassLoader's context. */

static int g_registered = 0;

static void registerIfNeeded(JNIEnv* env) {
    if (g_registered) return;
    jclass cls = (*env)->FindClass(env, "com/ohos/shim/bridge/OHBridge");
    if (!cls) {
        LOGE("OHBridge class not found — will register when available");
        return;
    }
    LOGI("Registering OHBridge native methods...");
    /* This is the same registration table as ohbridge_stub.c */
    JNINativeMethod m[] = {
        {"logDebug","(Ljava/lang/String;Ljava/lang/String;)V",(void*)OHB_logDebug},
        {"logInfo","(Ljava/lang/String;Ljava/lang/String;)V",(void*)OHB_logInfo},
        {"logWarn","(Ljava/lang/String;Ljava/lang/String;)V",(void*)OHB_logWarn},
        {"logError","(Ljava/lang/String;Ljava/lang/String;)V",(void*)OHB_logError},
        {"arkuiInit","()I",(void*)OHB_arkuiInit},
        {"surfaceCreate","(JII)J",(void*)OHB_surfaceCreate},
        {"surfaceGetCanvas","(J)J",(void*)OHB_surfaceGetCanvas},
        {"surfaceFlush","(J)I",(void*)OHB_surfaceFlush},
        {"surfaceDestroy","(J)V",(void*)OHB_surfaceDestroy},
        {"surfaceResize","(JII)V",(void*)OHB_surfaceResize},
        {"canvasCreate","(J)J",(void*)OHB_canvasCreate},
        {"canvasDestroy","(J)V",(void*)OHB_canvasDestroy},
        {"canvasDrawColor","(JI)V",(void*)OHB_canvasDrawColor},
        {"canvasDrawRect","(JFFFFJJ)V",(void*)OHB_canvasDrawRect},
        {"canvasDrawRoundRect","(JFFFFFFJJ)V",(void*)OHB_canvasDrawRoundRect},
        {"canvasDrawCircle","(JFFFJJ)V",(void*)OHB_canvasDrawCircle},
        {"canvasDrawLine","(JFFFFJ)V",(void*)OHB_canvasDrawLine},
        {"canvasDrawText","(JLjava/lang/String;FFJJJ)V",(void*)OHB_canvasDrawText},
        {"canvasSave","(J)V",(void*)OHB_canvasSave},
        {"canvasRestore","(J)V",(void*)OHB_canvasRestore},
        {"canvasTranslate","(JFF)V",(void*)OHB_canvasTranslate},
        {"canvasScale","(JFF)V",(void*)OHB_canvasScale},
        {"canvasClipRect","(JFFFF)V",(void*)OHB_canvasClipRect},
        {"canvasRotate","(JFFF)V",(void*)OHB_canvasRotate},
        {"canvasClipPath","(JJ)V",(void*)OHB_canvasClipPath},
        {"canvasConcat","(J[F)V",(void*)OHB_canvasConcat},
        {"canvasDrawOval","(JFFFFJJ)V",(void*)OHB_canvasDrawOval},
        {"canvasDrawArc","(JFFFFFFZJJ)V",(void*)OHB_canvasDrawArc},
        {"canvasDrawPath","(JJJJ)V",(void*)OHB_canvasDrawPath},
        {"canvasDrawBitmap","(JJFF)V",(void*)OHB_canvasDrawBitmap},
        {"penCreate","()J",(void*)OHB_penCreate},
        {"penSetColor","(JI)V",(void*)OHB_penSetColor},
        {"penSetWidth","(JF)V",(void*)OHB_penSetWidth},
        {"penSetAntiAlias","(JZ)V",(void*)OHB_penSetAntiAlias},
        {"penSetCap","(JI)V",(void*)OHB_penSetCap},
        {"penSetJoin","(JI)V",(void*)OHB_penSetJoin},
        {"penDestroy","(J)V",(void*)OHB_penDestroy},
        {"brushCreate","()J",(void*)OHB_brushCreate},
        {"brushSetColor","(JI)V",(void*)OHB_brushSetColor},
        {"brushSetAntiAlias","(JZ)V",(void*)OHB_brushSetAntiAlias},
        {"brushDestroy","(J)V",(void*)OHB_brushDestroy},
        {"fontCreate","()J",(void*)OHB_fontCreate},
        {"fontSetSize","(JF)V",(void*)OHB_fontSetSize},
        {"fontMeasureText","(JLjava/lang/String;)F",(void*)OHB_fontMeasureText},
        {"fontDestroy","(J)V",(void*)OHB_fontDestroy},
        {"fontGetMetrics","(J)[F",(void*)OHB_fontGetMetrics},
        {"bitmapCreate","(III)J",(void*)OHB_bitmapCreate},
        {"bitmapDestroy","(J)V",(void*)OHB_bitmapDestroy},
        {"bitmapGetWidth","(J)I",(void*)OHB_bitmapGetWidth},
        {"bitmapGetHeight","(J)I",(void*)OHB_bitmapGetHeight},
        {"bitmapSetPixel","(JIII)V",(void*)OHB_bitmapSetPixel},
        {"bitmapGetPixel","(JII)I",(void*)OHB_bitmapGetPixel},
        {"bitmapWriteToFile","(JLjava/lang/String;)I",(void*)OHB_bitmapWriteToFile},
        {"bitmapBlitToFb0","(JI)I",(void*)OHB_bitmapBlitToFb0},
        {"pathCreate","()J",(void*)OHB_pathCreate},
        {"pathMoveTo","(JFF)V",(void*)OHB_pathMoveTo},
        {"pathLineTo","(JFF)V",(void*)OHB_pathLineTo},
        {"pathClose","(J)V",(void*)OHB_pathClose},
        {"pathDestroy","(J)V",(void*)OHB_pathDestroy},
        {"pathCubicTo","(JFFFFFF)V",(void*)OHB_pathCubicTo},
        {"pathQuadTo","(JFFFF)V",(void*)OHB_pathQuadTo},
        {"pathAddRect","(JFFFFI)V",(void*)OHB_pathAddRect},
        {"pathAddCircle","(JFFFI)V",(void*)OHB_pathAddCircle},
        {"pathReset","(J)V",(void*)OHB_pathReset},
        {"preferencesOpen","(Ljava/lang/String;)J",(void*)OHB_preferencesOpen},
        {"preferencesGetString","(JLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;",(void*)OHB_preferencesGetString},
        {"preferencesGetInt","(JLjava/lang/String;I)I",(void*)OHB_preferencesGetInt},
        {"preferencesGetLong","(JLjava/lang/String;J)J",(void*)OHB_preferencesGetLong},
        {"preferencesGetFloat","(JLjava/lang/String;F)F",(void*)OHB_preferencesGetFloat},
        {"preferencesGetBoolean","(JLjava/lang/String;Z)Z",(void*)OHB_preferencesGetBoolean},
        {"preferencesPutString","(JLjava/lang/String;Ljava/lang/String;)V",(void*)OHB_preferencesPutString},
        {"preferencesPutInt","(JLjava/lang/String;I)V",(void*)OHB_preferencesPutInt},
        {"preferencesPutLong","(JLjava/lang/String;J)V",(void*)OHB_preferencesPutLong},
        {"preferencesPutFloat","(JLjava/lang/String;F)V",(void*)OHB_preferencesPutFloat},
        {"preferencesPutBoolean","(JLjava/lang/String;Z)V",(void*)OHB_preferencesPutBoolean},
        {"preferencesFlush","(J)V",(void*)OHB_preferencesFlush},
        {"preferencesRemove","(JLjava/lang/String;)V",(void*)OHB_preferencesRemove},
        {"preferencesClear","(J)V",(void*)OHB_preferencesClear},
        {"preferencesClose","(J)V",(void*)OHB_preferencesClose},
        {"rdbStoreOpen","(Ljava/lang/String;I)J",(void*)OHB_rdbStoreOpen},
        {"rdbStoreExecSQL","(JLjava/lang/String;)V",(void*)OHB_rdbStoreExecSQL},
        {"rdbStoreQuery","(JLjava/lang/String;[Ljava/lang/String;)J",(void*)OHB_rdbStoreQuery},
        {"rdbStoreInsert","(JLjava/lang/String;Ljava/lang/String;)J",(void*)OHB_rdbStoreInsert},
        {"rdbStoreUpdate","(JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I",(void*)OHB_rdbStoreUpdate},
        {"rdbStoreDelete","(JLjava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I",(void*)OHB_rdbStoreDelete},
        {"rdbStoreBeginTransaction","(J)V",(void*)OHB_rdbStoreBeginTransaction},
        {"rdbStoreCommit","(J)V",(void*)OHB_rdbStoreCommit},
        {"rdbStoreRollback","(J)V",(void*)OHB_rdbStoreRollback},
        {"rdbStoreClose","(J)V",(void*)OHB_rdbStoreClose},
        {"resultSetGoToFirstRow","(J)Z",(void*)OHB_resultSetGoToFirstRow},
        {"resultSetGoToNextRow","(J)Z",(void*)OHB_resultSetGoToNextRow},
        {"resultSetGetColumnIndex","(JLjava/lang/String;)I",(void*)OHB_resultSetGetColumnIndex},
        {"resultSetGetString","(JI)Ljava/lang/String;",(void*)OHB_resultSetGetString},
        {"resultSetGetInt","(JI)I",(void*)OHB_resultSetGetInt},
        {"resultSetGetLong","(JI)J",(void*)OHB_resultSetGetLong},
        {"resultSetGetFloat","(JI)F",(void*)OHB_resultSetGetFloat},
        {"resultSetGetDouble","(JI)D",(void*)OHB_resultSetGetDouble},
        {"resultSetGetBlob","(JI)[B",(void*)OHB_resultSetGetBlob},
        {"resultSetGetColumnCount","(J)I",(void*)OHB_resultSetGetColumnCount},
        {"resultSetGetColumnName","(JI)Ljava/lang/String;",(void*)OHB_resultSetGetColumnName},
        {"resultSetGetRowCount","(J)I",(void*)OHB_resultSetGetRowCount},
        {"resultSetIsNull","(JI)Z",(void*)OHB_resultSetIsNull},
        {"resultSetClose","(J)V",(void*)OHB_resultSetClose},
        {"showToast","(Ljava/lang/String;I)V",(void*)OHB_showToast},
        {"checkPermission","(Ljava/lang/String;)I",(void*)OHB_checkPermission},
        {"getDeviceBrand","()Ljava/lang/String;",(void*)OHB_getDeviceBrand},
        {"getDeviceModel","()Ljava/lang/String;",(void*)OHB_getDeviceModel},
        {"getOSVersion","()Ljava/lang/String;",(void*)OHB_getOSVersion},
        {"getNetworkType","()I",(void*)OHB_getNetworkType},
        {"startAbility","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",(void*)OHB_startAbility},
        {"terminateSelf","()V",(void*)OHB_terminateSelf},
        {"httpRequest","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",(void*)OHB_httpRequest},
        {"clipboardSet","(Ljava/lang/String;)V",(void*)OHB_clipboardSet},
        {"clipboardGet","()Ljava/lang/String;",(void*)OHB_clipboardGet},
        {"vibratorHasVibrator","()Z",(void*)OHB_vibratorHasVibrator},
        {"vibratorVibrate","(J)V",(void*)OHB_vibratorVibrate},
        {"vibratorCancel","()V",(void*)OHB_vibratorCancel},
        {"locationIsEnabled","()Z",(void*)OHB_locationIsEnabled},
        {"locationGetLast","()[D",(void*)OHB_locationGetLast},
        {"sensorIsAvailable","(I)Z",(void*)OHB_sensorIsAvailable},
        {"sensorGetData","(I)[F",(void*)OHB_sensorGetData},
        {"mediaPlayerCreate","()J",(void*)OHB_mediaPlayerCreate},
        {"mediaPlayerSetDataSource","(JLjava/lang/String;)V",(void*)OHB_mediaPlayerSetDataSource},
        {"mediaPlayerPrepare","(J)V",(void*)OHB_mediaPlayerPrepare},
        {"mediaPlayerStart","(J)V",(void*)OHB_mediaPlayerStart},
        {"mediaPlayerPause","(J)V",(void*)OHB_mediaPlayerPause},
        {"mediaPlayerStop","(J)V",(void*)OHB_mediaPlayerStop},
        {"mediaPlayerRelease","(J)V",(void*)OHB_mediaPlayerRelease},
        {"mediaPlayerReset","(J)V",(void*)OHB_mediaPlayerReset},
        {"mediaPlayerSeekTo","(JI)V",(void*)OHB_mediaPlayerSeekTo},
        {"mediaPlayerGetCurrentPosition","(J)I",(void*)OHB_mediaPlayerGetCurrentPosition},
        {"mediaPlayerGetDuration","(J)I",(void*)OHB_mediaPlayerGetDuration},
        {"mediaPlayerIsPlaying","(J)Z",(void*)OHB_mediaPlayerIsPlaying},
        {"mediaPlayerSetLooping","(JZ)V",(void*)OHB_mediaPlayerSetLooping},
        {"mediaPlayerSetVolume","(JFF)V",(void*)OHB_mediaPlayerSetVolume},
        {"audioGetStreamVolume","(I)I",(void*)OHB_audioGetStreamVolume},
        {"audioGetStreamMaxVolume","(I)I",(void*)OHB_audioGetStreamMaxVolume},
        {"audioSetStreamVolume","(III)V",(void*)OHB_audioSetStreamVolume},
        {"audioGetRingerMode","()I",(void*)OHB_audioGetRingerMode},
        {"audioSetRingerMode","(I)V",(void*)OHB_audioSetRingerMode},
        {"audioIsMusicActive","()Z",(void*)OHB_audioIsMusicActive},
        {"telephonyGetPhoneType","()I",(void*)OHB_telephonyGetPhoneType},
        {"telephonyGetNetworkType","()I",(void*)OHB_telephonyGetNetworkType},
        {"telephonyGetSimState","()I",(void*)OHB_telephonyGetSimState},
        {"telephonyGetDeviceId","()Ljava/lang/String;",(void*)OHB_telephonyGetDeviceId},
        {"telephonyGetLine1Number","()Ljava/lang/String;",(void*)OHB_telephonyGetLine1Number},
        {"telephonyGetNetworkOperatorName","()Ljava/lang/String;",(void*)OHB_telephonyGetNetworkOperatorName},
        {"wifiIsEnabled","()Z",(void*)OHB_wifiIsEnabled},
        {"wifiSetEnabled","(Z)Z",(void*)OHB_wifiSetEnabled},
        {"wifiGetState","()I",(void*)OHB_wifiGetState},
        {"wifiGetSSID","()Ljava/lang/String;",(void*)OHB_wifiGetSSID},
        {"wifiGetRssi","()I",(void*)OHB_wifiGetRssi},
        {"wifiGetLinkSpeed","()I",(void*)OHB_wifiGetLinkSpeed},
        {"wifiGetFrequency","()I",(void*)OHB_wifiGetFrequency},
        {"notificationPublish","(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V",(void*)OHB_notificationPublish},
        {"notificationCancel","(I)V",(void*)OHB_notificationCancel},
        {"notificationAddSlot","(Ljava/lang/String;Ljava/lang/String;I)V",(void*)OHB_notificationAddSlot},
        {"reminderScheduleTimer","(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I",(void*)OHB_reminderScheduleTimer},
        {"reminderCancel","(I)V",(void*)OHB_reminderCancel},
        {"nodeCreate","(I)J",(void*)OHB_nodeCreate},
        {"nodeDispose","(J)V",(void*)OHB_nodeDispose},
        {"nodeAddChild","(JJ)V",(void*)OHB_nodeAddChild},
        {"nodeRemoveChild","(JJ)V",(void*)OHB_nodeRemoveChild},
        {"nodeInsertChildAt","(JJI)V",(void*)OHB_nodeInsertChildAt},
        {"nodeSetAttrInt","(JII)I",(void*)OHB_nodeSetAttrInt},
        {"nodeSetAttrFloat","(JIFFFFI)I",(void*)OHB_nodeSetAttrFloat},
        {"nodeSetAttrColor","(JII)I",(void*)OHB_nodeSetAttrColor},
        {"nodeSetAttrString","(JILjava/lang/String;)I",(void*)OHB_nodeSetAttrString},
        {"nodeMarkDirty","(JI)V",(void*)OHB_nodeMarkDirty},
        {"nodeRegisterEvent","(JII)I",(void*)OHB_nodeRegisterEvent},
        {"nodeUnregisterEvent","(JI)V",(void*)OHB_nodeUnregisterEvent},
    };
    int n = sizeof(m) / sizeof(m[0]);
    /* Register one at a time to skip mismatches */
    int ok = 0;
    for (int i = 0; i < n; i++) {
        if ((*env)->RegisterNatives(env, cls, &m[i], 1) == 0) ok++;
        else (*env)->ExceptionClear(env);
    }
    LOGI("Registered %d/%d OHBridge methods", ok, n);
    g_registered = 1;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    g_vm = vm;
    JNIEnv* env;
    if ((*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_6) != JNI_OK) return -1;
    /* OHBridge class isn't available yet (loaded later via DexClassLoader) */
    /* Registration happens lazily in arkuiInit() */
    LOGI("libohbridge_android loaded");
    return JNI_VERSION_1_6;
}
