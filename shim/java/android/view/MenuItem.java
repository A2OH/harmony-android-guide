package android.view;

/**
 * Shim: android.view.MenuItem interface.
 *
 * Represents a single item in a Menu.
 */
public interface MenuItem {

    /** Return the identifier for this menu item. */
    int getItemId();

    /** Return the title associated with this item. */
    CharSequence getTitle();

    /** Change the title associated with this item. */
    MenuItem setTitle(CharSequence title);

    /** Change the title using a string resource id. */
    MenuItem setTitle(int title);

    /** Set the icon to display for this item. */
    MenuItem setIcon(int iconResId);

    /** Control whether this item is visible. */
    MenuItem setVisible(boolean visible);

    /** Control whether this item is enabled. */
    MenuItem setEnabled(boolean enabled);

    /** Return the checked state of this item. */
    boolean isChecked();

    /** Control whether this item is checked. */
    MenuItem setChecked(boolean checked);
}
