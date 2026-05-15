/*
 * PF-frag-lifecycle-res (2026-05-11): Resources subclass that returns
 * an XmlResourceParser (BinaryXmlParser) for noice layout IDs.
 *
 * The default shim Resources.getLayout looks for AXML bytes via
 * Resources.registerLayoutBytes() (not populated for stock APKs) and
 * via ResourceTable.getLayoutFileName() (which only works if the arsc
 * stores the path as a resolved string, not as an entry index).
 *
 * For noice, the arsc stores layout entries as references into the
 * global string pool — ResourceTable.getEntryFilePath(resId) returns
 * paths like "res/Xp.xml". This subclass uses THAT path + the
 * extracted res directory to find the compiled AXML bytes.
 *
 * Generic, no per-app names.
 */
package com.westlake.engine;

import android.app.Activity;
import android.content.res.BinaryXmlParser;
import android.content.res.ResourceTable;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import java.io.File;
import java.io.FileInputStream;

public final class WestlakeResources extends Resources {

    private final ResourceTable mArsc;
    private final String mResDir;
    /** PF-frag-lifecycle-axml (2026-05-11): a real AssetManager loaded with
     * the launched APK so getLayout can return a framework XmlBlock$Parser
     * (required for ThemeImpl.obtainStyledAttributes which check-casts the
     * AttributeSet to XmlBlock$Parser). */
    private volatile android.content.res.AssetManager mApkAssets;
    private volatile int mApkAssetsCookie;
    private volatile boolean mApkAssetsTried;

    public WestlakeResources(Activity hostActivity, ResourceTable arsc, String resDir) {
        super(); /* Resources has a no-arg ctor in our shim. */
        mArsc = arsc;
        mResDir = resDir;

        /* Wire the arsc into the base Resources so getResourceTable()
         * returns it too — LayoutInflater strategies 2 & 3.5 read that. */
        try {
            if (arsc != null) {
                loadResourceTable(arsc);
            }
        } catch (Throwable ignored) {}
    }

    /** Lazily load the APK into an AssetManager. The cookie returned by
     * AssetManager.addAssetPath identifies the loaded APK in subsequent
     * openXmlResourceParser calls. Cached after first attempt. */
    private android.content.res.AssetManager ensureApkAssets() {
        if (mApkAssetsTried) return mApkAssets;
        synchronized (this) {
            if (mApkAssetsTried) return mApkAssets;
            mApkAssetsTried = true;
            try {
                String apkPath = WestlakeLauncher.currentApkPathForShim();
                if (apkPath == null || apkPath.isEmpty()) {
                    try {
                        android.util.Log.w("WestlakeVM:",
                                "WestlakeResources ensureApkAssets: no apk path");
                    } catch (Throwable ignored) {}
                    return null;
                }
                /* Allocate a fresh AssetManager via reflection; addAssetPath
                 * is hidden but reachable via reflection. */
                android.content.res.AssetManager am = new android.content.res.AssetManager();
                java.lang.reflect.Method add = android.content.res.AssetManager.class
                        .getDeclaredMethod("addAssetPath", String.class);
                add.setAccessible(true);
                Object res = add.invoke(am, apkPath);
                int cookie = res instanceof Integer ? (Integer) res : 0;
                if (cookie == 0) {
                    try {
                        android.util.Log.w("WestlakeVM:",
                                "WestlakeResources addAssetPath returned 0 for "
                                + apkPath);
                    } catch (Throwable ignored) {}
                    return null;
                }
                mApkAssets = am;
                mApkAssetsCookie = cookie;
                try {
                    android.util.Log.d("WestlakeVM:",
                            "WestlakeResources ensureApkAssets OK cookie="
                            + cookie + " path=" + apkPath);
                } catch (Throwable ignored) {}
                return am;
            } catch (Throwable t) {
                try {
                    android.util.Log.w("WestlakeVM:",
                            "WestlakeResources ensureApkAssets threw: "
                            + t.getClass().getSimpleName() + ": " + t.getMessage());
                } catch (Throwable ignored) {}
                return null;
            }
        }
    }

