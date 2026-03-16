package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.ListView → ARKUI_NODE_LIST
 *
 * ArkUI List is a scrollable list container with ListItem children.
 * The Adapter pattern is preserved — when setAdapter is called,
 * we populate the list by creating ListItem nodes for each row.
 */
public class ListView extends AbsListView {
    static final int NODE_TYPE_LIST = 10;
    static final int NODE_TYPE_LIST_ITEM = 19;

    private android.widget.ListAdapter adapter;

    /** Internal observer that re-populates children when adapter data changes. */
    private final BaseAdapter.DataSetObserver dataObserver = new BaseAdapter.DataSetObserver() {
        @Override public void onChanged() { populateFromAdapter(); }
        @Override public void onInvalidated() { removeAllViews(); }
    };

    public ListView() {
        super(NODE_TYPE_LIST);
    }

    public void setAdapter(android.widget.ListAdapter adapter) {
        // Unregister from old adapter
        if (this.adapter instanceof BaseAdapter) {
            ((BaseAdapter) this.adapter).unregisterDataSetObserver(dataObserver);
        }

        // Remove old items
        removeAllViews();
        this.adapter = adapter;
        if (adapter == null) return;

        // Register for future data changes
        if (adapter instanceof BaseAdapter) {
            ((BaseAdapter) adapter).registerDataSetObserver(dataObserver);
        }

        // Populate list items from adapter
        populateFromAdapter();
    }

    /** Re-creates all child views from the adapter. */
    private void populateFromAdapter() {
        removeAllViews();
        if (adapter == null) return;
        for (int i = 0; i < adapter.getCount(); i++) {
            View itemView = adapter.getView(i, null, this);
            if (itemView != null) {
                addView(itemView);
            }
        }
    }

    public android.widget.ListAdapter getAdapter() { return adapter; }

    /**
     * Simulates a click on the item at the given position.
     * Dispatches to the OnItemClickListener if one is set.
     */
    public boolean performItemClick(View view, int position, long id) {
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

    /** Alias so code can reference ListView.ListAdapter. */
    public interface ListAdapter extends android.widget.ListAdapter {}
}
