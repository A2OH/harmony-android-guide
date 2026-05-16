/*
 * DisplayManagerAdapter.java
 *
 * Real-routed adapter for Android IDisplayManager interface, bridging to
 * OpenHarmony Rosen DisplayManager via JNI (liboh_adapter_bridge.so).
 *
 * Authoritative spec: doc/window_manager_ipc_adapter_design.html §1.1
 *
 * Mapping:
 *   IDisplayManager -> OH OHOS::Rosen::DisplayManager (interfaces/innerkits/dm)
 *
 * §1.1 getDisplayInfo populates the full Android DisplayInfo struct including:
 *   - supportedModes (≥1 entry; modeId/defaultModeId guaranteed in array)
 *   - supportedColorModes (≥1 entry, defaults to {COLOR_MODE_DEFAULT})
 *   - flags = 0 (NOT FLAG_PRESENTATION — that flag is for private-display)
 *   - address (DisplayAddress.fromPhysicalDisplayId)
 *   - state derived from OH DisplayState
 *   - appWidth/Height from OH GetAvailableArea (falls back to logical size)
 *
 * Method tags:
 *   [BRIDGED]  - Mapped to OH Rosen DisplayManager via JNI
 *   [STUB]     - Returns safe default (not exercised on the HelloWorld path)
 */
package adapter.window;

import android.content.pm.ParceledListSlice;
import android.graphics.Point;
import android.hardware.OverlayProperties;
import android.hardware.display.BrightnessConfiguration;
import android.hardware.display.BrightnessInfo;
import android.hardware.display.Curve;
import android.hardware.display.HdrConversionMode;
import android.hardware.display.IDisplayManager;
import android.hardware.display.IDisplayManagerCallback;
import android.hardware.display.IVirtualDisplayCallback;
import android.hardware.display.VirtualDisplayConfig;
import android.hardware.graphics.common.DisplayDecorationSupport;
import android.media.projection.IMediaProjection;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Display;
import android.view.DisplayAddress;
import android.view.DisplayInfo;
import android.view.Surface;

import java.util.ArrayList;
import java.util.List;

public final class DisplayManagerAdapter extends IDisplayManager.Stub {

    private static final String TAG = "OH_DisplayMgrAdapter";
    private static final int DEFAULT_DISPLAY_ID = 0;

    // Holds the (no-op) registered callbacks so they aren't GC'd while
    // framework code holds a reference. We never invoke them since OH's
    // DisplayManager event push isn't bridged yet.
    private final List<IDisplayManagerCallback> mCallbacks = new ArrayList<>();

    private static volatile DisplayManagerAdapter sInstance;

    public static DisplayManagerAdapter getInstance() {
        if (sInstance == null) {
            synchronized (DisplayManagerAdapter.class) {
                if (sInstance == null) {
                    sInstance = new DisplayManagerAdapter();
                }
            }
        }
        return sInstance;
    }

    public DisplayManagerAdapter() {
        System.err.println("[" + TAG + "] instantiated (real-routed via OH Rosen DisplayManager)");
    }

    // --- Native bridge to OH Rosen DisplayManager ---
    // Implemented in framework/window/jni/display_manager_adapter_jni.cpp
    // (compiled into liboh_adapter_bridge.so; loaded by OHEnvironment).
    private static native int   nativeGetDefaultDisplayWidth();
    private static native int   nativeGetDefaultDisplayHeight();
    private static native int   nativeGetDefaultDisplayRefreshRate();
    private static native int   nativeGetDefaultDisplayDpi();
    private static native int   nativeGetDefaultDisplayRotation();
    private static native float nativeGetDefaultDisplayXDpi();
    private static native float nativeGetDefaultDisplayYDpi();
    private static native float nativeGetDefaultDisplayDensity();
    // G2.0 (window_manager_ipc_adapter_design §1.1.5.2) — extra fields needed
    // so DisplayInfo can be populated completely enough for setContentView /
    // findMode / Choreographer to not throw.
    private static native int[] nativeGetSupportedRefreshRates();
    private static native int[] nativeGetAvailableArea();          // {x, y, w, h}
    private static native int   nativeGetDisplayState();           // Android STATE_*
    private static native long  nativeGetDisplayId();
    private static native int[] nativeGetSupportedColorSpaces();
    private static native int[] nativeGetSupportedHdrFormats();
    private static native int[] nativeGetRoundedCorners();         // {tl, tr, bl, br}
    private static native int[] nativeGetCutoutBoundingRects();    // (x,y,w,h)*N

    // ============================================================================
    // BRIDGED methods (HelloWorld setContentView critical path)
    // ============================================================================

