package android.support.v4.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.support.v4.app.FragmentStatePagerAdapter
 *
 * Implementation of PagerAdapter that represents each page as a Fragment. Unlike
 * FragmentPagerAdapter, fragments that are not visible to the user are destroyed
 * and their instance state is saved. This allows the pager to hold arbitrarily
 * large numbers of pages at the cost of potentially more overhead when switching
 * between pages.
 *
 * Subclasses must implement getItem(int) and getCount().
 */
public abstract class FragmentStatePagerAdapter extends PagerAdapter {

    private final FragmentManager mFragmentManager;

    // Currently active fragments (index → fragment; null if destroyed)
    private final List<Fragment> mFragments = new ArrayList<>();
    // Saved state for destroyed fragments
    private final List<Bundle> mSavedState = new ArrayList<>();

    public FragmentStatePagerAdapter(FragmentManager fm) {
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
    public void startUpdate(ViewGroup container) {}

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Expand lists to accommodate position
        while (mFragments.size() <= position) mFragments.add(null);
        while (mSavedState.size() <= position) mSavedState.add(null);

        Fragment fragment = mFragments.get(position);
        if (fragment != null) {
            return fragment;
        }

        fragment = getItem(position);
        // Restore saved state if available
        Bundle savedState = mSavedState.get(position);
        if (savedState != null && fragment.getArguments() == null) {
            fragment.setArguments(savedState);
        }
        mFragments.set(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (position >= mFragments.size()) return;

        Fragment fragment = mFragments.get(position);
        if (fragment == null) return;

        // Save state before destroying (no real Bundle save in shim)
        while (mSavedState.size() <= position) mSavedState.add(null);
        mSavedState.set(position, fragment.getArguments());

        mFragments.set(position, null);
        // Real impl: fm.beginTransaction().remove(fragment).commit()
    }

    @Override
    public void finishUpdate(ViewGroup container) {}

    @Override
    public boolean isViewFromObject(View view, Object object) {
        if (object instanceof Fragment) {
            View fragmentView = ((Fragment) object).getView();
            return fragmentView != null && fragmentView == view;
        }
        return false;
    }

    @Override
    public void notifyDataSetChanged() {
        // Clear destroyed-fragment slots; active fragments remain
        super.notifyDataSetChanged();
    }

    /**
     * Return a unique identifier for the item at the given position.
     *
     * @param position the position of the item within the adapter's data set
     * @return the id of the item at position
     */
    public long getItemId(int position) {
        return position;
    }
}
