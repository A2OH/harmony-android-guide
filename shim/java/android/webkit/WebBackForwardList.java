package android.webkit;

import java.util.ArrayList;
import java.util.List;

/**
 * Shim for android.webkit.WebBackForwardList.
 * Represents the back/forward navigation list for a WebView.
 * Supports cloning via {@link #clone()}.
 */
public class WebBackForwardList implements Cloneable {

    private final List<WebHistoryItem> mItems;
    private int mCurrentIndex;

    public WebBackForwardList() {
        mItems = new ArrayList<>();
        mCurrentIndex = -1;
    }

    public WebBackForwardList(List<WebHistoryItem> items, int currentIndex) {
        mItems = new ArrayList<>(items);
        mCurrentIndex = currentIndex;
    }

    /**
     * Returns the currently active {@link WebHistoryItem}, or {@code null}
     * if the list is empty.
     */
    public WebHistoryItem getCurrentItem() {
        if (mCurrentIndex < 0 || mCurrentIndex >= mItems.size()) {
            return null;
        }
        return mItems.get(mCurrentIndex);
    }

    /** Returns the index of the current item in the list. */
    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    /**
     * Returns the {@link WebHistoryItem} at the given index.
     *
     * @param index zero-based index into the history list
     * @return the item at that index
     * @throws IndexOutOfBoundsException { // stub@code index} is out of range
     */
    public WebHistoryItem getItemAtIndex(int index) {
        return mItems.get(index);
    }

    /** Returns the total number of items in this list. */
    public int getSize() {
        return mItems.size();
    }

    @Override
    public WebBackForwardList clone() {
        try {
            WebBackForwardList copy = (WebBackForwardList) super.clone();
            // deep-copy the item list; WebHistoryItem itself is cloneable
            List<WebHistoryItem> copiedItems = new ArrayList<>(mItems.size());
            for (WebHistoryItem item : mItems) {
                copiedItems.add(item.clone());
            }
            return new WebBackForwardList(copiedItems, mCurrentIndex);
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
