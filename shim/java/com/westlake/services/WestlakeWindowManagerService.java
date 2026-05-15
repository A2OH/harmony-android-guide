// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4b -- WestlakeWindowManagerService
//
// Minimum-surface implementation of android.view.IWindowManager.Stub for
// the Westlake dalvikvm sandbox.  Implements the Tier-1 transactions
// PhoneWindow.<init> + Activity.attach exercise (per
// BINDER_PIVOT_MILESTONES.md M4b and M4_DISCOVERY.md sec 13) and provides
// fail-loud overrides via ServiceMethodMissing.fail for the remaining
// ~143 abstract methods so the JVM can instantiate this class.
//
// Same-process Stub.asInterface elision:
//   When framework code does
//     IWindowManager wm = IWindowManager.Stub.asInterface(
//         ServiceManager.getService("window"));
//   the Stub looks up queryLocalInterface("android.view.IWindowManager")
//   on the IBinder, which returns THIS instance (because Stub() called
//   attachInterface(this, DESCRIPTOR)).  asInterface then returns the
//   raw object cast to IWindowManager -- no Parcel marshaling, no
//   onTransact dispatch, no kernel hop.  Methods are direct Java vtable
//   calls on this class.
//
// Compile-time vs runtime hierarchy:
//   Compile-time: extends shim's android.view.IWindowManager$Stub
//                 (abstract; ~11 declared abstract methods listed in
//                  shim/java/android/view/IWindowManager.java).
//   Runtime:      extends framework.jar's
//                 android.view.IWindowManager$Stub (abstract; 154
//                 abstract methods inherited from IWindowManager).
//   The runtime parent has many more abstract methods than the
//   compile-time parent.  To satisfy the JVM at `new
//   WestlakeWindowManagerService()` time, this class implements
//   every method of the Android 16 framework.jar IWindowManager.aidl
//   surface.  The Tier-1 set (11 methods) has real behaviour at the top
//   of the class; the rest fail loud via ServiceMethodMissing.fail.
//
// Constructor:
//   IWindowManager.Stub's deprecated no-arg constructor calls
//     PermissionEnforcer.fromContext(ActivityThread.currentActivityThread()
//                                         .getSystemContext())
//   which NPEs in the Westlake sandbox (ActivityThread is null) -- same
//   shape as IActivityManager.Stub.  We bypass it by calling the alternate
//   Stub(PermissionEnforcer) constructor with a subclassed PermissionEnforcer
//   whose protected no-arg ctor sets mContext=null and returns.  No system
//   services are touched.
//
// Rationale (see docs/engine/M4_DISCOVERY.md sec 13.9):
//   PhoneWindow.<init>(Context) and Activity.attach(...) eventually call
//   `mWindow.setWindowManager(WindowManagerImpl)`; the impl is a thin
//   wrapper that looks up "window" service.  Without an IWindowManager
//   registered, ServiceManager.getService("window") returns null and the
//   first IWindowManager call from PhoneWindow NPEs.  M4b's role is to
//   register a minimal IWindowManager that:
//     - reports rotation = 0 (portrait, no orientation lock)
//     - reports a 1080x2280 logical display size (OnePlus 6 baseline)
//     - reports density = 480 (typical phone xxhdpi)
//     - reports animation scales = 1.0 (normal animation speed)
//     - tracks window-token add/remove for sanity
//     - returns null from openSession (PhoneWindow's defensive code
//       handles a null Session; see M4b brief Step 2)
//     - lets every other transaction fail loud so discovery surfaces
//       new Tier-1 candidates as obvious stack traces.
//
// Method count: 154 IWindowManager methods.  11 Tier-1 real impls,
//   ~143 fail-loud unobserved-method overrides.
//
// No per-app branches: same shim works for noice, mock apps, future
// real APKs.  Window-token map is a synchronized HashMap so
// multi-threaded callers don't trip CME.

package com.westlake.services;

