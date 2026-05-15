// SPDX-License-Identifier: Apache-2.0
//
// Westlake V2-Step5 Window stub (decision 12-A from BINDER_PIVOT_DESIGN_V2.md §3.5).
//
// Westlake-owned classpath-shadowed Window. No real PhoneWindow chrome
// (no title bar, no action bar, no status/nav bar, no system insets).
// `setContentView(int)` inflates the layout via LayoutInflater and
// stores the result as both decor view and content view; `getDecorView()`
// returns that view.
//
// Generic across all APKs — no per-app branches. The pre-V2 Window had
// McDonalds-specific structured page-shell + toolbar alias logic; that
// has been removed per the "NO per-app hacks" architectural rule and the
// CR14/CR16 launcher-slim cleanup.
//
// Backwards-compatible shim methods (`adoptContext`, `installMinimalStandaloneContent`)
// are retained as benign no-ops because existing callers in Activity.java,
// WestlakeActivityThread.java, and WestlakeLauncher.java already wrap
// these calls in try/catch (Throwable), but keeping them as defined
// no-ops avoids stack-trace noise.

package android.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.widget.MediaController;

public class Window {

    // ------------------------------------------------------------------
    // Feature / progress / decor constants (kept from V1 Window — Tier-1
    // surface read by AppCompat and many host apps).
    // ------------------------------------------------------------------
    public static final int FEATURE_OPTIONS_PANEL = 0;
    public static final int FEATURE_NO_TITLE = 1;
    public static final int FEATURE_PROGRESS = 2;
    public static final int FEATURE_LEFT_ICON = 3;
    public static final int FEATURE_RIGHT_ICON = 4;
    public static final int FEATURE_INDETERMINATE_PROGRESS = 5;
    public static final int FEATURE_CONTEXT_MENU = 6;
    public static final int FEATURE_CUSTOM_TITLE = 7;
    public static final int FEATURE_ACTION_BAR = 8;
    public static final int FEATURE_ACTION_BAR_OVERLAY = 9;
    public static final int FEATURE_ACTION_MODE_OVERLAY = 10;
    public static final int FEATURE_SWIPE_TO_DISMISS = 11;
    public static final int FEATURE_CONTENT_TRANSITIONS = 12;
    public static final int FEATURE_ACTIVITY_TRANSITIONS = 13;
    public static final int FEATURE_MAX = FEATURE_ACTIVITY_TRANSITIONS;

    public static final int PROGRESS_VISIBILITY_ON = -1;
    public static final int PROGRESS_VISIBILITY_OFF = -2;
    public static final int PROGRESS_INDETERMINATE_ON = -3;
    public static final int PROGRESS_INDETERMINATE_OFF = -4;
    public static final int PROGRESS_START = 0;
    public static final int PROGRESS_END = 10000;
    public static final int PROGRESS_SECONDARY_START = 20000;
    public static final int PROGRESS_SECONDARY_END = 30000;

    public static final int DECOR_CAPTION_SHADE_AUTO = 0;
    public static final int DECOR_CAPTION_SHADE_DARK = 0;
    public static final int DECOR_CAPTION_SHADE_LIGHT = 0;

    public static final int ID_ANDROID_CONTENT = 0x01020002;
    public static final int NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME = 0;
    public static final int STATUS_BAR_BACKGROUND_TRANSITION_NAME = 0;

    // ------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------
    private Context mContext;
    private View mDecorView;
    private View mContentView;
    private Callback mCallback;
    private CharSequence mTitle;
    private int mTitleColor;
    private int mStatusBarColor;
    private int mNavigationBarColor;
    private int mFeatures;
    private int mFlags;

