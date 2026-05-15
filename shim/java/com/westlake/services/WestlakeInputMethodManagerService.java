// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4e -- WestlakeInputMethodManagerService
//
// Minimum-surface implementation of
// com.android.internal.view.IInputMethodManager.Stub for the Westlake
// dalvikvm sandbox.  ~4 Tier-1 real impls + ~33 fail-loud
// unobserved-method overrides (per codex CR2 / ServiceMethodMissing.fail
// pattern).
//
// Note: IInputMethodManager lives at `com.android.internal.view`, NOT
// at `android.view.inputmethod` -- the brief calls this out explicitly.
// The runtime AIDL is under com.android.internal.view, and that is the
// descriptor framework.jar's InputMethodManager looks up.
//
// Same-process Stub.asInterface elision:
//   When framework code does
//     IInputMethodManager imm = IInputMethodManager.Stub.asInterface(
//         ServiceManager.getService("input_method"));
//   the Stub looks up queryLocalInterface(IInputMethodManager.DESCRIPTOR)
//   on the IBinder, which returns THIS instance.
//
// Compile-time vs runtime hierarchy:
//   Compile-time: extends shim's com.android.internal.view.IInputMethodManager$Stub
//                 (abstract; 37 abstract methods declared in
//                  shim/java/com/android/internal/view/IInputMethodManager.java).
//   Runtime:      extends framework.jar's
//                 com.android.internal.view.IInputMethodManager$Stub.  Shim
//                 Stub is stripped from aosp-shim.dex via
//                 scripts/framework_duplicates.txt so the real Stub wins.
//
// Rationale (see docs/engine/M4_DISCOVERY.md sec 7):
//   Framework's InputMethodManager.getInstance() / .getEnabledInputMethodList()
//   are called whenever the app inflates an EditText, focus a TextView, or
//   touches IME bindings.  Without an IInputMethodManager registered under
//   "input_method", ServiceManager.getService returns null and IMM calls
//   NPE.  M4e registers a minimal IInputMethodManager that reports "no IMEs
//   installed and no current IME".  This is correct for the sandbox -- we
//   have no real input method services.  Apps that try to actually OPEN
//   an IME will (correctly) fail; pure focus / inflation flows survive.
//
// Method count: 37 IInputMethodManager methods.  Tier-1 (real):
//   getInputMethodList, getEnabledInputMethodList,
//   getCurrentInputMethodInfoAsUser, addClient.  (Note: Android 16
//   IInputMethodManager has no removeClient method; the brief's
//   removeClient Tier-1 entry does not exist in this AIDL version --
//   it's been removed since Android 13.)  Remaining 33 fail loud.
//
// CR17 (2026-05-12): per codex review #2 HIGH finding -- the previously
// used `super()` ctor expands to
//     PermissionEnforcer.fromContext(ActivityThread.currentActivityThread()
//                                         .getSystemContext())
// which NPEs in the Westlake sandbox (ActivityThread is null unless
// CharsetPrimer.primeActivityThread() runs first).  Production-path
// callers via ServiceRegistrar.registerAllServices() don't run the test
// harness primer, so the default ctor NPE'd there.  Fixed by adopting
// the same PermissionEnforcer-bypass pattern used by M4a/M4b/M4c/M4-power
// (subclassed PermissionEnforcer whose protected no-arg ctor sets
// mContext=null and returns).  No system services are touched.

package com.westlake.services;

import android.os.PermissionEnforcer;