import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PermissionEnforcer;
import android.os.RemoteException;
import android.view.IRotationWatcher;
import android.view.IWindowManager;
import android.view.IWindowSession;
import android.view.IWindowSessionCallback;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Minimum-surface IWindowManager implementation for the Westlake sandbox.
 * Eleven methods have real (Tier-1) behavior; the rest fail loud.
 */
public final class WestlakeWindowManagerService extends IWindowManager.Stub {

    // ------------------------------------------------------------------
    // Sandbox display constants.
    //
    // Sized for the OnePlus 6 baseline used elsewhere in the Westlake
    // bringup; these dimensions match the phone's logical display rather
    // than the AOSP defaults (320 dpi / 1280x800) so PhoneWindow's metric
    // queries see something realistic.
    // ------------------------------------------------------------------

    /** Default logical display width in pixels (OnePlus 6 baseline). */
    private static final int DISPLAY_WIDTH = 1080;

    /** Default logical display height in pixels. */
    private static final int DISPLAY_HEIGHT = 2280;

    /** Default rotation = ROTATION_0 (portrait, unrotated). */
    private static final int DEFAULT_ROTATION = 0;

    /** Default per-channel animation scale (1.0 = no scaling). */
    private static final float DEFAULT_ANIMATION_SCALE = 1.0f;

    // ------------------------------------------------------------------
    // Mutable state.
    // ------------------------------------------------------------------

    /** Active window tokens; key=IBinder, value=(type<<32)|displayId.
     *  We never inspect the value beyond unregister symmetry -- the
     *  point is just to remember which binders were added so remove
     *  is idempotent. */
    private final Map<IBinder, Long> mWindowTokens =
            Collections.synchronizedMap(new HashMap<>());

    /** Event-dispatching state.  Apps typically don't read this back;
     *  we just remember the last set value. */
    private volatile boolean mEventDispatching = true;

    // PermissionEnforcer subclass nested here so users don't need to
    // import it; protected constructor of PermissionEnforcer is
    // accessible to subclasses regardless of package.
    private static final class NoopPermissionEnforcer extends PermissionEnforcer {
        NoopPermissionEnforcer() { super(); }
    }

    public WestlakeWindowManagerService() {
        // Bypass the deprecated no-arg constructor that NPEs in the
        // sandbox (ActivityThread.getSystemContext() returns null); use
        // the Stub(PermissionEnforcer) overload with a no-op enforcer.
        // Base Stub still calls attachInterface(this, DESCRIPTOR), so
        // queryLocalInterface("android.view.IWindowManager") returns this.
        super(new NoopPermissionEnforcer());
    }

    // ------------------------------------------------------------------
    //   IMPLEMENTED (Tier-1) METHODS
    // ------------------------------------------------------------------

    /** Open a window Session.  Brief Step 2 path (a): return null and
     *  rely on PhoneWindow's defensive null-handling.  If discovery
     *  proves PhoneWindow NPEs on null Session, we'll add a sibling
     *  WestlakeWindowSession class (path b). */
    @Override
    public IWindowSession openSession(IWindowSessionCallback callback) throws RemoteException {
        // Notify the caller of our (sandbox-default) animator scale; this
        // mirrors what real WindowManagerService does after session open.
        if (callback != null) {
            try {
                callback.onAnimatorScaleChanged(DEFAULT_ANIMATION_SCALE);
            } catch (Throwable ignored) {
                // Defensive: callback failure shouldn't prevent session open.
            }
        }
        return null; // PhoneWindow's defensive null-handling kicks in.
    }

    /** Populate Point with our sandbox display size. */
    @Override
    public void getInitialDisplaySize(int displayId, Point outSize) throws RemoteException {
        if (outSize != null) {
            outSize.x = DISPLAY_WIDTH;
            outSize.y = DISPLAY_HEIGHT;
        }
    }

