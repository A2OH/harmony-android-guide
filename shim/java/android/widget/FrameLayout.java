package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;

/**
 * Shim: android.widget.FrameLayout with AOSP-based gravity and margin-aware layout.
 *
 * Maps to ARKUI_NODE_STACK. Children are stacked on top of each other.
 */
public class FrameLayout extends ViewGroup {
    static final int NODE_TYPE_STACK = 8;

    private static final int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;

    private final java.util.ArrayList<View> mMatchParentChildren = new java.util.ArrayList<View>(1);

    public FrameLayout() {
        super(NODE_TYPE_STACK);
    }

    public FrameLayout(Object context) {
        this();
    }

    public FrameLayout(android.content.Context context, android.util.AttributeSet attrs) {
        this();
    }

    public FrameLayout(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        this();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        boolean measureMatchParentChildren =
                MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY
                || MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;
        mMatchParentChildren.clear();

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                LayoutParams lp = getFrameLayoutParams(child);
                maxWidth = Math.max(maxWidth,
                        child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                maxHeight = Math.max(maxHeight,
                        child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
                childState = combineMeasuredStates(childState, child.getMeasuredState());
                if (measureMatchParentChildren) {
                    if (lp.width == LayoutParams.MATCH_PARENT
                            || lp.height == LayoutParams.MATCH_PARENT) {
                        mMatchParentChildren.add(child);
                    }
                }
            }
        }

        // Account for padding
        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();

        // Check against our minimum size
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));

        // Re-measure MATCH_PARENT children now that we know our own size
        count = mMatchParentChildren.size();
        if (count > 1) {
            for (int i = 0; i < count; i++) {
                View child = mMatchParentChildren.get(i);
                MarginLayoutParams lp = getFrameLayoutParams(child);

                int childWidthMeasureSpec;
                if (lp.width == LayoutParams.MATCH_PARENT) {
                    int width = Math.max(0, getMeasuredWidth()
                            - getPaddingLeft() - getPaddingRight()
                            - lp.leftMargin - lp.rightMargin);
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                            width, MeasureSpec.EXACTLY);
                } else {
                    childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                            getPaddingLeft() + getPaddingRight()
                            + lp.leftMargin + lp.rightMargin,
                            lp.width);
                }

                int childHeightMeasureSpec;
                if (lp.height == LayoutParams.MATCH_PARENT) {
                    int height = Math.max(0, getMeasuredHeight()
                            - getPaddingTop() - getPaddingBottom()
                            - lp.topMargin - lp.bottomMargin);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                            height, MeasureSpec.EXACTLY);
                } else {
                    childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                            getPaddingTop() + getPaddingBottom()
                            + lp.topMargin + lp.bottomMargin,
                            lp.height);
                }

                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        layoutChildren(l, t, r, b);
    }

    void layoutChildren(int left, int top, int right, int bottom) {
        int count = getChildCount();

        int parentLeft = getPaddingLeft();
        int parentRight = right - left - getPaddingRight();

        int parentTop = getPaddingTop();
        int parentBottom = bottom - top - getPaddingBottom();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams lp = getFrameLayoutParams(child);

                // Use measured dimensions; if unmeasured, default to parent size
                int parentW = parentRight - parentLeft;
                int parentH = parentBottom - parentTop;
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                if (width == 0 && height == 0) {
                    // Child was never measured -- default to fill parent (FrameLayout default)
                    width = parentW;
                    height = parentH;
                }

                int childLeft;
                int childTop;

                int gravity = lp.gravity;
                if (gravity == -1) {
                    gravity = DEFAULT_CHILD_GRAVITY;
                }

                int layoutDirection = getLayoutDirection();
                int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
                int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

                switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                    case Gravity.CENTER_HORIZONTAL:
                        childLeft = parentLeft + (parentRight - parentLeft - width) / 2
                                + lp.leftMargin - lp.rightMargin;
                        break;
                    case Gravity.RIGHT:
                        childLeft = parentRight - width - lp.rightMargin;
                        break;
                    case Gravity.LEFT:
                    default:
                        childLeft = parentLeft + lp.leftMargin;
                }

                switch (verticalGravity) {
                    case Gravity.TOP:
                        childTop = parentTop + lp.topMargin;
                        break;
                    case Gravity.CENTER_VERTICAL:
                        childTop = parentTop + (parentBottom - parentTop - height) / 2
                                + lp.topMargin - lp.bottomMargin;
                        break;
                    case Gravity.BOTTOM:
                        childTop = parentBottom - height - lp.bottomMargin;
                        break;
                    default:
                        childTop = parentTop + lp.topMargin;
                }

                child.layout(childLeft, childTop, childLeft + width, childTop + height);
            }
        }
    }

    /** Safely get FrameLayout.LayoutParams from child, creating default if needed. */
    private LayoutParams getFrameLayoutParams(View child) {
        Object lpo = child.getLayoutParams();
        if (lpo instanceof LayoutParams) {
            return (LayoutParams) lpo;
        }
        LayoutParams lp;
        if (lpo instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lpo;
            lp = new LayoutParams(mlp.width, mlp.height);
            lp.leftMargin = mlp.leftMargin;
            lp.topMargin = mlp.topMargin;
            lp.rightMargin = mlp.rightMargin;
            lp.bottomMargin = mlp.bottomMargin;
        } else if (lpo instanceof ViewGroup.LayoutParams) {
            ViewGroup.LayoutParams vlp = (ViewGroup.LayoutParams) lpo;
            lp = new LayoutParams(vlp.width, vlp.height);
        } else {
            lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        child.setLayoutParams(lp);
        return lp;
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public int gravity = -1;

        public LayoutParams() {
            super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source.width, source.height);
        }
    }
}
