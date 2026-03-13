package android.webkit;

/**
 * Shim: android.webkit.URLUtil
 * OH mapping: no direct OH equivalent — pure Java utility; used at Java layer only.
 *
 * Static utility methods for working with URLs in the context of a WebView.
 * No Android framework dependencies; all logic is implemented here so the class
 * works correctly on JVM without an Android runtime.
 */
public final class URLUtil {

    // ── Scheme constants ──

    private static final String HTTP_BASE    = "http://";
    private static final String HTTPS_BASE   = "https://";
    private static final String ASSET_BASE   = "file:///android_asset/";
    private static final String CONTENT_BASE = "content://";
    private static final String FILE_BASE    = "file://";
    private static final String DATA_BASE    = "data:";
    private static final String ABOUT_BASE   = "about:";

    // Utility class — no instances.
    private URLUtil() {}

    // ── Scheme predicates ──

    /**
     * Returns true if the URL uses the {@code http} scheme.
     */
    public static boolean isHttpUrl(String url) {
        return url != null && url.regionMatches(true, 0, HTTP_BASE, 0, HTTP_BASE.length());
    }

    /**
     * Returns true if the URL uses the {@code https} scheme.
     */
    public static boolean isHttpsUrl(String url) {
        return url != null && url.regionMatches(true, 0, HTTPS_BASE, 0, HTTPS_BASE.length());
    }

    /**
     * Returns true if the URL is a {@code file:///android_asset/} URL.
     */
    public static boolean isAssetUrl(String url) {
        return url != null && url.regionMatches(true, 0, ASSET_BASE, 0, ASSET_BASE.length());
    }

    /**
     * Returns true if the URL uses the {@code content://} scheme (ContentProvider).
     */
    public static boolean isContentUrl(String url) {
        return url != null && url.regionMatches(true, 0, CONTENT_BASE, 0, CONTENT_BASE.length());
    }

    /**
     * Returns true if the URL uses the {@code file://} scheme (includes asset URLs).
     */
    public static boolean isFileUrl(String url) {
        return url != null && url.regionMatches(true, 0, FILE_BASE, 0, FILE_BASE.length());
    }

    /**
     * Returns true if the URL uses the {@code data:} scheme.
     */
    public static boolean isDataUrl(String url) {
        return url != null && url.regionMatches(true, 0, DATA_BASE, 0, DATA_BASE.length());
    }

    /**
     * Returns true if the URL uses the {@code about:} scheme.
     */
    public static boolean isAboutUrl(String url) {
        return url != null && url.regionMatches(true, 0, ABOUT_BASE, 0, ABOUT_BASE.length());
    }

    /**
     * Returns true if the URL is a network URL (either http or https).
     */
    public static boolean isNetworkUrl(String url) {
        return isHttpUrl(url) || isHttpsUrl(url);
    }

