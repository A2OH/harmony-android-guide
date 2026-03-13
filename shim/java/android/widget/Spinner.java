package android.widget;
import android.text.Selection;
import android.view.View;
import android.view.ViewGroup;
import android.text.Selection;
import android.view.View;
import android.view.ViewGroup;

import android.view.View;
import android.view.ViewGroup;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.Spinner → ArkUI Select/Picker concept.
 *
 * ArkUI does not expose a "Spinner" node; we use a generic STACK node and
 * track selection state in Java. UI realisation is left to the native bridge.
 */
public class Spinner extends AdapterView {

    public static final int MODE_DIALOG   = 0;
    public static final int MODE_DROPDOWN = 1;

    private int mode;
    private int selectedPosition = 0;
    private ListAdapter adapter;

    public Spinner() {
        this(MODE_DROPDOWN);
    }

    public Spinner(int mode) {
        super(); // STACK node via ViewGroup default
        this.mode = mode;
    }

    // ── Selection ──

    public void setSelection(int position) {
        this.selectedPosition = position;
        OnItemSelectedListener listener = getOnItemSelectedListener();
        if (listener != null && adapter != null) {
            listener.onItemSelected(this, null, position,
                adapter.getItemId(position));
        }
    }

    public int getSelectedItemPosition() { return selectedPosition; }

    public Object getSelectedItem() {
        return adapter != null ? adapter.getItem(selectedPosition) : null;
    }

    // ── Adapter ──

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        this.selectedPosition = 0;
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

    // ── Mode ──

    public int getMode() { return mode; }

}
