package android.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.widget.AdapterView
 */
public class AdapterView extends ViewGroup {

    public static final int INVALID_POSITION = -1;
    public static final long INVALID_ROW_ID  = Long.MIN_VALUE;

    private OnItemClickListener  onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public AdapterView(Context context) {
        super(context);
    }
    public AdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public AdapterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {}

    public int getCount() { return 0; }
    public Object getItemAtPosition(int position) { return null; }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    protected void dispatchItemClick(View itemView, int position, long id) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(this, itemView, position, id);
        }
    }

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
