package android.widget;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewGroup;

import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.widget.ListAdapter interface — backing contract for list-like widgets.
 *
 * Replaces the inner interface previously embedded in ListView so that BaseAdapter,
 * ArrayAdapter, and other adapter classes can implement it from a single shared type.
 */
public interface ListAdapter {
    int getCount();
    Object getItem(int position);
    long getItemId(int position);
    View getView(int position, View convertView, ViewGroup parent);
    int getItemViewType(int position);
    int getViewTypeCount();
    boolean isEmpty();
    boolean areAllItemsEnabled();
    boolean isEnabled(int position);
}
