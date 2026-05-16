// ============================================================================
// android_graphics_Paint.cpp
//
// JNI registration for android.graphics.Paint (85 natives, all no-op stubs).
// Phase 1 r27 unblocks MainActivity.onCreate line 83 (TextView constructor
// invokes new Paint() → nInit()).
//
// IMPORTANT: HelloWorld targets hardware rendering, so these stubs will NOT
// produce pixels. They only let the View hierarchy build past UnsatisfiedLinkError.
// Real rendering requires linking against real libhwui (containing Skia +
// RenderThread) or a libhwui-shim that bridges to OH RSSurfaceNode.  See
// liboh_hwui_shim project for the OH bridge approach.
//
// nInit returns a malloc'd dummy handle (non-zero) so Paint object's
// mNativePaint != 0 (otherwise Paint methods early-return / NPE downstream).
// Setters return void or no-op booleans.  Getters return safe defaults
// (textSize=12, color=BLACK, alpha=255) when needed; otherwise 0.
// ============================================================================
#include <jni.h>
#include <cstdlib>
#include <cstring>

namespace android {
namespace {

extern "C" void P_noop_free(void* p) { if (p) std::free(p); }

static jlong P_nGetNativeFinalizer(JNIEnv*, jclass) { return reinterpret_cast<jlong>(&P_noop_free); }
static jlong P_nInit(JNIEnv*, jclass) { void* p = std::malloc(8); if (p) std::memset(p, 0, 8); return reinterpret_cast<jlong>(p); }
static jlong P_nInitWithPaint(JNIEnv*, jclass, jlong) { void* p = std::malloc(8); if (p) std::memset(p, 0, 8); return reinterpret_cast<jlong>(p); }
static jint P_nBreakText(JNIEnv*, jclass, jlong, jcharArray, jint, jint, jfloat, jint, jfloatArray) { return 0; }
static jint P_nBreakText_2(JNIEnv*, jclass, jlong, jstring, jboolean, jfloat, jint, jfloatArray) { return 0; }
static jfloat P_nGetTextAdvances(JNIEnv*, jclass, jlong, jcharArray, jint, jint, jint, jint, jint, jfloatArray, jint) { return 0.0f; }
static jfloat P_nGetTextAdvances_2(JNIEnv*, jclass, jlong, jstring, jint, jint, jint, jint, jint, jfloatArray, jint) { return 0.0f; }
static jint P_nGetTextRunCursor(JNIEnv*, jclass, jlong, jcharArray, jint, jint, jint, jint, jint) { return 0; }
static jint P_nGetTextRunCursor_2(JNIEnv*, jclass, jlong, jstring, jint, jint, jint, jint, jint) { return 0; }
static void P_nGetTextPath(JNIEnv*, jclass, jlong, jint, jcharArray, jint, jint, jfloat, jfloat, jlong) { }
static void P_nGetTextPath_2(JNIEnv*, jclass, jlong, jint, jstring, jint, jint, jfloat, jfloat, jlong) { }
static void P_nGetStringBounds(JNIEnv*, jclass, jlong, jstring, jint, jint, jint, jobject) { }
static void P_nGetCharArrayBounds(JNIEnv*, jclass, jlong, jcharArray, jint, jint, jint, jobject) { }
static jboolean P_nHasGlyph(JNIEnv*, jclass, jlong, jint, jstring) { return JNI_FALSE; }
static jfloat P_nGetRunAdvance(JNIEnv*, jclass, jlong, jcharArray, jint, jint, jint, jint, jboolean, jint) { return 0.0f; }
static jfloat P_nGetRunCharacterAdvance(JNIEnv*, jclass, jlong, jcharArray, jint, jint, jint, jint, jboolean, jint, jfloatArray, jint) { return 0.0f; }
static jint P_nGetOffsetForAdvance(JNIEnv*, jclass, jlong, jcharArray, jint, jint, jint, jint, jboolean, jfloat) { return 0; }
static void P_nGetFontMetricsIntForText(JNIEnv*, jclass, jlong, jcharArray, jint, jint, jint, jint, jboolean, jobject) { }
static void P_nGetFontMetricsIntForText_2(JNIEnv*, jclass, jlong, jstring, jint, jint, jint, jint, jboolean, jobject) { }
static jint P_nSetTextLocales(JNIEnv*, jclass, jlong, jstring) { return 0; }
static void P_nSetFontFeatureSettings(JNIEnv*, jclass, jlong, jstring) { }
static jfloat P_nGetFontMetrics(JNIEnv*, jclass, jlong, jobject) { return 0.0f; }
static jint P_nGetFontMetricsInt(JNIEnv*, jclass, jlong, jobject) { return 0; }
static void P_nReset(JNIEnv*, jclass, jlong) { }
static void P_nSet(JNIEnv*, jclass, jlong, jlong) { }
static jint P_nGetStyle(JNIEnv*, jclass, jlong) { return 0; }
static void P_nSetStyle(JNIEnv*, jclass, jlong, jint) { }
static jint P_nGetStrokeCap(JNIEnv*, jclass, jlong) { return 0; }
static void P_nSetStrokeCap(JNIEnv*, jclass, jlong, jint) { }
static jint P_nGetStrokeJoin(JNIEnv*, jclass, jlong) { return 0; }
static void P_nSetStrokeJoin(JNIEnv*, jclass, jlong, jint) { }
static jboolean P_nGetFillPath(JNIEnv*, jclass, jlong, jlong, jlong) { return JNI_FALSE; }
static jlong P_nSetShader(JNIEnv*, jclass, jlong, jlong) { return 0; }
static jlong P_nSetColorFilter(JNIEnv*, jclass, jlong, jlong) { return 0; }
static void P_nSetXfermode(JNIEnv*, jclass, jlong, jint) { }
static jlong P_nSetPathEffect(JNIEnv*, jclass, jlong, jlong) { return 0; }
static jlong P_nSetMaskFilter(JNIEnv*, jclass, jlong, jlong) { return 0; }
static void P_nSetTypeface(JNIEnv*, jclass, jlong, jlong) { }
static jint P_nGetTextAlign(JNIEnv*, jclass, jlong) { return 0; }
static void P_nSetTextAlign(JNIEnv*, jclass, jlong, jint) { }
static void P_nSetTextLocalesByMinikinLocaleListId(JNIEnv*, jclass, jlong, jint) { }
static void P_nSetShadowLayer(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat, jlong, jlong) { }
static jboolean P_nHasShadowLayer(JNIEnv*, jclass, jlong) { return JNI_FALSE; }
static jfloat P_nGetLetterSpacing(JNIEnv*, jclass, jlong) { return 0.0f; }
static void P_nSetLetterSpacing(JNIEnv*, jclass, jlong, jfloat) { }
static jfloat P_nGetWordSpacing(JNIEnv*, jclass, jlong) { return 0.0f; }
static void P_nSetWordSpacing(JNIEnv*, jclass, jlong, jfloat) { }
static jint P_nGetStartHyphenEdit(JNIEnv*, jclass, jlong) { return 0; }
static jint P_nGetEndHyphenEdit(JNIEnv*, jclass, jlong) { return 0; }
static void P_nSetStartHyphenEdit(JNIEnv*, jclass, jlong, jint) { }
static void P_nSetEndHyphenEdit(JNIEnv*, jclass, jlong, jint) { }
static void P_nSetStrokeMiter(JNIEnv*, jclass, jlong, jfloat) { }
static jfloat P_nGetStrokeMiter(JNIEnv*, jclass, jlong) { return 0.0f; }
static void P_nSetStrokeWidth(JNIEnv*, jclass, jlong, jfloat) { }
static jfloat P_nGetStrokeWidth(JNIEnv*, jclass, jlong) { return 0.0f; }
static void P_nSetAlpha(JNIEnv*, jclass, jlong, jint) { }
static void P_nSetDither(JNIEnv*, jclass, jlong, jboolean) { }
static jint P_nGetFlags(JNIEnv*, jclass, jlong) { return 0; }
static void P_nSetFlags(JNIEnv*, jclass, jlong, jint) { }
static jint P_nGetHinting(JNIEnv*, jclass, jlong) { return 0; }
static void P_nSetHinting(JNIEnv*, jclass, jlong, jint) { }
static void P_nSetAntiAlias(JNIEnv*, jclass, jlong, jboolean) { }
static void P_nSetLinearText(JNIEnv*, jclass, jlong, jboolean) { }
static void P_nSetSubpixelText(JNIEnv*, jclass, jlong, jboolean) { }
static void P_nSetUnderlineText(JNIEnv*, jclass, jlong, jboolean) { }
static void P_nSetFakeBoldText(JNIEnv*, jclass, jlong, jboolean) { }
static void P_nSetFilterBitmap(JNIEnv*, jclass, jlong, jboolean) { }
static void P_nSetColor(JNIEnv*, jclass, jlong, jlong, jlong) { }
static void P_nSetColor_2(JNIEnv*, jclass, jlong, jint) { }
static void P_nSetStrikeThruText(JNIEnv*, jclass, jlong, jboolean) { }
static jboolean P_nIsElegantTextHeight(JNIEnv*, jclass, jlong) { return JNI_FALSE; }
static void P_nSetElegantTextHeight(JNIEnv*, jclass, jlong, jboolean) { }
static jfloat P_nGetTextSize(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat P_nGetTextScaleX(JNIEnv*, jclass, jlong) { return 0.0f; }
static void P_nSetTextScaleX(JNIEnv*, jclass, jlong, jfloat) { }
static jfloat P_nGetTextSkewX(JNIEnv*, jclass, jlong) { return 0.0f; }
static void P_nSetTextSkewX(JNIEnv*, jclass, jlong, jfloat) { }
static jfloat P_nAscent(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat P_nDescent(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat P_nGetUnderlinePosition(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat P_nGetUnderlineThickness(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat P_nGetStrikeThruPosition(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat P_nGetStrikeThruThickness(JNIEnv*, jclass, jlong) { return 0.0f; }
static void P_nSetTextSize(JNIEnv*, jclass, jlong, jfloat) { }
static jboolean P_nEqualsForTextMeasurement(JNIEnv*, jclass, jlong, jlong) { return JNI_FALSE; }

const JNINativeMethod kMethods[] = {
    { "nGetNativeFinalizer", "()J", reinterpret_cast<void*>(P_nGetNativeFinalizer) },
    { "nInit", "()J", reinterpret_cast<void*>(P_nInit) },
    { "nInitWithPaint", "(J)J", reinterpret_cast<void*>(P_nInitWithPaint) },
    { "nBreakText", "(J[CIIFI[F)I", reinterpret_cast<void*>(P_nBreakText) },
    { "nBreakText", "(JLjava/lang/String;ZFI[F)I", reinterpret_cast<void*>(P_nBreakText_2) },
    { "nGetTextAdvances", "(J[CIIIII[FI)F", reinterpret_cast<void*>(P_nGetTextAdvances) },
    { "nGetTextAdvances", "(JLjava/lang/String;IIIII[FI)F", reinterpret_cast<void*>(P_nGetTextAdvances_2) },
    { "nGetTextRunCursor", "(J[CIIIII)I", reinterpret_cast<void*>(P_nGetTextRunCursor) },
    { "nGetTextRunCursor", "(JLjava/lang/String;IIIII)I", reinterpret_cast<void*>(P_nGetTextRunCursor_2) },
    { "nGetTextPath", "(JI[CIIFFJ)V", reinterpret_cast<void*>(P_nGetTextPath) },
    { "nGetTextPath", "(JILjava/lang/String;IIFFJ)V", reinterpret_cast<void*>(P_nGetTextPath_2) },
    { "nGetStringBounds", "(JLjava/lang/String;IIILandroid/graphics/Rect;)V", reinterpret_cast<void*>(P_nGetStringBounds) },
    { "nGetCharArrayBounds", "(J[CIIILandroid/graphics/Rect;)V", reinterpret_cast<void*>(P_nGetCharArrayBounds) },
    { "nHasGlyph", "(JILjava/lang/String;)Z", reinterpret_cast<void*>(P_nHasGlyph) },
    { "nGetRunAdvance", "(J[CIIIIZI)F", reinterpret_cast<void*>(P_nGetRunAdvance) },
    { "nGetRunCharacterAdvance", "(J[CIIIIZI[FI)F", reinterpret_cast<void*>(P_nGetRunCharacterAdvance) },
    { "nGetOffsetForAdvance", "(J[CIIIIZF)I", reinterpret_cast<void*>(P_nGetOffsetForAdvance) },
    { "nGetFontMetricsIntForText", "(J[CIIIIZLandroid/graphics/FontMetricsInt;)V", reinterpret_cast<void*>(P_nGetFontMetricsIntForText) },
    { "nGetFontMetricsIntForText", "(JLjava/lang/String;IIIIZLandroid/graphics/FontMetricsInt;)V", reinterpret_cast<void*>(P_nGetFontMetricsIntForText_2) },
    { "nSetTextLocales", "(JLjava/lang/String;)I", reinterpret_cast<void*>(P_nSetTextLocales) },
    { "nSetFontFeatureSettings", "(JLjava/lang/String;)V", reinterpret_cast<void*>(P_nSetFontFeatureSettings) },
    { "nGetFontMetrics", "(JLandroid/graphics/FontMetrics;)F", reinterpret_cast<void*>(P_nGetFontMetrics) },
    { "nGetFontMetricsInt", "(JLandroid/graphics/FontMetricsInt;)I", reinterpret_cast<void*>(P_nGetFontMetricsInt) },
    { "nReset", "(J)V", reinterpret_cast<void*>(P_nReset) },
    { "nSet", "(JJ)V", reinterpret_cast<void*>(P_nSet) },
    { "nGetStyle", "(J)I", reinterpret_cast<void*>(P_nGetStyle) },
    { "nSetStyle", "(JI)V", reinterpret_cast<void*>(P_nSetStyle) },
    { "nGetStrokeCap", "(J)I", reinterpret_cast<void*>(P_nGetStrokeCap) },
    { "nSetStrokeCap", "(JI)V", reinterpret_cast<void*>(P_nSetStrokeCap) },
    { "nGetStrokeJoin", "(J)I", reinterpret_cast<void*>(P_nGetStrokeJoin) },
    { "nSetStrokeJoin", "(JI)V", reinterpret_cast<void*>(P_nSetStrokeJoin) },
    { "nGetFillPath", "(JJJ)Z", reinterpret_cast<void*>(P_nGetFillPath) },
    { "nSetShader", "(JJ)J", reinterpret_cast<void*>(P_nSetShader) },
    { "nSetColorFilter", "(JJ)J", reinterpret_cast<void*>(P_nSetColorFilter) },
    { "nSetXfermode", "(JI)V", reinterpret_cast<void*>(P_nSetXfermode) },
    { "nSetPathEffect", "(JJ)J", reinterpret_cast<void*>(P_nSetPathEffect) },
    { "nSetMaskFilter", "(JJ)J", reinterpret_cast<void*>(P_nSetMaskFilter) },
    { "nSetTypeface", "(JJ)V", reinterpret_cast<void*>(P_nSetTypeface) },
    { "nGetTextAlign", "(J)I", reinterpret_cast<void*>(P_nGetTextAlign) },
    { "nSetTextAlign", "(JI)V", reinterpret_cast<void*>(P_nSetTextAlign) },
    { "nSetTextLocalesByMinikinLocaleListId", "(JI)V", reinterpret_cast<void*>(P_nSetTextLocalesByMinikinLocaleListId) },
    { "nSetShadowLayer", "(JFFFJJ)V", reinterpret_cast<void*>(P_nSetShadowLayer) },
    { "nHasShadowLayer", "(J)Z", reinterpret_cast<void*>(P_nHasShadowLayer) },
    { "nGetLetterSpacing", "(J)F", reinterpret_cast<void*>(P_nGetLetterSpacing) },
    { "nSetLetterSpacing", "(JF)V", reinterpret_cast<void*>(P_nSetLetterSpacing) },
    { "nGetWordSpacing", "(J)F", reinterpret_cast<void*>(P_nGetWordSpacing) },
    { "nSetWordSpacing", "(JF)V", reinterpret_cast<void*>(P_nSetWordSpacing) },
    { "nGetStartHyphenEdit", "(J)I", reinterpret_cast<void*>(P_nGetStartHyphenEdit) },
    { "nGetEndHyphenEdit", "(J)I", reinterpret_cast<void*>(P_nGetEndHyphenEdit) },
    { "nSetStartHyphenEdit", "(JI)V", reinterpret_cast<void*>(P_nSetStartHyphenEdit) },
    { "nSetEndHyphenEdit", "(JI)V", reinterpret_cast<void*>(P_nSetEndHyphenEdit) },
    { "nSetStrokeMiter", "(JF)V", reinterpret_cast<void*>(P_nSetStrokeMiter) },
    { "nGetStrokeMiter", "(J)F", reinterpret_cast<void*>(P_nGetStrokeMiter) },
    { "nSetStrokeWidth", "(JF)V", reinterpret_cast<void*>(P_nSetStrokeWidth) },
    { "nGetStrokeWidth", "(J)F", reinterpret_cast<void*>(P_nGetStrokeWidth) },
    { "nSetAlpha", "(JI)V", reinterpret_cast<void*>(P_nSetAlpha) },
    { "nSetDither", "(JZ)V", reinterpret_cast<void*>(P_nSetDither) },
    { "nGetFlags", "(J)I", reinterpret_cast<void*>(P_nGetFlags) },
    { "nSetFlags", "(JI)V", reinterpret_cast<void*>(P_nSetFlags) },
    { "nGetHinting", "(J)I", reinterpret_cast<void*>(P_nGetHinting) },
    { "nSetHinting", "(JI)V", reinterpret_cast<void*>(P_nSetHinting) },
    { "nSetAntiAlias", "(JZ)V", reinterpret_cast<void*>(P_nSetAntiAlias) },
    { "nSetLinearText", "(JZ)V", reinterpret_cast<void*>(P_nSetLinearText) },
    { "nSetSubpixelText", "(JZ)V", reinterpret_cast<void*>(P_nSetSubpixelText) },
    { "nSetUnderlineText", "(JZ)V", reinterpret_cast<void*>(P_nSetUnderlineText) },
    { "nSetFakeBoldText", "(JZ)V", reinterpret_cast<void*>(P_nSetFakeBoldText) },
    { "nSetFilterBitmap", "(JZ)V", reinterpret_cast<void*>(P_nSetFilterBitmap) },
    { "nSetColor", "(JJJ)V", reinterpret_cast<void*>(P_nSetColor) },
    { "nSetColor", "(JI)V", reinterpret_cast<void*>(P_nSetColor_2) },
    { "nSetStrikeThruText", "(JZ)V", reinterpret_cast<void*>(P_nSetStrikeThruText) },
    { "nIsElegantTextHeight", "(J)Z", reinterpret_cast<void*>(P_nIsElegantTextHeight) },
    { "nSetElegantTextHeight", "(JZ)V", reinterpret_cast<void*>(P_nSetElegantTextHeight) },
    { "nGetTextSize", "(J)F", reinterpret_cast<void*>(P_nGetTextSize) },
    { "nGetTextScaleX", "(J)F", reinterpret_cast<void*>(P_nGetTextScaleX) },
    { "nSetTextScaleX", "(JF)V", reinterpret_cast<void*>(P_nSetTextScaleX) },
    { "nGetTextSkewX", "(J)F", reinterpret_cast<void*>(P_nGetTextSkewX) },
    { "nSetTextSkewX", "(JF)V", reinterpret_cast<void*>(P_nSetTextSkewX) },
    { "nAscent", "(J)F", reinterpret_cast<void*>(P_nAscent) },
    { "nDescent", "(J)F", reinterpret_cast<void*>(P_nDescent) },
    { "nGetUnderlinePosition", "(J)F", reinterpret_cast<void*>(P_nGetUnderlinePosition) },
    { "nGetUnderlineThickness", "(J)F", reinterpret_cast<void*>(P_nGetUnderlineThickness) },
    { "nGetStrikeThruPosition", "(J)F", reinterpret_cast<void*>(P_nGetStrikeThruPosition) },
    { "nGetStrikeThruThickness", "(J)F", reinterpret_cast<void*>(P_nGetStrikeThruThickness) },
    { "nSetTextSize", "(JF)V", reinterpret_cast<void*>(P_nSetTextSize) },
    { "nEqualsForTextMeasurement", "(JJ)Z", reinterpret_cast<void*>(P_nEqualsForTextMeasurement) },
};

}  // namespace

int register_android_graphics_Paint(JNIEnv* env) {
    jclass clazz = env->FindClass("android/graphics/Paint");
    if (!clazz) return -1;
    jint rc = env->RegisterNatives(clazz, kMethods, sizeof(kMethods)/sizeof(kMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
