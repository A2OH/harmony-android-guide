package android.widget;

/**
 * Shim: android.widget.TextSwitcher → animated text switching
 *
 * Specialized ViewSwitcher that animates between two TextViews.
 */
public class TextSwitcher extends ViewSwitcher {

    public TextSwitcher() {
        super();
    }

    public void setText(CharSequence text) {
        // no-op stub — would animate to the next TextView with the given text
    }

    public void setCurrentText(CharSequence text) {
        // no-op stub — sets the current text without animation
    }
}
