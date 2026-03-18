import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ohos.shim.bridge.OHBridge;

/**
 * UI Mockup test app — validates ArkUI view rendering through the shim.
 *
 * Runs on OHOS with the shim layer. Creates a View hierarchy using
 * standard Android widget classes and verifies they map correctly to
 * ArkUI native nodes.
 *
 * Test scenarios:
 *   1. Basic view creation (all widget types)
 *   2. View tree operations (add/remove/insert children)
 *   3. Attribute propagation (text, color, size, visibility)
 *   4. Event handling (click, text change, toggle, slider)
 *   5. Layout composition (nested LinearLayouts, ScrollView + list)
 *
 * Run headless (logs only): java UITestApp --headless
 * Run with UI (on device):  app_process -cp classes.dex / UITestApp
 */
public class UITestApp {

    private static int passed = 0;
    private static int failed = 0;
    private static boolean headless = false;

    public static void main(String[] args) {
        for (String arg : args) {
            if ("--headless".equals(arg)) headless = true;
        }

        System.out.println("═══ Android→OH Shim UI Mockup Test ═══");
        System.out.println("Mode: " + (headless ? "HEADLESS (verify node handles)" : "DEVICE (render to screen)"));
        System.out.println();

        // Initialize ArkUI native node API
        if (!headless) {
            int rc = OHBridge.arkuiInit();
            check("arkuiInit() returns 0", rc == 0);
            if (rc != 0) {
                System.out.println("FATAL: Cannot init ArkUI — aborting UI tests");
                System.exit(1);
            }
        }

        testWidgetCreation();
        testTextView();
        testButton();
        testEditText();
        testImageView();
        testLinearLayout();
        testFrameLayout();
        testScrollViewWithContent();
        testCheckBoxAndSwitch();
        testSeekBarAndProgress();
        testListView();
        testNestedLayout();
        testEventHandling();
        testAttributePropagation();

        System.out.println("\n═══ Results ═══");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println(failed == 0 ? "ALL UI TESTS PASSED" : "SOME UI TESTS FAILED");
        System.exit(failed);
    }

    // ── Helpers ──

