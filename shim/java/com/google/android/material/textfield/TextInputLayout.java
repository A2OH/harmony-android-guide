package com.google.android.material.textfield;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * First-slice TextInputLayout shim.
 */
public class TextInputLayout extends LinearLayout {
    public static final int END_ICON_NONE = 0;
    public static final int END_ICON_CLEAR_TEXT = 2;
    public static final int BOX_BACKGROUND_OUTLINE = 2;

    private CharSequence hint;
    private CharSequence helperText;
    private int boxBackgroundColor = 0xffffffff;
    private int endIconMode = END_ICON_NONE;

    public TextInputLayout(Context context) {
        super(context);
        init();
    }

    public TextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        applyStyle();
    }

    private void applyStyle() {
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setColor(boxBackgroundColor);
        bg.setCornerRadius(18);
        bg.setStroke(1, 0xffd7d2d4);
        setBackground(bg);
        setPadding(14, 8, 14, 8);
    }

    public void setHint(CharSequence hint) {
        this.hint = hint;
    }

    public CharSequence getHint() {
        return hint;
    }

    public void setHelperText(CharSequence helperText) {
        this.helperText = helperText;
    }

    public CharSequence getHelperText() {
        return helperText;
    }

    public void setBoxBackgroundColor(int color) {
        boxBackgroundColor = color;
        applyStyle();
    }

    public void setBoxBackgroundMode(int mode) {
    }

    public void setEndIconMode(int mode) {
        endIconMode = mode;
    }

    public int getEndIconMode() {
        return endIconMode;
    }
}
