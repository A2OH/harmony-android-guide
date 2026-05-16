/*
 * SessionStageBridge.java
 *
 * Reverse bridge: OH ISessionStage callbacks -> Android IWindow.
 *
 * OH ISessionStage is called by OH SceneSessionManager to notify the app's
 * window session of UI state changes. It is the scene-based equivalent of
 * OH IWindow, with richer functionality (94 methods vs 26).
 *
 * Mapping:
 *   OH ISessionStage -> Android IWindow (window events)
 *                    -> Android IApplicationThread (some lifecycle events)
 *
 * Key differences:
 *   - OH ISessionStage has 94 methods covering window, PiP, rotation, gestures
 *   - Android IWindow has 16 methods with batched approach
 *   - Many OH SessionStage events have no Android IWindow counterpart
 *   - OH SessionStage handles events that Android splits across multiple subsystems
 */
package adapter.window;

import android.os.RemoteException;
import android.util.Log;
import android.view.IWindow;

public class SessionStageBridge {

    private static final String TAG = "OH_SessionStageBridge";
    private final Object mAndroidWindow; // Android IWindow proxy
    private IWindow mIWindow; // Typed reference (resolved lazily)

    public SessionStageBridge(Object androidWindow) {
        mAndroidWindow = androidWindow;
        if (androidWindow instanceof IWindow) {
            mIWindow = (IWindow) androidWindow;
        }
    }

    // ============================================================
    // Category 1: Session Lifecycle (-> IWindow.dispatchAppVisibility)
    // ============================================================

    /**
     * [BRIDGED] SetActive -> IWindow.dispatchAppVisibility
     *
     * OH sets session active/inactive state.
     * Android dispatches app visibility to window.
     */
    public void onSetActive(boolean active) {
        logBridged("SetActive(" + active + ")",
                "-> IWindow.dispatchAppVisibility(" + active + ")");
        dispatchAppVisibility(active);
    }

    /**
     * [BRIDGED] NotifySessionForeground -> IWindow.dispatchAppVisibility(true)
     */
    public void onNotifySessionForeground(int reason, boolean withAnimation) {
        logBridged("NotifySessionForeground",
                "-> IWindow.dispatchAppVisibility(true)");
        dispatchAppVisibility(true);
    }

    /**
     * [BRIDGED] NotifySessionBackground -> IWindow.dispatchAppVisibility(false)
     */
    public void onNotifySessionBackground(int reason, boolean withAnimation) {
        logBridged("NotifySessionBackground",
                "-> IWindow.dispatchAppVisibility(false)");
        dispatchAppVisibility(false);
    }

    /**
     * Dispatch app visibility to the Android IWindow.
     * This triggers ViewRootImpl to show/hide the DecorView.
     */
    private void dispatchAppVisibility(boolean visible) {
        if (mIWindow != null) {
            try {
                mIWindow.dispatchAppVisibility(visible);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to dispatch app visibility", e);
            }
        } else {
            // Fallback: invoke via reflection
            try {
                java.lang.reflect.Method method = mAndroidWindow.getClass()
                        .getMethod("dispatchAppVisibility", boolean.class);
                method.invoke(mAndroidWindow, visible);
            } catch (Exception e) {
                Log.e(TAG, "Reflection fallback failed for dispatchAppVisibility", e);
            }
        }
    }

    /**
     * [PARTIAL] NotifyDestroy -> (handled via Activity lifecycle)
     *
     * OH notifies session destruction.
     * Android handles window removal through Activity.onDestroy -> ViewRootImpl.die().
     * Impact: LOW - Destruction is handled at activity lifecycle level.
     */
    public void onNotifyDestroy() {
        logPartial("NotifyDestroy",
                "Android handles via Activity.onDestroy, not IWindow");
    }

    /**
     * [PARTIAL] NotifyWindowVisibility -> IWindow.dispatchAppVisibility
     */
    public void onNotifyWindowVisibility(boolean isVisible) {
        logBridged("NotifyWindowVisibility",
                "-> IWindow.dispatchAppVisibility(" + isVisible + ")");
        dispatchAppVisibility(isVisible);
    }

