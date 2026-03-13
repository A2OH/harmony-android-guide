package android.text.style;

import android.view.View;

/**
 * Android-compatible ClickableSpan shim.
 * Abstract marker span whose subclasses handle click events on the spanned text.
 */
public abstract class ClickableSpan {

    /**
     * Called when the spanned text region is clicked.
     *
     * @param widget The View that was clicked (typically a TextView).
     */
    public abstract void onClick(View widget);
}
