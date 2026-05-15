// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4d -- shim/java/android/hardware/display/IDisplayManager.java
//
// COMPILE-TIME STUB for android.hardware.display.IDisplayManager.  AOSP
// marks the real interface @hide so it isn't in the public SDK android.jar;
// this stub supplies just enough surface for the Westlake shim to compile
// against the same AIDL methods that framework.jar's IDisplayManager
// declares.
//
// At RUNTIME this class is stripped from aosp-shim.dex by the entry in
// scripts/framework_duplicates.txt -- framework.jar's real
// IDisplayManager.Stub wins, and WestlakeDisplayManagerService is loaded
// as a subclass of the real Stub.  Bytecode compatibility relies on (a)
// identical FQCN `android.hardware.display.IDisplayManager$Stub`, (b)
// Stub being abstract and extending android.os.Binder, and (c) every
// method signature of WestlakeDisplayManagerService matching the
// framework.jar IDisplayManager.aidl surface (Android 16 has 64 abstract
// methods).
//
// Method count: 64 declared abstract methods, matching Android 16
// IDisplayManager.aidl.

package android.hardware.display;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IDisplayManager extends IInterface {

    static final String DESCRIPTOR = "android.hardware.display.IDisplayManager";

    // --- 64 abstract methods (generated from baksmali on framework.jar Android 16) ---

    boolean areUserDisabledHdrTypesAllowed() throws android.os.RemoteException;
    void connectWifiDisplay(java.lang.String p0) throws android.os.RemoteException;
    int createVirtualDisplay(android.hardware.display.VirtualDisplayConfig p0, android.hardware.display.IVirtualDisplayCallback p1, android.media.projection.IMediaProjection p2, java.lang.String p3) throws android.os.RemoteException;
    void disableConnectedDisplay(int p0) throws android.os.RemoteException;
    void disconnectWifiDisplay() throws android.os.RemoteException;
    void enableConnectedDisplay(int p0) throws android.os.RemoteException;
    void forgetWifiDisplay(java.lang.String p0) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getAmbientBrightnessStats() throws android.os.RemoteException;
    float getBrightness(int p0) throws android.os.RemoteException;
    android.hardware.display.BrightnessConfiguration getBrightnessConfigurationForDisplay(java.lang.String p0, int p1) throws android.os.RemoteException;
    android.hardware.display.BrightnessConfiguration getBrightnessConfigurationForUser(int p0) throws android.os.RemoteException;
    android.content.pm.ParceledListSlice getBrightnessEvents(java.lang.String p0) throws android.os.RemoteException;
    android.hardware.display.BrightnessInfo getBrightnessInfo(int p0) throws android.os.RemoteException;
    android.hardware.display.BrightnessConfiguration getDefaultBrightnessConfiguration() throws android.os.RemoteException;
    float getDefaultDozeBrightness(int p0) throws android.os.RemoteException;
    android.hardware.graphics.common.DisplayDecorationSupport getDisplayDecorationSupport(int p0) throws android.os.RemoteException;
    int[] getDisplayIds(boolean p0) throws android.os.RemoteException;
    android.view.DisplayInfo getDisplayInfo(int p0) throws android.os.RemoteException;
    android.hardware.display.DisplayTopology getDisplayTopology() throws android.os.RemoteException;
    float[] getDozeBrightnessSensorValueToBrightness(int p0) throws android.os.RemoteException;
    android.hardware.display.HdrConversionMode getHdrConversionMode() throws android.os.RemoteException;
    android.hardware.display.HdrConversionMode getHdrConversionModeSetting() throws android.os.RemoteException;
    float getHighestHdrSdrRatio(int p0) throws android.os.RemoteException;
    android.hardware.display.Curve getMinimumBrightnessCurve() throws android.os.RemoteException;
    android.hardware.OverlayProperties getOverlaySupport() throws android.os.RemoteException;
    int getPreferredWideGamutColorSpaceId() throws android.os.RemoteException;
    int getRefreshRateSwitchingType() throws android.os.RemoteException;
    android.graphics.Point getStableDisplaySize() throws android.os.RemoteException;
    int[] getSupportedHdrOutputTypes() throws android.os.RemoteException;
    android.view.Display.Mode getSystemPreferredDisplayMode(int p0) throws android.os.RemoteException;
    int[] getUserDisabledHdrTypes() throws android.os.RemoteException;
    android.view.Display.Mode getUserPreferredDisplayMode(int p0) throws android.os.RemoteException;
    android.hardware.display.WifiDisplayStatus getWifiDisplayStatus() throws android.os.RemoteException;
    boolean isMinimalPostProcessingRequested(int p0) throws android.os.RemoteException;
    boolean isUidPresentOnDisplay(int p0, int p1) throws android.os.RemoteException;
    void overrideHdrTypes(int p0, int[] p1) throws android.os.RemoteException;
    void pauseWifiDisplay() throws android.os.RemoteException;
    void registerCallback(android.hardware.display.IDisplayManagerCallback p0) throws android.os.RemoteException;
    void registerCallbackWithEventMask(android.hardware.display.IDisplayManagerCallback p0, long p1) throws android.os.RemoteException;
    void releaseVirtualDisplay(android.hardware.display.IVirtualDisplayCallback p0) throws android.os.RemoteException;
    void renameWifiDisplay(java.lang.String p0, java.lang.String p1) throws android.os.RemoteException;
    void requestColorMode(int p0, int p1) throws android.os.RemoteException;
    void requestDisplayModes(android.os.IBinder p0, int p1, int[] p2) throws android.os.RemoteException;
    boolean requestDisplayPower(int p0, int p1) throws android.os.RemoteException;
    void resizeVirtualDisplay(android.hardware.display.IVirtualDisplayCallback p0, int p1, int p2, int p3) throws android.os.RemoteException;
    void resumeWifiDisplay() throws android.os.RemoteException;
    void setAreUserDisabledHdrTypesAllowed(boolean p0) throws android.os.RemoteException;
    void setBrightness(int p0, float p1) throws android.os.RemoteException;
    void setBrightnessConfigurationForDisplay(android.hardware.display.BrightnessConfiguration p0, java.lang.String p1, int p2, java.lang.String p3) throws android.os.RemoteException;
    void setBrightnessConfigurationForUser(android.hardware.display.BrightnessConfiguration p0, int p1, java.lang.String p2) throws android.os.RemoteException;
    void setDisplayIdToMirror(android.os.IBinder p0, int p1) throws android.os.RemoteException;
    void setDisplayTopology(android.hardware.display.DisplayTopology p0) throws android.os.RemoteException;
    void setHdrConversionMode(android.hardware.display.HdrConversionMode p0) throws android.os.RemoteException;
    void setRefreshRateSwitchingType(int p0) throws android.os.RemoteException;
    void setShouldAlwaysRespectAppRequestedMode(boolean p0) throws android.os.RemoteException;
    void setTemporaryAutoBrightnessAdjustment(float p0) throws android.os.RemoteException;
    void setTemporaryBrightness(int p0, float p1) throws android.os.RemoteException;
    void setUserDisabledHdrTypes(int[] p0) throws android.os.RemoteException;
    void setUserPreferredDisplayMode(int p0, android.view.Display.Mode p1) throws android.os.RemoteException;
    void setVirtualDisplayRotation(android.hardware.display.IVirtualDisplayCallback p0, int p1) throws android.os.RemoteException;
    void setVirtualDisplaySurface(android.hardware.display.IVirtualDisplayCallback p0, android.view.Surface p1) throws android.os.RemoteException;
    boolean shouldAlwaysRespectAppRequestedMode() throws android.os.RemoteException;
    void startWifiDisplayScan() throws android.os.RemoteException;
    void stopWifiDisplayScan() throws android.os.RemoteException;

    // --- AIDL-generated Stub abstract class -------------------------------
    //
    // Real Stub in framework.jar extends Binder, implements IDisplayManager,
    // and provides onTransact() that dispatches by TRANSACTION_xxx code.
    public static abstract class Stub extends android.os.Binder implements IDisplayManager {
        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }
        // CR17: Android 16 alternate constructor that accepts a
        // PermissionEnforcer.  The real framework.jar Stub uses this to
        // bypass the ActivityThread-dependent default ctor (which NPEs
        // in the Westlake sandbox).  WestlakeDisplayManagerService
        // invokes super(NoopPermissionEnforcer) via this overload.
        public Stub(android.os.PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
        }
        public static IDisplayManager asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            if (i instanceof IDisplayManager) return (IDisplayManager) i;
            return null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
