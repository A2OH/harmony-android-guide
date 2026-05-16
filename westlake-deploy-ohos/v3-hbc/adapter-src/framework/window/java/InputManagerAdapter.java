/*
 * InputManagerAdapter.java
 *
 * Adapter implementation of IInputManager (android.hardware.input.IInputManager)
 * for the OH environment.
 *
 * Why this exists:
 *   AOSP InputManagerGlobal.getInstance() calls
 *     ServiceManager.getServiceOrThrow("input")
 *   which throws on OH (no binder SA named "input").  PhoneWindow.preparePanel
 *   then walks KeyCharacterMap.load(deviceId) -> InputManagerGlobal.getInstance()
 *   -> NullPointerException, blocking ANY app that inflates a default panel
 *   (i.e. essentially every Activity).
 *
 *   This adapter is a Stub-side implementation reflectively injected into
 *   InputManagerGlobal.sInstance / sService by AppSpawnXInit, so all upstream
 *   accessors return a non-null binder.
 *
 * Five-tier method classification (see doc/Input_Adapter_design.html ch4):
 *   A — true-bridge to OH MMI inner_api (12 methods).  JNI -> oh_input_manager_bridge.cpp.
 *   B — local synthesis (identity / cache / compose-via-A).
 *   C — capability lives in non-MMI OH SA (vibrator/sensor/light/battery/bluetooth).
 *       Phase 1 returns NO_FEATURE baseline; Phase 3 will bridge to each SA.
 *   D — keyboard layout family.  OH 7.x has no equivalent; NO_LAYOUT baseline.
 *   E — Android 13+ additions (USI / tablet / mic / kbd backlight / port assoc).
 *       NO_FEATURE baseline.
 *
 * Authoritative spec: doc/Input_Adapter_design.html.
 */
package adapter.window;

import android.hardware.input.HostUsiVersion;
import android.hardware.input.IInputDeviceBatteryListener;
import android.hardware.input.IInputDeviceBatteryState;
import android.hardware.input.IInputDevicesChangedListener;
import android.hardware.input.IInputManager;
import android.hardware.input.IInputSensorEventListener;
import android.hardware.input.IKeyboardBacklightListener;
import android.hardware.input.ITabletModeChangedListener;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputSensorInfo;
import android.hardware.input.KeyboardLayout;
import android.hardware.input.TouchCalibration;
import android.hardware.lights.Light;
import android.hardware.lights.LightState;
import android.os.CombinedVibration;
import android.os.IBinder;
import android.os.IVibratorStateListener;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.InputMonitor;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.VerifiedInputEvent;
import android.view.VerifiedKeyEvent;
import android.view.VerifiedMotionEvent;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 2026-05-08 G2.14w: implements IInputManager (NOT extends IInputManager.Stub).
 *
 * Android 14 IInputManager.aidl 加了 11 处 @EnforcePermission，AIDL 编译器据此
 * 给 Stub 自动生成 final PermissionEnforcer mEnforcer 字段，Stub.<init> 调
 * PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext())
 * 初始化它。这要求 ctor 运行在已 attach 的 ActivityThread 内 — 但本 adapter
 * 在 ActivityThread.main 之前就要 install（要先于 PhoneWindow.preparePanel
 * → KeyCharacterMap.load → InputManagerGlobal.getInstance() 的访问点）。
 *
 * 因此本类 implements IInputManager（接口）而非 extends IInputManager.Stub。
 * InputManagerGlobal.mIm 字段类型本就是 IInputManager 接口，不要求 Stub 子类。
 * OH 上 forward bridge 走 Java 方法 dispatch，不走 binder transact，asBinder
 * 永不被调（返 null 即可）。
 *
 * IActivityManager / IPackageManager / IWindowManager 三个 AIDL 没用
 * @EnforcePermission，它们的 *Adapter 仍按 design 文档 "extends IXxx.Stub"
 * 模式工作不受影响。
 */
public final class InputManagerAdapter implements IInputManager {

