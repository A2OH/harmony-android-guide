package android.text;

/**
 * Shim: android.text.Spanned
 *
 * Interface for text to which markup objects can be attached. Not all
 * text classes have mutable markup; this read-only interface lets callers
 * query which spans are attached and their extents.
 */
public interface Spanned extends CharSequence {

    /** Flag: span starts at the start of the range. */
    int SPAN_INCLUSIVE_INCLUSIVE = 0x00000011;
    /** Flag: span starts at the start but ends before the end. */
    int SPAN_INCLUSIVE_EXCLUSIVE = 0x00000010;
    /** Flag: span starts after the start and ends at the end. */
    int SPAN_EXCLUSIVE_INCLUSIVE = 0x00000001;
    /** Flag: span is excluded at both endpoints (most common default). */
    int SPAN_EXCLUSIVE_EXCLUSIVE = 0x00000000;

    /** Used to mark the start of a paragraph. */
    int SPAN_PARAGRAPH = 0x00000033;

    /** Flag to indicate the span covers the entire text. */
    int SPAN_MARK_MARK    = 0x11;
    int SPAN_MARK_POINT   = 0x12;
    int SPAN_POINT_MARK   = 0x21;
    int SPAN_POINT_POINT  = 0x22;
    int SPAN_PRIORITY_SHIFT = 16;
    int SPAN_PRIORITY = 0x00FF0000;
    int SPAN_INTERMEDIATE = 0x00000200;
    int SPAN_USER_SHIFT = 24;
    int SPAN_USER = 0xFF000000;
    int SPAN_COMPOSING = 0x100;

    /**
     * Returns all spans of the specified type attached to this text between
     * {@code queryStart} (inclusive) and {@code queryEnd} (exclusive).
     */
    <Object> Object[] getSpans(int queryStart, int queryEnd, Class<Object> kind);

    /** Returns the start of the range to which {@code tag} is attached, or -1. */
    int getSpanStart(Object tag);

    /** Returns the end of the range to which {@code tag} is attached, or -1. */
    int getSpanEnd(Object tag);

    /** Returns the flags of the span. */
    int getSpanFlags(Object tag);

    /**
     * Returns the first offset >= {@code queryStart} where a span of type
     * {@code kind} begins or ends, or {@code queryLimit} if there is none.
     */
    int nextSpanTransition(int queryStart, int queryLimit, Object kind);
}
