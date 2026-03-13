package android.widget;

import android.view.View;
import android.view.ViewGroup;
import android.database.DataSetObserver;

/**
 * Shim: android.widget.Adapter interface
 *
 * Base interface for providing data to an AdapterView.
 */
public interface Adapter {

    int IGNORE_ITEM_VIEW_TYPE = -1;
    int NO_SELECTION = Integer.MIN_VALUE;

    void registerDataSetObserver(DataSetObserver observer);

    void unregisterDataSetObserver(DataSetObserver observer);

    int getCount();

    Object getItem(int position);

    long getItemId(int position);

    boolean hasStableIds();

    View getView(int position, View convertView, ViewGroup parent);

    int getItemViewType(int position);

    int getViewTypeCount();

    boolean isEmpty();
}
