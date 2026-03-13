package android.graphics;

/**
 * Android-compatible PorterDuffColorFilter shim.
 * OH mapping: drawing.OH_Drawing_ColorFilter (blend-mode variant)
 *
 * Blends a single source color onto every pixel using one of the
 * Porter-Duff compositing operators.
 */
public class PorterDuffColorFilter extends ColorFilter {

    private final int color;
    private final PorterDuff.Mode mode;

    /**
     * @param color  the source color (ARGB format) to blend with each pixel.
     * @param mode   the Porter-Duff compositing mode.
     */
    public PorterDuffColorFilter(int color, PorterDuff.Mode mode) {
        if (mode == null) throw new NullPointerException("PorterDuffColorFilter: mode must not be null");
        this.color = color;
        this.mode  = mode;
    }

    /** Returns the source color (ARGB). */
    public int getColor() { return color; }

    /** Returns the compositing mode. */
    public PorterDuff.Mode getMode() { return mode; }
}
