package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewGroup;

import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.widget.ExpandableListView — two-level expandable list.
 *
 * Backed by ExpandableListAdapter. Group expand/collapse state is tracked in Java;
 * the ArkUI node (inherited LIST) is repopulated on state change.
 */
public class ExpandableListView extends ListView {

    private ExpandableListAdapter expandableAdapter;
    private boolean[] groupExpanded;

    private OnGroupClickListener onGroupClickListener;
    private OnChildClickListener onChildClickListener;
    private OnGroupExpandListener onGroupExpandListener;
    private OnGroupCollapseListener onGroupCollapseListener;

    public ExpandableListView() {
        super();
    }

    // ── Adapter ──

    public void setAdapter(ExpandableListAdapter adapter) {
        this.expandableAdapter = adapter;
        if (adapter != null) {
            groupExpanded = new boolean[adapter.getGroupCount()];
        }
        rebuildList();
    }

    public ExpandableListAdapter getExpandableListAdapter() { return expandableAdapter; }

    // ── Group expand / collapse ──

    public boolean expandGroup(int groupPosition) {
        if (expandableAdapter == null || groupPosition < 0
                || groupPosition >= expandableAdapter.getGroupCount()) return false;
        if (!groupExpanded[groupPosition]) {
            groupExpanded[groupPosition] = true;
            if (onGroupExpandListener != null) {
                onGroupExpandListener.onGroupExpand(groupPosition);
            }
            rebuildList();
        }
        return true;
    }

    public boolean collapseGroup(int groupPosition) {
        if (expandableAdapter == null || groupPosition < 0
                || groupPosition >= expandableAdapter.getGroupCount()) return false;
        if (groupExpanded[groupPosition]) {
            groupExpanded[groupPosition] = false;
            if (onGroupCollapseListener != null) {
                onGroupCollapseListener.onGroupCollapse(groupPosition);
            }
            rebuildList();
        }
        return true;
    }

    public boolean isGroupExpanded(int groupPosition) {
        if (groupExpanded == null || groupPosition < 0
                || groupPosition >= groupExpanded.length) return false;
        return groupExpanded[groupPosition];
    }

    // ── Listeners ──

    public void setOnGroupClickListener(OnGroupClickListener listener) {
        this.onGroupClickListener = listener;
    }

    public void setOnChildClickListener(OnChildClickListener listener) {
        this.onChildClickListener = listener;
    }

    public void setOnGroupExpandListener(OnGroupExpandListener listener) {
        this.onGroupExpandListener = listener;
    }

    public void setOnGroupCollapseListener(OnGroupCollapseListener listener) {
        this.onGroupCollapseListener = listener;
    }

    // ── Internal rebuild ──

    private void rebuildList() {
        removeAllViews();
        if (expandableAdapter == null) return;
        for (int g = 0; g < expandableAdapter.getGroupCount(); g++) {
            View groupView = expandableAdapter.getGroupView(g, isGroupExpanded(g), null, this);
            if (groupView != null) addView(groupView);
            if (isGroupExpanded(g)) {
                for (int c = 0; c < expandableAdapter.getChildrenCount(g); c++) {
                    boolean isLast = (c == expandableAdapter.getChildrenCount(g) - 1);
                    View childView = expandableAdapter.getChildView(g, c, isLast, null, this);
                    if (childView != null) addView(childView);
                }
            }
        }
    }

    // ── Interfaces ──

    public interface ExpandableListAdapter {
        int getGroupCount();
        int getChildrenCount(int groupPosition);
        Object getGroup(int groupPosition);
        Object getChild(int groupPosition, int childPosition);
        long getGroupId(int groupPosition);
        long getChildId(int groupPosition, int childPosition);
        boolean hasStableIds();
        View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent);
        View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                          View convertView, ViewGroup parent);
        boolean isChildSelectable(int groupPosition, int childPosition);
    }

    public interface OnGroupClickListener {
        boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id);
    }

    public interface OnChildClickListener {
        boolean onChildClick(ExpandableListView parent, View v,
                             int groupPosition, int childPosition, long id);
    }

    public interface OnGroupExpandListener {
        void onGroupExpand(int groupPosition);
    }

    public interface OnGroupCollapseListener {
        void onGroupCollapse(int groupPosition);
    }
}
