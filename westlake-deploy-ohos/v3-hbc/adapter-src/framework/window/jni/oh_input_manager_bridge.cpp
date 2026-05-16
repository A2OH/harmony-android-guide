/*
 * oh_input_manager_bridge.cpp
 *
 * Native bridge implementation for adapter.window.InputManagerAdapter.
 *
 * Phase 1 (G2.14v / 2026-05-07):
 *   - Exposes 12 A-tier Java_adapter_window_InputManagerAdapter_native*
 *     symbols with NO_FEATURE-safe defaults so InputManagerGlobal is
 *     reachable without NPE.
 *   - MotionEvent injection statically delegates to oh_input_bridge.cpp's
 *     per-session data plane (cf. Input_Adapter_design §5.7.5).
 *   - OH MMI inner_api handle (OHOS::MMI::InputManager*) probe is stubbed:
 *     init() returns true unconditionally.  Phase 2 will replace the body
 *     with InputManager::GetInstance() and the 4 A-tier real bridges
 *     (GetDeviceIds / GetDevice / SupportKeys / RegisterDeviceListener).
 *
 * Why no #include "input_manager.h" yet:
 *   - Phase 1 contract is ONLY "InputManagerGlobal != null + no NPE".
 *   - Real MMI calls require precise inner_api signature alignment that
 *     belongs to Phase 2 work; pulling the header in now risks ECS build
 *     break on signature drift in this OH 7.0.0.18 strict branch.
 *   - The compile script + BUILD.gn already expose the MMI inner_api
 *     include path / -lmmi-client.z deps so Phase 2 can flip on without
 *     further build infra changes.
 */
#include "oh_input_manager_bridge.h"
#include "oh_input_bridge.h"  // ONLY cross-bridge include — for static delegate

// OH MMI inner_api — true-bridge to MultiModalInput service.
// libmmi-client.z.so via "input:libmmi-client" external_dep in BUILD.gn.
#include "input_manager.h"
#include "input_device.h"

#include <chrono>
#include <future>
#include <memory>

// Aligned with oh_window_manager_client.cpp (B.37 sediment template) and
// memory feedback_native_log_use_hilogprint.md: adapter native log MUST go
// through OH HiLogPrint (innerAPI), not __android_log_print — OH has no
// logd, so __android_log_print payload is silently dropped and any cpp-side
// LOG diagnosis is invisible.
#include "hilog/log.h"
#define LOG_TAG "OH_IIMBridge"
#define LOGI(fmt, ...) HiLogPrint(LOG_CORE, LOG_INFO,  0xD000F00u, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGW(fmt, ...) HiLogPrint(LOG_CORE, LOG_WARN,  0xD000F00u, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGE(fmt, ...) HiLogPrint(LOG_CORE, LOG_ERROR, 0xD000F00u, LOG_TAG, fmt, ##__VA_ARGS__)

