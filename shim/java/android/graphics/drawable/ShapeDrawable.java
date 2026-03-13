package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Shim: android.graphics.drawable.ShapeDrawable
 * OH mapping: path/shape primitive rendering
 *
 * Pure Java stub — stores a shape reference and paint; draw() is a no-op.
 */
public class ShapeDrawable extends Drawable {

    // ── State ────────────────────────────────────────────────────────────────

    private android.graphics.drawable.shapes.Shape shape;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int   intrinsicWidth  = -1;
    private int   intrinsicHeight = -1;
    private int   alpha           = 0xFF;

    // ── Constructors ─────────────────────────────────────────────────────────

    public ShapeDrawable() {}

    public ShapeDrawable(android.graphics.drawable.shapes.Shape shape) {
        this.shape = shape;
    }

    // ── Shape ────────────────────────────────────────────────────────────────

    public android.graphics.drawable.shapes.Shape getShape() { return shape; }

    public void setShape(android.graphics.drawable.shapes.Shape shape) { this.shape = shape; }

    // ── Paint ────────────────────────────────────────────────────────────────

    /**
     * Returns the mutable Paint used to draw the shape.
     * Callers may modify it directly (e.g., setColor, setStyle).
     */
    public Paint getPaint() { return paint; }

    // ── Intrinsic size ───────────────────────────────────────────────────────

    @Override
    public int getIntrinsicWidth()  { return intrinsicWidth; }

    @Override
    public int getIntrinsicHeight() { return intrinsicHeight; }

    public void setIntrinsicWidth(int width)   { this.intrinsicWidth  = width; }
    public void setIntrinsicHeight(int height) { this.intrinsicHeight = height; }

    // ── Alpha ────────────────────────────────────────────────────────────────

    @Override
    public int getAlpha() { return alpha; }

    @Override
    public void setAlpha(int alpha) {
        this.alpha = alpha & 0xFF;
        paint.setAlpha(this.alpha);
    }

    // ── Draw (no-op stub) ────────────────────────────────────────────────────

    @Override
    public void draw(Canvas canvas) { /* no-op */ }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "ShapeDrawable(shape=" + shape + ", paint=" + paint + ")";
    }
}