    private static final String TAG = "OH_IIMAdapter";

    @Override
    public IBinder asBinder() {
        // Forward bridge mode — never transacted via binder; returning null
        // is safe because InputManagerGlobal does not call asBinder() on mIm.
        return null;
    }

    // ============================================================
    // Singleton
    // ============================================================
    private static volatile InputManagerAdapter sInstance;

    public static InputManagerAdapter getInstance() {
        if (sInstance == null) {
            synchronized (InputManagerAdapter.class) {
                if (sInstance == null) {
                    sInstance = new InputManagerAdapter();
                }
            }
        }
        return sInstance;
    }

    private final long mNativeBridge;
    private final Map<String, TouchCalibration> mTouchCalibrationCache = new ConcurrentHashMap<>();
    private final List<IInputDevicesChangedListener> mDeviceListeners = new CopyOnWriteArrayList<>();

    public InputManagerAdapter() {
        long handle = 0L;
        try {
            handle = nativeInit();
        } catch (Throwable t) {
            System.err.println("[" + TAG + "] nativeInit failed (NO_FEATURE fallback): " + t);
        }
        mNativeBridge = handle;
        System.err.println("[" + TAG + "] instantiated, nativeBridge=0x" + Long.toHexString(mNativeBridge));
    }

    private static void log(String tier, String name) {
        // Keep quiet by default; flip to System.err when tracing tiers.
        // System.err.println("[" + TAG + "][" + tier + "] " + name);
    }

    // ============================================================
    // A tier — true-bridge to OH MMI inner_api (12 methods)
    // ============================================================

    @Override
    public int[] getInputDeviceIds() {
        log("A", "getInputDeviceIds");
        int[] ohIds = (mNativeBridge == 0L) ? new int[0]
                                            : nativeGetInputDeviceIds(mNativeBridge);
        if (ohIds == null) ohIds = new int[0];
        // Augment with KeyCharacterMap.VIRTUAL_KEYBOARD = -1.  Android API
        // contract assumes this id always exists (KCM.load fallback path);
        // OH MMI has no equivalent default device, so adapter must surface
        // -1 explicitly (Input_Adapter_design §5.3 B-tier).  The actual
        // InputDevice for -1 is resolved lazily in getInputDevice(-1) —
        // either remapped from an OH virtual+keyboard device (Phase 2
        // bidirectional mapping) or locally constructed (field-level
        // lacuna fallback).
        int[] out = new int[ohIds.length + 1];
        System.arraycopy(ohIds, 0, out, 0, ohIds.length);
        out[ohIds.length] = KeyCharacterMap.VIRTUAL_KEYBOARD;
        return out;
    }

    @Override
    public InputDevice getInputDevice(int deviceId) {
        log("A", "getInputDevice id=" + deviceId);
        if (deviceId == KeyCharacterMap.VIRTUAL_KEYBOARD) {
            return resolveVirtualKeyboard();
        }
        if (mNativeBridge == 0L) return null;
        return nativeGetInputDevice(mNativeBridge, deviceId);
    }

    // ===== VIRTUAL_KEYBOARD bidirectional mapping (Input_Adapter_design §5.3 B-tier) =====
    //
    // Android KCM.VIRTUAL_KEYBOARD = -1 is a fixed contract (KCM.load fallback).
    // OH MMI has no -1 default device; instead OH apps register virtual devices via
    // InputManager::AddVirtualInputDevice, which dynamically allocates a positive id.
    //
    // Mapping resolution:
    //   1. Phase 2 — try OH→Android: scan OH device list, find one with
    //      IsVirtual() + keyboard capability, copy its fields into a Builder
    //      with id=-1.  Real device data flows through.
    //   2. Fallback — OH has no virtual keyboard: locally construct minimal
    //      InputDevice with KeyCharacterMap.obtainEmptyMap(-1).  This is
    //      legitimate field-level lacuna per design §6.4 mKeyCharacterMap row,
    //      not a stub (OH simply has no concept matching VIRTUAL_KEYBOARD).
    private volatile InputDevice mVirtualKeyboardCache;

