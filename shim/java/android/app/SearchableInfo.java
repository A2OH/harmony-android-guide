package android.app;

/**
 * Android-compatible SearchableInfo shim.
 * Provides metadata about a searchable activity, including
 * its suggestion content provider configuration.
 * Stub implementation — returns sensible defaults.
 */
public class SearchableInfo {

    /** Returns the authority of the suggestions content provider, or null. */
    public String getSuggestAuthority() {
        return null;
    }

    /** Returns the path within the suggestions provider, or null. */
    public String getSuggestPath() {
        return null;
    }

    /** Returns the selection clause for suggestion queries, or null. */
    public String getSuggestSelection() {
        return null;
    }

    /** Returns the resource id for the label of the searchable activity. */
    public int getLabelId() {
        return 0;
    }

    /** Returns the resource id for the hint text. */
    public int getHintId() {
        return 0;
    }

    /** Returns the resource id for the search icon. */
    public int getIconId() {
        return 0;
    }

    /** Returns the suggest threshold (minimum number of characters before suggestions). */
    public int getSuggestThreshold() {
        return 0;
    }

    /** Returns whether voice search is enabled for this searchable. */
    public boolean getVoiceSearchEnabled() {
        return false;
    }

    /** Returns whether voice search should launch a web search. */
    public boolean getVoiceSearchLaunchWebSearch() {
        return false;
    }

    /** Returns whether voice search should launch the recognizer. */
    public boolean getVoiceSearchLaunchRecognizer() {
        return false;
    }

    @Override
    public String toString() {
        return "SearchableInfo{authority=" + getSuggestAuthority() + "}";
    }
}
