package android.telephony;

/**
 * Android-compatible SubscriptionInfo shim. Stub implementation for mock testing.
 */
public class SubscriptionInfo {

    private int mSubscriptionId = -1;
    private String mIccId = "";
    private int mSimSlotIndex = -1;
    private CharSequence mDisplayName = "";
    private CharSequence mCarrierName = "";
    private String mNumber = "";
    private String mCountryIso = "";
    private int mMcc = 0;
    private int mMnc = 0;
    private boolean mIsEmbedded = false;

    public SubscriptionInfo() {}

    public int getSubscriptionId() {
        return mSubscriptionId;
    }

    public String getIccId() {
        return mIccId;
    }

    public int getSimSlotIndex() {
        return mSimSlotIndex;
    }

    public CharSequence getDisplayName() {
        return mDisplayName;
    }

    public CharSequence getCarrierName() {
        return mCarrierName;
    }

    public String getNumber() {
        return mNumber;
    }

    public String getCountryIso() {
        return mCountryIso;
    }

    public int getMcc() {
        return mMcc;
    }

    public int getMnc() {
        return mMnc;
    }

    public boolean isEmbedded() {
        return mIsEmbedded;
    }
}
