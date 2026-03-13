package android.database;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

public class CursorWrapper implements Cursor {
    public CursorWrapper(android.database.Cursor cursor) {}
    public CursorWrapper() {}

    public void close() {}
    public void copyStringToBuffer(int p0, CharArrayBuffer p1) {}
    public byte[] getBlob(int p0) { return new byte[0]; }
    public int getColumnCount() { return 0; }
    public int getColumnIndex(String p0) { return 0; }
    public int getColumnIndexOrThrow(String p0) { return 0; }
    public String getColumnName(int p0) { return null; }
    public String[] getColumnNames() { return null; }
    public int getCount() { return 0; }
    public double getDouble(int p0) { return 0.0; }
    public Bundle getExtras() { return null; }
    public float getFloat(int p0) { return 0f; }
    public int getInt(int p0) { return 0; }
    public long getLong(int p0) { return 0L; }
    public Uri getNotificationUri() { return null; }
    public int getPosition() { return 0; }
    public short getShort(int p0) { return 0; }
    public String getString(int p0) { return null; }
    public int getType(int p0) { return 0; }
    public boolean getWantsAllOnMoveCalls() { return false; }
    public Cursor getWrappedCursor() { return null; }
    public boolean isAfterLast() { return false; }
    public boolean isBeforeFirst() { return false; }
    public boolean isClosed() { return false; }
    public boolean isFirst() { return false; }
    public boolean isLast() { return false; }
    public boolean isNull(int p0) { return false; }
    public boolean move(int p0) { return false; }
    public boolean moveToFirst() { return false; }
    public boolean moveToLast() { return false; }
    public boolean moveToNext() { return false; }
    public boolean moveToPosition(int p0) { return false; }
    public boolean moveToPrevious() { return false; }
    public void registerContentObserver(ContentObserver p0) {}
    public void registerDataSetObserver(DataSetObserver p0) {}
    public Bundle respond(Bundle p0) { return null; }
    public void setExtras(Bundle p0) {}
    public void setNotificationUri(ContentResolver p0, Uri p1) {}
    public void unregisterContentObserver(ContentObserver p0) {}
    public void unregisterDataSetObserver(DataSetObserver p0) {}
    public void setNotificationUris(ContentResolver p0, java.util.List<Object> p1) {}
}
