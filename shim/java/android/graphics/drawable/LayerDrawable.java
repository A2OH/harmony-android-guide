package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Shim: android.graphics.drawable.LayerDrawable
 * OH mapping: layered drawing with z-order composition
 *
 * Manages an array of child Drawables drawn in order (back to front).
 * Each layer has optional insets that shrink the layer relative to the
 * drawable bounds. Layers can also have IDs for lookup.
 */
public class LayerDrawable extends Drawable {

    // ── Padding mode constants ───────────────────────────────────────────────

    public static final int PADDING_MODE_NEST  = 0;
    public static final int PADDING_MODE_STACK = 1;

    // ── Layer record ─────────────────────────────────────────────────────────

    static final class Layer {
        Drawable drawable;
        int id = -1; // android.view.View.NO_ID
        int insetLeft, insetTop, insetRight, insetBottom;

        Layer(Drawable d) {
            this.drawable = d;
        }
    }

    // ── State ────────────────────────────────────────────────────────────────

    private Layer[] layers;
    private int     alpha = 0xFF;
    private int     paddingMode = PADDING_MODE_NEST;

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
     * Sets the ID for a given layer. IDs can be used to find layers by ID.
     */
    public void setId(int index, int id) {
        checkIndex(index);
        layers[index].id = id;
    }

    public int getId(int index) {
        checkIndex(index);
        return layers[index].id;
    }

    /**
     * Finds the first layer with the given ID and returns its index, or -1.
     */
    public int findIndexByLayerId(int id) {
        for (int i = layers.length - 1; i >= 0; i--) {
            if (layers[i].id == id) return i;
        }
        return -1;
    }

    /**
     * Finds the first layer with the given ID and returns its Drawable, or null.
     */
    public Drawable findDrawableByLayerId(int id) {
        int idx = findIndexByLayerId(id);
        return idx >= 0 ? layers[idx].drawable : null;
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

    public int getLayerInsetLeft(int index)   { checkIndex(index); return layers[index].insetLeft; }
    public int getLayerInsetTop(int index)    { checkIndex(index); return layers[index].insetTop; }
    public int getLayerInsetRight(int index)  { checkIndex(index); return layers[index].insetRight; }
    public int getLayerInsetBottom(int index) { checkIndex(index); return layers[index].insetBottom; }

    // ── Padding mode ─────────────────────────────────────────────────────────

    public void setPaddingMode(int mode) { this.paddingMode = mode; }
    public int getPaddingMode() { return paddingMode; }

    /**
     * Returns aggregated padding from all layers.
     */
    public boolean getPadding(Rect padding) {
        // Aggregate padding from all layers (nest mode sums, stack takes max)
        boolean hasPadding = false;
        padding.set(0, 0, 0, 0);
        for (Layer layer : layers) {
            if (layer.drawable != null) {
                Rect lp = new Rect();
                // Use insets as padding contribution
                if (layer.insetLeft != 0 || layer.insetTop != 0
                        || layer.insetRight != 0 || layer.insetBottom != 0) {
                    if (paddingMode == PADDING_MODE_NEST) {
                        padding.left   += layer.insetLeft;
                        padding.top    += layer.insetTop;
                        padding.right  += layer.insetRight;
                        padding.bottom += layer.insetBottom;
                    } else {
                        padding.left   = Math.max(padding.left,   layer.insetLeft);
                        padding.top    = Math.max(padding.top,    layer.insetTop);
                        padding.right  = Math.max(padding.right,  layer.insetRight);
                        padding.bottom = Math.max(padding.bottom, layer.insetBottom);
                    }
                    hasPadding = true;
                }
            }
        }
        return hasPadding;
    }

    // ── Alpha ────────────────────────────────────────────────────────────────

    @Override
    public int getAlpha() { return alpha; }

    @Override
    public void setAlpha(int alpha) { this.alpha = alpha & 0xFF; }

    // ── Draw — iterates layers with insets applied ───────────────────────────

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) return;
        Rect b = getBounds();
        for (Layer layer : layers) {
            if (layer.drawable != null) {
                layer.drawable.setBounds(
                    b.left + layer.insetLeft,
                    b.top + layer.insetTop,
                    b.right - layer.insetRight,
                    b.bottom - layer.insetBottom);
                layer.drawable.draw(canvas);
            }
        }
    }

    // ── Intrinsic size — maximum of all layers + their insets ────────────────

    @Override
    public int getIntrinsicWidth() {
        int width = -1;
        for (Layer layer : layers) {
            if (layer.drawable != null) {
                int lw = layer.drawable.getIntrinsicWidth();
                if (lw >= 0) {
                    lw += layer.insetLeft + layer.insetRight;
                    width = Math.max(width, lw);
                }
            }
        }
        return width;
    }

    @Override
    public int getIntrinsicHeight() {
        int height = -1;
        for (Layer layer : layers) {
            if (layer.drawable != null) {
                int lh = layer.drawable.getIntrinsicHeight();
                if (lh >= 0) {
                    lh += layer.insetTop + layer.insetBottom;
                    height = Math.max(height, lh);
                }
            }
        }
        return height;
    }

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
