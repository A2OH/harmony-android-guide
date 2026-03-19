package com.android.internal.view.menu;

import android.view.MenuItem;

/** Stub for AOSP MenuItemImpl. */
public class MenuItemImpl implements MenuItem {
    public MenuItemImpl() {}

    public int getItemId() { return 0; }
    public int getGroupId() { return 0; }
    public int getOrder() { return 0; }
    public MenuItem setTitle(CharSequence title) { return this; }
    public MenuItem setTitle(int title) { return this; }
    public CharSequence getTitle() { return ""; }
    public MenuItem setIcon(int iconRes) { return this; }
    public MenuItem setEnabled(boolean enabled) { return this; }
    public boolean isEnabled() { return true; }
    public MenuItem setVisible(boolean visible) { return this; }
    public boolean isVisible() { return true; }
    public MenuItem setChecked(boolean checked) { return this; }
    public boolean isChecked() { return false; }
    public MenuItem setCheckable(boolean checkable) { return this; }
    public boolean isCheckable() { return false; }
    public boolean hasSubMenu() { return false; }
    public android.view.SubMenu getSubMenu() { return null; }
    public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener listener) { return this; }
    public android.content.Intent getIntent() { return null; }
    public MenuItem setIntent(android.content.Intent intent) { return this; }
    public char getAlphabeticShortcut() { return 0; }
    public MenuItem setAlphabeticShortcut(char alphaChar) { return this; }
    public char getNumericShortcut() { return 0; }
    public MenuItem setNumericShortcut(char numericChar) { return this; }
    public MenuItem setShortcut(char numericChar, char alphaChar) { return this; }
    public android.view.ContextMenu.ContextMenuInfo getMenuInfo() { return null; }
    public void setShowAsAction(int actionEnum) {}
    public MenuItem setShowAsActionFlags(int actionEnum) { return this; }
    public MenuItem setActionView(android.view.View view) { return this; }
    public MenuItem setActionView(int resId) { return this; }
    public android.view.View getActionView() { return null; }
    public MenuItem setActionProvider(android.view.ActionProvider provider) { return this; }
    public android.view.ActionProvider getActionProvider() { return null; }
    public boolean expandActionView() { return false; }
    public boolean collapseActionView() { return false; }
    public boolean isActionViewExpanded() { return false; }
    public MenuItem setOnActionExpandListener(OnActionExpandListener listener) { return this; }
    public MenuItem setContentDescription(CharSequence contentDescription) { return this; }
    public CharSequence getContentDescription() { return null; }
    public MenuItem setTooltipText(CharSequence tooltipText) { return this; }
    public CharSequence getTooltipText() { return null; }
    public MenuItem setIcon(android.graphics.drawable.Drawable icon) { return this; }
    public android.graphics.drawable.Drawable getIcon() { return null; }
    public MenuItem setIconTintList(android.content.res.ColorStateList tint) { return this; }
    public android.content.res.ColorStateList getIconTintList() { return null; }
    public MenuItem setIconTintMode(android.graphics.PorterDuff.Mode mode) { return this; }
    public android.graphics.PorterDuff.Mode getIconTintMode() { return null; }
    public MenuItem setAlphabeticShortcut(char alphaChar, int alphaModifiers) { return this; }
    public int getAlphabeticModifiers() { return 0; }
    public MenuItem setNumericShortcut(char numericChar, int numericModifiers) { return this; }
    public int getNumericModifiers() { return 0; }
    public MenuItem setShortcut(char numericChar, char alphaChar, int numericModifiers, int alphaModifiers) { return this; }
    public void setActionViewExpanded(boolean expanded) {}
    public boolean requiresActionButton() { return false; }
    public boolean requiresOverflow() { return false; }
}
