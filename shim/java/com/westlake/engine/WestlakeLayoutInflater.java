/*
 * PF-frag-lifecycle-inflate (2026-05-11): Concrete LayoutInflater
 * subclass that bypasses framework.jar's native-dependent inflate path.
 *
 * Why this exists:
 *   framework.jar's LayoutInflater.inflate(int, ViewGroup, boolean)
 *   ultimately calls Resources$Theme.obtainStyledAttributes which casts
 *   the AttributeSet to android.content.res.XmlBlock$Parser (final, native-
 *   backed). Our BinaryXmlParser is a shim XmlResourceParser, not an
 *   XmlBlock$Parser, so the cast fails. Worse, the native ThemeImpl path
 *   would need libandroid_runtime.so which our standalone dalvikvm host
 *   doesn't load.
 *
 *   This subclass overrides inflate(int, ViewGroup, boolean) to:
 *     1. Resolve the layout id to a binary AXML file path via the arsc.
 *     2. Walk the AXML with our BinaryXmlParser (already in shim).
 *     3. Instantiate framework View classes via the View(Context) ctor —
 *        the no-arg form which does NOT call obtainStyledAttributes.
 *     4. Apply common attrs (text, textColor, textSize, layout_width,
 *        layout_height, background, gravity, padding) programmatically.
 *     5. Recurse into ViewGroup children.
 *
 *   Output is a real framework View tree that
 *   Fragment.onCreateView can return.
 *
 * Generic by design: no hardcoded per-app view class names.
 */
package com.westlake.engine;

import android.content.Context;
import android.content.res.BinaryXmlParser;
import android.content.res.ResourceTable;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.xmlpull.v1.XmlPullParser;

public final class WestlakeLayoutInflater extends LayoutInflater {

    private final Context mWlContext;
    private final ResourceTable mArsc;
    private final String mResDir;
    private final ClassLoader mAppClassLoader;

    private static final String[] FALLBACK_PACKAGES = new String[] {
            "android.widget.",
            "android.view.",
            "android.webkit.",
            "com.google.android.material.button.",
            "com.google.android.material.textview.",
            "com.google.android.material.textfield.",
            "com.google.android.material.card.",
            "com.google.android.material.bottomnavigation.",
            "com.google.android.material.appbar.",
            "com.google.android.material.imageview.",
            "androidx.appcompat.widget.",
            "androidx.constraintlayout.widget.",
            "androidx.coordinatorlayout.widget.",
            "androidx.recyclerview.widget.",
            "androidx.fragment.app.",
            "androidx.viewpager2.widget.",
    };

    public WestlakeLayoutInflater(Context context, ResourceTable arsc, String resDir,
            ClassLoader appCl) {
        super(context);
        mWlContext = context;
        mArsc = arsc;
        mResDir = resDir;
        mAppClassLoader = appCl != null ? appCl : context.getClassLoader();
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return new WestlakeLayoutInflater(newContext, mArsc, mResDir, mAppClassLoader);
    }

