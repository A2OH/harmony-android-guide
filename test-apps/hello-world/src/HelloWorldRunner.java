import android.app.MiniServer;
import android.content.ComponentName;
import android.content.Intent;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Headless runner for HelloWorldActivity.
 *
 * Exercises the full engine path without rendering:
 *   1. MiniServer init
 *   2. Activity instantiation + lifecycle
 *   3. setContentView → view tree construction
 *   4. Simulated touch input
 *   5. Activity finish + destroy
 *
 * Exit code 0 = success, 1 = failure
 */
public class HelloWorldRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("═══ Hello World APK End-to-End Test ═══\n");

        try {
            // 1. Initialize MiniServer
            System.out.println("── Step 1: Initialize MiniServer ──");
            MiniServer.init("com.example.helloworld");
            MiniServer server = MiniServer.get();
            check("MiniServer initialized", server != null);
            check("ActivityManager ready", server.getActivityManager() != null);

            // 2. Start HelloWorldActivity
            System.out.println("\n── Step 2: Start HelloWorldActivity ──");
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(
                    "com.example.helloworld", "HelloWorldActivity"));
            server.getActivityManager().startActivity(null, intent, -1);

            Activity activity = server.getActivityManager().getResumedActivity();
            check("Activity resumed", activity != null);
            check("Activity is HelloWorldActivity", activity instanceof HelloWorldActivity);

            // 3. Check view tree
            System.out.println("\n── Step 3: Verify view tree ──");
            View decor = activity.getWindow().getDecorView();
            check("Decor view exists", decor != null);

            // Walk the view tree and find our widgets
            TextView titleView = null;
            TextView subtitleView = null;
            android.widget.Button buttonView = null;

            if (decor instanceof ViewGroup) {
                titleView = findTextView((ViewGroup) decor, "Hello World!");
                subtitleView = findTextView((ViewGroup) decor, "Android running on OpenHarmony");
                buttonView = findButton((ViewGroup) decor, "Click Me");
            }

            check("Title TextView found", titleView != null);
            check("Title text is 'Hello World!'",
                    titleView != null && "Hello World!".equals(titleView.getText().toString()));
            check("Subtitle TextView found", subtitleView != null);
            check("Button found", buttonView != null);
            check("Button text is 'Click Me'",
                    buttonView != null && "Click Me".equals(buttonView.getText().toString()));

            // 4. Simulate rendering (headless — just verifies no crash)
            System.out.println("\n── Step 4: Headless render ──");
            if (decor != null) {
                decor.layout(0, 0, 480, 800);
                // Create a bitmap-backed canvas for rendering test
                android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(
                        480, 800, android.graphics.Bitmap.Config.ARGB_8888);
                android.graphics.Canvas canvas = new android.graphics.Canvas(bmp);
                decor.draw(canvas);
                check("View tree renders without crash", true);

                // Check draw log for expected ops
                java.util.List<com.ohos.shim.bridge.OHBridge.DrawRecord> log =
                        com.ohos.shim.bridge.OHBridge.getDrawLog(canvas.getNativeHandle());
                check("Rendering produces draw ops", log.size() > 0);
                check("Rendering includes drawText",
                        log.stream().anyMatch(r -> "drawText".equals(r.op)));

                canvas.release();
                bmp.recycle();
            }

            // 5. Simulate button click via touch dispatch
            System.out.println("\n── Step 5: Simulate button click ──");
            if (buttonView != null) {
                // Layout button so hit testing works
                buttonView.layout(0, 100, 200, 150);
                // Simulate tap
                MotionEvent down = MotionEvent.obtain(MotionEvent.ACTION_DOWN, 100, 125, 1000);
                MotionEvent up = MotionEvent.obtain(MotionEvent.ACTION_UP, 100, 125, 1050);
                buttonView.dispatchTouchEvent(down);
                buttonView.dispatchTouchEvent(up);

                // Check that click handler changed the title
                check("Button click changed title",
                        titleView != null && "Button was clicked!".equals(titleView.getText().toString()));
            }

            // 6. Finish activity
            System.out.println("\n── Step 6: Finish activity ──");
            activity.finish();
            check("Activity finished", activity.isFinishing());

        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            failed++;
        }

        // Results
        System.out.println("\n═══ Results ═══");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println(failed == 0 ? "ALL TESTS PASSED — Hello World works!" : "SOME TESTS FAILED");
        System.exit(failed);
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("  ✓ " + name);
            passed++;
        } else {
            System.out.println("  ✗ FAIL: " + name);
            failed++;
        }
    }

    private static TextView findTextView(ViewGroup parent, String text) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof TextView && text.equals(((TextView) child).getText().toString())) {
                return (TextView) child;
            }
            if (child instanceof ViewGroup) {
                TextView found = findTextView((ViewGroup) child, text);
                if (found != null) return found;
            }
        }
        return null;
    }

    private static android.widget.Button findButton(ViewGroup parent, String text) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof android.widget.Button
                    && text.equals(((android.widget.Button) child).getText().toString())) {
                return (android.widget.Button) child;
            }
            if (child instanceof ViewGroup) {
                android.widget.Button found = findButton((ViewGroup) child, text);
                if (found != null) return found;
            }
        }
        return null;
    }
}
