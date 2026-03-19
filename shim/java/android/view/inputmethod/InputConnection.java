package android.view.inputmethod;

/**
 * Android-compatible InputConnection shim.
 * Interface for an input method to interact with the application.
 */
public interface InputConnection {
    public static final int GET_TEXT_WITH_STYLES = 0x0001;
    public static final int GET_EXTRACTED_TEXT_MONITOR = 0x0001;
    public static final int CURSOR_UPDATE_IMMEDIATE = 1 << 0;
    public static final int CURSOR_UPDATE_MONITOR = 1 << 1;
    CharSequence getTextBeforeCursor(int n, int flags);
    CharSequence getTextAfterCursor(int n, int flags);
    boolean commitText(CharSequence text, int newCursorPosition);
    boolean setComposingText(CharSequence text, int newCursorPosition);
    boolean deleteSurroundingText(int beforeLength, int afterLength);
    boolean finishComposingText();
    boolean beginBatchEdit();
    boolean endBatchEdit();
    int getCursorCapsMode(int reqModes);

    // Additional methods required by AOSP AbsListView InputConnectionWrapper
    boolean reportFullscreenMode(boolean enabled);
    boolean performEditorAction(int editorAction);
    boolean sendKeyEvent(android.view.KeyEvent event);
    CharSequence getSelectedText(int flags);
    ExtractedText getExtractedText(ExtractedTextRequest request, int flags);
    boolean deleteSurroundingTextInCodePoints(int beforeLength, int afterLength);
    boolean setComposingRegion(int start, int end);
    boolean commitCompletion(CompletionInfo text);
    boolean commitCorrection(CorrectionInfo correctionInfo);
    boolean setSelection(int start, int end);
    boolean performContextMenuAction(int id);
    boolean clearMetaKeyStates(int states);
    boolean performPrivateCommand(String action, android.os.Bundle data);
    boolean requestCursorUpdates(int cursorUpdateMode);
    android.os.Handler getHandler();
    void closeConnection();
    boolean commitContent(InputContentInfo inputContentInfo, int flags, android.os.Bundle opts);
}
