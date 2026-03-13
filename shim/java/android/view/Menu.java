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
}