    private InputDevice resolveVirtualKeyboard() {
        InputDevice cached = mVirtualKeyboardCache;
        if (cached != null) return cached;
        synchronized (this) {
            if (mVirtualKeyboardCache != null) return mVirtualKeyboardCache;
            InputDevice resolved = null;
            // Phase 2: try OH→Android virtual keyboard mapping.
            if (mNativeBridge != 0L) {
                int ohId = nativeFindVirtualKeyboardId(mNativeBridge);
                if (ohId >= 0) {
                    InputDevice ohDevice = nativeGetInputDevice(mNativeBridge, ohId);
                    if (ohDevice != null) {
                        resolved = wrapAsVirtualKeyboard(ohDevice);
                    }
                }
            }
            // Fallback: OH has no virtual keyboard — local minimal construction.
            if (resolved == null) {
                resolved = buildLocalVirtualKeyboard();
            }
            mVirtualKeyboardCache = resolved;
            return resolved;
        }
    }

    /**
     * Wrap an OH virtual keyboard InputDevice with id remapped to
     * KeyCharacterMap.VIRTUAL_KEYBOARD = -1.  All other fields (name, vendor,
     * product, descriptor, sources, keyboardType, KCM) come from OH so the
     * caller sees real device data — only the id is rewritten to satisfy
     * Android's API contract.
     */
    private static InputDevice wrapAsVirtualKeyboard(InputDevice ohDevice) {
        return new InputDevice.Builder()
                .setId(KeyCharacterMap.VIRTUAL_KEYBOARD)
                .setName(ohDevice.getName())
                .setDescriptor(ohDevice.getDescriptor())
                .setVendorId(ohDevice.getVendorId())
                .setProductId(ohDevice.getProductId())
                .setSources(ohDevice.getSources())
                .setKeyboardType(ohDevice.getKeyboardType())
                .setKeyCharacterMap(ohDevice.getKeyCharacterMap())
                .build();
    }

    /**
     * Local minimal virtual keyboard for when OH has no virtual+keyboard
     * device.  Per design §5.3 B-tier "OH 无对应概念，本地构造 identity"
     * and §6.4 mKeyCharacterMap field-level lacuna.
     */
    private static InputDevice buildLocalVirtualKeyboard() {
        return new InputDevice.Builder()
                .setId(KeyCharacterMap.VIRTUAL_KEYBOARD)
                .setName("Virtual")
                .setDescriptor("virtual-keyboard")
                .setSources(InputDevice.SOURCE_KEYBOARD)
                .setKeyboardType(InputDevice.KEYBOARD_TYPE_NON_ALPHABETIC)
                .setKeyCharacterMap(KeyCharacterMap.obtainEmptyMap(
                        KeyCharacterMap.VIRTUAL_KEYBOARD))
                .build();
    }

    @Override
    public boolean isInputDeviceEnabled(int deviceId) {
        log("A", "isInputDeviceEnabled id=" + deviceId);
        if (mNativeBridge == 0L) return true;
        return nativeIsInputDeviceEnabled(mNativeBridge, deviceId);
    }

    @Override
    public void enableInputDevice(int deviceId) {
        log("A", "enableInputDevice id=" + deviceId);
        if (mNativeBridge == 0L) return;
        nativeEnableInputDevice(mNativeBridge, deviceId, true);
    }

    @Override
    public void disableInputDevice(int deviceId) {
        log("A", "disableInputDevice id=" + deviceId);
        if (mNativeBridge == 0L) return;
        nativeEnableInputDevice(mNativeBridge, deviceId, false);
    }

    @Override
    public boolean hasKeys(int deviceId, int sourceMask, int[] keyCodes, boolean[] keyExists) {
        log("A", "hasKeys id=" + deviceId);
        if (keyCodes == null || keyExists == null || keyCodes.length != keyExists.length) {
            return false;
        }
        if (mNativeBridge == 0L) {
            // NO_FEATURE fallback: claim none of the keys exist.
            for (int i = 0; i < keyExists.length; i++) keyExists[i] = false;
            return true;
        }
        return nativeHasKeys(mNativeBridge, deviceId, keyCodes, keyExists);
    }

