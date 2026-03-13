package android.view.inputmethod;

/**
 * Android-compatible BaseInputConnection shim.
 * Base class for implementors of the InputConnection interface.
 */
public class BaseInputConnection implements InputConnection {
    private boolean mDummyMode;

    public BaseInputConnection(boolean fullEditor) {
        mDummyMode = !fullEditor;
    }

    @Override
    public CharSequence getTextBeforeCursor(int n, int flags) {
        return "";
    }

    @Override
    public CharSequence getTextAfterCursor(int n, int flags) {
        return "";
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
        return false;
    }

    @Override
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        return false;
    }

    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        return false;
    }

    @Override
    public boolean finishComposingText() {
        return false;
    }

    @Override
    public boolean beginBatchEdit() {
        return false;
    }

    @Override
    public boolean endBatchEdit() {
        return false;
    }
}
