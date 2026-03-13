package android.telephony;

import android.app.PendingIntent;
import java.util.ArrayList;

/**
 * Android-compatible SmsManager shim. Stub implementation for mock testing.
 */
public class SmsManager {
    private static final SmsManager sInstance = new SmsManager();
    private static final int SMS_MAX_LENGTH = 160;

    private int mSubscriptionId = -1;

    private SmsManager() {}

    public static SmsManager getDefault() {
        return sInstance;
    }

    public void sendTextMessage(String destAddr, String scAddr, String text,
                                PendingIntent sentIntent, PendingIntent deliveryIntent) {
        System.out.println("[SMS] sendTextMessage to=" + destAddr + " text=" +
            (text != null ? text.substring(0, Math.min(text.length(), 40)) : "null"));
    }

    public void sendMultipartTextMessage(String destAddr, String scAddr,
                                         ArrayList<String> parts,
                                         ArrayList<PendingIntent> sentIntents,
                                         ArrayList<PendingIntent> deliveryIntents) {
        System.out.println("[SMS] sendMultipartTextMessage to=" + destAddr +
            " parts=" + (parts != null ? parts.size() : 0));
    }

    public ArrayList<String> divideMessage(String text) {
        ArrayList<String> parts = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            parts.add("");
            return parts;
        }
        int offset = 0;
        while (offset < text.length()) {
            int end = Math.min(offset + SMS_MAX_LENGTH, text.length());
            parts.add(text.substring(offset, end));
            offset = end;
        }
        return parts;
    }

    public void sendDataMessage(String destAddr, String scAddr, short destPort,
                                byte[] data, PendingIntent sentIntent,
                                PendingIntent deliveryIntent) {
        System.out.println("[SMS] sendDataMessage to=" + destAddr +
            " port=" + destPort + " bytes=" + (data != null ? data.length : 0));
    }

    public int getSubscriptionId() {
        return mSubscriptionId;
    }

    public static SmsManager getSmsManagerForSubscriptionId(int subId) {
        SmsManager mgr = new SmsManager();
        mgr.mSubscriptionId = subId;
        return mgr;
    }
}
