package android.telephony.mbms;

public class StreamingService implements AutoCloseable {
    public static final int BROADCAST_METHOD = 0;
    public static final int REASON_BY_USER_REQUEST = 0;
    public static final int REASON_END_OF_SESSION = 0;
    public static final int REASON_FREQUENCY_CONFLICT = 0;
    public static final int REASON_LEFT_MBMS_BROADCAST_AREA = 0;
    public static final int REASON_NONE = 0;
    public static final int REASON_NOT_CONNECTED_TO_HOMECARRIER_LTE = 0;
    public static final int REASON_OUT_OF_MEMORY = 0;
    public static final int STATE_STALLED = 0;
    public static final int STATE_STARTED = 0;
    public static final int STATE_STOPPED = 0;
    public static final int UNICAST_METHOD = 0;

    public StreamingService() {}

    public void close() {}
    public StreamingServiceInfo getInfo() { return null; }
}
