package android.net.sip;

/**
 * Android-compatible SipManager shim for A2OH migration.
 * Context is typed as Object to avoid pulling in android.content.Context.
 * All operations are no-ops; the device is treated as SIP-incapable.
 */
public class SipManager {

    // -----------------------------------------------------------------------
    // Registration listener interface

    public interface SipRegistrationListener {
        void onRegistering(String localProfileUri);
        void onRegistrationDone(String localProfileUri, long expiryTime);
        void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage);
    }

    // -----------------------------------------------------------------------

    private SipManager() {}

    /**
     * Creates a new SipManager. Returns null if SIP is not available on this
     * platform (which is always the case for this stub).
     *
     * @param context the application context (typed as Object for shim portability)
     */
    public static SipManager newInstance(Object context) {
        return null;
    }

    // -----------------------------------------------------------------------
    // Session management

    /** Opens a SIP session for the given profile. No-op in this stub. */
    public void open(SipProfile localProfile) {}

    /** Opens a SIP session and registers a listener. No-op in this stub.
     *  incomingCallPendingIntent is typed as Object for shim portability. */
    public void open(SipProfile localProfile,
                     Object incomingCallPendingIntent,
                     SipRegistrationListener listener) {}

    /** Closes the SIP session for the given profile. No-op in this stub. */
    public void close(String localProfileUri) {}

    /** Returns false: SIP sessions are never open in this stub. */
    public boolean isOpened(String localProfileUri) { return false; }

    /** Returns false: SIP is never registered in this stub. */
    public boolean isRegistered(String localProfileUri) { return false; }

    // -----------------------------------------------------------------------
    // Call management

    /**
     * Makes an outgoing audio call.
     * @return a new (stub) SipAudioCall
     */
    public SipAudioCall makeAudioCall(String localProfileUri,
                                      String peerProfileUri,
                                      SipAudioCall.Listener listener,
                                      int timeout) {
        return new SipAudioCall();
    }

    /**
     * Makes an outgoing audio call using SipProfile objects.
     * @return a new (stub) SipAudioCall
     */
    public SipAudioCall makeAudioCall(SipProfile localProfile,
                                      SipProfile peerProfile,
                                      SipAudioCall.Listener listener,
                                      int timeout) {
        return new SipAudioCall();
    }

    /**
     * Accepts an incoming audio call intent (typed as Object for shim portability).
     * @return a new (stub) SipAudioCall
     */
    public SipAudioCall takeAudioCall(Object incomingCallIntent,
                                      SipAudioCall.Listener listener) {
        return new SipAudioCall();
    }

    // -----------------------------------------------------------------------
    // Intent helpers

    /** Returns false: no incoming call intents in this stub.
     *  intent is typed as Object for shim portability. */
    public static boolean isIncomingCallIntent(Object intent) {
        return false;
    }

    /** Returns null: no call ID extractable from the intent in this stub.
     *  incomingCallIntent is typed as Object for shim portability. */
    public static String getCallId(Object incomingCallIntent) {
        return null;
    }

    // -----------------------------------------------------------------------
    // Registration

    /**
     * Registers the local profile with a SIP server.
     * No-op in this stub; immediately fires onRegistrationFailed if listener != null.
     */
    public void register(SipProfile localProfile,
                         int expiryTime,
                         SipRegistrationListener listener) {}

    /**
     * Unregisters the local profile from a SIP server.
     * No-op in this stub.
     */
    public void unregister(SipProfile localProfile,
                           SipRegistrationListener listener) {}
}
