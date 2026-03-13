package android.widget;

import android.view.View;

/**
 * Shim: android.widget.HorizontalScrollView → ArkUI Scroll (horizontal)
 *
 * A FrameLayout that allows horizontal scrolling of a single child.
 */
public class HorizontalScrollView extends FrameLayout {

    public HorizontalScrollView() {
        super();
    }

    public void scrollTo(int x, int y) {
        // no-op stub
    }

    public void smoothScrollTo(int x, int y) {
        // no-op stub
    }

    public void smoothScrollBy(int dx, int dy) {
        // no-op stub
    }

    public void fullScroll(int direction) {
        // no-op stub
    }

    public boolean isFillViewport() {
        return false;
    }

    public void setFillViewport(boolean fillViewport) {
        // no-op stub
    }

    public boolean isSmoothScrollingEnabled() {
        return true;
    }

    public void setSmoothScrollingEnabled(boolean smoothScrollingEnabled) {
        // no-op stub
    }

    public int getScrollX() {
        return 0;
    }

    public int getMaxScrollAmount() {
        return 0;
    }

    public boolean arrowScroll(int direction) {
        return false;
    }
}
