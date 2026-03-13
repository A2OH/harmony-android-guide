package android.content;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

/**
 * Shim: android.content.ContentResolver — minimal stub.
 * Tier 3 — no direct OH equivalent; mapped to DataShareHelper on OH.
 *
 * ContentResolver is Android's client-side gateway to ContentProviders.
 * In OpenHarmony the equivalent consumer API is:
 *   @ohos.data.dataShare → DataShareHelper
 *   helper.query(uri, predicates, columns, callback)
 *   helper.insert(uri, value, callback)
 *   helper.update(uri, predicates, value, callback)
 *   helper.delete(uri, predicates, callback)
 *
 * This shim provides no-op implementations sufficient for unit tests that
 * exercise code paths expecting a ContentResolver but not actually reading
 * data from a provider.  Real OH integration requires a DataShareHelper
 * obtained from the UIAbilityContext.
 */
public class ContentResolver {

    // ── URI scheme constants ──────────────────────────────────────────────────

    /** URI scheme used by ContentProviders ("content://…"). */
    public static final String SCHEME_CONTENT = "content";

    /** URI scheme for local file access ("file://…"). */
    public static final String SCHEME_FILE    = "file";

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Construct a ContentResolver.  In production Android code this is created
     * by the framework; callers normally obtain it via
     * {@code Context.getContentResolver()}.
     */
    public ContentResolver() {
        // no state needed for the stub
    }

    // ── CRUD stubs ────────────────────────────────────────────────────────────

    /**
     * Query the given URI, returning a Cursor over the result set.
     *
     * <p><b>OH migration:</b> replace with
     * {@code DataShareHelper.query(uri, predicates, columns, callback)}.
     *
     * @return always null in this stub
     */
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        return null;
    }

    /**
     * Query variant with a cancellation signal (API 16+).
     *
     * @return always null in this stub
     */
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder,
                        Object cancellationSignal) {
        return null;
    }

    /**
     * Insert a row into the table designated by the given URI.
     *
     * <p><b>OH migration:</b> replace with
     * {@code DataShareHelper.insert(uri, valuesBucket, callback)}.
     *
     * @return always null in this stub
     */
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    /**
     * Update rows in the table designated by the given URI.
     *
     * <p><b>OH migration:</b> replace with
     * {@code DataShareHelper.update(uri, predicates, valuesBucket, callback)}.
     *
     * @return always 0 in this stub
     */
    public int update(Uri uri,
                      ContentValues values,
                      String selection,
                      String[] selectionArgs) {
        return 0;
    }

    /**
     * Delete rows from the table designated by the given URI.
     *
     * <p><b>OH migration:</b> replace with
     * {@code DataShareHelper.delete(uri, predicates, callback)}.
     *
     * @return always 0 in this stub
     */
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of the given URI.
     *
     * @return always null in this stub
     */
    public String getType(Uri uri) {
        return null;
    }

    /**
     * Notify registered observers that a row was updated.
     * No-op in this stub.
     */
    public void notifyChange(Uri uri, Object observer) {
        // no-op
    }
}
