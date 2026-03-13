package android.widget;

import android.view.View;

/**
 * Android-compatible ListPopupWindow shim.
 * A popup window that displays a list of items. All operations are stubs;
 * no native UI is created in this migration shim.
 */
public class ListPopupWindow {

    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;

    public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
    public static final int INPUT_METHOD_NEEDED         = 1;
    public static final int INPUT_METHOD_NOT_NEEDED     = 2;

    private Object mAdapter;
    private View mAnchorView;
    private int mWidth = WRAP_CONTENT;
    private int mHeight = WRAP_CONTENT;
    private boolean mShowing = false;
    private boolean mModal = false;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    /**
     * Create a new ListPopupWindow anchored to the given context.
     *
     * @param context the context (typed as Object for cross-package compatibility)
     */
    public ListPopupWindow(Object context) {}

    /** Set the adapter that provides the data and the views for items. */
    public void setAdapter(Object adapter) {
        mAdapter = adapter;
    }

    /** Set the view that the popup should be positioned relative to. */
    public void setAnchorView(View anchor) {
        mAnchorView = anchor;
    }

    /** Set the width of the popup window in pixels. Use MATCH_PARENT or WRAP_CONTENT. */
    public void setWidth(int width) {
        mWidth = width;
    }

    /** Set the height of the popup window in pixels. Use MATCH_PARENT or WRAP_CONTENT. */
    public void setHeight(int height) {
        mHeight = height;
    }

    /** Show the popup list. Stub: marks as showing. */
    public void show() {
        mShowing = true;
    }

    /** Dismiss the popup list. Stub: marks as not showing. */
    public void dismiss() {
        mShowing = false;
    }

    /** Returns true if the popup is currently showing. */
    public boolean isShowing() {
        return mShowing;
    }

    /** Set a listener to receive item-click events on the popup list. */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /** Set whether the popup should be modal (intercepts all touch events). */
    public void setModal(boolean modal) {
        mModal = modal;
    }

    /**
     * Returns the ListView displayed within the popup window.
     * Stub: returns null.
     */
    public ListView getListView() {
        return null;
    }
}
