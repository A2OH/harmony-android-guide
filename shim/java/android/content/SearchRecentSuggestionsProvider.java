package android.content;

import android.database.Cursor;
import android.net.Uri;

/**
 * Android-compatible SearchRecentSuggestionsProvider shim.
 * Extends ContentProvider with setupSuggestions() stub.
 */
public class SearchRecentSuggestionsProvider extends ContentProvider {

    public static final int DATABASE_MODE_QUERIES = 1;
    public static final int DATABASE_MODE_2LINES = 2;

    private String mAuthority;
    private int mMode;

    /**
     * Set up the suggestions provider. Stub -- stores configuration only.
     */
    protected void setupSuggestions(String authority, int mode) {
        mAuthority = authority;
        mMode = mode;
    }

    @Override
    public boolean onCreate() {
        return true; // stub
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null; // stub
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return uri; // stub
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0; // stub
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0; // stub
    }

    @Override
    public String getType(Uri uri) {
        return null; // stub
    }
}
