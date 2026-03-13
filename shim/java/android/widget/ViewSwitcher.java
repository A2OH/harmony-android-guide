package android.widget;

import android.view.View;

/**
 * Shim: android.widget.ViewSwitcher → switches between two child views
 *
 * Extends ViewAnimator, adds ViewFactory support.
 */
public class ViewSwitcher extends ViewAnimator {

    private ViewFactory factory;

    public ViewSwitcher() {
        super();
    }

    public void setFactory(ViewFactory factory) {
        this.factory = factory;
    }

    public ViewFactory getFactory() {
        return factory;
    }

    public View getNextView() {
        return null;
    }

    public View getCurrentView() {
        return null;
    }

    /**
     * Creates views to be used by the ViewSwitcher.
     */
    public interface ViewFactory {
        View makeView();
    }
}
