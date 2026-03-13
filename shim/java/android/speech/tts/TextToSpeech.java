package android.speech.tts;

import java.util.Locale;

/**
 * Shim for android.speech.tts.TextToSpeech.
 * All methods return error/no-op — real synthesis not available on OpenHarmony.
 */
public class TextToSpeech {

    public static final int SUCCESS = 0;
    public static final int ERROR = -1;
    public static final int QUEUE_FLUSH = 0;
    public static final int QUEUE_ADD = 1;
    public static final String ACTION_TTS_QUEUE_PROCESSING_COMPLETED =
            "android.speech.tts.TTS_QUEUE_PROCESSING_COMPLETED";

    /** Object interface for TTS engine initialization. */
    public interface OnInitListener {
        void onInit(int status);
    }

    private final OnInitListener mListener;

    public TextToSpeech(Object context, OnInitListener listener) {
        mListener = listener;
    }

    public int speak(CharSequence text, int queueMode, Object params, String utteranceId) {
        return ERROR;
    }

    public int stop() {
        return ERROR;
    }

    public void shutdown() {
    }

    public int setLanguage(Locale loc) {
        return 0;
    }

    public int isLanguageAvailable(Locale loc) {
        return -2;
    }

    public int setSpeechRate(float speechRate) {
        return ERROR;
    }

    public int setPitch(float pitch) {
        return ERROR;
    }

    public void setOnUtteranceProgressListener(Object listener) {
    }
}
