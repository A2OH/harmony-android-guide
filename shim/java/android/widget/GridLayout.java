package android.widget;
import android.view.ViewGroup;
import android.view.ViewGroup;

import android.view.ViewGroup;

/**
 * Shim: android.widget.GridLayout → ARKUI_NODE_GRID (node type 11).
 *
 * GridLayout arranges children in a grid of rows and columns. The Spec inner class
 * and static spec() factory are included for source compatibility with code that
 * uses GridLayout.spec(row) / GridLayout.spec(col, size) syntax.
 */
public class GridLayout extends ViewGroup {

    // ArkUI Grid node type (same as GridView)
    static final int NODE_TYPE_GRID = 11;

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL   = 1;

    public static final int UNDEFINED  = Integer.MIN_VALUE;

    private int columnCount = 0;
    private int rowCount    = 0;
    private int orientation = HORIZONTAL;

    public GridLayout() {
        super(NODE_TYPE_GRID);
    }

    // ── Column / row counts ──

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public int getColumnCount() { return columnCount; }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getRowCount() { return rowCount; }

    // ── Orientation ──

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getOrientation() { return orientation; }

    // ── Static Spec factory ──

    public static Spec spec(int start) {
        return new Spec(start, 1, 1.0f);
    }

    public static Spec spec(int start, int size) {
        return new Spec(start, size, 1.0f);
    }

    public static Spec spec(int start, float weight) {
        return new Spec(start, 1, weight);
    }

    public static Spec spec(int start, int size, float weight) {
        return new Spec(start, size, weight);
    }

    // ── Inner classes ──

    /** Encodes a row or column range and optional weight. */
    public static class Spec {
        public final int startIndex;
        public final int size;
        public final float weight;

        Spec(int startIndex, int size, float weight) {
            this.startIndex = startIndex;
            this.size = size;
            this.weight = weight;
        }
    }

    public static class Object extends ViewGroup.MarginLayoutParams {
        public Spec rowSpec;
        public Spec columnSpec;

        public Object(Spec rowSpec, Spec columnSpec) {
            super(WRAP_CONTENT, WRAP_CONTENT);
            this.rowSpec = rowSpec;
            this.columnSpec = columnSpec;
        }

        public Object() {
            super(WRAP_CONTENT, WRAP_CONTENT);
        }
    }
}
