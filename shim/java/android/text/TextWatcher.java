package android.text;

/**
 * Shim: android.text.TextWatcher
 *
 * When an object of a type implementing this interface is attached to an
 * Editable, its methods will be called when the text changes.
 */
public interface TextWatcher {

    /**
     * Called before the text in {@code s} is changed. The characters
     * {@code [start, start+count)} are about to be replaced with new text
     * of length {@code after}.
     *
     * @param s     the text that is about to change
     * @param start start index of the section about to change
     * @param count number of characters about to be removed
     * @param after number of characters that will replace them
     */
    void beforeTextChanged(CharSequence s, int start, int count, int after);

    /**
     * Called after the text in {@code s} has changed. The characters
     * {@code [start, start+count)} have just replaced old text of length
     * {@code before}.
     *
     * @param s      the text after the change
     * @param start  start index of the changed section
     * @param before number of characters that were replaced
     * @param count  number of new characters in the changed section
     */
    void onTextChanged(CharSequence s, int start, int before, int count);

    /**
     * Called when the text has changed. {@code s} is now the text that was
     * changed.
     *
     * @param s the editable that was changed
     */
    void afterTextChanged(Editable s);
}
