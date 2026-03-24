/*
 * OHBridge native — draws to Android Canvas via JNI callbacks to WestlakeActivity.
 * No file I/O, no PNG, no polling — direct rendering.
 */
#include <jni.h>
#include <android/log.h>
#include <string.h>
#include <math.h>

#define TAG "OHBridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

static JavaVM* g_vm = NULL;
static jclass hostClass = NULL;
static jmethodID beginFrame, endFrame, newPaintM, getPaintM, newPathM, getPathM;
static jclass canvasClass = NULL;
static jmethodID c_drawColor, c_drawRect, c_drawRoundRect, c_drawCircle;
static jmethodID c_drawLine, c_drawText, c_drawPath, c_save, c_restore;
static jmethodID c_translate, c_scale, c_clipRect;
static jclass paintClass = NULL;
static jmethodID p_setColor, p_setStrokeWidth, p_setTextSize, p_setAlpha;
static jmethodID p_measureText, p_getFontMetrics;
static jfieldID fm_ascent, fm_descent;
static jclass pathClass = NULL;
static jmethodID path_moveTo, path_lineTo, path_close, path_reset;
static jfieldID hostCanvas;

static void cache(JNIEnv* e) {
    if (hostClass) return;
    hostClass = (*e)->NewGlobalRef(e, (*e)->FindClass(e, "com/westlake/host/WestlakeActivity"));
    beginFrame = (*e)->GetStaticMethodID(e, hostClass, "beginFrame", "()V");
    endFrame = (*e)->GetStaticMethodID(e, hostClass, "endFrame", "()V");
    newPaintM = (*e)->GetStaticMethodID(e, hostClass, "newPaint", "(I)I");
    getPaintM = (*e)->GetStaticMethodID(e, hostClass, "getPaint", "(I)Landroid/graphics/Paint;");
    newPathM = (*e)->GetStaticMethodID(e, hostClass, "newPath", "()I");
    getPathM = (*e)->GetStaticMethodID(e, hostClass, "getPath", "(I)Landroid/graphics/Path;");
    hostCanvas = (*e)->GetStaticFieldID(e, hostClass, "currentCanvas", "Landroid/graphics/Canvas;");

    canvasClass = (*e)->NewGlobalRef(e, (*e)->FindClass(e, "android/graphics/Canvas"));
    c_drawColor = (*e)->GetMethodID(e, canvasClass, "drawColor", "(I)V");
    c_drawRect = (*e)->GetMethodID(e, canvasClass, "drawRect", "(FFFFLandroid/graphics/Paint;)V");
    c_drawRoundRect = (*e)->GetMethodID(e, canvasClass, "drawRoundRect", "(FFFFFFLandroid/graphics/Paint;)V");
    c_drawCircle = (*e)->GetMethodID(e, canvasClass, "drawCircle", "(FFFLandroid/graphics/Paint;)V");
    c_drawLine = (*e)->GetMethodID(e, canvasClass, "drawLine", "(FFFFLandroid/graphics/Paint;)V");
    c_drawText = (*e)->GetMethodID(e, canvasClass, "drawText", "(Ljava/lang/String;FFLandroid/graphics/Paint;)V");
    c_drawPath = (*e)->GetMethodID(e, canvasClass, "drawPath", "(Landroid/graphics/Path;Landroid/graphics/Paint;)V");
    c_save = (*e)->GetMethodID(e, canvasClass, "save", "()I");
    c_restore = (*e)->GetMethodID(e, canvasClass, "restore", "()V");
    c_translate = (*e)->GetMethodID(e, canvasClass, "translate", "(FF)V");
    c_scale = (*e)->GetMethodID(e, canvasClass, "scale", "(FF)V");
    c_clipRect = (*e)->GetMethodID(e, canvasClass, "clipRect", "(FFFF)Z");

    paintClass = (*e)->NewGlobalRef(e, (*e)->FindClass(e, "android/graphics/Paint"));
    p_setColor = (*e)->GetMethodID(e, paintClass, "setColor", "(I)V");
    p_setStrokeWidth = (*e)->GetMethodID(e, paintClass, "setStrokeWidth", "(F)V");
    p_setTextSize = (*e)->GetMethodID(e, paintClass, "setTextSize", "(F)V");
    p_setAlpha = (*e)->GetMethodID(e, paintClass, "setAlpha", "(I)V");
    p_measureText = (*e)->GetMethodID(e, paintClass, "measureText", "(Ljava/lang/String;)F");
    jclass fmClass = (*e)->FindClass(e, "android/graphics/Paint$FontMetrics");
    fm_ascent = (*e)->GetFieldID(e, fmClass, "ascent", "F");
    fm_descent = (*e)->GetFieldID(e, fmClass, "descent", "F");
    p_getFontMetrics = (*e)->GetMethodID(e, paintClass, "getFontMetrics", "()Landroid/graphics/Paint$FontMetrics;");

    pathClass = (*e)->NewGlobalRef(e, (*e)->FindClass(e, "android/graphics/Path"));
    path_moveTo = (*e)->GetMethodID(e, pathClass, "moveTo", "(FF)V");
    path_lineTo = (*e)->GetMethodID(e, pathClass, "lineTo", "(FF)V");
    path_close = (*e)->GetMethodID(e, pathClass, "close", "()V");
    path_reset = (*e)->GetMethodID(e, pathClass, "reset", "()V");
    LOGI("JNI cache ready");
}

