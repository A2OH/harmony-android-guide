// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4-power -- WestlakePowerManagerService
//
// Minimum-surface implementation of android.os.IPowerManager.Stub for the
// Westlake dalvikvm sandbox.  ~10 Tier-1 real impls + ~61 fail-loud
// unobserved-method overrides.
//
// CR2 (2026-05-12): per codex review §2 Tier 2 #2, the previously
// silent safe-default no-ops were converted to throw
// UnsupportedOperationException via ServiceMethodMissing.fail("power", ...)
// so unobserved IPowerManager method calls surface as obvious stack
// traces during M4 discovery rather than masquerading as "success".
// Tier-1 methods (isInteractive/isDisplayInteractive,
// acquireWakeLock(+WithUid +Async), releaseWakeLock(+Async),
// getBrightnessConstraint, userActivity, setStayOnSetting) retain
// real behavior.
//
// Rationale (see docs/engine/M4_DISCOVERY.md sec 13, sec 14):
//   noice (the M4-PRE3 driver) and most Android apps acquire wake locks
//   during media playback and Activity onCreate.  AOSP framework code in
//   PowerManager.WakeLock.acquire() calls IPowerManager.acquireWakeLock()
//   via Binder.  Without an IPowerManager registered under name "power",
//   ServiceManager.getService("power") returns null, PowerManager's static
//   helper returns a null binder, and any wake-lock call NPEs.
//
//   M4-power's role is to register a minimal IPowerManager that:
//     - reports `isInteractive() == true` (sandbox device is always "on")
//     - accepts/releases wake locks as no-ops, tracking tokens for sanity
//     - returns mid-range brightness defaults
//     - lets every other transaction fall through as a safe no-op
//
// Strategy:
//   - Extend android.os.IPowerManager.Stub.  At compile time, the shim's
//     hand-written stub IPowerManager declares only the methods we
//     actually override; the duplicates-list step in build-shim-dex.sh
//     strips the shim's stub from aosp-shim.dex so framework.jar's
//     real IPowerManager.Stub wins at runtime.  Our class's bytecode is
//     loaded as a subclass of framework.jar's Stub, inheriting the real
//     onTransact dispatch logic.
//   - All 71 IPowerManager methods (as of Android 16) get a body here so
//     the JVM accepts WestlakePowerManagerService as concrete at
//     instantiation time.  Unrequested methods return safe defaults
//     (0/null/false/no-op) without throwing.
//   - No per-app branches: same shim works for noice, mock apps, future
//     real APKs.  WakeLock tokens go in a synchronized HashMap so
//     multi-threaded callers don't trip CME.
//
// Method count: 71 IPowerManager methods + 0 helpers.  ~290 LOC.

package com.westlake.services;

import android.os.IBinder;
import android.os.IPowerManager;
import android.os.PowerSaveState;
import android.os.WorkSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimum-surface IPowerManager implementation for the Westlake sandbox.
 * Five methods have real behavior; the rest are safe no-ops.
 */
public final class WestlakePowerManagerService extends IPowerManager.Stub {

    /** Default brightness reported by getBrightnessConstraint (mid-range). */
    private static final float DEFAULT_BRIGHTNESS = 0.5f;

    /** Active wake-lock tokens.  Key: IBinder, value: short debug tag. */
    private final Map<IBinder, String> mWakeLocks =
            Collections.synchronizedMap(new HashMap<>());

    public WestlakePowerManagerService() {
        // Base Stub() calls attachInterface(this, IPowerManager.DESCRIPTOR),
        // so queryLocalInterface("android.os.IPowerManager") returns this.
        super();
    }

    // ------------------------------------------------------------------
    //   IMPLEMENTED (Tier-1) METHODS
    // ------------------------------------------------------------------

    // acquireWakeLock: 8-arg Android-16 signature.  No real wake-lock
    // semantics in sandbox -- we just remember the token so release works.
    @Override
    public void acquireWakeLock(IBinder lock, int flags, String tag, String packageName,
            WorkSource ws, String historyTag, int displayId,
            android.os.IWakeLockCallback callback) {
        if (lock == null) return;
        mWakeLocks.put(lock, tag != null ? tag : "<no-tag>");
    }

    // acquireWakeLockWithUid: same body, different blame UID -- ignored in sandbox.
    @Override
    public void acquireWakeLockWithUid(IBinder lock, int flags, String tag, String packageName,
            int uidtoblame, int displayId, android.os.IWakeLockCallback callback) {
        if (lock == null) return;
        mWakeLocks.put(lock, tag != null ? tag : "<no-tag>");
    }

