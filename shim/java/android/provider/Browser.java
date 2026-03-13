package android.provider;

import android.net.Uri;

/**
 * Android-compatible Browser provider shim.
 *
 * OH mapping: @ohos.web.webview (read/write of browser history/bookmarks is not
 * exposed via a ContentProvider on OpenHarmony; migrate to WebviewController or
 * a local database if bookmark storage is required).
 */
public final class Browser {

    private Browser() {}

    /** Content URI for the bookmarks table. */
    public static final Uri BOOKMARKS_URI =
            Uri.parse("content://browser/bookmarks");

    /** Content URI for the searches table. */
    public static final Uri SEARCHES_URI =
            Uri.parse("content://browser/searches");

    // ── BookmarkColumns ────────────────────────────────────────────────────────

    /**
     * Column constants for the bookmarks table.
     */
    public static final class BookmarkColumns {
        private BookmarkColumns() {}

        public static final String _ID          = "_id";
        public static final String TITLE        = "title";
        public static final String URL          = "url";
        public static final String VISITS       = "visits";
        public static final String DATE         = "date";
        public static final String CREATED      = "created";
        public static final String DESCRIPTION  = "description";
        public static final String FAVICON      = "favicon";
        public static final String BOOKMARK     = "bookmark";
        public static final String USER_ENTERED = "user_entered";
    }

    // ── SearchColumns ──────────────────────────────────────────────────────────

    /**
     * Column constants for the searches table.
     */
    public static final class SearchColumns {
        private SearchColumns() {}

        public static final String _ID   = "_id";
        public static final String SEARCH = "search";
        public static final String DATE   = "date";
    }

    // ── Utility methods ────────────────────────────────────────────────────────

    /**
     * Send a search string to the browser.
     *
     * @param context   application context (typed as Object per A2OH convention)
     * @param string    the search query or URL to open
     */
    public static void sendString(Object context, String string) {
        // Stub: on real Android this fires an ACTION_WEB_SEARCH or ACTION_VIEW intent.
    }

    /**
     * Save a bookmark to the browser's bookmark database.
     *
     * @param context   application context (typed as Object per A2OH convention)
     * @param title     the display title of the bookmark
     * @param url       the URL to bookmark
     */
    public static void saveBookmark(Object context, String title, String url) {
        // Stub: inserts a row into BOOKMARKS_URI via ContentResolver.
    }

    /**
     * Return {@code true} if the given URL is present in the bookmarks table.
     *
     * @param context   application context (typed as Object per A2OH convention)
     * @param url       the URL to check
     */
    public static boolean isBookmark(Object context, String url) {
        return false; // stub
    }
}
