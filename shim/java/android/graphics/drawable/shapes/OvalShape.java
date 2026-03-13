package android.graphics.drawable.shapes;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Android-compatible OvalShape shim. Draws an oval.
 */
public class OvalShape extends Shape {
    public OvalShape() {}

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawOval(0, 0, getWidth(), getHeight(), paint);
    }
}