    /**
     * Returns true if the URL is syntactically valid (non-null, non-empty, and
     * starts with a recognised scheme).
     *
     * This is a lenient check that mirrors Android's behaviour: it accepts any
     * URL with a scheme followed by {@code ://} or with a known schemeless form.
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) return false;
        return isHttpUrl(url)    || isHttpsUrl(url)  ||
               isAssetUrl(url)   || isContentUrl(url)||
               isFileUrl(url)    || isDataUrl(url)   ||
               isAboutUrl(url);
    }

    // ── File-name guessing ──

    /**
     * Guesses a suitable filename for the resource at the given URL.
     *
     * The algorithm mirrors Android's implementation:
     * <ol>
     *   <li>Extract the last path segment from the URL (stripped of query/fragment).</li>
     *   <li>If that segment has no extension, append one derived from the Content-Disposition
     *       or MIME type parameters (not available here — stub uses the segment as-is).</li>
     *   <li>Fall back to {@code "downloadfile"} when no segment is found.</li>
     * </ol>
     *
     * @param url              the URL of the resource
     * @param contentDisposition  the Content-Disposition header value, or null
     * @param mimeType         the MIME type of the resource, or null
     */
    public static String guessFileName(String url,
                                       String contentDisposition,
                                       String mimeType) {
        // 1. Try Content-Disposition: attachment; filename="foo.html"
        if (contentDisposition != null) {
            String name = extractFilenameFromContentDisposition(contentDisposition);
            if (name != null) return name;
        }

        // 2. Extract last path segment from the URL.
        if (url != null) {
            String path = url;
            // Strip query and fragment.
            int q = path.indexOf('?');
            if (q != -1) path = path.substring(0, q);
            int f = path.indexOf('#');
            if (f != -1) path = path.substring(0, f);
            int slash = path.lastIndexOf('/');
            if (slash >= 0 && slash < path.length() - 1) {
                String segment = path.substring(slash + 1);
                if (!segment.isEmpty()) {
                    // Ensure an extension is present.
                    if (!segment.contains(".") && mimeType != null) {
                        String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                        if (ext != null) segment = segment + "." + ext;
                    }
                    return segment;
                }
            }
        }

        // 3. Fallback.
        String fallback = "downloadfile";
        if (mimeType != null) {
            String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
            if (ext != null) return fallback + "." + ext;
        }
        return fallback;
    }

    private static String extractFilenameFromContentDisposition(String header) {
        // Look for filename="..." or filename=...
        int idx = header.indexOf("filename=");
        if (idx < 0) return null;
        String value = header.substring(idx + "filename=".length()).trim();
        if (value.startsWith("\"")) {
            int end = value.indexOf('"', 1);
            if (end > 0) return value.substring(1, end);
        } else {
            int semi = value.indexOf(';');
            return semi >= 0 ? value.substring(0, semi).trim() : value.trim();
        }
        return null;
    }

    // ── URL guessing ──

    /**
     * Attempts to determine a URL from the given string.  If the string already
     * looks like a URL it is returned as-is; otherwise an {@code https://} prefix
     * is prepended.
     *
     * @param inUrl  user-entered string
     * @return       a best-guess URL string
     */
    public static String guessUrl(String inUrl) {
        if (inUrl == null || inUrl.trim().isEmpty()) return inUrl;
        String trimmed = inUrl.trim();
        if (isValidUrl(trimmed)) return trimmed;
        // Treat plain domain-looking strings as https.
        if (trimmed.contains(".") && !trimmed.contains(" ")) {
            return "https://" + trimmed;
        }
        return trimmed;
    }

    // ── Search URL composition ──

    /**
     * Composes a search URL by substituting the query term into the search
     * template.
     *
     * @param inUrl    search URL template; the query is appended or substituted
     *                 at the position of {@code {searchTerms}} if present
     * @param query    the search terms
     * @param encoding character encoding for percent-encoding the query (e.g. {@code "UTF-8"})
     */
    public static String composeSearchUrl(String inUrl, String query, String encoding) {
        if (inUrl == null || query == null) return inUrl;
        // Simple substitution for {searchTerms} placeholder; otherwise append.
        String encoded = percentEncode(query);
        if (inUrl.contains("{searchTerms}")) {
            return inUrl.replace("{searchTerms}", encoded);
        }
        return inUrl + encoded;
    }

    // ── Internal helpers ──

    /** Very basic percent-encoding for query parameters (ASCII only). */
    private static String percentEncode(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') ||
                (c >= '0' && c <= '9') ||
                c == '-' || c == '_' || c == '.' || c == '~') {
                sb.append(c);
            } else if (c == ' ') {
                sb.append('+');
            } else {
                // Encode as UTF-8 bytes (BMP only for simplicity).
                byte[] bytes;
                try {
                    bytes = String.valueOf(c).getBytes("UTF-8");
                } catch (java.io.UnsupportedEncodingException e) {
                    bytes = new byte[]{(byte) c};
                }
                for (byte b : bytes) {
                    sb.append('%');
                    sb.append(String.format("%02X", b & 0xFF));
                }
            }
        }
        return sb.toString();
    }
}
