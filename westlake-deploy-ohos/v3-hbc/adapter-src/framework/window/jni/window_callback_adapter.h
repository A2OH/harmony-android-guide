/*
 * window_callback_adapter.h
 *
 * Reverse callback adapter: OH WindowStub (IWindow) -> Android IWindow.
 *
 * This class inherits from OH WindowStub so it can be registered with
 * OH WindowManager as an IWindow callback endpoint. When OH WMS calls
 * window state methods (rect, mode, focus, avoid area, etc.), they are
 * forwarded via JNI to the Android IWindow Binder proxy.
 *
 * Threading: OH WMS calls arrive on IPC binder threads. Each override
 * attaches to the JVM via JavaVM->AttachCurrentThread before making JNI calls.
 *
 * Mapping summary:
 *   UpdateWindowRect   -> IWindow.resized(frames, ...)
 *   UpdateAvoidArea    -> IWindow.insetsChanged(InsetsState)
 *   NotifyForeground   -> IWindow.dispatchAppVisibility(true)
 *   NotifyBackground   -> IWindow.dispatchAppVisibility(false)
 *   UpdateWindowMode   -> IWindow.resized (via config change)
 *   UpdateFocusStatus  -> IWindow.windowFocusChanged(focused)
 *   NotifyDestroy      -> log only (lifecycle handled elsewhere)
 *   Other methods      -> log as OH_ONLY
 */
#ifndef OH_ADAPTER_WINDOW_CALLBACK_ADAPTER_H
#define OH_ADAPTER_WINDOW_CALLBACK_ADAPTER_H

#include <jni.h>
#include <string>
#include <mutex>

#include "window_stub.h"

namespace oh_adapter {

class WindowCallbackAdapter : public OHOS::Rosen::WindowStub {
public:
    /**
     * Construct the adapter.
     *
     * @param jvm              Pointer to the JavaVM (for AttachCurrentThread on IPC threads).
     * @param androidWindow    A Java android.view.IWindow instance. A JNI global ref
     *                         is created internally; the caller may release its local ref.
     */
    WindowCallbackAdapter(JavaVM* jvm, jobject androidWindow);
    ~WindowCallbackAdapter() override;

    // ================================================================
    // IWindow virtual method overrides - Category 1: Window Geometry
    // ================================================================

    // [BRIDGED] -> IWindow.resized(frames, ...)
    OHOS::Rosen::WMError UpdateWindowRect(const struct OHOS::Rosen::Rect& rect,
        bool decoStatus, OHOS::Rosen::WindowSizeChangeReason reason,
        const std::shared_ptr<OHOS::Rosen::RSTransaction>& rsTransaction = nullptr) override;

    // [BRIDGED] -> IWindow.resized (via config change, windowing mode)
    OHOS::Rosen::WMError UpdateWindowMode(OHOS::Rosen::WindowMode mode) override;

    // [OH_ONLY] Android WMS manages mode constraints internally
    OHOS::Rosen::WMError UpdateWindowModeSupportType(uint32_t windowModeSupportType) override;

    // ================================================================
    // Category 2: Focus
    // ================================================================

    // [BRIDGED] -> IWindow.windowFocusChanged(focused)
    OHOS::Rosen::WMError UpdateFocusStatus(bool focused) override;

    // [OH_ONLY] Active status handled internally by Android
    OHOS::Rosen::WMError UpdateActiveStatus(bool isActive) override;

    // ================================================================
    // Category 3: Avoid Area / Insets
    // ================================================================

    // [BRIDGED] -> IWindow.insetsChanged(InsetsState)
    OHOS::Rosen::WMError UpdateAvoidArea(const OHOS::sptr<OHOS::Rosen::AvoidArea>& avoidArea,
        OHOS::Rosen::AvoidAreaType type) override;

    // [BRIDGED] -> IWindow.insetsControlChanged (IME)
    OHOS::Rosen::WMError UpdateOccupiedAreaChangeInfo(
        const OHOS::sptr<OHOS::Rosen::OccupiedAreaChangeInfo>& info,
        const std::map<OHOS::Rosen::AvoidAreaType, OHOS::Rosen::AvoidArea>& avoidAreas,
        const std::shared_ptr<OHOS::Rosen::RSTransaction>& rsTransaction = nullptr) override;

