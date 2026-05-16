/*
 * oh_window_manager_client.h
 *
 * OpenHarmony WindowManager / SceneSessionManager IPC client.
 * Manages window creation, layout, and surface allocation for the adapter layer.
 *
 * OH WMS has two system services:
 *   - SA 4606: WindowManagerService (IWindowManager, legacy interface)
 *   - SA 4607: SceneSessionManager (ISceneSessionManager extends IWindowManager)
 *
 * ISceneSessionManager is a superset of IWindowManager. On modern OH with
 * SceneBoard enabled, only SA 4607 is needed. This adapter connects to SA 4607.
 *
 * Architecture:
 *   ISceneSessionManager (SA 4607) creates sessions via CreateAndConnectSpecificSession().
 *   ISession (per-window proxy, returned by creation) handles all per-window operations.
 *   SSM.CreateAndConnect() -> ISession.UpdateSessionRect() -> ISession.DrawingCompleted()
 */
#ifndef OH_WINDOW_MANAGER_CLIENT_H
#define OH_WINDOW_MANAGER_CLIENT_H

#include <jni.h>
#include <memory>
#include <string>
#include <mutex>
#include "iremote_object.h"
#include "session_interface.h"
#include "refbase.h"        // OHOS::sptr<>
namespace OHOS { class Surface; }   // 2026-05-08 G2.14ab forward decl

// Forward declare RSSurfaceNode in OHOS::Rosen so we can hold a shared_ptr
// without pulling in render_service_client/core/ui/rs_surface_node.h here.
namespace OHOS::Rosen {
class RSSurfaceNode;
}

namespace oh_adapter {

class SessionStageAdapter;
class WindowCallbackAdapter;
class WindowEventChannelAdapter;

/**
 * Window session info returned after creating a session with OH SceneSessionManager.
 */
struct OHWindowSession {
    int32_t sessionId = -1;
    int32_t surfaceNodeId = -1;    // OH RSSurfaceNode ID for rendering
    int32_t displayId = 0;
    int32_t width = 0;
    int32_t height = 0;
    bool valid = false;
    int32_t wsErr = 0;             // §3.1.5.6.2 — raw OH WSError on failure;
                                   // 0 on success. Surfaced to Java for
                                   // semantic mapping into ADD_* codes.
};

class OHWindowManagerClient {
public:
    static OHWindowManagerClient& getInstance();

    /**
     * Connect to OH SceneSessionManager (SA 4607).
     */
    bool connect();

    /**
     * Disconnect from services.
     */
    void disconnect();

    /**
     * Create a window session with OH SceneSessionManager.
     *
     * Authoritative spec: doc/window_manager_ipc_adapter_design.html §3.1
     *
     * Pipeline:
     *   1. mapAndroidWindowType(androidWindowType) -> OH WindowType  (§3.1.4.1)
     *   2. Construct SessionStageAdapter / WindowEventChannelAdapter
     *   3. RSSurfaceNode::Create(cfg, isWindowSurfaceNode=true)      (§3.1.4.4)
     *   4. WindowSessionProperty: SetWindowType(mapped) + SetTokenState(false)
     *      + SessionInfo{bundleName, abilityName, moduleName}        (§3.1.4.2/§3.1.4.6)
     *   5. ISceneSessionManager::CreateAndConnectSpecificSession(...)
     *
     * @param jvm               JavaVM for callback threads
     * @param androidWindow     Java IWindow Stub for OH→App reverse callbacks
     * @param bundleName        Real Android package name (com.example.foo)
     * @param abilityName       Activity simple name (e.g. MainActivity)
     * @param moduleName        HAP module name; default "entry"
     * @param windowName        attrs.title or "AndroidWindow"
     * @param androidWindowType Android LayoutParams.type (mapped per §3.1.4.1)
     * @param displayId         Target display (DAYU200 single-screen always 0)
     * @param requestedWidth    Pixels
     * @param requestedHeight   Pixels
     * @param ohTokenAddr       Raw pointer to OH AbilityRecord token
     *                          (looked up from AppSchedulerBridge.OhTokenRegistry
     *                          on the Java side from attrs.token). 0 = no token,
     *                          falls back to TokenState=false. §3.1.5.6.1
     * @return Session info; valid=false + wsErr on IPC failure
     */
    OHWindowSession createSession(JavaVM* jvm, jobject androidWindow,
                                   const std::string& bundleName,
                                   const std::string& abilityName,
                                   const std::string& moduleName,
                                   const std::string& windowName,
                                   int32_t androidWindowType, int32_t displayId,
                                   int32_t requestedWidth, int32_t requestedHeight,
                                   uint64_t ohTokenAddr);

    /**
     * Update session rectangle (relayout).
     *
     * @param sessionId     Session ID from createSession.
     * @param x             Left position.
     * @param y             Top position.
     * @param width         Width in pixels.
     * @param height        Height in pixels.
     * @return 0 on success.
     */
    int updateSessionRect(int32_t sessionId, int32_t x, int32_t y,
                          int32_t width, int32_t height);

