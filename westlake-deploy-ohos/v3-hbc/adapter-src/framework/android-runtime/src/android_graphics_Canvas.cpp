// ============================================================================
// android_graphics_Canvas.cpp
//
// Minimal Canvas JNI registration for handleBindApplication path.
// AOSP frameworks/base/graphics/java/android/graphics/Canvas.java:6786 path
// calls Canvas.setCompatibilityVersion(apiLevel) which calls
// nSetCompatibilityVersion(apiLevel). Without this JNI registered,
// UnsatisfiedLinkError → ExceptionInInitializerError → handleBindApplication
// crash.
//
// Per overall_design §15 + feedback_java_full_cpp_stub_only:
// JNI native impl can be stub (no-op) — this is C++ IPC layer 允许的 stub。
// Java Canvas class itself remains AOSP-original (not stubbed).
//
// Methods registered: just enough for handleBindApplication boot path.
// Adding more (nDrawColor / nClipRect etc.) when actual rendering is needed
// — currently HelloWorld TextView display goes through hwui/Skia chain
// at much later stage; minimal canvas natives suffice for boot.
// ============================================================================

#include <jni.h>

namespace android {

namespace {

void  nSetCompatibilityVersion(JNIEnv*, jclass, jint /*apiLevel*/) {
    // No-op: HelloWorld doesn't depend on Canvas SDK-version compat behavior.
}

void  nFreeCaches(JNIEnv*, jclass) {}
void  nFreeTextLayoutCaches(JNIEnv*, jclass) {}
// G2.14k (2026-05-01): MUST return a real function pointer, NOT 0. See
// android_view_InputChannel.cpp::IC_noop_free for the rationale (NativeAllocationRegistry
// dereferences freeFunction directly; 0 → SIGSEGV via art_quick_generic_jni_trampoline
// when ReferenceQueueD daemon processes the Cleaner).
static void Canvas_noop_free(void* /*p*/) {}
jlong nGetNativeFinalizer(JNIEnv*, jclass) {
    return reinterpret_cast<jlong>(&Canvas_noop_free);
}

const JNINativeMethod kMethods[] = {
    {"nSetCompatibilityVersion", "(I)V", (void*)nSetCompatibilityVersion},
    {"nFreeCaches",              "()V",  (void*)nFreeCaches},
    {"nFreeTextLayoutCaches",    "()V",  (void*)nFreeTextLayoutCaches},
    {"nGetNativeFinalizer",      "()J",  (void*)nGetNativeFinalizer},
};

}  // namespace

int register_android_graphics_Canvas(JNIEnv* env) {
    jclass clazz = env->FindClass("android/graphics/Canvas");
    if (!clazz) return -1;
    jint rc = env->RegisterNatives(clazz, kMethods,
                                    sizeof(kMethods) / sizeof(kMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