static jobject getCanvas(JNIEnv* e) {
    return (*e)->GetStaticObjectField(e, hostClass, hostCanvas);
}

/* ═══ OHBridge implementations using Android Canvas ═══ */

static void OHB_logDebug(JNIEnv* e, jclass c, jstring t, jstring m) {
    const char* a=(*e)->GetStringUTFChars(e,t,0); const char* b=(*e)->GetStringUTFChars(e,m,0);
    __android_log_print(ANDROID_LOG_DEBUG, a, "%s", b);
    (*e)->ReleaseStringUTFChars(e,m,b); (*e)->ReleaseStringUTFChars(e,t,a);
}
static void OHB_logInfo(JNIEnv* e, jclass c, jstring t, jstring m) {
    const char* a=(*e)->GetStringUTFChars(e,t,0); const char* b=(*e)->GetStringUTFChars(e,m,0);
    __android_log_print(ANDROID_LOG_INFO, a, "%s", b);
    (*e)->ReleaseStringUTFChars(e,m,b); (*e)->ReleaseStringUTFChars(e,t,a);
}
static void OHB_logWarn(JNIEnv* e, jclass c, jstring t, jstring m) {
    const char* a=(*e)->GetStringUTFChars(e,t,0); const char* b=(*e)->GetStringUTFChars(e,m,0);
    __android_log_print(ANDROID_LOG_WARN, a, "%s", b);
    (*e)->ReleaseStringUTFChars(e,m,b); (*e)->ReleaseStringUTFChars(e,t,a);
}
static void OHB_logError(JNIEnv* e, jclass c, jstring t, jstring m) {
    const char* a=(*e)->GetStringUTFChars(e,t,0); const char* b=(*e)->GetStringUTFChars(e,m,0);
    __android_log_print(ANDROID_LOG_ERROR, a, "%s", b);
    (*e)->ReleaseStringUTFChars(e,m,b); (*e)->ReleaseStringUTFChars(e,t,a);
}

static jint OHB_arkuiInit(JNIEnv* e, jclass c) { cache(e); return 0; }

