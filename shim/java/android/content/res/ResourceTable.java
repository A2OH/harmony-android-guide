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
    private static final Map<String, Integer> sGlobalIdentifiers = new HashMap<>();
    private final Map<String, Integer> mIdentifiers = sGlobalIdentifiers;
    private static final Map<Integer, Integer> sGlobalEntryValues = new HashMap<>();
    private final Map<Integer, Integer> mEntryValues = sGlobalEntryValues;

    /** Global string pool (all string values). */
    private Object[] mGlobalStringPool;
    private Object[] mPrevStringPool; // saved base pool during split parsing

    /** PF-arch-032: per-style attr → value map for FLAG_COMPLEX entries (themes). */
    private java.util.Map<Integer, java.util.Map<Integer, String>> mBagEntries;
    /** PF-arch-032: parent style of each bag entry (for attr inheritance). */
    private java.util.Map<Integer, Integer> mBagParents;

    /** PF-arch-032: get the attr→value map for a style, walking parents. */
    public java.util.Map<Integer, String> getStyleAttrs(int styleId) {
        java.util.HashMap<Integer, String> merged = new java.util.HashMap<Integer, String>();
        int cur = styleId;
        int hop = 0;
        while (cur != 0 && hop < 32) {
            if (mBagEntries != null) {
                java.util.Map<Integer, String> bag = mBagEntries.get(cur);
                if (bag != null) {
                    /* Child overrides parent — only put if not already set. */
                    for (java.util.Map.Entry<Integer, String> e : bag.entrySet()) {
                        if (!merged.containsKey(e.getKey())) {
                            merged.put(e.getKey(), e.getValue());
                        }
                    }
                }
            }
            cur = mBagParents != null && mBagParents.containsKey(cur)
                    ? mBagParents.get(cur) : 0;
            hop++;
        }
        return merged;
    }

    /** PF-arch-032: stringify a typed value from arsc into a renderer-friendly form. */
    private String formatTypedValue(int dataType, int data) {
        switch (dataType) {
            case 0x00: /* TYPE_NULL */
                return null;
            case 0x01: /* TYPE_REFERENCE */
                return "@0x" + Integer.toHexString(data);
            case 0x02: /* TYPE_ATTRIBUTE */
                return "?0x" + Integer.toHexString(data);
            case 0x03: /* TYPE_STRING */
                if (mGlobalStringPool != null && data >= 0 && data < mGlobalStringPool.length) {
                    return stringAt(mGlobalStringPool, data);
                }
                return null;
            case 0x04: /* TYPE_FLOAT */
                return Float.toString(Float.intBitsToFloat(data));
            case 0x05: /* TYPE_DIMENSION */
                return Integer.toString(data >>> 8);
            case 0x10: /* TYPE_INT_DEC */
            case 0x11: /* TYPE_INT_HEX */
                return Integer.toString(data);
            case 0x12: /* TYPE_INT_BOOLEAN */
                return data != 0 ? "true" : "false";
            case 0x1C: /* TYPE_INT_COLOR_ARGB8 */
                return "#" + paddedHex8(data);
            case 0x1D: /* TYPE_INT_COLOR_RGB8 */
                return "#" + paddedHex8(0xFF000000 | data);
            case 0x1E: /* TYPE_INT_COLOR_ARGB4 */
                /* Expand 4-bit channels to 8-bit. */
                int a4 = (data >>> 12) & 0xF;
                int r4 = (data >>> 8) & 0xF;
                int g4 = (data >>> 4) & 0xF;
                int b4 = data & 0xF;
                int argb = (a4 * 0x11 << 24) | (r4 * 0x11 << 16)
                        | (g4 * 0x11 << 8) | (b4 * 0x11);
                return "#" + paddedHex8(argb);
            case 0x1F: /* TYPE_INT_COLOR_RGB4 */
                int r4b = (data >>> 8) & 0xF;
                int g4b = (data >>> 4) & 0xF;
                int b4b = data & 0xF;
                int rgb = 0xFF000000 | (r4b * 0x11 << 16)
                        | (g4b * 0x11 << 8) | (b4b * 0x11);
                return "#" + paddedHex8(rgb);
            default:
                return Integer.toString(data);
        }
    }

    private static String paddedHex8(int v) {
        String s = Integer.toHexString(v);
        if (s.length() >= 8) return s.substring(s.length() - 8);
        StringBuilder sb = new StringBuilder();
        for (int i = s.length(); i < 8; i++) sb.append('0');
        sb.append(s);
        return sb.toString();
    }

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
     * Parse only selected string resources by entry name.
     *
     * This is used during strict standalone bootstrap before Application.onCreate.
     * Large production resources.arsc files can contain many configs and entries;
     * eager full parsing is too expensive at that point. This path lazily decodes
     * string pools, scans only the app package string type, and populates the same
     * maps used by getString().
     */
    public int parseStringResourcesByName(byte[] data, String[] targetKeyNames) {
        if (data == null || data.length < 12 || targetKeyNames == null
                || targetKeyNames.length == 0) {
            return 0;
        }

        int tableType = readU16(data, 0);
        int tableHeaderSize = readU16(data, 2);
        if (tableType != RES_TABLE_TYPE || tableHeaderSize < 12
                || tableHeaderSize > data.length) {
            return 0;
        }

        LazyStringPool globalPool = null;
        int found = 0;
        int pos = tableHeaderSize;
        while (pos >= 0 && pos + 8 <= data.length) {
            int chunkStart = pos;
            int chunkType = readU16(data, chunkStart);
            int chunkHeaderSize = readU16(data, chunkStart + 2);
            int chunkSize = readS32(data, chunkStart + 4);
            if (chunkSize < 8 || chunkHeaderSize < 8 || chunkStart + chunkSize > data.length) {
                break;
            }

            if (chunkType == RES_STRING_POOL_TYPE) {
                globalPool = newLazyStringPool(data, chunkStart, chunkHeaderSize, chunkSize);
            } else if (chunkType == RES_TABLE_PACKAGE_TYPE && globalPool != null) {
                found += parsePackageStringResourcesByName(
                        data, chunkStart, chunkHeaderSize, chunkSize,
                        globalPool, targetKeyNames);
            }

            pos = chunkStart + chunkSize;
        }
        return found;
    }

    public boolean parseStringResourceByName(byte[] data, String targetKeyName) {
        if (targetKeyName == null) {
            return false;
        }
        return parseStringResourcesByName(data, new String[] { targetKeyName }) > 0;
    }

    /**
     * Lazily parse every simple entry in the app package string type.
     *
     * This is the strict runtime path used before Application.onCreate: it gives
     * stock apps real string resources without paying for all drawables, layouts,
     * bags, and integer tables during early bootstrap.
     */
    public int parseStringResources(byte[] data) {
        if (data == null || data.length < 12) {
            return 0;
        }

        int tableType = readU16(data, 0);
        int tableHeaderSize = readU16(data, 2);
        if (tableType != RES_TABLE_TYPE || tableHeaderSize < 12
                || tableHeaderSize > data.length) {
            return 0;
        }

        LazyStringPool globalPool = null;
        int found = 0;
        int pos = tableHeaderSize;
        while (pos >= 0 && pos + 8 <= data.length) {
            int chunkStart = pos;
            int chunkType = readU16(data, chunkStart);
            int chunkHeaderSize = readU16(data, chunkStart + 2);
            int chunkSize = readS32(data, chunkStart + 4);
            if (chunkSize < 8 || chunkHeaderSize < 8 || chunkStart + chunkSize > data.length) {
                break;
            }

            if (chunkType == RES_STRING_POOL_TYPE) {
                globalPool = newLazyStringPool(data, chunkStart, chunkHeaderSize, chunkSize);
            } else if (chunkType == RES_TABLE_PACKAGE_TYPE && globalPool != null) {
                found += parsePackageStringResources(
                        data, chunkStart, chunkHeaderSize, chunkSize, globalPool);
            }

            pos = chunkStart + chunkSize;
        }
        return found;
    }

    private int parsePackageStringResources(byte[] data, int chunkStart,
            int chunkHeaderSize, int chunkSize, LazyStringPool globalPool) {
        int packageBody = chunkStart + 8;
        if (packageBody + 272 > data.length) {
            return 0;
        }

        int packageId = readS32(data, packageBody);
        int typeStringsOffset = readS32(data, packageBody + 4 + 256);
        int keyStringsOffset = readS32(data, packageBody + 4 + 256 + 8);
        int packageEnd = chunkStart + chunkSize;

        LazyStringPool typeStringPool = parseLazyPoolAtPackageOffset(
                data, chunkStart, packageEnd, typeStringsOffset);
        LazyStringPool keyStringPool = parseLazyPoolAtPackageOffset(
                data, chunkStart, packageEnd, keyStringsOffset);
        if (typeStringPool == null || keyStringPool == null) {
            return 0;
        }

        int stringTypeId = 0;
        for (int i = 0; i < typeStringPool.count; i++) {
            if ("string".equals(typeStringPool.get(i))) {
                stringTypeId = i + 1;
                break;
            }
        }
        if (stringTypeId == 0) {
            return 0;
        }

        int scanStart = chunkStart + chunkHeaderSize;
        if (typeStringPool.chunkEnd > scanStart) scanStart = typeStringPool.chunkEnd;
        if (keyStringPool.chunkEnd > scanStart) scanStart = keyStringPool.chunkEnd;

        int found = 0;
        int pos = scanStart;
        while (pos + 8 <= packageEnd) {
            int subChunkStart = pos;
            int subType = readU16(data, subChunkStart);
            int subHeaderSize = readU16(data, subChunkStart + 2);
            int subSize = readS32(data, subChunkStart + 4);
            if (subSize < 8 || subHeaderSize < 8 || subChunkStart + subSize > packageEnd) {
                break;
            }

            if (subType == RES_TABLE_TYPE_TYPE
                    && subChunkStart + 20 <= data.length
                    && (data[subChunkStart + 8] & 0xFF) == stringTypeId) {
                found += parseTypeStringResources(
                        data, subChunkStart, subHeaderSize, subSize,
                        packageId, stringTypeId, keyStringPool, globalPool);
            }

            pos = subChunkStart + subSize;
        }
        return found;
    }

    private int parseTypeStringResources(byte[] data, int chunkStart,
            int headerSize, int chunkSize, int packageId, int typeId,
            LazyStringPool keyStringPool, LazyStringPool globalPool) {
        if (chunkStart + 20 > data.length) {
            return 0;
        }
        int entryCount = readS32(data, chunkStart + 12);
        int entriesStart = readS32(data, chunkStart + 16);
        if (entryCount < 0 || entryCount > 100000) {
            return 0;
        }
        int offsetsPos = chunkStart + headerSize;
        int entryDataStart = chunkStart + entriesStart;
        int chunkEnd = chunkStart + chunkSize;
        if (offsetsPos < 0 || offsetsPos + (entryCount * 4) > data.length
                || entryDataStart < 0 || entryDataStart > chunkEnd) {
            return 0;
        }

        boolean defaultConfig = isDefaultConfig(data, chunkStart);
        int found = 0;
        for (int i = 0; i < entryCount; i++) {
            int offset = readS32(data, offsetsPos + (i * 4));
            if (offset < 0) {
                continue;
            }
            int entryPos = entryDataStart + offset;
            if (entryPos < 0 || entryPos + 8 > chunkEnd || entryPos + 8 > data.length) {
                continue;
            }

            int entrySize = readU16(data, entryPos);
            int entryFlags = readU16(data, entryPos + 2);
            int keyIndex = readS32(data, entryPos + 4);
            if ((entryFlags & FLAG_COMPLEX) != 0) {
                continue;
            }

            int valuePos = entryPos + entrySize;
            if (entrySize < 8 || valuePos + 8 > chunkEnd || valuePos + 8 > data.length) {
                continue;
            }

            int dataType = data[valuePos + 3] & 0xFF;
            int valueData = readS32(data, valuePos + 4);
            if (dataType != TYPE_STRING || !globalPool.isValidIndex(valueData)) {
                continue;
            }

            int resId = (packageId << 24) | (typeId << 16) | i;
            if (!defaultConfig && mStrings.containsKey(resId)) {
                continue;
            }
            String value = globalPool.get(valueData);
            if (value == null) {
                continue;
            }
            mStrings.put(resId, value);
            if (!mEntryValues.containsKey(resId)) {
                mEntryValues.put(resId, valueData);
            }
            if (keyStringPool.isValidIndex(keyIndex) && !mNames.containsKey(resId)) {
                String keyName = keyStringPool.get(keyIndex);
                if (keyName != null) {
                    putResourceName(resId, "string/" + keyName);
                }
            }
            found++;
        }
        return found;
    }

    private int parsePackageStringResourcesByName(byte[] data, int chunkStart,
            int chunkHeaderSize, int chunkSize, LazyStringPool globalPool,
            String[] targetKeyNames) {
        int packageBody = chunkStart + 8;
        if (packageBody + 272 > data.length) {
            return 0;
        }

        int packageId = readS32(data, packageBody);
        int typeStringsOffset = readS32(data, packageBody + 4 + 256);
        int keyStringsOffset = readS32(data, packageBody + 4 + 256 + 8);
        int packageEnd = chunkStart + chunkSize;

        LazyStringPool typeStringPool = parseLazyPoolAtPackageOffset(
                data, chunkStart, packageEnd, typeStringsOffset);
        LazyStringPool keyStringPool = parseLazyPoolAtPackageOffset(
                data, chunkStart, packageEnd, keyStringsOffset);
        if (typeStringPool == null || keyStringPool == null) {
            return 0;
        }

        int stringTypeId = 0;
        for (int i = 0; i < typeStringPool.count; i++) {
            if ("string".equals(typeStringPool.get(i))) {
                stringTypeId = i + 1;
                break;
            }
        }
        if (stringTypeId == 0) {
            return 0;
        }

        int[] targetKeyIndexes = new int[targetKeyNames.length];
        for (int i = 0; i < targetKeyIndexes.length; i++) {
            targetKeyIndexes[i] = -1;
        }
        int unresolved = 0;
        for (int i = 0; i < targetKeyNames.length; i++) {
            if (targetKeyNames[i] != null && targetKeyNames[i].length() > 0) {
                unresolved++;
            }
        }
        if (unresolved == 0) {
            return 0;
        }

        for (int i = 0; i < keyStringPool.count && unresolved > 0; i++) {
            String key = keyStringPool.get(i);
            for (int t = 0; t < targetKeyNames.length; t++) {
                if (targetKeyIndexes[t] < 0 && targetKeyNames[t] != null
                        && targetKeyNames[t].equals(key)) {
                    targetKeyIndexes[t] = i;
                    unresolved--;
                }
            }
        }
        if (unresolved == targetKeyNames.length) {
            return 0;
        }

        int scanStart = chunkStart + chunkHeaderSize;
        if (typeStringPool.chunkEnd > scanStart) scanStart = typeStringPool.chunkEnd;
        if (keyStringPool.chunkEnd > scanStart) scanStart = keyStringPool.chunkEnd;

        int found = 0;
        int pos = scanStart;
        while (pos + 8 <= packageEnd) {
            int subChunkStart = pos;
            int subType = readU16(data, subChunkStart);
            int subHeaderSize = readU16(data, subChunkStart + 2);
            int subSize = readS32(data, subChunkStart + 4);
            if (subSize < 8 || subHeaderSize < 8 || subChunkStart + subSize > packageEnd) {
                break;
            }

            if (subType == RES_TABLE_TYPE_TYPE
                    && subChunkStart + 20 <= data.length
                    && (data[subChunkStart + 8] & 0xFF) == stringTypeId) {
                found += parseTypeStringResourcesByName(
                        data, subChunkStart, subHeaderSize, subSize,
                        packageId, stringTypeId, keyStringPool, globalPool,
                        targetKeyNames, targetKeyIndexes);
            }

            pos = subChunkStart + subSize;
        }
        return found;
    }

    private int parseTypeStringResourcesByName(byte[] data, int chunkStart,
            int headerSize, int chunkSize, int packageId, int typeId,
            LazyStringPool keyStringPool, LazyStringPool globalPool,
            String[] targetKeyNames, int[] targetKeyIndexes) {
        if (chunkStart + 20 > data.length) {
            return 0;
        }
        int entryCount = readS32(data, chunkStart + 12);
        int entriesStart = readS32(data, chunkStart + 16);
        if (entryCount < 0 || entryCount > 100000) {
            return 0;
        }
        int offsetsPos = chunkStart + headerSize;
        int entryDataStart = chunkStart + entriesStart;
        int chunkEnd = chunkStart + chunkSize;
        if (offsetsPos < 0 || offsetsPos + (entryCount * 4) > data.length
                || entryDataStart < 0 || entryDataStart > chunkEnd) {
            return 0;
        }

        boolean defaultConfig = isDefaultConfig(data, chunkStart);
        int found = 0;
        for (int i = 0; i < entryCount; i++) {
            int offset = readS32(data, offsetsPos + (i * 4));
            if (offset < 0) {
                continue;
            }
            int entryPos = entryDataStart + offset;
            if (entryPos < 0 || entryPos + 8 > chunkEnd || entryPos + 8 > data.length) {
                continue;
            }

            int entrySize = readU16(data, entryPos);
            int entryFlags = readU16(data, entryPos + 2);
            int keyIndex = readS32(data, entryPos + 4);
            int targetSlot = indexOfTargetKey(targetKeyIndexes, keyIndex);
            if (targetSlot < 0 || (entryFlags & FLAG_COMPLEX) != 0) {
                continue;
            }

            int valuePos = entryPos + entrySize;
            if (entrySize < 8 || valuePos + 8 > chunkEnd || valuePos + 8 > data.length) {
                continue;
            }

            int dataType = data[valuePos + 3] & 0xFF;
            int valueData = readS32(data, valuePos + 4);
            if (dataType != TYPE_STRING || !globalPool.isValidIndex(valueData)) {
                continue;
            }

            int resId = (packageId << 24) | (typeId << 16) | i;
            if (!defaultConfig && mStrings.containsKey(resId)) {
                continue;
            }
            String value = globalPool.get(valueData);
            if (value == null) {
                continue;
            }
            mStrings.put(resId, value);
            if (!mEntryValues.containsKey(resId)) {
                mEntryValues.put(resId, valueData);
            }
            String keyName = targetKeyNames[targetSlot];
            if (keyName == null && keyStringPool.isValidIndex(keyIndex)) {
                keyName = keyStringPool.get(keyIndex);
            }
            if (keyName != null && !mNames.containsKey(resId)) {
                putResourceName(resId, "string/" + keyName);
            }
            found++;
        }
        return found;
    }

    private static int indexOfTargetKey(int[] targetKeyIndexes, int keyIndex) {
        if (targetKeyIndexes == null) {
            return -1;
        }
        for (int i = 0; i < targetKeyIndexes.length; i++) {
            if (targetKeyIndexes[i] == keyIndex) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isDefaultConfig(byte[] data, int typeChunkStart) {
        int configPos = typeChunkStart + 20;
        if (configPos + 4 > data.length) {
            return true;
        }
        int configSize = readS32(data, configPos);
        if (configSize < 4 || configSize > 4096 || configPos + configSize > data.length) {
            return true;
        }
        for (int i = configPos + 4; i < configPos + configSize; i++) {
            if (data[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private LazyStringPool parseLazyPoolAtPackageOffset(byte[] data, int packageStart,
            int packageEnd, int offset) {
        if (offset <= 0) {
            return null;
        }
        int poolStart = packageStart + offset;
        if (poolStart < packageStart || poolStart + 8 > packageEnd) {
            return null;
        }
        int poolType = readU16(data, poolStart);
        int poolHeaderSize = readU16(data, poolStart + 2);
        int poolSize = readS32(data, poolStart + 4);
        if (poolType != RES_STRING_POOL_TYPE || poolSize < 8
                || poolStart + poolSize > packageEnd) {
            return null;
        }
        return newLazyStringPool(data, poolStart, poolHeaderSize, poolSize);
    }

    private final class LazyStringPool {
        final byte[] data;
        final int chunkStart;
        final int chunkEnd;
        final int count;
        final boolean utf8;
        final int stringsStart;
        final int[] offsets;

        private LazyStringPool(byte[] data, int chunkStart, int chunkSize,
                int count, boolean utf8, int stringsStart, int[] offsets) {
            this.data = data;
            this.chunkStart = chunkStart;
            this.chunkEnd = chunkStart + chunkSize;
            this.count = count;
            this.utf8 = utf8;
            this.stringsStart = stringsStart;
            this.offsets = offsets;
        }

        boolean isValidIndex(int index) {
            return index >= 0 && index < count;
        }

        String get(int index) {
            if (!isValidIndex(index)) {
                return null;
            }
            int pos = stringsStart + offsets[index];
            if (pos < chunkStart || pos >= chunkEnd || pos >= data.length) {
                return null;
            }
            try {
                if (utf8) {
                    return readUtf8StringAt(data, pos, chunkEnd);
                }
                return readUtf16StringAt(data, pos, chunkEnd);
            } catch (Throwable ignored) {
                return null;
            }
        }
    }

    private LazyStringPool newLazyStringPool(byte[] data, int chunkStart,
            int headerSize, int chunkSize) {
        if (data == null || chunkStart < 0 || chunkStart + 28 > data.length
                || headerSize < 28 || chunkSize < headerSize
                || chunkStart + chunkSize > data.length) {
            return null;
        }
        int stringCount = readS32(data, chunkStart + 8);
        int styleCount = readS32(data, chunkStart + 12);
        int flags = readS32(data, chunkStart + 16);
        int stringsStartOffset = readS32(data, chunkStart + 20);
        if (stringCount < 0 || stringCount > 200000 || styleCount < 0
                || stringsStartOffset < headerSize || stringsStartOffset >= chunkSize) {
            return null;
        }
        int offsetsPos = chunkStart + 28;
        if (offsetsPos + (stringCount * 4) > data.length
                || offsetsPos + (stringCount * 4) > chunkStart + chunkSize) {
            return null;
        }
        int[] offsets = new int[stringCount];
        for (int i = 0; i < stringCount; i++) {
            offsets[i] = readS32(data, offsetsPos + (i * 4));
        }
        return new LazyStringPool(data, chunkStart, chunkSize, stringCount,
                (flags & (1 << 8)) != 0, chunkStart + stringsStartOffset, offsets);
    }

    private static int readU16(byte[] data, int offset) {
        if (offset < 0 || offset + 2 > data.length) {
            return 0;
        }
        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
    }

    private static int readS32(byte[] data, int offset) {
        if (offset < 0 || offset + 4 > data.length) {
            return 0;
        }
        return (data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8)
                | ((data[offset + 2] & 0xFF) << 16)
                | (data[offset + 3] << 24);
    }

    private String readUtf8StringAt(byte[] data, int pos, int limit) {
        if (pos < 0 || pos >= limit) {
            return "";
        }
        int[] p = new int[] { pos };
        int charLen = readUtf8Length(data, p, limit);
        int byteLen = readUtf8Length(data, p, limit);
        if (charLen < 0 || byteLen < 0 || p[0] + byteLen > limit
                || p[0] + byteLen > data.length) {
            return "";
        }
        byte[] strBytes = new byte[byteLen];
        System.arraycopy(data, p[0], strBytes, 0, byteLen);
        return decodeUtf8(strBytes);
    }

    private static int readUtf8Length(byte[] data, int[] pos, int limit) {
        if (pos[0] >= limit || pos[0] >= data.length) {
            return -1;
        }
        int value = data[pos[0]++] & 0xFF;
        if ((value & 0x80) != 0) {
            if (pos[0] >= limit || pos[0] >= data.length) {
                return -1;
            }
            value = ((value & 0x7F) << 8) | (data[pos[0]++] & 0xFF);
        }
        return value;
    }

    private String readUtf16StringAt(byte[] data, int pos, int limit) {
        if (pos < 0 || pos + 2 > limit || pos + 2 > data.length) {
            return "";
        }
        int charLen = readU16(data, pos);
        pos += 2;
        if ((charLen & 0x8000) != 0) {
            if (pos + 2 > limit || pos + 2 > data.length) {
                return "";
            }
            charLen = ((charLen & 0x7FFF) << 16) | readU16(data, pos);
            pos += 2;
        }
        if (charLen < 0 || pos + (charLen * 2) > limit
                || pos + (charLen * 2) > data.length) {
            return "";
        }
        char[] chars = new char[charLen];
        for (int i = 0; i < charLen; i++) {
            chars[i] = (char) readU16(data, pos + (i * 2));
        }
        return new String(chars);
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
        if (key == null) {
            return 0;
        }
        Integer exact = mIdentifiers.get(key);
        if (exact != null) {
            return exact.intValue();
        }
        int slash = key.indexOf('/');
        String type = slash > 0 ? key.substring(0, slash) : null;
        String name = slash >= 0 && slash + 1 < key.length()
                ? key.substring(slash + 1)
                : key;
        if (type != null) {
            return 0;
        }
        for (Map.Entry<Integer, String> entry : mNames.entrySet()) {
            String val = entry.getValue();
            if (val == null) {
                continue;
            }
            if (val.endsWith("/" + name)) {
                return entry.getKey();
            }
        }
        return 0;
    }

    private void putResourceName(int resId, String name) {
        if (name == null) {
            return;
        }
        if (!mNames.containsKey(resId)) {
            mNames.put(resId, name);
        }
        if (!mIdentifiers.containsKey(name)) {
            mIdentifiers.put(name, resId);
        }
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
                String s = stringAt(mGlobalStringPool, i);
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
                String path = stringAt(mGlobalStringPool, stringIdx);
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
        if (mGlobalStringPool == null) {
            return null;
        }
        String[] copy = new String[mGlobalStringPool.length];
        for (int i = 0; i < mGlobalStringPool.length; i++) {
            copy[i] = stringAt(mGlobalStringPool, i);
        }
        return copy;
    }

    // -----------------------------------------------------------------------
    // String pool parsing (supports UTF-8 and UTF-16)
    // -----------------------------------------------------------------------

    private Object[] parseStringPool(ByteBuffer buf, int chunkStart, int headerSize, int chunkSize) {
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

        Object[] pool = new Object[stringCount];
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

    private static String stringAt(Object[] pool, int index) {
        if (pool == null || index < 0 || index >= pool.length) {
            return null;
        }
        Object value = pool[index];
        return value instanceof String ? (String) value : null;
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
        Object[] typeStringPool = null;
        Object[] keyStringPool = null;

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
                           Object[] typeStringPool, Object[] keyStringPool) {
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
            typeName = stringAt(typeStringPool, typeId - 1);
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
                keyName = stringAt(keyStringPool, keyIndex);
            }
            if (typeName != null && keyName != null) {
                if (!mNames.containsKey(resId)) {
                    putResourceName(resId, typeName + "/" + keyName);
                }
            }

            // PF-arch-032: parse FLAG_COMPLEX (bag/map) entries — needed for
            // <style> resources whose items become a list of attr-value pairs.
            // ResTable_map_entry: extends ResTable_entry with parent(4), count(4)
            // followed by count ResTable_map { name: u32, value: Res_value (8 bytes) }
            if ((entryFlags & FLAG_COMPLEX) != 0) {
                if (buf.remaining() < 8) continue;
                int parentRef = buf.getInt();
                int bagCount = buf.getInt();
                if (bagCount < 0 || bagCount > 100000) continue;
                java.util.HashMap<Integer, String> bag =
                        new java.util.HashMap<Integer, String>();
                for (int b = 0; b < bagCount; b++) {
                    if (buf.remaining() < 12) break;
                    int mapName = buf.getInt();
                    int mvSize = buf.getShort() & 0xFFFF;
                    int mvRes0 = buf.get() & 0xFF;
                    int mvType = buf.get() & 0xFF;
                    int mvData = buf.getInt();
                    String formatted = formatTypedValue(mvType, mvData);
                    if (formatted != null) bag.put(mapName, formatted);
                }
                if (!bag.isEmpty()) {
                    if (mBagEntries == null) {
                        mBagEntries = new java.util.HashMap<Integer, java.util.Map<Integer, String>>();
                    }
                    mBagEntries.put(resId, bag);
                    /* Also remember parent so callers can chain. */
                    if (parentRef != 0) {
                        if (mBagParents == null) {
                            mBagParents = new java.util.HashMap<Integer, Integer>();
                        }
                        mBagParents.put(resId, parentRef);
                    }
                }
                continue;
            }

            // Res_value: size(2), res0(1), dataType(1), data(4)
            if (buf.remaining() < 8) continue;
            int valueSize = buf.getShort() & 0xFFFF;
            int valueRes0 = buf.get() & 0xFF;
            int dataType = buf.get() & 0xFF;
            int valueData = buf.getInt();

            switch (dataType) {
                case TYPE_STRING:
                    if (mGlobalStringPool != null && valueData >= 0
                            && valueData < mGlobalStringPool.length) {
                        String stringValue = stringAt(mGlobalStringPool, valueData);
                        if (!mStrings.containsKey(resId)) {
                            mStrings.put(resId, stringValue);
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
