package android.widget;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.widget.BaseExpandableListAdapter — abstract base for
 * two-level expandable-list adapters.
 *
 * Implements {@link ExpandableListAdapter} and provides default implementations
 * for the optional lifecycle callbacks, observer management, and the stable-IDs
 * flag, so concrete subclasses only need to supply the eight data/view methods.
 *
 * Required overrides:
 * <ul>
 *   <li>{@link #getGroupCount()}</li>
 *   <li>{@link #getChildrenCount(int)}</li>
 *   <li>{@link #getGroup(int)}</li>
 *   <li>{@link #getChild(int, int)}</li>
 *   <li>{@link #getGroupId(int)}</li>
 *   <li>{@link #getChildId(int, int)}</li>
 *   <li>{@link #getGroupView(int, boolean, View, ViewGroup)}</li>
 *   <li>{@link #getChildView(int, int, boolean, View, ViewGroup)}</li>
 *   <li>{@link #isChildSelectable(int, int)}</li>
 * </ul>
 */
public abstract class BaseExpandableListAdapter implements ExpandableListAdapter {

    private final List<DataSetObserver> observers = new ArrayList<>();

    // ── Abstract data methods ──

    @Override
    public abstract int getGroupCount();

    @Override
    public abstract int getChildrenCount(int groupPosition);

    @Override
    public abstract Object getGroup(int groupPosition);

    @Override
    public abstract Object getChild(int groupPosition, int childPosition);

    @Override
    public abstract long getGroupId(int groupPosition);

    @Override
    public abstract long getChildId(int groupPosition, int childPosition);

    // ── Abstract view methods ──

    @Override
    public abstract View getGroupView(int groupPosition, boolean isExpanded,
                                      View convertView, ViewGroup parent);

    @Override
    public abstract View getChildView(int groupPosition, int childPosition,
                                      boolean isLastChild,
                                      View convertView, ViewGroup parent);

    @Override
    public abstract boolean isChildSelectable(int groupPosition, int childPosition);

    // ── Default implementations ──

    /**
     * By default IDs are not considered stable. Override and return true if
     * your group/child IDs are stable across dataset changes.
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Return whether all children in all groups are selectable.
     * Default returns true; override for finer control.
     */
    public boolean areAllItemsEnabled() {
        return true;
    }

    // ── Combined position helpers ──

    /**
     * Pack a group and child position into a single long suitable for use as a
     * combined ID (mirrors {@code ExpandableListView.getPackedPositionForChild}).
     */
    public static long getPackedPositionForChild(int groupPosition, int childPosition) {
        return (((long) groupPosition) << 32) | (childPosition & 0xFFFFFFFFL);
    }

    /**
     * Pack a group position into a combined long (child bits are all 1s to
     * indicate a group row).
     */
    public static long getPackedPositionForGroup(int groupPosition) {
        return (((long) groupPosition) << 32) | 0xFFFFFFFFL;
    }

    // ── Observer management ──

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        observers.remove(observer);
    }

    /** Notify all registered observers that the data set has changed. */
    public void notifyDataSetChanged() {
        for (DataSetObserver o : observers) {
            o.onChanged();
        }
    }

    /** Notify all registered observers that the data set is no longer valid. */
    public void notifyDataSetInvalidated() {
        for (DataSetObserver o : observers) {
            o.onInvalidated();
        }
    }

    // ── Lifecycle callbacks (no-ops by default) ──

    @Override
    public void onGroupCollapsed(int groupPosition) {
        // no-op — override to react to group collapse events
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        // no-op — override to react to group expand events
    }
}
