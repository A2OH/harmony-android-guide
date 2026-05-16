/*
 * session_stage_adapter.h
 *
 * Reverse callback adapter: OH SessionStageStub (ISessionStage) -> Android IWindow.
 *
 * This class inherits from OH SessionStageStub so it can be registered with
 * OH SceneSessionManager as an ISessionStage callback endpoint. When OH SSM
 * calls session state methods, they are forwarded via JNI to the Android
 * IWindow and IApplicationThread Binder proxies.
 *
 * Threading: OH SSM calls arrive on IPC binder threads. Each override
 * attaches to the JVM via JavaVM->AttachCurrentThread before making JNI calls.
 *
 * Key mappings:
 *   SetActive         -> IWindow.dispatchAppVisibility
 *   UpdateRect        -> IWindow.resized
 *   HandleBackEvent   -> inject KEYCODE_BACK key event
 *   NotifyDestroy     -> lifecycle handling
 *   Most others       -> OH_ONLY (log and ignore)
 */
#ifndef OH_ADAPTER_SESSION_STAGE_ADAPTER_H
#define OH_ADAPTER_SESSION_STAGE_ADAPTER_H

#include <jni.h>
#include <string>
#include <mutex>

#include "session/container/include/zidl/session_stage_stub.h"
#include "ws_common.h"
#include "wm_common.h"

namespace oh_adapter {
using OHOS::Rosen::WSError;
using OHOS::Rosen::WSErrorCode;
using OHOS::Rosen::WMError;

class SessionStageAdapter : public OHOS::Rosen::SessionStageStub {
public:
    /**
     * Construct the adapter.
     *
     * @param jvm              Pointer to the JavaVM (for AttachCurrentThread on IPC threads).
     * @param androidWindow    A Java android.view.IWindow instance (also used for
     *                         IApplicationThread callbacks). A JNI global ref
     *                         is created internally; the caller may release its local ref.
     */
    SessionStageAdapter(JavaVM* jvm, jobject androidWindow);
    ~SessionStageAdapter() override;

    // ================================================================
    // Category 1: Session Lifecycle
    // ================================================================

    // [BRIDGED] -> IWindow.dispatchAppVisibility(active)
    WSError SetActive(bool active) override;

    // [BRIDGED] -> IWindow.dispatchAppVisibility(true)
    void NotifySessionForeground(uint32_t reason, bool withAnimation) override;

    // [BRIDGED] -> IWindow.dispatchAppVisibility(false)
    void NotifySessionBackground(uint32_t reason, bool withAnimation, bool isFromInnerkits) override;

    // [BRIDGED] -> lifecycle handling
    WSError NotifyDestroy() override;

    // [BRIDGED] -> IWindow.dispatchAppVisibility(isVisible)
    WSError NotifyWindowVisibility(bool isVisible) override;

    // [OH_ONLY] Android infers occlusion from lifecycle
    WSError NotifyWindowOcclusionState(const OHOS::Rosen::WindowVisibilityState state) override;

    // [BRIDGED] -> IWindow.dispatchAppVisibility(interactive)
    void NotifyForegroundInteractiveStatus(bool interactive) override;

    // [OH_ONLY] OH specific lifecycle state
    void NotifyLifecyclePausedStatus() override;

    // ================================================================
    // Category 2: Window Geometry
    // ================================================================

    // [BRIDGED] -> IWindow.resized(frames, config, insets)
    WSError UpdateRect(const OHOS::Rosen::WSRect& rect, OHOS::Rosen::SizeChangeReason reason,
        const OHOS::Rosen::SceneAnimationConfig& config = { nullptr,
            OHOS::Rosen::ROTATE_ANIMATION_DURATION, 0,
            OHOS::Rosen::WindowAnimationCurve::LINEAR, {0.0f, 0.0f, 0.0f, 0.0f} },
        const std::map<OHOS::Rosen::AvoidAreaType, OHOS::Rosen::AvoidArea>& avoidAreas = {}) override;

    // [BRIDGED] -> IWindow.resized (global coordinates)
    WSError UpdateGlobalDisplayRectFromServer(const OHOS::Rosen::WSRect& rect,
        OHOS::Rosen::SizeChangeReason reason) override;

    // ================================================================
    // Category 3: Window Mode
    // ================================================================

    // [BRIDGED] -> IWindow.resized (windowing mode in config)
    WSError UpdateWindowMode(OHOS::Rosen::WindowMode mode) override;

