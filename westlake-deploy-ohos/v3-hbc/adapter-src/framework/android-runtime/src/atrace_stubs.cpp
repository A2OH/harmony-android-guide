// ============================================================================
// atrace_stubs.cpp — atrace + AFileDescriptor NDK stubs
//
// Provides AOSP NDK helpers libnativehelper would normally export but OH does
// not. Minimum surface for AOSP-ported android_util_AssetManager.cpp +
// android_content_res_ApkAssets.cpp:
//   atrace_get_enabled_tags / atrace_begin_body / atrace_end_body  → no-op
//   AFileDescriptor_create / AFileDescriptor_getFd / AFileDescriptor_setFd
//     → minimal real impl via JNI reflection on java.io.FileDescriptor.
//
// 2026-05-08 G2.14ad: ANativeWindow_* 6 NDK bridges previously hosted here
// (a 2026-05-02 G2.14r expediency) have been moved to their proper home in
// framework/android-runtime/src/android_graphics_compat_shim.cpp, where they
// live alongside BBQ_nativeGetSurface (the upstream that sets the
// mNativeObject they read).
// ============================================================================
#include <stdint.h>
#include <jni.h>

extern "C" {

// --- atrace ---
uint64_t atrace_get_enabled_tags() { return 0; }
void atrace_begin_body(const char* /*name*/) {}
void atrace_end_body(void) {}

// --- AFileDescriptor (libnativehelper NDK API) ---
// Reflectively reads/writes java.io.FileDescriptor's "descriptor" int field.
// AOSP libnativehelper has accelerated path; we can be straightforward.

static jclass     g_fd_class    = nullptr;
static jfieldID   g_fd_field    = nullptr;
static jmethodID  g_fd_ctor     = nullptr;

static void afd_lazy_init(JNIEnv* env) {
    if (g_fd_class != nullptr) return;
    jclass local = env->FindClass("java/io/FileDescriptor");
    if (!local) return;
    g_fd_class = (jclass)env->NewGlobalRef(local);
    env->DeleteLocalRef(local);
    g_fd_field = env->GetFieldID(g_fd_class, "descriptor", "I");
    g_fd_ctor  = env->GetMethodID(g_fd_class, "<init>", "()V");
}

jobject AFileDescriptor_create(JNIEnv* env) {
    afd_lazy_init(env);
    if (!g_fd_class || !g_fd_ctor) return nullptr;
    return env->NewObject(g_fd_class, g_fd_ctor);
}

int AFileDescriptor_getFd(JNIEnv* env, jobject fd) {
    afd_lazy_init(env);
    if (fd == nullptr || g_fd_field == nullptr) return -1;
    return env->GetIntField(fd, g_fd_field);
}

void AFileDescriptor_setFd(JNIEnv* env, jobject fd, int value) {
    afd_lazy_init(env);
    if (fd == nullptr || g_fd_field == nullptr) return;
    env->SetIntField(fd, g_fd_field, value);
}

}  // extern "C"
