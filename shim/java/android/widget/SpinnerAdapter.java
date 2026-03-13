package android.widget;

import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.widget.SpinnerAdapter — adapter contract for Spinner widgets.
 *
 * Extends Adapter with getDropDownView(), which provides a View for each item
 * when the Spinner's drop-down list is shown. Spinner uses this method instead
 * of getView() when rendering drop-down entries.
 */
public interface SpinnerAdapter extends Adapter {

    /**
     * Return a View that displays in the drop-down popup the data at the
     * specified position in the data set.
     *
     * @param position    The index of the item whose drop-down View is requested.
     * @param convertView The old view to reuse, if possible. May be null.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    View getDropDownView(int position, Object convertView, Object parent);
}
