package android.widget;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.widget.ExpandableListAdapter — interface for two-level expandable lists.
 *
 * An ExpandableListAdapter supplies group rows and the child rows that appear
 * under each group when it is expanded. It is consumed by
 * {@link ExpandableListView} and implemented by
 * {@link BaseExpandableListAdapter} (and its subclasses).
 *
 * Note: The identical interface is also defined as the inner type
 * {@code ExpandableListView.ExpandableListAdapter} for source compatibility
 * with code that references it through the outer class.
 */
public interface ExpandableListAdapter {

    /**
     * Return the number of groups in the list.
     */
    int getGroupCount();

    /**
     * Return the number of children in a specified group.
     *
     * @param groupPosition The group whose children count is requested.
     */
    int getChildrenCount(int groupPosition);

    /**
     * Return the data associated with the given group.
     *
     * @param groupPosition The position of the group.
     */
    Object getGroup(int groupPosition);

    /**
     * Return the data associated with the given child within the given group.
     *
     * @param groupPosition The position of the group containing the child.
     * @param childPosition The position of the child within that group.
     */
    Object getChild(int groupPosition, int childPosition);

    /**
     * Return the ID for the group at the given position.
     *
     * @param groupPosition The position of the group.
     */
    long getGroupId(int groupPosition);

    /**
     * Return the ID for the given child within the given group.
     *
     * @param groupPosition The position of the group containing the child.
     * @param childPosition The position of the child within that group.
     */
    long getChildId(int groupPosition, int childPosition);

    /**
     * Indicate whether the IDs returned by {@link #getGroupId} and
     * {@link #getChildId} are stable across dataset changes.
     */
    boolean hasStableIds();

    /**
     * Return a View for the given group, optionally recycling {@code convertView}.
     *
     * @param groupPosition The position of the group whose View is needed.
     * @param isExpanded    Whether the group is currently expanded.
     * @param convertView   The old view to reuse if possible; may be null.
     * @param parent        The parent ViewGroup that the returned View will be attached to.
     */
    View getGroupView(int groupPosition, boolean isExpanded,
                      View convertView, ViewGroup parent);

    /**
     * Return a View for the given child, optionally recycling {@code convertView}.
     *
     * @param groupPosition The position of the group containing the child.
     * @param childPosition The position of the child within that group.
     * @param isLastChild   Whether this child is the last within its group.
     * @param convertView   The old view to reuse if possible; may be null.
     * @param parent        The parent ViewGroup that the returned View will be attached to.
     */
    View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                      View convertView, ViewGroup parent);

    /**
     * Return whether the child at the given position is selectable.
     *
     * @param groupPosition The position of the group containing the child.
     * @param childPosition The position of the child within that group.
     */
    boolean isChildSelectable(int groupPosition, int childPosition);

    // ── Optional observer hooks (no-op default in Java 8+ interfaces) ──

    /**
     * Register an observer that is called when changes happen to the data used
     * by this adapter.
     */
    default void registerDataSetObserver(android.database.DataSetObserver observer) {}

    /**
     * Unregister a previously registered observer.
     */
    default void unregisterDataSetObserver(android.database.DataSetObserver observer) {}

    /**
     * Called when the containing list is about to be recycled.
     */
    default void onGroupCollapsed(int groupPosition) {}

    /**
     * Called when a group is expanded.
     */
    default void onGroupExpanded(int groupPosition) {}
}
