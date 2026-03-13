package android.database.sqlite;
import android.database.Cursor;

public interface SQLiteCursorDriver {
    void cursorClosed();
    void cursorDeactivated();
    void cursorRequeried(Cursor p0);
    Cursor query(Object p0, String[] p1);
    void setBindArguments(String[] p0);
}
