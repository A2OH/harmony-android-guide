// ============================================================================
// android_content_res_ApkAssets.cpp
//
// Minimal JNI stubs for android.content.res.ApkAssets.
// Triggered by ContextImpl.createSystemContext → Resources → ApkAssets.loadFromPath
// → ApkAssets.<clinit> native methods resolution.
//
// AOSP ApkAssets real implementation relies on libandroidfw (ResTable / ApkAssets
// class in native) to parse resources.arsc + resources.apk. Adapter has neither
// libandroidfw nor a valid framework-res.apk content tree. Stubs return safe
// defaults to get past clinit + context creation; actual resource lookup will
// fail later (hit a separate stub or throw explicit NotFound), handled when
// real TextView drawing needs real resources.
//
// Native handle convention (same as Binder/SurfaceControl/AssetManager.Theme):
//   nativeLoad* → malloc(8) unique ptr (NativeAllocationRegistry would reject 0)
//   nativeDestroy → free(ptr)
//
// All other methods (nativeGetStringBlock / nativeOpenXml / nativeGetAssetPath
// etc.) return safe defaults (0 / null / false). If the caller dereferences
// the "string block" handle, libandroidfw code path will crash — acceptable
// for current Phase 8 #2b probe stage which only tries to reach
// WindowManager.addView. Real resource pipeline is a later phase.
// ============================================================================

#include "AndroidRuntime.h"

#include <jni.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>

namespace android {

namespace {

extern "C" void ApkAssets_noop_free(void* ptr) {
    if (ptr) free(ptr);
}

static jlong make_fake_handle() {
    void* p = malloc(8);
    if (p) memset(p, 0, 8);
    return reinterpret_cast<jlong>(p);
}

// All nativeLoad variants return a unique fake handle.
jlong AA_nativeLoad(JNIEnv*, jclass, jint /*format*/, jstring /*path*/,
                     jint /*flags*/, jobject /*provider*/) {
    return make_fake_handle();
}
jlong AA_nativeLoadEmpty(JNIEnv*, jclass, jint /*flags*/, jobject /*provider*/) {
    return make_fake_handle();
}
jlong AA_nativeLoadFd(JNIEnv*, jclass, jint /*format*/, jobject /*fd*/,
                      jstring /*path*/, jint /*flags*/, jobject /*provider*/) {
    return make_fake_handle();
}
jlong AA_nativeLoadFdOffsets(JNIEnv*, jclass, jint /*format*/, jobject /*fd*/,
                              jstring /*path*/, jlong /*offset*/, jlong /*len*/,
                              jint /*flags*/, jobject /*provider*/) {
    return make_fake_handle();
}
void AA_nativeDestroy(JNIEnv*, jclass, jlong ptr) {
    ApkAssets_noop_free(reinterpret_cast<void*>(ptr));
}
jstring AA_nativeGetAssetPath(JNIEnv* env, jclass, jlong /*ptr*/) {
    return env->NewStringUTF("/system/android/framework/framework-res.apk");
}
jstring AA_nativeGetDebugName(JNIEnv* env, jclass, jlong /*ptr*/) {
    return env->NewStringUTF("AdapterApkAssets-stub");
}
jlong AA_nativeGetStringBlock(JNIEnv*, jclass, jlong /*ptr*/) {
    // Return 0 — Java side may null-check; if it dereferences, later crash is
    // separate blocker surfacing the need for real libandroidfw.
    return 0;
}
jboolean AA_nativeIsUpToDate(jlong /*ptr*/) {
    return JNI_TRUE;
}
jlong AA_nativeOpenXml(JNIEnv*, jclass, jlong /*ptr*/, jstring /*path*/) {
    return 0;
}
jobject AA_nativeGetOverlayableInfo(JNIEnv*, jclass, jlong /*ptr*/, jstring /*name*/) {
    return nullptr;
}
jboolean AA_nativeDefinesOverlayable(JNIEnv*, jclass, jlong /*ptr*/) {
    return JNI_FALSE;
}

const JNINativeMethod kApkAssetsMethods[] = {
    { "nativeLoad",
      "(ILjava/lang/String;ILandroid/content/res/loader/AssetsProvider;)J",
      reinterpret_cast<void*>(AA_nativeLoad) },
    { "nativeLoadEmpty",
      "(ILandroid/content/res/loader/AssetsProvider;)J",
      reinterpret_cast<void*>(AA_nativeLoadEmpty) },
    { "nativeLoadFd",
      "(ILjava/io/FileDescriptor;Ljava/lang/String;ILandroid/content/res/loader/AssetsProvider;)J",
      reinterpret_cast<void*>(AA_nativeLoadFd) },
    { "nativeLoadFdOffsets",
      "(ILjava/io/FileDescriptor;Ljava/lang/String;JJILandroid/content/res/loader/AssetsProvider;)J",
      reinterpret_cast<void*>(AA_nativeLoadFdOffsets) },
    { "nativeDestroy", "(J)V", reinterpret_cast<void*>(AA_nativeDestroy) },
    { "nativeGetAssetPath", "(J)Ljava/lang/String;", reinterpret_cast<void*>(AA_nativeGetAssetPath) },
    { "nativeGetDebugName", "(J)Ljava/lang/String;", reinterpret_cast<void*>(AA_nativeGetDebugName) },
    { "nativeGetStringBlock", "(J)J", reinterpret_cast<void*>(AA_nativeGetStringBlock) },
    { "nativeIsUpToDate", "(J)Z", reinterpret_cast<void*>(AA_nativeIsUpToDate) },
    { "nativeOpenXml", "(JLjava/lang/String;)J", reinterpret_cast<void*>(AA_nativeOpenXml) },
    { "nativeGetOverlayableInfo",
      "(JLjava/lang/String;)Landroid/content/om/OverlayableInfo;",
      reinterpret_cast<void*>(AA_nativeGetOverlayableInfo) },
    { "nativeDefinesOverlayable", "(J)Z", reinterpret_cast<void*>(AA_nativeDefinesOverlayable) },
};

}  // namespace

int register_android_content_res_ApkAssets(JNIEnv* env) {
    jclass clazz = env->FindClass("android/content/res/ApkAssets");
    if (!clazz) {
        return -1;
    }
    jint rc = env->RegisterNatives(clazz, kApkAssetsMethods,
                                    sizeof(kApkAssetsMethods) / sizeof(kApkAssetsMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
