package android.os.storage;

import java.io.File;

/**
 * Android-compatible StorageVolume shim. Stub — represents a storage device.
 * On OpenHarmony, physical storage paths are accessed via the media/file access APIs.
 */
public class StorageVolume {
    private final String mDescription;
    private final boolean mPrimary;
    private final boolean mEmulated;
    private final boolean mRemovable;
    private final String mUuid;
    private final File mDirectory;

    public StorageVolume(String description, boolean primary, boolean emulated,
                         boolean removable, String uuid, File directory) {
        mDescription = description;
        mPrimary = primary;
        mEmulated = emulated;
        mRemovable = removable;
        mUuid = uuid;
        mDirectory = directory;
    }

    /** Human-readable description of this storage volume. */
    public String getDescription(android.content.Context context) {
        return mDescription;
    }

    /** Returns true if this is the primary (internal) storage volume. */
    public boolean isPrimary() { return mPrimary; }

    /** Returns true if this volume is emulated (e.g., internal emulated storage). */
    public boolean isEmulated() { return mEmulated; }

    /** Returns true if this volume can be physically removed (e.g., SD card). */
    public boolean isRemovable() { return mRemovable; }

    /**
     * Returns the filesystem UUID of this volume, or null if not applicable.
     * The primary emulated volume does not have a UUID.
     */
    public String getUuid() { return mUuid; }

    /** Returns the root directory of this storage volume. */
    public File getDirectory() { return mDirectory; }
}
