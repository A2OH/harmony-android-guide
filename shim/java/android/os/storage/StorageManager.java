package android.os.storage;

public class StorageManager {
    public StorageManager() {}

    public static final int ACTION_MANAGE_STORAGE = 0;
    public static final int EXTRA_REQUESTED_BYTES = 0;
    public static final int EXTRA_UUID = 0;
    public static final int UUID_DEFAULT = 0;
    public Object getMountedObbPath(Object p0) { return null; }
    public boolean isAllocationSupported(Object p0) { return false; }
    public boolean isCacheBehaviorGroup(Object p0) { return false; }
    public boolean isCacheBehaviorTombstone(Object p0) { return false; }
    public boolean isCheckpointSupported() { return false; }
    public boolean isEncrypted(Object p0) { return false; }
    public boolean isObbMounted(Object p0) { return false; }
    public boolean mountObb(Object p0, Object p1, Object p2) { return false; }
    public void registerStorageVolumeCallback(Object p0, Object p1) {}
    public void setCacheBehaviorGroup(Object p0, Object p1) {}
    public void setCacheBehaviorTombstone(Object p0, Object p1) {}
    public boolean unmountObb(Object p0, Object p1, Object p2) { return false; }
    public void unregisterStorageVolumeCallback(Object p0) {}
}
