package android.widget;

import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.widget.BaseAdapter — common base for concrete adapter implementations.
 *
 * Implements ListAdapter. Provides DataSetObserver registration stubs and default
 * implementations of areAllItemsEnabled() / isEnabled() so subclasses only need
 * to implement the four abstract methods.
 */
public abstract class BaseAdapter implements ListAdapter {

    /** Simple observer interface (mirrors android.database.DataSetObserver). */
    public interface DataSetObserver {
        void onChanged();
        void onInvalidated();
    }

    private final List<DataSetObserver> observers = new ArrayList<>();

    // ── Abstract methods ──

    @Override
    public abstract int getCount();

    @Override
    public abstract Object getItem(int position);

    @Override
    public abstract long getItemId(int position);

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    // ── ListAdapter defaults ──

    @Override
    public boolean areAllItemsEnabled() { return true; }

    @Override
    public boolean isEnabled(int position) { return true; }

    @Override
    public int getItemViewType(int position) { return 0; }

    @Override
    public int getViewTypeCount() { return 1; }

    @Override
    public boolean isEmpty() { return getCount() == 0; }

    // ── Observer management ──

    public void registerDataSetObserver(DataSetObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        observers.remove(observer);
    }

    public void notifyDataSetChanged() {
        for (DataSetObserver o : observers) o.onChanged();
    }

    public void notifyDataSetInvalidated() {
        for (DataSetObserver o : observers) o.onInvalidated();
    }
}
