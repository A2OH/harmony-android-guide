package android.nfc.cardemulation;

import android.content.ComponentName;
import android.content.Context;
import android.nfc.NfcAdapter;
import java.util.List;

/**
 * Android-compatible CardEmulation stub.
 * Provides an interface to the card emulation features of NFC.
 */
public final class CardEmulation {
    public static final String CATEGORY_PAYMENT = "payment";
    public static final String CATEGORY_OTHER   = "other";
    public static final String ACTION_CHANGE_DEFAULT =
            "android.nfc.cardemulation.action.ACTION_CHANGE_DEFAULT";

    private static final CardEmulation sInstance = new CardEmulation();

    private CardEmulation() {}

    /**
     * Returns the CardEmulation instance for the given NfcAdapter.
     * Context and adapter are ignored in the shim.
     */
    public static CardEmulation getInstance(NfcAdapter adapter) {
        return sInstance;
    }

    /**
     * Returns true if the given service is the default handler for the given category.
     */
    public boolean isDefaultServiceForCategory(ComponentName service, String category) {
        return false;
    }

    /**
     * Returns true if the given service is the default handler for the given AID.
     */
    public boolean isDefaultServiceForAid(ComponentName service, String aid) {
        return false;
    }

    /**
     * Returns true if the given category allows the user to configure a foreground preferred service.
     */
    public boolean categoryAllowsForegroundPreference(String category) {
        return false;
    }

    /**
     * Sets the default service for the given category.
     */
    public boolean setDefaultServiceForCategory(ComponentName service, String category) {
        return false;
    }

    /**
     * Sets the default service for the next tap.
     */
    public boolean setDefaultForNextTap(ComponentName service) {
        return false;
    }

    /**
     * Registers a list of AIDs for the given service.
     */
    public boolean registerAidsForService(ComponentName service, String category, List<String> aids) {
        return false;
    }

    /**
     * Removes AIDs registered for the given service and category.
     */
    public boolean removeAidsForService(ComponentName service, String category) {
        return false;
    }
}
