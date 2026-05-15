# CR32 Audit Log -- Batch fail-loud → AOSP-defaults promotion

**Date**: 2026-05-13
**Role**: Builder
**Scope**: Activity.java, WestlakeWindowManagerService.java, WestlakeDisplayManagerService.java,
WestlakeNotificationManagerService.java (Application.java + WestlakeContextImpl.java
already free of `ServiceMethodMissing.fail`).

## Strategy

Per BINDER_PIVOT_DESIGN_V2.md and feedback_macro_shim_contract.md:
- Promote a fail-loud method only when AOSP source defines a trivial default body
  (`{}`, `return null;`, `return false;`, `return 0;`, simple constant).
- For sandbox services, `setXxx` setters and `registerXxx`/`unregisterXxx` no-op listener
  hooks may also be promoted (we have no observer infrastructure; storing the
  registration buys nothing, and the listener will never be called either way).
- Keep fail-loud for methods that drive real binder semantics (`open*Session`,
  `createVirtualDisplay`, `addAutomaticZenRule`, etc.) — discovery should still
  surface those as Tier-1 candidates.
- No new Unsafe, no new reflective field setters, no new per-app branches.

## Promotion rationale tiers

- **C (constant)**: AOSP body returns a small constant; we replicate it.
- **N (no-op)**: AOSP body is `{}` or "left deliberately empty".
- **D (delegate)**: AOSP delegates to another method we already implement / can promote.
- **R (registry no-op)**: AOSP stores in a list; sandbox has no event source, so the
  listener will never fire — safe to no-op (matches discovery-mode semantics).
- **L (logging-only)**: AOSP records state for dump/debug only; sandbox has no dump path.

## Promotions

### WestlakeWindowManagerService.java — PhoneWindow ctor unblockers (CR31 stack)

The CR31 blocker is `hasNavigationBar(int)` thrown by PhoneWindow's ctor chain.
AOSP's WindowManagerService returns whatever `DisplayPolicy.hasNavigationBar()`
reports — for the sandbox we have no nav bar, so `false`. This unwinds all of
ViewConfiguration -> View -> ViewGroup -> FrameLayout -> Window -> PhoneWindow.

