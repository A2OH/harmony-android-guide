import android.app.Activity;
import android.app.MiniServer;
import android.app.MiniActivityManager;
import android.content.Intent;

public class RealApkRunner {
    public static void main(String[] args) {
        System.out.println("=== Real APK Runner ===");

        MiniServer.init("com.example.hello");
        MiniActivityManager am = MiniServer.get().getActivityManager();

        Intent intent = new Intent();
        intent.setClassName("com.example.hello", "com.example.hello.HelloActivity");
        am.startActivity(null, intent, 0);

        Activity activity = am.getResumedActivity();
        if (activity != null) {
            System.out.println("Activity: " + activity.getClass().getName());
        } else {
            System.out.println("ERROR: Activity not started");
        }
        System.out.println("=== Real APK Runner DONE ===");
    }
}
