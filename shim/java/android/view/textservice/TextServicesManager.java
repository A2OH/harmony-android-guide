package android.view.textservice;

/**
 * Android-compatible TextServicesManager shim. Spell-checker is always disabled on OpenHarmony.
 */
public class TextServicesManager {

    /**
     * Creates a new SpellCheckerSession. In this shim the returned session is a disconnected
     * stub — no real spell-check engine is available.
     *
     * @param locale   requested locale string (ignored)
     * @param listener callback (ignored)
     * @param referToSpellCheckerLanguageSettings ignored
     * @return a stub SpellCheckerSession, or null if spell-checking is disabled
     */
    public SpellCheckerSession newSpellCheckerSession(
            String locale,
            SpellCheckerSession.SpellCheckerSessionListener listener,
            boolean referToSpellCheckerLanguageSettings) {
        return null; // spell-checker not available in shim environment
    }

    /** Always returns false — no spell-check engine on OpenHarmony. */
    public boolean isSpellCheckerEnabled() { return false; }

    /** Always returns null — no spell-checker info available. */
    public SpellCheckerInfo getCurrentSpellCheckerInfo() { return null; }
}
