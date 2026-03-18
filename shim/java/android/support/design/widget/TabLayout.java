package android.support.design.widget;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import java.util.Set;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.support.design.widget.TabLayout
 *
 * TabLayout provides a horizontal layout to display tabs.
 * This shim tracks tabs, selection state and listeners; all visual rendering
 * is stubbed.
 */
public class TabLayout extends LinearLayout {

    // ── Tab mode constants ──

    /** Scrollable tabs display a subset of tabs at any given moment, and can contain
     *  longer tab labels and a larger number of tabs. */
    public static final int MODE_SCROLLABLE = 0;
    /** Fixed tabs display all tabs concurrently and are best used with content that benefits
     *  from quick pivots between tabs. */
    public static final int MODE_FIXED      = 1;

    // ── Tab gravity constants ──

    /** Gravity used to fill the TabLayout as much as possible. */
    public static final int GRAVITY_FILL   = 0;
    /** Gravity used to lay out the tabs in the center of the TabLayout. */
    public static final int GRAVITY_CENTER = 1;

    private final List<Tab> mTabs = new ArrayList<>();
    private int mSelectedPosition = -1;
    private int mTabMode = MODE_FIXED;
    private int mTabGravity = GRAVITY_FILL;
    private OnTabSelectedListener mOnTabSelectedListener;

    public TabLayout() {
        super(new android.content.Context());
    }

    public TabLayout(android.content.Context context) {
        super(context);
    }