    @Override
    public void releaseWakeLock(IBinder lock, int flags) {
        if (lock == null) return;
        mWakeLocks.remove(lock);
    }

    // Sandbox device is always "interactive" (screen on, no doze).
    @Override
    public boolean isInteractive() {
        return true;
    }

    @Override
    public boolean isDisplayInteractive(int displayId) {
        return true;
    }

    // Brightness reporting: mid-range default.  This stands in for the
    // brief's `getCurrentBrightness` (which lives on PowerManager, not on
    // IPowerManager); the closest IPowerManager method is the per-display
    // constraint accessor.
    @Override
    public float getBrightnessConstraint(int displayId, int constraint) {
        return DEFAULT_BRIGHTNESS;
    }

    // userActivity: extremely common AOSP-internal call (keep-alive timer).
    // No-op in sandbox.
    @Override
    public void userActivity(int displayId, long time, int event, int flags) {
        // no-op
    }

    @Override
    public void setStayOnSetting(int val) {
        // no-op
    }

    // ------------------------------------------------------------------
    //   FAIL-LOUD UNOBSERVED METHODS (CR2 / codex Tier 2 #2)
    //
    //   Every remaining abstract IPowerManager method has a concrete
    //   override here that throws ServiceMethodMissing.fail("power", ...).
    //   The Tier-1 set above retains real behavior.
    //
    //   How to promote a method to Tier-1: delete the throw body here and
    //   add a real implementation in the IMPLEMENTED block above.  Update
    //   PowerServiceTest to exercise the new path.
    //
    //   acquireWakeLockAsync / releaseWakeLockAsync KEPT as Tier-1 real
    //   impls (they share the wake-lock map with the sync variants).
    // ------------------------------------------------------------------

    @Override public void updateWakeLockUids(IBinder lock, int[] uids) { throw ServiceMethodMissing.fail("power", "updateWakeLockUids"); }
    @Override public void setPowerBoost(int boost, int durationMs) { throw ServiceMethodMissing.fail("power", "setPowerBoost"); }
    @Override public void setPowerMode(int mode, boolean enabled) { throw ServiceMethodMissing.fail("power", "setPowerMode"); }
    @Override public boolean setPowerModeChecked(int mode, boolean enabled) { throw ServiceMethodMissing.fail("power", "setPowerModeChecked"); }

    @Override
    public void updateWakeLockWorkSource(IBinder lock, WorkSource ws, String historyTag) {
        throw ServiceMethodMissing.fail("power", "updateWakeLockWorkSource");
    }

    @Override
    public void updateWakeLockCallback(IBinder lock, android.os.IWakeLockCallback callback) {
        throw ServiceMethodMissing.fail("power", "updateWakeLockCallback");
    }

    @Override public boolean isWakeLockLevelSupported(int level) { throw ServiceMethodMissing.fail("power", "isWakeLockLevelSupported"); }
    @Override public boolean isWakeLockLevelSupportedWithDisplayId(int level, int displayId) {
        throw ServiceMethodMissing.fail("power", "isWakeLockLevelSupportedWithDisplayId");
    }

    @Override
    public void addScreenTimeoutPolicyListener(int displayId,
            android.os.IScreenTimeoutPolicyListener listener) {
        throw ServiceMethodMissing.fail("power", "addScreenTimeoutPolicyListener");
    }

    @Override
    public void removeScreenTimeoutPolicyListener(int displayId,
            android.os.IScreenTimeoutPolicyListener listener) {
        throw ServiceMethodMissing.fail("power", "removeScreenTimeoutPolicyListener");
    }

    @Override
    public void wakeUp(long time, int reason, String details, String opPackageName) {
        throw ServiceMethodMissing.fail("power", "wakeUp");
    }

    @Override
    public void wakeUpWithDisplayId(long time, int reason, String details,
            String opPackageName, int displayId) {
        throw ServiceMethodMissing.fail("power", "wakeUpWithDisplayId");
    }

    @Override
    public void goToSleep(long time, int reason, int flags) {
        throw ServiceMethodMissing.fail("power", "goToSleep");
    }

    @Override
    public void goToSleepWithDisplayId(int displayId, long time, int reason, int flags) {
        throw ServiceMethodMissing.fail("power", "goToSleepWithDisplayId");
    }

