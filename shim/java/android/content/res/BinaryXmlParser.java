package android.content.res;

import android.util.AttributeSet;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * BinaryXmlParser -- parses Android's compiled binary XML format (AXML).
 *
 * Implements XmlPullParser and AttributeSet so it can be used both as a
 * pull parser for generic XML walking and as an AttributeSet for View
 * inflation (XmlResourceParser extends both).
 *
 * AXML format:
 * - Header: magic 0x00080003, file size
 * - String pool chunk (type 0x0001): indexed strings
 * - Resource ID pool chunk (type 0x0180): maps attr name indices to R.attr.* IDs
 * - XML tree nodes: START_NAMESPACE(0x0100), START_TAG(0x0102),
 *   END_TAG(0x0103), TEXT(0x0104), END_NAMESPACE(0x0101)
 *
 * Pure Java, no lambdas, no String.format/split/Pattern (KitKat Dalvik compat).
 */
public class BinaryXmlParser implements XmlPullParser, XmlResourceParser, AttributeSet {

    // AXML magic number
    private static final int AXML_MAGIC = 0x00080003;

    // Chunk types (16-bit type field)
    private static final int CHUNK_STRING_POOL   = 0x0001;
    private static final int CHUNK_RESOURCE_IDS  = 0x0180;

    // XML tree node types (16-bit type field)
    private static final int XML_START_NAMESPACE = 0x0100;
    private static final int XML_END_NAMESPACE   = 0x0101;
    private static final int XML_START_TAG       = 0x0102;
    private static final int XML_END_TAG         = 0x0103;
    private static final int XML_TEXT            = 0x0104;

    // TypedValue types
    private static final int TV_TYPE_NULL      = 0x00;
    private static final int TV_TYPE_REFERENCE = 0x01;
    private static final int TV_TYPE_ATTRIBUTE = 0x02;
    private static final int TV_TYPE_STRING    = 0x03;
    private static final int TV_TYPE_FLOAT     = 0x04;
    private static final int TV_TYPE_DIMENSION = 0x05;
    private static final int TV_TYPE_FRACTION  = 0x06;
    private static final int TV_TYPE_INT_DEC   = 0x10;
    private static final int TV_TYPE_INT_HEX   = 0x11;
    private static final int TV_TYPE_INT_BOOL  = 0x12;
    private static final int TV_TYPE_INT_COLOR_ARGB8 = 0x1c;
    private static final int TV_TYPE_INT_COLOR_RGB8  = 0x1d;
    private static final int TV_TYPE_INT_COLOR_ARGB4 = 0x1e;
    private static final int TV_TYPE_INT_COLOR_RGB4  = 0x1f;

    // ---- Parsed data ----
    private Object[] mStringPool;
    private int[] mResourceIds;

    // ---- Event stream ----
    // We pre-parse all events into arrays for efficient random access
    private int[] mEventTypes;      // XmlPullParser event types
    private int[] mEventNames;      // string pool index for element/text name
    private int[] mEventNamespaces; // string pool index for namespace URI
    private int[] mEventDepths;     // nesting depth at each event

    // Attributes: stored as flat arrays, indexed by event
    // For each START_TAG event, attrStart[eventIdx] and attrCount[eventIdx]
    // point into the flat attribute arrays.
    private int[] mAttrStart;   // start index into flat attr arrays
    private int[] mAttrCount;   // number of attrs for this event

    // Flat attribute arrays
    private int[] mAttrNs;       // namespace (string pool index)
    private int[] mAttrName;     // name (string pool index)
    private int[] mAttrRawValue; // raw string value (string pool index, or -1)
    private int[] mAttrType;     // typed value type
    private int[] mAttrData;     // typed value data

    // Text content for TEXT events
    private int[] mEventText;    // string pool index for TEXT events

    private int mEventCount;
    private int mCurrentEvent = -1;
    private int mTotalAttrCount;