    /**
     * Notify OH that drawing is completed for a session.
     *
     * @param sessionId     Session ID.
     * @return 0 on success.
     */
    int notifyDrawingCompleted(int32_t sessionId);

    /**
     * Destroy a window session.
     *
     * @param sessionId     Session ID to destroy.
     */
    void destroySession(int32_t sessionId);


    /**
     * Get the OH RSSurfaceNode ID for a session, used by Android's
     * SurfaceControl to render into the OH compositing tree.
     */
    int64_t getSurfaceNodeId(int32_t sessionId) const;

    /**
     * 2026-05-02 G2.14r: get an OHNativeWindow* wrapping the session's
     * RSSurfaceNode producer surface.  This is the canonical entry point
     * for libhwui's EglManager to create EGLSurface — eglCreateWindowSurface
     * accepts OHNativeWindow* (it's the OH ANativeWindow equivalent).
     *
     * Internally:
     *   sessions_[sessionId].surfaceNode → GetSurface() → sptr<Surface>
     *   → CreateNativeWindowFromSurface(&sptr) → OHNativeWindow*
     *
     * The returned pointer is owned by surface utils (lifecycle tied to
     * RSSurfaceNode); caller MUST NOT free.  Returns nullptr if sessionId
     * unknown or RSSurfaceNode has no producer surface.
     */
    void* getOhNativeWindow(int32_t sessionId);

    /**
     * 2026-05-06 — Per graphics_rendering_design.html §5.1 + §9.1 (三件套):
     *   Returns the shared_ptr to the RSSurfaceNode created in createSession()
     *   and registered with WMS via CreateWindow + AddWindow.
     *
     * Single-source rule (§9.1 condition #2): the surfaceNode that WMS
     * registered MUST be the same surfaceNode that hwui producer flushes
     * buffers into.  oh_surface_bridge.cpp must call this getter instead of
     * creating its own RSSurfaceNode (the dual-surfaceNode bug pre-fix).
     *
     * Returns nullptr if sessionId unknown.
     */
    std::shared_ptr<OHOS::Rosen::RSSurfaceNode> getRSSurfaceNode(int32_t sessionId);

    bool isConnected() const { return connected_; }

private:
    OHWindowManagerClient() = default;
    ~OHWindowManagerClient() = default;

    bool connected_ = false;
    OHOS::sptr<OHOS::IRemoteObject> ssmProxy_ = nullptr;  // SceneSessionManager (SA 4607)

    // Track active sessions
    struct SessionEntry {
        int32_t sessionId;
        int64_t surfaceNodeId;
        OHOS::sptr<OHOS::Rosen::ISession> sessionProxy;  // Per-window ISession for operations
        OHOS::sptr<SessionStageAdapter> stageAdapter;
        // V7 SceneSessionManager.CreateAndConnectSpecificSession requires an
        // IWindowEventChannel sptr (split out of the V6 IWindow callback). In
        // our adapter Android receives input via OHInputBridge → InputChannel,
        // so this stub is a no-op endpoint kept for SSM addressing.
        OHOS::sptr<WindowEventChannelAdapter> eventChannel;
        // Local RSSurfaceNode kept alive for the lifetime of the session; SSM
        // does not retain a strong ref on the client side.
        std::shared_ptr<OHOS::Rosen::RSSurfaceNode> surfaceNode;
        // 2026-05-08 G2.14ab: ProducerSurface sptr kept alive for the lifetime
        // of the session.  hwui RenderThread holds OHNativeWindow* (via
        // ANativeWindow* reinterpret_cast) and increments its backing ProducerSurface
        // refcount asynchronously (libsurface.z.so RefBase::IncStrongRef call site).
        // If this sptr is a local stack variable (the G2.14r mistake at line 573),
        // it releases on function return → sptr refcount drops to 0 → producer
        // memory poisoned with 0xcafe5c02 magic → hwui IncStrongRef SIGSEGV.
        // SessionEntry must hold the sptr until session destruction so that
        // CreateNativeWindowFromSurface(&this sptr) keeps a stable backing
        // object alive across hwui's async use.
        OHOS::sptr<OHOS::Surface> producerSurface;
        // 2026-05-02 G2.14r: cached OHNativeWindow* (from
        // CreateNativeWindowFromSurface(&producerSurface)).  Used
        // by hwui's EglManager to create EGLSurface for hardware rendering.
        // Lazy-initialized on first getOhNativeWindow() call.  Type is
        // forward-declared here as void* to avoid pulling window.h header
        // into every translation unit; cast to OHNativeWindow* at use site.
        void* ohNativeWindow = nullptr;
    };
    std::map<int32_t, SessionEntry> sessions_;
    std::mutex sessionMutex_;
    int32_t nextSessionId_ = 1;
};

}  // namespace oh_adapter

#endif  // OH_WINDOW_MANAGER_CLIENT_H
