package android.util;

import java.io.IOException;

/**
 * Android-compatible TypedXmlSerializer shim. Pure stub interface.
 *
 * Extends the XML serialization API with typed attribute writers that avoid
 * intermediate String allocation at call sites. Mirrors
 * android.util.TypedXmlSerializer.
 *
 * Note: Android's TypedXmlSerializer also extends org.xmlpull.v1.XmlSerializer,
 * but that interface is not shimmed here. Add it if downstream code requires
 * the full serializer API.
 */
public interface TypedXmlSerializer {

    /**
     * Writes an int-valued attribute.
     *
     * @param namespace attribute namespace URI (may be null / empty)
     * @param name      attribute local name
     * @param value     the value to write
     */
    TypedXmlSerializer attributeInt(String namespace, String name, int value)
            throws IOException;

    /**
     * Writes a long-valued attribute.
     */
    TypedXmlSerializer attributeLong(String namespace, String name, long value)
            throws IOException;

    /**
     * Writes a float-valued attribute.
     */
    TypedXmlSerializer attributeFloat(String namespace, String name, float value)
            throws IOException;

    /**
     * Writes a boolean-valued attribute.
     */
    TypedXmlSerializer attributeBoolean(String namespace, String name, boolean value)
            throws IOException;

    /**
     * Writes an int-valued attribute formatted as a hexadecimal string.
     */
    TypedXmlSerializer attributeHexInt(String namespace, String name, int value)
            throws IOException;

    /**
     * Writes a byte-array attribute encoded as a hexadecimal string.
     */
    TypedXmlSerializer attributeBytesHex(String namespace, String name, byte[] value)
            throws IOException;

    /**
     * Writes a byte-array attribute encoded as a Base64 string.
     */
    TypedXmlSerializer attributeBytesBase64(String namespace, String name, byte[] value)
            throws IOException;
}
