package android.sax;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Android-compatible RootElement shim. Pure stub with a working ContentHandler.
 *
 * Mirrors android.sax.RootElement: the root of an Element tree that also
 * exposes a SAX ContentHandler for driving the tree during parsing.
 */
public class RootElement extends Element {

    /**
     * Creates a RootElement with both a namespace URI and a local name.
     *
     * @param uri       namespace URI
     * @param localName local element name
     */
    public RootElement(String uri, String localName) {
        super(uri, localName);
    }

    /**
     * Creates a RootElement without a namespace (empty URI).
     *
     * @param localName local element name
     */
    public RootElement(String localName) {
        super("", localName);
    }

    // -------------------------------------------------------------------------
    // ContentHandler
    // -------------------------------------------------------------------------

    /**
     * Returns a {@link ContentHandler} that walks the Element tree defined
     * under this RootElement as the SAX parser produces events.
     */
    public ContentHandler getContentHandler() {
        return new ElementTreeContentHandler(this);
    }

    // -------------------------------------------------------------------------
    // Internal SAX dispatch handler
    // -------------------------------------------------------------------------

    private static final class ElementTreeContentHandler implements ContentHandler {

        private final Element mRoot;
        /** Stack of currently-open elements in document order. */
        private final Deque<Element> mStack = new ArrayDeque<>();
        private final StringBuilder mText = new StringBuilder();

        ElementTreeContentHandler(Element root) {
            this.mRoot = root;
        }

        @Override
        public void startDocument() throws SAXException {
            mStack.clear();
            mText.setLength(0);
        }

        @Override
        public void endDocument() throws SAXException {}

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes atts) throws SAXException {
            mText.setLength(0);
            Element current = mStack.isEmpty() ? null : mStack.peek();

            Element next;
            if (current == null) {
                // Expect the root element.
                next = matchUri(mRoot, uri, localName) ? mRoot : null;
            } else {
                next = findChild(current, uri, localName);
            }

            mStack.push(next != null ? next : new Element(uri, localName));
            if (next != null) {
                next.fireStart(atts);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            if (!mStack.isEmpty()) {
                Element el = mStack.pop();
                el.fireEnd(mText.toString());
            }
            mText.setLength(0);
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            mText.append(ch, start, length);
        }

        // Unused ContentHandler methods — no-ops.
        @Override public void setDocumentLocator(Locator locator) {}
        @Override public void startPrefixMapping(String prefix, String uri) {}
        @Override public void endPrefixMapping(String prefix) {}
        @Override public void ignorableWhitespace(char[] ch, int s, int l) {}
        @Override public void processingInstruction(String t, String d) {}
        @Override public void skippedEntity(String name) {}

        // -------------------------------------------------------------------------
        // Helpers
        // -------------------------------------------------------------------------

        private static boolean matchUri(Element el, String uri, String localName) {
            String elUri = el.getUri() == null ? "" : el.getUri();
            String inUri = uri == null ? "" : uri;
            return elUri.equals(inUri) && el.getLocalName().equals(localName);
        }

        private static Element findChild(Element parent, String uri,
                String localName) {
            for (Element child : parent.getChildren()) {
                if (matchUri(child, uri, localName)) {
                    return child;
                }
            }
            return null;
        }
    }
}
