package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewGroup;

import android.view.View;
import android.view.ViewGroup;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.LinearLayout → ARKUI_NODE_COLUMN (vertical) / ARKUI_NODE_ROW (horizontal)
 *
 * Default orientation is VERTICAL (COLUMN).
 */
public class LinearLayout extends ViewGroup {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    static final int NODE_TYPE_COLUMN = 16;
    static final int NODE_TYPE_ROW = 17;

    private int orientation = VERTICAL;

    public LinearLayout() {
        super(NODE_TYPE_COLUMN);
    }

    public LinearLayout(android.content.Context context) {
        this();
    }

    public LinearLayout(android.content.Context context, android.util.AttributeSet attrs) {
        this();
    }

    public LinearLayout(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        this();
    }

    /** Create with specific orientation.
     *  Note: changing orientation after creation requires node recreation.
     *  For now, set orientation before adding children. */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        // If orientation differs from what was created, we'd need to recreate.
        // For the shim, we create the correct type based on first setOrientation call.
    }

    public int getOrientation() { return orientation; }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        // Sum children sizes along orientation axis
        int totalWidth = 0, totalHeight = 0;
        int maxWidth = 0, maxHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            android.view.View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            if (orientation == VERTICAL) {
                totalHeight += child.getMeasuredHeight();
                maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
            } else {
                totalWidth += child.getMeasuredWidth();
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
        }
        if (orientation == VERTICAL) {
            setMeasuredDimension(
                resolveSize(maxWidth + getPaddingLeft() + getPaddingRight(), widthMeasureSpec),
                resolveSize(totalHeight + getPaddingTop() + getPaddingBottom(), heightMeasureSpec));
        } else {
            setMeasuredDimension(
                resolveSize(totalWidth + getPaddingLeft() + getPaddingRight(), widthMeasureSpec),
                resolveSize(maxHeight + getPaddingTop() + getPaddingBottom(), heightMeasureSpec));
        }
    }

    private static int resolveSize(int size, int measureSpec) {
        int specMode = android.view.View.MeasureSpec.getMode(measureSpec);
        int specSize = android.view.View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case android.view.View.MeasureSpec.EXACTLY: return specSize;
            case android.view.View.MeasureSpec.AT_MOST: return Math.min(size, specSize);
            default: return size;
        }
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        for (int i = 0; i < getChildCount(); i++) {
            android.view.View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            int cw = child.getMeasuredWidth() > 0 ? child.getMeasuredWidth() : (r - l);
            int ch = child.getMeasuredHeight() > 0 ? child.getMeasuredHeight() : 0;
            if (orientation == VERTICAL) {
                child.layout(childLeft, childTop, childLeft + cw, childTop + ch);
                childTop += ch;
            } else {
                child.layout(childLeft, childTop, childLeft + cw, childTop + ch);
                childLeft += cw;
            }
        }
    }

    // ── LayoutParams with weight support ──

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public float weight;
        public int gravity = -1;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height);
            this.weight = weight;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }
}
