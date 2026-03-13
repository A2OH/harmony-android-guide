package android.graphics.drawable;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.content.res.ColorStateList;
import android.graphics.Canvas;

import android.content.res.ColorStateList;
import android.graphics.Canvas;

/**
 * Shim: android.graphics.drawable.RippleDrawable
 * OH mapping: touch feedback ripple effect via ArkUI gesture response
 *
 * Pure Java stub — stores ripple color, content, and mask layers;
 * actual ripple animation is handled by the OH rendering pipeline.
 */
public class RippleDrawable extends LayerDrawable {

    // ── State ────────────────────────────────────────────────────────────────

    private ColorStateList color;
    private Drawable       content;
    private Drawable       mask;

    // ── Constructors ─────────────────────────────────────────────────────────

    /**
     * @param color   ripple colour (may not be null)
     * @param content background drawable shown behind the ripple (may be null)
     * @param mask    drawable used to clip the ripple (may be null)
     */
    public RippleDrawable(ColorStateList color, Drawable content, Drawable mask) {
        super(buildLayers(content, mask));
        if (color == null) throw new IllegalArgumentException("color must not be null");
        this.color   = color;
        this.content = content;
        this.mask    = mask;
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    public ColorStateList getColor() { return color; }

    public void setColor(ColorStateList color) {
        if (color == null) throw new IllegalArgumentException("color must not be null");
        this.color = color;
    }

    // ── Draw (no-op stub — ripple is a platform effect) ──────────────────────

    @Override
    public void draw(Canvas canvas) {
        if (content != null) content.draw(canvas);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static Drawable[] buildLayers(Drawable content, Drawable mask) {
        // Count non-null layers
        int count = 0;
        if (content != null) count++;
        if (mask    != null) count++;
        Drawable[] arr = new Drawable[count];
        int i = 0;
        if (content != null) arr[i++] = content;
        if (mask    != null) arr[i]   = mask;
        return arr;
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "RippleDrawable(color=" + color
             + ", content=" + content
             + ", mask=" + mask + ")";
    }
}