    /** Same as getInitialDisplaySize -- the sandbox has no
     *  override/forced-size concept. */
    @Override
    public void getBaseDisplaySize(int displayId, Point outSize) throws RemoteException {
        if (outSize != null) {
            outSize.x = DISPLAY_WIDTH;
            outSize.y = DISPLAY_HEIGHT;
        }
    }

    /** Animation scale lookup (currently active value); always 1.0 in sandbox. */
    @Override
    public float getCurrentAnimatorScale() throws RemoteException {
        return DEFAULT_ANIMATION_SCALE;
    }

    /** Per-channel animation scale.  `which` is one of
     *  Settings.Global.{WINDOW,TRANSITION,ANIMATOR}_ANIMATION_SCALE
     *  (0/1/2).  We return the same value regardless. */
    @Override
    public float getAnimationScale(int which) throws RemoteException {
        return DEFAULT_ANIMATION_SCALE;
    }

    /** Return all three animation scales at once. */
    @Override
    public float[] getAnimationScales() throws RemoteException {
        return new float[]{
                DEFAULT_ANIMATION_SCALE,
                DEFAULT_ANIMATION_SCALE,
                DEFAULT_ANIMATION_SCALE
        };
    }

    /** Register a rotation watcher and return the current rotation.
     *  We never call watcher.onRotationChanged() -- the sandbox display
     *  never rotates. */
    @Override
    public int watchRotation(IRotationWatcher watcher, int displayId) throws RemoteException {
        return DEFAULT_ROTATION;
    }

    /** Current rotation of the default display; always ROTATION_0 in sandbox. */
    @Override
    public int getDefaultDisplayRotation() throws RemoteException {
        return DEFAULT_ROTATION;
    }

    /** Register a window token.  We track it in a map so removeWindowToken
     *  is symmetric.  The Bundle argument is the Android 16 addition (Bundle
     *  options for window-level config); we ignore it. */
    @Override
    public void addWindowToken(IBinder token, int type, int displayId, Bundle options)
            throws RemoteException {
        if (token == null) return;
        // Pack (type, displayId) into a long for the map value.  We don't
        // currently inspect the value, but it's useful for diagnostics.
        long packed = ((long) type << 32) | (displayId & 0xFFFFFFFFL);
        mWindowTokens.put(token, packed);
    }

    /** Remove a previously-registered window token. */
    @Override
    public void removeWindowToken(IBinder token, int displayId) throws RemoteException {
        if (token == null) return;
        mWindowTokens.remove(token);
    }

    /** Enable/disable event dispatching.  No-op in sandbox; we remember
     *  the last set value for diagnostics. */
    @Override
    public void setEventDispatching(boolean enabled) throws RemoteException {
        mEventDispatching = enabled;
    }

    // ------------------------------------------------------------------
    //   FAIL-LOUD UNOBSERVED METHODS (CR2)
    //
    //   Every remaining abstract IWindowManager method has a concrete
    //   override here that throws ServiceMethodMissing.fail("window", ...).
    //   The Tier-1 set above retains real behavior.
    //
    //   How to promote a method to Tier-1: delete the throw body here and
    //   add a real implementation in the IMPLEMENTED block above.  Update
    //   WindowServiceTest to exercise the new path.
    // ------------------------------------------------------------------

    // ------------------------------------------------------------------
    //   CR32 PROMOTIONS -- AOSP-default-body methods (see docs/engine/CR32_AUDIT_LOG.md)
    //   These were fail-loud pre-CR32 but AOSP's WindowManagerService.java
    //   returns a constant / no-op for the sandbox-equivalent semantics.
    //   Promotion unblocks the PhoneWindow ctor chain that
    //   ViewConfiguration.<init> -> View.<init> -> ... -> PhoneWindow.<init>
    //   exercises in cold-start of every Activity.
    // ------------------------------------------------------------------

