package android.util;

import java.io.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

/**
 * Android-compatible Xml utility shim. Delegates to javax.xml.parsers.
 */
public class Xml {
    public static final String FEATURE_RELAXED = "http://xmlpull.org/v1/doc/features.html#relaxed";

    public static void parse(String xml, ContentHandler handler) throws SAXException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(handler);
            reader.parse(new InputSource(new StringReader(xml)));
        } catch (ParserConfigurationException | IOException e) {
            throw new SAXException(e);
        }
    }

    public static void parse(InputStream in, org.xml.sax.helpers.DefaultHandler handler)
            throws IOException, SAXException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(in, handler);
        } catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }
    }

    public static org.xml.sax.helpers.AttributesImpl asAttributeSet(Object parser) {
        return new org.xml.sax.helpers.AttributesImpl();
    }

    public static String findEncodingByBom(InputStream in) throws IOException {
        if (!in.markSupported()) return null;
        in.mark(4);
        int b1 = in.read();
        int b2 = in.read();
        int b3 = in.read();
        in.reset();
        if (b1 == 0xEF && b2 == 0xBB && b3 == 0xBF) return "UTF-8";
        if (b1 == 0xFE && b2 == 0xFF) return "UTF-16BE";
        if (b1 == 0xFF && b2 == 0xFE) return "UTF-16LE";
        return null;
    }
}
