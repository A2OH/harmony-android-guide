package android.os;

/**
 * Android-compatible Vibrator shim. Stub — logs vibration requests.
 */
public class Vibrator {

    public boolean hasVibrator() {
        return true; // assume device has vibrator
    }

    public void vibrate(long milliseconds) {
        System.out.println("[Vibrator] vibrate " + milliseconds + "ms");
    }

    public void vibrate(long[] pattern, int repeat) {
        System.out.println("[Vibrator] vibrate pattern, repeat=" + repeat);
    }

    public void cancel() {
        System.out.println("[Vibrator] cancel");
    }
}
