// ============================================================================
// android_os_GraphicsEnvironment.cpp
//
// JNI registration for android.os.GraphicsEnvironment. AOSP's
// libandroid_servers.so registers these natives in the system_server, but on
// OH there is no equivalent and the bindings need to live in
// liboh_android_runtime.so so the child process (which is not system_server)
// resolves them.
//
// All 11 natives implemented as no-op safe defaults — HelloWorld bring-up
// path doesn't actually need any of the GPU driver / ANGLE / vulkan
// configuration, only that the JNI bindings resolve so handleBindApplication
// past line 6680 (setupGraphicsSupport → isDebuggable → setup → ...) can run.
// ============================================================================

#include <jni.h>

namespace android {
namespace {

// isDebuggable() — return false; non-debug build assumption keeps
// setupGraphicsSupport on the production path that doesn't try to load
// debug GLES layers.
jboolean GE_isDebuggable(JNIEnv*, jclass) { return JNI_FALSE; }

// setLayerPaths(ClassLoader, String) — no-op
void GE_setLayerPaths(JNIEnv*, jclass, jobject, jstring) {}
// setDebugLayers(String) — no-op
void GE_setDebugLayers(JNIEnv*, jclass, jstring) {}
// setDebugLayersGLES(String) — no-op
void GE_setDebugLayersGLES(JNIEnv*, jclass, jstring) {}
// setDriverPathAndSphalLibraries(String, String) — no-op
void GE_setDriverPathAndSphalLibraries(JNIEnv*, jclass, jstring, jstring) {}
// setGpuStats(String, String, long, long, String, int) — no-op
void GE_setGpuStats(JNIEnv*, jclass, jstring, jstring, jlong, jlong, jstring, jint) {}
// setAngleInfo(String, String, String, String[]) — no-op
void GE_setAngleInfo(JNIEnv*, jclass, jstring, jstring, jstring, jobjectArray) {}
// getShouldUseAngle(String) — return false
jboolean GE_getShouldUseAngle(JNIEnv*, jclass, jstring) { return JNI_FALSE; }
// setInjectLayersPrSetDumpable() — return true (caller treats failure as ok)
jboolean GE_setInjectLayersPrSetDumpable(JNIEnv*, jclass) { return JNI_TRUE; }
// nativeToggleAngleAsSystemDriver(boolean) — no-op
void GE_nativeToggleAngleAsSystemDriver(JNIEnv*, jclass, jboolean) {}
// hintActivityLaunch() — no-op
void GE_hintActivityLaunch(JNIEnv*, jclass) {}

const JNINativeMethod kMethods[] = {
    {"isDebuggable",                  "()Z",                                  (void*)GE_isDebuggable},
    {"setLayerPaths",                 "(Ljava/lang/ClassLoader;Ljava/lang/String;)V", (void*)GE_setLayerPaths},
    {"setDebugLayers",                "(Ljava/lang/String;)V",               (void*)GE_setDebugLayers},
    {"setDebugLayersGLES",            "(Ljava/lang/String;)V",               (void*)GE_setDebugLayersGLES},
    {"setDriverPathAndSphalLibraries","(Ljava/lang/String;Ljava/lang/String;)V", (void*)GE_setDriverPathAndSphalLibraries},
    {"setGpuStats",                   "(Ljava/lang/String;Ljava/lang/String;JJLjava/lang/String;I)V", (void*)GE_setGpuStats},
    {"setAngleInfo",                  "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)V", (void*)GE_setAngleInfo},
    {"getShouldUseAngle",             "(Ljava/lang/String;)Z",               (void*)GE_getShouldUseAngle},
    {"setInjectLayersPrSetDumpable",  "()Z",                                  (void*)GE_setInjectLayersPrSetDumpable},
    {"nativeToggleAngleAsSystemDriver","(Z)V",                                (void*)GE_nativeToggleAngleAsSystemDriver},
    {"hintActivityLaunch",            "()V",                                  (void*)GE_hintActivityLaunch},
};

}  // namespace

int register_android_os_GraphicsEnvironment(JNIEnv* env) {
    jclass clazz = env->FindClass("android/os/GraphicsEnvironment");
    if (!clazz) return -1;
    jint rc = env->RegisterNatives(clazz, kMethods,
                                    sizeof(kMethods) / sizeof(kMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
