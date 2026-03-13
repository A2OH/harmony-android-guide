package android.support.v4.view;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewGroup;

import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.support.v4.view.PagerAdapter
 *
 * Abstract base class for adapters used with ViewPager. Provides the data
 * model for pages. Subclasses must implement getCount() and isViewFromObject().
 */
public class PagerAdapter {

    /** Returned by getItemPosition(Object) when the item position is unchanged. */
    public static final int POSITION_UNCHANGED = -1;

    /** Returned by getItemPosition(Object) when the item is not found in the adapter. */
    public static final int POSITION_NONE = -2;

    // ── Abstract API ──

    /**
     * Return the number of views available.
     */
    public int getCount() { return 0; }

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by instantiateItem(ViewGroup, int). This method is required
     * for a PagerAdapter to function properly.
     *
     * @param view   Page View to check for association with object
     * @param object Object to check for association with view
     * @return true if view is associated with the key object object
     */
    public boolean isViewFromObject(View view, Object object) { return false; }

    // ── Lifecycle callbacks ──

    /**
     * Called when a change in the adapter has been started.
     *
     * @param container the containing View which is displaying this adapter's page views
     */
    public void startUpdate(ViewGroup container) {}

    /**
     * Create the page for the given position. The adapter is responsible for
     * adding the view to the container given here, although it only must ensure
     * this is done by the time it returns from finishUpdate(ViewGroup).
     *
     * @param container the containing View in which the page will be shown
     * @param position  the page position to be instantiated
     * @return an Object representing the new page. This does not need to be a View,
     *         but can be some other container of the page.
     */
    public Object instantiateItem(ViewGroup container, int position) {
        return null;
    }

    /**
     * Remove a page for the given position. The adapter is responsible for
     * removing the view from its container, although it only must ensure this
     * is done by the time it returns from finishUpdate(ViewGroup).
     *
     * @param container the containing View from which the page will be removed
     * @param position  the page position to be removed
     * @param object    the same object that was returned by instantiateItem(View, int)
     */
    public void destroyItem(ViewGroup container, int position, Object object) {}

    /**
     * Called when the a change in the shown pages has been completed.
     *
     * @param container the containing View which is displaying this adapter's page views
     */
    public void finishUpdate(ViewGroup container) {}

    // ── Data change ──

    /**
     * This method should be called by the application if the data backing this
     * adapter has changed and associated views should update.
     */
    public void notifyDataSetChanged() {}

    /**
     * Called when the host view is attempting to determine if an item's position
     * has changed. Returns POSITION_UNCHANGED if the position of the given item
     * has not changed, or POSITION_NONE if the item is no longer present in the
     * adapter.
     *
     * @param object Object representing an item, previously returned by a call to
     *               instantiateItem(View, int).
     * @return object's new position index from [0, getCount()), POSITION_UNCHANGED
     *         if the object's position has not changed, or POSITION_NONE if the
     *         item is no longer present.
     */
    public int getItemPosition(Object object) {
        return POSITION_UNCHANGED;
    }

    /**
     * This method may be called by the ViewPager to obtain a title string to
     * describe the specified page.
     *
     * @param position the position of the title requested
     * @return a title for the requested page
     */
    public CharSequence getPageTitle(int position) {
        return null;
    }

    /**
     * Returns the proportional width of a given page as a percentage of the
     * ViewPager's measured width from (0.f-1.f].
     *
     * @param position the page position
     * @return proportional width for the given page position
     */
    public float getPageWidth(int position) {
        return 1.f;
    }
}
