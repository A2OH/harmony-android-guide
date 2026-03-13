package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewGroup;

import android.view.View;
import android.view.ViewGroup;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.RadioGroup → LinearLayout container that enforces
 * single-selection among child RadioButton views.
 *
 * Maps to an ArkUI COLUMN node (same as LinearLayout vertical).
 */
public class RadioGroup extends LinearLayout {

    private int checkedId = -1; // -1 = no selection
    private OnCheckedChangeListener onCheckedChangeListener;

    public RadioGroup() {
        super();
        setOrientation(VERTICAL);
    }

    // ── Check management ──

    /**
     * Check the RadioButton with the given view id; unchecks all others.
     * Pass -1 to clear the selection.
     */
    public void check(int id) {
        if (id == checkedId) return;

        // Uncheck previously checked button
        if (checkedId != -1) {
            View prev = (View) findViewById(checkedId);
            if (prev instanceof RadioButton) {
                ((RadioButton) prev).setChecked(false);
            }
        }

        checkedId = id;

        if (id != -1) {
            View next = (View) findViewById(id);
            if (next instanceof RadioButton) {
                ((RadioButton) next).setChecked(true);
            }
        }

        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, checkedId);
        }
    }

    /** Returns the id of the currently checked RadioButton, or -1 if none. */
    public int getCheckedRadioButtonId() { return checkedId; }

    /** Clear the current selection. */
    public void clearCheck() {
        check(-1);
    }

    // ── Listener ──

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    // ── Override addView to wire up RadioButton state changes ──

    
    public void addView(View child) {
        super.addView(child);
        wireRadioButton(child);
    }

    
    public void addView(View child, int index) {
        super.addView(child, index);
        wireRadioButton(child);
    }

    
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        wireRadioButton(child);
    }


    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        wireRadioButton(child);
    }

    private void wireRadioButton(View child) {
        if (child instanceof RadioButton) {
            final RadioButton rb = (RadioButton) child;
            rb.setOnCheckedChangeListener((button, isChecked) -> {
                if (isChecked) {
                    check(button.getId());
                }
            });
        }
    }

    // ── Interface ──

    public interface OnCheckedChangeListener {
        void onCheckedChanged(RadioGroup group, int checkedId);
    }
}
