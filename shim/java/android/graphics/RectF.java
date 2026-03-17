package android.graphics;

import android.os.Parcel;

/**
 * Shim: android.graphics.RectF
 * Pure Java — no OHBridge calls.
 */
public class RectF {

    public float left, top, right, bottom;

    // ── Constructors ─────────────────────────────────────────────────────────

    public RectF() {}

    public RectF(float left, float top, float right, float bottom) {
        this.left   = left;
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;
    }

    public RectF(RectF r) {
        if (r != null) {
            this.left   = r.left;
            this.top    = r.top;
            this.right  = r.right;
            this.bottom = r.bottom;
        }
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

    public final float width()  { return right  - left; }
    public final float height() { return bottom - top;  }

    public final float centerX() { return (left + right)  / 2.0f; }
    public final float centerY() { return (top  + bottom) / 2.0f; }

    public final float exactCenterX() { return (left + right) / 2.0f; }
    public final float exactCenterY() { return (top + bottom) / 2.0f; }

    // ── State ────────────────────────────────────────────────────────────────

    public final boolean isEmpty() {
        return left >= right || top >= bottom;
    }

    public void setEmpty() {
        left = right = top = bottom = 0f;
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
        return left < right && top < bottom
            && left <= l && top <= t && right >= r && bottom >= b;
    }

    public boolean contains(RectF r) {
        return contains(r.left, r.top, r.right, r.bottom);
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

    public boolean setIntersect(RectF a, RectF b) {
        float newLeft   = Math.max(a.left,   b.left);
        float newTop    = Math.max(a.top,    b.top);
        float newRight  = Math.min(a.right,  b.right);
        float newBottom = Math.min(a.bottom, b.bottom);
        if (newLeft < newRight && newTop < newBottom) {
            left   = newLeft;
            top    = newTop;
            right  = newRight;
            bottom = newBottom;
            return true;
        }
        return false;
    }

    public boolean intersects(float l, float t, float r, float b) {
        return left < r && l < right && top < b && t < bottom;
    }

    public static boolean intersects(RectF a, RectF b) {
        return a.left < b.right && b.left < a.right
            && a.top < b.bottom && b.top < a.bottom;
    }

    public void union(float x, float y) {
        if (x < left)   left   = x;
        if (x > right)  right  = x;
        if (y < top)    top    = y;
        if (y > bottom) bottom = y;
    }

    public void union(RectF r) {
        union(r.left, r.top, r.right, r.bottom);
    }

    public void union(float l, float t, float r, float b) {
        if (l < left)   left   = l;
        if (t < top)    top    = t;
        if (r > right)  right  = r;
        if (b > bottom) bottom = b;
    }

    public void offset(float dx, float dy) {
        left   += dx;
        top    += dy;
        right  += dx;
        bottom += dy;
    }

    public void offsetTo(float newLeft, float newTop) {
        right  += newLeft - left;
        bottom += newTop - top;
        left   = newLeft;
        top    = newTop;
    }

    public void inset(float dx, float dy) {
        left   += dx;
        top    += dy;
        right  -= dx;
        bottom -= dy;
    }

    public void sort() {
        if (left > right) { float t = left; left = right; right = t; }
        if (top > bottom) { float t = top; top = bottom; bottom = t; }
    }

    // ── Rounding ─────────────────────────────────────────────────────────────

    public void round(Rect dst) {
        dst.set(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
    }

    public void roundOut(Rect dst) {
        dst.set((int) Math.floor(left), (int) Math.floor(top),
                (int) Math.ceil(right), (int) Math.ceil(bottom));
    }

    // ── Parcel ───────────────────────────────────────────────────────────────

    public void readFromParcel(Parcel in) {
        left   = in.readFloat();
        top    = in.readFloat();
        right  = in.readFloat();
        bottom = in.readFloat();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(left);
        out.writeFloat(top);
        out.writeFloat(right);
        out.writeFloat(bottom);
    }

    // ── String conversion ────────────────────────────────────────────────────

    public String toShortString() {
        return "[" + left + "," + top + "][" + right + "," + bottom + "]";
    }

    public String flattenToString() {
        return left + " " + top + " " + right + " " + bottom;
    }

    public static RectF unflattenFromString(String str) {
        if (str == null) return null;
        String[] parts = splitByChar(str, ' ');
        if (parts.length != 4) return null;
        try {
            return new RectF(
                Float.parseFloat(parts[0]),
                Float.parseFloat(parts[1]),
                Float.parseFloat(parts[2]),
                Float.parseFloat(parts[3])
            );
        } catch (NumberFormatException e) {
            return null;
        }
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

    @Override
    public String toString() {
        return "RectF(" + left + ", " + top + " - " + right + ", " + bottom + ")";
    }
}
