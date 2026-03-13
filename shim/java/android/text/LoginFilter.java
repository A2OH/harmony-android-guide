package android.text;

/**
 * Android-compatible LoginFilter shim.
 * Abstract base class implementing InputFilter for login/username validation.
 * Filters out characters that are not acceptable in a login field.
 */
public abstract class LoginFilter implements InputFilter {

    private final boolean mAppendInvalid;

    protected LoginFilter(boolean appendInvalid) {
        mAppendInvalid = appendInvalid;
    }

    /**
     * Called for each character in the source sequence.
     * Subclasses return true if the character is allowed.
     */
    public boolean isAllowed(char c) { return false; }

    /** Called when an invalid character is encountered (no-op in stub). */
    public void onInvalidCharacter(char c) {}

    /** Called before filtering begins (no-op in stub). */
    public void onStart() {}

    /** Called after filtering ends (no-op in stub). */
    public void onStop() {}

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        onStart();
        StringBuilder sb = null;
        for (int i = start; i < end; i++) {
            char c = source.charAt(i);
            if (isAllowed(c)) {
                if (sb != null) sb.append(c);
            } else {
                onInvalidCharacter(c);
                if (sb == null) {
                    // Lazily build a filtered copy up to this point
                    sb = new StringBuilder(end - start);
                    sb.append(source, start, i);
                }
                if (mAppendInvalid) sb.append(c);
            }
        }
        onStop();
        if (sb == null) return null; // no change — all characters accepted
        return sb;
    }

    // ── UsernameFilterGeneric ─────────────────────────────────────────────────

    /**
     * Accepts characters that are typically allowed in a generic username:
     * alphanumerics, underscore, hyphen, dot, and at-sign.
     */
    public static class UsernameFilterGeneric extends LoginFilter {

        public UsernameFilterGeneric() {
            super(false);
        }

        public UsernameFilterGeneric(boolean appendInvalid) {
            super(appendInvalid);
        }

        @Override
        public boolean isAllowed(char c) {
            return Character.isLetterOrDigit(c)
                || c == '_'
                || c == '-'
                || c == '.'
                || c == '@';
        }
    }

    // ── UsernameFilterGMail ───────────────────────────────────────────────────

    /**
     * Accepts characters that are allowed in a Gmail-style username:
     * alphanumerics, dot, plus, and hyphen (no underscore, no at-sign).
     */
    public static class UsernameFilterGMail extends LoginFilter {

        public UsernameFilterGMail() {
            super(false);
        }

        public UsernameFilterGMail(boolean appendInvalid) {
            super(appendInvalid);
        }

        @Override
        public boolean isAllowed(char c) {
            return Character.isLetterOrDigit(c)
                || c == '.'
                || c == '+'
                || c == '-';
        }
    }
}
