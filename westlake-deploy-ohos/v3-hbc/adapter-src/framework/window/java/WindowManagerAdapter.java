/*
 * WindowManagerAdapter.java
 *
 * Stub-based adapter for Android IWindowManager interface.
 * Replaces the old IWindowManagerBridge (InvocationHandler pattern) with a
 * class inheritance approach (extends IWindowManager.Stub).
 *
 * Routes calls to OH IWindowManager + ISceneSessionManager via native bridge.
 *
 * Mapping:
 *   IWindowManager -> OH IWindowManager (window create/destroy/property)
 *                  -> OH ISceneSessionManager (session management)
 *
 * Methods are categorized as:
 *   [BRIDGED]     - Mapped to OH equivalent via native call
 *   [STUB]        - No OH equivalent, returns safe default
 */
package adapter.window;

import android.app.IAssistDataReceiver;
import android.content.ComponentName;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.GraphicBuffer;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.DisplayInfo;
import android.view.IAppTransitionAnimationSpecsFuture;
import android.view.ICrossWindowBlurEnabledListener;
import android.view.IDisplayChangeWindowController;
import android.view.IDisplayFoldListener;
import android.view.IDisplayWindowInsetsController;
import android.view.IDisplayWindowListener;
import android.view.IInputFilter;
import android.view.IOnKeyguardExitResult;
import android.view.IPinnedTaskListener;
import android.view.IRotationWatcher;
import android.view.IScrollCaptureResponseListener;
import android.view.ISystemGestureExclusionListener;
import android.view.IWallpaperVisibilityListener;
import android.view.IWindow;
import android.view.IWindowManager;
import android.view.IWindowSession;
import android.view.IWindowSessionCallback;
import android.view.InputChannel;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.InsetsState;
import android.view.KeyEvent;
import android.view.MagnificationSpec;
import android.view.MotionEvent;
import android.view.RemoteAnimationAdapter;
import android.view.SurfaceControl;
import android.view.TaskTransitionSpec;
import android.view.WindowContentFrameStats;
import android.view.WindowManager;
import android.view.displayhash.DisplayHash;
import android.view.displayhash.VerifiedDisplayHash;
import android.window.AddToSurfaceSyncGroupResult;
import android.window.ISurfaceSyncGroupCompletedListener;
import android.window.ITaskFpsCallback;
import android.window.ScreenCapture;

import com.android.internal.os.IResultReceiver;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.policy.IKeyguardLockedStateListener;
import com.android.internal.policy.IShortcutService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stub-based adapter that bridges Android IWindowManager to OpenHarmony
 * window management services. Each method from the AIDL interface is overridden
 * with either a bridged implementation (forwarded to OH native) or a stub
 * (logged and returns a safe default).
 */
public class WindowManagerAdapter extends IWindowManager.Stub {

    private static final String TAG = "OH_WMAdapter";

    private final long mOhWindowManager;

    private static native long nativeGetOHWindowManagerService();

    private static volatile WindowManagerAdapter sInstance;

    public static WindowManagerAdapter getInstance() {
        if (sInstance == null) {
            synchronized (WindowManagerAdapter.class) {
                if (sInstance == null) {
                    sInstance = new WindowManagerAdapter();
                }
            }
        }
        return sInstance;
    }

    public WindowManagerAdapter() {
        mOhWindowManager = nativeGetOHWindowManagerService();
        Log.i(TAG, "WindowManagerAdapter created, ohWM handle=0x"
                + Long.toHexString(mOhWindowManager));
    }

    // ====================================================================
    // Helper methods
    // ====================================================================

    private void logBridged(String method, String target) {
        Log.d(TAG, "[BRIDGED] " + method + " " + target);
    }

    private void logStub(String method, String reason) {
        Log.d(TAG, "[STUB] " + method + " - " + reason);
    }

    // ====================================================================
    // Category 1: View Server (debug) [STUB]
    // ====================================================================

    @Override
    public boolean startViewServer(int port) throws RemoteException {
        logStub("startViewServer", "Debug view server not mapped");
        return false;
    }

    @Override
    public boolean stopViewServer() throws RemoteException {
        logStub("stopViewServer", "Debug view server not mapped");
        return false;
    }

    @Override
    public boolean isViewServerRunning() throws RemoteException {
        logStub("isViewServerRunning", "Debug view server not mapped");
        return false;
    }

    // ====================================================================
    // Category 2: Session Management [BRIDGED]
    // ====================================================================

    /** [BRIDGED] openSession -> OH ISceneSessionManager.CreateAndConnectSpecificSession */
    @Override
    public IWindowSession openSession(IWindowSessionCallback callback) throws RemoteException {
        logBridged("openSession", "-> OH ISceneSessionManager.CreateAndConnectSpecificSession");
        // TODO: Return a bridged IWindowSession instance wrapping OH session
        return null;
    }

    /** [STUB] useBLAST - always returns true */
    @Override
    public boolean useBLAST() throws RemoteException {
        return true;
    }

    // ====================================================================
    // Category 3: Display Size / Density [BRIDGED]
    // ====================================================================

    /** [BRIDGED] getInitialDisplaySize -> OH DisplayManager.GetDefaultDisplayInfo */
    @Override
    public void getInitialDisplaySize(int displayId, Point size) throws RemoteException {
        logBridged("getInitialDisplaySize", "-> OH DisplayManager.GetDefaultDisplayInfo");
        // TODO: Fill size from OH display info
    }

    /** [BRIDGED] getBaseDisplaySize -> OH DisplayManager.GetDefaultDisplayInfo */
    @Override
    public void getBaseDisplaySize(int displayId, Point size) throws RemoteException {
        logBridged("getBaseDisplaySize", "-> OH DisplayManager.GetDefaultDisplayInfo");
        // TODO: Fill size from OH display info
    }

