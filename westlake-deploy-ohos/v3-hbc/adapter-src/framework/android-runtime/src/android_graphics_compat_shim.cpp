// ============================================================================
// android_graphics_compat_shim.cpp
//
// Last-wins JNI override shim for graphics native methods that either
//   (a) abort with "fid == null" / "mid == null" CheckJNI errors because
//       libhwui's register_X caches a JField/Method ID at register time
//       and the lookup silently failed (often when the AOSP class layout
//       differs from our framework.jar mainline version), or
//   (b) live in AOSP libandroid_runtime.so (which we don't cross-compile)
//       and would otherwise UnsatisfiedLinkError at first call.
//
// Spec: doc/graphics_jni_inventory.html §4.1
// Related: doc/window_manager_ipc_adapter_design.html §3.2 (SurfaceControl
//          equivalent path for Surface/BLASTBufferQueue)
//
// Strategy: explicit RegisterNatives for each problem method.  In ART, when
// liboh_android_runtime's register_X runs after libhwui's RegisterNatives,
// last-wins applies — our stub replaces libhwui's broken impl.  All overrides
// here are SAFE no-op or null returns; they don't render anything but they
// stop the abort/crash so HelloWorld setContentView/onResume can progress
// to the next-layer issue (which we then diagnose via the §3.7/§3.8 stderr
// + FATAL bridges).
//
// Each method below documents:
//   - Which class/method on Java side
//   - Why the "real" hwui/libgui impl can't run on OH
//   - What a real adapter implementation would do (P1 future work)
// ============================================================================

#include "AndroidRuntime.h"

#include <jni.h>
#include <cstring>  // 2026-05-02 G2.14r: std::strncpy / std::strcpy in BBQ alloc

extern "C" int HiLogPrint(int type, int level, unsigned int domain,
                          const char* tag, const char* fmt, ...)
    __attribute__((__format__(printf, 5, 6)));
#define ALOGI(...) HiLogPrint(3, 4, 0xD000F00u, "OH_GfxShim", __VA_ARGS__)
#define ALOGW(...) HiLogPrint(3, 5, 0xD000F00u, "OH_GfxShim", __VA_ARGS__)

