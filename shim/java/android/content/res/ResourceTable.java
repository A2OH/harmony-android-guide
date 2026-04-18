package android.content.res;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ResourceTable — parses Android's resources.arsc binary format.
 *
 * resources.arsc structure:
 * - ResTable_header: type=0x0002, headerSize, size, packageCount
 * - ResStringPool (type=0x0001): Global string pool with all string values
 * - ResTable_package (type=0x0200): Package chunks containing:
 *   - Type string pool (type names: "attr", "drawable", "layout", "string", etc.)
 *   - Key string pool (key names: "app_name", "hello_world", etc.)
 *   - ResTable_typeSpec (type=0x0202): flags per entry for each type
 *   - ResTable_type (type=0x0201): actual entries mapping ID to value
 *
 * Resource ID format: 0xPPTTEEEE
 *   PP = package ID (0x7f for app)
 *   TT = type ID (1-based, from type string pool)
 *   EEEE = entry index
 */
public class ResourceTable {

    // Chunk types (just the low 16 bits — the header stores type as uint16)
    private static final int RES_STRING_POOL_TYPE       = 0x0001;
    private static final int RES_TABLE_TYPE             = 0x0002;
    private static final int RES_TABLE_PACKAGE_TYPE     = 0x0200;
    private static final int RES_TABLE_TYPE_TYPE        = 0x0201;
    private static final int RES_TABLE_TYPE_SPEC_TYPE   = 0x0202;

    // Res_value data types
    private static final int TYPE_STRING    = 0x03;
    private static final int TYPE_INT_DEC   = 0x10;
    private static final int TYPE_INT_HEX   = 0x11;
    private static final int TYPE_INT_BOOL  = 0x12;
    private static final int TYPE_INT_COLOR_ARGB8 = 0x1c;
    private static final int TYPE_INT_COLOR_RGB8  = 0x1d;
    private static final int TYPE_INT_COLOR_ARGB4 = 0x1e;
    private static final int TYPE_INT_COLOR_RGB4  = 0x1f;
    private static final int TYPE_DIMENSION = 0x05;
    private static final int TYPE_FRACTION  = 0x06;
    private static final int TYPE_FLOAT     = 0x04;
    private static final int TYPE_REFERENCE = 0x01;
    private static final int TYPE_ATTRIBUTE = 0x02;

    // Entry flags
    private static final int FLAG_COMPLEX = 0x0001;

    /** Maps resource ID to string value (for string-type resources). */
    /** Shared global strings map — all ResourceTable instances share the same data */
    private static final Map<Integer, String> sGlobalStrings = new HashMap<>();
    private final Map<Integer, String> mStrings = sGlobalStrings;

    /** Maps resource ID to integer value (colors, dimensions, ints, booleans). */
    private final Map<Integer, Integer> mIntegers = new HashMap<>();

    /** Maps resource ID to resource name ("type/key"). */
    private static final Map<Integer, String> sGlobalNames = new HashMap<>();
    private final Map<Integer, String> mNames = sGlobalNames;
    private static final Map<Integer, Integer> sGlobalEntryValues = new HashMap<>();
    private final Map<Integer, Integer> mEntryValues = sGlobalEntryValues;

    /** Global string pool (all string values). */
    private String[] mGlobalStringPool;
    private String[] mPrevStringPool; // saved base pool during split parsing

