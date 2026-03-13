package android.text.style;

/**
 * Android-compatible CharacterStyle shim.
 * Base class for character-level span styling.
 */
public abstract class CharacterStyle {
    /**
     * Apply this character level styling to the given TextPaint.
     * @param tp the TextPaint to update (typed as Object to avoid dependency chain)
     */
    public abstract void updateDrawState(Object tp);

    /**
     * Returns a CharacterStyle that will apply this CharacterStyle's effect.
     */
    public CharacterStyle getUnderlying() {
        return this;
    }

    /**
     * Wraps a CharacterStyle so that it can be applied to a Spannable multiple times.
     */
    public static CharacterStyle wrap(CharacterStyle cs) {
        return cs;
    }
}
