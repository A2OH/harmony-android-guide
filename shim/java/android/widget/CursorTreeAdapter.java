package android.widget;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

public class CursorTreeAdapter extends BaseExpandableListAdapter implements Filterable {
    public CursorTreeAdapter() {}
    public CursorTreeAdapter(Cursor p0, Context p1) {}
    public CursorTreeAdapter(Cursor p0, Context p1, boolean p2) {}

    public void bindChildView(View p0, Context p1, Cursor p2, boolean p3) {}
    public void bindGroupView(View p0, Context p1, Cursor p2, boolean p3) {}
    public void changeCursor(Cursor p0) {}
    public String convertToString(Cursor p0) { return null; }
    public Cursor getChild(int p0, int p1) { return null; }
    public long getChildId(int p0, int p1) { return 0L; }
    public View getChildView(int p0, int p1, boolean p2, View p3, ViewGroup p4) { return null; }
    public int getChildrenCount(int p0) { return 0; }
    public Cursor getChildrenCursor(Cursor p0) { return null; }
    public Cursor getCursor() { return null; }
    public Filter getFilter() { return null; }
    public FilterQueryProvider getFilterQueryProvider() { return null; }
    public Cursor getGroup(int p0) { return null; }
    public int getGroupCount() { return 0; }
    public long getGroupId(int p0) { return 0L; }
    public View getGroupView(int p0, boolean p1, View p2, ViewGroup p3) { return null; }
    public boolean hasStableIds() { return false; }
    public boolean isChildSelectable(int p0, int p1) { return false; }
    public View newChildView(Context p0, Cursor p1, boolean p2, ViewGroup p3) { return null; }
    public View newGroupView(Context p0, Cursor p1, boolean p2, ViewGroup p3) { return null; }
    public void notifyDataSetChanged(boolean p0) {}
    public Cursor runQueryOnBackgroundThread(CharSequence p0) { return null; }
    public void setChildrenCursor(int p0, Cursor p1) {}
    public void setFilterQueryProvider(FilterQueryProvider p0) {}
    public void setGroupCursor(Cursor p0) {}
}
