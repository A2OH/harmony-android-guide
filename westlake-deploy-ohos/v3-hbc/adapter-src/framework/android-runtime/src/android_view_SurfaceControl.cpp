// ============================================================================
// android_view_SurfaceControl.cpp
//
// JNI bridge for android.view.SurfaceControl, routing AOSP's SurfaceControl
// API to OpenHarmony Rosen RSSurfaceNode + Surface (NativeWindow producer).
//
// Spec: doc/window_manager_ipc_adapter_design.html §3.2 (relayout 真因)
//
// AOSP SurfaceControl is the handle a client holds for one SurfaceFlinger
// layer.  OH has no SurfaceFlinger; the equivalent is RSSurfaceNode +
// per-window Surface (whose IGraphicBufferProducer maps to OH NativeWindow).
//
// G2 (2026-04-30) P1 minimum-viable shim:
//   * nativeCreate returns a real ptr to OhSurfaceControl struct so
//     SurfaceControl.Builder.build() succeeds and downstream Java code does
//     NOT see mNativeObject == 0 — that previously made hwui CHECK fail
//     in HardwareRenderer.setSurfaceControl path → SIGABRT.
//   * Most setters / transaction ops are no-op stubs that record but discard.
//   * nativeCopyFromSurfaceControl + nativeWriteToParcel + nativeReadFromParcel
//     return safe pointers so SurfaceControl.copyFrom() doesn't NPE.
//
// What the shim does NOT yet do (P2/P3 backlog):
//   * Actually drive OH RSSurfaceNode buffer queue from SurfaceControl ops.
//   * Wire transaction commits (setPosition / setMatrix / setAlpha) to
//     OH RSDisplayNode property updates.
//   * Forward layer Z-order / mirror / overlay setup to OH SceneSession.
//
// The hot path that matters for HelloWorld setContentView:
//   ViewRootImpl → relayout(...) → outSurfaceControl native ptr non-zero
//   → HardwareRenderer.setSurfaceControl(sc) → hwui sees a non-null sp
//   → first dequeueBuffer goes through ANativeWindow path
//   → ANativeWindow_dequeueBuffer (in liboh_hwui_shim.so) → OH NativeWindow
// As long as the ANativeWindow that hwui ends up using is the one tied to
// OH RSSurfaceNode (which the existing addToDisplay/relayout machinery
// produces via OHWindowSessionClient), we don't strictly need each
// transaction op wired — they're optional optimizations.
// ============================================================================

#include "AndroidRuntime.h"

#include <atomic>
#include <cstdint>
#include <cstring>
#include <dlfcn.h>
#include <jni.h>
#include <mutex>

// 2026-05-06 — Per design §5.6 / §9.1 三件套 condition #3:
//   SurfaceControl property setters and apply() must route to OH RSSurfaceNode
//   + RSTransactionProxy.  These C wrappers live in liboh_adapter_bridge.so
//   (oh_window_manager_client.cpp).  We resolve them via dlsym RTLD_DEFAULT
//   on first use — same pattern as G2.14r BBQ wiring (compat_shim.cpp).
//   If liboh_adapter_bridge.so isn't loaded yet (e.g., shouldn't happen post
//   appspawn-x boot), the call is a no-op and we keep the magic-struct stub
//   semantics intact.
namespace {
using FnSetBounds      = void (*)(int32_t, float, float, float, float);
using FnSetAlpha       = void (*)(int32_t, float);
using FnSetVisible     = void (*)(int32_t, int32_t);
using FnSetOpaque      = void (*)(int32_t, int32_t);   // G2.14al
using FnFlushTx        = void (*)();
using FnGetLastSession = int32_t (*)();                // G2.14al

FnSetBounds      g_oh_rs_set_layer_bounds  = nullptr;
FnSetAlpha       g_oh_rs_set_layer_alpha   = nullptr;
FnSetVisible     g_oh_rs_set_layer_visible = nullptr;
FnSetOpaque      g_oh_rs_set_layer_opaque  = nullptr;   // G2.14al
FnFlushTx        g_oh_rs_flush_transaction = nullptr;
FnGetLastSession g_oh_wm_get_last_session  = nullptr;   // G2.14al
std::once_flag g_rs_route_resolve_once;

void resolve_rs_routes() {
    std::call_once(g_rs_route_resolve_once, []() {
        g_oh_rs_set_layer_bounds  = reinterpret_cast<FnSetBounds>(
            dlsym(RTLD_DEFAULT, "oh_rs_set_layer_bounds"));
        g_oh_rs_set_layer_alpha   = reinterpret_cast<FnSetAlpha>(
            dlsym(RTLD_DEFAULT, "oh_rs_set_layer_alpha"));
        g_oh_rs_set_layer_visible = reinterpret_cast<FnSetVisible>(
            dlsym(RTLD_DEFAULT, "oh_rs_set_layer_visible"));
        g_oh_rs_set_layer_opaque  = reinterpret_cast<FnSetOpaque>(
            dlsym(RTLD_DEFAULT, "oh_rs_set_layer_opaque"));  // G2.14al
        g_oh_rs_flush_transaction = reinterpret_cast<FnFlushTx>(
            dlsym(RTLD_DEFAULT, "oh_rs_flush_transaction"));
        g_oh_wm_get_last_session  = reinterpret_cast<FnGetLastSession>(
            dlsym(RTLD_DEFAULT, "oh_wm_get_last_session"));  // G2.14al
    });
}

// 2026-05-11 G2.14al — resolve effective sessionId.
// If SurfaceControl wasn't directly stamped with sessionId (because the
// AOSP-side SC creation path took the SurfaceControl.Builder.build() route
// without going through oh_sc_attach_session — G2.14r's BBQ shortcut relies
// on a process-global last-attached-session thread-local instead), fall
// back to the most recent attached session.  Safe for single-foreground-
// app scenarios (HelloWorld baseline); revisit if we ever support multi-
// session multi-window apps.
int32_t resolve_effective_session(int32_t scSessionId) {
    if (scSessionId != 0) return scSessionId;
    resolve_rs_routes();
    if (g_oh_wm_get_last_session) {
        return g_oh_wm_get_last_session();
    }
    return 0;
}
}  // namespace

// Direct HiLogPrint (innerAPI libhilog.so) — same domain as §3.2 / §3.5 bridges
extern "C" int HiLogPrint(int type, int level, unsigned int domain,
                          const char* tag, const char* fmt, ...)
    __attribute__((__format__(printf, 5, 6)));
#define ALOGI(...) HiLogPrint(3, 4, 0xD000F00u, "OH_SurfaceControl", __VA_ARGS__)
#define ALOGW(...) HiLogPrint(3, 5, 0xD000F00u, "OH_SurfaceControl", __VA_ARGS__)

