package android.graphics.drawable.shapes;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Android-compatible PathShape shim. Draws a Path scaled to fit the shape's bounds.
 */
public class PathShape extends Shape {
    private Path mPath;
    private float mStdWidth;
    private float mStdHeight;

    /**
     * @param path      the Path to draw
     * @param stdWidth  the standard (design-time) width of the path
     * @param stdHeight the standard (design-time) height of the path
     */
    public PathShape(Path path, float stdWidth, float stdHeight) {
        mPath = path;
        mStdWidth = stdWidth;
        mStdHeight = stdHeight;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.save();
        canvas.scale(getWidth() / mStdWidth, getHeight() / mStdHeight);
        canvas.drawPath(mPath, paint);
        canvas.restore();
    }
}
