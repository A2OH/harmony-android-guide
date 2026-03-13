package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewGroup;

import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.widget.AdapterView — abstract base for list/grid/spinner widgets
 * that are backed by an Adapter.
 *
 * Maps to an ArkUI STACK node by default; concrete subclasses override with the
 * appropriate node type (LIST, GRID, …).
 */
public class AdapterView extends ViewGroup {

    public static final int INVALID_POSITION = -1;
    public static final long INVALID_ROW_ID  = Long.MIN_VALUE;

    private OnItemClickListener  onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    protected AdapterView() {
        super(); // STACK node type from ViewGroup default
    }

    protected AdapterView(int arkuiNodeType) {
        super(arkuiNodeType);
    }

    // ── Abstract adapter contract ──

    public int getCount() { return 0; }
    public Object getItemAtPosition(int position) { return null; }

    // ── Click listeners ──

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    /** Called by subclass or adapter infrastructure when an item is tapped. */
    protected void dispatchItemClick(View itemView, int position, long id) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(this, itemView, position, id);
        }
    }

    // ── Interfaces ──

    public interface OnItemClickListener {
        void onItemClick(AdapterView parent, View view, int position, long id);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(AdapterView parent, View view, int position, long id);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(AdapterView parent, View view, int position, long id);
        void onNothingSelected(AdapterView parent);
    }

    private OnItemSelectedListener onItemSelectedListener;

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    public OnItemSelectedListener getOnItemSelectedListener() {
        return onItemSelectedListener;
    }
}