// Surface → Android SurfaceView
static jlong OHB_surfaceCreate(JNIEnv* e, jclass c, jlong u, jint w, jint h) { return 1; }
static jlong OHB_surfaceGetCanvas(JNIEnv* e, jclass c, jlong s) {
    (*e)->CallStaticVoidMethod(e, hostClass, beginFrame);
    return 1;
}
static jint OHB_surfaceFlush(JNIEnv* e, jclass c, jlong s) {
    (*e)->CallStaticVoidMethod(e, hostClass, endFrame);
    return 0;
}
static void OHB_surfaceDestroy(JNIEnv* e, jclass c, jlong s) {}
static void OHB_surfaceResize(JNIEnv* e, jclass c, jlong s, jint w, jint h) {}

// Canvas → Android Canvas
static jlong OHB_canvasCreate(JNIEnv* e, jclass c, jlong b) { return 1; }
static void OHB_canvasDestroy(JNIEnv* e, jclass c, jlong cn) {}
static void OHB_canvasDrawColor(JNIEnv* e, jclass c, jlong cn, jint col) {
    jobject cv = getCanvas(e); if(cv) (*e)->CallVoidMethod(e, cv, c_drawColor, col);
}
static void OHB_canvasDrawRect(JNIEnv* e, jclass c, jlong cn, jfloat l, jfloat t, jfloat r, jfloat b2, jlong pen, jlong brush) {
    jobject cv = getCanvas(e); if(!cv) return;
    jobject p = brush>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)brush) :
                pen>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)pen) : NULL;
    if(p) (*e)->CallVoidMethod(e, cv, c_drawRect, l, t, r, b2, p);
}
static void OHB_canvasDrawRoundRect(JNIEnv* e, jclass c, jlong cn, jfloat l, jfloat t, jfloat r, jfloat b2, jfloat rx, jfloat ry, jlong pen, jlong brush) {
    jobject cv = getCanvas(e); if(!cv) return;
    jobject p = brush>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)brush) :
                pen>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)pen) : NULL;
    if(p) (*e)->CallVoidMethod(e, cv, c_drawRoundRect, l, t, r, b2, rx, ry, p);
}
static void OHB_canvasDrawCircle(JNIEnv* e, jclass c, jlong cn, jfloat cx, jfloat cy, jfloat r, jlong pen, jlong brush) {
    jobject cv = getCanvas(e); if(!cv) return;
    jobject p = brush>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)brush) : NULL;
    if(p) (*e)->CallVoidMethod(e, cv, c_drawCircle, cx, cy, r, p);
}
static void OHB_canvasDrawLine(JNIEnv* e, jclass c, jlong cn, jfloat x1, jfloat y1, jfloat x2, jfloat y2, jlong pen) {
    jobject cv = getCanvas(e); if(!cv) return;
    jobject p = pen>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)pen) : NULL;
    if(p) (*e)->CallVoidMethod(e, cv, c_drawLine, x1, y1, x2, y2, p);
}
static void OHB_canvasDrawText(JNIEnv* e, jclass c, jlong cn, jstring text, jfloat x, jfloat y, jlong font, jlong pen, jlong brush) {
    jobject cv = getCanvas(e); if(!cv || !text) return;
    jobject p = font>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)font) : NULL;
    if(!p && pen>0) p = (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)pen);
    if(p) {
        // Set color from pen if font paint doesn't have it
        if (pen>0 && font>0) {
            jobject pp = (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)pen);
            if(pp) {
                jmethodID gc = (*e)->GetMethodID(e, paintClass, "getColor", "()I");
                jint col = (*e)->CallIntMethod(e, pp, gc);
                (*e)->CallVoidMethod(e, p, p_setColor, col);
            }
        }
        (*e)->CallVoidMethod(e, cv, c_drawText, text, x, y, p);
    }
}
static void OHB_canvasSave(JNIEnv* e, jclass c, jlong cn) {
    jobject cv = getCanvas(e); if(cv) (*e)->CallIntMethod(e, cv, c_save);
}
static void OHB_canvasRestore(JNIEnv* e, jclass c, jlong cn) {
    jobject cv = getCanvas(e); if(cv) (*e)->CallVoidMethod(e, cv, c_restore);
}
static void OHB_canvasTranslate(JNIEnv* e, jclass c, jlong cn, jfloat dx, jfloat dy) {
    jobject cv = getCanvas(e); if(cv) (*e)->CallVoidMethod(e, cv, c_translate, dx, dy);
}
static void OHB_canvasScale(JNIEnv* e, jclass c, jlong cn, jfloat sx, jfloat sy) {
    jobject cv = getCanvas(e); if(cv) (*e)->CallVoidMethod(e, cv, c_scale, sx, sy);
}
static void OHB_canvasClipRect(JNIEnv* e, jclass c, jlong cn, jfloat l, jfloat t, jfloat r, jfloat b2) {
    jobject cv = getCanvas(e); if(cv) (*e)->CallBooleanMethod(e, cv, c_clipRect, l, t, r, b2);
}
static void OHB_canvasDrawPath(JNIEnv* e, jclass c, jlong cn, jlong path, jlong pen, jlong brush) {
    jobject cv = getCanvas(e); if(!cv) return;
    jobject pp = path>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPathM,(jint)path) : NULL;
    jobject pa = brush>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)brush) :
                 pen>0 ? (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)pen) : NULL;
    if(pp && pa) (*e)->CallVoidMethod(e, cv, c_drawPath, pp, pa);
}
static void OHB_canvasRotate(JNIEnv* e, jclass c, jlong cn, jfloat d, jfloat px, jfloat py) {}
static void OHB_canvasClipPath(JNIEnv* e, jclass c, jlong cn, jlong p) {}
static void OHB_canvasConcat(JNIEnv* e, jclass c, jlong cn, jfloatArray m) {}
static void OHB_canvasDrawOval(JNIEnv* e, jclass c, jlong cn, jfloat l, jfloat t, jfloat r, jfloat b2, jlong pen, jlong brush) {}
static void OHB_canvasDrawArc(JNIEnv* e, jclass c, jlong cn, jfloat l, jfloat t, jfloat r, jfloat b2, jfloat sa, jfloat sw, jboolean uc, jlong pen, jlong brush) {}
static void OHB_canvasDrawBitmap(JNIEnv* e, jclass c, jlong cn, jlong bmp, jfloat x, jfloat y) {}
static void OHB_canvasDrawBitmapNine(JNIEnv* e, jclass c, jlong cn, jlong bmp, jint cl, jint ct, jint cr, jint cb, jfloat dl, jfloat dt, jfloat dr, jfloat db, jlong p) {}

