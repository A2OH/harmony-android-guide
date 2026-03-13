package android.graphics;

/**
 * Shim: android.graphics.RecordingCanvas
 * OH mapping: drawing.OH_Drawing_Canvas (recording path via RenderNode)
 *
 * A Canvas subclass used for recording drawing commands into a RenderNode.
 * Adds hardware-accelerated extensions: Z-ordering (enable/disableZ) and
 * direct RenderNode drawing.  All operations are no-ops in this shim.
 */
public class RecordingCanvas extends Canvas {

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Package-private: instances are obtained via {@link RenderNode#beginRecording()}.
     */
    RecordingCanvas() {
        super();
    }

    // ── Z ordering ───────────────────────────────────────────────────────────

    /**
     * Enable Z-based reordering of draw calls within this canvas.
     * Children with a non-zero Z/elevation will be reordered relative to
     * their siblings.  No-op in this shim.
     */
    public void enableZ() { /* no-op */ }

    /**
     * Disable Z-based reordering of draw calls within this canvas.
     * Subsequent draw calls are issued in order.  No-op in this shim.
     */
    public void disableZ() { /* no-op */ }

    // ── RenderNode drawing ───────────────────────────────────────────────────

    /**
     * Draw the given RenderNode (or compatible Object) into this canvas.
     * The object is cast to {@link RenderNode} when possible; otherwise this
     * is a no-op.
     *
     * @param renderNode a {@link RenderNode} whose display list will be replayed
     */
    public void drawRenderNode(Object renderNode) { /* no-op */ }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "RecordingCanvas(width=" + getWidth()
                + ", height=" + getHeight() + ")";
    }
}