namespace android {
namespace {

// =====================================================================
// android.graphics.ImageDecoder
// =====================================================================
// G2.4 (2026-04-30): nGetColorSpace's libhwui impl caches gColorSpace
// jfieldID via GetStaticFieldID at register time.  When that fails (class
// layout mismatch on our framework.jar), the cached fid stays null;
// subsequent GetStaticObjectField(env, ..., null) trips CheckJNI abort.
// Override: return null so caller's getColorSpace() returns null.  No
// Android UI path I know of strict-requires non-null ColorSpace; sRGB
// is assumed when null.
jobject ID_nGetColorSpace(JNIEnv*, jclass, jlong /*nativePtr*/) {
    return nullptr;
}

// G2.4 (2026-04-30 follow-up): nGetPadding hits same fid/jclass cache
// problem as nGetColorSpace.  libhwui calls IsInstanceOf on a cached
// gNinePatchInsetsClass that's null → CheckJNI abort.  Override: leave
// outRect at default (0,0,0,0) — Android Rect default-init is empty.
void ID_nGetPadding(JNIEnv*, jclass, jlong /*nativePtr*/, jobject /*outRect*/) { }

// Stub the remaining ImageDecoder natives to avoid future surprises if
// libhwui's register caches more null IDs.  HelloWorld doesn't decode any
// images so these stubs only fire if some unexpected path hits them.
jobject ID_nCreateAsset(JNIEnv*, jclass, jlong, jboolean, jobject) { return nullptr; }
jobject ID_nCreateByteBuffer(JNIEnv*, jclass, jobject, jint, jint, jboolean, jobject) {
    return nullptr;
}
jobject ID_nCreateByteArray(JNIEnv*, jclass, jbyteArray, jint, jint, jboolean, jobject) {
    return nullptr;
}
jobject ID_nCreateInputStream(JNIEnv*, jclass, jobject, jbyteArray, jboolean, jobject) {
    return nullptr;
}
jobject ID_nCreateFd(JNIEnv*, jclass, jobject, jlong, jboolean, jobject) { return nullptr; }
jobject ID_nDecodeBitmap(JNIEnv*, jclass, jlong, jobject, jboolean, jint, jint,
                         jobject, jboolean, jint, jobject, jboolean, jboolean,
                         jlong, jboolean) {
    return nullptr;
}
jobject ID_nGetSampledSize(JNIEnv*, jclass, jlong, jint) { return nullptr; }
void ID_nClose(JNIEnv*, jclass, jlong /*nativePtr*/) { }
jstring ID_nGetMimeType(JNIEnv* env, jclass, jlong) { return env->NewStringUTF("image/unknown"); }

const JNINativeMethod kImageDecoderMethods[] = {
    { "nGetColorSpace", "(J)Landroid/graphics/ColorSpace;",
      reinterpret_cast<void*>(ID_nGetColorSpace) },
    { "nGetPadding", "(JLandroid/graphics/Rect;)V",
      reinterpret_cast<void*>(ID_nGetPadding) },
    { "nClose", "(J)V",
      reinterpret_cast<void*>(ID_nClose) },
    { "nGetMimeType", "(J)Ljava/lang/String;",
      reinterpret_cast<void*>(ID_nGetMimeType) },
    { "nGetSampledSize", "(JI)Landroid/util/Size;",
      reinterpret_cast<void*>(ID_nGetSampledSize) },
    // nCreate / nDecodeBitmap variants left unbound — if the AOSP overload
    // signature differs subtly, RegisterNatives fails this method only
    // (we register one-by-one) and the rest still bind.
};

// =====================================================================
// android.view.Surface (subset — most-frequently called by ViewRootImpl)
// =====================================================================
// AOSP register_android_view_Surface lives in libandroid_runtime.so which
// we don't cross-compile.  Without RegisterNatives, first call → JNI auto
// resolution by symbol name; if our adapter doesn't export Java_android_view_Surface_*
// → UnsatisfiedLinkError.  Provide a minimum-viable stub set.

// 2026-05-08 G2.14ae: forward decls + types for the SC→sessionId→OHNativeWindow
// chain reused by Surface stubs (below) and BLASTBufferQueue (next section).
// We are already inside `namespace android { namespace { ... } }`, so these
// land in the same anonymous namespace as the definitions in the BBQ block.
extern "C" int32_t oh_sc_get_session(jlong scNativeObject);
static int32_t oh_wm_get_last_session();
static void*   oh_wm_get_native_window(int32_t sessionId);

// OhBlastBufferQueue moved up here (was in BBQ section ~line 299) so Surface
// stubs below can use as_bbq() / b->ohNativeWindow when bridging
// nativeGetFromBlastBufferQueue. Definition, alloc_bbq, as_bbq are now here;
// the BBQ section keeps using them, just defined earlier in the same TU.
struct OhBlastBufferQueue {
    int32_t magic;
    char name[64];
    int32_t width;
    int32_t height;
    int32_t format;
    void* ohNativeWindow;   // resolved by BBQ_nativeUpdate
    int32_t sessionId;      // resolved from SurfaceControl during nativeUpdate
};
constexpr int32_t kOhBbqMagic = 0x4F484251;  // 'OHBQ'

static OhBlastBufferQueue* as_bbq(jlong p) {
    auto* b = reinterpret_cast<OhBlastBufferQueue*>(p);
    if (!b || b->magic != kOhBbqMagic) return nullptr;
    return b;
}

// Resolve a SurfaceControl* to a real OHNativeWindow* (the same handle BBQ
// stamps into Java Surface.mNativeObject). Falls back to "last attached
// session" hint when the SurfaceControl has no sessionId attached yet (mirror
// of BBQ_nativeUpdate's logic). Returns 0 if no window can be resolved —
// hwui's ANativeWindow_fromSurface treats that as "no surface, retry".
static jlong sc_to_oh_native_window(jlong surfaceControl) {
    int32_t scSessionId = surfaceControl ? oh_sc_get_session(surfaceControl) : 0;
    int32_t sessionId = scSessionId;
    int32_t lastSessionId = 0;
    if (sessionId == 0) {
        lastSessionId = oh_wm_get_last_session();
        sessionId = lastSessionId;
    }
    if (sessionId == 0) {
        ALOGW("[DEBUG] sc_to_oh_native_window: sc=0x%llx scSessionId=0 lastSessionId=0 -> 0",
              (long long)surfaceControl);
        return 0;
    }
    void* nw = oh_wm_get_native_window(sessionId);
    if (!nw) {
        ALOGW("[DEBUG] sc_to_oh_native_window: sc=0x%llx scSessionId=%d lastSessionId=%d sessionId=%d "
              "oh_wm_get_native_window returned nullptr -> 0",
              (long long)surfaceControl, scSessionId, lastSessionId, sessionId);
        return 0;
    }
    ALOGI("[DEBUG] sc_to_oh_native_window: sc=0x%llx scSessionId=%d lastSessionId=%d sessionId=%d -> nw=%p",
          (long long)surfaceControl, scSessionId, lastSessionId, sessionId, nw);
    return reinterpret_cast<jlong>(nw);
}

jlong S_nativeCreateFromSurfaceTexture(JNIEnv*, jclass, jobject /*surfaceTexture*/) {
    // Real impl: take the SurfaceTexture's IGraphicBufferProducer and wrap in
    // an OH NativeWindow.  HelloWorld doesn't use SurfaceTexture; return 0.
    return 0;
}
jlong S_nativeCreateFromSurfaceControl(JNIEnv*, jclass, jlong surfaceControl) {
    // 2026-05-08 G2.14ae: real bridge — was returning 0xCAFE5C01 sentinel,
    // which leaked through to hwui's ANativeWindow_fromSurface and made it
    // return null → hwuiTask0 deref crash. Now resolve the OHNativeWindow
    // via the same SC→sessionId→getOhNativeWindow chain BBQ uses.
    jlong nw = sc_to_oh_native_window(surfaceControl);
    ALOGI("S_nativeCreateFromSurfaceControl(sc=0x%llx) -> ohNativeWindow=0x%llx",
          (long long)surfaceControl, (long long)nw);
    return nw;  // 0 if unresolved — caller retries
}
jlong S_nativeCreateFromSurfaceControlNew(JNIEnv*, jclass,
                                          jlong surfaceControl, jlong /*nativeOldSurface*/) {
    return S_nativeCreateFromSurfaceControl(nullptr, nullptr, surfaceControl);
}
void S_nativeRelease(JNIEnv*, jclass, jlong /*nativeObject*/) { }
jboolean S_nativeIsValid(JNIEnv*, jclass, jlong nativeObject) {
    return nativeObject != 0 ? JNI_TRUE : JNI_FALSE;
}
jboolean S_nativeIsConsumerRunningBehind(JNIEnv*, jclass, jlong /*nativeObject*/) {
    return JNI_FALSE;
}
jlong S_nativeReadFromParcel(JNIEnv*, jclass, jlong nativeObject, jobject /*parcel*/) {
    return nativeObject;
}
void S_nativeWriteToParcel(JNIEnv*, jclass, jlong /*nativeObject*/, jobject /*parcel*/) { }
jlong S_nativeLockCanvas(JNIEnv*, jclass, jlong /*nativeObject*/, jobject /*canvas*/, jobject /*dirtyRect*/) {
    // Without a real surface buffer to lock, lockCanvas can't return a real
    // canvas.  HelloWorld doesn't use Surface.lockCanvas (hwui owns drawing).
    return 0;
}
void S_nativeUnlockCanvasAndPost(JNIEnv*, jclass, jlong /*nativeObject*/, jobject /*canvas*/) { }
void S_nativeAllocateBuffers(JNIEnv*, jclass, jlong /*nativeObject*/) { }
jint S_nativeGetWidth(JNIEnv*, jclass, jlong /*nativeObject*/) { return 720; }
jint S_nativeGetHeight(JNIEnv*, jclass, jlong /*nativeObject*/) { return 1280; }
// 2026-05-01 G2.14n: AOSP Surface.java declares these as `int` returns; we were
// registering with `(...)V` signatures and void-returning impls.  RegisterNatives
// in ART silently accepts mismatched signatures; Java callers then read garbage
// register r0 as the int return value → unpredictable behavior downstream.
jint S_nativeAttachAndQueueBufferWithColorSpace(JNIEnv*, jclass, jlong, jobject, jint) { return 0; }
jint S_nativeForceScopedDisconnect(JNIEnv*, jclass, jlong) { return 0; }
jint S_nativeSetFrameRate(JNIEnv*, jclass, jlong, jfloat, jint, jint) { return 0; }

// G2.14v r2 (2026-05-07) — Surface 9 missing fn 补全（同 SurfaceControl A/B/C 范式）。
// AOSP 14 共 22 native，原表 13 项，缺 9（含触发 G2.14v r1 部署后下一层 ULE 的
// nativeGetFromSurfaceControl）。
//
// 本类全 stub，因 OH 没有 Android Surface 等价物（OH 的 ProducerSurface 由
// RSSurfaceNode 持有、走 OHNativeWindow 路径，不通过 Surface JNI 桥）。
// 真渲染走 BLASTBufferQueue 路径（compat_shim §BBQ）；这里只确保 Java 端
// Surface.copyFrom / Surface.release 等不再 ULE。

// nativeGetFromSurfaceControl(currSurface, sc) → long
// AOSP: 把 Surface 对接到 SurfaceControl 的 BufferQueue 上，返回新 mNativeObject。
// 2026-05-08 G2.14ae: real bridge via SC→sessionId→OHNativeWindow chain
// (was returning 0xCAFE5C02 sentinel; same crash mode as nativeCreateFromSurfaceControl).
jlong S_nativeGetFromSurfaceControl(JNIEnv*, jclass, jlong /*nativeObject*/, jlong surfaceControl) {
    jlong nw = sc_to_oh_native_window(surfaceControl);
    ALOGI("S_nativeGetFromSurfaceControl(sc=0x%llx) -> ohNativeWindow=0x%llx",
          (long long)surfaceControl, (long long)nw);
    return nw;
}
// nativeGetFromBlastBufferQueue(currSurface, bbq) → long
// 2026-05-08 G2.14ae: real bridge — was returning the raw BBQ struct ptr as
// if it were ANativeWindow*, which hwui then dereferenced as OHNativeWindow
// (different layout) → crash. Now extract b->ohNativeWindow set by
// BBQ_nativeUpdate. If BBQ.update has not yet run, fall back to the
// last-attached-session hint (same fallback BBQ_nativeUpdate uses).
jlong S_nativeGetFromBlastBufferQueue(JNIEnv*, jclass, jlong /*nativeObject*/, jlong blastBufferQueue) {
    auto* b = as_bbq(blastBufferQueue);
    if (b && b->ohNativeWindow) {
        ALOGI("S_nativeGetFromBlastBufferQueue(bbq=0x%llx sessionId=%d) -> ohNativeWindow=%p",
              (long long)blastBufferQueue, b->sessionId, b->ohNativeWindow);
        return reinterpret_cast<jlong>(b->ohNativeWindow);
    }
    int32_t sid = oh_wm_get_last_session();
    if (sid != 0) {
        void* nw = oh_wm_get_native_window(sid);
        if (nw) {
            ALOGI("S_nativeGetFromBlastBufferQueue(bbq=0x%llx) BBQ.update not yet run; "
                  "using last-session=%d -> ohNativeWindow=%p",
                  (long long)blastBufferQueue, sid, nw);
            return reinterpret_cast<jlong>(nw);
        }
    }
    ALOGW("S_nativeGetFromBlastBufferQueue(bbq=0x%llx): unresolved -> 0",
          (long long)blastBufferQueue);
    return 0;
}
void S_nativeDestroy(JNIEnv*, jclass, jlong /*nativeObject*/) { /* no-op; OH RS 自管 */ }
jlong S_nativeGetNextFrameNumber(JNIEnv*, jclass, jlong /*nativeObject*/) { return 0; }
jint S_nativeSetAutoRefreshEnabled(JNIEnv*, jclass, jlong /*nativeObject*/, jboolean /*enabled*/) { return 0; }
jint S_nativeSetScalingMode(JNIEnv*, jclass, jlong /*nativeObject*/, jint /*mode*/) { return 0; }
jint S_nativeSetSharedBufferModeEnabled(JNIEnv*, jclass, jlong /*nativeObject*/, jboolean /*enabled*/) { return 0; }

const JNINativeMethod kSurfaceMethods[] = {
    { "nativeCreateFromSurfaceTexture", "(Landroid/graphics/SurfaceTexture;)J",
      reinterpret_cast<void*>(S_nativeCreateFromSurfaceTexture) },
    { "nativeCreateFromSurfaceControl", "(J)J",
      reinterpret_cast<void*>(S_nativeCreateFromSurfaceControl) },
    { "nativeGetFromSurfaceControl", "(JJ)J",
      reinterpret_cast<void*>(S_nativeGetFromSurfaceControl) },
    { "nativeGetFromBlastBufferQueue", "(JJ)J",
      reinterpret_cast<void*>(S_nativeGetFromBlastBufferQueue) },
    { "nativeRelease", "(J)V", reinterpret_cast<void*>(S_nativeRelease) },
    { "nativeDestroy", "(J)V", reinterpret_cast<void*>(S_nativeDestroy) },
    { "nativeIsValid", "(J)Z", reinterpret_cast<void*>(S_nativeIsValid) },
    { "nativeIsConsumerRunningBehind", "(J)Z",
      reinterpret_cast<void*>(S_nativeIsConsumerRunningBehind) },
    { "nativeReadFromParcel", "(JLandroid/os/Parcel;)J",
      reinterpret_cast<void*>(S_nativeReadFromParcel) },
    { "nativeWriteToParcel", "(JLandroid/os/Parcel;)V",
      reinterpret_cast<void*>(S_nativeWriteToParcel) },
    { "nativeLockCanvas", "(JLandroid/graphics/Canvas;Landroid/graphics/Rect;)J",
      reinterpret_cast<void*>(S_nativeLockCanvas) },
    { "nativeUnlockCanvasAndPost", "(JLandroid/graphics/Canvas;)V",
      reinterpret_cast<void*>(S_nativeUnlockCanvasAndPost) },
    { "nativeAllocateBuffers", "(J)V",
      reinterpret_cast<void*>(S_nativeAllocateBuffers) },
    { "nativeGetWidth", "(J)I", reinterpret_cast<void*>(S_nativeGetWidth) },
    { "nativeGetHeight", "(J)I", reinterpret_cast<void*>(S_nativeGetHeight) },
    { "nativeGetNextFrameNumber", "(J)J",
      reinterpret_cast<void*>(S_nativeGetNextFrameNumber) },
    { "nativeForceScopedDisconnect", "(J)I",
      reinterpret_cast<void*>(S_nativeForceScopedDisconnect) },
    { "nativeSetFrameRate", "(JFII)I",
      reinterpret_cast<void*>(S_nativeSetFrameRate) },
    { "nativeAttachAndQueueBufferWithColorSpace",
      "(JLandroid/hardware/HardwareBuffer;I)I",
      reinterpret_cast<void*>(S_nativeAttachAndQueueBufferWithColorSpace) },
    { "nativeSetAutoRefreshEnabled", "(JZ)I",
      reinterpret_cast<void*>(S_nativeSetAutoRefreshEnabled) },
    { "nativeSetScalingMode", "(JI)I",
      reinterpret_cast<void*>(S_nativeSetScalingMode) },
    { "nativeSetSharedBufferModeEnabled", "(JZ)I",
      reinterpret_cast<void*>(S_nativeSetSharedBufferModeEnabled) },
};

// =====================================================================
// android.graphics.BLASTBufferQueue
// =====================================================================
// AOSP register_android_graphics_BLASTBufferQueue lives in libandroid_runtime.
// Used by hwui's HardwareRenderer to submit frames.
//
// 2026-05-02 G2.14r — UPGRADED FROM PURE STUB to real OH wire-up.
//   Pre-G2.14r: BBQ_nativeCreate returned a fake sentinel int*; nativeGetSurface
//   returned nullptr; ViewRoot.mSurface stayed empty (mNativeObject=0); hwui's
//   eglCreateWindowSurface(NULL) → LOG_ALWAYS_FATAL → abort 12ms after
//   activityResumed.  Now we allocate a real OhBlastBufferQueue struct, read
//   the WSAdapter session ID from the SurfaceControl in nativeUpdate, and
//   resolve it to an OHNativeWindow* via OHWindowManagerClient.getOhNativeWindow.
//   nativeGetSurface returns a real Java Surface whose mNativeObject points to
//   the OHNativeWindow — hwui's eglCreateWindowSurface accepts it directly.
//
// Forward-declared C exports from sibling translation units:
//   android_view_SurfaceControl.cpp::oh_sc_get_session(jlong sc) → int32_t
//   oh_window_manager_client.cpp::getOhNativeWindow(int32_t) → void*
extern "C" int32_t oh_sc_get_session(jlong scNativeObject);
namespace oh_adapter {
class OHWindowManagerClient;
}

// 2026-05-08 G2.14ae: OhBlastBufferQueue / kOhBbqMagic / as_bbq moved up to
// line ~110 so Surface section (above) can also use them. Only alloc_bbq
// remains here (only used by BBQ_nativeCreate just below).
static OhBlastBufferQueue* alloc_bbq(const char* nameUtf) {
    auto* b = new OhBlastBufferQueue();
    b->magic = kOhBbqMagic;
    b->width = 0; b->height = 0; b->format = 1;
    b->ohNativeWindow = nullptr;
    b->sessionId = 0;
    if (nameUtf) {
        std::strncpy(b->name, nameUtf, sizeof(b->name) - 1);
        b->name[sizeof(b->name) - 1] = 0;
    } else {
        std::strcpy(b->name, "OhBBQ");
    }
    return b;
}

jlong BBQ_nativeCreate(JNIEnv* env, jclass, jstring jname, jboolean /*updateDestinationFrame*/) {
    const char* nameUtf = jname ? env->GetStringUTFChars(jname, nullptr) : nullptr;
    OhBlastBufferQueue* b = alloc_bbq(nameUtf);
    if (jname && nameUtf) env->ReleaseStringUTFChars(jname, nameUtf);
    ALOGI("BBQ.create name=%s ptr=%p", b->name, b);
    return reinterpret_cast<jlong>(b);
}

void BBQ_nativeDestroy(JNIEnv*, jclass, jlong ptr) {
    auto* b = as_bbq(ptr);
    if (b) {
        b->magic = 0;
        // ohNativeWindow is owned by surface_utils via RSSurfaceNode lifecycle
        // (see oh_window_manager_client.cpp::getOhNativeWindow).  We don't
        // free it here — the RSSurfaceNode in OHWindowManagerClient holds the
        // strong ref; destroy on session teardown.
        delete b;
    }
}

// Forward declaration so we can reference getOhNativeWindow without pulling
// the entire oh_window_manager_client header chain into compat_shim.
//
// 2026-05-02 G2.14r: these two symbols live in liboh_adapter_bridge.so but
// are referenced from liboh_android_runtime.so (this file).  Cross-.so
// link-time linkage is not declared (bridge.so is loaded later by JNI_OnLoad
// on adapter side), so resolve dynamically via dlsym to avoid "symbol not
// found" at dlopen of liboh_android_runtime.so.  Cached after first lookup.
#include <dlfcn.h>
typedef void* (*oh_wm_get_native_window_fn_t)(int32_t);
typedef int32_t (*oh_wm_get_last_session_fn_t)();
static oh_wm_get_native_window_fn_t g_oh_wm_get_native_window_fn = nullptr;
static oh_wm_get_last_session_fn_t  g_oh_wm_get_last_session_fn  = nullptr;
static void resolve_oh_wm_funcs() {
    static bool s_logged = false;
    if (g_oh_wm_get_native_window_fn && g_oh_wm_get_last_session_fn) return;

    // 2026-05-09 G2.14ac: previous code used dlsym(RTLD_DEFAULT, ...) which on
    // OH only searches the caller's linker namespace and the main executable
    // (appspawn-x). The target symbols live in liboh_adapter_bridge.so which
    // lives in a different namespace, so RTLD_DEFAULT misses them — dlerror
    // confirmed: "Symbol not found ... so=/system/bin/appspawn-x".
    //
    // Fix: dlopen the bridge .so explicitly to obtain a per-namespace handle,
    // then dlsym against that handle. RTLD_NOLOAD first to reuse the already-
    // loaded image (OHEnvironment static init loaded it earlier on the adapter
    // bridge namespace), fall back to a fresh dlopen if NOLOAD misses (e.g.
    // namespace boundary still hides it; OH dynamic linker then searches the
    // file from default lib paths).
    static void* s_bridgeHandle = nullptr;
    if (!s_bridgeHandle) {
        s_bridgeHandle = dlopen("liboh_adapter_bridge.so", RTLD_NOW | RTLD_NOLOAD);
        const char* mode = "RTLD_NOLOAD";
        if (!s_bridgeHandle) {
            (void)dlerror();
            s_bridgeHandle = dlopen("liboh_adapter_bridge.so", RTLD_NOW);
            mode = "fresh dlopen";
        }
        if (!s_bridgeHandle) {
            (void)dlerror();
            s_bridgeHandle = dlopen("/system/lib/liboh_adapter_bridge.so", RTLD_NOW);
            mode = "abs path /system/lib";
        }
        if (s_bridgeHandle) {
            ALOGI("[DEBUG] resolve_oh_wm_funcs: liboh_adapter_bridge.so handle=%p (%s)",
                  s_bridgeHandle, mode);
        } else {
            const char* err = dlerror();
            ALOGW("[DEBUG] resolve_oh_wm_funcs: dlopen liboh_adapter_bridge.so FAILED dlerror='%s'",
                  err ? err : "(null)");
        }
    }

    void* lookupHandle = s_bridgeHandle ? s_bridgeHandle : RTLD_DEFAULT;
    if (!g_oh_wm_get_native_window_fn) {
        g_oh_wm_get_native_window_fn = reinterpret_cast<oh_wm_get_native_window_fn_t>(
            dlsym(lookupHandle, "oh_wm_get_native_window"));
    }
    if (!g_oh_wm_get_last_session_fn) {
        g_oh_wm_get_last_session_fn = reinterpret_cast<oh_wm_get_last_session_fn_t>(
            dlsym(lookupHandle, "oh_wm_get_last_session"));
    }
    if (!s_logged) {
        const char* err = dlerror();
        ALOGI("[DEBUG] resolve_oh_wm_funcs: get_native_window_fn=%p get_last_session_fn=%p dlerror='%s'",
              (void*)g_oh_wm_get_native_window_fn,
              (void*)g_oh_wm_get_last_session_fn,
              err ? err : "(null)");
        if (g_oh_wm_get_native_window_fn && g_oh_wm_get_last_session_fn) {
            s_logged = true;
        }
    }
}
static void* oh_wm_get_native_window(int32_t sessionId) {
    resolve_oh_wm_funcs();
    return g_oh_wm_get_native_window_fn ? g_oh_wm_get_native_window_fn(sessionId) : nullptr;
}
static int32_t oh_wm_get_last_session() {
    resolve_oh_wm_funcs();
    return g_oh_wm_get_last_session_fn ? g_oh_wm_get_last_session_fn() : 0;
}

void BBQ_nativeUpdate(JNIEnv*, jclass, jlong bbqPtr, jlong scPtr,
                      jint width, jint height, jint format, jlong /*timestamp*/) {
    auto* b = as_bbq(bbqPtr);
    if (!b) {
        ALOGW("BBQ.update: invalid bbqPtr=%lld", (long long)bbqPtr);
        return;
    }
    b->width = width;
    b->height = height;
    b->format = format;
    int32_t sessionId = oh_sc_get_session(scPtr);
    if (sessionId == 0) {
        // 2026-05-02 G2.14r: fall back to "last attached session" hint set by
        // OHWindowManagerClient::createSession.  Avoids needing a BCP-jar
        // native method to attach session to SurfaceControl (which would
        // require boot image rebuild on every change to that BCP class).
        // Each child appspawn-x process spawns one app with one session, so
        // process-global last-session is unambiguous in our model.
        sessionId = oh_wm_get_last_session();
        if (sessionId != 0) {
            ALOGI("BBQ.update: SurfaceControl had no sessionId — falling back to "
                  "last-attached-session hint sessionId=%d", sessionId);
        }
    }
    b->sessionId = sessionId;
    if (sessionId == 0) {
        ALOGW("BBQ.update: no sessionId resolvable (no SC attach + no last hint); "
              "render will use empty Surface");
        return;
    }
    // Resolve sessionId → OHNativeWindow*.  Cached per-session in
    // OHWindowManagerClient so repeated update() calls get the same pointer.
    void* nw = oh_wm_get_native_window(sessionId);
    if (!nw) {
        ALOGW("BBQ.update: getOhNativeWindow(sessionId=%d) returned null", sessionId);
        return;
    }
    b->ohNativeWindow = nw;
    ALOGI("BBQ.update: sessionId=%d → OHNativeWindow=%p (%dx%d fmt=%d)",
          sessionId, nw, width, height, format);
}

// Forward declaration: AOSP exposes a JNI helper to construct a Java Surface
// from a native ANativeWindow*.  See frameworks/base/core/jni/android_view_Surface.cpp.
// On OH our OHNativeWindow* is pin-compatible with ANativeWindow*.
extern "C" jobject android_view_Surface_createFromIGraphicBufferProducer(
        JNIEnv* env, void* /*producer*/) __attribute__((weak));

jobject BBQ_nativeGetSurface(JNIEnv* env, jclass, jlong bbqPtr,
                              jboolean /*includeSurfaceControlHandle*/) {
    auto* b = as_bbq(bbqPtr);
    if (!b) {
        ALOGW("BBQ.getSurface: invalid bbqPtr=%lld", (long long)bbqPtr);
        return nullptr;
    }
    if (!b->ohNativeWindow) {
        ALOGW("BBQ.getSurface: ohNativeWindow null (sessionId=%d not attached "
              "or BBQ.update not yet called) — returning empty Surface so "
              "ViewRoot.mSurface.transferFrom doesn't NPE",
              b->sessionId);
        // Return an empty Surface so transferFrom() has a non-null target;
        // hwui will then operate on an empty surface (same as pre-G2.14r
        // behavior, but at least no NPE on transferFrom).
        jclass surfaceCls = env->FindClass("android/view/Surface");
        if (!surfaceCls) {
            if (env->ExceptionCheck()) env->ExceptionClear();
            return nullptr;
        }
        jmethodID ctor = env->GetMethodID(surfaceCls, "<init>", "()V");
        if (!ctor) {
            if (env->ExceptionCheck()) env->ExceptionClear();
            env->DeleteLocalRef(surfaceCls);
            return nullptr;
        }
        jobject empty = env->NewObject(surfaceCls, ctor);
        env->DeleteLocalRef(surfaceCls);
        return empty;
    }

    // Construct a real Java Surface and stamp ohNativeWindow into its
    // mNativeObject field.  On AOSP, Surface(long nativeObject) is a private
    // constructor that adopts the native handle — but that's not part of the
    // public API.  Use reflection: default-construct, then set mNativeObject.
    //
    // Important: AOSP Surface destructor calls nativeRelease which would try
    // to dec-ref a real ANativeWindow.  Our OHNativeWindow has a different
    // ref-count protocol (managed by surface_utils + RSSurfaceNode lifecycle).
    // To avoid double-free, we DO NOT take ownership: when the Java Surface
    // is GC'd, mNativeObject is set to 0 first via reflection in our future
    // session-teardown path.  For HelloWorld P1 this is sufficient.
    jclass surfaceCls = env->FindClass("android/view/Surface");
    if (!surfaceCls) {
        ALOGW("BBQ.getSurface: FindClass(android/view/Surface) failed");
        if (env->ExceptionCheck()) env->ExceptionClear();
        return nullptr;
    }
    jmethodID ctor = env->GetMethodID(surfaceCls, "<init>", "()V");
    if (!ctor) {
        ALOGW("BBQ.getSurface: GetMethodID(<init>) failed");
        if (env->ExceptionCheck()) env->ExceptionClear();
        env->DeleteLocalRef(surfaceCls);
        return nullptr;
    }
    jobject surface = env->NewObject(surfaceCls, ctor);
    if (!surface) {
        ALOGW("BBQ.getSurface: NewObject(Surface) failed");
        if (env->ExceptionCheck()) env->ExceptionClear();
        env->DeleteLocalRef(surfaceCls);
        return nullptr;
    }
    jfieldID mNativeObjectFid = env->GetFieldID(surfaceCls, "mNativeObject", "J");
    if (!mNativeObjectFid) {
        ALOGW("BBQ.getSurface: GetFieldID(mNativeObject) failed");
        if (env->ExceptionCheck()) env->ExceptionClear();
        env->DeleteLocalRef(surfaceCls);
        return surface;
    }
    env->SetLongField(surface, mNativeObjectFid, reinterpret_cast<jlong>(b->ohNativeWindow));
    ALOGI("BBQ.getSurface: returning Surface with mNativeObject=%p (sessionId=%d)",
          b->ohNativeWindow, b->sessionId);
    env->DeleteLocalRef(surfaceCls);
    return surface;
}

void BBQ_nativeSetNextTransaction(JNIEnv*, jclass, jlong, jlong) { }
void BBQ_nativeSetSyncTransaction(JNIEnv*, jclass, jlong, jlong, jboolean) { }
void BBQ_nativeMergeWithNextTransaction(JNIEnv*, jclass, jlong, jlong, jlong) { }
jlong BBQ_nativeGetLastAcquiredFrameNum(JNIEnv*, jclass, jlong) { return 0; }
void BBQ_nativeFlushShadowQueue(JNIEnv*, jclass, jlong) { }
void BBQ_nativeApplyPendingTransactions(JNIEnv*, jclass, jlong, jlong) { }
jlong BBQ_nativeGatherPendingTransactions(JNIEnv*, jclass, jlong, jlong) { return 0; }

const JNINativeMethod kBlastBufferQueueMethods[] = {
    { "nativeCreate", "(Ljava/lang/String;Z)J",
      reinterpret_cast<void*>(BBQ_nativeCreate) },
    { "nativeDestroy", "(J)V",
      reinterpret_cast<void*>(BBQ_nativeDestroy) },
    { "nativeGetSurface", "(JZ)Landroid/view/Surface;",
      reinterpret_cast<void*>(BBQ_nativeGetSurface) },
    { "nativeSetNextTransaction", "(JJ)V",
      reinterpret_cast<void*>(BBQ_nativeSetNextTransaction) },
    { "nativeUpdate", "(JJJIIIJ)V",
      reinterpret_cast<void*>(BBQ_nativeUpdate) },
    { "nativeSetSyncTransaction", "(JJZ)V",
      reinterpret_cast<void*>(BBQ_nativeSetSyncTransaction) },
    { "nativeMergeWithNextTransaction", "(JJJ)V",
      reinterpret_cast<void*>(BBQ_nativeMergeWithNextTransaction) },
    { "nativeGetLastAcquiredFrameNum", "(J)J",
      reinterpret_cast<void*>(BBQ_nativeGetLastAcquiredFrameNum) },
    { "nativeFlushShadowQueue", "(J)V",
      reinterpret_cast<void*>(BBQ_nativeFlushShadowQueue) },
    { "nativeApplyPendingTransactions", "(JJ)V",
      reinterpret_cast<void*>(BBQ_nativeApplyPendingTransactions) },
    { "nativeGatherPendingTransactions", "(JJ)J",
      reinterpret_cast<void*>(BBQ_nativeGatherPendingTransactions) },
};

// =====================================================================
// android.view.DisplayEventReceiver
// =====================================================================
// AOSP register_android_view_DisplayEventReceiver lives in libandroid_runtime.
// hwui Choreographer subclasses this to receive vsync.  Without a real
// impl, Choreographer.scheduleVsync NPE on first call.  Stub: return a
// non-null sentinel; manual frame scheduling won't be vsync-aligned but
// HelloWorld static layout doesn't strictly need vsync.

jlong DER_nativeInit(JNIEnv*, jclass, jobject /*receiverWeak*/, jobject /*messageQueue*/,
                    jint /*vsyncSource*/, jint /*eventRegistration*/, jlong /*layerHandle*/) {
    static int sDerSentinel = 0xCAFE0DE5;
    return reinterpret_cast<jlong>(&sDerSentinel);
}
void DER_nativeDispose(JNIEnv*, jclass, jlong /*receiverPtr*/) { }
void DER_nativeScheduleVsync(JNIEnv*, jclass, jlong /*receiverPtr*/) {
    // Real impl: register a one-shot OH RSDisplayNode vsync callback.
    // No-op for now — Choreographer will time out, then re-schedule;
    // each vsync cycle ~16.6ms the Choreographer falls back to a manual
    // tick (it uses currentTimeNanos delta) so animations may be jittery.
}
jobject DER_nativeGetLatestVsyncEventData(JNIEnv*, jclass, jlong) {
    return nullptr;
}

const JNINativeMethod kDisplayEventReceiverMethods[] = {
    { "nativeInit",
      "(Ljava/lang/ref/WeakReference;Landroid/os/MessageQueue;IIJ)J",
      reinterpret_cast<void*>(DER_nativeInit) },
    { "nativeDispose", "(J)V",
      reinterpret_cast<void*>(DER_nativeDispose) },
    { "nativeScheduleVsync", "(J)V",
      reinterpret_cast<void*>(DER_nativeScheduleVsync) },
    { "nativeGetLatestVsyncEventData",
      "(J)Landroid/view/DisplayEventReceiver$VsyncEventData;",
      reinterpret_cast<void*>(DER_nativeGetLatestVsyncEventData) },
};

// =====================================================================
// Helper: register one method-array, method-by-method, tolerate failures
// =====================================================================
int registerOne(JNIEnv* env, const char* className,
                const JNINativeMethod* methods, size_t count) {
    jclass clazz = env->FindClass(className);
    if (!clazz) {
        ALOGW("FindClass(%s) failed — class not in BCP yet?", className);
        if (env->ExceptionCheck()) env->ExceptionClear();
        return -1;
    }
    int ok = 0, fail = 0;
    for (size_t i = 0; i < count; ++i) {
        jint rc = env->RegisterNatives(clazz, &methods[i], 1);
        if (rc == JNI_OK) {
            ++ok;
        } else {
            ++fail;
            ALOGW("compat_shim: register %s.%s%s failed",
                  className, methods[i].name, methods[i].signature);
            if (env->ExceptionCheck()) env->ExceptionClear();
        }
    }
    env->DeleteLocalRef(clazz);
    ALOGI("compat_shim: %s registered %d/%zu", className, ok, count);
    return 0;  // never propagate failure to caller — shim is best-effort
}

// =====================================================================
// 2026-05-11 G2.14ar — G2.14an BaseCanvas / G2.14ao HardwareRenderer
//                       diagnostic probes REMOVED.
// =====================================================================
// G2.14an installed 13 last-wins overrides on android.graphics.BaseCanvas
// nDrawXxx (non-forwarding stubs, log only).  G2.14ao installed similar
// stubs on HardwareRenderer.nSyncAndDrawFrame.
//
// Both violated the project architecture invariant: BaseCanvas / Canvas /
// Paint / RenderNode / HardwareRenderer JNI MUST use the AOSP-native
// libhwui implementation registered via dlsym at startReg
// (AndroidRuntime.cpp ~line 411 `register_android_graphics_Canvas` and
// related entries).  adapter is a thin bridge, not an hwui replacement
// (parallel evidence: `android_graphics_Canvas.cpp`,
// `_HardwareRenderer.cpp`, `_RenderNode.cpp`, `_Paint.cpp` are all
// retired stubs in compile_oh_android_runtime.sh — already excluded from
// the .so build for this exact reason).
//
// Diagnostic value already captured (memory project_g214aq_rs_pid_bypass
// notes: probes 0 fire confirms Java-side BaseCanvas.nDrawXxx is never
// called; root cause is upstream in View tree record path — independent
// of whether libhwui or stub is bound here).
// =====================================================================

}  // namespace

// Public entry called by AndroidRuntime::startReg AFTER libhwui's
// register_X loop, so our overrides win (last-wins JNI semantics).
int register_android_graphics_compat_shim(JNIEnv* env) {
    registerOne(env, "android/graphics/ImageDecoder",
                kImageDecoderMethods,
                sizeof(kImageDecoderMethods) / sizeof(kImageDecoderMethods[0]));
    registerOne(env, "android/view/Surface",
                kSurfaceMethods,
                sizeof(kSurfaceMethods) / sizeof(kSurfaceMethods[0]));
    registerOne(env, "android/graphics/BLASTBufferQueue",
                kBlastBufferQueueMethods,
                sizeof(kBlastBufferQueueMethods) / sizeof(kBlastBufferQueueMethods[0]));
    // 2026-05-11 G2.14ar — G2.14an BaseCanvas probe + G2.14ao HardwareRenderer
    // probe registerOne() calls REMOVED.  BaseCanvas / Canvas / Paint /
    // HardwareRenderer / RenderNode JNI now bind only to the AOSP-native
    // libhwui impls registered via dlsym in startReg (AndroidRuntime.cpp
    // ~line 411 register_android_graphics_Canvas etc).  See the removed
    // diagnostic-probe block comment above for full rationale.
    // 2026-05-01 G2.14n: DisplayEventReceiver registration REMOVED from compat
    // shim.  The real timer-based vsync impl already lives in
    // android_view_DisplayEventReceiver.cpp (registered via kRegJNI).  Letting
    // compat_shim re-register here with a no-op DER_nativeScheduleVsync was
    // overriding the real impl (last-wins) — Choreographer never received vsync
    // ticks → ViewRootImpl.performTraversals never ran → HWUI never started →
    // blank window.
    return 0;
}

}  // namespace android

