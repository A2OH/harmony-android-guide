package android.accounts;

/**
 * Android-compatible AbstractAccountAuthenticator shim. Stub abstract class.
 */
public abstract class AbstractAccountAuthenticator {
    public AbstractAccountAuthenticator(Object context) {
        // stub
    }

    public abstract Object addAccount(AccountAuthenticatorResponse response,
            String accountType, String authTokenType,
            String[] requiredFeatures, Object options) throws AccountsException;

    public Object confirmCredentials(AccountAuthenticatorResponse response,
            Object account, Object options) throws AccountsException {
        return null; // stub
    }

    public Object getAccountRemovalAllowed(AccountAuthenticatorResponse response,
            Object account) throws AccountsException {
        return null; // stub
    }

    public Object updateCredentials(AccountAuthenticatorResponse response,
            Object account, String authTokenType,
            Object options) throws AccountsException {
        return null; // stub
    }

    public Object hasFeatures(AccountAuthenticatorResponse response,
            Object account, String[] features) throws AccountsException {
        return null; // stub
    }

    public Object getAuthToken(AccountAuthenticatorResponse response,
            Object account, String authTokenType,
            Object options) throws AccountsException {
        return null; // stub
    }

    public Object editProperties(AccountAuthenticatorResponse response,
            String accountType) {
        return null; // stub
    }
}
