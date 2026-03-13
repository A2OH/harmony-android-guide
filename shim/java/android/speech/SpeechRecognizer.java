package android.speech;

/**
 * Android-compatible SpeechRecognizer stub.
 */
public class SpeechRecognizer {
    public static final int ERROR_NETWORK_TIMEOUT = 1;
    public static final int ERROR_NETWORK = 2;
    public static final int ERROR_AUDIO = 3;
    public static final int ERROR_SERVER = 4;
    public static final int ERROR_CLIENT = 5;
    public static final int ERROR_SPEECH_TIMEOUT = 6;
    public static final int ERROR_NO_MATCH = 7;
    public static final int ERROR_RECOGNIZER_BUSY = 8;
    public static final int ERROR_INSUFFICIENT_PERMISSIONS = 9;

    public static SpeechRecognizer createSpeechRecognizer(Object context) {
        return new SpeechRecognizer();
    }

    public static boolean isRecognitionAvailable(Object context) {
        return false;
    }

    public void setRecognitionListener(Object listener) { }
    public void startListening(Object intent) { }
    public void stopListening() { }
    public void cancel() { }
    public void destroy() { }
}
