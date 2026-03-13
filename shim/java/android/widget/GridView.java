package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewGroup;

import android.view.View;
import android.view.ViewGroup;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.GridView → ARKUI_NODE_GRID (node type 11).
 *
 * Exposes column count, spacing, and stretch-mode setters. The adapter pattern
 * is the same as ListView: on setAdapter() we populate the grid with item views.
 */
public class GridView extends AdapterView {

    // ArkUI Grid node type
    static final int NODE_TYPE_GRID = 11;

    // Stretch modes
    public static final int NO_STRETCH             = 0;
    public static final int STRETCH_SPACING        = 1;
    public static final int STRETCH_COLUMN_WIDTH   = 2;
    public static final int STRETCH_SPACING_UNIFORM = 3;

    // Sentinel for auto-fit column count
    public static final int AUTO_FIT = -1;

    // ArkUI attribute IDs for Grid (approximate — actual values may differ)
    private static final int ATTR_GRID_COLUMNS = 1000; // placeholder for column count
    private static final int ATTR_COLUMN_GAP   = 1001;
    private static final int ATTR_ROW_GAP      = 1002;

    private int numColumns        = AUTO_FIT;
    private int columnWidth       = 0;
    private int horizontalSpacing = 0;
    private int verticalSpacing   = 0;
    private int stretchMode       = STRETCH_COLUMN_WIDTH;

    private ListAdapter adapter;

    public GridView() {
        super(NODE_TYPE_GRID);
    }

    // ── Column configuration ──

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        if (nativeHandle != 0 && numColumns != AUTO_FIT) {
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_GRID_COLUMNS, numColumns);
        }
    }

    public int getNumColumns() { return numColumns; }

    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }

    // ── Spacing ──

    public void setHorizontalSpacing(int horizontalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_COLUMN_GAP,
                (float) horizontalSpacing, 0, 0, 0, 1);
        }
    }

    public void setVerticalSpacing(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_ROW_GAP,
                (float) verticalSpacing, 0, 0, 0, 1);
        }
    }

    // ── Stretch mode ──

    public void setStretchMode(int stretchMode) {
        this.stretchMode = stretchMode;
    }

    public int getStretchMode() { return stretchMode; }

    // ── Adapter ──

    public void setAdapter(ListAdapter adapter) {
        removeAllViews();
        this.adapter = adapter;
        if (adapter == null) return;
        for (int i = 0; i < adapter.getCount(); i++) {
            View itemView = adapter.getView(i, null, this);
            if (itemView != null) {
                addView(itemView);
            }
        }
    }

    public ListAdapter getAdapter() { return adapter; }

    // ── AdapterView contract ──

    @Override
    public int getCount() {
        return adapter != null ? adapter.getCount() : 0;
    }

    @Override
    public Object getItemAtPosition(int position) {
        return adapter != null ? adapter.getItem(position) : null;
    }
}
