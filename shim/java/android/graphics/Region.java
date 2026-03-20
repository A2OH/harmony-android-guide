package android.graphics;

public class Region {
    public Region() {}
    public Region(Rect r) {}
    public Region(Region region) {}
    public Region(int left, int top, int right, int bottom) {}

    public boolean contains(int x, int y) { return false; }
    public int describeContents() { return 0; }
    public boolean getBoundaryPath(Path path) { return false; }
    public boolean getBounds(Rect r) { return false; }
    public boolean isComplex() { return false; }
    public boolean isEmpty() { return false; }
    public boolean isRect() { return false; }
    public boolean op(Rect r, Op op) { return false; }
    public boolean op(int left, int top, int right, int bottom, Op op) { return false; }
    public boolean op(Region region, Op op) { return false; }
    public boolean quickContains(Rect r) { return false; }
    public boolean quickContains(int left, int top, int right, int bottom) { return false; }
    public boolean quickReject(Rect r) { return false; }
    public boolean quickReject(int left, int top, int right, int bottom) { return false; }
    public boolean set(Region region) { return false; }
    public boolean set(int left, int top, int right, int bottom) { return false; }
    public void setEmpty() {}
    public boolean setPath(Path path, Region clip) { return false; }
    public void translate(int dx, int dy) {}
    public void translate(int dx, int dy, Region dst) {}
    public boolean union(Rect r) { return false; }
    public void writeToParcel(Object p0, int flags) {}

    public enum Op {
        DIFFERENCE,
        INTERSECT,
        UNION,
        XOR,
        REVERSE_DIFFERENCE,
        REPLACE
    }
}
