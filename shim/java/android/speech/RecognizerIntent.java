package android.speech;

/**
 * Android-compatible RecognizerIntent shim. Pure constants class for A2OH migration.
 */
public final class RecognizerIntent {

    // Not instantiable
    private RecognizerIntent() {}

    // -----------------------------------------------------------------------
    // Intent actions
    // -----------------------------------------------------------------------
    public static final String ACTION_RECOGNIZE_SPEECH =
            "android.speech.action.RECOGNIZE_SPEECH";
    public static final String ACTION_WEB_SEARCH =
            "android.speech.action.WEB_SEARCH";

    // -----------------------------------------------------------------------
    // Intent extras
    // -----------------------------------------------------------------------
    public static final String EXTRA_LANGUAGE_MODEL =
            "android.speech.extra.LANGUAGE_MODEL";
    public static final String EXTRA_LANGUAGE =
            "android.speech.extra.LANGUAGE";
    public static final String EXTRA_PROMPT =
            "android.speech.extra.PROMPT";
    public static final String EXTRA_MAX_RESULTS =
            "android.speech.extra.MAX_RESULTS";
    public static final String EXTRA_RESULTS =
            "android.speech.extra.RESULTS";

    // -----------------------------------------------------------------------
    // Language model values
    // -----------------------------------------------------------------------
    public static final String LANGUAGE_MODEL_FREE_FORM =
            "free_form";
    public static final String LANGUAGE_MODEL_WEB_SEARCH =
            "web_search";
}
