package android.support.v4.widget;

import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.support.v4.widget.SwipeRefreshLayout
 *
 * The SwipeRefreshLayout should be used whenever the user can refresh the
 * contents of a view via a vertical swipe gesture. This shim tracks the
 * refreshing state and listener; all animation is stubbed.
 */
public class SwipeRefreshLayout extends ViewGroup {

    private boolean mRefreshing = false;
    private OnRefreshListener mOnRefreshListener;

    public SwipeRefreshLayout() {
        super();
    }

    public SwipeRefreshLayout(Object context) {
        super();
    }

    public SwipeRefreshLayout(Object context, Object attrs) {
        super();
    }

    // ── Refresh state ──

    /**
     * Set the listener to be notified when a refresh is triggered via the
     * swipe gesture.
     *
     * @param listener the refresh listener
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mOnRefreshListener = listener;
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        this.mRefreshing = refreshing;
    }

    /**
     * Returns whether the view is displaying the swipe-to-refresh indicator.
     *
     * @return whether the view is showing the refresh indicator
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }

    // ── Visual customization ──

    /**
     * Set the color resources used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response to a user swipe gesture.
     *
     * @param colorResIds color resource ids
     */
    public void setColorSchemeResources(int... colorResIds) {
        // Stub — no actual rendering
    }

    /**
     * Set the colors used in the progress animation. The first color will also
     * be the color of the bar that grows in response to a user swipe gesture.
     *
     * @param colors colors to use in the animation
     */
    public void setColorSchemeColors(int... colors) {
        // Stub — no actual rendering
    }

    /**
     * Set the background color of the progress circle in the swipe refresh indicator.
     *
     * @param colorRes resource id of the color
     */
    public void setProgressBackgroundColorSchemeResource(int colorRes) {
        // Stub
    }

    /**
     * Set the background color of the progress circle in the swipe refresh indicator.
     *
     * @param color color to set
     */
    public void setProgressBackgroundColorSchemeColor(int color) {
        // Stub
    }

    /**
     * Set whether or not this layout should be considered an accessibility scroll container.
     *
     * @param enabled whether to enable nested scrolling
     */
    public void setNestedScrollingEnabled(boolean enabled) {
        // Stub
    }

    // ── OnRefreshListener interface ──

    /**
     * Classes that wish to be notified when the swipe gesture correctly triggers a
     * refresh should implement this interface.
     */
    public interface OnRefreshListener {
        /**
         * Called when a swipe gesture triggers a refresh.
         */
        void onRefresh();
    }
}
