package android.text;

/**
 * Shim: android.text.AutoText (stub)
 * Auto-text replacement dictionary — not applicable on OpenHarmony.
 * All methods return safe no-op defaults.
 */
public final class AutoText {

    private AutoText() {}

    /**
     * Retrieve the replacement text for the specified range, if any.
     * Stub always returns null (no auto-text replacements available).
     */
    public static String get(CharSequence src, int start, int end, Object view) {
        return null;
    }

    /**
     * Return the size of the auto-text dictionary.
     * Stub always returns 0 (empty dictionary).
     */
    public static int getSize(Object view) {
        return 0;
    }
}