    /**
     * [BRIDGED] Returns DisplayInfo populated from OH Rosen GetDefaultDisplay().
     *
     * Spec: doc/window_manager_ipc_adapter_design.html §1.1.4 (P1 fields).
     */
    @Override
    public DisplayInfo getDisplayInfo(int displayId) throws RemoteException {
        // Only the default display is meaningful; non-default ids return null
        // matching AOSP DisplayManagerService behaviour for unknown displays.
        if (displayId != DEFAULT_DISPLAY_ID) {
            System.err.println("[" + TAG + "] getDisplayInfo(" + displayId + ") → null (only id 0 supported)");
            return null;
        }

        DisplayInfo info = new DisplayInfo();
        try {
            int width   = nativeGetDefaultDisplayWidth();
            int height  = nativeGetDefaultDisplayHeight();
            int refresh = nativeGetDefaultDisplayRefreshRate();
            int dpi     = nativeGetDefaultDisplayDpi();
            int rot     = nativeGetDefaultDisplayRotation();
            float xDpi  = nativeGetDefaultDisplayXDpi();
            float yDpi  = nativeGetDefaultDisplayYDpi();

            // §1.1.4.1 geometry — appWidth/Height from OH GetAvailableArea,
            // fall back to logical w/h when OH returns no reliable area.
            int[] avail = safeIntArray(nativeGetAvailableArea(), 4);
            int appW = (avail.length >= 4 && avail[2] > 0) ? avail[2] : width;
            int appH = (avail.length >= 4 && avail[3] > 0) ? avail[3] : height;

            info.displayId               = DEFAULT_DISPLAY_ID;
            info.displayGroupId          = 0;
            info.layerStack              = 0;
            // §1.1.4.5: main display has NO special flags — must NOT set
            // FLAG_PRESENTATION (=4), which marks it as a private display
            // and breaks Surface allocation paths.
            info.flags                   = 0;
            info.type                    = Display.TYPE_INTERNAL;
            info.name                    = "Built-in Screen";
            // §1.1.4.6 uniqueId/address derived from OH display id.
            long ohDisplayId             = nativeGetDisplayId();
            info.uniqueId                = "local:" + ohDisplayId;
            info.address                 = DisplayAddress.fromPhysicalDisplayId(ohDisplayId);
            info.removeMode              = Display.REMOVE_MODE_MOVE_CONTENT_TO_PRIMARY;

            info.appWidth                = appW;
            info.appHeight               = appH;
            info.smallestNominalAppWidth = Math.min(appW, appH);
            info.smallestNominalAppHeight= Math.min(appW, appH);
            info.largestNominalAppWidth  = Math.max(appW, appH);
            info.largestNominalAppHeight = Math.max(appW, appH);
            info.logicalWidth            = width;
            info.logicalHeight           = height;
            info.rotation                = rot;
            info.installOrientation      = Surface.ROTATION_0;

            // §1.1.4.3 Mode synthesis — supportedModes MUST be ≥ 1 entry
            // and MUST contain modeId; otherwise DisplayInfo.findMode(modeId)
            // throws IllegalStateException (HelloWorld SIGABRT root cause
            // pre-G2.0 — see §1.1.5.5).
            int[] rates = safeIntArray(nativeGetSupportedRefreshRates(), 0);
            if (rates.length == 0) {
                rates = new int[] { Math.max(refresh, 60) };
            }
            Display.Mode[] modes = new Display.Mode[rates.length];
            int currentModeIndex = 0;
            for (int i = 0; i < rates.length; i++) {
                modes[i] = new Display.Mode(i + 1, width, height, rates[i]);
                if (rates[i] == refresh) currentModeIndex = i;
            }
            info.supportedModes  = modes;
            info.modeId          = modes[currentModeIndex].getModeId();
            info.defaultModeId   = info.modeId;
            info.renderFrameRate = refresh;

            // §1.1.4.4 ColorMode — supportedColorModes MUST be ≥ 1 entry.
            int[] colorSpaces = safeIntArray(nativeGetSupportedColorSpaces(), 0);
            if (colorSpaces.length == 0) {
                colorSpaces = new int[] { Display.COLOR_MODE_DEFAULT };
            }
            info.supportedColorModes = colorSpaces;
            info.colorMode           = Display.COLOR_MODE_DEFAULT;

            info.logicalDensityDpi   = dpi;
            info.physicalXDpi        = (xDpi > 0f) ? xDpi : (float) dpi;
            info.physicalYDpi        = (yDpi > 0f) ? yDpi : (float) dpi;

            // §1.1.4.7 vsync — appVsyncOffsetNanos = 0 (no offset);
            // presentationDeadlineNanos derived from refresh rate.
            info.appVsyncOffsetNanos       = 0L;
            long frameNanos                = (refresh > 0) ? (1_000_000_000L / refresh) : 16_666_666L;
            info.presentationDeadlineNanos = Math.max(frameNanos - 5_000_000L, 1_000_000L);

            // §1.1.4.5 state derived from OH DisplayState.
            int state            = nativeGetDisplayState();
            info.state           = (state == 0) ? Display.STATE_ON : state;  // STATE_UNKNOWN→ON for default display
            info.committedState  = info.state;

            info.ownerUid                = 1000;       // system uid
            info.ownerPackageName        = "android";

            System.err.println("[" + TAG + "] getDisplayInfo(0) -> "
                    + width + "x" + height + "@" + refresh + "Hz dpi=" + dpi
                    + " supportedModes=" + modes.length
                    + " currentMode=" + info.modeId
                    + " colorModes=" + colorSpaces.length
                    + " flags=0x" + Integer.toHexString(info.flags)
                    + " state=" + info.state
                    + " appArea=" + appW + "x" + appH);
        } catch (UnsatisfiedLinkError e) {
            // Native not yet linked: fall back to safe defaults so framework
            // doesn't NPE; will be visible in logs.
            System.err.println("[" + TAG + "] WARN native unavailable, returning hardcoded fallback: " + e);
            applyDayU200Defaults(info);
        }
        return info;
    }

