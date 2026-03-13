package android.telephony.mbms;
import java.util.List;

public class GroupCall implements AutoCloseable {
    public static final int REASON_BY_USER_REQUEST = 0;
    public static final int REASON_FREQUENCY_CONFLICT = 0;
    public static final int REASON_LEFT_MBMS_BROADCAST_AREA = 0;
    public static final int REASON_NONE = 0;
    public static final int REASON_NOT_CONNECTED_TO_HOMECARRIER_LTE = 0;
    public static final int REASON_OUT_OF_MEMORY = 0;
    public static final int STATE_STALLED = 0;
    public static final int STATE_STARTED = 0;
    public static final int STATE_STOPPED = 0;

    public GroupCall() {}

    public void close() {}
    public long getTmgi() { return 0L; }
    public void updateGroupCall(java.util.List<Object> p0, java.util.List<Object> p1) {}
}
