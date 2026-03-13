package android.widget;

import android.database.Cursor;

/**
 * Shim: android.widget.FilterQueryProvider interface
 *
 * Provides a Cursor for filtering query results, typically used with CursorAdapter.
 */
public interface FilterQueryProvider {

    Cursor runQuery(CharSequence constraint);
}
