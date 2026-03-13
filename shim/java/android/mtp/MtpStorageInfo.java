package android.mtp;

/**
 * Android-compatible MtpStorageInfo shim for A2OH migration.
 * Stub: all accessors return defaults.
 */
public final class MtpStorageInfo {

    private final int    mStorageId;
    private final String mDescription;
    private final String mVolumeIdentifier;
    private final long   mMaxCapacity;
    private final long   mFreeSpace;

    MtpStorageInfo(int storageId, String description, String volumeIdentifier,
                   long maxCapacity, long freeSpace) {
        mStorageId        = storageId;
        mDescription      = description;
        mVolumeIdentifier = volumeIdentifier;
        mMaxCapacity      = maxCapacity;
        mFreeSpace        = freeSpace;
    }

    public int getStorageId() {
        return mStorageId;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getVolumeIdentifier() {
        return mVolumeIdentifier;
    }

    public long getMaxCapacity() {
        return mMaxCapacity;
    }

    public long getFreeSpace() {
        return mFreeSpace;
    }
}