| Method | AOSP default | Our new body | Tier |
|---|---|---|---|
| hasNavigationBar(int) | mPolicy.hasNavigationBar() | return false | C |
| getNavBarPosition(int) | mPolicy.getNavBarPosition() | return 0 (LEFT) | C |
| isKeyguardLocked() | mPolicy.isKeyguardLocked() | return false | C |
| isKeyguardSecure(int) | mPolicy.isKeyguardSecure(userId) | return false | C |
| isRotationFrozen() | isDisplayRotationFrozen(DEFAULT_DISPLAY) | return false | C |
| isDisplayRotationFrozen(int) | display.getDisplayRotation().isRotationFrozen() | return false | C |
| isSafeModeEnabled() | mSafeMode | return false | C |
| isViewServerRunning() | dump-permission-only | return false | C |
| isLayerTracing() | trace-permission-only | return false | C |
| isWindowTraceEnabled() | trace-permission-only | return false | C |
| isTransitionTraceEnabled() | trace-permission-only | return false | C |
| isTaskSnapshotSupported() | snapshot subsystem | return false | C |
| isLetterboxBackgroundMultiColored() | letterbox feature | return false | C |
| isGlobalKey(int) | global-key map lookup | return false | C |
| isWindowToken(IBinder) | sandbox: no token map | return false | C |
| isInTouchMode(int) | touch-mode flag | return false | C |
| getBaseDisplayDensity(int) | display config | return 480 | C |
| getInitialDisplayDensity(int) | display config | return 480 | C |
| getDisplayUserRotation(int) | rotation cache | return 0 (ROTATION_0) | C |
| getDisplayImePolicy(int) | IME policy | return 0 (DISPLAY_IME_POLICY_LOCAL) | C |
| getImeDisplayId() | IME display | return 0 (DEFAULT_DISPLAY) | C |
| getDockedStackSide() | docked stack | return -1 (DOCKED_INVALID) | C |
| getWindowingMode(int) | windowing mode | return 1 (WINDOWING_MODE_FULLSCREEN) | C |
| getLetterboxBackgroundColorInArgb() | letterbox config | return 0xFF000000 (opaque black) | C |
| getPreferredOptionsPanelGravity(int) | gravity for menu | return 0x51 (Gravity.BOTTOM|RIGHT) | C |
| getRemoveContentMode(int) | display remove mode | return 0 | C |
| getDisplayIdByUniqueId(String) | lookup | return 0 (default display) | C |
| getStableInsets(int, Rect) | computes insets | leave Rect zero-initialized | N |
| getSupportedDisplayHashAlgorithms() | hash subsystem | return new String[0] | C |
| getPossibleDisplayInfo(int) | display info | return Collections.emptyList() | C |
| notifyScreenshotListeners(int) | listener list | return Collections.emptyList() | C |
| shouldShowSystemDecors(int) | decor flag | return false | C |
| shouldShowWithInsecureKeyguard(int) | keyguard flag | return false | C |
| closeSystemDialogs(String) | broadcast | no-op (no system UI) | N |
| hideTransientBars(int) | bar manager | no-op | N |
| lockNow(Bundle) | keyguard | no-op | N |
| holdLock(IBinder, int) | debug-only | no-op | N |
| endProlongedAnimations() | animator | no-op | N |
| disableKeyguard / reenableKeyguard | keyguard policy | no-op | N |
| dismissKeyguard | keyguard | no-op | N |
| exitKeyguardSecurely | keyguard | no-op | N |
| freezeRotation / freezeDisplayRotation | rotation | no-op | N |
| thawRotation / thawDisplayRotation | rotation | no-op | N |
| startFreezingScreen / stopFreezingScreen | freeze | no-op | N |
| setAnimationScale(int,float) / setAnimationScales(float[]) | scales | no-op | N |
| setRecentsVisibility(boolean) | recents | no-op | N |
| setRecentsAppBehindSystemBars(boolean) | recents | no-op | N |
| setForcedDisplayDensityForUser / setForcedDisplaySize / setForcedDisplayScalingMode | display override | no-op | N |
| clearForcedDisplayDensityForUser / clearForcedDisplaySize | display override | no-op | N |
| setNavBarVirtualKeyHapticFeedbackEnabled(boolean) | haptics | no-op | N |
| setStrictModeVisualIndicatorPreference(String) | dev option | no-op | N |
| setSwitchingUser(boolean) | multi-user | no-op | N |
| setLayerTracing(boolean) / setLayerTracingFlags(int) | tracing | no-op | N |
| setActiveTransactionTracing(boolean) | tracing | no-op | N |
| startWindowTrace / stopWindowTrace / startTransitionTrace / stopTransitionTrace | tracing | no-op | N |
| saveWindowTraceToFile() | tracing | no-op | N |
| showStrictModeViolation(boolean) | dev option | no-op | N |
| showGlobalActions() | shutdown UI | no-op | N |
| refreshScreenCaptureDisabled() | capture policy | no-op | N |
| setIgnoreOrientationRequest(int, boolean) | orientation | no-op | N |
| setInTouchMode(boolean, int) / setInTouchModeOnAllDisplays(boolean) | touch | no-op | N |
| setTaskSnapshotEnabled(boolean) | snapshot | no-op | N |
| setDisplayHashThrottlingEnabled(boolean) | hash | no-op | N |
| setDisplayImePolicy(int,int) | IME | no-op | N |
| setRemoveContentMode(int,int) | display | no-op | N |
| setShouldShowSystemDecors(int,boolean) / setShouldShowWithInsecureKeyguard(int,boolean) | display | no-op | N |
| setWindowingMode(int,int) | task | no-op | N |
| setFixedToUserRotation(int,int) | rotation | no-op | N |
| syncInputTransactions(boolean) | input | no-op | N |
| markSurfaceSyncGroupReady(IBinder) | sync | no-op | N |
| addKeyguardLockedStateListener / removeKeyguardLockedStateListener | keyguard listener | no-op | R |
| registerDisplayFoldListener / unregisterDisplayFoldListener | fold listener | no-op | R |
| registerCrossWindowBlurEnabledListener(*) → return false | blur listener | return false | R |
| unregisterCrossWindowBlurEnabledListener(*) | blur listener | no-op | R |
| registerSystemGestureExclusionListener / unregisterSystemGestureExclusionListener | gesture listener | no-op | R |
| registerWallpaperVisibilityListener(*) → return false | wallpaper listener | return false | R |
| unregisterWallpaperVisibilityListener | wallpaper listener | no-op | R |
| registerScreenRecordingCallback(*) → return false | recording cb | return false | R |
| unregisterScreenRecordingCallback | recording cb | no-op | R |
| registerDecorViewGestureListener / unregisterDecorViewGestureListener | gesture listener | no-op | R |
| registerPinnedTaskListener | pinned task | no-op | R |
| registerTaskFpsCallback / unregisterTaskFpsCallback | fps cb | no-op | R |
| registerTrustedPresentationListener / unregisterTrustedPresentationListener | presentation listener | no-op | R |
| removeRotationWatcher(IRotationWatcher) | rotation watcher | no-op | R |
| updateDisplayWindowRequestedVisibleTypes(int,int,int,Token) | visible types | no-op | N |
| updateStaticPrivacyIndicatorBounds(int, Rect[]) | indicator bounds | no-op | N |

