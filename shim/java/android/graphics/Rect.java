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

    public void setEmpty() {
        left = top = right = bottom = 0;
    }

    // ── Hit-test ─────────────────────────────────────────────────────────────

    public boolean contains(int x, int y) {
        return left < right && top < bottom
            && x >= left && x < right
            && y >= top  && y < bottom;
    }

    public boolean contains(int l, int t, int r, int b) {
        return this.left < this.right && this.top < this.bottom
            && this.left <= l && this.top <= t
            && this.right >= r && this.bottom >= b;
    }

    public boolean contains(Rect r) {
        return this.left < this.right && this.top < this.bottom
            && this.left <= r.left && this.top <= r.top
            && this.right >= r.right && this.bottom >= r.bottom;
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

    public boolean setIntersect(Rect a, Rect b) {
        int newLeft   = Math.max(a.left,   b.left);
        int newTop    = Math.max(a.top,    b.top);
        int newRight  = Math.min(a.right,  b.right);
        int newBottom = Math.min(a.bottom, b.bottom);
        if (newLeft < newRight && newTop < newBottom) {
            left   = newLeft;
            top    = newTop;
            right  = newRight;
            bottom = newBottom;
            return true;
        }
        return false;
    }

    public static boolean intersects(Rect a, Rect b) {
        return a.left < b.right && b.left < a.right
            && a.top < b.bottom && b.top < a.bottom;
    }

    public boolean intersects(int l, int t, int r, int b) {
        return this.left < r && l < this.right
            && this.top < b && t < this.bottom;
    }

    public void union(int l, int t, int r, int b) {
        if ((l < r) && (t < b)) {
            if (isEmpty()) {
                left = l; top = t; right = r; bottom = b;
            } else {
                if (l < left)   left   = l;
                if (t < top)    top    = t;
                if (r > right)  right  = r;
                if (b > bottom) bottom = b;
            }
        }
    }

    public void union(Rect r) {
        union(r.left, r.top, r.right, r.bottom);
    }

    public void union(int x, int y) {
        if (isEmpty()) {
            left = x; top = y; right = x; bottom = y;
        } else {
            if (x < left)   left   = x;
            if (x > right)  right  = x;
            if (y < top)    top    = y;
            if (y > bottom) bottom = y;
        }
    }

    public void offset(int dx, int dy) {
        left   += dx;
        top    += dy;
        right  += dx;
        bottom += dy;
    }

    public void offsetTo(int newLeft, int newTop) {
        right  += newLeft - left;
        bottom += newTop  - top;
        left    = newLeft;
        top     = newTop;
    }

    public void inset(int dx, int dy) {
        left   += dx;
        top    += dy;
        right  -= dx;
        bottom -= dy;
    }

    public void scale(float scale) {
        if (scale != 1.0f) {
            left = (int) (left * scale + 0.5f);
            top = (int) (top * scale + 0.5f);
            right = (int) (right * scale + 0.5f);
            bottom = (int) (bottom * scale + 0.5f);
        }
    }

    // ── Flatten / unflatten ──────────────────────────────────────────────────

    public String flattenToString() {
        return left + " " + top + " " + right + " " + bottom;
    }

    public static Rect unflattenFromString(String str) {
        if (str == null) return null;
        String[] parts = splitByChar(str, ' ');
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

    public String toShortString() {
        return "[" + left + "," + top + "][" + right + "," + bottom + "]";
    }

    public void sort() {
        if (left > right) { int t = left; left = right; right = t; }
        if (top > bottom) { int t = top; top = bottom; bottom = t; }
    }

    private static String[] splitByChar(String s, char delim) {
        java.util.ArrayList<String> parts = new java.util.ArrayList<>();
        int start = 0;
        for (int i = 0; i <= s.length(); i++) {
            if (i == s.length() || s.charAt(i) == delim) {
                parts.add(s.substring(start, i));
                start = i + 1;
            }
        }
        return parts.toArray(new String[0]);
    }
}
