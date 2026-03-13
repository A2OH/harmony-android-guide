package android.speech;

/**
 * Android-compatible SpeechRecognizer shim. Stub for A2OH migration.
 */
public class SpeechRecognizer {

    // -----------------------------------------------------------------------
    // Error constants (mirror android.speech.SpeechRecognizer)
    // -----------------------------------------------------------------------
    public static final int ERROR_NETWORK_TIMEOUT      = 1;
    public static final int ERROR_NETWORK               = 2;
    public static final int ERROR_AUDIO                 = 3;
    public static final int ERROR_SERVER                = 4;
    public static final int ERROR_CLIENT                = 5;
    public static final int ERROR_SPEECH_TIMEOUT        = 6;
    public static final int ERROR_NO_MATCH              = 7;
    public static final int ERROR_RECOGNIZER_BUSY       = 8;
    public static final int ERROR_INSUFFICIENT_PERMISSIONS = 9;
    public static final int ERROR_TOO_MANY_REQUESTS     = 10;
    public static final int ERROR_SERVER_DISCONNECTED   = 11;
    public static final int ERROR_LANGUAGE_NOT_SUPPORTED = 12;
    public static final int ERROR_LANGUAGE_UNAVAILABLE  = 13;

    // -----------------------------------------------------------------------
    // RecognitionListener interface
    // -----------------------------------------------------------------------

    public interface RecognitionListener {
        void onReadyForSpeech(Object params);
        void onBeginningOfSpeech();
        void onRmsChanged(float rmsdB);
        void onBufferReceived(byte[] buffer);
        void onEndOfSpeech();
        void onError(int error);
        void onResults(Object results);
        void onPartialResults(Object partialResults);
        void onEvent(int eventType, Object params);
    }

    // -----------------------------------------------------------------------
    // Private state
    // -----------------------------------------------------------------------

    private RecognitionListener mListener;

    private SpeechRecognizer() {}

    // -----------------------------------------------------------------------
    // Factory methods
    // -----------------------------------------------------------------------

    /**
     * Creates a SpeechRecognizer for the given context, or {@code null} if
     * speech recognition is not available (always the case in this shim).
     */
    public static SpeechRecognizer createSpeechRecognizer(Object context) {
        return new SpeechRecognizer();
    }

    /**
     * Returns {@code false} — speech recognition is not available in the shim.
     */
    public static boolean isRecognitionAvailable(Object context) {
        return false;
    }

    // -----------------------------------------------------------------------
    // Instance methods
    // -----------------------------------------------------------------------

    public void setRecognitionListener(RecognitionListener listener) {
        mListener = listener;
    }

    public void startListening(Object recognizerIntent) {
        if (mListener != null) {
            mListener.onError(ERROR_RECOGNIZER_BUSY);
        }
    }

    public void stopListening() {
        // no-op
    }

    public void cancel() {
        // no-op
    }

    public void destroy() {
        mListener = null;
    }

    @Override
    public String toString() {
        return "SpeechRecognizer[shim/unavailable]";
    }
}
