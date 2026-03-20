package android.text;
import android.view.View;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.text.SpannableString
 *
 * An immutable string that has been annotated with markup (spans). The
 * underlying string content cannot be changed after construction, but
 * spans can be added and removed freely via the {@link Spannable}
 * interface.
 *
 * Bridge delegation: pure Java implementation — no OHBridge calls needed
 * because span storage is a local data structure. Text display/rendering
 * that actually uses spans would go through the View/ArkUI bridge.
 */
public class SpannableString implements Spannable {

    private final String mText;

    // Each span entry: {what, start, end, flags}
    private static final class SpanRec {
        final Object what;
        int start;
        int end;
        int flags;
        SpanRec(Object what, int start, int end, int flags) {
            this.what = what; this.start = start; this.end = end; this.flags = flags;
        }
    }

    private final List<SpanRec> mSpans = new ArrayList<>();

    public SpannableString(CharSequence source) {
        mText = source != null ? source.toString() : "";
    }

    // ── CharSequence ─────────────────────────────────────────────────

    @Override public int length() { return mText.length(); }
    @Override public char charAt(int index) { return mText.charAt(index); }
    @Override public CharSequence subSequence(int start, int end) {
        return new SpannableString(mText.substring(start, end));
    }
    @Override public String toString() { return mText; }

    // ── Spannable ────────────────────────────────────────────────────

    @Override
    public void setSpan(Object what, int start, int end, int flags) {
        if (start < 0 || end < start || end > mText.length()) {
            throw new IndexOutOfBoundsException("setSpan(" + start + ", " + end
                    + ") on length " + mText.length());
        }
        // Remove any existing entry for the same object
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

    // ── Factory ──────────────────────────────────────────────────────

    /** Convenience factory mirroring Android's static valueOf(). */
    public static SpannableString valueOf(CharSequence source) {
        if (source instanceof SpannableString) return (SpannableString) source;
        return new SpannableString(source);
    }
}
