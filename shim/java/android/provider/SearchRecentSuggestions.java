package android.provider;

/**
 * Android-compatible SearchRecentSuggestions shim.
 *
 * OH mapping: No direct equivalent; use @ohos.data.preferences or a custom
 * RdbStore table to persist recent query strings.
 *
 * Usage:
 *   SearchRecentSuggestions suggestions =
 *       new SearchRecentSuggestions(context, MyProvider.AUTHORITY, MyProvider.MODE);
 *   suggestions.saveRecentQuery(query, null);
 */
public class SearchRecentSuggestions {

    /** Mode flag: single-line suggestion mode (query text only). */
    public static final int QUERIES_PROJECTION_1LINE = 1;

    /** Mode flag: two-line suggestion mode (query + second line of text). */
    public static final int QUERIES_PROJECTION_2LINE = 3;

    // ── Projection column indices for QUERIES_PROJECTION_1LINE ────────────────
    public static final int QUERIES_PROJECTION_DATE_COLUMN    = 0;
    public static final int QUERIES_PROJECTION_QUERY_COLUMN   = 2;

    private final Object  mContext;    // Context represented as Object
    private final String  mAuthority;
    private final int     mMode;

    /**
     * Constructor.
     *
     * @param context   application context (typed as Object per A2OH convention)
     * @param authority the authority of the suggestions ContentProvider
     * @param mode      {@link #QUERIES_PROJECTION_1LINE} or
     *                  {@link #QUERIES_PROJECTION_2LINE}
     */
    public SearchRecentSuggestions(Object context, String authority, int mode) {
        mContext   = context;
        mAuthority = authority;
        mMode      = mode;
    }

    /**
     * Save a recently-used search query string.
     *
     * @param queryString the search string to persist
     * @param line2       optional second line of text (used if mode is
     *                    {@link #QUERIES_PROJECTION_2LINE}); may be null
     */
    public void saveRecentQuery(String queryString, String line2) {
        // Stub: in a real implementation this would insert a row into the
        // SearchRecentSuggestionsProvider ContentProvider.
    }

    /**
     * Clear the full history of recent queries.
     */
    public void clearHistory() {
        // Stub: in a real implementation this would delete all rows from the
        // SearchRecentSuggestionsProvider ContentProvider.
    }
}
