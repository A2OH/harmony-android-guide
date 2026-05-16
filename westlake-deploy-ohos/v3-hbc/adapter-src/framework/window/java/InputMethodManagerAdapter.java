/*
 * InputMethodManagerAdapter.java
 *
 * Adapter implementation of IInputMethodManager for the OH environment.
 *
 * Why this exists:
 *   AOSP InputMethodManager.createRealInstance() calls
 *     ServiceManager.getService("input_method")
 *   and throws IllegalStateException("IInputMethodManager is not available")
 *   if it returns null.  Editor (TextView.append → setText path) calls
 *   InputMethodManager.forContext() during view inflation, so without a
 *   non-null binder ANY app that inflates an editable / focusable text widget
 *   (which means basically every app) crashes before reaching onResume.
 *
 *   OH has no Android-IME equivalent yet — there is no "IME picker" or
 *   per-language IME plug-in subsystem.  This adapter therefore returns
 *   "no IME present" sentinels for every method, mirroring AOSP's own
 *   layoutlib stub strategy (createStubInstance):
 *     - boolean methods → false
 *     - List<...>       → empty list
 *     - InputBindResult → InputBindResult.NO_IME
 *     - everything else → null / no-op
 *
 *   This is sufficient for ALL Android apps that don't actively rely on a
 *   third-party IME (the vast majority).  Apps that do require IME will
 *   gracefully see "no IME available" and continue running — they won't get
 *   a popup keyboard, but they also won't crash.  When OH ships its own IME
 *   subsystem we can map showSoftInput/hideSoftInput to it without changing
 *   any app code.
 *
 * Authoritative spec: doc/window_manager_ipc_adapter_design.html §5
 *   (added 2026-04-30 alongside this file).
 */
package adapter.window;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ImeTracker;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import android.window.ImeOnBackInvokedDispatcher;

import com.android.internal.inputmethod.IImeTracker;
import com.android.internal.inputmethod.IInputMethodClient;
import com.android.internal.inputmethod.IRemoteAccessibilityInputConnection;
import com.android.internal.inputmethod.IRemoteInputConnection;
import com.android.internal.inputmethod.InputBindResult;
import com.android.internal.view.IInputMethodManager;

import java.util.Collections;
import java.util.List;

public final class InputMethodManagerAdapter extends IInputMethodManager.Stub {

    private static final String TAG = "OH_IMMAdapter";

    private static volatile InputMethodManagerAdapter sInstance;

    public static InputMethodManagerAdapter getInstance() {
        if (sInstance == null) {
            synchronized (InputMethodManagerAdapter.class) {
                if (sInstance == null) {
                    sInstance = new InputMethodManagerAdapter();
                }
            }
        }
        return sInstance;
    }

    public InputMethodManagerAdapter() {
        System.err.println("[" + TAG + "] instantiated (NO_IME baseline — "
                + "Editor/TextView startup paths satisfied without a real IME)");
    }

    private static void traceCall(String name) {
        // Quiet by default; flip if needed for debug.
        // System.err.println("[" + TAG + "] " + name);
    }

    // ========================================================================
    // Client lifecycle
    // ========================================================================

    @Override
    public void addClient(IInputMethodClient client, IRemoteInputConnection inputmethod,
            int untrustedDisplayId) throws RemoteException {
        traceCall("addClient(displayId=" + untrustedDisplayId + ")");
        // No IMMS to register with.  IMM client retains the reference to its
        // local-side IInputMethodClient mClient; we just need this call not to
        // throw so the constructor path completes.
    }

    // ========================================================================
    // IME enumeration  (returns empty list / null — "no IME installed")
    // ========================================================================

    @Override
    public InputMethodInfo getCurrentInputMethodInfoAsUser(int userId) throws RemoteException {
        return null;
    }

    @Override
    public List<InputMethodInfo> getInputMethodList(int userId, int directBootAwareness)
            throws RemoteException {
        return Collections.emptyList();
    }

    @Override
    public List<InputMethodInfo> getEnabledInputMethodList(int userId) throws RemoteException {
        return Collections.emptyList();
    }

    @Override
    public List<InputMethodSubtype> getEnabledInputMethodSubtypeList(String imiId,
            boolean allowsImplicitlyEnabledSubtypes, int userId) throws RemoteException {
        return Collections.emptyList();
    }

    @Override
    public InputMethodSubtype getLastInputMethodSubtype(int userId) throws RemoteException {
        return null;
    }

    @Override
    public InputMethodSubtype getCurrentInputMethodSubtype(int userId) throws RemoteException {
        return null;
    }

    // ========================================================================
    // Soft-input show / hide  (no IME → returns false, never opens keyboard)
    // ========================================================================

