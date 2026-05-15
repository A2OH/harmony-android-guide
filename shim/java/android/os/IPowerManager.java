// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4-power -- shim/java/android/os/IPowerManager.java
//
// COMPILE-TIME STUB for android.os.IPowerManager.  AOSP marks the real
// interface @hide so it isn't in the public SDK android.jar; this stub
// supplies just enough surface for the Westlake shim to compile against
// the same AIDL methods that framework.jar's IPowerManager declares.
//
// At RUNTIME this class is stripped from aosp-shim.dex by the entry in
// scripts/framework_duplicates.txt -- framework.jar's real IPowerManager.Stub
// wins, and WestlakePowerManagerService is loaded as a subclass of the
// real Stub.  Bytecode compatibility relies on (a) identical FQCN
// `android.os.IPowerManager$Stub`, (b) Stub being abstract and extending
// android.os.Binder, and (c) all 71 method signatures matching the
// Android 16 IPowerManager.aidl surface.
//
// Method count: 71 declared abstract methods, matching
//   https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-16.0.0_r1/core/java/android/os/IPowerManager.aidl
//
// If you add/remove methods here, also update framework_duplicates.txt.

package android.os;

import java.util.List;

public interface IPowerManager extends android.os.IInterface {

    static final java.lang.String DESCRIPTOR = "android.os.IPowerManager";

    // --- wake locks ---
    void acquireWakeLock(IBinder lock, int flags, String tag, String packageName,
            WorkSource ws, String historyTag, int displayId,
            IWakeLockCallback callback) throws RemoteException;

    void acquireWakeLockWithUid(IBinder lock, int flags, String tag, String packageName,
            int uidtoblame, int displayId,
            IWakeLockCallback callback) throws RemoteException;

    void releaseWakeLock(IBinder lock, int flags) throws RemoteException;
    void updateWakeLockUids(IBinder lock, int[] uids) throws RemoteException;

    // --- power mode / boost (oneway) ---
    void setPowerBoost(int boost, int durationMs) throws RemoteException;
    void setPowerMode(int mode, boolean enabled) throws RemoteException;
    boolean setPowerModeChecked(int mode, boolean enabled) throws RemoteException;

    void updateWakeLockWorkSource(IBinder lock, WorkSource ws, String historyTag) throws RemoteException;
    void updateWakeLockCallback(IBinder lock, IWakeLockCallback callback) throws RemoteException;
    boolean isWakeLockLevelSupported(int level) throws RemoteException;
    boolean isWakeLockLevelSupportedWithDisplayId(int level, int displayId) throws RemoteException;

    void addScreenTimeoutPolicyListener(int displayId,
            IScreenTimeoutPolicyListener listener) throws RemoteException;
    void removeScreenTimeoutPolicyListener(int displayId,
            IScreenTimeoutPolicyListener listener) throws RemoteException;

    // --- user activity / sleep / wake ---
    void userActivity(int displayId, long time, int event, int flags) throws RemoteException;
    void wakeUp(long time, int reason, String details, String opPackageName) throws RemoteException;
    void wakeUpWithDisplayId(long time, int reason, String details, String opPackageName,
            int displayId) throws RemoteException;
    void goToSleep(long time, int reason, int flags) throws RemoteException;
    void goToSleepWithDisplayId(int displayId, long time, int reason, int flags) throws RemoteException;
    void nap(long time) throws RemoteException;

    // --- brightness ---
    float getBrightnessConstraint(int displayId, int constraint) throws RemoteException;

    // --- interactive / battery ---
    boolean isInteractive() throws RemoteException;
    boolean isDisplayInteractive(int displayId) throws RemoteException;
    boolean areAutoPowerSaveModesEnabled() throws RemoteException;
    boolean isPowerSaveMode() throws RemoteException;
    PowerSaveState getPowerSaveState(int serviceType) throws RemoteException;
    boolean setPowerSaveModeEnabled(boolean mode) throws RemoteException;
    boolean isBatterySaverSupported() throws RemoteException;
    BatterySaverPolicyConfig getFullPowerSavePolicy() throws RemoteException;
    boolean setFullPowerSavePolicy(BatterySaverPolicyConfig config) throws RemoteException;
    boolean setDynamicPowerSaveHint(boolean powerSaveHint, int disableThreshold) throws RemoteException;
    boolean setAdaptivePowerSavePolicy(BatterySaverPolicyConfig config) throws RemoteException;
    boolean setAdaptivePowerSaveEnabled(boolean enabled) throws RemoteException;
    int getPowerSaveModeTrigger() throws RemoteException;

    void setBatteryDischargePrediction(ParcelDuration timeRemaining, boolean isCustomized) throws RemoteException;
    ParcelDuration getBatteryDischargePrediction() throws RemoteException;
    boolean isBatteryDischargePredictionPersonalized() throws RemoteException;
    boolean isDeviceIdleMode() throws RemoteException;
    boolean isLightDeviceIdleMode() throws RemoteException;