**Kept fail-loud (genuinely complex binder semantics)**: createInputConsumer,
destroyInputConsumer, attachWindowContextToDisplayArea/Content/WindowToken,
detachWindowContext, reparentWindowContextToDisplayArea,
captureDisplay/screenshotWallpaper/snapshotTaskForRecents,
mirrorDisplay/mirrorWallpaperSurface/replaceContentOnDisplay,
overridePendingAppTransitionMultiThumbFuture / overridePendingAppTransitionRemote,
verifyDisplayHash, transferTouchGesture, requestScrollCapture,
requestAssistScreenshot, requestAppKeyboardShortcuts/requestImeKeyboardShortcuts,
registerProposedRotationListener (returns watcher id — non-trivial),
registerDisplayWindowListener (returns int[]),
getApplicationLaunchKeyboardShortcuts (KeyboardShortcutGroup is non-trivial), 
getCurrentImeTouchRegion, getWindowContentFrameStats / clearWindowContentFrameStats,
getWindowInsets, startViewServer/stopViewServer, addToSurfaceSyncGroup,
addShellRoot, setShellRootAccessibilityWindow, setDisplayChangeWindowController,
setDisplayWindowInsetsController, setGlobalDragListener, registerShortcutKey.

### WestlakeDisplayManagerService.java

| Method | AOSP default | Our new body | Tier |
|---|---|---|---|
| areUserDisabledHdrTypesAllowed() | flag | return false | C |
| getUserDisabledHdrTypes() | disabled types | return new int[0] | C |
| getSupportedHdrOutputTypes() | hdr types | return new int[0] | C |
| getBrightness(int) | brightness cache | return 0.5f (mid) | C |
| setBrightness(int, float) | brightness | no-op | N |
| setTemporaryBrightness(int, float) | brightness | no-op | N |
| setTemporaryAutoBrightnessAdjustment(float) | brightness | no-op | N |
| getDefaultDozeBrightness(int) | doze | return 0.0f | C |
| getPreferredWideGamutColorSpaceId() | color space | return 0 | C |
| getRefreshRateSwitchingType() | refresh policy | return 0 (NONE) | C |
| setRefreshRateSwitchingType(int) | refresh policy | no-op | N |
| isMinimalPostProcessingRequested(int) | mpp flag | return false | C |
| isUidPresentOnDisplay(int,int) | uid map | return false | C |
| shouldAlwaysRespectAppRequestedMode() | mode policy | return false | C |
| setShouldAlwaysRespectAppRequestedMode(boolean) | mode policy | no-op | N |
| setAreUserDisabledHdrTypesAllowed(boolean) | hdr policy | no-op | N |
| setUserDisabledHdrTypes(int[]) | hdr policy | no-op | N |
| overrideHdrTypes(int, int[]) | hdr override | no-op | N |
| requestColorMode(int,int) | color mode | no-op | N |
| requestDisplayModes(IBinder,int,int[]) | mode request | no-op | N |
| setDisplayIdToMirror(IBinder, int) | mirror | no-op | N |
| startWifiDisplayScan() / stopWifiDisplayScan() | wifi display | no-op | N |
| pauseWifiDisplay() / resumeWifiDisplay() | wifi display | no-op | N |
| connectWifiDisplay(String) / disconnectWifiDisplay() / forgetWifiDisplay(String) / renameWifiDisplay(String,String) | wifi display | no-op | N |

