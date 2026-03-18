package android.text;

/**
 * Shim: android.text.Spannable
 *
 * This is the interface for text whose markup can be changed.
 * It extends {@link Spanned} with mutation methods: setSpan and removeSpan.
 */
public interface Spannable extends Spanned {

    /**
     * Attaches the specified object to the range {@code start}…{@code end}
     * of the text.
     *
     * @param what  the span object to attach
     * @param start start of the range (inclusive based on flags)
     * @param end   end of the range (inclusive based on flags)
     * @param flags one of the SPAN_* constants from {@link Spanned}
     */
    void setSpan(Object what, int start, int end, int flags);

    /**
     * Removes the specified object from the range to which it was attached,
     * if any.
     *
     * @param what the span object to remove
     */
    void removeSpan(Object what);

    /** Auto-generated stub. */
    public static interface Factory {}
}
