package android.view;
import android.graphics.drawable.Icon;
import android.widget.PopupMenu;
import android.widget.Toolbar;
import android.graphics.drawable.Icon;
import android.widget.PopupMenu;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Shim-internal implementation of {@link Menu}.
 *
 * Used by PopupMenu, Toolbar and other widgets that need a concrete Menu
 * object. Not part of the public Android API — callers receive the Menu
 * interface.
 */
public class SimpleMenu implements Menu {

    private final List<MenuItem> mItems = new ArrayList<>();

    @Override
    public MenuItem add(CharSequence title) {
        return add(0, mItems.size(), mItems.size(), title);
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
        SimpleMenuItem item = new SimpleMenuItem(itemId, title);
        mItems.add(item);
        return item;
    }

    @Override
    public MenuItem findItem(int id) {
        for (MenuItem item : mItems) {
            if (item.getItemId() == id) return item;
        }
        return null;
    }

    @Override
    public int size() {
        return mItems.size();
    }

    @Override
    public void clear() {
        mItems.clear();
    }

    @Override
    public void removeItem(int id) {
        java.util.Iterator<MenuItem> it = mItems.iterator();
        while (it.hasNext()) {
            if (it.next().getItemId() == id) {
                it.remove();
            }
        }
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, int titleRes) {
        return add(groupId, itemId, order, (CharSequence) String.valueOf(titleRes));
    }

    public MenuItem add(int titleRes) {
        return add(0, mItems.size(), mItems.size(), "@" + titleRes);
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) { return null; }

    @Override
    public SubMenu addSubMenu(CharSequence title) { return null; }

    @Override
    public SubMenu addSubMenu(int titleRes) { return null; }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) { return null; }

    @Override
    public MenuItem getItem(int index) {
        if (index >= 0 && index < mItems.size()) return mItems.get(index);
        return null;
    }

    @Override
    public void removeGroup(int groupId) {}

    @Override
    public void setGroupEnabled(int group, boolean enabled) {}

    @Override
    public void setGroupVisible(int group, boolean visible) {}

    @Override
    public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {}

    @Override
    public void close() {}

    @Override
    public boolean hasVisibleItems() {
        for (MenuItem item : mItems) {
            if (item.isVisible()) return true;
        }
        return false;
    }

    // ── SimpleMenuItem inner class ──

    private static class SimpleMenuItem implements MenuItem {
        private final int mId;
        private CharSequence mTitle;
        private boolean mVisible  = true;
        private boolean mEnabled  = true;
        private boolean mChecked  = false;

        SimpleMenuItem(int id, CharSequence title) {
            mId    = id;
            mTitle = title;
        }

        @Override public int getItemId() { return mId; }

        @Override public CharSequence getTitle() { return mTitle; }

        @Override public MenuItem setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        @Override public MenuItem setTitle(int titleResId) {
            mTitle = "@" + titleResId;
            return this;
        }

        @Override public MenuItem setIcon(int iconResId) {
            // Icon display not yet wired to ArkUI
            return this;
        }

        @Override public MenuItem setVisible(boolean visible) {
            mVisible = visible;
            return this;
        }

        @Override public MenuItem setEnabled(boolean enabled) {
            mEnabled = enabled;
            return this;
        }

        @Override public boolean isChecked() { return mChecked; }

        @Override public MenuItem setChecked(boolean checked) {
            mChecked = checked;
            return this;
        }
        @Override public int getGroupId() { return 0; }
        @Override public int getOrder() { return 0; }
        @Override public MenuItem setIcon(android.graphics.drawable.Drawable icon) { return this; }
        @Override public android.graphics.drawable.Drawable getIcon() { return null; }
        @Override public boolean isVisible() { return mVisible; }
        @Override public boolean isEnabled() { return mEnabled; }
        @Override public MenuItem setCheckable(boolean checkable) { return this; }
        @Override public boolean isCheckable() { return false; }
        @Override public boolean hasSubMenu() { return false; }
        @Override public SubMenu getSubMenu() { return null; }
        @Override public android.content.Intent getIntent() { return null; }
        @Override public MenuItem setIntent(android.content.Intent intent) { return this; }
        @Override public char getAlphabeticShortcut() { return 0; }
        @Override public MenuItem setAlphabeticShortcut(char alphaChar) { return this; }
        @Override public char getNumericShortcut() { return 0; }
        @Override public MenuItem setNumericShortcut(char numericChar) { return this; }
        @Override public MenuItem setShortcut(char numericChar, char alphaChar) { return this; }
        @Override public ContextMenu.ContextMenuInfo getMenuInfo() { return null; }
        @Override public void setShowAsAction(int actionEnum) {}
        @Override public MenuItem setShowAsActionFlags(int actionEnum) { return this; }
        @Override public MenuItem setActionView(View view) { return this; }
        @Override public MenuItem setActionView(int resId) { return this; }
        @Override public View getActionView() { return null; }
        @Override public MenuItem setActionProvider(ActionProvider provider) { return this; }
        @Override public ActionProvider getActionProvider() { return null; }
        @Override public boolean expandActionView() { return false; }
        @Override public boolean collapseActionView() { return false; }
        @Override public boolean isActionViewExpanded() { return false; }
        @Override public MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener listener) { return this; }
        @Override public MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener listener) { return this; }
        @Override public MenuItem setContentDescription(CharSequence contentDescription) { return this; }
        @Override public CharSequence getContentDescription() { return null; }
        @Override public MenuItem setTooltipText(CharSequence tooltipText) { return this; }
        @Override public CharSequence getTooltipText() { return null; }
        @Override public MenuItem setIconTintList(android.content.res.ColorStateList tint) { return this; }
        @Override public android.content.res.ColorStateList getIconTintList() { return null; }
        @Override public MenuItem setIconTintMode(android.graphics.PorterDuff.Mode mode) { return this; }
        @Override public android.graphics.PorterDuff.Mode getIconTintMode() { return null; }
    }
}