    /**
     * Construct a BinaryXmlParser from raw AXML bytes.
     *
     * @param data the compiled binary XML data
     * @throws IllegalArgumentException if data is null, too short, or has wrong magic
     */
    public BinaryXmlParser(byte[] data) {
        if (data == null || data.length < 8) {
            throw new IllegalArgumentException("AXML data is null or too short");
        }

        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int magic = buf.getInt();
        int fileSize = buf.getInt();

        if (magic != AXML_MAGIC) {
            throw new IllegalArgumentException(
                    "Not an AXML file: magic=0x" + Integer.toHexString(magic));
        }

        // First pass: parse string pool and resource ID pool
        int savedPos = buf.position();
        while (buf.remaining() >= 8) {
            int chunkStart = buf.position();
            int chunkType = buf.getShort() & 0xFFFF;
            int chunkHeaderSize = buf.getShort() & 0xFFFF;
            int chunkSize = buf.getInt();
            if (chunkSize < 8 || chunkStart + chunkSize > data.length) break;

            if (chunkType == CHUNK_STRING_POOL) {
                mStringPool = parseStringPool(buf, chunkStart, chunkSize);
            } else if (chunkType == CHUNK_RESOURCE_IDS) {
                int count = (chunkSize - 8) / 4;
                mResourceIds = new int[count];
                for (int i = 0; i < count; i++) {
                    mResourceIds[i] = buf.getInt();
                }
            }

            buf.position(chunkStart + chunkSize);
        }

        if (mStringPool == null) {
            mStringPool = new Object[0];
        }
        if (mResourceIds == null) {
            mResourceIds = new int[0];
        }

        // Second pass: parse XML tree events
        buf.position(savedPos);

        // Temporary lists for building the event stream
        ArrayList eventTypeList = new ArrayList();
        ArrayList eventNameList = new ArrayList();
        ArrayList eventNsList = new ArrayList();
        ArrayList eventDepthList = new ArrayList();
        ArrayList eventTextList = new ArrayList();
        ArrayList attrStartList = new ArrayList();
        ArrayList attrCountList = new ArrayList();

        ArrayList attrNsList = new ArrayList();
        ArrayList attrNameList = new ArrayList();
        ArrayList attrRawList = new ArrayList();
        ArrayList attrTypeList = new ArrayList();
        ArrayList attrDataList = new ArrayList();

        int depth = 0;
        int totalAttrs = 0;

        // Add START_DOCUMENT
        eventTypeList.add(Integer.valueOf(START_DOCUMENT));
        eventNameList.add(Integer.valueOf(-1));
        eventNsList.add(Integer.valueOf(-1));
        eventDepthList.add(Integer.valueOf(0));
        eventTextList.add(Integer.valueOf(-1));
        attrStartList.add(Integer.valueOf(0));
        attrCountList.add(Integer.valueOf(0));

        while (buf.remaining() >= 8) {
            int chunkStart = buf.position();
            int chunkType = buf.getShort() & 0xFFFF;
            int chunkHeaderSize = buf.getShort() & 0xFFFF;
            int chunkSize = buf.getInt();
            if (chunkSize < 8 || chunkStart + chunkSize > data.length) break;

            // Skip string pool and resource ID chunks (already parsed)
            if (chunkType == CHUNK_STRING_POOL || chunkType == CHUNK_RESOURCE_IDS) {
                buf.position(chunkStart + chunkSize);
                continue;
            }

            switch (chunkType) {
                case XML_START_NAMESPACE: {
                    // lineNumber(4), comment(4), prefix(4), uri(4)
                    if (buf.remaining() >= 16) {
                        buf.getInt(); // line
                        buf.getInt(); // comment
                        buf.getInt(); // prefix
                        buf.getInt(); // uri
                    }
                    // Don't emit an event for namespace declarations
                    break;
                }

                case XML_END_NAMESPACE: {
                    if (buf.remaining() >= 16) {
                        buf.getInt(); // line
                        buf.getInt(); // comment
                        buf.getInt(); // prefix
                        buf.getInt(); // uri
                    }
                    break;
                }

                case XML_START_TAG: {
                    // lineNumber(4), comment(4), ns(4), name(4),
                    // attrStart(2), attrSize(2), attrCount(2),
                    // idIndex(2), classIndex(2), styleIndex(2)
                    if (buf.remaining() < 28) break;
                    buf.getInt(); // line
                    buf.getInt(); // comment
                    int ns = buf.getInt();
                    int name = buf.getInt();
                    int attrStartField = buf.getShort() & 0xFFFF;
                    int attrSizeField = buf.getShort() & 0xFFFF;
                    int attrCount = buf.getShort() & 0xFFFF;
                    buf.getShort(); // idIndex
                    buf.getShort(); // classIndex
                    buf.getShort(); // styleIndex

                    depth++;

                    eventTypeList.add(Integer.valueOf(START_TAG));
                    eventNameList.add(Integer.valueOf(name));
                    eventNsList.add(Integer.valueOf(ns));
                    eventDepthList.add(Integer.valueOf(depth));
                    eventTextList.add(Integer.valueOf(-1));
                    attrStartList.add(Integer.valueOf(totalAttrs));
                    attrCountList.add(Integer.valueOf(attrCount));

                    // Parse attributes
                    for (int i = 0; i < attrCount; i++) {
                        if (buf.remaining() < 20) break;
                        int aNs = buf.getInt();
                        int aName = buf.getInt();
                        int aRawValue = buf.getInt();
                        int tvSize = buf.getShort() & 0xFFFF;
                        int tvRes0 = buf.get() & 0xFF;
                        int aType = buf.get() & 0xFF;
                        int aData = buf.getInt();

                        attrNsList.add(Integer.valueOf(aNs));
                        attrNameList.add(Integer.valueOf(aName));
                        attrRawList.add(Integer.valueOf(aRawValue));
                        attrTypeList.add(Integer.valueOf(aType));
                        attrDataList.add(Integer.valueOf(aData));
                        totalAttrs++;
                    }
                    break;
                }

                case XML_END_TAG: {
                    if (buf.remaining() < 16) break;
                    buf.getInt(); // line
                    buf.getInt(); // comment
                    int ns = buf.getInt();
                    int name = buf.getInt();

                    eventTypeList.add(Integer.valueOf(END_TAG));
                    eventNameList.add(Integer.valueOf(name));
                    eventNsList.add(Integer.valueOf(ns));
                    eventDepthList.add(Integer.valueOf(depth));
                    eventTextList.add(Integer.valueOf(-1));
                    attrStartList.add(Integer.valueOf(0));
                    attrCountList.add(Integer.valueOf(0));

                    depth--;
                    break;
                }

                case XML_TEXT: {
                    if (buf.remaining() < 16) break;
                    buf.getInt(); // line
                    buf.getInt(); // comment
                    int textIdx = buf.getInt();
                    buf.getInt(); // unused typed value stuff (8 bytes total but we read the index)

                    eventTypeList.add(Integer.valueOf(TEXT));
                    eventNameList.add(Integer.valueOf(-1));
                    eventNsList.add(Integer.valueOf(-1));
                    eventDepthList.add(Integer.valueOf(depth));
                    eventTextList.add(Integer.valueOf(textIdx));
                    attrStartList.add(Integer.valueOf(0));
                    attrCountList.add(Integer.valueOf(0));
                    break;
                }

                default:
                    break;
            }

            buf.position(chunkStart + chunkSize);
        }

        // Add END_DOCUMENT
        eventTypeList.add(Integer.valueOf(END_DOCUMENT));
        eventNameList.add(Integer.valueOf(-1));
        eventNsList.add(Integer.valueOf(-1));
        eventDepthList.add(Integer.valueOf(0));
        eventTextList.add(Integer.valueOf(-1));
        attrStartList.add(Integer.valueOf(0));
        attrCountList.add(Integer.valueOf(0));

        // Convert lists to arrays
        mEventCount = eventTypeList.size();
        mEventTypes = toIntArray(eventTypeList);
        mEventNames = toIntArray(eventNameList);
        mEventNamespaces = toIntArray(eventNsList);
        mEventDepths = toIntArray(eventDepthList);
        mEventText = toIntArray(eventTextList);
        mAttrStart = toIntArray(attrStartList);
        mAttrCount = toIntArray(attrCountList);

        mTotalAttrCount = totalAttrs;
        mAttrNs = toIntArray(attrNsList);
        mAttrName = toIntArray(attrNameList);
        mAttrRawValue = toIntArray(attrRawList);
        mAttrType = toIntArray(attrTypeList);
        mAttrData = toIntArray(attrDataList);

        // Position at START_DOCUMENT
        mCurrentEvent = 0;
    }

