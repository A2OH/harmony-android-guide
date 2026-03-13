package android.view.inputmethod;

/**
 * Android-compatible ExtractedText shim.
 * Information about text that has been extracted for use by an input method.
 */
public class ExtractedText {
    public CharSequence text;
    public int startOffset;
    public int selectionStart;
    public int selectionEnd;
    public int flags;

    public static final int FLAG_SINGLE_LINE = 0x0001;
    public static final int FLAG_SELECTING = 0x0002;
}
