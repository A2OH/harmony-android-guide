package android.graphics.drawable.shapes;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Android-compatible RoundRectShape shim. Draws a rounded rectangle.
 * The outer radii array has 8 values (4 corners x 2 for x,y radius).
 * The inset specifies an inner rounded rectangle to subtract.
 * The inner radii array has 8 values for the inner rectangle corners.
 */
public class RoundRectShape extends Shape {
    private float[] mOuterRadii;
    private RectF mInset;
    private float[] mInnerRadii;

    /**
     * @param outerRadii 8 float values for outer corner radii (may be null for sharp corners)
     * @param inset      inset rectangle for the inner shape (may be null)
     * @param innerRadii 8 float values for inner corner radii (may be null)
     */
    public RoundRectShape(float[] outerRadii, RectF inset, float[] innerRadii) {
        if (outerRadii != null && outerRadii.length < 8) {
            throw new ArrayIndexOutOfBoundsException("outerRadii must have >= 8 values");
        }
        if (innerRadii != null && innerRadii.length < 8) {
            throw new ArrayIndexOutOfBoundsException("innerRadii must have >= 8 values");
        }
        mOuterRadii = outerRadii;
        mInset = inset;
        mInnerRadii = innerRadii;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        float rx = mOuterRadii != null ? mOuterRadii[0] : 0;
        float ry = mOuterRadii != null ? mOuterRadii[1] : 0;
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), rx, ry, paint);
    }
}
