package android.app.appsearch;

/**
 * Shim: android.app.appsearch.AppSearchManager (API 31+)
 * No OpenHarmony equivalent — stub / no-op.
 */
public class AppSearchManager {

    /**
     * Creates a search session. No-op stub.
     *
     * @param searchContext ignored
     * @param executor      ignored
     * @param callback      ignored
     */
    public void createSearchSession(Object searchContext, Object executor, Object callback) {
        // no-op
    }

    /** Builder for search context. */
    public static final class SearchContext {

        private final String mDatabaseName;

        private SearchContext(String databaseName) {
            mDatabaseName = databaseName;
        }

        /** Returns the database name. */
        public String getDatabaseName() {
            return mDatabaseName;
        }

        /** Builder for {@link SearchContext}. */
        public static final class Builder {
            private final String mDatabaseName;

            /**
             * Creates a new Builder.
             *
             * @param databaseName the name of the database
             */
            public Builder(String databaseName) {
                mDatabaseName = databaseName;
            }

            /** Builds the {@link SearchContext}. */
            public SearchContext build() {
                return new SearchContext(mDatabaseName);
            }
        }
    }

    /** Stub for search results. */
    public static class SearchResults {

        /**
         * Retrieves the next page of results. No-op stub.
         *
         * @param executor ignored
         * @param callback ignored
         */
        public void getNextPage(Object executor, Object callback) {
            // no-op
        }
    }
}
