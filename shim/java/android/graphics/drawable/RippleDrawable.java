package android.graphics.drawable;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Shim: android.graphics.drawable.RippleDrawable
 * OH mapping: touch feedback ripple effect via ArkUI gesture response
 *
 * Draws the content layer, then overlays a semi-transparent ripple color
 * when the drawable is in a pressed state. The mask layer clips the ripple
 * area. Full animation is not implemented — just the static pressed state.
 */
public class RippleDrawable extends LayerDrawable {

    // ── State ────────────────────────────────────────────────────────────────

    private ColorStateList color;
    private Drawable       content;
    private Drawable       mask;
    private int[]          currentState = new int[0];

    // ── Constructors ─────────────────────────────────────────────────────────

    /**
     * @param color   ripple colour (may not be null)
     * @param content background drawable shown behind the ripple (may be null)
     * @param mask    drawable used to clip the ripple (may be null)
     */
    public RippleDrawable(ColorStateList color, Drawable content, Drawable mask) {
        super(buildLayers(content, mask));
        if (color == null) throw new IllegalArgumentException("color must not be null");
        this.color   = color;
        this.content = content;
        this.mask    = mask;
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    public ColorStateList getColor() { return color; }

    public void setColor(ColorStateList color) {
        if (color == null) throw new IllegalArgumentException("color must not be null");
        this.color = color;
    }

    // ── State management ─────────────────────────────────────────────────────

    public boolean setState(int[] stateSet) {
        this.currentState = stateSet != null ? stateSet : new int[0];
        return true;
    }

    public int[] getState() {
        return currentState;
    }

    private boolean isPressed() {
        for (int s : currentState) {
            if (s == 16842919) return true; // android.R.attr.state_pressed
        }
        return false;
    }

    // ── Draw — content + ripple overlay when pressed ─────────────────────────

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) return;
        Rect b = getBounds();

        // Draw the content layer
        if (content != null) {
            content.setBounds(b);
            content.draw(canvas);
        }

        // Draw ripple overlay when pressed (static pressed state)
        if (isPressed()) {
            int rippleColor = color.getDefaultColor();
            // Make semi-transparent if fully opaque
            int rippleAlpha = (rippleColor >>> 24);
            if (rippleAlpha > 128) {
                rippleAlpha = 128;
                rippleColor = (rippleColor & 0x00FFFFFF) | (rippleAlpha << 24);
            }

            Paint ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            ripplePaint.setColor(rippleColor);
            ripplePaint.setStyle(Paint.Style.FILL);

            if (b.width() > 0 && b.height() > 0) {
                canvas.drawRect(b.left, b.top, b.right, b.bottom, ripplePaint);
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static Drawable[] buildLayers(Drawable content, Drawable mask) {
        int count = 0;
        if (content != null) count++;
        if (mask    != null) count++;
        Drawable[] arr = new Drawable[count];
        int i = 0;
        if (content != null) arr[i++] = content;
        if (mask    != null) arr[i]   = mask;
        return arr;
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "RippleDrawable(color=" + color
             + ", content=" + content
             + ", mask=" + mask + ")";
    }
}
