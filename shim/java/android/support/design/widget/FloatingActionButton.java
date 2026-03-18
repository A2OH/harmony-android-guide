package android.support.design.widget;
import android.opengl.Visibility;
import android.util.Size;
import android.view.View;
import android.widget.ImageButton;
import android.opengl.Visibility;
import android.util.Size;
import android.view.View;
import android.widget.ImageButton;

import android.view.View;
import android.widget.ImageButton;

/**
 * Shim: android.support.design.widget.FloatingActionButton
 *
 * Floating action buttons are used for a special type of promoted action.
 * They are distinguished by a circled icon floating above the UI and have
 * special motion behaviors related to morphing, launching, and the transferring
 * anchor point.
 *
 * All animation and elevation are stubbed.
 */
public class FloatingActionButton extends ImageButton {

    // ── Size constants ──

    /** Size which will change based on the window size. */
    public static final int SIZE_AUTO   = -1;
    /** The normal sized button. */
    public static final int SIZE_NORMAL =  0;
    /** The mini sized button. */
    public static final int SIZE_MINI   =  1;

    private int mSize = SIZE_NORMAL;
    private boolean mShown = true;
    private float mCompatElevation = 0f;

    public FloatingActionButton() {
        super(new android.content.Context());
    }

    public FloatingActionButton(android.content.Context context) {
        super(context);
    }

    public FloatingActionButton(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingActionButton(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // ── Visibility control ──

    /**
     * Shows the button. This method will animate the button show if the view
     * has already been laid out.
     */
    public void show() {
        show(null);
    }

    /**
     * Shows the button with a listener for visibility change.
     *
     * @param listener optional listener to be called when the FAB is shown
     */
    public void show(OnVisibilityChangedListener listener) {
        mShown = true;
        setVisibility(View.VISIBLE);
        if (listener != null) {
            listener.onShown(this);
        }
    }

    /**
     * Hides the button. This method will animate the button hide if the view
     * has already been laid out.
     */
    public void hide() {
        hide(null);
    }

    /**
     * Hides the button with a listener for visibility change.
     *
     * @param listener optional listener to be called when the FAB is hidden
     */
    public void hide(OnVisibilityChangedListener listener) {
        mShown = false;
        setVisibility(View.GONE);
        if (listener != null) {
            listener.onHidden(this);
        }
    }

    // ── Size ──

    /**
     * Sets the size of the button to one of SIZE_NORMAL, SIZE_MINI, or SIZE_AUTO.
     *
     * @param size the size to set
     */
    public void setSize(int size) {
        this.mSize = size;
    }

    /**
     * Returns the current size of the button.
     *
     * @return one of SIZE_NORMAL, SIZE_MINI, or SIZE_AUTO
     */
    public int getSize() {
        return mSize;
    }

    // ── Colors ──

    /**
     * Updates the ripple color for this button.
     *
     * @param color ARGB color to use for the ripple
     */
    public void setRippleColor(int color) {
        // Stub — no native ripple in shim
    }

    // ── Elevation ──

    /**
     * Updates the backward compatible elevation of the FloatingActionButton.
     *
     * @param elevation the backward compatible elevation in pixels
     */
    public void setCompatElevation(float elevation) {
        this.mCompatElevation = elevation;
    }

    /**
     * Returns the backward compatible elevation of the FloatingActionButton.
     *
     * @return the backward compatible elevation in pixels
     */
    public float getCompatElevation() {
        return mCompatElevation;
    }

    // ── OnVisibilityChangedListener inner class ──

    /**
     * Object to be invoked when the visibility of a FloatingActionButton changes.
     */
    public static class OnVisibilityChangedListener {

        /**
         * Called when a FloatingActionButton has been shown.
         *
         * @param fab the FloatingActionButton that was shown
         */
        public void onShown(FloatingActionButton fab) {}

        /**
         * Called when a FloatingActionButton has been hidden.
         *
         * @param fab the FloatingActionButton that was hidden
         */
        public void onHidden(FloatingActionButton fab) {}
    }
}
