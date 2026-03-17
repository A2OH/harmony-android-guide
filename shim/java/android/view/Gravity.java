package android.view;

/**
 * Shim: android.view.Gravity constants.
 */
public class Gravity {
    public static final int NO_GRAVITY = 0x0000;
    public static final int AXIS_SPECIFIED = 0x0001;
    public static final int AXIS_PULL_BEFORE = 0x0002;
    public static final int AXIS_PULL_AFTER = 0x0004;
    public static final int AXIS_CLIP = 0x0008;
    public static final int AXIS_X_SHIFT = 0;
    public static final int AXIS_Y_SHIFT = 4;
    public static final int TOP = (AXIS_PULL_BEFORE | AXIS_SPECIFIED) << AXIS_Y_SHIFT;
    public static final int BOTTOM = (AXIS_PULL_AFTER | AXIS_SPECIFIED) << AXIS_Y_SHIFT;
    public static final int LEFT = (AXIS_PULL_BEFORE | AXIS_SPECIFIED) << AXIS_X_SHIFT;
    public static final int RIGHT = (AXIS_PULL_AFTER | AXIS_SPECIFIED) << AXIS_X_SHIFT;
    public static final int CENTER_VERTICAL = AXIS_SPECIFIED << AXIS_Y_SHIFT;
    public static final int CENTER_HORIZONTAL = AXIS_SPECIFIED << AXIS_X_SHIFT;
    public static final int CENTER = CENTER_VERTICAL | CENTER_HORIZONTAL;
    public static final int FILL_VERTICAL = TOP | BOTTOM;
    public static final int FILL_HORIZONTAL = LEFT | RIGHT;
    public static final int FILL = FILL_VERTICAL | FILL_HORIZONTAL;
    public static final int START = 0x00800003;
    public static final int END = 0x00800005;

    public static final int HORIZONTAL_GRAVITY_MASK = (AXIS_SPECIFIED | AXIS_PULL_BEFORE | AXIS_PULL_AFTER) << AXIS_X_SHIFT;
    public static final int VERTICAL_GRAVITY_MASK = (AXIS_SPECIFIED | AXIS_PULL_BEFORE | AXIS_PULL_AFTER) << AXIS_Y_SHIFT;

    /** Mask for START/END relative bits. */
    public static final int RELATIVE_HORIZONTAL_GRAVITY_MASK = START | END;

    public static final int DISPLAY_CLIP_VERTICAL = 0x10000000;
    public static final int DISPLAY_CLIP_HORIZONTAL = 0x01000000;
    public static final int CLIP_VERTICAL = AXIS_CLIP << AXIS_Y_SHIFT;
    public static final int CLIP_HORIZONTAL = AXIS_CLIP << AXIS_X_SHIFT;

    /**
     * Convert START/END to LEFT/RIGHT based on layout direction.
     * For our shim, we always assume LTR (layoutDirection == 0).
     */
    public static int getAbsoluteGravity(int gravity, int layoutDirection) {
        int result = gravity;
        // If gravity uses relative bits (START/END), convert to LEFT/RIGHT
        if ((gravity & 0x00800000) != 0) {
            // Has relative horizontal component
            int relBits = gravity & RELATIVE_HORIZONTAL_GRAVITY_MASK;
            // Clear relative bits from result
            result = (result & ~RELATIVE_HORIZONTAL_GRAVITY_MASK);
            if (layoutDirection == 1) { // RTL
                if (relBits == START) {
                    result |= RIGHT;
                } else if (relBits == END) {
                    result |= LEFT;
                }
            } else { // LTR
                if (relBits == START) {
                    result |= LEFT;
                } else if (relBits == END) {
                    result |= RIGHT;
                }
            }
        }
        return result;
    }

    /**
     * Apply gravity to place an object within a container.
     */
    public static void apply(int gravity, int w, int h,
            android.graphics.Rect container, android.graphics.Rect outRect) {
        apply(gravity, w, h, container, 0, 0, outRect);
    }

    public static void apply(int gravity, int w, int h,
            android.graphics.Rect container, int xAdj, int yAdj,
            android.graphics.Rect outRect) {
        switch (gravity & ((AXIS_PULL_BEFORE | AXIS_PULL_AFTER) << AXIS_X_SHIFT)) {
            case 0:
                outRect.left = container.left + ((container.right - container.left - w) / 2) + xAdj;
                outRect.right = outRect.left + w;
                if ((gravity & (AXIS_CLIP << AXIS_X_SHIFT)) == (AXIS_CLIP << AXIS_X_SHIFT)) {
                    if (outRect.left < container.left) outRect.left = container.left;
                    if (outRect.right > container.right) outRect.right = container.right;
                }
                break;
            case AXIS_PULL_BEFORE << AXIS_X_SHIFT:
                outRect.left = container.left + xAdj;
                outRect.right = outRect.left + w;
                if ((gravity & (AXIS_CLIP << AXIS_X_SHIFT)) == (AXIS_CLIP << AXIS_X_SHIFT)) {
                    if (outRect.right > container.right) outRect.right = container.right;
                }
                break;
            case AXIS_PULL_AFTER << AXIS_X_SHIFT:
                outRect.right = container.right - xAdj;
                outRect.left = outRect.right - w;
                if ((gravity & (AXIS_CLIP << AXIS_X_SHIFT)) == (AXIS_CLIP << AXIS_X_SHIFT)) {
                    if (outRect.left < container.left) outRect.left = container.left;
                }
                break;
            default:
                outRect.left = container.left + xAdj;
                outRect.right = container.right - xAdj;
                break;
        }

        switch (gravity & ((AXIS_PULL_BEFORE | AXIS_PULL_AFTER) << AXIS_Y_SHIFT)) {
            case 0:
                outRect.top = container.top + ((container.bottom - container.top - h) / 2) + yAdj;
                outRect.bottom = outRect.top + h;
                if ((gravity & (AXIS_CLIP << AXIS_Y_SHIFT)) == (AXIS_CLIP << AXIS_Y_SHIFT)) {
                    if (outRect.top < container.top) outRect.top = container.top;
                    if (outRect.bottom > container.bottom) outRect.bottom = container.bottom;
                }
                break;
            case AXIS_PULL_BEFORE << AXIS_Y_SHIFT:
                outRect.top = container.top + yAdj;
                outRect.bottom = outRect.top + h;
                if ((gravity & (AXIS_CLIP << AXIS_Y_SHIFT)) == (AXIS_CLIP << AXIS_Y_SHIFT)) {
                    if (outRect.bottom > container.bottom) outRect.bottom = container.bottom;
                }
                break;
            case AXIS_PULL_AFTER << AXIS_Y_SHIFT:
                outRect.bottom = container.bottom - yAdj;
                outRect.top = outRect.bottom - h;
                if ((gravity & (AXIS_CLIP << AXIS_Y_SHIFT)) == (AXIS_CLIP << AXIS_Y_SHIFT)) {
                    if (outRect.top < container.top) outRect.top = container.top;
                }
                break;
            default:
                outRect.top = container.top + yAdj;
                outRect.bottom = container.bottom - yAdj;
                break;
        }
    }
}
