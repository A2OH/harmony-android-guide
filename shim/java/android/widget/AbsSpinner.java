package android.widget;
import android.text.Selection;
import android.view.View;
import android.text.Selection;
import android.view.View;

import android.view.View;

/**
 * Shim: android.widget.AbsSpinner — abstract base class for spinner-like widgets.
 *
 * Provides adapter management, selection tracking, and view recycling stubs.
 * Concrete subclasses (Spinner, Gallery) add mode-specific behaviour.
 */
public class AbsSpinner extends AdapterView {

    private Object adapter;
    private int selectedPosition = INVALID_POSITION;

    protected AbsSpinner(android.content.Context context) {
        super(context);
    }
    protected AbsSpinner(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }
    protected AbsSpinner(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // ── Adapter ──

    public void setAdapter(Object adapter) {
        this.adapter = adapter;
        this.selectedPosition = INVALID_POSITION;
    }

    public Object getAdapter() {
        return null;
    }

    // ── AdapterView contract ──

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItemAtPosition(int position) {
        return null;
    }

    // ── Selection ──

    public void setSelection(int position, boolean animate) {
        this.selectedPosition = position;
    }

    public void setSelection(int position) {
        setSelection(position, false);
    }

    public View getSelectedView() {
        return null;
    }

    public int getSelectedItemPosition() {
        return selectedPosition;
    }
}