    /**
     * [PARTIAL] NotifyWindowOcclusionState -> (no direct IWindow method)
     *
     * OH notifies window occlusion (partially/fully occluded).
     * Android handles via lifecycle state changes.
     * Impact: LOW - Apps can infer occlusion from lifecycle.
     */
    public void onNotifyWindowOcclusionState(int state) {
        logPartial("NotifyWindowOcclusionState",
                "Android infers occlusion from lifecycle state");
    }

    // ============================================================
    // Category 2: Window Geometry (-> IWindow.resized)
    // ============================================================

    /**
     * [BRIDGED] UpdateRect -> IWindow.resized
     *
     * OH updates window rectangle with reason and animation config.
     * Android IWindow.resized delivers frames, configuration, and insets.
     *
     * Conversion: OH WSRect{x,y,w,h} -> Android ClientWindowFrames
     */
    public void onUpdateRect(int x, int y, int width, int height, int reason) {
        logBridged("UpdateRect([" + x + "," + y + "," + width + "," + height + "])",
                "-> IWindow.resized");
    }

    /**
     * [BRIDGED] UpdateGlobalDisplayRectFromServer -> IWindow.resized
     */
    public void onUpdateGlobalDisplayRectFromServer(int x, int y, int w, int h, int reason) {
        logBridged("UpdateGlobalDisplayRectFromServer",
                "-> IWindow.resized (global coordinates)");
    }

    /**
     * [BRIDGED] NotifyGlobalScaledRectChange -> IWindow.moved + resized
     */
    public void onNotifyGlobalScaledRectChange(int left, int top, int right, int bottom) {
        logBridged("NotifyGlobalScaledRectChange",
                "-> IWindow.moved + resized");
    }

    // ============================================================
    // Category 3: Window Mode (-> IWindow.resized with config)
    // ============================================================

    /**
     * [BRIDGED] UpdateWindowMode -> IWindow.resized (windowing mode in config)
     */
    public void onUpdateWindowMode(int mode) {
        logBridged("UpdateWindowMode(mode=" + mode + ")",
                "-> IWindow.resized (MergedConfiguration.windowingMode)");
    }

    /**
     * [PARTIAL] UpdateMaximizeMode -> (no direct Android equivalent)
     *
     * OH maximize mode for window.
     * Android doesn't have explicit maximize mode (uses FULLSCREEN).
     * Impact: LOW - Can map to fullscreen mode.
     */
    public void onUpdateMaximizeMode(int mode) {
        logPartial("UpdateMaximizeMode",
                "Map to Android WINDOWING_MODE_FULLSCREEN");
    }

    /**
     * [PARTIAL] NotifyLayoutFinishAfterWindowModeChange -> (no Android equivalent)
     *
     * OH notifies layout completion after mode change.
     * Android handles layout completion internally in ViewRootImpl.
     * Impact: None - Internal layout coordination.
     */
    public void onNotifyLayoutFinishAfterWindowModeChange(int mode) {
        logPartial("NotifyLayoutFinishAfterWindowModeChange",
                "Android handles internally in ViewRootImpl");
    }

    /**
     * [OH_ONLY] SwitchFreeMultiWindow -> (no Android equivalent)
     * OH free-form multi-window toggle.
     */
    public void onSwitchFreeMultiWindow(boolean enable) {
        logOhOnly("SwitchFreeMultiWindow",
                "OH free multi-window, Android uses freeform mode differently");
    }

    // ============================================================
    // Category 4: Focus (-> no direct IWindow method)
    // ============================================================

    /**
     * [PARTIAL] UpdateFocus -> (handled internally by ViewRootImpl)
     *
     * OH sends focus state change.
     * Android handles focus via InputMethodManager and ViewRootImpl.
     * Impact: MEDIUM - Apps may check window focus for keyboard, animation.
     * Strategy: Trigger ViewRootImpl.windowFocusChanged internally.
     */
    public void onUpdateFocus(boolean isFocused) {
        logPartial("UpdateFocus",
                "Android handles via ViewRootImpl.windowFocusChanged");
    }