// =============================================================================
// 2026-05-08 G2.14ad: ANativeWindow NDK bridges (relocated from atrace_stubs.cpp)
// =============================================================================
//
// libhwui ThreadedRenderer (jni/android_graphics_HardwareRenderer.cpp:1100)
// does dlopen("libandroid.so") + dlsym("ANativeWindow_*"). On our device
// libandroid.so is symlinked to liboh_android_runtime.so (this library), so
// the dlsym hits the impls below.
//
// They live in compat_shim.cpp because the upstream of mNativeObject — the
// `BBQ_nativeGetSurface` call that stuffs an OHNativeWindow* into the Java
// Surface — is in the same file (~ line 440). Putting both ends of the
// mNativeObject contract here keeps the surface-handle invariants visible
// in one place.
//
// Real impl strategy:
//   - ANativeWindow_fromSurface: read mNativeObject from Java Surface; if it
//     looks like a sentinel (set by S_nativeCreateFromSurfaceControl etc.
//     when no real BBQ-bound surface is available yet), return null so
//     hwui retries on the next frame. Otherwise hand back the OHNativeWindow*
//     directly — on OH `OHNativeWindow ≡ ANativeWindow`, no conversion needed.
//   - ANativeWindow_acquire/release: route to OH NativeObjectReference/Unreference.
//   - getWidth/Height/getFormat: route to OH NativeWindowHandleOpt.
//
// All lifecycle / dimension queries land in OH inner_api/surface/window.h.

