package android.accounts;

import java.util.Objects;

/**
 * Android-compatible Account shim. Represents a user account with a name and type.
 */
public class Account {

    /** The account name, e.g. "user@example.com". */
    public final String name;

    /** The account type, e.g. "com.google". */
    public final String type;

    public Account(String name, String type) {
        if (name == null) throw new IllegalArgumentException("name cannot be null");
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Account)) return false;
        Account other = (Account) obj;
        return Objects.equals(name, other.name) && Objects.equals(type, other.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "Account{name=" + name + ", type=" + type + "}";
    }
}
