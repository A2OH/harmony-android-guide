package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.MotionEvent;

/**
 * Shim: android.widget.AbsListView — abstract base for scrollable list-like widgets.
 *
 * AOSP features extracted:
 *   - RecycleBin: view recycling for off-screen views
 *   - Choice mode: single/multiple selection
 *   - Fast scroll
 *   - Scroll state tracking
 *   - setSelection / smoothScrollToPosition
 *   - Touch scroll detection
 */
public class AbsListView extends AdapterView {

    // Choice mode constants
    public static final int CHOICE_MODE_NONE          = 0;
    public static final int CHOICE_MODE_SINGLE        = 1;
    public static final int CHOICE_MODE_MULTIPLE      = 2;
    public static final int CHOICE_MODE_MULTIPLE_MODAL = 3;

    // Scroll state constants
    public static final int SCROLL_STATE_IDLE         = 0;
    public static final int SCROLL_STATE_TOUCH_SCROLL = 1;
    public static final int SCROLL_STATE_FLING        = 2;

    // Transcript mode constants
    public static final int TRANSCRIPT_MODE_DISABLED       = 0;
    public static final int TRANSCRIPT_MODE_NORMAL         = 1;
    public static final int TRANSCRIPT_MODE_ALWAYS_SCROLL  = 2;

    private OnScrollListener onScrollListener;
    private int choiceMode = CHOICE_MODE_NONE;
    private boolean fastScrollEnabled = false;
    private int mScrollState = SCROLL_STATE_IDLE;
    private int mTranscriptMode = TRANSCRIPT_MODE_DISABLED;

    // Selection tracking
    private int mSelectedPosition = INVALID_POSITION;
    private long mSelectedRowId = INVALID_ROW_ID;

    // RecycleBin for view recycling
    private final RecycleBin mRecycleBin = new RecycleBin();

    // Checked items tracking (for choice modes)
    private final java.util.Map<Integer, Boolean> mCheckedItems = new java.util.HashMap<>();
    private int mCheckedItemCount = 0;

    // Touch state
    private static final int TOUCH_SLOP = 8;
    private float mLastMotionY;
    private boolean mIsBeingDragged;