    /**
     * Parse a resources.arsc byte array.
     */
    public void parse(byte[] data) {
        if (data == null || data.length < 12) return;

        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        // ResTable_header: type(2), headerSize(2), size(4), packageCount(4)
        int tableType = buf.getShort() & 0xFFFF;
        int tableHeaderSize = buf.getShort() & 0xFFFF;
        int tableSize = buf.getInt();
        int packageCount = buf.getInt();

        if (tableType != RES_TABLE_TYPE) {
            throw new IllegalArgumentException(
                    "Not a resources.arsc: type=0x" + Integer.toHexString(tableType));
        }

        // Position past the table header (might be larger than 12 bytes)
        buf.position(tableHeaderSize);

        // Parse top-level chunks: global string pool + package chunks
        while (buf.position() < data.length && buf.remaining() >= 8) {
            int chunkStart = buf.position();
            int chunkType = buf.getShort() & 0xFFFF;
            int chunkHeaderSize = buf.getShort() & 0xFFFF;
            int chunkSize = buf.getInt();

            if (chunkSize < 8 || chunkStart + chunkSize > data.length) {
                break; // corrupt chunk
            }

            switch (chunkType) {
                case RES_STRING_POOL_TYPE:
                    // For split resources, save the OLD pool and restore after parsing
                    mPrevStringPool = mGlobalStringPool;
                    mGlobalStringPool = parseStringPool(buf, chunkStart, chunkHeaderSize, chunkSize);
                    break;

                case RES_TABLE_PACKAGE_TYPE:
                    parsePackage(buf, data, chunkStart, chunkHeaderSize, chunkSize);
                    break;

                default:
                    // Skip unknown top-level chunk
                    break;
            }

            buf.position(chunkStart + chunkSize);
        }
    }

    /**
     * Get a string resource value by resource ID.
     */
    public String getString(int resId) {
        return mStrings.get(resId);
    }

    /**
     * Get an integer resource value (color, int, boolean, dimension) by resource ID.
     */
    public int getInteger(int resId, int defValue) {
        Integer v = mIntegers.get(resId);
        return v != null ? v : defValue;
    }

    /**
     * Check if a resource ID exists in this table.
     */
    public boolean hasResource(int resId) {
        return mStrings.containsKey(resId) || mIntegers.containsKey(resId);
    }

    /**
     * Get the resource name as "type/key" (e.g. "string/app_name").
     */
    public String getResourceName(int resId) {
        return mNames.get(resId);
    }

    /** Reverse lookup: find resource ID by "type/name" key */
    public int getIdentifier(String key) {
        for (Map.Entry<Integer, String> entry : mNames.entrySet()) {
            if (key.equals(entry.getValue())) return entry.getKey();
        }
        // Try partial match (just the name part)
        for (Map.Entry<Integer, String> entry : mNames.entrySet()) {
            String val = entry.getValue();
            if (val != null && val.endsWith("/" + key)) return entry.getKey();
            if (val != null && val.endsWith(key)) return entry.getKey();
        }
        return 0;
    }

    /**
     * Get the layout file name for a layout resource ID.
     * Returns e.g. "res/layout/activity_main.xml", or the raw string value if present.
     */
    public String getLayoutFileName(int resId) {
        // If we have an explicit string value (e.g. "res/layout/activity_main.xml"), return it
        String val = mStrings.get(resId);
        if (val != null) return val;

        // Otherwise, construct from resource name
        String name = mNames.get(resId);
        if (name != null && name.startsWith("layout/")) {
            return "res/" + name + ".xml";
        }
        return null;
    }

    /**
     * Get the file path for a resource entry (layout, drawable, etc.)
     * by looking up the raw string pool value for this resource ID.
     * This handles the case where the parser didn't store it in mStrings.
     */
    public String getEntryFilePath(int resId) {
        // First try the already-parsed mStrings
        String val = mStrings.get(resId);
        if (val != null && val.startsWith("res/")) return val;

        // Otherwise, look up directly in the raw data
        // The global string pool is stored in mGlobalStringPool
        // We need to find the entry for this resId in the type chunks
        // For now, try ALL string pool entries that start with "res/" and match
        // the type (layout = type 0x0e)
        int typeId = (resId >> 16) & 0xFF;
        int entryId = resId & 0xFFFF;
        // Walk mFilePathCache (lazy-built from raw data)
        if (mFilePathCache == null && mGlobalStringPool != null) {
            mFilePathCache = new java.util.HashMap<>();
            // Index all res/ paths in the global string pool
            for (int i = 0; i < mGlobalStringPool.length; i++) {
                String s = mGlobalStringPool[i];
                if (s != null && s.startsWith("res/") && s.endsWith(".xml")) {
                    mFilePathCache.put(i, s);
                }
            }
        }
        // Look up the entry's value data index in the raw arsc data
        // This requires re-parsing the type chunk for typeId
        // For a quick fix, check if we stored it in mEntryValues during initial parse
        if (mEntryValues != null) {
            Integer stringIdx = mEntryValues.get(resId);
            if (stringIdx != null && mGlobalStringPool != null && stringIdx >= 0 && stringIdx < mGlobalStringPool.length) {
                String path = mGlobalStringPool[stringIdx];
                if (path != null && path.startsWith("res/")) return path;
            }
        }
        return null;
    }