    public TabLayout(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public TabLayout(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // ── Tab management ──

    /**
     * Create and return a new Tab.
     *
     * @return a new Tab
     */
    public Tab newTab() {
        return new Tab(this);
    }

    /**
     * Add a tab to this layout. The tab will be inserted at the end of the
     * tab list.
     *
     * @param tab tab to add
     */
    public void addTab(Tab tab) {
        addTab(tab, mTabs.isEmpty());
    }

    /**
     * Add a tab to this layout. The tab will be inserted at the end of the
     * tab list.
     *
     * @param tab      tab to add
     * @param setSelected true if the added tab should become the selected tab
     */
    public void addTab(Tab tab, boolean setSelected) {
        addTab(tab, mTabs.size(), setSelected);
    }

    /**
     * Add a tab to this layout at the specified position.
     *
     * @param tab      tab to add
     * @param position the new position of the tab
     */
    public void addTab(Tab tab, int position) {
        addTab(tab, position, mTabs.isEmpty());
    }

    /**
     * Add a tab to this layout at the specified position.
     *
     * @param tab         tab to add
     * @param position    the new position of the tab
     * @param setSelected true if the added tab should become the selected tab
     */
    public void addTab(Tab tab, int position, boolean setSelected) {
        mTabs.add(position, tab);
        tab.mPosition = position;
        // Update positions for all tabs after the inserted one
        for (int i = position + 1; i < mTabs.size(); i++) {
            mTabs.get(i).mPosition = i;
        }
        if (setSelected) {
            tab.select();
        }
    }

    /**
     * Remove a tab from the layout.
     *
     * @param tab the tab to remove
     */
    public void removeTab(Tab tab) {
        int pos = tab.mPosition;
        if (pos >= 0 && pos < mTabs.size()) {
            mTabs.remove(pos);
            for (int i = pos; i < mTabs.size(); i++) {
                mTabs.get(i).mPosition = i;
            }
            if (mSelectedPosition == pos) {
                mSelectedPosition = mTabs.isEmpty() ? -1 : Math.min(pos, mTabs.size() - 1);
            } else if (mSelectedPosition > pos) {
                mSelectedPosition--;
            }
        }
    }

    /**
     * Remove all tabs from the layout and deselect the current tab.
     */
    public void removeAllTabs() {
        mTabs.clear();
        mSelectedPosition = -1;
    }

    /**
     * Returns the number of tabs currently registered with the action bar.
     *
     * @return tab count
     */
    public int getTabCount() {
        return mTabs.size();
    }

    /**
     * Returns the tab at the specified index.
     *
     * @param index the index of the tab to return
     * @return the tab at the given index, or null if the index is invalid
     */
    public Tab getTabAt(int index) {
        return (index >= 0 && index < mTabs.size()) ? mTabs.get(index) : null;
    }

    /**
     * Returns the position of the current selected tab.
     *
     * @return selected tab position, or -1 if no tab is selected
     */
    public int getSelectedTabPosition() {
        return mSelectedPosition;
    }

    // ── ViewPager integration ──

    /**
     * The one-stop shop for setting up this TabLayout with a ViewPager.
     *
     * This method will link the given ViewPager and this TabLayout together so that
     * changes in one are automatically reflected in the other.
     *
     * @param viewPager the ViewPager to link to, or null to clear any previous link
     */
    public void setupWithViewPager(ViewPager viewPager) {
        setupWithViewPager(viewPager, true);
    }

    /**
     * The one-stop shop for setting up this TabLayout with a ViewPager.
     *
     * @param viewPager           the ViewPager to link to, or null to clear the link
     * @param autoRefresh         whether this layout should be refreshed when the ViewPager's
     *                            adapter changes
     */
    public void setupWithViewPager(ViewPager viewPager, boolean autoRefresh) {
        if (viewPager == null) {
            return;
        }
        android.support.v4.view.PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) {
            return;
        }
        removeAllTabs();
        for (int i = 0; i < adapter.getCount(); i++) {
            Tab tab = newTab();
            CharSequence title = adapter.getPageTitle(i);
            if (title != null) {
                tab.setText(title);
            }
            addTab(tab, i == 0);
        }
    }

    // ── Mode / gravity ──

    /**
     * Set the behavior mode for the Tabs in this layout.
     *
     * @param mode one of MODE_FIXED or MODE_SCROLLABLE
     */
    public void setTabMode(int mode) {
        this.mTabMode = mode;
    }

    /**
     * Returns the current mode used by this TabLayout.
     *
     * @return the current tab mode
     */
    public int getTabMode() {
        return mTabMode;
    }

    /**
     * Set the gravity to use when laying out the tabs.
     *
     * @param gravity one of GRAVITY_CENTER or GRAVITY_FILL
     */
    public void setTabGravity(int gravity) {
        this.mTabGravity = gravity;
    }

    /**
     * The current gravity used for laying out tabs.
     *
     * @return the current tab gravity
     */
    public int getTabGravity() {
        return mTabGravity;
    }

    // ── Listener ──

    /**
     * Set the OnTabSelectedListener that will handle switching to and from tabs.
     *
     * @param listener listener callback, or null to clear
     * @deprecated use addOnTabSelectedListener(OnTabSelectedListener)
     */
    @Deprecated
    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        this.mOnTabSelectedListener = listener;
    }

    /**
     * Add a OnTabSelectedListener that will be invoked when tab selection changes.
     *
     * @param listener listener to add
     */
    public void addOnTabSelectedListener(OnTabSelectedListener listener) {
        this.mOnTabSelectedListener = listener;
    }

    /**
     * Remove the given OnTabSelectedListener that was previously added via
     * addOnTabSelectedListener(OnTabSelectedListener).
     *
     * @param listener listener to remove
     */
    public void removeOnTabSelectedListener(OnTabSelectedListener listener) {
        if (mOnTabSelectedListener == listener) {
            mOnTabSelectedListener = null;
        }
    }

    // ── Internal selection dispatch ──

    void selectTab(Tab tab) {
        Tab prevTab = mSelectedPosition >= 0 ? mTabs.get(mSelectedPosition) : null;
        int newPos = tab.mPosition;
        mSelectedPosition = newPos;
        if (mOnTabSelectedListener != null) {
            if (prevTab != null && prevTab != tab) {
                mOnTabSelectedListener.onTabUnselected(prevTab);
            }
            mOnTabSelectedListener.onTabSelected(tab);
        }
    }

    // ── Tab inner class ──

    /**
     * A tab in the tab bar.
     */
    public static final class Tab {

