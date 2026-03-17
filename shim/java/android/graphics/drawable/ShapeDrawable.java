package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.shapes.Shape;

/**
 * Shim: android.graphics.drawable.ShapeDrawable
 * OH mapping: path/shape primitive rendering
 *
 * Draws a Shape object using the configured Paint. The shape is resized to
 * match the drawable bounds and drawn with canvas translation (AOSP behavior).
 */
public class ShapeDrawable extends Drawable {

    // ── State ────────────────────────────────────────────────────────────────

    private Shape shape;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int   intrinsicWidth  = -1;
    private int   intrinsicHeight = -1;
    private int   alpha           = 0xFF;
    private Rect  padding         = null;

    // ── Constructors ─────────────────────────────────────────────────────────

    public ShapeDrawable() {}

    public ShapeDrawable(Shape shape) {
        this.shape = shape;
    }

    // ── Shape ────────────────────────────────────────────────────────────────

    public Shape getShape() { return shape; }

    public void setShape(Shape shape) { this.shape = shape; }

    // ── Paint ────────────────────────────────────────────────────────────────

    /**
     * Returns the mutable Paint used to draw the shape.
     * Callers may modify it directly (e.g., setColor, setStyle).
     */
    public Paint getPaint() { return paint; }

    // ── Padding ──────────────────────────────────────────────────────────────

    public void setPadding(int left, int top, int right, int bottom) {
        if (padding == null) padding = new Rect();
        padding.set(left, top, right, bottom);
    }

    public void setPadding(Rect pad) {
        if (pad != null) {
            if (padding == null) padding = new Rect();
            padding.set(pad);
        } else {
            padding = null;
        }
    }

    public boolean getPadding(Rect pad) {
        if (padding != null) {
            pad.set(padding);
            return true;
        }
        return false;
    }

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

    // ── Draw — resizes shape to bounds, translates, then delegates ───────────

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) return;
        Rect b = getBounds();
        if (b.width() <= 0 || b.height() <= 0) return;

        // Apply padding to determine the drawing area
        int pl = 0, pt = 0, pr = 0, pb = 0;
        if (padding != null) {
            pl = padding.left;
            pt = padding.top;
            pr = padding.right;
            pb = padding.bottom;
        }

        float drawWidth = b.width() - pl - pr;
        float drawHeight = b.height() - pt - pb;
        if (drawWidth <= 0 || drawHeight <= 0) return;

        if (shape != null) {
            // Resize the shape to fit the available area (AOSP behavior)
            shape.resize(drawWidth, drawHeight);
            // Translate canvas to the top-left of the draw area
            canvas.save();
            canvas.translate(b.left + pl, b.top + pt);
            shape.draw(canvas, paint);
            canvas.restore();
        } else {
            // No shape set — draw a filled rectangle as fallback
            canvas.drawRect(b.left + pl, b.top + pt,
                    b.right - pr, b.bottom - pb, paint);
        }
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "ShapeDrawable(shape=" + shape + ", paint=" + paint + ")";
    }
}
