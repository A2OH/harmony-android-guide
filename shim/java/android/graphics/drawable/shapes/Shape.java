package android.graphics.drawable.shapes;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Android-compatible Shape shim. Abstract base class for shapes that can be drawn.
 */
public class Shape {
    private float mWidth;
    private float mHeight;

    public void draw(Canvas canvas, Paint paint) {}

    public float getWidth() { return mWidth; }
    public float getHeight() { return mHeight; }

    public void resize(float width, float height) {
        if (width < 0) width = 0;
        if (height < 0) height = 0;
        if (mWidth != width || mHeight != height) {
            mWidth = width;
            mHeight = height;
            onResize(width, height);
        }
    }

    protected void onResize(float width, float height) {}

    @Override
    public Shape clone() throws CloneNotSupportedException {
        return (Shape) super.clone();
    }
}
