package com.google.android.material.card;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * First-slice MaterialCardView shim with card color, radius, stroke, checked
 * state, and elevation placeholders.
 */
public class MaterialCardView extends FrameLayout {
    private int cardColor = 0xffffffff;
    private int strokeColor = 0x00000000;
    private int strokeWidth;
    private float radius = 18f;
    private float elevation;
    private boolean checked;

    public MaterialCardView(Context context) {
        super(context);
        applyStyle();
    }

    public MaterialCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyStyle();
    }

    public MaterialCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyStyle();
    }

    private void applyStyle() {
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setColor(checked ? 0xfffff5f5 : cardColor);
        bg.setCornerRadius(radius);
        if (strokeWidth > 0) {
            bg.setStroke(strokeWidth, checked ? 0xffd32323 : strokeColor);
        }
        setBackground(bg);
        setPadding(16, 16, 16, 16);
        setClickable(true);
    }

    public void setCardBackgroundColor(int color) {
        cardColor = color;
        applyStyle();
    }

    public void setCardBackgroundColor(ColorStateList color) {
        if (color != null) {
            setCardBackgroundColor(color.getDefaultColor());
        }
    }

    public ColorStateList getCardBackgroundColor() {
        return ColorStateList.valueOf(cardColor);
    }

    public void setRadius(float radius) {
        this.radius = radius;
        applyStyle();
    }

    public float getRadius() {
        return radius;
    }

    public void setCardElevation(float elevation) {
        this.elevation = elevation;
    }

    public float getCardElevation() {
        return elevation;
    }

    public void setStrokeColor(int color) {
        strokeColor = color;
        applyStyle();
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeWidth(int width) {
        strokeWidth = width;
        applyStyle();
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        applyStyle();
    }

    public boolean isChecked() {
        return checked;
    }
}
