package android.content.res;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import java.util.HashMap;

/**
 * Shim: android.content.res.Resources — Phase 1
 *
 * Returns sensible defaults for all resource lookups so apps calling
 * getResources().getString() etc. don't crash.  If a ResourceTable has
 * been loaded (from resources.arsc), real values are returned instead.
 *
 * Phase 2 hook: registerStringResource / registerColorResource allow
 * programmatic injection of resource values.
 */
public class Resources {

    private static Resources sSystem;

    /** Returns the shared system Resources instance */
    public static Resources getSystem() {
        if (sSystem == null) sSystem = new Resources();
        return sSystem;
    }

    /** Register a string resource by ID (called by ApkRunner after parsing resources.arsc) */
    public void registerString(int id, String value) {
        mRegistry.put(Integer.valueOf(id), value);
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException() { super(); }
        public NotFoundException(String message) { super(message); }
        public NotFoundException(String message, Throwable cause) { super(message, cause); }
    }

    /** Minimal Theme stub so getColor(int, Theme) / getDrawable(int, Theme) compile. */
    public static class Theme {
        public Theme() {}
        public boolean resolveAttribute(int resid, android.util.TypedValue outValue, boolean resolveRefs) { return false; }
        public int[] getAttributeResolutionStack(int defStyleAttr, int defStyleRes, int explicitStyleRes) { return new int[0]; }
        public TypedArray obtainStyledAttributes(int[] attrs) { return new TypedArray(); }
        public TypedArray obtainStyledAttributes(android.util.AttributeSet set, int[] attrs) { return new TypedArray(); }
        public TypedArray obtainStyledAttributes(android.util.AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes) { return new TypedArray(); }
        public TypedArray obtainStyledAttributes(int resid, int[] attrs) { return new TypedArray(); }
        public void encode(Object encoder) {}
        public int getExplicitStyle(android.util.AttributeSet set) { return 0; }
        public void applyStyle(int resId, boolean force) {}
        public void setTo(Theme other) {}
        public TypedArray resolveAttributes(int[] values, int[] attrs) { return new TypedArray(); }
        public Resources getResources() { return Resources.getSystem(); }
    }

    private final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
    private final Configuration  mConfiguration  = new Configuration();
    private ResourceTable mTable;

    /** Phase 2 registry: manually registered resources keyed by resource ID. */
    private final HashMap<Integer, Object> mRegistry = new HashMap<Integer, Object>();

    /** Layout bytes registry: binary AXML layout data keyed by resource ID. */
    private final HashMap<Integer, byte[]> mLayoutBytesRegistry = new HashMap<Integer, byte[]>();

    public Resources() {}

    // ── ResourceTable integration ────────────────────────────────────────────

    /** Load a parsed ResourceTable for resource ID resolution. */
    public void loadResourceTable(ResourceTable table) {
        mTable = table;
    }

    /** Get the loaded ResourceTable, or null if none loaded. */
    public ResourceTable getResourceTable() {
        return mTable;
    }

    // ── Phase 2 registration hooks ───────────────────────────────────────────

    /** Register a string resource for the given ID (overrides defaults and ResourceTable). */
    public void registerStringResource(int id, String value) {
        mRegistry.put(Integer.valueOf(id), value);
    }

    /** Register a color resource for the given ID (overrides defaults and ResourceTable). */
    public void registerColorResource(int id, int color) {
        mRegistry.put(Integer.valueOf(id), Integer.valueOf(color));
    }

    /** Register an integer resource for the given ID. */
    public void registerIntegerResource(int id, int value) {
        mRegistry.put(Integer.valueOf(id), Integer.valueOf(value));
    }

    // ── Layout bytes registry ───────────────────────────────────────────────

    /** Register binary AXML layout data for the given resource ID. */
    public void registerLayoutBytes(int id, byte[] axml) {
        mLayoutBytesRegistry.put(Integer.valueOf(id), axml);
    }

    /** Get registered binary AXML layout data, or null if none registered. */
    public byte[] getLayoutBytes(int id) {
        return mLayoutBytesRegistry.get(Integer.valueOf(id));
    }

    // ── String resources ─────────────────────────────────────────────────────

