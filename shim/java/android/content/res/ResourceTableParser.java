package android.content.res;

import java.util.HashMap;
import java.util.Map;

/**
 * ResourceTableParser -- convenience facade that parses a resources.arsc
 * binary blob and populates a {@link Resources} instance with the discovered
 * string / color / integer / dimension values.
 *
 * Internally delegates to {@link ResourceTable} for the heavy lifting of
 * binary parsing (chunk walking, string-pool decoding, entry extraction).
 * This class adds:
 *   - Automatic registration of parsed values into {@code Resources}
 *     via registerStringResource / registerColorResource / registerIntegerResource.
 *   - A raw {@code parseToMap} helper that returns every resource ID mapped
 *     to its Java value (String or Integer).
 *
 * Pure Java, no JNI, no lambdas, no String.format, no String.split.
 */
public class ResourceTableParser {

    // Res_value dataType constants (duplicated here for classification)
    private static final int TYPE_STRING         = 0x03;
    private static final int TYPE_INT_DEC        = 0x10;
    private static final int TYPE_INT_HEX        = 0x11;
    private static final int TYPE_INT_BOOL       = 0x12;
    private static final int TYPE_INT_COLOR_ARGB8 = 0x1c;
    private static final int TYPE_INT_COLOR_RGB8  = 0x1d;
    private static final int TYPE_INT_COLOR_ARGB4 = 0x1e;
    private static final int TYPE_INT_COLOR_RGB4  = 0x1f;
    private static final int TYPE_FLOAT          = 0x04;
    private static final int TYPE_DIMENSION      = 0x05;
    private static final int TYPE_FRACTION       = 0x06;
    private static final int TYPE_REFERENCE      = 0x01;

    /**
     * Parse a resources.arsc byte array and populate the given
     * {@link Resources} instance with all discovered values.
     *
     * String-type resources are registered via
     * {@link Resources#registerStringResource(int, String)}.
     * Color resources (ARGB8/RGB8/ARGB4/RGB4) via
     * {@link Resources#registerColorResource(int, int)}.
     * All other integer-like resources via
     * {@link Resources#registerIntegerResource(int, int)}.
     *
     * The underlying {@link ResourceTable} is also attached to the
     * Resources instance via {@link Resources#loadResourceTable(ResourceTable)}.
     *
     * @param data       raw bytes of the resources.arsc file
     * @param resources  target Resources instance to populate
     */
    public static void parse(byte[] data, Resources resources) {
        if (data == null || resources == null) return;
        if (data.length < 12) return;

        // If Resources already has a table, parse INTO it (merging split resources)
        ResourceTable table = resources.getResourceTable();
        if (table != null) {
            table.parse(data);
        } else {
            table = new ResourceTable();
            table.parse(data);
            resources.loadResourceTable(table);
        }

        // Walk all possible resource IDs that the table knows about.
        // ResourceTable exposes getString / getInteger; we probe the
        // ID space by iterating the names map (every entry that was
        // successfully parsed gets a name).
        //
        // We use a slightly indirect approach: iterate a broad range
        // for each package+type that seems populated. Because
        // ResourceTable doesn't expose an iterator, we detect which
        // IDs have resources by calling hasResource().
        //
        // For efficiency, scan the range that AOSP uses: package 0x7f,
        // type 0x01..0x1f, entry 0x0000..0xFFFF (but cap at 0x0FFF
        // per type since most apps don't exceed ~4K entries per type).
        //
        // This is intentionally broad but bounded; the hash lookups are
        // O(1) so even 32 x 4096 = 131072 probes is cheap.

        for (int tt = 1; tt <= 0x1f; tt++) {
            for (int ee = 0; ee <= 0x0FFF; ee++) {
                int resId = 0x7f000000 | (tt << 16) | ee;
                if (!table.hasResource(resId)) continue;

                // String resource?
                String strVal = table.getString(resId);
                if (strVal != null) {
                    resources.registerStringResource(resId, strVal);
                    continue;
                }

                // Integer resource (color, int, bool, dimension, etc.)
                // getInteger returns defValue when missing; use a sentinel.
                int sentinel = 0x7EADBEEF;
                int intVal = table.getInteger(resId, sentinel);
                if (intVal != sentinel) {
                    // Classify as color or generic integer based on resource name
                    String name = table.getResourceName(resId);
                    if (name != null && name.startsWith("color")) {
                        resources.registerColorResource(resId, intVal);
                    } else {
                        resources.registerIntegerResource(resId, intVal);
                    }
                }
            }
        }

        // Also handle non-0x7f packages (system = 0x01, etc.)
        // Most apps only use 0x7f but let's be safe for package 0x01
        for (int pp = 0x01; pp < 0x7f; pp++) {
            for (int tt = 1; tt <= 0x1f; tt++) {
                for (int ee = 0; ee <= 0x00FF; ee++) {
                    int resId = (pp << 24) | (tt << 16) | ee;
                    if (!table.hasResource(resId)) continue;

                    String strVal = table.getString(resId);
                    if (strVal != null) {
                        resources.registerStringResource(resId, strVal);
                        continue;
                    }

                    int sentinel = 0x7EADBEEF;
                    int intVal = table.getInteger(resId, sentinel);
                    if (intVal != sentinel) {
                        String name = table.getResourceName(resId);
                        if (name != null && name.startsWith("color")) {
                            resources.registerColorResource(resId, intVal);
                        } else {
                            resources.registerIntegerResource(resId, intVal);
                        }
                    }
                }
            }
        }
    }

    /**
     * Parse a resources.arsc byte array and return a map of
     * resource ID to value. String resources map to {@link String},
     * integer-like resources map to {@link Integer}.
     *
     * @param data  raw bytes of the resources.arsc file
     * @return map from resource ID to value object, never null
     */
    public static Map<Integer, Object> parseToMap(byte[] data) {
        HashMap<Integer, Object> result = new HashMap<Integer, Object>();
        if (data == null || data.length < 12) return result;

        ResourceTable table = new ResourceTable();
        table.parse(data);

        // Same scanning approach as parse()
        scanIntoMap(table, result, 0x7f, 0x1f, 0x0FFF);

        // Non-0x7f packages (small range)
        for (int pp = 0x01; pp < 0x7f; pp++) {
            scanIntoMap(table, result, pp, 0x1f, 0x00FF);
        }

        return result;
    }

    /**
     * Parse and return the underlying ResourceTable directly,
     * for callers who need fine-grained access.
     *
     * @param data  raw bytes of the resources.arsc file
     * @return parsed ResourceTable, or null if data is invalid
     */
    public static ResourceTable parseToTable(byte[] data) {
        if (data == null || data.length < 12) return null;
        ResourceTable table = new ResourceTable();
        table.parse(data);
        return table;
    }

    // ── internal helpers ──────────────────────────────────────────────────

    private static void scanIntoMap(ResourceTable table, Map<Integer, Object> map,
                                     int packageId, int maxType, int maxEntry) {
        for (int tt = 1; tt <= maxType; tt++) {
            for (int ee = 0; ee <= maxEntry; ee++) {
                int resId = (packageId << 24) | (tt << 16) | ee;
                if (!table.hasResource(resId)) continue;

                String strVal = table.getString(resId);
                if (strVal != null) {
                    map.put(Integer.valueOf(resId), strVal);
                    continue;
                }

                int sentinel = 0x7EADBEEF;
                int intVal = table.getInteger(resId, sentinel);
                if (intVal != sentinel) {
                    map.put(Integer.valueOf(resId), Integer.valueOf(intVal));
                }
            }
        }
    }

    private ResourceTableParser() {
        // static-only utility class
    }
}
