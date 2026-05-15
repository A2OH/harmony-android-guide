/*
 * PF-arch-026 (task #2 of three, layout pass): assign x/y/w/h to every
 * WestlakeNode based on its container and the standard Android layout
 * params. Simplified: LinearLayout (vertical/horizontal), FrameLayout,
 * everything else treated as FrameLayout. Match-parent fills container;
 * wrap-content uses 0 with intrinsic measurement for text/image leaves.
 */
package com.westlake.engine;

public final class WestlakeLayout {
    private WestlakeLayout() {}

    public static void layout(WestlakeNode root, int width, int height) {
        if (root == null) return;
        root.x = 0;
        root.y = 0;
        root.w = width;
        root.h = height;
        layoutChildren(root);
    }

    private static void layoutChildren(WestlakeNode parent) {
        if (parent.children.isEmpty()) return;
        String tag = parent.tag;
        /* PF-arch-029: BottomNavigationView and similar menu hosts get
         * treated as horizontal LinearLayouts after expandMenu — they have
         * orientation="0" set explicitly by the menu expander. */
        boolean isLinear = "LinearLayout".equals(tag) || tag.endsWith(".LinearLayout")
                || tag.endsWith("BottomNavigationView")
                || tag.endsWith("NavigationRailView")
                || tag.endsWith("MaterialToolbar")
                || tag.endsWith("Toolbar");
        /* Binary AXML stores orientation as int: 0=horizontal, 1=vertical.
         * String form "vertical"/"horizontal" also possible. */
        String orient = parent.getAttr("orientation");
        boolean isVertical = isLinear &&
                ("vertical".equals(orient) || "1".equals(orient));

        int padL = parent.getDimAttr("paddingLeft", 0);
        int padR = parent.getDimAttr("paddingRight", 0);
        int padT = parent.getDimAttr("paddingTop", 0);
        int padB = parent.getDimAttr("paddingBottom", 0);
        int padAll = parent.getDimAttr("padding", -1);
        if (padAll >= 0) {
            padL = padR = padT = padB = padAll;
        }

        int innerW = parent.w - padL - padR;
        int innerH = parent.h - padT - padB;

        if (isLinear && isVertical) {
            /* Two-pass for layout_weight in LinearLayout: first sum fixed
             * heights and total weights, then distribute remaining space. */
            int fixedH = 0;
            float totalWeight = 0f;
            for (WestlakeNode child : parent.children) {
                int ch = child.getDimAttr("layout_height", -2);
                float w = parseFloat(child.getAttr("layout_weight"), 0f);
                if (w > 0f && ch == 0) {
                    totalWeight += w;
                } else {
                    if (ch == -1) ch = innerH;
                    if (ch == -2) ch = intrinsicH(child);
                    fixedH += ch;
                }
            }
            int remaining = Math.max(0, innerH - fixedH);
            int yCursor = parent.y + padT;
            for (WestlakeNode child : parent.children) {
                int cw = child.getDimAttr("layout_width", -1);
                int ch = child.getDimAttr("layout_height", -2);
                float weight = parseFloat(child.getAttr("layout_weight"), 0f);
                if (cw == -1) cw = innerW;
                if (cw == -2) cw = intrinsicW(child);
                if (weight > 0f && ch == 0 && totalWeight > 0f) {
                    ch = (int) (remaining * (weight / totalWeight));
                } else {
                    if (ch == -1) ch = innerH;
                    if (ch == -2) ch = intrinsicH(child);
                }
                child.x = parent.x + padL;
                child.y = yCursor;
                child.w = cw;
                child.h = ch;
                yCursor += ch;
                layoutChildren(child);
            }
            return;
        }
        if (isLinear) {
            /* horizontal — same two-pass weight handling but on width. */
            int fixedW = 0;
            float totalWeight = 0f;
            for (WestlakeNode child : parent.children) {
                int cw = child.getDimAttr("layout_width", -2);
                float w = parseFloat(child.getAttr("layout_weight"), 0f);
                if (w > 0f && cw == 0) {
                    totalWeight += w;
                } else {
                    if (cw == -1) cw = innerW;
                    if (cw == -2) cw = intrinsicW(child);
                    fixedW += cw;
                }
            }
            int remainingW = Math.max(0, innerW - fixedW);
            int xCursor = parent.x + padL;
            for (WestlakeNode child : parent.children) {
                int cw = child.getDimAttr("layout_width", -2);
                int ch = child.getDimAttr("layout_height", -1);
                float weight = parseFloat(child.getAttr("layout_weight"), 0f);
                if (weight > 0f && cw == 0 && totalWeight > 0f) {
                    cw = (int) (remainingW * (weight / totalWeight));
                } else {
                    if (cw == -1) cw = innerW;
                    if (cw == -2) cw = intrinsicW(child);
                }
                if (ch == -1) ch = innerH;
                if (ch == -2) ch = intrinsicH(child);
                child.x = xCursor;
                child.y = parent.y + padT;
                child.w = cw;
                child.h = ch;
                xCursor += cw;
                layoutChildren(child);
            }
            return;
        }
        boolean isConstraint = tag.endsWith("ConstraintLayout");
        if (isConstraint) {
            layoutConstraintChildren(parent, padL, padR, padT, padB, innerW, innerH);
            return;
        }
        /* FrameLayout / generic — all children fill parent (most-recent on top). */
        for (WestlakeNode child : parent.children) {
            int cw = child.getDimAttr("layout_width", -1);
            int ch = child.getDimAttr("layout_height", -1);
            if (cw == -1) cw = innerW;
            if (cw == -2) cw = intrinsicW(child);
            if (ch == -1) ch = innerH;
            if (ch == -2) ch = intrinsicH(child);
            child.x = parent.x + padL;
            child.y = parent.y + padT;
            child.w = cw;
            child.h = ch;
            layoutChildren(child);
        }
    }

