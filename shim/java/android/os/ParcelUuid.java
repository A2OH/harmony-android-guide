package android.os;

import java.util.UUID;

/**
 * Android-compatible ParcelUuid shim. Wraps java.util.UUID.
 */
public final class ParcelUuid {
    private final UUID mUuid;

    public ParcelUuid(UUID uuid) {
        if (uuid == null) throw new IllegalArgumentException("uuid must not be null");
        mUuid = uuid;
    }

    public static ParcelUuid fromString(String uuid) {
        return new ParcelUuid(UUID.fromString(uuid));
    }

    public UUID getUuid() {
        return mUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParcelUuid)) return false;
        return mUuid.equals(((ParcelUuid) o).mUuid);
    }

    @Override
    public int hashCode() {
        return mUuid.hashCode();
    }

    @Override
    public String toString() {
        return mUuid.toString();
    }
}
