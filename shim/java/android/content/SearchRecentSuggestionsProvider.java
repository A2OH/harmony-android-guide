package android.content;
import android.database.Cursor;
import android.net.Uri;

public class SearchRecentSuggestionsProvider extends ContentProvider {
    public static final int DATABASE_MODE_2LINES = 0;
    public static final int DATABASE_MODE_QUERIES = 0;

    public SearchRecentSuggestionsProvider() {}

    public int delete(Uri p0, String p1, String[] p2) { return 0; }
    public String getType(Uri p0) { return null; }
    public Uri insert(Uri p0, ContentValues p1) { return null; }
    public boolean onCreate() { return false; }
    public Cursor query(Uri p0, String[] p1, String p2, String[] p3, String p4) { return null; }
    public void setupSuggestions(String p0, int p1) {}
    public int update(Uri p0, ContentValues p1, String p2, String[] p3) { return 0; }
}
