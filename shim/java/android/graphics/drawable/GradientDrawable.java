package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

// Field needed by View.java
// public static boolean sWrapNegativeAngleMeasurements = true;
import android.graphics.RectF;

/**
 * Shim: android.graphics.drawable.GradientDrawable
 * OH mapping: drawing shape primitives / gradient fills
 *
 * Draws rectangle, oval, line, ring shapes with solid color or gradient fill,
 * optional stroke, and corner radii. Extracted from AOSP draw() logic.
 */
public class GradientDrawable extends Drawable {
    public static boolean sWrapNegativeAngleMeasurements = true;

    // ── Shape constants ───────────────────────────────────────────────────────

    public static final int RECTANGLE = 0;
    public static final int OVAL      = 1;
    public static final int LINE      = 2;
    public static final int RING      = 3;

    // ── Gradient type constants ──────────────────────────────────────────────

    public static final int LINEAR_GRADIENT = 0;
    public static final int RADIAL_GRADIENT = 1;
    public static final int SWEEP_GRADIENT  = 2;

    // ── Orientation enum ─────────────────────────────────────────────────────

    public enum Orientation {
        TOP_BOTTOM,
        TR_BL,
        RIGHT_LEFT,
        BR_TL,
        BOTTOM_TOP,
        BL_TR,
        LEFT_RIGHT,
        TL_BR
    }

    // ── State ────────────────────────────────────────────────────────────────

    private int         shape         = RECTANGLE;
    private int         gradientType  = LINEAR_GRADIENT;
    private int         solidColor    = 0;
    private int[]       colors        = null;
    private float       cornerRadius  = 0f;
    private float[]     cornerRadii   = null;
    private int         strokeColor   = 0;
    private float       strokeWidth   = 0f;
    private float       strokeDashWidth = 0f;
    private float       strokeDashGap   = 0f;
    private int         alpha         = 0xFF;
    private Orientation orientation   = Orientation.TOP_BOTTOM;
    private Rect        padding       = null;
    private float       gradientRadius = 0.5f;
    private boolean     useLevel      = false;
    private android.graphics.drawable.GradientDrawable.GradientDrawable_Size size = null;

    // Inner helper for intrinsic size
    private static class GradientDrawable_Size {
        int width = -1;
        int height = -1;
    }

    // ── Constructors ─────────────────────────────────────────────────────────

    public GradientDrawable() {}

    public GradientDrawable(Orientation orientation, int[] colors) {
        this.orientation = orientation;
        this.colors      = colors != null ? colors.clone() : null;
    }

    // ── Shape ────────────────────────────────────────────────────────────────

    public int getShape() { return shape; }

    public void setShape(int shape) { this.shape = shape; }

    // ── Color / fill ─────────────────────────────────────────────────────────

    public void setColor(int color) {
        this.solidColor = color;
        this.colors     = null; // override gradient
    }

    public int getColor() { return solidColor; }

    public void setColors(int[] colors) {
        this.colors = colors != null ? colors.clone() : null;
    }

    public int[] getColors() {
        return colors != null ? colors.clone() : null;
    }

    // ── Gradient type ────────────────────────────────────────────────────────

    public void setGradientType(int gradientType) { this.gradientType = gradientType; }

    public int getGradientType() { return gradientType; }

    public void setGradientRadius(float gradientRadius) { this.gradientRadius = gradientRadius; }

    public float getGradientRadius() { return gradientRadius; }

    // ── Orientation ──────────────────────────────────────────────────────────

    public Orientation getOrientation() { return orientation; }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    // ── Corner radius ────────────────────────────────────────────────────────

    public void setCornerRadius(float radius) {
        this.cornerRadius = radius;
        this.cornerRadii  = null;
    }

    /**
     * Sets corner radii for all four corners.
     * Array must have 8 elements: [topLeft-x, topLeft-y, topRight-x, topRight-y,
     * bottomRight-x, bottomRight-y, bottomLeft-x, bottomLeft-y].
     */
    public void setCornerRadii(float[] radii) {
        this.cornerRadii  = radii != null ? radii.clone() : null;
        this.cornerRadius = 0f;
    }

    public float getCornerRadius() { return cornerRadius; }

    public float[] getCornerRadii() {
        return cornerRadii != null ? cornerRadii.clone() : null;
    }

    // ── Stroke ───────────────────────────────────────────────────────────────

    public void setStroke(int width, int color) {
        setStroke(width, color, 0f, 0f);
    }

    public void setStroke(int width, int color, float dashWidth, float dashGap) {
        this.strokeWidth     = width;
        this.strokeColor     = color;
        this.strokeDashWidth = dashWidth;
        this.strokeDashGap   = dashGap;
    }

    // ── Size ─────────────────────────────────────────────────────────────────

    public void setSize(int width, int height) {
        if (size == null) size = new GradientDrawable_Size();
        size.width = width;
        size.height = height;
    }

