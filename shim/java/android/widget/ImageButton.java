package android.widget;

/**
 * Shim: android.widget.ImageButton → ImageView with click behavior.
 *
 * ImageButton is identical to ImageView in terms of structure; button
 * interaction (click events) is inherited from View. No additional
 * ArkUI node type is needed — ARKUI_NODE_IMAGE handles both.
 */
public class ImageButton extends ImageView {

    public ImageButton() {
        super();
    }

    /** Constructor accepting a context-like object (ignored in shim). */
    public ImageButton(Object context) {
        super();
    }

    /** Constructor accepting context and attribute set (both ignored). */
    public ImageButton(Object context, Object attrs) {
        super();
    }

    /** Constructor accepting context, attribute set and default style (all ignored). */
    public ImageButton(Object context, Object attrs, int defStyleAttr) {
        super();
    }
}