    /** PF-arch-033/034: ConstraintLayout solver with multi-pass sibling resolution.
     * Each child gets independent x/y/w/h based on its constraints. Parent-anchored
     * children resolve immediately; sibling-anchored children iterate until a fixed
     * point (max 8 passes). */
    private static void layoutConstraintChildren(WestlakeNode parent,
            int padL, int padR, int padT, int padB, int innerW, int innerH) {
        int n = parent.children.size();
        boolean[] xDone = new boolean[n];
        boolean[] yDone = new boolean[n];

        /* Multi-pass: each pass tries to resolve any child whose deps are met. */
        for (int pass = 0; pass < 8; pass++) {
            boolean changed = false;
            for (int i = 0; i < n; i++) {
                WestlakeNode child = parent.children.get(i);
                if (!yDone[i]) {
                    if (resolveVertical(parent, child, padT, padB, xDone, yDone)) {
                        yDone[i] = true;
                        changed = true;
                    }
                }
                if (!xDone[i]) {
                    if (resolveHorizontal(parent, child, padL, padR, xDone, yDone)) {
                        xDone[i] = true;
                        changed = true;
                    }
                }
            }
            if (!changed) break;
        }

        /* Default-resolve anything still missing (treat as anchored to parent top/start). */
        int parentLeft = parent.x + padL;
        int parentTop = parent.y + padT;
        for (int i = 0; i < n; i++) {
            WestlakeNode child = parent.children.get(i);
            if (!yDone[i]) {
                int ch = child.getDimAttr("layout_height", -2);
                child.y = parentTop;
                child.h = (ch == -1) ? parent.h - padT - padB
                        : (ch == -2) ? intrinsicH(child) : ch;
            }
            if (!xDone[i]) {
                int cw = child.getDimAttr("layout_width", -2);
                child.x = parentLeft;
                child.w = (cw == -1) ? parent.w - padL - padR
                        : (cw == -2) ? intrinsicW(child) : cw;
            }
        }

        /* Recurse into laid-out children. */
        for (WestlakeNode child : parent.children) {
            layoutChildren(child);
        }
    }

    private static boolean resolveVertical(WestlakeNode parent, WestlakeNode child,
            int padT, int padB, boolean[] xDone, boolean[] yDone) {
        int ch = child.getDimAttr("layout_height", -2);
        String topTo = child.getAttr("layout_constraintTop_toTopOf");
        String botTo = child.getAttr("layout_constraintBottom_toBottomOf");
        String topToBot = child.getAttr("layout_constraintTop_toBottomOf");
        String botToTop = child.getAttr("layout_constraintBottom_toTopOf");

        int mT = child.getDimAttr("layout_marginTop", 0);
        int mB = child.getDimAttr("layout_marginBottom", 0);
        int mAll = child.getDimAttr("layout_margin", -1);
        if (mAll >= 0) { mT = mB = mAll; }

        int parentTop = parent.y + padT;
        int parentBottom = parent.y + parent.h - padB;

        Integer topY = null;     /* requested top of child's bbox */
        Integer botY = null;     /* requested bottom of child's bbox */

        if (isParentRef(topTo)) topY = parentTop + mT;
        else if (topToBot != null) {
            int idx = findSiblingIndex(parent, topToBot);
            if (idx < 0 || !yDone[idx]) return false;
            WestlakeNode sib = parent.children.get(idx);
            topY = sib.y + sib.h + mT;
        }

        if (isParentRef(botTo)) botY = parentBottom - mB;
        else if (botToTop != null) {
            int idx = findSiblingIndex(parent, botToTop);
            if (idx < 0 || !yDone[idx]) return false;
            WestlakeNode sib = parent.children.get(idx);
            botY = sib.y - mB;
        }

        int hPx = (ch == -1) ? parent.h - padT - padB
                : (ch == -2) ? intrinsicH(child) : ch;

        if (topY != null && botY != null) {
            /* Constrained on both ends — fill the span. */
            int span = botY - topY;
            if (span <= 0) span = hPx;
            if (ch == 0) hPx = span;
            child.y = topY;
            child.h = hPx;
            return true;
        }
        if (topY != null) { child.y = topY; child.h = hPx; return true; }
        if (botY != null) { child.y = botY - hPx; child.h = hPx; return true; }
        return false;
    }

