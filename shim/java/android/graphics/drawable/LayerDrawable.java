package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.Canvas;

import android.graphics.Canvas;

/**
 * Shim: android.graphics.drawable.LayerDrawable
 * OH mapping: layered drawing with z-order composition
 *
 * Pure Java stub — stores a list of layers; draw() is a no-op.
 */
public class LayerDrawable extends Drawable {

    // ── Layer record ─────────────────────────────────────────────────────────

    private static final class Layer {
        Drawable drawable;
        int insetLeft, insetTop, insetRight, insetBottom;

        Layer(Drawable d) {
            this.drawable = d;
        }
    }

    // ── State ────────────────────────────────────────────────────────────────

    private Layer[] layers;
    private int     alpha = 0xFF;

    // ── Constructors ─────────────────────────────────────────────────────────

    public LayerDrawable(Drawable[] layers) {
        if (layers == null) {
            this.layers = new Layer[0];
        } else {
            this.layers = new Layer[layers.length];
            for (int i = 0; i < layers.length; i++) {
                this.layers[i] = new Layer(layers[i]);
            }
        }
    }

    // ── Layer access ─────────────────────────────────────────────────────────

    public int getNumberOfLayers() {
        return layers.length;
    }

    public Drawable getDrawable(int index) {
        checkIndex(index);
        return layers[index].drawable;
    }

    public void setDrawable(int index, Drawable drawable) {
        checkIndex(index);
        layers[index].drawable = drawable;
    }

    /**
     * Sets insets for the drawable at the given layer index.
     */
    public void setLayerInset(int index, int left, int top, int right, int bottom) {
        checkIndex(index);
        Layer l = layers[index];
        l.insetLeft   = left;
        l.insetTop    = top;
        l.insetRight  = right;
        l.insetBottom = bottom;
    }

    // ── Alpha ────────────────────────────────────────────────────────────────

    @Override
    public int getAlpha() { return alpha; }

    @Override
    public void setAlpha(int alpha) { this.alpha = alpha & 0xFF; }

    // ── Draw (no-op stub) ────────────────────────────────────────────────────

    @Override
    public void draw(Canvas canvas) { /* no-op */ }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void checkIndex(int index) {
        if (index < 0 || index >= layers.length) {
            throw new IndexOutOfBoundsException(
                "Layer index " + index + " out of range [0, " + layers.length + ")");
        }
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "LayerDrawable(layers=" + layers.length + ")";
    }
}
