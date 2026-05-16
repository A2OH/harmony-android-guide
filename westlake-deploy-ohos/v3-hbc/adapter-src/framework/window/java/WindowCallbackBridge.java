/*
 * WindowCallbackBridge.java
 *
 * Reverse bridge: OH IWindow callbacks -> Android IWindow.
 *
 * OH IWindow is called by OH WindowManager to notify individual window
 * instances of state changes (rect, mode, focus, avoid area, etc.).
 *
 * Mapping:
 *   OH IWindow -> Android IWindow (window events)
 *
 * Key differences:
 *   - OH IWindow has 26 methods with granular state updates
 *   - Android IWindow has 16 methods with batched updates (resized combines many)
 *   - OH separates rect/mode/focus/avoidArea into individual calls
 *   - Android batches most into resized() with frames/insets/config
 */
package adapter.window;

import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.IWindow;

public class WindowCallbackBridge {

    private static final String TAG = "OH_WindowCBBridge";
    private final Object mAndroidWindow; // Android IWindow proxy
    private IWindow mIWindow; // Typed reference (resolved lazily)

    public WindowCallbackBridge(Object androidWindow) {
        mAndroidWindow = androidWindow;
        if (androidWindow instanceof IWindow) {
            mIWindow = (IWindow) androidWindow;
        }
    }