    // [OH_ONLY] Android uses FULLSCREEN
    WSError UpdateMaximizeMode(OHOS::Rosen::MaximizeMode mode) override;

    // [OH_ONLY] Internal layout coordination
    WSError NotifyLayoutFinishAfterWindowModeChange(OHOS::Rosen::WindowMode mode) override;

    // [OH_ONLY] Free multi-window toggle
    WSError SwitchFreeMultiWindow(bool enable) override;

    // ================================================================
    // Category 4: Focus
    // ================================================================

    // [BRIDGED] -> IWindow.windowFocusChanged
    WSError UpdateFocus(const OHOS::sptr<OHOS::Rosen::FocusNotifyInfo>& focusNotifyInfo,
        bool isFocused) override;

    // [OH_ONLY] Visual feedback only
    WSError NotifyHighlightChange(const OHOS::sptr<OHOS::Rosen::HighlightNotifyInfo>& highlightNotifyInfo,
        bool isHighlight) override;

    // ================================================================
    // Category 5: Avoid Area / Insets
    // ================================================================

    // [BRIDGED] -> IWindow.insetsControlChanged
    WSError UpdateAvoidArea(const OHOS::sptr<OHOS::Rosen::AvoidArea>& avoidArea,
        OHOS::Rosen::AvoidAreaType type) override;

    // [BRIDGED] -> IWindow.insetsControlChanged (IME)
    void NotifyOccupiedAreaChangeInfo(OHOS::sptr<OHOS::Rosen::OccupiedAreaChangeInfo> info,
        const std::shared_ptr<OHOS::Rosen::RSTransaction>& rsTransaction,
        const OHOS::Rosen::Rect& callingSessionRect,
        const std::map<OHOS::Rosen::AvoidAreaType, OHOS::Rosen::AvoidArea>& avoidAreas) override;

    // ================================================================
    // Category 6: Back Event
    // ================================================================

    // [BRIDGED] -> inject KEYCODE_BACK key event
    WSError HandleBackEvent() override;

    // ================================================================
    // Category 7: Input Events
    // ================================================================

    // [OH_ONLY] Android uses InputChannel.finishInputEvent
    WSError MarkProcessed(int32_t eventId) override;

    // [OH_ONLY] Android uses ACTION_OUTSIDE via input channel
    WSError NotifyTouchOutside() override;

    // ================================================================
    // Category 8: Display / Density / Orientation
    // ================================================================

    // [BRIDGED] -> IWindow.resized (density in config)
    void UpdateDensity() override;

    // [BRIDGED] -> IWindow.resized (orientation in config)
    WSError UpdateOrientation() override;

    // [BRIDGED] -> IWindow.resized (displayId)
    WSError UpdateDisplayId(uint64_t displayId) override;

    // [OH_ONLY] Display move
    void NotifyDisplayMove(OHOS::Rosen::DisplayId from, OHOS::Rosen::DisplayId to) override;

    // [OH_ONLY] Viewport config
    WSError UpdateSessionViewportConfig(const OHOS::Rosen::SessionViewportConfig& config) override;

    // ================================================================
    // Category 9: PiP (Picture-in-Picture)
    // ================================================================

    // [OH_ONLY] PiP action event
    WSError SetPipActionEvent(const std::string& action, int32_t status) override;

    // [OH_ONLY] PiP size change
    WSError NotifyPipWindowSizeChange(double width, double height, double scale) override;

    // [OH_ONLY] PiP active status
    WSError NotifyPiPActiveStatusChange(bool status) override;

    // [OH_ONLY] PiP control event
    WSError SetPiPControlEvent(OHOS::Rosen::WsPiPControlType controlType,
        OHOS::Rosen::WsPiPControlStatus status) override;

    // [OH_ONLY] Close existing PiP window
    WSError NotifyCloseExistPipWindow() override;

    // ================================================================
    // Category 10: Rotation
    // ================================================================

    // [OH_ONLY] Set current rotation
    WSError SetCurrentRotation(int32_t currentRotation) override;

    // ================================================================
    // Category 11: Screenshot
    // ================================================================

    // [OH_ONLY] Screenshot notification
    void NotifyScreenshot() override;

    // [OH_ONLY] Screenshot app event
    WSError NotifyScreenshotAppEvent(OHOS::Rosen::ScreenshotEventType type) override;

    // ================================================================
    // Category 12: Extension / Component Data (OH_ONLY)
    // ================================================================

    // [OH_ONLY] Component data transfer
    WSError NotifyTransferComponentData(const OHOS::AAFwk::WantParams& wantParams) override;

