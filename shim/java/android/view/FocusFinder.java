package android.view;
import android.graphics.Rect;

public class FocusFinder {
    public FocusFinder() {}

    public View findNearestTouchable(ViewGroup p0, int p1, int p2, int p3, int[] p4) { return null; }
    public View findNextFocus(ViewGroup p0, View p1, int p2) { return null; }
    public View findNextFocusFromRect(ViewGroup p0, Rect p1, int p2) { return null; }
    public View findNextKeyboardNavigationCluster(View p0, View p1, int p2) { return null; }
    public static FocusFinder getInstance() { return new FocusFinder(); }

    public static void sort(View[] views, int start, int end, ViewGroup root, boolean isRtl) {
        // no-op stub - AOSP uses this for accessibility ordering
    }
}
