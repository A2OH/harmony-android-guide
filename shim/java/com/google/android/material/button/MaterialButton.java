package com.google.android.material.button;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * First-slice MaterialButton shim.
 *
 * This is intentionally small: it preserves the Material class/API surface used
 * by controlled canaries while rendering through the normal Westlake Button
 * path.
 */
public class MaterialButton extends Button {
    private int backgroundColor = 0xffd32323;
    private int strokeColor = 0x00000000;
    private int strokeWidth;
    private int cornerRadius = 18;
    private Drawable icon;

    public MaterialButton(Context context) {
        super(context);
        applyStyle();
    }

    public MaterialButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyStyle();
    }

    public MaterialButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyStyle();
    }

    private void applyStyle() {
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setColor(backgroundColor);
        bg.setCornerRadius(cornerRadius);
        if (strokeWidth > 0) {
            bg.setStroke(strokeWidth, strokeColor);
        }
        setBackground(bg);
        setTextColor(0xffffffff);
        setMinHeight(48);
        setPadding(18, 8, 18, 8);
        setClickable(true);
    }

    public void setCornerRadius(int radius) {
        cornerRadius = radius;
        applyStyle();
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setStrokeWidth(int width) {
        strokeWidth = width;
        applyStyle();
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeColor(ColorStateList color) {
        if (color != null) {
            strokeColor = color.getDefaultColor();
            applyStyle();
        }
    }

    public ColorStateList getStrokeColor() {
        return ColorStateList.valueOf(strokeColor);
    }

    @Override
    public void setBackgroundTintList(ColorStateList tint) {
        if (tint != null) {
            backgroundColor = tint.getDefaultColor();
            applyStyle();
        }
    }

    public void setIcon(Drawable drawable) {
        icon = drawable;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIconResource(int resId) {
    }

    public void setIconTint(ColorStateList tint) {
    }
}
