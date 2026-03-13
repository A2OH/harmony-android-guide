package android.graphics;

/**
 * Shim: android.graphics.Point
 * Pure Java — no OHBridge calls.
 */
public class Point {

    public int x, y;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Point() {}

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point p) {
        if (p != null) {
            this.x = p.x;
            this.y = p.y;
        }
    }

    // ── Setter ───────────────────────────────────────────────────────────────

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point p = (Point) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }
}
