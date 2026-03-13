package android.text.method;

public class HideReturnsTransformationMethod extends ReplacementTransformationMethod {
    public HideReturnsTransformationMethod() {}

    public static HideReturnsTransformationMethod getInstance() { return null; }
    public char[] getOriginal() { return new char[0]; }
    public char[] getReplacement() { return new char[0]; }
    public void onFocusChanged(android.view.View view, CharSequence sourceText, boolean focused, int direction, android.graphics.Rect previouslyFocusedRect) {}
}
