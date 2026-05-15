/*
 * PF-arch-026 (tasks #2 + #3 of three): walk a WestlakeNode tree and emit
 * OHBridge canvas primitives. Resolves @string/@color/@drawable refs via
 * the supplied ResourceTable.
 *
 * PF-arch-027: also expands FragmentContainerView with navGraph attribute
 * by resolving its start destination and inflating that fragment's layout
 * into the parent slot (lazily, once, cached on the node).
 */
package com.westlake.engine;

import android.content.res.ResourceTable;
import com.ohos.shim.bridge.OHBridge;

public final class WestlakeRenderer {
    private WestlakeRenderer() {}

    /** Set by WestlakeView per Activity — accessed during recursive draw. */
    private static final ThreadLocal<String> tlResDir = new ThreadLocal<String>();
    private static final ThreadLocal<WestlakeTheme> tlTheme = new ThreadLocal<WestlakeTheme>();
    /* C1 (2026-05-12): host Activity exposed via ThreadLocal so downstream
     * code (Launcher, M3-M7 lifecycle plumbing) can retrieve it. The
     * renderer itself no longer reads this — it's plumbing for the rest of
     * the engine. */
    private static final ThreadLocal<android.app.Activity> tlActivity =
            new ThreadLocal<android.app.Activity>();

    public static void setResDir(String resDir) { tlResDir.set(resDir); }
    public static void setTheme(WestlakeTheme theme) { tlTheme.set(theme); }
    public static void setActivity(android.app.Activity activity) { tlActivity.set(activity); }
    public static android.app.Activity getActivity() { return tlActivity.get(); }

    /** Render the laid-out tree to the given OHBridge canvas handle. */
    public static void render(WestlakeNode root, ResourceTable arsc, long canvas) {
        if (root == null || canvas == 0) return;
        long font = OHBridge.fontCreate();
        long pen = OHBridge.penCreate();
        long brush = OHBridge.brushCreate();
        try {
            /* PF-arch-037: caller has already painted background; we only
             * draw the tree on top. Skeleton-render outlines/labels use
             * high-contrast colors that work on both light and dark surfaces. */
            drawNode(root, arsc, canvas, font, pen, brush);
        } catch (Throwable t) {
            /* Don't let one bad node abort the whole frame. */
        }
    }

    /** PF-arch-037: resolve the activity's preferred background color via theme
     * (colorBackground, then colorSurface fallback). Returns 0 if no theme is
     * set so the caller can use a sensible default. */
    public static int resolveThemeBackgroundColor() {
        WestlakeTheme theme = tlTheme.get();
        if (theme == null) return 0;
        ResourceTable arsc = null;
        /* The renderer's resolveColor walks `?` through the theme; the arsc
         * arg is only used for `@` refs, which colorBackground typically
         * doesn't have once resolved. */
        String[] candidates = new String[] {
                "?0x1010098",   /* android:colorBackground */
                "?0x101020c",   /* android:windowBackground */
                "?0x7f04013f",  /* colorSurface (common Material attr) */
        };
        for (String c : candidates) {
            int color = resolveColor(c, arsc);
            if (color != 0) return color;
        }
        return 0;
    }

