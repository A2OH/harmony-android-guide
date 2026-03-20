package android.text;

import java.lang.reflect.Array;

/**
 * Android-compatible PrecomputedText stub (API 28+).
 *
 * PrecomputedText pre-computes text measurement data so that final layout
 * can skip the heavy work. In this A2OH shim layer we store the source
 * text and parameters but defer real measurement to the ArkUI bridge at
 * render time.
 *
 * Implements {@link Spannable} so the result can be passed anywhere a
 * Spannable/Spanned/CharSequence is expected.
 */
public class PrecomputedText implements Spannable {

    private final CharSequence mText;
    private final Params mParams;

    // ── Inner class: Params ──────────────────────────────────────────

    /**
     * Parameters that control how text is pre-measured.
     * Use {@link Params.Builder} to create an instance.
     */
    public static final class Params {

        private final Object mPaint;           // TextPaint
        private final int mBreakStrategy;
        private final int mHyphenationFrequency;
        private final Object mTextDirection;

        private Params(Builder b) {
            mPaint = b.mPaint;
            mBreakStrategy = b.mBreakStrategy;
            mHyphenationFrequency = b.mHyphenationFrequency;
            mTextDirection = b.mTextDirection;
        }

        public Params(Object paint, Object textDirection, int breakStrategy, int hyphenationFrequency) {
            mPaint = paint;
            mTextDirection = textDirection;
            mBreakStrategy = breakStrategy;
            mHyphenationFrequency = hyphenationFrequency;
        }

        public static final int USABLE = 0;
        public static final int UNUSABLE = 1;
        public static final int NEED_RECOMPUTE = 2;

        public int checkResultUsable(TextPaint paint, Object textDirection, int breakStrategy, int hyphenationFrequency) {
            return USABLE;
        }

        @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.SOURCE)
        public @interface CheckResultUsableResult {}

        /** Returns the TextPaint used for measurement. */
        public TextPaint getTextPaint() { return (TextPaint) mPaint; }

        /** Returns the break strategy. */
        public int getBreakStrategy() { return mBreakStrategy; }

        /** Returns the hyphenation frequency. */
        public int getHyphenationFrequency() { return mHyphenationFrequency; }

        /** Returns the text direction heuristic. */
        public TextDirectionHeuristic getTextDirection() { return (TextDirectionHeuristic) mTextDirection; }

        // ── Builder ──────────────────────────────────────────────────

        /**
         * Builder for {@link Params}.
         */
        public static final class Builder {

            final Object mPaint;
            int mBreakStrategy;
            int mHyphenationFrequency;
            Object mTextDirection;

            /**
             * Creates a new Builder.
             *
             * @param pa(int the TextPaint (typed as Object to avoid hard
             *              dependency on TextPaint in compilation order)
             */
            public Builder(Object paint) {
                mPaint = paint;
            }

            /**
             * Sets the break strategy (e.g. Layout.BREAK_STRATEGY_SIMPLE).
             */
            public Builder setBreakStrategy(int strategy) {
                mBreakStrategy = strategy;
                return this;
            }

            /**
             * Sets the hyphenation frequency.
             */
            public Builder setHyphenationFrequency(int frequency) {
                mHyphenationFrequency = frequency;
                return this;
            }

            /**
             * Sets the text direction heuristic.
             */
            public Builder setTextDirection(Object textDir) {
                mTextDirection = textDir;
                return this;
            }

            /**
             * Builds an immutable {@link Params} instance.
             */
            public Params build() {
                return new Params(this);
            }
        }
    }

    // ── Construction ─────────────────────────────────────────────────

    private PrecomputedText(CharSequence text, Params params) {
        mText = text != null ? text : "";
        mParams = params;
    }

    /**
     * Creates a new PrecomputedText from the given text and params.
     * In a full implementation this would trigger measurement; the shim
     * simply stores the inputs for later bridge delegation.
     */
    public static PrecomputedText create(CharSequence text, Params params) {
        return new PrecomputedText(text, params);
    }

    /** Returns the parameters used to create this PrecomputedText. */
    public Params getParams() { return mParams; }

    // ── CharSequence ─────────────────────────────────────────────────

    @Override
    public int length() {
        return mText.length();
    }

    @Override
    public char charAt(int index) {
        return mText.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return mText.subSequence(start, end);
    }

    @Override
    public String toString() {
        return mText.toString();
    }

    // ── Spannable ────────────────────────────────────────────────────

    @Override
    public void setSpan(Object what, int start, int end, int flags) {
        // Stub — PrecomputedText is effectively immutable for spans in
        // the real Android implementation; silently ignored here.
    }

    @Override
    public void removeSpan(Object what) {
        // Stub — no-op.
    }

    // ── Spanned ──────────────────────────────────────────────────────

    @Override
    @SuppressWarnings("unchecked")
    public <Object> Object[] getSpans(int queryStart, int queryEnd, Class<Object> kind) {
        // If the underlying text is itself a Spanned, delegate.
        if (mText instanceof Spanned) {
            return ((Spanned) mText).getSpans(queryStart, queryEnd, kind);
        }
        return (Object[]) Array.newInstance(kind);
    }

    @Override
    public int getSpanStart(Object tag) {
        if (mText instanceof Spanned) {
            return ((Spanned) mText).getSpanStart(tag);
        }
        return -1;
    }

    @Override
    public int getSpanEnd(Object tag) {
        if (mText instanceof Spanned) {
            return ((Spanned) mText).getSpanEnd(tag);
        }
        return -1;
    }

    @Override
    public int getSpanFlags(Object tag) {
        if (mText instanceof Spanned) {
            return ((Spanned) mText).getSpanFlags(tag);
        }
        return 0;
    }

    @Override
    public int nextSpanTransition(int queryStart, int queryLimit, Class kind) {
        if (mText instanceof Spanned) {
            return ((Spanned) mText).nextSpanTransition(queryStart, queryLimit, kind);
        }
        return queryLimit;
    }

    // ---- ParagraphInfo ----
    public static class ParagraphInfo {
        public final int paragraphEnd;
        public final MeasuredParagraph measured;

        public ParagraphInfo(int end, MeasuredParagraph mp) {
            paragraphEnd = end;
            measured = mp;
        }
    }

    public ParagraphInfo[] getParagraphInfo() {
        ParagraphInfo[] info = new ParagraphInfo[1];
        info[0] = new ParagraphInfo(mText.length(),
            MeasuredParagraph.buildForBidi(mText, 0, mText.length(), null, null));
        return info;
    }

    public @Params.CheckResultUsableResult int checkResultUsable(int start, int end,
            TextDirectionHeuristic textDir, TextPaint paint, int breakStrategy,
            int hyphenationFrequency) {
        return Params.USABLE;
    }

    public static ParagraphInfo[] createMeasuredParagraphs(CharSequence text, Params params,
            int start, int end, boolean computeLayout) {
        ParagraphInfo[] info = new ParagraphInfo[1];
        info[0] = new ParagraphInfo(end,
            MeasuredParagraph.buildForBidi(text, start, end, null, null));
        return info;
    }

    public void getBounds(int start, int end, android.graphics.Rect bounds) {
        if (bounds != null) bounds.set(0, 0, 0, 0);
    }
}
