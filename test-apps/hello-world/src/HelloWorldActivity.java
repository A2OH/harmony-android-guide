import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;

/**
 * Minimal Hello World Activity for end-to-end testing.
 *
 * Tests the full path:
 *   MiniServer → Activity lifecycle → setContentView → View tree → rendering
 *
 * Can run on:
 *   1. JVM with mock OHBridge (headless test)
 *   2. x86_64 Dalvik VM on Linux
 *   3. ARM32 Dalvik VM on OHOS QEMU
 */
public class HelloWorldActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("[HelloWorld] onCreate");

        // Build view tree programmatically (no layout XML needed)
        LinearLayout layout = new LinearLayout(new android.content.Context());
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView();
        title.setText("Hello World!");
        title.setTextSize(24);
        title.setTextColor(0xFF000000);
        layout.addView(title);

        TextView subtitle = new TextView();
        subtitle.setText("Android running on OpenHarmony");
        subtitle.setTextSize(14);
        subtitle.setTextColor(0xFF666666);
        layout.addView(subtitle);

        Button button = new Button(new android.content.Context());
        button.setText("Click Me");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[HelloWorld] Button clicked!");
                title.setText("Button was clicked!");
            }
        });
        layout.addView(button);

        setContentView(layout);
        System.out.println("[HelloWorld] setContentView done, view tree built");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("[HelloWorld] onResume — Activity is live!");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("[HelloWorld] onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("[HelloWorld] onDestroy");
    }
}
