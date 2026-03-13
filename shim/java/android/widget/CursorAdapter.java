package android.widget;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

public class CursorAdapter extends BaseAdapter implements Filterable, ThemedSpinnerAdapter {
    public CursorAdapter() {}
    public static final int FLAG_REGISTER_CONTENT_OBSERVER = 0;

    public CursorAdapter(Context p0, Cursor p1, boolean p2) {}
    public CursorAdapter(Context p0, Cursor p1, int p2) {}

    public void bindView(View p0, Context p1, Cursor p2) {}
    public void changeCursor(Cursor p0) {}
    public CharSequence convertToString(Cursor p0) { return null; }
    public int getCount() { return 0; }
    public Cursor getCursor() { return null; }
    public Object getDropDownViewTheme() { return null; }
    public Filter getFilter() { return null; }
    public FilterQueryProvider getFilterQueryProvider() { return null; }
    public Object getItem(int p0) { return null; }
    public long getItemId(int p0) { return 0L; }
    public View getView(int p0, View p1, ViewGroup p2) { return null; }
    public View newDropDownView(Context p0, Cursor p1, ViewGroup p2) { return null; }
    public View newView(Context p0, Cursor p1, ViewGroup p2) { return null; }
    public void onContentChanged() {}
    public void setDropDownViewTheme(Object p0) {}
    public void setFilterQueryProvider(FilterQueryProvider p0) {}
    public Cursor swapCursor(Cursor p0) { return null; }
    public View getDropDownView(int position, Object convertView, Object parent) { return null; }
    public boolean hasStableIds() { return false; }
    public void registerDataSetObserver(android.database.DataSetObserver observer) {}
    public void unregisterDataSetObserver(android.database.DataSetObserver observer) {}
    public int getItemViewType(int position) { return 0; }
    public int getViewTypeCount() { return 1; }
}
