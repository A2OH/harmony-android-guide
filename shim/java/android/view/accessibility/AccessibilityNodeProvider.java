package android.view.accessibility;

import android.os.Bundle;
import java.util.List;

/**
 * Shim: android.view.accessibility.AccessibilityNodeProvider
 *
 * Abstract base for custom accessibility node trees. Implementors override the
 * three virtual methods to expose a virtual view hierarchy below a single real
 * {@link android.view.View}. All methods return sensible no-op defaults so that
 * partial implementations compile without errors.
 */
public abstract class AccessibilityNodeProvider {

    /**
     * Return an {@link AccessibilityNodeInfo} describing the virtual view
     * identified by {@code virtualViewId}.
     *
     * <p>Pass {@link android.view.View#NO_ID} (or -1) to obtain the info for
     * the host view itself.
     *
     * @param virtualViewId the id of the virtual child, or -1 for the host
     * @return a new (caller-must-recycle) AccessibilityNodeInfo, or null
     */
    public AccessibilityNodeInfo createAccessibilityNodeInfo(int virtualViewId) {
        return null;
    }

    /**
     * Perform an accessibility action on the virtual view identified by
     * {@code virtualViewId}.
     *
     * @param virtualViewId the id of the virtual child on which the action
     *                      should be performed
     * @param action        the action id (one of the ACTION_* constants in
     *                      {@link AccessibilityNodeInfo.AccessibilityAction})
     * @param arguments     optional action arguments, may be null
     * @return true if the action was handled, false otherwise
     */
    public boolean performAction(int virtualViewId, int action, Bundle arguments) {
        return false;
    }

    /**
     * Find virtual views whose text matches the given query string, starting
     * the search from the virtual view identified by {@code virtualViewId}.
     *
     * @param text          the text to search for
     * @param virtualViewId the virtual view id at which to begin the search
     * @return a list of matching AccessibilityNodeInfo objects, or null
     */
    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(
            String text, int virtualViewId) {
        return null;
    }
}
