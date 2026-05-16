/*
 * OhTokenRegistry.java
 *
 * BCP-resident registry that bridges Android adapter-side IBinder tokens to
 * OH AbilityRecord token raw pointers (uint64 / long).
 *
 * Spec: doc/window_manager_ipc_adapter_design.html §3.1.4.6 / §3.1.5.6.1 / §3.1.5.6.3 / §3.1.5.6.4
 *
 * Lives in adapter.core (BCP / oh-adapter-framework.jar) so both:
 *   - adapter.activity.AppSchedulerBridge   (oh-adapter-runtime.jar, populates)
 *   - adapter.window.WindowSessionAdapter   (BCP, reads on addToDisplay)
 * can reference it without circular jar dependencies.
 *
 * Three concurrent maps:
 *   ohToAndroid       OH token raw addr   -> Android IBinder (canonical reverse map)
 *   androidToOh       Android IBinder     -> OH token raw addr (forward lookup, used by addToDisplay)
 *   recordToAndroid   OH AbilityRecord id -> Android IBinder (legacy LifecycleAdapter path)
 *   androidToMainSession  Android IBinder -> OH SCB main SceneSession persistentId (§3.1.5.6.3)
 */
package adapter.core;

import android.os.Binder;
import android.os.IBinder;

import java.util.concurrent.ConcurrentHashMap;

public final class OhTokenRegistry {

    private static final ConcurrentHashMap<Long, IBinder> sOhToAndroid = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<IBinder, Long> sAndroidToOh = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, IBinder> sRecordToAndroid = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<IBinder, Integer> sAndroidToMainSession = new ConcurrentHashMap<>();

    private OhTokenRegistry() {}

    /**
     * Get-or-create an Android-side IBinder for the given OH AbilityRecord token.
     * Caches both directions so subsequent lookups are stable. Called from
     * AppSchedulerBridge.nativeOnScheduleLaunchAbility (B.47 path).
     */
    public static IBinder acquireAndroidToken(int abilityRecordId, long ohTokenAddr) {
        if (ohTokenAddr != 0L) {
            IBinder existing = sOhToAndroid.get(ohTokenAddr);
            if (existing != null) return existing;
        }
        IBinder b = new Binder();
        if (ohTokenAddr != 0L) {
            sOhToAndroid.put(ohTokenAddr, b);
            sAndroidToOh.put(b, ohTokenAddr);
        }
        sRecordToAndroid.put(abilityRecordId, b);
        return b;
    }

    public static IBinder findByOhToken(long ohTokenAddr) {
        return sOhToAndroid.get(ohTokenAddr);
    }

    /** §3.1.5.6.1 — used by WindowSessionAdapter.addToDisplay to thread OH token to SCB. */
    public static Long findOhToken(IBinder androidBinder) {
        return sAndroidToOh.get(androidBinder);
    }

    public static IBinder findByRecordId(int abilityRecordId) {
        return sRecordToAndroid.get(abilityRecordId);
    }

    /**
     * §3.1.5.6.3 — record OH SCB-auto-created main SceneSession persistentId
     * (when SCB binds an existing main session to our SessionStageAdapter).
     * Triggers the addToDisplay reuse path instead of CreateAndConnect.
     */
    public static void setMainSessionId(long ohTokenAddr, int persistentId) {
        IBinder b = sOhToAndroid.get(ohTokenAddr);
        if (b != null && persistentId > 0) {
            sAndroidToMainSession.put(b, persistentId);
        }
    }

    public static int getMainSessionId(IBinder androidBinder) {
        if (androidBinder == null) return -1;
        Integer id = sAndroidToMainSession.get(androidBinder);
        return id == null ? -1 : id.intValue();
    }

    /**
     * §3.1.5.6.4 — DeathRecipient cleanup hook called from
     * app_scheduler_adapter.cpp::TokenDeathRecipient::OnRemoteDied via JNI.
     * Drops all maps keyed by the dead OH token.
     */
    public static void removeByOhToken(long ohTokenAddr) {
        IBinder b = sOhToAndroid.remove(ohTokenAddr);
        if (b != null) {
            sAndroidToOh.remove(b);
            sAndroidToMainSession.remove(b);
            // sRecordToAndroid is keyed by abilityRecordId — left alone here;
            // LifecycleAdapter cleans on terminate.
        }
    }
}
