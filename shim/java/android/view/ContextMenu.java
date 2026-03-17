package android.view;
import java.util.Set;

/**
 * Shim: android.view.ContextMenu interface.
 * Extends Menu with context-menu-specific header methods.
 */
public interface ContextMenu extends Menu {

    /** Set the context menu header's title. */
    ContextMenu setHeaderTitle(CharSequence title);

    /** Set the context menu header's title from a resource id. */
    ContextMenu setHeaderTitle(int titleRes);

    /** Set the context menu header's icon by resource id. */
    ContextMenu setHeaderIcon(int iconRes);

    /** Set the context menu header's custom view. */
    ContextMenu setHeaderView(View view);

    /** Remove the header from the context menu. */
    void clearHeader();

    /**
     * Additional information about the item for which the context menu is shown.
     * Provides extra data such as the position of the item in a list.
     */
    public interface ContextMenuInfo {
        // Marker interface — implementing classes provide specific fields
    }

    /** Backward-compat alias (stub generator used "Object") */
    public interface Object extends ContextMenuInfo {}
}
