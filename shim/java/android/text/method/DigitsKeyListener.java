package android.text.method;
import android.text.Editable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.text.Editable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;

/**
 * Android-compatible DigitsKeyListener shim.
 * For use as the key listener for a numeric text field. Accepts digits only.
 */
public class DigitsKeyListener implements KeyListener {
    private static final char[] DEFAULT_CHARS     = "0123456789".toCharArray();
    private static final char[] SIGN_CHARS        = "0123456789-".toCharArray();
    private static final char[] DECIMAL_CHARS     = "0123456789.".toCharArray();
    private static final char[] SIGN_DECIMAL_CHARS = "0123456789-.".toCharArray();

    private final char[] accepted;

    private DigitsKeyListener(char[] accepted) {
        this.accepted = accepted;
    }

    /** Returns a DigitsKeyListener that accepts digits (no sign, no decimal). */
    public static DigitsKeyListener getInstance() {
        return new DigitsKeyListener(DEFAULT_CHARS);
    }

    /** Returns a DigitsKeyListener, optionally allowing sign and decimal. */
    public static DigitsKeyListener getInstance(boolean sign, boolean decimal) {
        if (sign && decimal) return new DigitsKeyListener(SIGN_DECIMAL_CHARS);
        if (sign)            return new DigitsKeyListener(SIGN_CHARS);
        if (decimal)         return new DigitsKeyListener(DECIMAL_CHARS);
        return new DigitsKeyListener(DEFAULT_CHARS);
    }

    /** Returns a DigitsKeyListener for the given accepted character string. */
    public static DigitsKeyListener getInstance(String accepted) {
        return new DigitsKeyListener(accepted.toCharArray());
    }

    /** Returns the set of accepted characters. */
    public char[] getAcceptedChars() {
        return accepted.clone();
    }

    @Override
    public int getInputType() {
        return android.text.InputType.TYPE_CLASS_NUMBER;
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
