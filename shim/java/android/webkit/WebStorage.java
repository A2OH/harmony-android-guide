package android.webkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Shim: android.webkit.WebStorage
 * OH mapping: @ohos.web.webview.WebStorage (ArkTS side)
 *
 * Provides access to the Web Storage API (localStorage / sessionStorage quota
 * and origin management).  On OpenHarmony the equivalent functionality is
 * accessed through the ArkTS WebStorage class; this Java shim mirrors the
 * Android API so existing code compiles and runs on JVM for unit-testing.
 *
 * All data is kept in-memory; the bridge runtime is responsible for bridging
 * calls to the real OH WebStorage at runtime.
 */
public class WebStorage {

    // ── Singleton ──

    private static final WebStorage INSTANCE = new WebStorage();

    /** Returns the singleton WebStorage instance. */
    public static WebStorage getInstance() {
        return INSTANCE;
    }

    // ── Inner types ──

    /**
     * Object interface for asynchronous quota/usage queries.
     * OH mapping: callback parameter in WebStorage.getOrigins / getUsageForOrigin.
     */
    public interface QuotaUpdater {
        /** Called to update the quota for an origin. */
        void updateQuota(long newQuota);
    }

    /**
     * Represents a single web storage origin with its usage and quota figures.
     * OH equivalent: WebStorageItem in ArkTS.
     */
    public static class Origin {
        private final String origin;
        private final long   usage;
        private final long   quota;

        /** Constructs an Origin record. */
        public Origin(String origin, long usage, long quota) {
            this.origin = origin;
            this.usage  = usage;
            this.quota  = quota;
        }

        /** Returns the origin string (e.g. {@code "https://example.com"}). */
        public String getOrigin() { return origin; }

        /** Returns the amount of storage currently used by this origin (bytes). */
        public long getUsage()    { return usage;  }

        /** Returns the storage quota for this origin (bytes). */
        public long getQuota()    { return quota;  }
    }

    // ── Internal state ──

    /** Usage per origin (bytes). */
    private final Map<String, Long> usageMap = new HashMap<>();
    /** Quota per origin (bytes). */
    private final Map<String, Long> quotaMap = new HashMap<>();

    // Package-private constructor — use getInstance().
    WebStorage() {}

    // ── Data management ──

    /**
     * Clears all stored data for all origins.
     * OH equivalent: WebStorage.deleteAllData()
     */
    public void deleteAllData() {
        usageMap.clear();
        quotaMap.clear();
    }

    /**
     * Clears all stored data for the specified origin.
     * OH equivalent: WebStorage.deleteOrigin(origin)
     *
     * @param origin  origin string, e.g. {@code "https://example.com"}
     */
    public void deleteOrigin(String origin) {
        if (origin == null) return;
        usageMap.remove(origin);
        quotaMap.remove(origin);
    }

    // ── Queries ──

    /**
     * Returns a map of all known origins to their {@link Origin} records.
     * OH equivalent: WebStorage.getOrigins(callback)
     *
     * In this shim the result is delivered synchronously via the callback.
     */
    public void getOrigins(ValueCallback<Map<String, Origin>> callback) {
        if (callback == null) return;
        Map<String, Origin> result = new HashMap<>();
        Set<String> keys = usageMap.keySet();
        for (String key : keys) {
            long usage = usageMap.containsKey(key) ? usageMap.get(key) : 0L;
            long quota = quotaMap.containsKey(key) ? quotaMap.get(key) : 0L;
            result.put(key, new Origin(key, usage, quota));
        }
        callback.onReceiveValue(result);
    }

    /**
     * Returns the storage usage for the specified origin via callback.
     * OH equivalent: WebStorage.getOriginUsage(origin, callback)
     */
    public void getUsageForOrigin(String origin, ValueCallback<Long> callback) {
        if (callback == null) return;
        long usage = (origin != null && usageMap.containsKey(origin))
                ? usageMap.get(origin) : 0L;
        callback.onReceiveValue(usage);
    }

    /**
     * Returns the storage quota for the specified origin via callback.
     * OH equivalent: WebStorage.getOriginQuota(origin, callback)
     */
    public void getQuotaForOrigin(String origin, ValueCallback<Long> callback) {
        if (callback == null) return;
        long quota = (origin != null && quotaMap.containsKey(origin))
                ? quotaMap.get(origin) : 0L;
        callback.onReceiveValue(quota);
    }
}
