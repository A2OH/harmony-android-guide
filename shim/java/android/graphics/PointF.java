package android.graphics;

/**
 * Shim: android.graphics.PointF
 * Pure Java — no OHBridge calls.
 */
public class PointF {

    public float x, y;

    // ── Constructors ─────────────────────────────────────────────────────────

    public PointF() {}

    public PointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PointF(PointF p) {
        if (p != null) {
            this.x = p.x;
            this.y = p.y;
        }
    }

    // ── Setter ───────────────────────────────────────────────────────────────

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // ── Distance from origin ─────────────────────────────────────────────────

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PointF)) return false;
        PointF p = (PointF) o;
        return Float.compare(x, p.x) == 0 && Float.compare(y, p.y) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * Float.floatToIntBits(x) + Float.floatToIntBits(y);
    }

    @Override
    public String toString() {
        return "PointF(" + x + ", " + y + ")";
    }
}
