package android.text;

/**
 * Android-compatible DynamicLayout shim.
 * Extends Layout for text whose content can change (Editable).
 * Stub implementation: line structure is recalculated lazily on construction
 * using the same simple estimate as StaticLayout.
 */
public class DynamicLayout extends Layout {

    // ── Instance state ───────────────────────────────────────────────────────

    private final CharSequence mBase;   // original mutable source (Editable)
    private int mLineCount;
    private int mLineHeight;

    // ── Constructors ─────────────────────────────────────────────────────────

    /**
     * Standard constructor mirroring android.text.DynamicLayout.
     *
     * @param base         the Editable (or other CharSequence) being displayed
     * @param pa(int the TextPaint to use for measurement
     * @param width        layout width in pixels
     * @param align        text alignment
     * @param spacingMult  line-spacing multiplier
     * @param spacingAdd   line-spacing extra pixels
     */
    public DynamicLayout(CharSequence base, TextPaint paint, int width,
                         Alignment align, float spacingMult, float spacingAdd) {
        super(base, paint, width, align, spacingMult, spacingAdd);
        mBase       = base;
        mLineHeight = computeLineHeight(paint, spacingMult, spacingAdd);
        mLineCount  = Math.max(1, estimateLineCount(base, paint, width));
    }

    /**
     * Constructor variant that takes a separate display text from the backing
     * Editable (mirrors the Android API's display/base distinction).
     *
     * @param base    the Editable being tracked
     * @param display the CharSequence actually measured for layout
     * @param pa(int TextPaint for measurement
     * @param width   layout width in pixels
     * @param align   text alignment
     * @param spacingMult line-spacing multiplier
     * @param spacingAdd  line-spacing extra pixels
     * @param includePad  whether to add padding (ignored in stub)
     */
    public DynamicLayout(CharSequence base, CharSequence display,
                         TextPaint paint, int width,
                         Alignment align, float spacingMult, float spacingAdd,
                         boolean includePad) {
        super(display, paint, width, align, spacingMult, spacingAdd);
        mBase       = base;
        mLineHeight = computeLineHeight(paint, spacingMult, spacingAdd);
        mLineCount  = Math.max(1, estimateLineCount(display, paint, width));
    }

    // ── Layout overrides ─────────────────────────────────────────────────────

    @Override
    public int getLineCount() { return mLineCount; }

    @Override
    public int getLineTop(int line) { return line * mLineHeight; }

    @Override
    public int getLineStart(int line) {
        CharSequence t = getText();
        if (mLineCount <= 1 || t == null) return 0;
        int charsPerLine = Math.max(1, t.length() / mLineCount);
        return Math.min(line * charsPerLine, t.length());
    }

    @Override
    public int getLineEnd(int line) {
        CharSequence t = getText();
        if (t == null)         return 0;
        if (mLineCount <= 1)   return t.length();
        int charsPerLine = Math.max(1, t.length() / mLineCount);
        return Math.min((line + 1) * charsPerLine, t.length());
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private static int computeLineHeight(TextPaint paint, float spacingMult, float spacingAdd) {
        int base = (paint != null) ? Math.round(paint.getTextSize()) : 16;
        return Math.max(1, Math.round(base * spacingMult + spacingAdd));
    }

    public static final class Builder {
        private CharSequence mBase;
        private CharSequence mDisplay;
        private TextPaint mPaint;
        private int mWidth;
        private Alignment mAlignment = Alignment.ALIGN_NORMAL;
        private float mSpacingMult = 1.0f;
        private float mSpacingAdd = 0.0f;

        private Builder() {}

        public static Builder obtain(CharSequence base, TextPaint paint, int width) {
            Builder b = new Builder();
            b.mBase = base;
            b.mDisplay = base;
            b.mPaint = paint;
            b.mWidth = width;
            return b;
        }

        public Builder setDisplayText(CharSequence display) { mDisplay = display; return this; }
        public Builder setAlignment(Alignment alignment) { mAlignment = alignment; return this; }
        public Builder setLineSpacing(float add, float mult) { mSpacingAdd = add; mSpacingMult = mult; return this; }
        public Builder setTextDirection(TextDirectionHeuristic textDir) { return this; }
        public Builder setEllipsize(TextUtils.TruncateAt ellipsize) { return this; }
        public Builder setEllipsizedWidth(int width) { return this; }
        public Builder setBreakStrategy(int breakStrategy) { return this; }
        public Builder setHyphenationFrequency(int frequency) { return this; }
        public Builder setJustificationMode(int mode) { return this; }
        public Builder setIncludePad(boolean includePad) { return this; }
        public Builder setUseLineSpacingFromFallbacks(boolean value) { return this; }

        public DynamicLayout build() {
            return new DynamicLayout(mBase, mDisplay, mPaint, mWidth, mAlignment, mSpacingMult, mSpacingAdd, true);
        }
    }

    public static final int INVALID_BLOCK_INDEX = -1;

    public int[] getBlockEndLines() { return new int[0]; }
    public int[] getBlockIndices() { return new int[0]; }
    public int getNumberOfBlocks() { return 0; }
    public int getBlockIndex(int index) { return INVALID_BLOCK_INDEX; }
    public android.util.ArraySet<Integer> getBlocksAlwaysNeedToBeRedrawn() { return null; }
    public int getIndexFirstChangedBlock() { return 0; }
    public void setIndexFirstChangedBlock(int index) {}

    private static int estimateLineCount(CharSequence text, TextPaint paint, int width) {
        if (text == null || text.length() == 0 || width <= 0) return 1;
        float avgCharWidth = (paint != null) ? paint.getTextSize() * 0.6f : 10f;
        int charsPerLine = Math.max(1, (int) (width / avgCharWidth));
        return (text.length() + charsPerLine - 1) / charsPerLine;
    }
}
