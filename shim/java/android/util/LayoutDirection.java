package android.util;

/**
 * Android-compatible LayoutDirection shim.
 * Provides constants for layout direction.
 */
public final class LayoutDirection {

    /**
     * Horizontal layout direction is from Left to Right.
     */
    public static final int LTR = 0;

    /**
     * Horizontal layout direction is from Right to Left.
     */
    public static final int RTL = 1;

    /**
     * Horizontal layout direction is inherited.
     */
    public static final int INHERIT = 2;

    /**
     * Horizontal layout direction is deduced from the default language script for the locale.
     */
    public static final int LOCALE = 3;

    public static final int UNDEFINED = -1;

    private LayoutDirection() {}
}
