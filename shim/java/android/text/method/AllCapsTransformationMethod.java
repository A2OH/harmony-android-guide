package android.text.method;

/** Auto-generated stub for AOSP compilation. */
public class AllCapsTransformationMethod implements TransformationMethod2 {
    public AllCapsTransformationMethod() {}
    public AllCapsTransformationMethod(android.content.Context context) {}
    public CharSequence getTransformation(CharSequence source, Object view) {
        return source != null ? source.toString().toUpperCase() : null;
    }
    public void onFocusChanged(Object view, CharSequence sourceText, boolean focused, int direction, Object previouslyFocusedRect) {}
    public void setLengthChangesAllowed(boolean allowLengthChanges) {}
}
