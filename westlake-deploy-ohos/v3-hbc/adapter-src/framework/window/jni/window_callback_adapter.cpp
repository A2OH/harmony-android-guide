/*
 * window_callback_adapter.cpp
 *
 * Implementation of WindowCallbackAdapter.
 * Bridges OH IWindow callbacks to Android IWindow via JNI.
 */

#include "window_callback_adapter.h"
#include "adapter_bridge.h"

#include <android/log.h>
#include <cstdarg>

#include "oh_br_trace.h"   // G2.14ac IPC trace+log macros

#define LOG_TAG "OH_WindowAdapter"
#define ALOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define ALOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define ALOGW(...) __android_log_print(ANDROID_LOG_WARN,  LOG_TAG, __VA_ARGS__)
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

namespace oh_adapter {

// ================================================================
// Construction / Destruction
// ================================================================

WindowCallbackAdapter::WindowCallbackAdapter(JavaVM* jvm, jobject androidWindow)
    : jvm_(jvm)
{
    ALOGI("WindowCallbackAdapter created");

    JNIEnv* env = nullptr;
    bool needsDetach = false;
    env = getJNIEnv(needsDetach);
    if (!env) {
        ALOGE("Failed to get JNIEnv in constructor");
        return;
    }

    // Store Android IWindow as a global reference
    androidWindow_ = env->NewGlobalRef(androidWindow);

    // G2.14d ROOT CAUSE FIX:
    // - FindClass path was wrong: "adapter/bridge/callback/WindowCallbackBridge"
    //   does not exist — actual package is adapter.window per Java source
    //   (framework/window/java/WindowCallbackBridge.java line 18:
    //   `package adapter.window;`).
    // - When FindClass failed, ART set a pending ClassNotFoundException in
    //   JNIEnv. This ctor early-returned WITHOUT calling ExceptionClear().
    //   The pending exception then sat in JNIEnv. When the outer JNI
    //   `Java_..._nativeCreateSession` returned to Java, ART surfaced the
    //   pending exception as preallocated NoClassDefFoundError → addToDisplay
    //   threw NCDFE for an entirely synthetic reason (the actual createSession
    //   succeeded — windowId=12 was allocated by WMS and AddWindow succeeded
    //   too). HelloWorld saw `J_invokeStaticMain_main_threw NCDFE` 6 ms after
    //   `createSession success` log.
    // - Defensive ExceptionClear after every JNI call that can fail keeps
    //   bridgeObj_ as null but does not corrupt JNIEnv exception state.
    //   The reverse callbacks are non-critical for HelloWorld baseline; if
    //   bridgeObj_ stays null, callBridgeVoidMethod no-ops gracefully.
    jclass localBridgeClass = env->FindClass("adapter/window/WindowCallbackBridge");
    if (!localBridgeClass) {
        ALOGE("FindClass(adapter/window/WindowCallbackBridge) failed");
        if (env->ExceptionCheck()) env->ExceptionClear();
        detachIfNeeded(needsDetach);
        return;
    }
    bridgeClass_ = reinterpret_cast<jclass>(env->NewGlobalRef(localBridgeClass));
    env->DeleteLocalRef(localBridgeClass);

    jmethodID ctor = env->GetMethodID(bridgeClass_, "<init>",
        "(Ljava/lang/Object;)V");
    if (!ctor) {
        ALOGE("GetMethodID(WindowCallbackBridge.<init>) failed");
        if (env->ExceptionCheck()) env->ExceptionClear();
        detachIfNeeded(needsDetach);
        return;
    }

    jobject localBridgeObj = env->NewObject(bridgeClass_, ctor, androidWindow_);
    if (!localBridgeObj) {
        ALOGE("NewObject(WindowCallbackBridge) failed");
        if (env->ExceptionCheck()) env->ExceptionClear();
        detachIfNeeded(needsDetach);
        return;
    }
    bridgeObj_ = env->NewGlobalRef(localBridgeObj);
    env->DeleteLocalRef(localBridgeObj);

    detachIfNeeded(needsDetach);
    ALOGI("WindowCallbackAdapter initialized successfully");
}

WindowCallbackAdapter::~WindowCallbackAdapter()
{
    ALOGI("WindowCallbackAdapter destroyed");

    JNIEnv* env = nullptr;
    bool needsDetach = false;
    env = getJNIEnv(needsDetach);
    if (env) {
        if (androidWindow_) {
            env->DeleteGlobalRef(androidWindow_);
            androidWindow_ = nullptr;
        }
        if (bridgeObj_) {
            env->DeleteGlobalRef(bridgeObj_);
            bridgeObj_ = nullptr;
        }
        if (bridgeClass_) {
            env->DeleteGlobalRef(bridgeClass_);
            bridgeClass_ = nullptr;
        }
        detachIfNeeded(needsDetach);
    }
}

// ================================================================
// Category 1: Window Geometry
// ================================================================

OHOS::Rosen::WMError WindowCallbackAdapter::UpdateWindowRect(
    const struct OHOS::Rosen::Rect& rect, bool decoStatus,
    OHOS::Rosen::WindowSizeChangeReason reason,
    const std::shared_ptr<OHOS::Rosen::RSTransaction>& /*rsTransaction*/)
{
    OH_BR_IPC_SCOPE("WindowCB.UpdateWindowRect",
                    "rect=[%{public}d,%{public}d,%{public}d,%{public}d] deco=%{public}d reason=%{public}d",
                    rect.posX_, rect.posY_, rect.width_, rect.height_,
                    static_cast<int>(decoStatus), static_cast<int>(reason));

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onUpdateWindowRect", "(IIIIZI)V",
        rect.posX_, rect.posY_,
        rect.posX_ + rect.width_,
        rect.posY_ + rect.height_,
        static_cast<jboolean>(decoStatus),
        static_cast<jint>(reason));

