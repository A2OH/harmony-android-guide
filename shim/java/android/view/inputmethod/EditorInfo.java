package android.view.inputmethod;
import android.text.InputType;

/**
 * Android-compatible EditorInfo shim.
 */
public class EditorInfo {

    // InputType constants (duplicated here for compatibility)
    public static final int TYPE_NULL = InputType.TYPE_NULL;
    public static final int TYPE_CLASS_TEXT = InputType.TYPE_CLASS_TEXT;
    public static final int TYPE_CLASS_NUMBER = InputType.TYPE_CLASS_NUMBER;
    public static final int TYPE_CLASS_PHONE = InputType.TYPE_CLASS_PHONE;
    public static final int TYPE_CLASS_DATETIME = InputType.TYPE_CLASS_DATETIME;
    public static final int TYPE_MASK_CLASS = InputType.TYPE_MASK_CLASS;
    public static final int TYPE_MASK_VARIATION = InputType.TYPE_MASK_VARIATION;
    public static final int TYPE_TEXT_FLAG_MULTI_LINE = InputType.TYPE_TEXT_FLAG_MULTI_LINE;
    public static final int TYPE_TEXT_FLAG_AUTO_CORRECT = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;
    public static final int TYPE_TEXT_FLAG_CAP_SENTENCES = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
    public static final int TYPE_TEXT_FLAG_CAP_WORDS = InputType.TYPE_TEXT_FLAG_CAP_WORDS;
    public static final int TYPE_TEXT_FLAG_CAP_CHARACTERS = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
    public static final int TYPE_TEXT_VARIATION_NORMAL = InputType.TYPE_TEXT_VARIATION_NORMAL;
    public static final int TYPE_TEXT_VARIATION_PASSWORD = InputType.TYPE_TEXT_VARIATION_PASSWORD;
    public static final int TYPE_TEXT_VARIATION_EMAIL_ADDRESS = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
    public static final int TYPE_TEXT_VARIATION_EMAIL_SUBJECT = InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT;
    public static final int TYPE_TEXT_VARIATION_SHORT_MESSAGE = InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE;
    public static final int TYPE_TEXT_VARIATION_LONG_MESSAGE = InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE;
    public static final int TYPE_TEXT_VARIATION_VISIBLE_PASSWORD = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
    public static final int TYPE_TEXT_VARIATION_WEB_EDIT_TEXT = InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT;
    public static final int TYPE_TEXT_VARIATION_WEB_PASSWORD = InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD;
    public static final int TYPE_NUMBER_FLAG_SIGNED = InputType.TYPE_NUMBER_FLAG_SIGNED;
    public static final int TYPE_NUMBER_FLAG_DECIMAL = InputType.TYPE_NUMBER_FLAG_DECIMAL;
    public static final int TYPE_NUMBER_VARIATION_PASSWORD = InputType.TYPE_NUMBER_VARIATION_PASSWORD;
    public static final int TYPE_DATETIME_VARIATION_DATE = InputType.TYPE_DATETIME_VARIATION_DATE;
    public static final int TYPE_DATETIME_VARIATION_TIME = InputType.TYPE_DATETIME_VARIATION_TIME;

    // IME action constants
    public static final int IME_ACTION_UNSPECIFIED = 0x00000000;
    public static final int IME_ACTION_NONE    = 0x00000001;
    public static final int IME_ACTION_GO      = 0x00000002;
    public static final int IME_ACTION_SEARCH  = 0x00000003;
    public static final int IME_ACTION_SEND    = 0x00000004;
    public static final int IME_ACTION_NEXT    = 0x00000005;
    public static final int IME_ACTION_DONE    = 0x00000006;
    public static final int IME_ACTION_PREVIOUS = 0x00000007;
    public static final int IME_NULL           = 0x00000000;

    // IME flag constants
    public static final int IME_FLAG_NO_ENTER_ACTION        = 0x40000000;
    public static final int IME_FLAG_NO_EXTRACT_UI          = 0x10000000;
    public static final int IME_FLAG_NO_FULLSCREEN          = 0x02000000;
    public static final int IME_FLAG_NAVIGATE_NEXT          = 0x08000000;
    public static final int IME_FLAG_NAVIGATE_PREVIOUS      = 0x04000000;
    public static final int IME_FLAG_NO_ACCESSORY_ACTION    = 0x20000000;
    public static final int IME_FLAG_NO_PERSONALIZED_LEARNING = 0x01000000;
    public static final int IME_FLAG_FORCE_ASCII             = 0x80000000;

    // IME mask
    public static final int IME_MASK_ACTION = 0x000000ff;

    // Public fields
    public int inputType = 0;
    public int imeOptions = IME_ACTION_DONE;
    public CharSequence actionLabel = null;
    public int actionId = 0;
    public int initialSelStart = -1;
    public int initialSelEnd = -1;
    public String packageName = null;
    public int fieldId = 0;
    public CharSequence label;
    public CharSequence hintText;
    public android.os.Bundle extras;
    public String[] contentMimeTypes;
    public int initialCapsMode;
    public CharSequence initialText;
}
