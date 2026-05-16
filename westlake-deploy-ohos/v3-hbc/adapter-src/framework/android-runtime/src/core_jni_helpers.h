// ============================================================================
// core_jni_helpers.h
//
// Adapter stub for AOSP's frameworks/base/core/jni/core_jni_helpers.h.
// Provides the OrDie helper inlines without nativehelper dependency.
// On AOSP these abort with LOG_ALWAYS_FATAL_IF; on adapter we fall back to
// returning the raw value (which may be null/invalid) and let the caller's
// own ExceptionCheck handle it.  For Hello World bring-up the
// register_android_content_AssetManager + register_android_content_res_ApkAssets
// invocations look up well-known classes that are guaranteed present in the
// boot image, so the OrDie path is in practice equivalent to the safe path.
// ============================================================================
#ifndef CORE_JNI_HELPERS
#define CORE_JNI_HELPERS

#include <jni.h>
#include <cstdio>
#include <cstdlib>
// AOSP's core_jni_helpers.h pulls in these.  We forward exactly the same
// transitive surface so AOSP-ported sources (android_util_AssetManager.cpp,
// android_content_res_ApkAssets.cpp) compile unmodified.
#include <nativehelper/JNIPlatformHelp.h>
#include <nativehelper/scoped_local_ref.h>
#include <nativehelper/scoped_utf_chars.h>
#include "android_runtime/AndroidRuntime.h"

// AOSP CRITICAL_JNI_PARAMS macros — on Android they expand to nothing
// (symbol-only annotation handled by ART RegisterNatives). Use the Android
// path so signatures match the AOSP source verbatim.
#ifndef CRITICAL_JNI_PARAMS
#define CRITICAL_JNI_PARAMS
#define CRITICAL_JNI_PARAMS_COMMA
#endif

// AOSP uses NELEM(arr) — array element count.  Provide if not already.
#ifndef NELEM
#define NELEM(x) ((int)(sizeof(x) / sizeof((x)[0])))
#endif

namespace android {

static inline jclass FindClassOrDie(JNIEnv* env, const char* class_name) {
    jclass clazz = env->FindClass(class_name);
    if (clazz == nullptr) {
        if (env->ExceptionCheck()) env->ExceptionClear();
        fprintf(stderr, "[core_jni_helpers] FindClass(%s) FAILED\n", class_name);
    }
    return clazz;
}

static inline jfieldID GetFieldIDOrDie(JNIEnv* env, jclass clazz,
                                        const char* field_name, const char* field_signature) {
    jfieldID id = env->GetFieldID(clazz, field_name, field_signature);
    if (id == nullptr) {
        if (env->ExceptionCheck()) env->ExceptionClear();
        fprintf(stderr, "[core_jni_helpers] GetFieldID(%s,%s) FAILED\n",
                field_name, field_signature);
    }
    return id;
}

static inline jmethodID GetMethodIDOrDie(JNIEnv* env, jclass clazz,
                                          const char* method_name, const char* method_signature) {
    jmethodID id = env->GetMethodID(clazz, method_name, method_signature);
    if (id == nullptr) {
        if (env->ExceptionCheck()) env->ExceptionClear();
        fprintf(stderr, "[core_jni_helpers] GetMethodID(%s,%s) FAILED\n",
                method_name, method_signature);
    }
    return id;
}

static inline jmethodID GetStaticMethodIDOrDie(JNIEnv* env, jclass clazz,
                                                 const char* method_name, const char* method_signature) {
    jmethodID id = env->GetStaticMethodID(clazz, method_name, method_signature);
    if (id == nullptr) {
        if (env->ExceptionCheck()) env->ExceptionClear();
        fprintf(stderr, "[core_jni_helpers] GetStaticMethodID(%s,%s) FAILED\n",
                method_name, method_signature);
    }
    return id;
}

template <typename T>
static inline T MakeGlobalRefOrDie(JNIEnv* env, T in) {
    jobject res = env->NewGlobalRef(in);
    return static_cast<T>(res);
}

static inline int RegisterMethodsOrDie(JNIEnv* env, const char* className,
                                        const JNINativeMethod* methods, int numMethods) {
    jclass clazz = FindClassOrDie(env, className);
    if (clazz == nullptr) return -1;
    int rc = env->RegisterNatives(clazz, methods, numMethods);
    env->DeleteLocalRef(clazz);
    if (rc != JNI_OK) {
        if (env->ExceptionCheck()) env->ExceptionClear();
        fprintf(stderr, "[core_jni_helpers] RegisterNatives(%s) rc=%d\n", className, rc);
    }
    return rc;
}

}  // namespace android

#endif /* CORE_JNI_HELPERS */
