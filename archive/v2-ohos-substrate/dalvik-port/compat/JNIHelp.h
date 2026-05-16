/*
 * JNI helper functions — minimal stub for standalone Dalvik.
 */
#ifndef JNI_HELP_H
#define JNI_HELP_H

#include "jni.h"
#include <stdio.h>
#include <stdlib.h>

static inline int jniThrowException(JNIEnv* env, const char* className, const char* msg) {
    jclass cls = env->FindClass(className);
    if (cls) {
        env->ThrowNew(cls, msg);
        env->DeleteLocalRef(cls);
    }
    return -1;
}

static inline int jniThrowNullPointerException(JNIEnv* env, const char* msg) {
    return jniThrowException(env, "java/lang/NullPointerException", msg);
}

static inline int jniThrowRuntimeException(JNIEnv* env, const char* msg) {
    return jniThrowException(env, "java/lang/RuntimeException", msg);
}

static inline int jniThrowIOException(JNIEnv* env, int errnum) {
    char buf[128];
    snprintf(buf, sizeof(buf), "I/O error: %d", errnum);
    return jniThrowException(env, "java/io/IOException", buf);
}

static inline int jniRegisterNativeMethods(JNIEnv* env, const char* className,
                                            const JNINativeMethod* methods, int numMethods) {
    jclass cls = env->FindClass(className);
    if (!cls) return -1;
    int rc = env->RegisterNatives(cls, methods, numMethods);
    env->DeleteLocalRef(cls);
    return rc;
}

#endif
