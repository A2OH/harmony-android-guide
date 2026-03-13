package android.net.wifi.rtt;

/**
 * Android-compatible RangingResult shim. Stub.
 */
public class RangingResult {

    public static final int STATUS_SUCCESS                                    = 0;
    public static final int STATUS_FAIL                                       = 1;
    public static final int STATUS_RESPONDER_DOES_NOT_SUPPORT_IEEE80211MC    = 2;

    private final int    status;
    private final int    distanceMm;
    private final int    distanceStdDevMm;
    private final int    rssi;
    private final Object macAddress;
    private final long   rangingTimestampMillis;

    public RangingResult(int status, Object macAddress, int distanceMm,
                         int distanceStdDevMm, int rssi, long rangingTimestampMillis) {
        this.status                 = status;
        this.macAddress             = macAddress;
        this.distanceMm             = distanceMm;
        this.distanceStdDevMm       = distanceStdDevMm;
        this.rssi                   = rssi;
        this.rangingTimestampMillis = rangingTimestampMillis;
    }

    public int getStatus()                   { return status; }
    public int getDistanceMm()               { return distanceMm; }
    public int getDistanceStdDevMm()         { return distanceStdDevMm; }
    public int getRssi()                     { return rssi; }
    public Object getMacAddress()            { return macAddress; }
    public long getRangingTimestampMillis()  { return rangingTimestampMillis; }
}
