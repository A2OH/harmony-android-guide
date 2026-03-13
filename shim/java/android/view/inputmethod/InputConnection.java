package android.view.inputmethod;

/**
 * Android-compatible InputConnection shim.
 * Interface for an input method to interact with the application.
 */
public interface InputConnection {
    CharSequence getTextBeforeCursor(int n, int flags);
    CharSequence getTextAfterCursor(int n, int flags);
    boolean commitText(CharSequence text, int newCursorPosition);
    boolean setComposingText(CharSequence text, int newCursorPosition);
    boolean deleteSurroundingText(int beforeLength, int afterLength);
    boolean finishComposingText();
    boolean beginBatchEdit();
    boolean endBatchEdit();
}
