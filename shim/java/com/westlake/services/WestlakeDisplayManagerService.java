// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4d -- WestlakeDisplayManagerService
//
// Minimum-surface implementation of
// android.hardware.display.IDisplayManager.Stub for the Westlake dalvikvm
// sandbox.  ~5 Tier-1 real impls + ~60 fail-loud unobserved-method
// overrides (per codex CR2 / ServiceMethodMissing.fail pattern).
//
// Same-process Stub.asInterface elision:
//   When framework code does
//     IDisplayManager dm = IDisplayManager.Stub.asInterface(
//         ServiceManager.getService("display"));
//   the Stub looks up queryLocalInterface(IDisplayManager.DESCRIPTOR) on
//   the IBinder, which returns THIS instance (because Stub() called
//   attachInterface(this, DESCRIPTOR)).  asInterface then returns the
//   raw object cast to IDisplayManager -- no Parcel marshaling, no
//   onTransact dispatch.
//
// Compile-time vs runtime hierarchy:
//   Compile-time: extends shim's android.hardware.display.IDisplayManager$Stub
//                 (abstract; 64 abstract methods declared in
//                  shim/java/android/hardware/display/IDisplayManager.java).
//   Runtime:      extends framework.jar's
//                 android.hardware.display.IDisplayManager$Stub.  The
//                 shim Stub is stripped from aosp-shim.dex via
//                 scripts/framework_duplicates.txt so the real Stub
//                 wins.  Our subclass must provide a body for every
//                 abstract method.
//
// Rationale (see docs/engine/M4_DISCOVERY.md sec 7):
//   Apps (and AOSP framework code) routinely call
//     DisplayManagerGlobal.getDisplayInfo(0)
//     DisplayManagerGlobal.getDisplayIds()
//     dm.registerCallback(...)
//   during onCreate, View measure/layout, ImageView/Drawable density
//   resolution.  Without an IDisplayManager registered under "display",
//   ServiceManager.getService("display") returns null, DisplayManagerGlobal
//   NPEs, and framework code that touches Display geometry dies.
//
//   M4d's role: register a minimal IDisplayManager that reports a
//   sensible single internal Display (OnePlus 6 reference geometry:
//   1080x2280, density 480 dpi, 60Hz).  Callback register/unregister
//   are no-ops (we never dispatch display events).
//
// Method count: 64 IDisplayManager methods + 0 helpers.  Tier-1 (real):
//   getDisplayInfo, getDisplayIds, registerCallback,
//   registerCallbackWithEventMask.  Remaining 60 fail loud.
//
// Per CR2: every remaining abstract method throws
// UnsupportedOperationException via ServiceMethodMissing.fail("display", ...)
// so unobserved transactions surface as obvious stack traces during M4
// discovery rather than masquerading as "success".
//
// CR17 (2026-05-12): per codex review #2 HIGH finding -- the previously
// used `super()` ctor expands to
//     PermissionEnforcer.fromContext(ActivityThread.currentActivityThread()
//                                         .getSystemContext())
// which NPEs in the Westlake sandbox (ActivityThread is null unless
// CharsetPrimer.primeActivityThread() runs first).  Production-path
// callers via ServiceRegistrar.registerAllServices() don't run the test
// harness primer, so the default ctor NPE'd there.  Fixed by adopting
// the same PermissionEnforcer-bypass pattern used by M4a/M4b/M4c/M4-power
// (subclassed PermissionEnforcer whose protected no-arg ctor sets
// mContext=null and returns).  No system services are touched.

package com.westlake.services;

