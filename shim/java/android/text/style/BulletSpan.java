package android.text.style;

/**
 * Android shim: BulletSpan
 * Draws a bullet po(int at the beginning of a paragraph.
 */
public class BulletSpan {

    /** Default gap width between the bullet and the text, in pixels. */
    public static final int STANDARD_GAP_WIDTH = 2;

    /** Default bullet radius, in pixels. */
    public static final int STANDARD_BULLET_RADIUS = 4;

    private final int mGapWidth;
    private final int mColor;
    private final int mBulletRadius;

    /** Creates a BulletSpan using default gap width and no explicit color. */
    public BulletSpan() {
        this(STANDARD_GAP_WIDTH, 0, STANDARD_BULLET_RADIUS);
    }

    /**
     * Creates a BulletSpan with the given gap width.
     *
     * @param gapWidth gap in pixels between the bullet and the text
     */
    public BulletSpan(int gapWidth) {
        this(gapWidth, 0, STANDARD_BULLET_RADIUS);
    }

    /**
     * Creates a BulletSpan with the given gap width and bullet color.
     *
     * @param gapWidth gap in pixels between the bullet and the text
     * @param color    ARGB color of the bullet
     */
    public BulletSpan(int gapWidth, int color) {
        this(gapWidth, color, STANDARD_BULLET_RADIUS);
    }

    /**
     * Creates a BulletSpan with explicit gap width, color, and bullet radius.
     *
     * @param gapWidth     gap in pixels between the bullet and the text
     * @param color        ARGB color of the bullet
     * @param bulletRadius radius of the bullet in pixels
     */
    public BulletSpan(int gapWidth, int color, int bulletRadius) {
        mGapWidth     = gapWidth;
        mColor        = color;
        mBulletRadius = bulletRadius;
    }

    /** Returns the gap width between the bullet and the text, in pixels. */
    public int getGapWidth() {
        return mGapWidth;
    }

    /** Returns the ARGB color of the bullet. */
    public int getColor() {
        return mColor;
    }

    /** Returns the bullet radius in pixels. */
    public int getBulletRadius() {
        return mBulletRadius;
    }
}
