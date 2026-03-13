package android.graphics;

/**
 * Shim: android.graphics.HardwareRenderer
 * OH mapping: drawing.OH_Drawing_Canvas / XComponent surface pipeline
 *
 * Represents a hardware-accelerated renderer attached to a Surface.
 * All rendering calls are no-ops in this shim; lifecycle methods
 * (start/stop/destroy) track a simple running flag.
 */
public class HardwareRenderer {

    private Object mContentRoot  = null;
    private Object mSurface      = null;
    private boolean mRunning     = false;
    private boolean mOpaque      = true;

    // Light source geometry (defaults match Android's internal defaults)
    private float mLightX        = 0f;
    private float mLightY        = 0f;
    private float mLightZ        = 0f;
    private float mLightRadius   = 0f;
    private float mAmbientShadowAlpha = 0f;
    private float mSpotShadowAlpha    = 0f;

    // ── Constructor ──────────────────────────────────────────────────────────

    public HardwareRenderer() {}

    // ── Content / surface ────────────────────────────────────────────────────

    /**
     * Set the content root RenderNode that will be drawn by this renderer.
     *
     * @param rootNode the RenderNode (or compatible Object) to use as root
     */
    public void setContentRoot(Object rootNode) {
        mContentRoot = rootNode;
    }

    /**
     * Set the Surface this renderer will draw into.
     *
     * @param surface an android.view.Surface or compatible Object
     */
    public void setSurface(Object surface) {
        mSurface = surface;
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    /** Start rendering. No-op in this shim; marks the renderer as running. */
    public void start() {
        mRunning = true;
    }

    /** Stop rendering. No-op in this shim; marks the renderer as not running. */
    public void stop() {
        mRunning = false;
    }

    /**
     * Destroy the renderer and release all associated resources.
     * No-op in this shim.
     */
    public void destroy() {
        mRunning  = false;
        mSurface  = null;
        mContentRoot = null;
    }

    // ── Render request ───────────────────────────────────────────────────────

    /**
     * Create a {@link FrameRenderRequest} to schedule a frame.
     *
     * @return a new FrameRenderRequest (stub — sync is a no-op)
     */
    public FrameRenderRequest createRenderRequest() {
        return new FrameRenderRequest();
    }

    /**
     * Nested stub for the fluent RenderRequest API.
     */
    public final class FrameRenderRequest {

        private FrameRenderRequest() {}

        /**
         * Set the desired presentation time for the frame.
         *
         * @param vsyncId  choreographer vsync id (ignored)
         * @param frameDeadline  deadline in nanoseconds (ignored)
         * @return this request for chaining
         */
        public FrameRenderRequest setFrameCommitCallback(long vsyncId, long frameDeadline) {
            return this;
        }

        /**
         * Submit the frame request. No-op in this shim.
         *
         * @return 0 (SYNC_OK stub)
         */
        public int syncAndDraw() {
            return 0; // SYNC_OK
        }
    }

    // ── Light source ─────────────────────────────────────────────────────────

    /**
     * Set the position and radius of the light source used for shadows.
     *
     * @param lightX      X position of the light
     * @param lightY      Y position of the light
     * @param lightZ      Z position of the light
     * @param lightRadius radius of the light source
     */
    public void setLightSourceGeometry(float lightX, float lightY,
                                       float lightZ, float lightRadius) {
        mLightX      = lightX;
        mLightY      = lightY;
        mLightZ      = lightZ;
        mLightRadius = lightRadius;
    }

    /**
     * Set the alpha values used when drawing ambient and spot shadows.
     *
     * @param ambientShadowAlpha alpha for ambient shadows (0..1)
     * @param spotShadowAlpha    alpha for spot shadows (0..1)
     */
    public void setLightSourceAlpha(float ambientShadowAlpha, float spotShadowAlpha) {
        mAmbientShadowAlpha = ambientShadowAlpha;
        mSpotShadowAlpha    = spotShadowAlpha;
    }

    // ── Opacity ──────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the renderer's output surface is considered opaque.
     */
    public boolean isOpaque() {
        return mOpaque;
    }

    /**
     * Set whether the renderer's output surface is opaque.
     *
     * @param opaque {@code true} for an opaque surface
     */
    public void setOpaque(boolean opaque) {
        mOpaque = opaque;
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "HardwareRenderer(running=" + mRunning
                + ", opaque=" + mOpaque + ")";
    }
}
