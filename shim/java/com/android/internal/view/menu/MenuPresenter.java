package com.android.internal.view.menu;

import android.view.ViewGroup;

/** Stub for AOSP MenuPresenter interface. */
public interface MenuPresenter {

    public interface Callback {
        void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing);
        boolean onOpenSubMenu(MenuBuilder menu);
    }

    void initForMenu(android.content.Context context, MenuBuilder menu);
    MenuView getMenuView(ViewGroup root);
    void updateMenuView(boolean cleared);
    void setCallback(Callback cb);
    boolean onSubMenuSelected(SubMenuBuilder subMenu);
    void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing);
    boolean flagActionItems();
    boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item);
    boolean collapseItemActionView(MenuBuilder menu, MenuItemImpl item);
    int getId();
    android.os.Parcelable onSaveInstanceState();
    void onRestoreInstanceState(android.os.Parcelable state);
}
