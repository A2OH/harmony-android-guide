package android.app;

public final class UiAutomation {
    public UiAutomation() {}

    public static final int FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES = 0;
    public static final int ROTATION_FREEZE_0 = 0;
    public static final int ROTATION_FREEZE_180 = 0;
    public static final int ROTATION_FREEZE_270 = 0;
    public static final int ROTATION_FREEZE_90 = 0;
    public static final int ROTATION_FREEZE_CURRENT = 0;
    public static final int ROTATION_UNFREEZE = 0;
    public void adoptShellPermissionIdentity() {}
    public void adoptShellPermissionIdentity(Object p0) {}
    public void clearWindowAnimationFrameStats() {}
    public boolean clearWindowContentFrameStats(Object p0) { return false; }
    public void dropShellPermissionIdentity() {}
    public Object executeAndWaitForEvent(Object p0, Object p1, Object p2) { return null; }
    public Object executeShellCommand(Object p0) { return null; }
    public Object findFocus(Object p0) { return null; }
    public Object getRootInActiveWindow() { return null; }
    public Object getServiceInfo() { return null; }
    public Object getWindowAnimationFrameStats() { return null; }
    public Object getWindowContentFrameStats(Object p0) { return null; }
    public Object getWindows() { return null; }
    public void grantRuntimePermission(Object p0, Object p1) {}
    public void grantRuntimePermissionAsUser(Object p0, Object p1, Object p2) {}
    public boolean injectInputEvent(Object p0, Object p1) { return false; }
    public boolean performGlobalAction(Object p0) { return false; }
    public void revokeRuntimePermission(Object p0, Object p1) {}
    public void revokeRuntimePermissionAsUser(Object p0, Object p1, Object p2) {}
    public void setOnAccessibilityEventListener(Object p0) {}
    public boolean setRotation(Object p0) { return false; }
    public void setRunAsMonkey(Object p0) {}
    public void setServiceInfo(Object p0) {}
    public Object takeScreenshot() { return null; }
    public void waitForIdle(Object p0, Object p1) {}
}
