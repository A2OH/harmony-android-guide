package android.graphics.text;
import android.graphics.fonts.Font;
import android.graphics.fonts.Font;

import android.graphics.fonts.Font;

/**
 * Android-compatible PositionedGlyphs shim (API 31+).
 * Represents the result of text shaping: positioned glyphs with metrics.
 * Stub implementation returns zero/null defaults; no actual shaping.
 */
public class PositionedGlyphs {

    /** Returns the number of glyphs in this result. */
    public int glyphCount() {
        return 0;
    }

    /** Returns the X position of the glyph at the given index. */
    public float getGlyphX(int index) {
        return 0f;
    }

    /** Returns the Y position of the glyph at the given index. */
    public float getGlyphY(int index) {
        return 0f;
    }

    /** Returns the glyph ID at the given index. */
    public int getGlyphId(int index) {
        return 0;
    }

    /** Returns the Font used to render the glyph at the given index. */
    public Font getFont(int index) {
        return null;
    }

    /** Returns the total advance width of the shaped text. */
    public float getAdvance() {
        return 0f;
    }

    /** Returns the ascent of the shaped text. */
    public float getAscent() {
        return 0f;
    }

    /** Returns the descent of the shaped text. */
    public float getDescent() {
        return 0f;
    }
}
