// SPDX-License-Identifier: Apache-2.0
//
// V2-Step4 (2026-05-13) — WestlakeResources rewrite
//
// Replaces the M4-PRE6/PRE12/PRE13/PRE14 plant-and-Unsafe path with a
// thin façade over {@link WestlakeAssetManager} that reads the APK's
// resources.arsc directly via {@link ResourceArscParser}.
//
// Per BINDER_PIVOT_DESIGN_V2.md §3.4 (decision 11-B):
//   "WestlakeResources extends framework Resources only nominally (so
//    instanceof Resources works) and overrides every public method ...
//    each reads directly from a ResourceTable we build at Application
//    boot from the APK's resources.arsc."
//
// Anti-patterns avoided per V2-Step4 brief:
//   - NO Unsafe.allocateInstance.
//   - NO Field.setAccessible.
//   - NO per-app branches (the arsc parser works for any APK).
//   - NO buildReflective / M4-PRE12 createSyntheticAssetManager /
//     M4-PRE13 plantLocaleState / M4-PRE14 plantDisplayAdjustments.
//
// Compile-time hierarchy:
//   shim Resources (android.content.res.Resources, this shim's version
//   at shim/java/android/content/res/Resources.java) — has a no-arg
//   public ctor we delegate to.
//
// Runtime hierarchy:
//   framework Resources (android.content.res.Resources from
//   framework.jar) — the shim Resources class is stripped by
//   scripts/framework_duplicates.txt so the framework one wins. Its
//   hidden no-arg ctor is reachable via super() (this is the same
//   approach com.westlake.engine.WestlakeResources uses today).
//
// Backward compat:
//   {@link #createSafe()} remains as a static factory matching the
//   M4-PRE6 call site in WestlakeContextImpl. It now returns a new
//   instance of this class (no Unsafe, no plant).

package com.westlake.services;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.IOException;

/**
 * Thin {@link Resources} surface backed by {@link WestlakeAssetManager}
 * that reads an APK's resources.arsc directly.
 *
 * No reflective field plants. No Unsafe.
 */
public class WestlakeResources extends Resources {

    // ─── Defaults (OnePlus 6 / generic 1080×2280 hdpi) ──────────────

    /** Default density: hdpi (240) — matches OnePlus 6 (xxhdpi target). */
    private static final int DEFAULT_DENSITY_DPI = 240;
    private static final int DEFAULT_WIDTH_PX  = 1080;
    private static final int DEFAULT_HEIGHT_PX = 2280;

    // ─── State ──────────────────────────────────────────────────────

    private final WestlakeAssetManager mAssets;
    private final Configuration mConfig;
    private final DisplayMetrics mMetrics;

    // ─── Constructors ───────────────────────────────────────────────

    /** Build from APK path (parses resources.arsc on construction). */
    public WestlakeResources(String apkPath) throws IOException {
        this(new WestlakeAssetManager(apkPath));
    }

    /** Build from a pre-constructed {@link WestlakeAssetManager}. */
    public WestlakeResources(WestlakeAssetManager assets) {
        // super(ClassLoader) targets framework Resources's @UnsupportedAppUsage
        // public 1-arg ctor (native-free; just sets mClassLoader and calls
        // ResourcesManager.registerAllResourcesReference). The 0-arg ctor in
        // framework Resources is PRIVATE in API 30+ so super() would fail
        // at runtime — see V2-Step4 brief + M4-PRE6 historical notes.
        super(WestlakeResources.class.getClassLoader());
        this.mAssets = assets;
        this.mConfig = buildDefaultConfiguration();
        this.mMetrics = buildDefaultDisplayMetrics();
    }

    /** Build with NO arsc backing (everything returns null/default). */
    public WestlakeResources() {
        super(WestlakeResources.class.getClassLoader());
        this.mAssets = null;
        this.mConfig = buildDefaultConfiguration();
        this.mMetrics = buildDefaultDisplayMetrics();
    }

