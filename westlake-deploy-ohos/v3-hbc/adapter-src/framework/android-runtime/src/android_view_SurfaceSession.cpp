// ============================================================================
// android_view_SurfaceSession.cpp
//
// JNI binding for android.view.SurfaceSession. Mirrors AOSP
// frameworks/base/core/jni/android_view_SurfaceSession.cpp, but adapted to OH.
//
// Java side (frameworks/base/core/java/android/view/SurfaceSession.java):
//   private static native long nativeCreate();         // ()J
//   private static native void nativeDestroy(long ptr); // (J)V
//
// AOSP impl creates a new SurfaceComposerClient (ref-counted handle to
// SurfaceFlinger).  OH has no SurfaceFlinger — the equivalent on OH is OH
// RenderService driven via RSSurfaceNode / RSUIDirector.  But for App-side
// usage of SurfaceSession the ONLY observable behavior is:
//   1) nativeCreate must return a non-zero token (ViewRootImpl reads
//      mNativeClient back into native code via SurfaceControl.Builder.setParent)
//   2) nativeDestroy must accept that token without crashing
// Since AOSP SurfaceControl.Builder usage on our adapter goes through
// android_view_SurfaceControl.cpp (which already has its own OhSurfaceControl
// state and never dereferences the SurfaceSession token as a real
// SurfaceComposerClient), we can return a small heap-allocated state token.
//
// Pattern matches the existing OhSurfaceControl / OhTransaction structs in
// android_view_SurfaceControl.cpp — magic-tagged struct + as_X() validator.
//
// Future P2 (when OH SurfaceSession is real):
//   - Hold an `OHOS::sptr<OHOS::Rosen::RSUIDirector>` to coordinate window
//     transactions through OH's render service.
// ============================================================================

#include "AndroidRuntime.h"

#include <jni.h>
#include <atomic>
#include <cstdint>
#include <cstdio>

namespace android {
namespace {

// ----------------------------------------------------------------------------
// OhSurfaceSession — minimal struct backing Java SurfaceSession.mNativeClient.
// ----------------------------------------------------------------------------
struct OhSurfaceSession {
    int32_t magic;     // 'OHSS' = 0x4F485353 — sanity-check tag
    int64_t id;        // unique id, useful for debug logs
    // Future: OHOS::sptr<OHOS::Rosen::RSUIDirector> rsDirector;
};

constexpr int32_t kOhSurfaceSessionMagic = 0x4F485353;
std::atomic<int64_t> g_sessionId{1};

OhSurfaceSession* alloc_session() {
    auto* s = new OhSurfaceSession();
    s->magic = kOhSurfaceSessionMagic;
    s->id = g_sessionId.fetch_add(1);
    return s;
}

OhSurfaceSession* as_session(jlong p) {
    auto* s = reinterpret_cast<OhSurfaceSession*>(p);
    if (!s || s->magic != kOhSurfaceSessionMagic) return nullptr;
    return s;
}

// ----------------------------------------------------------------------------
// JNI methods.
// ----------------------------------------------------------------------------

jlong JNICALL
SS_nativeCreate(JNIEnv* /*env*/, jclass /*clazz*/) {
    OhSurfaceSession* s = alloc_session();
    return reinterpret_cast<jlong>(s);
}

void JNICALL
SS_nativeDestroy(JNIEnv* /*env*/, jclass /*clazz*/, jlong ptr) {
    OhSurfaceSession* s = as_session(ptr);
    if (s != nullptr) {
        delete s;
    }
    // If as_session returns null (already-freed / corrupted ptr), silently
    // ignore — matches AOSP behavior where decStrong on a freed client would
    // be a crashing no-op too, but we prefer crash-free over crash-prone.
}

const JNINativeMethod kSurfaceSessionMethods[] = {
    { "nativeCreate",  "()J",  reinterpret_cast<void*>(SS_nativeCreate) },
    { "nativeDestroy", "(J)V", reinterpret_cast<void*>(SS_nativeDestroy) },
};

}  // namespace

int register_android_view_SurfaceSession(JNIEnv* env) {
    jclass clazz = env->FindClass("android/view/SurfaceSession");
    if (clazz == nullptr) {
        fprintf(stderr, "[liboh_android_runtime] register_android_view_SurfaceSession:"
                        " FindClass(android/view/SurfaceSession) failed\n");
        return -1;
    }
    jint rc = env->RegisterNatives(clazz, kSurfaceSessionMethods,
                                    sizeof(kSurfaceSessionMethods)
                                        / sizeof(kSurfaceSessionMethods[0]));
    env->DeleteLocalRef(clazz);
    if (rc != JNI_OK) {
        fprintf(stderr, "[liboh_android_runtime] register_android_view_SurfaceSession:"
                        " RegisterNatives rc=%d\n", (int)rc);
        return -1;
    }
    fprintf(stderr, "[liboh_android_runtime] register_android_view_SurfaceSession:"
                    " 2 methods (nativeCreate, nativeDestroy)\n");
    return 0;
}

}  // namespace android
