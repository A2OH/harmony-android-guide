/*
 * oh_input_manager_bridge.h
 *
 * Native bridge for adapter.window.InputManagerAdapter (IInputManager).
 *
 * This is the CONTROL plane bridge to OH MultiModalInput (MMI) inner_api.
 * It is INDEPENDENT of (and PEER to) oh_input_bridge.{cpp,h}, which is the
 * per-session DATA plane bridge.  Collaboration between the two — when
 * InputManagerAdapter.injectInputEvent receives a MotionEvent — happens via
 * static delegation: the .cpp calls
 *     oh_adapter::OHInputBridge::getInstance().injectTouchEvent(...)
 * but the two singletons share no inheritance, no fields, no headers beyond
 * that single delegate include.
 *
 * See doc/Input_Adapter_design.html §5.7 for the full rationale.
 *
 * Phase 1 scope (G2.14v / 2026-05-07):
 *   - Expose 12 A-tier Java_adapter_window_InputManagerAdapter_native* JNI
 *     symbols so InputManagerGlobal.sService is reachable without NPE.
 *   - nativeInit returns a non-zero handle so Java side treats the bridge
 *     as "available".  Real OH MMI binding lands in Phase 2.
 *   - All A-tier methods return NO_FEATURE-safe defaults until Phase 2.
 *
 * No register_X entry — the JNI symbols are exported with the standard
 * Java_<pkg>_<class>_<method> naming scheme so ART's auto-dlsym binds them
 * (same pattern as InputEventBridge / WindowSessionAdapter natives).
 */
#ifndef OH_INPUT_MANAGER_BRIDGE_H
#define OH_INPUT_MANAGER_BRIDGE_H

#include <jni.h>
#include <cstdint>
#include <mutex>
#include <vector>

// Forward declaration to avoid leaking <input_manager.h> into headers
// that include this file (only the .cpp talks to OH MMI directly).
namespace OHOS { namespace MMI { class InputManager; } }

namespace oh_adapter {

class OHInputManagerBridge {
public:
    static OHInputManagerBridge& getInstance();

    // init: probes OH MMI singleton availability and stores the handle
    // (OHOS::MMI::InputManager*).  Returns true if MMI is reachable, false
    // if MMI service unreachable (caller stays operational — bridge methods
    // simply route to no-op or return safe defaults).
    bool init();

    // Accessor for the MMI singleton handle.  Returns nullptr if init() did
    // not succeed.  Used by JNI exports that need direct MMI calls (e.g.
    // GetDevice path inside nativeGetInputDevice).
    OHOS::MMI::InputManager* mmiInstance() const;

    // Phase 2 will populate these with real OH MMI calls.  Phase 1 returns
    // safe defaults so the Java side never NPEs.
    std::vector<int32_t> getInputDeviceIds();

    // Find an OH device that satisfies (IsVirtual() && keyboard-capable) —
    // i.e. a device App-side previously registered via AddVirtualInputDevice
    // and that carries keyboard capability.  Used to map OH-side virtual
    // keyboard onto Android's KeyCharacterMap.VIRTUAL_KEYBOARD (-1).
    // Returns the OH device id, or -1 if no such device exists (in which
    // case the Java caller falls back to a local-construction minimal
    // InputDevice per Input_Adapter_design §5.3 B-tier).
    int32_t findVirtualKeyboardOhId();

    bool isInputDeviceEnabled(int32_t deviceId);
    void enableInputDevice(int32_t deviceId, bool enabled);
    bool hasKeys(int32_t deviceId, const std::vector<int32_t>& keys, std::vector<bool>& exists);

    bool injectKeyEvent(int32_t deviceId, int32_t action, int32_t keyCode,
                        int32_t metaState, int64_t downTime, int64_t eventTime,
                        int32_t repeatCount, int32_t mode);
    // MotionEvent inject delegates to oh_input_bridge.cpp's per-session data
    // plane (see Input_Adapter_design §5.7.5).
    bool injectMotionEvent(int32_t deviceId, int32_t action, float x, float y,
                           int64_t downTime, int64_t eventTime, int32_t metaState,
                           int32_t buttonState, int32_t mode, int32_t targetUid);

    void setPointerSpeed(int32_t speed);
    void setPointerIconType(int32_t typeId);
    void setCustomPointerIcon(JNIEnv* env, jobject icon);

    void registerDeviceListener(JNIEnv* env, jobject javaListener);

private:
    OHInputManagerBridge() = default;
    ~OHInputManagerBridge() = default;
    OHInputManagerBridge(const OHInputManagerBridge&) = delete;
    OHInputManagerBridge& operator=(const OHInputManagerBridge&) = delete;

    std::mutex mutex_;
    bool initialized_ = false;
    void* mmiHandle_ = nullptr;       // OHOS::MMI::InputManager*  (Phase 2)
    jobject javaDeviceListener_ = nullptr;  // NewGlobalRef in Phase 2
};

}  // namespace oh_adapter

#endif  // OH_INPUT_MANAGER_BRIDGE_H