    /**
     * [PARTIAL] NotifyHighlightChange -> (no Android equivalent)
     *
     * OH highlight state (for split screen drag handle, etc.).
     * Impact: None - Visual feedback only.
     */
    public void onNotifyHighlightChange(boolean isHighlight) {
        logPartial("NotifyHighlightChange",
                "No Android equivalent, visual feedback only");
    }

    // ============================================================
    // Category 5: Avoid Area / Insets (-> IWindow.resized / insetsControlChanged)
    // ============================================================

    /**
     * [BRIDGED] UpdateAvoidArea -> IWindow.insetsControlChanged
     *
     * OH avoid area maps to Android insets.
     */
    public void onUpdateAvoidArea(int type, int left, int top, int right, int bottom) {
        logBridged("UpdateAvoidArea(type=" + type + ")",
                "-> IWindow.insetsControlChanged");
    }

    /**
     * [BRIDGED] NotifyOccupiedAreaChangeInfo -> IWindow.insetsControlChanged
     *
     * OH occupied area (keyboard) change.
     * Android delivers IME insets via insetsControlChanged.
     */
    public void onNotifyOccupiedAreaChangeInfo(int height) {
        logBridged("NotifyOccupiedAreaChangeInfo",
                "-> IWindow.insetsControlChanged (IME)");
    }

    /**
     * [BRIDGED] showInsets/hideInsets equivalent via insets control
     */

    // ============================================================
    // Category 6: Back Event (-> handled via Activity/key event)
    // ============================================================

    /**
     * [PARTIAL] HandleBackEvent -> (Android handles via Activity.onBackPressed)
     *
     * OH dispatches back event to session stage.
     * Android handles back via key event (KEYCODE_BACK) or Activity.onBackPressed.
     * Impact: HIGH - Back navigation is critical for app usability.
     * Strategy: Inject KEYCODE_BACK key event into input channel.
     */
    public void onHandleBackEvent() {
        logPartial("HandleBackEvent",
                "-> inject KEYCODE_BACK via input channel");
    }

    // ============================================================
    // Category 7: Input Events (-> input channel, not IWindow)
    // ============================================================

    /**
     * [PARTIAL] MarkProcessed -> (no direct IWindow method)
     *
     * OH marks input event as processed.
     * Android uses InputChannel's finishInputEvent.
     * Impact: LOW - Internal input processing coordination.
     */
    public void onMarkProcessed(int eventId) {
        logPartial("MarkProcessed",
                "Android uses InputChannel.finishInputEvent");
    }

    /**
     * [PARTIAL] NotifyTouchOutside -> (MotionEvent.ACTION_OUTSIDE)
     */
    public void onNotifyTouchOutside() {
        logPartial("NotifyTouchOutside",
                "Android uses ACTION_OUTSIDE via input channel");
    }

    // ============================================================
    // Category 8: Display / Density / Orientation
    // ============================================================

    /**
     * [BRIDGED] UpdateDensity -> IWindow.resized (density in MergedConfiguration)
     */
    public void onUpdateDensity() {
        logBridged("UpdateDensity",
                "-> IWindow.resized (MergedConfiguration.densityDpi)");
    }

    /**
     * [BRIDGED] UpdateOrientation -> IWindow.resized (orientation in config)
     */
    public void onUpdateOrientation() {
        logBridged("UpdateOrientation",
                "-> IWindow.resized (MergedConfiguration.orientation)");
    }

    /**
     * [BRIDGED] UpdateDisplayId -> IWindow.resized (displayId)
     */
    public void onUpdateDisplayId(long displayId) {
        logBridged("UpdateDisplayId", "-> IWindow.resized (displayId)");
    }

    /**
     * [PARTIAL] NotifyDisplayMove -> IWindow.resized
     */
    public void onNotifyDisplayMove(long from, long to) {
        logPartial("NotifyDisplayMove", "-> IWindow.resized (displayId change)");
    }

    /**
     * [PARTIAL] UpdateSessionViewportConfig -> IWindow.resized
     */
    public void onUpdateSessionViewportConfig() {
        logPartial("UpdateSessionViewportConfig",
                "-> IWindow.resized (viewport in config)");
    }

