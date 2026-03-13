package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.shapes.Shape;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.shapes.Shape;

import android.graphics.Canvas;

/**
 * Shim: android.graphics.drawable.GradientDrawable
 * OH mapping: drawing shape primitives / gradient fills
 *
 * Pure Java stub — stores shape, gradient, and stroke parameters;
 * draw() is a no-op.
 */
public class GradientDrawable extends Drawable {

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

    public void setColors(int[] colors) {
        this.colors = colors != null ? colors.clone() : null;
    }

    // ── Gradient type ────────────────────────────────────────────────────────

    public void setGradientType(int gradientType) { this.gradientType = gradientType; }

    public int getGradientType() { return gradientType; }

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

    // ── Stroke ───────────────────────────────────────────────────────────────

    public void setStroke(int width, int color) {
// FIXME:         setStroke(width, color, 0f);
    }

    public void setStroke(int width, int color, float dashWidth, float dashGap) {
        this.strokeWidth     = width;
        this.strokeColor     = color;
        this.strokeDashWidth = dashWidth;
        this.strokeDashGap   = dashGap;
    }

    // ── Alpha ────────────────────────────────────────────────────────────────

    @Override
    public int getAlpha() { return alpha; }

    @Override
    public void setAlpha(int alpha) { this.alpha = alpha & 0xFF; }

    // ── Draw (no-op stub) ────────────────────────────────────────────────────

    @Override
    public void draw(Canvas canvas) { /* no-op */ }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "GradientDrawable(shape=" + shape
             + ", gradientType=" + gradientType
             + ", solidColor=0x" + Integer.toHexString(solidColor)
             + ", cornerRadius=" + cornerRadius + ")";
    }
}
