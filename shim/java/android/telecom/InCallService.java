package android.telecom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Android-compatible InCallService shim. Abstract stub for mock testing.
 * In-call UI services extend this class to be notified of call events.
 */
public abstract class InCallService {

    /** Audio route: earpiece. */
    public static final int ROUTE_EARPIECE = 0x1;
    /** Audio route: Bluetooth headset. */
    public static final int ROUTE_BLUETOOTH = 0x2;
    /** Audio route: wired headset. */
    public static final int ROUTE_WIRED_HEADSET = 0x4;
    /** Audio route: speaker. */
    public static final int ROUTE_SPEAKER = 0x8;

    private final List<Call> mCalls = new ArrayList<>();
    private boolean mMuted;
    private int mAudioRoute = ROUTE_EARPIECE;

    // -------------------------------------------------------------------------
    // Abstract callbacks — subclasses override to respond to call events
    // -------------------------------------------------------------------------

    /** Called when a new Call is added to this in-call session. */
    public abstract void onCallAdded(Call call);

    /** Called when a Call is removed from this in-call session. */
    public abstract void onCallRemoved(Call call);

    // -------------------------------------------------------------------------
    // API
    // -------------------------------------------------------------------------

    /** Returns an unmodifiable snapshot of current calls. */
    public List<Call> getCalls() {
        return Collections.unmodifiableList(mCalls);
    }

    /** Sets whether the microphone is muted. */
    public void setMuted(boolean muted) {
        mMuted = muted;
        System.out.println("[Telecom] InCallService.setMuted=" + muted);
    }

    /** Returns whether the microphone is currently muted. */
    public boolean isMuted() {
        return mMuted;
    }

    /** Sets the audio route. One of the ROUTE_* constants. */
    public void setAudioRoute(int route) {
        mAudioRoute = route;
        System.out.println("[Telecom] InCallService.setAudioRoute=0x" + Integer.toHexString(route));
    }

    /** Returns the current audio route. */
    public int getAudioRoute() {
        return mAudioRoute;
    }

    // -------------------------------------------------------------------------
    // Shim-internal helpers (package-visible for tests)
    // -------------------------------------------------------------------------

    /** Simulates a new incoming call being added (for testing). */
    public void addCall(Call call) {
        mCalls.add(call);
        onCallAdded(call);
    }

    /** Simulates an existing call being removed (for testing). */
    public void removeCall(Call call) {
        if (mCalls.remove(call)) {
            onCallRemoved(call);
        }
    }
}
