package android.support.v7.widget;

import android.view.View;

/**
 * Shim: android.support.v7.widget.DividerItemDecoration
 *
 * Adds a divider between items in a RecyclerView. All drawing is stubbed —
 * only orientation state is retained.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL   = LinearLayoutManager.VERTICAL;

    private int mOrientation;

    /**
     * Creates a divider decoration.
     *
     * @param context     context-like object (ignored in shim)
     * @param orientation {@link #HORIZONTAL} or {@link #VERTICAL}
     */
    public DividerItemDecoration(Object context, int orientation) {
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        this.mOrientation = orientation;
    }

    public int getOrientation() {
        return mOrientation;
    }

    /** Set a drawable for the divider (ignored in shim). */
    public void setDrawable(Object drawable) {
        // Stub — drawable rendering not implemented
    }

    @Override
    public void onDraw(Object c, RecyclerView parent, Object state) {
        // Stub — no drawing in shim
    }

    @Override
    public void getItemOffsets(Object outRect, View view, RecyclerView parent, Object state) {
        // Stub — no offset in shim
    }
}
