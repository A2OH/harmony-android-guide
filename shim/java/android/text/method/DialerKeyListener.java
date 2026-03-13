package android.text.method;

public class DialerKeyListener extends NumberKeyListener {
    public static final int CHARACTERS = 0;

    public DialerKeyListener() {}

    public char[] getAcceptedChars() { return new char[0]; }
    public int getInputType() { return 0; }
    public static DialerKeyListener getInstance() { return null; }
    public void clearMetaKeyState(android.view.View view, android.text.Editable content, int states) {}
}