    // ─── Factory (backward compat for WestlakeContextImpl) ──────────

    /**
     * M4-PRE6 entry point retained for compile compatibility. Returns a
     * working {@link Resources}, or null if construction failed.
     *
     * V2-Step4: no Unsafe; no plant. Tries to source the APK path from
     * {@code WestlakeLauncher.currentApkPathForShim()} (set during boot)
     * and parse resources.arsc. On any failure returns a zero-arsc
     * WestlakeResources whose getString/etc. return null defaults.
     *
     * CR29-3 (2026-05-13): guaranteed never-null. The outer Throwable
     * catch previously returned null when {@code new WestlakeResources()}
     * failed for any reason (rare but possible — class init quirks during
     * cold-boot Activity.attach). McD's PHASE G4 hit this via
     * ContextThemeWrapper.initializeTheme → getResources NPE. We now
     * delegate to {@link #empty()} on outer failure so the caller always
     * sees a usable Resources surface.
     */
    public static Resources createSafe() {
        // Try to use the boot-snapshot APK path if available. Failure to
        // resolve / parse is non-fatal: we return a zero-arsc instance so
        // callers can still read Configuration / DisplayMetrics.
        //
        // The WestlakeLauncher lookup uses reflective Class.forName so this
        // class doesn't statically link against the engine layer — vital for
        // class init at this point in the cold-boot sequence where engine
        // class init may not yet have completed (PF-arch-054 may fire on
        // engine class load).
        try {
            String apkPath = resolveApkPathReflectively();
            if (apkPath != null && !apkPath.isEmpty()) {
                try {
                    return new WestlakeResources(apkPath);
                } catch (Throwable t) {
                    // Failed to parse arsc — fall through to zero-arsc.
                }
            }
            return new WestlakeResources();
        } catch (Throwable t) {
            return empty();
        }
    }

