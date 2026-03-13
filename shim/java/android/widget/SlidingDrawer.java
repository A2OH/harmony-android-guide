package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewGroup;

import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.widget.SlidingDrawer — deprecated sliding drawer widget.
 *
 * {@code SlidingDrawer} hides content out of the screen and allows the user
 * to drag a handle to bring the content on screen. This class was deprecated
 * in API level 17. This no-op shim compiles cleanly and returns sensible
 * defaults; all animation and interaction methods are no-ops.
 */
@Deprecated
public class SlidingDrawer extends ViewGroup {

    /** Drawer orientation constants (mirror the real API). */
    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL   = 1;

    /**
     * Object interface for drawer open/close events.
     */
    public interface OnDrawerOpenListener {
        void onDrawerOpened();
    }

    public interface OnDrawerCloseListener {
        void onDrawerClosed();
    }

    public interface OnDrawerScrollListener {
        void onScrollStarted();
        void onScrollEnded();
    }

    private boolean opened = false;
    private int     orientation = ORIENTATION_VERTICAL;

    private OnDrawerOpenListener   onDrawerOpenListener;
    private OnDrawerCloseListener  onDrawerCloseListener;
    private OnDrawerScrollListener onDrawerScrollListener;

    public SlidingDrawer() {
        super();
    }

    // ── Open / close ──

    /** Returns true if the drawer is fully open. */
    public boolean isOpened() { return opened; }

    /** Returns true if the drawer is fully closed (not open). */
    public boolean isMoving() { return false; }

    /**
     * Open the drawer immediately, without animation.
     */
    public void open() {
        opened = true;
        if (onDrawerOpenListener != null) {
            onDrawerOpenListener.onDrawerOpened();
        }
    }

    /**
     * Close the drawer immediately, without animation.
     */
    public void close() {
        opened = false;
        if (onDrawerCloseListener != null) {
            onDrawerCloseListener.onDrawerClosed();
        }
    }

    /**
     * Open the drawer with animation. No-op in this shim (delegates to open()).
     */
    public void animateOpen() {
        open();
    }

    /**
     * Close the drawer with animation. No-op in this shim (delegates to close()).
     */
    public void animateClose() {
        close();
    }

    /**
     * Toggle drawer open/closed with animation.
     */
    public void animateToggle() {
        if (opened) animateClose(); else animateOpen();
    }

    /**
     * Toggle drawer open/closed immediately.
     */
    public void toggle() {
        if (opened) close(); else open();
    }

    // ── Handle / content ──

    /**
     * Returns the handle View. Always null in this shim.
     */
    public View getHandle() { return null; }

    /**
     * Returns the content View. Always null in this shim.
     */
    public View getContent() { return null; }

    /** Lock the drawer in its current position. No-op in this shim. */
    public void lock()   {}
    public void unlock() {}

    // ── Orientation ──

    public int  getOrientation()             { return orientation; }
    public void setOrientation(int orientation) { this.orientation = orientation; }

    // ── Listeners ──

    public void setOnDrawerOpenListener(OnDrawerOpenListener listener) {
        this.onDrawerOpenListener = listener;
    }

    public void setOnDrawerCloseListener(OnDrawerCloseListener listener) {
        this.onDrawerCloseListener = listener;
    }

    public void setOnDrawerScrollListener(OnDrawerScrollListener listener) {
        this.onDrawerScrollListener = listener;
    }
}
