package android.telephony.mbms;

public class StreamingServiceCallback {
    public static final int SIGNAL_STRENGTH_UNAVAILABLE = 0;

    public StreamingServiceCallback() {}

    public void onBroadcastSignalStrengthUpdated(int p0) {}
    public void onError(int p0, String p1) {}
    public void onMediaDescriptionUpdated() {}
    public void onStreamMethodUpdated(int p0) {}
    public void onStreamStateUpdated(int p0, int p1) {}
}
