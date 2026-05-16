// ============================================================================
// android_graphics_HardwareRenderer.cpp
//
// Minimal HardwareRenderer JNI registration. handleBindApplication path
// (line 6877+) calls setDebuggingEnabled/setIsSystemOrPersistent which native
// out to nSetDebuggingEnabled/nSetIsSystemOrPersistent. Without these JNI
// registered: UnsatisfiedLinkError → handleBindApplication crash.
//
// All 70 natives included as no-op safe-default to avoid iterative
// UnsatisfiedLinkError chasing. HelloWorld TextView display goes through
// hwui's full render pipeline at much later stage; minimal stubs unblock
// boot path (handleBindApplication / makeApplicationInner).
//
// Per overall_design §15 + feedback_java_full_cpp_stub_only:
// stub at C++ JNI layer 是允许的（IPC interface 的 native 边界）。
// Java HardwareRenderer class itself remains AOSP-original.
// ============================================================================

#include <jni.h>

namespace android {

namespace {

// Top-level natives
void  HR_disableVsync(JNIEnv*, jclass) {}
void  HR_preload(JNIEnv*, jclass) {}

// Process stats
void  HR_nRotateProcessStatsBuffer(JNIEnv*, jclass) {}
void  HR_nSetProcessStatsBuffer(JNIEnv*, jclass, jint /*fd*/) {}
jint  HR_nGetRenderThreadTid(JNIEnv*, jclass, jlong /*nativeProxy*/) { return 0; }

// Proxy / RenderNode lifecycle (return non-zero handles so Java code accepts)
static const jlong kFakeHandle = 1L;
jlong HR_nCreateRootRenderNode(JNIEnv*, jclass) { return kFakeHandle; }
jlong HR_nCreateProxy(JNIEnv*, jclass, jboolean, jlong) { return kFakeHandle; }
void  HR_nDeleteProxy(JNIEnv*, jclass, jlong) {}
jboolean HR_nLoadSystemProperties(JNIEnv*, jclass, jlong) { return JNI_FALSE; }
void  HR_nSetName(JNIEnv*, jclass, jlong, jstring) {}
void  HR_nSetSurface(JNIEnv*, jclass, jlong, jobject, jboolean) {}
void  HR_nSetSurfaceControl(JNIEnv*, jclass, jlong, jlong) {}
jboolean HR_nPause(JNIEnv*, jclass, jlong) { return JNI_FALSE; }
void  HR_nSetStopped(JNIEnv*, jclass, jlong, jboolean) {}
void  HR_nSetLightGeometry(JNIEnv*, jclass, jlong, jfloat, jfloat, jfloat, jfloat) {}
void  HR_nSetLightAlpha(JNIEnv*, jclass, jlong, jfloat, jfloat) {}
void  HR_nSetOpaque(JNIEnv*, jclass, jlong, jboolean) {}
jfloat HR_nSetColorMode(JNIEnv*, jclass, jlong, jint) { return 1.0f; }
void  HR_nSetTargetSdrHdrRatio(JNIEnv*, jclass, jlong, jfloat) {}
void  HR_nSetSdrWhitePoint(JNIEnv*, jclass, jlong, jfloat) {}
void  HR_nSetIsHighEndGfx(JNIEnv*, jclass, jboolean) {}
void  HR_nSetIsLowRam(JNIEnv*, jclass, jboolean) {}
void  HR_nSetIsSystemOrPersistent(JNIEnv*, jclass, jboolean) {}
jint  HR_nSyncAndDrawFrame(JNIEnv*, jclass, jlong, jlongArray, jint) { return 0; }
void  HR_nDestroy(JNIEnv*, jclass, jlong, jlong) {}
void  HR_nRegisterAnimatingRenderNode(JNIEnv*, jclass, jlong, jlong) {}
void  HR_nRegisterVectorDrawableAnimator(JNIEnv*, jclass, jlong, jlong) {}

// Layers
jlong HR_nCreateTextureLayer(JNIEnv*, jclass, jlong) { return kFakeHandle; }
void  HR_nBuildLayer(JNIEnv*, jclass, jlong, jlong) {}
jboolean HR_nCopyLayerInto(JNIEnv*, jclass, jlong, jlong, jlong) { return JNI_FALSE; }
void  HR_nPushLayerUpdate(JNIEnv*, jclass, jlong, jlong) {}
void  HR_nCancelLayerUpdate(JNIEnv*, jclass, jlong, jlong) {}
void  HR_nDetachSurfaceTexture(JNIEnv*, jclass, jlong, jlong) {}
void  HR_nDestroyHardwareResources(JNIEnv*, jclass, jlong) {}

// Trim / cache
void  HR_nTrimMemory(JNIEnv*, jclass, jint) {}
void  HR_nTrimCaches(JNIEnv*, jclass, jint) {}
void  HR_nOverrideProperty(JNIEnv*, jclass, jstring, jstring) {}

// Frame
void  HR_nFence(JNIEnv*, jclass, jlong) {}
void  HR_nStopDrawing(JNIEnv*, jclass, jlong) {}
void  HR_nNotifyFramePending(JNIEnv*, jclass, jlong) {}
void  HR_nDumpProfileInfo(JNIEnv*, jclass, jlong, jobject, jint) {}
void  HR_nDumpGlobalProfileInfo(JNIEnv*, jclass, jobject, jint) {}

// RenderNode
void  HR_nAddRenderNode(JNIEnv*, jclass, jlong, jlong, jboolean) {}
void  HR_nRemoveRenderNode(JNIEnv*, jclass, jlong, jlong) {}
void  HR_nDrawRenderNode(JNIEnv*, jclass, jlong, jlong) {}
void  HR_nSetContentDrawBounds(JNIEnv*, jclass, jlong, jint, jint, jint, jint) {}
void  HR_nForceDrawNextFrame(JNIEnv*, jclass, jlong) {}

// Callbacks
void  HR_nSetPictureCaptureCallback(JNIEnv*, jclass, jlong, jobject) {}
void  HR_nSetASurfaceTransactionCallback(JNIEnv*, jclass, jlong, jobject) {}
void  HR_nSetPrepareSurfaceControlForWebviewCallback(JNIEnv*, jclass, jlong, jobject) {}
void  HR_nSetFrameCallback(JNIEnv*, jclass, jlong, jobject) {}
void  HR_nSetFrameCommitCallback(JNIEnv*, jclass, jlong, jobject) {}
void  HR_nSetFrameCompleteCallback(JNIEnv*, jclass, jlong, jobject) {}

// Observer / copy
void  HR_nAddObserver(JNIEnv*, jclass, jlong, jlong) {}
void  HR_nRemoveObserver(JNIEnv*, jclass, jlong, jlong) {}
void  HR_nCopySurfaceInto(JNIEnv*, jclass, jobject, jint, jint, jint, jint, jobject) {}
jobject HR_nCreateHardwareBitmap(JNIEnv*, jclass, jlong, jint, jint) { return nullptr; }

// Globals
void  HR_nSetHighContrastText(JNIEnv*, jclass, jboolean) {}
void  HR_nSetDebuggingEnabled(JNIEnv*, jclass, jboolean) {}
void  HR_nSetIsolatedProcess(JNIEnv*, jclass, jboolean) {}
void  HR_nSetContextPriority(JNIEnv*, jclass, jint) {}
void  HR_nAllocateBuffers(JNIEnv*, jclass, jlong) {}
void  HR_nSetForceDark(JNIEnv*, jclass, jlong, jboolean) {}
void  HR_nSetDisplayDensityDpi(JNIEnv*, jclass, jint) {}
void  HR_nInitDisplayInfo(JNIEnv*, jclass, jint, jint, jfloat, jint, jlong, jlong, jboolean, jboolean) {}
void  HR_nSetDrawingEnabled(JNIEnv*, jclass, jboolean) {}
jboolean HR_nIsDrawingEnabled(JNIEnv*, jclass) { return JNI_TRUE; }
void  HR_nSetRtAnimationsEnabled(JNIEnv*, jclass, jboolean) {}
void  HR_nNotifyCallbackPending(JNIEnv*, jclass, jlong) {}
void  HR_nNotifyExpensiveFrame(JNIEnv*, jclass, jlong) {}

// Inner-class type signatures used as parameters
#define SIG_PCC "Landroid/graphics/HardwareRenderer$PictureCapturedCallback;"
#define SIG_ASTC "Landroid/graphics/HardwareRenderer$ASurfaceTransactionCallback;"
#define SIG_PSCWVC "Landroid/graphics/HardwareRenderer$PrepareSurfaceControlForWebviewCallback;"
#define SIG_FDC "Landroid/graphics/HardwareRenderer$FrameDrawingCallback;"
#define SIG_FCC "Landroid/graphics/HardwareRenderer$FrameCommitCallback;"
#define SIG_FCpC "Landroid/graphics/HardwareRenderer$FrameCompleteCallback;"
#define SIG_CRC "Landroid/graphics/HardwareRenderer$CopyRequest;"
#define SIG_FD "Ljava/io/FileDescriptor;"
#define SIG_SF "Landroid/view/Surface;"
#define SIG_BMP "Landroid/graphics/Bitmap;"
#define SIG_STR "Ljava/lang/String;"

const JNINativeMethod kMethods[] = {
    {"disableVsync",          "()V",                              (void*)HR_disableVsync},
    {"preload",               "()V",                              (void*)HR_preload},
    {"nRotateProcessStatsBuffer", "()V",                          (void*)HR_nRotateProcessStatsBuffer},
    {"nSetProcessStatsBuffer","(I)V",                             (void*)HR_nSetProcessStatsBuffer},
    {"nGetRenderThreadTid",   "(J)I",                             (void*)HR_nGetRenderThreadTid},
    {"nCreateRootRenderNode", "()J",                              (void*)HR_nCreateRootRenderNode},
    {"nCreateProxy",          "(ZJ)J",                            (void*)HR_nCreateProxy},
    {"nDeleteProxy",          "(J)V",                             (void*)HR_nDeleteProxy},
    {"nLoadSystemProperties", "(J)Z",                             (void*)HR_nLoadSystemProperties},
    {"nSetName",              "(J" SIG_STR ")V",                  (void*)HR_nSetName},
    {"nSetSurface",           "(J" SIG_SF "Z)V",                  (void*)HR_nSetSurface},
    {"nSetSurfaceControl",    "(JJ)V",                            (void*)HR_nSetSurfaceControl},
    {"nPause",                "(J)Z",                             (void*)HR_nPause},
    {"nSetStopped",           "(JZ)V",                            (void*)HR_nSetStopped},
    {"nSetLightGeometry",     "(JFFFF)V",                         (void*)HR_nSetLightGeometry},
    {"nSetLightAlpha",        "(JFF)V",                           (void*)HR_nSetLightAlpha},
    {"nSetOpaque",            "(JZ)V",                            (void*)HR_nSetOpaque},
    {"nSetColorMode",         "(JI)F",                            (void*)HR_nSetColorMode},
    {"nSetTargetSdrHdrRatio", "(JF)V",                            (void*)HR_nSetTargetSdrHdrRatio},
    {"nSetSdrWhitePoint",     "(JF)V",                            (void*)HR_nSetSdrWhitePoint},
    {"nSetIsHighEndGfx",      "(Z)V",                             (void*)HR_nSetIsHighEndGfx},
    {"nSetIsLowRam",          "(Z)V",                             (void*)HR_nSetIsLowRam},
    {"nSetIsSystemOrPersistent","(Z)V",                           (void*)HR_nSetIsSystemOrPersistent},
    {"nSyncAndDrawFrame",     "(J[JI)I",                          (void*)HR_nSyncAndDrawFrame},
    {"nDestroy",              "(JJ)V",                            (void*)HR_nDestroy},
    {"nRegisterAnimatingRenderNode","(JJ)V",                      (void*)HR_nRegisterAnimatingRenderNode},
    {"nRegisterVectorDrawableAnimator","(JJ)V",                   (void*)HR_nRegisterVectorDrawableAnimator},
    {"nCreateTextureLayer",   "(J)J",                             (void*)HR_nCreateTextureLayer},
    {"nBuildLayer",           "(JJ)V",                            (void*)HR_nBuildLayer},
    {"nCopyLayerInto",        "(JJJ)Z",                           (void*)HR_nCopyLayerInto},
    {"nPushLayerUpdate",      "(JJ)V",                            (void*)HR_nPushLayerUpdate},
    {"nCancelLayerUpdate",    "(JJ)V",                            (void*)HR_nCancelLayerUpdate},
    {"nDetachSurfaceTexture", "(JJ)V",                            (void*)HR_nDetachSurfaceTexture},
    {"nDestroyHardwareResources","(J)V",                          (void*)HR_nDestroyHardwareResources},
    {"nTrimMemory",           "(I)V",                             (void*)HR_nTrimMemory},
    {"nTrimCaches",           "(I)V",                             (void*)HR_nTrimCaches},
    {"nOverrideProperty",     "(" SIG_STR SIG_STR ")V",           (void*)HR_nOverrideProperty},
    {"nFence",                "(J)V",                             (void*)HR_nFence},
    {"nStopDrawing",          "(J)V",                             (void*)HR_nStopDrawing},
    {"nNotifyFramePending",   "(J)V",                             (void*)HR_nNotifyFramePending},
    {"nDumpProfileInfo",      "(J" SIG_FD "I)V",                  (void*)HR_nDumpProfileInfo},
    {"nDumpGlobalProfileInfo","(" SIG_FD "I)V",                   (void*)HR_nDumpGlobalProfileInfo},
    {"nAddRenderNode",        "(JJZ)V",                           (void*)HR_nAddRenderNode},
    {"nRemoveRenderNode",     "(JJ)V",                            (void*)HR_nRemoveRenderNode},
    {"nDrawRenderNode",       "(JJ)V",                            (void*)HR_nDrawRenderNode},
    {"nSetContentDrawBounds", "(JIIII)V",                         (void*)HR_nSetContentDrawBounds},
    {"nForceDrawNextFrame",   "(J)V",                             (void*)HR_nForceDrawNextFrame},
    {"nSetPictureCaptureCallback","(J" SIG_PCC ")V",              (void*)HR_nSetPictureCaptureCallback},
    {"nSetASurfaceTransactionCallback","(J" SIG_ASTC ")V",        (void*)HR_nSetASurfaceTransactionCallback},
    {"nSetPrepareSurfaceControlForWebviewCallback","(J" SIG_PSCWVC ")V", (void*)HR_nSetPrepareSurfaceControlForWebviewCallback},
    {"nSetFrameCallback",     "(J" SIG_FDC ")V",                  (void*)HR_nSetFrameCallback},
    {"nSetFrameCommitCallback","(J" SIG_FCC ")V",                 (void*)HR_nSetFrameCommitCallback},
    {"nSetFrameCompleteCallback","(J" SIG_FCpC ")V",              (void*)HR_nSetFrameCompleteCallback},
    {"nAddObserver",          "(JJ)V",                            (void*)HR_nAddObserver},
    {"nRemoveObserver",       "(JJ)V",                            (void*)HR_nRemoveObserver},
    {"nCopySurfaceInto",      "(" SIG_SF "IIII" SIG_CRC ")V",     (void*)HR_nCopySurfaceInto},
    {"nCreateHardwareBitmap", "(JII)" SIG_BMP,                    (void*)HR_nCreateHardwareBitmap},
    {"nSetHighContrastText",  "(Z)V",                             (void*)HR_nSetHighContrastText},
    {"nSetDebuggingEnabled",  "(Z)V",                             (void*)HR_nSetDebuggingEnabled},
    {"nSetIsolatedProcess",   "(Z)V",                             (void*)HR_nSetIsolatedProcess},
    {"nSetContextPriority",   "(I)V",                             (void*)HR_nSetContextPriority},
    {"nAllocateBuffers",      "(J)V",                             (void*)HR_nAllocateBuffers},
    {"nSetForceDark",         "(JZ)V",                            (void*)HR_nSetForceDark},
    {"nSetDisplayDensityDpi", "(I)V",                             (void*)HR_nSetDisplayDensityDpi},
    {"nInitDisplayInfo",      "(IIFIJJZZ)V",                      (void*)HR_nInitDisplayInfo},
    {"nSetDrawingEnabled",    "(Z)V",                             (void*)HR_nSetDrawingEnabled},
    {"nIsDrawingEnabled",     "()Z",                              (void*)HR_nIsDrawingEnabled},
    {"nSetRtAnimationsEnabled","(Z)V",                            (void*)HR_nSetRtAnimationsEnabled},
    {"nNotifyCallbackPending","(J)V",                             (void*)HR_nNotifyCallbackPending},
    {"nNotifyExpensiveFrame", "(J)V",                             (void*)HR_nNotifyExpensiveFrame},
};

}  // namespace

int register_android_graphics_HardwareRenderer(JNIEnv* env) {
    jclass clazz = env->FindClass("android/graphics/HardwareRenderer");
    if (!clazz) return -1;
    jint rc = env->RegisterNatives(clazz, kMethods,
                                    sizeof(kMethods) / sizeof(kMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
