package android.view;

/**
 * Android-compatible ViewManager shim.
 * Interface for adding, updating, and removing views.
 */
public interface ViewManager {

    void addView(View view, Object layoutParams);

    void updateViewLayout(View view, Object layoutParams);

    void removeView(View view);
}
