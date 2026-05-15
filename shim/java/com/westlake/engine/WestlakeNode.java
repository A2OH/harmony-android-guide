/*
 * PF-arch-026: a layout-tree data class decoupled from android.view.View.
 *
 * Real View construction requires Resources/Context which we don't have in
 * standalone dalvikvm. Instead we represent the inflated layout as plain
 * data: tag, attributes, children. The renderer walks this tree and emits
 * OHBridge primitives directly — bypassing the View hierarchy.
 */
package com.westlake.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WestlakeNode {
    /** Element tag — "LinearLayout", "TextView", "ImageView", "Button", etc. */
    public final String tag;

    /** Attribute map keyed by attribute name (without "android:" prefix). */
    public final Map<String, String> attrs;

    /** Child nodes in document order. */
    public final List<WestlakeNode> children;

    /** Computed layout bounds in pixels (set by WestlakeLayout). Top-left + size. */
    public int x;
    public int y;
    public int w;
    public int h;

    public WestlakeNode(String tag) {
        this.tag = tag;
        this.attrs = new HashMap<String, String>();
        this.children = new ArrayList<WestlakeNode>();
    }

    public String getAttr(String name) {
        return attrs.get(name);
    }

    public String getAttr(String name, String defaultValue) {
        String v = attrs.get(name);
        return v != null ? v : defaultValue;
    }

    /** Resolve common color attributes — returns 0 if unset/unparseable. */
    public int getColorAttr(String name) {
        String v = attrs.get(name);
        if (v == null) return 0;
        if (v.startsWith("#")) {
            try {
                long parsed = Long.parseLong(v.substring(1), 16);
                /* #RGB → opaque, #RRGGBB → opaque, #AARRGGBB → as-is */
                int len = v.length() - 1;
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
        /* Hex int from binary XML — TV_TYPE_INT_COLOR_ARGB8 etc. arrives
         * formatted as plain decimal; try parsing as int. */
        try {
            return (int) Long.parseLong(v);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /** Resolve dimension to pixels — supports plain int, "Npx", "Ndp"
     * (treated 1:1 — no display density in our standalone). */
    public int getDimAttr(String name, int defaultValue) {
        String v = attrs.get(name);
        if (v == null) return defaultValue;
        if ("match_parent".equals(v) || "fill_parent".equals(v)) return -1;
        if ("wrap_content".equals(v)) return -2;
        try {
            int end = 0;
            while (end < v.length() && (v.charAt(end) == '-'
                    || (v.charAt(end) >= '0' && v.charAt(end) <= '9')
                    || v.charAt(end) == '.')) {
                end++;
            }
            if (end == 0) return defaultValue;
            String numStr = v.substring(0, end);
            int val = (int) Float.parseFloat(numStr);
            return val;
        } catch (Throwable t) {
            return defaultValue;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb, 0);
        return sb.toString();
    }

    private void toString(StringBuilder sb, int depth) {
        for (int i = 0; i < depth; i++) sb.append("  ");
        sb.append("<").append(tag);
        for (Map.Entry<String, String> e : attrs.entrySet()) {
            sb.append(" ").append(e.getKey()).append("=\"").append(e.getValue()).append("\"");
        }
        if (children.isEmpty()) {
            sb.append("/>\n");
        } else {
            sb.append(">\n");
            for (WestlakeNode c : children) c.toString(sb, depth + 1);
            for (int i = 0; i < depth; i++) sb.append("  ");
            sb.append("</").append(tag).append(">\n");
        }
    }
}
