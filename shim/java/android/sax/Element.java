package android.sax;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible Element shim for android.sax SAX helper API. Pure stub.
 *
 * Mirrors android.sax.Element: a node in the SAX element tree that can hold
 * child elements and optional start/end listeners.
 */
public class Element {

    protected String mUri;
    protected String mLocalName;

    protected StartElementListener mStartElementListener;
    protected EndElementListener mEndElementListener;
    protected EndTextElementListener mEndTextElementListener;

    protected List<Element> mChildren = new ArrayList<>();

    /** Package-private constructor used by subclasses. */
    Element(String uri, String localName) {
        this.mUri = uri == null ? "" : uri;
        this.mLocalName = localName;
    }

    // -------------------------------------------------------------------------
    // Child element access
    // -------------------------------------------------------------------------

    /**
     * Returns a child Element for the given local name (no namespace).
     * Creates one if it does not yet exist.
     */
    public Element getChild(String localName) {
        return getChild("", localName);
    }

    /**
     * Returns a child Element for the given namespace URI and local name.
     * Creates one if it does not yet exist.
     */
    public Element getChild(String uri, String localName) {
        for (Element child : mChildren) {
            if (child.mUri.equals(uri == null ? "" : uri)
                    && child.mLocalName.equals(localName)) {
                return child;
            }
        }
        Element child = new Element(uri, localName);
        mChildren.add(child);
        return child;
    }

    /**
     * Like {@link #getChild(String)} but throws if the child was already
     * required (same semantics as Android's android.sax.Element).
     */
    public Element requireChild(String localName) {
        return requireChild("", localName);
    }

    /**
     * Like {@link #getChild(String, String)} but throws if the child was
     * already required.
     */
    public Element requireChild(String uri, String localName) {
        // Shim: behave identically to getChild — callers just expect the element back.
        return getChild(uri, localName);
    }

    // -------------------------------------------------------------------------
    // Listener setters
    // -------------------------------------------------------------------------

    public void setStartElementListener(StartElementListener listener) {
        this.mStartElementListener = listener;
    }

    public void setEndElementListener(EndElementListener listener) {
        this.mEndElementListener = listener;
    }

    public void setEndTextElementListener(EndTextElementListener listener) {
        this.mEndTextElementListener = listener;
    }

    // -------------------------------------------------------------------------
    // Accessors used by the SAX dispatch machinery (RootElement)
    // -------------------------------------------------------------------------

    public String getUri() {
        return mUri;
    }

    public String getLocalName() {
        return mLocalName;
    }

    public List<Element> getChildren() {
        return mChildren;
    }

    /** Dispatch start — called by content handler. */
    void fireStart(org.xml.sax.Attributes attrs) {
        if (mStartElementListener != null) {
            mStartElementListener.start(attrs);
        }
    }

    /** Dispatch end with optional text — called by content handler. */
    void fireEnd(String text) {
        if (mEndTextElementListener != null) {
            mEndTextElementListener.end(text);
        } else if (mEndElementListener != null) {
            mEndElementListener.end();
        }
    }
}
