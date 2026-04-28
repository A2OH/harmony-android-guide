package com.google.android.material.chip;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * First-slice ChipGroup shim with simple single-selection bookkeeping.
 */
public class ChipGroup extends LinearLayout {
    private boolean singleSelection;
    private int checkedChipId = View.NO_ID;

    public ChipGroup(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
    }

    public ChipGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
    }

    public void setSingleSelection(boolean singleSelection) {
        this.singleSelection = singleSelection;
    }

    public boolean isSingleSelection() {
        return singleSelection;
    }

    public void setChipSpacing(int spacing) {
        setPadding(spacing, spacing / 2, spacing, spacing / 2);
    }

    public void check(int id) {
        checkedChipId = id;
        if (!singleSelection) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof Chip) {
                ((Chip) child).setChecked(child.getId() == id);
            }
        }
    }

    public void clearCheck() {
        checkedChipId = View.NO_ID;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof Chip) {
                ((Chip) child).setChecked(false);
            }
        }
    }

    public int getCheckedChipId() {
        return checkedChipId;
    }
}