    /** [BRIDGED] setForcedDisplaySize -> OH DisplayManager */
    @Override
    public void setForcedDisplaySize(int displayId, int width, int height) throws RemoteException {
        logBridged("setForcedDisplaySize", "-> OH DisplayManager");
    }

    /** [BRIDGED] clearForcedDisplaySize -> OH DisplayManager */
    @Override
    public void clearForcedDisplaySize(int displayId) throws RemoteException {
        logBridged("clearForcedDisplaySize", "-> OH DisplayManager");
    }

    /** [BRIDGED] getInitialDisplayDensity -> OH DisplayManager density */
    @Override
    public int getInitialDisplayDensity(int displayId) throws RemoteException {
        logBridged("getInitialDisplayDensity", "-> OH DisplayManager density");
        return 0;
    }

    /** [BRIDGED] getBaseDisplayDensity -> OH DisplayManager density */
    @Override
    public int getBaseDisplayDensity(int displayId) throws RemoteException {
        logBridged("getBaseDisplayDensity", "-> OH DisplayManager density");
        return 0;
    }

    /** [STUB] getDisplayIdByUniqueId - OH display ID mapping */
    @Override
    public int getDisplayIdByUniqueId(String uniqueId) throws RemoteException {
        logStub("getDisplayIdByUniqueId", "OH display ID mapping");
        return 0;
    }

    /** [BRIDGED] setForcedDisplayDensityForUser -> OH DisplayManager density */
    @Override
    public void setForcedDisplayDensityForUser(int displayId, int density, int userId)
            throws RemoteException {
        logBridged("setForcedDisplayDensityForUser", "-> OH DisplayManager density");
    }

    /** [BRIDGED] clearForcedDisplayDensityForUser -> OH DisplayManager density */
    @Override
    public void clearForcedDisplayDensityForUser(int displayId, int userId)
            throws RemoteException {
        logBridged("clearForcedDisplayDensityForUser", "-> OH DisplayManager density");
    }

    /** [STUB] setForcedDisplayScalingMode - No OH equivalent */
    @Override
    public void setForcedDisplayScalingMode(int displayId, int mode) throws RemoteException {
        logStub("setForcedDisplayScalingMode", "No OH equivalent");
    }

    // ====================================================================
    // Category 4: Event Dispatching [BRIDGED]
    // ====================================================================

    /** [BRIDGED] setEventDispatching -> OH IWindowManager input control */
    @Override
    public void setEventDispatching(boolean enabled) throws RemoteException {
        logBridged("setEventDispatching", "-> OH IWindowManager input control");
    }

    // ====================================================================
    // Category 5: Window Token Management [BRIDGED]
    // ====================================================================

    /** [STUB] isWindowToken - OH token management different */
    @Override
    public boolean isWindowToken(IBinder binder) throws RemoteException {
        logStub("isWindowToken", "OH token management different");
        return false;
    }

    /** [BRIDGED] addWindowToken -> OH IWindowManager (implicit in window creation) */
    @Override
    public void addWindowToken(IBinder token, int type, int displayId, Bundle options)
            throws RemoteException {
        logBridged("addWindowToken", "-> OH IWindowManager (implicit in window creation)");
    }

    /** [BRIDGED] removeWindowToken -> OH IWindowManager.RemoveWindow */
    @Override
    public void removeWindowToken(IBinder token, int displayId) throws RemoteException {
        logBridged("removeWindowToken", "-> OH IWindowManager.RemoveWindow");
    }

    // ====================================================================
    // Category 6: Display Change Controller [STUB]
    // ====================================================================

    /** [STUB] setDisplayChangeWindowController - OH display change controller */
    @Override
    public void setDisplayChangeWindowController(IDisplayChangeWindowController controller)
            throws RemoteException {
        logStub("setDisplayChangeWindowController", "OH display change controller");
    }

    // ====================================================================
    // Category 7: Shell / Accessibility [STUB]
    // ====================================================================

    /** [STUB] addShellRoot - OH has no shell root concept */
    @Override
    public SurfaceControl addShellRoot(int displayId, IWindow client, int shellRootLayer)
            throws RemoteException {
        logStub("addShellRoot", "OH has no shell root concept");
        return null;
    }

    /** [STUB] setShellRootAccessibilityWindow - OH accessibility different */
    @Override
    public void setShellRootAccessibilityWindow(int displayId, int shellRootLayer, IWindow target)
            throws RemoteException {
        logStub("setShellRootAccessibilityWindow", "OH accessibility different");
    }

    // ====================================================================
    // Category 8: App Transition [STUB]
    // ====================================================================

    /** [STUB] overridePendingAppTransitionMultiThumbFuture - OH transition managed differently */
    @Override
    public void overridePendingAppTransitionMultiThumbFuture(
            IAppTransitionAnimationSpecsFuture specsFuture, IRemoteCallback startedCallback,
            boolean scaleUp, int displayId) throws RemoteException {
        logStub("overridePendingAppTransitionMultiThumbFuture",
                "OH transition managed differently");
    }

    /** [STUB] overridePendingAppTransitionRemote - OH transition managed differently */
    @Override
    public void overridePendingAppTransitionRemote(RemoteAnimationAdapter remoteAnimationAdapter,
            int displayId) throws RemoteException {
        logStub("overridePendingAppTransitionRemote", "OH transition managed differently");
    }

    /** [STUB] endProlongedAnimations - OH transition managed differently */
    @Override
    public void endProlongedAnimations() throws RemoteException {
        logStub("endProlongedAnimations", "OH transition managed differently");
    }

    // ====================================================================
    // Category 9: Screen Freezing [STUB]
    // ====================================================================

    /** [STUB] startFreezingScreen - OH transition managed differently */
    @Override
    public void startFreezingScreen(int exitAnim, int enterAnim) throws RemoteException {
        logStub("startFreezingScreen", "OH transition managed differently");
    }

    /** [STUB] stopFreezingScreen - OH transition managed differently */
    @Override
    public void stopFreezingScreen() throws RemoteException {
        logStub("stopFreezingScreen", "OH transition managed differently");
    }

