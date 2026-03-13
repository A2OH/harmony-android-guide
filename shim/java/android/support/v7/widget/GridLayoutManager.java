package android.support.v7.widget;

/**
 * Shim: android.support.v7.widget.GridLayoutManager
 *
 * Extends LinearLayoutManager; places items in a 2-D grid. The span count
 * determines how many columns (or rows in HORIZONTAL orientation) exist.
 * All layout logic is stubbed — the shim only models state.
 */
public class GridLayoutManager extends LinearLayoutManager {

    private int mSpanCount;
    private SpanSizeLookup mSpanSizeLookup = new DefaultSpanSizeLookup();

    /**
     * Creates a vertical GridLayoutManager.
     *
     * @param context   context-like object (ignored in shim)
     * @param spanCount number of columns in the grid
     */
    public GridLayoutManager(Object context, int spanCount) {
        super(context, VERTICAL, false);
        setSpanCount(spanCount);
    }

    /**
     * Creates a GridLayoutManager with a specific orientation.
     *
     * @param context        context-like object (ignored in shim)
     * @param spanCount      number of spans
     * @param orientation    {@link #HORIZONTAL} or {@link #VERTICAL}
     * @param reverseLayout  {@code true} to reverse the layout direction
     */
    public GridLayoutManager(Object context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        setSpanCount(spanCount);
    }

    // ── Span count ──

    public void setSpanCount(int spanCount) {
        if (spanCount < 1) {
            throw new IllegalArgumentException("Span count should be at least 1. Provided " + spanCount);
        }
        this.mSpanCount = spanCount;
    }

    public int getSpanCount() {
        return mSpanCount;
    }

    // ── SpanSizeLookup ──

    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup != null ? spanSizeLookup : new DefaultSpanSizeLookup();
    }

    public SpanSizeLookup getSpanSizeLookup() {
        return mSpanSizeLookup;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SpanSizeLookup
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * A helper class to provide the number of spans each item occupies.
     */
    public static abstract class SpanSizeLookup {

        private boolean mCacheSpanIndices = false;

        /**
         * Returns the number of span occupied by the item at {@code position}.
         *
         * @param position adapter position of the item
         * @return number of spans occupied (1..spanCount)
         */
        public abstract int getSpanSize(int position);

        public int getSpanIndex(int position, int spanCount) {
            return position % spanCount;
        }

        public int getSpanGroupIndex(int adapterPosition, int spanCount) {
            return adapterPosition / spanCount;
        }

        public void setSpanIndexCacheEnabled(boolean cacheSpanIndices) {
            this.mCacheSpanIndices = cacheSpanIndices;
        }

        public boolean isSpanIndexCacheEnabled() {
            return mCacheSpanIndices;
        }

        public void invalidateSpanIndexCache() {
            // Stub — no cache to clear
        }
    }

    /** Default lookup — every item occupies exactly 1 span. */
    private static class DefaultSpanSizeLookup extends SpanSizeLookup {
        @Override
        public int getSpanSize(int position) {
            return 1;
        }
    }
}