// Pen → Android Paint (STROKE)
static jlong OHB_penCreate(JNIEnv* e, jclass c) { return (*e)->CallStaticIntMethod(e,hostClass,newPaintM,1); }
static void OHB_penSetColor(JNIEnv* e, jclass c, jlong p, jint col) {
    jobject pa = (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)p);
    if(pa) (*e)->CallVoidMethod(e, pa, p_setColor, col);
}
static void OHB_penSetWidth(JNIEnv* e, jclass c, jlong p, jfloat w) {
    jobject pa = (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)p);
    if(pa) (*e)->CallVoidMethod(e, pa, p_setStrokeWidth, w);
}
static void OHB_penSetAntiAlias(JNIEnv* e, jclass c, jlong p, jboolean aa) {}
static void OHB_penSetCap(JNIEnv* e, jclass c, jlong p, jint cap) {}
static void OHB_penSetJoin(JNIEnv* e, jclass c, jlong p, jint j) {}
static void OHB_penDestroy(JNIEnv* e, jclass c, jlong p) {}

// Brush → Android Paint (FILL)
static jlong OHB_brushCreate(JNIEnv* e, jclass c) { return (*e)->CallStaticIntMethod(e,hostClass,newPaintM,0); }
static void OHB_brushSetColor(JNIEnv* e, jclass c, jlong b, jint col) {
    jobject pa = (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)b);
    if(pa) (*e)->CallVoidMethod(e, pa, p_setColor, col);
}
static void OHB_brushSetAntiAlias(JNIEnv* e, jclass c, jlong b, jboolean aa) {}
static void OHB_brushDestroy(JNIEnv* e, jclass c, jlong b) {}

