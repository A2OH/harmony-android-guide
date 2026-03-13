package android.view;

/**
 * Shim: android.view.SubMenu interface.
 * Extends Menu to represent a sub-menu within a parent menu.
 */
public interface SubMenu extends Menu {

    /** Set the submenu header's title to a string. */
    SubMenu setHeaderTitle(CharSequence title);

    /** Set the submenu header's title from a resource id. */
    SubMenu setHeaderTitle(int titleRes);

    /** Set the submenu header's icon by resource id. */
    SubMenu setHeaderIcon(int iconRes);

    /** Set a custom view as the submenu header. */
    SubMenu setHeaderView(View view);

    /** Set the icon for the menu item that triggers this submenu. */
    SubMenu setIcon(int iconRes);

    /** Remove the header from the submenu. */
    void clearHeader();

    /** Return the MenuItem that represents this submenu in the parent menu. */
    MenuItem getItem();
}