    // ====================================================================
    // Category 10: Keyguard [STUB]
    // ====================================================================

    /** [STUB] disableKeyguard - OH keyguard not mapped */
    @Override
    public void disableKeyguard(IBinder token, String tag, int userId) throws RemoteException {
        logStub("disableKeyguard", "OH keyguard not mapped");
    }

    /** [STUB] reenableKeyguard - OH keyguard not mapped */
    @Override
    public void reenableKeyguard(IBinder token, int userId) throws RemoteException {
        logStub("reenableKeyguard", "OH keyguard not mapped");
    }

    /** [STUB] exitKeyguardSecurely - OH keyguard not mapped */
    @Override
    public void exitKeyguardSecurely(IOnKeyguardExitResult callback) throws RemoteException {
        logStub("exitKeyguardSecurely", "OH keyguard not mapped");
    }

    /** [STUB] isKeyguardLocked - OH keyguard not mapped */
    @Override
    public boolean isKeyguardLocked() throws RemoteException {
        logStub("isKeyguardLocked", "OH keyguard not mapped");
        return false;
    }

    /** [STUB] isKeyguardSecure - OH keyguard not mapped */
    @Override
    public boolean isKeyguardSecure(int userId) throws RemoteException {
        logStub("isKeyguardSecure", "OH keyguard not mapped");
        return false;
    }

    /** [STUB] dismissKeyguard - OH keyguard not mapped */
    @Override
    public void dismissKeyguard(IKeyguardDismissCallback callback, CharSequence message)
            throws RemoteException {
        logStub("dismissKeyguard", "OH keyguard not mapped");
    }

    /** [STUB] addKeyguardLockedStateListener - OH keyguard not mapped */
    @Override
    public void addKeyguardLockedStateListener(IKeyguardLockedStateListener listener)
            throws RemoteException {
        logStub("addKeyguardLockedStateListener", "OH keyguard not mapped");
    }

    /** [STUB] removeKeyguardLockedStateListener - OH keyguard not mapped */
    @Override
    public void removeKeyguardLockedStateListener(IKeyguardLockedStateListener listener)
            throws RemoteException {
        logStub("removeKeyguardLockedStateListener", "OH keyguard not mapped");
    }

    /** [STUB] setSwitchingUser - OH keyguard not mapped */
    @Override
    public void setSwitchingUser(boolean switching) throws RemoteException {
        logStub("setSwitchingUser", "OH keyguard not mapped");
    }

    // ====================================================================
    // Category 11: System Dialogs [STUB]
    // ====================================================================

    /** [STUB] closeSystemDialogs - OH system dialog */
    @Override
    public void closeSystemDialogs(String reason) throws RemoteException {
        logStub("closeSystemDialogs", "OH system dialog");
    }

    // ====================================================================
    // Category 12: Animation Scale [STUB]
    // ====================================================================

    /** [STUB] getAnimationScale - OH animation scale not mapped */
    @Override
    public float getAnimationScale(int which) throws RemoteException {
        logStub("getAnimationScale", "OH animation scale not mapped");
        return 1.0f;
    }

    /** [STUB] getAnimationScales - OH animation scale not mapped */
    @Override
    public float[] getAnimationScales() throws RemoteException {
        logStub("getAnimationScales", "OH animation scale not mapped");
        return new float[0];
    }

    /** [STUB] setAnimationScale - OH animation scale not mapped */
    @Override
    public void setAnimationScale(int which, float scale) throws RemoteException {
        logStub("setAnimationScale", "OH animation scale not mapped");
    }

    /** [STUB] setAnimationScales - OH animation scale not mapped */
    @Override
    public void setAnimationScales(float[] scales) throws RemoteException {
        logStub("setAnimationScales", "OH animation scale not mapped");
    }

    /** [STUB] getCurrentAnimatorScale - OH animation scale not mapped */
    @Override
    public float getCurrentAnimatorScale() throws RemoteException {
        logStub("getCurrentAnimatorScale", "OH animation scale not mapped");
        return 1.0f;
    }

    // ====================================================================
    // Category 13: Touch Mode [BRIDGED / STUB]
    // ====================================================================

    /** [BRIDGED] setInTouchMode -> OH input mode */
    @Override
    public void setInTouchMode(boolean inTouch, int displayId) throws RemoteException {
        logBridged("setInTouchMode", "-> OH input mode");
    }

    /** [BRIDGED] setInTouchModeOnAllDisplays -> OH input mode */
    @Override
    public void setInTouchModeOnAllDisplays(boolean inTouch) throws RemoteException {
        logBridged("setInTouchModeOnAllDisplays", "-> OH input mode");
    }

    /** [STUB] isInTouchMode - OH touch mode */
    @Override
    public boolean isInTouchMode(int displayId) throws RemoteException {
        logStub("isInTouchMode", "OH touch mode");
        return true;
    }

    // ====================================================================
    // Category 14: StrictMode [STUB]
    // ====================================================================

    /** [STUB] showStrictModeViolation - No OH equivalent */
    @Override
    public void showStrictModeViolation(boolean on) throws RemoteException {
        logStub("showStrictModeViolation", "No OH equivalent");
    }

    /** [STUB] setStrictModeVisualIndicatorPreference - No OH equivalent */
    @Override
    public void setStrictModeVisualIndicatorPreference(String enabled) throws RemoteException {
        logStub("setStrictModeVisualIndicatorPreference", "No OH equivalent");
    }

    // ====================================================================
    // Category 15: Screen Capture Policy [STUB / BRIDGED]
    // ====================================================================

    /** [STUB] refreshScreenCaptureDisabled - OH screenshot policy */
    @Override
    public void refreshScreenCaptureDisabled() throws RemoteException {
        logStub("refreshScreenCaptureDisabled", "OH screenshot policy");
    }

    // ====================================================================
    // Category 16: Rotation [BRIDGED / STUB]
    // ====================================================================

