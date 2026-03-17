package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.LinearLayout with AOSP-based weight distribution and gravity.
 *
 * Maps to ARKUI_NODE_COLUMN (vertical) / ARKUI_NODE_ROW (horizontal).
 * Default orientation is VERTICAL.
 */
public class LinearLayout extends ViewGroup {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    static final int NODE_TYPE_COLUMN = 16;
    static final int NODE_TYPE_ROW = 17;

    private int mOrientation = VERTICAL;
    private int mGravity = Gravity.START | Gravity.TOP;
    private float mWeightSum = -1.0f;
    private int mTotalLength;

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

    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }

    public int getOrientation() { return mOrientation; }

    public void setGravity(int gravity) {
        if (mGravity != gravity) {
            if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
                gravity |= Gravity.START;
            }
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
                gravity |= Gravity.TOP;
            }
            mGravity = gravity;
        }
    }

    public int getGravity() { return mGravity; }

    public void setWeightSum(float weightSum) { mWeightSum = weightSum; }
    public float getWeightSum() { return mWeightSum; }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mOrientation == VERTICAL) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        }
    }

    // ── Vertical measurement (AOSP-based) ──

    void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        mTotalLength = 0;
        int maxWidth = 0;
        int childState = 0;
        int alternativeMaxWidth = 0;
        int weightedMaxWidth = 0;
        boolean allFillParent = true;
        float totalWeight = 0;

        int count = getChildCount();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        boolean matchWidth = false;
        boolean skippedMeasure = false;
        int consumedExcessSpace = 0;

        // Pass 1: measure non-weight children, accumulate totalLength
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }

            LayoutParams lp = getLinearLayoutParams(child);
            totalWeight += lp.weight;

            boolean useExcessSpace = lp.height == 0 && lp.weight > 0;
            if (heightMode == MeasureSpec.EXACTLY && useExcessSpace) {
                // Optimization: skip measuring children that only use excess space
                int totalLength = mTotalLength;
                mTotalLength = Math.max(totalLength, totalLength + lp.topMargin + lp.bottomMargin);
                skippedMeasure = true;
            } else {
                if (useExcessSpace) {
                    lp.height = LayoutParams.WRAP_CONTENT;
                }

                int usedHeight = totalWeight == 0 ? mTotalLength : 0;
                measureChildBeforeLayout(child, i, widthMeasureSpec, 0,
                        heightMeasureSpec, usedHeight);

                int childHeight = child.getMeasuredHeight();
                if (useExcessSpace) {
                    lp.height = 0;
                    consumedExcessSpace += childHeight;
                }

                int totalLength = mTotalLength;
                mTotalLength = Math.max(totalLength, totalLength + childHeight
                        + lp.topMargin + lp.bottomMargin);
            }

            boolean matchWidthLocally = false;
            if (widthMode != MeasureSpec.EXACTLY && lp.width == LayoutParams.MATCH_PARENT) {
                matchWidth = true;
                matchWidthLocally = true;
            }

            int margin = lp.leftMargin + lp.rightMargin;
            int measuredWidth = child.getMeasuredWidth() + margin;
            maxWidth = Math.max(maxWidth, measuredWidth);
            childState = combineMeasuredStates(childState, child.getMeasuredState());

            allFillParent = allFillParent && lp.width == LayoutParams.MATCH_PARENT;
            if (lp.weight > 0) {
                weightedMaxWidth = Math.max(weightedMaxWidth,
                        matchWidthLocally ? margin : measuredWidth);
            } else {
                alternativeMaxWidth = Math.max(alternativeMaxWidth,
                        matchWidthLocally ? margin : measuredWidth);
            }
        }

        // Add padding
        mTotalLength += getPaddingTop() + getPaddingBottom();

        int heightSize = mTotalLength;
        heightSize = Math.max(heightSize, getSuggestedMinimumHeight());

        int heightSizeAndState = resolveSizeAndState(heightSize, heightMeasureSpec, 0);
        heightSize = heightSizeAndState & MEASURED_SIZE_MASK;

        // Pass 2: distribute excess space to weighted children
        int remainingExcess = heightSize - mTotalLength + consumedExcessSpace;
        if (skippedMeasure || (remainingExcess != 0 && totalWeight > 0.0f)) {
            float remainingWeightSum = mWeightSum > 0.0f ? mWeightSum : totalWeight;

            mTotalLength = 0;

            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child == null || child.getVisibility() == View.GONE) {
                    continue;
                }

                LayoutParams lp = getLinearLayoutParams(child);
                float childWeight = lp.weight;
                if (childWeight > 0) {
                    int share = (int) (childWeight * remainingExcess / remainingWeightSum);
                    remainingExcess -= share;
                    remainingWeightSum -= childWeight;

                    int childHeight;
                    if (lp.height == 0 && heightMode == MeasureSpec.EXACTLY) {
                        childHeight = share;
                    } else {
                        childHeight = child.getMeasuredHeight() + share;
                    }

                    int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                            Math.max(0, childHeight), MeasureSpec.EXACTLY);
                    int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                            getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
                            lp.width);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                    childState = combineMeasuredStates(childState, child.getMeasuredState()
                            & (MEASURED_STATE_MASK >> MEASURED_HEIGHT_STATE_SHIFT));
                }

                int margin = lp.leftMargin + lp.rightMargin;
                int measuredWidth = child.getMeasuredWidth() + margin;
                maxWidth = Math.max(maxWidth, measuredWidth);

                boolean matchWidthLocally = widthMode != MeasureSpec.EXACTLY
                        && lp.width == LayoutParams.MATCH_PARENT;
                alternativeMaxWidth = Math.max(alternativeMaxWidth,
                        matchWidthLocally ? margin : measuredWidth);
                allFillParent = allFillParent && lp.width == LayoutParams.MATCH_PARENT;

                int totalLength = mTotalLength;
                mTotalLength = Math.max(totalLength, totalLength + child.getMeasuredHeight()
                        + lp.topMargin + lp.bottomMargin);
            }

            mTotalLength += getPaddingTop() + getPaddingBottom();
        } else {
            alternativeMaxWidth = Math.max(alternativeMaxWidth, weightedMaxWidth);
        }

        if (!allFillParent && widthMode != MeasureSpec.EXACTLY) {
            maxWidth = alternativeMaxWidth;
        }

        maxWidth += getPaddingLeft() + getPaddingRight();
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                heightSizeAndState);

        if (matchWidth) {
            forceUniformWidth(count, heightMeasureSpec);
        }
    }

    // ── Horizontal measurement (AOSP-based) ──

    void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        mTotalLength = 0;
        int maxHeight = 0;
        int childState = 0;
        int alternativeMaxHeight = 0;
        int weightedMaxHeight = 0;
        boolean allFillParent = true;
        float totalWeight = 0;

        int count = getChildCount();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        boolean matchHeight = false;
        boolean skippedMeasure = false;
        int consumedExcessSpace = 0;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }

            LayoutParams lp = getLinearLayoutParams(child);
            totalWeight += lp.weight;

            boolean useExcessSpace = lp.width == 0 && lp.weight > 0;
            if (widthMode == MeasureSpec.EXACTLY && useExcessSpace) {
                int totalLength = mTotalLength;
                mTotalLength = Math.max(totalLength, totalLength + lp.leftMargin + lp.rightMargin);
                skippedMeasure = true;
            } else {
                if (useExcessSpace) {
                    lp.width = LayoutParams.WRAP_CONTENT;
                }

                int usedWidth = totalWeight == 0 ? mTotalLength : 0;
                measureChildBeforeLayout(child, i, widthMeasureSpec, usedWidth,
                        heightMeasureSpec, 0);

                int childWidth = child.getMeasuredWidth();
                if (useExcessSpace) {
                    lp.width = 0;
                    consumedExcessSpace += childWidth;
                }

                int totalLength = mTotalLength;
                mTotalLength = Math.max(totalLength, totalLength + childWidth
                        + lp.leftMargin + lp.rightMargin);
            }

            boolean matchHeightLocally = false;
            if (heightMode != MeasureSpec.EXACTLY && lp.height == LayoutParams.MATCH_PARENT) {
                matchHeight = true;
                matchHeightLocally = true;
            }

            int margin = lp.topMargin + lp.bottomMargin;
            int childHeight = child.getMeasuredHeight() + margin;
            maxHeight = Math.max(maxHeight, childHeight);
            childState = combineMeasuredStates(childState, child.getMeasuredState());

            allFillParent = allFillParent && lp.height == LayoutParams.MATCH_PARENT;
            if (lp.weight > 0) {
                weightedMaxHeight = Math.max(weightedMaxHeight,
                        matchHeightLocally ? margin : childHeight);
            } else {
                alternativeMaxHeight = Math.max(alternativeMaxHeight,
                        matchHeightLocally ? margin : childHeight);
            }
        }

        mTotalLength += getPaddingLeft() + getPaddingRight();

        int widthSize = mTotalLength;
        widthSize = Math.max(widthSize, getSuggestedMinimumWidth());

        int widthSizeAndState = resolveSizeAndState(widthSize, widthMeasureSpec, 0);
        widthSize = widthSizeAndState & MEASURED_SIZE_MASK;

        int remainingExcess = widthSize - mTotalLength + consumedExcessSpace;
        if (skippedMeasure || (remainingExcess != 0 && totalWeight > 0.0f)) {
            float remainingWeightSum = mWeightSum > 0.0f ? mWeightSum : totalWeight;

            mTotalLength = 0;

            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child == null || child.getVisibility() == View.GONE) {
                    continue;
                }

                LayoutParams lp = getLinearLayoutParams(child);
                float childWeight = lp.weight;
                if (childWeight > 0) {
                    int share = (int) (childWeight * remainingExcess / remainingWeightSum);
                    remainingExcess -= share;
                    remainingWeightSum -= childWeight;

                    int childWidth;
                    if (lp.width == 0 && widthMode == MeasureSpec.EXACTLY) {
                        childWidth = share;
                    } else {
                        childWidth = child.getMeasuredWidth() + share;
                    }

                    int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                            Math.max(0, childWidth), MeasureSpec.EXACTLY);
                    int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                            getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
                            lp.height);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                    childState = combineMeasuredStates(childState, child.getMeasuredState()
                            & (MEASURED_STATE_MASK >> MEASURED_HEIGHT_STATE_SHIFT));
                }

                int margin = lp.topMargin + lp.bottomMargin;
                int childHeight = child.getMeasuredHeight() + margin;
                maxHeight = Math.max(maxHeight, childHeight);

                boolean matchHeightLocally = heightMode != MeasureSpec.EXACTLY
                        && lp.height == LayoutParams.MATCH_PARENT;
                alternativeMaxHeight = Math.max(alternativeMaxHeight,
                        matchHeightLocally ? margin : childHeight);
                allFillParent = allFillParent && lp.height == LayoutParams.MATCH_PARENT;

                int totalLength = mTotalLength;
                mTotalLength = Math.max(totalLength, totalLength + child.getMeasuredWidth()
                        + lp.leftMargin + lp.rightMargin);
            }

            mTotalLength += getPaddingLeft() + getPaddingRight();
        } else {
            alternativeMaxHeight = Math.max(alternativeMaxHeight, weightedMaxHeight);
        }

        if (!allFillParent && heightMode != MeasureSpec.EXACTLY) {
            maxHeight = alternativeMaxHeight;
        }

        maxHeight += getPaddingTop() + getPaddingBottom();
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());

        setMeasuredDimension(widthSizeAndState,
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));

        if (matchHeight) {
            forceUniformHeight(count, widthMeasureSpec);
        }
    }

    // ── Layout methods (AOSP-based) ──

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        if (mOrientation == VERTICAL) {
            layoutVertical(l, t, r, b);
        } else {
            layoutHorizontal(l, t, r, b);
        }
    }

    void layoutVertical(int left, int top, int right, int bottom) {
        int paddingLeft = getPaddingLeft();
        int childTop;
        int childLeft;

        int width = right - left;
        int childRight = width - getPaddingRight();
        int childSpace = width - paddingLeft - getPaddingRight();

        int count = getChildCount();

        int majorGravity = mGravity & Gravity.VERTICAL_GRAVITY_MASK;
        int minorGravity = mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;

        switch (majorGravity) {
            case Gravity.BOTTOM:
                childTop = getPaddingTop() + bottom - top - mTotalLength;
                break;
            case Gravity.CENTER_VERTICAL:
                childTop = getPaddingTop() + (bottom - top - mTotalLength) / 2;
                break;
            case Gravity.TOP:
            default:
                childTop = getPaddingTop();
                break;
        }

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child == null || child.getVisibility() == GONE) {
                continue;
            }

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            LayoutParams lp = getLinearLayoutParams(child);

            int gravity = lp.gravity;
            if (gravity < 0) {
                gravity = Gravity.getAbsoluteGravity(minorGravity, getLayoutDirection());
                gravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
                // Default to LEFT if nothing set
                if (gravity == 0) gravity = Gravity.LEFT;
            }
            int absoluteGravity = Gravity.getAbsoluteGravity(gravity, getLayoutDirection());
            switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.CENTER_HORIZONTAL:
                    childLeft = paddingLeft + ((childSpace - childWidth) / 2)
                            + lp.leftMargin - lp.rightMargin;
                    break;
                case Gravity.RIGHT:
                    childLeft = childRight - childWidth - lp.rightMargin;
                    break;
                case Gravity.LEFT:
                default:
                    childLeft = paddingLeft + lp.leftMargin;
                    break;
            }

            childTop += lp.topMargin;
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            childTop += childHeight + lp.bottomMargin;
        }
    }

    void layoutHorizontal(int left, int top, int right, int bottom) {
        int paddingTop = getPaddingTop();
        int childTop;
        int childLeft;

        int height = bottom - top;
        int childBottom = height - getPaddingBottom();
        int childSpace = height - paddingTop - getPaddingBottom();

        int count = getChildCount();

        int majorGravity = mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        int minorGravity = mGravity & Gravity.VERTICAL_GRAVITY_MASK;

        int absoluteMajor = Gravity.getAbsoluteGravity(majorGravity, getLayoutDirection());
        switch (absoluteMajor & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.RIGHT:
                childLeft = getPaddingLeft() + right - left - mTotalLength;
                break;
            case Gravity.CENTER_HORIZONTAL:
                childLeft = getPaddingLeft() + (right - left - mTotalLength) / 2;
                break;
            case Gravity.LEFT:
            default:
                childLeft = getPaddingLeft();
                break;
        }

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child == null || child.getVisibility() == GONE) {
                continue;
            }

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            LayoutParams lp = getLinearLayoutParams(child);

            int gravity = lp.gravity;
            if (gravity < 0) {
                gravity = minorGravity;
            }

            switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
                case Gravity.TOP:
                    childTop = paddingTop + lp.topMargin;
                    break;
                case Gravity.CENTER_VERTICAL:
                    childTop = paddingTop + ((childSpace - childHeight) / 2)
                            + lp.topMargin - lp.bottomMargin;
                    break;
                case Gravity.BOTTOM:
                    childTop = childBottom - childHeight - lp.bottomMargin;
                    break;
                default:
                    childTop = paddingTop;
                    break;
            }

            childLeft += lp.leftMargin;
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            childLeft += childWidth + lp.rightMargin;
        }
    }

    // ── Helper methods ──

    void measureChildBeforeLayout(View child, int childIndex,
            int widthMeasureSpec, int totalWidth,
            int heightMeasureSpec, int totalHeight) {
        measureChildWithMargins(child, widthMeasureSpec, totalWidth,
                heightMeasureSpec, totalHeight);
    }

    private void forceUniformWidth(int count, int heightMeasureSpec) {
        int uniformMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
                MeasureSpec.EXACTLY);
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                LayoutParams lp = getLinearLayoutParams(child);
                if (lp.width == LayoutParams.MATCH_PARENT) {
                    int oldHeight = lp.height;
                    lp.height = child.getMeasuredHeight();
                    measureChildWithMargins(child, uniformMeasureSpec, 0, heightMeasureSpec, 0);
                    lp.height = oldHeight;
                }
            }
        }
    }

    private void forceUniformHeight(int count, int widthMeasureSpec) {
        int uniformMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(),
                MeasureSpec.EXACTLY);
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                LayoutParams lp = getLinearLayoutParams(child);
                if (lp.height == LayoutParams.MATCH_PARENT) {
                    int oldWidth = lp.width;
                    lp.width = child.getMeasuredWidth();
                    measureChildWithMargins(child, widthMeasureSpec, 0, uniformMeasureSpec, 0);
                    lp.width = oldWidth;
                }
            }
        }
    }

    /** Safely cast child's layout params, providing defaults if not our type. */
    private LayoutParams getLinearLayoutParams(View child) {
        Object lpo = child.getLayoutParams();
        if (lpo instanceof LayoutParams) {
            return (LayoutParams) lpo;
        }
        // Provide a default LayoutParams if none set or wrong type
        LayoutParams lp;
        if (lpo instanceof ViewGroup.MarginLayoutParams) {
            lp = new LayoutParams((ViewGroup.MarginLayoutParams) lpo);
        } else if (lpo instanceof ViewGroup.LayoutParams) {
            ViewGroup.LayoutParams vlp = (ViewGroup.LayoutParams) lpo;
            lp = new LayoutParams(vlp.width, vlp.height);
        } else {
            lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        child.setLayoutParams(lp);
        return lp;
    }

    // ── LayoutParams with weight support ──

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public float weight;
        public int gravity = -1;

        public LayoutParams() {
            super(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

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

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source.width, source.height);
        }
    }
}