    // --- low-power standby ---
    boolean isLowPowerStandbySupported() throws RemoteException;
    boolean isLowPowerStandbyEnabled() throws RemoteException;
    void setLowPowerStandbyEnabled(boolean enabled) throws RemoteException;
    void setLowPowerStandbyActiveDuringMaintenance(boolean activeDuringMaintenance) throws RemoteException;
    void forceLowPowerStandbyActive(boolean active) throws RemoteException;
    void setLowPowerStandbyPolicy(LowPowerStandbyPolicy policy) throws RemoteException;
    LowPowerStandbyPolicy getLowPowerStandbyPolicy() throws RemoteException;
    boolean isExemptFromLowPowerStandby() throws RemoteException;
    boolean isReasonAllowedInLowPowerStandby(int reason) throws RemoteException;
    boolean isFeatureAllowedInLowPowerStandby(String feature) throws RemoteException;
    void acquireLowPowerStandbyPorts(IBinder token,
            List<LowPowerStandbyPortDescription> ports) throws RemoteException;
    void releaseLowPowerStandbyPorts(IBinder token) throws RemoteException;
    List<LowPowerStandbyPortDescription> getActiveLowPowerStandbyPorts() throws RemoteException;

    // --- shutdown / reboot ---
    void reboot(boolean confirm, String reason, boolean wait) throws RemoteException;
    void rebootSafeMode(boolean confirm, boolean wait) throws RemoteException;
    void shutdown(boolean confirm, String reason, boolean wait) throws RemoteException;
    void crash(String message) throws RemoteException;
    int getLastShutdownReason() throws RemoteException;
    int getLastSleepReason() throws RemoteException;
    void setStayOnSetting(int val) throws RemoteException;
    void boostScreenBrightness(long time) throws RemoteException;

    // --- async wake-lock variants ---
    void acquireWakeLockAsync(IBinder lock, int flags, String tag, String packageName,
            WorkSource ws, String historyTag) throws RemoteException;
    void releaseWakeLockAsync(IBinder lock, int flags) throws RemoteException;
    void updateWakeLockUidsAsync(IBinder lock, int[] uids) throws RemoteException;

    boolean isScreenBrightnessBoosted() throws RemoteException;
    void setAttentionLight(boolean on, int color) throws RemoteException;
    void setDozeAfterScreenOff(boolean on) throws RemoteException;
    boolean isAmbientDisplayAvailable() throws RemoteException;
    void suppressAmbientDisplay(String token, boolean suppress) throws RemoteException;
    boolean isAmbientDisplaySuppressedForToken(String token) throws RemoteException;
    boolean isAmbientDisplaySuppressed() throws RemoteException;
    boolean isAmbientDisplaySuppressedForTokenByApp(String token, int appUid) throws RemoteException;
    boolean forceSuspend() throws RemoteException;

    // --- nested parcelables (AIDL inline) ---
    public static class LowPowerStandbyPolicy {
        public String identifier;
        public List<String> exemptPackages;
        public int allowedReasons;
        public List<String> allowedFeatures;
    }

    public static class LowPowerStandbyPortDescription {
        public int protocol;
        public int portMatcher;
        public int portNumber;
        public byte[] localAddress;
    }

