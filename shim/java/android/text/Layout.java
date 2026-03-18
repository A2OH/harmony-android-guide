package android.text;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Android-compatible Layout shim. Abstract base for text layout.
 */
public class Layout {

    public enum Alignment {
        ALIGN_NORMAL,
        ALIGN_CENTER,
        ALIGN_OPPOSITE
    }

    private CharSequence mText;
    private Paint mPaint;
    private int mWidth;
    private Alignment mAlignment;
    protected float mSpacingMult;
    protected float mSpacingAdd;

    // Break strategy constants
    public static final int BREAK_STRATEGY_SIMPLE = 0;
    public static final int BREAK_STRATEGY_HIGH_QUALITY = 1;
    public static final int BREAK_STRATEGY_BALANCED = 2;

    // Hyphenation frequency constants
    public static final int HYPHENATION_FREQUENCY_NONE = 0;
    public static final int HYPHENATION_FREQUENCY_NORMAL = 1;
    public static final int HYPHENATION_FREQUENCY_FULL = 2;

    // Justification mode constants
    public static final int JUSTIFICATION_MODE_NONE = 0;
    public static final int JUSTIFICATION_MODE_INTER_WORD = 1;

    // Text direction constants
    public static final int DIR_LEFT_TO_RIGHT = 1;
    public static final int DIR_RIGHT_TO_LEFT = -1;

    public static final float DEFAULT_LINESPACING_MULTIPLIER = 1.0f;
    public static final float DEFAULT_LINESPACING_ADDITION = 0.0f;

    protected Layout(CharSequence text, Paint paint, int width,
                     Alignment align, float spacingMult, float spacingAdd) {
        mText       = text;
        mPaint      = paint;
        mWidth      = width;
        mAlignment  = align;
        mSpacingMult = spacingMult;
        mSpacingAdd  = spacingAdd;
    }

    public final CharSequence getText()      { return mText; }
    public final Paint getPaint()            { return mPaint; }
    public final int getWidth()              { return mWidth; }
    public final Alignment getAlignment()    { return mAlignment; }

    public int getHeight() {
        int count = getLineCount();
        return count == 0 ? 0 : getLineBottom(count - 1);
    }

    public int getHeight(boolean cap) { return getHeight(); }

    public int getLineCount() { return 0; }
    public int getLineTop(int line) { return 0; }
    public int getLineBottom(int line) { return getLineTop(line + 1); }
    public int getLineStart(int line) { return 0; }
    public int getLineEnd(int line) { return 0; }
    public int getLineDescent(int line) { return 0; }
    public int getLineAscent(int line) { return 0; }
    public boolean getLineContainsTab(int line) { return false; }

    public int getLineForVertical(int vertical) {
        int high = getLineCount();
        int low  = -1;
        while (high - low > 1) {
            int guess = (high + low) / 2;
            if (getLineTop(guess) > vertical) high = guess;
            else low = guess;
        }
        return low < 0 ? 0 : low;
    }

    public int getLineForOffset(int offset) {
        int high = getLineCount();
        int low  = -1;
        while (high - low > 1) {
            int guess = (high + low) / 2;
            if (getLineStart(guess) > offset) high = guess;
            else low = guess;
        }
        return low < 0 ? 0 : low;
    }

    public int getParagraphDirection(int line) { return DIR_LEFT_TO_RIGHT; }

    public int getEllipsisCount(int line) { return 0; }
    public int getEllipsisStart(int line) { return 0; }
    public int getLineVisibleEnd(int line) { return getLineEnd(line); }
    public int getOffsetForHorizontal(int line, float horiz) { return getLineStart(line); }
    public int getOffsetToLeftOf(int offset) { return Math.max(0, offset - 1); }
    public int getOffsetToRightOf(int offset) { return offset + 1; }
    public float getLineLeft(int line) { return 0; }
    public float getLineRight(int line) { return getWidth(); }
    public float getLineMax(int line) { return getWidth(); }
    public float getLineWidth(int line) { return getWidth(); }
    public float getPrimaryHorizontal(int offset) { return 0; }
    public float getSecondaryHorizontal(int offset) { return 0; }
    public boolean isRtlCharAt(int offset) { return false; }
    public int getTopPadding() { return 0; }
    public int getBottomPadding() { return 0; }
    public boolean isSpanned() { return mText instanceof Spanned; }

    public float getSpacingMultiplier() { return mSpacingMult; }
    public float getSpacingAdd() { return mSpacingAdd; }

    public static float getDesiredWidth(CharSequence source, TextPaint paint) { return 0; }
    public static float getDesiredWidth(CharSequence source, int start, int end, TextPaint paint) { return 0; }

    public void draw(Canvas c) {}
    public void draw(Canvas c, Path highlight, Paint highlightPaint, int cursorOffsetVertical) {}

    public void increaseWidthTo(int wid) { mWidth = wid; }

    public Directions getLineDirections(int line) { return DIRS_ALL_LEFT_TO_RIGHT; }

    public int getDesiredWidth() { return mWidth; }

    public int getLineBaseline(int line) { return getLineTop(line + 1) - getLineDescent(line); }

    public int getLineBounds(int line, android.graphics.Rect bounds) {
        if (bounds != null) {
            bounds.left = 0;
            bounds.top = getLineTop(line);
            bounds.right = getWidth();
            bounds.bottom = getLineBottom(line);
        }
        return getLineBaseline(line);
    }

    /** Directions stub class. */
    public static class Directions {
        public int[] mDirections;
        public Directions(int[] dirs) { mDirections = dirs; }
    }

    public static final Directions DIRS_ALL_LEFT_TO_RIGHT = new Directions(new int[]{0, Integer.MAX_VALUE});

    /** Annotation stub for break strategy. */
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.SOURCE)
    @java.lang.annotation.Target({java.lang.annotation.ElementType.PARAMETER, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD})
    public @interface BreakStrategy {}

    /** Annotation stub for hyphenation frequency. */
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.SOURCE)
    @java.lang.annotation.Target({java.lang.annotation.ElementType.PARAMETER, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD})
    public @interface HyphenationFrequency {}

    /** Annotation stub for justification mode. */
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.SOURCE)
    @java.lang.annotation.Target({java.lang.annotation.ElementType.PARAMETER, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD})
    public @interface JustificationMode {}
}
