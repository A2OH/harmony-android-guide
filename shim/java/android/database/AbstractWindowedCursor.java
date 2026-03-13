package android.database;

public class AbstractWindowedCursor extends AbstractCursor {
    public int mWindow = 0;

    public AbstractWindowedCursor() {}

    public double getDouble(int p0) { return 0.0; }
    public float getFloat(int p0) { return 0f; }
    public int getInt(int p0) { return 0; }
    public long getLong(int p0) { return 0L; }
    public short getShort(int p0) { return 0; }
    public String getString(int p0) { return null; }
    public boolean hasWindow() { return false; }
    public boolean isNull(int p0) { return false; }
    public void setWindow(CursorWindow p0) {}
}