    // -- nav bar / rotation / keyguard / safe mode (queried by ViewConfiguration,
    //    PhoneWindow, AppCompat init) --
    public boolean hasNavigationBar(int p0) throws android.os.RemoteException { return false; }
    public int getNavBarPosition(int p0) throws android.os.RemoteException { return 0; /* NAV_BAR_LEFT (treated as "no nav bar present") */ }
    public boolean isKeyguardLocked() throws android.os.RemoteException { return false; }
    public boolean isKeyguardSecure(int p0) throws android.os.RemoteException { return false; }
    public boolean isRotationFrozen() throws android.os.RemoteException { return false; }
    public boolean isDisplayRotationFrozen(int p0) throws android.os.RemoteException { return false; }
    public boolean isSafeModeEnabled() throws android.os.RemoteException { return false; }
    public int getDisplayUserRotation(int p0) throws android.os.RemoteException { return 0; /* Surface.ROTATION_0 */ }

    // -- tracing / dev flags (sandbox has none of these enabled) --
    public boolean isLayerTracing() throws android.os.RemoteException { return false; }
    public boolean isWindowTraceEnabled() throws android.os.RemoteException { return false; }
    public boolean isTransitionTraceEnabled() throws android.os.RemoteException { return false; }
    public boolean isViewServerRunning() throws android.os.RemoteException { return false; }
    public void setLayerTracing(boolean p0) throws android.os.RemoteException { /* no-op */ }
    public void setLayerTracingFlags(int p0) throws android.os.RemoteException { /* no-op */ }
    public void setActiveTransactionTracing(boolean p0) throws android.os.RemoteException { /* no-op */ }
    public void startWindowTrace() throws android.os.RemoteException { /* no-op */ }
    public void stopWindowTrace() throws android.os.RemoteException { /* no-op */ }
    public void startTransitionTrace() throws android.os.RemoteException { /* no-op */ }
    public void stopTransitionTrace() throws android.os.RemoteException { /* no-op */ }
    public void saveWindowTraceToFile() throws android.os.RemoteException { /* no-op */ }
    public void showStrictModeViolation(boolean p0) throws android.os.RemoteException { /* no-op */ }
    public void setStrictModeVisualIndicatorPreference(java.lang.String p0) throws android.os.RemoteException { /* no-op */ }

    // -- feature / capability probes (sandbox: none supported) --
    public boolean isTaskSnapshotSupported() throws android.os.RemoteException { return false; }
    public boolean isLetterboxBackgroundMultiColored() throws android.os.RemoteException { return false; }
    public boolean isGlobalKey(int p0) throws android.os.RemoteException { return false; }
    public boolean isWindowToken(android.os.IBinder p0) throws android.os.RemoteException { return false; }
    public boolean isInTouchMode(int p0) throws android.os.RemoteException { return false; }
    public boolean shouldShowSystemDecors(int p0) throws android.os.RemoteException { return false; }
    public boolean shouldShowWithInsecureKeyguard(int p0) throws android.os.RemoteException { return false; }

    // -- display config probes (default phone-equivalent constants) --
    public int getBaseDisplayDensity(int p0) throws android.os.RemoteException { return 480; /* xxhdpi -- matches getDisplayInfo above */ }
    public int getInitialDisplayDensity(int p0) throws android.os.RemoteException { return 480; }
    public int getDisplayImePolicy(int p0) throws android.os.RemoteException { return 0; /* DISPLAY_IME_POLICY_LOCAL */ }
    public int getImeDisplayId() throws android.os.RemoteException { return 0; /* DEFAULT_DISPLAY */ }
    public int getDockedStackSide() throws android.os.RemoteException { return -1; /* DOCKED_INVALID */ }
    public int getWindowingMode(int p0) throws android.os.RemoteException { return 1; /* WINDOWING_MODE_FULLSCREEN */ }
    public int getLetterboxBackgroundColorInArgb() throws android.os.RemoteException { return 0xFF000000; /* opaque black */ }
    public int getPreferredOptionsPanelGravity(int p0) throws android.os.RemoteException { return 0x51; /* Gravity.BOTTOM | RIGHT */ }
    public int getRemoveContentMode(int p0) throws android.os.RemoteException { return 0; /* REMOVE_CONTENT_MODE_UNDEFINED */ }
    public int getDisplayIdByUniqueId(java.lang.String p0) throws android.os.RemoteException { return 0; /* DEFAULT_DISPLAY */ }
    public void getStableInsets(int p0, android.graphics.Rect p1) throws android.os.RemoteException { /* leave Rect zero-initialized */ }
    public java.lang.String[] getSupportedDisplayHashAlgorithms() throws android.os.RemoteException { return new java.lang.String[0]; }
    public java.util.List getPossibleDisplayInfo(int p0) throws android.os.RemoteException { return java.util.Collections.emptyList(); }
    public java.util.List notifyScreenshotListeners(int p0) throws android.os.RemoteException { return java.util.Collections.emptyList(); }

