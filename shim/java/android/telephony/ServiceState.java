package android.telephony;

/**
 * Android-compatible ServiceState shim. Stub returning out-of-service defaults.
 */
public class ServiceState {

    /** Normal operation - phone is registered with an operator. */
    public static final int STATE_IN_SERVICE = 0;
    /** Phone is not registered with any operator. */
    public static final int STATE_OUT_OF_SERVICE = 1;
    /** Phone can only make emergency calls. */
    public static final int STATE_EMERGENCY_ONLY = 2;
    /** Radio is explicitly powered off. */
    public static final int STATE_POWER_OFF = 3;

    private int mState = STATE_OUT_OF_SERVICE;
    private boolean mRoaming = false;
    private String mOperatorAlphaLong = null;
    private String mOperatorAlphaShort = null;
    private String mOperatorNumeric = null;
    private boolean mIsManualSelection = false;

    public ServiceState() {}

    public ServiceState(ServiceState s) {
        copyFrom(s);
    }

    private void copyFrom(ServiceState s) {
        if (s != null) {
            mState = s.mState;
            mRoaming = s.mRoaming;
            mOperatorAlphaLong = s.mOperatorAlphaLong;
            mOperatorAlphaShort = s.mOperatorAlphaShort;
            mOperatorNumeric = s.mOperatorNumeric;
            mIsManualSelection = s.mIsManualSelection;
        }
    }

    /** Returns the current service state. */
    public int getState() {
        return mState;
    }

    /** Returns whether the device is currently roaming. */
    public boolean getRoaming() {
        return mRoaming;
    }

    /** Returns the long operator name, or null if unavailable. */
    public String getOperatorAlphaLong() {
        return mOperatorAlphaLong;
    }

    /** Returns the short operator name, or null if unavailable. */
    public String getOperatorAlphaShort() {
        return mOperatorAlphaShort;
    }

    /** Returns the numeric operator code (MCC+MNC), or null if unavailable. */
    public String getOperatorNumeric() {
        return mOperatorNumeric;
    }

    /** Returns true if the user has manually selected a network. */
    public boolean getIsManualSelection() {
        return mIsManualSelection;
    }

    /** Sets the current service state. */
    public void setState(int state) {
        mState = state;
    }

    /** Sets the roaming indicator. */
    public void setRoaming(boolean roaming) {
        mRoaming = roaming;
    }

    /** Sets operator name and numeric code. */
    public void setOperatorName(String longName, String shortName, String numeric) {
        mOperatorAlphaLong = longName;
        mOperatorAlphaShort = shortName;
        mOperatorNumeric = numeric;
    }

    /** Sets whether the network was manually selected. */
    public void setIsManualSelection(boolean isManual) {
        mIsManualSelection = isManual;
    }

    @Override
    public String toString() {
        String stateStr;
        switch (mState) {
            case STATE_IN_SERVICE:      stateStr = "IN_SERVICE"; break;
            case STATE_OUT_OF_SERVICE:  stateStr = "OUT_OF_SERVICE"; break;
            case STATE_EMERGENCY_ONLY:  stateStr = "EMERGENCY_ONLY"; break;
            case STATE_POWER_OFF:       stateStr = "POWER_OFF"; break;
            default:                    stateStr = "UNKNOWN(" + mState + ")"; break;
        }
        return "ServiceState{state=" + stateStr +
               " roaming=" + mRoaming +
               " operator=" + mOperatorAlphaLong +
               " numeric=" + mOperatorNumeric +
               " manual=" + mIsManualSelection + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceState)) return false;
        ServiceState s = (ServiceState) o;
        return mState == s.mState
                && mRoaming == s.mRoaming
                && mIsManualSelection == s.mIsManualSelection
                && equals(mOperatorAlphaLong, s.mOperatorAlphaLong)
                && equals(mOperatorAlphaShort, s.mOperatorAlphaShort)
                && equals(mOperatorNumeric, s.mOperatorNumeric);
    }

    private static boolean equals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    @Override
    public int hashCode() {
        int result = mState;
        result = 31 * result + (mRoaming ? 1 : 0);
        result = 31 * result + (mIsManualSelection ? 1 : 0);
        result = 31 * result + (mOperatorAlphaLong != null ? mOperatorAlphaLong.hashCode() : 0);
        result = 31 * result + (mOperatorAlphaShort != null ? mOperatorAlphaShort.hashCode() : 0);
        result = 31 * result + (mOperatorNumeric != null ? mOperatorNumeric.hashCode() : 0);
        return result;
    }
}
