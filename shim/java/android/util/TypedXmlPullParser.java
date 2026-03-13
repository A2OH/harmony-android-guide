package android.util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Android-compatible TypedXmlPullParser shim. Pure stub interface.
 *
 * Extends XmlPullParser with typed attribute accessors that avoid the overhead
 * of string conversion at call sites. Mirrors android.util.TypedXmlPullParser.
 */
public interface TypedXmlPullParser extends XmlPullParser {

    /**
     * Returns the value of the named attribute as an {@code int}.
     *
     * @param namespace attribute namespace URI (may be null / empty)
     * @param name      attribute local name
     * @param defaultValue value to return if attribute is absent or unparseable
     * @return parsed int value or defaultValue
     */
    int getAttributeInt(String namespace, String name, int defaultValue)
            throws XmlPullParserException;

    /**
     * Returns the value of the named attribute as a {@code long}.
     */
    long getAttributeLong(String namespace, String name, long defaultValue)
            throws XmlPullParserException;

    /**
     * Returns the value of the named attribute as a {@code float}.
     */
    float getAttributeFloat(String namespace, String name, float defaultValue)
            throws XmlPullParserException;

    /**
     * Returns the value of the named attribute as a {@code boolean}.
     */
    boolean getAttributeBoolean(String namespace, String name, boolean defaultValue)
            throws XmlPullParserException;

    /**
     * Returns the value of the named attribute as an {@code int} parsed from
     * a hexadecimal string (e.g. "0xFF").
     */
    int getAttributeHexInt(String namespace, String name, int defaultValue)
            throws XmlPullParserException;

    /**
     * Returns the value of the named attribute as a byte array decoded from a
     * hex-encoded string.
     */
    byte[] getAttributeBytesHex(String namespace, String name, byte[] defaultValue)
            throws XmlPullParserException;

    /**
     * Returns the value of the named attribute as a byte array decoded from a
     * Base64-encoded string.
     */
    byte[] getAttributeBytesBase64(String namespace, String name, byte[] defaultValue)
            throws XmlPullParserException;
}
