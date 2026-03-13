package android.content.pm;
import android.content.ComponentName;
import android.os.UserHandle;

public class CrossProfileApps {
    public static final int ACTION_CAN_INTERACT_ACROSS_PROFILES_CHANGED = 0;

    public CrossProfileApps() {}

    public boolean canInteractAcrossProfiles() { return false; }
    public boolean canRequestInteractAcrossProfiles() { return false; }
    public void startMainActivity(ComponentName p0, UserHandle p1) {}
}