    // [BRIDGED] -> IWindow.resized (combined insets + rect)
    OHOS::Rosen::WMError UpdateOccupiedAreaAndRect(
        const OHOS::sptr<OHOS::Rosen::OccupiedAreaChangeInfo>& info,
        const OHOS::Rosen::Rect& rect,
        const std::map<OHOS::Rosen::AvoidAreaType, OHOS::Rosen::AvoidArea>& avoidAreas,
        const std::shared_ptr<OHOS::Rosen::RSTransaction>& rsTransaction = nullptr) override;

    // ================================================================
    // Category 4: Visibility / Lifecycle
    // ================================================================

    // [BRIDGED] -> IWindow.dispatchAppVisibility(visible)
    OHOS::Rosen::WMError UpdateWindowState(OHOS::Rosen::WindowState state) override;

    // [BRIDGED] -> IWindow.dispatchAppVisibility(true)
    OHOS::Rosen::WMError NotifyForeground(void) override;

    // [BRIDGED] -> IWindow.dispatchAppVisibility(false)
    OHOS::Rosen::WMError NotifyBackground(void) override;

    // [BRIDGED] -> IWindow.dispatchAppVisibility(interactive)
    void NotifyForegroundInteractiveStatus(bool interactive) override;

    // [LOG_ONLY] Lifecycle handled elsewhere
    OHOS::Rosen::WMError NotifyDestroy(void) override;

    // ================================================================
    // Category 5: Drag / Input
    // ================================================================

    // [BRIDGED] -> IWindow.dispatchDragEvent
    OHOS::Rosen::WMError UpdateWindowDragInfo(const OHOS::Rosen::PointInfo& point,
        OHOS::Rosen::DragEvent event) override;

    // [OH_ONLY] Android uses input channel for pointer events
    OHOS::Rosen::WMError NotifyWindowClientPointUp(
        const std::shared_ptr<OHOS::MMI::PointerEvent>& pointerEvent) override;

    // [OH_ONLY] Android uses input channel for key events
    void ConsumeKeyEvent(std::shared_ptr<OHOS::MMI::KeyEvent> event) override;

    // ================================================================
    // Category 6: Display
    // ================================================================

    // [BRIDGED] -> IWindow.resized (displayId)
    OHOS::Rosen::WMError UpdateDisplayId(OHOS::Rosen::DisplayId from,
        OHOS::Rosen::DisplayId to) override;

    // ================================================================
    // Category 7: Screenshot / Debug / Misc
    // ================================================================

    // [OH_ONLY] Android uses file observer or Activity callback
    OHOS::Rosen::WMError NotifyScreenshot() override;

    // [OH_ONLY] Android uses different screenshot notification
    OHOS::Rosen::WMError NotifyScreenshotAppEvent(OHOS::Rosen::ScreenshotEventType type) override;

    // [OH_ONLY] Android uses ACTION_OUTSIDE via input channel
    OHOS::Rosen::WMError NotifyTouchOutside() override;

    // [OH_ONLY] Android uses WindowManager.LayoutParams
    OHOS::sptr<OHOS::Rosen::WindowProperty> GetWindowProperty() override;

    // [OH_ONLY] Debug dump
    OHOS::Rosen::WMError DumpInfo(const std::vector<std::string>& params) override;

    // [OH_ONLY] Accessibility zoom
    OHOS::Rosen::WMError UpdateZoomTransform(const OHOS::Rosen::Transform& trans,
        bool isDisplayZoomOn) override;

    // [OH_ONLY] Split screen managed by WMS
    OHOS::Rosen::WMError RestoreSplitWindowMode(uint32_t mode) override;

    // [OH_ONLY] OH Multi-Modal Input service
    void NotifyMMIServiceOnline(uint32_t winId) override;

private:
    // Obtain JNIEnv for the current thread, attaching to JVM if needed.
    // @param needsDetach  Set to true if the thread was newly attached.
    JNIEnv* getJNIEnv(bool& needsDetach);

    // Detach current thread from JVM if it was attached by us.
    void detachIfNeeded(bool needsDetach);

    // Call a void method on the Java WindowCallbackBridge object.
    void callBridgeVoidMethod(const char* methodName, const char* signature, ...);

    JavaVM* jvm_ = nullptr;
    jobject androidWindow_ = nullptr;     // Global ref to Android IWindow
    jclass bridgeClass_ = nullptr;        // Global ref to WindowCallbackBridge class
    jobject bridgeObj_ = nullptr;         // Global ref to WindowCallbackBridge instance
    std::mutex jniMutex_;                 // Protects JNI calls from concurrent IPC threads
};

}  // namespace oh_adapter

#endif  // OH_ADAPTER_WINDOW_CALLBACK_ADAPTER_H
