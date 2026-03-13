package android.text;

/**
 * Interface for objects that use a heuristic for guessing at the paragraph direction
 * by examining text.
 *
 * <p>A2OH shim — stub interface for Android compatibility.</p>
 */
public interface TextDirectionHeuristic {

    /**
     * Guess if a {@link CharSequence} is in the RTL direction or not.
     *
     * @param cs   the CharSequence to check.
     * @param start start index (inclusive).
     * @param count the number of characters to check.
     * @return true if the range of text is RTL, false otherwise.
     */
    boolean isRtl(CharSequence cs, int start, int count);

    /**
     * Guess if a {@code char} array is in the RTL direction or not.
     *
     * @param array the char array to check.
     * @param start start index (inclusive).
     * @param count the number of characters to check.
     * @return true if the range of text is RTL, false otherwise.
     */
    boolean isRtl(char[] array, int start, int count);
}
