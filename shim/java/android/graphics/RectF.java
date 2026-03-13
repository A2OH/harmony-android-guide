package android.graphics;

/**
 * Shim: android.graphics.RectF
 * Pure Java — no OHBridge calls.
 */
public class RectF {
    public RectF(Object... args) {}
    public RectF(int left, int top, int right) {}

    public float left, top, right, bottom;

    // ── Constructors ─────────────────────────────────────────────────────────

    public RectF() {}

    public RectF(float left, float top, float right, float bottom) {
        this.left   = left;
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;
    }

    public RectF(Rect r) {
        if (r != null) {
            this.left   = r.left;
            this.top    = r.top;
            this.right  = r.right;
            this.bottom = r.bottom;
        }
    }

    // ── Dimensions ───────────────────────────────────────────────────────────

    public float width()  { return right  - left; }
    public float height() { return bottom - top;  }

    public float centerX() { return (left + right)  / 2.0f; }
    public float centerY() { return (top  + bottom) / 2.0f; }

    public float exactCenterX() { return centerX(); }
    public float exactCenterY() { return centerY(); }

    // ── State ────────────────────────────────────────────────────────────────

    public boolean isEmpty() {
        return left >= right || top >= bottom;
    }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void set(float left, float top, float right, float bottom) {
        this.left   = left;
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;
    }

    public void set(RectF src) {
        this.left   = src.left;
        this.top    = src.top;
        this.right  = src.right;
        this.bottom = src.bottom;
    }

    public void set(Rect src) {
        this.left   = src.left;
        this.top    = src.top;
        this.right  = src.right;
        this.bottom = src.bottom;
    }

    // ── Hit-test ─────────────────────────────────────────────────────────────

    public boolean contains(float x, float y) {
        return left < right && top < bottom
            && x >= left && x < right
            && y >= top  && y < bottom;
    }

    public boolean contains(float l, float t, float r, float b) {
        return left <= l && top <= t && right >= r && bottom >= b;
    }

    // ── Geometry ─────────────────────────────────────────────────────────────

    public boolean intersect(RectF r) {
        return intersect(r.left, r.top, r.right, r.bottom);
    }

    public boolean intersect(float l, float t, float r, float b) {
        float newLeft   = Math.max(left,   l);
        float newTop    = Math.max(top,    t);
        float newRight  = Math.min(right,  r);
        float newBottom = Math.min(bottom, b);
        if (newLeft < newRight && newTop < newBottom) {
            left   = newLeft;
            top    = newTop;
            right  = newRight;
            bottom = newBottom;
            return true;
        }
        return false;
    }

    public void union(float x, float y) {
        if (x < left)   left   = x;
        if (x > right)  right  = x;
        if (y < top)    top    = y;
        if (y > bottom) bottom = y;
    }

    public void union(RectF r) {
        if (r.left   < left)   left   = r.left;
        if (r.top    < top)    top    = r.top;
        if (r.right  > right)  right  = r.right;
        if (r.bottom > bottom) bottom = r.bottom;
    }

    public void offset(float dx, float dy) {
        left   += dx;
        top    += dy;
        right  += dx;
        bottom += dy;
    }

    public void inset(float dx, float dy) {
        left   += dx;
        top    += dy;
        right  -= dx;
        bottom -= dy;
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RectF)) return false;
        RectF r = (RectF) o;
        return Float.compare(left,   r.left)   == 0
            && Float.compare(top,    r.top)    == 0
            && Float.compare(right,  r.right)  == 0
            && Float.compare(bottom, r.bottom) == 0;
    }

    @Override
    public int hashCode() {
        int result = Float.floatToIntBits(left);
        result = 31 * result + Float.floatToIntBits(top);
        result = 31 * result + Float.floatToIntBits(right);
        result = 31 * result + Float.floatToIntBits(bottom);
        return result;
    }

    @Override
    public String toString() {
        return "RectF(" + left + ", " + top + " - " + right + ", " + bottom + ")";
    }
}