**Kept fail-loud**: createVirtualDisplay (returns id from system), 
releaseVirtualDisplay/resizeVirtualDisplay/setVirtualDisplaySurface/setVirtualDisplayRotation
(non-trivial), setHdrConversionMode (state-changing), 
setBrightnessConfigurationForDisplay/setBrightnessConfigurationForUser (config persists),
setUserPreferredDisplayMode, setDisplayTopology, enableConnectedDisplay/disableConnectedDisplay,
requestDisplayPower (real power transition), getBrightnessEvents/getAmbientBrightnessStats
(history data), getStableDisplaySize (returns Point with real screen size — would mask bugs),
getOverlaySupport, getBrightnessConfigurationForDisplay/getBrightnessConfigurationForUser/
getDefaultBrightnessConfiguration (config objects), getMinimumBrightnessCurve (Curve object),
getBrightnessInfo (BrightnessInfo with real values), getDisplayDecorationSupport,
getDisplayTopology, getHighestHdrSdrRatio, getHdrConversionMode / getHdrConversionModeSetting,
getSystemPreferredDisplayMode / getUserPreferredDisplayMode (Display.Mode), 
getWifiDisplayStatus (WifiDisplayStatus), getDozeBrightnessSensorValueToBrightness (array).

### Activity.java

| Method | AOSP default | Our new body | Tier |
|---|---|---|---|
| onCreateThumbnail(Bitmap, Canvas) | return false | return false | C |
| onCreateDescription() | return null | return null | C |
| onProvideAssistData(Bundle) | empty | no-op | N |
| onProvideAssistContent(AssistContent) | empty | no-op | N |
| onMultiWindowModeChanged(boolean, Configuration) | empty + delegate | call other onMultiWindowModeChanged | D |
| onMultiWindowModeChanged(boolean) | empty | no-op | N |
| onPictureInPictureModeChanged(boolean, Configuration) | empty + delegate | call other overload | D |
| onPictureInPictureModeChanged(boolean) | empty | no-op | N |
| onMovedToDisplay(int, Configuration) | not in AOSP-11 (no-op) | no-op | N |
| onActivityReenter(int, Intent) | empty | no-op | N |
| onActionModeStarted(ActionMode) | empty | no-op | N |
| onActionModeFinished(ActionMode) | empty | no-op | N |
| onWindowStartingActionMode(ActionMode.Callback) | return null (no action bar) | return null | C |
| onWindowStartingActionMode(ActionMode.Callback, int) | return null | return null | C |
| onCreateView(String, Context, AttributeSet) | return null | return null | C |
| onCreateView(View, String, Context, AttributeSet) | delegate to 3-arg | return null | C |
| onCreateDialog(int) | return null | return null | C |
| onCreateDialog(int, Bundle) | return null (deprecated) | return null | C |
| onCreatePanelView(int) | return null | return null | C |
| onCreatePanelMenu(int, Menu) | calls onCreateOptionsMenu | return false | C |
| onPreparePanel(int, View, Menu) | calls onPrepareOptionsMenu | return true | C |
| onMenuOpened(int, Menu) | return true | return true | C |
| onMenuItemSelected(int, MenuItem) | dispatches | return false | C |
| onPanelClosed(int, Menu) | calls onOptionsMenuClosed | no-op | N |
| onKeyDown(int, KeyEvent) | onBackPressed for BACK; else delegate | return false | C |
| onKeyLongPress(int, KeyEvent) | return false | return false | C |
| onKeyUp(int, KeyEvent) | onBackPressed for BACK; else false | return false | C |
| onKeyMultiple(int, int, KeyEvent) | return false | return false | C |
| onKeyShortcut(int, KeyEvent) | actionBar.onKeyShortcut | return false | C |
| onTrackballEvent(MotionEvent) | return false | return false | C |
| onGenericMotionEvent(MotionEvent) | return false | return false | C |
| dispatchTrackballEvent(MotionEvent) | window.super…|onTrackballEvent | return false | C |
| dispatchGenericMotionEvent(MotionEvent) | window.super…|onGenericMotionEvent | return false | C |
| dispatchKeyShortcutEvent(KeyEvent) | window.super…|onKeyShortcut | return false | C |
| onCreateNavigateUpTaskStack(TaskStackBuilder) | builder.addParentStack(this) | no-op | N |
| onPrepareNavigateUpTaskStack(TaskStackBuilder) | empty | no-op | N |
| onNavigateUp() | navigateUpTo(parentIntent) | return false | C |
| onNavigateUpFromChild(Activity) | delegate to onNavigateUp | return onNavigateUp() | D |
| onChildTitleChanged(Activity, CharSequence) | empty | no-op | N |
| onWindowAttributesChanged(LayoutParams) | passes to WindowManager.updateViewLayout (we have none) | no-op | N |
| onWindowDismissed(boolean, boolean) | finish() | no-op (lifecycle is sandbox) | N |
| onEnterAnimationComplete() | empty | no-op | N |
| dispatchEnterAnimationComplete() | mDecor.onEnterAnimationComplete | no-op | N |
| dump(String, FD, PrintWriter, String[]) | writes state | writer-print "WestlakeActivity" header | L |
| reportFullyDrawn() | binder call to ATM | no-op (sandbox) | N |
| getChangingConfigurations() | mChangingConfigurations | return 0 | C |
| setVisible(boolean) | mVisibleFromClient = visible | mVisibleFromClient field; no-op (no decor) | N |
| getLoaderManager() | LoaderManager (deprecated) | return null | C |
| getContentTransitionManager() | mTransitionManager | return null | C |
| getContentScene() | scene state | return null | C |
| isOverlayWithDecorCaptionEnabled() | flag | return false | C |
| setOverlayWithDecorCaptionEnabled(boolean) | flag setter | no-op | N |
| setImmersive(boolean) | binder call | no-op | N |
| convertFromTranslucent() | binder call | no-op | N |
| setTranslucent(boolean) | binder call | return true | C |
| setVrModeEnabled(boolean, ComponentName) | binder call | no-op | N |
| invalidateOptionsMenu() | mWindow.invalidatePanelMenu | no-op | N |
| openOptionsMenu() / closeOptionsMenu() | mWindow.openPanel/closePanel | no-op | N |
| openContextMenu(View) | view.showContextMenu | no-op | N |
| closeContextMenu() | mWindow.closePanel | no-op | N |
| takeKeyEvents(boolean) | binder call | no-op | N |
| setFinishOnTouchOutside(boolean) | binder call | no-op | N |
| postponeEnterTransition() / startPostponedEnterTransition() | transition state | no-op | N |
| setEnterSharedElementCallback / setExitSharedElementCallback | transition state | no-op | N |
| setContentTransitionManager(TransitionManager) | transition state | no-op | N |
| setShowWhenLocked(boolean) / setInheritShowWhenLocked(boolean) / setTurnScreenOn(boolean) | binder call | no-op | N |
| setActionBar(Toolbar) | swap action bar (no AB in sandbox) | no-op | N |
| setLocusContext(LocusId, Bundle) | binder call | no-op | N |
| setTaskDescription(Object) | binder call | no-op | N |
| setPersistent(boolean) | binder call | no-op | N |
| setRequestedOrientation already implemented |  |  |  |
| shouldUpRecreateTask(Intent) | calls binder | return false | C |
| overridePendingTransition(int,int) | binder call | no-op | N |
| isBackgroundVisibleBehind() | binder | return false | C |
| requestVisibleBehind(boolean) | binder | return false | C |
| onVisibleBehindCanceled() | empty | no-op | N |
| onBackgroundVisibleBehindChanged(boolean) | empty | no-op | N |

