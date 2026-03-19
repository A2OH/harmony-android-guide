package android.widget;

/** Stub for AOSP ExpandableListPosition. */
public class ExpandableListPosition {
    public static final int CHILD = 1;
    public static final int GROUP = 2;

    public int type;
    public int groupPos;
    public int childPos;
    public int flatListPos;

    public ExpandableListPosition() {}

    public static ExpandableListPosition obtainGroupPosition(int groupPosition) {
        ExpandableListPosition pos = new ExpandableListPosition();
        pos.type = GROUP;
        pos.groupPos = groupPosition;
        pos.childPos = -1;
        return pos;
    }

    public static ExpandableListPosition obtainChildPosition(int groupPosition, int childPosition) {
        ExpandableListPosition pos = new ExpandableListPosition();
        pos.type = CHILD;
        pos.groupPos = groupPosition;
        pos.childPos = childPosition;
        return pos;
    }

    public static ExpandableListPosition obtainPosition(long flatListPos) {
        ExpandableListPosition pos = new ExpandableListPosition();
        pos.flatListPos = (int) flatListPos;
        return pos;
    }

    public static ExpandableListPosition obtain(int type, int groupPos, int childPos, int flatListPos) {
        ExpandableListPosition pos = new ExpandableListPosition();
        pos.type = type;
        pos.groupPos = groupPos;
        pos.childPos = childPos;
        pos.flatListPos = flatListPos;
        return pos;
    }

    public long getPackedPosition() { return 0L; }
    public void recycle() {}
}
