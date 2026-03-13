package android.database;

import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: Cursor implementation backed by OH ResultSet via JNI bridge.
 */
public class CursorWrapper implements Cursor {
    private long handle;
    private int position = -1;
    private int count = -1;
    private boolean closed = false;

    public CursorWrapper(long resultSetHandle) {
        this.handle = resultSetHandle;
    }

    @Override
    public boolean moveToFirst() {
        boolean ok = OHBridge.resultSetGoToFirstRow(handle);
        position = ok ? 0 : -1;
        return ok;
    }

    @Override
    public boolean moveToNext() {
        boolean ok = OHBridge.resultSetGoToNextRow(handle);
        if (ok) position++;
        return ok;
    }

    @Override
    public boolean moveToPosition(int pos) {
        // Reset to first, then advance
        if (!moveToFirst()) return false;
        for (int i = 0; i < pos; i++) {
            if (!moveToNext()) return false;
        }
        return true;
    }

    @Override
    public boolean moveToPrevious() {
        if (position <= 0) return false;
        return moveToPosition(position - 1);
    }

    @Override
    public boolean moveToLast() {
        return moveToPosition(getCount() - 1);
    }

    @Override
    public boolean isAfterLast() {
        return position >= getCount();
    }

    @Override
    public boolean isBeforeFirst() {
        return position < 0;
    }

    @Override
    public boolean isFirst() {
        return position == 0;
    }

    @Override
    public boolean isLast() {
        return position == getCount() - 1;
    }

    @Override
    public int getPosition() { return position; }

    @Override
    public int getCount() {
        if (count < 0) {
            count = OHBridge.resultSetGetRowCount(handle);
        }
        return count;
    }

    @Override
    public int getColumnCount() {
        return OHBridge.resultSetGetColumnCount(handle);
    }

    @Override
    public int getColumnIndex(String columnName) {
        return OHBridge.resultSetGetColumnIndex(handle, columnName);
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        int idx = getColumnIndex(columnName);
        if (idx < 0) throw new IllegalArgumentException("column '" + columnName + "' does not exist");
        return idx;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return OHBridge.resultSetGetColumnName(handle, columnIndex);
    }

    @Override
    public String[] getColumnNames() {
        int cols = getColumnCount();
        String[] names = new String[cols];
        for (int i = 0; i < cols; i++) {
            names[i] = getColumnName(i);
        }
        return names;
    }

    @Override
    public String getString(int columnIndex) {
        return OHBridge.resultSetGetString(handle, columnIndex);
    }

    @Override
    public int getInt(int columnIndex) {
        return OHBridge.resultSetGetInt(handle, columnIndex);
    }

    @Override
    public long getLong(int columnIndex) {
        return OHBridge.resultSetGetLong(handle, columnIndex);
    }

    @Override
    public float getFloat(int columnIndex) {
        return OHBridge.resultSetGetFloat(handle, columnIndex);
    }

    @Override
    public double getDouble(int columnIndex) {
        return OHBridge.resultSetGetDouble(handle, columnIndex);
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        return OHBridge.resultSetGetBlob(handle, columnIndex);
    }

    @Override
    public boolean isNull(int columnIndex) {
        return OHBridge.resultSetIsNull(handle, columnIndex);
    }

    @Override
    public short getShort(int columnIndex) {
        return (short) getInt(columnIndex);
    }

    @Override
    public int getType(int columnIndex) {
        return isNull(columnIndex) ? FIELD_TYPE_NULL : FIELD_TYPE_STRING;
    }

    @Override
    public boolean move(int offset) {
        return moveToPosition(position + offset);
    }

    @Override
    public boolean getWantsAllOnMoveCalls() { return false; }

    @Override
    public void registerContentObserver(ContentObserver observer) {}
    @Override
    public void unregisterContentObserver(ContentObserver observer) {}
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {}
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {}

    @Override
    public void setNotificationUri(android.content.ContentResolver cr, android.net.Uri uri) {}
    @Override
    public android.net.Uri getNotificationUri() { return null; }

    @Override
    public void setExtras(android.os.Bundle extras) {}
    @Override
    public android.os.Bundle getExtras() { return android.os.Bundle.EMPTY; }
    @Override
    public android.os.Bundle respond(android.os.Bundle extras) { return android.os.Bundle.EMPTY; }

    @Override
    public void close() {
        if (!closed) {
            OHBridge.resultSetClose(handle);
            closed = true;
        }
    }

    @Override
    public boolean isClosed() { return closed; }
}
