package android.service.textservice;

import android.app.Service;

/**
 * Android-compatible SpellCheckerService shim. Stub for text spell-checker service.
 */
public abstract class SpellCheckerService extends Service {

    /**
     * Factory method called by the framework to obtain a new spell-checker session.
     *
     * @return a new {@link Session} instance
     */
    public abstract Session createSession();

    /**
     * Abstract base for a single spell-checking session.
     */
    public abstract static class Session {

        /**
         * Called when the session is created and ready to accept requests.
         */
        public abstract void onCreate();

        /**
         * Called to spell-check a single word.
         *
         * @param textInfo  the text to check (TextInfo in real API; Object here)
         * @param suggestionsLimit  maximum number of suggestions to return
         * @return suggestions result (SuggestionsInfo in real API; Object here)
         */
        public abstract Object onGetSuggestions(Object textInfo, int suggestionsLimit);

        /**
         * Called to spell-check a sentence (multiple words).
         *
         * @param textInfos  array of text segments to check
         * @param suggestionsLimit  maximum suggestions per word
         * @return array of sentence suggestion results (SentenceSuggestionsInfo[] in real API)
         */
        public abstract Object[] onGetSentenceSuggestions(Object[] textInfos, int suggestionsLimit);

        /**
         * Called to cancel any outstanding spell-checking requests for this session.
         */
        public abstract void onCancel();
    }
}
