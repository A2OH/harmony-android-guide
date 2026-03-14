package android.graphics;

public final class Insets {
    public static final Insets NONE = new Insets(0, 0, 0, 0);

    public final int left;
    public final int top;
    public final int right;
    public final int bottom;

    private Insets(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /** @deprecated Use {@link #of(int, int, int, int)} instead. */
    public Insets() {
        this(0, 0, 0, 0);
    }

    public static Insets of(int left, int top, int right, int bottom) {
        if (left == 0 && top == 0 && right == 0 && bottom == 0) {
            return NONE;
        }
        return new Insets(left, top, right, bottom);
    }

    public static Insets of(Rect r) {
        if (r == null) return NONE;
        return of(r.left, r.top, r.right, r.bottom);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Insets)) return false;
        Insets insets = (Insets) o;
        return left == insets.left && top == insets.top
                && right == insets.right && bottom == insets.bottom;
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
                + ", right=" + right + ", bottom=" + bottom + "}";
    }

    public int describeContents() { return 0; }
    public void writeToParcel(Object p0, Object p1) {}
}
