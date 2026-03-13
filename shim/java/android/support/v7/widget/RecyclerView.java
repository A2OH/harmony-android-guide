package android.support.v7.widget;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.support.v7.widget.RecyclerView
 *
 * Stub implementation for the AndroidX compatibility layer.
 * Extends ViewGroup so it participates in the view hierarchy.
 * Adapter, ViewHolder, LayoutManager and ItemDecoration are inner types
 * matching the real RecyclerView API surface.
 */
public class RecyclerView extends ViewGroup {

    private Adapter<?> mAdapter;
    private LayoutManager mLayoutManager;
    private final List<ItemDecoration> mItemDecorations = new ArrayList<>();

    public RecyclerView() {
        super();
    }

    /** Constructor accepting a context-like object (ignored in shim). */
    public RecyclerView(Object context) {
        super();
    }

    public RecyclerView(Object context, Object attrs) {
        super();
    }

    public RecyclerView(Object context, Object attrs, int defStyle) {
        super();
    }

    // ── Adapter ──

    public void setAdapter(Adapter<?> adapter) {
        this.mAdapter = adapter;
    }

    @SuppressWarnings("unchecked")
    public <T extends Adapter<?>> T getAdapter() {
        return (T) mAdapter;
    }

    // ── LayoutManager ──

    public void setLayoutManager(LayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    // ── ItemDecoration ──

    public void addItemDecoration(ItemDecoration decor) {
        if (decor != null) {
            mItemDecorations.add(decor);
        }
    }

    public void removeItemDecoration(ItemDecoration decor) {
        mItemDecorations.remove(decor);
    }

    // ── Scroll ──

    public void scrollToPosition(int position) {
        // Stub — no native scroll in shim
    }

    public void smoothScrollToPosition(int position) {
        // Stub
    }

    // ── RecycledViewPool ──

    public void setRecycledViewPool(Object pool) {
        // Stub
    }

    // ── ItemAnimator ──

    public void setItemAnimator(Object animator) {
        // Stub
    }

    // ── HasFixedSize ──

    public void setHasFixedSize(boolean hasFixedSize) {
        // Stub
    }

    // ── NestedScrolling stubs ──

    public void setNestedScrollingEnabled(boolean enabled) {
        // Stub
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Abstract inner classes
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Base class for an Adapter that provides data to a RecyclerView.
     *
     * @param <VH> the type of ViewHolder the adapter creates and binds
     */
    public static abstract class Adapter<VH extends ViewHolder> {

        private final List<Object> mObservers = new ArrayList<>();

        public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

        public abstract void onBindViewHolder(VH holder, int position);

        public abstract int getItemCount();

        public int getItemViewType(int position) {
            return 0;
        }

        public long getItemId(int position) {
            return -1;
        }

        public void setHasStableIds(boolean hasStableIds) {
            // Stub
        }

        // ── Change notifications ──

        public void notifyDataSetChanged() {
            // Stub — real impl would redraw all items
        }

        public void notifyItemInserted(int position) {
            // Stub
        }

        public void notifyItemRemoved(int position) {
            // Stub
        }

        public void notifyItemChanged(int position) {
            // Stub
        }

        public void notifyItemChanged(int position, Object payload) {
            // Stub
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            // Stub
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            // Stub
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            // Stub
        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
            // Stub
        }

        public void registerAdapterDataObserver(Object observer) {
            if (observer != null) mObservers.add(observer);
        }

        public void unregisterAdapterDataObserver(Object observer) {
            mObservers.remove(observer);
        }

        public void onAttachedToRecyclerView(RecyclerView recyclerView) {}
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {}
        public void onViewAttachedToWindow(VH holder) {}
        public void onViewDetachedFromWindow(VH holder) {}
        public void onViewRecycled(VH holder) {}
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    public static abstract class ViewHolder {
        /** The view contained within this ViewHolder. */
        public final View itemView;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
        }

        public final int getAdapterPosition() {
            return -1;  // Stub
        }

        public final int getLayoutPosition() {
            return -1;  // Stub
        }

        public final long getItemId() {
            return -1;  // Stub
        }

        public final int getItemViewType() {
            return 0;  // Stub
        }
    }

    /**
     * A LayoutManager is responsible for measuring and positioning item views
     * within a RecyclerView.
     */
    public static abstract class LayoutManager {

        public int[] calculateExtraLayoutSpace(Object state) {
            return new int[]{0, 0};
        }

        public void scrollToPosition(int position) {
            // Stub
        }

        public void smoothScrollToPosition(RecyclerView recyclerView, Object state, int position) {
            // Stub
        }

        public boolean canScrollVertically() {
            return false;
        }

        public boolean canScrollHorizontally() {
            return false;
        }

        public void onAttachedToWindow(RecyclerView view) {}
        public void onDetachedFromWindow(RecyclerView view, Object recycler) {}

        public void requestLayout() {
            // Stub
        }
    }

    /**
     * An ItemDecoration allows the application to add a special drawing and layout offset
     * to specific item views from the adapter's data set.
     */
    public static abstract class ItemDecoration {

        public void onDraw(Object c, RecyclerView parent, Object state) {
            // Stub
        }

        public void onDrawOver(Object c, RecyclerView parent, Object state) {
            // Stub
        }

        public void getItemOffsets(Object outRect, View view, RecyclerView parent, Object state) {
            // Stub — no offsets applied
        }
    }
}
