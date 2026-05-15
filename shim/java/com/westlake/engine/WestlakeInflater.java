/*
 * PF-arch-026 (task #1 of three): Context-free layout inflater.
 *
 * `LayoutInflater.from(activity).inflate(...)` requires a Context with
 * valid Resources, which we don't have. We walk the binary AXML directly
 * via the existing BinaryXmlParser and produce a WestlakeNode tree —
 * pure data, no Android Views.
 *
 * Resource references like "@string/foo" or "@drawable/bar" are kept as
 * raw strings; the renderer resolves them via ResourceTable lookup.
 */
package com.westlake.engine;

import android.content.res.BinaryXmlParser;
import org.xmlpull.v1.XmlPullParser;

public final class WestlakeInflater {
    private WestlakeInflater() {}

    /** Inflate a binary AXML layout into a WestlakeNode tree. Returns null on parse failure. */
    public static WestlakeNode inflate(byte[] axmlData) {
        if (axmlData == null || axmlData.length == 0) return null;
        try {
            BinaryXmlParser parser = new BinaryXmlParser(axmlData);
            WestlakeNode root = null;
            WestlakeNode current = null;
            java.util.ArrayDeque<WestlakeNode> stack = new java.util.ArrayDeque<WestlakeNode>();
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                if (event == XmlPullParser.START_TAG) {
                    String tag = parser.getName();
                    WestlakeNode node = new WestlakeNode(tag);
                    int n = parser.getAttributeCount();
                    for (int i = 0; i < n; i++) {
                        String name = parser.getAttributeName(i);
                        String value = parser.getAttributeValue(i);
                        if (name != null) {
                            node.attrs.put(name, value != null ? value : "");
                        }
                    }
                    if (root == null) root = node;
                    if (current != null) current.children.add(node);
                    stack.push(current = node);
                } else if (event == XmlPullParser.TEXT) {
                    /* PF-arch-031: capture inline text content. Used by
                     * <style><item name="foo">VALUE</item></style> in theme
                     * resources where the value is the inline text. Stash
                     * the text under the special key "$text". */
                    if (current != null) {
                        String text = parser.getText();
                        if (text != null && !text.isEmpty()) {
                            String existing = current.attrs.get("$text");
                            current.attrs.put("$text",
                                    existing == null ? text : existing + text);
                        }
                    }
                } else if (event == XmlPullParser.END_TAG) {
                    if (!stack.isEmpty()) stack.pop();
                    current = stack.isEmpty() ? null : stack.peek();
                }
                event = parser.next();
            }
            return root;
        } catch (Throwable t) {
            return null;
        }
    }
}
