package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.Canvas;
import android.graphics.drawable.shapes.RoundRectShape;
import java.util.Set;

import android.graphics.Canvas;
import android.graphics.drawable.shapes.RoundRectShape;

/**
 * Android-compatible PaintDrawable shim. A ShapeDrawable that defaults to a rectangle
 * and supports corner radii and shadow layers.
 */
public class PaintDrawable extends ShapeDrawable {
    private float mCornerRadius;

    public PaintDrawable() {
        super();
    }

    public PaintDrawable(int color) {
        super();
        getPaint().setColor(color);
    }

    /**
     * Set the shadow layer for the pa(int used to draw this drawable.
     */
    public void setShadowLayer(float radius, float dx, float dy, int shadowColor) {
        getPaint().setShadowLayer(radius, dx, dy, shadowColor);
        invalidateSelf();
    }

    /**
     * Set the corner radius for all corners.
     * @param radius corner radius in pixels; 0 for sharp corners
     */
    public void setCornerRadius(float radius) {
        mCornerRadius = radius;
        float[] radii = null;
        if (radius > 0) {
            radii = new float[] { radius, radius, radius, radius,
                                  radius, radius, radius, radius };
        }
        setShape(new RoundRectShape(radii, null, null));
        invalidateSelf();
    }

    /**
     * Set the corner radii for each corner individually.
     * @param radii array of 8 float values (4 corners x 2), or null for sharp corners
     */
    public void setCornerRadii(float[] radii) {
        if (radii == null) {
            setShape(null);
        } else {
            setShape(new RoundRectShape(radii, null, null));
        }
        invalidateSelf();
    }
}
