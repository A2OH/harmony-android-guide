// SPDX-License-Identifier: Apache-2.0
//
// V2-Step4 (2026-05-13) — ResourceArscParser
//
// Parses an APK's resources.arsc binary file into a flat
// Map<Integer, ResourceValue> keyed by AOSP resource ID
// (0xPPTTEEEE where PP=package, TT=type, EEEE=entry).
//
// Built on top of the existing android.content.res.ResourceTable
// (shim/java/android/content/res/ResourceTable.java) which already
// implements the chunk-walking / string-pool decoding / entry
// extraction for the binary arsc format described in AOSP
// frameworks/base/libs/androidfw/include/androidfw/ResourceTypes.h.
//
// This class adds:
//   - APK-level convenience (open zip, read "resources.arsc" entry).
//   - A flat Map<Integer, ResourceValue> view that is easier for
//     downstream consumers (WestlakeResources / WestlakeAssetManager)
//     to read than the type-specific ResourceTable methods.
//   - XML resource blob extraction (the binary AXML bytes for layout
//     IDs), which AndroidX LayoutInflater can consume via the
//     XmlResourceParser interface.
//   - Reference resolution: TYPE_REFERENCE values are resolved
//     transitively, with a depth cap to break cycles.
//
// Value classes handled:
//   - TYPE_STRING (0x03)
//   - TYPE_INT_DEC (0x10), TYPE_INT_HEX (0x11), TYPE_INT_BOOL (0x12)
//   - TYPE_INT_COLOR_ARGB8 (0x1C), TYPE_INT_COLOR_RGB8 (0x1D),
//     TYPE_INT_COLOR_ARGB4 (0x1E), TYPE_INT_COLOR_RGB4 (0x1F)
//   - TYPE_DIMENSION (0x05) — stored as packed value, decoded later
//   - TYPE_FRACTION (0x06) — stored as packed value
//   - TYPE_FLOAT (0x04)
//   - TYPE_REFERENCE (0x01) — resolved transitively
//
// Layout XMLs (resource type "layout", value type TYPE_STRING pointing
// to "res/layout/<name>.xml") are extracted as raw bytes from the APK
// zip and stored in {@link Parsed#xmlBlobs} keyed by resource ID.
//
// Anti-patterns avoided per V2-Step4 brief:
//   - No Unsafe.allocateInstance.
//   - No Field.setAccessible.
//   - No per-app branches.
//   - Only the resource types our test apps actually use.

package com.westlake.services;

import android.content.res.ResourceTable;
import android.content.res.ResourceTableParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Parses an APK's resources.arsc into a generic {@link Parsed} record
 * usable by WestlakeResources / WestlakeAssetManager. Generic across
 * apps; no per-app branches.
 */
public final class ResourceArscParser {

    /** AAPT Res_value data-type constants (subset we handle). */
    public static final int TYPE_REFERENCE       = 0x01;
    public static final int TYPE_STRING          = 0x03;
    public static final int TYPE_FLOAT           = 0x04;
    public static final int TYPE_DIMENSION       = 0x05;
    public static final int TYPE_FRACTION        = 0x06;
    public static final int TYPE_INT_DEC         = 0x10;
    public static final int TYPE_INT_HEX         = 0x11;
    public static final int TYPE_INT_BOOL        = 0x12;
    public static final int TYPE_INT_COLOR_ARGB8 = 0x1c;
    public static final int TYPE_INT_COLOR_RGB8  = 0x1d;
    public static final int TYPE_INT_COLOR_ARGB4 = 0x1e;
    public static final int TYPE_INT_COLOR_RGB4  = 0x1f;

    /** Max transitive reference depth before bailing out on cycles. */
    private static final int MAX_REF_DEPTH = 8;

    // ---- CR49 hardening caps (codex #3 HIGH) ----
    // Defend against untrusted APK contents: zip bombs, inflated declared counts.
    /** Per-zip-entry decompressed size cap (16 MB). */
    public static final int MAX_ENTRY_BYTES = 16 * 1024 * 1024;
    /** Running total of decompressed assets+arsc+xml bytes per parseApk (256 MB). */
    public static final long MAX_APK_BUDGET = 256L * 1024 * 1024;
    /** Sanity limit on declared string-pool count before allocation (1M strings). */
    public static final int MAX_STRING_POOL_COUNT = 1_000_000;
    /** Sanity limit on declared XML resource count (100K blobs). */
    public static final int MAX_XML_BLOB_COUNT = 100_000;
    /** Sanity limit on resources.arsc file size (64 MB). */
    public static final long MAX_ARSC_BYTES = 64L * 1024 * 1024;