    private java.util.HashMap<Integer, String> mFilePathCache;

    /**
     * Get the number of string resources parsed.
     */
    public int getStringCount() {
        return mStrings.size();
    }

    /**
     * Get a color resource value by resource ID.
     * Returns the raw integer ARGB value, or defValue if not found.
     */
    public int getColor(int resId) {
        Integer v = mIntegers.get(resId);
        return v != null ? v.intValue() : 0xFF000000;
    }

    /**
     * Get a boolean resource value by resource ID.
     */
    public boolean getBoolean(int resId) {
        Integer v = mIntegers.get(resId);
        return v != null && v.intValue() != 0;
    }

    /**
     * Get a dimension resource value by resource ID, scaled by the given density.
     * The raw value is stored as a packed dimension; we decode and multiply by density.
     *
     * @param resId   the resource ID
     * @param density screen density (e.g. 1.0f for mdpi, 2.0f for xhdpi)
     * @return the dimension in pixels, or 0 if not found
     */
    public float getDimension(int resId, float density) {
        Integer v = mIntegers.get(resId);
        if (v == null) return 0f;
        int data = v.intValue();
        // Decode the packed dimension value
        int unitType = data & 0x0F;
        int radixType = (data >> 4) & 0x03;
        int mantissa = data >> 8;

        float value;
        switch (radixType) {
            case 0: value = mantissa; break;
            case 1: value = mantissa / 128.0f; break;
            case 2: value = mantissa / 32768.0f; break;
            case 3: value = mantissa / 8388608.0f; break;
            default: value = mantissa;
        }

        // Apply density scaling for dp/sp units
        switch (unitType) {
            case 0: return value; // px
            case 1: return value * density; // dp
            case 2: return value * density; // sp (simplified: same as dp)
            case 3: return value * density * 72.0f; // pt
            case 4: return value * density * 160.0f; // in
            case 5: return value * density * (160.0f / 25.4f); // mm
            default: return value;
        }
    }

    /**
     * Get the byte offset (in the APK) for a layout resource.
     * In practice this returns the integer value stored for the resource ID,
     * which for layout resources is typically a reference or file path index.
     * Returns -1 if not found.
     */
    public int getLayoutResourceOffset(int resId) {
        // Layout resources are typically stored as string references to file paths
        // (e.g. "res/layout/activity_main.xml") rather than byte offsets.
        // Return the integer value if present, which may be a reference.
        Integer v = mIntegers.get(resId);
        return v != null ? v.intValue() : -1;
    }

    /**
     * Get the entry name for a resource ID (e.g. "activity_main" from "layout/activity_main").
     */
    public String getResourceEntryName(int resId) {
        String name = mNames.get(resId);
        if (name == null) return null;
        int slash = name.indexOf('/');
        if (slash >= 0 && slash + 1 < name.length()) {
            return name.substring(slash + 1);
        }
        return name;
    }

    /**
     * Get the type name for a resource ID (e.g. "layout" from "layout/activity_main").
     */
    public String getResourceTypeName(int resId) {
        String name = mNames.get(resId);
        if (name == null) return null;
        int slash = name.indexOf('/');
        if (slash > 0) {
            return name.substring(0, slash);
        }
        return null;
    }

    /**
     * Get the number of integer resources parsed.
     */
    public int getIntegerCount() {
        return mIntegers.size();
    }

