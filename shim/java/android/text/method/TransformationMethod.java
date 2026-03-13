package android.text.method;

/**
 * Android-compatible TransformationMethod stub.
 * Transforms source text before display (e.g. password masking).
 */
public interface TransformationMethod {

    /**
     * Returns a CharSequence that is a transformation of the source text.
     *
     * @param source the original text
     * @param view   the View the text is displayed in (typed as Object for compatibility)
     * @return the transformed CharSequence
     */
    CharSequence getTransformation(CharSequence source, Object view);

    /**
     * Called when the focus state of the view this transformation is attached to changes.
     *
     * @param view      the View whose focus state changed (typed as Object for compatibility)
     * @param sourceText the current source text
     * @param focused   whether the view has gained focus
     * @param direction the direction focus moved
     * @param previouslyFocusedRect the Rect that previously held focus (typed as Object for compatibility)
     */
    void onFocusChanged(Object view, CharSequence sourceText, boolean focused,
            int direction, Object previouslyFocusedRect);
}