    @Override
    public int getIntrinsicWidth() {
        return size != null ? size.width : -1;
    }

    @Override
    public int getIntrinsicHeight() {
        return size != null ? size.height : -1;
    }

    // ── Padding ──────────────────────────────────────────────────────────────

    public void setPadding(int left, int top, int right, int bottom) {
        if (padding == null) padding = new Rect();
        padding.set(left, top, right, bottom);
    }

    public boolean getPadding(Rect pad) {
        if (padding != null) {
            pad.set(padding);
            return true;
        }
        if (strokeWidth > 0) {
            int sw = (int) Math.ceil(strokeWidth);
            pad.set(sw, sw, sw, sw);
            return true;
        }
        return false;
    }

    // ── UseLevel ─────────────────────────────────────────────────────────────

    public boolean getUseLevel() { return useLevel; }

    public void setUseLevel(boolean useLevel) { this.useLevel = useLevel; }

    // ── Alpha ────────────────────────────────────────────────────────────────

    @Override
    public int getAlpha() { return alpha; }

    @Override
    public void setAlpha(int alpha) { this.alpha = alpha & 0xFF; }

    // ── Draw — extracted from AOSP GradientDrawable.draw() ──────────────────

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) return;
        Rect b = getBounds();
        if (b.width() <= 0 || b.height() <= 0) return;

        // Determine fill color — for gradient, use first color; for solid, use solidColor
        int fillColor = (colors != null && colors.length > 0) ? colors[0] : solidColor;

        // Apply alpha modulation
        int fillAlpha = (fillColor >>> 24);
        int modulatedFillAlpha = (fillAlpha * alpha) / 255;
        int modulatedFillColor = (fillColor & 0x00FFFFFF) | (modulatedFillAlpha << 24);

        Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(modulatedFillColor);
        fillPaint.setStyle(Paint.Style.FILL);

        boolean haveFill = modulatedFillAlpha > 0;
        boolean haveStroke = strokeWidth > 0 && ((strokeColor >>> 24) > 0);

        float l = b.left, t = b.top, r = b.right, bt = b.bottom;

        // Compute corner radius, clamped to half the smallest dimension (AOSP behavior)
        float rad = cornerRadius;
        if (cornerRadii != null && cornerRadii.length >= 2) rad = cornerRadii[0];
        if (rad > 0) {
            float maxRad = Math.min(b.width(), b.height()) * 0.5f;
            rad = Math.min(rad, maxRad);
        }

        // Draw fill
        if (haveFill) {
            switch (shape) {
                case OVAL:
                    canvas.drawOval(l, t, r, bt, fillPaint);
                    break;
                case LINE:
                    // LINE shape only draws a stroke, no fill
                    break;
                case RING:
                    // Simplified ring: draw oval stroke
                    float cx = (l + r) / 2f;
                    float cy = (t + bt) / 2f;
                    float ringRadius = Math.min(r - l, bt - t) / 3f;
                    Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    ringPaint.setColor(modulatedFillColor);
                    ringPaint.setStyle(Paint.Style.STROKE);
                    ringPaint.setStrokeWidth(ringRadius * 0.5f);
                    canvas.drawCircle(cx, cy, ringRadius, ringPaint);
                    break;
                default: // RECTANGLE
                    if (rad > 0) {
                        canvas.drawRoundRect(l, t, r, bt, rad, rad, fillPaint);
                    } else {
                        canvas.drawRect(l, t, r, bt, fillPaint);
                    }
                    break;
            }
        }

        // Draw stroke
        if (haveStroke) {
            int strokeAlpha = (strokeColor >>> 24);
            int modulatedStrokeAlpha = (strokeAlpha * alpha) / 255;
            int modulatedStrokeColor = (strokeColor & 0x00FFFFFF) | (modulatedStrokeAlpha << 24);

            Paint sp = new Paint(Paint.ANTI_ALIAS_FLAG);
            sp.setColor(modulatedStrokeColor);
            sp.setStyle(Paint.Style.STROKE);
            sp.setStrokeWidth(strokeWidth);

            switch (shape) {
                case OVAL:
                    canvas.drawOval(l, t, r, bt, sp);
                    break;
                case LINE: {
                    // AOSP: draw a horizontal line at the vertical center
                    float y = (t + bt) / 2f;
                    canvas.drawLine(l, y, r, y, sp);
                    break;
                }
                case RING:
                    // Ring stroke already handled above
                    break;
                default: // RECTANGLE
                    if (rad > 0) {
                        canvas.drawRoundRect(l, t, r, bt, rad, rad, sp);
                    } else {
                        canvas.drawRect(l, t, r, bt, sp);
                    }
                    break;
            }
        }
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "GradientDrawable(shape=" + shape
             + ", gradientType=" + gradientType
             + ", solidColor=0x" + Integer.toHexString(solidColor)
             + ", cornerRadius=" + cornerRadius + ")";
    }
}