    return OHOS::Rosen::WMError::WM_OK;
}

OHOS::Rosen::WMError WindowCallbackAdapter::UpdateWindowMode(OHOS::Rosen::WindowMode mode)
{
    OH_BR_IPC_SCOPE("WindowCB.UpdateWindowMode", "mode=%{public}d", static_cast<int>(mode));

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onUpdateWindowMode", "(I)V", static_cast<jint>(mode));

    return OHOS::Rosen::WMError::WM_OK;
}

OHOS::Rosen::WMError WindowCallbackAdapter::UpdateWindowModeSupportType(uint32_t windowModeSupportType)
{
    OH_BR_IPC_SCOPE("WindowCB.UpdateWindowModeSupportType",
                    "supportType=%{public}u (Android WMS manages internally)",
                    windowModeSupportType);

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onUpdateWindowModeSupportType", "(I)V",
        static_cast<jint>(windowModeSupportType));

    return OHOS::Rosen::WMError::WM_OK;
}

// ================================================================
// Category 2: Focus
// ================================================================

OHOS::Rosen::WMError WindowCallbackAdapter::UpdateFocusStatus(bool focused)
{
    OH_BR_IPC_SCOPE("WindowCB.UpdateFocusStatus", "focused=%{public}d", static_cast<int>(focused));

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onUpdateFocusStatus", "(Z)V", static_cast<jboolean>(focused));

    return OHOS::Rosen::WMError::WM_OK;
}

OHOS::Rosen::WMError WindowCallbackAdapter::UpdateActiveStatus(bool isActive)
{
    OH_BR_IPC_SCOPE("WindowCB.UpdateActiveStatus", "isActive=%{public}d", static_cast<int>(isActive));

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onUpdateActiveStatus", "(Z)V", static_cast<jboolean>(isActive));

    return OHOS::Rosen::WMError::WM_OK;
}

// ================================================================
// Category 3: Avoid Area / Insets
// ================================================================

