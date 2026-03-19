package android.view;
import android.widget.PopupMenu;
import android.widget.Toolbar;
import android.widget.PopupMenu;
import android.widget.Toolbar;

/**
 * Shim: android.view.Menu interface.
 *
 * Represents a menu containing MenuItems. Used by PopupMenu, Toolbar, etc.
 */
public interface Menu {

    public static final int NONE = 0;
    public static final int FIRST = 1;
    public static final int FLAG_APPEND_TO_GROUP = 0;
    public static final int FLAG_PERFORM_NO_CLOSE = 1;
    public static final int CATEGORY_CONTAINER = 0x00010000;
    public static final int CATEGORY_SYSTEM = 0x00020000;
    public static final int CATEGORY_SECONDARY = 0x00030000;
    public static final int CATEGORY_ALTERNATIVE = 0x00040000;
    public static final int CATEGORY_MASK = 0xffff0000;
    public static final int USER_MASK = 0x0000ffff;

    /** Add a menu item with the given title. Returns the new MenuItem. */
    MenuItem add(CharSequence title);

    /** Add a menu item with group, id, order and title. */
    MenuItem add(int groupId, int itemId, int order, CharSequence title);

    /** Find a MenuItem by its ID. Returns null if not found. */
    MenuItem findItem(int id);

    /** Return the number of items in this menu. */
    int size();

    /** Remove all items from this menu. */
    void clear();

    /** Remove the menu item with the given ID. */
    void removeItem(int id);

    /** Add a sub-menu with group, id, order and title. */
    SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title);

    /** Add a sub-menu with the given title. */
    SubMenu addSubMenu(CharSequence title);

    /** Add a sub-menu with a string resource title. */
    SubMenu addSubMenu(int titleRes);

    /** Get the menu item at the given index. */
    MenuItem getItem(int index);

    /** Remove a group of menu items. */
    void removeGroup(int groupId);

    /** Set a group's enabled state. */
    void setGroupEnabled(int group, boolean enabled);

    /** Set a group's visibility. */
    void setGroupVisible(int group, boolean visible);

    /** Set a group's checkable state. */
    void setGroupCheckable(int group, boolean checkable, boolean exclusive);

    /** Close the menu. */
    void close();

    /** Add a menu item with group, id, order and string resource title. */
    MenuItem add(int groupId, int itemId, int order, int titleRes);

    /** Add a menu item with a string resource title. */
    MenuItem add(int titleRes);

    /** Add a sub-menu with group, id, order and string resource title. */
    SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes);

    /** Check if there are visible items. */
    boolean hasVisibleItems();
}
