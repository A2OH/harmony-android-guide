package android.widget;

/**
 * Shim: android.widget.Filterable — marks an adapter as supporting client-driven filtering.
 *
 * Adapters that implement this interface (e.g. ArrayAdapter, CursorAdapter) expose a
 * {@link Filter} that can narrow their data set based on a CharSequence constraint.
 */
public interface Filterable {

    /**
     * Returns a {@link Filter} that can be used to constrain data with a filtering pattern.
     *
     * @return a Filter used to filter the adapter's data set
     */
    Filter getFilter();
}
