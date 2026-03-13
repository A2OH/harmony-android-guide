package android.support.v4.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.support.v4.app.FragmentPagerAdapter
 *
 * Implementation of PagerAdapter that represents each page as a Fragment that
 * is persistently kept in the fragment manager as long as the user can return
 * to the page. This version keeps fragments in memory even when not shown.
 * For lighter-weight behavior, see FragmentStatePagerAdapter.
 *
 * Subclasses must implement getItem(int) and getCount().
 */
public abstract class FragmentPagerAdapter extends PagerAdapter {

    private final FragmentManager mFragmentManager;

    public FragmentPagerAdapter(FragmentManager fm) {
        this.mFragmentManager = fm;
    }

    // ── Abstract API ──

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position the position of the item in the data set
     * @return Fragment for the given position
     */
    public abstract Fragment getItem(int position);

    /**
     * Return the number of views available.
     */
    @Override
    public abstract int getCount();

    // ── PagerAdapter implementation ──

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = getItem(position);
        if (fragment != null && mFragmentManager != null) {
            // In the shim, fragments are not actually transacted — just returned.
            // Real transaction would be: fm.beginTransaction().add(container.getId(), fragment).commit()
        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // FragmentPagerAdapter keeps fragments in memory; no real destruction.
        // A real impl would detach rather than remove.
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        if (object instanceof Fragment) {
            View fragmentView = ((Fragment) object).getView();
            return fragmentView != null && fragmentView == view;
        }
        return false;
    }

    /**
     * Return a unique identifier for the item at the given position.
     * The default implementation returns the position itself.
     *
     * @param position the position of the item within the adapter's data set
     * @return the id of the item at position
     */
    public long getItemId(int position) {
        return position;
    }
}
