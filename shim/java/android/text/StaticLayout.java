package android.text;
import android.graphics.Paint;
import android.graphics.Paint;

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
        private Paint mPaint   = new Paint();
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

        public Builder setTextDirection(TextDirectionHeuristic textDir) { return this; }
        public Builder setEllipsize(TextUtils.TruncateAt ellipsize) { return this; }
        public Builder setEllipsizedWidth(int ellipsizedWidth) { return this; }
        public Builder setMaxLines(int maxLines) { return this; }
        public Builder setBreakStrategy(int breakStrategy) { return this; }
        public Builder setHyphenationFrequency(int hyphenationFrequency) { return this; }
        public Builder setJustificationMode(int justificationMode) { return this; }
        public Builder setIncludePad(boolean includePad) { return this; }
        public Builder setUseLineSpacingFromFallbacks(boolean useLineSpacingFromFallbacks) { return this; }
        public Builder setIndents(int[] leftIndents, int[] rightIndents) { return this; }
        public Builder setAvailableWidth(int availableWidth) { return this; }
        public Builder setAddLastLineLineSpacing(boolean value) { return this; }

        public StaticLayout build() {
            return new StaticLayout(mText, mPaint, mWidth, mAlign, mSpacingMult, mSpacingAdd);
        }
    }

    // -----------------------------------------------------------------------
    // Instance state
    // -----------------------------------------------------------------------
    private int[] mLineStarts;
    private float[] mLineWidths;
    private int   mLineCount;
    private int   mLineHeight;

    /** Constructor with real word-wrapping. */
    public StaticLayout(CharSequence text, Paint paint, int width,
                        Alignment align, float spacingMult, float spacingAdd) {
        super(text, paint, width, align, spacingMult, spacingAdd);
        mLineHeight = computeLineHeight(paint, spacingMult, spacingAdd);
        breakText(text, paint, width);
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
        if (mLineStarts == null) return 0;
        if (line < 0) return 0;
        if (line >= mLineStarts.length) return mLineStarts[mLineStarts.length - 1];
        return mLineStarts[line];
    }

    @Override
    public int getLineEnd(int line) {
        if (mLineStarts == null) return 0;
        int idx = line + 1;
        if (idx < 0) return 0;
        if (idx >= mLineStarts.length) return mLineStarts[mLineStarts.length - 1];
        return mLineStarts[idx];
    }

    public float getLineWidth(int line) {
        if (mLineWidths == null || line < 0 || line >= mLineWidths.length) return 0f;
        return mLineWidths[line];
    }

    public int getEllipsisCount(int line) { return 0; }
    public int getEllipsisStart(int line) { return 0; }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static int computeLineHeight(Paint paint, float spacingMult, float spacingAdd) {
        if (paint != null) {
            Paint.FontMetrics fm = paint.getFontMetrics();
            int base = (int) Math.ceil(fm.descent - fm.ascent + fm.leading);
            if (base > 0) return Math.max(1, Math.round(base * spacingMult + spacingAdd));
        }
        int base = 16;
        return Math.round(base * spacingMult + spacingAdd);
    }

    private void breakText(CharSequence text, Paint paint, int width) {
        if (text == null || text.length() == 0) {
            mLineCount = 1;
            mLineStarts = new int[] { 0, 0 };
            mLineWidths = new float[] { 0f };
            return;
        }
        String s = text.toString();
        java.util.List startList = new java.util.ArrayList();
        java.util.List widthList = new java.util.ArrayList();
        // Split on hard line breaks then word-wrap each paragraph
        int paraStart = 0;
        while (paraStart <= s.length()) {
            int paraEnd = s.indexOf('\n', paraStart);
            if (paraEnd < 0) paraEnd = s.length();
            wrapParagraph(s, paraStart, paraEnd, paint, width, startList, widthList);
            paraStart = paraEnd + 1;
        }
        mLineCount = startList.size();
        if (mLineCount == 0) {
            mLineCount = 1;
            mLineStarts = new int[] { 0, s.length() };
            mLineWidths = new float[] { 0f };
            return;
        }
        mLineStarts = new int[mLineCount + 1];
        mLineWidths = new float[mLineCount];
        for (int i = 0; i < mLineCount; i++) {
            mLineStarts[i] = ((Integer) startList.get(i)).intValue();
            mLineWidths[i] = ((Float) widthList.get(i)).floatValue();
        }
        mLineStarts[mLineCount] = s.length();
    }

    private void wrapParagraph(String s, int start, int end, Paint paint, int maxWidth,
                                java.util.List lineStarts, java.util.List lineWidths) {
        if (maxWidth <= 0 || start >= end) {
            lineStarts.add(Integer.valueOf(start));
            lineWidths.add(Float.valueOf(0f));
            return;
        }
        int lineStart = start;
        while (lineStart < end) {
            float lineW = paint.measureText(s, lineStart, end);
            if (lineW <= maxWidth) {
                lineStarts.add(Integer.valueOf(lineStart));
                lineWidths.add(Float.valueOf(lineW));
                lineStart = end;
                break;
            }
            // Binary search for break point
            int lo = lineStart;
            int hi = end;
            while (lo < hi - 1) {
                int mid = (lo + hi) / 2;
                if (paint.measureText(s, lineStart, mid) <= maxWidth) lo = mid;
                else hi = mid;
            }
            int breakAt = lo;
            if (breakAt <= lineStart) breakAt = lineStart + 1;
            // Try word boundary
            int wordBreak = -1;
            for (int i = breakAt; i > lineStart; i--) {
                if (s.charAt(i - 1) == ' ') { wordBreak = i; break; }
            }
            if (wordBreak > lineStart) breakAt = wordBreak;
            float w = paint.measureText(s, lineStart, breakAt);
            lineStarts.add(Integer.valueOf(lineStart));
            lineWidths.add(Float.valueOf(w));
            lineStart = breakAt;
            while (lineStart < end && s.charAt(lineStart) == ' ') lineStart++;
        }
    }
}
