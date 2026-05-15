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

    public static Span enterCriticalSpan(String name) { return new Span(); }
    public static boolean vmIncorrectContextUseEnabled() { return false; }
    public static void onIncorrectContextUsed(String message, Exception e) {}

    public static class Span {
        public void finish() {}
    }

    // ---- ThreadPolicy ----
    public static final class ThreadPolicy {
        public static final ThreadPolicy LAX = new ThreadPolicy();

        private ThreadPolicy() {}

        public static final class Builder {
            public Builder() {}
            public Builder(ThreadPolicy policy) {}
            public Builder detectNetwork() { return this; }
            public Builder detectDiskReads() { return this; }
            public Builder detectDiskWrites() { return this; }
            public Builder detectCustomSlowCalls() { return this; }
            public Builder detectResourceMismatches() { return this; }
            public Builder detectAll()  { return this; }
            public Builder penaltyLog() { return this; }
            public Builder penaltyDialog() { return this; }
            public Builder penaltyDeath() { return this; }
            public Builder penaltyDeathOnNetwork() { return this; }
            public Builder penaltyDropBox() { return this; }
            public Builder penaltyFlashScreen() { return this; }
            public Builder permitNetwork() { return this; }
            public Builder permitDiskReads() { return this; }
            public Builder permitDiskWrites() { return this; }
            public Builder permitCustomSlowCalls() { return this; }
            public Builder permitUnbufferedIo() { return this; }
            public Builder detectUnbufferedIo() { return this; }
            public Builder permitAll()  { return this; }
            public ThreadPolicy build() { return new ThreadPolicy(); }
        }
    }

    // ---- VmPolicy ----
    public static final class VmPolicy {
        public static final VmPolicy LAX = new VmPolicy();

        private VmPolicy() {}

        public static final class Builder {
            public Builder() {}
            public Builder(VmPolicy policy) {}
            public Builder detectAll() { return this; }
            public Builder detectActivityLeaks() { return this; }
            public Builder detectCleartextNetwork() { return this; }
            public Builder detectContentUriWithoutPermission() { return this; }
            public Builder detectCredentialProtectedWhileLocked() { return this; }
            public Builder detectFileUriExposure() { return this; }
            public Builder detectLeakedSqlLiteObjects() { return this; }
            public Builder detectLeakedClosableObjects() { return this; }
            public Builder detectLeakedRegistrationObjects() { return this; }
            public Builder detectNonSdkApiUsage() { return this; }
            public Builder detectResourceMismatches() { return this; }
            public Builder detectUnsafeIntentLaunch() { return this; }
            public Builder permitNonSdkApiUsage() { return this; }
            public Builder permitUnsafeIntentLaunch() { return this; }
            public Builder penaltyDeath() { return this; }
            public Builder penaltyDropBox() { return this; }
            public Builder penaltyLog() { return this; }
            public VmPolicy build() { return new VmPolicy(); }
        }
    }

    /** Strict-mode violation payload passed by
     *  IActivityManager.handleApplicationStrictModeViolation.  Stub for
     *  compile-time only; framework.jar provides the real class. */
    public static class ViolationInfo {
        public ViolationInfo() {}
    }
}
