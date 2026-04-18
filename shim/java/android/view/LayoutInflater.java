package android.view;
import android.content.Context;
import android.content.res.ApkResourceLoader;
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
    private static final int MCD_LAYOUT_ACTIVITY_HOME_DASHBOARD = 0x7f0e0058;
    private static final int MCD_LAYOUT_FRAGMENT_HOME_DASHBOARD = 0x7f0e027d;
    private static final int MCD_ID_INTERMEDIATE_LAYOUT_CONTAINER = 0x7f0b0b83;
    private static final int MCD_ID_HOME_DASHBOARD_CONTAINER = 0x7f0b0ae8;
    private static final int MCD_ID_SPLASH_CAMPAIGN_LAYOUT_CONTAINER = 0x7f0b17b1;
    private static final int MCD_ID_SPLASH_INTERRUPTER_LAYOUT_CONTAINER = 0x7f0b17b2;
    private static final int MCD_ID_COACHMARK_CONTAINER = 0x7f0b03f2;
    private static final int MCD_ID_NESTED_SCROLL_VIEW = 0x7f0b0f0b;
    private static final int MCD_ID_PARENT_CONTAINER = 0x7f0b11fa;
    private static final int MCD_ID_IMMERSIVE_CONTAINER = 0x7f0b0b68;
    private static final int MCD_ID_SECTIONS_CONTAINER = 0x7f0b16c5;
    private static final int MCD_COLOR_BG_WHITE = 0x7f060405;
    private static final int MCD_COLOR_GREY_BG = 0x7f060453;
    private static final int MCD_DIMEN_409 = 0x7f0704df;
    private static final int MCD_DIMEN_MINUS_139 = 0x7f07051c;

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
    // Short name -> shim class name (for unqualified tags like "LinearLayout")
    private static final HashMap sTagClassMap = new HashMap();
    // Fully-qualified name -> shim class name (for AndroidX, appcompat, etc.)
    private static final HashMap sFqnClassMap = new HashMap();
    static {
        // android.view.* classes
        sTagClassMap.put("View", "android.view.View");
        sTagClassMap.put("ViewGroup", "android.view.ViewGroup");
        sTagClassMap.put("SurfaceView", "android.view.SurfaceView");
        sTagClassMap.put("TextureView", "android.view.TextureView");
        sTagClassMap.put("ViewStub", "android.view.View");
        // android.widget.* classes
        sTagClassMap.put("LinearLayout", "android.widget.LinearLayout");
        sTagClassMap.put("RelativeLayout", "android.widget.RelativeLayout");
        sTagClassMap.put("FrameLayout", "android.widget.FrameLayout");
        sTagClassMap.put("ScrollView", "android.widget.ScrollView");
        sTagClassMap.put("HorizontalScrollView", "android.widget.ScrollView");
        sTagClassMap.put("TextView", "android.widget.TextView");
        sTagClassMap.put("Button", "android.widget.Button");
        sTagClassMap.put("EditText", "android.widget.EditText");
        sTagClassMap.put("ImageView", "android.widget.ImageView");
        sTagClassMap.put("ImageButton", "android.widget.ImageView");
        sTagClassMap.put("CheckBox", "android.widget.CheckBox");
        sTagClassMap.put("RadioButton", "android.widget.RadioButton");
        sTagClassMap.put("RadioGroup", "android.widget.LinearLayout");
        sTagClassMap.put("Switch", "android.widget.Switch");
        sTagClassMap.put("SeekBar", "android.widget.SeekBar");
        sTagClassMap.put("ProgressBar", "android.widget.ProgressBar");
        sTagClassMap.put("ListView", "android.widget.ListView");
        sTagClassMap.put("GridView", "android.widget.FrameLayout");
        sTagClassMap.put("Spinner", "android.widget.Spinner");
        sTagClassMap.put("WebView", "android.webkit.WebView");
        sTagClassMap.put("Space", "android.view.View");
        sTagClassMap.put("TableLayout", "android.widget.LinearLayout");
        sTagClassMap.put("TableRow", "android.widget.LinearLayout");
        // Placeholder tags (no-op)
        // AndroidX components
        sTagClassMap.put("androidx.recyclerview.widget.RecyclerView", "androidx.recyclerview.widget.RecyclerView");
        sTagClassMap.put("androidx.constraintlayout.widget.ConstraintLayout", "androidx.constraintlayout.widget.ConstraintLayout");
        sTagClassMap.put("androidx.cardview.widget.CardView", "androidx.cardview.widget.CardView");
        sTagClassMap.put("androidx.coordinatorlayout.widget.CoordinatorLayout", "android.widget.FrameLayout");
        sTagClassMap.put("androidx.appcompat.widget.Toolbar", "android.widget.Toolbar");
        sTagClassMap.put("com.google.android.material.appbar.AppBarLayout", "com.google.android.material.appbar.AppBarLayout");
        sTagClassMap.put("com.google.android.material.appbar.CollapsingToolbarLayout", "com.google.android.material.appbar.CollapsingToolbarLayout");
        sTagClassMap.put("com.google.android.material.floatingactionbutton.FloatingActionButton", "com.google.android.material.floatingactionbutton.FloatingActionButton");
        sTagClassMap.put("com.google.android.material.bottomnavigation.BottomNavigationView", "com.google.android.material.bottomnavigation.BottomNavigationView");

        sTagClassMap.put("include", null);
        sTagClassMap.put("merge", null);
        sTagClassMap.put("fragment", null);
        sTagClassMap.put("requestFocus", null);

        // AndroidX and support library fully-qualified names -> shim approximations
        // These are ViewGroups so children can be added to them
        sFqnClassMap.put("androidx.constraintlayout.widget.ConstraintLayout", "androidx.constraintlayout.widget.ConstraintLayout");
        sFqnClassMap.put("androidx.coordinatorlayout.widget.CoordinatorLayout", "android.widget.FrameLayout");
        sFqnClassMap.put("androidx.drawerlayout.widget.DrawerLayout", "android.support.v4.widget.DrawerLayout");
        sFqnClassMap.put("androidx.fragment.app.FragmentContainerView", "android.widget.FrameLayout");
        sFqnClassMap.put("androidx.viewpager.widget.ViewPager", "android.widget.FrameLayout");
        sFqnClassMap.put("androidx.viewpager2.widget.ViewPager2", "android.widget.FrameLayout");
        sFqnClassMap.put("androidx.recyclerview.widget.RecyclerView", "android.widget.FrameLayout");
        sFqnClassMap.put("androidx.cardview.widget.CardView", "android.widget.FrameLayout");
        sFqnClassMap.put("androidx.swiperefreshlayout.widget.SwipeRefreshLayout", "android.widget.FrameLayout");
        sFqnClassMap.put("androidx.core.widget.NestedScrollView", "android.widget.ScrollView");
        sFqnClassMap.put("androidx.appcompat.widget.Toolbar", "android.widget.FrameLayout");
        sFqnClassMap.put("androidx.appcompat.widget.AppCompatTextView", "android.widget.TextView");
        sFqnClassMap.put("androidx.appcompat.widget.AppCompatButton", "android.widget.Button");
        sFqnClassMap.put("androidx.appcompat.widget.AppCompatEditText", "android.widget.EditText");
        sFqnClassMap.put("androidx.appcompat.widget.AppCompatImageView", "android.widget.ImageView");
        sFqnClassMap.put("androidx.appcompat.widget.AppCompatCheckBox", "android.widget.CheckBox");
        sFqnClassMap.put("androidx.appcompat.widget.LinearLayoutCompat", "android.widget.LinearLayout");
        sFqnClassMap.put("com.google.android.material.appbar.AppBarLayout", "android.widget.LinearLayout");
        sFqnClassMap.put("com.google.android.material.appbar.CollapsingToolbarLayout", "android.widget.FrameLayout");
        sFqnClassMap.put("com.google.android.material.appbar.MaterialToolbar", "android.widget.FrameLayout");
        sFqnClassMap.put("com.google.android.material.bottomnavigation.BottomNavigationView", "com.google.android.material.bottomnavigation.BottomNavigationView");
        sFqnClassMap.put("com.google.android.material.floatingactionbutton.FloatingActionButton", "android.widget.ImageView");
        sFqnClassMap.put("com.airbnb.lottie.LottieAnimationView", "com.airbnb.lottie.LottieAnimationView");
        sFqnClassMap.put("com.google.android.material.textfield.TextInputLayout", "android.widget.LinearLayout");
        sFqnClassMap.put("com.google.android.material.textfield.TextInputEditText", "android.widget.EditText");
        sFqnClassMap.put("com.google.android.material.button.MaterialButton", "android.widget.Button");
        sFqnClassMap.put("com.google.android.material.card.MaterialCardView", "android.widget.FrameLayout");
        sFqnClassMap.put("com.google.android.material.tabs.TabLayout", "android.widget.FrameLayout");
        // Old support library names
        sFqnClassMap.put("android.support.v4.widget.NestedScrollView", "android.widget.ScrollView");
        sFqnClassMap.put("android.support.v4.widget.SwipeRefreshLayout", "android.widget.FrameLayout");
        sFqnClassMap.put("android.support.v4.widget.DrawerLayout", "android.support.v4.widget.DrawerLayout");
        sFqnClassMap.put("android.support.v7.widget.RecyclerView", "android.widget.FrameLayout");
        sFqnClassMap.put("android.support.v7.widget.Toolbar", "android.widget.FrameLayout");
        sFqnClassMap.put("android.support.v7.widget.CardView", "android.widget.FrameLayout");
        sFqnClassMap.put("android.support.design.widget.CoordinatorLayout", "android.widget.FrameLayout");
        sFqnClassMap.put("android.support.design.widget.AppBarLayout", "android.widget.LinearLayout");
        sFqnClassMap.put("android.support.design.widget.FloatingActionButton", "android.widget.ImageView");
        sFqnClassMap.put("android.support.design.widget.TabLayout", "android.widget.FrameLayout");
    }

    /**
     * Create a View from an XML tag name.
     * Handles fully-qualified class names ("com.example.MyView"),
     * short Android names ("LinearLayout" -> android.widget.LinearLayout),
     * AndroidX/appcompat names (mapped to shim approximations),
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

        if (tagName.contains(".")) {
            // Fully-qualified name: check FQN map first, then try direct instantiation
            Object mapped = sFqnClassMap.get(tagName);
            if (mapped != null) {
                fullName = (String) mapped;
            } else {
                fullName = tagName;
            }
        } else {
            // Short name: look up in the tag map
            Object mapped = sTagClassMap.get(tagName);
            if (mapped != null) {
                fullName = (String) mapped;
            } else {
                // Default: try android.widget.* then android.view.*
                fullName = "android.widget." + tagName;
            }
        }

        // Try to instantiate the view class
        View view = tryInstantiate(fullName);
        if (view != null) {
            if (fullName.contains("McDToolBar")) {
                System.err.println("[LayoutInflater] McDToolBarView instantiated OK: " + view.getClass().getName());
            }
            return view;
        }
        if (fullName.contains("McDToolBar")) {
            System.err.println("[LayoutInflater] McDToolBarView tryInstantiate returned null!");
        }

        // For short names, also try android.view.* prefix
        if (!tagName.contains(".") && fullName.startsWith("android.widget.")) {
            view = tryInstantiate("android.view." + tagName);
            if (view != null) return view;
        }

        // For fully-qualified names that failed and aren't in the FQN map,
        // try to guess a suitable ViewGroup/View approximation from the short name
        if (tagName.contains(".")) {
            String shortName = tagName.substring(tagName.lastIndexOf('.') + 1);
            // Try to find the superclass and use it as fallback
            try {
                Class<?> cls = Class.forName(fullName);
                // Walk up the class hierarchy to find a known View subclass we can instantiate
                Class<?> sc = cls.getSuperclass();
                while (sc != null && sc != Object.class && sc != View.class) {
                    view = tryInstantiate(sc.getName());
                    if (view != null) {
                        System.err.println("[LayoutInflater] " + tagName + " -> fallback to " + sc.getSimpleName());
                        return view;
                    }
                    sc = sc.getSuperclass();
                }
            } catch (ClassNotFoundException cnf) { /* not on classpath */ }
            // If the short name ends in "Layout", "View", etc., approximate
            if (shortName.endsWith("Layout") || shortName.contains("Container")
                    || shortName.contains("Pager") || shortName.endsWith("Group")) {
                return new FrameLayout(mContext);
            }
            if (shortName.endsWith("Button")) {
                view = tryInstantiate("android.widget.Button");
                if (view != null) return view;
            }
            if (shortName.endsWith("TextView") || shortName.endsWith("Text")) {
                view = tryInstantiate("android.widget.TextView");
                if (view != null) return view;
            }
            if (shortName.endsWith("ImageView") || shortName.endsWith("Image")) {
                view = tryInstantiate("android.widget.ImageView");
                if (view != null) return view;
            }
            if (shortName.endsWith("EditText") || shortName.endsWith("Input")) {
                view = tryInstantiate("android.widget.EditText");
                if (view != null) return view;
            }
            // Last resort for fully-qualified: a FrameLayout (so children can be added)
            return new FrameLayout(mContext);
        }

        return null;
    }

    /**
     * Try to instantiate a View class by name, attempting no-arg and Context constructors.
     * Returns null if the class can't be found or instantiated.
     */
    private View tryInstantiate(String className) {
        try {
            if ("com.mcdonalds.mcduikit.widget.McDToolBarView".equals(className)) {
                return new com.mcdonalds.mcduikit.widget.McDToolBarView(mContext);
            }
            if ("com.mcdonalds.mcduikit.widget.McDTextView".equals(className)) {
                return new com.mcdonalds.mcduikit.widget.McDTextView(mContext);
            }
            if ("com.mcdonalds.mcduikit.widget.McDAppCompatTextView".equals(className)) {
                return new com.mcdonalds.mcduikit.widget.McDAppCompatTextView(mContext);
            }
            if ("com.mcdonalds.mcduikit.widget.ProgressStateTracker".equals(className)) {
                return new com.mcdonalds.mcduikit.widget.ProgressStateTracker(mContext);
            }
            ClassLoader cl = mContext != null ? mContext.getClass().getClassLoader() : null;
            Class cls = cl != null ? Class.forName(className, true, cl) : Class.forName(className);
            // Prefer Context-only constructors first. In the standalone shim runtime,
            // invoking (Context, AttributeSet) with a synthetic/null AttributeSet sends
            // framework widgets through fragile TypedArray code paths.
            try {
                return (View) cls.getConstructor(Context.class).newInstance(mContext);
            } catch (Throwable e1) {
                // Fall back to Context + AttributeSet when the widget requires it.
                try {
                    return (View) cls.getConstructor(Context.class, android.util.AttributeSet.class)
                        .newInstance(mContext, null);
                } catch (Throwable e2) {
                    // Try no-arg constructor
                    try {
                        return (View) cls.getConstructor().newInstance();
                    } catch (Throwable e3) {
                        if (className.contains("mcdonalds") || className.contains("material") || className.contains("McDToolBar") || className.contains("constraint") || className.contains("Constraint")) {
                            Throwable c1 = e1.getCause() != null ? e1.getCause() : e1;
                            Throwable c2 = e2.getCause() != null ? e2.getCause() : e2;
                            System.err.println("[LayoutInflater] tryInstantiate FAILED: " + className
                                + "\n  ctx: " + c1.getClass().getSimpleName() + ": " + c1.getMessage()
                                + "\n  ctx+attr: " + c2.getClass().getSimpleName() + ": " + c2.getMessage());
                        }
                        return null;
                    }
                }
            }
        } catch (Throwable e) {
            if (className.contains("McDToolBar")) {
                System.err.println("[LayoutInflater] tryInstantiate ClassNotFound: " + className + ": " + e);
            }
            return null;
        }
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

    private int getColorCompat(int colorId, int fallbackColor) {
        if (mContext == null) return fallbackColor;
        try {
            Resources res = mContext.getResources();
            if (res != null) {
                return res.getColor(colorId);
            }
        } catch (Throwable ignored) {
        }
        return fallbackColor;
    }

    private int getDimenCompat(int dimenId, int fallbackPx) {
        if (mContext == null) return fallbackPx;
        try {
            Resources res = mContext.getResources();
            if (res != null) {
                return res.getDimensionPixelOffset(dimenId);
            }
        } catch (Throwable ignored) {
        }
        return fallbackPx;
    }

    private View maybeBuildKnownMcdLayout(int resource) {
        if (resource == MCD_LAYOUT_ACTIVITY_HOME_DASHBOARD) {
            return buildMcdActivityHomeDashboard();
        }
        if (resource == MCD_LAYOUT_FRAGMENT_HOME_DASHBOARD) {
            return buildMcdFragmentHomeDashboard();
        }
        return null;
    }

    private void addCompatChild(ViewGroup parent, View child) {
        if (parent == null || child == null) {
            return;
        }
        ViewGroup.LayoutParams lp;
        if (parent instanceof LinearLayout) {
            lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        } else if (parent instanceof android.widget.FrameLayout) {
            lp = new android.widget.FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        } else if (parent instanceof android.widget.RelativeLayout) {
            lp = new android.widget.RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        parent.addView(child, lp);
    }

    private View buildMcdActivityHomeDashboard() {
        LinearLayout root = new LinearLayout(mContext);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getColorCompat(MCD_COLOR_BG_WHITE, 0xFFFFFFFF));

        android.widget.FrameLayout frame = new android.widget.FrameLayout(mContext);

        LinearLayout intermediate = new LinearLayout(mContext);
        intermediate.setOrientation(LinearLayout.VERTICAL);
        intermediate.setId(MCD_ID_INTERMEDIATE_LAYOUT_CONTAINER);
        intermediate.setVisibility(View.VISIBLE);
        addCompatChild(frame, intermediate);

        LinearLayout home = new LinearLayout(mContext);
        home.setOrientation(LinearLayout.VERTICAL);
        home.setId(MCD_ID_HOME_DASHBOARD_CONTAINER);
        home.setVisibility(View.GONE);
        addCompatChild(frame, home);

        LinearLayout splashCampaign = new LinearLayout(mContext);
        splashCampaign.setOrientation(LinearLayout.VERTICAL);
        splashCampaign.setId(MCD_ID_SPLASH_CAMPAIGN_LAYOUT_CONTAINER);
        splashCampaign.setVisibility(View.GONE);
        addCompatChild(frame, splashCampaign);

        LinearLayout splashInterrupter = new LinearLayout(mContext);
        splashInterrupter.setOrientation(LinearLayout.VERTICAL);
        splashInterrupter.setId(MCD_ID_SPLASH_INTERRUPTER_LAYOUT_CONTAINER);
        splashInterrupter.setVisibility(View.GONE);
        addCompatChild(frame, splashInterrupter);

        LinearLayout coachmark = new LinearLayout(mContext);
        coachmark.setOrientation(LinearLayout.VERTICAL);
        coachmark.setId(MCD_ID_COACHMARK_CONTAINER);
        coachmark.setVisibility(View.GONE);
        addCompatChild(frame, coachmark);

        addCompatChild(root, frame);
        return root;
    }

    private View buildMcdFragmentHomeDashboard() {
        android.widget.ScrollView scroll = new android.widget.ScrollView(mContext);
        scroll.setId(MCD_ID_NESTED_SCROLL_VIEW);
        scroll.setBackgroundColor(getColorCompat(MCD_COLOR_GREY_BG, 0xFFF5F5F5));
        scroll.setFillViewport(true);

        LinearLayout parent = new LinearLayout(mContext);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setId(MCD_ID_PARENT_CONTAINER);
        parent.setFocusable(false);

        android.widget.RelativeLayout immersive = new android.widget.RelativeLayout(mContext);
        immersive.setId(MCD_ID_IMMERSIVE_CONTAINER);
        immersive.setVisibility(View.GONE);
        immersive.setMinimumHeight(getDimenCompat(MCD_DIMEN_409, 409));
        addCompatChild(parent, immersive);

        LinearLayout sections = new LinearLayout(mContext);
        sections.setOrientation(LinearLayout.VERTICAL);
        sections.setId(MCD_ID_SECTIONS_CONTAINER);
        addCompatChild(parent, sections);

        addCompatChild(scroll, parent);
        return scroll;
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
     * Tries five strategies in order:
     * 1. Programmatic layout registry (ViewFactory)
     * 2. Registered layout bytes via Resources.getLayoutBytes()
     * 3. Binary layout XML from extracted APK res/ directory
     * 4. Binary layout XML read directly from APK ZIP
     * 5. Fallback: empty FrameLayout with resource ID
     *
     * Strategies 2-4 parse AXML into a BinaryXmlParser and delegate to
     * inflate(XmlPullParser, ...) for proper View creation, attribute
     * application, and LayoutParams generation.
     */
    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        View view = null;
        boolean canUseRealInflaterShortcut = (root == null && !attachToRoot);

        // 0. Try real Android LayoutInflater (app_process64 mode)
        Object realCtx = com.westlake.engine.WestlakeLauncher.sRealContext;
        if (canUseRealInflaterShortcut
                && com.westlake.engine.WestlakeLauncher.isRealFrameworkFallbackAllowed()
                && realCtx != null) {
            try {
                java.lang.reflect.Method fromMethod = LayoutInflater.class.getMethod("from", android.content.Context.class);
                Object realInflater = fromMethod.invoke(null, realCtx);
                if (realInflater != null && realInflater != this) {
                    java.lang.reflect.Method inflateMethod = realInflater.getClass().getMethod("inflate", int.class, ViewGroup.class, boolean.class);
                    view = (View) inflateMethod.invoke(realInflater, resource, null, false);
                    if (view != null) {
                        System.err.println("[LayoutInflater] Real inflate OK: 0x" + Integer.toHexString(resource) + " → " + view.getClass().getSimpleName());
                        if (attachToRoot && root != null) { root.addView(view); return root; }
                        return view;
                    }
                }
            } catch (Throwable t) {
                System.err.println("[LayoutInflater] Real inflate failed: " + t.getMessage());
            }
        }

        // 1. Check programmatic layout registry first
        Object factoryObj = sLayoutRegistry.get(Integer.valueOf(resource));
        if (factoryObj != null) {
            try {
                ViewFactory factory = (ViewFactory) factoryObj;
                view = factory.createView(mContext, root);
                if (view != null) {
                    if (attachToRoot && root != null) {
                        root.addView(view);
                        return root;
                    }
                    return view;
                }
            } catch (Throwable t) {
                try {
                    android.util.Log.w("LayoutInflater",
                            "Registered layout factory failed for 0x"
                                    + Integer.toHexString(resource) + ": "
                                    + t.getClass().getSimpleName() + ": " + t.getMessage());
                } catch (Throwable ignored) {
                }
            }
        }

        View knownMcdLayout = maybeBuildKnownMcdLayout(resource);
        if (knownMcdLayout != null) {
            if (attachToRoot && root != null) {
                addCompatChild(root, knownMcdLayout);
                return root;
            }
            return knownMcdLayout;
        }

        // Strategies 2-4: obtain binary AXML bytes, then inflate via XmlPullParser
        byte[] axmlData = null;
        try {
            System.err.println("[LayoutInflater] inflate(0x" + Integer.toHexString(resource) + ")");
        } catch (Throwable ignored) {
        }

        // 2. Try registered layout bytes
        if (axmlData == null && mContext != null) {
            try {
                android.content.res.Resources res = mContext.getResources();
                if (res != null) {
                    axmlData = res.getLayoutBytes(resource);
                    if (axmlData != null) {
                        System.err.println("[LayoutInflater] Strategy 2: getLayoutBytes OK (" + axmlData.length + " bytes)");
                    }
                }
            } catch (Throwable t) {
                try {
                    android.util.Log.w("LayoutInflater", "Strategy 2 failed for 0x"
                            + Integer.toHexString(resource) + ": "
                            + t.getClass().getSimpleName() + ": " + t.getMessage());
                } catch (Throwable ignored) {
                }
            }
        }

        // 3. Try loading from extracted APK res/ directory via ResourceTable
        if (axmlData == null && mContext != null) {
            try {
                android.content.res.Resources res = mContext.getResources();
                if (res != null) {
                    android.content.res.ResourceTable table = res.getResourceTable();
                    if (table != null) {
                        String layoutFile = table.getLayoutFileName(resource);
                        try {
                            // Keep resource-table diagnostics from aborting shim-only inflates.
                            String dbgStr = table.getString(resource);
                            String dbgName = table.getResourceName(resource);
                            System.err.println("[LayoutInflater] Strategy 3: table=true"
                                    + " layoutFile=" + layoutFile
                                    + " str=" + dbgStr + " name=" + dbgName
                                    + " entries=" + table.getStringCount());
                        } catch (Throwable ignored) {
                        }
                        if (layoutFile != null) {
                            axmlData = loadLayoutXml(layoutFile);
                            if (axmlData != null) {
                                System.err.println("[LayoutInflater] Strategy 3: OK (" + axmlData.length + " bytes)");
                            }
                        }
                    } else {
                        System.err.println("[LayoutInflater] Strategy 3: no ResourceTable");
                    }
                }
            } catch (Throwable t) {
                try {
                    android.util.Log.w("LayoutInflater", "Strategy 3 failed for 0x"
                            + Integer.toHexString(resource) + ": "
                            + t.getClass().getSimpleName() + ": " + t.getMessage());
                } catch (Throwable ignored) {
                }
            }
        }

        // 3.5: Try loading from pre-extracted res directory using resource table entry value
        if (axmlData == null && mContext != null) {
            try {
                String resDir = System.getProperty("westlake.apk.resdir");
                if (resDir != null) {
                    android.content.res.Resources res2 = mContext.getResources();
                    android.content.res.ResourceTable table2 = (res2 != null) ? res2.getResourceTable() : null;
                    if (table2 != null) {
                        // The resources.arsc maps resId -> string pool entry containing the file path
                        // Try getEntryValue which reads the raw Res_value string
                        String filePath = table2.getEntryFilePath(resource);
                        if (filePath != null) {
                            java.io.File f = new java.io.File(resDir, filePath);
                            if (f.exists()) {
                                try {
                                    java.io.FileInputStream fis = new java.io.FileInputStream(f);
                                    axmlData = new byte[(int) f.length()];
                                    fis.read(axmlData);
                                    fis.close();
                                    System.err.println("[LayoutInflater] Strategy 3.5: loaded " + filePath + " (" + axmlData.length + " bytes)");
                                } catch (Exception e) {
                                    System.err.println("[LayoutInflater] Strategy 3.5: read failed: " + e.getMessage());
                                }
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                try {
                    android.util.Log.w("LayoutInflater", "Strategy 3.5 failed for 0x"
                            + Integer.toHexString(resource) + ": "
                            + t.getClass().getSimpleName() + ": " + t.getMessage());
                } catch (Throwable ignored) {
                }
            }
        }

        // 4. Try reading directly from APK ZIP file
        if (axmlData == null && mContext != null) {
            try {
                android.content.res.Resources res = mContext.getResources();
                if (res != null) {
                    String apkPath = null;
                    try { apkPath = (String) res.getClass().getMethod("getApkPath").invoke(res); } catch (Exception e) {}
                    System.err.println("[LayoutInflater] Strategy 4: apkPath=" + apkPath);
                    axmlData = ApkResourceLoader.loadLayout(res, resource);
                    if (axmlData != null) {
                        System.err.println("[LayoutInflater] Strategy 4: OK (" + axmlData.length + " bytes)");
                    }
                }
            } catch (Throwable t) {
                try {
                    android.util.Log.w("LayoutInflater", "Strategy 4 failed for 0x"
                            + Integer.toHexString(resource) + ": "
                            + t.getClass().getSimpleName() + ": " + t.getMessage());
                } catch (Throwable ignored) {
                }
            }
        }

        // Parse AXML bytes via BinaryXmlParser and inflate using the XmlPullParser path
        if (axmlData != null && axmlData.length > 0) {
            try {
                BinaryXmlParser parser = new BinaryXmlParser(axmlData);
                view = inflate(parser, root, attachToRoot);
                // inflate(XmlPullParser,...) already handles attachToRoot,
                // so return the result directly
                if (view != null) {
                    System.err.println("[LayoutInflater] Inflated 0x"
                            + Integer.toHexString(resource)
                            + " -> " + describeViewTree(view, 0));
                    return view;
                }
            } catch (Throwable e) {
                System.err.println("[LayoutInflater] AXML parse failed for 0x"
                        + Integer.toHexString(resource) + ": " + e.getClass().getSimpleName() + ": " + e.getMessage());
                e.printStackTrace(System.out);
            }
            if (view == null) {
                System.err.println("[LayoutInflater] inflate(parser) returned null for 0x" + Integer.toHexString(resource));
            }
        }

        // 5. Fallback: visible placeholder with app info
        if (view == null) {
            android.widget.LinearLayout ll = new android.widget.LinearLayout(mContext);
            ll.setOrientation(android.widget.LinearLayout.VERTICAL);
            ll.setId(resource);
            ll.setBackgroundColor(0xFFFFCC00); // McDonald's yellow
            ll.setPadding(20, 60, 20, 20);

            android.widget.TextView title = new android.widget.TextView(mContext);
            title.setText("McDonald's");
            title.setTextSize(28);
            title.setTextColor(0xFFDA291C); // McDonald's red
            title.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
            ll.addView(title);

            android.widget.TextView sub = new android.widget.TextView(mContext);
            sub.setText("Running on Westlake Engine");
            sub.setTextSize(14);
            sub.setTextColor(0xFF333333);
            sub.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
            ll.addView(sub);

            android.widget.TextView status = new android.widget.TextView(mContext);
            String pkg = System.getProperty("westlake.apk.package", "");
            String act = System.getProperty("westlake.apk.activity", "");
            if (act.contains(".")) act = act.substring(act.lastIndexOf('.') + 1);
            status.setText(pkg + "\n" + act + "\nLayout: 0x" + Integer.toHexString(resource));
            status.setTextSize(12);
            status.setTextColor(0xFF666666);
            status.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
            status.setPadding(0, 30, 0, 0);
            ll.addView(status);

            view = ll;
        }

        if (root != null && attachToRoot) {
            try {
                root.addView(view);
                return root;
            } catch (Throwable addRootError) {
                String detail = "[LayoutInflater] final attach failed: root="
                        + root.getClass().getSimpleName() + " child="
                        + view.getClass().getSimpleName() + " err="
                        + addRootError.getClass().getSimpleName() + ": "
                        + addRootError.getMessage();
                System.err.println(detail);
                try {
                    android.util.Log.w("LayoutInflater", detail);
                } catch (Throwable ignored) {
                }
            }
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

                // Set result early so even if children fail, we return the root
                result = rootView;

                // Inflate children if this is a ViewGroup
                if (rootView instanceof ViewGroup) {
                    try {
                        inflateChildren(parser, (ViewGroup) rootView);
                    } catch (Throwable childEx) {
                        System.err.println("[LayoutInflater] inflateChildren error: " + childEx.getClass().getSimpleName() + ": " + childEx.getMessage());
                    }
                } else {
                    // Skip to matching END_TAG
                    skipToEndTag(parser);
                }

                // Attach to root or set layout params
                if (root != null) {
                    if (attachToRoot) {
                        try {
                            if (params != null) {
                                root.addView(rootView, params);
                            } else {
                                root.addView(rootView);
                            }
                            result = root;
                        } catch (Throwable addRootError) {
                            String detail = "[LayoutInflater] root addView failed: root="
                                    + root.getClass().getSimpleName() + " child="
                                    + rootView.getClass().getSimpleName() + " params="
                                    + (params != null ? params.getClass().getSimpleName() : "null")
                                    + " err=" + addRootError.getClass().getSimpleName() + ": "
                                    + addRootError.getMessage();
                            System.err.println(detail);
                            try {
                                android.util.Log.w("LayoutInflater", detail);
                            } catch (Throwable ignored) {
                            }
                            try {
                                root.addView(rootView);
                                result = root;
                            } catch (Throwable fallbackAddError) {
                                String fallbackDetail = "[LayoutInflater] root fallback addView failed: root="
                                        + root.getClass().getSimpleName() + " child="
                                        + rootView.getClass().getSimpleName() + " err="
                                        + fallbackAddError.getClass().getSimpleName() + ": "
                                        + fallbackAddError.getMessage();
                                System.err.println(fallbackDetail);
                                try {
                                    android.util.Log.w("LayoutInflater", fallbackDetail);
                                } catch (Throwable ignored) {
                                }
                                result = rootView;
                            }
                        }
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
        } catch (Throwable e) {
            System.err.println("[LayoutInflater] inflate(parser) error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
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
            System.err.println("[LayoutInflater] child tag: <" + tagName + "> in " + parent.getClass().getSimpleName() + " (depth=" + parser.getDepth() + ")");

            // Handle <include> tag
            if ("include".equals(tagName)) {
                handleInclude(parser, parent);
                continue;
            }

            // Handle <requestFocus>
            if ("requestFocus".equals(tagName)) {
                skipToEndTag(parser);
                continue;
            }

            // Handle <fragment> — create Fragment and add its view to parent
            if ("fragment".equals(tagName) || "FragmentContainerView".equals(tagName)
                    || "androidx.fragment.app.FragmentContainerView".equals(tagName)) {
                handleFragmentTag(parser, parent);
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
            try {
                if (params != null) {
                    parent.addView(child, params);
                } else {
                    parent.addView(child);
                }
            } catch (Throwable addViewError) {
                String detail = "[LayoutInflater] addView failed: tag=<" + tagName + "> parent="
                        + parent.getClass().getSimpleName() + " child="
                        + child.getClass().getSimpleName() + " params="
                        + (params != null ? params.getClass().getSimpleName() : "null")
                        + " err=" + addViewError.getClass().getSimpleName() + ": "
                        + addViewError.getMessage();
                System.err.println(detail);
                try {
                    android.util.Log.w("LayoutInflater", detail);
                } catch (Throwable ignored) {
                }
                // Best-effort inflate for unstable standalone ART paths: keep the
                // parent tree alive even if one child cannot attach correctly.
                continue;
            }
        }
    }

    /**
     * Handle an <include> tag by inflating the referenced layout.
     */
    /**
     * Handle &lt;fragment&gt; or &lt;FragmentContainerView&gt; tag.
     */
    private void handleFragmentTag(XmlPullParser parser, ViewGroup parent) throws Exception {
        String fragmentClass = null;
        int viewId = View.NO_ID;

        if (parser instanceof BinaryXmlParser) {
            BinaryXmlParser bxp = (BinaryXmlParser) parser;
            int count = bxp.getAttributeCount();
            for (int i = 0; i < count; i++) {
                String name = bxp.getAttributeName(i);
                if ("name".equals(name) || "class".equals(name)) {
                    fragmentClass = bxp.getAttributeValue(i);
                } else if ("id".equals(name)) {
                    viewId = bxp.getAttributeResourceValue(i, View.NO_ID);
                }
            }
        }

        System.err.println("[LayoutInflater] <fragment> class=" + fragmentClass + " id=0x" + Integer.toHexString(viewId));

        // Create a FrameLayout container for the fragment
        android.widget.FrameLayout container = new android.widget.FrameLayout(mContext);
        if (viewId != View.NO_ID) container.setId(viewId);

        // Try to instantiate the Fragment and call onCreateView
        if (fragmentClass != null) {
            try {
                ClassLoader cl = mContext.getClassLoader();
                if (cl == null) cl = Thread.currentThread().getContextClassLoader();
                Class<?> fragCls = cl.loadClass(fragmentClass);
                Object fragment = fragCls.newInstance();

                if (fragment instanceof androidx.fragment.app.Fragment) {
                    androidx.fragment.app.Fragment f = (androidx.fragment.app.Fragment) fragment;
                    if (mContext instanceof androidx.fragment.app.FragmentActivity) {
                        f.mActivity = (androidx.fragment.app.FragmentActivity) mContext;
                    }
                    try { f.onCreate(null); } catch (Throwable t) {
                        System.err.println("[LayoutInflater] Fragment.onCreate: " + t.getMessage());
                    }
                    try {
                        View fragView = f.onCreateView(this, container, null);
                        if (fragView != null) {
                            container.addView(fragView);
                            f.mView = fragView;
                            System.err.println("[LayoutInflater] Fragment view: " + fragView.getClass().getSimpleName());
                        }
                    } catch (Throwable t) {
                        System.err.println("[LayoutInflater] Fragment.onCreateView: " + t.getMessage());
                    }
                }
            } catch (Throwable t) {
                System.err.println("[LayoutInflater] Fragment error: " + t.getMessage());
            }
        }

        parent.addView(container);
        skipToEndTag(parser);
    }

    private void handleInclude(XmlPullParser parser, ViewGroup parent) throws Exception {
        // Log all attributes for debugging
        if (parser instanceof BinaryXmlParser) {
            BinaryXmlParser bxp2 = (BinaryXmlParser) parser;
            StringBuilder sb = new StringBuilder("[LayoutInflater] <include> attrs:");
            for (int j = 0; j < bxp2.getAttributeCount(); j++) {
                sb.append(" ").append(bxp2.getAttributeName(j)).append("=").append(bxp2.getAttributeValue(j));
            }
            System.err.println(sb.toString());
        }
        // Look for android:layout attribute (layout resource reference)
        if (parser instanceof BinaryXmlParser) {
            BinaryXmlParser bxp = (BinaryXmlParser) parser;
            int count = bxp.getAttributeCount();
            for (int i = 0; i < count; i++) {
                String name = bxp.getAttributeName(i);
                int nsRes = 0;
                try { nsRes = bxp.getAttributeNameResource(i); } catch (Throwable t) {}
                if ("layout".equals(name) || nsRes == 0x010100f2) {
                    int resId = bxp.getAttributeResourceValue(i, 0);
                    System.err.println("[LayoutInflater] <include> attr=" + name + " nsRes=0x" + Integer.toHexString(nsRes) + " layout=0x" + Integer.toHexString(resId));
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
    private static final int ATTR_TAG            = 0x01010003;
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
    private static final int ATTR_SRC            = 0x01010119; // android:src
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

            // Check for tag by name too (some AXML uses name-based attrs)
            if ("tag".equals(attrName) && resId != ATTR_TAG) {
                String tv = resolveStringAttr(bxp, i, res);
                if (tv == null) tv = bxp.getAttributeValue(i);
                if (tv != null) {
                    view.setTag(tv);
                    if (tv.startsWith("layout/")) {
                        System.err.println("[LayoutInflater] DATA BINDING TAG (by name): " + tv + " on " + view.getClass().getSimpleName());
                    }
                }
            }

            switch (resId) {
                case ATTR_ID:
                    view.setId(attrData);
                    break;

                case ATTR_TAG:
                    // Data binding sets android:tag on views (e.g., "layout/base_layout_0")
                    String tagStr = resolveStringAttr(bxp, i, res);
                    if (tagStr == null) {
                        tagStr = bxp.getAttributeValue(i);
                    }
                    if (tagStr != null) {
                        view.setTag(tagStr);
                        if (tagStr.startsWith("layout/")) {
                            System.err.println("[LayoutInflater] DATA BINDING TAG: " + tagStr + " on " + view.getClass().getSimpleName());
                        }
                    }
                    break;

                case ATTR_TEXT:
                    if (view instanceof TextView) {
                        String text = resolveStringAttr(bxp, i, res);
                        if (text != null) {
                            if (text.length() > 100) {
                                System.err.println("[LayoutInflater] WARNING: huge text (" + text.length() + " chars): " + text.substring(0, 80) + "...");
                            }
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

                case ATTR_SRC:
                    // android:src for ImageView — attrType 0x01 = reference
                    if (view instanceof android.widget.ImageView && attrType == 0x01 && attrData != 0) {
                        ((android.widget.ImageView) view).setImageResource(attrData);
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
        } else if (("src".equals(name) || "srcCompat".equals(name)) && view instanceof android.widget.ImageView) {
            if (type == 0x01 && data != 0) { // reference
                ((android.widget.ImageView) view).setImageResource(data);
            }
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
    // layout attribute IDs
    private static final int ATTR_LAYOUT_GRAVITY = 0x010100b3;
    private static final int ATTR_LAYOUT_ALIGN_PARENT_LEFT = 0x0101018b;
    private static final int ATTR_LAYOUT_ALIGN_PARENT_TOP = 0x0101018c;
    private static final int ATTR_LAYOUT_ALIGN_PARENT_RIGHT = 0x0101018d;
    private static final int ATTR_LAYOUT_ALIGN_PARENT_BOTTOM = 0x0101018e;
    private static final int ATTR_LAYOUT_CENTER_IN_PARENT = 0x0101018f;
    private static final int ATTR_LAYOUT_CENTER_HORIZONTAL = 0x01010190;
    private static final int ATTR_LAYOUT_CENTER_VERTICAL = 0x01010191;

    private ViewGroup.LayoutParams generateLayoutParams(ViewGroup parent, XmlPullParser parser) {
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        float weight = 0f;
        int layoutGravity = -1;
        int marginAll = Integer.MIN_VALUE;
        int marginLeft = Integer.MIN_VALUE;
        int marginTop = Integer.MIN_VALUE;
        int marginRight = Integer.MIN_VALUE;
        int marginBottom = Integer.MIN_VALUE;
        Resources res = (mContext != null) ? mContext.getResources() : null;

        if (parser instanceof BinaryXmlParser) {
            BinaryXmlParser bxp = (BinaryXmlParser) parser;
            int count = bxp.getAttributeCount();
            // First pass: explicit layout_gravity
            for (int i = 0; i < count; i++) {
                int resId = bxp.getAttributeNameResource(i);
                String attrName = bxp.getAttributeName(i);
                int type = bxp.getAttributeValueType(i);
                int data = bxp.getAttributeValueData(i);

                if (resId == ATTR_LAYOUT_WIDTH) {
                    width = resolveLayoutDimensionValue(type, data, res);
                } else if (resId == ATTR_LAYOUT_HEIGHT) {
                    height = resolveLayoutDimensionValue(type, data, res);
                } else if (resId == ATTR_LAYOUT_WEIGHT) {
                    weight = Float.intBitsToFloat(data);
                } else if (resId == ATTR_LAYOUT_GRAVITY) {
                    layoutGravity = data;
                } else if ("layout_margin".equals(attrName)) {
                    marginAll = resolveLayoutDimensionValue(type, data, res);
                } else if ("layout_marginLeft".equals(attrName)
                        || "layout_marginStart".equals(attrName)) {
                    marginLeft = resolveLayoutDimensionValue(type, data, res);
                } else if ("layout_marginTop".equals(attrName)) {
                    marginTop = resolveLayoutDimensionValue(type, data, res);
                } else if ("layout_marginRight".equals(attrName)
                        || "layout_marginEnd".equals(attrName)) {
                    marginRight = resolveLayoutDimensionValue(type, data, res);
                } else if ("layout_marginBottom".equals(attrName)) {
                    marginBottom = resolveLayoutDimensionValue(type, data, res);
                }
            }
            // Second pass: map RelativeLayout attrs to gravity (for FrameLayout fallback)
            if (layoutGravity == -1) {
                int g = 0;
                for (int i = 0; i < count; i++) {
                    int resId = bxp.getAttributeNameResource(i);
                    int data = bxp.getAttributeValueData(i);
                    if (data == 0) continue; // false (true = -1 or nonzero)
                    if (resId == ATTR_LAYOUT_CENTER_IN_PARENT) g = 0x11; // CENTER
                    else if (resId == ATTR_LAYOUT_ALIGN_PARENT_TOP) g |= 0x30; // TOP
                    else if (resId == ATTR_LAYOUT_ALIGN_PARENT_BOTTOM) g |= 0x50; // BOTTOM
                    else if (resId == ATTR_LAYOUT_ALIGN_PARENT_LEFT) g |= 0x03; // LEFT
                    else if (resId == ATTR_LAYOUT_ALIGN_PARENT_RIGHT) g |= 0x05; // RIGHT
                    else if (resId == ATTR_LAYOUT_CENTER_HORIZONTAL) g |= 0x01; // CENTER_HORIZONTAL
                    else if (resId == ATTR_LAYOUT_CENTER_VERTICAL) g |= 0x10; // CENTER_VERTICAL
                }
                if (g != 0) layoutGravity = g;
                // Debug: log unrecognized layout attributes
                if (g == 0) {
                    for (int i = 0; i < count; i++) {
                        int resId = bxp.getAttributeNameResource(i);
                        String name = bxp.getAttributeName(i);
                        if (name != null && name.startsWith("layout_") && resId != ATTR_LAYOUT_WIDTH
                                && resId != ATTR_LAYOUT_HEIGHT && resId != ATTR_LAYOUT_WEIGHT) {
                            System.err.println("[LayoutInflater] unhandled attr: " + name + " resId=0x" + Integer.toHexString(resId) + " data=" + bxp.getAttributeValueData(i));
                        }
                    }
                }
            }
        }

        if (parent instanceof LinearLayout) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
            lp.weight = weight;
            if (layoutGravity >= 0) lp.gravity = layoutGravity;
            applyMargins(lp, marginAll, marginLeft, marginTop, marginRight, marginBottom);
            return lp;
        }

        if (parent instanceof android.widget.FrameLayout) {
            android.widget.FrameLayout.LayoutParams lp =
                new android.widget.FrameLayout.LayoutParams(width, height);
            if (layoutGravity >= 0) lp.gravity = layoutGravity;
            applyMargins(lp, marginAll, marginLeft, marginTop, marginRight, marginBottom);
            return lp;
        }

        if (parent instanceof android.widget.RelativeLayout && parser instanceof BinaryXmlParser) {
            android.widget.RelativeLayout.LayoutParams lp =
                new android.widget.RelativeLayout.LayoutParams(width, height);
            BinaryXmlParser bxp = (BinaryXmlParser) parser;
            int count = bxp.getAttributeCount();
            for (int i = 0; i < count; i++) {
                int resId = bxp.getAttributeNameResource(i);
                int data = bxp.getAttributeValueData(i);
                // Map RelativeLayout rule attributes
                switch (resId) {
                    case 0x0101018b: lp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_LEFT, data != 0 ? -1 : 0); break;
                    case 0x0101018c: lp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_TOP, data != 0 ? -1 : 0); break;
                    case 0x0101018d: lp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT, data != 0 ? -1 : 0); break;
                    case 0x0101018e: lp.addRule(android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM, data != 0 ? -1 : 0); break;
                    case 0x0101018f: lp.addRule(android.widget.RelativeLayout.CENTER_IN_PARENT, data != 0 ? -1 : 0); break;
                    case 0x01010190: lp.addRule(android.widget.RelativeLayout.CENTER_HORIZONTAL, data != 0 ? -1 : 0); break;
                    case 0x01010191: lp.addRule(android.widget.RelativeLayout.CENTER_VERTICAL, data != 0 ? -1 : 0); break;
                    case 0x01010182: lp.addRule(android.widget.RelativeLayout.BELOW, data); break;
                    case 0x01010183: lp.addRule(android.widget.RelativeLayout.ALIGN_LEFT, data); break;
                    case 0x01010184: lp.addRule(android.widget.RelativeLayout.ABOVE, data); break;
                    case 0x01010185: lp.addRule(android.widget.RelativeLayout.ALIGN_RIGHT, data); break;
                    case 0x01010186: lp.addRule(android.widget.RelativeLayout.ALIGN_TOP, data); break;
                    case 0x01010187: lp.addRule(android.widget.RelativeLayout.ALIGN_BOTTOM, data); break;
                    case 0x01010188: lp.addRule(android.widget.RelativeLayout.LEFT_OF, data); break;
                    case 0x01010189: lp.addRule(android.widget.RelativeLayout.RIGHT_OF, data); break;
                }
            }
            applyMargins(lp, marginAll, marginLeft, marginTop, marginRight, marginBottom);
            return lp;
        }

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(width, height);
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            applyMargins((ViewGroup.MarginLayoutParams) lp,
                    marginAll, marginLeft, marginTop, marginRight, marginBottom);
        }
        return lp;
    }

    private void applyMargins(ViewGroup.MarginLayoutParams lp, int all, int left, int top, int right, int bottom) {
        if (lp == null) return;
        if (all != Integer.MIN_VALUE) {
            lp.setMargins(all, all, all, all);
        }
        int resolvedLeft = left != Integer.MIN_VALUE ? left : lp.leftMargin;
        int resolvedTop = top != Integer.MIN_VALUE ? top : lp.topMargin;
        int resolvedRight = right != Integer.MIN_VALUE ? right : lp.rightMargin;
        int resolvedBottom = bottom != Integer.MIN_VALUE ? bottom : lp.bottomMargin;
        lp.setMargins(resolvedLeft, resolvedTop, resolvedRight, resolvedBottom);
    }

    private int resolveLayoutDimensionValue(int type, int data, Resources res) {
        if (type == 0x01 && res != null && data != 0) {
            try {
                return res.getDimensionPixelOffset(data);
            } catch (Throwable ignored) {
            }
        }
        return resolveLayoutDimension(data);
    }

    /**
     * Resolve a layout dimension value: MATCH_PARENT (-1), WRAP_CONTENT (-2), or px.
     */
    private int resolveLayoutDimension(int data) {
        if (data == -1 || data == -2) {
            return data; // MATCH_PARENT or WRAP_CONTENT
        }
        // Unpack dimension: low 4 bits = unit type, high bits = value (complex format)
        // Android complex dimension: bits 0-3 = unit (0=px, 1=dp, 2=sp, 3=pt, 4=in, 5=mm)
        //                            bits 4-7 = radix (0=23p0, 1=16p7, 2=8p15, 3=0p23)
        //                            bits 8-31 = mantissa
        int unitType = data & 0xF;
        int radix = (data >> 4) & 0x3;
        int mantissa = data >> 8;
        float value;
        switch (radix) {
            case 0: value = mantissa; break;              // 23p0
            case 1: value = mantissa / 128.0f; break;     // 16p7
            case 2: value = mantissa / 32768.0f; break;   // 8p15
            case 3: value = mantissa / 8388608.0f; break;  // 0p23
            default: value = mantissa; break;
        }
        float density = android.content.res.Resources.getSystem().getDisplayMetrics().density;
        switch (unitType) {
            case 0: return (int) value;                   // px
            case 1: return (int) (value * density + 0.5f); // dp
            case 2: return (int) (value * density + 0.5f); // sp (treat as dp for layout)
            default: return (int) (value * density + 0.5f); // fallback: treat as dp
        }
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

                System.err.println("[LayoutInflater] loadLayoutXml: resDir=" + resDir + " path=" + layoutPath);
                if (resDir != null) {
                    java.io.File xmlFile = new java.io.File(resDir, layoutPath);
                    System.err.println("[LayoutInflater] loadLayoutXml: trying " + xmlFile.getAbsolutePath() + " exists=" + xmlFile.exists());
                    if (!xmlFile.exists()) {
                        xmlFile = new java.io.File(resDir, layoutPath.startsWith("res/") ? layoutPath.substring(4) : layoutPath);
                        System.err.println("[LayoutInflater] loadLayoutXml: trying " + xmlFile.getAbsolutePath() + " exists=" + xmlFile.exists());
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

    /**
     * Build a compact description of a View tree for diagnostics.
     * Example: "FrameLayout(id=0x7f0a004b)[TextView, Button]"
     */
    private static String describeViewTree(View view, int depth) {
        if (view == null) return "null";
        if (depth > 4) return "..."; // prevent infinite recursion

        StringBuilder sb = new StringBuilder();
        sb.append(view.getClass().getSimpleName());
        int id = view.getId();
        if (id != 0 && id != View.NO_ID) {
            sb.append("(id=0x");
            sb.append(Integer.toHexString(id));
            sb.append(')');
        }

        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            int count = vg.getChildCount();
            if (count > 0) {
                sb.append('[');
                for (int i = 0; i < count; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(describeViewTree(vg.getChildAt(i), depth + 1));
                }
                sb.append(']');
            }
        }
        return sb.toString();
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