    private static void drawNode(WestlakeNode n, ResourceTable arsc, long canvas,
            long font, long pen, long brush) {
        if (n == null) return;
        System.err.println("[WL-draw] " + n.tag + " bounds=" + n.x + "," + n.y + " " + n.w + "x" + n.h);
        if (n.w <= 0 || n.h <= 0) return;

        /* PF-arch-034: apply `style` and `textAppearance` references — fold
         * the style's attrs into the node as fallbacks. Done lazily on first
         * draw and cached via a sentinel attr. */
        if (!"1".equals(n.getAttr("$styleApplied"))) {
            applyStyle(n, n.getAttr("style"), arsc);
            applyStyle(n, n.getAttr("textAppearance"), arsc);
            n.attrs.put("$styleApplied", "1");
        }

        /* PF-arch-027: expand FragmentContainerView lazily. The expansion
         * itself adds a child WestlakeNode (the fragment's layout) which the
         * recursive draw below picks up naturally. */
        if (n.children.isEmpty() && isFragmentContainer(n)) {
            int savedX = n.x, savedY = n.y;
            expandFragmentContainer(n, arsc);
            WestlakeLayout.layout(n, n.w, n.h);
            n.x = savedX; n.y = savedY;
            translateChildren(n, savedX, savedY);
        } else if (n.children.isEmpty() && isMenuHost(n)) {
            int savedX = n.x, savedY = n.y;
            expandMenu(n, arsc);
            WestlakeLayout.layout(n, n.w, n.h);
            n.x = savedX; n.y = savedY;
            translateChildren(n, savedX, savedY);
        }
        /* PF-arch-038: paint elevated-surface bg for menu hosts EVERY frame
         * (the lazy expand-once block above runs only on first frame). */
        if (isMenuHost(n)) {
            paintRect(canvas, pen, brush, n.x, n.y, n.w, n.h, 0xFF1F1F1F);
        }

        /* Background — color or drawable. */
        String bg = n.getAttr("background");
        if (bg != null && !bg.isEmpty()) {
            int color = resolveColor(bg, arsc);
            if (color != 0) {
                /* Emit rect by clipping the canvas to the node bounds and
                 * drawing color; OHBridge.canvasDrawColor draws full canvas,
                 * so we fake a rect via brush color + drawRect-like text marker.
                 * Until we have a true drawRect primitive we just paint the
                 * region by drawing background-color text. */
                paintRect(canvas, pen, brush, n.x, n.y, n.w, n.h, color);
            }
        }

        /* Tag-specific rendering. Match on suffix so that prefixed names
         * (androidx.appcompat.widget.AppCompatTextView, etc.) also fire. */
        String tag = n.tag != null ? n.tag : "";
        boolean isTextish = tag.endsWith("TextView") || tag.endsWith("Button")
                || tag.endsWith("EditText");
        boolean isImageish = tag.endsWith("ImageView") || tag.endsWith("ImageButton");
        boolean isLeaf = n.children.isEmpty();

        /* PF-arch-038: render only ACTUAL content. Empty TextViews, anonymous
         * Spaces, and untyped leaves are visually omitted — they correspond
         * to layout slots whose content is set programmatically at runtime
         * (Hilt-injected ViewModels etc.), which we don't have. Drawing
         * debug placeholders for them just covers the surface in wireframe
         * noise. */
        if (isTextish) {
            String text = resolveString(n.getAttr("text"), arsc);
            if (text != null && !text.isEmpty()) {
                int textColor = resolveColor(n.getAttr("textColor"), arsc);
                if (textColor == 0) textColor = 0xFFEEEEEE;
                int textSize = n.getDimAttr("textSize", 16);
                OHBridge.fontSetSize(font, textSize);
                OHBridge.penSetColor(pen, textColor);
                int tx = n.x + Math.max(4, n.getDimAttr("paddingLeft", 0));
                int ty = n.y + textSize + Math.max(4, n.getDimAttr("paddingTop", 0));
                OHBridge.canvasDrawText(canvas, text, tx, ty, font, pen, brush);
            }
            /* No content → render nothing. The space is still claimed by layout. */
        } else if (isImageish) {
            String src = n.getAttr("src");
            if (src == null) src = n.getAttr("srcCompat");
            int tintColor = resolveColor(n.getAttr("tint"), arsc);
            drawAnyDrawable(src, arsc, canvas,
                    n.x, n.y, n.w, n.h, tintColor != 0 ? tintColor : 0xFFEEEEEE);
            /* Unresolved image → blank. No placeholder. */
        }
        /* Other leaf types (Space, View, FragmentContainerView) and containers:
         * nothing drawn here — only the children render. */

        /* Recurse into children. */
        for (WestlakeNode c : n.children) {
            drawNode(c, arsc, canvas, font, pen, brush);
        }
    }

    /** Fill rect with `color` using OHBridge primitives. */
    private static void paintRect(long canvas, long pen, long brush,
            int x, int y, int w, int h, int color) {
        OHBridge.penSetColor(pen, color);
        OHBridge.brushSetColor(brush, color);
        OHBridge.canvasDrawRect(canvas, x, y, x + w, y + h, pen, brush);
    }

    /** PF-arch-029: 1-pixel outline using four line draws. */
    private static void paintOutline(long canvas, long pen, long brush,
            int x, int y, int w, int h, int color) {
        if (w < 2 || h < 2) return;
        OHBridge.penSetColor(pen, color);
        OHBridge.canvasDrawLine(canvas, x, y, x + w, y, pen);
        OHBridge.canvasDrawLine(canvas, x + w, y, x + w, y + h, pen);
        OHBridge.canvasDrawLine(canvas, x, y + h, x + w, y + h, pen);
        OHBridge.canvasDrawLine(canvas, x, y, x, y + h, pen);
    }

