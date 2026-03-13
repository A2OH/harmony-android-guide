package android.content;
import android.accounts.Account;
import android.database.ContentObserver;
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
    public static final int SCHEME_ANDROID_RESOURCE = 0;
    public static final int SCHEME_CONTENT = 0;
    public static final int SCHEME_FILE = 0;
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

    public ContentResolver(Context p0) {}

    public static void addPeriodicSync(Account p0, String p1, Bundle p2, long p3) {}
    public static Object addStatusChangeListener(int p0, SyncStatusObserver p1) { return null; }
    public int bulkInsert(Uri p0, ContentValues[] p1) { return 0; }
    public static void cancelSync(Account p0, String p1) {}
    public static void cancelSync(SyncRequest p0) {}
    public int delete(Uri p0, String p1, String[] p2) { return 0; }
    public int delete(Uri p0, Bundle p1) { return 0; }
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
    public int update(Uri p0, ContentValues p1, String p2, String[] p3) { return 0; }
    public int update(Uri p0, ContentValues p1, Bundle p2) { return 0; }
    public static void validateSyncExtrasBundle(Bundle p0) {}
}
