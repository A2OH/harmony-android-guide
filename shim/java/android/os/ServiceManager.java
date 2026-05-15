// SPDX-License-Identifier: Apache-2.0
//
// Westlake M3++ — shim/java/android/os/ServiceManager.java
//
// Java <-> libbinder bridge.  Same shape as AOSP's hidden-API ServiceManager,
// thinned down to the surface our test programs and M4 framework callers
// actually exercise.  See aosp-libbinder-port/M3_NOTES.md for the path-A2
// design rationale.  M3++ additions:
//
//   * `addService(name, Binder)` passes the actual Java IBinder to the JNI
//     side, which extracts the underlying JavaBBinderHolder (via Binder
//     .mObject) and registers a sp<JavaBBinder> with servicemanager.
//
//   * `getService(name)` first tries `nativeGetLocalService` — if the
//     registered binder is same-process JavaBBinder, JNI returns the
//     ORIGINAL Java Binder object directly (preserving identity for the
//     AOSP `Stub.asInterface` queryLocalInterface optimization).  Otherwise
//     falls back to wrapping a NativeBinderProxy around the C++ handle.

package android.os;

public final class ServiceManager {
    private static final String TAG = "ServiceManager";

    static {
        try {
            System.loadLibrary("android_runtime_stub");
        } catch (UnsatisfiedLinkError e) {
            // Don't crash class loading — the first JNI call below will throw
            // a clearer error.  Log and continue.
            System.err.println("[" + TAG + "] loadLibrary(\"android_runtime_stub\") failed: " + e);
        }
    }

    private ServiceManager() {}

    // ----- Native bridge -----

    private static native long      nativeGetService(String name);
    /** M3++: returns Java Binder for same-process services, null otherwise. */
    private static native IBinder   nativeGetLocalService(String name);
    private static native String[]  nativeListServices();
    /** M3++: signature changed — now takes a Java IBinder, not a long. */
    private static native int       nativeAddService(String name, IBinder service);
    private static native boolean   nativeIsBinderAlive(long handle);
    private static native void      nativeReleaseBinder(long handle);
    private static native String    nativeBinderDescriptor(long handle);

    // ----- Public API -----

    public static IBinder getService(String name) {
        if (name == null) return null;
        // M3++ same-process optimization: try local lookup first.  When the
        // service was registered from this very process (e.g. AsInterfaceTest
        // doing addService("westlake.echo", echo) then getService("westlake.
        // echo")), JNI returns the SAME Java Binder object.  This is what
        // makes AOSP `IXxxService.Stub.asInterface(b)` skip marshaling.
        IBinder local = null;
        try {
            local = nativeGetLocalService(name);
        } catch (UnsatisfiedLinkError | NoSuchMethodError e) {
            // Older binder_jni_stub.cc without nativeGetLocalService.  Fall
            // through to the legacy long-handle path.
        }
        if (local != null) return local;

        long handle = nativeGetService(name);
        if (handle == 0) return null;
        return new NativeBinderProxy(name, handle);
    }

    public static IBinder checkService(String name) {
        return getService(name);
    }

    public static IBinder waitForService(String name) {
        return getService(name);
    }

    public static String[] listServices() {
        return nativeListServices();
    }

    public static void addService(String name, IBinder service) {
        addService(name, service, false, 0);
    }

    public static void addService(String name, IBinder service, boolean allowIsolated) {
        addService(name, service, allowIsolated, 0);
    }

    // CR1-fix: codex Tier 1 #1 -- AOSP's addService is `void` but throws on
    // failure.  Previously we silently swallowed the native return code, so a
    // failed register looked like success to callers (ServiceRegistrar would
    // then mark sRegistered=true and never retry).  Now: any non-zero status
    // is converted to a thrown RuntimeException with the status code embedded,
    // matching AOSP behavior.  Backwards compatible: signature unchanged
    // (void); callers that previously expected silent success now get an
    // exception they can catch (ServiceRegistrar.tryRegister does already).
    public static void addService(String name, IBinder service, boolean allowIsolated,
                                  int dumpPriority) {
        if (name == null) {
            throw new IllegalArgumentException("addService: name is null");
        }
        // M3++: pass the Java IBinder directly.  Native side picks the right
        // underlying C++ binder (JavaBBinder for android.os.Binder subclasses,
        // BAD_TYPE for non-null non-local binders -- see CR1-fix #2 in
        // binder_jni_stub.cc).
        int status = nativeAddService(name, service);
        if (status != 0) {
            throw new RuntimeException("ServiceManager.addService(\"" + name
                    + "\") native status=" + status);
        }
    }

    // ----- IBinder wrapper -----

    /**
     * IBinder proxy backed by a native sp<IBinder> handle from libbinder.
     * Minimal implementation suitable for M3 — no transact / linkToDeath.
     * That comes in M4 (per-service AIDL stub support).
     */
    static final class NativeBinderProxy implements IBinder {
        private final String mName;
        private long mHandle;

        NativeBinderProxy(String name, long handle) {
            mName = name;
            mHandle = handle;
        }

        @Override
        public String getInterfaceDescriptor() {
            if (mHandle == 0) return null;
            return nativeBinderDescriptor(mHandle);
        }

        @Override
        public boolean pingBinder() {
            return isBinderAlive();
        }

        @Override
        public boolean isBinderAlive() {
            return mHandle != 0 && nativeIsBinderAlive(mHandle);
        }

        @Override
        public IInterface queryLocalInterface(String descriptor) {
            return null;  // remote binder
        }

        @Override
        public void dump(java.io.FileDescriptor fd, String[] args) {
            // M3: no-op.
        }

        @Override
        public void dumpAsync(java.io.FileDescriptor fd, String[] args) {
            // M3: no-op.
        }

        @Override
        public boolean transact(int code, Parcel data, Parcel reply, int flags) {
            // M3: not yet implemented.  Callers that need transact will fail
            // here; that's M4's problem (per-service AIDL stubs).
            throw new UnsupportedOperationException(
                    "NativeBinderProxy.transact not supported in M3 (service: "
                    + mName + ", code: " + code + ")");
        }

        @Override
        public void linkToDeath(DeathRecipient recipient, int flags) {
            // M3: no-op; the test exits before any binder dies.
        }

        @Override
        public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
            return true;
        }

        @Override
        protected void finalize() throws Throwable {
            try {
                if (mHandle != 0) {
                    nativeReleaseBinder(mHandle);
                    mHandle = 0;
                }
            } finally {
                super.finalize();
            }
        }

        @Override
        public String toString() {
            return "NativeBinderProxy{name=" + mName + ", handle=0x"
                    + Long.toHexString(mHandle) + "}";
        }
    }
}
