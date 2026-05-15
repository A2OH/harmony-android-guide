/*
 * PF-frag-lifecycle-ctx (2026-05-11): Context wrapper that returns a
 * WestlakeResources backed by the noice arsc + extracted res dir.
 *
 * Built to unblock LayoutInflater.inflate(R.layout.fragment_home, ...)
 * inside Fragment.onCreateView. The default LayoutInflater shim chain
 * calls mContext.getResources().getResourceTable() to find layout files
 * — but the standalone-dalvikvm host Activity's Resources doesn't know
 * about the launched APK's arsc. WestlakeContext.getResources() returns
 * a Resources subclass that DOES know.
 *
 * Generic by design: takes packageName as ctor arg (typically the
 * launched apk's android:package), takes the parsed ResourceTable +
 * resDir as ctor args. No per-app symbol hardcoding.
 */
package com.westlake.engine;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ResourceTable;
import android.content.res.Resources;

public final class WestlakeContext extends ContextWrapper {

    private final Activity mHostActivity;
    private final ResourceTable mArsc;
    private final String mResDir;
    private final String mPackageName;
    private WestlakeResources mResources;

    /** Build a Westlake context wrapping the host Activity.
     *
     * @param hostActivity the standalone-dalvikvm host Activity (provides
     *                     base AssetManager/DisplayMetrics/Configuration
     *                     for the Resources super-ctor).
     * @param arsc         the parsed noice ResourceTable (resource lookups
     *                     delegate here for getString/getInteger/getLayout).
     * @param resDir       absolute path to the extracted res directory
     *                     (e.g. "/data/local/tmp/westlake/PKG_res/res/"
     *                     parent), used to load AXML files for layout
     *                     resource IDs.
     * @param packageName  the launched APK's package name (from manifest,
     *                     or Activity.getPackageName()).
     */
    public WestlakeContext(Activity hostActivity, ResourceTable arsc,
            String resDir, String packageName) {
        super(hostActivity);
        mHostActivity = hostActivity;
        mArsc = arsc;
        mResDir = resDir;
        mPackageName = packageName;
    }

    @Override
    public Resources getResources() {
        if (mResources == null) {
            try {
                mResources = new WestlakeResources(mHostActivity, mArsc, mResDir);
            } catch (Throwable t) {
                /* Fall back to parent's resources if we can't construct ours. */
                try {
                    android.util.Log.w("WestlakeVM:", "WestlakeContext getResources fallback: "
                            + t.getClass().getSimpleName() + ": " + t.getMessage());
                } catch (Throwable ignored) {}
                return mHostActivity != null ? mHostActivity.getResources() : super.getResources();
            }
        }
        return mResources;
    }

    @Override
    public String getPackageName() {
        if (mPackageName != null) return mPackageName;
        try {
            return mHostActivity != null ? mHostActivity.getPackageName() : super.getPackageName();
        } catch (Throwable t) {
            return mPackageName;
        }
    }

    @Override
    public Context getApplicationContext() {
        /* Return self so app code stays on this context. */
        return this;
    }

    @Override
    public ClassLoader getClassLoader() {
        try {
            if (mHostActivity != null) {
                ClassLoader cl = mHostActivity.getClass().getClassLoader();
                if (cl != null) return cl;
            }
        } catch (Throwable ignored) {}
        return WestlakeContext.class.getClassLoader();
    }

    private volatile android.content.res.Resources.Theme mCachedTheme;

    @Override
    public android.content.res.Resources.Theme getTheme() {
        android.content.res.Resources.Theme cached = mCachedTheme;
        if (cached != null) return cached;
        synchronized (this) {
            if (mCachedTheme != null) return mCachedTheme;
            mCachedTheme = buildUsableTheme();
            return mCachedTheme;
        }
    }