        /** An invalid position for a tab. */
        public static final int INVALID_POSITION = -1;

        private TabLayout mParent;
        int mPosition = INVALID_POSITION;
        private CharSequence mText;
        private Object mIcon;
        private Object mTag;
        private CharSequence mContentDesc;

        Tab(TabLayout parent) {
            this.mParent = parent;
        }

        /**
         * Return the current position of this tab in the action bar.
         *
         * @return current position, or INVALID_POSITION if this tab is not currently
         *         in the action bar
         */
        public int getPosition() {
            return mPosition;
        }

        /**
         * Return the text of this tab.
         *
         * @return the tab's text
         */
        public CharSequence getText() {
            return mText;
        }

        /**
         * Set the text displayed on this tab. Text may be truncated if there is not room
         * to display the entire string.
         *
         * @param text the text to display
         * @return the current instance for call chaining
         */
        public Tab setText(CharSequence text) {
            this.mText = text;
            return this;
        }

        /**
         * Set the text displayed on this tab.
         *
         * @param resId a resource ID referring to the text that should be displayed
         * @return the current instance for call chaining
         */
        public Tab setText(int resId) {
            return setText("string_" + resId);
        }

        /**
         * Set the icon displayed on this tab.
         *
         * @param icon the drawable to use as an icon
         * @return the current instance for call chaining
         */
        public Tab setIcon(Object icon) {
            this.mIcon = icon;
            return this;
        }

        /**
         * Set the icon displayed on this tab.
         *
         * @param resId a resource ID referring to the icon that should be displayed
         * @return the current instance for call chaining
         */
        public Tab setIcon(int resId) {
            this.mIcon = resId;
            return this;
        }

        /**
         * Return the icon associated with this tab.
         *
         * @return the tab's icon
         */
        public Object getIcon() {
            return mIcon;
        }

        /**
         * Set a description of this tab's content for use in accessibility support.
         *
         * @param resId a resource ID referring to the description text
         * @return the current instance for call chaining
         */
        public Tab setContentDescription(int resId) {
            return setContentDescription("string_" + resId);
        }

        /**
         * Set a description of this tab's content for use in accessibility support.
         *
         * @param contentDesc description of this tab's content
         * @return the current instance for call chaining
         */
        public Tab setContentDescription(CharSequence contentDesc) {
            this.mContentDesc = contentDesc;
            return this;
        }

        /**
         * Gets a brief description of this tab's content for use in accessibility support.
         *
         * @return description of this tab's content
         */
        public CharSequence getContentDescription() {
            return mContentDesc;
        }

        /**
         * Give this tab an arbitrary object to hold for later use.
         *
         * @param tag object to store
         * @return the current instance for call chaining
         */
        public Tab setTag(Object tag) {
            this.mTag = tag;
            return this;
        }

        /**
         * Returns this tab's tag object.
         *
         * @return this tab's tag object
         */
        public Object getTag() {
            return mTag;
        }

        /**
         * Select this tab. Only valid if the tab has been added to the action bar.
         */
        public void select() {
            if (mParent != null) {
                mParent.selectTab(this);
            }
        }

        /**
         * Returns true if this tab is currently selected.
         *
         * @return true if selected
         */
        public boolean isSelected() {
            return mParent != null && mParent.getSelectedTabPosition() == mPosition;
        }
    }

    // ── OnTabSelectedListener interface ──

    /**
     * Object interface invoked when a tab's selection state changes.
     */
    public interface OnTabSelectedListener {

        /**
         * Called when a tab enters the selected state.
         *
         * @param tab the tab that was selected
         */
        void onTabSelected(Tab tab);

        /**
         * Called when a tab exits the selected state.
         *
         * @param tab the tab that was unselected
         */
        void onTabUnselected(Tab tab);

        /**
         * Called when a tab that is already selected is chosen again by the user.
         * Some applications may use this action to return to the top level of a category.
         *
         * @param tab the tab that was reselected
         */
        void onTabReselected(Tab tab);
    }
}
