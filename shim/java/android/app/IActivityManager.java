// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4a -- shim/java/android/app/IActivityManager.java
//
// COMPILE-TIME STUB for android.app.IActivityManager.  AOSP marks the
// real interface @hide so it isn't in the public SDK android.jar; this
// stub supplies just enough surface for the Westlake shim to compile
// against the same AIDL methods that framework.jar's IActivityManager
// declares.
//
// At RUNTIME this class is stripped from aosp-shim.dex by the entry in
// scripts/framework_duplicates.txt -- framework.jar's real
// IActivityManager.Stub wins, and WestlakeActivityManagerService is
// loaded as a subclass of the real Stub.  Bytecode compatibility relies
// on (a) identical FQCN `android.app.IActivityManager$Stub`, (b) Stub
// being abstract and extending android.os.Binder, and (c) every method
// signature of WestlakeActivityManagerService matching the framework.jar
// IActivityManager.aidl surface (Android 16 IActivityManager has ~267
// abstract methods; ours has the same count).
//
// Reference: https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-16.0.0_r1/core/java/android/app/IActivityManager.aidl
//
// This stub declares only the methods the M4a Tier-1 set actually calls;
// WestlakeActivityManagerService is also responsible for implementing
// every method declared in framework.jar's IActivityManager (267 of them)
// to satisfy `new WestlakeActivityManagerService()` at runtime.

package android.app;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IActivityManager extends IInterface {

    static final java.lang.String DESCRIPTOR = "android.app.IActivityManager";

    // --- Tier-1 methods declared here for compile-time use ---

    int getCurrentUserId() throws RemoteException;
    java.util.List getRunningAppProcesses() throws RemoteException;
    void registerProcessObserver(IProcessObserver observer) throws RemoteException;
    void unregisterProcessObserver(IProcessObserver observer) throws RemoteException;
    android.content.Intent getIntentForIntentSender(android.content.IIntentSender sender)
            throws RemoteException;
    java.util.List getTasks(int maxNum) throws RemoteException;
    android.os.Debug.MemoryInfo[] getProcessMemoryInfo(int[] pids) throws RemoteException;
    boolean unbindService(IServiceConnection connection) throws RemoteException;

    // --- AIDL-generated Stub abstract class -------------------------------
    //
    // Real Stub in framework.jar extends Binder, implements IActivityManager,
    // and provides onTransact() that dispatches by TRANSACTION_xxx code.
    // Our stub matches that surface exactly.  Because Stub is abstract,
    // subclasses (like WestlakeActivityManagerService) must implement every
    // IActivityManager method or also be abstract.
    //
    // At runtime, the framework.jar Stub wins; the asInterface() and
    // attachInterface() wiring used by ServiceManager / queryLocalInterface
    // is the real one.
    public static abstract class Stub extends android.os.Binder implements IActivityManager {
        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }
        // Android 16 alternate constructor that accepts a PermissionEnforcer.
        // The real framework.jar Stub uses this in deferred to bypass the
        // ActivityThread-dependent default constructor.
        public Stub(android.os.PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
        }
        public static IActivityManager asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            if (i instanceof IActivityManager) return (IActivityManager) i;
            return null;  // shim doesn't implement Proxy
        }
        @Override public IBinder asBinder() { return this; }
    }
}