    // Wraps native int[] callers that may return null on JNI failure or
    // adjusts length when expected size is known.  Returns a non-null array
    // of length 0 when the input is null.
    private static int[] safeIntArray(int[] in, int expectedLen) {
        if (in == null) return (expectedLen > 0) ? new int[expectedLen] : new int[0];
        return in;
    }

    private static void applyDayU200Defaults(DisplayInfo info) {
        info.displayId               = DEFAULT_DISPLAY_ID;
        info.layerStack              = 0;
        info.flags                   = 0;
        info.type                    = Display.TYPE_INTERNAL;
        info.name                    = "Built-in Screen (fallback)";
        info.uniqueId                = "local:0";
        info.address                 = DisplayAddress.fromPhysicalDisplayId(0L);
        info.removeMode              = Display.REMOVE_MODE_MOVE_CONTENT_TO_PRIMARY;
        info.appWidth                = 720;
        info.appHeight               = 1280;
        info.smallestNominalAppWidth = 720;
        info.smallestNominalAppHeight= 720;
        info.largestNominalAppWidth  = 1280;
        info.largestNominalAppHeight = 1280;
        info.logicalWidth            = 720;
        info.logicalHeight           = 1280;
        info.rotation                = Surface.ROTATION_0;
        info.installOrientation      = Surface.ROTATION_0;
        Display.Mode m = new Display.Mode(1, 720, 1280, 60);
        info.supportedModes          = new Display.Mode[] { m };
        info.modeId                  = 1;
        info.defaultModeId           = 1;
        info.renderFrameRate         = 60;
        info.supportedColorModes     = new int[] { Display.COLOR_MODE_DEFAULT };
        info.colorMode               = Display.COLOR_MODE_DEFAULT;
        info.logicalDensityDpi       = 320;
        info.physicalXDpi            = 320f;
        info.physicalYDpi            = 320f;
        info.appVsyncOffsetNanos     = 0L;
        info.presentationDeadlineNanos = 11_600_000L;
        info.state                   = Display.STATE_ON;
        info.committedState          = Display.STATE_ON;
        info.ownerUid                = 1000;
        info.ownerPackageName        = "android";
    }

    /** [BRIDGED] HelloWorld only sees one display; return [0]. */
    @Override
    public int[] getDisplayIds(boolean includeDisabled) throws RemoteException {
        return new int[] { DEFAULT_DISPLAY_ID };
    }

    /** [BRIDGED] Stable display size (un-rotated physical size). */
    @Override
    public Point getStableDisplaySize() throws RemoteException {
        try {
            return new Point(nativeGetDefaultDisplayWidth(), nativeGetDefaultDisplayHeight());
        } catch (UnsatisfiedLinkError e) {
            return new Point(720, 1280);
        }
    }

    /** [STUB] Hold reference, never invoke (no OH Rosen → Android display event bridge yet). */
    @Override
    public void registerCallback(IDisplayManagerCallback callback) throws RemoteException {
        if (callback != null) mCallbacks.add(callback);
    }

