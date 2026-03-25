package com.example.mockdonalds;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;

/**
 * Tests BinaryXmlParser by manually inflating compiled XML into real Android Views.
 */
public class XmlTestHelper {

    public static View testXmlInflation(Context ctx) {
        LinearLayout result = new LinearLayout(ctx);
        result.setOrientation(LinearLayout.VERTICAL);
        result.setPadding(dp(ctx,16), dp(ctx,16), dp(ctx,16), dp(ctx,16));
        result.setBackgroundColor(0xFFFFFFFF);

        result.addView(boldLabel(ctx, "XML Layout Inflation Test", 22, 0xFF212121));

        try {
            // Read binary XML from assets
            java.io.InputStream is = ctx.getAssets().open("test_layout.axml");
            byte[] data = new byte[is.available()];
            is.read(data);
            is.close();

            result.addView(label(ctx, "AXML data: " + data.length + " bytes", 14, 0xFF757575));

            // Parse with BinaryXmlParser
            android.content.res.BinaryXmlParser parser = new android.content.res.BinaryXmlParser(data);

            // Manually inflate — walk XML events and create Views
            View inflated = inflateFromParser(ctx, parser);

            if (inflated != null) {
                result.addView(boldLabel(ctx, "✅ Inflated: " + inflated.getClass().getSimpleName(), 18, 0xFF4CAF50));

                // Count children
                if (inflated instanceof ViewGroup) {
                    int count = ((ViewGroup) inflated).getChildCount();
                    result.addView(label(ctx, "Children: " + count, 14, 0xFF757575));
                    for (int i = 0; i < count; i++) {
                        View child = ((ViewGroup) inflated).getChildAt(i);
                        String desc = child.getClass().getSimpleName();
                        if (child instanceof TextView) desc += " → \"" + ((TextView)child).getText() + "\"";
                        result.addView(label(ctx, "  [" + i + "] " + desc, 13, 0xFF424242));
                    }
                }

                // Show the inflated view
                View divider = new View(ctx);
                divider.setBackgroundColor(0xFFE0E0E0);
                LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx,2));
                dlp.setMargins(0, dp(ctx,16), 0, dp(ctx,16));
                divider.setLayoutParams(dlp);
                result.addView(divider);

                result.addView(boldLabel(ctx, "Rendered View:", 16, 0xFF212121));

                if (inflated.getParent() != null)
                    ((ViewGroup) inflated.getParent()).removeView(inflated);
                result.addView(inflated);
            } else {
                result.addView(boldLabel(ctx, "❌ Inflation returned null", 18, 0xFFFF0000));
            }
        } catch (Exception e) {
            result.addView(boldLabel(ctx, "❌ Error: " + e.getClass().getSimpleName(), 16, 0xFFFF0000));
            result.addView(label(ctx, e.getMessage(), 13, 0xFFFF0000));
            // Print stack trace
            java.io.StringWriter sw = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(sw));
            String stack = sw.toString();
            if (stack.length() > 500) stack = stack.substring(0, 500) + "...";
            result.addView(label(ctx, stack, 10, 0xFF999999));
        }

        // Back button
        Button back = new Button(ctx);
        back.setText("← Back to Menu");
        back.setTextSize(16);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { MockApp.showMenu(); }
        });
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        blp.topMargin = dp(ctx, 16);
        back.setLayoutParams(blp);
        result.addView(back);

        return result;
    }

    /**
     * Manually inflate a parsed AXML into real Android Views.
     * This bypasses the phone's LayoutInflater (which expects XmlBlock.Parser).
     */
    static View inflateFromParser(Context ctx, XmlPullParser parser) throws Exception {
        java.util.Stack<ViewGroup> stack = new java.util.Stack<>();
        View rootView = null;

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tag = parser.getName();
                View view = createViewForTag(ctx, tag);

                if (view != null) {
                    // Apply attributes
                    applyAttributes(ctx, view, parser);

                    // Add to parent
                    if (!stack.isEmpty()) {
                        ViewGroup parent = stack.peek();
                        parent.addView(view);
                    } else {
                        rootView = view;
                    }

                    // Push if it's a ViewGroup
                    if (view instanceof ViewGroup) {
                        stack.push((ViewGroup) view);
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                String tag = parser.getName();
                // Pop ViewGroup from stack
                if (!stack.isEmpty()) {
                    View top = stack.peek();
                    if (top.getClass().getSimpleName().equals(tag) ||
                        ("android.widget." + tag).equals(top.getClass().getName()) ||
                        ("android.view." + tag).equals(top.getClass().getName())) {
                        stack.pop();
                    }
                }
            }
            eventType = parser.next();
        }

        return rootView;
    }

    static View createViewForTag(Context ctx, String tag) {
        // Map common tags to real Android View classes
        if ("LinearLayout".equals(tag)) return new LinearLayout(ctx);
        if ("TextView".equals(tag)) return new TextView(ctx);
        if ("Button".equals(tag)) return new Button(ctx);
        if ("ListView".equals(tag)) return new ListView(ctx);
        if ("View".equals(tag)) return new View(ctx);
        if ("ScrollView".equals(tag)) return new android.widget.ScrollView(ctx);
        if ("FrameLayout".equals(tag)) return new android.widget.FrameLayout(ctx);
        if ("RelativeLayout".equals(tag)) return new android.widget.RelativeLayout(ctx);
        if ("ImageView".equals(tag)) return new android.widget.ImageView(ctx);
        if ("EditText".equals(tag)) return new android.widget.EditText(ctx);
        if ("CheckBox".equals(tag)) return new android.widget.CheckBox(ctx);
        if ("Switch".equals(tag)) return new android.widget.Switch(ctx);
        if ("ProgressBar".equals(tag)) return new android.widget.ProgressBar(ctx);
        if ("SeekBar".equals(tag)) return new android.widget.SeekBar(ctx);

        // Try full class name
        try {
            Class<?> cls = Class.forName(tag);
            return (View) cls.getConstructor(Context.class).newInstance(ctx);
        } catch (Exception e) {}
        try {
            Class<?> cls = Class.forName("android.widget." + tag);
            return (View) cls.getConstructor(Context.class).newInstance(ctx);
        } catch (Exception e) {}
        try {
            Class<?> cls = Class.forName("android.view." + tag);
            return (View) cls.getConstructor(Context.class).newInstance(ctx);
        } catch (Exception e) {}

        return null; // Unknown tag
    }

    static void applyAttributes(Context ctx, View view, XmlPullParser parser) {
        int count = parser.getAttributeCount();
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        float weight = 0;

        for (int i = 0; i < count; i++) {
            String name = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);

            if ("layout_width".equals(name)) {
                width = parseDimension(ctx, value);
            } else if ("layout_height".equals(name)) {
                height = parseDimension(ctx, value);
            } else if ("layout_weight".equals(name)) {
                try { weight = Float.parseFloat(value); } catch (Exception e) {}
            } else if ("text".equals(name) && view instanceof TextView) {
                ((TextView) view).setText(value);
            } else if ("textSize".equals(name) && view instanceof TextView) {
                float sp = parseSpValue(value);
                if (sp > 0) ((TextView) view).setTextSize(sp);
            } else if ("textColor".equals(name) && view instanceof TextView) {
                int color = parseColor(value);
                if (color != 0) ((TextView) view).setTextColor(color);
            } else if ("orientation".equals(name) && view instanceof LinearLayout) {
                if ("vertical".equals(value) || "1".equals(value))
                    ((LinearLayout) view).setOrientation(LinearLayout.VERTICAL);
                else
                    ((LinearLayout) view).setOrientation(LinearLayout.HORIZONTAL);
            } else if ("padding".equals(name)) {
                int px = parsePxValue(ctx, value);
                view.setPadding(px, px, px, px);
            } else if ("paddingBottom".equals(name) && view instanceof TextView) {
                int px = parsePxValue(ctx, value);
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), px);
            } else if ("background".equals(name)) {
                int color = parseColor(value);
                if (color != 0) view.setBackgroundColor(color);
            } else if ("gravity".equals(name)) {
                int grav = parseGravity(value);
                if (view instanceof LinearLayout) ((LinearLayout)view).setGravity(grav);
                if (view instanceof TextView) ((TextView)view).setGravity(grav);
            }
        }

        // Set LayoutParams
        if (view.getParent() == null) {
            // Will be set when added to parent, use tag to pass weight
            if (weight > 0) {
                view.setTag(new float[]{weight, width, height});
            }
        }
        // For LinearLayout children
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height, weight);
        view.setLayoutParams(lp);
    }

    static int parseDimension(Context ctx, String value) {
        if (value == null) return ViewGroup.LayoutParams.WRAP_CONTENT;
        if ("-1".equals(value) || "fill_parent".equals(value) || "match_parent".equals(value))
            return ViewGroup.LayoutParams.MATCH_PARENT;
        if ("-2".equals(value) || "wrap_content".equals(value))
            return ViewGroup.LayoutParams.WRAP_CONTENT;
        return parsePxValue(ctx, value);
    }

    static int parsePxValue(Context ctx, String value) {
        if (value == null) return 0;
        try {
            if (value.endsWith("dp") || value.endsWith("dip")) {
                float dp = Float.parseFloat(value.replace("dp", "").replace("dip", ""));
                return (int)(dp * ctx.getResources().getDisplayMetrics().density);
            }
            if (value.endsWith("sp")) {
                float sp = Float.parseFloat(value.replace("sp", ""));
                return (int)(sp * ctx.getResources().getDisplayMetrics().scaledDensity);
            }
            if (value.endsWith("px")) return Integer.parseInt(value.replace("px", ""));
            return Integer.parseInt(value);
        } catch (Exception e) { return 0; }
    }

    static float parseSpValue(String value) {
        if (value == null) return 0;
        try {
            if (value.endsWith("sp")) return Float.parseFloat(value.replace("sp", ""));
            return Float.parseFloat(value);
        } catch (Exception e) { return 0; }
    }

    static int parseColor(String value) {
        if (value == null) return 0;
        try {
            if (value.startsWith("#")) {
                if (value.length() == 7) return (int)(Long.parseLong("FF" + value.substring(1), 16));
                if (value.length() == 9) return (int)(Long.parseLong(value.substring(1), 16));
            }
            return Integer.parseInt(value);
        } catch (Exception e) { return 0; }
    }

    static int parseGravity(String value) {
        if (value == null) return Gravity.NO_GRAVITY;
        if ("center".equals(value)) return Gravity.CENTER;
        if ("center_horizontal".equals(value)) return Gravity.CENTER_HORIZONTAL;
        if ("center_vertical".equals(value)) return Gravity.CENTER_VERTICAL;
        try { return Integer.parseInt(value); } catch (Exception e) {}
        return Gravity.NO_GRAVITY;
    }

    static int dp(Context ctx, int dp) {
        return (int)(dp * ctx.getResources().getDisplayMetrics().density);
    }

    static TextView label(Context ctx, String text, int sp, int color) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextSize(sp);
        tv.setTextColor(color);
        tv.setPadding(0, dp(ctx,4), 0, dp(ctx,2));
        return tv;
    }

    static TextView boldLabel(Context ctx, String text, int sp, int color) {
        TextView tv = label(ctx, text, sp, color);
        tv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        return tv;
    }
    // Placeholder for testing calculator APK layout
    public static View testCalcLayout(Context ctx) {
        LinearLayout result = new LinearLayout(ctx);
        result.setOrientation(LinearLayout.VERTICAL);
        result.setPadding(dp(ctx,16), dp(ctx,16), dp(ctx,16), dp(ctx,16));
        result.setBackgroundColor(0xFFFFFFFF);

        result.addView(boldLabel(ctx, "Calculator APK Layout Test", 22, 0xFF212121));

        try {
            java.io.InputStream is = ctx.getAssets().open("calc_layout.axml");
            byte[] data = new byte[is.available()];
            is.read(data);
            is.close();

            result.addView(label(ctx, "AXML: " + data.length + " bytes (from ExactCalculator.apk)", 14, 0xFF757575));

            android.content.res.BinaryXmlParser parser = new android.content.res.BinaryXmlParser(data);
            View inflated = inflateFromParser(ctx, parser);

            if (inflated != null) {
                result.addView(boldLabel(ctx, "✅ Inflated: " + inflated.getClass().getSimpleName(), 18, 0xFF4CAF50));
                if (inflated instanceof ViewGroup) {
                    dumpViewTree(ctx, result, (ViewGroup) inflated, 0);
                }
                View divider = new View(ctx);
                divider.setBackgroundColor(0xFFE0E0E0);
                LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx,2));
                dlp.setMargins(0, dp(ctx,12), 0, dp(ctx,12));
                divider.setLayoutParams(dlp);
                result.addView(divider);
                result.addView(boldLabel(ctx, "Rendered:", 16, 0xFF212121));
                if (inflated.getParent() != null)
                    ((ViewGroup) inflated.getParent()).removeView(inflated);
                result.addView(inflated);
            } else {
                result.addView(boldLabel(ctx, "❌ null", 18, 0xFFFF0000));
            }
        } catch (Exception e) {
            result.addView(boldLabel(ctx, "❌ " + e.getClass().getSimpleName(), 16, 0xFFFF0000));
            result.addView(label(ctx, "" + e.getMessage(), 12, 0xFFFF0000));
        }

        Button back = new Button(ctx);
        back.setText("← Back to Menu");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { MockApp.showMenu(); }
        });
        result.addView(back);
        return result;
    }

    static void dumpViewTree(Context ctx, LinearLayout result, ViewGroup vg, int depth) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            String indent = "";
            for (int j = 0; j < depth; j++) indent += "  ";
            String desc = indent + child.getClass().getSimpleName();
            if (child instanceof TextView) desc += " \"" + ((TextView)child).getText() + "\"";
            result.addView(label(ctx, desc, 12, 0xFF424242));
            if (child instanceof ViewGroup) dumpViewTree(ctx, result, (ViewGroup)child, depth+1);
        }
    }

    public static View testApkLayout(Context ctx, String assetName, String title) {
        LinearLayout result = new LinearLayout(ctx);
        result.setOrientation(LinearLayout.VERTICAL);
        result.setPadding(dp(ctx,16), dp(ctx,16), dp(ctx,16), dp(ctx,16));
        result.setBackgroundColor(0xFFFFFFFF);

        result.addView(boldLabel(ctx, title + " Layout Inflation", 22, 0xFF212121));

        try {
            java.io.InputStream is = ctx.getAssets().open(assetName);
            byte[] data = new byte[is.available()];
            is.read(data);
            is.close();

            result.addView(label(ctx, "AXML: " + data.length + " bytes", 14, 0xFF757575));

            android.content.res.BinaryXmlParser parser = new android.content.res.BinaryXmlParser(data);
            View inflated = inflateFromParser(ctx, parser);

            if (inflated != null) {
                result.addView(boldLabel(ctx, "\u2705 Inflated: " + inflated.getClass().getSimpleName(), 18, 0xFF4CAF50));
                if (inflated instanceof ViewGroup) {
                    int total = countViews((ViewGroup) inflated);
                    result.addView(label(ctx, "Total views: " + total, 14, 0xFF757575));
                    dumpViewTree(ctx, result, (ViewGroup) inflated, 0);
                }
                View divider = new View(ctx);
                divider.setBackgroundColor(0xFFE0E0E0);
                LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx,2));
                dlp.setMargins(0, dp(ctx,12), 0, dp(ctx,12));
                divider.setLayoutParams(dlp);
                result.addView(divider);
                result.addView(boldLabel(ctx, "Rendered View:", 16, 0xFF212121));
                if (inflated.getParent() != null)
                    ((ViewGroup) inflated.getParent()).removeView(inflated);
                result.addView(inflated);
            } else {
                result.addView(boldLabel(ctx, "\u274C null", 18, 0xFFFF0000));
            }
        } catch (Exception e) {
            result.addView(boldLabel(ctx, "\u274C " + e.getClass().getSimpleName(), 16, 0xFFFF0000));
            result.addView(label(ctx, "" + e.getMessage(), 12, 0xFFFF0000));
        }

        Button back = new Button(ctx);
        back.setText("\u2190 Back to Menu");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { MockApp.showMenu(); }
        });
        result.addView(back);

        // Wrap in ScrollView
        ScrollView scroll = new ScrollView(ctx);
        scroll.addView(result);
        return scroll;
    }

    static int countViews(ViewGroup vg) {
        int count = vg.getChildCount();
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            if (child instanceof ViewGroup) count += countViews((ViewGroup)child);
        }
        return count;
    }
}