    // ------------------------------------------------------------------
    // Callback (Activity implements this)
    // ------------------------------------------------------------------
    public interface Callback {
        // Required methods (implementers must provide). These match the
        // pre-V2 Window.Callback so existing Activity.java implementations
        // remain valid.
        boolean dispatchKeyEvent(KeyEvent event);
        boolean dispatchTouchEvent(MotionEvent event);
        boolean dispatchTrackballEvent(MotionEvent event);
        void onContentChanged();
        void onWindowFocusChanged(boolean hasFocus);
        void onAttachedToWindow();
        void onDetachedFromWindow();
        void onWindowAttributesChanged(WindowManager.LayoutParams attrs);
        boolean onMenuOpened(int featureId, android.view.Menu menu);
        boolean onMenuItemSelected(int featureId, android.view.MenuItem item);
        void onPanelClosed(int featureId, android.view.Menu menu);
        boolean onSearchRequested();
        View onCreatePanelView(int featureId);
        boolean onCreatePanelMenu(int featureId, android.view.Menu menu);
        boolean onPreparePanel(int featureId, View view, android.view.Menu menu);

        // Methods added in AOSP after API 19. Defaulted to no-op so existing
        // shim Callback implementers (which only override the legacy set
        // above) compile unchanged.
        default boolean dispatchKeyShortcutEvent(KeyEvent event) { return false; }
        default boolean dispatchGenericMotionEvent(MotionEvent event) { return false; }
        default boolean dispatchPopulateAccessibilityEvent(android.view.accessibility.AccessibilityEvent event) { return false; }
        default boolean onSearchRequested(SearchEvent searchEvent) { return onSearchRequested(); }
        default android.view.ActionMode onWindowStartingActionMode(android.view.ActionMode.Callback callback) { return null; }
        default android.view.ActionMode onWindowStartingActionMode(android.view.ActionMode.Callback callback, int type) { return null; }
        default void onActionModeStarted(android.view.ActionMode mode) {}
        default void onActionModeFinished(android.view.ActionMode mode) {}
    }

    public static interface OnContentApplyWindowInsetsListener {
        android.util.Pair<android.graphics.Insets, WindowInsets> onContentApplyWindowInsets(View view, WindowInsets insets);
    }

    public static interface OnFrameMetricsAvailableListener {}

    // ------------------------------------------------------------------
    // ctor
    // ------------------------------------------------------------------
    public Window(Context context) {
        mContext = context;
        // CR36: don't construct a FrameLayout here. Building a real
        // android.view.View in our standalone dalvikvm substrate pulls
        // android.graphics.RenderNode.nCreate from framework.jar, whose
        // native side is not loaded — the ctor throws UnsatisfiedLinkError
        // (a LinkageError) and leaves mWindow=null all the way out to
        // AppCompat's attachToWindow, which then NPEs on Window.getCallback().
        //
        // setContentView() already lazily constructs its own decor when an
        // app invokes it, and that path is `try { ... } catch (Throwable)`
        // wrapped so the same UnsatisfiedLinkError there degrades to a
        // null content. peekDecorView() / getDecorView() return null until
        // setContentView fills them in, which matches AOSP's contract.
        //
        // Safe-primitive (contract bucket (b)): return null/empty for state
        // we cannot honestly populate in this substrate. The Window object
        // itself MUST be constructable so callers' mWindow field is non-null.
        mDecorView = null;
    }

    // ------------------------------------------------------------------
    // Tier-1 API surface
    // ------------------------------------------------------------------
    public Context getContext() { return mContext; }

    /** Backwards-compat shim entry point used by Activity / WestlakeActivityThread. */
    public void adoptContext(Context context) {
        if (context != null) {
            mContext = context;
        }
    }

    /**
     * Backwards-compat shim entry point used by WestlakeLauncher. V2 design
     * has no chrome/title bar/action bar — caller already handles return=false
     * gracefully.
     */
    public boolean installMinimalStandaloneContent() {
        return false;
    }

