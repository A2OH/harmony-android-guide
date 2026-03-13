package android.telephony;
import java.util.List;
import java.util.concurrent.Executor;

public class SubscriptionManager {
    public static final int ACTION_DEFAULT_SMS_SUBSCRIPTION_CHANGED = 0;
    public static final int ACTION_DEFAULT_SUBSCRIPTION_CHANGED = 0;
    public static final int ACTION_MANAGE_SUBSCRIPTION_PLANS = 0;
    public static final int ACTION_REFRESH_SUBSCRIPTION_PLANS = 0;
    public static final int DATA_ROAMING_DISABLE = 0;
    public static final int DATA_ROAMING_ENABLE = 0;
    public static final int DEFAULT_SUBSCRIPTION_ID = 0;
    public static final int EXTRA_SLOT_INDEX = 0;
    public static final int EXTRA_SUBSCRIPTION_INDEX = 0;
    public static final int INVALID_SIM_SLOT_INDEX = 0;
    public static final int INVALID_SUBSCRIPTION_ID = 0;
    public static final int SUBSCRIPTION_TYPE_LOCAL_SIM = 0;
    public static final int SUBSCRIPTION_TYPE_REMOTE_SIM = 0;

    public SubscriptionManager() {}

    public void addOnOpportunisticSubscriptionsChangedListener(Executor p0, Object p1) {}
    public void addOnSubscriptionsChangedListener(Object p0) {}
    public void addOnSubscriptionsChangedListener(Executor p0, Object p1) {}
    public boolean canManageSubscription(SubscriptionInfo p0) { return false; }
    public List<?> getAccessibleSubscriptionInfoList() { return null; }
    public static int getActiveDataSubscriptionId() { return 0; }
    public int getActiveSubscriptionInfoCountMax() { return 0; }
    public static int getDefaultDataSubscriptionId() { return 0; }
    public static int getDefaultSmsSubscriptionId() { return 0; }
    public static int getDefaultSubscriptionId() { return 0; }
    public static int getDefaultVoiceSubscriptionId() { return 0; }
    public static int getSlotIndex(int p0) { return 0; }
    public boolean isNetworkRoaming(int p0) { return false; }
    public static boolean isUsableSubscriptionId(int p0) { return false; }
    public static boolean isValidSubscriptionId(int p0) { return false; }
    public void removeOnOpportunisticSubscriptionsChangedListener(Object p0) {}
    public void removeOnSubscriptionsChangedListener(Object p0) {}
    public void setSubscriptionOverrideCongested(int p0, boolean p1, long p2) {}
    public void setSubscriptionOverrideUnmetered(int p0, boolean p1, long p2) {}
    public void setSubscriptionPlans(int p0, java.util.List<Object> p1) {}
}
