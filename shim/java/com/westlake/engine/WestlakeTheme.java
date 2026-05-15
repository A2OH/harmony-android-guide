/*
 * PF-arch-031: Minimal theme attribute resolver.
 *
 * View XML uses `?attr/colorPrimary` or `?0x7f04013f` to reference theme
 * attributes. The activity's theme XML maps attr IDs to concrete values
 * (colors, dimens, drawable refs). We parse the theme XML once, build a
 * lookup map, then translate `?` references to their concrete values.
 *
 * Theme XML structure:
 *   <style name="Theme.Foo" parent="Theme.Bar">
 *       <item name="colorPrimary">#FFBB0000</item>
 *       <item name="android:colorPrimary">@color/primary</item>
 *       ...
 *   </style>
 *
 * We currently parse one theme (no parent chaining yet) and return values
 * as raw strings — the renderer's existing resolveColor/resolveString
 * already handles hex/refs.
 */
package com.westlake.engine;

import android.content.res.ResourceTable;
import java.util.HashMap;
import java.util.Map;

public final class WestlakeTheme {
    /** Cached attr-id → value-string map. Populated by load(). */
    private final Map<Integer, String> attrs = new HashMap<Integer, String>();

    /** True once a theme has been successfully parsed. */
    private boolean loaded;

    public static WestlakeTheme load(int themeResId, String resDir, ResourceTable arsc) {
        WestlakeTheme t = new WestlakeTheme();
        if (themeResId == 0 || arsc == null) return t;
        /* PF-arch-032: themes live in arsc as bag entries, not separate XMLs.
         * Use the bag parser to get the resolved attr → value map (with
         * parent inheritance walked). */
        java.util.Map<Integer, String> bag = arsc.getStyleAttrs(themeResId);
        if (bag != null && !bag.isEmpty()) {
            t.attrs.putAll(bag);
        }
        t.loaded = !t.attrs.isEmpty();
        return t;
    }

    /** Resolve "?0xRESID" via the loaded theme. Returns null if unset. */

    public String resolve(String ref) {
        if (ref == null || ref.isEmpty() || !ref.startsWith("?")) return null;
        int id = parseAttrId(ref);
        if (id == 0) return null;
        return attrs.get(id);
    }

    public boolean isLoaded() { return loaded; }

    public int size() { return attrs.size(); }

    private static int parseAttrId(String ref) {
        if (ref == null) return 0;
        String s = ref;
        if (s.startsWith("?") || s.startsWith("@")) s = s.substring(1);
        if (s.startsWith("0x") || s.startsWith("0X")) {
            try { return (int) Long.parseLong(s.substring(2), 16); }
            catch (NumberFormatException nfe) { return 0; }
        }
        try { return (int) Long.parseLong(s); }
        catch (NumberFormatException nfe) { return 0; }
    }
}