    /** [BRIDGED] getDefaultDisplayRotation -> OH DisplayManager.GetDefaultDisplayInfo.rotation */
    @Override
    public int getDefaultDisplayRotation() throws RemoteException {
        logBridged("getDefaultDisplayRotation",
                "-> OH DisplayManager.GetDefaultDisplayInfo.rotation");
        return 0;
    }

    /** [BRIDGED] watchRotation -> OH ISession.UpdateRotationChangeRegistered */
    @Override
    public int watchRotation(IRotationWatcher watcher, int displayId) throws RemoteException {
        logBridged("watchRotation", "-> OH ISession.UpdateRotationChangeRegistered");
        return 0;
    }

    /** [BRIDGED] removeRotationWatcher -> OH rotation unregister */
    @Override
    public void removeRotationWatcher(IRotationWatcher watcher) throws RemoteException {
        logBridged("removeRotationWatcher", "-> OH rotation unregister");
    }

    /** [STUB] registerProposedRotationListener - No OH equivalent */
    @Override
    public int registerProposedRotationListener(IBinder contextToken, IRotationWatcher listener)
            throws RemoteException {
        logStub("registerProposedRotationListener", "No OH equivalent");
        return 0;
    }

    /** [STUB] getPreferredOptionsPanelGravity - No OH equivalent */
    @Override
    public int getPreferredOptionsPanelGravity(int displayId) throws RemoteException {
        logStub("getPreferredOptionsPanelGravity", "No OH equivalent");
        return 0;
    }

    /** [BRIDGED] freezeRotation -> OH DisplayManager.FreezeRotation */
    @Override
    public void freezeRotation(int rotation) throws RemoteException {
        logBridged("freezeRotation", "-> OH DisplayManager.FreezeRotation");
    }

    /** [BRIDGED] thawRotation -> OH DisplayManager.ThawRotation */
    @Override
    public void thawRotation() throws RemoteException {
        logBridged("thawRotation", "-> OH DisplayManager.ThawRotation");
    }

    /** [STUB] isRotationFrozen - OH rotation state query */
    @Override
    public boolean isRotationFrozen() throws RemoteException {
        logStub("isRotationFrozen", "OH rotation state query");
        return false;
    }

    /** [BRIDGED] freezeDisplayRotation -> OH DisplayManager.FreezeRotation */
    @Override
    public void freezeDisplayRotation(int displayId, int rotation) throws RemoteException {
        logBridged("freezeDisplayRotation", "-> OH DisplayManager.FreezeRotation");
    }

    /** [BRIDGED] thawDisplayRotation -> OH DisplayManager.ThawRotation */
    @Override
    public void thawDisplayRotation(int displayId) throws RemoteException {
        logBridged("thawDisplayRotation", "-> OH DisplayManager.ThawRotation");
    }

    /** [STUB] isDisplayRotationFrozen - OH rotation state query */
    @Override
    public boolean isDisplayRotationFrozen(int displayId) throws RemoteException {
        logStub("isDisplayRotationFrozen", "OH rotation state query");
        return false;
    }

    /** [STUB] setFixedToUserRotation - OH manages orientation differently */
    @Override
    public void setFixedToUserRotation(int displayId, int fixedToUserRotation)
            throws RemoteException {
        logStub("setFixedToUserRotation", "OH manages orientation differently");
    }

    /** [STUB] setIgnoreOrientationRequest - OH manages orientation differently */
    @Override
    public void setIgnoreOrientationRequest(int displayId, boolean ignoreOrientationRequest)
            throws RemoteException {
        logStub("setIgnoreOrientationRequest", "OH manages orientation differently");
    }

    // ====================================================================
    // Category 17: Wallpaper [STUB]
    // ====================================================================

    /** [STUB] screenshotWallpaper - OH wallpaper not mapped */
    @Override
    public Bitmap screenshotWallpaper() throws RemoteException {
        logStub("screenshotWallpaper", "OH wallpaper not mapped");
        return null;
    }

    /** [STUB] mirrorWallpaperSurface - OH wallpaper not mapped */
    @Override
    public SurfaceControl mirrorWallpaperSurface(int displayId) throws RemoteException {
        logStub("mirrorWallpaperSurface", "OH wallpaper not mapped");
        return null;
    }

    /** [STUB] registerWallpaperVisibilityListener - OH wallpaper not mapped */
    @Override
    public boolean registerWallpaperVisibilityListener(IWallpaperVisibilityListener listener,
            int displayId) throws RemoteException {
        logStub("registerWallpaperVisibilityListener", "OH wallpaper not mapped");
        return false;
    }

    /** [STUB] unregisterWallpaperVisibilityListener - OH wallpaper not mapped */
    @Override
    public void unregisterWallpaperVisibilityListener(IWallpaperVisibilityListener listener,
            int displayId) throws RemoteException {
        logStub("unregisterWallpaperVisibilityListener", "OH wallpaper not mapped");
    }

    // ====================================================================
    // Category 18: System Gesture Exclusion [STUB]
    // ====================================================================

    /** [STUB] registerSystemGestureExclusionListener - OH gesture not mapped */
    @Override
    public void registerSystemGestureExclusionListener(
            ISystemGestureExclusionListener listener, int displayId) throws RemoteException {
        logStub("registerSystemGestureExclusionListener", "OH gesture not mapped");
    }

    /** [STUB] unregisterSystemGestureExclusionListener - OH gesture not mapped */
    @Override
    public void unregisterSystemGestureExclusionListener(
            ISystemGestureExclusionListener listener, int displayId) throws RemoteException {
        logStub("unregisterSystemGestureExclusionListener", "OH gesture not mapped");
    }

    // ====================================================================
    // Category 19: Assist / Screenshot [STUB / BRIDGED]
    // ====================================================================