// Opaque alias — same layout as OHNativeWindow
struct ANativeWindow;
struct OHNativeWindow;

// OH inner API forward decls (graphic_surface/interfaces/inner_api/surface/window.h)
extern "C" int32_t NativeObjectReference(void *obj);
extern "C" int32_t NativeObjectUnreference(void *obj);
extern "C" int32_t NativeWindowHandleOpt(OHNativeWindow *window, int code, ...);
#define OH_OP_GET_BUFFER_GEOMETRY 1
#define OH_OP_GET_FORMAT          2

// G2.14ag: AdapterAnw shim probes (impl in framework/window/jni/
// oh_anativewindow_shim.cpp, packed into liboh_adapter_bridge.so). We
// do not #include the header here to avoid cross-target include path
// drift; opaque struct fwd decls above are sufficient.
extern "C" int oh_anw_try_acquire(struct ANativeWindow* aosp);
extern "C" int oh_anw_try_release(struct ANativeWindow* aosp);
extern "C" struct OHNativeWindow* oh_anw_get_oh(struct ANativeWindow* aosp);

// Unwrap an ANativeWindow* — if it's an AdapterAnw shim, return the
// embedded real OHNativeWindow*. Otherwise pass through (assume `w` is
// already an OHNativeWindow*, the pre-G2.14ae direct cast invariant).
static inline OHNativeWindow* anw_unwrap(ANativeWindow* w) {
    if (!w) return nullptr;
    OHNativeWindow* oh = oh_anw_get_oh(w);
    return oh ? oh : reinterpret_cast<OHNativeWindow*>(w);
}

