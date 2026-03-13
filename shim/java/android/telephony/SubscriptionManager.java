package android.telephony;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible SubscriptionManager shim. Stub implementation for mock testing.
 * Provides access to SIM subscription information.
 */
public class SubscriptionManager {

    /** Indicates an invalid subscription ID. */
    public static final int INVALID_SUBSCRIPTION_ID = -1;

    /** Default subscription ID used when no specific sub is required. */
    public static final int DEFAULT_SUBSCRIPTION_ID = Integer.MAX_VALUE;

    private static SubscriptionManager sInstance;

    private final List<SubscriptionInfo> mActiveSubscriptions = new ArrayList<>();
    private int mDefaultSubId = INVALID_SUBSCRIPTION_ID;
    private int mDefaultSmsSubId = INVALID_SUBSCRIPTION_ID;
    private int mDefaultVoiceSubId = INVALID_SUBSCRIPTION_ID;

    private SubscriptionManager() {}

    /**
     * Returns a singleton SubscriptionManager instance (shim convenience factory).
     * Real Android obtains this via Context.getSystemService().
     */
    public static SubscriptionManager from(Object context) {
        if (sInstance == null) {
            sInstance = new SubscriptionManager();
        }
        return sInstance;
    }

    // -------------------------------------------------------------------------
    // Subscription info
    // -------------------------------------------------------------------------

    /**
     * Returns the list of currently active SubscriptionInfo records, or an empty
     * list if none are registered.
     */
    public List<SubscriptionInfo> getActiveSubscriptionInfoList() {
        return new ArrayList<>(mActiveSubscriptions);
    }

    /** Returns the number of active subscriptions. */
    public int getActiveSubscriptionInfoCount() {
        return mActiveSubscriptions.size();
    }

    // -------------------------------------------------------------------------
    // Default subscription IDs
    // -------------------------------------------------------------------------

    /** Returns the default subscription ID for data/general use. */
    public static int getDefaultSubscriptionId() {
        if (sInstance != null && sInstance.mDefaultSubId != INVALID_SUBSCRIPTION_ID) {
            return sInstance.mDefaultSubId;
        }
        return INVALID_SUBSCRIPTION_ID;
    }

    /** Returns the default subscription ID for SMS. */
    public static int getDefaultSmsSubscriptionId() {
        if (sInstance != null && sInstance.mDefaultSmsSubId != INVALID_SUBSCRIPTION_ID) {
            return sInstance.mDefaultSmsSubId;
        }
        return INVALID_SUBSCRIPTION_ID;
    }

    /** Returns the default subscription ID for voice calls. */
    public static int getDefaultVoiceSubscriptionId() {
        if (sInstance != null && sInstance.mDefaultVoiceSubId != INVALID_SUBSCRIPTION_ID) {
            return sInstance.mDefaultVoiceSubId;
        }
        return INVALID_SUBSCRIPTION_ID;
    }

    // -------------------------------------------------------------------------
    // Shim-internal setters (for testing)
    // -------------------------------------------------------------------------

    /** Adds a SubscriptionInfo record (for shim simulation). */
    public void addSubscriptionInfo(SubscriptionInfo info) {
        mActiveSubscriptions.add(info);
    }

    /** Sets the default subscription ID (for shim simulation). */
    public void setDefaultSubscriptionId(int subId) {
        mDefaultSubId = subId;
    }

    /** Sets the default SMS subscription ID (for shim simulation). */
    public void setDefaultSmsSubscriptionId(int subId) {
        mDefaultSmsSubId = subId;
    }

    /** Sets the default voice subscription ID (for shim simulation). */
    public void setDefaultVoiceSubscriptionId(int subId) {
        mDefaultVoiceSubId = subId;
    }

    @Override
    public String toString() {
        return "SubscriptionManager{activeCount=" + mActiveSubscriptions.size() + "}";
    }

    // =========================================================================
    // SubscriptionInfo inner class
    // =========================================================================

    /**
     * Describes a single active SIM subscription.
     */
    public static class SubscriptionInfo {
        private final int mSubscriptionId;
        private final int mSimSlotIndex;
        private final CharSequence mDisplayName;
        private final String mIccId;
        private final String mNumber;

        public SubscriptionInfo(int subscriptionId, int simSlotIndex,
                                CharSequence displayName, String iccId, String number) {
            mSubscriptionId = subscriptionId;
            mSimSlotIndex = simSlotIndex;
            mDisplayName = displayName;
            mIccId = iccId;
            mNumber = number;
        }

        /** Returns the subscription ID. */
        public int getSubscriptionId() {
            return mSubscriptionId;
        }

        /** Returns the SIM slot index. */
        public int getSimSlotIndex() {
            return mSimSlotIndex;
        }

        /** Returns the user-visible display name for this subscription. */
        public CharSequence getDisplayName() {
            return mDisplayName;
        }

        /** Returns the ICC ID of the SIM card. */
        public String getIccId() {
            return mIccId;
        }

        /** Returns the phone number for this subscription, or empty string. */
        public String getNumber() {
            return mNumber != null ? mNumber : "";
        }

        @Override
        public String toString() {
            return "SubscriptionInfo{id=" + mSubscriptionId
                    + ", slot=" + mSimSlotIndex
                    + ", name=" + mDisplayName + "}";
        }
    }
}
