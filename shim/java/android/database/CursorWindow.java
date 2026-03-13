package android.database;
import android.database.sqlite.SQLiteClosable;
import android.os.Parcel;
import android.os.Parcelable;
import android.database.sqlite.SQLiteClosable;
import android.os.Parcel;
import android.os.Parcelable;

public class CursorWindow extends SQLiteClosable implements Parcelable {
    public CursorWindow(String p0) {}
    public CursorWindow(String p0, long p1) {}

    public boolean allocRow() { return false; }
    public void clear() {}
    public void copyStringToBuffer(int p0, int p1, CharArrayBuffer p2) {}
    public int describeContents() { return 0; }
    public void freeLastRow() {}
    public byte[] getBlob(int p0, int p1) { return new byte[0]; }
    public double getDouble(int p0, int p1) { return 0.0; }
    public float getFloat(int p0, int p1) { return 0f; }
    public int getInt(int p0, int p1) { return 0; }
    public long getLong(int p0, int p1) { return 0L; }
    public int getNumRows() { return 0; }
    public short getShort(int p0, int p1) { return 0; }
    public int getStartPosition() { return 0; }
    public String getString(int p0, int p1) { return null; }
    public int getType(int p0, int p1) { return 0; }
    public static CursorWindow newFromParcel(Parcel p0) { return null; }
    public void onAllReferencesReleased() {}
    public boolean putBlob(byte[] p0, int p1, int p2) { return false; }
    public boolean putDouble(double p0, int p1, int p2) { return false; }
    public boolean putLong(long p0, int p1, int p2) { return false; }
    public boolean putNull(int p0, int p1) { return false; }
    public boolean putString(String p0, int p1, int p2) { return false; }
    public boolean setNumColumns(int p0) { return false; }
    public void setStartPosition(int p0) {}
    public void writeToParcel(Parcel p0, int p1) {}
}
