package android.support.design.widget;

import android.view.View;

/**
 * Shim: android.support.design.widget.Snackbar
 *
 * Snackbars provide lightweight feedback about an operation. They show a brief
 * message at the bottom of the screen on mobile and lower left on larger devices.
 * Snackbars appear above all other elements on screen and only one can be
 * displayed at a time.
 *
 * All animation and display is stubbed.
 */
public final class Snackbar {

    // ── Duration constants ──

    /** Show the Snackbar indefinitely. */
    public static final int LENGTH_INDEFINITE = -2;
    /** Show the Snackbar for a short period of time. */
    public static final int LENGTH_SHORT      = -1;
    /** Show the Snackbar for a long period of time. */
    public static final int LENGTH_LONG       =  0;

    private CharSequence mText;
    private int mDuration;
    private CharSequence mActionText;
    private View.OnClickListener mActionListener;
    private boolean mShown = false;
    private Callback mCallback;

    // Private constructor — use make()
    private Snackbar(View view, CharSequence text, int duration) {
        this.mText = text;
        this.mDuration = duration;
    }

    // ── Factory method ──

    /**
     * Make a Snackbar to display a message.
     *
     * Having a CoordinatorLayout in the view hierarchy allows Snackbar to enable
     * certain features, such as swipe-to-dismiss and automatically moving widgets.
     *
     * @param view     the view to find a parent from
     * @param text     the text to show, or the text resource to show
     * @param duration how long to display the message: LENGTH_SHORT, LENGTH_LONG, or LENGTH_INDEFINITE
     * @return the created Snackbar
     */
    public static Snackbar make(View view, CharSequence text, int duration) {
        return new Snackbar(view, text, duration);
    }

    /**
     * Make a Snackbar to display a message.
     *
     * @param view     the view to find a parent from
     * @param resId    string resource to show
     * @param duration how long to display the message
     * @return the created Snackbar
     */
    public static Snackbar make(View view, int resId, int duration) {
        return new Snackbar(view, "string_" + resId, duration);
    }

    // ── Configuration ──

    /**
     * Set the action to be displayed in this Snackbar.
     *
     * @param text     text to display for the action
     * @param listener callback to be invoked when the action is clicked
     * @return this Snackbar
     */
    public Snackbar setAction(CharSequence text, View.OnClickListener listener) {
        this.mActionText = text;
        this.mActionListener = listener;
        return this;
    }

    /**
     * Set the action to be displayed in this Snackbar.
     *
     * @param resId    string resource to display for the action
     * @param listener callback to be invoked when the action is clicked
     * @return this Snackbar
     */
    public Snackbar setAction(int resId, View.OnClickListener listener) {
        return setAction("string_" + resId, listener);
    }

    /**
     * Update the text in this Snackbar.
     *
     * @param message the new text for this view
     * @return this Snackbar
     */
    public Snackbar setText(CharSequence message) {
        this.mText = message;
        return this;
    }

    /**
     * Set the duration to show the snackbar for.
     *
     * @param duration one of LENGTH_SHORT, LENGTH_LONG, or LENGTH_INDEFINITE
     * @return this Snackbar
     */
    public Snackbar setDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    /**
     * Return the duration of the snackbar.
     *
     * @return the duration in one of LENGTH_SHORT, LENGTH_LONG, or LENGTH_INDEFINITE
     */
    public int getDuration() {
        return mDuration;
    }

    /**
     * Sets the text color of the action specified in setAction(CharSequence, View.OnClickListener).
     *
     * @param color the ARGB color to use for the action text
     * @return this Snackbar
     */
    public Snackbar setActionTextColor(int color) {
        return this;
    }

    /**
     * Set a callback to be called when this Snackbar instance is shown or dismissed.
     *
     * @param callback callback to be called when transient bottom bar events occur
     * @return this Snackbar
     */
    public Snackbar addCallback(Callback callback) {
        this.mCallback = callback;
        return this;
    }

    // ── Show / dismiss ──

    /**
     * Show the Snackbar.
     */
    public void show() {
        mShown = true;
        System.out.println("[Snackbar] show: " + mText);
        if (mCallback != null) {
            mCallback.onShown(this);
        }
    }

    /**
     * Dismiss the Snackbar.
     */
    public void dismiss() {
        if (mShown) {
            mShown = false;
            if (mCallback != null) {
                mCallback.onDismissed(this, Callback.DISMISS_EVENT_MANUAL);
            }
        }
    }

    /**
     * Return whether this Snackbar is currently being shown.
     *
     * @return true if the view is shown
     */
    public boolean isShown() {
        return mShown;
    }

    // ── Callback abstract inner class ──

    /**
     * Callback class for Snackbar.onShown and onDismissed events.
     */
    public static abstract class Callback {

        /** Indicates that the Snackbar was dismissed via a swipe. */
        public static final int DISMISS_EVENT_SWIPE     = 0;
        /** Indicates that the Snackbar was dismissed via an action click. */
        public static final int DISMISS_EVENT_ACTION    = 1;
        /** Indicates that the Snackbar was dismissed via a timeout. */
        public static final int DISMISS_EVENT_TIMEOUT   = 2;
        /** Indicates that the Snackbar was dismissed via a call to dismiss(). */
        public static final int DISMISS_EVENT_MANUAL    = 3;
        /** Indicates that the Snackbar was dismissed from a new Snackbar being shown. */
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;

        /**
         * Called when the given Snackbar is visible.
         *
         * @param snackbar the snackbar which is now visible
         */
        public void onShown(Snackbar snackbar) {}

        /**
         * Called when the given Snackbar has been dismissed, either through a time-out,
         * having been manually dismissed, or an action being clicked.
         *
         * @param snackbar the snackbar which has been dismissed
         * @param event    the event which caused the dismissal
         */
        public void onDismissed(Snackbar snackbar, int event) {}
    }
}