    /**
     * PF-frag-lifecycle-theme (2026-05-11): build a Theme whose internal
     * `mLock` (synchronized block target) and `mThemeImpl` (delegate for
     * obtainStyledAttributes/resolveAttribute) are both non-null.
     *
     * Strategy: prefer a Theme created by a real, fully-initialized
     * Resources (sRealContext or Resources.getSystem). Those Themes are
     * built by the framework's Resources.newTheme() so internals are
     * correct. Fall back to Unsafe-allocating a Theme and reflectively
     * setting mLock + mThemeImpl from a backing ResourcesImpl.
     */
    private android.content.res.Resources.Theme buildUsableTheme() {
        /* Strategy 1: getResources().newTheme() — if our WestlakeResources
         * inherited a usable mResourcesImpl from super(), this is the
         * cleanest path. */
        try {
            android.content.res.Resources res = getResources();
            if (res != null && hasResourcesImpl(res)) {
                android.content.res.Resources.Theme t = res.newTheme();
                if (themeIsUsable(t)) {
                    logTheme("getResources().newTheme()", t);
                    return t;
                }
            }
        } catch (Throwable t) {
            logTheme("getResources().newTheme() failed: " + t.getClass().getSimpleName() + " " + t.getMessage(), null);
        }

        /* Strategy 2: WestlakeLauncher.sRealContext (real ContextImpl from
         * ActivityThread.getSystemContext). Its Resources is fully wired
         * with mResourcesImpl. */
        try {
            Object realCtx = WestlakeLauncher.sRealContext;
            if (realCtx instanceof Context) {
                android.content.res.Resources realRes = ((Context) realCtx).getResources();
                if (realRes != null) {
                    android.content.res.Resources.Theme t = realRes.newTheme();
                    if (themeIsUsable(t)) {
                        logTheme("sRealContext.getResources().newTheme()", t);
                        return t;
                    }
                }
            }
        } catch (Throwable t) {
            logTheme("sRealContext path failed: " + t.getClass().getSimpleName() + " " + t.getMessage(), null);
        }

        /* Strategy 3: Resources.getSystem().newTheme(). */
        try {
            android.content.res.Resources sys = android.content.res.Resources.getSystem();
            if (sys != null) {
                android.content.res.Resources.Theme t = sys.newTheme();
                if (themeIsUsable(t)) {
                    logTheme("Resources.getSystem().newTheme()", t);
                    return t;
                }
            }
        } catch (Throwable t) {
            logTheme("Resources.getSystem().newTheme() failed: "
                    + t.getClass().getSimpleName() + " " + t.getMessage(), null);
        }

        /* Strategy 4: host activity's theme — only if its internals are non-null. */
        try {
            if (mHostActivity != null) {
                android.content.res.Resources.Theme t = mHostActivity.getTheme();
                if (themeIsUsable(t)) {
                    logTheme("mHostActivity.getTheme()", t);
                    return t;
                }
                /* It exists but is broken — repair it. */
                if (t != null && repairThemeInPlace(t)) {
                    logTheme("mHostActivity.getTheme() repaired", t);
                    return t;
                }
            }
        } catch (Throwable t) {
            logTheme("mHostActivity.getTheme() failed: "
                    + t.getClass().getSimpleName() + " " + t.getMessage(), null);
        }

        /* Strategy 5: Unsafe-allocate a Theme and reflectively fill internals. */
        try {
            android.content.res.Resources.Theme t = unsafeAllocateTheme();
            if (t != null && repairThemeInPlace(t)) {
                logTheme("Unsafe.allocate + repair", t);
                return t;
            }
        } catch (Throwable t) {
            logTheme("Unsafe path failed: " + t.getClass().getSimpleName() + " " + t.getMessage(), null);
        }

        /* Last resort: return an unrepaired Theme. obtainStyledAttributes
         * will still NPE but at least we don't return null (which would
         * NPE in Context.obtainStyledAttributes anyway). */
        return new android.content.res.Resources.Theme();
    }

