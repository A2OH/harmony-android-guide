package android.nfc.cardemulation;
import android.app.Activity;
import android.content.ComponentName;
import android.nfc.NfcAdapter;
import java.util.List;

public final class CardEmulation {
    public static final int ACTION_CHANGE_DEFAULT = 0;
    public static final int CATEGORY_OTHER = 0;
    public static final int CATEGORY_PAYMENT = 0;
    public static final int EXTRA_CATEGORY = 0;
    public static final int EXTRA_SERVICE_COMPONENT = 0;
    public static final int SELECTION_MODE_ALWAYS_ASK = 0;
    public static final int SELECTION_MODE_ASK_IF_CONFLICT = 0;
    public static final int SELECTION_MODE_PREFER_DEFAULT = 0;

    public CardEmulation() {}

    public boolean categoryAllowsForegroundPreference(String p0) { return false; }
    public List<?> getAidsForService(ComponentName p0, String p1) { return null; }
    public static CardEmulation getInstance(NfcAdapter p0) { return null; }
    public int getSelectionModeForCategory(String p0) { return 0; }
    public boolean isDefaultServiceForAid(ComponentName p0, String p1) { return false; }
    public boolean isDefaultServiceForCategory(ComponentName p0, String p1) { return false; }
    public boolean registerAidsForService(ComponentName p0, String p1, java.util.List<Object> p2) { return false; }
    public boolean removeAidsForService(ComponentName p0, String p1) { return false; }
    public boolean setPreferredService(Activity p0, ComponentName p1) { return false; }
    public boolean supportsAidPrefixRegistration() { return false; }
    public boolean unsetPreferredService(Activity p0) { return false; }
}
