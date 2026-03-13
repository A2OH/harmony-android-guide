package android.content;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

/**
 * Shim: android.content.ContentProvider → @ohos.data.dataShare.DataShareExtensionAbility
 * Tier 2 — structural mapping.
 *
 * ContentProvider is Android's standard mechanism for sharing structured data
 * between processes. In OpenHarmony, the equivalent is DataShareExtensionAbility,
 * which exposes query/insert/update/delete callbacks via the dataShare module.
 *
 * Migration notes:
 * - Subclass this and implement the five abstract CRUD methods.
 * - On OH, your subclass corresponds to a DataShareExtensionAbility; the framework
 *   routes calls from DataShareHelper (consumer side) to these methods.
 * - The Uri authority maps to the dataShare URI registered in module.json5.
 * - Cursor returned from query() should be a MatrixCursor or SQLiteCursor backed
 *   by OH RdbStore — use the SQLiteDatabase shim for the actual storage.
 * - ContentValues maps directly to OH ValuesBucket (same key-value structure).
 * - bulkInsert() has a pure-Java default implementation; override for efficiency.
 * - call() maps loosely to DataShareExtensionAbility.normalizeUri/denormalizeUri
 *   or a custom RPC — no direct OH equivalent; override if needed.
 *
 * Lifecycle:
 *   attachInfo(context, info) → onCreate() → [CRUD calls] → (no explicit destroy)
 */
public abstract class ContentProvider {

    private Context mContext;

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    /**
     * Called by the system when the provider is first created.
     * Perform one-time initialization here (open databases, etc.).
     *
     * @return true if the provider was successfully loaded, false otherwise.
     */
    public abstract boolean onCreate();

    /**
     * Called by the runtime to attach a Context before onCreate().
     * Mirrors ContentProvider.attachInfo() in AOSP.
     *
     * @param context the hosting Context (UIAbilityContext on OH)
     * @param info    ProviderInfo from the manifest (stub on OH)
     */
    public void attachInfo(Context context, ProviderInfo info) {
        mContext = context;
    }

    /**
     * Returns the Context passed to {@link #attachInfo}.
     * May return null if called before attachInfo().
     */
    public final Context getContext() {
        return mContext;
    }

    // ── Abstract CRUD ──────────────────────────────────────────────────────────

    /**
     * Handle a query request.
     *
     * OH mapping: DataShareExtensionAbility.query(uri, predicates, columns, callback)
     *
     * @param uri           the URI of the data to query
     * @param projection    the list of columns to return; null returns all columns
     * @param selection     SQL WHERE clause (excluding WHERE itself); null selects all rows
     * @param selectionArgs values for '?' placeholders in selection
     * @param sortOrder     SQL ORDER BY clause; null uses the default sort order
     * @return a Cursor positioned before the first row, or null on error
     */
    public abstract Cursor query(Uri uri, String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder);

    /**
     * Handle an insert request.
     *
     * OH mapping: DataShareExtensionAbility.insert(uri, valueBucket, callback)
     *
     * @param uri    the URI of the table to insert into
     * @param values the key-value pairs of column names and values to insert
     * @return the URI of the newly inserted row, or null on failure
     */
    public abstract Uri insert(Uri uri, ContentValues values);

    /**
     * Handle an update request.
     *
     * OH mapping: DataShareExtensionAbility.update(uri, predicates, valueBucket, callback)
     *
     * @param uri           the URI of the rows to update
     * @param values        the new column values
     * @param selection     SQL WHERE clause; null updates all rows
     * @param selectionArgs values for '?' placeholders in selection
     * @return the number of rows affected
     */
    public abstract int update(Uri uri, ContentValues values, String selection,
                               String[] selectionArgs);

    /**
     * Handle a delete request.
     *
     * OH mapping: DataShareExtensionAbility.delete(uri, predicates, callback)
     *
     * @param uri           the URI of the rows to delete
     * @param selection     SQL WHERE clause; null deletes all rows
     * @param selectionArgs values for '?' placeholders in selection
     * @return the number of rows deleted
     */
    public abstract int delete(Uri uri, String selection, String[] selectionArgs);

    /**
     * Return the MIME type of the data at the given URI.
     *
     * OH mapping: DataShareExtensionAbility.getType — no direct equivalent;
     * return a custom MIME string matching your data schema.
     *
     * @param uri the URI to query
     * @return a MIME type string, or null if the type is unknown
     */
    public abstract String getType(Uri uri);

    // ── Default implementations ────────────────────────────────────────────────

    /**
     * Insert multiple rows in a single call. The default implementation calls
     * {@link #insert} in a loop; override for transactional/bulk efficiency.
     *
     * OH note: DataShareExtensionAbility has no bulkInsert equivalent; wrap in
     * an RdbStore transaction (beginTransaction/commit) in your override.
     *
     * @param uri    the URI to insert rows into
     * @param values an array of ContentValues, one per row
     * @return the number of rows successfully inserted
     */
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int count = 0;
        for (ContentValues cv : values) {
            if (insert(uri, cv) != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * A general-purpose hook for calling a provider-defined method identified by
     * {@code method}. The default implementation returns null.
     *
     * OH mapping: no direct DataShareExtensionAbility equivalent. If you need
     * custom RPC, implement this and route calls through a ServiceExtensionAbility
     * or a custom ArkTS IPC mechanism.
     *
     * @param method the method name to call
     * @param arg    optional string argument
     * @param extras optional Bundle with additional parameters
     * @return a result Bundle, or null if not handled
     */
    public Bundle call(String method, String arg, Bundle extras) {
        return null;
    }

    // ── ProviderInfo stub ──────────────────────────────────────────────────────

    /**
     * Stub for android.content.pm.ProviderInfo. Carries the authority string
     * needed for URI matching. On OH this maps to the dataShare URI registered
     * in module.json5 under abilities[].uri.
     */
    public static class ProviderInfo {
        /** The authority part of the content:// URI for this provider. */
        public String authority;

        /** Package that owns this provider. */
        public String packageName;

        /** Whether this provider is exported (visible to other apps). */
        public boolean exported = false;

        public ProviderInfo() {}

        public ProviderInfo(String authority) {
            this.authority = authority;
        }
    }
}
