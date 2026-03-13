package android.view.accessibility;
import android.view.View;
import android.view.View;
import java.util.Set;

import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible AccessibilityRecord shim.
 * Base class for AccessibilityEvent carrying record data.
 */
public class AccessibilityRecord {

    private View mSource;
    private CharSequence mClassName;
    private CharSequence mContentDescription;
    private final List<CharSequence> mText = new ArrayList<>();
    private int mFromIndex = -1;
    private int mToIndex   = -1;
    private int mItemCount = -1;
    private boolean mScrollable;

    protected AccessibilityRecord() {}

    /** Set the source View that originated this record. */
    public void setSource(View source) {
        mSource = source;
    }

    /** Return the source View. */
    public View getSource() {
        return mSource;
    }

    /** Set the class name of the event source. */
    public void setClassName(CharSequence className) {
        mClassName = className;
    }

    /** Return the class name of the event source. */
    public CharSequence getClassName() {
        return mClassName;
    }

    /** Set a content description for this record. */
    public void setContentDescription(CharSequence description) {
        mContentDescription = description;
    }

    /** Return the content description. */
    public CharSequence getContentDescription() {
        return mContentDescription;
    }

    /**
     * Return the mutable list of text strings attached to this record.
     * Callers append to the list directly.
     */
    public List<CharSequence> getText() {
        return mText;
    }

    /** Set the index of the first character of the changed text. */
    public void setFromIndex(int fromIndex) {
        mFromIndex = fromIndex;
    }

    /** Return the from-index. */
    public int getFromIndex() {
        return mFromIndex;
    }

    /** Set the index of the last character of the changed text. */
    public void setToIndex(int toIndex) {
        mToIndex = toIndex;
    }

    /** Return the to-index. */
    public int getToIndex() {
        return mToIndex;
    }

    /** Set the count of items in a list or other collection. */
    public void setItemCount(int itemCount) {
        mItemCount = itemCount;
    }

    /** Return the item count. */
    public int getItemCount() {
        return mItemCount;
    }

    /** Set whether the source is scrollable. */
    public void setScrollable(boolean scrollable) {
        mScrollable = scrollable;
    }

    /** Return whether the source is scrollable. */
    public boolean isScrollable() {
        return mScrollable;
    }
}