import android.hardware.display.IDisplayManager;
import android.hardware.display.IDisplayManagerCallback;
import android.os.IBinder;
import android.os.PermissionEnforcer;
import android.view.DisplayInfo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class WestlakeDisplayManagerService extends IDisplayManager.Stub {

    /** OnePlus 6 reference geometry. */
    private static final int DISPLAY_W = 1080;
    private static final int DISPLAY_H = 2280;
    private static final int DENSITY_DPI = 480;
    private static final float REFRESH_RATE = 60.0f;

    /** Tracks registered callbacks so unregister could be symmetric.  We
     *  never call onDisplayEvent from the sandbox; this set exists only
     *  so the registerCallback path returns cleanly and we can prove
     *  callbacks landed if the test or discovery probes inspect us. */
    private final Set<IDisplayManagerCallback> mCallbacks =
            Collections.synchronizedSet(new HashSet<IDisplayManagerCallback>());

    // PermissionEnforcer subclass nested here so users don't need to
    // import it; protected constructor of PermissionEnforcer is
    // accessible to subclasses regardless of package.
    private static final class NoopPermissionEnforcer extends PermissionEnforcer {
        NoopPermissionEnforcer() { super(); }
    }

    public WestlakeDisplayManagerService() {
        // Bypass the deprecated no-arg constructor that NPEs in the
        // sandbox (ActivityThread.getSystemContext() returns null); use
        // the Stub(PermissionEnforcer) overload with a no-op enforcer.
        // Base Stub still calls attachInterface(this, DESCRIPTOR), so
        // queryLocalInterface("android.hardware.display.IDisplayManager") returns this.
        super(new NoopPermissionEnforcer());
    }

    // ------------------------------------------------------------------
    //   IMPLEMENTED (Tier-1) METHODS
    // ------------------------------------------------------------------

    /** Build a DisplayInfo for the single internal display.  Returns the
     *  same configured DisplayInfo for any non-negative displayId so apps
     *  that probe alternate displays don't NPE.  Returns null for
     *  unknown displays only when displayId < 0 (matching framework
     *  semantics that negative IDs are invalid). */
    @Override
    public DisplayInfo getDisplayInfo(int p0) throws android.os.RemoteException {
        if (p0 < 0) return null;
        DisplayInfo info = new DisplayInfo();
        // Compile-time shim DisplayInfo and runtime framework.jar
        // DisplayInfo have different public-field sets.  Set everything
        // reflectively to tolerate the mismatch (shim has logicalWidth /
        // logicalHeight / smallestNominal* directly but lacks appWidth /
        // appHeight / logicalDensityDpi / refreshRate, while framework.jar
        // has them all).
        trySetField(info, "logicalWidth", DISPLAY_W);
        trySetField(info, "logicalHeight", DISPLAY_H);
        trySetField(info, "appWidth", DISPLAY_W);
        trySetField(info, "appHeight", DISPLAY_H);
        trySetField(info, "smallestNominalAppWidth", DISPLAY_W);
        trySetField(info, "smallestNominalAppHeight", DISPLAY_H);
        trySetField(info, "largestNominalAppWidth", DISPLAY_W);
        trySetField(info, "largestNominalAppHeight", DISPLAY_H);
        trySetField(info, "logicalDensityDpi", DENSITY_DPI);
        trySetField(info, "physicalXDpi", (float) DENSITY_DPI);
        trySetField(info, "physicalYDpi", (float) DENSITY_DPI);
        // refreshRate lives on Display.Mode in current Android versions,
        // not directly on DisplayInfo, but older snapshots had a field.
        trySetField(info, "refreshRate", REFRESH_RATE);
        return info;
    }

    /** Return {0} -- the single internal display ID.  Signature is
     *  Android 16's getDisplayIds(boolean includeDisabled). */
    @Override
    public int[] getDisplayIds(boolean p0) throws android.os.RemoteException {
        return new int[] { 0 };
    }

    /** Track the callback; we never dispatch display events. */
    @Override
    public void registerCallback(IDisplayManagerCallback p0) throws android.os.RemoteException {
        if (p0 != null) mCallbacks.add(p0);
    }

    /** Same body, just an additional event-mask parameter (ignored). */
    @Override
    public void registerCallbackWithEventMask(IDisplayManagerCallback p0, long p1)
            throws android.os.RemoteException {
        if (p0 != null) mCallbacks.add(p0);
    }

    /** Reflective field setter -- tolerant of compile-time shim vs
     *  runtime framework.jar field-set differences. */
    private static void trySetField(Object inst, String name, Object value) {
        try {
            java.lang.reflect.Field f = inst.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(inst, value);
        } catch (Throwable ignored) { /* field absent in this version */ }
    }

    // ------------------------------------------------------------------
    //   FAIL-LOUD UNOBSERVED METHODS (CR2 / codex Tier 2 #2)
    //
    //   How to promote a method to Tier-1: delete the throw body here and
    //   add a real implementation in the IMPLEMENTED block above.  Update
    //   DisplayServiceTest to exercise the new path.
    // ------------------------------------------------------------------

    // ---- CR32 promotions: AOSP defaults / sandbox-safe constants ----
    @Override public boolean areUserDisabledHdrTypesAllowed() throws android.os.RemoteException { return false; }
    @Override public int[] getUserDisabledHdrTypes() throws android.os.RemoteException { return new int[0]; }
    @Override public int[] getSupportedHdrOutputTypes() throws android.os.RemoteException { return new int[0]; }
    @Override public float getBrightness(int p0) throws android.os.RemoteException { return 0.5f; /* mid-brightness default */ }
    @Override public float getDefaultDozeBrightness(int p0) throws android.os.RemoteException { return 0.0f; }
    // CR32 NOTE: getPreferredWideGamutColorSpaceId stays fail-loud. Promoting it lets
    // DisplayManagerGlobal.<init>(IDisplayManager) proceed past the gamut probe into
    // its PropertyInvalidatedCache field-init chain, which triggers the underlying
    // PF-arch-054 SIGBUS in the patched loader_to_string JNI lambda
    // (SystemServiceRouteTest exit 135). Keeping this fail-loud lets the caller's
    // try/catch in DisplayManagerGlobal.getInstance / SystemServiceRouteTest absorb
    // the UOE cleanly. Will re-promote once PF-arch-054 is substrate-fixed.
    @Override public int getPreferredWideGamutColorSpaceId() throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getPreferredWideGamutColorSpaceId"); }
    @Override public int getRefreshRateSwitchingType() throws android.os.RemoteException { return 0; /* SWITCHING_TYPE_NONE */ }
    @Override public boolean isMinimalPostProcessingRequested(int p0) throws android.os.RemoteException { return false; }
    @Override public boolean isUidPresentOnDisplay(int p0, int p1) throws android.os.RemoteException { return false; }
    @Override public boolean shouldAlwaysRespectAppRequestedMode() throws android.os.RemoteException { return false; }

    // No-op setters (sandbox: no observers, no persistent display config)
    @Override public void setBrightness(int p0, float p1) throws android.os.RemoteException { /* no-op */ }
    @Override public void setTemporaryBrightness(int p0, float p1) throws android.os.RemoteException { /* no-op */ }
    @Override public void setTemporaryAutoBrightnessAdjustment(float p0) throws android.os.RemoteException { /* no-op */ }
    @Override public void setRefreshRateSwitchingType(int p0) throws android.os.RemoteException { /* no-op */ }
    @Override public void setShouldAlwaysRespectAppRequestedMode(boolean p0) throws android.os.RemoteException { /* no-op */ }
    @Override public void setAreUserDisabledHdrTypesAllowed(boolean p0) throws android.os.RemoteException { /* no-op */ }
    @Override public void setUserDisabledHdrTypes(int[] p0) throws android.os.RemoteException { /* no-op */ }
    @Override public void overrideHdrTypes(int p0, int[] p1) throws android.os.RemoteException { /* no-op */ }
    @Override public void requestColorMode(int p0, int p1) throws android.os.RemoteException { /* no-op */ }
    @Override public void requestDisplayModes(android.os.IBinder p0, int p1, int[] p2) throws android.os.RemoteException { /* no-op */ }
    @Override public void setDisplayIdToMirror(android.os.IBinder p0, int p1) throws android.os.RemoteException { /* no-op */ }

    // Wifi display (sandbox: no wifi display) -- no-ops
    @Override public void startWifiDisplayScan() throws android.os.RemoteException { /* no-op */ }
    @Override public void stopWifiDisplayScan() throws android.os.RemoteException { /* no-op */ }
    @Override public void pauseWifiDisplay() throws android.os.RemoteException { /* no-op */ }
    @Override public void resumeWifiDisplay() throws android.os.RemoteException { /* no-op */ }
    @Override public void connectWifiDisplay(java.lang.String p0) throws android.os.RemoteException { /* no-op */ }
    @Override public void disconnectWifiDisplay() throws android.os.RemoteException { /* no-op */ }
    @Override public void forgetWifiDisplay(java.lang.String p0) throws android.os.RemoteException { /* no-op */ }
    @Override public void renameWifiDisplay(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException { /* no-op */ }

    // ---- fail-loud: real binder semantics / non-trivial returns ----
    @Override public int createVirtualDisplay(android.hardware.display.VirtualDisplayConfig p0, android.hardware.display.IVirtualDisplayCallback p1, android.media.projection.IMediaProjection p2, java.lang.String p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "createVirtualDisplay"); }
    @Override public void disableConnectedDisplay(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "disableConnectedDisplay"); }
    @Override public void enableConnectedDisplay(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "enableConnectedDisplay"); }
    @Override public android.content.pm.ParceledListSlice getAmbientBrightnessStats() throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getAmbientBrightnessStats"); }
    @Override public android.hardware.display.BrightnessConfiguration getBrightnessConfigurationForDisplay(java.lang.String p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getBrightnessConfigurationForDisplay"); }
    @Override public android.hardware.display.BrightnessConfiguration getBrightnessConfigurationForUser(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getBrightnessConfigurationForUser"); }
    @Override public android.content.pm.ParceledListSlice getBrightnessEvents(java.lang.String p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getBrightnessEvents"); }
    @Override public android.hardware.display.BrightnessInfo getBrightnessInfo(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getBrightnessInfo"); }
    @Override public android.hardware.display.BrightnessConfiguration getDefaultBrightnessConfiguration() throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getDefaultBrightnessConfiguration"); }
    @Override public android.hardware.graphics.common.DisplayDecorationSupport getDisplayDecorationSupport(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getDisplayDecorationSupport"); }
    @Override public android.hardware.display.DisplayTopology getDisplayTopology() throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getDisplayTopology"); }
    @Override public float[] getDozeBrightnessSensorValueToBrightness(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getDozeBrightnessSensorValueToBrightness"); }
    @Override public android.hardware.display.HdrConversionMode getHdrConversionMode() throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getHdrConversionMode"); }
    @Override public android.hardware.display.HdrConversionMode getHdrConversionModeSetting() throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getHdrConversionModeSetting"); }
    @Override public float getHighestHdrSdrRatio(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getHighestHdrSdrRatio"); }
    @Override public android.hardware.display.Curve getMinimumBrightnessCurve() throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getMinimumBrightnessCurve"); }
    @Override public android.hardware.OverlayProperties getOverlaySupport() throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getOverlaySupport"); }
    @Override public android.graphics.Point getStableDisplaySize() throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getStableDisplaySize"); }
    @Override public android.view.Display.Mode getSystemPreferredDisplayMode(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getSystemPreferredDisplayMode"); }
    @Override public android.view.Display.Mode getUserPreferredDisplayMode(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getUserPreferredDisplayMode"); }
    @Override public android.hardware.display.WifiDisplayStatus getWifiDisplayStatus() throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "getWifiDisplayStatus"); }
    @Override public void releaseVirtualDisplay(android.hardware.display.IVirtualDisplayCallback p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "releaseVirtualDisplay"); }
    @Override public boolean requestDisplayPower(int p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "requestDisplayPower"); }
    @Override public void resizeVirtualDisplay(android.hardware.display.IVirtualDisplayCallback p0, int p1, int p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "resizeVirtualDisplay"); }
    @Override public void setBrightnessConfigurationForDisplay(android.hardware.display.BrightnessConfiguration p0, java.lang.String p1, int p2, java.lang.String p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "setBrightnessConfigurationForDisplay"); }
    @Override public void setBrightnessConfigurationForUser(android.hardware.display.BrightnessConfiguration p0, int p1, java.lang.String p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "setBrightnessConfigurationForUser"); }
    @Override public void setDisplayTopology(android.hardware.display.DisplayTopology p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "setDisplayTopology"); }
    @Override public void setHdrConversionMode(android.hardware.display.HdrConversionMode p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "setHdrConversionMode"); }
    @Override public void setUserPreferredDisplayMode(int p0, android.view.Display.Mode p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "setUserPreferredDisplayMode"); }
    @Override public void setVirtualDisplayRotation(android.hardware.display.IVirtualDisplayCallback p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "setVirtualDisplayRotation"); }
    @Override public void setVirtualDisplaySurface(android.hardware.display.IVirtualDisplayCallback p0, android.view.Surface p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("display", "setVirtualDisplaySurface"); }

    // ------------------------------------------------------------------
    //   Diagnostic helpers (not part of IDisplayManager surface)
    // ------------------------------------------------------------------

    /** Number of currently-registered callbacks (for tests). */
    public int registeredCallbackCount() {
        return mCallbacks.size();
    }

    @Override
    public String toString() {
        return "WestlakeDisplayManagerService{callbacks=" + mCallbacks.size() + "}";
    }
}