    // -- keyguard policy (sandbox: no lockscreen) --
    public void disableKeyguard(android.os.IBinder p0, java.lang.String p1, int p2) throws android.os.RemoteException { /* no-op */ }
    public void reenableKeyguard(android.os.IBinder p0, int p1) throws android.os.RemoteException { /* no-op */ }
    public void dismissKeyguard(com.android.internal.policy.IKeyguardDismissCallback p0, java.lang.CharSequence p1) throws android.os.RemoteException { /* no-op */ }
    public void exitKeyguardSecurely(android.view.IOnKeyguardExitResult p0) throws android.os.RemoteException { /* no-op */ }
    public void lockNow(android.os.Bundle p0) throws android.os.RemoteException { /* no-op */ }
    public void addKeyguardLockedStateListener(com.android.internal.policy.IKeyguardLockedStateListener p0) throws android.os.RemoteException { /* no-op */ }
    public void removeKeyguardLockedStateListener(com.android.internal.policy.IKeyguardLockedStateListener p0) throws android.os.RemoteException { /* no-op */ }

    // -- rotation / freeze (sandbox: orientation is fixed) --
    public void freezeRotation(int p0, java.lang.String p1) throws android.os.RemoteException { /* no-op */ }
    public void freezeDisplayRotation(int p0, int p1, java.lang.String p2) throws android.os.RemoteException { /* no-op */ }
    public void thawRotation(java.lang.String p0) throws android.os.RemoteException { /* no-op */ }
    public void thawDisplayRotation(int p0, java.lang.String p1) throws android.os.RemoteException { /* no-op */ }
    public void startFreezingScreen(int p0, int p1) throws android.os.RemoteException { /* no-op */ }
    public void stopFreezingScreen() throws android.os.RemoteException { /* no-op */ }
    public void setIgnoreOrientationRequest(int p0, boolean p1) throws android.os.RemoteException { /* no-op */ }
    public void setFixedToUserRotation(int p0, int p1) throws android.os.RemoteException { /* no-op */ }
    public void removeRotationWatcher(android.view.IRotationWatcher p0) throws android.os.RemoteException { /* no-op */ }

    // -- transient system UI / system dialogs (sandbox: none) --
    public void closeSystemDialogs(java.lang.String p0) throws android.os.RemoteException { /* no-op */ }
    public void hideTransientBars(int p0) throws android.os.RemoteException { /* no-op */ }
    public void showGlobalActions() throws android.os.RemoteException { /* no-op */ }
    public void refreshScreenCaptureDisabled() throws android.os.RemoteException { /* no-op */ }
    public void endProlongedAnimations() throws android.os.RemoteException { /* no-op */ }
    public void holdLock(android.os.IBinder p0, int p1) throws android.os.RemoteException { /* no-op */ }

