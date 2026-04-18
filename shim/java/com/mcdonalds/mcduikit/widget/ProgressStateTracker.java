package com.mcdonalds.mcduikit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ProgressStateTracker extends LinearLayout {
    public enum ProgressState {
        STATE_ONE,
        STATE_TWO,
        STATE_THREE
    }

    private final ImageView mFirstStateDotView;
    private final ImageView mSecondStateDotView;
    private final ImageView mThirdStateDotView;
    private final McDTextView mFirstStateText;
    private final McDTextView mSecondStateText;
    private final McDTextView mThirdStateText;

    public ProgressStateTracker(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        mFirstStateDotView = buildDot(context);
        mSecondStateDotView = buildDot(context);
        mThirdStateDotView = buildDot(context);
        mFirstStateText = new McDTextView(context);
        mSecondStateText = new McDTextView(context);
        mThirdStateText = new McDTextView(context);
        addView(mFirstStateDotView);
        addView(mSecondStateDotView);
        addView(mThirdStateDotView);
    }

    public ProgressStateTracker(Context context, AttributeSet attrs) {
        this(context);
    }

    public ProgressStateTracker(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context);
    }

    private ImageView buildDot(Context context) {
        ImageView dot = new ImageView(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(12, 12);
        lp.leftMargin = 4;
        lp.rightMargin = 4;
        dot.setLayoutParams(lp);
        dot.setBackgroundColor(0xFFB0B0B0);
        return dot;
    }

    private void applyDotState(ImageView dot, boolean active) {
        if (dot != null) {
            dot.setBackgroundColor(active ? 0xFFF5C518 : 0xFFB0B0B0);
        }
    }

    public void setState(ProgressState state) {
        applyDotState(mFirstStateDotView, state == ProgressState.STATE_ONE);
        applyDotState(mSecondStateDotView, state == ProgressState.STATE_TWO);
        applyDotState(mThirdStateDotView, state == ProgressState.STATE_THREE);
    }

    public ImageView getFirstStateDotView() {
        return mFirstStateDotView;
    }

    public ImageView getSecondStateDotView() {
        return mSecondStateDotView;
    }

    public ImageView getThirdStateDotView() {
        return mThirdStateDotView;
    }

    public void a() {}

    public void b() {}

    public void c() {}

    public void d() {}

    public void e() {}
}