    // ---- XmlPullParser implementation ----

    public int getEventType() {
        if (mCurrentEvent < 0 || mCurrentEvent >= mEventCount) return END_DOCUMENT;
        return mEventTypes[mCurrentEvent];
    }

    public int next() {
        if (mCurrentEvent < mEventCount - 1) {
            mCurrentEvent++;
        }
        return getEventType();
    }

    public String getName() {
        if (mCurrentEvent < 0 || mCurrentEvent >= mEventCount) return null;
        int idx = mEventNames[mCurrentEvent];
        return poolString(idx);
    }

    public String getNamespace() {
        if (mCurrentEvent < 0 || mCurrentEvent >= mEventCount) return null;
        int idx = mEventNamespaces[mCurrentEvent];
        return poolString(idx);
    }

    public int getAttributeCount() {
        if (mCurrentEvent < 0 || mCurrentEvent >= mEventCount) return -1;
        if (mEventTypes[mCurrentEvent] != START_TAG) return -1;
        return mAttrCount[mCurrentEvent];
    }

    public String getAttributeName(int index) {
        int base = attrBase(index);
        if (base < 0) return null;
        return poolString(mAttrName[base]);
    }

    public String getAttributeNamespace(int index) {
        int base = attrBase(index);
        if (base < 0) return null;
        return poolString(mAttrNs[base]);
    }

