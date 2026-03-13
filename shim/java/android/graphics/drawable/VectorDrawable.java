package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.Canvas;

import android.graphics.Canvas;

/**
 * Shim: android.graphics.drawable.VectorDrawable
 * OH mapping: SVG-style vector graphics via OH Canvas path API
 *
 * Stub implementation — no vector parsing or rendering is performed.
 * API surface is sufficient to satisfy type references in migrated code.
 */
public class VectorDrawable extends Drawable {

    // ── State ────────────────────────────────────────────────────────────────

    private int   intrinsicWidth  = -1;
    private int   intrinsicHeight = -1;
    private float alpha           = 1.0f;

    // ── Constructors ─────────────────────────────────────────────────────────

    public VectorDrawable() {}

    // ── Intrinsic size ───────────────────────────────────────────────────────

    @Override
    public int getIntrinsicWidth()  { return intrinsicWidth; }

    @Override
    public int getIntrinsicHeight() { return intrinsicHeight; }

    // ── Alpha ────────────────────────────────────────────────────────────────

    @Override
    public int getAlpha() { return (int) (alpha * 255 + 0.5f); }

    @Override
    public void setAlpha(int alpha) { this.alpha = (alpha & 0xFF) / 255.0f; }

    /** Returns the float alpha in [0.0, 1.0]. */
    public float getAlphaFloat() { return alpha; }

    /** Sets alpha as a float in [0.0, 1.0]. */
    public void setAlphaFloat(float alpha) {
        this.alpha = Math.max(0f, Math.min(1f, alpha));
    }

    // ── Draw (no-op stub) ────────────────────────────────────────────────────

    @Override
    public void draw(Canvas canvas) { /* no-op */ }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "VectorDrawable(size=" + intrinsicWidth + "x" + intrinsicHeight + ")";
    }
}