    @Override public void nap(long time) { throw ServiceMethodMissing.fail("power", "nap"); }
    @Override public boolean areAutoPowerSaveModesEnabled() { throw ServiceMethodMissing.fail("power", "areAutoPowerSaveModesEnabled"); }
    @Override public boolean isPowerSaveMode() { throw ServiceMethodMissing.fail("power", "isPowerSaveMode"); }

    @Override
    public PowerSaveState getPowerSaveState(int serviceType) {
        throw ServiceMethodMissing.fail("power", "getPowerSaveState");
    }

    @Override public boolean setPowerSaveModeEnabled(boolean mode) { throw ServiceMethodMissing.fail("power", "setPowerSaveModeEnabled"); }
    @Override public boolean isBatterySaverSupported() { throw ServiceMethodMissing.fail("power", "isBatterySaverSupported"); }

    @Override
    public android.os.BatterySaverPolicyConfig getFullPowerSavePolicy() {
        throw ServiceMethodMissing.fail("power", "getFullPowerSavePolicy");
    }

    @Override
    public boolean setFullPowerSavePolicy(android.os.BatterySaverPolicyConfig config) {
        throw ServiceMethodMissing.fail("power", "setFullPowerSavePolicy");
    }

    @Override
    public boolean setDynamicPowerSaveHint(boolean powerSaveHint, int disableThreshold) {
        throw ServiceMethodMissing.fail("power", "setDynamicPowerSaveHint");
    }

    @Override
    public boolean setAdaptivePowerSavePolicy(android.os.BatterySaverPolicyConfig config) {
        throw ServiceMethodMissing.fail("power", "setAdaptivePowerSavePolicy");
    }

    @Override public boolean setAdaptivePowerSaveEnabled(boolean enabled) { throw ServiceMethodMissing.fail("power", "setAdaptivePowerSaveEnabled"); }
    @Override public int getPowerSaveModeTrigger() { throw ServiceMethodMissing.fail("power", "getPowerSaveModeTrigger"); }

    @Override
    public void setBatteryDischargePrediction(android.os.ParcelDuration timeRemaining,
            boolean isCustomized) {
        throw ServiceMethodMissing.fail("power", "setBatteryDischargePrediction");
    }

    @Override
    public android.os.ParcelDuration getBatteryDischargePrediction() {
        throw ServiceMethodMissing.fail("power", "getBatteryDischargePrediction");
    }

    @Override public boolean isBatteryDischargePredictionPersonalized() { throw ServiceMethodMissing.fail("power", "isBatteryDischargePredictionPersonalized"); }
    @Override public boolean isDeviceIdleMode() { throw ServiceMethodMissing.fail("power", "isDeviceIdleMode"); }
    @Override public boolean isLightDeviceIdleMode() { throw ServiceMethodMissing.fail("power", "isLightDeviceIdleMode"); }
    @Override public boolean isLowPowerStandbySupported() { throw ServiceMethodMissing.fail("power", "isLowPowerStandbySupported"); }
    @Override public boolean isLowPowerStandbyEnabled() { throw ServiceMethodMissing.fail("power", "isLowPowerStandbyEnabled"); }
    @Override public void setLowPowerStandbyEnabled(boolean enabled) { throw ServiceMethodMissing.fail("power", "setLowPowerStandbyEnabled"); }
    @Override public void setLowPowerStandbyActiveDuringMaintenance(boolean active) { throw ServiceMethodMissing.fail("power", "setLowPowerStandbyActiveDuringMaintenance"); }
    @Override public void forceLowPowerStandbyActive(boolean active) { throw ServiceMethodMissing.fail("power", "forceLowPowerStandbyActive"); }

    @Override
    public void setLowPowerStandbyPolicy(android.os.IPowerManager.LowPowerStandbyPolicy policy) {
        throw ServiceMethodMissing.fail("power", "setLowPowerStandbyPolicy");
    }

    @Override
    public android.os.IPowerManager.LowPowerStandbyPolicy getLowPowerStandbyPolicy() {
        throw ServiceMethodMissing.fail("power", "getLowPowerStandbyPolicy");
    }

    @Override public boolean isExemptFromLowPowerStandby() { throw ServiceMethodMissing.fail("power", "isExemptFromLowPowerStandby"); }
    @Override public boolean isReasonAllowedInLowPowerStandby(int reason) { throw ServiceMethodMissing.fail("power", "isReasonAllowedInLowPowerStandby"); }
    @Override public boolean isFeatureAllowedInLowPowerStandby(String feature) { throw ServiceMethodMissing.fail("power", "isFeatureAllowedInLowPowerStandby"); }

