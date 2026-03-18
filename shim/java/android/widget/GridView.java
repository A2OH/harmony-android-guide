package android.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.widget.GridView
 */
public class GridView extends AdapterView {

    public static final int NO_STRETCH             = 0;
    public static final int STRETCH_SPACING        = 1;
    public static final int STRETCH_COLUMN_WIDTH   = 2;
    public static final int STRETCH_SPACING_UNIFORM = 3;
    public static final int AUTO_FIT = -1;

    protected long nativeHandle;

    private int numColumns        = AUTO_FIT;
    private int columnWidth       = 0;
    private int horizontalSpacing = 0;
    private int verticalSpacing   = 0;
    private int stretchMode       = STRETCH_COLUMN_WIDTH;
    private ListAdapter adapter;

    public GridView(Context context) { super(context); }
    public GridView(Context context, AttributeSet attrs) { super(context, attrs); }
    public GridView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    public void setNumColumns(int numColumns) { this.numColumns = numColumns; }
    public int getNumColumns() { return numColumns; }
    public void setColumnWidth(int columnWidth) { this.columnWidth = columnWidth; }
    public void setHorizontalSpacing(int horizontalSpacing) { this.horizontalSpacing = horizontalSpacing; }
    public void setVerticalSpacing(int verticalSpacing) { this.verticalSpacing = verticalSpacing; }
    public void setStretchMode(int stretchMode) { this.stretchMode = stretchMode; }
    public int getStretchMode() { return stretchMode; }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
    }
    public ListAdapter getAdapter() { return adapter; }

    @Override public int getCount() { return adapter != null ? adapter.getCount() : 0; }
    @Override public Object getItemAtPosition(int position) { return adapter != null ? adapter.getItem(position) : null; }
}
