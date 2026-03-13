package android.sax;

/**
 * Android-compatible TextElement shim. Pure stub.
 *
 * A TextElement is an Element that collects its character data and delivers
 * it to an EndTextElementListener. In this shim the distinction from Element
 * is structural only — the text-accumulation logic lives in RootElement's
 * ContentHandler.
 */
public class TextElement extends Element {

    /**
     * Creates a TextElement as a child of the given parent element.
     *
     * @param parent    the owning Element (used to attach this node to the tree)
     * @param uri       namespace URI (may be null / empty)
     * @param localName local XML element name
     */
    public TextElement(Element parent, String uri, String localName) {
        super(uri, localName);
        if (parent != null) {
            parent.mChildren.add(this);
        }
    }
}