    /** Open a path relative to the APK as an XmlBlock$Parser via the
     * loaded AssetManager. This is the cleanest way to get a real
     * framework XmlBlock$Parser that ThemeImpl.obtainStyledAttributes
     * can cast successfully.
     *
     * Uses reflection since shim AssetManager doesn't declare
     * openXmlResourceParser at compile-time but the real framework one does. */
    private XmlResourceParser tryOpenViaAssetManager(int id, String path) {
        android.content.res.AssetManager am = ensureApkAssets();
        if (am == null) return null;
        try {
            java.lang.reflect.Method m = android.content.res.AssetManager.class
                    .getMethod("openXmlResourceParser", int.class, String.class);
            m.setAccessible(true);
            Object p = m.invoke(am, mApkAssetsCookie, path);
            if (p instanceof XmlResourceParser) {
                try {
                    android.util.Log.d("WestlakeVM:",
                            "WestlakeResources getLayout via AssetManager OK 0x"
                            + Integer.toHexString(id) + " -> " + path + " parser="
                            + p.getClass().getName());
                } catch (Throwable ignored) {}
                return (XmlResourceParser) p;
            }
        } catch (Throwable t) {
            try {
                Throwable root = t;
                while (root.getCause() != null && root.getCause() != root) root = root.getCause();
                android.util.Log.w("WestlakeVM:",
                        "WestlakeResources openXmlResourceParser failed for "
                        + path + ": " + t.getClass().getSimpleName() + ": "
                        + t.getMessage() + " root="
                        + root.getClass().getSimpleName() + ": " + root.getMessage());
            } catch (Throwable ignored) {}
        }
        return null;
    }

    @Override
    public XmlResourceParser getLayout(int id) {
        /* Strategy 0 (PREFERRED): get the layout via real AssetManager so
         * the returned parser IS an XmlBlock$Parser — required for the
         * framework inflater path which casts to that type during
         * obtainStyledAttributes. */
        String path = pathForId(id);
        if (path != null) {
            XmlResourceParser viaAssets = tryOpenViaAssetManager(id, path);
            if (viaAssets != null) return viaAssets;
        }

        /* Strategy 1: BinaryXmlParser fallback (works for our own shim
         * inflater path; NOT for the real framework inflater). */
        XmlResourceParser parser = tryGetLayoutFromArsc(id);
        if (parser != null) return parser;

        /* Strategy 2: fall back to base Resources.getLayout (handles
         * registered layout bytes and existing ResourceTable file-name
         * resolution). */
        try {
            XmlResourceParser fallback = super.getLayout(id);
            if (fallback != null) return fallback;
        } catch (Throwable t) {
            try {
                android.util.Log.w("WestlakeVM:", "WestlakeResources super.getLayout failed: "
                        + t.getClass().getSimpleName() + ": " + t.getMessage());
            } catch (Throwable ignored) {}
        }
        return null;
    }

    /** Return the path inside the APK for a layout id, or null. */
    private String pathForId(int id) {
        if (mArsc == null) return null;
        try {
            return mArsc.getEntryFilePath(id);
        } catch (Throwable t) {
            return null;
        }
    }

    private XmlResourceParser tryGetLayoutFromArsc(int id) {
        if (mArsc == null || mResDir == null) return null;
        String path;
        try {
            path = mArsc.getEntryFilePath(id);
        } catch (Throwable t) {
            return null;
        }
        if (path == null || path.isEmpty()) return null;

        File f = new File(mResDir, path);
        if (!f.exists() && path.startsWith("res/")) {
            f = new File(mResDir, path.substring(4));
        }
        if (!f.exists()) {
            try {
                android.util.Log.w("WestlakeVM:", "WestlakeResources getLayout 0x"
                        + Integer.toHexString(id) + " path " + path + " not on disk");
            } catch (Throwable ignored) {}
            return null;
        }
        byte[] data = readAll(f);
        if (data == null || data.length == 0) return null;

        try {
            BinaryXmlParser p = new BinaryXmlParser(data);
            try {
                android.util.Log.d("WestlakeVM:", "WestlakeResources getLayout OK 0x"
                        + Integer.toHexString(id) + " -> " + path + " ("
                        + data.length + " bytes)");
            } catch (Throwable ignored) {}
            return p;
        } catch (Throwable t) {
            try {
                android.util.Log.w("WestlakeVM:", "WestlakeResources BinaryXmlParser failed for 0x"
                        + Integer.toHexString(id) + " path=" + path + ": "
                        + t.getClass().getSimpleName() + ": " + t.getMessage());
            } catch (Throwable ignored) {}
            return null;
        }
    }

