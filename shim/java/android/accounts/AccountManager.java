package android.accounts;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Android-compatible AccountManager shim. Manages user accounts.
 * Backed by an in-process store; changes do not persist across JVM restarts.
 */
public class AccountManager {

    private static AccountManager sInstance;

    private final List<Account> mAccounts = new CopyOnWriteArrayList<>();

    private AccountManager() {
        // seed a mock account so callers get non-empty results
        mAccounts.add(new Account("user@mock.example", "com.example"));
    }

    /**
     * Returns the singleton AccountManager instance associated with the context.
     * The context parameter is accepted as Object to avoid a hard dependency on
     * android.content.Context in the shim classpath.
     */
    public static AccountManager get(Object context) {
        if (sInstance == null) {
            synchronized (AccountManager.class) {
                if (sInstance == null) {
                    sInstance = new AccountManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * Returns all accounts known to the manager.
     */
    public Account[] getAccounts() {
        return mAccounts.toArray(new Account[0]);
    }

    /**
     * Returns accounts of the given type, or all accounts if type is null.
     */
    public Account[] getAccountsByType(String type) {
        if (type == null) return getAccounts();
        List<Account> result = new ArrayList<>();
        for (Account a : mAccounts) {
            if (type.equals(a.type)) result.add(a);
        }
        return result.toArray(new Account[0]);
    }

    /**
     * Explicitly adds an account. Returns true if the account did not already exist.
     *
     * @param account  account to add
     * @param password ignored in this shim
     * @param extras   ignored (pass null); typed as Object to avoid Bundle dependency
     * @return true if added; false if already present
     */
    public boolean addAccountExplicitly(Account account, String password, Object extras) {
        if (account == null) throw new IllegalArgumentException("account cannot be null");
        if (mAccounts.contains(account)) return false;
        mAccounts.add(account);
        return true;
    }

    /**
     * Removes the given account. Returns true if found and removed.
     */
    public boolean removeAccount(Account account) {
        return mAccounts.remove(account);
    }

    /**
     * Returns true if the given account exists.
     */
    public boolean hasAccount(Account account) {
        return mAccounts.contains(account);
    }

    /**
     * Retrieve the stored password for an account (stub — always returns null).
     */
    public String getPassword(Account account) {
        return null;
    }

    /**
     * Retrieve user data associated with the account and key (stub — always returns null).
     */
    public String getUserData(Account account, String key) {
        return null;
    }

    @Override
    public String toString() {
        return "AccountManager{accounts=" + mAccounts + "}";
    }
}
