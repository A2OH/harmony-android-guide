package android.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Shim: android.widget.GridLayout
 */
public class GridLayout extends ViewGroup {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL   = 1;
    public static final int UNDEFINED  = Integer.MIN_VALUE;

    private int columnCount = 0;
    private int rowCount    = 0;
    private int orientation = HORIZONTAL;

    public GridLayout(Context context) {
        super(context);
    }
    public GridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public GridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {}

    public void setColumnCount(int columnCount) { this.columnCount = columnCount; }
    public int getColumnCount() { return columnCount; }
    public void setRowCount(int rowCount) { this.rowCount = rowCount; }
    public int getRowCount() { return rowCount; }
    public void setOrientation(int orientation) { this.orientation = orientation; }
    public int getOrientation() { return orientation; }

    public static Spec spec(int start) { return new Spec(start, 1, 1.0f); }
    public static Spec spec(int start, int size) { return new Spec(start, size, 1.0f); }
    public static Spec spec(int start, float weight) { return new Spec(start, 1, weight); }
    public static Spec spec(int start, int size, float weight) { return new Spec(start, size, weight); }

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

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public Spec rowSpec;
        public Spec columnSpec;
        public LayoutParams(Spec rowSpec, Spec columnSpec) {
            super(WRAP_CONTENT, WRAP_CONTENT);
            this.rowSpec = rowSpec;
            this.columnSpec = columnSpec;
        }
        public LayoutParams() {
            super(WRAP_CONTENT, WRAP_CONTENT);
        }
    }
}
