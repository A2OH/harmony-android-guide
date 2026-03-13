package android.widget;

import android.view.Menu;
import android.view.MenuItem;
import android.view.SimpleMenu;
import android.view.View;

/**
 * Shim: android.widget.PopupMenu → context-menu overlay anchored to a View.
 *
 * ArkUI does not expose a popup-menu primitive through the C node API.
 * This shim stores the menu items in a {@link SimpleMenu} and calls the
 * caller-supplied listeners when items are selected. Actual display is
 * stubbed until an ArkUI overlay implementation is wired.
 */
public class PopupMenu {

    private final View mAnchor;
    private final Menu mMenu;
    private OnMenuItemClickListener mItemClickListener;
    private OnDismissListener mDismissListener;
    private boolean mShowing = false;

    /** Create a PopupMenu anchored to the given view. */
    public PopupMenu(Object context, View anchor) {
        mAnchor = anchor;
        mMenu = new SimpleMenu();
    }

    /** Create a PopupMenu anchored to the given view with a gravity hint. */
    public PopupMenu(Object context, View anchor, int gravity) {
        mAnchor = anchor;
        mMenu = new SimpleMenu();
    }

    // ── Menu access ──

    /** Return the Menu that will be displayed when this popup is shown. */
    public Menu getMenu() {
        return mMenu;
    }

    /**
     * Inflate a menu resource into this popup's {@link Menu}.
     * Resource inflation is not supported in the shim; this is a no-op.
     */
    public void inflate(int menuRes) {
        // Resource-based menu inflation not yet implemented
    }

    // ── Listeners ──

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setOnDismissListener(OnDismissListener listener) {
        mDismissListener = listener;
    }

    // ── Show / dismiss ──

    /**
     * Show the popup menu. In the real implementation this inflates an
     * ArkUI popup; in the shim it sets the showing flag and could be
     * extended to display a native overlay.
     */
    public void show() {
        mShowing = true;
        // ArkUI overlay display not yet wired — stub for compilation
    }

    /** Dismiss the popup menu and fire the dismiss listener. */
    public void dismiss() {
        if (!mShowing) return;
        mShowing = false;
        if (mDismissListener != null) {
            mDismissListener.onDismiss(this);
        }
    }

    /** Return true if the popup is currently visible. */
    public boolean isShowing() {
        return mShowing;
    }

    /**
     * Simulate a click on the menu item with the given ID.
     * Used for testing and programmatic selection.
     */
    public boolean performItemClick(int itemId) {
        MenuItem item = mMenu.findItem(itemId);
        if (item != null && mItemClickListener != null) {
            return mItemClickListener.onMenuItemClick(item);
        }
        return false;
    }

    // ── Listener interfaces ──

    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem item);
    }

    public interface OnDismissListener {
        void onDismiss(PopupMenu menu);
    }
}
