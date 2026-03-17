package android.widget;
import android.view.View;
import android.view.ViewGroup;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.ScrollView → ARKUI_NODE_SCROLL
 *
 * ArkUI Scroll is a single-child scrollable container.
 * Only the first child is used (matching Android ScrollView behavior).
 */
public class ScrollView extends FrameLayout {
    static final int NODE_TYPE_SCROLL = 9;
    static final int ATTR_SCROLL_BAR_DISPLAY = 9000;
    static final int ATTR_SCROLL_OFFSET = 9001;

    // Scroll indicator colors
    private static final int SCROLL_INDICATOR_COLOR = 0x66888888; // semi-transparent gray

    public ScrollView() {
        super();
    }

    /** Control scrollbar visibility */
    public void setScrollBarStyle(int style) {
        // 0=OFF, 1=AUTO, 2=ON
    }

    public void setVerticalScrollBarEnabled(boolean enabled) {
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_SCROLL_BAR_DISPLAY,
                enabled ? 1 : 0); // AUTO=1, OFF=0
        }
    }

    public void smoothScrollTo(int x, int y) {
        // Would need scroll controller — future enhancement
        scrollTo(x, y);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        if (nativeHandle != 0) {
            com.ohos.shim.bridge.OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_SCROLL_OFFSET, (float) y, 0, 0, 0, 1);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Measure children first
        int count = getChildCount();
        int maxWidth = 0;
        int maxHeight = 0;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // For ScrollView, give child unlimited height
                int childWidthSpec = widthMeasureSpec;
                int childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                child.measure(childWidthSpec, childHeightSpec);
                maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
        }

        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();

        // Set our measured dimensions respecting the parent spec
        setMeasuredDimension(
            resolveSize(maxWidth, widthMeasureSpec),
            resolveSize(maxHeight, heightMeasureSpec));
    }

    private static int resolveSize(int size, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                return specSize;
            case MeasureSpec.AT_MOST:
                return Math.min(size, specSize);
            default:
                return size;
        }
    }

    @Override
    protected void dispatchDraw(android.graphics.Canvas canvas) {
        // Apply scroll offset: translate by -scrollY before drawing children
        int scrollY = getScrollY();
        if (scrollY != 0) {
            canvas.save();
            canvas.translate(0, -scrollY);
        }

        // Draw children via parent implementation
        super.dispatchDraw(canvas);

        if (scrollY != 0) {
            canvas.restore();
        }
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        // Draw scroll indicators if content exceeds viewport
        int viewportHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int contentHeight = 0;

        // Get content height from first child (ScrollView uses single child)
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            contentHeight = child.getMeasuredHeight() > 0 ? child.getMeasuredHeight() : child.getHeight();
        }

        if (contentHeight > viewportHeight && viewportHeight > 0) {
            android.graphics.Paint indicatorPaint = new android.graphics.Paint();
            indicatorPaint.setColor(SCROLL_INDICATOR_COLOR);
            indicatorPaint.setStyle(android.graphics.Paint.Style.FILL);

            int scrollY = getScrollY();
            int maxScroll = contentHeight - viewportHeight;
            int w = getWidth();

            // Scroll indicator dimensions
            float indicatorWidth = 4f;
            float trackX = w - getPaddingRight() - indicatorWidth;

            // Calculate indicator position and size proportionally
            float indicatorRatio = (float) viewportHeight / contentHeight;
            float indicatorHeight = Math.max(20f, viewportHeight * indicatorRatio);
            float scrollRatio = maxScroll > 0 ? (float) scrollY / maxScroll : 0f;
            float indicatorTop = getPaddingTop() + scrollRatio * (viewportHeight - indicatorHeight);

            // Draw the scroll indicator track (lighter)
            android.graphics.Paint trackPaint = new android.graphics.Paint();
            trackPaint.setColor(0x22888888);
            trackPaint.setStyle(android.graphics.Paint.Style.FILL);
            canvas.drawRect(trackX, getPaddingTop(), trackX + indicatorWidth,
                getPaddingTop() + viewportHeight, trackPaint);

            // Draw the scroll indicator thumb
            canvas.drawRoundRect(trackX, indicatorTop, trackX + indicatorWidth,
                indicatorTop + indicatorHeight, 2f, 2f, indicatorPaint);
        }
    }
}
