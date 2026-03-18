import android.view.View;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ScrollView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.ohos.shim.bridge.OHBridge;

import java.util.List;

/**
 * Layout Validator: ~40 checks verifying layout measurement, rendering
 * coordinates, and touch dispatch without ArkUI pixels.
 *
 * Run: java -cp <classpath> LayoutValidatorRunner
 */
public class LayoutValidatorRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Layout Validator ===");
        System.out.println();

        testLinearLayoutVertical();
        testLinearLayoutHorizontal();
        testFrameLayoutStacking();
        testPaddingAffectsChildren();
        testCanvasDrawCoordinates();
        testBackgroundColorRender();
        testButtonRoundRect();
        testScrollViewClipping();
        testTouchDispatchHitTesting();
        testViewTreeDump();

        System.out.println();
        System.out.println("=== Results: " + passed + " passed, " + failed + " failed, "
                + (passed + failed) + " total ===");
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ── Helpers ──

    static void section(String name) {
        System.out.println();
        System.out.println("--- " + name + " ---");
    }

    static void check(String label, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("  [PASS] " + label);
        } else {
            failed++;
            System.out.println("  [FAIL] " + label);
        }
    }

    static OHBridge.DrawRecord findDrawText(List<OHBridge.DrawRecord> log, String text) {
        for (int i = 0; i < log.size(); i++) {
            OHBridge.DrawRecord r = log.get(i);
            if ("drawText".equals(r.op) && text.equals(r.text)) {
                return r;
            }
        }
        return null;
    }

    static boolean hasDrawOp(List<OHBridge.DrawRecord> log, String op) {
        for (int i = 0; i < log.size(); i++) {
            if (op.equals(log.get(i).op)) return true;
        }
        return false;
    }

    static boolean hasDrawColor(List<OHBridge.DrawRecord> log, int color) {
        for (int i = 0; i < log.size(); i++) {
            OHBridge.DrawRecord r = log.get(i);
            if ("drawColor".equals(r.op) && r.color == color) return true;
        }
        return false;
    }

    static boolean hasDrawRoundRect(List<OHBridge.DrawRecord> log) {
        return hasDrawOp(log, "drawRoundRect");
    }

    static boolean hasTranslate(List<OHBridge.DrawRecord> log, float dx, float dy) {
        for (int i = 0; i < log.size(); i++) {
            OHBridge.DrawRecord r = log.get(i);
            if ("translate".equals(r.op) && r.args.length >= 2) {
                if (Math.abs(r.args[0] - dx) < 0.01f && Math.abs(r.args[1] - dy) < 0.01f) {
                    return true;
                }
            }
        }
        return false;
    }

    static OHBridge.DrawRecord findDrawRoundRect(List<OHBridge.DrawRecord> log) {
        for (int i = 0; i < log.size(); i++) {
            OHBridge.DrawRecord r = log.get(i);
            if ("drawRoundRect".equals(r.op)) return r;
        }
        return null;
    }

    static String dumpViewTree(View v, int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) sb.append("  ");
        sb.append(v.getClass().getSimpleName());
        sb.append(" [").append(v.getLeft()).append(",").append(v.getTop());
        sb.append(" ").append(v.getWidth()).append("x").append(v.getHeight()).append("]");
        if (v instanceof TextView) {
            sb.append(" text=\"").append(((TextView) v).getText()).append("\"");
        }
        sb.append("\n");
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                sb.append(dumpViewTree(vg.getChildAt(i), depth + 1));
            }
        }
        return sb.toString();
    }

    // ── 1. LinearLayout vertical ──

    static void testLinearLayoutVertical() {
        section("LinearLayout vertical measurement");

        LinearLayout ll = new LinearLayout(new android.content.Context());
        ll.setOrientation(LinearLayout.VERTICAL);
        TextView tv1 = new TextView();
        tv1.setText("Line 1");
        TextView tv2 = new TextView();
        tv2.setText("Line 2");
        ll.addView(tv1);
        ll.addView(tv2);

        int wSpec = View.MeasureSpec.makeMeasureSpec(480, View.MeasureSpec.EXACTLY);
        int hSpec = View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY);
        ll.measure(wSpec, hSpec);
        ll.layout(0, 0, 480, 800);

        check("ll measured width = 480", ll.getMeasuredWidth() == 480);
        check("ll measured height = 800", ll.getMeasuredHeight() == 800);
        check("tv1 top = 0", tv1.getTop() == 0);
        check("tv2 top >= tv1 bottom", tv2.getTop() >= tv1.getBottom());
        check("tv1 width = 480", tv1.getWidth() == 480);
        check("tv2 width = 480", tv2.getWidth() == 480);
        check("tv1 left = 0", tv1.getLeft() == 0);
        check("tv2 left = 0", tv2.getLeft() == 0);
    }

    // ── 2. LinearLayout horizontal ──

    static void testLinearLayoutHorizontal() {
        section("LinearLayout horizontal measurement");

        LinearLayout ll = new LinearLayout(new android.content.Context());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv1 = new TextView();
        tv1.setText("Col A");
        TextView tv2 = new TextView();
        tv2.setText("Col B");
        ll.addView(tv1);
        ll.addView(tv2);

        int wSpec = View.MeasureSpec.makeMeasureSpec(480, View.MeasureSpec.EXACTLY);
        int hSpec = View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY);
        ll.measure(wSpec, hSpec);
        ll.layout(0, 0, 480, 800);

        check("ll horizontal measured width = 480", ll.getMeasuredWidth() == 480);
        check("tv1 top = 0", tv1.getTop() == 0);
        check("tv2 top = 0", tv2.getTop() == 0);
        check("tv1 left = 0", tv1.getLeft() == 0);
        check("tv2 left >= tv1 right (side-by-side)", tv2.getLeft() >= tv1.getRight());
    }

    // ── 3. FrameLayout stacking ──

    static void testFrameLayoutStacking() {
        section("FrameLayout stacking");

        FrameLayout fl = new FrameLayout(new android.content.Context());
        TextView child1 = new TextView();
        child1.setText("A");
        TextView child2 = new TextView();
        child2.setText("B");
        fl.addView(child1);
        fl.addView(child2);

        int wSpec = View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.EXACTLY);
        int hSpec = View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY);
        fl.measure(wSpec, hSpec);
        fl.layout(0, 0, 300, 400);

        check("child1 left = 0", child1.getLeft() == 0);
        check("child1 top = 0", child1.getTop() == 0);
        check("child2 left = 0", child2.getLeft() == 0);
        check("child2 top = 0", child2.getTop() == 0);
        check("child1 width = child2 width", child1.getWidth() == child2.getWidth());
        check("child1 height = child2 height", child1.getHeight() == child2.getHeight());
    }

    // ── 4. Padding affects children ──

    static void testPaddingAffectsChildren() {
        section("Padding affects children");

        LinearLayout ll = new LinearLayout(new android.content.Context());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(10, 20, 10, 20);
        TextView child = new TextView();
        child.setText("Padded");
        ll.addView(child);

        int wSpec = View.MeasureSpec.makeMeasureSpec(480, View.MeasureSpec.EXACTLY);
        int hSpec = View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY);
        ll.measure(wSpec, hSpec);
        ll.layout(0, 0, 480, 800);

        check("child left >= paddingLeft (10)", child.getLeft() >= 10);
        check("child top >= paddingTop (20)", child.getTop() >= 20);
        check("child right <= width - paddingRight", child.getRight() <= 480 - 10);
    }

    // ── 5. Canvas draw coordinates match layout ──

    static void testCanvasDrawCoordinates() {
        section("Canvas draw coordinates match layout");

        LinearLayout ll = new LinearLayout(new android.content.Context());
        ll.setOrientation(LinearLayout.VERTICAL);
        TextView tv1 = new TextView();
        tv1.setText("Line 1");
        TextView tv2 = new TextView();
        tv2.setText("Line 2");
        ll.addView(tv1);
        ll.addView(tv2);

        int wSpec = View.MeasureSpec.makeMeasureSpec(480, View.MeasureSpec.EXACTLY);
        int hSpec = View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY);
        ll.measure(wSpec, hSpec);
        ll.layout(0, 0, 480, 800);

        Bitmap bmp = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        ll.draw(canvas);

        List<OHBridge.DrawRecord> log = OHBridge.getDrawLog(canvas.getNativeHandle());

        check("draw log is not empty", log.size() > 0);

        // Find drawText for "Line 1"
        OHBridge.DrawRecord textDraw1 = findDrawText(log, "Line 1");
        check("drawText for 'Line 1' exists", textDraw1 != null);
        if (textDraw1 != null) {
            // The drawText call is made within the child's coordinate space after translate.
            // So the x,y are relative to the child. x should be >= 0 (paddingLeft) and
            // y should be > 0 (baseline offset from ascent).
            check("drawText 'Line 1' x >= 0", textDraw1.args[0] >= 0);
            check("drawText 'Line 1' y > 0 (baseline)", textDraw1.args[1] > 0);
        }

        // Find drawText for "Line 2"
        OHBridge.DrawRecord textDraw2 = findDrawText(log, "Line 2");
        check("drawText for 'Line 2' exists", textDraw2 != null);

        // There should be translate ops in the log for child positioning
        boolean hasAnyTranslate = hasDrawOp(log, "translate");
        check("translate ops exist for child positioning", hasAnyTranslate);

        canvas.release();
        bmp.recycle();
    }

    // ── 6. Background colors render at correct positions ──

    static void testBackgroundColorRender() {
        section("Background color rendering");

        LinearLayout ll = new LinearLayout(new android.content.Context());
        ll.setOrientation(LinearLayout.VERTICAL);
        TextView tv1 = new TextView();
        tv1.setText("Red BG");
        tv1.setBackgroundColor(0xFFFF0000);
        ll.addView(tv1);

        int wSpec = View.MeasureSpec.makeMeasureSpec(480, View.MeasureSpec.EXACTLY);
        int hSpec = View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY);
        ll.measure(wSpec, hSpec);
        ll.layout(0, 0, 480, 800);

        Bitmap bmp = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        ll.draw(canvas);

        List<OHBridge.DrawRecord> log = OHBridge.getDrawLog(canvas.getNativeHandle());

        check("draw log has entries", log.size() > 0);
        check("red background (0xFFFF0000) rendered", hasDrawColor(log, 0xFFFF0000));

        // Verify text was also drawn on top of background
        OHBridge.DrawRecord textRec = findDrawText(log, "Red BG");
        check("drawText 'Red BG' exists after background", textRec != null);

        canvas.release();
        bmp.recycle();
    }

    // ── 7. Button renders rounded rect ──

    static void testButtonRoundRect() {
        section("Button rounded rect rendering");

        LinearLayout ll = new LinearLayout(new android.content.Context());
        ll.setOrientation(LinearLayout.VERTICAL);
        Button btn = new Button(new android.content.Context());
        btn.setText("OK");
        ll.addView(btn);

        int wSpec = View.MeasureSpec.makeMeasureSpec(480, View.MeasureSpec.EXACTLY);
        int hSpec = View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY);
        ll.measure(wSpec, hSpec);
        ll.layout(0, 0, 480, 800);

        Bitmap bmp = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        ll.draw(canvas);

        List<OHBridge.DrawRecord> log = OHBridge.getDrawLog(canvas.getNativeHandle());

        check("draw log has entries for button", log.size() > 0);
        check("drawRoundRect exists (button bg)", hasDrawRoundRect(log));

        OHBridge.DrawRecord rrec = findDrawRoundRect(log);
        if (rrec != null && rrec.args.length >= 4) {
            // The drawRoundRect is drawn in child coordinate space (0,0,width,height)
            check("roundRect left = 0 (child coords)", rrec.args[0] == 0);
            check("roundRect top = 0 (child coords)", rrec.args[1] == 0);
            check("roundRect right > 0", rrec.args[2] > 0);
            check("roundRect bottom > 0", rrec.args[3] > 0);
        }

        OHBridge.DrawRecord textRec = findDrawText(log, "OK");
        check("drawText 'OK' exists (button label)", textRec != null);

        canvas.release();
        bmp.recycle();
    }

    // ── 8. ScrollView clips children ──

    static void testScrollViewClipping() {
        section("ScrollView scroll offset");

        ScrollView sv = new ScrollView(new android.content.Context());
        LinearLayout content = new LinearLayout(new android.content.Context());
        content.setOrientation(LinearLayout.VERTICAL);
        // Add many children that exceed ScrollView height
        for (int i = 0; i < 20; i++) {
            TextView tv = new TextView();
            tv.setText("Item " + i);
            content.addView(tv);
        }
        sv.addView(content);

        int wSpec = View.MeasureSpec.makeMeasureSpec(480, View.MeasureSpec.EXACTLY);
        int hSpec = View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY);
        sv.measure(wSpec, hSpec);
        sv.layout(0, 0, 480, 200);

        sv.scrollTo(0, 50);
        check("scrollY = 50", sv.getScrollY() == 50);

        Bitmap bmp = Bitmap.createBitmap(480, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        sv.draw(canvas);

        List<OHBridge.DrawRecord> log = OHBridge.getDrawLog(canvas.getNativeHandle());

        check("draw log not empty after scroll draw", log.size() > 0);
        // ScrollView.dispatchDraw translates by -scrollY before drawing children
        check("translate(0, -50) in draw log for scroll offset", hasTranslate(log, 0, -50));

        canvas.release();
        bmp.recycle();
    }

    // ── 9. Touch dispatch hit-testing ──

    static void testTouchDispatchHitTesting() {
        section("Touch dispatch hit-testing");

        LinearLayout root = new LinearLayout(new android.content.Context());
        root.setOrientation(LinearLayout.VERTICAL);
        Button btn1 = new Button(new android.content.Context());
        btn1.setText("Top");
        Button btn2 = new Button(new android.content.Context());
        btn2.setText("Bottom");
        root.addView(btn1);
        root.addView(btn2);

        int wSpec = View.MeasureSpec.makeMeasureSpec(480, View.MeasureSpec.EXACTLY);
        int hSpec = View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY);
        root.measure(wSpec, hSpec);
        root.layout(0, 0, 480, 800);

        check("btn1 height > 0", btn1.getHeight() > 0);
        check("btn2 height > 0", btn2.getHeight() > 0);
        check("btn2 top >= btn1 bottom", btn2.getTop() >= btn1.getBottom());

        // Track which button was clicked
        final String[] clicked = {""};
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { clicked[0] = "btn1"; }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { clicked[0] = "btn2"; }
        });

        // Touch at btn1's center
        float btn1CenterY = (btn1.getTop() + btn1.getBottom()) / 2f;
        MotionEvent touch1 = MotionEvent.obtain(MotionEvent.ACTION_UP, 240, btn1CenterY, 0);
        root.dispatchTouchEvent(touch1);
        check("touch hits btn1", "btn1".equals(clicked[0]));

        // Touch at btn2's center
        float btn2CenterY = (btn2.getTop() + btn2.getBottom()) / 2f;
        clicked[0] = "";
        MotionEvent touch2 = MotionEvent.obtain(MotionEvent.ACTION_UP, 240, btn2CenterY, 0);
        root.dispatchTouchEvent(touch2);
        check("touch hits btn2", "btn2".equals(clicked[0]));

        // Touch outside the root bounds entirely (negative Y)
        clicked[0] = "";
        MotionEvent touch3 = MotionEvent.obtain(MotionEvent.ACTION_UP, 240, -10, 0);
        root.dispatchTouchEvent(touch3);
        check("touch at (240,-10) misses both buttons", "".equals(clicked[0]));

        touch1.recycle();
        touch2.recycle();
        touch3.recycle();
    }

    // ── 10. View tree dump ──

    static void testViewTreeDump() {
        section("View tree dump");

        LinearLayout root = new LinearLayout(new android.content.Context());
        root.setOrientation(LinearLayout.VERTICAL);
        Button btn = new Button(new android.content.Context());
        btn.setText("Press Me");
        TextView label = new TextView();
        label.setText("Hello");
        root.addView(btn);
        root.addView(label);

        int wSpec = View.MeasureSpec.makeMeasureSpec(480, View.MeasureSpec.EXACTLY);
        int hSpec = View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY);
        root.measure(wSpec, hSpec);
        root.layout(0, 0, 480, 800);

        String dump = dumpViewTree(root, 0);
        System.out.println("  Tree dump:");
        // Print each line of dump with indent
        String[] lines = dump.split("\n");
        for (int i = 0; i < lines.length; i++) {
            System.out.println("    " + lines[i]);
        }

        check("dump contains LinearLayout", dump.contains("LinearLayout"));
        check("dump contains Button", dump.contains("Button"));
        check("dump contains TextView", dump.contains("TextView"));
        check("dump shows 480x dimension", dump.contains("480x"));
        check("dump shows 'Press Me' text", dump.contains("Press Me"));
        check("dump shows 'Hello' text", dump.contains("Hello"));
    }
}
