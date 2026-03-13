package android.os;

/**
 * Android-compatible StrictMode shim. No-op policy enforcement.
 */
public final class StrictMode {

    private StrictMode() {}

    private static ThreadPolicy sThreadPolicy = new ThreadPolicy();
    private static VmPolicy     sVmPolicy     = new VmPolicy();

    public static void setThreadPolicy(ThreadPolicy policy) {
        sThreadPolicy = policy;
    }

    public static void setVmPolicy(VmPolicy policy) {
        sVmPolicy = policy;
    }

    public static ThreadPolicy getThreadPolicy() { return sThreadPolicy; }
    public static VmPolicy     getVmPolicy()     { return sVmPolicy; }

    public static ThreadPolicy allowThreadDiskReads() {
        return sThreadPolicy;
    }

    public static ThreadPolicy allowThreadDiskWrites() {
        return sThreadPolicy;
    }

    // ---- ThreadPolicy ----
    public static final class ThreadPolicy {
        public static final ThreadPolicy LAX = new ThreadPolicy();

        private ThreadPolicy() {}

        public static final class Builder {
            public Builder detectAll()  { return this; }
            public Builder penaltyLog() { return this; }
            public Builder penaltyDeath() { return this; }
            public Builder permitAll()  { return this; }
            public ThreadPolicy build() { return new ThreadPolicy(); }
        }
    }

    // ---- VmPolicy ----
    public static final class VmPolicy {
        public static final VmPolicy LAX = new VmPolicy();

        private VmPolicy() {}

        public static final class Builder {
            public Builder detectLeakedSqlLiteObjects() { return this; }
            public Builder detectLeakedClosableObjects() { return this; }
            public VmPolicy build() { return new VmPolicy(); }
        }
    }
}
