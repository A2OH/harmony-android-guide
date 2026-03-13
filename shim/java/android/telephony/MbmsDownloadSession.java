package android.telephony;

public class MbmsDownloadSession {
    public MbmsDownloadSession() {}

    public static final int DEFAULT_TOP_LEVEL_TEMP_DIRECTORY = 0;
    public static final int EXTRA_MBMS_COMPLETED_FILE_URI = 0;
    public static final int EXTRA_MBMS_DOWNLOAD_REQUEST = 0;
    public static final int EXTRA_MBMS_DOWNLOAD_RESULT = 0;
    public static final int EXTRA_MBMS_FILE_INFO = 0;
    public static final int RESULT_CANCELLED = 0;
    public static final int RESULT_DOWNLOAD_FAILURE = 0;
    public static final int RESULT_EXPIRED = 0;
    public static final int RESULT_FILE_ROOT_UNREACHABLE = 0;
    public static final int RESULT_IO_ERROR = 0;
    public static final int RESULT_OUT_OF_STORAGE = 0;
    public static final int RESULT_SERVICE_ID_NOT_DEFINED = 0;
    public static final int RESULT_SUCCESSFUL = 0;
    public static final int STATUS_ACTIVELY_DOWNLOADING = 0;
    public static final int STATUS_PENDING_DOWNLOAD = 0;
    public static final int STATUS_PENDING_DOWNLOAD_WINDOW = 0;
    public static final int STATUS_PENDING_REPAIR = 0;
    public static final int STATUS_UNKNOWN = 0;
    public void addProgressListener(Object p0, Object p1, Object p2) {}
    public void addStatusListener(Object p0, Object p1, Object p2) {}
    public void cancelDownload(Object p0) {}
    public void close() {}
    public static Object create(Object p0, Object p1, Object p2) { return null; }
    public void download(Object p0) {}
    public void removeProgressListener(Object p0, Object p1) {}
    public void removeStatusListener(Object p0, Object p1) {}
    public void requestDownloadState(Object p0, Object p1) {}
    public void requestUpdateFileServices(Object p0) {}
    public void resetDownloadKnowledge(Object p0) {}
    public void setTempFileRootDirectory(Object p0) {}
}
