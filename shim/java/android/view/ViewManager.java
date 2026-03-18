package android.view;

/**
 * Android-compatible ViewManager shim.
 * Interface for adding, updating, and removing views.
 */
public interface ViewManager {
    void addView(View view, ViewGroup.LayoutParams params);
    void updateViewLayout(View view, ViewGroup.LayoutParams params);
    void removeView(View view);
}
