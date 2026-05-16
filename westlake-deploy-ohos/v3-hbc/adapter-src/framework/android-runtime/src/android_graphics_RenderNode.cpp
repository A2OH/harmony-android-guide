// ============================================================================
// android_graphics_RenderNode.cpp
//
// JNI registration for android.graphics.RenderNode (89 natives).  Used by
// View hierarchy to track display lists / transforms / shadows.  HelloWorld
// MainActivity.onCreate triggers nCreate as soon as setContentView inflates
// any View.  All natives are no-op safe defaults so the View tree builds
// without real hwui rendering — pixels won't display but the Java state
// machine is satisfied.
//
// nCreate returns a malloc'd dummy handle (non-zero) so RenderNode Java
// object's mNativeRenderNode is never 0 (which would trip "RenderNode
// has been destroyed" checks).  Setters return JNI_TRUE so the View
// thinks the change took effect (avoid retry loops).
// ============================================================================

#include <jni.h>
#include <cstdlib>
#include <cstring>

namespace android {
namespace {

extern "C" void RN_noop_free(void* p) { if (p) std::free(p); }

static jlong RN_nCreate(JNIEnv*, jclass, jstring) {
    void* p = std::malloc(8);
    if (p) std::memset(p, 0, 8);
    return reinterpret_cast<jlong>(p);
}
static jlong RN_nGetNativeFinalizer(JNIEnv*, jclass) {
    return reinterpret_cast<jlong>(&RN_noop_free);
}
static void RN_nOutput(JNIEnv*, jclass, jlong) { }
static jint RN_nGetUsageSize(JNIEnv*, jclass, jlong) { return 0; }
static jint RN_nGetAllocatedSize(JNIEnv*, jclass, jlong) { return 0; }
static void RN_nRequestPositionUpdates(JNIEnv*, jclass, jlong, jobject) { }
static void RN_nAddAnimator(JNIEnv*, jclass, jlong, jlong) { }
static void RN_nEndAllAnimators(JNIEnv*, jclass, jlong) { }
static void RN_nForceEndAnimators(JNIEnv*, jclass, jlong) { }
static void RN_nDiscardDisplayList(JNIEnv*, jclass, jlong) { }
static jboolean RN_nIsValid(JNIEnv*, jclass, jlong) { return JNI_TRUE; }
static void RN_nGetTransformMatrix(JNIEnv*, jclass, jlong, jlong) { }
static void RN_nGetInverseTransformMatrix(JNIEnv*, jclass, jlong, jlong) { }
static jboolean RN_nHasIdentityMatrix(JNIEnv*, jclass, jlong) { return JNI_TRUE; }
static jboolean RN_nOffsetTopAndBottom(JNIEnv*, jclass, jlong, jint) { return JNI_TRUE; }
static jboolean RN_nOffsetLeftAndRight(JNIEnv*, jclass, jlong, jint) { return JNI_TRUE; }
static jboolean RN_nSetLeftTopRightBottom(JNIEnv*, jclass, jlong, jint, jint, jint, jint) { return JNI_TRUE; }
static jboolean RN_nSetLeft(JNIEnv*, jclass, jlong, jint) { return JNI_TRUE; }
static jboolean RN_nSetTop(JNIEnv*, jclass, jlong, jint) { return JNI_TRUE; }
static jboolean RN_nSetRight(JNIEnv*, jclass, jlong, jint) { return JNI_TRUE; }
static jboolean RN_nSetBottom(JNIEnv*, jclass, jlong, jint) { return JNI_TRUE; }
static jint RN_nGetLeft(JNIEnv*, jclass, jlong) { return 0; }
static jint RN_nGetTop(JNIEnv*, jclass, jlong) { return 0; }
static jint RN_nGetRight(JNIEnv*, jclass, jlong) { return 0; }
static jint RN_nGetBottom(JNIEnv*, jclass, jlong) { return 0; }
static jboolean RN_nSetCameraDistance(JNIEnv*, jclass, jlong, jfloat) { return JNI_TRUE; }
static jboolean RN_nSetPivotY(JNIEnv*, jclass, jlong, jfloat) { return JNI_TRUE; }
static jboolean RN_nSetPivotX(JNIEnv*, jclass, jlong, jfloat) { return JNI_TRUE; }
static jboolean RN_nResetPivot(JNIEnv*, jclass, jlong) { return JNI_TRUE; }
static jboolean RN_nSetLayerType(JNIEnv*, jclass, jlong, jint) { return JNI_TRUE; }
static jint RN_nGetLayerType(JNIEnv*, jclass, jlong) { return 0; }
static jboolean RN_nSetLayerPaint(JNIEnv*, jclass, jlong, jlong) { return JNI_TRUE; }
static jboolean RN_nSetClipToBounds(JNIEnv*, jclass, jlong, jboolean) { return JNI_TRUE; }
static jboolean RN_nGetClipToBounds(JNIEnv*, jclass, jlong) { return JNI_TRUE; }
static jboolean RN_nSetClipBounds(JNIEnv*, jclass, jlong, jint, jint, jint, jint) { return JNI_TRUE; }
static jboolean RN_nSetClipBoundsEmpty(JNIEnv*, jclass, jlong) { return JNI_TRUE; }
static jboolean RN_nSetProjectBackwards(JNIEnv*, jclass, jlong, jboolean) { return JNI_TRUE; }
static jboolean RN_nSetProjectionReceiver(JNIEnv*, jclass, jlong, jboolean) { return JNI_TRUE; }
static jboolean RN_nSetOutlineRoundRect(JNIEnv*, jclass, jlong, jint, jint, jint, jint, jfloat, jfloat) { return JNI_TRUE; }
static jboolean RN_nSetOutlinePath(JNIEnv*, jclass, jlong, jlong, jfloat) { return JNI_TRUE; }
static jboolean RN_nSetOutlineEmpty(JNIEnv*, jclass, jlong) { return JNI_TRUE; }
static jboolean RN_nSetOutlineNone(JNIEnv*, jclass, jlong) { return JNI_TRUE; }
static jboolean RN_nClearStretch(JNIEnv*, jclass, jlong) { return JNI_TRUE; }
static jboolean RN_nStretch(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat, jfloat) { return JNI_TRUE; }
static jboolean RN_nHasShadow(JNIEnv*, jclass, jlong) { return JNI_TRUE; }
static jboolean RN_nSetSpotShadowColor(JNIEnv*, jclass, jlong, jint) { return JNI_TRUE; }
static jboolean RN_nSetAmbientShadowColor(JNIEnv*, jclass, jlong, jint) { return JNI_TRUE; }
static jint RN_nGetSpotShadowColor(JNIEnv*, jclass, jlong) { return 0; }
static jint RN_nGetAmbientShadowColor(JNIEnv*, jclass, jlong) { return 0; }
static jboolean RN_nSetClipToOutline(JNIEnv*, jclass, jlong, jboolean) { return JNI_TRUE; }
static jboolean RN_nSetRevealClip(JNIEnv*, jclass, jlong, jboolean, jfloat, jfloat, jfloat) { return JNI_TRUE; }
static jboolean RN_nSetAlpha(JNIEnv*, jclass, jlong, jfloat) { return JNI_TRUE; }
static jboolean RN_nSetRenderEffect(JNIEnv*, jclass, jlong, jlong) { return JNI_TRUE; }
static jboolean RN_nSetHasOverlappingRendering(JNIEnv*, jclass, jlong, jboolean) { return JNI_TRUE; }
static void RN_nSetUsageHint(JNIEnv*, jclass, jlong, jint) { }
static jboolean RN_nSetElevation(JNIEnv*, jclass, jlong, jfloat) { return JNI_TRUE; }
static jboolean RN_nSetTranslationX(JNIEnv*, jclass, jlong, jfloat) { return JNI_TRUE; }
static jboolean RN_nSetTranslationY(JNIEnv*, jclass, jlong, jfloat) { return JNI_TRUE; }
static jboolean RN_nSetTranslationZ(JNIEnv*, jclass, jlong, jfloat) { return JNI_TRUE; }
static jboolean RN_nSetRotation(JNIEnv*, jclass, jlong, jfloat) { return JNI_TRUE; }
static jboolean RN_nSetRotationX(JNIEnv*, jclass, jlong, jfloat) { return JNI_TRUE; }
static jboolean RN_nSetRotationY(JNIEnv*, jclass, jlong, jfloat) { return JNI_TRUE; }
static jboolean RN_nSetScaleX(JNIEnv*, jclass, jlong, jfloat) { return JNI_TRUE; }
static jboolean RN_nSetScaleY(JNIEnv*, jclass, jlong, jfloat) { return JNI_TRUE; }
static jboolean RN_nSetStaticMatrix(JNIEnv*, jclass, jlong, jlong) { return JNI_TRUE; }
static jboolean RN_nSetAnimationMatrix(JNIEnv*, jclass, jlong, jlong) { return JNI_TRUE; }
static jboolean RN_nHasOverlappingRendering(JNIEnv*, jclass, jlong) { return JNI_TRUE; }
static jboolean RN_nGetAnimationMatrix(JNIEnv*, jclass, jlong, jlong) { return JNI_TRUE; }
static jboolean RN_nGetClipToOutline(JNIEnv*, jclass, jlong) { return JNI_TRUE; }
static jfloat RN_nGetAlpha(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat RN_nGetCameraDistance(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat RN_nGetScaleX(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat RN_nGetScaleY(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat RN_nGetElevation(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat RN_nGetTranslationX(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat RN_nGetTranslationY(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat RN_nGetTranslationZ(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat RN_nGetRotation(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat RN_nGetRotationX(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat RN_nGetRotationY(JNIEnv*, jclass, jlong) { return 0.0f; }
static jboolean RN_nIsPivotExplicitlySet(JNIEnv*, jclass, jlong) { return JNI_TRUE; }
static jfloat RN_nGetPivotX(JNIEnv*, jclass, jlong) { return 0.0f; }
static jfloat RN_nGetPivotY(JNIEnv*, jclass, jlong) { return 0.0f; }
static jint RN_nGetWidth(JNIEnv*, jclass, jlong) { return 0; }
static jint RN_nGetHeight(JNIEnv*, jclass, jlong) { return 0; }
static jboolean RN_nSetAllowForceDark(JNIEnv*, jclass, jlong, jboolean) { return JNI_TRUE; }
static jboolean RN_nGetAllowForceDark(JNIEnv*, jclass, jlong) { return JNI_TRUE; }
static jlong RN_nGetUniqueId(JNIEnv*, jclass, jlong) { return 0; }
static void RN_nSetIsTextureView(JNIEnv*, jclass, jlong) { }

const JNINativeMethod kMethods[] = {
    { "nCreate", "(Ljava/lang/String;)J", reinterpret_cast<void*>(RN_nCreate) },
    { "nGetNativeFinalizer", "()J", reinterpret_cast<void*>(RN_nGetNativeFinalizer) },
    { "nOutput", "(J)V", reinterpret_cast<void*>(RN_nOutput) },
    { "nGetUsageSize", "(J)I", reinterpret_cast<void*>(RN_nGetUsageSize) },
    { "nGetAllocatedSize", "(J)I", reinterpret_cast<void*>(RN_nGetAllocatedSize) },
    { "nRequestPositionUpdates", "(JLjava/lang/ref/WeakReference;)V", reinterpret_cast<void*>(RN_nRequestPositionUpdates) },
    { "nAddAnimator", "(JJ)V", reinterpret_cast<void*>(RN_nAddAnimator) },
    { "nEndAllAnimators", "(J)V", reinterpret_cast<void*>(RN_nEndAllAnimators) },
    { "nForceEndAnimators", "(J)V", reinterpret_cast<void*>(RN_nForceEndAnimators) },
    { "nDiscardDisplayList", "(J)V", reinterpret_cast<void*>(RN_nDiscardDisplayList) },
    { "nIsValid", "(J)Z", reinterpret_cast<void*>(RN_nIsValid) },
    { "nGetTransformMatrix", "(JJ)V", reinterpret_cast<void*>(RN_nGetTransformMatrix) },
    { "nGetInverseTransformMatrix", "(JJ)V", reinterpret_cast<void*>(RN_nGetInverseTransformMatrix) },
    { "nHasIdentityMatrix", "(J)Z", reinterpret_cast<void*>(RN_nHasIdentityMatrix) },
    { "nOffsetTopAndBottom", "(JI)Z", reinterpret_cast<void*>(RN_nOffsetTopAndBottom) },
    { "nOffsetLeftAndRight", "(JI)Z", reinterpret_cast<void*>(RN_nOffsetLeftAndRight) },
    { "nSetLeftTopRightBottom", "(JIIII)Z", reinterpret_cast<void*>(RN_nSetLeftTopRightBottom) },
    { "nSetLeft", "(JI)Z", reinterpret_cast<void*>(RN_nSetLeft) },
    { "nSetTop", "(JI)Z", reinterpret_cast<void*>(RN_nSetTop) },
    { "nSetRight", "(JI)Z", reinterpret_cast<void*>(RN_nSetRight) },
    { "nSetBottom", "(JI)Z", reinterpret_cast<void*>(RN_nSetBottom) },
    { "nGetLeft", "(J)I", reinterpret_cast<void*>(RN_nGetLeft) },
    { "nGetTop", "(J)I", reinterpret_cast<void*>(RN_nGetTop) },
    { "nGetRight", "(J)I", reinterpret_cast<void*>(RN_nGetRight) },
    { "nGetBottom", "(J)I", reinterpret_cast<void*>(RN_nGetBottom) },
    { "nSetCameraDistance", "(JF)Z", reinterpret_cast<void*>(RN_nSetCameraDistance) },
    { "nSetPivotY", "(JF)Z", reinterpret_cast<void*>(RN_nSetPivotY) },
    { "nSetPivotX", "(JF)Z", reinterpret_cast<void*>(RN_nSetPivotX) },
    { "nResetPivot", "(J)Z", reinterpret_cast<void*>(RN_nResetPivot) },
    { "nSetLayerType", "(JI)Z", reinterpret_cast<void*>(RN_nSetLayerType) },
    { "nGetLayerType", "(J)I", reinterpret_cast<void*>(RN_nGetLayerType) },
    { "nSetLayerPaint", "(JJ)Z", reinterpret_cast<void*>(RN_nSetLayerPaint) },
    { "nSetClipToBounds", "(JZ)Z", reinterpret_cast<void*>(RN_nSetClipToBounds) },
    { "nGetClipToBounds", "(J)Z", reinterpret_cast<void*>(RN_nGetClipToBounds) },
    { "nSetClipBounds", "(JIIII)Z", reinterpret_cast<void*>(RN_nSetClipBounds) },
    { "nSetClipBoundsEmpty", "(J)Z", reinterpret_cast<void*>(RN_nSetClipBoundsEmpty) },
    { "nSetProjectBackwards", "(JZ)Z", reinterpret_cast<void*>(RN_nSetProjectBackwards) },
    { "nSetProjectionReceiver", "(JZ)Z", reinterpret_cast<void*>(RN_nSetProjectionReceiver) },
    { "nSetOutlineRoundRect", "(JIIIIFF)Z", reinterpret_cast<void*>(RN_nSetOutlineRoundRect) },
    { "nSetOutlinePath", "(JJF)Z", reinterpret_cast<void*>(RN_nSetOutlinePath) },
    { "nSetOutlineEmpty", "(J)Z", reinterpret_cast<void*>(RN_nSetOutlineEmpty) },
    { "nSetOutlineNone", "(J)Z", reinterpret_cast<void*>(RN_nSetOutlineNone) },
    { "nClearStretch", "(J)Z", reinterpret_cast<void*>(RN_nClearStretch) },
    { "nStretch", "(JFFFF)Z", reinterpret_cast<void*>(RN_nStretch) },
    { "nHasShadow", "(J)Z", reinterpret_cast<void*>(RN_nHasShadow) },
    { "nSetSpotShadowColor", "(JI)Z", reinterpret_cast<void*>(RN_nSetSpotShadowColor) },
    { "nSetAmbientShadowColor", "(JI)Z", reinterpret_cast<void*>(RN_nSetAmbientShadowColor) },
    { "nGetSpotShadowColor", "(J)I", reinterpret_cast<void*>(RN_nGetSpotShadowColor) },
    { "nGetAmbientShadowColor", "(J)I", reinterpret_cast<void*>(RN_nGetAmbientShadowColor) },
    { "nSetClipToOutline", "(JZ)Z", reinterpret_cast<void*>(RN_nSetClipToOutline) },
    { "nSetRevealClip", "(JZFFF)Z", reinterpret_cast<void*>(RN_nSetRevealClip) },
    { "nSetAlpha", "(JF)Z", reinterpret_cast<void*>(RN_nSetAlpha) },
    { "nSetRenderEffect", "(JJ)Z", reinterpret_cast<void*>(RN_nSetRenderEffect) },
    { "nSetHasOverlappingRendering", "(JZ)Z", reinterpret_cast<void*>(RN_nSetHasOverlappingRendering) },
    { "nSetUsageHint", "(JI)V", reinterpret_cast<void*>(RN_nSetUsageHint) },
    { "nSetElevation", "(JF)Z", reinterpret_cast<void*>(RN_nSetElevation) },
    { "nSetTranslationX", "(JF)Z", reinterpret_cast<void*>(RN_nSetTranslationX) },
    { "nSetTranslationY", "(JF)Z", reinterpret_cast<void*>(RN_nSetTranslationY) },
    { "nSetTranslationZ", "(JF)Z", reinterpret_cast<void*>(RN_nSetTranslationZ) },
    { "nSetRotation", "(JF)Z", reinterpret_cast<void*>(RN_nSetRotation) },
    { "nSetRotationX", "(JF)Z", reinterpret_cast<void*>(RN_nSetRotationX) },
    { "nSetRotationY", "(JF)Z", reinterpret_cast<void*>(RN_nSetRotationY) },
    { "nSetScaleX", "(JF)Z", reinterpret_cast<void*>(RN_nSetScaleX) },
    { "nSetScaleY", "(JF)Z", reinterpret_cast<void*>(RN_nSetScaleY) },
    { "nSetStaticMatrix", "(JJ)Z", reinterpret_cast<void*>(RN_nSetStaticMatrix) },
    { "nSetAnimationMatrix", "(JJ)Z", reinterpret_cast<void*>(RN_nSetAnimationMatrix) },
    { "nHasOverlappingRendering", "(J)Z", reinterpret_cast<void*>(RN_nHasOverlappingRendering) },
    { "nGetAnimationMatrix", "(JJ)Z", reinterpret_cast<void*>(RN_nGetAnimationMatrix) },
    { "nGetClipToOutline", "(J)Z", reinterpret_cast<void*>(RN_nGetClipToOutline) },
    { "nGetAlpha", "(J)F", reinterpret_cast<void*>(RN_nGetAlpha) },
    { "nGetCameraDistance", "(J)F", reinterpret_cast<void*>(RN_nGetCameraDistance) },
    { "nGetScaleX", "(J)F", reinterpret_cast<void*>(RN_nGetScaleX) },
    { "nGetScaleY", "(J)F", reinterpret_cast<void*>(RN_nGetScaleY) },
    { "nGetElevation", "(J)F", reinterpret_cast<void*>(RN_nGetElevation) },
    { "nGetTranslationX", "(J)F", reinterpret_cast<void*>(RN_nGetTranslationX) },
    { "nGetTranslationY", "(J)F", reinterpret_cast<void*>(RN_nGetTranslationY) },
    { "nGetTranslationZ", "(J)F", reinterpret_cast<void*>(RN_nGetTranslationZ) },
    { "nGetRotation", "(J)F", reinterpret_cast<void*>(RN_nGetRotation) },
    { "nGetRotationX", "(J)F", reinterpret_cast<void*>(RN_nGetRotationX) },
    { "nGetRotationY", "(J)F", reinterpret_cast<void*>(RN_nGetRotationY) },
    { "nIsPivotExplicitlySet", "(J)Z", reinterpret_cast<void*>(RN_nIsPivotExplicitlySet) },
    { "nGetPivotX", "(J)F", reinterpret_cast<void*>(RN_nGetPivotX) },
    { "nGetPivotY", "(J)F", reinterpret_cast<void*>(RN_nGetPivotY) },
    { "nGetWidth", "(J)I", reinterpret_cast<void*>(RN_nGetWidth) },
    { "nGetHeight", "(J)I", reinterpret_cast<void*>(RN_nGetHeight) },
    { "nSetAllowForceDark", "(JZ)Z", reinterpret_cast<void*>(RN_nSetAllowForceDark) },
    { "nGetAllowForceDark", "(J)Z", reinterpret_cast<void*>(RN_nGetAllowForceDark) },
    { "nGetUniqueId", "(J)J", reinterpret_cast<void*>(RN_nGetUniqueId) },
    { "nSetIsTextureView", "(J)V", reinterpret_cast<void*>(RN_nSetIsTextureView) },
};

}  // namespace

int register_android_graphics_RenderNode(JNIEnv* env) {
    jclass clazz = env->FindClass("android/graphics/RenderNode");
    if (!clazz) return -1;
    jint rc = env->RegisterNatives(clazz, kMethods,
                                    sizeof(kMethods) / sizeof(kMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
