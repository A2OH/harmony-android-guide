package android.graphics;

/**
 * Shim: android.graphics.Region
 * OH mapping: drawing.OH_Drawing_Region
 *
 * Pure Java stub — tracks an axis-aligned rectangular region.
 * Complex clipping operations (path-based regions) are not modelled;
 * set/op/contains/getBounds are implemented at the Rect level only.
 */
public class Region {

    // ── Op enum ──────────────────────────────────────────────────────────────

    public enum Op {
        DIFFERENCE,         // A - B
        INTERSECT,          // A ∩ B
        UNION,              // A ∪ B
        XOR,                // A ⊕ B  (A ∪ B minus A ∩ B)
        REVERSE_DIFFERENCE, // B - A
        REPLACE             // B
    }

    // ── State ────────────────────────────────────────────────────────────────

    private Rect    mBounds = new Rect();
    private boolean mEmpty  = true;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Region() {}

    public Region(Rect r) {
        set(r);
    }

    public Region(int left, int top, int right, int bottom) {
        set(left, top, right, bottom);
    }

    public Region(Region region) {
        if (region != null) {
            mBounds = new Rect(region.mBounds);
            mEmpty  = region.mEmpty;
        }
    }

    // ── Set ──────────────────────────────────────────────────────────────────

    public boolean set(Rect r) {
        if (r == null || r.isEmpty()) {
            markEmpty();
            return false;
        }
        mBounds.set(r);
        mEmpty = false;
        return true;
    }

    public boolean set(int left, int top, int right, int bottom) {
        if (left >= right || top >= bottom) {
            markEmpty();
            return false;
        }
        mBounds.set(left, top, right, bottom);
        mEmpty = false;
        return true;
    }

    public boolean set(Region region) {
        if (region == null) {
            markEmpty();
            return false;
        }
        mBounds.set(region.mBounds);
        mEmpty = region.mEmpty;
        return true;
    }

    public void setEmpty() {
        markEmpty();
    }

    private void markEmpty() {
        mEmpty = true;
        mBounds.set(0, 0, 0, 0);
    }

    // ── Query ────────────────────────────────────────────────────────────────

    public boolean isEmpty() { return mEmpty; }

    public boolean isRect()  { return !mEmpty; }

    /**
     * Return true if the point (x, y) is inside the region.
     */
    public boolean contains(int x, int y) {
        return !mEmpty && mBounds.contains(x, y);
    }

    /**
     * Copy the region's bounding rect into dst.
     * @return false if the region is empty
     */
    public boolean getBounds(Rect dst) {
        if (dst != null) dst.set(mBounds);
        return !mEmpty;
    }

    /**
     * Return the region's bounding rect (may be empty).
     */
    public Rect getBounds() {
        return new Rect(mBounds);
    }

    // ── Op ───────────────────────────────────────────────────────────────────

    /**
     * Perform a boolean operation between this region and the given rect.
     * This shim operates purely on bounding rects.
     */
    public boolean op(Rect r, Op op) {
        if (r == null) return false;
        return op(r.left, r.top, r.right, r.bottom, op);
    }

    public boolean op(int left, int top, int right, int bottom, Op op) {
        Rect r = new Rect(left, top, right, bottom);
        return opInternal(r, op);
    }

    public boolean op(Region region, Op op) {
        if (region == null) return false;
        return opInternal(region.mBounds, op);
    }

    public boolean op(Rect rect, Region region, Op op) {
        set(rect);
        return opInternal(region != null ? region.mBounds : new Rect(), op);
    }

    private boolean opInternal(Rect r, Op op) {
        switch (op) {
            case REPLACE:
                mBounds.set(r);
                mEmpty = r.isEmpty();
                break;
            case UNION: {
                if (mEmpty) {
                    mBounds.set(r);
                    mEmpty = r.isEmpty();
                } else if (!r.isEmpty()) {
                    mBounds.union(r);
                }
                break;
            }
            case INTERSECT: {
                if (mEmpty || r.isEmpty()) {
                    markEmpty();
                } else {
                    boolean intersects = mBounds.intersect(r);
                    mEmpty = !intersects;
                    if (mEmpty) mBounds.set(0, 0, 0, 0);
                }
                break;
            }
            case DIFFERENCE: {
                // Bounding-rect approximation: if r fully covers our bounds, become empty.
                if (!mEmpty && !r.isEmpty()
                        && r.contains(mBounds.left, mBounds.top, mBounds.right, mBounds.bottom)) {
                    markEmpty();
                }
                break;
            }
            case REVERSE_DIFFERENCE: {
                if (!r.isEmpty()
                        && mBounds.contains(r.left, r.top, r.right, r.bottom)) {
                    markEmpty();
                } else {
                    mBounds.set(r);
                    mEmpty = r.isEmpty();
                }
                break;
            }
            case XOR: {
                // Approximate: use the union (conservative; perfect XOR requires multi-rect repr.)
                Rect union = new Rect(mBounds);
                union.union(r);
                mBounds.set(union);
                mEmpty = union.isEmpty();
                break;
            }
        }
        return !mEmpty;
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return mEmpty ? "Region(empty)" : "Region(" + mBounds + ")";
    }
}
