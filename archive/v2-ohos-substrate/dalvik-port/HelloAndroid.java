import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
// Use our own minimal Log to avoid OHBridge dependency

/**
 * Minimal Android-skeleton app running on Dalvik VM.
 * Uses the shim framework classes (Activity, Bundle, Intent, Application).
 * No actual UI — exercises the Android lifecycle and API surface headlessly.
 */
public class HelloAndroid {
    private static final String TAG = "HelloAndroid";

    /** Minimal Log that prints to stdout (avoids OHBridge dependency) */
    static class Log {
        static int i(String tag, String msg) { System.out.println("I/" + tag + ": " + msg); return 0; }
        static int d(String tag, String msg) { System.out.println("D/" + tag + ": " + msg); return 0; }
    }

    /** Our "Activity" subclass */
    static class MainActivity extends Activity {
        @Override
        public void onCreate(Object savedInstanceState, Object persistentState) {
            super.onCreate(savedInstanceState, persistentState);
            Log.i(TAG, "MainActivity.onCreate called");
            Log.d(TAG, "  savedInstanceState = " + savedInstanceState);

            // Use Bundle
            Bundle extras = new Bundle();
            extras.putString("greeting", "Hello from Android on OHOS!");
            extras.putInt("answer", 42);
            Log.i(TAG, "  Bundle created with greeting and answer=42");

            // Use Intent
            Intent intent = new Intent();
            Log.i(TAG, "  Intent created: " + intent);

            // Query system
            String arch = System.getProperty("os.arch");
            String osName = System.getProperty("os.name");
            String vmName = System.getProperty("java.vm.name");
            Log.i(TAG, "  Running on: " + osName + "/" + arch + " VM=" + vmName);
        }

        public void onStart() {
            Log.i(TAG, "MainActivity.onStart called");
        }

        public void onResume() {
            Log.i(TAG, "MainActivity.onResume called");
        }

        public void onPause() {
            Log.i(TAG, "MainActivity.onPause called");
        }

        public void onStop() {
            Log.i(TAG, "MainActivity.onStop called");
        }

        public void onDestroy() {
            Log.i(TAG, "MainActivity.onDestroy called");
        }
    }

    public static void main(String[] args) {
        Log.i(TAG, "=== Android App Skeleton on Dalvik/OHOS ===");

        // Create Application
        Application app = new Application();
        Log.i(TAG, "Application created: " + app.getClass().getName());

        // Create and drive Activity lifecycle
        MainActivity activity = new MainActivity();
        Log.i(TAG, "Activity created: " + activity.getClass().getName());

        Log.i(TAG, "--- Lifecycle: onCreate ---");
        Bundle savedState = null;
        activity.onCreate(savedState, null);

        Log.i(TAG, "--- Lifecycle: onStart ---");
        activity.onStart();

        Log.i(TAG, "--- Lifecycle: onResume ---");
        activity.onResume();

        // Simulate some work
        Log.i(TAG, "--- App is running ---");
        int result = 6 * 7;
        Log.i(TAG, "  Computation: 6 * 7 = " + result);

        // Create an Intent as if launching another activity
        Intent launchIntent = new Intent();
        Log.i(TAG, "  Created launch intent: " + launchIntent);

        // Simulate going to background
        Log.i(TAG, "--- Lifecycle: onPause ---");
        activity.onPause();

        Log.i(TAG, "--- Lifecycle: onStop ---");
        activity.onStop();

        Log.i(TAG, "--- Lifecycle: onDestroy ---");
        activity.onDestroy();

        // Activity.finish()
        activity.finish();
        Log.i(TAG, "Activity finished");

        Log.i(TAG, "=== Android App Skeleton Complete ===");
    }
}
