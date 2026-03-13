package android.telecom;
import android.net.Uri;
import android.net.Uri;
import java.net.URI;

/**
 * Android-compatible Connection shim. Abstract stub for mock testing.
 * Represents a phone call connection, managed by a ConnectionService.
 */
public class Connection {

    /** Connection is in an initial, undefined state. */
    public static final int STATE_INITIALIZING = 0;
    /** Connection is new and not yet active. */
    public static final int STATE_NEW = 1;
    /** Connection is ringing. */
    public static final int STATE_RINGING = 2;
    /** Connection is dialing. */
    public static final int STATE_DIALING = 3;
    /** Connection is active. */
    public static final int STATE_ACTIVE = 4;
    /** Connection is on hold. */
    public static final int STATE_HOLDING = 5;
    /** Connection has been disconnected. */
    public static final int STATE_DISCONNECTED = 6;

    private int mState = STATE_NEW;
    private Object mAddress; // android.net.Uri stub
    private String mCallerDisplayName;
    private DisconnectCause mDisconnectCause;

    // -------------------------------------------------------------------------
    // Callbacks — subclasses must implement relevant ones
    // -------------------------------------------------------------------------

    /** Called when the connection should be answered. */
    public void onAnswer() {}

    /** Called when the connection should be answered with a specified video state. */
    public void onAnswer(int videoState) { onAnswer(); }

    /** Called when the connection should be rejected. */
    public void onReject() {}

    /** Called when the connection should be disconnected. */
    public void onDisconnect() {}

    /** Called when the connection should be placed on hold. */
    public void onHold() {}

    /** Called when the connection should be released from hold. */
    public void onUnhold() {}

    // -------------------------------------------------------------------------
    // State mutators — called by ConnectionService to update the call state
    // -------------------------------------------------------------------------

    /** Sets the address (URI) for this connection. */
    public void setAddress(Object address, int presentation) {
        mAddress = address;
    }

    /** Sets the caller display name. */
    public void setCallerDisplayName(String callerDisplayName, int presentation) {
        mCallerDisplayName = callerDisplayName;
    }

    /** Sets this connection to the active state. */
    public void setActive() {
        mState = STATE_ACTIVE;
        System.out.println("[Telecom] Connection.setActive");
    }

    /** Sets this connection to the on-hold state. */
    public void setOnHold() {
        mState = STATE_HOLDING;
        System.out.println("[Telecom] Connection.setOnHold");
    }

    /** Sets this connection to the disconnected state with the given cause. */
    public void setDisconnected(DisconnectCause disconnectCause) {
        mDisconnectCause = disconnectCause;
        mState = STATE_DISCONNECTED;
        System.out.println("[Telecom] Connection.setDisconnected cause=" + disconnectCause);
    }

    /** Sets this connection to the ringing state. */
    public void setRinging() {
        mState = STATE_RINGING;
    }

    /** Sets this connection to the dialing state. */
    public void setDialing() {
        mState = STATE_DIALING;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /** Returns the current state of this connection. */
    public int getState() {
        return mState;
    }

    /** Returns the address (URI) of this connection (as Object). */
    public Object getAddress() {
        return mAddress;
    }

    /** Returns the caller display name. */
    public String getCallerDisplayName() {
        return mCallerDisplayName;
    }

    /** Returns the DisconnectCause, or null if not yet disconnected. */
    public DisconnectCause getDisconnectCause() {
        return mDisconnectCause;
    }

    @Override
    public String toString() {
        return "Connection{state=" + mState + ", address=" + mAddress + "}";
    }
}
