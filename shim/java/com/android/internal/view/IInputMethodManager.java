// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- shim/java/com/android/internal/view/IInputMethodManager.java
//
// COMPILE-TIME STUB for com.android.internal.view.IInputMethodManager.
// (Note: the AIDL lives under com.android.internal.view, NOT under
// android.view.inputmethod -- this is the public-classpath alias for the
// system-server input-method binder service.)
//
// At RUNTIME this class is stripped from aosp-shim.dex by the entry in
// scripts/framework_duplicates.txt -- framework.jar's real
// IInputMethodManager.Stub wins, and WestlakeInputMethodManagerService
// is loaded as a subclass of the real Stub.  Bytecode compatibility
// relies on (a) identical FQCN `com.android.internal.view.IInputMethodManager$Stub`,
// (b) Stub being abstract and extending android.os.Binder, and (c)
// every method signature of WestlakeInputMethodManagerService matching
// the framework.jar IInputMethodManager.aidl surface (Android 16 has
// 37 abstract methods).
//
// Method count: 37 declared abstract methods, matching Android 16
// IInputMethodManager.aidl.

package com.android.internal.view;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IInputMethodManager extends IInterface {

    static final String DESCRIPTOR = "com.android.internal.view.IInputMethodManager";

    // --- 37 abstract methods (generated from baksmali on framework.jar Android 16) ---

    boolean acceptStylusHandwritingDelegation(com.android.internal.inputmethod.IInputMethodClient p0, int p1, java.lang.String p2, java.lang.String p3, int p4) throws android.os.RemoteException;
    void acceptStylusHandwritingDelegationAsync(com.android.internal.inputmethod.IInputMethodClient p0, int p1, java.lang.String p2, java.lang.String p3, int p4, com.android.internal.inputmethod.IBooleanListener p5) throws android.os.RemoteException;
    void addClient(com.android.internal.inputmethod.IInputMethodClient p0, com.android.internal.inputmethod.IRemoteInputConnection p1, int p2) throws android.os.RemoteException;
    void addVirtualStylusIdForTestSession(com.android.internal.inputmethod.IInputMethodClient p0) throws android.os.RemoteException;
    android.view.inputmethod.InputMethodInfo getCurrentInputMethodInfoAsUser(int p0) throws android.os.RemoteException;
    android.view.inputmethod.InputMethodSubtype getCurrentInputMethodSubtype(int p0) throws android.os.RemoteException;
    com.android.internal.inputmethod.InputMethodInfoSafeList getEnabledInputMethodList(int p0) throws android.os.RemoteException;
    java.util.List getEnabledInputMethodListLegacy(int p0) throws android.os.RemoteException;
    java.util.List getEnabledInputMethodSubtypeList(java.lang.String p0, boolean p1, int p2) throws android.os.RemoteException;
    com.android.internal.inputmethod.IImeTracker getImeTrackerService() throws android.os.RemoteException;
    com.android.internal.inputmethod.InputMethodInfoSafeList getInputMethodList(int p0, int p1) throws android.os.RemoteException;
    java.util.List getInputMethodListLegacy(int p0, int p1) throws android.os.RemoteException;
    int getInputMethodWindowVisibleHeight(com.android.internal.inputmethod.IInputMethodClient p0) throws android.os.RemoteException;
    android.view.inputmethod.InputMethodSubtype getLastInputMethodSubtype(int p0) throws android.os.RemoteException;
    boolean hideSoftInput(com.android.internal.inputmethod.IInputMethodClient p0, android.os.IBinder p1, android.view.inputmethod.ImeTracker.Token p2, int p3, android.os.ResultReceiver p4, int p5, boolean p6) throws android.os.RemoteException;
    void hideSoftInputFromServerForTest() throws android.os.RemoteException;
    boolean isImeTraceEnabled() throws android.os.RemoteException;
    boolean isInputMethodPickerShownForTest() throws android.os.RemoteException;
    boolean isStylusHandwritingAvailableAsUser(int p0, boolean p1) throws android.os.RemoteException;
    void onImeSwitchButtonClickFromSystem(int p0) throws android.os.RemoteException;
    void prepareStylusHandwritingDelegation(com.android.internal.inputmethod.IInputMethodClient p0, int p1, java.lang.String p2, java.lang.String p3) throws android.os.RemoteException;
    void removeImeSurface(int p0) throws android.os.RemoteException;
    void removeImeSurfaceFromWindowAsync(android.os.IBinder p0) throws android.os.RemoteException;
    void reportPerceptibleAsync(android.os.IBinder p0, boolean p1) throws android.os.RemoteException;
    void setAdditionalInputMethodSubtypes(java.lang.String p0, android.view.inputmethod.InputMethodSubtype[] p1, int p2) throws android.os.RemoteException;
    void setExplicitlyEnabledInputMethodSubtypes(java.lang.String p0, int[] p1, int p2) throws android.os.RemoteException;
    void setStylusWindowIdleTimeoutForTest(com.android.internal.inputmethod.IInputMethodClient p0, long p1) throws android.os.RemoteException;
    void showInputMethodPickerFromClient(com.android.internal.inputmethod.IInputMethodClient p0, int p1) throws android.os.RemoteException;
    void showInputMethodPickerFromSystem(int p0, int p1) throws android.os.RemoteException;
    boolean showSoftInput(com.android.internal.inputmethod.IInputMethodClient p0, android.os.IBinder p1, android.view.inputmethod.ImeTracker.Token p2, int p3, int p4, android.os.ResultReceiver p5, int p6, boolean p7) throws android.os.RemoteException;
    void startConnectionlessStylusHandwriting(com.android.internal.inputmethod.IInputMethodClient p0, int p1, android.view.inputmethod.CursorAnchorInfo p2, java.lang.String p3, java.lang.String p4, com.android.internal.inputmethod.IConnectionlessHandwritingCallback p5) throws android.os.RemoteException;
    void startImeTrace() throws android.os.RemoteException;
    com.android.internal.inputmethod.InputBindResult startInputOrWindowGainedFocus(int p0, com.android.internal.inputmethod.IInputMethodClient p1, android.os.IBinder p2, int p3, int p4, int p5, android.view.inputmethod.EditorInfo p6, com.android.internal.inputmethod.IRemoteInputConnection p7, com.android.internal.inputmethod.IRemoteAccessibilityInputConnection p8, int p9, int p10, android.window.ImeOnBackInvokedDispatcher p11) throws android.os.RemoteException;
    void startInputOrWindowGainedFocusAsync(int p0, com.android.internal.inputmethod.IInputMethodClient p1, android.os.IBinder p2, int p3, int p4, int p5, android.view.inputmethod.EditorInfo p6, com.android.internal.inputmethod.IRemoteInputConnection p7, com.android.internal.inputmethod.IRemoteAccessibilityInputConnection p8, int p9, int p10, android.window.ImeOnBackInvokedDispatcher p11, int p12, boolean p13) throws android.os.RemoteException;
    void startProtoDump(byte[] p0, int p1, java.lang.String p2) throws android.os.RemoteException;
    void startStylusHandwriting(com.android.internal.inputmethod.IInputMethodClient p0) throws android.os.RemoteException;
    void stopImeTrace() throws android.os.RemoteException;

    // --- AIDL-generated Stub abstract class -------------------------------
    public static abstract class Stub extends android.os.Binder implements IInputMethodManager {
        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }
        // CR17: Android 16 alternate constructor that accepts a
        // PermissionEnforcer.  The real framework.jar Stub uses this to
        // bypass the ActivityThread-dependent default ctor (which NPEs
        // in the Westlake sandbox).  WestlakeInputMethodManagerService
        // invokes super(NoopPermissionEnforcer) via this overload.
        public Stub(android.os.PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
        }
        public static IInputMethodManager asInterface(IBinder obj) {
            if (obj == null) return null;
            IInterface i = obj.queryLocalInterface(DESCRIPTOR);
            if (i instanceof IInputMethodManager) return (IInputMethodManager) i;
            return null;
        }
        @Override public IBinder asBinder() { return this; }
    }
}
