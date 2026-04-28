package com.google.android.material.slider;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;
import java.util.ArrayList;

/**
 * First-slice Slider shim backed by SeekBar.
 */
public class Slider extends SeekBar {
    private float valueFrom = 0f;
    private float valueTo = 100f;
    private float value = 0f;
    private final ArrayList<OnChangeListener> listeners = new ArrayList<OnChangeListener>();

    public Slider(Context context) {
        super(context);
        init();
    }

    public Slider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Slider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setMax(100);
    }

    public void setValueFrom(float valueFrom) {
        this.valueFrom = valueFrom;
    }

    public float getValueFrom() {
        return valueFrom;
    }

    public void setValueTo(float valueTo) {
        this.valueTo = valueTo;
    }

    public float getValueTo() {
        return valueTo;
    }

    public void setValue(float value) {
        this.value = value;
        float range = valueTo - valueFrom;
        int progress = range <= 0f ? 0 : (int) (((value - valueFrom) * 100f) / range);
        if (progress < 0) {
            progress = 0;
        }
        if (progress > 100) {
            progress = 100;
        }
        setProgress(progress);
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onValueChange(this, value, false);
        }
    }

    public float getValue() {
        return value;
    }

    public void addOnChangeListener(OnChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public interface OnChangeListener {
        void onValueChange(Slider slider, float value, boolean fromUser);
    }
}
