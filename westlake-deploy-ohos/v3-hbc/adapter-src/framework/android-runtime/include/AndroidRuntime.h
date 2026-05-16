// ============================================================================
// AndroidRuntime.h
//
// Minimal public header for OH-Adapter's replacement of libandroid_runtime.so.
//
// Target symbol signature (mangled): _ZN7android14AndroidRuntime8startRegEP7_JNIEnv
//   = int android::AndroidRuntime::startReg(JNIEnv*)
//
// This is what appspawn-x's `registerNativeMethods()` looks up via dlsym to
// register all framework JNI bindings. Progressive replacement: Stage 1 only
// wires the JNI methods Hello World actually exercises; further stages add
// more register_* modules until the full AOSP list is covered.
// ============================================================================

#pragma once
#include <jni.h>

namespace android {

class AndroidRuntime {
public:
    // Returns 0 on success, -1 on failure.
    static int startReg(JNIEnv* env);

    // AOSP-compatible signature so ApkAssets.cpp can call it.  Returns the
    // JNIEnv attached to the calling thread; if not attached, attaches it
    // to the cached JavaVM (set by setJavaVM during startReg).  Used by
    // ApkAssets's IncFs change-callback to dispatch back into Java.
    static JNIEnv* getJNIEnv();

    // Internal: stash the JavaVM at startReg entry so getJNIEnv can find it.
    static void setJavaVM(JavaVM* vm);
};

// Forward declarations of register_* functions (one per AOSP JNI module).
// Each is defined in its own .cpp and added to the dispatch table inside
// AndroidRuntime::startReg when Hello World or later stages need it.
int register_android_util_Log(JNIEnv* env);
int register_android_util_EventLog(JNIEnv* env);
int register_android_app_Activity(JNIEnv* env);
int register_android_view_SurfaceSession(JNIEnv* env);
int register_android_view_DisplayEventReceiver(JNIEnv* env);
int register_android_view_InputChannel(JNIEnv* env);
int register_android_view_InputEventReceiver(JNIEnv* env);
int register_android_os_SystemProperties(JNIEnv* env);
int register_android_os_Trace(JNIEnv* env);
int register_android_os_Process(JNIEnv* env);
int register_android_os_SystemClock(JNIEnv* env);
int register_android_os_Binder(JNIEnv* env);
int register_android_view_SurfaceControl(JNIEnv* env);
int register_android_content_AssetManager(JNIEnv* env);
int register_android_os_MessageQueue(JNIEnv* env);
int register_android_content_res_ApkAssets(JNIEnv* env);
int register_android_content_StringBlock(JNIEnv* env);
int register_android_content_XmlBlock(JNIEnv* env);
int register_com_android_internal_os_ClassLoaderFactory(JNIEnv* env);
int register_com_android_internal_util_VirtualRefBasePtr(JNIEnv* env);

}  // namespace android
