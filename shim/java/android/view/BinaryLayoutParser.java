package android.view;

import android.content.Context;
import android.content.res.ResourceTable;
import android.content.res.Resources;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses Android's binary layout XML (AXML format inside APK res/layout/*.xml)
 * and instantiates a View hierarchy.
 *
 * Layout XMLs use the same AXML binary format as AndroidManifest.xml.
 * Elements map to View class names (e.g. "LinearLayout" → android.widget.LinearLayout).
 * Attributes map to View property setters.
 */
public class BinaryLayoutParser {

    // AXML chunk types
    private static final int CHUNK_AXML_FILE    = 0x00080003;
    private static final int CHUNK_STRING_POOL   = 0x001C0001;
    private static final int CHUNK_RESOURCE_IDS  = 0x00080180;
    private static final int CHUNK_START_ELEMENT = 0x00100102;
    private static final int CHUNK_END_ELEMENT   = 0x00100103;
    private static final int CHUNK_START_NS      = 0x00100100;
    private static final int CHUNK_END_NS        = 0x00100101;

    // Common Android attribute resource IDs
    private static final int ATTR_ID             = 0x010100d0;
    private static final int ATTR_LAYOUT_WIDTH   = 0x010100f4;
    private static final int ATTR_LAYOUT_HEIGHT  = 0x010100f5;
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
    private static final int ATTR_HINT           = 0x01010043;
    private static final int ATTR_MAX_LINES      = 0x01010153;
    private static final int ATTR_INPUT_TYPE     = 0x01010220;
    private static final int ATTR_SRC            = 0x01010119;
    private static final int ATTR_SCALE_TYPE     = 0x0101011a;
    private static final int ATTR_CLICKABLE      = 0x010100e5;
    private static final int ATTR_LAYOUT_WEIGHT  = 0x01010181;
    private static final int ATTR_LAYOUT_GRAVITY = 0x010100b3;
    private static final int ATTR_LAYOUT_MARGIN  = 0x010100f6;
    private static final int ATTR_SINGLE_LINE    = 0x0101015d;
    private static final int ATTR_CHECKED        = 0x01010108;
    private static final int ATTR_MAX            = 0x01010136;
    private static final int ATTR_PROGRESS       = 0x01010137;

    // Typed value types
    private static final int TYPE_REFERENCE = 0x01;
    private static final int TYPE_STRING    = 0x03;
    private static final int TYPE_FLOAT     = 0x04;
    private static final int TYPE_DIMENSION = 0x05;
    private static final int TYPE_INT_DEC   = 0x10;
    private static final int TYPE_INT_HEX   = 0x11;
    private static final int TYPE_INT_BOOL  = 0x12;
    private static final int TYPE_INT_COLOR_ARGB8 = 0x1c;

    // View class name mappings (short name → full class)
    private static final Map<String, String> VIEW_CLASS_MAP = new HashMap<>();
    static {
        VIEW_CLASS_MAP.put("View", "android.view.View");
        VIEW_CLASS_MAP.put("ViewGroup", "android.view.ViewGroup");
        VIEW_CLASS_MAP.put("FrameLayout", "android.widget.FrameLayout");
        VIEW_CLASS_MAP.put("LinearLayout", "android.widget.LinearLayout");
        VIEW_CLASS_MAP.put("RelativeLayout", "android.widget.FrameLayout"); // approximate
        VIEW_CLASS_MAP.put("ConstraintLayout", "android.widget.FrameLayout"); // approximate
        VIEW_CLASS_MAP.put("TextView", "android.widget.TextView");
        VIEW_CLASS_MAP.put("EditText", "android.widget.TextView"); // EditText extends TextView
        VIEW_CLASS_MAP.put("Button", "android.widget.Button");
        VIEW_CLASS_MAP.put("ImageView", "android.widget.ImageView");
        VIEW_CLASS_MAP.put("ImageButton", "android.widget.ImageView"); // approximate
        VIEW_CLASS_MAP.put("CheckBox", "android.widget.CheckBox");
        VIEW_CLASS_MAP.put("ProgressBar", "android.widget.ProgressBar");
        VIEW_CLASS_MAP.put("ScrollView", "android.widget.FrameLayout"); // approximate
        VIEW_CLASS_MAP.put("HorizontalScrollView", "android.widget.FrameLayout");
        VIEW_CLASS_MAP.put("ListView", "android.widget.FrameLayout");
        VIEW_CLASS_MAP.put("RecyclerView", "android.widget.FrameLayout");
        VIEW_CLASS_MAP.put("WebView", "android.webkit.WebView");
        VIEW_CLASS_MAP.put("Space", "android.view.View");
    }

    private String[] stringPool;
    private int[] resourceIds;
    private final Context mContext;
    private final Resources mResources;

    public BinaryLayoutParser(Context context) {
        mContext = context;
        mResources = (context != null) ? context.getResources() : null;
    }

