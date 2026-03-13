package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Android-compatible RectShape shim. Draws a rectangle.
 */
public class RectShape extends Shape {
    public RectShape() {}

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }
}
