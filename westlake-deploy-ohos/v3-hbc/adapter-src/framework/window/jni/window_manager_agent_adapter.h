/*
 * window_manager_agent_adapter.h
 *
 * Reverse callback adapter: OH IWindowManagerAgent -> Android internal handling.
 *
 * Inherits from OH WindowManagerAgentStub to receive system-wide window state
 * notifications (focus changes, visibility, accessibility, etc.).
 * Android has no direct equivalent interface - notifications are handled
 * internally by WMS and dispatched through various internal mechanisms.
 *
 * Most callbacks are OH_ONLY with no Android counterpart.
 * PARTIAL callbacks are routed to internal Android mechanisms via JNI.
 */
#ifndef OH_WINDOW_MANAGER_AGENT_ADAPTER_H
#define OH_WINDOW_MANAGER_AGENT_ADAPTER_H

#include <jni.h>
#include "window_manager_agent_stub.h"

namespace oh_adapter {
using OHOS::sptr;

class WindowManagerAgentAdapter : public OHOS::Rosen::WindowManagerAgentStub {
public:
    WindowManagerAgentAdapter(JavaVM* jvm);
    ~WindowManagerAgentAdapter();

    // ============================================================
    // Category 1: Focus (-> internal WMS focus handling)
    // ============================================================

    // [PARTIAL] -> ViewRootImpl.windowFocusChanged
    void UpdateFocusChangeInfo(const sptr<OHOS::Rosen::FocusChangeInfo>& focusChangeInfo,
                               bool focused) override;

    // ============================================================
    // Category 2: Window Visibility
    // ============================================================

    // [PARTIAL] -> IWindow.dispatchAppVisibility per window
    void UpdateWindowVisibilityInfo(
        const std::vector<sptr<OHOS::Rosen::WindowVisibilityInfo>>& visibilityInfos) override;

    // [OH_ONLY]
    void UpdateVisibleWindowNum(
        const std::vector<OHOS::Rosen::VisibleWindowNumInfo>& visibleWindowNumInfo) override;

    // [OH_ONLY]
    void NotifyWindowPidVisibilityChanged(
        const sptr<OHOS::Rosen::WindowPidVisibilityInfo>& info) override;

    // ============================================================
    // Category 3: Window Mode
    // ============================================================

    // [PARTIAL] -> Configuration.windowConfiguration change
    void UpdateWindowModeTypeInfo(OHOS::Rosen::WindowModeType type) override;

    // ============================================================
    // Category 4: System Bar
    // ============================================================

    // [PARTIAL] -> Window.setStatusBarColor/setNavigationBarColor
    void UpdateSystemBarRegionTints(OHOS::Rosen::DisplayId displayId,
        const OHOS::Rosen::SystemBarRegionTints& tints) override;

    // [OH_ONLY]
    void NotifyWindowSystemBarPropertyChange(OHOS::Rosen::WindowType type,
        const OHOS::Rosen::SystemBarProperty& systemBarProperty) override;

    // ============================================================
    // Category 5: Accessibility
    // ============================================================

    // [PARTIAL] -> AccessibilityManagerService
    void NotifyAccessibilityWindowInfo(
        const std::vector<sptr<OHOS::Rosen::AccessibilityWindowInfo>>& infos,
        OHOS::Rosen::WindowUpdateType type) override;

    // ============================================================
    // Category 6: Display Group (OH_ONLY)
    // ============================================================

    void UpdateDisplayGroupInfo(OHOS::Rosen::DisplayGroupId displayGroupId,
        OHOS::Rosen::DisplayId displayId, bool isAdd) override;

    // ============================================================
    // Category 7: Camera / Float Window (OH_ONLY)
    // ============================================================

    void UpdateCameraFloatWindowStatus(uint32_t accessTokenId, bool isShowing) override;
    void UpdateCameraWindowStatus(uint32_t accessTokenId, bool isShowing) override;

    // ============================================================
    // Category 8: Window Drawing Content (OH_ONLY)
    // ============================================================

    void UpdateWindowDrawingContentInfo(
        const std::vector<sptr<OHOS::Rosen::WindowDrawingContentInfo>>& infos) override;

    // ============================================================
    // Category 9: Gesture / Style / PiP (OH_ONLY)
    // ============================================================

    void NotifyGestureNavigationEnabledResult(bool enable) override;
    void NotifyWaterMarkFlagChangedResult(bool isShowing) override;
    void NotifyWindowStyleChange(OHOS::Rosen::WindowStyleType type) override;
    void NotifyCallingWindowDisplayChanged(
        const OHOS::Rosen::CallingWindowInfo& callingWindowInfo) override;
    void UpdatePiPWindowStateChanged(const std::string& bundleName, bool isForeground) override;
    void NotifyWindowPropertyChange(uint32_t propertyDirtyFlags,
        const OHOS::Rosen::WindowInfoList& windowInfoList) override;
    void NotifySupportRotationChange(
        const OHOS::Rosen::SupportRotationInfo& supportRotationInfo) override;

private:
    JavaVM* jvm_;

    void logPartial(const char* method, const char* reason);
    void logOhOnly(const char* method, const char* reason);
};

}  // namespace oh_adapter

#endif  // OH_WINDOW_MANAGER_AGENT_ADAPTER_H