// Font → Android Paint with textSize
static jlong OHB_fontCreate(JNIEnv* e, jclass c) {
    int id = (*e)->CallStaticIntMethod(e,hostClass,newPaintM,0);
    jobject pa = (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,id);
    if(pa) (*e)->CallVoidMethod(e, pa, p_setTextSize, 16.0f);
    return id;
}
static void OHB_fontSetSize(JNIEnv* e, jclass c, jlong f, jfloat sz) {
    jobject pa = (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)f);
    if(pa) (*e)->CallVoidMethod(e, pa, p_setTextSize, sz);
}
static jfloat OHB_fontMeasureText(JNIEnv* e, jclass c, jlong f, jstring s) {
    if(!s) return 0;
    jobject pa = (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)f);
    if(!pa) return 0;
    return (*e)->CallFloatMethod(e, pa, p_measureText, s);
}
static void OHB_fontDestroy(JNIEnv* e, jclass c, jlong f) {}
static jfloatArray OHB_fontGetMetrics(JNIEnv* e, jclass c, jlong f) {
    jfloatArray arr = (*e)->NewFloatArray(e, 4);
    jobject pa = (*e)->CallStaticObjectMethod(e,hostClass,getPaintM,(jint)f);
    if(pa) {
        jobject fm = (*e)->CallObjectMethod(e, pa, p_getFontMetrics);
        if(fm) {
            jfloat m[4];
            m[0] = (*e)->GetFloatField(e, fm, fm_ascent);
            m[1] = (*e)->GetFloatField(e, fm, fm_descent);
            m[2] = 0;
            m[3] = m[1] - m[0];
            (*e)->SetFloatArrayRegion(e, arr, 0, 4, m);
        }
    }
    return arr;
}

// Bitmap — stubs (TODO: use Android Bitmap)
static jlong OHB_bitmapCreate(JNIEnv* e, jclass c, jint w, jint h, jint f) { return 1; }
static void OHB_bitmapDestroy(JNIEnv* e, jclass c, jlong b) {}
static jint OHB_bitmapGetWidth(JNIEnv* e, jclass c, jlong b) { return 480; }
static jint OHB_bitmapGetHeight(JNIEnv* e, jclass c, jlong b) { return 800; }
static void OHB_bitmapSetPixel(JNIEnv* e, jclass c, jlong b, jint x, jint y, jint col) {}
static jint OHB_bitmapGetPixel(JNIEnv* e, jclass c, jlong b, jint x, jint y) { return 0; }
static jint OHB_bitmapWriteToFile(JNIEnv* e, jclass c, jlong b, jstring p) { return 0; }
static jint OHB_bitmapBlitToFb0(JNIEnv* e, jclass c, jlong b, jint off) { return 0; }

