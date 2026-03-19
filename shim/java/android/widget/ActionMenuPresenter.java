package android.widget;

import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuItemImpl;
import com.android.internal.view.menu.MenuPresenter;
import com.android.internal.view.menu.MenuView;
import com.android.internal.view.menu.SubMenuBuilder;
import android.content.Context;
import android.view.ViewGroup;

/** Stub for ActionMenuPresenter in android.widget package for Toolbar. */
public class ActionMenuPresenter implements MenuPresenter {
    public ActionMenuPresenter() {}
    public ActionMenuPresenter(Context context) {}

    public void initForMenu(Context context, MenuBuilder menu) {}
    public MenuView getMenuView(ViewGroup root) { return null; }
    public void updateMenuView(boolean cleared) {}
    public void setCallback(MenuPresenter.Callback cb) {}
    public boolean onSubMenuSelected(SubMenuBuilder subMenu) { return false; }
    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {}
    public boolean flagActionItems() { return false; }
    public boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item) { return false; }
    public boolean collapseItemActionView(MenuBuilder menu, MenuItemImpl item) { return false; }
    public int getId() { return 0; }
    public void setExpandedActionViewsExclusive(boolean exclusive) {}
    public android.os.Parcelable onSaveInstanceState() { return null; }
    public void onRestoreInstanceState(android.os.Parcelable state) {}
}
