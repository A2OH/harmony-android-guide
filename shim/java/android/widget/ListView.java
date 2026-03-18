package android.widget;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.ArrayList;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.ListView → ARKUI_NODE_LIST
 *
 * AOSP features extracted:
 *   - View recycling via RecycleBin (reuse off-screen views)
 *   - Header/footer support
 *   - setSelection / smoothScrollToPosition
 *   - Choice mode (single/multiple) via AbsListView
 *   - Divider height/drawable
 *   - Adapter population with convertView reuse
 */
public class ListView extends AbsListView {
    static final int NODE_TYPE_LIST = 10;
    static final int NODE_TYPE_LIST_ITEM = 19;

    private android.widget.ListAdapter adapter;

    // Header / footer views
    private final ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<FixedViewInfo>();
    private final ArrayList<FixedViewInfo> mFooterViewInfos = new ArrayList<FixedViewInfo>();

    /** Internal observer that re-populates children when adapter data changes. */
    private final BaseAdapter.DataSetObserver dataObserver = new BaseAdapter.DataSetObserver() {
        @Override public void onChanged() { populateFromAdapter(); }
        @Override public void onInvalidated() { removeAllViews(); }
    };

    public ListView(android.content.Context context) {
        super(context);
    }

    public ListView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public ListView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Header / Footer support
    // ════════════════════════════════════════════════════════════════════════

    /** Wrapper holding a fixed (header/footer) view and its associated data. */
    public static class FixedViewInfo {
        public View view;
        public Object data;
        public boolean isSelectable;

        public FixedViewInfo(View view, Object data, boolean isSelectable) {
            this.view = view;
            this.data = data;
            this.isSelectable = isSelectable;
        }
    }

    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

    public void addHeaderView(View v, Object data, boolean isSelectable) {
        mHeaderViewInfos.add(new FixedViewInfo(v, data, isSelectable));
        // If adapter already set, re-populate
        if (adapter != null) {
            populateFromAdapter();
        }
    }

    public boolean removeHeaderView(View v) {
        for (int i = 0; i < mHeaderViewInfos.size(); i++) {
            if (mHeaderViewInfos.get(i).view == v) {
                mHeaderViewInfos.remove(i);
                if (adapter != null) populateFromAdapter();
                return true;
            }
        }
        return false;
    }

    public int getHeaderViewsCount() { return mHeaderViewInfos.size(); }

    public void addFooterView(View v) {
        addFooterView(v, null, true);
    }

    public void addFooterView(View v, Object data, boolean isSelectable) {
        mFooterViewInfos.add(new FixedViewInfo(v, data, isSelectable));
        if (adapter != null) {
            populateFromAdapter();
        }
    }

    public boolean removeFooterView(View v) {
        for (int i = 0; i < mFooterViewInfos.size(); i++) {
            if (mFooterViewInfos.get(i).view == v) {
                mFooterViewInfos.remove(i);
                if (adapter != null) populateFromAdapter();
                return true;
            }
        }
        return false;
    }

    public int getFooterViewsCount() { return mFooterViewInfos.size(); }

    // ════════════════════════════════════════════════════════════════════════
    //  Adapter
    // ════════════════════════════════════════════════════════════════════════

    public void setAdapter(android.widget.ListAdapter adapter) {
        // Unregister from old adapter
        if (this.adapter instanceof BaseAdapter) {
            ((BaseAdapter) this.adapter).unregisterDataSetObserver(dataObserver);
        }

        // Remove old items
        removeAllViews();
        getRecycleBin().clear();
        this.adapter = adapter;
        if (adapter == null) return;

        // Register for future data changes
        if (adapter instanceof BaseAdapter) {
            ((BaseAdapter) adapter).registerDataSetObserver(dataObserver);
        }

        // Populate list items from adapter
        populateFromAdapter();
    }

    public android.widget.ListAdapter getAdapter() { return adapter; }

