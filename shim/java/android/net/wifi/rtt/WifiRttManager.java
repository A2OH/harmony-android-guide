package android.net.wifi.rtt;
import java.util.concurrent.Executor;

/**
 * Android-compatible WifiRttManager shim. Stub.
 */
public class WifiRttManager {

    public boolean isAvailable() {
        return false;
    }

    public void startRanging(RangingRequest request,
                             java.util.concurrent.Executor executor,
                             RangingResultCallback callback) {
        // Stub: Wi-Fi RTT not available in shim layer
        callback.onRangingFailure(RangingResultCallback.STATUS_CODE_FAIL);
    }
}
