package android.text.method;

import android.view.KeyEvent;
import android.view.View;

/**
 * Android-compatible KeyListener interface shim.
 */
public interface KeyListener {
    int getInputType();
    boolean onKeyDown(View view, android.text.Editable text, int keyCode, KeyEvent event);
    boolean onKeyUp(View view, android.text.Editable text, int keyCode, KeyEvent event);
    boolean onKeyOther(View view, android.text.Editable text, KeyEvent event);
    void clearMetaKeyState(View view, android.text.Editable content, int states);
}
