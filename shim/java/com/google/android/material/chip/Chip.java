package com.google.android.material.chip;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * First-slice Chip shim. It behaves as a checkable Button for controlled
 * Material API validation.
 */
public class Chip extends Button {
    private boolean checked;
    private boolean checkable = true;
    private int chipBackgroundColor = 0xffffffff;
    private int chipStrokeColor = 0xffd7d2d4;
    private int chipStrokeWidth = 1;

    public Chip(Context context) {
        super(context);
        applyStyle();
    }

    public Chip(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyStyle();
    }

    public Chip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyStyle();
    }

    private void applyStyle() {
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(18);
        bg.setColor(checked ? 0xffffebee : chipBackgroundColor);
        bg.setStroke(chipStrokeWidth, checked ? 0xffd32323 : chipStrokeColor);
        setBackground(bg);
        setTextColor(checked ? 0xffa82020 : 0xff3f3a3b);
        setMinHeight(38);
        setPadding(14, 6, 14, 6);
        setClickable(true);
    }

    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
    }

    public boolean isCheckable() {
        return checkable;
    }

    public void setChecked(boolean checked) {
        if (checkable) {
            this.checked = checked;
            applyStyle();
        }
    }

    public boolean isChecked() {
        return checked;
    }

    public void toggle() {
        setChecked(!checked);
    }

    public void setChipBackgroundColor(ColorStateList color) {
        if (color != null) {
            chipBackgroundColor = color.getDefaultColor();
            applyStyle();
        }
    }

    public void setChipStrokeColor(ColorStateList color) {
        if (color != null) {
            chipStrokeColor = color.getDefaultColor();
            applyStyle();
        }
    }

    public void setChipStrokeWidth(float width) {
        chipStrokeWidth = (int) width;
        applyStyle();
    }
}
