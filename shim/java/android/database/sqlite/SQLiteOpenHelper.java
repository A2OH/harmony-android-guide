package android.database.sqlite;
import android.content.Context;
import android.database.DatabaseErrorHandler;

public class SQLiteOpenHelper implements AutoCloseable {
    public SQLiteOpenHelper(Context p0, String p1, Object p2, int p3) {}
    public SQLiteOpenHelper(Context p0, String p1, Object p2, int p3, DatabaseErrorHandler p4) {}
    public SQLiteOpenHelper(Context p0, String p1, int p2, Object p3) {}

    public void close() {}
    public String getDatabaseName() { return null; }
    public SQLiteDatabase getReadableDatabase() { return null; }
    public SQLiteDatabase getWritableDatabase() { return null; }
    public void onConfigure(SQLiteDatabase p0) {}
    public void onCreate(SQLiteDatabase p0) {}
    public void onDowngrade(SQLiteDatabase p0, int p1, int p2) {}
    public void onOpen(SQLiteDatabase p0) {}
    public void onUpgrade(SQLiteDatabase p0, int p1, int p2) {}
    public void setLookasideConfig(int p0, int p1) {}
    public void setOpenParams(Object p0) {}
    public void setWriteAheadLoggingEnabled(boolean p0) {}
}
