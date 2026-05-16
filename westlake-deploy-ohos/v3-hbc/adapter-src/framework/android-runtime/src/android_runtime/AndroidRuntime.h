// AOSP-compat header at "android_runtime/AndroidRuntime.h" path.
// Provides minimum surface AOSP register_* sources need:
//   class android::AndroidRuntime { static JNIEnv* getJNIEnv(); ... }
// Real impl is in our liboh_android_runtime/src/AndroidRuntime.cpp.
#pragma once
#include <jni.h>

namespace android {

class AndroidRuntime {
public:
    static int startReg(JNIEnv* env);
    static JNIEnv* getJNIEnv();
    static void setJavaVM(JavaVM* vm);
};

}  // namespace android
