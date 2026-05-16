/*
 * oh_window_manager_client.cpp
 *
 * OpenHarmony WindowManager / SceneSessionManager IPC client implementation.
 *
 * Connects to OH SceneSessionManager (scene-based WMS) and provides window
 * session lifecycle management. Each Android window maps to an OH session
 * backed by an RSSurfaceNode for rendering.
 *
 * IPC flow for Hello World window display:
 *   1. createSession() -> SSM.CreateAndConnectSpecificSession()
 *      - Creates an OH scene session via ISceneSessionManager (system singleton)
 *      - Registers SessionStageAdapter as ISessionStage callback
 *      - Returns ISession proxy (per-window) + RSSurfaceNode ID
 *   2. updateSessionRect() -> ISession.UpdateSessionRect()
 *      - Sets window position and size via per-window ISession proxy
 *   3. notifyDrawingCompleted() -> ISession.DrawingCompleted()
 *      - Tells OH compositor the window is ready to display
 *   4. destroySession() -> ISession.Disconnect() + SSM.DestroyAndDisconnectSpecificSession()
 *      - Disconnects per-window session, then notifies SSM to clean up
 *
 * Reference:
 *   OH: wms/window_scene/session_manager/include/zidl/scene_session_manager_interface.h
 *   OH: wms/window_scene/session/host/include/zidl/session_interface.h
 */
#include "oh_window_manager_client.h"
#include "session_stage_adapter.h"
#include "window_callback_adapter.h"
#include "window_event_channel_adapter.h"
#include <android/log.h>
#include <atomic>
#include <map>

#include "ipc_skeleton.h"
#include "iservice_registry.h"  // OHOS::SystemAbilityManagerClient
#include "system_ability_manager_proxy.h"
#include "window_manager_hilog.h"
#include "ui/rs_surface_node.h"                       // RSSurfaceNode::Create
#include "transaction/rs_transaction.h"               // RSTransaction::FlushImplicitTransaction
#include "oh_br_trace.h"                              // G2.14ac IPC trace+log macros

// 2026-05-02 G2.14r: forward-declare CreateNativeWindowFromSurface from
// graphic_surface/interfaces/inner_api/surface/window.h.  Direct #include is
// ambiguous because OH has 10+ different files named window.h on its include
// path (window_manager/libwm/include/window.h gets picked first by the
// compiler).  The signature is stable: void* pSurface is `OHOS::sptr<OHOS::Surface>*`.
struct NativeWindow;
typedef struct NativeWindow OHNativeWindow;
extern "C" OHNativeWindow* CreateNativeWindowFromSurface(void* pSurface);
// 2026-05-12 G2.14aw probe A.1: read producer uniqueId from OHNativeWindow.
// Defined in libnative_window.so; signature stable per
// graphic_surface/interfaces/inner_api/surface/external_window.h:674.
extern "C" int32_t OH_NativeWindow_GetSurfaceId(OHNativeWindow* window, uint64_t* surfaceId);

// 2026-05-02 G2.14r: file-scope declaration so namespace-internal createSession
// can call it without `extern "C"` block-level decl (which is not allowed in
// function body).  Same symbol is exported below via "C" linkage.
extern "C" void oh_wm_set_last_session(int32_t);

// 2026-05-09 G2.14ae: oh_anw_wrap from oh_anativewindow_shim.cpp wraps a raw
// OH NativeWindow handle into an AOSP-ABI-compatible ANativeWindow struct so
// hwui can use AOSP NDK offsets without crashing on OH's RefBase / virtual
// class layout. See doc/graphics_rendering_design.html §7.11.
extern "C" struct ANativeWindow* oh_anw_wrap(OHNativeWindow* oh);
// G2.14c (2026-05-01) — pivot from SCB-style 3-hop chain to legacy
// IWindowManager. SA 4606 host on this OH 7.0.0.18 build is libwms.z.so's
// WindowManagerService (legacy), NOT libsms.z.so's MockSessionManagerService.
// Confirmed by: (a) /system/profile/foundation.json says SA 4606 -> libwms.z.so
// (b) deployed libwms.z.so contains only WindowManagerService symbols, no
// MockSessionManagerService (c) OH BUILD.gn shows libwms is the legacy WMS
// library when window_manager_use_sceneboard=false (the project default).
// So we use IWindowManager.CreateWindow + AddWindow instead of SCB's
// CreateAndConnectSpecificSession 3-hop chain.
#include "window_manager_interface.h"                 // IWindowManager (legacy)
#include "window_property.h"                          // legacy WindowProperty

#define LOG_TAG "OH_WindowMgrClient"
// B.37 sediment / memory feedback_prefer_inner_api.md: use OH HiLogPrint
// directly. __android_log_print is no-op on OH (the Android log shim isn't
// wired up for adapter .so bridge code). HiLogPrint goes straight to OH
// hilog so logs from this file actually appear.
#include "hilog/log.h"
#define LOGI(fmt, ...) HiLogPrint(LOG_CORE, LOG_INFO,  0xD000F00u, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGE(fmt, ...) HiLogPrint(LOG_CORE, LOG_ERROR, 0xD000F00u, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGW(fmt, ...) HiLogPrint(LOG_CORE, LOG_WARN,  0xD000F00u, LOG_TAG, fmt, ##__VA_ARGS__)