    // ============================================================
    // Category 9: PiP (-> no direct Android IWindow methods)
    // ============================================================

    /**
     * [PARTIAL] SetPipActionEvent -> (Android PiP via Activity.onPictureInPictureModeChanged)
     *
     * OH PiP actions (close, restore, destroy).
     * Android PiP is managed via Activity callbacks and PictureInPictureParams.
     * Impact: MEDIUM - PiP controls need to work for media apps.
     * Strategy: Forward to Activity's PiP callback mechanism.
     */
    public void onSetPipActionEvent(String action, int status) {
        logPartial("SetPipActionEvent",
                "-> Activity.onPictureInPictureModeChanged");
    }

    /**
     * [PARTIAL] NotifyPipWindowSizeChange -> (Activity PiP resize)
     */
    public void onNotifyPipWindowSizeChange(double width, double height, double scale) {
        logPartial("NotifyPipWindowSizeChange",
                "-> PictureInPictureParams update");
    }

    /**
     * [PARTIAL] NotifyPiPActiveStatusChange
     */
    public void onNotifyPiPActiveStatusChange(boolean status) {
        logPartial("NotifyPiPActiveStatusChange",
                "-> Activity.onPictureInPictureModeChanged");
    }

    /**
     * [PARTIAL] SetPiPControlEvent -> (media session transport controls)
     */
    public void onSetPiPControlEvent(int controlType, int status) {
        logPartial("SetPiPControlEvent",
                "-> MediaSession transport controls");
    }

    /**
     * [PARTIAL] NotifyCloseExistPipWindow -> (PiP window management)
     */
    public void onNotifyCloseExistPipWindow() {
        logPartial("NotifyCloseExistPipWindow",
                "-> close existing PiP via Activity.finish");
    }

    // ============================================================
    // Category 10: Rotation (-> IWindow.resized with new config)
    // ============================================================

    /**
     * [BRIDGED] NotifyRotationChange -> IWindow.resized (rotation in config)
     */
    public void onNotifyRotationChange(int rotation) {
        logBridged("NotifyRotationChange",
                "-> IWindow.resized (config.orientation/rotation)");
    }

    /**
     * [PARTIAL] SetCurrentRotation -> (config change)
     */
    public void onSetCurrentRotation(int rotation) {
        logPartial("SetCurrentRotation",
                "-> Configuration.rotation update");
    }

    /**
     * [OH_ONLY] NotifyTargetRotationInfo
     */
    public void onNotifyTargetRotationInfo() {
        logOhOnly("NotifyTargetRotationInfo",
                "OH rotation prediction, no Android equivalent");
    }

    /**
     * [OH_ONLY] NotifyPageRotationIsIgnored
     */
    public void onNotifyPageRotationIsIgnored() {
        logOhOnly("NotifyPageRotationIsIgnored", "OH specific");
    }

    /**
     * [OH_ONLY] NotifyAppForceLandscapeConfigUpdated
     */
    public void onNotifyAppForceLandscapeConfigUpdated() {
        logOhOnly("NotifyAppForceLandscapeConfigUpdated", "OH specific");
    }

    // ============================================================
    // Category 11: Screenshot (-> no direct IWindow method)
    // ============================================================

    /**
     * [STUB] NotifyScreenshot -> (Activity callback)
     */
    public void onNotifyScreenshot() {
        logStub("NotifyScreenshot",
                "Android uses Activity.onScreenCaptured or FileObserver");
    }

    /**
     * [STUB] NotifyScreenshotAppEvent
     */
    public void onNotifyScreenshotAppEvent(int type) {
        logStub("NotifyScreenshotAppEvent", "Android handles differently");
    }

    // ============================================================
    // Category 12: Extension / Component Data (OH_ONLY)
    // ============================================================

    /**
     * [OH_ONLY] SendExtensionData - OH UIExtension IPC
     */
    public void onSendExtensionData() {
        logOhOnly("SendExtensionData", "OH UIExtension, no Android equivalent");
    }

