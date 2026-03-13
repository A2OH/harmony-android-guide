package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Android-compatible ArcShape shim. Draws an arc.
 */
public class ArcShape extends Shape {
    private float mStartAngle;
    private float mSweepAngle;

    /**
     * @param startAngle starting angle (in degrees) of the arc
     * @param sweepAngle sweep angle (in degrees) measured clockwise
     */
    public ArcShape(float startAngle, float sweepAngle) {
        mStartAngle = startAngle;
        mSweepAngle = sweepAngle;
    }

    public float getStartAngle() { return mStartAngle; }
    public float getSweepAngle() { return mSweepAngle; }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawArc(0, 0, getWidth(), getHeight(), mStartAngle, mSweepAngle, true, paint);
    }
}
