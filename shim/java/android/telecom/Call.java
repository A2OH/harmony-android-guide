package android.telecom;

/**
 * Android-compatible Call shim. Stub implementation for mock testing.
 */
public class Call {

    /** Call is in a new, undetermined state. */
    public static final int STATE_NEW = 0;
    /** Call is ringing. */
    public static final int STATE_RINGING = 2;
    /** Call is being dialed. */
    public static final int STATE_DIALING = 3;
    /** Call is active. */
    public static final int STATE_ACTIVE = 4;
    /** Call is on hold. */
    public static final int STATE_HOLDING = 5;
    /** Call has been disconnected. */
    public static final int STATE_DISCONNECTED = 7;

    private int mState = STATE_NEW;
    private Details mDetails;

    /** Default constructor. */
    public Call() {
        mDetails = new Details();
    }

    /** Answers an incoming ringing call. */
    public void answer(int videoState) {
        System.out.println("[Telecom] Call.answer videoState=" + videoState);
        mState = STATE_ACTIVE;
    }

    /** Rejects an incoming ringing call. */
    public void reject(boolean rejectWithMessage, String textMessage) {
        System.out.println("[Telecom] Call.reject message=" + textMessage);
        mState = STATE_DISCONNECTED;
    }

    /** Disconnects an active or dialing call. */
    public void disconnect() {
        System.out.println("[Telecom] Call.disconnect");
        mState = STATE_DISCONNECTED;
    }

    /** Places an active call on hold. */
    public void hold() {
        System.out.println("[Telecom] Call.hold");
        mState = STATE_HOLDING;
    }

    /** Releases a held call back to active. */
    public void unhold() {
        System.out.println("[Telecom] Call.unhold");
        mState = STATE_ACTIVE;
    }

    /** Returns the Details object for this call. */
    public Details getDetails() {
        return mDetails;
    }

    /** Returns the current call state. */
    public int getState() {
        return mState;
    }

    /** Sets the call state (for shim simulation). */
    public void setState(int state) {
        mState = state;
    }

    @Override
    public String toString() {
        return "Call{state=" + mState + ", details=" + mDetails + "}";
    }

    // -------------------------------------------------------------------------
    // Details inner class
    // -------------------------------------------------------------------------

    /** Encapsulates details of a Call. */
    public static final class Details {
        private Object mHandle; // android.net.Uri stub
        private String mCallerDisplayName;
        private long mConnectTimeMillis;
        private int mCallDirection;

        public Details() {}

        /** Returns the handle (URI) of this call (returned as Object). */
        public Object getHandle() {
            return mHandle;
        }

        /** Sets the handle (for shim simulation). */
        public void setHandle(Object handle) {
            mHandle = handle;
        }

        /** Returns the caller display name. */
        public String getCallerDisplayName() {
            return mCallerDisplayName;
        }

        /** Sets the caller display name (for shim simulation). */
        public void setCallerDisplayName(String name) {
            mCallerDisplayName = name;
        }

        /** Returns the time this call connected, in milliseconds since epoch. */
        public long getConnectTimeMillis() {
            return mConnectTimeMillis;
        }

        /** Sets the connect time (for shim simulation). */
        public void setConnectTimeMillis(long millis) {
            mConnectTimeMillis = millis;
        }

        @Override
        public String toString() {
            return "Details{handle=" + mHandle + ", name=" + mCallerDisplayName + "}";
        }
    }
}