    @Override
    public void registerCallbackWithEventMask(IDisplayManagerCallback callback, long eventsMask)
            throws RemoteException {
        if (callback != null) mCallbacks.add(callback);
    }

    // ============================================================================
    // STUB methods — none of these are on HelloWorld TextView path.
    // Each returns a safe default; non-void methods return null/0/false.
    // ============================================================================

    @Override public boolean isUidPresentOnDisplay(int uid, int displayId) { return false; }
    @Override public void startWifiDisplayScan() {}
    @Override public void stopWifiDisplayScan() {}
    @Override public void connectWifiDisplay(String address) {}
    @Override public void disconnectWifiDisplay() {}
    @Override public void renameWifiDisplay(String address, String alias) {}
    @Override public void forgetWifiDisplay(String address) {}
    @Override public void pauseWifiDisplay() {}
    @Override public void resumeWifiDisplay() {}
    @Override public android.hardware.display.WifiDisplayStatus getWifiDisplayStatus() { return null; }
    @Override public void setUserDisabledHdrTypes(int[] userDisabledTypes) {}
    @Override public void setAreUserDisabledHdrTypesAllowed(boolean v) {}
    @Override public boolean areUserDisabledHdrTypesAllowed() { return false; }
    @Override public int[] getUserDisabledHdrTypes() { return new int[0]; }
    @Override public void overrideHdrTypes(int displayId, int[] modes) {}
    @Override public void requestColorMode(int displayId, int colorMode) {}
    @Override public int createVirtualDisplay(VirtualDisplayConfig c, IVirtualDisplayCallback cb,
            IMediaProjection projectionToken, String packageName) { return -1; }
    @Override public void resizeVirtualDisplay(IVirtualDisplayCallback token, int w, int h, int d) {}
    @Override public void setVirtualDisplaySurface(IVirtualDisplayCallback token, Surface s) {}
    @Override public void releaseVirtualDisplay(IVirtualDisplayCallback token) {}
    @Override public void setVirtualDisplayState(IVirtualDisplayCallback token, boolean isOn) {}
    @Override public void setBrightnessConfigurationForUser(BrightnessConfiguration c, int u, String p) {}
    @Override public void setBrightnessConfigurationForDisplay(BrightnessConfiguration c, String id, int u, String p) {}
    @Override public BrightnessConfiguration getBrightnessConfigurationForDisplay(String id, int u) { return null; }
    @Override public BrightnessConfiguration getBrightnessConfigurationForUser(int u) { return null; }
    @Override public BrightnessConfiguration getDefaultBrightnessConfiguration() { return null; }
    @Override public boolean isMinimalPostProcessingRequested(int displayId) { return false; }
    @Override public void setTemporaryBrightness(int displayId, float brightness) {}
    @Override public void setBrightness(int displayId, float brightness) {}
    @Override public float getBrightness(int displayId) { return 0.5f; }
    @Override public void setTemporaryAutoBrightnessAdjustment(float a) {}
    @Override public Curve getMinimumBrightnessCurve() { return null; }
    @Override public BrightnessInfo getBrightnessInfo(int displayId) { return null; }
    @Override public ParceledListSlice getBrightnessEvents(String callingPackage) { return ParceledListSlice.emptyList(); }
    @Override public ParceledListSlice getAmbientBrightnessStats() { return ParceledListSlice.emptyList(); }
    @Override public int getPreferredWideGamutColorSpaceId() { return 0; }
    @Override public void setUserPreferredDisplayMode(int displayId, Display.Mode m) {}
    @Override public Display.Mode getUserPreferredDisplayMode(int displayId) { return null; }
    @Override public Display.Mode getSystemPreferredDisplayMode(int displayId) { return null; }
    @Override public void setHdrConversionMode(HdrConversionMode m) {}
    @Override public HdrConversionMode getHdrConversionModeSetting() { return null; }
    @Override public HdrConversionMode getHdrConversionMode() { return null; }
    @Override public int[] getSupportedHdrOutputTypes() { return new int[0]; }
    @Override public void setShouldAlwaysRespectAppRequestedMode(boolean enabled) {}
    @Override public boolean shouldAlwaysRespectAppRequestedMode() { return false; }
    @Override public void setRefreshRateSwitchingType(int newValue) {}
    @Override public int getRefreshRateSwitchingType() { return 0; }
    @Override public DisplayDecorationSupport getDisplayDecorationSupport(int displayId) { return null; }
    @Override public void setDisplayIdToMirror(IBinder token, int displayId) {}
    @Override public OverlayProperties getOverlaySupport() { return null; }
}
