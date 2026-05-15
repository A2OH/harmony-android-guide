// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4b -- shim/java/android/view/IWindowManager.java
//
// COMPILE-TIME STUB for android.view.IWindowManager.  AOSP marks the real
// interface @hide so it isn't in the public SDK android.jar; this stub
// supplies just enough surface for the Westlake shim to compile against
// the same AIDL methods that framework.jar's IWindowManager declares.
//
// At RUNTIME this class is stripped from aosp-shim.dex by the entry in
// scripts/framework_duplicates.txt -- framework.jar's real IWindowManager.Stub
// wins, and WestlakeWindowManagerService is loaded as a subclass of the
// real Stub.  Bytecode compatibility relies on (a) identical FQCN
// `android.view.IWindowManager$Stub`, (b) Stub being abstract and extending
// android.os.Binder, and (c) every method signature of
// WestlakeWindowManagerService matching the framework.jar IWindowManager.aidl
// surface (Android 16 IWindowManager has 154 abstract methods).
//
// Reference: Android 16 framework.jar dexdump of
// android.view.IWindowManager$Stub (154 methods, verified via
//   /home/dspfac/android-to-openharmony-migration/ohos-deploy/arm64-a15/framework.jar
//   classes4.dex).
//
// This stub declares only the Tier-1 methods (plus the few helpers used
// by the synthetic test).  WestlakeWindowManagerService is also responsible
// for implementing every method declared in framework.jar's IWindowManager
// to satisfy `new WestlakeWindowManagerService()` at runtime.

package android.view;

import android.graphics.Point;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IWindowManager extends IInterface {

    static final java.lang.String DESCRIPTOR = "android.view.IWindowManager";

    // Constants matching the framework.jar interface so that tests/callers
    // can reference IWindowManager.FIXED_TO_USER_ROTATION_DEFAULT etc.
    public static final int FIXED_TO_USER_ROTATION_DEFAULT = 0;
    public static final int FIXED_TO_USER_ROTATION_DISABLED = 1;
    public static final int FIXED_TO_USER_ROTATION_ENABLED = 2;
    public static final int FIXED_TO_USER_ROTATION_IF_NO_AUTO_ROTATION = 3;

    // --- Tier-1 methods declared here for compile-time use ---

    IWindowSession openSession(IWindowSessionCallback callback) throws RemoteException;
    void getInitialDisplaySize(int displayId, Point outSize) throws RemoteException;
    void getBaseDisplaySize(int displayId, Point outSize) throws RemoteException;
    float getCurrentAnimatorScale() throws RemoteException;
    float getAnimationScale(int which) throws RemoteException;
    float[] getAnimationScales() throws RemoteException;
    int watchRotation(IRotationWatcher watcher, int displayId) throws RemoteException;
    int getDefaultDisplayRotation() throws RemoteException;
    void addWindowToken(IBinder token, int type, int displayId, android.os.Bundle options) throws RemoteException;
    void removeWindowToken(IBinder token, int displayId) throws RemoteException;
    void setEventDispatching(boolean enabled) throws RemoteException;

    // --- AIDL-generated Stub abstract class -------------------------------
    //
    // Real Stub in framework.jar extends Binder, implements IWindowManager,
    // and provides onTransact() that dispatches by TRANSACTION_xxx code.
    // Our stub matches that surface exactly.  Because Stub is abstract,
    // subclasses (like WestlakeWindowManagerService) must implement every
    // IWindowManager method or also be abstract.
    //
    // At runtime, the framework.jar Stub wins; the asInterface() and
    // attachInterface() wiring used by ServiceManager / queryLocalInterface
    // is the real one.
    public static abstract class Stub extends android.os.Binder implements IWindowManager {
        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }
        // Android 16 alternate constructor that accepts a PermissionEnforcer.
        // Same pattern as IActivityManager$Stub: the no-arg ctor in
        // framework.jar IWindowManager$Stub indirects through
        // ActivityThread.currentActivityThread().getSystemContext() which
        // NPEs in the Westlake sandbox.  Subclasses pass a no-op
        // PermissionEnforcer to bypass that path entirely.
        public Stub(android.os.PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
        }
        public static IWindowManager asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            if (i instanceof IWindowManager) return (IWindowManager) i;
            return null;  // shim doesn't implement Proxy
        }
        @Override public IBinder asBinder() { return this; }
    }
}
