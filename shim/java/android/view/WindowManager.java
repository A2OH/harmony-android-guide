package android.view;

import android.view.Gravity;

/**
 * Shim: android.view.WindowManager — interface for creating and managing windows.
 *
 * On OpenHarmony the window system is managed by ArkUI; this shim provides
 * the WindowManager interface so that Android code that obtains a WindowManager
 * from Context.getSystemService(Context.WINDOW_SERVICE) and inspects display
 * metrics or creates system overlays can compile without changes.
 *
 * Overlay windows (LayoutParams.TYPE_APPLICATION_OVERLAY etc.) are not
 * supported by this shim — addView/removeView are no-ops.
 *
 * The inner LayoutParams class is fully populated for compilation compatibility.
 */
public interface WindowManager {

    /**
     * Returns the default display associated with this window manager.
     * On OpenHarmony this is the primary physical screen.
     */
    Display getDefaultDisplay();

    /**
     * Add a view to the window manager.
     * No-op in this shim — use ArkUI's native node APIs for overlay windows.
     */
    void addView(View view, LayoutParams params);

    /**
     * Update the layout parameters of a view previously added via addView.
     * No-op in this shim.
     */
    void updateViewLayout(View view, LayoutParams params);

    /**
     * Remove a view from the window manager.
     * No-op in this shim.
     */
    void removeView(View view);

    // ── LayoutParams ──

    /**
     * WindowManager.LayoutParams — parameters for a top-level window.
     *
     * All fields are public to match the real Android API. Default values
     * correspond to a standard application window.
     */
    class LayoutParams extends ViewGroup.LayoutParams {

        // ── Window types ──
        public static final int TYPE_APPLICATION            = 2;
        public static final int TYPE_APPLICATION_PANEL      = 1000;
        public static final int TYPE_APPLICATION_SUB_PANEL  = 1002;
        public static final int TYPE_APPLICATION_ATTACHED_DIALOG = 1003;
        public static final int TYPE_APPLICATION_OVERLAY    = 2038;
        public static final int TYPE_STATUS_BAR             = 2000;
        public static final int TYPE_TOAST                  = 2005;
        public static final int TYPE_SYSTEM_OVERLAY         = 2006;
        public static final int TYPE_SYSTEM_ALERT           = 2003;
        public static final int LAST_APPLICATION_WINDOW     = 1999;
        public static final int FIRST_SYSTEM_WINDOW         = 2000;

        // ── Flags ──
        public static final int FLAG_ALLOW_LOCK_WHILE_SCREEN_ON   = 0x00000001;
        public static final int FLAG_DIM_BEHIND                   = 0x00000002;
        public static final int FLAG_BLUR_BEHIND                   = 0x00000004;
        public static final int FLAG_NOT_FOCUSABLE                 = 0x00000008;
        public static final int FLAG_NOT_TOUCHABLE                 = 0x00000010;
        public static final int FLAG_NOT_TOUCH_MODAL               = 0x00000020;
        public static final int FLAG_KEEP_SCREEN_ON                = 0x00000080;
        public static final int FLAG_LAYOUT_IN_SCREEN              = 0x00000100;
        public static final int FLAG_FULLSCREEN                    = 0x00000400;
        public static final int FLAG_FORCE_NOT_FULLSCREEN          = 0x00000800;
        public static final int FLAG_SECURE                        = 0x00002000;
        public static final int FLAG_SCALED                        = 0x00004000;
        public static final int FLAG_SHOW_WHEN_LOCKED              = 0x00080000;
        public static final int FLAG_DISMISS_KEYGUARD              = 0x00400000;
        public static final int FLAG_TRANSLUCENT_STATUS            = 0x04000000;
        public static final int FLAG_TRANSLUCENT_NAVIGATION        = 0x08000000;

        // ── Soft-input modes ──
        public static final int SOFT_INPUT_STATE_UNSPECIFIED    = 0;
        public static final int SOFT_INPUT_STATE_UNCHANGED      = 1;
        public static final int SOFT_INPUT_STATE_HIDDEN         = 2;
        public static final int SOFT_INPUT_STATE_ALWAYS_HIDDEN  = 3;
        public static final int SOFT_INPUT_STATE_VISIBLE        = 4;
        public static final int SOFT_INPUT_STATE_ALWAYS_VISIBLE = 5;
        public static final int SOFT_INPUT_ADJUST_UNSPECIFIED   = 0x00;
        public static final int SOFT_INPUT_ADJUST_RESIZE        = 0x10;
        public static final int SOFT_INPUT_ADJUST_PAN           = 0x20;
        public static final int SOFT_INPUT_ADJUST_NOTHING       = 0x30;

        // ── Instance fields ──

        /** Window type; defaults to TYPE_APPLICATION. */
        public int  type  = TYPE_APPLICATION;

        /** Window behavioural flags; see FLAG_* constants. */
        public int  flags = 0;

        /** Gravity for window positioning; see {@link Gravity}. */
        public int  gravity = Gravity.NO_GRAVITY;

        /** Horizontal offset in pixels when gravity is set. */
        public int  x = 0;

        /** Vertical offset in pixels when gravity is set. */
        public int  y = 0;

        /** Window title (used for accessibility and debug tools). */
        public CharSequence title = "";

        /** Alpha for the entire window, 0.0 (transparent) to 1.0 (opaque). */
        public float alpha = 1.0f;

        /** Amount of dimming behind the window (0.0 to 1.0). */
        public float dimAmount = 1.0f;

        /** Soft input mode (combination of SOFT_INPUT_STATE_* and SOFT_INPUT_ADJUST_*). */
        public int  softInputMode = SOFT_INPUT_STATE_UNSPECIFIED;

        // ── Constructors ──

        public LayoutParams() {
            super(MATCH_PARENT, MATCH_PARENT);
        }

        public LayoutParams(int type) {
            super(MATCH_PARENT, MATCH_PARENT);
            this.type = type;
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(int w, int h, int type) {
            super(w, h);
            this.type = type;
        }

        public LayoutParams(int w, int h, int type, int flags, int format) {
            super(w, h);
            this.type  = type;
            this.flags = flags;
        }

        // ── Flag helpers ──

        public void setFlags(int flags, int mask) {
            this.flags = (this.flags & ~mask) | (flags & mask);
        }
    }

    // ── Default (no-op) implementation ──

    /**
     * Minimal no-op implementation of WindowManager.
     *
     * Returned by the shim Context.getSystemService() for WINDOW_SERVICE.
     * getDefaultDisplay() returns a mock Display.
     */
    class Impl implements WindowManager {
        @Override
        public Display getDefaultDisplay() {
            return new Display();
        }

        @Override
        public void addView(View view, LayoutParams params) {
            // no-op — overlay windows not supported in shim
        }

        @Override
        public void updateViewLayout(View view, LayoutParams params) {
            // no-op
        }

        @Override
        public void removeView(View view) {
            // no-op
        }
    }
}
