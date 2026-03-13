package android.database.sqlite;

/**
 * Android-compatible SQLiteTransactionListener shim.
 * Object interface for SQLite transaction lifecycle events.
 */
public interface SQLiteTransactionListener {

    /**
     * Called immediately after the transaction begins.
     */
    void onBegin();

    /**
     * Called immediately before the transaction is committed.
     */
    void onCommit();

    /**
     * Called if the transaction is rolled back, either explicitly via
     * {@code rollback} or implicitly when the transaction block throws.
     */
    void onRollback();
}
