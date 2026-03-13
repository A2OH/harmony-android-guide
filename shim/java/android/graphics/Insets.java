package android.graphics;

/**
 * Shim: android.graphics.Insets
 * OH mapping: No direct equivalent; layout insets are expressed via
 * component padding attributes in ArkUI.
 *
 * An immutable value type that describes the distances between a rectangle and
 * its container along each of the four edges. Mirrors the Android API exactly:
 * the four fields are public, final, and non-negative by convention.
 */
public final class Insets {

    // -------------------------------------------------------------------------
    // Constant
    // -------------------------------------------------------------------------

    /** Insets with zero on all sides. */
    public static final Insets NONE = new Insets(0, 0, 0, 0);

    // -------------------------------------------------------------------------
    // Public fields (immutable — final)
    // -------------------------------------------------------------------------

    /** Distance from the left edge. */
    public final int left;
    /** Distance from the top edge. */
    public final int top;
    /** Distance from the right edge. */
    public final int right;
    /** Distance from the bottom edge. */
    public final int bottom;

    // -------------------------------------------------------------------------
    // Constructor (private — use factory)
    // -------------------------------------------------------------------------

    private Insets(int left, int top, int right, int bottom) {
        this.left   = left;
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;
    }

    // -------------------------------------------------------------------------
    // Factory
    // -------------------------------------------------------------------------

    /**
     * Return an Insets instance with the given edge values.
     *
     * @param left   inset from the left
     * @param top    inset from the top
     * @param right  inset from the right
     * @param bottom inset from the bottom
     */
    public static Insets of(int left, int top, int right, int bottom) {
        if (left == 0 && top == 0 && right == 0 && bottom == 0) return NONE;
        return new Insets(left, top, right, bottom);
    }

    /**
     * Return an Insets instance derived from the given {@link Rect}.
     * The rect's edges map directly to left/top/right/bottom.
     */
    public static Insets of(Rect r) {
        return of(r.left, r.top, r.right, r.bottom);
    }

    // -------------------------------------------------------------------------
    // Arithmetic helpers (present in Android API)
    // -------------------------------------------------------------------------

    /** Returns a new Insets with each component negated. */
    public static Insets subtract(Insets a, Insets b) {
        return of(a.left - b.left, a.top - b.top, a.right - b.right, a.bottom - b.bottom);
    }

    /** Returns a new Insets that is the component-wise maximum of a and b. */
    public static Insets max(Insets a, Insets b) {
        return of(Math.max(a.left, b.left), Math.max(a.top, b.top),
                  Math.max(a.right, b.right), Math.max(a.bottom, b.bottom));
    }

    /** Returns a new Insets that is the component-wise minimum of a and b. */
    public static Insets min(Insets a, Insets b) {
        return of(Math.min(a.left, b.left), Math.min(a.top, b.top),
                  Math.min(a.right, b.right), Math.min(a.bottom, b.bottom));
    }

    /** Returns a new Insets that is the component-wise sum of a and b. */
    public static Insets add(Insets a, Insets b) {
        return of(a.left + b.left, a.top + b.top, a.right + b.right, a.bottom + b.bottom);
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Insets)) return false;
        Insets other = (Insets) o;
        return left == other.left && top == other.top
                && right == other.right && bottom == other.bottom;
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
        return "Insets{left=" + left + ", top=" + top
                + ", right=" + right + ", bottom=" + bottom + '}';
    }
}
