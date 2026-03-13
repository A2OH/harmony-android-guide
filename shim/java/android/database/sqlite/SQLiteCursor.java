package android.database.sqlite;

import android.database.CharArrayBuffer;
import android.database.CrossProcessCursor;
import android.database.DataSetObserver;

/**
 * Android-compatible SQLiteCursor shim.
 * Represents the result set of a SQLite query and implements
 * {@link CrossProcessCursor} so it can be transferred via Binder IPC.
 *
 * All data-access methods are stubs that return safe defaults.
 * The constructor signature mirrors the real Android one:
 *   SQLiteCursor(SQLiteCursorDriver driver, String editTable, SQLiteQuery query)
 * but accepts Object parameters for portability (no native dependencies).
 */
public class SQLiteCursor implements CrossProcessCursor {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private final Object mDriver;
    private final String mEditTable;
    private final Object mQuery;      // SQLiteQuery – typed as Object for portability

    private String[] mColumnNames = new String[0];
    private int mCount = 0;
    private int mPos   = -1;
    private boolean mClosed = false;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates a new SQLiteCursor.
     *
     * @param driver    the {@code SQLiteCursorDriver} that created this cursor (Object for portability)
     * @param editTable the name of the table used for editing, or null
     * @param query     the compiled {@code SQLiteQuery} backing this cursor (Object for portability)
     */
    public SQLiteCursor(Object driver, String editTable, Object query) {
        mDriver    = driver;
        mEditTable = editTable;
        mQuery     = query;
    }

    // -------------------------------------------------------------------------
    // CrossProcessCursor
    // -------------------------------------------------------------------------

    @Override
    public Object getWindow() { return null; }

    @Override
    public void fillWindow(int position, Object window) { /* stub */ }

    @Override
    public boolean onMove(int oldPosition, int newPosition) { return true; }

    // -------------------------------------------------------------------------
    // Cursor – position / navigation
    // -------------------------------------------------------------------------

    @Override public int getCount()    { return mCount; }
    @Override public int getPosition() { return mPos; }

    @Override public boolean move(int offset)            { return moveToPosition(mPos + offset); }
    @Override public boolean moveToFirst()               { return moveToPosition(0); }
    @Override public boolean moveToLast()                { return moveToPosition(mCount - 1); }
    @Override public boolean moveToNext()                { return moveToPosition(mPos + 1); }
    @Override public boolean moveToPrevious()            { return moveToPosition(mPos - 1); }

    @Override
    public boolean moveToPosition(int position) {
        if (mCount == 0) {
            mPos = -1;
            return false;
        }
        if (position < 0) {
            mPos = -1;
            return false;
        }
        if (position >= mCount) {
            mPos = mCount;
            return false;
        }
        mPos = position;
        return true;
    }

    @Override public boolean isFirst()       { return mPos == 0 && mCount > 0; }
    @Override public boolean isLast()        { return mPos == mCount - 1 && mCount > 0; }
    @Override public boolean isBeforeFirst() { return mCount == 0 || mPos < 0; }
    @Override public boolean isAfterLast()   { return mCount == 0 || mPos >= mCount; }

    // -------------------------------------------------------------------------
    // Cursor – columns
    // -------------------------------------------------------------------------

    @Override
    public int getColumnIndex(String columnName) {
        for (int i = 0; i < mColumnNames.length; i++) {
            if (mColumnNames[i].equalsIgnoreCase(columnName)) return i;
        }
        return -1;
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        int idx = getColumnIndex(columnName);
        if (idx < 0) throw new IllegalArgumentException("column '" + columnName + "' does not exist");
        return idx;
    }

    @Override public String   getColumnName(int columnIndex) { return mColumnNames[columnIndex]; }
    @Override public String[] getColumnNames()               { return mColumnNames.clone(); }
    @Override public int      getColumnCount()               { return mColumnNames.length; }

    // -------------------------------------------------------------------------
    // Cursor – data access (stubs)
    // -------------------------------------------------------------------------

    @Override public byte[]  getBlob(int columnIndex)   { return null; }
    @Override public String  getString(int columnIndex) { return null; }
    @Override public void    copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
        String s = getString(columnIndex);
        if (s != null) {
            char[] chars = s.toCharArray();
            if (buffer.data == null || buffer.data.length < chars.length) {
                buffer.data = chars;
            } else {
                System.arraycopy(chars, 0, buffer.data, 0, chars.length);
            }
            buffer.sizeCopied = chars.length;
        } else {
            buffer.sizeCopied = 0;
        }
    }
    @Override public short   getShort(int columnIndex)  { return 0; }
    @Override public int     getInt(int columnIndex)    { return 0; }
    @Override public long    getLong(int columnIndex)   { return 0L; }
    @Override public float   getFloat(int columnIndex)  { return 0f; }
    @Override public double  getDouble(int columnIndex) { return 0.0; }
    @Override public int     getType(int columnIndex)   { return FIELD_TYPE_NULL; }
    @Override public boolean isNull(int columnIndex)    { return true; }

    // -------------------------------------------------------------------------
    // Cursor – lifecycle
    // -------------------------------------------------------------------------

    @Override
    public void close() {
        mClosed = true;
    }

    @Override
    public boolean isClosed() { return mClosed; }

    // -------------------------------------------------------------------------
    // Cursor – observers / extras (stubs)
    // -------------------------------------------------------------------------

    @Override public void   registerContentObserver(Object observer)   { /* stub */ }
    @Override public void   unregisterContentObserver(Object observer) { /* stub */ }
    @Override public void   registerDataSetObserver(DataSetObserver observer)   { /* stub */ }
    @Override public void   unregisterDataSetObserver(DataSetObserver observer) { /* stub */ }
    @Override public void   setNotificationUri(Object cr, Object uri)  { /* stub */ }
    @Override public Object getNotificationUri()                        { return null; }
    @Override public boolean getWantsAllOnMoveCalls()                   { return false; }
    @Override public void   setExtras(Object extras)                   { /* stub */ }
    @Override public Object getExtras()                                 { return null; }
    @Override public Object respond(Object extras)                      { return null; }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Returns the table name used for editing, if any. */
    public String getEditTable() { return mEditTable; }

    @Override
    public String toString() {
        return "SQLiteCursor[table=" + mEditTable + ", pos=" + mPos + "/" + mCount + "]";
    }
}
