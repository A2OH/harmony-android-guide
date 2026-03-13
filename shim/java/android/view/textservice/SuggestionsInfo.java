package android.view.textservice;

/**
 * Android-compatible SuggestionsInfo shim. Data holder — no spell-check engine backing.
 */
public class SuggestionsInfo {

    public static final int RESULT_ATTR_IN_THE_DICTIONARY = 1;
    public static final int RESULT_ATTR_LOOKS_LIKE_TYPO   = 2;

    private final int      attrs;
    private final String[] suggestions;

    public SuggestionsInfo(int suggestionsAttributes, String[] suggestions) {
        this.attrs       = suggestionsAttributes;
        this.suggestions = suggestions != null ? suggestions : new String[0];
    }

    public int getAttrs() { return attrs; }

    public int getSuggestionsCount() { return suggestions.length; }

    public String getSuggestionAt(int i) {
        if (i < 0 || i >= suggestions.length) return "";
        return suggestions[i];
    }
}