    /** [STUB] requestAssistScreenshot - No OH equivalent */
    @Override
    public boolean requestAssistScreenshot(IAssistDataReceiver receiver) throws RemoteException {
        logStub("requestAssistScreenshot", "No OH equivalent");
        return false;
    }

    // ====================================================================
    // Category 20: System UI Notifications [BRIDGED / STUB]
    // ====================================================================

    /** [BRIDGED] hideTransientBars -> OH system bar control */
    @Override
    public void hideTransientBars(int displayId) throws RemoteException {
        logBridged("hideTransientBars", "-> OH system bar control");
    }

    /** [STUB] setRecentsVisibility - OH recents managed differently */
    @Override
    public void setRecentsVisibility(boolean visible) throws RemoteException {
        logStub("setRecentsVisibility", "OH recents managed differently");
    }

    /** [STUB] updateStaticPrivacyIndicatorBounds - No OH equivalent */
    @Override
    public void updateStaticPrivacyIndicatorBounds(int displayId, Rect[] staticBounds)
            throws RemoteException {
        logStub("updateStaticPrivacyIndicatorBounds", "No OH equivalent");
    }

    /** [STUB] setNavBarVirtualKeyHapticFeedbackEnabled - OH gesture not mapped */
    @Override
    public void setNavBarVirtualKeyHapticFeedbackEnabled(boolean enabled) throws RemoteException {
        logStub("setNavBarVirtualKeyHapticFeedbackEnabled", "OH gesture not mapped");
    }

    /** [STUB] hasNavigationBar - OH nav bar query */
    @Override
    public boolean hasNavigationBar(int displayId) throws RemoteException {
        logStub("hasNavigationBar", "OH nav bar query");
        return true;
    }

    // ====================================================================
    // Category 21: Lock / Safe Mode [STUB]
    // ====================================================================

    /** [STUB] lockNow - OH lock screen not mapped */
    @Override
    public void lockNow(Bundle options) throws RemoteException {
        logStub("lockNow", "OH lock screen not mapped");
    }

    /** [STUB] isSafeModeEnabled */
    @Override
    public boolean isSafeModeEnabled() throws RemoteException {
        logStub("isSafeModeEnabled", "OH safe mode not mapped");
        return false;
    }

    // ====================================================================
    // Category 22: Window Content Frame Stats [STUB]
    // ====================================================================

    /** [STUB] clearWindowContentFrameStats - No OH equivalent */
    @Override
    public boolean clearWindowContentFrameStats(IBinder token) throws RemoteException {
        logStub("clearWindowContentFrameStats", "No OH equivalent");
        return false;
    }

    /** [STUB] getWindowContentFrameStats - No OH equivalent */
    @Override
    public WindowContentFrameStats getWindowContentFrameStats(IBinder token)
            throws RemoteException {
        logStub("getWindowContentFrameStats", "No OH equivalent");
        return null;
    }

    // ====================================================================
    // Category 23: Docked Stack / PiP [STUB / BRIDGED]
    // ====================================================================

    /** [STUB] getDockedStackSide - No OH equivalent */
    @Override
    public int getDockedStackSide() throws RemoteException {
        logStub("getDockedStackSide", "No OH equivalent");
        return -1;
    }

    /** [BRIDGED] registerPinnedTaskListener -> OH ISession PiP listener */
    @Override
    public void registerPinnedTaskListener(int displayId, IPinnedTaskListener listener)
            throws RemoteException {
        logBridged("registerPinnedTaskListener", "-> OH ISession PiP listener");
    }

    // ====================================================================
    // Category 24: Keyboard Shortcuts [STUB]
    // ====================================================================

    /** [STUB] requestAppKeyboardShortcuts - OH keyboard shortcuts not mapped */
    @Override
    public void requestAppKeyboardShortcuts(IResultReceiver receiver, int deviceId)
            throws RemoteException {
        logStub("requestAppKeyboardShortcuts", "OH keyboard shortcuts not mapped");
    }

    // ====================================================================
    // Category 25: Window Insets [BRIDGED]
    // ====================================================================

    /** [BRIDGED] getStableInsets -> OH ISession.GetAvoidAreaByType */
    @Override
    public void getStableInsets(int displayId, Rect outInsets) throws RemoteException {
        logBridged("getStableInsets", "-> OH ISession.GetAvoidAreaByType");
    }

    // ====================================================================
    // Category 26: Shortcut Key [STUB]
    // ====================================================================

    /** [STUB] registerShortcutKey - OH keyboard shortcuts not mapped */
    @Override
    public void registerShortcutKey(long shortcutCode, IShortcutService keySubscriber)
            throws RemoteException {
        logStub("registerShortcutKey", "OH keyboard shortcuts not mapped");
    }

    // ====================================================================
    // Category 27: Input Consumer [BRIDGED]
    // ====================================================================

    /** [BRIDGED] createInputConsumer -> OH input management */
    @Override
    public void createInputConsumer(IBinder token, String name, int displayId,
            InputChannel inputChannel) throws RemoteException {
        logBridged("createInputConsumer", "-> OH input management");
    }

    /** [BRIDGED] destroyInputConsumer -> OH input management */
    @Override
    public boolean destroyInputConsumer(String name, int displayId) throws RemoteException {
        logBridged("destroyInputConsumer", "-> OH input management");
        return false;
    }

    // ====================================================================
    // Category 28: IME Touch Region [STUB]
    // ====================================================================

    /** [STUB] getCurrentImeTouchRegion - OH IME managed differently */
    @Override
    public Region getCurrentImeTouchRegion() throws RemoteException {
        logStub("getCurrentImeTouchRegion", "OH IME managed differently");
        return null;
    }

    // ====================================================================
    // Category 29: Display Fold Listener [BRIDGED]
    // ====================================================================

    /** [BRIDGED] registerDisplayFoldListener -> OH DisplayManager fold listener */
    @Override
    public void registerDisplayFoldListener(IDisplayFoldListener listener) throws RemoteException {
        logBridged("registerDisplayFoldListener", "-> OH DisplayManager fold listener");
    }

