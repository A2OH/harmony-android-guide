package android.widget;

/**
 * Shim: android.widget.Checkable
 *
 * Defines the contract for views that can be checked.
 */
public interface Checkable {
    void setChecked(boolean checked);
    boolean isChecked();
    void toggle();
}
