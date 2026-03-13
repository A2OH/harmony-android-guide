package android.graphics;

/**
 * Android-compatible BlurMaskFilter shim.
 * OH mapping: drawing.OH_Drawing_MaskFilter (Gaussian blur)
 *
 * Applies a Gaussian blur of the given radius to the paint's alpha mask,
 * using the specified blur style.
 */
public class BlurMaskFilter extends MaskFilter {

    /**
     * Specifies how the blur is computed relative to the source shape.
     */
    public enum Blur {
        /** Blurs inside and outside the shape boundary. */
        NORMAL,
        /** Blurs only inside the shape, keeping full alpha outside. */
        SOLID,
        /** Blurs only outside the shape boundary. */
        OUTER,
        /** Blurs only inside the shape boundary. */
        INNER
    }

    private final float radius;
    private final Blur  blur;

    /**
     * @param radius  standard deviation of the Gaussian blur kernel, in pixels. Must be > 0.
     * @param blur    the blur style to apply.
     */
    public BlurMaskFilter(float radius, Blur blur) {
        if (radius <= 0) {
            throw new IllegalArgumentException("BlurMaskFilter: radius must be > 0, got " + radius);
        }
        if (blur == null) throw new NullPointerException("BlurMaskFilter: blur style must not be null");
        this.radius = radius;
        this.blur   = blur;
    }

    /** Returns the blur radius. */
    public float getRadius() { return radius; }

    /** Returns the blur style. */
    public Blur getBlur() { return blur; }
}
