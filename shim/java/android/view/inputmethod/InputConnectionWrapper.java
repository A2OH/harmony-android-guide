package android.view.inputmethod;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

public class InputConnectionWrapper implements InputConnection {
    public InputConnectionWrapper(InputConnection p0, boolean p1) {}

    public boolean beginBatchEdit() { return false; }
    public boolean clearMetaKeyStates(int p0) { return false; }
    public void closeConnection() {}
    public boolean commitCompletion(CompletionInfo p0) { return false; }
    public boolean commitContent(InputContentInfo p0, int p1, Bundle p2) { return false; }
    public boolean commitCorrection(CorrectionInfo p0) { return false; }
    public boolean commitText(CharSequence p0, int p1) { return false; }
    public boolean deleteSurroundingText(int p0, int p1) { return false; }
    public boolean deleteSurroundingTextInCodePoints(int p0, int p1) { return false; }
    public boolean endBatchEdit() { return false; }
    public boolean finishComposingText() { return false; }
    public int getCursorCapsMode(int p0) { return 0; }
    public ExtractedText getExtractedText(ExtractedTextRequest p0, int p1) { return null; }
    public Handler getHandler() { return null; }
    public CharSequence getSelectedText(int p0) { return null; }
    public CharSequence getTextAfterCursor(int p0, int p1) { return null; }
    public CharSequence getTextBeforeCursor(int p0, int p1) { return null; }
    public boolean performContextMenuAction(int p0) { return false; }
    public boolean performEditorAction(int p0) { return false; }
    public boolean performPrivateCommand(String p0, Bundle p1) { return false; }
    public boolean reportFullscreenMode(boolean p0) { return false; }
    public boolean requestCursorUpdates(int p0) { return false; }
    public boolean sendKeyEvent(KeyEvent p0) { return false; }
    public boolean setComposingRegion(int p0, int p1) { return false; }
    public boolean setComposingText(CharSequence p0, int p1) { return false; }
    public boolean setSelection(int p0, int p1) { return false; }
    public void setTarget(InputConnection p0) {}
}
