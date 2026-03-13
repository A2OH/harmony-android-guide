package android.graphics.text;

/**
 * Android-compatible TextRunShaper stub (API 31+).
 * Shapes text runs into glyph arrays. Stub returns null for all methods.
 */
public class TextRunShaper {

    private TextRunShaper() {}

    public static Object shapeTextRun(CharSequence text, int start, int count,
            int contextStart, int contextCount, float xOffset, float yOffset,
            boolean isRtl, Object paint) {
        return null;
    }

    public static Object shapeTextRun(char[] text, int start, int count,
            int contextStart, int contextCount, float xOffset, float yOffset,
            boolean isRtl, Object paint) {
        return null;
    }
}