// OH system ability IDs (per system_ability_definition.h:285)
//   4606 = WINDOW_MANAGER_SERVICE_ID — serves BOTH legacy IWindowManager AND
//          ISceneSessionManager (the latter extends the former); this is what
//          OH's own WindowAdapter / SessionManager call (window_adapter.cpp:508,
//          session_manager.cpp:243).
//   4607 = DISPLAY_MANAGER_SERVICE_SA_ID — serves IDisplayManager (NOT WMS).
//
// G2.14 root cause: pre-fix used 4607 → silent iface_cast<ISceneSessionManager>
// on a DisplayManager proxy → CreateAndConnectSpecificSession got "method not
// found" on the wrong server → SendRequest returned non-ERR_NONE. The
// IPCObjectProxy log line "desc:*.IDisplayManager error:1" came from the same
// proxy and was the smoking gun.
static constexpr int32_t SCENE_SESSION_MANAGER_ID = 4606;

namespace oh_adapter {

// §3.1.4.1 — Android LayoutParams.type → OH WindowType.
//
// Android main DecorView (TYPE_BASE_APPLICATION=1 / TYPE_APPLICATION=2 /
// TYPE_APPLICATION_STARTING=3) is downgraded to OH WINDOW_TYPE_APP_SUB_WINDOW
// because OH manages the actual main window through the Ability lifecycle —
// CreateAndConnectSpecificSession is OH's IPC for sub windows / system windows
// only, and would reject a main-window-typed property.
//
// Casting Android's enum value directly to OH WindowType (the pre-fix behavior)
// dropped values 2/3/etc. into OH enum gaps -> WindowSessionProperty
// Marshalling rejected them -> server returned ERR_INVALID_DATA -> proxy logged
// "SendRequest failed". This map is the actual G2.13 fix.
static OHOS::Rosen::WindowType mapAndroidWindowType(int32_t androidType) {
    using OHOS::Rosen::WindowType;
    // G2.14c (legacy mode) — main app DecorView maps to APP_MAIN_WINDOW (1).
    // SUB_WINDOW (1001) requires parent windowId via property->SetParentId,
    // which adapter can't supply for a top-level Activity. Legacy WMS's
    // CheckSystemWindowPermission allows APP_MAIN_WINDOW for any caller (it
    // only blocks SystemWindow types 2000+ for non-SA callers).
    switch (androidType) {
        case 1:        // TYPE_BASE_APPLICATION
        case 2:        // TYPE_APPLICATION
        case 3:        // TYPE_APPLICATION_STARTING
            return WindowType::WINDOW_TYPE_APP_MAIN_WINDOW;
        // Real sub windows must have parent set explicitly by caller; for
        // now downgrade to MAIN type as well — adapter doesn't yet plumb
        // parent linkage for Android sub windows.
        case 1000:     // TYPE_APPLICATION_PANEL
        case 1001:     // TYPE_APPLICATION_MEDIA
        case 1002:     // TYPE_APPLICATION_SUB_PANEL
        case 1003:     // TYPE_APPLICATION_ATTACHED_DIALOG
            return WindowType::WINDOW_TYPE_APP_MAIN_WINDOW;
        case 2003:     // TYPE_SYSTEM_ALERT
        case 2008:     // TYPE_SYSTEM_DIALOG
        case 2038:     // TYPE_APPLICATION_OVERLAY
            return WindowType::WINDOW_TYPE_DIALOG;
        case 2005:     // TYPE_TOAST
            return WindowType::WINDOW_TYPE_TOAST;
        case 2006:     // TYPE_SYSTEM_OVERLAY
            return WindowType::WINDOW_TYPE_FLOAT;
        case 2011:     // TYPE_INPUT_METHOD
        case 2012:     // TYPE_INPUT_METHOD_DIALOG
            return WindowType::WINDOW_TYPE_INPUT_METHOD_FLOAT;
        default:
            return WindowType::WINDOW_TYPE_APP_MAIN_WINDOW;
    }
}

OHWindowManagerClient& OHWindowManagerClient::getInstance() {
    static OHWindowManagerClient instance;
    return instance;
}

bool OHWindowManagerClient::connect() {
    OH_BR_IPC_SCOPE("WMClient.connect", "");
    LOGI("Connecting to OH IWindowManager (legacy) via SA %d ...", SCENE_SESSION_MANAGER_ID);

    auto samgr = OHOS::SystemAbilityManagerClient::GetInstance().GetSystemAbilityManager();
    if (samgr == nullptr) {
        LOGE("Failed to get SystemAbilityManager");
        return false;
    }

    // SA 4606 = WINDOW_MANAGER_SERVICE_ID. On this OH 7.0.0.18 build (legacy
    // mode, window_manager_use_sceneboard=false), this SA hosts
    // WindowManagerService which exposes IWindowManager directly. Single
    // iface_cast — no 3-hop chain needed.
    ssmProxy_ = samgr->GetSystemAbility(SCENE_SESSION_MANAGER_ID);
    if (ssmProxy_ == nullptr) {
        LOGE("SAMGR returned null for SA %d", SCENE_SESSION_MANAGER_ID);
        return false;
    }

    LOGI("Connected to IWindowManager (legacy) via SA %d", SCENE_SESSION_MANAGER_ID);
    connected_ = true;
    return true;
}

void OHWindowManagerClient::disconnect() {
    OH_BR_IPC_SCOPE("WMClient.disconnect", "");
    LOGI("Disconnecting from OH window services");

    std::lock_guard<std::mutex> lock(sessionMutex_);
    sessions_.clear();
    ssmProxy_ = nullptr;
    connected_ = false;
}

OHWindowSession OHWindowManagerClient::createSession(
    JavaVM* jvm, jobject androidWindow,
    const std::string& bundleName, const std::string& abilityName,
    const std::string& moduleName, const std::string& windowName,
    int32_t androidWindowType, int32_t displayId,
    int32_t requestedWidth, int32_t requestedHeight,
    uint64_t ohTokenAddr)
{
    OH_BR_IPC_SCOPE("WMClient.createSession",
                    "bundle=%{public}s ability=%{public}s name=%{public}s w=%{public}d h=%{public}d ohToken=0x%{public}llx",
                    bundleName.c_str(), abilityName.c_str(), windowName.c_str(),
                    requestedWidth, requestedHeight,
                    (unsigned long long)ohTokenAddr);
    // §3.1.5.6.2 — wsErr defaults capture the most likely cause of early-exit.
    OHWindowSession result;
    result.wsErr = static_cast<int32_t>(OHOS::Rosen::WSError::WS_ERROR_IPC_FAILED);

    if (!connected_ || ssmProxy_ == nullptr) {
        LOGE("createSession: Not connected to SceneSessionManager");
        return result;
    }

    // §3.1.4.1 — translate Android type to a value OH SSM accepts. Direct cast
    // from Android type to OH WindowType drops most values into enum gaps and
    // makes WindowSessionProperty Marshalling reject the property → server
    // returns ERR_INVALID_DATA → proxy logs "SendRequest failed".
    OHOS::Rosen::WindowType ohType = mapAndroidWindowType(androidWindowType);

    // §3.1.5.6.1 — wrap raw OH token pointer into sptr for safe lifetime.
    // 0 = no token; we then keep TokenState=false to honor §3.1.4.6.6 矩阵.
    OHOS::sptr<OHOS::IRemoteObject> token = nullptr;
    if (ohTokenAddr != 0) {
        token = OHOS::sptr<OHOS::IRemoteObject>(
            reinterpret_cast<OHOS::IRemoteObject*>(ohTokenAddr));
    }

    LOGI("createSession: bundle=%{public}s ability=%{public}s name=%{public}s, "
         "androidType=%{public}d -> ohType=%{public}u, "
         "display=%{public}d, size=%{public}dx%{public}d, "
         "ohTokenAddr=0x%{public}llx token=%{public}p",
         bundleName.c_str(), abilityName.c_str(), windowName.c_str(),
         androidWindowType, static_cast<uint32_t>(ohType),
         displayId, requestedWidth, requestedHeight,
         static_cast<unsigned long long>(ohTokenAddr),
         token.GetRefPtr());

    // V7 SceneSessionManager.CreateAndConnectSpecificSession wire format:
    //   (sessionStage, eventChannel, surfaceNode, property, persistentId&,
    //    session&, systemConfig&, token=nullptr)
    // V6 (stageAdapter, windowAdapter, sessionInfo, session&, ...) signature
    // and WindowCallbackAdapter (V6 IWindow) are no longer used here. Input
    // events still flow on the Android side via OHInputBridge → InputChannel;
    // the IWindowEventChannel stub here exists only so SSM has a non-null
    // callback target. The RSSurfaceNode is client-created and its node id is
    // what Android-side SurfaceControl renders into.

    // G2.14c — legacy IWindow callback stub (single per-window endpoint, no
    // separate ISessionStage/IWindowEventChannel split).
    OHOS::sptr<WindowCallbackAdapter> windowCallback =
        new WindowCallbackAdapter(jvm, androidWindow);

    OHOS::Rosen::RSSurfaceNodeConfig nodeCfg;
    nodeCfg.SurfaceNodeName = windowName;
    // 2026-05-11 G2.14aq — reverted UI_EXTENSION_COMMON_NODE → APP_WINDOW_NODE.
    //
    // History:
    //   G2.14ah identified IsCallingPidValid blocking PERMISSION_APP commands
    //   on TF_ASYNC path (callingPid=0 ≠ commandPid).  G2.14ai chose
    //   UI_EXTENSION_COMMON_NODE because nodeMap.IsUIExtensionSurfaceNode
    //   provided a second bypass in the same check.  That made commands pass,
    //   but UI_EXTENSION_COMMON_NODE has the side-effect that:
    //     IsMainWindowType() = false   (nodeType_ > SELF_DRAWING_WINDOW_NODE)
    //     IsAppWindow()      = false
    //   so OH RS main compose loop does NOT schedule the surface for
    //   composition — buffers reach the producer queue but never appear
    //   on screen (G2.14ap proved this via RS hidumper: OpaqueRegion=Empty,
    //   shouldPaint_=0, even with bounds/frame correctly set).
    //
    // G2.14aq fixes the root cause at the OH side via
    // ohos_patches/graphic_2d/.../rs_transaction_data.cpp.patch — when
    // IsCallingPidValid sees callingPid=0 (TF_ASYNC sentinel), it falls back
    // to the SendingPid (RSTransactionData::pid_) the client Marshalled into
    // the parcel, which DOES equal commandPid for normal-apl clients.  That
    // restores the natural pid_==commandPid bypass for our app, allowing us
    // to use APP_WINDOW_NODE here so the layer enters main compositing.
    //
    // SECURITY NOTE: the patch trusts a client-supplied pid_ when the kernel
    // sender_pid is 0.  Detailed risk analysis (and why it is acceptable for
    // an Android-adapter device topology with ≤1 normal-apl process) is in
    // doc/build_patch_log.html [Patch G2.14aq].
    auto surfaceNode = OHOS::Rosen::RSSurfaceNode::Create(
        nodeCfg,
        OHOS::Rosen::RSSurfaceNodeType::APP_WINDOW_NODE,
        /*isWindow=*/true);
    if (!surfaceNode) {
        LOGE("createSession: RSSurfaceNode::Create failed for %s", windowName.c_str());
        result.wsErr = 1001;  // WS_ERROR_NULLPTR equivalent
        return result;
    }

    // 2026-05-11 G2.14am-PROBE result captured (memory project_g214am_probe_red.md):
    // RED bg + isOpaque=0 → screen all red → hwui buffer is fully transparent
    // (no GL clear, no Skia draws).  Conclusion: hwui produces empty buffer.
    // Probe code removed; next diagnostic phase (G2.14an) intercepts Canvas
    // DrawOps at adapter/shim layer to confirm whether helloworld View.draw
    // submits any draws to BaseCanvas natives.  See compat_shim.cpp.

    // Legacy WindowProperty (NOT V7 WindowSessionProperty). Fewer fields,
    // no SessionInfo / TokenState dance.
    OHOS::sptr<OHOS::Rosen::WindowProperty> property = new OHOS::Rosen::WindowProperty();
    property->SetWindowName(windowName);
    property->SetWindowType(ohType);
    property->SetWindowMode(OHOS::Rosen::WindowMode::WINDOW_MODE_FULLSCREEN);
    OHOS::Rosen::Rect rect{0, 0,
        static_cast<uint32_t>(requestedWidth), static_cast<uint32_t>(requestedHeight)};
    property->SetWindowRect(rect);
    property->SetRequestRect(rect);  // legacy WMS uses requestRect for AddWindow
    property->SetOriginRect(rect);
    property->SetDisplayId(displayId);

    auto wmsInterface = OHOS::iface_cast<OHOS::Rosen::IWindowManager>(ssmProxy_);
    if (wmsInterface == nullptr) {
        LOGE("createSession: Failed to cast to IWindowManager");
        result.wsErr = 1001;
        return result;
    }

    // Step 1: CreateWindow — pass our IWindow stub IN; server allocates windowId.
    uint32_t windowId = 0;
    OHOS::sptr<OHOS::Rosen::IWindow> windowProxy(windowCallback.GetRefPtr());
    auto retCreate = wmsInterface->CreateWindow(windowProxy, property, surfaceNode,
                                                 windowId, token);
    if (retCreate != OHOS::Rosen::WMError::WM_OK) {
        LOGE("createSession: IWindowManager::CreateWindow failed ret=%{public}d",
             static_cast<int>(retCreate));
        result.wsErr = static_cast<int32_t>(retCreate);
        return result;
    }
    LOGI("createSession: CreateWindow OK, windowId=%{public}u", windowId);

    // Step 2: AddWindow — actually display the window. WMS reads
    // property->GetWindowId() server-side; out-param windowId from CreateWindow
    // doesn't auto-propagate, so we must SetWindowId on the property before
    // sending it through the AddWindow IPC.
    property->SetWindowId(windowId);
    auto retAdd = wmsInterface->AddWindow(property);
    if (retAdd != OHOS::Rosen::WMError::WM_OK) {
        LOGE("createSession: IWindowManager::AddWindow failed ret=%{public}d (windowId=%{public}u)",
             static_cast<int>(retAdd), windowId);
        result.wsErr = static_cast<int32_t>(retAdd);
        wmsInterface->RemoveWindow(windowId, true);  // cleanup partial
        return result;
    }

    int64_t surfaceNodeId = static_cast<int64_t>(surfaceNode->GetId());
    int32_t persistentId = static_cast<int32_t>(windowId);

    int32_t sessionId;
    {
        std::lock_guard<std::mutex> lock(sessionMutex_);
        sessionId = persistentId > 0 ? persistentId : nextSessionId_++;
        // Legacy path: sessionProxy/stageAdapter/eventChannel are unused.
        // Store windowCallback in stageAdapter slot (sptr type compatible
        // through reinterpret) — TODO P2: refactor SessionEntry to be
        // path-aware (legacy vs V7). For now, keep stage/channel null and
        // hold windowCallback separately via static map keyed by sessionId.
        sessions_[sessionId] = {sessionId, surfaceNodeId, /*sessionProxy=*/nullptr,
                                /*stageAdapter=*/nullptr, /*eventChannel=*/nullptr,
                                surfaceNode};
        // Keep windowCallback alive for the session's lifetime.
        static std::map<int32_t, OHOS::sptr<WindowCallbackAdapter>> windowCallbacks;
        windowCallbacks[sessionId] = windowCallback;
    }

    result.sessionId = sessionId;
    result.surfaceNodeId = static_cast<int32_t>(surfaceNodeId);
    result.displayId = displayId;
    result.width = requestedWidth;
    result.height = requestedHeight;
    result.valid = true;
    result.wsErr = 0;  // §3.1.5.6.2 — explicit zero on success path

    LOGI("createSession: success, sessionId=%d, surfaceNodeId=%lld",
         sessionId, static_cast<long long>(surfaceNodeId));
    // 2026-05-02 G2.14r: stamp last-attached-session for BBQ_nativeUpdate
    // fallback (avoids BCP-jar boot-image rebuild for one new native method).
    oh_wm_set_last_session(sessionId);
    return result;
}

int OHWindowManagerClient::updateSessionRect(int32_t sessionId,
                                              int32_t x, int32_t y,
                                              int32_t width, int32_t height)
{
    OH_BR_IPC_SCOPE("WMClient.updateSessionRect",
                    "session=%{public}d rect=[%{public}d,%{public}d,%{public}d,%{public}d]",
                    sessionId, x, y, width, height);

    // G2.14c — legacy path: IWindowManager has no per-window UpdateRect IPC;
    // geometry updates go through IWindowManager::UpdateProperty(property, action).
    // For now (P1), no-op — Android Activity main DecorView doesn't strictly
    // need relayout to put pixels on screen for HelloWorld. P2: add legacy
    // UpdateProperty plumbing.
    LOGI("updateSessionRect: legacy path, no-op (window=%d %dx%d)",
         sessionId, width, height);
    return 0;
}

int OHWindowManagerClient::notifyDrawingCompleted(int32_t sessionId) {
    OH_BR_IPC_SCOPE("WMClient.notifyDrawingCompleted", "session=%{public}d", sessionId);
    // G2.14c — legacy IWindowManager has no DrawingCompleted IPC; OH legacy
    // WMS triggers display once AddWindow runs.
    return 0;
}

void OHWindowManagerClient::destroySession(int32_t sessionId) {
    OH_BR_IPC_SCOPE("WMClient.destroySession", "session=%{public}d", sessionId);

    OHOS::sptr<OHOS::Rosen::ISession> sessionProxy;
    {
        std::lock_guard<std::mutex> lock(sessionMutex_);
        auto it = sessions_.find(sessionId);
        if (it != sessions_.end()) {
            sessionProxy = it->second.sessionProxy;
            sessions_.erase(it);
        }
    }

    // G2.14c — legacy path uses IWindowManager.RemoveWindow + DestroyWindow.
    // sessionProxy is null in legacy path (V7-only field); skip Disconnect().
    if (connected_ && ssmProxy_ != nullptr) {
        auto wmsInterface = OHOS::iface_cast<OHOS::Rosen::IWindowManager>(ssmProxy_);
        if (wmsInterface) {
            wmsInterface->RemoveWindow(static_cast<uint32_t>(sessionId), true);
            wmsInterface->DestroyWindow(static_cast<uint32_t>(sessionId), false);
        }
    }
}


int64_t OHWindowManagerClient::getSurfaceNodeId(int32_t sessionId) const {
    auto it = sessions_.find(sessionId);
    if (it != sessions_.end()) {
        return it->second.surfaceNodeId;
    }
    return -1;
}

// 2026-05-06 — Per design §5.1 / §9.1 三件套 condition #2:
//   Single-source rule for surfaceNode.  Used by oh_surface_bridge.cpp to fetch
//   the same RSSurfaceNode that was registered with WMS, so the WMS-side
//   layer node and the hwui-producer-side buffer feed share one node.
std::shared_ptr<OHOS::Rosen::RSSurfaceNode>
OHWindowManagerClient::getRSSurfaceNode(int32_t sessionId) {
    OH_BR_IPC_SCOPE("WMClient.getRSSurfaceNode", "session=%{public}d", sessionId);
    std::lock_guard<std::mutex> lock(sessionMutex_);
    auto it = sessions_.find(sessionId);
    if (it == sessions_.end()) {
        LOGE("getRSSurfaceNode: unknown sessionId=%d", sessionId);
        return nullptr;
    }
    if (!it->second.surfaceNode) {
        LOGE("getRSSurfaceNode: sessionId=%d has no RSSurfaceNode", sessionId);
        return nullptr;
    }
    return it->second.surfaceNode;
}

// 2026-05-02 G2.14r: stable C wrapper for cross-.so callers (e.g.,
// liboh_android_runtime.so::compat_shim BBQ_nativeUpdate).  Avoids C++
// namespace + name mangling issues at the dlsym boundary.
extern "C" __attribute__((visibility("default")))
void* oh_wm_get_native_window(int32_t sessionId) {
    return oh_adapter::OHWindowManagerClient::getInstance().getOhNativeWindow(sessionId);
}

// 2026-05-02 G2.14r: cross-process "last touched session" hint.  Used by
// BBQ_nativeUpdate when its SurfaceControl carries no sessionId (avoids the
// need for a BCP-class native method to attach session, which would require
// boot image rebuild on every change).  Each child appspawn-x process spawns
// one app with one session, so a process-global last-session is unambiguous.
namespace {
std::atomic<int32_t> g_lastAttachedSession{0};
}  // namespace
extern "C" __attribute__((visibility("default")))
int32_t oh_wm_get_last_session() {
    return g_lastAttachedSession.load(std::memory_order_acquire);
}
extern "C" __attribute__((visibility("default")))
void oh_wm_set_last_session(int32_t sessionId) {
    OH_BR_IPC_SCOPE("oh_wm_set_last_session", "session=%{public}d", sessionId);
    g_lastAttachedSession.store(sessionId, std::memory_order_release);
}

// 2026-05-06 — Per design §5.6 / §9.1 三件套 condition #3:
//   Cross-.so C wrappers so liboh_android_runtime.so::android_view_SurfaceControl.cpp
//   (in a different .so) can route SurfaceControl property setters and apply()
//   into RSSurfaceNode + RSTransactionProxy without link-time dependency.
//   All callers use dlsym RTLD_DEFAULT; if liboh_adapter_bridge.so isn't yet
//   loaded into the process, the no-op fallback keeps the SC stub intact.
extern "C" __attribute__((visibility("default")))
void oh_rs_set_layer_bounds(int32_t sessionId, float x, float y, float w, float h) {
    OH_BR_IPC_SCOPE("oh_rs_set_layer_bounds",
                    "session=%{public}d xywh=[%{public}.1f,%{public}.1f,%{public}.1f,%{public}.1f]",
                    sessionId, x, y, w, h);
    auto node = oh_adapter::OHWindowManagerClient::getInstance().getRSSurfaceNode(sessionId);
    if (!node) return;
    // 2026-05-11 G2.14ap: OH RSNode requires SetBounds AND SetFrame as a pair
    // (per foundation/graphic/graphic_2d/.../rs_screen_render_node.h standard
    //  usage).  Without SetFrame, Frame stays at sentinel [-inf, -inf, -inf, -inf]
    //  and ClipToFrame=true (which adapter sets via SurfaceControl) clips the
    //  entire surface to an empty region — VisibleRegion / OpaqueRegion=Empty,
    //  shouldPaint_=0, localDrawRect_=[0,0,0,0] — hwui's frame submission then
    //  no-ops at the RS level even though buffers reach the producer queue.
    node->SetBounds(x, y, w, h);
    node->SetFrame(x, y, w, h);
}

extern "C" __attribute__((visibility("default")))
void oh_rs_set_layer_alpha(int32_t sessionId, float alpha) {
    OH_BR_IPC_SCOPE("oh_rs_set_layer_alpha", "session=%{public}d alpha=%{public}.2f", sessionId, alpha);
    auto node = oh_adapter::OHWindowManagerClient::getInstance().getRSSurfaceNode(sessionId);
    if (!node) return;
    node->SetAlpha(alpha);
}

extern "C" __attribute__((visibility("default")))
void oh_rs_set_layer_visible(int32_t sessionId, int32_t visible) {
    OH_BR_IPC_SCOPE("oh_rs_set_layer_visible", "session=%{public}d visible=%{public}d", sessionId, visible);
    auto node = oh_adapter::OHWindowManagerClient::getInstance().getRSSurfaceNode(sessionId);
    if (!node) return;
    node->SetVisible(visible != 0);
}

extern "C" __attribute__((visibility("default")))
void oh_rs_flush_transaction() {
    OH_BR_IPC_SCOPE("oh_rs_flush_transaction", "");
    // Triggers RSTransactionProxy::FlushImplicitTransaction which commits
    // pending RSCommand batch (createNode / setBounds / setAlpha / ...)
    // to RenderService via RSIClientToRenderConnection::CommitTransaction.
    OHOS::Rosen::RSTransaction::FlushImplicitTransaction();
}

// 2026-05-11 G2.14al — bridge AOSP SurfaceControl.Transaction.setOpaque to OH.
// Java side: SurfaceControl.Transaction.setOpaque(sc, isOpaque) compiles to
//   nativeSetFlags(tx, sc, isOpaque ? SURFACE_OPAQUE : 0, SURFACE_OPAQUE=0x02)
// android_view_SurfaceControl.cpp SC_nativeSetFlags extracts the opaque bit
// and forwards here via dlsym RTLD_DEFAULT (same indirection pattern as
// oh_rs_set_layer_alpha / oh_rs_set_layer_visible — keeps liboh_android_
// runtime.so independent of OH C++ headers).
//
// OH equivalent: RSSurfaceNode::SetSurfaceBufferOpaque(bool isOpaque).
// Without this hint OH RS composes the layers underneath (the OH SCB
// starting/leash window stack, 720×1136 white), letting their white show
// through transparent helloworld surface even when hwui has drawn opaque
// TextView content into the buffer.
extern "C" __attribute__((visibility("default")))
void oh_rs_set_layer_opaque(int32_t sessionId, int32_t isOpaque) {
    OH_BR_IPC_SCOPE("oh_rs_set_layer_opaque",
                    "session=%{public}d isOpaque=%{public}d",
                    sessionId, isOpaque);
    auto node = oh_adapter::OHWindowManagerClient::getInstance()
                    .getRSSurfaceNode(sessionId);
    if (!node) return;
    node->SetSurfaceBufferOpaque(isOpaque != 0);
}

// ============================================================
// 2026-05-08 G2.14aa: ASurfaceControl/ASurfaceTransaction NDK 真桥 helpers
//
// AOSP hwui RenderThread (ASurfaceControlFunctions ctor in
// frameworks/base/libs/hwui/renderthread/RenderThread.cpp) dlopen("libandroid.so")
// + dlsym 9 个 NDK 符号。device 上 libandroid.so 是 liboh_android_runtime.so
// 的 symlink，需要在那边 export 9 个 wrapper；wrapper 通过 dlsym(RTLD_DEFAULT,
// "oh_rs_*") 找下面的 helper 真桥到 OH RS。
//
// OH RS = SurfaceFlinger 等价：
//   ASurfaceControl     ↔ RSSurfaceNode (sptr<>)
//   ASurfaceTransaction ↔ RSTransaction (隐式 transaction via RSTransactionProxy)
// ============================================================

/**
 * Create a sub-RSSurfaceNode (non-window). hwui WebViewFunctorManager 用此
 * 创建 child surface; helloworld 主路径不真触发。
 *
 * @return opaque RSSurfaceNode* (caller stores as void*; release via
 *         oh_rs_destroy_subsurface)
 */
extern "C" __attribute__((visibility("default")))
void* oh_rs_create_subsurface(const char* name) {
    OH_BR_IPC_SCOPE("oh_rs_create_subsurface", "name=%{public}s", name ? name : "(null)");
    OHOS::Rosen::RSSurfaceNodeConfig cfg;
    cfg.SurfaceNodeName = (name && *name) ? name : "adapter_subsurface";
    auto node = OHOS::Rosen::RSSurfaceNode::Create(cfg, /*isWindow=*/false);
    if (!node) {
        LOGE("oh_rs_create_subsurface: RSSurfaceNode::Create failed for %s", cfg.SurfaceNodeName.c_str());
        return nullptr;
    }
    // 转 sptr → raw 指针给 C ABI；wrapper 用 holder map 维持 sptr 引用
    auto* holder = new std::shared_ptr<OHOS::Rosen::RSSurfaceNode>(node);
    return holder;
}

/**
 * Release sub-RSSurfaceNode. Decrements sptr refcount (likely destroy).
 */
extern "C" __attribute__((visibility("default")))
void oh_rs_destroy_subsurface(void* opaque) {
    OH_BR_IPC_SCOPE("oh_rs_destroy_subsurface", "holder=%p", opaque);
    if (!opaque) return;
    delete reinterpret_cast<std::shared_ptr<OHOS::Rosen::RSSurfaceNode>*>(opaque);
}

/**
 * Register buffer-available listener on RSSurfaceNode. AOSP hwui
 * CanvasContext.cpp 通过 ASurfaceControl_registerSurfaceStatsListener 让
 * RenderThread 知道 buffer ready；OH 等价是 RSSurfaceNode::RegisterBufferAvailableListener。
 *
 * Callback 签名（hwui ASC_StatsListener）：
 *   void cb(void* context, int32_t controlFd, ASurfaceTransactionStats* stats)
 * 我们桥时 controlFd=0、stats=nullptr（OH 暂无 stats 等价；hwui 处理 null 安全）。
 */
extern "C" __attribute__((visibility("default")))
void oh_rs_register_buffer_listener(void* opaque,
                                     void (*cb)(void* /*context*/, int32_t /*ctlFd*/, void* /*stats*/),
                                     void* context) {
    OH_BR_IPC_SCOPE("oh_rs_register_buffer_listener",
                    "holder=%p cb=%p ctx=%p", opaque, (void*)cb, context);
    if (!opaque || !cb) return;
    auto* holder = reinterpret_cast<std::shared_ptr<OHOS::Rosen::RSSurfaceNode>*>(opaque);
    if (!*holder) return;
    // OH 7.0.0.18 public API 是 SetBufferAvailableCallback (signature: std::function<void()>)，
    // 内部走 RSRenderPipelineClient::RegisterBufferAvailableListener。一次只能 set 一个 cb。
    (*holder)->SetBufferAvailableCallback([cb, context]() {
        cb(context, 0, nullptr);
    });
}

// 2026-05-02 G2.14r: bridge from sessionId to OHNativeWindow*.  Cached
// per-session so repeated calls return the same pointer (Java Surface lifecycle
// expects a stable native object).
void* OHWindowManagerClient::getOhNativeWindow(int32_t sessionId) {
    OH_BR_IPC_SCOPE("WMClient.getOhNativeWindow", "session=%{public}d", sessionId);
    std::lock_guard<std::mutex> lock(sessionMutex_);
    auto it = sessions_.find(sessionId);
    if (it == sessions_.end()) {
        LOGE("getOhNativeWindow: unknown sessionId=%d", sessionId);
        return nullptr;
    }
    if (it->second.ohNativeWindow != nullptr) {
        return it->second.ohNativeWindow;
    }
    if (!it->second.surfaceNode) {
        LOGE("getOhNativeWindow: sessionId=%d has no RSSurfaceNode", sessionId);
        return nullptr;
    }
    // 2026-05-08 G2.14ab: store sptr<Surface> in SessionEntry (not local var)
    // so its lifetime equals session lifetime. hwui RenderThread holds
    // OHNativeWindow* and async IncStrongRef on the backing ProducerSurface;
    // a local sptr would release on return, leading to use-after-free
    // (libsurface RefBase::IncStrongRef on 0xcafe5c02 poisoned memory).
    if (!it->second.producerSurface) {
        it->second.producerSurface = it->second.surfaceNode->GetSurface();
    }
    if (!it->second.producerSurface) {
        LOGE("getOhNativeWindow: sessionId=%d RSSurfaceNode has no producer surface", sessionId);
        return nullptr;
    }
    // 2026-05-12 G2.14aw probe A.1 baseline: producer 出身证据.
    // surfaceNode->GetSurface() 拿到的 ProducerSurface 真身 + RS 服务端注册的 uniqueId,
    // 后面 wrap/swap 路径上的所有 uniqueId 都应等于此值才算同源.
    LOGI("getOhNativeWindow[probe-baseline]: sessionId=%d surfaceNode=%p surfaceNodeId=%lld "
         "producerSurface_sptrAddr=%p producerSurface_refPtr=%p uniqueId=0x%llx",
         sessionId, it->second.surfaceNode.get(),
         (long long)it->second.surfaceNodeId,
         (void*)&it->second.producerSurface,
         it->second.producerSurface.GetRefPtr(),
         (unsigned long long)it->second.producerSurface->GetUniqueId());
    // CreateNativeWindowFromSurface signature: OHNativeWindow* fn(void* pSurface)
    // expects address of sptr<Surface> (not the raw surface ptr).  See
    // foundation/graphic/graphic_surface/surface/src/native_window.cpp.
    // Pass address of SessionEntry-held sptr so the backing producer survives
    // hwui's async refcount increments.
    OHNativeWindow* nw = ::CreateNativeWindowFromSurface(&it->second.producerSurface);
    if (!nw) {
        LOGE("getOhNativeWindow: CreateNativeWindowFromSurface failed for sessionId=%d", sessionId);
        return nullptr;
    }
    // 2026-05-12 G2.14aw probe A.1: post-CreateNativeWindowFromSurface uniqueId.
    // OH NativeWindow internally copies sptr<Surface>, so uniqueId here MUST match the baseline above.
    // Mismatch ⇒ CreateNativeWindowFromSurface silently swapped the backing surface (very unlikely
    // but worth checking once — if equal we can drop this log later).
    {
        uint64_t uidPost = 0;
        int32_t qrc = ::OH_NativeWindow_GetSurfaceId(nw, &uidPost);
        LOGI("getOhNativeWindow[probe-postCreate]: sessionId=%d nw=%p uniqueId=0x%llx (rc=%d)",
             sessionId, (void*)nw, (unsigned long long)uidPost, qrc);
    }
    // 2026-05-09 G2.14ae: hwui treats ANativeWindow as an AOSP POD struct
    // with a function-pointer table at fixed offsets (system/window.h).
    // OH NativeWindow is a C++ virtual class deriving from RefBase with
    // sptr<Surface> / unordered_map / atomic members — completely
    // incompatible layout. Returning the raw OH handle to hwui caused
    // SIGSEGV in CanvasContext::setupPipelineSurface where hwui dereferenced
    // an offset that, on AOSP, would be common.reserved[2] but on OH falls
    // inside RefBase internals.
    //
    // Wrap the OH handle in an adapter-allocated AOSP-ABI-compatible
    // ANativeWindow struct whose 10 function pointers route to wrappers
    // calling OH_NativeWindow_* NDK equivalents. hwui sees the AOSP ABI
    // it expects; OH-side details stay hidden behind the wrapper.
    //
    // Reference: doc/graphics_rendering_design.html §7.11.
    void* aospAnw = ::oh_anw_wrap(reinterpret_cast<OHNativeWindow*>(nw));
    if (!aospAnw) {
        LOGE("getOhNativeWindow: oh_anw_wrap failed for sessionId=%d", sessionId);
        return nullptr;
    }
    it->second.ohNativeWindow = aospAnw;  // hwui-facing handle (AOSP ABI)
    LOGI("getOhNativeWindow: sessionId=%d -> AOSP-compat ANativeWindow=%p "
         "(oh=%p, surfaceNodeId=%lld)",
         sessionId, aospAnw, (void*)nw, (long long)it->second.surfaceNodeId);
    return aospAnw;
}

}  // namespace oh_adapter
