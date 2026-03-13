package android.database.sqlite;

/**
 * Android-compatible SQLiteQuery stub.
 * Represents a compiled SQLite query that can be filled in with
 * parameter values and executed.
 */
public final class SQLiteQuery extends SQLiteProgram {

    private final Object mDatabase;
    private final String mSql;

    /**
     * Creates a SQLiteQuery with the given database and SQL string.
     *
     * @param database the database object (typed as Object for portability)
     * @param sql      the SQL query string
     */
    public SQLiteQuery(Object database, String sql) {
        mDatabase = database;
        mSql = sql;
    }

    @Override
    public String toString() {
        return mSql;
    }
}
