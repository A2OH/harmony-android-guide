/*
 * Minimal JniConstants stub for standalone Dalvik.
 * KitKat's Init.cpp calls JniConstants::init(env) to cache common class refs.
 * We provide a no-op implementation.
 */
#ifndef _JNI_CONSTANTS_H
#define _JNI_CONSTANTS_H

#include "jni.h"

struct JniConstants {
    static void init(JNIEnv* env) {
        (void)env;
        /* In a full build, this caches jclass refs for java.lang.String,
         * java.io.FileDescriptor, etc. from libcore.
         * For standalone operation, these are resolved lazily or not at all. */
    }
};

#endif /* _JNI_CONSTANTS_H */
