package android.view;
import android.content.Context;
import android.content.res.BinaryXmlParser;
import android.content.res.Resources;
import android.content.res.ResourceTable;
import android.util.AttributeSet;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;

/**
 * LayoutInflater — inflates layout resources into View trees.
 *
 * Supports three inflation strategies (tried in order):
 * 1. Programmatic layout registry — apps register ViewFactory builders for resource IDs
 * 2. Binary layout XML from APK — parses AXML format layout files
 * 3. Fallback — returns an empty FrameLayout with the resource ID set
 */
public class LayoutInflater {
    private Context mContext;

    // ── Programmatic layout registry ──────────────────────────────────────
    /**
     * Factory interface for programmatic layout creation.
     * Apps can register View tree builders for specific resource IDs,
     * enabling programmatic UI construction via LayoutInflater.inflate().
     */
    public interface ViewFactory {
        View createView(Context context, ViewGroup parent);
    }

    private static HashMap sLayoutRegistry = new HashMap();

    /**
     * Register a programmatic layout builder for the given resource ID.
     * When inflate() is called with this resource ID, the factory will be
     * invoked instead of parsing binary XML.
     */
    public static void registerLayout(int resourceId, ViewFactory factory) {
        sLayoutRegistry.put(Integer.valueOf(resourceId), factory);
    }

    /**
     * Unregister a previously registered layout factory.
     */
    public static void unregisterLayout(int resourceId) {
        sLayoutRegistry.remove(Integer.valueOf(resourceId));
    }

    /**
     * Check if a programmatic layout is registered for the given resource ID.
     */
    public static boolean hasRegisteredLayout(int resourceId) {
        return sLayoutRegistry.containsKey(Integer.valueOf(resourceId));
    }

    // ── View tag-to-class mapping ─────────────────────────────────────────
    private static final HashMap sTagClassMap = new HashMap();
    static {
        // android.view.* classes
        sTagClassMap.put("View", "android.view.View");
        sTagClassMap.put("ViewGroup", "android.view.ViewGroup");
        sTagClassMap.put("SurfaceView", "android.view.SurfaceView");
        sTagClassMap.put("TextureView", "android.view.TextureView");
        // android.widget.* classes
        sTagClassMap.put("LinearLayout", "android.widget.LinearLayout");
        sTagClassMap.put("RelativeLayout", "android.widget.FrameLayout"); // approximate
        sTagClassMap.put("FrameLayout", "android.widget.FrameLayout");
        sTagClassMap.put("ScrollView", "android.widget.ScrollView");
        sTagClassMap.put("TextView", "android.widget.TextView");
        sTagClassMap.put("Button", "android.widget.Button");
        sTagClassMap.put("EditText", "android.widget.EditText");
        sTagClassMap.put("ImageView", "android.widget.ImageView");
        sTagClassMap.put("CheckBox", "android.widget.CheckBox");
        sTagClassMap.put("RadioButton", "android.widget.RadioButton");
        sTagClassMap.put("Switch", "android.widget.Switch");
        sTagClassMap.put("SeekBar", "android.widget.SeekBar");
        sTagClassMap.put("ProgressBar", "android.widget.ProgressBar");
        sTagClassMap.put("ListView", "android.widget.ListView");
        sTagClassMap.put("Spinner", "android.widget.Spinner");
        sTagClassMap.put("WebView", "android.webkit.WebView");
        // Placeholder tags (no-op)
        sTagClassMap.put("include", null);
        sTagClassMap.put("merge", null);
        sTagClassMap.put("fragment", null);
        sTagClassMap.put("requestFocus", null);
    }