    private static void drawLabel(long canvas, long font, long pen, long brush,
            int x, int y, String text, int color, int size) {
        if (text == null || text.isEmpty()) return;
        OHBridge.fontSetSize(font, size);
        OHBridge.penSetColor(pen, color);
        OHBridge.canvasDrawText(canvas, text, x, y, font, pen, brush);
    }

    /** "androidx.fragment.app.FragmentContainerView" → "FragmentContainerView". */
    private static String simpleTag(String tag) {
        if (tag == null) return "";
        int dot = tag.lastIndexOf('.');
        return dot >= 0 ? tag.substring(dot + 1) : tag;
    }

    /* ── Resource resolvers ── */

    /** Resolve "@string/foo" via arsc; otherwise return raw string. */
    private static String resolveString(String raw, ResourceTable arsc) {
        if (raw == null) return null;
        /* PF-arch-031: theme attr → real value, then recurse. */
        if (raw.startsWith("?")) {
            WestlakeTheme theme = tlTheme.get();
            if (theme != null) {
                String resolved = theme.resolve(raw);
                if (resolved != null && !resolved.equals(raw)) {
                    return resolveString(resolved, arsc);
                }
            }
            return null;
        }
        if (!raw.startsWith("@")) return raw;
        if (arsc == null) return raw;
        int id = parseResId(raw);
        if (id == 0) return raw;
        try {
            String resolved = arsc.getString(id);
            return resolved != null ? resolved : raw;
        } catch (Throwable t) {
            return raw;
        }
    }

