package android.text.method;

/**
 * Android-compatible BaseKeyListener stub.
 */
public class BaseKeyListener implements KeyListener {

    public boolean backspace(Object view, Object content, int keyCode, Object event) {
        return false;
    }

    public boolean forwardDelete(Object view, Object content, int keyCode, Object event) {
        return false;
    }
    public void clearMetaKeyState(android.view.View view, android.text.Editable content, int states) {}

    public int getInputType() { return 0; }

    public boolean onKeyDown(android.view.View view, android.text.Editable text, int keyCode, android.view.KeyEvent event) {
        return false;
    }

    public boolean onKeyUp(android.view.View view, android.text.Editable text, int keyCode, android.view.KeyEvent event) {
        return false;
    }

    public boolean onKeyOther(android.view.View view, android.text.Editable text, android.view.KeyEvent event) {
        return false;
    }
}
