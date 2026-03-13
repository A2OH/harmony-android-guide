package android.telecom;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible TelecomManager shim. Stub implementation for mock testing.
 * Provides access to Telecom system services.
 */
public class TelecomManager {

    /** Action to launch the change-default-dialer flow. */
    public static final String ACTION_CHANGE_DEFAULT_DIALER =
            "android.telecom.action.CHANGE_DEFAULT_DIALER";

    /** Extra key carrying the package name of the proposed default dialer. */
    public static final String EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME =
            "android.telecom.extra.CHANGE_DEFAULT_DIALER_PACKAGE_NAME";

    private static TelecomManager sInstance;

    private String mDefaultDialerPackage = "";
    private boolean mInCall = false;
    private final List<PhoneAccountHandle> mCallCapableAccounts = new ArrayList<>();

    private TelecomManager() {}

    /**
     * Returns a singleton TelecomManager instance (shim convenience factory).
     * Real Android obtains this via Context.getSystemService().
     */
    public static TelecomManager from(Object context) {
        if (sInstance == null) {
            sInstance = new TelecomManager();
        }
        return sInstance;
    }

    // -------------------------------------------------------------------------
    // Default dialer
    // -------------------------------------------------------------------------

    /** Returns the package name of the current default dialer application. */
    public String getDefaultDialerPackage() {
        return mDefaultDialerPackage;
    }

    /** Sets the default dialer package (for shim simulation). */
    public void setDefaultDialerPackage(String packageName) {
        mDefaultDialerPackage = packageName;
    }

    // -------------------------------------------------------------------------
    // Call state
    // -------------------------------------------------------------------------

    /** Returns true if there is an ongoing phone call (foreground or background). */
    public boolean isInCall() {
        return mInCall;
    }

    /** Sets the in-call state (for shim simulation). */
    public void setInCall(boolean inCall) {
        mInCall = inCall;
    }

    /** Ends an ongoing call. Returns true if the operation was accepted. */
    public boolean endCall() {
        System.out.println("[Telecom] TelecomManager.endCall");
        mInCall = false;
        return true;
    }

    /**
     * Places an outgoing call to the specified URI handle with optional extras.
     * @param address the address to call (android.net.Uri, passed as Object in shim)
     * @param extras  optional bundle of extras (android.os.Bundle, passed as Object)
     */
    public void placeCall(Object address, Object extras) {
        System.out.println("[Telecom] TelecomManager.placeCall address=" + address);
        mInCall = true;
    }

    // -------------------------------------------------------------------------
    // Phone accounts
    // -------------------------------------------------------------------------

    /** Returns the list of PhoneAccountHandles that can make/receive calls. */
    public List<PhoneAccountHandle> getCallCapablePhoneAccounts() {
        return new ArrayList<>(mCallCapableAccounts);
    }

    /**
     * Returns the PhoneAccount for the given handle, or null if not found.
     * In the shim this always returns null; tests can subclass to override.
     */
    public PhoneAccount getPhoneAccount(PhoneAccountHandle account) {
        return null;
    }

    /** Registers a call-capable account handle (for shim simulation). */
    public void registerCallCapableAccount(PhoneAccountHandle handle) {
        if (!mCallCapableAccounts.contains(handle)) {
            mCallCapableAccounts.add(handle);
        }
    }

    @Override
    public String toString() {
        return "TelecomManager{defaultDialer=" + mDefaultDialerPackage
                + ", inCall=" + mInCall + "}";
    }
}
