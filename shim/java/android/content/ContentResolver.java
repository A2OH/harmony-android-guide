package android.content;
import android.accounts.Account;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import java.util.List;

public class ContentResolver {
    public static final int ANY_CURSOR_ITEM_TYPE = 0;
    public static final int CURSOR_DIR_BASE_TYPE = 0;
    public static final int CURSOR_ITEM_BASE_TYPE = 0;
    public static final int EXTRA_HONORED_ARGS = 0;
    public static final int EXTRA_REFRESH_SUPPORTED = 0;
    public static final int EXTRA_SIZE = 0;
    public static final int EXTRA_TOTAL_COUNT = 0;
    public static final int NOTIFY_DELETE = 0;
    public static final int NOTIFY_INSERT = 0;
    public static final int NOTIFY_SKIP_NOTIFY_FOR_DESCENDANTS = 0;
    public static final int NOTIFY_SYNC_TO_NETWORK = 0;
    public static final int NOTIFY_UPDATE = 0;
    public static final int QUERY_ARG_GROUP_COLUMNS = 0;
    public static final int QUERY_ARG_LIMIT = 0;
    public static final int QUERY_ARG_OFFSET = 0;
    public static final int QUERY_ARG_SORT_COLLATION = 0;
    public static final int QUERY_ARG_SORT_COLUMNS = 0;
    public static final int QUERY_ARG_SORT_DIRECTION = 0;
    public static final int QUERY_ARG_SORT_LOCALE = 0;
    public static final int QUERY_ARG_SQL_GROUP_BY = 0;
    public static final int QUERY_ARG_SQL_HAVING = 0;
    public static final int QUERY_ARG_SQL_LIMIT = 0;
    public static final int QUERY_ARG_SQL_SELECTION = 0;
    public static final int QUERY_ARG_SQL_SELECTION_ARGS = 0;
    public static final int QUERY_ARG_SQL_SORT_ORDER = 0;
    public static final int QUERY_SORT_DIRECTION_ASCENDING = 0;
    public static final int QUERY_SORT_DIRECTION_DESCENDING = 0;
    public static final String SCHEME_ANDROID_RESOURCE = "android.resource";
    public static final String SCHEME_CONTENT = "content";
    public static final String SCHEME_FILE = "file";
    public static final int SYNC_EXTRAS_DISCARD_LOCAL_DELETIONS = 0;
    public static final int SYNC_EXTRAS_DO_NOT_RETRY = 0;
    public static final int SYNC_EXTRAS_EXPEDITED = 0;
    public static final int SYNC_EXTRAS_IGNORE_BACKOFF = 0;
    public static final int SYNC_EXTRAS_IGNORE_SETTINGS = 0;
    public static final int SYNC_EXTRAS_INITIALIZE = 0;
    public static final int SYNC_EXTRAS_MANUAL = 0;
    public static final int SYNC_EXTRAS_OVERRIDE_TOO_MANY_DELETIONS = 0;
    public static final int SYNC_EXTRAS_REQUIRE_CHARGING = 0;
    public static final int SYNC_EXTRAS_UPLOAD = 0;
    public static final int SYNC_OBSERVER_TYPE_ACTIVE = 0;
    public static final int SYNC_OBSERVER_TYPE_PENDING = 0;
    public static final int SYNC_OBSERVER_TYPE_SETTINGS = 0;

    private final Context mContext;

    public ContentResolver(Context context) {
        mContext = context;
    }

    private ContentProvider acquireProvider(Uri uri) {
        if (uri == null) return null;
        String authority = uri.getAuthority();
        if (authority == null) return null;
        return android.app.MiniServer.get().getPackageManager().resolveProvider(authority);
    }

    // --------------------------------------------------------------
    // M4-PRE9 (2026-05-12): expose framework.jar's IContentProvider
    // surface as compile-time stubs so WestlakeContentResolver can
    // @Override them.  At runtime this whole shim ContentResolver is
    // stripped via scripts/framework_duplicates.txt; framework.jar's
    // real ContentResolver (which DOES declare these abstract methods)
    // takes over and WestlakeContentResolver's overrides plug into
    // framework's abstract surface directly.
    //
    // Method shapes match Android 16 ContentResolver.aidl:
    //   acquireProvider(Context,String)         PROTECTED ABSTRACT
    //   acquireUnstableProvider(Context,String) PROTECTED ABSTRACT
    //   releaseProvider(IContentProvider)       PUBLIC ABSTRACT
    //   releaseUnstableProvider(IContentProvider) PUBLIC ABSTRACT
    //   unstableProviderDied(IContentProvider)  PUBLIC ABSTRACT
    //   getUserId()                              PUBLIC (returns 0 default)
    //
    // For compile-time we provide non-abstract default bodies so the
    // shim ContentResolver remains concrete (existing callers tolerate
    // the safe defaults).  WestlakeContentResolver overrides them with
    // its no-op IContentProvider Proxy / USER_SYSTEM logic.
    // --------------------------------------------------------------

