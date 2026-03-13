package android.app;

/**
 * Android-compatible SearchManager shim. Stub — search UI not available on OHOS.
 */
public class SearchManager {

    /** Intent extra key for the search query string. */
    public static final String QUERY = "query";

    /** Intent extra key for the user-entered query. */
    public static final String USER_QUERY = "user_query";

    /** Intent action for global search. */
    public static final String INTENT_ACTION_GLOBAL_SEARCH =
            "android.search.action.GLOBAL_SEARCH";

    /** Intent action for a search settings activity. */
    public static final String INTENT_ACTION_SEARCH_SETTINGS =
            "android.search.action.SEARCH_SETTINGS";

    /** Intent extra for the component name of the searchable activity. */
    public static final String COMPONENT_NAME_KEY = "intent_extra_data_key";

    /** Suggests-URI path segment constant. */
    public static final String SUGGEST_URI_PATH_QUERY = "search_suggest_query";

    /** Column name for suggestion text. */
    public static final String SUGGEST_COLUMN_TEXT_1 = "suggest_text_1";
    public static final String SUGGEST_COLUMN_TEXT_2 = "suggest_text_2";
    public static final String SUGGEST_COLUMN_ICON_1 = "suggest_icon_1";
    public static final String SUGGEST_COLUMN_ICON_2 = "suggest_icon_2";
    public static final String SUGGEST_COLUMN_INTENT_ACTION = "suggest_intent_action";
    public static final String SUGGEST_COLUMN_INTENT_DATA   = "suggest_intent_data";
    public static final String SUGGEST_COLUMN_QUERY         = "suggest_intent_query";
    public static final String SUGGEST_COLUMN_SHORTCUT_ID   = "suggest_shortcut_id";

    private boolean mSearching = false;

    /**
     * Start a search. In the shim this is a no-op — no UI is shown.
     *
     * @param initialQuery  pre-filled query string, or {@code null}.
     * @param selectInitialQuery  whether to pre-select the initial query text.
     * @param launchActivity  the activity that launched the search (may be null).
     * @param appSearchData  additional application data passed to the search activity.
     * @param globalSearch   true to start a global (web) search.
     */
    public void startSearch(String initialQuery,
                            boolean selectInitialQuery,
                            Object launchActivity,
                            Object appSearchData,
                            boolean globalSearch) {
        mSearching = true;
        System.out.println("[SearchManager] startSearch: " + initialQuery
                + " global=" + globalSearch);
    }

    /**
     * Stop the active search and dismiss the search dialog.
     */
    public void stopSearch() {
        mSearching = false;
    }

    /**
     * Returns {@code true} if a search is currently active.
     */
    public boolean isVisible() {
        return mSearching;
    }

    /**
     * Trigger a query for search suggestions. Stub — returns nothing.
     *
     * @param searchable  searchable info (may be null in shim).
     * @param query       the query string.
     */
    public void triggerSearch(String query, Object searchable) {
        System.out.println("[SearchManager] triggerSearch: " + query);
    }
}