    /**
     * Create a View from an XML tag name.
     * Handles fully-qualified class names ("com.example.MyView"),
     * short Android names ("LinearLayout" -> android.widget.LinearLayout),
     * and special tags ("include", "merge", "fragment" -> null/placeholder).
     */
    public View createViewFromTag(String tagName) {
        if (tagName == null) return null;

        // Special tags that don't produce views
        if ("include".equals(tagName) || "merge".equals(tagName)
                || "fragment".equals(tagName) || "requestFocus".equals(tagName)) {
            return null;
        }

        String fullName = null;

        // Check if it's a fully-qualified name (contains a dot)
        if (tagName.contains(".")) {
            fullName = tagName;
        } else {
            // Look up in the tag map
            Object mapped = sTagClassMap.get(tagName);
            if (mapped != null) {
                fullName = (String) mapped;
            } else {
                // Default: try android.widget.* then android.view.*
                fullName = "android.widget." + tagName;
            }
        }

        // Try to instantiate the view class
        try {
            Class cls = Class.forName(fullName);
            // Try no-arg constructor first
            return (View) cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // Try Context constructor
            try {
                Class cls = Class.forName(fullName);
                return (View) cls.getDeclaredConstructor(Context.class).newInstance(mContext);
            } catch (Exception e2) {
                // For non-widget short names, try android.view.*
                if (!tagName.contains(".") && fullName.startsWith("android.widget.")) {
                    try {
                        Class cls = Class.forName("android.view." + tagName);
                        return (View) cls.getDeclaredConstructor().newInstance();
                    } catch (Exception e3) {
                        // fall through
                    }
                }
            }
        }
        return null;
    }

    // ── Constructor and factory ───────────────────────────────────────────

    public LayoutInflater(Context context) {
        mContext = context;
    }

    public LayoutInflater(LayoutInflater original, Context newContext) {
        mContext = newContext;
    }

    /**
     * Get a LayoutInflater from a Context.
     * This is the standard way apps obtain a LayoutInflater.
     */
    public static LayoutInflater from(Context context) {
        if (context == null) return new LayoutInflater(context);
        Object svc = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (svc instanceof LayoutInflater) {
            LayoutInflater base = (LayoutInflater) svc;
            // Return a clone bound to the caller's context (matches AOSP behavior)
            return base.cloneInContext(context);
        }
        return new LayoutInflater(context);
    }

    public Context getContext() { return mContext; }

    public LayoutInflater cloneInContext(Context newContext) {
        return new LayoutInflater(this, newContext);
    }

    /**
     * Inflate a layout resource.
     */
    public View inflate(int resource, ViewGroup root) {
        return inflate(resource, root, root != null);
    }

    /**
     * Inflate a layout resource with attachToRoot control.
     *
     * Tries four strategies in order:
     * 1. Programmatic layout registry (ViewFactory)
     * 2. Registered layout bytes via Resources.getLayoutBytes() (BinaryLayoutParser)
     * 3. Binary layout XML from APK via ResourceTable (BinaryLayoutParser)
     * 4. Fallback: empty FrameLayout with resource ID
     */
    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        View view = null;

        // 1. Check programmatic layout registry first
        Object factoryObj = sLayoutRegistry.get(Integer.valueOf(resource));
        if (factoryObj != null) {
            ViewFactory factory = (ViewFactory) factoryObj;
            view = factory.createView(mContext, root);
            if (view != null) {
                if (attachToRoot && root != null) {
                    root.addView(view);
                    return root;
                }
                return view;
            }
        }

        // 2. Try to inflate from registered layout bytes
        if (view == null && mContext != null) {
            android.content.res.Resources res = mContext.getResources();
            if (res != null) {
                byte[] layoutBytes = res.getLayoutBytes(resource);
                if (layoutBytes != null && layoutBytes.length > 0) {
                    try {
                        BinaryLayoutParser parser = new BinaryLayoutParser(mContext);
                        view = parser.parse(layoutBytes);
                    } catch (Exception e) {
                        // parse failed, fall through to next strategy
                    }
                }
            }
        }

        // 3. Try to inflate from the real binary layout XML via ResourceTable
        if (view == null && mContext != null) {
            android.content.res.Resources res = mContext.getResources();
            if (res != null) {
                android.content.res.ResourceTable table = res.getResourceTable();
                if (table != null) {
                    String layoutFile = table.getLayoutFileName(resource);
                    if (layoutFile != null) {
                        byte[] xmlData = loadLayoutXml(layoutFile);
                        if (xmlData != null && xmlData.length > 0) {
                            BinaryLayoutParser parser = new BinaryLayoutParser(mContext);
                            view = parser.parse(xmlData);
                        }
                    }
                }
            }
        }

