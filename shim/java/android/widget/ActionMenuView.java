package android.widget;

import android.view.MenuItem;

/**
 * Android-compatible ActionMenuView shim.
 *
 * A LinearLayout that displays action-menu items as a row of buttons. Typically
 * used inside a Toolbar. Menu item handling is stubbed out; callers receive a
 * placeholder Object from {@link #getMenu()}.
 */
public class ActionMenuView extends LinearLayout {

    private OnMenuItemClickListener mOnMenuItemClickListener;

    public ActionMenuView() {
        super();
    }

    public ActionMenuView(Object context) {
        super();
    }

    /**
     * Returns the Menu associated with this view. In this shim the returned
     * object is always {@code null}; cast to {@code android.view.Menu} in
     * production code guarded by a null-check.
     */
    public Object getMenu() {
        return null;
    }

    /**
     * Hides all items not currently showing and shows the overflow menu if
     * there are items to display.
     */
    public void showOverflowMenu() {
        // no-op stub
    }

    /**
     * Hides the overflow menu if it is currently showing.
     */
    public void hideOverflowMenu() {
        // no-op stub
    }

    /**
     * Returns {@code true} if the overflow menu is currently showing.
     */
    public boolean isOverflowMenuShowing() {
        return false;
    }

    /**
     * Registers a callback for menu-item click events.
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.mOnMenuItemClickListener = listener;
    }

    public OnMenuItemClickListener getOnMenuItemClickListener() {
        return mOnMenuItemClickListener;
    }

    // -------------------------------------------------------------------------
    // Inner interface
    // -------------------------------------------------------------------------

    /**
     * Interface responsible for receiving menu-item click events from an
     * ActionMenuView.
     */
    public interface OnMenuItemClickListener {
        /**
         * Called when a menu item is clicked.
         *
         * @param item the item that was clicked
         * @return {@code true} if the event was handled, {@code false} to
         *         allow normal menu processing to proceed
         */
        boolean onMenuItemClick(MenuItem item);
    }
}
