package android.support.v4.view;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import java.util.Set;

import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.support.v4.view.ViewPager
 *
 * Layout manager that allows the user to flip left and right through pages
 * of data. This shim tracks adapter and page state; all actual rendering
 * is a no-op stub.
 */
public class ViewPager extends ViewGroup {

    // ── Scroll state constants ──

    /** Indicates that the pager is in an idle, settled state. */
    public static final int SCROLL_STATE_IDLE     = 0;
    /** Indicates that the pager is currently being dragged by the user. */
    public static final int SCROLL_STATE_DRAGGING = 1;
    /** Indicates that the pager is in the process of settling to a final position. */
    public static final int SCROLL_STATE_SETTLING  = 2;

    private PagerAdapter mAdapter;
    private int mCurrentItem = 0;
    private int mOffscreenPageLimit = 1;
    private int mScrollState = SCROLL_STATE_IDLE;

    // We track a single legacy listener (setOnPageChangeListener) separately
    // from the list of listeners added via addOnPageChangeListener.
    private OnPageChangeListener mOnPageChangeListener;
    private final List<OnPageChangeListener> mOnPageChangeListeners = new ArrayList<>();

    public ViewPager() {
        super();
    }

    public ViewPager(Object context) {
        super();
    }

    public ViewPager(Object context, Object attrs) {
        super();
    }

    // ── Adapter ──

    /**
     * Set a PagerAdapter that will supply views for this pager as needed.
     *
     * @param adapter adapter to use
     */
    public void setAdapter(PagerAdapter adapter) {
        this.mAdapter = adapter;
        if (adapter != null) {
            // Reset to page 0 when adapter changes
            mCurrentItem = 0;
        }
    }

    /**
     * Retrieve the current adapter supplying pages.
     *
     * @return the currently registered PagerAdapter
     */
    public PagerAdapter getAdapter() {
        return mAdapter;
    }

    // ── Current item ──

    /**
     * Set the currently selected page.
     *
     * @param item item index to select
     */
    public void setCurrentItem(int item) {
        setCurrentItem(item, true);
    }

    /**
     * Set the currently selected page.
     *
     * @param item         item index to select
     * @param smoothScroll true to smoothly scroll to the new item, false to
     *                     transition immediately
     */
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (mAdapter == null) return;
        int count = mAdapter.getCount();
        if (count == 0) return;
        item = Math.max(0, Math.min(item, count - 1));
        if (item == mCurrentItem) return;
        int oldItem = mCurrentItem;
        mCurrentItem = item;
        dispatchOnPageSelected(item);
    }

    /**
     * Get the currently selected page.
     *
     * @return the currently selected page
     */
    public int getCurrentItem() {
        return mCurrentItem;
    }

    // ── Offscreen page limit ──

    /**
     * Set the number of pages that should be retained to either side of the
     * current page in the view hierarchy in an idle state.
     *
     * @param limit how many pages will be kept offscreen in an idle state
     */
    public void setOffscreenPageLimit(int limit) {
        if (limit < 1) limit = 1;
        this.mOffscreenPageLimit = limit;
    }

    /**
     * Returns the number of pages that will be retained to either side of the
     * current page in the view hierarchy in an idle state.
     *
     * @return the current offscreen page limit
     */
    public int getOffscreenPageLimit() {
        return mOffscreenPageLimit;
    }

    // ── Page change listeners ──

    /**
     * Set a listener that will be invoked whenever the page changes or is
     * incrementally scrolled.
     *
     * @param listener listener to set
     * @deprecated use addOnPageChangeListener(OnPageChangeListener)
     */
    @Deprecated
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mOnPageChangeListener = listener;
    }

    /**
     * Add a listener that will be invoked whenever the page changes or is
     * incrementally scrolled. You can add multiple listeners.
     *
     * @param listener listener to add
     */
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        if (listener != null && !mOnPageChangeListeners.contains(listener)) {
            mOnPageChangeListeners.add(listener);
        }
    }

    /**
     * Remove a listener that was previously added via addOnPageChangeListener(OnPageChangeListener).
     *
     * @param listener listener to remove
     */
    public void removeOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListeners.remove(listener);
    }

    /**
     * Remove all listeners that are notified of any changes in scroll state or position.
     */
    public void clearOnPageChangeListeners() {
        mOnPageChangeListeners.clear();
    }

    // ── Internal dispatch helpers ──

    private void dispatchOnPageSelected(int position) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
        for (OnPageChangeListener l : mOnPageChangeListeners) {
            l.onPageSelected(position);
        }
    }

    // ── OnPageChangeListener interface ──

    /**
     * Object interface for responding to changing state of the selected page.
     */
    public interface OnPageChangeListener {

        /**
         * This method will be invoked when the current page is scrolled, either
         * as part of a programmatically initiated smooth scroll or a user-initiated
         * touch scroll.
         *
         * @param position             position index of the first page currently being displayed.
         * @param positionOffset       value from [0) indicating the offset from the
         *                             page at position.
         * @param positionOffsetPixels value in pixels indicating the offset from position.
         */
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        /**
         * This method will be invoked when a new page becomes selected.
         *
         * @param position Position index of the new selected page.
         */
        void onPageSelected(int position);

        /**
         * Called when the scroll state changes.
         *
         * @param state one of SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, or SCROLL_STATE_SETTLING.
         */
        void onPageScrollStateChanged(int state);
    }

    /**
     * Simple implementation of OnPageChangeListener with no-op implementations of
     * each method. Extend only the callbacks you need.
     */
    public static class SimpleOnPageChangeListener implements OnPageChangeListener {
        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override public void onPageSelected(int position) {}
        @Override public void onPageScrollStateChanged(int state) {}
    }
}