    /**
     * Get the global string pool (all string values from the resources.arsc).
     */
    public String[] getGlobalStringPool() {
        return mGlobalStringPool;
    }

    // -----------------------------------------------------------------------
    // String pool parsing (supports UTF-8 and UTF-16)
    // -----------------------------------------------------------------------

    private String[] parseStringPool(ByteBuffer buf, int chunkStart, int headerSize, int chunkSize) {
        // StringPool header (after chunk header which is 8 bytes):
        // stringCount(4), styleCount(4), flags(4), stringsStart(4), stylesStart(4)
        int stringCount = buf.getInt();
        int styleCount = buf.getInt();
        int flags = buf.getInt();
        int stringsStart = buf.getInt();
        int stylesStart = buf.getInt();

        boolean isUtf8 = (flags & (1 << 8)) != 0;

        // Read string offsets
        int[] offsets = new int[stringCount];
        for (int i = 0; i < stringCount; i++) {
            offsets[i] = buf.getInt();
        }

        // Skip style offsets
        for (int i = 0; i < styleCount; i++) {
            buf.getInt();
        }

        // stringsStart is relative to the chunk start + 8 (after the ResChunk_header type+headerSize+size)
        // Actually, stringsStart is an offset from the start of the chunk header
        int dataStart = chunkStart + stringsStart;

        String[] pool = new String[stringCount];
        for (int i = 0; i < stringCount; i++) {
            int pos = dataStart + offsets[i];
            if (pos < 0 || pos >= buf.limit()) {
                pool[i] = "";
                continue;
            }
            buf.position(pos);

            try {
                if (isUtf8) {
                    pool[i] = readUtf8String(buf);
                } else {
                    pool[i] = readUtf16String(buf);
                }
            } catch (Exception e) {
                pool[i] = "";
            }
        }

        return pool;
    }

    private String readUtf8String(ByteBuffer buf) {
        // UTF-8 string: charLen (1 or 2 bytes), byteLen (1 or 2 bytes), data, null terminator
        int charLen = buf.get() & 0xFF;
        if ((charLen & 0x80) != 0) {
            charLen = ((charLen & 0x7F) << 8) | (buf.get() & 0xFF);
        }
        int byteLen = buf.get() & 0xFF;
        if ((byteLen & 0x80) != 0) {
            byteLen = ((byteLen & 0x7F) << 8) | (buf.get() & 0xFF);
        }
        if (byteLen < 0 || buf.remaining() < byteLen) {
            return "";
        }
        byte[] strBytes = new byte[byteLen];
        buf.get(strBytes);
        try {
            return decodeUtf8(strBytes);
        } catch (Exception e) {
            return "";
        }
    }

    private String decodeUtf8(byte[] data) {
        char[] out = new char[data.length * 2];
        int in = 0;
        int outLen = 0;
        while (in < data.length) {
            int b0 = data[in] & 0xFF;
            if (b0 < 0x80) {
                out[outLen++] = (char) b0;
                in++;
                continue;
            }
            if ((b0 & 0xE0) == 0xC0 && in + 1 < data.length) {
                int b1 = data[in + 1] & 0x3F;
                out[outLen++] = (char) (((b0 & 0x1F) << 6) | b1);
                in += 2;
                continue;
            }
            if ((b0 & 0xF0) == 0xE0 && in + 2 < data.length) {
                int b1 = data[in + 1] & 0x3F;
                int b2 = data[in + 2] & 0x3F;
                out[outLen++] = (char) (((b0 & 0x0F) << 12) | (b1 << 6) | b2);
                in += 3;
                continue;
            }
            if ((b0 & 0xF8) == 0xF0 && in + 3 < data.length) {
                int b1 = data[in + 1] & 0x3F;
                int b2 = data[in + 2] & 0x3F;
                int b3 = data[in + 3] & 0x3F;
                int cp = ((b0 & 0x07) << 18) | (b1 << 12) | (b2 << 6) | b3;
                cp -= 0x10000;
                out[outLen++] = (char) (0xD800 | (cp >> 10));
                out[outLen++] = (char) (0xDC00 | (cp & 0x3FF));
                in += 4;
                continue;
            }
            out[outLen++] = '\uFFFD';
            in++;
        }
        return new String(out, 0, outLen);
    }

