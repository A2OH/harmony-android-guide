package android.accounts;

/**
 * Android-compatible AuthenticatorDescription shim. Stub.
 */
public class AuthenticatorDescription {
    public final String type;
    public int labelId;
    public int iconId;
    public int smallIconId;
    public int accountPreferencesId;
    public boolean customTokens;

    public AuthenticatorDescription(String type) {
        this.type = type;
    }

    public static AuthenticatorDescription newKey(String type) {
        return new AuthenticatorDescription(type);
    }
}