    private static boolean resolveHorizontal(WestlakeNode parent, WestlakeNode child,
            int padL, int padR, boolean[] xDone, boolean[] yDone) {
        int cw = child.getDimAttr("layout_width", -2);
        String startTo = child.getAttr("layout_constraintStart_toStartOf");
        String endTo = child.getAttr("layout_constraintEnd_toEndOf");
        String startToEnd = child.getAttr("layout_constraintStart_toEndOf");
        String endToStart = child.getAttr("layout_constraintEnd_toStartOf");
        String leftTo = child.getAttr("layout_constraintLeft_toLeftOf");
        String rightTo = child.getAttr("layout_constraintRight_toRightOf");
        if (startTo == null) startTo = leftTo;
        if (endTo == null) endTo = rightTo;

        int mL = child.getDimAttr("layout_marginStart", 0);
        int mR = child.getDimAttr("layout_marginEnd", 0);
        int mAll = child.getDimAttr("layout_margin", -1);
        if (mAll >= 0) { mL = mR = mAll; }

        int parentLeft = parent.x + padL;
        int parentRight = parent.x + parent.w - padR;

        Integer leftX = null;
        Integer rightX = null;

        if (isParentRef(startTo)) leftX = parentLeft + mL;
        else if (startToEnd != null) {
            int idx = findSiblingIndex(parent, startToEnd);
            if (idx < 0 || !xDone[idx]) return false;
            WestlakeNode sib = parent.children.get(idx);
            leftX = sib.x + sib.w + mL;
        }

        if (isParentRef(endTo)) rightX = parentRight - mR;
        else if (endToStart != null) {
            int idx = findSiblingIndex(parent, endToStart);
            if (idx < 0 || !xDone[idx]) return false;
            WestlakeNode sib = parent.children.get(idx);
            rightX = sib.x - mR;
        }

        int wPx = (cw == -1) ? parent.w - padL - padR
                : (cw == -2) ? intrinsicW(child) : cw;

        if (leftX != null && rightX != null) {
            int span = rightX - leftX;
            if (span <= 0) span = wPx;
            if (cw == 0) wPx = span;
            child.x = leftX;
            child.w = wPx;
            return true;
        }
        if (leftX != null) { child.x = leftX; child.w = wPx; return true; }
        if (rightX != null) { child.x = rightX - wPx; child.w = wPx; return true; }
        return false;
    }

    private static int findSiblingIndex(WestlakeNode parent, String idRef) {
        if (parent == null || idRef == null) return -1;
        for (int i = 0; i < parent.children.size(); i++) {
            WestlakeNode c = parent.children.get(i);
            String id = c.getAttr("id");
            if (id == null) continue;
            if (id.equals(idRef)) return i;
            if (stripPrefix(id).equals(stripPrefix(idRef))) return i;
        }
        return -1;
    }

    private static boolean isParentRef(String ref) {
        if (ref == null) return false;
        /* "parent" or the binary-AXML int "0" both mean parent. */
        return "parent".equals(ref) || "0".equals(ref);
    }

    private static WestlakeNode findSiblingById(WestlakeNode parent, String idRef) {
        if (parent == null || idRef == null) return null;
        for (WestlakeNode c : parent.children) {
            String id = c.getAttr("id");
            if (id == null) continue;
            if (id.equals(idRef)) return c;
            /* Also try numeric match — both might be "@0xHEX" form. */
            String a = stripPrefix(id);
            String b = stripPrefix(idRef);
            if (a.equals(b)) return c;
        }
        return null;
    }

    private static String stripPrefix(String s) {
        String r = s;
        if (r.startsWith("@")) r = r.substring(1);
        if (r.startsWith("+id/")) r = r.substring(4);
        if (r.startsWith("id/")) r = r.substring(3);
        if (r.startsWith("0x") || r.startsWith("0X")) r = r.substring(2);
        return r;
    }

    /** Rough intrinsic-width guess for leaf views. */
    private static int intrinsicW(WestlakeNode node) {
        if ("TextView".equals(node.tag) || "Button".equals(node.tag)
                || node.tag.endsWith("TextView") || node.tag.endsWith("Button")) {
            String text = node.getAttr("text", "");
            return Math.max(20, text.length() * 10);
        }
        if ("ImageView".equals(node.tag) || node.tag.endsWith("ImageView")) {
            return 48;
        }
        return 100;
    }

    private static float parseFloat(String s, float defaultValue) {
        if (s == null || s.isEmpty()) return defaultValue;
        try { return Float.parseFloat(s); }
        catch (NumberFormatException nfe) { return defaultValue; }
    }

    /** Rough intrinsic-height guess for leaf views. */
    private static int intrinsicH(WestlakeNode node) {
        if ("TextView".equals(node.tag) || "Button".equals(node.tag)
                || node.tag.endsWith("TextView") || node.tag.endsWith("Button")) {
            int size = node.getDimAttr("textSize", 16);
            return size + 8;
        }
        if ("ImageView".equals(node.tag) || node.tag.endsWith("ImageView")) {
            return 48;
        }
        return 40;
    }
}
