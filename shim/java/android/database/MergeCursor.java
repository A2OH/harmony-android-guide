package android.database;

/**
 * Android-compatible MergeCursor shim. Pure Java stub.
 * Concatenates multiple Cursors into a single logical Cursor.
 * Column names are taken from the first non-null cursor.
 */
public class MergeCursor extends AbstractCursor {

    private final Cursor[] mCursors;
    private Cursor mCursor;   // currently active sub-cursor

    public MergeCursor(Cursor[] cursors) {
        mCursors = cursors;
        // Pick first non-null cursor as the representative for column names
        mCursor = null;
        for (Cursor c : mCursors) {
            if (c != null) { mCursor = c; break; }
        }
    }

    // ──────────────────────────────────────────────────────────
    // Position
    // ──────────────────────────────────────────────────────────

    @Override
    public boolean moveToPosition(int position) {
        if (position < 0 || position >= getCount()) {
            mCursor = (mCursors.length > 0) ? mCursors[0] : null;
            return super.moveToPosition(position);
        }

        // Walk sub-cursors to find which one owns this absolute position
        int offset = position;
        for (Cursor c : mCursors) {
            if (c == null) continue;
            int count = c.getCount();
            if (offset < count) {
                mCursor = c;
                c.moveToPosition(offset);
                return super.moveToPosition(position);
            }
            offset -= count;
        }
        return false;
    }

    // ──────────────────────────────────────────────────────────
    // AbstractCursor implementation
    // ──────────────────────────────────────────────────────────

    @Override
    public int getCount() {
        int total = 0;
        for (Cursor c : mCursors) {
            if (c != null) total += c.getCount();
        }
        return total;
    }

    @Override
    public String[] getColumnNames() {
        return mCursor != null ? mCursor.getColumnNames() : new String[0];
    }

    @Override
    public String getString(int columnIndex) {
        return mCursor != null ? mCursor.getString(columnIndex) : null;
    }

    @Override
    public short getShort(int columnIndex) {
        return mCursor != null ? mCursor.getShort(columnIndex) : 0;
    }

    @Override
    public int getInt(int columnIndex) {
        return mCursor != null ? mCursor.getInt(columnIndex) : 0;
    }

    @Override
    public long getLong(int columnIndex) {
        return mCursor != null ? mCursor.getLong(columnIndex) : 0L;
    }

    @Override
    public float getFloat(int columnIndex) {
        return mCursor != null ? mCursor.getFloat(columnIndex) : 0f;
    }

    @Override
    public double getDouble(int columnIndex) {
        return mCursor != null ? mCursor.getDouble(columnIndex) : 0.0;
    }

    @Override
    public boolean isNull(int columnIndex) {
        return mCursor == null || mCursor.isNull(columnIndex);
    }

    @Override
    public int getType(int columnIndex) {
        return mCursor != null ? mCursor.getType(columnIndex) : FIELD_TYPE_NULL;
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        return mCursor != null ? mCursor.getBlob(columnIndex) : null;
    }

    // ──────────────────────────────────────────────────────────
    // Close propagation
    // ──────────────────────────────────────────────────────────

    @Override
    public void close() {
        super.close();
        for (Cursor c : mCursors) {
            if (c != null) c.close();
        }
    }
}