    public String getString(int id) {
        // Check registry first
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof String) return (String) reg;
        // Then ResourceTable
        if (mTable != null) {
            String s = mTable.getString(id);
            if (s != null) return s;
        }
        return "string_" + id;
    }

    public String getString(int id, Object... formatArgs) {
        // Use SimpleFormatter: pure-Java String.format replacement
        // that doesn't need ICU/Locale natives (KitKat Dalvik compat)
        String template = getString(id);
        if (formatArgs == null || formatArgs.length == 0) return template;
        return android.text.format.SimpleFormatter.format(template, formatArgs);
    }

    public CharSequence getText(int id) {
        return getString(id);
    }

    public String[] getStringArray(int id) {
        return new String[0];
    }

    public CharSequence[] getTextArray(int id) {
        return new CharSequence[0];
    }

    // ── Color resources ──────────────────────────────────────────────────────

    public int getColor(int id) {
        // Check registry
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) return ((Integer) reg).intValue();
        // Then ResourceTable
        if (mTable != null) {
            return mTable.getInteger(id, 0xFF000000);
        }
        return 0xFF000000;
    }

    public int getColor(int id, Theme theme) {
        return getColor(id);
    }

    public ColorStateList getColorStateList(int id) {
        return ColorStateList.valueOf(getColor(id));
    }

    public ColorStateList getColorStateList(int id, Theme theme) {
        return getColorStateList(id);
    }

    // ── Integer / boolean resources ──────────────────────────────────────────

    public int getInteger(int id) {
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) return ((Integer) reg).intValue();
        if (mTable != null) {
            return mTable.getInteger(id, 0);
        }
        return 0;
    }

    public boolean getBoolean(int id) {
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) return ((Integer) reg).intValue() != 0;
        if (mTable != null) {
            return mTable.getInteger(id, 0) != 0;
        }
        return false;
    }

    // ── Dimension resources ──────────────────────────────────────────────────

    public float getDimension(int id) {
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) return (float) ((Integer) reg).intValue();
        if (mTable != null) {
            return (float) mTable.getInteger(id, 0);
        }
        return 0f;
    }

    public int getDimensionPixelSize(int id) {
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) return ((Integer) reg).intValue();
        if (mTable != null) {
            return mTable.getInteger(id, 0);
        }
        return 0;
    }

    public int getDimensionPixelOffset(int id) {
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) return ((Integer) reg).intValue();
        if (mTable != null) {
            return mTable.getInteger(id, 0);
        }
        return 0;
    }

    // ── Drawable resources ───────────────────────────────────────────────────

    public Drawable getDrawable(int id) {
        return new ColorDrawable(0xFFCCCCCC);
    }

    public Drawable getDrawable(int id, Theme theme) {
        return getDrawable(id);
    }

    // ── Layout resources ─────────────────────────────────────────────────────

    public XmlResourceParser getLayout(int id) {
        return null; // B9 will handle this
    }

    // ── Array resources ──────────────────────────────────────────────────────

    public int[] getIntArray(int id) {
        return new int[0];
    }

    // ── Resource ID lookup ───────────────────────────────────────────────────

    public int getIdentifier(String name, String defType, String defPackage) {
        return 0;
    }

    /**
     * Get a resource name by ID (e.g., "string/app_name").
     * Returns a synthetic name if no ResourceTable is loaded.
     */
    public String getResourceName(int id) {
        if (mTable != null) {
            String name = mTable.getResourceName(id);
            if (name != null) return name;
        }
        return "res/0x" + Integer.toHexString(id);
    }

    public String getResourceEntryName(int id) {
        if (mTable != null) {
            String name = mTable.getResourceName(id);
            if (name != null) {
                int slash = name.indexOf('/');
                if (slash >= 0 && slash + 1 < name.length()) {
                    return name.substring(slash + 1);
                }
                return name;
            }
        }
        return "entry_" + Integer.toHexString(id);
    }

    public String getResourceTypeName(int id) {
        if (mTable != null) {
            String name = mTable.getResourceName(id);
            if (name != null) {
                int slash = name.indexOf('/');
                if (slash > 0) {
                    return name.substring(0, slash);
                }
            }
        }
        // Derive type from the TT byte of the resource ID: 0xPPTTEEEE
        int typeId = (id >> 16) & 0xFF;
        switch (typeId) {
            case 0x01: return "attr";
            case 0x02: return "drawable";
            case 0x03: return "layout";
            case 0x04: return "anim";
            case 0x05: return "xml";
            case 0x06: return "raw";
            case 0x07: return "array";
            case 0x08: return "string";
            case 0x09: return "bool";
            case 0x0a: return "integer";
            case 0x0b: return "color";
            case 0x0c: return "dimen";
            case 0x0d: return "style";
            case 0x0e: return "id";
            default:   return "unknown";
        }
    }

    // ── Display / Configuration ──────────────────────────────────────────────

    public DisplayMetrics getDisplayMetrics() {
        return mDisplayMetrics;
    }

    public Configuration getConfiguration() {
        return mConfiguration;
    }

    // Additional methods needed by AOSP View/TextView
    public static final int ID_NULL = 0;
    public static int getAttributeSetSourceResId(Object attrs) { return 0; }
    public CompatibilityInfo getCompatibilityInfo() { return new CompatibilityInfo(); }
    public String getResourcePackageName(int resId) { return ""; }
    public android.content.res.XmlResourceParser getXml(int id) { return null; }
    public static boolean resourceHasPackage(int resId) { return (resId >>> 24) != 0; }
    public TypedArray obtainTypedArray(int id) { return new TypedArray(); }
    public android.graphics.Typeface getFont(int id) { return null; }
    public void parseBundleExtras(android.content.res.XmlResourceParser parser, android.os.Bundle outBundle) {}

    /** Create a new Theme instance. */
    public Theme newTheme() { return new Theme(); }

    /** Select a default theme resource ID given a target SDK version. */
    public static int selectDefaultTheme(int curTheme, int targetSdkVersion) { return curTheme; }

    /** Return an AssetManager instance (stub returns null). */
    public AssetManager getAssets() { return null; }

    /** Stub for AOSP Display.java compilation. */
    public android.view.DisplayAdjustments getDisplayAdjustments() {
        return new android.view.DisplayAdjustments();
    }

    /** Stub for AOSP Display.java compilation. */
    public boolean hasOverrideDisplayAdjustments() { return false; }

    /** Obtain attributes from a set, returning a TypedArray. */
    public TypedArray obtainAttributes(android.util.AttributeSet set, int[] attrs) {
        return new TypedArray(this, attrs != null ? new int[attrs.length] : new int[0], attrs != null ? attrs : new int[0], 0);
    }

    /** Return a DrawableInflater for this Resources. */
    public android.graphics.drawable.DrawableInflater getDrawableInflater() {
        return new android.graphics.drawable.DrawableInflater(this, getClass().getClassLoader());
    }

    public void getValueForDensity(int id, int density, android.util.TypedValue outValue, boolean resolveRefs) {
        // stub: no-op
    }

    public java.io.InputStream openRawResource(int id, android.util.TypedValue value) {
        return null;
    }
}