import com.android.internal.view.IInputMethodManager;
import com.android.internal.inputmethod.IInputMethodClient;
import com.android.internal.inputmethod.IRemoteInputConnection;
import com.android.internal.inputmethod.InputMethodInfoSafeList;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class WestlakeInputMethodManagerService extends IInputMethodManager.Stub {

    /** Tracks registered IInputMethodClients so any future removeClient
     *  path (or a sandbox-side cleanup probe) finds them.  We never
     *  dispatch IME events back into the client. */
    private final Set<IInputMethodClient> mClients =
            Collections.synchronizedSet(new HashSet<IInputMethodClient>());

    // PermissionEnforcer subclass nested here so users don't need to
    // import it; protected constructor of PermissionEnforcer is
    // accessible to subclasses regardless of package.
    private static final class NoopPermissionEnforcer extends PermissionEnforcer {
        NoopPermissionEnforcer() { super(); }
    }

    public WestlakeInputMethodManagerService() {
        // Bypass the deprecated no-arg constructor that NPEs in the
        // sandbox (ActivityThread.getSystemContext() returns null); use
        // the Stub(PermissionEnforcer) overload with a no-op enforcer.
        // Base Stub still calls attachInterface(this, DESCRIPTOR), so
        // queryLocalInterface("com.android.internal.view.IInputMethodManager") returns this.
        super(new NoopPermissionEnforcer());
    }

    // ------------------------------------------------------------------
    //   IMPLEMENTED (Tier-1) METHODS
    // ------------------------------------------------------------------

    /** Return an empty IMM list -- no input methods installed in the
     *  sandbox.  Android 16 signature: getInputMethodList(int userId,
     *  int directBootAwareness) returning InputMethodInfoSafeList.
     *  We use the framework.jar `empty()` factory reflectively so the
     *  returned object is the real framework type (not the shim stub),
     *  which is critical because framework code reads internal fields
     *  on the returned object. */
    @Override
    public InputMethodInfoSafeList getInputMethodList(int p0, int p1)
            throws android.os.RemoteException {
        return emptySafeList();
    }

    /** Same body: empty list of enabled IMEs. */
    @Override
    public InputMethodInfoSafeList getEnabledInputMethodList(int p0)
            throws android.os.RemoteException {
        return emptySafeList();
    }

    /** No current IME in the sandbox -- return null is the AOSP
     *  null-safe convention. */
    @Override
    public android.view.inputmethod.InputMethodInfo getCurrentInputMethodInfoAsUser(int p0)
            throws android.os.RemoteException {
        return null;
    }

    /** Track the client; framework code does not require any return
     *  data from addClient.  We never call any IInputMethodClient method
     *  back (no IME means no startInput notification arrives). */
    @Override
    public void addClient(IInputMethodClient p0, IRemoteInputConnection p1, int p2)
            throws android.os.RemoteException {
        if (p0 != null) mClients.add(p0);
    }

    /** Reflectively look up the real framework.jar InputMethodInfoSafeList.empty()
     *  factory so callers downstream of this method get the real type,
     *  not our shim type.  If the real method is unreachable at runtime
     *  (e.g. framework.jar shape changed), fall back to the shim
     *  instance -- that will at least keep getInputMethodList from
     *  returning null. */
    private static InputMethodInfoSafeList emptySafeList() {
        try {
            Class<?> cls = Class.forName(
                    "com.android.internal.inputmethod.InputMethodInfoSafeList");
            java.lang.reflect.Method m = cls.getDeclaredMethod("empty");
            m.setAccessible(true);
            Object inst = m.invoke(null);
            if (inst instanceof InputMethodInfoSafeList) {
                return (InputMethodInfoSafeList) inst;
            }
            // The real InputMethodInfoSafeList may not assignment-compatible
            // with our compile-time stub (different classloaders) but the
            // caller framework code only cares about the descriptor; cast
            // via Object reference is enough.
        } catch (Throwable ignored) {
            // Fall through to shim instance.
        }
        return new InputMethodInfoSafeList();
    }

    // ------------------------------------------------------------------
    //   FAIL-LOUD UNOBSERVED METHODS (CR2 / codex Tier 2 #2)
    //
    //   How to promote a method to Tier-1: delete the throw body here and
    //   add a real implementation in the IMPLEMENTED block above.  Update
    //   InputMethodServiceTest to exercise the new path.
    // ------------------------------------------------------------------

    @Override public boolean acceptStylusHandwritingDelegation(com.android.internal.inputmethod.IInputMethodClient p0, int p1, java.lang.String p2, java.lang.String p3, int p4) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "acceptStylusHandwritingDelegation"); }
    @Override public void acceptStylusHandwritingDelegationAsync(com.android.internal.inputmethod.IInputMethodClient p0, int p1, java.lang.String p2, java.lang.String p3, int p4, com.android.internal.inputmethod.IBooleanListener p5) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "acceptStylusHandwritingDelegationAsync"); }
    @Override public void addVirtualStylusIdForTestSession(com.android.internal.inputmethod.IInputMethodClient p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "addVirtualStylusIdForTestSession"); }
    @Override public android.view.inputmethod.InputMethodSubtype getCurrentInputMethodSubtype(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "getCurrentInputMethodSubtype"); }
    @Override public java.util.List getEnabledInputMethodListLegacy(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "getEnabledInputMethodListLegacy"); }
    @Override public java.util.List getEnabledInputMethodSubtypeList(java.lang.String p0, boolean p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "getEnabledInputMethodSubtypeList"); }
    @Override public com.android.internal.inputmethod.IImeTracker getImeTrackerService() throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "getImeTrackerService"); }
    @Override public java.util.List getInputMethodListLegacy(int p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "getInputMethodListLegacy"); }
    @Override public int getInputMethodWindowVisibleHeight(com.android.internal.inputmethod.IInputMethodClient p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "getInputMethodWindowVisibleHeight"); }
    @Override public android.view.inputmethod.InputMethodSubtype getLastInputMethodSubtype(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "getLastInputMethodSubtype"); }
    @Override public boolean hideSoftInput(com.android.internal.inputmethod.IInputMethodClient p0, android.os.IBinder p1, android.view.inputmethod.ImeTracker.Token p2, int p3, android.os.ResultReceiver p4, int p5, boolean p6) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "hideSoftInput"); }
    @Override public void hideSoftInputFromServerForTest() throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "hideSoftInputFromServerForTest"); }
    @Override public boolean isImeTraceEnabled() throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "isImeTraceEnabled"); }
    @Override public boolean isInputMethodPickerShownForTest() throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "isInputMethodPickerShownForTest"); }
    @Override public boolean isStylusHandwritingAvailableAsUser(int p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "isStylusHandwritingAvailableAsUser"); }
    @Override public void onImeSwitchButtonClickFromSystem(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "onImeSwitchButtonClickFromSystem"); }
    @Override public void prepareStylusHandwritingDelegation(com.android.internal.inputmethod.IInputMethodClient p0, int p1, java.lang.String p2, java.lang.String p3) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "prepareStylusHandwritingDelegation"); }
    @Override public void removeImeSurface(int p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "removeImeSurface"); }
    @Override public void removeImeSurfaceFromWindowAsync(android.os.IBinder p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "removeImeSurfaceFromWindowAsync"); }
    @Override public void reportPerceptibleAsync(android.os.IBinder p0, boolean p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "reportPerceptibleAsync"); }
    @Override public void setAdditionalInputMethodSubtypes(java.lang.String p0, android.view.inputmethod.InputMethodSubtype[] p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "setAdditionalInputMethodSubtypes"); }
    @Override public void setExplicitlyEnabledInputMethodSubtypes(java.lang.String p0, int[] p1, int p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "setExplicitlyEnabledInputMethodSubtypes"); }
    @Override public void setStylusWindowIdleTimeoutForTest(com.android.internal.inputmethod.IInputMethodClient p0, long p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "setStylusWindowIdleTimeoutForTest"); }
    @Override public void showInputMethodPickerFromClient(com.android.internal.inputmethod.IInputMethodClient p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "showInputMethodPickerFromClient"); }
    @Override public void showInputMethodPickerFromSystem(int p0, int p1) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "showInputMethodPickerFromSystem"); }
    @Override public boolean showSoftInput(com.android.internal.inputmethod.IInputMethodClient p0, android.os.IBinder p1, android.view.inputmethod.ImeTracker.Token p2, int p3, int p4, android.os.ResultReceiver p5, int p6, boolean p7) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "showSoftInput"); }
    @Override public void startConnectionlessStylusHandwriting(com.android.internal.inputmethod.IInputMethodClient p0, int p1, android.view.inputmethod.CursorAnchorInfo p2, java.lang.String p3, java.lang.String p4, com.android.internal.inputmethod.IConnectionlessHandwritingCallback p5) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "startConnectionlessStylusHandwriting"); }
    @Override public void startImeTrace() throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "startImeTrace"); }
    @Override public com.android.internal.inputmethod.InputBindResult startInputOrWindowGainedFocus(int p0, com.android.internal.inputmethod.IInputMethodClient p1, android.os.IBinder p2, int p3, int p4, int p5, android.view.inputmethod.EditorInfo p6, com.android.internal.inputmethod.IRemoteInputConnection p7, com.android.internal.inputmethod.IRemoteAccessibilityInputConnection p8, int p9, int p10, android.window.ImeOnBackInvokedDispatcher p11) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "startInputOrWindowGainedFocus"); }
    @Override public void startInputOrWindowGainedFocusAsync(int p0, com.android.internal.inputmethod.IInputMethodClient p1, android.os.IBinder p2, int p3, int p4, int p5, android.view.inputmethod.EditorInfo p6, com.android.internal.inputmethod.IRemoteInputConnection p7, com.android.internal.inputmethod.IRemoteAccessibilityInputConnection p8, int p9, int p10, android.window.ImeOnBackInvokedDispatcher p11, int p12, boolean p13) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "startInputOrWindowGainedFocusAsync"); }
    @Override public void startProtoDump(byte[] p0, int p1, java.lang.String p2) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "startProtoDump"); }
    @Override public void startStylusHandwriting(com.android.internal.inputmethod.IInputMethodClient p0) throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "startStylusHandwriting"); }
    @Override public void stopImeTrace() throws android.os.RemoteException { throw ServiceMethodMissing.fail("input_method", "stopImeTrace"); }

    // ------------------------------------------------------------------
    //   Diagnostic helpers (not part of IInputMethodManager surface)
    // ------------------------------------------------------------------

    /** Returns the count of currently-registered IM clients (for tests). */
    public int registeredClientCount() {
        return mClients.size();
    }

    @Override
    public String toString() {
        return "WestlakeInputMethodManagerService{clients=" + mClients.size() + "}";
    }
}