namespace oh_adapter {

OHInputManagerBridge& OHInputManagerBridge::getInstance() {
    static OHInputManagerBridge instance;
    return instance;
}

bool OHInputManagerBridge::init() {
    std::lock_guard<std::mutex> lk(mutex_);
    if (initialized_) return true;
    // True bridge to OH MMI inner_api singleton.
    LOGI("[DEBUG] init: calling OHOS::MMI::InputManager::GetInstance()");
    auto* mmi = OHOS::MMI::InputManager::GetInstance();
    if (!mmi) {
        LOGE("[DEBUG] init: OHOS::MMI::InputManager::GetInstance() returned null — MMI service unreachable");
        return false;
    }
    mmiHandle_ = static_cast<void*>(mmi);
    initialized_ = true;
    LOGI("[DEBUG] init: OK, MMI handle=%p", mmiHandle_);
    return true;
}

// ============================================================
// A-tier methods — Phase 1 NO_FEATURE-safe defaults
// ============================================================

std::vector<int32_t> OHInputManagerBridge::getInputDeviceIds() {
    // True bridge: MMI::GetDeviceIds is async-callback style; sync-wrap with
    // std::promise + 200ms timeout per Input_Adapter_design §6.3.
    auto* mmi = static_cast<OHOS::MMI::InputManager*>(mmiHandle_);
    if (!mmi) {
        LOGE("[DEBUG] getInputDeviceIds: mmiHandle is null (init not called or failed)");
        return {};
    }
    LOGI("[DEBUG] getInputDeviceIds: calling MMI::GetDeviceIds");
    auto p = std::make_shared<std::promise<std::vector<int32_t>>>();
    auto f = p->get_future();
    int32_t rc = mmi->GetDeviceIds([p](std::vector<int32_t>& ids) {
        p->set_value(ids);
    });
    if (rc != 0) {
        LOGE("[DEBUG] getInputDeviceIds: MMI::GetDeviceIds failed rc=%d", rc);
        return {};
    }
    if (f.wait_for(std::chrono::milliseconds(200)) != std::future_status::ready) {
        LOGE("[DEBUG] getInputDeviceIds: MMI::GetDeviceIds timeout (200ms) — async callback never fired");
        return {};
    }
    auto ids = f.get();
    LOGI("[DEBUG] getInputDeviceIds: OK, got %zu device id(s)", ids.size());
    return ids;
}

// Internal accessor: the MMI singleton handle stored by init().  Returns
// nullptr if init() failed (MMI service unreachable).
OHOS::MMI::InputManager* OHInputManagerBridge::mmiInstance() const {
    return static_cast<OHOS::MMI::InputManager*>(mmiHandle_);
}

// Forward declaration for getOHInputDevice (defined later in this same file
// as a namespace-internal static helper) — needed because the member fn
// findVirtualKeyboardOhId below depends on it but precedes its definition.
static std::shared_ptr<OHOS::MMI::InputDevice> getOHInputDevice(
        OHOS::MMI::InputManager* mmi, int32_t deviceId);

// Find an OH device satisfying IsVirtual() + keyboard capability.  Maps to
// Android's VIRTUAL_KEYBOARD (-1) when found.  Returns -1 if OH has no
// such device (Java caller falls back to local minimal InputDevice).
int32_t OHInputManagerBridge::findVirtualKeyboardOhId() {
    auto* mmi = static_cast<OHOS::MMI::InputManager*>(mmiHandle_);
    if (!mmi) {
        LOGE("[DEBUG] findVirtualKeyboardOhId: mmi null");
        return -1;
    }
    auto ids = getInputDeviceIds();  // sync-wrapped GetDeviceIds
    LOGI("[DEBUG] findVirtualKeyboardOhId: scanning %zu device(s)", ids.size());
    for (int32_t id : ids) {
        // getOHInputDevice is a file-internal static helper in this same
        // namespace — call without the namespace prefix.
        auto dev = getOHInputDevice(mmi, id);  // sync-wrapped GetDevice
        if (!dev) continue;
        bool isVirtual = dev->IsVirtual();
        bool hasKeyboard = (dev->GetCapabilities() &
                            (1u << OHOS::MMI::INPUT_DEV_CAP_KEYBOARD)) != 0;
        LOGI("[DEBUG] findVirtualKeyboardOhId: id=%d virtual=%d keyboard=%d",
             id, (int)isVirtual, (int)hasKeyboard);
        if (isVirtual && hasKeyboard) {
            LOGI("[DEBUG] findVirtualKeyboardOhId: matched OH virtual keyboard id=%d", id);
            return id;
        }
    }
    LOGI("[DEBUG] findVirtualKeyboardOhId: no OH virtual keyboard found");
    return -1;
}

// New: true-bridge to MMI::GetDevice (async callback → sync 200ms timeout).
// Returns shared_ptr<OHOS::MMI::InputDevice> or nullptr if MMI is unreachable
// or device id doesn't exist.
static std::shared_ptr<OHOS::MMI::InputDevice> getOHInputDevice(
        OHOS::MMI::InputManager* mmi, int32_t deviceId) {
    if (!mmi) {
        LOGE("[DEBUG] getOHInputDevice id=%d: mmi is null", deviceId);
        return nullptr;
    }
    LOGI("[DEBUG] getOHInputDevice id=%d: calling MMI::GetDevice", deviceId);
    auto p = std::make_shared<std::promise<std::shared_ptr<OHOS::MMI::InputDevice>>>();
    auto f = p->get_future();
    int32_t rc = mmi->GetDevice(deviceId,
        [p](std::shared_ptr<OHOS::MMI::InputDevice> dev) {
            p->set_value(dev);
        });
    if (rc != 0) {
        LOGE("[DEBUG] getOHInputDevice id=%d: MMI::GetDevice failed rc=%d", deviceId, rc);
        return nullptr;
    }
    if (f.wait_for(std::chrono::milliseconds(200)) != std::future_status::ready) {
        LOGE("[DEBUG] getOHInputDevice id=%d: MMI::GetDevice timeout (200ms) — async callback never fired", deviceId);
        return nullptr;
    }
    auto dev = f.get();
    if (!dev) {
        LOGW("[DEBUG] getOHInputDevice id=%d: callback returned null device (id may not exist)", deviceId);
    } else {
        LOGI("[DEBUG] getOHInputDevice id=%d: OK", deviceId);
    }
    return dev;
}

// OH InputDeviceCapability bitmask → Android InputDevice.SOURCE_* bitmask.
// Reference: Input_Adapter_design §6.4.
static int32_t mapCapabilitiesToAndroidSources(unsigned long ohCaps) {
    // Android source constants (frameworks/base/core/java/android/view/InputDevice.java).
    constexpr int SOURCE_KEYBOARD     = 0x00000101;
    constexpr int SOURCE_DPAD         = 0x00000201;
    constexpr int SOURCE_TOUCHSCREEN  = 0x00001002;
    constexpr int SOURCE_MOUSE        = 0x00002002;
    constexpr int SOURCE_STYLUS       = 0x00004002;
    constexpr int SOURCE_TOUCHPAD     = 0x00100008;
    constexpr int SOURCE_JOYSTICK     = 0x01000010;
    int32_t out = 0;
    if (ohCaps & (1u << OHOS::MMI::INPUT_DEV_CAP_KEYBOARD))     out |= SOURCE_KEYBOARD;
    if (ohCaps & (1u << OHOS::MMI::INPUT_DEV_CAP_POINTER))      out |= SOURCE_MOUSE;
    if (ohCaps & (1u << OHOS::MMI::INPUT_DEV_CAP_TOUCH))        out |= SOURCE_TOUCHSCREEN;
    if (ohCaps & (1u << OHOS::MMI::INPUT_DEV_CAP_TABLET_TOOL))  out |= SOURCE_STYLUS;
    if (ohCaps & (1u << OHOS::MMI::INPUT_DEV_CAP_TABLET_PAD))   out |= SOURCE_TOUCHPAD;
    if (ohCaps & (1u << OHOS::MMI::INPUT_DEV_CAP_GESTURE))      out |= SOURCE_TOUCHPAD;
    if (ohCaps & (1u << OHOS::MMI::INPUT_DEV_CAP_SWITCH))       out |= SOURCE_DPAD;
    if (ohCaps & (1u << OHOS::MMI::INPUT_DEV_CAP_JOYSTICK))     out |= SOURCE_JOYSTICK;
    return out;
}

// OH KeyboardType → Android InputDevice.KEYBOARD_TYPE_*.
static int32_t mapKeyboardTypeToAndroid(int32_t ohKbdType) {
    constexpr int KEYBOARD_TYPE_NONE          = 0;
    constexpr int KEYBOARD_TYPE_NON_ALPHABETIC = 1;
    constexpr int KEYBOARD_TYPE_ALPHABETIC    = 2;
    switch (ohKbdType) {
        case OHOS::MMI::KEYBOARD_TYPE_NONE:               return KEYBOARD_TYPE_NONE;
        case OHOS::MMI::KEYBOARD_TYPE_ALPHABETICKEYBOARD: return KEYBOARD_TYPE_ALPHABETIC;
        case OHOS::MMI::KEYBOARD_TYPE_UNKNOWN:
        case OHOS::MMI::KEYBOARD_TYPE_DIGITALKEYBOARD:
        case OHOS::MMI::KEYBOARD_TYPE_HANDWRITINGPEN:
        case OHOS::MMI::KEYBOARD_TYPE_REMOTECONTROL:
        default:                                          return KEYBOARD_TYPE_NON_ALPHABETIC;
    }
}

// Build a Java android.view.InputDevice from an OH MMI::InputDevice.  Uses
// android.view.InputDevice$Builder (public API, see AOSP 14 InputDevice.java
// line 544) to avoid touching private ctor signatures.  Field mapping per
// Input_Adapter_design §6.4 (17-field table).  KeyCharacterMap is the only
// field-level lacuna — OH MMI has no KCM concept, so obtainEmptyMap is the
// legitimate "no-equivalent" expression at the field level (§6.4 mKeyCharacterMap row).
static jobject buildAndroidInputDevice(JNIEnv* env, OHOS::MMI::InputDevice& dev) {
    // 1. Resolve InputDevice.Builder class + ctor.
    jclass builderCls = env->FindClass("android/view/InputDevice$Builder");
    if (!builderCls) {
        if (env->ExceptionCheck()) env->ExceptionDescribe();
        env->ExceptionClear();
        LOGE("FindClass InputDevice$Builder failed");
        return nullptr;
    }
    jmethodID builderCtor = env->GetMethodID(builderCls, "<init>", "()V");
    jobject builder = env->NewObject(builderCls, builderCtor);
    if (!builder) {
        if (env->ExceptionCheck()) env->ExceptionDescribe();
        env->ExceptionClear();
        LOGE("InputDevice.Builder ctor failed");
        env->DeleteLocalRef(builderCls);
        return nullptr;
    }

    // 2. Helper macros for fluent setter dispatch.
    auto callIntSetter = [&](const char* name, jint value) {
        jmethodID m = env->GetMethodID(builderCls, name, "(I)Landroid/view/InputDevice$Builder;");
        if (m) env->CallObjectMethod(builder, m, value);
        if (env->ExceptionCheck()) { env->ExceptionDescribe(); env->ExceptionClear(); }
    };
    auto callStringSetter = [&](const char* name, const std::string& value) {
        jmethodID m = env->GetMethodID(builderCls, name, "(Ljava/lang/String;)Landroid/view/InputDevice$Builder;");
        jstring js = env->NewStringUTF(value.c_str());
        if (m) env->CallObjectMethod(builder, m, js);
        env->DeleteLocalRef(js);
        if (env->ExceptionCheck()) { env->ExceptionDescribe(); env->ExceptionClear(); }
    };
    auto callBoolSetter = [&](const char* name, jboolean value) {
        jmethodID m = env->GetMethodID(builderCls, name, "(Z)Landroid/view/InputDevice$Builder;");
        if (m) env->CallObjectMethod(builder, m, value);
        if (env->ExceptionCheck()) { env->ExceptionDescribe(); env->ExceptionClear(); }
    };

    // 3. Map all 17 fields per §6.4.
    callIntSetter("setId", dev.GetId());
    callIntSetter("setGeneration", 1);              // OH no equivalent — fixed
    callIntSetter("setControllerNumber", 0);        // OH no equivalent — fixed
    callStringSetter("setName", dev.GetName());
    callIntSetter("setVendorId", dev.GetVendor());
    callIntSetter("setProductId", dev.GetProduct());
    callStringSetter("setDescriptor", dev.GetUniq());
    callBoolSetter("setExternal", dev.IsLocal() ? JNI_FALSE : JNI_TRUE);
    callIntSetter("setSources", mapCapabilitiesToAndroidSources(dev.GetCapabilities()));
    callIntSetter("setKeyboardType", mapKeyboardTypeToAndroid(dev.GetType()));

    // 3a. KeyCharacterMap field — only field-level lacuna (OH has no KCM concept).
    {
        jclass kcmCls = env->FindClass("android/view/KeyCharacterMap");
        jmethodID obtainEmpty = env->GetStaticMethodID(
                kcmCls, "obtainEmptyMap", "(I)Landroid/view/KeyCharacterMap;");
        jobject kcm = env->CallStaticObjectMethod(kcmCls, obtainEmpty, dev.GetId());
        jmethodID setKcm = env->GetMethodID(builderCls, "setKeyCharacterMap",
                "(Landroid/view/KeyCharacterMap;)Landroid/view/InputDevice$Builder;");
        if (setKcm && kcm) env->CallObjectMethod(builder, setKcm, kcm);
        if (kcm)    env->DeleteLocalRef(kcm);
        env->DeleteLocalRef(kcmCls);
        if (env->ExceptionCheck()) { env->ExceptionDescribe(); env->ExceptionClear(); }
    }

    // 3b. Capability flag has*: Phase 1 — false (C-tier sub-bridges land in Phase 3).
    callBoolSetter("setHasVibrator",       JNI_FALSE);
    callBoolSetter("setHasMicrophone",     JNI_FALSE);
    callBoolSetter("setHasButtonUnderPad", JNI_FALSE);
    callBoolSetter("setHasSensor",         JNI_FALSE);
    callBoolSetter("setHasBattery",        JNI_FALSE);

    // 4. build() → InputDevice.
    jmethodID buildM = env->GetMethodID(builderCls, "build", "()Landroid/view/InputDevice;");
    jobject out = env->CallObjectMethod(builder, buildM);
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
        LOGE("InputDevice.Builder.build() threw");
        out = nullptr;
    }
    env->DeleteLocalRef(builder);
    env->DeleteLocalRef(builderCls);
    return out;
}