    public void setContentView(int layoutResID) {
        try {
            LayoutInflater inflater = getLayoutInflater();
            android.widget.FrameLayout tempRoot = new android.widget.FrameLayout(mContext);
            View inflated = null;
            try {
                inflated = inflater.inflate(layoutResID, tempRoot, true);
            } catch (Throwable t) {
                try {
                    inflated = inflater.inflate(layoutResID, null);
                } catch (Throwable ignored) {}
            }
            if (tempRoot.getChildCount() > 0) {
                View content = tempRoot.getChildAt(0);
                tempRoot.removeAllViews();
                setContentView(content);
            } else if (inflated != null && inflated != tempRoot) {
                setContentView(inflated);
            }
        } catch (Throwable t) {
            android.util.Log.w("Window", "setContentView(int 0x"
                    + Integer.toHexString(layoutResID) + ") failed: "
                    + t.getClass().getName() + ": " + t.getMessage());
        }
    }

    public void setContentView(View view) {
        setContentView(view, null);
    }

    /**
     * Object-typed overload used by Activity.java's
     * `setContentView(Object, Object)`. Coerces params into
     * ViewGroup.LayoutParams when possible.
     */
    public void setContentView(View view, Object params) {
        setContentView(view, params instanceof ViewGroup.LayoutParams
                ? (ViewGroup.LayoutParams) params
                : null);
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mContentView = view;
        if (view == null) {
            return;
        }
        if (mContext != null && view.mContext == null) {
            view.mContext = mContext;
        }
        if (params != null) {
            view.setLayoutParams(params);
        }
        try {
            if (mDecorView instanceof ViewGroup && mDecorView != view) {
                ViewGroup decor = (ViewGroup) mDecorView;
                decor.removeAllViews();
                if (params == null && decor instanceof android.widget.FrameLayout) {
                    view.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                }
                decor.addView(view);
            } else {
                mDecorView = view;
            }
        } catch (Throwable ignored) {
            mDecorView = view;
        }
    }

    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (mDecorView instanceof ViewGroup) {
            if (params != null) {
                view.setLayoutParams(params);
            }
            ((ViewGroup) mDecorView).addView(view);
        }
    }

    public View getDecorView() { return mDecorView; }
    public View peekDecorView() { return mDecorView; }

    public LayoutInflater getLayoutInflater() {
        return new LayoutInflater(mContext);
    }

    public <T extends View> T findViewById(int id) {
        if (mDecorView != null) {
            return mDecorView.findViewById(id);
        }
        return null;
    }

    public Object findViewById_legacy(int id) {
        return findViewById(id);
    }

    public void setTheme(int resid) { /* no-op: no chrome */ }

    public void setTitle(CharSequence title) { mTitle = title; }
    public CharSequence getTitle() { return mTitle; }
    public void setTitleColor(int color) { mTitleColor = color; }

    public void setCallback(Callback callback) { mCallback = callback; }
    public void setCallback(Object callback) { /* legacy/erased overload */ }
    public final Callback getCallback() { return mCallback; }

    // requestFeature historically returned boolean in AOSP; some shim
    // callers expect that, so keep boolean.
    public boolean requestFeature(int featureId) {
        mFeatures |= (1 << featureId);
        return true;
    }
    public final void setFeatureInt(int featureId, int value) { /* no-op */ }
    public final void setFeatureDrawableResource(int featureId, int resId) { /* no-op */ }
    public final void setFeatureDrawable(int featureId, Drawable drawable) { /* no-op */ }
    public void setFeatureDrawableAlpha(int featureId, int alpha) { /* no-op */ }
    public void setFeatureDrawableUri(int featureId, Uri uri) { /* no-op */ }
    public boolean hasFeature(int featureId) { return (mFeatures & (1 << featureId)) != 0; }
    public int getFeatures() { return mFeatures; }
    public int getLocalFeatures() { return 0; }
    public int getForcedWindowFlags() { return 0; }
    public static int getDefaultFeatures(Context context) { return 0; }

    public void invalidatePanelMenu(int featureId) { /* no-op */ }

    public void addFlags(int flags) { mFlags |= flags; }
    public void clearFlags(int flags) { mFlags &= ~flags; }
    public void setFlags(int flags, int mask) { mFlags = (mFlags & ~mask) | (flags & mask); }

    public void setStatusBarColor(int color) { mStatusBarColor = color; }
    public int getStatusBarColor() { return mStatusBarColor; }
    public void setNavigationBarColor(int color) { mNavigationBarColor = color; }
    public int getNavigationBarColor() { return mNavigationBarColor; }
    public void setNavigationBarDividerColor(int color) { /* no-op */ }
    public int getNavigationBarDividerColor() { return 0; }
    public void setStatusBarContrastEnforced(boolean ensureContrast) { /* no-op */ }
    public boolean isStatusBarContrastEnforced() { return false; }
    public void setNavigationBarContrastEnforced(boolean ensureContrast) { /* no-op */ }
    public boolean isNavigationBarContrastEnforced() { return false; }

    public WindowManager getWindowManager() {
        // Lazy WindowManagerImpl per Window — V2-Step5 stub; addView is a no-op
        // until M6 surface daemon lands.
        return new WindowManagerImpl(mContext);
    }
    public TypedArray getWindowStyle() { return null; }

    public void setWindowManager(WindowManager wm, IBinder appToken, String appName) { /* no-op */ }
    public void setWindowManager(WindowManager wm, IBinder appToken, String appName, boolean hardwareAccelerated) { /* no-op */ }

    public boolean hasChildren() {
        return mDecorView instanceof ViewGroup
                && ((ViewGroup) mDecorView).getChildCount() > 0;
    }
    public boolean isActive() { return false; }
    public boolean isFloating() { return false; }
    public boolean isWideColorGamut() { return false; }
    public boolean hasSoftInputMode() { return false; }
    public boolean isShortcutKey(int keyCode, KeyEvent event) { return false; }

    public Window getContainer() { return null; }
    public void setContainer(Window container) { /* no-op */ }

    public void makeActive() { /* no-op */ }
    public void onActive() { /* no-op */ }
    public void onConfigurationChanged(Configuration newConfig) { /* no-op */ }

    public void closeAllPanels() { /* no-op */ }
    public void closePanel(int featureId) { /* no-op */ }
    public void openPanel(int featureId, KeyEvent event) { /* no-op */ }
    public void togglePanel(int featureId, KeyEvent event) { /* no-op */ }

    public boolean performContextMenuIdentifierAction(int id, int flags) { return false; }
    public boolean performPanelIdentifierAction(int featureId, int id, int flags) { return false; }
    public boolean performPanelShortcut(int featureId, int keyCode, KeyEvent event, int flags) { return false; }

    public boolean superDispatchKeyEvent(KeyEvent event) { return false; }
    public boolean superDispatchKeyShortcutEvent(KeyEvent event) { return false; }
    public boolean superDispatchTouchEvent(MotionEvent event) { return false; }
    public boolean superDispatchTrackballEvent(MotionEvent event) { return false; }
    public boolean superDispatchGenericMotionEvent(MotionEvent event) { return false; }

    public void takeInputQueue(Object callback) { /* no-op */ }
    public void takeKeyEvents(boolean get) { /* no-op */ }
    public void takeSurface(Object callback) { /* no-op */ }

    public void injectInputEvent(InputEvent event) { /* no-op */ }

    public void setBackgroundDrawable(Drawable drawable) { /* no-op */ }
    public void setBackgroundDrawableResource(int resId) { /* no-op */ }
    public void setChildDrawable(int featureId, Drawable drawable) { /* no-op */ }
    public void setChildInt(int featureId, int value) { /* no-op */ }
    public void setClipToOutline(boolean clipToOutline) { /* no-op */ }
    public void setColorMode(int colorMode) { /* no-op */ }
    public int getColorMode() { return 0; }
    public void setDecorCaptionShade(int decorCaptionShade) { /* no-op */ }
    public void setDecorFitsSystemWindows(boolean decorFitsSystemWindows) { /* no-op */ }
    public void setDefaultWindowFormat(int format) { /* no-op */ }
    public void setDimAmount(float amount) { /* no-op */ }
    public void setElevation(float elevation) { /* no-op */ }
    public void setFormat(int format) { /* no-op */ }
    public void setGravity(int gravity) { /* no-op */ }
    public void setIcon(int resId) { /* no-op */ }
    public void setLogo(int resId) { /* no-op */ }
    public void setLayout(int width, int height) { /* no-op */ }
    public void setLocalFocus(boolean hasFocus, boolean inTouchMode) { /* no-op */ }
    public void setMediaController(MediaController controller) { /* no-op */ }
    public MediaController getMediaController() { return null; }
    public void setPreferMinimalPostProcessing(boolean isPreferred) { /* no-op */ }
    public void setResizingCaptionDrawable(Drawable drawable) { /* no-op */ }
    public void setRestrictedCaptionAreaListener(Object listener) { /* no-op */ }
    public void setSoftInputMode(int mode) { /* no-op */ }
    public void setSustainedPerformanceMode(boolean enable) { /* no-op */ }
    public void setSystemGestureExclusionRects(java.util.List<Object> rects) { /* no-op */ }
    public void setType(int type) { /* no-op */ }
    public void setUiOptions(int uiOptions) { /* no-op */ }
    public void setUiOptions(int uiOptions, int mask) { /* no-op */ }
    public void setVolumeControlStream(int streamType) { /* no-op */ }
    public int getVolumeControlStream() { return 0; }
    public void setWindowAnimations(int resId) { /* no-op */ }

    public void setAttributes(Object attrs) { /* no-op */ }
    public Object getAttributes() { return null; }

    public void setAllowEnterTransitionOverlap(boolean allow) { /* no-op */ }
    public boolean getAllowEnterTransitionOverlap() { return false; }
    public void setAllowReturnTransitionOverlap(boolean allow) { /* no-op */ }
    public boolean getAllowReturnTransitionOverlap() { return false; }

    public void setEnterTransition(Transition transition) { /* no-op */ }
    public Transition getEnterTransition() { return null; }
    public void setExitTransition(Transition transition) { /* no-op */ }
    public Transition getExitTransition() { return null; }
    public void setReenterTransition(Transition transition) { /* no-op */ }
    public Transition getReenterTransition() { return null; }
    public void setReturnTransition(Transition transition) { /* no-op */ }
    public Transition getReturnTransition() { return null; }
    public void setSharedElementEnterTransition(Transition transition) { /* no-op */ }
    public Transition getSharedElementEnterTransition() { return null; }
    public void setSharedElementExitTransition(Transition transition) { /* no-op */ }
    public Transition getSharedElementExitTransition() { return null; }
    public void setSharedElementReenterTransition(Transition transition) { /* no-op */ }
    public Transition getSharedElementReenterTransition() { return null; }
    public void setSharedElementReturnTransition(Transition transition) { /* no-op */ }
    public Transition getSharedElementReturnTransition() { return null; }
    public void setSharedElementsUseOverlay(boolean sharedElementsUseOverlay) { /* no-op */ }
    public boolean getSharedElementsUseOverlay() { return false; }
    public void setTransitionBackgroundFadeDuration(long fadeDurationMillis) { /* no-op */ }
    public long getTransitionBackgroundFadeDuration() { return 0L; }
    public void setTransitionManager(TransitionManager tm) { /* no-op */ }
    public TransitionManager getTransitionManager() { return null; }
    public Scene getContentScene() { return null; }

    public void addOnFrameMetricsAvailableListener(Object listener, Handler handler) { /* no-op */ }
    public void removeOnFrameMetricsAvailableListener(Object listener) { /* no-op */ }

    public void restoreHierarchyState(Bundle savedInstanceState) { /* no-op */ }
    public Bundle saveHierarchyState() { return null; }
}
