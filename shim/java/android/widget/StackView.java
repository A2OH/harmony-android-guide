package android.widget;

import android.view.View;

/**
 * Shim: android.widget.StackView
 *
 * Displays the items of an Adapter as a stack of cards that the user can
 * flip through.  The real class extends AdapterViewAnimator; that class does
 * not exist in this shim tree, so StackView extends {@link AdapterView} and
 * inherits ViewGroup as its ArkUI node base.
 *
 * All flip/swipe animation logic is stubbed out.
 */
public class StackView extends AdapterView {

    // ArkUI node type: STACK
    static final int NODE_TYPE_STACK = 8;

    public static final int FLIP_INTERVAL_DEFAULT = 3000; // ms

    private Adapter adapter;
    private int displayedChild = 0;

    public StackView() {
        super(NODE_TYPE_STACK);
    }

    // ── Adapter ──

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        this.displayedChild = 0;
    }

    public Adapter getAdapter() {
        return adapter;
    }

    // ── AdapterView contract ──

    @Override
    public int getCount() {
        return adapter != null ? adapter.getCount() : 0;
    }

    @Override
    public Object getItemAtPosition(int position) {
        return adapter != null ? adapter.getItem(position) : null;
    }

    // ── Display control ──

    /**
     * Shows the next item in the stack.
     */
    public void showNext() {
        if (adapter == null || adapter.getCount() == 0) return;
        displayedChild = (displayedChild + 1) % adapter.getCount();
    }

    /**
     * Shows the previous item in the stack.
     */
    public void showPrevious() {
        if (adapter == null || adapter.getCount() == 0) return;
        int count = adapter.getCount();
        displayedChild = (displayedChild - 1 + count) % count;
    }

    /**
     * Returns the position of the currently displayed item.
     */
    public int getDisplayedChild() {
        return displayedChild;
    }

    /**
     * Sets the currently displayed item.
     *
     * @param whichChild the position of the item to display
     */
    public void setDisplayedChild(int whichChild) {
        this.displayedChild = whichChild;
    }

    // ── Auto-advance ──

    /**
     * Sets the interval (in milliseconds) between automatic advance of items.
     * No-op in the shim.
     *
     * @param milliseconds the interval in milliseconds
     */
    public void setAutoAdvanceViewId(int viewId) {
        // no-op stub — real impl registers with AppWidgetHost
    }
}
