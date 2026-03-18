package com.android.internal.widget;
import android.view.inputmethod.InputConnection;
import android.widget.TextView;
/** AOSP compilation stub. */
public class EditableInputConnection implements InputConnection {
    public EditableInputConnection(TextView textView) {}
    public CharSequence getTextBeforeCursor(int n, int flags) { return ""; }
    public CharSequence getTextAfterCursor(int n, int flags) { return ""; }
    public boolean commitText(CharSequence text, int newCursorPosition) { return false; }
    public boolean setComposingText(CharSequence text, int newCursorPosition) { return false; }
    public boolean deleteSurroundingText(int beforeLength, int afterLength) { return false; }
    public boolean finishComposingText() { return false; }
    public boolean beginBatchEdit() { return false; }
    public boolean endBatchEdit() { return false; }
    public boolean closeConnection() { return false; }
}
