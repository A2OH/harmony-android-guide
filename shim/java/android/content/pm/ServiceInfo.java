package android.content.pm;

public class ServiceInfo {
    public ServiceInfo() {}

    public static final int FLAG_EXTERNAL_SERVICE = 0;
    public static final int FLAG_ISOLATED_PROCESS = 0;
    public static final int FLAG_SINGLE_USER = 0;
    public static final int FLAG_STOP_WITH_TASK = 0;
    public static final int FLAG_USE_APP_ZYGOTE = 0;
    public static final int FOREGROUND_SERVICE_TYPE_CAMERA = 0;
    public static final int FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE = 0;
    public static final int FOREGROUND_SERVICE_TYPE_DATA_SYNC = 0;
    public static final int FOREGROUND_SERVICE_TYPE_LOCATION = 0;
    public static final int FOREGROUND_SERVICE_TYPE_MANIFEST = 0;
    public static final int FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK = 0;
    public static final int FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION = 0;
    public static final int FOREGROUND_SERVICE_TYPE_MICROPHONE = 0;
    public static final int FOREGROUND_SERVICE_TYPE_NONE = 0;
    public static final int FOREGROUND_SERVICE_TYPE_PHONE_CALL = 0;
    public int flags = 0;
    public int permission = 0;
    public int describeContents() { return 0; }
    public void dump(Object p0, Object p1) {}
    public int getForegroundServiceType() { return 0; }
}
