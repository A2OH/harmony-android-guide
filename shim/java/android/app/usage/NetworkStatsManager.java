package android.app.usage;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager;

/**
 * Android-compatible NetworkStatsManager stub (API 23+).
 * Provides compile-compatible method signatures that return {@code null}.
 * On a real device these would delegate to the OpenHarmony network-stats
 * subsystem; here they are safe no-op stubs for the A2OH shim layer.
 */
public class NetworkStatsManager {

    /**
     * Queries aggregate network usage for a device over a time period.
     *
     * @param networkType  network type constant (e.g. ConnectivityManager.TYPE_WIFI)
     * @param subscriberId IMSI for cellular, or {@code null} for non-cellular
     * @param startTime    start of the query window in millis since epoch
     * @param endTime      end   of the query window in millis since epoch
     * @return {@code null} — stub implementation
     */
    public NetworkStats.Bucket querySummaryForDevice(int networkType, String subscriberId,
                                                     long startTime, long endTime) {
        return null;
    }

    /**
     * Queries summary network usage grouped by UID over a time period.
     *
     * @param networkType  network type constant
     * @param subscriberId IMSI for cellular, or {@code null}
     * @param startTime    start of the query window in millis since epoch
     * @param endTime      end   of the query window in millis since epoch
     * @return {@code null} — stub implementation
     */
    public NetworkStats querySummary(int networkType, String subscriberId,
                                     long startTime, long endTime) {
        return null;
    }

    /**
     * Queries detailed network usage for a specific UID over a time period.
     *
     * @param networkType  network type constant
     * @param subscriberId IMSI for cellular, or {@code null}
     * @param startTime    start of the query window in millis since epoch
     * @param endTime      end   of the query window in millis since epoch
     * @param uid          the UID to query
     * @return {@code null} — stub implementation
     */
    public NetworkStats queryDetailsForUid(int networkType, String subscriberId,
                                           long startTime, long endTime, int uid) {
        return null;
    }

    /**
     * Queries detailed network usage over a time period.
     *
     * @param networkType  network type constant
     * @param subscriberId IMSI for cellular, or {@code null}
     * @param startTime    start of the query window in millis since epoch
     * @param endTime      end   of the query window in millis since epoch
     * @return {@code null} — stub implementation
     */
    public NetworkStats queryDetails(int networkType, String subscriberId,
                                     long startTime, long endTime) {
        return null;
    }
}
