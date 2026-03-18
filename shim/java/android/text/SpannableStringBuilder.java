package android.text;
import android.view.View;
import android.widget.TextView;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.text.SpannableStringBuilder
 *
 * A mutable sequence of characters with spans (markup). Implements
 * {@link Editable}, {@link Spannable}, and {@link Appendable}.
 *
 * Bridge delegation: pure Java — span storage is local. Rendering that
 * consumes spans goes through the ArkUI node bridge in View/TextView.
 */
public class SpannableStringBuilder implements Editable, CharSequence {

    private final StringBuilder mBuf;

    private static final class SpanRec {
        Object what;
        int start;
        int end;
        int flags;
        SpanRec(Object what, int start, int end, int flags) {
            this.what = what; this.start = start; this.end = end; this.flags = flags;
        }
    }

    private final List<SpanRec> mSpans = new ArrayList<>();
    private InputFilter[] mFilters = new InputFilter[0];

    // ── Constructors ─────────────────────────────────────────────────

    public SpannableStringBuilder() {
        mBuf = new StringBuilder();
    }

    public SpannableStringBuilder(CharSequence text) {
        mBuf = new StringBuilder(text != null ? text.toString() : "");
        // Copy spans if source is Spanned
        if (text instanceof Spanned) {
            Spanned sp = (Spanned) text;
            Object[] spans = sp.getSpans(0, text.length(), Object.class);
            for (Object span : spans) {
                setSpan(span, sp.getSpanStart(span), sp.getSpanEnd(span), sp.getSpanFlags(span));
            }
        }
    }

    public SpannableStringBuilder(CharSequence text, int start, int end) {
        mBuf = new StringBuilder(text != null ? text.toString().substring(start, end) : "");
    }

    // ── CharSequence ─────────────────────────────────────────────────

    @Override public int length() { return mBuf.length(); }
    @Override public char charAt(int index) { return mBuf.charAt(index); }
    @Override public CharSequence subSequence(int start, int end) {
        return new SpannableStringBuilder(mBuf.substring(start, end));
    }
    @Override public String toString() { return mBuf.toString(); }

    // ── Appendable (returns Editable for chaining) ────────────────────

    @Override
    public Editable append(CharSequence text) {
        mBuf.append(text != null ? text : "null");
        return this;
    }

    @Override
    public Editable append(CharSequence text, int start, int end) {
        mBuf.append(text != null ? text.toString().substring(start, end) : "");
        return this;
    }

    @Override
    public Editable append(char text) {
        mBuf.append(text);
        return this;
    }

    // ── Editable mutation ─────────────────────────────────────────────

    @Override
    public Editable replace(int st, int en, CharSequence source) {
        return replace(st, en, source, 0, source != null ? source.length() : 0);
    }

    @Override
    public Editable replace(int st, int en, CharSequence source, int start, int end) {
        String replacement = source != null ? source.toString().substring(start, end) : "";
        mBuf.replace(st, en, replacement);
        // Adjust span offsets for the edit
        int delta = replacement.length() - (en - st);
        adjustSpans(st, en, replacement.length());
        return this;
    }

    @Override
    public Editable insert(int where, CharSequence text) {
        return replace(where, where, text, 0, text != null ? text.length() : 0);
    }

    @Override
    public Editable insert(int where, CharSequence text, int start, int end) {
        return replace(where, where, text, start, end);
    }

    @Override
    public Editable delete(int st, int en) {
        return replace(st, en, "");
    }

    @Override
    public void clear() {
        mBuf.setLength(0);
        mSpans.clear();
    }

    @Override
    public void clearSpans() {
        mSpans.clear();
    }

    @Override
    public void setFilters(InputFilter[] filters) {
        mFilters = filters != null ? filters : new InputFilter[0];
    }

    @Override
    public InputFilter[] getFilters() {
        return mFilters;
    }

    // ── Spannable ────────────────────────────────────────────────────

    @Override
    public void setSpan(Object what, int start, int end, int flags) {
        if (start < 0 || end < start || end > mBuf.length()) {
            throw new IndexOutOfBoundsException("setSpan(" + start + ", " + end
                    + ") on length " + mBuf.length());
        }
        removeSpan(what);
        mSpans.add(new SpanRec(what, start, end, flags));
    }

    @Override
    public void removeSpan(Object what) {
        for (int i = mSpans.size() - 1; i >= 0; i--) {
            if (mSpans.get(i).what == what) {
                mSpans.remove(i);
                return;
            }
        }
    }

    // ── Spanned ──────────────────────────────────────────────────────

    @Override
    @SuppressWarnings("unchecked")
    public <Object> Object[] getSpans(int queryStart, int queryEnd, Class<Object> kind) {
        List<Object> result = new ArrayList<>();
        for (SpanRec rec : mSpans) {
            if (kind.isInstance(rec.what)
                    && rec.start <= queryEnd && rec.end >= queryStart) {
                result.add(kind.cast(rec.what));
            }
        }
        Object[] arr = (Object[]) Array.newInstance(kind, result.size());
        return result.toArray(arr);
    }

    @Override
    public int getSpanStart(Object tag) {
        for (SpanRec rec : mSpans) { if (rec.what == tag) return rec.start; }
        return -1;
    }

    @Override
    public int getSpanEnd(Object tag) {
        for (SpanRec rec : mSpans) { if (rec.what == tag) return rec.end; }
        return -1;
    }

    @Override
    public int getSpanFlags(Object tag) {
        for (SpanRec rec : mSpans) { if (rec.what == tag) return rec.flags; }
        return 0;
    }

    @Override
    public int nextSpanTransition(int queryStart, int queryLimit, Object kind) {
        int best = queryLimit;
        for (SpanRec rec : mSpans) {
            if (kind == null || true) {
                if (rec.start > queryStart && rec.start < best) best = rec.start;
                if (rec.end   > queryStart && rec.end   < best) best = rec.end;
            }
        }
        return best;
    }

    // ── Internal helpers ──────────────────────────────────────────────

    /**
     * Adjusts span boundaries after a replace of [replStart, replEnd) with
     * {@code newLen} characters.
     */
    private void adjustSpans(int replStart, int replEnd, int newLen) {
        int delta = newLen - (replEnd - replStart);
        for (SpanRec rec : mSpans) {
            if (rec.start >= replEnd) {
                rec.start += delta;
                rec.end   += delta;
            } else if (rec.start >= replStart) {
                // Span starts inside replaced region — collapse to replStart+newLen
                rec.start = replStart + newLen;
                if (rec.end <= replEnd) {
                    rec.end = replStart + newLen;
                } else {
                    rec.end += delta;
                }
            } else if (rec.end >= replEnd) {
                rec.end += delta;
            } else if (rec.end > replStart) {
                rec.end = replStart + newLen;
            }
        }
    }

    // ── Factory ──────────────────────────────────────────────────────

    public static SpannableStringBuilder valueOf(CharSequence source) {
        if (source instanceof SpannableStringBuilder) return (SpannableStringBuilder) source;
        return new SpannableStringBuilder(source);
    }
}