    /** Override the standard inflate(int, ViewGroup, boolean) entry point.
     * Note: this is NOT @Override of an abstract method (LayoutInflater
     * provides a default impl) but covers the call site noice's
     * onCreateView uses. */
    @Override
    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        log("inflate(int) id=0x" + Integer.toHexString(resource)
                + " root=" + (root != null ? root.getClass().getSimpleName() : "null")
                + " attach=" + attachToRoot);
        byte[] axml = readAxmlForId(resource);
        if (axml == null) {
            log("inflate(int) NO AXML for 0x" + Integer.toHexString(resource));
            /* Defer to super so the framework error path at least has a
             * chance — but it'll likely fail too. */
            return null;
        }
        View root2 = parseAndBuild(axml, root, attachToRoot);
        log("inflate(int) returning "
                + (root2 != null ? root2.getClass().getSimpleName() : "null"));
        return root2;
    }

    @Override
    public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        if (!(parser instanceof BinaryXmlParser)) {
            log("inflate(parser) non-BinaryXmlParser type=" + parser.getClass().getName());
            try {
                return super.inflate(parser, root, attachToRoot);
            } catch (Throwable t) {
                log("inflate(parser) super failed: " + describeThrow(t));
                return null;
            }
        }
        try {
            return buildFromParser((BinaryXmlParser) parser, root, attachToRoot);
        } catch (Throwable t) {
            log("inflate(parser) buildFromParser threw: " + describeThrow(t));
            return null;
        }
    }

    private byte[] readAxmlForId(int id) {
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
            log("readAxmlForId no file for 0x" + Integer.toHexString(id) + " path=" + path);
            return null;
        }
        return readAll(f);
    }

    private View parseAndBuild(byte[] axml, ViewGroup root, boolean attachToRoot) {
        try {
            BinaryXmlParser p = new BinaryXmlParser(axml);
            return buildFromParser(p, root, attachToRoot);
        } catch (Throwable t) {
            log("parseAndBuild threw: " + describeThrow(t));
            return null;
        }
    }

    private View buildFromParser(BinaryXmlParser parser, ViewGroup root, boolean attachToRoot)
            throws Exception {
        View rootView = null;
        View current = null;
        java.util.ArrayDeque<View> stack = new java.util.ArrayDeque<View>();
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                String tag = parser.getName();
                View v = createViewForTag(tag);
                if (v == null) {
                    /* Use a generic FrameLayout as fallback to keep the
                     * tree structure intact. */
                    v = newFallbackView();
                }
                applyAttrs(v, parser);
                if (rootView == null) {
                    rootView = v;
                } else if (current instanceof ViewGroup) {
                    /* Try regular addView first. If it throws (often
                     * because ViewGroup.addView triggers
                     * accessibility/autofill checks that need native
                     * libs we don't have), fall back to direct
                     * reflection on mChildren/mChildrenCount. */
                    boolean added = false;
                    try {
                        ((ViewGroup) current).addView(v);
                        added = true;
                    } catch (Throwable t) {
                        /* Skip noisy log — fallback path is normal here. */
                    }
                    if (!added) {
                        try {
                            attachChildReflective((ViewGroup) current, v);
                            added = true;
                        } catch (Throwable t) {
                            log("attachChildReflective threw for " + tag + ": "
                                    + describeThrow(t));
                        }
                    }
                }
                stack.push(v);
                current = v;
            } else if (event == XmlPullParser.END_TAG) {
                if (!stack.isEmpty()) stack.pop();
                current = stack.isEmpty() ? null : stack.peek();
            }
            event = parser.next();
        }
        if (rootView != null && root != null && attachToRoot) {
            try {
                root.addView(rootView);
            } catch (Throwable t) {
                log("attach root.addView threw: " + describeThrow(t));
            }
        }
        return rootView;
    }

    private View createViewForTag(String tag) {
        if (tag == null) return null;
        Class<?> cls = resolveViewClass(tag);
        if (cls != null) {
            View v = tryConstruct(cls, tag);
            if (v != null) return v;
        }
        /* Fallback chain: if the requested view class can't be constructed
         * (often due to native resources missing), substitute a basic
         * framework view that has a (Context) ctor we know works. This
         * keeps the tree structure usable for our renderer even if the
         * exact widget class is lost. */
        View fallback = substituteForTag(tag);
        if (fallback != null) {
            log("createViewForTag " + tag + " substituted with "
                    + fallback.getClass().getSimpleName());
            return fallback;
        }
        return null;
    }

    private View tryConstruct(Class<?> cls, String tag) {
        try {
            Constructor<?> ctor = cls.getConstructor(Context.class);
            ctor.setAccessible(true);
            Object inst = ctor.newInstance(mWlContext);
            if (inst instanceof View) return (View) inst;
        } catch (Throwable t) {
            /* Some custom views only have (Context, AttributeSet) ctor. */
            try {
                Constructor<?> ctor = cls.getConstructor(Context.class, AttributeSet.class);
                ctor.setAccessible(true);
                Object inst = ctor.newInstance(mWlContext, (AttributeSet) null);
                if (inst instanceof View) return (View) inst;
            } catch (Throwable t2) {
                log("tryConstruct " + tag + " ctor failed: " + describeThrow(t2));
                /* Last resort: Unsafe-allocate and reflectively set mContext.
                 * This makes the view "alive" enough for the rendering
                 * walk (getContext, getResources, getChildCount) but skips
                 * any internal state that obtainStyledAttributes would
                 * have set. Sufficient for our headless render. */
                try {
                    Object inst = allocateViewUnsafe(cls);
                    if (inst instanceof View) {
                        seedMinimalViewFields((View) inst);
                        log("tryConstruct " + tag + " Unsafe-allocated");
                        return (View) inst;
                    }
                } catch (Throwable t3) {
                    log("tryConstruct " + tag + " Unsafe-allocate failed: "
                            + describeThrow(t3));
                }
            }
        }
        return null;
    }

    private static volatile Object sUnsafe;
    private static volatile Method sAllocateInstance;

    private static Object allocateViewUnsafe(Class<?> cls) throws Throwable {
        if (sAllocateInstance == null) {
            Class<?> uc = Class.forName("sun.misc.Unsafe");
            Field f = uc.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            sUnsafe = f.get(null);
            sAllocateInstance = uc.getMethod("allocateInstance", Class.class);
        }
        return sAllocateInstance.invoke(sUnsafe, cls);
    }

    /** Directly attach a child to a ViewGroup's mChildren/mChildrenCount
     * arrays, bypassing addView's framework checks. The visible side effect
     * is that getChildCount() and getChildAt() report the child but no
     * lifecycle/measure/layout events fire. Good enough for the renderer
     * walk which only reads structure. */
    private static void attachChildReflective(ViewGroup parent, View child) throws Throwable {
        Class<?> vgCls = android.view.ViewGroup.class;
        Field mChildrenF = vgCls.getDeclaredField("mChildren");
        mChildrenF.setAccessible(true);
        Field mChildrenCountF = vgCls.getDeclaredField("mChildrenCount");
        mChildrenCountF.setAccessible(true);
        Object kids = mChildrenF.get(parent);
        View[] children;
        if (!(kids instanceof View[])) {
            children = new View[12];
            mChildrenF.set(parent, children);
            mChildrenCountF.setInt(parent, 0);
        } else {
            children = (View[]) kids;
        }
        int count = mChildrenCountF.getInt(parent);
        if (count >= children.length) {
            View[] grown = new View[children.length * 2 + 1];
            System.arraycopy(children, 0, grown, 0, count);
            children = grown;
            mChildrenF.set(parent, children);
        }
        children[count] = child;
        mChildrenCountF.setInt(parent, count + 1);
        /* Set the parent on the child for getParent() / hierarchy walks. */
        try {
            Field mParentF = android.view.View.class.getDeclaredField("mParent");
            mParentF.setAccessible(true);
            mParentF.set(child, parent);
        } catch (Throwable ignored) {}
    }

    /** Minimum field set so View.getContext() / getResources() / getChildCount
     * don't NPE when our renderer walks the tree. */
    private void seedMinimalViewFields(View v) {
        Class<?> viewCls = android.view.View.class;
        try {
            Field f = viewCls.getDeclaredField("mContext");
            f.setAccessible(true);
            f.set(v, mWlContext);
        } catch (Throwable ignored) {}
        try {
            Field f = viewCls.getDeclaredField("mResources");
            f.setAccessible(true);
            f.set(v, mWlContext.getResources());
        } catch (Throwable ignored) {}
        try {
            Field f = viewCls.getDeclaredField("mID");
            f.setAccessible(true);
            f.setInt(v, View.NO_ID);
        } catch (Throwable ignored) {}
        try {
            Field f = viewCls.getDeclaredField("mLayoutParams");
            f.setAccessible(true);
            f.set(v, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        } catch (Throwable ignored) {}
        try {
            Field f = viewCls.getDeclaredField("mVisibility");
            f.setAccessible(true);
            f.setInt(v, 0); /* VISIBLE=0 */
        } catch (Throwable ignored) {}
        /* For ViewGroup: ensure mChildren and mChildrenCount are valid. */
        if (v instanceof ViewGroup) {
            try {
                Field f = android.view.ViewGroup.class.getDeclaredField("mChildren");
                f.setAccessible(true);
                if (f.get(v) == null) {
                    f.set(v, new android.view.View[12]);
                }
            } catch (Throwable ignored) {}
        }
    }

    /** Heuristic substitution for view classes that can't be constructed:
     * map ViewGroup-like tags to FrameLayout, leaf widget tags to TextView.
     * Falls back to Unsafe-allocation when ctor calls fail (View ctor chain
     * needs system resources we don't have). */
    private View substituteForTag(String tag) {
        String low = tag.toLowerCase();
        boolean isGroup = low.contains("layout")
                || low.endsWith("view") && low.contains("group")
                || low.contains("constraint")
                || low.contains("coordinator")
                || low.contains("recycler")
                || low.contains("scroll")
                || low.contains("appbar")
                || low.contains("container")
                || low.contains("fragment")
                || low.contains("nav");
        boolean isText = low.endsWith("textview")
                || low.endsWith("button")
                || low.endsWith("edittext")
                || low.contains("title")
                || low.contains("label");
        boolean isImage = low.endsWith("imageview")
                || low.contains("image")
                || low.contains("icon");
        try {
            if (isGroup) {
                return new android.widget.LinearLayout(mWlContext);
            }
            if (isText) {
                return new android.widget.TextView(mWlContext);
            }
            if (isImage) {
                return new android.widget.ImageView(mWlContext);
            }
        } catch (Throwable t) {
            log("substituteForTag " + tag + " (" + (isGroup?"group":(isText?"text":"image"))
                    + ") ctor failed: " + describeThrow(t));
        }
        /* Generic fallback. */
        try {
            return new android.widget.FrameLayout(mWlContext);
        } catch (Throwable t) {
            log("substituteForTag fallback FrameLayout failed: " + describeThrow(t));
        }
        try {
            return new View(mWlContext);
        } catch (Throwable t) {
            log("substituteForTag fallback View failed: " + describeThrow(t));
        }
        /* Unsafe-allocate FrameLayout (or LinearLayout / TextView /
         * ImageView per tag bucket). */
        try {
            Class<?> targetCls;
            if (isText) targetCls = android.widget.TextView.class;
            else if (isImage) targetCls = android.widget.ImageView.class;
            else if (isGroup) targetCls = android.widget.LinearLayout.class;
            else targetCls = android.widget.FrameLayout.class;
            Object inst = allocateViewUnsafe(targetCls);
            if (inst instanceof View) {
                seedMinimalViewFields((View) inst);
                log("substituteForTag " + tag + " Unsafe-allocated "
                        + targetCls.getSimpleName());
                return (View) inst;
            }
        } catch (Throwable t) {
            log("substituteForTag Unsafe-allocate failed: " + describeThrow(t));
        }
        return null;
    }

    private Class<?> resolveViewClass(String tag) {
        if (tag.indexOf('.') >= 0) {
            try {
                return mAppClassLoader.loadClass(tag);
            } catch (Throwable ignored) {}
            try {
                return Class.forName(tag);
            } catch (Throwable ignored) {}
            return null;
        }
        for (String pkg : FALLBACK_PACKAGES) {
            String full = pkg + tag;
            try {
                return mAppClassLoader.loadClass(full);
            } catch (Throwable ignored) {}
            try {
                return Class.forName(full);
            } catch (Throwable ignored) {}
        }
        return null;
    }

    private View newFallbackView() {
        try {
            return new android.widget.FrameLayout(mWlContext);
        } catch (Throwable t) {
            /* skip */
        }
        try {
            return new View(mWlContext);
        } catch (Throwable t) {
            /* skip */
        }
        try {
            Object inst = allocateViewUnsafe(android.widget.FrameLayout.class);
            if (inst instanceof View) {
                seedMinimalViewFields((View) inst);
                return (View) inst;
            }
        } catch (Throwable ignored) {}
        return null;
    }

    /** Apply a small set of common attrs to the view. Each apply path
     * uses reflective method lookup to avoid hard dependencies on view
     * classes we may not have. */
    private void applyAttrs(View v, BinaryXmlParser parser) {
        if (v == null) return;
        int n = parser.getAttributeCount();
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean haveLP = false;
        for (int i = 0; i < n; i++) {
            String name = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);
            if (name == null) continue;
            if ("layout_width".equals(name)) {
                width = parseDim(value, ViewGroup.LayoutParams.WRAP_CONTENT);
                haveLP = true;
            } else if ("layout_height".equals(name)) {
                height = parseDim(value, ViewGroup.LayoutParams.WRAP_CONTENT);
                haveLP = true;
            } else if ("text".equals(name)) {
                applyText(v, resolveString(value));
            } else if ("textSize".equals(name)) {
                applyTextSize(v, parseDimPx(value, 0));
            } else if ("textColor".equals(name)) {
                applyTextColor(v, parseColor(value, 0xFF000000));
            } else if ("background".equals(name)) {
                int c = parseColor(value, 0);
                if (c != 0) applyBackgroundColor(v, c);
            } else if ("gravity".equals(name)) {
                applyGravity(v, parseGravity(value));
            } else if ("id".equals(name)) {
                /* Prefer AXML's resource value (binary id) over string parse. */
                int id = parser.getAttributeResourceValue(i, 0);
                if (id == 0) id = parseResId(value);
                if (id != 0) {
                    try {
                        v.setId(id);
                    } catch (Throwable ignored) {
                        /* Fallback: set mID reflectively. */
                        try {
                            Field f = android.view.View.class.getDeclaredField("mID");
                            f.setAccessible(true);
                            f.setInt(v, id);
                        } catch (Throwable t) {}
                    }
                }
            } else if ("padding".equals(name)) {
                int px = parseDimPx(value, 0);
                if (px != 0) v.setPadding(px, px, px, px);
            } else if ("visibility".equals(name)) {
                int vis = parseVisibility(value);
                if (vis >= 0) v.setVisibility(vis);
            }
        }
        if (haveLP) {
            try {
                v.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            } catch (Throwable ignored) {}
        }
    }

    private String resolveString(String value) {
        if (value == null) return "";
        if (value.startsWith("@string/") || value.startsWith("@android:string/")) {
            try {
                int id = parseResId(value);
                if (id != 0) return mWlContext.getResources().getString(id);
            } catch (Throwable ignored) {}
            return "";
        }
        if (value.startsWith("@")) {
            try {
                int id = parseResId(value);
                if (id != 0) return mWlContext.getResources().getString(id);
            } catch (Throwable ignored) {}
            return "";
        }
        return value;
    }

    /** Parse an @id/@layout/etc reference like "@7f0c0042" or "@id/foo". */
    private int parseResId(String value) {
        if (value == null) return 0;
        try {
            String v = value;
            if (v.startsWith("@")) v = v.substring(1);
            if (v.startsWith("+id/")) v = v.substring(4);
            if (v.startsWith("id/") || v.startsWith("string/") || v.startsWith("layout/")) {
                if (mArsc != null) {
                    int id = mArsc.getIdentifier(v);
                    if (id != 0) return id;
                }
                return 0;
            }
            /* Hex resource id form. */
            if (v.indexOf('x') >= 0 || v.length() == 8) {
                try { return (int) Long.parseLong(v, 16); }
                catch (Throwable ignored) {}
            }
            try { return Integer.parseInt(v); }
            catch (Throwable ignored) {}
        } catch (Throwable ignored) {}
        return 0;
    }

    private int parseDim(String value, int def) {
        if (value == null) return def;
        if ("match_parent".equals(value) || "fill_parent".equals(value)) {
            return ViewGroup.LayoutParams.MATCH_PARENT;
        }
        if ("wrap_content".equals(value)) {
            return ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        return parseDimPx(value, def);
    }

    private int parseDimPx(String value, int def) {
        if (value == null) return def;
        try {
            String v = value.trim();
            int end = v.length();
            while (end > 0 && !Character.isDigit(v.charAt(end - 1))
                    && v.charAt(end - 1) != '.') end--;
            if (end == 0) return def;
            float f = Float.parseFloat(v.substring(0, end));
            String unit = v.substring(end);
            if ("dp".equals(unit) || "dip".equals(unit)) {
                return (int) (f * mWlContext.getResources().getDisplayMetrics().density);
            }
            if ("sp".equals(unit)) {
                return (int) (f * mWlContext.getResources().getDisplayMetrics().scaledDensity);
            }
            return (int) f; /* px or no unit */
        } catch (Throwable t) {
            return def;
        }
    }

    private int parseColor(String value, int def) {
        if (value == null || value.isEmpty()) return def;
        try {
            if (value.startsWith("#")) {
                return (int) Long.parseLong(value.substring(1), 16);
            }
            if (value.startsWith("@")) {
                int id = parseResId(value);
                if (id != 0) {
                    return mWlContext.getResources().getColor(id);
                }
            }
        } catch (Throwable ignored) {}
        return def;
    }

    private int parseGravity(String value) {
        if (value == null) return android.view.Gravity.START;
        int g = 0;
        for (String part : value.split("\\|")) {
            String p = part.trim();
            if ("center".equals(p)) g |= android.view.Gravity.CENTER;
            else if ("center_horizontal".equals(p)) g |= android.view.Gravity.CENTER_HORIZONTAL;
            else if ("center_vertical".equals(p)) g |= android.view.Gravity.CENTER_VERTICAL;
            else if ("start".equals(p) || "left".equals(p)) g |= android.view.Gravity.START;
            else if ("end".equals(p) || "right".equals(p)) g |= android.view.Gravity.END;
            else if ("top".equals(p)) g |= android.view.Gravity.TOP;
            else if ("bottom".equals(p)) g |= android.view.Gravity.BOTTOM;
        }
        return g;
    }

    private int parseVisibility(String value) {
        if ("visible".equals(value)) return View.VISIBLE;
        if ("invisible".equals(value)) return View.INVISIBLE;
        if ("gone".equals(value)) return View.GONE;
        return -1;
    }

    private void applyText(View v, String text) {
        try {
            Method setText = findMethod(v.getClass(), "setText", CharSequence.class);
            if (setText != null) {
                setText.invoke(v, text);
            }
        } catch (Throwable ignored) {}
    }

    private void applyTextSize(View v, int px) {
        if (px <= 0) return;
        try {
            Method setTextSize = findMethod(v.getClass(), "setTextSize", int.class, float.class);
            if (setTextSize != null) {
                /* COMPLEX_UNIT_PX = 0 */
                setTextSize.invoke(v, 0, (float) px);
            }
        } catch (Throwable ignored) {}
    }

    private void applyTextColor(View v, int color) {
        try {
            Method setTextColor = findMethod(v.getClass(), "setTextColor", int.class);
            if (setTextColor != null) {
                setTextColor.invoke(v, color);
            }
        } catch (Throwable ignored) {}
    }

    private void applyBackgroundColor(View v, int color) {
        try {
            v.setBackgroundColor(color);
        } catch (Throwable ignored) {}
    }

    private void applyGravity(View v, int g) {
        try {
            Method setGravity = findMethod(v.getClass(), "setGravity", int.class);
            if (setGravity != null) {
                setGravity.invoke(v, g);
            }
        } catch (Throwable ignored) {}
    }

    private static Method findMethod(Class<?> cls, String name, Class<?>... params) {
        for (Class<?> c = cls; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                Method m = c.getDeclaredMethod(name, params);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException nsme) {
                /* try parent */
            } catch (Throwable t) {
                return null;
            }
        }
        return null;
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

    private static String describeThrow(Throwable t) {
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) root = root.getCause();
        return root.getClass().getSimpleName() + ": " + root.getMessage();
    }

    private static void log(String msg) {
        try {
            android.util.Log.d("WestlakeVM:", "WestlakeLayoutInflater " + msg);
        } catch (Throwable ignored) {}
    }
}
