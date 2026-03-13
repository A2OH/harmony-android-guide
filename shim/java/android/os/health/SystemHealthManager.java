package android.os.health;

/**
 * A2OH shim: SystemHealthManager - provides health snapshots per UID.
 * takeMyUidSnapshot() and takeUidSnapshot() return empty HealthStats stubs.
 */
public class SystemHealthManager {

    public SystemHealthManager() {}

    /**
     * Returns a HealthStats snapshot for the calling UID.
     */
    public HealthStats takeMyUidSnapshot() {
        return new HealthStats("uid");
    }

    /**
     * Returns a HealthStats snapshot for the specified UID.
     *
     * @param uid The UID to snapshot.
     */
    public HealthStats takeUidSnapshot(int uid) {
        return new HealthStats("uid");
    }
}