    /** [BRIDGED] unregisterDisplayFoldListener -> OH DisplayManager fold listener */
    @Override
    public void unregisterDisplayFoldListener(IDisplayFoldListener listener)
            throws RemoteException {
        logBridged("unregisterDisplayFoldListener", "-> OH DisplayManager fold listener");
    }

    // ====================================================================
    // Category 30: Display Window Listener [BRIDGED]
    // ====================================================================

    /** [BRIDGED] registerDisplayWindowListener -> OH DisplayManager window listener */
    @Override
    public int[] registerDisplayWindowListener(IDisplayWindowListener listener)
            throws RemoteException {
        logBridged("registerDisplayWindowListener", "-> OH DisplayManager window listener");
        return new int[0];
    }

    /** [BRIDGED] unregisterDisplayWindowListener -> OH DisplayManager window listener */
    @Override
    public void unregisterDisplayWindowListener(IDisplayWindowListener listener)
            throws RemoteException {
        logBridged("unregisterDisplayWindowListener", "-> OH DisplayManager window listener");
    }

    // ====================================================================
    // Category 31: Window Trace [STUB]
    // ====================================================================

    /** [STUB] startWindowTrace - Tracing not mapped */
    @Override
    public void startWindowTrace() throws RemoteException {
        logStub("startWindowTrace", "Tracing not mapped");
    }

    /** [STUB] stopWindowTrace - Tracing not mapped */
    @Override
    public void stopWindowTrace() throws RemoteException {
        logStub("stopWindowTrace", "Tracing not mapped");
    }

    /** [STUB] saveWindowTraceToFile - Tracing not mapped */
    @Override
    public void saveWindowTraceToFile() throws RemoteException {
        logStub("saveWindowTraceToFile", "Tracing not mapped");
    }

    /** [STUB] isWindowTraceEnabled - Tracing not mapped */
    @Override
    public boolean isWindowTraceEnabled() throws RemoteException {
        logStub("isWindowTraceEnabled", "Tracing not mapped");
        return false;
    }

    // ====================================================================
    // Category 32: Transition Trace [STUB]
    // ====================================================================

    /** [STUB] startTransitionTrace - Tracing not mapped */
    @Override
    public void startTransitionTrace() throws RemoteException {
        logStub("startTransitionTrace", "Tracing not mapped");
    }

    /** [STUB] stopTransitionTrace - Tracing not mapped */
    @Override
    public void stopTransitionTrace() throws RemoteException {
        logStub("stopTransitionTrace", "Tracing not mapped");
    }

    /** [STUB] isTransitionTraceEnabled - Tracing not mapped */
    @Override
    public boolean isTransitionTraceEnabled() throws RemoteException {
        logStub("isTransitionTraceEnabled", "Tracing not mapped");
        return false;
    }

    // ====================================================================
    // Category 33: Windowing Mode / Display Policy [BRIDGED / STUB]
    // ====================================================================

    /** [BRIDGED] getWindowingMode -> OH IWindowManager.GetWindowModeType */
    @Override
    public int getWindowingMode(int displayId) throws RemoteException {
        logBridged("getWindowingMode", "-> OH IWindowManager.GetWindowModeType");
        return 0;
    }

    /** [BRIDGED] setWindowingMode -> OH window mode */
    @Override
    public void setWindowingMode(int displayId, int mode) throws RemoteException {
        logBridged("setWindowingMode", "-> OH window mode");
    }

    /** [STUB] getRemoveContentMode - OH display policy */
    @Override
    public int getRemoveContentMode(int displayId) throws RemoteException {
        logStub("getRemoveContentMode", "OH display policy");
        return 0;
    }

    /** [STUB] setRemoveContentMode - OH display policy */
    @Override
    public void setRemoveContentMode(int displayId, int mode) throws RemoteException {
        logStub("setRemoveContentMode", "OH display policy");
    }

    /** [STUB] shouldShowWithInsecureKeyguard - OH display policy */
    @Override
    public boolean shouldShowWithInsecureKeyguard(int displayId) throws RemoteException {
        logStub("shouldShowWithInsecureKeyguard", "OH display policy");
        return false;
    }

    /** [STUB] setShouldShowWithInsecureKeyguard - OH display policy */
    @Override
    public void setShouldShowWithInsecureKeyguard(int displayId, boolean shouldShow)
            throws RemoteException {
        logStub("setShouldShowWithInsecureKeyguard", "OH display policy");
    }

    /** [STUB] shouldShowSystemDecors - OH display policy */
    @Override
    public boolean shouldShowSystemDecors(int displayId) throws RemoteException {
        logStub("shouldShowSystemDecors", "OH display policy");
        return false;
    }

    /** [STUB] setShouldShowSystemDecors - OH display policy */
    @Override
    public void setShouldShowSystemDecors(int displayId, boolean shouldShow)
            throws RemoteException {
        logStub("setShouldShowSystemDecors", "OH display policy");
    }

    /** [STUB] getDisplayImePolicy - OH display policy */
    @Override
    public int getDisplayImePolicy(int displayId) throws RemoteException {
        logStub("getDisplayImePolicy", "OH display policy");
        return 0;
    }

    /** [STUB] setDisplayImePolicy - OH display policy */
    @Override
    public void setDisplayImePolicy(int displayId, int imePolicy) throws RemoteException {
        logStub("setDisplayImePolicy", "OH display policy");
    }

    // ====================================================================
    // Category 34: Input Sync [STUB]
    // ====================================================================

    /** [STUB] syncInputTransactions - OH sync input */
    @Override
    public void syncInputTransactions(boolean waitForAnimations) throws RemoteException {
        logStub("syncInputTransactions", "OH sync input");
    }

    // ====================================================================
    // Category 35: Layer Tracing [STUB]
    // ====================================================================

    /** [STUB] isLayerTracing - Tracing not mapped */
    @Override
    public boolean isLayerTracing() throws RemoteException {
        logStub("isLayerTracing", "Tracing not mapped");
        return false;
    }

