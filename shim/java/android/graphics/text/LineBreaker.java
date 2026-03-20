package android.graphics.text;

/**
 * Android-compatible LineBreaker shim (API 29+).
 * Computes line-break positions for styled text; stubbed for A2OH migration.
 */
public class LineBreaker {

    // ── Break strategy constants ──────────────────────────────────────
    public static final int BREAK_STRATEGY_SIMPLE       = 0;
    public static final int BREAK_STRATEGY_HIGH_QUALITY = 1;
    public static final int BREAK_STRATEGY_BALANCED     = 2;

    // ── Hyphenation frequency constants ───────────────────────────────
    public static final int HYPHENATION_FREQUENCY_NONE   = 0;
    public static final int HYPHENATION_FREQUENCY_NORMAL = 1;
    public static final int HYPHENATION_FREQUENCY_FULL   = 2;

    // ── Justification mode constants ──────────────────────────────────
    public static final int JUSTIFICATION_MODE_NONE       = 0;
    public static final int JUSTIFICATION_MODE_INTER_WORD = 1;

    private int mBreakStrategy;
    private int mHyphenationFrequency;
    private int mJustificationMode;

    private LineBreaker(int breakStrategy, int hyphenationFrequency,
                        int justificationMode) {
        mBreakStrategy       = breakStrategy;
        mHyphenationFrequency = hyphenationFrequency;
        mJustificationMode   = justificationMode;
    }

    /**
     * Compute line breaks for the given measured text.
     * Stub: always returns {@code null}.
     */
    public Result computeLineBreaks(Object measuredText,
                                    Object constraints,
                                    int lineNumber) {
        return new Result();
    }

    // ── Result ────────────────────────────────────────────────────────

    /**
     * Holds the result of a line-break computation.
     */
    public static class Result {

        Result() {}

        public int getLineCount() { return 0; }

        public int getLineBreakOffset(int lineIndex) { return 0; }

        public float getLineWidth(int lineIndex) { return 0.0f; }

        public float getLineAscent(int lineIndex) { return 0.0f; }

        public float getLineDescent(int lineIndex) { return 0.0f; }

        public boolean hasLineTab(int lineIndex) { return false; }

        public int getLineHyphenEdit(int lineIndex) { return 0; }
        public int getStartLineHyphenEdit(int lineIndex) { return 0; }
        public int getEndLineHyphenEdit(int lineIndex) { return 0; }
    }

    // ── ParagraphConstraints ──────────────────────────────────────────

    /**
     * Describes width constraints for a paragraph.
     */
    public static class ParagraphConstraints {

        private float mWidth;
        private float mFirstWidth;
        private int   mFirstWidthLineCount;
        private float[] mTabStops;

        public ParagraphConstraints() {}

        public float getWidth() { return mWidth; }

        public float getFirstWidth() { return mFirstWidth; }

        public int getFirstWidthLineCount() { return mFirstWidthLineCount; }

        public float[] getTabStops() { return mTabStops; }

        public void setWidth(float width) { mWidth = width; }

        public void setFirstWidth(float firstWidth) { mFirstWidth = firstWidth; }

        public void setFirstWidthLineCount(int firstWidthLineCount) {
            mFirstWidthLineCount = firstWidthLineCount;
        }

        public void setTabStops(float[] tabStops) { mTabStops = tabStops; }
        public void setTabStops(float[] tabStops, float defaultTab) { mTabStops = tabStops; }

        public void setIndent(float firstWidth, int firstWidthLineCount) {
            mFirstWidth = firstWidth;
            mFirstWidthLineCount = firstWidthLineCount;
        }
    }

    // ── Builder ───────────────────────────────────────────────────────

    /**
     * Builder for {@link LineBreaker}.
     */
    public static class Builder {

        private int mBreakStrategy       = BREAK_STRATEGY_SIMPLE;
        private int mHyphenationFrequency = HYPHENATION_FREQUENCY_NONE;
        private int mJustificationMode   = JUSTIFICATION_MODE_NONE;

        public Builder() {}

        public Builder setBreakStrategy(int breakStrategy) {
            mBreakStrategy = breakStrategy;
            return this;
        }

        public Builder setHyphenationFrequency(int hyphenationFrequency) {
            mHyphenationFrequency = hyphenationFrequency;
            return this;
        }

        public Builder setJustificationMode(int justificationMode) {
            mJustificationMode = justificationMode;
            return this;
        }

        public Builder setIndents(int[] indents) {
            return this;
        }

        public LineBreaker build() {
            return new LineBreaker(mBreakStrategy, mHyphenationFrequency,
                                  mJustificationMode);
        }
    }
}
