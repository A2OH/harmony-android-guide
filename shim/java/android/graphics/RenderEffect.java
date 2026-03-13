package android.graphics;

/**
 * Shim: android.graphics.RenderEffect
 * OH mapping: drawing.OH_Drawing_Filter / effect chain
 *
 * Immutable descriptor for a GPU-side rendering effect applied to a View or
 * RenderNode.  Created exclusively via static factory methods.  All instances
 * are stubs; no actual pixel processing is performed.
 */
public final class RenderEffect {

    // ── Effect kind (for toString / debugging only) ──────────────────────────

    private final String mKind;

    private RenderEffect(String kind) {
        mKind = kind;
    }

    // ── Factory methods ──────────────────────────────────────────────────────

    /**
     * Create a blur effect.
     *
     * @param radiusX  horizontal blur radius in pixels
     * @param radiusY  vertical blur radius in pixels
     * @param shader   input shader or tile mode (Object; may be null for default)
     * @return a new RenderEffect stub
     */
    public static RenderEffect createBlurEffect(float radiusX, float radiusY, Object shader) {
        return new RenderEffect("blur(rx=" + radiusX + ",ry=" + radiusY + ")");
    }

    /**
     * Create a bitmap-based effect (renders the content through a Bitmap shader).
     *
     * @param bitmap the source Bitmap (Object; may be null in stub)
     * @return a new RenderEffect stub
     */
    public static RenderEffect createBitmapEffect(Object bitmap) {
        return new RenderEffect("bitmap(" + bitmap + ")");
    }

    /**
     * Chain two effects so that {@code outer} is applied after {@code inner}.
     *
     * @param inner the first effect to apply
     * @param outer the second effect to apply
     * @return a new RenderEffect stub representing the chain
     */
    public static RenderEffect createChainEffect(RenderEffect inner, RenderEffect outer) {
        String innerKind = (inner != null) ? inner.mKind : "null";
        String outerKind = (outer != null) ? outer.mKind : "null";
        return new RenderEffect("chain(" + innerKind + " -> " + outerKind + ")");
    }

    /**
     * Create an offset effect that shifts the rendered content.
     *
     * @param offsetX horizontal offset in pixels
     * @param offsetY vertical offset in pixels
     * @return a new RenderEffect stub
     */
    public static RenderEffect createOffsetEffect(float offsetX, float offsetY) {
        return new RenderEffect("offset(dx=" + offsetX + ",dy=" + offsetY + ")");
    }

    /**
     * Create a color-filter effect.
     *
     * @param colorFilter a {@link ColorFilter} or compatible Object
     * @return a new RenderEffect stub
     */
    public static RenderEffect createColorFilterEffect(Object colorFilter) {
        return new RenderEffect("colorFilter(" + colorFilter + ")");
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "RenderEffect{" + mKind + "}";
    }
}
