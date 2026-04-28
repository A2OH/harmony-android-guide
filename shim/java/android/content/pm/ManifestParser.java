package android.content.pm;

import java.util.ArrayList;
import java.util.List;

/**
 * ManifestParser — parses Android binary XML (AXML) from an APK's AndroidManifest.xml.
 *
 * Extracts component declarations without Android framework dependencies (no XmlPullParser,
 * no AssetManager). Operates on raw AXML bytes suitable for the Westlake engine on ART11.
 *
 * Binary AXML format:
 *   Header:        magic (0x00080003), file size
 *   StringPool:    chunk type 0x0001 — UTF-8 or UTF-16 string table
 *   ResourceIds:   chunk type 0x0180 — maps attribute indices to android resource IDs
 *   XML tree:      START_NAMESPACE (0x0100), END_NAMESPACE (0x0101),
 *                  START_ELEMENT (0x0102), END_ELEMENT (0x0103),
 *                  CDATA (0x0104)
 */
public class ManifestParser {

    // AXML chunk types
    private static final int CHUNK_STRINGPOOL      = 0x001C0001;
    private static final int CHUNK_RESOURCEIDS     = 0x00080180;
    private static final int CHUNK_START_NAMESPACE = 0x00100100;
    private static final int CHUNK_END_NAMESPACE   = 0x00100101;
    private static final int CHUNK_START_ELEMENT   = 0x00100102;
    private static final int CHUNK_END_ELEMENT     = 0x00100103;
    private static final int CHUNK_CDATA           = 0x00100104;

    // android:name resource ID
    private static final int ATTR_NAME = 0x01010003;
    private static final int ATTR_APP_COMPONENT_FACTORY = 0x0101057A;

    /** Result of parsing an AndroidManifest.xml */
    public static class ManifestInfo {
        public String packageName;
        public String applicationClass;
        public String appComponentFactoryClass;
        public List<String> activities = new ArrayList<>();
        public List<String> providers = new ArrayList<>();
        public List<String> services = new ArrayList<>();

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ManifestInfo{package=").append(packageName);
            if (applicationClass != null) {
                sb.append(", application=").append(applicationClass);
            }
            if (appComponentFactoryClass != null) {
                sb.append(", factory=").append(appComponentFactoryClass);
            }
            sb.append(", activities=").append(activities);
            sb.append(", providers=").append(providers);
            sb.append(", services=").append(services);
            sb.append('}');
            return sb.toString();
        }
    }

    /**
     * Parse raw AXML bytes (e.g. from AndroidManifest.xml inside an APK).
     *
     * @param axmlData the raw binary XML bytes
     * @return extracted manifest information
     * @throws IllegalArgumentException if the data is not valid AXML
     */
    public static ManifestInfo parse(byte[] axmlData) {
        if (axmlData == null || axmlData.length < 8) {
            throw new IllegalArgumentException("AXML data too short");
        }

        int magic = readInt(axmlData, 0);
        if (magic != 0x00080003) {
            throw new IllegalArgumentException(
                    "Bad AXML magic: 0x" + Integer.toHexString(magic));
        }

        // int fileSize = readInt(axmlData, 4);
        ManifestInfo info = new ManifestInfo();
        String[] stringPool = null;
        int[] resourceIds = null;

        int pos = 8; // skip magic + fileSize
        while (pos < axmlData.length) {
            if (pos + 8 > axmlData.length) break;

            int chunkType = readInt(axmlData, pos);
            int chunkSize = readInt(axmlData, pos + 4);
            if (chunkSize < 8) break; // corrupt

            switch (chunkType) {
                case CHUNK_STRINGPOOL:
                    stringPool = parseStringPool(axmlData, pos);
                    break;

                case CHUNK_RESOURCEIDS:
                    resourceIds = parseResourceIds(axmlData, pos, chunkSize);
                    break;

                case CHUNK_START_ELEMENT:
                    parseStartElement(axmlData, pos, stringPool, resourceIds, info);
                    break;

                // START_NAMESPACE, END_NAMESPACE, END_ELEMENT, CDATA — skip
                default:
                    break;
            }

            pos += chunkSize;
        }

        return info;
    }

