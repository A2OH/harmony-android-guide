package android.widget;

import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.widget.SimpleCursorAdapter — CursorAdapter that maps Cursor columns
 * to TextView/ImageView children of a row layout using parallel from[]/to[] arrays.
 *
 * In headless/shim mode no layout inflation occurs; newView() returns null and
 * bindView() is a no-op unless a ViewBinder is installed that handles every column.
 * The ViewBinder hook is preserved for source compatibility with apps that customise
 * column-to-view binding.
 */
public class SimpleCursorAdapter extends CursorAdapter {

    /** @deprecated Use {@link #SimpleCursorAdapter(Object, int, Cursor, String[], int[], int)} */
    @Deprecated
    public static final int FLAG_AUTO_REQUERY = 0x01;

    /** Register a ContentObserver on the Cursor (default behaviour). */
    public static final int FLAG_REGISTER_CONTENT_OBSERVER = 0x02;

    private final int layout;
    private final String[] from;
    private final int[] to;
    private ViewBinder viewBinder;

    /**
     * Standard constructor (API 11+).
     *
     * @param context  The context (stored as Object for headless compatibility).
     * @param layout   Resource ID of the row layout to inflate.
     * @param cursor   The Cursor supplying data; may be null.
     * @param from     Column names to read from the Cursor.
     * @param to       View IDs in the row layout to bind each column into.
     * @param flags    Combination of {@link #FLAG_AUTO_REQUERY} /
     *                 {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
     */
    public SimpleCursorAdapter(Object context, int layout, Cursor cursor,
                               String[] from, int[] to, int flags) {
        super(context, cursor, flags);
        this.layout = layout;
        this.from = (from != null) ? from.clone() : new String[0];
        this.to   = (to   != null) ? to.clone()   : new int[0];
    }

    // ── ViewBinder ──

    /**
     * Set a custom binder that overrides the default column-to-view mapping.
     *
     * @param binder The ViewBinder to use; null removes any existing binder.
     */
    public void setViewBinder(ViewBinder binder) {
        this.viewBinder = binder;
    }

    /** Return the currently installed ViewBinder, or null. */
    public ViewBinder getViewBinder() {
        return viewBinder;
    }

    // ── CursorAdapter abstract methods ──

    /**
     * Returns null in headless/shim mode — layout inflation is not supported.
     * Concrete subclasses running on a real Android or ArkUI stack should
     * override this to inflate the row layout.
     */
    @Override
    public View newView(Object context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    /**
     * Binds Cursor columns to child Views in the row layout.
     *
     * If a ViewBinder is installed it is tried first for each column; if it
     * returns false the default text/image binding logic would apply (not
     * implemented in this headless shim).
     */
    @Override
    public void bindView(View view, Object context, Cursor cursor) {
        if (view == null || cursor == null) return;
        for (int i = 0; i < from.length && i < to.length; i++) {
            int colIndex = cursor.getColumnIndex(from[i]);
            if (colIndex < 0) continue;
            String value = cursor.getString(colIndex);
            if (viewBinder != null) {
                // Let the custom binder handle it; ignore return value in shim
                viewBinder.setViewValue(view, value, value != null ? value : "");
            }
            // Default bind (setText / setImageURI) not implemented in headless shim
        }
    }

    // ── Accessor ──

    /** Return the column-name array used for binding. */
    public String[] getFrom() { return from.clone(); }

    /** Return the view-ID array used for binding. */
    public int[] getTo() { return to.clone(); }

    // ── Interface ──

    /**
     * Interface used to bind Cursor column values to Views.
     *
     * <p>Install via {@link #setViewBinder(ViewBinder)} to customise how individual
     * columns are rendered (e.g. formatting dates, loading images, etc.).</p>
     */
    public interface ViewBinder {
        /**
         * Bind a value from the Cursor to the specified View.
         *
         * @param view               The View that should display the data.
         * @param cursor             The Cursor positioned at the current row.
         * @param columnIndex        The column index to read from the Cursor.
         * @return true if binding was handled; false to use the default binding.
         */
        boolean setViewValue(View view, Cursor cursor, int columnIndex);
    }
}
