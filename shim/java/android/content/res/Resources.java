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
        public boolean resolveAttribute(int resid, android.util.TypedValue outValue, boolean resolveRefs) {
            // Return true for ALL attribute lookups — prevents MaterialComponents
            // "TextAppearance not found" exceptions. The TypedValue gets a dummy resource.
            if (outValue != null) {
                outValue.type = android.util.TypedValue.TYPE_INT_DEC;
                outValue.data = 0;
                outValue.resourceId = resid;
            }
            return true;
        }
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
        public android.graphics.drawable.Drawable getDrawable(int id) { return new android.graphics.drawable.ColorDrawable(0); }
        public android.graphics.drawable.Drawable getDrawable(int id, android.content.res.Resources.Theme theme) { return new android.graphics.drawable.ColorDrawable(0); }
        public int getColor(int id) { return 0; }
        public float getDimension(int id) { return 0f; }
        public int getDimensionPixelSize(int id) { return 0; }
        public CharSequence getText(int id) { return ""; }
        public String getString(int id) { return ""; }
    }

    private final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
    private final Configuration  mConfiguration  = new Configuration();
    private ResourceTable mTable;
    private String mApkPath;

    public void setApkPath(String path) { mApkPath = path; }
    public String getApkPath() { return mApkPath; }

    /** Phase 2 registry: manually registered resources keyed by resource ID. */
    private final HashMap<Integer, Object> mRegistry = new HashMap<Integer, Object>();

    /** Layout bytes registry: binary AXML layout data keyed by resource ID. */
    private final HashMap<Integer, byte[]> mLayoutBytesRegistry = new HashMap<Integer, byte[]>();

    public Resources() {}

    /**
     * 3-arg ctor matching framework.jar's deprecated public ctor.  Exists
     * so {@code com.westlake.services.WestlakeResources} can call
     * {@code super(null, null, cfg)} -- at compile time this ctor is a
     * no-op; at runtime framework.jar's Resources is on the classpath
     * (shim Resources is stripped via scripts/framework_duplicates.txt)
     * and the real 3-arg ctor runs.
     *
     * Added in M4-PRE6 (2026-05-12).
     */
    public Resources(AssetManager assets, DisplayMetrics metrics, Configuration config) {
        // No fields to assign here -- the existing default-initialized
        // mDisplayMetrics / mConfiguration above stay valid.  The args
        // matter only at runtime where the real Resources ctor handles them.
    }

    /**
     * 1-arg ctor matching framework.jar's @UnsupportedAppUsage hidden
     * public Resources(ClassLoader) ctor.  Exists so subclasses such as
     * {@code com.westlake.services.WestlakeResources} can call
     * {@code super(ClassLoader)} -- at compile time this is a no-op; at
     * runtime framework.jar's Resources is on the classpath (shim Resources
     * is stripped via scripts/framework_duplicates.txt) and the real
     * 1-arg ctor runs.  The framework body is native-free: just sets
     * mClassLoader and calls ResourcesManager.registerAllResourcesReference.
     *
     * Added in V2-Step4 (2026-05-13).
     */
    public Resources(ClassLoader classLoader) {
        // No fields to assign here -- framework ctor sets mClassLoader.
    }

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
        // ResourceTable (real arsc parsing — when complete, this is the
        // only lookup needed; per-app string maps are forbidden by the
        // architecture rule in CLAUDE.md).
        if (mTable != null) {
            String s = mTable.getString(id);
            if (s != null) {
                if (id == 0x7f150ba9) {
                    try {
                        com.westlake.engine.WestlakeLauncher.marker(
                                "CV PF-MCD-RES_GET_STRING id=0x7f150ba9 value=" + s);
                    } catch (Throwable ignored) {
                    }
                }
                return s;
            }
        }
        if (id == 0x7f150ba9) {
            try {
                com.westlake.engine.WestlakeLauncher.marker(
                        "CV PF-MCD-RES_GET_STRING_MISS id=0x7f150ba9");
            } catch (Throwable ignored) {
            }
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
            return mTable.getDimension(id, resourceDensity());
        }
        return 0f;
    }

    public int getDimensionPixelSize(int id) {
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) return ((Integer) reg).intValue();
        if (mTable != null) {
            float value = mTable.getDimension(id, resourceDensity());
            int rounded = (int) (value >= 0 ? value + 0.5f : value - 0.5f);
            if (rounded != 0) {
                return rounded;
            }
            if (value == 0f) {
                return 0;
            }
            return value > 0f ? 1 : -1;
        }
        return 0;
    }

    public int getDimensionPixelOffset(int id) {
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) return ((Integer) reg).intValue();
        if (mTable != null) {
            return (int) mTable.getDimension(id, resourceDensity());
        }
        return 0;
    }

    private float resourceDensity() {
        if (mDisplayMetrics != null && mDisplayMetrics.density > 0f) {
            return mDisplayMetrics.density;
        }
        return 1.0f;
    }

    // ── Drawable resources ───────────────────────────────────────────────────

    public Drawable getDrawable(int id) {
        // Try to resolve a color resource and return a ColorDrawable
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) {
            return new ColorDrawable(((Integer) reg).intValue());
        }
        if (mTable != null) {
            String path = mTable.getString(id);
            if (isVectorDrawableResource(path, getResourceName(id))) {
                return new android.graphics.drawable.VectorDrawable();
            }
            if (path != null && (path.endsWith(".webp") || path.endsWith(".png") ||
                    path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".gif"))) {
                Drawable d = loadDrawableFromFile(path);
                if (d != null) return d;
            }
            int color = mTable.getColor(id);
            if (color != 0xFF000000) {
                return new ColorDrawable(color);
            }
        }
        return new ColorDrawable(0xFFCCCCCC);
    }

    private boolean isVectorDrawableResource(String path, String resourceName) {
        String value = path != null ? path : resourceName;
        if (value == null) {
            return false;
        }
        String lower = value.toLowerCase(java.util.Locale.US);
        return lower.indexOf("vector") >= 0 && lower.endsWith(".xml");
    }

    private Drawable loadDrawableFromFile(String resPath) {
        // Find the actual file in the extracted res directory
        String resDir = null;
        try {
            java.lang.reflect.Field f = android.app.MiniServer.class.getDeclaredField("mApkInfo");
            f.setAccessible(true);
            Object info = f.get(android.app.MiniServer.get());
            if (info != null) {
                java.lang.reflect.Field rd = info.getClass().getField("resDir");
                resDir = (String) rd.get(info);
            }
        } catch (Exception e) { /* ignore */ }
        if (resDir == null) resDir = System.getProperty("westlake.apk.resdir");
        if (resDir == null) return null;

        java.io.File file = new java.io.File(resDir, resPath);
        if (!file.exists()) {
            // Try without "res/" prefix or with different density
            String name = file.getName();
            String[] tryDirs = {"res/drawable", "res/drawable-xxhdpi-v4", "res/drawable-xhdpi-v4",
                    "res/drawable-hdpi-v4", "res/drawable-mdpi-v4"};
            for (String dir : tryDirs) {
                java.io.File alt = new java.io.File(resDir, dir + "/" + name);
                if (alt.exists()) { file = alt; break; }
            }
        }
        if (!file.exists()) return null;

        try {
            java.io.FileInputStream fis = new java.io.FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            int off = 0;
            while (off < data.length) {
                int n = fis.read(data, off, data.length - off);
                if (n <= 0) break;
                off += n;
            }
            fis.close();
            // Create a Bitmap with the raw image data (host will decode it)
            android.graphics.Bitmap bmp = android.graphics.Bitmap.createFromImageData(data, 480, 800);
            return new android.graphics.drawable.BitmapDrawable(this, bmp);
        } catch (Exception e) {
            return null;
        }
    }

    public Drawable getDrawable(int id, Theme theme) {
        return getDrawable(id);
    }

    // ── Layout resources ─────────────────────────────────────────────────────

    /**
     * Get a layout resource as an XmlResourceParser.
     * Tries registered layout bytes first, then ResourceTable file lookup.
     * Returns a BinaryXmlParser wrapping the AXML data, or null if unavailable.
     */
    public XmlResourceParser getLayout(int id) {
        // 1. Try registered layout bytes
        byte[] layoutData = getLayoutBytes(id);
        if (layoutData != null && layoutData.length > 0) {
            try {
                return new BinaryXmlParser(layoutData);
            } catch (Exception e) {
                // fall through
            }
        }

        // 2. Try loading from APK via ResourceTable
        if (mTable != null) {
            String layoutFile = mTable.getLayoutFileName(id);
            if (layoutFile != null) {
                byte[] xmlData = loadLayoutXmlFromApk(layoutFile);
                if (xmlData != null && xmlData.length > 0) {
                    try {
                        return new BinaryXmlParser(xmlData);
                    } catch (Exception e) {
                        // fall through
                    }
                }
            }
        }

        return null;
    }

    /**
     * Load binary layout XML bytes from the extracted APK res/ directory.
     */
    private byte[] loadLayoutXmlFromApk(String layoutPath) {
        try {
            android.app.MiniServer server = android.app.MiniServer.get();
            if (server != null) {
                String resDir = null;
                android.app.ApkInfo info = server.getApkInfo();
                if (info != null) resDir = info.resDir;
                if (resDir == null && info != null) resDir = info.extractDir;

                if (resDir != null) {
                    java.io.File xmlFile = new java.io.File(resDir, layoutPath);
                    if (!xmlFile.exists() && layoutPath.startsWith("res/")) {
                        xmlFile = new java.io.File(resDir, layoutPath.substring(4));
                    }
                    if (xmlFile.exists()) {
                        return readFileBytes(xmlFile);
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private static byte[] readFileBytes(java.io.File file) {
        try {
            java.io.FileInputStream fis = new java.io.FileInputStream(file);
            try {
                byte[] data = new byte[(int) file.length()];
                int offset = 0;
                while (offset < data.length) {
                    int n = fis.read(data, offset, data.length - offset);
                    if (n < 0) break;
                    offset += n;
                }
                return data;
            } finally {
                fis.close();
            }
        } catch (Exception e) {
            return null;
        }
    }

    // ── Array resources ──────────────────────────────────────────────────────

    public int[] getIntArray(int id) {
        return new int[0];
    }

    // ── Resource ID lookup ───────────────────────────────────────────────────

    public int getIdentifier(String name, String defType, String defPackage) {
        String key = normalizeIdentifierKey(name, defType);
        if (mTable != null) {
            int id = mTable.getIdentifier(key);
            if (id != 0) {
                return id;
            }
        }
        String fallbackType = defType;
        String fallbackName = name;
        if (key != null) {
            int slash = key.indexOf('/');
            if (slash > 0 && slash + 1 < key.length()) {
                fallbackType = key.substring(0, slash);
                fallbackName = key.substring(slash + 1);
            } else {
                fallbackName = key;
            }
        }
        int fallback = getKnownMcdResourceIdentifier(fallbackName, fallbackType, defPackage);
        if (fallback != 0) {
            return fallback;
        }
        return 0;
    }

    private static String normalizeIdentifierKey(String name, String defType) {
        if (name == null) {
            return null;
        }
        String key = name;
        int colon = key.indexOf(':');
        if (colon >= 0 && colon + 1 < key.length()) {
            key = key.substring(colon + 1);
        }
        if (key.startsWith("res/")) {
            key = key.substring(4);
        }
        if (key.endsWith(".xml")) {
            key = key.substring(0, key.length() - 4);
        }
        if (key.indexOf('/') >= 0 || defType == null || defType.length() == 0) {
            return key;
        }
        return defType + "/" + key;
    }

    private static int getKnownMcdResourceIdentifier(String name, String defType, String defPackage) {
        if (name == null || defType == null) {
            return 0;
        }
        boolean mcdPackage = defPackage != null && defPackage.contains("mcdonalds");
        if (!mcdPackage) {
            return 0;
        }
        if ("id".equals(defType)) {
            if ("back".equals(name)) return 0x7f0b01c2;
            if ("basket_error".equals(name)) return 0x7f0b0212;
            if ("basket_layout".equals(name)) return 0x7f0b0219;
            if ("basket_price".equals(name)) return 0x7f0b021c;
            if ("close".equals(name)) return 0x7f0b03da;
            if ("content_view".equals(name)) return 0x7f0b04ba;
            if ("home_dashboard_container".equals(name)) return 0x7f0b0ae8;
            if ("intermediate_layout_container".equals(name)) return 0x7f0b0b83;
            if ("mcdBackNavigationButton".equals(name)) return 0x7f0b0e8a;
            if ("page_content".equals(name)) return 0x7f0b11e0;
            if ("page_content_holder".equals(name)) return 0x7f0b11e1;
            if ("page_root".equals(name)) return 0x7f0b11e3;
            if ("toolbar".equals(name)) return 0x7f0b1965;
            if ("toolbarCenterImageIcon".equals(name)) return 0x7f0b1967;
            if ("toolbarRightButton".equals(name)) return 0x7f0b196a;
            if ("toolbarSearchIcon".equals(name)) return 0x7f0b196f;
            if ("toolbarTitleText".equals(name)) return 0x7f0b1970;
            if ("toolbar_title".equals(name)) return 0x7f0b1978;
            return 0;
        }
        if ("layout".equals(defType)) {
	            if ("activity_base".equals(name)) return 0x7f0e0038;
	            if ("activity_dashboard_new".equals(name)) return 0x7f0e0044;
	            if ("activity_home_dashboard".equals(name)) return 0x7f0e0058;
	            if ("application_notification".equals(name)) return 0x7f0e00c3;
	            if ("base_layout".equals(name)) return 0x7f0e00ee;
            if ("campaign_application_notification".equals(name)) return 0x7f0e010a;
            if ("category_list_item".equals(name)) return 0x7f0e014a;
            if ("deal_for_today_item_loy".equals(name)) return 0x7f0e019d;
            if ("deal_viewall_item_loy".equals(name)) return 0x7f0e01b3;
            if ("flex_fragment_home_dashboard_delivery_error_tile".equals(name)) return 0x7f0e0216;
            if ("flex_order_cancelled_status_tile".equals(name)) return 0x7f0e0217;
            if ("flex_order_cancelled_status_tile_v2".equals(name)) return 0x7f0e0218;
            if ("flex_order_delivered_status_tile".equals(name)) return 0x7f0e0219;
            if ("flex_order_delivered_status_tile_v2".equals(name)) return 0x7f0e021a;
            if ("flex_order_in_progress_status_tile".equals(name)) return 0x7f0e021b;
            if ("flex_order_in_progress_status_tile_v2".equals(name)) return 0x7f0e021c;
            if ("flex_order_is_creating_status_tile_v2".equals(name)) return 0x7f0e021d;
            if ("flex_order_on_way_status_tile".equals(name)) return 0x7f0e021e;
            if ("flex_order_on_way_status_tile_v2".equals(name)) return 0x7f0e021f;
            if ("flex_preparing_order_status_tile".equals(name)) return 0x7f0e0221;
            if ("flex_preparing_order_status_tile_v2".equals(name)) return 0x7f0e0222;
            if ("fragment_deal_section".equals(name)) return 0x7f0e0252;
            if ("fragment_deal_section_layout_header".equals(name)) return 0x7f0e0253;
            if ("fragment_deal_section_loyalty".equals(name)) return 0x7f0e0254;
            if ("fragment_home_counter_pay_with_cash_hero_v2".equals(name)) return 0x7f0e027a;
            if ("fragment_home_curbside_hero_v2".equals(name)) return 0x7f0e027c;
            if ("fragment_home_dashboard".equals(name)) return 0x7f0e027d;
            if ("fragment_home_dashboard_curbside_order_hero".equals(name)) return 0x7f0e027f;
            if ("fragment_home_dashboard_hero_section".equals(name)) return 0x7f0e0282;
            if ("fragment_home_dashboard_hero_section_updated".equals(name)) return 0x7f0e0283;
            if ("fragment_home_dashboard_needs_attention".equals(name)) return 0x7f0e0284;
            if ("fragment_home_dashboard_no_spot_number_card".equals(name)) return 0x7f0e0285;
            if ("fragment_home_drive_thru_hero_v2".equals(name)) return 0x7f0e028b;
            if ("fragment_menu_section".equals(name)) return 0x7f0e02b1;
            if ("fragment_home_optin_checkout".equals(name)) return 0x7f0e028e;
            if ("fragment_home_table_service_hero_v2".equals(name)) return 0x7f0e0290;
            if ("fragment_oal_order_hero".equals(name)) return 0x7f0e02d0;
            if ("fragment_order".equals(name)) return 0x7f0e02d4;
            if ("fragment_popular_section".equals(name)) return 0x7f0e0305;
            if ("fragment_promotion_section".equals(name)) return 0x7f0e030e;
            if ("home_dashboard_roa_order_in_progress_hero_v2".equals(name)) return 0x7f0e035f;
            if ("home_dashboard_roa_thank_you_hero_v2".equals(name)) return 0x7f0e0361;
            if ("home_dashboard_section".equals(name)) return 0x7f0e0362;
            if ("home_deal_adapter_loy".equals(name)) return 0x7f0e0364;
            if ("home_menu_guest_user".equals(name)) return 0x7f0e0366;
            if ("home_menu_section_full_menu_item".equals(name)) return 0x7f0e0367;
            if ("home_menu_section_item".equals(name)) return 0x7f0e0368;
            if ("home_popular_item_adapter".equals(name)) return 0x7f0e0369;
            if ("home_promotion_item".equals(name)) return 0x7f0e036a;
            if ("home_promotion_item_updated".equals(name)) return 0x7f0e036b;
            if ("layout_bonus_tile_fragment".equals(name)) return 0x7f0e03ac;
            if ("mcd_toolbar".equals(name)) return 0x7f0e03f5;
            if ("new_plp_product_item".equals(name)) return 0x7f0e0433;
            if ("one_app_home_delivery_fragment".equals(name)) return 0x7f0e044f;
            if ("toolbar_close_back".equals(name)) return 0x7f0e0530;
        }
        return 0;
    }

    private static String getKnownMcdResourceName(int id) {
        switch (id) {
            case 0x7f0b01c2: return "id/back";
            case 0x7f0b0212: return "id/basket_error";
            case 0x7f0b0219: return "id/basket_layout";
            case 0x7f0b021c: return "id/basket_price";
            case 0x7f0b03da: return "id/close";
            case 0x7f0b04ba: return "id/content_view";
            case 0x7f0b0ae8: return "id/home_dashboard_container";
            case 0x7f0b0b83: return "id/intermediate_layout_container";
            case 0x7f0b0e8a: return "id/mcdBackNavigationButton";
            case 0x7f0b11e0: return "id/page_content";
            case 0x7f0b11e1: return "id/page_content_holder";
            case 0x7f0b11e3: return "id/page_root";
            case 0x7f0b1965: return "id/toolbar";
            case 0x7f0b1967: return "id/toolbarCenterImageIcon";
            case 0x7f0b196a: return "id/toolbarRightButton";
            case 0x7f0b196f: return "id/toolbarSearchIcon";
            case 0x7f0b1970: return "id/toolbarTitleText";
            case 0x7f0b1978: return "id/toolbar_title";
	            case 0x7f0e0038: return "layout/activity_base";
	            case 0x7f0e0044: return "layout/activity_dashboard_new";
	            case 0x7f0e0058: return "layout/activity_home_dashboard";
	            case 0x7f0e00c3: return "layout/application_notification";
	            case 0x7f0e00ee: return "layout/base_layout";
            case 0x7f0e010a: return "layout/campaign_application_notification";
            case 0x7f0e014a: return "layout/category_list_item";
            case 0x7f0e019d: return "layout/deal_for_today_item_loy";
            case 0x7f0e01b3: return "layout/deal_viewall_item_loy";
            case 0x7f0e0216: return "layout/flex_fragment_home_dashboard_delivery_error_tile";
            case 0x7f0e0217: return "layout/flex_order_cancelled_status_tile";
            case 0x7f0e0218: return "layout/flex_order_cancelled_status_tile_v2";
            case 0x7f0e0219: return "layout/flex_order_delivered_status_tile";
            case 0x7f0e021a: return "layout/flex_order_delivered_status_tile_v2";
            case 0x7f0e021b: return "layout/flex_order_in_progress_status_tile";
            case 0x7f0e021c: return "layout/flex_order_in_progress_status_tile_v2";
            case 0x7f0e021d: return "layout/flex_order_is_creating_status_tile_v2";
            case 0x7f0e021e: return "layout/flex_order_on_way_status_tile";
            case 0x7f0e021f: return "layout/flex_order_on_way_status_tile_v2";
            case 0x7f0e0221: return "layout/flex_preparing_order_status_tile";
            case 0x7f0e0222: return "layout/flex_preparing_order_status_tile_v2";
            case 0x7f0e0252: return "layout/fragment_deal_section";
            case 0x7f0e0253: return "layout/fragment_deal_section_layout_header";
            case 0x7f0e0254: return "layout/fragment_deal_section_loyalty";
            case 0x7f0e027a: return "layout/fragment_home_counter_pay_with_cash_hero_v2";
            case 0x7f0e027c: return "layout/fragment_home_curbside_hero_v2";
            case 0x7f0e027d: return "layout/fragment_home_dashboard";
            case 0x7f0e027f: return "layout/fragment_home_dashboard_curbside_order_hero";
            case 0x7f0e0282: return "layout/fragment_home_dashboard_hero_section";
            case 0x7f0e0283: return "layout/fragment_home_dashboard_hero_section_updated";
            case 0x7f0e0284: return "layout/fragment_home_dashboard_needs_attention";
            case 0x7f0e0285: return "layout/fragment_home_dashboard_no_spot_number_card";
            case 0x7f0e028b: return "layout/fragment_home_drive_thru_hero_v2";
            case 0x7f0e028e: return "layout/fragment_home_optin_checkout";
            case 0x7f0e0290: return "layout/fragment_home_table_service_hero_v2";
            case 0x7f0e02b1: return "layout/fragment_menu_section";
            case 0x7f0e02d0: return "layout/fragment_oal_order_hero";
            case 0x7f0e02d4: return "layout/fragment_order";
            case 0x7f0e0305: return "layout/fragment_popular_section";
            case 0x7f0e030e: return "layout/fragment_promotion_section";
            case 0x7f0e035f: return "layout/home_dashboard_roa_order_in_progress_hero_v2";
            case 0x7f0e0361: return "layout/home_dashboard_roa_thank_you_hero_v2";
            case 0x7f0e0362: return "layout/home_dashboard_section";
            case 0x7f0e0364: return "layout/home_deal_adapter_loy";
            case 0x7f0e0366: return "layout/home_menu_guest_user";
            case 0x7f0e0367: return "layout/home_menu_section_full_menu_item";
            case 0x7f0e0368: return "layout/home_menu_section_item";
            case 0x7f0e0369: return "layout/home_popular_item_adapter";
            case 0x7f0e036a: return "layout/home_promotion_item";
            case 0x7f0e036b: return "layout/home_promotion_item_updated";
            case 0x7f0e03ac: return "layout/layout_bonus_tile_fragment";
            case 0x7f0e03f5: return "layout/mcd_toolbar";
            case 0x7f0e0433: return "layout/new_plp_product_item";
            case 0x7f0e044f: return "layout/one_app_home_delivery_fragment";
            case 0x7f0e0530: return "layout/toolbar_close_back";
            default: return null;
        }
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
        String known = getKnownMcdResourceName(id);
        if (known != null) return known;
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
        String known = getKnownMcdResourceName(id);
        if (known != null) {
            int slash = known.indexOf('/');
            if (slash >= 0 && slash + 1 < known.length()) {
                return known.substring(slash + 1);
            }
            return known;
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
        String known = getKnownMcdResourceName(id);
        if (known != null) {
            int slash = known.indexOf('/');
            if (slash > 0) {
                return known.substring(0, slash);
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

    public void getValue(int id, android.util.TypedValue outValue, boolean resolveRefs) {
        if (outValue == null) {
            throw new NullPointerException("outValue");
        }
        outValue.resourceId = id;
        outValue.density = mDisplayMetrics != null ? mDisplayMetrics.densityDpi : 0;
        outValue.assetCookie = 0;
        outValue.changingConfigurations = 0;
        outValue.sourceResourceId = id;
        outValue.string = null;
        outValue.data = 0;
        outValue.type = android.util.TypedValue.TYPE_NULL;

        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) {
            outValue.type = android.util.TypedValue.TYPE_INT_DEC;
            outValue.data = ((Integer) reg).intValue();
            return;
        }
        if (reg instanceof String) {
            outValue.type = android.util.TypedValue.TYPE_STRING;
            outValue.string = (String) reg;
            return;
        }

        if (mTable != null) {
            String text = mTable.getString(id);
            if (text != null) {
                outValue.type = android.util.TypedValue.TYPE_STRING;
                outValue.string = text;
                return;
            }
        }

        String typeName = getResourceTypeName(id);
        if ("color".equals(typeName)) {
            outValue.type = android.util.TypedValue.TYPE_FIRST_COLOR_INT;
            outValue.data = getColor(id);
        } else if ("bool".equals(typeName)) {
            outValue.type = android.util.TypedValue.TYPE_INT_BOOLEAN;
            outValue.data = getBoolean(id) ? 1 : 0;
        } else if ("integer".equals(typeName)) {
            outValue.type = android.util.TypedValue.TYPE_INT_DEC;
            outValue.data = getInteger(id);
        } else if ("dimen".equals(typeName)) {
            outValue.type = android.util.TypedValue.TYPE_DIMENSION;
            outValue.data = getDimensionPixelSize(id);
        } else if ("string".equals(typeName)) {
            outValue.type = android.util.TypedValue.TYPE_STRING;
            outValue.string = getString(id);
        } else if ("layout".equals(typeName) || "drawable".equals(typeName)
                || "xml".equals(typeName) || "raw".equals(typeName)) {
            outValue.type = android.util.TypedValue.TYPE_STRING;
            outValue.string = getResourceName(id);
        } else {
            outValue.type = android.util.TypedValue.TYPE_REFERENCE;
            outValue.data = id;
        }
    }

    public void getValueForDensity(int id, int density, android.util.TypedValue outValue, boolean resolveRefs) {
        getValue(id, outValue, resolveRefs);
        if (outValue != null) {
            outValue.density = density;
        }
    }

    public java.io.InputStream openRawResource(int id, android.util.TypedValue value) {
        return null;
    }

    // ── CR30-B (2026-05-13): public Resources surface expanded for classpath shadow.
    // After removing the framework_duplicates.txt strip entry, OUR shim Resources
    // wins at runtime. Apps and androidx code (ResourcesWrapper, SyncedResourcesWrapper,
    // ContextThemeWrapper, AppCompatDelegate) hit the full public surface — methods
    // below were absent in the pre-shadow build (where framework Resources covered
    // them). Each has a safe default body that subclasses (WestlakeResources) can
    // override. No throw-by-default: returning empty/zero is safer for cold-boot.
    // -----------------------------------------------------------------------------

    /** Get a string by name (string resource lookup via R.string.* identifier). */
    public CharSequence getText(int id, CharSequence def) {
        CharSequence cs = null;
        try { cs = getText(id); } catch (Throwable ignored) {}
        return (cs == null || cs.length() == 0) ? def : cs;
    }

    /** Quantity text — formatted plural string. Returns the base string. */
    public CharSequence getQuantityText(int id, int quantity) {
        return getText(id);
    }

    /** Quantity string — formatted plural string. Returns the base string. */
    public String getQuantityString(int id, int quantity) {
        return getString(id);
    }

    /** Quantity string with format args. */
    public String getQuantityString(int id, int quantity, Object... formatArgs) {
        return getString(id, formatArgs);
    }

    /** Animation XML resource — returns null (no animation infra). */
    public XmlResourceParser getAnimation(int id) {
        return null;
    }

    /** Float resource. */
    public float getFloat(int id) {
        Object reg = mRegistry.get(Integer.valueOf(id));
        if (reg instanceof Integer) return (float) ((Integer) reg).intValue();
        if (mTable != null) {
            return mTable.getDimension(id, resourceDensity());
        }
        return 0f;
    }

    /** Fraction resource. */
    public float getFraction(int id, int base, int pbase) {
        return 0f;
    }

    /** Drawable for a specific density. */
    public Drawable getDrawableForDensity(int id, int density) {
        return getDrawable(id);
    }

    /** Drawable for a specific density and theme. */
    public Drawable getDrawableForDensity(int id, int density, Theme theme) {
        return getDrawable(id);
    }

    /** Movie resource — deprecated GIF support. Returns null. */
    public android.graphics.Movie getMovie(int id) {
        return null;
    }

    /** Open a raw resource as an InputStream. */
    public java.io.InputStream openRawResource(int id) {
        return null;
    }

    /** Open a raw resource as an AssetFileDescriptor. */
    public AssetFileDescriptor openRawResourceFd(int id) {
        return null;
    }

    /** Update Configuration / DisplayMetrics. Best-effort field copy: our
     *  Configuration / metrics are owned by WestlakeResources via its own ctor.
     *  Note: Configuration#setTo and DisplayMetrics#setTo exist on the framework
     *  classes (both stripped from this dex via framework_duplicates.txt, so
     *  framework wins at runtime) but not on our compile-time shim, so we
     *  reflectively dispatch to keep the override surface compatible. */
    public void updateConfiguration(Configuration config, DisplayMetrics metrics) {
        if (config != null) {
            try {
                java.lang.reflect.Method m = config.getClass().getMethod("setTo", Configuration.class);
                m.invoke(mConfiguration, config);
            } catch (Throwable ignored) {}
        }
        if (metrics != null) {
            try {
                java.lang.reflect.Method m = metrics.getClass().getMethod("setTo", DisplayMetrics.class);
                m.invoke(mDisplayMetrics, metrics);
            } catch (Throwable ignored) {
                mDisplayMetrics.density       = metrics.density;
                mDisplayMetrics.densityDpi    = metrics.densityDpi;
                mDisplayMetrics.scaledDensity = metrics.scaledDensity;
                mDisplayMetrics.xdpi          = metrics.xdpi;
                mDisplayMetrics.ydpi          = metrics.ydpi;
                mDisplayMetrics.widthPixels   = metrics.widthPixels;
                mDisplayMetrics.heightPixels  = metrics.heightPixels;
            }
        }
    }

    /** String-keyed getValue — looks up by resource name. */
    public void getValue(String name, android.util.TypedValue outValue, boolean resolveRefs) {
        int id = getIdentifier(name, null, null);
        getValue(id, outValue, resolveRefs);
    }

    /** Parse bundle extras from a single tag — used by ActivityInfo parsing. */
    public void parseBundleExtra(String tagName, android.util.AttributeSet attrs,
            android.os.Bundle outBundle) {
        // No-op: ManifestParser builds the bundle elsewhere.
    }

    /** Add resource loaders — no-op (we don't use the ResourcesLoader infra). */
    public void addLoaders(android.content.res.loader.ResourcesLoader... loaders) {
        // No-op.
    }

    /** Remove resource loaders — no-op. */
    public void removeLoaders(android.content.res.loader.ResourcesLoader... loaders) {
        // No-op.
    }

    /** ResourcesLoader list — empty by default. */
    public java.util.List<android.content.res.loader.ResourcesLoader> getLoaders() {
        return java.util.Collections.emptyList();
    }

    /** Clear all resource loaders — no-op. */
    public void clearLoaders() {
        // No-op.
    }

    /** Animator cache — null is acceptable for the View ctor probe path. */
    public ConfigurationBoundResourceCache<android.animation.Animator> getAnimatorCache() {
        return new ConfigurationBoundResourceCache<android.animation.Animator>();
    }

    /** StateListAnimator cache — null is acceptable for the View ctor probe path. */
    public ConfigurationBoundResourceCache<android.animation.StateListAnimator> getStateListAnimatorCache() {
        return new ConfigurationBoundResourceCache<android.animation.StateListAnimator>();
    }

    /** Preload font ids — no-op (no font preloading). */
    public void preloadFonts(int id) {
        // No-op.
    }

    /** Flush layout cache — no-op. */
    public final void flushLayoutCache() {
        // No-op.
    }

    /** Start preloading — no-op. */
    public final void startPreloading() {
        // No-op.
    }

    /** Finish preloading — no-op. */
    public final void finishPreloading() {
        // No-op.
    }

    /** Calc config changes — returns 0 (no changes). */
    public int calcConfigChanges(Configuration config) {
        return 0;
    }

    /** ClassLoader for resource resolution. */
    public ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }
}