bool OHInputManagerBridge::isInputDeviceEnabled(int32_t /*deviceId*/) {
    // Phase 2: query MMI; Phase 1: claim enabled so PhoneWindow accepts.
    return true;
}

void OHInputManagerBridge::enableInputDevice(int32_t /*deviceId*/, bool /*enabled*/) {
    // Phase 2: MMI::InputManager::EnableInputDevice(enabled).
}

bool OHInputManagerBridge::hasKeys(int32_t /*deviceId*/,
                                    const std::vector<int32_t>& keys,
                                    std::vector<bool>& exists) {
    // NO_FEATURE: claim none of the keys exist.  Caller already validated
    // sizes at the JNI boundary.
    exists.assign(keys.size(), false);
    return true;
}

bool OHInputManagerBridge::injectKeyEvent(int32_t /*deviceId*/, int32_t /*action*/,
                                           int32_t /*keyCode*/, int32_t /*metaState*/,
                                           int64_t /*downTime*/, int64_t /*eventTime*/,
                                           int32_t /*repeatCount*/, int32_t /*mode*/) {
    // Phase 2: build OH MMI KeyEvent and call SimulateInputEvent(keyEvent).
    return true;  // Pretend accepted so callers don't synthesise a fallback.
}

bool OHInputManagerBridge::injectMotionEvent(int32_t /*deviceId*/, int32_t action,
                                              float x, float y,
                                              int64_t downTime, int64_t eventTime,
                                              int32_t /*metaState*/, int32_t /*buttonState*/,
                                              int32_t /*mode*/, int32_t /*targetUid*/) {
    // Static delegate to oh_input_bridge data-plane (Input_Adapter_design §5.7.5).
    // sessionId here is unknown at this scope (control plane has no per-session
    // context); pass 0 so the data-plane treats it as "broadcast / current
    // focused session".  oh_input_bridge will fall back to a no-op route if
    // no session matches — that's the intended NO_FEATURE behavior.
    int32_t rc = OHInputBridge::getInstance().injectTouchEvent(
        /*sessionId*/ 0, action, x, y, downTime, eventTime);
    return rc == 0;
}

