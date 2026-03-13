package android.view.inputmethod;
import android.text.InputType;
import android.text.InputType;

/**
 * Android-compatible EditorInfo shim.
 * Describes an input editor session, including type and IME action options.
 */
public class EditorInfo {

    // ── IME action constants ──

    public static final int IME_ACTION_DONE    = 0x00000006;
    public static final int IME_ACTION_GO      = 0x00000002;
    public static final int IME_ACTION_NEXT    = 0x00000005;
    public static final int IME_ACTION_SEARCH  = 0x00000003;
    public static final int IME_ACTION_SEND    = 0x00000004;

    // ── IME flag constants ──

    public static final int IME_FLAG_NO_ENTER_ACTION = 0x40000000;
    public static final int IME_FLAG_NO_EXTRACT_UI   = 0x10000000;

    // ── IME mask (for extracting action from imeOptions) ──

    public static final int IME_MASK_ACTION = 0x000000ff;

    // ── Public fields (set by the editor before passing to IME) ──

    /** The content type of the text being edited (from android.text.InputType). */
    public int inputType = 0;

    /**
     * A bitmask of IME_FLAG_* values and one of the IME_ACTION_* values,
     * describing what the IME should do when the user presses the action button.
     */
    public int imeOptions = IME_ACTION_DONE;

    /** Custom label for the action button; may be null. */
    public CharSequence actionLabel = null;

    /** Supplies a custom action button ID; used together with actionLabel. */
    public int actionId = 0;

    /** The start of the selection in the initial text, or -1 if unknown. */
    public int initialSelStart = -1;

    /** The end of the selection in the initial text, or -1 if unknown. */
    public int initialSelEnd = -1;

    /** Package name of the application that owns the editor. */
    public String packageName = null;

    /** Field ID supplied by the application for bookkeeping. */
    public int fieldId = 0;
}