// Path → Android Path
static jlong OHB_pathCreate(JNIEnv* e, jclass c) { return (*e)->CallStaticIntMethod(e,hostClass,newPathM); }
static void OHB_pathMoveTo(JNIEnv* e, jclass c, jlong p, jfloat x, jfloat y) {
    jobject pp = (*e)->CallStaticObjectMethod(e,hostClass,getPathM,(jint)p);
    if(pp) (*e)->CallVoidMethod(e, pp, path_moveTo, x, y);
}
static void OHB_pathLineTo(JNIEnv* e, jclass c, jlong p, jfloat x, jfloat y) {
    jobject pp = (*e)->CallStaticObjectMethod(e,hostClass,getPathM,(jint)p);
    if(pp) (*e)->CallVoidMethod(e, pp, path_lineTo, x, y);
}
static void OHB_pathClose(JNIEnv* e, jclass c, jlong p) {
    jobject pp = (*e)->CallStaticObjectMethod(e,hostClass,getPathM,(jint)p);
    if(pp) (*e)->CallVoidMethod(e, pp, path_close);
}
static void OHB_pathDestroy(JNIEnv* e, jclass c, jlong p) {}
static void OHB_pathCubicTo(JNIEnv* e, jclass c, jlong p, jfloat x1, jfloat y1, jfloat x2, jfloat y2, jfloat x3, jfloat y3) {}
static void OHB_pathQuadTo(JNIEnv* e, jclass c, jlong p, jfloat x1, jfloat y1, jfloat x2, jfloat y2) {}
static void OHB_pathAddRect(JNIEnv* e, jclass c, jlong p, jfloat l, jfloat t, jfloat r, jfloat b, jint d) {}
static void OHB_pathAddCircle(JNIEnv* e, jclass c, jlong p, jfloat cx, jfloat cy, jfloat r, jint d) {}
static void OHB_pathReset(JNIEnv* e, jclass c, jlong p) {
    jobject pp = (*e)->CallStaticObjectMethod(e,hostClass,getPathM,(jint)p);
    if(pp) (*e)->CallVoidMethod(e, pp, path_reset);
}

// Stubs for all other OHBridge methods
#define STUB_V(name) static void OHB_##name(JNIEnv* e, jclass c, ...) {}
#define STUB_I(name) static jint OHB_##name(JNIEnv* e, jclass c, ...) { return 0; }
#define STUB_J(name) static jlong OHB_##name(JNIEnv* e, jclass c, ...) { return 0; }
#define STUB_Z(name) static jboolean OHB_##name(JNIEnv* e, jclass c, ...) { return 0; }
#define STUB_F(name) static jfloat OHB_##name(JNIEnv* e, jclass c, ...) { return 0; }
#define STUB_S(name) static jstring OHB_##name(JNIEnv* e, jclass c, ...) { return (*e)->NewStringUTF(e,""); }

// Preferences — return defaults
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

// RDB stubs
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
static jboolean OHB_resultSetGoToFirstRow(JNIEnv* e, jclass c, jlong h) { return 0; }
static jboolean OHB_resultSetGoToNextRow(JNIEnv* e, jclass c, jlong h) { return 0; }
static jint OHB_resultSetGetColumnIndex(JNIEnv* e, jclass c, jlong h, jstring n) { return -1; }
static jstring OHB_resultSetGetString(JNIEnv* e, jclass c, jlong h, jint i) { return (*e)->NewStringUTF(e,""); }
static jint OHB_resultSetGetInt(JNIEnv* e, jclass c, jlong h, jint i) { return 0; }
static jlong OHB_resultSetGetLong(JNIEnv* e, jclass c, jlong h, jint i) { return 0; }
static jfloat OHB_resultSetGetFloat(JNIEnv* e, jclass c, jlong h, jint i) { return 0; }
static jdouble OHB_resultSetGetDouble(JNIEnv* e, jclass c, jlong h, jint i) { return 0; }
static jbyteArray OHB_resultSetGetBlob(JNIEnv* e, jclass c, jlong h, jint i) { return NULL; }
static jint OHB_resultSetGetColumnCount(JNIEnv* e, jclass c, jlong h) { return 0; }
static jstring OHB_resultSetGetColumnName(JNIEnv* e, jclass c, jlong h, jint i) { return (*e)->NewStringUTF(e,""); }
static jint OHB_resultSetGetRowCount(JNIEnv* e, jclass c, jlong h) { return 0; }
static jboolean OHB_resultSetIsNull(JNIEnv* e, jclass c, jlong h, jint i) { return 1; }
static void OHB_resultSetClose(JNIEnv* e, jclass c, jlong h) {}

