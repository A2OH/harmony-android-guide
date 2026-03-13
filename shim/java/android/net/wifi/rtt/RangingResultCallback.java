package android.net.wifi.rtt;

import java.util.List;

/**
 * Android-compatible RangingResultCallback shim. Abstract stub.
 */
public abstract class RangingResultCallback {

    public static final int STATUS_CODE_FAIL          = 1;
    public static final int STATUS_CODE_FAIL_RTT_NOT_AVAILABLE = 2;

    /**
     * Called when ranging results are available.
     *
     * @param results list of {@link RangingResult} for each requested peer.
     */
    public abstract void onRangingResults(List<RangingResult> results);

    /**
     * Called when ranging fails before any results are produced.
     *
     * @param code failure status code.
     */
    public abstract void onRangingFailure(int code);
}
