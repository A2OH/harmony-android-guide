package android.widget;

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

    /**
     * Full constructor.
     *
     * @param context       ignored in shim
     * @param groupData     list of maps, one per group
     * @param groupLayout   resource id for the group row layout (unused in shim)
     * @param groupFrom     keys to pull from each group map
     * @param groupTo       view ids to bind group values into (unused in shim)
     * @param childData     list of lists of maps, one list per group, one map per child
     * @param childLayout   resource id for the child row layout (unused in shim)
     * @param childFrom     keys to pull from each child map
     * @param childTo       view ids to bind child values into (unused in shim)
     */
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

    /**
     * Convenience constructor using the same layout for expanded and collapsed groups.
     */
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
    public Object getGroupView(int groupPosition, boolean isExpanded,
                                Object convertView, Object parent) {
        // Stub — returns null; a real implementation would inflate mGroupLayout
        return null;
    }

    @Override
    public Object getChildView(int groupPosition, int childPosition,
                                boolean isLastChild, Object convertView, Object parent) {
        // Stub — returns null; a real implementation would inflate mChildLayout
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
}
