package android.text;

import java.lang.reflect.Array;

/**
 * Stub: SpanSet — cached array of spans for efficient layout.
 * Used by Layout.java for span iteration during drawing.
 */
class SpanSet<E> {
    int numberOfSpans;
    E[] spans;
    int[] spanStarts;
    int[] spanEnds;
    int[] spanFlags;

    private Class<? extends E> classType;

    SpanSet(Class<? extends E> type) {
        classType = type;
        numberOfSpans = 0;
    }

    @SuppressWarnings("unchecked")
    void init(Spanned spanned, int start, int limit) {
        E[] allSpans = spanned.getSpans(start, limit, classType);
        int length = allSpans.length;
        if (length > 0 && (spans == null || spans.length < length)) {
            spans = (E[]) Array.newInstance(classType, length);
            spanStarts = new int[length];
            spanEnds = new int[length];
            spanFlags = new int[length];
        }
        int count = 0;
        for (int i = 0; i < length; i++) {
            E span = allSpans[i];
            int spanStart = spanned.getSpanStart(span);
            int spanEnd = spanned.getSpanEnd(span);
            if (spanStart == spanEnd) continue;
            int spanFlag = spanned.getSpanFlags(span);
            spans[count] = span;
            spanStarts[count] = spanStart;
            spanEnds[count] = spanEnd;
            spanFlags[count] = spanFlag;
            count++;
        }
        numberOfSpans = count;
    }

    boolean hasSpansIntersecting(int start, int end) {
        for (int i = 0; i < numberOfSpans; i++) {
            if (spanStarts[i] < end && spanEnds[i] > start) {
                return true;
            }
        }
        return false;
    }

    int getNextTransition(int start, int limit) {
        for (int i = 0; i < numberOfSpans; i++) {
            int spanStart = spanStarts[i];
            int spanEnd = spanEnds[i];
            if (spanStart > start && spanStart < limit) limit = spanStart;
            if (spanEnd > start && spanEnd < limit) limit = spanEnd;
        }
        return limit;
    }

    void recycle() {
        numberOfSpans = 0;
    }
}
