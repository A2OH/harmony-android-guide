package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewGroup;

import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.Map;

/**
 * Android-compatible SimpleExpandableListAdapter shim.
 * Wraps lists of Maps to supply group and child rows in an ExpandableListView.
 */
public class SimpleExpandableListAdapter extends BaseExpandableListAdapter {

    private final List<? extends Map<String, ?>> mGroupData;
    private final List<? extends List<? extends Map<String, ?>>> mChildData;
    private final String[] mGroupFrom;
    private final int[] mGroupTo;
    private final String[] mChildFrom;
    private final int[] mChildTo;
    private final int mGroupLayout;
    private final int mChildLayout;

    public SimpleExpandableListAdapter(
            Object context,
            List<? extends Map<String, ?>> groupData,
            int groupLayout,
            String[] groupFrom,
            int[] groupTo,
            List<? extends List<? extends Map<String, ?>>> childData,
            int childLayout,
            String[] childFrom,
            int[] childTo) {
        mGroupData = groupData;
        mChildData = childData;
        mGroupLayout = groupLayout;
        mGroupFrom = groupFrom;
        mGroupTo = groupTo;
        mChildLayout = childLayout;
        mChildFrom = childFrom;
        mChildTo = childTo;
    }

    public SimpleExpandableListAdapter(
            Object context,
            List<? extends Map<String, ?>> groupData,
            int expandedGroupLayout,
            int collapsedGroupLayout,
            String[] groupFrom,
            int[] groupTo,
            List<? extends List<? extends Map<String, ?>>> childData,
            int childLayout,
            String[] childFrom,
            int[] childTo) {
        this(context, groupData, expandedGroupLayout, groupFrom, groupTo,
             childData, childLayout, childFrom, childTo);
    }

    @Override
    public int getGroupCount() {
        return mGroupData == null ? 0 : mGroupData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mChildData == null || groupPosition >= mChildData.size()) return 0;
        List<?> children = mChildData.get(groupPosition);
        return children == null ? 0 : children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupData == null ? null : mGroupData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (mChildData == null || groupPosition >= mChildData.size()) return null;
        List<? extends Map<String, ?>> children = mChildData.get(groupPosition);
        return children == null ? null : children.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) { return groupPosition; }

    @Override
    public long getChildId(int groupPosition, int childPosition) { return childPosition; }

    @Override
    public boolean hasStableIds() { return true; }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                              View convertView, ViewGroup parent) {
        // Stub — returns null; a real implementation would inflate mGroupLayout
        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                              boolean isLastChild, View convertView, ViewGroup parent) {
        // Stub — returns null; a real implementation would inflate mChildLayout
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
}
