package android.text;

/**
 * Android-compatible InputType constants shim.
 */
public final class InputType {
    public static final int TYPE_NULL                    = 0x00000000;
    public static final int TYPE_CLASS_TEXT              = 0x00000001;
    public static final int TYPE_CLASS_NUMBER            = 0x00000002;
    public static final int TYPE_CLASS_PHONE             = 0x00000003;
    public static final int TYPE_CLASS_DATETIME          = 0x00000004;
    public static final int TYPE_MASK_CLASS              = 0x0000000f;
    public static final int TYPE_MASK_FLAGS              = 0x00fff000;
    public static final int TYPE_MASK_VARIATION          = 0x00000ff0;
    public static final int TYPE_TEXT_FLAG_MULTI_LINE    = 0x00020000;
    public static final int TYPE_TEXT_FLAG_AUTO_COMPLETE = 0x00010000;
    public static final int TYPE_TEXT_FLAG_AUTO_CORRECT  = 0x00008000;
    public static final int TYPE_TEXT_FLAG_CAP_SENTENCES = 0x00004000;
    public static final int TYPE_TEXT_FLAG_CAP_WORDS     = 0x00002000;
    public static final int TYPE_TEXT_FLAG_CAP_CHARACTERS= 0x00001000;
    public static final int TYPE_TEXT_VARIATION_PASSWORD = 0x00000080;
    public static final int TYPE_TEXT_VARIATION_EMAIL_ADDRESS = 0x00000020;
    public static final int TYPE_NUMBER_FLAG_SIGNED      = 0x00001000;
    public static final int TYPE_NUMBER_FLAG_DECIMAL     = 0x00002000;

    private InputType() {}
}