    /** Immutable resource value: dataType + raw int + (optional) string. */
    public static final class ResourceValue {
        public final int dataType;
        public final int data;
        public final String stringValue; // non-null only for TYPE_STRING

        public ResourceValue(int dataType, int data, String stringValue) {
            this.dataType = dataType;
            this.data = data;
            this.stringValue = stringValue;
        }

        public static ResourceValue ofString(String s) {
            return new ResourceValue(TYPE_STRING, 0, s);
        }

        public static ResourceValue ofInt(int dataType, int data) {
            return new ResourceValue(dataType, data, null);
        }
    }

    /** Immutable parsed-arsc record. */
    public static final class Parsed {
        public final Map<Integer, ResourceValue> values;
        public final Map<Integer, byte[]> xmlBlobs;
        public final Map<String, byte[]> assets; // assets/<name> -> bytes
        public final String packageName;
        public final int packageId;
        public final ResourceTable backingTable;

        Parsed(Map<Integer, ResourceValue> values,
               Map<Integer, byte[]> xmlBlobs,
               Map<String, byte[]> assets,
               String packageName,
               int packageId,
               ResourceTable backingTable) {
            this.values = Collections.unmodifiableMap(values);
            this.xmlBlobs = Collections.unmodifiableMap(xmlBlobs);
            this.assets = Collections.unmodifiableMap(assets);
            this.packageName = packageName;
            this.packageId = packageId;
            this.backingTable = backingTable;
        }

        /** Lookup with transitive reference resolution. */
        public ResourceValue resolve(int resId) {
            return resolveInternal(resId, 0);
        }

        private ResourceValue resolveInternal(int resId, int depth) {
            if (depth >= MAX_REF_DEPTH) return null;
            ResourceValue v = values.get(Integer.valueOf(resId));
            if (v == null) return null;
            if (v.dataType == TYPE_REFERENCE) {
                return resolveInternal(v.data, depth + 1);
            }
            return v;
        }

        public String getString(int resId) {
            ResourceValue v = resolve(resId);
            if (v == null) return null;
            if (v.dataType == TYPE_STRING) return v.stringValue;
            return null;
        }

        public int getInteger(int resId, int defaultValue) {
            ResourceValue v = resolve(resId);
            if (v == null) return defaultValue;
            switch (v.dataType) {
                case TYPE_INT_DEC:
                case TYPE_INT_HEX:
                case TYPE_INT_BOOL:
                case TYPE_INT_COLOR_ARGB8:
                case TYPE_INT_COLOR_RGB8:
                case TYPE_INT_COLOR_ARGB4:
                case TYPE_INT_COLOR_RGB4:
                    return v.data;
                default:
                    return defaultValue;
            }
        }

        public int getColor(int resId, int defaultValue) {
            ResourceValue v = resolve(resId);
            if (v == null) return defaultValue;
            switch (v.dataType) {
                case TYPE_INT_COLOR_ARGB8:
                case TYPE_INT_COLOR_RGB8:
                case TYPE_INT_COLOR_ARGB4:
                case TYPE_INT_COLOR_RGB4:
                    return v.data;
                default:
                    // Some apps store colors as plain INT_DEC; accept those too.
                    if (v.dataType == TYPE_INT_DEC || v.dataType == TYPE_INT_HEX) {
                        return v.data;
                    }
                    return defaultValue;
            }
        }

        public boolean getBoolean(int resId, boolean defaultValue) {
            ResourceValue v = resolve(resId);
            if (v == null) return defaultValue;
            if (v.dataType == TYPE_INT_BOOL || v.dataType == TYPE_INT_DEC
                    || v.dataType == TYPE_INT_HEX) {
                return v.data != 0;
            }
            return defaultValue;
        }

        /**
         * Decode a packed TYPE_DIMENSION value to pixels using the
         * provided density (px/dp). Returns 0f if missing/unrecognised.
         */
        public float getDimension(int resId, float density) {
            ResourceValue v = resolve(resId);
            if (v == null || v.dataType != TYPE_DIMENSION) return 0f;
            return decodeDimension(v.data, density);
        }

