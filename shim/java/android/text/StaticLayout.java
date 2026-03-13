package android.text;

import android.graphics.Paint;

/**
 * Android-compatible StaticLayout shim. Lays out text into fixed-width lines.
 * Stub implementation: all text is treated as a single line.
 */
public class StaticLayout extends Layout {

    // -----------------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------------
    public static final class Builder {
        private CharSequence mText    = "";
        private Paint        mPaint   = new Paint();
        private int          mWidth   = 0;
        private Alignment    mAlign   = Alignment.ALIGN_NORMAL;
        private float        mSpacingMult = 1.0f;
        private float        mSpacingAdd  = 0.0f;

        private Builder() {}

        public static Builder obtain(CharSequence text, int start, int end, Paint paint, int width) {
            Builder b = new Builder();
            b.mText  = text.subSequence(start, end);
            b.mPaint = paint;
            b.mWidth = width;
            return b;
        }

        public Builder setText(CharSequence text) {
            mText = text;
            return this;
        }

        public Builder setAlignment(Alignment align) {
            mAlign = align;
            return this;
        }

        public Builder setLineSpacing(float add, float mult) {
            mSpacingAdd  = add;
            mSpacingMult = mult;
            return this;
        }

        public StaticLayout build() {
            return new StaticLayout(mText, mPaint, mWidth, mAlign, mSpacingMult, mSpacingAdd);
        }
    }

    // -----------------------------------------------------------------------
    // Instance state
    // -----------------------------------------------------------------------
    private final int   mLineCount;
    private final int   mLineHeight;

    /** Simple constructor (single-line stub). */
    public StaticLayout(CharSequence text, Paint paint, int width,
                        Alignment align, float spacingMult, float spacingAdd) {
        super(text, paint, width, align, spacingMult, spacingAdd);
        mLineHeight = computeLineHeight(paint, spacingMult, spacingAdd);
        mLineCount  = Math.max(1, estimateLineCount(text, paint, width));
    }

    /** Legacy constructor with includePad flag (ignored). */
    public StaticLayout(CharSequence text, Paint paint, int width,
                        Alignment align, float spacingMult, float spacingAdd,
                        boolean includePad) {
        this(text, paint, width, align, spacingMult, spacingAdd);
    }

    // -----------------------------------------------------------------------
    // Layout overrides
    // -----------------------------------------------------------------------

    @Override
    public int getLineCount() {
        return mLineCount;
    }

    @Override
    public int getLineTop(int line) {
        return line * mLineHeight;
    }

    @Override
    public int getLineStart(int line) {
        if (mLineCount <= 1 || getText() == null) return 0;
        int charsPerLine = Math.max(1, getText().length() / mLineCount);
        return Math.min(line * charsPerLine, getText().length());
    }

    @Override
    public int getLineEnd(int line) {
        if (getText() == null) return 0;
        if (mLineCount <= 1) return getText().length();
        int charsPerLine = Math.max(1, getText().length() / mLineCount);
        return Math.min((line + 1) * charsPerLine, getText().length());
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static int computeLineHeight(Paint paint, float spacingMult, float spacingAdd) {
        // Approximate: use font metrics if paint provides them; default to 16.
        int base = 16;
        return Math.round(base * spacingMult + spacingAdd);
    }

    private static int estimateLineCount(CharSequence text, Paint paint, int width) {
        if (text == null || text.length() == 0 || width <= 0) return 1;
        // Very rough: assume average char width of 10px
        int avgCharWidth = 10;
        int charsPerLine = Math.max(1, width / avgCharWidth);
        return (text.length() + charsPerLine - 1) / charsPerLine;
    }
}