// HiLogPrint forward-decl above (line 35) takes int level. Use the same magic
// numbers as ALOGI/ALOGW: type 3 = LOG_CORE, level 4/5/6 = INFO/WARN/ERROR.
#define NWFS_INFO(fmt, ...) \
    HiLogPrint(3, 4, 0xD000F00u, "OH_NWFromSurface", fmt, ##__VA_ARGS__)
#define NWFS_WARN(fmt, ...) \
    HiLogPrint(3, 5, 0xD000F00u, "OH_NWFromSurface", fmt, ##__VA_ARGS__)

// Sentinels (0xCAFE5C0X) live in BSS/RDATA of liboh_android_runtime.so as
// `static int sSentinelStorage = 0xCAFE5C0X;`. A Java Surface whose
// mNativeObject points at one of these has no real BBQ-bound buffer.
// Detect by reading the first 4 bytes — sentinel storage holds the magic
// value, while a real OHNativeWindow's first word is its vtable / RefBase.
static inline bool is_sentinel_handle(jlong h) {
    if (h == 0) return true;
    int32_t magic = *reinterpret_cast<int32_t*>(h);
    return (magic & 0xFFFFFFF0) == 0xCAFE5C00;
}

extern "C" ANativeWindow* ANativeWindow_fromSurface(JNIEnv* env, jobject surface) {
    NWFS_INFO("[STAGE0] ENTER env=%{public}p surface=%{public}p",
              (void*)env, (void*)surface);
    if (!env || !surface) {
        NWFS_WARN("[STAGE0] null arg -> null");
        return nullptr;
    }
    jclass cls = env->FindClass("android/view/Surface");
    if (!cls) { env->ExceptionClear();
        NWFS_WARN("[STAGE0] FindClass failed -> null"); return nullptr; }
    jfieldID fid = env->GetFieldID(cls, "mNativeObject", "J");
    env->DeleteLocalRef(cls);
    if (!fid) { env->ExceptionClear();
        NWFS_WARN("[STAGE0] GetFieldID failed -> null"); return nullptr; }
    jlong nativeObj = env->GetLongField(surface, fid);
    NWFS_INFO("[STAGE0] mNativeObject=0x%{public}llx",
              (unsigned long long)nativeObj);
    if (nativeObj == 0) {
        NWFS_WARN("[STAGE0] mNativeObject==0 -> null");
        return nullptr;
    }
    if (is_sentinel_handle(nativeObj)) {
        NWFS_WARN("[STAGE0] sentinel handle 0x%{public}llx -> null "
                  "(no real BBQ-bound surface yet; hwui will retry)",
                  (unsigned long long)nativeObj);
        return nullptr;
    }
    // Real OHNativeWindow* set by BBQ_nativeGetSurface above. On OH,
    // OHNativeWindow ≡ ANativeWindow, so hand it back to hwui directly.
    NWFS_INFO("[STAGE0] returning OHNativeWindow=%{public}p directly",
              reinterpret_cast<void*>(nativeObj));
    return reinterpret_cast<ANativeWindow*>(nativeObj);
}

// G2.14ag: detect AdapterAnw shim (post-G2.14ae). When hwui passes a shim
// here, route refcount to AdapterAnw's atomic (AOSP common.incRef/decRef
// equivalent); else forward to OH NativeObjectReference. See
// doc/graphics_rendering_design.html §7.13 (ref counting).
extern "C" void ANativeWindow_acquire(ANativeWindow* w) {
    if (!w) return;
    if (oh_anw_try_acquire(w)) return;          // shim path
    NativeObjectReference(reinterpret_cast<void*>(w));
}

extern "C" void ANativeWindow_release(ANativeWindow* w) {
    if (!w) return;
    if (oh_anw_try_release(w)) return;          // shim path
    NativeObjectUnreference(reinterpret_cast<void*>(w));
}

extern "C" int32_t ANativeWindow_getWidth(ANativeWindow* w) {
    if (!w) return 0;
    int32_t width = 0, height = 0, format = 0;
    NativeWindowHandleOpt(anw_unwrap(w),        // G2.14ag: unwrap shim
                           OH_OP_GET_BUFFER_GEOMETRY, &height, &width, &format);
    return width;
}

extern "C" int32_t ANativeWindow_getHeight(ANativeWindow* w) {
    if (!w) return 0;
    int32_t width = 0, height = 0, format = 0;
    NativeWindowHandleOpt(anw_unwrap(w),        // G2.14ag: unwrap shim
                           OH_OP_GET_BUFFER_GEOMETRY, &height, &width, &format);
    return height;
}

extern "C" int32_t ANativeWindow_getFormat(ANativeWindow* w) {
    if (!w) return 0;
    int32_t format = 0;
    NativeWindowHandleOpt(anw_unwrap(w),        // G2.14ag: unwrap shim
                           OH_OP_GET_FORMAT, &format);
    return format;
}
