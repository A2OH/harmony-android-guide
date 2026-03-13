package org.xmlpull.v1;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Minimal org.xmlpull.v1.XmlPullParser shim so that android.util.TypedXmlPullParser
 * can extend it without requiring an external xmlpull jar on the classpath.
 *
 * Only the methods referenced by Android's TypedXmlPullParser (and the most
 * common pull-parser consumer idioms) are declared here. Implementations should
 * use the real xmlpull library at runtime.
 */
public interface XmlPullParser {

    // Event types
    int START_DOCUMENT = 0;
    int END_DOCUMENT   = 1;
    int START_TAG       = 2;
    int END_TAG         = 3;
    int TEXT            = 4;
    int CDSECT          = 5;
    int ENTITY_REF      = 6;
    int IGNORABLE_WHITESPACE = 7;
    int PROCESSING_INSTRUCTION = 8;
    int COMMENT        = 9;
    int DOCDECL        = 10;

    String NO_NAMESPACE = "";

    // -------------------------------------------------------------------------
    // Setup
    // -------------------------------------------------------------------------

    void setFeature(String name, boolean state) throws XmlPullParserException;
    boolean getFeature(String name);
    void setProperty(String name, Object value) throws XmlPullParserException;
    Object getProperty(String name);
    void setInput(Reader in) throws XmlPullParserException;
    void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException;

    // -------------------------------------------------------------------------
    // Document info
    // -------------------------------------------------------------------------

    String getInputEncoding();
    void defineEntityReplacementText(String entityName, String replacementText)
            throws XmlPullParserException;
    int getNamespaceCount(int depth) throws XmlPullParserException;
    String getNamespacePrefix(int pos) throws XmlPullParserException;
    String getNamespaceUri(int pos) throws XmlPullParserException;
    String getNamespace(String prefix);
    int getDepth();
    String getPositionDescription();
    int getLineNumber();
    int getColumnNumber();
    boolean isWhitespace() throws XmlPullParserException;
    String getText();
    char[] getTextCharacters(int[] holderForStartAndLength);
    String getNamespace();
    String getName();
    String getPrefix();
    boolean isEmptyElementTag() throws XmlPullParserException;

    // -------------------------------------------------------------------------
    // Attributes
    // -------------------------------------------------------------------------

    int getAttributeCount();
    String getAttributeNamespace(int index);
    String getAttributeName(int index);
    String getAttributePrefix(int index);
    String getAttributeType(int index);
    boolean isAttributeDefault(int index);
    String getAttributeValue(int index);
    String getAttributeValue(String namespace, String name);

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    int getEventType() throws XmlPullParserException;
    int next() throws XmlPullParserException, IOException;
    int nextToken() throws XmlPullParserException, IOException;
    void require(int type, String namespace, String name)
            throws XmlPullParserException, IOException;
    String nextText() throws XmlPullParserException, IOException;
    int nextTag() throws XmlPullParserException, IOException;
}
