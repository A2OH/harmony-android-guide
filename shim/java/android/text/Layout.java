package android.text;

import android.graphics.Paint;

/**
 * Android-compatible Layout shim. Abstract base for text layout.
 */
public abstract class Layout {

    public enum Alignment {
        ALIGN_NORMAL,
        ALIGN_CENTER,
        ALIGN_OPPOSITE
    }

    private CharSequence mText;
    private Paint mPaint;
    private int mWidth;
    private Alignment mAlignment;
    private float mSpacingMult;
    private float mSpacingAdd;

    protected Layout(CharSequence text, Paint paint, int width,
                     Alignment align, float spacingMult, float spacingAdd) {
        mText       = text;
        mPaint      = paint;
        mWidth      = width;
        mAlignment  = align;
        mSpacingMult = spacingMult;
        mSpacingAdd  = spacingAdd;
    }

    public final CharSequence getText()      { return mText;       }
    public final Paint        getPaint()     { return mPaint;      }
    public final int          getWidth()     { return mWidth;      }
    public final Alignment    getAlignment() { return mAlignment;  }

    /** Total height of this layout in pixels. */
    public int getHeight() {
        int count = getLineCount();
        return count == 0 ? 0 : getLineBottom(count - 1);
    }

    // Abstract methods subclasses must implement ----------------------------

    public abstract int getLineCount();

    /** Vertical position (top) of the given line in pixels. */
    public abstract int getLineTop(int line);

    /** Vertical position (bottom) of the given line in pixels. */
    public int getLineBottom(int line) {
        return getLineTop(line + 1);
    }

    /** Character offset of the first character on this line. */
    public abstract int getLineStart(int line);

    /** Character offset just past the last visible character on this line. */
    public abstract int getLineEnd(int line);

    /**
     * Returns the line containing the given vertical position.
     */
    public int getLineForVertical(int vertical) {
        int high = getLineCount();
        int low  = -1;
        while (high - low > 1) {
            int guess = (high + low) / 2;
            if (getLineTop(guess) > vertical) {
                high = guess;
            } else {
                low = guess;
            }
        }
        return low < 0 ? 0 : low;
    }

    /**
     * Returns the line that contains the given character offset.
     */
    public int getLineForOffset(int offset) {
        int high = getLineCount();
        int low  = -1;
        while (high - low > 1) {
            int guess = (high + low) / 2;
            if (getLineStart(guess) > offset) {
                high = guess;
            } else {
                low = guess;
            }
        }
        return low < 0 ? 0 : low;
    }

    /** Returns the paragraph direction (LTR=1 / RTL=-1) for the line. */
    public int getParagraphDirection(int line) {
        return 1; // LTR stub
    }
}
