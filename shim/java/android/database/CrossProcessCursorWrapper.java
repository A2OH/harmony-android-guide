package android.database;

public class CrossProcessCursorWrapper extends CursorWrapper implements CrossProcessCursor {
    public CrossProcessCursorWrapper(Cursor p0) {}

    public void fillWindow(int p0, CursorWindow p1) {}
    public CursorWindow getWindow() { return null; }
    public boolean onMove(int p0, int p1) { return false; }
    public void setNotificationUris(android.content.ContentResolver p0, java.util.List<Object> p1) {}
}