// All other stubs
static void OHB_showToast(JNIEnv* e, jclass c, jstring m, jint d) {}
static jint OHB_checkPermission(JNIEnv* e, jclass c, jstring p) { return 0; }
static jstring OHB_getDeviceBrand(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e,"Westlake"); }
static jstring OHB_getDeviceModel(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e,"Phone"); }
static jstring OHB_getOSVersion(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e,"1.0"); }
static jint OHB_getNetworkType(JNIEnv* e, jclass c) { return 0; }
static void OHB_startAbility(JNIEnv* e, jclass c, jstring a, jstring b, jstring d) {}
static void OHB_terminateSelf(JNIEnv* e, jclass c) {}
static jstring OHB_httpRequest(JNIEnv* e, jclass c, jstring u, jstring m, jstring h, jstring b) { return NULL; }
static void OHB_clipboardSet(JNIEnv* e, jclass c, jstring t) {}
static jstring OHB_clipboardGet(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e,""); }
static jboolean OHB_vibratorHasVibrator(JNIEnv* e, jclass c) { return 0; }
static void OHB_vibratorVibrate(JNIEnv* e, jclass c, jlong ms) {}
static void OHB_vibratorCancel(JNIEnv* e, jclass c) {}
static jboolean OHB_locationIsEnabled(JNIEnv* e, jclass c) { return 0; }
static jdoubleArray OHB_locationGetLast(JNIEnv* e, jclass c) { return NULL; }
static jboolean OHB_sensorIsAvailable(JNIEnv* e, jclass c, jint t) { return 0; }
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
static jboolean OHB_mediaPlayerIsPlaying(JNIEnv* e, jclass c, jlong h) { return 0; }
static void OHB_mediaPlayerSetLooping(JNIEnv* e, jclass c, jlong h, jboolean l) {}
static void OHB_mediaPlayerSetVolume(JNIEnv* e, jclass c, jlong h, jfloat lv, jfloat rv) {}
static jint OHB_audioGetStreamVolume(JNIEnv* e, jclass c, jint s) { return 50; }
static jint OHB_audioGetStreamMaxVolume(JNIEnv* e, jclass c, jint s) { return 100; }
static void OHB_audioSetStreamVolume(JNIEnv* e, jclass c, jint s, jint v, jint f) {}
static jint OHB_audioGetRingerMode(JNIEnv* e, jclass c) { return 2; }
static void OHB_audioSetRingerMode(JNIEnv* e, jclass c, jint m) {}
static jboolean OHB_audioIsMusicActive(JNIEnv* e, jclass c) { return 0; }
static jint OHB_telephonyGetPhoneType(JNIEnv* e, jclass c) { return 0; }
static jint OHB_telephonyGetNetworkType(JNIEnv* e, jclass c) { return 0; }
static jint OHB_telephonyGetSimState(JNIEnv* e, jclass c) { return 0; }
static jstring OHB_telephonyGetDeviceId(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e,""); }
static jstring OHB_telephonyGetLine1Number(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e,""); }
static jstring OHB_telephonyGetNetworkOperatorName(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e,""); }
static jboolean OHB_wifiIsEnabled(JNIEnv* e, jclass c) { return 0; }
static jboolean OHB_wifiSetEnabled(JNIEnv* e, jclass c, jboolean en) { return 0; }
static jint OHB_wifiGetState(JNIEnv* e, jclass c) { return 0; }
static jstring OHB_wifiGetSSID(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e,""); }
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

/* Registration — called lazily when OHBridge class is available */
static int g_registered = 0;

void registerOHBridge(JNIEnv* env) {
    if (g_registered) return;
    jclass cls = (*env)->FindClass(env, "com/ohos/shim/bridge/OHBridge");
    if (!cls) { LOGI("OHBridge not found yet"); return; }

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
    int n = sizeof(m)/sizeof(m[0]);
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
    LOGI("libohbridge_native loaded");
    return JNI_VERSION_1_6;
}

/* JNI-named exports — ART finds these via dlsym without RegisterNatives */
JNIEXPORT jint JNICALL Java_com_ohos_shim_bridge_OHBridge_arkuiInit(JNIEnv* e, jclass c) {
    cache(e);
    registerOHBridge(e);
    return 0;
}