namespace android {
namespace {

// ----------------------------------------------------------------------------
// OhSurfaceControl — minimal struct that backs Java SurfaceControl.mNativeObject.
// Holds enough state that downstream calls (toString / copyFrom / setLayer
// / hide / show) don't need to talk to a real SurfaceFlinger.
//
// Future P2: add an `OHOS::sptr<OHOS::Rosen::RSSurfaceNode>` field so
// transaction ops can update real OH render properties.
// ----------------------------------------------------------------------------
struct OhSurfaceControl {
    int32_t magic;        // 'OHSC' = 0x4F485343 — used for sanity checks
    char name[64];
    int32_t width;
    int32_t height;
    int32_t format;
    int32_t flags;
    int32_t layer;        // z-order
    bool visible;
    // 2026-05-02 G2.14r: associated WSAdapter session ID — set after createSession
    // via attachSession exported helper.  Used by BLASTBufferQueue stub to look
    // up the underlying RSSurfaceNode + create OHNativeWindow for hwui rendering.
    // 0 = unattached (legacy fake-only path; pre-G2.14r behavior).
    int32_t sessionId;
    // Future: OHOS::sptr<OHOS::Rosen::RSSurfaceNode> rsSurfaceNode;
};

constexpr int32_t kOhSurfaceControlMagic = 0x4F485343;

OhSurfaceControl* alloc_sc(const char* name) {
    auto* sc = new OhSurfaceControl();
    sc->magic = kOhSurfaceControlMagic;
    sc->width = 0;  sc->height = 0;
    sc->format = 1; // RGBA_8888
    sc->flags = 0;  sc->layer = 0;  sc->visible = false;
    sc->sessionId = 0;  // G2.14r: 0 = unattached (legacy)
    if (name) {
        std::strncpy(sc->name, name, sizeof(sc->name) - 1);
        sc->name[sizeof(sc->name) - 1] = 0;
    } else {
        std::strcpy(sc->name, "OhSurfaceControl");
    }
    return sc;
}

OhSurfaceControl* as_sc(jlong p) {
    auto* sc = reinterpret_cast<OhSurfaceControl*>(p);
    if (!sc || sc->magic != kOhSurfaceControlMagic) return nullptr;
    return sc;
}

// ----------------------------------------------------------------------------
// OhTransaction — minimal struct backing SurfaceControl.Transaction.mNativeObject.
// AOSP transactions accumulate ops then apply them in one IPC.  For our
// shim each op is a no-op recorder; apply is a no-op flush.
// ----------------------------------------------------------------------------
struct OhTransaction {
    int32_t magic;
    int64_t id;
    // Future: list of pending ops to apply to OH RSSurfaceNode tree.
};

constexpr int32_t kOhTransactionMagic = 0x4F485458;  // 'OHTX'
std::atomic<int64_t> g_txId{1};

OhTransaction* alloc_tx() {
    auto* tx = new OhTransaction();
    tx->magic = kOhTransactionMagic;
    tx->id = g_txId.fetch_add(1);
    return tx;
}

OhTransaction* as_tx(jlong p) {
    auto* tx = reinterpret_cast<OhTransaction*>(p);
    if (!tx || tx->magic != kOhTransactionMagic) return nullptr;
    return tx;
}

// ----------------------------------------------------------------------------
// Free functions — registered as NativeAllocationRegistry freeFunction
// (Java SurfaceControl.sNativeRegistry / sNativeTransactionRegistry).
// ----------------------------------------------------------------------------
extern "C" void SC_free(void* ptr) {
    auto* sc = reinterpret_cast<OhSurfaceControl*>(ptr);
    if (sc && sc->magic == kOhSurfaceControlMagic) {
        sc->magic = 0;  // poison so as_sc rejects use-after-free
        delete sc;
    }
}
extern "C" void TX_free(void* ptr) {
    auto* tx = reinterpret_cast<OhTransaction*>(ptr);
    if (tx && tx->magic == kOhTransactionMagic) {
        tx->magic = 0;
        delete tx;
    }
}

// ----------------------------------------------------------------------------
// Lifecycle JNI
// ----------------------------------------------------------------------------
jlong SC_nativeGetNativeSurfaceControlFinalizer(JNIEnv*, jclass) {
    return reinterpret_cast<jlong>(&SC_free);
}
jlong SC_nativeGetNativeTransactionFinalizer(JNIEnv*, jclass) {
    return reinterpret_cast<jlong>(&TX_free);
}

// AOSP signature: long nativeCreate(SurfaceSession session, String name,
//     int w, int h, int format, int flags, long parentObject, Parcel metadata)
//     throws OutOfResourcesException
jlong SC_nativeCreate(JNIEnv* env, jclass /*clazz*/,
                     jobject /*session*/, jstring nameStr,
                     jint w, jint h, jint format, jint flags,
                     jlong /*parentObject*/, jobject /*metadata*/) {
    const char* nameC = nameStr ? env->GetStringUTFChars(nameStr, nullptr) : nullptr;
    OhSurfaceControl* sc = alloc_sc(nameC);
    sc->width = w;  sc->height = h;
    sc->format = format;  sc->flags = flags;
    if (nameStr && nameC) env->ReleaseStringUTFChars(nameStr, nameC);
    ALOGI("SC.create '%s' %dx%d fmt=%d flags=0x%x ptr=%p",
          sc->name, sc->width, sc->height, sc->format, sc->flags, sc);
    return reinterpret_cast<jlong>(sc);
}

jlong SC_nativeReadFromParcel(JNIEnv* /*env*/, jclass /*clazz*/, jobject /*parcel*/) {
    return reinterpret_cast<jlong>(alloc_sc("FromParcel"));
}
jlong SC_nativeCopyFromSurfaceControl(JNIEnv*, jclass, jlong nativeObject) {
    auto* src = as_sc(nativeObject);
    if (!src) return reinterpret_cast<jlong>(alloc_sc("CopyFromInvalid"));
    auto* dst = alloc_sc(src->name);
    dst->width = src->width;  dst->height = src->height;
    dst->format = src->format;  dst->flags = src->flags;
    dst->layer = src->layer;  dst->visible = src->visible;
    return reinterpret_cast<jlong>(dst);
}
void SC_nativeWriteToParcel(JNIEnv*, jclass, jlong /*nativeObject*/, jobject /*parcel*/) { }
void SC_nativeDisconnect(JNIEnv*, jclass, jlong /*nativeObject*/) { }
void SC_nativeUpdateDefaultBufferSize(JNIEnv*, jclass, jlong nativeObject, jint w, jint h) {
    if (auto* sc = as_sc(nativeObject)) { sc->width = w;  sc->height = h; }
}
jlong SC_nativeMirrorSurface(JNIEnv*, jclass, jlong mirrorOfObject) {
    return SC_nativeCopyFromSurfaceControl(nullptr, nullptr, mirrorOfObject);
}

// ---- Transaction lifecycle ----
jlong SC_nativeCreateTransaction(JNIEnv*, jclass) {
    return reinterpret_cast<jlong>(alloc_tx());
}
jlong SC_nativeGetTransactionId(JNIEnv*, jclass, jlong txObj) {
    auto* tx = as_tx(txObj);
    return tx ? tx->id : 0;
}
// 2026-05-06 — Per design §5.6 / §9.1 condition #3: route apply() to RS flush.
//   Old: empty no-op stub (RS never sees pending property updates → layer
//        prepares without metadata → standardized to type=0 CLIENT → HDI rejects).
//   New: trigger RSTransactionProxy::FlushImplicitTransaction so the batched
//        RSCommands accumulated by SetBounds/SetAlpha/SetVisible above reach
//        RenderService.
void SC_nativeApplyTransaction(JNIEnv*, jclass, jlong /*txObj*/, jboolean /*sync*/) {
    resolve_rs_routes();
    if (g_oh_rs_flush_transaction) {
        g_oh_rs_flush_transaction();
    }
}
void SC_nativeMergeTransaction(JNIEnv*, jclass, jlong /*tx*/, jlong /*other*/) { }
void SC_nativeClearTransaction(JNIEnv*, jclass, jlong /*tx*/) { }
void SC_nativeSetAnimationTransaction(JNIEnv*, jclass, jlong /*tx*/) { }
void SC_nativeSetEarlyWakeupStart(JNIEnv*, jclass, jlong /*tx*/) { }
void SC_nativeSetEarlyWakeupEnd(JNIEnv*, jclass, jlong /*tx*/) { }

// ---- Per-SC transaction setters (all stub no-ops) ----
void SC_nativeSetLayer(JNIEnv*, jclass, jlong /*tx*/, jlong sc, jint zorder) {
    if (auto* p = as_sc(sc)) p->layer = zorder;
}
void SC_nativeSetRelativeLayer(JNIEnv*, jclass, jlong, jlong, jlong, jint) { }
// 2026-05-06 — Per design §5.6 condition #3: route position to RS.
//   x/y are in pixel coords; w/h reuse the SC's last-known size so RS gets a
//   complete bounds rect (RSNode::SetBounds takes 4 floats).
void SC_nativeSetPosition(JNIEnv*, jclass, jlong /*tx*/, jlong sc, jfloat x, jfloat y) {
    auto* p = as_sc(sc);
    if (!p || p->sessionId == 0) return;
    resolve_rs_routes();
    if (g_oh_rs_set_layer_bounds) {
        g_oh_rs_set_layer_bounds(p->sessionId, x, y,
                                 static_cast<float>(p->width),
                                 static_cast<float>(p->height));
    }
}
void SC_nativeSetScale(JNIEnv*, jclass, jlong, jlong, jfloat, jfloat) { }
void SC_nativeSetTransparentRegionHint(JNIEnv*, jclass, jlong, jlong, jobject) { }
// 2026-05-06 — Per design §5.6 condition #3: route alpha to RS.
void SC_nativeSetAlpha(JNIEnv*, jclass, jlong /*tx*/, jlong sc, jfloat alpha) {
    auto* p = as_sc(sc);
    if (!p || p->sessionId == 0) return;
    resolve_rs_routes();
    if (g_oh_rs_set_layer_alpha) {
        g_oh_rs_set_layer_alpha(p->sessionId, alpha);
    }
}
void SC_nativeSetMatrix(JNIEnv*, jclass, jlong, jlong, jfloat, jfloat, jfloat, jfloat) { }
void SC_nativeSetColorTransform(JNIEnv*, jclass, jlong, jlong, jfloatArray, jfloatArray) { }
// G2.14v r1 (2026-05-07) — design §11.4 A 类真桥（复用 oh_rs_set_layer_bounds）。
// AOSP signature: nativeSetGeometry(long tx, long sc, Rect srcRect, Rect dstRect, long rotation)
// dstRect 是 android.graphics.Rect，4 个 public int 字段（left/top/right/bottom）；
// 读出来后转 OH RSNode bounds (x, y, w, h)，srcRect/rotation 暂忽略。
void SC_nativeSetGeometry(JNIEnv* env, jclass, jlong /*tx*/, jlong sc,
        jobject /*srcRect*/, jobject dstRect, jlong /*rotation*/) {
    auto* p = as_sc(sc);
    if (!p || p->sessionId == 0 || !dstRect) return;
    jclass rectCls = env->GetObjectClass(dstRect);
    if (!rectCls) return;
    jfieldID lf = env->GetFieldID(rectCls, "left",   "I");
    jfieldID tf = env->GetFieldID(rectCls, "top",    "I");
    jfieldID rf = env->GetFieldID(rectCls, "right",  "I");
    jfieldID bf = env->GetFieldID(rectCls, "bottom", "I");
    env->DeleteLocalRef(rectCls);
    if (env->ExceptionCheck()) { env->ExceptionClear(); return; }
    if (!lf || !tf || !rf || !bf) return;
    jint l = env->GetIntField(dstRect, lf);
    jint t = env->GetIntField(dstRect, tf);
    jint r = env->GetIntField(dstRect, rf);
    jint b = env->GetIntField(dstRect, bf);
    resolve_rs_routes();
    if (g_oh_rs_set_layer_bounds) {
        g_oh_rs_set_layer_bounds(p->sessionId,
            static_cast<float>(l), static_cast<float>(t),
            static_cast<float>(r - l), static_cast<float>(b - t));
    }
}
void SC_nativeSetCrop(JNIEnv*, jclass, jlong, jlong, jint, jint, jint, jint) { }
void SC_nativeSetCornerRadius(JNIEnv*, jclass, jlong, jlong, jfloat) { }
void SC_nativeSetBackgroundBlurRadius(JNIEnv*, jclass, jlong, jlong, jint) { }
// 2026-05-11 G2.14al — bridge SurfaceControl.Transaction.setOpaque to OH RS.
// AOSP Java: SurfaceControl.Transaction.setOpaque(sc, isOpaque) →
//   nativeSetFlags(tx, sc, isOpaque ? SURFACE_OPAQUE : 0, SURFACE_OPAQUE=0x02)
// We honor only the SURFACE_OPAQUE bit here; other Surface.flags bits
// (SECURE / HIDDEN / SKIP_SCREENSHOT / ...) are not yet mapped — extend the
// switch below when ViewRootImpl / other AOSP code starts setting them.
//
// Without this bridge, OH RS sees helloworld surface with `isSurfaceBuffer
// Opaque: 0` + `OpaqueRegion [Empty]` (RS Tree dump实证 G2.14al之前) and
// composes the SCB-owned starting/leashWindow stack underneath, whose white
// shows through helloworld's transparent surface — TextView content drawn
// by hwui ends up invisible.  See doc/window_manager_ipc_adapter_design.html
// §3.5 and §3.x (TBD) for the full causal chain.
void SC_nativeSetFlags(JNIEnv*, jclass, jlong, jlong sc, jint flags, jint mask) {
    auto* p = as_sc(sc);
    if (!p) return;
    p->flags = (p->flags & ~mask) | (flags & mask);
    static constexpr jint SURFACE_OPAQUE = 0x02;
    if ((mask & SURFACE_OPAQUE) == 0) return;
    int32_t effSession = resolve_effective_session(p->sessionId);
    int isOpaque = (flags & SURFACE_OPAQUE) != 0 ? 1 : 0;
    ALOGI("[G2.14al] setOpaque sc=%p scSession=%d effSession=%d isOpaque=%d",
          p, p->sessionId, effSession, isOpaque);
    if (effSession == 0) return;
    resolve_rs_routes();
    if (g_oh_rs_set_layer_opaque) {
        g_oh_rs_set_layer_opaque(effSession, isOpaque);
    }
}
void SC_nativeSetFrameRateSelectionPriority(JNIEnv*, jclass, jlong, jlong, jint) { }
void SC_nativeSetWindowCrop(JNIEnv*, jclass, jlong, jlong, jint, jint, jint, jint) { }
void SC_nativeSetCornerRadiusF(JNIEnv*, jclass, jlong, jlong, jfloat, jfloat, jfloat, jfloat) { }
void SC_nativeSetBlurRegions(JNIEnv*, jclass, jlong, jlong, jobjectArray, jint) { }
void SC_nativeSetStretchEffect(JNIEnv*, jclass, jlong, jlong, jfloat, jfloat, jfloat, jfloat,
                                jfloat, jfloat, jfloat, jfloat, jfloat) { }
void SC_nativeSetTrustedOverlay(JNIEnv*, jclass, jlong, jlong, jboolean) { }
void SC_nativeSetDropInputMode(JNIEnv*, jclass, jlong, jlong, jint) { }
void SC_nativeSurfaceFlushJankData(JNIEnv*, jclass, jlong) { }

void SC_nativeSetBuffer(JNIEnv*, jclass, jlong, jlong, jobject, jlong, jobject) { }
void SC_nativeUnsetBuffer(JNIEnv*, jclass, jlong, jlong) { }
void SC_nativeSetBufferTransform(JNIEnv*, jclass, jlong, jlong, jint) { }
void SC_nativeSetDataSpace(JNIEnv*, jclass, jlong, jlong, jint) { }
void SC_nativeSetExtendedRangeBrightness(JNIEnv*, jclass, jlong, jlong, jfloat, jfloat) { }
void SC_nativeSetCachingHint(JNIEnv*, jclass, jlong, jlong, jint) { }
void SC_nativeSetDamageRegion(JNIEnv*, jclass, jlong, jlong, jobject) { }
void SC_nativeSetDimmingEnabled(JNIEnv*, jclass, jlong, jlong, jboolean) { }
void SC_nativeSetTrustedPresentationCallback(JNIEnv*, jclass, jlong, jlong, jobject, jobject) { }
void SC_nativeClearTrustedPresentationCallback(JNIEnv*, jclass, jlong, jlong) { }
jboolean SC_nativeClearContentFrameStats(JNIEnv*, jclass, jlong) { return JNI_TRUE; }
jboolean SC_nativeGetContentFrameStats(JNIEnv*, jclass, jlong, jobject) { return JNI_TRUE; }
jboolean SC_nativeClearAnimationFrameStats(JNIEnv*, jclass) { return JNI_TRUE; }
jboolean SC_nativeGetAnimationFrameStats(JNIEnv*, jclass, jobject) { return JNI_TRUE; }

// ---- Visibility & misc ----
// 2026-05-06 — Per design §5.6 condition #3: route visibility to RS.
void SC_nativeSetHidden(JNIEnv*, jclass, jlong /*tx*/, jlong sc, jboolean hidden) {
    auto* p = as_sc(sc);
    if (!p) return;
    p->visible = !hidden;
    if (p->sessionId == 0) return;
    resolve_rs_routes();
    if (g_oh_rs_set_layer_visible) {
        g_oh_rs_set_layer_visible(p->sessionId, p->visible ? 1 : 0);
    }
}
// 2026-05-06 — Per design §5.6 condition #3: route size change to RS bounds.
void SC_nativeSetSize(JNIEnv*, jclass, jlong /*tx*/, jlong sc, jint w, jint h) {
    auto* p = as_sc(sc);
    if (!p) return;
    p->width = w;  p->height = h;
    if (p->sessionId == 0) return;
    resolve_rs_routes();
    if (g_oh_rs_set_layer_bounds) {
        // Position not changed here; reuse 0,0 if SC has no previous position.
        // Real position is set separately via nativeSetPosition.
        g_oh_rs_set_layer_bounds(p->sessionId, 0.0f, 0.0f,
                                 static_cast<float>(w), static_cast<float>(h));
    }
}
void SC_nativeReparent(JNIEnv*, jclass, jlong, jlong, jlong) { }
void SC_nativeSetColor(JNIEnv*, jclass, jlong, jlong, jfloatArray) { }
void SC_nativeSetColorSpaceAgnostic(JNIEnv*, jclass, jlong, jlong, jboolean) { }
void SC_nativeSetShadowRadius(JNIEnv*, jclass, jlong, jlong, jfloat) { }
void SC_nativeSetTrustedOverlayBoolean(JNIEnv*, jclass, jlong, jlong, jboolean) { }

// ---- Frame timeline / sync ----
void SC_nativeSetFrameTimelineVsync(JNIEnv*, jclass, jlong, jlong) { }
void SC_nativeSetDesiredPresentTimeNanos(JNIEnv*, jclass, jlong, jlong) { }
void SC_nativeSetFrameRate(JNIEnv*, jclass, jlong, jlong, jfloat, jint, jint) { }
void SC_nativeSetDefaultFrameRateCompatibility(JNIEnv*, jclass, jlong, jlong, jint) { }
jboolean SC_nativeIsValid(JNIEnv*, jclass, jlong nativeObject) {
    return as_sc(nativeObject) != nullptr ? JNI_TRUE : JNI_FALSE;
}

// ============================================================================
// G2.14v r1 (2026-05-07) — design §11 A/B/C 三层补全 (50 new fns)
// Spec: doc/graphics_rendering_design.html#ch11
// Strategy: 全表注册止血，A 类先 stub + TODO，B 类 HiLogPrint stub，C 类 token 自管。
// ============================================================================

// ---- C class (token / lifecycle 自管) — design §11.6 -----------------------
// nativeGetHandle: assignNativeObject 把返回值存到 mNativeHandle，仅用于跨 native
// 比对，非真 IBinder。返 sc 自指针即可。
static jlong SC_nativeGetHandle(JNIEnv*, jclass, jlong nativeObject) {
    auto* p = as_sc(nativeObject);
    return p ? reinterpret_cast<jlong>(p) : 0;
}
// HelloWorld 不旋转屏幕，returning IDENTITY 安全。
static jint SC_nativeGetTransformHint(JNIEnv*, jclass, jlong /*nativeObject*/) {
    return 0; // NATIVE_WINDOW_TRANSFORM_IDENTITY
}
static jint SC_nativeGetLayerId(JNIEnv*, jclass, jlong nativeObject) {
    auto* p = as_sc(nativeObject);
    return p ? p->sessionId : 0;
}
extern "C" void TPC_free(void* /*ptr*/) { /* no-op finalizer */ }
static jlong SC_getNativeTrustedPresentationCallbackFinalizer(JNIEnv*, jclass) {
    return reinterpret_cast<jlong>(&TPC_free);
}
constexpr int32_t kOhTpcMagic = 0x4F485450; // 'OHTP'
static jlong SC_nativeCreateTpc(JNIEnv*, jclass, jobject /*callback*/) {
    auto* p = static_cast<int32_t*>(std::calloc(1, sizeof(int32_t)));
    if (p) *p = kOhTpcMagic;
    return reinterpret_cast<jlong>(p);
}
static jlong SC_nativeReadTransactionFromParcel(JNIEnv*, jclass, jobject /*parcel*/) {
    // G2.14u r2: Parcel 不跨进程，返新空 Transaction 与 nativeCreateTransaction 等价
    return reinterpret_cast<jlong>(alloc_tx());
}
static void SC_nativeWriteTransactionToParcel(JNIEnv*, jclass, jlong /*tx*/, jobject /*parcel*/) {
    /* NOP — Parcel decoupled from binder, cross-process Tx not supported */
}

// ---- A class (桥 / 占位) — design §11.4 -----------------------------------
// nativeSetLayerStack: layer→display 绑定。OH 用 RSDisplayNode::AddChild；本轮占位。
static void SC_nativeSetLayerStack(JNIEnv*, jclass, jlong /*tx*/, jlong sc, jint layerStack) {
    auto* p = as_sc(sc);
    if (!p) return;
    ALOGI("[A-stub] SetLayerStack sc=%p sessionId=%d layerStack=%d "
          "(TODO: oh_rs_attach_layer_to_display)", p, p->sessionId, layerStack);
}
// nativeSetTransformHint(sc, transform): producer 旋转 hint。OH NativeWindow 有
// 等价 set_transform，本轮占位。
static void SC_nativeSetTransformHint(JNIEnv*, jclass, jlong sc, jint transform) {
    auto* p = as_sc(sc);
    if (!p) return;
    ALOGI("[A-stub] SetTransformHint sc=%p transform=%d "
          "(TODO: oh_window_set_transform)", p, transform);
}
// nativeSetMetadata(tx, sc, key, parcel): RSNode 自定义 meta。本轮纯占位。
static void SC_nativeSetMetadata(JNIEnv*, jclass, jlong /*tx*/, jlong sc,
        jint key, jobject /*parcel*/) {
    auto* p = as_sc(sc);
    if (!p) return;
    ALOGI("[A-stub] SetMetadata sc=%p key=%d (TODO: dispatch by key to RSNode)",
          p, key);
}
// nativeAddTransactionCommittedListener(tx, listener): 注册"transaction 提交完成"回调。
// 本轮：立即 invoke listener.onCommitted() 让 ViewRootImpl 不卡 callback 等待。
static void SC_nativeAddTransactionCommittedListener(JNIEnv* env, jclass,
        jlong /*tx*/, jobject listener) {
    if (!listener) return;
    jclass cls = env->GetObjectClass(listener);
    if (!cls) return;
    jmethodID m = env->GetMethodID(cls, "onTransactionCommitted", "()V");
    env->DeleteLocalRef(cls);
    if (env->ExceptionCheck()) { env->ExceptionClear(); return; }
    if (m) {
        env->CallVoidMethod(listener, m);
        if (env->ExceptionCheck()) env->ExceptionClear();
    }
}
// nativeAddWindowInfosReportedListener(tx, listener): WMS 回调；OH 无对应。stub。
static void SC_nativeAddWindowInfosReportedListener(JNIEnv*, jclass,
        jlong /*tx*/, jobject /*listener*/) {
    ALOGI("[A-stub] AddWindowInfosReportedListener: no-op (no WMS callback)");
}
// nativeSetDestinationFrame(tx, sc, l, t, r, b): 真桥（复用 oh_rs_set_layer_bounds）
static void SC_nativeSetDestinationFrame(JNIEnv*, jclass, jlong /*tx*/, jlong sc,
        jint l, jint t, jint r, jint b) {
    auto* p = as_sc(sc);
    if (!p || p->sessionId == 0) return;
    resolve_rs_routes();
    if (g_oh_rs_set_layer_bounds) {
        g_oh_rs_set_layer_bounds(p->sessionId,
            static_cast<float>(l), static_cast<float>(t),
            static_cast<float>(r - l), static_cast<float>(b - t));
    }
}
static void SC_nativeSetFixedTransformHint(JNIEnv*, jclass, jlong /*tx*/, jlong sc, jint transform) {
    auto* p = as_sc(sc);
    if (!p) return;
    ALOGI("[A-stub] SetFixedTransformHint sc=%p transform=%d", p, transform);
}

// ---- B class (HiLog warn + safe default stub) — design §11.5 --------------
// Display 管理类（ALOGW 高优提醒——OH 上无等价物）：
static void SC_nativeSetActiveColorMode_B(JNIEnv*, jclass, jobject /*displayToken*/, jint /*mode*/) {}
static jboolean SC_nativeSetActiveColorMode(JNIEnv*, jclass, jobject, jint mode) {
    ALOGW("[B-stub] SetActiveColorMode mode=%d: no-op", mode); return JNI_FALSE;
}
static jboolean SC_nativeSetDesiredDisplayModeSpecs(JNIEnv*, jclass, jobject, jobject) {
    ALOGW("[B-stub] SetDesiredDisplayModeSpecs: no-op"); return JNI_FALSE;
}
static jboolean SC_nativeSetDisplayBrightness(JNIEnv*, jclass, jobject, jfloat, jfloat, jfloat, jfloat) {
    ALOGW("[B-stub] SetDisplayBrightness: no-op"); return JNI_FALSE;
}
static jboolean SC_nativeGetDisplayBrightnessSupport(JNIEnv*, jclass, jobject) {
    ALOGW("[B-stub] GetDisplayBrightnessSupport: false"); return JNI_FALSE;
}
static jboolean SC_nativeSetDisplayedContentSamplingEnabled(JNIEnv*, jclass, jobject, jboolean, jint, jint) {
    ALOGW("[B-stub] SetDisplayedContentSamplingEnabled: no-op"); return JNI_FALSE;
}
static jobject SC_nativeGetDisplayedContentSample(JNIEnv*, jclass, jobject, jlong, jlong) {
    ALOGW("[B-stub] GetDisplayedContentSample: null"); return nullptr;
}
static jobject SC_nativeGetDisplayDecorationSupport(JNIEnv*, jclass, jobject) {
    ALOGW("[B-stub] GetDisplayDecorationSupport: null"); return nullptr;
}
static jobject SC_nativeGetDisplayNativePrimaries(JNIEnv*, jclass, jobject) {
    ALOGW("[B-stub] GetDisplayNativePrimaries: null"); return nullptr;
}
static jobject SC_nativeGetDynamicDisplayInfo(JNIEnv*, jclass, jlong /*displayId*/) {
    ALOGW("[B-stub] GetDynamicDisplayInfo: null"); return nullptr;
}
static jobject SC_nativeGetStaticDisplayInfo(JNIEnv*, jclass, jlong /*displayId*/) {
    ALOGW("[B-stub] GetStaticDisplayInfo: null"); return nullptr;
}
// Display setters / power
static void SC_nativeSetDisplayFlags(JNIEnv*, jclass, jlong, jobject, jint flags) {
    ALOGI("[B-stub] SetDisplayFlags=0x%x", flags);
}
static void SC_nativeSetDisplayLayerStack(JNIEnv*, jclass, jlong, jobject, jint layerStack) {
    ALOGI("[B-stub] SetDisplayLayerStack=%d", layerStack);
}
static void SC_nativeSetDisplayPowerMode(JNIEnv*, jclass, jobject, jint mode) {
    ALOGW("[B-stub] SetDisplayPowerMode=%d", mode);
}
static void SC_nativeSetDisplayProjection(JNIEnv*, jclass, jlong, jobject,
        jint, jint, jint, jint, jint, jint, jint, jint, jint, jint) {
    ALOGI("[B-stub] SetDisplayProjection");
}
static void SC_nativeSetDisplaySize(JNIEnv*, jclass, jlong, jobject, jint w, jint h) {
    ALOGI("[B-stub] SetDisplaySize %dx%d", w, h);
}
static void SC_nativeSetDisplaySurface(JNIEnv*, jclass, jlong, jobject, jlong nativeSurfaceObject) {
    ALOGI("[B-stub] SetDisplaySurface ptr=0x%lx", static_cast<long>(nativeSurfaceObject));
}
// Boot display mode
static void SC_nativeSetBootDisplayMode(JNIEnv*, jclass, jobject, jint mode) {
    ALOGW("[B-stub] SetBootDisplayMode=%d", mode);
}
static void SC_nativeClearBootDisplayMode(JNIEnv*, jclass, jobject) {
    ALOGW("[B-stub] ClearBootDisplayMode");
}
static jboolean SC_nativeGetBootDisplayModeSupport(JNIEnv*, jclass) {
    ALOGW("[B-stub] GetBootDisplayModeSupport: false"); return JNI_FALSE;
}
// Game / TV
static void SC_nativeSetGameContentType(JNIEnv*, jclass, jobject, jboolean on) {
    ALOGW("[B-stub] SetGameContentType=%d", on ? 1 : 0);
}
static void SC_nativeSetAutoLowLatencyMode(JNIEnv*, jclass, jobject, jboolean on) {
    ALOGW("[B-stub] SetAutoLowLatencyMode=%d", on ? 1 : 0);
}
// Input / focus
static void SC_nativeSetFocusedWindow(JNIEnv*, jclass, jlong /*tx*/, jobject /*toToken*/,
        jstring /*windowName*/, jint /*displayId*/) {
    ALOGI("[B-stub] SetFocusedWindow: no-op (input focus not adapter-managed)");
}
static void SC_nativeSetInputWindowInfo(JNIEnv*, jclass, jlong, jlong sc, jobject /*handle*/) {
    auto* p = as_sc(sc);
    ALOGI("[B-stub] SetInputWindowInfo sc=%p", p);
}
static void SC_nativeRemoveCurrentInputFocus(JNIEnv*, jclass, jlong, jint displayId) {
    ALOGI("[B-stub] RemoveCurrentInputFocus displayId=%d", displayId);
}
// Sanitize / global
static void SC_nativeSanitize(JNIEnv*, jclass, jlong, jint pid, jint uid) {
    ALOGI("[B-stub] Sanitize pid=%d uid=%d", pid, uid);
}
static void SC_nativeSetGlobalShadowSettings(JNIEnv*, jclass,
        jfloatArray /*ambient*/, jfloatArray /*spot*/, jfloat /*lx*/, jfloat /*ly*/, jfloat /*lz*/) {
    ALOGI("[B-stub] SetGlobalShadowSettings: no-op");
}
static jboolean SC_nativeBootFinished(JNIEnv*, jclass) {
    ALOGI("[B-stub] BootFinished: true (no SF, always 'booted')"); return JNI_TRUE;
}
// Stats / Jank
static void SC_nativeAddJankDataListener(JNIEnv*, jclass, jlong /*listener*/, jlong /*sc*/) {
    /* no-op */
}
static void SC_nativeRemoveJankDataListener(JNIEnv*, jclass, jlong /*listener*/) { /* no-op */ }
static jlong SC_nativeCreateJankDataListenerWrapper(JNIEnv*, jclass, jobject /*listener*/) {
    return 0; // null-token; caller uses it as opaque handle
}
// Misc queries
static jint SC_nativeGetGPUContextPriority(JNIEnv*, jclass) {
    ALOGW("[B-stub] GetGPUContextPriority: 0"); return 0;
}
static jobject SC_nativeGetOverlaySupport(JNIEnv*, jclass) {
    ALOGW("[B-stub] GetOverlaySupport: null"); return nullptr;
}
static jboolean SC_nativeGetProtectedContentSupport(JNIEnv*, jclass) {
    ALOGW("[B-stub] GetProtectedContentSupport: false"); return JNI_FALSE;
}
static jintArray SC_nativeGetCompositionDataspaces(JNIEnv* env, jclass) {
    ALOGW("[B-stub] GetCompositionDataspaces: empty array");
    return env->NewIntArray(0);
}
static jobject SC_nativeGetDefaultApplyToken(JNIEnv*, jclass) {
    ALOGW("[B-stub] GetDefaultApplyToken: null"); return nullptr;
}
static void SC_nativeSetDefaultApplyToken(JNIEnv*, jclass, jobject /*token*/) {
    ALOGW("[B-stub] SetDefaultApplyToken: no-op");
}

// ----------------------------------------------------------------------------
// Method table (107 entries — full AOSP 14 SurfaceControl native surface)
// Design ref: doc/graphics_rendering_design.html#ch11-7
// ----------------------------------------------------------------------------
const JNINativeMethod kSurfaceControlMethods[] = {
    // ─── Lifecycle (32 already covered) ───
    { "nativeGetNativeSurfaceControlFinalizer", "()J",
      reinterpret_cast<void*>(SC_nativeGetNativeSurfaceControlFinalizer) },
    { "nativeGetNativeTransactionFinalizer", "()J",
      reinterpret_cast<void*>(SC_nativeGetNativeTransactionFinalizer) },
    { "nativeCreate",
      "(Landroid/view/SurfaceSession;Ljava/lang/String;IIIIJLandroid/os/Parcel;)J",
      reinterpret_cast<void*>(SC_nativeCreate) },
    { "nativeReadFromParcel", "(Landroid/os/Parcel;)J",
      reinterpret_cast<void*>(SC_nativeReadFromParcel) },
    { "nativeCopyFromSurfaceControl", "(J)J",
      reinterpret_cast<void*>(SC_nativeCopyFromSurfaceControl) },
    { "nativeWriteToParcel", "(JLandroid/os/Parcel;)V",
      reinterpret_cast<void*>(SC_nativeWriteToParcel) },
    { "nativeDisconnect", "(J)V",
      reinterpret_cast<void*>(SC_nativeDisconnect) },
    { "nativeUpdateDefaultBufferSize", "(JII)V",
      reinterpret_cast<void*>(SC_nativeUpdateDefaultBufferSize) },
    { "nativeMirrorSurface", "(J)J",
      reinterpret_cast<void*>(SC_nativeMirrorSurface) },

    // ─── Transaction lifecycle ───
    { "nativeCreateTransaction", "()J",
      reinterpret_cast<void*>(SC_nativeCreateTransaction) },
    { "nativeGetTransactionId", "(J)J",
      reinterpret_cast<void*>(SC_nativeGetTransactionId) },
    { "nativeApplyTransaction", "(JZ)V",
      reinterpret_cast<void*>(SC_nativeApplyTransaction) },
    { "nativeMergeTransaction", "(JJ)V",
      reinterpret_cast<void*>(SC_nativeMergeTransaction) },
    { "nativeClearTransaction", "(J)V",
      reinterpret_cast<void*>(SC_nativeClearTransaction) },
    { "nativeSetAnimationTransaction", "(J)V",
      reinterpret_cast<void*>(SC_nativeSetAnimationTransaction) },
    { "nativeSetEarlyWakeupStart", "(J)V",
      reinterpret_cast<void*>(SC_nativeSetEarlyWakeupStart) },
    { "nativeSetEarlyWakeupEnd", "(J)V",
      reinterpret_cast<void*>(SC_nativeSetEarlyWakeupEnd) },

    // ─── Common transaction setters (most-frequently called by ViewRootImpl) ───
    { "nativeSetLayer", "(JJI)V",
      reinterpret_cast<void*>(SC_nativeSetLayer) },
    { "nativeSetPosition", "(JJFF)V",
      reinterpret_cast<void*>(SC_nativeSetPosition) },
    { "nativeSetScale", "(JJFF)V",
      reinterpret_cast<void*>(SC_nativeSetScale) },
    { "nativeSetAlpha", "(JJF)V",
      reinterpret_cast<void*>(SC_nativeSetAlpha) },
    { "nativeSetMatrix", "(JJFFFF)V",
      reinterpret_cast<void*>(SC_nativeSetMatrix) },
    { "nativeSetCornerRadius", "(JJF)V",
      reinterpret_cast<void*>(SC_nativeSetCornerRadius) },
    { "nativeSetBackgroundBlurRadius", "(JJI)V",
      reinterpret_cast<void*>(SC_nativeSetBackgroundBlurRadius) },
    { "nativeSetFlags", "(JJII)V",
      reinterpret_cast<void*>(SC_nativeSetFlags) },
    { "nativeSetWindowCrop", "(JJIIII)V",
      reinterpret_cast<void*>(SC_nativeSetWindowCrop) },
    { "nativeReparent", "(JJJ)V",
      reinterpret_cast<void*>(SC_nativeReparent) },
    { "nativeSetColor", "(JJ[F)V",
      reinterpret_cast<void*>(SC_nativeSetColor) },
    { "nativeSetShadowRadius", "(JJF)V",
      reinterpret_cast<void*>(SC_nativeSetShadowRadius) },
    { "nativeSetFrameRate", "(JJFII)V",
      reinterpret_cast<void*>(SC_nativeSetFrameRate) },
    { "nativeSetTrustedOverlay", "(JJZ)V",
      reinterpret_cast<void*>(SC_nativeSetTrustedOverlay) },
    { "nativeSurfaceFlushJankData", "(J)V",
      reinterpret_cast<void*>(SC_nativeSurfaceFlushJankData) },

    // ─── Orphan fns now wired (25) — design §11.7 #35-59 ───
    { "nativeSetGeometry", "(JJLandroid/graphics/Rect;Landroid/graphics/Rect;J)V",
      reinterpret_cast<void*>(SC_nativeSetGeometry) },
    { "nativeSetRelativeLayer", "(JJJI)V",
      reinterpret_cast<void*>(SC_nativeSetRelativeLayer) },
    { "nativeSetTransparentRegionHint", "(JJLandroid/graphics/Region;)V",
      reinterpret_cast<void*>(SC_nativeSetTransparentRegionHint) },
    { "nativeSetColorTransform", "(JJ[F[F)V",
      reinterpret_cast<void*>(SC_nativeSetColorTransform) },
    { "nativeSetColorSpaceAgnostic", "(JJZ)V",
      reinterpret_cast<void*>(SC_nativeSetColorSpaceAgnostic) },
    { "nativeSetBlurRegions", "(JJ[[FI)V",
      reinterpret_cast<void*>(SC_nativeSetBlurRegions) },
    { "nativeSetStretchEffect", "(JJFFFFFFFFFF)V",
      reinterpret_cast<void*>(SC_nativeSetStretchEffect) },
    { "nativeSetFrameRateSelectionPriority", "(JJI)V",
      reinterpret_cast<void*>(SC_nativeSetFrameRateSelectionPriority) },
    { "nativeSetFrameTimelineVsync", "(JJ)V",
      reinterpret_cast<void*>(SC_nativeSetFrameTimelineVsync) },
    { "nativeSetDefaultFrameRateCompatibility", "(JJI)V",
      reinterpret_cast<void*>(SC_nativeSetDefaultFrameRateCompatibility) },
    { "nativeSetBuffer",
      "(JJLandroid/hardware/HardwareBuffer;JLjava/util/function/Consumer;)V",
      reinterpret_cast<void*>(SC_nativeSetBuffer) },
    { "nativeUnsetBuffer", "(JJ)V",
      reinterpret_cast<void*>(SC_nativeUnsetBuffer) },
    { "nativeSetBufferTransform", "(JJI)V",
      reinterpret_cast<void*>(SC_nativeSetBufferTransform) },
    { "nativeSetDataSpace", "(JJI)V",
      reinterpret_cast<void*>(SC_nativeSetDataSpace) },
    { "nativeSetExtendedRangeBrightness", "(JJFF)V",
      reinterpret_cast<void*>(SC_nativeSetExtendedRangeBrightness) },
    { "nativeSetCachingHint", "(JJI)V",
      reinterpret_cast<void*>(SC_nativeSetCachingHint) },
    { "nativeSetDamageRegion", "(JJLandroid/graphics/Region;)V",
      reinterpret_cast<void*>(SC_nativeSetDamageRegion) },
    { "nativeSetDimmingEnabled", "(JJZ)V",
      reinterpret_cast<void*>(SC_nativeSetDimmingEnabled) },
    { "nativeSetTrustedPresentationCallback",
      "(JJJLandroid/view/SurfaceControl$TrustedPresentationThresholds;)V",
      reinterpret_cast<void*>(SC_nativeSetTrustedPresentationCallback) },
    { "nativeClearTrustedPresentationCallback", "(JJ)V",
      reinterpret_cast<void*>(SC_nativeClearTrustedPresentationCallback) },
    { "nativeClearContentFrameStats", "(J)Z",
      reinterpret_cast<void*>(SC_nativeClearContentFrameStats) },
    { "nativeGetContentFrameStats", "(JLandroid/view/WindowContentFrameStats;)Z",
      reinterpret_cast<void*>(SC_nativeGetContentFrameStats) },
    { "nativeClearAnimationFrameStats", "()Z",
      reinterpret_cast<void*>(SC_nativeClearAnimationFrameStats) },
    { "nativeGetAnimationFrameStats", "(Landroid/view/WindowAnimationFrameStats;)Z",
      reinterpret_cast<void*>(SC_nativeGetAnimationFrameStats) },
    { "nativeSetDropInputMode", "(JJI)V",
      reinterpret_cast<void*>(SC_nativeSetDropInputMode) },

    // ─── New: C class (token / lifecycle 自管) — design §11.6 ───
    { "nativeGetHandle", "(J)J",
      reinterpret_cast<void*>(SC_nativeGetHandle) },
    { "nativeGetTransformHint", "(J)I",
      reinterpret_cast<void*>(SC_nativeGetTransformHint) },
    { "nativeGetLayerId", "(J)I",
      reinterpret_cast<void*>(SC_nativeGetLayerId) },
    { "getNativeTrustedPresentationCallbackFinalizer", "()J",
      reinterpret_cast<void*>(SC_getNativeTrustedPresentationCallbackFinalizer) },
    { "nativeCreateTpc",
      "(Landroid/view/SurfaceControl$TrustedPresentationCallback;)J",
      reinterpret_cast<void*>(SC_nativeCreateTpc) },
    { "nativeReadTransactionFromParcel", "(Landroid/os/Parcel;)J",
      reinterpret_cast<void*>(SC_nativeReadTransactionFromParcel) },
    { "nativeWriteTransactionToParcel", "(JLandroid/os/Parcel;)V",
      reinterpret_cast<void*>(SC_nativeWriteTransactionToParcel) },

    // ─── New: A class (桥候选 / 占位) — design §11.4 ───
    { "nativeSetLayerStack", "(JJI)V",
      reinterpret_cast<void*>(SC_nativeSetLayerStack) },
    { "nativeSetTransformHint", "(JI)V",
      reinterpret_cast<void*>(SC_nativeSetTransformHint) },
    { "nativeSetMetadata", "(JJILandroid/os/Parcel;)V",
      reinterpret_cast<void*>(SC_nativeSetMetadata) },
    { "nativeAddTransactionCommittedListener",
      "(JLandroid/view/SurfaceControl$TransactionCommittedListener;)V",
      reinterpret_cast<void*>(SC_nativeAddTransactionCommittedListener) },
    { "nativeAddWindowInfosReportedListener", "(JLjava/lang/Runnable;)V",
      reinterpret_cast<void*>(SC_nativeAddWindowInfosReportedListener) },
    { "nativeSetDestinationFrame", "(JJIIII)V",
      reinterpret_cast<void*>(SC_nativeSetDestinationFrame) },
    { "nativeSetFixedTransformHint", "(JJI)V",
      reinterpret_cast<void*>(SC_nativeSetFixedTransformHint) },

    // ─── New: B class (HiLog warn + safe default stub) — design §11.5 ───
    { "nativeAddJankDataListener", "(JJ)V",
      reinterpret_cast<void*>(SC_nativeAddJankDataListener) },
    { "nativeRemoveJankDataListener", "(J)V",
      reinterpret_cast<void*>(SC_nativeRemoveJankDataListener) },
    { "nativeCreateJankDataListenerWrapper",
      "(Landroid/view/SurfaceControl$OnJankDataListener;)J",
      reinterpret_cast<void*>(SC_nativeCreateJankDataListenerWrapper) },
    { "nativeSanitize", "(JII)V",
      reinterpret_cast<void*>(SC_nativeSanitize) },
    { "nativeSetFocusedWindow",
      "(JLandroid/os/IBinder;Ljava/lang/String;I)V",
      reinterpret_cast<void*>(SC_nativeSetFocusedWindow) },
    { "nativeSetInputWindowInfo",
      "(JJLandroid/view/InputWindowHandle;)V",
      reinterpret_cast<void*>(SC_nativeSetInputWindowInfo) },
    { "nativeSetGameContentType", "(Landroid/os/IBinder;Z)V",
      reinterpret_cast<void*>(SC_nativeSetGameContentType) },
    { "nativeSetAutoLowLatencyMode", "(Landroid/os/IBinder;Z)V",
      reinterpret_cast<void*>(SC_nativeSetAutoLowLatencyMode) },
    { "nativeSetBootDisplayMode", "(Landroid/os/IBinder;I)V",
      reinterpret_cast<void*>(SC_nativeSetBootDisplayMode) },
    { "nativeClearBootDisplayMode", "(Landroid/os/IBinder;)V",
      reinterpret_cast<void*>(SC_nativeClearBootDisplayMode) },
    { "nativeGetBootDisplayModeSupport", "()Z",
      reinterpret_cast<void*>(SC_nativeGetBootDisplayModeSupport) },
    { "nativeSetActiveColorMode", "(Landroid/os/IBinder;I)Z",
      reinterpret_cast<void*>(SC_nativeSetActiveColorMode) },
    { "nativeSetDesiredDisplayModeSpecs",
      "(Landroid/os/IBinder;Landroid/view/SurfaceControl$DesiredDisplayModeSpecs;)Z",
      reinterpret_cast<void*>(SC_nativeSetDesiredDisplayModeSpecs) },
    { "nativeSetDisplayBrightness", "(Landroid/os/IBinder;FFFF)Z",
      reinterpret_cast<void*>(SC_nativeSetDisplayBrightness) },
    { "nativeGetDisplayBrightnessSupport", "(Landroid/os/IBinder;)Z",
      reinterpret_cast<void*>(SC_nativeGetDisplayBrightnessSupport) },
    { "nativeSetDisplayedContentSamplingEnabled",
      "(Landroid/os/IBinder;ZII)Z",
      reinterpret_cast<void*>(SC_nativeSetDisplayedContentSamplingEnabled) },
    { "nativeGetDisplayedContentSample",
      "(Landroid/os/IBinder;JJ)Landroid/hardware/display/DisplayedContentSample;",
      reinterpret_cast<void*>(SC_nativeGetDisplayedContentSample) },
    { "nativeGetDisplayDecorationSupport",
      "(Landroid/os/IBinder;)Landroid/hardware/graphics/common/DisplayDecorationSupport;",
      reinterpret_cast<void*>(SC_nativeGetDisplayDecorationSupport) },
    { "nativeGetDisplayNativePrimaries",
      "(Landroid/os/IBinder;)Landroid/view/SurfaceControl$DisplayPrimaries;",
      reinterpret_cast<void*>(SC_nativeGetDisplayNativePrimaries) },
    { "nativeGetDynamicDisplayInfo",
      "(J)Landroid/view/SurfaceControl$DynamicDisplayInfo;",
      reinterpret_cast<void*>(SC_nativeGetDynamicDisplayInfo) },
    { "nativeGetStaticDisplayInfo",
      "(J)Landroid/view/SurfaceControl$StaticDisplayInfo;",
      reinterpret_cast<void*>(SC_nativeGetStaticDisplayInfo) },
    { "nativeGetGPUContextPriority", "()I",
      reinterpret_cast<void*>(SC_nativeGetGPUContextPriority) },
    { "nativeGetOverlaySupport",
      "()Landroid/hardware/OverlayProperties;",
      reinterpret_cast<void*>(SC_nativeGetOverlaySupport) },
    { "nativeGetProtectedContentSupport", "()Z",
      reinterpret_cast<void*>(SC_nativeGetProtectedContentSupport) },
    { "nativeGetCompositionDataspaces", "()[I",
      reinterpret_cast<void*>(SC_nativeGetCompositionDataspaces) },
    { "nativeGetDefaultApplyToken", "()Landroid/os/IBinder;",
      reinterpret_cast<void*>(SC_nativeGetDefaultApplyToken) },
    { "nativeSetDefaultApplyToken", "(Landroid/os/IBinder;)V",
      reinterpret_cast<void*>(SC_nativeSetDefaultApplyToken) },
    { "nativeSetDisplayFlags", "(JLandroid/os/IBinder;I)V",
      reinterpret_cast<void*>(SC_nativeSetDisplayFlags) },
    { "nativeSetDisplayLayerStack", "(JLandroid/os/IBinder;I)V",
      reinterpret_cast<void*>(SC_nativeSetDisplayLayerStack) },
    { "nativeSetDisplayPowerMode", "(Landroid/os/IBinder;I)V",
      reinterpret_cast<void*>(SC_nativeSetDisplayPowerMode) },
    { "nativeSetDisplayProjection",
      "(JLandroid/os/IBinder;IIIIIIIII)V",
      reinterpret_cast<void*>(SC_nativeSetDisplayProjection) },
    { "nativeSetDisplaySize", "(JLandroid/os/IBinder;II)V",
      reinterpret_cast<void*>(SC_nativeSetDisplaySize) },
    { "nativeSetDisplaySurface", "(JLandroid/os/IBinder;J)V",
      reinterpret_cast<void*>(SC_nativeSetDisplaySurface) },
    { "nativeRemoveCurrentInputFocus", "(JI)V",
      reinterpret_cast<void*>(SC_nativeRemoveCurrentInputFocus) },
    { "nativeSetGlobalShadowSettings", "([F[FFFF)V",
      reinterpret_cast<void*>(SC_nativeSetGlobalShadowSettings) },
    { "nativeBootFinished", "()Z",
      reinterpret_cast<void*>(SC_nativeBootFinished) },
};

}  // namespace

int register_android_view_SurfaceControl(JNIEnv* env) {
    jclass clazz = env->FindClass("android/view/SurfaceControl");
    if (!clazz) {
        ALOGW("register_android_view_SurfaceControl: FindClass failed");
        if (env->ExceptionCheck()) env->ExceptionClear();
        return -1;
    }
    // Register one-by-one so a single bad signature doesn't sink the rest.
    // AOSP RegisterNatives is all-or-nothing on the input array; if any entry's
    // name+signature doesn't match a declared native method on the class,
    // the whole call returns JNI_ERR.  Method-at-a-time registration limits
    // the blast radius — Hello World only needs nativeCreate +
    // GetNativeSurfaceControlFinalizer to be wired for setContentView's
    // SurfaceControl.Builder().build() path to succeed.
    constexpr size_t kCount = sizeof(kSurfaceControlMethods)
                             / sizeof(kSurfaceControlMethods[0]);
    int ok = 0, fail = 0;
    for (size_t i = 0; i < kCount; ++i) {
        jint rc = env->RegisterNatives(clazz, &kSurfaceControlMethods[i], 1);
        if (rc == JNI_OK) {
            ++ok;
        } else {
            ++fail;
            ALOGW("register_android_view_SurfaceControl: failed to register %s%s",
                  kSurfaceControlMethods[i].name,
                  kSurfaceControlMethods[i].signature);
            if (env->ExceptionCheck()) env->ExceptionClear();
        }
    }
    env->DeleteLocalRef(clazz);
    ALOGI("register_android_view_SurfaceControl: registered %d/%zu methods (%d failed)",
          ok, kCount, fail);
    // Return success so caller's RegisterAll loop continues to the next module.
    return 0;
}

// 2026-05-02 G2.14r: out-of-namespace C exports for cross-translation-unit use.
//
// adapter_bridge / window adapter / compat_shim need to (a) attach a WSAdapter
// session ID to a Java SurfaceControl's native object, and (b) read it back
// from a SurfaceControl pointer (e.g., during BLASTBufferQueue.update).  These
// helpers are stable C ABI to avoid C++ name mangling issues and to allow
// dlsym lookup from any adapter .so.
extern "C" __attribute__((visibility("default")))
int oh_sc_attach_session(jlong scNativeObject, int32_t sessionId) {
    using namespace android;
    auto* sc = reinterpret_cast<OhSurfaceControl*>(scNativeObject);
    if (!sc || sc->magic != kOhSurfaceControlMagic) {
        return -1;
    }
    sc->sessionId = sessionId;
    HiLogPrint(3, 4, 0xD000F00u, "OH_SurfaceControl",
               "attach_session: sc=%p sessionId=%d", (void*)sc, sessionId);
    return 0;
}

extern "C" __attribute__((visibility("default")))
int32_t oh_sc_get_session(jlong scNativeObject) {
    using namespace android;
    auto* sc = reinterpret_cast<OhSurfaceControl*>(scNativeObject);
    if (!sc || sc->magic != kOhSurfaceControlMagic) {
        return 0;
    }
    return sc->sessionId;
}

// ============================================================
// 2026-05-08 G2.14aa: ASurfaceControl / ASurfaceTransaction NDK 9 个符号真桥
//
// hwui RenderThread.cpp ASurfaceControlFunctions ctor:
//   handle_ = dlopen("libandroid.so", RTLD_NOW | RTLD_NODELETE);  // → liboh_android_runtime.so
//   createFunc = dlsym(handle_, "ASurfaceControl_create");
//   LOG_ALWAYS_FATAL_IF(createFunc == nullptr, ...);
//   ... 8 more dlsym + FATAL_IF ...
//
// 这 9 个 NDK 符号必须 export，否则 hwui SIGABRT。
//
// 真桥到 OH RS（SurfaceFlinger 等价）：
//   ASurfaceControl     ↔ RSSurfaceNode (sptr<>)
//   ASurfaceTransaction ↔ RSTransaction（OH 用隐式 transaction via RSTransactionProxy）
//
// 复用 resolve_rs_routes() 已有的 dlsym(RTLD_DEFAULT, "oh_rs_*") 机制。
// 新增的 helper 在 framework/window/jni/oh_window_manager_client.cpp:
//   oh_rs_create_subsurface(name) → RSSurfaceNode::Create(cfg, isWindow=false) holder
//   oh_rs_destroy_subsurface(holder)
//   oh_rs_register_buffer_listener(holder, cb, ctx) → RSSurfaceNode::RegisterBufferAvailableListener
//   oh_rs_flush_transaction() → RSTransaction::FlushImplicitTransaction (现有)
// ============================================================

namespace {
using FnCreateSub  = void* (*)(const char* /*name*/);
using FnDestroySub = void (*)(void* /*holder*/);
using FnRegListener = void (*)(void* /*holder*/,
                                void (*cb)(void* /*ctx*/, int32_t /*ctlFd*/, void* /*stats*/),
                                void* /*ctx*/);

FnCreateSub   g_oh_rs_create_subsurface     = nullptr;
FnDestroySub  g_oh_rs_destroy_subsurface    = nullptr;
FnRegListener g_oh_rs_register_buf_listener = nullptr;
std::once_flag g_asurface_route_once;

void resolve_asurface_routes() {
    std::call_once(g_asurface_route_once, []() {
        g_oh_rs_create_subsurface     = reinterpret_cast<FnCreateSub>(
            dlsym(RTLD_DEFAULT, "oh_rs_create_subsurface"));
        g_oh_rs_destroy_subsurface    = reinterpret_cast<FnDestroySub>(
            dlsym(RTLD_DEFAULT, "oh_rs_destroy_subsurface"));
        g_oh_rs_register_buf_listener = reinterpret_cast<FnRegListener>(
            dlsym(RTLD_DEFAULT, "oh_rs_register_buffer_listener"));
    });
}

// ASurfaceControl wrapper：内部存 RS holder（指向 sptr<RSSurfaceNode>）。
// hwui 拿到 ASurfaceControl* 当不透明指针，从不解引用内容。
struct AdapterASurfaceControl {
    void* rsHolder;       // oh_rs_create_subsurface 返回的 holder（shared_ptr<RSSurfaceNode>*）
    int32_t refcount;     // ASurfaceControl_acquire/_release 维护
    bool ownsHolder;      // true = release 时调 oh_rs_destroy_subsurface
};

struct AdapterASurfaceTransaction {
    int32_t pendingOps;   // 当前 transaction 内积累的 op 数（仅诊断；OH 是隐式 transaction）
};

}  // namespace

extern "C" __attribute__((visibility("default")))
void* ASurfaceControl_create(void* /*parent — hwui 传 RootSurfaceControl，OH 隐式挂 default*/,
                              const char* debugName) {
    resolve_asurface_routes();
    if (!g_oh_rs_create_subsurface) {
        ALOGW("ASurfaceControl_create: oh_rs_create_subsurface not resolved (liboh_adapter_bridge.so missing)");
        return nullptr;
    }
    void* holder = g_oh_rs_create_subsurface(debugName);
    if (!holder) {
        ALOGW("ASurfaceControl_create: oh_rs_create_subsurface returned null for %s", debugName ? debugName : "");
        return nullptr;
    }
    auto* sc = new AdapterASurfaceControl{ holder, /*refcount*/1, /*ownsHolder*/true };
    return sc;
}

extern "C" __attribute__((visibility("default")))
void ASurfaceControl_acquire(void* surfaceControl) {
    auto* sc = reinterpret_cast<AdapterASurfaceControl*>(surfaceControl);
    if (!sc) return;
    __atomic_add_fetch(&sc->refcount, 1, __ATOMIC_RELAXED);
}

extern "C" __attribute__((visibility("default")))
void ASurfaceControl_release(void* surfaceControl) {
    auto* sc = reinterpret_cast<AdapterASurfaceControl*>(surfaceControl);
    if (!sc) return;
    if (__atomic_sub_fetch(&sc->refcount, 1, __ATOMIC_RELAXED) == 0) {
        if (sc->ownsHolder && sc->rsHolder && g_oh_rs_destroy_subsurface) {
            g_oh_rs_destroy_subsurface(sc->rsHolder);
        }
        delete sc;
    }
}

extern "C" __attribute__((visibility("default")))
void ASurfaceControl_registerSurfaceStatsListener(
        void* surfaceControl, int32_t /*id*/, void* context, void* func_void) {
    auto* sc = reinterpret_cast<AdapterASurfaceControl*>(surfaceControl);
    if (!sc || !sc->rsHolder || !g_oh_rs_register_buf_listener) return;
    auto cb = reinterpret_cast<void(*)(void*, int32_t, void*)>(func_void);
    if (!cb) return;
    g_oh_rs_register_buf_listener(sc->rsHolder, cb, context);
}

extern "C" __attribute__((visibility("default")))
void ASurfaceControl_unregisterSurfaceStatsListener(void* /*context*/, void* /*func*/) {
    // OH RSSurfaceNode::RegisterBufferAvailableListener 没暴露 unregister 接口
    // (一次注册直到 RSSurfaceNode 销毁)。adapter 暂留 no-op；待 OH 暴露后真桥。
    // helloworld 路径 hwui 的 unregister 时机在窗口销毁时，与 ASurfaceControl_release
    // 同时发生，holder 销毁会自动断开 listener。
}

extern "C" __attribute__((visibility("default")))
int64_t ASurfaceControlStats_getAcquireTime(void* /*stats*/) {
    // OH 暂无对应 buffer-acquire-time metric (P2 — 桥到 OH HiSysEvent / RSFrameStats)。
    // hwui CanvasContext 用此做诊断，0 表示 "stats 不可用"，hwui 处理安全。
    return 0;
}

extern "C" __attribute__((visibility("default")))
uint64_t ASurfaceControlStats_getFrameNumber(void* /*stats*/) {
    return 0;  // 同上
}

extern "C" __attribute__((visibility("default")))
void* ASurfaceTransaction_create() {
    return new AdapterASurfaceTransaction{ /*pendingOps*/0 };
}

extern "C" __attribute__((visibility("default")))
void ASurfaceTransaction_delete(void* transaction) {
    delete reinterpret_cast<AdapterASurfaceTransaction*>(transaction);
}

extern "C" __attribute__((visibility("default")))
void ASurfaceTransaction_apply(void* transaction) {
    // 真桥：FlushImplicitTransaction 提交 batched RSCommands 到 RenderService。
    // OH 是隐式 transaction，不区分多个 transaction object — 所有 RS op 自动批量。
    using namespace android;
    resolve_rs_routes();
    if (g_oh_rs_flush_transaction) {
        g_oh_rs_flush_transaction();
    }
    if (transaction) {
        reinterpret_cast<AdapterASurfaceTransaction*>(transaction)->pendingOps = 0;
    }
}

// ---- 3 个 transaction setter（hwui WebView 用，helloworld 主路径不真触发） ----
// reparent 在 OH RS 没直接对应（OH RSSurfaceNode 不允许动态 re-parent）；
// setVisibility 与 setZOrder 已有 oh_rs_set_layer_visible / SetLayer 等价路径，
// 但通过 sessionId 索引（与 ASurfaceControl* holder 不同）。Phase 1 暂 no-op
// 确保 dlsym 通过；Phase 2 (P2) 桥到 RSSurfaceNode 的 SetVisibility / SetPositionZ。

extern "C" __attribute__((visibility("default")))
void ASurfaceTransaction_reparent(void* /*transaction*/,
                                   void* /*surfaceControl*/,
                                   void* /*newParent*/) {
    // OH 没有运行时 re-parent；P2 桥到 OH RSNode::AddChild/RemoveChild。
}

extern "C" __attribute__((visibility("default")))
void ASurfaceTransaction_setVisibility(void* /*transaction*/,
                                        void* /*surfaceControl*/,
                                        int8_t /*visibility*/) {
    // P2 桥到 RSSurfaceNode::SetVisible(holder, visibility != 0)。
}

extern "C" __attribute__((visibility("default")))
void ASurfaceTransaction_setZOrder(void* /*transaction*/,
                                    void* /*surfaceControl*/,
                                    int32_t /*z*/) {
    // P2 桥到 RSSurfaceNode::SetPositionZ(holder, z)。
}

}  // namespace android