    public String getAttributeValue(int index) {
        int base = attrBase(index);
        if (base < 0) return null;

        // If there's a raw string value, use it
        int rawIdx = mAttrRawValue[base];
        if (rawIdx >= 0) {
            String raw = poolString(rawIdx);
            if (raw != null) return raw;
        }

        // Otherwise, convert the typed value to string
        return typedValueToString(mAttrType[base], mAttrData[base]);
    }

    public String getAttributeValue(String namespace, String name) {
        if (name == null) return null;
        int count = getAttributeCount();
        if (count < 0) return null;

        for (int i = 0; i < count; i++) {
            String aName = getAttributeName(i);
            if (!name.equals(aName)) continue;

            if (namespace != null) {
                String aNs = getAttributeNamespace(i);
                if (!namespace.equals(aNs)) continue;
            }

            return getAttributeValue(i);
        }
        return null;
    }

    /**
     * Get the integer value of an attribute, or defaultValue if not parseable.
     */
    public int getAttributeIntValue(int index, int defaultValue) {
        int base = attrBase(index);
        if (base < 0) return defaultValue;

        int type = mAttrType[base];
        int data = mAttrData[base];

        switch (type) {
            case TV_TYPE_INT_DEC:
            case TV_TYPE_INT_HEX:
            case TV_TYPE_INT_BOOL:
            case TV_TYPE_INT_COLOR_ARGB8:
            case TV_TYPE_INT_COLOR_RGB8:
            case TV_TYPE_INT_COLOR_ARGB4:
            case TV_TYPE_INT_COLOR_RGB4:
            case TV_TYPE_REFERENCE:
                return data;
            default:
                return defaultValue;
        }
    }

    /**
     * Get the float value of an attribute, or defaultValue if not parseable.
     */
    public float getAttributeFloatValue(int index, float defaultValue) {
        int base = attrBase(index);
        if (base < 0) return defaultValue;

        int type = mAttrType[base];
        int data = mAttrData[base];

        if (type == TV_TYPE_FLOAT) {
            return Float.intBitsToFloat(data);
        }
        if (type == TV_TYPE_DIMENSION) {
            return decodeDimension(data);
        }
        if (type == TV_TYPE_INT_DEC || type == TV_TYPE_INT_HEX) {
            return (float) data;
        }
        return defaultValue;
    }

    /**
     * Get the resource ID value of an attribute, or defaultValue if not a reference.
     */
    public int getAttributeResourceValue(int index, int defaultValue) {
        int base = attrBase(index);
        if (base < 0) return defaultValue;

        if (mAttrType[base] == TV_TYPE_REFERENCE) {
            return mAttrData[base];
        }
        return defaultValue;
    }