    // -- animation scales / recents (sandbox: no recents UI) --
    public void setAnimationScale(int p0, float p1) throws android.os.RemoteException { /* no-op */ }
    public void setAnimationScales(float[] p0) throws android.os.RemoteException { /* no-op */ }
    public void setRecentsVisibility(boolean p0) throws android.os.RemoteException { /* no-op */ }
    public void setRecentsAppBehindSystemBars(boolean p0) throws android.os.RemoteException { /* no-op */ }
    public void setNavBarVirtualKeyHapticFeedbackEnabled(boolean p0) throws android.os.RemoteException { /* no-op */ }
    public void setSwitchingUser(boolean p0) throws android.os.RemoteException { /* no-op */ }

    // -- forced display overrides (sandbox: no shell override surface) --
    public void setForcedDisplayDensityForUser(int p0, int p1, int p2) throws android.os.RemoteException { /* no-op */ }
    public void setForcedDisplaySize(int p0, int p1, int p2) throws android.os.RemoteException { /* no-op */ }
    public void setForcedDisplayScalingMode(int p0, int p1) throws android.os.RemoteException { /* no-op */ }
    public void clearForcedDisplayDensityForUser(int p0, int p1) throws android.os.RemoteException { /* no-op */ }
    public void clearForcedDisplaySize(int p0) throws android.os.RemoteException { /* no-op */ }

    // -- state setters (sandbox: no observers, nothing to react) --
    public void setInTouchMode(boolean p0, int p1) throws android.os.RemoteException { /* no-op */ }
    public void setInTouchModeOnAllDisplays(boolean p0) throws android.os.RemoteException { /* no-op */ }
    public void setTaskSnapshotEnabled(boolean p0) throws android.os.RemoteException { /* no-op */ }
    public void setDisplayHashThrottlingEnabled(boolean p0) throws android.os.RemoteException { /* no-op */ }
    public void setDisplayImePolicy(int p0, int p1) throws android.os.RemoteException { /* no-op */ }
    public void setRemoveContentMode(int p0, int p1) throws android.os.RemoteException { /* no-op */ }
    public void setShouldShowSystemDecors(int p0, boolean p1) throws android.os.RemoteException { /* no-op */ }
    public void setShouldShowWithInsecureKeyguard(int p0, boolean p1) throws android.os.RemoteException { /* no-op */ }
    public void setWindowingMode(int p0, int p1) throws android.os.RemoteException { /* no-op */ }
    public void syncInputTransactions(boolean p0) throws android.os.RemoteException { /* no-op */ }
    public void markSurfaceSyncGroupReady(android.os.IBinder p0) throws android.os.RemoteException { /* no-op */ }
    public void updateDisplayWindowRequestedVisibleTypes(int p0, int p1, int p2, android.view.inputmethod.ImeTracker.Token p3) throws android.os.RemoteException { /* no-op */ }
    public void updateStaticPrivacyIndicatorBounds(int p0, android.graphics.Rect[] p1) throws android.os.RemoteException { /* no-op */ }

