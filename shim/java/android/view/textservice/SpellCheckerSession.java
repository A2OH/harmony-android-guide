package android.view.textservice;

/**
 * Android-compatible SpellCheckerSession shim. All operations are no-ops;
 * no spell-check engine is present on OpenHarmony.
 */
public class SpellCheckerSession {

    /**
     * Listener interface for asynchronous spell-check results.
     */
    public interface SpellCheckerSessionListener {
        void onGetSuggestions(SuggestionsInfo[] results);
        void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results);
    }

    private boolean closed = false;

    /**
     * Requests sentence-level suggestions. The listener is never called in this shim.
     */
    public void getSentenceSuggestions(Object[] textInfos, int suggestionsLimit) {
        // no-op stub
    }

    /** Cancels any pending requests. No-op in shim. */
    public void cancel() {}

    /** Closes the session. Marks this session as disconnected. */
    public void close() {
        closed = true;
    }

    /** Returns true after {@link #close()} has been called. */
    public boolean isSessionDisconnected() {
        return closed;
    }
}
