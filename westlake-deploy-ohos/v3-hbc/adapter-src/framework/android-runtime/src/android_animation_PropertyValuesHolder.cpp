// ============================================================================
// android_animation_PropertyValuesHolder.cpp
//
// JNI binding for android.animation.PropertyValuesHolder.  Direct port from
// AOSP frameworks/base/core/jni/android_animation_PropertyValuesHolder.cpp
// (Apache 2.0 license, AOSP).
//
// PropertyValuesHolder caches reflection MethodIDs to setter methods on a
// target Class (e.g. View.setAlpha).  Used by ObjectAnimator throughout View
// hierarchy setup; if not registered, View setContentView path throws
// UnsatisfiedLinkError when an animatable property is touched.
//
// Self-contained — no dependencies on AOSP libandroid_runtime.so.
// ============================================================================

#include "AndroidRuntime.h"

#include <jni.h>
#include <cstring>

namespace android {
namespace {

jlong PVH_getIntMethod(JNIEnv* env, jclass /*pvhClass*/,
                       jclass targetClass, jstring methodName) {
    const char* name = env->GetStringUTFChars(methodName, nullptr);
    jmethodID mid = env->GetMethodID(targetClass, name, "(I)V");
    env->ReleaseStringUTFChars(methodName, name);
    if (env->ExceptionCheck()) env->ExceptionClear();
    return reinterpret_cast<jlong>(mid);
}

jlong PVH_getFloatMethod(JNIEnv* env, jclass /*pvhClass*/,
                         jclass targetClass, jstring methodName) {
    const char* name = env->GetStringUTFChars(methodName, nullptr);
    jmethodID mid = env->GetMethodID(targetClass, name, "(F)V");
    env->ReleaseStringUTFChars(methodName, name);
    if (env->ExceptionCheck()) env->ExceptionClear();
    return reinterpret_cast<jlong>(mid);
}

jlong PVH_getMultiparameterMethod(JNIEnv* env, jclass targetClass,
                                  jstring methodName, jint parameterCount,
                                  char parameterType) {
    const char* name = env->GetStringUTFChars(methodName, nullptr);
    char* signature = new char[parameterCount + 4];
    signature[0] = '(';
    std::memset(&signature[1], parameterType, parameterCount);
    std::strcpy(&signature[parameterCount + 1], ")V");
    jmethodID mid = env->GetMethodID(targetClass, name, signature);
    delete[] signature;
    env->ReleaseStringUTFChars(methodName, name);
    if (env->ExceptionCheck()) env->ExceptionClear();
    return reinterpret_cast<jlong>(mid);
}

jlong PVH_getMultipleFloatMethod(JNIEnv* env, jclass /*pvhClass*/,
                                 jclass targetClass, jstring methodName,
                                 jint parameterCount) {
    return PVH_getMultiparameterMethod(env, targetClass, methodName,
                                        parameterCount, 'F');
}

jlong PVH_getMultipleIntMethod(JNIEnv* env, jclass /*pvhClass*/,
                               jclass targetClass, jstring methodName,
                               jint parameterCount) {
    return PVH_getMultiparameterMethod(env, targetClass, methodName,
                                        parameterCount, 'I');
}

void PVH_callIntMethod(JNIEnv* env, jclass /*pvh*/, jobject target,
                       jlong methodID, jint arg) {
    env->CallVoidMethod(target, reinterpret_cast<jmethodID>(methodID), arg);
}

void PVH_callFloatMethod(JNIEnv* env, jclass /*pvh*/, jobject target,
                         jlong methodID, jfloat arg) {
    env->CallVoidMethod(target, reinterpret_cast<jmethodID>(methodID), arg);
}

void PVH_callTwoFloatMethod(JNIEnv* env, jclass /*pvh*/, jobject target,
                            jlong methodID, jfloat a1, jfloat a2) {
    env->CallVoidMethod(target, reinterpret_cast<jmethodID>(methodID), a1, a2);
}

void PVH_callFourFloatMethod(JNIEnv* env, jclass /*pvh*/, jobject target,
                             jlong methodID, jfloat a1, jfloat a2,
                             jfloat a3, jfloat a4) {
    env->CallVoidMethod(target, reinterpret_cast<jmethodID>(methodID),
                        a1, a2, a3, a4);
}

void PVH_callMultipleFloatMethod(JNIEnv* env, jclass /*pvh*/, jobject target,
                                 jlong methodID, jfloatArray arg) {
    jsize parameterCount = env->GetArrayLength(arg);
    jfloat* floatValues = env->GetFloatArrayElements(arg, nullptr);
    jvalue* values = new jvalue[parameterCount];
    for (int i = 0; i < parameterCount; i++) {
        values[i].f = floatValues[i];
    }
    env->CallVoidMethodA(target, reinterpret_cast<jmethodID>(methodID), values);
    delete[] values;
    env->ReleaseFloatArrayElements(arg, floatValues, JNI_ABORT);
}

void PVH_callTwoIntMethod(JNIEnv* env, jclass /*pvh*/, jobject target,
                          jlong methodID, jint a1, jint a2) {
    env->CallVoidMethod(target, reinterpret_cast<jmethodID>(methodID), a1, a2);
}

void PVH_callFourIntMethod(JNIEnv* env, jclass /*pvh*/, jobject target,
                           jlong methodID, jint a1, jint a2, jint a3, jint a4) {
    env->CallVoidMethod(target, reinterpret_cast<jmethodID>(methodID),
                        a1, a2, a3, a4);
}

void PVH_callMultipleIntMethod(JNIEnv* env, jclass /*pvh*/, jobject target,
                               jlong methodID, jintArray arg) {
    jsize parameterCount = env->GetArrayLength(arg);
    jint* intValues = env->GetIntArrayElements(arg, nullptr);
    jvalue* values = new jvalue[parameterCount];
    for (int i = 0; i < parameterCount; i++) {
        values[i].i = intValues[i];
    }
    env->CallVoidMethodA(target, reinterpret_cast<jmethodID>(methodID), values);
    delete[] values;
    env->ReleaseIntArrayElements(arg, intValues, JNI_ABORT);
}

const JNINativeMethod kMethods[] = {
    { "nGetIntMethod",          "(Ljava/lang/Class;Ljava/lang/String;)J",
      reinterpret_cast<void*>(PVH_getIntMethod) },
    { "nGetFloatMethod",        "(Ljava/lang/Class;Ljava/lang/String;)J",
      reinterpret_cast<void*>(PVH_getFloatMethod) },
    { "nGetMultipleFloatMethod","(Ljava/lang/Class;Ljava/lang/String;I)J",
      reinterpret_cast<void*>(PVH_getMultipleFloatMethod) },
    { "nGetMultipleIntMethod",  "(Ljava/lang/Class;Ljava/lang/String;I)J",
      reinterpret_cast<void*>(PVH_getMultipleIntMethod) },
    { "nCallIntMethod",         "(Ljava/lang/Object;JI)V",
      reinterpret_cast<void*>(PVH_callIntMethod) },
    { "nCallFloatMethod",       "(Ljava/lang/Object;JF)V",
      reinterpret_cast<void*>(PVH_callFloatMethod) },
    { "nCallTwoFloatMethod",    "(Ljava/lang/Object;JFF)V",
      reinterpret_cast<void*>(PVH_callTwoFloatMethod) },
    { "nCallFourFloatMethod",   "(Ljava/lang/Object;JFFFF)V",
      reinterpret_cast<void*>(PVH_callFourFloatMethod) },
    { "nCallMultipleFloatMethod","(Ljava/lang/Object;J[F)V",
      reinterpret_cast<void*>(PVH_callMultipleFloatMethod) },
    { "nCallTwoIntMethod",      "(Ljava/lang/Object;JII)V",
      reinterpret_cast<void*>(PVH_callTwoIntMethod) },
    { "nCallFourIntMethod",     "(Ljava/lang/Object;JIIII)V",
      reinterpret_cast<void*>(PVH_callFourIntMethod) },
    { "nCallMultipleIntMethod", "(Ljava/lang/Object;J[I)V",
      reinterpret_cast<void*>(PVH_callMultipleIntMethod) },
};

}  // namespace

int register_android_animation_PropertyValuesHolder(JNIEnv* env) {
    jclass clazz = env->FindClass("android/animation/PropertyValuesHolder");
    if (clazz == nullptr) return -1;
    jint rc = env->RegisterNatives(clazz, kMethods,
                                    sizeof(kMethods) / sizeof(kMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
