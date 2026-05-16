#include <jni.h>
#include <stdio.h>
#include <string.h>

/* Minimal stub: JNI_OnLoad does nothing. 
 * Native methods use automatic JNI naming (Java_pkg_Class_method).
 * Only implement methods that Dalvik actually calls during boot. */

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    fprintf(stderr, "[javacore-stub] JNI_OnLoad called\n");
    return JNI_VERSION_1_6;
}