        // 4. Fallback: stub FrameLayout
        if (view == null) {
            view = new FrameLayout(mContext);
        }
        view.setId(resource);

        if (root != null && attachToRoot) {
            root.addView(view);
            return root;
        }
        return view;
    }

    // ── AXML-based inflation via BinaryXmlParser ─────────────────────────

    /**
     * Inflate a layout from an XmlPullParser (typically a BinaryXmlParser
     * wrapping compiled AXML data from an APK).
     */
    public View inflate(XmlPullParser parser, ViewGroup root) {
        return inflate(parser, root, root != null);
    }

    /**
     * Inflate a layout from an XmlPullParser with attachToRoot control.
     * Walks the XML event stream, creating Views for each START_TAG and
     * building the parent-child tree.
     */
    public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        if (parser == null) return root;

        View result = root;
        try {
            // Advance to the first START_TAG
            int type;
            while ((type = parser.next()) != XmlPullParser.START_TAG
                    && type != XmlPullParser.END_DOCUMENT) {
                // skip
            }
            if (type != XmlPullParser.START_TAG) {
                return root; // no elements
            }

            String tagName = parser.getName();
            boolean isMerge = "merge".equals(tagName);

            View rootView = null;
            if (isMerge) {
                if (root == null) {
                    throw new android.view.InflateException(
                            "<merge /> can only be used with a valid ViewGroup root");
                }
                inflateChildren(parser, root);
            } else {
                rootView = createViewFromTag(tagName);
                if (rootView == null) {
                    rootView = new FrameLayout(mContext);
                }

                // Apply attributes from the parser to the root view
                applyXmlAttributes(rootView, parser);

                // Parse LayoutParams from the root element
                ViewGroup.LayoutParams params = null;
                if (root != null) {
                    params = generateLayoutParams(root, parser);
                }

                // Inflate children if this is a ViewGroup
                if (rootView instanceof ViewGroup) {
                    inflateChildren(parser, (ViewGroup) rootView);
                } else {
                    // Skip to matching END_TAG
                    skipToEndTag(parser);
                }

                // Attach to root or set layout params
                if (root != null) {
                    if (attachToRoot) {
                        if (params != null) {
                            root.addView(rootView, params);
                        } else {
                            root.addView(rootView);
                        }
                        result = root;
                    } else {
                        if (params != null) {
                            rootView.setLayoutParams(params);
                        }
                        result = rootView;
                    }
                } else {
                    result = rootView;
                }
            }
        } catch (Exception e) {
            // Inflation failed, return whatever we have
        }
        return result;
    }

    /**
     * Inflate child elements from the parser into the given parent ViewGroup.
     */
    private void inflateChildren(XmlPullParser parser, ViewGroup parent) throws Exception {
        int depth = parser.getDepth();
        int type;

        while (true) {
            type = parser.next();
            if (type == XmlPullParser.END_TAG && parser.getDepth() <= depth) {
                break;
            }
            if (type == XmlPullParser.END_DOCUMENT) {
                break;
            }
            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            String tagName = parser.getName();

            // Handle <include> tag
            if ("include".equals(tagName)) {
                handleInclude(parser, parent);
                continue;
            }

            // Handle <requestFocus> and <fragment>
            if ("requestFocus".equals(tagName) || "fragment".equals(tagName)) {
                skipToEndTag(parser);
                continue;
            }

            // Create the child View
            View child = createViewFromTag(tagName);
            if (child == null) {
                child = new View(mContext);
            }

            // Apply attributes
            applyXmlAttributes(child, parser);

            // Generate layout params from the parent
            ViewGroup.LayoutParams params = generateLayoutParams(parent, parser);

            // Inflate grandchildren if this child is a ViewGroup
            if (child instanceof ViewGroup) {
                inflateChildren(parser, (ViewGroup) child);
            } else {
                skipToEndTag(parser);
            }

            // Add to parent
            if (params != null) {
                parent.addView(child, params);
            } else {
                parent.addView(child);
            }
        }
    }

    /**
     * Handle an <include> tag by inflating the referenced layout.
     */
    private void handleInclude(XmlPullParser parser, ViewGroup parent) throws Exception {
        // Look for android:layout attribute (layout resource reference)
        if (parser instanceof BinaryXmlParser) {
            BinaryXmlParser bxp = (BinaryXmlParser) parser;
            int count = bxp.getAttributeCount();
            for (int i = 0; i < count; i++) {
                String name = bxp.getAttributeName(i);
                if ("layout".equals(name)) {
                    int resId = bxp.getAttributeResourceValue(i, 0);
                    if (resId != 0) {
                        View included = inflate(resId, parent, false);
                        if (included != null) {
                            parent.addView(included);
                        }
                    }
                    break;
                }
            }
        }
        skipToEndTag(parser);
    }

    /**
     * Skip events until we reach the matching END_TAG for the current element.
     */
    private void skipToEndTag(XmlPullParser parser) throws Exception {
        int depth = 1;
        while (depth > 0) {
            int type = parser.next();
            if (type == XmlPullParser.START_TAG) {
                depth++;
            } else if (type == XmlPullParser.END_TAG) {
                depth--;
            } else if (type == XmlPullParser.END_DOCUMENT) {
                break;
            }
        }
    }

    // Common Android attribute resource IDs
    private static final int ATTR_ID             = 0x010100d0;
    private static final int ATTR_TEXT           = 0x01010014;
    private static final int ATTR_TEXT_SIZE      = 0x01010095;
    private static final int ATTR_TEXT_COLOR     = 0x01010098;
    private static final int ATTR_BACKGROUND     = 0x010100d4;
    private static final int ATTR_ORIENTATION    = 0x010100c4;
    private static final int ATTR_GRAVITY        = 0x010100af;
    private static final int ATTR_PADDING        = 0x010100d5;
    private static final int ATTR_PADDING_LEFT   = 0x010100d6;
    private static final int ATTR_PADDING_TOP    = 0x010100d7;
    private static final int ATTR_PADDING_RIGHT  = 0x010100d8;
    private static final int ATTR_PADDING_BOTTOM = 0x010100d9;
    private static final int ATTR_VISIBILITY     = 0x010100dc;
    private static final int ATTR_LAYOUT_WIDTH   = 0x010100f4;
    private static final int ATTR_LAYOUT_HEIGHT  = 0x010100f5;
    private static final int ATTR_LAYOUT_WEIGHT  = 0x01010181;
    private static final int ATTR_HINT           = 0x01010043;
    private static final int ATTR_MAX_LINES      = 0x01010153;
    private static final int ATTR_SINGLE_LINE    = 0x0101015d;
    private static final int ATTR_CLICKABLE      = 0x010100e5;

    /**
     * Apply XML attributes from a BinaryXmlParser to a View.
     */
    private void applyXmlAttributes(View view, XmlPullParser parser) {
        if (!(parser instanceof BinaryXmlParser)) return;
        BinaryXmlParser bxp = (BinaryXmlParser) parser;
        int count = bxp.getAttributeCount();
        if (count < 0) return;

        Resources res = (mContext != null) ? mContext.getResources() : null;

        for (int i = 0; i < count; i++) {
            int resId = bxp.getAttributeNameResource(i);
            String attrName = bxp.getAttributeName(i);
            int attrType = bxp.getAttributeValueType(i);
            int attrData = bxp.getAttributeValueData(i);

            switch (resId) {
                case ATTR_ID:
                    view.setId(attrData);
                    break;

                case ATTR_TEXT:
                    if (view instanceof TextView) {
                        String text = resolveStringAttr(bxp, i, res);
                        if (text != null) {
                            ((TextView) view).setText(text);
                        }
                    }
                    break;

                case ATTR_TEXT_SIZE:
                    if (view instanceof TextView) {
                        float size = bxp.getAttributeFloatValue(i, 0f);
                        if (size > 0) ((TextView) view).setTextSize(size);
                    }
                    break;

                case ATTR_TEXT_COLOR:
                    if (view instanceof TextView) {
                        int color = resolveColorAttr(attrType, attrData, res);
                        ((TextView) view).setTextColor(color);
                    }
                    break;

                case ATTR_HINT:
                    if (view instanceof TextView) {
                        String hint = resolveStringAttr(bxp, i, res);
                        if (hint != null) {
                            ((TextView) view).setHint(hint);
                        }
                    }
                    break;

                case ATTR_MAX_LINES:
                    if (view instanceof TextView) {
                        ((TextView) view).setMaxLines(attrData);
                    }
                    break;

                case ATTR_SINGLE_LINE:
                    if (view instanceof TextView) {
                        ((TextView) view).setSingleLine(attrData != 0);
                    }
                    break;

                case ATTR_BACKGROUND:
                    if (attrType == 0x1c || attrType == 0x1d ||
                        attrType == 0x1e || attrType == 0x1f ||
                        attrType == 0x11) {
                        view.setBackgroundColor(attrData);
                    } else if (attrType == 0x01 && res != null) {
                        // reference to color resource
                        view.setBackgroundColor(res.getColor(attrData));
                    }
                    break;

                case ATTR_ORIENTATION:
                    if (view instanceof LinearLayout) {
                        ((LinearLayout) view).setOrientation(attrData);
                    }
                    break;

                case ATTR_GRAVITY:
                    if (view instanceof TextView) {
                        ((TextView) view).setGravity(attrData);
                    }
                    break;

                case ATTR_PADDING: {
                    int px = bxp.getAttributeIntValue(i, 0);
                    view.setPadding(px, px, px, px);
                    break;
                }

                case ATTR_PADDING_LEFT:
                    view.setPadding(bxp.getAttributeIntValue(i, 0),
                            view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                    break;

                case ATTR_PADDING_TOP:
                    view.setPadding(view.getPaddingLeft(),
                            bxp.getAttributeIntValue(i, 0),
                            view.getPaddingRight(), view.getPaddingBottom());
                    break;

                case ATTR_PADDING_RIGHT:
                    view.setPadding(view.getPaddingLeft(), view.getPaddingTop(),
                            bxp.getAttributeIntValue(i, 0), view.getPaddingBottom());
                    break;

                case ATTR_PADDING_BOTTOM:
                    view.setPadding(view.getPaddingLeft(), view.getPaddingTop(),
                            view.getPaddingRight(), bxp.getAttributeIntValue(i, 0));
                    break;

                case ATTR_VISIBILITY:
                    view.setVisibility(attrData);
                    break;

                case ATTR_CLICKABLE:
                    view.setClickable(attrData != 0);
                    break;

                default:
                    // Fall back to name-based attribute matching
                    if (attrName != null) {
                        applyByName(view, attrName, attrType, attrData,
                                bxp.getAttributeValue(i), res);
                    }
                    break;
            }
        }
    }

    /**
     * Apply attribute by name (fallback when resource ID is unknown).
     */
    private void applyByName(View view, String name, int type, int data,
                              String rawValue, Resources res) {
        if ("text".equals(name) && view instanceof TextView) {
            String text = rawValue;
            if (type == 0x01 && res != null && data != 0) {
                text = res.getString(data);
            }
            if (text != null) ((TextView) view).setText(text);
        } else if ("textSize".equals(name) && view instanceof TextView) {
            float size = (type == 0x04) ? Float.intBitsToFloat(data) : data;
            if (size > 0) ((TextView) view).setTextSize(size);
        } else if ("textColor".equals(name) && view instanceof TextView) {
            ((TextView) view).setTextColor(data);
        } else if ("orientation".equals(name) && view instanceof LinearLayout) {
            ((LinearLayout) view).setOrientation(data);
        }
    }

    /**
     * Resolve a string attribute value, handling both raw strings and @string/ references.
     */
    private String resolveStringAttr(BinaryXmlParser parser, int index, Resources res) {
        int type = parser.getAttributeValueType(index);
        int data = parser.getAttributeValueData(index);

        // If it's a reference to a string resource, resolve it
        if (type == 0x01 && res != null && data != 0) {
            String resolved = res.getString(data);
            if (resolved != null && !resolved.startsWith("string_")) {
                return resolved;
            }
        }

        // Otherwise use the raw string value
        return parser.getAttributeValue(index);
    }

    /**
     * Resolve a color attribute value, handling both inline colors and @color/ references.
     */
    private int resolveColorAttr(int type, int data, Resources res) {
        if (type == 0x01 && res != null && data != 0) {
            return res.getColor(data);
        }
        return data;
    }

    /**
     * Generate LayoutParams for a child from the parser's current attributes.
     * Reads layout_width, layout_height, layout_weight.
     */
    private ViewGroup.LayoutParams generateLayoutParams(ViewGroup parent, XmlPullParser parser) {
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        float weight = 0f;

        if (parser instanceof BinaryXmlParser) {
            BinaryXmlParser bxp = (BinaryXmlParser) parser;
            int count = bxp.getAttributeCount();
            for (int i = 0; i < count; i++) {
                int resId = bxp.getAttributeNameResource(i);
                int data = bxp.getAttributeValueData(i);

                if (resId == ATTR_LAYOUT_WIDTH) {
                    width = resolveLayoutDimension(data);
                } else if (resId == ATTR_LAYOUT_HEIGHT) {
                    height = resolveLayoutDimension(data);
                } else if (resId == ATTR_LAYOUT_WEIGHT) {
                    weight = Float.intBitsToFloat(data);
                }
            }
        }

        if (parent instanceof LinearLayout && weight != 0f) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
            lp.weight = weight;
            return lp;
        }

        return new ViewGroup.LayoutParams(width, height);
    }

    /**
     * Resolve a layout dimension value: MATCH_PARENT (-1), WRAP_CONTENT (-2), or px.
     */
    private int resolveLayoutDimension(int data) {
        if (data == -1 || data == -2) {
            return data; // MATCH_PARENT or WRAP_CONTENT
        }
        // Could be a dimension value; for simple cases, use raw value
        return data;
    }

    /**
     * Load binary layout XML bytes from the extracted APK res/ directory.
     */
    private byte[] loadLayoutXml(String layoutPath) {
        // Try to find the file in the APK's extracted directory
        try {
            android.app.MiniServer server = android.app.MiniServer.get();
            if (server != null) {
                String resDir = null;
                android.app.ApkInfo info = server.getApkInfo();
                if (info != null) resDir = info.resDir;
                if (resDir == null) resDir = info != null ? info.extractDir : null;

                if (resDir != null) {
                    java.io.File xmlFile = new java.io.File(resDir, layoutPath);
                    if (!xmlFile.exists()) {
                        // Try without res/ prefix
                        xmlFile = new java.io.File(resDir, layoutPath.startsWith("res/") ? layoutPath.substring(4) : layoutPath);
                    }
                    if (xmlFile.exists()) {
                        return readFile(xmlFile);
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private static byte[] readFile(java.io.File file) {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            int offset = 0;
            while (offset < data.length) {
                int n = fis.read(data, offset, data.length - offset);
                if (n < 0) break;
                offset += n;
            }
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public View createView(String name, String prefix, AttributeSet attrs) {
        try {
            String fullName = (prefix != null) ? prefix + name : name;
            Class<?> cls = Class.forName(fullName);
            return (View) cls.getConstructor(Context.class).newInstance(mContext);
        } catch (Exception e) {
            return null;
        }
    }

    public View onCreateView(String name, AttributeSet attrs) { return null; }
    public View onCreateView(View parent, String name, AttributeSet attrs) { return null; }

    public Factory getFactory() { return mFactory; }
    public Factory2 getFactory2() { return mFactory2; }
    public Filter getFilter() { return null; }
    public void setFactory(Factory factory) { mFactory = factory; }
    public void setFactory(Object factory) { /* legacy compat */ }
    public void setFactory2(Factory2 factory) { mFactory2 = factory; }
    public void setFactory2(Object factory) { /* legacy compat */ }

    private Factory mFactory;
    private Factory2 mFactory2;

    public interface Factory {
        View onCreateView(String name, Context context, AttributeSet attrs);
    }
    public interface Factory2 extends Factory {
        View onCreateView(View parent, String name, Context context, AttributeSet attrs);
    }
    public void setFilter(Filter filter) {}

    /** Inner Filter interface for allowed class filtering during inflation. */
    public interface Filter {
        boolean onLoadClass(Class clazz);
    }
}
