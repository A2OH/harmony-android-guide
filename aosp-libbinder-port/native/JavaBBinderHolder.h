// SPDX-License-Identifier: Apache-2.0
//
// Westlake M3++ — JavaBBinderHolder.h
//
// Declares JavaBBinder, JavaBBinderHolder, and the conversion helpers used
// by binder_jni_stub.cc to bridge Java `android.os.Binder` <-> C++ BBinder.
// Implementation lives in JavaBBinderHolder.cpp.  See that file for the
// design rationale.

#pragma once

#include <jni.h>

#include <mutex>

#include <binder/Binder.h>
#include <binder/IBinder.h>
#include <utils/RefBase.h>
#include <utils/String16.h>

extern "C" {
// Subclass-ID byte — pointer-compared by BBinder::checkSubclass to identify
// "this BBinder is a JavaBBinder".  Defined in JavaBBinderHolder.cpp.
extern const char gJavaBBinderSubclassID;
}

namespace android {

class JavaBBinder;

// Wraps a JavaBBinder weak ref so multiple addService/getService cycles
// from the same Java Binder share a single C++ BBinder identity.
class JavaBBinderHolder {
public:
    JavaBBinderHolder();
    ~JavaBBinderHolder();

    // Promote/create the JavaBBinder.  `env` and `javaBinder` are used only
    // on the first call (when the weak ref needs upgrading).
    sp<JavaBBinder> get(JNIEnv* env, jobject javaBinder);

    // Return existing JavaBBinder if alive, null otherwise.  Does not
    // re-create.  Used in code paths that mustn't reincarnate the binder.
    sp<JavaBBinder> getExisting();

private:
    std::mutex mLock;
    wp<JavaBBinder> mBinder;
};

// If `val` is a same-process JavaBBinder, return the original Java Binder
// (a global ref — do NOT DeleteGlobalRef on it; the JavaBBinder owns it).
// Returns nullptr if `val` is null, or remote (BpBinder), or a non-Java
// local BBinder (e.g. a raw `sp<BBinder>::make()` token).
jobject javaObjectForLocalIBinder(JNIEnv* env, const sp<IBinder>& val);

// Extract the sp<JavaBBinder> backing a Java android.os.Binder object.
// Returns nullptr if `javaBinder` has no JavaBBinderHolder set (e.g. it's a
// NativeBinderProxy or its `mObject` long field is 0).  Caller must pass the
// jfieldID of `android.os.Binder.mObject` (long).
sp<IBinder> ibinderForJavaBinder(JNIEnv* env, jobject javaBinder,
                                  jfieldID mObjectField);

}  // namespace android

// C-callable factory + finalizer.  Used by Binder.<init>'s JNI native to
// avoid re-declaring JavaBBinderHolder type in binder_jni_stub.cc.
extern "C" {
JNIEXPORT jlong JNICALL westlake_create_javabinder_holder();
JNIEXPORT void JNICALL westlake_destroy_javabinder_holder(jlong handle);
}