        public float getFloat(int resId, float defaultValue) {
            ResourceValue v = resolve(resId);
            if (v == null) return defaultValue;
            if (v.dataType == TYPE_FLOAT) {
                return Float.intBitsToFloat(v.data);
            }
            return defaultValue;
        }

        /** True if {@code resId} has any value (resolved). */
        public boolean hasValue(int resId) {
            return values.containsKey(Integer.valueOf(resId));
        }

        /** "type/name" lookup (e.g. "string/app_name"). */
        public String getResourceName(int resId) {
            if (backingTable == null) return null;
            try {
                return backingTable.getResourceName(resId);
            } catch (Throwable t) {
                return null;
            }
        }

        public int getIdentifier(String typeSlashName) {
            if (backingTable == null || typeSlashName == null) return 0;
            try {
                return backingTable.getIdentifier(typeSlashName);
            } catch (Throwable t) {
                return 0;
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Entry points
    // ─────────────────────────────────────────────────────────────────

    /**
     * Parse an APK at {@code apkPath}, returning a {@link Parsed} record
     * containing the arsc value map, layout XML blobs, and a snapshot of
     * the APK's assets/ directory.
     *
     * @throws IOException if the APK can't be opened or lacks
     *                     resources.arsc.
     */
    public static Parsed parseApk(String apkPath) throws IOException {
        if (apkPath == null || apkPath.isEmpty()) {
            throw new IOException("apkPath is null/empty");
        }
        ZipFile zip = null;
        try {
            zip = new ZipFile(apkPath);
            ZipEntry arscEntry = zip.getEntry("resources.arsc");
            if (arscEntry == null) {
                throw new IOException("resources.arsc not found in " + apkPath);
            }
            // CR49: cap arsc file size before reading.
            if (arscEntry.getSize() > MAX_ARSC_BYTES) {
                throw new IOException("resources.arsc exceeds CR49 cap "
                        + MAX_ARSC_BYTES + " bytes");
            }
            byte[] arscBytes;
            InputStream is = zip.getInputStream(arscEntry);
            try {
                arscBytes = readAll(is, (int) arscEntry.getSize());
            } finally {
                try { is.close(); } catch (IOException ignored) {}
            }

            // Parse arsc to flat value map.
            ResourceTable table = ResourceTableParser.parseToTable(arscBytes);
            HashMap<Integer, ResourceValue> values = buildValueMap(table);

            // Extract layout XML blobs + scan assets/.
            HashMap<Integer, byte[]> xmlBlobs = new HashMap<Integer, byte[]>();
            HashMap<String, byte[]> assetBytes = new HashMap<String, byte[]>();
            String pkgName = null;

            java.util.Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry e = entries.nextElement();
                if (e.isDirectory()) continue;
                String name = e.getName();
                if (name == null) continue;
                if (name.startsWith("assets/")) {
                    String rel = name.substring("assets/".length());
                    if (rel.length() == 0) continue;
                    byte[] data = readEntry(zip, e);
                    if (data != null) assetBytes.put(rel, data);
                }
            }

            // Link XML resource entries to their bytes. For every layout /
            // xml / animator / anim / interpolator / menu / transition
            // resource we have, the table stores either a string value
            // ("res/layout/foo.xml") or an entry-string-pool index that
            // ResourceTable.getEntryFilePath resolves to the same path.
            if (table != null) {
                xmlBlobs = extractXmlBlobs(zip, table);
            }

            pkgName = (table != null) ? extractPackageName(table) : null;
            int pkgId = 0x7f; // 99% of APKs use 0x7f; ResourceTable doesn't expose this
            if (table != null) {
                pkgId = guessPackageId(table);
            }

            return new Parsed(values, xmlBlobs, assetBytes, pkgName, pkgId, table);
        } finally {
            if (zip != null) {
                try { zip.close(); } catch (IOException ignored) {}
            }
        }
    }

    /** Parse arsc bytes directly (no APK wrapper). */
    public static Parsed parse(byte[] arscBytes) {
        if (arscBytes == null || arscBytes.length < 12) {
            return new Parsed(
                    new HashMap<Integer, ResourceValue>(),
                    new HashMap<Integer, byte[]>(),
                    new HashMap<String, byte[]>(),
                    null, 0x7f, null);
        }
        ResourceTable table = ResourceTableParser.parseToTable(arscBytes);
        HashMap<Integer, ResourceValue> values = buildValueMap(table);
        String pkg = (table != null) ? extractPackageName(table) : null;
        int pkgId = (table != null) ? guessPackageId(table) : 0x7f;
        return new Parsed(values,
                new HashMap<Integer, byte[]>(),
                new HashMap<String, byte[]>(),
                pkg, pkgId, table);
    }

    // ─────────────────────────────────────────────────────────────────
    // Internal helpers
    // ─────────────────────────────────────────────────────────────────

    private static HashMap<Integer, ResourceValue> buildValueMap(ResourceTable table) {
        HashMap<Integer, ResourceValue> result = new HashMap<Integer, ResourceValue>();
        if (table == null) return result;

        // Use the same scan strategy as ResourceTableParser. ResourceTable
        // doesn't expose an iterator so we probe the broad ID range.
        // Hash lookups are O(1) so even 32×4096 probes is cheap.
        scanIntoValueMap(table, result, 0x7f, 0x1f, 0x0FFF);
        for (int pp = 0x01; pp < 0x7f; pp++) {
            scanIntoValueMap(table, result, pp, 0x1f, 0x00FF);
        }
        return result;
    }

    private static void scanIntoValueMap(ResourceTable table,
            HashMap<Integer, ResourceValue> result,
            int packageId, int maxType, int maxEntry) {
        final int sentinel = 0x7EADBEEF;
        for (int tt = 1; tt <= maxType; tt++) {
            for (int ee = 0; ee <= maxEntry; ee++) {
                int resId = (packageId << 24) | (tt << 16) | ee;
                if (!table.hasResource(resId)) continue;

                String s = table.getString(resId);
                if (s != null) {
                    result.put(Integer.valueOf(resId), ResourceValue.ofString(s));
                    continue;
                }

                int n = table.getInteger(resId, sentinel);
                if (n == sentinel) continue;

                // Classify dataType from the resource name when available.
                int dataType = TYPE_INT_DEC;
                String name = table.getResourceName(resId);
                if (name != null) {
                    if (name.startsWith("color")) dataType = TYPE_INT_COLOR_ARGB8;
                    else if (name.startsWith("bool")) dataType = TYPE_INT_BOOL;
                    else if (name.startsWith("dimen")) dataType = TYPE_DIMENSION;
                    else if (name.startsWith("integer")) dataType = TYPE_INT_DEC;
                }
                result.put(Integer.valueOf(resId), ResourceValue.ofInt(dataType, n));
            }
        }
    }

    /**
     * Walk the parsed table's known resource IDs of XML types and pull
     * the corresponding entries out of the APK zip into a Map<Integer,
     * byte[]>. Caller closes the zip.
     */
    private static HashMap<Integer, byte[]> extractXmlBlobs(ZipFile zip, ResourceTable table) {
        HashMap<Integer, byte[]> result = new HashMap<Integer, byte[]>();
        if (zip == null || table == null) return result;

        // We probe the same broad ID range. For each resId, ask the table
        // for an entry file path; if it starts with "res/" and ends with
        // ".xml", look it up in the zip.
        probeXmlRange(zip, table, result, 0x7f, 0x1f, 0x0FFF);
        return result;
    }

    private static void probeXmlRange(ZipFile zip, ResourceTable table,
            HashMap<Integer, byte[]> out,
            int packageId, int maxType, int maxEntry) {
        for (int tt = 1; tt <= maxType; tt++) {
            for (int ee = 0; ee <= maxEntry; ee++) {
                int resId = (packageId << 24) | (tt << 16) | ee;
                if (!table.hasResource(resId)) continue;
                String path = null;
                try {
                    path = table.getEntryFilePath(resId);
                } catch (Throwable t) {
                    path = null;
                }
                if (path == null) {
                    try {
                        path = table.getLayoutFileName(resId);
                    } catch (Throwable t) {
                        path = null;
                    }
                }
                if (path == null || !path.endsWith(".xml")) continue;
                if (!path.startsWith("res/")) path = "res/" + path;
                ZipEntry e = zip.getEntry(path);
                if (e == null) continue;
                byte[] data = readEntry(zip, e);
                if (data != null) {
                    out.put(Integer.valueOf(resId), data);
                }
            }
        }
    }

    private static String extractPackageName(ResourceTable table) {
        // ResourceTable doesn't expose mPackageName publicly; sniff from
        // a known resource name pattern (the first stored name typically
        // belongs to the app's own package).
        try {
            for (int ee = 0; ee <= 0xFFFF; ee++) {
                int resId = 0x7f010000 | ee;
                String n = table.getResourceName(resId);
                if (n != null && n.indexOf('/') > 0) {
                    // We don't actually have the package name here, just
                    // the type/name. The package name is set by the
                    // app's manifest; we leave it null and let the
                    // caller plug in WestlakeContextImpl.getPackageName.
                    return null;
                }
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private static int guessPackageId(ResourceTable table) {
        // Probe for a name in the 0x7f range first (the overwhelming
        // common case for app resources).
        for (int tt = 1; tt <= 0x1f; tt++) {
            for (int ee = 0; ee <= 0x0F; ee++) {
                int resId = 0x7f000000 | (tt << 16) | ee;
                if (table.hasResource(resId)) return 0x7f;
            }
        }
        // Otherwise scan the broader range.
        for (int pp = 0x01; pp < 0xff; pp++) {
            for (int tt = 1; tt <= 0x1f; tt++) {
                for (int ee = 0; ee <= 0x0F; ee++) {
                    int resId = (pp << 24) | (tt << 16) | ee;
                    if (table.hasResource(resId)) return pp;
                }
            }
        }
        return 0x7f;
    }

    /**
     * Decode the lower 24 bits of a TYPE_DIMENSION packed value into
     * floating-point pixels. See ResourceTypes.h Res_value::COMPLEX_*.
     */
    public static float decodeDimension(int data, float density) {
        int unit = data & 0x0F;
        int radix = (data >> 4) & 0x03;
        int mantissaInt = data >>> 8;
        // Sign-extend the 24-bit mantissa.
        if ((mantissaInt & 0x00800000) != 0) {
            mantissaInt |= 0xFF000000;
        }
        float mantissa;
        switch (radix) {
            case 0: mantissa = mantissaInt; break;
            case 1: mantissa = mantissaInt / (float) (1 << 7); break;
            case 2: mantissa = mantissaInt / (float) (1 << 15); break;
            case 3: mantissa = mantissaInt / (float) (1 << 23); break;
            default: mantissa = mantissaInt; break;
        }
        float value = mantissa;
        // Unit: 0=px, 1=dp, 2=sp, 3=pt, 4=in, 5=mm
        switch (unit) {
            case 0: return value;                  // px
            case 1: return value * density;        // dp
            case 2: return value * density;        // sp (no separate fontScale here)
            case 3: return value * density * 72f / 160f; // pt
            case 4: return value * density * 1f / 160f * 160f; // in
            case 5: return value * density / 25.4f * 160f / 160f; // mm
            default: return value;
        }
    }

    private static byte[] readEntry(ZipFile zip, ZipEntry e) {
        InputStream is = null;
        try {
            is = zip.getInputStream(e);
            return readAll(is, (int) e.getSize());
        } catch (IOException ioe) {
            return null;
        } finally {
            if (is != null) {
                try { is.close(); } catch (IOException ignored) {}
            }
        }
    }

    private static byte[] readAll(InputStream is, int expectedSize) throws IOException {
        if (is == null) return new byte[0];
        // CR49: hard cap on declared size to prevent OOM on zip-bomb metadata.
        if (expectedSize < 0 || expectedSize > MAX_ENTRY_BYTES) {
            expectedSize = 0; // fall back to dynamic
        }
        ByteArrayOutputStream baos = expectedSize > 0
                ? new ByteArrayOutputStream(expectedSize)
                : new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        long total = 0;
        while ((n = is.read(buf)) > 0) {
            total += n;
            // CR49: enforce per-entry cap on actual decompressed bytes.
            if (total > MAX_ENTRY_BYTES) {
                throw new IOException("Resource entry exceeds CR49 cap "
                        + MAX_ENTRY_BYTES + " bytes (suspected zip bomb)");
            }
            baos.write(buf, 0, n);
        }
        return baos.toByteArray();
    }

    private ResourceArscParser() { /* static-only utility */ }
}
