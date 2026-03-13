package android.view;

/**
 * Android-compatible WindowInsetsController stub (API 30+).
 */
public interface WindowInsetsController {

    int APPEARANCE_LIGHT_STATUS_BARS = 4;
    int APPEARANCE_LIGHT_NAVIGATION_BARS = 16;

    int BEHAVIOR_DEFAULT = 1;
    int BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE = 2;

    void show(int types);

    void hide(int types);

    int getSystemBarsAppearance();

    void setSystemBarsAppearance(int appearance, int mask);

    int getSystemBarsBehavior();

    void setSystemBarsBehavior(int behavior);

    /**
     * Object for controllable insets changes.
     */
    interface OnControllableInsetsChangedListener {
        void onControllableInsetsChanged(WindowInsetsController controller, int typeMask);
    }
}