    /**
     * Get the resource ID that an attribute name maps to (from the resource ID pool).
     * Returns 0 if not available.
     */
    public int getAttributeNameResource(int index) {
        int base = attrBase(index);
        if (base < 0) return 0;
        int nameIdx = mAttrName[base];
        if (mResourceIds != null && nameIdx >= 0 && nameIdx < mResourceIds.length) {
            return mResourceIds[nameIdx];
        }
        return 0;
    }

    /**
     * Get the raw typed value type of an attribute.
     */
    public int getAttributeValueType(int index) {
        int base = attrBase(index);
        if (base < 0) return TV_TYPE_NULL;
        return mAttrType[base];
    }

    /**
     * Get the raw typed value data of an attribute.
     */
    public int getAttributeValueData(int index) {
        int base = attrBase(index);
        if (base < 0) return 0;
        return mAttrData[base];
    }

    public int getDepth() {
        if (mCurrentEvent < 0 || mCurrentEvent >= mEventCount) return 0;
        return mEventDepths[mCurrentEvent];
    }

    public String getText() {
        if (mCurrentEvent < 0 || mCurrentEvent >= mEventCount) return null;
        if (mEventTypes[mCurrentEvent] == TEXT) {
            int idx = mEventText[mCurrentEvent];
            return poolString(idx);
        }
        return null;
    }

    public String getPositionDescription() {
        return "BinaryXml event " + mCurrentEvent;
    }

    public boolean isEmptyElementTag() {
        // In binary XML, there's no concept of empty element tags;
        // they're always represented as START_TAG + END_TAG pairs
        return false;
    }

    public void require(int type, String namespace, String name)
            throws XmlPullParserException {
        if (getEventType() != type) {
            throw new XmlPullParserException("Expected event " + type
                    + " but got " + getEventType());
        }
        if (namespace != null) {
            String ns = getNamespace();
            if (!namespace.equals(ns)) {
                throw new XmlPullParserException("Expected namespace " + namespace
                        + " but got " + ns);
            }
        }
        if (name != null) {
            String n = getName();
            if (!name.equals(n)) {
                throw new XmlPullParserException("Expected name " + name
                        + " but got " + n);
            }
        }
    }

    public String nextText() throws XmlPullParserException {
        if (getEventType() != START_TAG) {
            throw new XmlPullParserException("Expected START_TAG, got " + getEventType());
        }
        int ev = next();
        if (ev == TEXT) {
            String text = getText();
            next(); // consume END_TAG
            return text;
        } else if (ev == END_TAG) {
            return "";
        }
        throw new XmlPullParserException("Expected TEXT or END_TAG, got " + ev);
    }

    // ---- AttributeSet implementation ----

    public int getStyleAttribute() {
        return 0;
    }

    public String getClassAttribute() {
        return getAttributeValue(null, "class");
    }

    public boolean getAttributeBooleanValue(int index, boolean defaultValue) {
        int base = attrBase(index);
        if (base < 0) return defaultValue;
        if (mAttrType[base] == TV_TYPE_INT_BOOL) {
            return mAttrData[base] != 0;
        }
        return defaultValue;
    }

    // ---- XmlResourceParser (AutoCloseable) ----

    public void close() {
        // nothing to release
    }

    // ---- String pool access ----

    /**
     * Get the string pool parsed from this AXML.
     */
    public Object[] getStringPool() {
        return mStringPool;
    }

    /**
     * Get the resource ID pool parsed from this AXML.
     */
    public int[] getResourceIdPool() {
        return mResourceIds;
    }

    /**
     * Get the total number of events parsed.
     */
    public int getEventCount() {
        return mEventCount;
    }

    /**
     * Reset the parser to the beginning (before START_DOCUMENT).
     */
    public void reset() {
        mCurrentEvent = 0;
    }

    // ---- Internal helpers ----

    private String poolString(int index) {
        if (index < 0 || mStringPool == null || index >= mStringPool.length) return null;
        Object value = mStringPool[index];
        return value != null ? value.toString() : null;
    }