    // ════════════════════════════════════════════════════════════════════════
    //  Divider
    // ════════════════════════════════════════════════════════════════════════

    /** Default padding for list items (dp-like values). */
    private static final int ITEM_PADDING_H = 16;
    private static final int ITEM_PADDING_V = 12;

    /** Divider height between items (pixels). */
    private int mDividerHeight = 1;
    private android.graphics.drawable.Drawable mDivider;
    private boolean mDividerIsOpaque;

    public void setDividerHeight(int height) { mDividerHeight = height; }
    public int getDividerHeight() { return mDividerHeight; }

    public void setDivider(android.graphics.drawable.Drawable divider) {
        mDivider = divider;
    }

    public android.graphics.drawable.Drawable getDivider() { return mDivider; }

    // ════════════════════════════════════════════════════════════════════════
    //  Populate / recycle
    // ════════════════════════════════════════════════════════════════════════

    /** Re-creates all child views from the adapter, using RecycleBin for convertView. */
    private void populateFromAdapter() {
        // Move current children to RecycleBin for reuse
        AbsListView.RecycleBin recycleBin = getRecycleBin();
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            // Don't recycle header/footer views
            boolean isHeaderOrFooter = false;
            for (int h = 0; h < mHeaderViewInfos.size(); h++) {
                if (mHeaderViewInfos.get(h).view == child) { isHeaderOrFooter = true; break; }
            }
            for (int f = 0; f < mFooterViewInfos.size(); f++) {
                if (mFooterViewInfos.get(f).view == child) { isHeaderOrFooter = true; break; }
            }
            if (!isHeaderOrFooter) {
                int viewType = 0; // default type
                if (adapter != null) {
                    // Use adapter's view type if available
                    // Position approximation: child index minus headers
                    int adapterPos = i - mHeaderViewInfos.size();
                    if (adapterPos >= 0 && adapterPos < adapter.getCount()) {
                        viewType = adapter.getItemViewType(adapterPos);
                    }
                }
                recycleBin.addScrapView(child, viewType);
            }
        }

        removeAllViews();
        if (adapter == null) return;

        // Add header views
        for (int i = 0; i < mHeaderViewInfos.size(); i++) {
            View hv = mHeaderViewInfos.get(i).view;
            addView(hv);
        }

        // Add adapter views (using convertView from RecycleBin)
        for (int i = 0; i < adapter.getCount(); i++) {
            int viewType = adapter.getItemViewType(i);
            View convertView = recycleBin.getScrapView(viewType);
            View itemView = adapter.getView(i, convertView, this);
            if (itemView != null) {
                // Apply default padding to item views if they have none
                if (itemView.getPaddingLeft() == 0 && itemView.getPaddingTop() == 0
                    && itemView.getPaddingRight() == 0 && itemView.getPaddingBottom() == 0) {
                    itemView.setPadding(ITEM_PADDING_H, ITEM_PADDING_V, ITEM_PADDING_H, ITEM_PADDING_V);
                }
                addView(itemView);
            }
        }