    /** Resolve "@color/foo" or "#RRGGBB"; returns ARGB int (0 = unresolved). */
    private static int resolveColor(String raw, ResourceTable arsc) {
        if (raw == null || raw.isEmpty()) return 0;
        /* PF-arch-031: theme attr → real value, then recursively resolve. */
        if (raw.startsWith("?")) {
            WestlakeTheme theme = tlTheme.get();
            if (theme != null) {
                String resolved = theme.resolve(raw);
                if (resolved != null && !resolved.equals(raw)) {
                    return resolveColor(resolved, arsc);
                }
            }
            return 0;
        }
        if (raw.startsWith("#")) {
            try {
                long parsed = Long.parseLong(raw.substring(1), 16);
                int len = raw.length() - 1;
                if (len == 6) return 0xFF000000 | (int) parsed;
                if (len == 3) {
                    int r = ((int) parsed >> 8) & 0xF;
                    int g = ((int) parsed >> 4) & 0xF;
                    int b = (int) parsed & 0xF;
                    return 0xFF000000 | (r * 0x11 << 16) | (g * 0x11 << 8) | (b * 0x11);
                }
                return (int) parsed;
            } catch (NumberFormatException nfe) {
                return 0;
            }
        }
        if (raw.startsWith("@")) {
            int id = parseResId(raw);
            if (id == 0 || arsc == null) return 0;
            try {
                /* ResourceTable currently exposes integers via getInteger. */
                int color = arsc.getInteger(id, 0);
                if (color != 0) return color;
            } catch (Throwable ignored) {}
        }
        try {
            return (int) Long.parseLong(raw);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /** PF-arch-028: resolve "@drawable/foo" (or "@0xRESID") to image bytes.
     * Steps: arsc.getEntryFilePath(id) -> path under resDir -> read bytes.
     * Returns null for vector drawables (XML) — caller falls back to vector rasterizer. */
    private static byte[] resolveDrawableBytes(String raw, ResourceTable arsc) {
        if (raw == null || raw.isEmpty()) return null;
        if (!raw.startsWith("@")) return null;
        if (arsc == null) return null;
        String resDir = tlResDir.get();
        if (resDir == null) return null;
        int id = parseResId(raw);
        if (id == 0) return null;
        String path = arsc.getEntryFilePath(id);
        if (path == null || path.isEmpty()) return null;
        if (path.endsWith(".xml")) return null;
        java.io.File f = new java.io.File(resDir + "/" + path);
        if (!f.isFile()) return null;
        return readAllBytes(f);
    }

    /** PF-arch-030: try raster path; on miss, try vector-XML rasterization;
     * else return false so caller can placeholder. */
    private static boolean drawAnyDrawable(String raw, ResourceTable arsc, long canvas,
            int x, int y, int w, int h, int tintColor) {
        if (raw == null || arsc == null) return false;
        byte[] raster = resolveDrawableBytes(raw, arsc);
        if (raster != null) {
            OHBridge.canvasDrawImage(canvas, raster, x, y, w, h);
            return true;
        }
        /* Vector path: read the XML and rasterize. */
        String resDir = tlResDir.get();
        if (resDir == null) return false;
        int id = parseResId(raw);
        if (id == 0) return false;
        String path = arsc.getEntryFilePath(id);
        if (path == null || !path.endsWith(".xml")) return false;
        java.io.File f = new java.io.File(resDir + "/" + path);
        if (!f.isFile()) return false;
        byte[] axml = readAllBytes(f);
        if (axml == null) return false;
        return WestlakeVector.draw(axml, canvas, x, y, w, h, tintColor);
    }

    /** PF-arch-034: walk a style's bag-entries and merge attr → value pairs
     * into the node as fallbacks (don't overwrite explicit attributes). */
    private static void applyStyle(WestlakeNode n, String styleRef, ResourceTable arsc) {
        if (styleRef == null || styleRef.isEmpty() || arsc == null) return;
        int styleId = parseResId(styleRef);
        if (styleId == 0) return;
        java.util.Map<Integer, String> bag = arsc.getStyleAttrs(styleId);
        if (bag == null || bag.isEmpty()) return;
        for (java.util.Map.Entry<Integer, String> e : bag.entrySet()) {
            String attrName = arsc.getResourceEntryName(e.getKey());
            if (attrName == null || attrName.isEmpty()) continue;
            if (!n.attrs.containsKey(attrName)) {
                n.attrs.put(attrName, e.getValue());
            }
        }
    }

    /** True if `n` is an *anything*-named FragmentContainerView. */
    private static boolean isFragmentContainer(WestlakeNode n) {
        String t = n.tag;
        if (t == null) return false;
        return t.endsWith("FragmentContainerView") || "fragment".equals(t);
    }

    /** Recognize fragment "holder" classes that don't render anything themselves —
     * they only host other fragments at runtime. */
    private static boolean isHolderFragment(String className) {
        if (className == null) return false;
        return className.equals("androidx.navigation.fragment.NavHostFragment")
                || className.equals("androidx.fragment.app.Fragment")
                || className.equals("android.app.Fragment")
                || className.endsWith("NavHostFragment");
    }

    /** True if `n` is a BottomNavigationView, NavigationRailView, etc. that
     * uses `app:menu="@menu/..."` to populate items at runtime. */
    private static boolean isMenuHost(WestlakeNode n) {
        String t = n.tag;
        if (t == null) return false;
        return t.endsWith("BottomNavigationView") || t.endsWith("NavigationRailView")
                || t.endsWith("NavigationView") || t.endsWith("MaterialToolbar")
                || t.endsWith("Toolbar");
    }

    /** PF-arch-029: expand `app:menu` into virtual TextView children — each
     * menu &lt;item&gt; with android:title becomes a label so the bottom-bar tabs
     * appear in the skeleton render. */
    private static void expandMenu(WestlakeNode n, ResourceTable arsc) {
        String menuAttr = n.getAttr("menu");
        if (menuAttr == null || arsc == null) return;
        String resDir = tlResDir.get();
        if (resDir == null) return;
        int menuId = parseResId(menuAttr);
        if (menuId == 0) return;
        String path = arsc.getEntryFilePath(menuId);
        if (path == null || path.isEmpty()) return;
        java.io.File f = new java.io.File(resDir + "/" + path);
        if (!f.isFile()) return;
        byte[] data = readAllBytes(f);
        if (data == null) return;
        WestlakeNode menu = WestlakeInflater.inflate(data);
        if (menu == null) return;
        /* Each menu item gets a vertical LinearLayout column with icon
         * (ImageView) on top and title (TextView) below. This mirrors the
         * real Material BottomNavigationView item layout. First item gets
         * selected/highlighted style. */
        int idx = 0;
        for (WestlakeNode item : menu.children) {
            if (!"item".equals(item.tag)) continue;
            String title = item.getAttr("title");
            String icon = item.getAttr("icon");
            String resolvedTitle = resolveString(title, arsc);
            if (resolvedTitle == null) resolvedTitle = title;
            boolean isSelected = (idx == 0);

            WestlakeNode column = new WestlakeNode("LinearLayout");
            column.attrs.put("orientation", "1"); /* vertical */
            column.attrs.put("layout_width", "0");
            column.attrs.put("layout_height", "-1");
            column.attrs.put("layout_weight", "1");
            column.attrs.put("gravity", "center");
            if (isSelected) {
                /* Subtle highlight bar for the selected tab. */
                column.attrs.put("background", "#33FFFFFF");
            }

            String tintColor = isSelected ? "#FFFFFFFF" : "#FF9E9E9E";
            String textColor = isSelected ? "#FFFFFFFF" : "#FF9E9E9E";

            if (icon != null && !icon.isEmpty()) {
                WestlakeNode iconView = new WestlakeNode("ImageView");
                iconView.attrs.put("src", icon);
                iconView.attrs.put("tint", tintColor);
                iconView.attrs.put("layout_width", "24");
                iconView.attrs.put("layout_height", "24");
                column.children.add(iconView);
            }
            WestlakeNode tv = new WestlakeNode("TextView");
            if (resolvedTitle != null) tv.attrs.put("text", resolvedTitle);
            tv.attrs.put("textColor", textColor);
            tv.attrs.put("textSize", "10");
            tv.attrs.put("layout_width", "-1");
            tv.attrs.put("layout_height", "-2");
            column.children.add(tv);
            n.children.add(column);
            idx++;
        }
        /* Force horizontal LinearLayout-style child distribution on the bar. */
        n.attrs.put("orientation", "0");
    }

    /** Resolve `navGraph` → start destination → fragment layout, append as child.
     * Also supports the old-style `<fragment android:name="...class.Foo">` form. */
    private static void expandFragmentContainer(WestlakeNode n, ResourceTable arsc) {
        String resDir = tlResDir.get();
        if (resDir == null || arsc == null) return;
        String navAttr = n.getAttr("navGraph");
        String fragmentClass = null;
        if (navAttr != null) {
            int navId = parseResId(navAttr);
            if (navId == 0) return;
            WestlakeNavGraph.StartDestination dest =
                    WestlakeNavGraph.resolve(navId, resDir, arsc);
            if (dest == null) return;
            fragmentClass = dest.fragmentClassName;
        } else {
            /* Old-style <fragment android:name="some.package.Foo"> — class name
             * is the direct attribute value (NOT a resource id). */
            String nameAttr = n.getAttr("name");
            String classAttr = n.getAttr("class");
            if (nameAttr != null && nameAttr.indexOf('.') > 0
                    && !nameAttr.startsWith("@")) {
                fragmentClass = nameAttr;
            } else if (classAttr != null && classAttr.indexOf('.') > 0
                    && !classAttr.startsWith("@")) {
                fragmentClass = classAttr;
            }
        }
        if (fragmentClass == null) return;
        WestlakeNavGraph.StartDestination dest =
                new WestlakeNavGraph.StartDestination(fragmentClass, "");
        if (dest == null) {
            /* Mark with a placeholder text node so renderer shows something. */
            WestlakeNode mark = new WestlakeNode("TextView");
            mark.attrs.put("text", "[FragmentContainerView: unresolved navGraph " + navAttr + "]");
            mark.attrs.put("textColor", "#FF888888");
            mark.attrs.put("textSize", "14");
            n.children.add(mark);
            return;
        }

        /* C1 (2026-05-12): inflate the static-XML layout the fragment declares
         * and append it as a child. No live-lifecycle invocation from the
         * renderer — fragment lifecycle is driven by FragmentManager. If the
         * fragment has no static layout (or the lookup fails), the slot is
         * left empty until M3-M7 wire the real lifecycle through. */
        WestlakeNode staticRoot = null;
        try {
            int layoutId = findFragmentLayoutId(dest.fragmentClassName, arsc);
            if (layoutId != 0) {
                String path = arsc.getEntryFilePath(layoutId);
                if (path != null && !path.isEmpty()) {
                    java.io.File f = new java.io.File(resDir + "/" + path);
                    if (f.isFile()) {
                        byte[] data = readAllBytes(f);
                        if (data != null) {
                            staticRoot = WestlakeInflater.inflate(data);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            System.err.println("[WL] Fragment static-XML inflate threw "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
            staticRoot = null;
        }

        if (staticRoot != null) {
            n.children.add(staticRoot);
        }
        /* If staticRoot is null we leave the slot empty; the fragment slot
         * will be filled by the FragmentManager-driven path (M3-M7). */
    }

    /** Look up the layout the fragment class declares. Try:
     *  1) `Fragment.mContentLayoutId` field (set by `super(R.layout.foo)`)
     *  2) Naming convention: ClassName→fragment_class_name. */
    private static int findFragmentLayoutId(String fragClassName, ResourceTable arsc) {
        try {
            Class<?> fragCls = Class.forName(fragClassName);
            /* Walk up to androidx.fragment.app.Fragment. */
            Class<?> c = fragCls;
            while (c != null && !c.getName().equals("androidx.fragment.app.Fragment")
                    && !c.getName().equals("android.app.Fragment")) {
                c = c.getSuperclass();
            }
            if (c != null) {
                try {
                    java.lang.reflect.Field fld = c.getDeclaredField("mContentLayoutId");
                    fld.setAccessible(true);
                    /* mContentLayoutId is set by the ctor — we'd need an instance
                     * to read it. Try Unsafe.allocateInstance + manual setter
                     * isn't reliable; instead read the @ContentView annotation. */
                } catch (NoSuchFieldException nsfe) {
                    /* fall through to naming convention */
                }
                /* Read @ContentView annotation if present. */
                try {
                    java.lang.annotation.Annotation[] anns = fragCls.getAnnotations();
                    for (java.lang.annotation.Annotation a : anns) {
                        String an = a.annotationType().getName();
                        if ("androidx.fragment.app.ContentView".equals(an)
                                || "android.app.ContentView".equals(an)) {
                            java.lang.reflect.Method m =
                                    a.annotationType().getDeclaredMethod("value");
                            Object v = m.invoke(a);
                            if (v instanceof Integer) return (Integer) v;
                        }
                    }
                } catch (Throwable ignored) {}
            }
        } catch (ClassNotFoundException cnfe) {
            /* fall through */
        } catch (Throwable t) {
            /* fall through */
        }
        /* Naming convention: HomeFragment → fragment_home, MainFragment → fragment_main. */
        String simple = fragClassName;
        int dot = simple.lastIndexOf('.');
        if (dot >= 0) simple = simple.substring(dot + 1);
        if (simple.endsWith("Fragment")) {
            simple = simple.substring(0, simple.length() - "Fragment".length());
        }
        if (simple.isEmpty()) return 0;
        /* CamelCase → snake_case. */
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < simple.length(); i++) {
            char ch = simple.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (i > 0) sb.append('_');
                sb.append(Character.toLowerCase(ch));
            } else sb.append(ch);
        }
        String snake = sb.toString();
        String[] candidates = new String[] {
                "fragment_" + snake,
                snake + "_fragment",
                snake,
        };
        for (String candidate : candidates) {
            int id = arsc.getIdentifier(candidate);
            if (id != 0) return id;
        }
        return 0;
    }

    private static byte[] readAllBytes(java.io.File f) {
        try {
            java.io.FileInputStream fis = new java.io.FileInputStream(f);
            try {
                long size = f.length();
                if (size <= 0 || size > 4 * 1024 * 1024) return null;
                byte[] buf = new byte[(int) size];
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
        } catch (java.io.IOException ioe) {
            return null;
        }
    }

    private static void translateChildren(WestlakeNode parent, int dx, int dy) {
        for (WestlakeNode c : parent.children) {
            c.x += dx;
            c.y += dy;
            translateChildren(c, dx, dy);
        }
    }

    /** Parse "@PKG_ID/NAME" or hex int after "@"; returns 0 if unparseable. */
    private static int parseResId(String raw) {
        if (raw == null || !raw.startsWith("@") || raw.length() < 2) return 0;
        /* Stripped binary AXML often stores @ID as the numeric ID directly. */
        try {
            String num = raw.substring(1);
            if (num.startsWith("0x") || num.startsWith("0X")) {
                return (int) Long.parseLong(num.substring(2), 16);
            }
            int slash = num.indexOf('/');
            if (slash < 0) return (int) Long.parseLong(num);
            /* "type/name" form — arsc lookup by name. Not handled here. */
            return 0;
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }
}
