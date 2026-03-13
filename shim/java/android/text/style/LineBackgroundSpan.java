package android.text.style;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * A2OH shim – Android-compatible stub for LineBackgroundSpan.
 *
 * Allows apps that reference this interface to compile against the
 * OpenHarmony shim layer without modification.
 */
public interface LineBackgroundSpan {

    /**
     * Draw the background for a line of text.
     *
     * @param canvas     the canvas (Object to avoid hard dependency on Canvas)
     * @param pa(int the pa(int (Object to avoid hard dependency on Paint)
     * @param left       left edge of the line
     * @param right      right edge of the line
     * @param top        top of the line
     * @param baseline   baseline of the line
     * @param bottom     bottom of the line
     * @param text       the text being drawn
     * @param start      start offset in text
     * @param end        end offset in text
     * @param lineNumber the line number (0-based)
     */
    void drawBackground(Object canvas, Object paint,
                        int left, int right,
                        int top, int baseline, int bottom,
                        CharSequence text, int start, int end,
                        int lineNumber);

    /**
     * Standard implementation that fills the background with a solid color.
     * In the A2OH shim layer the draw call is a no-op; the color value is
     * retained so that it can be queried and forwarded to the OH renderer.
     */
    public static class Standard implements LineBackgroundSpan {

        private final int mColor;

        /**
         * Create a Standard span with the given background color.
         *
         * @param color ARGB color value
         */
        public Standard(int color) {
            mColor = color;
        }

        /** Return the background color. */
        public int getColor() {
            return mColor;
        }

        /** No-op in the shim layer. */
        @Override
        public void drawBackground(Object canvas, Object paint,
                                   int left, int right,
                                   int top, int baseline, int bottom,
                                   CharSequence text, int start, int end,
                                   int lineNumber) {
            // Intentionally empty – rendering is handled by the OH bridge.
        }
    }
}