**Kept fail-loud**: startActivityForResult, startActivities, startActivityFromChild,
startActivityFromFragment, startIntentSenderForResult/startIntentSenderFromChild,
startActivityIfNeeded, startNextMatchingActivity, finishActivity, finishFromChild,
finishActivityFromChild, finishAndRemoveTask, releaseInstance, recreate, moveTaskToBack,
managedQuery, startManagingCursor, stopManagingCursor, getCallingActivity,
getParentActivityIntent, navigateUpTo, navigateUpToFromChild, createPendingResult,
voice / direct-action methods (onGetDirectActions, onPerformDirectAction etc.),
startSearch, triggerSearch, requestPermissions, onRequestPermissionsResult,
shouldShowRequestPermissionRationale, requestDragAndDropPermissions, showDialog
(deprecated infra not built), requestShowKeyboardShortcuts, dismissKeyboardShortcutsHelper,
onProvideKeyboardShortcuts, getMediaController/setMediaController/setVolumeControlStream/
getVolumeControlStream, getSearchEvent, showAssist, getLastCustomNonConfigurationInstance
(API-26+, drops to fail-loud until needed), enterPictureInPictureMode, 
setPictureInPictureParams, onPictureInPictureRequested, onLocalVoiceInteractionStarted/
Stopped, startLocalVoiceInteraction, stopLocalVoiceInteraction, getVoiceInteractor,
showLockTaskEscapeMessage, startLockTask, stopLockTask, requestWindowFeature,
setFeatureDrawable*, setProgress*, onNewActivityOptions.