    @Override
    public boolean showSoftInput(IInputMethodClient client, IBinder windowToken,
            ImeTracker.Token statsToken, int flags, int lastClickToolType,
            ResultReceiver resultReceiver, int reason) throws RemoteException {
        traceCall("showSoftInput");
        return false;
    }

    @Override
    public boolean hideSoftInput(IInputMethodClient client, IBinder windowToken,
            ImeTracker.Token statsToken, int flags,
            ResultReceiver resultReceiver, int reason) throws RemoteException {
        traceCall("hideSoftInput");
        return false;
    }

    // ========================================================================
    // Window/focus binding  (returns InputBindResult.NO_IME — fully tolerated
    // by AOSP InputMethodManager#startInputInner: id==null + result==ERROR_NO_IME
    // skips the IME session-bind path and the editor continues without a
    // keyboard).
    // ========================================================================

    @Override
    public InputBindResult startInputOrWindowGainedFocus(
            int startInputReason, IInputMethodClient client, IBinder windowToken,
            int startInputFlags, int softInputMode, int windowFlags,
            EditorInfo editorInfo, IRemoteInputConnection inputConnection,
            IRemoteAccessibilityInputConnection remoteAccessibilityInputConnection,
            int unverifiedTargetSdkVersion, int userId,
            ImeOnBackInvokedDispatcher imeDispatcher) throws RemoteException {
        traceCall("startInputOrWindowGainedFocus(reason=" + startInputReason + ")");
        return InputBindResult.NO_IME;
    }

    // ========================================================================
    // IME picker / subtype management  (no-op)
    // ========================================================================

    @Override
    public void showInputMethodPickerFromClient(IInputMethodClient client,
            int auxiliarySubtypeMode) throws RemoteException {
    }

    @Override
    public void showInputMethodPickerFromSystem(int auxiliarySubtypeMode, int displayId)
            throws RemoteException {
    }

    @Override
    public boolean isInputMethodPickerShownForTest() throws RemoteException {
        return false;
    }

    @Override
    public void setAdditionalInputMethodSubtypes(String id, InputMethodSubtype[] subtypes,
            int userId) throws RemoteException {
    }

    @Override
    public void setExplicitlyEnabledInputMethodSubtypes(String imeId, int[] subtypeHashCodes,
            int userId) throws RemoteException {
    }

    // ========================================================================
    // Display geometry / surface  (no-op; no IME window exists)
    // ========================================================================

    @Override
    public int getInputMethodWindowVisibleHeight(IInputMethodClient client) throws RemoteException {
        return 0;
    }

    @Override
    public void reportVirtualDisplayGeometryAsync(IInputMethodClient parentClient,
            int childDisplayId, float[] matrixValues) throws RemoteException {
    }

    @Override
    public void reportPerceptibleAsync(IBinder windowToken, boolean perceptible)
            throws RemoteException {
    }

    @Override
    public void removeImeSurface() throws RemoteException {
    }

    @Override
    public void removeImeSurfaceFromWindowAsync(IBinder windowToken) throws RemoteException {
    }

    // ========================================================================
    // Tracing  (disabled in adapter — IMM client tolerates false everywhere)
    // ========================================================================

    @Override
    public void startProtoDump(byte[] protoDump, int source, String where) throws RemoteException {
    }

    @Override
    public boolean isImeTraceEnabled() throws RemoteException {
        return false;
    }

    @Override
    public void startImeTrace() throws RemoteException {
    }

    @Override
    public void stopImeTrace() throws RemoteException {
    }

    // ========================================================================
    // Stylus handwriting  (not supported in adapter — false / no-op)
    // ========================================================================

    @Override
    public void startStylusHandwriting(IInputMethodClient client) throws RemoteException {
    }

    @Override
    public void prepareStylusHandwritingDelegation(IInputMethodClient client, int userId,
            String delegatePackageName, String delegatorPackageName) throws RemoteException {
    }

    @Override
    public boolean acceptStylusHandwritingDelegation(IInputMethodClient client, int userId,
            String delegatePackageName, String delegatorPackageName) throws RemoteException {
        return false;
    }

    @Override
    public boolean isStylusHandwritingAvailableAsUser(int userId) throws RemoteException {
        return false;
    }

    @Override
    public void addVirtualStylusIdForTestSession(IInputMethodClient client) throws RemoteException {
    }

    @Override
    public void setStylusWindowIdleTimeoutForTest(IInputMethodClient client, long timeout)
            throws RemoteException {
    }

    // ========================================================================
    // ImeTracker service  (returns null — IMM client uses ImeTracker.forLogging
    // local helper which is independent of this binder).
    // ========================================================================

    @Override
    public IImeTracker getImeTrackerService() throws RemoteException {
        return null;
    }
}
