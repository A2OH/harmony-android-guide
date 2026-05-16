// ============================================================================
// com_android_internal_os_ClassLoaderFactory.cpp
//
// JNI registration for ClassLoaderFactory.createClassloaderNamespace.
//
// AOSP impl creates a libnativeloader "namespace" — a sandbox for native
// library loading.  OH manages namespaces differently (via /etc/ld-musl-*.ini
// + dlopen-ext flags), so the per-classloader namespace concept doesn't
// translate.  The Java-side caller (ApplicationLoaders.getClassLoader) only
// uses the returned String to detect errors — null means "no error, native
// libs are loadable via the default namespace".
//
// Critical for HelloWorld: ContextImpl.createAppContext triggers
// LoadedApk.getClassLoader → ApplicationLoaders.getClassLoader →
// ClassLoaderFactory.createClassLoader → this native.  Without registration
// the entire handleBindApplication chain throws UnsatisfiedLinkError.
//
// AOSP method table is preserved exactly so the JNI signature matches.
// ============================================================================

#include "AndroidRuntime.h"

#include <jni.h>

namespace android {

namespace {

jstring CLF_createClassloaderNamespace(JNIEnv* env, jclass /*clazz*/,
        jobject /*classLoader*/, jint /*targetSdkVersion*/,
        jstring /*librarySearchPath*/, jstring /*libraryPermittedPath*/,
        jboolean /*isShared*/, jstring /*dexPath*/, jstring /*sonameList*/) {
    // Returning null tells the Java caller: namespace creation succeeded with
    // no error.  ApplicationLoaders proceeds to construct the PathClassLoader
    // normally, and dlopen falls back to default OH namespace search.
    return nullptr;
}

const JNINativeMethod kMethods[] = {
    { "createClassloaderNamespace",
      "(Ljava/lang/ClassLoader;ILjava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
      reinterpret_cast<void*>(CLF_createClassloaderNamespace) },
};

}  // namespace

int register_com_android_internal_os_ClassLoaderFactory(JNIEnv* env) {
    jclass clazz = env->FindClass("com/android/internal/os/ClassLoaderFactory");
    if (!clazz) return -1;
    jint rc = env->RegisterNatives(clazz, kMethods,
                                    sizeof(kMethods) / sizeof(kMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
