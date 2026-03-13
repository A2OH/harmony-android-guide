package android.text.method;

import android.text.Editable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;

/**
 * Android-compatible TextKeyListener shim.
 * Listener for key presses on views, delivering them to the Editable associated
 * with the view. Supports configurable capitalisation.
 */
public class TextKeyListener implements KeyListener {

    /**
     * Capitalization mode constants (mirrors android.text.method.TextKeyListener.Capitalize).
     */
    public enum Capitalize {
        NONE,
        SENTENCES,
        WORDS,
        CHARACTERS
    }

    private static TextKeyListener sInstance;

    private final Capitalize capitalize;
    private final boolean    autoText;

    public TextKeyListener(Capitalize capitalize, boolean autoText) {
        this.capitalize = capitalize;
        this.autoText   = autoText;
    }

    /** Returns a shared instance with NONE capitalisation and no auto-text. */
    public static TextKeyListener getInstance() {
        if (sInstance == null) {
            sInstance = new TextKeyListener(Capitalize.NONE, false);
        }
        return sInstance;
    }

    /** Returns a shared instance with given capitalisation and auto-text setting. */
    public static TextKeyListener getInstance(boolean autoText, Capitalize cap) {
        return new TextKeyListener(cap, autoText);
    }

    public Capitalize getCapitalize() {
        return capitalize;
    }

    public boolean getAutoText() {
        return autoText;
    }

    @Override
    public int getInputType() {
        int type = InputType.TYPE_CLASS_TEXT;
        if (autoText) {
            type |= InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;
        }
        switch (capitalize) {
            case CHARACTERS: type |= InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS; break;
            case WORDS:      type |= InputType.TYPE_TEXT_FLAG_CAP_WORDS;      break;
            case SENTENCES:  type |= InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;  break;
            default:         break;
        }
        return type;
    }

    @Override
    public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyOther(View view, Editable text, KeyEvent event) {
        return false;
    }

    @Override
    public void clearMetaKeyState(View view, Editable content, int states) {}
}
