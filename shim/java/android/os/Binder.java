package android.os;

import java.io.FileDescriptor;

/**
 * Android-compatible Binder shim, M3++.
 *
 * Base class for remotable objects.  In M3++ this class is the Java counterpart
 * of the C++ {@code JavaBBinder} (see aosp-libbinder-port/native/
 * JavaBBinderHolder.cpp).  The native {@code getNativeBBinderHolder()} mints a
 * {@code JavaBBinderHolder*} on first construction; that pointer is stored in
 * {@link #mObject} so that {@link android.os.ServiceManager#addService} can
 * re-find the same C++ BBinder identity when registering the same Java Binder
 * multiple times.
 *
 * The {@link #queryLocalInterface(String)} optimization implements the
 * standard AOSP {@code Stub.asInterface(IBinder)} elision: if {@link
 * #attachInterface(IInterface, String)} has been called with a matching
 * descriptor, queryLocalInterface returns the IInterface directly — caller
 * never marshals.  This is what unlocks M4 services running in-process.
 */
public class Binder implements IBinder {
    /** Pointer to a C++ JavaBBinderHolder.  Set by the native ctor. */
    private final long mObject;

    /** Interface set by attachInterface — drives queryLocalInterface. */
    private IInterface mOwner;
    private String mDescriptor = "";

    public Binder() {
        this(null);
    }

    public Binder(String descriptor) {
        // M3++: native side creates a JavaBBinderHolder and returns its
        // pointer.  Java keeps it in mObject for the lifetime of this Binder.
        // If the native side isn't available (e.g. dalvikvm was built without
        // libbinder), getNativeBBinderHolder may not be registered and the
        // call will throw UnsatisfiedLinkError.  We catch that and fall back
        // to mObject=0 — addService will then register an anonymous token.
        long h = 0L;
        try {
            h = getNativeBBinderHolder();
        } catch (UnsatisfiedLinkError ule) {
            // Native shim not registered; M3++ JNI bridge missing.
            // Stay in pure-Java mode; transact still works in-process via
            // queryLocalInterface.
        }
        mObject = h;
        mDescriptor = descriptor != null ? descriptor : "";
    }

    // --- Native bridge (registered by binder_jni_stub.cc) ---
    // M3++: all of these are RegisterNatives'd by JNI_OnLoad_binder_with_cl.
    // If the native bridge isn't loaded, calls throw UnsatisfiedLinkError —
    // matching AOSP's behavior when libandroid_runtime isn't available.

    private static native long getNativeBBinderHolder();
    private static native long getNativeFinalizer();
    private static native void nativeDestroy(long handle);

    public static native int getCallingPid();
    public static native int getCallingUid();
    public static native long clearCallingIdentity();
    public static native void restoreCallingIdentity(long token);
    public static native void flushPendingCommands();

    public static void joinThreadPool() {
        // no-op in shim
    }

    // --- IBinder implementation ---

    @Override
    public String getInterfaceDescriptor() throws RemoteException {
        return mDescriptor;
    }

    @Override
    public boolean pingBinder() {
        return true;
    }

    @Override
    public boolean isBinderAlive() {
        return true;
    }

    /**
     * AOSP-compatible {@code queryLocalInterface}.  After
     * {@link #attachInterface(IInterface, String)}, this returns the
     * registered {@code mOwner} when the descriptor matches.  When called
     * before attachInterface (or when descriptor doesn't match), returns
     * null — caller (AOSP-style {@code Stub.asInterface}) then falls back
     * to a Proxy wrapper that goes through transact.
     */
    @Override
    public IInterface queryLocalInterface(String descriptor) {
        if (mDescriptor != null && mDescriptor.equals(descriptor)) {
            return mOwner;
        }
        return null;
    }

    @Override
    public void dump(FileDescriptor fd, String[] args) throws RemoteException {
        // no-op stub
    }

    @Override
    public void dumpAsync(FileDescriptor fd, String[] args) throws RemoteException {
        // no-op stub
    }

    @Override
    public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        // Same-process transact — dispatch directly to onTransact.  This is
        // the path used by AOSP Stub.transact when a proxy is forced.  When
        // queryLocalInterface returns mOwner directly, transact is bypassed
        // entirely (the IInterface method is called via Java vtable).
        return onTransact(code, data, reply, flags);
    }

    @Override
    public void linkToDeath(IBinder.DeathRecipient recipient, int flags) throws RemoteException {
        // no-op — in-process binder never dies
    }

    @Override
    public boolean unlinkToDeath(IBinder.DeathRecipient recipient, int flags) {
        return true;
    }

    /**
     * Override in subclasses to handle incoming transactions.
     */
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        return false;
    }

    /**
     * AOSP-compatible attachInterface.  Used by Stub constructors (in
     * generated AIDL code) to register the IInterface that
     * queryLocalInterface should return for a given descriptor.
     */
    public void attachInterface(IInterface owner, String descriptor) {
        mOwner = owner;
        mDescriptor = descriptor != null ? descriptor : "";
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mObject != 0) {
                try { nativeDestroy(mObject); } catch (UnsatisfiedLinkError ignore) {}
            }
        } finally {
            super.finalize();
        }
    }

    /**
     * Internal accessor for ServiceManager — returns the native JavaBBinderHolder
     * pointer so the JNI addService can register the same C++ BBinder identity.
     * @hide
     */
    public final long getNativeHandle() {
        return mObject;
    }
}
