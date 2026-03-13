package android.content.res;

import org.xmlpull.v1.XmlPullParser;
import java.io.Closeable;

/**
 * Android-compatible XmlResourceParser shim.
 * Interface extending XmlPullParser and Closeable, with getAttributeNameResource().
 */
public interface XmlResourceParser extends XmlPullParser, Closeable {

    /**
     * Return the resource ID of the attribute name at the given index.
     * Returns 0 if the attribute name is not a resource reference.
     */
    int getAttributeNameResource(int index);

    /**
     * Close the parser. This must be called when the parser is no longer needed.
     */
    @Override
    void close();
}