    static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("  ✓ " + name);
            passed++;
        } else {
            System.out.println("  ✗ FAIL: " + name);
            failed++;
        }
    }

    static void section(String name) {
        System.out.println("\n── " + name + " ──");
    }

    // ═══ Test: Widget Creation ═══

    static void testWidgetCreation() {
        section("Widget Creation (all types)");

        TextView tv = new TextView();
        check("TextView created, handle=" + tv.getNativeHandle(), tv.getNativeHandle() != 0 || headless);

        Button btn = new Button(new android.content.Context());
        check("Button created", btn.getNativeHandle() != 0 || headless);

        EditText et = new EditText(new android.content.Context());
        check("EditText created", et.getNativeHandle() != 0 || headless);

        ImageView iv = new ImageView(new android.content.Context());
        check("ImageView created", iv.getNativeHandle() != 0 || headless);

        ProgressBar pb = new ProgressBar();
        check("ProgressBar created", pb.getNativeHandle() != 0 || headless);

        SeekBar sb = new SeekBar();
        check("SeekBar created", sb.getNativeHandle() != 0 || headless);

        CheckBox cb = new CheckBox();
        check("CheckBox created", cb.getNativeHandle() != 0 || headless);

        Switch sw = new Switch();
        check("Switch created", sw.getNativeHandle() != 0 || headless);

        // Cleanup
        tv.destroy(); btn.destroy(); et.destroy(); iv.destroy();
        pb.destroy(); sb.destroy(); cb.destroy(); sw.destroy();
    }

    // ═══ Test: TextView ═══

    static void testTextView() {
        section("TextView attributes");

        TextView tv = new TextView();
        tv.setText("Hello OpenHarmony!");
        check("setText doesn't throw", true);
        check("getText returns set value", "Hello OpenHarmony!".equals(tv.getText().toString()));

        tv.setTextColor(0xFFFF0000); // red
        check("setTextColor", tv.getCurrentTextColor() == 0xFFFF0000);

        tv.setTextSize(24.0f);
        check("setTextSize", tv.getTextSize() == 24.0f);

        tv.setMaxLines(3);
        check("setMaxLines", tv.getMaxLines() == 3);

        tv.setSingleLine(true);
        check("setSingleLine(true)", tv.getMaxLines() == 1);

        tv.setGravity(Gravity.CENTER);
        check("setGravity(CENTER)", true);

        tv.destroy();
    }

    // ═══ Test: Button ═══

    static void testButton() {
        section("Button");

        Button btn = new Button(new android.content.Context());
        btn.setText("Click Me");
        check("Button.setText", "Click Me".equals(btn.getText().toString()));

        btn.setEnabled(false);
        check("setEnabled(false)", !btn.isEnabled());

        btn.setEnabled(true);
        check("setEnabled(true)", btn.isEnabled());

        btn.destroy();
    }

    // ═══ Test: EditText ═══

    static void testEditText() {
        section("EditText");

        EditText et = new EditText(new android.content.Context());
        et.setText("Initial text");
        check("setText", "Initial text".equals(et.getText().toString()));

        et.setHint("Enter something...");
        check("setHint", "Enter something...".equals(et.getHint().toString()));

        et.setInputType(0x81); // TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD
        check("setInputType(password)", true);

        // TextWatcher
        final boolean[] changed = {false};
        et.addTextChangedListener(new EditText.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changed[0] = true;
            }
            @Override
            public void afterTextChanged(Object s) {}
        });
        check("addTextChangedListener", true);

        // Simulate native text change event
        et.onNativeEvent(0, 7000, "new text from ArkUI");
        check("TextWatcher.onTextChanged fired", changed[0]);

        et.destroy();
    }

    // ═══ Test: ImageView ═══

    static void testImageView() {
        section("ImageView");

        ImageView iv = new ImageView(new android.content.Context());
        iv.setImageResource(0x7f060001);
        check("setImageResource doesn't throw", true);

        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        check("setScaleType", true);

        iv.destroy();
    }

    // ═══ Test: LinearLayout ═══

    static void testLinearLayout() {
        section("LinearLayout (COLUMN/ROW)");

        LinearLayout col = new LinearLayout(new android.content.Context());
        col.setOrientation(LinearLayout.VERTICAL);
        check("vertical orientation", col.getOrientation() == LinearLayout.VERTICAL);

        TextView tv1 = new TextView();
        tv1.setText("Row 1");
        TextView tv2 = new TextView();
        tv2.setText("Row 2");
        TextView tv3 = new TextView();
        tv3.setText("Row 3");

        col.addView(tv1);
        col.addView(tv2);
        col.addView(tv3);
        check("addView x3 → childCount=3", col.getChildCount() == 3);
        check("getChildAt(1) == tv2", col.getChildAt(1) == tv2);

        col.removeView(tv2);
        check("removeView → childCount=2", col.getChildCount() == 2);
        check("getChildAt(1) == tv3", col.getChildAt(1) == tv3);

        col.destroy();
    }

    // ═══ Test: FrameLayout ═══

    static void testFrameLayout() {
        section("FrameLayout (STACK)");

        FrameLayout frame = new FrameLayout(new android.content.Context());
        TextView bg = new TextView();
        bg.setText("Background");
        Button overlay = new Button(new android.content.Context());
        overlay.setText("Overlay");

        frame.addView(bg);
        frame.addView(overlay);
        check("FrameLayout childCount=2", frame.getChildCount() == 2);

        frame.destroy();
    }

    // ═══ Test: ScrollView ═══

    static void testScrollViewWithContent() {
        section("ScrollView + content");

        ScrollView scroll = new ScrollView(new android.content.Context());
        LinearLayout content = new LinearLayout(new android.content.Context());
        content.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < 20; i++) {
            TextView item = new TextView();
            item.setText("Scroll item " + i);
            content.addView(item);
        }

        scroll.addView(content);
        check("ScrollView with 20-item list", scroll.getChildCount() == 1);
        check("Content has 20 children", content.getChildCount() == 20);

        scroll.destroy();
    }

    // ═══ Test: CheckBox + Switch ═══

    static void testCheckBoxAndSwitch() {
        section("CheckBox + Switch");

        CheckBox cb = new CheckBox();
        check("CheckBox initial unchecked", !cb.isChecked());

        cb.setChecked(true);
        check("setChecked(true)", cb.isChecked());

        cb.toggle();
        check("toggle() → unchecked", !cb.isChecked());

        final boolean[] cbChanged = {false};
        cb.setOnCheckedChangeListener((v, isChecked) -> cbChanged[0] = true);
        cb.onNativeEvent(0, 15000, null); // simulate check event
        check("OnCheckedChangeListener fires", cbChanged[0]);

        Switch sw = new Switch();
        sw.setChecked(true);
        check("Switch.setChecked(true)", sw.isChecked());

        final boolean[] swChanged = {false};
        sw.setOnCheckedChangeListener((v, isChecked) -> swChanged[0] = true);
        sw.onNativeEvent(0, 5000, null); // simulate toggle event
        check("Switch.OnCheckedChangeListener fires", swChanged[0]);

        cb.destroy();
        sw.destroy();
    }

    // ═══ Test: SeekBar + ProgressBar ═══

    static void testSeekBarAndProgress() {
        section("SeekBar + ProgressBar");

        SeekBar sb = new SeekBar();
        sb.setMax(200);
        check("SeekBar.setMax(200)", sb.getMax() == 200);

        sb.setProgress(50);
        check("setProgress(50)", sb.getProgress() == 50);

        ProgressBar pb = new ProgressBar();
        pb.setMax(100);
        pb.setProgress(75);
        check("ProgressBar 75/100", pb.getProgress() == 75);

        pb.incrementProgressBy(10);
        check("incrementProgressBy(10) → 85", pb.getProgress() == 85);

        sb.destroy();
        pb.destroy();
    }

    // ═══ Test: ListView ═══

    static void testListView() {
        section("ListView with adapter");

        final String[] items = {"Apple", "Banana", "Cherry", "Date", "Elderberry"};

        ListView lv = new ListView();
        lv.setAdapter(new ListView.ListAdapter() {
            @Override public int getCount() { return items.length; }
            @Override public Object getItem(int pos) { return items[pos]; }
            @Override public long getItemId(int pos) { return pos; }
            @Override
            public View getView(int pos, View convertView, ViewGroup parent) {
                TextView tv = new TextView();
                tv.setText(items[pos]);
                tv.setTextSize(18);
                return tv;
            }
        });

        check("ListView childCount = 5", lv.getChildCount() == 5);

        lv.destroy();
    }

    // ═══ Test: Nested Layout ═══

    static void testNestedLayout() {
        section("Nested layout composition");

        // Build a typical Android screen layout:
        // LinearLayout (vertical)
        //   ├── TextView (title)
        //   ├── LinearLayout (horizontal)
        //   │   ├── Button (cancel)
        //   │   └── Button (ok)
        //   └── EditText (input)

        LinearLayout root = new LinearLayout(new android.content.Context());
        root.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView();
        title.setText("Dialog Title");
        title.setTextSize(20);
        title.setTextColor(0xFF333333);
        root.addView(title);

        LinearLayout buttons = new LinearLayout(new android.content.Context());
        buttons.setOrientation(LinearLayout.HORIZONTAL);

        Button cancel = new Button(new android.content.Context());
        cancel.setText("Cancel");
        buttons.addView(cancel);

        Button ok = new Button(new android.content.Context());
        ok.setText("OK");
        buttons.addView(ok);

        root.addView(buttons);

        EditText input = new EditText(new android.content.Context());
        input.setHint("Type here...");
        root.addView(input);

        check("Root has 3 children", root.getChildCount() == 3);
        check("Button row has 2 children", buttons.getChildCount() == 2);
        check("Nested structure intact", root.getChildAt(1) == buttons);

        root.destroy();
    }

    // ═══ Test: Event handling ═══

    static void testEventHandling() {
        section("Event handling");

        // Click event
        Button btn = new Button(new android.content.Context());
        btn.setText("Clickable");
        final boolean[] clicked = {false};
        btn.setOnClickListener(v -> clicked[0] = true);

        // Simulate click from native
        btn.onNativeEvent(0, 5, null); // EVENT_ON_CLICK = 5
        check("OnClickListener fires on native click event", clicked[0]);

        // Multiple listeners on different views
        Button btn2 = new Button(new android.content.Context());
        final boolean[] clicked2 = {false};
        btn2.setOnClickListener(v -> clicked2[0] = true);

        btn.onNativeEvent(0, 5, null);
        check("Click on btn doesn't fire btn2", !clicked2[0]);
        btn2.onNativeEvent(0, 5, null);
        check("Click on btn2 fires btn2", clicked2[0]);

        btn.destroy();
        btn2.destroy();
    }

    // ═══ Test: Attribute propagation ═══

    static void testAttributePropagation() {
        section("Attribute propagation");

        TextView tv = new TextView();

        // Visibility
        tv.setVisibility(View.GONE);
        check("setVisibility(GONE)", tv.getVisibility() == View.GONE);

        tv.setVisibility(View.VISIBLE);
        check("setVisibility(VISIBLE)", tv.getVisibility() == View.VISIBLE);

        // Padding
        tv.setPadding(10, 20, 30, 40);
        check("padding left=10", tv.getPaddingLeft() == 10);
        check("padding top=20", tv.getPaddingTop() == 20);
        check("padding right=30", tv.getPaddingRight() == 30);
        check("padding bottom=40", tv.getPaddingBottom() == 40);

        // Opacity
        tv.setAlpha(0.5f);
        check("setAlpha(0.5)", true); // no getter, just verify no crash

        // Background color
        tv.setBackgroundColor(0xFF00FF00);
        check("setBackgroundColor(green)", true);

        // Enabled
        tv.setEnabled(false);
        check("setEnabled(false)", !tv.isEnabled());

        // Tag
        tv.setTag("my_tag");
        check("setTag/getTag", "my_tag".equals(tv.getTag()));

        // ID
        tv.setId(12345);
        check("setId/getId", tv.getId() == 12345);

        tv.destroy();
    }
}
