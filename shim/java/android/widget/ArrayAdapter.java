package android.widget;

import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Shim: android.widget.ArrayAdapter<T> — adapter backed by a Java List.
 *
 * The resource ID and context are accepted for source compatibility but are not
 * used in this headless shim — getView() returns null, which callers must handle.
 */
public class ArrayAdapter<T> extends BaseAdapter {

    private final Object context;
    private final int resource;
    private final List<T> objects;

    public ArrayAdapter(Object context, int resource) {
        this.context = context;
        this.resource = resource;
        this.objects = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public ArrayAdapter(Object context, int resource, T[] items) {
        this.context = context;
        this.resource = resource;
        this.objects = new ArrayList<>(Arrays.asList(items));
    }

    public ArrayAdapter(Object context, int resource, List<T> items) {
        this.context = context;
        this.resource = resource;
        this.objects = new ArrayList<>(items);
    }

    // ── Mutation ──

    public void add(T object) {
        objects.add(object);
        notifyDataSetChanged();
    }

    public void addAll(T... items) {
        Collections.addAll(objects, items);
        notifyDataSetChanged();
    }

    public void addAll(List<T> items) {
        objects.addAll(items);
        notifyDataSetChanged();
    }

    public void insert(T object, int index) {
        objects.add(index, object);
        notifyDataSetChanged();
    }

    public void remove(T object) {
        objects.remove(object);
        notifyDataSetChanged();
    }

    public void clear() {
        objects.clear();
        notifyDataSetChanged();
    }

    public void sort(Comparator<? super T> comparator) {
        Collections.sort(objects, comparator);
        notifyDataSetChanged();
    }

    public int getPosition(T item) {
        return objects.indexOf(item);
    }

    // ── BaseAdapter contract ──

    @Override
    public int getCount() { return objects.size(); }

    @Override
    public T getItem(int position) { return objects.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    /**
     * Returns null in this shim. Real implementations inflate a TextView from
     * the provided resource — resource inflation is not supported in headless mode.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) { return null; }
}
