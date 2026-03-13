package android.text.method;

public class SingleLineTransformationMethod extends ReplacementTransformationMethod {
    public SingleLineTransformationMethod() {}

    public static SingleLineTransformationMethod getInstance() { return null; }
    public char[] getOriginal() { return new char[0]; }
    public char[] getReplacement() { return new char[0]; }
    public void onFocusChanged(android.view.View view, CharSequence sourceText, boolean focused, int direction, android.graphics.Rect previouslyFocusedRect) {}
}