    private int attrBase(int index) {
        if (mCurrentEvent < 0 || mCurrentEvent >= mEventCount) return -1;
        if (mEventTypes[mCurrentEvent] != START_TAG) return -1;
        int count = mAttrCount[mCurrentEvent];
        if (index < 0 || index >= count) return -1;
        return mAttrStart[mCurrentEvent] + index;
    }

    private String typedValueToString(int type, int data) {
        switch (type) {
            case TV_TYPE_NULL:
                return null;
            case TV_TYPE_REFERENCE:
                return "@0x" + Integer.toHexString(data);
            case TV_TYPE_ATTRIBUTE:
                return "?0x" + Integer.toHexString(data);
            case TV_TYPE_FLOAT:
                return Float.toString(Float.intBitsToFloat(data));
            case TV_TYPE_DIMENSION:
                return Float.toString(decodeDimension(data)) + "px";
            case TV_TYPE_INT_DEC:
                return Integer.toString(data);
            case TV_TYPE_INT_HEX:
                return "0x" + Integer.toHexString(data);
            case TV_TYPE_INT_BOOL:
                return data != 0 ? "true" : "false";
            case TV_TYPE_INT_COLOR_ARGB8:
            case TV_TYPE_INT_COLOR_RGB8:
            case TV_TYPE_INT_COLOR_ARGB4:
            case TV_TYPE_INT_COLOR_RGB4:
                return "#" + Integer.toHexString(data);
            default:
                return Integer.toString(data);
        }
    }

    /**
     * Decode a packed dimension value.
     * Format: mantissa in high 24 bits, radix in bits 4-7, units in bits 0-3.
     * Units: 0=px, 1=dip, 2=sp, 3=pt, 4=in, 5=mm
     * Radix: 0=23p0, 1=16p7, 2=8p15, 3=0p23
     */
    private static float decodeDimension(int data) {
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

        return value;
    }

    // ---- String pool parsing ----

    private Object[] parseStringPool(ByteBuffer buf, int chunkStart, int chunkSize) {
        // String pool header (after the 8-byte chunk header already read):
        // stringCount(4), styleCount(4), flags(4), stringsStart(4), stylesStart(4)
        int stringCount = buf.getInt();
        int styleCount = buf.getInt();
        int flags = buf.getInt();
        int stringsStart = buf.getInt();
        int stylesStart = buf.getInt();

        boolean isUtf8 = (flags & (1 << 8)) != 0;

        int[] offsets = new int[stringCount];
        for (int i = 0; i < stringCount; i++) {
            offsets[i] = buf.getInt();
        }

        // Skip style offsets
        for (int i = 0; i < styleCount; i++) {
            buf.getInt();
        }

        // stringsStart is relative to the chunk start
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
                    pool[i] = readUtf8(buf);
                } else {
                    pool[i] = readUtf16(buf);
                }
            } catch (Exception e) {
                pool[i] = "";
            }
        }
        return pool;
    }

    private String readUtf8(ByteBuffer buf) {
        int charLen = buf.get() & 0xFF;
        if ((charLen & 0x80) != 0) {
            charLen = ((charLen & 0x7F) << 8) | (buf.get() & 0xFF);
        }
        int byteLen = buf.get() & 0xFF;
        if ((byteLen & 0x80) != 0) {
            byteLen = ((byteLen & 0x7F) << 8) | (buf.get() & 0xFF);
        }
        if (byteLen < 0 || buf.remaining() < byteLen) return "";
        byte[] b = new byte[byteLen];
        buf.get(b);
        try {
            return decodeUtf8(b);
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

    private String readUtf16(ByteBuffer buf) {
        int charLen = buf.getShort() & 0xFFFF;
        if ((charLen & 0x8000) != 0) {
            charLen = ((charLen & 0x7FFF) << 16) | (buf.getShort() & 0xFFFF);
        }
        if (charLen < 0 || buf.remaining() < charLen * 2) return "";
        char[] c = new char[charLen];
        for (int j = 0; j < charLen; j++) {
            c[j] = (char) (buf.getShort() & 0xFFFF);
        }
        return new String(c);
    }

    private static int[] toIntArray(ArrayList list) {
        int[] arr = new int[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = ((Integer) list.get(i)).intValue();
        }
        return arr;
    }
}