    // -- listener register/unregister (sandbox: no event source, listener never fires) --
    public void registerDisplayFoldListener(android.view.IDisplayFoldListener p0) throws android.os.RemoteException { /* no-op */ }
    public void unregisterDisplayFoldListener(android.view.IDisplayFoldListener p0) throws android.os.RemoteException { /* no-op */ }
    public boolean registerCrossWindowBlurEnabledListener(android.view.ICrossWindowBlurEnabledListener p0) throws android.os.RemoteException { return false; /* blur disabled in sandbox */ }
    public void unregisterCrossWindowBlurEnabledListener(android.view.ICrossWindowBlurEnabledListener p0) throws android.os.RemoteException { /* no-op */ }
    public void registerSystemGestureExclusionListener(android.view.ISystemGestureExclusionListener p0, int p1) throws android.os.RemoteException { /* no-op */ }
    public void unregisterSystemGestureExclusionListener(android.view.ISystemGestureExclusionListener p0, int p1) throws android.os.RemoteException { /* no-op */ }
    public boolean registerWallpaperVisibilityListener(android.view.IWallpaperVisibilityListener p0, int p1) throws android.os.RemoteException { return false; /* wallpaper hidden in sandbox */ }
    public void unregisterWallpaperVisibilityListener(android.view.IWallpaperVisibilityListener p0, int p1) throws android.os.RemoteException { /* no-op */ }
    public boolean registerScreenRecordingCallback(android.window.IScreenRecordingCallback p0) throws android.os.RemoteException { return false; /* never recording */ }
    public void unregisterScreenRecordingCallback(android.window.IScreenRecordingCallback p0) throws android.os.RemoteException { /* no-op */ }
    public void registerDecorViewGestureListener(android.view.IDecorViewGestureListener p0, int p1) throws android.os.RemoteException { /* no-op */ }
    public void unregisterDecorViewGestureListener(android.view.IDecorViewGestureListener p0, int p1) throws android.os.RemoteException { /* no-op */ }
    public void registerPinnedTaskListener(int p0, android.view.IPinnedTaskListener p1) throws android.os.RemoteException { /* no-op */ }
    public void registerTaskFpsCallback(int p0, android.window.ITaskFpsCallback p1) throws android.os.RemoteException { /* no-op */ }
    public void unregisterTaskFpsCallback(android.window.ITaskFpsCallback p0) throws android.os.RemoteException { /* no-op */ }
    public void registerTrustedPresentationListener(android.os.IBinder p0, android.window.ITrustedPresentationListener p1, android.window.TrustedPresentationThresholds p2, int p3) throws android.os.RemoteException { /* no-op */ }
    public void unregisterTrustedPresentationListener(android.window.ITrustedPresentationListener p0, int p1) throws android.os.RemoteException { /* no-op */ }

    // ------------------------------------------------------------------
    //   FAIL-LOUD UNOBSERVED METHODS (genuine binder semantics / non-trivial returns)
    // ------------------------------------------------------------------