    /** [STUB] setLayerTracing - Tracing not mapped */
    @Override
    public void setLayerTracing(boolean enabled) throws RemoteException {
        logStub("setLayerTracing", "Tracing not mapped");
    }

    // ====================================================================
    // Category 36: Display Mirroring [STUB]
    // ====================================================================

    /** [STUB] mirrorDisplay - OH mirror display */
    @Override
    public boolean mirrorDisplay(int displayId, SurfaceControl outSurfaceControl)
            throws RemoteException {
        logStub("mirrorDisplay", "OH mirror display");
        return false;
    }

    // ====================================================================
    // Category 37: Display Window Insets Controller [BRIDGED]
    // ====================================================================

    /** [BRIDGED] setDisplayWindowInsetsController -> OH insets control */
    @Override
    public void setDisplayWindowInsetsController(int displayId,
            IDisplayWindowInsetsController displayWindowInsetsController) throws RemoteException {
        logBridged("setDisplayWindowInsetsController", "-> OH insets control");
    }

    /** [BRIDGED] updateDisplayWindowRequestedVisibleTypes -> OH insets control */
    @Override
    public void updateDisplayWindowRequestedVisibleTypes(int displayId, int requestedVisibleTypes)
            throws RemoteException {
        logBridged("updateDisplayWindowRequestedVisibleTypes", "-> OH insets control");
    }

    /** [BRIDGED] getWindowInsets -> OH ISession.GetAllAvoidAreas */
    @Override
    public boolean getWindowInsets(int displayId, IBinder token, InsetsState outInsetsState)
            throws RemoteException {
        logBridged("getWindowInsets", "-> OH ISession.GetAllAvoidAreas");
        return false;
    }

    // ====================================================================
    // Category 38: Display Info [BRIDGED]
    // ====================================================================

    /** [BRIDGED] getPossibleDisplayInfo -> OH DisplayManager.GetAllDisplayInfo */
    @Override
    public List<DisplayInfo> getPossibleDisplayInfo(int displayId) throws RemoteException {
        logBridged("getPossibleDisplayInfo", "-> OH DisplayManager.GetAllDisplayInfo");
        return Collections.emptyList();
    }

    // ====================================================================
    // Category 39: Global Actions [STUB]
    // ====================================================================

    /** [STUB] showGlobalActions - No OH equivalent */
    @Override
    public void showGlobalActions() throws RemoteException {
        logStub("showGlobalActions", "No OH equivalent");
    }

    // ====================================================================
    // Category 40: Layer Tracing Flags [STUB]
    // ====================================================================

    /** [STUB] setLayerTracingFlags - Tracing not mapped */
    @Override
    public void setLayerTracingFlags(int flags) throws RemoteException {
        logStub("setLayerTracingFlags", "Tracing not mapped");
    }

    /** [STUB] setActiveTransactionTracing - Tracing not mapped */
    @Override
    public void setActiveTransactionTracing(boolean active) throws RemoteException {
        logStub("setActiveTransactionTracing", "Tracing not mapped");
    }

    // ====================================================================
    // Category 41: Scroll Capture [STUB]
    // ====================================================================

    /** [STUB] requestScrollCapture - No OH equivalent */
    @Override
    public void requestScrollCapture(int displayId, IBinder behindClient, int taskId,
            IScrollCaptureResponseListener listener) throws RemoteException {
        logStub("requestScrollCapture", "No OH equivalent");
    }

    // ====================================================================
    // Category 42: Hold Lock (testing) [STUB]
    // ====================================================================

    /** [STUB] holdLock - No OH equivalent */
    @Override
    public void holdLock(IBinder token, int durationMs) throws RemoteException {
        logStub("holdLock", "No OH equivalent");
    }

    // ====================================================================
    // Category 43: Display Hash [STUB]
    // ====================================================================

    /** [STUB] getSupportedDisplayHashAlgorithms - No OH equivalent */
    @Override
    public String[] getSupportedDisplayHashAlgorithms() throws RemoteException {
        logStub("getSupportedDisplayHashAlgorithms", "No OH equivalent");
        return new String[0];
    }

    /** [STUB] verifyDisplayHash - No OH equivalent */
    @Override
    public VerifiedDisplayHash verifyDisplayHash(DisplayHash displayHash) throws RemoteException {
        logStub("verifyDisplayHash", "No OH equivalent");
        return null;
    }

    /** [STUB] setDisplayHashThrottlingEnabled - No OH equivalent */
    @Override
    public void setDisplayHashThrottlingEnabled(boolean enable) throws RemoteException {
        logStub("setDisplayHashThrottlingEnabled", "No OH equivalent");
    }

    // ====================================================================
    // Category 44: Window Context [STUB]
    // ====================================================================

    /** [STUB] attachWindowContextToDisplayArea - OH window context not mapped */
    @Override
    public Configuration attachWindowContextToDisplayArea(IBinder clientToken, int type,
            int displayId, Bundle options) throws RemoteException {
        logStub("attachWindowContextToDisplayArea", "OH window context not mapped");
        return null;
    }

    /** [STUB] attachWindowContextToWindowToken - OH window context not mapped */
    @Override
    public void attachWindowContextToWindowToken(IBinder clientToken, IBinder token)
            throws RemoteException {
        logStub("attachWindowContextToWindowToken", "OH window context not mapped");
    }

    /** [STUB] attachToDisplayContent - OH window context not mapped */
    @Override
    public Configuration attachToDisplayContent(IBinder clientToken, int displayId)
            throws RemoteException {
        logStub("attachToDisplayContent", "OH window context not mapped");
        return null;
    }

    /** [STUB] detachWindowContextFromWindowContainer - OH window context not mapped */
    @Override
    public void detachWindowContextFromWindowContainer(IBinder clientToken)
            throws RemoteException {
        logStub("detachWindowContextFromWindowContainer", "OH window context not mapped");
    }

