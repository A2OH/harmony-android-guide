package android.view;
import java.util.Set;

/**
 * Shim: android.view.ActionProvider → stub.
 *
 * Abstract base class for providing alternative actions to menu items.
 * Subclasses override onCreateActionView() to supply a custom View that
 * replaces the default menu-item presentation.
 */
public class ActionProvider {
    public ActionProvider() {}

    private Object mContext;
    private VisibilityListener mVisibilityListener;

    /**
     * Object for visibility changes on an ActionProvider.
     */
    public interface VisibilityListener {
        void onActionProviderVisibilityChanged(boolean isVisible);
    }

    public ActionProvider(Object context) {
        mContext = context;
    }

    /**
     * Object method for creating a View that represents this action in a menu.
     */
    public View onCreateActionView() { return null; }

    /**
     * Object method called with the MenuItem context. Default returns null;
     * subclasses may override.
     */
    public View onCreateActionView(Object menuItem) {
        return null;
    }

    /**
     * Performs a default action associated with this provider.
     *
     * @return false by default (no action performed).
     */
    public boolean onPerformDefaultAction() {
        return false;
    }

    /**
     * @return true if this provider has a submenu.
     */
    public boolean hasSubMenu() {
        return false;
    }

    /**
     * Called to prepare a submenu { // stub@link #hasSubMenu()} returns true.
     */
    public void onPrepareSubMenu(Object subMenu) {
        // no-op
    }

    /**
     * @return true if this provider is visible.
     */
    public boolean isVisible() {
        return true;
    }

    /**
     * Refresh visibility state. Default is a no-op.
     */
    public void refreshVisibility() {
        // no-op
    }

    /**
     * Set a listener to observe visibility changes.
     */
    public void setVisibilityListener(VisibilityListener listener) {
        mVisibilityListener = listener;
    }
}
