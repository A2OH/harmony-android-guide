package android.view;

/**
 * Android-compatible Window shim.
 *
 * Abstract base class for a top-level application window. On OpenHarmony the
 * window is managed by ArkUI; this shim provides the API surface so that
 * Android code referencing Window compiles without modification.
 *
 * Concrete instances are normally created by the Activity machinery via
 * PolicyManager.makeNewWindow() — callers should not construct Window
 * directly. The shim provides a minimal concrete subclass {@link Impl} for
 * testing purposes.
 */
public abstract class Window {

    // ── Feature constants ──

    /** Request feature: disable the title bar. */
    public static final int FEATURE_NO_TITLE = 1;
    /** Request feature: the action bar. */
    public static final int FEATURE_ACTION_BAR = 8;
    /** Request feature: action bar with overlay mode. */
    public static final int FEATURE_ACTION_BAR_OVERLAY = 9;
    /** Request feature: custom title. */
    public static final int FEATURE_CUSTOM_TITLE = 7;
    /** Request feature: indeterminate progress spinner in title. */
    public static final int FEATURE_INDETERMINATE_PROGRESS = 5;

    // ── Flag constants ──

    public static final int FLAG_FULLSCREEN            = 0x00000400;
    public static final int FLAG_FORCE_NOT_FULLSCREEN  = 0x00000800;
    public static final int FLAG_KEEP_SCREEN_ON        = 0x00000080;
    public static final int FLAG_ALLOW_LOCK_WHILE_SCREEN_ON = 0x00000001;
    public static final int FLAG_SHOW_WHEN_LOCKED      = 0x00080000;
    public static final int FLAG_TRANSLUCENT_STATUS    = 0x04000000;
    public static final int FLAG_TRANSLUCENT_NAVIGATION = 0x08000000;
    public static final int FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS = 0x80000000;

    // ── Callback interface ──

    /**
     * API from a Window back to its caller. This allows the client to
     * intercept key dispatching, panels and menus, as well as be informed
     * of changes in the window.
     */
    public interface Callback {
        /**
         * Called to process key events.
         * @return true if the event was consumed
         */
        boolean dispatchKeyEvent(KeyEvent event);

        /**
         * Called to process touch screen events.
         * @return true if the event was consumed
         */
        boolean dispatchTouchEvent(MotionEvent event);
    }

    // ── State ──

    private Callback mCallback;
    private CharSequence mTitle = "";
    private int mFlags = 0;
    private int mStatusBarColor = 0xFF000000;
    private int mNavigationBarColor = 0xFF000000;
    private WindowManager.LayoutParams mWindowAttributes = new WindowManager.LayoutParams();

    // ── Abstract methods ──

    /**
     * Returns the top-level window decor view (the outermost View of the window),
     * which contains the standard window frame/decorations and the client's content
     * inside of that.
     */
    public abstract View getDecorView();

    /**
     * Convenience for {@link #setContentView(View, ViewGroup.LayoutParams)}
     * to set the screen content from a layout resource.
     */
    public abstract void setContentView(int layoutResID);

    /**
     * Convenience for {@link #setContentView(View, ViewGroup.LayoutParams)}
     * using the default layout params.
     */
    public abstract void setContentView(View view);

    /**
     * Set the screen content to an explicit view.
     */
    public abstract void setContentView(View view, ViewGroup.LayoutParams params);

    // ── Concrete methods ──

    /** Set the title of the window. */
    public void setTitle(CharSequence title) {
        mTitle = title != null ? title : "";
    }

    /** Get the window title. */
    public CharSequence getTitle() {
        return mTitle;
    }

    /**
     * Set the flags of the window, as per the
     * {@link WindowManager.LayoutParams WindowManager.LayoutParams} flags.
     */
    public void setFlags(int flags, int mask) {
        mFlags = (mFlags & ~mask) | (flags & mask);
        mWindowAttributes.flags = mFlags;
    }

    /**
     * Convenience method to set flags with a full mask.
     */
    public void addFlags(int flags) {
        setFlags(flags, flags);
    }

    /**
     * Clear the specified flags of the window.
     */
    public void clearFlags(int flags) {
        setFlags(0, flags);
    }

    /** @return current window flags */
    public int getFlags() {
        return mFlags;
    }

    /**
     * Sets the color of the status bar. Only applies when
     * {@link #FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS} is set and
     * {@link #FLAG_TRANSLUCENT_STATUS} is not set.
     */
    public void setStatusBarColor(int color) {
        mStatusBarColor = color;
    }

    /** @return the current status bar color */
    public int getStatusBarColor() {
        return mStatusBarColor;
    }

    /**
     * Sets the color of the navigation bar. Only applies when
     * {@link #FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS} is set and
     * {@link #FLAG_TRANSLUCENT_NAVIGATION} is not set.
     */
    public void setNavigationBarColor(int color) {
        mNavigationBarColor = color;
    }

    /** @return the current navigation bar color */
    public int getNavigationBarColor() {
        return mNavigationBarColor;
    }

    /**
     * Returns the {@link WindowManager.LayoutParams} for this window.
     * The window attributes are always mutable; callers may change individual
     * fields and then call {@link WindowManager#updateViewLayout} to apply them.
     */
    public WindowManager.LayoutParams getAttributes() {
        return mWindowAttributes;
    }

    /** Set the Callback interface for this window, used to intercept key and touch events. */
    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    /** @return the current Callback, or null if none set */
    public Callback getCallback() {
        return mCallback;
    }

    /**
     * Enable a window feature. Feature constants are FEATURE_*.
     * @return true if the feature is now enabled
     */
    public boolean requestFeature(int featureId) {
        // stub — always succeeds
        return true;
    }

    // ── Minimal concrete implementation (for shim/test use) ──

    /**
     * Minimal no-op Window implementation returned by the shim Activity
     * when no real ArkUI window is available.
     */
    public static class Impl extends Window {
        private View mDecorView;

        public Impl() {
            // create a stub decor view
            mDecorView = new View() {};
        }

        @Override
        public View getDecorView() {
            return mDecorView;
        }

        @Override
        public void setContentView(int layoutResID) {
            // no-op stub
        }

        @Override
        public void setContentView(View view) {
            // no-op stub
        }

        @Override
        public void setContentView(View view, ViewGroup.LayoutParams params) {
            // no-op stub
        }
    }
}
