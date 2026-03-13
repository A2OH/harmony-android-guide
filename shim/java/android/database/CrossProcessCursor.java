package android.database;

public interface CrossProcessCursor extends Cursor {
    void fillWindow(int p0, CursorWindow p1);
    CursorWindow getWindow();
    boolean onMove(int p0, int p1);
}