    public android.view.SurfaceControl addShellRoot(int p0, android.view.IWindow p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "addShellRoot"); }
    public boolean addToSurfaceSyncGroup(android.os.IBinder p0, boolean p1, android.window.ISurfaceSyncGroupCompletedListener p2, android.window.AddToSurfaceSyncGroupResult p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "addToSurfaceSyncGroup"); }
    public android.window.WindowContextInfo attachWindowContextToDisplayArea(android.app.IApplicationThread p0, android.os.IBinder p1, int p2, int p3, android.os.Bundle p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "attachWindowContextToDisplayArea"); }
    public android.window.WindowContextInfo attachWindowContextToDisplayContent(android.app.IApplicationThread p0, android.os.IBinder p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "attachWindowContextToDisplayContent"); }
    public android.window.WindowContextInfo attachWindowContextToWindowToken(android.app.IApplicationThread p0, android.os.IBinder p1, android.os.IBinder p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "attachWindowContextToWindowToken"); }
    public void captureDisplay(int p0, android.window.ScreenCapture.CaptureArgs p1, android.window.ScreenCapture.ScreenCaptureListener p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "captureDisplay"); }
    public boolean clearWindowContentFrameStats(android.os.IBinder p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "clearWindowContentFrameStats"); }
    public void createInputConsumer(android.os.IBinder p0, java.lang.String p1, int p2, android.view.InputChannel p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "createInputConsumer"); }
    public boolean destroyInputConsumer(android.os.IBinder p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "destroyInputConsumer"); }
    public void detachWindowContext(android.os.IBinder p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "detachWindowContext"); }
    public android.view.KeyboardShortcutGroup getApplicationLaunchKeyboardShortcuts(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "getApplicationLaunchKeyboardShortcuts"); }
    public android.graphics.Region getCurrentImeTouchRegion() throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "getCurrentImeTouchRegion"); }
    public android.view.WindowContentFrameStats getWindowContentFrameStats(android.os.IBinder p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "getWindowContentFrameStats"); }
    public boolean getWindowInsets(int p0, android.os.IBinder p1, android.view.InsetsState p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "getWindowInsets"); }
    public boolean mirrorDisplay(int p0, android.view.SurfaceControl p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "mirrorDisplay"); }
    public android.view.SurfaceControl mirrorWallpaperSurface(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "mirrorWallpaperSurface"); }
    public void overridePendingAppTransitionMultiThumbFuture(android.view.IAppTransitionAnimationSpecsFuture p0, android.os.IRemoteCallback p1, boolean p2, int p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "overridePendingAppTransitionMultiThumbFuture"); }
    public void overridePendingAppTransitionRemote(android.view.RemoteAnimationAdapter p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "overridePendingAppTransitionRemote"); }
    public int[] registerDisplayWindowListener(android.view.IDisplayWindowListener p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "registerDisplayWindowListener"); }
    public int registerProposedRotationListener(android.os.IBinder p0, android.view.IRotationWatcher p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "registerProposedRotationListener"); }
    public void registerShortcutKey(long p0, com.android.internal.policy.IShortcutService p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "registerShortcutKey"); }
    public boolean reparentWindowContextToDisplayArea(android.app.IApplicationThread p0, android.os.IBinder p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "reparentWindowContextToDisplayArea"); }
    public boolean replaceContentOnDisplay(int p0, android.view.SurfaceControl p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "replaceContentOnDisplay"); }
    public void requestAppKeyboardShortcuts(com.android.internal.os.IResultReceiver p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "requestAppKeyboardShortcuts"); }
    public boolean requestAssistScreenshot(android.app.IAssistDataReceiver p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "requestAssistScreenshot"); }
    public void requestImeKeyboardShortcuts(com.android.internal.os.IResultReceiver p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "requestImeKeyboardShortcuts"); }
    public void requestScrollCapture(int p0, android.os.IBinder p1, int p2, android.view.IScrollCaptureResponseListener p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "requestScrollCapture"); }
    public android.graphics.Bitmap screenshotWallpaper() throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "screenshotWallpaper"); }
    public void setDisplayChangeWindowController(android.view.IDisplayChangeWindowController p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "setDisplayChangeWindowController"); }
    public void setDisplayWindowInsetsController(int p0, android.view.IDisplayWindowInsetsController p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "setDisplayWindowInsetsController"); }
    public void setGlobalDragListener(android.window.IGlobalDragListener p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "setGlobalDragListener"); }
    public void setShellRootAccessibilityWindow(int p0, int p1, android.view.IWindow p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "setShellRootAccessibilityWindow"); }
    public android.graphics.Bitmap snapshotTaskForRecents(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "snapshotTaskForRecents"); }
    public boolean startViewServer(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "startViewServer"); }
    public boolean stopViewServer() throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "stopViewServer"); }
    public boolean transferTouchGesture(android.window.InputTransferToken p0, android.window.InputTransferToken p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "transferTouchGesture"); }
    public void unregisterDisplayWindowListener(android.view.IDisplayWindowListener p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "unregisterDisplayWindowListener"); }
    public android.view.displayhash.VerifiedDisplayHash verifyDisplayHash(android.view.displayhash.DisplayHash p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("window", "verifyDisplayHash"); }

    // ------------------------------------------------------------------
    //   Diagnostic helpers (not part of IWindowManager surface)
    // ------------------------------------------------------------------

    /** Returns the count of currently-tracked window tokens (for tests). */
    public int activeWindowTokenCount() {
        return mWindowTokens.size();
    }

    /** Returns the last value passed to setEventDispatching (for tests). */
    public boolean isEventDispatching() {
        return mEventDispatching;
    }

    @Override
    public String toString() {
        return "WestlakeWindowManagerService{tokens=" + mWindowTokens.size()
                + ", dispatching=" + mEventDispatching + "}";
    }
}
