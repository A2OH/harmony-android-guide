package android.text;
import android.graphics.Paint;
import android.graphics.Paint;

import android.graphics.Paint;

/**
 * Android-compatible TextPaint shim. Extends Paint with text-specific fields.
 */
public class TextPaint extends Paint {

    /** Logical density of the display, used for dp-to-px conversions. */
    public float density = 1.0f;

    /**
     * Offset in pixels of the baseline from the normal baseline position.
     * Positive values shift text upward (superscript), negative downward (subscript).
     */
    public int baselineShift = 0;

    /** Background colour drawn behind the text (0 = transparent). */
    public int bgColor = 0;

    /** Colour applied to hyperlinks within this text. */
    public int linkColor = 0xFF0000EE; // default Android blue

    public TextPaint() {
        super();
    }

    public TextPaint(int flags) {
        super(flags);
    }

    public TextPaint(Paint src) {
        super(src);
        if (src instanceof TextPaint) {
            TextPaint tp = (TextPaint) src;
            density       = tp.density;
            baselineShift = tp.baselineShift;
            bgColor       = tp.bgColor;
            linkColor     = tp.linkColor;
        }
    }
}