    @Override
    public XmlResourceParser getXml(int id) {
        /* AXML for non-layout resources (anim, drawable XML, raw XML) —
         * try the AssetManager route first so callers get XmlBlock$Parser. */
        String path = pathForId(id);
        if (path != null) {
            XmlResourceParser viaAssets = tryOpenViaAssetManager(id, path);
            if (viaAssets != null) return viaAssets;
        }
        XmlResourceParser p = tryGetLayoutFromArsc(id);
        if (p != null) return p;
        return super.getXml(id);
    }

    @Override
    public String getString(int id) {
        if (mArsc != null) {
            try {
                String s = mArsc.getString(id);
                if (s != null) return s;
            } catch (Throwable ignored) {}
        }
        try {
            return super.getString(id);
        } catch (Throwable t) {
            return "";
        }
    }

    @Override
    public String getString(int id, Object... formatArgs) {
        String template = getString(id);
        if (formatArgs == null || formatArgs.length == 0) return template;
        try {
            return android.text.format.SimpleFormatter.format(template, formatArgs);
        } catch (Throwable t) {
            return template;
        }
    }

    @Override
    public int getColor(int id) {
        if (mArsc != null) {
            try {
                int c = mArsc.getInteger(id, 0);
                if (c != 0) return c;
            } catch (Throwable ignored) {}
        }
        try {
            return super.getColor(id);
        } catch (Throwable t) {
            return 0;
        }
    }

    @Override
    public int getColor(int id, Theme theme) {
        return getColor(id);
    }

    /* PF-frag-lifecycle-res (2026-05-11): the framework View(Context) ctor
     * chain calls Resources.getDimensionPixelSize(android.R.dimen.config_*)
     * for things like scrollbar size, font padding, touch slop. Our
     * WestlakeResources doesn't have system framework-res.apk loaded, so
     * super.getDimensionPixelSize would throw NotFoundException and the
     * View ctor would propagate it.
     *
     * For these cases, return a sensible default per ID — never throw.
     * The IDs are 0x010xxxxx (package=1, framework). For app IDs (0x7fxxxxxx),
     * delegate to super. */
    @Override
    public int getDimensionPixelSize(int id) {
        try {
            return super.getDimensionPixelSize(id);
        } catch (Throwable t) {
            return defaultDimensionPx(id);
        }
    }

    @Override
    public int getDimensionPixelOffset(int id) {
        try {
            return super.getDimensionPixelOffset(id);
        } catch (Throwable t) {
            return defaultDimensionPx(id);
        }
    }

    @Override
    public float getDimension(int id) {
        try {
            return super.getDimension(id);
        } catch (Throwable t) {
            return defaultDimensionPx(id);
        }
    }

    /** Default pixel values for the framework dimens that View ctors load.
     * Picked to match Android defaults. */
    private int defaultDimensionPx(int id) {
        /* Common dimens by ID, derived from AOSP. */
        switch (id) {
            case 0x01050102: return 6;  /* config_scrollbarSize */
            case 0x01050006: return 6;  /* scrollbar_size */
            case 0x01050007: return 12; /* fading_edge_length */
            case 0x01050008: return 8;  /* touchSlop */
            default: break;
        }
        /* Other framework IDs: 0. App IDs: super already handled. */
        return 0;
    }

    /* PF-frag-lifecycle-res (2026-05-11): View(Context) ctor + super ctors
     * also read several integer/boolean/float framework configs via
     * Resources.getInteger / getBoolean / getFloat. ViewConfiguration is the
     * heaviest consumer. Wrap each so NotFoundException becomes a safe
     * default. */
    @Override
    public int getInteger(int id) {
        if (mArsc != null) {
            try {
                /* Only return arsc value if it really has the id (not 0 default). */
                int v = mArsc.getInteger(id, Integer.MIN_VALUE);
                if (v != Integer.MIN_VALUE) return v;
            } catch (Throwable ignored) {}
        }
        try {
            return super.getInteger(id);
        } catch (Throwable t) {
            return defaultIntegerFor(id);
        }
    }

    @Override
    public boolean getBoolean(int id) {
        try {
            if (mArsc != null) {
                int v = mArsc.getInteger(id, Integer.MIN_VALUE);
                if (v != Integer.MIN_VALUE) return v != 0;
            }
            return super.getBoolean(id);
        } catch (Throwable t) {
            /* Most boolean configs default to false; toggles that need
             * true would still be safer false (no scrollbars, no haptics). */
            return false;
        }
    }

