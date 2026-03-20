package android.text;
import android.view.View;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.text.SpannedString
 *
 * An immutable {@link Spanned} snapshot. Once constructed from a source
 * {@link Spanned} (or plain {@link CharSequence}), neither the text content
 * nor the attached spans can be modified.
 *
 * This is the read-only counterpart of {@link SpannableString}: callers who
 * need a stable, unchanging copy of a spanned text use SpannedString.
 *
 * Bridge delegation: pure Java — span storage is a local data structure.
 * No OHBridge calls are required because span metadata is not surfaced
 * directly to ArkUI; only the rendered output (handled by the View bridge)
 * consumes spans.
 */
public final class SpannedString implements Spanned {

    private final String mText;

    /** Internal span record. Immutable after construction. */
    private static final class SpanRec {
        final Object what;
        final int start;
        final int end;
        final int flags;

        SpanRec(Object what, int start, int end, int flags) {
            this.what  = what;
            this.start = start;
            this.end   = end;
            this.flags = flags;
        }
    }

    private final SpanRec[] mSpans;

    // ── Constructors ──────────────────────────────────────────────────

    /**
     * Creates a SpannedString that is a copy of the given {@link Spanned},
     * preserving all spans.
     *
     * @param source the source spanned text; must not be {@code null}
     */
    public SpannedString(Spanned source) {
        mText = source.toString();
        Object[] spans = source.getSpans(0, source.length(), Object.class);
        mSpans = new SpanRec[spans.length];
        for (int i = 0; i < spans.length; i++) {
            Object span = spans[i];
            mSpans[i] = new SpanRec(
                    span,
                    source.getSpanStart(span),
                    source.getSpanEnd(span),
                    source.getSpanFlags(span));
        }
    }

    /**
     * Creates a SpannedString from a plain {@link CharSequence}.
     * If the source is already a {@link Spanned}, spans are copied; otherwise
     * the resulting SpannedString has no spans.
     *
     * @param source the source text; {@code null} is treated as an empty string
     */
    public SpannedString(CharSequence source) {
        if (source == null) {
            mText  = "";
            mSpans = new SpanRec[0];
        } else if (source instanceof Spanned) {
            // Delegate to the Spanned constructor via a local copy
            Spanned sp = (Spanned) source;
            mText = sp.toString();
            Object[] spans = sp.getSpans(0, sp.length(), Object.class);
            mSpans = new SpanRec[spans.length];
            for (int i = 0; i < spans.length; i++) {
                Object span = spans[i];
                mSpans[i] = new SpanRec(
                        span,
                        sp.getSpanStart(span),
                        sp.getSpanEnd(span),
                        sp.getSpanFlags(span));
            }
        } else {
            mText  = source.toString();
            mSpans = new SpanRec[0];
        }
    }

    // ── Factory ───────────────────────────────────────────────────────

    /**
     * Returns a SpannedString for the given CharSequence, avoiding a copy if
     * the source is already a SpannedString.
     *
     * @param source the source text
     * @return a SpannedString equal to {@code source}
     */
    public static SpannedString valueOf(CharSequence source) {
        if (source instanceof SpannedString) return (SpannedString) source;
        return new SpannedString(source);
    }

    // ── CharSequence ──────────────────────────────────────────────────

    @Override
    public int length() { return mText.length(); }

    @Override
    public char charAt(int index) { return mText.charAt(index); }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new SpannedString(mText.substring(start, end));
    }

    @Override
    public String toString() { return mText; }

    // ── Spanned ───────────────────────────────────────────────────────

    @Override
    @SuppressWarnings("unchecked")
    public <Object> Object[] getSpans(int queryStart, int queryEnd, Class<Object> kind) {
        List<Object> result = new ArrayList<>();
        for (SpanRec rec : mSpans) {
            if (kind.isInstance(rec.what)
                    && rec.start <= queryEnd
                    && rec.end   >= queryStart) {
                result.add(kind.cast(rec.what));
            }
        }
        Object[] arr = (Object[]) Array.newInstance(kind, result.size());
        return result.toArray(arr);
    }

    @Override
    public int getSpanStart(Object tag) {
        for (SpanRec rec : mSpans) {
            if (rec.what == tag) return rec.start;
        }
        return -1;
    }

    @Override
    public int getSpanEnd(Object tag) {
        for (SpanRec rec : mSpans) {
            if (rec.what == tag) return rec.end;
        }
        return -1;
    }

    @Override
    public int getSpanFlags(Object tag) {
        for (SpanRec rec : mSpans) {
            if (rec.what == tag) return rec.flags;
        }
        return 0;
    }

    @Override
    public int nextSpanTransition(int queryStart, int queryLimit, Class kind) {
        int best = queryLimit;
        for (SpanRec rec : mSpans) {
            if (kind == null || true) {
                if (rec.start > queryStart && rec.start < best) best = rec.start;
                if (rec.end   > queryStart && rec.end   < best) best = rec.end;
            }
        }
        return best;
    }
}
