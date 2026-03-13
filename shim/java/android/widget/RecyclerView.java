package android.widget;

import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.widget.RecyclerView
 *
 * NOTE: The real RecyclerView lives in {@code androidx.recyclerview.widget.RecyclerView}.
 * This shim covers the rare cases where legacy code imports from
 * {@code android.widget.RecyclerView}, and also provides the nested
 * {@link LayoutManager}, {@link Adapter}, and {@link ViewHolder} types that
 * all RecyclerView users depend on regardless of import path.
 *
 * All recycling, layout, and animation logic is stubbed out.  The class
 * extends {@link ViewGroup} (STACK node) so it participates in the shim
 * view hierarchy without requiring a dedicated ArkUI list node.
 */
public class RecyclerView extends ViewGroup {

    // ArkUI node type: use LIST (node type 8 = STACK fallback)
    static final int NODE_TYPE_STACK = 8;

    // ── State constants (mirrors real RecyclerView.State) ──

    public static final int SCROLL_STATE_IDLE        = 0;
    public static final int SCROLL_STATE_DRAGGING    = 1;
    public static final int SCROLL_STATE_SETTLING    = 2;

    public static final int NO_ID       = -1;
    public static final int NO_POSITION = -1;
    public static final long NO_ID_LONG = Long.MIN_VALUE;

    // ── Fields ──

    private Adapter<?> adapter;
    private LayoutManager layoutManager;
    private ItemAnimator itemAnimator;
    private final List<RecyclerListener> recyclerListeners = new ArrayList<>();
    private OnScrollListener onScrollListener;
    private boolean hasFixedSize = false;
    private int scrollState = SCROLL_STATE_IDLE;

    // ── Constructor ──

    public RecyclerView() {
        super(NODE_TYPE_STACK);
    }

    // ── Adapter ──

