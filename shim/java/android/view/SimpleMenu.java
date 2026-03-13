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
        mItems.removeIf(item -> item.getItemId() == id);
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
    }
}