OHOS::Rosen::WMError WindowCallbackAdapter::UpdateAvoidArea(
    const OHOS::sptr<OHOS::Rosen::AvoidArea>& avoidArea, OHOS::Rosen::AvoidAreaType type)
{
    OH_BR_IPC_SCOPE("WindowCB.UpdateAvoidArea", "type=%{public}d", static_cast<int>(type));

    if (!avoidArea) {
        ALOGW("UpdateAvoidArea: avoidArea is null");
        return OHOS::Rosen::WMError::WM_ERROR_NULLPTR;
    }

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onUpdateAvoidArea", "(IIIII)V",
        static_cast<jint>(type),
        static_cast<jint>(avoidArea->topRect_.posX_),
        static_cast<jint>(avoidArea->topRect_.posY_),
        static_cast<jint>(avoidArea->bottomRect_.posX_ + avoidArea->bottomRect_.width_),
        static_cast<jint>(avoidArea->bottomRect_.posY_ + avoidArea->bottomRect_.height_));

    return OHOS::Rosen::WMError::WM_OK;
}

OHOS::Rosen::WMError WindowCallbackAdapter::UpdateOccupiedAreaChangeInfo(
    const OHOS::sptr<OHOS::Rosen::OccupiedAreaChangeInfo>& info,
    const std::map<OHOS::Rosen::AvoidAreaType, OHOS::Rosen::AvoidArea>& /*avoidAreas*/,
    const std::shared_ptr<OHOS::Rosen::RSTransaction>& /*rsTransaction*/)
{
    int occupiedHeight = 0;
    if (info) {
        occupiedHeight = info->rect_.height_;
    }
    OH_BR_IPC_SCOPE("WindowCB.UpdateOccupiedAreaChangeInfo",
                    "occupiedHeight=%{public}d", occupiedHeight);

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onUpdateOccupiedAreaChangeInfo", "(I)V",
        static_cast<jint>(occupiedHeight));

    return OHOS::Rosen::WMError::WM_OK;
}

OHOS::Rosen::WMError WindowCallbackAdapter::UpdateOccupiedAreaAndRect(
    const OHOS::sptr<OHOS::Rosen::OccupiedAreaChangeInfo>& /*info*/,
    const OHOS::Rosen::Rect& /*rect*/,
    const std::map<OHOS::Rosen::AvoidAreaType, OHOS::Rosen::AvoidArea>& /*avoidAreas*/,
    const std::shared_ptr<OHOS::Rosen::RSTransaction>& /*rsTransaction*/)
{
    OH_BR_IPC_SCOPE("WindowCB.UpdateOccupiedAreaAndRect", "");

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onUpdateOccupiedAreaAndRect", "()V");

    return OHOS::Rosen::WMError::WM_OK;
}

// ================================================================
// Category 4: Visibility / Lifecycle
// ================================================================

OHOS::Rosen::WMError WindowCallbackAdapter::UpdateWindowState(OHOS::Rosen::WindowState state)
{
    OH_BR_IPC_SCOPE("WindowCB.UpdateWindowState", "state=%{public}d", static_cast<int>(state));

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onUpdateWindowState", "(I)V", static_cast<jint>(state));

    return OHOS::Rosen::WMError::WM_OK;
}

OHOS::Rosen::WMError WindowCallbackAdapter::NotifyForeground(void)
{
    OH_BR_IPC_SCOPE("WindowCB.NotifyForeground", "");

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onNotifyForeground", "()V");

    return OHOS::Rosen::WMError::WM_OK;
}

OHOS::Rosen::WMError WindowCallbackAdapter::NotifyBackground(void)
{
    OH_BR_IPC_SCOPE("WindowCB.NotifyBackground", "");

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onNotifyBackground", "()V");

    return OHOS::Rosen::WMError::WM_OK;
}

void WindowCallbackAdapter::NotifyForegroundInteractiveStatus(bool interactive)
{
    OH_BR_IPC_SCOPE("WindowCB.NotifyForegroundInteractiveStatus",
                    "interactive=%{public}d", static_cast<int>(interactive));

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onNotifyForegroundInteractiveStatus", "(Z)V",
        static_cast<jboolean>(interactive));
}

