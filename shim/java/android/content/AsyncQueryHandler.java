package android.content;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.database.Cursor;

/**
 * Android-compatible AsyncQueryHandler shim.
 * Provides asynchronous wrappers around ContentResolver CRUD operations.
 * In this stub the operations execute synchronously on the calling thread.
 */
public abstract class AsyncQueryHandler extends Handler {

    private static final int EVENT_ARG_QUERY  = 1;
    private static final int EVENT_ARG_INSERT = 2;
    private static final int EVENT_ARG_UPDATE = 3;
    private static final int EVENT_ARG_DELETE = 4;

    private final ContentResolver mResolver;

    public AsyncQueryHandler(ContentResolver cr) {
        super(Looper.myLooper());
        mResolver = cr;
    }

    // ── query ────────────────────────────────────────────────────────────────

    /**
     * Start an asynchronous query. {@link #onQueryComplete} will be called
     * with the result. In this stub the call is made synchronously.
     *
     * @param token     caller-defined token passed back to {@link #onQueryComplete}
     * @param cookie    caller-defined cookie passed back to {@link #onQueryComplete}
     * @param uri       the URI to query (typed as Object for portability)
     * @param projection column list; null means all columns
     * @param selection  SQL WHERE clause (without the word WHERE)
     * @param selectionArgs values that replace '?' in selection
     * @param orderBy   sort order
     */
    public void startQuery(int token, Object cookie, Object uri,
                           String[] projection, String selection,
                           String[] selectionArgs, String orderBy) {
        // stub: no real resolver available; deliver null cursor immediately
        onQueryComplete(token, cookie, null);
    }

    /**
     * Called when an asynchronous query is complete.
     *
     * @param token  the token passed to {@link #startQuery}
     * @param cookie the cookie passed to {@link #startQuery}
     * @param cursor the result cursor; may be null if the query failed
     */
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        // default no-op — subclasses override
    }

    // ── insert ───────────────────────────────────────────────────────────────

    /**
     * Start an asynchronous insert. {@link #onInsertComplete} will be called
     * with the result URI (as Object). In this stub the call is made synchronously.
     *
     * @param token        caller-defined token
     * @param cookie       caller-defined cookie
     * @param uri          the URI into which to insert
     * @param initialValues values to insert
     */
    public void startInsert(int token, Object cookie, Object uri,
                            ContentValues initialValues) {
        onInsertComplete(token, cookie, null);
    }

    /**
     * Called when an asynchronous insert is complete.
     *
     * @param token  the token passed to {@link #startInsert}
     * @param cookie the cookie passed to {@link #startInsert}
     * @param uri    the URI of the newly inserted row, or null on failure
     */
    protected void onInsertComplete(int token, Object cookie, Object uri) {
        // default no-op — subclasses override
    }

    // ── update ───────────────────────────────────────────────────────────────

    /**
     * Start an asynchronous update. {@link #onUpdateComplete} will be called
     * with the number of rows affected.
     *
     * @param token         caller-defined token
     * @param cookie        caller-defined cookie
     * @param uri           the URI of the dataset to update
     * @param values        a map from column names to new column values
     * @param selection     SQL WHERE clause
     * @param selectionArgs values that replace '?' in selection
     */
    public void startUpdate(int token, Object cookie, Object uri,
                            ContentValues values, String selection,
                            String[] selectionArgs) {
        onUpdateComplete(token, cookie, 0);
    }

    /**
     * Called when an asynchronous update is complete.
     *
     * @param token  the token passed to {@link #startUpdate}
     * @param cookie the cookie passed to {@link #startUpdate}
     * @param result the number of rows updated
     */
    protected void onUpdateComplete(int token, Object cookie, int result) {
        // default no-op — subclasses override
    }

    // ── delete ───────────────────────────────────────────────────────────────

    /**
     * Start an asynchronous delete. {@link #onDeleteComplete} will be called
     * with the number of rows deleted.
     *
     * @param token         caller-defined token
     * @param cookie        caller-defined cookie
     * @param uri           the URI of the dataset to delete
     * @param selection     SQL WHERE clause
     * @param selectionArgs values that replace '?' in selection
     */
    public void startDelete(int token, Object cookie, Object uri,
                            String selection, String[] selectionArgs) {
        onDeleteComplete(token, cookie, 0);
    }

    /**
     * Called when an asynchronous delete is complete.
     *
     * @param token  the token passed to {@link #startDelete}
     * @param cookie the cookie passed to {@link #startDelete}
     * @param result the number of rows deleted
     */
    protected void onDeleteComplete(int token, Object cookie, int result) {
        // default no-op — subclasses override
    }

    // ── cancellation ─────────────────────────────────────────────────────────

    /**
     * Cancel any pending queries with the given token.
     * In this stub all operations are synchronous so this is a no-op.
     *
     * @param token the token of the operations to cancel
     */
    public final void cancelOperation(int token) {
        removeMessages(token);
    }
}
