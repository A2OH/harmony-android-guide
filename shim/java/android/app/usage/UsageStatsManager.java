package android.app.usage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shim: android.app.usage.UsageStatsManager
 * Tier 2 — provides access to per-app usage statistics.
 *
 * OH mapping: @ohos.bundleState / @ohos.resourceschedule.usageStatistics
 *   queryUsageStats()             → bundleState.queryBundleStateInfos()
 *   queryAndAggregateUsageStats() → bundleState.queryBundleActiveStates()
 *
 * This shim returns empty collections so that callers compile and run
 * without crashing; bridge integration is needed for real data.
 */
public class UsageStatsManager {

    // ── Interval type constants ──

    /** An interval type that spans a day. */
    public static final int INTERVAL_DAILY   = 0;
    /** An interval type that spans a week. */
    public static final int INTERVAL_WEEKLY  = 1;
    /** An interval type that spans a month. */
    public static final int INTERVAL_MONTHLY = 2;
    /** An interval type that spans a year. */
    public static final int INTERVAL_YEARLY  = 3;
    /**
     * An interval type that will use the best fit interval for the given time range.
     * Currently maps to INTERVAL_DAILY in the shim.
     */
    public static final int INTERVAL_BEST    = 4;

    // ── UsageStats inner class ──

    /**
     * Data holder for per-package usage information over a time interval.
     *
     * OH mapping: maps to bundleState.BundleStateInfo
     *   packageName         → bundleName
     *   firstTimeStamp      → infosBeginTime
     *   lastTimeStamp       → infosEndTime
     *   totalTimeInForeground → abilityInFgTotalTime (milliseconds)
     *   lastTimeUsed        → lastUsedTime
     */
    public static class UsageStats {
        /** The package name of the application. */
        public String packageName;
        /** The first time this package was used (ms since epoch). */
        public long firstTimeStamp;
        /** The end of the time interval (ms since epoch). */
        public long lastTimeStamp;
        /** The total time the app was in the foreground in ms. */
        public long totalTimeInForeground;
        /** The last time the app was used (ms since epoch). */
        public long lastTimeUsed;
        /** Total time the app was visible (API 29+). */
        public long totalTimeVisible;

        public UsageStats() {}

        public String getPackageName()           { return packageName; }
        public long   getFirstTimeStamp()        { return firstTimeStamp; }
        public long   getLastTimeStamp()         { return lastTimeStamp; }
        public long   getTotalTimeInForeground() { return totalTimeInForeground; }
        public long   getLastTimeUsed()          { return lastTimeUsed; }
        public long   getTotalTimeVisible()      { return totalTimeVisible; }

        /**
         * Merge another UsageStats from the same package into this one, extending
         * the covered time range and accumulating foreground time.
         */
        public void add(UsageStats right) {
            if (right == null || !right.packageName.equals(this.packageName)) return;
            if (right.firstTimeStamp < this.firstTimeStamp) this.firstTimeStamp = right.firstTimeStamp;
            if (right.lastTimeStamp  > this.lastTimeStamp)  this.lastTimeStamp  = right.lastTimeStamp;
            if (right.lastTimeUsed   > this.lastTimeUsed)   this.lastTimeUsed   = right.lastTimeUsed;
            this.totalTimeInForeground += right.totalTimeInForeground;
            this.totalTimeVisible      += right.totalTimeVisible;
        }
    }

    // ── API ──

    /**
     * Gets application usage stats for the given time range, aggregated with the given
     * interval type.
     *
     * @param intervalType One of {@link #INTERVAL_DAILY}, {@link #INTERVAL_WEEKLY},
     *                     {@link #INTERVAL_MONTHLY}, {@link #INTERVAL_YEARLY},
     *                     or {@link #INTERVAL_BEST}.
     * @param beginTime    Inclusive beginning of the range, measured in milliseconds
     *                     since the epoch.
     * @param endTime      Exclusive end of the range, measured in milliseconds since
     *                     the epoch.
     * @return A list of {@link UsageStats} or an empty list if none are available.
     */
    public List<UsageStats> queryUsageStats(int intervalType, long beginTime, long endTime) {
        return new ArrayList<>(); // stub — bridge layer populates this
    }

    /**
     * Gets application usage stats for the given time range and aggregates them
     * into a map keyed by package name.
     *
     * @param intervalType see {@link #queryUsageStats(int, long, long)}
     * @param beginTime    inclusive range start in ms since epoch
     * @param endTime      exclusive range end in ms since epoch
     * @return A {@link Map} of package name → aggregated {@link UsageStats},
     *         or an empty map if none are available.
     */
    public Map<String, UsageStats> queryAndAggregateUsageStats(int intervalType,
            long beginTime, long endTime) {
        List<UsageStats> list = queryUsageStats(intervalType, beginTime, endTime);
        Map<String, UsageStats> result = new HashMap<>();
        for (UsageStats stats : list) {
            UsageStats existing = result.get(stats.packageName);
            if (existing == null) {
                result.put(stats.packageName, stats);
            } else {
                existing.add(stats);
            }
        }
        return result;
    }
}
