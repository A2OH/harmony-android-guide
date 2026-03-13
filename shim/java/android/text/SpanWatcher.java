package android.text;

/**
 * Android-compatible SpanWatcher shim.
 * When an object of this type is attached to a Spannable, its methods will
 * be called to notify it that other markup objects have been added, removed,
 * or changed.
 */
public interface SpanWatcher extends NoCopySpan {

    /**
     * Called when a new markup object has been attached to the specified range
     * of the text.
     *
     * @param text  the text object that was modified
     * @param what  the span object that was added
     * @param start the start of the range
     * @param end   the end of the range
     */
    void onSpanAdded(Spannable text, Object what, int start, int end);

    /**
     * Called when a markup object has been removed from the specified range
     * of the text.
     *
     * @param text  the text object that was modified
     * @param what  the span object that was removed
     * @param start the start of the previous range
     * @param end   the end of the previous range
     */
    void onSpanRemoved(Spannable text, Object what, int start, int end);

    /**
     * Called when a markup object that was previously attached to the
     * specified range has been repositioned to a new range.
     *
     * @param text     the text object that was modified
     * @param what     the span object that was changed
     * @param ostart   the old start of the range
     * @param oend     the old end of the range
     * @param nstart   the new start of the range
     * @param nend     the new end of the range
     */
    void onSpanChanged(Spannable text, Object what, int ostart, int oend,
                       int nstart, int nend);
}
