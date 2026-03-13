package android.widget;

import android.view.View;
import android.view.ViewGroup;
import com.ohos.shim.bridge.OHBridge;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.widget.TabHost → ArkUI tab container (STACK node).
 *
 * TabHost manages a set of TabSpec entries. Each TabSpec has an indicator
 * (label) and content. The shim stores tabs in memory and exposes them
 * via the ArkUI COLUMN node, switching visibility on tab change.
 *
 * ArkUI does not have a direct TabHost equivalent in the C node API;
 * this shim approximates tab switching by showing/hiding content views.
 */
public class TabHost extends ViewGroup {

    private final List<TabSpec> mTabs = new ArrayList<>();
    private int mCurrentTab = 0;
    private OnTabChangeListener mOnTabChangeListener;

    public TabHost() {
        // Use COLUMN (LinearLayout-like) as the container
        super(16); // NODE_TYPE_COLUMN
    }

    /** Constructor accepting a context-like object (ignored in shim). */
    public TabHost(Object context) {
        this();
    }

    /** Constructor accepting context and attribute set (both ignored). */
    public TabHost(Object context, Object attrs) {
        this();
    }

    /**
     * Call setup() before adding tabs. In a real Activity this wires up
     * the TabWidget and FrameLayout children found by ID; in the shim it
     * is a no-op but must be present for API compatibility.
     */
    public void setup() {
        // No-op in shim — layout inflation not supported
    }

    /**
     * Create a new TabSpec with the given tag identifier.
     * The TabSpec must be configured and then passed to addTab().
     */
    public TabSpec newTabSpec(String tag) {
        return new TabSpec(tag);
    }

    /** Add a configured TabSpec to this TabHost. */
    public void addTab(TabSpec tabSpec) {
        mTabs.add(tabSpec);
        // Add the tab's content view to our node tree (initially hidden)
        View content = tabSpec.getContentView();
        if (content != null) {
            content.setVisibility(mTabs.size() == 1 ? View.VISIBLE : View.GONE);
            addView(content);
        }
    }

    /** Switch to the tab at the given zero-based index. */
    public void setCurrentTab(int index) {
        if (index < 0 || index >= mTabs.size()) return;
        int previous = mCurrentTab;
        mCurrentTab = index;

        // Update content visibility
        for (int i = 0; i < mTabs.size(); i++) {
            View content = mTabs.get(i).getContentView();
            if (content != null) {
                content.setVisibility(i == mCurrentTab ? View.VISIBLE : View.GONE);
            }
        }

        if (mOnTabChangeListener != null && previous != mCurrentTab) {
            mOnTabChangeListener.onTabChanged(mTabs.get(mCurrentTab).getTag());
        }
    }

    /** Return the index of the currently selected tab. */
    public int getCurrentTab() {
        return mCurrentTab;
    }

    /** Return the tag string of the currently selected tab. */
    public String getCurrentTabTag() {
        if (mCurrentTab >= 0 && mCurrentTab < mTabs.size()) {
            return mTabs.get(mCurrentTab).getTag();
        }
        return null;
    }

    /** Return the content view of the currently selected tab. */
    public View getCurrentView() {
        if (mCurrentTab >= 0 && mCurrentTab < mTabs.size()) {
            return mTabs.get(mCurrentTab).getContentView();
        }
        return null;
    }

    /** Return the TabWidget (tab strip) associated with this TabHost. */
    public TabWidget getTabWidget() {
        // Stub — no actual TabWidget child; return a new empty one.
        return new TabWidget();
    }

    public void setOnTabChangedListener(OnTabChangeListener listener) {
        mOnTabChangeListener = listener;
    }

    // ── Listener interface ──

    public interface OnTabChangeListener {
        void onTabChanged(String tabId);
    }

    // ── TabSpec inner class ──

    /**
     * A tab specification, created via {@link TabHost#newTabSpec(String)}.
     * Must have an indicator (label) and content set before being added.
     */
    public class TabSpec {
        private final String mTag;
        private CharSequence mIndicator;
        private View mContentView;

        private TabSpec(String tag) {
            mTag = tag;
        }

        /** Return the tag identifier for this tab. */
        public String getTag() {
            return mTag;
        }

        /**
         * Specify the label shown in the tab strip.
         * Returns {@code this} for chaining.
         */
        public TabSpec setIndicator(CharSequence label) {
            mIndicator = label;
            return this;
        }

        /**
         * Specify the label shown in the tab strip (with optional icon, ignored).
         * Returns {@code this} for chaining.
         */
        public TabSpec setIndicator(CharSequence label, Object icon) {
            mIndicator = label;
            return this;
        }

        /**
         * Specify the content as a View.
         * Returns {@code this} for chaining.
         */
        public TabSpec setContent(View contentView) {
            mContentView = contentView;
            return this;
        }

        /**
         * Specify content using a TabContentFactory (not fully supported in shim).
         * Returns {@code this} for chaining.
         */
        public TabSpec setContent(TabContentFactory factory) {
            if (factory != null) {
                mContentView = factory.createTabContent(mTag);
            }
            return this;
        }

        /** Specify content by a view resource ID (resource inflation not supported). */
        public TabSpec setContent(int viewId) {
            // Resource lookup not implemented; content remains null
            return this;
        }

        public CharSequence getIndicator() {
            return mIndicator;
        }

        public View getContentView() {
            return mContentView;
        }
    }

    /** Factory interface for creating tab content views on demand. */
    public interface TabContentFactory {
        View createTabContent(String tag);
    }
}