    // [OH_ONLY] Sync component data transfer
    WSErrorCode NotifyTransferComponentDataSync(const OHOS::AAFwk::WantParams& wantParams,
        OHOS::AAFwk::WantParams& reWantParams) override;

    // [OH_ONLY] Extension secure limit
    WSError NotifyExtensionSecureLimitChange(bool isLimit) override;

    // ================================================================
    // Category 13: Debug / Diagnostic
    // ================================================================

    // [OH_ONLY] Debug dump
    void DumpSessionElementInfo(const std::vector<std::string>& params) override;

    // [OH_ONLY] Router stack info
    WMError GetRouterStackInfo(std::string& routerStackInfo) override;

    // [OH_ONLY] Top navigation destination
    WSError GetTopNavDestinationName(std::string& topNavDestName) override;

    // ================================================================
    // Category 14: Transform / UI
    // ================================================================

    // [OH_ONLY] Window transform
    void NotifyTransformChange(const OHOS::Rosen::Transform& transform) override;

    // [OH_ONLY] Single-hand transform
    void NotifySingleHandTransformChange(
        const OHOS::Rosen::SingleHandTransform& singleHandTransform) override;

    // [OH_ONLY] Dialog state
    WSError NotifyDialogStateChange(bool isForeground) override;

    // [OH_ONLY] Title bar
    WSError UpdateTitleInTargetPos(bool isShow, int32_t height) override;

    // ================================================================
    // Category 15: Remaining OH_ONLY methods
    // ================================================================

    WSError NotifyAppForceLandscapeConfigUpdated() override;
    WSError NotifyAppForceLandscapeConfigEnableUpdated(bool needUpdateViewport = false) override;
    WSError NotifyAppHookWindowInfoUpdated() override;

    // ================================================================
    // V7-only newly added pure virtuals — implemented as no-op OH_ONLY
    // (Android side has no semantic counterpart; SSM still requires the
    //  symbol to instantiate the stub.)
    // ================================================================
    WSError NotifySubWindowAfterParentWindowSizeChange(OHOS::Rosen::Rect rect) override;
    WSError NotifySubWindowAfterParentWindowStatusChange(OHOS::Rosen::WindowMode mode) override;
    WSError GetSceneNodeCount(uint32_t& nodeCount) override;
    WSError UpdateAppHookWindowInfo(const OHOS::Rosen::HookWindowInfo& hookWindowInfo) override;
    void NotifyAppUseControlStatus(bool isUseControl) override;
    void SetUniqueVirtualPixelRatio(bool useUniqueDensity, float virtualPixelRatio) override;
    void UpdateAnimationSpeed(float speed) override;
    WSError GetUIContentRemoteObj(OHOS::sptr<OHOS::IRemoteObject>& uiContentRemoteObj) override;
    WSError LinkKeyFrameNode() override;
    WSError SetStageKeyFramePolicy(const OHOS::Rosen::KeyFramePolicy& keyFramePolicy) override;
    WSError SetSplitButtonVisible(bool isVisible) override;
    WSError SetEnableDragBySystem(bool dragEnable) override;
    WSError SetDragActivated(bool dragActivated) override;
    WSError SendContainerModalEvent(const std::string& eventName,
        const std::string& eventValue) override;
    void NotifyWindowCrossAxisChange(OHOS::Rosen::CrossAxisState state) override;
    WSError SendFbActionEvent(const std::string& action) override;
    WSError UpdateIsShowDecorInFreeMultiWindow(bool isShow) override;
    WSError UpdateBrightness(float brightness) override;
    WMError UpdateWindowModeForUITest(int32_t updateMode) override;

private:
    // Obtain JNIEnv for the current thread, attaching to JVM if needed.
    JNIEnv* getJNIEnv(bool& needsDetach);

    // Detach current thread from JVM if it was attached by us.
    void detachIfNeeded(bool needsDetach);

    // Call a void method on the Java SessionStageBridge object.
    void callBridgeVoidMethod(const char* methodName, const char* signature, ...);

    JavaVM* jvm_ = nullptr;
    jobject androidWindow_ = nullptr;     // Global ref to Android IWindow
    jclass bridgeClass_ = nullptr;        // Global ref to SessionStageBridge class
    jobject bridgeObj_ = nullptr;         // Global ref to SessionStageBridge instance
    std::mutex jniMutex_;                 // Protects JNI calls from concurrent IPC threads
};

}  // namespace oh_adapter

#endif  // OH_ADAPTER_SESSION_STAGE_ADAPTER_H
