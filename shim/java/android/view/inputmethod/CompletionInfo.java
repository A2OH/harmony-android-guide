package android.view.inputmethod;

/**
 * Android-compatible CompletionInfo shim.
 * Information about a single text completion that an editor reports to an input method.
 */
public class CompletionInfo {
    private long mId;
    private int mPosition;
    private CharSequence mText;
    private CharSequence mLabel;

    public CompletionInfo(long id, int index, CharSequence text) {
        this(id, index, text, null);
    }

    public CompletionInfo(long id, int index, CharSequence text, CharSequence label) {
        mId = id;
        mPosition = index;
        mText = text;
        mLabel = label;
    }

    public long getId() {
        return mId;
    }

    public int getPosition() {
        return mPosition;
    }

    public CharSequence getText() {
        return mText;
    }

    public CharSequence getLabel() {
        return mLabel;
    }
}
