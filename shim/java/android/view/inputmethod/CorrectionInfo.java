package android.view.inputmethod;

/**
 * Android-compatible CorrectionInfo shim.
 * Information about a single text correction that an editor reports to an input method.
 */
public class CorrectionInfo {
    private int mOffset;
    private CharSequence mOldText;
    private CharSequence mNewText;

    public CorrectionInfo(int offset, CharSequence oldText, CharSequence newText) {
        mOffset = offset;
        mOldText = oldText;
        mNewText = newText;
    }

    public int getOffset() {
        return mOffset;
    }

    public CharSequence getOldText() {
        return mOldText;
    }

    public CharSequence getNewText() {
        return mNewText;
    }
}