void OHInputManagerBridge::setPointerSpeed(int32_t /*speed*/) {
    // Phase 2: MMI::InputManager::SetPointerSpeed(speed).
}

void OHInputManagerBridge::setPointerIconType(int32_t /*typeId*/) {
    // Phase 2: MMI::InputManager::SetPointerStyle(windowId, mapped style).
}

void OHInputManagerBridge::setCustomPointerIcon(JNIEnv* /*env*/, jobject /*icon*/) {
    // Phase 2: PointerIcon → OH Pixmap conversion + SetCustomCursor.
}

void OHInputManagerBridge::registerDeviceListener(JNIEnv* env, jobject javaListener) {
    if (!env || !javaListener) return;
    std::lock_guard<std::mutex> lk(mutex_);
    if (javaDeviceListener_) {
        env->DeleteGlobalRef(javaDeviceListener_);
        javaDeviceListener_ = nullptr;
    }
    javaDeviceListener_ = env->NewGlobalRef(javaListener);
    // Phase 2: MMI::InputManager::RegisterDevListener("change", cb).  Phase 1
    // just retains the global ref — never fires onInputDevicesChanged so the
    // listener stays cold-but-installed (callers that registered won't NPE).
}

}  // namespace oh_adapter

// ============================================================
// JNI exports — auto-dlsym from adapter.window.InputManagerAdapter
// ============================================================
using oh_adapter::OHInputManagerBridge;