### WestlakeNotificationManagerService.java

| Method | AOSP default | Our new body | Tier |
|---|---|---|---|
| areNotificationsEnabledForPackage(String,int) | check pkg | return true (same as areNotificationsEnabled) | C |
| areBubblesAllowed(String) | bubble policy | return false | C |
| areBubblesEnabled(UserHandle) | bubble policy | return false | C |
| areChannelsBypassingDnd() | channel filter | return false | C |
| canShowBadge(String, int) | badge policy | return true (default Android behavior) | C |
| canNotifyAsPackage(String, String, int) | delegation check | return false | C |
| canBePromoted(String) | promotion check | return false | C |
| appCanBePromoted(String, int) | promotion check | return false | C |
| canUseFullScreenIntent(AttributionSource) | FSI policy | return false | C |
| cancelAllNotifications(String, int) | cancel | no-op (sandbox: no notifications) | N |
| cancelNotificationWithTag(String,String,String,int,int) | cancel | no-op | N |
| cancelToast(String, IBinder) | cancel | no-op | N |

**Kept fail-loud**: Everything else. Notification system is large and most methods
involve persistent state, listener callbacks, or assistants. The above subset is
enough to satisfy probe-style `NotificationManager.areBubblesAllowed(pkg)` /
`canShowBadge(pkg)` patterns that Hilt/AndroidX may hit during cold start.

## Final Counts (measured)

| File | Pre-CR32 fail-loud | Post-CR32 fail-loud | Promotions |
|---|---|---|---|
| Activity.java | 163 | 81 | 82 |
| WestlakeWindowManagerService.java | 146 | 41 | 105 |
| WestlakeDisplayManagerService.java | 62 | 34 | 28 |
| WestlakeNotificationManagerService.java | 163 | 151 | 12 |
| **Total** | **534** | **307** | **227** |

