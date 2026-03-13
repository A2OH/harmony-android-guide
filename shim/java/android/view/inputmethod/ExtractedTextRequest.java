package android.view.inputmethod;

/**
 * Android-compatible ExtractedTextRequest shim.
 * Description of what an application wants from extracted text.
 */
public class ExtractedTextRequest {
    public int token;
    public int flags;
    public int hintMaxLines;
    public int hintMaxChars;
}