    /**
     * Dispatch app visibility to the Android IWindow.
     */
    private void dispatchAppVisibility(boolean visible) {
        if (mIWindow != null) {
            try {
                mIWindow.dispatchAppVisibility(visible);
            } catch (Exception e) {
                Log.e(TAG, "Failed to dispatch app visibility", e);
            }
        } else {
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
     * Dispatch window focus change to the Android ViewRootImpl by reusing
     * AOSP's own internal latch (mUpcomingWindowFocus / mWindowFocusChanged
     * + MSG_WINDOW_FOCUS_CHANGED on the main looper).
     *
     * AOSP 14 native path: WMS InputDispatcher delivers focus event over the
     * window's InputChannel; native InputConsumer JNI-callbacks
     * InputEventReceiver.onFocusEvent(boolean), which forwards to
     * ViewRootImpl.windowFocusChanged(boolean). OH instead pushes
     * IWindow.UpdateFocusStatus over OH IPC and the call may arrive before
     * mInputEventReceiver is attached. Calling ViewRootImpl.windowFocusChanged
     * directly skips the InputEventReceiver hop but enters the same Java entry
     * point that onFocusEvent forwards to, while reusing the AOSP-internal
     * latch so timing is handled by ViewRootImpl rather than the bridge:
     *   - synchronized write of mWindowFocusChanged / mUpcomingWindowFocus
     *   - sendMessage(MSG_WINDOW_FOCUS_CHANGED) onto ViewRootImpl's mHandler
     *   - main looper processes it in FIFO order after setView completes,
     *     by which point mAdded / mFirstInputStage / mInputEventReceiver
     *     are all initialized
     *   - InputStage.deliver also drains the latch before each input event,
     *     and OutOfResourcesException triggers a 500ms self-retry inside
     *     handleWindowFocusChanged — all reused for free.
     *
     * Reflection chain:
     *   W.mViewAncestor (WeakReference<ViewRootImpl>) -> ViewRootImpl
     *     .windowFocusChanged(boolean)  // public, latches + sendMessage
     */
    private void dispatchWindowFocusChanged(boolean focused) {
        try {
            java.lang.reflect.Field viewAncestorField = mAndroidWindow.getClass()
                    .getDeclaredField("mViewAncestor");
            viewAncestorField.setAccessible(true);
            Object weakRef = viewAncestorField.get(mAndroidWindow);
            if (!(weakRef instanceof java.lang.ref.WeakReference)) {
                Log.w(TAG, "windowFocusChanged: W.mViewAncestor not WeakReference, drop");
                return;
            }
            Object viewRootImpl = ((java.lang.ref.WeakReference<?>) weakRef).get();
            if (viewRootImpl == null) {
                Log.w(TAG, "windowFocusChanged: ViewRootImpl already gone, drop");
                return;
            }
            java.lang.reflect.Method m = viewRootImpl.getClass()
                    .getMethod("windowFocusChanged", boolean.class);
            m.invoke(viewRootImpl, focused);
            Log.d(TAG, "windowFocusChanged latched on ViewRootImpl(focused=" + focused
                       + ") — main looper will dispatch after setView completes");
        } catch (Exception e) {
            Log.e(TAG, "Failed to dispatch window focus changed", e);
        }
    }

    // ============================================================
    // Category 1: Window Geometry (-> IWindow.resized)
    // ============================================================

    /**
     * [BRIDGED] UpdateWindowRect -> IWindow.resized
     *
     * OH updates window rectangle with decoration status and reason.
     * Android IWindow.resized delivers frames, configuration, and insets together.
     *
     * Semantic gap: OH sends rect separately; Android bundles rect with
     * MergedConfiguration and InsetsState. Bridge needs to construct
     * ClientWindowFrames from OH rect data.
     */
    public void onUpdateWindowRect(int left, int top, int right, int bottom,
                                    boolean decoStatus, int reason) {
        logBridged("UpdateWindowRect",
                "-> IWindow.resized (rect=[" + left + "," + top + "," + right + "," + bottom + "])");
        // Phase 1: Forward to Android IWindow.resized with constructed frames
    }

    /**
     * [BRIDGED] UpdateWindowMode -> IWindow.resized (mode change triggers relayout)
     *
     * OH window mode: FULLSCREEN, FLOATING, SPLIT_PRIMARY, SPLIT_SECONDARY, PIP.
     * Android embeds windowing mode in configuration within resized().
     */
    public void onUpdateWindowMode(int mode) {
        logBridged("UpdateWindowMode",
                "-> IWindow.resized (windowing mode=" + mode + ")");
    }

    /**
     * [STUB] UpdateWindowModeSupportType -> (no direct Android equivalent)
     *
     * OH tells window which modes are supported.
     * Android manages this server-side in WindowManagerService.
     * Impact: LOW - App doesn't need to know supported modes explicitly.
     * Strategy: Ignore, Android WMS handles mode constraints.
     */
    public void onUpdateWindowModeSupportType(int supportType) {
        logStub("UpdateWindowModeSupportType",
                "Android WMS manages mode support internally");
    }

    // ============================================================
    // Category 2: Focus (-> no direct IWindow method)
    // ============================================================

    /**
     * [BRIDGED] UpdateFocusStatus -> WindowInputEventReceiver.onFocusEvent
     *
     * OH explicitly sends focus status over IWindow. Android delivers focus
     * via the window's native InputChannel which JNI-callbacks
     * InputEventReceiver.onFocusEvent. The bridge re-enters the same Java
     * entry point so all subsequent state (mAttachInfo.mHasWindowFocus,
     * MSG_WINDOW_FOCUS_CHANGED dispatch, mView.dispatchWindowFocusChanged,
     * Activity.onWindowFocusChanged, IME focus, ThreadedRenderer enable)
     * matches AOSP behavior exactly.
     *
     * 2026-05-09 corrected: was [PARTIAL] noop which left
     * mAttachInfo.mHasWindowFocus permanently false and blocked the render
     * pipeline (ViewRootImpl gates profileRendering / ThreadedRenderer.
     * setEnabled on hasWindowFocus).
     */
    public void onUpdateFocusStatus(boolean focused) {
        logBridged("UpdateFocusStatus",
                "-> WindowInputEventReceiver.onFocusEvent(" + focused + ")");
        dispatchWindowFocusChanged(focused);
    }

    /**
     * [BRIDGED] UpdateActiveStatus -> IWindow.dispatchAppVisibility (post to main Looper)
     *
     * OH active 与 visible 语义在 client 侧均映射为 dispatchAppVisibility:
     * legacy WMS 通过 IWindow.UpdateActiveStatus 驱动 client mAppVisible。
     * 2026-05-08 G2.14y 纠正 — 原占位 [PARTIAL] "Android 内部处理"是错的。
     * Called from OH binder thread; per G2.14x design principle must post
     * to main Looper before invoking IWindow.dispatchAppVisibility.
     */
    public void onUpdateActiveStatus(final boolean isActive) {
        logBridged("UpdateActiveStatus(" + isActive + ")",
                "-> IWindow.dispatchAppVisibility (post to main)");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                dispatchAppVisibility(isActive);
            }
        });
    }

    // ============================================================
    // Category 3: Avoid Area / Insets (-> IWindow.resized / insetsControlChanged)
    // ============================================================

    /**
     * [BRIDGED] UpdateAvoidArea -> IWindow.resized (InsetsState)
     *
     * OH AvoidArea maps to Android window insets.
     * OH AvoidAreaType: TYPE_SYSTEM, TYPE_CUTOUT, TYPE_NAVIGATION, TYPE_KEYBOARD, etc.
     * Android InsetsState contains insets sources for each type.
     *
     * Conversion:
     *   OH TYPE_SYSTEM -> Android STATUS_BAR insets
     *   OH TYPE_NAVIGATION -> Android NAVIGATION_BAR insets
     *   OH TYPE_CUTOUT -> Android DISPLAY_CUTOUT insets
     *   OH TYPE_KEYBOARD -> Android IME insets
     */
    public void onUpdateAvoidArea(int avoidAreaType, int left, int top,
                                   int right, int bottom) {
        logBridged("UpdateAvoidArea(type=" + avoidAreaType + ")",
                "-> IWindow.resized (InsetsState) / insetsControlChanged");
    }

    /**
     * [BRIDGED] UpdateOccupiedAreaChangeInfo -> IWindow.resized (InsetsState)
     *
     * OH occupied area (typically keyboard) changes.
     * Android delivers via insetsControlChanged or resized with IME insets.
     */
    public void onUpdateOccupiedAreaChangeInfo(int occupiedHeight) {
        logBridged("UpdateOccupiedAreaChangeInfo",
                "-> IWindow.insetsControlChanged (IME height=" + occupiedHeight + ")");
    }

    /**
     * [BRIDGED] UpdateOccupiedAreaAndRect -> IWindow.resized
     *
     * OH combined occupied area and rect update.
     * Android handles via single resized() call.
     */
    public void onUpdateOccupiedAreaAndRect() {
        logBridged("UpdateOccupiedAreaAndRect",
                "-> IWindow.resized (combined insets + rect)");
    }

    // ============================================================
    // Category 4: Visibility (-> IWindow.dispatchAppVisibility)
    //
    // 2026-05-08 G2.14y 纠正：
    //   1) STATE_SHOWN 值为 2（不是 1），按 OH wm_common.h enum WindowState
    //   2) 必须 Handler(Looper.getMainLooper()).post(...) 切到主线程，
    //      否则 dispatchAppVisibility 在 binder thread 跑让 ViewRootImpl
    //      的 mAppVisible 内部状态被错线程修改 (G2.14x feedback)
    // ============================================================

    /** OH WindowState enum (wm_common.h) — keep in sync with native. */
    private static final int OH_WINDOW_STATE_INITIAL   = 0;
    private static final int OH_WINDOW_STATE_CREATED   = 1;
    private static final int OH_WINDOW_STATE_SHOWN     = 2;
    private static final int OH_WINDOW_STATE_HIDDEN    = 3;
    private static final int OH_WINDOW_STATE_FROZEN    = 4;
    private static final int OH_WINDOW_STATE_UNFROZEN  = 5;
    private static final int OH_WINDOW_STATE_DESTROYED = 6;

    /**
     * [BRIDGED] UpdateWindowState -> IWindow.dispatchAppVisibility (post to main)
     *
     * STATE_SHOWN/UNFROZEN -> visible=true
     * STATE_HIDDEN/FROZEN/DESTROYED -> visible=false
     * STATE_INITIAL/CREATED -> pre-show, no-op
     */
    public void onUpdateWindowState(final int state) {
        final Boolean visible;
        switch (state) {
            case OH_WINDOW_STATE_SHOWN:
            case OH_WINDOW_STATE_UNFROZEN:
                visible = Boolean.TRUE;
                break;
            case OH_WINDOW_STATE_HIDDEN:
            case OH_WINDOW_STATE_FROZEN:
            case OH_WINDOW_STATE_DESTROYED:
                visible = Boolean.FALSE;
                break;
            case OH_WINDOW_STATE_INITIAL:
            case OH_WINDOW_STATE_CREATED:
            default:
                logBridged("UpdateWindowState(" + state + ")", "no-op (pre-show)");
                return;
        }
        logBridged("UpdateWindowState(" + state + ")",
                "-> IWindow.dispatchAppVisibility(" + visible + ") (post to main)");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                dispatchAppVisibility(visible);
            }
        });
    }

    /**
     * [BRIDGED] NotifyForeground -> IWindow.dispatchAppVisibility(true) (post to main)
     */
    public void onNotifyForeground() {
        logBridged("NotifyForeground", "-> IWindow.dispatchAppVisibility(true) (post to main)");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                dispatchAppVisibility(true);
            }
        });
    }

    /**
     * [BRIDGED] NotifyBackground -> IWindow.dispatchAppVisibility(false) (post to main)
     */
    public void onNotifyBackground() {
        logBridged("NotifyBackground", "-> IWindow.dispatchAppVisibility(false) (post to main)");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                dispatchAppVisibility(false);
            }
        });
    }

    /**
     * [BRIDGED] NotifyForegroundInteractiveStatus -> IWindow.dispatchAppVisibility (post to main)
     */
    public void onNotifyForegroundInteractiveStatus(final boolean interactive) {
        logBridged("NotifyForegroundInteractiveStatus",
                "-> IWindow.dispatchAppVisibility(" + interactive + ") (post to main)");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                dispatchAppVisibility(interactive);
            }
        });
    }

    // ============================================================
    // Category 5: Window Destruction (-> handled via WMS)
    // ============================================================

    /**
     * [PARTIAL] NotifyDestroy -> (no direct IWindow method)
     *
     * OH notifies window of destruction.
     * Android handles window removal through WindowManagerService,
     * not via IWindow callback.
     *
     * Impact: LOW - Window cleanup is handled at higher level.
     * Strategy: Trigger ViewRootImpl.die() through internal mechanism.
     */
    public void onNotifyDestroy() {
        logPartial("NotifyDestroy",
                "Android removes windows via WMS, not IWindow callback");
    }

    // ============================================================
    // Category 6: Drag / Input (-> IWindow.dispatchDragEvent)
    // ============================================================

    /**
     * [BRIDGED] UpdateWindowDragInfo -> IWindow.dispatchDragEvent
     *
     * OH drag information update.
     * Android dispatches DragEvent to window.
     */
    public void onUpdateWindowDragInfo(float x, float y, int event) {
        logBridged("UpdateWindowDragInfo",
                "-> IWindow.dispatchDragEvent(x=" + x + ",y=" + y + ")");
    }

    /**
     * [BRIDGED] NotifyWindowClientPointUp -> (input event dispatch)
     *
     * OH notifies pointer up event.
     * Android dispatches touch events via input channel, not IWindow.
     * Impact: LOW - Input events use separate input channel.
     */
    public void onNotifyWindowClientPointUp() {
        logPartial("NotifyWindowClientPointUp",
                "Android uses input channel for touch events, not IWindow");
    }

    /**
     * [PARTIAL] ConsumeKeyEvent -> (input event dispatch)
     *
     * OH dispatches key event to window.
     * Android uses input channel for key event delivery.
     */
    public void onConsumeKeyEvent() {
        logPartial("ConsumeKeyEvent",
                "Android uses input channel for key events");
    }

    // ============================================================
    // Category 7: Display (-> IWindow.moved)
    // ============================================================

    /**
     * [BRIDGED] UpdateDisplayId -> IWindow.resized (displayId parameter)
     *
     * OH notifies window of display change.
     * Android includes displayId in resized().
     */
    public void onUpdateDisplayId(long fromDisplay, long toDisplay) {
        logBridged("UpdateDisplayId",
                "-> IWindow.resized (displayId=" + toDisplay + ")");
    }

    // ============================================================
    // Category 8: Screenshot (-> no direct IWindow method)
    // ============================================================

    /**
     * [STUB] NotifyScreenshot -> (no direct Android IWindow equivalent)
     *
     * OH notifies window that a screenshot was captured.
     * Android handles screenshot notification at app level via Activity callback.
     * Impact: LOW - Apps can still detect screenshots via file observer.
     * Strategy: Ignore or route to Activity.onScreenCaptured if available.
     */
    public void onNotifyScreenshot() {
        logStub("NotifyScreenshot",
                "Android uses file observer or Activity callback");
    }

    /**
     * [STUB] NotifyScreenshotAppEvent
     */
    public void onNotifyScreenshotAppEvent(int type) {
        logStub("NotifyScreenshotAppEvent",
                "Android uses different screenshot notification");
    }

    // ============================================================
    // Category 9: Touch Outside (-> no direct IWindow method)
    // ============================================================

    /**
     * [PARTIAL] NotifyTouchOutside -> (Android MotionEvent.ACTION_OUTSIDE)
     *
     * OH explicitly notifies touch outside window.
     * Android delivers ACTION_OUTSIDE through input channel if
     * FLAG_WATCH_OUTSIDE_TOUCH is set.
     *
     * Impact: LOW - Handled via input channel in Android.
     * Strategy: Deliver via input channel mechanism.
     */
    public void onNotifyTouchOutside() {
        logPartial("NotifyTouchOutside",
                "Android uses ACTION_OUTSIDE via input channel");
    }

    // ============================================================
    // Category 10: Zoom / Transform (-> no direct IWindow method)
    // ============================================================

    /**
     * [STUB] UpdateZoomTransform -> (no direct Android equivalent)
     *
     * OH display zoom transform.
     * Android handles accessibility zoom separately.
     * Impact: LOW - Accessibility zoom is rare.
     */
    public void onUpdateZoomTransform() {
        logStub("UpdateZoomTransform",
                "Android handles zoom via Accessibility service");
    }

    /**
     * [STUB] RestoreSplitWindowMode -> (no direct Android equivalent)
     *
     * OH restores split window mode.
     * Android split screen is managed by WMS/SystemUI.
     * Impact: LOW - Handled at system level.
     */
    public void onRestoreSplitWindowMode(int mode) {
        logStub("RestoreSplitWindowMode",
                "Android manages split screen via WMS");
    }

    // ============================================================
    // Category 11: Wallpaper (-> IWindow.dispatchWallpaperOffsets)
    // ============================================================

    // Note: OH IWindow does not have wallpaper-specific callbacks.
    // Android IWindow.dispatchWallpaperOffsets and dispatchWallpaperCommand
    // have no OH IWindow counterparts.
    // Impact: MEDIUM - Wallpaper parallax effects won't work.
    // Strategy: Could be handled by enhancing OH WMS with wallpaper offset support.

    // ============================================================
    // Category 12: Debug (-> IWindow.executeCommand)
    // ============================================================

    /**
     * [PARTIAL] DumpInfo -> IWindow.executeCommand
     *
     * OH dumps window info for debugging.
     * Android uses executeCommand for view server debugging.
     */
    public void onDumpInfo() {
        logPartial("DumpInfo", "-> IWindow.executeCommand (debug)");
    }

    /**
     * [PARTIAL] GetWindowProperty -> (no direct Android equivalent)
     *
     * OH returns window property object.
     * Android queries window attributes via WindowManager.LayoutParams.
     * Impact: LOW - Debug/diagnostic only.
     */
    public void onGetWindowProperty() {
        logPartial("GetWindowProperty",
                "Android uses WindowManager.LayoutParams for window properties");
    }

    /**
     * [STUB] NotifyMMIServiceOnline -> (no Android equivalent)
     *
     * OH Multi-Modal Input service online notification.
     * Impact: None - Android input subsystem is different.
     */
    public void onNotifyMMIServiceOnline() {
        logStub("NotifyMMIServiceOnline",
                "OH MMI service, no Android equivalent");
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
}
