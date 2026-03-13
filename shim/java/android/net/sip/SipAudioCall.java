package android.net.sip;

/**
 * Android-compatible SipAudioCall shim for A2OH migration.
 * All operations are no-ops; state flags always return false.
 */
public class SipAudioCall {

    // -----------------------------------------------------------------------
    // Listener interface

    public static abstract class Object {
        public void onReadyToCall(SipAudioCall call) {}
        public void onCalling(SipAudioCall call) {}
        public void onRinging(SipAudioCall call, SipProfile caller) {}
        public void onRingingBack(SipAudioCall call) {}
        public void onCallEstablished(SipAudioCall call) {}
        public void onCallEnded(SipAudioCall call) {}
        public void onCallBusy(SipAudioCall call) {}
        public void onCallHeld(SipAudioCall call) {}
        public void onError(SipAudioCall call, int errorCode, String errorMessage) {}
        public void onChanged(SipAudioCall call) {}
    }

    // -----------------------------------------------------------------------

    private boolean mMuted  = false;
    private boolean mOnHold = false;
    private boolean mInCall = false;

    public SipAudioCall() {}

    /** Starts audio stream for this call. */
    public void startAudio() {
        mInCall = true;
    }

    /** Ends the call. */
    public void endCall() {
        mInCall = false;
        mOnHold = false;
    }

    /** Puts the call on hold. */
    public void holdCall(int timeout) {
        mOnHold = true;
    }

    /** Answers an incoming call. */
    public void answerCall(int timeout) {
        mInCall = true;
    }

    /** Sends a DTMF tone. */
    public void sendDtmf(int code) {}

    /** Sends a DTMF tone with a message callback (typed as Object for shim portability). */
    public void sendDtmf(int code, Object result) {}

    /** Turns the speaker on or off. */
    public void setSpeakerMode(boolean speakerMode) {}

    /** Toggles the microphone mute state. */
    public void toggleMute() {
        mMuted = !mMuted;
    }

    public boolean isMuted()  { return mMuted; }
    public boolean isOnHold() { return mOnHold; }
    public boolean isInCall() { return mInCall; }

    /** Sets the listener for call events. */
    public void setListener(Object listener) {}

    /** Sets the listener, optionally calling back immediately with current state. */
    public void setListener(Object listener, boolean callbackImmediately) {}

    /** Returns the local SIP profile. */
    public SipProfile getLocalProfile() { return null; }

    /** Returns the peer SIP profile. */
    public SipProfile getPeerProfile() { return null; }
}