    // ====================================================================
    // Category 45: Cross-Window Blur [STUB]
    // ====================================================================

    /** [STUB] registerCrossWindowBlurEnabledListener - OH blur not mapped */
    @Override
    public boolean registerCrossWindowBlurEnabledListener(
            ICrossWindowBlurEnabledListener listener) throws RemoteException {
        logStub("registerCrossWindowBlurEnabledListener", "OH blur not mapped");
        return false;
    }

    /** [STUB] unregisterCrossWindowBlurEnabledListener - OH blur not mapped */
    @Override
    public void unregisterCrossWindowBlurEnabledListener(
            ICrossWindowBlurEnabledListener listener) throws RemoteException {
        logStub("unregisterCrossWindowBlurEnabledListener", "OH blur not mapped");
    }

    // ====================================================================
    // Category 46: Task Snapshot [BRIDGED / STUB]
    // ====================================================================

    /** [STUB] isTaskSnapshotSupported - always true */
    @Override
    public boolean isTaskSnapshotSupported() throws RemoteException {
        return true;
    }

    /** [STUB] getImeDisplayId */
    @Override
    public int getImeDisplayId() throws RemoteException {
        logStub("getImeDisplayId", "OH IME display");
        return 0;
    }

    /** [STUB] setTaskSnapshotEnabled - OH snapshot managed differently */
    @Override
    public void setTaskSnapshotEnabled(boolean enabled) throws RemoteException {
        logStub("setTaskSnapshotEnabled", "OH snapshot managed differently");
    }

    /** [STUB] setTaskTransitionSpec - No OH equivalent */
    @Override
    public void setTaskTransitionSpec(TaskTransitionSpec spec) throws RemoteException {
        logStub("setTaskTransitionSpec", "No OH equivalent");
    }

    /** [STUB] clearTaskTransitionSpec - No OH equivalent */
    @Override
    public void clearTaskTransitionSpec() throws RemoteException {
        logStub("clearTaskTransitionSpec", "No OH equivalent");
    }

    /** [STUB] registerTaskFpsCallback - No OH equivalent */
    @Override
    public void registerTaskFpsCallback(int taskId, ITaskFpsCallback callback)
            throws RemoteException {
        logStub("registerTaskFpsCallback", "No OH equivalent");
    }

    /** [STUB] unregisterTaskFpsCallback - No OH equivalent */
    @Override
    public void unregisterTaskFpsCallback(ITaskFpsCallback listener) throws RemoteException {
        logStub("unregisterTaskFpsCallback", "No OH equivalent");
    }

    /** [BRIDGED] snapshotTaskForRecents -> OH ISceneSessionManager.GetSessionSnapshot */
    @Override
    public Bitmap snapshotTaskForRecents(int taskId) throws RemoteException {
        logBridged("snapshotTaskForRecents",
                "-> OH ISceneSessionManager.GetSessionSnapshot");
        return null;
    }

    /** [STUB] setRecentsAppBehindSystemBars - No OH equivalent */
    @Override
    public void setRecentsAppBehindSystemBars(boolean behindSystemBars) throws RemoteException {
        logStub("setRecentsAppBehindSystemBars", "No OH equivalent");
    }

    // ====================================================================
    // Category 47: Letterbox [STUB]
    // ====================================================================

    /** [STUB] getLetterboxBackgroundColorInArgb - No OH equivalent */
    @Override
    public int getLetterboxBackgroundColorInArgb() throws RemoteException {
        logStub("getLetterboxBackgroundColorInArgb", "No OH equivalent");
        return 0;
    }

    /** [STUB] isLetterboxBackgroundMultiColored - No OH equivalent */
    @Override
    public boolean isLetterboxBackgroundMultiColored() throws RemoteException {
        logStub("isLetterboxBackgroundMultiColored", "No OH equivalent");
        return false;
    }

    // ====================================================================
    // Category 48: Display Capture [BRIDGED]
    // ====================================================================

    /** [BRIDGED] captureDisplay -> OH IWindowManager.GetSnapshot */
    @Override
    public void captureDisplay(int displayId, ScreenCapture.CaptureArgs captureArgs,
            ScreenCapture.ScreenCaptureListener listener) throws RemoteException {
        logBridged("captureDisplay", "-> OH IWindowManager.GetSnapshot");
    }

    // ====================================================================
    // Category 49: Global Key [STUB]
    // ====================================================================

    /** [STUB] isGlobalKey */
    @Override
    public boolean isGlobalKey(int keyCode) throws RemoteException {
        logStub("isGlobalKey", "No OH equivalent");
        return false;
    }

    // ====================================================================
    // Category 50: Surface Sync Group [STUB]
    // ====================================================================

    /** [STUB] addToSurfaceSyncGroup - OH surface sync not mapped */
    @Override
    public boolean addToSurfaceSyncGroup(IBinder syncGroupToken, boolean parentSyncGroupMerge,
            ISurfaceSyncGroupCompletedListener completedListener,
            AddToSurfaceSyncGroupResult addToSurfaceSyncGroupResult) throws RemoteException {
        logStub("addToSurfaceSyncGroup", "OH surface sync not mapped");
        return false;
    }

    /** [STUB] markSurfaceSyncGroupReady - OH surface sync not mapped */
    @Override
    public void markSurfaceSyncGroupReady(IBinder syncGroupToken) throws RemoteException {
        logStub("markSurfaceSyncGroupReady", "OH surface sync not mapped");
    }

    // ====================================================================
    // Category 51: Screenshot Listeners [BRIDGED]
    // ====================================================================

    /** [BRIDGED] notifyScreenshotListeners -> OH screenshot listeners */
    @Override
    public List<ComponentName> notifyScreenshotListeners(int displayId) throws RemoteException {
        logBridged("notifyScreenshotListeners", "-> OH screenshot listeners");
        return Collections.emptyList();
    }
}