    @Override
    public void acquireLowPowerStandbyPorts(IBinder token,
            List<android.os.IPowerManager.LowPowerStandbyPortDescription> ports) {
        throw ServiceMethodMissing.fail("power", "acquireLowPowerStandbyPorts");
    }

    @Override
    public void releaseLowPowerStandbyPorts(IBinder token) {
        throw ServiceMethodMissing.fail("power", "releaseLowPowerStandbyPorts");
    }

    @Override
    public List<android.os.IPowerManager.LowPowerStandbyPortDescription>
            getActiveLowPowerStandbyPorts() {
        throw ServiceMethodMissing.fail("power", "getActiveLowPowerStandbyPorts");
    }

    @Override public void reboot(boolean confirm, String reason, boolean wait) { throw ServiceMethodMissing.fail("power", "reboot"); }
    @Override public void rebootSafeMode(boolean confirm, boolean wait) { throw ServiceMethodMissing.fail("power", "rebootSafeMode"); }
    @Override public void shutdown(boolean confirm, String reason, boolean wait) { throw ServiceMethodMissing.fail("power", "shutdown"); }
    @Override public void crash(String message) { throw ServiceMethodMissing.fail("power", "crash"); }
    @Override public int getLastShutdownReason() { throw ServiceMethodMissing.fail("power", "getLastShutdownReason"); }
    @Override public int getLastSleepReason() { throw ServiceMethodMissing.fail("power", "getLastSleepReason"); }
    @Override public void boostScreenBrightness(long time) { throw ServiceMethodMissing.fail("power", "boostScreenBrightness"); }

    // acquireWakeLockAsync / releaseWakeLockAsync: Tier-1 real impls (the
    // sync variants share the same wake-lock map; async-tagged tokens are
    // observed needed during onCreate paths).
    @Override
    public void acquireWakeLockAsync(IBinder lock, int flags, String tag, String packageName,
            WorkSource ws, String historyTag) {
        if (lock != null) mWakeLocks.put(lock, tag != null ? tag : "<async>");
    }

    @Override public void releaseWakeLockAsync(IBinder lock, int flags) {
        if (lock != null) mWakeLocks.remove(lock);
    }

    @Override public void updateWakeLockUidsAsync(IBinder lock, int[] uids) { throw ServiceMethodMissing.fail("power", "updateWakeLockUidsAsync"); }
    @Override public boolean isScreenBrightnessBoosted() { throw ServiceMethodMissing.fail("power", "isScreenBrightnessBoosted"); }
    @Override public void setAttentionLight(boolean on, int color) { throw ServiceMethodMissing.fail("power", "setAttentionLight"); }
    @Override public void setDozeAfterScreenOff(boolean on) { throw ServiceMethodMissing.fail("power", "setDozeAfterScreenOff"); }
    @Override public boolean isAmbientDisplayAvailable() { throw ServiceMethodMissing.fail("power", "isAmbientDisplayAvailable"); }
    @Override public void suppressAmbientDisplay(String token, boolean suppress) { throw ServiceMethodMissing.fail("power", "suppressAmbientDisplay"); }
    @Override public boolean isAmbientDisplaySuppressedForToken(String token) { throw ServiceMethodMissing.fail("power", "isAmbientDisplaySuppressedForToken"); }
    @Override public boolean isAmbientDisplaySuppressed() { throw ServiceMethodMissing.fail("power", "isAmbientDisplaySuppressed"); }
    @Override public boolean isAmbientDisplaySuppressedForTokenByApp(String token, int uid) {
        throw ServiceMethodMissing.fail("power", "isAmbientDisplaySuppressedForTokenByApp");
    }
    @Override public boolean forceSuspend() { throw ServiceMethodMissing.fail("power", "forceSuspend"); }

    // ------------------------------------------------------------------
    //   Diagnostic helpers (not part of IPowerManager surface)
    // ------------------------------------------------------------------

    /** Returns the count of currently-held wake-lock tokens (for tests). */
    public int activeWakeLockCount() {
        return mWakeLocks.size();
    }

    @Override
    public String toString() {
        return "WestlakePowerManagerService{wakelocks=" + mWakeLocks.size() + "}";
    }
}