    public void setAdapter(Adapter<?> adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterAdapterDataObserver(null);
        }
        this.adapter = adapter;
        // In a real implementation this triggers a full rebind
    }

    @SuppressWarnings("unchecked")
    public <A extends Adapter<?>> A getAdapter() {
        return (A) adapter;
    }

    // ── LayoutManager ──

    public void setLayoutManager(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public LayoutManager getLayoutManager() {
        return layoutManager;
    }

    // ── ItemAnimator ──

    public void setItemAnimator(ItemAnimator animator) {
        this.itemAnimator = animator;
    }

    public ItemAnimator getItemAnimator() {
        return itemAnimator;
    }

    // ── Misc configuration ──

    public void setHasFixedSize(boolean hasFixedSize) {
        this.hasFixedSize = hasFixedSize;
    }

    public boolean hasFixedSize() {
        return hasFixedSize;
    }

    public void addOnScrollListener(OnScrollListener listener) {
        this.onScrollListener = listener;
    }

    public void removeOnScrollListener(OnScrollListener listener) {
        if (this.onScrollListener == listener) {
            this.onScrollListener = null;
        }
    }

    public void addRecyclerListener(RecyclerListener listener) {
        recyclerListeners.add(listener);
    }

    public int getScrollState() {
        return scrollState;
    }

    public void scrollToPosition(int position) {
        // no-op stub
    }

    public void smoothScrollToPosition(int position) {
        // no-op stub
    }

    public void scrollBy(int dx, int dy) {
        // no-op stub
    }

    public boolean canScrollVertically(int direction) {
        return false;
    }

    public boolean canScrollHorizontally(int direction) {
        return false;
    }

    public View findViewHolderForAdapterPosition(int position) {
        return null;
    }

    public void invalidateItemDecorations() {
        // no-op stub
    }

    public void addItemDecoration(ItemDecoration decor) {
        // no-op stub
    }

    public void addItemDecoration(ItemDecoration decor, int index) {
        // no-op stub
    }

    public void removeItemDecoration(ItemDecoration decor) {
        // no-op stub
    }

    // ── Nested: ViewHolder ──

    /**
     * A ViewHolder describes an item view and metadata about its place within
     * the RecyclerView.
     */
    public abstract static class ViewHolder {
        public final View itemView;
        int position = NO_POSITION;
        long itemId = NO_ID_LONG;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView must not be null");
            }
            this.itemView = itemView;
        }

        public final int getAdapterPosition() {
            return position;
        }

        public final int getLayoutPosition() {
            return position;
        }

        public final long getItemId() {
            return itemId;
        }

        public final int getItemViewType() {
            return 0;
        }
    }

    // ── Nested: Adapter ──

    /**
     * Base class for an Adapter that provides data to a RecyclerView.
     *
     * @param <VH> The type of ViewHolder created by this adapter.
     */
    public abstract static class Adapter<VH extends ViewHolder> {

        private final List<AdapterDataObserver> observers = new ArrayList<>();

        /** Create a new ViewHolder to represent an item. */
        public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

        /** Bind data to the ViewHolder at the given position. */
        public abstract void onBindViewHolder(VH holder, int position);

        /** Return the total number of items in the data set. */
        public abstract int getItemCount();

        public int getItemViewType(int position) {
            return 0;
        }

        public long getItemId(int position) {
            return NO_ID_LONG;
        }

        public boolean hasStableIds() {
            return false;
        }

        public void setHasStableIds(boolean hasStableIds) {
            // no-op stub
        }

        public final void notifyDataSetChanged() {
            for (AdapterDataObserver obs : observers) {
                obs.onChanged();
            }
        }

        public final void notifyItemChanged(int position) {
            for (AdapterDataObserver obs : observers) {
                obs.onItemRangeChanged(position, 1);
            }
        }

        public final void notifyItemChanged(int position, Object payload) {
            notifyItemChanged(position);
        }

        public final void notifyItemRangeChanged(int positionStart, int itemCount) {
            for (AdapterDataObserver obs : observers) {
                obs.onItemRangeChanged(positionStart, itemCount);
            }
        }

        public final void notifyItemInserted(int position) {
            for (AdapterDataObserver obs : observers) {
                obs.onItemRangeInserted(position, 1);
            }
        }

        public final void notifyItemRangeInserted(int positionStart, int itemCount) {
            for (AdapterDataObserver obs : observers) {
                obs.onItemRangeInserted(positionStart, itemCount);
            }
        }

        public final void notifyItemRemoved(int position) {
            for (AdapterDataObserver obs : observers) {
                obs.onItemRangeRemoved(position, 1);
            }
        }

        public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
            for (AdapterDataObserver obs : observers) {
                obs.onItemRangeRemoved(positionStart, itemCount);
            }
        }

        public final void notifyItemMoved(int fromPosition, int toPosition) {
            for (AdapterDataObserver obs : observers) {
                obs.onItemRangeMoved(fromPosition, toPosition, 1);
            }
        }

        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            if (observer != null) {
                observers.add(observer);
            }
        }

        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            observers.remove(observer);
        }

        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            // no-op stub
        }

        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            // no-op stub
        }

        public void onViewAttachedToWindow(VH holder) {
            // no-op stub
        }

        public void onViewDetachedFromWindow(VH holder) {
            // no-op stub
        }

        public void onViewRecycled(VH holder) {
            // no-op stub
        }
    }

    // ── Nested: LayoutManager ──

    /**
     * A LayoutManager is responsible for measuring and positioning item views
     * within a RecyclerView.
     */
    public abstract static class LayoutManager {

        public abstract int scrollHorizontallyBy(int dx, Recycler recycler, State state);
        public abstract int scrollVerticallyBy(int dy, Recycler recycler, State state);
        public abstract boolean canScrollHorizontally();
        public abstract boolean canScrollVertically();
        public abstract void layoutChildren(Recycler recycler, State state);

        public void onAttachedToWindow(RecyclerView view) {
            // no-op stub
        }

        public void onDetachedFromWindow(RecyclerView view) {
            // no-op stub
        }

        public void scrollToPosition(int position) {
            // no-op stub
        }

        public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position) {
            // no-op stub
        }

        public int findFirstVisibleItemPosition() {
            return NO_POSITION;
        }

        public int findLastVisibleItemPosition() {
            return NO_POSITION;
        }

        public int findFirstCompletelyVisibleItemPosition() {
            return NO_POSITION;
        }

        public int findLastCompletelyVisibleItemPosition() {
            return NO_POSITION;
        }

        public int getItemCount() {
            return 0;
        }

        public void removeAllViews() {
            // no-op stub
        }

        public View findViewByPosition(int position) {
            return null;
        }
    }

    // ── Nested: LinearLayoutManager ──

    /**
     * A LayoutManager that arranges items in a single-direction list.
     */
    public static class LinearLayoutManager extends LayoutManager {

        public static final int HORIZONTAL = 0;
        public static final int VERTICAL   = 1;

        private int orientation;
        private boolean reverseLayout = false;

        public LinearLayoutManager() {
            this(VERTICAL);
        }

        public LinearLayoutManager(int orientation) {
            this.orientation = orientation;
        }

        public LinearLayoutManager(int orientation, boolean reverseLayout) {
            this.orientation = orientation;
            this.reverseLayout = reverseLayout;
        }

        public void setOrientation(int orientation) {
            this.orientation = orientation;
        }

        public int getOrientation() {
            return orientation;
        }

        public void setReverseLayout(boolean reverseLayout) {
            this.reverseLayout = reverseLayout;
        }

        public boolean getReverseLayout() {
            return reverseLayout;
        }

        public void setStackFromEnd(boolean stackFromEnd) {
            // no-op stub
        }

        @Override
        public int scrollHorizontallyBy(int dx, Recycler recycler, State state) {
            return 0;
        }

        @Override
        public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
            return 0;
        }

        @Override
        public boolean canScrollHorizontally() {
            return orientation == HORIZONTAL;
        }

        @Override
        public boolean canScrollVertically() {
            return orientation == VERTICAL;
        }

        @Override
        public void layoutChildren(Recycler recycler, State state) {
            // no-op stub
        }
    }

    // ── Nested: GridLayoutManager ──

    /**
     * A LayoutManager that arranges items in a grid.
     */
    public static class GridLayoutManager extends LinearLayoutManager {

        private int spanCount;

        public GridLayoutManager(int spanCount) {
            this(spanCount, VERTICAL);
        }

        public GridLayoutManager(int spanCount, int orientation) {
            super(orientation);
            this.spanCount = spanCount;
        }

        public void setSpanCount(int spanCount) {
            this.spanCount = spanCount;
        }

        public int getSpanCount() {
            return spanCount;
        }

        public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
            // no-op stub
        }

        public abstract static class SpanSizeLookup {
            public abstract int getSpanSize(int position);
        }
    }

    // ── Nested: StaggeredGridLayoutManager ──

    /**
     * A LayoutManager that places children in a staggered grid.
     */
    public static class StaggeredGridLayoutManager extends LayoutManager {

        public static final int HORIZONTAL = 0;
        public static final int VERTICAL   = 1;

        private int spanCount;
        private int orientation;

        public StaggeredGridLayoutManager(int spanCount, int orientation) {
            this.spanCount = spanCount;
            this.orientation = orientation;
        }

        public int getSpanCount() { return spanCount; }
        public int getOrientation() { return orientation; }

        @Override
        public int scrollHorizontallyBy(int dx, Recycler recycler, State state) { return 0; }
        @Override
        public int scrollVerticallyBy(int dy, Recycler recycler, State state) { return 0; }
        @Override
        public boolean canScrollHorizontally() { return orientation == HORIZONTAL; }
        @Override
        public boolean canScrollVertically() { return orientation == VERTICAL; }
        @Override
        public void layoutChildren(Recycler recycler, State state) { /* no-op */ }
    }

    // ── Nested: Recycler ──

    /**
     * A Recycler is responsible for managing scrapped or detached item views
     * for reuse.
     */
    public final class Recycler {
        public ViewHolder getViewForPosition(int position) {
            return null;
        }

        public void recycleView(View view) {
            // no-op stub
        }
    }

    // ── Nested: State ──

    /**
     * Contains useful information about the current RecyclerView state.
     */
    public static class State {
        private int itemCount = 0;

        public int getItemCount() { return itemCount; }
        public boolean isPreLayout() { return false; }
        public boolean didStructureChange() { return false; }
        public boolean willRunSimpleAnimations() { return false; }
        public boolean willRunPredictiveAnimations() { return false; }
    }

    // ── Nested: ItemDecoration ──

    /**
     * An ItemDecoration allows the application to add a special drawing and
     * layout offset to specific item views from the adapter's data set.
     */
    public abstract static class ItemDecoration {
        public void onDraw(Object canvas, RecyclerView parent, State state) {
            // no-op stub
        }

        public void onDrawOver(Object canvas, RecyclerView parent, State state) {
            // no-op stub
        }

        public void getItemOffsets(Object outRect, View view, RecyclerView parent, State state) {
            // no-op stub
        }
    }

    // ── Nested: ItemAnimator ──

    /**
     * An ItemAnimator animates changes to the adapter's data set.
     */
    public abstract static class ItemAnimator {
        public abstract boolean animateDisappearance(ViewHolder viewHolder, Object preLayoutInfo, Object postLayoutInfo);
        public abstract boolean animateAppearance(ViewHolder viewHolder, Object preLayoutInfo, Object postLayoutInfo);
        public abstract boolean animatePersistence(ViewHolder viewHolder, Object preLayoutInfo, Object postLayoutInfo);
        public abstract boolean animateChange(ViewHolder oldHolder, ViewHolder newHolder, Object preLayoutInfo, Object postLayoutInfo);
        public abstract void runPendingAnimations();
        public abstract void endAnimation(ViewHolder item);
        public abstract void endAnimations();
        public abstract boolean isRunning();
    }

    // ── Nested: AdapterDataObserver ──

    /**
     * Observer base class for watching changes to a RecyclerView Adapter.
     */
    public abstract static class AdapterDataObserver {
        public void onChanged() { }
        public void onItemRangeChanged(int positionStart, int itemCount) { }
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            onItemRangeChanged(positionStart, itemCount);
        }
        public void onItemRangeInserted(int positionStart, int itemCount) { }
        public void onItemRangeRemoved(int positionStart, int itemCount) { }
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) { }
    }

    // ── Nested: RecyclerListener ──

    /**
     * A listener that can be used to receive messages whenever a view is recycled.
     */
    public interface RecyclerListener {
        void onViewRecycled(ViewHolder holder);
    }

    // ── Nested: OnScrollListener ──

    /**
     * An OnScrollListener can be added to a RecyclerView to receive messages
     * when a scrolling event has occurred on that RecyclerView.
     */
    public abstract static class OnScrollListener {
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) { }
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) { }
    }

    // ── Nested: SmoothScroller ──

    /**
     * Base class for smooth scrolling.
     */
    public abstract static class SmoothScroller {
        public abstract void onStart();
        public abstract void onStop();
        public abstract void onSeekTargetStep(int dx, int dy, State state, Action action);
        public abstract void onTargetFound(View targetView, State state, Action action);

        public static class Action {
            public static final int UNDEFINED_DURATION = Integer.MIN_VALUE;
            private int dx;
            private int dy;
            private int duration;

            public Action(int dx, int dy) {
                this(dx, dy, UNDEFINED_DURATION);
            }

            public Action(int dx, int dy, int duration) {
                this.dx = dx;
                this.dy = dy;
                this.duration = duration;
            }

            public int getDx() { return dx; }
            public int getDy() { return dy; }
            public int getDuration() { return duration; }
            public void update(int dx, int dy, int duration) {
                this.dx = dx; this.dy = dy; this.duration = duration;
            }
        }
    }
}
