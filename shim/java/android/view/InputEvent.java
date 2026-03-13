package android.view;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Shim: android.view.InputEvent — abstract base class for input events.
 *
 * Common superclass of {@link KeyEvent} and {@link MotionEvent}.
 * Pure Java stub for the A2OH compatibility layer; all methods return
 * sensible defaults (null, 0, or false).
 */
public class InputEvent implements Parcelable {

    /** @hide */
    protected int mSource;

    /**
     * Returns the InputDevice for this event, or null if unknown.
     * Stub: always returns null.
     */
    public InputDevice getDevice() {
        return null;
    }

    /**
     * Returns the id for the device that this event came from.
     * Stub: always returns 0 (virtual keyboard).
     */
    public int getDeviceId() {
        return 0;
    }

    /**
     * Returns the source of this event.
     */
    public int getSource() {
        return mSource;
    }

    /**
     * Modifies the source of this event.
     */
    public void setSource(int source) {
        mSource = source;
    }

    /**
     * Returns the time (in ms) when this specific event was generated.
     * Stub: always returns 0.
     */
    public long getEventTime() {
        return 0L;
    }

    /**
     * Determines whether the event is from the given source.
     *
     * @param source the input source to check against
     * @return true if the event comes from that source, false otherwise
     */
    public boolean isFromSource(int source) {
        return (getSource() & source) == source;
    }

    // ── Parcelable (abstract — subclasses must implement) ──

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Stub: no-op
    }
}