    /**
     * [OH_ONLY] NotifyTransferComponentData - OH component data transfer
     */
    public void onNotifyTransferComponentData() {
        logOhOnly("NotifyTransferComponentData",
                "OH component data transfer, no Android equivalent");
    }

    /**
     * [OH_ONLY] NotifyExtensionSecureLimitChange
     */
    public void onNotifyExtensionSecureLimitChange(boolean isLimit) {
        logOhOnly("NotifyExtensionSecureLimitChange", "OH extension security");
    }

    // ============================================================
    // Category 13: Keyboard (-> IWindow.showInsets / hideInsets)
    // ============================================================

    /**
     * [BRIDGED] NotifyKeyboardAnimationCompleted -> IWindow.showInsets/hideInsets
     */
    public void onNotifyKeyboardAnimationCompleted() {
        logBridged("NotifyKeyboardAnimationCompleted",
                "-> IWindow.showInsets/hideInsets (IME)");
    }

    /**
     * [BRIDGED] NotifyKeyboardAnimationWillBegin -> IWindow.showInsets
     */
    public void onNotifyKeyboardAnimationWillBegin() {
        logBridged("NotifyKeyboardAnimationWillBegin",
                "-> IWindow.showInsets (IME animation start)");
    }

    // ============================================================
    // Category 14: Dialog / Lifecycle State
    // ============================================================

    /**
     * [PARTIAL] NotifyDialogStateChange -> (Android Dialog lifecycle)
     */
    public void onNotifyDialogStateChange(boolean isForeground) {
        logPartial("NotifyDialogStateChange",
                "-> Dialog.show/dismiss lifecycle");
    }

    /**
     * [PARTIAL] NotifyForegroundInteractiveStatus -> IWindow.dispatchAppVisibility
     */
    public void onNotifyForegroundInteractiveStatus(boolean interactive) {
        logBridged("NotifyForegroundInteractiveStatus",
                "-> IWindow.dispatchAppVisibility(" + interactive + ")");
        dispatchAppVisibility(interactive);
    }

    /**
     * [OH_ONLY] NotifyLifecyclePausedStatus - OH specific lifecycle state
     */
    public void onNotifyLifecyclePausedStatus() {
        logOhOnly("NotifyLifecyclePausedStatus",
                "OH specific, Android uses Activity.onPause");
    }

    // ============================================================
    // Category 15: Title / Decor / UI (OH_ONLY)
    // ============================================================

    /**
     * [OH_ONLY] UpdateTitleInTargetPos - OH window title bar
     */
    public void onUpdateTitleInTargetPos(boolean isShow, int height) {
        logOhOnly("UpdateTitleInTargetPos", "OH window title management");
    }

    /**
     * [OH_ONLY] NotifyTransformChange - OH window transform
     */
    public void onNotifyTransformChange() {
        logOhOnly("NotifyTransformChange", "OH window transform");
    }

    /**
     * [OH_ONLY] NotifySingleHandTransformChange
     */
    public void onNotifySingleHandTransformChange() {
        logOhOnly("NotifySingleHandTransformChange", "OH single-hand mode");
    }

    /**
     * [OH_ONLY] NotifyCompatibleModePropertyChange
     */
    public void onNotifyCompatibleModePropertyChange() {
        logOhOnly("NotifyCompatibleModePropertyChange", "OH compat mode");
    }

    /**
     * [OH_ONLY] SetUniqueVirtualPixelRatio
     */
    public void onSetUniqueVirtualPixelRatio(boolean useUniqueDensity, float ratio) {
        logOhOnly("SetUniqueVirtualPixelRatio", "OH density management");
    }

    /**
     * [OH_ONLY] Various UI state methods (30+)
     *
     * UpdateAnimationSpeed, NotifySessionFullScreen, SetSplitButtonVisible,
     * SetEnableDragBySystem, SetDragActivated, SetFullScreenWaterfallMode,
     * SendContainerModalEvent, UpdateWindowUIType, etc.
     *
     * These are OH-specific UI management features without Android equivalents.
     * Impact: LOW to NONE - Most are visual enhancements or OH-specific features.
     * Strategy: Ignore. Android UI framework handles these differently.
     */