    /**
     * CR29-3 (2026-05-13): guaranteed-non-null factory for the
     * WestlakeContextImpl.getResources fallback path. Returns a zero-arsc
     * WestlakeResources whose Configuration/DisplayMetrics are the
     * defaults (so AppCompatDelegate.attachBaseContext + ContextThemeWrapper
     * .initializeTheme don't NPE) and whose value lookups return null/0
     * (so callers see NotFoundException-equivalents instead of crashes).
     *
     * <p>Unlike {@link #createSafe()} the only way this returns null is
     * if the framework Resources(ClassLoader) ctor itself throws — at that
     * point the runtime is so degraded that no Resources instance is
     * possible. Callers should still null-check (see WestlakeContextImpl).
     *
     * <p>This is a static factory, NOT a new public instance method —
     * does not widen WestlakeContextImpl's surface per CR22 freeze.
     */
    public static WestlakeResources empty() {
        try {
            return new WestlakeResources();
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * CR29-3 (2026-05-13): APK-path factory used by
     * {@link com.westlake.services.WestlakeContextImpl#getResources()} —
     * the Context already knows its own ApplicationInfo.sourceDir, so we
     * don't need the {@code -Dwestlake.apk.path} system property. Returns
     * a fully-loaded WestlakeResources backed by the APK's arsc on
     * success; falls back to {@link #empty()} on any parse failure.
     */
    public static WestlakeResources createForApkPath(String apkPath) {
        if (apkPath != null && !apkPath.isEmpty()) {
            try {
                return new WestlakeResources(apkPath);
            } catch (Throwable t) {
                // arsc parse failed — caller still needs a working Resources
                // for Configuration/DisplayMetrics; fall through to empty().
            }
        }
        return empty();
    }

    private static String resolveApkPathReflectively() {
        // Read directly from the system property that WestlakeLauncher uses
        // (set during boot via -Dwestlake.apk.path=...). Avoids triggering
        // class init of WestlakeLauncher at WestlakeResources class-init
        // time, which can fail during the cold-boot Activity.attach chain.
        try {
            String v = System.getProperty("westlake.apk.path");
            if (v != null && !v.isEmpty()) return v;
        } catch (Throwable ignored) {}
        return null;
    }

    /** Public so the M4-PRE6 brief's Configuration shape stays testable. */
    public static Configuration buildDefaultConfiguration() {
        Configuration c = new Configuration();
        try { c.setToDefaults(); } catch (Throwable ignored) {}
        c.fontScale = 1.0f;
        c.densityDpi = DEFAULT_DENSITY_DPI;
        c.screenWidthDp = pxToDp(DEFAULT_WIDTH_PX, DEFAULT_DENSITY_DPI);
        c.screenHeightDp = pxToDp(DEFAULT_HEIGHT_PX, DEFAULT_DENSITY_DPI);
        c.smallestScreenWidthDp = Math.min(c.screenWidthDp, c.screenHeightDp);
        c.orientation = Configuration.ORIENTATION_PORTRAIT;
        // V2-Step4: no longer plant Locale / LocaleList reflectively. Use
        // public APIs: assign the legacy 'locale' field directly (public on
        // both shim and framework Configuration), and try setLocales(...)
        // for the modern LocaleList path. Both are public APIs — no
        // Field.setAccessible.
        java.util.Locale defaultLocale = java.util.Locale.US;
        try { c.locale = defaultLocale; } catch (Throwable ignored) {}
        try {
            android.os.LocaleList ll = new android.os.LocaleList(defaultLocale);
            c.setLocales(ll);
        } catch (Throwable ignored) {}
        return c;
    }

    public static DisplayMetrics buildDefaultDisplayMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        // Note: shim DisplayMetrics lacks setToDefaults(); field defaults
        // (density=2.0f, scaledDensity=2.0f) are already reasonable. We
        // override the dimension fields explicitly below.
        dm.density = DEFAULT_DENSITY_DPI / (float) DisplayMetrics.DENSITY_DEFAULT;
        dm.densityDpi = DEFAULT_DENSITY_DPI;
        dm.scaledDensity = dm.density;
        dm.xdpi = DEFAULT_DENSITY_DPI;
        dm.ydpi = DEFAULT_DENSITY_DPI;
        dm.widthPixels = DEFAULT_WIDTH_PX;
        dm.heightPixels = DEFAULT_HEIGHT_PX;
        return dm;
    }

    private static int pxToDp(int px, int dpi) {
        return Math.round((px * DisplayMetrics.DENSITY_DEFAULT) / (float) dpi);
    }

    // ─── Configuration / DisplayMetrics overrides ───────────────────

    @Override public Configuration getConfiguration() { return mConfig; }
    @Override public DisplayMetrics getDisplayMetrics() { return mMetrics; }

    // CR30-B (2026-05-13): Restored @Override of getAssets(). With
    // android/content/res/Resources REMOVED from framework_duplicates.txt
    // (V2-Step4/CR30-B per BINDER_PIVOT_DESIGN_V2 §3.4 decision 11-B), OUR
    // shim Resources wins on classpath at runtime — and shim's getAssets()
    // is declared NON-FINAL (shim/java/android/content/res/Resources.java
    // line ~795). So this override is legal again. McD's PHASE G4 NPE was:
    //   PhraseResources.<init>(Resources)
    //     -> SyncedResourcesWrapper.<init>(Resources)
    //     -> ResourcesWrapper.<init>(Resources)
    //     -> framework Resources.getAssets() [final, reads mResourcesImpl=null]
    //     -> ResourcesImpl.getAssets() NPE
    // Now `resources.getAssets()` virtual-dispatches into THIS method on
    // our WestlakeResources, which returns null safely (callers store the
    // reference, they rarely actually call methods on the AssetManager).
    //
    // CR29-3 history: @Override was REMOVED then to silence LinkageError
    // against framework Resources's final getAssets. CR30-B is the proper
    // architectural fix: own the Resources class outright.
    @Override
    public android.content.res.AssetManager getAssets() {
        // Westlake doesn't expose a framework AssetManager (its native
        // ApkAssets/NativeAssetManager backend isn't bootstrapped in our
        // dalvikvm process). Callers that need asset bytes should reach
        // through getWestlakeAssets() / WestlakeAssetManager.
        return null;
    }

    // CR30-B (2026-05-13): newTheme() override restored. shim Resources's
    // newTheme() is non-final and returns a stub Theme.
    @Override
    public Theme newTheme() {
        try {
            return super.newTheme();
        } catch (Throwable t) {
            return new Theme();
        }
    }

    /** Westlake-side accessor for the actual asset manager. */
    public WestlakeAssetManager getWestlakeAssets() { return mAssets; }

    // ─── String / text accessors ────────────────────────────────────

    @Override public String getString(int id) {
        if (mAssets != null) {
            String s = mAssets.getString(id);
            if (s != null) return s;
        }
        try { return super.getString(id); } catch (Throwable t) { return ""; }
    }

    @Override public String getString(int id, Object... formatArgs) {
        String template = getString(id);
        if (template == null) template = "";
        if (formatArgs == null || formatArgs.length == 0) return template;
        try {
            return String.format(template, formatArgs);
        } catch (Throwable t) {
            return template;
        }
    }

    @Override public CharSequence getText(int id) {
        if (mAssets != null) {
            String s = mAssets.getString(id);
            if (s != null) return s;
        }
        try { return super.getText(id); } catch (Throwable t) { return ""; }
    }

    public CharSequence getText(int id, CharSequence def) {
        // Not an @Override against the shim Resources (which lacks the
        // 2-arg signature); we provide it for callers that look it up
        // reflectively or against framework Resources at runtime.
        CharSequence cs = getText(id);
        return (cs == null || cs.length() == 0) ? def : cs;
    }

    @Override public String[] getStringArray(int id) {
        try { return super.getStringArray(id); }
        catch (Throwable t) { return new String[0]; }
    }

    @Override public CharSequence[] getTextArray(int id) {
        try { return super.getTextArray(id); }
        catch (Throwable t) { return new CharSequence[0]; }
    }

    // ─── Numeric accessors ──────────────────────────────────────────

    @Override public int getInteger(int id) {
        if (mAssets != null) {
            int v = mAssets.getInteger(id, Integer.MIN_VALUE);
            if (v != Integer.MIN_VALUE) return v;
        }
        try { return super.getInteger(id); } catch (Throwable t) { return 0; }
    }

    @Override public boolean getBoolean(int id) {
        if (mAssets != null && mAssets.hasValue(id)) {
            return mAssets.getBoolean(id, false);
        }
        try { return super.getBoolean(id); } catch (Throwable t) { return false; }
    }

    @Override public int getColor(int id) {
        if (mAssets != null) {
            int c = mAssets.getColor(id, 0);
            if (c != 0) return c;
        }
        try { return super.getColor(id); } catch (Throwable t) { return 0; }
    }

    @Override public int getColor(int id, Theme theme) { return getColor(id); }

    @Override public int[] getIntArray(int id) {
        try { return super.getIntArray(id); }
        catch (Throwable t) { return new int[0]; }
    }

    @Override public float getDimension(int id) {
        if (mAssets != null) {
            float d = mAssets.getDimension(id, mMetrics.density);
            if (d != 0f) return d;
        }
        try { return super.getDimension(id); } catch (Throwable t) { return 0f; }
    }

    @Override public int getDimensionPixelSize(int id) {
        float f = getDimension(id);
        return Math.round(f);
    }

    @Override public int getDimensionPixelOffset(int id) {
        return (int) getDimension(id);
    }

    // ─── Drawable / XML / TypedValue ────────────────────────────────

    @Override public Drawable getDrawable(int id) {
        try { return super.getDrawable(id); }
        catch (Throwable t) { return new ColorDrawable(0); }
    }

    @Override public Drawable getDrawable(int id, Theme theme) {
        try { return super.getDrawable(id, theme); }
        catch (Throwable t) { return new ColorDrawable(0); }
    }

    @Override public XmlResourceParser getXml(int id) {
        // CR48 wire-up: existing BinaryXmlParser implements XmlResourceParser
        // and takes axml byte[] directly. WestlakeAssetManager hands us the
        // axml blob from arsc.xmlBlobs (parsed at boot). No super-delegation
        // (framework's getXml requires ResourcesImpl state we don't carry).
        if (mAssets != null) {
            byte[] blob = mAssets.getXmlBytes(id);
            if (blob != null) {
                return new android.content.res.BinaryXmlParser(blob);
            }
        }
        try { return super.getXml(id); }
        catch (Throwable t) { return null; }
    }

    @Override public XmlResourceParser getLayout(int id) {
        // Layouts ARE xml resources in AOSP — same path.
        return getXml(id);
    }

    @Override public void getValue(int id, TypedValue outValue, boolean resolveRefs) {
        // Try to populate outValue from arsc first; fall back to super.
        if (outValue != null && mAssets != null) {
            String s = mAssets.getString(id);
            if (s != null) {
                outValue.type = TypedValue.TYPE_STRING;
                outValue.string = s;
                outValue.resourceId = id;
                return;
            }
            int v = mAssets.getInteger(id, Integer.MIN_VALUE);
            if (v != Integer.MIN_VALUE) {
                outValue.type = TypedValue.TYPE_INT_DEC;
                outValue.data = v;
                outValue.resourceId = id;
                return;
            }
        }
        try { super.getValue(id, outValue, resolveRefs); }
        catch (Throwable t) {
            if (outValue != null) {
                outValue.type = TypedValue.TYPE_FLOAT;
                outValue.data = Float.floatToIntBits(1.0f);
                outValue.resourceId = id;
                outValue.string = "";
            }
        }
    }

    // ─── Identifier / Name lookups ──────────────────────────────────

    @Override public int getIdentifier(String name, String defType, String defPackage) {
        if (mAssets != null && name != null) {
            String key;
            if (name.indexOf('/') >= 0) {
                key = name;
            } else if (defType != null && defType.length() > 0) {
                key = defType + "/" + name;
            } else {
                key = name;
            }
            int id = mAssets.getIdentifier(key);
            if (id != 0) return id;
        }
        try { return super.getIdentifier(name, defType, defPackage); }
        catch (Throwable t) { return 0; }
    }

    @Override public String getResourceName(int id) {
        if (mAssets != null) {
            String n = mAssets.getResourceName(id);
            if (n != null) return n;
        }
        try { return super.getResourceName(id); }
        catch (Throwable t) { return null; }
    }

    @Override public String getResourceEntryName(int id) {
        String n = (mAssets != null) ? mAssets.getResourceName(id) : null;
        if (n != null) {
            int slash = n.indexOf('/');
            return slash >= 0 ? n.substring(slash + 1) : n;
        }
        try { return super.getResourceEntryName(id); }
        catch (Throwable t) { return null; }
    }

    @Override public String getResourceTypeName(int id) {
        String n = (mAssets != null) ? mAssets.getResourceName(id) : null;
        if (n != null) {
            int slash = n.indexOf('/');
            return slash >= 0 ? n.substring(0, slash) : null;
        }
        try { return super.getResourceTypeName(id); }
        catch (Throwable t) { return null; }
    }

    // ─── Theme factory ──────────────────────────────────────────────
    // CR30-B (2026-05-13): newTheme() is now overridden above (post-shadow
    // shim Resources owns the class). See the @Override block earlier in
    // this file.
}
