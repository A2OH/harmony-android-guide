package android.text;

/**
 * Android-compatible Selection shim.
 * Utility class for manipulating text selection spans in a Spannable.
 */
public class Selection {

    private Selection() {} // non-instantiable

    /** Span object used to mark the selection start. */
    public static final Object SELECTION_START = new Object();

    /** Span object used to mark the selection end. */
    public static final Object SELECTION_END = new Object();

    /**
     * Returns the offset of the selection start in the buffer, or -1
     * if there is no selection or cursor.
     */
    public static int getSelectionStart(CharSequence text) {
        if (text instanceof Spannable) {
            return ((Spannable) text).getSpanStart(SELECTION_START);
        }
        return -1;
    }

    /**
     * Returns the offset of the selection end in the buffer, or -1
     * if there is no selection or cursor.
     */
    public static int getSelectionEnd(CharSequence text) {
        if (text instanceof Spannable) {
            return ((Spannable) text).getSpanStart(SELECTION_END);
        }
        return -1;
    }

    /**
     * Sets the selection range. If start == stop, it places the cursor.
     */
    public static void setSelection(Spannable text, int start, int stop) {
        text.setSpan(SELECTION_START, start, start, Spanned.SPAN_POINT_POINT);
        text.setSpan(SELECTION_END, stop, stop, Spanned.SPAN_POINT_POINT);
    }

    /**
     * Moves the cursor to the specified offset.
     */
    public static void setSelection(Spannable text, int index) {
        setSelection(text, index, index);
    }

    /**
     * Removes the selection or cursor from the text.
     */
    public static void removeSelection(Spannable text) {
        text.removeSpan(SELECTION_START);
        text.removeSpan(SELECTION_END);
    }

    /**
     * Selects the entire contents of the buffer.
     */
    public static void selectAll(Spannable text) {
        setSelection(text, 0, text.length());
    }

    /**
     * Moves the selection end to the specified offset, keeping the
     * selection start where it is.
     */
    public static void extendSelection(Spannable text, int index) {
        text.setSpan(SELECTION_END, index, index, Spanned.SPAN_POINT_POINT);
    }
}
