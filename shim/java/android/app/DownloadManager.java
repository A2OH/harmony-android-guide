package android.app;
import android.net.Network;
import android.net.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Android-compatible DownloadManager shim. Stub — downloads are not executed.
 */
public class DownloadManager {

    // Column constants used by Query cursor results
    public static final String COLUMN_ID                    = "_id";
    public static final String COLUMN_TITLE                 = "title";
    public static final String COLUMN_DESCRIPTION           = "description";
    public static final String COLUMN_URI                   = "uri";
    public static final String COLUMN_LOCAL_URI             = "local_uri";
    public static final String COLUMN_LOCAL_FILENAME        = "local_filename";
    public static final String COLUMN_MEDIA_TYPE            = "media_type";
    public static final String COLUMN_TOTAL_SIZE_BYTES      = "total_size";
    public static final String COLUMN_BYTES_DOWNLOADED_SO_FAR = "bytes_so_far";
    public static final String COLUMN_STATUS                = "status";
    public static final String COLUMN_REASON                = "reason";

    // Status codes
    public static final int STATUS_PENDING    = 1;
    public static final int STATUS_RUNNING    = 2;
    public static final int STATUS_PAUSED     = 4;
    public static final int STATUS_SUCCESSFUL = 8;
    public static final int STATUS_FAILED     = 16;

    // Error codes
    public static final int ERROR_UNKNOWN                    = 1000;
    public static final int ERROR_FILE_ERROR                 = 1001;
    public static final int ERROR_UNHANDLED_HTTP_CODE        = 1002;
    public static final int ERROR_HTTP_DATA_ERROR            = 1004;
    public static final int ERROR_TOO_MANY_REDIRECTS         = 1005;
    public static final int ERROR_INSUFFICIENT_SPACE         = 1006;
    public static final int ERROR_DEVICE_NOT_FOUND           = 1007;
    public static final int ERROR_CANNOT_RESUME              = 1008;
    public static final int ERROR_FILE_ALREADY_EXISTS        = 1009;

    // Paused reasons
    public static final int PAUSED_WAITING_TO_RETRY          = 1;
    public static final int PAUSED_WAITING_FOR_NETWORK       = 2;
    public static final int PAUSED_QUEUED_FOR_WIFI           = 3;
    public static final int PAUSED_UNKNOWN                   = 4;

    // Network type flags for Request
    public static final int NETWORK_MOBILE  = 1;
    public static final int NETWORK_WIFI    = 2;
    public static final int NETWORK_BLUETOOTH = 4;

    // Broadcast action
    public static final String ACTION_DOWNLOAD_COMPLETE  = "android.intent.action.DOWNLOAD_COMPLETE";
    public static final String ACTION_NOTIFICATION_CLICKED = "android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED";
    public static final String EXTRA_DOWNLOAD_ID        = "extra_download_id";

    private long mNextId = 1;
    private final Map<Long, Object> mQueue = new HashMap<>();

    // -------------------------------------------------------------------------
    // Request inner class
    // -------------------------------------------------------------------------

    public static class Object {
        private String mUri;
        private String mDestinationUri;
        private String mTitle = "";
        private String mDescription = "";
        private String mMimeType;
        private boolean mAllowRoaming = true;
        private int mAllowedNetworkTypes = NETWORK_MOBILE | NETWORK_WIFI;
        private boolean mNotificationVisibility = true;
        private boolean mVisibleInDownloadsUi = true;

        public Object(String uri) {
            mUri = uri;
        }

        public Object setTitle(CharSequence title) {
            mTitle = title != null ? title.toString() : "";
            return this;
        }

        public Object setDescription(CharSequence description) {
            mDescription = description != null ? description.toString() : "";
            return this;
        }

        public Object setMimeType(String type) {
            mMimeType = type;
            return this;
        }

        public Object setDestinationUri(String uri) {
            mDestinationUri = uri;
            return this;
        }

        public Object setAllowedNetworkTypes(int flags) {
            mAllowedNetworkTypes = flags;
            return this;
        }

        public Object setAllowedOverRoaming(boolean allowed) {
            mAllowRoaming = allowed;
            return this;
        }

        public Object setAllowedOverMetered(boolean allowed) {
            return this; // absorbed into network type logic
        }

        public Object setNotificationVisibility(int visibility) {
            mNotificationVisibility = (visibility != 0);
            return this;
        }

        public Object setVisibleInDownloadsUi(boolean isVisible) {
            mVisibleInDownloadsUi = isVisible;
            return this;
        }

        public Object addRequestHeader(String header, String value) {
            return this; // stub — HTTP headers not forwarded in shim
        }

        public String getUri() { return mUri; }
        public String getTitle() { return mTitle; }
        public String getDescription() { return mDescription; }
        public String getDestinationUri() { return mDestinationUri; }
    }

    // -------------------------------------------------------------------------
    // Query inner class
    // -------------------------------------------------------------------------

    public static class Query {
        private long[] mIds;
        private int mStatusFlags = ~0; // all statuses
        private String mOrderByColumn = COLUMN_ID;
        private boolean mOrderAscending = true;

        public Query setFilterById(long... ids) {
            mIds = ids;
            return this;
        }

        public Query setFilterByStatus(int flags) {
            mStatusFlags = flags;
            return this;
        }

        public Query orderBy(String column, int direction) {
            mOrderByColumn = column;
            mOrderAscending = (direction == 1);
            return this;
        }

        public long[] getIds() { return mIds; }
        public int getStatusFlags() { return mStatusFlags; }
    }

    // -------------------------------------------------------------------------
    // Stub cursor returned by query()
    // -------------------------------------------------------------------------

    public static class DownloadCursor {
        private final List<Map<String, Object>> mRows;
        private int mPos = -1;

        DownloadCursor(List<Map<String, Object>> rows) {
            mRows = rows;
        }

        public boolean moveToFirst() {
            if (mRows.isEmpty()) return false;
            mPos = 0;
            return true;
        }

        public boolean moveToNext() {
            if (mPos + 1 >= mRows.size()) return false;
            mPos++;
            return true;
        }

        public int getCount() { return mRows.size(); }

        public long getLong(int column) {
            // stub — column index mapping not implemented
            return 0L;
        }

        public int getInt(int column) {
            return 0;
        }

        public String getString(int column) {
            return "";
        }

        public int getColumnIndex(String name) {
            return -1; // stub
        }

        public void close() {}
    }

    // -------------------------------------------------------------------------
    // DownloadManager API
    // -------------------------------------------------------------------------

    public long enqueue(Object request) {
        long id = mNextId++;
        mQueue.put(id, request);
        System.out.println("[DownloadManager] enqueue id=" + id + " uri=" + request.getUri());
        return id;
    }

    public int remove(long... ids) {
        int removed = 0;
        for (long id : ids) {
            if (mQueue.remove(id) != null) removed++;
        }
        return removed;
    }

    public DownloadCursor query(Query query) {
        List<Map<String, Object>> rows = new ArrayList<>();
        // Return empty result set — stub, no real download tracking
        return new DownloadCursor(rows);
    }
}
