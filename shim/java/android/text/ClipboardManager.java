package android.text;

/**
 * Shim: android.text.ClipboardManager — deprecated clipboard API stub.
 *
 * This is the legacy pre-Honeycomb clipboard manager that was superseded by
 * {@link android.content.ClipboardManager}.  All methods return safe defaults
 * (null / false / no-op) so that old code referencing this class compiles and
 * runs without crashing.
 *
 * @deprecated Use {@link android.content.ClipboardManager} instead.
 */
@Deprecated
public class ClipboardManager {

    /** Constructs a ClipboardManager stub. */
    public ClipboardManager() {
        // no-op
    }

    /**
     * Returns the text on the clipboard, or {@code null} if none.
     *
     * @return always {@code null} in this stub.
     * @deprecated Use {@link android.content.ClipboardManager#getPrimaryClip()}.
     */
    @Deprecated
    public CharSequence getText() {
        return null;
    }

    /**
     * Sets the contents of the clipboard to the specified text.
     *
     * @param text the text to place on the clipboard.
     * @deprecated Use {@link android.content.ClipboardManager#setPrimaryClip}.
     */
    @Deprecated
    public void setText(CharSequence text) {
        // no-op
    }

    /**
     * Returns {@code true} if the clipboard contains text.
     *
     * @return always {@code false} in this stub.
     * @deprecated Use {@link android.content.ClipboardManager#hasPrimaryClip()}.
     */
    @Deprecated
    public boolean hasText() {
        return false;
    }
}
