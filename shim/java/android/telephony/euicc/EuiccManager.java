package android.telephony.euicc;

/**
 * Android-compatible EuiccManager shim. Stub implementation for mock testing.
 */
public class EuiccManager {

    public static final String ACTION_MANAGE_EMBEDDED_SUBSCRIPTIONS =
            "android.telephony.euicc.action.MANAGE_EMBEDDED_SUBSCRIPTIONS";

    public boolean isEnabled() {
        return false;
    }

    public String getEid() {
        return null;
    }

    public void downloadSubscription(Object subscription, boolean switchAfterDownload,
            Object callbackIntent) {
        System.out.println("[EUICC] downloadSubscription switchAfterDownload=" + switchAfterDownload);
    }

    public void deleteSubscription(int cardId, String iccid, Object callbackIntent) {
        System.out.println("[EUICC] deleteSubscription cardId=" + cardId + " iccid=" + iccid);
    }

    public void switchToSubscription(int cardId, String iccid, Object callbackIntent) {
        System.out.println("[EUICC] switchToSubscription cardId=" + cardId + " iccid=" + iccid);
    }
}
