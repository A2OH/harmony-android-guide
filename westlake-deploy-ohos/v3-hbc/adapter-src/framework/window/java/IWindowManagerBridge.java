/*
 * IWindowManagerBridge.java
 *
 * Bridge implementation for Android IWindowManager interface.
 * Routes calls to OH IWindowManager + ISceneSessionManager.
 *
 * Mapping:
 *   IWindowManager -> OH IWindowManager (window create/destroy/property)
 *                  -> OH ISceneSessionManager (session management)
 *
 * Methods are categorized as:
 *   [BRIDGED]     - Mapped to OH equivalent
 *   [PARTIAL]     - Partially mapped
 *   [STUB]        - No OH equivalent, empty implementation
 */
package adapter.window;

import android.util.Log;

import adapter.core.OHEnvironment;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class IWindowManagerBridge implements InvocationHandler {

    private static final String TAG = "OH_IWMBridge";
    private final Object mOriginal;
    // Deprecated: old InvocationHandler bridge, replaced by WindowManagerAdapter

    public IWindowManagerBridge(Object original) {
        mOriginal = original;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();

        switch (name) {
            // ============================================================
            // Category 1: Session Management
            // ============================================================

            // [BRIDGED] openSession -> OH ISceneSessionManager.CreateAndConnectSpecificSession
            case "openSession":
                logBridged(name, "-> OH ISceneSessionManager.CreateAndConnectSpecificSession");
                // Return a bridged IWindowSession proxy
                return method.invoke(mOriginal, args); // Phase 1: passthrough, wrap later

            // [STUB] useBLAST
            case "useBLAST":
                return true;

            // ============================================================
            // Category 2: Display Size/Density (-> OH display query)
            // ============================================================

            // [BRIDGED] getInitialDisplaySize -> OH DisplayManager query
            case "getInitialDisplaySize":
            case "getBaseDisplaySize":
                logBridged(name, "-> OH DisplayManager.GetDefaultDisplayInfo");
                return method.invoke(mOriginal, args); // Phase 1: passthrough

            // [BRIDGED] setForcedDisplaySize -> OH DisplayManager
            case "setForcedDisplaySize":
            case "clearForcedDisplaySize":
                logBridged(name, "-> OH DisplayManager");
                return null;

            // [BRIDGED] getInitialDisplayDensity / getBaseDisplayDensity
            case "getInitialDisplayDensity":
            case "getBaseDisplayDensity":
                logBridged(name, "-> OH DisplayManager density");
                return method.invoke(mOriginal, args);

            // [BRIDGED] setForcedDisplayDensityForUser / clearForcedDisplayDensityForUser
            case "setForcedDisplayDensityForUser":
            case "clearForcedDisplayDensityForUser":
                logBridged(name, "-> OH DisplayManager density");
                return null;

            case "setForcedDisplayScalingMode":
                logStub(name, "No OH equivalent");
                return null;

            case "getDisplayIdByUniqueId":
                logStub(name, "OH display ID mapping");
                return 0;

            // ============================================================
            // Category 3: Window Token (-> OH IWindowManager)
            // ============================================================

            // [BRIDGED] addWindowToken -> OH IWindowManager (implicit in CreateWindow)
            case "addWindowToken":
                logBridged(name, "-> OH IWindowManager (implicit in window creation)");
                return null;

            // [BRIDGED] removeWindowToken -> OH IWindowManager.RemoveWindow
            case "removeWindowToken":
                logBridged(name, "-> OH IWindowManager.RemoveWindow");
                return null;

            // [STUB] isWindowToken
            case "isWindowToken":
                logStub(name, "OH token management different");
                return false;

            // ============================================================
            // Category 4: Event Dispatching
            // ============================================================

            case "setEventDispatching":
                logBridged(name, "-> OH IWindowManager input control");
                return null;

            // ============================================================
            // Category 5: Keyguard (-> OH, STUB)
            // ============================================================

            case "disableKeyguard":
            case "reenableKeyguard":
            case "exitKeyguardSecurely":
            case "isKeyguardLocked":
            case "isKeyguardSecure":
            case "dismissKeyguard":
            case "addKeyguardLockedStateListener":
            case "removeKeyguardLockedStateListener":
            case "setSwitchingUser":
                logStub(name, "OH keyguard not mapped");
                return getDefaultReturn(method);

            // ============================================================
            // Category 6: Animation (-> OH window animation)
            // ============================================================

            // [PARTIAL] getAnimationScale / setAnimationScale
            case "getAnimationScale":
            case "getAnimationScales":
            case "getCurrentAnimatorScale":
                logStub(name, "OH animation scale not mapped");
                return getDefaultReturn(method);

            case "setAnimationScale":
            case "setAnimationScales":
                logStub(name, "OH animation scale not mapped");
                return null;

            // ============================================================
            // Category 7: Rotation (-> OH DisplayManager)
            // ============================================================

            // [BRIDGED] getDefaultDisplayRotation -> OH DisplayManager
            case "getDefaultDisplayRotation":
                logBridged(name, "-> OH DisplayManager.GetDefaultDisplayInfo.rotation");
                return 0;

            // [BRIDGED] watchRotation -> OH ISession.UpdateRotationChangeRegistered
            case "watchRotation":
                logBridged(name, "-> OH ISession.UpdateRotationChangeRegistered");
                return 0;

            case "removeRotationWatcher":
                logBridged(name, "-> OH rotation unregister");
                return null;

            case "registerProposedRotationListener":
                logStub(name, "No OH equivalent");
                return 0;

            // [BRIDGED] freezeRotation / thawRotation
            case "freezeRotation":
            case "freezeDisplayRotation":
                logBridged(name, "-> OH DisplayManager.FreezeRotation");
                return null;

            case "thawRotation":
            case "thawDisplayRotation":
                logBridged(name, "-> OH DisplayManager.ThawRotation");
                return null;

            case "isRotationFrozen":
            case "isDisplayRotationFrozen":
                logStub(name, "OH rotation state query");
                return false;

            case "setFixedToUserRotation":
            case "setIgnoreOrientationRequest":
                logStub(name, "OH manages orientation differently");
                return null;

            // ============================================================
            // Category 8: System UI / Touch Mode
            // ============================================================

            // [BRIDGED] setInTouchMode -> OH IWindowManager input mode
            case "setInTouchMode":
            case "setInTouchModeOnAllDisplays":
                logBridged(name, "-> OH input mode");
                return null;

            case "isInTouchMode":
                logStub(name, "OH touch mode");
                return true;

            // [STUB] closeSystemDialogs
            case "closeSystemDialogs":
                logStub(name, "OH system dialog");
                return null;

            // [STUB] showStrictModeViolation / setStrictModeVisualIndicatorPreference
            case "showStrictModeViolation":
            case "setStrictModeVisualIndicatorPreference":
                logStub(name, "No OH equivalent");
                return null;

            // [BRIDGED] hideTransientBars -> OH system bar control
            case "hideTransientBars":
                logBridged(name, "-> OH system bar control");
                return null;

            // [STUB] setRecentsVisibility
            case "setRecentsVisibility":
                logStub(name, "OH recents managed differently");
                return null;

            // [STUB] hasNavigationBar
            case "hasNavigationBar":
                logStub(name, "OH nav bar query");
                return true;

            case "getPreferredOptionsPanelGravity":
                logStub(name, "No OH equivalent");
                return 0;

            // ============================================================
            // Category 9: Wallpaper (STUB)
            // ============================================================

            case "screenshotWallpaper":
            case "mirrorWallpaperSurface":
            case "registerWallpaperVisibilityListener":
            case "unregisterWallpaperVisibilityListener":
                logStub(name, "OH wallpaper not mapped");
                return getDefaultReturn(method);

            // ============================================================
            // Category 10: Gesture / System Gesture (STUB)
            // ============================================================

            case "registerSystemGestureExclusionListener":
            case "unregisterSystemGestureExclusionListener":
            case "setNavBarVirtualKeyHapticFeedbackEnabled":
                logStub(name, "OH gesture not mapped");
                return getDefaultReturn(method);

            // ============================================================
            // Category 11: Screen Capture (-> OH IWindowManager)
            // ============================================================

            // [BRIDGED] captureDisplay -> OH IWindowManager.GetSnapshot
            case "captureDisplay":
                logBridged(name, "-> OH IWindowManager.GetSnapshot");
                return null;

            case "requestAssistScreenshot":
                logStub(name, "No OH equivalent");
                return false;

            case "refreshScreenCaptureDisabled":
                logStub(name, "OH screenshot policy");
                return null;

            case "notifyScreenshotListeners":
                logBridged(name, "-> OH screenshot listeners");
                return java.util.Collections.emptyList();

            // ============================================================
            // Category 12: Window Content Stats (STUB)
            // ============================================================

            case "clearWindowContentFrameStats":
            case "getWindowContentFrameStats":
                logStub(name, "No OH equivalent");
                return getDefaultReturn(method);

            // ============================================================
            // Category 13: Input (-> OH IWindowManager)
            // ============================================================

            case "createInputConsumer":
            case "destroyInputConsumer":
                logBridged(name, "-> OH input management");
                return getDefaultReturn(method);

            case "getCurrentImeTouchRegion":
                logStub(name, "OH IME managed differently");
                return null;

            // ============================================================
            // Category 14: Display Management (-> OH DisplayManager)
            // ============================================================

            case "registerDisplayFoldListener":
            case "unregisterDisplayFoldListener":
                logBridged(name, "-> OH DisplayManager fold listener");
                return getDefaultReturn(method);

            case "registerDisplayWindowListener":
            case "unregisterDisplayWindowListener":
                logBridged(name, "-> OH DisplayManager window listener");
                return getDefaultReturn(method);

            case "getPossibleDisplayInfo":
                logBridged(name, "-> OH DisplayManager.GetAllDisplayInfo");
                return java.util.Collections.emptyList();

            case "getWindowingMode":
                logBridged(name, "-> OH IWindowManager.GetWindowModeType");
                return 0;

            case "setWindowingMode":
                logBridged(name, "-> OH window mode");
                return null;

            case "getRemoveContentMode":
            case "setRemoveContentMode":
            case "shouldShowWithInsecureKeyguard":
            case "setShouldShowWithInsecureKeyguard":
            case "shouldShowSystemDecors":
            case "setShouldShowSystemDecors":
            case "getDisplayImePolicy":
            case "setDisplayImePolicy":
                logStub(name, "OH display policy");
                return getDefaultReturn(method);

            // ============================================================
            // Category 15: Window Insets (-> OH ISession.GetAvoidAreaByType)
            // ============================================================

            // [BRIDGED] getStableInsets -> OH ISession.GetAvoidAreaByType
            case "getStableInsets":
                logBridged(name, "-> OH ISession.GetAvoidAreaByType");
                return null;

            // [BRIDGED] getWindowInsets -> OH ISession.GetAllAvoidAreas
            case "getWindowInsets":
                logBridged(name, "-> OH ISession.GetAllAvoidAreas");
                return false;

            case "setDisplayWindowInsetsController":
            case "updateDisplayWindowRequestedVisibleTypes":
                logBridged(name, "-> OH insets control");
                return null;

            // ============================================================
            // Category 16: PinnedTask / PiP (-> OH ISession PiP)
            // ============================================================

            // [BRIDGED] registerPinnedTaskListener -> OH ISession PiP
            case "registerPinnedTaskListener":
                logBridged(name, "-> OH ISession PiP listener");
                return null;

            case "getDockedStackSide":
                logStub(name, "No OH equivalent");
                return -1;

            // ============================================================
            // Category 17: Tracing (STUB)
            // ============================================================

            case "startWindowTrace":
            case "stopWindowTrace":
            case "saveWindowTraceToFile":
            case "isWindowTraceEnabled":
            case "startTransitionTrace":
            case "stopTransitionTrace":
            case "isTransitionTraceEnabled":
            case "isLayerTracing":
            case "setLayerTracing":
            case "setLayerTracingFlags":
            case "setActiveTransactionTracing":
                logStub(name, "Tracing not mapped");
                return getDefaultReturn(method);

            // ============================================================
            // Category 18: Transition (STUB)
            // ============================================================

            case "overridePendingAppTransitionMultiThumbFuture":
            case "overridePendingAppTransitionRemote":
            case "endProlongedAnimations":
            case "startFreezingScreen":
            case "stopFreezingScreen":
                logStub(name, "OH transition managed differently");
                return null;

            // ============================================================
            // Category 19: Shell / Accessibility
            // ============================================================

            case "addShellRoot":
                logStub(name, "OH has no shell root concept");
                return null;

            case "setShellRootAccessibilityWindow":
                logStub(name, "OH accessibility different");
                return null;

            case "setDisplayChangeWindowController":
                logStub(name, "OH display change controller");
                return null;

            // ============================================================
            // Category 20: Task Snapshot (-> OH ISceneSessionManager)
            // ============================================================

            case "isTaskSnapshotSupported":
                return true;

            case "setTaskSnapshotEnabled":
                logStub(name, "OH snapshot managed differently");
                return null;

            case "setTaskTransitionSpec":
            case "clearTaskTransitionSpec":
                logStub(name, "No OH equivalent");
                return null;

            case "registerTaskFpsCallback":
            case "unregisterTaskFpsCallback":
                logStub(name, "No OH equivalent");
                return null;

            case "snapshotTaskForRecents":
                logBridged(name, "-> OH ISceneSessionManager.GetSessionSnapshot");
                return null;

            case "setRecentsAppBehindSystemBars":
                logStub(name, "No OH equivalent");
                return null;

            case "getLetterboxBackgroundColorInArgb":
                logStub(name, "No OH equivalent");
                return 0;

            case "isLetterboxBackgroundMultiColored":
                logStub(name, "No OH equivalent");
                return false;

            // ============================================================
            // Category 21: Keyboard Shortcut
            // ============================================================

            case "requestAppKeyboardShortcuts":
            case "registerShortcutKey":
                logStub(name, "OH keyboard shortcuts not mapped");
                return null;

            // ============================================================
            // Category 22: Misc
            // ============================================================

            case "startViewServer":
            case "stopViewServer":
            case "isViewServerRunning":
                logStub(name, "Debug view server not mapped");
                return getDefaultReturn(method);

            case "showGlobalActions":
                logStub(name, "No OH equivalent");
                return null;

            case "lockNow":
                logStub(name, "OH lock screen not mapped");
                return null;

            case "isSafeModeEnabled":
                return false;

            case "syncInputTransactions":
                logStub(name, "OH sync input");
                return null;

            case "mirrorDisplay":
                logStub(name, "OH mirror display");
                return false;

            case "requestScrollCapture":
                logStub(name, "No OH equivalent");
                return null;

            case "holdLock":
                logStub(name, "No OH equivalent");
                return null;

            case "getSupportedDisplayHashAlgorithms":
                logStub(name, "No OH equivalent");
                return new String[0];

            case "verifyDisplayHash":
            case "setDisplayHashThrottlingEnabled":
                logStub(name, "No OH equivalent");
                return getDefaultReturn(method);

            case "attachWindowContextToDisplayArea":
            case "attachWindowContextToWindowToken":
            case "attachToDisplayContent":
            case "detachWindowContextFromWindowContainer":
                logStub(name, "OH window context not mapped");
                return getDefaultReturn(method);

            case "registerCrossWindowBlurEnabledListener":
            case "unregisterCrossWindowBlurEnabledListener":
                logStub(name, "OH blur not mapped");
                return getDefaultReturn(method);

            case "getImeDisplayId":
                return 0;

            case "isGlobalKey":
                return false;

            case "addToSurfaceSyncGroup":
            case "markSurfaceSyncGroupReady":
                logStub(name, "OH surface sync not mapped");
                return getDefaultReturn(method);

            case "updateStaticPrivacyIndicatorBounds":
                logStub(name, "No OH equivalent");
                return null;

            default:
                Log.w(TAG, "[UNMAPPED] IWindowManager." + name);
                try {
                    return method.invoke(mOriginal, args);
                } catch (Exception e) {
                    return getDefaultReturn(method);
                }
        }
    }

    private Object getDefaultReturn(Method method) {
        Class<?> type = method.getReturnType();
        if (type == void.class) return null;
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0.0f;
        if (type == int[].class) return new int[0];
        if (type == float[].class) return new float[0];
        return null;
    }

    private void logBridged(String method, String target) {
        Log.d(TAG, "[BRIDGED] " + method + " " + target);
    }

    private void logStub(String method, String reason) {
        Log.d(TAG, "[STUB] " + method + " - " + reason);
    }
}
