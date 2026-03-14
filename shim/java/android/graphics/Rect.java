package android.graphics;

/**
 * Shim: android.graphics.Rect
 * Pure Java — no OHBridge calls.
 */
public class Rect {

    public int left, top, right, bottom;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Rect() {}

    public Rect(int left, int top, int right, int bottom) {
        this.left   = left;
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;
    }

    public Rect(Rect r) {
        if (r != null) {
            this.left   = r.left;
            this.top    = r.top;
            this.right  = r.right;
            this.bottom = r.bottom;
        }
    }

    // ── Dimensions ───────────────────────────────────────────────────────────

    public int width()  { return right  - left; }
    public int height() { return bottom - top;  }

    public int centerX() { return (left + right)  >> 1; }
    public int centerY() { return (top  + bottom) >> 1; }

    public float exactCenterX() { return (left + right)  / 2.0f; }
    public float exactCenterY() { return (top  + bottom) / 2.0f; }

    // ── State ────────────────────────────────────────────────────────────────

    public boolean isEmpty() {
        return left >= right || top >= bottom;
    }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void set(int left, int top, int right, int bottom) {
        this.left   = left;
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;
    }

    public void set(Rect src) {
        this.left   = src.left;
        this.top    = src.top;
        this.right  = src.right;
        this.bottom = src.bottom;
    }

    // ── Hit-test ─────────────────────────────────────────────────────────────

    public boolean contains(int x, int y) {
        return left < right && top < bottom
            && x >= left && x < right
            && y >= top  && y < bottom;
    }

    public boolean contains(int l, int t, int r, int b) {
        return left <= l && top <= t && right >= r && bottom >= b;
    }

    // ── Geometry ─────────────────────────────────────────────────────────────

    public boolean intersect(Rect r) {
        return intersect(r.left, r.top, r.right, r.bottom);
    }

    public boolean intersect(int l, int t, int r, int b) {
        int newLeft   = Math.max(left,   l);
        int newTop    = Math.max(top,    t);
        int newRight  = Math.min(right,  r);
        int newBottom = Math.min(bottom, b);
        if (newLeft < newRight && newTop < newBottom) {
            left   = newLeft;
            top    = newTop;
            right  = newRight;
            bottom = newBottom;
            return true;
        }
        return false;
    }

    public void union(int x, int y) {
        if (x < left)   left   = x;
        if (x > right)  right  = x;
        if (y < top)    top    = y;
        if (y > bottom) bottom = y;
    }

    public void union(Rect r) {
        if (r.left   < left)   left   = r.left;
        if (r.top    < top)    top    = r.top;
        if (r.right  > right)  right  = r.right;
        if (r.bottom > bottom) bottom = r.bottom;
    }

    public void offset(int dx, int dy) {
        left   += dx;
        top    += dy;
        right  += dx;
        bottom += dy;
    }

    public void inset(int dx, int dy) {
        left   += dx;
        top    += dy;
        right  -= dx;
        bottom -= dy;
    }

    // ── Flatten / unflatten ──────────────────────────────────────────────────

    public String flattenToString() {
        return left + " " + top + " " + right + " " + bottom;
    }

    public static Rect unflattenFromString(String str) {
        if (str == null) return null;
        String[] parts = str.split(" ");
        if (parts.length != 4) return null;
        try {
            return new Rect(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3])
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rect)) return false;
        Rect r = (Rect) o;
        return left == r.left && top == r.top
            && right == r.right && bottom == r.bottom;
    }

    @Override
    public int hashCode() {
        int result = left;
        result = 31 * result + top;
        result = 31 * result + right;
        result = 31 * result + bottom;
        return result;
    }

    @Override
    public String toString() {
        return "Rect(" + left + ", " + top + " - " + right + ", " + bottom + ")";
    }
}
