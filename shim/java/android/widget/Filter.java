package android.widget;

/**
 * Shim: android.widget.Filter — abstract filtering mechanism for Filterable adapters.
 *
 * Subclasses implement performFiltering() (runs on a worker thread) and
 * publishResults() (runs on the UI thread) to supply filtered content.
 */
public class Filter {

    // ── Inner class ──────────────────────────────────────────────────────────

    /**
     * Holds the results of a filtering operation.
     */
    public static class FilterResults {
        /** Number of items in the filtered set. */
        public int count = 0;
        /** The filtered data, type is adapter-specific. */
        public Object values = null;
    }

    // ── FilterListener ───────────────────────────────────────────────────────

    /**
     * Optional callback notified after publishResults() completes.
     */
    public interface FilterListener {
        void onFilterComplete(int count);
    }

    // ── Abstract methods ─────────────────────────────────────────────────────

    /**
     * Invoked on a worker thread to perform the filtering.
     *
     * @param constra(int the constra(int used to filter the data
     * @return            a FilterResults object containing the filtered set
     */
    protected FilterResults performFiltering(CharSequence constraint) { return null; }

    /**
     * Invoked on the UI thread to publish the results of the filtering.
     *
     * @param constra(int the constra(int used to filter the data
     * @param results     the results of the filtering operation
     */
    protected void publishResults(CharSequence constraint, FilterResults results) {}

    // ── Public filter entry points ───────────────────────────────────────────

    /**
     * Starts a filtering operation. In this shim the work is performed
     * synchronously on the calling thread (no background thread is created).
     *
     * @param constra(int the constra(int used to filter the data
     */
    public final void filter(CharSequence constraint) {
        filter(constraint, null);
    }

    /**
     * Starts a filtering operation with an optional completion listener.
     *
     * @param constra(int the constra(int used to filter the data
     * @param listener   optional listener notified when filtering completes
     */
    public final void filter(CharSequence constraint, FilterListener listener) {
        FilterResults results = performFiltering(constraint);
        if (results == null) results = new FilterResults();
        publishResults(constraint, results);
        if (listener != null) {
            listener.onFilterComplete(results.count);
        }
    }

    // ── convertResultToString ────────────────────────────────────────────────

    /**
     * Converts a result item to a CharSequence for display in an AutoComplete
     * widget. Default returns {@code Object.toString()}.
     *
     * @param resultValue a single item from FilterResults.values
     * @return CharSequence representation of the item
     */
    public CharSequence convertResultToString(Object resultValue) {
        return (resultValue == null) ? "" : resultValue.toString();
    }
}
