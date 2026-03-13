package android.app;

/**
 * Android-compatible RemoteInput shim.
 * Used by Notification.Action to accept inline text replies from notification UI.
 */
public final class RemoteInput {

    private final String      mResultKey;
    private final CharSequence mLabel;
    private final CharSequence[] mChoices;
    private final boolean     mAllowFreeFormInput;

    private RemoteInput(Builder b) {
        mResultKey          = b.mResultKey;
        mLabel              = b.mLabel;
        mChoices            = b.mChoices;
        mAllowFreeFormInput = b.mAllowFreeFormInput;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** The key used to identify the result in the reply Intent extras. */
    public String getResultKey() { return mResultKey; }

    /** Human-readable label shown next to the input field, or null. */
    public CharSequence getLabel() { return mLabel; }

    /** Pre-defined choices the user can select, or null. */
    public CharSequence[] getChoices() { return mChoices; }

    /** Whether the user may type arbitrary text in addition to (or instead of) choices. */
    public boolean getAllowFreeFormInput() { return mAllowFreeFormInput; }

    // -------------------------------------------------------------------------
    // Static helper
    // -------------------------------------------------------------------------

    /**
     * Returns the results bundle stored in an intent by the system after the
     * user has entered a reply.  Always returns null in the shim.
     *
     * @param intent the reply Intent (as Object to avoid android.content.Intent dependency)
     * @return null in shim environment
     */
    public static Object getResultsFromIntent(Object intent) {
        return null;
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static final class Builder {

        private final String mResultKey;
        private CharSequence  mLabel              = null;
        private CharSequence[] mChoices           = null;
        private boolean       mAllowFreeFormInput = true;

        /**
         * @param resultKey the key used to look up the result in the reply Intent extras
         */
        public Builder(String resultKey) {
            if (resultKey == null) throw new IllegalArgumentException("resultKey must not be null");
            mResultKey = resultKey;
        }

        /** Sets the human-readable label to display next to the input control. */
        public Builder setLabel(CharSequence label) {
            mLabel = label;
            return this;
        }

        /** Sets a list of pre-defined choices the user can pick. */
        public Builder setChoices(CharSequence[] choices) {
            mChoices = choices;
            return this;
        }

        /** Controls whether the user can enter arbitrary text. Default is true. */
        public Builder setAllowFreeFormInput(boolean allowFreeFormInput) {
            mAllowFreeFormInput = allowFreeFormInput;
            return this;
        }

        /** Builds and returns the RemoteInput. */
        public RemoteInput build() {
            return new RemoteInput(this);
        }
    }
}