    private String readUtf16String(ByteBuffer buf) {
        // UTF-16 string: charLen (2 or 4 bytes), data, null terminator
        int charLen = buf.getShort() & 0xFFFF;
        if ((charLen & 0x8000) != 0) {
            charLen = ((charLen & 0x7FFF) << 16) | (buf.getShort() & 0xFFFF);
        }
        if (charLen < 0 || buf.remaining() < charLen * 2) {
            return "";
        }
        char[] chars = new char[charLen];
        for (int j = 0; j < charLen; j++) {
            chars[j] = (char) (buf.getShort() & 0xFFFF);
        }
        return new String(chars);
    }

    // -----------------------------------------------------------------------
    // Package parsing
    // -----------------------------------------------------------------------

    private void parsePackage(ByteBuffer buf, byte[] data, int chunkStart,
                               int chunkHeaderSize, int chunkSize) {
        // ResTable_package header (after chunk header):
        // id(4), name(256 bytes = 128 char16), typeStrings(4), lastPublicType(4),
        // keyStrings(4), lastPublicKey(4)
        int packageId = buf.getInt();

        // Package name: 128 UTF-16 chars = 256 bytes
        byte[] nameBytes = new byte[256];
        buf.get(nameBytes);
        // We don't need the package name string for parsing, skip decoding

        int typeStringsOffset = buf.getInt();
        int lastPublicType = buf.getInt();
        int keyStringsOffset = buf.getInt();
        int lastPublicKey = buf.getInt();

        // Position past the full package header (may be larger than what we've read)
        buf.position(chunkStart + chunkHeaderSize);

        // Parse type string pool and key string pool
        // They are at known offsets from the start of the package chunk
        String[] typeStringPool = null;
        String[] keyStringPool = null;

        // The typeStrings and keyStrings offsets are relative to the package chunk start
        int typeStringsPos = chunkStart + typeStringsOffset;
        int keyStringsPos = chunkStart + keyStringsOffset;

        // Parse type string pool
        if (typeStringsOffset > 0 && typeStringsPos < chunkStart + chunkSize) {
            buf.position(typeStringsPos);
            int tsType = buf.getShort() & 0xFFFF;
            int tsHeaderSize = buf.getShort() & 0xFFFF;
            int tsSize = buf.getInt();
            if (tsType == RES_STRING_POOL_TYPE && typeStringsPos + tsSize <= data.length) {
                typeStringPool = parseStringPool(buf, typeStringsPos, tsHeaderSize, tsSize);
            }
        }

        // Parse key string pool
        if (keyStringsOffset > 0 && keyStringsPos < chunkStart + chunkSize) {
            buf.position(keyStringsPos);
            int ksType = buf.getShort() & 0xFFFF;
            int ksHeaderSize = buf.getShort() & 0xFFFF;
            int ksSize = buf.getInt();
            if (ksType == RES_STRING_POOL_TYPE && keyStringsPos + ksSize <= data.length) {
                keyStringPool = parseStringPool(buf, keyStringsPos, ksHeaderSize, ksSize);
            }
        }

        // Now scan through the package chunk for typeSpec and type chunks
        // Start after the key string pool (or type string pool if no key pool)
        int scanStart = chunkStart + chunkHeaderSize;
        // We need to find where the actual type/typeSpec chunks start.
        // They come after both string pools. Find the end of the key string pool.
        if (keyStringsPos > 0 && keyStringsPos < chunkStart + chunkSize) {
            buf.position(keyStringsPos);
            if (buf.remaining() >= 8) {
                buf.getShort(); // type
                buf.getShort(); // headerSize
                int ksSize = buf.getInt();
                scanStart = keyStringsPos + ksSize;
            }
        }

        if (scanStart < chunkStart + chunkHeaderSize) {
            scanStart = chunkStart + chunkHeaderSize;
        }

        int packageEnd = chunkStart + chunkSize;
        buf.position(scanStart);

        while (buf.position() < packageEnd && buf.remaining() >= 8) {
            int subChunkStart = buf.position();
            int subType = buf.getShort() & 0xFFFF;
            int subHeaderSize = buf.getShort() & 0xFFFF;
            int subSize = buf.getInt();

            if (subSize < 8 || subChunkStart + subSize > packageEnd) {
                break; // corrupt
            }

            switch (subType) {
                case RES_TABLE_TYPE_SPEC_TYPE:
                    // ResTable_typeSpec: just skip it, we don't need the flags
                    break;

                case RES_TABLE_TYPE_TYPE:
                    parseType(buf, data, subChunkStart, subHeaderSize, subSize,
                              packageId, typeStringPool, keyStringPool);
                    break;

                case RES_STRING_POOL_TYPE:
                    // Additional string pool inside package (shouldn't happen normally, but handle)
                    break;

                default:
                    // Unknown sub-chunk
                    break;
            }

            buf.position(subChunkStart + subSize);
        }
    }

