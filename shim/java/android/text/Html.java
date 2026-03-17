package android.text;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable;

/**
 * Shim: android.text.Html
 *
 * Converts HTML to/from styled text.
 *
 * Tier 3 — stub implementation. Full HTML rendering is not available on
 * OpenHarmony's ArkUI layer from the Java shim. The {@link #fromHtml}
 * family strips all HTML tags and returns plain text. The {@link #toHtml}
 * family wraps the text in a minimal {@code <p>} tag without any span
 * processing.
 *
 * Bridge delegation: none required for the stub. A future implementation
 * could call OHBridge.invoke("html.parse", …) if the OH runtime exposes
 * a native HTML parser.
 */
public final class Html {

    private Html() {}

    // ── fromHtml flags (API 24+) ──────────────────────────────────────

    /**
     * Legacy mode: a single newline at the end of the source is ignored
     * and two newlines in a row produce a single line break.
     */
    public static final int FROM_HTML_MODE_LEGACY = 0x00000000;

    /**
     * Separates block-level elements with blank lines.
     */
    public static final int FROM_HTML_MODE_COMPACT = 0x00000001;

    // ── toHtml flags ──────────────────────────────────────────────────

    /**
     * Wrap paragraphs in {@code <p>} tags.
     */
    public static final int TO_HTML_PARAGRAPH_LINES_CONSECUTIVE = 0x00000000;
    public static final int TO_HTML_PARAGRAPH_LINES_INDIVIDUAL  = 0x00000001;

    // ── fromHtml ─────────────────────────────────────────────────────

    /**
     * Returns displayable styled text from the provided HTML string.
     *
     * Stub: strips all HTML tags using a simple regex and returns
     * the remaining text as a plain {@link SpannableString}.
     *
     * @param source HTML-formatted string
     * @return a SpannableString with the plain text content
     */
    public static Spanned fromHtml(String source) {
        return fromHtml(source, FROM_HTML_MODE_LEGACY);
    }

    /**
     * Returns displayable styled text from the provided HTML string.
     *
     * @param source HTML-formatted string
     * @param flags  processing flags ({@link #FROM_HTML_MODE_LEGACY} etc.)
     * @return a SpannableString with the plain text content
     */
    public static Spanned fromHtml(String source, int flags) {
        return fromHtml(source, flags, null);
    }

    /**
     * Returns displayable styled text from the provided HTML string.
     *
     * @param source      HTML-formatted string
     * @param flags       processing flags
     * @param imageGetter callback for inline images (ignored in stub)
     * @return a SpannableString with the plain text content
     */
    public static Spanned fromHtml(String source, int flags, ImageGetter imageGetter) {
        if (source == null) return new SpannableString("");
        // Strip HTML tags using manual indexOf loops (no regex JNI)
        String plain = stripTags(source);
        // Decode HTML entities
        plain = plain
                .replace("&amp;",  "&")
                .replace("&lt;",   "<")
                .replace("&gt;",   ">")
                .replace("&quot;", "\"")
                .replace("&#39;",  "'")
                .replace("&nbsp;", "\u00a0");
        if ((flags & FROM_HTML_MODE_LEGACY) == 0) {
            // compact mode: collapse multiple newlines
            plain = collapseNewlines(plain);
        }
        return new SpannableString(plain.trim());
    }

    // ── toHtml ───────────────────────────────────────────────────────

    /**
     * Returns an HTML representation of the provided styled text.
     *
     * Stub: HTML-encodes special characters and wraps the result in a
     * {@code <p>} tag. Span information is not serialised.
     *
     * @param text the styled text to convert
     * @return an HTML string
     */
    public static String toHtml(Spanned text) {
        return toHtml(text, TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
    }

    /**
     * Returns an HTML representation of the provided styled text.
     *
     * @param text  the styled text to convert
     * @param flags processing flags
     * @return an HTML string
     */
    public static String toHtml(Spanned text, int flags) {
        if (text == null) return "";
        String raw = text.toString();
        // Basic HTML-encode
        String encoded = raw
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("\n", "<br>\n");
        return "<p>" + encoded + "</p>";
    }

    // ── escapeHtml ─────────────────────────────────────────────────

    /**
     * Returns an HTML-escaped representation of the given plain text.
     */
    public static String escapeHtml(CharSequence text) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '&':  sb.append("&amp;"); break;
                case '<':  sb.append("&lt;"); break;
                case '>':  sb.append("&gt;"); break;
                case '"':  sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                default:   sb.append(c); break;
            }
        }
        return sb.toString();
    }

    /** Strip all HTML tags, replacing <br> and </p> with newlines. */
    private static String stripTags(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '<') {
                // Find closing >
                int end = s.indexOf('>', i);
                if (end < 0) {
                    // No closing bracket, keep rest
                    sb.append(s, i, s.length());
                    break;
                }
                String tag = s.substring(i, end + 1).toLowerCase();
                // Replace <br>, <br/>, <br /> with newline
                if (tag.startsWith("<br")) {
                    sb.append('\n');
                }
                // Replace </p> with newline
                if (tag.equals("</p>")) {
                    sb.append('\n');
                }
                // Skip all other tags (including <p...>)
                i = end + 1;
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }

    /** Collapse runs of 2+ newlines into a single newline. */
    private static String collapseNewlines(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        int nlCount = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\n') {
                nlCount++;
                if (nlCount <= 1) sb.append(c);
            } else {
                nlCount = 0;
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ── ImageGetter ──────────────────────────────────────────────────

    /**
     * Retrieves images for HTML {@code <img>} tags.
     *
     * Stub: implementations should return a Drawable-like object; on the
     * shim platform this interface exists purely for source compatibility.
     */
    public interface ImageGetter {
        /**
         * Called when the HTML parser encounters an {@code <img>} tag. The
         * {@code source} attribute of the tag is the argument. Returns an
         * object that can be used as an image placeholder, or {@code null}.
         */
        Object getDrawable(String source);
    }

    /**
     * Processes tags not handled by the built-in parser.
     *
     * Stub: exists for source compatibility.
     */
    public interface TagHandler {
        /**
         * Called when a tag is being processed.
         *
         * @param opening {@code true} if the tag is opening, {@code false} if closing
         * @param tag     the tag name
         * @param output  the output builder
         * @param xmlReader not used in the shim
         */
        void handleTag(boolean opening, String tag, Editable output, Object xmlReader);
    }
}
