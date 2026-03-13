package android.view;

/**
 * Android-compatible OrientationEventListener shim.
 * Abstract base for listening to device orientation changes.
 */
public abstract class OrientationEventListener {

    public static final int ORIENTATION_UNKNOWN = -1;

    private boolean mEnabled = false;

    public OrientationEventListener(Object context) {
        // no-op: sensor wiring not available in shim
    }

    public OrientationEventListener(Object context, int rate) {
        // no-op: sensor wiring not available in shim
    }

    public void enable() {
        mEnabled = true;
    }

    public void disable() {
        mEnabled = false;
    }

    public boolean canDetectOrientation() {
        return false;
    }

    public abstract void onOrientationChanged(int orientation);
}
