package android.database;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

public class AbstractCursor implements CrossProcessCursor {
    public AbstractCursor() {}

    public void checkPosition() {}
    public void close() {}
    public void copyStringToBuffer(int p0, CharArrayBuffer p1) {}
    public void deactivate() {}
    public void fillWindow(int p0, CursorWindow p1) {}
    public void finalize() {}
    public byte[] getBlob(int p0) { return new byte[0]; }
    public int getColumnCount() { return 0; }
    public int getColumnIndex(String p0) { return 0; }
    public int getColumnIndexOrThrow(String p0) { return 0; }
    public String getColumnName(int p0) { return null; }
    public Bundle getExtras() { return null; }
    public Uri getNotificationUri() { return null; }
    public int getPosition() { return 0; }
    public int getType(int p0) { return 0; }
    public boolean getWantsAllOnMoveCalls() { return false; }
    public CursorWindow getWindow() { return null; }
    public boolean isAfterLast() { return false; }
    public boolean isBeforeFirst() { return false; }
    public boolean isClosed() { return false; }
    public boolean isFirst() { return false; }
    public boolean isLast() { return false; }
    public boolean move(int p0) { return false; }
    public boolean moveToFirst() { return false; }
    public boolean moveToLast() { return false; }
    public boolean moveToNext() { return false; }
    public boolean moveToPosition(int p0) { return false; }
    public boolean moveToPrevious() { return false; }
    public void onChange(boolean p0) {}
    public boolean onMove(int p0, int p1) { return false; }
    public void registerContentObserver(ContentObserver p0) {}
    public void registerDataSetObserver(DataSetObserver p0) {}
    public boolean requery() { return false; }
    public Bundle respond(Bundle p0) { return null; }
    public void setExtras(Bundle p0) {}
    public void setNotificationUri(ContentResolver p0, Uri p1) {}
    public void unregisterContentObserver(ContentObserver p0) {}
    public void unregisterDataSetObserver(DataSetObserver p0) {}
    public void setNotificationUris(ContentResolver p0, java.util.List<Object> p1) {}
    public String[] getColumnNames() { return new String[0]; }
    public int getCount() { return 0; }
    public double getDouble(int p0) { return 0.0; }
    public float getFloat(int p0) { return 0f; }
    public int getInt(int p0) { return 0; }
    public long getLong(int p0) { return 0L; }
    public short getShort(int p0) { return 0; }
    public String getString(int p0) { return null; }
    public boolean isNull(int p0) { return false; }
}
