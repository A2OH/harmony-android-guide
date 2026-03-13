package android.speech.tts;

import java.util.HashMap;
import java.util.Locale;

/**
 * Android-compatible TextToSpeech shim. Stub for A2OH migration.
 */
public class TextToSpeech {

    // -----------------------------------------------------------------------
    // Status / result codes
    // -----------------------------------------------------------------------
    public static final int SUCCESS = 0;
    public static final int ERROR   = -1;

    // -----------------------------------------------------------------------
    // Language availability codes
    // -----------------------------------------------------------------------
    public static final int LANG_AVAILABLE          =  0;
    public static final int LANG_COUNTRY_AVAILABLE  =  1;
    public static final int LANG_COUNTRY_VAR_AVAILABLE = 2;
    public static final int LANG_MISSING_DATA       = -1;
    public static final int LANG_NOT_SUPPORTED      = -2;

    // -----------------------------------------------------------------------
    // Queue modes
    // -----------------------------------------------------------------------
    public static final int QUEUE_FLUSH = 0;
    public static final int QUEUE_ADD   = 1;

    // -----------------------------------------------------------------------
    // Interfaces
    // -----------------------------------------------------------------------

    public interface OnInitListener {
        void onInit(int status);
    }

    /** @deprecated Use {@link UtteranceProgressListener} instead. */
    @Deprecated
    public interface OnUtteranceCompletedListener {
        void onUtteranceCompleted(String utteranceId);
    }

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    public TextToSpeech(Object context, OnInitListener listener) {
        // In the shim environment TTS engine is not present; signal error.
        if (listener != null) {
            listener.onInit(ERROR);
        }
    }

    // -----------------------------------------------------------------------
    // Playback
    // -----------------------------------------------------------------------

    public int speak(String text, int queueMode, HashMap<String, String> params) {
        return ERROR;
    }

    public int speak(CharSequence text, int queueMode, Object bundle, String utteranceId) {
        return ERROR;
    }

    public int stop() {
        return ERROR;
    }

    public void shutdown() {
        // no-op
    }

    // -----------------------------------------------------------------------
    // Language
    // -----------------------------------------------------------------------

    public int setLanguage(Locale loc) {
        return LANG_NOT_SUPPORTED;
    }

    public int isLanguageAvailable(Locale loc) {
        return LANG_NOT_SUPPORTED;
    }

    // -----------------------------------------------------------------------
    // Rate / pitch
    // -----------------------------------------------------------------------

    public int setSpeechRate(float speechRate) {
        return ERROR;
    }

    public int setPitch(float pitch) {
        return ERROR;
    }

    // -----------------------------------------------------------------------
    // Listener registration
    // -----------------------------------------------------------------------

    public void setOnUtteranceProgressListener(UtteranceProgressListener listener) {
        // no-op
    }

    @Override
    public String toString() {
        return "TextToSpeech[shim/unavailable]";
    }
}