OHOS::Rosen::WMError WindowCallbackAdapter::NotifyDestroy(void)
{
    OH_BR_IPC_SCOPE("WindowCB.NotifyDestroy",
                    "(LOG_ONLY: lifecycle handled by Activity/ViewRootImpl)");
    // Destruction is managed at a higher level via Activity lifecycle.
    // Intentionally not forwarding to Android IWindow.
    return OHOS::Rosen::WMError::WM_OK;
}

// ================================================================
// Category 5: Drag / Input
// ================================================================

OHOS::Rosen::WMError WindowCallbackAdapter::UpdateWindowDragInfo(
    const OHOS::Rosen::PointInfo& point, OHOS::Rosen::DragEvent event)
{
    OH_BR_IPC_SCOPE("WindowCB.UpdateWindowDragInfo",
                    "x=%{public}.1f y=%{public}.1f event=%{public}d",
                    point.x, point.y, static_cast<int>(event));

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onUpdateWindowDragInfo", "(FFI)V",
        static_cast<jfloat>(point.x),
        static_cast<jfloat>(point.y),
        static_cast<jint>(event));

    return OHOS::Rosen::WMError::WM_OK;
}

OHOS::Rosen::WMError WindowCallbackAdapter::NotifyWindowClientPointUp(
    const std::shared_ptr<OHOS::MMI::PointerEvent>& /*pointerEvent*/)
{
    OH_BR_IPC_SCOPE("WindowCB.NotifyWindowClientPointUp",
                    "(OH_ONLY: Android uses input channel for touch)");

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onNotifyWindowClientPointUp", "()V");

    return OHOS::Rosen::WMError::WM_OK;
}

void WindowCallbackAdapter::ConsumeKeyEvent(std::shared_ptr<OHOS::MMI::KeyEvent> /*event*/)
{
    OH_BR_IPC_SCOPE("WindowCB.ConsumeKeyEvent",
                    "(OH_ONLY: Android uses input channel for keys)");

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onConsumeKeyEvent", "()V");
}

// ================================================================
// Category 6: Display
// ================================================================

OHOS::Rosen::WMError WindowCallbackAdapter::UpdateDisplayId(
    OHOS::Rosen::DisplayId from, OHOS::Rosen::DisplayId to)
{
    OH_BR_IPC_SCOPE("WindowCB.UpdateDisplayId",
                    "from=%{public}llu to=%{public}llu",
                    static_cast<unsigned long long>(from),
                    static_cast<unsigned long long>(to));

    std::lock_guard<std::mutex> lock(jniMutex_);
    callBridgeVoidMethod("onUpdateDisplayId", "(JJ)V",
        static_cast<jlong>(from), static_cast<jlong>(to));

    return OHOS::Rosen::WMError::WM_OK;
}

// ================================================================
// Category 7: Screenshot / Debug / Misc (all OH_ONLY)
// ================================================================

OHOS::Rosen::WMError WindowCallbackAdapter::NotifyScreenshot()
{
    OH_BR_IPC_SCOPE("WindowCB.NotifyScreenshot",
                    "(OH_ONLY: Android uses file observer or Activity callback)");
    return OHOS::Rosen::WMError::WM_OK;
}

OHOS::Rosen::WMError WindowCallbackAdapter::NotifyScreenshotAppEvent(
    OHOS::Rosen::ScreenshotEventType type)
{
    OH_BR_IPC_SCOPE("WindowCB.NotifyScreenshotAppEvent",
                    "type=%{public}d (OH_ONLY)", static_cast<int>(type));
    return OHOS::Rosen::WMError::WM_OK;
}

OHOS::Rosen::WMError WindowCallbackAdapter::NotifyTouchOutside()
{
    OH_BR_IPC_SCOPE("WindowCB.NotifyTouchOutside",
                    "(OH_ONLY: Android uses ACTION_OUTSIDE via input channel)");
    return OHOS::Rosen::WMError::WM_OK;
}

