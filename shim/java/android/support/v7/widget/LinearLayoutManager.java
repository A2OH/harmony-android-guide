package android.support.v7.widget;

/**
 * Shim: android.support.v7.widget.LinearLayoutManager
 *
 * Extends RecyclerView.LayoutManager; arranges items in a single column (VERTICAL)
 * or row (HORIZONTAL). All layout logic is stubbed — the shim only models state.
 */
public class LinearLayoutManager extends RecyclerView.LayoutManager {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL   = 1;

    private int mOrientation;
    private boolean mReverseLayout;
    private boolean mStackFromEnd;

    /** Default constructor — vertical orientation, no reverse. */
    public LinearLayoutManager(Object context) {
        this(context, VERTICAL, false);
    }

    /**
     * Full constructor.
     *
     * @param context      context-like object (ignored in shim)
     * @param orientation  {@link #HORIZONTAL} or {@link #VERTICAL}
     * @param reverseLayout {@code true} to reverse the layout direction
     */
    public LinearLayoutManager(Object context, int orientation, boolean reverseLayout) {
        this.mOrientation   = orientation;
        this.mReverseLayout = reverseLayout;
    }

    // ── Orientation ──

    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }

    public int getOrientation() {
        return mOrientation;
    }

    // ── Reverse layout ──

    public void setReverseLayout(boolean reverseLayout) {
        this.mReverseLayout = reverseLayout;
    }

    public boolean getReverseLayout() {
        return mReverseLayout;
    }

    // ── Stack from end ──

    public void setStackFromEnd(boolean stackFromEnd) {
        this.mStackFromEnd = stackFromEnd;
    }

    public boolean getStackFromEnd() {
        return mStackFromEnd;
    }

    // ── Scroll ──

    @Override
    public void scrollToPosition(int position) {
        // Stub
    }

    @Override
    public boolean canScrollVertically() {
        return mOrientation == VERTICAL;
    }

    @Override
    public boolean canScrollHorizontally() {
        return mOrientation == HORIZONTAL;
    }

    // ── Required abstract impl ──

    @Override
    public int[] calculateExtraLayoutSpace(Object state) {
        return new int[]{0, 0};
    }

    // ── Find first/last visible item positions (stub) ──

    public int findFirstVisibleItemPosition() {
        return 0;
    }

    public int findLastVisibleItemPosition() {
        return 0;
    }

    public int findFirstCompletelyVisibleItemPosition() {
        return 0;
    }

    public int findLastCompletelyVisibleItemPosition() {
        return 0;
    }
}
