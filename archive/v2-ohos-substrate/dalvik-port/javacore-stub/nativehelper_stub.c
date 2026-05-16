/**
 * libnativehelper.so stub — Dalvik expects this but we don't need it.
 * Just provides JNI_OnLoad that returns success.
 */
#include <jni.h>

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    return JNI_VERSION_1_6;
}
