package android.graphics;

/**
 * Shim: android.graphics.Canvas
 * OH mapping: drawing.OH_Drawing_Canvas
 *
 * Pure Java stub — all draw calls are no-ops.
 * The save/restore stack depth is tracked so that callers that rely on
 * the integer save-count returned by save() can function correctly.
 */
public class Canvas {

    private final Bitmap bitmap;
    private int saveDepth = 0;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Canvas() {
        this.bitmap = null;
    }

    public Canvas(Bitmap bitmap) {
        if (bitmap == null) throw new NullPointerException("bitmap must not be null");
        this.bitmap = bitmap;
    }

    // ── Dimensions ───────────────────────────────────────────────────────────

    public int getWidth()  { return (bitmap != null) ? bitmap.getWidth()  : 0; }
    public int getHeight() { return (bitmap != null) ? bitmap.getHeight() : 0; }

    // ── Draw operations (all no-ops in this shim) ────────────────────────────

    public void drawColor(int color) { /* no-op */ }

    public void drawRect(float left, float top, float right, float bottom, Paint paint) { /* no-op */ }
    public void drawRect(Rect r, Paint paint)  { /* no-op */ }
    public void drawRect(RectF r, Paint paint) { /* no-op */ }

    public void drawCircle(float cx, float cy, float radius, Paint paint) { /* no-op */ }

    public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) { /* no-op */ }

    public void drawText(String text, float x, float y, Paint paint) { /* no-op */ }

    public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) { /* no-op */ }

    public void drawRoundRect(float left, float top, float right, float bottom,
                              float rx, float ry, Paint paint) { /* no-op */ }
    public void drawRoundRect(RectF rect, float rx, float ry, Paint paint) { /* no-op */ }

    public void drawArc(float left, float top, float right, float bottom,
                        float startAngle, float sweepAngle, boolean useCenter, Paint paint) { /* no-op */ }
    public void drawArc(RectF oval, float startAngle, float sweepAngle,
                        boolean useCenter, Paint paint) { /* no-op */ }

    public void drawOval(float left, float top, float right, float bottom, Paint paint) { /* no-op */ }
    public void drawOval(RectF oval, Paint paint) { /* no-op */ }

    public void drawPath(Path path, Paint paint) { /* no-op */ }

    public void drawPicture(Picture picture) { /* no-op */ }

    // ── Transform stack ──────────────────────────────────────────────────────

    /**
     * Saves the current matrix and clip onto a private stack.
     * @return the depth of the save stack just before this call (1-based after return)
     */
    public int save() {
        return ++saveDepth;
    }

    /**
     * Pops the most recently saved state. Balances a preceding save().
     */
    public void restore() {
        if (saveDepth > 0) saveDepth--;
    }

    /** Returns the number of matrix/clip states on the save stack. */
    public int getSaveCount() { return saveDepth; }

    public void translate(float dx, float dy)  { /* no-op */ }
    public void scale(float sx, float sy)      { /* no-op */ }
    public void rotate(float degrees)          { /* no-op */ }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Canvas(" + getWidth() + "x" + getHeight()
             + ", saveDepth=" + saveDepth + ")";
    }
}
