package com.android.internal.view.menu;

import android.content.Context;
import android.view.MenuItem;

/** Stub for AOSP MenuBuilder. */
public class MenuBuilder implements android.view.Menu {
    public MenuBuilder() {}
    public MenuBuilder(Context context) {}

    public interface Callback {
        boolean onMenuItemSelected(MenuBuilder menu, MenuItem item);
        void onMenuModeChange(MenuBuilder menu);
    }

    public void setCurrentMenuInfo(android.view.ContextMenu.ContextMenuInfo info) {}
    public void addMenuPresenter(MenuPresenter presenter, Context context) {}
    public void addMenuPresenter(MenuPresenter presenter) {}
    public void removeMenuPresenter(MenuPresenter presenter) {}
    public MenuItem getItem(int index) { return null; }
    public int size() { return 0; }
    public boolean hasVisibleItems() { return false; }
    public void setCallback(Callback callback) {}
    public void close() {}
    public void clear() {}
    public MenuItem add(CharSequence title) { return null; }
    public MenuItem add(int titleRes) { return null; }
    public MenuItem add(int groupId, int itemId, int order, CharSequence title) { return null; }
    public MenuItem add(int groupId, int itemId, int order, int titleRes) { return null; }
    public MenuItem findItem(int id) { return null; }
    public void removeItem(int id) {}
    public void collapseItemActionView(MenuItemImpl item) {}
    public boolean expandItemActionView(MenuItemImpl item) { return false; }
}
