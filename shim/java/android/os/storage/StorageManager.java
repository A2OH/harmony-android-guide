package android.os.storage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Android-compatible StorageManager shim. Stub — returns a single primary volume.
 * On OpenHarmony, storage access uses the media library and file access extension APIs.
 */
public class StorageManager {

    private static final StorageVolume PRIMARY_VOLUME = new StorageVolume(
        "Internal Storage",
        /* primary */  true,
        /* emulated */ true,
        /* removable */ false,
        /* uuid */     null,
        /* directory */ new File("/storage/emulated/0")
    );

    public StorageManager() {}

    /**
     * Returns the list of all storage volumes available on the device.
     * Stub: returns only the single primary emulated volume.
     */
    public List<StorageVolume> getStorageVolumes() {
        return Arrays.asList(PRIMARY_VOLUME);
    }

    /**
     * Returns the primary storage volume.
     */
    public StorageVolume getPrimaryStorageVolume() {
        return PRIMARY_VOLUME;
    }
}