    /**
     * Parse a binary layout XML and return the root View.
     */
    public View parse(byte[] data) {
        if (data == null || data.length < 8) return null;

        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int magic = buf.getInt();
        if (magic != CHUNK_AXML_FILE) return null;
        int fileSize = buf.getInt();

        // Parse string pool and resource IDs first
        // Use a two-pass approach: first parse pools, then parse elements
        int savedPos = buf.position();
        while (buf.position() < data.length && buf.remaining() >= 8) {
            int chunkStart = buf.position();
            int chunkType = buf.getInt();
            int chunkSize = buf.getInt();
            if (chunkSize < 8 || chunkStart + chunkSize > data.length) break;

            if (chunkType == CHUNK_STRING_POOL) {
                parseStringPool(buf, chunkStart, chunkSize);
            } else if (chunkType == CHUNK_RESOURCE_IDS) {
                int count = (chunkSize - 8) / 4;
                resourceIds = new int[count];
                for (int i = 0; i < count; i++) {
                    resourceIds[i] = buf.getInt();
                }
            }
            buf.position(chunkStart + chunkSize);
        }

        // Second pass: build view tree
        buf.position(savedPos);
        View rootView = null;
        java.util.ArrayDeque<ViewGroup> parentStack = new java.util.ArrayDeque<>();

        while (buf.position() < data.length && buf.remaining() >= 8) {
            int chunkStart = buf.position();
            int chunkType = buf.getInt();
            int chunkSize = buf.getInt();
            if (chunkSize < 8 || chunkStart + chunkSize > data.length) break;

            switch (chunkType) {
                case CHUNK_START_ELEMENT: {
                    int line = buf.getInt();
                    int comment = buf.getInt();
                    int ns = buf.getInt();
                    int name = buf.getInt();
                    int attrStart = buf.getShort() & 0xFFFF;
                    int attrSize = buf.getShort() & 0xFFFF;
                    int attrCount = buf.getShort() & 0xFFFF;
                    buf.getShort(); // idIndex
                    buf.getShort(); // classIndex
                    buf.getShort(); // styleIndex

                    String elemName = getString(name);
                    View view = createView(elemName);

                    // Parse and apply attributes
                    for (int i = 0; i < attrCount; i++) {
                        int attrNs = buf.getInt();
                        int attrName = buf.getInt();
                        int attrRawValue = buf.getInt();
                        buf.getShort(); // typedValueSize
                        buf.get(); // res0
                        int attrType = buf.get() & 0xFF;
                        int attrData = buf.getInt();

                        String attrValueStr = (attrRawValue >= 0) ? getString(attrRawValue) : null;
                        int resId = getResourceId(attrName);
                        applyAttribute(view, resId, getString(attrName), attrType, attrData, attrValueStr);
                    }

                    // Add to parent or set as root
                    if (!parentStack.isEmpty()) {
                        parentStack.peek().addView(view);
                    } else {
                        rootView = view;
                    }

                    // Push onto stack if it's a ViewGroup
                    if (view instanceof ViewGroup) {
                        parentStack.push((ViewGroup) view);
                    }
                    break;
                }

                case CHUNK_END_ELEMENT: {
                    buf.getInt(); // line
                    buf.getInt(); // comment
                    buf.getInt(); // ns
                    int endName = buf.getInt();

                    // Pop from parent stack if it was a ViewGroup
                    String elemName = getString(endName);
                    if (!parentStack.isEmpty()) {
                        View top = parentStack.peek();
                        String topClass = top.getClass().getSimpleName();
                        // Pop if the closing tag matches a ViewGroup class
                        if (isViewGroup(elemName)) {
                            parentStack.pop();
                        }
                    }
                    break;
                }

                default:
                    break;
            }

            buf.position(chunkStart + chunkSize);
        }

        return rootView;
    }

    private View createView(String name) {
        if (name == null) return new View();

        // Strip package prefix for short name lookup
        String shortName = name;
        if (name.contains(".")) {
            shortName = name.substring(name.lastIndexOf('.') + 1);
        }

        // Look up full class name
        String fullName = VIEW_CLASS_MAP.get(shortName);
        if (fullName == null && name.contains(".")) {
            fullName = name; // already fully qualified
        }
        if (fullName == null) {
            fullName = "android.view.View"; // fallback
        }

        try {
            Class<?> cls = Class.forName(fullName);
            // Try no-arg constructor first
            return (View) cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // Try Context constructor
            try {
                Class<?> cls = Class.forName(fullName);
                return (View) cls.getDeclaredConstructor(Context.class).newInstance(mContext);
            } catch (Exception e2) {
                return new View();
            }
        }
    }

