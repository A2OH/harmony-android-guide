package android.widget;

import android.content.Context;
import android.database.Cursor;

/** Stub for AOSP SuggestionsAdapter used by SearchView. */
public class SuggestionsAdapter extends CursorAdapter {

    public static final int REFINE_NONE = 0;
    public static final int REFINE_BY_ENTRY = 1;
    public static final int REFINE_ALL = 2;

    public SuggestionsAdapter(Context context, android.app.SearchableInfo searchable,
                              android.app.SearchManager searchManager) {
    }

    public SuggestionsAdapter(Context context, SearchView searchView,
                              android.app.SearchableInfo searchable, java.util.WeakHashMap cache) {
    }

    public static String getColumnString(Cursor c, String columnName) {
        if (c == null || columnName == null) return null;
        try {
            int idx = c.getColumnIndex(columnName);
            if (idx >= 0) return c.getString(idx);
        } catch (Exception e) { /* ignore */ }
        return null;
    }

    public void setQueryRefinement(int refine) {}
    public void changeCursor(Cursor c) {}

    @Override
    public android.view.View newView(Context context, Cursor cursor, android.view.ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(android.view.View view, Context context, Cursor cursor) {}
}