    @Override
    public boolean injectInputEvent(InputEvent ev, int mode) {
        log("A", "injectInputEvent mode=" + mode);
        return injectInputEventInternal(ev, mode, /*targetUid*/ -1);
    }

    @Override
    public boolean injectInputEventToTarget(InputEvent ev, int mode, int targetUid) {
        log("A", "injectInputEventToTarget targetUid=" + targetUid);
        return injectInputEventInternal(ev, mode, targetUid);
    }

    private boolean injectInputEventInternal(InputEvent ev, int mode, int targetUid) {
        if (ev == null || mNativeBridge == 0L) return false;
        if (ev instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) ev;
            return nativeInjectKeyEvent(mNativeBridge, ke.getDeviceId(), ke.getAction(),
                    ke.getKeyCode(), ke.getMetaState(), ke.getDownTime(), ke.getEventTime(),
                    ke.getRepeatCount(), mode);
        } else if (ev instanceof MotionEvent) {
            MotionEvent me = (MotionEvent) ev;
            // Delegate to the existing oh_input_bridge data-plane via static
            // collaboration on the C++ side (see Input_Adapter_design §5.7.5).
            return nativeInjectMotionEvent(mNativeBridge, me.getDeviceId(), me.getAction(),
                    me.getX(), me.getY(), me.getDownTime(), me.getEventTime(),
                    me.getMetaState(), me.getButtonState(), mode, targetUid);
        }
        // VerifiedInputEvent / unknown subclass: cannot inject directly, accept silently.
        return true;
    }

    @Override
    public void tryPointerSpeed(int speed) {
        log("A", "tryPointerSpeed=" + speed);
        if (mNativeBridge == 0L) return;
        nativeSetPointerSpeed(mNativeBridge, speed);
    }

    @Override
    public void setPointerIconType(int typeId) {
        log("A", "setPointerIconType=" + typeId);
        if (mNativeBridge == 0L) return;
        nativeSetPointerIconType(mNativeBridge, typeId);
    }

    @Override
    public void setCustomPointerIcon(PointerIcon icon) {
        log("A", "setCustomPointerIcon");
        if (mNativeBridge == 0L || icon == null) return;
        nativeSetCustomPointerIcon(mNativeBridge, icon);
    }

    @Override
    public void registerInputDevicesChangedListener(IInputDevicesChangedListener listener) {
        log("A", "registerInputDevicesChangedListener");
        if (listener == null) return;
        mDeviceListeners.add(listener);
        if (mNativeBridge != 0L) {
            nativeRegisterDeviceListener(mNativeBridge, listener);
        }
    }

    // ============================================================
    // B tier — local synthesis (identity / cache / compose-via-A)
    // ============================================================

    @Override
    public int getKeyCodeForKeyLocation(int deviceId, int locationKeyCode) {
        log("B", "getKeyCodeForKeyLocation");
        // OH 7.x has no per-device key-location remapping; identity is exact.
        return locationKeyCode;
    }

    @Override
    public TouchCalibration getTouchCalibrationForInputDevice(String inputDeviceDescriptor,
            int rotation) {
        log("B", "getTouchCalibrationForInputDevice");
        if (inputDeviceDescriptor == null) return TouchCalibration.IDENTITY;
        TouchCalibration cached = mTouchCalibrationCache.get(inputDeviceDescriptor + "@" + rotation);
        return cached != null ? cached : TouchCalibration.IDENTITY;
    }

    @Override
    public void setTouchCalibrationForInputDevice(String inputDeviceDescriptor, int rotation,
            TouchCalibration calibration) {
        log("B", "setTouchCalibrationForInputDevice");
        if (inputDeviceDescriptor == null || calibration == null) return;
        mTouchCalibrationCache.put(inputDeviceDescriptor + "@" + rotation, calibration);
        // No downstream OH MMI sink — getter consistency only.
    }

    @Override
    public void cancelCurrentTouch() {
        log("B", "cancelCurrentTouch");
        long now = SystemClock.uptimeMillis();
        MotionEvent ev = MotionEvent.obtain(now, now, MotionEvent.ACTION_CANCEL, 0f, 0f, 0);
        ev.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        try {
            injectInputEvent(ev, /*mode*/ 0 /* INJECT_INPUT_EVENT_MODE_ASYNC */);
        } finally {
            ev.recycle();
        }
    }

    @Override
    public VerifiedInputEvent verifyInputEvent(InputEvent event) {
        log("B", "verifyInputEvent");
        if (event instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) event;
            return new VerifiedKeyEvent(ke.getDeviceId(), ke.getEventTimeNanos(),
                    ke.getSource(), ke.getDisplayId(), ke.getAction(), ke.getDownTime(),
                    ke.getFlags(), ke.getKeyCode(), ke.getScanCode(), ke.getMetaState(),
                    ke.getRepeatCount());
        } else if (event instanceof MotionEvent) {
            MotionEvent me = (MotionEvent) event;
            return new VerifiedMotionEvent(me.getDeviceId(), me.getEventTimeNanos(),
                    me.getSource(), me.getDisplayId(), me.getRawX(), me.getRawY(),
                    me.getActionMasked(), me.getDownTime(), me.getFlags(), me.getMetaState(),
                    me.getButtonState());
        }
        return null;
    }

    @Override
    public String getVelocityTrackerStrategy() {
        log("B", "getVelocityTrackerStrategy");
        // OH MMI has no exposed velocity strategy selector.  AOSP defaults to
        // "default" when this returns null/empty; safe identity behavior.
        return "";
    }

    // ============================================================
    // C tier — capability in non-MMI OH SA; Phase 1 NO_FEATURE baseline
    //   vibrator (7), sensor (6), light (5), battery (3), bluetooth (1)
    // ============================================================

    // --- vibrator ---------------------------------------------------------
    @Override
    public void vibrate(int deviceId, VibrationEffect effect, IBinder token) {
        log("C", "vibrate"); /* NO_FEATURE: vibrator SA bridge is Phase 3 work */
    }

    @Override
    public void vibrateCombined(int deviceId, CombinedVibration vibration, IBinder token) {
        log("C", "vibrateCombined");
    }

    @Override
    public void cancelVibrate(int deviceId, IBinder token) {
        log("C", "cancelVibrate");
    }

    @Override
    public int[] getVibratorIds(int deviceId) {
        log("C", "getVibratorIds");
        return new int[0];
    }

    @Override
    public boolean isVibrating(int deviceId) {
        log("C", "isVibrating");
        return false;
    }

    @Override
    public boolean registerVibratorStateListener(int deviceId, IVibratorStateListener listener) {
        log("C", "registerVibratorStateListener");
        return false;
    }

    @Override
    public boolean unregisterVibratorStateListener(int deviceId, IVibratorStateListener listener) {
        log("C", "unregisterVibratorStateListener");
        return false;
    }

    // --- sensor -----------------------------------------------------------
    @Override
    public InputSensorInfo[] getSensorList(int deviceId) {
        log("C", "getSensorList");
        return new InputSensorInfo[0];
    }

    @Override
    public boolean registerSensorListener(IInputSensorEventListener listener) {
        log("C", "registerSensorListener");
        return false;
    }

    @Override
    public void unregisterSensorListener(IInputSensorEventListener listener) {
        log("C", "unregisterSensorListener");
    }

    @Override
    public boolean enableSensor(int deviceId, int sensorType, int samplingPeriodUs,
            int maxBatchReportLatencyUs) {
        log("C", "enableSensor");
        return false;
    }

    @Override
    public void disableSensor(int deviceId, int sensorType) {
        log("C", "disableSensor");
    }

    @Override
    public boolean flushSensor(int deviceId, int sensorType) {
        log("C", "flushSensor");
        return false;
    }

    // --- light ------------------------------------------------------------
    @Override
    public List<Light> getLights(int deviceId) {
        log("C", "getLights");
        return Collections.emptyList();
    }

    @Override
    public LightState getLightState(int deviceId, int lightId) {
        log("C", "getLightState");
        return null;
    }

    @Override
    public void setLightStates(int deviceId, int[] lightIds, LightState[] states, IBinder token) {
        log("C", "setLightStates");
    }

    @Override
    public void openLightSession(int deviceId, String opPkg, IBinder token) {
        log("C", "openLightSession");
    }

    @Override
    public void closeLightSession(int deviceId, IBinder token) {
        log("C", "closeLightSession");
    }

    // --- battery ----------------------------------------------------------
    @Override
    public IInputDeviceBatteryState getBatteryState(int deviceId) {
        log("C", "getBatteryState");
        return null;
    }

    @Override
    public void registerBatteryListener(int deviceId, IInputDeviceBatteryListener listener) {
        log("C", "registerBatteryListener");
    }

    @Override
    public void unregisterBatteryListener(int deviceId, IInputDeviceBatteryListener listener) {
        log("C", "unregisterBatteryListener");
    }

    // --- bluetooth --------------------------------------------------------
    @Override
    public String getInputDeviceBluetoothAddress(int deviceId) {
        log("C", "getInputDeviceBluetoothAddress");
        return null;
    }

    // ============================================================
    // D tier — keyboard layout family; OH 7.x no equivalent (NO_LAYOUT)
    // ============================================================
    @Override
    public KeyboardLayout[] getKeyboardLayouts() {
        log("D", "getKeyboardLayouts");
        return new KeyboardLayout[0];
    }

    @Override
    public KeyboardLayout[] getKeyboardLayoutsForInputDevice(InputDeviceIdentifier identifier) {
        log("D", "getKeyboardLayoutsForInputDevice");
        return new KeyboardLayout[0];
    }

    @Override
    public KeyboardLayout getKeyboardLayout(String keyboardLayoutDescriptor) {
        log("D", "getKeyboardLayout");
        return null;
    }

    @Override
    public String getCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier) {
        log("D", "getCurrentKeyboardLayoutForInputDevice");
        return null;
    }

    @Override
    public void setCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier,
            String keyboardLayoutDescriptor) {
        log("D", "setCurrentKeyboardLayoutForInputDevice");
    }

    @Override
    public String[] getEnabledKeyboardLayoutsForInputDevice(InputDeviceIdentifier identifier) {
        log("D", "getEnabledKeyboardLayoutsForInputDevice");
        return new String[0];
    }

    @Override
    public void addKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier,
            String keyboardLayoutDescriptor) {
        log("D", "addKeyboardLayoutForInputDevice");
    }

    @Override
    public void removeKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier,
            String keyboardLayoutDescriptor) {
        log("D", "removeKeyboardLayoutForInputDevice");
    }

    @Override
    public String getKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, int userId,
            InputMethodInfo imeInfo, InputMethodSubtype imeSubtype) {
        log("D", "getKeyboardLayoutForInputDevice (new API)");
        return null;
    }

    @Override
    public void setKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, int userId,
            InputMethodInfo imeInfo, InputMethodSubtype imeSubtype,
            String keyboardLayoutDescriptor) {
        log("D", "setKeyboardLayoutForInputDevice (new API)");
    }

    @Override
    public KeyboardLayout[] getKeyboardLayoutListForInputDevice(InputDeviceIdentifier identifier,
            int userId, InputMethodInfo imeInfo, InputMethodSubtype imeSubtype) {
        log("D", "getKeyboardLayoutListForInputDevice");
        return new KeyboardLayout[0];
    }

    @Override
    public void remapModifierKey(int fromKey, int toKey) {
        log("D", "remapModifierKey");
    }

    @Override
    public void clearAllModifierKeyRemappings() {
        log("D", "clearAllModifierKeyRemappings");
    }

    @Override
    public Map getModifierKeyRemapping() {
        log("D", "getModifierKeyRemapping");
        return new HashMap<>();
    }

    // ============================================================
    // E tier — Android 13+ additions / tablet / mic / kbd backlight /
    //          USI / port association / pointer capture / monitor
    // ============================================================
    @Override
    public int isInTabletMode() {
        log("E", "isInTabletMode");
        // 0 == tablet mode unknown (AOSP InputManager.SWITCH_STATE_UNKNOWN)
        return 0;
    }

    @Override
    public void registerTabletModeChangedListener(ITabletModeChangedListener listener) {
        log("E", "registerTabletModeChangedListener");
    }

    @Override
    public int isMicMuted() {
        log("E", "isMicMuted");
        return 0;
    }

    @Override
    public void requestPointerCapture(IBinder inputChannelToken, boolean enabled) {
        log("E", "requestPointerCapture");
    }

    @Override
    public InputMonitor monitorGestureInput(IBinder token, String name, int displayId) {
        log("E", "monitorGestureInput");
        return null;
    }

    @Override
    public void addPortAssociation(String inputPort, int displayPort) {
        log("E", "addPortAssociation");
    }

    @Override
    public void removePortAssociation(String inputPort) {
        log("E", "removePortAssociation");
    }

    @Override
    public void addUniqueIdAssociation(String inputPort, String displayUniqueId) {
        log("E", "addUniqueIdAssociation");
    }

    @Override
    public void removeUniqueIdAssociation(String inputPort) {
        log("E", "removeUniqueIdAssociation");
    }

    @Override
    public void pilferPointers(IBinder inputChannelToken) {
        log("E", "pilferPointers");
    }

    @Override
    public void registerKeyboardBacklightListener(IKeyboardBacklightListener listener) {
        log("E", "registerKeyboardBacklightListener");
    }

    @Override
    public void unregisterKeyboardBacklightListener(IKeyboardBacklightListener listener) {
        log("E", "unregisterKeyboardBacklightListener");
    }

    @Override
    public HostUsiVersion getHostUsiVersionFromDisplayConfig(int displayId) {
        log("E", "getHostUsiVersionFromDisplayConfig");
        return null;
    }

    // ============================================================
    // Native bridges — implemented in oh_input_manager_bridge.cpp
    // ============================================================
    private static native long nativeInit();

    private static native int[] nativeGetInputDeviceIds(long handle);
    private static native InputDevice nativeGetInputDevice(long handle, int deviceId);
    // Phase 2 VIRTUAL_KEYBOARD bidirectional mapping: returns OH device id of
    // an IsVirtual()+keyboard-capable device, or -1 when OH has no such
    // device (Java caller falls back to local minimal construction).
    private static native int nativeFindVirtualKeyboardId(long handle);
    private static native boolean nativeIsInputDeviceEnabled(long handle, int deviceId);
    private static native void nativeEnableInputDevice(long handle, int deviceId, boolean enabled);
    private static native boolean nativeHasKeys(long handle, int deviceId,
            int[] keyCodes, boolean[] keyExists);

    private static native boolean nativeInjectKeyEvent(long handle, int deviceId, int action,
            int keyCode, int metaState, long downTime, long eventTime, int repeatCount, int mode);
    private static native boolean nativeInjectMotionEvent(long handle, int deviceId, int action,
            float x, float y, long downTime, long eventTime, int metaState, int buttonState,
            int mode, int targetUid);

    private static native void nativeSetPointerSpeed(long handle, int speed);
    private static native void nativeSetPointerIconType(long handle, int typeId);
    private static native void nativeSetCustomPointerIcon(long handle, PointerIcon icon);

    private static native void nativeRegisterDeviceListener(long handle,
            IInputDevicesChangedListener listener);
}
