package android.companion;
import android.content.ComponentName;
import android.content.IntentSender;
import android.os.Handler;

public final class CompanionDeviceManager {
    public static final int EXTRA_DEVICE = 0;


    public void associate(AssociationRequest p0, Object p1, Handler p2) {}
    public void disassociate(String p0) {}
    public boolean hasNotificationAccess(ComponentName p0) { return false; }
    public void requestNotificationAccess(ComponentName p0) {}
    public void onDeviceFound(IntentSender p0) {}
    public void onFailure(CharSequence p0) {}
}
