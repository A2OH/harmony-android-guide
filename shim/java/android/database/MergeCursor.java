package android.database;

public class MergeCursor extends AbstractCursor {
    private final Cursor[] mCursors;
    private int mActiveCursor;
    private int mCount;
    private int mPosition = -1;

    public MergeCursor(Cursor[] cursors) {
        mCursors = (cursors != null) ? cursors : new Cursor[0];
        mCount = 0;
        for (Cursor c : mCursors) {
            if (c != null) mCount += c.getCount();
        }
    }

    @Override
    public String[] getColumnNames() {
        if (mCursors.length > 0 && mCursors[0] != null) {
            return mCursors[0].getColumnNames();
        }
        return new String[0];
    }

    @Override public int getCount() { return mCount; }
    @Override public int getPosition() { return mPosition; }

    @Override
    public boolean moveToPosition(int position) {
        if (position < 0 || position >= mCount) {
            mPosition = position < 0 ? -1 : mCount;
            return false;
        }
        mPosition = position;
        int remaining = position;
        for (int i = 0; i < mCursors.length; i++) {
            Cursor c = mCursors[i];
            if (c == null) continue;
            int count = c.getCount();
            if (remaining < count) {
                mActiveCursor = i;
                c.moveToPosition(remaining);
                return true;
            }
            remaining -= count;
        }
        return false;
    }

    @Override public boolean moveToFirst()    { return moveToPosition(0); }
    @Override public boolean moveToLast()      { return moveToPosition(mCount - 1); }
    @Override public boolean moveToNext()      { return moveToPosition(mPosition + 1); }
    @Override public boolean moveToPrevious()  { return moveToPosition(mPosition - 1); }
    @Override public boolean move(int offset)  { return moveToPosition(mPosition + offset); }

    @Override public boolean isBeforeFirst() { return mCount > 0 && mPosition < 0; }
    @Override public boolean isAfterLast()   { return mCount > 0 && mPosition >= mCount; }
    @Override public boolean isFirst()       { return mPosition == 0 && mCount > 0; }
    @Override public boolean isLast()        { return mPosition == mCount - 1 && mCount > 0; }

    @Override
    public int getColumnIndex(String columnName) {
        String[] cols = getColumnNames();
        for (int i = 0; i < cols.length; i++) {
            if (cols[i].equals(columnName)) return i;
        }
        return -1;
    }

    @Override public int getColumnCount() { return getColumnNames().length; }

    private Cursor active() { return mCursors[mActiveCursor]; }

    @Override public double  getDouble(int col) { return active().getDouble(col); }
    @Override public float   getFloat(int col)  { return active().getFloat(col); }
    @Override public int     getInt(int col)     { return active().getInt(col); }
    @Override public long    getLong(int col)     { return active().getLong(col); }
    @Override public short   getShort(int col)   { return active().getShort(col); }
    @Override public String  getString(int col)  { return active().getString(col); }
    @Override public boolean isNull(int col)     { return active().isNull(col); }
}