    // --- AIDL-generated Default static class -------------------------------
    //
    // Provides empty default impls for every method so callers can use
    // IPowerManager.Default in proxy fallback chains.  Stub does NOT
    // extend Default in real AIDL output (this is critical -- if you make
    // it extend Default you change a runtime supertype that framework.jar
    // doesn't agree with, and class loading throws IncompatibleClassChange).
    public static class Default implements IPowerManager {
        @Override public void acquireWakeLock(IBinder l, int f, String t, String p, WorkSource w, String h, int d, IWakeLockCallback c) {}
        @Override public void acquireWakeLockWithUid(IBinder l, int f, String t, String p, int u, int d, IWakeLockCallback c) {}
        @Override public void releaseWakeLock(IBinder l, int f) {}
        @Override public void updateWakeLockUids(IBinder l, int[] u) {}
        @Override public void setPowerBoost(int b, int d) {}
        @Override public void setPowerMode(int m, boolean e) {}
        @Override public boolean setPowerModeChecked(int m, boolean e) { return false; }
        @Override public void updateWakeLockWorkSource(IBinder l, WorkSource w, String h) {}
        @Override public void updateWakeLockCallback(IBinder l, IWakeLockCallback c) {}
        @Override public boolean isWakeLockLevelSupported(int l) { return false; }
        @Override public boolean isWakeLockLevelSupportedWithDisplayId(int l, int d) { return false; }
        @Override public void addScreenTimeoutPolicyListener(int d, IScreenTimeoutPolicyListener s) {}
        @Override public void removeScreenTimeoutPolicyListener(int d, IScreenTimeoutPolicyListener s) {}
        @Override public void userActivity(int d, long t, int e, int f) {}
        @Override public void wakeUp(long t, int r, String d, String o) {}
        @Override public void wakeUpWithDisplayId(long t, int r, String s, String o, int d) {}
        @Override public void goToSleep(long t, int r, int f) {}
        @Override public void goToSleepWithDisplayId(int d, long t, int r, int f) {}
        @Override public void nap(long t) {}
        @Override public float getBrightnessConstraint(int d, int c) { return 0.0f; }
        @Override public boolean isInteractive() { return false; }
        @Override public boolean isDisplayInteractive(int d) { return false; }
        @Override public boolean areAutoPowerSaveModesEnabled() { return false; }
        @Override public boolean isPowerSaveMode() { return false; }
        @Override public PowerSaveState getPowerSaveState(int s) { return null; }
        @Override public boolean setPowerSaveModeEnabled(boolean m) { return false; }
        @Override public boolean isBatterySaverSupported() { return false; }
        @Override public BatterySaverPolicyConfig getFullPowerSavePolicy() { return null; }
        @Override public boolean setFullPowerSavePolicy(BatterySaverPolicyConfig c) { return false; }
        @Override public boolean setDynamicPowerSaveHint(boolean p, int d) { return false; }
        @Override public boolean setAdaptivePowerSavePolicy(BatterySaverPolicyConfig c) { return false; }
        @Override public boolean setAdaptivePowerSaveEnabled(boolean e) { return false; }
        @Override public int getPowerSaveModeTrigger() { return 0; }
        @Override public void setBatteryDischargePrediction(ParcelDuration t, boolean c) {}
        @Override public ParcelDuration getBatteryDischargePrediction() { return null; }
        @Override public boolean isBatteryDischargePredictionPersonalized() { return false; }
        @Override public boolean isDeviceIdleMode() { return false; }
        @Override public boolean isLightDeviceIdleMode() { return false; }
        @Override public boolean isLowPowerStandbySupported() { return false; }
        @Override public boolean isLowPowerStandbyEnabled() { return false; }
        @Override public void setLowPowerStandbyEnabled(boolean e) {}
        @Override public void setLowPowerStandbyActiveDuringMaintenance(boolean a) {}
        @Override public void forceLowPowerStandbyActive(boolean a) {}
        @Override public void setLowPowerStandbyPolicy(LowPowerStandbyPolicy p) {}
        @Override public LowPowerStandbyPolicy getLowPowerStandbyPolicy() { return null; }
        @Override public boolean isExemptFromLowPowerStandby() { return false; }
        @Override public boolean isReasonAllowedInLowPowerStandby(int r) { return false; }
        @Override public boolean isFeatureAllowedInLowPowerStandby(String f) { return false; }
        @Override public void acquireLowPowerStandbyPorts(IBinder t, List<LowPowerStandbyPortDescription> p) {}
        @Override public void releaseLowPowerStandbyPorts(IBinder t) {}
        @Override public List<LowPowerStandbyPortDescription> getActiveLowPowerStandbyPorts() { return null; }
        @Override public void reboot(boolean c, String r, boolean w) {}
        @Override public void rebootSafeMode(boolean c, boolean w) {}
        @Override public void shutdown(boolean c, String r, boolean w) {}
        @Override public void crash(String m) {}
        @Override public int getLastShutdownReason() { return 0; }
        @Override public int getLastSleepReason() { return 0; }
        @Override public void setStayOnSetting(int v) {}
        @Override public void boostScreenBrightness(long t) {}
        @Override public void acquireWakeLockAsync(IBinder l, int f, String t, String p, WorkSource w, String h) {}
        @Override public void releaseWakeLockAsync(IBinder l, int f) {}
        @Override public void updateWakeLockUidsAsync(IBinder l, int[] u) {}
        @Override public boolean isScreenBrightnessBoosted() { return false; }
        @Override public void setAttentionLight(boolean on, int c) {}
        @Override public void setDozeAfterScreenOff(boolean on) {}
        @Override public boolean isAmbientDisplayAvailable() { return false; }
        @Override public void suppressAmbientDisplay(String t, boolean s) {}
        @Override public boolean isAmbientDisplaySuppressedForToken(String t) { return false; }
        @Override public boolean isAmbientDisplaySuppressed() { return false; }
        @Override public boolean isAmbientDisplaySuppressedForTokenByApp(String t, int u) { return false; }
        @Override public boolean forceSuspend() { return false; }
        @Override public IBinder asBinder() { return null; }
    }

    // --- AIDL-generated Stub abstract class -------------------------------
    //
    // Real Stub in framework.jar extends Binder, implements IPowerManager,
    // and provides onTransact() that dispatches by TRANSACTION_xxx code.
    // Our stub matches that surface exactly.  Because Stub is abstract,
    // subclasses (like WestlakePowerManagerService) must implement every
    // IPowerManager method or also be abstract.
    //
    // At runtime, the framework.jar Stub wins; the asInterface() and
    // attachInterface() wiring used by ServiceManager / queryLocalInterface
    // is the real one.
    public static abstract class Stub extends android.os.Binder implements IPowerManager {
        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }
        public static IPowerManager asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            if (i instanceof IPowerManager) return (IPowerManager) i;
            return null;  // shim doesn't implement Proxy
        }
        @Override public IBinder asBinder() { return this; }
    }
}