    // ============================================================
    // Category 16: Accessibility (-> Android Accessibility framework)
    // ============================================================

    /**
     * [PARTIAL] NotifyAccessibilityHoverEvent -> AccessibilityManager
     *
     * OH accessibility hover events.
     * Android has its own accessibility event dispatch mechanism.
     * Impact: MEDIUM - Accessibility support is important.
     * Strategy: Forward to Android AccessibilityManager.
     */
    public void onNotifyAccessibilityHoverEvent(float x, float y, int sourceType,
                                                 int eventType) {
        logPartial("NotifyAccessibilityHoverEvent",
                "-> Android AccessibilityManager event dispatch");
    }

    /**
     * [PARTIAL] NotifyAccessibilityChildTreeRegister/Unregister
     */
    public void onNotifyAccessibilityChildTreeRegister(int windowId, int treeId) {
        logPartial("NotifyAccessibilityChildTreeRegister",
                "-> Android AccessibilityNodeProvider");
    }

    // ============================================================
    // Category 17: Debug (-> IWindow.executeCommand)
    // ============================================================

    /**
     * [PARTIAL] DumpSessionElementInfo -> IWindow.executeCommand
     */
    public void onDumpSessionElementInfo() {
        logPartial("DumpSessionElementInfo",
                "-> IWindow.executeCommand (debug dump)");
    }

    /**
     * [PARTIAL] GetRouterStackInfo -> (no Android equivalent)
     */
    public void onGetRouterStackInfo() {
        logPartial("GetRouterStackInfo",
                "OH navigation stack, Android uses FragmentManager");
    }

    /**
     * [PARTIAL] GetTopNavDestinationName -> (no Android equivalent)
     */
    public void onGetTopNavDestinationName() {
        logPartial("GetTopNavDestinationName",
                "OH navigation, Android uses NavController");
    }

    // ============================================================
    // Category 18: Remaining OH_ONLY methods
    // ============================================================

    /**
     * [OH_ONLY] NotifyAppUseControlStatus
     */
    public void onNotifyAppUseControlStatus(boolean isUseControl) {
        logOhOnly("NotifyAppUseControlStatus", "OH app control feature");
    }

    /**
     * [OH_ONLY] NotifyWindowAttachStateChange
     */
    public void onNotifyWindowAttachStateChange(boolean isAttach) {
        logOhOnly("NotifyWindowAttachStateChange", "OH window attach state");
    }

    /**
     * [OH_ONLY] NotifyWindowCrossAxisChange
     */
    public void onNotifyWindowCrossAxisChange() {
        logOhOnly("NotifyWindowCrossAxisChange", "OH cross-axis feature");
    }

    /**
     * [OH_ONLY] PcAppInPadNormalClose
     */
    public void onPcAppInPadNormalClose() {
        logOhOnly("PcAppInPadNormalClose", "OH PC-Pad cross feature");
    }

    /**
     * [OH_ONLY] CloseSpecificScene
     */
    public void onCloseSpecificScene() {
        logOhOnly("CloseSpecificScene", "OH scene management");
    }

    /**
     * [OH_ONLY] UpdateBrightness
     */
    public void onUpdateBrightness(float brightness) {
        logOhOnly("UpdateBrightness", "OH window brightness");
    }

    /**
     * [OH_ONLY] LinkKeyFrameNode / SetStageKeyFramePolicy
     */
    public void onLinkKeyFrameNode() {
        logOhOnly("LinkKeyFrameNode", "OH render engine feature");
    }

    // ==================== Utility ====================

    private void logBridged(String method, String target) {
        Log.d(TAG, "[BRIDGED] " + method + " " + target);
    }

    private void logPartial(String method, String reason) {
        Log.d(TAG, "[PARTIAL] " + method + " - " + reason);
    }

    private void logStub(String method, String reason) {
        Log.d(TAG, "[STUB] " + method + " - " + reason);
    }

    private void logOhOnly(String method, String reason) {
        Log.d(TAG, "[OH_ONLY] " + method + " - " + reason);
    }
}
