// ============================================================================
// android_app_Activity.cpp
//
// JNI binding for android.app.Activity. Mirrors AOSP
// frameworks/base/core/jni/android_app_Activity.cpp.
//
// Java side:
//   private static native String getDlWarning();   // ()Ljava/lang/String;
//
// AOSP impl calls bionic linker's android_dlwarning(...) to retrieve the
// dynamic-linker's accumulated warning string (e.g. "library X uses non-public
// symbol from Y").  OH ships musl, not bionic, and has no such API.  The
// adapter returns null — meaning "no warnings" — which is also what AOSP
// returns on a clean process.
//
// AOSP caller (Activity.performStart at line 8645):
//   String dlwarning = getDlWarning();
//   if (dlwarning != null) { Toast.makeText(...).show(); }
// So null is the correct "no problems detected" return.
// ============================================================================

#include "AndroidRuntime.h"

#include <jni.h>

namespace android {
namespace {

jstring JNICALL
Activity_getDlWarning(JNIEnv* /*env*/, jclass /*clazz*/) {
    // OH's musl has no android_dlwarning equivalent.  Return null = no warnings.
    return nullptr;
}

const JNINativeMethod kActivityMethods[] = {
    { "getDlWarning", "()Ljava/lang/String;",
      reinterpret_cast<void*>(Activity_getDlWarning) },
};

}  // namespace

int register_android_app_Activity(JNIEnv* env) {
    jclass clazz = env->FindClass("android/app/Activity");
    if (clazz == nullptr) {
        return -1;
    }
    jint rc = env->RegisterNatives(clazz, kActivityMethods,
                                    sizeof(kActivityMethods)
                                        / sizeof(kActivityMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
