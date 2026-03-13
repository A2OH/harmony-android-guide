package android.database.sqlite;

/**
 * Android-compatible SQLiteStatement shim.
 * Represents a compiled SQLite statement that can be executed (DML/DDL) or
 * queried for a single scalar value. Extends {@link SQLiteClosable} so callers
 * must release the object when done.
 *
 * All execution methods are stubs that print to stdout and return safe defaults.
 */
public class SQLiteStatement extends SQLiteClosable {

    private final String mSql;

    /** Package-private: constructed by SQLiteDatabase helpers. */
    public SQLiteStatement(String sql) {
        mSql = sql;
    }

    // -------------------------------------------------------------------------
    // SQLiteClosable implementation
    // -------------------------------------------------------------------------

    @Override
    protected void onAllReferencesReleased() {
        // stub: would finalize the native compiled statement
        System.out.println("[SQLiteStatement] onAllReferencesReleased sql=" + mSql);
    }

    // -------------------------------------------------------------------------
    // Bind helpers (stubs – real impl would forward to native sqlite3_bind_*)
    // -------------------------------------------------------------------------

    public void bindNull(int index) { /* stub */ }
    public void bindLong(int index, long value) { /* stub */ }
    public void bindDouble(int index, double value) { /* stub */ }
    public void bindString(int index, String value) { /* stub */ }
    public void bindBlob(int index, byte[] value) { /* stub */ }
    public void clearBindings() { /* stub */ }

    // -------------------------------------------------------------------------
    // Execution
    // -------------------------------------------------------------------------

    /**
     * Executes this SQL statement. Suitable for INSERT/UPDATE/DELETE/DDL.
     */
    public void execute() {
        System.out.println("[SQLiteStatement] execute: " + mSql);
    }

    /**
     * Executes this SQL INSERT statement and returns the row ID of the last
     * inserted row, or -1 if no row was inserted.
     *
     * @return the row ID of the inserted row, or -1
     */
    public long executeInsert() {
        System.out.println("[SQLiteStatement] executeInsert: " + mSql);
        return -1L;
    }

    /**
     * Executes this SQL UPDATE or DELETE statement and returns the number of
     * rows affected.
     *
     * @return number of rows changed
     */
    public int executeUpdateDelete() {
        System.out.println("[SQLiteStatement] executeUpdateDelete: " + mSql);
        return 0;
    }

    /**
     * Executes a statement that returns a single long value (e.g. a COUNT).
     *
     * @return the long result, or 0 as a stub default
     */
    public long simpleQueryForLong() {
        System.out.println("[SQLiteStatement] simpleQueryForLong: " + mSql);
        return 0L;
    }

    /**
     * Executes a statement that returns a single String value.
     *
     * @return the String result, or null as a stub default
     */
    public String simpleQueryForString() {
        System.out.println("[SQLiteStatement] simpleQueryForString: " + mSql);
        return null;
    }

    @Override
    public String toString() {
        return "SQLiteStatement[" + mSql + "]";
    }
}
