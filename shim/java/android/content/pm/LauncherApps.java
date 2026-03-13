package android.content.pm;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import java.util.List;
import java.util.concurrent.Executor;

public class LauncherApps {
    public static final int ACTION_CONFIRM_PIN_APPWIDGET = 0;
    public static final int ACTION_CONFIRM_PIN_SHORTCUT = 0;
    public static final int EXTRA_PIN_ITEM_REQUEST = 0;


    public List<?> getActivityList(String p0, UserHandle p1) { return null; }
    public ApplicationInfo getApplicationInfo(String p0, int p1, UserHandle p2) { return null; }
    public Object getPinItemRequest(Intent p0) { return null; }
    public List<?> getProfiles() { return null; }
    public Drawable getShortcutBadgedIconDrawable(ShortcutInfo p0, int p1) { return null; }
    public List<?> getShortcutConfigActivityList(String p0, UserHandle p1) { return null; }
    public Drawable getShortcutIconDrawable(ShortcutInfo p0, int p1) { return null; }
    public boolean hasShortcutHostPermission() { return false; }
    public boolean isActivityEnabled(ComponentName p0, UserHandle p1) { return false; }
    public boolean isPackageEnabled(String p0, UserHandle p1) { return false; }
    public void pinShortcuts(String p0, java.util.List<Object> p1, UserHandle p2) {}
    public void registerCallback(Object p0) {}
    public void registerCallback(Object p0, Handler p1) {}
    public void registerPackageInstallerSessionCallback(Executor p0, Object p1) {}
    public LauncherActivityInfo resolveActivity(Intent p0, UserHandle p1) { return null; }
    public boolean shouldHideFromSuggestions(String p0, UserHandle p1) { return false; }
    public void startAppDetailsActivity(ComponentName p0, UserHandle p1, Rect p2, Bundle p3) {}
    public void startMainActivity(ComponentName p0, UserHandle p1, Rect p2, Bundle p3) {}
    public void startPackageInstallerSessionDetailsActivity(Object p0, Rect p1, Bundle p2) {}
    public void startShortcut(String p0, String p1, Rect p2, Bundle p3, UserHandle p4) {}
    public void startShortcut(ShortcutInfo p0, Rect p1, Bundle p2) {}
    public void unregisterCallback(Object p0) {}
    public void unregisterPackageInstallerSessionCallback(Object p0) {}
    public void onPackageAdded(String p0, UserHandle p1) {}
    public void onPackageChanged(String p0, UserHandle p1) {}
    public void onPackageRemoved(String p0, UserHandle p1) {}
    public void onPackagesAvailable(String[] p0, UserHandle p1, boolean p2) {}
    public void onPackagesSuspended(String[] p0, UserHandle p1) {}
    public void onPackagesUnavailable(String[] p0, UserHandle p1, boolean p2) {}
    public void onPackagesUnsuspended(String[] p0, UserHandle p1) {}
    public void onShortcutsChanged(String p0, java.util.List<Object> p1, UserHandle p2) {}
}
