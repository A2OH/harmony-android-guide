package android.telephony;
import android.content.Context;
import android.telephony.mbms.MbmsStreamingSessionCallback;
import java.util.List;
import java.util.concurrent.Executor;

public class MbmsStreamingSession implements AutoCloseable {
    public MbmsStreamingSession() {}

    public void close() {}
    public static MbmsStreamingSession create(Context p0, Executor p1, MbmsStreamingSessionCallback p2) { return null; }
    public void requestUpdateStreamingServices(java.util.List<Object> p0) {}
}
