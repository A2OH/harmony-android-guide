package android.widget;

import android.view.View;

/**
 * Shim: android.widget.PopupWindow → floating overlay view.
 *
 * ArkUI does not expose a direct popup / floating window API through the
 * C node API used by this shim layer. This class therefore maintains the
 * popup state in Java and delegates show/dismiss to a no-op stub that
 * can be wired to a custom native overlay implementation when available.
 *
 * All API surface is preserved so that app code compiles without changes.
 */
public class PopupWindow {

    private View contentView;
    private int width  = -2; // ViewGroup.LayoutParams.WRAP_CONTENT
    private int height = -2; // ViewGroup.LayoutParams.WRAP_CONTENT
    private boolean focusable = false;
    private boolean outsideTouchable = false;
    private boolean showing = false;

    /** Default constructor. */
    public PopupWindow() {}

    /** Constructor with content view. */
    public PopupWindow(View contentView) {
        this.contentView = contentView;
    }

    /** Constructor with content view and size. */
    public PopupWindow(View contentView, int width, int height) {
        this.contentView = contentView;
        this.width = width;
        this.height = height;
    }

    /** Constructor with content view, size and focusable flag. */
    public PopupWindow(View contentView, int width, int height, boolean focusable) {
        this.contentView = contentView;
        this.width = width;
        this.height = height;
        this.focusable = focusable;
    }

    // ── Content ──

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    // ── Dimensions ──

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    // ── Touch / focus behaviour ──

    public boolean isFocusable() {
        return focusable;
    }

    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    public boolean isOutsideTouchable() {
        return outsideTouchable;
    }

    public void setOutsideTouchable(boolean outsideTouchable) {
        this.outsideTouchable = outsideTouchable;
    }

    // ── Show / dismiss ──

    /**
     * Show the popup at a fixed position relative to the parent view.
     *
     * @param parent  the anchor view (used for context in real Android)
     * @param gravity Gravity constant for placement
     * @param x       horizontal offset in pixels
     * @param y       vertical offset in pixels
     */
    public void showAtLocation(View parent, int gravity, int x, int y) {
        showing = true;
        // ArkUI overlay display not yet wired — stub for compilation
    }

    /**
     * Show the popup in a drop-down fashion below the anchor view.
     *
     * @param anchor the view that the popup should be anchored to
     */
    public void showAsDropDown(View anchor) {
        showing = true;
        // ArkUI overlay display not yet wired — stub for compilation
    }

    /**
     * Show the popup in a drop-down fashion below the anchor view,
     * with an (xoff, yoff) pixel offset.
     */
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        showing = true;
    }

    /**
     * Show the popup in a drop-down fashion below the anchor view,
     * with an offset and a gravity hint.
     */
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        showing = true;
    }

    /** Dismiss the popup window. */
    public void dismiss() {
        showing = false;
        // Tear down ArkUI overlay node when wired
    }

    /** Return true if the popup is currently showing. */
    public boolean isShowing() {
        return showing;
    }

    // ── Dismiss listener ──

    public interface OnDismissListener {
        void onDismiss();
    }

    private OnDismissListener onDismissListener;

    public void setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
    }
}
