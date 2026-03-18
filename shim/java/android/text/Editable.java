package android.text;

/**
 * Shim: android.text.Editable
 *
 * This is the interface for an object whose text content and markup can
 * both be changed. It extends {@link Spannable} (for markup mutation) and
 * {@link Appendable} (for appending characters/sequences).
 */
public interface Editable extends Spannable, Appendable {

    /**
     * Replaces the specified range {@code [st, en)} with the characters
     * in {@code source}.
     */
    Editable replace(int st, int en, CharSequence source);

    /**
     * Replaces the specified range {@code [st, en)} with the characters
     * {@code [start, end)} from {@code source}.
     */
    Editable replace(int st, int en, CharSequence source, int start, int end);

    /**
     * Inserts {@code text} at offset {@code where}.
     */
    Editable insert(int where, CharSequence text);

    /**
     * Inserts the range {@code [start, end)} of {@code text} at offset
     * {@code where}.
     */
    Editable insert(int where, CharSequence text, int start, int end);

    /**
     * Deletes the characters in the specified range {@code [st, en)}.
     */
    Editable delete(int st, int en);

    // Appendable methods return Editable for chaining
    Editable append(CharSequence text);
    Editable append(CharSequence text, int start, int end);
    Editable append(char text);

    /**
     * Clears the contents of this Editable.
     */
    void clear();

    /**
     * Clears all spans from this Editable.
     */
    void clearSpans();

    /**
     * Sets the filter array. Each filter is called in order before any
     * text change is committed.
     */
    void setFilters(android.text.InputFilter[] filters);

    /**
     * Returns the current filter array.
     */
    android.text.InputFilter[] getFilters();

    public static class Factory {
        private static final Factory sInstance = new Factory();
        public static Factory getInstance() { return sInstance; }
        public Editable newEditable(CharSequence source) {
            return new SpannableStringBuilder(source != null ? source : "");
        }
    }
}