OHOS::sptr<OHOS::Rosen::WindowProperty> WindowCallbackAdapter::GetWindowProperty()
{
    OH_BR_IPC_SCOPE("WindowCB.GetWindowProperty",
                    "(OH_ONLY: Android uses WindowManager.LayoutParams)");
    return nullptr;
}

OHOS::Rosen::WMError WindowCallbackAdapter::DumpInfo(const std::vector<std::string>& params)
{
    OH_BR_IPC_SCOPE("WindowCB.DumpInfo", "paramCount=%{public}zu", params.size());
    return OHOS::Rosen::WMError::WM_OK;
}

OHOS::Rosen::WMError WindowCallbackAdapter::UpdateZoomTransform(
    const OHOS::Rosen::Transform& /*trans*/, bool isDisplayZoomOn)
{
    OH_BR_IPC_SCOPE("WindowCB.UpdateZoomTransform",
                    "zoomOn=%{public}d (OH_ONLY)", static_cast<int>(isDisplayZoomOn));
    return OHOS::Rosen::WMError::WM_OK;
}

OHOS::Rosen::WMError WindowCallbackAdapter::RestoreSplitWindowMode(uint32_t mode)
{
    OH_BR_IPC_SCOPE("WindowCB.RestoreSplitWindowMode",
                    "mode=%{public}u (OH_ONLY)", mode);
    return OHOS::Rosen::WMError::WM_OK;
}

void WindowCallbackAdapter::NotifyMMIServiceOnline(uint32_t winId)
{
    OH_BR_IPC_SCOPE("WindowCB.NotifyMMIServiceOnline",
                    "winId=%{public}u (OH_ONLY)", winId);
}

// ================================================================
// JNI Helpers
// ================================================================

JNIEnv* WindowCallbackAdapter::getJNIEnv(bool& needsDetach)
{
    needsDetach = false;
    if (!jvm_) {
        ALOGE("getJNIEnv: JavaVM is null");
        return nullptr;
    }

    JNIEnv* env = nullptr;
    jint result = jvm_->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
    if (result == JNI_OK) {
        return env;
    }

    if (result == JNI_EDETACHED) {
        JavaVMAttachArgs args;
        args.version = JNI_VERSION_1_6;
        args.name = "OH_WindowAdapter";
        args.group = nullptr;
        result = jvm_->AttachCurrentThread(&env, &args);
        if (result == JNI_OK) {
            needsDetach = true;
            return env;
        }
        ALOGE("getJNIEnv: AttachCurrentThread failed (result=%d)", result);
    }

    return nullptr;
}

void WindowCallbackAdapter::detachIfNeeded(bool needsDetach)
{
    if (needsDetach && jvm_) {
        jvm_->DetachCurrentThread();
    }
}

void WindowCallbackAdapter::callBridgeVoidMethod(
    const char* methodName, const char* signature, ...)
{
    if (!bridgeObj_ || !bridgeClass_) {
        ALOGW("callBridgeVoidMethod(%s): bridge not initialized", methodName);
        return;
    }

    bool needsDetach = false;
    JNIEnv* env = getJNIEnv(needsDetach);
    if (!env) {
        ALOGE("callBridgeVoidMethod(%s): failed to get JNIEnv", methodName);
        return;
    }

    jmethodID method = env->GetMethodID(bridgeClass_, methodName, signature);
    if (!method) {
        ALOGE("callBridgeVoidMethod: method %s%s not found", methodName, signature);
        env->ExceptionClear();
        detachIfNeeded(needsDetach);
        return;
    }

    va_list args;
    va_start(args, signature);
    env->CallVoidMethodV(bridgeObj_, method, args);
    va_end(args);

    if (env->ExceptionCheck()) {
        ALOGE("callBridgeVoidMethod(%s): Java exception occurred", methodName);
        env->ExceptionDescribe();
        env->ExceptionClear();
    }

    detachIfNeeded(needsDetach);
}

}  // namespace oh_adapter