    // ── String Pool ─────────────────────────────────────────────────────────

    private static String[] parseStringPool(byte[] data, int offset) {
        // StringPool header:
        //   0: chunk type (4)
        //   4: chunk size (4)
        //   8: string count (4)
        //  12: style count (4)
        //  16: flags (4) — bit 0 = sorted, bit 8 = UTF-8
        //  20: strings start offset (relative to chunk start) (4)
        //  24: styles start offset (4)
        //  28: string offset array [stringCount] (4 each)

        int stringCount = readInt(data, offset + 8);
        // int styleCount = readInt(data, offset + 12);
        int flags = readInt(data, offset + 16);
        boolean isUtf8 = (flags & (1 << 8)) != 0;
        int stringsStart = readInt(data, offset + 20) + offset;
        // int stylesStart = readInt(data, offset + 24);

        int[] stringOffsets = new int[stringCount];
        for (int i = 0; i < stringCount; i++) {
            stringOffsets[i] = readInt(data, offset + 28 + i * 4);
        }

        String[] pool = new String[stringCount];
        for (int i = 0; i < stringCount; i++) {
            int strOff = stringsStart + stringOffsets[i];
            if (isUtf8) {
                pool[i] = readUtf8String(data, strOff);
            } else {
                pool[i] = readUtf16String(data, strOff);
            }
        }

        return pool;
    }

