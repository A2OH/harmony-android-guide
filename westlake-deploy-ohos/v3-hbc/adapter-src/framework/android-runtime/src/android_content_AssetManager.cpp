// ============================================================================
// android_content_AssetManager.cpp
//
// JNI stubs for android.content.res.AssetManager covering all 55 native
// methods.  Hello World launchActivity path (B.20.r10) hits
// nativeThemeCreate / nativeApplyStyle / nativeRetrieveAttributes /
// nativeGetResourceValue etc. via ContextImpl.initializeTheme →
// Resources.newTheme → ResourcesImpl$ThemeImpl.<init>, so we register the
// full surface in one pass to avoid round-trip iteration.
//
// Stub semantics:
//   nativeCreate / nativeThemeCreate / nativeOpen* return malloc'd dummy
//   handle (non-zero) so NativeAllocationRegistry / Theme objects don't
//   reject a 0 handle.  Free function freed via AM_noop_free.
//   Resource lookup / attribute resolve return zero / empty / nullptr —
//   APK with hardcoded layouts (no R.string lookup) works; layouts that
//   reference real resources will silently degrade.
// ============================================================================

#include "AndroidRuntime.h"
#include <cstdlib>
#include <cstring>
#include <jni.h>

namespace android {
namespace {

extern "C" void AM_noop_free(void* /*ptr*/) { }

jlong AM_nativeGetThemeFreeFunction(JNIEnv*, jclass) {
    return reinterpret_cast<jlong>(&AM_noop_free);
}

// Allocate small dummy block so handle is non-zero; resource methods
// don't dereference it.
jlong AM_nativeCreate(JNIEnv*, jclass) {
    void* p = std::malloc(8);
    if (p) std::memset(p, 0, 8);
    return reinterpret_cast<jlong>(p);
}

void AM_nativeDestroy(JNIEnv*, jclass, jlong ptr) {
    if (ptr) std::free(reinterpret_cast<void*>(ptr));
}

void AM_nativeSetApkAssets(JNIEnv*, jclass, jlong, jobjectArray, jboolean) {}
void AM_nativeSetConfiguration(JNIEnv*, jclass,
        jlong, jint, jint, jstring,
        jint, jint, jint, jint,
        jint, jint, jint, jint,
        jint, jint, jint, jint,
        jint, jint, jint, jint) {}

jobject AM_nativeGetAssignedPackageIdentifiers(JNIEnv* env, jclass, jlong, jboolean, jboolean) {
    jclass spClass = env->FindClass("android/util/SparseArray");
    if (!spClass) return nullptr;
    jmethodID ctor = env->GetMethodID(spClass, "<init>", "()V");
    jobject sp = env->NewObject(spClass, ctor);
    env->DeleteLocalRef(spClass);
    return sp;
}

jboolean AM_nativeContainsAllocatedTable(JNIEnv*, jclass, jlong) { return JNI_FALSE; }

// === Asset I/O — return "not found" sentinels ===
static jobjectArray AM_nativeList(JNIEnv*, jclass, jlong, jstring) { return nullptr; }
static jlong AM_nativeOpenAsset(JNIEnv*, jclass, jlong, jstring, jint) { return 0; }
static jobject AM_nativeOpenAssetFd(JNIEnv*, jclass, jlong, jstring, jlongArray) { return nullptr; }
static jlong AM_nativeOpenNonAsset(JNIEnv*, jclass, jlong, jint, jstring, jint) { return 0; }
static jobject AM_nativeOpenNonAssetFd(JNIEnv*, jclass, jlong, jint, jstring, jlongArray) { return nullptr; }
static jlong AM_nativeOpenXmlAsset(JNIEnv*, jclass, jlong, jint, jstring) { return 0; }
static jlong AM_nativeOpenXmlAssetFd(JNIEnv*, jclass, jlong, jint, jobject) { return 0; }

// === Resource lookup — 0/null indicates "not found"; framework tolerates ===
static jint AM_nativeGetResourceValue(JNIEnv*, jclass, jlong, jint, jshort, jobject, jboolean) { return 0; }
static jint AM_nativeGetResourceBagValue(JNIEnv*, jclass, jlong, jint, jint, jobject) { return 0; }
static jintArray AM_nativeGetStyleAttributes(JNIEnv*, jclass, jlong, jint) { return nullptr; }
static jobjectArray AM_nativeGetResourceStringArray(JNIEnv*, jclass, jlong, jint) { return nullptr; }
static jintArray AM_nativeGetResourceStringArrayInfo(JNIEnv*, jclass, jlong, jint) { return nullptr; }
static jintArray AM_nativeGetResourceIntArray(JNIEnv*, jclass, jlong, jint) { return nullptr; }
static jint AM_nativeGetResourceArraySize(JNIEnv*, jclass, jlong, jint) { return 0; }
static jint AM_nativeGetResourceArray(JNIEnv*, jclass, jlong, jint, jintArray) { return 0; }
static jint AM_nativeGetResourceIdentifier(JNIEnv*, jclass, jlong, jstring, jstring, jstring) { return 0; }
static jstring AM_nativeGetResourceName(JNIEnv*, jclass, jlong, jint) { return nullptr; }
static jstring AM_nativeGetResourcePackageName(JNIEnv*, jclass, jlong, jint) { return nullptr; }
static jstring AM_nativeGetResourceTypeName(JNIEnv*, jclass, jlong, jint) { return nullptr; }
static jstring AM_nativeGetResourceEntryName(JNIEnv*, jclass, jlong, jint) { return nullptr; }
static jobjectArray AM_nativeGetLocales(JNIEnv*, jclass, jlong, jboolean) { return nullptr; }
static jobjectArray AM_nativeGetSizeConfigurations(JNIEnv*, jclass, jlong) { return nullptr; }
static jobjectArray AM_nativeGetSizeAndUiModeConfigurations(JNIEnv*, jclass, jlong) { return nullptr; }
static void AM_nativeSetResourceResolutionLoggingEnabled(JNIEnv*, jclass, jlong, jboolean) { }
static jstring AM_nativeGetLastResourceResolution(JNIEnv*, jclass, jlong) { return nullptr; }

// === Style / attribute resolution ===
static jintArray AM_nativeAttributeResolutionStack(JNIEnv*, jclass, jlong, jlong, jint, jint, jint) { return nullptr; }
static void AM_nativeApplyStyle(JNIEnv*, jclass, jlong, jlong, jint, jint, jlong, jintArray, jlong, jlong) { }
static jboolean AM_nativeResolveAttrs(JNIEnv*, jclass, jlong, jlong, jint, jint, jintArray, jintArray, jintArray, jintArray) { return JNI_FALSE; }
static jboolean AM_nativeRetrieveAttributes(JNIEnv*, jclass, jlong, jlong, jintArray, jintArray, jintArray) { return JNI_FALSE; }

// === Theme — handle must be non-zero so Theme.<init> stores it without
//     downstream NPE on dereference (we never actually dereference it). ===
static jlong AM_nativeThemeCreate(JNIEnv*, jclass, jlong) {
    void* p = std::malloc(8);
    if (p) std::memset(p, 0, 8);
    return reinterpret_cast<jlong>(p);
}
static void AM_nativeThemeApplyStyle(JNIEnv*, jclass, jlong, jlong, jint, jboolean) { }
static void AM_nativeThemeRebase(JNIEnv*, jclass, jlong, jlong, jintArray, jbooleanArray, jint) { }
static void AM_nativeThemeCopy(JNIEnv*, jclass, jlong, jlong, jlong, jlong) { }
static jint AM_nativeThemeGetAttributeValue(JNIEnv*, jclass, jlong, jlong, jint, jobject, jboolean) { return 0; }
static void AM_nativeThemeDump(JNIEnv*, jclass, jlong, jlong, jint, jstring, jstring) { }
static jint AM_nativeThemeGetChangingConfigurations(JNIEnv*, jclass, jlong) { return 0; }
static jint AM_nativeGetParentThemeIdentifier(JNIEnv*, jclass, jlong, jint) { return 0; }

// === Asset stream ===
static void AM_nativeAssetDestroy(JNIEnv*, jclass, jlong) { }
static jint AM_nativeAssetReadChar(JNIEnv*, jclass, jlong) { return -1; }
static jint AM_nativeAssetRead(JNIEnv*, jclass, jlong, jbyteArray, jint, jint) { return -1; }
static jlong AM_nativeAssetSeek(JNIEnv*, jclass, jlong, jlong, jint) { return 0; }
static jlong AM_nativeAssetGetLength(JNIEnv*, jclass, jlong) { return 0; }
static jlong AM_nativeAssetGetRemainingLength(JNIEnv*, jclass, jlong) { return 0; }

// === Overlay ===
static jobject AM_nativeGetOverlayableMap(JNIEnv*, jclass, jlong, jstring) { return nullptr; }
static jstring AM_nativeGetOverlayablesToString(JNIEnv*, jclass, jlong, jstring) { return nullptr; }

// === Global stats ===
static jint AM_getGlobalAssetCount(JNIEnv*, jclass) { return 0; }
static jstring AM_getAssetAllocations(JNIEnv*, jclass) { return nullptr; }
static jint AM_getGlobalAssetManagerCount(JNIEnv*, jclass) { return 0; }

const JNINativeMethod kAssetManagerMethods[] = {
    { "nativeCreate", "()J", reinterpret_cast<void*>(AM_nativeCreate) },
    { "nativeDestroy", "(J)V", reinterpret_cast<void*>(AM_nativeDestroy) },
    { "nativeSetApkAssets", "(J[Landroid/content/res/ApkAssets;Z)V", reinterpret_cast<void*>(AM_nativeSetApkAssets) },
    { "nativeSetConfiguration", "(JIILjava/lang/String;IIIIIIIIIIIIIIII)V", reinterpret_cast<void*>(AM_nativeSetConfiguration) },
    { "nativeGetAssignedPackageIdentifiers", "(JZZ)Landroid/util/SparseArray;", reinterpret_cast<void*>(AM_nativeGetAssignedPackageIdentifiers) },
    { "nativeContainsAllocatedTable", "(J)Z", reinterpret_cast<void*>(AM_nativeContainsAllocatedTable) },
    { "nativeList", "(JLjava/lang/String;)[Ljava/lang/String;", reinterpret_cast<void*>(AM_nativeList) },
    { "nativeOpenAsset", "(JLjava/lang/String;I)J", reinterpret_cast<void*>(AM_nativeOpenAsset) },
    { "nativeOpenAssetFd", "(JLjava/lang/String;[J)Landroid/os/ParcelFileDescriptor;", reinterpret_cast<void*>(AM_nativeOpenAssetFd) },
    { "nativeOpenNonAsset", "(JILjava/lang/String;I)J", reinterpret_cast<void*>(AM_nativeOpenNonAsset) },
    { "nativeOpenNonAssetFd", "(JILjava/lang/String;[J)Landroid/os/ParcelFileDescriptor;", reinterpret_cast<void*>(AM_nativeOpenNonAssetFd) },
    { "nativeOpenXmlAsset", "(JILjava/lang/String;)J", reinterpret_cast<void*>(AM_nativeOpenXmlAsset) },
    { "nativeOpenXmlAssetFd", "(JILjava/io/FileDescriptor;)J", reinterpret_cast<void*>(AM_nativeOpenXmlAssetFd) },
    { "nativeGetResourceValue", "(JISLandroid/util/TypedValue;Z)I", reinterpret_cast<void*>(AM_nativeGetResourceValue) },
    { "nativeGetResourceBagValue", "(JIILandroid/util/TypedValue;)I", reinterpret_cast<void*>(AM_nativeGetResourceBagValue) },
    { "nativeGetStyleAttributes", "(JI)[I", reinterpret_cast<void*>(AM_nativeGetStyleAttributes) },
    { "nativeGetResourceStringArray", "(JI)[Ljava/lang/String;", reinterpret_cast<void*>(AM_nativeGetResourceStringArray) },
    { "nativeGetResourceStringArrayInfo", "(JI)[I", reinterpret_cast<void*>(AM_nativeGetResourceStringArrayInfo) },
    { "nativeGetResourceIntArray", "(JI)[I", reinterpret_cast<void*>(AM_nativeGetResourceIntArray) },
    { "nativeGetResourceArraySize", "(JI)I", reinterpret_cast<void*>(AM_nativeGetResourceArraySize) },
    { "nativeGetResourceArray", "(JI[I)I", reinterpret_cast<void*>(AM_nativeGetResourceArray) },
    { "nativeGetResourceIdentifier", "(JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)I", reinterpret_cast<void*>(AM_nativeGetResourceIdentifier) },
    { "nativeGetResourceName", "(JI)Ljava/lang/String;", reinterpret_cast<void*>(AM_nativeGetResourceName) },
    { "nativeGetResourcePackageName", "(JI)Ljava/lang/String;", reinterpret_cast<void*>(AM_nativeGetResourcePackageName) },
    { "nativeGetResourceTypeName", "(JI)Ljava/lang/String;", reinterpret_cast<void*>(AM_nativeGetResourceTypeName) },
    { "nativeGetResourceEntryName", "(JI)Ljava/lang/String;", reinterpret_cast<void*>(AM_nativeGetResourceEntryName) },
    { "nativeGetLocales", "(JZ)[Ljava/lang/String;", reinterpret_cast<void*>(AM_nativeGetLocales) },
    { "nativeGetSizeConfigurations", "(J)[Landroid/content/res/Configuration;", reinterpret_cast<void*>(AM_nativeGetSizeConfigurations) },
    { "nativeGetSizeAndUiModeConfigurations", "(J)[Landroid/content/res/Configuration;", reinterpret_cast<void*>(AM_nativeGetSizeAndUiModeConfigurations) },
    { "nativeSetResourceResolutionLoggingEnabled", "(JZ)V", reinterpret_cast<void*>(AM_nativeSetResourceResolutionLoggingEnabled) },
    { "nativeGetLastResourceResolution", "(J)Ljava/lang/String;", reinterpret_cast<void*>(AM_nativeGetLastResourceResolution) },
    { "nativeAttributeResolutionStack", "(JJIII)[I", reinterpret_cast<void*>(AM_nativeAttributeResolutionStack) },
    { "nativeApplyStyle", "(JJIIJ[IJJ)V", reinterpret_cast<void*>(AM_nativeApplyStyle) },
    { "nativeResolveAttrs", "(JJII[I[I[I[I)Z", reinterpret_cast<void*>(AM_nativeResolveAttrs) },
    { "nativeRetrieveAttributes", "(JJ[I[I[I)Z", reinterpret_cast<void*>(AM_nativeRetrieveAttributes) },
    { "nativeThemeCreate", "(J)J", reinterpret_cast<void*>(AM_nativeThemeCreate) },
    { "nativeGetThemeFreeFunction", "()J", reinterpret_cast<void*>(AM_nativeGetThemeFreeFunction) },
    { "nativeThemeApplyStyle", "(JJIZ)V", reinterpret_cast<void*>(AM_nativeThemeApplyStyle) },
    { "nativeThemeRebase", "(JJ[I[ZI)V", reinterpret_cast<void*>(AM_nativeThemeRebase) },
    { "nativeThemeCopy", "(JJJJ)V", reinterpret_cast<void*>(AM_nativeThemeCopy) },
    { "nativeThemeGetAttributeValue", "(JJILandroid/util/TypedValue;Z)I", reinterpret_cast<void*>(AM_nativeThemeGetAttributeValue) },
    { "nativeThemeDump", "(JJILjava/lang/String;Ljava/lang/String;)V", reinterpret_cast<void*>(AM_nativeThemeDump) },
    { "nativeThemeGetChangingConfigurations", "(J)I", reinterpret_cast<void*>(AM_nativeThemeGetChangingConfigurations) },
    { "nativeGetParentThemeIdentifier", "(JI)I", reinterpret_cast<void*>(AM_nativeGetParentThemeIdentifier) },
    { "nativeAssetDestroy", "(J)V", reinterpret_cast<void*>(AM_nativeAssetDestroy) },
    { "nativeAssetReadChar", "(J)I", reinterpret_cast<void*>(AM_nativeAssetReadChar) },
    { "nativeAssetRead", "(J[BII)I", reinterpret_cast<void*>(AM_nativeAssetRead) },
    { "nativeAssetSeek", "(JJI)J", reinterpret_cast<void*>(AM_nativeAssetSeek) },
    { "nativeAssetGetLength", "(J)J", reinterpret_cast<void*>(AM_nativeAssetGetLength) },
    { "nativeAssetGetRemainingLength", "(J)J", reinterpret_cast<void*>(AM_nativeAssetGetRemainingLength) },
    { "nativeGetOverlayableMap", "(JLjava/lang/String;)Ljava/util/Map;", reinterpret_cast<void*>(AM_nativeGetOverlayableMap) },
    { "nativeGetOverlayablesToString", "(JLjava/lang/String;)Ljava/lang/String;", reinterpret_cast<void*>(AM_nativeGetOverlayablesToString) },
    { "getGlobalAssetCount", "()I", reinterpret_cast<void*>(AM_getGlobalAssetCount) },
    { "getAssetAllocations", "()Ljava/lang/String;", reinterpret_cast<void*>(AM_getAssetAllocations) },
    { "getGlobalAssetManagerCount", "()I", reinterpret_cast<void*>(AM_getGlobalAssetManagerCount) },
};

}  // namespace

int register_android_content_AssetManager(JNIEnv* env) {
    jclass clazz = env->FindClass("android/content/res/AssetManager");
    if (!clazz) return -1;
    jint rc = env->RegisterNatives(clazz, kAssetManagerMethods,
                                    sizeof(kAssetManagerMethods) / sizeof(kAssetManagerMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
