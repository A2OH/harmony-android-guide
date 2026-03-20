package android.graphics.drawable;

import android.content.res.Resources;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;

/**
 * Stub: DrawableInflater — inflates Drawable instances from XML.
 */
public class DrawableInflater {

    private final Resources mRes;

    public DrawableInflater(Resources res, ClassLoader cl) {
        mRes = res;
    }

    public Drawable inflateFromXml(String name, XmlPullParser parser, AttributeSet attrs,
            Resources.Theme theme) throws XmlPullParserException, IOException {
        return null;
    }

    public Drawable inflateFromXmlForDensity(String name, XmlPullParser parser, AttributeSet attrs,
            int density, Resources.Theme theme) throws XmlPullParserException, IOException {
        return null;
    }
}
