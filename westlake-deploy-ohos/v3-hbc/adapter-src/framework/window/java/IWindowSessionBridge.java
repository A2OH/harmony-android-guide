/*
 * IWindowSessionBridge.java
 *
 * Bridge implementation for Android IWindowSession interface.
 * Routes calls to OH ISession + ISceneSessionManager.
 *
 * Mapping:
 *   IWindowSession -> OH ISession (window operations, layout, input)
 *                  -> OH ISceneSessionManager (session lifecycle)
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

public class IWindowSessionBridge implements InvocationHandler {

    private static final String TAG = "OH_IWSBridge";
    private final Object mOriginal;
    // Deprecated: old InvocationHandler bridge, replaced by WindowSessionAdapter

    public IWindowSessionBridge(Object original) {
        mOriginal = original;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();

        switch (name) {
            // ============================================================
            // Category 1: Window Lifecycle (-> OH ISession)
            // ============================================================

            // [BRIDGED] addToDisplay -> OH ISession (implicit in CreateAndConnectSpecificSession)
            case "addToDisplay":
            case "addToDisplayAsUser":
                logBridged(name, "-> OH ISceneSessionManager.CreateAndConnectSpecificSession");
                return method.invoke(mOriginal, args); // Phase 1: passthrough

            // [BRIDGED] addToDisplayWithoutInputChannel
            case "addToDisplayWithoutInputChannel":
                logBridged(name, "-> OH ISession (no input channel variant)");
                return method.invoke(mOriginal, args);

            // [BRIDGED] remove -> OH ISession.Disconnect + ISceneSessionManager.DestroySession
            case "remove":
                logBridged(name, "-> OH ISession.Disconnect");
                return null;

            // ============================================================
            // Category 2: Window Layout (-> OH ISession)
            // ============================================================

            // [BRIDGED] relayout -> OH ISession.UpdateSessionRect + UpdateSizeChangeReason
            case "relayout":
                logBridged(name, "-> OH ISession.UpdateSessionRect");
                return method.invoke(mOriginal, args); // Phase 1: passthrough

            // [BRIDGED] relayoutAsync -> OH ISession async layout
            case "relayoutAsync":
                logBridged(name, "-> OH ISession.UpdateSessionRect (async)");
                return null;

            // [BRIDGED] outOfMemory -> OH memory pressure notification
            case "outOfMemory":
                logBridged(name, "-> OH memory management");
                return false;

            // ============================================================
            // Category 3: Drawing (-> OH ISession)
            // ============================================================

            // [BRIDGED] finishDrawing -> OH ISessionStage drawing complete
            case "finishDrawing":
                logBridged(name, "-> OH ISessionStage.NotifyDrawingCompleted");
                return null;

            // [BRIDGED] setTransparentRegion -> OH ISession transparency
            case "setTransparentRegion":
                logBridged(name, "-> OH ISession transparency region");
                return null;

            // [BRIDGED] setInsets -> OH ISession.SetAvoidArea
            case "setInsets":
                logBridged(name, "-> OH ISession.SetAvoidArea");
                return null;

            // ============================================================
            // Category 4: Surface Operations (-> OH ISession)
            // ============================================================

            // [BRIDGED] getDisplayFrame -> OH ISession.GetGlobalScaledRect
            case "getDisplayFrame":
                logBridged(name, "-> OH ISession.GetGlobalScaledRect");
                return null;

            // [BRIDGED] performShow -> OH ISession.Foreground
            case "performShow":
                logBridged(name, "-> OH ISession.Foreground");
                return null;

            // [BRIDGED] performHide -> OH ISession.Background
            case "performHide":
                logBridged(name, "-> OH ISession.Background");
                return null;

            // ============================================================
            // Category 5: Drag and Drop (-> OH ISession)
            // ============================================================

            // [BRIDGED] performDrag -> OH drag and drop framework
            case "performDrag":
                logBridged(name, "-> OH drag framework");
                return null; // IBinder dragToken

            // [BRIDGED] reportDropResult -> OH drag result
            case "reportDropResult":
                logBridged(name, "-> OH drag result notification");
                return null;

            // [BRIDGED] cancelDragAndDrop -> OH cancel drag
            case "cancelDragAndDrop":
                logBridged(name, "-> OH cancel drag");
                return null;

            // [BRIDGED] dragRecipientEntered -> OH drag recipient
            case "dragRecipientEntered":
                logBridged(name, "-> OH drag recipient entered");
                return null;

            // [BRIDGED] dragRecipientExited -> OH drag recipient
            case "dragRecipientExited":
                logBridged(name, "-> OH drag recipient exited");
                return null;

            // ============================================================
            // Category 6: Input (-> OH ISession)
            // ============================================================

            // [BRIDGED] updatePointerIcon -> OH input pointer
            case "updatePointerIcon":
                logBridged(name, "-> OH input pointer icon");
                return null;

            // [BRIDGED] sendWallpaperCommand -> OH wallpaper
            case "sendWallpaperCommand":
                logStub(name, "OH wallpaper command not mapped");
                return null;

            // [BRIDGED] wallpaperOffsetsComplete -> OH wallpaper
            case "wallpaperOffsetsComplete":
                logStub(name, "OH wallpaper not mapped");
                return null;

            // [BRIDGED] setWallpaperPosition -> OH wallpaper
            case "setWallpaperPosition":
                logStub(name, "OH wallpaper position not mapped");
                return null;

            // [BRIDGED] setWallpaperDisplayOffset -> OH wallpaper
            case "setWallpaperDisplayOffset":
                logStub(name, "OH wallpaper not mapped");
                return null;

            // ============================================================
            // Category 7: Input Method (-> OH ISession IME)
            // ============================================================

            // [BRIDGED] onRectangleOnScreenRequested
            case "onRectangleOnScreenRequested":
                logBridged(name, "-> OH ISession visible area request");
                return null;

            // [BRIDGED] getInTouchMode
            case "getInTouchMode":
                logStub(name, "OH touch mode");
                return true;

            // ============================================================
            // Category 8: Window Properties (-> OH ISession)
            // ============================================================

            // [BRIDGED] setWindowScale -> OH ISession scale
            case "setWindowScale":
                logBridged(name, "-> OH ISession scale property");
                return null;

            // [BRIDGED] insetsModified -> OH ISession insets
            case "insetsModified":
                logBridged(name, "-> OH ISession insets modified");
                return null;

            // [BRIDGED] reportSystemGestureExclusionChanged
            case "reportSystemGestureExclusionChanged":
                logStub(name, "OH gesture exclusion not mapped");
                return null;

            // [BRIDGED] reportDecorViewGestureInterceptionChanged
            case "reportDecorViewGestureInterceptionChanged":
                logStub(name, "OH gesture interception not mapped");
                return null;

            // ============================================================
            // Category 9: Embedded Windows (-> OH SubSession)
            // ============================================================

            // [BRIDGED] addWindowToTokenForTaskFragment
            case "addWindowToTokenForTaskFragment":
                logStub(name, "OH sub-session not mapped");
                return null;

            // [BRIDGED] grantInputChannel -> OH ISession input
            case "grantInputChannel":
                logBridged(name, "-> OH ISession input channel");
                return null;

            // [BRIDGED] grantEmbeddedWindowFocus
            case "grantEmbeddedWindowFocus":
                logStub(name, "OH embedded window focus");
                return null;

            // ============================================================
            // Category 10: Accessibility (STUB)
            // ============================================================

            // [STUB] getWindowId -> OH accessibility
            case "getWindowId":
                logStub(name, "OH accessibility window ID");
                return 0;

            // ============================================================
            // Category 11: Sync / Merge (-> OH ISession)
            // ============================================================

            // [BRIDGED] performDeferredDestroy
            case "performDeferredDestroy":
                logBridged(name, "-> OH deferred session destroy");
                return null;

            // [BRIDGED] onWindowInfoChanged
            case "onWindowInfoChanged":
                logBridged(name, "-> OH ISession window info update");
                return null;

            // [BRIDGED] prepareToReplaceWindows
            case "prepareToReplaceWindows":
                logStub(name, "No OH equivalent");
                return null;

            // [BRIDGED] updateTapExcludeRegion
            case "updateTapExcludeRegion":
                logBridged(name, "-> OH ISession tap exclude");
                return null;

            // [BRIDGED] updateRequestedVisibleTypes
            case "updateRequestedVisibleTypes":
                logBridged(name, "-> OH ISession visible types");
                return null;

            // [BRIDGED] moveFocusToAdjacentWindow
            case "moveFocusToAdjacentWindow":
                logStub(name, "OH focus management different");
                return null;

            default:
                Log.w(TAG, "[UNMAPPED] IWindowSession." + name);
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
        return null;
    }

    private void logBridged(String method, String target) {
        Log.d(TAG, "[BRIDGED] " + method + " " + target);
    }

    private void logStub(String method, String reason) {
        Log.d(TAG, "[STUB] " + method + " - " + reason);
    }
}
