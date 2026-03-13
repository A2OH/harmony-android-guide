package android.widget;

/**
 * Android-compatible MultiAutoCompleteTextView shim.
 * An editable TextView that can show completion suggestions for a substring
 * of its text (rather than just the whole thing). The user can select one or
 * more tokens from the suggestions.
 */
public class MultiAutoCompleteTextView extends AutoCompleteTextView {
    private Tokenizer tokenizer;

    public MultiAutoCompleteTextView() {
        super();
    }

    /**
     * Sets the Tokenizer that will be used to determine the relevant portion
     * of the text where the user is typing.
     */
    public void setTokenizer(Tokenizer t) {
        this.tokenizer = t;
    }

    /**
     * Returns the current Tokenizer.
     */
    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    /**
     * Interface for splitting a CharSequence into tokens and reconstituting
     * them (e.g. for multi-recipient email address fields).
     */
    public interface Tokenizer {
        /**
         * Returns the start of the token that ends at offset {@code cursor} in
         * {@code text}.
         */
        int findTokenStart(CharSequence text, int cursor);

        /**
         * Returns the end of the token (not including any delimiter) that
         * begins at offset {@code cursor} in {@code text}.
         */
        int findTokenEnd(CharSequence text, int cursor);

        /**
         * Returns {@code text}, modified (if necessary) to ensure that it ends
         * with a delimiter (e.g. a comma-space for email).
         */
        CharSequence terminateToken(CharSequence text);
    }
}
