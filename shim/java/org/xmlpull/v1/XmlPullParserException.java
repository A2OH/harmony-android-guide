package org.xmlpull.v1;

/**
 * Minimal org.xmlpull.v1.XmlPullParserException shim.
 */
public class XmlPullParserException extends Exception {

    public XmlPullParserException(String message) {
        super(message);
    }

    public XmlPullParserException(String message, XmlPullParser parser,
            Throwable chain) {
        super(message, chain);
    }
}
