package android.widget;

import android.view.View;

/**
 * Shim: android.widget.Gallery
 *
 * @deprecated Deprecated in API level 16. Use a horizontal {@link android.widget.LinearLayout}
 *             or a ViewPager instead.
 *
 * A horizontally scrolling widget that shows items from an Adapter, keeping the
 * centre item selected.  AbsSpinner does not exist in this shim tree, so Gallery
 * extends {@link AdapterView} directly, implementing the minimal contract.
 *
 * All scroll, fling, and animation logic is stubbed out.
 */
@Deprecated
public class Gallery extends AdapterView {

    // ArkUI node type: STACK (no native gallery equivalent)
    static final int NODE_TYPE_STACK = 8;

    public static final int SPACING_NORMAL   = 0;
    public static final int SPACING_GALLERY  = 1;

    private ListAdapter adapter;
    private int selectedPosition = 0;
    private int spacing = 0;
    private float unselectedAlpha = 0.5f;
    private int gravity = 0;

    public Gallery() {
        super(NODE_TYPE_STACK);
    }

    // ── Adapter ──

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        this.selectedPosition = 0;
    }

    public ListAdapter getAdapter() {
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

    // ── Selection ──

    public void setSelection(int position) {
        this.selectedPosition = position;
        OnItemSelectedListener listener = getOnItemSelectedListener();
        if (listener != null && adapter != null) {
            listener.onItemSelected(this, null, position, adapter.getItemId(position));
        }
    }

    public int getSelectedItemPosition() {
        return selectedPosition;
    }

    public Object getSelectedItem() {
        return getItemAtPosition(selectedPosition);
    }

    // ── Visual properties ──

    /**
     * Sets the spacing between items in the Gallery.
     *
     * @param spacing the spacing in pixels
     */
    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    /**
     * Sets the alpha of items that are not selected.
     *
     * @param unselectedAlpha alpha value in [0.0, 1.0]; default is 0.5
     */
    public void setUnselectedAlpha(float unselectedAlpha) {
        this.unselectedAlpha = unselectedAlpha;
    }

    /**
     * Sets how the items should be aligned within the Gallery.
     *
     * @param gravity see {@link android.view.Gravity}
     */
    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    /**
     * Scrolls the gallery by the given amount.  No-op in the shim.
     */
    public void scrollBy(int amount) {
        // no-op stub
    }
}
