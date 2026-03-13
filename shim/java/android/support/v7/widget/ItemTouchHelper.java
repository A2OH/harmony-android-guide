package android.support.v7.widget;

/**
 * Shim: android.support.v7.widget.ItemTouchHelper
 *
 * Utility class to add swipe-to-dismiss and drag-to-reorder behaviour to a
 * RecyclerView.  All gesture handling is stubbed — only the callback wiring
 * is modelled.
 */
public class ItemTouchHelper extends RecyclerView.ItemDecoration {

    // Movement flag directions
    public static final int UP    = 1;
    public static final int DOWN  = 2;
    public static final int LEFT  = 4;
    public static final int RIGHT = 8;

    // Action states
    public static final int ACTION_STATE_IDLE  = 0;
    public static final int ACTION_STATE_SWIPE = 1;
    public static final int ACTION_STATE_DRAG  = 2;

    private final Callback mCallback;
    private RecyclerView mRecyclerView;

    /**
     * Creates an ItemTouchHelper that will work with the given Callback.
     *
     * @param callback the Callback to control the behaviour of this helper
     */
    public ItemTouchHelper(Callback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback must not be null");
        }
        this.mCallback = callback;
    }

    /**
     * Attaches the ItemTouchHelper to the provided RecyclerView.
     * A null argument detaches from the current RecyclerView (if any).
     *
     * @param recyclerView the RecyclerView to attach to, or null to detach
     */
    public void attachToRecyclerView(RecyclerView recyclerView) {
        if (mRecyclerView == recyclerView) {
            return;
        }
        if (mRecyclerView != null) {
            mRecyclerView.removeItemDecoration(this);
        }
        mRecyclerView = recyclerView;
        if (recyclerView != null) {
            recyclerView.addItemDecoration(this);
        }
    }

    public Callback getCallback() {
        return mCallback;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Callback
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Abstract Callback class that controls the behaviour of ItemTouchHelper.
     */
    public static abstract class Callback {

        /**
         * Returns a composite flag that defines the enabled move directions in
         * each state (idle, swiping, dragging).
         *
         * @param recyclerView the RecyclerView to which ItemTouchHelper is attached
         * @param viewHolder   the ViewHolder for which the movement information is necessary
         * @return flags specifying which movements are enabled for the ViewHolder
         */
        public abstract int getMovementFlags(RecyclerView recyclerView,
                                             RecyclerView.ViewHolder viewHolder);

        /**
         * Called when ItemTouchHelper wants to move the dragged item from its old position
         * to the new position.
         *
         * @param recyclerView the RecyclerView to which ItemTouchHelper is attached
         * @param viewHolder   the ViewHolder which is being dragged by the user
         * @param target       the ViewHolder over which the currently active item is dragged
         * @return {@code true} if the viewHolder has been moved to the adapter position of target
         */
        public abstract boolean onMove(RecyclerView recyclerView,
                                       RecyclerView.ViewHolder viewHolder,
                                       RecyclerView.ViewHolder target);

        /**
         * Called when a ViewHolder is swiped by the user.
         *
         * @param viewHolder the ViewHolder which has been swiped by the user
         * @param direction  the direction to which the ViewHolder is swiped
         */
        public abstract void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);

        // ── Utility methods ──

        /**
         * Helper method to create a movement flags integer.
         */
        public static int makeMovementFlags(int dragFlags, int swipeFlags) {
            return makeFlag(ItemTouchHelper.ACTION_STATE_IDLE, swipeFlags | dragFlags)
                    | makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, swipeFlags)
                    | makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, dragFlags);
        }

        public static int makeFlag(int actionState, int directions) {
            return directions << (actionState * 4);
        }

        // ── Optional overrides ──

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        public int interpolateOutOfBoundsScroll(RecyclerView recyclerView,
                                                int viewSize,
                                                int viewSizeOutOfBounds,
                                                int totalSize,
                                                long msSinceStartScroll) {
            return 0;
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {}

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {}

        public void onChildDraw(Object c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder,
                                float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {}

        public void onChildDrawOver(Object c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {}
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SimpleCallback
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * A simple wrapper for the most common ItemTouchHelper use case —
     * drag and swipe with fixed directions.
     */
    public static abstract class SimpleCallback extends Callback {

        private int mDragDirs;
        private int mSwipeDirs;

        /**
         * Creates a SimpleCallback with the given drag and swipe direction flags.
         *
         * @param dragDirs  bitwise OR of drag directions
         * @param swipeDirs bitwise OR of swipe directions
         */
        public SimpleCallback(int dragDirs, int swipeDirs) {
            this.mDragDirs  = dragDirs;
            this.mSwipeDirs = swipeDirs;
        }

        public void setDefaultDragDirs(int defaultDragDirs) {
            this.mDragDirs = defaultDragDirs;
        }

        public void setDefaultSwipeDirs(int defaultSwipeDirs) {
            this.mSwipeDirs = defaultSwipeDirs;
        }

        public int getDragDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return mDragDirs;
        }

        public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return mSwipeDirs;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(
                    getDragDirs(recyclerView, viewHolder),
                    getSwipeDirs(recyclerView, viewHolder));
        }
    }
}