    /**
     * Read a UTF-8 string from the string pool.
     * Format: varint charCount, varint byteCount, then byteCount bytes of UTF-8, then 0x00.
     */
    private static String readUtf8String(byte[] data, int offset) {
        int pos = offset;

        // Skip char count (encoded as 1 or 2 bytes)
        if ((data[pos] & 0x80) != 0) {
            pos += 2;
        } else {
            pos += 1;
        }

        // Read byte count (encoded as 1 or 2 bytes)
        int byteCount;
        if ((data[pos] & 0x80) != 0) {
            byteCount = ((data[pos] & 0x7F) << 8) | (data[pos + 1] & 0xFF);
            pos += 2;
        } else {
            byteCount = data[pos] & 0xFF;
            pos += 1;
        }

        if (byteCount == 0) return "";
        if (pos + byteCount > data.length) byteCount = data.length - pos;

        try {
            return new String(data, pos, byteCount, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return new String(data, pos, byteCount);
        }
    }

    /**
     * Read a UTF-16LE string from the string pool.
     * Format: uint16 charCount (or 2x uint16 if high bit set), then charCount*2 bytes of UTF-16LE, then 0x0000.
     */
    private static String readUtf16String(byte[] data, int offset) {
        int pos = offset;
        int charCount;

        int first = readShort(data, pos);
        if ((first & 0x8000) != 0) {
            // High bit set: next two bytes are the low word of a 32-bit length
            int second = readShort(data, pos + 2);
            charCount = ((first & 0x7FFF) << 16) | second;
            pos += 4;
        } else {
            charCount = first;
            pos += 2;
        }

        if (charCount == 0) return "";

        char[] chars = new char[charCount];
        for (int i = 0; i < charCount; i++) {
            if (pos + 2 > data.length) break;
            chars[i] = (char) readShort(data, pos);
            pos += 2;
        }

        return new String(chars);
    }

    // ── Resource ID Table ───────────────────────────────────────────────────

    private static int[] parseResourceIds(byte[] data, int offset, int chunkSize) {
        // Header is 8 bytes (type + size), rest are uint32 resource IDs
        int count = (chunkSize - 8) / 4;
        int[] ids = new int[count];
        for (int i = 0; i < count; i++) {
            ids[i] = readInt(data, offset + 8 + i * 4);
        }
        return ids;
    }

    // ── Element Parsing ─────────────────────────────────────────────────────

    private static void parseStartElement(byte[] data, int offset,
            String[] stringPool, int[] resourceIds, ManifestInfo info) {
        // START_ELEMENT layout:
        //   0: chunk type (4)
        //   4: chunk size (4)
        //   8: line number (4)
        //  12: comment index (4)
        //  16: namespace index (4)  — -1 if none
        //  20: name index (4)       — element tag name in string pool
        //  24: attribute start (2)  — offset to first attribute (from chunk + 16??... actually from start of attributes block)
        //  26: attribute size (2)   — size of each attribute (20 bytes)
        //  28: attribute count (2)
        //  30: id index (2)
        //  32: class index (2)
        //  34: style index (2)
        //  36: attributes begin here, each 20 bytes:
        //       0: namespace string index (4)
        //       4: name string index (4)
        //       8: value string index (4) — -1 if typed value only
        //      12: value type (4): size(2) + res0(1) + dataType(1)... actually: (size<<16)|(res0<<8)|type
        //      16: value data (4)

        if (stringPool == null) return;

        int nameIdx = readInt(data, offset + 20);
        if (nameIdx < 0 || nameIdx >= stringPool.length) return;
        String tagName = stringPool[nameIdx];

        int attrCount = readShort(data, offset + 28);

        // For "manifest" element, extract package attribute
        if ("manifest".equals(tagName)) {
            String pkg = findAttributeByName(data, offset, attrCount, stringPool,
                    "package", resourceIds, -1);
            if (pkg != null) {
                info.packageName = pkg;
            }
            return;
        }

        // For "application", extract android:name and android:appComponentFactory.
        if ("application".equals(tagName)) {
            String name = findAttributeByResId(data, offset, attrCount, stringPool,
                    resourceIds, ATTR_NAME);
            if (name == null) {
                // Fallback: find by attribute name string "name"
                name = findAttributeByName(data, offset, attrCount, stringPool,
                        "name", resourceIds, ATTR_NAME);
            }
            if (name != null) {
                info.applicationClass = resolveClassName(name, info.packageName);
            }
            String factory = findAttributeByResId(data, offset, attrCount, stringPool,
                    resourceIds, ATTR_APP_COMPONENT_FACTORY);
            if (factory == null) {
                factory = findAttributeByName(data, offset, attrCount, stringPool,
                        "appComponentFactory", resourceIds, ATTR_APP_COMPONENT_FACTORY);
            }
            if (factory != null) {
                info.appComponentFactoryClass = resolveClassName(factory, info.packageName);
            }
            return;
        }

        // For activity, provider, service — extract android:name
        if ("activity".equals(tagName) || "activity-alias".equals(tagName)) {
            String name = findAttributeByResId(data, offset, attrCount, stringPool,
                    resourceIds, ATTR_NAME);
            if (name == null) {
                name = findAttributeByName(data, offset, attrCount, stringPool,
                        "name", resourceIds, ATTR_NAME);
            }
            if (name != null) {
                info.activities.add(resolveClassName(name, info.packageName));
            }
        } else if ("provider".equals(tagName)) {
            String name = findAttributeByResId(data, offset, attrCount, stringPool,
                    resourceIds, ATTR_NAME);
            if (name == null) {
                name = findAttributeByName(data, offset, attrCount, stringPool,
                        "name", resourceIds, ATTR_NAME);
            }
            if (name != null) {
                info.providers.add(resolveClassName(name, info.packageName));
            }
        } else if ("service".equals(tagName)) {
            String name = findAttributeByResId(data, offset, attrCount, stringPool,
                    resourceIds, ATTR_NAME);
            if (name == null) {
                name = findAttributeByName(data, offset, attrCount, stringPool,
                        "name", resourceIds, ATTR_NAME);
            }
            if (name != null) {
                info.services.add(resolveClassName(name, info.packageName));
            }
        }
    }

    /**
     * Find an attribute value by matching the resource ID from the resourceIds table.
     * The resourceIds table maps attribute name string indices to android resource IDs.
     * If the attribute's name string index maps to the target resId, it's our attribute.
     */
    private static String findAttributeByResId(byte[] data, int elementOffset,
            int attrCount, String[] stringPool, int[] resourceIds, int targetResId) {
        if (resourceIds == null) return null;

        int attrOffset = elementOffset + 36;
        for (int i = 0; i < attrCount; i++) {
            int attrBase = attrOffset + i * 20;
            if (attrBase + 20 > data.length) break;

            int attrNameIdx = readInt(data, attrBase + 4);

            // Check if this attribute's name string index maps to our target resource ID
            if (attrNameIdx >= 0 && attrNameIdx < resourceIds.length
                    && resourceIds[attrNameIdx] == targetResId) {
                return getAttributeValue(data, attrBase, stringPool);
            }
        }
        return null;
    }

    /**
     * Find an attribute value by matching the attribute name string.
     * Used as a fallback when resource IDs are not available or don't match.
     */
    private static String findAttributeByName(byte[] data, int elementOffset,
            int attrCount, String[] stringPool, String targetName,
            int[] resourceIds, int expectedResId) {
        int attrOffset = elementOffset + 36;
        for (int i = 0; i < attrCount; i++) {
            int attrBase = attrOffset + i * 20;
            if (attrBase + 20 > data.length) break;

            int attrNameIdx = readInt(data, attrBase + 4);
            if (attrNameIdx < 0 || attrNameIdx >= stringPool.length) continue;

            if (targetName.equals(stringPool[attrNameIdx])) {
                // If we have an expected resource ID, verify it doesn't conflict
                // (the "name" string might be reused for non-android:name attributes)
                if (expectedResId >= 0 && resourceIds != null
                        && attrNameIdx < resourceIds.length
                        && resourceIds[attrNameIdx] != 0
                        && resourceIds[attrNameIdx] != expectedResId) {
                    continue; // Different resource ID, not our attribute
                }
                return getAttributeValue(data, attrBase, stringPool);
            }
        }
        return null;
    }

    /**
     * Extract the string value from an attribute at the given offset.
     * Prefers the raw string value (valueStringIdx); falls back to typed value data
     * if the type is TYPE_STRING.
     */
    private static String getAttributeValue(byte[] data, int attrBase, String[] stringPool) {
        int valueStringIdx = readInt(data, attrBase + 8);

        // If we have a direct string pool reference, use it
        if (valueStringIdx >= 0 && valueStringIdx < stringPool.length) {
            return stringPool[valueStringIdx];
        }

        // Otherwise check typed value
        // Typed value at offset +12: 2 bytes size, 1 byte res0, 1 byte type
        // Typed value data at offset +16
        int typedValueRaw = readInt(data, attrBase + 12);
        int dataType = (typedValueRaw >> 24) & 0xFF;
        int valueData = readInt(data, attrBase + 16);

        // TYPE_STRING = 0x03
        if (dataType == 0x03 && valueData >= 0 && valueData < stringPool.length) {
            return stringPool[valueData];
        }

        return null;
    }

    // ── Class Name Resolution ───────────────────────────────────────────────

    /**
     * Resolve a class name relative to the package.
     * - ".Foo" → "com.example.Foo"
     * - "Foo"  → "com.example.Foo"  (no dots at all)
     * - "com.example.Foo" → "com.example.Foo" (already fully qualified)
     */
    private static String resolveClassName(String name, String packageName) {
        if (name == null || name.isEmpty()) return name;

        if (name.startsWith(".")) {
            // Relative: ".MyActivity" → "com.example.MyActivity"
            return (packageName != null ? packageName : "") + name;
        }

        if (name.indexOf('.') < 0 && packageName != null) {
            // Short name: "MyActivity" → "com.example.MyActivity"
            return packageName + "." + name;
        }

        // Already fully qualified
        return name;
    }

    // ── Little-Endian Readers ───────────────────────────────────────────────

    private static int readInt(byte[] data, int offset) {
        if (offset + 4 > data.length) return 0;
        return (data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8)
                | ((data[offset + 2] & 0xFF) << 16)
                | ((data[offset + 3] & 0xFF) << 24);
    }

    private static int readShort(byte[] data, int offset) {
        if (offset + 2 > data.length) return 0;
        return (data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8);
    }
}
