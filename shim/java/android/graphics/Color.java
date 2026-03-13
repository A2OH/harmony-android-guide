package android.graphics;

/**
 * Shim: android.graphics.Color
 * Pure Java — no OHBridge calls.
 */
public class Color {

    // ── Named color constants ────────────────────────────────────────────────

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

    // ── Component extractors ─────────────────────────────────────────────────

    public static int alpha(int color) {
        return (color >>> 24);
    }

    public static int red(int color) {
        return (color >> 16) & 0xFF;
    }

    public static int green(int color) {
        return (color >> 8) & 0xFF;
    }

    public static int blue(int color) {
        return color & 0xFF;
    }

    // ── Constructors ─────────────────────────────────────────────────────────

    public static int argb(int alpha, int red, int green, int blue) {
        return ((alpha & 0xFF) << 24)
             | ((red   & 0xFF) << 16)
             | ((green & 0xFF) <<  8)
             |  (blue  & 0xFF);
    }

    public static int rgb(int red, int green, int blue) {
        return argb(0xFF, red, green, blue);
    }

    // ── parseColor ───────────────────────────────────────────────────────────

    public static int parseColor(String colorString) {
        if (colorString == null) {
            throw new IllegalArgumentException("Unknown color: null");
        }
        if (colorString.startsWith("#")) {
            String hex = colorString.substring(1);
            if (hex.length() == 6) {
                // #RRGGBB — fully opaque
                int rgb = (int) Long.parseLong(hex, 16);
                return 0xFF000000 | rgb;
            } else if (hex.length() == 8) {
                // #AARRGGBB
                long val = Long.parseLong(hex, 16);
                return (int) val;
            }
            throw new IllegalArgumentException("Unknown color: " + colorString);
        }
        // Named colors
        switch (colorString.toLowerCase()) {
            case "black":   return BLACK;
            case "white":   return WHITE;
            case "red":     return RED;
            case "green":   return GREEN;
            case "blue":    return BLUE;
            case "yellow":  return YELLOW;
            case "cyan":    return CYAN;
            case "magenta": return MAGENTA;
            case "gray":
            case "grey":    return GRAY;
            case "transparent": return TRANSPARENT;
            default:
                throw new IllegalArgumentException("Unknown color: " + colorString);
        }
    }

    // ── HSV conversion ───────────────────────────────────────────────────────

    /**
     * Convert RGB to HSV.
     * @param red   0-255
     * @param green 0-255
     * @param blue  0-255
     * @param hsv   output array of length >= 3: [H(0-360), S(0-1), V(0-1)]
     * @return hsv (same array)
     */
    public static float[] RGBToHSV(int red, int green, int blue, float[] hsv) {
        float r = red   / 255f;
        float g = green / 255f;
        float b = blue  / 255f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        // Value
        float v = max;

        // Saturation
        float s = (max == 0f) ? 0f : (delta / max);

        // Hue
        float h;
        if (delta == 0f) {
            h = 0f;
        } else if (max == r) {
            h = 60f * (((g - b) / delta) % 6f);
        } else if (max == g) {
            h = 60f * (((b - r) / delta) + 2f);
        } else {
            h = 60f * (((r - g) / delta) + 4f);
        }
        if (h < 0f) h += 360f;

        hsv[0] = h;
        hsv[1] = s;
        hsv[2] = v;
        return hsv;
    }

    /**
     * Convert HSV to a packed ARGB color (alpha = 0xFF).
     * @param hsv [H(0-360), S(0-1), V(0-1)]
     */
    public static int HSVToColor(float[] hsv) {
        return HSVToColor(0xFF, hsv);
    }

    /**
     * Convert HSV to a packed ARGB color with the supplied alpha.
     * @param alpha 0-255
     * @param hsv   [H(0-360), S(0-1), V(0-1)]
     */
    public static int HSVToColor(int alpha, float[] hsv) {
        float h = hsv[0];
        float s = hsv[1];
        float v = hsv[2];

        if (s == 0f) {
            int grey = Math.round(v * 255f);
            return argb(alpha, grey, grey, grey);
        }

        float hh = (h % 360f) / 60f;
        int   i  = (int) hh;
        float ff = hh - i;
        float p  = v * (1f - s);
        float q  = v * (1f - s * ff);
        float t  = v * (1f - s * (1f - ff));

        float r, g, b;
        switch (i) {
            case 0:  r = v; g = t; b = p; break;
            case 1:  r = q; g = v; b = p; break;
            case 2:  r = p; g = v; b = t; break;
            case 3:  r = p; g = q; b = v; break;
            case 4:  r = t; g = p; b = v; break;
            default: r = v; g = p; b = q; break;
        }
        return argb(alpha,
                    Math.round(r * 255f),
                    Math.round(g * 255f),
                    Math.round(b * 255f));
    }
}