    // -----------------------------------------------------------------------
    // Type chunk parsing — extracts entries and their values
    // -----------------------------------------------------------------------

    private void parseType(ByteBuffer buf, byte[] data, int chunkStart, int headerSize,
                           int chunkSize, int packageId,
                           String[] typeStringPool, String[] keyStringPool) {
        // ResTable_type header (after chunk header 8 bytes):
        // id(1), res0(1), res1(2), entryCount(4), entriesStart(4), config(variable)
        int typeId = buf.get() & 0xFF;   // 1-based
        int res0 = buf.get() & 0xFF;
        int res1 = buf.getShort() & 0xFFFF;
        int entryCount = buf.getInt();
        int entriesStart = buf.getInt();    // offset from start of this chunk to entry data

        // Read ResTable_config to determine if this is the default config.
        // Config starts at offset 20 from chunk start; first field is size(4).
        // A default config has all qualifier bytes as zero.
        // Strategy: read config size, then check if the qualifier bytes are all zero.
        boolean isDefaultConfig = true;
        int configPos = chunkStart + 20; // after chunk header(8) + typeId(1) + res0(1) + res1(2) + entryCount(4) + entriesStart(4)
        if (configPos + 4 <= data.length) {
            buf.position(configPos);
            int configSize = buf.getInt();
            // Check qualifier bytes: bytes 4..configSize of config (skip the size field itself)
            // If any qualifier byte is non-zero, this is a qualified config
            int qualEnd = Math.min(configPos + configSize, data.length);
            for (int q = configPos + 4; q < qualEnd; q++) {
                if (data[q] != 0) { isDefaultConfig = false; break; }
            }
        }

        // Read entry offset array — starts right after the header
        buf.position(chunkStart + headerSize);

        if (entryCount < 0 || entryCount > 100000) {
            return; // sanity check
        }

        int[] entryOffsets = new int[entryCount];
        for (int i = 0; i < entryCount; i++) {
            entryOffsets[i] = buf.getInt();
        }

        // Entry data starts at chunkStart + entriesStart
        int entryDataStart = chunkStart + entriesStart;

        // Get the type name from the type string pool (typeId is 1-based)
        String typeName = null;
        if (typeStringPool != null && typeId >= 1 && typeId <= typeStringPool.length) {
            typeName = typeStringPool[typeId - 1];
        }

        for (int i = 0; i < entryCount; i++) {
            int offset = entryOffsets[i];
            if (offset == -1 || offset == 0xFFFFFFFF) {
                continue; // no entry
            }

            // Also treat the unsigned 0xFFFFFFFF stored as int -1
            if (offset < 0) continue;

            int entryPos = entryDataStart + offset;
            if (entryPos < 0 || entryPos + 8 > data.length) {
                continue; // out of bounds
            }

            buf.position(entryPos);

            // ResTable_entry: size(2), flags(2), key(4)
            int entrySize = buf.getShort() & 0xFFFF;
            int entryFlags = buf.getShort() & 0xFFFF;
            int keyIndex = buf.getInt();

            // Build the resource ID
            int resId = (packageId << 24) | (typeId << 16) | i;

            // Store the resource name
            String keyName = null;
            if (keyStringPool != null && keyIndex >= 0 && keyIndex < keyStringPool.length) {
                keyName = keyStringPool[keyIndex];
            }
            if (typeName != null && keyName != null) {
                if (!mNames.containsKey(resId)) {
                    mNames.put(resId, typeName + "/" + keyName);
                }
            }

            // Debug: log type 8 (drawable) entries
            if (typeId == 8 && i < 5) {
                System.err.println("[RT] typeId=8 entry=" + i + " resId=0x" + Integer.toHexString(resId) + " name=" + keyName + " flags=0x" + Integer.toHexString(entryFlags));
            }
            if (typeId == 8 && (i == 0x0108 || i == 0x00fd)) {
                System.err.println("[RT] FOUND typeId=8 entry=0x" + Integer.toHexString(i) + " resId=0x" + Integer.toHexString(resId) + " name=" + keyName);
            }

            // If FLAG_COMPLEX (bag/map entry), skip it — we only handle simple values
            if ((entryFlags & FLAG_COMPLEX) != 0) {
                // ResTable_map_entry: extends ResTable_entry with parent(4), count(4)
                // followed by count ResTable_map entries — skip all
                continue;
            }

            // Res_value: size(2), res0(1), dataType(1), data(4)
            if (buf.remaining() < 8) continue;
            int valueSize = buf.getShort() & 0xFFFF;
            int valueRes0 = buf.get() & 0xFF;
            int dataType = buf.get() & 0xFF;
            int valueData = buf.getInt();

            // Only store if default config, or if no value stored yet (first-wins).
            // This ensures unqualified (default) resources take priority.
            if (resId == 0x7f080108) {
                System.err.println("[RT] 0x7f080108: dataType=" + dataType + " valueData=" + valueData + " poolLen=" + (mGlobalStringPool != null ? mGlobalStringPool.length : -1) + " inStrings=" + mStrings.containsKey(resId));
            }
            switch (dataType) {
                case TYPE_STRING:
                    if (mGlobalStringPool != null && valueData >= 0
                            && valueData < mGlobalStringPool.length) {
                        if (!mStrings.containsKey(resId)) {
                            mStrings.put(resId, mGlobalStringPool[valueData]);
                            if (resId == 0x7f080108) {
                                System.err.println("[RT] STORED 0x7f080108 = " + mGlobalStringPool[valueData]);
                            }
                        }
                        if (!mEntryValues.containsKey(resId)) {
                            mEntryValues.put(resId, valueData);
                        }
                    }
                    break;

                case TYPE_INT_DEC:
                case TYPE_INT_HEX:
                case TYPE_INT_COLOR_ARGB8:
                case TYPE_INT_COLOR_RGB8:
                case TYPE_INT_COLOR_ARGB4:
                case TYPE_INT_COLOR_RGB4:
                case TYPE_DIMENSION:
                case TYPE_FRACTION:
                case TYPE_FLOAT:
                case TYPE_REFERENCE:
                case TYPE_ATTRIBUTE:
                    if (isDefaultConfig || !mIntegers.containsKey(resId)) {
                        mIntegers.put(resId, valueData);
                    }
                    break;

                case TYPE_INT_BOOL:
                    // Boolean: 0 = false, nonzero = true; store raw
                    if (isDefaultConfig || !mIntegers.containsKey(resId)) {
                        mIntegers.put(resId, valueData);
                    }
                    break;

                default:
                    // Store any other numeric types as integers
                    if (dataType >= 0x10 && dataType <= 0x1f) {
                        mIntegers.put(resId, valueData);
                    }
                    break;
            }
        }
    }
}