    private static boolean hasResourcesImpl(android.content.res.Resources res) {
        try {
            java.lang.reflect.Field f = android.content.res.Resources.class.getDeclaredField("mResourcesImpl");
            f.setAccessible(true);
            return f.get(res) != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean themeIsUsable(android.content.res.Resources.Theme t) {
        if (t == null) return false;
        try {
            java.lang.reflect.Field lockF = t.getClass().getDeclaredField("mLock");
            lockF.setAccessible(true);
            if (lockF.get(t) == null) return false;
        } catch (Throwable ignored) {
            return false;
        }
        try {
            java.lang.reflect.Field implF = t.getClass().getDeclaredField("mThemeImpl");
            implF.setAccessible(true);
            return implF.get(t) != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** Try to fill in mLock + mThemeImpl on an existing Theme. Used when
     * we get a Theme back from somewhere but its internals are partially
     * null. */
    private static boolean repairThemeInPlace(android.content.res.Resources.Theme t) {
        if (t == null) return false;
        try {
            /* mLock: just needs to be a non-null Object. */
            java.lang.reflect.Field lockF = t.getClass().getDeclaredField("mLock");
            lockF.setAccessible(true);
            if (lockF.get(t) == null) {
                lockF.set(t, new Object());
            }
        } catch (Throwable ignored) {}
        try {
            /* mThemeImpl: build a fresh ResourcesImpl$ThemeImpl from any
             * accessible ResourcesImpl. The mThemeImpl is what
             * obtainStyledAttributes delegates to. */
            java.lang.reflect.Field implF = t.getClass().getDeclaredField("mThemeImpl");
            implF.setAccessible(true);
            if (implF.get(t) == null) {
                Object themeImpl = buildThemeImplFromAnyResources();
                if (themeImpl != null) {
                    implF.set(t, themeImpl);
                }
            }
            return implF.get(t) != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** Walk the well-known Resources sources looking for one with a non-null
     * mResourcesImpl, then call ResourcesImpl.newThemeImpl() to get a fresh
     * ThemeImpl. */
    private static Object buildThemeImplFromAnyResources() {
        Object[] candidates = new Object[] {
                WestlakeLauncher.sRealContext,
                null /* placeholder for getSystem */,
        };
        try {
            candidates[1] = android.content.res.Resources.getSystem();
        } catch (Throwable ignored) {}
        for (Object o : candidates) {
            try {
                android.content.res.Resources res = null;
                if (o instanceof Context) {
                    res = ((Context) o).getResources();
                } else if (o instanceof android.content.res.Resources) {
                    res = (android.content.res.Resources) o;
                }
                if (res == null) continue;
                java.lang.reflect.Field f = android.content.res.Resources.class.getDeclaredField("mResourcesImpl");
                f.setAccessible(true);
                Object impl = f.get(res);
                if (impl == null) continue;
                java.lang.reflect.Method m = impl.getClass().getDeclaredMethod("newThemeImpl");
                m.setAccessible(true);
                Object themeImpl = m.invoke(impl);
                if (themeImpl != null) return themeImpl;
            } catch (Throwable ignored) { /* try next */ }
        }
        return null;
    }

    private static android.content.res.Resources.Theme unsafeAllocateTheme() {
        try {
            Class<?> uc = Class.forName("sun.misc.Unsafe");
            java.lang.reflect.Field f = uc.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Object unsafe = f.get(null);
            java.lang.reflect.Method m = uc.getMethod("allocateInstance", Class.class);
            return (android.content.res.Resources.Theme)
                    m.invoke(unsafe, android.content.res.Resources.Theme.class);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static void logTheme(String tag, android.content.res.Resources.Theme t) {
        try {
            android.util.Log.d("WestlakeVM:", "WestlakeContext theme: " + tag
                    + " usable=" + (t != null && themeIsUsable(t)));
        } catch (Throwable ignored) {}
    }

    @Override
    public Object getSystemService(String name) {
        if (Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
            /* android.view.LayoutInflater is abstract in framework.jar so
             * `new LayoutInflater(this)` throws InstantiationError. We need
             * a concrete subclass: try the standard PhoneLayoutInflater via
             * reflection. If that fails fall back to a per-activity cached
             * one (some standalone Activities are missing Window so their
             * getLayoutInflater throws AbstractMethodError). */
            android.view.LayoutInflater li = makeConcreteLayoutInflater();
            if (li != null) return li;
            return null;
        }
        try {
            if (mHostActivity != null) return mHostActivity.getSystemService(name);
        } catch (Throwable ignored) {}
        return super.getSystemService(name);
    }

    private static volatile Class<?> sPhoneLayoutInflaterCls;
    private static volatile java.lang.reflect.Constructor<?> sPhoneLayoutInflaterCtor;

    /** Construct a concrete LayoutInflater bound to this context.
     *
     * PF-frag-lifecycle-inflate (2026-05-11): PREFER WestlakeLayoutInflater,
     * which uses our BinaryXmlParser + View(Context) ctor chain. This
     * bypasses framework.jar's native-dependent inflate path (which
     * requires libandroid_runtime.so for XmlBlock$Parser construction).
     * Fragment.onCreateView's `inflater.inflate(int, root, attach)` calls
     * land here, so we control the entire inflate logic. */
    private android.view.LayoutInflater makeConcreteLayoutInflater() {
        /* Strategy 0 (PREFERRED): our own LayoutInflater that uses
         * BinaryXmlParser + framework View(Context) ctors. */
        try {
            ClassLoader appCl = mHostActivity != null
                    ? mHostActivity.getClass().getClassLoader()
                    : WestlakeContext.class.getClassLoader();
            WestlakeLayoutInflater wli = new WestlakeLayoutInflater(this,
                    mArsc, mResDir, appCl);
            return wli;
        } catch (Throwable t) {
            try {
                android.util.Log.w("WestlakeVM:",
                        "WestlakeContext WestlakeLayoutInflater ctor failed: "
                                + t.getClass().getSimpleName() + ": " + t.getMessage());
            } catch (Throwable ignored) {}
        }
        /* Strategy 1: framework's PhoneLayoutInflater (will hit native NPEs). */
        try {
            if (sPhoneLayoutInflaterCtor == null) {
                Class<?> cls = Class.forName("com.android.internal.policy.PhoneLayoutInflater");
                java.lang.reflect.Constructor<?> ctor = cls.getConstructor(Context.class);
                ctor.setAccessible(true);
                sPhoneLayoutInflaterCls = cls;
                sPhoneLayoutInflaterCtor = ctor;
            }
            Object inst = sPhoneLayoutInflaterCtor.newInstance(this);
            if (inst instanceof android.view.LayoutInflater) {
                return (android.view.LayoutInflater) inst;
            }
        } catch (Throwable t) {
            try {
                android.util.Log.w("WestlakeVM:",
                        "WestlakeContext PhoneLayoutInflater reflection failed: "
                                + t.getClass().getSimpleName() + ": " + t.getMessage());
            } catch (Throwable ignored) {}
        }
        /* Fallback: try the activity's inflater + cloneInContext. */
        try {
            if (mHostActivity != null) {
                android.view.LayoutInflater base = mHostActivity.getLayoutInflater();
                if (base != null) {
                    try {
                        android.view.LayoutInflater clone = base.cloneInContext(this);
                        if (clone != null) return clone;
                    } catch (Throwable ignored) {}
                    return base;
                }
            }
        } catch (Throwable ignored) {}
        return null;
    }

    /** Test/diagnostic accessor — the underlying arsc this context exposes. */
    public ResourceTable getArsc() { return mArsc; }

    /** Test/diagnostic accessor — the extracted-res directory. */
    public String getResDir() { return mResDir; }
}
