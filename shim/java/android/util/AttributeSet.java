package android.util;
import android.view.View;
import android.view.View;

/**
 * Android-compatible AttributeSet shim.
 * A collection of attributes, as found associated with a tag in an XML
 * document. Used by the view inflation system to pass XML attributes to
 * View constructors.
 * Stub — no-op interface for A2OH migration.
 */
public interface AttributeSet {

    /**
     * Returns the number of attributes available in the set.
     *
     * @return A positive integer, or 0 if the set is empty.
     */
    int getAttributeCount();

    /**
     * Returns the name of the attribute at the given zero-based index.
     *
     * @param index Zero-based index of the desired attribute.
     * @return The attribute name, or null if the index is out of range.
     */
    String getAttributeName(int index);

    /**
     * Returns the value of the attribute at the given zero-based index.
     *
     * @param index Zero-based index of the desired attribute.
     * @return The attribute value as a String, or null if not found.
     */
    String getAttributeValue(int index);

    /**
     * Returns the value of the named attribute in the given namespace.
     *
     * @param namespace The namespace of the attribute, or null for any namespace.
     * @param name      The name of the attribute.
     * @return The attribute value as a String, or null if not found.
     */
    String getAttributeValue(String namespace, String name);

    /**
     * Returns the resource ID of the style attribute, or 0 if none.
     */
    int getStyleAttribute();

    /**
     * Returns the value of the {@code class} attribute, or null if absent.
     */
    String getClassAttribute();

    /** Return the resource ID of the attribute name at the given index. */
    default int getAttributeNameResource(int index) { return 0; }

    /** Return a boolean value of the attribute at the given index. */
    default boolean getAttributeBooleanValue(int index, boolean defaultValue) { return defaultValue; }
}