    private boolean isViewGroup(String elemName) {
        if (elemName == null) return false;
        String shortName = elemName.contains(".") ?
                elemName.substring(elemName.lastIndexOf('.') + 1) : elemName;
        String fullName = VIEW_CLASS_MAP.get(shortName);
        if (fullName == null && elemName.contains(".")) fullName = elemName;
        if (fullName == null) return false;
        try {
            return ViewGroup.class.isAssignableFrom(Class.forName(fullName));
        } catch (Exception e) {
            return false;
        }
    }

    private void applyAttribute(View view, int resId, String attrName, int type, int data, String rawValue) {
        // Resolve string references (@string/xxx)
        String stringValue = rawValue;
        if (type == TYPE_REFERENCE && mResources != null && data != 0) {
            String resolved = mResources.getString(data);
            if (resolved != null && !resolved.startsWith("string_")) {
                stringValue = resolved;
            }
        }

        switch (resId) {
            case ATTR_ID:
                view.setId(data);
                break;

            case ATTR_LAYOUT_WIDTH:
                // -1 = MATCH_PARENT, -2 = WRAP_CONTENT, else pixels
                break; // stored in LayoutParams, handled by parent

            case ATTR_LAYOUT_HEIGHT:
                break;

            case ATTR_TEXT:
                if (view instanceof android.widget.TextView) {
                    String text = stringValue;
                    if (text == null && type == TYPE_REFERENCE && mResources != null) {
                        text = mResources.getString(data);
                    }
                    if (text != null) {
                        ((android.widget.TextView) view).setText(text);
                    }
                }
                break;

            case ATTR_TEXT_SIZE:
                if (view instanceof android.widget.TextView) {
                    float size = (type == TYPE_DIMENSION) ? decodeDimension(data) :
                                 (type == TYPE_FLOAT) ? Float.intBitsToFloat(data) : data;
                    if (size > 0) ((android.widget.TextView) view).setTextSize(size);
                }
                break;

            case ATTR_TEXT_COLOR:
                if (view instanceof android.widget.TextView) {
                    int color = (type == TYPE_REFERENCE && mResources != null) ?
                            mResources.getColor(data) : data;
                    ((android.widget.TextView) view).setTextColor(color);
                }
                break;

            case ATTR_HINT:
                if (view instanceof android.widget.TextView && stringValue != null) {
                    ((android.widget.TextView) view).setHint(stringValue);
                }
                break;

            case ATTR_MAX_LINES:
                if (view instanceof android.widget.TextView) {
                    ((android.widget.TextView) view).setMaxLines(data);
                }
                break;

            case ATTR_SINGLE_LINE:
                if (view instanceof android.widget.TextView) {
                    ((android.widget.TextView) view).setSingleLine(data != 0);
                }
                break;

            case ATTR_INPUT_TYPE:
                if (view instanceof android.widget.TextView) {
                    ((android.widget.TextView) view).setInputType(data);
                }
                break;

            case ATTR_BACKGROUND:
                if (type == TYPE_INT_COLOR_ARGB8 || type == TYPE_INT_HEX) {
                    view.setBackgroundColor(data);
                }
                break;

            case ATTR_ORIENTATION:
                if (view instanceof android.widget.LinearLayout) {
                    ((android.widget.LinearLayout) view).setOrientation(data);
                }
                break;

            case ATTR_GRAVITY:
                if (view instanceof android.widget.TextView) {
                    ((android.widget.TextView) view).setGravity(data);
                }
                break;

            case ATTR_PADDING:
                int px = (type == TYPE_DIMENSION) ? (int) decodeDimension(data) : data;
                view.setPadding(px, px, px, px);
                break;

            case ATTR_PADDING_LEFT:
                view.setPadding((type == TYPE_DIMENSION) ? (int) decodeDimension(data) : data,
                        view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                break;

            case ATTR_PADDING_TOP:
                view.setPadding(view.getPaddingLeft(),
                        (type == TYPE_DIMENSION) ? (int) decodeDimension(data) : data,
                        view.getPaddingRight(), view.getPaddingBottom());
                break;

            case ATTR_PADDING_RIGHT:
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(),
                        (type == TYPE_DIMENSION) ? (int) decodeDimension(data) : data,
                        view.getPaddingBottom());
                break;

            case ATTR_PADDING_BOTTOM:
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(),
                        view.getPaddingRight(),
                        (type == TYPE_DIMENSION) ? (int) decodeDimension(data) : data);
                break;

            case ATTR_VISIBILITY:
                view.setVisibility(data);
                break;

            case ATTR_CLICKABLE:
                view.setClickable(data != 0);
                break;

            case ATTR_CHECKED:
                if (view instanceof android.widget.CheckBox) {
                    ((android.widget.CheckBox) view).setChecked(data != 0);
                }
                break;

            case ATTR_MAX:
                if (view instanceof android.widget.ProgressBar) {
                    ((android.widget.ProgressBar) view).setMax(data);
                }
                break;

            case ATTR_PROGRESS:
                if (view instanceof android.widget.ProgressBar) {
                    ((android.widget.ProgressBar) view).setProgress(data);
                }
                break;

            default:
                // Try matching by attribute name string for non-standard attrs
                if (attrName != null) {
                    applyByName(view, attrName, type, data, stringValue);
                }
                break;
        }
    }

    private void applyByName(View view, String attrName, int type, int data, String stringValue) {
        if ("text".equals(attrName) && view instanceof android.widget.TextView) {
            if (stringValue != null) ((android.widget.TextView) view).setText(stringValue);
        } else if ("textSize".equals(attrName) && view instanceof android.widget.TextView) {
            float size = (type == TYPE_DIMENSION) ? decodeDimension(data) :
                         (type == TYPE_FLOAT) ? Float.intBitsToFloat(data) : data;
            if (size > 0) ((android.widget.TextView) view).setTextSize(size);
        } else if ("textColor".equals(attrName) && view instanceof android.widget.TextView) {
            ((android.widget.TextView) view).setTextColor(data);
        } else if ("orientation".equals(attrName) && view instanceof android.widget.LinearLayout) {
            ((android.widget.LinearLayout) view).setOrientation(data);
        }
    }

    /**
     * Decode a dimension value from its packed representation.
     * Format: (value << 8) | (units << 4) | (radix << 0)
     * Units: 0=px, 1=dip, 2=sp, 3=pt, 4=in, 5=mm
     * For simplicity, treat all units as pixels (proper density scaling needs DisplayMetrics).
     */
    private float decodeDimension(int data) {
        // Android encodes dimensions as: mantissa in high 24 bits, radix in bits 4-7, units in bits 0-3
        int unitType = (data) & 0x0F;
        int radixType = (data >> 4) & 0x03;
        int mantissa = data >> 8;

        float value;
        switch (radixType) {
            case 0: value = mantissa; break;                    // 23p0
            case 1: value = mantissa / 128.0f; break;           // 16p7
            case 2: value = mantissa / 32768.0f; break;         // 8p15
            case 3: value = mantissa / 8388608.0f; break;       // 0p23
            default: value = mantissa;
        }

        // Simple unit conversion (no density scaling)
        return value;
    }

    // ── String pool parsing (same as BinaryXmlParser) ──

    private void parseStringPool(ByteBuffer buf, int chunkStart, int chunkSize) {
        int stringCount = buf.getInt();
        int styleCount = buf.getInt();
        int flags = buf.getInt();
        int stringsStart = buf.getInt();
        int stylesStart = buf.getInt();
        boolean isUtf8 = (flags & (1 << 8)) != 0;

        int[] offsets = new int[stringCount];
        for (int i = 0; i < stringCount; i++) offsets[i] = buf.getInt();
        for (int i = 0; i < styleCount; i++) buf.getInt(); // skip styles

        int dataStart = chunkStart + 8 + stringsStart;
        stringPool = new String[stringCount];
        for (int i = 0; i < stringCount; i++) {
            int pos = dataStart + offsets[i];
            if (pos < 0 || pos >= buf.limit()) { stringPool[i] = ""; continue; }
            buf.position(pos);
            try {
                stringPool[i] = isUtf8 ? readUtf8(buf) : readUtf16(buf);
            } catch (Exception e) { stringPool[i] = ""; }
        }
    }

    private String readUtf8(ByteBuffer buf) {
        int charLen = buf.get() & 0xFF;
        if ((charLen & 0x80) != 0) charLen = ((charLen & 0x7F) << 8) | (buf.get() & 0xFF);
        int byteLen = buf.get() & 0xFF;
        if ((byteLen & 0x80) != 0) byteLen = ((byteLen & 0x7F) << 8) | (buf.get() & 0xFF);
        if (byteLen < 0 || buf.remaining() < byteLen) return "";
        byte[] b = new byte[byteLen];
        buf.get(b);
        try { return new String(b, "UTF-8"); } catch (Exception e) { return ""; }
    }

    private String readUtf16(ByteBuffer buf) {
        int charLen = buf.getShort() & 0xFFFF;
        if ((charLen & 0x8000) != 0) charLen = ((charLen & 0x7FFF) << 16) | (buf.getShort() & 0xFFFF);
        if (charLen < 0 || buf.remaining() < charLen * 2) return "";
        char[] c = new char[charLen];
        for (int j = 0; j < charLen; j++) c[j] = (char) (buf.getShort() & 0xFFFF);
        return new String(c);
    }

    private String getString(int index) {
        if (stringPool == null || index < 0 || index >= stringPool.length) return null;
        return stringPool[index];
    }

    private int getResourceId(int index) {
        if (resourceIds == null || index < 0 || index >= resourceIds.length) return 0;
        return resourceIds[index];
    }
}