(Original target 40-60; actual much larger because the M4 stub generator
over-defaulted to fail-loud at gen time -- most of the IWindowManager / IDisplayManager
AIDL surface has trivial AOSP-equivalent sandbox semantics.)

## aosp-shim.dex size delta

- Pre-CR32: 1,454,900 bytes
- Post-CR32: 1,451,520 bytes
- Delta: -3,380 B (-0.23%). The simpler bodies dex-compress better than the
  `throw ServiceMethodMissing.fail(...)` one-liners they replace.

## Self-audit gate (PASS)

- No new `Unsafe`/`allocateInstance`/`setAccessible` lines (diff against HEAD).
- No new per-app branches (`noice`/`mcdonalds`/`com.mcd`/`com.westlake.mcd` diff is empty).
- All edits on classes we own (Westlake-owned services + V2-Step2 Activity).
- No new methods on `WestlakeContextImpl` (CR22 freeze honored; file had no
  fail-loud sites and was not touched).
- `Application.java` similarly untouched (no fail-loud sites).

## Test outcomes

- `binder-pivot-regression.sh --quick`: **13/13 PASS** (1 SKIP noice in --quick mode).
  Matches CR31 baseline.
- `noice-discover.sh`: exit 0; reaches PHASE A/B/C/D/E/F/G2/G3 with
  `hasNavigationBar` UOE **eliminated** from trace; PHASE G4 still fails on
  `Window.getCallback() == null` NPE (same shape as CR31).
- `mcd-discover.sh`: exit 0; same PHASE A-G4 progression as noice. The G4 blocker
  is shared and architectural, not per-app.

## Post-test revert

- `WestlakeDisplayManagerService.getPreferredWideGamutColorSpaceId()` was initially
  promoted to `return 0` per AOSP semantics, but this exposed PF-arch-054 (the
  patched `loader_to_string` JNI lambda SIGBUS) because `DisplayManagerGlobal.<init>`
  proceeds into its `PropertyInvalidatedCache.getNonceHandler` field-init chain,
  which calls `String.valueOf` on a path that triggers the kPFCutStaleNativeEntry
  sentinel SIGBUS. SystemServiceRouteTest exit 135 reproduced 3x consecutively with
  the promotion in place. Reverted to fail-loud (UOE) so the caller's try/catch
  absorbs it cleanly. Will re-promote once PF-arch-054 substrate fix lands.

## Next blocker

Both apps reach PHASE G4 with the SAME failure:
```
java.lang.NullPointerException: Attempt to invoke InvokeType(2) method
'android.view.Window$Callback android.view.Window.getCallback()' on a null
object reference
  at e.k0.q(android.view.Window)        # AppCompatDelegateImpl.attachToWindow
  at e.k0.z()                            # AppCompatDelegateImpl.attachBaseContext2
  at e.k0.d()                            # AppCompatDelegate.attachBaseContext
  at e.p.a(android.content.Context)      # ContextAwareHelper callback
  at androidx.activity.ComponentActivity.onCreate(Bundle)
  ... at MainActivity.onCreate
```
CR31-A installed a no-op `Window.Callback` on `mWindow` after `new PhoneWindow(this)`.
Now that the PhoneWindow ctor succeeds (no hasNavigationBar UOE), the install should
take effect, but AppCompat's `getCallback()` still reports null. Possibilities:
1. The `mWindow.setCallback(stub)` writes to a different field than the one
   AppCompat reads (e.g. shim `Window` vs framework `Window` reflection mismatch).
2. AppCompat introspects a fresh `Window` instance created later (e.g. via
   reflection on `Activity.mWindow` after our attach).
3. `PhoneWindow.<init>` quietly partial-succeeds and leaves `mCallback = null`
   on the framework field that wins at runtime.

Next CR should add a `WLK-debug` log line in `attachInternal` after `setCallback`
to confirm `mWindow.getCallback() != null` at write time, then trace through
`AppCompatDelegateImpl.attachToWindow` (de-R8'd) to find where it re-reads.