    protected android.content.IContentProvider acquireProvider(
            Context context, String name) {
        return null;
    }

    protected android.content.IContentProvider acquireUnstableProvider(
            Context context, String name) {
        return null;
    }

    public boolean releaseProvider(android.content.IContentProvider icp) {
        return false;
    }

    public boolean releaseUnstableProvider(android.content.IContentProvider icp) {
        return false;
    }

    public void unstableProviderDied(android.content.IContentProvider icp) {
        // no-op
    }

    public int getUserId() {
        return 0; // USER_SYSTEM
    }

    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        ContentProvider provider = acquireProvider(uri);
        if (provider != null) {
            return provider.query(uri, projection, selection, selectionArgs, sortOrder);
        }
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        ContentProvider provider = acquireProvider(uri);
        if (provider != null) return provider.insert(uri, values);
        return null;
    }

    public static void addPeriodicSync(Account p0, String p1, Bundle p2, long p3) {}
    public static Object addStatusChangeListener(int p0, SyncStatusObserver p1) { return null; }
    public int bulkInsert(Uri p0, ContentValues[] p1) { return 0; }
    public static void cancelSync(Account p0, String p1) {}
    public static void cancelSync(SyncRequest p0) {}
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        ContentProvider provider = acquireProvider(uri);
        if (provider != null) return provider.delete(uri, selection, selectionArgs);
        return 0;
    }
    public int delete(Uri uri, Bundle extras) {
        ContentProvider provider = acquireProvider(uri);
        if (provider != null) return provider.delete(uri, extras);
        return 0;
    }
    public String getType(Uri p0) { return null; }
    public static List<?> getCurrentSyncs() { return null; }
    public static int getIsSyncable(Account p0, String p1) { return 0; }
    public static boolean getMasterSyncAutomatically() { return false; }
    public static List<?> getPeriodicSyncs(Account p0, String p1) { return null; }
    public static SyncAdapterType[] getSyncAdapterTypes() { return null; }
    public static boolean getSyncAutomatically(Account p0, String p1) { return false; }
    public static boolean isSyncActive(Account p0, String p1) { return false; }
    public static boolean isSyncPending(Account p0, String p1) { return false; }
    public void notifyChange(Uri p0, ContentObserver p1) {}
    public void notifyChange(Uri p0, ContentObserver p1, int p2) {}
    public boolean refresh(Uri p0, Bundle p1, CancellationSignal p2) { return false; }
    public void registerContentObserver(Uri p0, boolean p1, ContentObserver p2) {}
    public void registerContentObserver(Uri p0, boolean p1, ContentObserver p2, int userId) {}
    public void releasePersistableUriPermission(Uri p0, int p1) {}
    public static void removePeriodicSync(Account p0, String p1, Bundle p2) {}
    public static void removeStatusChangeListener(Object p0) {}
    public static void requestSync(Account p0, String p1, Bundle p2) {}
    public static void requestSync(SyncRequest p0) {}
    public static void setIsSyncable(Account p0, String p1, int p2) {}
    public static void setMasterSyncAutomatically(boolean p0) {}
    public static void setSyncAutomatically(Account p0, String p1, boolean p2) {}
    public void takePersistableUriPermission(Uri p0, int p1) {}
    public void unregisterContentObserver(ContentObserver p0) {}
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        ContentProvider provider = acquireProvider(uri);
        if (provider != null) return provider.update(uri, values, selection, selectionArgs);
        return 0;
    }
    public int update(Uri uri, ContentValues values, Bundle extras) {
        ContentProvider provider = acquireProvider(uri);
        if (provider != null) return provider.update(uri, values, extras);
        return 0;
    }
    public static void validateSyncExtrasBundle(Bundle p0) {}

    public static class OpenResourceIdResult {
        public android.content.res.Resources r;
        public int id;
    }

    public OpenResourceIdResult getResourceId(android.net.Uri uri) throws java.io.FileNotFoundException {
        return new OpenResourceIdResult();
    }
}
