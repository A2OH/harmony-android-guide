package android.widget;

import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.widget.CursorAdapter — adapter backed by a database Cursor.
 *
 * newView() and bindView() are abstract; subclasses implement them to inflate
 * and populate row Views. changeCursor() / swapCursor() manage the Cursor lifecycle.
 */
public abstract class CursorAdapter extends BaseAdapter {

    private Cursor cursor;
    private final Object context;

    public CursorAdapter(Object context, Cursor cursor, boolean autoRequery) {
        this.context = context;
        this.cursor = cursor;
    }

    // ── Abstract methods ──

    /** Inflate a new View for a row. Called only when no convertView is available. */
    public abstract View newView(Object context, Cursor cursor, ViewGroup parent);

    /** Bind data from the current Cursor row into an existing View. */
    public abstract void bindView(View view, Object context, Cursor cursor);

    // ── Cursor management ──

    public Cursor getCursor() { return cursor; }

    public void changeCursor(Cursor newCursor) {
        Cursor old = swapCursor(newCursor);
        if (old != null) old.close();
    }

    public Cursor swapCursor(Cursor newCursor) {
        Cursor old = this.cursor;
        this.cursor = newCursor;
        notifyDataSetChanged();
        return old;
    }

    // ── BaseAdapter contract ──

    @Override
    public int getCount() {
        return (cursor != null && !cursor.isClosed()) ? cursor.getCount() : 0;
    }

    @Override
    public Object getItem(int position) {
        if (cursor == null || cursor.isClosed()) return null;
        cursor.moveToPosition(position);
        return cursor;
    }

    @Override
    public long getItemId(int position) {
        if (cursor == null || cursor.isClosed()) return 0;
        cursor.moveToPosition(position);
        // Return the _id column if it exists, otherwise the position
        int idCol = cursor.getColumnIndex("_id");
        return idCol >= 0 ? cursor.getLong(idCol) : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (cursor == null || cursor.isClosed()) return null;
        cursor.moveToPosition(position);
        if (convertView == null) {
            convertView = newView(context, cursor, parent);
        }
        bindView(convertView, context, cursor);
        return convertView;
    }
}