        // Add footer views
        for (int i = 0; i < mFooterViewInfos.size(); i++) {
            View fv = mFooterViewInfos.get(i).view;
            addView(fv);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Measurement and layout
    // ════════════════════════════════════════════════════════════════════════

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int totalHeight = 0;
        int maxWidth = 0;
        int visibleCount = 0;
        for (int i = 0; i < getChildCount(); i++) {
            android.view.View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            totalHeight += child.getMeasuredHeight();
            maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
            visibleCount++;
        }
        // Add divider heights between items
        if (visibleCount > 1) {
            totalHeight += mDividerHeight * (visibleCount - 1);
        }
        int wMode = android.view.View.MeasureSpec.getMode(widthMeasureSpec);
        int wSize = android.view.View.MeasureSpec.getSize(widthMeasureSpec);
        int hMode = android.view.View.MeasureSpec.getMode(heightMeasureSpec);
        int hSize = android.view.View.MeasureSpec.getSize(heightMeasureSpec);

        int measuredW;
        if (wMode == android.view.View.MeasureSpec.EXACTLY) {
            measuredW = wSize;
        } else if (wMode == android.view.View.MeasureSpec.AT_MOST) {
            measuredW = Math.min(maxWidth + getPaddingLeft() + getPaddingRight(), wSize);
        } else {
            measuredW = maxWidth + getPaddingLeft() + getPaddingRight();
        }

        int measuredH;
        if (hMode == android.view.View.MeasureSpec.EXACTLY) {
            measuredH = hSize;
        } else if (hMode == android.view.View.MeasureSpec.AT_MOST) {
            measuredH = Math.min(totalHeight + getPaddingTop() + getPaddingBottom(), hSize);
        } else {
            measuredH = totalHeight + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(measuredW, measuredH);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childTop = getPaddingTop();
        int childLeft = getPaddingLeft();
        int availWidth = (r - l) - getPaddingLeft() - getPaddingRight();
        for (int i = 0; i < getChildCount(); i++) {
            android.view.View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            int cw = child.getMeasuredWidth() > 0 ? child.getMeasuredWidth() : availWidth;
            if (cw < availWidth) cw = availWidth;
            int ch = child.getMeasuredHeight() > 0 ? child.getMeasuredHeight() : 0;
            child.layout(childLeft, childTop, childLeft + cw, childTop + ch);
            childTop += ch + mDividerHeight;
        }
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        // Draw divider lines between items
        if (mDividerHeight > 0 && getChildCount() > 1) {
            android.graphics.Paint dividerPaint = new android.graphics.Paint();
            dividerPaint.setColor(0xFFCCCCCC);
            dividerPaint.setStyle(android.graphics.Paint.Style.FILL);
            for (int i = 0; i < getChildCount() - 1; i++) {
                android.view.View child = getChildAt(i);
                if (child.getVisibility() == GONE) continue;
                int dividerTop = child.getBottom();
                canvas.drawRect(getPaddingLeft(), dividerTop,
                    getWidth() - getPaddingRight(), dividerTop + mDividerHeight, dividerPaint);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Item click / position
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Simulates a click on the item at the given position.
     * Dispatches to the OnItemClickListener if one is set.
     */
    public boolean performItemClick(View view, int position, long id) {
        // Handle choice mode
        if (getChoiceMode() != CHOICE_MODE_NONE) {
            if (getChoiceMode() == CHOICE_MODE_MULTIPLE || getChoiceMode() == CHOICE_MODE_MULTIPLE_MODAL) {
                boolean newVal = !isItemChecked(position);
                setItemChecked(position, newVal);
            } else if (getChoiceMode() == CHOICE_MODE_SINGLE) {
                setItemChecked(position, true);
            }
        }
        dispatchItemClick(view, position, id);
        return getOnItemClickListener() != null;
    }

    @Override
    public int getCount() {
        return adapter != null ? adapter.getCount() : 0;
    }

    @Override
    public Object getItemAtPosition(int position) {
        return adapter != null ? adapter.getItem(position) : null;
    }

    /** Get the position within the adapter for a given child position
     *  (accounting for headers). */
    public int getAdapterPosition(int childPosition) {
        return childPosition - mHeaderViewInfos.size();
    }

    /** Get the first visible position. */
    public int getFirstVisiblePosition() {
        return 0; // Simplified: all items visible
    }

    /** Get the last visible position. */
    public int getLastVisiblePosition() {
        int count = getChildCount();
        return count > 0 ? count - 1 : INVALID_POSITION;
    }

    /** Get item at a position. */
    public long getItemIdAtPosition(int position) {
        return adapter != null && position >= 0 && position < adapter.getCount()
                ? adapter.getItemId(position)
                : INVALID_ROW_ID;
    }

    /** Alias so code can reference ListView.ListAdapter. */
    public interface ListAdapter extends android.widget.ListAdapter {}
}
