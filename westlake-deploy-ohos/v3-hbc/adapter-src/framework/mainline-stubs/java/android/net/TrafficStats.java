// Mainline APEX stub.  android.net.TrafficStats (Connectivity APEX).
// Covers exactly the 5 method signatures referenced by framework.jar bytecode
// so ART pre-execution invoke check resolves without preallocated NCDFE.
// AOSP real source at packages/modules/Connectivity/framework-t/src/android/net/TrafficStats.java
// (1148 lines, transitive mainline deps; tech debt per memory:project_b19_tech_debt_postponed).

package android.net;

import android.content.Context;

public class TrafficStats {
    public static void attachSocketTagger() { /* no-op */ }
    public static int getAndSetThreadStatsTag(int tag) { return 0; }
    public static void init(Context context) { /* no-op */ }
    public static void setThreadStatsTag(int tag) { /* no-op */ }
    public static void setThreadStatsTagApp() { /* no-op */ }
}
