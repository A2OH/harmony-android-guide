package com.android.internal.widget;
import android.view.inputmethod.*;
import android.widget.TextView;
/** AOSP compilation stub. */
public class EditableInputConnection implements InputConnection {
    public EditableInputConnection(TextView textView) {}

    public static int getComposingSpanStart(android.text.Spannable sp) { return -1; }
    public static int getComposingSpanEnd(android.text.Spannable sp) { return -1; }
    public CharSequence getTextBeforeCursor(int n, int flags) { return ""; }
    public CharSequence getTextAfterCursor(int n, int flags) { return ""; }
    public boolean commitText(CharSequence text, int newCursorPosition) { return false; }
    public boolean setComposingText(CharSequence text, int newCursorPosition) { return false; }
    public boolean deleteSurroundingText(int beforeLength, int afterLength) { return false; }
    public boolean finishComposingText() { return false; }
    public boolean beginBatchEdit() { return false; }
    public boolean endBatchEdit() { return false; }
    public int getCursorCapsMode(int reqModes) { return 0; }
    public boolean reportFullscreenMode(boolean enabled) { return false; }
    public boolean performEditorAction(int editorAction) { return false; }
    public boolean sendKeyEvent(android.view.KeyEvent event) { return false; }
    public CharSequence getSelectedText(int flags) { return null; }
    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) { return null; }
    public boolean deleteSurroundingTextInCodePoints(int beforeLength, int afterLength) { return false; }
    public boolean setComposingRegion(int start, int end) { return false; }
    public boolean commitCompletion(CompletionInfo text) { return false; }
    public boolean commitCorrection(CorrectionInfo correctionInfo) { return false; }
    public boolean setSelection(int start, int end) { return false; }
    public boolean performContextMenuAction(int id) { return false; }
    public boolean clearMetaKeyStates(int states) { return false; }
    public boolean performPrivateCommand(String action, android.os.Bundle data) { return false; }
    public boolean requestCursorUpdates(int cursorUpdateMode) { return false; }
    public android.os.Handler getHandler() { return null; }
    public void closeConnection() {}
    public boolean commitContent(InputContentInfo inputContentInfo, int flags, android.os.Bundle opts) { return false; }
}
