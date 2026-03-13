package android.accessibilityservice;

public final class FingerprintGestureController {
    public FingerprintGestureController() {}

    public static final int FINGERPRINT_GESTURE_SWIPE_DOWN = 0;
    public static final int FINGERPRINT_GESTURE_SWIPE_LEFT = 0;
    public static final int FINGERPRINT_GESTURE_SWIPE_RIGHT = 0;
    public static final int FINGERPRINT_GESTURE_SWIPE_UP = 0;
    public boolean isGestureDetectionAvailable() { return false; }
    public void registerFingerprintGestureCallback(Object p0, Object p1) {}
    public void unregisterFingerprintGestureCallback(Object p0) {}
    public void onGestureDetected(Object p0) {}
    public void onGestureDetectionAvailabilityChanged(Object p0) {}
}
