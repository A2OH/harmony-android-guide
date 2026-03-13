package android.view.animation;

/**
 * Android shim: android.view.animation.PathInterpolator
 *
 * An interpolator that can traverse a {@code Path} that extends from
 * {@code Point(0, 0)} to {@code Point(1, 1)}. The x coordinate along the
 * {@code Path} is the input value and the output is the y coordinate of the
 * line at that point.
 *
 * Because android.graphics.Path is not available in this shim set, the
 * Path-based constructor accepts {@code Object} as a placeholder. The two-
 * and four-float constructors implement cubic Bezier evaluation directly.
 */
public class PathInterpolator implements Interpolator {

    // Control points for the cubic Bezier (two-point form stores P1 and P2).
    private final float mX1;
    private final float mY1;
    private final float mX2;
    private final float mY2;

    // Whether this instance was created from a Path object (stub behaviour).
    private final boolean mFromPath;

    /**
     * Creates a PathInterpolator from a Path object.
     * <p>
     * NOTE: {@code android.graphics.Path} is not shimmed; this constructor
     * is provided for source compatibility only. The interpolator will fall
     * back to linear behaviour at runtime.
     *
     * @param path the Path to use (accepted as Object for shim compatibility)
     */
    public PathInterpolator(Object path) {
        mFromPath = true;
        mX1 = 0.0f;
        mY1 = 0.0f;
        mX2 = 1.0f;
        mY2 = 1.0f;
    }

    /**
     * Creates a PathInterpolator for a quadratic Bezier curve. Equivalent to
     * specifying the single control point {@code (controlX, controlY)}.
     *
     * @param controlX the x coordinate of the quadratic control point
     * @param controlY the y coordinate of the quadratic control point
     */
    public PathInterpolator(float controlX, float controlY) {
        // Convert quadratic to cubic: P1 = (2/3)*control, P2 = (1+2/3*control)/2 approx.
        // More precisely, for a quadratic Bezier Q(t) = (1-t)^2*P0 + 2t(1-t)*Pc + t^2*P1
        // expressed as cubic: C1 = (2/3)*Pc, C2 = (1/3)*(P1 + 2*Pc)
        mX1 = 2.0f / 3.0f * controlX;
        mY1 = 2.0f / 3.0f * controlY;
        mX2 = (1.0f + 2.0f / 3.0f * controlX);
        mY2 = (1.0f + 2.0f / 3.0f * controlY);
        mFromPath = false;
    }

    /**
     * Creates a PathInterpolator for a cubic Bezier curve. The start and end
     * points are implicitly {@code (0,0)} and {@code (1,1)}; this constructor
     * accepts the two inner control points.
     *
     * @param controlX1 the x coordinate of the first control point
     * @param controlY1 the y coordinate of the first control point
     * @param controlX2 the x coordinate of the second control point
     * @param controlY2 the y coordinate of the second control point
     */
    public PathInterpolator(float controlX1, float controlY1,
                            float controlX2, float controlY2) {
        mX1 = controlX1;
        mY1 = controlY1;
        mX2 = controlX2;
        mY2 = controlY2;
        mFromPath = false;
    }

    /**
     * Using the cubic Bezier defined by the control points, solves for the
     * y value at the given x using Newton-Raphson iteration on the parametric
     * x equation, then evaluates parametric y.
     */
    @Override
    public float getInterpolation(float input) {
        if (mFromPath) {
            // Path-based constructor: fall back to linear (shim limitation).
            return input;
        }
        return bezierY(solveBezierX(input));
    }

    /**
     * Solves for the Bezier parameter {@code t} such that {@code Bx(t) == x}
     * using Newton-Raphson iteration.
     */
    private float solveBezierX(float x) {
        float t = x; // initial guess
        for (int i = 0; i < 8; i++) {
            float currentX = bezierX(t) - x;
            if (Math.abs(currentX) < 1e-6f) {
                break;
            }
            float derivative = bezierXDerivative(t);
            if (Math.abs(derivative) < 1e-6f) {
                break;
            }
            t -= currentX / derivative;
        }
        // Clamp to [0, 1] to guard against floating-point drift.
        return Math.max(0.0f, Math.min(1.0f, t));
    }

    /** Evaluates the x component of the cubic Bezier at parameter {@code t}. */
    private float bezierX(float t) {
        float mt = 1.0f - t;
        return 3.0f * mt * mt * t * mX1
             + 3.0f * mt * t * t * mX2
             + t * t * t;
    }

    /** Evaluates the y component of the cubic Bezier at parameter {@code t}. */
    private float bezierY(float t) {
        float mt = 1.0f - t;
        return 3.0f * mt * mt * t * mY1
             + 3.0f * mt * t * t * mY2
             + t * t * t;
    }

    /** Evaluates d/dt of the x Bezier (used by Newton-Raphson). */
    private float bezierXDerivative(float t) {
        float mt = 1.0f - t;
        return 3.0f * mt * mt * mX1
             + 6.0f * mt * t * (mX2 - mX1)
             + 3.0f * t * t * (1.0f - mX2);
    }
}
