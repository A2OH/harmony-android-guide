package android.view.textservice;

/**
 * Android-compatible TextInfo shim.
 * Wraps a {@link CharSequence} (or plain {@link String}) for submission to a
 * spell-checker session. All methods return safe defaults.
 */
public class TextInfo {

    private final CharSequence mCharSequence;
    private final int mCookie;
    private final int mSequenceNumber;

    /**
     * Creates a TextInfo from a plain string.
     *
     * @param charSequence the text to check; must not be null
     */
    public TextInfo(String charSequence) {
        this(charSequence, 0, 0);
    }

    /**
     * Creates a TextInfo with cookie and sequence number tracking.
     *
     * @param charSequence   the text to check; must not be null
     * @param cookie         caller-defined cookie for result matching
     * @param sequenceNumber caller-defined sequence number for ordering
     */
    public TextInfo(String charSequence, int cookie, int sequenceNumber) {
        if (charSequence == null) throw new NullPointerException("charSequence must not be null");
        mCharSequence   = charSequence;
        mCookie         = cookie;
        mSequenceNumber = sequenceNumber;
    }

    /**
     * Creates a TextInfo from a {@link CharSequence} with optional sub-range.
     *
     * @param charSequence the text; must not be null
     * @param start        start offset (inclusive) within the CharSequence
     * @param end          end offset (exclusive) within the CharSequence
     * @param cookie       caller-defined cookie
     * @param sequenceNumber caller-defined sequence number
     */
    public TextInfo(CharSequence charSequence, int start, int end,
                    int cookie, int sequenceNumber) {
        if (charSequence == null) throw new NullPointerException("charSequence must not be null");
        mCharSequence   = charSequence.subSequence(start, end);
        mCookie         = cookie;
        mSequenceNumber = sequenceNumber;
    }

    // ----- Accessors -----

    /**
     * Returns the text as a {@link String}.
     */
    public String getText() {
        return mCharSequence.toString();
    }

    /**
     * Returns the text as a {@link CharSequence}.
     */
    public CharSequence getCharSequence() {
        return mCharSequence;
    }

    /**
     * Returns the length of the wrapped CharSequence.
     */
    public int getCharSequenceLength() {
        return mCharSequence.length();
    }

    /**
     * Returns the caller-defined cookie, useful for matching results to requests.
     */
    public int getCookie() {
        return mCookie;
    }

    /**
     * Returns the caller-defined sequence number.
     */
    public int getSequenceNumber() {
        return mSequenceNumber;
    }

    @Override
    public String toString() {
        return "TextInfo{text=\"" + getText() + "\", cookie=" + mCookie
                + ", seq=" + mSequenceNumber + "}";
    }
}