extern "C" {

JNIEXPORT jlong JNICALL
Java_adapter_window_InputManagerAdapter_nativeInit(JNIEnv* /*env*/, jclass /*clazz*/) {
    bool ok = OHInputManagerBridge::getInstance().init();
    // Return a non-zero opaque handle on success.  Java side treats 0 as
    // "bridge unavailable, fall back to NO_FEATURE" — that path is also
    // safe but we'd rather report success here.
    return ok ? reinterpret_cast<jlong>(&OHInputManagerBridge::getInstance()) : 0L;
}

JNIEXPORT jintArray JNICALL
Java_adapter_window_InputManagerAdapter_nativeGetInputDeviceIds(
        JNIEnv* env, jclass /*clazz*/, jlong /*handle*/) {
    auto ids = OHInputManagerBridge::getInstance().getInputDeviceIds();
    jintArray arr = env->NewIntArray(static_cast<jsize>(ids.size()));
    if (arr && !ids.empty()) {
        env->SetIntArrayRegion(arr, 0, static_cast<jsize>(ids.size()), ids.data());
    }
    return arr;
}

JNIEXPORT jobject JNICALL
Java_adapter_window_InputManagerAdapter_nativeGetInputDevice(
        JNIEnv* env, jclass /*clazz*/, jlong /*handle*/, jint deviceId) {
    LOGI("[DEBUG] nativeGetInputDevice JNI: deviceId=%d", deviceId);
    // True bridge: OH MMI::GetDevice (async callback → 200ms sync wait) +
    // 17-field mapping per Input_Adapter_design §6.4.  The Java-side `handle`
    // points to OHInputManagerBridge instance (set by nativeInit), but we
    // route through the singleton accessor below to keep the cast logic in
    // one place — both refer to the same backend MMI handle.
    auto* mmi = OHInputManagerBridge::getInstance().mmiInstance();
    if (!mmi) {
        LOGE("[DEBUG] nativeGetInputDevice: MMI singleton null (init failed?)");
        return nullptr;
    }
    auto ohDev = oh_adapter::getOHInputDevice(mmi, deviceId);
    if (!ohDev) {
        // MMI returned no device (id doesn't exist or call failed/timed out).
        // Caller (KeyCharacterMap.load) will fall back to VIRTUAL_KEYBOARD;
        // if that path also returns null, UnavailableException is the
        // legitimate "device truly not present" surface — do not synthesize.
        return nullptr;
    }
    return oh_adapter::buildAndroidInputDevice(env, *ohDev);
}

JNIEXPORT jint JNICALL
Java_adapter_window_InputManagerAdapter_nativeFindVirtualKeyboardId(
        JNIEnv* /*env*/, jclass /*clazz*/, jlong /*handle*/) {
    // Returns OH device id of a virtual+keyboard-capable device, or -1
    // when no such OH device exists.  Java caller maps the returned id
    // onto Android KCM.VIRTUAL_KEYBOARD = -1 (with field copy from OH
    // device data) or falls back to local-construction (B-tier).
    return OHInputManagerBridge::getInstance().findVirtualKeyboardOhId();
}

JNIEXPORT jboolean JNICALL
Java_adapter_window_InputManagerAdapter_nativeIsInputDeviceEnabled(
        JNIEnv* /*env*/, jclass /*clazz*/, jlong /*handle*/, jint deviceId) {
    return OHInputManagerBridge::getInstance().isInputDeviceEnabled(deviceId)
            ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_adapter_window_InputManagerAdapter_nativeEnableInputDevice(
        JNIEnv* /*env*/, jclass /*clazz*/, jlong /*handle*/, jint deviceId, jboolean enabled) {
    OHInputManagerBridge::getInstance().enableInputDevice(deviceId, enabled == JNI_TRUE);
}

JNIEXPORT jboolean JNICALL
Java_adapter_window_InputManagerAdapter_nativeHasKeys(
        JNIEnv* env, jclass /*clazz*/, jlong /*handle*/, jint deviceId,
        jintArray keyCodes, jbooleanArray keyExists) {
    if (!keyCodes || !keyExists) return JNI_FALSE;
    jsize n = env->GetArrayLength(keyCodes);
    if (env->GetArrayLength(keyExists) != n) return JNI_FALSE;

    std::vector<int32_t> keys(n);
    if (n > 0) {
        env->GetIntArrayRegion(keyCodes, 0, n, keys.data());
    }
    std::vector<bool> exists;
    bool ok = OHInputManagerBridge::getInstance().hasKeys(deviceId, keys, exists);

    if (n > 0 && exists.size() == static_cast<size_t>(n)) {
        // Copy std::vector<bool> -> jboolean[] (vector<bool> is bit-packed).
        std::vector<jboolean> out(n);
        for (jsize i = 0; i < n; ++i) out[i] = exists[i] ? JNI_TRUE : JNI_FALSE;
        env->SetBooleanArrayRegion(keyExists, 0, n, out.data());
    }
    return ok ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_adapter_window_InputManagerAdapter_nativeInjectKeyEvent(
        JNIEnv* /*env*/, jclass /*clazz*/, jlong /*handle*/,
        jint deviceId, jint action, jint keyCode, jint metaState,
        jlong downTime, jlong eventTime, jint repeatCount, jint mode) {
    bool ok = OHInputManagerBridge::getInstance().injectKeyEvent(
        deviceId, action, keyCode, metaState, downTime, eventTime, repeatCount, mode);
    return ok ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_adapter_window_InputManagerAdapter_nativeInjectMotionEvent(
        JNIEnv* /*env*/, jclass /*clazz*/, jlong /*handle*/,
        jint deviceId, jint action, jfloat x, jfloat y,
        jlong downTime, jlong eventTime, jint metaState, jint buttonState,
        jint mode, jint targetUid) {
    bool ok = OHInputManagerBridge::getInstance().injectMotionEvent(
        deviceId, action, x, y, downTime, eventTime, metaState, buttonState, mode, targetUid);
    return ok ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_adapter_window_InputManagerAdapter_nativeSetPointerSpeed(
        JNIEnv* /*env*/, jclass /*clazz*/, jlong /*handle*/, jint speed) {
    OHInputManagerBridge::getInstance().setPointerSpeed(speed);
}

JNIEXPORT void JNICALL
Java_adapter_window_InputManagerAdapter_nativeSetPointerIconType(
        JNIEnv* /*env*/, jclass /*clazz*/, jlong /*handle*/, jint typeId) {
    OHInputManagerBridge::getInstance().setPointerIconType(typeId);
}

JNIEXPORT void JNICALL
Java_adapter_window_InputManagerAdapter_nativeSetCustomPointerIcon(
        JNIEnv* env, jclass /*clazz*/, jlong /*handle*/, jobject icon) {
    OHInputManagerBridge::getInstance().setCustomPointerIcon(env, icon);
}

JNIEXPORT void JNICALL
Java_adapter_window_InputManagerAdapter_nativeRegisterDeviceListener(
        JNIEnv* env, jclass /*clazz*/, jlong /*handle*/, jobject listener) {
    OHInputManagerBridge::getInstance().registerDeviceListener(env, listener);
}

}  // extern "C"