    private int defaultIntegerFor(int id) {
        switch (id) {
            case 0x010e000d: return 1; /* config_longPressOnHomeBehavior */
            default: break;
        }
        return 0;
    }

    @Override
    public int getIdentifier(String name, String defType, String defPackage) {
        if (mArsc != null && name != null) {
            try {
                /* Build "type/name" key for arsc lookup. */
                String key;
                if (name.indexOf('/') >= 0) {
                    key = name;
                } else if (defType != null && defType.length() > 0) {
                    key = defType + "/" + name;
                } else {
                    key = name;
                }
                int id = mArsc.getIdentifier(key);
                if (id != 0) return id;
            } catch (Throwable ignored) {}
        }
        return super.getIdentifier(name, defType, defPackage);
    }

    @Override
    public String getResourceName(int id) {
        if (mArsc != null) {
            try {
                String n = mArsc.getResourceName(id);
                if (n != null) return n;
            } catch (Throwable ignored) {}
        }
        return super.getResourceName(id);
    }

    @Override
    public void getValue(int id, android.util.TypedValue outValue, boolean resolveRefs) {
        /* PF-frag-lifecycle-res: handle the common path inflate needs —
         * resolve @id/@string refs from arsc. Fall through to super for
         * unknown types so the base shim's TypedValue init still runs.
         *
         * Wrap super.getValue so a NotFoundException for an unknown
         * framework ID doesn't propagate (e.g. View ctor reads
         * 0x10500c8 = config_ambiguousGestureMultiplier as TypedValue —
         * we don't have that resource but a default 1.0f is fine). */
        boolean superOk = false;
        try {
            super.getValue(id, outValue, resolveRefs);
            superOk = true;
        } catch (Throwable t) {
            /* Fill outValue with a safe default. */
            if (outValue != null) {
                outValue.type = android.util.TypedValue.TYPE_FLOAT;
                outValue.data = Float.floatToIntBits(1.0f);
                outValue.resourceId = id;
                outValue.string = "";
            }
        }
        if (mArsc == null || outValue == null) return;
        /* Override the string for known types. */
        try {
            String s = mArsc.getString(id);
            if (s != null) {
                outValue.type = android.util.TypedValue.TYPE_STRING;
                outValue.string = s;
                return;
            }
        } catch (Throwable ignored) {}
        try {
            int n = mArsc.getInteger(id, Integer.MIN_VALUE);
            if (n != Integer.MIN_VALUE) {
                outValue.type = android.util.TypedValue.TYPE_INT_DEC;
                outValue.data = n;
            }
        } catch (Throwable ignored) {}
    }

    /* PF-frag-lifecycle-res (2026-05-11): also wrap the other commonly-used
     * resource accessors so View ctor chain doesn't throw on missing
     * framework resources. */
    @Override
    public android.graphics.drawable.Drawable getDrawable(int id) {
        try {
            return super.getDrawable(id);
        } catch (Throwable t) {
            return new android.graphics.drawable.ColorDrawable(0);
        }
    }

    @Override
    public android.graphics.drawable.Drawable getDrawable(int id, Theme theme) {
        try {
            return super.getDrawable(id, theme);
        } catch (Throwable t) {
            return new android.graphics.drawable.ColorDrawable(0);
        }
    }

    @Override
    public CharSequence getText(int id) {
        try {
            return super.getText(id);
        } catch (Throwable t) {
            if (mArsc != null) {
                try {
                    String s = mArsc.getString(id);
                    if (s != null) return s;
                } catch (Throwable ignored) {}
            }
            return "";
        }
    }

    @Override
    public int[] getIntArray(int id) {
        try {
            return super.getIntArray(id);
        } catch (Throwable t) {
            return new int[0];
        }
    }

    @Override
    public String[] getStringArray(int id) {
        try {
            return super.getStringArray(id);
        } catch (Throwable t) {
            return new String[0];
        }
    }

    private static byte[] readAll(File f) {
        try {
            long size = f.length();
            if (size <= 0 || size > 8 * 1024 * 1024) return null;
            byte[] buf = new byte[(int) size];
            FileInputStream fis = new FileInputStream(f);
            try {
                int off = 0;
                while (off < buf.length) {
                    int r = fis.read(buf, off, buf.length - off);
                    if (r < 0) break;
                    off += r;
                }
                return buf;
            } finally {
                try { fis.close(); } catch (java.io.IOException ignored) {}
            }
        } catch (Throwable t) {
            return null;
        }
    }
}
