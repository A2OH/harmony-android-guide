package android.widget;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuItem;

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

    public ActionMenuView() { super(new android.content.Context());
    }

    public ActionMenuView(Object context) { super(new android.content.Context());
    }

    /**
     * Returns the Menu associated with this view.
     */
    public android.view.Menu getMenu() {
        return null;
    }

    /**
     * Hides all items not currently showing and shows the overflow menu if
     * there are items to display.
     */
    public boolean showOverflowMenu() {
        // no-op stub
        return false;
    }

    /**
     * Hides the overflow menu if it is currently showing.
     */
    public boolean hideOverflowMenu() {
        // no-op stub
        return false;
    }

    /**
     * Returns {@code true} if the overflow menu is currently showing.
     */
    public boolean isOverflowMenuShowing() {
        return false;
    }

    public boolean isOverflowMenuShowPending() { return false; }
    public boolean isOverflowReserved() { return false; }
    public void dismissPopupMenus() {}
    public void setExpandedActionViewsExclusive(boolean exclusive) {}
    public void setPopupTheme(int resId) {}
    public com.android.internal.view.menu.MenuBuilder peekMenu() { return null; }
    public android.graphics.drawable.Drawable getOverflowIcon() { return null; }
    public void setOverflowIcon(android.graphics.drawable.Drawable icon) {}
    public void setMenuCallbacks(com.android.internal.view.menu.MenuPresenter.Callback pcb,
                                  com.android.internal.view.menu.MenuBuilder.Callback mcb) {}
    public void setPresenter(ActionMenuPresenter presenter) {}

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
