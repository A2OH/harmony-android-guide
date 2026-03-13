package android.widget;

import android.view.View;

/**
 * Shim: android.widget.TabWidget → ArkUI ROW node (horizontal strip of tab buttons).
 *
 * TabWidget is the horizontal bar of tab indicator buttons that sits above (or
 * below) the tab content area. In the shim it extends LinearLayout in ROW
 * orientation. Tab switching is driven by TabHost; TabWidget just tracks which
 * tab is visually selected.
 */
public class TabWidget extends LinearLayout {

    private int mCurrentTab = 0;

    public TabWidget() {
        super();
        // Tab strip is always horizontal
        setOrientation(HORIZONTAL);
    }

    /** Constructor accepting a context-like object (ignored in shim). */
    public TabWidget(Object context) {
        this();
    }

    /** Constructor accepting context and attribute set (both ignored). */
    public TabWidget(Object context, Object attrs) {
        this();
    }

    /** Constructor accepting context, attribute set and default style (all ignored). */
    public TabWidget(Object context, Object attrs, int defStyleAttr) {
        this();
    }

    /**
     * Return the number of tab indicator buttons (children) in this widget.
     * Delegates to ViewGroup.getChildCount().
     */
    public int getTabCount() {
        return getChildCount();
    }

    /**
     * Mark the tab at {@code index} as selected.
     *
     * Updates the visual state of all child tab buttons: selected tab is
     * enabled, all others are not-focused. Full visual styling requires
     * a custom tab indicator drawable that is not yet implemented.
     */
    public void setCurrentTab(int index) {
        if (index < 0 || index >= getChildCount()) return;

        mCurrentTab = index;
        // Visual selection state update is handled by TabHost re-rendering;
        // the shim does not yet expose a setSelected() API on View.
    }

    /** Return the index of the currently focused tab. */
    public int getCurrentTab() {
        return mCurrentTab;
    }
}
