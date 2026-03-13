package android.content;

import android.accounts.Account;
import android.os.Bundle;

/**
 * Android-compatible PeriodicSync shim.
 * Contains account, authority, extras, and period fields for sync scheduling.
 */
public class PeriodicSync {

    public final Account account;
    public final String authority;
    public final Bundle extras;
    public final long period;

    public PeriodicSync(Account account, String authority, Bundle extras, long period) {
        this.account = account;
        this.authority = authority;
        this.extras = extras != null ? new Bundle(extras) : new Bundle();
        this.period = period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeriodicSync)) return false;
        PeriodicSync that = (PeriodicSync) o;
        return period == that.period
                && account.equals(that.account)
                && authority.equals(that.authority);
    }

    @Override
    public int hashCode() {
        int result = account.hashCode();
        result = 31 * result + authority.hashCode();
        result = 31 * result + (int) (period ^ (period >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "PeriodicSync{account=" + account
                + ", authority=" + authority
                + ", period=" + period + "}";
    }
}
