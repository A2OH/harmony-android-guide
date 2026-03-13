package android.widget;

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

    private ListAdapter adapter;

    public ListView() {
        super(NODE_TYPE_LIST);
    }

    public void setAdapter(ListAdapter adapter) {
        // Remove old items
        removeAllViews();
        this.adapter = adapter;
        if (adapter == null) return;

        // Populate list items from adapter
        for (int i = 0; i < adapter.getCount(); i++) {
            View itemView = adapter.getView(i, null, this);
            if (itemView != null) {
                addView(itemView);
            }
        }
    }

    public ListAdapter getAdapter() { return adapter; }
}
