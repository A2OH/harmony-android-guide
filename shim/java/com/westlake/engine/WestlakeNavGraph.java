/*
 * PF-arch-027: Nav graph resolver.
 *
 * `androidx.fragment.app.FragmentContainerView` with `app:navGraph="@..."`
 * is the Jetpack Navigation Component pattern. At runtime, NavController
 * reads the graph XML, picks the `app:startDestination`, and instantiates
 * the corresponding `<fragment>`/`<dialog>` entry's `android:name` class.
 *
 * Westlake doesn't run NavController. We do the same lookup ourselves
 * by parsing the nav graph AXML and returning the start destination's
 * fragment class name + its layout (looked up later via reflection).
 */
package com.westlake.engine;

import android.content.res.ResourceTable;

public final class WestlakeNavGraph {
    private WestlakeNavGraph() {}

    public static final class StartDestination {
        public final String fragmentClassName;   // android:name
        public final String label;               // android:label, if present
        public StartDestination(String cls, String label) {
            this.fragmentClassName = cls;
            this.label = label;
        }
    }

    /** Resolve nav-graph resId → start destination. Returns null if unresolvable. */
    public static StartDestination resolve(int navGraphResId, String resDir, ResourceTable arsc) {
        if (arsc == null || resDir == null || navGraphResId == 0) return null;
        String path = arsc.getEntryFilePath(navGraphResId);
        if (path == null || path.isEmpty()) return null;
        java.io.File f = new java.io.File(resDir + "/" + path);
        if (!f.isFile()) return null;
        byte[] data;
        try {
            java.io.FileInputStream fis = new java.io.FileInputStream(f);
            try {
                long size = f.length();
                if (size <= 0 || size > 4 * 1024 * 1024) return null;
                data = new byte[(int) size];
                int off = 0;
                while (off < data.length) {
                    int r = fis.read(data, off, data.length - off);
                    if (r < 0) break;
                    off += r;
                }
            } finally {
                try { fis.close(); } catch (java.io.IOException ignored) {}
            }
        } catch (java.io.IOException ioe) {
            return null;
        }
        WestlakeNode graphRoot = WestlakeInflater.inflate(data);
        if (graphRoot == null) return null;
        /* The root is typically <navigation startDestination="@id/..."> with
         * children <fragment id="@id/..." name="..." />.
         * Strategy: read startDestination attribute, then find the child whose
         * id matches. */
        String startIdAttr = graphRoot.getAttr("startDestination");
        if (startIdAttr == null) {
            /* No startDestination — fall back to first <fragment>. */
            return firstFragmentEntry(graphRoot);
        }
        WestlakeNode match = findChildWithId(graphRoot, startIdAttr);
        if (match == null) {
            return firstFragmentEntry(graphRoot);
        }
        return entryFromNode(match);
    }

    private static StartDestination firstFragmentEntry(WestlakeNode parent) {
        for (WestlakeNode c : parent.children) {
            if ("fragment".equals(c.tag) || "dialog".equals(c.tag)
                    || "activity".equals(c.tag) || c.tag.endsWith("fragment")) {
                return entryFromNode(c);
            }
        }
        return null;
    }

    private static StartDestination entryFromNode(WestlakeNode node) {
        if (node == null) return null;
        String name = node.getAttr("name");
        if (name == null) name = node.getAttr("class");
        if (name == null) return null;
        String label = node.getAttr("label", "");
        return new StartDestination(name, label);
    }

    private static WestlakeNode findChildWithId(WestlakeNode parent, String targetIdRef) {
        if (parent == null || targetIdRef == null) return null;
        /* targetIdRef may be "@id/foo", "@+id/foo", or numeric like "@0x7f..." */
        for (WestlakeNode c : parent.children) {
            String childId = c.getAttr("id");
            if (childId == null) continue;
            if (idsMatch(targetIdRef, childId)) return c;
        }
        return null;
    }

    private static boolean idsMatch(String a, String b) {
        if (a == null || b == null) return false;
        if (a.equals(b)) return true;
        /* Strip @ and @+id/, also trim 0x prefix. */
        return normalizeId(a).equals(normalizeId(b));
    }

    private static String normalizeId(String s) {
        if (s == null) return "";
        String r = s;
        if (r.startsWith("@")) r = r.substring(1);
        if (r.startsWith("+id/")) r = r.substring(4);
        if (r.startsWith("id/")) r = r.substring(3);
        if (r.startsWith("0x")) r = r.substring(2);
        if (r.startsWith("0X")) r = r.substring(2);
        return r;
    }
}
