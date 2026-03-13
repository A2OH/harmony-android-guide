package android.support.v4.widget;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import java.util.Set;

import android.view.View;
import android.widget.FrameLayout;

/**
 * Shim: android.support.v4.widget.NestedScrollView
 *
 * NestedScrollView is just like ScrollView, but it supports acting as both a
 * nested scrolling parent and child on both new and old versions of Android.
 * This shim extends FrameLayout and stubs all scroll behaviour.
 */
public class NestedScrollView extends FrameLayout {

    private OnScrollChangeListener mOnScrollChangeListener;

    public NestedScrollView() {
        super();
    }

    public NestedScrollView(Object context) {
        super();
    }

    public NestedScrollView(Object context, Object attrs) {
        super();
    }

    public NestedScrollView(Object context, Object attrs, int defStyleAttr) {
        super();
    }

    // ── Scroll operations ──

    /**
     * Like View.scrollTo, but scroll smoothly instead of immediately.
     *
     * @param x the position where to scroll on the X axis
     * @param y the position where to scroll on the Y axis
     */
    public final void smoothScrollTo(int x, int y) {
        // Stub — no native scroll implementation in shim
    }

    /**
     * Handles scrolling in response to a "full scroll" which is sent when
     * the user performs a keyboard action to scroll all the way to the
     * beginning or end of the view.
     *
     * @param direction the direction to scroll. FOCUS_UP to scroll to the top,
     *                  FOCUS_DOWN to scroll to the bottom
     * @return true if the scroll was handled, false otherwise
     */
    public boolean fullScroll(int direction) {
        // Stub
        return false;
    }

    // ── Scroll change listener ──

    /**
     * Register a callback to be invoked when the scroll position of this view
     * changes.
     *
     * @param l the scroll change listener
     */
    public void setOnScrollChangeListener(OnScrollChangeListener l) {
        this.mOnScrollChangeListener = l;
    }

    // ── Nested scrolling ──

    /**
     * Set whether or not this view should act as a nested scrolling parent.
     *
     * @param enabled whether to enable nested scrolling
     */
    public void setNestedScrollingEnabled(boolean enabled) {
        // Stub
    }

    /**
     * Returns true if nested scrolling is enabled for this view.
     *
     * @return whether nested scrolling is enabled
     */
    public boolean isNestedScrollingEnabled() {
        return false;
    }

    // ── OnScrollChangeListener interface ──

    /**
     * Interface definition for a callback to be invoked when the scroll X or Y
     * position of a view changes.
     */
    public interface OnScrollChangeListener {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param v          the view whose scroll position has changed
         * @param scrollX    current horizontal scroll origin
         * @param scrollY    current vertical scroll origin
         * @param oldScrollX previous horizontal scroll origin
         * @param oldScrollY previous vertical scroll origin
         */
        void onScrollChange(NestedScrollView v, int scrollX, int scrollY,
                            int oldScrollX, int oldScrollY);
    }
}