    protected AbsListView(android.content.Context context) {
        super(context);
    }
    protected AbsListView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }
    protected AbsListView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  RecycleBin (AOSP view recycling)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * The RecycleBin facilitates reuse of views across layout passes.
     * Views that scroll off screen are added to a scrap list, and can be
     * retrieved by getScrapView() for reuse in Adapter.getView().
     */
    public static class RecycleBin {
        // Scrap views keyed by view type
        private final java.util.Map<Integer, java.util.ArrayList<View>> mScrapViews
                = new java.util.HashMap<>();

        /** Add a view to the scrap heap for later reuse. */
        public void addScrapView(View scrap, int viewType) {
            java.util.ArrayList<View> list = mScrapViews.get(Integer.valueOf(viewType));
            if (list == null) {
                list = new java.util.ArrayList<View>();
                mScrapViews.put(Integer.valueOf(viewType), list);
            }
            list.add(scrap);
        }

        /** Retrieve a scrap view for the given view type. Returns null if none available. */
        public View getScrapView(int viewType) {
            java.util.ArrayList<View> list = mScrapViews.get(Integer.valueOf(viewType));
            if (list != null && !list.isEmpty()) {
                return list.remove(list.size() - 1);
            }
            return null;
        }

        /** Clear all scrap views. */
        public void clear() {
            for (java.util.ArrayList<View> list : mScrapViews.values()) {
                list.clear();
            }
            mScrapViews.clear();
        }

        /** Get number of scrap views for a given type. */
        public int getScrapCount(int viewType) {
            java.util.ArrayList<View> list = mScrapViews.get(Integer.valueOf(viewType));
            return list != null ? list.size() : 0;
        }
    }

    /** Get the RecycleBin for view recycling. */
    public RecycleBin getRecycleBin() { return mRecycleBin; }

    // ════════════════════════════════════════════════════════════════════════
    //  Scroll listener
    // ════════════════════════════════════════════════════════════════════════

    public void setOnScrollListener(OnScrollListener listener) {
        this.onScrollListener = listener;
    }

    public OnScrollListener getOnScrollListener() { return onScrollListener; }

    // ════════════════════════════════════════════════════════════════════════
    //  Scroll / selection helpers
    // ════════════════════════════════════════════════════════════════════════

    public void smoothScrollToPosition(int position) {
        setSelection(position);
    }

    public void smoothScrollToPositionFromTop(int position, int offset) {
        setSelection(position);
    }

    public void smoothScrollBy(int distance, int duration) {
        // Simplified: immediate scroll
        scrollTo(getScrollX(), getScrollY() + distance);
    }

    public void setSelection(int position) {
        mSelectedPosition = position;
        // Scroll to make the position visible
        if (position >= 0 && position < getChildCount()) {
            View child = getChildAt(position);
            if (child != null) {
                int scrollTarget = child.getTop();
                scrollTo(0, scrollTarget);
            }
        }
    }

    public int getSelectedItemPosition() { return mSelectedPosition; }
    public long getSelectedItemId() { return mSelectedRowId; }

    // ════════════════════════════════════════════════════════════════════════
    //  Selector drawable resource
    // ════════════════════════════════════════════════════════════════════════

    public void setSelector(int resId) {
        // No-op shim: resource loading not supported in headless mode
    }

    public void setSelector(android.graphics.drawable.Drawable sel) {
        // No-op shim
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Fast scroll
    // ════════════════════════════════════════════════════════════════════════

    public void setFastScrollEnabled(boolean enabled) {
        this.fastScrollEnabled = enabled;
    }

    public boolean isFastScrollEnabled() { return fastScrollEnabled; }

    public void setFastScrollAlwaysVisible(boolean alwaysVisible) {}
    public boolean isFastScrollAlwaysVisible() { return false; }

    // ════════════════════════════════════════════════════════════════════════
    //  Choice mode
    // ════════════════════════════════════════════════════════════════════════

    public void setChoiceMode(int choiceMode) {
        this.choiceMode = choiceMode;
        if (choiceMode != CHOICE_MODE_NONE) {
            mCheckedItems.clear();
            mCheckedItemCount = 0;
        }
    }

    public int getChoiceMode() { return choiceMode; }

    public void setItemChecked(int position, boolean value) {
        if (choiceMode == CHOICE_MODE_NONE) return;

        if (choiceMode == CHOICE_MODE_SINGLE) {
            // Uncheck all others
            mCheckedItems.clear();
            mCheckedItemCount = 0;
        }

        Boolean prev = mCheckedItems.put(Integer.valueOf(position), Boolean.valueOf(value));
        if (value && (prev == null || !prev.booleanValue())) {
            mCheckedItemCount++;
        } else if (!value && prev != null && prev.booleanValue()) {
            mCheckedItemCount--;
        }
    }

    public boolean isItemChecked(int position) {
        Boolean checked = mCheckedItems.get(Integer.valueOf(position));
        return checked != null && checked.booleanValue();
    }

    public int getCheckedItemCount() { return mCheckedItemCount; }

    public int getCheckedItemPosition() {
        if (choiceMode == CHOICE_MODE_SINGLE) {
            for (java.util.Map.Entry<Integer, Boolean> entry : mCheckedItems.entrySet()) {
                if (entry.getValue().booleanValue()) {
                    return entry.getKey().intValue();
                }
            }
        }
        return INVALID_POSITION;
    }

    public long[] getCheckedItemIds() {
        java.util.ArrayList<Long> ids = new java.util.ArrayList<Long>();
        for (java.util.Map.Entry<Integer, Boolean> entry : mCheckedItems.entrySet()) {
            if (entry.getValue().booleanValue()) {
                ids.add(Long.valueOf(entry.getKey().intValue()));
            }
        }
        long[] result = new long[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            result[i] = ids.get(i).longValue();
        }
        return result;
    }

    public void clearChoices() {
        mCheckedItems.clear();
        mCheckedItemCount = 0;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Transcript mode
    // ════════════════════════════════════════════════════════════════════════

    public void setTranscriptMode(int mode) { mTranscriptMode = mode; }
    public int getTranscriptMode() { return mTranscriptMode; }

    // ════════════════════════════════════════════════════════════════════════
    //  Stack from bottom
    // ════════════════════════════════════════════════════════════════════════

    private boolean mStackFromBottom;
    public void setStackFromBottom(boolean stackFromBottom) { mStackFromBottom = stackFromBottom; }
    public boolean isStackFromBottom() { return mStackFromBottom; }

    // ════════════════════════════════════════════════════════════════════════
    //  Dispatcher helpers for subclasses
    // ════════════════════════════════════════════════════════════════════════

    protected void dispatchScrollStateChanged(int state) {
        mScrollState = state;
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(this, state);
        }
    }

    protected void dispatchScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (onScrollListener != null) {
            onScrollListener.onScroll(this, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Cache color hint (for list backgrounds)
    // ════════════════════════════════════════════════════════════════════════

    private int mCacheColorHint;
    public void setCacheColorHint(int color) { mCacheColorHint = color; }
    public int getCacheColorHint() { return mCacheColorHint; }

    // ════════════════════════════════════════════════════════════════════════
    //  Interface
    // ════════════════════════════════════════════════════════════════════════

    public interface OnScrollListener {
        void onScrollStateChanged(AbsListView view, int scrollState);
        void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }
}
