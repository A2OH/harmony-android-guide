package android.graphics;

public class Color {
    // Packed ARGB color stored by valueOf()
    private final int mColor;

    public Color() { mColor = BLACK; }

    private Color(int color) { mColor = color; }

    public static final int BLACK       = 0xFF000000;
    public static final int DKGRAY      = 0xFF444444;
    public static final int GRAY        = 0xFF888888;
    public static final int LTGRAY      = 0xFFCCCCCC;
    public static final int WHITE       = 0xFFFFFFFF;
    public static final int RED         = 0xFFFF0000;
    public static final int GREEN       = 0xFF00FF00;
    public static final int BLUE        = 0xFF0000FF;
    public static final int YELLOW      = 0xFFFFFF00;
    public static final int CYAN        = 0xFF00FFFF;
    public static final int MAGENTA     = 0xFFFF00FF;
    public static final int TRANSPARENT = 0x00000000;

    // ── Static component extractors ──

    public static int alpha(int color) { return (color >>> 24); }
    public static int red(int color)   { return (color >> 16) & 0xFF; }
    public static int green(int color) { return (color >> 8) & 0xFF; }
    public static int blue(int color)  { return color & 0xFF; }

    // ── Static packing ──

    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static int rgb(int red, int green, int blue) {
        return argb(255, red, green, blue);
    }

    // ── Float packing (API 26+) ──

    public static int argb(float alpha, float red, float green, float blue) {
        return argb((int) (alpha * 255.0f + 0.5f),
                     (int) (red   * 255.0f + 0.5f),
                     (int) (green * 255.0f + 0.5f),
                     (int) (blue  * 255.0f + 0.5f));
    }

    public static int rgb(float red, float green, float blue) {
        return argb(1.0f, red, green, blue);
    }

    // ── Parse ──

    public static int parseColor(String colorString) {
        if (colorString == null || colorString.isEmpty()) {
            throw new IllegalArgumentException("Unknown color: " + colorString);
        }
        if (colorString.charAt(0) == '#') {
            String hex = colorString.substring(1);
            long color;
            if (hex.length() == 6) {
                color = Long.parseLong(hex, 16) | 0xFF000000L;
            } else if (hex.length() == 8) {
                color = Long.parseLong(hex, 16);
            } else {
                throw new IllegalArgumentException("Unknown color: " + colorString);
            }
            return (int) color;
        }
        switch (colorString.toLowerCase()) {
            case "black":       return BLACK;
            case "darkgray":
            case "dkgray":      return DKGRAY;
            case "gray":        return GRAY;
            case "lightgray":
            case "ltgray":      return LTGRAY;
            case "white":       return WHITE;
            case "red":         return RED;
            case "green":       return GREEN;
            case "blue":        return BLUE;
            case "yellow":      return YELLOW;
            case "cyan":        return CYAN;
            case "magenta":     return MAGENTA;
            case "transparent": return TRANSPARENT;
            default:
                throw new IllegalArgumentException("Unknown color: " + colorString);
        }
    }

    // ── valueOf (API 26+) ──

    public static Color valueOf(int color) {
        return new Color(color);
    }

    public static Color valueOf(float r, float g, float b) {
        return valueOf(rgb(r, g, b));
    }

    public static Color valueOf(float r, float g, float b, float a) {
        return valueOf(argb(a, r, g, b));
    }

    /** Return the packed ARGB int representation. */
    public int toArgb() {
        return mColor;
    }

    // ── Instance float-returning component accessors (API 26+) ──

    public float alpha() { return ((mColor >>> 24) & 0xFF) / 255.0f; }
    public float red()   { return ((mColor >> 16) & 0xFF) / 255.0f; }
    public float green() { return ((mColor >> 8) & 0xFF) / 255.0f; }
    public float blue()  { return (mColor & 0xFF) / 255.0f; }

    // ── Luminance ──

    /**
     * Returns the relative luminance of a packed color int, per BT.709.
     */
    public static float luminance(int color) {
        float r = red(color) / 255.0f;
        float g = green(color) / 255.0f;
        float b = blue(color) / 255.0f;
        // Linearize sRGB
        r = (r < 0.04045f) ? r / 12.92f : (float) Math.pow((r + 0.055) / 1.055, 2.4);
        g = (g < 0.04045f) ? g / 12.92f : (float) Math.pow((g + 0.055) / 1.055, 2.4);
        b = (b < 0.04045f) ? b / 12.92f : (float) Math.pow((b + 0.055) / 1.055, 2.4);
        return 0.2126f * r + 0.7152f * g + 0.0722f * b;
    }

    /** Instance luminance for API 26+ Color objects. */
    public float luminance() {
        return luminance(mColor);
    }

    // Keep the Object-parameter overload for binary compat with generated stubs
    public static float luminance(Object p0) {
        if (p0 instanceof Integer) return luminance((int)(Integer) p0);
        return 0f;
    }

    // ── HSV conversion ──

    public static void RGBToHSV(int red, int green, int blue, float[] hsv) {
        if (hsv == null || hsv.length < 3) {
            throw new RuntimeException("hsv array must have length >= 3");
        }
        float r = red / 255.0f;
        float g = green / 255.0f;
        float b = blue / 255.0f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        // Value
        hsv[2] = max;

        // Saturation
        if (max == 0f) {
            hsv[1] = 0f;
        } else {
            hsv[1] = delta / max;
        }

        // Hue
        if (delta == 0f) {
            hsv[0] = 0f;
        } else if (max == r) {
            hsv[0] = 60f * (((g - b) / delta) % 6f);
        } else if (max == g) {
            hsv[0] = 60f * (((b - r) / delta) + 2f);
        } else {
            hsv[0] = 60f * (((r - g) / delta) + 4f);
        }
        if (hsv[0] < 0f) hsv[0] += 360f;
    }

    public static void colorToHSV(int color, float[] hsv) {
        RGBToHSV(red(color), green(color), blue(color), hsv);
    }

    public static int HSVToColor(float[] hsv) {
        return HSVToColor(255, hsv);
    }

    public static int HSVToColor(int alpha, float[] hsv) {
        float h = hsv[0];
        float s = hsv[1];
        float v = hsv[2];

        float c = v * s;
        float x = c * (1f - Math.abs((h / 60f) % 2f - 1f));
        float m = v - c;

        float r, g, b;
        if (h < 60f)       { r = c; g = x; b = 0; }
        else if (h < 120f) { r = x; g = c; b = 0; }
        else if (h < 180f) { r = 0; g = c; b = x; }
        else if (h < 240f) { r = 0; g = x; b = c; }
        else if (h < 300f) { r = x; g = 0; b = c; }
        else               { r = c; g = 0; b = x; }

        int ri = Math.round((r + m) * 255f);
        int gi = Math.round((g + m) * 255f);
        int bi = Math.round((b + m) * 255f);
        return argb(alpha, ri, gi, bi);
    }

    // ── Color space helpers (sRGB by default) ──

    public float getComponent(Object p0, Object p1) { return 0f; }
    public Object getModel() { return null; }
    public static boolean isInColorSpace(Object p0, Object p1) { return false; }
    public boolean isSrgb() { return true; }
    public static boolean isSrgb(Object p0) { return true; }
    public boolean isWideGamut() { return false; }
    public static boolean isWideGamut(Object p0) { return false; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Color)) return false;
        return mColor == ((Color) o).mColor;
    }

    @Override
    public int hashCode() {
        return mColor;
    }

    @Override
    public String toString() {
        return "Color(" + Integer.toHexString(mColor) + ")";
    }
}
