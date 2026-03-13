package android.view;

/**
 * Android-compatible SoundEffectConstants shim.
 * Constants for sound effects played by views.
 */
public class SoundEffectConstants {

    public static final int CLICK = 0;
    public static final int NAVIGATION_LEFT = 1;
    public static final int NAVIGATION_UP = 2;
    public static final int NAVIGATION_RIGHT = 3;
    public static final int NAVIGATION_DOWN = 4;

    private SoundEffectConstants() {}

    // Direction constants mirror android.view.View.FOCUS_* values
    private static final int FOCUS_LEFT  = 0x11; // View.FOCUS_LEFT
    private static final int FOCUS_UP    = 0x21; // View.FOCUS_UP
    private static final int FOCUS_RIGHT = 0x42; // View.FOCUS_RIGHT
    private static final int FOCUS_DOWN  = 0x82; // View.FOCUS_DOWN

    public static int getContantForFocusDirection(int direction) {
        switch (direction) {
            case FOCUS_LEFT:
                return NAVIGATION_LEFT;
            case FOCUS_UP:
                return NAVIGATION_UP;
            case FOCUS_RIGHT:
                return NAVIGATION_RIGHT;
            case FOCUS_DOWN:
                return NAVIGATION_DOWN;
            default:
                throw new IllegalArgumentException("Unknown direction: " + direction);
        }
    }
}
