package android.widget;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

/** Stub for AOSP ExpandableListConnector. */
public class ExpandableListConnector extends BaseAdapter {
    private ExpandableListAdapter mExpandableListAdapter;

    public ExpandableListConnector(ExpandableListAdapter adapter) {
        mExpandableListAdapter = adapter;
    }

    public static class GroupMetadata {
        public int gPos;
        public int flPos;
        public int lastChildFlPos;
        public GroupMetadata() {}
    }

    public static class PositionMetadata {
        public ExpandableListPosition position;
        public GroupMetadata groupMetadata;
        public int groupInsertIndex;
        public PositionMetadata() {
            this.position = new ExpandableListPosition();
        }
        public boolean isExpanded() { return false; }
        public void recycle() {}
    }

    public PositionMetadata getUnflattenedPos(int flatPos) { return new PositionMetadata(); }
    public ExpandableListAdapter getAdapter() { return mExpandableListAdapter; }
    public void setExpandedGroupMetadataList(ArrayList<GroupMetadata> list) {}
    public ArrayList<GroupMetadata> getExpandedGroupMetadataList() { return new ArrayList<>(); }
    public boolean isGroupExpanded(int groupPosition) { return false; }
    public boolean collapseGroup(int groupPosition) { return false; }
    public boolean collapseGroup(PositionMetadata posMetadata) { return false; }
    public boolean expandGroup(int groupPosition) { return false; }
    public boolean expandGroup(PositionMetadata posMetadata) { return false; }
    public PositionMetadata getFlattenedPos(ExpandableListPosition pos) { return new PositionMetadata(); }
    public void setMaxExpGroupCount(int max) {}

    @Override
    public int getCount() { return 0; }

    @Override
    public Object getItem(int position) { return null; }

    @Override
    public long getItemId(int position) { return 0; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { return null; }
}
