package android.app;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

public class ActivityOptions {
    public static final int EXTRA_USAGE_TIME_REPORT = 0;
    public static final int EXTRA_USAGE_TIME_REPORT_PACKAGES = 0;

    public ActivityOptions() {}

    public int getLaunchDisplayId() { return 0; }
    public boolean getLockTaskMode() { return false; }
    public static ActivityOptions makeBasic() { return null; }
    public static ActivityOptions makeClipRevealAnimation(View p0, int p1, int p2, int p3, int p4) { return null; }
    public static ActivityOptions makeCustomAnimation(Context p0, int p1, int p2) { return null; }
    public static ActivityOptions makeScaleUpAnimation(View p0, int p1, int p2, int p3, int p4) { return null; }
    public static ActivityOptions makeSceneTransitionAnimation(Activity p0, View p1, String p2) { return null; }
    public static ActivityOptions makeTaskLaunchBehind() { return null; }
    public static ActivityOptions makeThumbnailScaleUpAnimation(View p0, Bitmap p1, int p2, int p3) { return null; }
    public void requestUsageTimeReport(PendingIntent p0) {}
    public ActivityOptions setAppVerificationBundle(Bundle p0) { return null; }
    public ActivityOptions setLaunchBounds(Rect p0) { return null; }
    public ActivityOptions setLaunchDisplayId(int p0) { return null; }
    public ActivityOptions setLockTaskEnabled(boolean p0) { return null; }
    public Bundle toBundle() { return null; }
    public void update(ActivityOptions p0) {}
}
