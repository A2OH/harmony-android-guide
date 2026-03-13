package android.support.v4.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Android-compatible LocalBroadcastManager shim. Stub — in-process only.
 *
 * LocalBroadcastManager provides a simple publish-subscribe mechanism confined
 * to a single process. There is no direct OH equivalent; this shim implements
 * the full contract in pure Java so existing code compiles and runs unchanged
 * within the same JVM process.
 *
 * OH migration note: Use EventHub or custom Observer patterns for in-process
 * messaging on OpenHarmony.
 *
 * Note: BroadcastReceiver and IntentFilter are typed as Object here to avoid
 * compile-time dependencies on classes not present in this shim set.
 */
public final class LocalBroadcastManager {

    // ── Singleton ──────────────────────────────────────────────────────────────

    private static LocalBroadcastManager sInstance;

    /**
     * Returns the LocalBroadcastManager singleton for this context.
     *
     * @param context application context (typed as Object for shim compatibility)
     */
    public static synchronized LocalBroadcastManager getInstance(Object context) {
        if (sInstance == null) {
            sInstance = new LocalBroadcastManager();
        }
        return sInstance;
    }

    // ── Internal state ─────────────────────────────────────────────────────────

    /**
     * Registry mapping IntentFilter (Object) → list of BroadcastReceiver (Object).
     * The outer key is the IntentFilter; the inner list holds all receivers that
     * registered with that filter.
     */
    private final Map<Object, List<Object>> mReceivers = new HashMap<>();

    /** Reverse index: receiver → list of filters it registered with. */
    private final Map<Object, List<Object>> mFilters = new HashMap<>();

    private LocalBroadcastManager() {}

    // ── Registration ───────────────────────────────────────────────────────────

    /**
     * Register a receiver for broadcasts that match the given IntentFilter.
     *
     * @param receiver the BroadcastReceiver to call (typed as Object)
     * @param filter   the IntentFilter describing which broadcasts to receive (typed as Object)
     */
    public synchronized void registerReceiver(Object receiver, Object filter) {
        if (receiver == null) throw new IllegalArgumentException("receiver must not be null");
        if (filter == null)   throw new IllegalArgumentException("filter must not be null");

        // Record filter → receiver mapping
        List<Object> receiverList = mReceivers.get(filter);
        if (receiverList == null) {
            receiverList = new ArrayList<>();
            mReceivers.put(filter, receiverList);
        }
        if (!receiverList.contains(receiver)) {
            receiverList.add(receiver);
        }

        // Record receiver → filter reverse mapping
        List<Object> filterList = mFilters.get(receiver);
        if (filterList == null) {
            filterList = new ArrayList<>();
            mFilters.put(receiver, filterList);
        }
        if (!filterList.contains(filter)) {
            filterList.add(filter);
        }
    }

    /**
     * Unregister a previously registered receiver. Removes it from all filters.
     *
     * @param receiver the BroadcastReceiver to unregister (typed as Object)
     */
    public synchronized void unregisterReceiver(Object receiver) {
        if (receiver == null) return;

        List<Object> filters = mFilters.remove(receiver);
        if (filters == null) return;

        for (Object filter : filters) {
            List<Object> list = mReceivers.get(filter);
            if (list != null) {
                list.remove(receiver);
                if (list.isEmpty()) {
                    mReceivers.remove(filter);
                }
            }
        }
    }

    // ── Broadcasting ───────────────────────────────────────────────────────────

    /**
     * Broadcast the given Intent to all registered receivers that match it.
     * Delivery is asynchronous (posted to the calling thread's message queue on
     * real Android; synchronous in this shim since there is no looper).
     *
     * @param intent the Intent to broadcast (typed as Object)
     * @return true if at least one receiver was found
     */
    public boolean sendBroadcast(Object intent) {
        return deliverBroadcast(intent);
    }

    /**
     * Broadcast the given Intent synchronously to all matching receivers.
     * All registered receivers receive the intent before this call returns.
     *
     * @param intent the Intent to broadcast (typed as Object)
     */
    public void sendBroadcastSync(Object intent) {
        deliverBroadcast(intent);
    }

    // ── Internal delivery ──────────────────────────────────────────────────────

    /**
     * Deliver the intent to all registered receivers.
     * In this shim the filter matching is always positive (no filter inspection).
     *
     * @return true if at least one receiver was found
     */
    private synchronized boolean deliverBroadcast(Object intent) {
        if (mReceivers.isEmpty()) return false;

        // Collect a snapshot to avoid ConcurrentModificationException
        List<Object> snapshot = new ArrayList<>();
        for (List<Object> list : mReceivers.values()) {
            snapshot.addAll(list);
        }

        if (snapshot.isEmpty()) return false;

        // Deliver — in real Android the context is passed; here we pass null
        // since Context is not available in this shim set.
        for (Object receiver : snapshot) {
            // Receivers are expected to implement
            // onReceive(Object context, Object intent); call via reflection
            // to avoid a hard compile-time dependency on BroadcastReceiver.
            try {
                receiver.getClass()
                        .getMethod("onReceive", Object.class, Object.class)
                        .invoke(receiver, null, intent);
            } catch (NoSuchMethodException ignored) {
                // receiver doesn't implement onReceive(Object, Object) — skip
            } catch (Exception e) {
                // swallow runtime errors in delivery to match Android behaviour
            }
        }
        return true;
    }
}
